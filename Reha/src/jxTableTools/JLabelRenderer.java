package jxTableTools;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class JLabelRenderer extends JLabel implements TableCellRenderer{

	   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JLabelRenderer(){
	      setOpaque( true );
	   }
	   
	   public Component getTableCellRendererComponent(JTable table, Object value,
	         boolean isSelected, boolean hasFocus, int row, int column) {
	      
	      // die normalen Farben
	      setBackground( (Color) table.getValueAt(row, column-2) );
	      setForeground( (Color) table.getValueAt(row, column-1) );
	      if(value instanceof JLabel){
	    	  setHorizontalAlignment(JLabel.CENTER);
	    	  if( ((JLabel)value).toString().equals("")){
	    		  setText("Demo-Text"); 	    		  
	    	  }else{
    			  setText( ((JLabel)value).getText() );
	    	  }
	      }
	      
	      return this;
	   }

	
	}
