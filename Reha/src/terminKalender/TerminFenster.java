package terminKalender;




import generalSplash.RehaSplash;
import hauptFenster.AktiveFenster;
import hauptFenster.Reha;
import hilfsFenster.TerminEinpassen;
import hilfsFenster.TerminObenUntenAnschliessen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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


import sqlTools.SqlInfo;
import stammDatenTools.RezTools;
import systemEinstellungen.SystemConfig;
import systemTools.JRtaTextField;
import systemTools.ListenerTools;
import RehaInternalFrame.JRehaInternal;
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
	private Vector vTerm = new Vector();  //  Wird gebraucht zur Daten�bergabe auf die Spalten
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
	public String DRAG_UHR;
	public String DRAG_PAT;	
	public String DRAG_NUMMER;	
	public JRehaInternal eltern;
	
	public JXPanel init(int setOben,int ansicht,JRehaInternal eltern) {

		this.eltern = eltern;
		this.setOben = setOben;
		this.ansicht = ansicht;
		xEvent = new RehaTPEventClass();
		xEvent.addRehaTPEventListener((RehaTPEventListener)this);
		
		ViewPanel = new JXPanel(new BorderLayout());
		ViewPanel.addFocusListener(new java.awt.event.FocusAdapter() {   
			public void focusLost(java.awt.event.FocusEvent e) {    
				Reha.thisClass.shiftLabel.setText("VP Focus weg");
			
			}
			public void focusGained(java.awt.event.FocusEvent e) {
				Reha.thisClass.shiftLabel.setText("VP Focus da");
			
			}
		});
		ViewPanel.setName(eltern.getName());

		GrundFlaeche = getGrundFlaeche();
		GrundFlaeche.addFocusListener(new java.awt.event.FocusAdapter() {   
			public void focusLost(java.awt.event.FocusEvent e) {    
				Reha.thisClass.shiftLabel.setText("GF Focus weg");
			}
			public void focusGained(java.awt.event.FocusEvent e) {
				Reha.thisClass.shiftLabel.setText("GF Focus da");
				
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
				Reha.thisClass.shiftLabel.setText("VP Focus weg");
		
			}
			public void focusGained(java.awt.event.FocusEvent e) {
				Reha.thisClass.shiftLabel.setText("VP Focus da");
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

		setzeStatement();

		return ViewPanel;
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
			System.out.println("von ResultSet SQLState: " + ex.getSQLState());
			System.out.println("von ResultSet ErrorCode: " + ex.getErrorCode ());System.out.println("ErrorCode: " + ex.getErrorCode ());
			System.out.println("von ResultSet ErrorMessage: " + ex.getMessage ());
			Reha.thisClass.shiftLabel.setText("Lock misslungen");
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
						//Neuer Tag soll gew�hlt werden
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
						setAufruf();
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
						
					}

				}else if(ansicht == MASKEN_ANSICHT){
					maskenbelegung = ParameterLaden.vKKollegen.get(wahl).Reihe ;
					maskenwahl = wahl;
					String maskenbehandler = (maskenbelegung < 10 ? "0"+maskenbelegung+"BEHANDLER" : Integer.toString(maskenbelegung)+"BEHANDLER");
					String stmtmaske = "select * from masken where behandler = '"+maskenbehandler+"' ORDER BY art";
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
 * die Comboboxen mit Werden f�llen
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
					      DRAG_PAT = 	 ((String) ((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(0)).get(aktiveSpalte[0]) ).replaceAll("\u00A9 ", "");
					      DRAG_NUMMER = 	 (String) ((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(1)).get(aktiveSpalte[0]) ;
					      DRAG_UHR =   (String) ((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(2)).get(aktiveSpalte[0]) ;
					      altaktiveSpalte = aktiveSpalte.clone();
					      JLabel lab = new JLabel("TERMDATINTERN"+"°"+sdaten[0]+"°"+sdaten[1]+"°"+sdaten[3]+" Min.");
					      lab.setTransferHandler(new TransferHandler("text"));
					      TransferHandler th = lab.getTransferHandler();
					      th.exportAsDrag(lab, e, TransferHandler.COPY);
					      
					}
					public void mouseReleased(MouseEvent e) {
					      JComponent c = (JComponent)e.getSource();
					      int v = new Integer(c.getName().split("-")[1]);
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
					float fPixelProMinute = (float) iMaxHoehe;
					fPixelProMinute =(float) fPixelProMinute / 900;
					iPixelProMinute = ((int) (fPixelProMinute));
				}
			});
			TerminFlaeche.revalidate();
		}
		return TerminFlaeche;
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
			//fare niente - Terminkalender l�uft nicht!!
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
							setAufruf();
							oSpalten[tspalte].requestFocus();
							break;
						}
						if (ec==16){
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
						if ( (e.getKeyCode()==155) && (e.isControlDown()) ){
							//Daten in Speicher (f�her Aufruf �ber F2)
							//System.out.println("Strg+Einfg");
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
						if (e.getKeyCode()==155 && e.isShiftDown()){
							//Daten in den Kalender schreiben (f�her Aufruf �ber F3)
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
								datenAusSpeicherHolen();								
							}else{
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
								datenAusSpeicherHolen();								
							}else{
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
							//F7 + Schift + Alt
							blockSetzen(999);
							e.consume();
							oSpalten[tspalte].requestFocus();
							break;
						}
						if (e.getKeyCode()==118){
							//F7
							break;
						}
						if (e.getKeyCode()==119){
							//F8
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
								blockSetzen(11);								
							}else{
								wartenAufReady = false;								
							}


							e.consume();
							oSpalten[tspalte].requestFocus();
							break;
						}
						if (e.getKeyCode()==120){
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
								blockSetzen(10);								
							}else{
								wartenAufReady = false;								
							}
							oSpalten[tspalte].requestFocus();							
							//F9
							break;
						}
						if (e.getKeyCode()==122 && e.isShiftDown()){
							//F11 + Shift
							terminBestaetigen(tspalte);
							gruppeAusschalten();
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
									while(lockok == 0){
										try {
											Thread.sleep(20);
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
										sperreAnzeigen();
										setUpdateVerbot(false);
									}
								}else if(ansicht==WOCHEN_ANSICHT){	//WOCHEN_ANSICHT mu� noch entwickelt werden!
									if(aktiveSpalte[2] == 0){

										setLockStatement((wochenbelegung >=10 ? Integer.toString(wochenbelegung)+"BEHANDLER" : "0"+(wochenbelegung)+"BEHANDLER"),getWocheErster() );											
									}else{
										setLockStatement((wochenbelegung >=10 ? Integer.toString(wochenbelegung)+"BEHANDLER" : "0"+(wochenbelegung)+"BEHANDLER"),DatFunk.sDatPlusTage(getWocheErster(),aktiveSpalte[2]) );											
									}
									new Thread(new LockRecord()).start();
									while(lockok == 0){
										try {
											Thread.sleep(20);
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
										sperreAnzeigen();
										setUpdateVerbot(false);
									}
								}else if(ansicht==MASKEN_ANSICHT){	//WOCHEN_ANSICHT mu� noch entwickelt werden!
									//System.out.println("Maskenansicht-Doppelklick");
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
					Reha.thisClass.shiftLabel.setText("Spalte"+tspalte+"  / Drag:X="+e.getX()+" Y="+e.getY());
				}
				public void mouseMoved(java.awt.event.MouseEvent e) {
					dragDaten.y = e.getY();
					dragDaten.x = e.getX();
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
					submenu.setIcon(new ImageIcon(SystemConfig.homeDir+"/icons/refresh.gif"));
					submenu.add(getTauschemitvorherigem());
					submenu.add(getTauschemitnachfolger());
					jPopupMenu.add(submenu);
					jPopupMenu.addSeparator();					
					jPopupMenu.add(getBehandlerset());
					jPopupMenu.addSeparator();					
					jPopupMenu.add(getTerminedespatsuchen());
					jPopupMenu.addSeparator();
					submenu = new JMenu("Patient suchen / Telefonliste");
					submenu.setIcon(new ImageIcon(SystemConfig.homeDir+"/icons/personen16.gif"));
					submenu.add(getPatientsuchen());
					submenu.add(getTelefonliste());
					jPopupMenu.add(submenu);
					jPopupMenu.addSeparator();
					jPopupMenu.add(getTerminliste());				
					jPopupMenu.addSeparator();
					submenu = new JMenu("Termine gruppieren");
					submenu.setIcon(new ImageIcon(SystemConfig.homeDir+"/icons/failed.gif"));
					submenu.add(getGruppezusammenfassen());
					submenu.add(getGruppekopieren());
					submenu.add(getGruppeeinfuegen());
					submenu.add(getGruppeloeschen());
					jPopupMenu.add(submenu);					
					jPopupMenu.addSeparator();
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
					if(ansicht ==NORMAL_ANSICHT){
						Behandlerset.setEnabled(true);
						Wochenanzeige.setEnabled(true);
						Normalanzeige.setEnabled(false);
						Tagvor.setEnabled(true);
						Tagzurueck.setEnabled(true);					
						Tagesdialog.setEnabled(true);
						Tagvor.setText("einen Tag vorwärts blättern");
						Tagzurueck.setText("einen Tag rückwärts blättern");						
					}else if(ansicht ==WOCHEN_ANSICHT){
						Behandlerset.setEnabled(false);
						Wochenanzeige.setEnabled(false);
						Normalanzeige.setEnabled(true);
						Tagvor.setText("eine Woche vorwärts blättern");
						Tagzurueck.setText("eine Woche rückwärts blättern");
						Tagesdialog.setEnabled(false);
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
				Tauschemitvorherigem.setIcon(new ImageIcon(SystemConfig.homeDir+"/icons/oben.gif"));
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
				Tauschemitnachfolger.setIcon(new ImageIcon(SystemConfig.homeDir+"/icons/unten.gif"));				
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
				Behandlerset.setRolloverEnabled(true);
				Behandlerset.setEnabled(true);
				Behandlerset.addActionListener(this);
			}
			return Behandlerset;
		}

		private JMenuItem getTagvor() {
			if (Tagvor == null) {
				Tagvor = new JMenuItem();
				Tagvor.setText("einen Tag vorwärts blättern");
				Tagvor.setIcon(new ImageIcon(SystemConfig.homeDir+"/icons/forward.gif"));
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
				Tagzurueck.setIcon(new ImageIcon(SystemConfig.homeDir+"/icons/backward.gif"));
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
				Patientsuchen.setText("Patient suchen - Alt+P (über Rezept-Nummer)");
				Terminedespatsuchen.setActionCommand("PatRezSuchen");				
				Patientsuchen.setRolloverEnabled(true);
				Patientsuchen.addActionListener(this);
			}
			return Patientsuchen;
		}
		private JMenuItem getTelefonliste() {
			if (Telefonliste == null) {
				Telefonliste = new JMenuItem();
				Telefonliste.setText("Telefonliste aller Patienten (über Rezept-Nummer)");
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
				Terminedespatsuchen.setRolloverEnabled(true);
				Terminedespatsuchen.addActionListener(this);
			}
			return Terminedespatsuchen;
		}
		/***************************/
		private JMenuItem getNormalanzeige() {
			if (Normalanzeige == null) {
				Normalanzeige = new JMenuItem();
				Normalanzeige.setText("Normalanzeige (7 Kollegen gleicher Tag) Strg+N");
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
				Wochenanzeige.setText("Wochenanzeige (1 Kollege 7 Tage) Strg+W");
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
			tagSprung(this.aktuellerTag,1);
			if(aktiveSpalte[2]>=0){
				oSpalten[aktiveSpalte[2]].setSpalteaktiv(false);				
			}
			intagWahl = false;
			break;
		case 34: //Bild ab
			if(interminEdit){
				return;
			}
			intagWahl = true;
			tagSprung(this.aktuellerTag,-1);
			if(aktiveSpalte[2]>=0){
				oSpalten[aktiveSpalte[2]].setSpalteaktiv(false);				
			}
			intagWahl = false;
			break;

		case 38: //Pfeil auf
			if(!gruppierenAktiv){
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
			}else{
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
			}
			break;
		case 40: //Pfeil ab
			if(!gruppierenAktiv){
				if( ((aktiveSpalte[0] >= 0) && (aktiveSpalte[2] >= 0) && (belegung[aktiveSpalte[2]] >= 0)) ||
						((aktiveSpalte[0] >= 0) && (aktiveSpalte[2] >= 0) && (maskenbelegung >= 0))){
					if(ansicht==NORMAL_ANSICHT){
						try{
						anz = ((Vector<?>)((ArrayList<?>) vTerm.get(belegung[aktiveSpalte[2]
						                                                                  ])).get(0)).size();
						}catch(java.lang.ArrayIndexOutOfBoundsException ob){
							System.out.println("Spalte nicht belegt");
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
			}else{
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
				while(lockok == 0){
					try {
						Thread.sleep(20);
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
					sperreAnzeigen();
					setUpdateVerbot(false);					
				}
			}else if(ansicht==WOCHEN_ANSICHT){
				if(aktiveSpalte[2] == 0){
					setLockStatement((wochenbelegung >=10 ? Integer.toString(wochenbelegung)+"BEHANDLER" : "0"+(wochenbelegung)+"BEHANDLER"),getWocheErster() );											
				}else{
					setLockStatement((wochenbelegung >=10 ? Integer.toString(wochenbelegung)+"BEHANDLER" : "0"+(wochenbelegung)+"BEHANDLER"),DatFunk.sDatPlusTage(getWocheErster(),aktiveSpalte[2]) );											
				}
				new Thread(new LockRecord()).start();
				while(lockok == 0){
					try {
						Thread.sleep(20);
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
					sperreAnzeigen();
					setUpdateVerbot(false);					
				}
			}else if(ansicht==MASKEN_ANSICHT){	//WOCHEN_ANSICHT mu� noch entwickelt werden!
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
			Reha.thisClass.shiftLabel.setText("Update-Verbot");			
		}else{
			Reha.thisClass.shiftLabel.setText("Update ok.");			
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
	public void setAufruf(){
		if (this.ansicht == 1){
			JOptionPane.showMessageDialog (null, "Aufruf Terminset ist nur in der Normalansicht möglich (und sinnvoll...)");
			return;			
		}
		Point xpoint = this.ViewPanel.getLocationOnScreen();
		setUpdateVerbot(true);
		swSetWahl = -1;
		SetWahl sw = new SetWahl(this);
		xpoint.x = xpoint.x +(this.ViewPanel.getWidth()/2) - (sw.getWidth()/2);
		xpoint.y = xpoint.y +(this.ViewPanel.getHeight()/2) - (sw.getHeight()/2);
		sw.setLocation(xpoint);
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
		Reha.thisClass.messageLabel.setText("in hole");
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
				Reha.thisClass.terminpanel.eltern.setTitle(DatFunk.WochenTag(serster)+" "+serster+"  bis  "+DatFunk.WochenTag(sletzter)+" "+sletzter+"-----Behandler:"+sbehandler+" ---- [Wochenansicht]");
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
				System.out.println("Im Thread - Mache Statement");				
				System.out.println("von ResultSet SQLState: " + ex.getSQLState());
				System.out.println("von ResultSet ErrorCode: " + ex.getErrorCode ());
				System.out.println("von ResultSet ErrorMessage: " + ex.getMessage ());
			}

	} catch(SQLException ex) {
		System.out.println("Im Thread - Mache Statement");
		System.out.println("von stmt -SQLState: " + ex.getSQLState());
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
				System.out.println("von ResultSet SQLState: " + ex.getSQLState());
				System.out.println("von ResultSet ErrorCode: " + ex.getErrorCode ());
				System.out.println("von ResultSet ErrorMessage: " + ex.getMessage ());
			}

	} catch(SQLException ex) {
		System.out.println("von stmt -SQLState: " + ex.getSQLState());
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
				System.out.println(evt);
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
	
	private void sperreAnzeigen(){
		JOptionPane.showMessageDialog (null, "Diese Terminspalte ist derzeit gesperrt von Benutzer \n\n" +
				"---> "+lockmessage+
				"\n\n und kann deshalb nicht veränder werden!");
	}
	public static void starteUnlock(){
		new Thread(new UnlockRecord()).start();
	}
	/********
	 *  Strg+Einfg.
	 */
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
		datenSpeicher[0]= (String) ((String)((Vector)((ArrayList)vTerm.get(aktbehandler)).get(0)).get(aktblock)).replaceAll("\u00A9 ", "");		
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
		srueck[0]= (String) ((String)((Vector)((ArrayList)vTerm.get(aktbehandler)).get(0)).get(aktblock)).replaceAll("\u00A9 ", "");		
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
		int aktbehandler=-1;
		int aktdauer;
		String aktstart;
		String aktend;
		setUpdateVerbot(true); /****************///////
		if(datenSpeicher[0]==null){
			setUpdateVerbot(false);
			return;
		}
		//****Hier rein die Abfrage ob die Druckerliste neu gestartet werden soll!
		int anzahl;
		if((anzahl = terminVergabe.size()) > 0){
			if(!datenSpeicher[0].equals(terminVergabe.get(anzahl-1)[8])){
				String confText = 
					"Der Patient --> "+datenSpeicher[0]+" <--ist jetzt NEU im internen Speicher.\n\n"+
					"BISLANG war der Patient --> "+terminVergabe.get(anzahl-1)[8]+" <-- im Speicher und damit in der Druckliste.\n"+
					"Soll die bisherige Druckliste gel�scht werden und der Patient "+datenSpeicher[0]+
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
		for(int i = 0; i<1; i++){
			if(aktdauer == Integer.parseInt(datenSpeicher[3])){
				datenSpeicher[2] = aktstart;
				datenSpeicher[4] = aktend;
				blockSetzen(1);
				break;
			}
			if(aktdauer < Integer.parseInt(datenSpeicher[3])){
				dialogRetInt = 0;
	 		   Point p = positionErmitteln();
	 		   new TerminEinpassen(p.x,p.y);
				switch(dialogRetInt){
					case 0:
						wartenAufReady = false;
						break;
					case 1:
						datenSpeicher[2] = aktstart;
						datenSpeicher[4] = aktend;
						blockSetzen(4);
						break;
					case 2:
						//in Nachfolgeblock k�rzen
						datenSpeicher[2] = aktstart;
						datenSpeicher[4] = aktend;
						int ende1,ende2;
						int aktanzahl = ((Vector<?>)((ArrayList<?>)vTerm.get(aktbehandler)).get(0)).size()-1;
						if(aktanzahl==aktblock){
							//ende1 = 
						}else{ //pr�fen ob nachfolgender Block gek�rzt werden kann oder ob zu klein
							ende1 = (int) ZeitFunk.MinutenSeitMitternacht((String) ((Vector<?>)((ArrayList<?>)vTerm.get(aktbehandler)).get(4)).get(aktblock+1));
							ende2 = (int) ZeitFunk.MinutenSeitMitternacht(aktstart)+Integer.parseInt(datenSpeicher[3]);
							
							if (ende2 >= ende1){
								JOptionPane.showMessageDialog (null, "Der nachfolgende Block ist von kürzerer Dauer\n"+
										"als er für die von Ihnen gewünscht Operation sein müßte\n\n"+
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
 			   Point p = positionErmitteln();
			   new TerminObenUntenAnschliessen(p.x,p.y);
				switch(dialogRetInt){
					case 0:
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
							//System.out.println("case 4: blockSetzen(5)  zeiten:"+zeit1+" / "+zeit2+" / "+zeit3+" / "+zeit4);
							blockSetzen(5);
							break;
						}else if((zeit1 == zeit2) && (zeit3 > zeit4)){
							blockSetzen(3);
							//System.out.println("case 4: mu� unten andocken  zeiten:"+zeit1+" / "+zeit2+" / "+zeit3+" / "+zeit4);
							break;
						}else if((zeit1 < zeit2) && (zeit3 == zeit4)){
							blockSetzen(2);
							//System.out.println("case 4: mu� oben andocken  zeiten:"+zeit1+" / "+zeit2+" / "+zeit3+" / "+zeit4);
							break;							
						}else{
							//System.out.println("case 4: pa�t nicht  zeiten:"+zeit1+" / "+zeit2+" / "+zeit3+" / "+zeit4);
							JOptionPane.showMessageDialog (null, "Die von Ihnen angegebene Startzeit "+datenSpeicher[2]+"\n"+
									" und die Dauer des Termines von "+datenSpeicher[3]+" Minuten, passt hinten und\n"+
									"verne nicht. Entweder ergibt dies Startzeit eine Überschneidung mit \n"+
									"dem vorherigen oder mit dem nachfolgenden Termin\n\n"+
									"Kopiert wird daher --> nix!");
							wartenAufReady = false;
						}							
						break;
					
				}
				break;
			}
		}
		setUpdateVerbot(false);
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
* �bergabe = 1 Block passt genaut
* �bergabe = 2 Block oben anschlie�en
* �bergabe = 3 Block unten anschlie�en
* �bergabe = 4 Block ausdehnen
* �bergabe = 5 Startzeit wurde manuell festgelegt
* �bergabe = 6 Nachfolgenden Block k�rzen
* �bergabe = 7 Vorblock k�rzen
* �bergabe = 8 Gruppierten Terminblock zusammenfassen 
* �bergabe = 9 Gruppierten Terminblock l�schen	
* �bergabe = 10 Freitermin eintragen
* �bergabe = 10 Block l�schen
* �bergabe = 11 Block tauschen mit vorg�nger
* �bergabe = 12 Block tauschen mit nachfolger
*/
		private void blockSetzen(int wohin){
			int gesperrt=0;
			gesperrt = lockVorbereiten();
			setzeRueckgabe();

			if(gesperrt < 0){
				sperreAnzeigen();
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
					setUpdateVerbot(false);
				}
			}
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
					  //minuten_tag = minuten_tag + new Integer( ((String) ((Vector<?>)((ArrayList<?>)Reha.thisClass.terminpanel.vTerm.get(tage)).get(3)).get(i)).trim());
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
				if((System.currentTimeMillis()-zeit) > 1500){
					JOptionPane.showMessageDialog(null,"Fehler im Lock-Mechanismus -> Funktion LockVorbereiten(), bitte informieren Sie den Entwickler");
					lockok = -1;
				}
			} catch (InterruptedException e1) {
				e1.printStackTrace();
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
				setAufruf();
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
			if(((AbstractButton) arg0.getSource()).getText() == "Patient suchen - Alt+P (über Rezept-Nummer)"){
				doPatSuchen();
			}
			if(((AbstractButton) arg0.getSource()).getText() == "Telefonliste aller Patienten (über Rezept-Nummer)"){
				doTelefonListe();
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
		if(vec.size() == 0){
			JOptionPane.showMessageDialog(null,"Rezept nicht gefunden!\nIst die eingetragene Rzeptnummer korrekt?");
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
		if(patient == null){
			final String xpat_int = pat_int;
			new SwingWorker<Void,Void>(){
				protected Void doInBackground() throws Exception {
					JComponent xpatient = AktiveFenster.getFensterAlle("PatientenVerwaltung");
					Reha.thisClass.progLoader.ProgPatientenVerwaltung(1);
					while( (xpatient == null) ){
						Thread.sleep(20);
						xpatient = AktiveFenster.getFensterAlle("PatientenVerwaltung");
					}
					while(  (!AktuelleRezepte.initOk) ){
						Thread.sleep(20);
					}
					
					String s1 = "#PATSUCHEN";
					String s2 = (String) xpat_int;
					PatStammEvent pEvt = new PatStammEvent(Reha.thisClass.terminpanel);
					pEvt.setPatStammEvent("PatSuchen");
					pEvt.setDetails(s1,s2,"#REZHOLEN-"+xreznr) ;
					PatStammEventClass.firePatStammEvent(pEvt);
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
		}
	}
	private void tauscheTermin(int richtung){
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
	public void terminAufnehmen(int behandler,int block){
		String[] sTerminVergabe = {null,null,null,null,null,null,null,null,null,null,null};
		int xaktBehandler = behandler;
		if(ansicht==MASKEN_ANSICHT){
			return;
		}
		if(terminVergabe.size()>0){
			int anzahl = terminVergabe.size();
			boolean gleiche = false;
			String nametext = (String) ((Vector) ((ArrayList)  vTerm.get(xaktBehandler)).get(0)).get(block);
			String reztext = (String) ((Vector) ((ArrayList)  vTerm.get(xaktBehandler)).get(1)).get(block);
			String starttext = (String) ((Vector) ((ArrayList)  vTerm.get(xaktBehandler)).get(2)).get(block);
			String sdauer = (String) ((Vector) ((ArrayList)  vTerm.get(xaktBehandler)).get(3)).get(block);
			String stestdat = (String) ((Vector) ((ArrayList)  vTerm.get(xaktBehandler)).get(5)).get(4);
		
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
				return;
			}
		}
		sTerminVergabe[8] = (String) ((Vector) ((ArrayList)  vTerm.get(xaktBehandler)).get(0)).get(block);
		sTerminVergabe[9] = (String) ((Vector) ((ArrayList)  vTerm.get(xaktBehandler)).get(1)).get(block);
		sTerminVergabe[2] = (String) ((Vector) ((ArrayList)  vTerm.get(xaktBehandler)).get(2)).get(block);
		sTerminVergabe[4] = (String) ((Vector) ((ArrayList)  vTerm.get(xaktBehandler)).get(3)).get(block);
		sTerminVergabe[3] = (String) ((Vector) ((ArrayList)  vTerm.get(xaktBehandler)).get(5)).get(4);
				
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
	}
	public void aktualisieren(){
        if(ansicht == NORMAL_ANSICHT){
        	String sstmt = 	ansichtStatement(this.ansicht,this.aktuellerTag);
        }else if(ansicht == WOCHEN_ANSICHT){
        	if(this.wocheAktuellerTag.isEmpty()){
        		this.wocheAktuellerTag = this.aktuellerTag;
        	}
        	String sstmt = 	ansichtStatement(this.ansicht,this.wocheAktuellerTag);
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
		System.out.println(dte.getSource());
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
		//System.out.println("Es wurde gedroppt");
		
		if(TerminFenster.DRAG_MODE == TerminFenster.DRAG_NONE){
			oSpalten[aktiveSpalte[2]].schwarzAbgleich(aktiveSpalte[0], aktiveSpalte[0]);
			dragLab[aktiveSpalte[2]].setIcon(null);
			dragLab[aktiveSpalte[2]].setText("");
			System.out.println("Drag_Mode == Drag_None");
			dtde.dropComplete(true);
			return;
		}
		
		//System.out.println("Drag_Mode != Drag_None");

		try {
			dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

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
					return;
				}
				if((altaktiveSpalte[0]==aktiveSpalte[0]) && (altaktiveSpalte[2]==aktiveSpalte[2])){
					return;
				}

				String sname = (String) ((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(0)).get(aktiveSpalte[0]);
				String sreznum = (String) ((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(1)).get(aktiveSpalte[0]);

				// Hier testen ob alter Block mit Daten gef�llt war
				if(!sname.equals("")){
					int frage = JOptionPane.showConfirmDialog(null, "Wollen Sie den bisherigen Eintrag -> "+sname+
							" <- tatsächlich überschreiben?", "Achtung wichtige Benutzeranfrage!!!", JOptionPane.YES_NO_OPTION);
					if(frage==JOptionPane.NO_OPTION){
						break;
					}
				}
				String[] teilen;
				//System.out.println("D&DÜbergabe = "+mitgebracht);
				if(mitgebracht.indexOf("°") >= 0 ){
					teilen = mitgebracht.split("°");
					if(! teilen[0].contains("TERMDAT")){
						return;
					}
					teilen[3] = teilen[3].toUpperCase();
					teilen[3] = teilen[3].replaceAll(" MIN.", "");

					datenSpeicher[0]= teilen[1];		
					datenSpeicher[1]= teilen[2];		
					datenSpeicher[3]= teilen[3];
					
					datenAusSpeicherHolen();

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
						
						if(!grobRaus){
							String sbeginnneu = (String) ((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(2)).get(spaltneu[0]); 
							int spAktiv = aktiveSpalte[2];
							aktiveSpalte = altaktiveSpalte.clone();
							wartenAufReady = true;
							grobRaus = false;
							setUpdateVerbot(true);
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
							if(!grobRaus){
								//Stufe 2 - o.k.
								if(altaktiveSpalte[2]==spAktiv){
									String sbeginn = (String) ((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(2)).get(altaktiveSpalte[0]) ;
									int lang = ((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(0)).size();
									//Suche nach Uhrzeit -> "+sbeginn
									for(int i2 = 0 ; i2 < lang; i2++){
										if( ((Vector<?>)((ArrayList<?>) vTerm.get(behandler)).get(2)).get(i2).equals(DRAG_UHR)){
											aktiveSpalte[0] = i2;
											aktiveSpalte[1] = i2;
											wartenAufReady = true;
											blockSetzen(11);
											break;
										}
									}
								}else{
									wartenAufReady = true;
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
							}
							aktiveSpalte = spaltneu.clone();							
						}else{
							aktiveSpalte = spaltneu.clone();
							wartenAufReady = false;								
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

				}catch(Exception ex){}
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
	public void terminBestaetigen(int spalte){
		if( (!this.getAktuellerTag().equals(DatFunk.sHeute())) || ansicht == WOCHEN_ANSICHT){
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
		String sname = ((String) ((ArrayList<Vector<String>>) vTerm.get(xaktBehandler)).get(0).get(aktiveSpalte[0]));
		String sreznum = ((String) ((ArrayList<Vector<String>>) vTerm.get(xaktBehandler)).get(1).get(aktiveSpalte[0]));
		String sorigreznum = sreznum;
		String sbeginn = ((String) ((ArrayList<Vector<String>>) vTerm.get(xaktBehandler)).get(2).get(aktiveSpalte[0]));
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
		final String swname = sname.replaceAll("\u00A9 ","");
		final String swbeginn = sbeginn;

		final int swbehandler = xaktBehandler; 
		
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				Vector vec = null;
				Vector tvec = null;
				String copyright = "\u00A9 ";
				try{
				vec = SqlInfo.holeSatz("verordn", "termine,anzahl1,pos1,pos2,pos3,pos3,hausbes,unter18,jahrfrei,pat_intern,preisgruppe,zzregel", "rez_nr='"+swreznum+"'", Arrays.asList(new String[] {}));
				if (vec.size() > 0){
					StringBuffer termbuf = new StringBuffer();
					termbuf.append((String) vec.get(0));
					if(termbuf.toString().contains(DatFunk.sHeute())){
						JOptionPane.showMessageDialog(null, "Dieser Termin ist am heutigen Tag bereits erfaßt");
						gruppeAusschalten();
						return null; 
					}
					tvec = RezTools.splitteTermine(termbuf.toString());
					int anzahl = new Integer((String)vec.get(1));
					if(tvec.size() >= anzahl){
						if(tvec.size()==anzahl){
							///rezept voll
							// nachfragen ob wirklich schreiben
						}else{
							//rezept bereits �bervoll
							//nachfragen ob wirklich schreiben
						}
					}else{
						termbuf.append(macheNeuTermin(ParameterLaden.getKollegenUeberDBZeile(swbehandler+1),
								"",(String) vec.get(2),(String) vec.get(3),(String) vec.get(4),(String) vec.get(5)));
						/********************************/
						boolean unter18 =  ( ((String)vec.get(7)).equals("T") ? true : false );
						boolean vorjahrfrei = ( ((String)vec.get(8)).equals("") ? false : true );
						if(!unter18 && !vorjahrfrei){
							SqlInfo.aktualisiereSatz("verordn", "termine='"+termbuf.toString()+"'", "rez_nr='"+swreznum+"'");			
						}else if(unter18 && !vorjahrfrei){
							/// Testen ob immer noch unter 18 ansonsten ZuZahlungsstatus �ndern;
							String geboren = DatFunk.sDatInDeutsch(SqlInfo.holePatFeld("geboren","pat_intern='"+vec.get(9)+"'" ));
							System.out.println("Geboren = "+geboren);
							if(DatFunk.Unter18(DatFunk.sHeute(), geboren)){
								SqlInfo.aktualisiereSatz("verordn", "termine='"+termbuf.toString()+"'", "rez_nr='"+swreznum+"'");				
							}else{
								SqlInfo.aktualisiereSatz("verordn", "termine='"+termbuf.toString()+"', zzstatus='2'", "rez_nr='"+swreznum+"'");				
							}

						}else if(!unter18 && vorjahrfrei){
							String bef_dat = SqlInfo.holePatFeld("befreit","pat_intern='"+vec.get(9)+"'" );
							//String bef_dat = datFunk.sDatInDeutsch(SqlInfo.holePatFeld("befreit","pat_intern='"+vec.get(9)+"'" ));
							if(!bef_dat.equals("T")){
								if(DatFunk.DatumsWert("31.12."+vec.get(9)) < DatFunk.DatumsWert(DatFunk.sHeute()) ){
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
						String towhere = "datum='"+DatFunk.sDatInSQL(DatFunk.sHeute())+"' AND "+
						"behandler='"+(swbehandler < 10 ? "0"+Integer.toString(swbehandler+1)+"BEHANDLER" : Integer.toString(swbehandler+1)+"BEHANDLER"   )+"' "+
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
					}
					
				}else{
					JOptionPane.showMessageDialog(null, "Dieses Rezept existiert nicht bzw. ist bereits abgerechnet!!");
				}

				}catch(Exception ex){
					ex.printStackTrace();
				}
				vec = null;
				tvec = null;
				return null;
			}
		}.execute();
	}
	public static String macheNeuTermin(String kollege,String text,String pos1,String pos2,String pos3,String pos4){
		String ret =
			DatFunk.sHeute()+
			"@"+
			kollege+
			"@"+
			text+
			"@"+
			pos1+
			( pos2.trim().equals("") ? "" : ","+ pos2 )+
			( pos3.trim().equals("") ? "" : ","+ pos3 )+
			( pos4.trim().equals("") ? "" : ","+ pos4 )+
			"@"+
			DatFunk.sDatInSQL(DatFunk.sHeute())+"\n";
		return ret;
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
				threadStmt = "select * from flexlock where sperre = '"+TerminFenster.getLockStatement()+"'";
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
				System.out.println("von ResultSet SQLState: " + ex.getSQLState());
				System.out.println("von ResultSet ErrorCode: " + ex.getErrorCode ());
				System.out.println("von ResultSet ErrorMessage: " + ex.getMessage ());

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
			try {
				this.sState = TerminFenster.getThisClass().privstmt;
				success = this.sState.execute("Delete from flexlock where sperre = '"+TerminFenster.getLockSpalte()+"' AND maschine = '"+SystemConfig.dieseMaschine+"'");
				success = this.sState.execute("COMMIT");
				Reha.thisClass.messageLabel.setText("Entserrung efolgreich");
				TerminFenster.setLockOk(0,"");
				TerminFenster.getThisClass().wartenAufReady = false;
			}catch(SQLException ex) {
				System.out.println("von ResultSet SQLState: " + ex.getSQLState());
				System.out.println("von ResultSet ErrorCode: " + ex.getErrorCode ());
				System.out.println("von ResultSet ErrorMessage: " + ex.getMessage ());
				TerminFenster.getThisClass().wartenAufReady = false;
				JOptionPane.showMessageDialog (null, "Achtung!!!!! \n\nDiese Terminspalte wurde bereits  von Benutzer \n\n" +
						"einem anderen Benutzer gesperrt. Bitte brechen Sie den Eingabevorgang ab \n\n"+
						"und versuchen es später erneut!!");
				Reha.thisClass.messageLabel.setText("Entsperren misslungen");
				TerminFenster.setLockOk(-1," Durch Fehler in SQL-Statement:" +ex.getMessage());

			}

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
			"' , maschine = '"+SystemConfig.dieseMaschine+"'";
			try {
				this.sState = TerminFenster.getThisClass().privstmt;
				
				klappt = this.sState.execute(threadStmt);
				klappt = this.sState.execute("COMMIT");

			}catch(SQLException ex) {
				System.out.println("von ResultSet SQLState: " + ex.getSQLState());
				System.out.println("von ResultSet ErrorCode: " + ex.getErrorCode ());
				System.out.println("von ResultSet ErrorMessage: " + ex.getMessage ());
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
				threadStmt = "select * from flexlock where sperre = '"+TerminFenster.getLockStatement()+"'";
				rs = this.sState.executeQuery(threadStmt);
				if(!rs.next()){

					threadStmt = "insert into flexlock set sperre = '"+TerminFenster.getLockStatement()+
					"' , maschine = '"+SystemConfig.dieseMaschine+"'";
					this.gesperrt = this.sState.execute(threadStmt);
					this.gesperrt = this.sState.execute("COMMIT");
					TerminFenster.setLockOk(1,"");
					TerminFenster.setLockSpalte(TerminFenster.getLockStatement());
					Reha.thisClass.messageLabel.setText("Lock erfolgreich");
				}else{
					TerminFenster.setLockOk(-1,rs.getString("maschine"));
					Reha.thisClass.messageLabel.setText("Lock misslungen");
				}
				
			}catch(SQLException ex) {
				this.gesperrt = false;
				System.out.println("von ResultSet SQLState: " + ex.getSQLState());
				System.out.println("von ResultSet ErrorCode: " + ex.getErrorCode ());
				System.out.println("von ResultSet ErrorMessage: " + ex.getMessage ());

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
	    			Reha.thisClass.shiftLabel.setText("in Update...");
	    			TerminFenster.getThisClass().setUpdateVerbot(true);
	    			TerminFenster.getThisClass().aktualisieren();
	    			TerminFenster.getThisClass().setUpdateVerbot(false);	 
	    			Reha.thisClass.shiftLabel.setText("Update ok.");	    			
	    			gelesen++;
	    			}catch(Exception ex){
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
					Reha.thisClass.shiftLabel.setText(""+PixelzuMinute);
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
    	Reha.thisClass.shiftLabel.setText(dtde.getLocation().toString());
    	//System.out.println("Drag-Support"+dtde);
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
		//System.out.println("in datGasture "+arg0);
		
	}
	
}