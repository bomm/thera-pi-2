package rehaKassenbuch;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import Tools.ButtonTools;
import Tools.DatFunk;
import Tools.JCompTools;
import Tools.JRtaComboBox;
import Tools.OOTools;
import Tools.SqlInfo;
import Tools.TableTool;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.spreadsheet.ISpreadsheetDocument;
import ag.ion.noa.NOAException;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.XSheetCellCursor;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XSpreadsheets;
import com.sun.star.uno.UnoRuntime;

public class RehaKassenbuchEdit extends JXPanel implements TableModelListener{
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 903327960325888094L;
	
	
	RehaKassenbuchTab eltern;
	JButton[] buts = {null,null,null,null};
	ActionListener al = null;
	Vector<String> datavec = new Vector<String>();	
	JRtaComboBox combo = null;
	
	MyKassenbuchTableModel tabmod = null;
	JXTable tab = null;
	
	DecimalFormat dcf = new DecimalFormat("###0.00");
	
	Vector<Vector<String>> feldNamen = null;

	
	JLabel anzahlsaetze = null;
	
	int calcrow = 0;	
	ISpreadsheetDocument spreadsheetDocument = null;;
	IDocument document  = null;
	XSheetCellCursor cellCursor = null;
	
	public RehaKassenbuchEdit(RehaKassenbuchTab rkbtab){
		super();
		eltern = rkbtab;
		setLayout(new BorderLayout());
		activateListener();
		add(getContent(),BorderLayout.CENTER);
		
	}
	private JXPanel getContent(){
		//                 1     2                3     
		String xwerte = "10dlu,fill:0:grow(1.0),10dlu";
		//                1          2           3   4   5   6
		String ywerte = "10dlu,fill:0:grow(1.0),2dlu,p,2dlu,10dlu";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		JXPanel jpan = new JXPanel();
		jpan.setLayout(lay);
		long zeit = System.currentTimeMillis();
		while(!RehaKassenbuch.DbOk){
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(System.currentTimeMillis()-zeit > 5000){
				break;
			}
		}
		if(RehaKassenbuch.DbOk){
			doKBErmitteln();					
		}else{
			JOptionPane.showMessageDialog(null,"Datenbankserver kann nicht geöffnet werden.\nSystem wird beendet.");
			System.exit(0);
		}
		tabmod = new MyKassenbuchTableModel();
		feldNamen = SqlInfo.holeFelder("describe kasse");
		//Vector<Vector<String>> felder = SqlInfo.holeFelder("describe faktura");
		//System.out.println(feldNamen);
		//System.out.println(feldNamen.size());
		String[] spalten = new String[feldNamen.size()];
		for(int i= 0; i < feldNamen.size();i++){
			spalten[i] = feldNamen.get(i).get(0);
		}
		tabmod.setColumnIdentifiers(spalten);
		tab = new JXTable(tabmod);
		/*
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
		*/
		tab.setCellSelectionEnabled(true);
		tab.setAutoStartEditOnKeyStroke(true);
		tab.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.CLASSIC_LINE_PRINTER));
		tab.setSortable(false);
		tabmod.addTableModelListener(this);
		tab.getColumn(0).setCellRenderer(new Tools.DoubleTableCellRenderer());
		tab.getColumn(0).setCellEditor(new Tools.DblCellEditor());
		tab.getColumn(1).setCellRenderer(new Tools.DoubleTableCellRenderer());
		tab.getColumn(1).setCellEditor(new Tools.DblCellEditor());
		tab.getColumn(3).setMinWidth(350);
		tab.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0));
		MyListener myEnterListener = new MyListener();		
		tab.registerKeyboardAction(myEnterListener, "Enter",
		KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
		JComponent.WHEN_FOCUSED);
		
 

		
		
		DateTableCellEditor tble = new DateTableCellEditor();
		tab.getColumnModel().getColumn(2).setCellEditor(tble);
		JScrollPane jscr = JCompTools.getTransparentScrollPane(tab);
		jscr.validate();
		jpan.add(jscr,cc.xy(2,2,CellConstraints.FILL,CellConstraints.FILL));
		
		/***************/
		//                                 1    2     3   4     5     6    7    8     9         1    2  4
		FormLayout lay2 = new FormLayout("0dlu,65dlu,5dlu,65dlu,5dlu,65dlu,5dlu,65dlu,0dlu:g","10dlu,p,10dlu");
		CellConstraints cc2 = new CellConstraints();
		JXPanel jpan2 = new JXPanel(lay2);
		combo = new JRtaComboBox();
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				doKBErmitteln();
				return null;
			}
		}.execute();
		combo.setActionCommand("combo");
		combo.addActionListener(al);
		jpan2.add(combo,cc2.xy(2,2));
		buts[0] = ButtonTools.macheButton("neue Buchung", "buchungneu", al);
		buts[0].setMnemonic(KeyEvent.VK_N);
		jpan2.add(buts[0],cc2.xy(4,2));
		buts[1] = ButtonTools.macheButton("löschen Buchung", "buchungloeschen", al);
		buts[1].setMnemonic(KeyEvent.VK_L);
		jpan2.add(buts[1],cc2.xy(6,2));
		buts[2] = ButtonTools.macheButton("Calc starten", "calc", al);
		buts[2].setMnemonic(KeyEvent.VK_C);
		jpan2.add(buts[2],cc2.xy(8,2));
		
		anzahlsaetze = new JLabel("");
		anzahlsaetze.setForeground(Color.BLUE);
		jpan2.add(anzahlsaetze,cc.xy(9,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		jpan2.validate();
		
		jpan.add(jpan2,cc.xy(2,4));
		
		jpan.validate();
		
		
		return jpan;
	}
	
	private class MyListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			//Object src = e.getSource();
			String actionCmd = e.getActionCommand();
			if (actionCmd.equals("Enter")) {
				int row = tab.getSelectedRow();
				int col = tab.getSelectedColumn();
				//System.out.println(row+" / "+col);
				tab.getCellEditor(row, col).stopCellEditing();
				if(col== tab.getColumnCount()-1){
					col=0;
				}else{
					col++;
				}
				tab.setRowSelectionInterval(row, row);
				tab.setColumnSelectionInterval(col, col);
			}
		}
	}
	
	private void activateListener(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("combo")){
					fuelleTabelle(combo.getSelectedIndex());
					return;
				}
				if(cmd.equals("buchungneu")){
					doNeuSatz();
					anzahlsaetze.setText(Integer.toString(tab.getRowCount())+" Buchungssätze");
						return;
				}
				if(cmd.equals("buchungloeschen")){
					doLoeschenSatz();
					anzahlsaetze.setText(Integer.toString(tab.getRowCount())+" Buchungssätze");
					return;
				}
				if(cmd.equals("calc")){
					try {
						starteCalc();
					} catch (NoSuchElementException e) {
						e.printStackTrace();
					} catch (WrappedTargetException e) {
						e.printStackTrace();
					} catch (UnknownPropertyException e) {
						e.printStackTrace();
					} catch (PropertyVetoException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (OfficeApplicationException e) {
						e.printStackTrace();
					} catch (NOAException e) {
						e.printStackTrace();
					} catch (IndexOutOfBoundsException e) {
						e.printStackTrace();
					}
					return;
				}
			}
		};
	}
	
	private void doNeuSatz(){
		if(combo.getSelectedIndex()<=0){
			return;
		}
		String cmd = "insert into "+combo.getSelectedItem()+" set ktext='',rez_nr=''";
		int id = SqlInfo.holeId(combo.getSelectedItem().toString(),cmd);
		Vector<Vector<String>> vecx = SqlInfo.holeFelder("select * from "+
				combo.getSelectedItem().toString()+" where id = '"+Integer.toString(id)+"' LIMIT 1");
		tabmod.addRow((Vector<?>)vecx.get(0).clone());
		tab.requestFocus();
		tab.scrollCellToVisible(tab.getRowCount()-1, 0);
		tab.setRowSelectionInterval(tab.getRowCount()-1, tab.getRowCount()-1);
		tab.setColumnSelectionInterval(0, 0);
	}
	private void doLoeschenSatz(){
		if(tab.getSelectedRow() < 0){
			return;
		}
		//int id = Integer.parseInt((String)tab.getValueAt(tab.getSelectedRow(), 6));
		SqlInfo.sqlAusfuehren("delete from "+combo.getSelectedItem().toString()+
				" where id = '"+tab.getValueAt(tab.getSelectedRow(), 6).toString()+"' LIMIT 1");
		TableTool.loescheRow(tab, tab.getSelectedRow());
	}
	public void doKBErmitteln(){
		
		long zeit = System.currentTimeMillis();
		while(!RehaKassenbuch.DbOk){
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(System.currentTimeMillis()-zeit > 5000){
				break;
			}
		}
		if(!RehaKassenbuch.DbOk){
			System.exit(0);					
		}

		Vector<Vector<String>> vec = SqlInfo.holeFelder("show tables");
		datavec.clear();
		datavec.add("./.");
		for(int i = 0; i < vec.size();i++){
			if(vec.get(i).get(0).startsWith("kb_")){
				datavec.add(vec.get(i).get(0));
			}
		}
		System.out.println(datavec);
		if(combo == null){
			System.out.println("combo == null");
			return;
		}
		combo.setDataVector(datavec);
		combo.setSelectedIndex(0);
		fuelleTabelle(0);
		//kbvorhanden.setListData(datavec);
	}
	public void fuelleTabelle(int tabindex){
		tabmod.setRowCount(0);
		tab.validate();

		if(tabindex <= 0){
			anzahlsaetze.setText("");
			return;
		}
		tabmod.setRowCount(0);
		tab.validate();
		tab.repaint();
		Statement stmt = null;
		ResultSet rs = null;
		String mystmt = "select * from "+datavec.get(tabindex)+" order by id";
		try {
			stmt = RehaKassenbuch.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
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
						Thread.sleep(75);
						durchlauf = 0;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				durchlauf++;
			}
			tab.requestFocus();
			if(tab.getRowCount() > 0){
				tab.scrollCellToVisible(tab.getRowCount()-1, 0);
				tab.setRowSelectionInterval(tab.getRowCount()-1, tab.getRowCount()-1);
				tab.setColumnSelectionInterval(0, 0);
			}
			anzahlsaetze.setText(Integer.toString(tab.getRowCount())+" Buchungssätze");
			tab.validate();
			tab.repaint();
			
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
				//String colname = tabmod.getColumnName(col).toString();
				String value = "";
				//String id = Integer.toString((Integer)tabmod.getValueAt(row,6));

				if( tabmod.getColumnClass(col) == Boolean.class){
					value = (tabmod.getValueAt(row,col) == Boolean.FALSE ? "F" : "T");
				}else if(tabmod.getColumnClass(col) == Date.class){
					value = DatFunk.sDatInSQL(tabmod.getValueAt(row,col).toString());
				}else if(tabmod.getColumnClass(col) == Double.class){
					value = dcf.format((Double)tabmod.getValueAt(row,col)).replace(",",".");
				}else if(tabmod.getColumnClass(col) == Integer.class){
					value = Integer.toString((Integer)tabmod.getValueAt(row,col));
				}else if(tabmod.getColumnClass(col) == String.class){
					value = tabmod.getValueAt(row,col).toString();
				}
				direktSchreiben(row,col,value);
			
			}catch(Exception ex){
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null,"Fehler in der Dateneingbe");
			}
			
			
			return;
		}
		if(arg0.getType() == TableModelEvent.DELETE){
		}

	}	
	
	class MyKassenbuchTableModel extends DefaultTableModel{
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
			if(col == 6){
				return false;				
			}
			return true;
		}
		   
	}
	
	private void direktSchreiben(int row,int col,String value){
		if(combo.getSelectedIndex()<=0){
			return;
		}

		String id = tabmod.getValueAt(row,6).toString();
		

		String cmd = "update "+combo.getSelectedItem().toString()+" set "+
		feldNamen.get(col).get(0)+" = '"+value+"' where id = '"+id+"' LIMIT 1";
		//System.out.println(cmd);
		SqlInfo.sqlAusfuehren(cmd);
		
	}

	@SuppressWarnings("unchecked")
	private void starteCalc() throws OfficeApplicationException, NOAException, NoSuchElementException, WrappedTargetException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException, IndexOutOfBoundsException{
		int tabindex = combo.getSelectedIndex();
		if(tabindex <=0){
			return;
		}
		fuelleTabelle(tabindex);
		if(!RehaKassenbuch.officeapplication.isActive()){
			RehaKassenbuch.starteOfficeApplication();
		}
		IDocumentService documentService = RehaKassenbuch.officeapplication.getDocumentService();
        IDocumentDescriptor docdescript = new DocumentDescriptor();
       	docdescript.setHidden(true);
        docdescript.setAsTemplate(false);
		document = documentService.constructNewDocument(IDocument.CALC, DocumentDescriptor.DEFAULT);
		spreadsheetDocument = (ISpreadsheetDocument) document;
		//Tools.OOTools.setzePapierFormatCalc((ISpreadsheetDocument) spreadsheetDocument, 21000, 29700);
		//Tools.OOTools.setzeRaenderCalc((ISpreadsheetDocument) spreadsheetDocument, 1000,1000, 1000, 1000);
		
		XSpreadsheets spreadsheets = spreadsheetDocument.getSpreadsheetDocument().getSheets();
		String sheetName= "Tabelle1";
		XSpreadsheet spreadsheet1 = (XSpreadsheet)UnoRuntime.queryInterface(XSpreadsheet.class,spreadsheets.getByName(sheetName));
		cellCursor = spreadsheet1.createCursor();
		final ISpreadsheetDocument xspredsheetDocument = spreadsheetDocument;
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				xspredsheetDocument.getFrame().getXFrame().getContainerWindow().setVisible(true);
				xspredsheetDocument.getFrame().setFocus();
			}
		});
		OOTools.doColWidth(spreadsheetDocument, sheetName, 3, 3, 1000);
		OOTools.doColWidth(spreadsheetDocument, sheetName, 4, 4, 10000);
		OOTools.doColNumberFormat(spreadsheetDocument, sheetName, 0, 1, 2);

		OOTools.doCellValue(cellCursor, 0, 0, (String) "EINNAHME");
		OOTools.doCellValue(cellCursor, 1, 0, (String) "AUSGABE");
		OOTools.doCellValue(cellCursor, 2, 0, (String) "DATUM");
		OOTools.doCellValue(cellCursor, 4, 0, (String) "TEXT");
		Vector<Vector<Object>> vec = (Vector<Vector<Object>>)tabmod.getDataVector();
		for(int i = 0; i < vec.size();i++){
			if(vec.get(i).get(0) instanceof Double){
				if( ((Double) vec.get(i).get(0)) != 0.){
					OOTools.doCellValue(cellCursor, 0, i+1, (Double) vec.get(i).get(0));				
				}
				if( ((Double) vec.get(i).get(1)) != 0.){
					OOTools.doCellValue(cellCursor, 1, i+1, (Double) vec.get(i).get(1));				
				}
			}
			if(vec.get(i).get(0) instanceof java.lang.String){
				if( ((Double) vec.get(i).get(0)) != 0.){
					OOTools.doCellValue(cellCursor, 0, i+1, (Double) vec.get(i).get(0));				
				}
				if( ((Double) vec.get(i).get(1)) != 0.){
					OOTools.doCellValue(cellCursor, 1, i+1, (Double) vec.get(i).get(1));				
				}
			}

			try{
				OOTools.doCellValue(cellCursor, 2, i+1, DatFunk.sDatInDeutsch(((Date) vec.get(i).get(2)).toString()));
			}catch(Exception ex){}
			OOTools.doCellValue(cellCursor, 4, i+1, (String) vec.get(i).get(3));
			
		}
	}


}