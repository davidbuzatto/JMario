package infraestrutura.som;

/**
 * A classe FilterSequence é um SoundFilter que combina vários SoundFilters 
 * em um só.
 * @see FilteredSoundStream
 *
 * @author David Buzatto
 */
public class FilterSequence extends SoundFilter {
    
    private SoundFilter[] filters;
    
    /**
     * Cria um novo FilterSequence usando um array de SoundFilters.
     * As amostras executam sobre cada SoundFilet na ordem do array.
     */
    public FilterSequence( SoundFilter[] filters ) {
        this.filters = filters;
    }
    
    
    /**
     * Retorna a quantidade restante do todos os SoundFilters nesse 
     * FilterSequence.
     */
    public int getRemainingSize() {
        int max = 0;
        for ( int i = 0; i < filters.length; i++ ) {
            max = Math.max( max, filters[ i ].getRemainingSize() );
        }
        return max;
    }
    
    
    /**
     * Reseta cada SoundFilter nesse FilterSequence.
     */
    public void reset() {
        for ( int i = 0; i < filters.length; i++ ) {
            filters[ i ].reset();
        }
    }
    
    
    /**
     * Filtra o som de cada SoundFilter desse FilterSequence.
     */
    public void filter( byte[] samples, int offset, int length ) {
        for (int i = 0; i < filters.length; i++ ) {
            filters[ i ].filter( samples, offset, length );
        }
    }
    
}