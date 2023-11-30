package infraestrutura.grafico;

import java.awt.*;
import java.util.*;

/**
 * A classe Animation gerencia uma série de imagens (quadros) e a quantidade
 * de tempo para exibir cada imagem.
 *
 * @author David Buzatto
 */
public class Animation {
    
    private ArrayList< AnimFrame > frames;
    private int currFrameIndex;
    private long animTime;
    private long totalDuration;
    
    
    /**
     * Cria uma nova Animation vazia.
     */
    public Animation() {
        this( new ArrayList< AnimFrame >(), 0 );
    }
    
    
    /**
     * Cria uma animação com os frames e a duração total da exibição 
     * da animação.
     */
    private Animation( ArrayList< AnimFrame > frames, long totalDuration ) {
        this.frames = frames;
        this.totalDuration = totalDuration;
        start();
    }
    
    
    /**
     * Cria uma duplicata da animação. A lista de frames é compartilhada 
     * entre duas animações, mas cada animação pode ser animada 
     * independentemente.
     */
    public Object clone() {
        return new Animation( frames, totalDuration );
    }
    
    
    /**
     * Adiciona uma imagem à animação com uma duração especificada.
     * (tempo para exibir a imagem).
     */
    public synchronized void addFrame( Image image, long duration ) {
        
        totalDuration += duration;
        frames.add( new AnimFrame( image, totalDuration ) );
        
    }
    
    
    /**
     * Inicia a animação desde o início.
     */
    public synchronized void start() {
        animTime = 0;
        currFrameIndex = 0;
    }
    
    
    /**
     * Atualiza o quadro atual desta animação, se necessário.
     */
    public synchronized void update( long elapsedTime ) {
        
        if ( frames.size() > 1 ) {
            
            animTime += elapsedTime;
            
            if ( animTime >= totalDuration ) {
                
                animTime = animTime % totalDuration;
                currFrameIndex = 0;
                
            }
            
            while ( animTime > getFrame( currFrameIndex ).endTime ) {
                
                currFrameIndex++;
                
            }
            
        }
        
    }
    
    
    /**
     * Obtém a imagem atual da animação. Retorna null se a animação
     * não tiver nenhuma imagem.
     */
    public synchronized Image getImage() {
        
        if ( frames.size() == 0 ) {
            
            return null;
            
        } else {
            
            return getFrame( currFrameIndex ).image;
            
        }
        
    }
    
    /**
     * Obtém um frame da animação.
     */
    private AnimFrame getFrame( int i ) {
        
        return ( AnimFrame ) frames.get( i );
        
    }
    
    
    /**
     * Class interna privada que modela um frame (quadro) da animação.
     */
    private class AnimFrame {
        
        Image image;
        long endTime;
        
        /**
         * Cria uma novo AnimFrame com uma imagem e o tempo de duração.
         */
        public AnimFrame( Image image, long endTime ) {
            
            this.image = image;
            this.endTime = endTime;
            
        }
        
    }
    
}