package patientenFenster;

import hauptFenster.Reha;

import java.awt.Color;
import java.awt.Cursor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import systemTools.Colors;
import terminKalender.datFunk;

public class Historie extends JXPanel{
	public JXTable tabhistorie = null;
	public MyHistorieTableModel dtblm;
	public static Historie historie;	
	public JLabel anzahlRezepte;
	public Historie(){
		super();
		historie = this;
		setOpaque(false);
		FormLayout lay = new FormLayout("5dlu,fill:0:grow(1.00),5dlu","5dlu,p,2dlu,fill:0:grow(0.50),5dlu");
		CellConstraints cc = new CellConstraints();
		setLayout(lay);		 
		/***************************/
		anzahlRezepte = new JLabel("Anzahl Rezepte in Historie: 0");
		anzahlRezepte.setForeground(Color.BLUE);
		add(anzahlRezepte,cc.xy(2,2));
		JScrollPane ptc = new JScrollPane();
        ptc.setBackground(Color.WHITE);
        ptc.setViewportBorder(null);
        ptc.setViewportView(getTabelle());
        ptc.revalidate();
		add(ptc,cc.xy(2,4));
		/***************************/
		
	}
	
	public void holeHistorie(String patint){
		final String xpatint = patint;
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				dtblm.setRowCount(0);
				new HistorieHolen(xpatint);
				return null;
			}
			
		}.execute();
	}
	
	public JXTable getTabelle(){
		dtblm = new MyHistorieTableModel();
		String[] column = 	{"Rezept-Nr.","Rez-Datum","Anzahl","Beh.1","Beh.2","Beh.3","Beh.4"};
		dtblm.setColumnIdentifiers(column);
		tabhistorie = new JXTable(dtblm);
		
		
		tabhistorie.setHighlighters(HighlighterFactory.createSimpleStriping(Colors.PiOrange.alpha(0.5f)));
		tabhistorie.setDoubleBuffered(true);
		tabhistorie.setEditable(false);
		tabhistorie.setSortable(false);
		tabhistorie.validate();
		tabhistorie.setName("Historie");

		
		return tabhistorie;
	}
	public void macheTabelle(Vector vec){
		if(vec.size()> 0){
			dtblm.addRow(vec);	
		}else{
			dtblm.setRowCount(0);
			tabhistorie.validate();
		}
		
	}

}
/*************************************/
class HistorieHolen{
	HistorieHolen(String patint){
	Statement stmt = null;
	ResultSet rs = null;
	String sstmt = new String();

	sstmt = "select * from lza where PAT_INTERN ='"+patint+"' ORDER BY REZ_DATUM DESC";
		
	try {
		stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
		            ResultSet.CONCUR_UPDATABLE );
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	try{
		rs = stmt.executeQuery(sstmt);
		Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		Vector xvec = new Vector();
		int anzahl = 0;
		while( rs.next()){
				xvec.add(rs.getString("REZ_NR"));
				xvec.add(datFunk.sDatInDeutsch(rs.getString("REZ_DATUM")));			
				xvec.add(rs.getInt("ANZAHL1"));			
				xvec.add(rs.getInt("ART_DBEH1"));
				xvec.add(rs.getInt("ART_DBEH2"));
				xvec.add(rs.getInt("ART_DBEH3"));
				xvec.add(rs.getInt("ART_DBEH4"));
				Historie.historie.macheTabelle((Vector)xvec.clone());
				xvec.clear();
				anzahl++;
		}
		Historie.historie.anzahlRezepte.setText("Anzahl Rezepte in Historie: "+anzahl);
		Reha.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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
}
/*************************************/

class MyHistorieTableModel extends DefaultTableModel{
	   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Class getColumnClass(int columnIndex) {
		   if(columnIndex==0){return String.class;}
		  /* if(columnIndex==1){return JLabel.class;}*/
		   else{return String.class;}
        //return (columnIndex == 0) ? Boolean.class : String.class;
    }

	    public boolean isCellEditable(int row, int col) {
	        //Note that the data/cell address is constant,
	        //no matter where the cell appears onscreen.
	        if (col == 0){
	        	return true;
	        }else if(col == 6){
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
