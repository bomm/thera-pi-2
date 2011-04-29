package Tools;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;

import Tools.JRtaTextField;



@SuppressWarnings("unused")
public class DblCellEditor extends AbstractCellEditor implements KeyListener,TableCellEditor {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2101893331215081609L;
	JComponent component = new JRtaTextField("D",true,"6.2","");
    boolean mitMaus = false;

    // This method is called when editing is completed.
    // It must return the new value to be stored in the cell.
    public DblCellEditor(){
    	component.addKeyListener(this);
    	((JRtaTextField)component).listenerLoeschen();
    }
    public Object getCellEditorValue() {
    	String foo;
    	try{
    	foo = ((JFormattedTextField)component).getText().replaceAll(",", ".");
        if(foo.length()==0){foo ="0.00";}
    	}catch(Exception ex){
    		foo = "0.00";
    	}
    	double i_spent_hours_on_this;
    	try{
    		i_spent_hours_on_this = Double.valueOf(foo);
    	}catch(Exception ex){
    		i_spent_hours_on_this = Double.valueOf("0.00");
    	}
        return new Double(i_spent_hours_on_this);
    }
    // This method is called when a cell value is edited by the user.
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        // Configure the component with the specified value
    	if(!mitMaus){
            ((JFormattedTextField)component).setText(String.valueOf(value));
            ((JFormattedTextField)component).selectAll();
            ((JFormattedTextField)component).setHorizontalAlignment(SwingConstants.RIGHT);
            ((JFormattedTextField)component).setBackground(Color.YELLOW);

    	}else{
    		final String xvalue = String.valueOf(value);
    		SwingUtilities.invokeLater(new Runnable(){
    			public void run(){
    	    		((JFormattedTextField)component).setText(String.valueOf(xvalue).replace(".", ","));
    	            ((JFormattedTextField)component).selectAll();
    	            ((JFormattedTextField)component).setBackground(Color.YELLOW);
    	            ((JFormattedTextField)component).setHorizontalAlignment(SwingConstants.RIGHT);
    	            ((JFormattedTextField)component).setCaretPosition(0);
    			}
    		});
                		
    	}

        
        // Return the configured component
        //////System.out.println("I've been Called!!");
        return component;
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

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		if(mitMaus && arg0.getKeyCode()==10){
			//////System.out.println("in Maus + Return gedrückt");
			this.fireEditingStopped();
		}
		if(arg0.getKeyCode()==27){
			//////System.out.println("in Maus + Return gedrückt");
			this.fireEditingCanceled();
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