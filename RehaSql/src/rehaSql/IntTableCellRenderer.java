package rehaSql;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class IntTableCellRenderer extends DefaultTableCellRenderer

{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	//DecimalFormat dform = new DecimalFormat("####0.00");
	public Component getTableCellRendererComponent(final JTable table, final
			Object value,boolean isSelected,boolean hasFocus,int row,int column){
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus,row, column);
			setHorizontalAlignment(javax.swing.SwingConstants. RIGHT);
			if(value instanceof Integer){
				if(value==null){
					setText(null);	
				}else{
					setText(Integer.toString((Integer)value));
				}
			}else{
				if(value==null){
					setText(null);
				}else{
					setText(value.toString());					
				}
			}
			return this;
	}
}