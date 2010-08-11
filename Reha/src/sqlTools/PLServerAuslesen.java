package sqlTools;

import hauptFenster.Reha;

import java.awt.Cursor;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JOptionPane;



public class PLServerAuslesen {
	static Connection con = null;
	
	public PLServerAuslesen(){
		oeffnePLConnection();
	}
	@SuppressWarnings("unchecked")
	public static Vector<Vector<String>> holeFelder(String xstmt){
		Statement stmt = null;
		ResultSet rs = null;
		Vector<String> retvec = new Vector<String>();
		Vector<Vector<String>> retkomplett = new Vector<Vector<String>>();	
		ResultSetMetaData rsMetaData = null;
		int numberOfColumns = 0;
		try {
			stmt =  con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try{
			Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
			String sstmt = xstmt;
			rs = stmt.executeQuery(sstmt);
			while(rs.next()){
				retvec.clear();
				 rsMetaData = rs.getMetaData() ;
				 numberOfColumns = rsMetaData.getColumnCount()+1;
				 for(int i = 1 ; i < numberOfColumns;i++){
						 retvec.add( (rs.getString(i)==null  ? "" :  rs.getString(i)) );

				 }
				 retkomplett.add((Vector<String>)retvec.clone());
			}
			Reha.thisFrame.setCursor(Reha.thisClass.cdefault);
			retvec.clear();
			retvec = null;
		}catch(SQLException ev){
			System.out.println("SQLException: " + ev.getMessage());
			System.out.println("SQLState: " + ev.getSQLState());
			System.out.println("VendorError: " + ev.getErrorCode());
		}

		finally {
			if(rsMetaData != null){
				rsMetaData = null;
			}
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException sqlEx) { // ignore }
					rs = null;
				}
			}	
			if (stmt != null) {
				try {
					stmt.close();
					stmt = null;
				} catch (SQLException sqlEx) { // ignore }
					stmt = null;
				}
			}
		}
		return (Vector<Vector<String>>) retkomplett;
	}
	
	public static void sqlAusfuehren(String sstmt){
		Statement stmt = null;
		try {
			stmt =  con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Fehler bei der Ausführung des Statements\nMethode:sqlAusfuehren("+sstmt+")");
		}
		try{
			stmt.execute(sstmt);
			Reha.thisFrame.setCursor(Reha.thisClass.cdefault);
		}catch(SQLException ev){
			System.out.println("SQLException: " + ev.getMessage());
		}	
		finally {
			if (stmt != null) {
				try {
					stmt.close();
					stmt = null;
				} catch (SQLException sqlEx) { // ignore }
					stmt = null;
				}
			}
		}
		return;
	}
	
	public boolean oeffnePLConnection(){
		try{
			Class.forName("de.root1.jpmdbc.Driver");
    	}
    	catch ( final Exception e ){
    		JOptionPane.showMessageDialog(null,"Fehler beim Laden des Datenbanktreibers für Preislisten-Server");
    		return false;
        }
    	try {
			Properties connProperties = new Properties();
			connProperties.setProperty("user", "dbo336243054");
			connProperties.setProperty("password", "allepreise");
			connProperties.setProperty("host", "db2614.1und1.de");	        			
			connProperties.setProperty("port", "3306");
			connProperties.setProperty("compression","false");
			connProperties.setProperty("NO_DRIVER_INFO", "1");
			con =  DriverManager.getConnection("jdbc:jpmdbc:http://www.thera-pi.org/jpmdbc.php?db336243054",connProperties);
			
			Date zeit = new Date();
			String stx = "Insert into eingeloggt set comp='"+"Preis-Listen "+java.net.InetAddress.getLocalHost()+": import"+"', zeit='"+zeit.toString()+"', einaus='ein';";
			sqlAusfuehren(stx);
			
    	} 
    	catch (final SQLException ex) {
    		System.out.println("SQLException-1: " + ex.getMessage());
    		System.out.println("SQLState-1: " + ex.getSQLState());
    		System.out.println("VendorError-1: " + ex.getErrorCode());
    		JOptionPane.showMessageDialog(null,"Fehler: Datenbankkontakt zum Preislisten-Server konnte nicht hergestellt werden.");
    		return false;
    	}catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		}
    	return true;
	}
	public boolean schliessePLConnection(){
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
