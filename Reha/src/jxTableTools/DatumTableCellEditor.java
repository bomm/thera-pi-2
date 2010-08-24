package jxTableTools;

import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.EventObject;
import java.util.Formatter;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

import systemTools.JRtaTextField;

public class DatumTableCellEditor extends DefaultCellEditor{
	
	    JFormattedTextField ftf;
	    NumberFormat integerFormat;
	    private Integer minimum, maximum;
	    private boolean DEBUG = false;

	    public DatumTableCellEditor(int min,int max) {
	        super(new JFormattedTextField());
	        ftf = (JFormattedTextField)getComponent();
	        minimum = min;
	        maximum = max;

	        //Set up the editor for the integer cells.
	        integerFormat = NumberFormat.getIntegerInstance();
	        NumberFormatter intFormatter = new NumberFormatter(integerFormat);
	        intFormatter.setFormat(integerFormat);
	        intFormatter.setMinimum(minimum);
	        intFormatter.setMaximum(maximum);

	        ftf.setFormatterFactory(
	                new DefaultFormatterFactory(intFormatter));
	        ftf.setValue(minimum);
	        ftf.setHorizontalAlignment(JTextField.TRAILING);
	        ftf.setFocusLostBehavior(JFormattedTextField.PERSIST);

	        //React when the user presses Enter while the editor is
	        //active.  (Tab is handled as specified by
	        //JFormattedTextField's focusLostBehavior property.)
	        ftf.getInputMap().put(KeyStroke.getKeyStroke(
	                                        KeyEvent.VK_ENTER, 0),
	                                        "check");
	        ftf.getActionMap().put("check", new AbstractAction() {
				@Override
	        	public void actionPerformed(ActionEvent e) {

	            	if (!ftf.isEditValid()) { //The text is invalid.
	                    if (userSaysRevert()) { //reverted
			        ftf.postActionEvent(); //inform the editor
			    }
	                } else try {              //The text is valid,
	                    ftf.commitEdit();     //so use it.
	                    ftf.postActionEvent(); //stop editing
	                } catch (java.text.ParseException exc) { }
	            }

	        });
	    }

	    //Override to invoke setValue on the formatted text field.
	    public Component getTableCellEditorComponent(JTable table,
	            Object value, boolean isSelected,
	            int row, int column) {
	        JFormattedTextField ftf =
	            (JFormattedTextField)super.getTableCellEditorComponent(
	                table, value, isSelected, row, column);
	        ftf.setValue(value);
	        return ftf;
	    }

	    //Override to ensure that the value remains an Integer.
	    public Object getCellEditorValue() {
	        JFormattedTextField ftf = (JFormattedTextField)getComponent();
	        Object o = ftf.getValue();
	        if (o instanceof Integer) {
	            return o;
	        } else if (o instanceof Number) {
	            return Integer.valueOf(((Number)o).intValue());
	        } else {
	            if (DEBUG) {
	                //System.out.println("getCellEditorValue: o isn't a Number");
	            }
	            try {
	                return integerFormat.parseObject(o.toString());
	            } catch (ParseException exc) {
	                System.err.println("getCellEditorValue: can't parse o: " + o);
	                return null;
	            }
	        }
	    }

	    //Override to check whether the edit is valid,
	    //setting the value if it is and complaining if
	    //it isn't.  If it's OK for the editor to go
	    //away, we need to invoke the superclass's version 
	    //of this method so that everything gets cleaned up.
	    public boolean stopCellEditing() {
	        JFormattedTextField ftf = (JFormattedTextField)getComponent();
	        if (ftf.isEditValid()) {
	            try {
	                ftf.commitEdit();
	            } catch (java.text.ParseException exc) { }
		    
	        } else { //text is invalid
	            if (!userSaysRevert()) { //user wants to edit
		        return false; //don't let the editor go away
		    } 
	        }
	        return super.stopCellEditing();
	    }

	    /** 
	     * Lets the user know that the text they entered is 
	     * bad. Returns true if the user elects to revert to
	     * the last good value.  Otherwise, returns false, 
	     * indicating that the user wants to continue editing.
	     */
	    protected boolean userSaysRevert() {
	        Toolkit.getDefaultToolkit().beep();
	        ftf.selectAll();
	        Object[] options = {"Edit",
	                            "Revert"};
	        int answer = JOptionPane.showOptionDialog(
	            SwingUtilities.getWindowAncestor(ftf),
	            "The value must be an integer between "
	            + minimum + " and "
	            + maximum + ".\n"
	            + "You can either continue editing "
	            + "or revert to the last valid value.",
	            "Invalid Text Entered",
	            JOptionPane.YES_NO_OPTION,
	            JOptionPane.ERROR_MESSAGE,
	            null,
	            options,
	            options[1]);
		    
	        if (answer == 1) { //Revert!
	            ftf.setValue(ftf.getValue());
		    return true;
	        }
		return false;
	    }
	}
