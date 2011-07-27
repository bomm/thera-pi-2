package systemEinstellungen;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collections;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;

import systemTools.JRtaComboBox;
import systemTools.JRtaTextField;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

// Lemmi 20101225 neue Klasse in der System-Inititalisierung
public class SysUtilBedienung extends JXPanel implements KeyListener, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 858117043130060154L;
	JRtaTextField[] tfs = {null,null,null,null,null,null};
	JButton abbruch = null;
	JButton speichern = null;
	JButton[] buts = {null,null};
	boolean neustartRequire = false;
	Vector<String> originale = new Vector<String>();
	boolean nummernkreisok = true;


	
	public SysUtilBedienung(ImageIcon img){
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
		
		tfs[0].setText((Boolean)SystemConfig.hmPatientenWerkzeugDlgIni.get("ToolsDlgShowButton") ? "1" : "0");
		tfs[1].setText(SystemConfig.hmPatientenWerkzeugDlgIni.get("ToolsDlgClickCount").toString());
		
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
		for ( int i = 0; i < tfs.length; i++ ) {
			originale.add( tfs[i].getText() );
		}
		
	}
	
	// prüft, ob sich Einträge geändert haben
	private Boolean HasChanged(){
		
		for ( int i = 0; i < tfs.length; i++) {
			if(! tfs[i].getText().trim().equals(originale.get(i))){
				if(i==4){
					neustartRequire = true;
				}
				return true;
			}
		}
		
		return false;
	}
	
	JRtaComboBox cmbBut = new JRtaComboBox();
	JRtaComboBox cmbClk = new JRtaComboBox();
	JRtaComboBox cmbRezAbbruch = new JRtaComboBox();
	JRtaComboBox cmbShowHmDlg = new JRtaComboBox();
	JRtaComboBox cmbShowDiffOnly = new JRtaComboBox();
	JRtaComboBox cmbDesktop = new JRtaComboBox();
	
	Vector<Vector<String>>vecJN = new Vector<Vector<String>>();
	Vector<Vector<String>>vec12 = new Vector<Vector<String>>();
	Vector<Vector<String>>vecHV = new Vector<Vector<String>>();
	
	private JPanel getVorlagenSeite(){
        //                                      1.            2.    3.    4.     5.     6.    7.      8.     9.    10     11
		FormLayout lay = new FormLayout("right:max(60dlu;p), 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu",
       //1.    2. 3. 4.   5.   6.   7.   8.  9.  10.  11. 12. 13.  14.  15. 16.  17. 18.  19.  20.    21.   22.   23.
		"p, 0dlu, p, p,0dlu,p,2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, "+
	   // 21.  22.  23.  24. 25.  26. 27. 28.  29.  30.  31.  32.  33. 34.  35.  36.  37   38   39  40   41  42  43   44  45  46
		"2dlu, p , 2dlu ,p , 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p,  2dlu,  p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p," +
	   // 47    48  49   50  51   52
		" 2dlu, p, 2dlu, p, 2dlu, p ");

		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		//JLabel lab = new JLabel("Nummernkreis von Mandant: "+Reha.aktIK);
		//lab.setFont(new Font("Tahoma",Font.BOLD,12));
		//builder.add(lab,cc.xyw(3, 4, 7));
		Vector<String> vec = new Vector<String>();
		int iAktY = 6;
		


		JLabel lab3 = new JLabel("<html><b>Werkzeug-Dialoge <font color=#0000FF>(Benutzer individuelle Einstellung)</font></b></html>");
		builder.add(lab3,cc.xyw(1, iAktY, 11));
		
		iAktY += 2;
		builder.addLabel("Zusatzknopf \"ausführen\" anzeigen",cc.xy(1,iAktY));
//		builder.addLabel("(0, 1)",cc.xy(5,iAktY));
		tfs[0] = new JRtaTextField("Zahlen", true);
		tfs[0].setName("zusatzknopf");
		tfs[0].setText((Boolean)SystemConfig.hmPatientenWerkzeugDlgIni.get("ToolsDlgShowButton") ? "1" : "0");
//		builder.add(tfs[0],cc.xy(3,iAktY));
		// ComboBox vorbereiten
		vec.clear();  // für die nächste Belegung freimachen
		Collections.addAll( vec, "Ja",   "1" );
		vecJN.add(0,(Vector<String>) vec.clone());
		vec.clear();  // für die nächste Belegung freimachen
		Collections.addAll( vec, "Nein", "0" );
		vecJN.add(1,(Vector<String>) vec.clone());
		vec.clear();

		Collections.addAll( vec, "vertikal", "0" );
		vecHV.add(0,(Vector<String>) vec.clone());
		vec.clear();  // für die nächste Belegung freimachen
		Collections.addAll( vec, "horizontal",   "1" );
		vecHV.add(1,(Vector<String>) vec.clone());
		vec.clear();  // für die nächste Belegung freimachen
		
		cmbBut.setDataVectorVector(vecJN, 0, 1);
		cmbBut.setSelectedItem(tfs[0].getText().equals("0") ? "Nein" : "Ja" );  // setze den aktuell gewählten Wert
		cmbBut.setActionCommand("cmbBut");
		cmbBut.addActionListener(this);
		builder.add(cmbBut, cc.xy(3,iAktY));

		iAktY += 2;
		builder.addLabel("Anzahl Mausklicks zum Werkzeugstart",cc.xy(1,iAktY));
//		builder.addLabel("(1, 2)",cc.xy(5,iAktY));
		tfs[1] = new JRtaTextField("ZAHLEN", true);
		tfs[1].setName("anzmaus");
		tfs[1].setText(SystemConfig.hmPatientenWerkzeugDlgIni.get("ToolsDlgClickCount").toString());
//		builder.add(tfs[1],cc.xy(3,iAktY));
		// ComboBox vorbereiten
		vec12.clear();
		vec.clear();  // für die nächste Belegung freimachen
		Collections.addAll( vec, "1 Klick",   "1" );
		vec12.add(0,(Vector<String>) vec.clone());
		vec.clear();  // für die nächste Belegung freimachen
		Collections.addAll( vec, "2 Klicks", "2" );
		vec12.add(1,(Vector<String>) vec.clone());
		cmbClk.setDataVectorVector(vec12, 0, 1);
		cmbClk.setSelectedItem(tfs[1].getText().equals("1") ? "1 Klick" : "2 Klicks" );  // setze den aktuell gewählten Wert
		cmbClk.setActionCommand("cmbClk");
		cmbClk.addActionListener(this);
		builder.add(cmbClk, cc.xy(3,iAktY));
		
		// Trennlinie mit Leerzeilen
		iAktY += 2;
		builder.addLabel(" ",cc.xyw(1, iAktY, 11));
		//-------------------------------------------------------------------------------
		iAktY += 1;
		builder.addSeparator("", cc.xyw(1,iAktY,11));
		iAktY += 1;
		builder.addLabel(" ",cc.xyw(1, iAktY, 11));

		iAktY += 2;
		JLabel lab4 = new JLabel("<html><b>Rezept-Dialog <font color=#0000FF>(Benutzer individuelle Einstellung)</font></b></html>");
		builder.add(lab4,cc.xyw(1, iAktY, 11));

		
		// Lemmi 20110116: Abfrage Abbruch bei Rezeptänderungen mit Warnung
		iAktY += 2;
		builder.addLabel("Warnung bei Rezeptabbruch nach Änderung",cc.xy(1,iAktY));
//		builder.addLabel("(0, 1)",cc.xy(5,iAktY));
		tfs[2] = new JRtaTextField("Zahlen", true);
		tfs[2].setName("rezabbruchwarn");
		tfs[2].setText((Boolean)SystemConfig.hmRezeptDlgIni.get("RezAendAbbruchWarn") ? "1" : "0");
//		builder.add(tfs[2],cc.xy(3,iAktY));
		// ComboBox vorbereiten
//		vec.clear();  // für die nächste Belegung freimachen
//		Collections.addAll( vec, "Ja",   "1" );
//		vecJN.add(0,(Vector<String>) vec.clone());
//		vec.clear();  // für die nächste Belegung freimachen
//		Collections.addAll( vec, "Nein", "0" );
//		vecJN.add(1,(Vector<String>) vec.clone());
		cmbRezAbbruch.setDataVectorVector(vecJN, 0, 1);
		cmbRezAbbruch.setSelectedItem(tfs[2].getText().equals("0") ? "Nein" : "Ja" );  // setze den aktuell gewählten Wert
		cmbRezAbbruch.setActionCommand("cmbRezAbbruch");
		cmbRezAbbruch.addActionListener(this);
		builder.add(cmbRezAbbruch, cc.xy(3,iAktY));
		

		
		// Trennlinie mit Leerzeilen
		iAktY += 2;
		builder.addLabel(" ",cc.xyw(1, iAktY, 11));
		//-------------------------------------------------------------------------------
		iAktY += 1;
		builder.addSeparator("", cc.xyw(1,iAktY,11));
		iAktY += 1;
		builder.addLabel(" ",cc.xyw(1, iAktY, 11));

		//iAktY += 1;

		//JOptionPane.showMessageDialog(null, "aktuelle Y-Position im Layout = "+iAktY);
		iAktY += 2;
		lab3 = new JLabel("<html><b>Heilmittel-Dialog <font color=#0000FF>(Benutzer individuelle Einstellung)</font></b></html>");
		builder.add(lab3,cc.xyw(1, iAktY, 11));
		iAktY += 2;
		builder.addLabel("Bei Terminbestätigung (Shift-F11) zeigen wenn erforderlich",cc.xy(1,iAktY));

		
		tfs[3] = new JRtaTextField("Zahlen", true);
		tfs[3].setName("hmdlgzeigen");
		tfs[3].setText((Boolean)SystemConfig.hmTerminBestaetigen.get("dlgzeigen") ? "1" : "0");
		cmbShowHmDlg.setDataVectorVector(vecJN, 0, 1);
		cmbShowHmDlg.setSelectedItem(tfs[3].getText().equals("0") ? "Nein" : "Ja" );  // setze den aktuell gewählten Wert
		cmbShowHmDlg.setActionCommand("cmbShowHmDlg");
		cmbShowHmDlg.addActionListener(this);
		builder.add(cmbShowHmDlg, cc.xy(3,iAktY));

		iAktY += 2;
		builder.addLabel("Dialog nur zeigen bei unterschiedlicher Mengenangabe",cc.xy(1,iAktY));
		tfs[5] = new JRtaTextField("Zahlen", true);
		tfs[5].setName("diffmenge");
		tfs[5].setText((Boolean)SystemConfig.hmTerminBestaetigen.get("dlgdiffzeigen") ? "1" : "0");
		cmbShowDiffOnly.setDataVectorVector(vecJN, 0, 1);
		cmbShowDiffOnly.setSelectedItem(tfs[5].getText().equals("0") ? "Nein" : "Ja" );  // setze den aktuell gewählten Wert
		cmbShowDiffOnly.setActionCommand("cmbShowDiffOnly");
		cmbShowDiffOnly.addActionListener(this);
		builder.add(cmbShowDiffOnly, cc.xy(3,iAktY));
		
		// Trennlinie mit Leerzeilen
		
		iAktY += 2;
		builder.addLabel(" ",cc.xyw(1, iAktY, 11));
		//-------------------------------------------------------------------------------
		iAktY += 1;
		builder.addSeparator("", cc.xyw(1,iAktY,11));
		iAktY += 1;
		builder.addLabel(" ",cc.xyw(1, iAktY, 11));
		
		iAktY += 2;
		lab3 = new JLabel("<html><b>Arbeitsflächen <font color=#0000FF>(Benutzer individuelle Einstellung)</font></b></html>");
		builder.add(lab3,cc.xyw(1, iAktY, 11));
		iAktY += 2;
		builder.addLabel("Horizontal (übereinander) oder vertikal (nebeneinander) teilen",cc.xy(1,iAktY));
		tfs[4] = new JRtaTextField("Zahlen", true);
		tfs[4].setName("desktop");
		tfs[4].setText((Boolean)SystemConfig.desktopHorizontal ? "1" : "0");
		cmbDesktop.setDataVectorVector(vecHV, 0, 1);
		cmbDesktop.setSelectedItem(tfs[4].getText().equals("1") ? "horizontal" : "vertikal" );  // setze den aktuell gewählten Wert
		cmbDesktop.setActionCommand("cmbDesktop");
		cmbDesktop.addActionListener(this);
		builder.add(cmbDesktop, cc.xy(3,iAktY));

		// Trennlinie mit Leerzeilen
		/*
		iAktY += 2;
		builder.addLabel(" ",cc.xyw(1, iAktY, 11));
		//-------------------------------------------------------------------------------
		iAktY += 1;
		builder.addSeparator("", cc.xyw(1,iAktY,11));
		iAktY += 1;
		builder.addLabel(" ",cc.xyw(1, iAktY, 11));
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
		
		if(cmd.equals("abbrechen")){
			SystemInit.abbrechen();
			return;
		}
		if(cmd.equals("speichern")){
			doSpeichern();
			return;
		}
		
		
		if(cmd.equals("cmbBut")){
//			System.out.println("cmbBut.getSecValue() = " + cmbBut.getSecValue());
			tfs[0].setText((String)cmbBut.getSecValue());
			return;
		}
		if(cmd.equals("cmbClk")){
//			System.out.println("cmbClk.getSecValue() = " + cmbClk.getSecValue());
			tfs[1].setText((String)cmbClk.getSecValue());
			return;
		}
		// Lemmi 20110116: Abfrage Abbruch bei Rezeptänderungen mit Warnung
		if(cmd.equals("cmbRezAbbruch")){
			tfs[2].setText((String)cmbRezAbbruch.getSecValue());
			return;
		}
		if(cmd.equals("cmbShowHmDlg")){
			tfs[3].setText((String)cmbShowHmDlg.getSecValue());
			return;
		}
		if(cmd.equals("cmbShowDiffOnly")){
			tfs[5].setText((String)cmbShowDiffOnly.getSecValue());
			return;
		}
		if(cmd.equals("cmbDesktop")){
			tfs[4].setText((String)cmbDesktop.getSecValue());
			return;
		}


	}
	
	
	private void doSpeichern(){
//		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/bedienung.ini");
		if(HasChanged() && neustartRequire){
			JOptionPane.showMessageDialog(null,"Sie haben die Einstellung für die Arbeitsflächen verändert.\n\n"+
					"Diese Einstellung wird erst nach einem Neustart von Thera-Pi wirksam!");
		}
		// Die benutzerindividuellen Daten müssen in die INI-Datei weggespeichert werden
		SystemConfig.hmPatientenWerkzeugDlgIni.put("ToolsDlgClickCount", Integer.parseInt(tfs[1].getText()) );
		SystemConfig.hmPatientenWerkzeugDlgIni.put("ToolsDlgShowButton", tfs[0].getText().equals("1") ? true : false );

		// Lemmi 20110116: auskommentiert, weil zentrales Wegschreiben in die INI
//		inif.setIntegerProperty("Bedienung", "WerkzeugaufrufMausklicks", Integer.parseInt(SystemConfig.hmPatientenWerkzeugDlgIni.get("ToolsDlgClickCount").toString()), " Anzahl Klicks für Werkzeugaufruf");
//		inif.setIntegerProperty("Bedienung", "WerkzeugaufrufButtonZeigen", (Boolean)SystemConfig.hmPatientenWerkzeugDlgIni.get("ToolsDlgShowButton") ? 1 : 0, " Zusatzknopf im Werkzeugdialog");

		// Lemmi 20110116: Abfrage Abbruch bei Rezeptänderungen mit Warnung
		SystemConfig.hmRezeptDlgIni.put("RezAendAbbruchWarn", tfs[2].getText().equals("1") ? true : false );
		// HeilmittelDialog bei Terminbestätigen zeigen falls notwendig /st.
		SystemConfig.hmTerminBestaetigen.put("dlgzeigen",tfs[3].getText().equals("1") ? true : false );
		SystemConfig.hmTerminBestaetigen.put("dlgdiffzeigen",tfs[5].getText().equals("1") ? true : false );
		
		SystemConfig.desktopHorizontal = (tfs[4].getText().equals("1") ? true : false );
		
		SaveChangeStatus();
		neustartRequire = false;
		SystemConfig.BedienungIni_WriteToIni();
		
		JOptionPane.showMessageDialog(null,"Einstellungen wurden erfolgreich in bedienung.ini gespeichert");
	}
}
