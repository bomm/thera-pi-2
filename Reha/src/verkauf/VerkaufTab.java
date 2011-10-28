package verkauf;

import java.awt.BorderLayout;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.JXPanel;

import com.jgoodies.looks.windows.WindowsTabbedPaneUI;

import rechteTools.Rechte;
import rehaInternalFrame.JVerkaufInternal;



public class VerkaufTab extends JXPanel implements ChangeListener {


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
		//damit das Design gleich ist wie alle anderen TabbedPanes in Thera-Pi
		pane.setUI(new WindowsTabbedPaneUI());
		pane.addChangeListener(this);
		pane.add("Verkäufe tätigen", new VerkaufGUI());
		pane.add("Lagerverwaltung", new LagerGUI());	
		return pane;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JTabbedPane pane = (JTabbedPane)e.getSource();
		if(pane.getSelectedIndex() > 0 &&
				Rechte.hatRecht(Rechte.Sonstiges_artikelanlegen, true)){
			pane.setSelectedIndex(0);
			return;
		}
	}
}
