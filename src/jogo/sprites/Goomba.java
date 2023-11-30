package jogo.sprites;

import infraestrutura.grafico.Animation;

/**
 * Um Goomba é uma Creature que se move devagar no chão.
 */
public class Goomba extends Creature {
    
    public Goomba( Animation left, Animation right,
            Animation deadLeft, Animation deadRight ) {
        super( left, right, deadLeft, deadRight );
        setMaxSpeed( 0.1f );
    }
    
}