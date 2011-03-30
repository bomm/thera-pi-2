package org.therapi.reha.patient;

import hauptFenster.Reha;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
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
import jxTableTools.TableTool;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.IconValues;
import org.jdesktop.swingx.renderer.MappedValue;
import org.jdesktop.swingx.renderer.StringValues;

import patientenFenster.HistorDaten;
import patientenFenster.KeinRezept;
import rechteTools.Rechte;
import sqlTools.SqlInfo;
import stammDatenTools.RezTools;
import systemEinstellungen.SystemConfig;
import systemTools.Colors;
import systemTools.IconListRenderer;
import systemTools.JCompTools;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dialoge.ToolsDialog;

public class Historie extends JXPanel implements ActionListener, TableModelListener, PropertyChangeListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7023226994175632749L;
	JXPanel leerPanel = null;
	public HistorPanel vollPanel = null;
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
					vollPanel = new HistorPanel();
					FormLayout vplay = new FormLayout("fill:0:grow(0.75),5dlu,fill:0:grow(0.25),5dlu","13dlu,53dlu,5dlu,fill:0:grow(1.00),0dlu");
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
					FormLayout dumlay = new FormLayout("fill:0:grow(0.25),p,fill:0:grow(0.25),p,fill:0:grow(0.25),p,fill:0:grow(0.25)",
														"fill:0:grow(1.00),2dlu,p,2dlu");
					CellConstraints dumcc = new CellConstraints();
					dummy.setLayout(dumlay);
					vollPanel.add(dummy,vpcc.xywh(3,2,1,3));
					
					
					dummy.add(getTermine(),dumcc.xyw(1, 1, 7));
					dummy.add(getTerminToolbar(),dumcc.xyw(1, 3, 7));

					jpan1 = new HistorDaten();
					vollPanel.add(jpan1,vpcc.xyw(1,4,1));
				}catch(Exception ex){
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null,"Fehler im Modul Historie");
				}
			
				return null;
			}
			
		}.execute();
		

	}
	class HistorPanel extends JXPanel{
		/**
		 * 
		 */
		private static final long serialVersionUID = -1044284924785143054L;
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

		public void paintComponent( Graphics g ) { 
			super.paintComponent( g );
			@SuppressWarnings("unused")
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
		jbut.setToolTipText("Neuen Termin eintragen");
		jbut.setActionCommand("terminplus");
		jbut.addActionListener(this);
		jbut.setEnabled(false);
		jtb.add(jbut);

		jbut = new JButton();
		jbut.setIcon(SystemConfig.hmSysIcons.get("delete"));
		jbut.setToolTipText("Termin löschen");
		jbut.setActionCommand("terminminus");
		jbut.addActionListener(this);
		jbut.setEnabled(false);		
		jtb.add(jbut);
		
		jtb.addSeparator(new Dimension(40,0));
		jbut = new JButton();
		jbut.setIcon(SystemConfig.hmSysIcons.get("sort"));
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
		tabaktterm.setSelectionMode(0);
		tabaktterm.setHorizontalScrollEnabled(true);
		tbl = new DateTableCellEditor();
		tabaktterm.getColumnModel().getColumn(0).setCellEditor(tbl);
		tabaktterm.getColumn(0).setMinWidth(60);
		tabaktterm.getColumn(1).setMinWidth(60);
		tabaktterm.getColumn(2).setMinWidth(40);
		tabaktterm.getColumn(3).setMinWidth(40);
		tabaktterm.getColumn(4).setMinWidth(0);
		tabaktterm.getColumn(4).setMaxWidth(0);
		tabaktterm.setOpaque(true);
		tabaktterm.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode()==10){
				}
				if(arg0.getKeyCode()==27){
				}
			}
			
		});
		tabaktterm.validate();
		tabaktterm.setName("AktTerm");
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
		tabhistorie.getColumn(5).setMinWidth(0);
		tabhistorie.getColumn(5).setMaxWidth(0);		
		tabhistorie.getColumn(6).setMinWidth(0);
		tabhistorie.getColumn(6).setMaxWidth(0);		
		tabhistorie.validate();
		tabhistorie.setName("AktRez");
		tabhistorie.setSelectionMode(0);
		tabhistorie.getSelectionModel().addListSelectionListener( new HistorRezepteListSelectionHandler());
		tabhistorie.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent arg0) {
				long zeit = System.currentTimeMillis();
				if(arg0.getClickCount()==2){
					while(inRezeptDaten){
						try {
							Thread.sleep(20);
							if((System.currentTimeMillis()-zeit) > 2000){
								return;
							}
							
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		tabhistorie.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode()==10){
					arg0.consume();
				}
				if(arg0.getKeyCode()==27){
					arg0.consume();
				}
			}

		});
		dummypan.setPreferredSize(new Dimension(0,100));
		JScrollPane aktrezscr = JCompTools.getTransparentScrollPane((Component)tabhistorie); 
		aktrezscr.getVerticalScrollBar().setUnitIncrement(15);
		dummypan.add(aktrezscr,BorderLayout.CENTER);
		dummypan.validate();
		return dummypan;
	}
	
	public void setzeHistoriePanelAufNull(boolean aufnull){
		if(aufnull){
			if(aktPanel.equals("vollPanel")){
				wechselPanel.remove(vollPanel);
				wechselPanel.add(leerPanel);
				aktPanel = "leerPanel";
				for(int i = 0; i < 4;i++){
					try{
						histbut[i].setEnabled(false);
					}catch(Exception ex){
						
					}
				}
			}
		}else{
			if(aktPanel.equals("leerPanel")){
				wechselPanel.remove(leerPanel);
				wechselPanel.add(vollPanel);
				aktPanel = "vollPanel";
				for(int i = 0; i < 4;i++){
					try{
						histbut[i].setEnabled(true);
					}catch(Exception ex){
						
					}
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
		
		jtb.addSeparator(new Dimension(30,0));
		
		histbut[1] = new JButton();
		histbut[1].setIcon(SystemConfig.hmSysIcons.get("tools"));
		histbut[1].setToolTipText("Werkzeugkiste für die Historie");
		histbut[1].setActionCommand("werkzeuge");
		histbut[1].addActionListener(this);		
		jtb.add(histbut[1]);
		for(int i = 0; i < 4;i++){
			try{
				histbut[i].setEnabled(false);
			}catch(Exception ex){
				
			}
		}
		return jtb;
	}

	public void macheTabelle(Vector<String> vec){
		if(vec.size()> 0){
			dtblm.addRow(vec);	
		}else{
			dtblm.setRowCount(0);
			tabhistorie.validate();
		}
		
	}
	private void holeEinzelTermine(int row,Vector<?> vvec){
		Vector<?> xvec = null;
		if(vvec == null){
			xvec = SqlInfo.holeSatz("lza", "termine", "id='"+tabhistorie.getValueAt(row,6)+"'", Arrays.asList(new String[] {}));			
		}else{
			xvec = vvec;
		}

		String terms = (String) xvec.get(0);
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
		Vector<String> tvec = new Vector<String>();
		dtermm.setRowCount(0);
		for(int i = 0;i<lines;i++){
			String[] terdat = tlines[i].split("@");
			int ieinzel = terdat.length;
			tvec.clear();
			for(int y = 0; y < ieinzel;y++){
				if(y==0){
					tvec.add((terdat[y].trim().equals("") ? "  .  .    " : terdat[y]));
				}else{
					tvec.add(terdat[y]);					
				}
			}
			dtermm.addRow((Vector<?>)tvec.clone());
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
		int rows = tabhistorie.getRowCount();
		String felder = "anzahl1,anzahl2,anzahl3,anzahl3,preise1,preise2,preise3,preise4";
		Double gesamtumsatz = new Double(0.00); 
		if(db.equals("lza")){
			if(rows <= 0){
				return new Double(0.00);
			}
			for(int i = 0; i < rows;i++){
				String suchrez = (String)tabhistorie.getValueAt(i,6);
				Vector<String> vec = SqlInfo.holeSatz(db, felder, "id='"+suchrez+"'", Arrays.asList(new String[] {}));
				if(vec.size() > 0){
					BigDecimal preispos = BigDecimal.valueOf(new Double(0.00));
					for(int anz = 0;anz <4;anz++){
						preispos = BigDecimal.valueOf(new Double((String)vec.get(anz+4))).multiply( BigDecimal.valueOf(new Double((String)vec.get(anz)))) ;
						gesamtumsatz = gesamtumsatz+preispos.doubleValue();
					}
				}
			}
		}else{
			String cmd = "pat_intern='"+Reha.thisClass.patpanel.aktPatID+"'";
			Vector<Vector<String>> vec = SqlInfo.holeSaetze(db, "id,rez_nr",cmd , Arrays.asList(new String[] {}));
			rows = vec.size();
			for(int i = 0; i < rows;i++){
				String suchrez = (String)((Vector<?>)vec.get(i)).get(0);//(String)tabhistorie.getValueAt(i,6);
				Vector<String> vec2 = SqlInfo.holeSatz(db, felder, "id='"+suchrez+"'", Arrays.asList(new String[] {}));
				if(vec2.size() > 0){
					BigDecimal preispos = BigDecimal.valueOf(new Double(0.00));
					for(int anz = 0;anz <4;anz++){
						preispos = BigDecimal.valueOf(new Double((String)vec2.get(anz+4))).multiply( BigDecimal.valueOf(new Double((String)vec2.get(anz)))) ;
						gesamtumsatz = gesamtumsatz+preispos.doubleValue();
					}
				}
			}
			
		}
		return gesamtumsatz;
	}
	/******************
	 * 
	 * 
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String cmd = arg0.getActionCommand();
		if(cmd.equals("arztbericht")){
			if(!Rechte.hatRecht(Rechte.Historie_thbericht, true)){
				return;
			}
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
			ArztBericht ab = new ArztBericht(null,"arztberichterstellen",xneuber,xxreznr,xberid,1,xxverfasser,"",xcurrow);
			ab.setModal(true);
			ab.setLocationRelativeTo(null);
			ab.setVisible(true);
			ab = null;
			return;
		}else if(cmd.equals("historinfo")){
			return;			
		}else if(cmd.equals("historumsatz")){
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					doRechneAlles();
					return null;
				}
			}.execute();
			return;			
		}else if(cmd.equals("historprinttage")){
			return;			
		}
		if(cmd.equals("werkzeuge")){
			new ToolsDlgHistorie("",histbut[1].getLocationOnScreen());
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
				}
			});	

		}
	}	
	private String macheHtmlTitel(int anz,String titel){
		String ret = titel+" - "+Integer.toString(anz);
		return ret;
	}
	/*************************************************/
	@SuppressWarnings("rawtypes")
	public void holeRezepte(String patint,String rez_nr){
		final String xpatint = patint;
		final String xrez_nr = rez_nr;
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
				zzbild = Integer.valueOf((String) ((Vector)vec.get(i)).get(1) );
			}
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
			setzeHistoriePanelAufNull(false);
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
			}else{
				rezneugefunden = true;
				tabhistorie.setRowSelectionInterval(0, 0);
				jpan1.setRezeptDaten((String)tabhistorie.getValueAt(0, 0),(String)tabhistorie.getValueAt(0, 6));
			}
			anzahlHistorie.setText("Anzahl Rezepte in Historie: "+anz);
			wechselPanel.revalidate();
			wechselPanel.repaint();					
		}else{
			setzeHistoriePanelAufNull(true);
			anzahlHistorie.setText("Anzahl Rezepte in Historie: "+anz);
			wechselPanel.revalidate();
			wechselPanel.repaint();
			dtblm.setRowCount(0);
			dtermm.setRowCount(0);
		}
	}
	private void doUebertrag(){
		int row = tabhistorie.getSelectedRow();
		if(row >= 0){
			try{
			int mod = tabhistorie.convertRowIndexToModel(row);
			String rez_nr = dtblm.getValueAt(mod, 0).toString().trim();
			SqlInfo.transferRowToAnotherDB("lza", "verordn","rez_nr", rez_nr, true, Arrays.asList(new String[] {"id"}));
			String xcmd = "update verordn set abschluss='F' where rez_nr='"+rez_nr+"' LIMIT 1";
			SqlInfo.sqlAusfuehren(xcmd);
			SqlInfo.sqlAusfuehren("delete from lza where rez_nr='"+rez_nr+"'");
			TableTool.loescheRowAusModel(tabhistorie, row);
			String htmlstring = "<html><b><font color='#ff0000'>Achtung!!!!</font><br>"+
			"Wenn Sie das Rezept lediglich zur Ansicht in die aktuelle Rezepte transferieren<br>"+
			"sollten Sie die zugehörigen Fakturadaten <font color='#ff0000'>nicht löschen.</font><br><br>"+
			"Wenn Sie das Rezept jedoch <u>neu abrechnen</u> wollen, sollten Sie<br>"+
			"die Fakturadaten <font color='#ff0000'>unbedingt löschen</font>.<br><br>"+
			"Wollen Sie die Fakturadaten jetzt löschen?</b></html>";
			int anfrage = JOptionPane.showConfirmDialog(null, htmlstring, "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
			if(anfrage == JOptionPane.YES_OPTION){
				SqlInfo.sqlAusfuehren("delete from faktura where rez_nr='"+rez_nr+"'");
				SqlInfo.sqlAusfuehren("delete from fertige where rez_nr='"+rez_nr+"'");
				if(Reha.thisClass.abrechnungpanel != null){
					String[] diszis = {"Physio","Massage","Ergo","Logo","Podo"};
					String aktDisziplin = diszis[Reha.thisClass.abrechnungpanel.cmbDiszi.getSelectedIndex()];
					if(RezTools.putRezNrGetDisziplin(rez_nr).equals(aktDisziplin)){
						Reha.thisClass.abrechnungpanel.einlesenErneuern();
					}
				}
			}
			//new sqlTools.ExUndHop().setzeStatement("delete from faktura where rez_nr='"+rez_nr+"'");
			setzeKarteiLasche();
			Reha.thisClass.patpanel.aktRezept.holeRezepte(Reha.thisClass.patpanel.patDaten.get(29),"");
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}else{
			JOptionPane.showMessageDialog(null, "Kein Historien-Rezept für den Übertrag in aktuelle Rezepte ausgewählt!");
		}
		
	}
	public void setzeKarteiLasche(){
		if(tabhistorie.getRowCount()==0){
			holeRezepte(Reha.thisClass.patpanel.patDaten.get(29),"");
			Reha.thisClass.patpanel.multiTab.setTitleAt(1,macheHtmlTitel(tabhistorie.getRowCount(),"Rezept-Historie"));
		}else{
			Reha.thisClass.patpanel.multiTab.setTitleAt(1,macheHtmlTitel(tabhistorie.getRowCount(),"Rezept-Historie"));			
		}
	}
	
	
	/*************************************************/
	class HistorRezepteListSelectionHandler implements ListSelectionListener {

	    public void valueChanged(ListSelectionEvent e) {
			if(rezneugefunden){
				rezneugefunden = false;
				return;
			}
	        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
	        boolean isAdjusting = e.getValueIsAdjusting();
	        if(isAdjusting){
	        	return;
	        }
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
									inRezeptDaten = true;
		                			setCursor(Reha.thisClass.wartenCursor);
		                    		holeEinzelTermine(ix,null);
		    						jpan1.setRezeptDaten((String)tabhistorie.getValueAt(ix, 0),(String)tabhistorie.getValueAt(ix, 6));
		    						setCursor(Reha.thisClass.normalCursor);
		    						inRezeptDaten = false;
								}catch(Exception ex){
									ex.printStackTrace();
									setCursor(Reha.thisClass.normalCursor);
								}

	    						return null;
							}
	                		
	                	}.execute();

	                    break;
	                }
	            }
	        }
	    }
	}
	
	private void doTageDrucken(){
		int akt = 		this.tabhistorie.getSelectedRow();
		if(akt < 0){
			JOptionPane.showMessageDialog(null, "Kein Historien-Rezept für Übertrag in Clipboard ausgewählt");
			return;
		}
		String stage = "Rezeptnummer: "+tabhistorie.getValueAt(akt,0).toString()+" - Rezeptdatum: "+tabhistorie.getValueAt(akt,2).toString()+"\n";
		int tage = dtermm.getRowCount();
		 
 
		for(int i = 0; i < tage;i++){
			stage = stage + Integer.toString(i+1)+"\t"+dtermm.getValueAt(i, 0).toString()+"\n";
		}
		copyToClipboard(stage);
	}
	public static void copyToClipboard(String s) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(s), null);
    }	
	
	private void do301FallSteuerung(){
		if(!Rechte.hatRecht(Rechte.Sonstiges_Reha301, true)){return;}
		int row = tabhistorie.getSelectedRow();
		if(row < 0){JOptionPane.showMessageDialog(null,"Kein Rezept für Fallsteuerung ausgewählt"); return;}
		Reha.thisClass.progLoader.Dta301Fenster(1, tabhistorie.getValueAt(row, 0).toString());
	}
	private void doRgebKopie(){
		if(!Rechte.hatRecht(Rechte.Rezept_gebuehren, true)){return;}
		int row = tabhistorie.getSelectedRow();
		if(row < 0){JOptionPane.showMessageDialog(null,"Kein Rezept für Rezeptgebühr-Kopie ausgewählt"); return;}
	}
	
	class ToolsDlgHistorie{
		public ToolsDlgHistorie(String command,Point pt){

			Map<Object, ImageIcon> icons = new HashMap<Object, ImageIcon>();
			icons.put("Gesamtumsatz dieses Patienten",SystemConfig.hmSysIcons.get("euro"));
			icons.put("Behandlungstage in Clipboard",SystemConfig.hmSysIcons.get("einzeltage"));
			icons.put("Transfer in aktuelle Rezepte",SystemConfig.hmSysIcons.get("undo"));
			icons.put("Rezeptgebührquittung (Kopie)",SystemConfig.hmSysIcons.get("rezeptgebuehr"));
			icons.put("§301 Reha-Fallsteuerung",SystemConfig.hmSysIcons.get("abrdreieins"));
			// create a list with some test data
			JList list = new JList(	new Object[] {"Gesamtumsatz dieses Patienten",
					"Behandlungstage in Clipboard","Transfer in aktuelle Rezepte",
					"Rezeptgebührquittung (Kopie)","§301 Reha-Fallsteuerung"});
			list.setCellRenderer(new IconListRenderer(icons));	
			Reha.toolsDlgRueckgabe = -1;
			ToolsDialog tDlg = new ToolsDialog(Reha.thisFrame,"Werkzeuge: Historie",list);
			tDlg.setPreferredSize(new Dimension(225,200+
					((Boolean)SystemConfig.hmPatientenWerkzeugDlgIni.get("ToolsDlgShowButton")? 25 : 0) ));
			tDlg.setLocation(pt.x-70,pt.y+30);
			tDlg.pack();
			tDlg.setModal(true);
			tDlg.activateListener();
			tDlg.setVisible(true);
			switch(Reha.toolsDlgRueckgabe){
			case 0:
				if(!Rechte.hatRecht(Rechte.Historie_gesamtumsatz, true)){
					return;
				}
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						doRechneAlles();
						return null;
					}
				}.execute();				
				break;
			case 1:
				if(!Rechte.hatRecht(Rechte.Historie_tagedrucken, true)){
					return;
				}
				doTageDrucken();
				break;
			case 2:
				if(!Rechte.hatRecht(Rechte.Sonstiges_rezepttransfer, true)){
					return;
				}
				int anfrage = JOptionPane.showConfirmDialog(null, "Das ausgewählte Rezept wirklich zurück in den aktuellen Rezeptstamm transferieren?", "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
				if(anfrage == JOptionPane.YES_OPTION){
					doUebertrag();					
				}
				break;
			case 3:
				doRgebKopie();
				break;
			case 4:
				do301FallSteuerung();
				break;
				
			}
			tDlg = null;
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

	public Class<?> getColumnClass(int columnIndex) {
		   if(columnIndex==1){
			   return JLabel.class;}
		   else{
			   return String.class;
		   }
    }

	    public boolean isCellEditable(int row, int col) {
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

	public Class<?> getColumnClass(int columnIndex) {
		   if(columnIndex==0){return String.class;}
		   else{return String.class;}
    }

    public boolean isCellEditable(int row, int col) {
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
