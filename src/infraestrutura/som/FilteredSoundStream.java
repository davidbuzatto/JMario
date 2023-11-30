package infraestrutura.som;

import java.io.*;

/**
 * A classe FilteredSoundStream é um FilterInputStream que aplica 
 * um SoundFilter ao input stream da camada abaixo.
 * @see SoundFilter
 *
 * @author David Buzatto
 */
public class FilteredSoundStream extends FilterInputStream {
    
    private static final int REMAINING_SIZE_UNKNOWN = -1;
    
    private SoundFilter soundFilter;
    private int remainingSize;
    
    
    /**
     * Cria um novo FilteredSoundStream com o InputStream e
     * SoundFilter especificados.
     */
    public FilteredSoundStream( InputStream in,
            SoundFilter soundFilter ) {
        super( in );
        this.soundFilter = soundFilter;
        remainingSize = REMAINING_SIZE_UNKNOWN;
    }
    
    
    /**
     * Sobrescreve o método read de FilterInputStream para aplicar o filtro 
     * nos bytes lidos.
     */
    public int read( byte[] samples, int offset, int length )
            throws IOException {
        
        // lê e filtra a amostra de som no stream
        int bytesRead = super.read( samples, offset, length );
        if ( bytesRead > 0 ) {
            soundFilter.filter( samples, offset, bytesRead );
            return bytesRead;
        }
        
        /* se não há bytes sobrando no stream de som, verifica se o filtro 
         * contém bytes restantes (écos).
         */
        if ( remainingSize == REMAINING_SIZE_UNKNOWN ) {
            remainingSize = soundFilter.getRemainingSize();
            // arredonda para baixo para o próximo múltiplo mais próximo de  4
            // (normalmente o tamanho do frame)
            remainingSize = remainingSize / 4 * 4;
        }
        
        if ( remainingSize > 0 ) {
            length = Math.min( length, remainingSize );
            
            // limpar o buffer
            for ( int i = offset; i < offset + length; i++) {
                samples[ i ] = 0;
            }
            
            // filtra
            soundFilter.filter( samples, offset, length );
            remainingSize -= length;
            
            // retorna
            return length;
            
        } else {
            // fim do stream
            return -1;
        }
    }
    
}