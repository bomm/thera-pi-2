package org.therapi.reha.patient;

import org.jdesktop.swingx.JXPanel;

public class PatientMultiFunctionPanel extends JXPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1284209871875228012L;
	PatientHauptPanel patientHauptPanel = null;
	public PatientMultiFunctionPanel(PatientHauptPanel patHauptPanel){
		super();
		setOpaque(false);
		this.patientHauptPanel = patHauptPanel;
	}
	public void fireAufraeumen(){
		patientHauptPanel = null;
	}

}
