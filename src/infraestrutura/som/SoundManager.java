package infraestrutura.som;

import java.io.*;
import javax.sound.sampled.*;
import javax.sound.midi.*;
import infraestrutura.util.*;


/**
 * A classe SoundManager gerencia a execução dos sons. A SoundManager é um 
 * ThreadPool, onde cada thread executa um som por vez. Isso permite que o 
 * SoundManager facilmente limite o tamanho de sons simultâneos que são executados.
 *
 * @author David Buzatto
 */
public class SoundManager extends ThreadPool {
    
    private AudioFormat playbackFormat;
    private ThreadLocal< SourceDataLine > localLine;
    private ThreadLocal< byte[] > localBuffer;
    private Object pausedLock;
    private boolean paused;
    
    /**
     * Cria um novo SoundManager usando o número máqimo de sons executados 
     * simultaneamente.
     */
    public SoundManager( AudioFormat playbackFormat ) {
        this( playbackFormat,
                getMaxSimultaneousSounds( playbackFormat ) );
    }
    
    
    /**
     * Cria um novo SoundManager usando o número máqimo de sons executados 
     * simultaneamente.
     */
    public SoundManager( AudioFormat playbackFormat,
            int maxSimultaneousSounds ) {
        super( maxSimultaneousSounds );
        this.playbackFormat = playbackFormat;
        localLine = new ThreadLocal< SourceDataLine >();
        localBuffer = new ThreadLocal< byte[] >();
        pausedLock = new Object();
        // notifica o thread pool que está tudo ok para iniciar
        synchronized ( this ) {
            notifyAll();
        }
    }
    
    
    /**
     * Obtém o número máximo de sons que pode ser executados;
     */
    public static int getMaxSimultaneousSounds( AudioFormat playbackFormat) {
        DataLine.Info lineInfo = new DataLine.Info(
                SourceDataLine.class, playbackFormat );
        Mixer mixer = AudioSystem.getMixer( null );
        int lines = mixer.getMaxLines( lineInfo );
        
        if ( lines == AudioSystem.NOT_SPECIFIED )
            return 16; // presume que seja 16
        
        return mixer.getMaxLines( lineInfo );
    }
    
    
    /**
     * Faz limpesa antes de fechar.
     */
    protected void cleanUp() {
        
        // sinal para parar
        setPaused( false );
        
        // fecha o mixer (para qualquer som que está executando)
        Mixer mixer = AudioSystem.getMixer( null );
        if ( mixer.isOpen() ) {
            mixer.close();
        }
    }
    
    
    public void close() {
        cleanUp();
        super.close();
    }
    
    
    public void join() {
        cleanUp();
        super.join();
    }
    
    
    /**
     * Configura o estado de pausa. Os sons podem não pausar imediatamente.
     */
    public void setPaused( boolean paused ) {
        if ( this.paused != paused ) {
            synchronized ( pausedLock ) {
                this.paused = paused;
                if ( !paused ) {
                    // reinicia os sons
                    pausedLock.notifyAll();
                }
            }
        }
    }
    
    
    /**
     * Retorna o estado de pausa.
     */
    public boolean isPaused() {
        return paused;
    }
    
    
    /**
     * Carrega um som do sistema de arquivos. Retorna null caso ocorra algum erro.
     */
    public Sound getSound( String name ) {
        return getSound( getAudioInputStream( name ) );
    }
    
    
    /**
     * Carreha um dom de um AudioInputStream.
     */
    public Sound getSound( AudioInputStream audioStream ) {
        if ( audioStream == null ) {
            return null;
        }
        
        // obtém o número de bytes a ler
        int length = ( int ) ( audioStream.getFrameLength() *
                audioStream.getFormat().getFrameSize() );
        
        // lê todo o stream
        byte[] samples = new byte[ length ];
        DataInputStream is = new DataInputStream( audioStream );
        try {
            is.readFully( samples );
        } catch ( IOException ex ) {
            ex.printStackTrace();
        }
        
        // returna os samples
        return new Sound(samples);
    }
    
    
    /**
     * Cria um AudioInputStream usando um arquivo.
     */
    public AudioInputStream getAudioInputStream( String name ) {
        
        String filename = "/recursos/sons/" + name;
        
        try {
            
            // abre o arquivo
            AudioInputStream source =
                    AudioSystem.getAudioInputStream( getClass().getResource( filename ) );
            
            // converte para o formato de execução
            return AudioSystem.getAudioInputStream(
                    playbackFormat, source );
            
        } catch ( UnsupportedAudioFileException ex ) {
            ex.printStackTrace();
        } catch ( IOException ex ) {
            ex.printStackTrace();
        } catch ( IllegalArgumentException ex ) {
            ex.printStackTrace();
        }
        
        return null;
    }
    
    
    /**
     * Executa um som. Esse método retorna imediatamente.
     */
    public InputStream play( Sound sound ) {
        return play( sound, null, false );
    }
    
    
    /**
     * Executa um som com um SoundFilter especificado. 
     * Esse método retorna imediatamente.
     */
    public InputStream play( Sound sound, SoundFilter filter,
            boolean loop ) {
        
        InputStream is;
        
        if ( sound != null ) {
            if ( loop ) {
                is = new LoopingByteInputStream(
                        sound.getSamples() );
            } else {
                is = new ByteArrayInputStream( sound.getSamples() );
            }
            
            return play( is, filter );
        }
        
        return null;
    }
    
    
    /**
     * Executa um som de um InputStream.
     * Esse método retorna imediatamente.
     */
    public InputStream play( InputStream is ) {
        return play( is, null );
    }
    
    
    /**
     * Executa um som de um InputStream com um filtro opcional.
     * Esse método retorna imediatamente.
     */
    public InputStream play( InputStream is, SoundFilter filter ) {
        if ( is != null ) {
            if ( filter != null ) {
                is = new FilteredSoundStream( is, filter );
            }
            runTask( new SoundPlayer( is ) );
        }
        return is;
    }
    
    
    /**
     * Sinaliza a PooledThread que iniciou. Cria a linha e o buffer da thread.
     */
    protected void threadStarted() {
        
        // aguarda o construtor do SoundManager terminar
        synchronized ( this ) {
            try {
                wait();
            } catch ( InterruptedException ex ) { }
        }
        
        // use um pequeno buffer de , 100ms (1/10th seg) para os filtros
        // que mudam em tempo real
        int bufferSize = playbackFormat.getFrameSize() *
                Math.round( playbackFormat.getSampleRate() / 10 );
        
        // cria, abre e inicia a linha
        SourceDataLine line;
        DataLine.Info lineInfo = new DataLine.Info(
                SourceDataLine.class, playbackFormat );
        
        try {
            line = ( SourceDataLine ) AudioSystem.getLine( lineInfo );
            line.open( playbackFormat, bufferSize );
        } catch ( LineUnavailableException ex ) {
            // a linha não está disponível, sinaliza para finalizar a thread
            Thread.currentThread().interrupt();
            return;
        }
        
        line.start();
        
        // cria o buffer
        byte[] buffer = new byte[ bufferSize ];
        
        // seta a thread como local
        localLine.set( line );
        localBuffer.set( buffer );
    }
    
    
    /**
     * Sinaliza que a PooledThread parou. Drena e fecha a linha da thread.
     */
    protected void threadStopped() {
        SourceDataLine line = localLine.get();
        if ( line != null ) {
            line.drain();
            line.close();
        }
    }
    
    
    /**
     * A classe SoundPlauer é uma tareda para as PooledThreads executarem. Este
     * recebe uma linha da thread e um buffer de bytes das variáveis da 
     * ThreadLocal e executa o som de um InputStream.
     * <p>Essa classe só funciona quando chamada de dentro de uma PooledThread.
     */
    protected class SoundPlayer implements Runnable {
        
        private InputStream source;
        
        public SoundPlayer( InputStream source ) {
            this.source = source;
        }
        
        public void run() {
            
            // obtém a linha e o buffer do ThreadLocals
            SourceDataLine line = ( SourceDataLine ) localLine.get();
            byte[] buffer = ( byte[] ) localBuffer.get();
            
            if ( line == null || buffer == null ) {
                // linha não disponível
                return;
            }
            
            // copia os dados para a linha
            try {
                
                int numBytesRead = 0;
                
                while ( numBytesRead != -1 ) {
                    
                    // se pausado, aguarda até sair da pausa
                    synchronized ( pausedLock ) {
                        if ( paused ) {
                            try {
                                pausedLock.wait();
                            } catch (InterruptedException ex) {
                                return;
                            }
                        }
                    }
                    
                    // copia os dados
                    numBytesRead =
                            source.read( buffer, 0, buffer.length );
                    if ( numBytesRead != -1 ) {
                        line.write( buffer, 0, numBytesRead );
                    }
                }
                
            } catch ( IOException ex ) {
                ex.printStackTrace();
            }
        }
    }
    
}