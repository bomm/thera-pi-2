package offenePosten;


import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import rechteTools.Rechte;

import Tools.ButtonTools;
import Tools.DatFunk;
import Tools.JCompTools;
import Tools.JRtaComboBox;
import Tools.JRtaTextField;
import Tools.SqlInfo;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class OffenepostenPanel extends JXPanel implements TableModelListener{

	// LEMMI 20101220: Leicht anpassbare Numerierung für die Offene-Posten-Spalten
	// ACHTUNG bei Änderungen: 
	// Die Reihenfolge der Spalten muß in der Tabelle "rliste",  H I E R  und in der Routine starteSuche() geändert werden. Sonst nirgends mehr!
	static int i = 0;
	public enum  eSpalte { cX_NUMMER(i++), cR_NUMMER(i++),  
						   cR_DATUM(i++), cR_KASSE(i++), cR_NAME(i++), cR_KLASSE(i++), cR_BETRAG(i++), cR_OFFEN(i++),
						   cR_BEZDATUM(i++), cR_STORNODAT(i++), cR_ZUZAHL(i++),
						   cMAHNDAT1(i++), cMAHNDAT2(i++), cMAHNDAT3(i++), cMAHNSPERR(i++),
						   cPATINTERN(i++), cIKKTRAEGER(i++), cID(i++),       cOUTOFRANGE(i++);  

		public int iValue;  // Member-Variable
	
		private eSpalte(int i) {  // Konstruktor
			iValue = i;
		}

		public int value() {  // Wert auslesen
			return iValue;		
		}
		// Wandelt eine übergebene Spalten-Nummer 0..n in den sprechenden enum-Wert um
		public static eSpalte intToSpalte(int j) {
			for (eSpalte eS : eSpalte.values()) {
				if ( j == eS.iValue ) return eS;
			}
			return cOUTOFRANGE;  // dann ist es ein Fehler und die gesuchte Spalte gibt es nicht!
		}

	}  // end enum eSpalte
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7883557713071422132L;

	JRtaTextField suchen = null;
	JRtaTextField offen = null;
	JRtaTextField[] tfs = {null,null,null,null};
	JButton[] buts = {null,null,null};
	JRtaComboBox combo = null;
	JXPanel content = null;
	KeyListener kl = null;
	ActionListener al = null;
	
	MyOffenePostenTableModel tabmod = null;
	JXTable tab = null;
	JLabel summeOffen;
	JLabel summeRechnung;
	JLabel summeGesamtOffen;
	JLabel anzahlSaetze;
	
	BigDecimal gesamtOffen = BigDecimal.valueOf(Double.parseDouble("0.00"));
	BigDecimal suchOffen = BigDecimal.valueOf(Double.parseDouble("0.00"));
	BigDecimal suchGesamt = BigDecimal.valueOf(Double.parseDouble("0.00"));
	DecimalFormat dcf = new DecimalFormat("###0.00");
	int gefunden;
	OffenepostenTab eltern = null;
	public OffenepostenPanel(OffenepostenTab xeltern){
		super();
		this.eltern = xeltern;
		startKeyListener();
		startActionListener();
		setLayout(new BorderLayout());
		add(getContent(),BorderLayout.CENTER);
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				setzeFocus();
			}
		});

	}
	public void setzeFocus(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				suchen.requestFocus();
			}
		});
	}
	private Object[] nonsens(){
		Object[] retobj = {null,null,null};
		retobj[0]= (String) "nix";
		retobj[1] = -1;
		retobj[2] = new Vector<String>();
		retobj[0].getClass();
		return retobj;
		
	}
	private JXPanel getContent(){
		content = new JXPanel();
		//				 1     2     3    4     5     6      7    8      9    10    11   12    13   14     15    16    17
		String xwerte = "10dlu,50dlu,2dlu,90dlu,10dlu,30dlu,1dlu,50dlu:g,5dlu,50dlu,2dlu,40dlu,2dlu,35dlu,10dlu,50dlu,10dlu";
		//				 1 2  3     4       5    6      7
//		String ywerte = "p,p,2dlu,150dlu:g,5dlu,80dlu,0dlu";
		//				 1    2  3     4                5    6      7
		String ywerte = "10dlu,p,2dlu,fill:150:grow(1.0),5dlu,50dlu,0dlu";
		
		
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		content.setLayout(lay);
		
		// Lemmi Doku: CombBox zur Auswahl des Suchkriteriums
		JLabel lab = new JLabel("Suchkriterium");
		content.add(lab,cc.xy(2,2));
		String[] args = {"Rechnungsnummer =","Rechnungsnummer >","Rechnungsnummer <",
				"Rechnungsbetrag =","Rechnungsbetrag >","Rechnungsbetrag <",
				"Noch offen =","Noch offen >","Noch offen <",
				"R_Kasse =","R_Kasse enthält",
				"R_Name =","R_Name enthält",
				"Rechnungsdatum =","Rechnungsdatum >","Rechnungsdatum <",
				// Lemmi 20101220 Suchmöglichkeit für Storno-Datum eingebaut
				"Nur stornierte" };
		combo = new JRtaComboBox(args);
		content.add(combo,cc.xy(4,2));
		// Lemmi 20101220 ToolTip Text eingebaut
		combo.setToolTipText("Auswahl einer Scuhmethode.");
		
		// Lemmi Doku: Das Eingabefeld für den Suchwert
		lab = new JLabel("finde:");
		content.add(lab,cc.xy(6,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		suchen = new JRtaTextField("nix",true);
		suchen.setName("suchen");
		suchen.addKeyListener(kl);
		content.add(suchen,cc.xy(8,2));

		
		// Lemmi Doku: Knöpfe für Ausbuchen und Storno
		content.add((buts[0] = ButtonTools.macheButton("ausbuchen", "ausbuchen", al)),cc.xy(10,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		buts[0].setMnemonic('a');
		
		// Lemmi 20101220: Neuer Knopf zum Storno, sowie Prompt & Eingabefeld für den "Offen"-Betrag.
		content.add((buts[1] = ButtonTools.macheButton("storno", "storno", al)),cc.xy(16,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		buts[1].setMnemonic('b');
		
		lab = new JLabel("noch offen:");
		content.add(lab,cc.xy(12,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tfs[0] = new JRtaTextField("F",true,"6.2","");
		tfs[0].setHorizontalAlignment(SwingConstants.RIGHT);
		tfs[0].setText("0,00");
		tfs[0].setName("offen");
		tfs[0].addKeyListener(kl);
		content.add(tfs[0],cc.xy(14,2));
		while(!OffenePosten.DbOk){  // Lemmi Frage: Was ist das für ein unheimlicher Trick????
			
		}
		
		// Lemmi Doku: hier werden die Spalten für die OP-Liste definiert
		tabmod = new MyOffenePostenTableModel();
		Vector<Vector<String>> felder = SqlInfo.holeFelder("describe rliste");
		String[] spalten = new String[felder.size()];
		for(int i= 0; i < felder.size();i++){
			spalten[i] = felder.get(i).get(0);
		}
		tabmod.setColumnIdentifiers(spalten);
		tab = new JXTable(tabmod);
		tab.setHorizontalScrollEnabled(true);
		tab.getColumn(eSpalte.cR_NUMMER.iValue).setCellRenderer(new Tools.MitteRenderer());
		
		//tab.getColumn(1).setCellEditor();
		DateTableCellEditor tble = new DateTableCellEditor();
		// Lemmi 20101220: Umstellung aller festen Indices auf sprechende Parameter
		tab.getColumn(eSpalte.cR_KLASSE.iValue).setCellRenderer(new Tools.MitteRenderer());
		tab.getColumn(eSpalte.cR_BETRAG.iValue).setCellRenderer(new Tools.DoubleTableCellRenderer());
		tab.getColumn(eSpalte.cR_OFFEN.iValue).setCellRenderer(new Tools.DoubleTableCellRenderer());
		tab.getColumn(eSpalte.cR_ZUZAHL.iValue).setCellRenderer(new Tools.DoubleTableCellRenderer());
		tab.getColumn(eSpalte.cR_BETRAG.iValue).setCellEditor(new Tools.DblCellEditor());
		tab.getColumn(eSpalte.cR_OFFEN.iValue).setCellEditor(new Tools.DblCellEditor());
		tab.getColumn(eSpalte.cR_ZUZAHL.iValue).setCellEditor(new Tools.DblCellEditor());
		
		tab.getColumn(eSpalte.cR_DATUM.iValue).setCellEditor(tble);
		tab.getColumn(eSpalte.cR_BEZDATUM.iValue).setCellEditor(tble);
		tab.getColumn(eSpalte.cMAHNDAT1.iValue).setCellEditor(tble);
		tab.getColumn(eSpalte.cMAHNDAT2.iValue).setCellEditor(tble);
		tab.getColumn(eSpalte.cMAHNDAT3.iValue).setCellEditor(tble);

		// Lemmi 20101220: zwei neue Spalten im Spiel:
		tab.getColumn(eSpalte.cX_NUMMER.iValue).setCellRenderer(new Tools.MitteRenderer()); //der hier darf nicht bearbeitet werden !
		tab.getColumn(eSpalte.cR_STORNODAT.iValue).setCellEditor(tble);

		// Lemmi 20101220: Die Spalte R_NUMMER unsichtbar machen
		tab.getColumn(eSpalte.cR_NUMMER.iValue).setMinWidth(0);
		tab.getColumn(eSpalte.cR_NUMMER.iValue).setMaxWidth(0);

		
		tab.getSelectionModel().addListSelectionListener( new OPListSelectionHandler());
		tab.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.CLASSIC_LINE_PRINTER));
		
		
		JScrollPane jscr = JCompTools.getTransparentScrollPane(tab);
		// Lemmi Doku: Hier wird die Daten-Tabelle in das Layout einpositioniert
		content.add(jscr,cc.xyw(2,4,15));
		
		// Lemmi Doku: Die Bilanzwerte am unteren Bildschirm-Rand
		JXPanel auswertung = new JXPanel();
		String xwerte2 = "10dlu,150dlu,5dlu,100dlu";
		String ywerte2 = "1dlu,p,2dlu,p,2dlu,p,2dlu,p,5dlu";			
		FormLayout lay2 = new FormLayout(xwerte2,ywerte2);
		CellConstraints cc2 = new CellConstraints();
		auswertung.setLayout(lay2);
		lab = new JLabel("Offene Posten gesamt:");
		auswertung.add(lab,cc2.xy(2,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		summeGesamtOffen = new JLabel("0,00");
		summeGesamtOffen.setForeground(Color.RED);
		auswertung.add(summeGesamtOffen,cc2.xy(4, 2));

		lab = new JLabel("Offene Posten der letzten Abfrage:");
		auswertung.add(lab,cc2.xy(2,4,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		summeOffen = new JLabel("0,00");
		summeOffen.setForeground(Color.BLUE);
		auswertung.add(summeOffen,cc2.xy(4,4));
		
		lab = new JLabel("Summe Rechnunsbetrag der letzten Abfrage:");
		auswertung.add(lab,cc2.xy(2,6,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		summeRechnung = new JLabel("0,00");
		summeRechnung.setForeground(Color.BLUE);
		auswertung.add(summeRechnung,cc2.xy(4,6));

		lab = new JLabel("Anzahl Datensätze der letzten Abfrage:");
		auswertung.add(lab,cc2.xy(2,8,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		anzahlSaetze = new JLabel("0");
		anzahlSaetze.setForeground(Color.BLUE);
		auswertung.add(anzahlSaetze,cc2.xy(4,8));
		
		content.add(auswertung,cc.xyw(1,6,15));
		content.validate();
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				ermittleGesamtOffen();
				return null;
			}
			
		}.execute();
		
		return content;
	}
	private OffenepostenPanel getInstance(){
		return this;
	}
	
	private void startKeyListener(){
		kl = new KeyListener(){
			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode()==10){
					arg0.consume();
					if( ((JComponent)arg0.getSource()).getName().equals("suchen")){
						sucheEinleiten();
						return;
					}else if( ((JComponent)arg0.getSource()).getName().equals("offen") ){
						setzeFocus();
					}
				}
				
			}
			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		};
	}
	private void startActionListener(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("ausbuchen")){
					tabmod.removeTableModelListener(getInstance());
					doAusbuchen();
					tabmod.addTableModelListener(getInstance());
					setzeFocus();
				}
				// Lemmi 20101220: neues Modul zum STORNO von Rechnungen
				if(cmd.equals("storno")){
					tabmod.removeTableModelListener(getInstance());
					doStorno();
					tabmod.addTableModelListener(getInstance());
					setzeFocus();
				}
			}
			
		};
	}
	private void sucheEinleiten(){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					OffenePosten.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					setzeFocus();
					tabmod.removeTableModelListener(getInstance());
					doSuchen();
					schreibeAbfrage();
					tabmod.addTableModelListener(getInstance());
					suchen.setEnabled(true);
					buts[0].setEnabled(true);
					// Lemmi 20101220 Storno-Button
					buts[1].setEnabled(true);

				}catch(Exception ex){
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null,"Fehler beim einlesen der Datensätze");
					OffenePosten.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					setzeFocus();
					suchen.setEnabled(true);
					buts[0].setEnabled(true);
					// Lemmi 20101220 Storno-Button
					buts[1].setEnabled(true);
				}
				OffenePosten.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				setzeFocus();
				return null;
			}
		}.execute();
	}
	private void doAusbuchen(){
		if(!tfs[0].getText().equals("0,00")){
			int anfrage = JOptionPane.showConfirmDialog(null, "Wollen Sie den Restbetrag von "
					    + tfs[0].getText() + " in der Rechnung speichern", "Benutzeranfrage", JOptionPane.YES_NO_OPTION);
			if( ! (anfrage == JOptionPane.YES_OPTION) ){
				return;
			}
		}
		int row = tab.getSelectedRow();
		if(row < 0){
			JOptionPane.showMessageDialog(null, "Keine Rechnung zum Ausbuchen ausgewählt");
			return;
		}
		//Lemmi 20101220: Sprechende Numerierung der Spalten
//		String offeninTabelle = dcf.format((Double)tabmod.getValueAt(tab.convertRowIndexToModel(row), 6));
		String offeninTabelle = dcf.format((Double)tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cR_OFFEN.iValue));
		if(offeninTabelle.equals("0,00") && tfs[0].getText().equals("0,00")){
			JOptionPane.showMessageDialog(null,"Diese Rechnung ist bereits auf bezahlt gesetzt oder storniert");
			return;
		}
		//Lemmi 20101220: Vernünftige Numerierung der Spalten
		//suchOffen = suchOffen.subtract( BigDecimal.valueOf((Double)tabmod.getValueAt(tab.convertRowIndexToModel(row), 6)) );
		suchOffen = suchOffen.subtract( BigDecimal.valueOf((Double)tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cR_OFFEN.iValue)) );
		suchOffen = suchOffen.add( BigDecimal.valueOf(Double.parseDouble(tfs[0].getText().replace(",", ".")) ) );

		//Lemmi 20101220: Vernünftige Numerierung der Spalten
		//gesamtOffen = gesamtOffen.subtract( BigDecimal.valueOf((Double)tabmod.getValueAt(tab.convertRowIndexToModel(row), 6)) );
		gesamtOffen = gesamtOffen.subtract( BigDecimal.valueOf((Double)tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cR_OFFEN.iValue)) );
		gesamtOffen = gesamtOffen.add( BigDecimal.valueOf(Double.parseDouble(tfs[0].getText().replace(",", ".")) ) );

		
		//Lemmi 20101220: Vernünftige Numerierung der Spalten
		//tabmod.setValueAt(Double.parseDouble(tfs[0].getText().replace(",", ".")), tab.convertRowIndexToModel(row), 6);
		tabmod.setValueAt(Double.parseDouble(tfs[0].getText().replace(",", ".")), tab.convertRowIndexToModel(row), eSpalte.cR_OFFEN.iValue);
		//Lemmi 20101220: Direktes Setzen des Ausbuchungs-Datums in die sichtbare Dialog-Tabelle ergänzt
		tabmod.setValueAt(DatFunk.sHeute(), tab.convertRowIndexToModel(row), eSpalte.cR_BEZDATUM.iValue);

		//Lemmi 20101220: Vernünftige Numerierung der Spalten
		//int id = (Integer) tabmod.getValueAt(tab.convertRowIndexToModel(row), 15);
		int id = (Integer)tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cID.iValue);
		String cmd = "update rliste set r_offen='"+tfs[0].getText().replace(",", ".")+"', r_bezdatum='"+
					 DatFunk.sDatInSQL(DatFunk.sHeute()) + "' where id ='"+Integer.toString(id)+"' LIMIT 1";
		
		if(!OffenePosten.testcase){
			SqlInfo.sqlAusfuehren(cmd);			
		}
		
		//vvv Lemmi 20101220: Im Falle einer RGR muß die Bezahlung beim aktuellen Rezept oder uach in der Historie eingetragen werden
		// das entspricht der RMT-Funktion im Pat-Rez  "if(cmd.equals("statusbezahlt"))"
		String strRechNr = (String)tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cX_NUMMER.iValue);
		String strTabelle = "";
		Vector<Vector<String>> vecDaten;
		
		if ( strRechNr.contains("RGR-")) {  // Dann handelt es sich um eine Rezeptgebühren-Rechnung (RGR)
			// Rezept-Nummer zur RGR-Rechnung holen
			// String strRezNr = (String)tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cIKKTRAEGER.iValue);
			// das ist die sichere Methode:
			vecDaten = SqlInfo.holeFelder( "select reznr from rgaffaktura where rnr = '" + strRechNr + "' LIMIT 1");
			String strRezNr = vecDaten.get(0).get(0);
			
			// Prüfung ab Rezept noch aktuell?
			vecDaten = SqlInfo.holeFelder( "select rez_nr from verordn where rez_nr = '" + strRezNr + "' LIMIT 1");
			if ( !vecDaten.isEmpty() )  // dann ist das Rezept in der aktuellen Liste
				strTabelle = "verordn";
			else {   // prüfen, ob Rezept "historisch"
				vecDaten = SqlInfo.holeFelder( "select rez_nr from lza where rez_nr = '" + strRezNr + "' LIMIT 1");
				if ( !vecDaten.isEmpty() )  // dann ist das Rezept in der Historie
					strTabelle = "lza";
			}
			if ( !strTabelle.isEmpty() ) {
			 	// Update der Tabelle "verordn" oder "lza"
				String strCmd = "update " + strTabelle + " set zzstatus='"+1+"', befr='F',rez_bez='T' where rez_nr='" 
							  + vecDaten.get(0).get(0) + "' LIMIT 1"; 
				if(!OffenePosten.testcase)
					SqlInfo.sqlAusfuehren(strCmd);
			}
		}
		//^^^ Lemmi 20101220: Im Falle eine RGR muß die Bezahlung beim aktuellen Rezept oder uach in der Historie eingetragen werden
		
		schreibeAbfrage();			
		tfs[0].setText("0,00");
	}
	
	// Lemmi 20101220: neues Modul zum STORNO von Rechnungen
	// es wird wie beim Ausbuchen der Offen-Batrag auf 0,00 gesetzt und in der Spalte 
	// Storno-Datum das Datum des Stornovorgangs eingetragen 
	private void doStorno(){
		if(!tfs[0].getText().equals("0,00")){
			int anfrage = JOptionPane.showConfirmDialog(null, "Wollen Sie den Restbetrag von " + tfs[0].getText()
						+ " in der Rechnung speichern", "Benutzeranfrage", JOptionPane.YES_NO_OPTION);
			if( ! (anfrage == JOptionPane.YES_OPTION) ){
				return;
			}
		}
		int row = tab.getSelectedRow();
		if(row < 0){
			JOptionPane.showMessageDialog(null, "Keine Rechnung zum Stornieren ausgewählt");
			return;
		}
		//Lemmi 20101220: Vernünftige Numerierung der Spalten
//		String offeninTabelle = dcf.format((Double)tabmod.getValueAt(tab.convertRowIndexToModel(row), 6));
		String offeninTabelle = dcf.format((Double)tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cR_OFFEN.iValue));
		if(offeninTabelle.equals("0,00") && tfs[0].getText().equals("0,00")){
			JOptionPane.showMessageDialog(null,"Diese Rechnung ist bereits auf bezahlt gesetzt oder storniert");
			return;
		}
		suchOffen = suchOffen.subtract( BigDecimal.valueOf((Double)tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cR_OFFEN.iValue)) );
		suchOffen = suchOffen.add( BigDecimal.valueOf(Double.parseDouble(tfs[0].getText().replace(",", ".")) ) );

		gesamtOffen = gesamtOffen.subtract( BigDecimal.valueOf((Double)tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cR_OFFEN.iValue)) );
		gesamtOffen = gesamtOffen.add( BigDecimal.valueOf(Double.parseDouble(tfs[0].getText().replace(",", ".")) ) );

		
		tabmod.setValueAt(Double.parseDouble(tfs[0].getText().replace(",", ".")), tab.convertRowIndexToModel(row), eSpalte.cR_OFFEN.iValue);
		tabmod.setValueAt(DatFunk.sHeute(), tab.convertRowIndexToModel(row), eSpalte.cR_STORNODAT.iValue);


		int id = (Integer) tabmod.getValueAt(tab.convertRowIndexToModel(row), eSpalte.cID.iValue);
		String cmd = "update rliste set r_offen='" + tfs[0].getText().replace(",", ".") + "', r_stornodat='"
				   + DatFunk.sDatInSQL(DatFunk.sHeute()) + "' where id ='" + Integer.toString(id) + "' LIMIT 1";
 
		if(!OffenePosten.testcase){
			SqlInfo.sqlAusfuehren(cmd);			
		}
		schreibeAbfrage();
		///
		tfs[0].setText("0,00");			
	}

	private void ermittleGesamtOffen(){
		Vector<Vector<String>> offen = SqlInfo.holeFelder("select sum(r_offen) from rliste where r_offen > '0.00'");
		gesamtOffen = BigDecimal.valueOf( Double.parseDouble(offen.get(0).get(0)) );
		schreibeGesamtOffen();
	}
	private void schreibeAbfrage(){
		schreibeGesamtOffen();
		summeOffen.setText(dcf.format(suchOffen));
		summeRechnung.setText(dcf.format(suchGesamt));
		anzahlSaetze.setText(Integer.toString(gefunden));
	}
	private void schreibeGesamtOffen(){
		summeGesamtOffen.setText( dcf.format(gesamtOffen) );
	}
	
	

	// Lemmi Doku: Liste mit den vorgegebenen Suchabfrage-Strukturen
	private void doSuchen(){
		if(suchen.getText().trim().equals("")){
			return;
		}
		int suchart = combo.getSelectedIndex();
		String cmd = "";
		/*
						//  0                     1                  2
		String[] args = {"Rechnungsnummer =","Rechnungsnummer >","Rechnungsnummer <",
				//     3                  4                  5
				"Rechnungsbetrag =","Rechnungsbetrag >","Rechnungsbetrag <",
				//   6               7            8
				"Noch offen =","Noch offen >","Noch offen <",
				//    9                  10
				"Feld-Kasse =","Feld-Kasse enthalten in",
				//    11                  12
				"Feld-Name =","Feld-Name enthalten in",
				//    13                  14                15
				"Rechnungsdatum =","Rechnungsdatum >","Rechnungsdatum <"};
		*/		
		try{
		switch(suchart){
		case 0:
//			cmd = "select * from rliste where r_nummer ='"+suchen.getText().trim()+"'";
			// Lemmi 20101220: Erweitertes Suchkriterium, um auch AFR- und RGR-nn zu finden
//			cmd = "SELECT * FROM rliste WHERE r_nummer LIKE '"+suchen.getText().trim()+"' OR x_nummer LIKE '%"+suchen.getText().trim()+"'";
			cmd = "SELECT * FROM rliste WHERE r_nummer LIKE '"+suchen.getText().trim()+"' OR SUBSTR(x_nummer,5) LIKE '" + suchen.getText().trim()+"'";
			break;
		case 1:
			// Lemmi 20101220: Erweitertes Suchkriterium, um auch AFR- und RGR-nn zu finden
			//cmd = "select * from rliste where r_nummer >'"+suchen.getText().trim()+"'";
			cmd = "select * from rliste where r_nummer >'"+suchen.getText().trim()+"' OR SUBSTR(x_nummer,5) > " + suchen.getText().trim();
			break;
		case 2:
			// Lemmi 20101220: Erweitertes Suchkriterium, um auch AFR- und RGR-nn zu finden
			//cmd = "select * from rliste where r_nummer <'"+suchen.getText().trim()+"'";
			cmd = "select * from rliste where r_nummer <'"+suchen.getText().trim()+"' OR SUBSTR(x_nummer,5) < " + suchen.getText().trim();
			break;
			
			
		case 3:
			cmd = "select * from rliste where r_betrag ='"+suchen.getText().trim().replace(",", ".")+"'";
			break;
		case 4:
			cmd = "select * from rliste where r_betrag >'"+suchen.getText().trim().replace(",", ".")+"'";
			break;
		case 5:
			cmd = "select * from rliste where r_betrag <'"+suchen.getText().trim().replace(",", ".")+"'";
			break;

		case 6:
			cmd = "select * from rliste where r_offen ='"+suchen.getText().trim().replace(",", ".")+"'";
			break;
		case 7:
			cmd = "select * from rliste where r_offen >'"+suchen.getText().trim().replace(",", ".")+"'";
			break;
		case 8:
			cmd = "select * from rliste where r_offen <'"+suchen.getText().trim().replace(",", ".")+"'";
			break;
		
		case 9:
			// Lemmi Frage: Ist das sinnvoll? Viel Tipparbeit und es muß auf's letzte Zeichen passen !
			// Lemmi Fehler: Der Austausch von Komma zu Punkt macht die letzte Hoffnung zunichte !
			//cmd = "select * from rliste where r_kasse ='"+suchen.getText().trim().replace(",", ".")+"'";
			cmd = "select * from rliste where r_kasse = '" + suchen.getText().trim() + "'";  // Lemmi: Besser ist das
			break;
		case 10:
			// Lemmi Fehler: Der Austausch von Komma zu Punkt macht die letzte Hoffnung zunichte !
			//cmd = "select * from rliste where r_kasse like'%"+suchen.getText().trim().replace(",", ".")+"%'";
			cmd = "select * from rliste where r_kasse like '%" + suchen.getText().trim() + "%'";
			break;
		
		case 11:
			// Lemmi Frage: Ist das sinnvoll? Viel Tipparbeit und es muß auf's letzte Zeichen passen!
			// Lemmi Fehler: Der Austausch von Komma zu Punkt macht die letzte Hoffnung zunichte !
			//cmd = "select * from rliste where r_name ='"+suchen.getText().trim().replace(",", ".")+"'";
			cmd = "select * from rliste where r_name = '" + suchen.getText().trim() + "'";
			break;
		case 12:
			// Lemmi Fehler: Der Austausch von Komma zu Punkt macht die letzte Hoffnung zunichte !
			//cmd = "select * from rliste where r_name like'%"+suchen.getText().trim().replace(",", ".")+"%'";
			cmd = "select * from rliste where r_name like '%" + suchen.getText().trim() + "%'";
			break;
		
			
		case 13:
			cmd = "select * from rliste where r_datum ='"+DatFunk.sDatInSQL(suchen.getText().trim())+"'";
			break;
		case 14:
			cmd = "select * from rliste where r_datum >'"+DatFunk.sDatInSQL(suchen.getText().trim())+"'";
			break;
		case 15:
			cmd = "select * from rliste where r_datum <'"+DatFunk.sDatInSQL(suchen.getText().trim())+"'";
			break;

			// Lemmi 20101228: neuer Case mit Storno-Datum
		case 16:
			cmd = "select * from rliste where r_stornodat > 0";
			break;
		}
		
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		if(!cmd.equals("")){
			buts[0].setEnabled(false);
			// Lemmi 20101220 Storno-Button
			buts[1].setEnabled(false);

			suchen.setEnabled(false);
			try{
			starteSuche(cmd);
			}catch(Exception ex){
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, "Korrekte Auflistung des Suchergebnisses fehlgeschlagen");
			}
			OffenePosten.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			suchen.setEnabled(true);
			buts[0].setEnabled(true);
			// Lemmi 20101220: Neuer Knopf zum Storno
			buts[1].setEnabled(true);
			
			setzeFocus();
		}

	}
	
	
	class MyOffenePostenTableModel extends DefaultTableModel{
		   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Class<?> getColumnClass(int columnIndex) {
		//  Lemmi 20101220: Routine auf eParameter umgestellt und vereinfacht 
			
			// Umwandlung des numerischen Index in den Wert einer sprechenden enum-Konstanten
			eSpalte eS = eSpalte.intToSpalte(columnIndex);
	
			switch(eS){
				case cR_NUMMER:
				case cID:
					return Integer.class;

				case cR_DATUM:
				case cR_BEZDATUM:
				case cMAHNDAT1:
				case cMAHNDAT2:
				case cMAHNDAT3:
				case cR_STORNODAT:		// Lemmi 20101220:  neue Spalte an der Liste
					return Date.class;				

				case cR_KASSE:
				case cR_NAME:
				case cR_KLASSE:
				case cPATINTERN:
				case cIKKTRAEGER:
				case cX_NUMMER:		// Lemmi 20101220:  neue Spalte an der Liste
					return String.class;
					
				case cR_BETRAG:
				case cR_OFFEN:
				case cR_ZUZAHL:
					return Double.class;
					
				case cMAHNSPERR:
					return Boolean.class;				
			}  // end switch
			return String.class;
	    }

		public boolean isCellEditable(int row, int col) {
			// Lemmi 20101220: fixe Zahl ersetzt durch sprechenden Parameter
			//if(col < 15){
			if(col < eSpalte.cID.iValue){  // die Spalte "ID" darf nicht mehr geändert werden !
				return true;				
			}
			return false;
		}
		   
	}
	
	// Lemmi Doku: Suche auf der DB ausführen und die Felder in die Tabelle werfen
	private void starteSuche(String sstmt){
		tabmod.setRowCount(0);
		tab.validate();
		tab.repaint();
		Statement stmt = null;
		ResultSet rs = null;
			
		try {
			stmt =  OffenePosten.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			
			rs = stmt.executeQuery(sstmt);
			Vector<Object> vec = new Vector<Object>();
			int durchlauf = 0;
			suchOffen = BigDecimal.valueOf(Double.parseDouble("0.00"));
			suchGesamt = BigDecimal.valueOf(Double.parseDouble("0.00"));
			gefunden = 0;
		
			while(rs.next()){
				vec.clear();

/*				// Lemmi 20101220: den ganzen Block mit den fixen Index-Zahlen auskommentiert
				vec.add(rs.getInt(1)); //r_nummer
				vec.add(rs.getDate(2)); // r_datum
				// Lemmi: DAS GEHT super: vec.add(rs.getDate("r_datum")); // r_datum
				vec.add( (rs.getString(3)==null ? "" : rs.getString(3)) );// r_kasse
				vec.add( (rs.getString(4)==null ? "" : rs.getString(4)) );//r_name
				vec.add( (rs.getString(5)==null ? "" : rs.getString(5)) );//r_klasse
				vec.add(rs.getBigDecimal(6).doubleValue());//r_betrag
				vec.add(rs.getBigDecimal(7).doubleValue());//r_offen
				vec.add(rs.getDate(8)); //r_bezdatum
				vec.add(rs.getBigDecimal(9).doubleValue());//r_zuzahl
				vec.add(rs.getDate(10));//mahndat1
				vec.add(rs.getDate(11));//mahndat2
				vec.add(rs.getDate(12));//mahndat3
				vec.add( (rs.getString(13)==null ?  Boolean.FALSE : (rs.getString(13).equals("T") ? Boolean.TRUE : Boolean.FALSE)) );//mahnsperr
				vec.add( (rs.getString(14)==null ? "" : rs.getString(14)) );//pat_intern
				vec.add( (rs.getString(15)==null ? "" : rs.getString(15)));//ikktraeger
				vec.add(rs.getInt(16));//id
				
				// Lemmi 20101220 TEST Erweiterung: X_NUMMER nach R_NUMMER kopieren, sobald existent
				if ( rs.getString(17) != null )
					vec.set(0,rs.getString(17));
				vec.add(rs.getString(17));//id
				
				// Lemmi 20101220 TEST Einschub Storno-Datum; alle nachfolgenden Indicies inkrekmentiert !
				vec.add(rs.getDate(18)); //r_stornodat
*/
				
				// Lemmi 20101220: Flexible Parameter anstatt fixer Index-Zahlen (zB 6 bzw. 7)
				// Lemmi 20101220 TEST Erweiterung: X_NUMMER nach R_NUMMER kopieren, sobald existent
				vec.add( (rs.getString("x_nummer")==null ? rs.getString("r_nummer") : rs.getString("x_nummer"))); //"x_nummer"
				vec.add(rs.getInt("r_nummer")); //r_nummer
			 	vec.add(rs.getDate("r_datum")); // r_datum
				vec.add( (rs.getString("r_kasse")==null ? "" : rs.getString("r_kasse")) );// r_kasse
				vec.add( (rs.getString("r_name")==null ? "" : rs.getString("r_name")) );//r_name
				vec.add( (rs.getString("r_klasse")==null ? "" : rs.getString("r_klasse")) );//r_klasse
				vec.add(rs.getBigDecimal("r_betrag").doubleValue());//r_betrag
				vec.add(rs.getBigDecimal("r_offen").doubleValue());//r_offen
				vec.add(rs.getDate("r_bezdatum")); //r_bezdatum
				
				// Lemmi 20101220 TEST Einschub Storno-Datum; alle nachfolgenden Indicies inkrekmentiert !
				vec.add(rs.getDate("r_stornodat")); //r_stornodat
				
				vec.add(rs.getBigDecimal("r_zuzahl").doubleValue());//r_zuzahl
				vec.add(rs.getDate("mahndat1"));//mahndat1
				vec.add(rs.getDate("mahndat2"));//mahndat2
				vec.add(rs.getDate("mahndat3"));//mahndat3
				vec.add( (rs.getString("mahnsperr")==null ?  Boolean.FALSE : (rs.getString("mahnsperr").equals("T") ? Boolean.TRUE : Boolean.FALSE)) );//mahnsperr
				vec.add( (rs.getString("pat_intern")==null ? "" : rs.getString("pat_intern")) );//pat_intern
				vec.add( (rs.getString("ikktraeger")==null ? "" : rs.getString("ikktraeger")));//ikktraeger
				vec.add(rs.getInt("id"));//id
				
				
				// Lemmi 20101220: Flexible Parameter anstatt fixer Zahlen (zB 6 bzw. 7)
//				suchOffen = suchOffen.add(rs.getBigDecimal(7));  	// r_offen
//				suchGesamt = suchGesamt.add(rs.getBigDecimal(6));	// r_betrag
				suchOffen = suchOffen.add(rs.getBigDecimal("r_offen"));  		// r_offen
				suchGesamt = suchGesamt.add(rs.getBigDecimal("r_betrag"));	// r_betrag
				
				tabmod.addRow( (Vector<?>) vec.clone());
				if(durchlauf>200){
					try {
						tab.validate();
						tab.repaint();
						Thread.sleep(100);
						durchlauf = 0;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				durchlauf++;
				gefunden++;
			}
			
			tab.validate();
			tab.repaint();
			if(tab.getRowCount() > 0){
				tab.setRowSelectionInterval(0, 0);
			}
			
		}catch(SQLException ev){
			System.out.println("SQLException: " + ev.getMessage());
			System.out.println("SQLState: " + ev.getSQLState());
			System.out.println("VendorError: " + ev.getErrorCode());
		}	
		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { // ignore }
					rs = null;
				}
			}	
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { // ignore }
					stmt = null;
				}
			}
		}
		
		
	}
	/*****************************************************/
	class OPListSelectionHandler implements ListSelectionListener {
		
	    public void valueChanged(ListSelectionEvent e) {
	        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
	        boolean isAdjusting = e.getValueIsAdjusting();
	        if(isAdjusting){
	        	return;
	        }
	        if (lsm.isSelectionEmpty()) {

	        } else {
	            int minIndex = lsm.getMinSelectionIndex();
	            int maxIndex = lsm.getMaxSelectionIndex();
	            for (int i = minIndex; i <= maxIndex; i++) {
	                if (lsm.isSelectedIndex(i)) {
	                	//System.out.println("Satz "+i);
	                    break;
	                }
	            }
	        }

	    }
	}


	@Override
	public void tableChanged(TableModelEvent arg0) {
		if(arg0.getType() == TableModelEvent.INSERT){
			System.out.println("Insert");
			return;
		}
		if(arg0.getType() == TableModelEvent.UPDATE){
			try{
				int col = arg0.getColumn();
				int row = arg0.getFirstRow();
				String colname = tabmod.getColumnName(col).toString();
				String value = "";
				// Lemmi 20101220 keine Zahlen (zB 15), sondern leicht veränderbare Parameter
				//String id = Integer.toString((Integer)tabmod.getValueAt(row,15));
				String id = Integer.toString((Integer)tabmod.getValueAt(row,eSpalte.cID.iValue));
				
				if( tabmod.getColumnClass(col) == Boolean.class){
					value = (tabmod.getValueAt(row,col) == Boolean.FALSE ? "F" : "T");
				}else if(tabmod.getColumnClass(col) == Date.class){
					if(tabmod.getValueAt(row,col)==null ){
						value =  "1900-01-01";
					}else{
						String test = tabmod.getValueAt(row,col).toString();
						if(test.contains(".")){
							value = DatFunk.sDatInSQL(test);
						}else{
							value = test;
						}
					}
					//value = tabmod.getValueAt(row,col).toString();
				}else if(tabmod.getColumnClass(col) == Double.class){
					value = dcf.format((Double)tabmod.getValueAt(row,col)).replace(",",".");
				}else if(tabmod.getColumnClass(col) == Integer.class){
//					value = Integer.toString((Integer)tabmod.getValueAt(row,15));   Lemmi FEHLER: "15" <-- da muß doch sicher "col" stehen !!!
					value = Integer.toString((Integer)tabmod.getValueAt(row,col));  
				}else if(tabmod.getColumnClass(col) == String.class){
					value = tabmod.getValueAt(row,col).toString();
				}
				String cmd = "update rliste set "+colname+"='"+value+"' where id='"+id+"' LIMIT 1";
				//System.out.println(cmd);
				SqlInfo.sqlAusfuehren(cmd);
			
			}catch(Exception ex){
				JOptionPane.showMessageDialog(null,"Fehler in der Dateneingabe");
			}
			
			return;
		}

	}

}
