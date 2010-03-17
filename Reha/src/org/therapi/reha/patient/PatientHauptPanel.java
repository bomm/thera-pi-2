package org.therapi.reha.patient;

import hauptFenster.Reha;
import hauptFenster.UIFSplitPane;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;

import systemTools.JCompTools;
import systemTools.JRtaTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import events.PatStammEvent;
import events.PatStammEventClass;
import events.PatStammEventListener;

/**
 * @author juergen
 *
 */
public class PatientHauptPanel extends JXPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 36015777152668128L;

	//Logik-Klasse für PatientHauptPanel
	PatientLogic patientLogic = null;
	
	//SuchenFenster
	public Object sucheComponent = null;
	
	//ToolBar-Controls & Listener
	public JButton[] jbut = {null,null,null,null,null,null,null};
	public JFormattedTextField tfsuchen;
	public JComboBox jcom;
	public ActionListener toolBarAction;
	public MouseListener toolBarMouse;
	public KeyListener toolBarKeys;
	
	//StammDaten-Controls & Listener
	public JPatTextField[] ptfield = {null,null,null,null,null,null,
			null,null,null,null,null,null,null,null,null};
	public MouseListener stammDatenMouse;
	public KeyListener stammDatenKeys;
	
	//PatStamm-Event Listener == extrem wichtig
	private PatStammEventListener patientStammEventListener = null;
	private PatStammEventClass ptp = null;
	
	//Instanz-Variable für die einzelnen Panels
	private PatientToolBar patToolBarPanel = null;
	private PatientMemoPanel patMemoPanel = null;
	private PatientMultiFunctionPanel patMultiFunctionPanel = null;
	
	/*******************************************************/
	public PatientHauptPanel(String name){
		super();
		setName(name);
		setDoubleBuffered(true);

		patientLogic = new PatientLogic(this);
		
		createPatStammListener();

		createActionListeners();
		createKeyListeners();
		createMouseListeners();
		
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("HauptPanel"));
		FormLayout lay = new FormLayout("0dlu,fill:0:grow(0.33),fill:0:grow(0.66)","0dlu,p,fill:0:grow(1.0)");
		CellConstraints cc = new CellConstraints();
		setLayout(lay);
		
		add(getToolBarPatient(),cc.xyw(1, 2, 3));
		add(constructSplitPaneLR(),cc.xyw(1,3,3));
		setVisible(true);
	}
	/*******************************************************/
	
	private UIFSplitPane constructSplitPaneLR(){
		UIFSplitPane jSplitLR =  UIFSplitPane.createStrippedSplitPane(JSplitPane.HORIZONTAL_SPLIT,
        		getStammDatenPatient(),
        		constructSplitPaneOU());
		jSplitLR.setOpaque(false);
		jSplitLR.setDividerSize(7);
		jSplitLR.setDividerBorderVisible(true);
		jSplitLR.setName("PatGrundSplitLinksRechts");
		jSplitLR.setOneTouchExpandable(true);
		jSplitLR.setDividerColor(Color.LIGHT_GRAY);		
		jSplitLR.validate();
		return jSplitLR;
	}
	private JXPanel getStammDatenPatient(){
		JXPanel stammDatenPatient = new PatientStammDatenPanel(this);
		return stammDatenPatient;
	}
	private UIFSplitPane constructSplitPaneOU(){
		UIFSplitPane jSplitRechtsOU =  UIFSplitPane.createStrippedSplitPane(JSplitPane.VERTICAL_SPLIT,
        		getMemosPatient(),
        		getMultiFunctionTab());
		jSplitRechtsOU.setOpaque(false);
		jSplitRechtsOU.setDividerSize(7);
		jSplitRechtsOU.setDividerBorderVisible(true);
		jSplitRechtsOU.setName("PatGrundSplitRechteSeiteObenUnten");
		jSplitRechtsOU.setOneTouchExpandable(true);
		jSplitRechtsOU.setDividerColor(Color.LIGHT_GRAY);
		jSplitRechtsOU.validate();
		return jSplitRechtsOU;
	}
	private JScrollPane getMemosPatient(){
		patMemoPanel  = new PatientMemoPanel(this);
		JScrollPane jscr = JCompTools.getTransparentScrollPane(patMemoPanel );
		jscr.validate();
		return jscr;
	}
	private JScrollPane getMultiFunctionTab(){
		patMultiFunctionPanel = new PatientMultiFunctionPanel(this);
		JScrollPane jscr = JCompTools.getTransparentScrollPane(patMultiFunctionPanel);
		jscr.validate();
		return jscr;		
	}
	private JXPanel getToolBarPatient(){
		patToolBarPanel = new PatientToolBar(this);
		return patToolBarPanel;

	}
	/*****************Dieser EventListener handled alle wesentlichen Funktionen inklusive der CloseWindow-Methode*************/
	private void createPatStammListener(){
		patientStammEventListener = new PatStammEventListener(){
			@Override
			public void patStammEventOccurred(PatStammEvent evt) {
			}
		};
		this.ptp = new PatStammEventClass();
		this.ptp.addPatStammEventListener((PatStammEventListener)patientStammEventListener);
		
	}

	/****************************************************/	
	/**
	 * Installiert die ActionListeners für alle drei Panels
	 * 
	 */
	private void createActionListeners(){
		toolBarAction = new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					
				}
			};
	}
	/****************************************************/
	/**
	 * Installiert die KeyListeners für alle drei Panels
	 * 
	 */
	private void createKeyListeners(){
		//PateintToolBar
		toolBarKeys = new KeyListener(){
			@Override
			public void keyPressed(KeyEvent e) {
				patToolBarPanel.reactOnKeys(e);
			}
			@Override
			public void keyReleased(KeyEvent arg0) {
			}
			@Override
			public void keyTyped(KeyEvent arg0) {
			}
		};	
	}
	/****************************************************/
	/**
	 * Installiert die MouseListeners für alle drei Panels
	 * 
	 */
	
	private void createMouseListeners(){
		toolBarMouse = new MouseListener(){
			public void mouseClicked(MouseEvent arg0) {
				patToolBarPanel.reactOnMouse(arg0);
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
		};

		stammDatenMouse = new MouseListener(){
			public void mouseClicked(MouseEvent arg0) {
				
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
		};
	}

	
	
	/****************************************************/	
	/**
	 * 
	 * Aufräumarbeiten
	 * zuerst die Listener entfernen
	 * 
	 */
	public void allesAufraeumen(){
		patToolBarPanel.fireAufraeumen();
		patMemoPanel.fireAufraeumen();
		patMultiFunctionPanel.fireAufraeumen();
	}
	
	
	/***********Inner-Class JPatTextField*************/
	class JPatTextField extends JRtaTextField{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2904164740273664807L;

		public JPatTextField(String type, boolean selectWhenFocus) {
			super(type, selectWhenFocus);
			setOpaque(false);
			setEditable(false);
			setBorder(null);
			addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent arg0) {
					if(arg0.getClickCount()==2 && arg0.getButton()==1){
						new SwingWorker<Void,Void>(){
							@Override
							protected Void doInBackground() throws Exception {
								String s1 = "#KORRIGIEREN";
								String s2 = getName();
								PatStammEvent pEvt = new PatStammEvent(this);
								pEvt.setPatStammEvent("PatSuchen");
								pEvt.setDetails(s1,s2,"") ;
								PatStammEventClass.firePatStammEvent(pEvt);	
								return null;
							}
						}.execute();
					}
				}
			});
		}
	}	
/**************************************************/
}
