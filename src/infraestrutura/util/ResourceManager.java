package infraestrutura.util;

import infraestrutura.grafico.*;
import jogo.tile.*;
import jogo.sprites.*;
import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;

/**
 * A classe ResourceManager carrega e gerencia os pedaços (tiles) de Images e 
 * as Sprites "hospedeiras" usadas no jogo. As sprites do Jogo são clonadas a 
 * partir das Sprites hospedeiras.
 *
 * @author David Buzatto
 */
public class ResourceManager {
    
    private ArrayList< Image > tiles;
    private int currentMap;
    private GraphicsConfiguration gc;
    
    // sprites hospedeiras usadas na clonagem
    private Sprite playerSprite;
    private Sprite mushroomSprite;
    private Sprite coinSprite;
    private Sprite fireFlowerSprite;
    private Sprite oneUpSprite;
    private Sprite goalSprite;
    private Sprite goombaSprite;
    private Sprite flyGoombaSprite;
    private Sprite greenKoopaSprite;
    private Sprite redKoopaSprite;
    private Sprite blueKoopaSprite;
    private Sprite yellowKoopaSprite;
    private Sprite bombSprite;
    private Sprite bulletSprite;
    private Sprite batSprite;
    private Sprite turtleSprite;
    private Sprite mummyTurtleSprite;
    private Sprite blueDragonSprite;
    
    
    /**
     * Cria um novo ResourceManager com o GraphicsConfiguration especificado e 
     * a letra do último tile.
     */
    public ResourceManager( GraphicsConfiguration gc, char maxTileLetter ) {
        this.gc = gc;
        loadTileImages( maxTileLetter );
        loadCreatureSprites();
        loadPowerUpSprites();
    }
    
    
    /**
     * Obtem uma imagem do diretório /recursos/imagens/
     */
    public Image loadImage( String name ) {
        String filename = "/recursos/imagens/" + name;
        return new ImageIcon( getClass().getResource( filename ) ).getImage();
    }
    
    
    /**
     * Cria uma imagem espelhada.
     */
    public Image getMirrorImage( Image image ) {
        return getScaledImage( image, -1, 1 );
    }
    
    
    /**
     * Cria uma imagem rotacionada.
     */
    public Image getFlippedImage( Image image ) {
        return getScaledImage(image, 1, -1);
    }
    
    
    /**
     * Aplica operação de escala em uma imagem.
     */
    private Image getScaledImage( Image image, float x, float y ) {
        
        // configura a transformação
        AffineTransform transform = new AffineTransform();
        transform.scale( x, y );
        transform.translate(
                ( x - 1 ) * image.getWidth( null ) / 2,
                ( y - 1 ) * image.getHeight( null ) / 2);
        
        // cria uma imagem transparente (não translúcida)
        Image newImage = gc.createCompatibleImage(
                image.getWidth( null ),
                image.getHeight( null ),
                Transparency.BITMASK );
        
        // desenha a imagem transformada
        Graphics2D g = ( Graphics2D ) newImage.getGraphics();
        g.drawImage( image, transform, null );
        g.dispose();
        
        return newImage;
    }
    
    
    /**
     * Aplica operação de escala esmagando uma figura.
     */
    private Image getSmashedImage( Image image ) {
        
        // configura a transformação
        AffineTransform transform = new AffineTransform();
        transform.scale( 1, 0.5 );
        transform.translate( 0, image.getHeight( null ) );
        
        // cria uma imagem transparente (não translúcida)
        Image newImage = gc.createCompatibleImage(
                image.getWidth( null ),
                image.getHeight( null ),
                Transparency.BITMASK );
        
        // desenha a imagem transformada
        Graphics2D g = ( Graphics2D ) newImage.getGraphics();
        g.drawImage( image, transform, null );
        g.dispose();
        
        return newImage;
    }
    
    
    /**
     * Carrega o próximo mapa.
     */
    public TileMap loadNextMap() {
        TileMap map = null;
        while ( map == null ) {
            currentMap++;
            try {
                map = loadMap( "map" + currentMap + ".txt");
            } catch ( IOException ex ) {
                if ( currentMap == 1 ) {
                    // nenhum para carregado
                    return null;
                }
                currentMap = 0;
                map = null;
            }
        }
        
        return map;
    }
    
    
    /**
     * Recarrega o mapa atual.
     */
    public TileMap reloadMap() {
        try {
            return loadMap( "map" + currentMap + ".txt" );
        } catch ( IOException ex ) {
            ex.printStackTrace();
            return null;
        }
    }
    
    
    /**
     * Carrega um mapa do diretório /recursos/mapas/
     */
    private TileMap loadMap( String name )
            throws IOException {
        
        String filename = "/recursos/mapas/" + name;
        
        ArrayList< String > lines = new ArrayList< String >();
        int width = 0;
        int height = 0;
        BufferedReader reader;
        
        try {
            
            reader = new BufferedReader( new InputStreamReader( 
                    getClass().getResourceAsStream( filename ) ) );
            
            while ( true ) {
                String line = reader.readLine();
                // sem mais linhas para ler
                if ( line == null ) {
                    reader.close();
                    break;
                }
                
                // adiciona toda linha menos os comentários
                if ( !line.startsWith( "#" ) ) {
                    lines.add( line );
                    width = Math.max( width, line.length() );
                }
            }
            
            // parseia as linhas para criar uma TileEngine
            height = lines.size();
            TileMap newMap = new TileMap( width, height );
            
            for ( int y = 0; y < height; y++ ) {
                
                String line = ( String ) lines.get( y );
                for ( int x = 0; x < line.length(); x++ ) {
                    char ch = line.charAt( x );
                    
                    // verifica o tile que o caracter atual representa
                    int tile = ch - 'A';
                    if ( tile >= 0 && tile < tiles.size() ) {
                        newMap.setTile( x, y, ( Image ) tiles.get(tile) );
                    }
                    
                    // checa se o caracter representa umaa sprite
                    else if ( ch == 'o' ) {
                        addSprite( newMap, coinSprite, x, y );
                    } else if ( ch == '!' ) {
                        addSprite( newMap, mushroomSprite, x, y );
                    } else if ( ch == 'f' ) {
                        addSprite( newMap, fireFlowerSprite, x, y );
                    } else if ( ch == 'u' ) {
                        addSprite( newMap, oneUpSprite, x, y );
                    } else if ( ch == '*' ) {
                        addSprite( newMap, goalSprite, x, y );
                    } else if ( ch == '1' ) {
                        addSprite( newMap, goombaSprite, x, y );
                    } else if ( ch == '2' ) {
                        addSprite( newMap, flyGoombaSprite, x, y );
                    } else if ( ch == '3' ) {
                        addSprite( newMap, greenKoopaSprite, x, y );
                    } else if ( ch == '4' ) {
                        addSprite( newMap, redKoopaSprite, x, y );
                    } else if ( ch == '5' ) {
                        addSprite( newMap, blueKoopaSprite, x, y );
                    } else if ( ch == '6' ) {
                        addSprite( newMap, yellowKoopaSprite, x, y );
                    } else if ( ch == '7' ) {
                        addSprite( newMap, bombSprite, x, y );
                    } else if ( ch == '8' ) {
                        addSprite( newMap, bulletSprite, x, y );
                    } else if ( ch == '9' ) {
                        addSprite( newMap, batSprite, x, y );
                    } else if ( ch == '@' ) {
                        addSprite( newMap, turtleSprite, x, y );
                    } else if ( ch == '$' ) {
                        addSprite( newMap, mummyTurtleSprite, x, y );
                    } else if ( ch == '%' ) {
                        addSprite( newMap, blueDragonSprite, x, y );
                    }
                    
                    
                }
            }
            
            // adiciona o jogador no mapa
            Sprite player = ( Sprite ) playerSprite.clone();
            player.setX( TileMapRenderer.tilesToPixels( 3 ) );
            player.setY( 0 );
            newMap.setPlayer( player );
            return newMap;
            
        } catch ( FileNotFoundException ex ) {
            ex.printStackTrace();
        }
        
        return null;
    }
    
    
    /**
     * Adiciona uma Sprite em uma mapa.
     */
    private void addSprite( TileMap map,
            Sprite hostSprite, int tileX, int tileY ) {
        
        if ( hostSprite != null ) {
            // clona a sprite usando a hospedeira
            Sprite sprite = ( Sprite ) hostSprite.clone();
            
            // centraliza a sprite
            sprite.setX(
                    TileMapRenderer.tilesToPixels( tileX ) +
                    (TileMapRenderer.tilesToPixels( 1 ) -
                    sprite.getWidth() ) / 2 );
            
            // alinha a sprite no chão
            sprite.setY(
                    TileMapRenderer.tilesToPixels( tileY + 1 ) -
                    sprite.getHeight() );
            
            // adiciona no mapa
            map.addSprite(sprite);
        }
    }
    
    
    /* -----------------------------------------------------------
     * Código para carga de sprites e imagens.
     * ---------------------------------------------------------*/
    
    
    /**
     * Carrega as imagens dos tiles usando um caracter máximo.
     */
    public void loadTileImages( char max ) {
        
        tiles = new ArrayList< Image >();
        
        char ch = 'A';
        
        while ( ch <= max ) {
            
            String name = "tile_" + ch + ".png";
            tiles.add( loadImage( name ) );
            ch++;
            
        }
        
    }
    
    
    /**
     * Carrega as sprites das criaturas.
     */
    public void loadCreatureSprites() {
        
        // o número de linhas indica a quantidade de estados da sprite
        Image[][] imagensMario = new Image[ 2 ][];
        Image[][] imagensMarioAndando = new Image[ 2 ][];
        Image[][] imagensMarioPulando = new Image[ 2 ][];
        Image[][] imagensMarioMorrendo = new Image[ 2 ][];
        Image[][] imagensMarioAbaixado = new Image[ 2 ][];
        Image[][] imagensFlyGoomba = new Image[ 4 ][];
        Image[][] imagensGoomba = new Image[ 4 ][];
        Image[][] imagensGreenKoopa = new Image[ 4 ][];
        Image[][] imagensRedKoopa = new Image[ 4 ][];
        Image[][] imagensBlueKoopa = new Image[ 4 ][];
        Image[][] imagensYellowKoopa = new Image[ 4 ][];
        Image[][] imagensBomb = new Image[ 4 ][];
        Image[][] imagensBullet = new Image[ 4 ][];
        Image[][] imagensBat = new Image[ 4 ][];
        Image[][] imagensTurtle = new Image[ 4 ][];
        Image[][] imagensMummyTurtle = new Image[ 4 ][];
        Image[][] imagensBlueDragon = new Image[ 4 ][];
        
        // carrega imagens viradas à esquerda para o sonic
        imagensMario[ 0 ] = new Image[] {
            loadImage( "mario1.png" )
        };
        
        imagensMarioAndando[ 0 ] = new Image[] {
            loadImage( "mario1.png" ),
            loadImage( "mario2.png" )
        };
        
        imagensMarioPulando[ 0 ] = new Image[] {
            loadImage( "marioJump1.png" ),
            loadImage( "marioJump2.png" )
        };
        
        imagensMarioMorrendo[ 0 ] = new Image[] {
            loadImage( "marioDying1.png" ),
            loadImage( "marioDying2.png" )
        };
        
        imagensMarioAbaixado[ 0 ] = new Image[] {
            loadImage( "marioDown1.png" )
        };
        
        // carrega imagens viradas à esquerda para o flyGoomba
        imagensFlyGoomba[ 0 ] = new Image[] {
            loadImage( "flyGoomba1.png" ),
            loadImage( "flyGoomba2.png" )
        };
        
        // carrega imagens viradas à esquerda para o goomba
        imagensGoomba[ 0 ] = new Image[] {
            loadImage( "goomba1.png" ),
            loadImage( "goomba2.png" )
        };
        
        // carrega imagens viradas à esquerda para o green koopa
        imagensGreenKoopa[ 0 ] = new Image[] {
            loadImage( "greenKoopa1.png" ),
            loadImage( "greenKoopa2.png" )
        };
        
        // carrega imagens viradas à esquerda para o red koopa
        imagensRedKoopa[ 0 ] = new Image[] {
            loadImage( "redKoopa1.png" ),
            loadImage( "redKoopa2.png" )
        };
        
        // carrega imagens viradas à esquerda para o blue koopa
        imagensBlueKoopa[ 0 ] = new Image[] {
            loadImage( "blueKoopa1.png" ),
            loadImage( "blueKoopa2.png" )
        };
        
        // carrega imagens viradas à esquerda para o yellow koopa
        imagensYellowKoopa[ 0 ] = new Image[] {
            loadImage( "yellowKoopa1.png" ),
            loadImage( "yellowKoopa2.png" )
        };
        
        // carrega imagens viradas à esquerda para o bomb
        imagensBomb[ 0 ] = new Image[] {
            loadImage( "bomb1.png" ),
            loadImage( "bomb2.png" )
        };
        
        // carrega imagens viradas à esquerda para o bullet
        imagensBullet[ 0 ] = new Image[] {
            loadImage( "bullet1.png" )
        };
        
        // carrega imagens viradas à esquerda para o bat
        imagensBat[ 0 ] = new Image[] {
            loadImage( "bat1.png" ),
            loadImage( "bat2.png" )
        };
        
        // carrega imagens viradas à esquerda para o turtle
        imagensTurtle[ 0 ] = new Image[] {
            loadImage( "turtle1.png" ),
            loadImage( "turtle2.png" )
        };
        
        // carrega imagens viradas à esquerda para o mummy turtle
        imagensMummyTurtle[ 0 ] = new Image[] {
            loadImage( "mummyTurtle1.png" ),
            loadImage( "mummyTurtle2.png" )
        };
        
        // carrega imagens viradas à esquerda para o blue dragon
        imagensBlueDragon[ 0 ] = new Image[] {
            loadImage( "blueDragon1.png" ),
            loadImage( "blueDragon2.png" )
        };
        
        // para cada estado, cria um novo array com o tamanho da primeira linha
        imagensMario[ 1 ] = new Image[ imagensMario[ 0 ].length ];
        imagensMarioAndando[ 1 ] = new Image[ imagensMarioAndando[ 0 ].length ];
        imagensMarioPulando[ 1 ] = new Image[ imagensMarioPulando[ 0 ].length ];
        imagensMarioMorrendo[ 1 ] = new Image[ imagensMarioMorrendo[ 0 ].length ];
        imagensMarioAbaixado[ 1 ] = new Image[ imagensMarioAbaixado[ 0 ].length ];
        
        imagensFlyGoomba[ 1 ] = new Image[ imagensFlyGoomba[ 0 ].length ];
        imagensFlyGoomba[ 2 ] = new Image[ imagensFlyGoomba[ 0 ].length ];
        imagensFlyGoomba[ 3 ] = new Image[ imagensFlyGoomba[ 0 ].length ];
        
        imagensGoomba[ 1 ] = new Image[ imagensGoomba[ 0 ].length ];
        imagensGoomba[ 2 ] = new Image[ imagensGoomba[ 0 ].length ];
        imagensGoomba[ 3 ] = new Image[ imagensGoomba[ 0 ].length ];
        
        imagensGreenKoopa[ 1 ] = new Image[ imagensGreenKoopa[ 0 ].length ];
        imagensGreenKoopa[ 2 ] = new Image[ imagensGreenKoopa[ 0 ].length ];
        imagensGreenKoopa[ 3 ] = new Image[ imagensGreenKoopa[ 0 ].length ];
        
        imagensRedKoopa[ 1 ] = new Image[ imagensRedKoopa[ 0 ].length ];
        imagensRedKoopa[ 2 ] = new Image[ imagensRedKoopa[ 0 ].length ];
        imagensRedKoopa[ 3 ] = new Image[ imagensRedKoopa[ 0 ].length ];
        
        imagensBlueKoopa[ 1 ] = new Image[ imagensBlueKoopa[ 0 ].length ];
        imagensBlueKoopa[ 2 ] = new Image[ imagensBlueKoopa[ 0 ].length ];
        imagensBlueKoopa[ 3 ] = new Image[ imagensBlueKoopa[ 0 ].length ];
        
        imagensYellowKoopa[ 1 ] = new Image[ imagensYellowKoopa[ 0 ].length ];
        imagensYellowKoopa[ 2 ] = new Image[ imagensYellowKoopa[ 0 ].length ];
        imagensYellowKoopa[ 3 ] = new Image[ imagensYellowKoopa[ 0 ].length ];
        
        imagensBomb[ 1 ] = new Image[ imagensBomb[ 0 ].length ];
        imagensBomb[ 2 ] = new Image[ imagensBomb[ 0 ].length ];
        imagensBomb[ 3 ] = new Image[ imagensBomb[ 0 ].length ];
        
        imagensBullet[ 1 ] = new Image[ imagensBullet[ 0 ].length ];
        imagensBullet[ 2 ] = new Image[ imagensBullet[ 0 ].length ];
        imagensBullet[ 3 ] = new Image[ imagensBullet[ 0 ].length ];
        
        imagensBat[ 1 ] = new Image[ imagensBat[ 0 ].length ];
        imagensBat[ 2 ] = new Image[ imagensBat[ 0 ].length ];
        imagensBat[ 3 ] = new Image[ imagensBat[ 0 ].length ];
        
        imagensTurtle[ 1 ] = new Image[ imagensTurtle[ 0 ].length ];
        imagensTurtle[ 2 ] = new Image[ imagensTurtle[ 0 ].length ];
        imagensTurtle[ 3 ] = new Image[ imagensTurtle[ 0 ].length ];
        
        imagensMummyTurtle[ 1 ] = new Image[ imagensMummyTurtle[ 0 ].length ];
        imagensMummyTurtle[ 2 ] = new Image[ imagensMummyTurtle[ 0 ].length ];
        imagensMummyTurtle[ 3 ] = new Image[ imagensMummyTurtle[ 0 ].length ];
        
        imagensBlueDragon[ 1 ] = new Image[ imagensBlueDragon[ 0 ].length ];
        imagensBlueDragon[ 2 ] = new Image[ imagensBlueDragon[ 0 ].length ];
        imagensBlueDragon[ 3 ] = new Image[ imagensBlueDragon[ 0 ].length ];
        
        // para cada coluna do array, configura as imagens correpondentes aos estados.
        for ( int i = 0; i < imagensMario[ 0 ].length; i++ ) {
            // imagens viradas à direita
            imagensMario[ 1 ][ i ] = getMirrorImage( imagensMario[ 0 ][ i ] );
        }
        
        for ( int i = 0; i < imagensMarioAndando[ 0 ].length; i++ ) {
            // imagens viradas à direita
            imagensMarioAndando[ 1 ][ i ] = getMirrorImage( imagensMarioAndando[ 0 ][ i ] );
        }
        
        for ( int i = 0; i < imagensMarioPulando[ 0 ].length; i++ ) {
            // imagens viradas à direita
            imagensMarioPulando[ 1 ][ i ] = getMirrorImage( imagensMarioPulando[ 0 ][ i ] );
        }
        
        for ( int i = 0; i < imagensMarioAndando[ 0 ].length; i++ ) {
            // imagens viradas à direita
            imagensMarioAndando[ 1 ][ i ] = getMirrorImage( imagensMarioAndando[ 0 ][ i ] );
        }
        
        for ( int i = 0; i < imagensMarioMorrendo[ 0 ].length; i++ ) {
            // imagens viradas à direita
            imagensMarioMorrendo[ 1 ][ i ] = getMirrorImage( imagensMarioMorrendo[ 0 ][ i ] );
        }
        
        for ( int i = 0; i < imagensMarioAbaixado[ 0 ].length; i++ ) {
            // imagens viradas à direita
            imagensMarioAbaixado[ 1 ][ i ] = getMirrorImage( imagensMarioAbaixado[ 0 ][ i ] );
        }
        
        for ( int i = 0; i < imagensFlyGoomba[ 0 ].length; i++ ) {
            // imagens viradas à direita
            imagensFlyGoomba[ 1 ][ i ] = getMirrorImage( imagensFlyGoomba[ 0 ][ i ] );
            // imagens viradas à esquerda "mortas"
            imagensFlyGoomba[ 2 ][ i ] = getSmashedImage( imagensFlyGoomba[ 0 ][ i ] );
            // imagens viradas à direita "mortas"
            imagensFlyGoomba[ 3 ][ i ] = getSmashedImage( imagensFlyGoomba[ 1 ][ i ] );
        }
        
        for ( int i = 0; i < imagensGoomba[ 0 ].length; i++ ) {
            // imagens viradas à direita
            imagensGoomba[ 1 ][ i ] = getMirrorImage( imagensGoomba[ 0 ][ i ] );
            // imagens viradas à esquerda "mortas"
            imagensGoomba[ 2 ][ i ] = getSmashedImage( imagensGoomba[ 0 ][ i ] );
            // imagens viradas à direita "mortas"
            imagensGoomba[ 3 ][ i ] = getSmashedImage( imagensGoomba[ 1 ][ i ] );
        }
        
        for ( int i = 0; i < imagensGreenKoopa[ 0 ].length; i++ ) {
            // imagens viradas à direita
            imagensGreenKoopa[ 1 ][ i ] = getMirrorImage( imagensGreenKoopa[ 0 ][ i ] );
            // imagens viradas à esquerda "mortas"
            imagensGreenKoopa[ 2 ][ i ] = getSmashedImage( imagensGreenKoopa[ 0 ][ i ] );
            // imagens viradas à direita "mortas"
            imagensGreenKoopa[ 3 ][ i ] = getSmashedImage( imagensGreenKoopa[ 1 ][ i ] );
        }
        
        for ( int i = 0; i < imagensRedKoopa[ 0 ].length; i++ ) {
            // imagens viradas à direita
            imagensRedKoopa[ 1 ][ i ] = getMirrorImage( imagensRedKoopa[ 0 ][ i ] );
            // imagens viradas à esquerda "mortas"
            imagensRedKoopa[ 2 ][ i ] = getSmashedImage( imagensRedKoopa[ 0 ][ i ] );
            // imagens viradas à direita "mortas"
            imagensRedKoopa[ 3 ][ i ] = getSmashedImage( imagensRedKoopa[ 1 ][ i ] );
        }
        
        for ( int i = 0; i < imagensBlueKoopa[ 0 ].length; i++ ) {
            // imagens viradas à direita
            imagensBlueKoopa[ 1 ][ i ] = getMirrorImage( imagensBlueKoopa[ 0 ][ i ] );
            // imagens viradas à esquerda "mortas"
            imagensBlueKoopa[ 2 ][ i ] = getSmashedImage( imagensBlueKoopa[ 0 ][ i ] );
            // imagens viradas à direita "mortas"
            imagensBlueKoopa[ 3 ][ i ] = getSmashedImage( imagensBlueKoopa[ 1 ][ i ] );
        }
        
        for ( int i = 0; i < imagensYellowKoopa[ 0 ].length; i++ ) {
            // imagens viradas à direita
            imagensYellowKoopa[ 1 ][ i ] = getMirrorImage( imagensYellowKoopa[ 0 ][ i ] );
            // imagens viradas à esquerda "mortas"
            imagensYellowKoopa[ 2 ][ i ] = getSmashedImage( imagensYellowKoopa[ 0 ][ i ] );
            // imagens viradas à direita "mortas"
            imagensYellowKoopa[ 3 ][ i ] = getSmashedImage( imagensYellowKoopa[ 1 ][ i ] );
        }
        
        for ( int i = 0; i < imagensBomb[ 0 ].length; i++ ) {
            // imagensBomb viradas à direita
            imagensBomb[ 1 ][ i ] = getMirrorImage( imagensBomb[ 0 ][ i ] );
            // imagensBomb viradas à esquerda "mortas"
            imagensBomb[ 2 ][ i ] = getSmashedImage( imagensBomb[ 0 ][ i ] );
            // imagensBomb viradas à direita "mortas"
            imagensBomb[ 3 ][ i ] = getSmashedImage( imagensBomb[ 1 ][ i ] );
        }
        
        for ( int i = 0; i < imagensBullet[ 0 ].length; i++ ) {
            // imagensBullet viradas à direita
            imagensBullet[ 1 ][ i ] = getMirrorImage( imagensBullet[ 0 ][ i ] );
            // imagensBullet viradas à esquerda "mortas"
            imagensBullet[ 2 ][ i ] = getSmashedImage( imagensBullet[ 0 ][ i ] );
            // imagensBullet viradas à direita "mortas"
            imagensBullet[ 3 ][ i ] = getSmashedImage( imagensBullet[ 1 ][ i ] );
        }
        
        for ( int i = 0; i < imagensBat[ 0 ].length; i++ ) {
            // imagensBat viradas à direita
            imagensBat[ 1 ][ i ] = getMirrorImage( imagensBat[ 0 ][ i ] );
            // imagensBat viradas à esquerda "mortas"
            imagensBat[ 2 ][ i ] = getSmashedImage( imagensBat[ 0 ][ i ] );
            // imagensBat viradas à direita "mortas"
            imagensBat[ 3 ][ i ] = getSmashedImage( imagensBat[ 1 ][ i ] );
        }
        
        for ( int i = 0; i < imagensTurtle[ 0 ].length; i++ ) {
            // imagensTurtle viradas à direita
            imagensTurtle[ 1 ][ i ] = getMirrorImage( imagensTurtle[ 0 ][ i ] );
            // imagensTurtle viradas à esquerda "mortas"
            imagensTurtle[ 2 ][ i ] = getSmashedImage( imagensTurtle[ 0 ][ i ] );
            // imagensTurtle viradas à direita "mortas"
            imagensTurtle[ 3 ][ i ] = getSmashedImage( imagensTurtle[ 1 ][ i ] );
        }
        
        for ( int i = 0; i < imagensMummyTurtle[ 0 ].length; i++ ) {
            // imagensMummyTurtle viradas à direita
            imagensMummyTurtle[ 1 ][ i ] = getMirrorImage( imagensMummyTurtle[ 0 ][ i ] );
            // imagensMummyTurtle viradas à esquerda "mortas"
            imagensMummyTurtle[ 2 ][ i ] = getSmashedImage( imagensMummyTurtle[ 0 ][ i ] );
            // imagensMummyTurtle viradas à direita "mortas"
            imagensMummyTurtle[ 3 ][ i ] = getSmashedImage( imagensMummyTurtle[ 1 ][ i ] );
        }
        
        for ( int i = 0; i < imagensBlueDragon[ 0 ].length; i++ ) {
            // imagensBlueDragon viradas à direita
            imagensBlueDragon[ 1 ][ i ] = getMirrorImage( imagensBlueDragon[ 0 ][ i ] );
            // imagensBlueDragon viradas à esquerda "mortas"
            imagensBlueDragon[ 2 ][ i ] = getSmashedImage( imagensBlueDragon[ 0 ][ i ] );
            // imagensBlueDragon viradas à direita "mortas"
            imagensBlueDragon[ 3 ][ i ] = getSmashedImage( imagensBlueDragon[ 1 ][ i ] );
        }
        
        
        
        // cria as animações das criaturas, baseado na quantidade de estados
        Animation[] playerAnim = new Animation[ imagensMario.length ];
        Animation[] playerAnimAndando = new Animation[ imagensMarioAndando.length ];
        Animation[] playerAnimPulando = new Animation[ imagensMarioPulando.length ];
        Animation[] playerAnimMorrendo = new Animation[ imagensMarioMorrendo.length ];
        Animation[] playerAnimAbaixado = new Animation[ imagensMarioAbaixado.length ];
        Animation[] flyGoombaAnim = new Animation[ imagensFlyGoomba.length ];
        Animation[] goombaAnim = new Animation[ imagensGoomba.length ];
        Animation[] greenKoopaAnim = new Animation[ imagensGreenKoopa.length ];
        Animation[] redKoopaAnim = new Animation[ imagensRedKoopa.length ];
        Animation[] blueKoopaAnim = new Animation[ imagensBlueKoopa.length ];
        Animation[] yellowKoopaAnim = new Animation[ imagensYellowKoopa.length ];
        Animation[] bombAnim = new Animation[ imagensBomb.length ];
        Animation[] bulletAnim = new Animation[ imagensBullet.length ];
        Animation[] batAnim = new Animation[ imagensBat.length ];
        Animation[] turtleAnim = new Animation[ imagensTurtle.length ];
        Animation[] mummyTurtleAnim = new Animation[ imagensMummyTurtle.length ];
        Animation[] blueDragonAnim = new Animation[ imagensBlueDragon.length ];
        
        
        // itera pela quantidade de estados, criando uma animação para cada um deles
        for ( int i = 0; i < playerAnim.length; i++ ) {
            playerAnim[ i ] = createPlayerAnim( imagensMario[ i ][ 0 ] );
        }
        
        for ( int i = 0; i < playerAnimAndando.length; i++ ) {
            playerAnimAndando[ i ] = createPlayerAnimAndando( imagensMarioAndando[ i ][ 0 ],
                    imagensMarioAndando[ i ][ 1 ] );
        }
        
        for ( int i = 0; i < playerAnimPulando.length; i++ ) {
            playerAnimPulando[ i ] = createPlayerAnimPulando( imagensMarioPulando[ i ][ 0 ],
                    imagensMarioPulando[ i ][ 1 ] );
        }
        
        for ( int i = 0; i < playerAnimMorrendo.length; i++ ) {
            playerAnimMorrendo[ i ] = createPlayerAnimMorrendo( imagensMarioMorrendo[ i ][ 0 ],
                    imagensMarioMorrendo[ i ][ 1 ] );
        }
        
        for ( int i = 0; i < playerAnimAbaixado.length; i++ ) {
            playerAnimAbaixado[ i ] = createPlayerAnimAbaixado( imagensMarioAbaixado[ i ][ 0 ] );
        }
        
        for ( int i = 0; i < flyGoombaAnim.length; i++ ) {
            flyGoombaAnim[ i ] = createFlyGoombaAnim(
                    imagensFlyGoomba[ i ][ 0 ], imagensFlyGoomba[ i ][ 1 ] );
        }
        
        for ( int i = 0; i < goombaAnim.length; i++ ) {
            goombaAnim[ i ] = createGoombaAnim(
                    imagensGoomba[ i ][ 0 ], imagensGoomba[ i ][ 1 ] );
        }
        
        for ( int i = 0; i < greenKoopaAnim.length; i++ ) {
            greenKoopaAnim[ i ] = createGreenKoopaAnim(
                    imagensGreenKoopa[ i ][ 0 ], imagensGreenKoopa[ i ][ 1 ] );
        }
        
        for ( int i = 0; i < redKoopaAnim.length; i++ ) {
            redKoopaAnim[ i ] = createRedKoopaAnim(
                    imagensRedKoopa[ i ][ 0 ], imagensRedKoopa[ i ][ 1 ] );
        }
        
        for ( int i = 0; i < blueKoopaAnim.length; i++ ) {
            blueKoopaAnim[ i ] = createBlueKoopaAnim(
                    imagensBlueKoopa[ i ][ 0 ], imagensBlueKoopa[ i ][ 1 ] );
        }
        
        for ( int i = 0; i < yellowKoopaAnim.length; i++ ) {
            yellowKoopaAnim[ i ] = createYellowKoopaAnim(
                    imagensYellowKoopa[ i ][ 0 ], imagensYellowKoopa[ i ][ 1 ] );
        }
        
        for ( int i = 0; i < bombAnim.length; i++ ) {
            bombAnim[ i ] = createBombAnim(
                    imagensBomb[ i ][ 0 ], imagensBomb[ i ][ 1 ] );
        }
        
        for ( int i = 0; i < bulletAnim.length; i++ ) {
            bulletAnim[ i ] = createBulletAnim(
                    imagensBullet[ i ][ 0 ] );
        }
        
        for ( int i = 0; i < batAnim.length; i++ ) {
            batAnim[ i ] = createBatAnim(
                    imagensBat[ i ][ 0 ], imagensBat[ i ][ 1 ] );
        }
        
        for ( int i = 0; i < turtleAnim.length; i++ ) {
            turtleAnim[ i ] = createBatAnim(
                    imagensTurtle[ i ][ 0 ], imagensTurtle[ i ][ 1 ] );
        }
        
        for ( int i = 0; i < mummyTurtleAnim.length; i++ ) {
            mummyTurtleAnim[ i ] = createBatAnim(
                    imagensMummyTurtle[ i ][ 0 ], imagensMummyTurtle[ i ][ 1 ] );
        }
        
        for ( int i = 0; i < blueDragonAnim.length; i++ ) {
            blueDragonAnim[ i ] = createBatAnim(
                    imagensBlueDragon[ i ][ 0 ], imagensBlueDragon[ i ][ 1 ] );
        }
        
        // cria as sprites das criaturas, usando as animações criadas
        playerSprite = new Player( playerAnim[ 0 ], playerAnim[ 1 ], 
                playerAnimMorrendo[ 0 ], playerAnimMorrendo[ 1 ],
                playerAnimAndando[ 0 ], playerAnimAndando[ 1 ],
                playerAnimPulando[ 0 ], playerAnimPulando[ 1 ],
                playerAnimAbaixado[ 0 ], playerAnimAbaixado[ 1 ] );
        
        flyGoombaSprite = new FlyGoomba( flyGoombaAnim[ 0 ], flyGoombaAnim[ 1 ],
                flyGoombaAnim[ 2 ], flyGoombaAnim[ 3 ] );
        
        goombaSprite = new Goomba( goombaAnim[ 0 ], goombaAnim[ 1 ],
                goombaAnim[ 2 ], goombaAnim[ 3 ] );
        
        greenKoopaSprite = new GreenKoopa( greenKoopaAnim[ 0 ], greenKoopaAnim[ 1 ],
                greenKoopaAnim[ 2 ], greenKoopaAnim[ 3 ] );
        
        redKoopaSprite = new RedKoopa( redKoopaAnim[ 0 ], redKoopaAnim[ 1 ],
                redKoopaAnim[ 2 ], redKoopaAnim[ 3 ] );
        
        blueKoopaSprite = new BlueKoopa( blueKoopaAnim[ 0 ], blueKoopaAnim[ 1 ],
                blueKoopaAnim[ 2 ], blueKoopaAnim[ 3 ] );
        
        yellowKoopaSprite = new YellowKoopa( yellowKoopaAnim[ 0 ], yellowKoopaAnim[ 1 ],
                yellowKoopaAnim[ 2 ], yellowKoopaAnim[ 3 ] );
        
        bombSprite = new Bomb( bombAnim[ 0 ], bombAnim[ 1 ],
                bombAnim[ 2 ], bombAnim[ 3 ] );
        
        bulletSprite = new Bullet( bulletAnim[ 0 ], bulletAnim[ 1 ],
                bulletAnim[ 2 ], bulletAnim[ 3 ] );
        
        batSprite = new Bat( batAnim[ 0 ], batAnim[ 1 ],
                batAnim[ 2 ], batAnim[ 3 ] );
        
        turtleSprite = new Turtle( turtleAnim[ 0 ], turtleAnim[ 1 ],
                turtleAnim[ 2 ], turtleAnim[ 3 ] );
        
        mummyTurtleSprite = new MummyTurtle( mummyTurtleAnim[ 0 ], mummyTurtleAnim[ 1 ],
                mummyTurtleAnim[ 2 ], mummyTurtleAnim[ 3 ] );
        
        blueDragonSprite = new BlueDragon( blueDragonAnim[ 0 ], blueDragonAnim[ 1 ],
                blueDragonAnim[ 2 ], blueDragonAnim[ 3 ] );
        
    }
    
    
    /**
     * Cria a animação do jogador.
     */
    private Animation createPlayerAnim( Image... player ) {
        Animation anim = new Animation();
        anim.addFrame( player[ 0 ], 250 );
        return anim;
    }
    
    /**
     * Cria a animação do jogador andando.
     */
    private Animation createPlayerAnimAndando( Image... player ) {
        Animation anim = new Animation();
        anim.addFrame( player[ 0 ], 100 );
        anim.addFrame( player[ 1 ], 100 );
        return anim;
    }
    
    /**
     * Cria a animação do jogador pulando.
     */
    private Animation createPlayerAnimPulando( Image... player ) {
        Animation anim = new Animation();
        anim.addFrame( player[ 0 ], 250 );
        anim.addFrame( player[ 1 ], 5000 );
        return anim;
    }
    
    /**
     * Cria a animação do jogador morrendo.
     */
    private Animation createPlayerAnimMorrendo( Image... player ) {
        Animation anim = new Animation();
        anim.addFrame( player[ 0 ], 100 );
        anim.addFrame( player[ 1 ], 100 );
        return anim;
    }
    
    
    /**
     * Cria a animação do jogador abaixado.
     */
    private Animation createPlayerAnimAbaixado( Image... player ) {
        Animation anim = new Animation();
        anim.addFrame( player[ 0 ], 250 );
        return anim;
    }
    
    
    /**
     * Cria a animação do FlyGoomba.
     */
    private Animation createFlyGoombaAnim( Image... img ) {
        Animation anim = new Animation();
        anim.addFrame( img[ 0 ], 200 );
        anim.addFrame( img[ 1 ], 200 );
        return anim;
    }
    
    
    /**
     * Cria a animação do Goomba.
     */
    private Animation createGoombaAnim( Image... img ) {
        Animation anim = new Animation();
        anim.addFrame( img[ 0 ], 200 );
        anim.addFrame( img[ 1 ], 200 );
        return anim;
    }
    
    /**
     * Cria a animação do Green Koopa.
     */
    private Animation createGreenKoopaAnim( Image... img ) {
        Animation anim = new Animation();
        anim.addFrame( img[ 0 ], 200 );
        anim.addFrame( img[ 1 ], 200 );
        return anim;
    }
    
    /**
     * Cria a animação do Red Koopa.
     */
    private Animation createRedKoopaAnim( Image... img ) {
        Animation anim = new Animation();
        anim.addFrame( img[ 0 ], 200 );
        anim.addFrame( img[ 1 ], 200 );
        return anim;
    }
    
    /**
     * Cria a animação do Blue Koopa.
     */
    private Animation createBlueKoopaAnim( Image... img ) {
        Animation anim = new Animation();
        anim.addFrame( img[ 0 ], 200 );
        anim.addFrame( img[ 1 ], 200 );
        return anim;
    }
    
    /**
     * Cria a animação do Yellow Koopa.
     */
    private Animation createYellowKoopaAnim( Image... img ) {
        Animation anim = new Animation();
        anim.addFrame( img[ 0 ], 200 );
        anim.addFrame( img[ 1 ], 200 );
        return anim;
    }
    
    /**
     * Cria a animação do Bomb.
     */
    private Animation createBombAnim( Image... img ) {
        Animation anim = new Animation();
        anim.addFrame( img[ 0 ], 200 );
        anim.addFrame( img[ 1 ], 200 );
        return anim;
    }
    
    /**
     * Cria a animação do Bullet.
     */
    private Animation createBulletAnim( Image... img ) {
        Animation anim = new Animation();
        anim.addFrame( img[ 0 ], 200 );
        return anim;
    }
    
    /**
     * Cria a animação do Bat.
     */
    private Animation createBatAnim( Image... img ) {
        Animation anim = new Animation();
        anim.addFrame( img[ 0 ], 200 );
        anim.addFrame( img[ 1 ], 200 );
        return anim;
    }
    
    /**
     * Cria a animação do Turtle.
     */
    private Animation createTurtleAnim( Image... img ) {
        Animation anim = new Animation();
        anim.addFrame( img[ 0 ], 200 );
        anim.addFrame( img[ 1 ], 200 );
        return anim;
    }
    
    /**
     * Cria a animação do Mummy Turtle.
     */
    private Animation createMummyTurtleAnim( Image... img ) {
        Animation anim = new Animation();
        anim.addFrame( img[ 0 ], 200 );
        anim.addFrame( img[ 1 ], 200 );
        return anim;
    }
    
    /**
     * Cria a animação do Blue Dragon.
     */
    private Animation createBlueDragonAnim( Image... img ) {
        Animation anim = new Animation();
        anim.addFrame( img[ 0 ], 200 );
        anim.addFrame( img[ 1 ], 200 );
        return anim;
    }
    
    /**
     * Carrega as sprites de power up.
     */
    private void loadPowerUpSprites() {
        
        // cria a sprite de finalização "goal""
        Animation anim = new Animation();
        anim.addFrame( loadImage( "star1.png" ), 20 );
        anim.addFrame( loadImage( "star2.png" ), 40 );
        anim.addFrame( loadImage( "star3.png" ), 60 );
        anim.addFrame( loadImage( "star4.png" ), 80 );
        anim.addFrame( loadImage( "star5.png" ), 100 );
        anim.addFrame( loadImage( "star6.png" ), 120 );
        anim.addFrame( loadImage( "star7.png" ), 150 );
        anim.addFrame( loadImage( "star6.png" ), 120 );
        anim.addFrame( loadImage( "star5.png" ), 100 );
        anim.addFrame( loadImage( "star4.png" ), 80 );
        anim.addFrame( loadImage( "star3.png" ), 60 );
        anim.addFrame( loadImage( "star2.png" ), 40 );
        goalSprite = new PowerUp.Goal( anim );
        
        // cria a sprite de moeda
        anim = new Animation();
        anim.addFrame( loadImage( "coin1.png" ), 100 );
        anim.addFrame( loadImage( "coin2.png" ), 100 );
        anim.addFrame( loadImage( "coin3.png" ), 100 );
        anim.addFrame( loadImage( "coin4.png" ), 100 );
        anim.addFrame( loadImage( "coin5.png" ), 100 );
        coinSprite = new PowerUp.Coin( anim );
        
        // cria a sprite do cogumelo
        anim = new Animation();
        anim.addFrame( loadImage( "mushroom.png" ), 150 );
        mushroomSprite = new PowerUp.Mushroom( anim );
        
        // cria a sprite de vida
        anim = new Animation();
        anim.addFrame( loadImage( "oneUp.png" ), 150 );
        oneUpSprite = new PowerUp.OneUp( anim );
        
        // cria a sprite fire flower
        anim = new Animation();
        anim.addFrame( loadImage( "fireFlower.png" ), 150 );
        fireFlowerSprite = new PowerUp.FireFlower( anim );
    }
    
}