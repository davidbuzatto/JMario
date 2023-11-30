package jogo;

import infraestrutura.core.GameCore;
import infraestrutura.grafico.Sprite;
import infraestrutura.input.GameAction;
import infraestrutura.input.InputManager;
import infraestrutura.som.EchoFilter;
import infraestrutura.som.MidiPlayer;
import infraestrutura.som.Sound;
import infraestrutura.som.SoundManager;
import infraestrutura.util.ResourceManager;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioFormat;
import jogo.sprites.Creature;
import jogo.sprites.Player;
import jogo.sprites.PowerUp;
import jogo.tile.TileMap;
import jogo.tile.TileMapRenderer;


/**
 * A classe GameManager gerencia todas as partes do jogo.
 *
 * @author David Buzatto
 */
public class GameManager extends GameCore {
    
    public static void main( String[] args ) {
        new GameManager().run();
    }
    
    // descomprimido, 11025Hz, 8-bit, mono, signed, little-endian
    private static final AudioFormat PLAYBACK_FORMAT =
            new AudioFormat( 11025, 8, 1, true, false );
    
    // quantidade máxima de fases (importante para a finalização)
    private static int QUANTIDADE_FASES = 3;
    
    private static final int DRUM_TRACK = 1;
    
    public static final float GRAVITY = 0.002f;
    
    // indica se o jogo está pausado
    private boolean paused;
    
    // indica se deve ir para a próxima fase
    private boolean goToNextLevel;
    
    // indica que o jogo terminou
    private boolean gameOver;
    
    private Point pointCache = new Point();
    private TileMap map;
    private MidiPlayer midiPlayerMusica;
    private MidiPlayer midiPlayerMusicaFinalFase;
    private MidiPlayer midiPlayerMusicaGameOver;
    private SoundManager soundManager;
    private ResourceManager resourceManager;
    private Sound coinSound;
    private Sound stompSound;
    private Sound oneUpSound;
    private Sound deathSound;
    private Sound jumpSound;
    private Sound powerUpSound;
    private Sound powerDownSound;
    private Sequence musica;
    private Sequence musicaFinalFase;
    private Sequence musicaGameOver;
    private InputManager inputManager;
    private TileMapRenderer renderer;
    
    // ações
    private GameAction moveLeft;
    private GameAction moveRight;
    private GameAction moveDown;
    private GameAction jump;
    private GameAction exit;
    private GameAction run;
    private GameAction configAction;
    private GameAction pause;
    
    // contadores da interface
    private int quantidadeVidas;
    private int quantidadePontos;
    private int quantidadePontosFase;
    private int quantidadeCoins;
    private int quantidadeEstrelas;
    
    // contadores de quadros
    private int quadrosAbertura;
    private int quadrosFechamento;
    private int quadrosGameOver;
    private int quadroAtualAbertura;
    private int quadroAtualFechamento;
    private int quadroAtualGameOver;
    
    // imagens da interface
    private Image imagemInterfaceMario;
    private Image imagemInterfaceStar;
    private Image imagemInterfaceCoin;
    private Image imagemInterfacePontuacao;
    private Image imagemInterfaceTotal;
    private Image imagemInterfacePausa;
    private Image imagemInterfaceEstagioFinalizado;
    private Image imagemInterfaceJogoFinalizado;
    private Image imagemInterfaceGameOver;
    private Image imagemInterfaceX;
    private Image imagemInterface0;
    private Image imagemInterface1;
    private Image imagemInterface2;
    private Image imagemInterface3;
    private Image imagemInterface4;
    private Image imagemInterface5;
    private Image imagemInterface6;
    private Image imagemInterface7;
    private Image imagemInterface8;
    private Image imagemInterface9;
    
    public void init() {
        
        super.init();
        
        // assegura que os componentes Swing não irão "se pintar"
        //NullRepaintManager.install();
        
        // inicia o input manager
        initInput();
        
        // configura como não pausado
        setPaused( false );
        
        // configura se é para ir para a próxima fase
        setGoToNextLevel( false );
        
        // configura que o jogo não terminou
        setGameOver( false );
        
        // inicia o resource manager
        resourceManager = new ResourceManager(
                screen.getFullScreenWindow().getGraphicsConfiguration(), 'Z' );
        
        // carrega os recursos
        renderer = new TileMapRenderer();
        renderer.setBackground(
                resourceManager.loadImage( "background" + 
                quantidadeEstrelas + ".png" ) );
        
        // carrega o primeiro mapa
        map = resourceManager.loadNextMap();
        
        // carrega os sons
        soundManager = new SoundManager( PLAYBACK_FORMAT );
        coinSound = soundManager.getSound( "smw_coin.wav" );
        stompSound = soundManager.getSound( "smw_stomp.wav" );
        oneUpSound = soundManager.getSound( "smw_1-up.wav" );
        deathSound = soundManager.getSound( "smw_death.wav" );
        jumpSound = soundManager.getSound( "smw_jump.wav" );
        powerUpSound = soundManager.getSound( "smw_powerup.wav" );
        powerDownSound = soundManager.getSound( "smw_powerdown.wav" );
        
        // inicia os contadores do jogo
        quantidadeVidas = 5;
        quantidadeCoins = 0;
        quantidadePontos = 0;
        quantidadePontosFase = 0;
        quantidadeEstrelas = 0;
        
        // inicia os contadores de animação
        quadrosAbertura = 50;
        quadroAtualAbertura = 0; // zerado fazer animação na carga do jogo
        
        quadrosFechamento = 50;
        quadroAtualFechamento = quadrosFechamento;
        
        quadrosGameOver = 120;
        quadroAtualGameOver = 0;
        
        
        // carrega as imagens
        imagemInterfaceMario = resourceManager.loadImage( "interfaceMario.png" );
        imagemInterfaceStar = resourceManager.loadImage( "interfaceStar.png" );
        imagemInterfaceCoin = resourceManager.loadImage( "interfaceCoin.png" );
        imagemInterfacePontuacao = resourceManager.loadImage( "interfacePontuacao.png" );
        imagemInterfaceTotal = resourceManager.loadImage( "interfaceTotal.png" );
        imagemInterfacePausa = resourceManager.loadImage( "interfacePausa.png" );
        imagemInterfaceEstagioFinalizado = resourceManager.loadImage( "interfaceEstagioFinalizado.png" );
        imagemInterfaceJogoFinalizado = resourceManager.loadImage( "interfaceJogoFinalizado.png" );
        imagemInterfaceGameOver = resourceManager.loadImage( "interfaceGameOver.png" );
        imagemInterfaceX = resourceManager.loadImage( "interfaceX.png" );
        imagemInterface0 = resourceManager.loadImage( "interface0.png" );
        imagemInterface1 = resourceManager.loadImage( "interface1.png" );
        imagemInterface2 = resourceManager.loadImage( "interface2.png" );
        imagemInterface3 = resourceManager.loadImage( "interface3.png" );
        imagemInterface4 = resourceManager.loadImage( "interface4.png" );
        imagemInterface5 = resourceManager.loadImage( "interface5.png" );
        imagemInterface6 = resourceManager.loadImage( "interface6.png" );
        imagemInterface7 = resourceManager.loadImage( "interface7.png" );
        imagemInterface8 = resourceManager.loadImage( "interface8.png" );
        imagemInterface9 = resourceManager.loadImage( "interface9.png" );
        
        // inicia a música
        midiPlayerMusica = new MidiPlayer();
        midiPlayerMusicaFinalFase = new MidiPlayer();
        midiPlayerMusicaGameOver = new MidiPlayer();
        musica = midiPlayerMusica.getSequence( "music" + 
                quantidadeEstrelas + ".midi" );
        musicaFinalFase = midiPlayerMusicaFinalFase.getSequence( "musicLevelEnd.midi" );
        musicaGameOver = midiPlayerMusicaGameOver.getSequence( "musicGameOver.midi" );
        
        midiPlayerMusica.play( musica, true );
        toggleDrumPlayback();
        
    }
    
    
    /**
     * Fecha os recursos usados pelo GameManager.
     */
    public void stop() {
        super.stop();
        midiPlayerMusica.close();
        soundManager.close();
    }
    
    
    private void initInput() {
        
        moveLeft = new GameAction( "moveLeft" );
        moveRight = new GameAction( "moveRight" );
        moveDown = new GameAction( "moveDown" );
        jump = new GameAction( "jump", GameAction.DETECT_INITAL_PRESS_ONLY );
        exit = new GameAction( "exit", GameAction.DETECT_INITAL_PRESS_ONLY );
        run = new GameAction( "run" );
        configAction = new GameAction( "config", GameAction.DETECT_INITAL_PRESS_ONLY );
        pause = new GameAction("pause", GameAction.DETECT_INITAL_PRESS_ONLY );
        
        inputManager = new InputManager( screen.getFullScreenWindow() );
        inputManager.setCursor( InputManager.INVISIBLE_CURSOR );
        
        inputManager.mapToKey( moveLeft, KeyEvent.VK_LEFT );
        inputManager.mapToKey( moveRight, KeyEvent.VK_RIGHT );
        inputManager.mapToKey( moveDown, KeyEvent.VK_DOWN );
        inputManager.mapToKey( jump, KeyEvent.VK_SPACE );
        inputManager.mapToKey( exit, KeyEvent.VK_ESCAPE );
        inputManager.mapToKey( run, KeyEvent.VK_CONTROL );
        inputManager.mapToKey( pause, KeyEvent.VK_P );
        inputManager.mapToKey( configAction, KeyEvent.VK_C );
        
    }
    
    
    private void checkInput( long elapsedTime ) {
        
        if ( exit.isPressed() ) {
            stop();
        }
        
        Player player = ( Player ) map.getPlayer();
        
        if ( player.isAlive() ) {
            
            float velocityX = 0;
            
            if ( moveLeft.isPressed() && !player.isDown() ) {
                velocityX -= player.getMaxSpeed();
            }
            
            if ( moveRight.isPressed() && !player.isDown() ) {
                velocityX += player.getMaxSpeed();
            }
            
            if ( moveDown.isPressed() ) {
                player.setDown( true );
            } else {
                player.setDown( false );
            }
            
            if ( jump.isPressed() ) {
                
                // toca apenas se o jogador não estiver pulando
                if ( !player.isPulando() )
                    soundManager.play( jumpSound );
                
                player.jump( false );
                
            }
            
            // verifica se é para correr
            if ( run.isPressed() ) {
                player.setMaxSpeed( 0.5f );
            } else {
                player.setMaxSpeed( 0.3f );
            }
            
            // se a pausa for pressionada e as animações de abertura/fechamento
            // não estiverem sendo executadas
            if ( pause.isPressed() &&
                    quadroAtualAbertura == quadrosAbertura && 
                    quadroAtualFechamento == quadrosFechamento ) {
                setPaused( !isPaused() );
                
                // se estiver pausado, para a música
                if ( isPaused() ) {
                    
                    // se o player não estiver pausado, pausa
                    if ( !midiPlayerMusica.isPaused() ) {
                        
                        midiPlayerMusica.stop();
                        
                    }
                    
                } else {
                    
                        midiPlayerMusica.play( musica, true );
                    
                }
            }
            
            if ( configAction.isPressed() ) {
                //TODO - implementar configuração
                System.out.println( "implementar..." );
            }
            
            player.setVelocityX( velocityX );
            
        }
        
    }
    
    
    public void draw( Graphics2D g ) {
        
        renderer.draw( g, map, screen.getWidth(), screen.getHeight() );
        
        // desenha a interface gráfica
        drawInterface( g );
        
        // desenha a animação de abertura de tela
        if ( quadroAtualAbertura < quadrosAbertura && !isGoToNextLevel() )
            drawAbertura( g );
        
        // desenha a animação de fechamento de tela
        if ( quadroAtualFechamento < quadrosFechamento && !isGoToNextLevel() )
            drawFechamento( g );
        
        // desenha a tela de passagem de fase (transferência de pontos)
        if ( isGoToNextLevel() )
            drawPointTransfer( g );
        
        // desenha tela de fim de jogo
        if ( isGameOver() )
            drawGameOver( g );
        
        // desenha a animação de pausa
        if ( isPaused() )
            drawPause( g );
        
    }
    
    
    /**
     * Obtém o mapa atual.
     */
    public TileMap getMap() {
        return map;
    }
    
    
    /**
     * Liga/desliga as baterias no midi (track1)
     */
    public void toggleDrumPlayback() {
        
        Sequencer sequencer = midiPlayerMusica.getSequencer();
        
        if ( sequencer != null ) {
            
            sequencer.setTrackMute( DRUM_TRACK,
                    !sequencer.getTrackMute( DRUM_TRACK ) );
            
        }
        
    }
    
    
    /**
     * Obtém o tile que a Sprite colide. Somente X ou Y da Sprite deve ser
     * mudado não ambos. Retorna null se nenhuma colisão for detectada.
     */
    public Point getTileCollision( Sprite sprite,
            float newX, float newY ) {
        
        float fromX = Math.min( sprite.getX(), newX );
        float fromY = Math.min( sprite.getY(), newY );
        float toX = Math.max( sprite.getX(), newX );
        float toY = Math.max( sprite.getY(), newY );
        
        // obtem a localização do tile
        int fromTileX = TileMapRenderer.pixelsToTiles( fromX );
        int fromTileY = TileMapRenderer.pixelsToTiles( fromY );
        int toTileX = TileMapRenderer.pixelsToTiles(
                toX + sprite.getWidth() - 1 );
        int toTileY = TileMapRenderer.pixelsToTiles(
                toY + sprite.getHeight() - 1 );
        
        // checa cada tile para verificar a colisão
        for ( int x = fromTileX; x <= toTileX; x++ ) {
            
            for ( int y = fromTileY; y <= toTileY; y++ ) {
                
                if ( x < 0 || x >= map.getWidth() ||
                        map.getTile( x, y ) != null ) {
                    // colisão achada, retorna o tile
                    pointCache.setLocation( x, y );
                    return pointCache;
                }
                
            }
            
        }
        
        // nenhuma colisão achada
        return null;
        
    }
    
    
    /**
     * Verifica se duas sprites colidiram entre si. Retorna false caso duas
     * Sprites sejam a mesma. Retorna false se um uma das Sprites não estiver
     * viva.
     */
    public boolean isCollision( Sprite s1, Sprite s2 ) {
        
        // se as sprites são a mesma, retorn false
        if ( s1 == s2 ) {
            return false;
        }
        
        // se uma das sprites é uma criatura morta, retorna false
        if ( s1 instanceof Creature && ! ( ( Creature ) s1 ).isAlive() ) {
            return false;
        }
        if ( s2 instanceof Creature && !( ( Creature ) s2 ).isAlive() ) {
            return false;
        }
        
        // obtem a localização em pixel das sprites
        int s1x = Math.round( s1.getX() );
        int s1y = Math.round( s1.getY() );
        int s2x = Math.round( s2.getX() );
        int s2y = Math.round( s2.getY() );
        
        // verifica se as bordas das sprites se interceptam
        return ( s1x < s2x + s2.getWidth() &&
                s2x < s1x + s1.getWidth() &&
                s1y < s2y + s2.getHeight() &&
                s2y < s1y + s1.getHeight() );
    }
    
    
    /**
     * Obtém a Sprite que colide com uma Sprite específica,
     * ou null se nenhum Sprite colide com a Sprite especificada.
     */
    public Sprite getSpriteCollision(Sprite sprite) {
        
        // itera pela lista de sprites
        Iterator i = map.getSprites();
        
        while ( i.hasNext() ) {
            
            Sprite otherSprite = ( Sprite ) i.next();
            
            if ( isCollision( sprite, otherSprite ) ) {
                
                // colisão encontrada, retorna a sprite
                return otherSprite;
                
            }
            
        }
        
        // sem colisão
        return null;
    }
    
    
    /**
     * Atualiza a animação, posição e velocidade de todas as sprites do mapa
     * atual.
     */
    public void update( long elapsedTime ) {       
        
        Creature player = ( Creature ) map.getPlayer();
        
        // jogador está morto, reinicia o mapa
        if ( player.getState() == Creature.STATE_DEAD ) {
            
            // se não há mais vidas, mostra a tela de fim de jogo e termina
            if ( quantidadeVidas == 0 ) {
                
                setGameOver( true );
                
            } else {
                
                // prepara abertura
                quadroAtualAbertura = 0;
                
                // recarrega o mapa atual
                map = resourceManager.reloadMap();
                
                // reinicia o som
                midiPlayerMusica.play( musica, true );
                
                // reseta os contadores de pontuação
                quantidadeCoins = 0;
                quantidadePontosFase = 0;
                
                return;
                
            }
            
        }
        
        // verifica a entrada do teclado/mouse
        checkInput( elapsedTime );
        
        // verifica se está pausado
        if ( !isPaused() && !isGoToNextLevel() ) {
            
            // atualiza o jogador
            updateCreature( player, elapsedTime );
            player.update( elapsedTime );
            
            // atualiza as outras sprites
            Iterator i = map.getSprites();
            
            while ( i.hasNext() ) {
                
                Sprite sprite = ( Sprite )i.next();
                
                if ( sprite instanceof Creature ) {
                    
                    Creature creature = ( Creature ) sprite;
                    
                    if ( creature.getState() == Creature.STATE_DEAD ) {
                        i.remove();
                    } else {
                        updateCreature( creature, elapsedTime );
                    }
                    
                }
                
                // atualização normal
                sprite.update( elapsedTime );
                
            }
            
        }
        
    }
    
    
    /**
     * Atualiza as criaturas, usando gravidade para as criaturas que não estão
     * voando e verifica colisão.
     */
    private void updateCreature( Creature creature, long elapsedTime ) {
        
        // usa gravidade
        if ( !creature.isFlying() ) {
            creature.setVelocityY( creature.getVelocityY() +
                    GRAVITY * elapsedTime );
        }
        
        // altera x
        float dx = creature.getVelocityX();
        float oldX = creature.getX();
        float newX = oldX + dx * elapsedTime;
        Point tile = getTileCollision( creature, newX, creature.getY() );
        
        if ( tile == null ) {
            creature.setX( newX );
        } else {
            
            // alinha com a borda do tile
            if ( dx > 0 ) {
                creature.setX(
                        TileMapRenderer.tilesToPixels( tile.x ) -
                        creature.getWidth() );
            } else if ( dx < 0 ) {
                creature.setX(
                        TileMapRenderer.tilesToPixels( tile.x + 1 ) );
            }
            creature.collideHorizontal();
        }
        
        if ( creature instanceof Player ) {
            checkPlayerCollision( ( Player ) creature, false );
        }
        
        // troca y
        float dy = creature.getVelocityY();
        float oldY = creature.getY();
        float newY = oldY + dy * elapsedTime;
        tile = getTileCollision( creature, creature.getX(), newY );
        
        if ( tile == null ) {
            creature.setY( newY );
        } else {
            // alinha com a borda do tile
            if ( dy > 0 ) {
                creature.setY(
                        TileMapRenderer.tilesToPixels( tile.y ) -
                        creature.getHeight() );
            } else if ( dy < 0 ) {
                creature.setY(
                        TileMapRenderer.tilesToPixels( tile.y + 1 ) );
            }
            creature.collideVertical();
        }
        if ( creature instanceof Player ) {
            boolean canKill = ( oldY < creature.getY() );
            checkPlayerCollision( ( Player ) creature, canKill );
        }
        
        // se o jogador cai (y muito alto), tira vida e reinicia
        if ( creature instanceof Player ) {
            
            // se o jogador está além do pixel 2000 de altura, morre
            if ( creature.getY() > 2000 ) {
                
                // se tem vidas
                if ( quantidadeVidas != 0 ) {
                    
                    // pára a música
                    midiPlayerMusica.stop();

                    // reproduz o som
                    soundManager.play( deathSound );

                    creature.setState( creature.STATE_DEAD );

                    quantidadeVidas--;

                    // dorme por 4 segundos para esperar a música ser reproduzida
                    try {
                        Thread.sleep( 4000 );
                    } catch ( InterruptedException exc ) { }
                    
                }
                
                // prepara fechamento
                // quadroAtualFechamento = 0;
                
            }
            
        }
        
    }
    
    
    /**
     * Verifica colisão entre o jogador e outras sprites. If canKill é true,
     * a colisão com as criaturas irá matá-las.
     */
    public void checkPlayerCollision( Player player,
            boolean canKill ) {
        
        if ( !player.isAlive() ) {
            return;
        }
        
        // verifica a colisão do jogador com outras Sprites
        Sprite collisionSprite = getSpriteCollision( player );
        
        if ( collisionSprite instanceof PowerUp ) {
            
            acquirePowerUp( ( PowerUp ) collisionSprite );
            
        } else if ( collisionSprite instanceof Creature ) {
            
            Creature badguy = ( Creature ) collisionSprite;
            
            if ( canKill ) {
                
                // mata o inimigo a faz o jogador oscilar
                soundManager.play( stompSound );
                
                badguy.setState( Creature.STATE_DYING );
                
                quantidadePontosFase += 100;
                
                player.setY( badguy.getY() - player.getHeight() );
                
                player.jump( true );
                
            } else {
                
                // jogador morre
                player.setState( Creature.STATE_DYING );
                
                // decrementa quantidade de vidas
                quantidadeVidas--;
                
                // pára a música
                midiPlayerMusica.stop();
                
                // reproduz o som
                soundManager.play( deathSound );
                
                // prepara fechamento
                // quadroAtualFechamento = 0;
                
            }
            
        }
        
    }
    
    
    /**
     * Dá ao jogador o powerUp especificado e remove o mesmo do mapa.
     */
    public void acquirePowerUp( PowerUp powerUp ) {
        
        // remove do mapa
        map.removeSprite( powerUp );
        
        if ( powerUp instanceof PowerUp.Coin ) {
            
            // soma 10 pontos
            quantidadePontosFase += 10;
            
            // soma um coin
            quantidadeCoins += 1;
            
            // reproduz o som
            soundManager.play( coinSound );
            
            // se tiver uma quantidade múltipla de 100
            if ( quantidadeCoins % 100 == 0 ) {
                
                // ganha uma vida
                quantidadeVidas++;
                
                // reproduz o som
                soundManager.play( oneUpSound );
                
            }
            
        } else if ( powerUp instanceof PowerUp.Mushroom ) {
            
            // soma 1000 pontos
            quantidadePontosFase += 1000;
            
            // altera a música
            soundManager.play( coinSound );
            
            // reproduz o som
            soundManager.play( powerUpSound );
            
        } else if ( powerUp instanceof PowerUp.FireFlower ) {
            
            // soma 1000 pontos
            quantidadePontosFase += 1000;
            
        } else if ( powerUp instanceof PowerUp.OneUp ) {
            
            // soma 1000 pontos
            quantidadePontosFase += 1000;
            
            // soma 1 vida
            quantidadeVidas++;
            
            // reproduz o som
            soundManager.play( oneUpSound );
            
        } else if ( powerUp instanceof PowerUp.Goal ) {
            
            // soma uma estrela
            quantidadeEstrelas++;
            
            // prepara fechamento
            quadroAtualAbertura = 0;
                
            // avança no mapa
            soundManager.play( powerUpSound );
            
            // sinaliza para ir para a próxima fase
            setGoToNextLevel( true );
            
            // pára a música e toca a música de nova fase
            midiPlayerMusica.stop();
            
            midiPlayerMusicaFinalFase.play( musicaFinalFase, false );
            
            //midiPlayer2
            
            // a responsabilidade de ir para o próximo mapa e carregar o fundo
            // é fo método drawPointTransfer
            
        }
        
    }
    
    
    /**
     * Método para desenhar a interface gráfica.
     */
    private void drawInterface( Graphics2D g2d ) {
        
        g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        
        g2d.drawImage( imagemInterfaceMario, 20, 20, null );
        g2d.drawImage( imagemInterfaceX, 30, 40, null );
        montaNumero( g2d, quantidadeVidas, 50, 42, 0 );
        
        g2d.drawImage( imagemInterfaceStar, 150, 22, null );
        g2d.drawImage( imagemInterfaceX, 170, 22, null );
        montaNumero( g2d, quantidadeEstrelas, 190, 24, 0 );
        
        g2d.drawImage( imagemInterfaceCoin, 280, 22, null );
        g2d.drawImage( imagemInterfaceX, 300, 22, null );
        montaNumero( g2d, quantidadeCoins, 320, 24, 0 );
        
        g2d.drawImage( imagemInterfacePontuacao, 410, 22, null );
        g2d.drawImage( imagemInterfaceX, 420, 40, null );
        montaNumero( g2d, quantidadePontosFase, 440, 42, 0 );
        
        g2d.drawImage( imagemInterfaceTotal, 560, 22, null );
        g2d.drawImage( imagemInterfaceX, 570, 40, null );
        montaNumero( g2d, quantidadePontos, 590, 42, 0 );
        
    }
    
    
    /**
     * Método que recebe um inteiro e monta as imagens correspondentes.
     */
    private void montaNumero( Graphics2D g2d, int numero, int x, int y, int kern  ) {
        
        char[] n = String.valueOf( numero ).toCharArray();
        
        for ( int i = 0; i < n.length; i++ ) {
            
            switch ( n[ i ] ) {
                case '0':
                    g2d.drawImage( imagemInterface0, x, y, null );
                    x += imagemInterface0.getWidth( null ) + kern;
                    break;
                case '1':
                    g2d.drawImage( imagemInterface1, x, y, null );
                    x += imagemInterface1.getWidth( null ) + kern;
                    break;
                case '2':
                    g2d.drawImage( imagemInterface2, x, y, null );
                    x += imagemInterface2.getWidth( null ) + kern;
                    break;
                case '3':
                    g2d.drawImage( imagemInterface3, x, y, null );
                    x += imagemInterface3.getWidth( null ) + kern;
                    break;
                case '4':
                    g2d.drawImage( imagemInterface4, x, y, null );
                    x += imagemInterface4.getWidth( null ) + kern;
                    break;
                case '5':
                    g2d.drawImage( imagemInterface5, x, y, null );
                    x += imagemInterface5.getWidth( null ) + kern;
                    break;
                case '6':
                    g2d.drawImage( imagemInterface6, x, y, null );
                    x += imagemInterface6.getWidth( null ) + kern;
                    break;
                case '7':
                    g2d.drawImage( imagemInterface7, x, y, null );
                    x += imagemInterface7.getWidth( null ) + kern;
                    break;
                case '8':
                    g2d.drawImage( imagemInterface8, x, y, null );
                    x += imagemInterface8.getWidth( null ) + kern;
                    break;
                case '9':
                    g2d.drawImage( imagemInterface9, x, y, null );
                    x += imagemInterface9.getWidth( null ) + kern;
                    break;
                    
            }
            
        }
        
    }
    
    
    /**
     * Desenha o que deve aparecer durante a pausa.
     */
    private void drawPause( Graphics2D g2d ) {
        
        int largura = screen.getFullScreenWindow().getWidth();
        int altura = screen.getFullScreenWindow().getHeight();
        
        g2d.setColor( new Color( 0, 0, 0, 50 ) );
        
        g2d.fillRect( 0, 0, largura, altura );
        
        g2d.drawImage( imagemInterfacePausa, 
                ( largura / 2 ) - ( imagemInterfacePausa.getWidth( null ) / 2 ),
                ( altura / 2 ) - ( imagemInterfacePausa.getHeight( null ) / 2 ), null );
        
    }
    
    
    /**
     * Desenha a abertura de tela (início e reinício de fase).
     */
    private void drawAbertura( Graphics2D g2d ) {
        
        int largura = screen.getFullScreenWindow().getWidth();
        int altura = screen.getFullScreenWindow().getHeight();
        int passoL = largura / quadrosAbertura;
        int passoA = altura / quadrosAbertura;
        
        g2d.setColor( Color.BLACK );
        
        g2d.fillRect( ( largura - ( ( quadrosAbertura* passoL ) - ( quadroAtualAbertura * passoL ) ) ) / 2 , 
                ( altura - ( ( quadrosAbertura * passoA ) - ( quadroAtualAbertura * passoA ) ) ) / 2, 
                ( quadrosAbertura * passoL ) - ( quadroAtualAbertura * passoL ), 
                ( quadrosAbertura * passoA ) - ( quadroAtualAbertura * passoA ) );
        
        quadroAtualAbertura++;
        
    }
    
    
    /**
     * Desenha o fechamento de tela (quando morre ou quando passa de fase).
     */
    private void drawFechamento( Graphics2D g2d ) {
        
        int largura = screen.getFullScreenWindow().getWidth();
        int altura = screen.getFullScreenWindow().getHeight();
        int passoL = largura / quadrosFechamento;
        int passoA = altura / quadrosFechamento;
        
        g2d.setColor( Color.BLACK );
        
        g2d.fillRect( largura / 2 - ( quadroAtualFechamento * passoL / 2 ), 
                altura / 2 - ( quadroAtualFechamento * passoA / 2 ), 
                ( quadroAtualFechamento * passoL ), 
                ( quadroAtualFechamento * passoA ) );
        
        quadroAtualFechamento++;
        
    }
    
    
    /**
     * Desenha a transferência de pontos.
     */
    private void drawPointTransfer( Graphics2D g2d ) {
        
        g2d.setColor( Color.BLACK );
        
        int largura = screen.getFullScreenWindow().getWidth();
        int altura = screen.getFullScreenWindow().getHeight();
        int xCabecalho = largura / 2 - imagemInterfaceEstagioFinalizado.getWidth( null ) / 2;
        int xJogo = largura / 2 - imagemInterfaceJogoFinalizado.getWidth( null ) / 2;
        int xPontuacao = largura / 2 - imagemInterfacePontuacao.getWidth( null ) / 2;
        
        // pára a música
        if ( !midiPlayerMusica.isPaused() )
            midiPlayerMusica.stop();
        
        // desenha o retângulo preto
        g2d.fillRect( 0, 0, largura, altura );
        
        if ( quantidadeEstrelas != QUANTIDADE_FASES )
            g2d.drawImage( imagemInterfaceEstagioFinalizado, xCabecalho,
                    100, null );
        else
            g2d.drawImage( imagemInterfaceJogoFinalizado, xJogo,
                    100, null );
        
        g2d.drawImage( imagemInterfacePontuacao, xPontuacao, 
                ( altura / 2 ) - 20, null );
        g2d.drawImage( imagemInterfaceX, xPontuacao + 20, 
                ( altura / 2 ) - 20 + 18, null );
        montaNumero( g2d, quantidadePontosFase, xPontuacao + 40, 
                ( altura / 2 ) - 20 + 20, 0 );
        
        g2d.drawImage( imagemInterfaceTotal, xPontuacao, 
                ( altura / 2 ) + 30, null );
        g2d.drawImage( imagemInterfaceX, xPontuacao + 20, 
                ( altura / 2 ) + 48, null );
        montaNumero( g2d, quantidadePontos, xPontuacao + 40, 
                ( altura / 2 ) + 50, 0 );
        
        // transfere os pontos
        if ( quantidadePontosFase > 0 ) {
            quantidadePontos += 10;
            quantidadePontosFase -= 10;
            soundManager.play( coinSound );
        }
        
        // se ja transferiu os pontos e se a espera terminou, carrega próxima fase
        if ( quantidadePontosFase == 0 && 
                !midiPlayerMusicaFinalFase.getSequencer().isRunning() ) {
            
            // sinaliza para ir para a próxima fase
            setGoToNextLevel( false );
            
            // seta o fundo
            renderer.setBackground( resourceManager.loadImage(
                    "background" + quantidadeEstrelas + ".png" ) );
            
            // carrega o próximo mapa
            map = resourceManager.loadNextMap();
            
            // para a música do final
            midiPlayerMusicaFinalFase.stop();
            
            // troca a música
            musica = midiPlayerMusica.getSequence( "music" + quantidadeEstrelas + ".midi" );
            
            // coloca a música para rodar de novo
            midiPlayerMusica.play( musica, true );
            
            // zera as moedas
            quantidadeCoins = 0;
            
        }
        
        
    }
    
    
    /**
     * Desenha a tela de fim de jogo.
     */
    private void drawGameOver( Graphics2D g2d ) {
        
        g2d.setColor( Color.BLACK );
        
        int largura = screen.getFullScreenWindow().getWidth();
        int altura = screen.getFullScreenWindow().getHeight();
        int x = largura / 2 - imagemInterfaceGameOver.getWidth( null ) / 2;
        int y = largura / 2 - imagemInterfaceGameOver.getWidth( null ) / 2;
        
        // pára a música
        if ( !midiPlayerMusica.isPaused() )
            midiPlayerMusica.stop();
        
        // toca a música de game over
        if ( midiPlayerMusicaGameOver.isPaused() );
            midiPlayerMusicaGameOver.play( musicaGameOver, false );
        
        // desenha o retângulo preto
        g2d.fillRect( 0, 0, largura, altura );
        
        g2d.drawImage( imagemInterfaceGameOver, x, y, null );
        
        // se todos os quadros já passaram, finaliza o jogo
        if ( quadroAtualGameOver == quadrosGameOver ) {
            
            stop();
            
        }
        
        // incrementa o quadro atual do fim de jogo
        quadroAtualGameOver++;
        
    }
    
    
    /**
     * Faz pausar a execução.
     */
    public void setPaused( boolean p ) {
        
        if ( isPaused() != p ) {
            this.paused = p;
            inputManager.resetAllGameActions();
        }
        
    }
    
    
    public boolean isPaused() {
        return paused;
    }

    public boolean isGoToNextLevel() {
        return goToNextLevel;
    }

    public void setGoToNextLevel(boolean goToNextLevel) {
        this.goToNextLevel = goToNextLevel;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
    
}