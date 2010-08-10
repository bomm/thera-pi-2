package jxTableTools;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class AbrechnungTreeTableRenderer extends JLabel implements TableCellRenderer {
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private boolean isBordered;
public AbrechnungTreeTableRenderer(boolean isBordered) {
	this.isBordered = isBordered;
	setOpaque(true); //MUST do this for background to show up.
}

public Component getTableCellRendererComponent(JTable table, Object color,boolean isSelected, boolean hasFocus,
		 int row, int column) {
		Color newColor = (Color)color;
		setBackground(newColor);
		if (isBordered) {
			if (isSelected) {
				//selectedBorder is a solid border in the color
				//table.getSelectionBackground().
				//setBorder(selectedBorder);
		} else {
			//unselectedBorder is a solid border in the color
			//table.getBackground().
			//setBorder(unselectedBorder);
		}
}
setToolTipText("kdkdkdkd"); //Discussed in the following section
return this;
}
}



