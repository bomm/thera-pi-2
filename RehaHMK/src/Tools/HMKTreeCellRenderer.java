package Tools;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import sun.swing.DefaultLookup;

public class HMKTreeCellRenderer extends DefaultTreeCellRenderer implements TreeCellRenderer{
/**
	 * 
	 */
	private static final long serialVersionUID = -5690508896668147474L;
	private JTree tree;
	private boolean isDropCell;
	private IndiKey indi;
	private JXHMKTreeTableNode node ;
	//DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();
	   public HMKTreeCellRenderer(){
		   super();
	   }
	   
	   
	   public Component getTableCellRendererComponent(JTree table,
	                                                  Object value,
	                                                  boolean isSelected,
	                                                  boolean hasFocus,
	                                                  int row,
	                                                  int column) {
		   System.out.println("in treecell renderer");
		   setBackground(Color.BLACK);
		   return this;
		   
	   }
	    public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean sel,
                boolean expanded,
                boolean leaf, int row,
                boolean hasFocus) {
	    	String stringValue = null;
	    	if(((JXHMKTreeTableNode)value).key != null){
	    		node = ((JXHMKTreeTableNode)value);
	    		indi = ((JXHMKTreeTableNode)value).key;
	    		stringValue = (String) indi.indischl;
	    		setBackgroundSelectionColor((indi.ungerade ? HighlighterFactory.QUICKSILVER : HighlighterFactory.CLASSIC_LINE_PRINTER) );
	    		setBackgroundNonSelectionColor((indi.ungerade ? HighlighterFactory.BEIGE : HighlighterFactory.CLASSIC_LINE_PRINTER) );
	    		setBackground((indi.ungerade ? HighlighterFactory.BEIGE : HighlighterFactory.CLASSIC_LINE_PRINTER));
	    		setTextSelectionColor(Color.RED);
	    		//jXTreeTable.getColumnModel().getColumn(1).setCellEditor(myDate);
	    	}else{
	    		indi = null;
	    		stringValue = tree.convertValueToText(value, sel,   expanded, leaf, row, hasFocus);
	    	}
	    	
	    	this.tree = tree;

	    	this.hasFocus = hasFocus;
	    	setText(stringValue);
	    	
	    	Color fg = null;
	    	isDropCell = false;

	    	JTree.DropLocation dropLocation = tree.getDropLocation();
	    	if (dropLocation != null
	    			&& dropLocation.getChildIndex() == -1
	    			&& tree.getRowForPath(dropLocation.getPath()) == row) {
	    		
	    		Color col = DefaultLookup.getColor(this, ui, "Tree.dropCellForeground");
	    		if (col != null) {
	    			fg = col;
	    		} else {
	    			fg = getTextSelectionColor();
	    		}
	    		
	    		isDropCell = true;
	    	} else if (sel) {
	    		fg = getTextSelectionColor();
	    	} else {
	    		fg = getTextNonSelectionColor();
	    	}

	    	setForeground(fg);

	    	Icon icon = null;
	    	if (leaf) {
	    		icon = getLeafIcon();
	    	} else if (expanded) {
	    		icon = getOpenIcon();
	    	} else {
	    		icon = getClosedIcon();
	    	}

	    	if (!tree.isEnabled()) {
	    		setEnabled(false);
	    		LookAndFeel laf = UIManager.getLookAndFeel();
	    		Icon disabledIcon = laf.getDisabledIcon(tree, icon);
	    		if (disabledIcon != null) icon = disabledIcon;
	    		setDisabledIcon(icon);
	    	} else {
	    		setEnabled(true);
	    		setIcon(icon);
	    	}
	    	setComponentOrientation(tree.getComponentOrientation());

	    	selected = sel;


	    	return this;
	    }
	

	
}
