package therapiDBAdmin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import org.jdesktop.swingx.JXPanel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class Seite1 extends JXPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JFormattedTextField[] tfs  = {null,null,null,null,null};
	JPasswordField pw = null;
	JButton[] buts = {null,null};
	MySqlTab eltern = null;

	Seite1(MySqlTab xeltern){
		super();
		eltern = xeltern;
		//					     1             2           3     4         5
		String xwerte = "fill:0:grow(0.5),right:max(250;p),5dlu,150dlu,fill:0:grow(0.5)";
		//                       1        2  3   4  5   6  7   8  9  10  11  12    13           14 15    
		String ywerte = "fill:0:grow(0.5),p,5dlu,p,5dlu,p,5dlu,p,5dlu,p, 5dlu,p,fill:0:grow(0.5),p,5dlu";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		setLayout(lay);
		add(new JLabel("'localhost', oder IP-Adresse, oder www-Adresse des MySql-Servers"),cc.xy(2, 2));
		add(tfs[0] = new JFormattedTextField(),cc.xy(4, 2));
		add(new JLabel("Port des MySql-Servers"),cc.xy(2, 4));
		add(tfs[1] = new JFormattedTextField("3306"),cc.xy(4, 4));
		add(new JLabel("Name der System-Datenbank (mysql)"),cc.xy(2, 6));
		add(tfs[4] = new JFormattedTextField("mysql"),cc.xy(4, 6));
		add(new JLabel("Benutzername für die System-Datenbank (root)"),cc.xy(2, 8));
		add(tfs[3] = new JFormattedTextField("root"),cc.xy(4, 8));
		add(new JLabel("Passwort für den Benutzer root"),cc.xy(2, 10));
		add(pw = new JPasswordField(),cc.xy(4, 10));
		buts[0] = new JButton("MySql-Kontakt testen");
		buts[0].addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!doTestConnectivity()){
					JOptionPane.showMessageDialog(null, "Kontakt zur MySql-Datenbank konnte nicht hergestellt werden!");
					eltern.setSeite1Ok(false);
					return;
				}else{
					JOptionPane.showMessageDialog(null, "Kontakt zur Datenbank wurde erfolgreich hergestellt");
					eltern.setSeite1Ok(true);
					eltern.sqlTab.setSelectedIndex(1);
					buts[0].setEnabled(false);
					MySqlTab.iPAdresse = tfs[0].getText().trim();
					MySqlTab.portAdresse = tfs[1].getText().trim();
					return;
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
			tfs[0].getText().trim()+":"+tfs[1].getText().trim()+"/";
			//tfs[0].getText().trim()+":"+tfs[1].getText().trim()+"/"+tfs[4].getText().trim();
			System.out.println(connection);
			TheraPiDbAdmin.conn_root = (Connection) DriverManager.getConnection(connection,
					tfs[3].getText().trim(),
					String.valueOf(pw.getPassword()));
			//TheraPiDbAdmin.conn_root.close();
			return true;
		}catch(final SQLException ex){
			JOptionPane.showMessageDialog(null, ex.getMessage());
			ex.printStackTrace();
		}
		
		return ret;
	}

}
