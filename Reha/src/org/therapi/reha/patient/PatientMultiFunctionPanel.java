package org.therapi.reha.patient;

import hauptFenster.Reha;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;

import org.jdesktop.swingx.JXPanel;

import patientenFenster.Dokumentation;
import patientenFenster.Gutachten;
import patientenFenster.Historie;
import patientenFenster.TherapieBerichte;

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
	
	private JXPanel getTabs(){
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
	     
	    patientHauptPanel.historie = new Historie();
	    patientHauptPanel.multiTab.addTab(patientHauptPanel.tabTitel[1]+" - 0", patientHauptPanel.historie);
	

	    patientHauptPanel.berichte = new TherapieBerichte();
	    patientHauptPanel.multiTab.addTab(patientHauptPanel.tabTitel[2]+" - 0", patientHauptPanel.berichte);


	    patientHauptPanel.dokumentation = new Dokumentation();
	    patientHauptPanel.multiTab.addTab(patientHauptPanel.tabTitel[3]+" - 0", patientHauptPanel.dokumentation);
	
	    patientHauptPanel.gutachten = new Gutachten();
	    patientHauptPanel.multiTab.addTab(patientHauptPanel.tabTitel[4]+" - 0", patientHauptPanel.gutachten);

	    rechts.add(patientHauptPanel.multiTab,BorderLayout.CENTER);
		rechts.revalidate();
		return rechts;
	}
	public AktuelleRezepte getAktRez(){
		return patientHauptPanel.aktRezept;
	}

}
