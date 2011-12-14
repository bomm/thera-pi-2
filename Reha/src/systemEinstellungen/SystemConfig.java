package systemEinstellungen;

import hauptFenster.Reha;

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;



import sqlTools.SqlInfo;
import stammDatenTools.RezTools;
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
	

	@SuppressWarnings("unchecked")
	public static Vector vSysColDlg;
	public static HashMap<String,Color[]> aktTkCol;
	public static boolean[] RoogleTage = {false,false,false,false,false,false,false};
	public static int RoogleZeitraum;
	public static HashMap<String,String> RoogleZeiten = null;
	
	public static boolean[] taskPaneCollapsed = {false,false,true,true,false,true};
	/**
	 * nachfolgende static's sind notwendig für den Einsatz des Terminkalenders
	 */
	public static ArrayList<ArrayList<ArrayList<String[]>>> aTerminKalender;
	public static int AnzahlKollegen;
	public static Color KalenderHintergrund = null;
	public static boolean KalenderBarcode = false;
	public static boolean KalenderLangesMenue = false;
	public static boolean KalenderStartWochenAnsicht = false;
	public static String KalenderStartWADefaultUser = "./.";
	public static String KalenderStartNADefaultSet = "./.";
	public static boolean KalenderZeitLabelZeigen = false;
	public static boolean KalenderWochenTagZeigen = false;
	public static boolean KalenderTimeLineZeigen = false;
	public static String[]  KalenderUmfang =  {null,null};
	public static long[]  KalenderMilli =  {0,0};
	public static int UpdateIntervall;
	public static float KalenderAlpha = 0.0f;
	
	public static boolean AngelegtVonUser = false;
	
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

	@SuppressWarnings("unchecked")
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
	public static HashMap<String,String> hmRgkDaten = new HashMap<String,String>();
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
	public static String sReaderCtApiLib = null;
	public static String sReaderDeviceID = null;
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
	

	public static HashMap<String,Vector<String>> hmTherapBausteine = null;

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
	
	public static HashMap<String,Object> hmPatientenWerkzeugDlgIni = new HashMap<String,Object>();

	// Lemmi 20101223 Steuerparanmeter für den Patienten-Suchen-Dialog	
	public static HashMap<String,Integer> hmPatientenSuchenDlgIni = new HashMap<String,Integer>();
	
	// Lemmi 20110116 Steuerparanmeter für den Rezept-Dialog	
	public static HashMap<String,Object> hmRezeptDlgIni = new HashMap<String,Object>(); 
	
	
	// Lemmi 20101224 Steuerparanmeter für RGR und AFR Behandlung in OffenePosten und Mahnungen	
	public static HashMap<String,Integer> hmZusatzInOffenPostenIni = new HashMap<String,Integer>();
	
	public static HashMap<String,Object> hmTerminBestaetigen = new HashMap<String,Object>();
	
	public static boolean desktopHorizontal = true;

	public static String dta301InBox = null;
	public static String dta301OutBox = null;
	
	public static long timerdelay = 600000;
	public static boolean timerpopup = true;
	public static boolean timerprogressbar = true;
	
	public static Vector<String> vFeiertage;
	
	public static String sWebCamActive = null;
	public static int[] sWebCamSize = {320,240};
	                     
	public SystemConfig(){
	
	}
	public void SystemStart(String homedir){
		ini = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/rehajava.ini");
		PDFformularPfad = ini.getStringProperty("Formulare","PDFFormularPfad");
		try {
			dieseMaschine = java.net.InetAddress.getLocalHost();
		}
		catch (java.net.UnknownHostException uhe) {
			//System.out.println(uhe);
		}
		
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
	@SuppressWarnings("unchecked")
	private void DatenBank(){
		try{
		ini = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/rehajava.ini");
		//ini = new INIFile("c:\\RehaVerwaltung\\ini\\rehajava.ini");

		int lesen;
		int i;
		ArrayList<String> aKontakt;
		aKontakt = new ArrayList<String>();
		
		vDatenBank = new Vector<ArrayList<String>>();
		lesen =  ini.getIntegerProperty("DatenBank","AnzahlConnections") ;
		for (i=1;i<(lesen+1);i++){
			aKontakt.add(String.valueOf(ini.getStringProperty("DatenBank","DBTreiber"+i)) );
			aKontakt.add(String.valueOf(ini.getStringProperty("DatenBank","DBKontakt"+i)) );			
			aKontakt.add(String.valueOf(ini.getStringProperty("DatenBank","DBType"+i)) );
			String sbenutzer =String.valueOf(ini.getStringProperty("DatenBank","DBBenutzer"+i));
			aKontakt.add(String.valueOf(sbenutzer));
			String pw = String.valueOf(ini.getStringProperty("DatenBank","DBPasswort"+i));
			String decrypted = null;
			if(!pw.equals("")){
				if(pw != null){
					Verschluesseln man = Verschluesseln.getInstance();
					man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
					decrypted = man.decrypt (pw);
				}else{
					decrypted = String.valueOf("");
					JOptionPane.showMessageDialog(null,"Passwort der MySql-Datenbank = null");
				}
			}else{
				Object ret = JOptionPane.showInputDialog(null, "Geben Sie bitte das Passwort für die MySql-Datenbank ein", "");
				if(ret == null){
					decrypted = String.valueOf("");
				}else{
					decrypted = ((String)ret).trim();
					Verschluesseln man = Verschluesseln.getInstance();
					man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
					ini.setStringProperty("DatenBank","DBPasswort"+i,man.encrypt(((String)ret)),null);
					ini.save();
				}
			}

			aKontakt.add(decrypted);
			vDatenBank.add( (ArrayList<String>) aKontakt.clone());
			aKontakt.clear();
		}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return;
	}

	private void HauptFenster(){
		try{
			ini = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/rehajava.ini");
			
			AngelegtVonUser = ini.getBooleanProperty("SystemIntern", "AngelegtVonUser");
			
			//ini = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/terminkalender.ini");
			boolean mustsave = false;
			aHauptFenster = new ArrayList<String>();
			aHauptFenster.add(ini.getStringProperty("HauptFenster","Hintergrundbild"));
			aHauptFenster.add(ini.getStringProperty("HauptFenster","Bildgroesse"));			
			aHauptFenster.add(ini.getStringProperty("HauptFenster","FensterFarbeRGB"));
			aHauptFenster.add(ini.getStringProperty("HauptFenster","FensterTitel"));
			aHauptFenster.add(ini.getStringProperty("HauptFenster","LookAndFeel"));
			
			if ( ini.getStringProperty("HauptFenster", "HorizontalTeilen") != null ){
				desktopHorizontal = ((Integer)ini.getIntegerProperty("HauptFenster", "HorizontalTeilen") == 1 ? true : false) ;
			}else{
				ini.setStringProperty("HauptFenster", "HorizontalTeilen","1",null);
				mustsave = true;
				
			}
			if ( ini.getStringProperty("HauptFenster", "TP1Offen") != null ){
				for(int i = 1; i < 7; i++){
					taskPaneCollapsed[i-1] = (ini.getStringProperty("HauptFenster", "TP"+Integer.toString(i)+"Offen").equals("1") ? true : false);
				}
			}else{
				ini.setStringProperty("HauptFenster", "TP1Offen","0",null);
				ini.setStringProperty("HauptFenster", "TP2Offen","0",null);
				ini.setStringProperty("HauptFenster", "TP3Offen","1",null);
				ini.setStringProperty("HauptFenster", "TP4Offen","1",null);
				ini.setStringProperty("HauptFenster", "TP5Offen","0",null);
				ini.setStringProperty("HauptFenster", "TP6Offen","1",null);
				mustsave = true;
			}
			if(mustsave){
				ini.save();
			}

			
			OpenOfficePfad = ini.getStringProperty("OpenOffice.org","OfficePfad");
			if(!pfadOk(OpenOfficePfad)){
				String meldung = "Es konnte keine gültige OpenOffice-Installation entdeckt werden\n"+
				"Bislang zeigt der Pfad auf OO.org auf "+OpenOfficePfad+"\n\n"+
				"Öffnen Sie bitte in Thera-Pi die System-Initialisierung und\n"+
				"gehen Sie dann auf die Seite -> sonsige Einstellungen -> Fremdprogramme.\n\n"+
				"Stellen Sie dann bitte auf dieser Seite den Pfad zu OpenOffice.org ein\n\n"+
				"Auf der selben Seite stellen Sie dann bitte Ihren PDF-Reader ein\n\n"+
				"Abschließend beenden Sie bitte Thera-Pi und starten Sie dann Thera-Pi erneut\n\n"+
				"Everything should then be fine - wie der Schwabe zu sagen pflegt!";
				JOptionPane.showMessageDialog(null, meldung);
			}
			OpenOfficeNativePfad = ini.getStringProperty("OpenOffice.org","OfficeNativePfad");
			
			ini = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/nachrichten.ini");
			timerdelay = ini.getLongProperty("RehaNachrichten", "NachrichtenTimer");
			timerpopup = (ini.getIntegerProperty("RehaNachrichten", "NachrichtenPopUp") <= 0 ? false : true);
			timerprogressbar = (ini.getIntegerProperty("RehaNachrichten", "NachrichtenProgressbar") <= 0 ? false : true);

			wissenURL = ini.getStringProperty("WWW-Services","RTA-Wissen");
			homePageURL = ini.getStringProperty("WWW-Services","HomePage");		
			homeDir = Reha.proghome;
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		//homeDir = ini.getStringProperty("Application","HeimatVerzeichnis");
		////System.out.println("HomeDir = "+homeDir);
		return;
	}
	private static boolean pfadOk(String pfad){
		File f = new File(pfad);
		return f.exists();
	}
	
	@SuppressWarnings("unchecked")
	private void TerminKalender(){
		//ini = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/rehajava.ini");	
		INIFile termkalini = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/terminkalender.ini");
		//(TestFenster) eltern.splashText("Lade Kalenderparameter");
		aTerminKalender = new ArrayList<ArrayList<ArrayList<String[]>>>();
		ArrayList<String> aList1  = new ArrayList<String>();
		ArrayList<String[]> aList2 = new ArrayList<String[]>();
		ArrayList<ArrayList<ArrayList<String[]>>> aList3 = new ArrayList<ArrayList<ArrayList<String[]>>>();
		int lesen,i;
		lesen =  Integer.parseInt(String.valueOf(termkalini.getStringProperty("Kalender","AnzahlSets")) );
		for (i=1;i<(lesen+1);i++){
			aList1.add(String.valueOf(termkalini.getStringProperty("Kalender","NameSet"+i)) );
			aList2.add(String.valueOf(termkalini.getStringProperty("Kalender","FeldSet"+i)).split(",") );
			aList3.add((ArrayList)aList1.clone());
			aList3.add((ArrayList)aList2.clone());
			aTerminKalender.add((ArrayList)aList3.clone());
			aList1.clear();
			aList2.clear();
			aList3.clear();
		}	
		KalenderUmfang[0] = String.valueOf(termkalini.getStringProperty("Kalender","KalenderStart"));
		KalenderUmfang[1] = String.valueOf(termkalini.getStringProperty("Kalender","KalenderEnde"));	
		KalenderMilli[0] = ZeitFunk.MinutenSeitMitternacht(KalenderUmfang[0]);
		KalenderMilli[1] = ZeitFunk.MinutenSeitMitternacht(KalenderUmfang[1]);		
		KalenderBarcode =  (termkalini.getStringProperty("Kalender","KalenderBarcode").trim().equals("0") ? false : true );
		UpdateIntervall = Integer.valueOf(String.valueOf(termkalini.getStringProperty("Kalender","KalenderTimer")));
		/*ParameterLaden kolLad = */new ParameterLaden();
		AnzahlKollegen = ParameterLaden.vKKollegen.size()-1;
		String s = String.valueOf(termkalini.getStringProperty("Kalender","KalenderHintergrundRGB"));
		String[] ss = s.split(",");
		KalenderHintergrund = new Color(Integer.parseInt(ss[0]),Integer.parseInt(ss[1]),Integer.parseInt(ss[2]));
		KalenderAlpha = new Float(String.valueOf(termkalini.getStringProperty("Kalender","KalenderHintergrundAlpha")));
		////System.out.println("Anzal Kollegen = "+AnzahlKollegen);
		oTerminListe = new TerminListe().init();
		Reha.thisClass.setzeInitStand("Gruppendefinition einlesen");
		GruppenLesen();
		//oGruppen = new GruppenEinlesen().init();
		try{
			ini = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/kalender.ini");
			KalenderLangesMenue = (ini.getStringProperty("Kalender","LangesMenue").trim().equals("0") ? false : true );
			KalenderStartWochenAnsicht = (ini.getStringProperty("Kalender","StartWochenAnsicht").trim().equals("0") ? false : true );
			KalenderStartWADefaultUser = (ini.getStringProperty("Kalender","AnsichtDefault").split("@")[0]);
			KalenderStartNADefaultSet = (ini.getStringProperty("Kalender","AnsichtDefault").split("@")[1]);
			KalenderZeitLabelZeigen = (ini.getStringProperty("Kalender","ZeitLabelZeigen").trim().equals("0") ? false : true );
			if(ini.getStringProperty("Kalender", "ZeitLinieZeigen")==null){
				ini.setStringProperty("Kalender", "ZeitLinieZeigen", "0", null);
				ini.save();
				KalenderTimeLineZeigen = false;
			}else{
				KalenderTimeLineZeigen = (ini.getStringProperty("Kalender", "ZeitLinieZeigen").trim().equals("0") ? false : true);	
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
		
		return;
	}
	@SuppressWarnings("unchecked")
	public static void NurSets(){
		//ini = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/rehajava.ini");
		ini = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/terminkalender.ini");

		//(TestFenster) eltern.splashText("Lade Kalenderparameter");
		aTerminKalender = new ArrayList<ArrayList<ArrayList<String[]>>>();
		aTerminKalender.clear();
		ArrayList<String> aList1  = new ArrayList<String>();
		ArrayList<String[]> aList2 = new ArrayList<String[]>();
		ArrayList<ArrayList<ArrayList<String[]>>> aList3 = new ArrayList<ArrayList<ArrayList<String[]>>>();
		int lesen,i;
		lesen =  Integer.parseInt(String.valueOf(ini.getStringProperty("Kalender","AnzahlSets")) );
		for (i=1;i<(lesen+1);i++){
			aList1.add(String.valueOf(ini.getStringProperty("Kalender","NameSet"+i)) );
			aList2.add(String.valueOf(ini.getStringProperty("Kalender","FeldSet"+i)).split(",") );
			aList3.add((ArrayList)aList1.clone());
			aList3.add((ArrayList)aList2.clone());
			aTerminKalender.add((ArrayList)aList3.clone());
			aList1.clear();
			aList2.clear();
			aList3.clear();
		}	
	}
	@SuppressWarnings("unchecked")
	public static void  RoogleGruppen(){
		INIFile roogleini = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/terminkalender.ini");
		//ini = new INIFile("c:\\RehaVerwaltung\\ini\\rehajava.ini");
		aRoogleGruppen = new ArrayList<ArrayList<ArrayList<String[]>>>();
		int lesen,i;
		lesen =  Integer.parseInt(String.valueOf(roogleini.getStringProperty("RoogleEinstellungen","RoogleAnzahlGruppen")) );
		ArrayList<String> aList1  = new ArrayList<String>();
		ArrayList<String[]> aList2 = new ArrayList<String[]>();
		ArrayList<ArrayList<ArrayList<String[]>>> aList3 = new ArrayList<ArrayList<ArrayList<String[]>>>();

		for(i=1;i<=lesen;i++){
			aList1.add(String.valueOf(roogleini.getStringProperty("RoogleEinstellungen","RoogleNameGruppen"+i)) );
			aList2.add((String[]) String.valueOf(roogleini.getStringProperty("RoogleEinstellungen","RoogleFelderGruppen"+i)).split(",") );
			aList3.add((ArrayList)aList1.clone());
			aList3.add((ArrayList) aList2.clone());
			aRoogleGruppen.add((ArrayList)aList3.clone());
			aList1.clear();
			aList2.clear();
			aList3.clear();
		}
		for(i=0;i<7;i++){
			RoogleTage[i] = (roogleini.getStringProperty("RoogleEinstellungen","Tag"+(i+1)).trim().equals("0") ? false : true );
		}
		RoogleZeitraum = Integer.valueOf(roogleini.getStringProperty("RoogleEinstellungen","Zeitraum"));
		RoogleZeiten = new  HashMap<String,String>();
		RoogleZeiten.put("KG",roogleini.getStringProperty("RoogleEinstellungen","KG") );
		RoogleZeiten.put("MA",roogleini.getStringProperty("RoogleEinstellungen","MA") );
		RoogleZeiten.put("ER",roogleini.getStringProperty("RoogleEinstellungen","ER") );
		RoogleZeiten.put("LO",roogleini.getStringProperty("RoogleEinstellungen","LO") );
		RoogleZeiten.put("SP",roogleini.getStringProperty("RoogleEinstellungen","SP") );
	}

	private void EmailParameter(){
		INIFile emailini = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/email.ini");
		//ini = new INIFile("c:\\RehaVerwaltung\\ini\\rehajava.ini");
		hmEmailExtern = new HashMap<String,String>();
		hmEmailExtern.put("SmtpHost",emailini.getStringProperty("EmailExtern","SmtpHost"));
		hmEmailExtern.put("SmtpAuth",emailini.getStringProperty("EmailExtern","SmtpAuth"));			
		hmEmailExtern.put("Pop3Host",emailini.getStringProperty("EmailExtern","Pop3Host"));
		hmEmailExtern.put("Username",emailini.getStringProperty("EmailExtern","Username"));
		String pw = emailini.getStringProperty("EmailExtern","Password");
		Verschluesseln man = Verschluesseln.getInstance();
	    man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
		String decrypted = man.decrypt (pw);
		hmEmailExtern.put("Password",decrypted);
		hmEmailExtern.put("SenderAdresse",emailini.getStringProperty("EmailExtern","SenderAdresse"));			
		hmEmailExtern.put("Bestaetigen",emailini.getStringProperty("EmailExtern","EmpfangBestaetigen"));			
		/********************/
		hmEmailIntern = new HashMap<String,String>();
		hmEmailIntern.put("SmtpHost",emailini.getStringProperty("EmailIntern","SmtpHost"));
		hmEmailIntern.put("SmtpAuth",emailini.getStringProperty("EmailIntern","SmtpAuth"));			
		hmEmailIntern.put("Pop3Host",emailini.getStringProperty("EmailIntern","Pop3Host"));
		hmEmailIntern.put("Username",emailini.getStringProperty("EmailIntern","Username"));
		pw = emailini.getStringProperty("EmailIntern","Password");
		man = Verschluesseln.getInstance();
	    man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
		decrypted = man.decrypt (pw);
		hmEmailIntern.put("Password",decrypted);
		hmEmailIntern.put("SenderAdresse",emailini.getStringProperty("EmailIntern","SenderAdresse"));			
		hmEmailIntern.put("Bestaetigen",emailini.getStringProperty("EmailIntern","EmpfangBestaetigen"));	
		if(new File(Reha.proghome+"ini/"+Reha.aktIK+"/dta301.ini").exists()){
			INIFile dtaini = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/dta301.ini");
			dta301InBox = dtaini.getStringProperty("DatenPfade301", "inbox");
			dta301OutBox = dtaini.getStringProperty("DatenPfade301", "outbox");
		}

	}	
	
	private void Verzeichnisse(){
		ini = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/rehajava.ini");
		hmVerzeichnisse = new HashMap<String,String>();
		hmVerzeichnisse.put("Programmverzeichnis",String.valueOf(Reha.proghome));
		hmVerzeichnisse.put("Vorlagen",String.valueOf(Reha.proghome+"vorlagen/"+Reha.aktIK));
		hmVerzeichnisse.put("Icons",String.valueOf(Reha.proghome+"icons"));
		hmVerzeichnisse.put("Temp",String.valueOf(Reha.proghome+"temp/"+Reha.aktIK));		
		hmVerzeichnisse.put("Ini",String.valueOf(Reha.proghome+"ini/"+Reha.aktIK));		
		hmVerzeichnisse.put("Rehaplaner",ini.getStringProperty("Verzeichnisse", "Rehaplaner"));
		hmVerzeichnisse.put("Fahrdienstliste",ini.getStringProperty("Verzeichnisse", "Fahrdienstliste"));
		hmVerzeichnisse.put("Fahrdienstrohdatei",ini.getStringProperty("Verzeichnisse", "Fahrdienstrohdatei"));
	}
	
	@SuppressWarnings("unchecked")
	private void TKFarben(){
		if (colini==null){
			colini = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/color.ini");
		}
		////System.out.println("In TK-Farben");
		int anz  = Integer.valueOf( String.valueOf(colini.getStringProperty("Terminkalender","FarbenAnzahl")));
		vSysColsNamen = new Vector<String>();
		vSysColsBedeut = new Vector<String>();
		vSysColsCode = new Vector<String>();
		//ArrayList<String> colnames = new ArrayList<String>();
		for(int i = 0;i<anz;i++){
			vSysColsNamen.add(String.valueOf(colini.getStringProperty("Terminkalender","FarbenNamen"+(i+1))));
			vSysColsBedeut.add(String.valueOf(colini.getStringProperty("Terminkalender","FarbenBedeutung"+(i+1))));
			vSysColsCode.add(String.valueOf(colini.getStringProperty("Terminkalender","FarbenCode"+(i+1))));			
			//colnames.add(String.valueOf(colini.getStringProperty("Terminkalender","FarbenNamen")));
		}
		int def = Integer.valueOf( String.valueOf(colini.getStringProperty("Terminkalender","FarbenDefaults")));
		vSysDefNamen = new Vector<String>();
		//ArrayList<String> defnames = new ArrayList<String>();		
		for(int i = 0;i<def;i++){
			vSysDefNamen.add(String.valueOf(colini.getStringProperty("Terminkalender","FarbenDefaultNamen"+(i+1))));
			//defnames.add(String.valueOf(colini.getStringProperty("Terminkalender","FarbenDefaultNamen")));
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

			String[] farb = String.valueOf(colini.getStringProperty( "UserFarben",vSysColsNamen.get(j))).split(",");
			Color[] farbe = new Color[2];
			farbe[0] = new Color(Integer.valueOf(farb[0]),Integer.valueOf(farb[1]),Integer.valueOf(farb[2]));
			farbe[1] = new Color(Integer.valueOf(farb[3]),Integer.valueOf(farb[4]),Integer.valueOf(farb[5]));
			colv.add(farbe);
		}
		vSysColsObject.add((Vector<Color[]>)colv.clone());
		
		
		for(int i = 0; i < def; i++){
			//Anzahl der Sets
			colv = new Vector<Color[]>();
			for(int j = 0; j < anz;j++){
				////System.out.println("Bei i="+i+" /  und j="+j);
				String[] farb = String.valueOf(colini.getStringProperty( vSysDefNamen.get(i),vSysColsNamen.get(j))).split(",");
				Color[] farbe = new Color[2];
				farbe[0] = new Color(Integer.valueOf(farb[0]),Integer.valueOf(farb[1]),Integer.valueOf(farb[2]));
				farbe[1] = new Color(Integer.valueOf(farb[3]),Integer.valueOf(farb[4]),Integer.valueOf(farb[5]));
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
	@SuppressWarnings({ "unchecked" })
	public static void MandantenEinlesen(){

		INIFile inif = new INIFile(Reha.proghome+"ini/mandanten.ini");
		int AnzahlMandanten = inif.getIntegerProperty("TheraPiMandanten", "AnzahlMandanten");
		AuswahlImmerZeigen = inif.getIntegerProperty("TheraPiMandanten", "AuswahlImmerZeigen");
		DefaultMandant = inif.getIntegerProperty("TheraPiMandanten", "DefaultMandant");
		//int LetzterMandant = inif.getIntegerProperty("TheraPiMandanten", "LetzterMandant");
		Mandanten = new Vector<String[]>();			
		for(int i = 0; i < AnzahlMandanten;i++){
			String[] mand = {null,null};
			mand[0] = String.valueOf(inif.getStringProperty("TheraPiMandanten", "MAND-IK"+(i+1)));
			mand[1] = String.valueOf(inif.getStringProperty("TheraPiMandanten", "MAND-NAME"+(i+1)));
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
				decrypted = String.valueOf("");
			}
			mandantDB.add(String.valueOf(decrypted));
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
			String pw = String.valueOf(inif.getStringProperty("TheraPiHilfe","HilfeDBPassword"));
			Verschluesseln man = Verschluesseln.getInstance();
		    man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
			String decrypted = man.decrypt (pw);
			hmHilfeServer.put("HilfeDBPassword", decrypted);			
		}
	}	
 

	public static void UpdateIni(String inidatei,String gruppe,String element,String wert){

		INIFile	updateini = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/"+inidatei);
	
		updateini.setStringProperty(gruppe, element, wert, null);
		updateini.save();
	}
	public static void UpdateIni(INIFile inidatei,String gruppe,String element,Object wert,String hinweis){
		if(wert instanceof java.lang.String){
			inidatei.setStringProperty(gruppe, element, (String)wert, hinweis);			
		}else if(wert instanceof java.lang.Integer){
			inidatei.setIntegerProperty(gruppe, element, (Integer)wert, hinweis);
		}
		//System.out.println(inidatei.getFileName()+" - Gruppe="+gruppe+" / Element="+element+" / Wert="+wert );
		inidatei.save();
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
		//System.out.println("Neue Preise ab="+vNeuePreiseAb);
		//System.out.println("Neue Preise Regeln = "+vNeuePreiseRegel);

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
				,"<Rposition4>","<Rpreis1>","<Rpreis2>","<Rpreis3>","<Rpreis4>","<Rproz1>","<Rproz2>","<Rproz3>"
				,"<Rproz4>","<Rgesamt1>","<Rgesamt2>","<Rgesamt3>","<Rgesamt4>","<Rpauschale>","<Rendbetrag>","<Ranzahl1>"
				,"<Ranzahl2>","<Ranzahl3>","<Ranzahl4>","<Rerstdat>","<Rletztdat>","<Rid>","<Rtage>","<Rkurz1>","<Rkurz2"
				,"<Rkurz3>","<Rkurz4>","<Rlang1>","<Rlang2>","<Rlang3>","<Rlang4>","<Rbarcode>","<Systemik>","<Rwert>"
				,"<Rhbpos>","<Rwegpos>","<Rhbpreis>","<Rwegpreis>","<Rhbproz>","<Rwegproz>","<Rhbanzahl>","<Rweganzahl>"
				,"<Rhbgesamt>","<Rweggesamt>","<Rwegkm>","<Rtage>"});
		for(int i = 0; i < lAdrRDaten.size(); i++){
			hmAdrRDaten.put(lAdrRDaten.get(i),"");
		}
		/********************/		
		hmAdrBDaten = new HashMap<String,String>();
		List<String> lAdrBDaten = Arrays.asList(new String[]{"<Badr1>","<Badr2>","<Badr3>","<Badr4>","<Badr5>","<Bbanrede>",
				"<Bihrenpat>","<Bdisziplin>","<Bdiagnose>","<Breznr>","<Brezdatum>","<Bblock1>","<Bblock2>","<Bblock3>","<Bblock4>",
				"<Btitel1>","<Btitel2>","<Btitel3>","<Btitel4>","<Bnname>","<Bvnname>","<Bgeboren>","<Btherapeut>",
				"<Berstdat>","<Bletztdat>","<Banzahl1>","<Banzahl2>","<Banzahl3>","<Banzahl4>",
				"<Blang1>","<Blang2>","<Blang3>","<Blang4>"});
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
		String[] fenster = {"Kasse","Patient","Kalender","Arzt","Gutachten","Abrechnung"};
		String[] files = {"kasse.ini","patient.ini","kalender.ini","arzt.ini","gutachten.ini","abrechnung.ini"};
		INIFile inif = null;
		for(int i = 0; i < fenster.length;i++){
			inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/"+files[i]);
			
			//desktopPane 0 oder 1
			if(inif.getIntegerProperty("Container", "StarteIn")==null)
				UpdateIni(inif,"Container","StarteIn",1,null);
			hmContainer.put(fenster[i], inif.getIntegerProperty("Container", "StarteIn"));
			//immer auf maximale Größe
			if(inif.getIntegerProperty("Container", "ImmerOptimieren")==null)
				UpdateIni(inif,"Container","ImmerOptimieren",(fenster[i].equals("Kalender") ? 1 : 0),null);
			hmContainer.put(fenster[i]+"Opti", inif.getIntegerProperty("Container", "ImmerOptimieren"));
			//X-Position
			if(inif.getIntegerProperty("Container", "ZeigeAnPositionX")==null){
				UpdateIni(inif,"Container","ZeigeAnPositionX",5,null);
				if(fenster[i].equals("Kalender")){
					hmContainer.put(fenster[i]+"Opti",1);		
				}
			}
			hmContainer.put(fenster[i]+"LocationX", inif.getIntegerProperty("Container", "ZeigeAnPositionX"));
			//Y-Position
			if(inif.getIntegerProperty("Container", "ZeigeAnPositionY")==null)
				UpdateIni(inif,"Container","ZeigeAnPositionY",5,null);
			hmContainer.put(fenster[i]+"LocationY", inif.getIntegerProperty("Container", "ZeigeAnPositionY"));
			//X-Größe
			if(inif.getIntegerProperty("Container", "DimensionX")==null)
				UpdateIni(inif,"Container","DimensionX",-1,null);
			hmContainer.put(fenster[i]+"DimensionX", inif.getIntegerProperty("Container", "DimensionX"));
			//Y-Größe
			if(inif.getIntegerProperty("Container", "DimensionY")==null)
				UpdateIni(inif,"Container","DimensionY",-1,null);
			hmContainer.put(fenster[i]+"DimensionY", inif.getIntegerProperty("Container", "DimensionY"));
		}
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
			boolean mustsave = false;
			if(inif.getStringProperty("KartenLeser", "KartenLeserCTAPILib")==null){
				inif.setStringProperty("KartenLeser", "KartenLeserCTAPILib","ctpcsc31kv",null);
				mustsave = true;
			}
			if(inif.getStringProperty("KartenLeser", "KartenLeserDeviceID")==null){
				inif.setStringProperty("KartenLeser", "KartenLeserDeviceID","0",null);
				mustsave = true;
			}
			if(mustsave){
				inif.save();
			}

			sReaderCtApiLib = inif.getStringProperty("KartenLeser", "KartenLeserCTAPILib");
			sReaderDeviceID = inif.getStringProperty("KartenLeser", "KartenLeserDeviceID");
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
			hmKVKDaten.put("Anrede", "");
		}else{
			sReaderName = "";
			sReaderAktiv = "0";
			sReaderCtApiLib = inif.getStringProperty("KartenLeser", "KartenLeserCTAPILib");			
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
				arztGruppen[i] =  inif.getStringProperty("ArztGruppen", "Gruppe"+Integer.valueOf(i+1).toString());
			}
		}
	}
	@SuppressWarnings("unchecked")
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
			rezeptKlassen[i] = inif.getStringProperty("RezeptKlassen", "Klasse"+Integer.valueOf(i+1).toString());
			aktiv = inif.getIntegerProperty("RezeptKlassen", "KlasseAktiv"+Integer.valueOf(i+1).toString());
			if(aktiv > 0){
				vec.clear();
				vec.add(String.valueOf(rezeptKlassen[i]));
				vec.add(inif.getStringProperty("RezeptKlassen", "KlasseKurz"+Integer.valueOf(i+1).toString())  );
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
		//System.out.println(rezeptKlassenAktiv);

	}
	@SuppressWarnings("unchecked")
	public static void TherapBausteinInit() {
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/thbericht.ini");
		hmTherapBausteine = new HashMap<String,Vector<String>>();
		int lang = rezeptKlassenAktiv.size();
		Vector<String> vec = new Vector<String>();
		for(int i = 0; i<lang;i++){
			vec.clear();
			String prop = "AnzahlThemen_"+RezTools.putRezNrGetDisziplin(rezeptKlassenAktiv.get(i).get(1));
			int lang2 = inif.getIntegerProperty("Textbausteine", prop);
			String prop2 = RezTools.putRezNrGetDisziplin(rezeptKlassenAktiv.get(i).get(1));
			//System.out.println("****************************"+prop);
			//System.out.println("****************************"+prop2);
			//String gelenk;
			for(int i2 = 0; i2 < lang2;i2++ ){
				//vec.add( inif.getStringProperty("Textbausteine", prop2+(i2+1)) );
				vec.add( inif.getStringProperty(prop2, "Thema"+(i2+1)) );
			}
			hmTherapBausteine.put(prop2, (Vector<String>) vec.clone());
		}
		//System.out.println(hmTherapBausteine);
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
	
	
	// Lemmi 20101224 Steuerparanmeter für RGR und AFR in OffenPosten und Mahnungen, zentral einlesen
	public static void OffenePostenIni_ReadFromIni(){
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/offeneposten.ini");

		// Voreinstellung von Defaultwerten
		hmZusatzInOffenPostenIni.put("RGRinOPverwaltung", 0);
		hmZusatzInOffenPostenIni.put("AFRinOPverwaltung", 0);

		try{
			if ( inif.getStringProperty("ZusaetzlicheRechnungen", "RGRinOPverwaltung") != null )  // Prüfung auf Existenz
				hmZusatzInOffenPostenIni.put("RGRinOPverwaltung", inif.getIntegerProperty("ZusaetzlicheRechnungen", "RGRinOPverwaltung"));
			if ( inif.getStringProperty("ZusaetzlicheRechnungen", "AFRinOPverwaltung") != null )  // Prüfung auf Existenz
				hmZusatzInOffenPostenIni.put("AFRinOPverwaltung", inif.getIntegerProperty("ZusaetzlicheRechnungen", "AFRinOPverwaltung"));
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null,"Die Datei 'offeneposten.ini' zur aktuellen IK-Nummer kann nicht gelesen werden.");
		}
	}
	

	// Lemmi 20101223 Steuerparanmeter für den Patienten-Suchen-Dialog aus der INI zentral einlesen
	public static void BedienungIni_ReadFromIni(){
		boolean mustsave = false;
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/bedienung.ini");
		
//		if ( inif.IsFileLoaded() )
//			int x = 5;
		// funktioniert auch mit Defaultwerten, wenn die INI-Datei bis dato NICHT existiert. In BedienungIni_WriteToIni()
		// wird dann eine komplette INI angelegt !
		
		// Voreinstellung von Defaultwerten
		hmPatientenWerkzeugDlgIni.put("ToolsDlgClickCount", 2);
		if ( inif.getStringProperty("Bedienung", "WerkzeugaufrufMausklicks") != null )  // Prüfung auf Existenz
			hmPatientenWerkzeugDlgIni.put("ToolsDlgClickCount",inif.getIntegerProperty("Bedienung", "WerkzeugaufrufMausklicks") );

		hmPatientenWerkzeugDlgIni.put("ToolsDlgShowButton", false);
		if ( inif.getStringProperty("Bedienung", "WerkzeugaufrufButtonZeigen") != null )  // Prüfung auf Existenz
			hmPatientenWerkzeugDlgIni.put("ToolsDlgShowButton",(Integer)inif.getIntegerProperty("Bedienung", "WerkzeugaufrufButtonZeigen") == 1 ? true : false );
		//System.out.println("Default1 = "+hmPatientenWerkzeugDlgIni.get("ToolsDlgClickCount"));
		
		// Lemmi 20110116: Abfrage Abbruch bei Rezeptänderungen mit Warnung
		hmRezeptDlgIni.put("RezAendAbbruchWarn", false);
		if ( inif.getStringProperty("Rezept", "RezeptAenderungAbbruchWarnung") != null )  // Prüfung auf Existenz
			hmRezeptDlgIni.put("RezAendAbbruchWarn",(Integer)inif.getIntegerProperty("Rezept", "RezeptAenderungAbbruchWarnung") == 1 ? true : false );
		
		///Zeigt den Terminbestätigungsdialog wenn erforderlich, siehe Erweiterung von Drud
		//Ist der Wert false wird der Dialog nie gezeigt
		//mit Strg+F11 anstatt Shift+F11 kann die Anzeige des Dialoges
		//aber unabhängig von dieser Einstellung erzwungen werden /st.
		if ( inif.getStringProperty("Termine", "HMDialogZeigen") != null ){
			hmTerminBestaetigen.put("dlgzeigen",((Integer)inif.getIntegerProperty("Termine", "HMDialogZeigen") == 1 ? true : false) );
		}else{
			hmTerminBestaetigen.put("dlgzeigen",false);
			inif.setStringProperty("Termine", "HMDialogZeigen","0",null);
			mustsave = true;
		}
		//nur wenn unterschiedliche Anzahlen im Rezept vermerkt und auch nur solange
		//bis es nichts mehr zu zeigen gibt, sprich die Heilmittel mit der geringeren Anzahl
		//bereits bis zur Maximalanzahl bestätigt wurden
		//die dahinterliegende Funktion ist noch nicht implementiert
		//Persönlicher Wunsch von mir: Drud's Job /st.
		if ( inif.getStringProperty("Termine", "HMDialogDiffZeigen") != null ){
			hmTerminBestaetigen.put("dlgdiffzeigen",((Integer)inif.getIntegerProperty("Termine", "HMDialogDiffZeigen") == 1 ? true : false) );
		}else{
			hmTerminBestaetigen.put("dlgdiffzeigen",false);
			inif.setStringProperty("Termine", "HMDialogDiffZeigen","0",null);
			mustsave = true;
		}

		// Voreinstellung von Defaultwerten
		hmPatientenSuchenDlgIni.put("suchart",0);
		hmPatientenSuchenDlgIni.put("fensterbreite", 300);
		hmPatientenSuchenDlgIni.put("fensterhoehe", 400);
		try{
			if ( inif.getStringProperty("PatientenSuche", "Suchart") != null )  // Prüfung auf Existenz
				hmPatientenSuchenDlgIni.put("suchart", inif.getIntegerProperty("PatientenSuche", "Suchart"));
			if ( inif.getStringProperty("PatientenSuche", "SuchFensterBreite") != null )  // Prüfung auf Existenz
				hmPatientenSuchenDlgIni.put("fensterbreite", inif.getIntegerProperty("PatientenSuche", "SuchFensterBreite"));
			if ( inif.getStringProperty("PatientenSuche", "SuchFensterHoehe") != null )  // Prüfung auf Existenz
				hmPatientenSuchenDlgIni.put("fensterhoehe", inif.getIntegerProperty("PatientenSuche", "SuchFensterHoehe"));
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null,"Die Datei 'bedienung.ini' zur aktuellen IK-Nummer kann nicht gelesen werden.");
		}
		if(mustsave){
			inif.save();
		}
	}

	// Lemmi 20101223 Steuerparanmeter für den Patienten-Suchen-Dialog in die INI schreiben	
	public static void BedienungIni_WriteToIni(){
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/bedienung.ini");

		// Sofern alle Parameter hier wieder in die INI geschrieben werden, legt das eine komplette INI an !
	
		inif.setIntegerProperty("Bedienung", "WerkzeugaufrufMausklicks", Integer.parseInt(hmPatientenWerkzeugDlgIni.get("ToolsDlgClickCount").toString()), " Anzahl Klicks für Werkzeugaufruf");
		inif.setIntegerProperty("Bedienung", "WerkzeugaufrufButtonZeigen", (Boolean)hmPatientenWerkzeugDlgIni.get("ToolsDlgShowButton") ? 1 : 0, " Zusatzknopf im Werkzeugdialog");
		
		inif.setIntegerProperty("PatientenSuche", "SuchFensterBreite", hmPatientenSuchenDlgIni.get("fensterbreite"), " letzte Breite des Suchfensters");
		inif.setIntegerProperty("PatientenSuche", "SuchFensterHoehe", hmPatientenSuchenDlgIni.get("fensterhoehe"), " letzte Höhe des Suchfensters");
		inif.setIntegerProperty("PatientenSuche", "Suchart", hmPatientenSuchenDlgIni.get("suchart"), " letzte angewählte Suchart Suchfensters");

		// Lemmi 20110116: Abfrage Abbruch bei Rezeptänderungen mit Warnung
		inif.setIntegerProperty("Rezept", "RezeptAenderungAbbruchWarnung", (Boolean)hmRezeptDlgIni.get("RezAendAbbruchWarn") ? 1 : 0, " Abfrage Abbruch bei Rezeptänderungen mit Warnung");
		inif.setIntegerProperty("Termine", "HMDialogZeigen", (Boolean)hmTerminBestaetigen.get("dlgzeigen") ? 1 : 0, null);
		inif.setIntegerProperty("Termine", "HMDialogZeigen", (Boolean)hmTerminBestaetigen.get("dlgzeigen") ? 1 : 0, null);
		inif.setIntegerProperty("Termine", "HMDialogDiffZeigen",(Boolean)hmTerminBestaetigen.get("dlgdiffzeigen") ? 1 : 0, null);

		inif.save();  // Daten wegschreiben
		
		ini = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/rehajava.ini");
		ini.setStringProperty("HauptFenster", "HorizontalTeilen",(SystemConfig.desktopHorizontal ? "1" : "0"),null);
		ini.save();
/*		
		// Wegschreiben der INI-Parameter in die Datenbank TESTHALBER
		inidb.WritePropInteger("bedienung.ini", "Bedienung", "WerkzeugaufrufMausklicks", 
								Integer.parseInt(hmPatientenWerkzeugDlgIni.get("ToolsDlgClickCount").toString()), "Anzahl Klicks für Werkzeugaufruf");
		inidb.WritePropInteger("bedienung.ini", "Bedienung", "WerkzeugaufrufButtonZeigen", 
								(Boolean)hmPatientenWerkzeugDlgIni.get("ToolsDlgShowButton") ? 1 : 0, "Zusatzknopf im Werkzeugdialog");
	
		inidb.WritePropInteger("bedienung.ini", "PatientenSuche", "SuchFensterBreite", 
								hmPatientenSuchenDlgIni.get("fensterbreite"), "letzte Breite des Suchfensters"); 
		inidb.WritePropInteger("bedienung.ini", "PatientenSuche", "SuchFensterHoehe", 
								hmPatientenSuchenDlgIni.get("fensterhoehe"), " letzte Höhe des Suchfensters");
		inidb.WritePropInteger("bedienung.ini", "PatientenSuche", "Suchart", 
								hmPatientenSuchenDlgIni.get("suchart"), " letzte angewählte Suchart im Suchfenster");
*/		
	}
	
	
	@SuppressWarnings("unchecked")
	public static void FremdProgs(){
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/fremdprog.ini");
		vFremdProgs = new Vector<Vector<String>>();
		Vector<String> progs = new Vector<String>();
		int anzahl =  inif.getIntegerProperty("FremdProgramme", "FremdProgrammeAnzahl");
		for(int i = 0; i < anzahl; i++){
			progs.clear();
			progs.add(inif.getStringProperty("FremdProgramme", "FremdProgrammName"+(i+1)));
			progs.add(inif.getStringProperty("FremdProgramme", "FremdProgrammPfad"+(i+1)));
			vFremdProgs.add((Vector<String>)progs.clone());
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
		String[] string2 = new String[anzahl];
		boolean speichern = false;
		for(int i = 0; i < anzahl; i++){
			string[i] = inif.getStringProperty("KartenLeserListe", "Leser"+(i+1));
			if(inif.getStringProperty("KartenLeserListe", "CTAPILib"+(i+1))==null){
				speichern = true;
				inif.setStringProperty("KartenLeserListe", "CTAPILib"+(i+1),"ctpcsc31kv",null);
			}
			string2[i] = inif.getStringProperty("KartenLeserListe", "CTAPILib"+(i+1));
		}
		sWebCamActive = inif.getStringProperty("WebCam", "WebCamActive");
		if(sWebCamActive == null){
			speichern = true;
			sWebCamActive = "0";
			inif.setStringProperty("WebCam", "WebCamActive","0",null);
		}
		String dummy = inif.getStringProperty("WebCam", "WebCamX");
		if(dummy == null){
			speichern = true;
			inif.setIntegerProperty("WebCam", "WebCamX",sWebCamSize[0],null);
			inif.setIntegerProperty("WebCam", "WebCamY",sWebCamSize[1],null);
		}
		//sWebCamActive
		if(speichern){inif.save();}
		sWebCamSize[0] = inif.getIntegerProperty("WebCam", "WebCamX");
		sWebCamSize[1] = inif.getIntegerProperty("WebCam", "WebCamY");

		
		hmGeraete.put("Kartenleser", string.clone());
		hmGeraete.put("CTApi", string2.clone());

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
		String sask = inif.getStringProperty("GemeinsameParameter", "FragenVorEmail");
		if(sask==null){
			System.out.println("Erstelle Parameter 'FrageVorEmail'");
			inif.setStringProperty("GemeinsameParameter", "FragenVorEmail","1",null);
			inif.save();
		}
		hmAbrechnung.put("hmaskforemail", inif.getStringProperty("GemeinsameParameter", "FragenVorEmail"));
		//sask = inif.getStringProperty("GemeinsameParameter", "ZuzahlmodusNormal");
		
		String INI_FILE = "";
		if(System.getProperty("os.name").contains("Windows")){
			INI_FILE = Reha.proghome+ "nebraska_windows.conf";
		}else if(System.getProperty("os.name").contains("Linux")){
			INI_FILE = Reha.proghome+ "nebraska_linux.conf";			
		}else if(System.getProperty("os.name").contains("String für MaxOSX????")){
			INI_FILE = Reha.proghome+"nebraska_mac.conf";
		}
		Verschluesseln man = Verschluesseln.getInstance();
		man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
		try{
			inif = new INIFile(INI_FILE);
			String pw = null;
			String decrypted = null;
			hmAbrechnung.put("hmkeystorepw", "");
			int anzahl = inif.getIntegerProperty("KeyStores", "KeyStoreAnzahl");
			for(int i = 0; i < anzahl;i++){
				if(inif.getStringProperty("KeyStores", "KeyStoreAlias"+Integer.toString(i+1)).trim().equals("IK"+Reha.aktIK)){
					pw = inif.getStringProperty("KeyStores", "KeyStorePw"+Integer.toString(i+1));
					decrypted = man.decrypt(pw);
					hmAbrechnung.put("hmkeystorepw", decrypted);
					break;
				}
			}

		}catch(Exception ex){
			JOptionPane.showMessageDialog(null,"Zertifikatsdatenbank nicht vorhanden oder fehlerhaft.\nAbrechnung nach § 302 kann nicht durchgeführt werden.");
		}
		//System.out.println("Keystore-Passwort = "+hmAbrechnung.get("hmkeystorepw"));
	}
	
	public static void JahresUmstellung(){
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/rehajava.ini");
		aktJahr = inif.getStringProperty("SystemIntern","AktJahr");
		String jahrHeute = DatFunk.sHeute().substring(6);
		if(! aktJahr.equals(jahrHeute) ){
			/*
			JOptionPane.showMessageDialog(null, "Wichtiger Hinweis!!!!!\n\nDer letzte Programmstart war im Kalenderjahr -->"+aktJahr+"\n"+
					"Bitte fragen Sie den Administrator ob alle Befreiungen des Jahes "+aktJahr+" zurückgesetzt wurden\n"+
					"Beginnen Sie erst dann mit der Arbeit wenn sichergestellt ist daß alle Jahresabschlußarbeiten erledigt worden sind!!!!");
			//System.out.println("Aktuelles Jahr wurde veränder auf "+jahrHeute);
			 */
			aktJahr = String.valueOf(jahrHeute);

			inif.setStringProperty("SystemIntern","AktJahr",jahrHeute,null);
			inif.save();
		}else{
			//System.out.println("Aktuelles Jahr ist o.k.: "+jahrHeute);
		}
		vorJahr = Integer.valueOf(Integer.valueOf(aktJahr)-1).toString();
		String umstellung = SqlInfo.holeEinzelFeld("select altesjahr from jahresabschluss LIMIT 1" );
		if( (Integer.parseInt(jahrHeute) - Integer.parseInt(umstellung)) > 1){
			String htmlstring = "<html><b><font color='#ff0000'>Achtung !</font><br><br>Die Umstellung der Rezeptgebührbefreiungen aus<br>"+
				"dem <font color='#ff0000'>Kalenderjahr "+vorJahr+"</font> "+
				"wurde noch nicht durchgeführt.<br><br>Rezeptebühren und Kassenabrechnung können deshalb fehlerhaft sein.<br><br>"+
				"Bitte informieren Sie den Systemadministrator umgehend<br><br>"+
				"Sollten Sie die Berechtigung für die Umstellung haben, <font color='#ff0000'>stellen Sie bitte selbst um:</font><br>"+
				"System-Initialisierung -> sonstige Einstellungen -> Befreiungen zurücksetzen/Jahreswechsel</b></html>";
			JOptionPane.showMessageDialog(null,htmlstring);
		}
	}
	
	public static void SystemIconsInit(){
		String[] bilder = {"neu","edit","delete","print","save","find","stop","zuzahlfrei","zuzahlok","zuzahlnichtok",
				"nichtgesperrt","rezeptgebuehr","ausfallrechnung","arztbericht","privatrechnung",
				"sort","historieumsatz","historietage","historieinfo","keinerezepte","hausbesuch","historie","kvkarte",
				"ooowriter","ooocalc","oooimpress","openoffice","barcode","info","scanner","email","sms","tools","links",
				"rechts","abbruch","pdf","euro","einzeltage","info2","bild","patbild","bunker","camera","oofiles",
				"kleinehilfe","achtung","vorschau","patstamm","arztstamm","kassenstamm","drvlogo","personen16",
				"forward","wecker16","mond","roogle","scannergross","rot","gruen","inaktiv","buttonrot","buttongruen",
				"statusoffen","statuszu","statusset","abschliessen","bombe","openoffice26","tporgklein","information","undo","redo",
				"abrdreizwei","abriv","att","close","confirm","copy","cut","day","dayselect","down","left","minimize","paste","patsearch",
				"quicksearch","refresh","right","search","tellist","termin","upw","week","abrdreieins","ebcheck","hbmehrere","verkaufArtikel",
				"verkaufLieferant", "verkaufTuten"};
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/icons.ini");
		hmSysIcons = new HashMap<String,ImageIcon>();
		Image ico = null;
		
		int xscale = 0;
		int yscale = 0;
		int lang = bilder.length;
		int i;
		for(i = 0; i < lang; i++){
			try{
				////System.out.println(Reha.proghome+"icons/"+inif.getStringProperty("Icons", bilder[i]));
				xscale = inif.getIntegerProperty("Icons", bilder[i]+"ScaleX");
				yscale = inif.getIntegerProperty("Icons", bilder[i]+"ScaleY");
				if((xscale >0) && (yscale > 0)){
					ico = new ImageIcon(Reha.proghome+"icons/"+inif.getStringProperty("Icons", bilder[i])).getImage().getScaledInstance(xscale,yscale, Image.SCALE_SMOOTH);
					hmSysIcons.put(bilder[i], new ImageIcon(ico));				
				}else{
					hmSysIcons.put(bilder[i], new ImageIcon(Reha.proghome+"icons/"+inif.getStringProperty("Icons", bilder[i])));
				}
			}catch(Exception ex){
				System.out.println("Fehler!!!!!!!!! bei Bild: "+bilder[i]+". Fehler->Bilddatei existiert nicht");
				//ex.printStackTrace();
			}
			ico = null;
		}
		//Reha.thisClass.copyLabel.setDropTarget(true);
		////System.out.println("System-Icons wurden geladen");
	}
	public static void Feiertage(){
		vFeiertage = SqlInfo.holeFeld("select datsql from feiertage where jahr >= '"+aktJahr+"' AND "+
				"buland <> ''");
	}
	/*
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
			@Override
			public int compare(Vector o1, Vector o2) {
				// TODO Auto-generated method stub
				String s1 = (String)o1.get(0);
				String s2 = (String)o2.get(0);
				return s1.compareTo(s2);
			}
		};
		Collections.sort(vec,comparator);
	
	}
	*/

	
	
}

/*****************************************/

