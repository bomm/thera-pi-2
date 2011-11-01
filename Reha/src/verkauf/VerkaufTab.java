package verkauf;

import java.awt.BorderLayout;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
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
	JTabbedPane pane = null;
	
	public VerkaufTab(JVerkaufInternal vki) {
		super();
		eltern = vki;
		this.setOpaque(false);
		this.setLayout(new BorderLayout());
		this.add(getContent(), BorderLayout.CENTER);
	}
	
	private JTabbedPane getContent() {
		pane = new JTabbedPane();
		//damit das Design gleich ist wie alle anderen TabbedPanes in Thera-Pi
		pane.setUI(new WindowsTabbedPaneUI());
		pane.addChangeListener(this);
		pane.add("Verkäufe tätigen", new VerkaufGUI());
		pane.add("Lager- und Artikelverwaltung", new LagerGUI());	
		return pane;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		try{
			if(  (((JTabbedPane)e.getSource()).getSelectedIndex() > 0) &&
					 (!Rechte.hatRecht(Rechte.Sonstiges_artikelanlegen, false)) ){
				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						pane.setSelectedIndex(0);
						JOptionPane.showMessageDialog(pane,"Keine Berechtigung zum Aufruf der Artikelverwaltung");
					}
				});
				return;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public void removeListeners(){
		((VerkaufGUI)pane.getComponentAt(0)).removeListener();
		((LagerGUI)pane.getComponentAt(1)).removeListener();
	}
}
