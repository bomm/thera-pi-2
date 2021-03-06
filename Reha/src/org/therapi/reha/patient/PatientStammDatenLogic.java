package org.therapi.reha.patient;

import javax.swing.event.HyperlinkEvent;

public class PatientStammDatenLogic {
	PatientHauptPanel patHauptPanel = null;
	PatientStammDatenPanel patientStammDatenPanel = null;
	public PatientStammDatenLogic(PatientHauptPanel patHauptPanel,PatientStammDatenPanel patStammPanel){
		this.patHauptPanel = patHauptPanel;
		this. patientStammDatenPanel = patStammPanel; 
	}
	public void reactOnHyperlink(HyperlinkEvent arg0){
	    if (arg0.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			if( extractFieldName(arg0.getURL().toString()).equals("FOTO") ){
				patHauptPanel.getLogic().doPatFoto();
				return;
			}
	    	if(!patHauptPanel.getLogic().neuDlgOffen){
		    	patHauptPanel.getLogic().editFeld(extractFieldName(arg0.getURL().toString()));	    		
	    	}
	    }
	}    
	private String extractFieldName(String url){
		String ext = url.substring(7);
		return ext.replace(".de", "");
	}
}
