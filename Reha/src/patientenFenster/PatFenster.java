package patientenFenster;

import hauptFenster.Reha;


import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JInternalFrame;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.FlowLayout;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.mysql.jdbc.Statement;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JTextArea;
import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatterFactory;

import org.jdesktop.swingx.JXFrame;

import terminKalender.DatFunk;

public class PatFenster extends JInternalFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JPanel jPanel = null;
	private JTextField jTextField = null;
	private JButton jButton = null;
	private Reha Eltern = null;
	private JPanel jPanel1 = null;
	private JLabel jLabel = null;
	private JScrollPane jScrollPane = null;
	private JTable jTable = null;
	private JScrollPane jScrollPane1 = null;
	private JTextArea jTextArea = null;
	private JFormattedTextField jFormattedTextField = null;
	/**
	 * This is the xxx default constructor
	 */
	public PatFenster(Reha owner) {
		super();
		initialize();
		Eltern = owner;
		
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(668, 418);
		this.setContentPane(getJContentPane());
		this.setClosable(true);
		this.setResizable(true);
		this.setVisible(true);
		System.out.println("PatFenster ist gestartet");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJPanel(), BorderLayout.NORTH);
			jContentPane.add(getJPanel1(), BorderLayout.SOUTH);
			jContentPane.add(getJScrollPane(), BorderLayout.CENTER);
			jContentPane.add(getJScrollPane1(), BorderLayout.EAST);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jLabel = new JLabel();
			jLabel.setText("Nachname,Vorname");
			jLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
			jPanel = new JPanel();
			jPanel.setLayout(new FlowLayout());
			jPanel.setPreferredSize(new Dimension(0, 35));
			jPanel.add(jLabel, null);
			jPanel.add(getJTextField(), null);
			jPanel.add(getJFormattedTextField(), null);
			jPanel.add(getJButton(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setPreferredSize(new Dimension(100, 20));
			jTextField.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyReleased(java.awt.event.KeyEvent e) {
					System.out.println("keyReleased()"+e.getKeyCode()); // TODO Auto-generated Event stub keyReleased()
					if (e.getKeyCode() == 10){
						SuchePatient();
					}
					if (e.getKeyCode() == 40){
						jTable.requestFocus();
						jTable.dispatchEvent(e);
					}
				}
			});
		}
		return jTextField;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("Suchen");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					//System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
					SuchePatient();
				}
			});
		}
		return jButton;
	}

	private void SuchePatient(){
		Statement stmt = null;
		ResultSet rs = null;
		String sstmt = new String();
		Vector <Vector<String[]>>dataVector = new Vector<Vector<String[]>>();
		DefaultTableModel tblDataModel = new DefaultTableModel();
		Vector reiheVector = new Vector();
		reiheVector.addElement("Nachname");
		reiheVector.addElement("Nachname");
		reiheVector.addElement("Geboren");
		reiheVector.addElement("Patientennummer");
		String[] suche;
		if (jTextField.getText().trim().contains(" ") ){
			suche = jTextField.getText().split(" ");
			sstmt = "Select n_name,v_name,geboren,pat_intern  from pat5 where n_name LIKE '"+
			suche[0] +"%' AND v_name LIKE '"+suche[1]+"%' order by n_name,v_name";			
		}else{
			sstmt = "Select n_name,v_name,geboren,pat_intern from pat5 where n_name LIKE '"+
			jTextField.getText() +"%'  order by n_name,v_name";
		}
		try {
			stmt = (Statement) this.Eltern.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE );
			try{
				rs = (ResultSet) stmt.executeQuery(sstmt);		
				while( rs.next()){
					Vector rowVector = new Vector();
					for(int i = 1; i <= 4; i++){
						//System.out.println(rs.getString(i));
					rowVector.addElement((i==3 ? (rs.getString(i) != null ? DatFunk.sDatInDeutsch((String) rs.getString(i)) : "  .  .  ") : rs.getString(i)) );
					//rowVector.addElement(rs.getString(i) );
					}
					dataVector.addElement(rowVector);
				}
				tblDataModel.setDataVector(dataVector,reiheVector);
				this.jTable.setModel(tblDataModel);
				this.jTable.updateUI();
			}catch(SQLException ev){
        		System.out.println("SQLException: " + ev.getMessage());
        		System.out.println("SQLState: " + ev.getSQLState());
        		System.out.println("VendorError: " + ev.getErrorCode());
			}	

		}catch(SQLException ex) {
			System.out.println("von stmt -SQLState: " + ex.getSQLState());
		}

		finally {
			if (rs != null) {
			try {
				rs.close();
			} catch (SQLException sqlEx) { // ignore }
				rs = null;
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
/***************************/
	private void DBHoleAlles(String patnummer){
		Statement stmt = null;
		ResultSet rs = null;
		String sstmt = new String();
;
		String stext = new String();
		sstmt = "Select * from pat5 where pat_intern = '"+patnummer+"'";
		try {
			stmt = (Statement) this.Eltern.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE );
			try{
				rs = (ResultSet) stmt.executeQuery(sstmt);		
				while( rs.next()){
						stext = (rs.getString(1) == null ? "" : rs.getString(1));
				}
				jTextArea.setText(stext);
				jTextArea.setCaretPosition(0);
			}catch(SQLException ev){
        		System.out.println("SQLException: " + ev.getMessage());
        		System.out.println("SQLState: " + ev.getSQLState());
        		System.out.println("VendorError: " + ev.getErrorCode());
			}	

		}catch(SQLException ex) {
			System.out.println("von stmt -SQLState: " + ex.getSQLState());
		}

		finally {
			if (rs != null) {
			try {
				rs.close();
			} catch (SQLException sqlEx) { // ignore }
				rs = null;
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

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.setLayout(new GridBagLayout());
			jPanel1.setPreferredSize(new Dimension(0, 25));
		}
		return jPanel1;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTable());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getJTable() {
		if (jTable == null) {
			jTable = new JTable();
			jTable.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					System.out.println("mouseClicked()"); // TODO Auto-generated Event stub mouseClicked()
					PatientHoleAlles();
				}
			});
			jTable.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyReleased(java.awt.event.KeyEvent e) {
					System.out.println("keyReleased()"); // TODO Auto-generated Event stub keyReleased()
					if(e.getKeyCode() == 40 || e.getKeyCode() == 38){
						PatientHoleAlles();
					}
				}
			});
		}
		return jTable;
	}

	private void PatientHoleAlles(){
		int col = 3; //jTable.getSelectedColumn(); 
		int row = jTable.getSelectedRow(); 
		//System.out.println( jTable.getModel().getValueAt(row, col) );
		DBHoleAlles((String) jTable.getModel().getValueAt(row, col) );
	}

	/**
	 * This method initializes jScrollPane1	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setPreferredSize(new Dimension(200, 3));
			jScrollPane1.setViewportView(getJTextArea());
		}
		return jScrollPane1;
	}

	/**
	 * This method initializes jTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			jTextArea.setLineWrap(true);
		}
		return jTextArea;
	}

	/**
	 * This method initializes jFormattedTextField	
	 * 	
	 * @return javax.swing.JFormattedTextField	
	 */
	private JFormattedTextField getJFormattedTextField() {
		if (jFormattedTextField == null) {
			jFormattedTextField = new JFormattedTextField();
			jFormattedTextField.setFormatterFactory(new DefaultFormatterFactory());
		}
		return jFormattedTextField;
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
