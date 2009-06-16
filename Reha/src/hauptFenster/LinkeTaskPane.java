package hauptFenster;



import generalSplash.RehaSplash;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TooManyListenersException;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.border.DropShadowBorder;
import org.jdesktop.swingx.plaf.windows.WindowsTaskPaneUI;

import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.desktop.DesktopException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.event.IDocumentEvent;
import ag.ion.bion.officelayer.event.IDocumentListener;
import ag.ion.bion.officelayer.event.IEvent;
import ag.ion.bion.officelayer.filter.RTFFilter;
import ag.ion.bion.officelayer.text.IPageCursor;
import ag.ion.bion.officelayer.text.ITextCursor;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;

import dialoge.SimpleInternal;

import patientenFenster.PatFenster;
import roogle.RoogleFenster;
import sqlTools.ExUndHop;
import systemEinstellungen.INIFile;
import systemEinstellungen.SystemConfig;
import systemEinstellungen.SystemUtil;
import terminKalender.ParameterLaden;
import terminKalender.SchnellSuche;
import terminKalender.TagWahlNeu;
import terminKalender.datFunk;

public class LinkeTaskPane extends JXPanel implements ActionListener, ComponentListener, DropTargetListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private Reha eltern;
	private static JXTaskPaneContainer jxTPcontainer = null; 
	private static JXTaskPane tp1 = null;
	private static JXTaskPane tp2 = null;
	private static JXTaskPane tp3 = null;	
	private static JXTaskPane tp4 = null;	
	private static JXTaskPane tp5 = null;
	private JXHyperlink oo1 = null;
	private JXHyperlink oo2 = null;
	public static boolean OOok = true;
	public static LinkeTaskPane thisClass = null;
	public static ITextDocument itestdocument = null;
	public LinkeTaskPane(){
		super();

		this.setBorder(null);
		this.setBackground(Color.WHITE);
		//this.eltern = Reha.thisClass;
		this.setPreferredSize(new Dimension(200,500));
		
		GridBagConstraints gridBagConstraints = new GridBagConstraints() ;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.anchor = GridBagConstraints.CENTER;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1.0D;
		gridBagConstraints.weighty = 1.0D;
		gridBagConstraints.insets = new Insets(5, 2, 5, 2);
		
		this.setLayout(new GridBagLayout());

		
		/**
		 * Zuerst die Scrollpane generieren falls der
		 * spätere TaskPane-Container die y-Dimension des Fensters übersteigt.
		 */
		JScrollPane jScrp = new JScrollPane();
		jScrp.setBorder(null);
		jScrp.setViewportBorder(null);
		jScrp.setBackground(Color.white);
		jScrp.setPreferredSize(new Dimension(180, 100));
		DropShadowBorder dropShadow = new DropShadowBorder(Color.BLACK, 10, 1, 5, false, true, true, true);
		jScrp.setBorder(dropShadow);
		/**
		 * Jetz generieren wir den Taskpane-Container anschließend die TaskPanes
		 */
		jxTPcontainer =	new JXTaskPaneContainer();
		jxTPcontainer.setBackground(new Color(106,130,218));
		//jxTPcontainer.setPreferredSize(new Dimension(250,0));

		jxTPcontainer.add(getPatientenStamm());

		jxTPcontainer.add(getTerminKalender());
		
		jxTPcontainer.add(getOpenOfficeOrg());
		
		jxTPcontainer.add(getNuetzliches());		
		
		jxTPcontainer.add(getSystemEinstellungen());
		
		/**
		 * dann fügen wir den TaskpaneContainer der ScrollPane hinzu
		 */
		jScrp.setViewportView(jxTPcontainer);
		jScrp.setVisible(true);
		jScrp.revalidate();
		this.add(jScrp,gridBagConstraints);
		this.validate();
		thisClass = this;
	}
	/**
	 * Task-Pane für den Patientenstamm erstellen
	 * @return
	 */
	
	private JXTaskPane getPatientenStamm(){

		tp1 = new JXTaskPane();
		UIManager.put("TaskPane.titleBackgroundGradientStart",Color.WHITE);
		UIManager.put("TaskPane.titleBackgroundGradientEnd",new Color(200,212,247));
		UIManager.put("TaskPane.background",new Color(214,223,247));
		UIManager.put("TaskPane.foreground",Color.BLUE);		
		UIManager.put("TaskPane.useGradient", Boolean.TRUE);
		WindowsTaskPaneUI wui = new WindowsTaskPaneUI();
		tp1.setUI(wui);
		tp1.setTitle("Stammdaten");
		tp1.setIcon(new ImageIcon(Reha.proghome+"icons/personen16.gif"));				
		JXHyperlink jxLink = new JXHyperlink();
		jxLink.setText("Patienten und Rezepte  (Strg+P)");
		jxLink.setClickedColor(new Color(0, 0x33, 0xFF));
		jxLink.addActionListener(this);
		jxLink.setEnabled(true);
		tp1.add(jxLink);
		jxLink = new JXHyperlink();
		jxLink.setText("Ärzte - Stammdaten  (Strg+A)");
		jxLink.setClickedColor(new Color(0, 0x33, 0xFF));
		jxLink.addActionListener(this);
		jxLink.setEnabled(false);
		tp1.add(jxLink);
		jxLink = new JXHyperlink();
		jxLink.setText("Krankenkassen  (Strg+K)");
		jxLink.setClickedColor(new Color(0, 0x33, 0xFF));
		jxLink.addActionListener(this);
		tp1.add(jxLink);
		//tp1.setExpanded(true);
		return tp1;
	}
	
	private JXTaskPane getTerminKalender(){
		tp4 = new JXTaskPane();
		UIManager.put("TaskPane.titleBackgroundGradientStart",Color.WHITE);
		UIManager.put("TaskPane.titleBackgroundGradientEnd",new Color(200,212,247));
		UIManager.put("TaskPane.background",new Color(214,223,247));
		UIManager.put("TaskPane.useGradient", Boolean.TRUE);
		WindowsTaskPaneUI wui = new WindowsTaskPaneUI();
		tp4.setUI(wui);		
		tp4.setTitle("Termine");
		tp4.setIcon(new ImageIcon(Reha.proghome+"icons/table_mode.png"));				
		JXHyperlink jxLink = new JXHyperlink();
		jxLink.setText("Terminkalender starten  (Strg+T)");
		jxLink.setClickedColor(new Color(0, 0x33, 0xFF));
		jxLink.addActionListener(this);
		tp4.add(jxLink);
		jxLink = new JXHyperlink();
		DropTarget dndt = new DropTarget();
		DropTargetListener dropTargetListener =
			 new DropTargetListener() {
			  public void dragEnter(DropTargetDragEvent e) {}
			  public void dragExit(DropTargetEvent e) {}
			  public void dragOver(DropTargetDragEvent e) {}
			  public void drop(DropTargetDropEvent e) {
				  String mitgebracht = "";
			    try {
			      Transferable tr = e.getTransferable();
			      DataFlavor[] flavors = tr.getTransferDataFlavors();
			      for (int i = 0; i < flavors.length; i++){
			        	mitgebracht  = new String((String) tr.getTransferData(flavors[i]));
			      }
			      if(mitgebracht.indexOf("°") >= 0){
			    	  ProgLoader.ProgRoogleFenster(0, mitgebracht);
			      }
			      System.out.println(mitgebracht);
			    } catch (Throwable t) { t.printStackTrace(); }
			    // Ein Problem ist aufgetreten
			    e.dropComplete(true);
			  }
			  public void dropActionChanged(
			         DropTargetDragEvent e) {}
		};
		try {
			dndt.addDropTargetListener(dropTargetListener);
		} catch (TooManyListenersException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		jxLink.setDropTarget(dndt);
		jxLink.setName("Rugl");
		jxLink.setText("[Ru:gl] - Die Terminsuchmaschine  (Strg+R)");
		jxLink.setClickedColor(new Color(0, 0x33, 0xFF));
		jxLink.addActionListener(this);
		tp4.add(jxLink);
		jxLink = new JXHyperlink();
		jxLink.setText("Wocharbeitszeiten definieren");
		jxLink.setClickedColor(new Color(0, 0x33, 0xFF));
		jxLink.addActionListener(this);
		tp4.add(jxLink);
		jxLink = new JXHyperlink();
		jxLink.setText("Zeitrechner");
		jxLink.setClickedColor(new Color(0, 0x33, 0xFF));
		jxLink.addActionListener(this);
		jxLink.setEnabled(false);
		tp4.add(jxLink);

		//tp4.setExpanded(true);
		return tp4;
	}
	

	private JXTaskPane getOpenOfficeOrg(){
		tp3 = new JXTaskPane();
		UIManager.put("TaskPane.titleBackgroundGradientStart",Color.WHITE);
		UIManager.put("TaskPane.titleBackgroundGradientEnd",new Color(200,212,247));
		UIManager.put("TaskPane.background",new Color(214,223,247));
		UIManager.put("TaskPane.useGradient", Boolean.TRUE);
		WindowsTaskPaneUI wui = new WindowsTaskPaneUI();
		tp3.setUI(wui);
		tp3.setTitle("OpenOffice.org");
		tp3.setIcon(SystemConfig.hmSysIcons.get("openoffice"));				
		oo1 = new JXHyperlink();
		oo1.setText("OpenOffice-Writer");
		oo1.setIcon(SystemConfig.hmSysIcons.get("ooowriter"));
		oo1.addActionListener(this);
		tp3.add(oo1);
		oo2 = new JXHyperlink();
		oo2.setIcon(SystemConfig.hmSysIcons.get("ooocalc"));
		oo2.setText("OpenOffice-Calc");
		oo2.addActionListener(this);
		tp3.add(oo2);
		oo2 = new JXHyperlink();
		oo2.setIcon(SystemConfig.hmSysIcons.get("oooimpress"));
		oo2.setText("OpenOffice-Impress");
		oo2.addActionListener(this);
		tp3.add(oo2);
		return tp3;
	}
	
	private JXTaskPane getNuetzliches(){
		tp5 = new JXTaskPane();
		UIManager.put("TaskPane.titleBackgroundGradientStart",Color.WHITE);
		UIManager.put("TaskPane.titleBackgroundGradientEnd",new Color(200,212,247));
		UIManager.put("TaskPane.background",new Color(214,223,247));
		UIManager.put("TaskPane.useGradient", Boolean.TRUE);
		WindowsTaskPaneUI wui = new WindowsTaskPaneUI();
		tp5.setUI(wui);
		
		tp5.setTitle("Nützliches...");
		//tp3.setIcon(new ImageIcon("icons/pdf.gif"));				
		JXHyperlink jxLink = new JXHyperlink();
		jxLink.setText("Thera-PI - Browser");
		jxLink.setClickedColor(new Color(0, 0x33, 0xFF));		
		jxLink.setIcon(new ImageIcon(Reha.proghome+"icons/home.gif"));		
		jxLink.addActionListener(this);
		tp5.add(jxLink);
		jxLink = new JXHyperlink();
		jxLink.setText("piTool - ScreenShots");
		jxLink.setClickedColor(new Color(0, 0x33, 0xFF));		
		jxLink.setIcon(new ImageIcon(Reha.proghome+"icons/cameraklein.png"));
		jxLink.setActionCommand("piTool");
		jxLink.addActionListener(this);
		//jxLink.setEnabled(false);
		tp5.add(jxLink);
		jxLink = new JXHyperlink();
		jxLink.setText("piHelp - Hifetextgenerator");
		jxLink.setClickedColor(new Color(0, 0x33, 0xFF));		
		jxLink.setIcon(new ImageIcon(Reha.proghome+"icons/fragezeichenklein.png"));
		jxLink.setActionCommand("piHelp");
		jxLink.addActionListener(this);
		//jxLink.setEnabled(false);
		tp5.add(jxLink);
		return tp5;
	}


	private JXTaskPane getSystemEinstellungen(){
		tp2 = new JXTaskPane();
		UIManager.put("TaskPane.titleBackgroundGradientStart",Color.WHITE);
		UIManager.put("TaskPane.titleBackgroundGradientEnd",new Color(200,212,247));
		UIManager.put("TaskPane.background",new Color(214,223,247));
		UIManager.put("TaskPane.useGradient", Boolean.TRUE);
		WindowsTaskPaneUI wui = new WindowsTaskPaneUI();
		tp2.setUI(wui);
		
		tp2.setTitle("Systemeinstellungen");
		tp2.setIcon(new ImageIcon(Reha.proghome+"icons/pdf.gif"));				
		JXHyperlink jxLink = new JXHyperlink();
		jxLink.setText("Benutzerverwaltung");
		jxLink.addActionListener(this);
		if( (SystemConfig.dieseMaschine.toString().indexOf("10.8.0.6") > 0) ||
				(SystemConfig.dieseMaschine.toString().indexOf("192.168.2.55") > 0)	){
			jxLink.setEnabled(true);
		}else{
			jxLink.setEnabled(false);			
		}
		tp2.add(jxLink);
		jxLink = new JXHyperlink();
		jxLink.setText("System Initialisierung");
		jxLink.addActionListener(this);
		tp2.add(jxLink);
		jxLink = new JXHyperlink();
		jxLink.setText("Look & Feel");
		jxLink.addActionListener(this);
		jxLink.setEnabled(false);
		tp2.add(jxLink);
		return tp2;
	}

	
	public static void UpdateUI(){
		jxTPcontainer.updateUI();
		tp1.updateUI();
		tp2.updateUI();		
		tp3.updateUI();
		tp4.updateUI();
		tp5.updateUI();		
		//System.out.println("TaskPane-Container L&F");
	}
	/**
	 * Eigener Event-Handler man wird sehen ob das vernüftig ist.
	 * 	@Override
	 */
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		int i;
		for(i=0;i<1;i++){
			if (cmd.equals("Look & Feel")) {
				ExUndHop eUh = new ExUndHop();
				eUh.setzeStatement("delete from flexlock");
				//ProgLoader.ProgLookAndFeel(0);
				break;
			} 
			
			if (cmd.equals("System Initialisierung")){
				ProgLoader.SystemInitialisierung();
				/*SystemUtil sysUtil = new SystemUtil(Reha.thisFrame);
				sysUtil.setSize(800,600);
				sysUtil.setLocation(100,75);
				sysUtil.setVisible(true);
				*/
				break;
			}
			
			if (cmd.equals("Krankenkassen  (Strg+K)")){
				ProgLoader.KassenFenster(0);
				break;
			}
			
			if (cmd.equals("Terminkalender starten  (Strg+T)")){
				ProgLoader.ProgTerminFenster(1,0);
				break;
			}
			if (cmd.equals("Wocharbeitszeiten definieren")){
				JComponent termin = AktiveFenster.getFensterAlle("TerminFenster");
				if(termin != null){
					JOptionPane.showMessageDialog (null, "Achtung!!!!! \n\nWährend der Arbeitszeit-Definition\n" +
					"darf der Terminkalender aus Sicherheitsgründen nicht geöffnet sein.\n"+
					"Beenden Sie den Terminkalender und rufen Sie diese Funktion erneut auf.\n\n");
					return;
				}
				ProgLoader.ProgTerminFenster(0,2);
				//MaskenErstellen();
				break;
			}
			
			
			if (cmd.equals("OpenOffice-Writer")){
				//ProgLoader.ProgOOWriterFenster(1);
				break;
			}
			
			if (cmd.equals("OpenOffice-Calc")){
				//berichtTest(true);
				break;
			}

			if (cmd.equals("OpenOffice-Calc")){
				//berichtTest(true);
				break;
			}

			if (cmd.equals("Benutzerverwaltung")){
				ProgLoader.ProgBenutzerVerwaltung(0);				
				break;
			}
			if (cmd.equals("[Ru:gl] - Die Terminsuchmaschine  (Strg+R)")){
				ProgLoader.ProgRoogleFenster(0,null);
				break;
			}
			if (cmd.equals("RTA-Wisssen das Universalwissen")){
				break;
			}
			if (cmd.equals("Thera-PI - Browser")){
				new ladeProg(Reha.proghome+"RehaWissen.jar");
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						RehaSplash rspl = new RehaSplash(null,"Hilfebrowser laden....dieser Vorgang kann einige Sekunden dauern...");
						long zeit = System.currentTimeMillis();
						while(true){
							Thread.sleep(20);
							if(System.currentTimeMillis()-zeit > 2000){
								break;
							}
						}
						rspl.dispose();
						return null;
					}
					
				}.execute();

				
				break;
			}
			if (cmd.equals("Neuer Wissensbeitrag anlegen")){
				JOptionPane.showMessageDialog (null, "Achtung!!!!! \n\nDer Wissens-Generator ist auf diesem System\n\n" +
						"nicht installiert - oder konnte nicht gefunden werden...\n\n");
				break;
			}
			if (cmd.equals("Patienten und Rezepte  (Strg+P)")){
				ProgLoader.ProgPatientenVerwaltung(1);
				break;
			}
			if (cmd.equals("piHelp")){
				new ladeProg(Reha.proghome+"piHelp.jar");
				break;
			}
			if (cmd.equals("piTool")){
				new ladeProg(Reha.proghome+"piTool.jar");				
				break;
			}


		}
		
		
	}
	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		//System.out.println("Linke-Task-Pane: "+e);
		
	}
	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	public static void OoOk(){
		//LinkeTaskPane.thisClass.oo1.setEnabled(true);
		//LinkeTaskPane.thisClass.oo2.setEnabled(true);
	}
	
	public void MaskenErstellen(){
		String sstmt = "";
		String behandler = "";
		ResultSet rs = null;
		Statement stmt = null;
		boolean ok = false;
		try{
			stmt = (Statement) Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE );
			for(int i=1;i<61;i++){
				for(int t=1;t<8;t++){
					behandler  = (i<10 ? "0"+i+"BEHANDLER" : new Integer(i).toString()+"BEHANDLER");
					sstmt = "insert into masken set behandler='"+behandler+"' , art = '"+t+"' ,belegt='1', N1='@FREI', TS1='07:00:00', TD1='900', TE1='22:00:00'";
					System.out.println(sstmt);
					try {
						ok = stmt.execute(sstmt);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}	
			}
					
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
				if (stmt != null) {

					try {
						stmt.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		}


	}
	
/************************************************************/
	public static void berichtTest(boolean visible){
		Statement stmt = null;
		ResultSet rs = null;
		InputStream is = null;
		String sbericht = "";
		String dateiName = "Test.txt";
		int itest = 0;
		try {
			stmt = Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
	                ResultSet.CONCUR_UPDATABLE );

			rs = stmt.executeQuery("SELECT FREITEXT FROM bericht2 Where berichtid='"+6118+"'");
			int durchlauf = 0;

			while( rs.next()){
				if(durchlauf == 0){
					sbericht = rs.getString("FREITEXT");
				}
				durchlauf++;
			}
			is = new ByteArrayInputStream(sbericht.getBytes());
/*
			StringReader input = new StringReader(sbericht);
			int zeich;
			while((zeich = input.read()) != -1)
			   //parsing the string byte for byte
			input.close();
			
		    FileOutputStream schreibeStrom = 
		                     new FileOutputStream(dateiName);
		    for (int i=0; i < sbericht.length(); i++){
		      schreibeStrom.write((byte)sbericht.charAt(i));
		    }
		    schreibeStrom.close();
*/		    

		}catch(SQLException ex){
			System.out.println("Kollegen2="+ex);
		}
		finally {
			if (rs != null) {
				try {
					rs.close();
				}catch (SQLException sqlEx) { // ignorieren }
					rs = null;
				}
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException sqlEx) { // ignorieren }
						stmt = null;
					}
				}
			}
		}
		IDocumentService documentService = null;
		try {
			documentService = Reha.officeapplication.getDocumentService();
		} catch (OfficeApplicationException e) {
			e.printStackTrace();
		}
		ITextDocument document = null;
		try {
			//is = new FileInputStream( dateiName );
			if(visible){
				itestdocument = (ITextDocument)documentService.constructNewDocument(IDocument.WRITER, DocumentDescriptor.DEFAULT);
	 			
				
			}else{
				IDocumentDescriptor docdescript = new DocumentDescriptor();
	            docdescript.setHidden(true);
	            itestdocument = (ITextDocument)documentService.constructNewDocument(IDocument.WRITER, docdescript);

			}
			document.getViewCursorService().getViewCursor().getTextCursorFromStart().insertDocument( is, new RTFFilter() );
			IPageCursor pageCursor = document.getViewCursorService().getViewCursor().getPageCursor();
			pageCursor.jumpToFirstPage(); 
/*			
			ITextCursor textCursor;
			try {
				textCursor = document.getTextService().getText().getTextCursorService().getTextCursor();
				textCursor.gotoEnd(true);
			  	
			} catch (TextException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		  	
	  	

		} catch (NOAException e) {
			e.printStackTrace();
		}
 
	}
/************************************************************/
	public static void terminTest(boolean visible){
		Statement stmt = null;
		ResultSet rs = null;
		InputStream is = null;
		String sbericht = "";
		String dateiName = "Test.txt";
		int itest = 0;
		INIFile file = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/terminliste.ini");
		String vl = file.getStringProperty("TerminListe1","NameTemplate");
		String url = Reha.proghome+"vorlagen/"+Reha.aktIK+"/"+vl;
		IDocumentService documentService = null;
		ITextDocument textDocument = null;
		try {
			documentService = Reha.officeapplication.getDocumentService();
			Reha.officeapplication.getDesktopService().addDocumentListener(new DokumentListener(Reha.officeapplication)); 
		} catch (OfficeApplicationException e) {
			e.printStackTrace();
		} catch (DesktopException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ITextDocument document = null;
		try {
			//is = new FileInputStream( dateiName );
			if(visible){
	 			//document = (ITextDocument)documentService.constructNewDocument(IDocument.WRITER, DocumentDescriptor.DEFAULT);

				
			}else{
				IDocumentDescriptor docdescript = new DocumentDescriptor();

	            docdescript.setHidden(true);
	            itestdocument = (ITextDocument)documentService.loadDocument(url, docdescript);
	 			//textDocument = (ITextDocument)document;
			}
/*			
			ITextCursor textCursor;
			try {
				textCursor = document.getTextService().getText().getTextCursorService().getTextCursor();
				textCursor.gotoEnd(true);
			  	
			} catch (TextException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		  	
	  	

		} catch (NOAException e) {
			e.printStackTrace();
		}
 
	}
@Override
public void dragEnter(DropTargetDragEvent arg0) {
	// TODO Auto-generated method stub
	System.out.println("Enter---->"+arg0);
	System.out.println(((JComponent)arg0.getSource()).getName());
	
}
@Override
public void dragExit(DropTargetEvent arg0) {
	// TODO Auto-generated method stub
	
}
@Override
public void dragOver(DropTargetDragEvent arg0) {
	// TODO Auto-generated method stub
	
}
@Override
public void drop(DropTargetDropEvent arg0) {
	// TODO Auto-generated method stub
	System.out.println(arg0);
	
}
@Override
public void dropActionChanged(DropTargetDragEvent arg0) {
	// TODO Auto-generated method stub
	
}
	
/************************************************************/
}

class DokumentListener implements IDocumentListener {

	private IOfficeApplication officeAplication = null;
	public DokumentListener(IOfficeApplication officeAplication) {
		this.officeAplication = officeAplication;
	}
	@Override
	public void onAlphaCharInput(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onFocus(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onInsertDone(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onInsertStart(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onLoad(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onLoadDone(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("************************Dokument geladen************************* "+arg0);
		
	}
	@Override
	public void onLoadFinished(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("************************Dokument geladen finished************************* "+arg0);	

			//IDocument[] docs = Reha.officeapplication.getDocumentService().getCurrentDocuments();
			//docs[docs.length-1].close();
			try {
				Thread.sleep(200);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			LinkeTaskPane.itestdocument.close();
			//Reha.officeapplication.getDocumentService().getCurrentDocuments()[0].close();
			LinkeTaskPane.OoOk();
		try {
			Reha.officeapplication.getDesktopService().removeDocumentListener(this);
		} catch (OfficeApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void onModifyChanged(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onMouseOut(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onMouseOver(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onNew(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onNonAlphaCharInput(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSave(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSaveAs(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSaveAsDone(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSaveDone(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSaveFinished(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onUnload(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void disposing(IEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	
}	
	
class ladeProg{

	public ladeProg(String prog){
	File f = new File(prog);
	if(! f.exists()){
		JOptionPane.showMessageDialog(null,"Diese Software ist auf Ihrem System nicht installiert!");
		return;
	}
	String vmload = "java -jar ";
	String commandx = new String(vmload + prog); 

    File ausgabedatei = new File(Reha.proghome+"laden.bat"); 
    FileWriter fw;
	try {
		fw = new FileWriter(ausgabedatei);
	    BufferedWriter bw = new BufferedWriter(fw); 
	    bw.write(commandx); 
	    bw.close(); 
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} 
	final String xprog = prog;
	new SwingWorker<Void, Void>(){

		@Override
		protected Void doInBackground() throws Exception {
			// TODO Auto-generated method stub
			try {
				
				Process process = new ProcessBuilder("java","-jar",xprog).start();
			       InputStream is = process.getInputStream();
			       InputStreamReader isr = new InputStreamReader(is);
			       BufferedReader br = new BufferedReader(isr);
			       String line;
			       while ((line = br.readLine()) != null) {
			         System.out.println(line);
			       }
			       is.close();
			       isr.close();
			       br.close();

			       /*
			       Runtime runtime = Runtime.getRuntime();
			       Process process = runtime.exec("java -jar "+xprog);
			       InputStream is = process.getInputStream();
			       InputStreamReader isr = new InputStreamReader(is);
			       BufferedReader br = new BufferedReader(isr);
			       String line;
			      // System.out.printf("Output of running %s is:", 
			      //     Arrays.toString(args));
			       while ((line = br.readLine()) != null) {
			         //System.out.println(line);
			       }
			       is.close();
			       isr.close();
			       */

				//Runtime r = Runtime.getRuntime();
				//Process p = r.exec("cmd.exe /c start C:\\RehaVerwaltung\\laden.bat " + new File(new Long(System.currentTimeMillis()).toString()+".pid"));
				//startet den AcrobatReader (muss im Pfad erreichbar sein!) und öffnet die Datei outputFile. Sollte IrfanView eigentlich auch irgendwie können.

				//Process prc = Runtime.getRuntime().exec(Reha.proghome+"laden.bat");
				//System.out.println(prc.getErrorStream());
				//prc.waitFor();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
	}.execute();
	
	/*
	ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar",prog);
    try {
		processBuilder.start();
	} catch (IOException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	}
	System.out.println("Programm gestartet -> "+prog);
	*/
}
}