package therapiDBAdmin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import org.jdesktop.swingx.JXPanel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class Seite2 extends JXPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JFormattedTextField[] tfs  = {null,null,null,null,null};
	JPasswordField pw1 = null;
	JPasswordField pw2 = null;
	JCheckBox checkalt = null;
	JButton[] buts = {null,null};
	MySqlTab eltern = null;
	
	Seite2(MySqlTab xeltern){
		eltern = xeltern;
		//					     1             2           3     4         5
		String xwerte = "fill:0:grow(0.5),right:max(250;p),5dlu,150dlu,fill:0:grow(0.5)";
		//                       1        2  3   4  5   6  7   8  9  10  11  12    13           14 15    
		String ywerte = "fill:0:grow(0.5),p,5dlu,p,5dlu,p,5dlu,p,5dlu,p, 5dlu,p,fill:0:grow(0.5),p,5dlu";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		setLayout(lay);
		add(new JLabel("<html>Bitte den Namen für die <b>neue(!) Datenbank</b> eingeben</html>"),cc.xy(2, 2));
		add(tfs[0] = new JFormattedTextField(),cc.xy(4, 2));
		add(new JLabel("<html>Bitte den Namen fur den <b>neuen(!) Datenbankbenutzer</b> eingeben</html>"),cc.xy(2, 4));
		add(tfs[1] = new JFormattedTextField(),cc.xy(4, 4));
		add(new JLabel("<html>Bitte das Passwort für den <b>neuen(!) Datenbankzugang<b> eingeben</html>"),cc.xy(2, 6));
		add(tfs[2] = new JFormattedTextField(),cc.xy(4, 6));
		add(new JLabel("<html>Bitte das Passwort wiederholen</html>"),cc.xy(2, 8));
		add(pw1 = new JPasswordField(),cc.xy(4, 8));
		
		checkalt = new JCheckBox("nicht neu erstellen sondern vorhandene DB mit diesen Daten öffnen");
		add(checkalt,cc.xy(4, 10));
		
		buts[0] = new JButton("Datenbank und Benutzer erstellen");
		buts[0].addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(checkalt.isSelected()){
					if(doTestConnectivity()){
						eltern.setSeite2Ok(true);
						eltern.sqlTab.setSelectedIndex(2);
						buts[0].setEnabled(false);
						doTestConnectivity();
					}else{
						eltern.setSeite2Ok(false);
					}
				}else{
					if(!testDBCreate()){
						JOptionPane.showMessageDialog(null, "Die Anlage der Datenbank und/oder neuem Benutzer ist fehlgeschlagen");
						return;
					}else{
						JOptionPane.showMessageDialog(null, "Datenbank und Datenbank-Benutzer wurden erfolgreich angelegt");
						eltern.setSeite2Ok(true);
						eltern.sqlTab.setSelectedIndex(2);
						buts[0].setEnabled(false);
						doTestConnectivity();
					}
				}
			}
			
		});
		add(buts[0],cc.xy(4, 12));

		buts[1] = new JButton("Assistenten beenden");
		buts[1].addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}			
		});
		add(buts[1],cc.xy(5, 14));
		
		validate();
	}
	
	private boolean doTestConnectivity(){
		boolean ret = false;
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (InstantiationException e) {
			e.printStackTrace();
        } catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}	
		try{
			//jdbc:mysql://192.168.2.3:3306/rtadaten
			String connection = "jdbc:mysql://"+
			MySqlTab.iPAdresse+":"+MySqlTab.portAdresse+"/"+tfs[0].getText().trim();
			//tfs[0].getText().trim()+":"+tfs[1].getText().trim()+"/"+tfs[4].getText().trim();
			TheraPiDbAdmin.conn_db = (Connection) DriverManager.getConnection(connection,
					tfs[1].getText().trim(),
					tfs[2].getText().trim() );
			return true;
		}catch(final SQLException ex){
			JOptionPane.showMessageDialog(null, ex.getMessage());
			ex.printStackTrace();
		}
		
		return ret;
	}

	
	private boolean testDBCreate(){
		boolean bret = true;
		
		if(!String.valueOf(pw1.getPassword()).equals(tfs[2].getText().trim()) ){
			JOptionPane.showMessageDialog(null, "Die Passwortwiederholung ist nicht identisch");
			return false;
		}
		if(tfs[0].getText().trim().equals("") || tfs[1].getText().trim().equals("") || 
				tfs[2].getText().trim().equals("")){
			JOptionPane.showMessageDialog(null, "Ein oder mehrere Eingabefelder sind leer - das ist unzulässig");
			return false;
		}
		Statement stmt = null;
		try{
			stmt = TheraPiDbAdmin.conn_root.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
			stmt.executeUpdate("CREATE DATABASE "+tfs[0].getText().trim());
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null, ex.getMessage());
			JOptionPane.showMessageDialog(null,"Anlegen der Datenbank fehlgeschlagen, vermutlich existiert die DB bereits!");
			//JOptionPane.showMessageDialog(null,ex.getMessage());
			return false;
		}
		try{
			stmt.executeUpdate("GRANT ALL ON "+tfs[0].getText().trim()+".* TO '"+
					tfs[1].getText().trim()+"'@'%' IDENTIFIED BY '"+
					tfs[2].getText().trim()+"'");
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null, ex.getMessage());
			JOptionPane.showMessageDialog(null,"Anlegen des neuen Benutzers fehlgeschlagen, vermutlich existiert der Benutzer bereits!");
			//JOptionPane.showMessageDialog(null,ex.getMessage());
			return false;
		}
		return bret;
	}

}
