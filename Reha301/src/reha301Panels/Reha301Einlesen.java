package reha301Panels;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.thera_pi.nebraska.crypto.NebraskaCryptoException;
import org.thera_pi.nebraska.crypto.NebraskaDecryptor;
import org.thera_pi.nebraska.crypto.NebraskaFileException;
import org.thera_pi.nebraska.crypto.NebraskaKeystore;
import org.thera_pi.nebraska.crypto.NebraskaNotInitializedException;
import org.thera_pi.nebraska.gui.NebraskaMain;

import reha301.Reha301;
import reha301.Reha301Tab;

import Tools.ButtonTools;
import Tools.DatFunk;
import Tools.INIFile;
import Tools.IntegerTools;
import Tools.JCompTools;
import Tools.OOTools;
import Tools.SqlInfo;
import Tools.StringTools;
import Tools.Verschluesseln;

import ag.ion.bion.officelayer.text.ITextDocument;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


public class Reha301Einlesen{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	JButton[] buts = {null,null,null};
	ActionListener al = null;
	JTextArea meldung = null;
	Reha301Tab eltern = null;
	boolean bewilligung = false;
	boolean auftragsleistung = false;
	boolean erstertext = true;
	
	int UNTS = 0;
	Vector<String> edifact_vec = new Vector<String>();
	StringBuffer buf = new StringBuffer();
	StringBuffer fallbuf = new StringBuffer();
	Vector<String> db_vec = new Vector<String>();
	HashMap<String,String> dbHmap = new HashMap<String,String>();
	//String name,vorname,titel,geboren,strasse,ort,plz,ikkasse,telefon;
	String[] hmtitel =	{"sender","datum","bearbeiter","aktenzeichen","funktionag","beauftragtestelle",
			"versicherungsnr","berechtigtennr","massnahmennr","geschlechtfamstand","national",
			"artderleistung","diagschluessel","anrede","nachname","vorname","geboren",
			"strasse","plz","ort","ikkasse","vnranspruchsberechtigter","ktraeger","tage",
			"a12","a09","pnabm","adr0","adr1","klinik","aufnahmegeplant","aufnahmefruehestens","telefon","haname","haadresse"};
	boolean pnaMsActive = false;
	boolean pnaMrActive = false;
	boolean pnaByActive = false;
	boolean pnaMtActive = false;
	boolean pnaHaActive = false;
	boolean pnaBmActive = false;
	int nachrichtentyp = -1;
	boolean erstercheck = false;
	// 0 = Sender, 1 = Sender, 2 = Empfänger mit Entschl., 3 = Physik Empfänger, 4 = Originalgr.
	// 5 = Encoded-Größe, 6 = log.Dateiname, 7 = physik.Dateiname
	Object[] decodeparms = {null,null,null,null,null,null,null,null};
	String decodedfile = null;
	boolean erstdiagnose = false;
	int anzahldiagnosen = 0;
	String encodepfad = Reha301.inbox; //"C:/OODokumente/RehaVerwaltung/Dokumentation/301-er/";
	File auftragfile = null;
	
	public Reha301Einlesen(Reha301Tab xeltern){
		//super(new BorderLayout());
		eltern = xeltern;
		//ActivateListener();
		//add(getFormLayout(),BorderLayout.CENTER);
	}
	public boolean decodeAndRead(String dir,File xfile){
		encodepfad = dir;
		auftragfile = xfile;
		boolean erfolg = doDecode(encodepfad);
		//starteEinlesen(encodepfad);
		encodepfad = null;
		eltern.activateNachricht();
		return erfolg;
	}
	
	public void ActivateListener(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("einlesen")){
					if(decodedfile==null){
						JOptionPane.showMessageDialog(null,"Keine Entschlüsselte Datei verfügbar");
						return;
					}
					starteEinlesen(decodedfile,"");
					decodedfile = null;
					
					return;
				}
				if(cmd.equals("decode")){
					doDecode("");
					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() throws Exception {
							eltern.activateNachricht();
							return null;
						}
						
					}.execute();
				}
			}
		};
	}
	private boolean doDecode(String pfad){
		//String pfad = dateiDialog(encodepfad);
		if(pfad.trim().equals("")){return false;}
		pfad = pfad.replace("\\", "/");
		String datei = pfad.substring(pfad.lastIndexOf("/")+1);
		////System.out.println("Ausgewählte Datei = "+datei);
		////System.out.println("Kompletter Pfad = "+pfad);
		////System.out.println("Auftragsfile = "+this.auftragfile.getName());
		if( datei.length() > 0){
		//if( (datei.toUpperCase().startsWith("EREH") && datei.toUpperCase().endsWith(".AUF")) ){
			boolean test = testeAuftragsDatei(pfad,datei);
			if(!test){
				return false;
			}
			test = false;
			try {
				test = doEntschluesseln(pfad,datei);
			} catch (NebraskaCryptoException e) {
				e.printStackTrace();
			} catch (NebraskaNotInitializedException e) {
				e.printStackTrace();
			} catch (NebraskaFileException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(!test){
				return false;
			}
			////System.out.println("1 - this.decodedfile="+this.decodedfile+" --- datei="+datei);
			
			starteEinlesen(this.decodedfile,datei);
			
			//Wichtig für die Aktualisierung in Therapie
			
		/*)}else if(pfad.length()>0){
			//System.out.println("Pfad = "+pfad.replace("\\", "/"));
			this.decodedfile = pfad.replace(".auf", ".org");
			//System.out.println("2 -this.decodedfile="+this.decodedfile+" --- datei="+datei);
			starteEinlesen(this.decodedfile,datei);
			//return false;*/
		}else{
			JOptionPane.showMessageDialog(null,"Die ausgewählte Datei ist keine Auftragsdatei gemäß DTA nach §301");
			return false;
		}
		return true;
	}
	private void starteEinlesen(String filename,String datei){
		////System.out.println("Starte Einlesen Filename = "+filename);
		////System.out.println("Starte Einlesen Dateiname = "+datei);
		doEinlesen(filename,datei);
		//doEinlesen("C:/OODokumente/RehaVerwaltung/Dokumentation/301-er/bewi.txt");
	}
	private void regleNachricht(String pfad,String datei){

		if(nachrichtentyp == 1){
			if(dbHmap.get("beauftragtestelle")==null){
				//dbHmap.put("beauftragtestelle",dbHmap.get("ktraeger"));
				dbHmap.put("beauftragtestelle","");
			}
			try {
				doBewilligungSpeichern(pfad,datei);
				doHmapLeeren();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//doPatTest();
			return;
		}
		if(nachrichtentyp == 2){
			if(dbHmap.get("beauftragtestelle")==null){
				//dbHmap.put("beauftragtestelle",dbHmap.get("ktraeger"));
				dbHmap.put("beauftragtestelle","");
			}
			try {
				doBewilligungSpeichern(pfad,datei);
				doHmapLeeren();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//doPatTest();
			return;
		}

	}
	private void doPatTest(){
		String sqlcmd = "select * from pat5 where n_name ='"+
		dbHmap.get("nachname")+"' and v_name ='"+
		dbHmap.get("vorname")+"' and geboren ='"+
		dbHmap.get("geboren")+"'";
		Vector<Vector<String>> pvec = SqlInfo.holeFelder(sqlcmd);

		if(pvec.size()==0){
			String patdaten = "Nachname: <b>"+dbHmap.get("nachname")+"</b><br>"+
			"Vorname: <b>"+dbHmap.get("vorname")+"</b><br>"+
			"Geboren am: <b>"+DatFunk.sDatInDeutsch(dbHmap.get("geboren"))+"</b><br>"+
			"Strasse: <b>"+dbHmap.get("strasse")+"</b><br>"+
			"Wohnort: <b>"+dbHmap.get("plz")+" "+dbHmap.get("ort")+"</b><br></html>";
			JOptionPane.showMessageDialog(null, "<html>Für die aktuelle Bewilligung ist noch kein Patient angelegt<br><br>"+patdaten);
		}else if(pvec.size() > 0){
			String patdaten = "Nachname: <b>"+dbHmap.get("nachname")+"</b><br>"+
			"Vorname: <b>"+dbHmap.get("vorname")+"</b><br>"+
			"Geboren am: <b>"+DatFunk.sDatInDeutsch(dbHmap.get("geboren"))+"</b><br></html>";
			JOptionPane.showMessageDialog(null, "<html>Es gibt "+pvec.size()+" Patient(en) mit diesen Daten im Patiententamm<br><br>"+patdaten);			
		}
	}
	private JXPanel getFormLayout(){
		JXPanel pan = new JXPanel();
		String xwerte = "2dlu,fill:0:grow(0.33),5dlu,fill:0:grow(0.66),2dlu";
		String ywerte = "5dlu,fill:0:grow(1.0),5dlu";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		pan.add(getLinkeSeite(),cc.xy(2,2,CellConstraints.FILL,CellConstraints.FILL));
		pan.add(getRechteSeite(),cc.xy(4,2,CellConstraints.FILL,CellConstraints.FILL));
		pan.validate();
		return pan;
	}
	private JXPanel getLinkeSeite(){
		JXPanel pan = new JXPanel();
		String xwerte = "2dlu,fill:0:grow(1.0),2dlu";
		String ywerte = "5dlu,p,15dlu,p,fill:0:grow(1.0),5dlu";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		buts[0] = ButtonTools.macheButton("Nachricht einlesen", "einlesen", al);
		buts[1] = ButtonTools.macheButton("Nachricht entschlüsseln", "decode", al);
		pan.add(buts[1],cc.xy(2,2));
		pan.add(buts[0],cc.xy(2,4));
		pan.validate();
		return pan;
	}
	private JXPanel getRechteSeite(){
		JXPanel pan = new JXPanel();
		String xwerte = "2dlu,fill:0:grow(1.0),2dlu";
		String ywerte = "5dlu,fill:0:grow(1.0),5dlu";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		meldung = new JTextArea();
		meldung.setFont(new Font("Courier",Font.PLAIN,12));
		meldung.setLineWrap(true);
		meldung.setWrapStyleWord(true);
		//tafiles.setEditable(false);
		meldung.setBackground(Color.WHITE);
		meldung.setForeground(Color.BLUE);
		JScrollPane jscr = JCompTools.getTransparentScrollPane(meldung);
		jscr.validate();
		pan.add(jscr,cc.xy(2,2,CellConstraints.FILL,CellConstraints.FILL));
		
		
		pan.validate();
		return pan;
	}
	private void doBewilligungSpeichern(String pfad,String datei) throws IOException{
		String bedingung = "versicherungsnr='"+dbHmap.get("versicherungsnr")+"' and datum='"+
		dbHmap.get("datum")+"' and nachrichtentyp='"+(bewilligung ? "1" : "2")+"'";
		int anzahl = SqlInfo.zaehleSaetze("dta301", bedingung);
		if(anzahl==0){
			bedingung = "insert into dta301 set "+
			"nachrichtentyp='"+(bewilligung ? "1" : "2")+"', sender='"+dbHmap.get("sender")+"', datum='"+
			dbHmap.get("datum")+"', bearbeiter='"+dbHmap.get("bearbeiter")+"', aktenzeichen='"+
			dbHmap.get("aktenzeichen")+"', funktionag='"+dbHmap.get("funktionag")+"', beauftragtestelle='"+
			dbHmap.get("beauftragtestelle")+"', versicherungsnr='"+dbHmap.get("versicherungsnr")+"', berechtigtennr='"+
			dbHmap.get("berechtigtennr")+"', massnahmennr='"+dbHmap.get("massnahmennr")+"', geschlechtfamstand='"+
			dbHmap.get("geschlechtfamstand")+"', national='"+dbHmap.get("national")+"', artderleistung='"+
			dbHmap.get("artderleistung")+"', diagschluessel='"+dbHmap.get("diagschluessel")+"', lfnr='1', "+
			"patangaben='"+dbHmap.get("anrede")+"#"+dbHmap.get("nachname")+"#"+dbHmap.get("vorname")+"#"+
			dbHmap.get("geboren")+"#"+dbHmap.get("strasse")+"#"+dbHmap.get("plz")+"#"+dbHmap.get("ort")+
			"#"+dbHmap.get("ikkasse")+"', vnranspruchsberechtigter='"+dbHmap.get("vnranspruchsberechtigter")+"', "+
			"ktraeger='"+dbHmap.get("ktraeger")+"', pnaab='"+dbHmap.get("beauftragtestelle")+"', auftragsleistung='"+
			(auftragsleistung ? "T" : "F")+"', tage='"+dbHmap.get("tage")+"',edifact='"+buf.toString()+"', "+
			"eilfall='"+dbHmap.get("a09")+"', leistung='"+dbHmap.get("a12")+"', "+
			"esol='"+StringTools.EscapedDouble(fallbuf.toString())+"', receiver='"+dbHmap.get("klinik")+"',"+
			"adr0='"+dbHmap.get("telefon").replace("/"," ")+"',"+
			"adr1='"+dbHmap.get("haname")+"#"+dbHmap.get("haadresse")+"'";

			SqlInfo.sqlAusfuehren(bedingung);
			////System.out.println(bedingung);
			/*
			int anfrage = JOptionPane.showConfirmDialog(null, "Nachricht im OpenOffice.org-Writer öffnen?","Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
			if(anfrage == JOptionPane.YES_OPTION){
				ITextDocument document = OOTools.starteLeerenWriter(true);
				document.getTextService().getText().setText(buf.toString());
			}
			*/
			////System.out.println(bedingung);
		}else{
			////System.out.println("Die Bewilligung ist bereits in der Datenbank enthalten");
			JOptionPane.showMessageDialog(null, "Die Bewilligung ist bereits in der Datenbank enthalten");
		}
	}
	private void doHmapLeeren(){
		for(int i = 0; i < hmtitel.length;i++){
			dbHmap.put(hmtitel[i],"");
		}
	}
	private void doEinlesen(String pfad,String datei){
		try {
			Object[] obj = null;
			File xpfad = new File(pfad);
			String test = new String(BytesFromFile(xpfad));
			if(test.indexOf("\n")>= 0 || test.indexOf("\r")>= 0){
				getVectorFromFile(xpfad,true);	
			}else{
				getVectorFromFile(xpfad,false);
			}
			//test.replace("\n", "");
			//test.replace("\r", "");
			
			//Die Datei in die Textarea schreiben
			//meldung.setText("");
			String[] unz = null;
			if(edifact_vec.size()>0){
				String UNZ = edifact_vec.get(edifact_vec.size()-1);
				if(! UNZ.startsWith("UNZ")){
					JOptionPane.showMessageDialog(null, "Kein korrekter Edifact-Aufbau");
					return;
				}
				unz = UNZ.split("\\+");
				int anzahlunts = StringTools.holeZahlVorneNullen(unz[1]);
				int start = 2;
				int nachricht = 1;
				int zeilen;
				
				for(int x = 0; x < anzahlunts;x++){
					erstercheck = false;
					zeilen = 0;
					buf.setLength(0);
					buf.trimToSize();
					fallbuf.setLength(0);
					fallbuf.trimToSize();
					erstdiagnose = false;
					anzahldiagnosen = 0; 
					for(int i = start; i < edifact_vec.size();i++){
						zeilen++;
						fallbuf.append(edifact_vec.get(i)+"\n");
						if(edifact_vec.get(i).startsWith("UNH")){
							//meldung.setText(meldung.getText()+(nachricht > 1 ? "\n\n" : "")+"******************Nachricht Nr. "+nachricht+" von "+anzahlunts+" ****************\n\n");
							nachricht++;
						}
						/*****************Bewilligung************/
						if(edifact_vec.get(start).contains("+MEDR01:D")){
							if(!erstercheck){
								nachrichtentyp = 1;
								erstercheck = true;
							}
							obj = doAuswertenBewilligung(StringTools.do301NormalizeString(edifact_vec.get(i)),i);
							if(obj[0] != null){
								buf.append(StringTools.Escaped(obj[0].toString())+"\n");
								//meldung.setText(meldung.getText()+obj[0].toString()+"\n");
							}
						}
						/******************************************/
						if(edifact_vec.get(start).contains("+MEDR02:D")){
							if(edifact_vec.get(edifact_vec.size()-3).contains("PCR+ADMIN5")){
								if(!erstercheck){
									nachrichtentyp = 14;
									erstercheck = true;
								}
							}else{
								if(!erstercheck){
									nachrichtentyp = 2;
									erstercheck = true;
								}
							}
							obj = doAuswertenBewilligung(StringTools.do301NormalizeString(edifact_vec.get(i)),i);
							if(obj[0] != null){
								buf.append(StringTools.Escaped(obj[0].toString())+"\n");
								//meldung.setText(meldung.getText()+obj[0].toString()+"\n");
							}
						}
						/******************************************/
						//Hier die neuen Anlässe einer Nachricht
						//wie z.B. Antwort auf Verlängerungsantrag etc.
						/******************************************/
						if(edifact_vec.get(i).startsWith("UNT")){
							if(IntegerTools.trailNullAndRetInt(edifact_vec.get(i).split("\\+")[1]) != zeilen){
								JOptionPane.showMessageDialog(null, "Anzahl Zeilen = "+
										IntegerTools.trailNullAndRetInt(edifact_vec.get(i).split("\\+")[1])+
										" in Nachricht "+nachricht+" ist nicht korrekt");
							}
							regleNachricht(pfad,datei);
							start = i+1;
							break;
						}
					}
					////System.out.println(fallbuf.toString());
				}
				JOptionPane.showMessageDialog(null, "In dieser Datei sind "+Integer.toString(anzahlunts)+" Nachrichten enthalten");
				//Untersuchen wieviel Nachrichten enthalten sind
				//JOptionPane.showMessageDialog(null, "In dieser Datei sind "+Integer.toString(anzahlunts)+" Nachrichten enthalten");
			}else{
				JOptionPane.showMessageDialog(null, "Kein korrekter Edifact-Aufbau\nDatei enthält keine Nachrichten");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void getVectorFromFile(File file,boolean mitlinefeed) throws IOException { 
		edifact_vec.clear();
		edifact_vec.trimToSize();
		dbHmap.clear();
		doHmapLeeren();
		int count13 = 0;
		int count10 = 0;
		InputStream is = new FileInputStream(file); 
		final byte ZEILENENDE = "\n".getBytes()[0];
		byte[] ende = {13};
		byte letztes_byte = 0;
		final String ERSATZ = new String(ende);
		final byte SYSTEMZEILE = (System.getProperty("line.separator").getBytes().length == 2 ? 
				System.getProperty("line.separator").getBytes()[1] : 
					System.getProperty("line.separator").getBytes()[0]	);
		byte[] bytes = new byte[1]; 
		// Read in the bytes 
		int offset = 0; int numRead = 0; 
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String inhalt = null;
		//int zeilen = 0;
		byte fragezeichen = "?".getBytes()[0];
		byte hochkomma = "'".getBytes()[0];
		////System.out.println("ByteWert für Fragezeichen = "+fragezeichen.getBytes()[0]);
		////System.out.println("ByteWert für Hochkomma    = "+hochkomma.getBytes()[0]);
		int zeile = 0;
		while (true) {
			numRead = is.read(bytes,offset,1);
			if(numRead > 0){
				baos.write(bytes[0]);
			}else{
				baos.flush();
				baos.close();
				is.close();
				inhalt = new String(baos.toByteArray()).replace(ERSATZ,"").replace("\n", "");
				if(inhalt.length()>1){
					edifact_vec.add(String.valueOf(inhalt.substring(0,inhalt.length()-1)));	
				}
				////System.out.println("Einlesen beendet");
				break;
			}
			//Testen auf Zeilenende = \n
			if(bytes[0] == 13){
				count13++;
				//continue;
			}
			if(bytes[0] == 10){
				count10++;
				//continue;
			}
			byte[] b = {bytes[0]};
			////System.out.print("Byte="+new String(b)+" / Wert="+bytes[0]);
			if(mitlinefeed){
				
				////System.out.println("Mit-Linefeed");
				if(bytes[0] == ZEILENENDE || bytes[0]==SYSTEMZEILE || bytes[0]==10 ){
					inhalt = new String(baos.toByteArray()).replace(ERSATZ,"").replace("\n", "");
					if(inhalt.substring(inhalt.length()-1).equals("'")){
						////System.out.println("Mit-Linefeed und Ende der Zeile erkannt");
						baos.flush();
						baos.close();
						////System.out.println("Zeile "+(zeile++)+" = "+String.valueOf(inhalt.substring(0,inhalt.length()-1)));
						if(!inhalt.trim().equals("")){
							////System.out.println("Vector angehängt "+inhalt);
							edifact_vec.add(String.valueOf(inhalt.substring(0,inhalt.length()-1)));
						}
						baos = new ByteArrayOutputStream();
					}
				}else{
					letztes_byte = bytes[0]; 
				}
			}else{
				////System.out.println("Ohne-Linefeed");
				if(	(bytes[0]==hochkomma && letztes_byte!=fragezeichen)  ){
					////System.out.println("Ohne-Linefeed - Zeile erkannt");
					inhalt = new String(baos.toByteArray()).replace(ERSATZ,"").replace("\n", "");
					if(inhalt.substring(inhalt.length()-1).equals("'")){
						baos.flush();
						baos.close();
						if(!inhalt.trim().equals("")){
							////System.out.println("Vector angehängt "+inhalt);
							edifact_vec.add(String.valueOf(inhalt.substring(0,inhalt.length()-1)));
						}
						baos = new ByteArrayOutputStream();
					}
				}else{
					letztes_byte = bytes[0]; 
				}
			}
		}
		//JOptionPane.showMessageDialog(null,"Count13 ="+Integer.toString(count13)+"\nCount10="+Integer.toString(count10));
 	
	}
	/*********************************/
	private void setPnaActive(String pna){
		pnaMsActive = false;
		pnaMrActive = false;
		pnaByActive = false;
		pnaMtActive = false;
		pnaHaActive = false;
		pnaBmActive = false;
		if(pna.equals("MS")){
			pnaMsActive = true;
			return;
		}else if(pna.equals("MR")){
			pnaMrActive = true;
			return;
		}else if(pna.equals("BY")){
			pnaByActive = true;
			return;
		}else if(pna.equals("MT")){
			pnaMtActive = true;
			return;
		}else if(pna.equals("HA")){
			pnaHaActive = true;
			return;
		}else if(pna.equals("BM")){
			pnaBmActive = true;
			return;
		}
	}
	/*********************************/
	private Object[] doAuswertenBewilligung(String zeile,int i){
		Object[] ret = {null,(Integer) i};
		String[] teile = null;
		String test = null;
		teile = zeile.split("\\+");
		/*************/
		if(zeile.startsWith("ADR++1:")){
			try{
				ret[0] = String.valueOf(teile[2].split(":")[1]+"\n"+teile[4]+" "+teile[3])+"\n";
				dbHmap.put("strasse",teile[2].split(":")[1]);
				dbHmap.put("plz",teile[4]);
				dbHmap.put("ort",teile[3]);
				if(pnaHaActive){
					dbHmap.put("haadresse", String.valueOf(teile[3]));
					pnaHaActive = false;
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
			return ret;
		}
		if(zeile.startsWith("ADR+1++")){
			ret[0] = String.valueOf("Geburtsort: "+teile[3]);
			return ret;
		}
		if(zeile.startsWith("AGR+BY:")){
			ret[0] = String.valueOf("Funktion des Auftraggebers: "+regleBY(teile[1].split(":")[1]));
			dbHmap.put("funktionag", teile[1].split(":")[1]);
			return ret;
		}
		if(teile[0].equals("BGM")){
			ret[0] = "Anlaß der Nachricht: "+String.valueOf(regleBGM(teile[1]));
			return ret;
		}
		if(zeile.startsWith("CIN+DIA+")){
			String rvgkv = (teile[2].split(":")[1].equals("10R") || teile[2].split(":")[1].equals("I0R") ? "RV-Träger (10R)" : "GKV (10K)");
			anzahldiagnosen++;
			//ret[0] = "Diagnoseschlüssel: "+teile[2].split(":")[0]+" - "+ (teile[2].split(":")[1].equals("10R")? "RV-Träger (10R)" : "GKV (10K)");
			ret[0] = "Diagnoseschlüssel: "+teile[2].split(":")[0]+" - "+ rvgkv;
			if(!erstdiagnose){
				dbHmap.put("diagschluessel", zeile);
				erstdiagnose = true;
			}else{
				dbHmap.put("diagschluessel",dbHmap.get("diagschluessel")+"@"+zeile);
			}
			return ret;
		}
		if(zeile.startsWith("COM+")){
			if(teile.length < 2){ret[0]="";return ret;}
			//if(teile.length == 2){ret[0]="Kontakt: "+teile[1];return ret;}
			if(teile[1].trim().endsWith(":TE")){
				ret[0] = String.valueOf("Telefon: "+teile[1].split(":")[0]);	
				dbHmap.put("telefon",teile[1].split(":")[0] );
			}else if(teile[1].trim().endsWith(":FX")){
				ret[0] = String.valueOf("Fax: "+teile[1].split(":")[0]);	
			}else if(teile[1].trim().endsWith(":EM")){
				ret[0] = String.valueOf("Email: "+teile[1].split(":")[0]);	
			}
			return ret;
		}
		if(zeile.startsWith("CTA+BEA")){
			ret[0] = String.valueOf("Bearbeiter: "+teile[2]);
			dbHmap.put("bearbeiter",zeile );
			return ret;
		}
		if(zeile.startsWith("CTA+ABT")){
			ret[0] = String.valueOf("Abteilung der Klinik: "+teile[2]);
			return ret;
		}
		if(zeile.startsWith("FCA+")){
			ret[0] = String.valueOf("Art der Daten: "+regleFCA(teile[1]));
			return ret;
		}
		//"aufnahmegeplant","aufnahmefruehestens"}
		//Aufnahme geplant am
		if(zeile.startsWith("DTM+291:")){
			String datum = teile[1].substring(4,14);
			datum = datum.substring(6,8)+"."+datum.substring(4,6)+"."+datum.substring(0,4);
			ret[0] = String.valueOf("Aufnahme geplant für den: "+datum);
			dbHmap.put("aufnahmegeplant",DatFunk.sDatInSQL(datum));
			return ret;
			
		}
		//Aufnahme frühestens am
		if(zeile.startsWith("DTM+231:")){
			String datum = teile[1].substring(4,14);
			datum = datum.substring(6,8)+"."+datum.substring(4,6)+"."+datum.substring(0,4);
			ret[0] = String.valueOf("!!!!!Aufnahme frühestens am: "+datum);
			dbHmap.put("aufnahmefruehestens",DatFunk.sDatInSQL(datum));
			return ret;
		}

		if(zeile.startsWith("DTM+137:")){
			String datum = teile[1].substring(4,14);
			datum = datum.substring(6,8)+"."+datum.substring(4,6)+"."+datum.substring(0,4);
			ret[0] = String.valueOf("Datum der Dateierstellung (Fachverfahren): "+datum);
			dbHmap.put("datum",DatFunk.sDatInSQL(datum));
			return ret;
		}
		if(zeile.startsWith("DTM+182:")){
			String datum = teile[1].substring(4,14);
			datum = datum.substring(6,8)+"."+datum.substring(4,6)+"."+datum.substring(0,4);
			String art = "";
			if(nachrichtentyp == 1){
				art = "Bewilligung: ";
			}else if(nachrichtentyp == 2){
				art = "Ablehnung: ";
			}else if(nachrichtentyp == 14){
				art = "Bestätigung einer Verlängerung: ";
			}
			ret[0] = String.valueOf("Datum der "+art+datum);
			return ret;
		}
		if(zeile.startsWith("DTM+48:")){
			String datum = teile[1].split(":")[1];
			if(teile[1].split(":")[2].equals("804")){
				ret[0] = String.valueOf("Bewilligte Tage: "+Integer.toString( IntegerTools.trailNullAndRetInt(datum)/7*5 ));
				dbHmap.put("tage",Integer.toString( IntegerTools.trailNullAndRetInt(datum)/7*5 ));
			}else if(teile[1].split(":")[2].equals("102")){
				ret[0] = String.valueOf("Bewilligte Tage: "+edifact_vec.get( ((Integer)ret[1])+1 ).split("\\+")[1].split(":")[1]  );
				dbHmap.put("tage","");
			}
			return ret;
		}
		if(zeile.startsWith("DTM+55:")){
			String datum = teile[1].split(":")[1];
			datum = datum.substring(6,8)+"."+datum.substring(4,6)+"."+datum.substring(0,4);
			ret[0] = String.valueOf("Verfall der Bewilligung: "+datum);
			return ret;
		}
		if(zeile.startsWith("DTM+329:")){
			try{
			String datum = teile[1].split(":")[1];
			datum = datum.substring(6,8)+"."+datum.substring(4,6)+"."+datum.substring(0,4);
			ret[0] = String.valueOf("Geburtsdatum: "+datum);
			dbHmap.put("geboren",DatFunk.sDatInSQL(datum));
			}catch(Exception ex){
				ex.printStackTrace();
			}
			return ret;
		}
		if(zeile.startsWith("NAT+2+")){
			if(teile.length < 3){ret[0] = "Nationalität: D"; return ret;}
			ret[0] = String.valueOf("Nationalität: "+teile[2]);
			dbHmap.put("national",zeile);
			return ret;
		}
		if(zeile.startsWith("IMD+")){
			ret[0] = String.valueOf(regleArtDerLeistung(teile[3]));
			dbHmap.put("artderleistung",zeile);
			return ret;
		}
		if(zeile.startsWith("PDI+")){
			String inhalt = regleGeschlecht(teile[1]);
			if(teile.length==3){
				inhalt = inhalt+"\n"+regleFamilienstand(teile[2]);
			}
			ret[0] = String.valueOf(inhalt);
			dbHmap.put("geschlechtfamstand",zeile);
			return ret;
		}
		/*****************************BEGINN PNA's****************************************/		
		//Sender
		if(zeile.startsWith("PNA+MS")){
			ret[0] = String.valueOf("Sender-IK: "+teile[3]);
			dbHmap.put("sender",teile[3]);
			setPnaActive(teile[1]);
			return ret;
		}
		//Receiver
		if(zeile.startsWith("PNA+MR")){
			ret[0] = String.valueOf("Empfänger-IK: "+teile[3]);
			setPnaActive(teile[1]);
			return ret;
		}
		//Kostenträger
		if(zeile.startsWith("PNA+BY++")){
			ret[0] = "\n"+String.valueOf("Kostenträger-IK: "+teile[3]);
			if(teile.length==8){
				ret[0] = String.valueOf(ret[0]+"\n"+teile[6].split(":")[1]+"\n"+teile[7].split(":")[1]);
			}else if(teile.length==7){
				ret[0] = String.valueOf(ret[0]+"\n"+teile[6].split(":")[1]);
			}
			dbHmap.put("ktraeger",teile[3]);
			setPnaActive(teile[1]);
			return ret;
		}
		//Klinik
		if(zeile.startsWith("PNA+MT")){
			dbHmap.put("klinik",zeile);
			ret[0] = String.valueOf("Klinik-IK: "+teile[3]);
			dbHmap.put("klinik",String.valueOf(teile[3]));
			setPnaActive(teile[1]);
			return ret;
		}
		//Krankenkasse
		if(zeile.startsWith("PNA+KK")){
			ret[0] = "\n"+String.valueOf("Krankenkasse-IK: "+teile[3]);
			dbHmap.put("ikkasse",teile[3]);
			if(teile.length==8){
				ret[0] = String.valueOf(ret[0]+"\n"+
						(teile[6].split(":").length >= 2 ? teile[6].split(":")[1]+"\n" : "")+
						(teile[7].split(":").length >= 2 ? teile[7].split(":")[1]+"\n" : ""));

				
			}else if(teile.length==7){
				ret[0] = String.valueOf(ret[0]+"\n"+
						(teile[6].split(":").length >= 2 ? teile[6].split(":")[1]+"\n" : ""));
			}
			setPnaActive(teile[1]);
			return ret;
		}
		//Patient !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*******************
		//***********************************************************
		if(zeile.startsWith("PNA+BM")){
			/*
			//System.out.println("Zeileninhalt = "+zeile);
			for(int ii = 0; ii < teile.length;ii++){
				//System.out.println("Teilstück "+ii+" = "+teile[ii]);
			}
			*/

			String inhalt = "";
			if(!teile[2].trim().equals("")){
				inhalt = "\n"+String.valueOf("KV-Nummer: "+teile[2]);
				ret[0] = inhalt;
			}
			if(!teile[3].trim().equals("")){
				inhalt = inhalt + "\n"+String.valueOf("IK der Krankenkasse: "+teile[3]);
				ret[0] = inhalt;	
			}
			if(!teile[4].trim().equals("")){
				inhalt = inhalt + "\nAnrede: "+	regleAnrede(teile[4]);
				dbHmap.put("anrede",regleAnrede(teile[4]));
				ret[0] = inhalt;
			}
			if(!teile[6].trim().equals("")){
				inhalt = inhalt + "\nNachname: "+	teile[6].split(":")[1];
				dbHmap.put("nachname",teile[6].split(":")[1]);
				ret[0] = inhalt;
			}
			if(!teile[7].trim().equals("")){
				inhalt = inhalt + "\nVorname: "+	teile[7].split(":")[1];
				dbHmap.put("vorname",teile[7].split(":")[1]);
				ret[0] = inhalt;
			}
			if(teile.length <= 8){return ret;}
			if(!teile[8].trim().equals("")){ //A:
				inhalt = inhalt + "\nNamenszusatz: "+	teile[8].split(":")[1];
				ret[0] = inhalt;
			}
			if(teile.length <= 9){return ret;}
			if(!teile[9].trim().equals("")){//B:
				inhalt = inhalt + "\nTitel: "+	teile[9].split(":")[1];
				dbHmap.put("titel",teile[9].split(":")[1]);
				ret[0] = inhalt;
			}
			if(teile.length <= 10){return ret;}
			if(!teile[10].trim().equals("")){//C:
				inhalt = inhalt + "\nGeburtsname: "+	teile[10].split(":")[1];
				ret[0] = inhalt;
			}
			setPnaActive(teile[1]);
			// 0  1  2 3 4 5 6
			//PNA+BM+ + +1+ +1:Finckh+:Konrad'

			return ret;
		}
		//Beteiligte Institution: Beauftragte Stelle
		if(zeile.startsWith("PNA+AB")){
			ret[0] = "PNA+AB = Beteiligte Institution: Beauftragte Stelle noch nicht belegt!!!!!!!!!!!\n"+zeile;
			dbHmap.put("beauftragtestelle",teile[3]);
			setPnaActive(teile[1]);
			return ret;
		}
		//Beteiligte Institution: Veranlassendes Krankenhaus
		if(zeile.startsWith("PNA+RP")){
			ret[0] = "PNA+RP = Beteiligte Institution: Veranlassendes Krankenhaus noch nicht belegt!!!!!!!!!!!\n"+zeile;
			setPnaActive(teile[1]);
			return ret;
		}
		//Beteiligte Institution: Einweisender Arzt
		if(zeile.startsWith("PNA+OP")){
			ret[0] = "PNA+OP = Beteiligte Institution: Einweisender Arzt noch nicht belegt!!!!!!!!!!!\n"+zeile;
			setPnaActive(teile[1]);
			return ret;
		}
		//Beteiligte Institution: Anspruchsberechtigter
		if(zeile.startsWith("PNA+BR")){
			ret[0] = "PNA+BR = Beteiligte Institution: Anspruchsberechtigter noch nicht belegt!!!!!!!!!!!\n"+zeile;
			setPnaActive(teile[1]);
			return ret;
		}
		//Mitteilungsempfänger
		if(zeile.startsWith("PNA+ME")){
			ret[0] = "PNA+ME = Mitteilungsempfänger noch nicht belegt!!!!!!!!!!!\n"+zeile;
			setPnaActive(teile[1]);
			return ret;
		}
		//Nächster Angehöriger
		if(zeile.startsWith("PNA+NA")){
			ret[0] = "PNA+NA = Nächster Angehöriger noch nicht belegt!!!!!!!!!!!\n"+zeile;
			setPnaActive(teile[1]);
			return ret;
		}
		//Hausarzt
		if(zeile.startsWith("PNA+HA")){
			String pnaha = "";
			ret[0] = "Hausarzt = "+zeile;
			setPnaActive(teile[1]);
			try{
				pnaha = (teile[6].split(":").length > 0 ? teile[6].split(":")[1] : "");
				pnaha = pnaha + (teile.length >= 8 ? " "+teile[7].split(":")[1] : "");
			}catch(Exception ex){
				JOptionPane.showMessageDialog(null, "Fehler bei Hausarzt = \n\n"+zeile+"\n");
			}
			dbHmap.put("haname", pnaha);
			return ret;
		}
		//Facharzt oder andere behandelnde Person
		if(zeile.startsWith("PNA+DR")){
			ret[0] = "PNA+DR = Facharzt oder andere behandelnde Person noch nicht belegt!!!!!!!!!!!\n"+zeile;
			setPnaActive(teile[1]);
			return ret;
		}
		//Arbeitgeber
		if(zeile.startsWith("PNA+AG")){
			ret[0] = "PNA+AG = Arbeitgeber noch nicht belegt!!!!!!!!!!!\n"+zeile;
			setPnaActive(teile[1]);
			return ret;
		}
		//Arbeitsamt
		if(zeile.startsWith("PNA+AA")){
			ret[0] = "PNA+AA = Arbeitsamt noch nicht belegt!!!!!!!!!!!\n"+zeile;
			setPnaActive(teile[1]);
			return ret;
		}
		//Gesundheitsamt (nur Bayern)
		if(zeile.startsWith("PNA+GA")){
			ret[0] = "PNA+GA = Gesundheitsamt (nur Bayern) noch nicht belegt!!!!!!!!!!!\n"+zeile;
			return ret;
		}
		//Suchtberatung
		if(zeile.startsWith("PNA+SB")){
			ret[0] = "PNA+SB = Suchtberatung noch nicht belegt!!!!!!!!!!!\n"+zeile;
			return ret;
		}
		/*****************************ENDE PNA's****************************************/
		if(zeile.startsWith("PRC+")){
			ret[0] = "Kennzeichnung der Folgedaten: "+teile[1]+" = "+(bewilligung ? "Bewilligung" : "Ablehnung!!!");
			return ret;
		}
		if(zeile.startsWith("RCS+12")){
			ret[0] = String.valueOf("Ergänzende Angaben: "+regleRCS(teile[2]));
			return ret;
		}
		if(zeile.startsWith("RFF+ACD:")){
			ret[0] = String.valueOf("ACD-laufende Nummer des Geschäftsvorfalles: "+teile[1].split(":")[1]);
			return ret;
		}
		if(zeile.startsWith("RFF+AGU:")){
			ret[0] = String.valueOf("AGU-Versicherungsnummer Patient: "+teile[1].split(":")[1]);
			dbHmap.put("versicherungsnr",teile[1].split(":")[1]);
			return ret;
		}
		if(zeile.startsWith("RFF+AGF:")){
			ret[0] = String.valueOf("Berechtigten Nummer: "+teile[1].split(":")[1]);
			dbHmap.put("berechtigtennr",teile[1].split(":")[1]);
			return ret;
		}
		if(zeile.startsWith("RFF+ADE:")){
			ret[0] = String.valueOf("Maßnahmennummer: "+teile[1].split(":")[1]);
			dbHmap.put("massnahmennr",teile[1].split(":")[1]);
			return ret;
		}
		if(zeile.startsWith("RFF+AEN:")){
			ret[0] = String.valueOf("Versicherungsnummer des Anspruchsberechtigten: "+teile[1].split(":")[1]);
			dbHmap.put("vnranspruchsberechtigter",teile[1].split(":")[1]);
			return ret;
		}
		if(zeile.startsWith("RFF+FI:")){
			ret[0] = String.valueOf("Aktenzeichen: "+teile[1].split(":")[1]);
			dbHmap.put("aktenzeichen",teile[1].split(":")[1]);
			return ret;
		}
		if(zeile.startsWith("RFF+JB:")){
			ret[0] = String.valueOf("Berufsgruppenschlüssel: "+teile[1].split(":")[1]);
			return ret;
		}
		if(zeile.startsWith("FTX+TXT+++B:")){
			test = String.valueOf(teile[4].split(":")[1]).trim();
			if(erstertext){
				//test = String.valueOf(teile[4].split(":")[1]);
				ret[0] = "\n"+test+
						(teile[4].split(":").length > 2 ? teile[4].split(":")[2] :
							"").trim();
				erstertext = false;
				return ret;
			}else{
				//test = String.valueOf(teile[4].split(":")[1]);
				ret[0] = test+
						(teile[4].split(":").length > 2 ? teile[4].split(":")[2] :
							"").trim();
			}
			//ret[0] = String.valueOf(teile[4].split(":")[1]);
		}
		return ret;
	}
	/****************************************/
	private String regleAnrede(String wert){
		String ret = "Geschlecht unbekannt";
		if(wert.equals("1")){
			return ("Herr");
		}
		if(wert.equals("2")){
			return ("Frau");
		}
		if(wert.equals("3")){
			return ("Institution");
		}
		if(wert.equals("4")){
			return ("Fräulein");
		}
		return ret;
	}
	/****************************************/
	private String regleBGM(String wert){
		String ret = "unbekannter Anlaß der Nachricht";
		if(wert.equals("11")){
			bewilligung= true;
			return String.valueOf("11 - Bewilligung einer Leistung");
		}else if(wert.equals("12")){
			bewilligung = false;
			return String.valueOf("18 - Absage einer Leistung");
		}else if(wert.equals("18")){
			bewilligung = false;
			return String.valueOf("18 - Absage einer Leistung");
		}else if(wert.equals("15")){
				bewilligung = false;
				return String.valueOf("15 - Bestätigung einer Verlängerung");
		}else if(wert.equals("50")){
			bewilligung= true;
			auftragsleistung = true;
			return String.valueOf("50 - Bewilligung einer Leistung als Auftragsleistung");
		}
		return ret;
	}
	/****************************************/
	private String regleFCA(String wert){
		String ret = "Inhalt der Datenlieferung - unbekannt";
		if(wert.equals("AD")){
			return "AD - Administrationsdaten";
		}
		if(wert.equals("BD")){
			return "BD - Bewilligungsdaten";
		}
		if(wert.equals("MD")){
			return "MD - Maßnahmedaten";
		}
		if(wert.equals("RD")){
			return "RD - Rechnungs-/Zahlungsdaten";
		}

		return ret;
	}
	/****************************************/
	private String regleBY(String wert){
		int iwert = StringTools.holeZahlVorneNullen(wert);
		String retwert = "BY - Kostenträger unbekannt";
		switch(iwert){
			case 1:
				retwert = "1 - Krankenkasse ist Kostenträger";
				break;
			case 2:
				retwert = "2 - Krankenkasse ist Kostenträger und Auftraggeber";
				break;
			case 3:
				retwert = "3 - RV-Träger ist Kostenträger";
				break;
			case 4:
				retwert = "4 - RV-Träger ist Kostenträger und Auftraggeber";
				break;
		}
		return retwert; 
	}
	private String regleGeschlecht(String wert){
		String ret = "Geschlecht - unbekannt";
		if(wert.equals("1")){
			return "Geschlecht - männlich";
		}
		if(wert.equals("2")){
			return "Geschlecht - weiblich";
		}
		return ret;
	}
	
	private String regleArtDerLeistung(String wert){
		String ret = "Art der Leistung - unbekannt";
		String codeliste = wert.split(":")[1];
		String inhalt = wert.split(":")[0];
		if(codeliste.equals("B11")){
			if(inhalt.equals("1")){
				return "Art der Leistung: stationär";
			}
			if(inhalt.equals("2")){
				return "Art der Leistung: ganztägig ambulant";
			}
			if(inhalt.equals("3")){
				return "Art der Leistung: ambulant (nur RV)";
			}
		}
		if(codeliste.equals("B09")){
			if(inhalt.equals("01")){
				return "Indikationsgruppe - 01 = Krankheiten des Herzens und des Kreislaufs";
			}
			if(inhalt.equals("02")){
				return "Indikationsgruppe - 02 = Krankheiten der Gefäße";
			}
			if(inhalt.equals("03")){
				return "Indikationsgruppe - 03 = Entzündliche rheumatische Erkrankungen";
			}
			if(inhalt.equals("04")){
				return "Indikationsgruppe - 04 = Degenerative rheumatische Krankheiten (orthop.)";
			}
			if(inhalt.equals("09")){
				return "Indikationsgruppe - 09 = Neurologische Erkrankungen";
			}
		}
		return ret;
	}
	
	private String regleFamilienstand(String wert){
		String ret = "Familienstand - unbekannt";
		if(wert.equals("1")){
			return "Familienstand - ledig";
		}
		if(wert.equals("2")){
			return "Familienstand - verheiratet";
		}
		if(wert.equals("3")){
			return "Familienstand - geschieden";
		}
		if(wert.equals("5")){
			return "Familienstand - verwitwet";
		}
		return ret;
	}
	
	/****************************************/	
	/****************************************/
	private String regleRCS(String wert){
		String ret = "A... Wert unbekannt";
		String art = wert.split(":")[1];
		String index = wert.split(":")[0];
		if(art.equals("A01")){
			if(index.equals("1"))
				return "A01 - 1 - Anreise mit öffentliche Verkehrsmittel, Patient löst Fahrkarte selbst";
			if(index.equals("2"))
				return "A01 - 2 - Anreise mit öffentliche Verkehrsmittel, Fahrkarte wird gestellt";
			if(index.equals("3"))
				return "A01 - 3 - Anreise mit PKW";
			if(index.equals("4"))
				return "A01 - 4 - Anreise mit Krankentrasport / Taxi";
			if(index.equals("5"))
				return "A01 - 5 - Anreise mit Flugzeug";
			if(index.equals("6"))
				return "A01 - 6 - Anreise mit Sammelanreise (Kinder)";
		}
		if(art.equals("A02")){
			if(index.equals("11"))
				return "A02 - 11 - Normale medizinische Reha einschl. RPK";
			if(index.equals("12"))
				return "A02 - 12 - Entwöhnungsbehandlung (stationäre Suchtleistung)";
			if(index.equals("13"))
				return "A02 - 13 - CA-Leistung für den Versicherten";
			if(index.equals("21"))
				return "A02 - 21 - Medizinische Leistung für nicht versicherte Erwachsene";
			if(index.equals("22"))
				return "A02 - 22 - Kinderheilbehandlung";
			if(index.equals("31"))
				return "A02 - 31 - ambulante und/oder ganztägig ambulante medizinische Reha gem. § 15 SGB VI";
			if(index.equals("32"))
				return "A02 - 32 - Sonstige Leistungen gemäß § 31 Abs. 1 Nr. 1 SGB VI";
			if(index.equals("33"))
				return "A02 - 33 - Sonstige Leistungen gemäß § 31 Abs. 1 Nr. 2 SGB VI";
			if(index.equals("99"))
				return "A02 - 99 - Auftagsheilbehandlung";
		}
		if(art.equals("A03")){
			if(index.equals("1"))
				return "A03 - 1 - Keine Suchterkrankung bekannt";
			if(index.equals("2"))
				return "A03 - 2 - Alkoholabhängigkeit";
			if(index.equals("3"))
				return "A03 - 3 - Medikamentenabhängigkeit";
			if(index.equals("4"))
				return "A03 - 4 - Drogenabhängigkeit";
			if(index.equals("5"))
				return "A03 - 5 - Mehrfachabhängigkeit";
			if(index.equals("6"))
				return "A03 - 6 - Spielsucht";
			if(index.equals("7"))
				return "A03 - 7 - Eßstörungen";
		}
		if(art.equals("A04")){
			if(index.equals("1"))
				return "A04 - 1 - Keine Auftragsleistung";
			if(index.equals("2"))
				return "A04 - 2 - Vollständige Fallabwicklung durch beauftragte Stelle";
			if(index.equals("3"))
				return "A04 - 3 - Bewilligung durch beauftragte Stelle, weitere Abwicklung durch Klinik";
		}	
		if(art.equals("A05")){
			if(index.equals("1"))
				return "A05 - 1 - Anreise keine Begleitperson";
			if(index.equals("2"))
				return "A05 - 2 - Anreise Begleitperson (Erwachsene)";
			if(index.equals("3"))
				return "A05 - 3 - Anreise Begleitperson (Kind)";
			if(index.equals("4"))
				return "A05 - 4 - Anreise Begleitperson Ehepartner/Lebenspartner";
			if(index.equals("9"))
				return "A05 - 9 - Anreise mehr als eine Begleitperson (Familienkur)";
		}	
		if(art.equals("A06")){
			if(index.equals("1"))
				return "A06 - 1 - Rückreise keine Begleitperson";
			if(index.equals("2"))
				return "A06 - 2 - Rückreise Begleitperson (Erwachsene)";
			if(index.equals("3"))
				return "A06 - 3 - Rückreise Begleitperson (Kind)";
			if(index.equals("4"))
				return "A06 - 4 - Rückreise Begleitperson Ehepartner/Lebenspartner";
			if(index.equals("9"))
				return "A06 - 9 - Rückreise mehr als eine Begleitperson (Familienkur)";
		}	
		if(art.equals("A07")){
			if(index.equals("1"))
				return "A07 - 1 - Aufenthalt keine Begleitperson";
			if(index.equals("2"))
				return "A07 - 2 - Aufenthalt Begleitperson (Erwachsene)";
			if(index.equals("3"))
				return "A07 - 3 - Aufenthalt Begleitperson (Kind)";
			if(index.equals("4"))
				return "A07 - 4 - Aufenthalt Begleitperson Ehepartner/Lebenspartner";
			if(index.equals("9"))
				return "A07 - 9 - Aufenthalt mehr als eine Begleitperson (Familienkur)";
		}	
		if(art.equals("A08")){
			if(index.equals("01"))
				return "A08 - 01 - Keine Schwerbehinderung bekannt";
			if(index.equals("02"))
				return "A08 - 02 - Geistige Behinderung";
			if(index.equals("03"))
				return "A08 - 03 - Anfallskrankheit";
			if(index.equals("04"))
				return "A08 - 04 - Blindheit";
			if(index.equals("05"))
				return "A08 - 05 - Gehbehinderung";
			if(index.equals("06"))
				return "A08 - 06 - Querschnittslähmung";
			if(index.equals("07"))
				return "A08 - 07 - Rollstuhlfahrer/in";
			if(index.equals("08"))
				return "A08 - 08 - Taubstummheit";
			if(index.equals("09"))
				return "A08 - 09 - Mehrfachbehinderung";
			if(index.equals("10"))
				return "A08 - 10 - Nicht näher sepzifiz. Schwerbehinderung";

		}	
		if(art.equals("A09")){
			if(index.equals("J")){
				{dbHmap.put("a09", "Eilfall"); return "A09 - J - Eilfall = JA";}
			}
			if(index.equals("N"))
			{dbHmap.put("a09", "Normalfall");return "A09 - N - Eilfall = NEIN";}
		}
		if(art.equals("A10")){
			if(index.equals("1"))
				return "A10 - 1 - Entgeldkategorie = Vergütung";
			if(index.equals("2"))
				return "A10 - 2 - Entgeldkategorie = Zuschuß";
			if(index.equals("3"))
				return "A10 - 3 - Entgeldkategorie = Fallpauschale";
		}
		if(art.equals("A11")){
			if(index.equals("00"))
				return "A11 - 00 - Maßnahmenart = Noch keine Angaben möglich";
			if(index.equals("10"))
				return "A11 - 10 - Maßnahmenart = Normale medizinische Heilbehandlung";
			if(index.equals("21"))
				return "A11 - 21 - Maßnahmenart = Normale Leistung wegen psychischer Erkrankung";
			if(index.equals("99"))
				return "A11 - 99 - Maßnahmenart = Auftragsheilbehandlung";
		}
		if(art.equals("A12")){
			if(index.equals("71"))
				{dbHmap.put("a12", "AR");return "A12 - 71 - Verfahrensart = Anschlußrehabilitation";}
			if(index.equals("72"))
				{dbHmap.put("a12", "MED");return "A12 - 72 - Verfahrensart = Wiederholungsheilbehandlung";}
			if(index.equals("73"))
				{dbHmap.put("a12", "MED");return "A12 - 73 - Verfahrensart = Vorleistung nach § 6 Abs.2 RehaAnglG";}
			if(index.equals("74"))
				{dbHmap.put("a12", "MED");return "A12 - 74 - Verfahrensart = Rehabehandlung in mehreren Behandl.abschnitten";}
			if(index.equals("75"))
				{dbHmap.put("a12", "MED");return "A12 - 75 - Verfahrensart = Erstattungsfall";}
			if(index.equals("76"))
				{dbHmap.put("a12", "MED");return "A12 - 76 - Verfahrensart = Verfahren nach § 51 SGB V, § 105a AFG";}
			if(index.equals("77"))
				{dbHmap.put("a12", "MED");return "A12 - 77 - Verfahrensart = Reha-Leistung aus dem Rentenverfahren";}
			if(index.equals("78"))
				{dbHmap.put("a12", "MED");return "A12 - 78 - Verfahrensart = Reha-Leistung nach Rechtsbehelf";}
			if(index.equals("79"))
				{dbHmap.put("a12", "MED");return "A12 - 79 - Verfahrensart = Normales Reha-Verfahren";}
			if(index.equals("80"))
				{dbHmap.put("a12", "REHASPORT");return "A12 - 80 - Verfahrensart = Rehabilitationssport nach §44 Abs.1 Nr.3 SGB IX";}
			if(index.equals("81"))
				{dbHmap.put("a12", "FUNKTIONSTRAINING");return "A12 - 81 - Verfahrensart = Funktionstraining nach §44 Abs.1 Nr.4 SGB IX";}
			if(index.equals("82"))
				{dbHmap.put("a12", "NACHSORGE");return "A12 - 82 - Verfahrensart = Nachsorgeleistung nach §15 SGB IV";}
		}	
		if(art.equals("A13")){
			if(index.equals("1"))
				return "A13 - 1 - Keine Zuzahlung";
			if(index.equals("2"))
				return "A13 - 2 - Zuzahlung soll von der Klinik eingezogen werden";
			if(index.equals("3"))
				return "A13 - 3 - Zuzahlung soll von der Klinik nicht(!!) eingezogen werden";
		}
		if(art.equals("A14")){
			if(index.equals("J"))
				return "A14 - J - Zwischenrechnung erlaubt = JA";
			if(index.equals("N"))
				return "A14 - N - Zwischenrechnung erlaubt = NEIN";
		}
		return ret;
	}	
	/****************************************/
	private String doUebersetzen(String zeile){
		String ret = zeile;
		ret = ret.replace("}","ü").replace("{","ä").replace("|","ö").replace("~","ß");
		//ret = ret.replace("{","ä");
		//ret = ret.replace("~","ß");
		return ret;
	}
	
	private String dateiDialog(String pfad){
		String sret = "";
		final JFileChooser chooser = new JFileChooser("Auftragsdatei .AUF-Datei wählen");
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        final File file = new File(pfad);

        chooser.setCurrentDirectory(file);

        chooser.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)
                        || e.getPropertyName().equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
                    //final File f = (File) e.getNewValue();
                }
            }

        });
        chooser.setVisible(true);
        
        final int result = chooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File inputVerzFile = chooser.getSelectedFile();
            //String inputVerzStr = inputVerzFile.getPath();
            

            if(inputVerzFile.getName().trim().equals("")){
            	sret = "";
            }else{
            	//sret = inputVerzFile.getName().trim();
            	sret = inputVerzFile.getPath().toString();	
            }
        }else{
        	sret = ""; //vorlagenname.setText(SystemConfig.oTerminListe.NameTemplate);
        }
        chooser.setVisible(false); 

        return sret;
	}
	
	private boolean testeAuftragsDatei(String pfad,String datei){
		boolean bret = true;
		String auftragsdatei = null;
		try {
			auftragsdatei = new String(BytesFromFile(new File(pfad)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(auftragsdatei.getBytes().length > 348){
			JOptionPane.showMessageDialog(null, "Der Aufbau dieser Auftragsdatei ist nicht korrekt");
			return false;
		}
		// 0 = Sender, 1 = Sender, 2 = Empfänger mit Entschl., 3 = Physik Empfänger, 4 = Originalgr.
		// 5 = Encoded-Größe, 6 = log.Dateiname, 7 = physik. Dateiname
		//Object[] decodeparms = {null,null,null,null,null,null,null};
		decodeparms[7] = (String) auftragsdatei.substring(19,19+8);
		decodeparms[0] = (String) auftragsdatei.substring(32,32+15).trim();
		decodeparms[1] = (String) auftragsdatei.substring(47,47+15).trim();
		decodeparms[2] = (String) auftragsdatei.substring(62,62+15).trim();
		decodeparms[3] = (String) auftragsdatei.substring(77,77+15).trim();
		decodeparms[4] = (Integer) IntegerTools.trailNullAndRetInt(auftragsdatei.substring(178,178+12));
		decodeparms[5] = (Integer) IntegerTools.trailNullAndRetInt(auftragsdatei.substring(190,190+12));
		decodeparms[6] = (String) auftragsdatei.substring(104,104+25).trim();
		/*
		for(int i = 0; i < 8;i++){
			//System.out.println("Eingelesene Parameter = "+Integer.toString(i)+" "+decodeparms[i]);
		}
		*/

		/*
		if(! datei.toUpperCase().startsWith((String)decodeparms[7])){
			JOptionPane.showMessageDialog(null, "Dateiname der Auftragsdatei und der Dateiname innehalb der Datei stimmen nicht überein");
			return false;
		}
		*/
		////System.out.println("Dateiname in der Auftragsdatei = "+(String)decodeparms[7]);
		return bret;
	}
	 public static byte[] BytesFromFile(File file) throws IOException {
	        InputStream is = new FileInputStream(file);
	        long length = file.length();
	    
	        if (length > Integer.MAX_VALUE) {
	        	//System.out.println("Datei zu groß zum einlesen");
	        	return null;
	        }

	        byte[] bytes = new byte[(int)length];
	        int offset = 0;
	        int numRead = 0;
	        while (offset < bytes.length && (numRead=is.read(bytes, 
	                    offset, bytes.length-offset)) >= 0) {
	            offset += numRead;
	        }
	        if (offset < bytes.length) {
	            throw new IOException("Datei konnte nicht komplett gelesen werden "
	                                + file.getName());
	        }
	        is.close();
	        return bytes;
	}
	private boolean doEntschluesseln(String xpfad,String xdatei) throws NebraskaCryptoException, NebraskaNotInitializedException, NebraskaFileException, IOException{
		boolean bret = true;
		// 0 = Sender, 1 = Sender, 2 = Empfänger mit Entschl., 3 = Physik Empfänger, 4 = Originalgr.
		// 5 = Encoded-Größe, 6 = log.Dateiname, 7 = physik. Dateiname
		//decodeparms
		
		//Ab hier testen ob etwas zum Entschlüsseln vorhanden ist.
		 
		File dir = new File(Reha301.inbox);
		File[] files = dir.listFiles();
		String vergleich = auftragfile.getName().substring(0,auftragfile.getName().indexOf("."));
		////System.out.println("der Vergleich lautet "+vergleich);
		String  encdatei = "";
		for(int i = 0; i < files.length;i++){
			if( (files[i].getName().startsWith(vergleich)) && 
			(! files[i].getName().equals(auftragfile.getName())) && 
			(! files[i].getName().toLowerCase().endsWith(".org"))){
				encdatei = files[i].getName();
				break;
			}
			
		}
		if(encdatei.equals("")){
			JOptionPane.showMessageDialog(null, "Keine Datei für die Entschlüsselung gefunden");
			return false;
		}

		String inipath = Reha301.progHome+"nebraska_windows.conf";
		INIFile file = new INIFile(inipath);
		int anzahl = file.getIntegerProperty("KeyStores", "KeyStoreAnzahl");
		////System.out.println("\n\n\nAnzahl Keystores = "+anzahl);
		String kstorefile=null;String kstorealias=null;String kstorepw=null; 
		for(int i = 1; i <= anzahl;i++){
			/*
			//System.out.println("\nAlias im Store = "+file.getStringProperty("KeyStores", "KeyStoreAlias"+Integer.toString(i)));
			//System.out.println("Gesuchter Alias = "+"IK"+((String)decodeparms[2]));
			//System.out.println("Sind identisch ="+file.getStringProperty("KeyStores", "KeyStoreAlias"+Integer.toString(i)).equals("IK"+((String)decodeparms[2]).trim() ));
			*/
			if(file.getStringProperty("KeyStores", "KeyStoreAlias"+Integer.toString(i)).equals("IK"+((String)decodeparms[2]).trim() )){
				kstorefile = file.getStringProperty("KeyStores","KeyStoreFile"+Integer.toString(i));
								
				String pw = String.valueOf(file.getStringProperty("KeyStores","KeyStorePw"+Integer.toString(i)));
				Verschluesseln man = Verschluesseln.getInstance();
				man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
				kstorepw = man.decrypt (pw);
				
				kstorealias =  file.getStringProperty("KeyStores","KeyStoreAlias"+Integer.toString(i));
				break;
			}
		}
		/*
		//System.out.println("KeystoreFile = "+kstorefile);
		//System.out.println("KeystorePw   = "+kstorepw);
		//System.out.println("KeystoreAlias = "+kstorealias);
		*/
		NebraskaKeystore keystore = 
			new NebraskaKeystore(kstorefile,
						kstorepw,
						"abc",
						kstorealias);
		//String filein = "";//pfad.substring(0,pfad.length()-4);
		////System.out.println("FileIn = "+"*"+filein+"*");
		
		NebraskaDecryptor decrypt = keystore.getDecryptor();
		FileInputStream fin = new FileInputStream(Reha301.inbox+encdatei);
		FileOutputStream fout = new FileOutputStream(Reha301.inbox+vergleich.trim()+".org");
		decrypt.decrypt(fin, fout);
		fin.close();
		fout.flush();
		fout.close();
		decodedfile = String.valueOf(Reha301.inbox+vergleich.trim()+".org");
		return bret;
	}
}
