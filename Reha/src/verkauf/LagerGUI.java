package verkauf;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.thera_pi.nebraska.gui.utils.ButtonTools;
import org.thera_pi.nebraska.gui.utils.JCompTools;

import systemEinstellungen.INIFile;
import systemTools.JRtaTextField;
import verkauf.model.Artikel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class LagerGUI extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ActionListener al = null;
	ListSelectionListener ll = null;
	JRtaTextField[] edits = {null, null, null, null,null};
	JButton[] buts = {null, null, null};
	JComboBox[] combo = {null, null};
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
	
	private JXPanel getContent() {
		JXPanel pan = new JXPanel();
		JXLabel lab = new JXLabel();
		 //                1     2      3     4        5     6      7     8      9    10     11    12      13
		String xwerte = "5dlu, 60dlu, 5dlu, 120dlu:g, 5dlu, 60dlu, 5dlu, 60dlu, 5dlu, 60dlu, 5dlu, 60dlu, 5dlu";
		//					1		2		3  4  5   6    7   8
		String ywerte = "5dlu, 160dlu:g, 5dlu, p, p, 5dlu, p, 5dlu";
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
		pan.add( (buts[0] = ButtonTools.macheBut("speichern", "speicher", al)),cc.xy(8,7));
		pan.add( (buts[1] = ButtonTools.macheBut("löschen", "loesche", al)),cc.xy(10,7));
		pan.add( (buts[2] = ButtonTools.macheBut("neu", "neu", al)),cc.xy(12,7));
		
		pan.validate();
		return pan;
	}
	
	private void leereFelder() {
		edits[0].setText("");
		edits[1].setText("");
		edits[2].setText("");
		edits[4].setText("");
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
					if(aktuellerArtikel == null) {
						long ean = Long.parseLong(edits[0].getText());
						if(!Artikel.artikelExistiert(ean)) {
							String beschreibung = edits[1].getText();
							String einheit = (String) combo[1].getSelectedItem();
							double preis = Double.parseDouble(edits[2].getText().replace(",", "."));
							double mwst = (Integer) combo[0].getSelectedItem();
							double lagerstand = Double.parseDouble(edits[4].getText().replace(",", "."));
							aktuellerArtikel = new Artikel(ean, beschreibung, einheit, preis, mwst, lagerstand);
							lgmod.update(Artikel.artikelListe());
							leereFelder();
						} else {
							JOptionPane.showMessageDialog(null, "Den Artikel gibt es in deiner Datenbank schon!");
						}
					} else {
						aktuellerArtikel.setEan(Long.parseLong(edits[0].getText()));
						aktuellerArtikel.setBeschreibung(edits[1].getText());
						aktuellerArtikel.setPreis(Double.parseDouble(edits[2].getText().replace(",", ".")));
						aktuellerArtikel.setMwst((Integer) combo[0].getSelectedItem());
						aktuellerArtikel.setEinheit((String) combo[1].getSelectedItem());
						aktuellerArtikel.setLagerstand(Double.parseDouble((edits[4].getText().replace(",", "."))));
						lgmod.update(Artikel.artikelListe());
						leereFelder();
					}
				} else if(cmd.equals("loesche")) {
					if(aktuellerArtikel != null) {
						aktuellerArtikel.löscheArtikel();
						aktuellerArtikel = null;
						lgmod.update(Artikel.artikelListe());
						leereFelder();
					}
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
				}
			}	
			
		};
	}
	
	private void selectRow(int n) {
		if(lgmod.getRowCount() < n) {
			n = lgmod.getRowCount() - 1;
		}
		lgtab.setRowSelectionInterval(n, n);
	}
}
