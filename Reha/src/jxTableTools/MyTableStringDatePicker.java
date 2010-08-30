package jxTableTools;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.jdesktop.swingx.JXDatePicker;

public class MyTableStringDatePicker extends AbstractCellEditor implements TableCellEditor,ActionListener{ 
	// This is the component that will handle the editing of the cell value 
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	JComponent component = new JXDatePicker(); 
	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy"); 
	// This method is called when a cell value is edited by the user. 

	public MyTableStringDatePicker(){
		//((JXDatePicker)component).addActionListener(((JComponent)component).getParent() );
		((JXDatePicker)component).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//label.setText(((JXDatePicker)component).getDate().toString());
			}
		});
	}
	public MyTableStringDatePicker(JXDatePicker xcomponent){
		//((JXDatePicker)component).addActionListener(((JComponent)component).getParent() );
		this.component = xcomponent;
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int rowIndex, int vColIndex) {
		if (isSelected)	{ 
			((JXDatePicker)component).getEditor().setEditable(false);

			// cell (and perhaps other cells) are selected } 
			// Configure the component with the specified value 
			//((JXDatePicker)component).setDate((Date) table.getValueAt(rowIndex,vColIndex) );
			try {
				((JXDatePicker)component).setDate( sdf.parse((String)value) );
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Return the configured component 
			((JXDatePicker)component).setVisible(true);
			return component;
		} // This method is called when editing is completed. 
		return null;
		
	}
	public Component getComponent(){
		return component;
	}
	// 'value' is value contained in the cell located at (rowIndex, vColIndex) 
	// It must return the new value to be stored in the cell. 
	public Object getCellEditorValue() { 
		return ( sdf.format( ((JXDatePicker)component).getDate() )  ); 
	} 
	public void actionPerformed(ActionEvent e) {
		//System.out.println("Action Performed in JXDatePicker");
		fireEditingStopped();
	}
	 public boolean stopCellEditing() {
		 fireEditingStopped();
		 return super.stopCellEditing();
	 }
	 public void cancelCellEditing() {
		 fireEditingCanceled();
	    	super.cancelCellEditing();
	 }
}
