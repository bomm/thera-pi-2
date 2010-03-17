package org.therapi.reha.patient;

import org.jdesktop.swingx.JXPanel;


public class PatientStammDatenPanel extends JXPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4929837198414837133L;
	PatientHauptPanel patientHauptPanel = null;
	
	public PatientStammDatenPanel(PatientHauptPanel patHauptPanel){
		super();
		setOpaque(false);
		this.patientHauptPanel = patHauptPanel;
	}
	
	public void fireAufraeumen(){

	}
	
}
