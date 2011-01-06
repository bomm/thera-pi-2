package patientenFenster;
// Lemmi 20110101: Kopieren des letzten Rezepts des selben Patienten bei Rezept-Neuanlage
// dazu neue Klasse mit Auswahlfenster angelegt !

import hauptFenster.Reha;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import oOorgTools.OOTools;

//import offenePosten.OffenePosten;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.therapi.reha.patient.PatientHauptPanel;

import sqlTools.ExUndHop;
import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemTools.JRtaCheckBox;
import systemTools.JRtaRadioButton;
import systemTools.LeistungTools;
import terminKalender.DatFunk;

import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;

public class RezeptVorlage extends RehaSmartDialog implements RehaTPEventListener,WindowListener, ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//ActionListener al = null;

	JRtaRadioButton[] rbDiszi = {null,null,null,null};
	ButtonGroup bgroup = new ButtonGroup();

//	public JRtaCheckBox[] leistung = {null,null,null,null,null}; 

	private RehaTPEventClass rtp = null;
	private RezeptVorlageHintergrund rgb;	
	
	public JButton uebernahme;
	public JButton abbrechen;
//	public String afrNummer;

	public String strSelected = "";
	Vector<String> vecDiszi = new Vector<String>();
	public Vector<String> vecResult = new Vector<String>();
	
	public RezeptVorlage(Point pt){
		super(null,"RezeptVorlage");		

		// Ermittlung der Rezept-Daten zu diesem Patienten
		String strPatIntern = (String)Reha.thisClass.patpanel.vecaktrez.get(0);
		
		String cmd = "SELECT DISTINCT SUBSTR(REZ_NR,1,2) as diszi from " +
					"(SELECT \"lza\", `PAT_INTERN`,`REZ_NR`, `REZ_DATUM` FROM `lza` lza WHERE `PAT_INTERN` = " + strPatIntern +
					" union " +
					"SELECT \"verordn\", `PAT_INTERN`,`REZ_NR`, `REZ_DATUM` FROM `verordn` ver WHERE `PAT_INTERN` = " + strPatIntern +
					") uni";
		//				ORDER BY REZ_NR asc, rez_datum desc
		starteSuche( cmd, "diszi" );
		
		
		pinPanel = new PinPanel();
		pinPanel.setName("RezeptVorlage");
		pinPanel.getGruen().setVisible(false);
		setPinPanel(pinPanel);
		getSmartTitledPanel().setTitle("Rezeptvorlage wählen");

		setSize(300,270);
		setPreferredSize(new Dimension(300,270));
		getSmartTitledPanel().setPreferredSize(new Dimension (300,270));
		setPinPanel(pinPanel);
		rgb = new RezeptVorlageHintergrund();
		rgb.setLayout(new BorderLayout());

		
		// Das erzeugt den farbigen Fensterhintergrund mit Farbverlauf
		new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {
			     rgb.setBackgroundPainter(Reha.thisClass.compoundPainter.get("RezNeuanlage"));
				return null;
			}
		}.execute();
			
		rgb.add(getVorlage(),BorderLayout.CENTER);
		
		getSmartTitledPanel().setContentContainer(rgb);
		getSmartTitledPanel().getContentContainer().setName("RezeptVorlage");
		setName("RezeptVorlage");
		setModal(true);
	    //Point lpt = new Point(pt.x-125,pt.y+30);
		Point lpt = new Point(pt.x-150,pt.y+30);
	    setLocation(lpt);
	    
		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener((RehaTPEventListener) this);

		pack();
		
		// prüfe die Anzahl der gefundenen Ergebnisse und treffe voreilige Entscheidungen !
		//int iTest = vecDiszi.size();
		if ( vecDiszi.size() < 2 ) {
			if ( vecDiszi.size() == 1 ) strSelected = vecDiszi.get( 0 );
			this.dispose();
			return;
		}

	}
	
/****************************************************/

	// ermittelt die ganz konkrete Vorlage für die Kopieraktion
	private void starteSucheVorlage( String strPatIntern, String strDiszi) {
		
		String cmd = 
		"SELECT * FROM `lza` WHERE `PAT_INTERN` = " + strPatIntern + " AND rez_nr like \"" + strDiszi + "%\"" +
		" union " +
		"SELECT * FROM `verordn` WHERE `PAT_INTERN` = " + strPatIntern + " AND rez_nr like \"" + strDiszi + "%\"" +
		" ORDER BY REZ_NR asc, rez_datum desc LIMIT 1";
		
		starteSuche( cmd, "vorlage" );
	}

	
	
	private void starteSuche(String sstmt, String strMode){
		Statement stmt = null;
		ResultSet rs = null;
		vecDiszi.clear();

			
		try {
			stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			rs = stmt.executeQuery(sstmt);
			
			if ( strMode.equals("diszi") ) while( rs.next() ) {
				// Lemmi 20101202: Fixe Spalten-Nummern durch lesbare Parameter ersetzt
				vecDiszi.add( rs.getString("diszi") );
			}  // end if while
			else if ( strMode.equals("vorlage") ) {
				vecResult.clear();
				ResultSetMetaData rsMetaData = rs.getMetaData() ;
				int numberOfColumns = rsMetaData.getColumnCount()+1;
				while( rs.next() ) for(int i = 1 ; i < numberOfColumns; i++){
							 vecResult.add( (rs.getString(i) == null  ? "" :  rs.getString(i) ) );
				}
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
	
	

	private JPanel getVorlage(){     //  1     2                   3     4     5    6    7                 8  
		FormLayout lay = new FormLayout("10dlu,fill:0:grow(0.50),50dlu,30dlu,5dlu,80dlu,fill:0:grow(0.50),10dlu",
									//     1   2  3    4  5   6  7   8  9   10  11    12  13    14  15
										"15dlu,p,10dlu,p,2dlu,p,2dlu,p,2dlu,p, 10dlu, p,  20dlu, p ,20dlu");
		PanelBuilder pb = new PanelBuilder(lay);
		CellConstraints cc = new CellConstraints();

		pb.getPanel().setOpaque(false);
		
		pb.addLabel("<html>Es existieren Rezepte in mehreren Disziplinen.<br><br>" + 
					"Bitte die Disziplin wählen, deren letztes Rezept Sie JETZT kopieren wollen",
					cc.xyw(2, 2, 5));

		int iAnzAktiv = SystemConfig.rezeptKlassenAktiv.size();
		int iAnzVorh = vecDiszi.size();
		int iYpos = 4;  //Start-Position der Radio-Buttons;
		Boolean bFirst = true;
		
		for ( int iVorh = 0; iVorh < iAnzVorh; iVorh++ ) {  // renne über alle gefunden Rezept-Disziplinen
			
			for(int iAktiv = 0; iAktiv < iAnzAktiv; iAktiv++) {  // Gleiche ab gegen die aktuelle aktiven Disziplinen
				if ( vecDiszi.get(iVorh).equals(SystemConfig.rezeptKlassenAktiv.get(iAktiv).get(1) ) ) { 

					// nur gefundene && aktive Disziplienen anzeigen
					rbDiszi[0] = new JRtaRadioButton(SystemConfig.rezeptKlassenAktiv.get(iAktiv).get(1));
					if ( bFirst ) {
						rbDiszi[0].setSelected(true);
						strSelected = vecDiszi.get(iVorh);
					}
					rbDiszi[0].setName(SystemConfig.rezeptKlassenAktiv.get(iAktiv).get(1));
					rbDiszi[0].addActionListener(this);
					bgroup.add(rbDiszi[0]);
					pb.add(rbDiszi[0], cc.xy(4,iYpos));
					
					// rechts noch die Langbeschreibung daneben fummeln
					pb.addLabel(SystemConfig.rezeptKlassenAktiv.get(iAktiv).get(0), cc.xy(6, iYpos));

					iYpos += 2;  // nächste Y-Position festlegen
					bFirst = false;
					break;  // für das innere "for"
				}
			}
		}

/*		
		pb.addLabel("Heilmittel 1",cc.xy(3, 4));
		String lab = (String)Reha.thisClass.patpanel.vecaktrez.get(48);
		leistung[0] = new JRtaCheckBox((lab.equals("") ? "----" : lab));
		leistung[0].setOpaque(false);
		if(!lab.equals("")){
			leistung[0].setSelected(true);			
		}else{
			leistung[0].setSelected(false);
			leistung[0].setEnabled(false);
		}
		pb.add(leistung[0],cc.xyw(5, 4, 2));
		
		pb.addLabel("Heilmittel 2",cc.xy(3, 6));
		lab = (String)Reha.thisClass.patpanel.vecaktrez.get(49);
		leistung[1] = new JRtaCheckBox((lab.equals("") ? "----" : lab));
		leistung[1].setOpaque(false);
		if(!lab.equals("")){
						
		}else{
			leistung[1].setSelected(false);
			leistung[1].setEnabled(false);
		}
		pb.add(leistung[1],cc.xyw(5, 6, 2));

		pb.addLabel("Heilmittel 3",cc.xy(3, 8));
		lab = (String)Reha.thisClass.patpanel.vecaktrez.get(50);
		leistung[2] = new JRtaCheckBox((lab.equals("") ? "----" : lab));
		leistung[2].setOpaque(false);
		if(!lab.equals("")){
						
		}else{
			leistung[2].setSelected(false);
			leistung[2].setEnabled(false);
		}
		pb.add(leistung[2],cc.xyw(5, 8, 2));

		pb.addLabel("Heilmittel 4",cc.xy(3, 10));
		lab = (String)Reha.thisClass.patpanel.vecaktrez.get(51);
		leistung[3] = new JRtaCheckBox((lab.equals("") ? "----" : lab));
		leistung[3].setOpaque(false);
		if(!lab.equals("")){
						
		}else{
			leistung[3].setSelected(false);
			leistung[3].setEnabled(false);
		}
		pb.add(leistung[3],cc.xyw(5, 10, 2));

		pb.addLabel("Eintragen in Memo",cc.xy(3, 12));
		leistung[4] = new JRtaCheckBox("Fehldaten");
		leistung[4].setOpaque(false);
		leistung[4].setSelected(true);
		pb.add(leistung[4],cc.xyw(5, 12, 2));
*/		
		uebernahme = new JButton("kopieren");
		uebernahme.setActionCommand("kopieren");
		uebernahme.addActionListener(this);
		uebernahme.addKeyListener(this);
		pb.add(uebernahme,cc.xyw(3,14,2));
		
		
		abbrechen = new JButton("abbrechen");
		abbrechen.setActionCommand("abbrechen");
		abbrechen.addActionListener(this);
		abbrechen.addKeyListener(this);
		pb.add(abbrechen,cc.xy(6,14));
		
		
		pb.getPanel().validate();
		return pb.getPanel();
	}
/****************************************************/	
	
	public void rehaTPEventOccurred(RehaTPEvent evt) {
		// TODO Auto-generated method stub
		try{
			if(evt.getDetails()[0] != null){
				if(evt.getDetails()[0].equals(this.getName())){
					this.setVisible(false);
					rtp.removeRehaTPEventListener((RehaTPEventListener) this);
					rtp = null;
					this.dispose();
					super.dispose();
					//System.out.println("****************Ausfallrechnung -> Listener entfernt**************");				
				}
			}
		}catch(NullPointerException ne){
			//System.out.println("In PatNeuanlage" +evt);
		}
	}
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		if(rtp != null){
			this.setVisible(false);			
			rtp.removeRehaTPEventListener((RehaTPEventListener) this);		
			rtp = null;
			pinPanel = null;
			dispose();
			super.dispose();
			//System.out.println("****************Ausfallrechnung -> Listener entfernt (Closed)**********");
		}
	}
	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getActionCommand().equals("kopieren")){
			// hier wird vecResult gefüllt
			starteSucheVorlage( (String)Reha.thisClass.patpanel.vecaktrez.get(0), strSelected );
//			Vector<String> vecTest = new Vector<String>();
//			vecTest = vecResult;
			this.dispose();
		}
		if( vecDiszi.contains( arg0.getActionCommand()) ) {  // Wenn eine der gefundenen Disziplinen angewählt worden ist
			strSelected = arg0.getActionCommand();
			// hier wird vecResult gefüllt
			starteSucheVorlage( (String)Reha.thisClass.patpanel.vecaktrez.get(0), strSelected );
//			Vector<String> vecTest = new Vector<String>();
//			vecTest = vecResult;

			this.dispose();
		}
		if(arg0.getActionCommand().equals("abbrechen")){
			strSelected = "";
			vecResult.clear();
			this.dispose();
		}

	}
	private RezeptVorlage getInstance(){
		return this;
	}
/*	
	private void doBuchen(){
		StringBuffer buf = new StringBuffer();
		buf.append("insert into rgaffaktura set ");
		buf.append("rnr='"+afrNummer+"', ");
		buf.append("reznr='"+(String)Reha.thisClass.patpanel.vecaktrez.get(1)+"', ");
		buf.append("pat_intern='"+(String)Reha.thisClass.patpanel.vecaktrez.get(0)+"', ");
		buf.append("rgesamt='"+(String)SystemConfig.hmAdrAFRDaten.get("<AFRgesamt>").replace(",",".")+"', ");
		buf.append("roffen='"+(String)SystemConfig.hmAdrAFRDaten.get("<AFRgesamt>").replace(",",".")+"', ");
		buf.append("rdatum='"+DatFunk.sDatInSQL(DatFunk.sHeute())+"'");
		////System.out.println(buf.toString());
		sqlTools.SqlInfo.sqlAusfuehren(buf.toString());
		
		// vvv Lemmi 20101220: Eintrag der AFR auch in Tabelle "rliste" (Offene Posten & Mahnungen)
		if ( SystemConfig.hmZusatzInOffenPostenIni.get("AFRinOPverwaltung") == 1) {
			String strHelp = "";
			StringBuffer buf2 = new StringBuffer();
			buf2.append("insert into rliste set ");
			buf2.append("r_nummer='0', ");
			buf2.append("x_nummer='" + afrNummer + "', ");
			buf2.append("r_datum='" + DatFunk.sDatInSQL(DatFunk.sHeute())+"', ");
	
			// Patienten-Name holen und eintragen
			String cmd = "select n_name, v_name from pat5 where id='" + (String)Reha.thisClass.patpanel.vecaktrez.get(0) + "'";
			//System.out.println(cmd);
			Vector<Vector<String>> vec = SqlInfo.holeFelder(cmd);
			if(vec.size() <= 0) strHelp = "Patient, unbekannt";
			else 				strHelp = vec.get(0).get(0) + ", " + vec.get(0).get(1);  // N_name, V_name
			buf2.append("r_kasse='" + strHelp + "', ");
			
			// Hole die ersten beiden Buchstaben aus der Rezeptnummer als "Klasse"
			strHelp = (String)Reha.thisClass.patpanel.vecaktrez.get(1);
			buf2.append("r_klasse='" + strHelp.substring(0, 2) + "', ");  
			
			buf2.append("r_betrag='" + (String)SystemConfig.hmAdrAFRDaten.get("<AFRgesamt>").replace(",",".")+"', ");
			buf2.append("r_offen='" + (String)SystemConfig.hmAdrAFRDaten.get("<AFRgesamt>").replace(",",".")+"', ");
			buf2.append("r_zuzahl='0.00', ");		
			buf2.append("pat_intern='" + (String)Reha.thisClass.patpanel.vecaktrez.get(0) + "', ");
			buf2.append("ikktraeger='" + (String)Reha.thisClass.patpanel.vecaktrez.get(1) + "'");  // Rezept-Nummer, z.B. ER23
			sqlTools.SqlInfo.sqlAusfuehren(buf2.toString());		
			// ^^^ Lemmi 20101220: Eintrag der RGR auch in Tabelle "rliste" (Offene Posten & Mahnungen)
		}
	}
	private void macheMemoEintrag(){
		StringBuffer sb = new StringBuffer();
		sb.append(DatFunk.sHeute()+" - unentschuldigt oder zu spät abgesagt - Rechnung!!\n");
		sb.append(Reha.thisClass.patpanel.pmemo[1].getText());
		Reha.thisClass.patpanel.pmemo[1].setText(sb.toString());
		String cmd = "update pat5 set pat_text='"+sb.toString()+"' where pat_intern = '"+Reha.thisClass.patpanel.aktPatID+"'";
		new ExUndHop().setzeStatement(cmd);
	}
	private void macheAFRHmap(){
		String mappos = "";
		String mappreis = "";
		String mapkurz = "";
		String maplang = "";
		String[] inpos = {null,null};
		String spos = "";
		String sart = "";
		Double gesamt = new Double(0.00);
		int preisgruppe = 0;
		
//		List<String> lAdrAFRDaten = Arrays.asList(new String[]{"<AFRposition1>","<AFRposition2>","<AFRposition3>"
//				,"<AFRposition4>","<AFRpreis1>","<AFRpreis2>","<AFRpreis3>","<AFRpreis4>","<AFRgesamt>","<AFRnummer>"});
			
		DecimalFormat df = new DecimalFormat( "0.00" );
		
		for(int i = 0 ; i < 4; i++){
			mappos = "<AFRposition"+(i+1)+">";
			mappreis = "<AFRpreis"+(i+1)+">";
			mapkurz = "<AFRkurz"+(i+1)+">";
			maplang = "<AFRlang"+(i+1)+">";
			if(leistung[i].isSelected()){
				Double preis = new Double( (String)Reha.thisClass.patpanel.vecaktrez.get(18+i));
				String s = df.format( preis);
				SystemConfig.hmAdrAFRDaten.put(mappos,leistung[i].getText());
				SystemConfig.hmAdrAFRDaten.put(mappreis,s);
				gesamt = gesamt+preis;
				
				spos = (String)Reha.thisClass.patpanel.vecaktrez.get(8+i);
				sart = (String)Reha.thisClass.patpanel.vecaktrez.get(1);
				sart = sart.substring(0,2);
				preisgruppe = Integer.parseInt(Reha.thisClass.patpanel.vecaktrez.get(41))-1;		
				inpos = LeistungTools.getLeistung(sart, spos,preisgruppe);	
				SystemConfig.hmAdrAFRDaten.put(maplang,inpos[0]);
				SystemConfig.hmAdrAFRDaten.put(mapkurz,inpos[1]);
				////System.out.println(inpos[0]);
				////System.out.println(inpos[1]);
				
			}else{
				spos = (String)Reha.thisClass.patpanel.vecaktrez.get(8+i);
				sart = (String)Reha.thisClass.patpanel.vecaktrez.get(1);
				sart = sart.substring(0,2);
				preisgruppe = Integer.parseInt(Reha.thisClass.patpanel.vecaktrez.get(41))-1;
				inpos = LeistungTools.getLeistung(sart, spos,preisgruppe);	

				SystemConfig.hmAdrAFRDaten.put(mappos,leistung[i].getText());
				SystemConfig.hmAdrAFRDaten.put(mappreis,"0,00");
				SystemConfig.hmAdrAFRDaten.put(maplang,(!inpos[0].equals("") ? inpos[0] : "----") );
				SystemConfig.hmAdrAFRDaten.put(mapkurz,(!inpos[1].equals("") ? inpos[1] : "----") );

			}
			
		}
		SystemConfig.hmAdrAFRDaten.put("<AFRgesamt>",df.format( gesamt));
		/// Hier mu� noch die Rechnungsnummer bezogen und eingetragen werden
		afrNummer = "AFR-"+Integer.toString(sqlTools.SqlInfo.erzeugeNummer("afrnr"));
		SystemConfig.hmAdrAFRDaten.put("<AFRnummer>",afrNummer);
	}
*/	
	public void keyPressed(KeyEvent event) {
		if(event.getKeyCode()==10){
			event.consume();
			
			if( ((JComponent)event.getSource()).getName().equals("uebernahme")){
				//doUebernahme();
			}
			if( ((JComponent)event.getSource()).getName().equals("abbrechen")){
				this.dispose();
			}

			//System.out.println("Return Gedrückt");
		}
		if( event.getKeyCode() == KeyEvent.VK_ESCAPE ){  // 27  Abbruch mit der Tastatur
			int iTest = KeyEvent.VK_ENTER;
			this.dispose();
		}
	}
	
/*	
	@SuppressWarnings("unchecked")
	public static void starteAusfallRechnung(String url){
		IDocumentService documentService = null;;
		//System.out.println("Starte Datei -> "+url);
		if(!Reha.officeapplication.isActive()){
			Reha.starteOfficeApplication();
		}
		try {
			documentService = Reha.officeapplication.getDocumentService();
		} catch (OfficeApplicationException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Fehler im OpenOffice-System - Ausfallrechnung kann nicht erstellt werden");
			return;
		}
        IDocumentDescriptor docdescript = new DocumentDescriptor();
       	docdescript.setHidden(false);
        docdescript.setAsTemplate(true);
		IDocument document = null;
		//ITextTable[] tbl = null;
		try {
			document = documentService.loadDocument(url,docdescript);
		} catch (NOAException e) {

			e.printStackTrace();
		}
		ITextDocument textDocument = (ITextDocument)document;
		ITextFieldService textFieldService = textDocument.getTextFieldService();
		ITextField[] placeholders = null;
		try {
			placeholders = textFieldService.getPlaceholderFields();
		} catch (TextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < placeholders.length; i++) {
			//boolean loeschen = false;
			boolean schonersetzt = false;
			String placeholderDisplayText = placeholders[i].getDisplayText().toLowerCase();
			////System.out.println(placeholderDisplayText);	
		    //////////////////////////////
			Set<?> entries = SystemConfig.hmAdrPDaten.entrySet();
		    Iterator<?> it = entries.iterator();
		    while (it.hasNext()) {
		      Map.Entry<String,String> entry = ((Map.Entry<String,String>) it.next());
		      if(((String)entry.getKey()).toLowerCase().equals(placeholderDisplayText)){
		    	  placeholders[i].getTextRange().setText(((String)entry.getValue()));
		    	  schonersetzt = true;
		    	  break;
		      }
		    }
		    ///////////////////////////////
		    entries = SystemConfig.hmAdrAFRDaten.entrySet();
		    it = entries.iterator();
		    while (it.hasNext() && (!schonersetzt)) {
		      Map.Entry entry = (Map.Entry) it.next();
		      if(((String)entry.getKey()).toLowerCase().equals(placeholderDisplayText)){
		    	  placeholders[i].getTextRange().setText(((String)entry.getValue()));
		    	  schonersetzt = true;
		    	  break;
		      }
		    }
		    if(!schonersetzt){
		    	OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
		    }
		    ////////////////////////
		}
		
	}
*/	
	
}
class RezeptVorlageHintergrund extends JXPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ImageIcon hgicon;
	int icx,icy;
	AlphaComposite xac1 = null;
	AlphaComposite xac2 = null;		
	public RezeptVorlageHintergrund(){
		super();
		/*
		hgicon = new ImageIcon(Reha.proghome+"icons/geld.png");
		icx = hgicon.getIconWidth()/2;
		icy = hgicon.getIconHeight()/2;
		xac1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.15f); 
		xac2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f);
		*/			
		
	}
	@Override
	public void paintComponent( Graphics g ) { 
		super.paintComponent( g );
		@SuppressWarnings("unused")
		Graphics2D g2d = (Graphics2D)g;
		
		if(hgicon != null){
			//g2d.setComposite(this.xac1);
			//g2d.drawImage(hgicon.getImage(), (getWidth()/2)-icx , (getHeight()/2)-icy,null);
			//g2d.setComposite(this.xac2);
		}
	}
}