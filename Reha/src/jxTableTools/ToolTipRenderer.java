package jxTableTools;


import java.awt.Color;
import java.awt.Component;
import java.util.Date;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class ToolTipRenderer extends JLabel implements TableCellRenderer{
Color hinterGrund1 = null;
Color hinterGrund2 = null;
int align = 0;
	   public ToolTipRenderer(Color col){
		   hinterGrund1 = new Color(255,255,255);
		   hinterGrund2 =  col;
	   }
	   
	   public Component getTableCellRendererComponent(JTable table, Object value,
	         boolean isSelected, boolean hasFocus, int row, int column) {
		   //setBackground(new Color(255,0,0));
		   //setBackground((row%2 ==0 ? hinterGrund1 : hinterGrund2));
		   setForeground(Color.BLACK);

		   if (isSelected) {
			   setOpaque(true);
	    	   setForeground(table.getSelectionForeground());
               setBackground(table.getSelectionBackground());
	       }else {
			   setOpaque(true);	    	   
               setForeground(table.getForeground());
               setBackground(table.getBackground());
	       }

		   
	        
	        if(value instanceof JLabel){
		    	  if( ((JLabel)value).toString().equals("")){
		    		  setText("Demo-Text"); 	    		  
		    	  }else{
	    			  setText( ((JLabel)value).getText() );
		    	  }
		     }
	        if(value instanceof java.lang.String){
	        	setText((String)value);
	        }
	        setToolTipText("Behandler: " + value) ;
	      return this;
	   }

	
	}
