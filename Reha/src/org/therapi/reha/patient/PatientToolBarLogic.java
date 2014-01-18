package org.therapi.reha.patient;

import geraeteInit.SMS;
import hauptFenster.Reha;
import hauptFenster.RehaIOMessages;
import hauptFenster.RehaIOServer;
import hauptFenster.ReverseSocket;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;





import CommonTools.StringTools;
import rechteTools.Rechte;
import systemEinstellungen.SystemConfig;
import systemTools.IconListRenderer;
import dialoge.ToolsDialog;



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
				if(patientHauptPanel.inMemo > -1){
					Reha.thisClass.patpanel.pmemo[patientHauptPanel.inMemo].requestFocus();
					return;
				}
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
		patientToolBarPanel.sucheLabel.removeFocusListener(patientHauptPanel.toolBarFocus);
		patientHauptPanel.tfsuchen.removeFocusListener(patientHauptPanel.toolBarFocus);
		patientHauptPanel.tfsuchen.getDropTarget().removeDropTargetListener(patientHauptPanel.dropTargetListener);
		patientHauptPanel.dropTargetListener = null;
		patientHauptPanel.toolBarFocus = null;
		patientToolBarPanel.sucheLabel = null;		
		patientHauptPanel = null;

	}
	public void reactOnFocusGained(FocusEvent e){
		if(((JComponent)e.getSource()).getName().equals("suchenach") && patientHauptPanel.inMemo > -1 ){
			Reha.thisClass.patpanel.pmemo[patientHauptPanel.inMemo].requestFocus();
		}
		if(!patientHauptPanel.getInternal().getActive()){
			patientHauptPanel.getInternal().setSpecialActive(true);
		}	
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
		if(cmd.equals("werkzeuge")){
			new ToolsDlgPatient("",patientHauptPanel.jbut[4].getLocationOnScreen());
			//patientHauptPanel.getLogic().setzeFocus();
		}
	}
	public void doPatNachricht(){
		new Thread(){
			public void run(){
				try{
					//hier Pat_intern und Rezeptnummer ermitteln
					if(patientHauptPanel.aktPatID.equals("")){return;}
					String spat_intern = patientHauptPanel.patDaten.get(29);
					String srez_nr = "-1";
					String sbetreff = "Patient(in): "+StringTools.EGross(patientHauptPanel.patDaten.get(2))+", "+StringTools.EGross(patientHauptPanel.patDaten.get(3))+" PatID: "+spat_intern;
					if(Reha.thisClass.patpanel.aktRezept.tabaktrez.getRowCount() > 0){
						srez_nr = Reha.thisClass.patpanel.vecaktrez.get(1);
					}
					if(! RehaIOServer.rehaMailIsActive){
						if(Reha.aktUser.startsWith("Therapeut")){return;}
						Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
						
								new LadeProg(Reha.proghome+"RehaMail.jar"+" "+Reha.proghome+" "+Reha.aktIK+" "+Reha.xport+" "+Reha.aktUser.replace(" ", "#"));
								long warten = System.currentTimeMillis();
								while( (!RehaIOServer.rehaMailIsActive)  && (System.currentTimeMillis()-warten < 15000)){
									Thread.sleep(75);
								}
								if(!RehaIOServer.rehaMailIsActive){
									JOptionPane.showMessageDialog(null,"Kann Thera-Pi Nachrichten nicht starten");
									return;
								}
						
					}else{
						if(Reha.aktUser.startsWith("Therapeut")){return;}
						new ReverseSocket().setzeRehaNachricht(RehaIOServer.rehaMailreversePort,"Reha#"+RehaIOMessages.MUST_GOTOFRONT);
					}		
					new ReverseSocket().setzeRehaNachricht(RehaIOServer.rehaMailreversePort,"Reha#WANTPATMESSAGE#"+spat_intern+"#"+srez_nr+"#"+sbetreff);
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}.start();

	}
	public void doPatFragebogen(){
		try{
			if(patientHauptPanel.aktPatID.equals("")){return;}
			String srez_nr = "-1";
			if(Reha.thisClass.patpanel.aktRezept.tabaktrez.getRowCount() > 0){
				srez_nr = Reha.thisClass.patpanel.vecaktrez.get(1);
			}
			final String spat_intern = patientHauptPanel.patDaten.get(29);
			final String xsrez_nr = srez_nr;
			new Thread(){
				public void run(){
					//System.out.println(Reha.proghome+"RehaFbBrowser.jar"+" ?patnummer="+spat_intern+"#reznummer="+xsrez_nr);
					new LadeProg(Reha.proghome+"RehaFbBrowser.jar"+" ?pat_intern="+spat_intern+"#rez_nr="+xsrez_nr);		
				}
			}.start();			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	class ToolsDlgPatient{
		public ToolsDlgPatient(String command,Point pt){
			Map<Object, ImageIcon> icons = new HashMap<Object, ImageIcon>();
			icons.put("Patientenbezogene Nachricht erstellen",SystemConfig.hmSysIcons.get("patnachrichten"));
			icons.put("(e)Mail für Patient erstellen (Alt+M)",SystemConfig.hmSysIcons.get("email"));
			icons.put("SMS für Patient erstellen (Alt+S)",SystemConfig.hmSysIcons.get("sms"));
			icons.put("Zusatzinformationen zum aktuellen Patient (Alt+I)",SystemConfig.hmSysIcons.get("info"));
			// create a list with some test data
			JList list = new JList(	new Object[] {"Patientenbezogene Nachricht erstellen",
					"(e)Mail für Patient erstellen (Alt+M)", 
					"SMS für Patient erstellen (Alt+S)", 
					"Zusatzinformationen zum aktuellen Patient (Alt+I)"});
			list.setCellRenderer(new IconListRenderer(icons));	
			Reha.toolsDlgRueckgabe = -1;
			ToolsDialog tDlg = new ToolsDialog(Reha.thisFrame,"Werkzeuge: aktueller Patient",list);
			tDlg.setPreferredSize(new Dimension(300,200+
					((Boolean)SystemConfig.hmPatientenWerkzeugDlgIni.get("ToolsDlgShowButton")? 25 : 0) ));
			tDlg.setLocation(pt.x-200,pt.y+30);
			tDlg.pack();
			tDlg.setModal(true);
			tDlg.activateListener();
			tDlg.setVisible(true);
			switch(Reha.toolsDlgRueckgabe){
			case 0:
				doPatNachricht();
				return;
			case 1:
				if(!Rechte.hatRecht(Rechte.Patient_email, true)){
					return;
				}
				break;
			case 2:
				if(!Rechte.hatRecht(Rechte.Patient_sms, true)){
					return;
				}
				//new SMS(null);
				break;
			case 3:
				doPatFragebogen();
				if(!Rechte.hatRecht(Rechte.Patient_zusatzinfo, true)){
					
					return;
				}
				break;
			case 4:
				break;
				
			}
			tDlg = null;
		}
	}


}
