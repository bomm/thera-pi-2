package jxTableTools;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
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
import javax.swing.text.NumberFormatter;

public class ZahlTableCellEditor extends AbstractCellEditor implements KeyListener,TableCellEditor {
    // This is the component that will handle the editing of the cell value
    JComponent component = new JFormattedTextField();
    MaskFormatter mf = new MaskFormatter();
    boolean mitMaus = false;
    JTable tab;
    
    public ZahlTableCellEditor(){

       	
    	try {
			mf.setMask("###");
			mf.setOverwriteMode(true);
			//mf.setCommitsOnValidEdit(false);			

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       	//mf.setOverwriteMode(true);
		DefaultFormatterFactory factory = new DefaultFormatterFactory(mf);
		((JFormattedTextField)component).setFormatterFactory(factory);
		component.addKeyListener(this);
    	
    }
    

    // This method is called when a cell value is edited by the user.
    
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
        // 'value' is value contained in the cell located at (rowIndex, vColIndex)

        if (isSelected) {
            // cell (and perhaps other cells) are selected
        }

        // Configure the component with the specified value
        ((JFormattedTextField)component).setText(((String)value));
        SwingUtilities.invokeLater(new Runnable(){
        	public void run(){
                //
                ((JFormattedTextField)component).setCaretPosition(0);
        		
        	}
        });
        tab = table;
   
        //((JFormattedTextField)component).select(0,((String)value).length()-1);
        // Return the configured component
        return component;
    }

    // This method is called when editing is completed.
    // It must return the new value to be stored in the cell.
    
    public Object getCellEditorValue() {
        return ((JFormattedTextField)component).getText().trim();
    }
    /*
    public boolean startCellEditing() {
        String s = (String)getCellEditorValue();
		//System.out.println("Zu Beginn der Editing / Ganzer String = "+s);
        ((JFormattedTextField)component).setCaretPosition(0);
        return startCellEditing();
    }
    */
/*********************************/	
    @Override
    public boolean isCellEditable(EventObject evt) {
        if (evt instanceof MouseEvent) {
        	if(((MouseEvent)evt).getClickCount()==2){
        		((MouseEvent)evt).consume();
            	mitMaus = true;
                return true;
        	}
        } else if(evt instanceof KeyEvent) {
        	((KeyEvent)evt).consume();
        	mitMaus = false;
            return true;
        }
        return false;
    }
	
	public boolean testeUhr(String s){
		boolean ret = true;
		if(s.trim().equals("") ){
	    	((JFormattedTextField)component).setCaretPosition(0);
			//System.out.println("equals('') String= "+s);
			return false;
		}
		if(new Integer(s.substring(0,1)) == 0 ){
		    	((JFormattedTextField)component).setCaretPosition(0);
				//System.out.println("Erste Zahl = 0 String = "+s);
		    	return false;
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
            return false;
        }
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
