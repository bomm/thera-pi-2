package verkauf;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.thera_pi.nebraska.gui.utils.ButtonTools;
import org.thera_pi.nebraska.gui.utils.JCompTools;

import systemEinstellungen.INIFile;
import systemTools.JRtaTextField;
import verkauf.model.Artikel;
import verkauf.model.Lieferant;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class LagerGUI extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	LieferantDialog lfdlg = null;	
	ActionListener al = null;
	ListSelectionListener ll = null;
	JRtaTextField[] edits = {null, null, null, null,null};
	JRtaTextField einkaufspreis;
	JButton[] buts = {null, null, null};
	JComboBox[] combo = {null, null};
	JComboBox lieferant;
	JButton lieferantEdit, lieferantNeu;
	MyLagerTableModel lgmod = new MyLagerTableModel();
	JXTable lgtab = null;
	JScrollPane jscr = null;
	int lastcol = 5;
	int lastSelected = -1;
	
	Artikel aktuellerArtikel = null;

	public LagerGUI() {
		super();
		this.activateListener();
		this.setOpaque(false);
		this.setLayout(new BorderLayout());
		this.add(getContent(), BorderLayout.CENTER);
	}
	public void removeListener(){
	}

	
	private JXPanel getContent() {
		JXPanel pan = new JXPanel();
		JXLabel lab = new JXLabel();
		 //                1     2      3     4        5     6      7     8      9    10     11    12      13
		String xwerte = "5dlu, 60dlu, 5dlu, 120dlu:g, 5dlu, 60dlu, 5dlu, 60dlu, 5dlu, 60dlu, 5dlu, 60dlu, 5dlu";
		//					1		2		3  4  5   6    7   8    9  10  11
		String ywerte = "5dlu, 160dlu:g, 5dlu, p, p, 5dlu, p, p, 5dlu, p, 5dlu";
		FormLayout lay = new FormLayout(xwerte, ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		
		/**Tabelle**/
		lgtab = new JXTable(lgmod);
		ListSelectionModel lsm = lgtab.getSelectionModel();
		lsm.addListSelectionListener(ll);
		lgtab.getColumn(lastcol).setMinWidth(0);
		lgtab.getColumn(lastcol).setMaxWidth(0);
		jscr = JCompTools.getTransparentScrollPane(lgtab);
		jscr.validate();
		pan.add(jscr,cc.xyw(2, 2, 11));
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				lgmod.update(Artikel.artikelListe());
				return null;
			}
		}.execute();
		
		/**Legende**/
		lab = new JXLabel("Artikel-ID");
		pan.add(lab, cc.xy(2, 4));
		lab = new JXLabel("Beschreibung");
		pan.add(lab, cc.xy(4, 4));
		lab = new JXLabel("Einheit");
		pan.add(lab, cc.xy(6, 4));
		lab = new JXLabel("Preis");
		pan.add(lab, cc.xy(8, 4));
		lab = new JXLabel("MwSt.");
		pan.add(lab, cc.xy(10, 4));
		lab = new JXLabel("Lager");
		pan.add(lab, cc.xy(12, 4));
		lab = new JXLabel("Lieferant");
		pan.add(lab, cc.xy(4, 7));
		lab = new JXLabel("Einkaufspreis");
		pan.add(lab, cc.xy(8, 7));
		
		/**5 Edits, 1 Combo, 3 Buts **/
		pan.add( (edits[0] = new JRtaTextField("ZAHLEN",true)),cc.xy(2,5)); // artikelID
		pan.add( (edits[1] = new JRtaTextField("nix",true)),cc.xy(4,5)); // beschreibung
		
		INIFile settings = new INIFile(Reha.proghome +"ini/"+ Reha.aktIK +"/verkauf.ini");
		String[] einheiten = new String[settings.getIntegerProperty("Einheiten", "AnzahlEinheiten")];
		for(int i = 0; i < einheiten.length; i++) {
			einheiten[i] = settings.getStringProperty("Einheiten", "Einheit"+(i+1));
		}
		combo[1] = new JComboBox(einheiten);
		pan.add(combo[1], cc.xy(6, 5));
		
		pan.add( (edits[2] = new JRtaTextField("FL",true,"6.2","RECHTS")),cc.xy(8,5)); // preis
		
		Integer[] mwstSaetze = {0, 7, 19};
		combo[0] = new JComboBox(mwstSaetze);
		
		pan.add(combo[0],cc.xy(10,5)); // mwst - ausklappmenü?
		pan.add( (edits[4] = new JRtaTextField("FL",true,"6.2","RECHTS")),cc.xy(12,5));
		
		
		lieferant = new JComboBox(Lieferant.liefereLieferanten());
		pan.add(lieferant, cc.xyw(4,8,3));
		
		pan.add( (einkaufspreis = new JRtaTextField("FL",true,"6.2","RECHTS")),cc.xy(8, 8)); // preis
		
		pan.add((lieferantEdit = new JXButton("L. bearbeiten")), cc.xy(10, 8)); 
		lieferantEdit.setActionCommand("lieferantEdit");
		lieferantEdit.addActionListener(al);
		pan.add((lieferantNeu = new JXButton("L. neu")), cc.xy(12, 8));
		lieferantNeu.setActionCommand("lieferantNeu");
		lieferantNeu.addActionListener(al);
		
		pan.add( (buts[0] = ButtonTools.macheBut("speichern", "speicher", al)),cc.xy(8,10));
		pan.add( (buts[1] = ButtonTools.macheBut("löschen", "loesche", al)),cc.xy(10,10));
		pan.add( (buts[2] = ButtonTools.macheBut("neu", "neu", al)),cc.xy(12,10));
		
		pan.validate();
		return pan;
	}
	private Point holePosition(boolean neu){
		Point point = null;
		JButton but = (neu ? lieferantNeu : lieferantEdit);
		point = but.getLocationOnScreen();
		point.x = point.x+but.getWidth()-150;
		point.y = point.y-300;
		return point;
	}

	private LagerGUI getInstance(){
		return this;
	}
	private void leereFelder() {
		edits[0].setText("");
		edits[1].setText("");
		edits[2].setText("");
		edits[4].setText("");
		einkaufspreis.setText("");
		combo[0].setSelectedIndex(0);
		combo[1].setSelectedIndex(0);
		edits[0].requestFocus();
	}
	
	private void activateListener(){
		al = new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				
				if(cmd.equals("neu")) {
					leereFelder();
					aktuellerArtikel = null;
				} else if(cmd.equals("speicher")) {
					String dummyparse = "";
					if(aktuellerArtikel == null) {
						dummyparse = (edits[0].getText().equals("") ? "0" : edits[0].getText());
						long ean = Long.parseLong(dummyparse);
						if(!Artikel.artikelExistiert(ean)) {
							String beschreibung = edits[1].getText();
							String einheit = (String) combo[1].getSelectedItem();
							dummyparse = (edits[2].getText().equals("") ? "0.00" : edits[2].getText().replace(",", "."));
							double preis = Double.parseDouble(dummyparse);
							double mwst = (Integer) combo[0].getSelectedItem();
							dummyparse = (edits[4].getText().equals("") ? "0.00" : edits[4].getText().replace(",", "."));
							double lagerstand = Double.parseDouble(dummyparse);
							dummyparse = (einkaufspreis.getText().equals("") ? "0.00" : einkaufspreis.getText().replace(",", "."));
							double ek = Double.parseDouble(dummyparse);
							if(lieferant.getItemCount() <= 0){
								JOptionPane.showMessageDialog(getInstance(), "Bitte legen Sie zuerst einen Lieferanten an.\n\nLieferant können auch Sie selbst sein!");
								return;
							}
							int lieferantenID = ((Lieferant) lieferant.getSelectedItem()).getID();
							
							
							new Artikel(ean, beschreibung, einheit, preis, mwst, lagerstand, ek, lieferantenID);
							aktuellerArtikel =  null;
							lgmod.update(Artikel.artikelListe());
							leereFelder();
						} else {
							JOptionPane.showMessageDialog(null, "Den Artikel gibt es in deiner Datenbank schon!");
						}
					} else {
						int option = JOptionPane.showConfirmDialog(null, "Es wird der Artikel " + aktuellerArtikel.getBeschreibung() +" überschrieben!");
						if( option == JOptionPane.YES_OPTION) {
							dummyparse = (edits[0].getText().equals("") ? "0" : edits[0].getText());
							aktuellerArtikel.setEan(Long.parseLong(dummyparse));
							aktuellerArtikel.setBeschreibung(edits[1].getText());
							dummyparse = (edits[2].getText().equals("") ? "0.00" : edits[2].getText().replace(",", "."));
							aktuellerArtikel.setPreis(Double.parseDouble(dummyparse));
							aktuellerArtikel.setMwst((Integer) combo[0].getSelectedItem());
							aktuellerArtikel.setEinheit((String) combo[1].getSelectedItem());
							dummyparse = (edits[4].getText().equals("") ? "0.00" : edits[4].getText().replace(",", "."));
							aktuellerArtikel.setLagerstand(Double.parseDouble(dummyparse));
							dummyparse = (einkaufspreis.getText().equals("") ? "0.00" : einkaufspreis.getText().replace(",", "."));
							aktuellerArtikel.setEinkaufspreis(Double.parseDouble(dummyparse));
							if(lieferant.getItemCount() <= 0){
								aktuellerArtikel.setLieferant(-1);
							}else{
								aktuellerArtikel.setLieferant(((Lieferant) lieferant.getSelectedItem()).getID());
							}
							lgmod.update(Artikel.artikelListe());
							leereFelder();
						}
					}
				} else if(cmd.equals("loesche")) {
					if(aktuellerArtikel != null) {
						aktuellerArtikel.löscheArtikel();
						aktuellerArtikel = null;
						lgmod.update(Artikel.artikelListe());
						leereFelder();
					}
				} else if(cmd.equals("lieferantEdit")) {
					if(lgtab.getSelectedRow() < 0){return;}
					doLieferantDialog(((Lieferant)lieferant.getSelectedItem()).getID(),holePosition(false));
					/*
					Point position = getLocation();
					Dimension dim = getSize();
					position.setLocation((position.getX() + (dim.getWidth() / 2)), position.getY());
					new LieferantDialog(((Lieferant)lieferant.getSelectedItem()).getID(), position);
					*/
					DefaultComboBoxModel model = new DefaultComboBoxModel(Lieferant.liefereLieferanten());
					lieferant.setModel(model);
					if(aktuellerArtikel != null) {
						lieferant.setSelectedItem(new Lieferant(aktuellerArtikel.getLieferant()));
					}
				} else if(cmd.equals("lieferantNeu")) {
					doLieferantDialog(-1,holePosition(true));
					/*
					Point position = getLocation();
					Dimension dim = getSize();
					position.setLocation((position.getX() + (dim.getWidth() / 2)), position.getY());
					new LieferantDialog(-1, position);
					*/
					DefaultComboBoxModel model = new DefaultComboBoxModel(Lieferant.liefereLieferanten());
					lieferant.setModel(model);
				}
				
				
				selectRow(lastSelected);
				
			}
		};
		
		ll = new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				
				if(lgtab.getSelectedRow() != -1) {
					lastSelected = lgtab.getSelectedRow();
					DecimalFormat df = new DecimalFormat("0.00");
					aktuellerArtikel = new Artikel(Long.parseLong((String) lgmod.getValueAt(lgtab.getSelectedRow(), 0)));
					edits[0].setText(String.valueOf(aktuellerArtikel.getEan()));
					edits[1].setText(aktuellerArtikel.getBeschreibung());
					edits[2].setText(df.format(aktuellerArtikel.getPreis()));
					combo[0].setSelectedItem((int)aktuellerArtikel.getMwst());
					combo[1].setSelectedItem(aktuellerArtikel.getEinheit());
					edits[4].setText(df.format(aktuellerArtikel.getLagerstand()));
					einkaufspreis.setText(df.format(aktuellerArtikel.getEinkaufspreis()));
					lieferant.setSelectedItem(new Lieferant(aktuellerArtikel.getLieferant()));
				}
			}	
			
		};
	}
	private void doLieferantDialog(int id,Point pt){
		lfdlg = new LieferantDialog(id,pt);
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				lfdlg.setzeFocus();		
			}
		});
		lfdlg.setVisible(true);
		lfdlg = null;
	}
	private void selectRow(int n) {
		if(n != -1 && n < lgtab.getRowCount()) {
			lgtab.setRowSelectionInterval(n, n);
		}
	}
}
