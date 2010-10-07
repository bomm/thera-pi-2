package org.therapi.reha.patient;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.TooManyListenersException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JToolBar;

import org.jdesktop.swingx.JXPanel;

import systemEinstellungen.SystemConfig;
import systemTools.Colors;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class PatientToolBarPanel extends JXPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8491959397526727602L;
	PatientHauptPanel patientHauptPanel = null;
	PatientToolBarLogic patToolLogic = null;
	public JLabel sucheLabel = null;
	
	public PatientToolBarPanel(PatientHauptPanel patHauptPanel){
		super();
		setOpaque(false);
		this.patientHauptPanel = patHauptPanel;
		patToolLogic = new PatientToolBarLogic(patHauptPanel,this);
		setBorder(BorderFactory.createLineBorder(Color.WHITE));
		//setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
		FormLayout lay = new FormLayout("3dlu,right:max(35dlu;p),3dlu,p,45dlu,fill:0:grow(0.10),0dlu ,right:max(39dlu;p),3dlu, p,45dlu,7dlu,"+
				//  2-teSpalte (13)  14  15 16     17            18   19      20             21   22  23    24 25  26      27                28
				"right:max(39dlu;p),3dlu,p,90,fill:0:grow(0.60),0dlu,7dlu,right:max(39dlu;p),3dlu,p,40dlu,2dlu,p,50dlu,fill:0:grow(0.30),5dlu,10dlu",
				// 1                 2  3  4   5  6	  7  8   9 10     11
		"fill:0:grow(0.50),p,fill:0:grow(0.50)");
		CellConstraints cc = new CellConstraints();
		setLayout(lay);
		JLabel lbl = new JLabel("Kriterium:");
		add(lbl,cc.xy(2,2));
		patientHauptPanel.jcom = new JComboBox(new String[] {"Name Vorname","Patienten-ID","Vorname Name","4.Kriterium noch unbelegt","5.Kriterium noch unbelegt","6.Kriterium noch unbelegt","7.Kriterium noch unbelegt"});
		patientHauptPanel.jcom.setBackground(new Color(247,209,176));
		add(patientHauptPanel.jcom,cc.xyw(15, 2, 3));
		add(patientHauptPanel.jcom,cc.xyw(4,2,8));
		
		sucheLabel = new JLabel("finde Pat. -->");
		sucheLabel.setName("Suchen");
		sucheLabel.setIcon(SystemConfig.hmSysIcons.get("find"));
		sucheLabel.addMouseListener(patientHauptPanel.toolBarMouse);
		sucheLabel.addFocusListener(patientHauptPanel.toolBarFocus);

		patientHauptPanel.dropTargetListener =
			 new DropTargetListener() {
			  public void dragEnter(DropTargetDragEvent e) {
				  if(!patientHauptPanel.tfsuchen.getText().equals("")){
					  patientHauptPanel.tfsuchen.setText("");					  
				  }
			  }
			  public void dragExit(DropTargetEvent e) {}
			  public void dragOver(DropTargetDragEvent e) {}
			  public void drop(DropTargetDropEvent e) {
				  //String mitgebracht = "";
				    try {
					    patientHauptPanel.patientLogic.starteSuche();
					    //System.out.println("erhalte Drop = "+mitgebracht);
				    } catch (Throwable t) {
				    	t.printStackTrace();
						System.out.println("Fehler***************1********");
				    }
			    	e.dropComplete(true);
			  	}
			  public void dropActionChanged(
			         DropTargetDragEvent e) {System.out.println(e);}
		};	
		//sucheLabel.setDropTarget(dndt);
		add(sucheLabel,cc.xy(13,2));
		patientHauptPanel.tfsuchen = new JFormattedTextField();
		patientHauptPanel.tfsuchen.setFont(new Font("Tahoma",Font.BOLD,11));
		patientHauptPanel.tfsuchen.setBackground(Colors.PiOrange.alpha(0.15f));
		patientHauptPanel.tfsuchen.setForeground(new Color(136,136,136));
		patientHauptPanel.tfsuchen.setName("suchenach");
		patientHauptPanel.tfsuchen.addKeyListener(patientHauptPanel.toolBarKeys);
		patientHauptPanel.tfsuchen.addFocusListener(patientHauptPanel.toolBarFocus);
		//patientHauptPanel.tfsuchen.setDropTarget(dndt);
		try {
			patientHauptPanel.tfsuchen.getDropTarget().addDropTargetListener(patientHauptPanel.dropTargetListener);
		} catch (TooManyListenersException e1) {
			e1.printStackTrace();
		}
		add(patientHauptPanel.tfsuchen,cc.xyw(15, 2, 3));
		
		JToolBar jtb = new JToolBar();
		jtb.setRollover(true);
		jtb.setBorder(null);
		jtb.setOpaque(false);

		patientHauptPanel.jbut[0] = new JButton();
		patientHauptPanel.jbut[0].setIcon(SystemConfig.hmSysIcons.get("neu"));
		patientHauptPanel.jbut[0].setToolTipText("neuen Patient anlegen (Alt+N)");
		patientHauptPanel.jbut[0].setActionCommand("neu");
		patientHauptPanel.jbut[0].addActionListener(patientHauptPanel.toolBarAction);
		jtb.add(patientHauptPanel.jbut[0]);

		patientHauptPanel.jbut[1] = new JButton();
		patientHauptPanel.jbut[1].setIcon(SystemConfig.hmSysIcons.get("edit"));
		patientHauptPanel.jbut[1].setToolTipText("aktuellen Patient ändern/editieren (Alt+E)");		
		patientHauptPanel.jbut[1].setActionCommand("edit");
		patientHauptPanel.jbut[1].addActionListener(patientHauptPanel.toolBarAction);
		jtb.add(patientHauptPanel.jbut[1]);

		patientHauptPanel.jbut[2] = new JButton();
		patientHauptPanel.jbut[2].setIcon(SystemConfig.hmSysIcons.get("delete"));
		patientHauptPanel.jbut[2].setToolTipText("Patient löschen (Alt+L)");
		patientHauptPanel.jbut[2].setActionCommand("delete");
		patientHauptPanel.jbut[2].addActionListener(patientHauptPanel.toolBarAction);
		jtb.add(patientHauptPanel.jbut[2]);

		jtb.addSeparator(new Dimension(30,0));

		patientHauptPanel.jbut[3] = new JButton();
		patientHauptPanel.jbut[3].setIcon(SystemConfig.hmSysIcons.get("print"));
		patientHauptPanel.jbut[3].setToolTipText("Brief/Formular für Patient erstellen (Alt+B)");
		patientHauptPanel.jbut[3].setActionCommand("formulare");
		patientHauptPanel.jbut[3].addActionListener(patientHauptPanel.toolBarAction);
		jtb.add(patientHauptPanel.jbut[3]);

		jtb.addSeparator(new Dimension(30,0));
		
		patientHauptPanel.jbut[4] = new JButton();
		patientHauptPanel.jbut[4].setIcon(SystemConfig.hmSysIcons.get("tools"));
		patientHauptPanel.jbut[4].setToolTipText("Werkzeugkiste für aktuellen Patient");
		patientHauptPanel.jbut[4].setActionCommand("werkzeuge");
		patientHauptPanel.jbut[4].addActionListener(patientHauptPanel.toolBarAction);
		jtb.add(patientHauptPanel.jbut[4]);
		
		/*
		patientHauptPanel.jbut[4] = new JButton();
		patientHauptPanel.jbut[4].setIcon(SystemConfig.hmSysIcons.get("email"));
		patientHauptPanel.jbut[4].setToolTipText("(e)Mail für Patient erstellen (Alt+M)");
		patientHauptPanel.jbut[4].setActionCommand("email");
		patientHauptPanel.jbut[4].addActionListener(patientHauptPanel.toolBarAction);
		jtb.add(patientHauptPanel.jbut[4]);

		patientHauptPanel.jbut[5] = new JButton();
		patientHauptPanel.jbut[5].setIcon(SystemConfig.hmSysIcons.get("sms"));
		patientHauptPanel.jbut[5].setToolTipText("SMS für Patient erstellen (Alt+S)");
		patientHauptPanel.jbut[5].setActionCommand("sms");
		patientHauptPanel.jbut[5].addActionListener(patientHauptPanel.toolBarAction);
		jtb.add(patientHauptPanel.jbut[5]);
		
		patientHauptPanel.jbut[6] = new JButton();
		patientHauptPanel.jbut[6].setIcon(SystemConfig.hmSysIcons.get("info"));
		patientHauptPanel.jbut[6].setToolTipText("Zusatzinformationen zum aktuellen Patient (Alt+I)");
		patientHauptPanel.jbut[6].setActionCommand("zusatzinfo");
		patientHauptPanel.jbut[6].addActionListener(patientHauptPanel.toolBarAction);
		jtb.add(patientHauptPanel.jbut[6]);
		*/
		add(jtb,cc.xyw(20,2,8));
	}
	public PatientToolBarLogic getLogic(){
		return patToolLogic;
	}
	

}
