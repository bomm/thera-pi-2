package org.therapi.reha.patient;

import org.jdesktop.swingx.JXPanel;

public class PatientMemoPanel extends JXPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1894163619378832811L;
	PatientHauptPanel patientHauptPanel = null;
	public PatientMemoPanel(PatientHauptPanel patHauptPanel){
		super();
		setOpaque(false);
		this.patientHauptPanel = patHauptPanel;
	}
	public void fireAufraeumen(){
		
	}

}
