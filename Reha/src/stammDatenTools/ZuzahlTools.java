package stammDatenTools;

import java.util.Vector;

import javax.swing.JOptionPane;

import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;

public class ZuzahlTools {
	public static boolean zzStatusEdit(String pat_int,String geboren, String rez_nr,String frei, String kassid){
		String preisgrp = "";
		String zzid = "";
		int zzregel = -1; 
		if(kassid.equals("-1")){
			JOptionPane.showMessageDialog(null,"Keine gültige Kasse angegeben");
			return false;
		}
		if(rez_nr.equals("")){
			Vector vec = SqlInfo.holeFelder("select rez_nr,kid,id from verordn where pat_intern='"+pat_int+"'");
			for(int i = 0;i < vec.size(); i++){
				zzregel = getZuzahlRegel((String)((Vector)vec.get(i)).get(1));
				zzid = (String)((Vector)vec.get(i)).get(2);
				System.out.println("Rezeptnummer = "+((Vector)vec.get(i)).get(0)+" Zuzahlregel = "+zzregel);
				if(zzregel > 0){
					if(frei.equals("F")){
//						SqlInfo.aktualisiereSatze("verordn", "set befr='F',zzstatus='2', kriterium)
					}else{

					}
				}else{
					
				}
			}
			return true;
		}else{
			//Wenn Rezeptnummer nicht leer, dann kommt der Aufruf aus Rezept!!!
		}
		return false;
	}
	/**********************************************************/
	public static int getZuzahlRegel(String kassid){
		String preisgrp = "";
		int zzregel = -1;
		if(kassid.equals("-1")){
			JOptionPane.showMessageDialog(null,"Keine gültige Kasse angegeben");
			return -1;
		}
		Vector vec = SqlInfo.holeFelder("select preisgruppe from kass_adr where id='"+kassid.trim()+"' LIMIT 1");
		preisgrp = ((String)((Vector)vec.get(0)).get(0));
		zzregel = SystemConfig.vZuzahlRegeln.get(new Integer(preisgrp));
		return zzregel;
	}
}
