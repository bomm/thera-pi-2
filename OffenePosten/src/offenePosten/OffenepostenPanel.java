package offenePosten;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;
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
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import Tools.ButtonTools;
import Tools.DatFunk;
import Tools.JCompTools;
import Tools.JRtaComboBox;
import Tools.JRtaTextField;
import Tools.SqlInfo;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class OffenepostenPanel extends JXPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7883557713071422132L;

	JRtaTextField suchen = null;
	JRtaTextField offen = null;
	JRtaTextField[] tfs = {null,null,null,null};
	JButton[] buts = {null,null,null};
	JRtaComboBox combo = null;
	JXPanel content = null;
	KeyListener kl = null;
	ActionListener al = null;
	
	MyOffenePostenTableModel tabmod = null;
	JXTable tab = null;
	JLabel summeOffen;
	JLabel summeRechnung;
	JLabel summeGesamtOffen;
	JLabel anzahlSaetze;
	
	BigDecimal gesamtOffen = BigDecimal.valueOf(Double.parseDouble("0.00"));
	BigDecimal suchOffen = BigDecimal.valueOf(Double.parseDouble("0.00"));
	BigDecimal suchGesamt = BigDecimal.valueOf(Double.parseDouble("0.00"));
	DecimalFormat dcf = new DecimalFormat("###0.00");
	int gefunden;
	OffenepostenTab eltern = null;
	public OffenepostenPanel(OffenepostenTab xeltern){
		super();
		this.eltern = xeltern;
		startKeyListener();
		startActionListener();
		setLayout(new BorderLayout());
		add(getContent(),BorderLayout.CENTER);
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				setzeFocus();
			}
		});

	}
	private void setzeFocus(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				suchen.requestFocus();
			}
		});
	}
	
	private JXPanel getContent(){
		content = new JXPanel();
		//				 1     2     3    4     5     6      7    8      9    10    11   12    13   14     15  
		String xwerte = "10dlu,50dlu,2dlu,90dlu,10dlu,30dlu,1dlu,50dlu:g,5dlu,50dlu,2dlu,40dlu,2dlu,35dlu,10dlu";
		//				 1     2  3     4       5    6      7
		String ywerte = "10dlu,p,2dlu,150dlu:g,5dlu,80dlu,0dlu";	
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		content.setLayout(lay);
		
		JLabel lab = new JLabel("Suchkriterium");
		content.add(lab,cc.xy(2,2));
		String[] args = {"Rechnungsnummer =","Rechnungsnummer >","Rechnungsnummer <",
				"Rechnungsbetrag =","Rechnungsbetrag >","Rechnungsbetrag <",
				"Noch offen =","Noch offen >","Noch offen <",
				"R_Kasse =","R_Kasse enthalten in",
				"R_Name =","R_Name enthalten in",
				"Rechnungsdatum =","Rechnungsdatum >","Rechnungsdatum <"};

		combo = new JRtaComboBox(args);
		content.add(combo,cc.xy(4,2));
		
		lab = new JLabel("finde:");
		content.add(lab,cc.xy(6,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		suchen = new JRtaTextField("nix",true);
		suchen.setName("suchen");
		suchen.addKeyListener(kl);
		content.add(suchen,cc.xy(8,2));

		content.add((buts[0] = ButtonTools.macheButton("ausbuchen", "ausbuchen", al)),cc.xy(10,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		buts[0].setMnemonic('a');
		
		lab = new JLabel("noch offen:");
		content.add(lab,cc.xy(12,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tfs[0] = new JRtaTextField("F",true,"6.2","");
		tfs[0].setHorizontalAlignment(SwingConstants.RIGHT);
		tfs[0].setText("0,00");
		tfs[0].setName("offen");
		tfs[0].addKeyListener(kl);
		content.add(tfs[0],cc.xy(14,2));
		while(!OffenePosten.DbOk){
			
		}
		tabmod = new MyOffenePostenTableModel();
		Vector<Vector<String>> felder = SqlInfo.holeFelder("describe rliste");
		String[] spalten = new String[felder.size()];
		for(int i= 0; i < felder.size();i++){
			spalten[i] = felder.get(i).get(0);
		}
		tabmod.setColumnIdentifiers(spalten);
		tab = new JXTable(tabmod);
		tab.setHorizontalScrollEnabled(true);
		tab.getColumn(0).setCellRenderer(new Tools.MitteRenderer());
		tab.getColumn(4).setCellRenderer(new Tools.MitteRenderer());
		tab.getColumn(5).setCellRenderer(new Tools.DoubleTableCellRenderer());
		tab.getColumn(6).setCellRenderer(new Tools.DoubleTableCellRenderer());
		tab.getColumn(8).setCellRenderer(new Tools.DoubleTableCellRenderer());
		tab.getColumn(5).setCellEditor(new Tools.DblCellEditor());
		tab.getColumn(6).setCellEditor(new Tools.DblCellEditor());
		tab.getColumn(8).setCellEditor(new Tools.DblCellEditor());
		tab.getSelectionModel().addListSelectionListener( new OPListSelectionHandler());
		tab.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.CLASSIC_LINE_PRINTER));
		
		JScrollPane jscr = JCompTools.getTransparentScrollPane(tab);
		content.add(jscr,cc.xyw(2,4,13));
		
		JXPanel auswertung = new JXPanel();
		String xwerte2 = "10dlu,150dlu,5dlu,100dlu";
		String ywerte2 = "0dlu,p,2dlu,p,2dlu,p,2dlu,p,10dlu";			
		FormLayout lay2 = new FormLayout(xwerte2,ywerte2);
		CellConstraints cc2 = new CellConstraints();
		auswertung.setLayout(lay2);
		lab = new JLabel("Offene Posten gesamt:");
		auswertung.add(lab,cc2.xy(2,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		summeGesamtOffen = new JLabel("0,00");
		summeGesamtOffen.setForeground(Color.RED);
		auswertung.add(summeGesamtOffen,cc2.xy(4, 2));

		lab = new JLabel("Offene Posten der letzten Abfrage:");
		auswertung.add(lab,cc2.xy(2,4,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		summeOffen = new JLabel("0,00");
		summeOffen.setForeground(Color.BLUE);
		auswertung.add(summeOffen,cc2.xy(4,4));
		
		lab = new JLabel("Summe Rechnunsbetrag der letzten Abfrage:");
		auswertung.add(lab,cc2.xy(2,6,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		summeRechnung = new JLabel("0,00");
		summeRechnung.setForeground(Color.BLUE);
		auswertung.add(summeRechnung,cc2.xy(4,6));

		lab = new JLabel("Anzahl Datensätze der letzten Abfrage:");
		auswertung.add(lab,cc2.xy(2,8,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		anzahlSaetze = new JLabel("0");
		anzahlSaetze.setForeground(Color.BLUE);
		auswertung.add(anzahlSaetze,cc2.xy(4,8));
		
		content.add(auswertung,cc.xyw(1,6,15));
		content.validate();
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				ermittleGesamtOffen();
				return null;
			}
			
		}.execute();
		
		return content;
	}
	
	private void startKeyListener(){
		kl = new KeyListener(){
			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode()==10){
					arg0.consume();
					if( ((JComponent)arg0.getSource()).getName().equals("suchen")){
						sucheEinleiten();
						return;
					}else if( ((JComponent)arg0.getSource()).getName().equals("offen") ){
						setzeFocus();
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
	private void startActionListener(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("ausbuchen")){
					doAusbuchen();
					setzeFocus();
				}
			}
			
		};
	}
	private void sucheEinleiten(){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					OffenePosten.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					setzeFocus();
					doSuchen();
					schreibeAbfrage();
					suchen.setEnabled(true);
					buts[0].setEnabled(true);

				}catch(Exception ex){
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null,"Fehler beim einlesen der Datensätze");
					OffenePosten.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					setzeFocus();
					suchen.setEnabled(true);
					buts[0].setEnabled(true);
				}
				OffenePosten.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				setzeFocus();
				return null;
			}
		}.execute();
	}
	private void doAusbuchen(){
		if(!tfs[0].getText().equals("0,00")){
			int anfrage = JOptionPane.showConfirmDialog(null, "Wollen Sie den Restbetrag von "+tfs[0].getText()+" in der Rechnung speichern","Benutzeranfrage", JOptionPane.YES_NO_OPTION);
			if( ! (anfrage == JOptionPane.YES_OPTION) ){
				return;
			}
		}
		int row = tab.getSelectedRow();
		if(row < 0){
			JOptionPane.showMessageDialog(null, "Keine Rechnung zum Ausbuchen ausgewählt");
			return;
		}
		String offeninTabelle = dcf.format((Double)tabmod.getValueAt(tab.convertRowIndexToModel(row), 6));
		if(offeninTabelle.equals("0,00") && tfs[0].getText().equals("0,00")){
			JOptionPane.showMessageDialog(null,"Diese Rechnung ist bereits auf bezahlt gesetzt");
			return;
		}
		suchOffen = suchOffen.subtract( BigDecimal.valueOf((Double)tabmod.getValueAt(tab.convertRowIndexToModel(row), 6)) );
		suchOffen = suchOffen.add( BigDecimal.valueOf(Double.parseDouble(tfs[0].getText().replace(",", ".")) ) );

		gesamtOffen = gesamtOffen.subtract( BigDecimal.valueOf((Double)tabmod.getValueAt(tab.convertRowIndexToModel(row), 6)) );
		gesamtOffen = gesamtOffen.add( BigDecimal.valueOf(Double.parseDouble(tfs[0].getText().replace(",", ".")) ) );

		
		tabmod.setValueAt(Double.parseDouble(tfs[0].getText().replace(",", ".")), tab.convertRowIndexToModel(row), 6);


		int id = (Integer) tabmod.getValueAt(tab.convertRowIndexToModel(row), 15);
		String cmd = "update rliste set r_offen='"+tfs[0].getText().replace(",", ".")+"', r_bezdatum='"+
		DatFunk.sDatInSQL(DatFunk.sHeute())+"' where id ='"+Integer.toString(id)+"' LIMIT 1";
		
		if(!OffenePosten.testcase){
			SqlInfo.sqlAusfuehren(cmd);			
		}
		schreibeAbfrage();			
		tfs[0].setText("0,00");			
	}

	private void ermittleGesamtOffen(){
		Vector<Vector<String>> offen = SqlInfo.holeFelder("select sum(r_offen) from rliste where r_offen > '0.00'");
		gesamtOffen = BigDecimal.valueOf( Double.parseDouble(offen.get(0).get(0)) );
		schreibeGesamtOffen();
	}
	private void schreibeAbfrage(){
		schreibeGesamtOffen();
		summeOffen.setText(dcf.format(suchOffen));
		summeRechnung.setText(dcf.format(suchGesamt));
		anzahlSaetze.setText(Integer.toString(gefunden));
	}
	private void schreibeGesamtOffen(){
		summeGesamtOffen.setText( dcf.format(gesamtOffen) );
	}
	
	

	
	private void doSuchen(){
		if(suchen.getText().trim().equals("")){
			return;
		}
		int suchart = combo.getSelectedIndex();
		String cmd = "";
		/*
						//  0                     1                  2
		String[] args = {"Rechnungsnummer =","Rechnungsnummer >","Rechnungsnummer <",
				//     3                  4                  5
				"Rechnungsbetrag =","Rechnungsbetrag >","Rechnungsbetrag <",
				//   6               7            8
				"Noch offen =","Noch offen >","Noch offen <",
				//    9                  10
				"Feld-Kasse =","Feld-Kasse enthalten in",
				//    11                  12
				"Feld-Name =","Feld-Name enthalten in",
				//    13                  14                15
				"Rechnungsdatum =","Rechnungsdatum >","Rechnungsdatum <"};
		*/		
		try{
		switch(suchart){
		case 0:
			cmd = "select * from rliste where r_nummer ='"+suchen.getText().trim()+"'";
			break;
		case 1:
			cmd = "select * from rliste where r_nummer >'"+suchen.getText().trim()+"'";
			break;
		case 2:
			cmd = "select * from rliste where r_nummer <'"+suchen.getText().trim()+"'";
			break;
		case 3:
			cmd = "select * from rliste where r_betrag ='"+suchen.getText().trim().replace(",", ".")+"'";
			break;
		case 4:
			cmd = "select * from rliste where r_betrag >'"+suchen.getText().trim().replace(",", ".")+"'";
			break;
		case 5:
			cmd = "select * from rliste where r_betrag <'"+suchen.getText().trim().replace(",", ".")+"'";
			break;
		case 6:
			cmd = "select * from rliste where r_offen ='"+suchen.getText().trim().replace(",", ".")+"'";
			break;
		case 7:
			cmd = "select * from rliste where r_offen >'"+suchen.getText().trim().replace(",", ".")+"'";
			break;
		case 8:
			cmd = "select * from rliste where r_offen <'"+suchen.getText().trim().replace(",", ".")+"'";
			break;
		case 9:
			cmd = "select * from rliste where r_kasse ='"+suchen.getText().trim().replace(",", ".")+"'";
			break;
		case 10:
			cmd = "select * from rliste where r_kasse like'%"+suchen.getText().trim().replace(",", ".")+"%'";
			break;
		case 11:
			cmd = "select * from rliste where r_name ='"+suchen.getText().trim().replace(",", ".")+"'";
			break;
		case 12:
			cmd = "select * from rliste where r_name like'%"+suchen.getText().trim().replace(",", ".")+"%'";
			break;
		case 13:
			cmd = "select * from rliste where r_datum ='"+DatFunk.sDatInSQL(suchen.getText().trim())+"'";
			break;
		case 14:
			cmd = "select * from rliste where r_datum >'"+DatFunk.sDatInSQL(suchen.getText().trim())+"'";
			break;
		case 15:
			cmd = "select * from rliste where r_datum <'"+DatFunk.sDatInSQL(suchen.getText().trim())+"'";
			break;

		}
		
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		if(!cmd.equals("")){
			buts[0].setEnabled(false);
			suchen.setEnabled(false);
			try{
			starteSuche(cmd);
			}catch(Exception ex){
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, "Korrekte Auflistung des Suchergebnisses fehlgeschlagen");
			}
			OffenePosten.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			suchen.setEnabled(true);
			buts[0].setEnabled(true);
			setzeFocus();
		}

	}
	
	
	class MyOffenePostenTableModel extends DefaultTableModel{
		   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Class<?> getColumnClass(int columnIndex) {
			switch(columnIndex){
			case 0:
				return Integer.class;
			case 1:
				return Date.class;				
			case 2:
				return String.class;
			case 3:
				return String.class;				
			case 4:
				return String.class;
			case 5:
				return Double.class;				
			case 6:
				return Double.class;
			case 7:
				return Date.class;				
			case 8:
				return Double.class;				
			case 9:
				return Date.class;
			case 10:
				return Date.class;				
			case 11:
				return Date.class;				
			case 12:
				return Boolean.class;				
			case 13:
				return String.class;				
			case 14:
				return String.class;				
			case 15:
				return Integer.class;				

			}
		   return String.class;
	    }

		public boolean isCellEditable(int row, int col) {
			if(col==12){
				return true;				
			}
			return false;
		}
		   
	}
	
	private void starteSuche(String sstmt){
		tabmod.setRowCount(0);
		tab.validate();
		tab.repaint();
		Statement stmt = null;
		ResultSet rs = null;

			
		try {
			stmt =  OffenePosten.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			
			
			rs = stmt.executeQuery(sstmt);
			Vector<Object> vec = new Vector<Object>();
			int durchlauf = 0;
			suchOffen = BigDecimal.valueOf(Double.parseDouble("0.00"));
			suchGesamt = BigDecimal.valueOf(Double.parseDouble("0.00"));
			gefunden = 0;
			while(rs.next()){
				vec.clear();
				vec.add(rs.getInt(1)); //r_nummer
				vec.add(rs.getDate(2)); // r_datum
				vec.add( (rs.getString(3)==null ? "" : rs.getString(3)) );// r_kasse
				vec.add( (rs.getString(4)==null ? "" : rs.getString(4)) );//r_name
				vec.add( (rs.getString(5)==null ? "" : rs.getString(5)) );//r_klasse
				vec.add(rs.getBigDecimal(6).doubleValue());//r_betrag
				vec.add(rs.getBigDecimal(7).doubleValue());//r_offen
				vec.add(rs.getDate(8));//r_bezdatum
				vec.add(rs.getBigDecimal(9).doubleValue());//r_zuzahl
				vec.add(rs.getDate(10));//mahndat1
				vec.add(rs.getDate(11));//mahndat2
				vec.add(rs.getDate(12));//mahndat3
				vec.add( (rs.getString(13)==null ?  Boolean.FALSE : (rs.getString(13).equals("T") ? Boolean.TRUE : Boolean.FALSE)) );//mahnsperr
				vec.add( (rs.getString(14)==null ? "" : rs.getString(14)) );//pat_intern
				vec.add( (rs.getString(15)==null ? "" : rs.getString(15)));//ikktraeger
				vec.add(rs.getInt(16));//id
				
				suchOffen = suchOffen.add(rs.getBigDecimal(7));
				suchGesamt = suchGesamt.add(rs.getBigDecimal(6));
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
				gefunden++;
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
	/*****************************************************/
	class OPListSelectionHandler implements ListSelectionListener {
		
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


}
