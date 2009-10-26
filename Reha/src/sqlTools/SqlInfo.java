package sqlTools;

import hauptFenster.Reha;

import java.awt.Cursor;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;





import systemEinstellungen.SystemConfig;

public class SqlInfo {
	
/***********************************/	
	public static boolean gibtsSchon(String sstmt){
		boolean gibtsschon = false;
		Statement stmt = null;
		ResultSet rs = null;
			
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
			if(rs.next()){
				gibtsschon = true;
			}
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
		return gibtsschon;
	}
/*******************************************/
	public static int holeId(String tabelle, String feld){
		int retid = -1;
		Statement stmt = null;
		ResultSet rs = null;
			
		try {
			stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			String sstmt1 = "insert into "+tabelle+" set "+feld+" = '"+SystemConfig.dieseMaschine+"'";
			stmt.execute(sstmt1);			
			String sstmt2 = "select id from "+tabelle+" where "+feld+" = '"+SystemConfig.dieseMaschine+"'";
			rs = stmt.executeQuery(sstmt2);
			if(rs.next()){
				retid = rs.getInt("id");
			}
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
		return retid;
	}
/*******************************/
	public static Vector holeSatz(String tabelle, String felder, String kriterium, List ausschliessen){
		Statement stmt = null;
		ResultSet rs = null;
		Vector<String> retvec = new Vector<String>();
			
		try {
			stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
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
		return (Vector)retvec.clone();
	}
/*****************************************/
	/*******************************/
	public static Vector holeSatzLimit(String tabelle, String felder, String kriterium,int[] limit, List ausschliessen){
		Statement stmt = null;
		ResultSet rs = null;
		Vector<String> retvec = new Vector<String>();
			
		try {
			stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
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
		return (Vector)retvec.clone();
	}
/*****************************************/

	public static Vector holeFeldForUpdate(String tabelle, String feld, String kriterium){
		Statement stmt = null;
		ResultSet rs = null;
		Vector<String> retvec = new Vector<String>();
			
		try {
			stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			String sstmt = "select "+feld+" from "+tabelle+" where "+kriterium;
			rs = stmt.executeQuery(sstmt);
			if(rs.next()){
				 retvec.add( (rs.getString(1)==null  ? "" :  rs.getString(1)) );						 
				 retvec.add( (rs.getString(2)==null  ? "" :  rs.getString(2)) );
			}
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
		return (Vector)retvec.clone();
	}

	/*******************************/
	public static Vector holeSaetze(String tabelle, String felder, String kriterium, List ausschliessen){
		Statement stmt = null;
		ResultSet rs = null;
		Vector<String> retvec = new Vector<String>();
		Vector<Vector> retkomplett = new Vector<Vector>();
		ResultSetMetaData rsMetaData = null;
		try {
			stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			String sstmt = "select "+felder+" from "+tabelle+" where "+kriterium;
			rs = stmt.executeQuery(sstmt);
			
			int nichtlesen = ausschliessen.size();
			while(rs.next()){
				retvec.clear();
				 rsMetaData = rs.getMetaData() ;
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
			retvec.clear();
			retvec = null;
			Reha.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}catch(SQLException ev){
			System.out.println("SQLException: " + ev.getMessage());
			System.out.println("SQLState: " + ev.getSQLState());
			System.out.println("VendorError: " + ev.getErrorCode());
		}	
		finally {
			if (rsMetaData != null){
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
		return (Vector)retkomplett.clone();
	}
/*****************************************/
	public static String macheWhereKlausel(String praefix,String test,String[] suchein){
		//paraefix = wenn der eine fixe Bedinung vorangestellt wird z.B. "(name='steinhilber') AND " bzw. "" fals keine notwendig
		//test = der suchbegriff bzw. die durch Leerzeichen getrennte suchbegriffe
		//suchein[] sind die spalten bzw. die spalte die durchsucht werden soll
		//werden mehrere suchbegriffe eingegeben, bezogen auf die Begriffe -> AND-Suche
		//innerhalb der spalten, bezogen auf die Spalten -> OR-Suche
		String ret = praefix;
		String cmd = test;
		//zun‰chst versuchen daﬂ immer nur ein Leerzeichen zwischen den Begriffen existiert 
		cmd = new String(cmd.replaceAll("   ", " "));
		cmd = new String(cmd.replaceAll("  ", " "));
		// wer jetzt immer noch Leerzeichen in der Suchbedingung hat ist selbst schuld daﬂ er nix finder!!!
		/*
		String[] felder = suchein;
		String[] split = cmd.split(" ");
		if(split.length==1){
			ret = ret +" (";
			for(int i = 0; i < felder.length;i++){
				ret = ret+felder[i]+" like '%"+cmd+"%'";
				if(i < felder.length-1){
					ret = ret+ " OR ";
				}
			}
			ret = ret +") ";
			return ret;
		}
		
		
		ret = ret +"( ";
		for(int i = 0; i < split.length;i++){
			if(! split[i].equals("")){
				ret = ret +" (";
				for(int i2 = 0; i2 < felder.length;i2++){
					ret = ret+felder[i2]+" like '%"+split[i]+"%'";
					if(i2 < felder.length-1){
						ret = ret+ " OR ";
					}
				}
				ret = ret +") ";
				if(i < split.length-1){
					ret = ret+ " AND ";
				}
			}
			
		}
		ret = ret +") ";
		return ret;
		*/
		String[] felder = suchein;
		String[] split = cmd.split(" ");
		if(split.length==1){
			ret = ret +" (";
			for(int i = 0; i < felder.length;i++){
				ret = ret+felder[i]+" like '%"+cmd+"%'";
				if(i < felder.length-1){
					ret = ret+ " OR ";
				}
			}
			ret = ret +") ";
			return ret;
		}
		
		
		ret = ret +"( ";
		for(int i = 0; i < split.length;i++){
			if(! split[i].equals("")){
				ret = ret +" (";
				for(int i2 = 0; i2 < felder.length;i2++){
					ret = ret+felder[i2]+" like '%"+split[i]+"%'";
					if(i2 < felder.length-1){
						ret = ret+ " OR ";
					}
				}
				ret = ret +") ";
				if(i < split.length-1){
					ret = ret+ " AND ";
				}
			}
			
		}
		ret = ret +") ";
		return ret;
		
	}
	
	public static int erzeugeNummer(String nummer){
		int reznr = -1;
		/****** Zun‰chst eine neue Rezeptnummer holen ******/
		Vector numvec = null;
		try {
			Reha.thisClass.conn.setAutoCommit(false);
			String numcmd = nummer+",id";
			//System.out.println("numcmd = "+numcmd);
			numvec = SqlInfo.holeFeldForUpdate("nummern", nummer+",id", "mandant='"+Reha.aktIK+"' FOR UPDATE");
			//System.out.println(Reha.aktIK);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(numvec.size() > 0){
			reznr = new Integer( (String)((Vector) numvec).get(0) );
			//System.out.println("Neue Rezeptnummer = "+reznr);
			String cmd = "update nummern set "+nummer+"='"+(reznr+1)+"' where id='"+((Vector) numvec).get(1)+"'";
			//System.out.println("Kommando = "+cmd);
			new ExUndHop().setzeStatement(cmd);
			//System.out.println("bisherige Rezeptnummer = "+nummer.toUpperCase()+reznr+" / neue Rezeptnummer = "+nummer.toUpperCase()+(reznr+1));
			try {
				Reha.thisClass.conn.setAutoCommit(true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			try {
				Reha.thisClass.conn.rollback();
				Reha.thisClass.conn.setAutoCommit(true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		numvec = null;
		return reznr;

	}
	
	/*******************************************/
	public static int zaehleSaetze(String tabelle, String bedingung){
		int retid = -1;
		Statement stmt = null;
		ResultSet rs = null;
			
		try {
			stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			String sstmt1 = "select count(*) from "+tabelle+" where "+bedingung;
			rs = stmt.executeQuery(sstmt1);			
			if(rs.next()){
				retid = rs.getInt(1);
			}
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
		return retid;
	}
/*******************************/
	public static void aktualisiereSatz(String tabelle, String sets, String kriterium){
		Statement stmt = null;
		ResultSet rs = null;
	
		try {
			stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			String sstmt = "update "+tabelle+" set "+sets+" where "+kriterium+" LIMIT 1";
			//System.out.println("SqlInfo-Statement:\n"+sstmt+"\n*************");
			Object ret = stmt.execute(sstmt);
			//System.out.println(ret);
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
		return ;
	}
	/*******************************/
	public static void aktualisiereSaetze(String tabelle, String sets, String kriterium){
		Statement stmt = null;
		ResultSet rs = null;
	
		try {
			stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			String sstmt = "update "+tabelle+" set "+sets+" where "+kriterium;
			//System.out.println("SqlInfo-Statement:\n"+sstmt+"\n*************");
			Object ret = stmt.execute(sstmt);
			//System.out.println(ret);
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
			stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			String sstmt = "select "+feld+" from pat5 where "+kriterium+" LIMIT 1";
			rs = stmt.executeQuery(sstmt);

			if(rs.next()){
				ret = (rs.getString(feld)==null  ? "" :  rs.getString(feld));
			}
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
	/*****************************************/
	public static Vector<Vector<String>> holeFelder(String xstmt){
		Statement stmt = null;
		ResultSet rs = null;
		String ret = "";
		Vector<String> retvec = new Vector<String>();
		Vector<Vector<String>> retkomplett = new Vector<Vector<String>>();	
		ResultSetMetaData rsMetaData = null;
		int numberOfColumns = 0;
		try {
			stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
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
			Reha.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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
		return (Vector<Vector<String>>) retkomplett.clone();
	}
/*****************************************/

	/*****************************************/
	public static String holeRezFeld(String feld, String kriterium){
		Statement stmt = null;
		ResultSet rs = null;
		String ret = "";
			
		try {
			stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			String sstmt = "select "+feld+" from verordn where "+kriterium+" LIMIT 1";
			rs = stmt.executeQuery(sstmt);

			if(rs.next()){
				ret = (rs.getString(feld)==null  ? "" :  rs.getString(feld));
			}
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
	public static void sqlAusfuehren(String sstmt){
		boolean geklappt = false;
		Statement stmt = null;
			
		try {
			stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			geklappt =  stmt.execute(sstmt);
			Reha.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}catch(SQLException ev){
			System.out.println("SQLException: " + ev.getMessage());
			System.out.println("SQLState: " + ev.getSQLState());
			System.out.println("VendorError: " + ev.getErrorCode());
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

	public static InputStream holeStream(String tabelle, String feld, String kriterium){
		Statement stmt = null;
		ResultSet rs = null;
		InputStream is = null;
			
		try {
			stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			String sstmt = "select "+feld+" from "+tabelle+" where "+kriterium;
			rs = stmt.executeQuery(sstmt);
			if(rs.next()){
				is = rs.getBinaryStream(1); 
			}
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
		return is;
	}


}
