package infraestrutura.gui;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * Painel de configuração do jogo.
 *
 * @author David Buzatto
 */
public class ConfigPanel extends JPanel {
    
    public ConfigPanel() {
        super();
        setSize( 100, 100 );
        add( new JButton( "teste" ) );
    }
    
    public void painComponent( Graphics g ) {
        
        super.paintComponent( g );
        
        Graphics2D g2d = ( Graphics2D ) g;
        
        g2d.setColor( Color.WHITE );
        
        g2d.fillRect( 0, 0, getWidth(), getHeight() );
        
    }
    
}
