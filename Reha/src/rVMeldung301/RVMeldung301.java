package rVMeldung301;

import hauptFenster.Reha;

import java.util.Date;

import terminKalender.DatFunk;
import utils.StringTools;

public class RVMeldung301 {
	static String UNA = "UNA";
	static String UNB = "UNB";
	static String UNH = "UNH";
	static String UNT = "UNT";
	static String UNZ = "UNZ";
	static String DTM = "DTM";
	static String RFF = "RFF";
	static String PNA = "PNA";
	static String CTA = "CTA";
	static String AGR = "AGR";
	static String FCA = "FCA";
	static String ADR = "ADR";
	static String PDI = "PDI";
	static String NAT = "NAT";
	static String PRC = "PRC";
	static String IMD = "IMD";
	static String CIN = "CIN";
	static String EOL = "'";
	static String NEWLINE = "\n";
	static String PLUS = "+";
	static String DOPPELPUNKT = ":";
	static String STARTCHARS = ",? ";
	
	static String VORFALL ="";
	static String BEARBEITER ="";
	static String DATUM8 = "";
	static String DATUM10 = "";
	static String UHRZEIT4 = "";
	static String EMPFAENGERIK = "";
	static String LFDNUMMER = "";
	static String REHANUMMER = "";
	static String BERICHTID = "";
	StringBuffer buf301Header = new StringBuffer();
	StringBuffer buf301Body = new StringBuffer();
	StringBuffer buf301Footer = new StringBuffer();
	
	RVMeldung301(int artDerMeldung,String rez_nr,String recipient,String lfdnr,String berichtid){
		DATUM8 = DatFunk.sHeute().substring(8)+DatFunk.sHeute().substring(3,5)+DatFunk.sHeute().substring(0,2);
		DATUM10 = DatFunk.sHeute().substring(6)+DatFunk.sHeute().substring(3,5)+DatFunk.sHeute().substring(0,2);
		UHRZEIT4 = getEdiTimeString(false);
		EMPFAENGERIK = recipient;
		LFDNUMMER = StringTools.fuelleMitZeichen(lfdnr, "0", true, 5);
		REHANUMMER = rez_nr;
		BERICHTID = berichtid;

		buf301Header.setLength(0);
		buf301Header.trimToSize();
		buf301Body.setLength(0);
		buf301Body.trimToSize();
		buf301Footer.setLength(0);
		buf301Footer.trimToSize();
		
		if(artDerMeldung == 1){
			doAufnahmeMitteilung();
		}
		
	}
	private void doAufnahmeMitteilung(){
		doKopfDaten();
		/************************************/
		buf301Body.append("UNH+00001+MEDR02:D:01A:KR:97B'"+NEWLINE);
		buf301Body.append("BGM+01++10'"+NEWLINE);
		buf301Body.append("DTM+137:"+DATUM10+":102'"+NEWLINE);
		buf301Body.append("RFF+ACD:01'"+NEWLINE);
		buf301Body.append("PNA+MS++"+Reha.aktIK+EOL+NEWLINE);
		buf301Body.append("PNA+MR++"+EMPFAENGERIK+EOL+NEWLINE);
		buf301Body.append("PNA+BY++"+EMPFAENGERIK+EOL+NEWLINE);//??Kostenträger für Ersatzfall nochmals prüfen
		buf301Body.append("CTA+BEA+"/*+8110*/+EOL+NEWLINE);//??Kennzeichen Bearbeiter muß noch geklärt werden
		buf301Body.append("RFF+FI:"/*+Versicherungsnummer und Kennzeichen*/+EOL+NEWLINE); //?? Kennzichen
		buf301Body.append("PNA+MT++"+Reha.aktIK+EOL+NEWLINE);
		buf301Body.append("CTA+ABT+"+"2300");//Für andere Indikationen überarbeiten!!!!!!!!!
		buf301Body.append("RFF+AES:"+REHANUMMER+EOL+NEWLINE);//?? eigene Rehanummer o.k. ??
		
		buf301Body.append("PNA+AB++"/*+keine Ahnung*/+EOL+NEWLINE); //?? IK von was??
		buf301Body.append("CTA+BEA+"/*+8110*/+EOL+NEWLINE);//??Kennzeichen Bearbeiter muß noch geklärt werden
		buf301Body.append("RFF+AHN:"/*+Versicherungsnummer und Kennzeichen*/+EOL+NEWLINE); //?? Kennzichen
		buf301Body.append("AGR+BY:4"/*+Versicherungsnummer und Kennzeichen*/+EOL+NEWLINE); //4 = RV-Träger
		buf301Body.append("FCA+AD"+EOL+NEWLINE);
		buf301Body.append("PNA+BM+"+EOL+NEWLINE);
		buf301Body.append("RFF+AGU:"/*+Versicherungsnummer*/+EOL+NEWLINE);
		buf301Body.append("RFF+AGF:"+"04"+EOL+NEWLINE);
		buf301Body.append("RFF+ADE:"+"1"+EOL+NEWLINE);
		buf301Body.append("RFF+AEN:"/*+Versicherungsnummer*/+EOL+NEWLINE);
		buf301Body.append("ADR++1:"/*Strasse u.Hausnr.*+Stadt+PLZ*/+EOL+NEWLINE);
		buf301Body.append("DTM+329:"/*Geburtstag*/+":102"+EOL+NEWLINE);
		buf301Body.append("PDI+"/*1+2*/+EOL+NEWLINE);
		buf301Body.append("NAT+"/*2+D*/+EOL+NEWLINE);
		buf301Body.append("PRC+ADMIN3"+EOL+NEWLINE);		
		buf301Body.append("IMD+++"/*2:B11*/+EOL+NEWLINE);
		buf301Body.append("CIN+DIA"/*+Diagnoseschlüssel*/+":10R"+EOL+NEWLINE);
		buf301Body.append("DTM+194:"/*+Datum10*/+":102"+EOL+NEWLINE);
		buf301Body.append("DTM+163:"/*+Uhrzeit4*/+":401"+EOL+NEWLINE);
		buf301Body.append("UNT"/*+Anzahlzeilen*/+"+00001"+EOL+NEWLINE);
		/********************************/
		doFussDaten();
	}
	private void doKopfDaten(){
		buf301Header.append(UNA+DOPPELPUNKT+PLUS+STARTCHARS+EOL+NEWLINE);
		buf301Header.append(UNB+PLUS+"UNOC:3"+PLUS+Reha.aktIK+PLUS+EMPFAENGERIK+
				PLUS+DATUM8+DOPPELPUNKT+UHRZEIT4+PLUS+
				LFDNUMMER+PLUS+PLUS+"REH"+LFDNUMMER+EOL+NEWLINE);
	}
	private void doFussDaten(){
		buf301Footer.append("UNZ+00001+"+LFDNUMMER+EOL);
	}
	
	private String getEdiTimeString(boolean mitsekunden){
		Date date = new Date();
		String[] datesplit = date.toString().split(" ");
		if(mitsekunden){
			return datesplit[3].substring(0,2)+datesplit[3].substring(3,5)+datesplit[3].substring(6,8);
		}
		return datesplit[3].substring(0,2)+datesplit[3].substring(3,5);
	}

	
}
