package jogo.sprites;

import infraestrutura.grafico.Animation;

/**
 * Um Bomb é uma Creature que se move devagar no chão.
 */
public class Bomb extends Creature {
    
    public Bomb( Animation left, Animation right,
            Animation deadLeft, Animation deadRight ) {
        super( left, right, deadLeft, deadRight );
        setMaxSpeed( 0.1f );
    }
    
}