package verkauf;

import java.awt.BorderLayout;

import javax.swing.JTabbedPane;

import org.jdesktop.swingx.JXPanel;

import rehaInternalFrame.JVerkaufInternal;



public class VerkaufTab extends JXPanel {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public JVerkaufInternal eltern;
	
	public VerkaufTab(JVerkaufInternal vki) {
		super();
		eltern = vki;
		this.setOpaque(false);
		this.setLayout(new BorderLayout());
		this.add(getContent(), BorderLayout.CENTER);
	}
	
	private JTabbedPane getContent() {
		JTabbedPane pane = new JTabbedPane();
		pane.add("Verkäufe tätigen", new VerkaufGUI());
		pane.add("Lagerverwaltung", new LagerGUI());	
		return pane;
	}
}
