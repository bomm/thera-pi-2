package Tools;





import java.awt.Cursor;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import rehaMail.RehaMail;



public class SqlInfo {
	
/***********************************/	
	public static boolean gibtsSchon(String sstmt){
		boolean gibtsschon = false;
		Statement stmt = null;
		ResultSet rs = null;
			
		try {
			stmt =  RehaMail.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			rs = stmt.executeQuery(sstmt);
			
			if(rs.next()){
				gibtsschon = true;
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
		return gibtsschon;
	}
/*******************************************/
	public static int holeId(String tabelle, String feld){
		int retid = -1;
		Statement stmt = null;
		ResultSet rs = null;
			
		try {
			stmt =  RehaMail.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		return retid;
	}
/*******************************/
	public static Vector holeSatz(String tabelle, String felder, String kriterium, List ausschliessen){
		Statement stmt = null;
		ResultSet rs = null;
		Vector<String> retvec = new Vector<String>();
			
		try {
			stmt =  RehaMail.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			
			String sstmt = "select "+felder+" from "+tabelle+" where "+kriterium+" LIMIT 1";
			rs = stmt.executeQuery(sstmt);
			int nichtlesen = ausschliessen.size();
			if(rs.next()){
				 ResultSetMetaData rsMetaData = rs.getMetaData() ;
				 int numberOfColumns = rsMetaData.getColumnCount()+1;
				 for(int i = 1 ; i < numberOfColumns;i++){
					 if(nichtlesen > 0){
						 if(!ausschliessen.contains( rsMetaData.getColumnName(i)) ){
							 retvec.add( (rs.getString(i)==null  ? "" :  rs.getString(i)) );						 
						 }
					 }else{
						 retvec.add((rs.getString(i)==null  ? "" :  rs.getString(i)));
					 }
				 }
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
		return (Vector)retvec.clone();
	}
/*****************************************/
	/*******************************/
	public static Vector holeSatzLimit(String tabelle, String felder, String kriterium,int[] limit, List ausschliessen){
		Statement stmt = null;
		ResultSet rs = null;
		Vector<String> retvec = new Vector<String>();
			
		try {
			stmt =  RehaMail.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
		            ResultSet.CONCUR_UPDATABLE );
/*			
			stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
*/
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			//Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			String sstmt = "select "+felder+" from "+tabelle+" "+kriterium+" LIMIT "+new Integer(limit[0]).toString()+
			","+new Integer(limit[1]).toString()+"";
			rs = stmt.executeQuery(sstmt);
			int nichtlesen = ausschliessen.size();
			if(rs.next()){
				 ResultSetMetaData rsMetaData = rs.getMetaData() ;
				 int numberOfColumns = rsMetaData.getColumnCount()+1;
				 for(int i = 1 ; i < numberOfColumns;i++){
					 if(nichtlesen > 0){
						 if(!ausschliessen.contains( rsMetaData.getColumnName(i)) ){
							 retvec.add( (rs.getString(i)==null  ? "" :  rs.getString(i)) );						 
						 }
					 }else{
						 retvec.add((rs.getString(i)==null  ? "" :  rs.getString(i)));
					 }
				 }
			}
			//Reha.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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
		return (Vector)retvec.clone();
	}
/*****************************************/

	public static Vector holeFeldForUpdate(String tabelle, String feld, String kriterium){
		Statement stmt = null;
		ResultSet rs = null;
		Vector<String> retvec = new Vector<String>();
			
		try {
			stmt =  RehaMail.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			
			String sstmt = "select "+feld+" from "+tabelle+" where "+kriterium;
			rs = stmt.executeQuery(sstmt);
			if(rs.next()){
				 retvec.add( (rs.getString(1)==null  ? "" :  rs.getString(1)) );						 
				 retvec.add( (rs.getString(2)==null  ? "" :  rs.getString(2)) );
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
		return (Vector)retvec.clone();
	}

	/*******************************/
	public static Vector holeSaetze(String tabelle, String felder, String kriterium, List ausschliessen){
		Statement stmt = null;
		ResultSet rs = null;
		Vector<String> retvec = new Vector<String>();
		Vector<Vector> retkomplett = new Vector<Vector>();	
		try {
			stmt =  RehaMail.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			
			String sstmt = "select "+felder+" from "+tabelle+" where "+kriterium;
			rs = stmt.executeQuery(sstmt);
			int nichtlesen = ausschliessen.size();
			while(rs.next()){
				retvec.clear();
				 ResultSetMetaData rsMetaData = rs.getMetaData() ;
				 int numberOfColumns = rsMetaData.getColumnCount()+1;
				 for(int i = 1 ; i < numberOfColumns;i++){
					 if(nichtlesen > 0){
						 if(!ausschliessen.contains( rsMetaData.getColumnName(i)) ){
							 retvec.add( (rs.getString(i)==null  ? "" :  rs.getString(i)) );						 
						 }
					 }else{
						 retvec.add( (rs.getString(i)==null  ? "" :  rs.getString(i)) );
					 }
				 }
				 retkomplett.add((Vector)retvec.clone());
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
		return (Vector)retkomplett.clone();
	}
/*****************************************/
	
	
	
	
	/*******************************************/
	public static int zaehleSaetze(String tabelle, String bedingung){
		int retid = -1;
		Statement stmt = null;
		ResultSet rs = null;
			
		try {
			stmt =  RehaMail.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			
			String sstmt1 = "select count(*) from "+tabelle+" where "+bedingung;
			rs = stmt.executeQuery(sstmt1);			
			if(rs.next()){
				retid = rs.getInt(1);
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
		return retid;
	}
/*******************************/
	public static void aktualisiereSatz(String tabelle, String sets, String kriterium){
		Statement stmt = null;
		ResultSet rs = null;
	
		try {
			stmt =  RehaMail.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			
			String sstmt = "update "+tabelle+" set "+sets+" where "+kriterium+" LIMIT 1";
			//System.out.println("SqlInfo-Statement:\n"+sstmt+"\n*************");
			Object ret = stmt.execute(sstmt);
			//System.out.println(ret);
			
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
		return ;
	}
	/*******************************/
	public static void aktualisiereSaetze(String tabelle, String sets, String kriterium){
		Statement stmt = null;
		ResultSet rs = null;
	
		try {
			stmt =  RehaMail.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			
			String sstmt = "update "+tabelle+" set "+sets+" where "+kriterium;
			//System.out.println("SqlInfo-Statement:\n"+sstmt+"\n*************");
			Object ret = stmt.execute(sstmt);
			//System.out.println(ret);
			
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
		return ;
	}
/*****************************************/

	/*****************************************/
	public static String holePatFeld(String feld, String kriterium){
		Statement stmt = null;
		ResultSet rs = null;
		String ret = "";
		//Vector<String> retvec = new Vector<String>();
			
		try {
			stmt =  RehaMail.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			
			String sstmt = "select "+feld+" from pat5 where "+kriterium+" LIMIT 1";
			rs = stmt.executeQuery(sstmt);

			if(rs.next()){
				ret = (rs.getString(feld)==null  ? "" :  rs.getString(feld));
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
		return ret;
	}
/*****************************************/
	/*****************************************/
	public static Vector<Vector<String>> holeFelder(String xstmt){
		Statement stmt = null;
		ResultSet rs = null;
		String ret = "";
		Vector<String> retvec = new Vector<String>();
		Vector<Vector<String>> retkomplett = new Vector<Vector<String>>();	
		try {
			stmt =  RehaMail.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			
			String sstmt = xstmt;
			rs = stmt.executeQuery(sstmt);

			while(rs.next()){
				retvec.clear();
				 ResultSetMetaData rsMetaData = rs.getMetaData() ;
				 int numberOfColumns = rsMetaData.getColumnCount()+1;
				 for(int i = 1 ; i < numberOfColumns;i++){
						 retvec.add( (rs.getString(i)==null  ? "" :  rs.getString(i)) );

				 }
				 retkomplett.add((Vector<String>)retvec.clone());
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
		return (Vector<Vector<String>>) retkomplett.clone();
	}
/*****************************************/

	/*****************************************/
	public static String holeRezFeld(String feld, String kriterium){
		Statement stmt = null;
		ResultSet rs = null;
		String ret = "";
		Vector<String> retvec = new Vector<String>();
			
		try {
			stmt =  RehaMail.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			
			String sstmt = "select "+feld+" from verordn where "+kriterium+" LIMIT 1";
			rs = stmt.executeQuery(sstmt);

			if(rs.next()){
				ret = (rs.getString(feld)==null  ? "" :  rs.getString(feld));
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
		return ret;
	}
/*****************************************/
	public static void sqlAusfuehren(String sstmt){
		boolean geklappt = false;
		Statement stmt = null;
			
		try {
			stmt =  RehaMail.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			geklappt =  stmt.execute(sstmt);
			
		}catch(SQLException ev){
			System.out.println("SQLException: " + ev.getMessage());
			System.out.println("SQLState: " + ev.getSQLState());
			System.out.println("VendorError: " + ev.getErrorCode());
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
		return;
	}

	public static InputStream holeStream(String tabelle, String feld, String kriterium){
		Statement stmt = null;
		ResultSet rs = null;
		InputStream is = null;
			
		try {
			stmt =  RehaMail.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
		
			String sstmt = "select "+feld+" from "+tabelle+" where "+kriterium;
			rs = stmt.executeQuery(sstmt);
			if(rs.next()){
				is = rs.getBinaryStream(1); 
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
		return is;
	}

/*************************************/
	public static String holeEinzelFeld(String xstmt){
		Statement stmt = null;
		ResultSet rs = null;
		String ret = "";
		ResultSetMetaData rsMetaData = null;
		try {
			stmt =  RehaMail.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			e.printStackTrace();
			return "";
		}
		try{
			RehaMail.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			String sstmt = xstmt;
			rs = stmt.executeQuery(sstmt);
			while(rs.next()){
						 ret =  (rs.getString(1)==null  ? "" :  rs.getString(1)).trim() ;
						 break;
			}
			RehaMail.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			return ret;
		}catch(SQLException ev){
			//System.out.println("SQLException: " + ev.getMessage());
			//System.out.println("SQLState: " + ev.getSQLState());
			//System.out.println("VendorError: " + ev.getErrorCode());
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
		return ret;
	}
/*****************************************/

	
}
