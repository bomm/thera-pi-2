package jxTableTools;

import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class DoubleTableCellRenderer extends DefaultTableCellRenderer

{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	DecimalFormat dform = new DecimalFormat("####0.00");
	public Component getTableCellRendererComponent(final JTable table, final
			Object value,boolean isSelected,boolean hasFocus,int row,int column){
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus,row, column);
			setHorizontalAlignment(javax.swing.SwingConstants. RIGHT);
			if(value instanceof Double){
				Double d = (Double) value;
				setText(String.format("%.2f", d));
			}else{
				try{
					setText(value.toString());
				}catch(Exception ex){
					setText("0,00");	
				}
				
			}
			return this;
	}
}
