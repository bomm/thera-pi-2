package org.thera_pi.nebraska.gui.utils;

import java.util.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;
import java.text.SimpleDateFormat;
//import java.text.ParseException;
//import javax.swing.JFormattedTextField;

public class DatFunk {


//private String sDatumJava = "";
//private String sDatumDeutsch = "";

	public static String sDatInDeutsch(String sJavaDat){
		if(sJavaDat==null){
			return "  .  .    ";
		}
		//if(sJavaDat.length() != 10){
		//	return "  .  .    ";
		//}
		String[] splittArray = sJavaDat.split("-");
		return splittArray[2]+"."+splittArray[1]+"."+splittArray[0];
	}
	
	public static String sDatInSQL(String sDeutschDat){
		//String sDatumSQL= new String();
		String[] splittArray = sDeutschDat.split("\\.");
		//sDatumSQL = splittArray[2]+"-"+splittArray[1]+"-"+splittArray[0]; 
		//System.out.println(sDatumSQL);
		return  splittArray[2]+"-"+splittArray[1]+"-"+splittArray[0];
	}

	public static String sHeute(){
		DateFormat df = DateFormat.getDateTimeInstance( DateFormat.MEDIUM,
				DateFormat.MEDIUM,  
                Locale.GERMANY );
		String s = df.format(new java.util.Date());
		s = s.substring(0,s.indexOf( ' ' )); 
	    return s;

	}
/*************************************************************************************/	
	public static String sDatPlusTage(String datum,int Tage){
		long anz_milli_1 = 0;
		long anz_milli_2 = 0;
		long anz_milli_3 = 0;
		//boolean deutsch = false;
		String[] datsplit = datum.split("\\."); 
		Date dDatum;
		String s;
		Calendar cal1 = new GregorianCalendar();
	    cal1.set( Integer.parseInt(datsplit[2]), Integer.parseInt(datsplit[1])-1, Integer.parseInt(datsplit[0]) );
 
		anz_milli_1 = cal1.getTime().getTime();
		anz_milli_2 = Math.round( Tage* (24. * 60.*60.*1000.) );
		anz_milli_3 = anz_milli_1+anz_milli_2;		
		

		DateFormat df = DateFormat.getDateTimeInstance( DateFormat.MEDIUM,
					DateFormat.MEDIUM,  
					Locale.GERMANY);

		if (anz_milli_2==0){
			dDatum = new Date();
		}else{
			dDatum = new Date(anz_milli_3);
		}
		s = df.format(dDatum);
		s = s.substring(0,s.indexOf( ' ' )); 
		return s;
	}
/*************************************************************************************/	
	@SuppressWarnings("deprecation")
	public static String WochenTag(String sdatum) {
		String[] asDatTeil = sdatum.split("\\.");
		SimpleDateFormat formatter = new SimpleDateFormat("dd.mm.yyyy");
		
		formatter.applyPattern( "EEEE', 'dd.mm yyyy hh:mm" );
		
		String sErgebnis = formatter.format(new Date(Integer.parseInt(asDatTeil[2]),
										Integer.parseInt(asDatTeil[1])-1,
										Integer.parseInt(asDatTeil[0])-1 ));
		String[] asErgebnis = sErgebnis.split(",");
	return asErgebnis[0];
	}
	
	
	public static int TagDerWoche(String sdatum) {
		int tag=0;
		String[] datsplit = sdatum.split("\\.");
		Calendar cal1 = new GregorianCalendar();
		cal1.set( Integer.parseInt(datsplit[2]), Integer.parseInt(datsplit[1])-1, Integer.parseInt(datsplit[0]) ); 
		tag = cal1.get(Calendar.DAY_OF_WEEK); 
		if(tag == 1){
			tag = 7; 
		}else{
			tag = tag - 1;
		}
		return tag;
	}

	public static long TageDifferenz(String saltdatum,String sneudatum){
		long mul1 = 0;
		long mul2 = 0;
		long mul3 = 0;

		String[] datsplit1 = saltdatum.split("\\.");
		String[] datsplit2 = sneudatum.split("\\.");
		Calendar cal1 = new GregorianCalendar();
		Calendar cal2 = new GregorianCalendar();
		cal1.set( Integer.parseInt(datsplit1[2]), Integer.parseInt(datsplit1[1])-1, Integer.parseInt(datsplit1[0]) ); 
		cal2.set( Integer.parseInt(datsplit2[2]), Integer.parseInt(datsplit2[1])-1, Integer.parseInt(datsplit2[0]) );

		mul1 = cal1.getTimeInMillis();
		mul2 = cal2.getTimeInMillis();
		
		mul3 = mul2-mul1;
		if(mul3==0){
			return 0;
		}
		return  mul3/(24*60*60*1000L);
	}

	public static String WocheErster(String sdatum){
		long mul = 0;
		long mul2 = 0;
		int tag, monat, jahr;
		String[] datsplit = sdatum.split("\\.");
		Calendar cal1 = new GregorianCalendar();
		cal1.set( Integer.parseInt(datsplit[2]), Integer.parseInt(datsplit[1])-1, Integer.parseInt(datsplit[0]) ); 
		tag = cal1.get(Calendar.DAY_OF_WEEK); 
		if(tag == 1){
			tag = 7; 
		}else{
			tag = tag - 1;
		}
		mul = (tag-1)*24*60*60*1000L;
		mul2 = cal1.getTimeInMillis();
		cal1.setTimeInMillis(mul2-mul);
		
		tag   = cal1.get(Calendar.DATE);
		monat = cal1.get(Calendar.MONTH)+1;
		jahr  = cal1.get(Calendar.YEAR);
		String a = (new Integer(tag)).toString();
		a= (a.length()<2 ? "0"+a : a);
		String b = (new Integer(monat)).toString();
		b= (b.length()<2 ? "0"+b : b);
		return a+"."+b+"."+Integer.toString(jahr);
	}
	public static String WocheLetzter(String sdatum){
		long mul = 0;
		long mul2 = 0;
		int tag, monat, jahr;
		String serster = WocheErster(sdatum);
		String[] datsplit = serster.split("\\.");
		Calendar cal1 = new GregorianCalendar();		
		cal1.set( Integer.parseInt(datsplit[2]), Integer.parseInt(datsplit[1])-1, Integer.parseInt(datsplit[0]) );		
		mul = 6*24*60*60*1000L;
		mul2 = cal1.getTimeInMillis();
		cal1.setTimeInMillis(mul2+mul);
		
		tag   = cal1.get(Calendar.DATE);
		monat = cal1.get(Calendar.MONTH)+1;
		jahr  = cal1.get(Calendar.YEAR);
		String a = Integer.toString(tag);
		a= (a.length()<2 ? "0"+a : a);
		String b = Integer.toString(monat);
		b= (b.length()<2 ? "0"+b : b);
		return a+"."+b+"."+jahr;
	}
	public String WocheNaechste(String sdatum){
		return "";
	}
	public static int KalenderWoche(String sdatum){
		String[] datsplit = sdatum.split("\\.");
		Calendar gcal =  Calendar.getInstance();
	    gcal.set( Integer.parseInt(datsplit[2]), Integer.parseInt(datsplit[1])-1, Integer.parseInt(datsplit[0]) ); 
	    return gcal.get(Calendar.WEEK_OF_YEAR);
	}
	public static long DatumsWert(String sdatum){
		Calendar gcal =  Calendar.getInstance();
		if(sdatum.contains(".")){
			String[] datsplit = sdatum.split("\\.");
			gcal.set( Integer.parseInt(datsplit[2]), Integer.parseInt(datsplit[1])-1, Integer.parseInt(datsplit[0]) );
		}else{
			String[] datsplit = sdatum.split("-");
			gcal.set( Integer.parseInt(datsplit[0]), Integer.parseInt(datsplit[1])-1, Integer.parseInt(datsplit[2]) );
		}
	    return gcal.getTimeInMillis();
	}
	public static String WertInDatum(long ldatum){
		String s;
		Date dDatum;
		DateFormat df = DateFormat.getDateTimeInstance( DateFormat.MEDIUM,
				DateFormat.MEDIUM,  
				Locale.GERMANY);

		if (ldatum==0){
			dDatum = new Date();
		}else{
			dDatum = new Date(ldatum);
		}
		s =df.format(dDatum);
		s = s.substring(0,s.indexOf( ' ' )); 
	    return s;
	}
	public static String datumOk(String datum){
		if(datum==null){
			return null;
		}
		if(datum.trim().equals("") || datum.trim().equals(".  .") || datum.trim().equals("-  -")){
			return null;
		}
		return DatFunk.sDatInDeutsch(datum);
	}
	
	public static boolean GeradeWoche(String sdatum){
		return ((KalenderWoche(sdatum) % 2)!= 0 ? false : true);
	}
	public static boolean Unter18(String bezugdat,String geburtstag){
		String[] datsplit = bezugdat.split("\\.");
		int jahr = new Integer(datsplit[2]) - 18;
		String testdatum = datsplit[0]+"."+datsplit[1]+"."+Integer.toString(jahr);
		if(DatumsWert(testdatum) < DatumsWert(geburtstag)){
			return true;
		}else{
			return false;			
		}
	}
	
	public static boolean Schaltjahr(int jahr) {
		if( jahr == 0 ) {
		    //System.out.println("Es gibt kein Jahr 0!");
			return false;
		} else {
			if( jahr % 4 == 0 ) {
				if( jahr % 100 == 0 ) {
					if( jahr % 400 == 0 ) {
						//System.out.println("Schaltjahr!");
						return true;
					} else {
						//System.out.println("Kein Schaltjahr!");
						return false;
					}
				} else {
			    //System.out.println("Schaltjahr!");
				return true;
				}
			} else {
				//System.out.println("Kein Schaltjahr!");
				return false;
			}
		}
	}

	public static int JahreDifferenz(String sAktuellesJahr,String sInputJahr) {
		int aktuell = new Integer(sAktuellesJahr.substring(6));
		int input = new Integer(sInputJahr.substring(6));
		return (aktuell-input);
	}

	/********************Klassen-Klammer*************/
}
	

/*
 *
 * /-------------------------------------------
//Daten aufnehmen
SimpleDateFormat formatter = new SimpleDateFormat("dd.mm.yyyy");
Date spaeter = formatter.parse("17.10.2004");
Date frueher = formatter.parse("13.10.2004");

//Millisekunden seit 1970 ;-)
long timeSpaeter = spaeter.getTime();
long timeFrueher = frueher.getTime();

//Millisekunden --> Tage
long diff = (timeSpaeter - timeFrueher) / 24 / 60 / 60 / 1000;

System.out.println("Vergangen: " + diff + " Tage !");
//-----------------------------------------

*
    // heutiges Datum wird erzeugt
    Date heute = new Date();

    // Anzahl der Sekunden nach 1.1.1970 ermitteln
    long anz_milli_sekunden_nach_1970 = heute.getTime();
 
    // heutiges Datum wird erzeugt
    long milli_sekunden_gestern = anz_milli_sekunden_nach_1970
                            - 24L*60L*60L*1000L;

    // gestriges Datum wird erzeugt
    Date gestern = new Date(milli_sekunden_gestern);
  // heutiges Datum wird erzeugt
	    Date heute = new Date();

	    // Anzahl der Sekunden nach 1.1.1970 ermitteln
	    long anz_milli_sekunden_nach_1970 = heute.getTime();
	 
	    // heutiges Datum wird erzeugt
	    long milli_sekunden_gestern = anz_milli_sekunden_nach_1970
	                            - 24L*60L*60L*1000L;

	    // gestriges Datum wird erzeugt
	    Date gestern = new Date(milli_sekunden_gestern);


*/

