package dta301;

import hauptFenster.Reha;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.Vector;

import sqlTools.SqlInfo;
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
	Vector<Vector<String>> vecdta = null;
	Vector<String> originaldata = new Vector<String>();
	int intAktEREH = -1;
	long originalSize = -1;
	long decodedSize = -1;
	
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
		vecdta = SqlInfo.holeFelder("select * from dta301 where id='"+id+"' LIMIT 1");
		
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
		buf301Body.append(springeAufUndHole("CTA+ABT+","CTA+ABT+")+EOL+NEWLINE);zeilen++;
		buf301Body.append("RFF+AES:"+(REHANUMMER =vecdta.get(0).get(2).toString()) +EOL+NEWLINE);zeilen++;
		if( ! (test =  springeAufUndHole("PNA+AB+","PNA+AB+")).equals("")){
			buf301Body.append(test+EOL+NEWLINE);zeilen++;
			if( ! (test =  springeAufUndHole("PNA+AB+","CTA+BEA+")).equals("")){
				buf301Body.append(test+EOL+NEWLINE);zeilen++;
			}
			if( ! (test =  springeAufUndHole("RFF+AHN:","RFF+AHN:")).equals("")){
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
		doOriginalDatei();
		
		//Datei erstellen
		//Größe festhalten
		
		//Verschlüsseln
		//Größe festhalten
		
		//AUF-Datei erstellen

		//Email erstellen
		
		//ab dafür
		
		/*
				f = new File(Reha.proghome+"edifact/"+Reha.aktIK+"/"+"esol0"+aktEsol+".auf");
				fw = new FileWriter(f);
			    bw = new BufferedWriter(fw); 
			    bw.write(auftragsBuf.toString()); 
			    bw.close(); 
			    fw.close();
		 
		 */
		
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
}
