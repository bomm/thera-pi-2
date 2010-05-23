package systemEinstellungen;

import hauptFenster.AktiveFenster;
import hauptFenster.Reha;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.LinearGradientPaint;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import org.jdesktop.swingworker.SwingWorker;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

//import roogle.SuchenSeite;
import sqlTools.SqlInfo;
import systemTools.JRtaTextField;
import terminKalender.ParameterLaden;
//import terminKalender.TerminFenster;
import terminKalender.DatFunk;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SysUtilKalenderanlegen extends JXPanel implements KeyListener, ActionListener {

	
	JButton knopf1 = null;
	JButton knopf2 = null;
	JButton knopf3 = null;
	JButton knopf4 = null;
	JButton knopf5 = null;
	static JXLabel KalMake = null;	
	static JXLabel FeierTag = null;
	JCheckBox AZPlan = null;
	
	JScrollPane listscr = null;
	JLabel Datum = null;
	static JProgressBar Fortschritt = null;
	static JProgressBar Fortschritt2 = null;
	static boolean dblaeuft = false;
	static int progress = 0;
	
	JComboBox Netz = null;	
	JComboBox BuLand = null;
	static JComboBox FJahr = null;
	
	static int speed = 100;
	
	JXTable FreiTage = null;
	
	static JXLabel KalBis = null;

	FeiertagTableModel ftm = null;
	
	static String bisDatum = null;
	static int kalTage;
	static Vector<Object> vecMasken = new Vector<Object>();
	Vector vecLeer = new Vector();
 
	//static Vector<Object> aMaskenDaten = new Vector<Object>();
	
	String[] laender = {"BW","BY","BE","BB","HB","HH","HE","MV","NI","NW","RP","SL","SN","ST","SH","TH"};
	String[] netz = {"DSL","LAN"};

	
	SysUtilKalenderanlegen(){
		super(new GridLayout(1,1));
		////System.out.println("Aufruf SysUtilKalenderanlagen");
		this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 0));
		/****/
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("SystemInit"));
		/****/
	     JScrollPane jscr = new JScrollPane();
	     jscr.setBorder(null);
	     jscr.setOpaque(false);
	     jscr.getViewport().setOpaque(false);
	     jscr.setViewportView(getAnlegenSeite());
	     jscr.getVerticalScrollBar().setUnitIncrement(15);
	     jscr.validate();
	     
	     add(jscr);
	     HoleMaxDatum hmd = new HoleMaxDatum();
	     hmd.setzeStatement("select max(DATUM) from flexkc");
		return;
	}
	
	/************** Beginn der Methode f�r die Objekterstellung und -platzierung *********/
	private JPanel getAnlegenSeite(){
       
		knopf1 = new JButton("los"); // neues Jahr in Datenbank anlegen
		knopf1.setPreferredSize(new Dimension(70, 20));
		knopf1.addActionListener(this);
		knopf1.setActionCommand("NeuJahr");
		knopf1.addKeyListener(this);

		knopf2 = new JButton("los");  // Feiertage importieren
		knopf2.setPreferredSize(new Dimension(70, 20));
		knopf2.addActionListener(this);
		knopf2.setActionCommand("FTimport");
		knopf2.addKeyListener(this);
		
		knopf3 = new JButton("hinzufügen"); // Feiertage oder Betr.-Ferien in Tabelle zuf�gen
		knopf3.setPreferredSize(new Dimension(70, 20));
		knopf3.addActionListener(this);
		knopf3.setActionCommand("add");
		knopf3.addKeyListener(this);
		
		knopf4 = new JButton("entfernen"); // Feiertage oder Betr.-Ferien in Tabelle entfernen
		knopf4.setPreferredSize(new Dimension(70, 20));
		knopf4.addActionListener(this);		
		knopf4.setActionCommand("delete");
		knopf4.addKeyListener(this);		
		
		knopf5 = new JButton("FERTIG"); // Feiertage oder Betr.-Ferien in Datenbank schreiben
		knopf5.setPreferredSize(new Dimension(70, 20));
		knopf5.addActionListener(this);		
		knopf5.setActionCommand("take");
		knopf5.addKeyListener(this);
		
 
		FreiTage = new JXTable();
		
		KalBis = new JXLabel("");
		KalMake = new JXLabel("");
		AZPlan = new JCheckBox("");
		
		//                                1.       2.     3.    4   5.
		FormLayout lay = new FormLayout("8dlu, p:g, 130dlu, 40dlu,20dlu",
       //1.    2.  3.  4.    5.  6.   7.   8.  9.  10.  11. 12.  13. 14.   15.  16.  17. 18   19  20   21   22   23   24   25  26   27   28   29  30    31  32   33  34    35   36    37  38   39     40    41   42   43  44   45  46   47   48  49   50  51  52   53  54   55  56   57  58    59   60   61    62
		"p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 10dlu, p, 15dlu, p, 10dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, 80dlu, 2dlu, p, 10dlu, p, 10dlu, p,2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 10dlu, p, 10dlu");
		
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator("0. Voreinstellung", cc.xyw(1, 1, 4));
		Netz = new JComboBox(netz);
		Netz.setActionCommand("netz");
		Netz.addActionListener(this);
		builder.addLabel("Art der Netzwerkverbindung", cc.xyw(2, 3,2));
		builder.add(Netz, cc.xy(4, 3));
		builder.addLabel("Sollte Ihr Rechner mit der Datenbank in einem lokalen", cc.xyw(2, 5, 2));
		builder.addLabel("Netzwerk verbunden sein, können Sie die Einstellung LAN", cc.xyw(2, 7, 2));
		builder.addLabel("wählen.", cc.xy(2,9));
		builder.addLabel("Bei jeder anderen Art der Verbindung sind Sie mit DSL", cc.xyw(2,11,2));
		builder.addLabel("auf der SICHEREN Seite!", cc.xyw(2,13,2));
		
		builder.addSeparator("1. Kalenderdatenbank anlegen", cc.xyw(1, 15, 4));	
		builder.addLabel("Daten sind aktuell vorhanden bis zum Jahr ", cc.xyw(2, 17, 2));
		KalBis.setForeground(Color.RED);
		KalBis.setFont(new Font("Arial",Font.BOLD,11));		
		KalBis.setText("");
		builder.add(KalBis, cc.xy(4,17));
		
		builder.addLabel("Arbeitszeitpläne berücksichtigen? (empfohlen)", cc.xyw(2,19,2));
		AZPlan.setSelected(true);
		builder.add(AZPlan, cc.xy(4,19));
		
		builder.addLabel("Datenbank wird angelegt für das Jahr ", cc.xyw(2, 21,2));
		KalMake.setText("");
		KalMake.setFont(new Font("Arial",Font.BOLD,11));
		KalMake.setForeground(Color.RED);		
		builder.add(KalMake, cc.xy(4,21));
		
		builder.addLabel("Datenbank anlegen", cc.xyw(2,23,2));
		builder.add(knopf1, cc.xy(4,23));
		
		builder.addSeparator("Prozessfortschritt / Datenbank", cc.xyw(2,25,3));
	
		Fortschritt = new JProgressBar ();
		builder.add(Fortschritt, cc.xyw(2, 27, 3));
		
		
		builder.addSeparator("2. Feiertage importieren", cc.xyw(1, 29, 4));
		
		builder.addLabel("Bundesland auswählen", cc.xyw(2,31,2));
		String[] bula = {"Baden-Württemberg","Bayern","Berlin","Brandenburg","Bremen",
						"Hamburg","Hessen","Meckl.-Vorpommern","Niedersachsen","Rheinland-Pfalz",
						"Saarlan","Sachsen","Sachs.-Anhalt","Schleswig-Holstein","Thüringen"};
		//BuLand = new JComboBox(bula);
		BuLand = new JComboBox(laender);
		BuLand.setSelectedIndex(0);
		builder.add(BuLand, cc.xy(4,31));
		
		builder.addLabel("Kalenderjahr auswählen", cc.xyw(2, 33,2));
		String[] jahr = {"2008","2009","2010","2011"};
		FJahr = new JComboBox(jahr);
		FJahr.setSelectedIndex(0);
		FJahr.setActionCommand("jahr");
		FJahr.addActionListener(this);
		builder.add(FJahr, cc.xy(4, 33));
		
		builder.addLabel("Feiertage einlesen", cc.xyw(2,35,2));
		builder.add(knopf2, cc.xy(4,35));
		
		
		builder.addSeparator("3. Feiertagsliste / Betriebsferien bearbeiten", cc.xyw(1, 37, 4));
		ftm = new FeiertagTableModel();
		String[] dat = {"Datum","Feiertag/Ferien","Bundesland"};
		ftm.setColumnIdentifiers(getColVector(dat));
		//klm.setDataVector((Vector) vkollegen.clone(), getColVector(kcolumn));
		//ftm.setDataVector((Vector)vec.clone(),getColVector(dat));
		//ftm.setDataVector((Vector)vec.clone(),getColVector(dat));
		FreiTage.setModel(ftm);
		FreiTage.validate();
		listscr = new JScrollPane(FreiTage);
		//builder.add(FreiTage, cc.xyw(2, 39,3));
		builder.add(listscr, cc.xyw(2, 39,3));
		
		builder.add(knopf3, cc.xy(2, 41));
		builder.add(knopf4, cc.xy(4,41));
		
		
		builder.addSeparator("4. Daten in Kalender übernehmen", cc.xyw(1, 43, 4));
		
		builder.addLabel("Die Daten aus der Liste werden in den Kalender eingetragen.", cc.xyw(2, 45, 2));
		//builder.addLabel("", cc.xyw(2, 47, 2));
		builder.addLabel("Änderungen können danach nur noch manuell im Terminplan ", cc.xyw(2, 51, 2));
		builder.addLabel("oder über den Arbeitszeitplan vorgenommen werden.", cc.xyw(2, 53, 2));
		//builder.addLabel("", cc.xyw(2, 55, 2));
		
		
		FeierTag = new JXLabel("Feiertagsliste für das Jahr 9999 eintragen.");
		builder.add(FeierTag, cc.xyw(2,57,2));
		builder.add(knopf5, cc.xy(4, 57));
		
		builder.addSeparator("Prozessfortschritt / Feiertage eintragen", cc.xyw(2,59,3));
		
		Fortschritt2 = new JProgressBar ();
		builder.add(Fortschritt2, cc.xyw(2, 61, 3));
		
		return builder.getPanel();
	}
	private Vector getColVector(String[] cols){
		Vector col = new Vector();
		for(int i = 0;i<cols.length;i++){
			col.add(cols[i]);				
		}
		return col;
	}
	

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		int i;
		for(i=0;i<1;i++){
			if(arg0.getActionCommand().equals("FTimport")){
			     SwingUtilities.invokeLater(new Runnable(){
				 	   public  void run(){
				 		   try {
								starteSession((String)BuLand.getSelectedItem(),(String)FJahr.getSelectedItem());
								FeierTag.setText("Feiertagsliste für das Jahr -> "+FJahr.getSelectedItem()+" <- eintragen.");
				 		   } catch (IOException e) {
								// TODO Auto-generated catch block
				 			   ftm.getDataVector().clear();
				 			   FreiTage.validate();
				 			   JOptionPane.showMessageDialog(null, "Verbindung zu www.feiertage.net konnte nicht aufgebaut werden\n"+
																	"Ist Ihre Internetverbindung o.k.???");
				 			   
				 		   }
				 	   }
					});
					break;
			}


			if(arg0.getActionCommand().equals("NeuJahr")){
				JComponent termin = AktiveFenster.getFensterAlle("TerminFenster");
				if(termin != null){
					JOptionPane.showMessageDialog (null, "Achtung!!!!! \n\nWährend der Anlage eines Kalenderjahres\n" +
					"darf der Terminkalender aus Sicherheitsgründen nicht geöffnet sein.\n"+
					"Beenden Sie den Terminkalender und rufen Sie diese Funktion erneut auf.\n\n");
					break;
				}
				String frage = "Bitte beachten Sie!\n\n"+
								"1. Stellen Sie sicher, daß Sie zum Zeitpunkt der Kalenderanlage möglichst er einzige Benutzer im Netzwerk sind\n"+
								"2. Wurde die Kalenderanlage gestartet, brechen Sie den Vorgang bitte keinesfalls ab\n"+
								"3. Die Kalenderanlage kann einige Zeit in Anspruch nehmen. Sie sehen den Fortschritt anhand des 'Laufbalkens'\n\n"+
								"Wollen Sie jetzt das Kalenderjahr wie folgt anlegen:\n"+
								"angelegt wird das Jahr -> "+KalMake.getText()+" <- \n"+
								"automatische Übernahme der Wochenarbeitszeit -> "+(AZPlan.isSelected() ? "JA" : "NEIN")+" <-" ;
				int anfrage = JOptionPane.showConfirmDialog(null, frage, "Achtung wichtige Benutzeranfrage!!!!", JOptionPane.YES_NO_OPTION);
				if(anfrage == JOptionPane.YES_OPTION){
					SwingUtilities.invokeLater(new Runnable(){
						public  void run(){
							new Thread(){
								public void run(){
									knopfGedoense(false);									
								}
							}.start();
							
							starteKraftakt();
						}
					});
					break;
				}else{
					break;
				}
			}
			if(arg0.getActionCommand().equals("add")){
				String[] inhalt = {"","",""};
				ftm.addRow(new Vector(Arrays.asList(inhalt)));
				int aktrow = FreiTage.getRowCount()-1;
				FreiTage.setRowSelectionInterval(aktrow, aktrow);
				FreiTage.validate();
				Rectangle re = FreiTage.getCellRect(aktrow+1, 1, false);
				JViewport vp = listscr.getViewport();
				vp.setView(FreiTage);
				vp.setViewPosition(re.getLocation());
				//FreiTage.changeSelection(FreiTage.getRowCount()-1, 0, false, false);
				
				//FreiTage.setEditingRow(FreiTage.getRowCount()-1);
				break;
			}
			if(arg0.getActionCommand().equals("delete")){
 
				while(FreiTage.getSelectedRows().length > 0){;
					int[] select = FreiTage.getSelectedRows();
					//ftm.removeRow(FreiTage.convertRowIndexToModel(select[0]));
					ftm.removeRow(select[0]);
				}
				break;
			}
			if(arg0.getActionCommand().equals("take")){
				SwingUtilities.invokeLater(new Runnable(){
					public  void run(){
						knopfGedoense(false);							
						starteFTEintragen();
					}
				});
				break;
			}
			if(arg0.getActionCommand().equals("jahr")){
				SwingUtilities.invokeLater(new Runnable(){
					public  void run(){
						if(FJahr != null){
							FeierTag.setText("Feiertagsliste für das Jahr -> "+FJahr.getSelectedItem()+" <- eintragen.");
						}	
					}
				});
				break;
			}
			if(arg0.getActionCommand().equals("netz")){
				if(arg0.getSource() instanceof JComboBox){
					if ( ((JComboBox)arg0.getSource()).getSelectedItem().equals("DSL") ){
						speed = 100;
					}else{
						speed = 40;
					}
					////System.out.println("Dauer der Pause = "+speed+" Millisekunden");
				}
			}

		}
	}
	private void starteFTEintragen(){
		int max = FreiTage.getRowCount();
		int i;
		long zeit1 = System.currentTimeMillis();
		String fdatum=null, ftext=null,sret =null,sqldat=null ;
		//StringBuffer sbuf = new StringBuffer();
		if(max==0){
			knopfGedoense(true);
			return;
		}

		ftext = (String) FreiTage.getValueAt(0,1);
		if(ftext.trim().equals("")){
			JOptionPane.showMessageDialog(null,"Bitte geben Sie eine Bezeichnung für den Feiertag/Betriebsurlaub an");
			return;
		}

		Fortschritt2.setMinimum(0);
		Fortschritt2.setMaximum(max-1);
		Fortschritt2.setStringPainted(true);
		dblaeuft = true;
		progress = 0;
		ProgressVerarbeiten pv = new ProgressVerarbeiten(Fortschritt2);
		pv.execute();
		String tstart = SystemConfig.KalenderUmfang[0];
		String tend = SystemConfig.KalenderUmfang[1];
		String tdauer = new Long(SystemConfig.KalenderMilli[1]-SystemConfig.KalenderMilli[0]).toString();

		for(i=0;i<max;i++){
			progress = i;
			fdatum = (String) FreiTage.getValueAt(i,0);
			ftext = (String) FreiTage.getValueAt(i,1);

			if(! fdatum.trim().equals("")){
				sqldat = DatFunk.sDatInSQL(fdatum);
				sret = "Update flexkc set ";
				sret = sret + "T1='"+ftext.trim().toUpperCase()+"', N1='@FREI', TS1='"+tstart+"', TD1='"+tdauer+"', TE1='"+tend+"',";
				sret = sret + "BELEGT='1' Where DATUM='"+sqldat+"'";
				////System.out.println(sret);
				SchreibeNeuenKalender snk = new SchreibeNeuenKalender();
				snk.setzeStatement(new String(sret));
				try {
					Thread.sleep(speed);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		JOptionPane.showMessageDialog(null, "Feiertagübernahme beendet nach "+((System.currentTimeMillis()-zeit1))+" Millisekunden");
		dblaeuft = false;
		knopfGedoense(true);
	}
	
	private void starteKraftakt(){

		int i;
		if(AZPlan.isSelected()){
			Fortschritt.setMinimum(1);
			Fortschritt.setMaximum(60);
			dblaeuft = true;
			progress = 1;
			ProgressVerarbeiten pv = new ProgressVerarbeiten(Fortschritt);
			pv.execute();
			for(i = 1;i<61;i++){
				
				String sbehandler = (i<10 ? "0"+new Integer(i).toString()+"BEHANDLER" : new Integer(i).toString()+"BEHANDLER");
				String stmtmaske = "select * from masken where behandler = '"+sbehandler+"' ORDER BY art";
				new HoleMasken(stmtmaske);
				try {
					Thread.sleep(15);
					progress = i;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			dblaeuft = false;
		}
		new Thread(){
			public void run(){
				// ab hier geht's dann zur Sache
				starteDbAppend();
			}
		}.start();
		

		//xxxx	
	}
	private void knopfGedoense(boolean einschalten){
		if(einschalten){
			knopf1.setEnabled(false);
		}else{
			knopf1.setEnabled(false);			
		}
		knopf2.setEnabled(einschalten);
		knopf3.setEnabled(einschalten);
		knopf4.setEnabled(einschalten);
		knopf5.setEnabled(einschalten);
		AZPlan.setEnabled(einschalten);
		BuLand.setEnabled(einschalten);
		FJahr.setEnabled(einschalten);		

		
	}
	private void starteDbAppend(){
//		new Thread(){
//			public void run(){
		int durchgang = 0;		
		long zeit1=System.currentTimeMillis();
		dblaeuft = true;
		if(KalMake.getText().equals("")){
			dblaeuft = false;
			return;
		}
		kalTage = (DatFunk.Schaltjahr( new Integer(KalMake.getText())) ? (366) : (365));
		Fortschritt.setMinimum(1);
		Fortschritt.setMaximum(kalTage*60  );
		Fortschritt.setStringPainted(true);

		String starttag = "01.01."+KalMake.getText();
		String stoptag 	= "31.12."+KalMake.getText();
		//String stoptag 	= "02.01."+KalMake.getText();
		String akttag =  new String(starttag);
		progress = 0;
		int i;
		ProgressVerarbeiten pv = new ProgressVerarbeiten(Fortschritt);
		pv.execute();
		String stmt = null;
		while(DatFunk.DatumsWert(akttag) <= DatFunk.DatumsWert(stoptag)){
			for(i=1;i<61;i++){
				durchgang ++;
				if(durchgang > 200){
					durchgang = 0;
					Runtime r = Runtime.getRuntime();
				    r.gc();
				}

				stmt = macheStatement(DatFunk.sDatInSQL(akttag),
								(ArrayList)((Vector)vecMasken.get(i-1)).get(DatFunk.TagDerWoche(akttag)-1),
								(i<10 ? "0"+new Integer(i).toString()+"BEHANDLER" :new Integer(i).toString()+"BEHANDLER"),
								AZPlan.isSelected());
				////System.out.println(stmt);
				SqlInfo.sqlAusfuehren(stmt);
				//SchreibeNeuenKalender snk = new SchreibeNeuenKalender();
				//snk.setzeStatement(new String(stmt));
				try {
					Thread.sleep(speed);
					//Thread.sleep(15);
					++progress;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			akttag = DatFunk.sDatPlusTage(akttag,1);
		}
		dblaeuft = false;
		JOptionPane.showMessageDialog(null, "Kalenderanlegen beendet nach "+((System.currentTimeMillis()-zeit1)/1000)+" Sekunden\n\n"+"Kalender wird jetzt auf Integrität geprüft!");
		
		knopfGedoense(true);
		Vector vec = SqlInfo.holeFelder("select min(datum),max(datum) from flexkc");
		Reha.kalMin = DatFunk.sDatInDeutsch( ((String)((Vector)vec.get(0)).get(0)) );
		Reha.kalMax = DatFunk.sDatInDeutsch( ((String)((Vector)vec.get(0)).get(1)) );
		//System.out.println("Kalenderspanne = von "+Reha.kalMin+" bis "+Reha.kalMax);		
//			}
//		}.start();

		
	}
	private String macheStatement(String sqldat,ArrayList list,String sBehandler,boolean mitmaske){
		String sret = null;
		int i,j;
		int bloecke =new Integer( (String)  ((Vector) list.get(5)).get(0) );
		String nummer;
		if(mitmaske){
			sret = "Insert into flexkc set ";
			for (i = 0; i<bloecke;i++){
				  if(((String)((Vector) list.get(1)).get(i)).contains("\\")){
					  String replace =  ((String)((Vector) list.get(1)).get(i));
					  String [] split = {null,null};
					  split = replace.split("\\\\");
					  nummer =  split[0]+"\\\\"+split[1];
					  ////System.out.println("Backslashtermin = "+nummer);
				  	
				  }else{
					  nummer = ((String)((Vector) list.get(1)).get(i));
				  }

				sret = sret + "T"+ (i+1) + "='" + ((Vector) list.get(0)).get(i) + "', " ;
				sret = sret + "N"+ (i+1) + "='" + nummer + "', "; 
				sret = sret + "TS"+ (i+1) + "='" + ((Vector) list.get(2)).get(i) + "', ";			
				sret = sret + "TD"+ (i+1) + "='" + ((Vector) list.get(3)).get(i) + "', ";			
				sret = sret + "TE"+ (i+1) + "='" + ((Vector) list.get(4)).get(i) + "', ";
			}
			sret = sret + "BELEGT='"+new Integer(bloecke).toString()+"', DATUM='"+sqldat+"' , BEHANDLER='"+sBehandler+"'";
		}else{
			//
			String tstart = SystemConfig.KalenderUmfang[0];
			String tend = SystemConfig.KalenderUmfang[1];
			String tdauer = new Long(SystemConfig.KalenderMilli[1]-SystemConfig.KalenderMilli[0]).toString();
			sret = "Insert into flexkc set ";
			sret = sret + "T1='', N1='@FREI', TS1='"+tstart+"', TD1='"+tdauer+"', TE1='"+tend+"',";
			sret = sret + "BELEGT='1', DATUM='"+sqldat+"' , BEHANDLER='"+sBehandler+"'";
		}
		return sret;
	}

	private String macheStatement2(String sqldat,ArrayList list,String sBehandler,boolean mitmaske){
		String sret = "";
		int i,j;
		int bloecke =new Integer( (String)  ((Vector) list.get(5)).get(0) );
		String nummer;
		if(mitmaske){
			//sret = "Insert into flexkc set ";
			for (i = 0; i<bloecke;i++){
				  if(((String)((Vector) list.get(1)).get(i)).contains("\\")){
					  String replace =  ((String)((Vector) list.get(1)).get(i));
					  String [] split = {null,null};
					  split = replace.split("\\\\");
					  nummer =  split[0]+"\\\\"+split[1];
					  ////System.out.println("Backslashtermin = "+nummer);
				  	
				  }else{
					  nummer = ((String)((Vector) list.get(1)).get(i));
				  }

				sret = sret + "T"+ (i+1) + "='" + ((Vector) list.get(0)).get(i) + "', " ;
				sret = sret + "N"+ (i+1) + "='" + nummer + "', "; 
				sret = sret + "TS"+ (i+1) + "='" + ((Vector) list.get(2)).get(i) + "', ";			
				sret = sret + "TD"+ (i+1) + "='" + ((Vector) list.get(3)).get(i) + "', ";			
				sret = sret + "TE"+ (i+1) + "='" + ((Vector) list.get(4)).get(i) + "', ";
			}
			sret = sret + "BELEGT='"+new Integer(bloecke).toString()+"', DATUM='"+sqldat+"' , BEHANDLER='"+sBehandler+"'";
		}else{
			//
			String tstart = SystemConfig.KalenderUmfang[0];
			String tend = SystemConfig.KalenderUmfang[1];
			String tdauer = new Long(SystemConfig.KalenderMilli[1]-SystemConfig.KalenderMilli[0]).toString();
			//sret = "Insert into flexkc set ";
			sret = sret + "T1='', N1='@FREI', TS1='"+tstart+"', TD1='"+tdauer+"', TE1='"+tend+"',";
			sret = sret + "BELEGT='1', DATUM='"+sqldat+"' , BEHANDLER='"+sBehandler+"'";
		}
		return sret;
	}

	private void starteSession(String land,String jahr) throws IOException{
		String urltext = "http://www.feiertage.net/csvfile.php?state="+land+"&year="+jahr+"&type=csv";
		String text = null;
		ftm.getDataVector().clear();
		FreiTage.validate();
		URL url = new URL(urltext);
		   
		      URLConnection conn = url.openConnection();
		      ////System.out.println(conn.getContentEncoding());
		      

		      BufferedReader inS = new BufferedReader( new InputStreamReader( conn.getInputStream() ));
		      int durchlauf = 0;
		      while ( (text  = inS.readLine())!= null ) {
		    	  String s = makeUTF8(text);
		          String [] spl = s.split(";");
		          if(durchlauf > 0){
		        	  Vector reihe = new Vector(Arrays.asList(spl));
		        	  ftm.addRow((Vector)reihe.clone());
		        	  FreiTage.setRowSelectionInterval(0, 0);
		          }
		          ++durchlauf;
		      }
		inS.close();
		
	}
	public static String makeUTF8(final String toConvert){
		try {
			return new String(toConvert.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	// und dann zum testen vielleicht

	public static void setJahr(String item){
		FJahr.setSelectedItem(item);
		String ftext = "Feiertagsliste für das Jahr "+item+" eintragen";
		FeierTag.setText(ftext);
	}

}

/***********************************/
class FeiertagTableModel extends DefaultTableModel{
	   public Class getColumnClass(int columnIndex) {
		   return String.class;
       }

	    public boolean isCellEditable(int row, int col) {
	        //Note that the data/cell address is constant,
	        //no matter where the cell appears onscreen.
	          return true;
	      }

}
/***********************************/
class HoleMaxDatum extends Thread implements Runnable{
	Statement stmt = null;
	ResultSet rs = null;
	String statement;
	boolean geklappt = false;
 
	public void setzeStatement(String statement){
		this.statement = statement;
		start();
	}
	public void run(){

		try {
			stmt = (Statement) Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE );
			try{
					rs =  stmt.executeQuery(this.statement);
					if(rs.next()){
						if(rs.getString(1) != null){
						String datum = rs.getString(1);
						int altjahr = new Integer(datum.substring(0,4));
						SysUtilKalenderanlegen.KalBis.setText(new Integer(altjahr).toString());
						SysUtilKalenderanlegen.KalMake.setText(new Integer(altjahr+1).toString());
						//SysUtilKalenderanlegen.setJahr(new Integer(altjahr+1).toString());
						SysUtilKalenderanlegen.setJahr(new Integer(altjahr+1).toString());

						}else{
							String datum = DatFunk.sHeute().substring(6);
							SysUtilKalenderanlegen.KalBis.setText("leer");
							SysUtilKalenderanlegen.KalMake.setText(datum);
							SysUtilKalenderanlegen.setJahr(datum);							
						}
					}
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
	}
	
}

/******************************************************************************/
/******************************/
class HoleMasken{
	HoleMasken(String sstmt){

	// TODO Auto-generated method stub
	Statement stmt = null;
	ResultSet rs = null;
	
	try {
		stmt = Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE );
		try{
		rs = stmt.executeQuery(sstmt);				
		int i = 0;
		int durchlauf = 0;
		int maxbehandler = 7;

		int maxblock = 0;
		int aktbehandler = 1;			
		ArrayList <Object>aKalList = new ArrayList<Object>();
		Vector<Object> aMaskenDaten = new Vector<Object>();
		//SysUtilKalenderanlegen.aMaskenDaten.clear();
		//*******************SysUtilKalenderanlegen.aMaskenDaten.clear();
		while( (rs.next()) ){
			Vector<String> v1 = new Vector<String>();
			Vector<String> v2 = new Vector<String>();
			Vector<String> v3 = new Vector<String>();
			Vector<String> v4 = new Vector<String>();
			Vector<String> v5 = new Vector<String>();	
			Vector<String> v6 = new Vector<String>();				
			
			/*in Spalte 301 steht die Anzahl der belegten Bl�cke*/ 
			int belegt = rs.getInt(226);
			/*letzte zu durchsuchende Spalte festlegen*/
			int ende = (5*belegt);
			maxblock = maxblock + (ende+5);
			durchlauf = 1;
			/* abgeschaltet f�r Performance-Check
			if (aktbehandler == 1){
				Titel.setText(rs.getString(305));	
			}
			*/

			if (!SystemConfig.vDatenBank.get(0).get(2).equals("ADS")){
				for(i=1;i<ende;i=i+5){
					v1.addElement(rs.getString(i)!= null ? rs.getString(i) : "");
					v2.addElement(rs.getString(i+1)!= null ? rs.getString(i+1) : "");
					v3.addElement(rs.getString(i+2));
					v4.addElement(rs.getString(i+3));
					v5.addElement(rs.getString(i+4));					
					durchlauf = durchlauf+1;
				}
			}else{ // ADS
				for(i=1;i<ende;i=i+5){
					v1.addElement(rs.getString(i)!= null ? rs.getString(i) : "");
					v2.addElement(rs.getString(i+1)!= null ? rs.getString(i+1) : "");
					v3.addElement(rs.getString(i+2));
					v4.addElement(rs.getString(i+3));
					v5.addElement(rs.getString(i+4));					
					durchlauf = durchlauf+1;
				}
			}

			v6.addElement(rs.getString(226));	//Anzahl
			v6.addElement(rs.getString(227));	//Art			
			v6.addElement(rs.getString(228));	//Behandler
			v6.addElement(rs.getString(229));	//MEMO
			v6.addElement(rs.getString(230));	//Datum			

			aKalList.add(v1.clone());
			aKalList.add(v2.clone());			
			aKalList.add(v3.clone());
			aKalList.add(v4.clone());
			aKalList.add(v5.clone());
			aKalList.add(v6.clone());	
			aMaskenDaten.add(aKalList.clone());	
			aKalList.clear();
			aktbehandler++;
		}
		SysUtilKalenderanlegen.vecMasken.add(aMaskenDaten.clone());
		//aSpaltenDaten.add(aKalList.clone());
	
		if(maxblock > 0){
		//datenZeichnen(aSpaltenDaten);
		//TerminFenster.rechneMaske();
		////System.out.println("Anzahl Tage = "+SysUtilKalenderanlegen.aMaskenDaten.size());
		////System.out.println("Inhalt = "+SysUtilKalenderanlegen.aMaskenDaten);
		}
		} catch(SQLException ex){
			//System.out.println("von ResultSet SQLState: " + ex.getSQLState());
			//System.out.println("von ResultSet ErrorCode: " + ex.getErrorCode ());
			//System.out.println("ErrorCode: " + ex.getErrorCode ());
			//System.out.println("von ResultSet ErrorMessage: " + ex.getMessage ());
		}

} catch(SQLException ex) {
	//System.out.println("von stmt -SQLState: " + ex.getSQLState());
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
	}
}
	

/******************************/	
class TesteKalender{
	TesteKalender(String sstmt){

	// TODO Auto-generated method stub
	Statement stmt = null;
	ResultSet rs = null;
	int tage = 0;
	
	try {
		stmt = Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE );
		try{
			rs = stmt.executeQuery(sstmt);				
			if(rs.next()){
				tage = rs.getInt(1);
				if( tage != (SysUtilKalenderanlegen.kalTage*60) ){
					JOptionPane.showMessageDialog(null,"Fehler!!!!! ---- Der Kalender wurde unvollständig angelegt!\n\n"+
										"Zur Sicherheit wird der fehlerhafte Kalender wieder gelöscht\n\n"+
										"Stellen Sie bei einem neuen Versuch die Einstellung\n"+
										"'Art der Netzwerkverbindung' auf -> DSL");
				}else{
					JOptionPane.showMessageDialog(null,"Kalender wurde perfekt angelegt");
				}
			}
		} catch(SQLException ex){
			//System.out.println("von ResultSet SQLState: " + ex.getSQLState());
			//System.out.println("von ResultSet ErrorCode: " + ex.getErrorCode ());
			//System.out.println("ErrorCode: " + ex.getErrorCode ());
			//System.out.println("von ResultSet ErrorMessage: " + ex.getMessage ());
		}

	} catch(SQLException ex) {
		//System.out.println("von stmt -SQLState: " + ex.getSQLState());
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
	}
}
	

/******************************/	


class ProgressVerarbeiten extends SwingWorker<Void,JComponent>{
	JProgressBar jpb = null;
	ProgressVerarbeiten(JComponent laufbalken){
			jpb = (JProgressBar) laufbalken;
	}
	@Override
	protected synchronized Void doInBackground() throws Exception {
		while(SysUtilKalenderanlegen.dblaeuft){
			/*
			SysUtilKalenderanlegen.Fortschritt.setValue(SysUtilKalenderanlegen.progress);
			SysUtilKalenderanlegen.Fortschritt.repaint();
			*/
			jpb.setValue(SysUtilKalenderanlegen.progress);
			jpb.repaint();

			Thread.sleep(10);
		}
		return null;
		
	}
}

class SchreibeNeuenKalender extends Thread implements Runnable{
	Statement stmt = null;
	ResultSet rs = null;
	String statement;
	boolean geklappt = false;
 
	public void setzeStatement(String statement){
		this.statement = statement;
		start();
	}
	public synchronized void run(){
		//Vector treadVect = new Vector();
		try {
			stmt = (Statement) Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
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
	}
	
}
