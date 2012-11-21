package jxTableTools;



import java.awt.Component;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import CommonTools.JRtaComboBox;

public class MyTableComboBox extends AbstractCellEditor implements TableCellEditor{ 
	// This is the component that will handle the editing of the cell value 
	/**
	 * 
	 */
	JComponent component =null;
	public MyTableComboBox(){
		component = new JRtaComboBox();
	}
	
	public MyTableComboBox(JRtaComboBox comboParam){
		component = comboParam;
	}
	private static final long serialVersionUID = 1L;
 
	 
	// This method is called when a cell value is edited by the user. 
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int rowIndex, int vColIndex) {
		if (isSelected)	{ 
			if(((JRtaComboBox)component).vec != null){
				((JRtaComboBox)component).setSelectedVecIndex(0, (String)table.getValueAt(rowIndex,vColIndex) );
				((JRtaComboBox)component).setVisible(true);
			}else{
				((JRtaComboBox)component).setSelectedItem( (String)table.getValueAt(rowIndex,vColIndex) );
			}
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
			if(((JRtaComboBox)component).vec != null){
				return ( ((JRtaComboBox)component).getValueAt(0)  );	
			}else{
				return ( ((JRtaComboBox)component).getSelectedItem() );
			}
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
