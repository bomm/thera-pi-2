package theraPiUpdates;





import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;





public class SqlInfo {
	
/**
 * @throws SQLException *********************************/	
	public static void sqlAusfuehren(Connection conn,String sstmt) throws SQLException{

		Statement stmt = null;
		stmt =  conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );

			stmt.execute(sstmt);
			
			if (stmt != null) {
					stmt.close();
			}

		return;
	}
	
/*****************************************/

	
}
