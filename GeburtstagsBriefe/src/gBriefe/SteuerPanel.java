package gBriefe;



import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.table.TableColumnExt;



import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SteuerPanel extends JXPanel implements ActionListener,MouseListener,KeyListener{
	public JFormattedTextField[] jtf = {null,null,null,null,null};
	public JFormattedTextField suchdatum;
	public MyGbTableModel dtblm;
	public JXTable jtab = null;
	public JLabel anzahl = null;
	public static SteuerPanel thisClass;
	public int aktJahr; 
	public Vector gebTag = new Vector();
	public String aktPat = "";
	public boolean nachtraeglich;
	public int insgesamt;
	public Vector noPrint = new Vector();
	public JCheckBox direktPrint = null;
	
	
	public SteuerPanel(){
		
		
		super();
		thisClass = this;
		
		aktJahr = new Integer(datFunk.sHeute().substring(6));
		
		setPreferredSize(new Dimension(0,250));
		setBackground( Color.WHITE);
		Point2D start = new Point2D.Float(0, 0);
	     Point2D end = new Point2D.Float(0,250);
	     float[] dist = {0.0f, 0.75f};
	     Color[] colors = {Color.WHITE,new Color(231,120,23)};
	     //Color[] colors = {Color.WHITE,Colors.TaskPaneBlau.alpha(0.5f)};
	     //Color[] colors = {Color.WHITE,getBackground()};
	     LinearGradientPaint p =
	         new LinearGradientPaint(start, end, dist, colors);
	     MattePainter mp = new MattePainter(p);
	     setBackgroundPainter(new CompoundPainter(mp));		
		FormLayout lay = new FormLayout("10dlu,right:max(50dlu;p),2dlu,150dlu,2dlu,150dlu,20dlu,p,5dlu,fill:0:grow(1.00),5dlu,p,5dlu","10dlu,p,1dlu,fill:0:grow(1.00),5dlu");
		setLayout(lay);
		CellConstraints cc = new CellConstraints();
		JLabel lab = new JLabel("Geburtstag:");
		add(lab,cc.xy(2, 2));
		suchdatum = new JFormattedTextField();
		add(suchdatum,cc.xy(4,2));
		JButton jbut = new JButton("Daten holen");
		jbut.setActionCommand("datenholen");
		jbut.addActionListener(this);
		add(jbut,cc.xy(6,2));
		
		direktPrint = new JCheckBox("nach Doppelkick sofort drucken");
		direktPrint.setOpaque(false);
		direktPrint.setSelected(false);
		add(direktPrint,cc.xy(8,2));
		
		anzahl = new JLabel("Adressen: 0");
		add(anzahl,cc.xy(2,4));
		JScrollPane jscr = new JScrollPane(getTabelle());
		jscr.validate();
		add(new JScrollPane(getTabelle()),cc.xyw(4,4,7));
		//System.out.println("Grafik = "+GBriefe.proghome+"icons/rta.gif");
		lab = new JLabel();
		//BufferedImage img = (BufferedImage)Toolkit.getDefaultToolkit().createImage(GBriefe.proghome+"icons/rta.gif");// Image(GBriefe.proghome+"icons/rta.gif");
		BufferedImage img = null;
		try {
			//img = (BufferedImage) new ImageIcon(ImageIO.read(new File(GBriefe.proghome+"icons/rta.gif"))).getImage();
			img = (BufferedImage) new ImageIcon(ImageIO.read(new File(GBriefe.proghome+"icons/handschlag.gif"))).getImage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// Image(GBriefe.proghome+"icons/rta.gif"))).getImage();
		lab.setIcon(new ImageIcon(img));
		//lab.setIcon(new ImageIcon(img.getScaledInstance(150, 100,1)));
		add(lab,cc.xy(12,4));
		
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		String comm = arg0.getActionCommand();
		if(comm.equals("datenholen")){
			//System.out.println("Geburtstage vom "+suchdatum.getText().trim());
			new GeburtstagHolen(suchdatum.getText().trim());
		}
	}
	public JXTable getTabelle(){
		dtblm = new MyGbTableModel();
		String[] column = 	{"Anrede","Vorn. Nachn.","Briefanrede","Strasse","Ort","Geboren","Alter","Pat-Nr.","Text","feddisch"};
		dtblm.setColumnIdentifiers(column);
		jtab = new JXTable(dtblm);
		jtab.setHighlighters(HighlighterFactory.createSimpleStriping(new Color(204,255,255)));
		jtab.setDoubleBuffered(true);
		//jtab.setEditable(false);
		jtab.setSortable(false);
		jtab.getColumn(0).setMaxWidth(50);
		jtab.getColumn(5).setMaxWidth(70);
		jtab.getColumn(6).setMaxWidth(30);
		jtab.getColumn(7).setMaxWidth(50);		
		jtab.getColumn(8).setMinWidth(0);
		jtab.getColumn(8).setMaxWidth(0);		
		jtab.getColumn(9).setMaxWidth(45);
		((TableColumnExt)jtab.getColumn(9)).setCellEditor(new JXTable.BooleanEditor());
		jtab.validate();
		jtab.setName("Geburtstage");
		jtab.addMouseListener(this);
		jtab.addKeyListener(this);

		
		return jtab;
	}

	public Vector holEinzeldaten(String patint){
		Vector vec = new Vector();
		Statement stmt = null;
		ResultSet rs = null;
		String sstmt = new String();

		sstmt = "select * from pat5 where PAT_INTERN = '"+patint+"'";
			
		try {
			stmt =  GBriefe.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			//{"Anrede","Nachname","Voname","Geboren","Pat-Nr."};
			rs = stmt.executeQuery(sstmt);
			GBriefe.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));

			

			while( rs.next()){
				ResultSetMetaData rm = rs.getMetaData();
				int end = rm.getColumnCount();
				for(int i = 1 ; i <= end;i++){
					vec.add(rs.getString(i));
					//System.out.println("Feld "+i+" => "+rs.getString(i));
				}	
					
			}
			//anzahlRezepte.setText("Anzahl aktueller Rezepte: "+anzahl);
			GBriefe.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			SteuerPanel.thisClass.jtab.validate();
			
			
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
		return (Vector)vec.clone();
	}
	class GeburtstagHolen{
		GeburtstagHolen(String geb){
		if(geb.length() < 6 || geb.length() > 6){
			JOptionPane.showMessageDialog(null,"Den Datumswert bitte in der Form '01.02.' eingeben.\n\n(insgesamt 6 Zeichen, inklusive der 2 Punkte)");
			return;
		}
		String aktuell = new String(geb+new Integer(SteuerPanel.thisClass.aktJahr).toString());
		if(datFunk.DatumsWert(aktuell) <= datFunk.DatumsWert(datFunk.sHeute())){
			SteuerPanel.thisClass.nachtraeglich = true;
		}else{
			SteuerPanel.thisClass.nachtraeglich = false;
		}
		Statement stmt = null;
		ResultSet rs = null;
		String sstmt = new String();
		String daten[] = testDatum(geb);
		sstmt = "select * from pat5 where geboren like '%"+(daten[1].length()<2 ? "0"+daten[1] : daten[1])+
		"-"+(daten[0].length()<2 ? "0"+daten[0] : daten[0])+"' order by geboren";
		//sstmt = "select * from pat5 where Convert(Month(GEBOREN),SQL_CHAR)='"+daten[1]+"' AND  Convert(DayOfMonth(GEBOREN),SQL_CHAR)= '"+daten[0]+"' ORDER BY GEBOREN";
			
		try {
			stmt =  GBriefe.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			//{"Anrede","Vorn. Nachn.","Briefanrede","Strasse","Ort","Geboren","Alter","Pat-Nr.","Text"};
			rs = stmt.executeQuery(sstmt);
			GBriefe.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			Vector xvec = new Vector();
			int anzahl = 0;
			int falsch = 0;
			SteuerPanel.thisClass.dtblm.setRowCount(0);
			String sex ="",nname="",vname="",strasse="",ort="",alter="";
			int aktJahr = 0;
			SteuerPanel.thisClass.noPrint.clear();
			while( rs.next()){
				
				//try{
				aktJahr =  new Integer(rs.getString("GEBOREN").substring(0,4).trim());
				//System.out.println("Aktjahr = "+aktJahr);
				sex = StringTools.EGross(rs.getString("ANREDE"));
				xvec.add(sex);
				nname = StringTools.EGross(rs.getString("N_NAME"));
				vname = StringTools.EGross(rs.getString("V_NAME"));
				xvec.add(vname+" "+nname);
				if(SteuerPanel.thisClass.aktJahr - aktJahr > 13){
					xvec.add((sex.equals("Frau") ? "Sehr geehrte Frau " : "Sehr geehrter Herr ")+nname);	
				}else{
					xvec.add((sex.equals("Frau") ? "Liebe " : "Lieber ")+vname);
				}
				
				strasse =  StringTools.EGross(rs.getString("STRASSE"));
				xvec.add(strasse);
				ort = StringTools.EGross(rs.getString("PLZ"))+" "+StringTools.EGross(rs.getString("Ort"));
				xvec.add(ort);
				xvec.add(datFunk.sDatInDeutsch(rs.getString("GEBOREN")));
				xvec.add(new Integer(SteuerPanel.thisClass.aktJahr - aktJahr).toString());			
				xvec.add(rs.getString("PAT_INTERN"));
				String anamnese = rs.getString("ANAMNESE"); 
				if(anamnese == null){
					anamnese = "";
				}
				xvec.add(anamnese);
				xvec.add(new Boolean(false));
				////System.out.println(xvec);
				if(checkPrint(anamnese.toUpperCase())){
					anzahl++;
					SteuerPanel.thisClass.dtblm.addRow((Vector)xvec.clone());
				}else{
					falsch++;
				}
				//AktuelleRezepte.aktRez.macheTabelle((Vector)xvec.clone());
				xvec.clear();
				/*
				}catch(){
					anzahl--;
					xvec.clear();
				}*/
			}
			if(falsch > 0){
				JOptionPane.showMessageDialog(null, "Es wurden insgesamt "+falsch+" Adressen gefiltert.\n\nGrund: Vermerk Adresse des Patienten ist falsch, oder Patient ist verstorben" );
			}
			//anzahlRezepte.setText("Anzahl aktueller Rezepte: "+anzahl);
			GBriefe.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			SteuerPanel.thisClass.jtab.validate();
			SteuerPanel.thisClass.insgesamt = anzahl;
			SteuerPanel.thisClass.anzahl.setText("Adressen: "+SteuerPanel.thisClass.insgesamt);
			
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
		//return datvec;
		}
		private boolean checkPrint(String anamnese){
			boolean print= true;
			if(anamnese.contains("KEINE GEBURTSTAGS")){return false;}
			if(anamnese.contains("KEIN GEBRURTS")){return false;}
			if(anamnese.contains("ADRESSE FALSCH")){return false;}
			if(anamnese.contains("KEIN GEBURTSTAGS")){return false;}
			if(anamnese.contains("GESTORBEN")){return false;}			
			if(anamnese.contains("VERSTORBEN")){return false;}
			if(anamnese.contains("ADRESSE")){return false;}			
			if(anamnese.contains("ADRESSSE")){return false;}
			return print;
		}

	}
	private String[] testDatum(String xtest){
		
		String[] vtest =  xtest.split("\\.");
		//System.out.println("Tag = "+vtest[0]);
		//System.out.println("Monat = "+vtest[1]);		
		
		if(vtest[0].substring(0,1).equals("0")){
			vtest[0] = new String(vtest[0].substring(1,2));
		}
		if(vtest[1].substring(0,1).equals("0")){
			vtest[1] = new String(vtest[1].substring(1,2));
		}
		//System.out.println("Tag = "+vtest[0]);
		//System.out.println("Monat = "+vtest[1]);		
		return vtest.clone();
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getClickCount()==2){
			if(((JComponent)arg0.getSource()).getName().equals("Geburtstage")){
				starteDruck();
				/*
				int row = jtab.getSelectedRow();
				anzahl.setText("Adr. "+(row+1)+" von "+insgesamt);
				aktPat = (String) jtab.getValueAt(jtab.getSelectedRow(), 7);
				int alter = new Integer( (String) jtab.getValueAt(jtab.getSelectedRow(), 6) );
				String datei = "";
				String doku1 = GBriefe.thisClass.vorlagenvz+"GBE";
				String sex = ((String) jtab.getValueAt(jtab.getSelectedRow(), 0)).substring(0,1);
				if(sex.trim().equals("")){
					JOptionPane.showMessageDialog(null,"Keine Anrede im Patientenstamm verhanden.\nBrief wird nicht gedruckt!!!");
					return;
				}
				if(nachtraeglich){
					if(alter > 13){
						datei = doku1+sex+"N.ott";
					}else{
						datei = doku1+"KN.ott";
					}
					
				}else{
					if(alter > 13){
						datei = doku1+sex+"A.ott";
					}else{
						datei = doku1+"KA.ott";						
					}

				}
				//System.out.println("Alter = "+alter+" / Datei die geladen wird "+datei);
				final String xdatei = datei;
				new SwingWorker<Void,Void>(){

					@Override
					protected Void doInBackground() throws Exception {
						// TODO Auto-generated method stub
						//System.out.println(dtblm.getDataVector().get(jtab.getSelectedRow()));
						OOoPanel.thisClass.dokumentLaden(xdatei,(Vector)dtblm.getDataVector().get(jtab.getSelectedRow()),direktPrint.isSelected());
						return null;
					}
					
				}.execute();
				*/
			}
		}
	}
	public void starteDruck(){
		int row = jtab.getSelectedRow();
		anzahl.setText("Adr. "+(row+1)+" von "+insgesamt);
		aktPat = (String) jtab.getValueAt(jtab.getSelectedRow(), 7);
		int alter = new Integer( (String) jtab.getValueAt(jtab.getSelectedRow(), 6) );
		//gebTag.clear();
		//gebTag = holEinzeldaten(aktPat);
		String datei = "";
		String doku1 = GBriefe.thisClass.vorlagenvz+"GBE";
		String sex = ((String) jtab.getValueAt(jtab.getSelectedRow(), 0)).substring(0,1);
		if(sex.trim().equals("")){
			JOptionPane.showMessageDialog(null,"Keine Anrede im Patientenstamm verhanden.\nBrief wird nicht gedruckt!!!");
			return;
		}
		if(nachtraeglich){
			if(alter > 13){
				datei = doku1+sex+"N.ott";
			}else{
				datei = doku1+"KN.ott";
			}
			
		}else{
			if(alter > 13){
				datei = doku1+sex+"A.ott";
			}else{
				datei = doku1+"KA.ott";						
			}

		}
		//System.out.println("Alter = "+alter+" / Datei die geladen wird "+datei);
		final String xdatei = datei;
		new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				// TODO Auto-generated method stub
				//System.out.println(dtblm.getDataVector().get(jtab.getSelectedRow()));
				OOoPanel.thisClass.dokumentLaden(xdatei,(Vector)dtblm.getDataVector().get(jtab.getSelectedRow()),direktPrint.isSelected());
				return null;
			}
			
		}.execute();
		
	}
	public void setzteFertig(){
		int row = jtab.getSelectedRow();
		jtab.setValueAt(new Boolean(true),row, 9 );
		jtab.validate();
		//System.out.println("Es wurde gedruckt");
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getKeyCode() == 10){
			if(((JComponent)arg0.getSource()).getName().equals("Geburtstage")){
				arg0.consume();
				starteDruck();
			}	
		}
		
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
}

class MyGbTableModel extends DefaultTableModel{
	   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Class getColumnClass(int columnIndex) {
		   if(columnIndex==0){
			   return String.class;
		   }else if (columnIndex==9){
			   return Boolean.class;
		   }else{
			   return String.class;
		   }
        //return (columnIndex == 0) ? Boolean.class : String.class;
    }

	    public boolean isCellEditable(int row, int col) {
	        //Note that the data/cell address is constant,
	        //no matter where the cell appears onscreen.
	        if (col == 9){
	        	return true;
	        }else{
	          return false;
	        }
	      }
	   
}


