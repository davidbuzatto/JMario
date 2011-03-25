package jogo.sprites;

import infraestrutura.grafico.Animation;

/**
 * Um RedKoopa é uma Creature que se move devagar no chão.
 */
public class RedKoopa extends Creature {
    
    public RedKoopa( Animation left, Animation right,
            Animation deadLeft, Animation deadRight ) {
        super( left, right, deadLeft, deadRight );
        setMaxSpeed( 0.1f );
    }
    
}