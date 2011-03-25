package jogo.sprites;

import infraestrutura.grafico.Animation;

/**
 * Um BlueKoopa é uma Creature que se move devagar no chão.
 */
public class BlueKoopa extends Creature {
    
    public BlueKoopa( Animation left, Animation right,
            Animation deadLeft, Animation deadRight ) {
        super( left, right, deadLeft, deadRight );
        setMaxSpeed( 0.1f );
    }
    
}