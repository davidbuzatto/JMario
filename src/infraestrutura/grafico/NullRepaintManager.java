package infraestrutura.grafico;

import javax.swing.*;

/**
 * O NullRepaintManager é um RepaintManager que não faz nenhum repaint.
 * É útil quando toda a renderização é feita manualmente pela aplicação.
 *
 * @author David Buzatto
 */
public class NullRepaintManager extends RepaintManager {
    
    /**
     * Instala o NullRepaintManager.
     */
    public static void install() {
        RepaintManager repaintManager = new NullRepaintManager();
        repaintManager.setDoubleBufferingEnabled( false );
        RepaintManager.setCurrentManager( repaintManager );
    }
    
    
    public void addInvalidComponent( JComponent c ) {
        // não faz nada
    }
    
    
    public void addDirtyRegion( JComponent c, int x, int y,
            int w, int h ) {
        // não faz nada
    }
    
    
    public void markCompletelyDirty( JComponent c ) {
        // não faz nada
    }
    
    
    public void paintDirtyRegions() {
        // não faz nada
    }
    
}