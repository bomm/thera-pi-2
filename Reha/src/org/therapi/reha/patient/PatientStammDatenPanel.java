package org.therapi.reha.patient;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import org.jdesktop.swingx.JXPanel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


public class PatientStammDatenPanel extends JXPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4929837198414837133L;
	PatientHauptPanel patientHauptPanel = null;
	
	public PatientStammDatenPanel(PatientHauptPanel patHauptPanel){
		super();
		setLayout(new BorderLayout());
		setOpaque(false);
		this.patientHauptPanel = patHauptPanel;
		setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
		add(getStammDatenPanel(),BorderLayout.CENTER);
	}
	
	public void fireAufraeumen(){

	}
	private JXPanel getStammDatenPanel(){   //1         2            3       4    5           6           7         8         
		FormLayout lay = new FormLayout("3dlu,right:max(38dlu;p),3dlu,55dlu:g,3dlu,right:max(39dlu;p),3dlu,45dlu:g,5dlu",
				// 1     2  3  4  5  6	7  8  9 10 11 12 13
				"0dlu ,0dlu,p,1px,p,1px,p,1px,p,1px,p,1px,p,"+
				//14 15 16 17 18 19 20 21  22 23 24 25
				"15px,p,1px,p,1px,p,1px,p,15px,p,1px,p");
		JXPanel jpan = new JXPanel();
		jpan.setOpaque(false);
		jpan.setLayout(lay);
		CellConstraints cc = new CellConstraints();
		jpan.add(new JLabel("Anrede / Titel"),cc.xy(2,3));
		patientHauptPanel.ptfield[0] = new JPatTextField("XGROSS",false);
		patientHauptPanel.ptfield[0].setName("ANREDE");
		jpan.add(patientHauptPanel.ptfield[0],cc.xy(4,3));
		
		//add(new JLabel("Titel"),cc.xy(6,3));
		patientHauptPanel.ptfield[1] = new JPatTextField("XGROSS",false);
		patientHauptPanel.ptfield[1].setName("TITEL");
		jpan.add(patientHauptPanel.ptfield[1],cc.xyw(6,3,3));	
		
		jpan.add(new JLabel("Name"),cc.xy(2,5));
		patientHauptPanel.ptfield[2] = new JPatTextField("XGROSS",false);
		patientHauptPanel.ptfield[2].setName("N_NAME");		
		jpan.add(patientHauptPanel.ptfield[2],cc.xyw(4,5,5));		

		jpan.add(new JLabel("Vorname"),cc.xy(2,7));
		patientHauptPanel.ptfield[3] = new JPatTextField("XGROSS",false);
		patientHauptPanel.ptfield[3].setName("V_NAME");		
		jpan.add(patientHauptPanel.ptfield[3],cc.xyw(4,7,5));		

		jpan.add(new JLabel("Geboren"),cc.xy(2,9));
		patientHauptPanel.ptfield[4] = new JPatTextField("DATUM",false); //new JPatTextField("DATUM",true);
		patientHauptPanel.ptfield[4].setName("GEBOREN");		
		jpan.add(patientHauptPanel.ptfield[4],cc.xy(4,9));
		
		jpan.add(new JLabel("Strasse"),cc.xy(2,11));
		patientHauptPanel.ptfield[10] = new JPatTextField("XROSS",false);
		patientHauptPanel.ptfield[10].setName("STRASSE");
		jpan.add(patientHauptPanel.ptfield[10],cc.xyw(4,11,5));		

		jpan.add(new JLabel("PLZ, Ort"),cc.xy(2,13));
		patientHauptPanel.ptfield[11] = new JPatTextField("ZAHLEN",false);
		patientHauptPanel.ptfield[11].setName("PLZ");		
		jpan.add(patientHauptPanel.ptfield[11],cc.xy(4,13));		

		patientHauptPanel.ptfield[12] = new JPatTextField("XGROSS",false);
		patientHauptPanel.ptfield[12].setName("ORT");		
		jpan.add(patientHauptPanel.ptfield[12],cc.xyw(6,13,3));		

		jpan.add(new JLabel("Telefon(p)"),cc.xy(2,15));
		patientHauptPanel.ptfield[6] = new JPatTextField("XGROSS",false);
		patientHauptPanel.ptfield[6].setName("TELEFONP");
		jpan.add(patientHauptPanel.ptfield[6],cc.xyw(4,15,5));		

		jpan.add(new JLabel("Telefon(g)"),cc.xy(2,17));
		patientHauptPanel.ptfield[7] = new JPatTextField("XGROSS",false);
		patientHauptPanel.ptfield[7].setName("TELEFONG");		
		jpan.add(patientHauptPanel.ptfield[7],cc.xyw(4,17,5));		

		jpan.add(new JLabel("Mobil"),cc.xy(2,19));
		patientHauptPanel.ptfield[8] = new JPatTextField("XGROSS",false);
		patientHauptPanel.ptfield[8].setName("TELEFONM");
		//patientHauptPanel.ptfield[8].addMouseListener(patientHauptPanel.ml);		
		jpan.add(patientHauptPanel.ptfield[8],cc.xyw(4,19,5));		

		jpan.add(new JLabel("Email"),cc.xy(2,21));
		patientHauptPanel.ptfield[9] = new JPatTextField("KLEIN",false);
		patientHauptPanel.ptfield[9].setName("EMAILA");
		//patientHauptPanel.ptfield[9].addMouseListener(patientHauptPanel.ml);
		jpan.add(patientHauptPanel.ptfield[9],cc.xyw(4,21,5));	
		
		jpan.add(new JLabel("Krankenkasse"),cc.xy(2,23));
		patientHauptPanel.ptfield[14] = new JPatTextField("XROSS",false);
		patientHauptPanel.ptfield[14].setName("KASSE");
		jpan.add(patientHauptPanel.ptfield[14],cc.xyw(4,23,5));		
		
		jpan.add(new JLabel("Hausarzt"),cc.xy(2,25));
		patientHauptPanel.ptfield[13] = new JPatTextField("XROSS",false);
		patientHauptPanel.ptfield[13].setName("ARZT");
		jpan.add(patientHauptPanel.ptfield[13],cc.xyw(4,25,5));
		
		patientHauptPanel.ptfield[5] = new JPatTextField("XROSS",false);
		patientHauptPanel.ptfield[5].setName("PAT_INTERN");
	    for(int i = 0;i < 		patientHauptPanel.ptfield.length;i++){
	    	patientHauptPanel.ptfield[i].setForeground(Color.BLUE);
	    	patientHauptPanel.ptfield[i].setFont(patientHauptPanel.font);
	    	//patientHauptPanel.ptfield[i].addFocusListener(getTextFieldFocusListener());
	    }
		patientHauptPanel.ptfield[2].setForeground(Color.RED);
		patientHauptPanel.ptfield[3].setForeground(Color.RED);
		patientHauptPanel.ptfield[4].setForeground(Color.RED);
		
		jpan.validate();
		return jpan;
		
	}
	
	
}

