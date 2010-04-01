package systemTools;

import java.awt.Component;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

public class IconListRenderer extends DefaultListCellRenderer {
	private Map<Object, ImageIcon> icons = null;
	 
	public IconListRenderer(Map<Object, ImageIcon> icons) {
	        this.icons = icons;
	}
    @Override
    public Component getListCellRendererComponent(
	        JList list, Object value, int index,
	        boolean isSelected, boolean cellHasFocus) {
	        // Get the renderer component from parent class
	        JLabel label =
	            (JLabel) super.getListCellRendererComponent(list,
	                value, index, isSelected, cellHasFocus);
	        // Get icon to use for the list item value
	        ImageIcon icon = icons.get(value);
	        // Set icon to display for value
	        label.setIcon(icon);
	        return label;
    }
}
