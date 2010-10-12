package therapiDBAdmin;



import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class Seite3 extends JXPanel  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	MySqlTab eltern = null;
	JLabel sqlFile = null;
	JTextArea ta = null;
	JButton[] buts = {null,null,null,null};
	String pfad = TheraPiDbAdmin.proghome+"tableImport/";
	Seite3(MySqlTab xeltern){
		super();
		eltern = xeltern;
		//					     1             2           3     4         5
		String xwerte = "fill:0:grow(0.5),right:max(250;p),5dlu,150dlu,fill:0:grow(0.5)";
		//                       1        2  3   4  5   6  7   8  9  10  11  12    13              14  15    
		String ywerte = "fill:0:grow(0.33),p,5dlu,p,5dlu,p,5dlu,p,5dlu,p, 5dlu,p,fill:0:grow(0.66),2dlu,p,5dlu";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		setLayout(lay);
		sqlFile = new JLabel("noch keine Tabellendefinition ausgewählt");
		sqlFile.setForeground(Color.BLUE);
		add(sqlFile,cc.xy(2,2));
		
		buts[0] = new JButton("Tabelledifinition auswählen");
		buts[0].addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				doOpenSqlFile();
			}
			
		});
		add(buts[0],cc.xy(4,2));

		buts[1] = new JButton("Tabellenimport starten");
		buts[1].addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						try{
						buts[0].setEnabled(false);
						buts[1].setEnabled(false);
						TheraPiDbAdmin.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
						if(!doImportStarten()){
							TheraPiDbAdmin.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
							JOptionPane.showMessageDialog(null,"Fehler beim Datenimport evtl. ist die Importdatei beschädigt");
						}else{
							TheraPiDbAdmin.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
							JOptionPane.showMessageDialog(null,"Tabellen wurden erfolgreich importiert.");
							eltern.setSeite3Ok(true);
							eltern.sqlTab.setSelectedIndex(3);
							//Runtime.getRuntime().exec("java -jar FirstRun.jar");
							//System.exit(0);
						}
						}catch(Exception ex){
							ex.printStackTrace();
						}
						return null;
					}
				}.execute();
			}
		});
		buts[1].setEnabled(false);
		add(buts[1],cc.xy(4,4));
		ta = new JTextArea();
		JScrollPane jscr = new JScrollPane(ta);
		jscr.validate();
		add(jscr,cc.xyw(1, 13, 5));
		
		buts[2] = new JButton("Assistenten beenden");
		buts[2].addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}			
		});
		add(buts[2],cc.xy(5, 15));

		validate();
	}
	private boolean doImportStarten(){
		
		boolean bret = true;

		String thisLine, sqlQuery;
		Statement stmt = null;
		try {
			BufferedReader d = new BufferedReader(new FileReader(sqlFile.getText()));
			stmt = TheraPiDbAdmin.conn_db.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
		            ResultSet.CONCUR_UPDATABLE );

		    sqlQuery = "";
		    while ((thisLine = d.readLine()) != null) 
		    {  
		        if(thisLine.length() > 0 && thisLine.charAt(0) == '-' || thisLine.length() == 0 ) 
		            continue;
		        sqlQuery = sqlQuery + " " + thisLine;

		        if(sqlQuery.charAt(sqlQuery.length() - 1) == ';') {
		            sqlQuery = sqlQuery.replace(';' , ' '); 
		            try {
		                stmt.execute(sqlQuery);
		                ta.setText(sqlQuery+"\n"+ta.getText());
		            }
		            catch(SQLException ex) {
		            	ta.setText(ex.getMessage()+"\n"+ta.getText());
		    			return false;
		            }
		            catch(Exception ex) {
		            	ta.setText(ex.getMessage()+"\n"+ta.getText());
		            	return false;
		            }
		            sqlQuery = "";
		        }   
		    }
		    
		}
		catch(IOException ex) {
			ta.setText(ex.getMessage()+"\n"+ta.getText());
			return false;
		}
		catch(Exception ex) {
			ta.setText(ex.getMessage()+"\n"+ta.getText());
			return false;
		}		
		return bret;
	}
	
	private void doOpenSqlFile(){
		String sret = "";
		final JFileChooser chooser = new JFileChooser("Verzeichnis wählen");
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        final File file = new File(pfad);

        chooser.setCurrentDirectory(file);

        chooser.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)
                        || e.getPropertyName().equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
                    //final File f = (File) e.getNewValue();
                }
            }
        });
        chooser.setVisible(true);
       
        final int result = chooser.showOpenDialog(null);
        File inputVerzFile = null;
        if (result == JFileChooser.APPROVE_OPTION) {
            inputVerzFile = chooser.getSelectedFile();

            if(inputVerzFile.getName().trim().equals("")){
            	sret = "";
            }else{
            	sret = inputVerzFile.getName().trim();	
            }
        }else{
        	sret = ""; //vorlagenname.setText(SystemConfig.oTerminListe.NameTemplate);
        }
        chooser.setVisible(false); 
        if(sret.equals("") ){
        	sqlFile.setText("noch keine Tabellendefinition ausgewählt");
        	buts[1].setEnabled(false);
        }else{
        	if(inputVerzFile != null){
            	sqlFile.setText(inputVerzFile.getPath());
            	buts[1].setEnabled(true);
            	pfad = inputVerzFile.getPath();
        	}
        }
        return;
	}

}
