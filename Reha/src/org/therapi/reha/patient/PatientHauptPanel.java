package org.therapi.reha.patient;

import hauptFenster.Reha;
import hauptFenster.UIFSplitPane;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;

import patientenFenster.Dokumentation;
import patientenFenster.Gutachten;
import patientenFenster.Historie;
import patientenFenster.TherapieBerichte;
import systemTools.JCompTools;
import systemTools.JRtaTextField;
import RehaInternalFrame.JPatientInternal;

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
	PatientHauptLogic patientLogic = null;
	
	//SuchenFenster
	public Object sucheComponent = null;
	
	//ToolBar-Controls & Listener
	public JButton[] jbut = {null,null,null,null,null,null,null};
	public JFormattedTextField tfsuchen;
	public JComboBox jcom;
	public ActionListener toolBarAction;
	public MouseListener toolBarMouse;
	public KeyListener toolBarKeys;
	public FocusListener toolBarFocus;
	
	//StammDaten-Controls & Listener
	public JPatTextField[] ptfield = {null,null,null,null,null,null,
			null,null,null,null,null,null,null,null,null};
	public MouseListener stammDatenMouse;
	public KeyListener stammDatenKeys;
	
	//MemoPanel-Controls & Listener
	public JTabbedPane memotab = null;
	public JButton[] memobut = {null,null,null,null,null,null};
	public JTextArea[] pmemo = {null,null};
	public ActionListener memoAction = null;
	public int inMemo = -1;
	
	//MultiFunctionPanel-Controls & Listener
	JTabbedPane multiTab = null;
	public AktuelleRezepte aktRezept = null;
	public Historie historie = null;
	public TherapieBerichte berichte = null;
	public Dokumentation dokumentation = null;
	public Gutachten gutachten = null;
	public String[] tabTitel = {"aktuelle Rezepte",
			"Rezept-Historie",
			"Therapieberichte",
			"Dokumentation",
			"Gutachten",
			"Arzt & KK",
			"Plandaten"};	
	public JLabel[] rezlabs = {null,null,null,null,null,
			null,null,null,null,null,
			null,null,null,null,null};
	public JTextArea rezdiag = null;

	public ImageIcon[] imgzuzahl = {null,null,null,null};
	public ImageIcon[] imgrezstatus = {null,null};
	public Vector<String> patDaten = new Vector<String>();
	public Vector<String> vecaktrez = null;
	public Vector<String> vecakthistor = null;
	

	
	
	//PatStamm-Event Listener == extrem wichtig
	private PatStammEventListener patientStammEventListener = null;
	private PatStammEventClass ptp = null;
	
	//Instanz-Variable für die einzelnen Panels
	private PatientToolBarPanel patToolBarPanel = null;
	private PatientStammDatenPanel stammDatenPanel = null;
	private PatientMemoPanel patMemoPanel = null;
	private PatientMultiFunctionPanel patMultiFunctionPanel = null;
	
	//Gemeinsam genutzte Variable
	public Font font = new Font("Courier New",Font.BOLD,13);
	public Font fehler = new Font("Courier",Font.ITALIC,13);
	public String aktPatID = "";
	public int autoPatid = -1;
	public int aid = -1;
	public int kid = -1;
	public boolean patDatenOk = false;
	public boolean rezDatenOk = false;


	//Bezug zum unterliegenden JInternalFrame
	JPatientInternal patientInternal = null;
	/*******************************************************/
	public PatientHauptPanel(String name,JPatientInternal internal){
		super();
		setName(name);
		setDoubleBuffered(true);

		patientLogic = new PatientHauptLogic(this);
		patientInternal = internal;
		
		createPatStammListener();

		createActionListeners();
		createKeyListeners();
		createMouseListeners();
		createFocusListeners();
		
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("getTabs2"));
		//setBackgroundPainter(Reha.thisClass.compoundPainter.get("HauptPanel"));
		FormLayout lay = new FormLayout("0dlu,fill:0:grow(0.33),fill:0:grow(0.66)","0dlu,p,fill:0:grow(1.0)");
		CellConstraints cc = new CellConstraints();
		setLayout(lay);
		
		add(getToolBarPatient(),cc.xyw(1, 2, 3));
		add(constructSplitPaneLR(),cc.xyw(1,3,3));
		setVisible(true);
		setzeFocus();
	}
	/*******************************************************/
	public PatientHauptLogic getLogic(){
		return patientLogic;
	}
	public PatientHauptPanel getInstance(){
		return this;
	}
	public JPatientInternal getInternal(){
		return patientInternal;
	}
	public void setInternalToNull(){
		patientInternal = null;
	}

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
		jSplitLR.setDividerLocation(200);
		jSplitLR.validate();
		return jSplitLR;
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
		jSplitRechtsOU.setDividerLocation(175);
		jSplitRechtsOU.validate();
		return jSplitRechtsOU;
	}
	private JScrollPane getStammDatenPatient(){
		stammDatenPanel = new PatientStammDatenPanel(this);
		JScrollPane jscr = JCompTools.getTransparentScrollPane(stammDatenPanel );
		jscr.validate();
		JScrollPane jscr2 = JCompTools.getTransparent2ScrollPane(jscr);
		jscr2.validate();
		return jscr2;
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
		patToolBarPanel = new PatientToolBarPanel(this);
		return patToolBarPanel;

	}
	public JTabbedPane getTab(){
		return multiTab;
	}
	public PatientStammDatenPanel getStammDaten(){
		return stammDatenPanel;
	}
	public PatientMemoPanel getMemo(){
		return patMemoPanel;
	}
	public PatientMultiFunctionPanel getMultiFuncPanel(){
		return patMultiFunctionPanel;
	
	}
	public PatientToolBarPanel getToolBar(){
		return patToolBarPanel;
	}
	
	public void starteSuche(){
		patientLogic.starteSuche();
	}

	/*****************Dieser EventListener handled alle wesentlichen Funktionen inklusive der CloseWindow-Methode*************/
	private void createPatStammListener(){
		patientStammEventListener = new PatStammEventListener(){
			@Override
			public void patStammEventOccurred(PatStammEvent evt) {
				patientLogic.patStammEventOccurred(evt);
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
					patToolBarPanel.getLogic().reactOnAction(arg0);
				}
		};
		memoAction = new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					patMemoPanel.doMemoAction(arg0);
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
				patToolBarPanel.getLogic().reactOnKeyPressed(e);
			}
			@Override
			public void keyReleased(KeyEvent arg0) {
			}
			@Override
			public void keyTyped(KeyEvent arg0) {
			}
		};	
	}
	private void createFocusListeners(){
		toolBarFocus = new FocusListener(){
			@Override
			public void focusGained(FocusEvent e) {
				patToolBarPanel.getLogic().reactOnFocusGained(e);	
			}
			@Override
			public void focusLost(FocusEvent e) {
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
				patToolBarPanel.getLogic().reactOnMouseClicked(arg0);
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
		stammDatenPanel.fireAufraeumen();
		patToolBarPanel.getLogic().fireAufraeumen();
		patMemoPanel.fireAufraeumen();
		patMultiFunctionPanel.fireAufraeumen();
		this.ptp.removePatStammEventListener(patientStammEventListener);
		ptp = null;
		patientLogic.fireAufraeumen();
	}
	
	public void setzeFocus(){
		patientLogic.setzeFocus();
	}
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
class DatenHolen{
}
