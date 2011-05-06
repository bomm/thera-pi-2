package terminKalender;





import generalSplash.RehaSplash;
import hauptFenster.AktiveFenster;
import hauptFenster.Reha;
import hilfsFenster.TerminEinpassen;
import hilfsFenster.TerminObenUntenAnschliessen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyVetoException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.TooManyListenersException;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import kurzAufrufe.KurzAufrufe;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.border.DropShadowBorder;
import org.therapi.reha.patient.AktuelleRezepte;

import rechteTools.Rechte;
import rehaInternalFrame.JRehaInternal;
import sqlTools.SqlInfo;
import stammDatenTools.RezTools;
import systemEinstellungen.SystemConfig;
import systemTools.JRtaTextField;
import systemTools.ListenerTools;
import events.PatStammEvent;
import events.PatStammEventClass;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;


public class TerminFenster extends Observable implements RehaTPEventListener, ActionListener,DropTargetListener,DragSourceListener,DragGestureListener{

	
	private int setOben; 

	private String FensterName = ""; 
	
	private JXPanel GrundFlaeche = null;
	private JXPanel ComboFlaeche = null;	
	private JXPanel TerminFlaeche = null;
	public JXPanel ViewPanel = null;

	private kalenderPanel[] oSpalten = {null,null,null,null,null,null,null};	
	private JComboBox[] oCombo = {null,null,null,null,null,null,null};
	
	private JPopupMenu jPopupMenu = null; 
	private JMenuItem Normalanzeige = null;
	private JMenuItem Wochenanzeige = null;
	private JMenuItem Patientsuchen = null;
	private JMenuItem Gruppezusammenfassen = null;
	private JMenuItem Gruppeloeschen = null;
	private JMenuItem Gruppekopieren = null;	
	private JMenuItem Terminliste = null;
	private JMenuItem Gruppeeinfuegen = null;
	private JMenuItem Terminedespatsuchen = null;	
	
	private JMenuItem Interminkalenderschreiben = null;
	private JMenuItem Copy = null;
	private JMenuItem Cut = null;
	private JMenuItem Paste = null;
	private JMenuItem Confirm = null;
	private JMenuItem Tagvor = null;
	private JMenuItem Tagzurueck = null;
	private JMenuItem Tagesdialog = null;	
	private JMenuItem Behandlerset = null;
	private JMenuItem Tauschemitvorherigem = null;	
	private JMenuItem Tauschemitnachfolger = null;	
	private JMenuItem Telefonliste = null;	
	

	
	private Zeitfenster zf;
	private DruckFenster df;
	private SchnellSuche sf;
	private MaskeInKalenderSchreiben mb = null;
	
	public int ansicht = 0; // 0=Normalansicht 1=Wochenansicht
	private int[] belegung = {-1,-1,-1,-1,-1,-1,-1}; //Welcher Kollege(Nr. ist in der jeweiligen Spalte sichtbar
	private int wochenbelegung = 0; // nimmt die KollegenNr auf dessen Woche angezeigt wird
	private int maskenbelegung = 0; // nimmt die KollegenNr auf dessen Maske erstellt/editiert wird
	private int maskenwahl = 0; // nimmt die KollegenNr auf dessen Maske erstellt/editiert wird
	private String[] sbelegung = {"./.","./.","./.","./.","./.","./.","./."};
	private int aktSet = 0; //Welches Set soll dargestellt werden
 
	private Vector<Object> aSpaltenDaten = new Vector<Object>();  //nimmt die Termindaten auf  
	private Vector vTerm = new Vector();  //  Wird gebraucht zur Datenübergabe auf die Spalten
	private boolean updateverbot = false;

	
	private boolean shiftGedrueckt;
	private boolean ctrlGedrueckt;
	private boolean altGedrueckt;	
	private Point dragDaten = new Point(0,0);
	private String[] dragInhalt = {null,null,null,null,null};
	public boolean dragStart = false;
	private boolean dragAllowed = true;
	//public DragPanel dragPanel = null;
	private Point dragPosition = new Point(0,0);
	private int[] dragStartObject = {-1,-1};
	private TDragObjekt tdragObjekt = new TDragObjekt();
	private int[] aktiveSpalte = {0,0,0,0}; //zur Positionsbestimmung Spalte, Block, aktiver Block etc.
	private int[] altaktiveSpalte = {-1,-1,-1,-1}; //zur Positionsbestimmung Spalte, Block, aktiver Block etc.
	boolean verschieben = false;
	private String aktuellerTag;
	private String wocheAktuellerTag = "";
	private String wocheErster = "";
	private int wocheBehandler;
	private int maskenBehandler;
	public int swSetWahl = -1;
	final int NORMAL_ANSICHT = 0;
	final int WOCHEN_ANSICHT = 1;
	final int MASKEN_ANSICHT = 2;
	public String[] terminangaben = {"" /*Name*/, "" /*RezeptNr.*/ , "" /*Startzeit*/ , "" /*Dauer*/, "" /*Endzeit*/, "" /*BlockNr.*/}; 
	public String[] terminrueckgabe = {"" /*Name*/, "" /*RezeptNr.*/ , "" /*Startzeit*/ , "" /*Dauer*/, "" /*Endzeit*/, "" /*BlockNr.*/};
	
	public int focus[] = {0,0};
	public boolean hasFocus = false;
	private RehaTPEventClass xEvent;
	
	private static String lockStatement = "";
	public Statement privstmt = null;
	public String lastStatement = "";
	private int lockok = 0;
	private String lockowner = "";
	private String lockspalte="";
	private String lockmessage="";
	private String lockedRecord = "";
	private String [] spaltenDatum = {null,null,null,null,null,null,null};
	
	private String [] datenSpeicher = {null,null,null,null,null};
	private int dialogRetInt;
	private String[] dialogRetData = {null,null};
	
	private boolean gruppierenAktiv = false;
	private int[] gruppierenBloecke = {-1,-1};
	private int gruppierenBehandler;
	private int gruppierenSpalte;	
	private int[] gruppierenClipBoard = {-1,-1,-1,-1};
	private boolean gruppierenKopiert = false;
	
	public int iPixelProMinute = 0;
	float fPixelProMinute = 0.f;

	private ArrayList<String[]> terminVergabe = new ArrayList<String[]>();
	
	public Thread db_Aktualisieren;
	public boolean indrag = false;
	public static boolean inTerminDrag = false;
	public String datGewaehlt = null;
	public boolean wartenAufReady = false;
	
	public boolean intagWahl = false;
	public boolean interminEdit = false;
	
	public JLabel[] dragLab = {null,null,null,null,null,null,null};
	public JRtaTextField draghandler = new JRtaTextField("GROSS",false);
	public DragAndMove dragAndMove = null;
	public HashMap<String,String> hmDragSource = new HashMap<String,String>();
	public static int DRAG_COPY = 0;
	public static int DRAG_MOVE = 1;
	public static int DRAG_UNKNOWN = 2;
	public static int DRAG_NONE = -1;
	public static int DRAG_MODE = -1;
	public String DRAG_UHR = "";
	public String DRAG_PAT = "";	
	public String DRAG_NUMMER = "";
	public boolean terminGedropt = false;
	public boolean setFromMouse = false;
	public boolean terminBreak = false;
	public JRehaInternal eltern;
	
	FinalGlassPane fgp = null;
	
	public JXPanel init(int setOben,int ansicht,JRehaInternal eltern) {

		this.eltern = eltern;
		this.setOben = setOben;
		this.ansicht = ansicht;
		xEvent = new RehaTPEventClass();
		xEvent.addRehaTPEventListener((RehaTPEventListener)this);
		
		ViewPanel = new JXPanel(new BorderLayout());
		ViewPanel.addFocusListener(new java.awt.event.FocusAdapter() {   
			public void focusLost(java.awt.event.FocusEvent e) {    
				//Reha.thisClass.shiftLabel.setText("VP Focus weg");
			
			}
			public void focusGained(java.awt.event.FocusEvent e) {
				//Reha.thisClass.shiftLabel.setText("VP Focus da");
			
			}
		});
		ViewPanel.setName(eltern.getName());

		GrundFlaeche = getGrundFlaeche();
		GrundFlaeche.addFocusListener(new java.awt.event.FocusAdapter() {   
			public void focusLost(java.awt.event.FocusEvent e) {    
				//Reha.thisClass.shiftLabel.setText("GF Focus weg");
			}
			public void focusGained(java.awt.event.FocusEvent e) {
				//Reha.thisClass.shiftLabel.setText("GF Focus da");
				
				holeFocus();
			}
		});

		ViewPanel.add(GrundFlaeche,BorderLayout.CENTER);
		ViewPanel.setBackground(SystemConfig.KalenderHintergrund);
		/****************************/
		DropTarget dndt = new DropTarget();
		try {
			dndt.addDropTargetListener(this);
		} catch (TooManyListenersException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		TerminFlaeche.setDropTarget(dndt);
	
		/****************************/
		setCombos();

		
		this.aktuellerTag = DatFunk.sHeute();
		if(this.ansicht < MASKEN_ANSICHT){
			String sstmt = ansichtStatement(this.ansicht,this.aktuellerTag);
			macheStatement(sstmt,this.ansicht);
		}else{
			String stmtmaske = "select from masken where behandler = '00BEHANDLER' ORDER BY art";
			maskenStatement(stmtmaske);
		}
    	

		ViewPanel.addFocusListener(new java.awt.event.FocusAdapter() {   
			public void focusLost(java.awt.event.FocusEvent e) {    
				//Reha.thisClass.shiftLabel.setText("VP Focus weg");
		
			}
			public void focusGained(java.awt.event.FocusEvent e) {
				//Reha.thisClass.shiftLabel.setText("VP Focus da");
				((JRehaInternal)Reha.thisClass.terminpanel.eltern).feuereEvent(25554);
				((JRehaInternal)Reha.thisClass.terminpanel.eltern).feuereEvent(25554);
				if(! Reha.thisClass.terminpanel.eltern.isSelected()){
					try {
						Reha.thisClass.terminpanel.eltern.setSelected(true);
					} catch (PropertyVetoException e1) {
						e1.printStackTrace();
					}
				}
				holeFocus();
			}
		});


		GrundFlaeche.revalidate();
		ViewPanel.revalidate();
		
		if(ansicht == WOCHEN_ANSICHT){
			this.ansicht = NORMAL_ANSICHT;
			oCombo[0].setSelectedItem(SystemConfig.KalenderStartWADefaultUser);
			setWochenanzeige();
		}else if (ansicht == NORMAL_ANSICHT){
			int lang = SystemConfig.aTerminKalender.size();
			int pos = -1;
			for(int i=0;i<lang;i++){
				if (SystemConfig.aTerminKalender.get(i).get(0).contains(SystemConfig.KalenderStartNADefaultSet)) {
					pos = i;
					break;
				}
			}
			// Nur wenn Set-Name gefunden z.B. ! ./. für kein Set als Voreinstellung
			if(pos >= 0){
				this.aktSet = pos;
				String[] sSet;
				sSet = ((ArrayList<String[]>)SystemConfig.aTerminKalender.get(this.aktSet).get(1)).get(0);
				for(int i = 0;i <7;i++){
					oCombo[i].setSelectedItem(sSet[i]);					
				}
			}
		}

		setzeStatement();

		if(SystemConfig.KalenderZeitLabelZeigen){
			fgp = new FinalGlassPane(eltern);
			eltern.setGlassPane(fgp);
		}
		getDatenVonExternInSpeicherNehmen();
		return ViewPanel;

	}
	public void regleZeitLabel(){
		if(!SystemConfig.KalenderZeitLabelZeigen){
			fgp.setVisible(false);
			fgp = null;
		}else if(SystemConfig.KalenderZeitLabelZeigen){
			if(fgp == null){
				fgp = new FinalGlassPane(eltern);
				eltern.setGlassPane(fgp);
			}
		}
	}
	public JXPanel getTerminFlaecheFromOutside(){
		return TerminFlaeche;
	}
	public void finalise(){
		vTerm.clear();
		vTerm = null;
		for(int i = 0; i < 7;i++){
			ListenerTools.removeListeners(oSpalten[i]);
			oSpalten[i] = null;
			ListenerTools.removeListeners(oCombo[i]);
			oCombo[i] = null;
			if(ViewPanel != null){
				ListenerTools.removeListeners(ViewPanel);
				ViewPanel = null;
			}
			if(ComboFlaeche != null){
				ListenerTools.removeListeners(ComboFlaeche);
				ComboFlaeche = null;
			}
			if(TerminFlaeche != null){
				ListenerTools.removeListeners(TerminFlaeche);
				TerminFlaeche = null;
			}
			if(GrundFlaeche != null){
				ListenerTools.removeListeners(GrundFlaeche);
				GrundFlaeche = null;
			}
		}
	}
	private void setzeStatement(){
		try {
			this.privstmt = Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE); //ResultSet.CONCUR_READ_ONLY);//
			this.privstmt.setQueryTimeout(1);		


		}catch(SQLException ex) {
			SqlInfo.loescheLocksMaschine();
		}
		if(SystemConfig.UpdateIntervall > 0 && this.ansicht < 2){
			db_Aktualisieren = new Thread(new sperrTest());
			db_Aktualisieren.start();
		}else if(this.ansicht == 2){

		}
	}
	private JXPanel	getGrundFlaeche(){
		if(GrundFlaeche == null){
			GridBagConstraints gridBagConstraints0 = new GridBagConstraints() ;
			gridBagConstraints0.gridx = 0;
			gridBagConstraints0.weighty = 150.0D;
			gridBagConstraints0.weightx = 1.0D;
			gridBagConstraints0.fill = GridBagConstraints.BOTH;
			gridBagConstraints0.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints0.gridwidth = 0;
			gridBagConstraints0.gridheight = 1;
			gridBagConstraints0.anchor = GridBagConstraints.CENTER;
			gridBagConstraints0.gridy = 1;
			gridBagConstraints0.gridx = 0;

			GridBagConstraints gridBagConstraints1 = new GridBagConstraints() ;
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.weighty = 1.0D;
			gridBagConstraints1.weightx = 1.0D;
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints1.gridwidth = 0;
			gridBagConstraints1.gridheight = 1;
			gridBagConstraints1.anchor = GridBagConstraints.NORTH;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.gridx = 0;

			GrundFlaeche = new JXPanel();
			GrundFlaeche.setDoubleBuffered(true);
			GrundFlaeche.setBorder(null);
			GrundFlaeche.setBackground(SystemConfig.KalenderHintergrund);

			GrundFlaeche.setLayout(new GridBagLayout());
			GrundFlaeche.add(getComboFlaeche(),gridBagConstraints1);
			GrundFlaeche.add(getTerminFlaeche(),gridBagConstraints0);	

		}
		return GrundFlaeche;
	}
	
	public String getAktuellerTag(){
		return this.aktuellerTag;
	}

	private JXPanel getComboFlaeche(){
		if(ComboFlaeche==null){
			DropShadowBorder dropShadow = new DropShadowBorder(Color.BLACK, 5, 1, 3, true, true, false, true);
			ComboFlaeche = new JXPanel();
			ComboFlaeche.setBackground(SystemConfig.KalenderHintergrund);
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(1);
			ComboFlaeche.setLayout(gridLayout);
			ComboFlaeche.setDoubleBuffered(true);
			ComboFlaeche.setBorder(null);
			JXPanel cb = null;
			for(int i = 0;i<7;i++){
				cb = new JXPanel(new BorderLayout());
				cb.setBorder(dropShadow);
				cb.setBackground(SystemConfig.KalenderHintergrund);
				oCombo[i] = new JComboBox();
				oCombo[i].setName("Combo"+i);
				cb.add(oCombo[i],BorderLayout.CENTER);
				ComboFlaeche.add(cb);
			}
			ComboFlaeche.revalidate();	
		}	
		return ComboFlaeche;
	}
/***
 * Jetzt die Listener f�r die Combos installieren
 * 
 */
	private void comboListenerInit(final int welche){
		oCombo[welche].setPopupVisible(false);	
		oCombo[welche].addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent e) {
				for(int i=0;i<1;i++){
					if (e.isControlDown()){ctrlGedrueckt=true;}
					if (e.isAltDown()){altGedrueckt=true;}
					if(!e.isControlDown()){
						ctrlGedrueckt=false;
					}
					if(!e.isAltDown()){
						altGedrueckt=false;
					}
					if ( (e.getKeyCode()==33 || e.getKeyCode()==34) && (ansicht < MASKEN_ANSICHT) ){
						e.consume();
						//Neuer Tag soll gewählt werden
						panelTastenAuswerten(e);
						break;
					}
					if (e.getKeyCode()==38 || e.getKeyCode()==40){
						if(!oCombo[welche].isPopupVisible()){
							oCombo[welche].setPopupVisible(false);
						}
						break;
					}
					if ((e.getKeyCode()==123) && (ansicht < MASKEN_ANSICHT) ){
						setAufruf(null);
						aktiveSpalte[2] = welche;
						oSpalten[welche].requestFocus();
						break;
					}
					if (e.getKeyCode()==27){
						aktiveSpalte[2] = welche;
						oSpalten[welche].requestFocus();
						e.consume();
						break;
					}
					if ( (e.getKeyCode()==76) && (e.isControlDown()) && (ansicht < MASKEN_ANSICHT) ){
						terminListe();
						break;
					}
					if (e.getKeyCode()==122 && (!e.isShiftDown()) && (ansicht < MASKEN_ANSICHT) ){
						//F11 ohne Shift
						schnellSuche();
						break;
					}
					if(e.getKeyCode()==68 && e.isControlDown()){
						//Terminplan drucken
						DruckeViewPanel dvp = new DruckeViewPanel();
						dvp.setPrintPanel((JXPanel) Reha.thisClass.terminpanel.ViewPanel);
						break;	
					}
					if ( (e.getKeyCode()==87) && (e.isControlDown()) ){
						//Wochenansicht
						setWochenanzeige();
						break;
					}
					if ( (e.getKeyCode()==78) && (e.isControlDown()) ){
						//Normalansicht
						setNormalanzeige();
						break;
					}
					if(e.getKeyCode()==80 && e.isAltDown()){
						doPatSuchen();
					}
					
				}	
				
			}
		});
		oCombo[welche].addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				int wahl = ParameterLaden.suchen((String)oCombo[welche].getSelectedItem());							
				if(!oCombo[welche].isPopupVisible()){
					oCombo[welche].setPopupVisible(false);
				}
				if (ansicht == NORMAL_ANSICHT){
					try{
						belegung[welche] = ParameterLaden.vKKollegen.get(wahl).Reihe -1 ;
						oSpalten[welche].datenZeichnen(vTerm,belegung[welche]);
						oSpalten[aktiveSpalte[2]].requestFocus();
						if (welche==0){
							wochenbelegung = ParameterLaden.vKKollegen.get(wahl).Reihe ;
						}

					}catch(java.lang.ArrayIndexOutOfBoundsException ex){
						SqlInfo.loescheLocksMaschine();
					}
				}else if(ansicht == WOCHEN_ANSICHT){
					try{
						if (welche==0){
							wochenbelegung = ParameterLaden.vKKollegen.get(wahl).Reihe ;
							if(wocheErster.equals("")){
								ansichtStatement(ansicht,aktuellerTag);
							}else{
								ansichtStatement(ansicht,wocheErster);							
							}						
						}
					}catch(java.lang.ArrayIndexOutOfBoundsException ex){
						SqlInfo.loescheLocksMaschine();
					}

				}else if(ansicht == MASKEN_ANSICHT){
		
					maskenbelegung = ParameterLaden.vKKollegen.get(wahl).Reihe ;
					maskenwahl = wahl;
					String maskenbehandler = (maskenbelegung < 10 ? "0"+maskenbelegung+"BEHANDLER" : Integer.toString(maskenbelegung)+"BEHANDLER");
					String stmtmaske = "select * from masken where behandler = '"+maskenbehandler+"' ORDER BY art";
					////System.out.println(stmtmaske);
					maskenStatement(stmtmaske);
					
				}
			}
		});
		oCombo[welche].addFocusListener(new java.awt.event.FocusAdapter() {   
			public void focusLost(java.awt.event.FocusEvent e) {    
				focusHandling(0,-1);
			}
			public void focusGained(java.awt.event.FocusEvent e) {
				focusHandling(0,1);
				try{
					if(!Reha.thisClass.terminpanel.eltern.isActive){
						Reha.thisClass.terminpanel.eltern.feuereEvent(25554);
					}
				}catch(Exception ex){
					
				}
			}
		});
		oCombo[welche].addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				//String arg = ((String) ((JComboBox) e.getSource()).getActionCommand());
				((JComboBox)e.getSource()).setToolTipText("Kalenderbenutzer: "+oCombo[welche].getSelectedItem());
			}
			
		});
		
		oCombo[welche].addPopupMenuListener( new PopupMenuListener() {
			@Override
			public void popupMenuCanceled(PopupMenuEvent arg0) {
				((JComboBox)arg0.getSource()).setToolTipText("Cancel-Kalenderbenutzer: "+oCombo[welche].getSelectedItem());
			}
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
				((JComboBox)arg0.getSource()).setToolTipText("Kalenderbenutzer: "+oCombo[welche].getSelectedItem());				
			}
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
				((JComboBox)arg0.getSource()).setToolTipText("Become-Invisible Kalenderbenutzer: "+oCombo[welche].getSelectedItem());				
			}
	      });
	}	
/****
 * die Comboboxen mit Werden füllen
 * 	
 */
	public void setCombosOutside(){
		int von = 0;
		int bis = ParameterLaden.vKKollegen.size();
		for(int i = 0; i < 7; i++){
			oCombo[i].removeAllItems();
		}
		for(von=0; von < bis; von++){
			oCombo[0].addItem(ParameterLaden.vKKollegen.get(von).Matchcode);
			oCombo[1].addItem(ParameterLaden.vKKollegen.get(von).Matchcode);
			oCombo[2].addItem(ParameterLaden.vKKollegen.get(von).Matchcode);			
			oCombo[3].addItem(ParameterLaden.vKKollegen.get(von).Matchcode);
			oCombo[4].addItem(ParameterLaden.vKKollegen.get(von).Matchcode);
			oCombo[5].addItem(ParameterLaden.vKKollegen.get(von).Matchcode);			
			oCombo[6].addItem(ParameterLaden.vKKollegen.get(von).Matchcode);
		}
		if(ansicht < MASKEN_ANSICHT){
			oCombo[0].setMaximumRowCount( 35 ); oCombo[0].setSelectedItem( "./." ); 
			oCombo[1].setMaximumRowCount( 35 ); oCombo[1].setSelectedItem( "./." );			
			oCombo[2].setMaximumRowCount( 35 ); oCombo[2].setSelectedItem( "./." );
			oCombo[3].setMaximumRowCount( 35 ); oCombo[3].setSelectedItem( "./." );
			oCombo[4].setMaximumRowCount( 35 ); oCombo[4].setSelectedItem( "./." );		
			oCombo[5].setMaximumRowCount( 35 ); oCombo[5].setSelectedItem( "./." );
			oCombo[6].setMaximumRowCount( 35 ); oCombo[6].setSelectedItem( "./." );
		}else if(ansicht == MASKEN_ANSICHT){
			oCombo[0].setMaximumRowCount( 35 ); oCombo[0].setSelectedItem( "./." ); 
			oCombo[1].setMaximumRowCount( 35 ); oCombo[1].setSelectedItem( "./." );			
			oCombo[2].setMaximumRowCount( 35 ); oCombo[2].setSelectedItem( "./." );
			oCombo[3].setMaximumRowCount( 35 ); oCombo[3].setSelectedItem( "./." );
			oCombo[4].setMaximumRowCount( 35 ); oCombo[4].setSelectedItem( "./." );		
			oCombo[5].setMaximumRowCount( 35 ); oCombo[5].setSelectedItem( "./." );
			oCombo[6].setMaximumRowCount( 35 ); oCombo[6].setSelectedItem( "./." );
			oCombo[1].setEnabled(false);			
			oCombo[2].setEnabled(false);
			oCombo[3].setEnabled(false);
			oCombo[4].setEnabled(false);		
			oCombo[5].setEnabled(false);
			oCombo[6].setEnabled(false);
		}
	}
	
	public void setCombos(){
		int von = 0;
		int bis = ParameterLaden.vKKollegen.size();
		for(von=0; von < bis; von++){
			oCombo[0].addItem(ParameterLaden.vKKollegen.get(von).Matchcode);
			oCombo[1].addItem(ParameterLaden.vKKollegen.get(von).Matchcode);
			oCombo[2].addItem(ParameterLaden.vKKollegen.get(von).Matchcode);			
			oCombo[3].addItem(ParameterLaden.vKKollegen.get(von).Matchcode);
			oCombo[4].addItem(ParameterLaden.vKKollegen.get(von).Matchcode);
			oCombo[5].addItem(ParameterLaden.vKKollegen.get(von).Matchcode);			
			oCombo[6].addItem(ParameterLaden.vKKollegen.get(von).Matchcode);
		}
		if(this.ansicht < MASKEN_ANSICHT){
			oCombo[0].setMaximumRowCount( 35 ); oCombo[0].setSelectedItem( "./." ); 
			oCombo[1].setMaximumRowCount( 35 ); oCombo[1].setSelectedItem( "./." );			
			oCombo[2].setMaximumRowCount( 35 );	oCombo[2].setSelectedItem( "./." );
			oCombo[3].setMaximumRowCount( 35 );	oCombo[3].setSelectedItem( "./." );
			oCombo[4].setMaximumRowCount( 35 );	oCombo[4].setSelectedItem( "./." );		
			oCombo[5].setMaximumRowCount( 35 );	oCombo[5].setSelectedItem( "./." );
			oCombo[6].setMaximumRowCount( 35 );	oCombo[6].setSelectedItem( "./." );
		}else if(this.ansicht == MASKEN_ANSICHT){
			oCombo[0].setMaximumRowCount( 35 ); oCombo[0].setSelectedItem( "./." ); 
			oCombo[1].setMaximumRowCount( 35 ); oCombo[1].setSelectedItem( "./." );			
			oCombo[2].setMaximumRowCount( 35 );	oCombo[2].setSelectedItem( "./." );
			oCombo[3].setMaximumRowCount( 35 );	oCombo[3].setSelectedItem( "./." );
			oCombo[4].setMaximumRowCount( 35 );	oCombo[4].setSelectedItem( "./." );		
			oCombo[5].setMaximumRowCount( 35 );	oCombo[5].setSelectedItem( "./." );
			oCombo[6].setMaximumRowCount( 35 );	oCombo[6].setSelectedItem( "./." );
			oCombo[1].setEnabled(false);			
			oCombo[2].setEnabled(false);
			oCombo[3].setEnabled(false);
			oCombo[4].setEnabled(false);		
			oCombo[5].setEnabled(false);
			oCombo[6].setEnabled(false);
		}
		/**jetzt noch die Listener initialisieren**/
		for(int i = 0; i < 7; i++){
			comboListenerInit(i);
		}
	}
	
/****
 * 	
 * @return
 */
	private JXPanel	getTerminFlaeche(){
		if(TerminFlaeche==null){
			TerminFlaeche = new JXPanel();
			TerminFlaeche.setBackground(SystemConfig.KalenderHintergrund);
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(1);
			TerminFlaeche.setLayout(gridLayout);
			TerminFlaeche.setBorder(null);
			JXPanel cb = null;

			for(int i = 0;i<7;i++){
				cb = new JXPanel(new BorderLayout());
				cb.setBorder(null);
				cb.setBackground(SystemConfig.KalenderHintergrund);
				oSpalten[i] =  new kalenderPanel();
				oSpalten[i].setName("Spalte"+i);
				oSpalten[i].setDoubleBuffered(true);
				oSpalten[i].setAlpha(SystemConfig.KalenderAlpha);

				dragLab[i] = new JLabel();
				dragLab[i].setName("draLab-"+i);
				dragLab[i].setForeground(SystemConfig.aktTkCol.get("aktBlock")[1]);
				dragLab[i].setBounds(0,0,oSpalten[i].getWidth(),oSpalten[i].getHeight());
				dragLab[i].addMouseListener(new MouseAdapter() {
					
					public void mousePressed(MouseEvent e) {
					      String[] sdaten = datenInDragSpeicherNehmen();
					      if(sdaten[0]==null){
					    	  return;
					      }
					      if(!Rechte.hatRecht(Rechte.Kalender_termindragdrop, false)){
					    	  return;
					      }
					      dragStart = true;
					      if(e.isAltDown()){
					    	  DRAG_MODE = DRAG_MOVE;
					      }else if(e.isControlDown()){
					    	  DRAG_MODE = DRAG_COPY;
					      }else{
					    	  DRAG_MODE = DRAG_NONE;
					      }
					      int behandler=-1;
					      if(ansicht==NORMAL_ANSICHT){
					    	  behandler = belegung[aktiveSpalte[2]];
					      }else if(ansicht==WOCHEN_ANSICHT){
					    	  behandler = aktiveSpalte[2];
					      }else if(ansicht==MASKEN_ANSICHT){
					    	  behandler = aktiveSpalte[2];
					      }
					      if(behandler <= -1){
					    	  return;
					      }
					      DRAG_PAT = 	 ((String) ((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(0)).get(aktiveSpalte[0]) ).replaceAll("\u00AE"  , "");
					      DRAG_NUMMER = 	 (String) ((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(1)).get(aktiveSpalte[0]) ;
					      DRAG_UHR =   (String) ((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(2)).get(aktiveSpalte[0]) ;
					      altaktiveSpalte = aktiveSpalte.clone();
					      //JLabel lab = new JLabel("TERMDATINTERN"+"°"+sdaten[0]+"°"+sdaten[1]+"°"+sdaten[3]+" Min.");
					      JLabel lab = new JLabel("TERMDATINTERN"+"°"+sdaten[0]+"°"+sdaten[1]+"°"+sdaten[3]+" Min."+"°"+DRAG_UHR);
					      lab.setTransferHandler(new TransferHandler("text"));
					      TransferHandler th = lab.getTransferHandler();
					      th.exportAsDrag(lab, e, TransferHandler.COPY);
					      
					}
					public void mouseReleased(MouseEvent e) {
					      JComponent c = (JComponent)e.getSource();
					      int v = Integer.valueOf(c.getName().split("-")[1]);
					      dragLab[v].setText("");
					      dragLab[v].setIcon(null);
					      dragStart = false;
					      oSpalten[v].repaint();
						}
				  });
				oSpalten[i].add(dragLab[i]);
				
				PanelListenerInit(i);
				oSpalten[i].ListenerSetzen(i);
				cb.add(oSpalten[i],BorderLayout.CENTER);
				TerminFlaeche.add(cb);
			}
			TerminFlaeche.addComponentListener(new java.awt.event.ComponentAdapter() {
				public void componentResized(java.awt.event.ComponentEvent e) {
					//GroesseSetzen();
					oSpalten[0].zeitSpanne();
					oSpalten[1].zeitSpanne();
					oSpalten[2].zeitSpanne();
					oSpalten[3].zeitSpanne();					
					oSpalten[4].zeitSpanne();
					oSpalten[5].zeitSpanne();
					oSpalten[6].zeitSpanne();		
					GrundFlaeche.revalidate();
					int iMaxHoehe = TerminFlaeche.getHeight();
					fPixelProMinute = (float) iMaxHoehe;
					fPixelProMinute =(float) fPixelProMinute / 900;
					iPixelProMinute = ((int) (fPixelProMinute));
				}
			});
			TerminFlaeche.revalidate();
		}
		return TerminFlaeche;
	}
	public float getPanelPixelProMinute(){
		return oSpalten[0].getPixels();
	}
	
/**
* Hier werden s�mtliche Listener f�r die Kalenderspalten installiert
*
*
*/
	public static void setDurchlass(float alf){
		try{
			if(! (Reha.thisClass.terminpanel.oSpalten == null)){
				for(int i = 0; i < 7;i++){
					Reha.thisClass.terminpanel.oSpalten[i].setAlpha(alf);
					Reha.thisClass.terminpanel.oSpalten[i].setBackground(SystemConfig.KalenderHintergrund);	
					Reha.thisClass.terminpanel.oSpalten[i].repaint();
					Reha.thisClass.terminpanel.oCombo[i].getParent().setBackground(SystemConfig.KalenderHintergrund);
				}
				Reha.thisClass.terminpanel.ViewPanel.setBackground(SystemConfig.KalenderHintergrund);
				Reha.thisClass.terminpanel.GrundFlaeche.setBackground(SystemConfig.KalenderHintergrund);
				Reha.thisClass.terminpanel.ComboFlaeche.setBackground(SystemConfig.KalenderHintergrund);
				Reha.thisClass.terminpanel.TerminFlaeche.setBackground(SystemConfig.KalenderHintergrund);
				Reha.thisClass.terminpanel.ViewPanel.validate();
				Reha.thisClass.terminpanel.ViewPanel.repaint();
			}
		}catch(java.lang.NullPointerException n){
			//fare niente - Terminkalender läuft nicht!!
		}
		
	}
	public void altCtrlAus(){
		ctrlGedrueckt = false;
		altGedrueckt = false;
	}
	private void PanelListenerInit(final int tspalte){

			oSpalten[tspalte].addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyPressed(java.awt.event.KeyEvent e) {
					int ec = e.getKeyCode();
					for(int i = 0;i < 1;i++){
						if(!e.isControlDown()){
							ctrlGedrueckt=false;
						}
						if(!e.isAltDown()){
							altGedrueckt=false;
						}
						if(!e.isShiftDown()){
							shiftGedrueckt=false;
						}
						if (ec==17){
							ctrlGedrueckt=true;
							break;
						}
						if (ec==18){
							altGedrueckt=true;
							break;
						}
						if(ec==68 && e.isControlDown()){
							DruckeViewPanel dvp = new DruckeViewPanel();
							dvp.setPrintPanel((JXPanel) Reha.thisClass.terminpanel.ViewPanel);
							break;	
						}
						if ( (ec==123) && (ansicht != MASKEN_ANSICHT) ){
							setAufruf(null);
							oSpalten[tspalte].requestFocus();
							break;
						}
						/*********
						 * 
						 * 
						 */
						if ( (e.getKeyCode()==155 && e.isShiftDown()) || (e.getKeyCode()==KeyEvent.VK_V && e.isControlDown()) ) {
							//Shift einfügen (Shift+Einfg / Strg+V)
							//Daten in den Kalender schreiben (früher Aufruf über F3)
							long zeit = System.currentTimeMillis();
							boolean grobRaus = false;
							while(wartenAufReady){
								try {
									Thread.sleep(20);
									if( (System.currentTimeMillis()-zeit) > 1500){
										grobRaus = true;
										break;
									}
								} catch (InterruptedException e1) {
									e1.printStackTrace();
								}
							}
							if(!grobRaus){
								wartenAufReady = true;
								terminGedropt = false;
								terminBreak = false;
								datenAusSpeicherHolen();								
							}else{
								wartenAufReady = false;
								SqlInfo.loescheLocksMaschine();
							}
							
							shiftGedrueckt = false;
							gruppierenAktiv = false;
							gruppierenBloecke[0] = -1;
							gruppierenBloecke[1] = -1;	
							oSpalten[gruppierenSpalte].setInGruppierung(false);
							oSpalten[tspalte].requestFocus();							
							break;
						}
						
						if ( ((e.getKeyCode()==155) && (e.isControlDown())) || ((e.getKeyCode()==KeyEvent.VK_C) && (e.isControlDown()))  ) {
							//Daten in Speicher (früher Aufruf über F3)
							////System.out.println("Strg+Einfg / Strg+C");
							int xaktBehandler  = -1;
							datenInSpeicherNehmen();
							if(terminVergabe.size() > 0){
								terminVergabe.clear();
							}
							if(ansicht == NORMAL_ANSICHT){
								xaktBehandler = belegung[aktiveSpalte[2]];
							}else  if(ansicht == WOCHEN_ANSICHT){
								xaktBehandler = aktiveSpalte[2]; 
							}else  if(ansicht == MASKEN_ANSICHT){
								xaktBehandler = aktiveSpalte[2];
							}
							terminAufnehmen(xaktBehandler,aktiveSpalte[0]);
							break;
						}
						
						/********
						 * 
						 * 
						 * 
						 */
						if (ec==16){
							if(!Rechte.hatRecht(Rechte.Kalender_termingroup, false)){
								////System.out.println("Rückgabewert von hatRechte(termingroup) = "+Rechte.hatRecht(Rechte.Kalender_termingroup, false));
								shiftGedrueckt = true;
								gruppierenAktiv = false;
								oSpalten[tspalte].shiftGedrueckt(true);
								oSpalten[gruppierenSpalte].setInGruppierung(false);
								gruppierenBloecke[0] = -1;
								gruppierenBloecke[1] = -1;	
								break;
							}
							shiftGedrueckt = true;
							if(!gruppierenAktiv){
								gruppierenAktiv = true;
								gruppierenBloecke[0] = aktiveSpalte[0];
								gruppierenBloecke[1] = aktiveSpalte[0];
								gruppierenSpalte = aktiveSpalte[2];
								gruppierenBehandler = (ansicht==NORMAL_ANSICHT ? belegung[aktiveSpalte[2]] : aktiveSpalte[2]);
								gruppierenKopiert = false;
								oSpalten[gruppierenSpalte].setInGruppierung(true);
							}
							oSpalten[tspalte].shiftGedrueckt(true);
							break;
						}
						if (ec==27){
							oCombo[tspalte].requestFocus();
							e.consume();
							break;
						}
						if ((ec==33 || ec==38 || ec==34 ||  ec==40 || ec==10 || ec ==37 || ec == 39)
								&& (!e.isControlDown() && (!e.isAltDown())) && (!e.isShiftDown()) ){
						//HauptAufgabe ist Weitergabe an Tastenauswerten
							e.consume();
							panelTastenAuswerten(e);
							oSpalten[tspalte].requestFocus();
							break;
						}
						if( ( ec==38 || ec==40 )
								&& (!e.isControlDown() && (!e.isAltDown())) && (e.isShiftDown()) ){
						//HauptAufgabe ist Weitergabe und Tastenauswerten
							//gruppierungMalen();
							panelTastenAuswerten(e);
							oSpalten[tspalte].requestFocus();
							break;
						}
						if ( (e.getKeyCode()==87) && (e.isControlDown()) ){
							//Wochenansicht
							setWochenanzeige();
							break;
						}
						if ( (e.getKeyCode()==78) && (e.isControlDown()) ){
							//Normalansicht
							setNormalanzeige();
							break;
						}
						if(ec==80 && e.isAltDown()){
							doPatSuchen();
						}
						/*****************/
						//ursprünglich an dieser Stelle
						//
						/*****************/
						/**********************************************/
						if ( (e.getKeyCode()==76) && (e.isControlDown()) ){
							terminListe();
							break;
						}
						if (e.getKeyCode()==112){
							//F1
						}
						if (e.getKeyCode()==113){
							//F2
							long zeit = System.currentTimeMillis();
							boolean grobRaus = false;
							while(wartenAufReady){
								try {
									Thread.sleep(20);
									if( (System.currentTimeMillis()-zeit) > 1500){
										grobRaus = true;
										break;
									}
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
							if(!grobRaus){
								wartenAufReady = true;
								terminGedropt = false;
								terminBreak = false;
								datenAusSpeicherHolen();								
							}else{
								SqlInfo.loescheLocksMaschine();
								wartenAufReady = false;						
							}
							shiftGedrueckt = false;
							gruppierenAktiv = false;
							gruppierenBloecke[0] = -1;
							gruppierenBloecke[1] = -1;	
							oSpalten[gruppierenSpalte].setInGruppierung(false);
							oSpalten[tspalte].requestFocus();							
							break;
						}
						if (e.getKeyCode()==114){
							//F3 = Daten in Speicher nehmen
							int xaktBehandler  = -1;
							datenInSpeicherNehmen();
							if(terminVergabe.size() > 0){
								terminVergabe.clear();
							}
							if(ansicht == NORMAL_ANSICHT){
								xaktBehandler = belegung[aktiveSpalte[2]];
							}else  if(ansicht == WOCHEN_ANSICHT){
								xaktBehandler = aktiveSpalte[2]; 
							}else  if(ansicht == MASKEN_ANSICHT){
								xaktBehandler = aktiveSpalte[2];
							}
							terminAufnehmen(xaktBehandler,aktiveSpalte[0]);

							break;
						}
						if ((e.getKeyCode()==118) && (e.isAltDown()) && (e.isShiftDown())){
							//F7 + Schift + Alt = Radikalkur!!!!!!!
							if(!Rechte.hatRecht(Rechte.Kalender_termindelete, true)){
								wartenAufReady = false;
								shiftGedrueckt = false;
								gruppierenAktiv = false;
								e.consume();
								oSpalten[tspalte].requestFocus();
								break;
							}
							blockSetzen(999);
							e.consume();
							oSpalten[tspalte].requestFocus();
							break;
						}
						if (e.getKeyCode()==118){
							//F7
							break;
						}
						if ( (e.getKeyCode()==119) || (e.getKeyCode()==127 && e.isShiftDown()) || (e.getKeyCode()==KeyEvent.VK_X && e.isControlDown()) ) {
							//F8 / Shift-Entf / Strg-X
							if((!Rechte.hatRecht(Rechte.Kalender_termindelete, true))){
								//getAktTestTermin("name").equals(""))
								wartenAufReady = false;
								shiftGedrueckt = false;
								gruppierenAktiv = false;
								e.consume();
								oSpalten[tspalte].requestFocus();
								break;
							}
							long zeit = System.currentTimeMillis();
							boolean grobRaus = false;
							while(wartenAufReady){
								try {
									Thread.sleep(20);
									if( (System.currentTimeMillis()-zeit) > 1500){
										grobRaus = true;
										break;
									}
								} catch (InterruptedException e1) {
									e1.printStackTrace();
								}
							}
							//System.out.println("GrobRaus = "+grobRaus);
							if(!grobRaus){
								
								//int xaktBehandler = -1;
								wartenAufReady = true;
								testeObAusmustern();
								
								/************Neu Anfang*************/
								datenInSpeicherNehmen(); 
								/*
								if(terminVergabe.size() > 0){
									terminVergabe.clear();
								}
								if(ansicht == NORMAL_ANSICHT){
									xaktBehandler = belegung[aktiveSpalte[2]];
								}else  if(ansicht == WOCHEN_ANSICHT){
									xaktBehandler = aktiveSpalte[2]; 
								}else  if(ansicht == MASKEN_ANSICHT){
									xaktBehandler = aktiveSpalte[2];
								}
								terminAufnehmen(xaktBehandler,aktiveSpalte[0]);
								*/
								/************Neu Ende*************/
								blockSetzen(11);								

							}else{
								SqlInfo.loescheLocksMaschine();
								wartenAufReady = false;								
							}


							e.consume();
							oSpalten[tspalte].requestFocus();
							break;
						}
						if (e.getKeyCode()==120){
							//F9
							if( (!Rechte.hatRecht(Rechte.Kalender_termindelete, false))){ 
								Rechte.hatRecht(Rechte.Kalender_termindelete, true);
								wartenAufReady = false;
								shiftGedrueckt = false;
								gruppierenAktiv = false;
								e.consume();
								oSpalten[tspalte].requestFocus();
								break;
								
							}
							long zeit = System.currentTimeMillis();
							boolean grobRaus = false;
							while(wartenAufReady){
								try {
									Thread.sleep(20);
									if( (System.currentTimeMillis()-zeit) > 1500){
										grobRaus = true;
										break;
									}
								} catch (InterruptedException e1) {
									e1.printStackTrace();
								}
							}
							if(!grobRaus){
								wartenAufReady = true;
								testeObAusmustern();
								blockSetzen(10);								
							}else{
								SqlInfo.loescheLocksMaschine();
								wartenAufReady = false;								
							}
							oSpalten[tspalte].requestFocus();							
							//F9
							break;
						}
						//F11 + Shift
						if (e.getKeyCode()==122 && e.isShiftDown()){
							if(!Rechte.hatRecht(Rechte.Kalender_terminconfirm, true)){
								gruppeAusschalten();
							}else{
								terminBestaetigen(tspalte,false);
								gruppeAusschalten();
							}
							oSpalten[tspalte].requestFocus();
							break;
						}
						//F11 + Strg erzwingt in jeden Fall den Dialog
						if (e.getKeyCode()==122 && e.isControlDown()){
							if(!Rechte.hatRecht(Rechte.Kalender_terminconfirm, true)){
								gruppeAusschalten();
							}else{
								terminBestaetigen(tspalte,true);
								gruppeAusschalten();
							}
							oSpalten[tspalte].requestFocus();
							break;
						}
						
						if (e.getKeyCode()==122 && (!e.isShiftDown()) && (!e.isControlDown())
								&& (!e.isAltDown())){
							//nur F11 ohne Shift etc.
							schnellSuche();
							break;
						}
						if(e.isControlDown() && ec == 79){
							//Termin nach oben
							setUpdateVerbot(true);
							tauscheTermin(-1);
							setUpdateVerbot(false);
							break;
						}
						if(e.isControlDown() && ec == 85){
							//Termin nach unten
							setUpdateVerbot(true);
							tauscheTermin(1);
							setUpdateVerbot(false);
							break;
						}
						oSpalten[tspalte].requestFocus();				
					}
				}
				public void keyReleased(java.awt.event.KeyEvent e) {
					if (e.getKeyCode()==17){
						ctrlGedrueckt=false;
					}
					if (e.getKeyCode()==18){
						altGedrueckt=false;
					}
					if (e.getKeyCode()==16){
						shiftGedrueckt = false;
						gruppierenAktiv = false;
						gruppierenBloecke[0] = -1;
						gruppierenBloecke[1] = -1;
						oSpalten[gruppierenSpalte].setInGruppierung(false);
						oSpalten[tspalte].shiftGedrueckt(false);
					}else{
						oSpalten[tspalte].requestFocus();				
					}
				}
			});
			
			oSpalten[tspalte].addMouseListener(new java.awt.event.MouseAdapter() { 
				
				public void mouseExited(java.awt.event.MouseEvent e) {    
				}
				public void mouseEntered(java.awt.event.MouseEvent e) {
					e.setSource(this);
				}				
				public void mouseReleased(java.awt.event.MouseEvent e) {
					dragStart = false;
				}
				public void mouseMoved(java.awt.event.MouseEvent e) {
					dragDaten.y = e.getY();
					dragDaten.x = e.getX();
					if(fgp != null){
						if(!fgp.isVisible()){
							fgp.setVisible(true);
						}
						fgp.eventDispatched(e);	
					}
					
					
					//fgp.setPoint(e.getPoint());
					//fgp.repaint();
				}				

				public void mousePressed(java.awt.event.MouseEvent e) {
					for(int i = 0; i < 1; i++){
						if ( (e.getClickCount() == 1) && (e.getButton() == java.awt.event.MouseEvent.BUTTON1) ){
							KlickSetzen(oSpalten[tspalte], e);
							oSpalten[tspalte].neuzeichnen = true;
							oSpalten[tspalte].requestFocus();
							dragLab[i].setVisible(true);
							break;
						}
						if ( (e.getClickCount() == 1) && (e.getButton() == java.awt.event.MouseEvent.BUTTON3) ){
							if(!gruppierenAktiv){
								KlickSetzen(oSpalten[tspalte], e);
								dragDaten.y = e.getY();
								dragDaten.x = e.getX();
								ZeigePopupMenu(e);
								break;
							}else{
								dragDaten.y = e.getY();
								dragDaten.x = e.getX();
								ZeigePopupMenu(e);
								oSpalten[tspalte].requestFocus();
								break;
							}
						}
						if ( (e.getClickCount() == 2) && (e.getButton() == java.awt.event.MouseEvent.BUTTON1) ){
							final java.awt.event.MouseEvent me = e;
							SwingUtilities.invokeLater(new Runnable(){
							 	   public  void run()
							 	   {
							 		   KlickSetzen(oSpalten[tspalte], me);
							 	   }
							}); 	   
							dragDaten.y = e.getY();
							dragDaten.x = e.getX();
							if(aktiveSpalte[0]>=0){
								if(ansicht==NORMAL_ANSICHT){
									setLockStatement((belegung[tspalte]+1 >=10 ? Integer.toString(belegung[tspalte]+1)+"BEHANDLER" : "0"+(belegung[tspalte]+1)+"BEHANDLER"),aktuellerTag);
									new Thread(new LockRecord()).start();
									long lockzeit = System.currentTimeMillis();
									while(lockok == 0){
										try {
											Thread.sleep(20);
											if(System.currentTimeMillis()-lockzeit > 1500){
												break;
											}
										} catch (InterruptedException e1) {
											e1.printStackTrace();
										}
									}
									if (lockok > 0){
										setUpdateVerbot(true);
										Zeiteinstellen(e.getLocationOnScreen(),belegung[tspalte],aktiveSpalte[0]);
										oSpalten[tspalte].requestFocus();
										setUpdateVerbot(false);
									}else{
										lockok = 0;
										sperreAnzeigen("");
										setUpdateVerbot(false);
										SqlInfo.loescheLocksMaschine();
									}
								}else if(ansicht==WOCHEN_ANSICHT){	//WOCHEN_ANSICHT muß noch entwickelt werden!
									if(aktiveSpalte[2] == 0){

										setLockStatement((wochenbelegung >=10 ? Integer.toString(wochenbelegung)+"BEHANDLER" : "0"+(wochenbelegung)+"BEHANDLER"),getWocheErster() );											
									}else{
										setLockStatement((wochenbelegung >=10 ? Integer.toString(wochenbelegung)+"BEHANDLER" : "0"+(wochenbelegung)+"BEHANDLER"),DatFunk.sDatPlusTage(getWocheErster(),aktiveSpalte[2]) );											
									}
									new Thread(new LockRecord()).start();
									long lockzeit = System.currentTimeMillis();
									while(lockok == 0){
										try {
											Thread.sleep(20);
											if(System.currentTimeMillis()-lockzeit > 1500){
												break;
											}
										} catch (InterruptedException e1) {
											e1.printStackTrace();
										}
									}
									if (lockok > 0){
										setUpdateVerbot(true);
										Zeiteinstellen(e.getLocationOnScreen(),aktiveSpalte[2],aktiveSpalte[0]);
										oSpalten[tspalte].requestFocus();
										setUpdateVerbot(false);
									}else{
										lockok = 0;
										sperreAnzeigen("(e.getClickCount() == 2) && (e.getButton() == java.awt.event.MouseEvent.BUTTON1)");
										setUpdateVerbot(false);
										SqlInfo.loescheLocksMaschine();
									}
								}else if(ansicht==MASKEN_ANSICHT){	//WOCHEN_ANSICHT mu� noch entwickelt werden!
									////System.out.println("Maskenansicht-Doppelklick");
									lockok = 1;
									Zeiteinstellen(e.getLocationOnScreen(),aktiveSpalte[2],aktiveSpalte[0]);
									oSpalten[tspalte].requestFocus();
									lockok = 0;
								}	
							}	
							break;
						}
						
					}

				}
				public void mouseClicked(java.awt.event.MouseEvent e) {
				}
			});
			oSpalten[tspalte].addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
				public void mouseDragged(java.awt.event.MouseEvent e) {
					//Reha.thisClass.shiftLabel.setText("Spalte"+tspalte+"  / Drag:X="+e.getX()+" Y="+e.getY());
					if(fgp != null){
						if(!fgp.isVisible()){
							fgp.setVisible(true);
						}
						fgp.eventDispatched(e);	
					}

				}
				public void mouseMoved(java.awt.event.MouseEvent e) {
					dragDaten.y = e.getY();
					dragDaten.x = e.getX();
					if(fgp != null){
						if(!fgp.isVisible()){
							fgp.setVisible(true);
						}
						fgp.eventDispatched(e);	
					}
				}				
			});
			oSpalten[tspalte].addFocusListener(new java.awt.event.FocusAdapter() {   
				public void focusLost(java.awt.event.FocusEvent e) {    
					focusHandling(1,-1);

				}
				public void focusGained(java.awt.event.FocusEvent e) {
					focusHandling(1,1);
					try{
						if(!Reha.thisClass.terminpanel.eltern.isActive){
							Reha.thisClass.terminpanel.eltern.feuereEvent(25554);
						}
					}catch(Exception ex){
						
					}
				}
			});
			
			oSpalten[tspalte].zeitInit((int)ZeitFunk.MinutenSeitMitternacht(SystemConfig.KalenderUmfang[0]),
					   (int)ZeitFunk.MinutenSeitMitternacht(SystemConfig.KalenderUmfang[1]));
			
			return;
		}
/*
 * 
 * 
 * 
 * 		
 */
		public String getWocheErster(){
			return this.wocheErster;
		}
		private void ZeigePopupMenu(java.awt.event.MouseEvent me){
			JPopupMenu jPop = getTerminPopupMenu();
			jPop.show( me.getComponent(), me.getX(), me.getY() ); 
		}

		private JPopupMenu getTerminPopupMenu() {
			if (jPopupMenu == null) {
				jPopupMenu = new JPopupMenu();
				if(ansicht < MASKEN_ANSICHT){
					jPopupMenu.add(getTagvor());
					jPopupMenu.add(getTagzurueck());					
					jPopupMenu.add(getTagesdialog());
					jPopupMenu.addSeparator();
					JMenu submenu = new JMenu("Termine tauschen");
					submenu.setIcon(SystemConfig.hmSysIcons.get("refresh"));
					submenu.add(getTauschemitvorherigem());
					submenu.add(getTauschemitnachfolger());
					jPopupMenu.add(submenu);
					jPopupMenu.addSeparator();					
					jPopupMenu.add(getBehandlerset());
					jPopupMenu.addSeparator();					
					jPopupMenu.add(getTerminedespatsuchen());
					jPopupMenu.addSeparator();
					submenu = new JMenu("Patient suchen / Telefonliste");
					submenu.setIcon(new ImageIcon(Reha.proghome+"/icons/personen16.gif"));
					submenu.add(getPatientsuchen());
					submenu.add(getTelefonliste());
					jPopupMenu.add(submenu);
					jPopupMenu.addSeparator();
					jPopupMenu.add(getTerminliste());				
					jPopupMenu.addSeparator();
					submenu = new JMenu("Termine gruppieren");
					submenu.setIcon(SystemConfig.hmSysIcons.get("att"));
					submenu.add(getGruppezusammenfassen());
					submenu.add(getGruppekopieren());
					submenu.add(getGruppeeinfuegen());
					submenu.add(getGruppeloeschen());
					jPopupMenu.add(submenu);					
					jPopupMenu.addSeparator();
					submenu = new JMenu("Termin");
					submenu.setIcon(SystemConfig.hmSysIcons.get("termin"));
					submenu.add(getCopy());
					submenu.add(getCut());
					submenu.add(getPaste());
					submenu.add(getConfirm());
					if (SystemConfig.KalenderLangesMenue){
						jPopupMenu.add(submenu);
						jPopupMenu.addSeparator();						
					}
					jPopupMenu.add(getNormalanzeige());
					jPopupMenu.add(getWochenanzeige());
				}else if(ansicht == MASKEN_ANSICHT){
					jPopupMenu.add(getGruppezusammenfassen());
					jPopupMenu.addSeparator();
					jPopupMenu.add(getKalenderschreiben());
				}
			}	
			if(gruppierenAktiv){
				if(ansicht < MASKEN_ANSICHT){
					Behandlerset.setEnabled(false);
					Tagvor.setEnabled(false);
					Tagzurueck.setEnabled(false);					
					Tagesdialog.setEnabled(false);					
					Gruppezusammenfassen.setEnabled(true);
					Gruppekopieren.setEnabled(true);
					Gruppeloeschen.setEnabled(true);
					Terminliste.setEnabled(false);
					Patientsuchen.setEnabled(false);
					Terminedespatsuchen.setEnabled(false);
					Wochenanzeige.setEnabled(false);
					Normalanzeige.setEnabled(false);
					Tauschemitvorherigem.setEnabled(false);					
					Tauschemitnachfolger.setEnabled(false);		
				}else if(ansicht == MASKEN_ANSICHT){
					Interminkalenderschreiben.setEnabled(false);
					Gruppezusammenfassen.setEnabled(true);
				}
				setGruppierenClipBoard();
			}else{
				if(ansicht < MASKEN_ANSICHT){
					Tagvor.setEnabled(true);
					Tagzurueck.setEnabled(true);					
					Tagesdialog.setEnabled(true);
					Gruppezusammenfassen.setEnabled(false);
					Gruppekopieren.setEnabled(false);
					Gruppeloeschen.setEnabled(false);
					Terminliste.setEnabled(true);
					Patientsuchen.setEnabled(true);
					Terminedespatsuchen.setEnabled(true);
					Tauschemitvorherigem.setEnabled(true);					
					Tauschemitnachfolger.setEnabled(true);
					if (Reha.thisClass.copyLabel.getText().length()!=0){ Paste.setEnabled(true); 
					} else { Paste.setEnabled(false); 
					}
					if(ansicht ==NORMAL_ANSICHT){
						Behandlerset.setEnabled(true);
						Wochenanzeige.setEnabled(true);
						Normalanzeige.setEnabled(false);
						Tagvor.setEnabled(true);
						Tagzurueck.setEnabled(true);					
						Tagesdialog.setEnabled(true);
						Tagvor.setText("einen Tag vorwärts blättern");
						Tagzurueck.setText("einen Tag rückwärts blättern");		
						if ( ((Rechte.hatRecht(Rechte.Kalender_terminconfirminpast, false)) || (this.getAktuellerTag().equals(DatFunk.sHeute()))) && (Reha.thisClass.copyLabel.getText().length()!=0) ){
							Confirm.setEnabled(true);
						} else {
							Confirm.setEnabled(false);
						}
					}else if(ansicht ==WOCHEN_ANSICHT){
						Behandlerset.setEnabled(false);
						Wochenanzeige.setEnabled(false);
						Normalanzeige.setEnabled(true);
						Tagvor.setText("eine Woche vorwärts blättern");
						Tagzurueck.setText("eine Woche rückwärts blättern");
						Tagesdialog.setEnabled(false);
						Confirm.setEnabled(false);
					}
				}else if(ansicht == MASKEN_ANSICHT){
					Interminkalenderschreiben.setEnabled(true);
					Gruppezusammenfassen.setEnabled(false);
				}
			}
			if(ansicht < MASKEN_ANSICHT){			
				if((!gruppierenAktiv) && (gruppierenKopiert) ){
					Gruppeeinfuegen.setEnabled(true);
				}else{
					Gruppeeinfuegen.setEnabled(false);
				}
			}
			return jPopupMenu;
		}

		private JMenuItem getTauschemitvorherigem() {
			if (Tauschemitvorherigem == null) {
				Tauschemitvorherigem = new JMenuItem();
				Tauschemitvorherigem.setText("Termin mit Vorgängertermin tauschen");
				Tauschemitvorherigem.setToolTipText("Strg+O");
				Tauschemitvorherigem.setIcon(SystemConfig.hmSysIcons.get("upw"));
				Tauschemitvorherigem.setRolloverEnabled(true);
				Tauschemitvorherigem.setEnabled(true);
				Tauschemitvorherigem.addActionListener(this);
			}
			return Tauschemitvorherigem;
		}

		private JMenuItem getTauschemitnachfolger() {
			if (Tauschemitnachfolger == null) {
				Tauschemitnachfolger = new JMenuItem();
				Tauschemitnachfolger.setText("Termin mit Nachfolgetermin tauschen");
				Tauschemitnachfolger.setToolTipText("Strg+U");
				Tauschemitnachfolger.setIcon(SystemConfig.hmSysIcons.get("down"));				
				Tauschemitnachfolger.setRolloverEnabled(true);
				Tauschemitnachfolger.setEnabled(true);
				Tauschemitnachfolger.addActionListener(this);
			}
			return Tauschemitnachfolger;
		}

		private JMenuItem getBehandlerset() {
			if (Behandlerset == null) {
				Behandlerset = new JMenuItem();
				Behandlerset.setText("Behandler-Set aufrufen");
				Behandlerset.setToolTipText("F12");
				Behandlerset.setRolloverEnabled(true);
				Behandlerset.setEnabled(true);
				Behandlerset.addActionListener(this);
			}
			return Behandlerset;
		}
/** neu **/
		private JMenuItem getCopy() {
			if (Copy == null) {
				Copy = new JMenuItem();
				Copy.setText("kopieren");
				Copy.setIcon(SystemConfig.hmSysIcons.get("copy"));
				Copy.setToolTipText("Strg-Einf");
				Copy.setRolloverEnabled(true);
				Copy.setEnabled(true);
				Copy.addActionListener(this);
			}
			return Copy;
		}
		private JMenuItem getCut() {
			if (Cut == null) {
				Cut = new JMenuItem();
				Cut.setText("ausschneiden");
				Cut.setIcon(SystemConfig.hmSysIcons.get("cut"));
				Cut.setToolTipText("Shift-Entf");
				Cut.setRolloverEnabled(true);
				Cut.setEnabled(true);
				Cut.addActionListener(this);
			}
			return Cut;
		}
		private JMenuItem getPaste() {
			if (Paste == null) {
				Paste = new JMenuItem();
				Paste.setText("einfügen");
				Paste.setIcon(SystemConfig.hmSysIcons.get("paste"));
				Paste.setToolTipText("Shift-Einf");
				Paste.setRolloverEnabled(true);
				Paste.setEnabled(true);
				Paste.addActionListener(this);
			}
			return Paste;			
		}
		private JMenuItem getConfirm() {
			if (Confirm == null) {
				Confirm = new JMenuItem();
				Confirm.setText("bestätigen");
				Confirm.setIcon(SystemConfig.hmSysIcons.get("confirm"));
				Confirm.setToolTipText("Shift-F11");
				Confirm.setRolloverEnabled(true);
				Confirm.setEnabled(false);
				Confirm.addActionListener(this);
			}
			return Confirm;
		}
/** Ende **/
		
		private JMenuItem getTagvor() {
			if (Tagvor == null) {
				Tagvor = new JMenuItem();
				Tagvor.setText("einen Tag vorwärts blättern");
				Tagvor.setIcon(SystemConfig.hmSysIcons.get("right"));
				Tagvor.setToolTipText("Bild-auf");
				Tagvor.setRolloverEnabled(true);
				Tagvor.setEnabled(true);
				Tagvor.addActionListener(this);
			}
			return Tagvor;
		}
		private JMenuItem getTagzurueck() {
			if (Tagzurueck == null) {
				Tagzurueck = new JMenuItem();
				Tagzurueck.setText("einen Tag rückwärts blättern");
				Tagzurueck.setIcon(SystemConfig.hmSysIcons.get("left"));
				Tagzurueck.setToolTipText("Bild-ab");
				Tagzurueck.setRolloverEnabled(true);
				Tagzurueck.setEnabled(true);
				Tagzurueck.addActionListener(this);
			}
			return Tagzurueck;
		}
		private JMenuItem getTagesdialog() {
			if (Tagesdialog == null) {
				Tagesdialog = new JMenuItem();
				Tagesdialog.setText("Datums-Dialog aufrufen");
				Tagesdialog.setIcon(SystemConfig.hmSysIcons.get("dayselect"));
				Tagesdialog.setRolloverEnabled(true);
				Tagesdialog.setEnabled(true);
				Tagesdialog.addActionListener(this);
			}
			return Tagesdialog;
		}
		private JMenuItem getKalenderschreiben() {
			if (Interminkalenderschreiben == null) {
				Interminkalenderschreiben = new JMenuItem();
				Interminkalenderschreiben.setText("Arbeitszeitdefinition in Terminkalender übertragen");
				Interminkalenderschreiben.setRolloverEnabled(true);
				Interminkalenderschreiben.setEnabled(true);
				Interminkalenderschreiben.addActionListener(this);
			}
			return Interminkalenderschreiben;
		}
		private JMenuItem getTerminliste() {
			if (Terminliste == null) {
				Terminliste = new JMenuItem();
				Terminliste.setText("Terminliste aufrufen");
				Terminliste.setRolloverEnabled(true);
				Terminliste.setEnabled(false);
				Terminliste.addActionListener(this);
			}
			return Terminliste;
		}
		private JMenuItem getGruppezusammenfassen() {
			if (Gruppezusammenfassen == null) {
				Gruppezusammenfassen = new JMenuItem();
				Gruppezusammenfassen.setText("Gruppierung zusammenfassen");
				Gruppezusammenfassen.setRolloverEnabled(true);
				Gruppezusammenfassen.setEnabled(false);
				Gruppezusammenfassen.addActionListener(this);
			}	
				return Gruppezusammenfassen;
		}
		private JMenuItem getGruppeloeschen() {
			if (Gruppeloeschen == null) {
				Gruppeloeschen = new JMenuItem();
				Gruppeloeschen.setText("Gruppierung löschen");
				Gruppeloeschen.setRolloverEnabled(true);
				Gruppeloeschen.setEnabled(false);
				Gruppeloeschen.addActionListener(this);
			}
			return Gruppeloeschen;
		}
		private JMenuItem getGruppekopieren() {
			if (Gruppekopieren == null) {
				Gruppekopieren = new JMenuItem();
				Gruppekopieren.setText("Gruppierung kopieren");
				Gruppekopieren.setRolloverEnabled(true);
				Gruppekopieren.setEnabled(false);
				Gruppekopieren.addActionListener(this);
			}
			return Gruppekopieren;
		}
		private JMenuItem getGruppeeinfuegen() {
			if (Gruppeeinfuegen == null) {
				Gruppeeinfuegen = new JMenuItem();
				Gruppeeinfuegen.setText("Termingruppe einfügen");
				Gruppeeinfuegen.setRolloverEnabled(true);
				Gruppeeinfuegen.setEnabled(false);
				Gruppeeinfuegen.addActionListener(this);
				
			}
			return Gruppeeinfuegen;
		}
		private JMenuItem getPatientsuchen() {
			if (Patientsuchen == null) {
				Patientsuchen = new JMenuItem();
				Patientsuchen.setText("Patient suchen (über Rezept-Nummer)");
				Patientsuchen.setToolTipText("Alt-P");
				Patientsuchen.setIcon(SystemConfig.hmSysIcons.get("patsearch"));
				Patientsuchen.setActionCommand("PatRezSuchen");				
				Patientsuchen.setRolloverEnabled(true);
				Patientsuchen.addActionListener(this);
			}
			return Patientsuchen;
		}
		private JMenuItem getTelefonliste() {
			if (Telefonliste == null) {
				Telefonliste = new JMenuItem();
				Telefonliste.setText("Telefonliste aller Patienten (über Rezept-Nummer)");
				Telefonliste.setIcon(SystemConfig.hmSysIcons.get("tellist"));
				Telefonliste.setActionCommand("TelefonListe");				
				Telefonliste.setRolloverEnabled(true);
				Telefonliste.addActionListener(this);
			}
			return Telefonliste;
		}
		private JMenuItem getTerminedespatsuchen() {
			if (Terminedespatsuchen == null) {
				Terminedespatsuchen = new JMenuItem();
				Terminedespatsuchen.setText("Schnellsuche (Heute + 4 Tage)");
				Terminedespatsuchen.setIcon(SystemConfig.hmSysIcons.get("quicksearch"));
				Terminedespatsuchen.setRolloverEnabled(true);
				Terminedespatsuchen.addActionListener(this);
			}
			return Terminedespatsuchen;
		}
		/***************************/
		private JMenuItem getNormalanzeige() {
			if (Normalanzeige == null) {
				Normalanzeige = new JMenuItem();
				Normalanzeige.setText("Normalanzeige (7 Kollegen gleicher Tag)");
				Normalanzeige.setToolTipText("Strg-N");
				Normalanzeige.setIcon(SystemConfig.hmSysIcons.get("day"));
				Normalanzeige.setRolloverEnabled(true);
				Normalanzeige.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						setNormalanzeige();
					}
				});
			}
			return Normalanzeige;
		}
		/**************************************/
		private void setNormalanzeige(){
			try{
				if(ansicht == NORMAL_ANSICHT){
					JOptionPane.showMessageDialog (null, "Sie sind bereits in der Normalanzeige....");
					return;
				}
				
				oCombo[0].setSelectedItem(sbelegung[0]);oCombo[0].setEnabled(true);
				oCombo[1].setSelectedItem(sbelegung[1]);oCombo[1].setEnabled(true);
				oCombo[2].setSelectedItem(sbelegung[2]);oCombo[2].setEnabled(true);
				oCombo[3].setSelectedItem(sbelegung[3]);oCombo[3].setEnabled(true);
				oCombo[4].setSelectedItem(sbelegung[4]);oCombo[4].setEnabled(true);
				oCombo[5].setSelectedItem(sbelegung[5]);oCombo[5].setEnabled(true);
				oCombo[6].setSelectedItem(sbelegung[6]);oCombo[6].setEnabled(true);
				
				ansicht = NORMAL_ANSICHT;
				aktiveSpalte[0]=0;
				aktiveSpalte[1]=0;						
				aktiveSpalte[2]=0;						
				aktiveSpalte[3]=0;
				for(int i=0;i<7;i++){
					oSpalten[i].spalteDeaktivieren();
				}
				ansichtStatement(ansicht,aktuellerTag);
				Normalanzeige.setEnabled(false);
				Wochenanzeige.setEnabled(true);
			}catch(Exception ex){}
		}
		/**************************************/		
		
		private JMenuItem getWochenanzeige() {
			if (Wochenanzeige == null) {
				Wochenanzeige = new JMenuItem();
				Wochenanzeige.setText("Wochenanzeige (1 Kollege 7 Tage)");
				Wochenanzeige.setToolTipText("Strg-W");
				Wochenanzeige.setIcon(SystemConfig.hmSysIcons.get("week"));
				Wochenanzeige.setRolloverEnabled(true);
				Wochenanzeige.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						setWochenanzeige();
					}

				});
			}
			return Wochenanzeige;
		}
/**************************************/
		private void setWochenanzeige(){
			if(ansicht == WOCHEN_ANSICHT){
				JOptionPane.showMessageDialog (null, "Sie sind bereits in der Wochenanzeige....");
				return;
			}
			sbelegung[0]= (String)oCombo[0].getSelectedItem();
			sbelegung[1]= (String)oCombo[1].getSelectedItem();
			sbelegung[2]= (String)oCombo[2].getSelectedItem();
			sbelegung[3]= (String)oCombo[3].getSelectedItem();
			sbelegung[4]= (String)oCombo[4].getSelectedItem();
			sbelegung[5]= (String)oCombo[5].getSelectedItem();
			sbelegung[6]= (String)oCombo[6].getSelectedItem();		

			ansicht = WOCHEN_ANSICHT;

			oCombo[0].setSelectedItem(oCombo[aktiveSpalte[2]].getSelectedItem());
			oCombo[1].setSelectedIndex(0);
			oCombo[2].setSelectedIndex(0);
			oCombo[3].setSelectedIndex(0);
			oCombo[4].setSelectedIndex(0);
			oCombo[5].setSelectedIndex(0);
			oCombo[6].setSelectedIndex(0);

			oCombo[1].setEnabled(false);
			oCombo[2].setEnabled(false);
			oCombo[3].setEnabled(false);
			oCombo[4].setEnabled(false);
			oCombo[5].setEnabled(false);
			oCombo[6].setEnabled(false);
			
			aktiveSpalte[0]=0;
			aktiveSpalte[1]=0;						
			aktiveSpalte[2]=0;						
			aktiveSpalte[3]=0;
			for(int i=0;i<7;i++){
				oSpalten[i].spalteDeaktivieren();
			}
			ansichtStatement(ansicht,aktuellerTag);
			try{
				Normalanzeige.setEnabled(true);
				Wochenanzeige.setEnabled(false);
			}catch(java.lang.NullPointerException ex){
				
			}
		}
/**************************************/			
		public int[] getGruppierenClipBoard(){
			return gruppierenClipBoard;
		}
		private void setGruppierenClipBoard(){
			gruppierenClipBoard[0]= gruppierenBloecke[0];
			gruppierenClipBoard[1]= gruppierenBloecke[1];
			gruppierenClipBoard[2]= gruppierenSpalte;
			gruppierenClipBoard[3]=	gruppierenBehandler;	
		}

/***
 * 
 * 
 * 	
 */
	public void panelTastenAuswerten(java.awt.event.KeyEvent e){
		e.consume();
		int anz = -1;
		switch(e.getKeyCode()){
		case 33: //Bild auf
			if(interminEdit){
				return;
			}
			intagWahl = true;
			try{
				tagSprung(this.aktuellerTag,1);
				if(aktiveSpalte[2]>=0){
					oSpalten[aktiveSpalte[2]].setSpalteaktiv(false);				
				}
			}catch(Exception ex){
				JOptionPane.showMessageDialog(null, "Fehler beim Aufruf des Datumdialog. Fehler =\n"+ex.getMessage());
			}
			intagWahl = false;
			break;
		case 34: //Bild ab
			if(interminEdit){
				return;
			}
			intagWahl = true;
			try{
				tagSprung(this.aktuellerTag,-1);
				if(aktiveSpalte[2]>=0){
					oSpalten[aktiveSpalte[2]].setSpalteaktiv(false);				
				}
			}catch(Exception ex){
				JOptionPane.showMessageDialog(null, "Fehler beim Aufruf des Datumdialog. Fehler =\n"+ex.getMessage());				
			}
			intagWahl = false;
			break;

		case 38: //Pfeil auf
			if(!gruppierenAktiv){
				try{
					if( ((aktiveSpalte[0] >= 0) && (aktiveSpalte[2] >= 0) && (belegung[aktiveSpalte[2]] >= 0)) ||
							((aktiveSpalte[0] >= 0) && (aktiveSpalte[2] >= 0) && (maskenbelegung >= 0))){
						if(ansicht==NORMAL_ANSICHT){
							anz = ((Vector<?>)((ArrayList<?>) vTerm.get(belegung[aktiveSpalte[2]])).get(0)).size();
						}else if(ansicht==WOCHEN_ANSICHT){
							anz = ((Vector<?>)((ArrayList<?>) vTerm.get(aktiveSpalte[2])).get(0)).size();
						}else if(ansicht==MASKEN_ANSICHT){
							anz = ((Vector<?>)((ArrayList<?>) vTerm.get(aktiveSpalte[2])).get(0)).size();						
						}
						if(anz > 1){
							if(aktiveSpalte[0] == 0){
								aktiveSpalte[0] = anz-1;
								aktiveSpalte[1] = anz-1;
							}else{
								aktiveSpalte[0] = aktiveSpalte[0]-1;
									aktiveSpalte[1] = aktiveSpalte[0]-1;
							}
							if(dragLab[aktiveSpalte[2]].getIcon()!= null){
								dragLab[aktiveSpalte[2]].setIcon(null);
								dragLab[aktiveSpalte[2]].setText("");
								oSpalten[aktiveSpalte[2]].spalteDeaktivieren();
							}
							oSpalten[aktiveSpalte[2]].setSpalteaktiv(false);
							oSpalten[aktiveSpalte[2]].schwarzAbgleich(aktiveSpalte[0],aktiveSpalte[0]);
						}	
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}else{
				try{
					if(ansicht==NORMAL_ANSICHT){
						anz = ((Vector<?>)((ArrayList<?>) vTerm.get(belegung[aktiveSpalte[2]])).get(0)).size();
					}else if(ansicht==WOCHEN_ANSICHT){
						anz = ((Vector<?>)((ArrayList<?>) vTerm.get(aktiveSpalte[2])).get(0)).size();
					}else if(ansicht==MASKEN_ANSICHT){
						anz = ((Vector<?>)((ArrayList<?>) vTerm.get(aktiveSpalte[2])).get(0)).size();
					}
					if( (gruppierenBloecke[1] >0) && (anz>0) ){
						gruppierenBloecke[1]=gruppierenBloecke[1]-1;
						if(dragLab[aktiveSpalte[2]].getIcon()!= null){
							dragLab[aktiveSpalte[2]].setIcon(null);
							dragLab[aktiveSpalte[2]].setText("");
							oSpalten[aktiveSpalte[2]].spalteDeaktivieren();
						}
						oSpalten[gruppierenSpalte].gruppierungZeichnen(gruppierenBloecke.clone());
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			break;
		case 40: //Pfeil ab
			if(!gruppierenAktiv){
				try{
					if( ((aktiveSpalte[0] >= 0) && (aktiveSpalte[2] >= 0) && (belegung[aktiveSpalte[2]] >= 0)) ||
							((aktiveSpalte[0] >= 0) && (aktiveSpalte[2] >= 0) && (maskenbelegung >= 0))){
						if(ansicht==NORMAL_ANSICHT){
							try{
							anz = ((Vector<?>)((ArrayList<?>) vTerm.get(belegung[aktiveSpalte[2]
							                                                                  ])).get(0)).size();
							}catch(java.lang.ArrayIndexOutOfBoundsException ob){
								//System.out.println("Spalte nicht belegt");
							}
						}else  if(ansicht==WOCHEN_ANSICHT){
							anz = ((Vector<?>)((ArrayList<?>)vTerm.get(aktiveSpalte[2])).get(0)).size();
						}else  if(ansicht==MASKEN_ANSICHT){
							anz = ((Vector<?>)((ArrayList<?>)vTerm.get(aktiveSpalte[2])).get(0)).size();
						}
						if(anz > 1){
							if(aktiveSpalte[0] == anz-1){
								aktiveSpalte[0] = 0;
								aktiveSpalte[1] = 0;
							}else{
								aktiveSpalte[0] =aktiveSpalte[0]+1;	
								aktiveSpalte[1] =aktiveSpalte[0]+1;					
							}
							if(dragLab[aktiveSpalte[2]].getIcon()!= null){
								dragLab[aktiveSpalte[2]].setIcon(null);
								dragLab[aktiveSpalte[2]].setText("");
								oSpalten[aktiveSpalte[2]].spalteDeaktivieren();
							}
							oSpalten[aktiveSpalte[2]].setSpalteaktiv(false);
							oSpalten[aktiveSpalte[2]].schwarzAbgleich(aktiveSpalte[0],aktiveSpalte[0]);
						}
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}else{
				try{
					if(belegung[aktiveSpalte[2]]==-1){return;}
					if(ansicht==NORMAL_ANSICHT){
						anz = ((Vector<?>)((ArrayList<?>) vTerm.get(belegung[aktiveSpalte[2]])).get(0)).size();
					}else if(ansicht==WOCHEN_ANSICHT){
						anz = ((Vector<?>)((ArrayList<?>) vTerm.get(aktiveSpalte[2])).get(0)).size();
					}else if(ansicht==MASKEN_ANSICHT){
						anz = ((Vector<?>)((ArrayList<?>) vTerm.get(aktiveSpalte[2])).get(0)).size();
					}
					if( (gruppierenBloecke[1]< anz-1) && anz > 0){
						gruppierenBloecke[1]=gruppierenBloecke[1]+1;
						if(dragLab[aktiveSpalte[2]].getIcon()!= null){
							dragLab[aktiveSpalte[2]].setIcon(null);
							dragLab[aktiveSpalte[2]].setText("");
							oSpalten[aktiveSpalte[2]].spalteDeaktivieren();
						}
						oSpalten[gruppierenSpalte].gruppierungZeichnen(gruppierenBloecke.clone());
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			break;
		case 37: //Pfeil nach links
			if (aktiveSpalte[2]==0){
				oSpalten[aktiveSpalte[2]].spalteDeaktivieren();
				aktiveSpalte[2] = 6;
				aktiveSpalte[0]=0;
				aktiveSpalte[1]=0;
				oSpalten[aktiveSpalte[2]].requestFocus();	
				oSpalten[aktiveSpalte[2]].setSpalteaktiv(false);
				oSpalten[aktiveSpalte[2]].schwarzAbgleich(aktiveSpalte[0],aktiveSpalte[1]);				
			}else{
				oSpalten[aktiveSpalte[2]].spalteDeaktivieren();
				aktiveSpalte[2] = aktiveSpalte[2]-1;
				aktiveSpalte[0]=0;
				aktiveSpalte[1]=0;
				oSpalten[aktiveSpalte[2]].requestFocus();	
				oSpalten[aktiveSpalte[2]].setSpalteaktiv(false);
				oSpalten[aktiveSpalte[2]].schwarzAbgleich(aktiveSpalte[0],aktiveSpalte[1]);				
			}
			break;

		case 39: //Pfeil nach rechts
			if (aktiveSpalte[2]==6){
				oSpalten[aktiveSpalte[2]].spalteDeaktivieren();
				aktiveSpalte[2] = 0;
				aktiveSpalte[0]=0;
				aktiveSpalte[1]=0;
				oSpalten[aktiveSpalte[2]].requestFocus();				
				oSpalten[aktiveSpalte[2]].schwarzAbgleich(aktiveSpalte[0],aktiveSpalte[1]);				
			}else{
				oSpalten[aktiveSpalte[2]].spalteDeaktivieren();
				aktiveSpalte[2] = aktiveSpalte[2]+1;
				aktiveSpalte[0]=0;
				aktiveSpalte[1]=0;
				oSpalten[aktiveSpalte[2]].requestFocus();				
				oSpalten[aktiveSpalte[2]].schwarzAbgleich(aktiveSpalte[0],aktiveSpalte[1]);				
			}
			break;			

		case 10: //ReturnTaste
			if(intagWahl){
				return;
			}
			interminEdit = true;
			int[] position;
			int x,y;
			Point pPosition = new Point();
			position = oSpalten[aktiveSpalte[2]].getPosition();
			pPosition = oSpalten[aktiveSpalte[2]].getLocationOnScreen();
			x= pPosition.x+position[0]+(oSpalten[aktiveSpalte[2]].getWidth()/2);
			y= pPosition.y+position[1];
			if(ansicht==NORMAL_ANSICHT){
				setLockStatement((belegung[aktiveSpalte[2]]+1 >=10 ? Integer.toString(belegung[aktiveSpalte[2]]+1)+"BEHANDLER" : "0"+(belegung[aktiveSpalte[2]]+1)+"BEHANDLER"),aktuellerTag);
				new Thread (new LockRecord()).start();
				long lockzeit = System.currentTimeMillis();
				while(lockok == 0){
					try {
						Thread.sleep(20);
						if(System.currentTimeMillis()-lockzeit > 1500){
							break;
						}
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
				if (lockok > 0){
					if(intagWahl){
						return;
					}
					setUpdateVerbot(true);
					Zeiteinstellen(new Point(x,y),belegung[aktiveSpalte[2]],aktiveSpalte[0]);
					interminEdit = false;
					oSpalten[aktiveSpalte[2]].setSpalteaktiv(false);
					oSpalten[aktiveSpalte[2]].repaint();
					oSpalten[aktiveSpalte[2]].requestFocus();
					setUpdateVerbot(false);
				}else{
					lockok = 0;
					sperreAnzeigen("ansicht==NORMAL_ANSICHT - case Taste = 10");
					setUpdateVerbot(false);	
					SqlInfo.loescheLocksMaschine();
					interminEdit = false;
				}
			}else if(ansicht==WOCHEN_ANSICHT){
				if(aktiveSpalte[2] == 0){
					setLockStatement((wochenbelegung >=10 ? Integer.toString(wochenbelegung)+"BEHANDLER" : "0"+(wochenbelegung)+"BEHANDLER"),getWocheErster() );											
				}else{
					setLockStatement((wochenbelegung >=10 ? Integer.toString(wochenbelegung)+"BEHANDLER" : "0"+(wochenbelegung)+"BEHANDLER"),DatFunk.sDatPlusTage(getWocheErster(),aktiveSpalte[2]) );											
				}
				new Thread(new LockRecord()).start();
				long lockzeit = System.currentTimeMillis();
				while(lockok == 0){
					try {
						Thread.sleep(20);
						if(System.currentTimeMillis()-lockzeit > 1500){
							break;
						}
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
				if (lockok > 0){
					if(intagWahl){
						return;
					}
					setUpdateVerbot(true);
					Zeiteinstellen(new Point(x,y),aktiveSpalte[2],aktiveSpalte[0]);
					interminEdit = false;
					oSpalten[aktiveSpalte[2]].setSpalteaktiv(false);
					oSpalten[aktiveSpalte[2]].requestFocus();
					oSpalten[aktiveSpalte[2]].repaint();					
					setUpdateVerbot(false);
				}else{
					lockok = 0;
					sperreAnzeigen("ansicht==WOCHEN_ANSICHT - case Taste = 10");
					setUpdateVerbot(false);	
					interminEdit = false;
					SqlInfo.loescheLocksMaschine();
				}
			}else if(ansicht==MASKEN_ANSICHT){	//WOCHEN_ANSICHT muß noch entwickelt werden!
				e.consume();
				lockok = 1;
				if(intagWahl){
					return;
				}
				Zeiteinstellen(new Point(x,y),aktiveSpalte[2],aktiveSpalte[0]);
				interminEdit = false;
				oSpalten[aktiveSpalte[2]].setSpalteaktiv(false);
				oSpalten[aktiveSpalte[2]].repaint();
				lockok = 0;
			}	
			break;
		}
		return;
	}
	private Point positionErmitteln(){
		Point pPosition = null;
		int [] position = null;
		int x,y;
		position = oSpalten[aktiveSpalte[2]].getPosition();
		pPosition = oSpalten[aktiveSpalte[2]].getLocationOnScreen();
		x= pPosition.x+position[0]+(oSpalten[aktiveSpalte[2]].getWidth()/2);
		y= pPosition.y+position[1];
		return new Point(x,y);
	}
/***
 * 
 * 
 * 
 * 
 * 
 */
	
	@SuppressWarnings("unchecked")
	private void Zeiteinstellen(Point position, int behandler,int block){
		if (behandler < 0){
			starteUnlock();
			return;
		}	
		terminangaben[0] = ((String) ((ArrayList<Vector<String>>) vTerm.get(behandler)).get(0).get(block));
		terminangaben[1] = ((String) ((ArrayList<Vector<String>>) vTerm.get(behandler)).get(1).get(block));
		terminangaben[2] = ((String) ((ArrayList<Vector<String>>) vTerm.get(behandler)).get(2).get(block));
		terminangaben[3] = ((String) ((ArrayList<Vector<String>>) vTerm.get(behandler)).get(3).get(block));
		terminangaben[4] = ((String) ((ArrayList<Vector<String>>) vTerm.get(behandler)).get(4).get(block));
		terminangaben[5] = Integer.toString(block);

		/**************Test der Berechtigungen*****************/
		if(!rechteTest(terminangaben[0])){
			starteUnlock();
			wartenAufReady = false;
			setUpdateVerbot(false);
			return;
		}
		/*****************************************************/
		
	if(lockok > 0){
		this.zf = new Zeitfenster(this);
		int x,y;
		x = position.x;
		int xvp = this.ViewPanel.getLocationOnScreen().x+this.ViewPanel.getWidth();
		if((x+zf.getWidth()+10) > xvp){
			x=x-zf.getWidth();
		}
		y = position.y;
		int yvp = this.ViewPanel.getLocationOnScreen().y+this.ViewPanel.getHeight();
		if(y+zf.getHeight() > yvp){
			y=y-zf.getHeight();
		}else{
			//y=y-(zf.getHeight()/2);
		}
		zf.pack();
		zf.setLocation(x,y);
		zf.toFront();
		zf.setModal(true);
		zf.setVisible(true);
		boolean update = false;
		if(!this.terminrueckgabe[2].isEmpty() && !this.terminrueckgabe[3].isEmpty()){
			for(int i = 0;i<=4;i++){
				if(! this.terminangaben[i].equals(this.terminrueckgabe[i])){
					update = true;
					break;
				}
			}
		}
		if(update){
			if(lockok > 0 && ansicht==NORMAL_ANSICHT){
				Tblock tbl = new Tblock();
				spaltenDatumSetzen(true);
				if((tbl.TblockInit(this,this.terminrueckgabe,aktiveSpalte[2],aktiveSpalte[0],belegung[aktiveSpalte[2]],vTerm,spaltenDatum,0))>=0){
					setUpdateVerbot(true);
					oSpalten[aktiveSpalte[2]].schwarzAbgleich(aktiveSpalte[0],aktiveSpalte[0]);
					oSpalten[aktiveSpalte[2]].datenZeichnen(vTerm,belegung[aktiveSpalte[2]]);
					setUpdateVerbot(false);
				}
			}else if(lockok > 0 && ansicht==WOCHEN_ANSICHT){
				Tblock tbl = new Tblock();
				spaltenDatumSetzen(false);
				if((tbl.TblockInit(this,this.terminrueckgabe,aktiveSpalte[2],aktiveSpalte[0],aktiveSpalte[2],vTerm,spaltenDatum,this.wocheBehandler))>=0){
					setUpdateVerbot(true);
					oSpalten[aktiveSpalte[2]].schwarzAbgleich(aktiveSpalte[0],aktiveSpalte[0]);
					oSpalten[aktiveSpalte[2]].datenZeichnen(vTerm,aktiveSpalte[2]);
					setUpdateVerbot(false);
				}
			}else if(lockok > 0 && ansicht==MASKEN_ANSICHT){
				Tblock tbl = new Tblock();
				spaltenDatumSetzen(true);
				if((tbl.TblockInit(this,this.terminrueckgabe,aktiveSpalte[2],aktiveSpalte[0],aktiveSpalte[2],vTerm,spaltenDatum,maskenbelegung))>=0){
					setUpdateVerbot(true);
					oSpalten[aktiveSpalte[2]].schwarzAbgleich(aktiveSpalte[0],aktiveSpalte[0]);
					oSpalten[aktiveSpalte[2]].datenZeichnen(vTerm,aktiveSpalte[2]);
					setUpdateVerbot(false);
				}
			}
		}else{
			setUpdateVerbot(false);
			starteUnlock();
		}
		this.zf = null;
	} // von rlockok > 0
	}
/***
 * 
 * 
 * 
 * 	
 */
	private boolean rechteTest(String testtermin){
		/**************Test der Berechtigungen*****************/
		boolean teil = Rechte.hatRecht(Rechte.Kalender_terminanlegenteil, false);
		boolean voll = Rechte.hatRecht(Rechte.Kalender_terminanlegenvoll, false);
		if( (testtermin.trim().equals("")) && (! (teil || voll)) ){
			Rechte.hatRecht(Rechte.Kalender_terminanlegenteil, true);
			return false;
		}
		if( (!testtermin.trim().equals(""))  && (!voll)){
			Rechte.hatRecht(Rechte.Kalender_terminanlegenvoll, true);
			return false;
		}
		/*****************************************************/
		return true;
	}
	private void spaltenDatumSetzen(boolean heute){
		if(heute){
			spaltenDatum[0]=this.aktuellerTag;spaltenDatum[1]=this.aktuellerTag;spaltenDatum[2]=this.aktuellerTag;
			spaltenDatum[3]=this.aktuellerTag;spaltenDatum[4]=this.aktuellerTag;spaltenDatum[5]=this.aktuellerTag;
			spaltenDatum[6]=this.aktuellerTag;
		}else{
			spaltenDatum[0]=this.wocheErster;
			spaltenDatum[1]=DatFunk.sDatPlusTage(this.wocheErster,1);
			spaltenDatum[2]=DatFunk.sDatPlusTage(this.wocheErster,2);
			spaltenDatum[3]=DatFunk.sDatPlusTage(this.wocheErster,3);
			spaltenDatum[4]=DatFunk.sDatPlusTage(this.wocheErster,4);
			spaltenDatum[5]=DatFunk.sDatPlusTage(this.wocheErster,5);
			spaltenDatum[6]=DatFunk.sDatPlusTage(this.wocheErster,6);
		}
	}
	public void setWerte(String[] srueck){
		this.terminrueckgabe[0] = srueck[0];
		this.terminrueckgabe[1] = srueck[1];
		this.terminrueckgabe[2] = srueck[2];
		this.terminrueckgabe[3] = srueck[3];
		this.terminrueckgabe[4] = srueck[4];
		this.terminrueckgabe[5] = srueck[5];
		return;
	}
	public void setzeRueckgabe(){
		int behandler = -1;
		int block = -1;
		if(ansicht==NORMAL_ANSICHT){
			behandler = belegung[aktiveSpalte[2]];
			block = aktiveSpalte[0];
		}else if(ansicht==WOCHEN_ANSICHT){
			behandler = aktiveSpalte[2];
			block = aktiveSpalte[0];
		}else if(ansicht==MASKEN_ANSICHT){
			behandler = aktiveSpalte[2];
			block = aktiveSpalte[0];
		}
		try{
			terminrueckgabe[0] = (String) ((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(0)).get(block);
			terminrueckgabe[1] = (String) ((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(1)).get(block);
			terminrueckgabe[2] = (String) ((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(2)).get(block);
			terminrueckgabe[3] = (String) ((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(3)).get(block);
			terminrueckgabe[4] = (String) ((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(4)).get(block);
			terminrueckgabe[5] = Integer.toString(block);
		}catch(java.lang.ArrayIndexOutOfBoundsException ex){
			terminrueckgabe[0] = "";
			terminrueckgabe[1] = "";
			terminrueckgabe[2] = "";
			terminrueckgabe[3] = "";
			terminrueckgabe[4] = "";
			terminrueckgabe[5] = Integer.toString(-1);
			neuerBlockAktiv(  ((ArrayList)vTerm.get(behandler)).size());
		}
	}
	
	public String[] getWerte(){
		return this.terminangaben;
	}
	public JXPanel getViewPanel(){
		return this.ViewPanel;
	}
/*
 * 
 * 
 * 
 * 	
 */
	public int aktuelleAnsicht(){
		return this.ansicht;
	}	
	public void start(){
		oSpalten[0].requestFocus();
		return;
	}	
	public void setUpdateVerbot(boolean lwert){
		this.updateverbot = lwert;
		if(lwert){
			//Reha.thisClass.shiftLabel.setText("Update-Verbot");			
		}else{
			//Reha.thisClass.shiftLabel.setText("Update ok.");			
		}
		return;
	}
	public boolean getUpdateVerbot(){
		return this.updateverbot;
	}

	public int aktuellesSet(){
		return this.aktSet;
	}
/*
 * 
 * 
 * 
 * 
 */
	public void neuerBlockAktiv(int neuBlock){
		aktiveSpalte[0] =neuBlock;
		aktiveSpalte[1] =neuBlock;
	}
/**
 * 
 * 
 * 	
 */
	private void KlickSetzen(kalenderPanel oPanel,java.awt.event.MouseEvent e){
		//int spalte = 0;
		aktiveSpalte = oPanel.BlockTest(e.getX(),e.getY(),aktiveSpalte);
		if (aktiveSpalte[2] != aktiveSpalte[3]){
			if(gruppierenAktiv){
				gruppierenAktiv = false;
				oSpalten[gruppierenSpalte].setInGruppierung(false);
				oSpalten[gruppierenSpalte].repaint();
				gruppierenBloecke[0]=-1;
				gruppierenBloecke[1]=-1;
			}
			oPanel.blockGeklickt(aktiveSpalte[0]);
			oSpalten[aktiveSpalte[3]].blockGeklickt(-1);
			//spalte = oPanel.blockGeklickt(aktiveSpalte[0]);
			//spalte =  oSpalten[aktiveSpalte[3]].blockGeklickt(-1); //  = jPanelT0.blockGeklickt(1);
		}else{
			if(gruppierenAktiv){
				//gruppierenSpalte;
				//spalte = gruppierenSpalte;
				oSpalten[gruppierenSpalte].setInGruppierung(true);
				gruppierenBloecke[1]= aktiveSpalte[0];
				oSpalten[gruppierenSpalte].gruppierungZeichnen(gruppierenBloecke.clone());
				oSpalten[gruppierenSpalte].requestFocus();
			}else{
				oPanel.blockGeklickt(aktiveSpalte[0]);
				//spalte = oPanel.blockGeklickt(aktiveSpalte[0]);
			}
		}
		return;
	}
	
/***
 * 
 * 
 * 
 */
	public void setAufruf(Point p){
		if (this.ansicht == 1){
			JOptionPane.showMessageDialog (null, "Aufruf Terminset ist nur in der Normalansicht möglich (und sinnvoll...)");
			return;			
		}
		Point xpoint = null;
		SetWahl sw = null;
		if(p == null){
			xpoint = this.ViewPanel.getLocationOnScreen();
			setUpdateVerbot(true);
			swSetWahl = -1;
			sw = new SetWahl(this);
			xpoint.x = xpoint.x +(this.ViewPanel.getWidth()/2) - (sw.getWidth()/2);
			xpoint.y = xpoint.y +(this.ViewPanel.getHeight()/2) - (sw.getHeight()/2);
			sw.setLocation(xpoint);

		}else{
			xpoint = p;
			setUpdateVerbot(true);
			swSetWahl = -1;
			sw = new SetWahl(this);
			//xpoint.x = xpoint.x +(this.ViewPanel.getWidth()/2) - (sw.getWidth()/2);
			//xpoint.y = xpoint.y +(this.ViewPanel.getHeight()/2) - (sw.getHeight()/2);
			xpoint.x = xpoint.x-(sw.getWidth()/2);
			xpoint.y = xpoint.y-(sw.getHeight()/2);
			sw.setLocation(xpoint);
		}
		
		sw.pack();
		sw.setVisible(true);
		setUpdateVerbot(false);
		if (sw.ret >= 0){
			this.aktSet = swSetWahl;
			String[] sSet;// = new String[7];
			sSet = ((ArrayList<String[]>)SystemConfig.aTerminKalender.get(this.aktSet).get(1)).get(0);
			oCombo[0].setSelectedItem(sSet[0]);
			oCombo[1].setSelectedItem(sSet[1]);
			oCombo[2].setSelectedItem(sSet[2]);			
			oCombo[3].setSelectedItem(sSet[3]);
			oCombo[4].setSelectedItem(sSet[4]);
			oCombo[5].setSelectedItem(sSet[5]);			
			oCombo[6].setSelectedItem(sSet[6]);
		}
		if(this.lockok > 0){
			//satzEntsperren();
		}
	}
	public void startTitel(){
		String stag = DatFunk.sHeute();
		Reha.thisClass.terminpanel.eltern.setTitle(DatFunk.WochenTag(stag)+" "+stag+" -- KW: "+DatFunk.KalenderWoche(stag)+" -- [Normalansicht]");

	}
	public void setzeFocus(){
		oSpalten[0].requestFocus();
	}
	private void SetzeLabel(){
		String ss = Integer.toString(aktiveSpalte[0])+","+
		Integer.toString(aktiveSpalte[1])+","+
		Integer.toString(aktiveSpalte[2])+","+
		Integer.toString(aktiveSpalte[3]);		
		Reha.thisClass.messageLabel.setText(ss);
	}	
	private void holeFocus(){
		oSpalten[aktiveSpalte[2]].requestFocus();
		//Reha.thisClass.messageLabel.setText("in hole");
	}
	private void focusHandling(int panel,int plusminus){
		focus[panel] = focus[panel]+plusminus;
		if (focus[0]== 0 && focus[1]== 0){
			if (this.hasFocus){
			}
			this.hasFocus = false;
		}else{
			if(!this.hasFocus){
				this.hasFocus = true;
				/*
				if (this.setOben !=0){
					//((JXPanel)this.GrundFlaeche.getParent().getParent()).aktiviereIcon();
				}
				*/

			}
		}
	}
/**
 * 
 * 
 * 
 * 	
 */
/******************Bastelt das SQL-Statement****************/
		public String ansichtStatement(int iansicht,String stag){
			String sstate = "";
			int behandler;
			String sletzter,serster, sbehandler;
			if (this.ansicht == NORMAL_ANSICHT){
				if (!SystemConfig.vDatenBank.get(0).get(2).equals("ADS")){
					sstate = "SELECT * FROM flexkc WHERE datum = '"+DatFunk.sDatInSQL(stag)+"' LIMIT "+ParameterLaden.maxKalZeile;
				}else{ //ADS
					sstate = "SELECT * FROM flexkc WHERE datum = '"+DatFunk.sDatInSQL(stag)+"'";
				}
				macheStatement(sstate,iansicht);
				/*******bislang aktiv*********/
				if (! (ViewPanel.getParent()==null) ){
					Reha.thisClass.terminpanel.eltern.setTitle(DatFunk.WochenTag(stag)+" "+stag+" -- KW: "+DatFunk.KalenderWoche(stag)+" -- [Normalansicht]");
				}
				/****************/
				this.wocheAktuellerTag = "";
			}else if(ansicht == WOCHEN_ANSICHT){
				behandler = wochenbelegung;
				if (behandler==0){
					behandler = ParameterLaden.vKKollegen.get(ParameterLaden.suchen((String)oCombo[0].getSelectedItem())).Reihe ;
				}
				sbehandler = (behandler < 10 ? "0"+behandler : ""+Integer.toString(behandler)); 
				serster   = DatFunk.WocheErster(stag);
				this.wocheErster = serster;
				/***********Nur zum Test*************/
				this.wocheBehandler = behandler;
				sletzter  = DatFunk.WocheLetzter(stag);
				
				sstate = "SELECT * FROM flexkc WHERE datum >= '"+
				DatFunk.sDatInSQL(serster)+"'"+ 
				" AND datum <= '"+
				DatFunk.sDatInSQL(sletzter)+"'"+
				" AND behandler = '"+sbehandler+
				"BEHANDLER'";
				macheStatement(sstate,iansicht);
				Reha.thisClass.terminpanel.eltern.setTitle(DatFunk.WochenTag(serster)+" "+serster+"  bis  "+DatFunk.WochenTag(sletzter)+" "+sletzter+"-----Behandler:"+sbehandler+"-----KW:"+DatFunk.KalenderWoche(serster)+" ----- [Wochenansicht]");
			}
			return sstate;
		}

/******************Mache Statement****************/
	protected void macheStatement(String sstmt,int ansicht) {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE );
			try{
			rs = stmt.executeQuery(sstmt);				
			int i = 0;
			int durchlauf = 0;
			int maxbehandler;
			if(ansicht==0){
				maxbehandler = ParameterLaden.maxKalZeile;
				//maxbehandler = ParameterLaden.vKKollegen.size();
			}else{
				maxbehandler = 7;
			}
			int maxblock = 0;
			int aktbehandler = 1;			
			ArrayList <Object>aKalList = new ArrayList<Object>();
			aSpaltenDaten.clear();
			while( (rs.next()) &&  (aktbehandler <= maxbehandler)){
				Vector<String> v1 = new Vector<String>();
				Vector<String> v2 = new Vector<String>();
				Vector<String> v3 = new Vector<String>();
				Vector<String> v4 = new Vector<String>();
				Vector<String> v5 = new Vector<String>();	
				Vector<String> v6 = new Vector<String>();				
				
				/*in Spalte 301 steht die Anzahl der belegten Bl�cke*/ 
				int belegt = rs.getInt(301);
				/*letzte zu durchsuchende Spalte festlegen*/
				int ende = (5*belegt);
				maxblock = maxblock + (ende+5);
				durchlauf = 1;

				if (!SystemConfig.vDatenBank.get(0).get(2).equals("ADS")){
					for(i=1;i<ende;i=i+5){
						v1.addElement(rs.getString(i));
						v2.addElement(rs.getString(i+1));
						v3.addElement(rs.getString(i+2));
						v4.addElement(rs.getString(i+3));
						v5.addElement(rs.getString(i+4));					
						durchlauf = durchlauf+1;
					}
				}else{ // ADS
					for(i=1;i<ende;i=i+5){
						v1.addElement(rs.getString(i)!= null ? rs.getString(i) : "");
						v2.addElement(rs.getString(i+1)!= null ? rs.getString(i+1) : "");
						v3.addElement(rs.getString(i+2));
						v4.addElement(rs.getString(i+3));
						v5.addElement(rs.getString(i+4));					
						durchlauf = durchlauf+1;
					}
				}

				v6.addElement(rs.getString(301));	//Anzahl
				v6.addElement(rs.getString(302));	//Art			
				v6.addElement(rs.getString(303));	//Behandler
				v6.addElement(rs.getString(304));	//MEMO
				v6.addElement(rs.getString(305));	//Datum			
				v6.addElement(rs.getString(306));	//id

				aKalList.add(v1.clone());
				aKalList.add(v2.clone());			
				aKalList.add(v3.clone());
				aKalList.add(v4.clone());
				aKalList.add(v5.clone());
				aKalList.add(v6.clone());

				aSpaltenDaten.add(aKalList.clone());	
				aKalList.clear();
				aktbehandler++;
			}
			aSpaltenDaten.add(aKalList.clone());
			//ge�ndert
			aKalList = null;
			if(maxblock > 0){
				datenZeichnen(aSpaltenDaten);
			}
			} catch(SQLException ex){
				//System.out.println("Im Thread - Mache Statement");				
				//System.out.println("von ResultSet SQLState: " + ex.getSQLState());
				//System.out.println("von ResultSet ErrorCode: " + ex.getErrorCode ());
				//System.out.println("von ResultSet ErrorMessage: " + ex.getMessage ());
			}

	} catch(SQLException ex) {
		//System.out.println("Im Thread - Mache Statement");
		//System.out.println("von stmt -SQLState: " + ex.getSQLState());
		if (ex.getSQLState().equals("08003")){
    		int nochmals = JOptionPane.showConfirmDialog(null,"Die Datenbank konnte nicht gestartet werden, erneuter Versuch?","Wichtige Benuterzinfo",JOptionPane.YES_NO_OPTION);
    		if(nochmals == JOptionPane.YES_OPTION){
    			Reha.thisClass.ladenach();
    		}

			
		}
	}
	finally {
		if (rs != null) {
			try {
				rs.close();
				rs = null;
			} catch (SQLException sqlEx) { // ignore }
				rs = null;
			}
		}
		if (stmt != null) {
			try {
				stmt.close();
				stmt = null;
			} catch (SQLException sqlEx) { // ignore }
				stmt = null;
			}
		}

	}
	}
/******************************/
	private void maskenStatement(String sstmt){
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE );
			try{
			rs = stmt.executeQuery(sstmt);				
			int i = 0;
			int durchlauf = 0;
			int maxbehandler;
			if(ansicht==0){
				maxbehandler = ParameterLaden.vKKollegen.size();
			}else{
				maxbehandler = 7;
			}
			int maxblock = 0;
			int aktbehandler = 1;			
			ArrayList <Object>aKalList = new ArrayList<Object>();
			aSpaltenDaten.clear();
			while( (rs.next()) ){
				Vector<String> v1 = new Vector<String>();
				Vector<String> v2 = new Vector<String>();
				Vector<String> v3 = new Vector<String>();
				Vector<String> v4 = new Vector<String>();
				Vector<String> v5 = new Vector<String>();	
				Vector<String> v6 = new Vector<String>();				
				
				/*in Spalte 301 steht die Anzahl der belegten Bl�cke*/ 
				int belegt = rs.getInt(226);
				/*letzte zu durchsuchende Spalte festlegen*/
				int ende = (5*belegt);
				maxblock = maxblock + (ende+5);
				durchlauf = 1;
				if (!SystemConfig.vDatenBank.get(0).get(2).equals("ADS")){
					for(i=1;i<ende;i=i+5){
						v1.addElement(rs.getString(i)!= null ? rs.getString(i) : "");
						v2.addElement(rs.getString(i+1)!= null ? rs.getString(i+1) : "");
						v3.addElement(rs.getString(i+2));
						v4.addElement(rs.getString(i+3));
						v5.addElement(rs.getString(i+4));					
						durchlauf = durchlauf+1;
					}
				}else{ // ADS
					for(i=1;i<ende;i=i+5){
						v1.addElement(rs.getString(i)!= null ? rs.getString(i) : "");
						v2.addElement(rs.getString(i+1)!= null ? rs.getString(i+1) : "");
						v3.addElement(rs.getString(i+2));
						v4.addElement(rs.getString(i+3));
						v5.addElement(rs.getString(i+4));					
						durchlauf = durchlauf+1;
					}
				}

				v6.addElement(rs.getString(226));	//Anzahl
				v6.addElement(rs.getString(227));	//Art			
				v6.addElement(rs.getString(228));	//Behandler
				v6.addElement(rs.getString(229));	//MEMO
				v6.addElement(rs.getString(230));	//Datum			

				aKalList.add(v1.clone());
				aKalList.add(v2.clone());			
				aKalList.add(v3.clone());
				aKalList.add(v4.clone());
				aKalList.add(v5.clone());
				aKalList.add(v6.clone());	
				aSpaltenDaten.add(aKalList.clone());	
				aKalList.clear();
				aktbehandler++;
			}
	
			if(maxblock > 0){
				datenZeichnen(aSpaltenDaten);
				TerminFenster.rechneMaske();
				if(ansicht==MASKEN_ANSICHT){
					oSpalten[aktiveSpalte[2]].requestFocus(true);
					oSpalten[aktiveSpalte[2]].schwarzAbgleich(aktiveSpalte[0], aktiveSpalte[0]);
				}
			}
			} catch(SQLException ex){
				//System.out.println("von ResultSet SQLState: " + ex.getSQLState());
				//System.out.println("von ResultSet ErrorCode: " + ex.getErrorCode ());
				//System.out.println("von ResultSet ErrorMessage: " + ex.getMessage ());
			}

	} catch(SQLException ex) {
		//System.out.println("von stmt -SQLState: " + ex.getSQLState());
	}
	finally {
		if (rs != null) {
			try {
				rs.close();
				rs = null;
			} catch (SQLException sqlEx) { // ignore }
				rs = null;
			}
		}
		if (stmt != null) {
			try {
				stmt.close();
				stmt = null;
			} catch (SQLException sqlEx) { // ignore }
				stmt = null;
			}
		}

	}
}
		
	
/******************************/	
	public void datenZeichnen(Vector<Object> vect){
		vTerm = ((Vector)vect.clone());
		//ge�ndert
		vect = null;
		if (vTerm.size() > 0){
			if (this.ansicht == NORMAL_ANSICHT){
				SwingUtilities.invokeLater(new Runnable(){
				public  void run(){
					oSpalten[0].datenZeichnen(vTerm,belegung[0]);
					oSpalten[1].datenZeichnen(vTerm,belegung[1]);
					oSpalten[2].datenZeichnen(vTerm,belegung[2]);
					oSpalten[3].datenZeichnen(vTerm,belegung[3]);
					oSpalten[4].datenZeichnen(vTerm,belegung[4]);
					oSpalten[5].datenZeichnen(vTerm,belegung[5]);		
					oSpalten[6].datenZeichnen(vTerm,belegung[6]);
				}
				}); 	   
				
			}else if(ansicht == WOCHEN_ANSICHT || ansicht== MASKEN_ANSICHT){
				new Thread(new KalZeichnen(oSpalten[0],vTerm,0)).start();
				new Thread(new KalZeichnen(oSpalten[1],vTerm,1)).start();
				new Thread(new KalZeichnen(oSpalten[2],vTerm,2)).start();
				new Thread(new KalZeichnen(oSpalten[3],vTerm,3)).start();
				new Thread(new KalZeichnen(oSpalten[4],vTerm,4)).start();
				new Thread(new KalZeichnen(oSpalten[5],vTerm,5)).start();				
				new Thread(new KalZeichnen(oSpalten[6],vTerm,6)).start();				
			}
		}	
	}	
/****
 * 
 * 
 * 
 * 
 * 	
 */

	
	public void rehaTPEventOccurred(RehaTPEvent evt) {
			try{
			if (evt.getDetails()[0].equals(this.GrundFlaeche.getParent().getName()) ){
				if (evt.getDetails()[1]=="ROT"){
					//String fname = evt.getDetails()[0];
					xEvent.removeRehaTPEventListener((RehaTPEventListener)this);
					if(SystemConfig.UpdateIntervall > 0 || db_Aktualisieren!= null){
						db_Aktualisieren.stop();
					}
					finalise();
				}else if(evt.getDetails()[2].equals("RequestFocus")){
					oSpalten[aktiveSpalte[2]].requestFocus();
				}else if(evt.getDetails()[1]=="GRUEN"){	
					oSpalten[aktiveSpalte[2]].requestFocus();
				}else if(evt.getRehaEvent().equals("ChangeLocation")){
					setOben = Integer.parseInt(evt.getDetails()[1]);
				}
			}
			}catch(NullPointerException ne){
				//System.out.println(evt);
			}
	}

	public int getAnsicht(){
		return this.ansicht;
	}
	private String getName(){
		return this.FensterName;
	}

	public void setStandort(int setOben){
		this.setOben = setOben;
	}
	public void setLockStatement(String sBehandler,String sDatum){
		lockStatement = sBehandler+sDatum;
		lockedRecord = lockStatement;
	}

	public static String getLockStatement(){
		return lockStatement;
	}
	public static String getLockMaschine(){
		return Reha.thisClass.terminpanel.lockowner;
	}

	public static String getLockSpalte(){
		return Reha.thisClass.terminpanel.lockspalte;
	}

	public static void setLockMaschine(String maschine){
		Reha.thisClass.terminpanel.lockowner = maschine;
	}

	public static void setLockSpalte(String spalte){
		Reha.thisClass.terminpanel.lockspalte = spalte;
	}

	public static synchronized void setLockOk(int lock,String message){
		Reha.thisClass.terminpanel.lockok = lock;
		Reha.thisClass.terminpanel.lockmessage = message;
		/*
		if (Reha.thisClass.terminpanel.lockok < 0 && Reha.thisClass.terminpanel.zf != null){
			
		}
		*/
	}
	

	public static TerminFenster getThisClass(){
		return Reha.thisClass.terminpanel;
	}
	
	private void sperreAnzeigen(String programmteil){
		JOptionPane.showMessageDialog (null, "Diese Terminspalte ist derzeit gesperrt von Benutzer \n\n" +
				"---> "+lockmessage+
				"\n\n und kann deshalb nicht veränder werden!\n\n"+
				"Bitte informieren Sie den Administrator und notiern Sie zuvor -> "+programmteil);
	}
	public static void starteUnlock(){
		new Thread(new UnlockRecord()).start();
	}
	/********
	 *  Strg+Einfg.
	 */
	public void setDatenVonExternInSpeicherNehmen(String[] daten){
		datenSpeicher[0]= String.valueOf(daten[0]);		
		datenSpeicher[1]= String.valueOf(daten[1]);		
		datenSpeicher[3]= String.valueOf(daten[2]);		
		Reha.thisClass.copyLabel.setText(datenSpeicher[0]+"°"+datenSpeicher[1]+"°"+datenSpeicher[3]+" Min.");
	}
	private void getDatenVonExternInSpeicherNehmen(){
		if(Reha.thisClass.bunker.getText().indexOf("°") >= 0 ){
			try{
				String[] teilen = Reha.thisClass.bunker.getText().split("°");
				if(teilen.length <= 0){return;}
				teilen[3] = teilen[3].toUpperCase();
				teilen[3] = teilen[3].replaceAll(" MIN.", "");

				datenSpeicher[0]= teilen[1];		
				datenSpeicher[1]= teilen[2];		
				datenSpeicher[3]= teilen[3];
				Reha.thisClass.copyLabel.setText(datenSpeicher[0]+"°"+datenSpeicher[1]+"°"+datenSpeicher[3]+" Min.");
				Reha.thisClass.shiftLabel.setText("bereit für F2= "+datenSpeicher[0]+"°"+datenSpeicher[1]+"°"+datenSpeicher[3]+" Min.");
			}catch(Exception ex){
				JOptionPane.showMessageDialog(null,"Fehler bei der Vorbereitung des Datenspeichers");
			}
			
		}	
	}
	private void datenInSpeicherNehmen(){
		int aktbehandler = -1;
		if(ansicht==NORMAL_ANSICHT){
			aktbehandler = belegung[aktiveSpalte[2]];
		}else if(ansicht==WOCHEN_ANSICHT){
			aktbehandler = aktiveSpalte[2];
		}else if(ansicht==MASKEN_ANSICHT){
			aktbehandler = aktiveSpalte[2];
		}
		int aktblock = aktiveSpalte[0];
		if (aktbehandler == -1){
			return;
		}
		datenSpeicher[0]= (String) ((String)((Vector)((ArrayList)vTerm.get(aktbehandler)).get(0)).get(aktblock)).replaceAll("\u00AE"  , "");		
		datenSpeicher[1]= (String) ((Vector)((ArrayList)vTerm.get(aktbehandler)).get(1)).get(aktblock);		
		datenSpeicher[3]= (String) ((Vector)((ArrayList)vTerm.get(aktbehandler)).get(3)).get(aktblock);		

		Reha.thisClass.copyLabel.setText(datenSpeicher[0]+"°"+datenSpeicher[1]+"°"+datenSpeicher[3]+" Min.");
		Reha.thisClass.bunker.setText("TERMDATEXT°"+datenSpeicher[0]+"°"+datenSpeicher[1]+"°"+datenSpeicher[3]+" Min.");
	}
	private String[] datenInDragSpeicherNehmen(){
		String[] srueck = {null,null,null,null,null};
		int aktbehandler = -1;
		if(ansicht==NORMAL_ANSICHT){
			aktbehandler = belegung[aktiveSpalte[2]];
		}else if(ansicht==WOCHEN_ANSICHT){
			aktbehandler = aktiveSpalte[2];
		}else if(ansicht==MASKEN_ANSICHT){
			aktbehandler = aktiveSpalte[2];
		}
		int aktblock = aktiveSpalte[0];
		if (aktbehandler == -1){
			return srueck;
		}
		try{
		srueck[0]= (String) ((String)((Vector)((ArrayList)vTerm.get(aktbehandler)).get(0)).get(aktblock)).replaceAll("\u00AE"  , "");		
		srueck[1]= (String) ((Vector)((ArrayList)vTerm.get(aktbehandler)).get(1)).get(aktblock);		
		srueck[3]= (String) ((Vector)((ArrayList)vTerm.get(aktbehandler)).get(3)).get(aktblock);
		return srueck;
		}catch(java.lang.ArrayIndexOutOfBoundsException ex){
			return new String[]{null,null,null,null,null};
		}
	}

	public void setDatenSpeicher(String[] speicher){
		datenSpeicher[0]= speicher[0];		
		datenSpeicher[1]= speicher[1];		
		datenSpeicher[3]= speicher[3];		
		Reha.thisClass.copyLabel.setText(datenSpeicher[0]+"°"+datenSpeicher[1]+"°"+datenSpeicher[3]+" Min.");		
	}
	/********
	 *  Shift+Einfg.
	 */
	private void datenAusSpeicherHolen(){
		//System.out.println("Warten auf Ready = "+wartenAufReady);
		try{
		int aktbehandler=-1;
		int aktdauer;
		String aktstart;
		String aktend;
		String akttermdaten;
		setUpdateVerbot(true); /****************///////

		gruppierenAktiv = false;
		gruppierenBloecke[0] = -1;
		gruppierenBloecke[1] = -1;	
		oSpalten[gruppierenSpalte].setInGruppierung(false);


		if(datenSpeicher[0]==null){
			////System.out.println("datenSpeicher[0] hat den Wert null -> return");
			wartenAufReady = false;
			setUpdateVerbot(false);
			SqlInfo.loescheLocksMaschine();
			return;
		}
		//****Hier rein die Abfrage ob die Druckerliste neu gestartet werden soll!
		int anzahl;
		if((anzahl = terminVergabe.size()) > 0){
			if(!datenSpeicher[0].equals(terminVergabe.get(anzahl-1)[8])){
				String confText = 
					"Der Patient --> "+datenSpeicher[0]+" <--ist jetzt NEU im internen Speicher.\n\n"+
					"BISLANG war der Patient --> "+terminVergabe.get(anzahl-1)[8]+" <-- im Speicher und damit in der Druckliste.\n"+
					"Soll die bisherige Druckliste gelöscht werden und der Patient "+datenSpeicher[0]+
					" übernommen werden?\n\n";
					String meldungText = "Achtung!!! - wichtige Benutzeranfrage";
					int abfrage = JOptionPane.showConfirmDialog(null,confText,meldungText,JOptionPane.YES_NO_OPTION);
					if (abfrage == JOptionPane.YES_OPTION) {
						terminVergabe.clear();
					}else{
						String[] internerSpeicher = {terminVergabe.get(anzahl-1)[8],
						terminVergabe.get(anzahl-1)[9],"",terminVergabe.get(anzahl-1)[4]};
						setDatenSpeicher(internerSpeicher.clone());							 		   
					}
			}
		}
		
		if(ansicht==NORMAL_ANSICHT){
			aktbehandler = belegung[aktiveSpalte[2]];
		}else if(ansicht==WOCHEN_ANSICHT){
			aktbehandler = aktiveSpalte[2];
		}else if(ansicht==MASKEN_ANSICHT){
			aktbehandler = aktiveSpalte[2];
		}
		int aktblock = aktiveSpalte[0];
		aktdauer = Integer.parseInt((String) ((Vector<?>)((ArrayList<?>)vTerm.get(aktbehandler)).get(3)).get(aktblock));	
		aktstart = (String) ((Vector<?>)((ArrayList<?>)vTerm.get(aktbehandler)).get(2)).get(aktblock); 
		aktend = (String) ((Vector<?>)((ArrayList<?>)vTerm.get(aktbehandler)).get(4)).get(aktblock);
		akttermdaten = (String) ((Vector<?>)((ArrayList<?>)vTerm.get(aktbehandler)).get(0)).get(aktblock);
		/**************Test der Berechtigungen*****************/
		if(!rechteTest(akttermdaten)){
			wartenAufReady = false;
			setUpdateVerbot(false);
			SqlInfo.loescheLocksMaschine();
			return;
		}
		/*******************************************/
		for(int i = 0; i<1; i++){
			if(aktdauer == Integer.parseInt(datenSpeicher[3])){
				datenSpeicher[2] = aktstart;
				datenSpeicher[4] = aktend;
				blockSetzen(1);
				break;
			}
			if(aktdauer < Integer.parseInt(datenSpeicher[3])){
				dialogRetInt = 0;
		 		   Point p = null; //positionErmitteln();
	 			   if(!terminGedropt){
	  				  p = positionErmitteln();
	  			   }else{
	  				   p = MouseInfo.getPointerInfo().getLocation();
	  				   p.y = p.y+4;
	  			   }
		 		   new TerminEinpassen(p.x,p.y);
					switch(dialogRetInt){
						case 0:
							terminBreak = true;
							wartenAufReady = false;
							break;
						case 1:
							datenSpeicher[2] = aktstart;
							datenSpeicher[4] = aktend;
							blockSetzen(4);
							break;
						case 2:
							//in Nachfolgeblock kürzen
							datenSpeicher[2] = aktstart;
							datenSpeicher[4] = aktend;
							int ende1,ende2;
							int aktanzahl = ((Vector<?>)((ArrayList<?>)vTerm.get(aktbehandler)).get(0)).size()-1;
							if(aktanzahl==aktblock){
								//ende1 = 
							}else{ //pr�fen ob nachfolgender Block gekürzt werden kann oder ob zu klein
								ende1 = (int) ZeitFunk.MinutenSeitMitternacht((String) ((Vector<?>)((ArrayList<?>)vTerm.get(aktbehandler)).get(4)).get(aktblock+1));
								ende2 = (int) ZeitFunk.MinutenSeitMitternacht(aktstart)+Integer.parseInt(datenSpeicher[3]);
								
								if (ende2 >= ende1){
									JOptionPane.showMessageDialog (null, "Der nachfolgende Block ist von kürzerer Dauer\n"+
											"als er für die von Ihnen gewünschte Operation sein müßte\n\n"+
											"Kopiert wird daher --> nix!");
									wartenAufReady = false;
									
								}else{
									blockSetzen(6);
								}
							}
							break;
					}
					break;
				}
			if(aktdauer > Integer.parseInt(datenSpeicher[3])){
				dialogRetInt = 0;
				
 			   Point p = null;
 			   if(!terminGedropt){
 				  p = positionErmitteln();
 			   }else{
 				   p = MouseInfo.getPointerInfo().getLocation();
 				   p.y = p.y+4;
 			   }
			   new TerminObenUntenAnschliessen(p.x,p.y);
			   //System.out.println("DialogretInt = "+dialogRetInt);
			   
				switch(dialogRetInt){
					case 0:
						terminBreak = true;
						wartenAufReady = false;
						break;
					case 1: 
						//Block oben anschliessen
						datenSpeicher[2] = aktstart;
						datenSpeicher[4] = aktend;
						blockSetzen(2);
						//o.k. blockObenAnschliessen();
						break;
					
					case 2: 
						//Block unten anschliessen
						datenSpeicher[2] = aktstart;
						datenSpeicher[4] = aktend;
						blockSetzen(3);
						break;
						//blockUntenAnschliessen();
					
					case 3: 
						//Block einpassen
						datenSpeicher[2] = aktstart;
						datenSpeicher[4] = aktend;
						blockSetzen(4);
						//blockAusdehnen();
						break;
					
					case 4: 
						int zeit1,zeit2,zeit3,zeit4;
						datenSpeicher[2] = dialogRetData[0]+":"+dialogRetData[1]+":00";
						zeit1 = (int) ZeitFunk.MinutenSeitMitternacht(datenSpeicher[2])+Integer.parseInt(datenSpeicher[3]);
						zeit2 = (int) ZeitFunk.MinutenSeitMitternacht(aktend);
						zeit3 = (int) ZeitFunk.MinutenSeitMitternacht(datenSpeicher[2]);
						zeit4 = (int) ZeitFunk.MinutenSeitMitternacht(aktstart);
						if ((zeit1 < zeit2) && (zeit3 > zeit4) ){
							////System.out.println("case 4: blockSetzen(5)  zeiten:"+zeit1+" / "+zeit2+" / "+zeit3+" / "+zeit4);
							blockSetzen(5);
							break;
						}else if((zeit1 == zeit2) && (zeit3 > zeit4)){
							blockSetzen(3);
							////System.out.println("case 4: mu� unten andocken  zeiten:"+zeit1+" / "+zeit2+" / "+zeit3+" / "+zeit4);
							break;
						}else if((zeit1 < zeit2) && (zeit3 == zeit4)){
							blockSetzen(2);
							////System.out.println("case 4: mu� oben andocken  zeiten:"+zeit1+" / "+zeit2+" / "+zeit3+" / "+zeit4);
							break;							
						}else{
							////System.out.println("case 4: pa�t nicht  zeiten:"+zeit1+" / "+zeit2+" / "+zeit3+" / "+zeit4);
							JOptionPane.showMessageDialog (null, "Die von Ihnen angegebene Startzeit "+datenSpeicher[2]+"\n"+
									" und die Dauer des Termines von "+datenSpeicher[3]+" Minuten, passt hinten und\n"+
									"vorne nicht. Entweder ergibt dies Startzeit eine Überschneidung mit \n"+
									"dem vorherigen oder mit dem nachfolgenden Termin\n\n"+
									"Kopiert wird daher --> nix!");
							wartenAufReady = false;
						}							
						break;
					
				}
				break;
			}
		}
		SqlInfo.loescheLocksMaschine();
		setUpdateVerbot(false);
		}catch(Exception ex){
			ex.printStackTrace();
			SqlInfo.loescheLocksMaschine();
			setUpdateVerbot(false);
			wartenAufReady = false;
		}
	}
	public int[] getAktiverBlock(){
		return aktiveSpalte;
	}
	public static void setDialogRet(int iret, String[] sret){

		Reha.thisClass.terminpanel.dialogRetInt = iret;
		Reha.thisClass.terminpanel.dialogRetData[0] = sret[0];
		Reha.thisClass.terminpanel.dialogRetData[1] = sret[1];	
	}
/******************
* Nachfolgend das Blockhandling
* Übergabe = 1 Block passt genaut
* Übergabe = 2 Block oben anschließen
* Übergabe = 3 Block unten anschließen
* Übergabe = 4 Block ausdehnen
* Übergabe = 5 Startzeit wurde manuell festgelegt
* Übergabe = 6 Nachfolgenden Block kürzen
* Übergabe = 7 Vorblock kürzen
* Übergabe = 8 Gruppierten Terminblock zusammenfassen 
* Übergabe = 9 Gruppierten Terminblock löschen	
* Übergabe = 10 Freitermin eintragen
* Übergabe = 10 Block löschen
* Übergabe = 11 Block tauschen mit vorgänger
* Übergabe = 12 Block tauschen mit nachfolger
*/
		private void blockSetzen(int wohin){
			int gesperrt=0;
			gesperrt = lockVorbereiten();
			setzeRueckgabe();

			if(gesperrt < 0){
				sperreAnzeigen("in blockSetzen - Parameter="+Integer.toString(wohin));
				SqlInfo.loescheLocksMaschine();
				return;
			}else{
				setUpdateVerbot(true);
				int rueck = -1;
				if(ansicht == NORMAL_ANSICHT){
					spaltenDatumSetzen(true);
					BlockHandling bhd = new BlockHandling(wohin,vTerm,belegung[aktiveSpalte[2]],aktiveSpalte[2],aktiveSpalte[0],spaltenDatum,0,datenSpeicher);
					rueck = bhd.init();
				}else if(ansicht==WOCHEN_ANSICHT){
					spaltenDatumSetzen(false);
					BlockHandling bhd = new BlockHandling(wohin,vTerm,aktiveSpalte[2],aktiveSpalte[2],aktiveSpalte[0],spaltenDatum,this.wocheBehandler,datenSpeicher);				
					rueck = bhd.init();
				}else if(ansicht==MASKEN_ANSICHT){
					spaltenDatumSetzen(true);
					BlockHandling bhd = new BlockHandling(wohin,vTerm,aktiveSpalte[2],aktiveSpalte[2],aktiveSpalte[0],spaltenDatum,maskenbelegung,datenSpeicher);
					rueck = bhd.init();
				}
				if(rueck >=0){
					if(ansicht==NORMAL_ANSICHT){
						//in Datenzeichnen
						if(wohin == 8 || wohin == 9){ // Block komplett zusammenfassen
							aktiveSpalte[0]=Math.min(gruppierenClipBoard[0],gruppierenClipBoard[1]);
						}	
						int anzahl = ((Vector<?>)((ArrayList<?>) vTerm.get(belegung[aktiveSpalte[2]])).get(0)).size();
						if(aktiveSpalte[0] >= anzahl){
							aktiveSpalte[0] = anzahl -1;
						}
						oSpalten[aktiveSpalte[2]].schwarzAbgleich(aktiveSpalte[0],aktiveSpalte[0]);
						oSpalten[aktiveSpalte[2]].datenZeichnen(vTerm,belegung[aktiveSpalte[2]]);
						oSpalten[aktiveSpalte[2]].repaint();
					}else if(ansicht==WOCHEN_ANSICHT){
						if(wohin == 8  || wohin == 9){ // Block komplett zusammenfassen
							aktiveSpalte[0]=Math.min(gruppierenClipBoard[0],gruppierenClipBoard[1]);
						}	
						int anzahl = ((Vector<?>)((ArrayList<?>) vTerm.get(aktiveSpalte[2])).get(0)).size();
						if(aktiveSpalte[0] >= anzahl){
								aktiveSpalte[0] = anzahl -1;
						}
						oSpalten[aktiveSpalte[2]].schwarzAbgleich(aktiveSpalte[0],aktiveSpalte[0]);
						oSpalten[aktiveSpalte[2]].datenZeichnen(vTerm,aktiveSpalte[2]);
						oSpalten[aktiveSpalte[2]].repaint();
					}else if(ansicht==MASKEN_ANSICHT){
						if(wohin == 8  || wohin == 9){ // Block komplett zusammenfassen
							aktiveSpalte[0]=Math.min(gruppierenClipBoard[0],gruppierenClipBoard[1]);
						}	
						int anzahl = ((Vector<?>)((ArrayList<?>) vTerm.get(aktiveSpalte[2])).get(0)).size();
						if(aktiveSpalte[0] >= anzahl){
								aktiveSpalte[0] = anzahl -1;
						}
						oSpalten[aktiveSpalte[2]].schwarzAbgleich(aktiveSpalte[0],aktiveSpalte[0]);
						oSpalten[aktiveSpalte[2]].datenZeichnen(vTerm,aktiveSpalte[2]);
						oSpalten[aktiveSpalte[2]].repaint();
					}
				}else{
					starteUnlock();
					wartenAufReady = false;
					setUpdateVerbot(false);
				}
			}
			SqlInfo.loescheLocksMaschine();
			wartenAufReady = false;
			setUpdateVerbot(false);
		}
		public void setUpdateVector(Vector vTerm){
			oSpalten[aktiveSpalte[2]].datenZeichnen(vTerm,aktiveSpalte[2]);
		}
		public Vector getDatenVector(){
			return vTerm;
		}
		public static void rechneMaske(){
		String titel = "";
		  Double stunden = 0.00;
		  String [] wochensicht = {"Mo=","Di=","Mi=","Do=","Fr=","Sa=","So="};
		  int anzahl;
		  int minuten_tag = 0;
		  Double stunden_woche = 0.00;
		  DecimalFormat df = new DecimalFormat();
		  df.setMaximumFractionDigits(2);
		  df.setMinimumFractionDigits(2);
		  for(int tage=0;tage<7;tage++){
			  anzahl = ((Vector<?>)((ArrayList<?>) Reha.thisClass.terminpanel.vTerm.get(tage)).get(0)).size(); 
			  minuten_tag = 0;
			  for(int i=0;i<anzahl;i++){
				  ;
				  if(! ((String) ((Vector<?>)((ArrayList<?>) Reha.thisClass.terminpanel.vTerm.get(tage)).get(1)).get(i)).trim().contains("@FREI")){
					  //minuten_tag = minuten_tag + Integer.valueOf( ((String) ((Vector<?>)((ArrayList<?>)Reha.thisClass.terminpanel.vTerm.get(tage)).get(3)).get(i)).trim());
					  minuten_tag = minuten_tag + Integer.parseInt( ((String) ((Vector<?>)((ArrayList<?>)Reha.thisClass.terminpanel.vTerm.get(tage)).get(3)).get(i)).trim());
				  }
			  }
			  stunden = 0.00+minuten_tag;
			  stunden_woche = stunden_woche+stunden;
			  titel = titel + wochensicht[tage]+df.format(stunden/60)+" - ";
		  }
		  titel = titel + "Wochenstunden: "+df.format(stunden_woche/60);
		  Reha.thisClass.terminpanel.eltern.setTitle(titel);
		}
		
/***********
 * 
 * 
 * 
 * ***************/	
	private int lockVorbereiten(){
		lockok = 0;
		if(ansicht==NORMAL_ANSICHT){
			setLockStatement((belegung[aktiveSpalte[2]]+1 >=10 ? Integer.toString(belegung[aktiveSpalte[2]]+1)+"BEHANDLER" : "0"+(belegung[aktiveSpalte[2]]+1)+"BEHANDLER"),aktuellerTag);
		}else if(ansicht==WOCHEN_ANSICHT){
			if(aktiveSpalte[2] == 0){
				setLockStatement((wochenbelegung >=10 ? Integer.toString(wochenbelegung)+"BEHANDLER" : "0"+(wochenbelegung)+"BEHANDLER"),getWocheErster() );											
			}else{
				setLockStatement((wochenbelegung >=10 ? Integer.toString(wochenbelegung)+"BEHANDLER" : "0"+(wochenbelegung)+"BEHANDLER"),DatFunk.sDatPlusTage(getWocheErster(),aktiveSpalte[2]) );											
			}
		}
		
		new Thread(new LockRecord()).start();
		long zeit = System.currentTimeMillis();
		while(lockok == 0){
			try {
				Thread.sleep(20);
				if((System.currentTimeMillis()-zeit) > 2500){
					JOptionPane.showMessageDialog(null,"Fehler im Lock-Mechanismus -> Funktion LockVorbereiten(), bitte informieren Sie den Entwickler");
					SqlInfo.loescheLocksMaschine();
					lockok = -1;
				}
			} catch (InterruptedException e1) {
				JOptionPane.showMessageDialog(null, "Fehler im Modul lockVorbereiten");
				e1.printStackTrace();
				SqlInfo.loescheLocksMaschine();
			}
		}
		return lockok;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		for(int i = 0; i < 1; i++){
			if(((AbstractButton) arg0.getSource()).getText() == "Gruppierung zusammenfassen"){
				blockSetzen(8);
				oSpalten[aktiveSpalte[2]].repaint();
				break;
			}
			if(((AbstractButton) arg0.getSource()).getText() == "Terminliste aufrufen"){
				terminListe();
				break;
			}
			if(((AbstractButton) arg0.getSource()).getText() == "Schnellsuche (Heute + 4 Tage)"){
				schnellSuche();
				break;
			}
			if( (((AbstractButton) arg0.getSource()).getText() == "einen Tag vorwärts blättern") ||
					(((AbstractButton) arg0.getSource()).getText() == "eine Woche vorwärts blättern")) {
				setUpdateVerbot(true);
				tagBlaettern(1);
				setUpdateVerbot(false);				
				oSpalten[aktiveSpalte[2]].requestFocus();
				break;
			}

			if( (((AbstractButton) arg0.getSource()).getText() == "einen Tag rückwärts blättern") ||
			(((AbstractButton) arg0.getSource()).getText() == "eine Woche rückwärts blättern")) {
				setUpdateVerbot(true);
				tagBlaettern(-1);
				setUpdateVerbot(false);				
				oSpalten[aktiveSpalte[2]].requestFocus();				
				break;
			}
			if(((AbstractButton) arg0.getSource()).getText() == "Behandler-Set aufrufen"){
				setUpdateVerbot(true);
				setAufruf(MouseInfo.getPointerInfo().getLocation());
				setUpdateVerbot(false);				
				oSpalten[aktiveSpalte[2]].requestFocus();
				break;
			}
			if(((AbstractButton) arg0.getSource()).getText() == "Datums-Dialog aufrufen"){
				setUpdateVerbot(true);
				tagSprung(this.aktuellerTag,0);	
				setUpdateVerbot(false);
				oSpalten[aktiveSpalte[2]].requestFocus();
				break;
			}
			if(((AbstractButton) arg0.getSource()).getText() == "Termin mit Vorgängertermin tauschen"){
				setUpdateVerbot(true);
				tauscheTermin(-1);
				setUpdateVerbot(false);
				oSpalten[aktiveSpalte[2]].requestFocus();
				break;
			}
			if(((AbstractButton) arg0.getSource()).getText() == "Termin mit Nachfolgetermin tauschen"){
				setUpdateVerbot(true);
				tauscheTermin(1);
				setUpdateVerbot(false);
				oSpalten[aktiveSpalte[2]].requestFocus();
				break;
			}
			if(((AbstractButton) arg0.getSource()).getText() == "Arbeitszeitdefinition in Terminkalender übertragen"){
				if(maskenbelegung < 1){
					JOptionPane.showMessageDialog(null,"Um die AZ-Definition in den Terminkalender zu übertragen empfiehlt es sich erst auszuwählen\nwelche(!) Definition übertragen werden soll....");
					return;
				}
				if(!Rechte.hatRecht(Rechte.Masken_uebertragen, true)){
					return;
				}
				setUpdateVerbot(true);
				mb = new MaskeInKalenderSchreiben(Reha.thisFrame,maskenbelegung,(Vector) vTerm.clone());
				mb.setSize(new Dimension(700,430));
	 			mb.setLocation(new Point(250,200));
				mb.setVisible(true);
				mb.setModal(true);
				setUpdateVerbot(false);
				oSpalten[aktiveSpalte[2]].requestFocus();
				break;
			}
			if(((AbstractButton) arg0.getSource()).getText() == "Patient suchen (über Rezept-Nummer)"){
				doPatSuchen();
			}
			if(((AbstractButton) arg0.getSource()).getText() == "Telefonliste aller Patienten (über Rezept-Nummer)"){
				doTelefonListe();
			}
			if(((AbstractButton) arg0.getSource()).getText() == "kopieren"){
				int xaktBehandler  = -1;
				datenInSpeicherNehmen();
				if(terminVergabe.size() > 0){
					terminVergabe.clear();
				}
				if(ansicht == NORMAL_ANSICHT){
					xaktBehandler = belegung[aktiveSpalte[2]];
				}else  if(ansicht == WOCHEN_ANSICHT){
					xaktBehandler = aktiveSpalte[2]; 
				}else  if(ansicht == MASKEN_ANSICHT){
					xaktBehandler = aktiveSpalte[2];
				}
				terminAufnehmen(xaktBehandler,aktiveSpalte[0]);
				break;
			}
			if(((AbstractButton) arg0.getSource()).getText() == "ausschneiden"){
				if((!Rechte.hatRecht(Rechte.Kalender_termindelete, true))){
					wartenAufReady = false;
					shiftGedrueckt = false;
					gruppierenAktiv = false;
//					oSpalten[tspalte].requestFocus();
					break;
				}
				long zeit = System.currentTimeMillis();
				boolean grobRaus = false;
				while(wartenAufReady){
					try {
						Thread.sleep(20);
						if( (System.currentTimeMillis()-zeit) > 1500){
							grobRaus = true;
							break;
						}
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
				if(!grobRaus){
					wartenAufReady = true;
					testeObAusmustern();
					blockSetzen(11);								
				}else{
					SqlInfo.loescheLocksMaschine();
					wartenAufReady = false;								
				}
//				oSpalten[tspalte].requestFocus();
				break;
			}
			if(((AbstractButton) arg0.getSource()).getText() == "einfügen"){
				long zeit = System.currentTimeMillis();
				boolean grobRaus = false;
				while(wartenAufReady){
					try {
						Thread.sleep(20);
						if( (System.currentTimeMillis()-zeit) > 1500){
							grobRaus = true;
							break;
						}
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
				if(!grobRaus){
					wartenAufReady = true;
					terminGedropt = false;
					terminBreak = false;
					datenAusSpeicherHolen();								
				}else{
					wartenAufReady = false;
					SqlInfo.loescheLocksMaschine();
				}
				
				shiftGedrueckt = false;
				gruppierenAktiv = false;
				gruppierenBloecke[0] = -1;
				gruppierenBloecke[1] = -1;	
				oSpalten[gruppierenSpalte].setInGruppierung(false);
//				oSpalten[tspalte].requestFocus();							
				break;
			}
			if (((AbstractButton) arg0.getSource()).getText() == "bestätigen"){
				terminBestaetigen(1,false /*TODO kennt tspalte nicht (ersatzweise 1); tspalte wird auch garnicht gelesen ?!*/);
				gruppeAusschalten();
				//oSpalten[tspalte].requestFocus();
				break;
			}
		}
	}
	private void doTelefonListe(){
		int xaktBehandler= 0;
		if(aktiveSpalte[0] < 0){
			return;
		}
		if(ansicht == NORMAL_ANSICHT){
			xaktBehandler = belegung[aktiveSpalte[2]];
		}else  if(ansicht == WOCHEN_ANSICHT){
			xaktBehandler = aktiveSpalte[2]; 
		}else  if(ansicht == MASKEN_ANSICHT){
			JOptionPane.showMessageDialog(null,"Patientenzuordnung in Definition der Wochenarbeitszeit nicht möglich");
			return;
		}
		if(xaktBehandler < 0){
			return;
		}
		final int fxaktBehandler = xaktBehandler;
		
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				KurzAufrufe.starteFunktion("Telefonliste",vTerm.get(fxaktBehandler),null);
				return null;
			}
		}.execute();
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				RehaSplash rspl = new RehaSplash(null,"Telefonliste starten -  dieser Vorgang kann einige Sekunden dauern...");
				long zeit = System.currentTimeMillis();
				while(true){
					Thread.sleep(20);
					if(System.currentTimeMillis()-zeit > 3000){
						break;
					}
				}
				rspl.dispose();
				return null;
			}
		}.execute();
	}
	private void doPatSuchen(){
		String pat_int;
		int xaktBehandler= 0;
		boolean inhistorie = false;
		if(aktiveSpalte[0] < 0){
			return;
		}
		if(ansicht == NORMAL_ANSICHT){
			xaktBehandler = belegung[aktiveSpalte[2]];
		}else  if(ansicht == WOCHEN_ANSICHT){
			xaktBehandler = aktiveSpalte[2]; 
		}else  if(ansicht == MASKEN_ANSICHT){
			JOptionPane.showMessageDialog(null,"Patientenzuordnung in Definition der Wochenarbeitszeit nicht möglich");
			return;
		}
		if(xaktBehandler < 0){
			return;
		}
		String reznr = ((String) ((ArrayList<Vector<String>>) vTerm.get(xaktBehandler)).get(1).get(aktiveSpalte[0]));
		int ind = reznr.indexOf("\\");
		if(ind >= 0){
			reznr = reznr.substring(0,ind);
		}
		Vector vec = SqlInfo.holeSatz("verordn", "pat_intern", "rez_nr='"+reznr+"'",(List) new ArrayList() );
		if(vec.size()==0){
			vec = SqlInfo.holeSatz("lza", "pat_intern", "rez_nr='"+reznr+"'",(List) new ArrayList() );
			if(vec.size() > 0){
				JOptionPane.showMessageDialog(null, "Achtung das Rezept ist bereits abgerechnet und befindet sich in der Historie");
				inhistorie = true;
			}
		}
		if(vec.size() == 0){
			JOptionPane.showMessageDialog(null,"Rezept nicht gefunden!\nIst die eingetragene Rezeptnummer korrekt?");
			return;
		}
		
		vec = SqlInfo.holeSatz("pat5", "pat_intern", "pat_intern='"+vec.get(0)+"'",(List) new ArrayList() );
		if(vec.size() == 0){
			JOptionPane.showMessageDialog(null,"Patient mit zugeordneter Rezeptnummer -> "+reznr+" <- wurde nicht gefunden");
			return;
		}
		pat_int = (String) vec.get(0);
		JComponent patient = AktiveFenster.getFensterAlle("PatientenVerwaltung");
		final String xreznr = reznr;
		final boolean xinhistorie = inhistorie;
		if(patient == null){
			final String xpat_int = pat_int;
			new SwingWorker<Void,Void>(){
				protected Void doInBackground() throws Exception {
					JComponent xpatient = AktiveFenster.getFensterAlle("PatientenVerwaltung");
					Reha.thisClass.progLoader.ProgPatientenVerwaltung(1);
					long whilezeit = System.currentTimeMillis(); 
					while( (xpatient == null) ){
						Thread.sleep(20);
						xpatient = AktiveFenster.getFensterAlle("PatientenVerwaltung");
						if(System.currentTimeMillis()-whilezeit > 2500){
							break;
						}
					}
					whilezeit = System.currentTimeMillis();
					while(  (!AktuelleRezepte.initOk) ){
						Thread.sleep(20);
						if(System.currentTimeMillis()-whilezeit > 2500){
							break;
						}
					}
					
					String s1 = "#PATSUCHEN";
					String s2 = (String) xpat_int;
					PatStammEvent pEvt = new PatStammEvent(Reha.thisClass.terminpanel);
					pEvt.setPatStammEvent("PatSuchen");
					pEvt.setDetails(s1,s2,"#REZHOLEN-"+xreznr) ;
					PatStammEventClass.firePatStammEvent(pEvt);
					if(xinhistorie){
						Reha.thisClass.patpanel.getTab().setSelectedIndex(1);	
					}else{
						Reha.thisClass.patpanel.getTab().setSelectedIndex(0);
					}
					return null;
				}
				
			}.execute();
		}else{
			Reha.thisClass.progLoader.ProgPatientenVerwaltung(1);
			String s1 = "#PATSUCHEN";
			String s2 = (String) pat_int;
			PatStammEvent pEvt = new PatStammEvent(Reha.thisClass.terminpanel);
			pEvt.setPatStammEvent("PatSuchen");
			pEvt.setDetails(s1,s2,"#REZHOLEN-"+xreznr) ;
			PatStammEventClass.firePatStammEvent(pEvt);
			if(xinhistorie){
				Reha.thisClass.patpanel.getTab().setSelectedIndex(1);	
			}else{
				Reha.thisClass.patpanel.getTab().setSelectedIndex(0);
			}

		}
	}
	private void tauscheTermin(int richtung){
		if(!Rechte.hatRecht(Rechte.Kalender_terminanlegenvoll, true)){
			return;
		}
		String[][] tauschTermine = {{null,null,null,null,null},{null,null,null,null,null}};
		int behandler=-1,block=-1,blockanzahl=-1,blockmax;
		if(ansicht==NORMAL_ANSICHT){
			behandler = belegung[aktiveSpalte[2]];
		}else if(ansicht==WOCHEN_ANSICHT){
			behandler = aktiveSpalte[2];
		}else if(ansicht==MASKEN_ANSICHT){
			behandler = aktiveSpalte[2];
		}
		block = aktiveSpalte[0];
		blockmax = ((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(0)).size();
	
		for(int i = 0;i<1;i++){
			if(richtung < 0){
				// mit Vorg�ngertermin tauschen;
				if(block == 0){
					JOptionPane.showMessageDialog(null,"Sie sind bereits auf dem ersten Termin und dieser hat in der Regel keinen Vorgänger....");
					return;
				}else{
					blockSetzen(12);
					aktiveSpalte[0]=aktiveSpalte[0]-1;
					neuerBlockAktiv(aktiveSpalte[0]);
					oSpalten[aktiveSpalte[2]].schwarzAbgleich(aktiveSpalte[0], aktiveSpalte[0]);
					oSpalten[aktiveSpalte[2]].repaint();
				}
			}else{
				// mit Nachfolgetermin tauschen
				if(block == (blockmax-1)){
					JOptionPane.showMessageDialog(null,"Sie sind bereits auf dem letzten Termin und dieser hat in der Regel keinen Nachfolger....");
					return;
				}else{
					blockSetzen(13);
					aktiveSpalte[0]=aktiveSpalte[0]+1;
					neuerBlockAktiv(aktiveSpalte[0]);
					oSpalten[aktiveSpalte[2]].schwarzAbgleich(aktiveSpalte[0], aktiveSpalte[0]);

					oSpalten[aktiveSpalte[2]].repaint();
				}
			}
		}
	}
	public void springeAufDatum(String datum){
		//tagBlaettern((int)DatFunk.TageDifferenz(this.aktuellerTag,datum));
		datGewaehlt = datum;
		if(ansicht == WOCHEN_ANSICHT){
			this.wocheAktuellerTag = DatFunk.WocheErster(datum);	
		}
		suchSchonMal();
		//tagSprung(datum,(int)DatFunk.TageDifferenz(this.aktuellerTag,datum));
	}
	private void tagSprung(String sprungdatum,int sprung){
		datGewaehlt = null;

	        if(ansicht == NORMAL_ANSICHT){
	        	intagWahl = true;
	        	final String datwahl = (sprung != 0 ? DatFunk.sDatPlusTage(this.aktuellerTag,sprung) : this.aktuellerTag);
				TagWahlNeu tagWahlNeu = new TagWahlNeu(Reha.thisFrame,null,datwahl);
				tagWahlNeu.setPreferredSize(new Dimension(240,170));
				tagWahlNeu.getSmartTitledPanel().setPreferredSize(new Dimension(240,170));
				tagWahlNeu.pack();
				tagWahlNeu.setLocationRelativeTo(ViewPanel);				
				tagWahlNeu.setVisible(true);
				tagWahlNeu.dispose();
				intagWahl = false;
				dragLab[aktiveSpalte[2]].setIcon(null);
				dragLab[aktiveSpalte[2]].setText("");
				tagWahlNeu = null;
	        }else if(ansicht == WOCHEN_ANSICHT){
	        	if(this.wocheAktuellerTag.isEmpty()){
	        		this.wocheAktuellerTag = this.aktuellerTag;
	        	}
	        	this.wocheAktuellerTag = DatFunk.sDatPlusTage(this.wocheAktuellerTag,(7*sprung));
				dragLab[aktiveSpalte[2]].setIcon(null);
				dragLab[aktiveSpalte[2]].setText("");
	        	String sstmt = 	ansichtStatement(this.ansicht,this.wocheAktuellerTag);
	        }
    	SetzeLabel();
	}
	public void suchSchonMal(){
		if( datGewaehlt != null && (!datGewaehlt.equals(this.aktuellerTag)) ){	        	
			this.aktuellerTag = datGewaehlt;
			String sstmt = 	ansichtStatement(this.ansicht,this.aktuellerTag);
		}
	}

	private void tagBlaettern(int richtung){
        if (ansicht == NORMAL_ANSICHT)/*Normalansicht*/{
        	this.aktuellerTag = DatFunk.sDatPlusTage(this.aktuellerTag,+richtung);
        	String sstmt = 	ansichtStatement(this.ansicht,this.aktuellerTag);		        	
        	this.oSpalten[0].requestFocus();
        	
        }else if(ansicht == WOCHEN_ANSICHT){
        	if(this.wocheAktuellerTag.isEmpty()){
        		this.aktuellerTag = DatFunk.sDatPlusTage(this.aktuellerTag,+richtung);
        		this.wocheAktuellerTag = this.aktuellerTag;
        	}
        	this.wocheAktuellerTag = DatFunk.sDatPlusTage(this.wocheAktuellerTag,+(richtung*7));
        	String sstmt = 	ansichtStatement(this.ansicht,this.wocheAktuellerTag);
        }
	}
	private void testeObAusmustern(){
		int xblock;
		int xaktBehandler=0;
		String xBehandler = "";
		if(ansicht == NORMAL_ANSICHT){
			xaktBehandler = Integer.parseInt(Integer.toString(belegung[aktiveSpalte[2]]));
			xBehandler = (String) ParameterLaden.getKollegenUeberReihe(xaktBehandler+1);
		}else  if(ansicht == WOCHEN_ANSICHT){
			xaktBehandler = Integer.parseInt(Integer.toString(aktiveSpalte[2]));
			xBehandler = (String) ParameterLaden.getKollegenUeberReihe(wocheBehandler);
		}else  if(ansicht == MASKEN_ANSICHT){
			return;
		}
		xblock = Integer.parseInt(Integer.toString(aktiveSpalte[0]));
		String nametext = ((String) ((Vector<?>) ((ArrayList<?>)  vTerm.get(xaktBehandler)).get(0)).get(xblock)).replaceAll("\u00AE"  ,"");
		String reztext = (String) ((Vector<?>) ((ArrayList<?>)  vTerm.get(xaktBehandler)).get(1)).get(xblock);
		String starttext = (String) ((Vector<?>) ((ArrayList<?>)  vTerm.get(xaktBehandler)).get(2)).get(xblock);
		String sdauer = (String) ((Vector<?>) ((ArrayList<?>)  vTerm.get(xaktBehandler)).get(3)).get(xblock);
		String stestdat = (String) ((Vector<?>) ((ArrayList<?>)  vTerm.get(xaktBehandler)).get(5)).get(4);
		terminAusmustern(stestdat+starttext,sdauer,xBehandler,nametext,reztext);
	}
	public void terminAusmustern(String tagundstart,String dauer,String behandler,String name,String reznum ){
		for(int y = 0; y < terminVergabe.size();y++ ){
			if(terminVergabe.get(y)[3].trim().equals(tagundstart.trim()) &&
					terminVergabe.get(y)[4].trim().equals(dauer.trim()) &&
					terminVergabe.get(y)[5].trim().equals(behandler.trim()) &&
					terminVergabe.get(y)[8].trim().equals(name.trim()) &&
					terminVergabe.get(y)[9].trim().equals(reznum.trim()) ){
				terminVergabe.remove(y);
				break;
			}
		}
		if(terminVergabe.size() > 0){
			Reha.thisClass.mousePositionLabel.setForeground(Color.RED);
			Reha.thisClass.mousePositionLabel.setText(Integer.toString(terminVergabe.size())+" * "+terminVergabe.get(0)[8]+" in Liste");	
		}else{
			Reha.thisClass.mousePositionLabel.setForeground(Color.BLACK);
			Reha.thisClass.mousePositionLabel.setText("Druckliste = leer");
		}
	}
	// wird gebraucht für Rechtetest gibt name des Termines zurück
	private String getAktTestTermin(String retwert){
		int retart = 0;
		if(retwert.equals("name")){
			retart = 0;
		}else if(retwert.equals("rezeptnummer")){
			retart = 1;
		}else if(retwert.equals("beginn")){
			retart = 2;
		}else if(retwert.equals("dauer")){
			retart = 3;
		}else if(retwert.equals("ende")){
			retart = 4;
		}
		int testBehandler = -1;
		int block = aktiveSpalte[0];
		if(ansicht == NORMAL_ANSICHT){
			testBehandler = belegung[aktiveSpalte[2]];
		}else  if(ansicht == WOCHEN_ANSICHT){
			testBehandler = aktiveSpalte[2]; 
		}else  if(ansicht == MASKEN_ANSICHT){
			testBehandler = aktiveSpalte[2];
		}
		if(testBehandler <= 0){
			return "terminbelegt";
		}else{
			return((String) ((Vector<?>) ((ArrayList<?>)  vTerm.get(testBehandler)).get(retart)).get(block)).trim();
		}
	}
	public void terminAufnehmen(int behandler,int block){
		String[] sTerminVergabe = {null,null,null,null,null,null,null,null,null,null,null};
		String nametext = "";
		String reztext = "";
		String sdauer = "";
		int xaktBehandler = behandler;
		if(ansicht==MASKEN_ANSICHT){
			return;
		}
		if(terminVergabe.size()>0){
			int anzahl = terminVergabe.size();
			boolean gleiche = false;
			nametext = ((String) ((Vector<?>) ((ArrayList<?>)  vTerm.get(xaktBehandler)).get(0)).get(block)).replaceAll("\u00AE"  ,"");
			reztext = (String) ((Vector<?>) ((ArrayList<?>)  vTerm.get(xaktBehandler)).get(1)).get(block);
			String starttext = (String) ((Vector<?>) ((ArrayList<?>)  vTerm.get(xaktBehandler)).get(2)).get(block);
			sdauer = (String) ((Vector<?>) ((ArrayList<?>)  vTerm.get(xaktBehandler)).get(3)).get(block);
			String stestdat = (String) ((Vector<?>) ((ArrayList<?>)  vTerm.get(xaktBehandler)).get(5)).get(4);
		
			for(int i = 0; i < anzahl;i++){
				if(terminVergabe.get(i)[8].equals(nametext) && 
						terminVergabe.get(i)[9].equals(reztext) &&
						terminVergabe.get(i)[2].equals(starttext) &&
						terminVergabe.get(i)[3].equals(stestdat+starttext)){
						gleiche = true;
						break;
				}
			}
			if(gleiche){
				try{
					Reha.thisClass.shiftLabel.setText("bereit für F2= "+nametext+"°"+reztext+"°"+sdauer+" Min.");	
				}catch(Exception ex){
					ex.printStackTrace();
					Reha.thisClass.shiftLabel.setText("");
				}				
				return;
			}
		}
		try{
			sTerminVergabe[8] = ((String) ((Vector<?>) ((ArrayList<?>)  vTerm.get(xaktBehandler)).get(0)).get(block)).replaceAll("\u00AE"  ,"");
			sTerminVergabe[9] = (String) ((Vector<?>) ((ArrayList<?>)  vTerm.get(xaktBehandler)).get(1)).get(block);
			sTerminVergabe[2] = (String) ((Vector<?>) ((ArrayList<?>)  vTerm.get(xaktBehandler)).get(2)).get(block);
			sTerminVergabe[4] = (String) ((Vector<?>) ((ArrayList<?>)  vTerm.get(xaktBehandler)).get(3)).get(block);
			sTerminVergabe[3] = (String) ((Vector<?>) ((ArrayList<?>)  vTerm.get(xaktBehandler)).get(5)).get(4);
					
			if(ansicht == NORMAL_ANSICHT){
				sTerminVergabe[5] = (String) ParameterLaden.getKollegenUeberReihe(xaktBehandler+1);
				sTerminVergabe[6] = Integer.toString(behandler+1);
			}else if(ansicht==WOCHEN_ANSICHT){
				sTerminVergabe[5] = (String) ParameterLaden.getKollegenUeberReihe(wocheBehandler);
				sTerminVergabe[6] = Integer.toString(wocheBehandler);
			}
			sTerminVergabe[10] = Integer.toString(behandler);
			sTerminVergabe[7] = Integer.toString(block);
			sTerminVergabe[1] = DatFunk.sDatInDeutsch(sTerminVergabe[3]);
			sTerminVergabe[0] = DatFunk.WochenTag(sTerminVergabe[1]);	
			sTerminVergabe[3] = sTerminVergabe[3]+sTerminVergabe[2];
			terminVergabe.add(sTerminVergabe.clone());
			if(terminVergabe.size() > 0){
				Reha.thisClass.mousePositionLabel.setForeground(Color.RED);
				Reha.thisClass.mousePositionLabel.setText(Integer.toString(terminVergabe.size())+" * "+terminVergabe.get(0)[8]+" in Liste");	
			}else{
				Reha.thisClass.mousePositionLabel.setForeground(Color.BLACK);
				Reha.thisClass.mousePositionLabel.setText("Druckliste = leer");
			}
			try{
				Reha.thisClass.shiftLabel.setText("bereit für F2= "+sTerminVergabe[8]+"°"+sTerminVergabe[9]+"°"+sTerminVergabe[4]+" Min.");	
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}catch(Exception ex){
			terminVergabe.clear();
			Reha.thisClass.mousePositionLabel.setForeground(Color.BLACK);
			Reha.thisClass.mousePositionLabel.setText("Druckliste = leer");
			Reha.thisClass.shiftLabel.setText("");
		}
		
				
		/*
		for(int y = 0; y<terminVergabe.size();y++ ){
			//System.out.println("*********************");
			for(int i = 0;i < sTerminVergabe.length;i++){
				//System.out.println(terminVergabe.get(y)[i]);		
			}
			//System.out.println("*********************");
		}
		*/
		
	}
	public void aktualisieren(){
        if(ansicht == NORMAL_ANSICHT){
        	/*String sstmt = 	*/ansichtStatement(this.ansicht,this.aktuellerTag);
        }else if(ansicht == WOCHEN_ANSICHT){
        	if(this.wocheAktuellerTag.isEmpty()){
        		this.wocheAktuellerTag = this.aktuellerTag;
        	}
        	/*String sstmt = 	*/ansichtStatement(this.ansicht,this.wocheAktuellerTag);
        }
	}

	public void terminListe(){
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run()
		 	   {
		 			df = new DruckFenster(Reha.thisFrame,terminVergabe);
		 			df.setSize(new Dimension(760,480));
		 			df.setLocation(new Point(50,150));
		 			df.setFocusTabelle();
		 			df.setVisible(true);

		 	   }
			});   
	}
	public void schnellSuche(){
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run()
		 	   {
		 			sf = new SchnellSuche(Reha.thisFrame);
				 	sf.setSize(new Dimension(720,400));
		 			sf.setLocation(new Point(250,200));
		 			sf.setVisible(true);
		 			
		 	   }
			});   
	}
	public void setAktiverBlock(int block){
		aktiveSpalte[0] = block;
	}
	@Override
	public void dragExit(DropTargetEvent dte) {
		////System.out.println(dte.getSource());
	}
	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
	}
	@Override
	public void dragOver(DropTargetDragEvent dtde) {
	}
	@Override
	public void drop(DropTargetDropEvent dtde) {
		String mitgebracht = null;
		////System.out.println("Es wurde gedroppt");
		
		if(TerminFenster.DRAG_MODE == TerminFenster.DRAG_NONE){
			oSpalten[aktiveSpalte[2]].schwarzAbgleich(aktiveSpalte[0], aktiveSpalte[0]);
			dragLab[aktiveSpalte[2]].setIcon(null);
			dragLab[aktiveSpalte[2]].setText("");
			////System.out.println("Drag_Mode == Drag_None");
			dtde.dropComplete(true);
			return;
		}
		
		////System.out.println("Drag_Mode != Drag_None");

		try {
			dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
			terminGedropt = true;
	        Transferable tr = dtde.getTransferable();
	        DataFlavor[] flavors = tr.getTransferDataFlavors();
	        for (int i = 0; i < flavors.length; i++){
	        	if(flavors[i].getRepresentationClass().toString().equals("java.lang.String")){
	        		mitgebracht  =(String) tr.getTransferData(flavors[i]).toString();
	        	}
	        	mitgebracht  = (String) tr.getTransferData(flavors[i]);
	        }
	      } catch (Throwable t) { t.printStackTrace(); }
	      dtde.dropComplete(true);

	      int x = dtde.getLocation().x;
	      int breit = TerminFlaeche.getWidth()/7;
	      for(int i = 0; i < 7;i++){
			if( (x>=(i*breit)) && (x<=((i*breit)+breit)) ){
				int[] neuint = oSpalten[i].BlockTestOhneAktivierung(dtde.getLocation().x-(i*breit),dtde.getLocation().y);

				aktiveSpalte = oSpalten[i].BlockTest(dtde.getLocation().x-(i*breit),dtde.getLocation().y,aktiveSpalte);

				oSpalten[i].schwarzAbgleich(aktiveSpalte[0], aktiveSpalte[0]);
 
				if(TerminFenster.DRAG_MODE == TerminFenster.DRAG_COPY){
				}
				if(TerminFenster.DRAG_MODE == TerminFenster.DRAG_MOVE){
				}
				int behandler=-1;
				if(ansicht==NORMAL_ANSICHT){
					behandler = belegung[i];
				}else if(ansicht==WOCHEN_ANSICHT){
					behandler = i;
				}else if(ansicht==MASKEN_ANSICHT){
					behandler = i;
				}
				if(behandler <= -1){
					//System.out.println("behandler <= -1");
					return;
				}

				String sname = (String) ((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(0)).get(aktiveSpalte[0]);
				String sreznum = (String) ((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(1)).get(aktiveSpalte[0]);


				// Hier testen ob alter Block mit Daten gefüllt war
				if(!sname.equals("")){
					int frage = JOptionPane.showConfirmDialog(null, "Wollen Sie den bisherigen Eintrag -> "+sname+
							" <- tatsächlich überschreiben?", "Achtung wichtige Benutzeranfrage!!!", JOptionPane.YES_NO_OPTION);
					if(frage==JOptionPane.NO_OPTION){
						break;
					}
				}
				String[] teilen;
				////System.out.println("D&DÜbergabe = "+mitgebracht);
				if(mitgebracht.indexOf("°") >= 0 ){
					teilen = mitgebracht.split("°");
					if(! teilen[0].contains("TERMDAT")){
						//System.out.println("! teilen[0].contains('TERMDAT')");
						return;
					}
					
					if((altaktiveSpalte[0]==aktiveSpalte[0]) && (altaktiveSpalte[2]==aktiveSpalte[2]) &&
							sname.equals(teilen[1]) && sreznum.equals(teilen[2])){
						//System.out.println("altaktiveSpalte[0]==aktiveSpalte[0]) && (altaktiveSpalte[2]==aktiveSpalte[2])");
						return;
					}
					
					teilen[3] = teilen[3].toUpperCase();
					teilen[3] = teilen[3].replaceAll(" MIN.", "");

					datenSpeicher[0]= teilen[1];		
					datenSpeicher[1]= teilen[2];		
					datenSpeicher[3]= teilen[3];
					try{
						terminBreak = false;
						datenAusSpeicherHolen();
					}catch(Exception ex){
						ex.printStackTrace();
					}
					if(terminBreak){
						try{
						oSpalten[altaktiveSpalte[2]].spalteDeaktivieren();
						//System.out.println("oSpalten[altaktiveSpalte[2]].spalteDeaktivieren()");
						}catch(Exception ex){}
						return;
					}
					int[] spaltneu = aktiveSpalte.clone();
					if(TerminFenster.DRAG_MODE == TerminFenster.DRAG_MOVE){

						long zeit = System.currentTimeMillis();
						boolean grobRaus = false;
						while(getUpdateVerbot()){
							try {
								Thread.sleep(20);
								if( (System.currentTimeMillis()-zeit) > 2500){
									grobRaus = true;
									break;
								}
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
						}
						//System.out.println("Wert von Grobraus = "+grobRaus);
						zeit = System.currentTimeMillis();
						if(!grobRaus){
							try{
							String sbeginnneu = (String) ((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(2)).get(spaltneu[0]); 
							int spAktiv = aktiveSpalte[2];
							aktiveSpalte = altaktiveSpalte.clone();
							wartenAufReady = true;
							grobRaus = false;
							setUpdateVerbot(true);
							/*
							//xx
							while(getUpdateVerbot()){
								try {
									Thread.sleep(20);
									if( (System.currentTimeMillis()-zeit) > 2500){
										grobRaus = true;
										break;
									}
								} catch (InterruptedException e1) {
									e1.printStackTrace();
								}
							}
							*/
							//System.out.println("Wert von Grobraus = "+grobRaus);
							if(!grobRaus){
								//Stufe 2 - o.k.
								if(altaktiveSpalte[2]==spAktiv){
									
									//System.out.println("Termin verschieben und zwar in der selben Spalte");//
									String sbeginn = (String) ((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(2)).get(altaktiveSpalte[0]) ;
									int lang = ((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(0)).size();
									//Suche nach Uhrzeit -> "+sbeginn
									for(int i2 = 0 ; i2 < lang; i2++){
										if( ((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(2)).get(i2).equals(DRAG_UHR)){
											aktiveSpalte[0] = i2;
											aktiveSpalte[1] = i2;
											try{
												String tagundstart = (String) ((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(5)).get(4);
												tagundstart = tagundstart+((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(2)).get(i2);
												String altdauer = (String) ((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(3)).get(i2);
												String altname = (String) ((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(0)).get(i2);
												String altrezept = (String) ((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(1)).get(i2);
												String altbehandler="";
												if(ansicht == NORMAL_ANSICHT){
													altbehandler = (String) ParameterLaden.getKollegenUeberReihe(behandler+1);
												}else if(ansicht==WOCHEN_ANSICHT){
													altbehandler = (String) ParameterLaden.getKollegenUeberReihe(wocheBehandler);
												}
												terminAusmustern(tagundstart,altdauer,altbehandler,altname,altrezept);

											}catch(Exception ex){
												ex.printStackTrace();
											}
											wartenAufReady = true;
											blockSetzen(11);
											break;
										}
									}
								}else{
									//System.out.println("Termin verschieben aber nicht in der selben Spalte");
									//
									wartenAufReady = true;
									
									int ialtbehandler = 0;
									int i2 = altaktiveSpalte[2];
									int ibehandlung = altaktiveSpalte[0];
									if(ansicht==NORMAL_ANSICHT){
								    	  ialtbehandler = belegung[i2];
								     }else if(ansicht==WOCHEN_ANSICHT){
								    	  ialtbehandler = i2;
								     }else if(ansicht==MASKEN_ANSICHT){
								    	  ialtbehandler = i2;
								    }	

									String tagundstart = (String) ((Vector<?>)((ArrayList<?>) vTerm.get(ialtbehandler)).get(5)).get(4);
									tagundstart = tagundstart+((Vector<?>)((ArrayList<?>) vTerm.get(ialtbehandler)).get(2)).get(ibehandlung);
									String altdauer = (String) ((Vector<?>)((ArrayList<?>) vTerm.get(ialtbehandler)).get(3)).get(ibehandlung);
									String altname = (String) ((Vector<?>)((ArrayList<?>) vTerm.get(ialtbehandler)).get(0)).get(ibehandlung);
									String altrezept = (String) ((Vector<?>)((ArrayList<?>) vTerm.get(ialtbehandler)).get(1)).get(ibehandlung);
									String altbehandler="";
									if(ansicht == NORMAL_ANSICHT){
										altbehandler = (String) ParameterLaden.getKollegenUeberReihe(ialtbehandler+1);
									}else if(ansicht==WOCHEN_ANSICHT){
										altbehandler = (String) ParameterLaden.getKollegenUeberReihe(wocheBehandler);
									}
									////System.out.println( tagundstart+"/"+altdauer+"/"+altbehandler+"/"+altname+"/"+altrezept);
									terminAusmustern(tagundstart,altdauer,altbehandler,altname,altrezept);

									blockSetzen(11);
									aktiveSpalte = spaltneu.clone();
								}
								
								int lang = ((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(0)).size();
									for(int i2 = 0 ; i2 < lang; i2++){
										if( ((String)((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(2)).get(i2)).trim().equals(sbeginnneu.trim())){
											aktiveSpalte[0] = i2;
											aktiveSpalte[1] = i2;
											break;
										}
									}
							}else{
								aktiveSpalte = spaltneu.clone();
								wartenAufReady = false;
								SqlInfo.loescheLocksMaschine();
							}
							aktiveSpalte = spaltneu.clone();
							}catch(Exception ex){
								ex.printStackTrace();
								SqlInfo.loescheLocksMaschine();
							}
						}else{
							aktiveSpalte = spaltneu.clone();
							wartenAufReady = false;
							SqlInfo.loescheLocksMaschine();
						}					
					}
					if((spaltneu[2] != altaktiveSpalte[2]) && (altaktiveSpalte[2]>=0)){
						oSpalten[altaktiveSpalte[2]].spalteDeaktivieren();
					}
					
				}
				try{
					for(int x2 = 0; x2 < 1; x2++){
						String name = (String) ((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(0)).get(aktiveSpalte[0]+1);
						String nummer = (String)((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(1)).get(aktiveSpalte[0]+1);
						if(name.trim().equals(DRAG_PAT.trim()) && nummer.equals(DRAG_NUMMER.trim())){
							aktiveSpalte[0]++;
							aktiveSpalte[1]++;
							break;
						}
						name = (String) ((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(0)).get(aktiveSpalte[0]-1);
						nummer = (String)((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(1)).get(aktiveSpalte[0]-1);
						if(name.trim().equals(DRAG_PAT.trim()) && nummer.equals(DRAG_NUMMER.trim())){
							aktiveSpalte[0]--;
							aktiveSpalte[1]--;
							break;
						}
					}

				}catch(Exception ex){ex.printStackTrace();}
				if(altaktiveSpalte[2] >= 0){
					dragLab[aktiveSpalte[2]].setIcon(null);
					dragLab[aktiveSpalte[2]].setText("");
					dragLab[altaktiveSpalte[2]].setIcon(null);
					dragLab[altaktiveSpalte[2]].setText("");
				}
				oSpalten[i].schwarzAbgleich(aktiveSpalte[0],aktiveSpalte[0] );
			}else{
				oSpalten[i].spalteDeaktivieren();
			}

			
		}
		
	}
	public static void setDragMode(int mode){
		TerminFenster.DRAG_MODE = mode;
	}
	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
	}
	@Override
	public void dragDropEnd(DragSourceDropEvent dsde) {
	}
	@Override
	public void dragEnter(DragSourceDragEvent dsde) {
	}
	@Override
	public void dragExit(DragSourceEvent dse) {
	}
	@Override
	public void dragOver(DragSourceDragEvent dsde) {
	}
	@Override
	public void dropActionChanged(DragSourceDragEvent dsde) {
	}
	@Override
	public void dragGestureRecognized(DragGestureEvent arg0) {
	}
	public void terminBestaetigen(int spalte,boolean forceDlg){
		if ( (Rechte.hatRecht(Rechte.Kalender_terminconfirminpast, false)) || (this.getAktuellerTag().equals(DatFunk.sHeute())) ){
			gruppeAusschalten();
		} else if (!Rechte.hatRecht(Rechte.Kalender_terminconfirminpast, true)){
			gruppeAusschalten();
			return;
		}
		if (ansicht == WOCHEN_ANSICHT){
			JOptionPane.showMessageDialog(null,"Behandlungsbestätigung ist nur für den aktuellen Tag in der -> Normalansicht <- möglich");
			gruppeAusschalten();
			return;
		}
		String pat_int;
		int xaktBehandler= 0;
		if(aktiveSpalte[0] < 0){
			gruppeAusschalten();
			return;
		}
		if(ansicht == NORMAL_ANSICHT){
			xaktBehandler = belegung[aktiveSpalte[2]];
		}else  if(ansicht == WOCHEN_ANSICHT){
			xaktBehandler = aktiveSpalte[2]; 
		}else  if(ansicht == MASKEN_ANSICHT){
			JOptionPane.showMessageDialog(null,"Terminaufnahme in Definition der Wochenarbeitszeit nicht möglich");
			gruppeAusschalten();
			return;
		}
		if(xaktBehandler < 0){
			gruppeAusschalten();
			return;
		}
		String sname = ((String) ((Vector<?>)((ArrayList<?>) vTerm.get(xaktBehandler)).get(0)).get(aktiveSpalte[0]));
		String sreznum = ((String) ((Vector<?>)((ArrayList<?>) vTerm.get(xaktBehandler)).get(1)).get(aktiveSpalte[0]));
		String sorigreznum = sreznum;
		String sbeginn = ((String) ((Vector<?>)((ArrayList<?>) vTerm.get(xaktBehandler)).get(2)).get(aktiveSpalte[0]));
		String sende = ((String) ((Vector<?>)((ArrayList<?>) vTerm.get(xaktBehandler)).get(4)).get(aktiveSpalte[0]));

		String sdatum = ((String) ((Vector<?>)((ArrayList<?>) vTerm.get(xaktBehandler)).get(5)).get(4));
		int occur = -1;
		if( (occur = sreznum.indexOf("\\")) > -1){
			sorigreznum = sreznum.replace("\\", "\\\\"); 
			sreznum = sreznum.substring(0,occur);
		}
		//Rezeptnummer = "+sreznum
		if(sreznum.length()<=2){
			JOptionPane.showMessageDialog(null, "Falsche oder nicht vorhandene Rezeptnummer");
			gruppeAusschalten();
			return;
		}

		final String swreznum = sreznum;
		final String sworigreznum = sorigreznum;		
		final String swaltname = sname;
		final String swname = sname.replaceAll("\u00AE"  ,"");
		final String swbeginn = sbeginn;
		final String swende = sende;
		final String swdatum = sdatum;
		 

		final int swbehandler = xaktBehandler;
		
		final boolean xforceDlg = forceDlg;

		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				/*****************************************/
				int i,j,count =0;
				boolean doppelBeh = false;
				int doppelBehA = 0, doppelBehB = 0;
				boolean springen = false; // unterdrückt die Anzeige des TeminBestätigenAuswahlFensters
				Vector<BestaetigungsDaten> hMPos= new Vector<BestaetigungsDaten>();
				hMPos.add(new BestaetigungsDaten(false, "./.", 0, 0));
				hMPos.add(new BestaetigungsDaten(false, "./.", 0, 0));
				hMPos.add(new BestaetigungsDaten(false, "./.", 0, 0));
				hMPos.add(new BestaetigungsDaten(false, "./.", 0, 0));
				Vector<String> vec = null;
				String copyright = "\u00AE"  ;
				int iposindex = -1;
				boolean erstedoppel = true;

				try{
					// die anzahlen 1-4 werden jetzt zusammenhängend ab index 11 abgerufen
					vec = SqlInfo.holeSatz("verordn", "termine,pos1,pos2,pos3,pos4,hausbes,unter18,jahrfrei,pat_intern,preisgruppe,zzregel,anzahl1,anzahl2,anzahl3,anzahl4,preisgruppe", "rez_nr='"+swreznum+"'", Arrays.asList(new String[] {}));
					if (vec.size() > 0){
						StringBuffer termbuf = new StringBuffer();
						termbuf.append((String) vec.get(0));
						if(termbuf.toString().contains(swdatum)){
							JOptionPane.showMessageDialog(null, "Dieser Termin ist am "+DatFunk.sDatInDeutsch(swdatum)+" bereits erfasst");
							gruppeAusschalten();
							return null; 
						}
						Vector<ArrayList<?>> termine = RezTools.holePosUndAnzahlAusTerminen(swreznum);
						for (i=0;i<=3;i++){
							if(vec.get(1+i).toString().trim().equals("")){
								hMPos.get(i).hMPosNr = "./.";
								hMPos.get(i).vOMenge = 0;
							}else{
								hMPos.get(i).hMPosNr = String.valueOf(vec.get(1+i));
								hMPos.get(i).vOMenge = Integer.parseInt( (String) vec.get(i+11) );
							}
							//Hier wart noch ein Jenseits-Fehler drin z.B. bei 6xKG,6xKG,1xEis (= 12 x KG als Doppelbehandlung plus 1 x Eis)
							//vermutlich aufgrund von der von mir zusammengemurksten Funktion -> holePosUndAnzahlAusTerminen(String string)
							count = 0; // Anzahl bereits bestätigter Termine mit dieser HMPosNr
							if (!hMPos.get(i).hMPosNr.equals("./.")){
								//Vector<ArrayList<?>> termine = RezTools.holePosUndAnzahlAusTerminen(swreznum);
								if ( (iposindex=termine.get(0).indexOf(hMPos.get(i).hMPosNr)) >=0 &&
									(termine.get(0).lastIndexOf(hMPos.get(i).hMPosNr) == iposindex)	){
									//Einzeltermin
									count = Integer.parseInt(termine.get(1).get(termine.get(0).indexOf(hMPos.get(i).hMPosNr)).toString());
								}else if((iposindex=termine.get(0).indexOf(hMPos.get(i).hMPosNr)) >=0 &&
										(termine.get(0).lastIndexOf(hMPos.get(i).hMPosNr) != iposindex)	){
									//Doppeltermin
									if(!erstedoppel){
										doppelBehB = i;	
										count = Integer.parseInt(termine.get(1).get(termine.get(0).lastIndexOf(hMPos.get(i).hMPosNr)).toString());
									}else{
										doppelBehA = i;
										doppelBeh = true;
										erstedoppel = false;
										count = Integer.parseInt(termine.get(1).get(termine.get(0).indexOf(hMPos.get(i).hMPosNr)).toString());
									}
								}
							}
							hMPos.get(i).anzBBT = count; //außerhalb der if-Abfrage i.O. -> dann anzBBT = count(==0)
						}
						/*
						System.out.println("**********************");
							System.out.println(" Doppelbehandlung = "+doppelBeh);
							System.out.println("Position Doppel 1 = "+doppelBehA);
							System.out.println("Position Doppel 2 = "+doppelBehB);
						for(int ix = 0; ix < 4;ix++){
							System.out.println("\n    Positions-Nr. = "+hMPos.get(ix).hMPosNr);
							System.out.println(" verordnete Menge = "+hMPos.get(ix).vOMenge);
							System.out.println("bereits geleistet = "+hMPos.get(ix).anzBBT);
						}
						System.out.println("**********************");
						*/

					
						count = 0; //Prüfen, ob es nur eine HMPos gibt, bei der anzBBT < vOMenge; dann überspringe AuswahlFenster und bestätige diese HMPos
						//alternativ: nur die beiden Doppelbehandlungspositionen sind noch offen
						for (i=0; i<=3; i++){
							if (hMPos.get(i).anzBBT < hMPos.get(i).vOMenge){
								count++;
							}
						}
						if (count == 1){
							for (i=0; i<=3; i++){
								if (hMPos.get(i).anzBBT < hMPos.get(i).vOMenge){
									hMPos.get(i).best = true;
									springen = true;
									break;
								}
							}
						}else if ((count == 2) && doppelBeh && (hMPos.get(doppelBehA).anzBBT < hMPos.get(doppelBehA).vOMenge)){
								hMPos.get(doppelBehA).best = true;
								hMPos.get(doppelBehB).best = true;
								springen = true; // false: Auswalfenster bei Doppelbehandlungen trotzdem anzeigen
						}

						count = 0; // Prüfen, ob alle HMPos bereits voll bestätigt sind
						for (i=0; i<=3; i++){
							if (hMPos.get(i).anzBBT == hMPos.get(i).vOMenge){
								hMPos.get(i).best = false;
								count++;
							}
						}
						//Testen ob beide oder auch nur eine der Doppelbehandlungen voll ist.
						if(doppelBeh){
							if(hMPos.get(doppelBehA).anzBBT == hMPos.get(doppelBehA).vOMenge ||
									hMPos.get(doppelBehB).anzBBT == hMPos.get(doppelBehB).vOMenge	){
								if(hMPos.get(doppelBehA).anzBBT != hMPos.get(doppelBehB).anzBBT){
									JOptionPane.showMessageDialog(null, "Achtung: sämtliche Heilmittelpositionen der Verordnung "+swreznum+" wurden bereits voll geleistet und bestätigt!\n\n" +
											"Die Doppelbehandlung wurde teilweise als Einzelbehandlung abgegeben (!!)\n"+
											"Eine weitere Einzelbehandlung darf nicht abgegeben werden (Sie haben Geld verschenkt!!!).\n\n" +
											"Bitte prüfen Sie die Verordnungsmengen und die Termindaten!");
									return null;									
								}
								count = 4;
							}
						}
						
						if (count == 4){
							JOptionPane.showMessageDialog(null, "Achtung: sämtliche Heilmittelpositionen der Verordnung "+swreznum+" wurden bereits voll geleistet und bestätigt!\n\n" +
									"Eine zusätzliche Behandlung wird nicht eingetragen.\n\n" +
									"Bitte prüfen Sie die Verordnungsmengen und die Termindaten!");
							return null;
						}

						count = 0; // Prüfen, ob eine oder mehrere HMPos bereits übervoll bestätigt sind
						for (i=0; i<=3; i++){
							if (hMPos.get(i).anzBBT > hMPos.get(i).vOMenge){
								hMPos.get(i).best = false;
								count++;
							}
						}
						if (count !=0){
							JOptionPane.showMessageDialog(null, "Achtung: die verordneten Mengen der Verordnung "+swreznum+ " wurden bereits überschritten!\n\n" +
									"Eine zusätzliche Behandlung wird nicht eingetragen.\n\n" +
									"Bitte prüfen Sie die Verordnungsmengen und die bestätigten Termindaten!");
							return null;
						}
						// TerminBestätigenAuswahlFenster anzeigen oder überspringen
						if ((!springen && (Boolean)SystemConfig.hmTerminBestaetigen.get("dlgzeigen") ) || xforceDlg){ 
									TerminBestaetigenAuswahlFenster termBestAusw = new TerminBestaetigenAuswahlFenster(Reha.thisFrame,null,(Vector<BestaetigungsDaten>)hMPos,sworigreznum,Integer.parseInt((String)vec.get(15)));
									termBestAusw.pack();
									termBestAusw.setLocation(computeLocation(termBestAusw,swbeginn,swende));
									termBestAusw.setzeFocus();
									termBestAusw.setModal(true);
									termBestAusw.setVisible(true);
						}else{
							/*
							 * Der Nutzer wünscht kein Auswahlfenster:
							 * bestätige alle noch offenen Heilmittel
							 *   
							 */		
							for (i=0; i<=3; i++){
								hMPos.get(i).best = (hMPos.get(i).anzBBT < hMPos.get(i).vOMenge);
							}
						}

						count = 0; // Dialog abgebrochen

						for (i=0; i < 4 ; i++){
							count += (hMPos.get(i).best ? 1 : 0);
						}
						if (count == 0){
							return null;
						}
						
						count = 0; // Prüfe, ob der oder die letzten offene(n) Termin(e) bestätigt werden sollen: Hinweis, dass VO abgerechnet werden kann und in VolleTabelle schreiben
						for (i=0; i<=3; i++){
							if ((hMPos.get(i).anzBBT + (hMPos.get(i).best ? 1 : 0)) == hMPos.get(i).vOMenge){
								count++;
							}	
							if (count == 4){
								JOptionPane.showMessageDialog(null, "Achtung das Rezept "+swreznum+" ist jetzt voll bestätigt!\n\nBitte die Daten prüfen und zur Abrechnung weiterleiten!");
								try{
									RezTools.fuelleVolleTabelle(swreznum, ParameterLaden.getKollegenUeberDBZeile(swbehandler+1));	
								}catch(Exception ex){
									JOptionPane.showMessageDialog(null,"Fehler beim Aufruf von 'fuelleVolleTabelle'");
								}						
							}
						}	

						termbuf.append(macheNeuTermin(DatFunk.sDatInDeutsch(swdatum), ParameterLaden.getKollegenUeberDBZeile(swbehandler+1),"",
								(String) (hMPos.get(0).best ? vec.get(1) : ""),
								(String) (hMPos.get(1).best ? vec.get(2) : ""),
								(String) (hMPos.get(2).best ? vec.get(3) : ""),
								(String) (hMPos.get(3).best ? vec.get(4) : "")));
						/********************************/
						boolean unter18 =  ( ((String)vec.get(6)).equals("T") ? true : false );
						boolean vorjahrfrei = ( ((String)vec.get(7)).equals("") ? false : true );
						if(!unter18 && !vorjahrfrei){
							SqlInfo.aktualisiereSatz("verordn", "termine='"+termbuf.toString()+"'", "rez_nr='"+swreznum+"'");			
						}else if(unter18 && !vorjahrfrei){
							/// Testen ob immer noch unter 18 ansonsten ZuZahlungsstatus ändern;
							String geboren = DatFunk.sDatInDeutsch(SqlInfo.holePatFeld("geboren","pat_intern='"+vec.get(8)+"'" ));
							////System.out.println("Geboren = "+geboren);
							if(DatFunk.Unter18(DatFunk.sDatInDeutsch(swdatum), geboren)){
								SqlInfo.aktualisiereSatz("verordn", "termine='"+termbuf.toString()+"'", "rez_nr='"+swreznum+"'");				
							}else{
								SqlInfo.aktualisiereSatz("verordn", "termine='"+termbuf.toString()+"', zzstatus='2'", "rez_nr='"+swreznum+"'");				
							}

						}else if(!unter18 && vorjahrfrei){
							String bef_dat = SqlInfo.holePatFeld("befreit","pat_intern='"+vec.get(8)+"'" );
							if(!bef_dat.equals("T")){
								if(DatFunk.DatumsWert("31.12."+vec.get(8)) < DatFunk.DatumsWert(swdatum) ){
									SqlInfo.aktualisiereSatz("verordn", "termine='"+termbuf.toString()+"', zzstatus='2'", "rez_nr='"+swreznum+"'");
								}else{
									SqlInfo.aktualisiereSatz("verordn", "termine='"+termbuf.toString()+"'", "rez_nr='"+swreznum+"'");					
								}
							}else{
								SqlInfo.aktualisiereSatz("verordn", "termine='"+termbuf.toString()+"'", "rez_nr='"+swreznum+"'");
							}
						}else{
							SqlInfo.aktualisiereSatz("verordn", "termine='"+termbuf.toString()+"'", "rez_nr='"+swreznum+"'");			
						}
						/**************************************/

						/**********Datenbank beschreiben*************/
						String sblock = Integer.toString(aktiveSpalte[0]+1);
						String toupdate = "T"+sblock+" = '"+copyright+swname+"'";
						String towhere = "datum='"+swdatum+"' AND "+
						"behandler='"+( (swbehandler+1) < 10 ? "0"+Integer.toString(swbehandler+1)+"BEHANDLER" : Integer.toString(swbehandler+1)+"BEHANDLER"   )+"' "+
						"AND TS"+sblock+"='"+swbeginn+"' AND T"+sblock+"='"+swaltname+
						"' AND N"+sblock+"='"+sworigreznum+"'"; 

						SqlInfo.aktualisiereSatz("flexkc",
								toupdate,
								towhere);
						/**********Ende Datenbank beschreiben*************/
						((ArrayList<Vector<String>>) vTerm.get(swbehandler)).get(0).set(aktiveSpalte[0],copyright+swname);
						oSpalten[aktiveSpalte[2]].repaint();
						JComponent patient = AktiveFenster.getFensterAlle("PatientenVerwaltung");
						if(patient != null){
							if(Reha.thisClass.patpanel.aktRezept.rezAngezeigt.equals(swreznum)){
								Reha.thisClass.patpanel.aktRezept.updateEinzelTermine(termbuf.toString());
							}
						}
					}else{
						JOptionPane.showMessageDialog(null, "Dieses Rezept existiert nicht bzw. ist bereits abgerechnet!!");	
					}
				}catch(Exception ex){				
					ex.printStackTrace();
				}finally{
					vec = null;
					hMPos = null;					
				}
				/***********************************************************************/
				return null;
			}
		}.execute();
	}
	public static String macheNeuTermin(String datum, String kollege,String text,String pos1,String pos2,String pos3,String pos4){
		String ret =
			datum+
			"@"+
			kollege+
			"@"+
			text+
			"@"+
			/*
			pos1 + ( pos1.trim().equals("") || pos2.trim().equals("") ? "" : "," )+ 
			pos2 + ( pos2.trim().equals("") || pos3.trim().equals("") ? "" : "," )+
			pos3 + ( pos3.trim().equals("") || pos4.trim().equals("") ? "" : "," )+
			pos4 +  // TODO es gibt trotz Umstellung weiterhin drei Fälle in denen Kommas falsch gesetzt werden könnten: 1&3,2&4 bzw. 1&4 -> dann fehlen Kommas
			*/
			machePositionsString(Arrays.asList(pos1,pos2,pos3,pos4))+
			"@"+
			DatFunk.sDatInSQL(datum)+"\n";
		return ret;
	}
	private static String machePositionsString(List<String> list){
		String ret = "";
		for(int i = 0; i < list.size();i++){
			if(!list.get(i).equals("")){
				if(i==0){
					ret = ret+list.get(i);
				}else{
					if(ret.length() > 0){
						//erstes element war nicht leer
						ret = ret+","+list.get(i);
					}else{
						ret = ret+list.get(i);
					}
				}
			}
		}
		return String.valueOf(ret);
	}	
	private Point computeLocation(Window win,String start,String ende){
		int xwin = win.getWidth();
		int ywin = win.getHeight();
		
		Point p = oSpalten[aktiveSpalte[2]].getLocationOnScreen();
		try{
			float ypos = Float.valueOf(oSpalten[aktiveSpalte[2]].getFloatPixelProMinute()*
					(Float.valueOf(ZeitFunk.MinutenSeitMitternacht(ende))-Float.valueOf(ZeitFunk.MinutenSeitMitternacht(SystemConfig.KalenderUmfang[0]))) );
			//Zuerst Y-testen
			if( (Math.round(p.y+ypos+ywin) > p.y+oSpalten[aktiveSpalte[2]].getHeight()) ){
				//Fenster würde nach unten ins Nirvana abtauchen
				ypos = Float.valueOf(oSpalten[aktiveSpalte[2]].getFloatPixelProMinute()*
						(Float.valueOf(ZeitFunk.MinutenSeitMitternacht(start))-Float.valueOf(ZeitFunk.MinutenSeitMitternacht(SystemConfig.KalenderUmfang[0]))) );
				p.y = Math.round( (p.y+ypos)-ywin);
			}else{
				p.y = Math.round(p.y+ypos);			
			}
			//Jetzt X-testen damit nichts nach rechts abhaut
			if(p.x+xwin > ViewPanel.getLocationOnScreen().x +ViewPanel.getWidth()){
				p.x = (ViewPanel.getLocationOnScreen().x +ViewPanel.getWidth()) - xwin;
			}
		}catch(Exception ex){/*wird nicht ausgewertet*/}
		
		return p;
	}
	
	public void gruppeAusschalten(){
		shiftGedrueckt = false;
		gruppierenAktiv = false;
		gruppierenBloecke[0] = -1;
		gruppierenBloecke[1] = -1;
		try{
			oSpalten[gruppierenSpalte].setInGruppierung(false);
			oSpalten[gruppierenSpalte].shiftGedrueckt(false);
		}catch(Exception ex){
			
		}
	}
	


	/********************************************************************************************/
}	// Ende Klasse
/********************************************************************************************/
/********************************************************************************************/
/********************************************************************************************/
/********************************************************************************************/

class KalZeichnen implements Runnable{

	  private kalenderPanel kPanel = null;
	  private int belegung;
	  private Vector vTerm = null;

	  public KalZeichnen(kalenderPanel kPanel,Vector vTerm, int belegung){
	    this.kPanel=kPanel;
	    this.vTerm = vTerm;
	    this.belegung = belegung;
	  }

	  public void run(){
	    this.kPanel.datenZeichnen(vTerm, belegung);
	  }

}

class LockRecord implements Runnable{
	boolean gesperrt = false;
	String sstmt = "";
	Statement sState = null;
	ResultSet rs = null;
	String threadStmt = "";
	  public void SatzSperren(){
		  TerminFenster.setLockOk(0,"");
		  this.sState = TerminFenster.getThisClass().privstmt;
		  
		try {
				threadStmt = "select * from flexlock where sperre = '"+TerminFenster.getLockStatement()+"' LIMIT 1";
				rs = this.sState.executeQuery(threadStmt);
				if(!rs.next()){
					new Thread(new SetLock()).start();
					TerminFenster.setLockOk(1,"");
					TerminFenster.setLockSpalte(TerminFenster.getLockStatement());
					Reha.thisClass.messageLabel.setText("Lock erfolgreich == 1");
				}else{
					TerminFenster.setLockOk(-1,rs.getString("maschine"));
					Reha.thisClass.messageLabel.setText("Lock misslungen");
					Reha.thisClass.messageLabel.setText("Lock misslungen == -1");					
				}
				
		}catch(SQLException ex) {
				this.gesperrt = false;
				//System.out.println("von ResultSet SQLState: " + ex.getSQLState());
				//System.out.println("von ResultSet ErrorCode: " + ex.getErrorCode ());
				//System.out.println("von ResultSet ErrorMessage: " + ex.getMessage ());

				TerminFenster.setLockOk(-1," Durch Fehler in SQL-Statement:" +ex.getMessage());
				Reha.thisClass.messageLabel.setText("Lock misslungen");
				
		}

	  }
	  public void run(){
	    SatzSperren();
	  }
}

class UnlockRecord implements Runnable{
Statement sState = null;
boolean success = false;
	  public void SatzEntsperren(){
			this.sState = TerminFenster.getThisClass().privstmt;
			SqlInfo.sqlAusfuehren("delete from flexlock where maschine like '%"+SystemConfig.dieseMaschine+"%'");
	  }
	  public void run(){
	    SatzEntsperren();
	  }

}


class SetLock implements Runnable{

	  private String threadStmt = "";
	  private Statement sState = null;
	  private boolean klappt=false;;
	  public void LockSetzen(){
			threadStmt = "insert into flexlock set sperre = '"+TerminFenster.getLockStatement()+
			"' , maschine = '"+SystemConfig.dieseMaschine+"', zeit='"+Long.toString(System.currentTimeMillis())+"'";
			try {
				this.sState = TerminFenster.getThisClass().privstmt;
				
				klappt = this.sState.execute(threadStmt);
				klappt = this.sState.execute("COMMIT");

			}catch(SQLException ex) {
				SqlInfo.sqlAusfuehren("delete from flexlock where maschine like '%"+SystemConfig.dieseMaschine+"%'");
				Reha.thisClass.messageLabel.setText("Entsperren misslungen");			
				TerminFenster.setLockOk(-1," Durch Fehler in SQL-Statement:" +ex.getMessage());				
			}
	  }

	  public void run(){
	   LockSetzen();
	  }

}

class DirectLockRecord implements Runnable{
	boolean gesperrt; 
	String sstmt = "";
	Statement sState;
	ResultSet rs;
	String threadStmt = "";
	  public void SatzSperren(){
		  TerminFenster.setLockOk(0,"");
		  this.sState = TerminFenster.getThisClass().privstmt;
		  
		try {
				threadStmt = "select * from flexlock where sperre = '"+TerminFenster.getLockStatement()+"' LIMT 1";
				rs = this.sState.executeQuery(threadStmt);
				if(!rs.next()){

					threadStmt = "insert into flexlock set sperre = '"+TerminFenster.getLockStatement()+
					"' , maschine = '"+SystemConfig.dieseMaschine+"', zeit='"+Long.toString(System.currentTimeMillis())+"'";
					this.gesperrt = this.sState.execute(threadStmt);
					this.gesperrt = this.sState.execute("COMMIT");
					TerminFenster.setLockOk(1,"");
					TerminFenster.setLockSpalte(TerminFenster.getLockStatement());
					Reha.thisClass.messageLabel.setText("Lock erfolgreich");
				}else{
					SqlInfo.sqlAusfuehren("delete from flexlock where maschine like '%"+SystemConfig.dieseMaschine+"%'");
					//new ExUndHop().setzeStatement("delete from flexlock where maschine like '%"+SystemConfig.dieseMaschine+"%'");
					TerminFenster.setLockOk(-1,rs.getString("maschine"));
					Reha.thisClass.messageLabel.setText("Lock misslungen");
				}
				
			}catch(SQLException ex) {
				this.gesperrt = false;
				//System.out.println("von ResultSet SQLState: " + ex.getSQLState());
				//System.out.println("von ResultSet ErrorCode: " + ex.getErrorCode ());
				//System.out.println("von ResultSet ErrorMessage: " + ex.getMessage ());
				SqlInfo.sqlAusfuehren("delete from flexlock where maschine like '%"+SystemConfig.dieseMaschine+"%'");
				//new ExUndHop().setzeStatement("delete from flexlock where maschine like '%"+SystemConfig.dieseMaschine+"%'");
				TerminFenster.setLockOk(-1," Durch Fehler in SQL-Statement:" +ex.getMessage());
				Reha.thisClass.messageLabel.setText("Lock misslungen");
				
			}
	  }
	  public void run(){
	    SatzSperren();
	  }
}
final class sperrTest extends Thread implements Runnable{
private int gelesen;
	private void sperrTest(){
		int gelesen = 1;
	}
	
	public void run() {
	    while(true) {
	    	
	    	if (gelesen > 1){ //beim ersten Aufruf liegen bereits aktuelle Daten vor
	    		if(TerminFenster.getThisClass()==null){
	    			break;
	    		}
	    		if (!TerminFenster.getThisClass().getUpdateVerbot()){
	    			try{
	    			//Reha.thisClass.shiftLabel.setText("in Update...");
	    			TerminFenster.getThisClass().setUpdateVerbot(true);
	    			TerminFenster.getThisClass().aktualisieren();
	    			TerminFenster.getThisClass().setUpdateVerbot(false);	 
	    			//Reha.thisClass.shiftLabel.setText("Update ok.");	    			
	    			gelesen++;
	    			}catch(Exception ex){
	    				SqlInfo.loescheLocksMaschine();
	    				//SqlInfo.sqlAusfuehren("delete from flexlock where maschine like '%"+SystemConfig.dieseMaschine+"%'");
	    				break;
	    			}
	    			//********>Toolkit.getDefaultToolkit().beep();
	    		}else{
	    			//TestFenster.LabelSetzen(2,"DB-Aktualisierungsverbot");
	    			gelesen++;
	    			//Toolkit.getDefaultToolkit().beep();	    		
	    		}
	    		if(gelesen > 10000){
	    			gelesen = 2;
	    		}
	    	}else{
	    		gelesen++;
	    	}
	    		
	    	try {
	          Thread.sleep(SystemConfig.UpdateIntervall);
	        }
	        catch(InterruptedException e) {
	        }
	      }
	}
}
/*********************/
class TDragObjekt{
	public Point dragPosOnScreen = new Point(-1,-1);
	public Point dragPosInColumn = new Point(-1,-1);
	public int column = 0;
	public void init(Point dpos,Point dpic,int col){
		dragPosOnScreen = dpos;
		dragPosInColumn = dpic;
		column = col;
	}
	
}

class DragAndMove extends Thread implements Runnable{
	public static int PixelzuMinute = 0;
	public void setzeMinute(int min){
		PixelzuMinute = min;
		start();
	}

	public void run()  {
		new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {

				while(PixelzuMinute >= 0){
					//Reha.thisClass.shiftLabel.setText(""+PixelzuMinute);
					sleep(40);
				}

				return null;
			}
			
		}.execute();

	}
	
}
class DropSupport implements DropTargetListener
{
    private boolean fAccept;
    
    public void dragEnter(DropTargetDragEvent dtde)
    {
        fAccept = false;
        DataFlavor flavors[] = dtde.getCurrentDataFlavors();
        
        int i;
        fAccept = true;
    }

    public void dragExit(DropTargetEvent dte)
    {
        if (!fAccept) return;
 
    }

    public void dragOver(DropTargetDragEvent dtde)
    {
    	//Reha.thisClass.shiftLabel.setText(dtde.getLocation().toString());
    	////System.out.println("Drag-Support"+dtde);
    	if (!fAccept) return;
 
    }

    public void drop(DropTargetDropEvent dtde)
    {
        if (!fAccept) return;

 
        dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
        
        // actually do the drop
        Transferable trans = dtde.getTransferable();
        DataFlavor flavors[] = trans.getTransferDataFlavors();

        // go through all the flavors and find one I handle.
        // depending on what you're accepting, you may wish to
        // do this a different way.
        for (int i = 0; i < flavors.length; ++i) {
        }
        
        dtde.dropComplete(true);
 
    }

    public void dropActionChanged(DropTargetDragEvent dtde)
    {
        dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
    }
}
class DragSupport implements DragGestureListener{

	@Override
	public void dragGestureRecognized(DragGestureEvent arg0) {
		// TODO Auto-generated method stub
		////System.out.println("in datGasture "+arg0);
		
	}
	
}

