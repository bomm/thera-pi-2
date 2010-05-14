package hauptFenster;



import generalSplash.RehaSplash;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import javax.swing.UIManager;

import kurzAufrufe.KurzAufrufe;
import oOorgTools.OOTools;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.border.DropShadowBorder;
import org.jdesktop.swingx.plaf.windows.WindowsTaskPaneUI;
import org.therapi.reha.patient.AktuelleRezepte;
import org.therapi.reha.patient.LadeProg;



import dialoge.DatumWahl;
import events.PatStammEvent;
import events.PatStammEventClass;

import rechteTools.Rechte;
import sqlTools.ExUndHop;
import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemTools.TestePatStamm;
import terminKalender.TerminFenster;
import ag.ion.bion.officelayer.text.ITextDocument;

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
	private static JXTaskPane tp6 = null;
	private JXHyperlink oo1 = null;
	private JXHyperlink oo2 = null;
	public static boolean OOok = true;
	public static LinkeTaskPane thisClass = null;
	public static ITextDocument itestdocument = null;
	private ActionListener al;
	private String aktTag = "x";
	private String wahlTag = "y";
	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
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
		 * sp�tere TaskPane-Container die y-Dimension des Fensters �bersteigt.
		 */
		JScrollPane jScrp = new JScrollPane();
		jScrp.setBorder(null);
		jScrp.setViewportBorder(null);
		jScrp.setBackground(Color.white);
		jScrp.setPreferredSize(new Dimension(180, 100));
		DropShadowBorder dropShadow = new DropShadowBorder(Color.BLACK, 10, 1, 5, false, true, true, true);
		jScrp.setBorder(dropShadow);
		/**
		 * Jetz generieren wir den Taskpane-Container anschlie�end die TaskPanes
		 */
		jxTPcontainer =	new JXTaskPaneContainer();
		jxTPcontainer.setBackground(new Color(106,130,218));
		//jxTPcontainer.setPreferredSize(new Dimension(250,0));

		jxTPcontainer.add(getPatientenStamm());

		jxTPcontainer.add(getTerminKalender());
		
		jxTPcontainer.add(getOpenOfficeOrg());
		
		jxTPcontainer.add(getNuetzliches());		
		
		jxTPcontainer.add(getSystemEinstellungen());
		jxTPcontainer.add(getMonatsUebersicht());
		/**
		 * dann f�gen wir den TaskpaneContainer der ScrollPane hinzu
		 */
		jScrp.setViewportView(jxTPcontainer);
		jScrp.setVisible(true);
		jScrp.revalidate();
		this.add(jScrp,gridBagConstraints);
		this.validate();
		thisClass = this;
	}
	/**
	 * Task-Pane f�r den Patientenstamm erstellen
	 * @return
	 */
	
	private JXTaskPane getPatientenStamm(){
		Image img = null;
		tp1 = new JXTaskPane();
		UIManager.put("TaskPane.titleBackgroundGradientStart",Color.WHITE);
		UIManager.put("TaskPane.titleBackgroundGradientEnd",new Color(200,212,247));
		UIManager.put("TaskPane.background",new Color(214,223,247));
		UIManager.put("TaskPane.foreground",Color.BLUE);		
		UIManager.put("TaskPane.useGradient", Boolean.TRUE);
		WindowsTaskPaneUI wui = new WindowsTaskPaneUI();
		tp1.setUI(wui);
		tp1.setTitle("Stammdaten");
		//tp1.setIcon(new ImageIcon(Reha.proghome+"icons/personen16.gif"));				
		JXHyperlink jxLink = new JXHyperlink();
		jxLink.setText("Patienten und Rezepte");
		jxLink.setToolTipText("Strg+P = Patienten-/Rezeptstamm starten");
		img = new ImageIcon(Reha.proghome+"icons/kontact_contacts.png").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
		jxLink.setIcon(new ImageIcon(img));		
		jxLink.setClickedColor(new Color(0, 0x33, 0xFF));
		jxLink.addActionListener(this);
		jxLink.setEnabled(true);
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
			        	mitgebracht  = (String) tr.getTransferData(flavors[i]);
			      }
			      System.out.println(mitgebracht);
			      if(mitgebracht.indexOf("°") >= 0){
			    	  if( ! mitgebracht.split("°")[0].contains("TERMDAT")){
			    		  return;
			    	  }
			    	  doPatientDrop(mitgebracht.split("°")[2].trim());
			    	  //ProgLoader.ProgRoogleFenster(0, mitgebracht);
			    	  //Reha.thisClass.progLoader.ProgRoogleFenster(0, mitgebracht);
			      }
			      System.out.println(mitgebracht+" auf Patientenstamm gedropt");
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
		
		tp1.add(jxLink);
		jxLink = new JXHyperlink();
		jxLink.setText("Arzte");
		jxLink.setActionCommand("Arztstamm");
		jxLink.setToolTipText("Strg+A = Arztstamm starten");
		img = new ImageIcon(Reha.proghome+"icons/system-users.png").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
		jxLink.setIcon(new ImageIcon(img));				
		jxLink.setClickedColor(new Color(0, 0x33, 0xFF));
		jxLink.addActionListener(this);
		//jxLink.setEnabled(false);
		tp1.add(jxLink);
		jxLink = new JXHyperlink();
		jxLink.setText("Krankenkassen");
		jxLink.setToolTipText("Strg+K = Kassenstamm starten");
		img = new ImageIcon(Reha.proghome+"icons/krankenkasse.png").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
		jxLink.setIcon(new ImageIcon(img));				
		jxLink.setClickedColor(new Color(0, 0x33, 0xFF));
		jxLink.addActionListener(this);
		tp1.add(jxLink);
		//tp1.setExpanded(true);
		return tp1;
	}
	
	private JXTaskPane getTerminKalender(){
		Image img = null;
		tp4 = new JXTaskPane();
		UIManager.put("TaskPane.titleBackgroundGradientStart",Color.WHITE);
		UIManager.put("TaskPane.titleBackgroundGradientEnd",new Color(200,212,247));
		UIManager.put("TaskPane.background",new Color(214,223,247));
		UIManager.put("TaskPane.useGradient", Boolean.TRUE);
		WindowsTaskPaneUI wui = new WindowsTaskPaneUI();
		tp4.setUI(wui);		
		tp4.setTitle("Termin-Management");
		tp4.setIcon(new ImageIcon(Reha.proghome+"icons/table_mode.png"));				
		JXHyperlink jxLink = new JXHyperlink();
		jxLink.setText("Terminkalender starten");
		jxLink.setToolTipText("Strg+T = Terminkalender starten");
		img = new ImageIcon(Reha.proghome+"icons/evolution-addressbook.png").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
		jxLink.setIcon(new ImageIcon(img));
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
			        	mitgebracht  = (String) tr.getTransferData(flavors[i]);
			      }
			      System.out.println(mitgebracht);
			      if(mitgebracht.indexOf("°") >= 0){
			    	  if( ! mitgebracht.split("°")[0].contains("TERMDAT")){
			    		  return;
			    	  }
			    	  //ProgLoader.ProgRoogleFenster(0, mitgebracht);
			    	  Reha.thisClass.progLoader.ProgRoogleFenster(0, mitgebracht);
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
		String srugl = "<html><font color='#000000'>[</font><font color='#0000ff'>R</font><font color='#ff0000'>u</font>"+
		"<font color='#00ffff'><b>:</b></font><font color='#0000ff'>g</font><font color='#00ff00'>l</font>"+
		"<font color='#000000'>]</font>&nbsp;- Die Terminsuchmaschine";
		jxLink.setText(srugl);
		//jxLink.setText("[Ru:gl] - Die Terminsuchmaschine");
		jxLink.setToolTipText("Strg+R = [Ru:gl] starten");
		img = new ImageIcon(Reha.proghome+"icons/orca.png").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
		jxLink.setIcon(new ImageIcon(img));		
		jxLink.setClickedColor(new Color(0, 0x33, 0xFF));
		jxLink.setActionCommand("[Ru:gl] - Die Terminsuchmaschine");
		jxLink.addActionListener(this);
		tp4.add(jxLink);
		jxLink = new JXHyperlink();
		jxLink.setText("Wochenarbeitszeiten definieren");
		img = new ImageIcon(Reha.proghome+"icons/alacarte.png").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
		jxLink.setIcon(new ImageIcon(img));		
		jxLink.setClickedColor(new Color(0, 0x33, 0xFF));
		jxLink.addActionListener(this);
		tp4.add(jxLink);
		jxLink = new JXHyperlink();
		jxLink.setText("Akutliste - kurzfristige Termine");
		jxLink.setActionCommand("Akutliste");
		img = new ImageIcon(Reha.proghome+"icons/chronometer.png").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
		jxLink.setIcon(new ImageIcon(img));		
		jxLink.setClickedColor(new Color(0, 0x33, 0xFF));
		jxLink.addActionListener(this);
		tp4.add(jxLink);
		/*
		jxLink = new JXHyperlink();
		jxLink.setText("Monatsübersicht");
		jxLink.setActionCommand("monthview");
		jxLink.setClickedColor(new Color(0, 0x33, 0xFF));
		jxLink.addActionListener(this);
		tp4.add(jxLink);
		*/
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
		oo1.setClickedColor(new Color(0, 0x33, 0xFF));
		oo1.setIcon(SystemConfig.hmSysIcons.get("ooowriter"));
		oo1.addActionListener(this);
		tp3.add(oo1);
		oo2 = new JXHyperlink();
		oo2.setIcon(SystemConfig.hmSysIcons.get("ooocalc"));
		oo2.setText("OpenOffice-Calc");
		oo2.setClickedColor(new Color(0, 0x33, 0xFF));		
		oo2.addActionListener(this);
		tp3.add(oo2);
		oo2 = new JXHyperlink();
		oo2.setIcon(SystemConfig.hmSysIcons.get("oooimpress"));
		oo2.setText("OpenOffice-Impress");
		oo2.setClickedColor(new Color(0, 0x33, 0xFF));		
		oo2.addActionListener(this);
		tp3.add(oo2);
		tp3.setCollapsed(true);
		return tp3;
	}
	
	private JXTaskPane getNuetzliches(){
		Image img = null;
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
		img = new ImageIcon(Reha.proghome+"icons/home.gif").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
		jxLink.setIcon(new ImageIcon(img));		
		jxLink.addActionListener(this);
		tp5.add(jxLink);
		jxLink = new JXHyperlink();
		jxLink.setText("piTool - ScreenShots");
		jxLink.setClickedColor(new Color(0, 0x33, 0xFF));
		img = new ImageIcon(Reha.proghome+"icons/camera_unmount.png").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
		//Reha.proghome+"icons/cameraklein.png"
		jxLink.setIcon(new ImageIcon(img));
		jxLink.setActionCommand("piTool");
		jxLink.addActionListener(this);
		//jxLink.setEnabled(false);
		tp5.add(jxLink);
		jxLink = new JXHyperlink();
		jxLink.setText("piHelp - Hifetextgenerator");
		jxLink.setClickedColor(new Color(0, 0x33, 0xFF));	
		img = new ImageIcon(Reha.proghome+"icons/fragezeichenklein.png").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
		jxLink.setIcon(new ImageIcon(img));
		jxLink.setActionCommand("piHelp");
		jxLink.addActionListener(this);
		//jxLink.setEnabled(false);
		tp5.add(jxLink);
		jxLink = new JXHyperlink();
		jxLink.setText("Textbausteine - Therapiebericht");
		jxLink.setClickedColor(new Color(0, 0x33, 0xFF));	
		img = new ImageIcon(Reha.proghome+"icons/abiword.png").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
		jxLink.setIcon(new ImageIcon(img));
		jxLink.setActionCommand("piTextb");
		jxLink.addActionListener(this);
		//jxLink.setEnabled(false);
		tp5.add(jxLink);

		jxLink = new JXHyperlink();
		jxLink.setText("Textbausteine - Gutachten");
		jxLink.setClickedColor(new Color(0, 0x33, 0xFF));	
		img = new ImageIcon(Reha.proghome+"icons/abiword.png").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
		jxLink.setIcon(new ImageIcon(img));
		jxLink.setActionCommand("piArztTextb");
		jxLink.addActionListener(this);
		//jxLink.setEnabled(false);
		tp5.add(jxLink);

		tp5.setCollapsed(true);
		return tp5;
	}


	private JXTaskPane getSystemEinstellungen(){
		Image img = null;
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
		img = new ImageIcon(Reha.proghome+"icons/contact-new.png").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
		jxLink.setIcon(new ImageIcon(img));		
		jxLink.setClickedColor(new Color(0, 0x33, 0xFF));
		jxLink.addActionListener(this);
		jxLink.setEnabled(true);	
		tp2.add(jxLink);
		jxLink = new JXHyperlink();
		jxLink.setText("System Initialisierung");
		img = new ImageIcon(Reha.proghome+"icons/galternatives.png").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
		jxLink.setIcon(new ImageIcon(img));
		jxLink.setClickedColor(new Color(0, 0x33, 0xFF));
		jxLink.addActionListener(this);
		tp2.add(jxLink);
		jxLink = new JXHyperlink();
		jxLink.setText("Look & Feel");
		jxLink.addActionListener(this);
		jxLink.setEnabled(false);
		tp2.add(jxLink);
		return tp2;
	}
	
	private JXTaskPane getMonatsUebersicht(){
		tp6 = new JXTaskPane();
		UIManager.put("TaskPane.titleBackgroundGradientStart",Color.WHITE);
		UIManager.put("TaskPane.titleBackgroundGradientEnd",new Color(200,212,247));
		UIManager.put("TaskPane.background",new Color(214,223,247));
		UIManager.put("TaskPane.useGradient", Boolean.TRUE);
		WindowsTaskPaneUI wui = new WindowsTaskPaneUI();
		tp6.setUI(wui);
		tp6.setTitle("Monatsübersicht");
		final JXMonthView monthView = new JXMonthView ();
		  monthView.setPreferredColumnCount (1);
		  monthView.setPreferredRowCount (1);
		  monthView.setTraversable(true);
		  monthView.setShowingWeekNumber(true);
		  al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(TerminFenster.getThisClass()!= null){
					Date dat = monthView.getSelectionDate();
					if(dat==null){
						return;
					}
					wahlTag = sdf.format(monthView.getSelectionDate());
					if(wahlTag.equals(aktTag)){
						return;
					}
					aktTag = wahlTag;
					Reha.thisClass.progLoader.ProgTerminFenster(1, 0);
					TerminFenster.getThisClass().springeAufDatum(aktTag);
				}else{
					Date dat = monthView.getSelectionDate();
					if(dat==null){
						return;
					}
					wahlTag = sdf.format(monthView.getSelectionDate());
					if(wahlTag.equals(aktTag)){
						return;
					}
					aktTag = wahlTag;
					Reha.thisClass.progLoader.ProgTerminFenster(1, 0);
					SwingUtilities.invokeLater(new Runnable(){
						public void run(){
							TerminFenster.getThisClass().springeAufDatum(aktTag);
							
						}
					});
				}
				
			}
		  };
		  monthView.addActionListener(al);
		  tp6.add(monthView);
		  tp6.setCollapsed(true);
		return tp6;
	}

	
	public static void UpdateUI(){
		jxTPcontainer.updateUI();
		tp1.updateUI();
		tp2.updateUI();		
		tp3.updateUI();
		tp4.updateUI();
		tp5.updateUI();	
		tp6.updateUI();
		//System.out.println("TaskPane-Container L&F");
	}
	/**
	 * Eigener Event-Handler man wird sehen ob das vern�ftig ist.
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
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						try{
							Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
							//ProgLoader.SystemInitialisierung();
							Reha.thisClass.progLoader.SystemInit(1, "");
							Reha.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						}catch(Exception ex){
							ex.printStackTrace();
						}
						return null;
					}
					
				}.execute();

				/*SystemUtil sysUtil = new SystemUtil(Reha.thisFrame);
				sysUtil.setSize(800,600);
				sysUtil.setLocation(100,75);
				sysUtil.setVisible(true);
				*/
				break;
			}
			
			if (cmd.equals("Krankenkassen")){
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
						Reha.thisClass.progLoader.KassenFenster(0,TestePatStamm.PatStammKasseID());
						//ProgLoader.KassenFenster(0,TestePatStamm.PatStammKasseID());
						Reha.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						return null;
					}
				}.execute();
				break;
			}
			
			if (cmd.equals("Terminkalender starten")){
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
						Reha.thisClass.progLoader.ProgTerminFenster(1, 0);
						//ProgLoader.ProgTerminFenster(1,0);
						Reha.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						return null;
					}
				}.execute();
				break;
			}
			if (cmd.equals("Arztstamm")){
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
						Reha.thisClass.progLoader.ArztFenster(0,TestePatStamm.PatStammArztID());
						//ProgLoader.ArztFenster(0,TestePatStamm.PatStammArztID());
						Reha.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						return null;
					}
				}.execute();
				break;
			}
			if (cmd.equals("Wochenarbeitszeiten definieren")){
				JComponent termin = AktiveFenster.getFensterAlle("TerminFenster");
				if(termin != null){
					JOptionPane.showMessageDialog (null, "Achtung!!!!! \n\nWährend der Arbeitszeit-Definition\n" +
					"darf der Terminkalender aus Sicherheitsgründen nicht geöffnet sein.\n"+
					"Beenden Sie den Terminkalender und rufen Sie diese Funktion erneut auf.\n\n");
					return;
				}
				Reha.thisClass.progLoader.ProgTerminFenster(0, 2);
				//ProgLoader.ProgTerminFenster(0,2);
				//MaskenErstellen();
				break;
			}
			if (cmd.equals("monthview")){
				new DatumWahl(200,200);
				break;
			}
			
			if (cmd.equals("OpenOffice-Writer")){
				OOTools.starteLeerenWriter();
				break;
			}
			
			if (cmd.equals("OpenOffice-Calc")){
				OOTools.starteLeerenCalc();
				break;
			}

			if (cmd.equals("OpenOffice-Impress")){
				OOTools.starteLeerenImpress();
				break;
			}

			if (cmd.equals("Benutzerverwaltung")){
				Reha.thisClass.progLoader.BenutzerrechteFenster(1,"");
				//ProgLoader.ProgBenutzerVerwaltung(0);				
				break;
			}
			if (cmd.equals("[Ru:gl] - Die Terminsuchmaschine")){
				Reha.thisClass.progLoader.ProgRoogleFenster(0,null);
				//ProgLoader.ProgRoogleFenster(0,null);
				break;
			}
			if (cmd.equals("RTA-Wisssen das Universalwissen")){
				break;
			}
			if (cmd.equals("Thera-PI - Browser")){
				File file = new File(Reha.proghome+"xulrunner/xulrunner.exe");
				if(! file.exists()){
					JOptionPane.showMessageDialog(null,"Die Mozilla-Runtime 'xulrunner' wurde nicht, oder nicht korrekt installiert\n"+
							"Der Thera-PI - Browser kann deshalb nicht gestartet werden");
					return;
				}
				new LadeProg(Reha.proghome+"RehaWissen.jar");
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
			if (cmd.equals("Patienten und Rezepte")){
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						setCursor(new Cursor(Cursor.WAIT_CURSOR));
						Reha.thisClass.progLoader.ProgPatientenVerwaltung(1);
						//Reha.thisClass.progLoader.ProgTerminFenster(0, 1);
						//ProgLoader.ProgPatientenVerwaltung(1);
						setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						return null;
					}
				}.execute();
				break;
			}
			if (cmd.equals("piHelp")){
				new LadeProg(Reha.proghome+"piHelp.jar");
				break;
			}
			if (cmd.equals("piTool")){
				new LadeProg(Reha.proghome+"piTool.jar");				
				break;
			}
			if (cmd.equals("piTextb")){
				new LadeProg(Reha.proghome+"TBedit.jar "+
						Reha.proghome+"ini/"+Reha.aktIK+"/rehajava.ini"+" "+
						Reha.proghome+"ini/textbaustein.ini");	
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						RehaSplash rspl = new RehaSplash(null,"Textbaustein-Editor laden....dieser Vorgang kann einige Sekunden dauern...");
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
			if (cmd.equals("piArztTextb")){
				if(!Rechte.hatRecht(Rechte.Sonstiges_textbausteinegutachten, true)){
					return;
				}
				new LadeProg(Reha.proghome+"ArztBaustein.jar "+
						Reha.proghome+"ini/"+Reha.aktIK+"/rehajava.ini");	
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						RehaSplash rspl = new RehaSplash(null,"Textbaustein-Editor laden....dieser Vorgang kann einige Sekunden dauern...");
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
			
			
			
			if (cmd.equals("Akutliste")){
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						KurzAufrufe.starteFunktion("Akutliste",null,null);
						return null;
					}
				}.execute();
				
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						RehaSplash rspl = new RehaSplash(null,"Akutliste starten -  dieser Vorgang kann einige Sekunden dauern...");
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
		Statement stmt = null;
		try{
			stmt = (Statement) Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE );
			for(int i=1;i<61;i++){
				for(int t=1;t<8;t++){
					behandler  = (i<10 ? "0"+i+"BEHANDLER" : Integer.toString(i)+"BEHANDLER");
					sstmt = "insert into masken set behandler='"+behandler+"' , art = '"+t+"' ,belegt='1', N1='@FREI', TS1='07:00:00', TD1='900', TE1='22:00:00'";
					System.out.println(sstmt);
					try {
						stmt.execute(sstmt);
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
	private void doPatientDrop(String rez_nr){
		String pat_int = "";
		String reznr = rez_nr;
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
	
/************************************************************/

/************************************************************/

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


/*	
class ladeProg{

	public ladeProg(String prog){
	String progname= null;
	if(prog.indexOf(" ")>=0){
		progname = prog.split(" ")[0];
	}else{
		progname = prog;
	}
	File f = new File(progname);
	if(! f.exists()){
		JOptionPane.showMessageDialog(null,"Diese Software ist auf Ihrem System nicht installiert!");
		return;
	}
	String vmload = "java -jar ";
	String commandx = vmload + prog; 
    File ausgabedatei = new File(Reha.proghome+"laden.bat"); 
    FileWriter fw;
	try {
		fw = new FileWriter(ausgabedatei);
	    BufferedWriter bw = new BufferedWriter(fw); 
	    bw.write(commandx); 
	    bw.close(); 
	} catch (IOException e1) {
		e1.printStackTrace();
	} 
	final String xprog = prog;
	new SwingWorker<Void, Void>(){

		@Override
		protected Void doInBackground() throws Exception {
			try {
				List<String>list = Arrays.asList(xprog.split(" "));
				ArrayList<String> alist = new ArrayList<String>(list);
				alist.add(0,"-jar");
				alist.add(0,"java");
				System.out.println(list);
				System.out.println("Die Liste = "+alist);
				
				System.out.println("Liste = "+list);
				Process process = new ProcessBuilder(alist).start();
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


			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
	}.execute();
}
}
*/