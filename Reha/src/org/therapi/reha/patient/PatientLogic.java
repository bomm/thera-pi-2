package org.therapi.reha.patient;

import hauptFenster.Reha;
import hauptFenster.SuchenDialog;

import java.awt.Point;

import javax.swing.JOptionPane;

public class PatientLogic {
	
	public PatientHauptPanel patientHauptPanel = null;
	private String lastseek = "";
	
	public PatientLogic(PatientHauptPanel patHauptPanel){
		this.patientHauptPanel = patHauptPanel;
	}
	public void fireAufraeumen(){
		patientHauptPanel = null;
	}
	
	/***
	 * SuchenDialog aufrufen
	 */
	public void starteSuche(){
		if(patientHauptPanel.tfsuchen.getText().trim().equals("")){
			String cmd = "<html>Sie haben <b>kein</b> Suchkriterium eingegeben.<br>"+
			"Das bedeutet Sie laden den <b>kompletten Patientenstamm!!!<b><br><br>"+
			"Wollen Sie das wirklich?";
			int anfrage = JOptionPane.showConfirmDialog(null, cmd,"Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
			if(anfrage == JOptionPane.NO_OPTION){
				return;
			}
		}
		if (patientHauptPanel.sucheComponent != null){
			Point thispoint = Reha.thisClass.patpanel.getLocationOnScreen();
			((SuchenDialog) patientHauptPanel.sucheComponent).setLocation(thispoint.x+30, thispoint.y+80);
			if(! patientHauptPanel.tfsuchen.getText().trim().equals(lastseek)){
				((SuchenDialog) patientHauptPanel.sucheComponent).suchDasDing(patientHauptPanel.tfsuchen.getText());
				lastseek = patientHauptPanel.tfsuchen.getText().trim();
			}
			((SuchenDialog) patientHauptPanel.sucheComponent).setVisible(true);
		}else{
			patientHauptPanel.sucheComponent = new SuchenDialog(null,Reha.thisClass.patpanel,patientHauptPanel.tfsuchen.getText());
			Point thispoint = Reha.thisClass.patpanel.getLocationOnScreen();
			((SuchenDialog) patientHauptPanel.sucheComponent).setLocation(thispoint.x+30, thispoint.y+80);
			((SuchenDialog) patientHauptPanel.sucheComponent).setVisible(true);
			lastseek = patientHauptPanel.tfsuchen.getText().trim();
		}
	}
	
	
	
}
