package rehaSql;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JTabbedPane;

import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;



import com.jgoodies.looks.windows.WindowsTabbedPaneUI;

public class RehaSqlTab extends JXPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7580783032048353314L;
	/*
	private Vector<String> vectitel = new Vector<String>();
	private Vector<String> vecdescript = new Vector<String>();
	private Vector<ImageIcon> vecimg = new Vector<ImageIcon>();
	*/
	JTabbedPane sqlTab = null;
	public JXTitledPanel jxTitel;
	public JXHeader jxh;
	
	RehaSqlEdit sqlEditPanel = null;
	
	RehaSqlPanel sqlPanel = null;
	
	public RehaSqlTab(){
		super();
		setLayout(new BorderLayout());
		sqlTab = new JTabbedPane();
		sqlTab.setUI(new WindowsTabbedPaneUI());
		
		sqlPanel = new RehaSqlPanel(this);
		sqlTab.add("Sql-Befehle absetzen",sqlPanel);
		
		sqlEditPanel = new RehaSqlEdit(this);
		sqlTab.add("Sql-Befehle entwerfen/bearbeiten",sqlEditPanel);
		
		jxh = new JXHeader();
        ((JLabel)jxh.getComponent(1)).setVerticalAlignment(JLabel.NORTH);
        add(jxh, BorderLayout.NORTH);
        add(sqlTab, BorderLayout.CENTER);

        jxh.validate();
        sqlTab.validate();
		validate();

		
	}

}
