package benutzerVerwaltung;


import hauptFenster.ContainerConfig;
import hauptFenster.Reha;



import java.awt.BorderLayout;
import java.awt.Color;

import java.awt.Dimension;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTable;

import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dialoge.RehaSmartDialog;

import systemEinstellungen.SystemConfig;
import terminKalender.ParameterLaden;
import events.RehaEventListener;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;

public class BenutzerVerwaltung extends JScrollPane implements RehaTPEventListener{ 

	/**
	 * 
	 */
	private static final long serialVersionUID = -5809398507546826743L;
	private int setOben;
	private RehaTPEventClass xEvent; 
	private RehaTPEventClass rtp = null;
	private RehaEventListener evt;
	private JXPanel jp1 = null;
	private JXPanel jp2 = null;	

	private JXPanel jpLinks = null;
	private JXPanel jpRechts = null;
	private JXPanel jpx = null;
	
	private JComboBox cb = null;
	
	private JXButton bnew = null;
	private JXButton bedit = null;
	private JXButton bdel = null;
	private JXButton bsave = null;
	private boolean neu = false;
	private boolean inaktion = false;
	
	private JTextField tf = null;
	private JPasswordField[] pf = {null,null}; 
	
	private JXTable jxTable = null;
	private AbstractTableModel tblDataModel = null; 
	private String[] rechte = {"Alle Rechte (User root)","Rezepte abrechnen","Patientendaten �ndern"};  

	public BenutzerEvent bevent = null;
	public BenutzerVerwaltung(int setOben){
		super();

		this.setOben = setOben;
		
		
		setBorder(null);
		setViewportBorder(null);
		bevent = new BenutzerEvent(this);
		bevent.setDetails("","");
		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener((RehaTPEventListener) this);
		//rtp.addListener((RehaTPEventListener) this);

		jp1 = new JXPanel();
		jp1.setBorder(null);
		jp1.setBackground(Color.WHITE);
		//jp1.setBackgroundPainter(new GradientPainter());

/*******************************************************/			
        jp1.setLayout(new BorderLayout());
        //jp1.setLayout(new VerticalLayout(1));
        String ss = "icons/header-image.png";
        JXHeader header = new JXHeader("Benutzerverwaltung",
                "Hier legen Sie die Benutzernamen des Programmes fest. Sie können Passwörter erstellen oder ändern.\n" +
                "Der Benutzername des eingeloggten Users erscheint später im Fenster-Titel. \n" +
                "Darüberhinaus können Sie hier jedem Benutzer individuelle Berechtigungen für einzelne Programmteile zuweisen.",
                new ImageIcon(ss));
        //header.setBackgroundPainter(Reha.RehaPainter[0]);
        /*
        GradientPainter gp1 = new GradientPainter();
        gp1.addSegment(new GradientSegment(new Color(255,255,255), new Color(192,192,192), 0.0,0.30114943,false));
        gp1.addSegment(new GradientSegment(new Color(192,192,192), new Color(128,128,128), 0.30114943,0.7241379,false));
        gp1.addSegment(new GradientSegment(new Color(128,128,128), new Color(0,0,0), 0.7241379,1.0,false));
		*/
        //header.setBackgroundPainter((Painter) gp1);
        jp1.add(header,BorderLayout.NORTH);
        JXPanel unten = new JXPanel(new GridLayout(1,2));
        unten.setBorder(null);
        JScrollPane ptc = new JScrollPane();
        ptc.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        ptc.setViewportView(getLinks());
        JXPanel neu = new JXPanel(new GridBagLayout());
        neu.setBorder(null);
		GridBagConstraints gridBagConstraints = new GridBagConstraints() ;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.anchor = GridBagConstraints.CENTER;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1.0D;
		gridBagConstraints.weighty = 1.0D;
		gridBagConstraints.insets = new Insets(10,10,10,10);

        
        neu.add(ptc,gridBagConstraints);
        unten.add(neu);
        //unten.add(ptc);//unten links = ptc o.k.

        JXPanel neurechts = new JXPanel(new GridBagLayout());
        neurechts.setBorder(null);
        /*
		Vector <String>reiheVector = new Vector<String>();
		reiheVector.addElement("Programmteil / Userart");
		reiheVector.addElement("Berechtigung");

		Vector dataVector = new Vector();
		Vector testVector = new Vector();
		
		testVector.addElement(" ");
		testVector.addElement(new Boolean(false));
		dataVector.addElement(testVector);
		*/
		
		tblDataModel = new DefaultTableModel();
		jxTable = new JXTable(tblDataModel);
		jxTable.setEditable(true);
		//jxTable.setModel(tblDataModel);
		//jxTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JCheckBox()));
		jxTable.validate();
		//jxTable = new JXTable();
		TabelleSetzen(0);
        
        ptc = new JScrollPane();
        ptc.setViewportBorder(null);
        ptc.setViewportView(jxTable);
        ptc.revalidate();
        
        neurechts.add(ptc,gridBagConstraints);
        unten.add(neurechts);
        
        //jp1.add(ptc,BorderLayout.CENTER);
        jp1.add(unten,BorderLayout.CENTER);
   
        jp1.revalidate();
		this.setViewportView(jp1);
		this.revalidate();
		this.addComponentListener(new java.awt.event.ComponentAdapter() {
			public void componentResized(java.awt.event.ComponentEvent e) {
				revalidate();
			}
		});

		
	}	
	

/*********************************************************/
	
public void FensterSchliessen(String welches){
	((RehaSmartDialog)this.getParent().getParent().getParent().getParent().getParent()).dispose();
	////System.out.println("Eltern-->"+this.getParent().getParent().getParent().getParent().getParent());
	//Reha.thisClass.TPschliessen(setOben,(Object) this.getParent().getParent().getParent().getParent().getParent(),welches);
}


@Override
public void rehaTPEventOccurred(RehaTPEvent evt) {
	// TODO Auto-generated method stub
//	//System.out.println("****************das darf doch nicht wahr sein**************");
	String ss = ((JXTitledPanel) this.getParent()).getContentContainer().getName();	
//	

	if (evt.getDetails()[0].equals(ss) && evt.getDetails()[1]=="ROT"){
		//System.out.println("Von BenutzerVerwaltung - ROT "+this.getName()+" Eltern "+ss);	
		FensterSchliessen(evt.getDetails()[0]);
		rtp.removeRehaTPEventListener((RehaTPEventListener) this);
	}	
	if (evt.getDetails()[0].equals(ss) && evt.getDetails()[1]=="GRUEN"){
		ContainerConfig conf = new ContainerConfig();
		conf.addContainer("personen16.gif",evt.getDetails()[0],this.getParent().getParent().getParent().getParent().getParent(),null);
		//System.out.println("Name für Container verkleinern = "+ss);
		//rtp.removeRehaTPEventListener((RehaTPEventListener) this);
	}	
	

}


/******************************************/
	private JScrollPane getLinks(){
		jpLinks = new JXPanel();
		jpLinks.setBorder(null);
		//jpLinks.setBackground(Color.WHITE);
		//jpLinks.setBackgroundPainter(Reha.RehaPainter[0]);
		jpLinks.setLayout(new GridBagLayout());
		//jpLinks.setPreferredSize(new Dimension(500,500));
		//jpLinks.setLayout(new BorderLayout());
		GridBagConstraints gridBagConstraints = new GridBagConstraints() ;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.anchor = GridBagConstraints.CENTER;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1.0D;
		gridBagConstraints.weighty = 1.0D;
		gridBagConstraints.insets = new Insets(10,10,10,10);
		
		jpx = new JXPanel();
		jpx.setBorder(null);
		//jpx.setBackground(Color.WHITE);
		//jpx.setBackgroundPainter(Reha.RehaPainter[0]);
		jpx.setPreferredSize(new Dimension(350,550));
		jpx.setMinimumSize(new Dimension(280,280));
		 FormLayout layout = new FormLayout("10dlu,80dlu ,4dlu, 80dlu,4dlu,80dlu,4dlu,80dlu,4dlu",
			"80dlu,15dlu,15dlu,15dlu,30dlu,15dlu");
			jpx.setLayout(layout);
		 CellConstraints cc = new CellConstraints();

		 JXLabel lbl = new JXLabel("Benutzer wählen");
		 jpx.add(lbl,cc.xy(2,1));
		 
		 cb = new JComboBox();
		 jpx.add(cb,cc.xy(4,1));
		 
		 lbl = new JXLabel("Benutzername");
		 jpx.add(lbl,cc.xy(2,2));

		 tf = new JTextField();
		 jpx.add(tf,cc.xy(4,2));

		 lbl = new JXLabel("Passwort");
		 jpx.add(lbl,cc.xy(2,3));

		 pf[0] = new JPasswordField();
		 jpx.add(pf[0],cc.xy(4,3));
		 
		 lbl = new JXLabel("Passwort wiederholen");
		 jpx.add(lbl,cc.xy(2,4));

		 pf[1] = new JPasswordField();
		 jpx.add(pf[1],cc.xy(4,4));
		 
			//private JXButton bnew = null;
			//private JXButton bedit = null;
			//private JXButton bdel = null;
			//private JXButton bsave = null;

		 bnew = new JXButton("neuer Benutzer");
		 bnew.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					neuerBenutzer();
					neu = true;					
				}
			});	
		 jpx.add(bnew,cc.xy(2,5));

		 bedit = new JXButton("Benutzer ändern");
		 bedit.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					speichernBenutzer();
					neu = false;
				}
			});		 
		 jpx.add(bedit,cc.xy(4,5));
		 
		 bsave = new JXButton("Benutzer speichern");
		 bsave.setEnabled(false);
		 bsave.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					speichernBenutzer();
					neu = false;
				}
					
			});		 
		 jpx.add(bsave,cc.xy(2,6));

		 bdel = new JXButton("Benutzer löschen");
		 bdel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					loeschenBenutzer();				
				}
			});		 
		 jpx.add(bdel,cc.xy(4,6));
		 
		 ComboFuellen();
		 
		 jpx.addComponentListener(new java.awt.event.ComponentAdapter() {
				public void componentResized(java.awt.event.ComponentEvent e) {
					jpx.setPreferredSize(new Dimension(e.getComponent().getMinimumSize().width,e.getComponent().getMinimumSize().height));
					e.getComponent().setPreferredSize(new Dimension(e.getComponent().getMinimumSize().width,e.getComponent().getMinimumSize().height));
					jpx.revalidate();
				}
			});

		 /*
		 this.jFormPanel.add(jB2,cc.xy(4,1));
		    this.jFormPanel.add(jB3,cc.xy(6,1));
		    this.jFormPanel.add(jB4,cc.xy(8,1));	    
		    this.jFormPanel.add(jB5,cc.xy(2,2));
		    this.jFormPanel.add(jlbl,cc.xy(4,2));
		    this.jFormPanel.add(jxRB,cc.xy(6,2));
		    this.jFormPanel.add(jxCB,cc.xy(8,2));
		    this.jFormPanel.setVisible(true);
		  */  
		 
		 //JScrollPane scr = new JScrollPane();
		//jpLinks.add(scr,BorderLayout.CENTER);
		 
		jpLinks.add(jpx,gridBagConstraints);
		jpLinks.setVisible(true);
		jpLinks.validate();
		JScrollPane scr = new JScrollPane();
		scr.setBorder(null);
		scr.setViewportBorder(null);
		scr.setViewportView(jpLinks);

		
		return scr;
	}


	private void neuerBenutzer(){
		neu = true;
		tf.setText("");
		pf[0].setText("");
		pf[1].setText("");
		int i;
		for(i=0;i<40;i++){
			jxTable.setValueAt(Boolean.valueOf(false),i,1);
		}
		tf.requestFocus();
	}
	
	private void speichernBenutzer(){
		String sr = holeRechte();
		String name = tf.getText().toUpperCase();
		tf.setText(name);
		String stmt = "";
		String hk  = "'"; 
		int aktuell;
		if( ! pf[0].getText().toUpperCase().equals(pf[1].getText().toUpperCase()) ){
			return ;
		}
		String passw = pf[0].getText().toUpperCase();
		if (neu){
			stmt = "Insert into rehalogin set user="+ hk+ name +hk +
					", password =" + hk+ passw+hk + ", rights="+ hk+sr+hk;
		}else{
			stmt = "Update rehalogin set user="+ hk+ name +hk +
			", password =" + hk+ passw+hk + ", rights="+ hk+sr+hk+" where ID="+ ParameterLaden.pKollegen.get(cb.getSelectedIndex()).get(4).toString();
			
		}
		//System.out.println(stmt);
		MacheStatement(stmt);
		if(neu){
			aktuell = (cb.getSelectedIndex()+1);
		}else{
			aktuell = cb.getSelectedIndex();
		}
		inaktion = true;
		ParameterLaden.Passwort();
		cb.removeAllItems();
		ComboFuellen();
		cb.setSelectedIndex(aktuell);
		WerteSetzen(aktuell);
		TabelleSetzen(aktuell);
		inaktion = false;
		neu = false;
				
	}
	private void loeschenBenutzer(){
		String sr = holeRechte();
		String name = tf.getText().toUpperCase();
		tf.setText(name);
		String stmt = "";
		String hk  = "'"; 
		int aktuell;
		if (neu){
			return;
		}else{
			stmt = "Delete from rehalogin where ID="+ ParameterLaden.pKollegen.get(cb.getSelectedIndex()).get(4).toString();
		}
		MacheStatement(stmt);
		aktuell = cb.getSelectedIndex();
		inaktion = true;
		ParameterLaden.Passwort();
		cb.removeAllItems();
		ComboFuellen();
		if(aktuell <= (ParameterLaden.pKollegen.size()-1)){
			cb.setSelectedIndex(aktuell);
			WerteSetzen(aktuell);
			TabelleSetzen(aktuell);
		}	
		inaktion = false;
		neu = false;
	}
	private String holeRechte(){
		StringBuffer sb = new StringBuffer();
		int i;
		for (i=0;i<40;i++){
			sb.append(   jxTable.getValueAt(i,1).toString().equals("true") ? "1" : "0" );
		}
		
		return sb.toString();
	}
	


	private void ComboFuellen(){
		int i,size;
		size = ParameterLaden.pKollegen.size();
		for(i=0;i<size;i++){
			cb.addItem(ParameterLaden.pKollegen.get(i).get(0));
		}
		cb.setSelectedItem(ParameterLaden.pKollegen.get(0).get(0));
		cb.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				if(! inaktion){
				WerteSetzen(cb.getSelectedIndex());
				TabelleSetzen(cb.getSelectedIndex());
				}
			}
		});	
		WerteSetzen(0);
	}


	private void WerteSetzen(int wert){
		//System.out.println("ItemIndex = "+wert);
		tf.setText((String) ParameterLaden.pKollegen.get(wert).get(0));
		pf[0].setText((String) ParameterLaden.pKollegen.get(wert).get(1));
		pf[1].setText((String) ParameterLaden.pKollegen.get(wert).get(1));		
	}
	
	private void TabelleSetzen(int wert){
		Object[][] dataVector = 
				{{null,null},{null,null},{null,null},{null,null},{null,null},
				{null,null},{null,null},{null,null},{null,null},{null,null},
				{null,null},{null,null},{null,null},{null,null},{null,null},
				{null,null},{null,null},{null,null},{null,null},{null,null},
				{null,null},{null,null},{null,null},{null,null},{null,null},
				{null,null},{null,null},{null,null},{null,null},{null,null},
				{null,null},{null,null},{null,null},{null,null},{null,null},
				{null,null},{null,null},{null,null},{null,null},{null,null}	};

		String[] rehaRechte = {
				"Benutzer ist ROOT und besitzt alle Rechte","Ändern von Patientestammdaten erlaubt",
				"Darf patientenbezogene Umsätze einsehen",
				"Hat volles Schreibrecht im Terminkalender","Darf nur leere Termine beschreiben",
				"Darf mit 'Roogle' Termine überschreiben (Achtung!!!!!!)","Zugang zur Barkassen Abrechnung",
				"Zugang zur Rezept-/Rehaabrechnung","Voller Zugang zur Urlaubs-/Überstundenermittlung",
				"Darf eigenen Urlaub-/Überstunden einsehen","Zugang zur Anmeldestatistik",
				"Zugang zum Modul Tagesumsätze","Zugang zum Modul Mitarbeiterumsatz",
				"Zugang zum Modul Freie Mitarbeiter-Abrechnung ","Zugang zum Verkaufsmodul",
				"Zugang zum SQL-Modul (Achtung!!!!!!)", "Zugang zur Benutzerverwaltung",
				"Darf alle Berichtsarten erstellen/ändern","Darf nur therapeutische Berichte erstellen/ändern",
				"Kann alle Textbausteine erstellen/ändern","Kann nur therapeutische Textbausteine erstellen/ändern",
				"Darf alle Fremdprogramme starten","Darf nur Fremdprogramme bis Level-1 starten",
				"Darf Zeitmasken anlegen","Unbelegt","Unbelegt","Unbelegt","Unbelegt","Unbelegt","Unbelegt","Unbelegt",
				"Unbelegt","Unbelegt","Unbelegt","Unbelegt","Unbelegt","Unbelegt","Unbelegt","Unbelegt","Unbelegt","Unbelegt"
				};
		//DefaultTableModel tblDataModel = new DefaultTableModel();
		
		String[] column = {"Programmteil / Userart","Berechtigung"};
		int i,size;
		String sRechte = (String) ParameterLaden.pKollegen.get(wert).get(2);
		size = sRechte.length() -1;
		//System.out.println("Ingesamt Recht = "+size);
		for(i=0;i<40;i++){
			Vector rowVector = new Vector();
			rowVector.add("Art der Berechtigung "+i);
			//rowVector.add(rechte[i]);
			rowVector.add(sRechte.substring(i,i).equals("1") ? Boolean.valueOf(true) : Boolean.valueOf(false));
			dataVector[i][0] = rehaRechte[i]; // rechte[i];
			dataVector[i][1] = sRechte.substring(i,i+1).equals("1") ? Boolean.valueOf(true) : Boolean.valueOf(false);			
		}

		//((DefaultTableModel) jxTable.getModel()).setDataVector(dataVector,column);
		//jxTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JCheckBox()));

		//tblDataModel = new DefaultTableModel();
		//tblDataModel.setDataVector(dataVector,column);
		
		MyTableModel myTable = new MyTableModel();
		myTable.columnNames = column;
		myTable.data = dataVector;
		//System.out.println("Klasse von Column 2 = "+myTable.getColumnClass(1));
		//jxTable.setModel(tblDataModel);
		jxTable.setModel(myTable);
		jxTable.getColumn(1).setMinWidth(100);	
		jxTable.getColumn(1).setMaxWidth(100);
		//jxTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JCheckBox()));

		jxTable.setEditable(true);
		jxTable.validate();
	}


	protected void MacheStatement(String sstmt) {

		// TODO Auto-generated method stub
		Statement stmt = null;
		boolean rs = false;
		
		try {
			stmt = Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE );
			try{
				rs = stmt.execute(sstmt);				
			}catch(SQLException ex) {
				//System.out.println("SQLException: " + ex.getMessage());
        		//System.out.println("SQLState: " + ex.getSQLState());
        		//System.out.println("VendorError: " + ex.getErrorCode());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
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


class MyTableModel extends AbstractTableModel {
    private static final boolean DEBUG = false;

    //public String[] columnNames = null;
    //public Object[][] data = null;    
    
    public String[] columnNames = { "", "",};

    public Object[][] data = {{"",Boolean.valueOf(false)}};

    public int getColumnCount() {
      return columnNames.length;
    }

    public int getRowCount() {
      return data.length;
    }

    public String getColumnName(int col) {
      return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
      return data[row][col];
    }

    /*
     * JTable uses this method to determine the default renderer/ editor for
     * each cell. If we didn't implement this method, then the last column
     * would contain text ("true"/"false"), rather than a check box.
     */
    public Class getColumnClass(int c) {
      return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's editable.
     */
    public boolean isCellEditable(int row, int col) {
      //Note that the data/cell address is constant,
      //no matter where the cell appears onscreen.
      if (col < 1 ) {
        return false;
      } else {
        return true;
      }
    }

    /*
     * Don't need to implement this method unless your table's data can
     * change.
     */
    public void setValueAt(Object value, int row, int col) {
      if (DEBUG) {
        //System.out.println("Setting value at " + row + "," + col
            //+ " to " + value + " (an instance of "
            //+ value.getClass() + ")");
      }

      data[row][col] = value;
      fireTableCellUpdated(row, col);

      if (DEBUG) {
        //System.out.println("New value of data:");
        printDebugData();
      }
    }

    private void printDebugData() {
      int numRows = getRowCount();
      int numCols = getColumnCount();

      for (int i = 0; i < numRows; i++) {
        //System.out.print("    row " + i + ":");
        for (int j = 0; j < numCols; j++) {
          //System.out.print("  " + data[i][j]);
        }
        //System.out.println();
      }
      //System.out.println("--------------------------");
    }
  }
