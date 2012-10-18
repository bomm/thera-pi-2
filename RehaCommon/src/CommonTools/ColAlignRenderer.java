package CommonTools;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class ColAlignRenderer extends JLabel implements TableCellRenderer{
/**
	 * 
	 */
	private static final long serialVersionUID = -5690508896668147474L;
Color hinterGrund = null;
int align = 0;
	   public ColAlignRenderer(Color col,int ali){
		   hinterGrund = col;
		   align =  ali;
	      //setOpaque( true );
	   }
	   
	   public Component getTableCellRendererComponent(JTable table, Object value,
	         boolean isSelected, boolean hasFocus, int row, int column) {
	      
	      // die normalen Farben
		  setForeground(hinterGrund);
		  setHorizontalAlignment(align);
		  /*
	      setBackground( (Color) table.getValueAt(row, column-2) );
	      setForeground( (Color) table.getValueAt(row, column-1) );
	      
	      
	      if(value instanceof JLabel){
	    	  if( ((JLabel)value).toString().equals("")){
	    		  //setText("Demo-Text"); 	    		  
	    	  }else{
    			  setText( ((JLabel)value).getText() );
	    	  }
	      }
		  */
	      return this;
	   }

	
	}
