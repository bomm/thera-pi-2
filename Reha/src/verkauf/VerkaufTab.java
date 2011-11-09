package verkauf;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingworker.SwingWorker;
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
	
	/**Konstanten für aktiviereFunktion **/
	public static int neu = 1;
	public static int edit = 2;
	public static int delete = 3;
	public static int rechnungEnde = 4;
	public static int bonEnde = 5;
	public static int suche = 6;
	
	private static final long serialVersionUID = 1L;
	JVerkaufInternal eltern;
	JTabbedPane pane = null;
	JXLabel sucheLabel = null;
	JButton neuButton, editButton, delButton;
	ActionListener al;
	KeyListener kl;
	MouseListener ml;
	
	public VerkaufTab(JVerkaufInternal vki) {
		super();
		eltern = vki;
		this.activateListener();
		this.setOpaque(false);
		String ywerte = "0dlu, 300dlu:g, 0dlu";
		String xwerte = "0dlu, 200dlu:g, 0dlu";
		FormLayout lay = new FormLayout(xwerte, ywerte);
		CellConstraints cc = new CellConstraints();
		this.setLayout(lay);
		this.add(getTabs(), cc.xy(2, 2,CellConstraints.FILL,CellConstraints.FILL));
	}
	
	private JTabbedPane getTabs() {
		
		pane = new JTabbedPane();
		//damit das Design gleich ist wie alle anderen TabbedPanes in Thera-Pi
		pane.setUI(new WindowsTabbedPaneUI());
		pane.addChangeListener(this);
		pane.addTab("Verkäufe tätigen",SystemConfig.hmSysIcons.get("verkaufTuten"), new VerkaufGUI(this));
		pane.addTab("Lager- und Artikelverwaltung", SystemConfig.hmSysIcons.get("verkaufArtikel"), new LagerGUI(this));	
		pane.addTab("Lieferantenverwaltung", SystemConfig.hmSysIcons.get("verkaufLieferant"), new LieferantGUI(this));	
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
		pane.validate();
		return pane;
	}

	JToolBar getToolbar(JRtaTextField sucheText) {
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
		
		/*
		JXLabel lab = new JXLabel(SystemConfig.hmSysIcons.get("find"));
		lab.addMouseListener(this.ml);
		lab.setHorizontalTextPosition(SwingConstants.RIGHT);
		pane.add(lab, cc.xy(2, 1));
		*/
		sucheLabel = new JXLabel("Artikel suchen:");
		sucheLabel.setIcon(SystemConfig.hmSysIcons.get("find"));
		sucheLabel.addMouseListener(this.ml);
		sucheLabel.setHorizontalTextPosition(SwingConstants.LEFT);
		pane.add(sucheLabel, cc.xyw(2, 1, 3,CellConstraints.RIGHT,CellConstraints.DEFAULT));
	
		sucheText.addKeyListener(kl);
		pane.add(sucheText, cc.xy(6, 1));
		
		neuButton = new JButton();
		neuButton.setOpaque(false);
		neuButton.setIcon(SystemConfig.hmSysIcons.get("neu"));
		neuButton.setActionCommand("neu");
		neuButton.addActionListener(al);
		pane.add(neuButton, cc.xy(8, 1));
		
		editButton = new JButton();
		editButton.setOpaque(false);
		editButton.setIcon(SystemConfig.hmSysIcons.get("edit"));
		editButton.setActionCommand("edit");
		editButton.addActionListener(al);
		pane.add(editButton, cc.xy(10, 1));
		
		delButton = new JButton();
		delButton.setOpaque(false);
		delButton.setIcon(SystemConfig.hmSysIcons.get("delete"));
		delButton.setActionCommand("delete");
		delButton.addActionListener(al);
		pane.add(delButton, cc.xy(12, 1));
		
		pane.validate();
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
		this.al = null;
	}
	
	private void activateListener() {
		al = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ex) {


				final ActionEvent e = ex;
				
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						try{
							if(e.getActionCommand().equals("neu")) {
								aktiviereFunktion(VerkaufTab.neu);
							} else if(e.getActionCommand().equals("edit")) {
								aktiviereFunktion(VerkaufTab.edit);
							} else if(e.getActionCommand().equals("delete")) {
								aktiviereFunktion(VerkaufTab.delete);
							} else if(e.getActionCommand().equals("bonEnde")) {
								aktiviereFunktion(VerkaufTab.bonEnde);
							} else if(e.getActionCommand().equals("rechnungEnde")) {
								aktiviereFunktion(VerkaufTab.rechnungEnde);
							}
						}catch(Exception ex){
							ex.printStackTrace();
						}
						return null;
					}
				}.execute();

				
			}
			
		};
		kl = new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					aktiviereFunktion(VerkaufTab.suche);
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
			}
			
		};
		ml = new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				aktiviereFunktion(VerkaufTab.suche);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
		};
	}
	
	public void aktiviereFunktion(int funktion){
		if(pane.getSelectedIndex() == 0){
			((VerkaufGUI)pane.getComponentAt(0)).aktiviereFunktion(funktion);
		}else if(pane.getSelectedIndex() == 1){
			((LagerGUI)pane.getComponentAt(1)).aktiviereFunktion(funktion);			
		}else if(pane.getSelectedIndex() == 2){
			((LieferantGUI)pane.getComponentAt(2)).aktiviereFunktion(funktion);
		}
	}
	
	public Point holePosition(int zielBreite, int zielHöhe) {
		Point p = this.getLocationOnScreen();
		int breite = this.getWidth() / 2;
		int höhe = this.getHeight() / 2;
		zielBreite = zielBreite / 2;
		zielHöhe = zielHöhe / 2;
		
		p.x = p.x + breite - zielBreite;
		p.y = p.y + höhe - zielHöhe;
		
		return p;
	}
}
