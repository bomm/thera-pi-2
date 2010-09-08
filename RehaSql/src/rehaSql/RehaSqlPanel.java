package rehaSql;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;



import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import Tools.ButtonTools;
import Tools.DatFunk;
import Tools.DblCellEditor;
import Tools.DoubleTableCellRenderer;
import Tools.INIFile;
import Tools.JCompTools;
import Tools.JRtaCheckBox;
import Tools.JRtaComboBox;
import Tools.JRtaTextField;
import Tools.OOTools;
import Tools.SqlInfo;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.spreadsheet.ISpreadsheetDocument;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextTable;
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


public class RehaSqlPanel extends JXPanel implements ListSelectionListener, ActionListener, TableModelListener  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5545910505665721828L;
	
	RehaSqlTab elternTab = null;
	JXPanel content = null;
	JRtaTextField[] tfs = {null,null,null,null,null,null,null,null};
	JRtaCheckBox originalChb = null;
	
	JRtaComboBox chbstatement = null;
	JButton[] buts = {null,null,null,null};
	
	KeyListener kl = null;
	ActionListener al = null;
	
	SqlTableModel tabmod = null;
	JXTable tab = null;
	
	DefaultTableModel alletablemod = null;
	JXTable alletabletab = null;
	
	JTextArea textArea = null;
	JLabel labgefunden = null;
	
	//Vector<Vector<String>> feldNamen = null; 

	JLabel rnummerAlt = null;
	JLabel rbetragAlt = null;
	JLabel rbetragNeu = null;
	JLabel rdatumAlt = null;
	Double ibetragNeu = Double.parseDouble("0.00");
	int originalPositionen = -1;
	
	BigDecimal rechnungGesamt = null;

	
	Vector<String> colName = new Vector<String>();
	Vector<String> colClassName = new Vector<String>();
	Vector<Integer> colType = new Vector<Integer>();
	Vector<Boolean> colAutoinc = new Vector<Boolean>();
	Vector<String> colTypeName = new Vector<String>();
	Vector<Integer> colVisible = new Vector<Integer>();
	
	Vector<Vector<String>> vecStatements = new Vector<Vector<String>>();
	
	int autoIncCol = -1;
	
	HashMap<String,String> hmAdresse = new HashMap<String,String>(); 
	
	ITextTable textTable = null;
	ITextTable textEndbetrag = null;
	ITextDocument textDocument = null;
	int aktuellePosition = 0;
	int patKilometer = 0;	
	
	JScrollPane jscr = null;
	
	DateTableCellEditor tabDateEditor = new DateTableCellEditor();
	DateTableCellRenderer tabDateRenderer = new DateTableCellRenderer();
	
	DblCellEditor tabDoubleEditor = new DblCellEditor();
	DoubleTableCellRenderer tabDoubleRenderer = new DoubleTableCellRenderer();
	
	IntTableCellEditor tabIntegerEditor = new IntTableCellEditor();
	IntTableCellRenderer tabIntegerRenderer = new IntTableCellRenderer();
	
	JRtaTextField sqlstatement = null;
	
	DecimalFormat dcf = new DecimalFormat("##########0.00");
	SimpleDateFormat datumsFormat = new SimpleDateFormat ("dd.MM.yyyy"); //Konv.
	
	int calcrow = 0;	
	ISpreadsheetDocument spreadsheetDocument = null;;
	IDocument document  = null;
	XSheetCellCursor cellCursor = null;
	String sheetName = null;
	
	public RehaSqlPanel(RehaSqlTab eltern){
		super();
		elternTab = eltern;
		setLayout(new BorderLayout());
		activateActionListener();
		add(getContent(),BorderLayout.CENTER);
		
		validate();
		content.validate();
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				long zeit = System.currentTimeMillis();
				while(! RehaSql.DbOk){
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						if(System.currentTimeMillis()-zeit > 10000){
							System.exit(0);
						}
						e.printStackTrace();
					}
				}
				chbstatement.addActionListener(al);
				setzeFocus((JComponent) sqlstatement);
				return null;
			}
			
		}.execute();
	}
	private JXPanel getContent(){
		String xwerte = "fill:0:grow(0.2),fill:0:grow(0.8)";
		String ywerte = "0dlu,fill:0:grow(1.0),0dlu";
		
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		content = new JXPanel();
		content.setLayout(lay);
		//content.add(getEdits(),cc.xy(1,2));
		content.add(getSqlPanel(),cc.xy(2,2,CellConstraints.FILL,CellConstraints.FILL));
		content.add(getAlleTabellenPanel(),cc.xy(1,2,CellConstraints.FILL,CellConstraints.FILL));
		content.revalidate();
		return content;
	}
	private JXPanel getAlleTabellenPanel(){
		String xwerte = "5dlu,fill:0:grow(1.0),5dlu";
		String ywerte = "5dlu,fill:0:grow(1.0),5dlu";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		JXPanel jpan = new JXPanel();
		jpan.setOpaque(false);
		jpan.setLayout(lay);

		alletablemod = new DefaultTableModel();
		alletablemod.setColumnIdentifiers(new String[] {"Alle-Tabellen"});
		alletabletab = new JXTable(alletablemod);
		alletabletab.setEditable(false);
		alletabletab.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				if(me.getClickCount()==2){
					sqlstatement.setText("describe "+alletabletab.getValueAt(alletabletab.getSelectedRow(), 0).toString());
					doStatementAuswerten();
				}
			}
		});
		
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				long zeit = System.currentTimeMillis();
				while(! RehaSql.DbOk){
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						if(System.currentTimeMillis()-zeit > 10000){
							System.exit(0);
						}
						e.printStackTrace();
					}
				}
				Vector<Vector<String>> vec = SqlInfo.holeFelder("show tables");
				for(int i = 0;i<vec.size();i++){
					alletablemod.addRow(vec.get(i));
				}
				if(vec.size() > 0){
					alletabletab.setRowSelectionInterval(0, 0);
				}
				return null;
			}
		}.execute();
		JScrollPane alletbljscr = JCompTools.getTransparentScrollPane(alletabletab);
		alletbljscr.validate();
		jpan.add(alletbljscr,cc.xy(2,2,CellConstraints.FILL,CellConstraints.FILL));
		jpan.validate();
		return jpan;
	}
	private JXPanel getSqlPanel(){
		//                1    2     3          4          5    6    7
		String xwerte = "5dlu,40dlu,2dlu,fill:0:grow(1.0),2dlu,60dlu,5dlu";
		//                1   2  3     4               5   6  7   8  9      10
		//String ywerte = "5dlu,p,2dlu,fill:0:grow(0.5),2dlu,p,2dlu,p,2dlu,fill:0:grow(0.5),5dlu";
		//				  1   2  3     4                    5   6  7   8  9    10                   11
		String ywerte = "5dlu,p,2dlu,fill:100dlu:grow(0.5),2dlu,p,2dlu,p,2dlu,fill:100dlu:grow(0.5),5dlu";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		JXPanel jpan = new JXPanel();
		jpan.setOpaque(false);
		jpan.setLayout(lay);
		jpan.add(new JLabel("Statement"),cc.xy(2,2));
		jpan.add(sqlstatement = new JRtaTextField("nix",true),cc.xy(4,2));
		sqlstatement.setName("sqlstatement");
		sqlstatement.setFont(new Font("Courier New",Font.PLAIN,12));
		sqlstatement.addKeyListener(kl);
		jpan.add(buts[0] = ButtonTools.macheButton("execute", "exekutieren", al),cc.xy(6,2));
		tabmod = new SqlTableModel();
		tab = new JXTable(tabmod);
		tab.setColumnControlVisible(true);
		
		tab.setHorizontalScrollEnabled(true);
		tab.getSelectionModel().addListSelectionListener( new BillListSelectionHandler());
		
		tab.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.CLASSIC_LINE_PRINTER));
		tabmod.addTableModelListener(this);
		
		jscr = JCompTools.getTransparentScrollPane(tab);
		jscr.validate();
		jpan.add(jscr,cc.xyw(2,4,5,CellConstraints.DEFAULT,CellConstraints.FILL));		

		labgefunden = new JLabel("noch keine Abfageergebnisse");
		labgefunden.setForeground(Color.BLUE);
		jpan.add(labgefunden,cc.xyw(2,6,5));

		//Hier mit y-Wert = 8 die Funktionsleiste einbauen
		jpan.add(getFunktionsPanel(),cc.xyw(2,8,5,CellConstraints.FILL,CellConstraints.FILL));
		
		textArea = new JTextArea();
		textArea.setFont(new Font("Courier",Font.PLAIN,12));
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setBackground(Color.LIGHT_GRAY);
		textArea.setForeground(Color.BLUE);
		//ta.setEnabled(false);
		//ta.setDisabledTextColor(Color.BLUE);
		JScrollPane span = JCompTools.getTransparentScrollPane(textArea);
		span.setBackground(Color.WHITE);
		span.validate();
		jpan.add(span, cc.xyw(2,10,5,CellConstraints.DEFAULT,CellConstraints.FILL));
		
		jpan.validate();
		return jpan;
		
	}
	private void setzeFocus(JComponent comp){
		final JComponent xcomp = comp;
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				xcomp.requestFocus();
			}
		});
	}
	private JXPanel getFunktionsPanel(){
		//1.export in OO-Calc
		//Speichern des SQL-Befehles in der SQL-INI und Platzhalterhandling
		//Auswahl ob Ergebnis in Tabelle oder in TextArea geworfen wird
		//Listbox mit den vorhandenen Sql-Befehlen
		//                1    2     3     4     5    6          7           8     9     10    
		String xwerte = "0dlu,160dlu,2dlu,80dlu,2dlu,80dlu,fill:0:grow(1.0),40dlu,2dlu,40dlu";
		//                1   2  3     4               5   6  7   8  9      10
		//String ywerte = "5dlu,p,2dlu,fill:0:grow(0.5),2dlu,p,2dlu,p,2dlu,fill:0:grow(0.5),5dlu";
		//				  1   2  3   
		String ywerte = "5dlu,p,5dlu";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		JXPanel jpan = new JXPanel();
		jpan.setOpaque(false);
		jpan.setLayout(lay);
		jpan.add(buts[0]=ButtonTools.macheButton("Export in OO-Calc", "exportcalc", al),cc.xy(6, 2));
		chbstatement = new JRtaComboBox();
		chbstatement.setActionCommand("statementliste");
		
		jpan.add(chbstatement,cc.xy(2,2));
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				doFillStatementBox();
				return null;
			}
		}.execute();
		jpan.add(buts[1]=ButtonTools.macheButton("Sql ausführen", "executestmt", al),cc.xy(4, 2));
		jpan.add(buts[2]=ButtonTools.macheButton("neu", "neuersatz", al),cc.xy(8, 2));
		jpan.add(buts[3]=ButtonTools.macheButton("löschen", "loeschensatz", al),cc.xy(10, 2));
		jpan.validate();
		return jpan;
		
	}
	private void doFillStatementBox(){
		String inipfad = RehaSql.progHome+"ini/"+RehaSql.aktIK+"/sqlmodul.ini";
		try{
			INIFile inif = new INIFile(inipfad);
			Vector<String> vecstmts = new Vector<String>();
			int anzahl = inif.getIntegerProperty("SqlStatements", "StatementsAnzahl");
			System.out.println("Statements Insgesamt= "+anzahl);
			for(int i = 0; i < anzahl;i++){
				vecstmts.clear();
				vecstmts.add((String)inif.getStringProperty("SqlStatements", "StatementTitel"+Integer.toString(i+1)));
				vecstmts.add((String)inif.getStringProperty("SqlStatements", "Statement"+Integer.toString(i+1)));
				vecStatements.add( ((Vector<String>)vecstmts.clone()) );
			}
			chbstatement.setDataVectorWithStartElement(vecStatements, 0, 1, "./.");
			chbstatement.setSelectedItem("./.");
		}catch(Exception ex){
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Fehler beim Einlesen der Statements");
		}
	}

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
	                	//System.out.println("Satz "+i);
	                    break;
	                }
	            }
	        }

	    }
	}
	private void activateActionListener(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("exekutieren")){
					try{
						doStatementAuswerten();
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
				if(cmd.equals("exportcalc")){
					try{
						starteExport();	
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
				if(cmd.equals("statementliste")){
					try{
						if(chbstatement.getSelectedIndex()<=0){
							sqlstatement.setText("");
							tabmod.setRowCount(0);
							tab.validate();
							return;
						}
						doMacheStatement();
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}

				if(cmd.equals("executestmt")){
					try{
						if(chbstatement.getSelectedIndex()<=0){
							sqlstatement.setText("");
							return;
						}
						doStatementAuswerten();
						//doMacheStatement();
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
				if(cmd.equals("zeileloeschen")){
					try{
						//doZeileLoeschen();
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
				if(cmd.equals("indbspeichern")){
					try{
						//doInDbSpeichern();
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
				if(cmd.equals("abbrechen")){
					try{
						//doAbbrechen();
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
			}
			
		};
		kl = new KeyListener(){
			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode()==10){
					if( ((JComponent)arg0.getSource()).getName().equals("sqlstatement") ){
						doStatementAuswerten();
						SwingUtilities.invokeLater(new Runnable(){
							public void run(){
								sqlstatement.requestFocus();
							}
						});
					}
				}
				
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		};
	}
	private void doMacheStatement(){
		int ind = this.chbstatement.getSelectedIndex();
		if(ind <= 0){
			JOptionPane.showMessageDialog(null,"Kein vorbereitetes Statement ausgewählt");
			return;
		}
		String prepstatement = vecStatements.get(ind-1).get(1);
		prepstatement = testeAufPlatzhalter(prepstatement);
		if(prepstatement.equals("")){
			JOptionPane.showMessageDialog(null,"Fehler in der Eingabe");
			return;
		}
		sqlstatement.setText(prepstatement);
		buts[1].requestFocus();
	}
	private String testeAufPlatzhalter(String text){
		String sret = "";
		//int lang = text.length();
		String stext = text;
		int start = 0;
		//int end = 0;
		String dummy;
		int vars = 0;
		//int sysvar = -1;
		boolean noendfound = false;
		while ((start = stext.indexOf("^")) >= 0){
			noendfound = true;
			for(int i = 1;i < 150;i++){
				if(stext.substring(start+i,start+(i+1)).equals("^")){
					dummy = stext.substring(start,start+(i+1));
					String sanweisung = dummy.toString().replace("^", "");
					Object ret = JOptionPane.showInputDialog(null,"<html>Bitte Wert eingeben für: --\u003E<b> "+sanweisung+" </b> &nbsp; </html>","Platzhalter gefunden", 1);
					if(ret==null){
						return "";
							//sucheErsetze(dummy,"");
					}else{
						//sucheErsetze(document,dummy,((String)ret).trim(),false);
						if( ((String)ret).trim().length()==10 && ((String)ret).trim().indexOf(".") ==2 &&
										((String)ret).trim().lastIndexOf(".") == 5 ) {

								
							try{
								ret = DatFunk.sDatInSQL((String)ret);
							}catch(Exception ex){
								JOptionPane.showMessageDialog(null,"Fehler in der Konvertierung des Datums");
							}
							
						}
						sret = stext.replace(dummy, ((String)ret).trim());
						stext = sret;
					}
					noendfound = false;
					vars++;
					break;
				}
			}
			if(noendfound){
				JOptionPane.showMessageDialog(null,"Der Baustein ist fehlerhaft, eine Übernahme deshalb nicht möglich"+
						"\n\nVermutete Ursache des Fehlers: es wurde ein Start-/Endezeichen '^' für Variable vergessen\n");
				return "";
			}
		}
		
		return sret;
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void tableChanged(TableModelEvent arg0) {
		if(arg0.getType() == TableModelEvent.INSERT){
			return;
		}
		if(arg0.getType() == TableModelEvent.UPDATE){
			try{
				int col = arg0.getColumn();
				int row = arg0.getFirstRow();
				//String colname = tabmod.getColumnName(col).toString();
				String value = "";
				//String id = Integer.toString((Integer)tabmod.getValueAt(row,tabmod.getColumnCount()-1));

				if( tabmod.getColumnClass(col) == Boolean.class){
					value = (tabmod.getValueAt(row,col) == Boolean.FALSE ? "F" : "T");
				}else if(tabmod.getColumnClass(col) == Date.class){
					value = tabmod.getValueAt(row,col).toString();
				}else if(tabmod.getColumnClass(col) == Double.class){
					value = dcf.format((Double)tabmod.getValueAt(row,col)).replace(",",".");
				}else if(tabmod.getColumnClass(col) == Integer.class){
					value = Integer.toString((Integer)tabmod.getValueAt(row,col));
				}else if(tabmod.getColumnClass(col) == String.class){
					value = tabmod.getValueAt(row,col).toString();
				}
			}catch(Exception ex){
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null,"Fehler in der Dateneingbe");
			}
			
			return;
		}
		if(arg0.getType() == TableModelEvent.DELETE){
			//System.out.println(arg0.getFirstRow()+" / "+arg0.getColumn());
			//System.out.println("ID des zu löschenden Satzes = "+tabmod.getValueAt(arg0.getFirstRow(),tabmod.getColumnCount()-1));
		}

	}

		
	private void doExecuteStatement(){
		Statement stmt = null;
		//auf delete und update testen und wenn ja auf Limit 1 testen und wenn nein SuperUser-Passwort anfordern
		try {
			textArea.setText("Ihr Statement: ["+sqlstatement.getText().trim()+"]\n"+textArea.getText());
			stmt =  RehaSql.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
			stmt.executeUpdate(sqlstatement.getText());
		} catch (SQLException e) {
			textArea.setText(e.getMessage()+"\n"+textArea.getText());
		}
		finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { // ignore }
					stmt = null;
				}
			}
		}

		
	}
	private void doOnlySuperUserExecuteStatement(){
		Statement stmt = null;
		//SuperUser-Passwort anfordern		
		try {
			textArea.setText("Ihr Statement: ["+sqlstatement.getText().trim()+"]\n"+textArea.getText());
			stmt =  RehaSql.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
			stmt.executeUpdate(sqlstatement.getText());

		} catch (SQLException e) {
			textArea.setText(e.getMessage()+"\n"+textArea.getText());
		}
		finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { // ignore }
					stmt = null;
				}
			}
		}

		
	}

	private void doStatementAuswerten(){
		if(sqlstatement.getText().trim().equals("")){
			return;
		}
		if(sqlstatement.getText().trim().toLowerCase().startsWith("drop")){
			doOnlySuperUserExecuteStatement();
			return;
		}
		
		if(sqlstatement.getText().trim().toLowerCase().startsWith("update") || sqlstatement.getText().trim().toLowerCase().startsWith("insert") ||
				sqlstatement.getText().trim().toLowerCase().startsWith("delete")){
			doExecuteStatement();
			return;
		}
		
		/*
		if(sqlstatement.getText().trim().toLowerCase().startsWith("select") || sqlstatement.getText().trim().toLowerCase().startsWith("show") ||
				sqlstatement.getText().trim().toLowerCase().startsWith("describe")){
			doExecuteStatement();
		}
		*/
		autoIncCol = -1;
		tabmod.removeTableModelListener(this);
		tabmod.setRowCount(0);
		colName.clear();
		colType.clear();
		colAutoinc.clear();
		colClassName.clear();
		colTypeName.clear();
		
		Statement stmt = null;
		ResultSet rs = null;
		//ResultSet md = null;
		ResultSetMetaData md = null;

			
		try {
			textArea.setText("Ihr Statement: ["+sqlstatement.getText().trim()+"]\n"+textArea.getText());
			stmt =  RehaSql.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
			rs = stmt.executeQuery(sqlstatement.getText().trim());

			md = (ResultSetMetaData) rs.getMetaData();
			int cols = md.getColumnCount();
			for(int i = 0; i < cols;i++){
				colName.add(md.getColumnName(i+1));
				colType.add(md.getColumnType(i+1));
				colClassName.add(md.getColumnClassName(i+1));
				if(md.getColumnType(i+1) == 1 && md.getColumnDisplaySize(i+1)==1){
					//dann wird enum('T','F') also logisch vermutet und
					//per Hand BOOLEAN eingetragen. Stimmt natürlich nicht wenn in der
					//Tabellendefinition tatsächlich Spaltenbreite 1 und typ CHAR eingetragen ist. - Leider
					colTypeName.add("BOOLEAN");
				}else{
					colTypeName.add(md.getColumnTypeName(i+1));
				}
				colAutoinc.add(md.isAutoIncrement(i+1));
				if(md.isAutoIncrement(i+1)){
					autoIncCol = i;
				}
	
			}
			tabmod.setColumnIdentifiers(colName);
			
			Vector<Object> vec = new Vector<Object>();
			int durchlauf = 0;
			int lang = colName.size();
			//Saudummerweise entspricht der Rückgabewert von getColumnTypeName() oder
			//getColumnType() nicht der Abfrag von describe tabelle
			//so werden alle Integer-Typen unter INT zusammengefaßt
			//Longtext, Mediumtext, Varchar = alles VARCHAR
			//CHAR kann sowohl ein einzelnes Zeichen als auch enum('T','F') also boolean sein...
			//eigentlich ein Riesenmist!
			while(rs.next()){
				vec.clear();

				try{
				for(int i = 0;i<lang;i++){
					if(colTypeName.get(i).contains("VARCHAR")){
						vec.add( (rs.getString(i+1)==null ? "" : rs.getString(i+1)) );
					}else if(colTypeName.get(i).equals("BOOLEAN")){
						vec.add( (rs.getString(i+1)==null ?  Boolean.FALSE : (rs.getString(i+1).equals("T") ? Boolean.TRUE : Boolean.FALSE)) );
					}else if(colTypeName.get(i).contains("DECIMAL")){
						if(rs.getBigDecimal(i+1) == null){
							vec.add(Double.parseDouble("0.00"));
						}else{
							vec.add(rs.getBigDecimal(i+1).doubleValue());	
						}
					}else if(colTypeName.get(i).contains("tinyint(")){
						vec.add(rs.getInt(i+1));
					}else if(colTypeName.get(i).contains("INT")){
						vec.add(rs.getInt(i+1));
					}else if(colTypeName.get(i).contains("DATE")){
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
			for(int i = 0;i<lang;i++){
				if(colTypeName.get(i).contains("DATE")){
					tab.getColumn(i).setCellEditor(tabDateEditor);
					tab.getColumn(i).setCellRenderer(tabDateRenderer);
					
				}else if(colTypeName.get(i).contains("DECIMAL")){
					tab.getColumn(i).setCellEditor(tabDoubleEditor);
					tab.getColumn(i).setCellRenderer(tabDoubleRenderer);
				}else if(colTypeName.get(i).contains("INT")){
					tab.getColumn(i).setCellEditor(tabIntegerEditor);
					tab.getColumn(i).setCellRenderer(tabIntegerRenderer);
				}
			}
			tab.validate();
			tab.repaint();
			if(tab.getRowCount() > 0){
				tab.setRowSelectionInterval(0, 0);
			}
			jscr.validate();
			doSetAbfrageErgebnis();
			
			

		} catch (SQLException e) {
			textArea.setText(e.getMessage()+"\n"+textArea.getText());
		}finally {
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

		tabmod.addTableModelListener(this);
		
		
	}
	private void doSetAbfrageErgebnis(){
		labgefunden.setText("Abfrageergebnis: Datensätze = "+Integer.toString(tab.getRowCount())+" / Spalten = "+Integer.toString(tab.getColumnCount()));
	}
	class SqlTableModel extends DefaultTableModel{
		   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Class<?> getColumnClass(int columnIndex) {
			if(colTypeName.get(columnIndex).contains("VARCHAR")){
				return String.class;
			}else if(colTypeName.get(columnIndex).equals("BOOLEAN")){
				return Boolean.class;
			}else if(colTypeName.get(columnIndex).contains("DECIMAL")){
				return Double.class;
			}else if(colTypeName.get(columnIndex).contains("tinyint(")){
				return Integer.class;
			}else if(colTypeName.get(columnIndex).contains("INT")){
				return Integer.class;
			}else if(colTypeName.get(columnIndex).contains("DATE")){
				return Date.class;
			}else if(colTypeName.get(columnIndex).contains("longtext")){
				return String.class;
			}
		   return String.class;
	    }

		public boolean isCellEditable(int row, int col) {
			
			if(colAutoinc.get(col)){
				return false;				
			}
			return true;
		}
		   
	}
	
	private void starteExport(){
		int rowcount;
		if( (rowcount = tab.getRowCount()) <= 0){
			JOptionPane.showMessageDialog(null,"Kein Abfrageergebnis für OO.org-Export vorhanden");
			return;
		}
		int colcount = tab.getColumnCount(false);
		colVisible.clear();
		for(int i = 0; i < colcount;i++){
			colVisible.add(tab.convertColumnIndexToModel(i));
		}
		try {
			starteCalc();
			for(int i = 0;i < colVisible.size();i++){
				OOTools.doCellFontBold(cellCursor, i, 0);
				OOTools.doCellValue(cellCursor, i, 0, colName.get(colVisible.get(i)));
				if( tabmod.getColumnClass(colVisible.get(i)).toString().contains("java.lang.Double")){
					OOTools.doColNumberFormat(spreadsheetDocument, sheetName, i, i, 2);
				}
			}
			Object obj = null;
			for(int i = 0; i < rowcount;i++){
				for(int y = 0;y < colVisible.size();y++){
					obj = tabmod.getValueAt(i,colVisible.get(y));
					if(obj!=null){
						if(obj instanceof Double){
							OOTools.doCellValue(cellCursor, y, i+1, (Double) obj);
						}else if(obj instanceof Integer){
							OOTools.doCellValue(cellCursor, y, i+1, (Integer) obj);
						}else if(obj instanceof Boolean){
							OOTools.doCellValue(cellCursor, y, i+1, (String) (obj==Boolean.TRUE ? "ja" : "nein"));
						}else if(obj instanceof Date){
							try{
								
								OOTools.doCellValue(cellCursor, y, i+1, (String) datumsFormat.format(obj));								
							}catch(Exception ex){
								OOTools.doCellValue(cellCursor, y, i+1, (String)obj.toString());
							}
						}else{
							OOTools.doCellValue(cellCursor, y, i+1, (String) obj);
						}
					}
				}
			}
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
	}
	
	private void starteCalc() throws OfficeApplicationException, NOAException, NoSuchElementException, WrappedTargetException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException{
		if(!RehaSql.officeapplication.isActive()){
			RehaSql.starteOfficeApplication();
		}
		IDocumentService documentService = RehaSql.officeapplication.getDocumentService();
        IDocumentDescriptor docdescript = new DocumentDescriptor();
       	//docdescript.setHidden(true);
        docdescript.setAsTemplate(true);
		document = documentService.constructNewDocument(IDocument.CALC, docdescript);
		spreadsheetDocument = (ISpreadsheetDocument) document;
		OOTools.setzePapierFormatCalc((ISpreadsheetDocument) spreadsheetDocument, 21000, 29700);
		OOTools.setzeRaenderCalc((ISpreadsheetDocument) spreadsheetDocument, 1000,1000, 1000, 1000);
		sheetName= "Tabelle1";
		XSpreadsheets spreadsheets = spreadsheetDocument.getSpreadsheetDocument().getSheets();		
		XSpreadsheet spreadsheet1 = (XSpreadsheet)UnoRuntime.queryInterface(XSpreadsheet.class,spreadsheets.getByName(sheetName));
		cellCursor = spreadsheet1.createCursor();

	}


}	
