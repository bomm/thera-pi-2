package Tools;

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
		if(value instanceof String){
			if(value == null){
				setText("");
			}else{
				setText((String)value);
			}
		}else if(value instanceof Integer){
			if(value == null){
				setText("");
			}else{
				setText(Integer.toString((Integer)value).replace(".", ""));
			}
		}
	}	

}
