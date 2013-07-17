package CommonTools;







import java.awt.Cursor;
import java.io.InputStream;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;








public class SqlInfo {
	
	static JFrame frame = null;
	static Connection conn = null;
	static InetAddress dieseMaschine;
	static Cursor wartenCursor = new Cursor(Cursor.WAIT_CURSOR);
	static Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
	
	
	public SqlInfo(JFrame frame,Connection conn,InetAddress dieseMaschine){
		SqlInfo.frame = frame;
		SqlInfo.conn = conn;
		SqlInfo.dieseMaschine = dieseMaschine;
	}
	

	public SqlInfo(JFrame frame, Connection conn) {
		SqlInfo.frame = frame;
		SqlInfo.conn = conn;
		SqlInfo.dieseMaschine = null;
	}
	public SqlInfo() {
		SqlInfo.frame = null;
		SqlInfo.conn = null;
		SqlInfo.dieseMaschine = null;
	}
	
	public void setFrame(JFrame frame){
		SqlInfo.frame = frame;
	}
	public void setConnection(Connection conn){
		SqlInfo.conn = conn;
	}
	public void setDieseMaschine(InetAddress dieseMaschine){
		SqlInfo.dieseMaschine = dieseMaschine;
	}
	public JFrame getFrame(){
		return SqlInfo.frame;
	}
	public Connection getConnection(){
		return SqlInfo.conn;
	}
	public InetAddress getDieseMaschine(){
		return SqlInfo.dieseMaschine;
	}


/***********************************/
	public static void loescheLocksMaschine(){
		int stelle = dieseMaschine.toString().indexOf("/");
		String maschine = dieseMaschine.toString().substring(0,stelle);
		SqlInfo.sqlAusfuehren("delete from flexlock where maschine like '%"+maschine+"%'");
	}
	public static boolean gibtsSchon(String sstmt){
		boolean gibtsschon = false;
		Statement stmt = null;
		ResultSet rs = null;
			
		try {
			if(frame != null)
			frame.setCursor(wartenCursor);
			stmt =  conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
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
			if(frame != null)
			frame.setCursor(normalCursor);
			
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
		return gibtsschon;
	}
/*******************************************/
	public static int holeId(String tabelle, String feld){
		int retid = -1;
		Statement stmt = null;
		ResultSet rs = null;
			
		try {
			stmt =  conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			if(frame != null)
			frame.setCursor(wartenCursor);
			String sstmt1 = "insert into "+tabelle+" set "+feld+" = '"+dieseMaschine+"'";
			stmt.execute(sstmt1);			
			String sstmt2 = "select id from "+tabelle+" where "+feld+" = '"+dieseMaschine+"'";
			rs = stmt.executeQuery(sstmt2);
			if(rs.next()){
				retid = rs.getInt("id");
			}
			if(frame != null)
			frame.setCursor(normalCursor);
		}catch(SQLException ev){
			//System.out.println("SQLException: " + ev.getMessage());
			//System.out.println("SQLState: " + ev.getSQLState());
			//System.out.println("VendorError: " + ev.getErrorCode());
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
	public static int holeIdSimple(String tabelle, String befehl){
		int retid = -1;
		Statement stmt = null;
		ResultSet rs = null;
			
		try {

			stmt =  conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
			stmt.execute(befehl);
			rs = stmt.executeQuery("select max(id) from "+tabelle);
			if(rs.next()){
				retid = rs.getInt(1);	
			}
			
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
	
	public static Vector<String> holeSatz(String tabelle, String felder, String kriterium, List<?> ausschliessen){
		Statement stmt = null;
		ResultSet rs = null;
		Vector<String> retvec = new Vector<String>();
			
		try {
			stmt =  conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			if(frame != null)
			frame.setCursor(wartenCursor);
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
			if(frame != null)
			frame.setCursor(normalCursor);
		}catch(SQLException ev){
			//System.out.println("SQLException: " + ev.getMessage());
			//System.out.println("SQLState: " + ev.getSQLState());
			//System.out.println("VendorError: " + ev.getErrorCode());
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
		return (Vector<String>)retvec;
	}
/*****************************************/
	/*******************************/
	public static Vector<String> holeSatzLimit(String tabelle, String felder, String kriterium,int[] limit, List<?> ausschliessen){
		Statement stmt = null;
		ResultSet rs = null;
		Vector<String> retvec = new Vector<String>();
			
		try {
			stmt =  conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
		            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			//Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
			String sstmt = "select "+felder+" from "+tabelle+" "+kriterium+" LIMIT "+Integer.toString(limit[0])+
			","+Integer.toString(limit[1])+"";
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
			//Reha.thisFrame.setCursor(Reha.thisClass.normalCursor);
		}catch(SQLException ev){
			//System.out.println("SQLException: " + ev.getMessage());
			//System.out.println("SQLState: " + ev.getSQLState());
			//System.out.println("VendorError: " + ev.getErrorCode());
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
		return (Vector<String>)retvec;
	}
/*****************************************/
	public static Vector<String> holeFeldNamen(String tabelle, boolean ausnahmen, List<?> lausnahmen){
		Vector<String> vec = new Vector<String>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt =  conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
		            ResultSet.CONCUR_UPDATABLE );
			rs = stmt.executeQuery("describe "+tabelle);
			while(rs.next()){
				 if(ausnahmen){
					 if(! lausnahmen.contains(rs.getString(1).toLowerCase() )){
						 vec.add( rs.getString(1).toLowerCase() );
					 }
				 }else{
					 vec.add( rs.getString(1).toLowerCase() );
				 }
			}
		} catch (SQLException e) {
			e.printStackTrace();
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
		return vec;
	}

	public static Vector<String> holeFeldForUpdate(String tabelle, String feld, String kriterium){
		Statement stmt = null;
		ResultSet rs = null;
		Vector<String> retvec = new Vector<String>();
			
		try {
			stmt =  conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			if(frame != null)
			frame.setCursor(wartenCursor);
			String sstmt = "select "+feld+" from "+tabelle+kriterium;
			rs = stmt.executeQuery(sstmt);
			if(rs.next()){
				 retvec.add( (rs.getString(1)==null  ? "" :  rs.getString(1)) );						 
				 retvec.add( (rs.getString(2)==null  ? "" :  rs.getString(2)) );
			}
			if(frame != null)
			frame.setCursor(normalCursor);
		}catch(SQLException ev){
			//System.out.println("SQLException: " + ev.getMessage());
			//System.out.println("SQLState: " + ev.getSQLState());
			//System.out.println("VendorError: " + ev.getErrorCode());
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
		return (Vector<String>)retvec;
	}

	/*******************************/
	@SuppressWarnings("unchecked")
	public static Vector<Vector<String>> holeSaetze(String tabelle, String felder, String kriterium, List<String> ausschliessen){
		Statement stmt = null;
		ResultSet rs = null;
		Vector<String> retvec = new Vector<String>();
		Vector<Vector<String>> retkomplett = new Vector<Vector<String>>();
		ResultSetMetaData rsMetaData = null;
		try {
			stmt =  conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			if(frame != null)
			frame.setCursor(wartenCursor);
			String sstmt = "select "+felder+" from "+tabelle+" where "+kriterium;
			rs = stmt.executeQuery(sstmt);
			
			int nichtlesen = ausschliessen.size();
			while(rs.next()){
				try{
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
					 retkomplett.add((Vector<String>)((Vector<?>)retvec.clone()));
				}catch(Exception ex){
					//ex.printStackTrace();
				}
			}
			retvec.clear();
			retvec = null;
			if(frame != null)
			frame.setCursor(normalCursor);
		}catch(SQLException ev){
			ev.printStackTrace();
			//JOptionPane.showMessageDialog(null, "Fehler in der MySqlFunktion-HoeleSaetze");
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
		return (Vector<Vector<String>>)retkomplett;
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
		//zun�chst versuchen da� immer nur ein Leerzeichen zwischen den Begriffen existiert 
		cmd = cmd.replaceAll("   ", " ");
		cmd = cmd.replaceAll("  ", " ");
		// wer jetzt immer noch Leerzeichen in der Suchbedingung hat ist selbst schuld daß er nix finder!!!
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
	/***********************************/	
	private static String toRTF(String toConvert){
		String convertet = "";
		convertet = toConvert.replace("Ö", "\\\\\\\\\\'d6").replace("ö", "\\\\\\\\\\'f6");
		convertet = convertet.replace("Ä", "\\\\\\\\\\'c4").replace("ä", "\\\\\\\\\\'e4");
		convertet = convertet.replace("Ü", "\\\\\\\\\\'dc").replace("ü", "\\\\\\\\\\'fc");
		convertet = convertet.replace("ß", "\\\\\\\\\\'df");
		return String.valueOf(convertet);
	}
	/***********************************/	
	public static String macheWhereKlauselRTF(String praefix,String test,String[] suchein){
		String ret = praefix;
		String cmd = test;
		cmd = cmd.replaceAll("   ", " ");
		cmd = cmd.replaceAll("  ", " ");
		String[] felder = suchein;
		String[] split = cmd.split(" ");
		if(split.length==1){
			ret = ret +" (";
			for(int i = 0; i < felder.length;i++){
				if(i==0){
					ret = ret+felder[i]+" like '%"+cmd+"%'";	
				}else{
					ret = ret+felder[i]+" like '%"+toRTF(cmd)+"%'";
				}
				
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
					if(i2==0){
						ret = ret+felder[i2]+" like '%"+split[i]+"%'";	
					}else{
						ret = ret+felder[i2]+" like '%"+toRTF(split[i])+"%'";
					}
					
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
		/****** Zunächst eine neue Rezeptnummer holen ******/
		Vector<String> numvec = null;
		try {
			conn.setAutoCommit(false);
			//String numcmd = nummer+",id";
			////System.out.println("numcmd = "+numcmd);
			//numvec = SqlInfo.holeFeldForUpdate("nummern", nummer+",id", "mandant='"+Reha.aktIK+"' FOR UPDATE");
			numvec = SqlInfo.holeFeldForUpdate("nummern", nummer+",id", " FOR UPDATE");
			////System.out.println(Reha.aktIK);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(numvec.size() > 0){
			try{
			reznr = Integer.parseInt( (String)((Vector<String>) numvec).get(0) );
			String cmd = "update nummern set "+nummer+"='"+(reznr+1)+"' where id='"+((Vector<String>) numvec).get(1)+"'";
			SqlInfo.sqlAusfuehren(cmd);
			}catch(Exception ex){
				reznr = -1;
			}
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			try {
				conn.rollback();
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		numvec = null;
		return reznr;

	}
	
	public static int erzeugeNummerMitMax(String nummer,int max){
		int reznr = -1;
		/****** Zunächst eine neue Rezeptnummer holen ******/
		Vector<String> numvec = null;
		try {
			conn.setAutoCommit(false);
			//String numcmd = nummer+",id";
			////System.out.println("numcmd = "+numcmd);
			numvec = SqlInfo.holeFeldForUpdate("nummern", nummer+",id", " FOR UPDATE");
			////System.out.println(Reha.aktIK);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(numvec.size() > 0){
			reznr = Integer.parseInt(  (String)((Vector<String>) numvec).get(0) );
			if((reznr+1) > max){
				reznr = 1;
			}
			////System.out.println("Neue Rezeptnummer = "+reznr);
			String cmd = "update nummern set "+nummer+"='"+(reznr+1)+"' where id='"+((Vector<String>) numvec).get(1)+"'";
			////System.out.println("Kommando = "+cmd);
			new ExUndHop().setzeStatement(cmd);
			////System.out.println("bisherige Rezeptnummer = "+nummer.toUpperCase()+reznr+" / neue Rezeptnummer = "+nummer.toUpperCase()+(reznr+1));
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			try {
				conn.rollback();
				conn.setAutoCommit(true);
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
			stmt =  conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			if(frame != null)
			frame.setCursor(wartenCursor);
			String sstmt1 = "select count(*) from "+tabelle+" where "+bedingung;
			rs = stmt.executeQuery(sstmt1);			
			if(rs.next()){
				retid = rs.getInt(1);
			}
			if(frame != null)
			frame.setCursor(normalCursor);
		}catch(SQLException ev){
			//System.out.println("SQLException: " + ev.getMessage());
			//System.out.println("SQLState: " + ev.getSQLState());
			//System.out.println("VendorError: " + ev.getErrorCode());
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
			stmt =  conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			if(frame != null)
			frame.setCursor(wartenCursor);
			String sstmt = "update "+tabelle+" set "+sets+" where "+kriterium+" LIMIT 1";
			////System.out.println("SqlInfo-Statement:\n"+sstmt+"\n*************");
			stmt.execute(sstmt);
			////System.out.println(ret);
			if(frame != null)
			frame.setCursor(normalCursor);
		}catch(SQLException ev){
			//System.out.println("SQLException: " + ev.getMessage());
			//System.out.println("SQLState: " + ev.getSQLState());
			//System.out.println("VendorError: " + ev.getErrorCode());
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
			stmt =  conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			if(frame != null)
			frame.setCursor(wartenCursor);
			String sstmt = "update "+tabelle+" set "+sets+" where "+kriterium;
			////System.out.println("SqlInfo-Statement:\n"+sstmt+"\n*************");
			stmt.execute(sstmt);
			////System.out.println(ret);
			if(frame != null)
			frame.setCursor(normalCursor);
		}catch(SQLException ev){
			//System.out.println("SQLException: " + ev.getMessage());
			//System.out.println("SQLState: " + ev.getSQLState());
			//System.out.println("VendorError: " + ev.getErrorCode());
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
			stmt =  conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			if(frame != null)
			frame.setCursor(wartenCursor);
			String sstmt = "select "+feld+" from pat5 where "+kriterium+" LIMIT 1";
			rs = stmt.executeQuery(sstmt);

			if(rs.next()){
				ret = (rs.getString(feld)==null  ? "" :  rs.getString(feld));
			}
			if(frame != null)
			frame.setCursor(normalCursor);
		}catch(SQLException ev){
			//System.out.println("SQLException: " + ev.getMessage());
			//System.out.println("SQLState: " + ev.getSQLState());
			//System.out.println("VendorError: " + ev.getErrorCode());
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
	public static String holeEinzelFeld(String xstmt){
		Statement stmt = null;
		ResultSet rs = null;
		String ret = "";
		ResultSetMetaData rsMetaData = null;
		try {
			stmt =  conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			e.printStackTrace();
			return "";
		}
		try{
			if(frame != null)
			frame.setCursor(wartenCursor);
			String sstmt = xstmt;
			rs = stmt.executeQuery(sstmt);
			while(rs.next()){
						 ret =  (rs.getString(1)==null  ? "" :  rs.getString(1)).trim() ;
						 break;
			}
			if(frame != null)
			frame.setCursor(normalCursor);
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
	
	/*****************************************/
	@SuppressWarnings("unchecked")
	public static Vector<Vector<String>> holeFelder(String xstmt){
		Statement stmt = null;
		ResultSet rs = null;
		//String ret = "";
		Vector<String> retvec = new Vector<String>();
		Vector<Vector<String>> retkomplett = new Vector<Vector<String>>();	
		ResultSetMetaData rsMetaData = null;
		int numberOfColumns = 0;
		try {
			stmt =  conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			if(frame != null)
			frame.setCursor(wartenCursor);
			String sstmt = xstmt;
			rs = stmt.executeQuery(sstmt);
			while(rs.next()){
				retvec.clear();
				 rsMetaData = rs.getMetaData() ;
				 numberOfColumns = rsMetaData.getColumnCount()+1;
				 for(int i = 1 ; i < numberOfColumns;i++){
						 retvec.add( (rs.getString(i)==null  ? "" :  rs.getString(i)) );

				 }
				 retkomplett.add( ((Vector<String>)retvec.clone()));
			}
			if(frame != null)
			frame.setCursor(normalCursor);
			retvec.clear();
			retvec = null;
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
		return (Vector<Vector<String>>) retkomplett;
	}
/*****************************************/

	/*****************************************/
	public static String holeRezFeld(String feld, String kriterium){
		Statement stmt = null;
		ResultSet rs = null;
		String ret = "";
			
		try {
			stmt =  conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			if(frame != null)
			frame.setCursor(wartenCursor);
			String sstmt = "select "+feld+" from verordn where "+kriterium+" LIMIT 1";
			rs = stmt.executeQuery(sstmt);

			if(rs.next()){
				ret = (rs.getString(feld)==null  ? "" :  rs.getString(feld));
			}
			if(frame != null)
			frame.setCursor(normalCursor);
		}catch(SQLException ev){
			//System.out.println("SQLException: " + ev.getMessage());
			//System.out.println("SQLState: " + ev.getSQLState());
			//System.out.println("VendorError: " + ev.getErrorCode());
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
	public static Vector<String> holeFeld(String sstmt){
		Statement stmt = null;
		ResultSet rs = null;
		String ret = "";
		Vector<String> vecret = new Vector<String>();
			
		try {
			stmt =  conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			if(frame != null)
			frame.setCursor(wartenCursor);
			rs = stmt.executeQuery(sstmt);

			while(rs.next()){
				ret = (rs.getString(1)==null  ? "" :  rs.getString(1));
				vecret.add(String.valueOf(ret));
			}
			if(frame != null)
			frame.setCursor(normalCursor);
		}catch(SQLException ev){
			//System.out.println("SQLException: " + ev.getMessage());
			//System.out.println("SQLState: " + ev.getSQLState());
			//System.out.println("VendorError: " + ev.getErrorCode());
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
		return vecret;
	}

	/*****************************************/
	public static boolean sqlAusfuehren(String sstmt){
		Statement stmt = null;
		boolean ret = true;	
		try {
			stmt =  conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Fehler bei der Ausführung des Statements\nMethode:sqlAusfuehren("+sstmt+")");
			System.exit(0);
		}
		try{
			stmt.execute(sstmt);
			if(frame != null)
			frame.setCursor(normalCursor);
		}catch(SQLException ev){
			ev.printStackTrace();
			JOptionPane.showMessageDialog(null, "Fehler bei der Ausführung des Statements\nMethode:sqlAusfuehren("+sstmt+")\n\nBitte informieren Sie sofort den Administrator!!!");
			ret = false;
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
		return ret;
	}

	public static InputStream holeStream(String tabelle, String feld, String kriterium){
		Statement stmt = null;
		ResultSet rs = null;
		InputStream is = null;
			
		try {
			stmt =  conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			if(frame != null)
			frame.setCursor(wartenCursor);
			String sstmt = "select "+feld+" from "+tabelle+" where "+kriterium+" LIMIT 1";
			
			rs = stmt.executeQuery(sstmt);
			if(rs.next()){
				is = rs.getBinaryStream(1); 
			}
			if(frame != null)
			frame.setCursor(normalCursor);
		}catch(SQLException ev){
			//System.out.println("SQLException: " + ev.getMessage());
			//System.out.println("SQLState: " + ev.getSQLState());
			//System.out.println("VendorError: " + ev.getErrorCode());
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
	public static boolean transferRowToAnotherDB(String sourcedb,
			String targetdb,
			String dbfield,
			String argument,
			boolean ausnahmen,
			List<?> lausnahmen){
		
		boolean ret = false;
		StringBuffer transferBuf = new StringBuffer();
		StringBuffer insertBuf = new StringBuffer();
		Vector<String> feldNamen = SqlInfo.holeFeldNamen(sourcedb,ausnahmen,lausnahmen );
		transferBuf.append("select ");
		int rezeptFelder = 0;
		for(int i = 0; i < feldNamen.size();i++){
			if(i > 0){
				transferBuf.append(","+feldNamen.get(i));				
			}else{
				transferBuf.append(feldNamen.get(i));
			}
		}
		transferBuf.append(" from "+sourcedb+" where "+dbfield+"='"+argument+"' LIMIT 1");
		////System.out.println(transferBuf.toString());
		//System.out.println(transferBuf.toString());
		Vector<Vector<String>> vec = SqlInfo.holeFelder(transferBuf.toString());
		
		if(vec.size()<=0){
			return false;
		}
		try{
			rezeptFelder = vec.get(0).size();	
			insertBuf.append("insert into "+targetdb+" set ");
			for(int i = 0; i < rezeptFelder;i++){
				if(!vec.get(0).get(i).equals("")){
					if(i > 0){
						insertBuf.append(","+feldNamen.get(i)+"='"+StringTools.Escaped(vec.get(0).get(i))+"'");
					}else{
						insertBuf.append(feldNamen.get(i)+"='"+StringTools.Escaped(vec.get(0).get(i))+"'");
					}
				}
			}
			//System.out.println(insertBuf.toString());
			SqlInfo.sqlAusfuehren(insertBuf.toString());
			return true;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return ret;
	}
	
	public static InputStream liesIniAusTabelle(String inifilename){
		InputStream retStream = null;
		Statement stmt = null;;
		ResultSet rs = null;
		try {
			stmt = (Statement) conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE );
				String test = "select inhalt from inidatei where dateiname='"+inifilename+"' LIMIT 1";
				
				rs = (ResultSet) stmt.executeQuery(test);
				if(rs.next()){
					retStream = rs.getBinaryStream(1);
				}
		}catch (SQLException e) {
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
		return retStream;
	}
	public static boolean schreibeIniInTabelle(String inifilename,byte[] buf){
		boolean ret = false;
		try{
			Statement stmt = null;;
			ResultSet rs = null;
			PreparedStatement ps = null;
			try {
				stmt = (Statement) conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_UPDATABLE );
				String select = null;
				if(SqlInfo.holeEinzelFeld("select dateiname from inidatei where dateiname='"+inifilename+"' LIMIT 1").equals("")){
						select = "insert into inidatei set dateiname = ? , inhalt = ?";
				}else{
					select = "update inidatei set dateiname = ? , inhalt = ? where dateiname = '"+inifilename+"'" ;						
				}
				ps = (PreparedStatement) conn.prepareStatement(select);
				ps.setString(1, inifilename);
				ps.setBytes(2, buf);			  
				ps.execute();
			} catch (SQLException e) {
				e.printStackTrace();
			}catch(Exception ex){
				ex.printStackTrace();
			}
			finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException sqlEx) {
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
				if(ps != null){
					ps.close();
				}
			}
			ret = true;
		}catch(Exception ex){
			
		}
		return ret;
	}


}
