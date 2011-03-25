package infraestrutura.som;

import java.io.*;
import javax.sound.midi.*;

/*
 * A classe MidiPlayer é responsável por reproduzir arquivos mid.
 *
 * @author David Buzatto
 */
public class MidiPlayer implements MetaEventListener {
    
    // Evento meta Midi
    public static final int END_OF_TRACK_MESSAGE = 47;
    
    private Sequencer sequencer;
    private boolean loop;
    private boolean paused;
    public boolean executando = false;
    
    /**
     * Cria um novo MidiPlayer.
     */
    public MidiPlayer() {
        
        try {
            
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.addMetaEventListener( this );
            
        } catch ( MidiUnavailableException ex ) {
            
            sequencer = null;
            
        }
    }
    
    
    /**
     * Carrega a seqüência do sistema de arquivos. Retorna null se um erro 
     * ocorrer.
     */
    public Sequence getSequence( String name ) {
        
        String filename = "/recursos/sons/" + name;
        try {
            return MidiSystem.getSequence( getClass().getResource( ( filename ) ) );
        } catch ( InvalidMidiDataException ex ) {
            ex.printStackTrace();
            return null;
        } catch ( IOException ex ) {
            ex.printStackTrace();
            return null;
        }
        
    }
    
    
    /**
     * Executa uma seqüência, realizando um loop opcionalmente. 
     * Esse método retorna imediatamente.
     * A seqüência não é executada se não for válida.
     */
    public void play( Sequence sequence, boolean loop ) {
        
        if ( sequencer != null && sequence != null ) {
            
            try {
                
                sequencer.setSequence( sequence );
                sequencer.start();
                this.loop = loop;
                
            } catch ( InvalidMidiDataException ex ) {
                
                ex.printStackTrace();
                
            }
        }
    }
    
    
    /**
     * Esse método é chamado pelo sistema de som onde o evento meta ocorre.
     * Nesse caso, quando o meta evento "end-of-track" é recebido, 
     * a seqüência é reiniciada se o loop estiver ligado.
     */
    public void meta( MetaMessage event ) {
        if ( event.getType() == END_OF_TRACK_MESSAGE ) {
            if ( sequencer != null && sequencer.isOpen() && loop ) {
                sequencer.start();
            }
            executando = false;
        }
    }
    
    
    /**
     * Para o seqüenciador e reinicia sua posição para 0.
     */
    public void stop() {
        if ( sequencer != null && sequencer.isOpen() ) {
            sequencer.stop();
            sequencer.setMicrosecondPosition( 0 );
        }
    }
    
    
    /**
     * Fechao o seqüenciador.
     */
    public void close() {
        if ( sequencer != null && sequencer.isOpen() ) {
            sequencer.close();
        }
    }
    
    
    /**
     * Obtém o seqüenciador.
     */
    public Sequencer getSequencer() {
        return sequencer;
    }
    
    
    /**
     * Configura o estado de pausa. A música pode não parar imediatamente.
     */
    public void setPaused( boolean paused ) {
        if ( this.paused != paused && sequencer != null ) {
            this.paused = paused;
            if ( paused ) {
                sequencer.stop();
            } else {
                sequencer.start();
            }
        }
    }
    
    
    /**
     * Returna se está pausado.
     */
    public boolean isPaused() {
        return paused;
    }
    
}