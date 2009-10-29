package stammDatenTools;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.JOptionPane;

import patientenFenster.AktuelleRezepte;
import patientenFenster.PatGrundPanel;

import sqlTools.ExUndHop;
import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import terminKalender.DatFunk;

public class ZuzahlTools {
	public static boolean zzStatusEdit(String pat_int,String geboren, String rez_nr,String frei, String kassid){
		String preisgrp = "";
		String zzid = "";
		String rez_geb = "";
		int zzregel = -1; 
		if(kassid.equals("-1")){
			JOptionPane.showMessageDialog(null,"Keine gültige Kasse angegeben");
			return false;
		}
		if(rez_nr.equals("")){
			Vector vec = SqlInfo.holeFelder("select rez_nr,kid,rez_geb,befr,id from verordn where pat_intern='"+pat_int+"'");
			for(int i = 0;i < vec.size(); i++){
				zzregel = getZuzahlRegel((String)((Vector)vec.get(i)).get(1));
				zzid = (String)((Vector)vec.get(i)).get(4);
				rez_geb = (String)((Vector)vec.get(i)).get(2);
				System.out.println("Rezeptnummer = "+((Vector)vec.get(i)).get(0)+" Zuzahlregel = "+zzregel);
				if(zzregel > 0 && rez_geb.equals("0.00")){
					if(frei.equals("F")){
						SqlInfo.aktualisiereSaetze("verordn", "befr='F',zzstatus='2'", "id='"+zzid+"'");
					}else{
						SqlInfo.aktualisiereSaetze("verordn", "befr='T',zzstatus='0'", "id='"+zzid+"'");
					}
				}else if(zzregel > 0 && (!rez_geb.equals("0.00"))){
					if(frei.equals("F")){
						SqlInfo.aktualisiereSaetze("verordn", "befr='F',zzstatus='1'", "id='"+zzid+"'");
					}
				}
			}
			return true;
		}else{
			//Wenn Rezeptnummer nicht leer, dann kommt der Aufruf aus Rezept!!!
		}
		return false;
	}
	/**********************************************************/
	public static Object[] unter18TestDirekt(Vector<String> termine,boolean azTest,boolean jahrTest){
						// Rez geb fällig   //Anzahl Term   //Anzahl frei  //Anzahl unfrei  //Zuzahlstatus
		Object[] ret = {new Boolean(false),new Integer(-1),new Integer(-1),new Integer(-1),new Integer(-1)};
		//Vector vec = SqlInfo.holeFelder("select termine,id,pat_intern,jahrfrei,unter18,zzregel,zzstatus from verordn where rez_nr='"+rez_nr+"' LIMIT 1");
		Vector<String> tage  = (Vector<String>)termine.clone();
		if(tage.size()==0){
			return ret;
		}
		Comparator comparator = new Comparator<String>() {
			public int compare(String s1, String s2) {
		        String strings1 = DatFunk.sDatInSQL(s1);
		        String strings2 = DatFunk.sDatInSQL(s2);
		        return strings1.compareTo(strings2);
		    }
		};	
		Collections.sort(tage,comparator);
		String rez_nr = (String) PatGrundPanel.thisClass.vecaktrez.get(1);
		String unter18 = (String) PatGrundPanel.thisClass.vecaktrez.get(60);
		String pat_int = (String) PatGrundPanel.thisClass.vecaktrez.get(0);
		String aktzzstatus = (String) PatGrundPanel.thisClass.vecaktrez.get(39);
		String aktzzregel = (String) PatGrundPanel.thisClass.vecaktrez.get(63);
		if(unter18.equals("T") && (!aktzzregel.equals("0"))){
			String stichtag = "";
			String geburtstag = DatFunk.sDatInDeutsch(PatGrundPanel.thisClass.patDaten.get(4));
			String gebtag = (DatFunk.sDatInDeutsch((String)PatGrundPanel.thisClass.vecaktrez.get(22))).substring(0,6)+new Integer(new Integer(SystemConfig.aktJahr)-18).toString();
			
			boolean einergroesser = false;
			int erstergroesser = -1; 
			for(int i = 0; i < tage.size();i++){
				stichtag = ((String)tage.get(i)).substring(0,6)+new Integer(new Integer(SystemConfig.aktJahr)-18).toString();
				if(DatFunk.TageDifferenz(geburtstag ,stichtag) >= 0 ){
					einergroesser = true;
					break;
				}
				//System.out.println("Differenz an Tagen zwischen Behandlung vom "+tage.get(i)+
						//" und dem Geburtstag "+geburtstag+" = "+
						//datFunk.TageDifferenz(geburtstag ,stichtag));
				
			}
			if( (aktzzstatus.equals("3") || aktzzstatus.equals("0") || aktzzstatus.equals("2"))&& einergroesser){
				//String cmd = "update verordn set zzstatus='2' where rez_nr='"+rez_nr+" LIMIT 1";
				//new ExUndHop().setzeStatement(cmd);
				SqlInfo.aktualisiereSaetze("verordn", "zzstatus='2'", "rez_nr='"+rez_nr+"' LIMIT 1");
				AktuelleRezepte.aktRez.setzeBild(AktuelleRezepte.aktRez.tabaktrez.getSelectedRow(),2);				
				ret[0] = new Boolean(true); 
				ret[1] = new Integer(tage.size());
				ret[2] = new Integer(erstergroesser-1);
				ret[3] = ((Integer)ret[1]) - (Integer)ret[2];
				ret[4] = new Integer(2);
				return ret.clone();
			}
			if( (aktzzstatus.equals("2") || aktzzstatus.equals("1")) && (!einergroesser)){
				//String cmd = "update verordn set zzstatus='3' where rez_nr='"+rez_nr+" LIMIT 1";
				//new ExUndHop().setzeStatement(cmd);
				long tagex = DatFunk.TageDifferenz(geburtstag ,gebtag);
				//System.out.println("Tagex = ---------------> "+tagex);
				if(tagex <= 0 && tagex > -45){
					//JOptionPane.showMessageDialog(null ,"Achtung es sind noch "+(tagex*-1)+" Tage bis zur Volljährigkeit\n"+
							//"Unter Umständen wechselt der Zuzahlungsstatus im Verlauf dieses Rezeptes");
					AktuelleRezepte.aktRez.setzeBild(AktuelleRezepte.aktRez.tabaktrez.getSelectedRow(),3);
					SqlInfo.aktualisiereSaetze("verordn", "zzstatus='3'", "rez_nr='"+rez_nr+"' LIMIT 1");
					ret[4] = new Integer(3);					
				}else{
					AktuelleRezepte.aktRez.setzeBild(AktuelleRezepte.aktRez.tabaktrez.getSelectedRow(),0);
					SqlInfo.aktualisiereSaetze("verordn", "zzstatus='0'", "rez_nr='"+rez_nr+"' LIMIT 1");
					ret[4] = new Integer(0);
				}
				ret[0] = Boolean.valueOf(false); 
				ret[1] = tage.size();
				return ret.clone();
				
			}
		}else if(unter18.equals("T") && (aktzzregel.equals("0"))){
			AktuelleRezepte.aktRez.setzeBild(AktuelleRezepte.aktRez.tabaktrez.getSelectedRow(),0);			
			ret[0] = Boolean.valueOf(false); 
			ret[1] = tage.size();
			ret[4] = new Integer(0);			
			
		}
		//AktuelleRezepte.aktRez.tabaktrez.validate();
		return ret.clone();
	}
	/********************************************************/

	/********************************************************/	
	public static Object[] unter18TestAllesSuchen(String rez_nr,boolean azTest,boolean jahrTest){

		Object[] ret = {new Boolean(false),new Integer(-1),new Integer(-1),new Integer(-1)};
		Vector vec = SqlInfo.holeFelder("select termine,id,pat_intern,jahrfrei,unter18,zzregel,zzstatus from verordn where rez_nr='"+rez_nr+"' LIMIT 1");
		Vector<String> tage  = RezTools.holeEinzelTermineAusRezept(null,(String)((Vector<String>)vec.get(0)).get(0));
		if(tage.size()==0){
			return ret;
		}
		Comparator comparator = new Comparator<String>() {
			public int compare(String s1, String s2) {
		        String strings1 = DatFunk.sDatInSQL(s1);
		        String strings2 = DatFunk.sDatInSQL(s2);
		        return strings1.compareTo(strings2);
		    }
		};	
		Collections.sort(tage,comparator);	
		String unter18 = (String)((Vector)vec.get(0)).get(4);
		String pat_int = (String)((Vector)vec.get(0)).get(2);
		String aktzzstatus = (String)((Vector)vec.get(0)).get(6);
		String aktzzregel = (String)((Vector)vec.get(0)).get(5);
		if(unter18.equals("T") && (!aktzzregel.equals("0"))){
			String stichtag = "";
			String geburtstag = DatFunk.sDatInDeutsch(SqlInfo.holePatFeld("geboren", "pat_intern='"+pat_int+"'"));
			boolean einergroesser = false;
			int erstergroesser = -1; 
			for(int i = 0; i < tage.size();i++){
				stichtag = ((String)tage.get(i)).substring(0,6)+new Integer(new Integer(SystemConfig.aktJahr)-18).toString();
				if(DatFunk.TageDifferenz(geburtstag ,stichtag) >= 0 ){
					einergroesser = true;
					break;
				}
				/*
				System.out.println("Differenz an Tagen zwischen Behandlung vom "+tage.get(i)+
						" und dem Geburtstag "+geburtstag+" = "+
						datFunk.TageDifferenz(geburtstag ,stichtag));
				*/		
			}
			if(aktzzstatus.equals("3") && einergroesser){
				String cmd = "update verordn set zzstatus='2' where rez_nr='"+rez_nr+" LIMIT 1";
				new ExUndHop().setzeStatement(cmd);
				ret[0] = Boolean.valueOf(true); 
				ret[1] = tage.size();
				ret[2] = Integer.toString(erstergroesser-1);
				ret[3] = ((Integer)ret[1]) - erstergroesser;
			}
			if( (aktzzstatus.equals("2") || aktzzstatus.equals("1")) && (!einergroesser)){
				String cmd = "update verordn set zzstatus='3' where rez_nr='"+rez_nr+" LIMIT 1";
				new ExUndHop().setzeStatement(cmd);				
				ret[0] = Boolean.valueOf(false); 
				ret[1] = tage.size();
			}
		}else if(unter18.equals("T") && (aktzzregel.equals("0"))){
			ret[0] = Boolean.valueOf(false); 
			ret[1] = tage.size();
		}
		return ret;
	}
	/********************************************************/
	public static void jahresWechselTest(String rez_nr,boolean azTest,boolean jahrTest){
		Vector vec = SqlInfo.holeFelder("select termine,id from verordn where rez_nr='"+rez_nr+"' LIMIT 1");
		vec = RezTools.holeEinzelTermineAusRezept(null,(String)((Vector)vec.get(0)).get(0));
		//System.out.println(vec);
	}

	public static int getZuzahlRegel(String kassid){
		String preisgrp = "";
		int zzregel = -1;
		if(kassid.equals("-1")){
			JOptionPane.showMessageDialog(null,"Keine gültige Kasse angegeben");
			return -1;
		}
		Vector vec = SqlInfo.holeFelder("select preisgruppe from kass_adr where id='"+kassid.trim()+"' LIMIT 1");
		System.out.println("Die Preisgruppe von KassenID ="+kassid.trim()+" = "+((String)((Vector)vec.get(0)).get(0)) );
		preisgrp = ((String)((Vector)vec.get(0)).get(0));
		
		zzregel = SystemConfig.vZuzahlRegeln.get(new Integer(preisgrp)-1);
		return zzregel;
	}
	
	public static int[] terminNachAchtzehn(Vector<String>tage,String geburtstag){
		String stichtag = "";
		int ret = -1;
		for(int i = 0;i < tage.size();i++){
			stichtag = ((String)tage.get(i)).substring(0,6)+new Integer(new Integer(SystemConfig.aktJahr)-18).toString();
			if(DatFunk.TageDifferenz(geburtstag ,stichtag) >= 0 ){
				return new int[]{1,i};
			}
			
		}
		return new int[] {0,-1};
	}

}
