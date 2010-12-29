package systemEinstellungen;


import org.jdesktop.swingx.JXPanel;
import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.thera_pi.nebraska.gui.utils.ButtonTools;

import sqlTools.SqlInfo;
import systemTools.JRtaComboBox;
import systemTools.JRtaTextField;
import theraPiUpdates.JCompTools;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

// Lemmi 20101228 neue Klasse in der System-Inititalisierung
public class SysUtilOpMahnung extends JXPanel implements KeyListener, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 858117043130060154L;
	JRtaTextField[] tfs = {null,null};
	Integer cANZTFS = 2;  // Anzahl der benutzten Textfelder
	
	JButton abbruch = null;
	JButton speichern = null;
	JButton[] buts = {null,null};
	
	Vector<String> originale = new Vector<String>();
	boolean nummernkreisok = true;


	
	public SysUtilOpMahnung(ImageIcon img){
		super(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(15, 40, 20, 20));
		/****/
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("SystemInit"));
		/****/
		/*
		JScrollPane jscr = JCompTools.getTransparentScrollPane(getVorlagenSeite());
		jscr.validate();
		jscr.getVerticalScrollBar().setUnitIncrement(15);
		add(jscr,BorderLayout.CENTER);
		*/
	    add(getVorlagenSeite(),BorderLayout.CENTER);
	    add(getKnopfPanel(),BorderLayout.SOUTH);
	    new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				//fuelleTextFelder();
				// Merken der Original eingelesenen Textfelder
				SaveChangeStatus();

				return null;
			}
	    	
	    }.execute();
	    validate();
		return;
	}
	/************** Beginn der Methode für die Objekterstellung und -platzierung *********/
/*	private void fuelleTextFelder(){
		
		tfs[0].setText(SystemConfig.hmZusatzInOffenPostenIni.get("RGRinOPverwaltung").toString());
		tfs[1].setText(SystemConfig.hmZusatzInOffenPostenIni.get("AFRinOPverwaltung").toString());

		// Merken der Original eingelesenen Textfelder
		SaveChangeStatus();
	}
*/		
/*		
		//String cmd = "select * from nummern where mandant = '"+Reha.aktIK+"' LIMIT 1";
		String cmd = "select * from nummern LIMIT 1";
		Vector<Vector<String>> vec = SqlInfo.holeFelder(cmd);
		if(vec.size() <= 0){
			JOptionPane.showMessageDialog(null,"Achtung für den aktuellen Mandanten existiert noch kein Nummernkreis");
			nummernkreisok = false;
			return;
		}
*/
	
	// Merken der Originalwerte der eingelesenen Textfelder
	private void SaveChangeStatus(){
		originale.clear();
		for ( int i = 0; i < cANZTFS; i++ ) {
			originale.add( tfs[i].getText() );
		}
		
	}
	
	// prüft, ob sich Einträge geändert haben
	private Boolean HasChanged(){
		
		for ( int i = 0; i < cANZTFS; i++) {
			if(! tfs[i].getText().trim().equals(originale.get(i)))
				return true;
		}
		
		return false;
	}
	
	JRtaComboBox cmbRGR = new JRtaComboBox();
	JRtaComboBox cmbAFR = new JRtaComboBox();
	Vector<Vector<String>>vecJN = new Vector<Vector<String>>();
	
	private JPanel getVorlagenSeite(){
        //                                      1.            2.    3.    4.     5.     6.    7.      8.     9.    10     11
		FormLayout lay = new FormLayout("right:max(60dlu;p), 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu",
       //1.    2. 3. 4.   5.   6.   7.   8.  9.  10.  11. 12. 13.  14.  15. 16.  17. 18.  19.  20.    21.   22.   23.
		"p, 0dlu, p, p,0dlu,p,2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, "+
	   // 21.  22.  23.  24. 25.  26. 27. 28.  29.  30.  31.  32.  33. 34.  35.  36.
		"2dlu, p , 2dlu ,p , 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p,  2dlu,  p, 2dlu, p, 2dlu, p, 2dlu, p ");

		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		//JLabel lab = new JLabel("Nummernkreis von Mandant: "+Reha.aktIK);
		//lab.setFont(new Font("Tahoma",Font.BOLD,12));
		//builder.add(lab,cc.xyw(3, 4, 7));
		Vector<String> vec = new Vector<String>();
		int iAktY = 6;
		
		JLabel lab = new JLabel("<html><b>Rechnungsgebühren- & Ausfall-Rechnung unter Offene Posten und Mahnung handhaben</b></html>");
		builder.add(lab,cc.xyw(1, iAktY, 11));

		iAktY += 2;
		builder.addLabel("RGR in OP u. Mahnung aufnehmen",cc.xy(1,iAktY));
//		builder.addLabel("(0, 1)",cc.xy(5,iAktY));
		tfs[0] = new JRtaTextField("ZAHLEN", true);
		tfs[0].setName("rgr_opm");
		tfs[0].setText(SystemConfig.hmZusatzInOffenPostenIni.get("RGRinOPverwaltung").toString());
//		builder.add(tfs[0],cc.xy(3,iAktY));
		// ComboBox vorbereiten
		Collections.addAll( vec, "Ja",   "1" );
//		vec.addElement("Ja");
	//	vec.addElement("1");
		vecJN.add(0,(Vector<String>) vec.clone());
		vec.clear();  // für die nächste Belegung freimachen
//		vec.addElement("Nein");
//		vec.addElement("0");
		Collections.addAll( vec, "Nein", "0" );
		vecJN.add(1,(Vector<String>) vec.clone());
		//vec_kuerzel.add(1, (Vector<String>)(Arrays.asList( new String[] {"Nein","0"})));
		//Collections.addAll(vec_kuerzel, "a", "b", "c")
		//vec_kuerzel.clear();
		cmbRGR.setDataVectorVector(vecJN, 0, 1);
//		String strTest1 = tfs[0].getText();
//		String strTest2 = (String)cmbRGR.getSelectedItem();
		cmbRGR.setSelectedItem(tfs[0].getText().equals("0") ? "Nein" : "Ja" );  // setze den aktuell gewählten Wert
//		String strTest3 = (String)cmbRGR.getSelectedItem();
		cmbRGR.setActionCommand("cmbRGR");
		cmbRGR.addActionListener(this);
		builder.add(cmbRGR, cc.xy(3,iAktY));

		iAktY += 2;
		builder.addLabel("AFR in OP u. Mahnung aufnehmen",cc.xy(1,iAktY));
//		builder.addLabel("(0, 1)",cc.xy(5,iAktY));
		tfs[1] = new JRtaTextField("ZAHLEN", true);
		tfs[1].setName("afr_opm");
		tfs[1].setText(SystemConfig.hmZusatzInOffenPostenIni.get("AFRinOPverwaltung").toString());
//		builder.add(tfs[1],cc.xy(3,iAktY));
		// ComboBox vorbereiten
//		vec.clear();  // für die nächste Belegung freimachen
//		Collections.addAll( vec, "Ja",   "1" );
//		vec_kuerzel.add(0,(Vector<String>) vec.clone());
//		vec.clear();  // für die nächste Belegung freimachen
//		Collections.addAll( vec, "Nein", "0" );
//		vec_kuerzel.add(1,(Vector<String>) vec.clone());
		cmbAFR.setDataVectorVector(vecJN, 0, 1);
		cmbAFR.setSelectedItem(tfs[1].getText().equals("0") ? "Nein" : "Ja" );  // setze den aktuell gewählten Wert
		cmbAFR.setActionCommand("cmbAFR");
		cmbAFR.addActionListener(this);
		builder.add(cmbAFR, cc.xy(3,iAktY));

		iAktY += 2;
//		builder.addLabel("RGR u. AFR in OPs testen", cc.xy(1, iAktY ));
		buts[0] = ButtonTools.macheBut("RGR & AFR für OPs testen", "testen", this);
		builder.add(buts[0], cc.xy(1,iAktY,CellConstraints.FILL,CellConstraints.DEFAULT));

		iAktY += 2;
		buts[1] = ButtonTools.macheBut("RGR & AFR in OPs übertragen", "uebernehmen", this);
		builder.add(buts[1], cc.xy(1,iAktY,CellConstraints.FILL,CellConstraints.DEFAULT));
	
		
		iAktY += 2;
		builder.addLabel(" ",cc.xyw(1, iAktY, 11));
		//-------------------------------------------------------------------------------
		iAktY += 1;
		builder.addSeparator("", cc.xyw(1,iAktY,11));
		iAktY += 1;
		builder.addLabel(" ",cc.xyw(1, iAktY, 11));
/*
 * 		
		iAktY += 2;
		JLabel lab3 = new JLabel("<html><b>Werkzeug-Dialoge <font color=#0000FF>(Benutzer individulle Einstellung)</font></b></html>");
		builder.add(lab3,cc.xyw(1, iAktY, 11));
		
		iAktY += 2;
		builder.addLabel("Zusatzknopf \"ausführen\" anzeigen",cc.xy(1,iAktY));
//		builder.addLabel("(0, 1)",cc.xy(5,iAktY));
		tfs[3] = new JRtaTextField("Zahlen", true);
		tfs[3].setName("zusatzknopf");
		tfs[3].setText((Boolean)SystemConfig.hmPatientenWerkzeugDlgIni.get("ToolsDlgShowButton") ? "1" : "0");
//		builder.add(tfs[3],cc.xy(3,iAktY));
		// ComboBox vorbereiten
//		vec.clear();  // für die nächste Belegung freimachen
//		Collections.addAll( vec, "Ja",   "1" );
//		vec_kuerzel.add(0,(Vector<String>) vec.clone());
//		vec.clear();  // für die nächste Belegung freimachen
//		Collections.addAll( vec, "Nein", "0" );
//		vec_kuerzel.add(1,(Vector<String>) vec.clone());
		cmbBut.setDataVectorVector(vecJN, 0, 1);
		cmbBut.setSelectedItem(tfs[3].getText().equals("0") ? "Nein" : "Ja" );  // setze den aktuell gewählten Wert
		cmbBut.setActionCommand("cmbBut");
		cmbBut.addActionListener(this);
		builder.add(cmbBut, cc.xy(3,iAktY));

		iAktY += 2;
		builder.addLabel("Anzahl Mausklicks zum Werkzeugstart",cc.xy(1,iAktY));
//		builder.addLabel("(1, 2)",cc.xy(5,iAktY));
		tfs[2] = new JRtaTextField("ZAHLEN", true);
		tfs[2].setName("anzmaus");
		tfs[2].setText(SystemConfig.hmPatientenWerkzeugDlgIni.get("ToolsDlgClickCount").toString());
//		builder.add(tfs[2],cc.xy(3,iAktY));
		// ComboBox vorbereiten
		vec12.clear();
		vec.clear();  // für die nächste Belegung freimachen
		Collections.addAll( vec, "1 Klick",   "1" );
		vec12.add(0,(Vector<String>) vec.clone());
		vec.clear();  // für die nächste Belegung freimachen
		Collections.addAll( vec, "2 Klicks", "2" );
		vec12.add(1,(Vector<String>) vec.clone());
		cmbClk.setDataVectorVector(vec12, 0, 1);
		cmbClk.setSelectedItem(tfs[2].getText().equals("1") ? "1 Klick" : "2 Klicks" );  // setze den aktuell gewählten Wert
		cmbClk.setActionCommand("cmbClk");
		cmbClk.addActionListener(this);
		builder.add(cmbClk, cc.xy(3,iAktY));
		
*/		
		return builder.getPanel();
	}
	
	private JPanel getKnopfPanel(){
		abbruch = new JButton("abbrechen");
		abbruch.setActionCommand("abbrechen");
		abbruch.addActionListener(this);
		speichern = new JButton("speichern");
		speichern.setActionCommand("speichern");
		speichern.addActionListener(this);
		
									//      1.                      2.    3.    4.     5.     6.    7.      8.     9.
		FormLayout jpanlay = new FormLayout("right:max(150dlu;p), 60dlu, 60dlu, 4dlu, 60dlu",
       //1.    2. 3.   4.   5.   6.     7.    8. 9.  10.  11. 12. 13.  14.  15. 16.  17. 18.  19.   20.    21.   22.   23.
		"p, 10dlu, p");
		
		PanelBuilder jpan = new PanelBuilder(jpanlay);
		jpan.getPanel().setOpaque(false);		
		CellConstraints jpancc = new CellConstraints();
		
		jpan.addSeparator("", jpancc.xyw(1,1,5));
		jpan.add(abbruch, jpancc.xy(3,3));
		jpan.add(speichern, jpancc.xy(5,3));
		jpan.addLabel("Änderungen übernehmen?", jpancc.xy(1,3));
		
		jpan.getPanel().validate();
		return jpan.getPanel();
	}


	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		
		if(cmd.equals("testen")){
			doTesten();
			return;
		}
		if(cmd.equals("uebernehmen")){
			doUebernehmen();
			return;
		}
		
		if(cmd.equals("abbrechen")){
			SystemInit.abbrechen();
			return;
		}
		if(cmd.equals("speichern")){
			doSpeichern();
			return;
		}
		
		
		if(cmd.equals("cmbRGR")){
//			System.out.println("cmbRGR.getSecValue() = " + cmbRGR.getSecValue());
			tfs[0].setText((String)cmbRGR.getSecValue());
			return;
		}
		if(cmd.equals("cmbAFR")){
//			System.out.println("cmbAFR.getSecValue() = " + cmbAFR.getSecValue());
			tfs[1].setText((String)cmbAFR.getSecValue());
			return;
		}
	}
	
	// prüft, wieviele RGR und AFR breits in rliste drin sind und wieviele in rgaffakture enthalten sind
	private void doTesten(){
		String strCntRgrRliste = SqlInfo.holeEinzelFeld("select COUNT(*) from rliste where x_nummer like 'RGR-%'");
		String strCntAfrRliste = SqlInfo.holeEinzelFeld("select COUNT(*) from rliste where x_nummer like 'AFR-%'");

		String strCntRgrFaktura = SqlInfo.holeEinzelFeld("select COUNT(*) from rgaffaktura where rnr like 'RGR-%'");
		String strCntAfrFaktura = SqlInfo.holeEinzelFeld("select COUNT(*) from rgaffaktura where rnr like 'AFR-%'");

		JOptionPane.showMessageDialog(null, "Zur OP & Mahnungshadhabung sind bereits übertragen: " + strCntRgrRliste + " RGR und " + strCntAfrRliste + " AFR\n" +
										    "Fakturiert sind insgesamt: " + strCntRgrFaktura + " RGR und " + strCntAfrFaktura + " AFR. " +
										    "\n\nSofern die Anzahl der fakturierten Rechnungen größer ist, können die noch nicht übertragenen mit\ndem nächsten Knopf ebenfalls in die OP u. Mahnungs-Handhabung transportiert werden");
	}
	
	// Lemmi 20101225: Routine zum Umkopieren von rgaffaktura nach rliste
	private void doUebernehmen(){
//		System.out.println("Kopiere Tabelle  rgaffakura nach rliste");  
		
		if ( HasChanged () ) {
			JOptionPane.showMessageDialog(null, "Es wurden Eingaben geändert!\n\nBitte vor Ausführung dieser Aktion erst die Änderungen speichern.",
												"Einstellungen wurden geändert", JOptionPane.WARNING_MESSAGE, null);
			return;
		}
		
		
		// Lemmi 20101220: Routine zum Umkopieren von rgaffaktura nach rliste
//		private void kopiereAlleRgrNachRliste() {
		String cmd = "select * from rgaffaktura";
		String strHelp = "", strKopiert = "K E I N E", strExistiert = "K E I N E";
		//System.out.println(cmd);
		Vector<Vector<String>> vec2 = SqlInfo.holeFelder(cmd);
		int iCntExistiert = 0, iCntKopiert = 0;
		
//		int iSize2 = vec2.size();
		
		for ( int z = 0; z < vec2.size(); z++ ){  // Datensatz für Datensatz

			// prüfen, ob diese RGR oder AFR bereits in rliste eingetragen ist
			cmd = "select x_nummer from rliste where x_nummer='" + vec2.get(z).get(0) + "'";
			Vector<Vector<String>> vec = SqlInfo.holeFelder(cmd);
//			strHelp = vec.get(0).get(0);

			int iSize = vec.size();
			Boolean bTest = vec2.get(z).get(0).contains("RGR-");
			Boolean bHm = SystemConfig.hmZusatzInOffenPostenIni.get("RGRinOPverwaltung") == 1;
			
			if(    vec.size() <= 0    // nur wenn es den Datensatz in rliste noch NICHT gibt
				&& (   (vec2.get(z).get(0).contains("RGR-") && SystemConfig.hmZusatzInOffenPostenIni.get("RGRinOPverwaltung") == 1)  // RGR zugelassen ?
					|| (vec2.get(z).get(0).contains("AFR-") && SystemConfig.hmZusatzInOffenPostenIni.get("AFRinOPverwaltung") == 1)  // AFR zugelassen ?
				   )
			  ) {
				StringBuffer buf2 = new StringBuffer();
				buf2.append("insert into rliste set ");
				buf2.append("r_nummer='0', ");
				buf2.append("x_nummer='" + vec2.get(z).get(0) + "', ");
				buf2.append("r_datum='" + vec2.get(z).get(7) + "', ");
		
				// Patienten-Name, Vorname holen und eintragen
				cmd = "select n_name, v_name from pat5 where id='" + vec2.get(z).get(2) + "'";
				vec.clear();
				vec = SqlInfo.holeFelder(cmd);
				if(vec.size() <= 0) strHelp = "Patient, unbekannt";
				else 				strHelp = vec.get(0).get(0) + ", " + vec.get(0).get(1);  // N_name, V_name
				buf2.append("r_kasse='" + strHelp + "', ");
				
				strHelp = vec2.get(z).get(1);
				buf2.append("r_klasse='" + strHelp.substring(0, 2) + "', ");  // Hole die ersten beiden Buchstaben aus der Rezeptnummer als "Klasse"
				
				buf2.append("r_betrag='" + vec2.get(z).get(3) + "', ");
				buf2.append("r_offen='" + vec2.get(z).get(4) + "', ");
				buf2.append("r_zuzahl='0.00', ");		
				buf2.append("pat_intern='" + vec2.get(z).get(2) + "', ");
				buf2.append("ikktraeger='" + vec2.get(z).get(1) + "' ");  // Rezept-Nummer ER23
				SqlInfo.sqlAusfuehren(buf2.toString());		

				if ( strKopiert.equals("K E I N E") )  // Init-Wert löschen
					strKopiert = "";
//				System.out.println("Datensatz " + vec2.get(z).get(0) + " kopiert");
				else if ( iCntKopiert % 10 == 0 )
					strKopiert += "<br>";
				else if ( iCntKopiert > 0 )
					strKopiert += ", ";
				strKopiert += vec2.get(z).get(0) + " ";
				iCntKopiert++;

			} else {
				if ( strExistiert.equals("K E I N E") )	// Init-Wert löschen
					strExistiert = "";
//				System.out.println("Datensatz " + vec2.get(z).get(0) + " existiert bereits");
				else if ( iCntExistiert % 10 == 0 )
					strExistiert += ",<br>";
				else if ( iCntExistiert > 0 )
					strExistiert += ", ";
				strExistiert += vec2.get(z).get(0);
				iCntExistiert++;
				
			}
		
		}
	
		JOptionPane.showMessageDialog(null, "<html>Folgende Rechnungs-Datensätze wurden kopiert:<br><font color=#0000FF>" + strKopiert + "</font><br><br>" + 
											"Folgende Rechnungs-Datensätze haben bereits exisitiert oder wurden nicht zur Übernahme ausgewählt<br>und wurden deshalb NICHT kopiert:<br><font color=#FF0000>" 
											+ strExistiert + "</font></html>");

	}

	
	private void doSpeichern(){
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/offeneposten.ini");
	
		SystemConfig.hmZusatzInOffenPostenIni.put("RGRinOPverwaltung", Integer.parseInt(tfs[0].getText()) );
		SystemConfig.hmZusatzInOffenPostenIni.put("AFRinOPverwaltung", Integer.parseInt(tfs[1].getText()) );

		inif.setIntegerProperty("ZusaetzlicheRechnungen", "RGRinOPverwaltung", (Integer)SystemConfig.hmZusatzInOffenPostenIni.get("RGRinOPverwaltung"), " RFR in OP-Verwaltung" );
		inif.setIntegerProperty("ZusaetzlicheRechnungen", "AFRinOPverwaltung", (Integer)SystemConfig.hmZusatzInOffenPostenIni.get("AFRinOPverwaltung"), " AFR in OP-Verwaltung" );

		// Die sysetmspezifischen Daten müssen eigentlich in die Tabelle 'ini' weggespeichert werden
		
		inif.save();  // Daten wegschreiben
		
		// Merken der aktuell vorhandenen Textfelder
		SaveChangeStatus();

		
/*		
		String meldung = "Folgende Nummern wurden geändert\n";
		String cmd = (!nummernkreisok ? "insert into nummern set " : "update nummern set ") ;
		int edited = 0;
		for(int i = 0; i < 15; i++){
			if(! tfs[i].getText().trim().equals(originale.get(i))){
				edited++;
				cmd = cmd+(edited > 1 ? "," : "")+tfs[i].getName()+"='"+tfs[i].getText().trim()+"'";
				meldung = meldung+tfs[i].getName()+" = "+tfs[i].getText().trim()+"\n";
			}
		}
		if(edited > 0){
			cmd = cmd+ " LIMIT 1";
			//cmd = cmd+(!nummernkreisok ? "" : " where mandant = '"+Reha.aktIK+"' LIMIT 1");
			meldung = meldung+"\n\n"+"Diese Nummern abspeichern?"+"\n";
			int frage = JOptionPane.showConfirmDialog(null, meldung, "Die geänderten Nummern abspeichern", JOptionPane.YES_NO_OPTION);
			if(frage == JOptionPane.YES_OPTION){
				SqlInfo.sqlAusfuehren(cmd);
			}else{
				for(int i = 0; i < 15; i++){
					tfs[i].setText(originale.get(i));
				}
			}
		}else{
			JOptionPane.showMessageDialog(null,"Nummernkreis wurde nicht verändert (gute Entscheidung!!)");
			SystemInit.abbrechen();
		}
*/	
	}
}
