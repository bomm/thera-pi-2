package systemTools;

import hauptFenster.AktiveFenster;

import javax.swing.JComponent;

import patientenFenster.PatGrundPanel;
import RehaInternalFrame.JPatientInternal;

public class TestePatStamm {
	public static String PatStammArztID(){
		String ret = "";
		JComponent patient = AktiveFenster.getFensterAlle("PatientenVerwaltung");
		if(patient == null){
			return "";
		}
		if(! (PatGrundPanel.thisClass.vecaktrez == null)){
			if(PatGrundPanel.thisClass.vecaktrez.size() > 0){
				return (String)PatGrundPanel.thisClass.vecaktrez.get(16);
			}
		}
		if(! (PatGrundPanel.thisClass.patDaten == null)){	
			if(PatGrundPanel.thisClass.patDaten.size() > 0){
				//System.out.println("größe der PatDaten = "+PatGrundPanel.thisClass.patDaten.size());
				//System.out.println("größe der PatDaten = "+PatGrundPanel.thisClass.patDaten);
				return (String) PatGrundPanel.thisClass.patDaten.get(67);
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
		if(! (PatGrundPanel.thisClass.vecaktrez == null)){
			if(PatGrundPanel.thisClass.vecaktrez.size() > 0){
				return (String)PatGrundPanel.thisClass.vecaktrez.get(37);
			}
		}
		if(! (PatGrundPanel.thisClass.patDaten == null)){	
			if(PatGrundPanel.thisClass.patDaten.size() > 0){			
				return (String) PatGrundPanel.thisClass.patDaten.get(68);
			}
		}else{
			return "";
		}
		return "";
	}

}
