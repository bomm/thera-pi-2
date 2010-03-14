package utils;

import javax.swing.table.DefaultTableCellRenderer;

public class MitteRenderer extends DefaultTableCellRenderer {
	/**
 * 
 */
private static final long serialVersionUID = 1L;


	public MitteRenderer() {
		super();
		setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		
	}
	

	public void setValue(Object value) {
		if(value == null){
			setText("");
		}else{
			setText((String)value);
		}
	}	

}
