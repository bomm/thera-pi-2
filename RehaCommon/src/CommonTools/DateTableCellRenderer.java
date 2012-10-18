package CommonTools;
import java.awt.Component;
import java.text.SimpleDateFormat;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class DateTableCellRenderer extends DefaultTableCellRenderer

{
	/**
	 * 
	 */
	int align;
	public DateTableCellRenderer(boolean center){
		super();
		align = (center ? javax.swing.SwingConstants.CENTER : javax.swing.SwingConstants.RIGHT);
	}
	private static final long serialVersionUID = -1029644753226393604L;
	SimpleDateFormat datumsFormat = new SimpleDateFormat ("dd.MM.yyyy");
	//DecimalFormat dform = new DecimalFormat("####0.00");
	public Component getTableCellRendererComponent(final JTable table, final
			Object value,boolean isSelected,boolean hasFocus,int row,int column){
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus,row, column);
			//setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			setHorizontalAlignment(align);
			if(value instanceof java.util.Date){
				if(value==null){
					setText(null);	
				}else{
					setText(datumsFormat.format(value));
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