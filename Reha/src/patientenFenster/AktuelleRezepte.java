package patientenFenster;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import jxTableTools.DateTableCellEditor;
import jxTableTools.MyTableStringDatePicker;
import jxTableTools.TableTool;
import krankenKasse.KassenFormulare;
import oOorgTools.OOTools;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.event.TableColumnModelExtListener;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.IconValues;
import org.jdesktop.swingx.renderer.MappedValue;
import org.jdesktop.swingx.renderer.StringValues;

import sqlTools.ExUndHop;
import sqlTools.SqlInfo;
import stammDatenTools.RezTools;
import stammDatenTools.ZuzahlTools;
import systemEinstellungen.INIFile;
import systemEinstellungen.SystemConfig;
import systemTools.Colors;
import systemTools.JCompTools;
import systemTools.JRtaTextField;
import systemTools.ListenerTools;
import systemTools.StringTools;
import terminKalender.DatFunk;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;


public class AktuelleRezepte  extends JXPanel implements ListSelectionListener,TableModelListener,TableColumnModelExtListener,PropertyChangeListener, ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5440388431022834348L;
	//public AktuelleRezepte aktRez = null;
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
	public JButton[] aktrbut = {null,null,null,null,null,null,null,null,null};
	public boolean suchePatUeberRez = false;
	public String rezAngezeigt = "";
	public static boolean inRezeptDaten = false;
	public static boolean inEinzelTermine = false;
	public static boolean initOk = false;
	public JLabel dummyLabel = null;
	private JRtaTextField formularid = new JRtaTextField("NIX",false);	
	Vector<String> titel = new Vector<String>() ;
	Vector<String> formular = new Vector<String>();
	int iformular = -1;

	//public boolean lneu = false;
	public AktuelleRezepte(PatGrundPanel eltern){
		super();
		//aktRez = this;

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
		final PatGrundPanel xeltern = eltern; 
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

				jpan1 = new RezeptDaten(xeltern);
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
		new Thread(){
			public void run(){
				SwingUtilities.invokeLater(new Runnable(){
				 	   public  void run(){
				 		   holeFormulare();
				 		   return;
				 	   }
				}); 	  
			}
		}.start();
		
		
	}
	public void getAktDates(){
		
	}
	public void formulareAuswerten(){
		int row = tabaktrez.getSelectedRow(); 
		if(row >= 0){
    		iformular = -1;
    		KassenFormulare kf = new KassenFormulare(Reha.thisFrame,titel,formularid);
    		Point pt = aktrbut[8].getLocationOnScreen();
    		kf.setLocation(pt.x-100,pt.y+32);
    		kf.setModal(true);
    		kf.setVisible(true);
    		iformular = new Integer(formularid.getText());
    		kf = null;
    		if(iformular >= 0){
    			new SwingWorker<Void,Void>(){

					@Override
					protected Void doInBackground() throws Exception {
						OOTools.starteStandardFormular(Reha.proghome+"vorlagen/"+Reha.aktIK+"/"+formular.get(iformular),null);
						return null;
					}
    			}.execute();
    			
    		}
 
    		//System.out.println("Es wurde Formular "+iformular+" gewählt");
        	
		}else{
			String mes = "Wenn man eine Kasse anschreiben möchte, empfiehlt es sich\n"+ 
			"vorher die Kasse auszuwählen die man anschreiben möchte!!!\n\n"+
			"Aber trösten Sie sich, unser Herrgott hat ein Herz für eine ganz spezielle Randgruppe.\n"+
			"Sie dürfen also hoffen....\n\n";
			JOptionPane.showMessageDialog(null, mes);
			iformular = -1;
		}
		
	}
	

	public void setzeRezeptPanelAufNull(boolean aufnull){
		if(aufnull){
			if(aktPanel.equals("vollPanel")){
				wechselPanel.remove(vollPanel);
				wechselPanel.add(leerPanel);
				aktPanel = "leerPanel";
				aktrbut[0].setEnabled(true);
				for(int i = 1; i < 9;i++){
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
				for(int i = 0; i < 9;i++){
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
		//CellConstraints cc = new CellConstraints();
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
		jtb.addSeparator(new Dimension(30,0));
		aktrbut[4] = new JButton();
		aktrbut[4].setIcon(SystemConfig.hmSysIcons.get("rezeptgebuehr"));
		aktrbut[4].setToolTipText("Rezeptgebühren kassieren");
		aktrbut[4].setActionCommand("rezeptgebuehr");
		aktrbut[4].addActionListener(this);		
		jtb.add(aktrbut[4]);
		aktrbut[3] = new JButton();
		aktrbut[3].setIcon(SystemConfig.hmSysIcons.get("barcode"));
		aktrbut[3].setToolTipText("Rezept mit Barcode bedrucken");
		aktrbut[3].setActionCommand("barcode");
		aktrbut[3].addActionListener(this);
		jtb.add(aktrbut[3]);		
		jtb.addSeparator(new Dimension(30,0));
		aktrbut[5] = new JButton();
		aktrbut[5].setIcon(SystemConfig.hmSysIcons.get("ausfallrechnung"));
		aktrbut[5].setToolTipText("Ausfallrechnung erstellen");
		aktrbut[5].setActionCommand("ausfallrechnung");
		aktrbut[5].addActionListener(this);		
		jtb.add(aktrbut[5]);
		aktrbut[6] = new JButton();
		aktrbut[6].setIcon(SystemConfig.hmSysIcons.get("statusset"));
		//jbut.setIcon(new ImageIcon(Reha.proghome+"icons/privatrechnung.png"));
		aktrbut[6].setToolTipText("Rezept abschließen");
		aktrbut[6].setActionCommand("rezeptabschliessen");
		aktrbut[6].addActionListener(this);		
		jtb.add(aktrbut[6]);
		jtb.addSeparator(new Dimension(30,0));
		aktrbut[7] = new JButton();
		aktrbut[7].setIcon(SystemConfig.hmSysIcons.get("arztbericht"));
		aktrbut[7].setToolTipText("Arztbericht erstellen/ändern");
		aktrbut[7].setActionCommand("arztbericht");
		aktrbut[7].addActionListener(this);		
		jtb.add(aktrbut[7]);
		aktrbut[8] = new JButton();
		aktrbut[8].setIcon(SystemConfig.hmSysIcons.get("print"));
		aktrbut[8].setToolTipText("Rezeptbezogenen Brief/Formular erstellen");
		aktrbut[8].setActionCommand("rezeptbrief");
		aktrbut[8].addActionListener(this);		
		jtb.add(aktrbut[8]);

		for(int i = 0; i < 9;i++){
			aktrbut[i].setEnabled(false);
		}
		return jtb;
	}
	public JXPanel getTabelle(){
		JXPanel dummypan = new JXPanel(new BorderLayout());
		dummypan.setOpaque(false);
		dummypan.setBorder(null);
		dtblm = new MyAktRezeptTableModel();
		String[] column = 	{"Rezept-Nr.","bezahlt","Rez-Datum","angelegt am","spät.Beginn","Status","Pat-Nr.",""};
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
		tabaktrez.getColumn(3).setMaxWidth(75);
		tabaktrez.getColumn(5).setMaxWidth(45);
		tabaktrez.getColumn(5).setCellRenderer(renderer);

		//tabaktrez.getColumn(4).setMaxWidth(70);
		tabaktrez.getColumn(6).setMinWidth(0);
		tabaktrez.getColumn(6).setMaxWidth(0);		
		tabaktrez.getColumn(7).setMinWidth(0);
		tabaktrez.getColumn(7).setMaxWidth(0);		
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
					if(rezGeschlossen()){return;}
					neuanlageRezept(false,"");
				}
				if(arg0.getClickCount()==1 && arg0.getButton()==3){
				   Point point = arg0.getPoint();
				   int row = tabaktrez.rowAtPoint(point);
				   tabaktrez.columnAtPoint(point);
				   tabaktrez.setRowSelectionInterval(row, row);
					//System.out.println("Rechte Maustaste gedrückt auf Tabelle\n"+
						//	"Selektiertes Rezept = "+tabaktrez.getValueAt(row, 0));
					ZeigePopupMenu(arg0);
					
				}
			}
		});
		tabaktrez.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode()==10){
					arg0.consume();
					if(rezGeschlossen()){return;}
					neuanlageRezept(false,"");
				}
				if(arg0.getKeyCode()==27){
					arg0.consume();
				}
				if(arg0.getKeyCode()==65 && arg0.isControlDown()){
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
	private void ZeigePopupMenu(java.awt.event.MouseEvent me){
		JPopupMenu jPop = getTerminPopupMenu();
		
		jPop.show( me.getComponent(), me.getX(), me.getY() ); 
	}
	private JPopupMenu getTerminPopupMenu(){
		JPopupMenu jPopupMenu = new JPopupMenu();
		JMenuItem item = new JMenuItem("Zuzahlungsstatus auf befreit setzen");
		item.setActionCommand("statusfrei");
		item.addActionListener(this);
		jPopupMenu.add(item);
		item = new JMenuItem("auf bereits bezahlt setzen");
		item.setActionCommand("statusbezahlt");
		item.addActionListener(this);
		jPopupMenu.add(item);
		item = new JMenuItem("auf nicht befreit und nicht bezahlt");
		item.setActionCommand("statusnichtbezahlt");
		item.addActionListener(this);
		jPopupMenu.add(item);

		return jPopupMenu;
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
		tabaktterm = new JXTable(dtermm){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public boolean editCellAt(int row, int column, EventObject e) {
				//System.out.println("edit! in Zeile: "+row+" Spalte: "+column);
				//System.out.println("Event = "+e);
				if (e == null) {

					//System.out.println("edit! in Zeile: "+row+" Spalte: "+column);
				}
				if (e instanceof MouseEvent) {
					MouseEvent mouseEvent = (MouseEvent) e;
					if (mouseEvent.getClickCount() > 1) {
						//System.out.println("edit!");
					}
				}

				return super.editCellAt(row, column, e);
			}
		};
		//abaktterm.setSurrendersFocusOnKeystroke(false);
		//tabaktterm.setVerifyInputWhenFocusTarget(true);

		tabaktterm.setHighlighters(HighlighterFactory.createSimpleStriping(Colors.PiOrange.alpha(0.25f)));
		tabaktterm.setDoubleBuffered(true);
		tabaktterm.addPropertyChangeListener(this);
		tabaktterm.setEditable(true);
		tabaktterm.setSortable(false);
		//SortOrder setSort = SortOrder.ASCENDING;
		//tabaktterm.setSortOrder(4,(SortOrder) setSort);
		tabaktterm.setSelectionMode(0);
		tabaktterm.setHorizontalScrollEnabled(true);

		//tbl = new DateTableCellEditor();
		//tabaktterm.getColumnModel().getColumn(0).setCellEditor(tbl);
		//tabaktterm.getColumnModel().getColumn(0).setCellEditor(tbl);

		MyTableStringDatePicker pic = new MyTableStringDatePicker();
		tabaktterm.getColumnModel().getColumn(0).setCellEditor(pic);
		
		//tabaktterm.getColumn(0).setCellEditor(new DatumTableCellEditor(new JFormattedTextField()));
		//tabaktterm.getColumnModel().getColumn(0).setCellEditor(new DatumTableCellEditor(new JFormattedTextField()));
		//tabaktterm.getColumn(0).setCellEditor(new MyDateEditor(new SimpleDateFormat("dd.mm.yyyyy")));
		tabaktterm.setAutoStartEditOnKeyStroke(true);
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
				}else if(arg0.getKeyCode()==27){
				
					//System.out.println("cancel in tabelle");
					tbl.cancelCellEditing();
				}else{/*
					   int row = tabaktterm.getSelectedRow();
					   int col = tabaktterm.getSelectedColumn();
					    boolean success = tabaktterm.editCellAt(row, col);
					    if (success) {
					        // Select cell
					        boolean toggle = false;
					        boolean extend = false;
					        tabaktterm.changeSelection(row, col, toggle, extend);
					    } else {
					        // Cell could not be edited
					    }
					    */
					
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
		
		String ret = titel+" - "+Integer.toString(anz);
		
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
			@SuppressWarnings("unchecked")
			@Override
			protected Void doInBackground() throws Exception {
		
				//String sstmt = "select * from verordn where PAT_INTERN ='"+xpatint+"' ORDER BY REZ_DATUM";
				Vector<Vector<String>> vec = SqlInfo.holeSaetze("verordn", "rez_nr,zzstatus,DATE_FORMAT(rez_datum,'%d.%m.%Y') AS drez_datum,DATE_FORMAT(datum,'%d.%m.%Y') AS datum," +
						"DATE_FORMAT(lastdate,'%d.%m.%Y') AS datum,abschluss,pat_intern,id", 
						"pat_intern='"+xpatint+"' ORDER BY rez_datum", Arrays.asList(new String[]{}));
				int anz = vec.size();
				
				for(int i = 0; i < anz;i++){
					if(i==0){
						dtblm.setRowCount(0);						
					}

					int zzbild = 0;
					int rezstatus = 0;
					if( ((Vector)vec.get(i)).get(1) == null){
						zzbild = 0;
					}else if(!((Vector)vec.get(i)).get(1).equals("")){
						zzbild = new Integer((String) ((Vector)vec.get(i)).get(1) );
					}
					if(((Vector)vec.get(i)).get(5).equals("T")){
						rezstatus = 1;
					}
					//((Vector)vec.get(i)).set(3, PatGrundPanel.thisClass.imgzuzahl[zzbild]);
					
					//System.out.println("Inhalt von zzstatus ="+zzbild);
					dtblm.addRow((Vector)vec.get(i));
					
					dtblm.setValueAt(Reha.thisClass.patpanel.imgzuzahl[zzbild], i, 1);
					dtblm.setValueAt(Reha.thisClass.patpanel.imgrezstatus[rezstatus],i,5);
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
				Reha.thisClass.patpanel.jtab.setTitleAt(0,macheHtmlTitel(anz,"aktuelle Rezepte"));
				if(anz > 0){
					setzeRezeptPanelAufNull(false);
					//int anzeigen = -1;
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
						jpan1.setRezeptDaten((String)tabaktrez.getValueAt(row, 0),(String)tabaktrez.getValueAt(row, 7));
						tabaktrez.scrollRowToVisible(row);
						holeEinzelTermine(row,null);
						//System.out.println("rezeptdaten akutalisieren in holeRezepte 1");
					}else{
						rezneugefunden = true;
						tabaktrez.setRowSelectionInterval(0, 0);
						jpan1.setRezeptDaten((String)tabaktrez.getValueAt(0, 0),(String)tabaktrez.getValueAt(0, 7));
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
					
					Reha.thisClass.patpanel.vecaktrez.clear();
					Reha.thisClass.patpanel.vecaktrez = null;
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
					String id = (String)tabaktrez.getValueAt(xrow,7);
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
		Vector<String> tvec = new Vector<String>();
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
					if(i==0){
						SystemConfig.hmAdrRDaten.put("<Rerstdat>",(terdat[y].trim().equals("") ? "  .  .    " : terdat[y]));						
					}
				}else{
					tvec.add(terdat[y]);					
				}
				//System.out.println("Feld "+y+" = "+terdat[y]);	
			}
			//System.out.println("Termivector = "+tvec);
			dtermm.addRow((Vector)tvec.clone());
		}
		tabaktterm.validate();
		tabaktterm.repaint();
		anzahlTermine.setText("Anzahl Termine: "+lines);
		if(lines > 0){
			tabaktterm.setRowSelectionInterval(lines-1, lines-1);
		}
		SystemConfig.hmAdrRDaten.put("<Rletztdat>",(terdat[0].trim().equals("") ? "  .  .    " : terdat[0]));
	}
	public void setzeBild(int satz,int icon){
		dtblm.setValueAt(Reha.thisClass.patpanel.imgzuzahl[icon],satz,1);
		tabaktrez.validate();
		tabaktrez.repaint();
	}
	private void holeEinzelTermine(int row,Vector<String> vvec){
		inEinzelTermine = true;
		Vector<String> xvec = null;
		if(vvec == null){
			xvec = SqlInfo.holeSatz("verordn", "termine", "id='"+tabaktrez.getValueAt(row,7)+"'", Arrays.asList(new String[] {}));			
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
			SystemConfig.hmAdrRDaten.put("<Rletztdat>","");
			SystemConfig.hmAdrRDaten.put("<Rerstdat>","");
			inEinzelTermine = false;
			return;
		}
		if(terms.equals("")){
			dtermm.setRowCount(0);
			tabaktterm.validate();
			anzahlTermine.setText("Anzahl Termine: 0");
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
					if(i==0){
						SystemConfig.hmAdrRDaten.put("<Rerstdat>",new String((terdat[y].trim().equals("") ? "  .  .    " : terdat[y])));						
					}
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
		SystemConfig.hmAdrRDaten.put("<Rletztdat>",(terdat[0].trim().equals("") ? "  .  .    " : terdat[0]));
		inEinzelTermine = false;
	}

	public void termineSpeichern(){
		int reihen = dtermm.getRowCount();
		StringBuffer sb = new StringBuffer();
		String sdat = "";
		//System.out.println("Speichern Einzeltermine f�r rezept"+tabaktrez.getValueAt(tabaktrez.getSelectedRow(),0) ); 
		for(int i = 0;i<reihen;i++){
			sdat = (dtermm.getValueAt(i,0)!= null ? ((String)dtermm.getValueAt(i,0)).trim() : ".  .");
			//System.out.println("Sdat = "+sdat);
			dtermm.setValueAt((sdat.equals(".  .") ? " " : DatFunk.sDatInSQL(sdat)), i, 4);
			sb.append((sdat.equals(".  .") ?  "  .  .    @" : sdat)+"@");
			sb.append((dtermm.getValueAt(i,1)!= null ? ((String)dtermm.getValueAt(i,1)).trim() : "")+"@");
			sb.append((dtermm.getValueAt(i,2)!= null ? ((String)dtermm.getValueAt(i,2)).trim() : "")+"@");
			sb.append((dtermm.getValueAt(i,3)!= null ? ((String)dtermm.getValueAt(i,3)).trim() : "")+"@");			
			sb.append((dtermm.getValueAt(i,4)!= null ? ((String)dtermm.getValueAt(i,4)).trim() : "")+"\n");
		}

		//String stmt = "update verordn set termine='"+sb.toString()+"' where id='"+(String)tabaktrez.getValueAt(tabaktrez.getSelectedRow(), 6)+"'";
		//System.out.println(sb.toString());
		SqlInfo.aktualisiereSatz("verordn", "termine='"+sb.toString()+"'","id='"+(String)tabaktrez.getValueAt(tabaktrez.getSelectedRow(), 7)+"'");
		//new ExUndHop().setzeStatement(stmt);
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
				final int xcol = col;
				new Thread(){
					public void run(){
						termineSpeichern();
						if(xcol==0){
							//starteTests();
						}

					}
				}.start();


		}

	}
	private void starteTests(){
		new Thread(){
			public void run(){
				System.out.println("Hier den Termintest");
				if(Reha.thisClass.patpanel.vecaktrez.get(60).equals("T")){
					Vector<String>tage = new Vector<String>();
					Vector v = dtermm.getDataVector();
					for(int i = 0; i < v.size();i++){
						tage.add((String) ((Vector)v.get(i)).get(0));
					}
					Object[] ret =  ZuzahlTools.unter18TestDirekt(tage,true,false);
					String resultgleich = "";
					
				}
				if(!Reha.thisClass.patpanel.patDaten.get(69).equals("")){
					ZuzahlTools.jahresWechselTest((String)Reha.thisClass.patpanel.vecaktrez.get(1),true,false);	
				}
			}
		}.start();
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
			if(!RezeptDaten.feddisch){
				return;
			}
	        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
	        
	        int firstIndex = e.getFirstIndex();
	        int lastIndex = e.getLastIndex();
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
	                    		
	    						jpan1.setRezeptDaten((String)tabaktrez.getValueAt(ix, 0),(String)tabaktrez.getValueAt(ix, 7));
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
	public void holeFormulare(){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				// TODO Auto-generated method stub
				INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/rezept.ini");
				int forms = inif.getIntegerProperty("Formulare", "RezeptFormulareAnzahl");
				for(int i = 1; i <= forms; i++){
					titel.add(inif.getStringProperty("Formulare","RFormularText"+i));			
					formular.add(inif.getStringProperty("Formulare","RFormularName"+i));
				}	
				return null;
			}
			
		}.execute();
		
	}

	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
		String cmd = arg0.getActionCommand();
		for(int i = 0; i < 1; i++){
			if(cmd.equals("terminplus")){
				if(rezGeschlossen()){return;}
				Vector<String> vec = new Vector<String>();
				vec.add(DatFunk.sHeute());
				vec.add("");
				vec.add("");
				vec.add( (((String)Reha.thisClass.patpanel.vecaktrez.get(48)).trim().equals("") ? "" : (String)Reha.thisClass.patpanel.vecaktrez.get(48)) +
						(((String)Reha.thisClass.patpanel.vecaktrez.get(49)).trim().equals("") ? "" : ","+(String)Reha.thisClass.patpanel.vecaktrez.get(49)) +
						(((String)Reha.thisClass.patpanel.vecaktrez.get(50)).trim().equals("") ? "" : ","+(String)Reha.thisClass.patpanel.vecaktrez.get(50)) +
						(((String)Reha.thisClass.patpanel.vecaktrez.get(51)).trim().equals("") ? "" : ","+(String)Reha.thisClass.patpanel.vecaktrez.get(51)) 
						);
				vec.add(DatFunk.sDatInSQL(DatFunk.sHeute()));
				dtermm.addRow((Vector<String>)vec.clone());
				tabaktterm.validate();
				anzahlTermine.setText("Anzahl Terimine: "+tabaktterm.getRowCount());
				tabaktterm.setRowSelectionInterval(tabaktterm.getRowCount()-1, tabaktterm.getRowCount()-1);
				
				new Thread(){
					public void run(){
						termineSpeichern();
						starteTests();						
					}
				}.start();
				
				break;
				
			}
			if(cmd.equals("terminminus")){
				if(rezGeschlossen()){return;}
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
							starteTests();
						}
					}.start();
				
				}
				break;
			}
			if(cmd.equals("terminsortieren")){
				if(rezGeschlossen()){return;}
				int row = tabaktterm.getRowCount();
				if(row > 1){
					
					Vector<Vector<String>> vec = (Vector<Vector<String>>)dtermm.getDataVector().clone();

					//System.out.println("Unsortiert = "+vec);
					
					Comparator<Vector<String>> comparator = new Comparator<Vector<String>>() {
						@Override
						public int compare(Vector<String> o1, Vector<String> o2) {
							String s1 = (String)o1.get(4);
							String s2 = (String)o2.get(4);
							return s1.compareTo(s2);
						}
					};
					Collections.sort(vec,comparator);
					dtermm.setRowCount(0);
					//System.out.println("Sortiert = "+vec);
					for(int y = 0;y < vec.size();y++){
						dtermm.addRow(vec.get(y));
					}
					tabaktterm.validate();
					new Thread(){
						public void run(){
							termineSpeichern();
							//starteTests();
						}
					}.start();
				}
				break;
			}
			if(cmd.equals("rezneu")){
				
				if(Reha.thisClass.patpanel.autoPatid <= 0){
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
				if(rezGeschlossen()){return;}
				neuanlageRezept(false,"");
				break;
			}
			if(cmd.equals("rezdelete")){
				if(aktPanel.equals("leerPanel")){
					JOptionPane.showMessageDialog(null,"Oh Herr laß halten...\n\n"+
							"....und welches der nicht vorhandenen Rezepte möchten Sie bitteschön löschen....");
					return;
				}
				if(rezGeschlossen()){return;}
				int currow = tabaktrez.getSelectedRow();
				int anzrow = tabaktrez.getRowCount();
				if(currow == -1){
					JOptionPane.showMessageDialog(null,"Kein Rezept zum -> löschen <- ausgewählt");
					return;
				}
				String reznr = (String)tabaktrez.getValueAt(currow, 0);
				String rezid = (String)tabaktrez.getValueAt(currow, 7);
				int frage = JOptionPane.showConfirmDialog(null,"Wollen Sie das Rezept "+reznr+" wirklich löschen?","Wichtige Benutzeranfrage",JOptionPane.YES_NO_OPTION);
				if(frage == JOptionPane.NO_OPTION){
					return;
				}
				String sqlcmd = "delete from verordn where id='"+rezid+"'";
				new ExUndHop().setzeStatement(sqlcmd);
				currow = TableTool.loescheRow(tabaktrez, new Integer(currow));
				int uebrig = tabaktrez.getRowCount();
				
				anzahlRezepte.setText("Anzahl Rezepte: "+Integer.toString(uebrig));
				Reha.thisClass.patpanel.jtab.setTitleAt(0,macheHtmlTitel(uebrig,"aktuelle Rezepte"));
				if(uebrig <= 0){
					holeRezepte(Reha.thisClass.patpanel.patDaten.get(29),"");
				}else{
				}
				
			}
			if(cmd.equals("rezeptgebuehr")){
				if(aktPanel.equals("leerPanel")){
					JOptionPane.showMessageDialog(null,"Ich sag jetz nix....\n\n"+
							"....außer - und von welchem der nicht vorhandenen Rezepte wollen Sie Rezeptgebühren kassieren....");
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
				// hier  mu� noch getestet werden:
				// 1 ist es eine Neuanlage oder soll ein bestehender Ber. editiert werden
				// 2 ist ein Ber. �berhaupt angefordert
				// 3 gibt es einen Rezeptbezug oder nicht
				if(aktPanel.equals("leerPanel")){
					JOptionPane.showMessageDialog(null,"Ich sag jetz nix....\n\n"+
							"....außer - und für welches der nicht vorhandenen Rezepte wollen Sie einen Therapiebericht erstellen....");
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
				int  iexistiert = Reha.thisClass.patpanel.berichte.berichtExistiert(xreznr);
				if(iexistiert > 0){
					xverfasser = Reha.thisClass.patpanel.berichte.holeVerfasser();
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
			if(cmd.equals("statusfrei")){
				if(rezGeschlossen()){return;}
				int currow = tabaktrez.getSelectedRow();
				String xreznr;
				if(currow >=0){
					xreznr = (String)tabaktrez.getValueAt(currow,0);
					String xcmd = "update verordn set zzstatus='"+0+"', befr='T',rez_bez='F' where rez_nr='"+xreznr+"' LIMIT 1"; 
					new ExUndHop().setzeStatement(xcmd);
					dtblm.setValueAt(Reha.thisClass.patpanel.imgzuzahl[0],currow,1);
					tabaktrez.validate();
				}	
			}
			if(cmd.equals("statusbezahlt")){
				if(rezGeschlossen()){return;}
				int currow = tabaktrez.getSelectedRow();
				String xreznr;
				if(currow >=0){
					xreznr = (String)tabaktrez.getValueAt(currow,0);
					String xcmd = "update verordn set zzstatus='"+1+"', befr='F',rez_bez='T' where rez_nr='"+xreznr+"' LIMIT 1"; 
					new ExUndHop().setzeStatement(xcmd);
					dtblm.setValueAt(Reha.thisClass.patpanel.imgzuzahl[1],currow,1);
					tabaktrez.validate();
				}	
			
			}
			if(cmd.equals("statusnichtbezahlt")){
				if(rezGeschlossen()){return;}
				int currow = tabaktrez.getSelectedRow();
				String xreznr;
				if(currow >=0){
					xreznr = (String)tabaktrez.getValueAt(currow,0);
					String xcmd = "update verordn set zzstatus='"+2+"', befr='F', rez_geb='0.00',rez_bez='F' where rez_nr='"+xreznr+"' LIMIT 1"; 
					new ExUndHop().setzeStatement(xcmd);
					dtblm.setValueAt(Reha.thisClass.patpanel.imgzuzahl[2],currow,1);
					tabaktrez.validate();
				}	
			}
			if(cmd.equals("rezeptbrief")){
				formulareAuswerten();				
			}
			if(cmd.equals("rezeptabschliessen")){
				try{
					if(this.neuDlgOffen){return;}
					doAbschlussTest();	
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
			}
			
		}

	}
	private void doAbschlussTest(){
		int currow = tabaktrez.getSelectedRow();
		if(currow < 0){return;}
			if(dtblm.getValueAt(currow,5)==null){
				// derzeit offen also abschliessen
				int anzterm = dtermm.getRowCount();
				if(anzterm <= 0){return;}
				String vgldat1 = (String) tabaktrez.getValueAt(currow, 2);
				String vgldat2 = (String) dtermm.getValueAt(0,0);
				System.out.println("Tage differenz = "+DatFunk.TageDifferenz(vgldat1, vgldat2));
				//ist Rezeptdatum und Erstdatum > 10 Tage ?
				if(DatFunk.TageDifferenz(vgldat1, vgldat2) > 10){
					int anfrage = JOptionPane.showConfirmDialog(null, "Behandlungsbeginn länger als 10 Tage nach Ausstellung des Rezeptes!!!\nRezept trotzdem abschließen", "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
					//JOptionPane.showMessageDialog(null,"Behandlungsbeginn länger als 10 Tage nach Ausstellung des Rezeptes!!!");
					if(anfrage != JOptionPane.YES_OPTION){
						return;
					}else{
						//Abgeschlossen trotz verspäteter Behandlungsbeginn;
					}
				}
				if(! doTageTest(vgldat2,anzterm)){return;}
				dtblm.setValueAt(Reha.thisClass.patpanel.imgrezstatus[1],currow,5);
				doAbschliessen();
				String xcmd = "update verordn set abschluss='T' where id='"+Reha.thisClass.patpanel.vecaktrez.get(35)+"' LIMIT 1";
				SqlInfo.sqlAusfuehren(xcmd);
				Reha.thisClass.patpanel.vecaktrez.set(62,(String)"T");
				Vector<Vector<String>> kdat = SqlInfo.holeFelder("select ik_kasse,ik_kostent from kass_adr where id='"+
						Reha.thisClass.patpanel.vecaktrez.get(37)+"' LIMIT 1");
				String ikkass="",ikkost="",kname="",rnr="",patint="";
				if(kdat.size()>0){
					ikkass = kdat.get(0).get(0);
					ikkost = kdat.get(0).get(1);
				}else{
					ikkass = "";
					ikkost = "";
				}
				kname = (String) Reha.thisClass.patpanel.vecaktrez.get(36);
				patint=(String) Reha.thisClass.patpanel.vecaktrez.get(0);
				rnr = (String) Reha.thisClass.patpanel.vecaktrez.get(1);
				String cmd = "insert into fertige set ikktraeger='"+ikkost+"', ikkasse='"+ikkass+"', "+
				"name1='"+kname+"', rez_nr='"+rnr+"', pat_intern='"+patint+"', rezklasse='"+rnr.substring(0,2)+"'";
				SqlInfo.sqlAusfuehren(cmd);
				
			}else{
				// bereits abgeschlossen muß geöffnet werden
				dtblm.setValueAt(Reha.thisClass.patpanel.imgrezstatus[0],currow,5);
				doAufschliessen();
				String xcmd = "update verordn set abschluss='F' where id='"+Reha.thisClass.patpanel.vecaktrez.get(35)+"' LIMIT 1";
				Reha.thisClass.patpanel.vecaktrez.set(62,(String)"F");
				SqlInfo.sqlAusfuehren(xcmd);
				//System.out.println(xcmd);
				String rnr = (String) Reha.thisClass.patpanel.vecaktrez.get(1);
				String cmd = "delete from fertige where rez_nr='"+rnr+"' LIMIT 1";
				SqlInfo.sqlAusfuehren(cmd);
			}
	}
	private boolean doTageTest(String starttag,int tage){
		String vglalt;
		String vglneu;
		String kommentar;
		String ret;
		for(int i = 0; i < tage;i++){
			if(i > 0){
				vglalt = (String) dtermm.getValueAt(i-1,0);
				vglneu = (String) dtermm.getValueAt(i,0);
				if(vglalt.equals(vglneu)){
					JOptionPane.showMessageDialog(null,"Zwei identische Behandlungstage sind nicht zulässig - Abschluß des Rezeptes fehlgeschlagen");
					return false; 
				}
				if(DatFunk.TageDifferenz(vglalt, vglneu) < 0 ){
					JOptionPane.showMessageDialog(null,"Bitte sortieren Sie zuerst die Behandlungstage - Abschluß des Rezeptes fehlgeschlagen");
					return false;
				}
				kommentar = (String) dtermm.getValueAt(i,2);
				if(DatFunk.TageDifferenz(vglalt, vglneu) > 10 && kommentar.trim().equals("")){
					ret = rezUnterbrechung(true,"",i+1);// Unterbrechungsgrund
					if(ret.equals("")){
						return false;
					}else{
						dtermm.setValueAt(ret,i,2);
					}
				}
			}
		}
		return true;
	}
	private boolean rezGeschlossen(){
		if(Reha.thisClass.patpanel.vecaktrez.get(62).equals("T")){
			JOptionPane.showMessageDialog(null,"Das Rezept ist bereits abgeschlossen\nÄnderungen sind nur noch durch berechtigte Personen möglich");
			return true;
		}else{
			return false;
		}
	}
	private void doAbschliessen(){
		
	}
	private void doAufschliessen(){
		
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
		    	if(Reha.thisClass.patpanel.vecaktrez.get(62).equals("T")){
		    		return false;
		    	}
		        if (col == 0){
		        	return true;
		        }else if(col == 1){
		        	return true;
		        }else if(col == 2){
		        	return true;
		        }else if(col == 3){
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
			   if(columnIndex==1 || columnIndex==5 ){
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
		// erst pr�fen ob Zuzahlstatus = 0, wenn ja zur�ck;
		// dann pr�fen ob bereits bezahlt wenn ja fragen ob Kopie erstellt werden soll;
		if( (boolean)Reha.thisClass.patpanel.vecaktrez.get(39).equals("0") ){
			JOptionPane.showMessageDialog(null,"Zuzahlung nicht erforderlich!");
			return;
		}
		if(DatFunk.Unter18(DatFunk.sHeute(), DatFunk.sDatInDeutsch(Reha.thisClass.patpanel.patDaten.get(4)))){
			JOptionPane.showMessageDialog(null,"Stand heute ist der Patient noch nicht Volljährig - Zuzahlung deshalb (bislang) noch nicht erforderlich");
			return;
		}

		if( (boolean)Reha.thisClass.patpanel.vecaktrez.get(39).equals("1") || 
				(new Double((String)Reha.thisClass.patpanel.vecaktrez.get(13)) > 0.00) ){
			String reznr = (String)Reha.thisClass.patpanel.vecaktrez.get(1);
			int frage = JOptionPane.showConfirmDialog(null,"Zuzahlung für Rezept "+reznr+" bereits geleistet!\n\n Wollen Sie eine Kopie erstellen?","Wichtige Benutzeranfrage",JOptionPane.YES_NO_OPTION);
			if(frage == JOptionPane.NO_OPTION){
				return;
			}
			bereitsbezahlt = true;
		}

		int art = RezTools.testeRezGebArt(false,(String)Reha.thisClass.patpanel.vecaktrez.get(1),(String)Reha.thisClass.patpanel.vecaktrez.get(34));
		new RezeptGebuehren(bereitsbezahlt,false,pt);
	}
	private void doBarcode(){
		int art = RezTools.testeRezGebArt(false,(String)Reha.thisClass.patpanel.vecaktrez.get(1),(String)Reha.thisClass.patpanel.vecaktrez.get(34));
		String ik = "510884019";
		SystemConfig.hmAdrRDaten.put("<Bcik>",Reha.aktIK);
		SystemConfig.hmAdrRDaten.put("<Bcode>","*"+(String)Reha.thisClass.patpanel.vecaktrez.get(1)+"*");
		int iurl = new Integer((String)Reha.thisClass.patpanel.vecaktrez.get(46));
		String url = SystemConfig.rezBarCodForm.get((iurl < 0 ? 0 : iurl));
		SystemConfig.hmAdrRDaten.put("<Bzu>",StringTools.fuelleMitZeichen(
				SystemConfig.hmAdrRDaten.get("<Rendbetrag>"), " ", true, 5));
		SystemConfig.hmAdrRDaten.put("<Bges>",StringTools.fuelleMitZeichen(
				SystemConfig.hmAdrRDaten.get("<Rwert>"), " ", true, 6));
		SystemConfig.hmAdrRDaten.put("<Bnr>",SystemConfig.hmAdrRDaten.get("<Rnummer>"));
		
		OOTools.starteStandardFormular(Reha.proghome+"vorlagen/"+Reha.aktIK+"/"+url,SystemConfig.rezBarcodeDrucker);
		
	}
	public String rezUnterbrechung(boolean lneu,String feldname,int behandlung){
		if(neuDlgOffen){return "";}
		try{
			neuDlgOffen = true;
			String ret;
			RezTest rezTest = new RezTest();
			PinPanel pinPanel = new PinPanel();
			pinPanel.setName("RezeptTest");
			pinPanel.getGruen().setVisible(false);
			rezTest.setSize(200,200);
			rezTest.setPreferredSize(new Dimension(250,200));
			rezTest.getSmartTitledPanel().setPreferredSize(new Dimension (250,200));
			rezTest.setPinPanel(pinPanel);
			RezTestPanel testPan =  new RezTestPanel((dummyLabel = new JLabel()));
			rezTest.getSmartTitledPanel().setContentContainer(testPan );
			rezTest.getSmartTitledPanel().setTitle("Unterbrechung bei der "+behandlung+". Behandlung");
			rezTest.setName("RezeptTest");
			rezTest.setModal(true);
			Point pt = tabaktterm.getLocationOnScreen();
			pt.x= pt.x-250;
			pt.y= pt.y-15;
			rezTest.setLocation(pt);
			rezTest.pack();
			rezTest.setVisible(true);
			rezTest.dispose();
			System.out.println("Rez unterbrechung geschlossen - Ergebnis = "+dummyLabel.getText());
			ret = new String(dummyLabel.getText());
			testPan.dummylab = null;
			testPan = null;
			rezTest = null;
			neuDlgOffen = false;
			return ret;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return "";
	}
 
	public void neuanlageRezept(boolean lneu,String feldname){
		String zzstatus = "";
		if(Reha.thisClass.patpanel.aid < 0 || Reha.thisClass.patpanel.kid < 0){
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
			zzstatus = ((String)Reha.thisClass.patpanel.vecaktrez.get(39));
			System.out.println("bisheriger Zuzahlungsstatus = "+zzstatus);
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

		neuRez.dispose();
		neuRez = null;
		pinPanel = null;
		if(!lneu){
			if(tabaktrez.getRowCount()>0){
				RezeptDaten.feddisch = false;
				jpan1.setRezeptDaten((String)tabaktrez.getValueAt(tabaktrez.getSelectedRow(), 0),(String)tabaktrez.getValueAt(tabaktrez.getSelectedRow(), 7));
				while(!RezeptDaten.feddisch){
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.out.println("Rezeptdaten fertig geladen = "+RezeptDaten.feddisch);
				if(((String)Reha.thisClass.patpanel.vecaktrez.get(39)).equals("")){
					System.out.println("zuzahlstatus provisorisch gesetzt!!!!!!!!");
					Reha.thisClass.patpanel.vecaktrez.set(39,"0");
				}else{
					System.out.println("Zuzahlungsstatus neu = "+Reha.thisClass.patpanel.vecaktrez.get(39));
				}
				System.out.println("Bild Einstellen -> "+new Integer((String)Reha.thisClass.patpanel.vecaktrez.get(39)) );
				dtblm.setValueAt(Reha.thisClass.patpanel.imgzuzahl[new Integer((String)Reha.thisClass.patpanel.vecaktrez.get(39))], 
									tabaktrez.getSelectedRow(),1);
				tabaktrez.validate();
				tabaktrez.repaint();
			}
		}else{
			if(aktPanel.equals("leerPanel")){
				holeRezepte(Reha.thisClass.patpanel.patDaten.get(29),"");
			}
		}
		   /*
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run(){

					Runtime r = Runtime.getRuntime();
				    r.gc();
				    long freeMem = r.freeMemory();
				    System.out.println("Freier Speicher nach  gc():    " + freeMem);
			
		 	   }
		});
	    */

		//System.out.println("Pat Neu/�ndern ist disposed");
		neuDlgOffen = false;

	}
	public Vector getModelTermine() {
		return (Vector)dtermm.getDataVector().clone();
	}
	

}
class RezNeuDlg extends RehaSmartDialog implements RehaTPEventListener,WindowListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7104716962577408414L;
	private RehaTPEventClass rtp = null;
	public RezNeuDlg(){
		super(null,"RezeptNeuanlage");
		this.setName("RezeptNeuanlage");
		//super.getPinPanel().setName("RezeptNeuanlage");
		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener((RehaTPEventListener) this);

	}
	public void rehaTPEventOccurred(RehaTPEvent evt) {
		// TODO Auto-generated method stub
		try{
			if(evt.getDetails()[0] != null){
				if(evt.getDetails()[0].equals(this.getName())){
					System.out.println("In rezNeuDlg set Visible false***************");
					this.setVisible(false);
					this.dispose();
					rtp.removeRehaTPEventListener((RehaTPEventListener) this);
					rtp = null;
					ListenerTools.removeListeners(this);					
					super.dispose();

					System.out.println("****************Rezept Neu/ändern -> Listener entfernt**************");				
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
			ListenerTools.removeListeners(this);
			super.dispose();
			System.out.println("****************Rezept Neu/ändern -> Listener entfernt (Closed)**********");
		}
	}
	
	
	
}
/************************************/
class RezTest extends RehaSmartDialog implements RehaTPEventListener,WindowListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5410743025474817628L;
	private RehaTPEventClass rtp = null;
	public RezTest(){
		super(null,"RezeptTest");
		this.setName("RezeptTest");
		//super.getPinPanel().setName("RezeptNeuanlage");
		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener((RehaTPEventListener) this);

	}
	public void rehaTPEventOccurred(RehaTPEvent evt) {
		try{
			if(evt.getDetails()[0] != null){
				if(evt.getDetails()[0].equals(this.getName())){
					System.out.println("In rezNeuDlg set Visible false***************");
					this.setVisible(false);
					this.dispose();
					rtp.removeRehaTPEventListener((RehaTPEventListener) this);
					rtp = null;
					ListenerTools.removeListeners(this);					
					super.dispose();
					System.out.println("****************RezeptTest -> Listener entfernt**************");				
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
			ListenerTools.removeListeners(this);
			super.dispose();
			System.out.println("****************RezeptTest -> Listener entfernt (Closed)**********");
		}
	}
}