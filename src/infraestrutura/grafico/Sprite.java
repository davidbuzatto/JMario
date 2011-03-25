package infraestrutura.grafico;

import java.awt.*;

/**
 * A classe Sprite define uma entidade do jogo, algo que recebe animações.
 *
 * @author David Buzatto
 */
public class Sprite {
    
    protected Animation anim;
    
    // posicionamento
    private float x;
    private float y;
    
    // velocidade (pixels por milisegundo)
    private float velocityX;
    private float velocityY;
    
    
    /*
     * Cria um novo objeto Sprite com a animação especificada.
     */
    public Sprite( Animation anim ) {
        this.anim = anim;
    }
    
    
    /**
     * Atualiza a Animação da Sprite e sua posição baseada na velocidade.
     */
    public void update( long elapsedTime ) {
        x += velocityX * elapsedTime;
        y += velocityY * elapsedTime;
        anim.update( elapsedTime );
    }
    
    
    /**
     * Obtém a posição x atual da sprite.
     */
    public float getX() {
        return x;
    }
    
    
    /**
     * Configura a posição x atual da sprite.
     */
    public void setX(float x) {
        this.x = x;
    }
    
    
    /**
     * Obtém a posição y atual da sprite.
     */
    public float getY() {
        return y;
    }
    
    
    /**
     * Configura a posição x atual da sprite.
     */
    public void setY(float y) {
        this.y = y;
    }
    
    
    /**
     * Obtém a velocidade horizontal da sprite em pixels por milisegundo.
     */
    public float getVelocityX() {
        return velocityX;
    }
    
    
    /**
     * Configura a velocidade horizontal da sprite em pixels por milisegundo.
     */
    public void setVelocityX(float velocityX) {
        this.velocityX = velocityX;
    }
    
    
    /**
     * Obtém a velocidade vertical da sprite em pixels por milisegundo.
     */
    public float getVelocityY() {
        return velocityY;
    }
    
    
    /**
     * Configura a velocidade vertical da sprite em pixels por milisegundo.
     */
    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;
    }
    
    
    /**
     * Obtém a largura da Sprite, baseado no tamanho da imagem atual.
     */
    public int getWidth() {
        return anim.getImage().getWidth( null );
    }
    
    
    /**
     * Obtém a altura da Sprite, baseado no tamanho da imagem atual.
     */
    public int getHeight() {
        return anim.getImage().getHeight( null );
    }
    
    
    /**
     * Obtém a imagem atual da Sprite.
     */
    public Image getImage() {
        return anim.getImage();
    }
    
    
    /**
     * Clona a Sprite. Não clona a posição ou valocidade.
     */
    public Object clone() {
        return new Sprite( anim );
    }
    
}