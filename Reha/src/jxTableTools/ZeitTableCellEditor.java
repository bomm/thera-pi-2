package jxTableTools;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

public class ZeitTableCellEditor extends AbstractCellEditor implements KeyListener,TableCellEditor {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3654965278742138928L;
	// This is the component that will handle the editing of the cell value
    JComponent component = new JFormattedTextField();
    MaskFormatter mf = new MaskFormatter();
    JTable tab;
    int reihe;
    boolean mitMaus;
    
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
    @Override
    public boolean isCellEditable(EventObject evt) {
        if (evt instanceof MouseEvent) {
        	if(((MouseEvent)evt).getClickCount()==2){
        		((MouseEvent)evt).consume();
            	mitMaus = true;
                return true;
        	}
        } else {
        	mitMaus = false;
            return true;
        }
        return false;
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
        //final Object xvalue = value;
        ((JFormattedTextField)component).setText((String)value);
        SwingUtilities.invokeLater(new Runnable(){
        	public void run(){
                //((JFormattedTextField)component).select(0,((String)xvalue).length()-1);
                ((JFormattedTextField)component).setCaretPosition(0);
        	}
        });

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
    	//System.out.println("Zu Beginn der Editing / Ganzer String = "+s);
    	
    	if (! testeUhr(s) ) {
            return false;
        }
        
        return startCellEditing();
    }
    */
/*********************************/	
	public boolean testeUhr(String s){
		boolean ret = true;
		//System.out.println("substr 0,1 = "+s.substring(0,1));
		//System.out.println("substr 3,4 = "+s.substring(3,4));
		//System.out.println("Ganzer String = "+s);
			if(s.length() < 5){return false;}
			if(Integer.valueOf(s.substring(0,1)) > 2 ){return false;}
			if(Integer.valueOf(s.substring(3,4)) > 5 ){return false;}
			if(Integer.valueOf(s.substring(0,2)) > 24 ){return false;}
			if(Integer.valueOf(s.substring(0,2)) == 24 ){
				if(Integer.valueOf(s.substring(3,4)) > 0 ){return false;}
				if(Integer.valueOf(s.substring(4,5)) > 0 ){return false;}
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


	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getKeyCode()==10){
			////System.out.println("in Maus + Return gedrückt");
			this.fireEditingStopped();
		}
		if(arg0.getKeyCode()==27){
			this.cancelCellEditing();
		}
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	


}
