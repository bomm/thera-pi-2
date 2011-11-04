package verkauf;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

import rechteTools.Rechte;
import rehaInternalFrame.JVerkaufInternal;
import systemEinstellungen.SystemConfig;
import systemTools.JRtaTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.windows.WindowsTabbedPaneUI;



public class VerkaufTab extends JXPanel implements ChangeListener {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public JVerkaufInternal eltern;
	JTabbedPane pane = null;
	JXLabel sucheLabel = null;
	JRtaTextField sucheText = null;
	JButton neuButton, editButton, delButton;
	
	public VerkaufTab(JVerkaufInternal vki) {
		super();
		eltern = vki;
		this.setOpaque(false);
		String ywerte = "0dlu, p, 0dlu, p:g";
		String xwerte = "p:g";
		FormLayout lay = new FormLayout(xwerte, ywerte);
		CellConstraints cc = new CellConstraints();
		this.setLayout(lay);
		this.add(getToolbar(), cc.xy(1, 2));
		this.add(getTabs(), cc.xy(1, 4));
	}
	
	private JTabbedPane getTabs() {
		
		pane = new JTabbedPane();
		//damit das Design gleich ist wie alle anderen TabbedPanes in Thera-Pi
		pane.setUI(new WindowsTabbedPaneUI());
		pane.addChangeListener(this);
		pane.addTab("Verkäufe tätigen",SystemConfig.hmSysIcons.get("verkaufTuten"), new VerkaufGUI());
		pane.addTab("Lager- und Artikelverwaltung", SystemConfig.hmSysIcons.get("verkaufArtikel"), new LagerGUI());	
		pane.addTab("Lieferantenverwaltung", SystemConfig.hmSysIcons.get("verkaufLieferant"), new LagerGUI());	
		pane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				int index =pane.getSelectedIndex();
				if(index == 0 || index == 1) {
					sucheLabel.setText("Artikel suchen:");
				} else if(index == 2) {
					sucheLabel.setText("Lieferanten suchen:");
				}
			}
			
		});
		return pane;
	}

	private JToolBar getToolbar() {
		JToolBar pane = new JToolBar();
		pane.setOpaque(false);
		//					1  2   3     4       5      6     7   8   9    10   11  12 13
		String xwerte = "5dlu, p, 5dlu, 80dlu, 5dlu, 80dlu:g, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu";
		String ywerte = "p";
		FormLayout lay = new FormLayout(xwerte, ywerte);
		CellConstraints cc = new CellConstraints();
		pane.setRollover(true);
		pane.setBorder(null);
		pane.setLayout(lay);
		
		JXLabel lab = new JXLabel(SystemConfig.hmSysIcons.get("find"));
		pane.add(lab, cc.xy(2, 1));
		
		sucheLabel = new JXLabel("Artikel suchen:");
		pane.add(sucheLabel, cc.xy(4, 1));
		
		sucheText = new JRtaTextField("nix", false);
		pane.add(sucheText, cc.xy(6, 1));
		
		neuButton = new JButton();
		neuButton.setOpaque(false);
		neuButton.setIcon(SystemConfig.hmSysIcons.get("neu"));
		neuButton.setActionCommand("neu");
		pane.add(neuButton, cc.xy(8, 1));
		
		editButton = new JButton();
		editButton.setOpaque(false);
		editButton.setIcon(SystemConfig.hmSysIcons.get("edit"));
		editButton.setActionCommand("neu");
		pane.add(editButton, cc.xy(10, 1));
		
		delButton = new JButton();
		delButton.setOpaque(false);
		delButton.setIcon(SystemConfig.hmSysIcons.get("delete"));
		delButton.setActionCommand("neu");
		pane.add(delButton, cc.xy(12, 1));
		
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
	}/*
	public void aktiviereFunktion(String funktion){
		if(pane.getSelectedIndex() == 0){
			((VerkaufGUI)pane.getComponentAt(0)).aktiviereFunktion(funktion);
		}else if(pane.getSelectedIndex() == 1){
			((LagerGUI)pane.getComponentAt(1)).aktiviereFunktion(funktion);			
		}else if(pane.getSelectedIndex() == 2){
			((LieferantenGUI)pane.getComponentAt(2)).aktiviereFunktion(funktion);
		}
	}*/
}
