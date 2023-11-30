package jogo.tile;

import infraestrutura.grafico.*;
import java.awt.Image;
import java.io.*;
import java.util.*;

/**
 * A classe TileMap contém a informação para um pequeno mapa de figuras
 * lado a lado, incluindo Sprites.
 * Cada pedaço é uma referência a uma imagem, sendo essas imagens usadas
 * múltiplas vezes no mesmo mapa.
 *
 * @author David Buzatto
 */
public class TileMap {
    
    private Image[][] tiles;
    private LinkedList< Sprite > sprites;
    private Sprite player;
    
    /**
     * Cria um novo TileMap com a largura e altura especificada
     * (em número de pedaços) do mapa.
     */
    public TileMap( int width, int height ) {
        tiles = new Image[ width ][ height ];
        sprites = new LinkedList< Sprite >();
    }
    
    
    /**
     * Obtém a largura do TileMap (número de pedaços).
     */
    public int getWidth() {
        return tiles.length;
    }
    
    /**
     * Obtém a altura do TileMap (número de pedaços).
     */
    public int getHeight() {
        return tiles[ 0 ].length;
    }
    
    
    /**
     * Obtém o pedaço de uma localização espefífica. Retorna null is
     * não haver nenhum pedaço na localização espeficada ou então se a
     * localizaçãono for fora dos limites do mapa.
     */
    public Image getTile( int x, int y ) {
        if ( x < 0 || x >= getWidth() ||
                y < 0 || y >= getHeight() ) {
            return null;
        } else {
            return tiles[ x ][ y ];
        }
    }
    
    
    /**
     * Configura o pedaço no local especificado.
     */
    public void setTile( int x, int y, Image tile ) {
        tiles[ x ][ y ] = tile;
    }
    
    
    /**
     * Obtém a Sprite do jogador.
     */
    public Sprite getPlayer() {
        return player;
    }
    
    
    /**
     * Configura a Sprite do jogador.
     */
    public void setPlayer( Sprite player ) {
        this.player = player;
    }
    
    
    /**
     * Adiciona a Sprite no mapa.
     */
    public void addSprite( Sprite sprite ) {
        sprites.add( sprite );
    }
    
    
    /**
     * Remove a Sprite do mapa.
     */
    public void removeSprite( Sprite sprite ) {
        sprites.remove( sprite );
    }
    
    
    /**
     * Obtém o Iterator de todas as Sprites desse mapa, menos a do jogador.
     */
    public Iterator getSprites() {
        return sprites.iterator();
    }
    
}