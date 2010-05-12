package systemEinstellungen;





import hauptFenster.Reha;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import systemTools.Verschluesseln;
import terminKalender.DatFunk;
import terminKalender.ParameterLaden;
import terminKalender.ZeitFunk;


public class SystemConfig {
 
	public static Vector<ArrayList<String>> vDatenBank;
	public static Vector<ArrayList<String>> vSystemKollegen;
	public static Vector<String> vComboKollegen;
	public static String  aktJahr = "";
	public static String  vorJahr = "";
	public static Vector<String> vKalenderSet;
	public static Vector<Object> vKalenderFarben;
	public static ArrayList<String> aHauptFenster;
	//
	public static Vector<Vector<Color[]>> hmDefaultCols;
	public static Vector<Vector<Color[]>> vSysColsObject;
	public static Vector<String> vSysColsNamen;
	public static Vector<String> vSysColsBedeut;	
	public static Vector<String> vSysColsCode;	
	public static Vector<String> vSysDefNamen;

	public static Vector vSysColDlg;
	public static HashMap<String,Color[]> aktTkCol;
	public static boolean[] RoogleTage = {false,false,false,false,false,false,false};
	public static int RoogleZeitraum;
	public static HashMap<String,String> RoogleZeiten = null;
	/**
	 * nachfolgende static's sind notwendig f�r den Einsatz des Terminkalenders
	 */
	public static ArrayList<ArrayList<ArrayList<String[]>>> aTerminKalender;
	public static int AnzahlKollegen;
	public static Color KalenderHintergrund = null;
	public static boolean KalenderBarcode = false;
	public static String[]  KalenderUmfang =  {null,null};
	public static long[]  KalenderMilli =  {0,0};
	public static int UpdateIntervall;
	public static float KalenderAlpha = 0.0f;
	
	public static ArrayList<ArrayList<ArrayList<String[]>>> aRoogleGruppen;
	
	public static String OpenOfficePfad = null;
	public static String OpenOfficeNativePfad = null;
	private static INIFile ini; 
	private static INIFile colini;
	public static java.net.InetAddress dieseMaschine = null;
	public static String PDFformularPfad;
	public static String wissenURL = null;
	public static String homeDir = null;
	public static String homePageURL = null;
	public static int TerminUeberlappung = 1;
	public static TerminListe oTerminListe = null;
	public static GruppenEinlesen oGruppen = null;
		
	public static HashMap<String,String> hmEmailExtern;
	public static HashMap<String,String> hmEmailIntern;	
	public static HashMap<String,String> hmVerzeichnisse  = null;
	public static HashMap<String,String> hmFirmenDaten  = null;

	public static HashMap<String,Vector> hmDBMandant  = null;
	public static Vector<String[]>Mandanten = null;
	public static Vector<String[]>DBTypen = null;	
	public static int AuswahlImmerZeigen;
	public static int DefaultMandant;		

	public static Vector<ArrayList<String>>InetSeiten = null;
	public static String HilfeServer = null;
	public static boolean HilfeServerIstDatenServer;
	public static HashMap <String,String> hmHilfeServer;
	
	public static HashMap<String,String> hmAdrKDaten = null;
	public static HashMap<String,String> hmAdrADaten = null;
	public static HashMap<String,String> hmAdrPDaten = null;
	public static HashMap<String,String> hmAdrRDaten = null;
	public static HashMap<String,String> hmAdrBDaten = null;
	public static HashMap<String,String> hmAdrAFRDaten = null;
	public static HashMap<String,String> hmAdrHMRDaten = null;
	public static HashMap<String,String> hmEBerichtDaten = new HashMap<String,String>();
	/*
	public static List<String> lAdrKDaten = null;
	public static List<String> lAdrADaten = null;
	public static List<String> lAdrPDaten = null;
	public static List<String> lAdrRDaten = null;
	*/
	public static HashMap<String,Integer> hmContainer = null;
	
	/*
	public static Vector<String> vPreisGruppen;
	public static Vector<Integer> vHMRAbrechnung;
	public static Vector<Integer> vZuzahlRegeln;
	public static Vector<String> vPreisGueltig;
	public static Vector <Vector<String>> vNeuePreiseAb;
	public static Vector<Vector<Integer>> vNeuePreiseRegel;
	public static Vector<Vector<String>> vHBRegeln;
	public static Vector<String> vBerichtRegeln;
	*/
	
	public static Vector<String> vPatMerker = null;
	public static Vector<ImageIcon> vPatMerkerIcon = null;
	
	public static HashMap<String,String> hmKVKDaten = null;
	public static String sReaderName = null;
	public static String sReaderAktiv = null;
	public static String sReaderCom = null;
	public static String sDokuScanner = null;
	public static String sBarcodeScanner = null;
	public static String sBarcodeAktiv = null;
	public static String sBarcodeCom = null;
	public static String[] arztGruppen = null;
	public static String[] rezeptKlassen = null;
	public static Vector<Vector<String>> rezeptKlassenAktiv = null;
	public static String initRezeptKlasse = null;
	public static String rezGebVorlageNeu = null;
	public static String rezGebVorlageAlt = null;
	public static String rezGebVorlageHB = null;
	public static boolean rezGebDirektDruck = false;
	public static String rezGebDrucker = null;
	public static String rezBarcodeDrucker = null;
	public static HashMap<String,String> hmDokuScanner = null;
	public static HashMap<String,String[]> hmGeraete = null;
	
	public static HashMap<String,String> hmFremdProgs = null;
	public static Vector<Vector<String>> vFremdProgs = null;
	public static HashMap<String,String> hmCompany = null;
	
	public static String[] rezBarCodName = null;
	public static Vector<String>rezBarCodForm = null;
	
	public static HashMap<String,Vector> hmTherapBausteine = null; 
	public static String[] berichttitel = {null,null,null,null};
	public static String  thberichtdatei = "";
	public static HashMap<String,ImageIcon> hmSysIcons = null;
	
	public static Vector<String> vGutachtenEmpfaenger;
	public static Vector<String> vGutachtenIK;
	public static Vector<String> vGutachtenAbsAdresse;
	public static Vector<String> vGutachtenArzt;
	public static Vector<String> vGutachtenDisplay;
	public static String sGutachtenOrt;
	
	public static HashMap<String,String> hmAbrechnung = new HashMap<String,String>();
	                     
	public SystemConfig(){
	
	}
	public void SystemStart(String homedir){
		ini = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/rehajava.ini");
		aktJahr = ini.getStringProperty("SystemIntern","AktJahr");
		String jahrHeute = DatFunk.sHeute().substring(6);
		PDFformularPfad = ini.getStringProperty("Formulare","PDFFormularPfad");
		
		if(! aktJahr.equals(jahrHeute) ){
			JOptionPane.showMessageDialog(null, "Wichtiger Hinweis!!!!!\n\nDer letzte Programmstart war im Kalenderjahr -->"+aktJahr+"\n"+
					"Bitte fragen Sie den Administrator ob alle Befreiungen des Jahes "+aktJahr+" zurückgesetzt wurden\n"+
					"Beginnen Sie erst dann mit der Arbeit wenn sichergestellt ist daß alle Jahresabschlußarbeiten erledigt worden sind!!!!");
			System.out.println("Aktuelles Jahr wurde veränder auf "+jahrHeute);
			aktJahr = new String(jahrHeute);
			ini.setStringProperty("SystemIntern","AktJahr",jahrHeute,null);
			ini.save();
		}else{
			System.out.println("Aktuelles Jahr ist o.k.: "+jahrHeute);
		}
			
		try {
			dieseMaschine = java.net.InetAddress.getLocalHost();
		}
		catch (java.net.UnknownHostException uhe) {
			System.out.println(uhe);
		}
		vorJahr = new Integer(new Integer(aktJahr)-1).toString();
	}
	public void SystemInit(int i){
		switch(i){
		case 1:
			HauptFenster();	
			break;
		case 2:
			DatenBank();
			break;
		case 3:
			TerminKalender();
			break;
		case 4:
			EmailParameter();
			break;
		case 5:
			//EmailParameter();
			break;
		case 6:
			Verzeichnisse();
			break;
		case 7:
			RoogleGruppen();
			break;	
		case 8:
			NurSets();
			break;
		case 9:
			try{
				TKFarben();
			}catch(Exception ex){
				ex.printStackTrace();
			}
			break;
		case 10:
			GruppenLesen();
			break;
		case 11:
			MandantenEinlesen();
			break;


		}
		return;
	}
	private void DatenBank(){
		try{
		if (ini==null){
			ini = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/rehajava.ini");
			//ini = new INIFile("c:\\RehaVerwaltung\\ini\\rehajava.ini");
		}
		int lesen;
		int i;
		ArrayList<String> aKontakt;
		aKontakt = new ArrayList<String>();
		
		vDatenBank = new Vector<ArrayList<String>>();
		System.out.println("INI-Verzeichnis = "+Reha.proghome+"ini/"+Reha.aktIK+"/rehajava.ini");
		System.out.println("IniFile = "+ini.getFileName());
		System.out.println("Anzahl der Datenbanktreiber einlesen");
		lesen =  ini.getIntegerProperty("DatenBank","AnzahlConnections") ;
		//lesen =  Integer.parseInt(new String(ini.getStringProperty("DatenBank","AnzahlConnections")) );
		System.out.println("Anzahl der Datenbanktreiber = "+lesen);
		for (i=1;i<(lesen+1);i++){
			aKontakt.add(new String(ini.getStringProperty("DatenBank","DBTreiber"+i)) );
			aKontakt.add(new String(ini.getStringProperty("DatenBank","DBKontakt"+i)) );			
			aKontakt.add(new String(ini.getStringProperty("DatenBank","DBType"+i)) );
			String sbenutzer =new String(ini.getStringProperty("DatenBank","DBBenutzer"+i));
			//mandantDB.add(minif.getStringProperty("Application", "DBPasswort1"));
			aKontakt.add(new String(sbenutzer));
			String pw = new String(ini.getStringProperty("DatenBank","DBPasswort"+i));
			String decrypted = null;
			if(pw != null){
				Verschluesseln man = Verschluesseln.getInstance();
				man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
				decrypted = man.decrypt (pw);
			}else{
				decrypted = new String("");
			}
			aKontakt.add(decrypted);
			vDatenBank.add((ArrayList<String>) aKontakt.clone());
			aKontakt.clear();
		}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return;
	}

	private void HauptFenster(){
			if (ini==null){
				ini = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/rehajava.ini");
				//ini = new INIFile("c:\\RehaVerwaltung\\ini\\rehajava.ini");
			}	
			aHauptFenster = new ArrayList<String>();
			aHauptFenster.add(ini.getStringProperty("HauptFenster","Hintergrundbild"));
			aHauptFenster.add(ini.getStringProperty("HauptFenster","Bildgroesse"));			
			aHauptFenster.add(ini.getStringProperty("HauptFenster","FensterFarbeRGB"));
			aHauptFenster.add(ini.getStringProperty("HauptFenster","FensterTitel"));
			aHauptFenster.add(ini.getStringProperty("HauptFenster","LookAndFeel"));

			OpenOfficePfad = ini.getStringProperty("OpenOffice.org","OfficePfad");
			OpenOfficeNativePfad = ini.getStringProperty("OpenOffice.org","OfficeNativePfad");
			wissenURL = ini.getStringProperty("WWW-Services","RTA-Wissen");
			homePageURL = ini.getStringProperty("WWW-Services","HomePage");		
			homeDir = Reha.proghome;
			//homeDir = ini.getStringProperty("Application","HeimatVerzeichnis");
			System.out.println("HomeDir = "+homeDir);
			return;
	}
	
	private void TerminKalender(){
		if (ini==null){
			ini = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/rehajava.ini");			
			//ini = new INIFile("/RehaVerwaltung/ini/rehajava.ini");
			//ini = new INIFile("c:\\RehaVerwaltung\\ini\\rehajava.ini");
		}	
		//(TestFenster) eltern.splashText("Lade Kalenderparameter");
		aTerminKalender = new ArrayList<ArrayList<ArrayList<String[]>>>();
		ArrayList<String> aList1  = new ArrayList<String>();
		ArrayList<String[]> aList2 = new ArrayList<String[]>();
		ArrayList<ArrayList<ArrayList<String[]>>> aList3 = new ArrayList<ArrayList<ArrayList<String[]>>>();
		int lesen,i;
		lesen =  Integer.parseInt(new String(ini.getStringProperty("Kalender","AnzahlSets")) );
		for (i=1;i<(lesen+1);i++){
			aList1.add(new String(ini.getStringProperty("Kalender","NameSet"+i)) );
			aList2.add(new String(ini.getStringProperty("Kalender","FeldSet"+i)).split(",") );
			aList3.add((ArrayList)aList1.clone());
			aList3.add((ArrayList)aList2.clone());
			aTerminKalender.add((ArrayList)aList3.clone());
			aList1.clear();
			aList2.clear();
			aList3.clear();
		}	
		KalenderUmfang[0] = new String(ini.getStringProperty("Kalender","KalenderStart"));
		KalenderUmfang[1] = new String(ini.getStringProperty("Kalender","KalenderEnde"));	
		KalenderMilli[0] = ZeitFunk.MinutenSeitMitternacht(KalenderUmfang[0]);
		KalenderMilli[1] = ZeitFunk.MinutenSeitMitternacht(KalenderUmfang[1]);		
		KalenderBarcode =  (ini.getStringProperty("Kalender","KalenderBarcode").trim().equals("0") ? false : true );
		UpdateIntervall = new Integer(new String(ini.getStringProperty("Kalender","KalenderTimer")));
		ParameterLaden kolLad = new ParameterLaden();
		AnzahlKollegen = ParameterLaden.vKKollegen.size()-1;
		String s = new String(ini.getStringProperty("Kalender","KalenderHintergrundRGB"));
		String[] ss = s.split(",");
		KalenderHintergrund = new Color(Integer.parseInt(ss[0]),Integer.parseInt(ss[1]),Integer.parseInt(ss[2]));
		KalenderAlpha = new Float(new String(ini.getStringProperty("Kalender","KalenderHintergrundAlpha")));
		System.out.println("Anzal Kollegen = "+AnzahlKollegen);
		oTerminListe = new TerminListe().init();
		Reha.thisClass.setzeInitStand("Gruppendefinition einlesen");
		GruppenLesen();
		//oGruppen = new GruppenEinlesen().init();
		
		
		return;
	}
	public static void NurSets(){
		if (ini==null){
			ini = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/rehajava.ini");
			//ini = new INIFile("c:\\RehaVerwaltung\\ini\\rehajava.ini");
		}	
		//(TestFenster) eltern.splashText("Lade Kalenderparameter");
		aTerminKalender = new ArrayList<ArrayList<ArrayList<String[]>>>();
		aTerminKalender.clear();
		ArrayList<String> aList1  = new ArrayList<String>();
		ArrayList<String[]> aList2 = new ArrayList<String[]>();
		ArrayList<ArrayList<ArrayList<String[]>>> aList3 = new ArrayList<ArrayList<ArrayList<String[]>>>();
		int lesen,i;
		lesen =  Integer.parseInt(new String(ini.getStringProperty("Kalender","AnzahlSets")) );
		for (i=1;i<(lesen+1);i++){
			aList1.add(new String(ini.getStringProperty("Kalender","NameSet"+i)) );
			aList2.add(new String(ini.getStringProperty("Kalender","FeldSet"+i)).split(",") );
			aList3.add((ArrayList)aList1.clone());
			aList3.add((ArrayList)aList2.clone());
			aTerminKalender.add((ArrayList)aList3.clone());
			aList1.clear();
			aList2.clear();
			aList3.clear();
		}	
	}
	public static void  RoogleGruppen(){
		if (ini==null){
			ini = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/rehajava.ini");
			//ini = new INIFile("c:\\RehaVerwaltung\\ini\\rehajava.ini");
		}	
		aRoogleGruppen = new ArrayList<ArrayList<ArrayList<String[]>>>();
		int lesen,i;
		lesen =  Integer.parseInt(new String(ini.getStringProperty("Kalender","RoogleAnzahlGruppen")) );
		ArrayList<String> aList1  = new ArrayList<String>();
		ArrayList<String[]> aList2 = new ArrayList<String[]>();
		ArrayList<ArrayList<ArrayList<String[]>>> aList3 = new ArrayList<ArrayList<ArrayList<String[]>>>();

		for(i=1;i<=lesen;i++){
			aList1.add(new String(ini.getStringProperty("Kalender","RoogleNameGruppen"+i)) );
			aList2.add((String[]) new String(ini.getStringProperty("Kalender","RoogleFelderGruppen"+i)).split(",") );
			aList3.add((ArrayList)aList1.clone());
			aList3.add((ArrayList) aList2.clone());
			aRoogleGruppen.add((ArrayList)aList3.clone());
			aList1.clear();
			aList2.clear();
			aList3.clear();
		}
		for(i=0;i<7;i++){
			RoogleTage[i] = (ini.getStringProperty("RoogleEinstellungen","Tag"+(i+1)).trim().equals("0") ? false : true );
		}
		RoogleZeitraum = new Integer(ini.getStringProperty("RoogleEinstellungen","Zeitraum"));
		RoogleZeiten = new  HashMap<String,String>();
		RoogleZeiten.put("KG",ini.getStringProperty("RoogleEinstellungen","KG") );
		RoogleZeiten.put("MA",ini.getStringProperty("RoogleEinstellungen","MA") );
		RoogleZeiten.put("ER",ini.getStringProperty("RoogleEinstellungen","ER") );
		RoogleZeiten.put("LO",ini.getStringProperty("RoogleEinstellungen","LO") );
		RoogleZeiten.put("SP",ini.getStringProperty("RoogleEinstellungen","SP") );
	}

	private void EmailParameter(){
		if (ini==null){
			ini = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/rehajava.ini");
		}
			//ini = new INIFile("c:\\RehaVerwaltung\\ini\\rehajava.ini");
			hmEmailExtern = new HashMap<String,String>();
			hmEmailExtern.put("SmtpHost",ini.getStringProperty("EmailExtern","SmtpHost"));
			hmEmailExtern.put("SmtpAuth",ini.getStringProperty("EmailExtern","SmtpAuth"));			
			hmEmailExtern.put("Pop3Host",ini.getStringProperty("EmailExtern","Pop3Host"));
			hmEmailExtern.put("Username",ini.getStringProperty("EmailExtern","Username"));
			String pw = ini.getStringProperty("EmailExtern","Password");
			Verschluesseln man = Verschluesseln.getInstance();
		    man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
			String decrypted = man.decrypt (pw);
			hmEmailExtern.put("Password",decrypted);
			hmEmailExtern.put("SenderAdresse",ini.getStringProperty("EmailExtern","SenderAdresse"));			
			hmEmailExtern.put("Bestaetigen",ini.getStringProperty("EmailExtern","EmpfangBestaetigen"));			
			/********************/
			hmEmailIntern = new HashMap<String,String>();
			hmEmailIntern.put("SmtpHost",ini.getStringProperty("EmailIntern","SmtpHost"));
			hmEmailIntern.put("SmtpAuth",ini.getStringProperty("EmailIntern","SmtpAuth"));			
			hmEmailIntern.put("Pop3Host",ini.getStringProperty("EmailIntern","Pop3Host"));
			hmEmailIntern.put("Username",ini.getStringProperty("EmailIntern","Username"));
			pw = ini.getStringProperty("EmailIntern","Password");
			man = Verschluesseln.getInstance();
		    man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
			decrypted = man.decrypt (pw);
			hmEmailIntern.put("Password",decrypted);
			hmEmailIntern.put("SenderAdresse",ini.getStringProperty("EmailIntern","SenderAdresse"));			
			hmEmailIntern.put("Bestaetigen",ini.getStringProperty("EmailIntern","EmpfangBestaetigen"));			

	}	
	
	private void Verzeichnisse(){
		if (ini==null){
			ini = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/rehajava.ini");
		}
		hmVerzeichnisse = new HashMap<String,String>();
		hmVerzeichnisse.put("Programmverzeichnis",new String(Reha.proghome));
		hmVerzeichnisse.put("Vorlagen",new String(Reha.proghome+"vorlagen/"+Reha.aktIK));
		hmVerzeichnisse.put("Icons",new String(Reha.proghome+"icons"));
		hmVerzeichnisse.put("Temp",new String(Reha.proghome+"temp/"+Reha.aktIK));		
		hmVerzeichnisse.put("Ini",new String(Reha.proghome+"ini/"+Reha.aktIK));		
	}
	
	private void TKFarben(){
		if (colini==null){
			colini = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/color.ini");
		}
		System.out.println("In TK-Farben");
		int anz  = new Integer( new String(colini.getStringProperty("Terminkalender","FarbenAnzahl")));
		vSysColsNamen = new Vector<String>();
		vSysColsBedeut = new Vector<String>();
		vSysColsCode = new Vector<String>();
		//ArrayList<String> colnames = new ArrayList<String>();
		for(int i = 0;i<anz;i++){
			vSysColsNamen.add(new String(colini.getStringProperty("Terminkalender","FarbenNamen"+(i+1))));
			vSysColsBedeut.add(new String(colini.getStringProperty("Terminkalender","FarbenBedeutung"+(i+1))));
			vSysColsCode.add(new String(colini.getStringProperty("Terminkalender","FarbenCode"+(i+1))));			
			//colnames.add(new String(colini.getStringProperty("Terminkalender","FarbenNamen")));
		}
		int def = new Integer( new String(colini.getStringProperty("Terminkalender","FarbenDefaults")));
		vSysDefNamen = new Vector<String>();
		//ArrayList<String> defnames = new ArrayList<String>();		
		for(int i = 0;i<def;i++){
			vSysDefNamen.add(new String(colini.getStringProperty("Terminkalender","FarbenDefaultNamen"+(i+1))));
			//defnames.add(new String(colini.getStringProperty("Terminkalender","FarbenDefaultNamen")));
		}
		/*
		public static Vector<Vector<Color[]>> hmDefaultCols;
		public static Vector<Color[]> vSysColsObject;
		public static Vector<String> vSysColsNamen;
		public static Vector<String> vSysDefNamen;
		public static Vector<String> vSysColsBedeut;
		*/

		vSysColsObject = new Vector<Vector<Color[]>>();

		// Zuerst die Userfarben
		Vector<Color[]> colv = new Vector<Color[]>();
		for(int j = 0; j < anz;j++){

			String[] farb = new String(colini.getStringProperty( "UserFarben",vSysColsNamen.get(j))).split(",");
			Color[] farbe = new Color[2];
			farbe[0] = new Color(new Integer(farb[0]),new Integer(farb[1]),new Integer(farb[2]));
			farbe[1] = new Color(new Integer(farb[3]),new Integer(farb[4]),new Integer(farb[5]));
			colv.add(farbe);
		}
		vSysColsObject.add((Vector<Color[]>)colv.clone());
		
		
		for(int i = 0; i < def; i++){
			//Anzahl der Sets
			colv = new Vector<Color[]>();
			for(int j = 0; j < anz;j++){
				//System.out.println("Bei i="+i+" /  und j="+j);
				String[] farb = new String(colini.getStringProperty( vSysDefNamen.get(i),vSysColsNamen.get(j))).split(",");
				Color[] farbe = new Color[2];
				farbe[0] = new Color(new Integer(farb[0]),new Integer(farb[1]),new Integer(farb[2]));
				farbe[1] = new Color(new Integer(farb[3]),new Integer(farb[4]),new Integer(farb[5]));
				colv.add(farbe);
			}
			vSysColsObject.add((Vector<Color[]>)colv.clone());
		}

		JLabel BeispielDummi = new JLabel("so sieht's aus");		
		int i,lang;
		lang = SystemConfig.vSysColsNamen.size();
		vSysColDlg = new Vector();
		for(i=0;i<lang;i++){
			Vector ovec = new Vector();
			ovec.add(SystemConfig.vSysColsCode.get(i));
			ovec.add(SystemConfig.vSysColsBedeut.get(i));
			ovec.add(SystemConfig.vSysColsObject.get(0).get(i)[0]);
			ovec.add(SystemConfig.vSysColsObject.get(0).get(i)[1]);			
			ovec.add(BeispielDummi);
			vSysColDlg.add(ovec.clone());
		}
		aktTkCol = new HashMap<String,Color[]>();
		for(i=0;i<lang;i++){
			aktTkCol.put(vSysColsNamen.get(i), new Color[] {SystemConfig.vSysColsObject.get(0).get(i)[0],
				SystemConfig.vSysColsObject.get(0).get(i)[1]});
			 
		}
		KalenderHintergrund = aktTkCol.get("AusserAZ")[0];

	}
	public static void MandantenEinlesen(){

		INIFile inif = new INIFile(Reha.proghome+"ini/mandanten.ini");
		int AnzahlMandanten = inif.getIntegerProperty("TheraPiMandanten", "AnzahlMandanten");
		AuswahlImmerZeigen = inif.getIntegerProperty("TheraPiMandanten", "AuswahlImmerZeigen");
		DefaultMandant = inif.getIntegerProperty("TheraPiMandanten", "DefaultMandant");
		int LetzterMandant = inif.getIntegerProperty("TheraPiMandanten", "LetzterMandant");
		Mandanten = new Vector<String[]>();			
		for(int i = 0; i < AnzahlMandanten;i++){
			String[] mand = {null,null};
			mand[0] = new String(inif.getStringProperty("TheraPiMandanten", "MAND-IK"+(i+1)));
			mand[1] = new String(inif.getStringProperty("TheraPiMandanten", "MAND-NAME"+(i+1)));
			Mandanten.add(mand.clone());
		}
		hmDBMandant = new HashMap<String,Vector>(); 
		for(int i = 0; i < AnzahlMandanten;i++){
			INIFile minif = new INIFile(Reha.proghome+"ini/"+Mandanten.get(i)[0]+"/rehajava.ini");
			Vector<String> mandantDB = new Vector<String>();
			mandantDB.add(minif.getStringProperty("DatenBank", "DBType1"));
			mandantDB.add(minif.getStringProperty("DatenBank", "DBTreiber1"));
			mandantDB.add(minif.getStringProperty("DatenBank", "DBServer1"));			
			mandantDB.add(minif.getStringProperty("DatenBank", "DBPort1"));			
			mandantDB.add(minif.getStringProperty("DatenBank", "DBName1"));			
			mandantDB.add(minif.getStringProperty("DatenBank", "DBBenutzer1"));	
			String pw = minif.getStringProperty("DatenBank", "DBPasswort1");
			String decrypted = null;
			if(pw != null){
				Verschluesseln man = Verschluesseln.getInstance();
				man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
				decrypted = man.decrypt (pw);
			}else{
				decrypted = new String("");
			}
			mandantDB.add(new String(decrypted));
			hmDBMandant.put(Mandanten.get(i)[1],(Vector<String>)mandantDB.clone());
		}
		
		INIFile dbtini = new INIFile(Reha.proghome+"ini/dbtypen.ini");
		int itypen = dbtini.getIntegerProperty("Datenbanktypen", "TypenAnzahl");
		DBTypen = new Vector<String[]>();
		String[] typen = new String[] {null,null,null};
		for(int i = 0; i < itypen;i++){
			typen[0] = dbtini.getStringProperty("Datenbanktypen", "Typ"+(i+1)+"Typ");
			typen[1] = dbtini.getStringProperty("Datenbanktypen", "Typ"+(i+1)+"Treiber");			
			typen[2] = dbtini.getStringProperty("Datenbanktypen", "Typ"+(i+1)+"Port");
			DBTypen.add(typen.clone());
		}
	}

	public static void InetSeitenEinlesen(){
		INIFile inif = new INIFile(Reha.proghome+"ini/rehabrowser.ini");
		int seitenanzahl = inif.getIntegerProperty("RehaBrowser", "SeitenAnzahl");
		InetSeiten = new Vector<ArrayList<String>>();
		ArrayList<String> seite = null;
		for(int i = 0; i < seitenanzahl; i++){
			seite = new ArrayList<String>();
			seite.add(inif.getStringProperty("RehaBrowser", "SeitenName"+(i+1)));
			seite.add(inif.getStringProperty("RehaBrowser", "SeitenIcon"+(i+1)));
			seite.add(inif.getStringProperty("RehaBrowser", "SeitenAdresse"+(i+1)));
			InetSeiten.add(seite);
		}
		HilfeServer = inif.getStringProperty("TheraPiHilfe", "HilfeServer");
		HilfeServerIstDatenServer = (inif.getIntegerProperty("TheraPiHilfe", "HilfeDBIstDatenDB") > 0 ? true : false);
		if(! HilfeServerIstDatenServer){
			hmHilfeServer = new HashMap<String,String>();
			hmHilfeServer.put("HilfeDBTreiber", inif.getStringProperty("TheraPiHilfe", "HilfeDBTreiber"));
			hmHilfeServer.put("HilfeDBLogin", inif.getStringProperty("TheraPiHilfe", "HilfeDBLogin"));		
			hmHilfeServer.put("HilfeDBUser", inif.getStringProperty("TheraPiHilfe", "HilfeDBUser"));
			String pw = new String(inif.getStringProperty("TheraPiHilfe","HilfeDBPassword"));
			Verschluesseln man = Verschluesseln.getInstance();
		    man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
			String decrypted = man.decrypt (pw);
			hmHilfeServer.put("HilfeDBPassword", decrypted);			
		}
	}	
 

	public static void UpdateIni(String gruppe,String element,String wert){
		if (ini==null){
			ini = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/rehajava.ini");
		}	
		ini.setStringProperty(gruppe, element, wert, null);
		ini.save();
	}
	public static void GruppenLesen(){
		oGruppen = new GruppenEinlesen().init();
	}
	/*
	public static void TarifeLesen(){
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/kasse.ini");
		int tarife = inif.getIntegerProperty("PreisGruppen", "AnzahlPreisGruppen");
		vPreisGruppen = new Vector<String>() ;
		vPreisGueltig = new Vector<String>() ;
		vZuzahlRegeln = new Vector<Integer>();
		vNeuePreiseAb = new Vector<Vector<String>>();
		vNeuePreiseRegel = new Vector<Vector<Integer>>();
		vHBRegeln = new Vector<Vector<String>>();
		vBerichtRegeln = new Vector<String>();
		vHMRAbrechnung = new Vector<Integer>();
		//xxx

		Vector<String> vec = new Vector<String>();

		for(int i = 1; i <= tarife; i++){
			vPreisGruppen.add(inif.getStringProperty("PreisGruppen","PGName"+i));
			vPreisGueltig.add(inif.getStringProperty("PreisGruppen","PGGueltig"+i));
			vZuzahlRegeln.add(inif.getIntegerProperty("ZuzahlRegeln","ZuzahlRegel"+i));
			vHMRAbrechnung.add(inif.getIntegerProperty("HMRAbrechnung","HMRAbrechnung"+i));
		}
		tarife = inif.getIntegerProperty("HBRegeln", "AnzahlHBRegeln");
		for(int i = 1; i <= tarife; i++){
			vec.clear();
			vec.add(inif.getStringProperty("HBRegeln","HBPosVoll"+i));
			vec.add(inif.getStringProperty("HBRegeln","HBPosMit"+i));			
			vec.add(inif.getStringProperty("HBRegeln","HBKilometer"+i));			
			vec.add(inif.getStringProperty("HBRegeln","HBPauschal"+i));			
			vec.add(inif.getStringProperty("HBRegeln","HBMitZuZahl"+i));
			vHBRegeln.add((Vector<String>)vec.clone());
		}
		tarife = inif.getIntegerProperty("BerichtRegeln", "AnzahlBerichtRegeln");
		for(int i = 1; i <= tarife; i++){
			vBerichtRegeln.add( inif.getStringProperty("BerichtRegeln","Bericht1"+i) );
		}
		String[] diszis = {"Physio","Massage","Ergo","Logo","REHA"};
		tarife = inif.getIntegerProperty("PreisRegeln", "PreisRegelAnzahl");
		Integer iregel;
		Vector<String> vpreisab = new Vector<String>();
		Vector<Integer> vpreisreg = new Vector<Integer>();		
		for(int d = 0 ; d < diszis.length; d++){
			vpreisab.clear();
			vpreisreg.clear();
			for(int i = 1; i <= tarife; i++){
				vpreisab.add(inif.getStringProperty("PreisRegeln","Preis"+diszis[d]+"Ab"+i));
				iregel = inif.getIntegerProperty("PreisRegeln","Preis"+diszis[d]+"Regel"+i);
				vpreisreg.add( ( (iregel != null) ? iregel : 0) );				
			}
			vNeuePreiseAb.add((Vector<String>)vpreisab.clone());
			vNeuePreiseRegel.add((Vector<Integer>)vpreisreg.clone());
		}
		System.out.println("Neue Preise ab="+vNeuePreiseAb);
		System.out.println("Neue Preise Regeln = "+vNeuePreiseRegel);

	}
	*/
	public static void HashMapsVorbereiten(){
		/********************/
		hmAdrKDaten = new HashMap<String,String>();
		List<String> lAdrKDaten = Arrays.asList(new String[]{"<Kadr1>","<Kadr2>","<Kadr3>","<Kadr4>","<Kadr5>",
												"<Ktel>","<Kfax>","<Kemail>","<Kid>"});
		for(int i = 0; i < lAdrKDaten.size(); i++){
			hmAdrKDaten.put(lAdrKDaten.get(i),"");
		}
		/********************/
		hmAdrADaten = new HashMap<String,String>();
		List<String> lAdrADaten = Arrays.asList(new String[]{"<Aadr1>","<Aadr2>","<Aadr3>","<Aadr4>","<Aadr5>",
												"<Atel>","<Afax>","<Aemail>","<Aid>","<Aihrer>","<Apatientin>","<Adie>"});
		for(int i = 0; i < lAdrADaten.size(); i++){
			hmAdrADaten.put(lAdrADaten.get(i),"");
		}
		/********************/
		hmAdrPDaten = new HashMap<String,String>();
		List<String> lAdrPDaten = Arrays.asList(new String[]{"<Padr1>","<Padr2>","<Padr3>","<Padr4>","<Padr5>",
											"<Pgeboren>","<Panrede>","<Pnname>","<Pvname>","<Pbanrede>",
											"<Ptelp>","<Ptelg>","<Ptelmob>","<Pfax>","<Pemail>","<Ptitel>","<Pihrem>","<Pihnen>","<Pid>","<Palter>","<Pzigsten>"});
		for(int i = 0; i < lAdrPDaten.size(); i++){
			hmAdrPDaten.put(lAdrPDaten.get(i),"");
		}
		/********************/		
		hmAdrRDaten = new HashMap<String,String>();
		List<String> lAdrRDaten = Arrays.asList(new String[]{"<Rpatid>","<Rnummer>","<Rdatum>","<Rposition1>","<Rposition2>","<Rposition3>"
				,"<Rposition4>","<Rpreise1>","<Rpreise2>","<Rpreise3>","<Rpreise4>","<Rproz1>","<Rproz2>","<Rproz3>"
				,"<Rproz4>","<Rgesamt1>","<Rgesamt2>","<Rgesamt3>","<Rgesamt4>","<Rpauschale>","<Rendbetrag>","<Ranzahl1>"
				,"<Ranzahl4>","<Ranzahl4>","<Ranzahl4>","<Rerstdat>","<Rletztdat>","<Rid>","<Rtage>","<Rkurz1>","<Rkurz2"
				,"<Rkurz3>","<Rkurz4>","<Rlang1>","<Rlang2>","<Rlang3>","<Rlang4>","<Rbarcode>","<Systemik>","<Rwert>"
				,"<Rhbpos>","<Rwegpos>","<Rhbpreis>","<Rwegpreis>","<Rhbproz>","<Rwegproz>","<Rhbanzahl>","<Rweganzahl>"
				,"<Rhbgesamt>","<Rweggesamt>","<Rwegkm>"});
		for(int i = 0; i < lAdrRDaten.size(); i++){
			hmAdrRDaten.put(lAdrRDaten.get(i),"");
		}
		/********************/		
		hmAdrBDaten = new HashMap<String,String>();
		List<String> lAdrBDaten = Arrays.asList(new String[]{"<Badr1>","<Badr2>","<Badr3>","<Badr4>","<Badr5>","<Bbanrede>",
				"<Bihrenpat>","<Bdisziplin>","<Bdiagnose>","<Breznr>","<Brezdatum>","<Bblock1>","<Bblock2>","<Bblock3>","<Bblock4>",
				"<Btitel1>","<Btitel2>","<Btitel3>","<Btitel4>","<Bnname>","<Bvnname>","<Bgeboren>","<Btherapeut>"});
		for(int i = 0; i < lAdrBDaten.size(); i++){
			hmAdrBDaten.put(lAdrBDaten.get(i),"");
		}	
		/********************/
		hmAdrAFRDaten = new HashMap<String,String>();
		List<String> lAdrAFRDaten = Arrays.asList(new String[]{"<AFRposition1>","<AFRposition2>","<AFRposition3>"
				,"<AFRposition4>","<AFRpreis1>","<AFRpreis2>","<AFRpreis3>","<AFRpreis4>","<AFRgesamt>","<AFRnummer>",
				"<AFRkurz1>","<AFRkurz2>","<AFRkurz3>","<AFRkurz4>","<AFRlang1>","<AFRlang2>","<AFRlang3>","<AFRlang4>"});
		for(int i = 0; i < lAdrAFRDaten.size(); i++){
			hmAdrAFRDaten.put(lAdrAFRDaten.get(i),"");
		}
		/********************/
		hmAdrHMRDaten = new HashMap<String,String>();
		
	}
		

	public static void DesktopLesen(){
		hmContainer = new HashMap<String,Integer>();
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/kasse.ini");
		hmContainer.put("Kasse", inif.getIntegerProperty("Container", "StarteIn"));	
		hmContainer.put("KasseOpti", inif.getIntegerProperty("Container", "ImmerOptimieren"));
		inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/patient.ini");
		hmContainer.put("Patient", inif.getIntegerProperty("Container", "StarteIn"));	
		hmContainer.put("PatientOpti", inif.getIntegerProperty("Container", "ImmerOptimieren"));
		inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/kalender.ini");
		hmContainer.put("Kalender", inif.getIntegerProperty("Container", "StarteIn"));
		hmContainer.put("KalenderOpti", inif.getIntegerProperty("Container", "ImmerOptimieren"));
		inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/arzt.ini");
		hmContainer.put("Arzt", inif.getIntegerProperty("Container", "StarteIn"));	
		hmContainer.put("ArztOpti", inif.getIntegerProperty("Container", "ImmerOptimieren"));
	}
	public static void PatientLesen(){
		vPatMerker = new Vector<String>();
		vPatMerkerIcon = new Vector<ImageIcon>();
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/patient.ini");
		for(int i = 1; i < 7;i++){
			vPatMerker.add(inif.getStringProperty("Kriterien", "Krit"+i));
			String simg = inif.getStringProperty("Kriterien", "Image"+i);
			if(simg.equals("")){
				vPatMerkerIcon.add(null);
			}else{
				vPatMerkerIcon.add(new ImageIcon(Reha.proghome+"icons/"+simg));
			}
		}
	}
	public static void GeraeteInit(){
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/geraete.ini");
		if(inif.getIntegerProperty("KartenLeser", "KartenLeserAktivieren") > 0){
			sReaderName = inif.getStringProperty("KartenLeser", "KartenLeserName");
			sReaderAktiv = "1";
			sReaderCom = inif.getStringProperty("KartenLeser", "KartenLeserAnschluss");			
			hmKVKDaten = new HashMap<String,String>();
			hmKVKDaten.put("Krankekasse", "");
			hmKVKDaten.put("Kassennummer", "");
			hmKVKDaten.put("Kartennummer", "");
			hmKVKDaten.put("Versichertennummer", "");			
			hmKVKDaten.put("Status", "");			
			hmKVKDaten.put("Statusext", "");
			hmKVKDaten.put("Vorname", "");			
			hmKVKDaten.put("Nachname", "");			
			hmKVKDaten.put("Geboren", "");			
			hmKVKDaten.put("Strasse", "");			
			hmKVKDaten.put("Land", "");			
			hmKVKDaten.put("Plz", "");			
			hmKVKDaten.put("Ort", "");			
			hmKVKDaten.put("Gueltigkeit", "");			
			hmKVKDaten.put("Checksumme", "");	
			hmKVKDaten.put("Fehlercode", "");
			hmKVKDaten.put("Fehlertext", "");
		}else{
			sReaderName = "";
			sReaderAktiv = "0";
			sReaderCom = inif.getStringProperty("KartenLeser", "KartenLeserAnschluss");			
		}
		if(inif.getIntegerProperty("BarcodeScanner", "BarcodeScannerAktivieren") > 0){
			sBarcodeScanner = inif.getStringProperty("BarcodeScanner", "BarcodeScannerName");
			sBarcodeAktiv = inif.getStringProperty("BarcodeScanner", "BarcodeScannerAktivieren");
			sBarcodeCom = inif.getStringProperty("BarcodeScanner", "BarcodeScannerAnschluss");
		}else{
			sBarcodeScanner = "";
			sBarcodeAktiv = "0";
			sBarcodeCom = inif.getStringProperty("BarcodeScanner", "BarcodeScannerAnschluss");
		}
		hmDokuScanner = new HashMap<String,String>();
		if(inif.getIntegerProperty("DokumentenScanner", "DokumentenScannerAktivieren") > 0){
			sDokuScanner = inif.getStringProperty("DokumentenScanner", "DokumentenScannerName");
			hmDokuScanner.put("aktivieren", "1");
			hmDokuScanner.put("aufloesung", inif.getStringProperty("DokumentenScanner", "DokumentenScannerAufloesung"));
			hmDokuScanner.put("farben",inif.getStringProperty("DokumentenScanner", "DokumentenScannerFarben") );
			hmDokuScanner.put("seiten", inif.getStringProperty("DokumentenScanner", "DokumentenScannerSeiten"));
			hmDokuScanner.put("dialog", inif.getStringProperty("DokumentenScanner", "DokumentenScannerDialog"));
		}else{
			sDokuScanner = "Scanner nicht aktiviert!";
			hmDokuScanner.put("aktivieren", "0");
			hmDokuScanner.put("aufloesung", "---");
			hmDokuScanner.put("farben","---");
			hmDokuScanner.put("seiten", "---");
			hmDokuScanner.put("dialog", "---");
		}
	}
	public static void ArztGruppenInit(){
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/arzt.ini");
		int ags;
		if( (ags = inif.getIntegerProperty("ArztGruppen", "AnzahlGruppen")) > 0){
			arztGruppen = new String[ags];
			for(int i = 0; i < ags; i++){
				arztGruppen[i] =  inif.getStringProperty("ArztGruppen", "Gruppe"+new Integer(i+1).toString());
			}
		}
	}
	public static void RezeptInit(){
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/rezept.ini");
		int args;
		//public static String[] rezeptKlassen = null;
		initRezeptKlasse = inif.getStringProperty("RezeptKlassen", "InitKlasse");
		args = inif.getIntegerProperty("RezeptKlassen", "KlassenAnzahl");
		rezeptKlassen = new String[args];
		rezeptKlassenAktiv = new Vector<Vector<String>>();

		int aktiv;
		Vector<String> vec = new Vector<String>();
		for(int i = 0;i < args;i++){
			rezeptKlassen[i] = inif.getStringProperty("RezeptKlassen", "Klasse"+new Integer(i+1).toString());
			aktiv = inif.getIntegerProperty("RezeptKlassen", "KlasseAktiv"+new Integer(i+1).toString());
			if(aktiv > 0){
				vec.clear();
				vec.add(new String(rezeptKlassen[i]));
				vec.add(inif.getStringProperty("RezeptKlassen", "KlasseKurz"+new Integer(i+1).toString())  );
				rezeptKlassenAktiv.add((Vector<String>)vec.clone());
			}
		}
		rezGebDrucker = inif.getStringProperty("DruckOptionen", "RezGebDrucker");
		rezGebVorlageNeu = Reha.proghome+"vorlagen/"+Reha.aktIK+"/"+inif.getStringProperty("Vorlagen", "RezGebVorlageNeu");
		rezGebVorlageAlt = Reha.proghome+"vorlagen/"+Reha.aktIK+"/"+inif.getStringProperty("Vorlagen", "RezGebVorlageAlt");
		rezGebVorlageHB = Reha.proghome+"vorlagen/"+Reha.aktIK+"/"+inif.getStringProperty("Vorlagen", "RezGebVorlageHB");
		rezGebDirektDruck = (inif.getIntegerProperty("DruckOptionen", "DirektDruck") <= 0 ? false : true);
		rezBarcodeDrucker = inif.getStringProperty("DruckOptionen", "BarCodeDrucker");
		args = inif.getIntegerProperty("BarcodeForm", "BarcodeFormAnzahl");
		if(args > 0){
			rezBarCodName = new String[args];
			rezBarCodForm = new Vector<String>();
			for(int i = 0; i < args; i++){
				rezBarCodName[i] = inif.getStringProperty("BarcodeForm", "FormName"+(i+1));
				rezBarCodForm.add(inif.getStringProperty("BarcodeForm", "FormVorlage"+(i+1)));
			}
		}else{
			rezBarCodName = new String[] {null};
			rezBarCodForm = new Vector<String>();
		}

	}
	public static void TherapBausteinInit() {
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/thbericht.ini");
		hmTherapBausteine = new HashMap<String,Vector>();
		int lang = rezeptKlassenAktiv.size();
		Vector<String> vec = new Vector<String>();
		for(int i = 0; i<lang;i++){
			vec.clear();
			String prop = "Anzahl"+rezeptKlassenAktiv.get(i).get(1);
			int lang2 = inif.getIntegerProperty("Textbausteine", prop);
			String prop2 = rezeptKlassenAktiv.get(i).get(1);
			String gelenk;
			for(int i2 = 0; i2 < lang2;i2++ ){
				//gelenk = inif.getStringProperty("Textbausteine", prop2+(i2+1));
				vec.add( inif.getStringProperty("Textbausteine", prop2+(i2+1)) );
				/*
				try {
					System.out.println(new String(gelenk.getBytes("UTF-8")));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				byte[] b = null;

					b = gelenk.getBytes();
				for(int y = 0; y < b.length;y++){
					System.out.println(b[y]);
				}
				
				
				vec.add( new String( gelenk.getBytes()  )  );
				*/
			}
			System.out.println(vec);
			hmTherapBausteine.put(prop2, (Vector) vec.clone());
		}
		for(int i = 0; i<4;i++){
			berichttitel[i] = inif.getStringProperty("Bericht", "Block"+(i+1));	
		}
		thberichtdatei = Reha.proghome+"vorlagen/"+Reha.aktIK+"/"+inif.getStringProperty("Datei", "BerichtsDatei");
		

	}
	public static void FirmenDaten(){
		String[] stitel = {"Ik","Ikbezeichnung","Firma1","Firma2","Anrede","Nachname","Vorname",
				"Strasse","Plz","Ort","Telefon","Telefax","Email","Internet","Bank","Blz","Kto",
				"Steuernummer","Hrb","Logodatei","Zusatz1","Zusatz2","Zusatz3","Zusatz4","Bundesland"};
		hmFirmenDaten = new HashMap<String,String>();
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/firmen.ini");
		for(int i = 0; i < stitel.length;i++){
			hmFirmenDaten.put(stitel[i],inif.getStringProperty("Firma",stitel[i] ) );
		}
	}
	public static void FremdProgs(){
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/fremdprog.ini");
		vFremdProgs = new Vector<Vector<String>>();
		Vector<String> progs = new Vector<String>();
		int anzahl =  inif.getIntegerProperty("FremdProgramme", "FremdProgrammeAnzahl");
		for(int i = 0; i < anzahl; i++){
			progs.clear();
			progs.add(inif.getStringProperty("FremdProgramme", "FremdProgrammName"+(i+1)));
			progs.add(inif.getStringProperty("FremdProgramme", "FremdProgrammPfad"+(i+1)));
			vFremdProgs.add((Vector)progs.clone());
		}
		hmFremdProgs = new HashMap<String,String>();
		anzahl =  inif.getIntegerProperty("FestProg", "FestProgAnzahl");
		for(int i = 0; i < anzahl; i++){		
			hmFremdProgs.put(
					inif.getStringProperty("FestProg", "FestProgName"+(i+1)),
					inif.getStringProperty("FestProg", "FestProgPfad"+(i+1))
			);
		}

	}
	public static void GeraeteListe(){
		hmGeraete = new HashMap<String,String[]>();
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/geraete.ini");

		int anzahl =  inif.getIntegerProperty("KartenLeserListe", "LeserAnzahl");
		String[] string = new String[anzahl]; 
		for(int i = 0; i < anzahl; i++){
			string[i] = inif.getStringProperty("KartenLeserListe", "Leser"+(i+1));
		}
		hmGeraete.put("Kartenleser", string.clone());

		anzahl =  inif.getIntegerProperty("BarcodeScannerListe", "ScannerAnzahl");
		string = new String[anzahl];
		for(int i = 0; i < anzahl; i++){
			string[i] = inif.getStringProperty("BarcodeScannerListe", "Scanner"+(i+1));
		}		
		hmGeraete.put("Barcode", string.clone());
		
		anzahl =  inif.getIntegerProperty("ECKartenLeserListe", "ECLeserAnzahl");
		string = new String[anzahl];
		for(int i = 0; i < anzahl; i++){
			string[i] = inif.getStringProperty("ECKartenLeserListe", "ECLeser"+(i+1));
		}		
		hmGeraete.put("ECKarte", string.clone());
		string = new String[4];
		for(int i = 1; i < 11; i++){
			string[0] = inif.getStringProperty("COM"+i, "BaudRate");
			string[1] = inif.getStringProperty("COM"+i, "Bits");
			string[2] = inif.getStringProperty("COM"+i, "Parity");			
			string[3] = inif.getStringProperty("COM"+i, "StopBit");
			hmGeraete.put("COM"+i, string.clone());			
		}		
	}
	
	public static void CompanyInit(){
		hmCompany = new HashMap<String,String>();
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/company.ini");
		hmCompany.put("name", inif.getStringProperty("Company", "CompanyName"));
		hmCompany.put("enable", inif.getStringProperty("Company", "DeliverEnable"));
		hmCompany.put("event", inif.getStringProperty("Company", "DeliverEvent"));
		hmCompany.put("ip", inif.getStringProperty("Company", "DeliverIP"));		
		hmCompany.put("port", inif.getStringProperty("Company", "DeliverPort"));
		hmCompany.put("mail", inif.getStringProperty("Company", "DeliverMail"));
		hmCompany.put("adress", inif.getStringProperty("Company", "DeliverAdress"));		
	}
	public static void GutachtenInit(){
		vGutachtenEmpfaenger = new Vector<String>();
		vGutachtenIK = new Vector<String>();
		vGutachtenArzt = new Vector<String>();
		vGutachtenDisplay = new Vector<String>();
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/gutachten.ini");
		int anzahl =  inif.getIntegerProperty("GutachtenEmpfaenger", "AnzahlEmpfaenger");
		for(int i = 0; i < anzahl;i++){
			vGutachtenEmpfaenger.add( inif.getStringProperty("GutachtenEmpfaenger", "Empfaenger"+(i+1)) );
			vGutachtenIK.add( inif.getStringProperty("IKAbsender", "AnsenderIK"+(i+1)) );
		}
		vGutachtenAbsAdresse = new Vector<String>();
		for(int i = 0; i < 5;i++){
			vGutachtenAbsAdresse.add(inif.getStringProperty("AbsenderAdresse", "AbsenderZeile"+(i+1)));
		}
		sGutachtenOrt = inif.getStringProperty("AbsenderDaten", "Ort");
		anzahl =  inif.getIntegerProperty("Arzt", "ArztAnzahl");
		for(int i = 0; i < anzahl;i++){
			vGutachtenArzt.add(inif.getStringProperty("Arzt", "Arzt"+(i+1)));
		}
		vGutachtenDisplay.add(inif.getStringProperty("AbsenderDisplayAdresse", "Display1"));
		vGutachtenDisplay.add(inif.getStringProperty("AbsenderDisplayAdresse", "Display2"));
		vGutachtenDisplay.add(inif.getStringProperty("AbsenderDisplayAdresse", "Display3"));
	}
	
	public static void AbrechnungParameter(){
		hmAbrechnung.clear();
		/********Heilmittelabrechnung********/
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/abrechnung.ini");
		hmAbrechnung.put("hmgkvformular", inif.getStringProperty("HMGKVRechnung", "Rformular"));
		hmAbrechnung.put("hmgkvrechnungdrucker", inif.getStringProperty("HMGKVRechnung", "Rdrucker"));
		hmAbrechnung.put("hmgkvtaxierdrucker", inif.getStringProperty("HMGKVRechnung", "Tdrucker"));
		hmAbrechnung.put("hmgkvbegleitzettel", inif.getStringProperty("HMGKVRechnung", "Begleitzettel"));
		hmAbrechnung.put("hmgkvrauchdrucken", inif.getStringProperty("HMGKVRechnung", "Rauchdrucken"));
		hmAbrechnung.put("hmgkvrexemplare", inif.getStringProperty("HMGKVRechnung", "Rexemplare"));

		hmAbrechnung.put("hmpriformular", inif.getStringProperty("HMPRIRechnung", "Pformular"));
		hmAbrechnung.put("hmpridrucker", inif.getStringProperty("HMPRIRechnung", "Pdrucker"));
		hmAbrechnung.put("hmpriexemplare", inif.getStringProperty("HMPRIRechnung", "Pexemplare"));
		
		hmAbrechnung.put("hmbgeformular", inif.getStringProperty("HMBGERechnung", "Bformular"));
		hmAbrechnung.put("hmbgedrucker", inif.getStringProperty("HMBGERechnung", "Bdrucker"));
		hmAbrechnung.put("hmbgeexemplare", inif.getStringProperty("HMBGERechnung", "Bexemplare"));
		/********Rehaabrechnung********/
		hmAbrechnung.put("rehagkvformular", inif.getStringProperty("RehaGKVRechnung", "RehaGKVformular"));
		hmAbrechnung.put("rehagkvdrucker", inif.getStringProperty("RehaGKVRechnung", "RehaGKVdrucker"));
		hmAbrechnung.put("rehagkvexemplare", inif.getStringProperty("RehaGKVRechnung", "RehaGKVexemplare"));
		hmAbrechnung.put("rehagkvik", inif.getStringProperty("RehaGKVRechnung", "RehaGKVik"));
		
		hmAbrechnung.put("rehadrvformular", inif.getStringProperty("RehaDRVRechnung", "RehaDRVformular"));
		hmAbrechnung.put("rehadrvdrucker", inif.getStringProperty("RehaDRVRechnung", "RehaDRVdrucker"));
		hmAbrechnung.put("rehadrvexemplare", inif.getStringProperty("RehaDRVRechnung", "RehaDRVexemplare"));
		hmAbrechnung.put("rehadrvik", inif.getStringProperty("RehaDRVRechnung", "RehaDRVik"));
		
		hmAbrechnung.put("rehapriformular", inif.getStringProperty("RehaPRIRechnung", "RehaPRIformular"));
		hmAbrechnung.put("rehapridrucker", inif.getStringProperty("RehaPRIRechnung", "RehaPRIdrucker"));
		hmAbrechnung.put("rehapriexemplare", inif.getStringProperty("RehaPRIRechnung", "RehaPRIexemplare"));
		hmAbrechnung.put("rehapriik", inif.getStringProperty("RehaPRIRechnung", "RehaPRIik"));
		
		hmAbrechnung.put("hmallinoffice", inif.getStringProperty("GemeinsameParameter", "InOfficeStarten"));
	}
	public static void SystemIconsInit(){
		String[] bilder = {"neu","edit","delete","print","save","find","stop","zuzahlfrei","zuzahlok","zuzahlnichtok",
				"nichtgesperrt","rezeptgebuehr","ausfallrechnung","arztbericht","privatrechnung",
				"sort","historieumsatz","historietage","historieinfo","keinerezepte","hausbesuch","historie","kvkarte",
				"ooowriter","ooocalc","oooimpress","openoffice","barcode","info","scanner","email","sms","tools","links",
				"rechts","abbruch","pdf","euro","einzeltage","info2","bild","patbild","bunker","camera","oofiles",
				"kleinehilfe","achtung","vorschau","patstamm","arztstamm","kassenstamm","drvlogo","personen16",
				"forward","wecker16","mond","roogle","scannergross","rot","gruen","inaktiv","buttonrot","buttongruen",
				"statusoffen","statuszu","statusset","abschliessen","bombe","openoffice26","tporgklein"};
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/icons.ini");
		hmSysIcons = new HashMap<String,ImageIcon>();
		Image ico = null;
		
		int xscale = 0;
		int yscale = 0;
		int lang = bilder.length;
		int i;
		for(i = 0; i < lang; i++){
			xscale = inif.getIntegerProperty("Icons", bilder[i]+"ScaleX");
			yscale = inif.getIntegerProperty("Icons", bilder[i]+"ScaleY");
			try{
				//System.out.println(Reha.proghome+"icons/"+inif.getStringProperty("Icons", bilder[i]));
				if((xscale >0) && (yscale > 0)){
					ico = new ImageIcon(Reha.proghome+"icons/"+inif.getStringProperty("Icons", bilder[i])).getImage().getScaledInstance(xscale,yscale, Image.SCALE_SMOOTH);
					hmSysIcons.put(bilder[i], new ImageIcon(ico));				
				}else{
					hmSysIcons.put(bilder[i], new ImageIcon(Reha.proghome+"icons/"+inif.getStringProperty("Icons", bilder[i])));
				}
			}catch(Exception ex){
				System.out.println("Fehler bei Bild "+bilder[i]);
				ex.printStackTrace();
			}
			ico = null;
		}
		//Reha.thisClass.copyLabel.setDropTarget(true);
		System.out.println("System-Icons wurden geladen");
	}
	
	public static void compTest(){
		Vector<Vector> vec = new Vector<Vector>();
		Vector<String> ve2 = new Vector<String>();
		ve2.add("Zundermann");
		ve2.add("0");
		vec.add((Vector)ve2.clone());
		ve2.clear();
		ve2.add("Maier");
		ve2.add("1");
		vec.add((Vector)ve2.clone());
		ve2.clear();
		ve2.add("Ammann");
		ve2.add("2");
		vec.add((Vector)ve2.clone());
		Comparator<Vector> comparator = new Comparator<Vector>() {
			/*
			public int compare(String s1, String s2) {
		        String[] strings1 = s1.split("\\s");
		        String[] strings2 = s2.split("\\s");
		        return strings1[strings1.length - 1]
		            .compareTo(strings2[strings2.length - 1]);
		    }
		    */

			@Override
			public int compare(Vector o1, Vector o2) {
				// TODO Auto-generated method stub
				String s1 = (String)o1.get(0);
				String s2 = (String)o2.get(0);
				return s1.compareTo(s2);
			}
		};
		Collections.sort(vec,comparator);
		System.out.println("Sortierter Vector = "+vec);
	}
	

	
	
}

/*****************************************/

