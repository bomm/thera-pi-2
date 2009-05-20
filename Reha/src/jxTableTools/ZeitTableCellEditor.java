package jxTableTools;

import java.awt.Component;
import java.text.ParseException;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

public class ZeitTableCellEditor extends AbstractCellEditor implements TableCellEditor {
    // This is the component that will handle the editing of the cell value
    JComponent component = new JFormattedTextField();
    MaskFormatter mf = new MaskFormatter();
    JTable tab;
    int reihe;
    
    public ZeitTableCellEditor(){
        try {
			mf.setMask("##:##");
			mf.setOverwriteMode(true);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        //mf.setPlaceholderCharacter(':');
		DefaultFormatterFactory factory = new DefaultFormatterFactory(mf);
		((JFormattedTextField)component).setFormatterFactory(factory);
    	
    }
    

    // This method is called when a cell value is edited by the user.
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
        // 'value' is value contained in the cell located at (rowIndex, vColIndex)

        if (isSelected) {
            // cell (and perhaps other cells) are selected
        }

        // Configure the component with the specified value
        ((JFormattedTextField)component).setText((String)value);
        ((JFormattedTextField)component).select(0,((String)value).length()-1);
        tab = table;
        reihe = rowIndex;
        // Return the configured component
        return component;
    }

    // This method is called when editing is completed.
    // It must return the new value to be stored in the cell.

    public Object getCellEditorValue() {
        return ((JFormattedTextField)component).getText();
    }
    /*
    public boolean startCellEditing() {
        String s = (String)getCellEditorValue();
    	//((JFormattedTextField)component).setCaretPosition(0);
    	System.out.println("Zu Beginn der Editing / Ganzer String = "+s);
    	
    	if (! testeUhr(s) ) {
            return false;
        }
        
        return startCellEditing();
    }
    */
/*********************************/	
	public boolean testeUhr(String s){
		boolean ret = true;
		System.out.println("substr 0,1 = "+s.substring(0,1));
		System.out.println("substr 3,4 = "+s.substring(3,4));
		System.out.println("Ganzer String = "+s);
			if(s.length() < 5){return false;}
			if(new Integer(s.substring(0,1)) > 2 ){return false;}
			if(new Integer(s.substring(3,4)) > 5 ){return false;}
			if(new Integer(s.substring(0,2)) > 24 ){return false;}
			if(new Integer(s.substring(0,2)) == 24 ){
				if(new Integer(s.substring(3,4)) > 0 ){return false;}
				if(new Integer(s.substring(4,5)) > 0 ){return false;}
			}
						
			
		return ret;
	}
/********************************/
	@Override
    public boolean stopCellEditing() {
        String s = (String)getCellEditorValue();

        if (! testeUhr(s)) {
        	((JFormattedTextField)component).setCaretPosition(0);
            // Should display an error message at this point
            tab.setRowSelectionInterval(reihe,reihe);
        	return false;
        }
        tab.setRowSelectionInterval(reihe,reihe);
        return super.stopCellEditing();
    }
	


}
