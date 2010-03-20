package patientenFenster;

import hauptFenster.Reha;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import jxTableTools.DateTableCellEditor;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.IconValues;
import org.jdesktop.swingx.renderer.MappedValue;
import org.jdesktop.swingx.renderer.StringValues;

import patientenFenster.AktuelleRezepte.MyAktRezeptTableModel;
import patientenFenster.AktuelleRezepte.MyTermTableModel;
import patientenFenster.AktuelleRezepte.RezepteListSelectionHandler;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemTools.Colors;
import systemTools.JCompTools;
import terminKalender.DatFunk;

public class Historie extends JXPanel implements ActionListener, TableModelListener, PropertyChangeListener{
	//public static Historie historie = null;
	JXPanel leerPanel = null;
	//JXPanel vollPanel = null;
	HistorPanel vollPanel = null;
	JXPanel wechselPanel = null;
	public JLabel anzahlTermine= null;
	public JLabel anzahlHistorie= null;
	public String aktPanel = "";
	public JXTable tabhistorie = null;
	public JXTable tabaktterm = null;
	public MyHistorieTableModel dtblm;
	public MyHistorTermTableModel dtermm;
	public TableCellEditor tbl = null;
	public boolean rezneugefunden = false;
	public boolean neuDlgOffen = false;
	public String[] indphysio = null;
	public String[] indergo = null;
	public String[] indlogo = null;
	public HistorDaten jpan1 = null;
	public JButton[] histbut = {null,null,null,null};
	public static boolean inRezeptDaten = false;

	public Historie(){
		super();
		//historie = this;
		setOpaque(false);
		setLayout(new BorderLayout());
		/********zuerst das Leere Panel basteln**************/
		leerPanel = new KeinRezept("noch keine Rezepte in der Historie für diesen Patient");
		leerPanel.setName("leerpanel");
		leerPanel.setOpaque(false);
		
		/********dann das volle**************/		
		JXPanel allesrein = new JXPanel(new BorderLayout());
		allesrein.setOpaque(false);
		allesrein.setBorder(null);
		
		FormLayout lay = new FormLayout("5dlu,fill:0:grow(1.00),0dlu",
		"0dlu,p,2dlu,p,2dlu,fill:0:grow(1.00),5dlu");
		CellConstraints cc = new CellConstraints();
		allesrein.setLayout(lay);
		
		wechselPanel = new JXPanel(new BorderLayout());
		wechselPanel.setOpaque(false);
		wechselPanel.setBorder(null);
		wechselPanel.add(leerPanel,BorderLayout.CENTER);
		aktPanel = "leerPanel";
		
		allesrein.add(getToolbar(),cc.xy(2, 2));
		//allesrein.add(getTabelle(),cc.xy(2, 4));
		allesrein.add(wechselPanel,cc.xy(2, 6));

		add(JCompTools.getTransparentScrollPane(allesrein),BorderLayout.CENTER);
		validate();

		new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				try{
				// TODO Auto-generated method stub
				//vollPanel = new JXPanel();
				//HistorPanel vollPanel = new HistorPanel();
				vollPanel = new HistorPanel();

				FormLayout vplay = new FormLayout("fill:0:grow(0.60),5dlu,fill:0:grow(0.40),5dlu","13dlu,53dlu,5dlu,fill:0:grow(1.00),0dlu");
				CellConstraints vpcc = new CellConstraints();
				vollPanel.setLayout(vplay);
				vollPanel.setOpaque(false);
				vollPanel.setBorder(null);
				
				Font font = new Font("Tahome",Font.PLAIN,11);
				anzahlHistorie = new JLabel("Anzahl Rezepte in Historie: 0");
				anzahlHistorie.setFont(font);
				vollPanel.add(anzahlHistorie,vpcc.xy(1,1));
				
				vollPanel.add(getTabelle(),vpcc.xywh(1,2,1,1));

				anzahlTermine = new JLabel("Anzahl Termine: 0");
				anzahlTermine.setFont(font);
				anzahlTermine.setOpaque(false);
				vollPanel.add(anzahlTermine,vpcc.xywh(3,1,1,1));
				
				JXPanel dummy = new JXPanel();
				dummy.setOpaque(false);
				//dummy.setBackground(Color.BLACK);
				FormLayout dumlay = new FormLayout("fill:0:grow(0.25),p,fill:0:grow(0.25),p,fill:0:grow(0.25),p,fill:0:grow(0.25)",
													"fill:0:grow(1.00),2dlu,p,2dlu");
				CellConstraints dumcc = new CellConstraints();
				dummy.setLayout(dumlay);
				vollPanel.add(dummy,vpcc.xywh(3,2,1,3));
				
				
				dummy.add(getTermine(),dumcc.xyw(1, 1, 7));
				dummy.add(getTerminToolbar(),dumcc.xyw(1, 3, 7));

				jpan1 = new HistorDaten();
				vollPanel.add(jpan1,vpcc.xyw(1,4,1));
				//indiSchluessel();
				}catch(Exception ex){
					ex.printStackTrace();
				}
			
				return null;
			}
			
		}.execute();
		

	}
	class HistorPanel extends JXPanel{
		ImageIcon hgicon;
		int icx,icy;
		AlphaComposite xac1 = null;
		AlphaComposite xac2 = null;		
		HistorPanel(){
			super();
			setOpaque(false);
			/*
			hgicon = SystemConfig.hmSysIcons.get("historie"); 
			icx = hgicon.getIconWidth()/2;
			icy = hgicon.getIconHeight()/2;
			xac1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.075f); 
			xac2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f);
			*/			
			
		}

		//@Override
		public void paintComponent( Graphics g ) { 
			super.paintComponent( g );
			Graphics2D g2d = (Graphics2D)g;
			
			if(hgicon != null){
				//g2d.setComposite(this.xac1);
				//g2d.drawImage(hgicon.getImage(), (getWidth()/3)-(icx+20) , (getHeight()/2)-(icy-40),null);
				//g2d.setComposite(this.xac2);
			}
		}
	}	
	
	public JToolBar getTerminToolbar(){
		JToolBar jtb = new JToolBar();
		jtb.setOpaque(false);
		jtb.setRollover(true);
		jtb.setBorder(null);
		jtb.setOpaque(false);
		
		JButton jbut = new JButton();
		jbut.setIcon(SystemConfig.hmSysIcons.get("neu"));
		//jbut.setIcon(new ImageIcon(Reha.proghome+"icons/list-add.png"));
		jbut.setToolTipText("Neuen Termin eintragen");
		jbut.setActionCommand("terminplus");
		jbut.addActionListener(this);
		jbut.setEnabled(false);
		jtb.add(jbut);

		jbut = new JButton();
		jbut.setIcon(SystemConfig.hmSysIcons.get("delete"));
		//jbut.setIcon(new ImageIcon(Reha.proghome+"icons/list-remove.png"));
		jbut.setToolTipText("Termin löschen");
		jbut.setActionCommand("terminminus");
		jbut.addActionListener(this);
		jbut.setEnabled(false);		
		jtb.add(jbut);
		
		jtb.addSeparator(new Dimension(40,0));
		jbut = new JButton();
		jbut.setIcon(SystemConfig.hmSysIcons.get("sort"));
		//jbut.setIcon(new ImageIcon(Reha.proghome+"icons/alphab_sort_22.png"));
		jbut.setActionCommand("terminsortieren");
		jbut.addActionListener(this);		
		jbut.setToolTipText("Termine nach Datum sortieren");
		jbut.setEnabled(false);		
		jtb.add(jbut);
		return jtb;		
		
	}	
	public JScrollPane getTermine(){
		
		dtermm = new MyHistorTermTableModel();
		dtermm.addTableModelListener(this);
		String[] column = 	{"Beh.Datum","Behandler","Text","Beh.Art",""};
		dtermm.setColumnIdentifiers(column);
		tabaktterm = new JXTable(dtermm);
		tabaktterm.setHighlighters(HighlighterFactory.createSimpleStriping(Colors.PiOrange.alpha(0.25f)));
		tabaktterm.setDoubleBuffered(true);
		tabaktterm.addPropertyChangeListener(this);
		tabaktterm.setEditable(false);
		tabaktterm.setSortable(false);
		//SortOrder setSort = SortOrder.ASCENDING;
		//tabaktterm.setSortOrder(4,(SortOrder) setSort);
		tabaktterm.setSelectionMode(0);
		tabaktterm.setHorizontalScrollEnabled(true);
		tbl = new DateTableCellEditor();
		//tbl = new MyEditor(new JTextField());
		tabaktterm.getColumnModel().getColumn(0).setCellEditor(tbl);
		//tabaktterm.getColumn(0).setCellEditor(new DatumTableCellEditor(new JFormattedTextField()));
		//tabaktterm.getColumnModel().getColumn(0).setCellEditor(new DatumTableCellEditor(new JFormattedTextField()));
		//tabaktterm.getColumn(0).setCellEditor(new MyDateEditor(new SimpleDateFormat("dd.mm.yyyyy")));

		tabaktterm.getColumn(0).setMinWidth(60);
		tabaktterm.getColumn(1).setMinWidth(60);
		tabaktterm.getColumn(2).setMinWidth(40);
		tabaktterm.getColumn(3).setMinWidth(40);
		tabaktterm.getColumn(4).setMinWidth(0);
		tabaktterm.getColumn(4).setMaxWidth(0);
		tabaktterm.setOpaque(true);
		tabaktterm.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
				//System.out.println("keypressed in Editor");
				if(arg0.getKeyCode()==10){
					//arg0.consume();
					//tbl.stopCellEditing();
				}
				if(arg0.getKeyCode()==27){
					//System.out.println("cancel in tabelle");
					//tbl.cancelCellEditing();
				}
			}
			
		});
		tabaktterm.validate();
		tabaktterm.setName("AktTerm");
		//tabaktterm.setPreferredSize(new Dimension(300,300));
		//tabaktterm.addPropertyChangeListener(this);
		JScrollPane termscr = JCompTools.getTransparentScrollPane(tabaktterm);
		termscr.getVerticalScrollBar().setUnitIncrement(15);
		return termscr;
	}

	public JXPanel getTabelle(){
		JXPanel dummypan = new JXPanel(new BorderLayout());
		dummypan.setOpaque(false);
		dummypan.setBorder(null);
		dtblm = new MyHistorieTableModel();
		String[] column = 	{"Rezept-Nr.","bezahlt","Rez-Datum","angelegt am","spät.Beginn","Pat-Nr.",""};
		dtblm.setColumnIdentifiers(column);
		tabhistorie = new JXTable(dtblm);
		tabhistorie.setHighlighters(HighlighterFactory.createSimpleStriping(Colors.PiOrange.alpha(0.25f)));
		tabhistorie.setDoubleBuffered(true);
		tabhistorie.setEditable(false);
		tabhistorie.setSortable(false);
		tabhistorie.getColumn(0).setMaxWidth(75);
		TableCellRenderer renderer = new DefaultTableRenderer(new MappedValue(StringValues.EMPTY, IconValues.ICON), JLabel.CENTER);
		tabhistorie.getColumn(1).setCellRenderer(renderer);
		tabhistorie.getColumn(1).setMaxWidth(45);
		tabhistorie.getColumn(2).setMaxWidth(75);
		tabhistorie.getColumn(3).setMaxWidth(75);
		//tabaktrez.getColumn(4).setMaxWidth(70);
		tabhistorie.getColumn(5).setMinWidth(0);
		tabhistorie.getColumn(5).setMaxWidth(0);		
		tabhistorie.getColumn(6).setMinWidth(0);
		tabhistorie.getColumn(6).setMaxWidth(0);		
		tabhistorie.validate();
		tabhistorie.setName("AktRez");
		tabhistorie.setSelectionMode(0);
		//tabaktrez.addPropertyChangeListener(this);
		tabhistorie.getSelectionModel().addListSelectionListener( new HistorRezepteListSelectionHandler());
		tabhistorie.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				if(arg0.getClickCount()==2){
					while(inRezeptDaten){
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					//neuanlageRezept(false,"");
				}
			}
		});
		tabhistorie.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode()==10){
					arg0.consume();
					//neuanlageRezept(false,"");
				}
				if(arg0.getKeyCode()==27){
					arg0.consume();
				}
			}

		});
		//tabaktrez.getSelectionModel().addListSelectionListener(this);
		//dtblm.addTableModelListener(this);
		dummypan.setPreferredSize(new Dimension(0,100));
		JScrollPane aktrezscr = JCompTools.getTransparentScrollPane((Component)tabhistorie); 
		aktrezscr.getVerticalScrollBar().setUnitIncrement(15);
		dummypan.add(aktrezscr,BorderLayout.CENTER);
		dummypan.validate();
		return dummypan;
	}
	
	public void setzeRezeptPanelAufNull(boolean aufnull){
		if(aufnull){
			if(aktPanel.equals("vollPanel")){
				wechselPanel.remove(vollPanel);
				wechselPanel.add(leerPanel);
				aktPanel = "leerPanel";
				for(int i = 0; i < 4;i++){
					histbut[i].setEnabled(false);
				}
			}
		}else{
			if(aktPanel.equals("leerPanel")){
				wechselPanel.remove(leerPanel);
				wechselPanel.add(vollPanel);
				aktPanel = "vollPanel";
				for(int i = 0; i < 4;i++){
					histbut[i].setEnabled(true);
				}
			}
		}
	}
	
	
	public JToolBar getToolbar(){
		JToolBar jtb = new JToolBar();
		jtb.setOpaque(false);
		jtb.setRollover(true);
		jtb.setBorder(null);
		jtb.setOpaque(false);

		histbut[0] = new JButton();
		histbut[0].setIcon(SystemConfig.hmSysIcons.get("arztbericht"));
		histbut[0].setToolTipText("Nachträglich Arztbericht Rezept erstellen");
		histbut[0].setActionCommand("arztbericht");
		histbut[0].addActionListener(this);		
		jtb.add(histbut[0]);

		histbut[1] = new JButton();
		histbut[1].setIcon(SystemConfig.hmSysIcons.get("info2"));
		histbut[1].setToolTipText("Zusatzinfos zum Rezept in der Historie");
		histbut[1].setActionCommand("historinfo");
		histbut[1].addActionListener(this);		
		jtb.add(histbut[1]);

		histbut[2] = new JButton();
		histbut[2].setIcon(SystemConfig.hmSysIcons.get("euro"));
		histbut[2].setToolTipText("Gesamtumsatz des Patienten (aller aktuellen und in der Historie befindlichen Rezepte)");
		histbut[2].setActionCommand("historumsatz");
		histbut[2].addActionListener(this);		
		jtb.add(histbut[2]);
		
		histbut[3] = new JButton();
		histbut[3].setIcon(SystemConfig.hmSysIcons.get("einzeltage"));
		histbut[3].setToolTipText("Behandlungstage des Historien-Rezeptes drucken");
		histbut[3].setActionCommand("historprinttage");
		histbut[3].addActionListener(this);		
		jtb.add(histbut[3]);
		for(int i = 0; i < 4;i++){
			histbut[i].setEnabled(false);
		}
		
		
		return jtb;
	}
	
	
	

	public void macheTabelle(Vector vec){
		if(vec.size()> 0){
			dtblm.addRow(vec);	
		}else{
			dtblm.setRowCount(0);
			tabhistorie.validate();
		}
		
	}
	private void holeEinzelTermine(int row,Vector vvec){
		Vector xvec = null;
		if(vvec == null){
			xvec = SqlInfo.holeSatz("lza", "termine", "id='"+tabhistorie.getValueAt(row,6)+"'", Arrays.asList(new String[] {}));			
		}else{
			xvec = vvec;
		}

		String terms = (String) xvec.get(0);
		//System.out.println(terms+" / id der rezeptes = "+tabaktrez.getValueAt(row,4));
		//System.out.println("Inhalt von Termine = *********\n"+terms+"**********");
		if(terms==null){
			dtermm.setRowCount(0);
			tabaktterm.validate();
			anzahlTermine.setText("Anzahl Termine: 0");			
			return;
		}
		if(terms.equals("")){
			dtermm.setRowCount(0);
			tabaktterm.validate();
			anzahlTermine.setText("Anzahl Termine: 0");
			return;
		}
		String[] tlines = terms.split("\n");
		int lines = tlines.length;
		//System.out.println("Anzahl Termine = "+lines);
		Vector tvec = new Vector();
		dtermm.setRowCount(0);
		for(int i = 0;i<lines;i++){
			String[] terdat = tlines[i].split("@");
			int ieinzel = terdat.length;
			//System.out.println("Anzahl Splits = "+ieinzel);
			tvec.clear();
			for(int y = 0; y < ieinzel;y++){
				if(y==0){
					
					tvec.add((terdat[y].trim().equals("") ? "  .  .    " : terdat[y]));
				}else{
					tvec.add(terdat[y]);					
				}
				//System.out.println("Feld "+y+" = "+terdat[y]);	
			}
			//System.out.println("Termivector = "+tvec);
			dtermm.addRow((Vector)tvec.clone());
		}
		anzahlTermine.setText("Anzahl Termine: "+lines);
		
		
	}
	/******************
	 * 
	 * 
	 */
	public void doRechneAlles(){
		double gesamtHistor = 0.00; //new Double(0.00);
		double gesamtAkt = 0.00; //new Double(0.00);
		gesamtHistor = doRechneHistorie("lza");
		gesamtAkt = doRechneHistorie("verordn");
		double gesamtumsatz = gesamtHistor+gesamtAkt;
		DecimalFormat dfx = new DecimalFormat( "0.00" );
		String msg = "<html><font font-family='Courier New'>Gesamtumsatz von Patient --> "+(String) Reha.thisClass.patpanel.patDaten.get(2)+", "+
		(String) Reha.thisClass.patpanel.patDaten.get(3)+"&nbsp;&nbsp;&nbsp;&nbsp;"+   
		"<br><br>Historie&nbsp;=&nbsp;"+dfx.format(gesamtHistor)+" EUR"+
		"<br>Aktuell&nbsp;&nbsp;=&nbsp;"+dfx.format(gesamtAkt)+" EUR"+
		"<br><br><p><b>Gesamt = <font align='center' color='#FF0000'>"+dfx.format(gesamtumsatz)+" EUR </font></b></p><br><br></font>";

	    JOptionPane optionPane = new JOptionPane();
	    optionPane.setMessage(msg);
	    optionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
	    String xtitel = "";
	    if(gesamtumsatz < 1000.00){
	    	xtitel ="könnte besser sein...";
	    }else if(gesamtumsatz > 1000.00 && gesamtumsatz < 2000.00){
	    	xtitel ="geht doch...";
	    }else if(gesamtumsatz > 2000.00){
	    	xtitel ="'Sternle-Patient' bitte warmhalten...";
	    }
	    JDialog dialog = optionPane.createDialog(null, xtitel);
	    dialog.setVisible(true);

		
		
	}
	public double doRechneHistorie(String db){
		//String[] column = 	{"Rezept-Nr.","bezahlt","Rez-Datum","angelegt am","sp�t.Beginn","Pat-Nr.",""};
		int rows = tabhistorie.getRowCount();
		String felder = "anzahl1,anzahl2,anzahl3,anzahl3,preise1,preise2,preise3,preise4";
		Double gesamtumsatz = new Double(0.00); 
		//DecimalFormat dfx = new DecimalFormat( "0.00" );
		if(db.equals("lza")){
			if(rows <= 0){
				//JOptionPane.showMessageDialog(null, "F�r diesen Patient wurde noch keine Verordnung abgerechnet!");
				return new Double(0.00);
			}
			for(int i = 0; i < rows;i++){
				String suchrez = (String)tabhistorie.getValueAt(i,6);
				Vector vec = SqlInfo.holeSatz(db, felder, "id='"+suchrez+"'", Arrays.asList(new String[] {}));
				if(vec.size() > 0){
					BigDecimal preispos = BigDecimal.valueOf(new Double(0.00));
					for(int anz = 0;anz <4;anz++){
						preispos = BigDecimal.valueOf(new Double((String)vec.get(anz+4))).multiply( BigDecimal.valueOf(new Double((String)vec.get(anz)))) ;
//						System.out.println("Einzelpreis von "+anz+" von "+suchrez+" = "+(String)vec.get(anz+4)+" anzahl = "+(String)vec.get(anz));
//						System.out.println("PosUmsatz von "+suchrez+" = "+dfx.format(preispos.doubleValue()));
						gesamtumsatz = gesamtumsatz+preispos.doubleValue();
					}
				}
			}
		}else{
			String cmd = "pat_intern='"+Reha.thisClass.patpanel.aktPatID+"'";
			System.out.println(cmd);
			Vector vec = SqlInfo.holeSaetze(db, "id,rez_nr",cmd , Arrays.asList(new String[] {}));
			rows = vec.size();
			System.out.println("Gefundene aktuelle Rezepte: "+rows);
			for(int i = 0; i < rows;i++){
				System.out.println(((Vector)vec.get(i)).get(1));
				String suchrez = (String)((Vector)vec.get(i)).get(0);//(String)tabhistorie.getValueAt(i,6);
				Vector vec2 = SqlInfo.holeSatz(db, felder, "id='"+suchrez+"'", Arrays.asList(new String[] {}));
				if(vec2.size() > 0){
					BigDecimal preispos = BigDecimal.valueOf(new Double(0.00));
					for(int anz = 0;anz <4;anz++){
						preispos = BigDecimal.valueOf(new Double((String)vec2.get(anz+4))).multiply( BigDecimal.valueOf(new Double((String)vec2.get(anz)))) ;
//						System.out.println("Einzelpreis von "+anz+" von "+suchrez+" = "+(String)vec.get(anz+4)+" anzahl = "+(String)vec.get(anz));
//						System.out.println("PosUmsatz von "+suchrez+" = "+dfx.format(preispos.doubleValue()));
						gesamtumsatz = gesamtumsatz+preispos.doubleValue();
					}
				}
			}
			
		}
		return gesamtumsatz;
		/*
		String ums = "Gesamtumsatz von Patient "+(String) Reha.thisClass.patpanel.patDaten.get(2)+", "+
		(String) Reha.thisClass.patpanel.patDaten.get(3)+" = "+dfx.format(gesamtumsatz)+" EUR **********";
		JOptionPane.showMessageDialog(null,ums);
		*/
	}
	/******************
	 * 
	 * 
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		String cmd = arg0.getActionCommand();
		if(cmd.equals("arztbericht")){
			if(aktPanel.equals("leerPanel")){
				JOptionPane.showMessageDialog(null,"Ich sag jetzt nix....\n\n"+
						"....außer - und für welches der nicht vorhandenen Rezepte in der Historie wollen Sie einen Therapiebericht erstellen....");
				return;
			}
			boolean neuber = true;
			int berid = 0;
			String xreznr;
			String xverfasser = "";
			int currow = tabhistorie.getSelectedRow();
			if(currow >=0){
				xreznr = (String)tabhistorie.getValueAt(currow,0);
			}else{
				xreznr = ""; 
			}
			
			int  iexistiert = Reha.thisClass.patpanel.berichte.berichtExistiert(xreznr);
			if(iexistiert > 0){
				xverfasser = Reha.thisClass.patpanel.berichte.holeVerfasser();
				neuber = false;
				berid = iexistiert;
				String meldung = "<html>Für das Historienrezept <b>"+xreznr+"</b> existiert bereits ein Bericht.<br>\nVorhandener Bericht wird jetzt geöffnet";
				JOptionPane.showMessageDialog(null, meldung);
			}


			final boolean xneuber = neuber;
			final String xxreznr = xreznr;
			final int xberid = berid;
			final int xcurrow = currow;
			final String xxverfasser = xverfasser;
			/*
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
				*/
					System.out.println("Vor arzbericht-aufruf");
					ArztBericht ab = new ArztBericht(null,"arztberichterstellen",xneuber,xxreznr,xberid,1,xxverfasser,"",xcurrow);
					ab.setModal(true);
					ab.setLocationRelativeTo(null);
					//ab.toFront();
					System.out.println("vor Arzbericht set Visible");
					ab.setVisible(true);
					ab = null;
					System.out.println("Arzbericht=null");
				/*	
					return null;
				}
				
			}.execute();
			*/
			/*
			ArztBericht ab = new ArztBericht(null,"arztberichterstellen",neuber,xreznr,berid,1,"","",currow);
			ab.setModal(true);
			ab.setLocationRelativeTo(null);
			ab.setVisible(true);
			ab = null;
			*/
			return;
		}else if(cmd.equals("historinfo")){
			return;			
		}else if(cmd.equals("historumsatz")){
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					// TODO Auto-generated method stub
					doRechneAlles();
					return null;
				}
			}.execute();
			return;			
		}else if(cmd.equals("historprinttage")){
			return;			
		}
		
	}
	@Override
	public void tableChanged(TableModelEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	void setRezeptDaten(){
		int row = this.tabhistorie.getSelectedRow();
		if(row >= 0){
			final int xrow = row;
			SwingUtilities.invokeLater(new Runnable(){
				public  void run(){
					String reznr = (String)tabhistorie.getValueAt(xrow,0);
					String id = (String)tabhistorie.getValueAt(xrow,6);
					jpan1.setRezeptDaten(reznr,id);
					System.out.println("Aus Bericht....."+reznr+"....."+id);
				}
			});	

		}
	}	
	private String macheHtmlTitel(int anz,String titel){
		
		String ret = titel+" - "+Integer.toString(anz);
		
		/*
		String ret = "<html>"+titel+
		(anz > 0 ? " - <font color='#ff0000'>"+new Integer(anz).toString()+"<font></html>" : " - <font color='#000000'>"+new Integer(anz).toString()+"</font>");
		*/
		return ret;
	}
	/*************************************************/
	@SuppressWarnings("unchecked")
	public void holeRezepte(String patint,String rez_nr){
		final String xpatint = patint;
		final String xrez_nr = rez_nr;
/*
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
		*/
				//String sstmt = "select * from verordn where PAT_INTERN ='"+xpatint+"' ORDER BY REZ_DATUM";
				Vector<Vector<String>> vec = SqlInfo.holeSaetze("lza", "rez_nr,zzstatus,DATE_FORMAT(rez_datum,'%d.%m.%Y') AS drez_datum,DATE_FORMAT(datum,'%d.%m.%Y') AS datum," +
						"DATE_FORMAT(lastdate,'%d.%m.%Y') AS datum,pat_intern,id", 
						"pat_intern='"+xpatint+"' ORDER BY rez_datum DESC", Arrays.asList(new String[]{}));

				int anz = vec.size();

				for(int i = 0; i < anz;i++){
					if(i==0){
						dtblm.setRowCount(0);						
					}

					int zzbild = 0;
					if( ((Vector)vec.get(i)).get(1) == null){
						zzbild = 0;
					}else if(!((Vector)vec.get(i)).get(1).equals("")){
						zzbild = new Integer((String) ((Vector)vec.get(i)).get(1) );
					}
					//((Vector)vec.get(i)).set(3, Reha.thisClass.patpanel.imgzuzahl[zzbild]);
					
					//System.out.println("Inhalt von zzstatus ="+zzbild);
					dtblm.addRow((Vector)vec.get(i));
					
					dtblm.setValueAt(Reha.thisClass.patpanel.imgzuzahl[zzbild], i, 1);
					if(i==0){
						final int ix = i;
	                    new Thread(){
	                    	public void run(){
	                    		holeEinzelTermine(ix,null);
	                    	}
	                    }.start();
					}
				}
				Reha.thisClass.patpanel.getTab().setTitleAt(1,macheHtmlTitel(anz,"Rezept-Historie"));
				if(anz > 0){
					setzeRezeptPanelAufNull(false);
					int anzeigen = -1;
					if(xrez_nr.length() > 0){
						int row = 0;
						rezneugefunden = true;
						for(int ii = 0; ii < anz;ii++){
							if(tabhistorie.getValueAt(ii,0).equals(xrez_nr)){
								row = ii;
								break;
							}
							
						}
						tabhistorie.setRowSelectionInterval(row, row);
						jpan1.setRezeptDaten((String)tabhistorie.getValueAt(row, 0),(String)tabhistorie.getValueAt(row, 6));
						tabhistorie.scrollRowToVisible(row);
						holeEinzelTermine(row,null);
						//System.out.println("rezeptdaten akutalisieren in holeRezepte 1");
					}else{
						rezneugefunden = true;
						tabhistorie.setRowSelectionInterval(0, 0);
						jpan1.setRezeptDaten((String)tabhistorie.getValueAt(0, 0),(String)tabhistorie.getValueAt(0, 6));
						//System.out.println("rezeptdaten akutalisieren in holeRezepte 1");						
					}
					anzahlHistorie.setText("Anzahl Rezepte in Historie: "+anz);
					wechselPanel.revalidate();
					wechselPanel.repaint();					
				}else{
					setzeRezeptPanelAufNull(true);
					anzahlHistorie.setText("Anzahl Rezepte in Historie: "+anz);
					wechselPanel.revalidate();
					wechselPanel.repaint();
					dtblm.setRowCount(0);
					dtermm.setRowCount(0);
				}
				/*					
				return null;
			}
		
		}.execute();
*/		
	}
	
	
	/*************************************************/
	class HistorRezepteListSelectionHandler implements ListSelectionListener {

	    public void valueChanged(ListSelectionEvent e) {
			if(rezneugefunden){
				rezneugefunden = false;
				return;
			}
	        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
	        
	        //int firstIndex = e.getFirstIndex();
	        //int lastIndex = e.getLastIndex();
	        boolean isAdjusting = e.getValueIsAdjusting();
	        if(isAdjusting){
	        	return;
	        }
			//StringBuffer output = new StringBuffer();
	        if (lsm.isSelectionEmpty()) {

	        } else {
	            int minIndex = lsm.getMinSelectionIndex();
	            int maxIndex = lsm.getMaxSelectionIndex();
	            for (int i = minIndex; i <= maxIndex; i++) {
	                if (lsm.isSelectedIndex(i)) {
	                	final int ix = i;
	                	
	                	new SwingWorker<Void,Void>(){

							@Override
							protected Void doInBackground() throws Exception {
								try{
									

								// TODO Auto-generated method stub
								inRezeptDaten = true;
	                			setCursor(new Cursor(Cursor.WAIT_CURSOR));
	                    		holeEinzelTermine(ix,null);
	    						jpan1.setRezeptDaten((String)tabhistorie.getValueAt(ix, 0),(String)tabhistorie.getValueAt(ix, 6));
	    						//System.out.println("rezeptdaten akutalisieren in ListSelectionHandler");
	    						setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	    						inRezeptDaten = false;
								}catch(Exception ex){
									ex.printStackTrace();
									setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
								}

	    						return null;
							}
	                		
	                	}.execute();

	                    break;
	                }
	            }
	        }
	        //System.out.println(output.toString());
	    }
	}   
	

}
/*************************************/
/*************************************/

class MyHistorieTableModel extends DefaultTableModel{
	   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Class getColumnClass(int columnIndex) {
		   if(columnIndex==1){
			   return JLabel.class;}
		   else{
			   return String.class;
		   }
        //return (columnIndex == 0) ? Boolean.class : String.class;
    }

	    public boolean isCellEditable(int row, int col) {
	        //Note that the data/cell address is constant,
	        //no matter where the cell appears onscreen.

	        if (col == 0){
	        	return true;
	        }else if(col == 3){
	        	return true;
	        }else if(col == 7){
	        	return true;
	        }else if(col == 11){
	        	return true;
	        } else{
	          return false;
	        }
	      }
	   
}

class MyHistorTermTableModel extends DefaultTableModel{
	   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Class getColumnClass(int columnIndex) {
		   if(columnIndex==0){return String.class;}
		   /*else if(columnIndex==1){return JLabel.class;}*/
		   else{return String.class;}
        //return (columnIndex == 0) ? Boolean.class : String.class;
    }

	    public boolean isCellEditable(int row, int col) {
	        //Note that the data/cell address is constant,
	        //no matter where the cell appears onscreen.
	        if (col == 0){
	        	return true;
	        }else if(col == 1){
	        	return true;
	        }else if(col == 2){
	        	return true;
	        }else if(col == 11){
	        	return true;
	        } else{
	          return false;
	        }
	      }
}
