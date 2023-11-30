package jogo.sprites;

import infraestrutura.grafico.Animation;
import infraestrutura.grafico.Sprite;
import java.lang.reflect.Constructor;

/**
 * PowerUp é uma Sprite que o jogador pode pegar.
 */
public abstract class PowerUp extends Sprite {
    
    public PowerUp( Animation anim ) {
        super( anim );
    }
    
    
    public Object clone() {
        // usa reflexão para criar a subclasse correta
        Constructor constructor = getClass().getConstructors()[ 0 ];
        try {
            return constructor.newInstance(
                    new Object[] { ( Animation)anim.clone() } );
        } catch ( Exception ex ) {
            // provavelmente nunca irá acontecer
            ex.printStackTrace();
            return null;
        }
    }
    
    
    /**
     * O PowerUp Coin. Dá pontos ao jogador.
     */
    public static class Coin extends PowerUp {
        public Coin( Animation anim ) {
            super( anim );
        }
    }
    
    
    /**
     * O PowerUp Mushroom faz o jogador crescer.
     */
    public static class Mushroom extends PowerUp {
        public Mushroom( Animation anim ) {
            super( anim );
        }
    }
    
    /**
     * O PowerUp OneUpe dá uma vida ao jogador.
     */
    public static class OneUp extends PowerUp {
        public OneUp( Animation anim ) {
            super( anim );
        }
    }
    
    /**
     * O PowerUp FireFlower dá ao jogador poder de jogar fogo.
     */
    public static class FireFlower extends PowerUp {
        public FireFlower( Animation anim ) {
            super( anim );
        }
    }
    
    
    /**
     * O PowerUp Goal. Avança para o próximo mapa.
     */
    public static class Goal extends PowerUp {
        public Goal( Animation anim ) {
            super( anim );
        }
    }
    
}