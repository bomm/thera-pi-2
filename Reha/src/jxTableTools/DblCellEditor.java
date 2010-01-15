package jxTableTools;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;

public class DblCellEditor extends AbstractCellEditor implements TableCellEditor {
    JComponent component = new JFormattedTextField();
    boolean mitMaus = false;

    // This method is called when editing is completed.
    // It must return the new value to be stored in the cell.
    public Object getCellEditorValue() {
    	String foo;
    	try{
    		foo = ((JFormattedTextField)component).getText().replaceAll(",", ".");
    		if(foo.length()==0){foo ="0.00";}
    	}catch(Exception ex){
    		foo = "0.00";
    	}
        double i_spent_hours_on_this = Double.valueOf(foo);
        return new Double(i_spent_hours_on_this);
    }
    // This method is called when a cell value is edited by the user.
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        // Configure the component with the specified value
        ((JFormattedTextField)component).setText(String.valueOf(value));
        ((JFormattedTextField)component).selectAll();
        ((JFormattedTextField)component).setHorizontalAlignment(SwingConstants.RIGHT);
        
        // Return the configured component
        //System.out.println("I've been Called!!");
        return component;
    }
    


    public boolean isCellEditable(EventObject evt) {
        if (evt instanceof MouseEvent) {
        	mitMaus = true;
            return false;
        } else {
        	mitMaus = false;
            return true;
        }
    }

}