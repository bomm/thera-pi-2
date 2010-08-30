package jxTableTools;


import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;

import systemTools.JRtaCheckBox;

public class MyTableCheckBox extends AbstractCellEditor implements TableCellEditor,ActionListener{ 
	// This is the component that will handle the editing of the cell value 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JComponent component = null; //new JRtaCheckBox(); 
	
	public MyTableCheckBox(){
		component = new JRtaCheckBox();
		((JRtaCheckBox)component).setVerticalAlignment(SwingConstants.CENTER);
		((JRtaCheckBox)component).setHorizontalAlignment(SwingConstants.CENTER);
		((JRtaCheckBox)component).setOpaque(true);
		//((JRtaCheckBox)component).addActionListener(this);		

	}
	public MyTableCheckBox(JRtaCheckBox box, ActionListener al){
		component = box;
		
		((JRtaCheckBox)component).setVerticalAlignment(SwingConstants.CENTER);
		((JRtaCheckBox)component).setHorizontalAlignment(SwingConstants.CENTER);
		((JRtaCheckBox)component).setOpaque(true);
		
		((JRtaCheckBox)component).addActionListener(al);
		((JRtaCheckBox)component).addActionListener(this);
	}
	 
	// This method is called when a cell value is edited by the user. 
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int rowIndex, int vColIndex) {
		if (isSelected)	{ 
			//((JRtaCheckBox)component).setVerticalAlignment(SwingConstants.CENTER);
			//((JRtaCheckBox)component).setHorizontalAlignment(SwingConstants.CENTER);
			((JRtaCheckBox)component).setOpaque(true);
			((JRtaCheckBox)component).setBackground(table.getSelectionBackground());
			((JRtaCheckBox)component).setSelected(Boolean.valueOf((Boolean)table.getValueAt(rowIndex,vColIndex)) );
			//((JRtaCheckBox)component).setVisible(true);
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
		return ( ((JRtaCheckBox)component).isSelected() ); 
	} 

	 public boolean stopCellEditing() {
		 fireEditingStopped();
		 return super.stopCellEditing();
	 }
	 public void cancelCellEditing() {
		 fireEditingCanceled();
		 super.cancelCellEditing();
	    }
	@Override
	public void actionPerformed(ActionEvent arg0) {
		fireEditingStopped();
		////System.out.println("Interner ActionListener selected="+((JRtaCheckBox)component).isSelected());
		
	}
}
