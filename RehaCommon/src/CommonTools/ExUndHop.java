package CommonTools;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ExUndHop extends Thread implements Runnable{
	Statement stmt = null;
	ResultSet rs = null;
	String statement;
	boolean geklappt = false;
	public static boolean processdone = false;
	public void setzeStatement(String statement){
		processdone = false;
		this.statement = statement;
		start();
	}
	public synchronized void run(){
		
		//Vector treadVect = new Vector();
		try {
			stmt = (Statement) SqlInfo.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE );
			try{
					geklappt =  stmt.execute(this.statement);
					
			}catch(SQLException ev){
					//System.out.println("SQLException: " + ev.getMessage());
					//System.out.println("SQLState: " + ev.getSQLState());
					//System.out.println("VendorError: " + ev.getErrorCode());
			}	

		}catch(SQLException ex) {
			//System.out.println("von stmt -SQLState: " + ex.getSQLState());
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
		processdone = true;
	}
	
	
}
