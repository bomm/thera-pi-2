package dta301;

import hauptFenster.Reha;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.thera_pi.nebraska.crypto.NebraskaCryptoException;
import org.thera_pi.nebraska.crypto.NebraskaEncryptor;
import org.thera_pi.nebraska.crypto.NebraskaFileException;
import org.thera_pi.nebraska.crypto.NebraskaKeystore;
import org.thera_pi.nebraska.crypto.NebraskaNotInitializedException;

import emailHandling.EmailSendenExtern;
import entlassBerichte.EBerichtPanel;

import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemTools.IntegerTools;
import systemTools.StringTools;
import terminKalender.DatFunk;


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
	int anzahlUnhs = 1;
	int aktUnh = 1;
	int lastCIN = -1;
	int lfdNr = -1;
	boolean shouldBreak = false;
	boolean imtest = false;
	
	public RVMeldung301(int art, String id,int lfdNr){
		
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
		anzahlUnhs = 1;
		aktUnh = 1;
		EMPFAENGERIK = vecdta.get(0).get(3);
		this.lfdNr = lfdNr; 
		
	}
	/************************************************************/
	public boolean doEbericht(EBerichtPanel epanel){
		epanel.abrDlg.setzeLabel("erzeuge Blatt 1");
		holeVector();
		int zeilen = 1;
		shouldBreak = false;
		String test = "";
		String seite = "";
		String diagtext = "";
		Vector<String> flvec = new Vector<String>();
		String aktunh = StringTools.fuelleMitZeichen(Integer.toString(aktUnh), "0", true, 5);
		buf301Body.append("UNH+"+aktunh+"+MEDR03:D:08A:KR:97B'"+NEWLINE);zeilen++;
		buf301Body.append("BGM+21++10'"+NEWLINE);zeilen++; 
		buf301Body.append("DTM+137:"+DATUM10+":102'"+NEWLINE);zeilen++;
		buf301Body.append("RFF+ACD:01'"+NEWLINE);zeilen++; //Hier die Datenbank untersuchen
		//buf301Body.append("PNA+MS++"+Reha.aktIK+EOL+NEWLINE);zeilen++;
		buf301Body.append("PNA+MS++"+vecdta.get(0).get(4).toString()+EOL+NEWLINE);zeilen++;
		buf301Body.append("PNA+MR++"+(EMPFAENGERIK = vecdta.get(0).get(3).toString())+EOL+NEWLINE);zeilen++;
		buf301Body.append("PNA+BY++"+(KOSTENTRAEGER = vecdta.get(0).get(6).toString())+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("CTA+BEA+","CTA+BEA+")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("RFF+FI:","RFF+FI:")+EOL+NEWLINE);zeilen++;
		buf301Body.append("PNA+MT++"+vecdta.get(0).get(4).toString()+EOL+NEWLINE);zeilen++; //Hier die div. IK's einbauen
		buf301Body.append("ADR+2++"+SystemConfig.hmFirmenDaten.get("Ort")+EOL+NEWLINE);zeilen++; //Ort der Unterschriften
		buf301Body.append("CTA+ABT+2300"+EOL+NEWLINE);zeilen++; //hier nach Inikationsgruppen untersuchen
		//Arzt 1
		buf301Body.append("CTA+DRL"+(epanel.barzttf[0].getText().trim().equals("")?"":"+:"+epanel.barzttf[0].getText().trim())+EOL+NEWLINE);zeilen++;
		buf301Body.append("CTA+DRS"+(epanel.barzttf[2].getText().trim().equals("")?"":"+:"+epanel.barzttf[2].getText().trim())+EOL+NEWLINE);zeilen++;
		buf301Body.append("CTA+DRO"+(epanel.barzttf[1].getText().trim().equals("")?"":"+:"+epanel.barzttf[1].getText().trim())+EOL+NEWLINE);zeilen++;
		buf301Body.append("RFF+AES:"+(REHANUMMER =vecdta.get(0).get(2).toString()) +EOL+NEWLINE);zeilen++;
		buf301Body.append("DTM+242:"+(epanel.btf[27].getText().trim().length() < 10
				? this.mache10erDatum(DatFunk.sHeute())+":102" :
				mache10erDatum(epanel.btf[27].getText().trim())+":102")+EOL+NEWLINE);zeilen++;
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
		buf301Body.append("FCA+MD'"+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("PNA+BM+","PNA+BM+")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("PNA+BM+","RFF+AGU:")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("PNA+BM+","RFF+AGF:")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("PNA+BM+","RFF+ADE:")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("PNA+BM+","RFF+AEN:")+EOL+NEWLINE);zeilen++;
		if( ! (test =  springeAufUndHole("PNA+BM+","RFF+ALX:")).equals("")){
			buf301Body.append(test+EOL+NEWLINE);zeilen++;
		}
		buf301Body.append(springeAufUndHole("PNA+BM+","DTM+329:")+EOL+NEWLINE);zeilen++;
		
		buf301Body.append("PRC+BL1:::03"+EOL+NEWLINE);zeilen++;

		test = epanel.bcmb[0].getSelectedItem().toString();
		if(test.trim().equals("")){shouldBreak=true;}
		buf301Body.append("IMD+++"+test+":B06"+EOL+NEWLINE);zeilen++;
		
		test = epanel.bcmb[1].getSelectedItem().toString();
		if(test.trim().equals("")){shouldBreak=true;}
		buf301Body.append("IMD+++"+test+":B01"+EOL+NEWLINE);zeilen++;
		
		test = epanel.bcmb[17].getSelectedItem().toString();
		if(test.trim().equals("")){shouldBreak=true;}
		buf301Body.append("IMD+++"+test+":B10"+EOL+NEWLINE);zeilen++;
		
		test = epanel.bcmb[18].getSelectedItem().toString();
		if(test.trim().equals("")){shouldBreak=true;}
		buf301Body.append("IMD+++"+test+":B03"+EOL+NEWLINE);zeilen++;
		
		test = epanel.bcmb[19].getSelectedItem().toString();
		if(test.trim().equals("")){shouldBreak=true;}
		buf301Body.append("IMD+++"+test+":B13"+EOL+NEWLINE);zeilen++;
		
		int[] iseite = {2,5,8,11,14};
		int[] isicher = {3,6,9,12,15};
		int[] ierfolg = {4,7,10,13,16};
		for(int i = 0;i < 5;i++){
			test = epanel.btf[17+i].getText().trim();
			if(!test.equals("")){
				seite = ( ! epanel.bcmb[iseite[i]].getSelectedItem().toString().trim().equals("")
						? epanel.bcmb[iseite[i]].getSelectedItem().toString()
						: "");
				seite = seite+"+";
				seite = seite+ ( ! epanel.bcmb[isicher[i]].getSelectedItem().toString().trim().equals("")
						? epanel.bcmb[isicher[i]].getSelectedItem().toString()
						: "");
				seite = seite+":::";
				seite = seite+( ! epanel.bcmb[ierfolg[i]].getSelectedItem().toString().trim().equals("")
						? epanel.bcmb[ierfolg[i]].getSelectedItem().toString()
								: "");
				buf301Body.append("CIN+DIA+"+test+":10R::"+seite+EOL+NEWLINE);zeilen++;	
			}
		}
		buf301Body.append("RFF+AEA:99999"+EOL+NEWLINE);zeilen++;
		flvec = StringTools.fliessTextZerhacken(StringTools.do301String(epanel.btf[25].getText().trim()), 55, "\n");
		if(flvec.size() > 1 ){
			buf301Body.append("FTX+BRF+++B:"+flvec.get(0)+EOL+NEWLINE);zeilen++;
			buf301Body.append("FTX+BRF+++B:"+flvec.get(1)+EOL+NEWLINE);zeilen++;
		}else{
			buf301Body.append("FTX+BRF+++B:"+flvec.get(0)+EOL+NEWLINE);zeilen++;
		}
		test = Integer.toString(IntegerTools.trailNullAndRetInt(epanel.btf[22].getText()));
		buf301Body.append("QTY+AGW:"+test+":KG"+EOL+NEWLINE);zeilen++;
		test = Integer.toString(IntegerTools.trailNullAndRetInt(epanel.btf[23].getText()));
		buf301Body.append("QTY+EGW:"+test+":KG"+EOL+NEWLINE);zeilen++;
		test = Integer.toString(IntegerTools.trailNullAndRetInt(epanel.btf[24].getText()));
		buf301Body.append("QTY+GRO:"+test+":CM"+EOL+NEWLINE);zeilen++;
		buf301Body.append("PAS+2"+EOL+NEWLINE);zeilen++;
		test = mache10erDatum(epanel.btf[15].getText())+mache10erDatum(epanel.btf[16].getText());
		buf301Body.append("DTM+322:"+test+":711"+EOL+NEWLINE);zeilen++;
		for(int i = 0; i < 17; i++){
			if(epanel.bchb[i].isSelected()){
				test = StringTools.fuelleMitZeichen(Integer.toString(i+1), "0", true, 2);
				buf301Body.append("CLI+VMS+"+test+":MSN"+EOL+NEWLINE);zeilen++;
			}
		}
		epanel.abrDlg.setzeLabel("erzeuge Blatt 1b Sozialmedizin");
		buf301Body.append("PRC+B1X:::03"+EOL+NEWLINE);zeilen++;
		buf301Body.append("CLI+DTX"+EOL+NEWLINE);zeilen++;
		/************Diagnosetexte******/
		for(int i = 0;i < 5;i++){
			test = epanel.btf[17+i].getText();
			if(!test.equals("")){
				if(test.trim().length() > 120){
					JOptionPane.showMessageDialog(null,"Achtung: Diagnosetext:"+Integer.toString(i+1)+" ist größer als 120 Zeichen");
				}
				diagtext = StringTools.do301String(epanel.bta[i].getText()).trim().replace("\n"," ").replace("\r", " ").replace("\t", " ");
				flvec = StringTools.fliessTextZerhacken(diagtext, 41, "\n");
				test = "FTX+TXT+++B:";
				for(int i2 = 0;i2 < flvec.size();i2++){
					test = test+flvec.get(i2);
					if(i2 >= 3){
						JOptionPane.showMessageDialog(null,"Achtung mehr als 3 Zeilen Diagnosetext bei Diagnose "+Integer.toString(i+1)+"\n\n"+
								"Manueller Eingriff in die Edifakt-Nachricht notwendig");
					}
					if(i2 == (flvec.size()-1) ){
						break;
					}
					test=test+":"+NEWLINE;
				}
				buf301Body.append(test+EOL+NEWLINE);zeilen++;
			}
		}
		buf301Body.append("CLI+MAS"+EOL+NEWLINE);zeilen++;
		flvec = StringTools.fliessTextZerhacken(StringTools.do301String(epanel.bta[5].getText().trim()), 70, "\n");
		if(flvec.size()<=0){
			buf301Body.append("FTX+TXT+++B"+EOL+NEWLINE);zeilen++;
		}else{
			for(int i = 0; i < flvec.size();i++){
				buf301Body.append("FTX+TXT+++B:"+flvec.get(i)+EOL+NEWLINE);zeilen++;
			}
		}
		buf301Body.append("PRC+B1a:::02"+EOL+NEWLINE);zeilen++;
		for(int i = 1;i < 15;i++){
			buf301Body.append("CIN+SML+"+getSMLResult(epanel,i)+":C"+StringTools.fuelleMitZeichen(Integer.toString(i), "0", true, 2)+EOL+NEWLINE);zeilen++;	
		}
		test = StringTools.do301String(epanel.bta[7].getText().trim()); 
		if(!test.equals("")){
			flvec = StringTools.fliessTextZerhacken(test,70,"\n");
			for(int i = 0; i < flvec.size();i++){
				buf301Body.append("FTX+SMX+++B:"+flvec.get(i)+EOL+NEWLINE);zeilen++;				
			}
		}else{
			buf301Body.append("FTX+SMX+++B"+EOL+NEWLINE);zeilen++;
		}
		//KTL
		epanel.abrDlg.setzeLabel("untersuche KTL-Codes");
		buf301Body.append("PRC+Bb1:::02"+EOL+NEWLINE);zeilen++;
		//
		test = StringTools.do301String(epanel.bta[8].getText().trim()); 
		if(!test.equals("")){
			flvec = StringTools.fliessTextZerhacken(test,70,"\n");
			for(int i = 0; i < flvec.size();i++){
				buf301Body.append("FTX+FTX+++B:"+flvec.get(i)+EOL+NEWLINE);zeilen++;				
			}
		}else{
			buf301Body.append("FTX+TXT+++B"+EOL+NEWLINE);zeilen++;
		}
		//KTL-Seite1 (1-25)
		boolean wenigerAls25 = false;
		String ktlcode="",ktldauer="",ktlanzahl="",ktlfehler="",ktltext="";
		for(int i = 0; i < 25;i++){
			if(epanel.ktlcmb[i].getSelectedIndex()>0){
				ktlcode = epanel.ktltfc[i].getText().trim();
				ktldauer = epanel.ktltfd[i].getText().trim();
				ktlanzahl = epanel.ktltfa[i].getText().trim();
				ktltext = StringTools.do301String(epanel.ktlcmb[i].getSelectedItem().toString().trim());
				if(ktlcode.equals("") || ktldauer.equals("") || ktlanzahl.equals("") || ktltext.equals("") ){
					ktlfehler = "Fehlerhafter KTL auf KTL-Blatt 1, Maßnahmenummer "+Integer.toString(i+1);
					JOptionPane.showMessageDialog(null,ktlfehler);
					shouldBreak = true;
				}
				buf301Body.append("CLI+KTL+"+ktlcode+ktldauer+":KTL"+EOL+NEWLINE);zeilen++;
				buf301Body.append("IMD+++"+Integer.toString(i+1)+EOL+NEWLINE);zeilen++;
				flvec = StringTools.fliessTextZerhacken(ktltext,54,"\n");
				for(int i2 = 0; i2 < flvec.size();i2++){
					buf301Body.append("FTX+TXT+++B:"+flvec.get(i2)+EOL+NEWLINE);zeilen++;
					if(i2 == 1){break;} // mehr als 2 Zeilen sind nicht erlaubt
				}
				buf301Body.append("QTY+3:"+ktlanzahl+EOL+NEWLINE);zeilen++;
			}else{
				wenigerAls25 = true;
				break;
			}
		}
		//KTL-Seite 2 (1-25) wird erforderlich
		if(!wenigerAls25 && epanel.ktlcmb[25].getSelectedIndex()>0){
			buf301Body.append("PRC+Bb2:::02"+EOL+NEWLINE);zeilen++;
			test = StringTools.do301String(epanel.bta[9].getText().trim()); 
			if(!test.equals("")){
				flvec = StringTools.fliessTextZerhacken(test,70,"\n");
				for(int i = 0; i < flvec.size();i++){
					buf301Body.append("FTX+TXT+++B:"+flvec.get(i)+EOL+NEWLINE);zeilen++;				
				}
			}else{
				buf301Body.append("FTX+TXT+++B"+EOL+NEWLINE);zeilen++;
			}
			for(int i = 0; i < 25;i++){
				if(epanel.ktlcmb[i+25].getSelectedIndex()>0){
					ktlcode = epanel.ktltfc[i+25].getText().trim();
					ktldauer = epanel.ktltfd[i+25].getText().trim();
					ktlanzahl = epanel.ktltfa[i+25].getText().trim();
					ktltext = StringTools.do301String(epanel.ktlcmb[i+25].getSelectedItem().toString().trim());
					if(ktlcode.equals("") || ktldauer.equals("") || ktlanzahl.equals("") || ktltext.equals("") ){
						ktlfehler = "Fehlerhafter KTL auf KTL-Blatt 2, Maßnahmenummer "+Integer.toString(i+1);
						JOptionPane.showMessageDialog(null,ktlfehler);
						shouldBreak = true;
					}
					buf301Body.append("CLI+KTL+"+ktlcode+ktldauer+":KTL"+EOL+NEWLINE);zeilen++;
					buf301Body.append("IMD+++"+Integer.toString(i+1)+EOL+NEWLINE);zeilen++;
					flvec = StringTools.fliessTextZerhacken(ktltext,54,"\n");
					for(int i2 = 0; i2 < flvec.size();i2++){
						buf301Body.append("FTX+TXT+++B:"+flvec.get(i2)+EOL+NEWLINE);zeilen++;
						if(i2 == 1){break;} // mehr als 2 Zeilen sind nicht erlaubt
					}
					buf301Body.append("QTY+3:"+ktlanzahl+EOL+NEWLINE);zeilen++;
				}else{
					break;
				}
			}
			
		}
		epanel.abrDlg.setzeLabel("erzeuge Freitext");
		buf301Body.append("PRC+BER:::01"+EOL+NEWLINE);zeilen++;
		//Jetzt den Freitext holen und aufbereiten - der größte Scheiß aller Zeiten!!!
		test = StringTools.do301String(epanel.document.getTextService().getText().getText());
		flvec = StringTools.fliessTextZerhacken(test, 70, "\n");
		int seiten = flvec.size()/53;
		if( (flvec.size() % 53) > 0){seiten++;}
		buf301Body.append("QTY+3:"+Integer.toString(seiten)+EOL+NEWLINE);zeilen++;
		int aktuellezeile = -1;
		//System.out.println("Seitenanzahl = "+seiten);
		for(int i = 0; i < seiten;i++){
			buf301Body.append("CLI+LDT"+EOL+NEWLINE);zeilen++;
			buf301Body.append("IMD+++5:B05"+EOL+NEWLINE);zeilen++;
			for(int i2=0; i2 < 53; i2++){
				aktuellezeile = (i*53 )+i2;
				test = flvec.get(aktuellezeile);
				if(test.trim().equals("")){
					buf301Body.append("FTX+LTX+++B"+EOL+NEWLINE);zeilen++;
				}else{
					buf301Body.append("FTX+LTX+++"+(Dta301CodeListen.mussFettDruck(test) ? "F:" : "B:")
							+test+EOL+NEWLINE);zeilen++;
				}
				if(aktuellezeile==(flvec.size()-1)){
					break;
				}
			}
		}
		//String aktunh = StringTools.fuelleMitZeichen(Integer.toString(aktUnh), "0", true, 5);
		buf301Body.append("UNT+"+
				StringTools.fuelleMitZeichen(Integer.toString(zeilen),"0",true,5)+"+"+aktunh+
				EOL+NEWLINE);zeilen++;
		if(this.shouldBreak){
			JOptionPane.showMessageDialog(null, "In der Erstellung des E-Berichtes nach §301 ist ein Fehler aufgetreten.\nVersand findet nicht statt!!!");
			return false;
		}
		doKopfDaten();
		doFussDaten();
		anzahlUnhs++;
		gesamtbuf.append(buf301Header.toString());
		gesamtbuf.append(buf301Body.toString());
		gesamtbuf.append(buf301Footer.toString());
		intAktEREH = SqlInfo.erzeugeNummerMitMax("esol", 999);
		strAktEREH = "0"+StringTools.fuelleMitZeichen(Integer.toString(intAktEREH), "0", true, 3);
		epanel.abrDlg.setzeLabel("E-Bericht verschlüsseln");
		if(doKeyStoreAktion(true)){
			//Mail Versenden
			epanel.abrDlg.setzeLabel("E-Bericht versenden");
			//In neue Tabelle schreiben
			String cmd = "insert into dtafall set nachrichttyp='7', nachrichtart='7', pat_intern='"+
			vecdta.get(0).get(1).toString()+"', rez_nr='"+vecdta.get(0).get(2).toString()+"', "+
			"nachrichtdatum='"+DatFunk.sDatInSQL(DatFunk.sHeute())+"', nachrichtorg='"+
			StringTools.EscapedDouble(gesamtbuf.toString())+"',"+
			"nachrichtauf='"+
			StringTools.Escaped(auftragsBuf.toString())+"', bearbeiter='"+Reha.aktUser+"', "+
			"esolname='"+(imtest ? "T" : "E")+"REH"+strAktEREH+"', "+
			"icr='"+LFDNUMMER+"'";
			SqlInfo.sqlAusfuehren(cmd);
			return true;
		}
		return false;
	}
	
	/************************************************************/
	public boolean doBeginn(String beginnDatum,String uhrZeit,String fliesstext,int aufnahmeart,boolean bewilligt,String kkhentlassDatum){
		holeVector();
		int zeilen = 1;
		shouldBreak = false;
		String test = "";
		String aktunh = StringTools.fuelleMitZeichen(Integer.toString(aktUnh), "0", true, 5);
		buf301Body.append("UNH+"+aktunh+"+MEDR02:D:01A:KR:97B'"+NEWLINE);zeilen++;
		//Aufnahmeanzeige
		if(aufnahmeart==0){
			if(bewilligt){
				buf301Body.append("BGM+01++10'"+NEWLINE);zeilen++;		
			}else{
				buf301Body.append("BGM+02++10'"+NEWLINE);zeilen++;
			}
		}
		//Rückstellung
		if(aufnahmeart==1){
			buf301Body.append("BGM+08++10'"+NEWLINE);zeilen++;
		}
		//Absage
		if(aufnahmeart==2){
			buf301Body.append("BGM+07++10'"+NEWLINE);zeilen++;
		}
		buf301Body.append("DTM+137:"+DATUM10+":102'"+NEWLINE);zeilen++;
		buf301Body.append("RFF+ACD:01'"+NEWLINE);zeilen++;
		buf301Body.append("PNA+MS++"+vecdta.get(0).get(4).toString()+EOL+NEWLINE);zeilen++;
		buf301Body.append("PNA+MR++"+(EMPFAENGERIK = vecdta.get(0).get(3).toString())+EOL+NEWLINE);zeilen++;
		buf301Body.append("PNA+BY++"+(KOSTENTRAEGER = vecdta.get(0).get(6).toString())+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("CTA+BEA+","CTA+BEA+")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("RFF+FI:","RFF+FI:")+EOL+NEWLINE);zeilen++;
		buf301Body.append("PNA+MT++"+vecdta.get(0).get(4).toString()+EOL+NEWLINE);zeilen++;//Hier die div. IK's einbauen
		buf301Body.append("CTA+ABT+2300"+EOL+NEWLINE);zeilen++;//hier nach Inikationsgruppen untersuchen
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
		if( ! (test =  springeAufUndHole("PNA+BM+","RFF+ALX:")).equals("")){
			buf301Body.append(test+EOL+NEWLINE);zeilen++;
		}
		buf301Body.append(springeAufUndHole("PNA+BM+","ADR++")+EOL+NEWLINE);zeilen++;
		if( ! (test =  springeAufUndHole("PNA+BM+","ADR+1+")).equals("")){
			buf301Body.append(test+EOL+NEWLINE);zeilen++;
		}
		buf301Body.append(springeAufUndHole("PNA+BM+","DTM+329:")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("PNA+BM+","PDI+")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("PNA+BM+","NAT+")+EOL+NEWLINE);zeilen++;
		//Aufnahmemitteilung
		//*******************//
		if(aufnahmeart==0){
			buf301Body.append("PRC+ADMIN3'"+NEWLINE);zeilen++;
			buf301Body.append(springeAufUndHole("PNA+BM+","IMD+")+EOL+NEWLINE);zeilen++;
			String cin = springeAufUndHole("PNA+BM+","CIN+").replace(":I0R",":10R");
			buf301Body.append(cin+EOL+NEWLINE);zeilen++;
			while( ! (cin=springeAufUndHoleNaechsten(cin,"CIN+").replace(":I0R",":10R")).equals("") ){
				buf301Body.append(cin+EOL+NEWLINE);zeilen++;
			}
			buf301Body.append("DTM+194:"+mache10erDatum(beginnDatum)+":102"+EOL+NEWLINE);zeilen++;
			buf301Body.append("DTM+163:"+uhrZeit+":401"+EOL+NEWLINE);zeilen++;
			if(kkhentlassDatum != null){
				if(kkhentlassDatum.trim().equals(".  .")){
					JOptionPane.showMessageDialog(null,"Fehler kein oder falsches Krankenhaus-Entlassdatum angegeben");
					return false;
				}
				buf301Body.append("DTM+293:"+mache10erDatum(kkhentlassDatum)+":102"+EOL+NEWLINE);zeilen++;
			}
			if(!fliesstext.equals("")){
				Vector<String> flvec = new Vector<String>();
				test = StringTools.do301String(fliesstext);
				flvec = StringTools.fliessTextZerhacken(test, 70, "\n");
				for(int i = 0; i < flvec.size();i++){
					buf301Body.append("FTX+TXT+++B:"+flvec.get(i)+EOL+NEWLINE);zeilen++;	
				}
			}
			buf301Body.append("UNT+"+
					StringTools.fuelleMitZeichen(Integer.toString(zeilen),"0",true,5)+"+"+aktunh+
					EOL+NEWLINE);zeilen++;
			doKopfDaten();
			doFussDaten();
		}
		//Rückstellung
		if(aufnahmeart==1){
			buf301Body.append("PRC+ADMIN1'"+NEWLINE);zeilen++;
			buf301Body.append("DTM+341:"+mache10erDatum(beginnDatum)+":102"+EOL+NEWLINE);zeilen++;
			if(!fliesstext.equals("")){
				Vector<String> flvec = new Vector<String>();
				test = StringTools.do301String(fliesstext);
				flvec = StringTools.fliessTextZerhacken(test, 70, "\n");
				for(int i = 0; i < flvec.size();i++){
					buf301Body.append("FTX+TXT+++B:"+flvec.get(i)+EOL+NEWLINE);zeilen++;	
				}
			}
			buf301Body.append("UNT+"+
					StringTools.fuelleMitZeichen(Integer.toString(zeilen),"0",true,5)+"+"+aktunh+
					EOL+NEWLINE);zeilen++;

			doKopfDaten();
			doFussDaten();			
		}
		//Absage
		if(aufnahmeart==2){
			buf301Body.append("PRC+ADMIN2'"+NEWLINE);zeilen++;
			buf301Body.append("DTM+46:"+mache10erDatum(beginnDatum)+":102"+EOL+NEWLINE);zeilen++;
			if(!fliesstext.equals("")){
				Vector<String> flvec = new Vector<String>();
				test = StringTools.do301String(fliesstext);
				flvec = StringTools.fliessTextZerhacken(test, 70, "\n");
				for(int i = 0; i < flvec.size();i++){
					buf301Body.append("FTX+TXT+++B:"+flvec.get(i)+EOL+NEWLINE);zeilen++;	
				}
			}
			buf301Body.append("UNT+"+
					StringTools.fuelleMitZeichen(Integer.toString(zeilen),"0",true,5)+"+"+aktunh+
					EOL+NEWLINE);zeilen++;

			doKopfDaten();
			doFussDaten();			
		}
		//Geplante Aufnahme melden
		if(aufnahmeart==3){
			buf301Body.append("PRC+ADMIN1'"+NEWLINE);zeilen++;
			buf301Body.append("DTM+291:"+mache10erDatum(beginnDatum)+":102"+EOL+NEWLINE);zeilen++;
			if(!fliesstext.equals("")){
				Vector<String> flvec = new Vector<String>();
				test = StringTools.do301String(fliesstext);
				flvec = StringTools.fliessTextZerhacken(test, 70, "\n");
				for(int i = 0; i < flvec.size();i++){
					buf301Body.append("FTX+TXT+++B:"+flvec.get(i)+EOL+NEWLINE);zeilen++;	
				}
			}
			buf301Body.append("UNT+"+
					StringTools.fuelleMitZeichen(Integer.toString(zeilen),"0",true,5)+"+"+aktunh+
					EOL+NEWLINE);zeilen++;

			doKopfDaten();
			doFussDaten();			
		}

		gesamtbuf.append(buf301Header.toString());
		gesamtbuf.append(buf301Body.toString());
		gesamtbuf.append(buf301Footer.toString());
		anzahlUnhs++;
		//doOriginalDatei();
		//Datei erstellen
		//int intAktEREH = -1;
		//String strAktEREH = null;
		//long originalSize = -1;
		//long decodedSize = -1;
		intAktEREH = SqlInfo.erzeugeNummerMitMax("esol", 999);
		strAktEREH = "0"+StringTools.fuelleMitZeichen(Integer.toString(intAktEREH), "0", true, 3);
		
		if(doKeyStoreAktion(true)){
			//Mail Versenden
			//In neue Tabelle schreiben
			String typ = "";
			if(aufnahmeart==0){typ="3";}
			if(aufnahmeart==1){typ="11";}
			if(aufnahmeart==2){typ="9";}
			if(aufnahmeart==3){typ="10";}
			String cmd = "insert into dtafall set nachrichttyp='"+typ+"', nachrichtart='"+typ+"', pat_intern='"+
			vecdta.get(0).get(1).toString()+"', rez_nr='"+vecdta.get(0).get(2).toString()+"', "+
			"nachrichtdatum='"+DatFunk.sDatInSQL(DatFunk.sHeute())+"', nachrichtorg='"+
			StringTools.Escaped(gesamtbuf.toString())+"',"+
			"nachrichtauf='"+
			StringTools.Escaped(auftragsBuf.toString())+"', bearbeiter='"+Reha.aktUser+"', "+
			"esolname='"+(imtest ? "T" : "E")+"REH"+strAktEREH+"', "+
			"icr='"+LFDNUMMER+"'";

			SqlInfo.sqlAusfuehren(cmd);
			return true;
		}
		return false;
	}
	/*********************************
	 * 
	 * 
	 * 
	 * 
	 * @param beginnDatum
	 * @param endeDatum
	 * @param vart
	 * @param hinweis
	 * @return
	 * 
	 * 
	 */
	public boolean doVerlaengerung(String beginnDatum,String endeDatum,int vart, String hinweis){
		//ubart = 0 Beginn der U, 1 = Ende der U, 2 = Beginn und Ende der U
		holeVector();
		int zeilen = 1;
		shouldBreak = false;
		String test = "";
		String aktunh = StringTools.fuelleMitZeichen(Integer.toString(aktUnh), "0", true, 5);
		buf301Body.append("UNH+"+aktunh+"+MEDR02:D:01A:KR:97B'"+NEWLINE);zeilen++;
		if(vart==0){
			buf301Body.append("BGM+10++10'"+NEWLINE);zeilen++;	
		}
		if(vart==1){
			buf301Body.append("BGM+03++10'"+NEWLINE);zeilen++;	
		}
		
		buf301Body.append("DTM+137:"+DATUM10+":102'"+NEWLINE);zeilen++;
		buf301Body.append("RFF+ACD:"+StringTools.fuelleMitZeichen(Integer.toString(this.lfdNr), "0", true, 2)+"'"+NEWLINE);zeilen++; //Hier die Datenbank untersuchen
		buf301Body.append("PNA+MS++"+vecdta.get(0).get(4).toString()+EOL+NEWLINE);zeilen++;
		buf301Body.append("PNA+MR++"+(EMPFAENGERIK = vecdta.get(0).get(3).toString())+EOL+NEWLINE);zeilen++;
		buf301Body.append("PNA+BY++"+(KOSTENTRAEGER = vecdta.get(0).get(6).toString())+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("CTA+BEA+","CTA+BEA+")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("RFF+FI:","RFF+FI:")+EOL+NEWLINE);zeilen++;
		buf301Body.append("PNA+MT++"+vecdta.get(0).get(4).toString()+EOL+NEWLINE);zeilen++; //Hier die div. IK's einbauen
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
		if( ! (test =  springeAufUndHole("PNA+BM+","RFF+ALX:")).equals("")){
			buf301Body.append(test+EOL+NEWLINE);zeilen++;
		}
		buf301Body.append(springeAufUndHole("PNA+BM+","ADR++")+EOL+NEWLINE);zeilen++;
		if( ! (test =  springeAufUndHole("PNA+BM+","ADR+1+")).equals("")){
			buf301Body.append(test+EOL+NEWLINE);zeilen++;
		}
		buf301Body.append(springeAufUndHole("PNA+BM+","DTM+329:")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("PNA+BM+","PDI+")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("PNA+BM+","NAT+")+EOL+NEWLINE);zeilen++;
		/******************/
		buf301Body.append("PRC+ADMIN4'"+NEWLINE);zeilen++;	
		//buf301Body.append(springeAufUndHole("PNA+BM+","CIN+").replace(":I0R",":10R")+EOL+NEWLINE);zeilen++;
		String cin = springeAufUndHole("PNA+BM+","CIN+").replace(":I0R",":10R");
		buf301Body.append(cin+EOL+NEWLINE);zeilen++;
		while( ! (cin=springeAufUndHoleNaechsten(cin,"CIN+").replace(":I0R",":10R")).equals("") ){
			buf301Body.append(cin+EOL+NEWLINE);zeilen++;
		}

		buf301Body.append("DTM+48:"+mache10erDatum(endeDatum)+":102"+EOL+NEWLINE);zeilen++;
		long tage = DatFunk.TageDifferenz(beginnDatum, endeDatum);
		tage++;
		buf301Body.append("QTY+192:"+StringTools.fuelleMitZeichen(Long.toString(tage), "0", true, 3)+":DAY"+EOL+NEWLINE);zeilen++;


		buf301Body.append("UNT+"+
				StringTools.fuelleMitZeichen(Integer.toString(zeilen),"0",true,5)+"+"+aktunh+
				EOL+NEWLINE);zeilen++;
				
		
				
/*
				
*/				
		/***************Medizinsiche Begündung*****************/
		if(vart==1){
			anzahlUnhs ++;
			aktUnh++;
			aktunh = StringTools.fuelleMitZeichen(Integer.toString(aktUnh), "0", true, 5);

			zeilen = 1;
			test = "";
			buf301Body.append("UNH+"+aktunh+"+MEDR02:D:01A:KR:97B'"+NEWLINE);zeilen++;
			buf301Body.append("BGM+05++10'"+NEWLINE);zeilen++;
			buf301Body.append("DTM+137:"+DATUM10+":102'"+NEWLINE);zeilen++;
			buf301Body.append("RFF+ACD:"+StringTools.fuelleMitZeichen(Integer.toString(this.lfdNr+1), "0", true, 2)+"'"+NEWLINE);zeilen++; //Hier die Datenbank untersuchen
			//buf301Body.append("RFF+ACD:01'"+NEWLINE);zeilen++; //Hier die Datenbank untersuchen
			buf301Body.append("PNA+MS++"+vecdta.get(0).get(4).toString()+EOL+NEWLINE);zeilen++;
			buf301Body.append("PNA+MR++"+(EMPFAENGERIK = vecdta.get(0).get(3).toString())+EOL+NEWLINE);zeilen++;
			buf301Body.append("PNA+BY++"+(KOSTENTRAEGER = vecdta.get(0).get(6).toString())+EOL+NEWLINE);zeilen++;
			buf301Body.append(springeAufUndHole("CTA+BEA+","CTA+BEA+")+EOL+NEWLINE);zeilen++;
			buf301Body.append(springeAufUndHole("RFF+FI:","RFF+FI:")+EOL+NEWLINE);zeilen++;
			buf301Body.append("PNA+MT++"+vecdta.get(0).get(4).toString()+EOL+NEWLINE);zeilen++; //Hier die div. IK's einbauen
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
			if( ! (test =  springeAufUndHole("PNA+BM+","RFF+ALX:")).equals("")){
				buf301Body.append(test+EOL+NEWLINE);zeilen++;
			}
			buf301Body.append(springeAufUndHole("PNA+BM+","ADR++")+EOL+NEWLINE);zeilen++;
			if( ! (test =  springeAufUndHole("PNA+BM+","ADR+1+")).equals("")){
				buf301Body.append(test+EOL+NEWLINE);zeilen++;
			}
			buf301Body.append(springeAufUndHole("PNA+BM+","DTM+329:")+EOL+NEWLINE);zeilen++;
			buf301Body.append(springeAufUndHole("PNA+BM+","PDI+")+EOL+NEWLINE);zeilen++;
			buf301Body.append(springeAufUndHole("PNA+BM+","NAT+")+EOL+NEWLINE);zeilen++;
			/******************/
			buf301Body.append("PRC+ADMIN4'"+NEWLINE);zeilen++;	
			//buf301Body.append(springeAufUndHole("PNA+BM+","CIN+").replace(":I0R",":10R")+EOL+NEWLINE);zeilen++;
			cin = springeAufUndHole("PNA+BM+","CIN+").replace(":I0R",":10R");
			buf301Body.append(cin+EOL+NEWLINE);zeilen++;
			while( ! (cin=springeAufUndHoleNaechsten(cin,"CIN+").replace(":I0R",":10R")).equals("") ){
				buf301Body.append(cin+EOL+NEWLINE);zeilen++;
			}
			
			buf301Body.append("DTM+48:"+mache10erDatum(endeDatum)+":102"+EOL+NEWLINE);zeilen++;
			if(!hinweis.equals("")){
				Vector<String> flvec = new Vector<String>();
				test = StringTools.do301String("Medizinische Begündung:\n"+hinweis);
				flvec = StringTools.fliessTextZerhacken(test, 70, "\n");
				for(int i = 0; i < flvec.size();i++){
					buf301Body.append("FTX+TXT+++B:"+flvec.get(i)+EOL+NEWLINE);zeilen++;	
				}
			}			
			buf301Body.append("QTY+192:"+StringTools.fuelleMitZeichen(Long.toString(tage), "0", true, 3)+":DAY"+EOL+NEWLINE);zeilen++;


			buf301Body.append("UNT+"+
					StringTools.fuelleMitZeichen(Integer.toString(zeilen),"0",true,5)+"+"+aktunh+
					EOL+NEWLINE);zeilen++;
			
		}
		/***************Ende der Medizinsiche Begündung*****************/
		doKopfDaten();
		doFussDaten();
		gesamtbuf.append(buf301Header.toString());
		gesamtbuf.append(buf301Body.toString());
		gesamtbuf.append(buf301Footer.toString());
		intAktEREH = SqlInfo.erzeugeNummerMitMax("esol", 999);
		strAktEREH = "0"+StringTools.fuelleMitZeichen(Integer.toString(intAktEREH), "0", true, 3);
		anzahlUnhs++;
		if(doKeyStoreAktion(true)){
			//Mail Versenden
			//In neue Tabelle schreiben
			try{
				String cmd = "insert into dtafall set nachrichttyp='5', nachrichtart='5', pat_intern='"+
				vecdta.get(0).get(1).toString()+"', rez_nr='"+vecdta.get(0).get(2).toString()+"', "+
				"nachrichtdatum='"+DatFunk.sDatInSQL(DatFunk.sHeute())+"', nachrichtorg='"+
				StringTools.EscapedDouble(gesamtbuf.toString())+"',"+
				"nachrichtauf='"+
				StringTools.Escaped(auftragsBuf.toString())+"', bearbeiter='"+Reha.aktUser+"', "+
				"esolname='"+(imtest ? "T" : "E")+"REH"+strAktEREH+"', "+
				"icr='"+LFDNUMMER+"'";
				SqlInfo.sqlAusfuehren(cmd);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			return true;
			/*************************/
		}
		return false;
	}
 
	public boolean doUnterbrechung(String beginnDatum,String endeDatum,int ubart,int ubgrund, String hinweis){
		//ubart = 0 Beginn der U, 1 = Ende der U, 2 = Beginn und Ende der U
		String fallart = "";
		holeVector();
		int zeilen = 1;
		shouldBreak = false;
		String test = "";
		String aktunh = StringTools.fuelleMitZeichen(Integer.toString(aktUnh), "0", true, 5);
		buf301Body.append("UNH+"+aktunh+"+MEDR02:D:01A:KR:97B'"+NEWLINE);zeilen++;
		buf301Body.append("BGM+06++10'"+NEWLINE);zeilen++; 
		buf301Body.append("DTM+137:"+DATUM10+":102'"+NEWLINE);zeilen++;
		buf301Body.append("RFF+ACD:"+StringTools.fuelleMitZeichen(Integer.toString(this.lfdNr), "0", true, 2)+"'"+NEWLINE);zeilen++; //Hier die Datenbank untersuchen
		//buf301Body.append("RFF+ACD:01'"+NEWLINE);zeilen++; //Hier die Datenbank untersuchen
		buf301Body.append("PNA+MS++"+vecdta.get(0).get(4).toString()+EOL+NEWLINE);zeilen++;
		buf301Body.append("PNA+MR++"+(EMPFAENGERIK = vecdta.get(0).get(3).toString())+EOL+NEWLINE);zeilen++;
		buf301Body.append("PNA+BY++"+(KOSTENTRAEGER = vecdta.get(0).get(6).toString())+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("CTA+BEA+","CTA+BEA+")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("RFF+FI:","RFF+FI:")+EOL+NEWLINE);zeilen++;
		buf301Body.append("PNA+MT++"+vecdta.get(0).get(4).toString()+EOL+NEWLINE);zeilen++; //Hier die div. IK's einbauen
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
		if( ! (test =  springeAufUndHole("PNA+BM+","RFF+ALX:")).equals("")){
			buf301Body.append(test+EOL+NEWLINE);zeilen++;
		}
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
		buf301Body.append("IMD+++"+Dta301CodeListen.getCodeListe("B08")[ubgrund][0]+":B08"+EOL+NEWLINE);zeilen++;
		if(ubart==0){
			buf301Body.append("DTM+158:"+mache10erDatum(beginnDatum)+":102"+EOL+NEWLINE);zeilen++;
			fallart = "4";
		}else if(ubart==1){
			buf301Body.append("DTM+159:"+mache10erDatum(endeDatum)+":102"+EOL+NEWLINE);zeilen++;
			fallart = "15";
		}else if(ubart==2){
			buf301Body.append("DTM+324:"+
					mache10erDatum(beginnDatum)+
					mache10erDatum(endeDatum)+":711"+EOL+NEWLINE);zeilen++;
			fallart = "16";		
		}
		if(!hinweis.equals("")){
			Vector<String> flvec = new Vector<String>();
			test = StringTools.do301String(hinweis);
			flvec = StringTools.fliessTextZerhacken(test, 70, "\n");
			for(int i = 0; i < flvec.size();i++){
				buf301Body.append("FTX+TXT+++B:"+flvec.get(i)+EOL+NEWLINE);zeilen++;	
			}
		}
		buf301Body.append("UNT+"+
				StringTools.fuelleMitZeichen(Integer.toString(zeilen),"0",true,5)+"+"+aktunh+
				EOL+NEWLINE);zeilen++;
		doKopfDaten();
		doFussDaten();
		gesamtbuf.append(buf301Header.toString());
		gesamtbuf.append(buf301Body.toString());
		gesamtbuf.append(buf301Footer.toString());
		intAktEREH = SqlInfo.erzeugeNummerMitMax("esol", 999);
		strAktEREH = "0"+StringTools.fuelleMitZeichen(Integer.toString(intAktEREH), "0", true, 3);
		anzahlUnhs++;
		if(doKeyStoreAktion(true)){
			//Mail Versenden
			//In neue Tabelle schreiben
			String cmd = "insert into dtafall set nachrichttyp='"+fallart+"', nachrichtart='"+fallart+"', pat_intern='"+
			vecdta.get(0).get(1).toString()+"', rez_nr='"+vecdta.get(0).get(2).toString()+"', "+
			"nachrichtdatum='"+DatFunk.sDatInSQL(DatFunk.sHeute())+"', nachrichtorg='"+
			StringTools.EscapedDouble(gesamtbuf.toString())+"',"+
			"nachrichtauf='"+
			StringTools.Escaped(auftragsBuf.toString())+"', bearbeiter='"+Reha.aktUser+"', "+
			"esolname='"+(imtest ? "T" : "E")+"REH"+strAktEREH+"', "+
			"icr='"+LFDNUMMER+"'";
			SqlInfo.sqlAusfuehren(cmd);
			return true;
		}
		return false;
	}
	public boolean doEntlassung(String erstDatum,String letztDatum,String uhrZeit,
			int arbeitsfaehig,int entlassform,boolean mitfahrgeld,String fahrgeld,String hinweis,
			boolean nurfahrgeld){
		if(!nurfahrgeld){
			holeVector();
			int zeilen = 1;
			shouldBreak = false;
			String test = "";
			String aktunh = StringTools.fuelleMitZeichen(Integer.toString(aktUnh), "0", true, 5);
			
			buf301Body.append("UNH+"+aktunh+"+MEDR03:D:08A:KR:97B'"+NEWLINE);zeilen++;
			buf301Body.append("BGM+04++10'"+NEWLINE);zeilen++; 
			buf301Body.append("DTM+137:"+DATUM10+":102'"+NEWLINE);zeilen++;
			buf301Body.append("RFF+ACD:01'"+NEWLINE);zeilen++; //Hier die Datenbank untersuchen
			//buf301Body.append("PNA+MS++"+Reha.aktIK+EOL+NEWLINE);zeilen++;
			buf301Body.append("PNA+MS++"+vecdta.get(0).get(4).toString()+EOL+NEWLINE);zeilen++;
			buf301Body.append("PNA+MR++"+(EMPFAENGERIK = vecdta.get(0).get(3).toString())+EOL+NEWLINE);zeilen++;
			buf301Body.append("PNA+BY++"+(KOSTENTRAEGER = vecdta.get(0).get(6).toString())+EOL+NEWLINE);zeilen++;
			buf301Body.append(springeAufUndHole("CTA+BEA+","CTA+BEA+")+EOL+NEWLINE);zeilen++;
			buf301Body.append(springeAufUndHole("RFF+FI:","RFF+FI:")+EOL+NEWLINE);zeilen++;
			buf301Body.append("PNA+MT++"+vecdta.get(0).get(4).toString()+EOL+NEWLINE);zeilen++; //Hier die div. IK's einbauen
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
			buf301Body.append("FCA+MD'"+NEWLINE);zeilen++;
			
			buf301Body.append(springeAufUndHole("PNA+BM+","PNA+BM+")+EOL+NEWLINE);zeilen++;
			buf301Body.append(springeAufUndHole("PNA+BM+","RFF+AGU:")+EOL+NEWLINE);zeilen++;
			buf301Body.append(springeAufUndHole("PNA+BM+","RFF+AGF:")+EOL+NEWLINE);zeilen++;
			buf301Body.append(springeAufUndHole("PNA+BM+","RFF+ADE:")+EOL+NEWLINE);zeilen++;
			buf301Body.append(springeAufUndHole("PNA+BM+","RFF+AEN:")+EOL+NEWLINE);zeilen++;
			if( ! (test =  springeAufUndHole("PNA+BM+","RFF+ALX:")).equals("")){
				buf301Body.append(test+EOL+NEWLINE);zeilen++;
			}
			buf301Body.append(springeAufUndHole("PNA+BM+","DTM+329:")+EOL+NEWLINE);zeilen++;
			buf301Body.append("PRC+ETL'"+NEWLINE);zeilen++;
			buf301Body.append("IMD+++"+Dta301CodeListen.getCodeListe("B02")[arbeitsfaehig][0]+":B02"+EOL+NEWLINE);zeilen++;
			buf301Body.append("IMD+++"+Dta301CodeListen.getCodeListe("B07")[entlassform][0]+":B07"+EOL+NEWLINE);zeilen++;
			//buf301Body.append(springeAufUndHole("PNA+BM+","CIN+").replace(":I0R",":10R")+EOL+NEWLINE);zeilen++;
			String cin = springeAufUndHole("PNA+BM+","CIN+").replace(":I0R",":10R");
			buf301Body.append(cin+EOL+NEWLINE);zeilen++;
			while( ! (cin=springeAufUndHoleNaechsten(cin,"CIN+").replace(":I0R",":10R")).equals("") ){
				buf301Body.append(cin+EOL+NEWLINE);zeilen++;
			}

			buf301Body.append("DTM+194:"+mache10erDatum(erstDatum)+":102"+EOL+NEWLINE);zeilen++;
			buf301Body.append("DTM+293:"+mache10erDatum(letztDatum)+":102"+EOL+NEWLINE);zeilen++;
			buf301Body.append("DTM+96:"+uhrZeit+":401"+EOL+NEWLINE);zeilen++;
			buf301Body.append("UNT+"+
					StringTools.fuelleMitZeichen(Integer.toString(zeilen),"0",true,5)+"+"+aktunh+
					EOL+NEWLINE);zeilen++;

			/**************Zusätzliche Fahrgeldrechnung erstellen*************/

			if(mitfahrgeld){
				anzahlUnhs++;
				aktUnh++;
				aktunh = StringTools.fuelleMitZeichen(Integer.toString(aktUnh),"0",true,5);
				zeilen=1;
				zeilen = doRechnungKopf(zeilen,fahrgeld,hinweis,
				mache10erDatum(erstDatum),mache10erDatum(letztDatum),true,9999);
				zeilen = doFahrgeld(zeilen,fahrgeld,hinweis,
				mache10erDatum(erstDatum),mache10erDatum(letztDatum),true);
				buf301Body.append("UNT+"+
				StringTools.fuelleMitZeichen(Integer.toString(zeilen),"0",true,5)+"+"+aktunh+
				EOL+NEWLINE);zeilen++;
			}
		}else{
			//nur fahrgeld
			holeVector();
			int zeilen = 1;
			shouldBreak = false;
			String aktunh = StringTools.fuelleMitZeichen(Integer.toString(aktUnh), "0", true, 5);
			zeilen = doRechnungKopf(zeilen,fahrgeld,hinweis,
					mache10erDatum(erstDatum),mache10erDatum(letztDatum),true,9999);
			zeilen = doFahrgeld(zeilen,fahrgeld,hinweis,
					mache10erDatum(erstDatum),mache10erDatum(letztDatum),true);
			buf301Body.append("UNT+"+
			StringTools.fuelleMitZeichen(Integer.toString(zeilen),"0",true,5)+"+"+aktunh+
			EOL+NEWLINE);zeilen++;
		}
		doKopfDaten();
		doFussDaten();

		gesamtbuf.append(buf301Header.toString());
		gesamtbuf.append(buf301Body.toString());
		gesamtbuf.append(buf301Footer.toString());
		intAktEREH = SqlInfo.erzeugeNummerMitMax("esol", 999);
		strAktEREH = "0"+StringTools.fuelleMitZeichen(Integer.toString(intAktEREH), "0", true, 3);

		/**************Zusätzliche Fahrgeldrechnung erstellen*************/
		if(doKeyStoreAktion(true)){
			//Mail Versenden
			//In neue Tabelle schreiben
			String typus = (mitfahrgeld ? "12" : "6");
			typus = (nurfahrgeld ? "13" : typus);
			String cmd = "insert into dtafall set nachrichttyp='"+typus+"', nachrichtart='"+typus+"', pat_intern='"+
			vecdta.get(0).get(1).toString()+"', rez_nr='"+vecdta.get(0).get(2).toString()+"', "+
			"nachrichtdatum='"+DatFunk.sDatInSQL(DatFunk.sHeute())+"', nachrichtorg='"+
			StringTools.EscapedDouble(gesamtbuf.toString())+"',"+
			"nachrichtauf='"+
			StringTools.Escaped(auftragsBuf.toString())+"', bearbeiter='"+Reha.aktUser+"', "+
			"esolname='"+(imtest ? "T" : "E")+"REH"+strAktEREH+"', "+
			"icr='"+LFDNUMMER+"'";
			SqlInfo.sqlAusfuehren(cmd);
			return true;
		}
		return false;
	}
	private int doRechnungKopf(int zeilen,String gesamt,String hinweis,
			String aufnahmedatum,String entlassdatum,boolean beientlass,int xrnummer){
		
		String test = "";
		String aktunh = StringTools.fuelleMitZeichen(Integer.toString(aktUnh), "0", true, 5);
		buf301Body.append("UNH+"+aktunh+"+MEDR04:D:01A:KR:97B'"+NEWLINE);zeilen++;
		buf301Body.append("BGM+30++10'"+NEWLINE);zeilen++; 
		buf301Body.append("DTM+137:"+DATUM10+":102'"+NEWLINE);zeilen++;
		buf301Body.append("DTM+3:"+DATUM10+":102'"+NEWLINE);zeilen++;
		buf301Body.append("RFF+ACD:"+StringTools.fuelleMitZeichen(Integer.toString(this.lfdNr), "0", true, 2)+"'"+NEWLINE);zeilen++; //Hier die Datenbank untersuchen
		//buf301Body.append("RFF+ACD:01'"+NEWLINE);zeilen++; //Hier die Datenbank untersuchen
		//Rechnungsnummer im RTA
		//int rnummer = SqlInfo.erzeugeNummer("rnr");
		buf301Body.append("RFF+CKN:"+Integer.toString(xrnummer)+EOL+NEWLINE);zeilen++; //Hier die Datenbank untersuchen
		buf301Body.append("MOA+39:"+gesamt+":EUR"+EOL+NEWLINE);zeilen++;
		buf301Body.append("PNA+MS++"+vecdta.get(0).get(4).toString()+EOL+NEWLINE);zeilen++;
		buf301Body.append("PNA+MR++"+(EMPFAENGERIK = vecdta.get(0).get(3).toString())+EOL+NEWLINE);zeilen++;
		buf301Body.append("PNA+BY++"+(KOSTENTRAEGER = vecdta.get(0).get(6).toString())+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("CTA+BEA+","CTA+BEA+")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("RFF+FI:","RFF+FI:")+EOL+NEWLINE);zeilen++;
		buf301Body.append("PNA+MT++"+vecdta.get(0).get(4).toString()+EOL+NEWLINE);zeilen++; //Hier die div. IK's einbauen
		buf301Body.append("CTA+ABT+2300"+EOL+NEWLINE);zeilen++; //hier nach Inikationsgruppen untersuchen
		buf301Body.append("RFF+AES:"+(REHANUMMER =vecdta.get(0).get(2).toString()) +EOL+NEWLINE);zeilen++;
		buf301Body.append("RFF+AEP:"+Integer.toString(xrnummer) +EOL+NEWLINE);zeilen++;
		buf301Body.append("FII+BF+"+vecdta.get(0).get(4).toString()+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("AGR+BY:","AGR+BY:")+EOL+NEWLINE);zeilen++;
		buf301Body.append("FCA+RD'"+NEWLINE);zeilen++;
		// Wirklich Schlußrechnung wenn mit Entlassungsmitteilung erzeugt????
		buf301Body.append("GIS+02:RA'"+NEWLINE);zeilen++;
		buf301Body.append("DTM+194:"+aufnahmedatum+":102'"+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("PNA+BM+","PNA+BM+")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("PNA+BM+","RFF+AGU:")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("PNA+BM+","RFF+AGF:")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("PNA+BM+","RFF+ADE:")+EOL+NEWLINE);zeilen++;
		buf301Body.append(springeAufUndHole("PNA+BM+","RFF+AEN:")+EOL+NEWLINE);zeilen++;
		if( ! (test =  springeAufUndHole("PNA+BM+","RFF+ALX:")).equals("")){
			buf301Body.append(test+EOL+NEWLINE);zeilen++;
		}
		buf301Body.append(springeAufUndHole("PNA+BM+","DTM+329:")+EOL+NEWLINE);zeilen++;
		buf301Body.append("PRC+ETG'"+NEWLINE);zeilen++;
		return zeilen;
		
	}
	public int doFahrgeld(int zeilen,String gesamt,String hinweis,
			String aufnahmedatum,String entlassdatum,boolean beientlass){
	
		String verfahren = untersucheCodeListe(":A12",2);
		buf301Body.append("CLI+ENT+"+verfahren+"050299"+":ENT'"+NEWLINE);zeilen++;
		buf301Body.append("DTM+263:"+aufnahmedatum+entlassdatum+":711'"+NEWLINE);zeilen++;
		buf301Body.append("QTY+47:1:ANZ'"+NEWLINE);zeilen++;
		buf301Body.append("QTY+193:0:ANZ'"+NEWLINE);zeilen++;
		buf301Body.append("MOA+146:"+gesamt+":EUR'"+NEWLINE);zeilen++;
		return zeilen;
	}
	private int doPositionen(int zeilen,Object[] obj,String aufnahmedatum,String entlassdatum){
		//1+2 = Verfahrensart med oder AHB
		//3 = Art der Versorgung (ganztägig ambulant)
		//3 - 85,39 - LVA-MED
		//1 - 25,20 - AHBBefund
		//2 - 12,50 - Fako2  = Taxi
		//1 - 2,50 - Fako1 = Fahrtkosten

		String[] positionen = {"Fako1","Fako2","AHBBefund"}; 
		List<String> list1 = Arrays.asList(positionen);

		String[] schluessel = {"50299","50270","50110","70501"}; 

		String clisegment = "";
		int pos = -1;
		if( (pos=list1.indexOf(((String)obj[2]).trim()) ) < 0){
			return zeilen;
			//clisegment = schluessel[3];
		}else{
			clisegment = schluessel[pos];
		}
		System.out.println((String)obj[2]);
		String verfahren = untersucheCodeListe(":A12",2);
		String versorgungsart = (clisegment.startsWith("5") ? "0" :untersucheCodeListe(":B11",3)); 
		buf301Body.append("CLI+ENT+"+verfahren+versorgungsart+clisegment+":ENT'"+NEWLINE);zeilen++;
		buf301Body.append("DTM+263:"+aufnahmedatum+entlassdatum+":711'"+NEWLINE);zeilen++;
		buf301Body.append("QTY+47:"+obj[0]+":ANZ'"+NEWLINE);zeilen++;
		buf301Body.append("QTY+193:0:ANZ'"+NEWLINE);zeilen++;
		buf301Body.append("MOA+146:"+obj[1]+":EUR'"+NEWLINE);zeilen++;
		return zeilen;
	}
	public boolean doRechnung(String erstDatum,String letztDatum,
			Vector<Object[]> vecobj, String gesamt,int rnr){
			if(erstDatum.trim().length() < 10 || letztDatum.trim().length() < 10){
				JOptionPane.showMessageDialog(null,"Fehler in der Abrechnung nach DTA-301, Datumswerte sind nicht korrekt");
				return false;
			}
			String aktunh = StringTools.fuelleMitZeichen(Integer.toString(aktUnh), "0", true, 5);
			holeVector();
			int zeilen = 1;
			shouldBreak = false;
//			private int doRechnungKopf(int zeilen,String gesamt,String hinweis,
//			String aufnahmedatum,String entlassdatum,boolean beientlass){
			/******
			 * Hier müssen noch die Tagessätze aus "gesamt" abgezogen werden 
			 ******/
			zeilen = doRechnungKopf(zeilen, gesamt,"",mache10erDatum(erstDatum),mache10erDatum(letztDatum),false,rnr);
			for(int i = 0; i < vecobj.size(); i++){
				zeilen = doPositionen(zeilen,(Object[])vecobj.get(i),mache10erDatum(erstDatum),mache10erDatum(letztDatum));
			}
			System.out.println(buf301Body.toString());
			
			buf301Body.append("UNT+"+
					StringTools.fuelleMitZeichen(Integer.toString(zeilen),"0",true,5)+"+"+aktunh+
					EOL+NEWLINE);zeilen++;			
			doKopfDaten();
			doFussDaten();

			gesamtbuf.append(buf301Header.toString());
			gesamtbuf.append(buf301Body.toString());
			gesamtbuf.append(buf301Footer.toString());
			intAktEREH = SqlInfo.erzeugeNummerMitMax("esol", 999);
			strAktEREH = "0"+StringTools.fuelleMitZeichen(Integer.toString(intAktEREH), "0", true, 3);

			if(doKeyStoreAktion(true)){
				//Mail Versenden
				//In neue Tabelle schreiben
				String typus = "8";
				String cmd = "insert into dtafall set nachrichttyp='"+typus+"', nachrichtart='"+typus+"', pat_intern='"+
				vecdta.get(0).get(1).toString()+"', rez_nr='"+vecdta.get(0).get(2).toString()+"', "+
				"nachrichtdatum='"+DatFunk.sDatInSQL(DatFunk.sHeute())+"', nachrichtorg='"+
				StringTools.EscapedDouble(gesamtbuf.toString())+"',"+
				"nachrichtauf='"+
				StringTools.Escaped(auftragsBuf.toString())+"', bearbeiter='"+Reha.aktUser+"', "+
				"esolname='"+(imtest ? "T" : "E")+"REH"+strAktEREH+"', "+
				"icr='"+LFDNUMMER+"'";
				SqlInfo.sqlAusfuehren(cmd);
				return true;
			}
			return false;
			
			
	}
	private boolean doKeyStoreAktion(boolean mitemail){
		try {
			originalSize =gesamtbuf.length();
			doDateiErstellen(0);
			int frage1 = JOptionPane.showConfirmDialog(null, "Die Nachricht --> "+strAktEREH+" <-- auf folgende IK verschlüsseln?\n\n"+EMPFAENGERIK+"\n\n","Achtun wichtige Anfrage",JOptionPane.YES_NO_OPTION);
			if(frage1 != JOptionPane.YES_OPTION){
				return false;
			}
			String keystore = Reha.proghome+"keystore/"+Reha.aktIK+"/"+Reha.aktIK+".p12";
			NebraskaKeystore store = new NebraskaKeystore(keystore, SystemConfig.hmAbrechnung.get("hmkeystorepw"),"123456", Reha.aktIK);
			NebraskaEncryptor encryptor = store.getEncryptor(EMPFAENGERIK);
			String inFile = (SystemConfig.dta301OutBox+(imtest ? "T" : "E")+"REH"+strAktEREH+".ORG").toLowerCase();
			long size = encryptor.encrypt(inFile, inFile.replace(".org", ""));
			encryptedSize = Integer.parseInt(Long.toString(size));
			//System.out.println("       Originalgröße = "+originalSize);
			//System.out.println("Verschlüsselte Größe = "+encryptedSize);
			doAuftragsDatei();
			doDateiErstellen(1);
			//Hier Die Email
			try{
				String ik_email = SqlInfo.holeEinzelFeld("select email1 from kass_adr where ik_kasse='"+EMPFAENGERIK+"' LIMIT 1");
				if( ik_email.equals("") ){
					JOptionPane.showMessageDialog(null,"Dem Empfänger "+EMPFAENGERIK+" wurde keine Emailadresse zugewiesen!");
					return false;
				}
				String smtphost = SystemConfig.hmEmailExtern.get("SmtpHost");
				String authent = SystemConfig.hmEmailExtern.get("SmtpAuth");
				//String benutzer = SystemConfig.hmEmailExtern.get("Username") ;				
				//String pass1 = SystemConfig.hmEmailExtern.get("Password");
				String benutzer = "dta301@rta.de";
				String pass1 = "dta301rta";
				//String sender = SystemConfig.hmEmailExtern.get("SenderAdresse"); 
				
				String recipient = ik_email;
				
				//String recipient = SystemConfig.hmEmailExtern.get("SenderAdresse");
				
				//String recipient = (imtest ? "" : ik_email+",") +SystemConfig.hmEmailExtern.get("SenderAdresse");
				if(!mitemail){
					return true;
				}
				int frage = JOptionPane.showConfirmDialog(null, "Ist die unten angegebene Emailadresse korrekt?\n\n"+recipient+"\n","Achtung wichtige Benutzeranfrage",JOptionPane.YES_NO_OPTION);
				if(frage != JOptionPane.YES_OPTION){
					return true;
				}

				String text = "";
				boolean authx = (authent.equals("0") ? false : true);
				boolean bestaetigen = false;
				String endfile = inFile.substring(inFile.lastIndexOf("/")+1).trim();
				//System.out.println("Endflile = "+endfile);
				String[] aufDat = {inFile.replace(".org", ".auf"),endfile.replace(".org", ".auf") };
				String[] encodedDat = {inFile.replace(".org", ""),endfile.replace(".org", "")};
				ArrayList<String[]> attachments = new ArrayList<String[]>();
				attachments.add(encodedDat);
				attachments.add(aufDat);
				
				//System.out.println("Sender-IK = "+vecdta.get(0).get(4).toString());
				EmailSendenExtern oMail = new EmailSendenExtern();
				try{
					oMail.sendMail(smtphost, benutzer, pass1, benutzer, recipient, vecdta.get(0).get(4).toString(), text,attachments,authx,bestaetigen);
					oMail = null;
					return true;
				}catch(Exception e){
					e.printStackTrace( );
					JOptionPane.showMessageDialog(null, "Emailversand fehlgeschlagen\n\n"+
		        			"Mögliche Ursachen:\n"+
		        			"- falsche Angaben zu Ihrem Emailpostfach und/oder dem Provider\n"+
		        			"- Sie haben keinen Kontakt zum Internet\n\n"+
		        			"Bitte senden Sie die Datei --> "+encodedDat[1]+" <-- manuell");
					return true;
				}


			}catch(Exception ex){
					ex.printStackTrace();
					return false;
			}
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
	
	public String normalizeString(String in){
		return in.replace(",", "?,").replace(":", "?:");
	}
	private void doDateiErstellen(int art) throws IOException{
		File f = null;
		FileWriter fw = null;
		BufferedWriter bw = null;
		if(art == 0){
			f = new File( (SystemConfig.dta301OutBox+(imtest ? "T" : "E")+"REH"+strAktEREH+".ORG").toLowerCase() );
			fw = new FileWriter(f);
		    bw = new BufferedWriter(fw); 
		    bw.write(gesamtbuf.toString()); 
		    bw.flush();
		    fw.flush();
		    fw.close();
		    bw.close();
		    return;
		}else if(art == 1){
			f = new File( (SystemConfig.dta301OutBox+(imtest ? "T" : "E")+"REH"+strAktEREH+".AUF").toLowerCase() );
			fw = new FileWriter(f);
		    bw = new BufferedWriter(fw); 
		    bw.write(auftragsBuf.toString()); 
		    bw.flush();
		    fw.flush();
		    fw.close();
		    bw.close();
		    return;
		}
		
	}
	public void doDateiErstellenFrei(String datname,String in) throws IOException{
		File f = null;
		FileWriter fw = null;
		BufferedWriter bw = null;
		f = new File( datname );
		fw = new FileWriter(f);
	    bw = new BufferedWriter(fw); 
	    bw.write(in); 
	    bw.flush();
	    fw.flush();
	    fw.close();
	}

	private void doKopfDaten(){
		buf301Header.append(UNA+DOPPELPUNKT+PLUS+STARTCHARS+EOL+NEWLINE);
		buf301Header.append(UNB+PLUS+"UNOC:3"+PLUS+vecdta.get(0).get(4).toString()+PLUS+EMPFAENGERIK+
				PLUS+DATUM8+DOPPELPUNKT+UHRZEIT4+PLUS+
				LFDNUMMER+PLUS+PLUS+"REH"+LFDNUMMER+EOL+NEWLINE);
	}
	private void doFussDaten(){
		String unhs = StringTools.fuelleMitZeichen(Integer.toString(anzahlUnhs), "0", true, 5);
		buf301Footer.append("UNZ+"+unhs+"+"+LFDNUMMER+EOL);
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
		String[] data = vecdta.get(0).get(29).split("\n");
		for(int i = 0; i < data.length;i++){
			originaldata.add(String.valueOf(data[i]).replace("\n", "").replace("\r", ""));
		}
	}
	private String springeAufUndHole(String springeauf,String hole){
		String ret = "";
		for(int i = 0; i < originaldata.size();i++){
			if(originaldata.get(i).startsWith(springeauf)){
				for(int x = i; x < originaldata.size();x++){
					if(originaldata.get(x).startsWith(hole)){
						lastCIN = Integer.valueOf(x);
						return String.valueOf(originaldata.get(x).toString().replace("\n","").replace("\r",""));
					}
				}
			}
		}
		return ret;
	}
	private String springeAufUndHoleNaechsten(String springeauf,String hole){
		String ret = "";
		if(originaldata.size() > (lastCIN+1)){
			if(originaldata.get(lastCIN+1).startsWith(hole)){
				lastCIN += 1;
				return String.valueOf(originaldata.get(lastCIN).toString().replace("\n","").replace("\r",""));
			}else{
				return ret;
			}
		}
		return ret;
	}

	private String untersucheCodeListe(String codeliste,int element){
		//Z.B. :A12
		String ret = "";
		for(int i = 0; i < originaldata.size();i++){
			if(originaldata.get(i).endsWith(codeliste)){
				String[] sret = originaldata.get(i).split("\\+");
				return String.valueOf(sret[element].split(":")[0]);
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
		auftragsBuf.setLength(0);
		auftragsBuf.trimToSize();
		String abrDateiName="REH-RTA    ";
		auftragsBuf.append("500000"+"01"+"00000348"+"000");
		auftragsBuf.append((imtest ? "T" : "E")+"REH"+this.strAktEREH);
		auftragsBuf.append("     ");
		auftragsBuf.append(StringTools.fuelleMitZeichen(vecdta.get(0).get(4).toString(), " ", false, 15));
		auftragsBuf.append(StringTools.fuelleMitZeichen(vecdta.get(0).get(4).toString(), " ", false, 15));
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
		auftragsBuf.append("U");
		auftragsBuf.append("  ");
		auftragsBuf.append(StringTools.fuelleMitZeichen("0", "0", true, 5) );
		auftragsBuf.append(StringTools.fuelleMitZeichen("0", "0", true, 8) );
		auftragsBuf.append("0");
		auftragsBuf.append("00");
		auftragsBuf.append("5");
		auftragsBuf.append(StringTools.fuelleMitZeichen("0", "0", true, 10) );
		auftragsBuf.append(StringTools.fuelleMitZeichen("0", "0", true, 6) );
		auftragsBuf.append(StringTools.fuelleMitZeichen(" ", " ", true, 28) );
		auftragsBuf.append(StringTools.fuelleMitZeichen(" ", " ", true, 44) );
		auftragsBuf.append(StringTools.fuelleMitZeichen(" ", " ", true, 30) );
	}
	
	private String getSMLResult(EBerichtPanel epanel,int i){
		String ret = "";
		switch(i){
		case 1: //Stehen
			if(epanel.bchb[24].isSelected()){return "2";}
			if(epanel.bchb[25].isSelected()){return "3";}
			if(epanel.bchb[26].isSelected()){return "4";}
			JOptionPane.showMessageDialog(null,"Angaben zur Arbeitshaltung fehlerhaft oder unvollständig");
			shouldBreak = true;
			return "1";
		case 2: //Sitzen
			if(epanel.bchb[30].isSelected()){return "2";}
			if(epanel.bchb[31].isSelected()){return "3";}
			if(epanel.bchb[32].isSelected()){return "4";}
			JOptionPane.showMessageDialog(null,"Angaben zur Arbeitshaltung fehlerhaft oder unvollständig");
			shouldBreak = true;
			return "1";
		case 3: //Gehen
			if(epanel.bchb[27].isSelected()){return "2";}
			if(epanel.bchb[28].isSelected()){return "3";}
			if(epanel.bchb[29].isSelected()){return "4";}
			JOptionPane.showMessageDialog(null,"Angaben zur Arbeitshaltung fehlerhaft oder unvollständig");
			shouldBreak = true;
			return "1";
		case 4: //Arbeitsschwere
			if(epanel.bchb[20].isSelected()){return "2";}
			if(epanel.bchb[21].isSelected()){return "3";}
			if(epanel.bchb[22].isSelected()){return "4";}
			if(epanel.bchb[23].isSelected()){return "5";}
			JOptionPane.showMessageDialog(null,"Angaben zur Arbeitsschwere fehlerhaft oder unvollständig");
			shouldBreak = true;
			return "1";
		case 5: //Leistungsfähigk im zuletzt ausgeübten Beruf			
			if(epanel.bchb[17].isSelected()){return "5";}
			if(epanel.bchb[18].isSelected()){return "6";}
			if(epanel.bchb[19].isSelected()){return "7";}
			JOptionPane.showMessageDialog(null,"Angaben zur Leistungsfähigkeit (letzte Tätigkeit) fehlerhaft oder unvollständig");
			shouldBreak = true;
			return "9";
		case 6: //Leistungsfähigk auf d. allgem. Arbeitsmarkt			
			if(epanel.bchb[41].isSelected()){return "5";}
			if(epanel.bchb[42].isSelected()){return "6";}
			if(epanel.bchb[43].isSelected()){return "7";}
			JOptionPane.showMessageDialog(null,"Angaben zur Leistungsfähigkeit (allgem. Arbeitsmarkt) fehlerhaft oder unvollständig");
			shouldBreak = true;
			return "9";
		case 7: //Tagesschicht
			if(epanel.bchb[33].isSelected()){return "J";}
			return "N";
		case 8: //Früh-/Spätschicht
			if(epanel.bchb[34].isSelected()){return "J";}
			return "N";
		case 9: //Nachtschicht
			if(epanel.bchb[35].isSelected()){return "J";}
			return "N";
		case 10: //Keine wesentlichen Einschränkungen
			if(epanel.bchb[36].isSelected() && (!epanel.bta[7].getText().trim().equals(""))){
				JOptionPane.showMessageDialog(null,"Keine Einschränkung angekreuzt aber Einschränkungen im Text benannt -> nicht möglch");
				shouldBreak = true;
				return "J";
			}
			if(epanel.bchb[36].isSelected()){return "J";}
			return "N";
		case 11: //Balastbarkeit (geistig/psychisch)
			if(epanel.bchb[37].isSelected() && (epanel.bta[7].getText().trim().equals(""))){
				JOptionPane.showMessageDialog(null,"174 angekreuzt aber Einschränkungen im Text nicht benannt -> nicht möglch");
				shouldBreak = true;
				return "J";
			}
			if(epanel.bchb[37].isSelected()){return "J";}
			return "N";
		case 12: //Sinnesorgane
			if(epanel.bchb[38].isSelected() && (epanel.bta[7].getText().trim().equals(""))){
				JOptionPane.showMessageDialog(null,"175 angekreuzt aber Einschränkungen im Text nicht benannt -> nicht möglch");
				shouldBreak = true;
				return "J";
			}
			if(epanel.bchb[38].isSelected()){return "J";}
			return "N";
		case 13: //Bewegungs-/Haltungsapparat
			if(epanel.bchb[39].isSelected() && (epanel.bta[7].getText().trim().equals(""))){
				JOptionPane.showMessageDialog(null,"176 angekreuzt aber Einschränkungen im Text nicht benannt -> nicht möglch");
				shouldBreak = true;
				return "J";
			}
			if(epanel.bchb[39].isSelected()){return "J";}
			return "N";
		case 14: //Gefährdungs- und Belastungsfaktoren
			if(epanel.bchb[40].isSelected() && (epanel.bta[7].getText().trim().equals(""))){
				JOptionPane.showMessageDialog(null,"177 angekreuzt aber Einschränkungen im Text nicht benannt -> nicht möglch");
				shouldBreak = true;
				return "J";
			}
			if(epanel.bchb[40].isSelected()){return "J";}
			return "N";

		}
		
		return ret;
	}
	
}
