package infraestrutura.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * O LoopingByteInputStream é um ByteArrayInputStream que
 * entra em loop infinito. O loop para quando o método close() é chamado.
 * <p>Idéias possíveis para estender a classe:<ul>
 * <li>Adicionar uma opção para executar um número definido de vezes.
 * </ul>
 *
 * @author David Buzatto
 */
public class LoopingByteInputStream extends ByteArrayInputStream {
    
    private boolean closed;
    
    /**
     * Cria um novo LoopingByteInputStream com o array de bytes especificado.
     * O array não é copiado.
     */
    public LoopingByteInputStream( byte[] buffer ) {
        super( buffer );
        closed = false;
    }
    
    
    /**
     * Lê <code>length</code> bytes do array. Se o fim do array é 
     * atingido, a leitura inicia novamente do começo do array.
     * Returna -1 se o array foi fechado.
     */
    public int read( byte[] buffer, int offset, int length ) {
        
        if ( closed ) {
            return -1;
        }
        
        int totalBytesRead = 0;
        
        while ( totalBytesRead < length ) {
            
            int numBytesRead = super.read( buffer,
                    offset + totalBytesRead,
                    length - totalBytesRead );
            
            if ( numBytesRead > 0 ) {
                totalBytesRead += numBytesRead;
            } else {
                reset();
            }
        }
        
        return totalBytesRead;
    }
    
    
    /**
     * Fecha o stream. Chamadas futuras a read() retornarão 1.
     */
    public void close() throws IOException {
        super.close();
        closed = true;
    }
    
}