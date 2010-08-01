package rehaBillEdit;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JTabbedPane;

import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;

import com.jgoodies.looks.windows.WindowsTabbedPaneUI;

public class RehaBillEditTab extends JXPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7580783032048353314L;
	/*
	private Vector<String> vectitel = new Vector<String>();
	private Vector<String> vecdescript = new Vector<String>();
	private Vector<ImageIcon> vecimg = new Vector<ImageIcon>();
	*/
	JTabbedPane billEditTab = null;
	public JXTitledPanel jxTitel;
	public JXHeader jxh;
	
	RehaBillPanel editPanel = null;
	
	RehaBillPanel billPanel = null;
	
	public RehaBillEditTab(){
		super();
		setLayout(new BorderLayout());
		billEditTab = new JTabbedPane();
		billEditTab.setUI(new WindowsTabbedPaneUI());
		
		billPanel = new RehaBillPanel(this);
		billEditTab.add("Rechnung ändern/Kopie erstellen",billPanel);
		
		jxh = new JXHeader();
        ((JLabel)jxh.getComponent(1)).setVerticalAlignment(JLabel.NORTH);
        add(jxh, BorderLayout.NORTH);
        add(billEditTab, BorderLayout.CENTER);

        jxh.validate();
        billEditTab.validate();
		validate();

		
	}

}
