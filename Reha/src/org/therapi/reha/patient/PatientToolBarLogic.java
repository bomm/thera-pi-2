package org.therapi.reha.patient;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JLabel;



public class PatientToolBarLogic {
	PatientHauptPanel patientHauptPanel = null;
	PatientToolBarPanel patientToolBarPanel = null;
	public PatientToolBarLogic(PatientHauptPanel patientHauptPanel,PatientToolBarPanel patientTbPanel){
		this.patientHauptPanel = patientHauptPanel;
		this.patientToolBarPanel = patientTbPanel;
	}
	
	public void reactOnMouseClicked(MouseEvent arg0){
		if(arg0.getSource() instanceof JLabel){
			if(((JComponent)arg0.getSource()).getName().equals("Suchen")){
				patientHauptPanel.starteSuche();
				return;
			}
		}
	}
	
	public void reactOnKeyPressed(KeyEvent e){
		if(e.getKeyCode() == 10){
			if(((JComponent)e.getSource()).getName() != null){
				if( ((JComponent) e.getSource()).getName().equals("suchenach") ){
					patientHauptPanel.patientLogic.starteSuche();
				}
			}
		}
	}
	
	public void fireAufraeumen(){
		for(int i = 0; i < patientHauptPanel.jbut.length;i++){
			if(patientHauptPanel.jbut[i] != null){
				patientHauptPanel.jbut[i].removeActionListener(patientHauptPanel.toolBarAction);
				patientHauptPanel.jbut[i] = null;
			}
		}
		patientHauptPanel.toolBarAction = null;
		patientHauptPanel.tfsuchen.removeKeyListener(patientHauptPanel.toolBarKeys);
		patientHauptPanel.toolBarKeys = null;
		patientToolBarPanel.sucheLabel.removeMouseListener(patientHauptPanel.toolBarMouse);
		patientHauptPanel.toolBarMouse = null;
		patientHauptPanel = null;
	}
	public void reactOnAction(ActionEvent arg0){
		String cmd = arg0.getActionCommand();
		if(cmd.equals("neu")){
			patientHauptPanel.getLogic().patNeu();
			}
		if(cmd.equals("edit")){
			patientHauptPanel.getLogic().patEdit();
		}
		if(cmd.equals("delete")){
			patientHauptPanel.getLogic().patDelete();
		}
		if(cmd.equals("formulare")){
			patientHauptPanel.getLogic().patStarteFormulare();
			//patientHauptPanel.getLogic().setzeFocus();
		}
		if(cmd.equals("email")){
			patientHauptPanel.getLogic().setzeFocus();
		}
		if(cmd.equals("sms")){
			//new SMS();
			patientHauptPanel.getLogic().setzeFocus();
		}

	}


}
