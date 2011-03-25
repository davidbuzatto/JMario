package jogo.sprites;

import infraestrutura.grafico.Animation;

/**
 * Uma Bullet é uma Creature que voa vagarozamente no ar.
 */
public class Bullet extends Creature {
    
    public Bullet( Animation left, Animation right,
            Animation deadLeft, Animation deadRight ) {
        super( left, right, deadLeft, deadRight );
        setMaxSpeed( 0.25f );
    }
    
    
    public boolean isFlying() {
        return isAlive();
    }
    
}