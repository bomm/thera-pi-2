package dta301;

import hauptFenster.Reha;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import org.thera_pi.nebraska.crypto.NebraskaCryptoException;
import org.thera_pi.nebraska.crypto.NebraskaEncryptor;
import org.thera_pi.nebraska.crypto.NebraskaFileException;
import org.thera_pi.nebraska.crypto.NebraskaKeystore;
import org.thera_pi.nebraska.crypto.NebraskaNotInitializedException;

import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
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
	static String KOSTENTRAEGER = "";
	static String LFDNUMMER = "";
	static String REHANUMMER = "";
	static String BERICHTID = "";
	StringBuffer buf301Header = new StringBuffer();
	StringBuffer buf301Body = new StringBuffer();
	StringBuffer buf301Footer = new StringBuffer();
	StringBuffer gesamtbuf = new StringBuffer();
	StringBuffer auftragsBuf = new StringBuffer();
	Vector<Vector<String>> vecdta = null;
	Vector<String> originaldata = new Vector<String>();
	int intAktEREH = -1;
	String strAktEREH = null;
	int originalSize = -1;
	int encryptedSize = -1;
	
	RVMeldung301(int art, String id){
		
		/*
		EMPFAENGERIK = recipient;
		LFDNUMMER = StringTools.fuelleMitZeichen(lfdnr, "0", true, 5);
		REHANUMMER = rez_nr;
		BERICHTID = berichtid;
		*/
		DATUM8 = DatFunk.sHeute().substring(8)+DatFunk.sHeute().substring(3,5)+DatFunk.sHeute().substring(0,2);
		DATUM10 = DatFunk.sHeute().substring(6)+DatFunk.sHeute().substring(3,5)+DatFunk.sHeute().substring(0,2);
		UHRZEIT4 = getEdiTimeString(false);
		LFDNUMMER = StringTools.fuelleMitZeichen(
				Integer.toString(SqlInfo.erzeugeNummerMitMax("dfue", 99999)),
				"0",
				true,
				5);
		buf301Header.setLength(0);
		buf301Header.trimToSize();
		buf301Body.setLength(0);
		buf301Body.trimToSize();
		buf301Footer.setLength(0);
		buf301Footer.trimToSize();
		gesamtbuf.setLength(0);
		gesamtbuf.trimToSize();
		auftragsBuf.setLength(0);
		auftragsBuf.trimToSize();
		vecdta = SqlInfo.holeFelder("select * from dta301 where id='"+id+"' LIMIT 1");
		EMPFAENGERIK = vecdta.get(0).get(3);
		
	}
	
	/************************************************************/
	public void doBeginn(String beginnDatum,String uhrZeit){
		holeVector();
		int zeilen = 1;
		String test = "";
		buf301Body.append("UNH+00001+MEDR02:D:01A:KR:97B'"+NEWLINE);zeilen++;
		buf301Body.append("BGM+01++10'"+NEWLINE);zeilen++;
		buf301Body.append("DTM+137:"+DATUM10+":102'"+NEWLINE);zeilen++;
		buf301Body.append("RFF+ACD:01'"+NEWLINE);zeilen++;
		buf301Body.append("PNA+MS++"+Reha.aktIK+EOL+NEWLINE);zeilen++;
		buf301Body.append("PNA+MR++"+(EMPFAENGERIK = vecdta.get(0).get(3).toString())+EOL+NEWLINE);zeilen++;
		buf301Body.append("PNA+BY++"+(KOSTENTRAEGER = vecdta.get(0).get(5).toString())+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("CTA+BEA+","CTA+BEA+")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("RFF+FI:","RFF+FI:")+EOL+NEWLINE);zeilen++;
		buf301Body.append("PNA+MT++"+Reha.aktIK+EOL+NEWLINE);zeilen++;
		buf301Body.append("CTA+ABT+2300"+EOL+NEWLINE);zeilen++;
		buf301Body.append("RFF+AES:"+(REHANUMMER =vecdta.get(0).get(2).toString()) +EOL+NEWLINE);zeilen++;
		if( ! (test =  springeAufUndHole("PNA+AB+","PNA+AB+")).equals("")){
			buf301Body.append(test+EOL+NEWLINE);zeilen++;
			if( ! (test =  springeAufUndHole("PNA+AB+","CTA+BEA+")).equals("")){
				buf301Body.append(test+EOL+NEWLINE);zeilen++;
			}
			if( ! (test =  springeAufUndHole("PNA+AB+","RFF+AHN:")).equals("")){
				buf301Body.append(test+EOL+NEWLINE);zeilen++;
			}
		}
		buf301Body.append(springeAufUndHole("AGR+BY:","AGR+BY:")+EOL+NEWLINE);zeilen++;
		buf301Body.append("FCA+AD'"+NEWLINE);zeilen++;
		
		buf301Body.append(springeAufUndHole("PNA+BM+","PNA+BM+")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("PNA+BM+","RFF+AGU:")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("PNA+BM+","RFF+AGF:")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("PNA+BM+","RFF+ADE:")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("PNA+BM+","RFF+AEN:")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("PNA+BM+","ADR++")+EOL+NEWLINE);zeilen++;
		if( ! (test =  springeAufUndHole("PNA+BM+","ADR+1+")).equals("")){
			buf301Body.append(test+EOL+NEWLINE);zeilen++;
		}
		buf301Body.append(springeAufUndHole("PNA+BM+","DTM+329:")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("PNA+BM+","PDI+")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("PNA+BM+","NAT+")+EOL+NEWLINE);zeilen++;
		buf301Body.append("PRC+ADMIN3'"+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("PNA+BM+","IMD+")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("PNA+BM+","CIN+")+EOL+NEWLINE);zeilen++;

		buf301Body.append("DTM+194:"+mache10erDatum(beginnDatum)+":102"+EOL+NEWLINE);zeilen++;
		buf301Body.append("DTM+163:"+uhrZeit+":401"+EOL+NEWLINE);zeilen++;
		buf301Body.append("UNT+"+
				StringTools.fuelleMitZeichen(Integer.toString(zeilen),"0",true,5)+"+00001"+
				EOL+NEWLINE);zeilen++;
		doKopfDaten();
		doFussDaten();
		

		gesamtbuf.append(buf301Header.toString());
		gesamtbuf.append(buf301Body.toString());
		gesamtbuf.append(buf301Footer.toString());

		//doOriginalDatei();
		//Datei erstellen
		//int intAktEREH = -1;
		//String strAktEREH = null;
		//long originalSize = -1;
		//long decodedSize = -1;
		intAktEREH = SqlInfo.erzeugeNummerMitMax("esol", 999);
		strAktEREH = "0"+StringTools.fuelleMitZeichen(Integer.toString(intAktEREH), "0", true, 3);
		if(doKeyStoreAktion()){
			//Mail Versenden
			//In neue Tabelle schreiben
		}
		
	}
	public void doUnterbrechung(String beginnDatum,String endeDatum,int ubart,int ubgrund, String hinweis){
		//ubart = 0 Beginn der U, 1 = Ende der U, 2 = Beginn und Ende der U
		holeVector();
		int zeilen = 1;
		String test = "";
		buf301Body.append("UNH+00001+MEDR02:D:01A:KR:97B'"+NEWLINE);zeilen++;
		buf301Body.append("BGM+06++10'"+NEWLINE);zeilen++; 
		buf301Body.append("DTM+137:"+DATUM10+":102'"+NEWLINE);zeilen++;
		buf301Body.append("RFF+ACD:01'"+NEWLINE);zeilen++; //Hier die Datenbank untersuchen
		buf301Body.append("PNA+MS++"+Reha.aktIK+EOL+NEWLINE);zeilen++;
		buf301Body.append("PNA+MR++"+(EMPFAENGERIK = vecdta.get(0).get(3).toString())+EOL+NEWLINE);zeilen++;
		buf301Body.append("PNA+BY++"+(KOSTENTRAEGER = vecdta.get(0).get(5).toString())+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("CTA+BEA+","CTA+BEA+")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("RFF+FI:","RFF+FI:")+EOL+NEWLINE);zeilen++;
		buf301Body.append("PNA+MT++"+Reha.aktIK+EOL+NEWLINE);zeilen++;
		buf301Body.append("CTA+ABT+2300"+EOL+NEWLINE);zeilen++; //hier nach Inikationsgruppen untersuchen
		buf301Body.append("RFF+AES:"+(REHANUMMER =vecdta.get(0).get(2).toString()) +EOL+NEWLINE);zeilen++;
		if( ! (test =  springeAufUndHole("PNA+AB+","PNA+AB+")).equals("")){
			buf301Body.append(test+EOL+NEWLINE);zeilen++;
			if( ! (test =  springeAufUndHole("PNA+AB+","CTA+BEA+")).equals("")){
				buf301Body.append(test+EOL+NEWLINE);zeilen++;
			}
			if( ! (test =  springeAufUndHole("PNA+AB+","RFF+AHN:")).equals("")){
				buf301Body.append(test+EOL+NEWLINE);zeilen++;
			}
		}
		buf301Body.append(springeAufUndHole("AGR+BY:","AGR+BY:")+EOL+NEWLINE);zeilen++;
		buf301Body.append("FCA+AD'"+NEWLINE);zeilen++;
		
		buf301Body.append(springeAufUndHole("PNA+BM+","PNA+BM+")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("PNA+BM+","RFF+AGU:")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("PNA+BM+","RFF+AGF:")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("PNA+BM+","RFF+ADE:")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("PNA+BM+","RFF+AEN:")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("PNA+BM+","ADR++")+EOL+NEWLINE);zeilen++;
		if( ! (test =  springeAufUndHole("PNA+BM+","ADR+1+")).equals("")){
			buf301Body.append(test+EOL+NEWLINE);zeilen++;
		}
		buf301Body.append(springeAufUndHole("PNA+BM+","DTM+329:")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("PNA+BM+","PDI+")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("PNA+BM+","NAT+")+EOL+NEWLINE);zeilen++;
		/******************/
		buf301Body.append("PRC+ADMIN6'"+NEWLINE);zeilen++;	
		buf301Body.append(springeAufUndHole("PNA+BM+","IMD+")+EOL+NEWLINE);zeilen++;
		buf301Body.append("IMD+++"+Dta301CodeListen.codeB08[ubgrund][0]+":B08"+EOL+NEWLINE);zeilen++;
		//String beginnDatum,String endeDatum,int ubart,int ubgrund, String hinweis
		if(ubart==0){
			buf301Body.append("DTM+158:"+mache10erDatum(beginnDatum)+":102"+EOL+NEWLINE);zeilen++;
		}else if(ubart==1){
			buf301Body.append("DTM+159"+mache10erDatum(endeDatum)+":102"+EOL+NEWLINE);zeilen++;
		}else if(ubart==2){
			buf301Body.append("DTM+324"+
					mache10erDatum(beginnDatum)+
					mache10erDatum(endeDatum)+":711"+EOL+NEWLINE);zeilen++;
		}
		if(!hinweis.equals("")){
			buf301Body.append("FTX+TXT+++B:"+normalizeString(hinweis)+EOL+NEWLINE);zeilen++;
		}
		buf301Body.append("UNT+"+
				StringTools.fuelleMitZeichen(Integer.toString(zeilen),"0",true,5)+"+00001"+
				EOL+NEWLINE);zeilen++;
		doKopfDaten();
		doFussDaten();
		gesamtbuf.append(buf301Header.toString());
		gesamtbuf.append(buf301Body.toString());
		gesamtbuf.append(buf301Footer.toString());
		intAktEREH = SqlInfo.erzeugeNummerMitMax("esol", 999);
		strAktEREH = "0"+StringTools.fuelleMitZeichen(Integer.toString(intAktEREH), "0", true, 3);
		if(doKeyStoreAktion()){
			//Mail Versenden
			//In neue Tabelle schreiben
		}

		
	}
	private boolean doKeyStoreAktion(){
		try {
			originalSize =gesamtbuf.length();
			doDateiErstellen(0);
			String keystore = Reha.proghome+"keystore/"+Reha.aktIK+"/"+Reha.aktIK+".p12";
			NebraskaKeystore store = new NebraskaKeystore(keystore, SystemConfig.hmAbrechnung.get("hmkeystorepw"),"123456", Reha.aktIK);
			NebraskaEncryptor encryptor = store.getEncryptor(EMPFAENGERIK);
			String inFile = (SystemConfig.dta301OutBox+"EREH"+strAktEREH+".ORG").toLowerCase();
			long size = encryptor.encrypt(inFile, inFile.replace(".org", ""));
			encryptedSize = Integer.parseInt(Long.toString(size));
			System.out.println("       Originalgröße = "+originalSize);
			System.out.println("Verschlüsselte Größe = "+encryptedSize);
			doAuftragsDatei();
			doDateiErstellen(1);
			return true;
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NebraskaCryptoException e) {
			e.printStackTrace();
		} catch (NebraskaFileException e) {
			e.printStackTrace();
		} catch (NebraskaNotInitializedException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private String normalizeString(String in){
		return in.replace(",", "?,").replace(":", "?:");
	}
	private void doDateiErstellen(int art) throws IOException{
		File f = null;
		FileWriter fw = null;
		BufferedWriter bw = null;
		if(art == 0){
			f = new File( (SystemConfig.dta301OutBox+"EREH"+strAktEREH+".ORG").toLowerCase() );
			fw = new FileWriter(f);
		    bw = new BufferedWriter(fw); 
		    bw.write(gesamtbuf.toString()); 
		    bw.flush();
		    fw.flush();
		    fw.close();
		    return;
		}else if(art == 1){
			f = new File( (SystemConfig.dta301OutBox+"EREH"+strAktEREH+".AUF").toLowerCase() );
			fw = new FileWriter(f);
		    bw = new BufferedWriter(fw); 
		    bw.write(auftragsBuf.toString()); 
		    bw.flush();
		    fw.flush();
		    fw.close();
		    return;
		}
		
	}
	private void doOriginalDatei(){
		
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
	private String getEdiDatumFromDeutsch(String deutschDat){
		if(deutschDat.trim().length()<10){
			return "";
		}
		return deutschDat.substring(6)+deutschDat.substring(3,5)+deutschDat.substring(0,2);
	}
	
	private void holeVector(){
		originaldata.clear();
		//final byte ZEILENENDE = "\n".getBytes()[0];
		byte[] ende = {13};
		final String ERSATZ = new String(ende);
		final byte[] SYSTEMZEILE = {(System.getProperty("line.separator").getBytes().length == 2 ? 
				System.getProperty("line.separator").getBytes()[1] : 
					System.getProperty("line.separator").getBytes()[0]	)};

		String[] data = vecdta.get(0).get(28).split("\n");
		//String[] data = vecdta.get(0).get(28).split(System.getProperty("line.separator"));
		for(int i = 0; i < data.length;i++){
			originaldata.add(String.valueOf(data[i]));
		}
		/*
		for(int i = 0; i < data.length;i++){
			System.out.println("Element "+Integer.toString(i)+" = "+originaldata.get(i));
		}
		*/

	}
	private String springeAufUndHole(String springeauf,String hole){
		String ret = "";
		for(int i = 0; i < originaldata.size();i++){
			if(originaldata.get(i).startsWith(springeauf)){
				for(int x = i; x < originaldata.size();x++){
					if(originaldata.get(x).startsWith(hole)){
						return String.valueOf(originaldata.get(x).toString());
					}
				}
			}
		}
		return ret;
	}
	private String mache10erDatum(String datum){
		
		if(datum.contains("-")){
			return datum.replace("-","");
		}else{
			String[] dats = datum.split("\\.");
			return dats[2]+dats[1]+dats[0];
		}
	}
	private void doAuftragsDatei(){
		String abrDateiName="REH-RTA    ";
		auftragsBuf.append("500000"+"01"+"00000348"+"000");
		auftragsBuf.append("EREH"+this.strAktEREH);
		auftragsBuf.append("     ");
		auftragsBuf.append(StringTools.fuelleMitZeichen(Reha.aktIK, " ", false, 15));
		auftragsBuf.append(StringTools.fuelleMitZeichen(Reha.aktIK, " ", false, 15));
		auftragsBuf.append(StringTools.fuelleMitZeichen(EMPFAENGERIK, " ", false, 15));
		auftragsBuf.append(StringTools.fuelleMitZeichen(EMPFAENGERIK, " ", false, 15));
		auftragsBuf.append("000000");
		auftragsBuf.append("000000");
		auftragsBuf.append(abrDateiName);
		auftragsBuf.append(getEdiDatumFromDeutsch(DatFunk.sHeute())+getEdiTimeString(true));
		auftragsBuf.append(StringTools.fuelleMitZeichen("0", "0", false, 14));
		auftragsBuf.append(StringTools.fuelleMitZeichen("0", "0", false, 14));
		auftragsBuf.append(StringTools.fuelleMitZeichen("0", "0", false, 14));
		auftragsBuf.append("000000");
		auftragsBuf.append("0");
		auftragsBuf.append(StringTools.fuelleMitZeichen(Integer.toString(originalSize), "0", true, 12) );
		auftragsBuf.append(StringTools.fuelleMitZeichen(Integer.toString(encryptedSize), "0", true, 12) );
		auftragsBuf.append("I800");
		auftragsBuf.append("0303");
		auftragsBuf.append("   ");
		auftragsBuf.append(StringTools.fuelleMitZeichen("0", "0", true, 5) );
		auftragsBuf.append(StringTools.fuelleMitZeichen("0", "0", true, 8) );
		auftragsBuf.append("0");
		auftragsBuf.append("00");
		auftragsBuf.append("0");
		auftragsBuf.append(StringTools.fuelleMitZeichen("0", "0", true, 10) );
		auftragsBuf.append(StringTools.fuelleMitZeichen("0", "0", true, 6) );
		auftragsBuf.append(StringTools.fuelleMitZeichen(" ", " ", true, 28) );
		auftragsBuf.append(StringTools.fuelleMitZeichen(" ", " ", true, 44) );
		auftragsBuf.append(StringTools.fuelleMitZeichen(" ", " ", true, 30) );
	}
	
}
