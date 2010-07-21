package rehaBillEdit;



import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;





import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;





import Tools.ButtonTools;
import Tools.DatFunk;
import Tools.JCompTools;
import Tools.JRtaCheckBox;
import Tools.JRtaTextField;
import Tools.RezTools;
import Tools.SqlInfo;
import Tools.TableTool;

import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.ITextTable;
import ag.ion.bion.officelayer.text.TextException;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class RehaBillPanel extends JXPanel implements ListSelectionListener, ActionListener, TableModelListener  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5545910505665721828L;
	
	RehaBillEditTab elternTab = null;
	JXPanel content = null;
	JRtaTextField[] tfs = {null,null,null,null,null,null,null,null};
	JRtaCheckBox originalChb = null;
	
	KeyListener kl = null;
	ActionListener al = null;
	
	MyOffenePostenTableModel tabmod = null;
	JXTable tab = null;
	
	DecimalFormat dcf = new DecimalFormat("###0.00");
	
	Vector<Vector<String>> feldNamen = null; 

	JLabel rnummerAlt = null;
	JLabel rbetragAlt = null;
	JLabel rbetragNeu = null;
	JLabel rdatumAlt = null;
	Double ibetragNeu = Double.parseDouble("0.00");
	int originalPositionen = -1;
	BigDecimal rechnungGesamt = null;
	JButton[] buts = {null,null,null,null};
	
	Vector<String> originalPos = new Vector<String>();
	Vector<Integer> originalAnzahl = new Vector<Integer>();
	Vector<Double> einzelPreis = new Vector<Double>();
	Vector<Double> gesamtPreis = new Vector<Double>();
	Vector<String> originalId = new Vector<String>();
	Vector<String> originalLangtext = new Vector<String>();

 
	HashMap<String,String> hmAdresse = new HashMap<String,String>(); 
	
	ITextTable textTable = null;
	ITextTable textEndbetrag = null;
	ITextDocument textDocument = null;
	int aktuellePosition = 0;
	int patKilometer = 0;	

	
	public RehaBillPanel(RehaBillEditTab eltern){
		super();
		elternTab = eltern;
		setLayout(new BorderLayout());
		activateActionListener();
		add(getContent(),BorderLayout.CENTER);
	}
	
	private JXPanel getContent(){
		String xwerte = "fill:0:grow(0.33),fill:0:grow(0.66)";
		String ywerte = "0dlu,250dlu,0dlu";
		
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		content = new JXPanel();
		content.setLayout(lay);
		content.add(getEdits(),cc.xy(1,2));
		content.add(getTable(),cc.xy(2,2,CellConstraints.DEFAULT,CellConstraints.TOP));
		
		return content;
	}
	private JXPanel getEdits(){
		
		//                1    2    3     4        5    6     7
		String xwerte = "5dlu,60dlu,2dlu,100dlu:g,2dlu,25dlu,5dlu";
		//                1    2  3   4  5  6   7   8  9  10  11 12 13   14 15  16  17  18 19   20  21 22  23 24  25  26 27
		String ywerte = "15dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,10dlu,p,2dlu,p,10dlu,p,15dlu,p,2dlu,p,2dlu,p,2dlu,p,15dlu";
		
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		JXPanel jpan = new JXPanel();
		jpan.setLayout(lay);
		
		try{
		JLabel lab = new JLabel("finde RNr.:");
		jpan.add(lab,cc.xy(2,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tfs[0] = new JRtaTextField("nix",true);
		jpan.add(tfs[0],cc.xy(4,2));
		
		JButton but = ButtonTools.macheButton("los...", "suchen", al);
		jpan.add(but,cc.xy(6,2));
		
		lab = new JLabel("Name1");
		jpan.add(lab,cc.xy(2,4,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tfs[1] = new JRtaTextField("nix",true);
		jpan.add(tfs[1],cc.xyw(4,4,3));
		
		lab = new JLabel("Name2");
		jpan.add(lab,cc.xy(2,6,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tfs[2] = new JRtaTextField("nix",true);
		jpan.add(tfs[2],cc.xyw(4,6,3));

		lab = new JLabel("Strasse");
		jpan.add(lab,cc.xy(2,8,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tfs[3] = new JRtaTextField("nix",true);
		jpan.add(tfs[3],cc.xyw(4,8,3));
		
		lab = new JLabel("PLZ");
		jpan.add(lab,cc.xy(2,10,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tfs[4] = new JRtaTextField("nix",true);
		jpan.add(tfs[4],cc.xyw(4,10,3));
		
		lab = new JLabel("Ort");
		jpan.add(lab,cc.xy(2,12,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tfs[5] = new JRtaTextField("nix",true);
		jpan.add(tfs[5],cc.xyw(4,12,3));

		lab = new JLabel("Originaldatum");
		jpan.add(lab,cc.xy(2,14,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tfs[6] = new JRtaTextField("DATUM",true);
		jpan.add(tfs[6],cc.xyw(4,14,3));

		originalChb = new JRtaCheckBox("Originaldatum für Ausdruck verwenden");
		jpan.add(originalChb,cc.xyw(4,16,3));
		
		but = ButtonTools.macheButton("Rechnung in OpenOffice starten", "drucken", al);
		jpan.add(but,cc.xyw(4,18,3));
		/*
		String rbetragAlt = "";
		String rbetragNeu = "";
		String rdatumAlt = "";
		*/

		lab = new JLabel("R-Nummer:");
		jpan.add(lab,cc.xy(2,20));
		rnummerAlt = new JLabel("");
		jpan.add(rnummerAlt,cc.xy(4,20));
		
		lab = new JLabel("R-Betrag alt:");
		jpan.add(lab,cc.xy(2,22));
		rbetragAlt = new JLabel("0,00");
		jpan.add(rbetragAlt,cc.xy(4,22));

		lab = new JLabel("R-Betrag neu:");
		jpan.add(lab,cc.xy(2,24));
		rbetragNeu = new JLabel("0,00");
		jpan.add(rbetragNeu,cc.xy(4,24));
		/*
		lab = new JLabel("R-Datum alt:");
		jpan.add(lab,cc.xy(2,26));
		rdatumAlt = new JLabel("  .  .    ");
		jpan.add(rdatumAlt,cc.xy(4,26));
		*/
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		jpan.validate();
		return jpan;
	}
	private JXPanel getTable(){
		
		String xwerte = "fill:0:grow(1.0)";
		String ywerte = "22dlu,155dlu:g,5dlu,p,5dlu";
		
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		JXPanel jpan = new JXPanel();
		jpan.setLayout(lay);
		
		while(!RehaBillEdit.DbOk){
			
		}
		tabmod = new MyOffenePostenTableModel();
		feldNamen = SqlInfo.holeFelder("describe faktura");
		//Vector<Vector<String>> felder = SqlInfo.holeFelder("describe faktura");
		System.out.println(feldNamen);
		System.out.println(feldNamen.size());
		String[] spalten = new String[feldNamen.size()];
		for(int i= 0; i < feldNamen.size();i++){
			spalten[i] = feldNamen.get(i).get(0);
		}
		tabmod.setColumnIdentifiers(spalten);
		tab = new JXTable(tabmod);
		tab.setHorizontalScrollEnabled(true);
		DateTableCellEditor tble = new DateTableCellEditor();


		for(int i = 0; i < feldNamen.size();i++){
			if(feldNamen.get(i).get(1).contains("decimal(")){
				tab.getColumn(i).setCellRenderer(new Tools.DoubleTableCellRenderer());
				tab.getColumn(i).setCellEditor(new Tools.DblCellEditor());
			}else if(feldNamen.get(i).get(1).contains("date")){
				tab.getColumn(i).setCellEditor(tble);
			}
		}
		/*
		tab.getColumn(0).setCellRenderer(new Tools.MitteRenderer());
		tab.getColumn(4).setCellRenderer(new Tools.MitteRenderer());
		tab.getColumn(5).setCellRenderer(new Tools.DoubleTableCellRenderer());
		tab.getColumn(6).setCellRenderer(new Tools.DoubleTableCellRenderer());
		tab.getColumn(8).setCellRenderer(new Tools.DoubleTableCellRenderer());
		tab.getColumn(5).setCellEditor(new Tools.DblCellEditor());
		tab.getColumn(6).setCellEditor(new Tools.DblCellEditor());
		tab.getColumn(8).setCellEditor(new Tools.DblCellEditor());
		*/
		tab.getSelectionModel().addListSelectionListener( new BillListSelectionHandler());
		
		tab.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.CLASSIC_LINE_PRINTER));
		tabmod.addTableModelListener(this);
		
		
		JScrollPane jscr = JCompTools.getTransparentScrollPane(tab);
		jpan.add(jscr,cc.xy(1,2));
		
		String xwerte2 = "0dlu,60dlu,5dlu,60dlu,5dlu,60dlu,15dlu,60dlu";
		String ywerte2 = "0dlu,p,5dlu";
		FormLayout lay2 = new FormLayout(xwerte2,ywerte2);
		CellConstraints cc2 = new CellConstraints();
		JXPanel butpan = new JXPanel();
		butpan.setLayout(lay2);
		butpan.add( buts[0]=ButtonTools.macheButton("+", "zeileneu", al),cc2.xy(2, 2));
		butpan.add( buts[1]=ButtonTools.macheButton("-", "zeileloeschen", al),cc2.xy(4, 2));
		butpan.add( buts[2]=ButtonTools.macheButton("in DB speichern", "indbspeichern", al),cc2.xy(6, 2));
		butpan.add( buts[3]=ButtonTools.macheButton("abbrechen", "abbrechen", al),cc2.xy(8, 2));
		buts[0].setEnabled(false);
		buts[1].setEnabled(false);
		buts[2].setEnabled(false);
		buts[3].setEnabled(false);
		butpan.validate();
		jpan.add(butpan,cc.xy(1,4));
		jpan.validate();
		return jpan;
	}
	
	private void activateActionListener(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("suchen")){
					try{
						starteSuche();	
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
				if(cmd.equals("drucken")){
					try{
						System.out.println("Druck wird gestartet");
						starteDrucken();	
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
				if(cmd.equals("zeileneu")){
					try{
						doZeileNeu();
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
				if(cmd.equals("zeileloeschen")){
					try{
						doZeileLoeschen();
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
				if(cmd.equals("indbspeichern")){
					try{
						doInDbSpeichern();
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
				if(cmd.equals("abbrechen")){
					try{
						doAbbrechen();
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
			}
			
		};
	}
	private void doAbbrechen(){
		for(int i = 0; i < tabmod.getRowCount();i++){
			if( ((Integer)tabmod.getValueAt(i, tabmod.getColumnCount()-1)) == -1){
				int tabrow = tab.convertRowIndexToView(i);
				tab.setRowSelectionInterval(tabrow,tabrow);
				TableTool.loescheRowAusModel(tab, i);
				break;
			}
		}
		buts[0].setEnabled(true);
		buts[1].setEnabled(true);
		buts[3].setEnabled(false);
		starteSuche();
		rechneNeu(false);
	}
	private void doZeileLoeschen(){
		int anfrage = JOptionPane.showConfirmDialog(null, "Sie sind im Begriff die´ausgewählte Position unwiederbringlich\n"+
				"aus der Tabelle 'Faktura' zu löschen!\n\nWollen Sie das wirklich?\n", "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
		if(anfrage == JOptionPane.YES_OPTION){
			int row = tab.getSelectedRow();
			if(row < 0){
				return;
			}
			String id =tabmod.getValueAt(tab.convertRowIndexToModel(row),tabmod.getColumnCount()-1).toString().replace(".", "");
			String cmd = "delete from faktura where id='"+id+"' LIMIT 1";
			System.out.println(cmd);
			SqlInfo.sqlAusfuehren(cmd);
			TableTool.loescheRowAusModel(tab, row);
			rechneNeu(false);
			cmd = "update rliste set r_betrag='"+dcf.format(rechnungGesamt).replace(",",".")+"', r_offen='"+dcf.format(rechnungGesamt).replace(",",".")+"' where r_nummer='"+rnummerAlt.getText().trim()+"' LIMIT 1";
			System.out.println(cmd);
			SqlInfo.sqlAusfuehren(cmd);
			for(int i = 0;i < tabmod.getRowCount();i++){
				tabmod.setValueAt(i, tabmod.getRowCount()-1, 5);
			}
		}

	}
	private void doZeileNeu(){
		int row = tab.getSelectedRow();
		if(row < 0){
			return;
		}
		buts[0].setEnabled(false);
		buts[1].setEnabled(false);
		buts[3].setEnabled(true);
		int modvec = tab.convertRowIndexToModel(row);
		Vector<Vector<Object>> vec = (Vector<Vector<Object>>) tabmod.getDataVector();
		tabmod.addRow( (Vector<?>) vec.get(modvec).clone());
		tabmod.setValueAt(tabmod.getRowCount()-1, tabmod.getRowCount()-1, 5);
		tabmod.setValueAt(-1, tabmod.getRowCount()-1, tabmod.getColumnCount()-1);
		rechneNeu(false);
	}
	private void doInDbSpeichern(){
		String cmd = "";
		for(int i = 0; i < tab.getRowCount();i++){
			if( ((Integer)tab.getValueAt(i, tab.getColumnCount()-1))  < 0 ){
				cmd = neuFaktura(i);
			}else{
				cmd = altFaktura(i);
			}
			System.out.println(cmd);
			SqlInfo.sqlAusfuehren(cmd);
		}
		cmd = "update rliste set r_betrag='"+dcf.format(rechnungGesamt).replace(",",".")+"', r_offen='"+dcf.format(rechnungGesamt).replace(",",".")+"' where r_nummer='"+rnummerAlt.getText().trim()+"' LIMIT 1";
		//cmd = "update rliste set r_betrag='"+dcf.format(rechnungGesamt).replace(",",".")+"' where r_nummer='"+rnummerAlt.getText().trim()+"' LIMIT 1";
		System.out.println(cmd);
		SqlInfo.sqlAusfuehren(cmd);

		buts[0].setEnabled(true);
		buts[1].setEnabled(true);
		buts[3].setEnabled(false);

		
	}
	private String neuFaktura(int row){
		StringBuffer buf = new StringBuffer();
		buf.append("insert into faktura set ");
		for(int i = 0; i < (feldNamen.size()-1);i++){
			if(i ==0 ){
				buf.append(feldNamen.get(i).get(0)+"='"+retWert(row,i)+"'");
			}else{
					buf.append(", "+feldNamen.get(i).get(0)+"='"+retWert(row,i)+"'");					
			}
		}
		return buf.toString();
	}
	private String altFaktura(int row){
		StringBuffer buf = new StringBuffer();
		buf.append("update faktura set ");
		for(int i = 0; i < (feldNamen.size()-1);i++){
			if(i ==0 ){
				buf.append(feldNamen.get(i).get(0)+"='"+retWert(row,i)+"'");
			}else{
				buf.append(", "+feldNamen.get(i).get(0)+"='"+retWert(row,i)+"'");
			}
		}
		buf.append(" where id='"+retWert(row,tabmod.getColumnCount()-1)+"' LIMIT 1");
		return buf.toString();
		
	}
	private String retWert(int row,int col){
		String value = "";
	
		if( tabmod.getColumnClass(col) == Boolean.class){
			value = (tabmod.getValueAt(row,col) == Boolean.FALSE ? "F" : "T");
		}else if(tabmod.getColumnClass(col) == Date.class){
			if(tabmod.getValueAt(row,col)==null ){
				value =  "1900-01-01";
			}else{
				String test = tabmod.getValueAt(row,col).toString();
				if(test.contains(".")){
					value = DatFunk.sDatInSQL(test);
				}else{
					value = test;
				}
			}
		}else if(tabmod.getColumnClass(col) == Double.class){
			value = dcf.format((Double)tabmod.getValueAt(row,col)).replace(",",".");
		}else if(tabmod.getColumnClass(col) == Integer.class){
			value = Integer.toString((Integer)tabmod.getValueAt(row,col));
		}else if(tabmod.getColumnClass(col) == String.class){
			value = tabmod.getValueAt(row,col).toString();
		}	
		return value;
	}

	private void starteSuche(){
		String c = tfs[0].getText().trim();
		if(c.equals("")){
			return;
		}
		String cmd = "select * from faktura where rnummer='"+c+"' order by lfnr";
		try{
			suchen(cmd);
			bestueckeLabels( (tab.getRowCount() > 0) );
			rechneNeu(false);
			if(tab.getRowCount() > 0){
				buts[0].setEnabled(true);
				buts[1].setEnabled(true);
				buts[2].setEnabled(true);
				buts[3].setEnabled(false);
			}else{
				buts[0].setEnabled(false);
				buts[1].setEnabled(false);
				buts[2].setEnabled(false);
				buts[3].setEnabled(false);
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Fehler in suchen(cmd)");
		}
	}
	@Override
	public void valueChanged(ListSelectionEvent arg0) {
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
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
			
			if(col != 40){
				return true;				
			}
			return false;
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

	@Override
	public void tableChanged(TableModelEvent arg0) {
		if(arg0.getType() == TableModelEvent.INSERT){
			System.out.println("Insert");
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
				}else if(tabmod.getColumnClass(col) == Date.class){
					value = tabmod.getValueAt(row,col).toString();
				}else if(tabmod.getColumnClass(col) == Double.class){
					value = dcf.format((Double)tabmod.getValueAt(row,col)).replace(",",".");
				}else if(tabmod.getColumnClass(col) == Integer.class){
					value = Integer.toString((Integer)tabmod.getValueAt(row,col));
				}else if(tabmod.getColumnClass(col) == String.class){
					value = tabmod.getValueAt(row,col).toString();
				}
				rechneNeu(false);
			
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
	private void bestueckeLabels(boolean mitwerten){
		if(mitwerten){
			String cmd = "select r_datum,r_betrag from rliste where r_nummer='"+tfs[0].getText().trim()+"' LIMIT 1";
			Vector<Vector<String>> vec = SqlInfo.holeFelder(cmd);
			if(vec.size() > 0){
				rbetragAlt.setText(vec.get(0).get(1).replace(".",","));
				//rdatumAlt.setText(DatFunk.sDatInDeutsch(vec.get(0).get(0)));
				rbetragNeu.setText(dcf.format(ibetragNeu));
				rnummerAlt.setText(tfs[0].getText().trim());
				for(int i = 0;i <5;i++){
					tfs[i+1].setText(tabmod.getValueAt(0, i).toString());
				}
				tfs[6].setText(DatFunk.sDatInDeutsch(vec.get(0).get(0)));
			}else{
				rbetragAlt.setText("0,00");
				//rdatumAlt.setText("  .  .    ");
				rbetragNeu.setText("0,00");
				rnummerAlt.setText("");
				for(int i = 0;i <5;i++){
					tfs[i+1].setText("");
				}
				tfs[6].setText("  .  .    ");
			}
		}else{
			rbetragAlt.setText("0,00");
			//rdatumAlt.setText("  .  .    ");
			rbetragNeu.setText("0,00");
			rnummerAlt.setText("");
			for(int i = 0;i <5;i++){
				tfs[i+1].setText("");
			}
			tfs[6].setText("  .  .    ");
		}
	}
	private void suchen(String sstmt){
		tabmod.setRowCount(0);
		tab.validate();
		tab.repaint();
		Statement stmt = null;
		ResultSet rs = null;

			
		try {
			stmt =  RehaBillEdit.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			
			
			rs = stmt.executeQuery(sstmt);
			Vector<Object> vec = new Vector<Object>();
			int durchlauf = 0;
			int lang = feldNamen.size();

			while(rs.next()){
				vec.clear();
				try{
				for(int i = 0;i<lang;i++){
					if(feldNamen.get(i).get(1).contains("varchar(")){
						vec.add( (rs.getString(i+1)==null ? "" : rs.getString(i+1)) );
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
			System.out.println("SQLException: " + ev.getMessage());
			System.out.println("SQLState: " + ev.getSQLState());
			System.out.println("VendorError: " + ev.getErrorCode());
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
	private void rechneNeu(boolean inDBschreiben){
		tabmod.removeTableModelListener(this);
		BigDecimal reiheEinzel = BigDecimal.valueOf(Double.parseDouble("0.00"));
		BigDecimal reiheGesamt = BigDecimal.valueOf(Double.parseDouble("0.00"));
		rechnungGesamt = BigDecimal.valueOf(Double.parseDouble("0.00"));
		BigDecimal reiheAnzahl = BigDecimal.valueOf(Double.parseDouble("0.00"));
		String cmd= "";
		String id = "";
		for(int i = 0; i < tabmod.getRowCount();i++){

			id = tabmod.getValueAt(i,tabmod.getColumnCount()-1).toString().replace(".","");
			reiheAnzahl = BigDecimal.valueOf(Double.parseDouble( tabmod.getValueAt(i,11).toString()  ));
			reiheEinzel = BigDecimal.valueOf( (Double) tabmod.getValueAt(i,12) );
			reiheGesamt = reiheAnzahl.multiply(reiheEinzel);
			rechnungGesamt = rechnungGesamt.add(reiheGesamt);
			tabmod.setValueAt(reiheGesamt.doubleValue(),i,14);
			tabmod.setValueAt(reiheGesamt.doubleValue(),i,17);
			if(inDBschreiben){
				cmd = "update faktura set anzahl='"+
				Integer.toString(reiheAnzahl.intValue())+"', preis='"+
				dcf.format(reiheEinzel).replace(",", ".")+"', gesamt='"+
				dcf.format(reiheGesamt).replace(",", ".")+"', netto='"+
				dcf.format(reiheGesamt).replace(",", ".")+"' where id='"+id+"' LIMIT 1";
				System.out.println(cmd);
				SqlInfo.sqlAusfuehren(cmd);
			}
		}
		System.out.println(rechnungGesamt);
		rbetragNeu.setText(dcf.format(rechnungGesamt));
		if(inDBschreiben){
			cmd = "update rliste set r_betrag='"+dcf.format(rechnungGesamt).replace(",",".")+"', r_offen='"+dcf.format(rechnungGesamt).replace(",",".")+"' where r_nummer='"+rnummerAlt.getText().trim()+"' LIMIT 1";
//			cmd = "update rliste set r_betrag='"+dcf.format(rechnungGesamt).replace(",", ".")+"' where r_nummer='"+
//			rnummerAlt.getText().trim()+"' LIMIT 1";
//			System.out.println(cmd);
//			SqlInfo.sqlAusfuehren(cmd);
		}
		tabmod.addTableModelListener(this);
	}
	
	private void starteDrucken(){
		try{
			if(tab.getRowCount()<=0){
				JOptionPane.showMessageDialog(null,"Depp - deppader!\n\nRowCount()==0\n");
				return;
			}
			String kassenid = Integer.toString( ((Integer)tabmod.getValueAt(0, 32)) );
			String disziplin = ((String)tabmod.getValueAt(0, 38)).trim().toLowerCase();
			String pgruppe = SqlInfo.holeEinzelFeld("select preisgruppe from kass_adr where id='"+kassenid+"' LIMIT 1");
			if(pgruppe.equals("")){
				JOptionPane.showMessageDialog(null, "Kann Preisgruppe nicht ermitteln");
				return;
			}
			RehaBillEdit.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			int preisgruppe = Integer.parseInt(pgruppe);
			String test = ((String)tabmod.getValueAt(0, 0)).trim();
			String cmd = "select * from "+disziplin.toLowerCase()+"tarif"+preisgruppe;
			System.out.println(cmd);
			Vector<Vector<String>> preisvec = SqlInfo.holeFelder(cmd);
			if(test.equalsIgnoreCase("Herr") || test.equalsIgnoreCase("Frau") ){
				//es ist eine Rechnung an Privatpersonen
				macheHashMap("privat", (! disziplin.equals("rh")) , preisvec);
			}else{
				//es ist eine Rechnung an den Kostenträger
				macheHashMap("institution", (! disziplin.equals("rh")), preisvec);
			}
			RehaBillEdit.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private void macheHashMap(String fuerwen,boolean heilmittel, Vector<Vector<String>> preisvec){
		try{
		originalPos.clear();
		originalAnzahl.clear();
		einzelPreis.clear();
		gesamtPreis.clear();
		originalId.clear();
		originalLangtext.clear();
		aktuellePosition = 0;
		if( fuerwen.equals("privat") && heilmittel ){
			try{
			System.out.println("in privat und heilmittel");
			hmAdresse.put("<pri1>",((String)tabmod.getValueAt(0, 0)).trim() );
			hmAdresse.put("<pri2>",((String)tabmod.getValueAt(0, 1)).trim() );
			hmAdresse.put("<pri3>",((String)tabmod.getValueAt(0, 2)).trim() );
			hmAdresse.put("<pri4>",((String)tabmod.getValueAt(0, 3)).trim()+" "+((String)tabmod.getValueAt(0, 4)).trim() );
			hmAdresse.put("<pri5>",rnummerAlt.getText() );
			hmAdresse.put("<pri6>",(originalChb.isSelected() ? tfs[6].getText().trim() : DatFunk.sHeute()) );
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			for(int i = 0; i < tabmod.getRowCount();i++){
				try{
					int id = ((Integer)tabmod.getValueAt(i, 8));
					String langtext = RezTools.getLangtextFromID(Integer.toString(id), preisvec).replace("30Min.", "").replace("45Min.", "");
					//System.out.println(langtext);
					String preis = RezTools.getPreisAktFromID(Integer.toString(id), preisvec);
					//System.out.println(preis);
					originalLangtext.add(langtext);
					originalAnzahl.add( ((Integer)tabmod.getValueAt(i, 11)) );
					einzelPreis.add( ((Double)tabmod.getValueAt(i, 12)) );
					gesamtPreis.add( ((Double)tabmod.getValueAt(i, 17)) );
					aktuellePosition++;	
					
				}catch(Exception ex){
					ex.printStackTrace();
				}

			}
			try {
				System.out.println(originalAnzahl);
				System.out.println(einzelPreis);
				System.out.println(gesamtPreis);
				System.out.println(rechnungGesamt);
				System.out.println(RehaBillEdit.hmRechnungPrivat);
				starteDokument( RehaBillEdit.progHome+"vorlagen/"+RehaBillEdit.aktIK+"/"+RehaBillEdit.hmRechnungPrivat );
				System.out.println(RehaBillEdit.progHome+"vorlagen/"+RehaBillEdit.aktIK+"/"+RehaBillEdit.hmRechnungPrivat);
				starteErsetzen();
				aktuellePosition = 0;
				startePositionen();
				textDocument.getFrame().getXFrame().getContainerWindow().setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}

			
		}else if( fuerwen.equals("institution") && heilmittel ){
			try{
				System.out.println("in privat und heilmittel");
				hmAdresse.put("<pri1>",((String)tabmod.getValueAt(0, 0)).trim() );
				hmAdresse.put("<pri2>",((String)tabmod.getValueAt(0, 1)).trim() );
				hmAdresse.put("<pri3>",((String)tabmod.getValueAt(0, 2)).trim() );
				hmAdresse.put("<pri4>",((String)tabmod.getValueAt(0, 3)).trim()+" "+((String)tabmod.getValueAt(0, 4)).trim() );
				hmAdresse.put("<pri5>",rnummerAlt.getText() );
				hmAdresse.put("<pri6>",(originalChb.isSelected() ? tfs[6].getText().trim() : DatFunk.sHeute()) );
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
				for(int i = 0; i < tabmod.getRowCount();i++){
					try{
						int id = ((Integer)tabmod.getValueAt(i, 8));
						String langtext = RezTools.getLangtextFromID(Integer.toString(id), preisvec).replace("30Min.", "").replace("45Min.", "");
						//System.out.println(langtext);
						String preis = RezTools.getPreisAktFromID(Integer.toString(id), preisvec);
						//System.out.println(preis);
						originalLangtext.add(langtext);
						originalAnzahl.add( ((Integer)tabmod.getValueAt(i, 11)) );
						einzelPreis.add( ((Double)tabmod.getValueAt(i, 12)) );
						gesamtPreis.add( ((Double)tabmod.getValueAt(i, 17)) );
						aktuellePosition++;	
						
					}catch(Exception ex){
						ex.printStackTrace();
					}

				}
				try {
					System.out.println(originalAnzahl);
					System.out.println(einzelPreis);
					System.out.println(gesamtPreis);
					System.out.println(rechnungGesamt);
					System.out.println(RehaBillEdit.hmRechnungPrivat);
					starteDokument( RehaBillEdit.progHome+"vorlagen/"+RehaBillEdit.aktIK+"/"+RehaBillEdit.hmRechnungPrivat );
					System.out.println(RehaBillEdit.progHome+"vorlagen/"+RehaBillEdit.aktIK+"/"+RehaBillEdit.hmRechnungPrivat);
					starteErsetzen();
					aktuellePosition = 0;
					startePositionen();
					textDocument.getFrame().getXFrame().getContainerWindow().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			
		}else if( fuerwen.equals("privat") && (!heilmittel) ){
			
		}else if( fuerwen.equals("institution") && (!heilmittel) ){
			
		}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}

	
	public void starteDokument(String url) throws Exception{
		IDocumentService documentService = null;;
		documentService = RehaBillEdit.officeapplication.getDocumentService();
		IDocumentDescriptor docdescript = new DocumentDescriptor();
        docdescript.setHidden(true);
        docdescript.setAsTemplate(true);
		IDocument document = null;
		document = documentService.loadDocument(url,docdescript);
		/**********************/
		textDocument = (ITextDocument)document;
		textTable = textDocument.getTextTableService().getTextTable("Tabelle1");
		textEndbetrag = textDocument.getTextTableService().getTextTable("Tabelle2");
	}
	private void starteErsetzen(){
		ITextFieldService textFieldService = textDocument.getTextFieldService();
		ITextField[] placeholders = null;
		try {
			placeholders = textFieldService.getPlaceholderFields();
		} catch (TextException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < placeholders.length; i++) {
			if(placeholders[i].getDisplayText().toLowerCase().equals("<pri1>")){
				placeholders[i].getTextRange().setText(hmAdresse.get("<pri1>"));
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<pri2>")){
				placeholders[i].getTextRange().setText(hmAdresse.get("<pri2>"));				
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<pri3>")){
				placeholders[i].getTextRange().setText(hmAdresse.get("<pri3>"));				
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<pri4>")){
				placeholders[i].getTextRange().setText(hmAdresse.get("<pri4>"));				
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<pri5>")){
				placeholders[i].getTextRange().setText(hmAdresse.get("<pri5>"));				
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<pri6>")){
				placeholders[i].getTextRange().setText(hmAdresse.get("<pri6>"));
			}
		}
		
	}
	private void startePositionen() throws TextException{
		//ITextTableCell[] tcells;// = null;
		//Vector<BigDecimal> einzelpreis = new Vector<BigDecimal>();
		//Vector<BigDecimal> gesamtpreis = new Vector<BigDecimal>();
		aktuellePosition++;	 
		for(int i = 0; i < originalAnzahl.size();i++){
			//tcells = textTable.getRow(aktuellePosition).getCells();
			textTable.getCell(0,aktuellePosition).getTextService().getText().setText(originalLangtext.get(i));
			textTable.getCell(1,aktuellePosition).getTextService().getText().setText(Integer.toString(originalAnzahl.get(i)));
			textTable.getCell(2,aktuellePosition).getTextService().getText().setText(dcf.format(einzelPreis.get(i)));
			//BigDecimal zeilengesamt = BigDecimal.valueOf(einzelPreis.get(i)).multiply(BigDecimal.valueOf(Double.valueOf(Integer.toString(originalAnzahl.get(i)))));
			//zeilenGesamt.add(BigDecimal.valueOf(zeilengesamt.doubleValue()));
			//rechnungGesamt = rechnungGesamt.add(BigDecimal.valueOf(zeilenGesamt.get(i).doubleValue()));
			
			textTable.getCell(3,aktuellePosition).getTextService().getText().setText(dcf.format(gesamtPreis.get(i)));
			//if(i < (originalAnzahl.size()-1) ){
				textTable.addRow(1);
				aktuellePosition++;				
			//}
		}
		//tcells = textEndbetrag.getRow(0).getCells();
		textEndbetrag.getCell(1,0).getTextService().getText().setText(dcf.format(rechnungGesamt.doubleValue())+" EUR");

	}

}