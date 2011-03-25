package jogo.sprites;

import infraestrutura.grafico.Animation;
import java.lang.reflect.Constructor;

/**
 * O jogador.
 *
 * @author David Buzatto
 */
public class Player extends Creature {
    
    private static final float JUMP_SPEED = -.77f;
    
    private boolean onGround;
    
    // variáveis de controle
    
    // indica se está virado para a direita.
    private boolean turnedToRight;
    
    // indica se está abaixado
    private boolean down;
    
    // indica se está pulando
    private boolean pulando;
    
    
    private Animation runLeft;
    private Animation runRight;
    private Animation jumpLeft;
    private Animation jumpRight;
    private Animation downLeft;
    private Animation downRight;
    
    public Player( Animation left, Animation right,
            Animation deadLeft, Animation deadRight,
            Animation runLeft, Animation runRight,
            Animation jumpLeft, Animation jumpRight,
            Animation downLeft, Animation downRight ) {
        
        super( left, right, deadLeft, deadRight );
        
        this.setRunLeft( runLeft );
        this.setRunRight( runRight );
        this.setJumpLeft( jumpLeft );
        this.setJumpRight( jumpRight );
        this.setDownLeft( downLeft );
        this.setDownRight( downRight );
        
        turnedToRight = true;
        
        setMaxSpeed( 0.1f );
        setDown( false );
    }
    
    
    public Object clone() {
        // usa reflexão para criar a subclasse correta.
        Constructor constructor = getClass().getConstructors()[ 0 ];
        try {
            return constructor.newInstance( new Object[] {
                ( Animation ) getLeft().clone(),
                ( Animation ) getRight().clone(),
                ( Animation ) getDeadLeft().clone(),
                ( Animation ) getDeadRight().clone(),
                ( Animation ) getRunLeft().clone(),
                ( Animation ) getRunRight().clone(),
                ( Animation ) getJumpLeft().clone(),
                ( Animation ) getJumpRight().clone(),
                ( Animation ) getDownLeft().clone(),
                ( Animation ) getDownRight().clone()
            });
        } catch ( Exception ex ) {
            // provavelmente nunca deve ocorrer
            ex.printStackTrace();
            return null;
        }
    }
    
    
    public void collideHorizontal() {
        setVelocityX( 0 );
    }
    
    
    public void collideVertical() {
        // verifica se colidiu com o chão
        if ( getVelocityY() > 0 ) {
            onGround = true;
            setPulando( false );
        }
        setVelocityY( 0 );
    }
    
    
    public void setY(float y) {
        // verifica se está caindo
        if ( Math.round( y ) > Math.round( getY() ) ) {
            onGround = false;
        }
        super.setY( y );
    }
    
    
    public void wakeUp() {
        // não faz nada
    }
    
    
    /**
     * Faz o jogador pular se o mesmo estiver no chão ou então se forceJump 
     * é true.
     */
    public void jump( boolean forceJump ) {
        if ( onGround || forceJump ) {
            onGround = false;
            setVelocityY( JUMP_SPEED );
        }
        if ( !onGround )
            setPulando( true );
        else
            setPulando( false );
    }
    
    /**
     * Atualiza a animação desse jogador.
     */
    public void update( long elapsedTime ) {
        
        // seleciona a animação correta
        Animation newAnim = anim;
        
        if ( getVelocityX() < 0 ) {
            
            if ( !isPulando() )
                newAnim = getRunLeft();
            turnedToRight = false;
            
        } else if ( getVelocityX() > 0 ) {
            
            if ( !isPulando() )
                newAnim = getRunRight();
            turnedToRight = true;
            
        } else {
            
            if ( turnedToRight ) {
                
                if ( isDown() )
                    newAnim = getDownRight();
                else if ( isPulando() )
                    newAnim = getJumpRight();
                else
                    newAnim = getRight();
                
            } else {
                
                if ( isDown() )
                    newAnim = getDownLeft();
                else if ( isPulando() )
                    newAnim = getJumpLeft();
                else
                    newAnim = getLeft();
                
            }
            
        }
        
        if ( getVelocityY() < 0 ) {
            
            if ( turnedToRight )
                newAnim = jumpRight;
            else
                newAnim = jumpLeft;
            
            setPulando( true );
            
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
        setStateTime( getStateTime() + elapsedTime );
        if ( getState() == STATE_DYING && getStateTime() >= 3500 ) {
            setState( STATE_DEAD );
        }
        
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }
    
    public boolean isPulando() {
        return pulando;
    }

    public void setPulando(boolean pulando) {
        this.pulando = pulando;
    }
    
    public Animation getRunLeft() {
        return runLeft;
    }

    public void setRunLeft(Animation runLeft) {
        this.runLeft = runLeft;
    }

    public Animation getRunRight() {
        return runRight;
    }

    public void setRunRight(Animation runRight) {
        this.runRight = runRight;
    }

    public Animation getJumpLeft() {
        return jumpLeft;
    }

    public void setJumpLeft(Animation jumpLeft) {
        this.jumpLeft = jumpLeft;
    }

    public Animation getJumpRight() {
        return jumpRight;
    }

    public void setJumpRight(Animation jumpRight) {
        this.jumpRight = jumpRight;
    }

    public Animation getDownLeft() {
        return downLeft;
    }

    public void setDownLeft(Animation downLeft) {
        this.downLeft = downLeft;
    }

    public Animation getDownRight() {
        return downRight;
    }

    public void setDownRight(Animation downRight) {
        this.downRight = downRight;
    }
    
}