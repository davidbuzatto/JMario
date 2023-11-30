package jogo.sprites;

import infraestrutura.grafico.Animation;

/**
 * Uma Bat é uma Creature que voa vagarozamente no ar.
 */
public class Bat extends Creature {
    
    public Bat( Animation left, Animation right,
            Animation deadLeft, Animation deadRight ) {
        super( left, right, deadLeft, deadRight );
        setMaxSpeed( 0.2f );
    }
    
    
    public boolean isFlying() {
        return isAlive();
    }
    
}