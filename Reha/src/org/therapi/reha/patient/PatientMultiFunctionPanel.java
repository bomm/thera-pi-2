package org.therapi.reha.patient;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;

import org.jdesktop.swingx.JXPanel;


import com.jgoodies.looks.windows.WindowsTabbedPaneUI;

public class PatientMultiFunctionPanel extends JXPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1284209871875228012L;
	PatientHauptPanel patientHauptPanel = null;
	
	public PatientMultiFunctionPanel(PatientHauptPanel patHauptPanel){
		super();
		setLayout(new BorderLayout());
		setOpaque(false);
		this.patientHauptPanel = patHauptPanel;
		add(getTabs(),BorderLayout.CENTER);;
	}
	public void fireAufraeumen(){
		patientHauptPanel = null;
	}
	
	private synchronized JXPanel getTabs(){
		JXPanel rechts = new JXPanel(new BorderLayout());
		rechts.setOpaque(false);
		rechts.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		
		patientHauptPanel.multiTab = new JTabbedPane();

		//patientHauptPanel.multiTab.addFocusListener(eltern.getFocusListener());

		try{
			patientHauptPanel.multiTab.setUI(new WindowsTabbedPaneUI());
		}catch(Exception ex){
			// Kein KarstenLentzsch LAF
		}
		
		JXPanel tabpan = new JXPanel(new BorderLayout());
		tabpan.setBorder(BorderFactory.createEmptyBorder(0,0, 0, 0));
		tabpan.setOpaque(true);
		tabpan.setBackgroundPainter(Reha.thisClass.compoundPainter.get("getTabs2"));
		patientHauptPanel.aktRezept = new AktuelleRezepte(patientHauptPanel);
	    tabpan.add(patientHauptPanel.aktRezept);
	    patientHauptPanel.multiTab.addTab(patientHauptPanel.tabTitel[0]+" - 0", tabpan);
	    patientHauptPanel.multiTab.setMnemonicAt(0, KeyEvent.VK_A);
	    
	    patientHauptPanel.historie = new Historie();
	    patientHauptPanel.multiTab.addTab(patientHauptPanel.tabTitel[1]+" - 0", patientHauptPanel.historie);
	    patientHauptPanel.multiTab.setMnemonicAt(1, KeyEvent.VK_H);

	    patientHauptPanel.berichte = new TherapieBerichte();
	    patientHauptPanel.multiTab.addTab(patientHauptPanel.tabTitel[2]+" - 0", patientHauptPanel.berichte);
	    patientHauptPanel.multiTab.setMnemonicAt(2, KeyEvent.VK_T);

	    patientHauptPanel.dokumentation = new Dokumentation();
	    patientHauptPanel.multiTab.addTab(patientHauptPanel.tabTitel[3]+" - 0", patientHauptPanel.dokumentation);
	    patientHauptPanel.multiTab.setMnemonicAt(3, KeyEvent.VK_D);
	    
	    patientHauptPanel.gutachten = new Gutachten();
	    patientHauptPanel.multiTab.addTab(patientHauptPanel.tabTitel[4]+" - 0", patientHauptPanel.gutachten);
	    patientHauptPanel.multiTab.setMnemonicAt(4, KeyEvent.VK_G);
	    
	    rechts.add(patientHauptPanel.multiTab,BorderLayout.CENTER);
		rechts.revalidate();
		return rechts;
	}
	public AktuelleRezepte getAktRez(){
		return patientHauptPanel.aktRezept;
	}

}
