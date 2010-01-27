package jxTableTools;



import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.jdesktop.swingx.JXDatePicker;

import systemTools.JRtaComboBox;

public class MyTableComboBox extends AbstractCellEditor implements TableCellEditor{ 
	// This is the component that will handle the editing of the cell value 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JComponent component = new JRtaComboBox(); 
	 
	// This method is called when a cell value is edited by the user. 
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int rowIndex, int vColIndex) {
		if (isSelected)	{ 
			((JRtaComboBox)component).setSelectedVecIndex(0, (String)table.getValueAt(rowIndex,vColIndex) );
			((JRtaComboBox)component).setVisible(true);
			return component;
		} // This method is called when editing is completed. 
		return null;
		
	}
	public Component getComponent(){
		return component;
	}

	public void setVector(Vector<Vector<String>> vec,int disp,int ret){
		((JRtaComboBox)component).setDataVectorVector(vec, disp, ret);
	}
	// 'value' is value contained in the cell located at (rowIndex, vColIndex) 
	// It must return the new value to be stored in the cell. 
	public Object getCellEditorValue() { 
		try{
			return ( ((JRtaComboBox)component).getValueAt(0)  );
		}catch(Exception ex){
			return null;
		}
	} 

	 public boolean stopCellEditing() {
		 try{
		 fireEditingStopped();
		 }catch(Exception ex){}
		 return super.stopCellEditing();
	 }
	 public void cancelCellEditing() {
		 fireEditingCanceled();
	    	super.cancelCellEditing();
	    }
}
