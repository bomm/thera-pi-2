package hmrCheck;

import hauptFenster.Reha;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import java.util.Vector;

import javax.swing.JOptionPane;

import sqlTools.SqlInfo;
import stammDatenTools.RezTools;
import systemEinstellungen.SystemConfig;
import systemEinstellungen.SystemPreislisten;
import terminKalender.DatFunk;

public class HMRCheck {
	Vector<Integer> anzahl = null;
	Vector<String> positionen = null;
	Vector<Vector<String>> preisvec = null;
	String indischluessel = null;
	String diszis[] = {"2","1","5","3","8"};
	//RezNeuanlage rezanlage = null;
	int disziplin;
	int preisgruppe;
	final String maxanzahl = "Die Höchstmenge pro Rezept bei ";
	final String rotein = "<>";
	boolean testok = true;
	String fehlertext = "";
	int rezeptart;
	String reznummer = null;
	String rezdatum = null;
	String letztbeginn = null;
	boolean AdRrezept = false;
	boolean folgerezept = false;
	boolean neurezept = false;
	boolean doppelbehandlung = false;
	boolean unter18 = false;
	static SimpleDateFormat sdDeutsch = new SimpleDateFormat("dd.MM.yyyy");	
	static SimpleDateFormat sdSql = new SimpleDateFormat("yyyy-MM-dd");
	
	String[] rezarten = {"Erstverodnung","Folgeverordnung",	"außerhalb des Regelfalles"};

	String[] keinefolgevo = {"EX1a","EX1b","EX1c","WS1a","WS1b","WS1c","WS1d","WS1e",
			"AT1a","AT1b","AT1c","SB4","ST3"};
	String[] nurunter18 = {"ZN1a","ZN1b","ZN1c","EN1","PS1"};
	String[] nurueber18 = {"ZN2a","ZN2b","ZN2c","EN2"};
	
	public HMRCheck(String indi,int idiszi,Vector<Integer> vecanzahl,Vector<String>vecpositionen,
			int xpreisgruppe,Vector<Vector<String>> xpreisvec,int xrezeptart,String xreznr,String xrezdatum,String xletztbeginn){
		indischluessel = indi;
		disziplin = idiszi;
		anzahl = vecanzahl;
		positionen = vecpositionen;
		preisgruppe = xpreisgruppe;
		preisvec = xpreisvec;
		//rezanlage = xrezanlage;
		rezeptart =xrezeptart;
		reznummer = xreznr;
		unter18 = DatFunk.Unter18(DatFunk.sHeute(), DatFunk.sDatInDeutsch(Reha.thisClass.patpanel.patDaten.get(4)));
		if(reznummer.equals("")){
			neurezept = true;
		}
		rezdatum = xrezdatum;
		letztbeginn = xletztbeginn;
		//aktualisiereHMRs();
	}
	/*
	 * 
	 * Abhängig vom Indikationsschlüssel muß geprüft werden
	 * 1. Ist die Anzahl pro Rezept o.k.
	 * 2. Ist das gewählte Heilmittel o.k.
	 * 3. ist das ergänzende Heilmittel o.k.
	 * 4.
	 * 5. 
	 * 
	 */
	public boolean check(){
		
		AdRrezept = (rezeptart==2);
		folgerezept = (rezeptart==1);
		
		Vector<Vector<String>> vec = SqlInfo.holeFelder("select * from hmrcheck where indischluessel='"+
				indischluessel+"' LIMIT 1");

		if(vec.size() <= 0 || indischluessel.equals("")){
			JOptionPane.showMessageDialog(null,"Indikationsschlüssel "+indischluessel+" unbekannt oder nicht angegeben!"); 
			return false;
		}

		int maxprorezept = Integer.parseInt(vec.get(0).get(2));
		String[] vorrangig = vec.get(0).get(3).split("@");
		String[] ergaenzend = vec.get(0).get(5).split("@");
		for(int i = 0; i < vorrangig.length;i++){
			vorrangig[i] = diszis[disziplin]+vorrangig[i];
		}
		for(int i = 0; i < ergaenzend.length;i++){
			ergaenzend[i] = diszis[disziplin]+ergaenzend[i];
		}
		//hier einbauen:
		//testen auf WS1,Ex1 etc. hier ist keine Folgeverordnung möglich // Status:erledigt!!
		//testen auf Doppelbehandlung und Verordnungsmenge // Status:erledigt
		//testen auf außerhalb des Regelfalles (hebt) die Verordnungsmenge auf // Status:erledigt aber halblebig
		//testen auf Rezdatum und Behandlungsbeginn = 0.k. // Status:ausstehend
		//testen ob Unterbrechungen zwischen den Behandlungen o.k. // Status:ausstehend

		// mögliche Höchstmenge pro Rezept wurde überschritten?
		//System.out.println("Max pro Rezept="+maxprorezept);
		//System.out.println("Anzahlen = "+anzahl);
		for(int i = 0; i < anzahl.size();i++){
			if( (anzahl.get(i) > maxprorezept) && (!AdRrezept) ) {
				fehlertext = String.valueOf("<html><b>Bei Indikationsschlüssel "+indischluessel+" sind maximal<br><font color='#ff0000'>"+Integer.toString(maxprorezept)+" Behandlungen</font> pro Rezept erlaubt!!<br><br>" +
						"Möglickeit -> Ändern der Rezeptart auf außerhalb des Regelfalles<br><br></b>");
				testok = false;
			}
		}
		//Checken ob Indischlüssel in der Liste der Schlüssel ohne Folgeverordnung enthalten ist
		if( (Arrays.asList(keinefolgevo).contains(indischluessel)) && (rezeptart > 0) ){
			fehlertext = fehlertext + String.valueOf( (fehlertext.length() <= 0 ? "<html>" : "")+
					"<b>Bei Indikationsschlüssel "+indischluessel+" ist keine<br><font color='#ff0000'>"+
					rezarten[rezeptart]+
					"</font> erlaubt!!</b><br><br>");
			testok = false;
		}
		//Hier der Check ob für Kinder ein Erwachsenen-Indischlüssel verwendet wurde z.B. ZN2a
		if( (unter18) && (Arrays.asList(nurueber18).contains(indischluessel)) ){
			fehlertext = fehlertext + String.valueOf( (fehlertext.length() <= 0 ? "<html>" : "")+
					"<b>Der Indikationsschlüssel "+indischluessel+" ist nur bei <br><font color='#ff0000'>"+
					"Erwachsenen über 18 Jahren"+
					"</font> erlaubt!!</b><br><br>");
			testok = false;
		//Hier der Check ob für Erwachsene ein Kinder-Indischlüssel verwendet wurde z.B. ZN1a
		}else if((!unter18) && (Arrays.asList(nurunter18).contains(indischluessel))){
			fehlertext = fehlertext + String.valueOf( (fehlertext.length() <= 0 ? "<html>" : "")+
					"<b>Der Indikationsschlüssel "+indischluessel+" ist nur bei <br><font color='#ff0000'>"+
					"Kindern und Jugendlichen bis 18 Jahren"+
					"</font> erlaubt!!</b><br><br>");
			testok = false;
		}
		
		try{
			if(positionen.size() >= 2){
				if(positionen.get(0).equals(positionen.get(1))){
					doppelbehandlung = true;
					int doppelgesamt = anzahl.get(0) + anzahl.get(1);
					if((doppelgesamt > maxprorezept) && (!AdRrezept)){
						fehlertext = String.valueOf("<html><b>Die Doppelbehandlung bei Indikationsschlüssel "+indischluessel+
								", übersteigt<br>die maximal erlaubte Höchstverordnungsmenge pro Rezept von<br><font color='#ff0000'>"+Integer.toString(maxprorezept)+
								" Behandlungen</font>!!</b><br><br>Wechsel auf -> außerhalb des Regelfalles <- ist erforderlich<br><br>");
						testok = false;
					}
				}
			}
			// jetzt haben wir schon einmal die Doppelbehandlung
			// dann testen ob die Positionsnummer überhaupt ein zugelassenes vorrangiges Heilmittel ist.
			
			for(int i = 0; i < positionen.size();i++){
				//Hier Doppelbehandlung einbauen start
				if(i==0){
					if(! Arrays.asList(vorrangig).contains(positionen.get(i))){
						fehlertext = fehlertext+String.valueOf(
								getDialogText(true,getHeilmittel(positionen.get(i)),positionen.get(i),vorrangig));
						testok = false;
					}
				}else if(i==1 && doppelbehandlung){
					
				}else{
					if(! Arrays.asList(ergaenzend).contains(positionen.get(i))){
						fehlertext = fehlertext+String.valueOf(
								getDialogText(false,getHeilmittel(positionen.get(i)),positionen.get(i),ergaenzend));
						testok = false;
					}
				}
				//Hier Doppelbehandlung einbauen ende
			}
			
			//Jetzt auf Rezeptbeginn testen
			if(neurezept){
				long differenz = DatFunk.TageDifferenz(rezdatum,DatFunk.sHeute());
				if(differenz < 0){
					fehlertext = fehlertext+(fehlertext.length() <= 0 ? "<html>" : "")+"<br><b><font color='#ff0000'>Rezeptdatum ist absolut kritisch!</font><br>Spanne zwischen Behandlungsbeginn und Rezeptdatum beträgt <font color='#ff0000'>"+
					Long.toString(differenz)+" Tag(e) </font>.<br>Behandlungsbeginn ist also <font color='#ff0000'>vor</font> dem  Ausstellungsdatum!!</b><br><br>";
					testok = false;
				}					
				if( (differenz=DatFunk.TageDifferenz(letztbeginn,DatFunk.sHeute() )) > 0){
					//System.out.println("Differenz 2 = "+differenz);
					fehlertext = fehlertext+(fehlertext.length() <= 0 ? "<html>" : "")+"<br><b><font color='#ff0000'>Behandlungsbeginn ist kritisch!</font><br><br>Die Differenz zwischen <font color='#ff0000'>spätester Behandlungsbeginn</font> und 1.Behandlung<br>beträgt <font color='#ff0000'>"+
					Long.toString(differenz)+" Tage </font><br>" +
					"</b><br><br>";
					testok = false;
				}
			}else{
				String cmd = "select termine from verordn where rez_nr='"+reznummer+"' LIMIT 1";
				String termine = SqlInfo.holeFeld(cmd).get(0);
				//Keine Termine notiert
				if(termine.trim().equals("")){
					//LetzterBeginn abhandeln
					long differenz = DatFunk.TageDifferenz(rezdatum,DatFunk.sHeute());
					if(differenz < 0){
						fehlertext = fehlertext+(fehlertext.length() <= 0 ? "<html>" : "")+"<br><b><font color='#ff0000'>Rezeptdatum ist absolut kritisch!</font><br>Spanne zwischen Behandlungsbeginn und Rezeptdatum beträgt <font color='#ff0000'>"+
						Long.toString(differenz)+" Tag(e) </font>.<br>Behandlungsbeginn ist also <font color='#ff0000'>vor</font> dem  Ausstellungsdatum!!</b><br><br>";
						testok = false;
					}					
					if( (differenz=DatFunk.TageDifferenz(letztbeginn,DatFunk.sHeute() )) > 0){
						//System.out.println("Differenz 2 = "+differenz);
						fehlertext = fehlertext+(fehlertext.length() <= 0 ? "<html>" : "")+"<br><b><font color='#ff0000'>Behandlungsbeginn ist kritisch!</font><br><br>Die Differenz zwischen <font color='#ff0000'>spätester Behandlungsbeginn</font> und 1.Behandlung<br>beträgt <font color='#ff0000'>"+
						Long.toString(differenz)+" Tage </font><br>" +
						"</b><br><br>";
						testok = false;
					}

				}else{
					//LetzterBeginn abhandeln
					String erstbehandlung = RezTools.holeEinzelTermineAusRezept(null, termine).get(0);
					long differenz = DatFunk.TageDifferenz(rezdatum,erstbehandlung);
					if(differenz < 0){
						fehlertext = fehlertext+(fehlertext.length() <= 0 ? "<html>" : "")+"<br><b><font color='#ff0000'>Rezeptdatum ist absolut kritisch!</font><br>Spanne zwischen Behandlungsbeginn und Rezeptdatum beträgt <font color='#ff0000'>"+
						Long.toString(differenz)+" Tag(e) </font>.<br>Behandlungsbeginn ist also <font color='#ff0000'>vor</font> dem  Ausstellungsdatum!!</b><br><br>";
						testok = false;
					}					
					if( (differenz=DatFunk.TageDifferenz(letztbeginn,erstbehandlung )) > 0){
						//System.out.println("Differenz 2 = "+differenz);
						fehlertext = fehlertext+(fehlertext.length() <= 0 ? "<html>" : "")+"<br><b><font color='#ff0000'>Behandlungsbeginn ist kritisch!</font><br><br>Die Differenz zwischen <font color='#ff0000'>spätester Behandlungsbeginn</font> und 1.Behandlung<br>beträgt <font color='#ff0000'>"+
						Long.toString(differenz)+" Tage </font><br>" +
						"</b><br><br>";
						testok = false;
					}

				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		if(! testok){
			JOptionPane.showMessageDialog(null,fehlertext+"</html>");
		}
		return testok;
	}
	private String getDialogText(boolean vorrangig,String heilmittel,String hmpos,String[] positionen){
		String meldung = (fehlertext.length() <= 0 ? "<html>" : "")+"Bei dem Indikationsschlüssel <b><font color='#ff0000'>"+indischluessel+"</font></b> ist das "+(vorrangig ? "vorrangige " : "ergänzende")+
		" Heilmittel<br><br>--> <b><font color='#ff0000'>"+heilmittel+"</font></b> <-- nicht erlaubt!<br><br><br>"+
		"Mögliche "+(vorrangig ? "vorrangige " : "ergänzende")+" Heilmittel sind:<br><b><font color='#ff0000'>"+
		getErlaubteHeilmittel(positionen)+"</font></b><br><br>";
		return meldung;
		
	}
	/************************/
	private String getErlaubteHeilmittel(String[] heilmittel){
		StringBuffer buf = new StringBuffer();
		String hm = "";
		for(int i = 0;i < heilmittel.length;i++){
			hm = getHeilmittel(heilmittel[i]);
			if(!hm.equals("")){
				buf.append(getHeilmittel(heilmittel[i])+"<br>");				
			}
		}
		return (buf.toString().equals("") ? "<br>keine<br>" : buf.toString());
	}
	/************************/	
	private String getHeilmittel(String heilmittel){
		for(int i = 0;i < preisvec.size();i++){
			if(preisvec.get(i).get(2).equals(heilmittel)){
				return preisvec.get(i).get(0);
			}
		}
		return "";
	}
	/************************/

	public static String X_holeLetztMoeglichenTag(boolean erstebehandlung,String disziplin,int preisgruppe,String referenz){
		int frist = (Integer)((Vector<?>)SystemPreislisten.hmFristen.get(disziplin).get((erstebehandlung ? 0 : 2))).get(preisgruppe);
		boolean kalendertage = (Boolean) ((Vector<?>)SystemPreislisten.hmFristen.get(disziplin).get((erstebehandlung ? 1 : 3))).get(preisgruppe);
		boolean mitsamstag = (Boolean) ((Vector<?>)SystemPreislisten.hmFristen.get(disziplin).get((erstebehandlung ? 4 : 5))).get(preisgruppe);
		if(kalendertage){
			return DatFunk.sDatPlusTage(referenz, frist);
		}
		return hmrLetztesDatum(referenz,frist,mitsamstag);
	}
	
	public static long X_hmrTageErmitteln(int preisgruppe,String reznum,String rezdatum,String testdatum,String letzter){
		long differenz = DatFunk.TageDifferenz(rezdatum, testdatum	);
		int wotag = DatFunk.TagDerWoche(rezdatum);
		//System.out.println("Tag der Woche der Rezeptausstellung = "+wotag);
		//System.out.println("Preisgruppe = "+preisgruppe);
		//System.out.println("Tage Differenz insgesamt = "+differenz);
		if(preisgruppe != 1 && differenz > 10){
			if(wotag >= 3){
				differenz -= 2;
			}else{
				differenz -= 1;	
			}
		}

		return differenz;
	}
	@SuppressWarnings("deprecation")
	public static int[] XX_hmrArbeitsTage(Date start,Date ende,boolean samstagWochenende,boolean checkFeiertage){
		int[] ret = {0,0,0};  //0=Arbeitstag,1=wochenende,2=Feiertag

		for(Date date = start; date.compareTo(ende) <= 0; date = new Date(date.getTime() + 24*60*60*1000)) {
		   if (date.getDay() % 7 == 0) {
			   ret[1] ++;
		   } else if (checkFeiertage && istFeiertag(date)) {  //selber implementieren
			   ret[2] ++; 
		   } else {
		     ret[0]++;
		   }
		}
		return ret;
	}
	/******************************************/
	public static int hmrTageDifferenz(String referenzdatum,String vergleichsdatum,int differenz,boolean samstagistwerktag){
		int ret = 1;
		try{
		String letztesdatum = hmrLetztesDatum(referenzdatum,differenz,samstagistwerktag);
		ret = Integer.parseInt(Long.toString(DatFunk.TageDifferenz(letztesdatum, vergleichsdatum)));
		}catch(Exception ex){
			System.out.println("Fehler in der Ermittlung der Unterbrechungszeiträume");
			ex.printStackTrace();
		}
		return ret;
	}
	/******************************************/	
	@SuppressWarnings("deprecation")
	public static String hmrLetztesDatum(String startdatum,int differenz,boolean samstagistwerktag){
		//SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy");
		int i = 0; 
		int werktage = 0;
		Date date = null;
		//System.out.println("Übergabe = "+startdatum);
		
		try {
			date = sdDeutsch.parse(startdatum);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		 
		//System.out.println("geparstes Datum = "+sd.format(date));
		while(true){
			//System.out.println("Getestetes Datum = "+sd.format(date));
			if( (!(date.getDay() % 7 ==0)) && (samstagistwerktag) ){
				if(! istFeiertag(date)){
					if(werktage==differenz){
						return sdDeutsch.format(date);	
					}
					werktage++;
				}
			}else if( (!(date.getDay() % 7 ==0)) && (!samstagistwerktag) && (!(date.getDay() % 6 ==0))){
				if(! istFeiertag(date)){
					if(werktage==differenz){
						return sdDeutsch.format(date);	
					}
					werktage++;
				}
			}
			i++;
			date = new Date(date.getTime()+ (24*60*60*1000) );
		}
	}
	@SuppressWarnings("deprecation")
	public static int XX_hmrAnzahlTageLetztesDatum(String startdatum,int differenz){
		//SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy");
		int i = 0; 
		int werktage = 0;

		Date date = null;
		//System.out.println("Übergabe = "+startdatum);
		
		try {
			date = sdDeutsch.parse(startdatum);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		 
		//System.out.println("geparstes Datum = "+sd.format(date));
		while(true){
			//System.out.println("Getestetes Datum = "+sd.format(date));
			if(! (date.getDay() % 7 ==0) ){
				if(! istFeiertag(date)){
					if(werktage==differenz){
						return Integer.valueOf(i);	
					}
					werktage++;
				}
			}
			i++;
			date = new Date(date.getTime()+ (24*60*60*1000) );
		}
	}	

	public static boolean istFeiertag(Date date){
		//SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
		//System.out.println("*******************"+sd.format(date));
		if( SystemConfig.vFeiertage.contains(sdSql.format(date))){
			return true;
		}
		return false;
	}
	
	/*
	private void aktualisiereHMRs(){
		Vector<Vector<String>> vec = SqlInfo.holeFelder("select ergaenzend,maxergaenzend,id from hmrcheck  where ergaenzend LIKE '%1508%'");
		String sanzahl = "";
		String cmd = "";
		System.out.println("Anzahl indis = "+vec.size());
		for(int i = 0; i < vec.size();i++){
			
			try{
				sanzahl = vec.get(i).get(1).split("@")[0];
			}catch(Exception ex){
				sanzahl = "6";
			}
			cmd = "update hmrcheck set ergaenzend='"+vec.get(i).get(0)+"@1531', maxergaenzend='"+
			vec.get(i).get(1)+"@"+sanzahl+"' where id ='"+vec.get(i).get(2)+"' LIMIT 1";
			System.out.println(cmd);
			SqlInfo.sqlAusfuehren(cmd);
		}
	}
	*/
}
