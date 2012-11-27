package rehaUrlaub;





import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;


import ag.ion.bion.officelayer.NativeView;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.spreadsheet.ISpreadsheetDocument;
import ag.ion.bion.officelayer.text.ITextDocument;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.sun.star.sheet.XSheetCellCursor;
import com.sun.star.sheet.XSpreadsheets;

import CommonTools.DatFunk;
import CommonTools.DblCellEditor;
import CommonTools.DoubleTableCellRenderer;
import CommonTools.JCompTools;
import CommonTools.JRtaComboBox;
import CommonTools.JRtaTextField;
import CommonTools.MitteRenderer;
import CommonTools.SqlInfo;
import CommonTools.StringTools;

public class RehaUrlaubPanel extends JXPanel implements TableModelListener  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5795688684244627970L;
	
	RehaUrlaubTab elternTab = null;
	JXPanel content = null;
	
	JRtaTextField[] funktfs = {null,null,null,null,null,null};
	JRtaTextField[] tagetfs = {null,null,null,null,null,null,null};
	
	JLabel[] funklabs = {null,null,null};
	JRtaComboBox[] funkcombo = {null,null,null};
	JRtaComboBox[] tagecombo = {null,null,null,null,null,null,null};
	
	JRtaComboBox tagArt = null;
	
	JRtaComboBox usercombo = null;
	
	NativeView nativeView = null;
	IFrame calcFrame = null;
	
	IDocument document  = null;
	XSheetCellCursor cellCursor = null;

	
	JButton[] buts = {null,null,null,null};
	
	JButton calcstart = null;
	
	JPanel ooPan = null;
	
	ISpreadsheetDocument spreadsheetDocument = null;
	
	JLabel status1 = null;
	JLabel status2 = null;
	
	
	KeyListener kl = null;
	ActionListener al = null;
	
	MyOffenePostenTableModel tabmod = null;
	JXTable tab = null;
	
	DecimalFormat dcf = new DecimalFormat("###0.00");
	
	Vector<Vector<String>> feldNamen = null;

	
	Double aktuelleWochenStunden = Double.parseDouble("0.00");
	
	private Vector<String> vectabellen = new Vector<String>();
	private Vector<Vector<String>> vecKalZeile = new Vector<Vector<String>>();
	private StringBuffer tagBuf = new StringBuffer();

	Vector<Vector<String>> kalenderWerte = new Vector<Vector<String>>();
	
	BigDecimal zuschlag = BigDecimal.valueOf(Double.parseDouble("0.00"));
	BigDecimal halberzuschlag = BigDecimal.valueOf(Double.parseDouble("0.00"));
	
	String aktuellerUrlaubFile = "";
	
	String[] tooltips = { "Normaler Arbeitstag ", "voller Urlaubstag ",
			"voller Feiertag ", "voller Krankheitstag ","halber Urlaubstag und halber Feiertag", "halber Urlaubstag und halber normaler Arbeitstag",
			"halber Feiertag und halber normaler Arbeitstag ","halber Krankheitstag und halber normaler Arbeitstag ",
			"Krankheitstag unklar!!!! ","Urlaubstag unklar!!!!"};
	
	String[][] tageart = {{"----","----"},{"UuUu","UuUu"},{"FfFf","FfFf"},{"KkKk","KkKk"},
			{"UuFf","UuFf"},{"Uu--","Uu--"},{"Ff--","Ff--"},
			{"Kk--","Kk--"},{"K???","K???"},{"U???","U???"},{"F???","F???"} };
	
	boolean gestartet = false;

	
	public RehaUrlaubPanel(RehaUrlaubTab eltern){
		super();
		this.elternTab = eltern;
		setLayout(new BorderLayout());
		
		activateActionListener();
		
		add(getContent(),BorderLayout.CENTER);
		validate();
		setVisible(true);
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					while(!RehaUrlaub.DbOk){
						Thread.sleep(20);
					}
					holeTabellen();
					holeKalUser();
					funkcombo[0].setSelectedItem(DatFunk.sHeute().substring(6));
				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}
			
		}.execute();

	}
	private void macheTagArtCombo(){
		/*
		String[][] tageart = { {"Normal","----"},{"Urlaub","UuUu"},{"Feiertag","FfFf"},{"Krank","KkKk"},
				{"1/2Url.1/2Fei.","UuFf"},{"1/2Url.1/2Norm.","UuNn"},{"1/2Fei.1/2Norm.","FfNn"},
				{"1/2Krank 1/2Norm.","KkNn"}};
		*/


		Vector<Vector<String>> vec = new Vector<Vector<String>>();
		Vector<String> dummy = new Vector<String>();
		for(int i = 0; i < tageart.length;i++){
			dummy.clear();
			dummy.add(tageart[i][0]);
			dummy.add(tageart[i][1]);
			vec.add((Vector<String>)dummy.clone());
		}
		tagArt = new JRtaComboBox();
		tagArt.setDataVectorVector(vec, 0, 1);
		tagArt.setRenderer(new MyComboBoxRenderer());
		
	}
	private void holeTabellen(){
		Vector<Vector<String>> dummy = SqlInfo.holeFelder("show tables");
		vectabellen.clear();
		vectabellen.trimToSize();
		for(int i = 0; i < dummy.size();i++){
			vectabellen.add( String.valueOf( dummy.get(i).get(0) ) );
		}
	}
	private void activateActionListener(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("jahrwahl")){
					doJahrHandlen();
					return;
				}
				if(cmd.equals("userwahl")){
					doUserHandlen();
					return;
				}
				if(cmd.equals("wocheermitteln")){
					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() throws Exception {
							RehaUrlaub.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
							doErmitteln();
							RehaUrlaub.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));							
							return null;
						}
					}.execute();

					return;
				}
				if(cmd.equals("calc")){
					if(tabmod.getRowCount()<=0){
						return;
					}
					new RehaUrlaubCalc(getInstance(),
							funkcombo[0].getSelectedItem().toString().trim(),
							usercombo.getSelectedItem().toString().trim());
					return;
				}
			}
		};
	}
	private RehaUrlaubPanel getInstance(){
		return this;
	}
	private void doUserHandlen(){
		try{
			if(funkcombo[0].getSelectedIndex() > 0){
				if(usercombo.getSelectedIndex()>0){
					if(vectabellen.indexOf("urlaub"+funkcombo[0].getSelectedItem().toString().trim()) >= 0){
						doSucheUserDaten(funkcombo[0].getSelectedItem().toString(),
								this.vecKalZeile.get(usercombo.getSelectedIndex()).get(1) );
						aktuelleWochenStunden = Double.parseDouble(usercombo.getValueAt(2).toString().replace(",", "."));
						status1.setText("Std/Wo: "+dcf.format(aktuelleWochenStunden));
						status2.setText("");
 
						try{
							zuschlag = BigDecimal.valueOf(aktuelleWochenStunden); 
							if(zuschlag.doubleValue() > 0.0d){
								zuschlag = zuschlag.divide(BigDecimal.valueOf(Double.parseDouble("5.00")),2,BigDecimal.ROUND_HALF_UP);
								halberzuschlag = zuschlag.divide(BigDecimal.valueOf(Double.parseDouble("2.00")),2,BigDecimal.ROUND_HALF_UP);
							}
						}catch(Exception ex){
							zuschlag = BigDecimal.valueOf(Double.parseDouble("0.00"));							
							halberzuschlag = BigDecimal.valueOf(Double.parseDouble("0.00"));
						}
						
						//System.out.println("Gutschrift für Krankeit = "+zuschlag);
						//System.out.println("Gutschrift für Halber-Krankeit = "+halberzuschlag);
					}else{
						JOptionPane.showMessageDialog(null, "Die Urlaubstabelle für das gewählte Kalenderjahr existiert nicht");
						usercombo.setSelectedIndex(0);
						tabmod.setRowCount(0);
						tab.validate();
						zuschlag = BigDecimal.valueOf(Double.parseDouble("0.00"));
						halberzuschlag = BigDecimal.valueOf(Double.parseDouble("0.00"));
					}
				}else{
					usercombo.setSelectedIndex(0);
					tabmod.setRowCount(0);
					tab.validate();
					zuschlag = BigDecimal.valueOf(Double.parseDouble("0.00"));
					halberzuschlag = BigDecimal.valueOf(Double.parseDouble("0.00"));
				}
			}else{
				if(gestartet){
					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() throws Exception {
							JOptionPane.showMessageDialog(null, "Stellen Sie zuerste das entsprechende Kalenderjahr ein");
							return null;
						}
					}.execute();
				}else{
					gestartet = true;
				}
				usercombo.setSelectedIndex(0);
				tabmod.setRowCount(0);
				tab.validate();
				return;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}	
		
	}
	private void doJahrHandlen(){
		try{
		

		if(funkcombo[0].getSelectedIndex() > 0){
			if(vectabellen.indexOf("urlaub"+funkcombo[0].getSelectedItem().toString().trim()) >= 0){
				doJahrEinstellen();
				tabmod.setRowCount(0);
				tab.validate();
				aktuellerUrlaubFile = "urlaub"+funkcombo[0].getSelectedItem().toString().trim();
			}else{
				int anfrage = JOptionPane.showConfirmDialog(null, "Die Urlaubstabelle für das Kalenderjahr -> "+
						funkcombo[0].getSelectedItem().toString().trim()+" <- existiert nicht!\n\n"+
						"Wollen Sie die Tabelle jetzt anlegen?\n\n"+
						"Es werden für 99 Kalender-User jeweils eine Zeile pro Kalenderwoche angelegt.\n"+
						"Der Vorgang Urlaubstabelle  anlegen wird einige Minuten dauern und darf nicht unterbrochen werden!!!!!",
						"Achtung wichtige Benutzeranfrage",
						JOptionPane.YES_NO_OPTION);
				if(anfrage == JOptionPane.YES_OPTION){
					doJahrAnlegen();
					aktuellerUrlaubFile = "urlaub"+funkcombo[0].getSelectedItem().toString().trim();
				}else{
					tabmod.setRowCount(0);
					tab.validate();
					aktuellerUrlaubFile = "";
				}
			}
		}else{
			tabmod.setRowCount(0);
			tab.validate();
			aktuellerUrlaubFile = "";
		}
		
		}catch(Exception ex){
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null,"Fehler bei der Auswahl des Kalenderjahres");
		}
	}
	private void doJahrEinstellen(){
		
	}
	private JXPanel getContent(){
		String xwerte = "fill:0:grow(1.0)";
		String ywerte = "fill:0:grow(1.0),0dlu,p";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setPreferredSize(new Dimension(dim.width-150,dim.height-150));
		content = new JXPanel();
		content.setLayout(lay);
		
		//ooPan = getNoaPanel();
		//content.add(ooPan,cc.xy(1,1,CellConstraints.FILL,CellConstraints.FILL));
		content.add(getTabPanel(),cc.xy(1,1,CellConstraints.FILL,CellConstraints.FILL));
		content.add(getFunktionsPanel(),cc.xy(1,3,CellConstraints.FILL,CellConstraints.FILL));
		
		content.validate();
		content.setVisible(true);
		return content;
	}
	private JXPanel getFunktionsPanel(){
		//                1     2    3     4     5    6    7     8    9    10    11   12
		String xwerte = "10dlu,60dlu,2dlu,60dlu,5dlu,60dlu,2dlu,60dlu,5dlu,60dlu,2dlu,60dlu";
		
		String ywerte = "10dlu,p,10dlu,p,10dlu";
		Vector<String> jahre = new Vector<String>();
		jahre.add("./.");
		for(int i = 2009; i < 2021; i++){
			jahre.add(Integer.toString(i));
		}
		
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		JXPanel jpan = new JXPanel();
		jpan.setLayout(lay);

		JLabel lab = new JLabel("User");
		jpan.add(lab,cc.xy(2, 2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		usercombo = new JRtaComboBox();
		usercombo.setActionCommand("userwahl");
		usercombo.addActionListener(al);
		jpan.add(usercombo,cc.xy(4, 2));
		
		lab = new JLabel("Kalenderjahr");
		jpan.add(lab,cc.xy(6, 2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		
		funkcombo[0] = new JRtaComboBox();
		funkcombo[0].setDataVector(jahre);
		funkcombo[0].setActionCommand("jahrwahl");
		funkcombo[0].addActionListener(al);
		
		jpan.add(funkcombo[0],cc.xy(8, 2));

		lab = new JLabel("Calc starten");
		jpan.add(lab,cc.xy(10,2));
		calcstart = new JButton("OO-Calc");
		calcstart.setActionCommand("calc");
		calcstart.addActionListener(al);
		jpan.add(calcstart,cc.xy(12,2));
		
		status1 = new JLabel(" ");
		status1.setForeground(Color.BLUE);
		jpan.add(status1,cc.xy(2, 4,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		
		status2 = new JLabel(" ");
		status2.setForeground(Color.RED);
		jpan.add(status2,cc.xy(6, 4,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		jpan.validate();

		return jpan;
	}

	private JXPanel getTabPanel(){
		while(!RehaUrlaub.DbOk){
			
		}
		macheTagArtCombo();
		tabmod = new MyOffenePostenTableModel();
		feldNamen = SqlInfo.holeFelder("describe urlaub");
		//Vector<Vector<String>> felder = SqlInfo.holeFelder("describe faktura");
		//System.out.println(feldNamen);
		//System.out.println(feldNamen.size());
		String[] spalten = new String[feldNamen.size()];
		for(int i= 0; i < feldNamen.size();i++){
			spalten[i] = feldNamen.get(i).get(0);
		}
		tabmod.setColumnIdentifiers(spalten);

		tab = new JXTable(tabmod){
			private static final long serialVersionUID = 1L;
			@Override
			public boolean editCellAt(int row, int column, EventObject e) {
				////System.out.println("edit! in Zeile: "+row+" Spalte: "+column);
				////System.out.println("Event = "+e);
				if (e == null) {
					return false;
					////System.out.println("edit! in Zeile: "+row+" Spalte: "+column);
				}
				if (e instanceof MouseEvent) {
					MouseEvent mouseEvent = (MouseEvent) e;
					if (mouseEvent.getClickCount() > 1) {
						//System.out.println("edit!");
						return super.editCellAt(row, column, e);
					}
				}else if (e instanceof KeyEvent) {
					KeyEvent keyEvent = (KeyEvent) e;
					if (keyEvent.getKeyChar()==10) {
						//System.out.println("edit!");
						return super.editCellAt(row, column, e);
					}
				}

				return false;
				
			}
		};
		
		tab.setHorizontalScrollEnabled(true);
		for(int i = 0; i < feldNamen.size();i++){
			if(feldNamen.get(i).get(1).contains("decimal(")){
				tab.getColumn(i).setCellRenderer(new DoubleTableCellRenderer());
				tab.getColumn(i).setCellEditor(new DblCellEditor());
			}
			if(feldNamen.get(i).get(0).contains("_ART")){
				//tab.getColumn(i).setCellRenderer(new Tools.DoubleTableCellRenderer());
				//tab.getColumn(i).setCellEditor(new Tools.DblCellEditor());
			}

			
		}
		
		tab.getColumn(0).setCellRenderer(new MitteRenderer());
		tab.getColumn(0).setMaxWidth(40);
		tab.getColumn(1).setMinWidth(120);
		
		for(int i = 3;i < 17; i+=2){
			tab.getColumn(i).setCellEditor(new DefaultCellEditor(tagArt));
			tab.getColumn(i).setCellRenderer(new MitteRenderer());
		}
		for(int i = 2;i < 16; i++){
			tab.getColumn(i).setMaxWidth(45);
		}
		tab.getSelectionModel().addListSelectionListener( new BillListSelectionHandler());
		tab.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent arg0) {
				arg0.consume();
				if(arg0.getButton()==1){
					if(arg0.getClickCount()==2){
						tab.editCellAt(tab.getSelectedRow(), tab.getSelectedColumn());
					}
					if(arg0.getClickCount()==1){
						
					}
					return;
				}
				if(arg0.getButton()==3){
					showPopUp(arg0);
					return;
				}
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				return;
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				return;
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				arg0.consume();
				return;
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				arg0.consume();
				return;
			}
			
		});
		
		tab.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.CLASSIC_LINE_PRINTER));
		tab.setSortable(false);
		tabmod.addTableModelListener(this);
		JScrollPane jscr = JCompTools.getTransparentScrollPane(tab);
		jscr.validate();
		String xwerte = "fill:0:grow(1.0)";
		String ywerte = "fill:0:grow(1.0)";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		JXPanel jpan = new JXPanel();
		jpan.setLayout(lay);
		jpan.add(jscr,cc.xy(1,1));
		jpan.validate();
		return jpan;
	}
	
	private void showPopUp(MouseEvent evt){
		JPopupMenu jPop = getErmittelnMenu();
		jPop.show( evt.getComponent(), evt.getX(), evt.getY() ); 
		
	}
	private JPopupMenu getErmittelnMenu(){
		JPopupMenu jPopupMenu = new JPopupMenu();
		JMenuItem item = new JMenuItem("Wochenarbeitszeit berechnen");
		item.setActionCommand("wocheermitteln");
		item.addActionListener(al);
		jPopupMenu.add(item);
		return jPopupMenu;
	}	
	/*****************************************************/
	private JPanel getNoaPanel(){
		JPanel jpan = new JPanel();
		jpan.validate();
		jpan.setVisible(true);
		return jpan;
	}
	/*********
	 * 
	 * 
	 * 
	 * @author admin
	 *
	 */
	class BillListSelectionHandler implements ListSelectionListener {
		
	    public void valueChanged(ListSelectionEvent e) {
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
	                	////System.out.println("Satz "+i);
	                    break;
	                }
	            }
	        }

	    }
	}

	@Override
	public void tableChanged(TableModelEvent arg0) {
		if(arg0.getType() == TableModelEvent.INSERT){
			////System.out.println("Insert");
			return;
		}
		if(arg0.getType() == TableModelEvent.UPDATE){
			try{
				int col = arg0.getColumn();
				int row = arg0.getFirstRow();
				String colname = tabmod.getColumnName(col).toString();
				String value = "";
				String id = Integer.toString((Integer)tabmod.getValueAt(row,tabmod.getColumnCount()-1));

				if( tabmod.getColumnClass(col) == Boolean.class){
					value = (tabmod.getValueAt(row,col) == Boolean.FALSE ? "F" : "T");
					direktSchreiben(row,col,value);
				}else if(tabmod.getColumnClass(col) == Date.class){
					value = tabmod.getValueAt(row,col).toString();
				}else if(tabmod.getColumnClass(col) == Double.class){
					value = dcf.format((Double)tabmod.getValueAt(row,col)).replace(",",".");
				}else if(tabmod.getColumnClass(col) == Integer.class){
					value = Integer.toString((Integer)tabmod.getValueAt(row,col));
				}else if(tabmod.getColumnClass(col) == String.class){
					value = tabmod.getValueAt(row,col).toString();
				}
				//System.out.println("aufruf teste Woche");
				testeWoche(row,col);
			
			}catch(Exception ex){
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null,"Fehler in der Dateneingbe");
			}
			
			
			return;
		}
		if(arg0.getType() == TableModelEvent.DELETE){
		}

	}
	
	private void direktSchreiben(int row,int col,String value){
		String id = Integer.toString((Integer)tabmod.getValueAt(row,26));
		String cmd = "update "+this.aktuellerUrlaubFile+" set berechnet='"+value+"' where id ='"+id+"' LIMIT 1";
		SqlInfo.sqlAusfuehren(cmd);
		
	}
	class MyOffenePostenTableModel extends DefaultTableModel{
		   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Class<?> getColumnClass(int columnIndex) {
			if(feldNamen.get(columnIndex).get(1).contains("varchar(")){
				return String.class;
			}else if(feldNamen.get(columnIndex).get(1).contains("enum(")){
				return Boolean.class;
			}else if(feldNamen.get(columnIndex).get(1).contains("decimal(")){
				return Double.class;
			}else if(feldNamen.get(columnIndex).get(1).contains("tinyint(")){
				return Integer.class;
			}else if(feldNamen.get(columnIndex).get(1).contains("int(")){
				return Integer.class;
			}else if(feldNamen.get(columnIndex).get(1).contains("date")){
				return Date.class;
			}
		   return String.class;
	    }

		public boolean isCellEditable(int row, int col) {
			
			if(feldNamen.get(col).get(5).equalsIgnoreCase("auto_increment")){
				return false;				
			}else if(col >= 2 && col <= 15){
				return true;
			}else if(col >= 16 && col <= 25){
				return true;
			}
			return false;
		}
		   
	}
	public Double getAktArbeitszeit(){
		if(usercombo.getSelectedIndex() > 0){
			return Double.parseDouble(usercombo.getValueAt(2).toString().trim().replace(",", "."));
		}
		return Double.parseDouble("0.00");
	}

	/***************
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	
	
	private void holeKalUser(){
		vecKalZeile = SqlInfo.holeFelder("select matchcode,kalzeile,astunden from kollegen2 order by matchcode");
		Vector<String> dummy = new Vector<String>();
		dummy.add("./.");
		dummy.add("0");
		dummy.add("0");
		vecKalZeile.insertElementAt((Vector<String>)dummy.clone(), 0);
		usercombo.setDataVectorVector(vecKalZeile, 0, 1);
	}
	
	private void doJahrAnlegen(){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {

				boolean ersteWocheIstNull = false;
				String jahr = funkcombo[0].getSelectedItem().toString().trim();
				int ersteWoche = DatFunk.KalenderWoche("01.01."+jahr);
				int letzteWoche = DatFunk.KalenderWoche("31.12."+jahr);
				//System.out.println("erste Woche = "+ersteWoche+" letzte Woche = "+letzteWoche);		

				String ersterTag = "";
				String letzterTag = "";
				String kalenderWoche = "";
				
				RehaUrlaub.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				
				int startWoche = -1;
				if(ersteWoche > 1 ){
					ersteWocheIstNull = true;
					ersteWoche = 0;
					startWoche = 0;
				}else{
					startWoche = ersteWoche;
				}
				if(letzteWoche <= 1 ){
					letzteWoche = 53;
				}
				String cmd = UrlaubFunktionen.getUrlaubTableDef(jahr);
				String urlaubstabelle = "urlaub"+jahr;
				try{
					SqlInfo.sqlAusfuehren(StringTools.EscapedDouble(cmd));
					Thread.sleep(100);
				}catch(Exception ex){
					ex.printStackTrace();
					RehaUrlaub.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(null, "Fehler in Funktion Tabelle für Urlaub anlegen");
					return null;
				}
				/*
				System.out.println("ersteWoche = "+ersteWoche);
				System.out.println("letzteWoche = "+letzteWoche);
				System.out.println("starteWoche = "+startWoche);
				System.out.println("ersteWocheIstNull = "+ersteWocheIstNull);
				int x = 0;
				if(x == 0){
					return null;	
				}
				*/
				try{
					String von_bis = "";
					for(int behandler = 1; behandler < 100; behandler++){
						status1.setText("User:"+Integer.toString(behandler)+"von 99");
						status1.validate();
						((JComponent)status1.getParent()).validate();
						int durchlauf = 0;
						
						String matchcode = 
							(behandler <= elternTab.vecKalUser.size() ? elternTab.vecKalUser.get(behandler-1).get(1) : "unbelegt");
						
						for(int i = startWoche; i <= letzteWoche;i++){
							status2.setText("KW: "+Integer.toString(i));
							status2.validate();
							((JComponent)status2.getParent()).validate();
							Thread.sleep(75);
							if(durchlauf == 0 && ersteWocheIstNull){
								ersterTag = DatFunk.WocheErster("01.01."+jahr);
								letzterTag = DatFunk.WocheLetzter("01.01."+jahr);
								kalenderWoche = Integer.toString(durchlauf);
								von_bis = ersterTag+"-"+letzterTag;
								////System.out.println("in durchlauf == 0 "+ersterTag+" "+letzterTag);
							}else if(durchlauf==0 && startWoche==1 ){
								ersterTag = DatFunk.WocheErster("01.01."+jahr);
								letzterTag = DatFunk.WocheLetzter("01.01."+jahr);
								kalenderWoche = Integer.toString(durchlauf);
								von_bis = ersterTag+"-"+letzterTag;
								////System.out.println("startWoche = 1 in durchlauf == "+i+" / "+ersterTag+" "+letzterTag);
							}else{
								ersterTag = DatFunk.WocheErster( DatFunk.sDatPlusTage("01.01."+jahr, durchlauf*7) );
								letzterTag = DatFunk.WocheLetzter( DatFunk.sDatPlusTage("01.01."+jahr, durchlauf*7) );
								kalenderWoche = Integer.toString(durchlauf);
								von_bis = ersterTag+"-"+letzterTag;
								////System.out.println("in durchlauf == "+i+" / "+ersterTag+" "+letzterTag);
							}
							
							SqlInfo.sqlAusfuehren("insert into "+urlaubstabelle+" set kw='"+kalenderWoche+
									"', von_bis='"+von_bis+"', kal_zeile='"+Integer.toString(behandler)+"', "+
									"kal_benutzer='"+matchcode+"', "+
									"jahr='"+Integer.parseInt(jahr)+"'");
							/*
							if(durchlauf==5){
								break;
							}
							*/
							durchlauf++;
						}
					
					}
					RehaUrlaub.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					holeTabellen();
					JOptionPane.showMessageDialog(null, "Urlaubstabelle für das Jahr "+jahr+" wurde erfolgreich angelegt!");
				}catch(Exception ex){
					ex.printStackTrace();
					RehaUrlaub.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(null, "Fehler in Funktion Tabelle für Urlaub anlegen");
					return null;
				}
				return null;
			}
		}.execute();	
	}
/******************
 * 
 * 
 * 
 */
	private void doSucheUserDaten(String jahr,String kalzeile){
		tabmod.setRowCount(0);
		tab.validate();
		tab.repaint();
		Statement stmt = null;
		ResultSet rs = null;
		String mystmt = "select * from urlaub"+jahr+" where jahr='"+jahr+"' and kal_zeile='"+kalzeile+"' ORDER BY kw LIMIT 55";
		////System.out.println(mystmt);
			
		try {
			stmt = RehaUrlaub.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			
			
			rs = stmt.executeQuery(mystmt);
			Vector<Object> vec = new Vector<Object>();
			int durchlauf = 0;
			int lang = feldNamen.size();

			while(rs.next()){
				vec.clear();
				try{
				for(int i = 0;i<lang;i++){
					if(feldNamen.get(i).get(1).contains("varchar(")){
						vec.add( (rs.getString(i+1)==null ? "" : (rs.getString(i+1).equals("") ? "" :  rs.getString(i+1))  ) );
					}else if(feldNamen.get(i).get(1).contains("enum(")){
						vec.add( (rs.getString(i+1)==null ?  Boolean.FALSE : (rs.getString(i+1).equals("T") ? Boolean.TRUE : Boolean.FALSE)) );
					}else if(feldNamen.get(i).get(1).contains("decimal(")){
						if(rs.getBigDecimal(i+1) == null){
							vec.add(Double.parseDouble("0.00"));
						}else{
							vec.add(rs.getBigDecimal(i+1).doubleValue());	
						}
					}else if(feldNamen.get(i).get(1).contains("tinyint(")){
						vec.add(rs.getInt(i+1));
					}else if(feldNamen.get(i).get(1).contains("int(")){
						vec.add(rs.getInt(i+1));
					}else if(feldNamen.get(i).get(1).contains("date")){
						vec.add(rs.getDate(i+1));
					}else{
						vec.add( (rs.getString(i+1)==null ? "" : rs.getString(i+1)) );
					}
				}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
				tabmod.addRow( (Vector<?>) vec.clone());
				if(durchlauf>200){
					try {
						tab.validate();
						tab.repaint();
						Thread.sleep(100);
						durchlauf = 0;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				durchlauf++;
			}
			
			tab.validate();
			tab.repaint();
			if(tab.getRowCount() > 0){
				tab.setRowSelectionInterval(0, 0);
			}
			
		}catch(SQLException ev){
			//System.out.println("SQLException: " + ev.getMessage());
			//System.out.println("SQLState: " + ev.getSQLState());
			//System.out.println("VendorError: " + ev.getErrorCode());
		}	
		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { // ignore }
					rs = null;
				}
			}	
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { // ignore }
					stmt = null;
				}
			}
		}
		
		
	}
	public BigDecimal ermittleTag(String tag, int kaluser){
		String cmd = "select * from flexkc where datum='"+DatFunk.sDatInSQL(tag)+"' and behandler='"+
		(kaluser < 10 ? "0"+Integer.toString(kaluser)+"BEHANDLER" : Integer.toString(kaluser)+"BEHANDLER")+"' LIMIT 1";
		//System.out.println(cmd);
		BigDecimal bdret = analysiereTag(SqlInfo.holeFelder(cmd));
		return bdret;
		
		
	}
	public String ermittleWoche(String tag, int kaluser){
		String wocheerster = DatFunk.WocheErster(tag);
		String wocheletzter = DatFunk.WocheLetzter(tag);
		String cmd = "select * from flexkc where datum >='"+DatFunk.sDatInSQL(wocheerster)+"' and datum <='"+
		DatFunk.sDatInSQL(wocheletzter)+"' and behandler='"+
		(kaluser < 10 ? "0"+Integer.toString(kaluser)+"BEHANDLER" : Integer.toString(kaluser)+"BEHANDLER")+"' order by datum LIMIT 7";
		//System.out.println(cmd);
		return cmd;
	}
	
	public void ermittleZeitraum(String von, String bis, int kaluser){
		
	}

	private void schreibeWoche(int row){
		int id = (Integer) tabmod.getValueAt(row, 26);
		tagBuf.setLength(0);
		tagBuf.trimToSize();
		tagBuf.append("update "+aktuellerUrlaubFile+" set ");
		for(int i = 2; i < 26 ;i++){
			if(i==2){
				tagBuf.append(feldNamen.get(i).get(0)+"='"+allesInString(row,i)+"' ");
			}else{
				tagBuf.append(","+feldNamen.get(i).get(0)+"='"+allesInString(row,i)+"' ");
			}
		}
		tagBuf.append(" where id='"+Integer.toString(id)+"' LIMIT 1");
		SqlInfo.sqlAusfuehren(tagBuf.toString());
		
	}
	private String allesInString(int row,int col){
		String ret = "";
		if(feldNamen.get(col).get(1).startsWith("decimal(")){
			ret = Double.toString((Double)tabmod.getValueAt(row, col));
		}else if(feldNamen.get(col).get(1).startsWith("varchar(")){
			ret = (String)tabmod.getValueAt(row, col).toString();
		}else if(feldNamen.get(col).get(1).startsWith("enum(")){
			ret = ( (Boolean)tabmod.getValueAt(row, col) ? "T" : "F");
		}else if(feldNamen.get(col).get(1).startsWith("int(")){
			ret = Integer.toString((Integer)tabmod.getValueAt(row, col));
		}
		return ret;
	}
	
	private void doErmitteln(){
		int rowcount = tab.getSelectedRowCount();
		if(rowcount <=0 ){
			return;
		}
		int rows[] = tab.getSelectedRows();
		String erster = "";
		int id = -1;
		tabmod.removeTableModelListener(this);
		for(int i = 0; i < rows.length;i++){
			erster = tab.getValueAt(rows[i], 1).toString().split("-")[0];
			id = (Integer) tabmod.getValueAt(rows[i], tabmod.getColumnCount()-1);
			kalenderWerte.clear();
			kalenderWerte.trimToSize();
			kalenderWerte = SqlInfo.holeFelder(ermittleWoche(erster,Integer.parseInt(usercombo.getValueAt(1).toString())));
			////System.out.println(kalenderWerte.size());
			analysiereWoche(kalenderWerte,id,rows[i]);
		}
		tabmod.addTableModelListener(this);
		tab.setRowSelectionInterval(rows[rows.length-1], rows[rows.length-1]);

	}
	
	/***********************************************************/
	private void analysiereWoche(Vector<Vector<String>> termine,int id,int row){
		//System.out.println("id in Termintabelle = "+id+" / Row in Table = "+row);
		//Wenn die Woche bereits berechnet wurde evtl. nachfragen ob neuberechnung ja/nein
		//  b*5 = Name
		// (b*5)+1 Nummer
		// (b*5)+2 Start
		// (b*5)+3 Dauer
		// (b*5)+4 Ende

		int bloecke = -1;
		boolean krank = false;
		boolean urlaub = false;
		int ikrank = 0;
		int iurlaub = 0;
		
		BigDecimal tagdauer = BigDecimal.valueOf(Double.parseDouble("0.000"));
		BigDecimal wochendauer = BigDecimal.valueOf(Double.parseDouble("0.000"));
		BigDecimal ausfalldauer = BigDecimal.valueOf(Double.parseDouble("0.000"));
		//ausfalldauer.setScale(2, BigDecimal.ROUND_HALF_UP);
		//wochendauer.setScale(2, BigDecimal.ROUND_HALF_UP);
		
		for(int i = 0; i < termine.size();i++){
			
			bloecke = Integer.parseInt( termine.get(i).get(termine.get(i).size()-6) );
			krank = false;
			urlaub = false;
			
			tagdauer = BigDecimal.valueOf(Double.parseDouble("0.000"));
				
			for(int b = 0; b < bloecke; b++){
				
				if( ! termine.get(i).get((b*5)+1).equalsIgnoreCase("@FREI")){
					
					// Minuten aufaddieren
					tagdauer = tagdauer.add( BigDecimal.valueOf( 
							Double.parseDouble(termine.get(i).get( (b*5)+3 )+".000"  )  )  );
					
					//Untersuchen ob Ausfall
					if( termine.get(i).get( (b*5) ).trim().equals("") && 
							termine.get(i).get( (b*5)+1 ).trim().equals("") ){
						ausfalldauer = ausfalldauer.add( BigDecimal.valueOf( 
								Double.parseDouble(termine.get(i).get( (b*5)+3 )+".000"  )  )  );
					}
					
					
				}
				
				if(termine.get(i).get( (b*5)+1 ).equalsIgnoreCase("@FREI") && 
						termine.get(i).get(b*5).toUpperCase().startsWith("KRANK")){
					krank = true;
					ikrank++;
				}
				if(termine.get(i).get( (b*5)+1 ).equalsIgnoreCase("@FREI") && 
						termine.get(i).get( b*5 ).toUpperCase().startsWith("URLAUB")){
					urlaub = true;
					iurlaub++;
				}
			}
			//Ende des Tages
			//
			wochendauer = wochendauer.add(tagdauer);
			
			
			tagdauer = tagdauer.divide(BigDecimal.valueOf(Double.parseDouble("60.000")),2,BigDecimal.ROUND_HALF_UP);
			
			tabmod.setValueAt(tagdauer.doubleValue(),row,( (i*2)+2 ) );
			
			if(krank){
				tabmod.setValueAt("K???",row,( (i*2)+3 ) );	
			}
			if(urlaub){
				tabmod.setValueAt("U???",row,( (i*2)+3 ) );	
			}
			if( (!urlaub) && (!krank) ){
				tabmod.setValueAt("----",row,( (i*2)+3 ) );	
			}
			//System.out.println("Tag Nr."+i+" hat insgesamt "+tagdauer+"minuten");
		}
		//Ende der Woche
		//System.out.println("Wochendauer in Minuten = "+wochendauer);
		wochendauer = wochendauer.divide(BigDecimal.valueOf(Double.parseDouble("60.00")),2,BigDecimal.ROUND_HALF_UP);
		ausfalldauer = ausfalldauer.divide(BigDecimal.valueOf(Double.parseDouble("60.00")),2,BigDecimal.ROUND_HALF_UP);
		//System.out.println("Wochendauer in Stunden = "+wochendauer);
		tabmod.setValueAt(wochendauer.doubleValue(),row,16);
		tabmod.setValueAt(ausfalldauer.doubleValue(),row,21);
		tabmod.setValueAt(Double.parseDouble(usercombo.getValueAt(2).toString().replace(",", ".")),row,17);
		tabmod.setValueAt((Double)tabmod.getValueAt(row, 16)-(Double)tabmod.getValueAt(row, 17),row,18);
		tabmod.setValueAt(Double.parseDouble(Integer.toString(ikrank)), row, 20);
		tabmod.setValueAt(Double.parseDouble(Integer.toString(iurlaub)), row, 19);
		tabmod.setValueAt(Boolean.TRUE,row,25);
		schreibeWoche(row);
		
	}

	/***********************************************************/
	private BigDecimal analysiereTag(Vector<Vector<String>> termine){
		//System.out.println("id in Termintabelle = "+id+" / Row in Table = "+row);
		//Wenn die Woche bereits berechnet wurde evtl. nachfragen ob neuberechnung ja/nein
		//  b*5 = Name
		// (b*5)+1 Nummer
		// (b*5)+2 Start
		// (b*5)+3 Dauer
		// (b*5)+4 Ende

		int bloecke = -1;
		
		BigDecimal tagdauer = BigDecimal.valueOf(Double.parseDouble("0.000"));
		BigDecimal ausfalldauer = BigDecimal.valueOf(Double.parseDouble("0.000"));

			//System.out.println(termine);
			bloecke = Integer.parseInt( termine.get(0).get(termine.get(0).size()-6) );
			
			tagdauer = BigDecimal.valueOf(Double.parseDouble("0.000"));
				
			for(int b = 0; b < bloecke; b++){
				
				if( ! termine.get(0).get((b*5)+1).equalsIgnoreCase("@FREI")){
					
					// Minuten aufaddieren
					tagdauer = tagdauer.add( BigDecimal.valueOf( 
							Double.parseDouble(termine.get(0).get( (b*5)+3 )+".000"  )  )  );
					
					//Untersuchen ob Ausfall
					if( termine.get(0).get( (b*5) ).trim().equals("") && 
							termine.get(0).get( (b*5)+1 ).trim().equals("") ){
						ausfalldauer = ausfalldauer.add( BigDecimal.valueOf( 
								Double.parseDouble(termine.get(0).get( (b*5)+3 )+".000"  )  )  );
					}
					
					
				}
				
			}
			tagdauer = tagdauer.divide(BigDecimal.valueOf(Double.parseDouble("60.000")),2,BigDecimal.ROUND_HALF_UP);
			return tagdauer;
	}
	
	public void testeWoche(int row,int col){
		for(int i = 2; i <= 17;i++){
			//System.out.println("in der schleife "+i+" col = "+col);
			if(col==i){
				if(i % 2 == 0){
					//System.out.println("Stunden wurden editiert");
					tabmod.removeTableModelListener(this);
					rechneWoche(row);
					schreibeWoche(row);
					tabmod.addTableModelListener(this);
				}else{
					//System.out.println("Tagart wurde gewählt");
					tabmod.removeTableModelListener(this);
					rechneTagArt(row,col);
					tabmod.addTableModelListener(this);
				}
			}
		}
	}
	
	public void rechneTagArt(int row,int col){
		String tag =  holeTagDatum( row, ((col+1)/2)-2 );
		if(tabmod.getValueAt(row,col) instanceof Double){
			tabmod.removeTableModelListener(this);
			rechneWoche(row);
			schreibeWoche(row);
			tabmod.addTableModelListener(this);
			return;
		}
		String wert = (String) tabmod.getValueAt(row,col);
		//System.out.println(tag);
		BigDecimal istarbeit = null;
		if(wert.equals("----")){
			istarbeit = ermittleTag(tag,Integer.parseInt(usercombo.getValueAt(1).toString()));
			tabmod.setValueAt(istarbeit.doubleValue(), row, col-1);
		}else if(wert.equals("UuUu")){
			tabmod.setValueAt(zuschlag.doubleValue(), row, col-1);
		}else if(wert.equals("KkKk")){
			tabmod.setValueAt(zuschlag.doubleValue(), row, col-1);
		}else if(wert.equals("FfFf")){
			tabmod.setValueAt(zuschlag.doubleValue(), row, col-1);
		}else if(wert.equals("UuFf")){
			tabmod.setValueAt(zuschlag.doubleValue(), row, col-1);
		}else if(wert.equals("Uu--")){
			istarbeit = ermittleTag(tag,Integer.parseInt(usercombo.getValueAt(1).toString()));
			tabmod.setValueAt(istarbeit.add(halberzuschlag).doubleValue(), row, col-1);
		}else if(wert.equals("Ff--")){
			istarbeit = ermittleTag(tag,Integer.parseInt(usercombo.getValueAt(1).toString()));
			tabmod.setValueAt(istarbeit.add(halberzuschlag).doubleValue(), row, col-1);
		}else if(wert.equals("Kk--")){
			istarbeit = ermittleTag(tag,Integer.parseInt(usercombo.getValueAt(1).toString()));
			tabmod.setValueAt(istarbeit.add(halberzuschlag).doubleValue(), row, col-1);
		}
		rechneWoche(row);
		schreibeWoche(row);
		
	}
	public String holeTagDatum(int row,int plustage){
		String ret = "";
		String erster = tabmod.getValueAt(row,1).toString().split("-")[0];
		return DatFunk.sDatPlusTage(erster, plustage);
	}
	public void rechneWoche(int row){
		BigDecimal urlaub = BigDecimal.valueOf(Double.parseDouble("0.00"));
		BigDecimal krankheit = BigDecimal.valueOf(Double.parseDouble("0.00"));
		BigDecimal stunden = BigDecimal.valueOf(Double.parseDouble("0.00"));
		BigDecimal feiertag = BigDecimal.valueOf(Double.parseDouble("0.00"));
		
		for(int i = 3; i < 17; i+=2){
			stunden = stunden.add(BigDecimal.valueOf((Double)tabmod.getValueAt(row, i-1)));
			if(tabmod.getValueAt(row, i).equals("UuUu")){
				urlaub = urlaub.add(BigDecimal.valueOf(Double.parseDouble("1.00")));
			}else if(tabmod.getValueAt(row, i).equals("KkKk")){
				krankheit = krankheit.add(BigDecimal.valueOf(Double.parseDouble("1.00")));
			}else if(tabmod.getValueAt(row, i).equals("FfFf")){
				feiertag = feiertag.add(BigDecimal.valueOf(Double.parseDouble("1.00")));
			}else if(tabmod.getValueAt(row, i).equals("UuFf")){
				urlaub = urlaub.add(BigDecimal.valueOf(Double.parseDouble("0.50")));
				feiertag = feiertag.add(BigDecimal.valueOf(Double.parseDouble("0.50")));
			}else if(tabmod.getValueAt(row, i).equals("Uu--")){
				urlaub = urlaub.add(BigDecimal.valueOf(Double.parseDouble("0.50")));
			}else if(tabmod.getValueAt(row, i).equals("Ff--")){
				feiertag = feiertag.add(BigDecimal.valueOf(Double.parseDouble("0.50")));
			}else if(tabmod.getValueAt(row, i).equals("Kk--")){
				krankheit = krankheit.add(BigDecimal.valueOf(Double.parseDouble("0.50")));
			}
		}
		tabmod.setValueAt(stunden.doubleValue(), row, 16);
		tabmod.setValueAt(urlaub.doubleValue(), row, 19);
		tabmod.setValueAt(krankheit.doubleValue(), row, 20);
		tabmod.setValueAt((Double)tabmod.getValueAt(row,16)-(Double)tabmod.getValueAt(row,17), row, 18);
	}
	/********************************************************************/
	class MyComboBoxRenderer extends BasicComboBoxRenderer {
		    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
				if (-1 < index) {
					list.setToolTipText(tooltips[index]);
				}
			} else {
		        setBackground(list.getBackground());
		        setForeground(list.getForeground());
			}
		      setFont(list.getFont());
		      setText((value == null) ? "" : value.toString());
		      return this;
		    }
		  }


}
