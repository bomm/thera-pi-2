package patientenFenster;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EventObject;
import java.util.Vector;

import javax.mail.internet.ParseException;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;


import jxTableTools.DateFieldDocument;
import jxTableTools.DateInputVerifier;
import jxTableTools.DateTableCellEditor;
import jxTableTools.DatumTableCellEditor;
import jxTableTools.TableTool;

import oOorgTools.OOTools;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.decorator.SortOrder;
import org.jdesktop.swingx.event.TableColumnModelExtListener;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.IconValues;
import org.jdesktop.swingx.renderer.MappedValue;
import org.jdesktop.swingx.renderer.StringValues;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;


import sqlTools.ExUndHop;
import sqlTools.SqlInfo;
import stammDatenTools.RezTools;
import systemEinstellungen.SystemConfig;
import systemTools.Colors;

import systemTools.DoubleTools;
import systemTools.JCompTools;
import systemTools.JRtaTextField;
import systemTools.StringTools;
import terminKalender.datFunk;


public class AktuelleRezepte  extends JXPanel implements ListSelectionListener,TableModelListener,TableColumnModelExtListener,PropertyChangeListener, ActionListener{
	public static AktuelleRezepte aktRez = null;
	JXPanel leerPanel = null;
	JXPanel vollPanel = null;
	JXPanel wechselPanel = null;
	public JLabel anzahlTermine= null;
	public JLabel anzahlRezepte= null;
	public String aktPanel = "";
	public JXTable tabaktrez = null;
	public JXTable tabaktterm = null;
	public MyAktRezeptTableModel dtblm;
	public MyTermTableModel dtermm;
	public TableCellEditor tbl = null;
	public boolean rezneugefunden = false;
	public boolean neuDlgOffen = false;
	public String[] indphysio = null;
	public String[] indergo = null;
	public String[] indlogo = null;
	public RezeptDaten jpan1 = null;
	public JButton[] aktrbut = {null,null,null,null,null,null,null,null};
	public boolean suchePatUeberRez = false;
	public String rezAngezeigt = "";
	public static boolean inRezeptDaten = false;
	public static boolean inEinzelTermine = false;
	public static boolean initOk = false;
	//public boolean lneu = false;
	public AktuelleRezepte(){
		super();
		aktRez = this;
		setOpaque(false);
		setBorder(null);
		setLayout(new BorderLayout());
		
		leerPanel = new KeinRezept("Keine Rezepte angelegt für diesen Patient");
		leerPanel.setName("leerpanel");
		leerPanel.setOpaque(false);
		
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
		//leerPanel = new KeinRezept();

		wechselPanel.add(leerPanel,BorderLayout.CENTER);

		aktPanel = "leerPanel";

		//wechselPanel.add(getDatenpanel(),BorderLayout.CENTER);
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
				vollPanel = new JXPanel();
				FormLayout vplay = new FormLayout("fill:0:grow(0.60),5dlu,fill:0:grow(0.40),5dlu","13dlu,53dlu,5dlu,fill:0:grow(1.00),0dlu");
				CellConstraints vpcc = new CellConstraints();
				vollPanel.setLayout(vplay);
				vollPanel.setOpaque(false);
				vollPanel.setBorder(null);
				
				Font font = new Font("Tahome",Font.PLAIN,11);
				anzahlRezepte = new JLabel("Anzahl Rezepte: 0");
				anzahlRezepte.setFont(font);
				vollPanel.add(anzahlRezepte,vpcc.xy(1,1));
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

				jpan1 = new RezeptDaten();
				vollPanel.add(jpan1,vpcc.xyw(1,4,1));
				indiSchluessel();
				initOk = true;
				}catch(Exception ex){
					ex.printStackTrace();
					initOk = true;
				}
				return null;
			}
			
		}.execute();
		
	}
	public void getAktDates(){
		
	}

	public void setzeRezeptPanelAufNull(boolean aufnull){
		if(aufnull){
			if(aktPanel.equals("vollPanel")){
				wechselPanel.remove(vollPanel);
				wechselPanel.add(leerPanel);
				aktPanel = "leerPanel";
				aktrbut[0].setEnabled(true);
				for(int i = 1; i < 8;i++){
					aktrbut[i].setEnabled(false);	
				}
				//PatGrundPanel.thisClass.jtab.setIconAt(0, SystemConfig.hmSysIcons.get("zuzahlnichtok"));
			}else{
				aktrbut[0].setEnabled(true);
			}


			
		}else{
			if(aktPanel.equals("leerPanel")){
				wechselPanel.remove(leerPanel);
				wechselPanel.add(vollPanel);
				aktPanel = "vollPanel";
				for(int i = 0; i < 8;i++){
					aktrbut[i].setEnabled(true);	
				}
				//PatGrundPanel.thisClass.jtab.setIconAt(0, SystemConfig.hmSysIcons.get("zuzahlok"));
			}
		}	
	}
	
	public JXPanel getDatenpanel(){
		FormLayout datenlay = new FormLayout("","");
		PanelBuilder builder = new PanelBuilder(datenlay);
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		JXPanel dumm = new JXPanel(new BorderLayout());
		dumm.setOpaque(false);
		dumm.setBorder(null);
		dumm.add(builder.getPanel(),BorderLayout.CENTER);
		return dumm;
	}
	public JToolBar getToolbar(){
		JToolBar jtb = new JToolBar();
		jtb.setOpaque(false);
		jtb.setRollover(true);
		jtb.setBorder(null);
		jtb.setOpaque(false);
		
		aktrbut[0] = new JButton();
		aktrbut[0].setIcon(SystemConfig.hmSysIcons.get("neu"));
		aktrbut[0].setToolTipText("neues Rezept anlegen");
		aktrbut[0].setActionCommand("rezneu");
		aktrbut[0].addActionListener(this);		
		jtb.add(aktrbut[0]);
		aktrbut[1] = new JButton();
		aktrbut[1].setIcon(SystemConfig.hmSysIcons.get("edit"));
		aktrbut[1].setToolTipText("aktuelles Rezept ändern/editieren");
		aktrbut[1].setActionCommand("rezedit");
		aktrbut[1].addActionListener(this);		
		jtb.add(aktrbut[1]);
		aktrbut[2] = new JButton();
		aktrbut[2].setIcon(SystemConfig.hmSysIcons.get("delete"));
		aktrbut[2].setToolTipText("aktuelles Rezept löschen");
		aktrbut[2].setActionCommand("rezdelete");
		aktrbut[2].addActionListener(this);		
		jtb.add(aktrbut[2]);
		jtb.addSeparator(new Dimension(40,0));
		aktrbut[3] = new JButton();
		aktrbut[3].setIcon(SystemConfig.hmSysIcons.get("barcode"));
		aktrbut[3].setToolTipText("Rezept mit Barcode bedrucken");
		aktrbut[3].setActionCommand("barcode");
		aktrbut[3].addActionListener(this);
		jtb.add(aktrbut[3]);		
		aktrbut[4] = new JButton();
		aktrbut[4].setIcon(SystemConfig.hmSysIcons.get("rezeptgebuehr"));
		aktrbut[4].setToolTipText("Rezeptgebühren kassieren");
		aktrbut[4].setActionCommand("rezeptgebuehr");
		aktrbut[4].addActionListener(this);		
		jtb.add(aktrbut[4]);
		aktrbut[5] = new JButton();
		aktrbut[5].setIcon(SystemConfig.hmSysIcons.get("ausfallrechnung"));
		aktrbut[5].setToolTipText("Ausfallrechnung erstellen");
		aktrbut[5].setActionCommand("ausfallrechnung");
		aktrbut[5].addActionListener(this);		
		jtb.add(aktrbut[5]);
		aktrbut[6] = new JButton();
		aktrbut[6].setIcon(SystemConfig.hmSysIcons.get("privatrechnung"));
		//jbut.setIcon(new ImageIcon(Reha.proghome+"icons/privatrechnung.png"));
		aktrbut[6].setToolTipText("Privatrechnung erstellen");
		aktrbut[6].setActionCommand("privatrechnung");
		aktrbut[6].addActionListener(this);		
		jtb.add(aktrbut[6]);
		aktrbut[7] = new JButton();
		aktrbut[7].setIcon(SystemConfig.hmSysIcons.get("arztbericht"));
		aktrbut[7].setToolTipText("Arztbericht erstellen/ändern");
		aktrbut[7].setActionCommand("arztbericht");
		aktrbut[7].addActionListener(this);		
		jtb.add(aktrbut[7]);
		for(int i = 0; i < 8;i++){
			aktrbut[i].setEnabled(false);
		}
		return jtb;
	}
	public JXPanel getTabelle(){
		JXPanel dummypan = new JXPanel(new BorderLayout());
		dummypan.setOpaque(false);
		dummypan.setBorder(null);
		dtblm = new MyAktRezeptTableModel();
		String[] column = 	{"Rezept-Nr.","bezahlt","Rez-Datum","angelegt am","spät.Beginn","Pat-Nr.",""};
		dtblm.setColumnIdentifiers(column);
		tabaktrez = new JXTable(dtblm);
		tabaktrez.setHighlighters(HighlighterFactory.createSimpleStriping(Colors.PiOrange.alpha(0.25f)));
		tabaktrez.setDoubleBuffered(true);
		tabaktrez.setEditable(false);
		tabaktrez.setSortable(false);
		tabaktrez.getColumn(0).setMaxWidth(75);
		TableCellRenderer renderer = new DefaultTableRenderer(new MappedValue(StringValues.EMPTY, IconValues.ICON), JLabel.CENTER);
		tabaktrez.getColumn(1).setCellRenderer(renderer);
		tabaktrez.getColumn(1).setMaxWidth(45);
		tabaktrez.getColumn(2).setMaxWidth(75);
		tabaktrez.getColumn(3).setMaxWidth(75);
		//tabaktrez.getColumn(4).setMaxWidth(70);
		tabaktrez.getColumn(5).setMinWidth(0);
		tabaktrez.getColumn(5).setMaxWidth(0);		
		tabaktrez.getColumn(6).setMinWidth(0);
		tabaktrez.getColumn(6).setMaxWidth(0);		
		tabaktrez.validate();
		tabaktrez.setName("AktRez");
		tabaktrez.setSelectionMode(0);
		//tabaktrez.addPropertyChangeListener(this);
		tabaktrez.getSelectionModel().addListSelectionListener( new RezepteListSelectionHandler());
		tabaktrez.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				if(arg0.getClickCount()==2 && arg0.getButton()==1){
					//while(inRezeptDaten && !RezeptDaten.feddisch){					
					while(!RezeptDaten.feddisch){
						try {
							Thread.sleep(20);
							//System.out.println("in der warteschleife....");
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					neuanlageRezept(false,"");
				}
				if(arg0.getClickCount()==1 && arg0.getButton()==3){
				   Point point = arg0.getPoint();
				   int row = tabaktrez.rowAtPoint(point);
				   int column = tabaktrez.columnAtPoint(point);
				   tabaktrez.setRowSelectionInterval(row, row);
					System.out.println("Rechte Maustaste gedrückt auf Tabelle\n"+
							"Selektiertes Rezept = "+tabaktrez.getValueAt(row, 0));
				}
			}
		});
		tabaktrez.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode()==10){
					arg0.consume();
					neuanlageRezept(false,"");
				}
				if(arg0.getKeyCode()==27){
					arg0.consume();
				}
			}

		});
		//tabaktrez.getSelectionModel().addListSelectionListener(this);
		//dtblm.addTableModelListener(this);
		dummypan.setPreferredSize(new Dimension(0,100));
		JScrollPane aktrezscr = JCompTools.getTransparentScrollPane((Component)tabaktrez); 
		aktrezscr.getVerticalScrollBar().setUnitIncrement(15);
		dummypan.add(aktrezscr,BorderLayout.CENTER);
		dummypan.validate();
		return dummypan;
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
		jtb.add(jbut);
		jbut = new JButton();
		jbut.setIcon(SystemConfig.hmSysIcons.get("delete"));
		//jbut.setIcon(new ImageIcon(Reha.proghome+"icons/list-remove.png"));
		jbut.setToolTipText("Termin löschen");
		jbut.setActionCommand("terminminus");
		jbut.addActionListener(this);		
		jtb.add(jbut);
		jtb.addSeparator(new Dimension(40,0));
		jbut = new JButton();
		jbut.setIcon(SystemConfig.hmSysIcons.get("sort"));
		//jbut.setIcon(new ImageIcon(Reha.proghome+"icons/alphab_sort_22.png"));
		jbut.setActionCommand("terminsortieren");
		jbut.addActionListener(this);		
		jbut.setToolTipText("Termine nach Datum sortieren");		
		jtb.add(jbut);
		return jtb;
	}

	
	public JScrollPane getTermine(){
		
		dtermm = new MyTermTableModel();
		dtermm.addTableModelListener(this);
		String[] column = 	{"Beh.Datum","Behandler","Text","Beh.Art",""};
		dtermm.setColumnIdentifiers(column);
		tabaktterm = new JXTable(dtermm);
		tabaktterm.setHighlighters(HighlighterFactory.createSimpleStriping(Colors.PiOrange.alpha(0.25f)));
		tabaktterm.setDoubleBuffered(true);
		tabaktterm.addPropertyChangeListener(this);
		tabaktterm.setEditable(true);
		tabaktterm.setSortable(false);
		SortOrder setSort = SortOrder.ASCENDING;
		tabaktterm.setSortOrder(4,(SortOrder) setSort);
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
					tbl.cancelCellEditing();
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

	
	private String macheHtmlTitel(int anz,String titel){
		
		String ret = titel+" - "+new Integer(anz).toString();
		
		/*
		String ret = "<html>"+titel+
		(anz > 0 ? " - <font color='#ff0000'>"+new Integer(anz).toString()+"<font></html>" : " - <font color='#000000'>"+new Integer(anz).toString()+"</font>");
		*/
		return ret;
	}
	
	public void holeRezepte(String patint,String rez_nr){
		final String xpatint = patint;
		final String xrez_nr = rez_nr;

		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
		
				//String sstmt = "select * from verordn where PAT_INTERN ='"+xpatint+"' ORDER BY REZ_DATUM";
				Vector vec = SqlInfo.holeSaetze("verordn", "rez_nr,zzstatus,DATE_FORMAT(rez_datum,'%d.%m.%Y') AS drez_datum,DATE_FORMAT(datum,'%d.%m.%Y') AS datum," +
						"DATE_FORMAT(lastdate,'%d.%m.%Y') AS datum,pat_intern,id", 
						"pat_intern='"+xpatint+"' ORDER BY rez_datum", Arrays.asList(new String[]{}));
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
					//((Vector)vec.get(i)).set(3, PatGrundPanel.thisClass.imgzuzahl[zzbild]);
					
					//System.out.println("Inhalt von zzstatus ="+zzbild);
					dtblm.addRow((Vector)vec.get(i));
					
					dtblm.setValueAt(PatGrundPanel.thisClass.imgzuzahl[zzbild], i, 1);
					if(i==0){
						final int ix = i;
	                    new Thread(){
	                    	public void run(){
								if(suchePatUeberRez){
									suchePatUeberRez = false;
									return;
								}else{
		                    		holeEinzelTermine(ix,null);									
								}
	                    	}
	                    }.start();
					}
				}
				PatGrundPanel.thisClass.jtab.setTitleAt(0,macheHtmlTitel(anz,"aktuelle Rezepte"));
				if(anz > 0){
					setzeRezeptPanelAufNull(false);
					int anzeigen = -1;
					if(xrez_nr.length() > 0){
						int row = 0;
						rezneugefunden = true;
						for(int ii = 0; ii < anz;ii++){
							if(tabaktrez.getValueAt(ii,0).equals(xrez_nr)){
								row = ii;
								break;
							}
							
						}
						tabaktrez.setRowSelectionInterval(row, row);
						jpan1.setRezeptDaten((String)tabaktrez.getValueAt(row, 0),(String)tabaktrez.getValueAt(row, 6));
						tabaktrez.scrollRowToVisible(row);
						holeEinzelTermine(row,null);
						//System.out.println("rezeptdaten akutalisieren in holeRezepte 1");
					}else{
						rezneugefunden = true;
						tabaktrez.setRowSelectionInterval(0, 0);
						jpan1.setRezeptDaten((String)tabaktrez.getValueAt(0, 0),(String)tabaktrez.getValueAt(0, 6));
						//System.out.println("rezeptdaten akutalisieren in holeRezepte 1");						
					}
					
					anzahlRezepte.setText("Anzahl Rezepte: "+anz);
					wechselPanel.revalidate();
					wechselPanel.repaint();

				}else{
					setzeRezeptPanelAufNull(true);
					rezAngezeigt = "";
					anzahlRezepte.setText("Anzahl Rezepte: "+anz);
					wechselPanel.revalidate();
					wechselPanel.repaint();
					dtblm.setRowCount(0);
					dtermm.setRowCount(0);
					
					PatGrundPanel.thisClass.vecaktrez.clear();
					PatGrundPanel.thisClass.vecaktrez = null;
				}
				
				return null;
			}
			
		}.execute();
		
	}
	void setRezeptDaten(){
		int row = this.tabaktrez.getSelectedRow();
		if(row >= 0){
			final int xrow = row;
			SwingUtilities.invokeLater(new Runnable(){
				public  void run(){
					String reznr = (String)tabaktrez.getValueAt(xrow,0);
					rezAngezeigt = reznr;
					String id = (String)tabaktrez.getValueAt(xrow,6);
					jpan1.setRezeptDaten(reznr,id);
					System.out.println("Aus Bericht....."+reznr+"....."+id);
				}
			});	

		}
	}
	public void updateEinzelTermine(String einzel){
		String[] tlines = einzel.split("\n");
		int lines = tlines.length;
		//System.out.println("Anzahl Termine = "+lines);
		Vector tvec = new Vector();
		dtermm.setRowCount(0);
		String[] terdat = null;
		for(int i = 0;i<lines;i++){
			terdat = tlines[i].split("@");
			int ieinzel = terdat.length;
			//System.out.println("Anzahl Splits = "+ieinzel);
			tvec.clear();
			for(int y = 0; y < ieinzel;y++){
				if(y==0){
					tvec.add(new String((terdat[y].trim().equals("") ? "  .  .    " : terdat[y])));
					SystemConfig.hmAdrRDaten.put("<Rerstdat>",new String((terdat[y].trim().equals("") ? "  .  .    " : terdat[y])));
				}else{
					tvec.add(new String(terdat[y]));					
				}
				//System.out.println("Feld "+y+" = "+terdat[y]);	
			}
			//System.out.println("Termivector = "+tvec);
			dtermm.addRow((Vector)tvec.clone());
		}
		tabaktterm.validate();
		tabaktterm.repaint();
		anzahlTermine.setText("Anzahl Terimine: "+lines);
		if(lines > 0){
			tabaktterm.setRowSelectionInterval(lines-1, lines-1);
		}
		SystemConfig.hmAdrRDaten.put("<Rletztdat>",new String((terdat[0].trim().equals("") ? "  .  .    " : terdat[0])));
	}
	private void holeEinzelTermine(int row,Vector vvec){
		inEinzelTermine = true;
		Vector xvec = null;
		if(vvec == null){
			xvec = SqlInfo.holeSatz("verordn", "termine", "id='"+tabaktrez.getValueAt(row,6)+"'", Arrays.asList(new String[] {}));			
		}else{
			xvec = vvec;
		}

		String terms = (String) xvec.get(0);
		//System.out.println(terms+" / id der rezeptes = "+tabaktrez.getValueAt(row,4));
		//System.out.println("Inhalt von Termine = *********\n"+terms+"**********");
		if(terms==null){
			dtermm.setRowCount(0);
			tabaktterm.validate();
			anzahlTermine.setText("Anzahl Terimine: 0");
			SystemConfig.hmAdrRDaten.put("<Rletztdat>","");
			SystemConfig.hmAdrRDaten.put("<Rerstdat>","");
			inEinzelTermine = false;
			return;
		}
		if(terms.equals("")){
			dtermm.setRowCount(0);
			tabaktterm.validate();
			anzahlTermine.setText("Anzahl Terimine: 0");
			SystemConfig.hmAdrRDaten.put("<Rletztdat>","");
			SystemConfig.hmAdrRDaten.put("<Rerstdat>","");
			inEinzelTermine = false;
			return;
		}
		String[] tlines = terms.split("\n");
		int lines = tlines.length;
		//System.out.println("Anzahl Termine = "+lines);
		Vector tvec = new Vector();
		dtermm.setRowCount(0);
		String[] terdat = null;
		for(int i = 0;i<lines;i++){
			terdat = tlines[i].split("@");
			int ieinzel = terdat.length;
			//System.out.println("Anzahl Splits = "+ieinzel);
			tvec.clear();
			for(int y = 0; y < ieinzel;y++){
				if(y==0){
					tvec.add(new String((terdat[y].trim().equals("") ? "  .  .    " : terdat[y])));
					SystemConfig.hmAdrRDaten.put("<Rerstdat>",new String((terdat[y].trim().equals("") ? "  .  .    " : terdat[y])));
				}else{
					tvec.add(new String(terdat[y]));					
				}
				//System.out.println("Feld "+y+" = "+terdat[y]);	
			}
			//System.out.println("Termivector = "+tvec);
			dtermm.addRow((Vector)tvec.clone());
		}
		tabaktterm.validate();
		tabaktterm.repaint();
		anzahlTermine.setText("Anzahl Terimine: "+lines);
		if(lines > 0){
			tabaktterm.setRowSelectionInterval(lines-1, lines-1);
		}
		SystemConfig.hmAdrRDaten.put("<Rletztdat>",new String((terdat[0].trim().equals("") ? "  .  .    " : terdat[0])));
		inEinzelTermine = false;
	}

	public void termineSpeichern(){
		int reihen = dtermm.getRowCount();
		StringBuffer sb = new StringBuffer();
		String sdat = "";
		//System.out.println("Speichern Einzeltermine für rezept"+tabaktrez.getValueAt(tabaktrez.getSelectedRow(),0) ); 
		for(int i = 0;i<reihen;i++){
			sdat = (dtermm.getValueAt(i,0)!= null ? ((String)dtermm.getValueAt(i,0)).trim() : ".  .");
			//System.out.println("Sdat = "+sdat);
			dtermm.setValueAt((sdat.equals(".  .") ? " " : datFunk.sDatInSQL(sdat)), i, 4);
			sb.append((sdat.equals(".  .") ?  "  .  .    @" : sdat)+"@");
			sb.append((dtermm.getValueAt(i,1)!= null ? ((String)dtermm.getValueAt(i,1)).trim() : "")+"@");
			sb.append((dtermm.getValueAt(i,2)!= null ? ((String)dtermm.getValueAt(i,2)).trim() : "")+"@");
			sb.append((dtermm.getValueAt(i,3)!= null ? ((String)dtermm.getValueAt(i,3)).trim() : "")+"@");			
			sb.append((dtermm.getValueAt(i,4)!= null ? ((String)dtermm.getValueAt(i,4)).trim() : "")+"\n");
		}

		String stmt = "update verordn set termine='"+sb.toString()+"' where id='"+(String)tabaktrez.getValueAt(tabaktrez.getSelectedRow(), 6)+"'";
		//System.out.println(stmt);
		new ExUndHop().setzeStatement(stmt);
	}
	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub
		//System.out.println("PropertyChange->"+arg0);
		
	}
	@Override
	public void tableChanged(TableModelEvent arg0) {
		// TODO Auto-generated method stub
		boolean update = false;
		int fi = arg0.getFirstRow();
		int fl = arg0.getLastRow();
		int col = arg0.getColumn();
		int type = arg0.getType();
		String stype = "";
		if(type == TableModelEvent.UPDATE){
			stype = "Update";
		}
		if(type == TableModelEvent.INSERT){
			stype = "Insert";
		}
		if(type == TableModelEvent.DELETE){
			stype = "Delete";
		}
		
		if( (col >=  0 && col < 4 && type == TableModelEvent.UPDATE) ){

				new Thread(){
					public void run(){
						termineSpeichern();
					}
				}.start();


		}
	}
	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub
		int index;
		if( (index = arg0.getFirstIndex()) >= 0){
			if(! arg0.getValueIsAdjusting()){
				//System.out.println("ListSelectionEvent->"+arg0.getFirstIndex());				
			}
		}
		
	}
	class RezepteListSelectionHandler implements ListSelectionListener {

	    public void valueChanged(ListSelectionEvent e) {
			if(rezneugefunden){
				rezneugefunden = false;
				return;
			}
	        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
	        
	        int firstIndex = e.getFirstIndex();
	        int lastIndex = e.getLastIndex();
	        boolean isAdjusting = e.getValueIsAdjusting();
	        if(isAdjusting){
	        	return;
	        }
			StringBuffer output = new StringBuffer();
	        if (lsm.isSelectionEmpty()) {

	        } else {
	            int minIndex = lsm.getMinSelectionIndex();
	            int maxIndex = lsm.getMaxSelectionIndex();
	            for (int i = minIndex; i <= maxIndex; i++) {
	                if (lsm.isSelectedIndex(i)) {
	                	RezeptDaten.feddisch = false;
	                	final int ix = i;
	                	new SwingWorker<Void,Void>(){
							@Override
							protected Void doInBackground() throws Exception {
								// TODO Auto-generated method stub
								if(suchePatUeberRez){
									suchePatUeberRez = false;
									return null;
								}
	                			setCursor(new Cursor(Cursor.WAIT_CURSOR));
	                    		holeEinzelTermine(ix,null);
	                    		
	    						jpan1.setRezeptDaten((String)tabaktrez.getValueAt(ix, 0),(String)tabaktrez.getValueAt(ix, 6));
	    						setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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

	public void indiSchluessel(){
		indphysio = new String[] {
				"kein IndiSchl.",
				"AT1 a","AT1 b","AT1 c",
				"AT2 a","AT2 b","AT2 c",
				"AT3 a","AT3 b","AT3 c",
				"CS a","CS b",
				"EX1 a","EX1 b","EX1 c",
				"EX2 a","EX2 b","EX2 c","EX2 d",
				"EX3 a","EX3 b","EX3 c","EX3 d",
				"EX4 a",
				"GE a",
				"LY1 a", "LY1 b","LY2 a","LY3 a", 
				"PN a","PN b","PN c",
				"SO1 a","SO2 a","SO3 a","SO4 a","SO5 a", 
				"WS1 a","WS1 b","WS1 c","WS1 d","WS1 e",
				"WS2 a","WS2 b","WS2 c","WS2 d","WS2 e","WS2 f","WS2 g",
				"ZN1 a","ZN1 b","ZN1 c",
				"ZN2 a","ZN2 b","ZN2 c",
		};
		
		indergo =  new String[] {
				"kein IndiSchl.",
				"EN1","EN2","EN3","EN4",
				"SB1","SB2","SB3","SB4","SB5","SB6","SB7", 
				"PS1","PS2","PS3","PS4","PS5",
		};
		indlogo = new String[] {
				"kein IndiSchl.",
				"RE1","RE2",
				"SC1","SC2",
				"SF",
				"SP1","SP2","SP3","SP4","SP5","SP6",
				"ST1","ST2","ST3","ST4",
		};
		/*
		String[] indischluessel = {
								"WS1 a","WS1 b","WS1 c","WS1 d","WS1 e",
								"WS2 a","WS2 b","WS2 c","WS2 d","WS2 e","WS2 f","WS2 g",
								"EX1 a","EX1 b","EX1 c",
								"EX2 a","EX2 b","EX2 c","EX2 d",
								"EX3 a","EX3 b","EX3 c","EX3 d","EX4 a",
								"CS a","CS b",
								"ZN1 a","ZN1 b","ZN1 c",
								"ZN2 a","ZN2 b","ZN2 c",
								"PN a","PN b","PN c",
								"AT1 a","AT1 b","AT1 c",
								"AT2 a","AT2 b","AT2 c",
								"AT3 a","AT3 b","AT3 c",
								"GE a","LY1 a",													
								"LY1 b","LY2 a","LY3 a",													
								"SO1 a","SO2 a","SO3 a","SO4 a","SO5 a",													
								"SB1","SB2","SB3","SB4","SB5","SB6","SB7",					
								"EN1","EN2","EN3","EN4",
								"PS1","PS2","PS3","PS4","PS5",
								"ST1","ST2","ST3","ST4",
								"SP1","SP2","SP3","SP4","SP5","SP6",
								"RE1","RE2",
								"SF",
								"SC1","SC2"
		};
		*/
	}	

	@Override
	public void columnPropertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub
		//System.out.println("model-listener"+arg0);
	}
	@Override
	public void columnAdded(TableColumnModelEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void columnMarginChanged(ChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void columnMoved(TableColumnModelEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void columnRemoved(TableColumnModelEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void columnSelectionChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	} 
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		String cmd = arg0.getActionCommand();
		for(int i = 0; i < 1; i++){
			if(cmd.equals("terminplus")){
				Vector<String> vec = new Vector<String>();
				vec.add(datFunk.sHeute());
				vec.add("");
				vec.add("");
				vec.add("");
				vec.add(datFunk.sDatInSQL(datFunk.sHeute()));
				dtermm.addRow((Vector<String>)vec.clone());
				tabaktterm.validate();
				anzahlTermine.setText("Anzahl Terimine: "+tabaktterm.getRowCount());
				tabaktterm.setRowSelectionInterval(tabaktterm.getRowCount()-1, tabaktterm.getRowCount()-1);
				
				new Thread(){
					public void run(){
						termineSpeichern();
					}
				}.start();
				
				break;
				
			}
			if(cmd.equals("terminminus")){
				int row = tabaktterm.getSelectedRow();
				if(row>=0){
					dtermm.removeRow(row);
					tabaktterm.validate();
					if(tabaktterm.getRowCount() > 0){
						tabaktterm.setRowSelectionInterval(tabaktterm.getRowCount()-1, tabaktterm.getRowCount()-1);
					}
					anzahlTermine.setText("Anzahl Terimine: "+tabaktterm.getRowCount());
				
					new Thread(){
						public void run(){
							termineSpeichern();
						}
					}.start();
				
				}
				break;
			}
			if(cmd.equals("terminsortieren")){
				int row = tabaktterm.getRowCount();
				if(row > 1){

					Vector vec = dtermm.getDataVector();
					//System.out.println("Größe des Vectors = "+vec.size()+"  / "+vec);
					
					Vector<MyTermClass> vterm = new Vector<MyTermClass>();
					int lang = vec.size();
					for(int y = 0;y < lang;y++){
						vterm.add(new MyTermClass((String) ((Vector)vec.get(y)).get(0),
								(String)((Vector)vec.get(y)).get(1),(String)((Vector)vec.get(y)).get(2),
								(String)((Vector)vec.get(y)).get(3),(String)((Vector)vec.get(y)).get(4)) );
					}
					Collections.sort(vterm, new Comparator<MyTermClass>() {
						   public int compare(MyTermClass o1, MyTermClass o2){
						      return o1.getQDatum().compareTo(o2.getQDatum());
						   }
						});
					dtermm.setRowCount(0);
					Vector<String> sortvec = new Vector<String>();
					for(int y = 0;y < lang;y++){
						sortvec.clear();
						sortvec.add(vterm.get(y).getDDatum());
						sortvec.add(vterm.get(y).getBehandler());
						sortvec.add(vterm.get(y).getStext());						
						sortvec.add(vterm.get(y).getSArt());						
						sortvec.add(vterm.get(y).getQDatum());
						dtermm.addRow((Vector<String>)sortvec.clone());
					}
					new Thread(){
						public void run(){
							termineSpeichern();
						}
					}.start();
				}
				break;
			}
			if(cmd.equals("rezneu")){
				
				if(PatGrundPanel.thisClass.autoPatid <= 0){
					JOptionPane.showMessageDialog(null,"Oh Herr laß halten...\n\n"+
							"....und für welchen Patient wollen Sie ein neues Rezept anlegen....");
					return;
				}
				neuanlageRezept(true,"");
				break;
			}
			if(cmd.equals("rezedit")){
				if(aktPanel.equals("leerPanel")){
					JOptionPane.showMessageDialog(null,"Oh Herr laß halten...\n\n"+
							"....und welches der nicht vorhandenen Rezepte möchten Sie bitteschön ändern....");
					return;
				}
				neuanlageRezept(false,"");
				break;
			}
			if(cmd.equals("rezdelete")){
				if(aktPanel.equals("leerPanel")){
					JOptionPane.showMessageDialog(null,"Oh Herr laß halten...\n\n"+
							"....und welches der nicht vorhandenen Rezepte möchten Sie bitteschön löschen....");
					return;
				}
				int currow = tabaktrez.getSelectedRow();
				int anzrow = tabaktrez.getRowCount();
				if(currow == -1){
					JOptionPane.showMessageDialog(null,"Kein Rezept zum -> löschen <- ausgewählt");
					return;
				}
				String reznr = (String)tabaktrez.getValueAt(currow, 0);
				String rezid = (String)tabaktrez.getValueAt(currow, 6);
				int frage = JOptionPane.showConfirmDialog(null,"Wollen Sie das Rezept "+reznr+" wirklich löschen?","Wichtige Benutzeranfrage",JOptionPane.YES_NO_OPTION);
				if(frage == JOptionPane.NO_OPTION){
					return;
				}
				String sqlcmd = "delete from verordn where id='"+rezid+"'";
				new ExUndHop().setzeStatement(sqlcmd);
				currow = TableTool.loescheRow(tabaktrez, new Integer(currow));
				int uebrig = tabaktrez.getRowCount();
				
				anzahlRezepte.setText("Anzahl Rezepte: "+new Integer(uebrig).toString());
				PatGrundPanel.thisClass.jtab.setTitleAt(0,macheHtmlTitel(uebrig,"aktuelle Rezepte"));
				if(uebrig <= 0){
					holeRezepte(PatGrundPanel.thisClass.patDaten.get(29),"");
				}else{
				}
				
			}
			if(cmd.equals("rezeptgebuehr")){
				if(aktPanel.equals("leerPanel")){
					JOptionPane.showMessageDialog(null,"D E P P \n\n"+
							"....und von welchem der nicht vorhandenen Rezepte wollen Sie Rezeptgebühren kassieren....");
					return;
				}
				int currow = tabaktrez.getSelectedRow();
				int anzrow = tabaktrez.getRowCount();
				if(currow == -1){
					JOptionPane.showMessageDialog(null,"Kein Rezept zum -> kassieren <- ausgewählt");
					return;
				}				
				doRezeptGebuehr( ((JComponent)arg0.getSource()).getLocationOnScreen() );

			}
			if(cmd.equals("barcode")){
				doBarcode();
			}
			
			if(cmd.equals("arztbericht")){
				// hier  muß noch getestet werden:
				// 1 ist es eine Neuanlage oder soll ein bestehender Ber. editiert werden
				// 2 ist ein Ber. überhaupt angefordert
				// 3 gibt es einen Rezeptbezug oder nicht
				if(aktPanel.equals("leerPanel")){
					JOptionPane.showMessageDialog(null,"D E P P \n\n"+
							"....und für welches der nicht vorhandenen Rezepte wollen Sie einen Therapiebericht erstellen....");
					return;
				}


				boolean neuber = true;
				int berid = 0;
				String xreznr;
				String xverfasser = "";
				int currow = tabaktrez.getSelectedRow();
				if(currow >=0){
					xreznr = (String)tabaktrez.getValueAt(currow,0);
				}else{
					xreznr = ""; 
				}
				int  iexistiert = TherapieBerichte.aktBericht.berichtExistiert(xreznr);
				if(iexistiert > 0){
					xverfasser = TherapieBerichte.aktBericht.holeVerfasser();
					neuber = false;
					berid = iexistiert;
					String meldung = "<html>Für das Rezept <b>"+xreznr+"</b> existiert bereits ein Bericht.<br>\nVorhandener Bericht wird jetzt geöffnet";
					JOptionPane.showMessageDialog(null, meldung);
				}
				//System.out.println("ArztberichtFenster erzeugen!");
				final boolean xneuber = neuber;
				final String xxreznr = xreznr;
				final int xberid = berid;
				final int xcurrow = currow;
				final String xxverfasser = xverfasser;
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						ArztBericht ab = new ArztBericht(null,"arztberichterstellen",xneuber,xxreznr,xberid,0,xxverfasser,"",xcurrow);
						ab.setModal(true);
						ab.setLocationRelativeTo(null);
						ab.toFront();
						ab.setVisible(true);
						ab = null;
						return null;
					}
					
				}.execute();
			}
			if(cmd.equals("ausfallrechnung")){
				final JComponent comp = ((JComponent)arg0.getSource());
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						System.out.println("in Ausfallrechnung");
						AusfallRechnung ausfall = new AusfallRechnung( (Point)comp.getLocationOnScreen() );
						ausfall.setModal(true);
						//#ausfall.setLocationRelativeTo(null);
						ausfall.toFront();
						ausfall.setVisible(true);
						ausfall = null;
						return null;
					}
					
				}.execute();
			}
			
		}
	}
	/*****************************************************/
	class MyTermClass{
		   String ddatum;
		   String behandler;
		   String stext;		   
		   String sart;		   
		   String qdatum;		 
		   public MyTermClass(String s1, String s2,String s3,String s4,String s5){
		      ddatum = s1;
		      behandler = s2;
		      stext = s3;
		      sart = s4;
		      qdatum = (s5==null ? " " : s5);
		   }
		 
		   public String getDDatum(){
			      return ddatum;
			   }
		   public String getBehandler(){
			      return behandler;
			   }
		   public String getStext(){
			      return stext;
			   }
		   public String getSArt(){
			      return sart;
			   }
		   public String getQDatum(){
		      return qdatum;
		   }
		}

	class MyTermTableModel extends DefaultTableModel{
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
	class MyAktRezeptTableModel extends DefaultTableModel{
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
	public void doRezeptGebuehr(Point pt){
		boolean bereitsbezahlt = false;
		// noch zu erledigen
		// erst prüfen ob Zuzahlstatus = 0, wenn ja zurück;
		// dann prüfen ob bereits bezahlt wenn ja fragen ob Kopie erstellt werden soll;
		if( (boolean)PatGrundPanel.thisClass.vecaktrez.get(39).equals("0") ){
			JOptionPane.showMessageDialog(null,"Zuzahlung nicht erforderlich!");
			return;
		}
		if( (boolean)PatGrundPanel.thisClass.vecaktrez.get(39).equals("1") ){
			String reznr = (String)PatGrundPanel.thisClass.vecaktrez.get(1);
			int frage = JOptionPane.showConfirmDialog(null,"Zuzahlung für Rezept "+reznr+" bereits geleistet!\n\n Wollen Sie eine Kopie erstellen?","Wichtige Benutzeranfrage",JOptionPane.YES_NO_OPTION);
			if(frage == JOptionPane.NO_OPTION){
				return;
			}
			bereitsbezahlt = true;
		}
		int art = RezTools.testeRezGebArt((String)PatGrundPanel.thisClass.vecaktrez.get(1),(String)PatGrundPanel.thisClass.vecaktrez.get(34));
		new RezeptGebuehren(bereitsbezahlt,false,pt);
	}
	private void doBarcode(){
		int art = RezTools.testeRezGebArt((String)PatGrundPanel.thisClass.vecaktrez.get(1),(String)PatGrundPanel.thisClass.vecaktrez.get(34));
		String ik = "510884019";
		SystemConfig.hmAdrRDaten.put("<Bcik>",Reha.aktIK);
		SystemConfig.hmAdrRDaten.put("<Bcode>","*"+(String)PatGrundPanel.thisClass.vecaktrez.get(1)+"*");
		int iurl = new Integer((String)PatGrundPanel.thisClass.vecaktrez.get(46));
		String url = SystemConfig.rezBarCodForm.get((iurl < 0 ? 0 : iurl));
		SystemConfig.hmAdrRDaten.put("<Bzu>",StringTools.fuelleMitZeichen(
				SystemConfig.hmAdrRDaten.get("<Rendbetrag>"), " ", true, 5));
		SystemConfig.hmAdrRDaten.put("<Bges>",StringTools.fuelleMitZeichen(
				SystemConfig.hmAdrRDaten.get("<Rwert>"), " ", true, 6));
		SystemConfig.hmAdrRDaten.put("<Bnr>",SystemConfig.hmAdrRDaten.get("<Rnummer>"));
		
		OOTools.starteStandardFormular(Reha.proghome+"vorlagen/"+Reha.aktIK+"/"+url,SystemConfig.rezBarcodeDrucker);
		
	}

 
	public void neuanlageRezept(boolean lneu,String feldname){
		if(PatGrundPanel.thisClass.aid < 0 || PatGrundPanel.thisClass.kid < 0){
			String meldung = "Hausarzt und/oder Krankenkasse sind nicht verwertbar.\n"+
			"Die jeweils ungültigen Angaben sind -> kursiv <- dargestellt.\n\n"+
			"Bitte korrigieren Sie die entsprechenden Angaben";
			JOptionPane.showMessageDialog(null, meldung);
			return;	
		}		
		if(neuDlgOffen){
			JOptionPane.showMessageDialog(null, "neuDlgOffen hat den wert true");
			return;
		}
		neuDlgOffen = true;
		RezNeuDlg neuRez = new RezNeuDlg();
		//JDialog neuPat = new JDialog();
		PinPanel pinPanel = new PinPanel();
		pinPanel.setName("RezeptNeuanlage");
		pinPanel.getGruen().setVisible(false);
		//neuRez.setPinPanel(pinPanel);
		if(lneu){
			neuRez.getSmartTitledPanel().setTitle("Rezept Neuanlage");	
		}else{
//			neuRez.getSmartTitledPanel().setTitle("editieren ---> "+ptfield[2].getText().trim()+", "+ptfield[3].getText().trim()+", geboren am: "+ptfield[4].getText().trim());		
		}
		neuRez.setSize(480,768);
		neuRez.setPreferredSize(new Dimension(480,630));
		neuRez.getSmartTitledPanel().setPreferredSize(new Dimension (480,630));
		neuRez.setPinPanel(pinPanel);
		if(lneu){
			neuRez.getSmartTitledPanel().setContentContainer(new RezNeuanlage(new Vector(),lneu,feldname));
			neuRez.getSmartTitledPanel().setTitle("RezeptNeuanlage");
		}else{
			neuRez.getSmartTitledPanel().setContentContainer(new RezNeuanlage(jpan1.vecaktrez,lneu,feldname));
			neuRez.getSmartTitledPanel().setTitle("editieren Rezept ---> "+jpan1.vecaktrez.get(1));
		}
		neuRez.getSmartTitledPanel().getContentContainer().setName("RezeptNeuanlage");
		neuRez.setName("RezeptNeuanlage");
		neuRez.setModal(true);
		neuRez.setLocationRelativeTo(null);
		//neuRez.setTitle("Patienten Neuanlage");
		neuRez.pack();
		neuRez.setVisible(true);

		repaint();
		//neuPat.setVisible(false);

		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run(){
//		 		   setzeFocus();
		 	   }
		}); 	   	
		//neuPat = null;
		neuRez.dispose();
		neuRez = null;
		if(!lneu){
			if(tabaktrez.getRowCount()>0){
				jpan1.setRezeptDaten((String)tabaktrez.getValueAt(tabaktrez.getSelectedRow(), 0),(String)tabaktrez.getValueAt(tabaktrez.getSelectedRow(), 6));
				if(((String)PatGrundPanel.thisClass.vecaktrez.get(39)).equals("")){
					System.out.println("zuzahlstatus provisorisch gesetzt!!!!!!!!");
					PatGrundPanel.thisClass.vecaktrez.set(39,"0");
				}
				System.out.println("Bild Einstellen -> "+new Integer((String)PatGrundPanel.thisClass.vecaktrez.get(39)) );
				dtblm.setValueAt(PatGrundPanel.thisClass.imgzuzahl[new Integer((String)PatGrundPanel.thisClass.vecaktrez.get(39))], 
									tabaktrez.getSelectedRow(),1);
				tabaktrez.validate();
				tabaktrez.repaint();
			}
		}else{
			if(aktPanel.equals("leerPanel")){
				holeRezepte(PatGrundPanel.thisClass.patDaten.get(29),"");
			}
		}

		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run(){
					Runtime r = Runtime.getRuntime();
				    r.gc();
				    long freeMem = r.freeMemory();
				    System.out.println("Freier Speicher nach  gc():    " + freeMem);
		 	   }
		});


		//System.out.println("Pat Neu/Ändern ist disposed");
		neuDlgOffen = false;

	}
	public Vector getModelTermine() {
		return (Vector)dtermm.getDataVector().clone();
	}
	

}
class RezNeuDlg extends RehaSmartDialog implements RehaTPEventListener,WindowListener{
	private RehaTPEventClass rtp = null;
	public RezNeuDlg(){
		super(null,"RezeptNeuanlage");
		this.setName("RezeptNeuanlage");
		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener((RehaTPEventListener) this);

	}
	public void RehaTPEventOccurred(RehaTPEvent evt) {
		// TODO Auto-generated method stub
		try{
			if(evt.getDetails()[0] != null){
				if(evt.getDetails()[0].equals(this.getName())){
					this.setVisible(false);
					rtp.removeRehaTPEventListener((RehaTPEventListener) this);
					rtp = null;
					this.dispose();
					System.out.println("****************Rezept Neu/Ändern -> Listener entfernt**************");				
				}
			}
		}catch(NullPointerException ne){
			System.out.println("In RezeptNeuanlage" +evt);
		}
	}
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		if(rtp != null){
			this.setVisible(false);			
			rtp.removeRehaTPEventListener((RehaTPEventListener) this);		
			rtp = null;
			dispose();
			System.out.println("****************Rezept Neu/Ändern -> Listener entfernt (Closed)**********");
		}
		
		
	}
	
	
}
