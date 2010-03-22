package systemTools;

import hauptFenster.AktiveFenster;
import hauptFenster.Reha;

import javax.swing.JComponent;


import RehaInternalFrame.JPatientInternal;

public class TestePatStamm {
	public static String PatStammArztID(){
		String ret = "";
		JComponent patient = AktiveFenster.getFensterAlle("PatientenVerwaltung");
		if(patient == null){
			return "";
		}
		if(! (Reha.thisClass.patpanel.vecaktrez == null)){
			if(Reha.thisClass.patpanel.vecaktrez.size() > 0){
				return (String)Reha.thisClass.patpanel.vecaktrez.get(16);
			}
		}
		if(! (Reha.thisClass.patpanel.patDaten == null)){	
			if(Reha.thisClass.patpanel.patDaten.size() > 0){
				//System.out.println("gr��e der PatDaten = "+PatGrundPanel.thisClass.patDaten.size());
				//System.out.println("gr��e der PatDaten = "+PatGrundPanel.thisClass.patDaten);
				return (String) Reha.thisClass.patpanel.patDaten.get(67);
			}	
		}else{
			return "";
		}
		return "";
	}
	public static String PatStammKasseID(){
		String ret = "";
		JComponent patient = AktiveFenster.getFensterAlle("PatientenVerwaltung");
		if(patient == null){
			return "";
		}
		if(! (Reha.thisClass.patpanel.vecaktrez == null)){
			if(Reha.thisClass.patpanel.vecaktrez.size() > 0){
				return (String)Reha.thisClass.patpanel.vecaktrez.get(37);
			}
		}
		if(! (Reha.thisClass.patpanel.patDaten == null)){	
			if(Reha.thisClass.patpanel.patDaten.size() > 0){			
				return (String) Reha.thisClass.patpanel.patDaten.get(68);
			}
		}else{
			return "";
		}
		return "";
	}

}
