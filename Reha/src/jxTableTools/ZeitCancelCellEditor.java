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

public class ZeitCancelCellEditor extends AbstractCellEditor implements TableCellEditor {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4850040467284880352L;
	// This is the component that will handle the editing of the cell value
    JComponent component = new JFormattedTextField();
    MaskFormatter mf = new MaskFormatter();
    JTable tab;
    
    public ZeitCancelCellEditor(){
        try {
			mf.setMask("##:##");
			mf.setOverwriteMode(true);
			mf.setCommitsOnValidEdit(false);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        //mf.setPlaceholderCharacter(':');
		DefaultFormatterFactory factory = new DefaultFormatterFactory(mf);
		((JFormattedTextField)component).setFormatterFactory(factory);
		//((JFormattedTextField)component).
    	
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
        // Return the configured component
        return component;
    }

    // This method is called when editing is completed.
    // It must return the new value to be stored in the cell.

    public Object getCellEditorValue() {
        return ((JFormattedTextField)component).getText();
    }
	public boolean startCellEditing() {
        //String s = (String)getCellEditorValue();
    	((JFormattedTextField)component).setCaretPosition(0);
    	/*
    	if (! testeUhr(s) ) {
            return false;
        }
        */
        return true; //startCellEditing();
    }
/*********************************/	
	
	public boolean testeUhr(String s){
		boolean ret = true;
		//System.out.println("substr 0,1 = "+s.substring(0,1));
		//System.out.println("substr 3,4 = "+s.substring(3,4));

		if(s.trim().equals(":")){
			((JFormattedTextField)component).setText("");
			//System.out.println("equals(':') String = "+s);
			return true;
		}

		if(s.length() < 5){
				((JFormattedTextField)component).setText("");
				return true;
			}
			if(Integer.valueOf(s.substring(0,1)) > 2 ){
				((JFormattedTextField)component).setText("");
				return true;
			}
			if(Integer.valueOf(s.substring(3,4)) > 5 ){
				((JFormattedTextField)component).setText("");
				return true;
			}
			if(Integer.valueOf(s.substring(0,2)) > 24 ){
				((JFormattedTextField)component).setText("");
				return true;
			}
			if(Integer.valueOf(s.substring(0,2)) == 24 ){
				if(Integer.valueOf(s.substring(3,4)) > 0 ){
					((JFormattedTextField)component).setText("");
					return true;
				}
				if(Integer.valueOf(s.substring(4,5)) > 0 ){
					((JFormattedTextField)component).setText("");
					return true;
				}
			}
		return ret;
	}
/********************************/
	@Override
	public boolean stopCellEditing() {
        String s = (String)getCellEditorValue();
		//System.out.println("In stop cell Editing");
        if (! testeUhr(s)) {
        	((JFormattedTextField)component).setCaretPosition(0);
            // Should display an error message at this point
            return false;
        }
        return super.stopCellEditing();
    }
	


}
