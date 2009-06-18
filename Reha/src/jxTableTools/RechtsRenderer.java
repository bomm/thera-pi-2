package jxTableTools;

import javax.swing.table.DefaultTableCellRenderer;

public class RechtsRenderer extends DefaultTableCellRenderer {
		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


		public RechtsRenderer() {
			super();
			setHorizontalAlignment(javax.swing.SwingConstants. RIGHT);
			
		}
		

		public void setValue(Object value) {
			if(value == null){
				setText("");
			}else{
				setText((String)value);
			}
		}	

}
