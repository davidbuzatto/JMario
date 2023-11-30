package jogo.sprites;

import infraestrutura.grafico.Animation;

/**
 * Uma FlyGoomba é uma Creature que voa vagarozamente no ar.
 */
public class FlyGoomba extends Creature {
    
    public FlyGoomba( Animation left, Animation right,
            Animation deadLeft, Animation deadRight ) {
        super( left, right, deadLeft, deadRight );
        setMaxSpeed( 0.2f );
    }
    
    
    public boolean isFlying() {
        return isAlive();
    }
    
}