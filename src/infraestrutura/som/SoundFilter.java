package infraestrutura.som;

/**
 * Classe abstrata desenvolvida para filtrar amostras de som.
 * Como os SoundFilters podem usar buferização interna de amostras,
 * um novo SoundFilter deve ser criado para cada som executado.
 * Entretanto, SoundFilters podem ser reusados após serem finalizados chamando o 
 * método reset().
 * Assume-se que todas as amostrars são de 16-bit, sinalizadas, e no formato 
 * "little-endian".
 * @see FilteredSoundStream
 *
 * @author David Buzatto
 */
public abstract class SoundFilter {
    
    /**
     * Reseta esse SoundFilter. Não faz nada por padrão.
     */
    public void reset() {
        // não faz nada
    }
    
    
    /**
     * Obtém o tamanho restante, em bytes, que esre filtro executa após o som 
     * ser finalizado.that this filter. Um exemplo pode ser um éco que executa 
     * mais que o seu som original.
     * Esse método retorna 0 por padrão.
     */
    public int getRemainingSize() {
        return 0;
    }
    
    
    /**
     * Filtra um array de amostrar. As amostras devem ser de 16-bit, 
     * sinalizadas e no formato "little-endian".
     */
    public void filter( byte[] samples ) {
        filter( samples, 0, samples.length );
    }
    
    
    /**
     * Filtra um array de amostras. As amostras devem ser de 16-bit, 
     * sinalizadas e no formato "little-endian". 
     * Esse método deve ser implementado pelas subclasses. Atenção, o offset e 
     * o tamanho referen-se ao número de bytes e não amostras.
     */
    public abstract void filter(
            byte[] samples, int offset, int length );
    
    
    /**
     * Método de conveniência para obter uma amostra de 16-bit de um 
     * array de bytes. As amostras devem ser de 16-bit, 
     * sinalizadas e no formato "little-endian".
     */
    public static short getSample( byte[] buffer, int position ) {
        return ( short ) (
                ( ( buffer[ position + 1 ] & 0xff ) << 8 ) |
                ( buffer[ position ] & 0xff ) );
    }
    
    
    /**
     * Método de conveniência para configurar uma amostra de 16-bit em um array 
     * de bytes. As amostras devem ser de 16-bit, 
     * sinalizadas e no formato "little-endian".
     */
    public static void setSample( byte[] buffer, int position,
            short sample ) {
        buffer[ position ] = ( byte ) ( sample & 0xff );
        buffer[ position + 1 ] = ( byte ) ( ( sample >> 8 ) & 0xff );
    }
    
}