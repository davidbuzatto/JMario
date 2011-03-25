package infraestrutura.core;

import java.awt.*;
import javax.swing.*;
import infraestrutura.grafico.*;

/**
 * Classe abstrata utilizada como base do jogo. As subclasses devem implementar
 * o método draw().
 *
 * @author David Buzatto
 */
public abstract class GameCore {
    
    protected static final int FONT_SIZE = 24;
    
    // modos de visualização
    private static final DisplayMode POSSIBLE_MODES[] = {
        new DisplayMode( 800, 600, 16, 0 ),
        new DisplayMode( 800, 600, 32, 0 ),
        new DisplayMode( 800, 600, 24, 0 ),
        new DisplayMode( 640, 480, 16, 0 ),
        new DisplayMode( 640, 480, 32, 0 ),
        new DisplayMode( 640, 480, 24, 0 ),
        new DisplayMode( 1024, 768, 16, 0 ),
        new DisplayMode( 1024, 768, 32, 0 ),
        new DisplayMode( 1024, 768, 24, 0 ),
    };
    
    private boolean isRunning;
    protected ScreenManager screen;
    
    /**
     * Sinaliza ao loop do jogo que é hora de terminar.
     */
    public void stop() {
        isRunning = false;
    }
    
    
    /**
     * Chama init() e gameLoop().
     */
    public void run() {
        try {
            init();
            gameLoop();
        } finally {
            screen.restoreScreen();
            lazilyExit();
        }
    }
    
    
    /**
     * Finaliza a máquina virtual usando uma thread daemon.
     * A thread daemon aguarda 2 segundos então chama System.exit(0).
     * Como a máquina virtual deve finalizar apenas quando o daemon estiver 
     * rodando, isso dá certeza que System.exit(0) é chamado somente quanto 
     * necessário. Isso se faz necessário para quando o sistema de som do Java
     * estiver sendo executado.
     */
    public void lazilyExit() {
        Thread thread = new Thread() {
            public void run() {
                // primeiro aguarda que a máquina virtual finaliza por si própria
                try {
                    Thread.sleep( 2000 );
                } catch ( InterruptedException ex ) { }
                // o sistema ainda está rodando, então força a finalização
                System.exit( 0 );
            }
        };
        thread.setDaemon( true );
        thread.start();
    }
    
    
    /**
     * Configura o modo de tela cheia, inicializa e cria os objetos.
     */
    public void init() {
        screen = new ScreenManager();
        DisplayMode displayMode =
                screen.findFirstCompatibleMode( POSSIBLE_MODES );
        screen.setFullScreen(displayMode);
        
        Window window = screen.getFullScreenWindow();
        window.setFont( new Font( "Dialog", Font.PLAIN, FONT_SIZE ) );
        window.setBackground( Color.BLUE );
        window.setForeground( Color.WHITE );
        
        isRunning = true;
    }
    
    
    /**
     * Carrega uma imagem.
     */
    public Image loadImage( String name ) {
        return new ImageIcon( getClass().getResource( "/recursos/imagens/" + name ) ).getImage();
    }
    
    
    /**
     * Executa o game loop até que stop() seja chamado.
     */
    public void gameLoop() {
        long startTime = System.currentTimeMillis();
        long currTime = startTime;
        
        while ( isRunning ) {
            long elapsedTime =
                    System.currentTimeMillis() - currTime;
            currTime += elapsedTime;
            
            // atualiza
            update( elapsedTime );
            
            // desenha
            Graphics2D g = screen.getGraphics();
            draw( g );
            g.dispose();
            screen.update();
            
            // não forme, executando da forma mais rápida possível
            /*try {
                Thread.sleep(20);
            }
            catch (InterruptedException ex) { }*/
        }
    }
    
    
    /**
     * Atualiza o estado do jogo/animação baseado da quantidade de tempo 
     * que passou.
     */
    public void update(long elapsedTime) {
        // não faz nada
    }
    
    
    /**
     * Desenha na tela. As subclasses devem sobrescrever esse método.
     */
    public abstract void draw(Graphics2D g);
    
}