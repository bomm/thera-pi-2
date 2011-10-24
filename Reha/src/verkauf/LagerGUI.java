package verkauf;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
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

import rehaInternalFrame.JVerkaufInternal;
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
	MyLagerTableModel lgmod = new MyLagerTableModel();
	JXTable lgtab = null;
	JScrollPane jscr = null;
	int lastcol = 5;
	public JVerkaufInternal eltern;
	
	Artikel aktuellerArtikel = null;

	public LagerGUI(JVerkaufInternal vki) {
		super();
		eltern = vki;
		this.activateListener();
		this.setOpaque(false);
		this.setLayout(new BorderLayout());
		this.add(getContent(), BorderLayout.CENTER);
	}
	
	private JXPanel getContent() {
		JXPanel pan = new JXPanel();
		JXLabel lab = new JXLabel();
		 //                1     2      3     4        5     6      7     8      9    10     11 
		String xwerte = "5dlu, 60dlu, 5dlu, 120dlu:g, 5dlu, 60dlu, 5dlu, 60dlu, 5dlu, 60dlu, 5dlu";
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
		pan.add(jscr,cc.xyw(2, 2, 9));
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
		lab = new JXLabel("Preis");
		pan.add(lab, cc.xy(6, 4));
		lab = new JXLabel("MwSt.");
		pan.add(lab, cc.xy(8, 4));
		lab = new JXLabel("Lager");
		pan.add(lab, cc.xy(10, 4));
		
		/**5 Edits, 2 Buts **/
		pan.add( (edits[0] = new JRtaTextField("ZAHLEN",true)),cc.xy(2,5)); // artikelID
		pan.add( (edits[1] = new JRtaTextField("nix",true)),cc.xy(4,5)); // beschreibung
		pan.add( (edits[2] = new JRtaTextField("FL",true,"6.2","RECHTS")),cc.xy(6,5)); // preis
		pan.add( (edits[3] = new JRtaTextField("ZAHLEN",true)),cc.xy(8,5)); // mwst - ausklappmenü?
		pan.add( (edits[4] = new JRtaTextField("ZAHLEN",true)),cc.xy(10,5));
		pan.add( (buts[0] = ButtonTools.macheBut("speichern", "speicher", al)),cc.xy(6,7));
		pan.add( (buts[1] = ButtonTools.macheBut("löschen", "loesche", al)),cc.xy(8,7));
		pan.add( (buts[2] = ButtonTools.macheBut("neu", "neu", al)),cc.xy(10,7));
		
		pan.validate();
		return pan;
	}
	
	private void leereFelder() {
		edits[0].setText("");
		edits[1].setText("");
		edits[2].setText("");
		edits[3].setText("");
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
							double preis = Double.parseDouble(edits[2].getText().replace(",", "."));
							double mwst = Double.parseDouble(edits[3].getText());
							int lagerstand = Integer.parseInt(edits[4].getText());
							aktuellerArtikel = new Artikel(ean, beschreibung, preis, mwst, lagerstand);
							lgmod.update(Artikel.artikelListe());
							leereFelder();
						} else {
							JOptionPane.showMessageDialog(null, "Den Artikel gibt es in deiner Datenbank schon!");
						}
					} else {
						aktuellerArtikel.setEan(Long.parseLong(edits[0].getText()));
						aktuellerArtikel.setBeschreibung(edits[1].getText());
						aktuellerArtikel.setPreis(Double.parseDouble(edits[2].getText().replace(",", ".")));
						aktuellerArtikel.setMwst(Double.parseDouble((edits[3].getText())));
						aktuellerArtikel.setLagerstand(Integer.parseInt((edits[4].getText())));
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
				
			}
		};
		
		ll = new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if(lgtab.getSelectedRow() != -1) {
					DecimalFormat df = new DecimalFormat("0.00");
					DecimalFormat df2 = new DecimalFormat("0");
					aktuellerArtikel = new Artikel(Long.parseLong((String) lgmod.getValueAt(lgtab.getSelectedRow(), 0)));
					edits[0].setText(String.valueOf(aktuellerArtikel.getEan()));
					edits[1].setText(aktuellerArtikel.getBeschreibung());
					edits[2].setText(df.format(aktuellerArtikel.getPreis()));
					edits[3].setText(df2.format(aktuellerArtikel.getMwst()));
					edits[4].setText(String.valueOf(aktuellerArtikel.getLagerstand()));
				}
			}	
			
		};
	}
}
