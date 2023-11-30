package jogo.sprites;

import infraestrutura.grafico.Animation;
import infraestrutura.grafico.Sprite;
import java.lang.reflect.Constructor;

/**
 * Uma Creature é uma Sprite que é afetada pela gravidade e pode morrer.
 * Ela tem quatro animações: moveno para a esquerda, direita, morrendo para a 
 * esquerda e direita.
 *
 * @author David Buzatto
 */
public abstract class Creature extends Sprite {
    
    /**
     * Quantidade de tempo para ir de STATE_DYING para STATE_DEAD.
     */
    public static final int DIE_TIME = 500;
    
    public static final int STATE_NORMAL = 0;
    public static final int STATE_DYING = 1;
    public static final int STATE_DEAD = 2;
    
    private Animation left;
    private Animation right;
    private Animation deadLeft;
    private Animation deadRight;
    private int state;
    private long stateTime;
    private float maxSpeed;
    
    /**
     * Cria uma nova criatura com as animações especificadas.
     */
    public Creature( Animation left, Animation right,
            Animation deadLeft, Animation deadRight ) {
        super( right );
        this.setLeft( left );
        this.setRight( right );
        this.setDeadLeft( deadLeft );
        this.setDeadRight( deadRight );
        this.setMaxSpeed( 0 );
        setState( STATE_NORMAL );
    }
    
    public Object clone() {
        // usa reflexão para criar a subclasse correta.
        Constructor constructor = getClass().getConstructors()[ 0 ];
        try {
            return constructor.newInstance( new Object[] {
                ( Animation ) getLeft().clone(),
                ( Animation ) getRight().clone(),
                ( Animation ) getDeadLeft().clone(),
                ( Animation ) getDeadRight().clone()
            });
        } catch ( Exception ex ) {
            // provavelmente nunca deve ocorrer
            ex.printStackTrace();
            return null;
        }
    }
    
    
    /**
     * Configura a velocidade máxima dessa criatura.
     */
    public void setMaxSpeed( float maxSpeed ) {
        this.maxSpeed = maxSpeed;
    }
    
    
    /**
     * Obtém a velocidade máxima dessa criatura.
     */
    public float getMaxSpeed() {
        return maxSpeed;
    }
    
    
    /**
     * Acorda a criatura quanto esta aparece pela primeira vez na tela.
     * Normalmente, a criatura inicia movimentando-se para a esquerda.
     */
    public void wakeUp() {
        if ( getState() == STATE_NORMAL && getVelocityX() == 0 ) {
            setVelocityX( -getMaxSpeed() );
        }
    }
    
    
    /**
     * Obtém o estado dessa criatura, podendo ser 
     * STATE_NORMAL, STATE_DYING, ou STATE_DEAD.
     */
    public int getState() {
        return state;
    }
    
    
    /**
     * Configura o estado dessa criatura para STATE_NORMAL,
     * STATE_DYING, ou STATE_DEAD.
     */
    public void setState( int state ) {
        if ( this.state != state ) {
            this.state = state;
            setStateTime(0);
            if ( state == STATE_DYING ) {
                setVelocityX( 0 );
                setVelocityY( 0 );
            }
        }
    }
    
    
    /**
     * Verifica se essa criatura está viva.
     */
    public boolean isAlive() {
        return ( getState() == STATE_NORMAL );
    }
    
    
    /**
     * Verifica se esta criatura está voando.
     */
    public boolean isFlying() {
        return false;
    }
    
    
    /**
     * Chamado antes de update() se a criatura colidiu com um tile 
     * horizontalmente.
     */
    public void collideHorizontal() {
        setVelocityX( -getVelocityX() );
    }
    
    
    /**
     * Chamado antes de update() se a criatura colidiu com um tile 
     * verticalmente.
     */
    public void collideVertical() {
        setVelocityY(0);
    }
    
    
    /**
     * Atualiza a animação dessa criatura.
     */
    public void update( long elapsedTime ) {
        // seleciona a animação correta
        Animation newAnim = anim;
        if ( getVelocityX() < 0 ) {
            newAnim = getLeft();
        } else if ( getVelocityX() > 0 ) {
            newAnim = getRight();
        }
        if ( getState() == STATE_DYING && newAnim == getLeft() ) {
            newAnim = getDeadLeft();
        } else if ( getState() == STATE_DYING && newAnim == getRight() ) {
            newAnim = getDeadRight();
        }
        
        // atualiza a animação
        if ( anim != newAnim ) {
            anim = newAnim;
            anim.start();
        } else {
            anim.update( elapsedTime );
        }
        
        // atualiza o estado de "morte"
        setStateTime(getStateTime() + elapsedTime);
        if ( state == STATE_DYING && getStateTime() >= DIE_TIME ) {
            setState( STATE_DEAD );
        }
        
    }

    public Animation getLeft() {
        return left;
    }

    public void setLeft(Animation left) {
        this.left = left;
    }

    public Animation getRight() {
        return right;
    }

    public void setRight(Animation right) {
        this.right = right;
    }

    public Animation getDeadLeft() {
        return deadLeft;
    }

    public void setDeadLeft(Animation deadLeft) {
        this.deadLeft = deadLeft;
    }

    public Animation getDeadRight() {
        return deadRight;
    }

    public void setDeadRight(Animation deadRight) {
        this.deadRight = deadRight;
    }

    public long getStateTime() {
        return stateTime;
    }

    public void setStateTime(long stateTime) {
        this.stateTime = stateTime;
    }
    
}