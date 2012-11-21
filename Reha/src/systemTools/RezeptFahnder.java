package systemTools;

import hauptFenster.AktiveFenster;
import hauptFenster.Reha;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.jdesktop.swingworker.SwingWorker;
import org.therapi.reha.patient.AktuelleRezepte;

import events.PatStammEvent;
import events.PatStammEventClass;

import CommonTools.SqlInfo;

public class RezeptFahnder {
	public RezeptFahnder(boolean showDialog){
		if(showDialog){
			Object ret = JOptionPane.showInputDialog(null, "Geben Sie bitte die Rezeptnummer ein", "");
			if(ret == null){
				return;
			}
			doFahndung(ret.toString().trim());
		}
	}
	
	public void doFahndung(String rez_nr){
		boolean inhistorie = false;
		String pat_intern = SqlInfo.holeEinzelFeld("select pat_intern from verordn where rez_nr = '"+rez_nr+"' LIMIT 1");
		if(pat_intern.equals("")){
			pat_intern = SqlInfo.holeEinzelFeld("select pat_intern from lza where rez_nr = '"+rez_nr+"' LIMIT 1");
			if(pat_intern.equals("")){
				JOptionPane.showMessageDialog(null, "Die Rezeptnummer ist weder im aktuellen Rezeptstamm, noch in der Historie\n\n"+ 
						"Vermutlich wurde das Rezept gelöscht\n\n"+
						"Rezeptnummer = "+rez_nr+"\n");
				return;
			}
			inhistorie = true;
			JOptionPane.showMessageDialog(null, "Dieses Rezept befindet sich bereits in der Historie");
		}
		JComponent patient = AktiveFenster.getFensterAlle("PatientenVerwaltung");
		final String xreznr = rez_nr;
		final boolean xinhistorie = inhistorie;
		if(patient == null){
			final String xpat_int = pat_intern;
			new SwingWorker<Void,Void>(){
				protected Void doInBackground() throws Exception {
					try{
					JComponent xpatient = AktiveFenster.getFensterAlle("PatientenVerwaltung");
					Reha.thisClass.progLoader.ProgPatientenVerwaltung(1);
					while( (xpatient == null) ){
						Thread.sleep(20);
						xpatient = AktiveFenster.getFensterAlle("PatientenVerwaltung");
					}
					while(  (!AktuelleRezepte.initOk) ){
						Thread.sleep(20);
					}
					
					String s1 = "#PATSUCHEN";
					String s2 = (String) xpat_int;
					PatStammEvent pEvt = new PatStammEvent(this);
					pEvt.setPatStammEvent("PatSuchen");
					pEvt.setDetails(s1,s2,"#REZHOLEN-"+xreznr) ;
					PatStammEventClass.firePatStammEvent(pEvt);
					if(xinhistorie){
						Reha.thisClass.patpanel.getTab().setSelectedIndex(1);	
					}else{
						Reha.thisClass.patpanel.getTab().setSelectedIndex(0);
					}
					}catch(Exception ex){
						ex.printStackTrace();
					}

					return null;
				}
				
			}.execute();
		}else{
			try{
			Reha.thisClass.progLoader.ProgPatientenVerwaltung(1);
			String s1 = "#PATSUCHEN";
			String s2 = (String) pat_intern;
			PatStammEvent pEvt = new PatStammEvent(this);
			pEvt.setPatStammEvent("PatSuchen");
			pEvt.setDetails(s1,s2,"#REZHOLEN-"+xreznr) ;
			PatStammEventClass.firePatStammEvent(pEvt);
			if(xinhistorie){
				Reha.thisClass.patpanel.getTab().setSelectedIndex(1);	
			}else{
				Reha.thisClass.patpanel.getTab().setSelectedIndex(0);
			}
			}catch(Exception ex){
				ex.printStackTrace();
			}

		}		
		
	}

}
