package infraestrutura.som;

import infraestrutura.grafico.*;

/**
 * A classe Filter3d é um SoundFilter que cria um efeito de som 3D.
 * O som é filtrado tomando como base a fonte do ouvinte.
 * <p>Idéias possíveis para estender essa classe:
 * <ul><li>Varie o som da caixa de som da esquerda para a da direita
 * </ul>
 * @see FilteredSoundStream
 *
 * @author David Buzatto
 */
public class Filter3d extends SoundFilter {
    
    // número de amostrar que alterar enquanto o volume é mudado.
    private static final int NUM_SHIFTING_SAMPLES = 500;
    
    private Sprite source;
    private Sprite listener;
    private int maxDistance;
    private float lastVolume;
    
    
    /**
     * Cria um novo Filter3d com a fonte especificada como um sprite e o ouvinte 
     * outro sprite. A posição dos Sprites podem ser mudadoas enquanto o filtro 
     * está sendo executado.
     * <p>O parâmetro de distância máxima é a distância máxima que o som pode 
     * ser ouvido.
     */
    public Filter3d( Sprite source, Sprite listener,
            int maxDistance ) {
        this.source = source;
        this.listener = listener;
        this.maxDistance = maxDistance;
        this.lastVolume = 0.0f;
    }
    
    
    /**
     * Filtra o som, fazendo que fique mais baixo quando a distância aumenta.
     */
    public void filter( byte[] samples, int offset, int length ) {
        
        if ( source == null || listener == null ) {
            // nada para filtrar, retorna
            return;
        }
        
        // calcula o distância entre o ouvinte e a fonte do som
        float dx = ( source.getX() - listener.getX() );
        float dy = ( source.getY() - listener.getY() );
        float distance = ( float ) Math.sqrt( dx * dx + dy * dy );
        
        // seta o voume de 0 (sem som) para 1
        float newVolume = ( maxDistance - distance ) / maxDistance;
        if ( newVolume <= 0 ) {
            newVolume = 0;
        }
        
        // seta o volume da amostra
        int shift = 0;
        for ( int i = offset; i < offset + length; i += 2 ) {
            
            float volume = newVolume;
            
            // altera o volume anterior para o próximo
            if ( shift < NUM_SHIFTING_SAMPLES ) {
                volume = lastVolume + ( newVolume - lastVolume ) *
                        shift / NUM_SHIFTING_SAMPLES;
                shift++;
            }
            
            // altera o volume da amostra
            short oldSample = getSample( samples, i );
            short newSample = ( short ) ( oldSample * volume );
            setSample( samples, i, newSample );
        }
        
        lastVolume = newVolume;
    }
    
}