package theraPiUpdates;





import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.net.ftp.FTPFile;
import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;




import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;





public class UpdatePanel extends JXPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8445390544969581869L;
	TheraPiUpdates eltern = null;
	UpdateTab updateTab = null;
	
	UpdateTableModel tabmod = null;
	JXTable tab = null;

	public JProgressBar pbar = null;
	
	public JTextArea ta = null;
	
	DecimalFormat dcf = new DecimalFormat("##########0.00");
	SimpleDateFormat datumsFormat = new SimpleDateFormat ("dd.MM.yyyy  HH:mm:ss"); //Konv.

	public static Vector<Vector<String>> updatefiles = new Vector<Vector<String>>();
	public static Vector<String[]> mandvec = new Vector<String[]>();
	
	public UpdateListSelectionHandler updateListener = null;
	
	FTPTools ftpt = null;
	
	Image imgkeinupdate = new ImageIcon(TheraPiUpdates.proghome+"icons/clean.png").getImage().getScaledInstance(16,16, Image.SCALE_SMOOTH);
	Image imgupdate = new ImageIcon(TheraPiUpdates.proghome+"icons/application-exit.png").getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);	
	ImageIcon icokeinupdate;
	ImageIcon icoupdate;
	public FTPFile[] ffile = null;
	
	static String dbIpAndName = "";
	static String dbUser = "";
	static String dbPassword = "";
	
	public static boolean DbOk;
	public Connection conn;
	
	UpdatePanel(TheraPiUpdates xeltern,UpdateTab xupdateTab){
	
		
		super();
		eltern = xeltern;
		if(xupdateTab != null){
			updateTab = xupdateTab;
		}
		setLayout(new BorderLayout());
		add(getHeader(),BorderLayout.NORTH);
		add(getContent(),BorderLayout.CENTER);
		doUpdateCheck();
	}
	public void doUpdateCheck(){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				icokeinupdate = new ImageIcon(imgkeinupdate);
				icoupdate = new ImageIcon(imgupdate);
				doHoleUpdateConf();
				updateCheck(TheraPiUpdates.proghome+"update.files");
				doFtpTest();
				return null;
			}
		}.execute();
	}
	private JXHeader getHeader(){
		JXHeader head = new JXHeader();
		String titel = "<html><font size='5'><font color='e77817'>Thera-Pi</font>&nbsp;&nbsp; Update-Explorer</font></html>";
        head.setTitle(titel);
        String description = "<html>Ein rotes "+
        "<img src='file:///"+TheraPiUpdates.proghome+"icons/application-exit.png' width='16' height='16' align=\"bottom\">"+ 
        "signalisiert, daß die angezeigte Datei <b>neuer</b> ist als die Datei die sich auf Ihrem Rechner befindet.<br>"+
        "Wenn Sie in der Tabelle einen Doppelklick auf einer dieser Dateien ausführen, kopieren Sie diese Datei in Ihre Thera-Pi-Installation."+
        "<b><font color='aa0000'><br>Achtung:</font></b><br>Wenn INI-Dateien zum Update angeboten werden überschreiben Sie evtl. bestehende individuelle INI-Dateien. Bitte "+
        "machen Sie in diesem Fall vor dem Update eine <b>Sicherungskopie Ihres 'INI-Verzeichnisses'</b></html>";
        head.setDescription(description);
        head.setIcon(new ImageIcon(TheraPiUpdates.proghome+"icons/TPorg.png"));   
		return head;
	}
	
	private JXPanel getContent(){
		JXPanel jpan = new JXPanel();
		String xwerte = "5dlu,p:g,5dlu";
		//                1     2    3    4     5   6  7     8    9   10
		String ywerte = "5dlu,0dlu,0dlu,100dlu,5dlu,p,2dlu,0dlu:g,5dlu,p,5dlu";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		jpan.setLayout(lay);
		tabmod = new UpdateTableModel();
		tabmod.setColumnIdentifiers(new String[] { "Update-Datei","Dateidatum/Uhrzeit","Größe in Bytes","aktuell"});

		tab = new JXTable(tabmod);
		tab.setSortable(false);
		tab.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent evt){
				if(evt.getClickCount()==2){
					testeObUpdate(tab.getSelectedRow());
				}
			}
		});
		updateListener = new UpdateListSelectionHandler();
		tab.getSelectionModel().addListSelectionListener(updateListener);
		JScrollPane jscr = JCompTools.getTransparentScrollPane(tab);
		jscr.validate();
		jpan.add(jscr,cc.xy(2,4));
		pbar = new JProgressBar();
		pbar.setStringPainted(false);
		jpan.add(pbar,cc.xy(2,6));
		
		ta = new JTextArea();
		ta.setFont(new Font("Courier",Font.PLAIN,12));
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		ta.setEditable(false);
		ta.setBackground(Color.WHITE);
		ta.setForeground(Color.BLUE);
		jscr = JCompTools.getTransparentScrollPane(ta);
		jscr.validate();
		jpan.add(jscr,cc.xy(2,8,CellConstraints.FILL,CellConstraints.FILL));

		JButton but = new JButton("Update-Explorer beenden");
		but.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(TheraPiUpdates.starteTheraPi){
					int anfrage = JOptionPane.showConfirmDialog(null, "Wollen Sie Thera-Pi 1.0 jetzt starten?","Thera-Pi starten?",JOptionPane.YES_NO_OPTION);
					if(anfrage == JOptionPane.YES_OPTION){
						try {
							Runtime.getRuntime().exec("java -jar "+TheraPiUpdates.proghome+"TheraPi.jar");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					System.exit(0);
				}else{
					System.exit(0);
				}
			}
		});
		jpan.add(but,cc.xy(2,10,CellConstraints.RIGHT,CellConstraints.BOTTOM));
		jpan.validate();

		return jpan;
	}
	private void testeObUpdate(int row){
		String upddatei = tab.getValueAt(row, 0).toString();
		Long gross = Long.parseLong(tab.getValueAt(row, 2).toString());
		if(tab.getValueAt(row, 3).equals(this.icokeinupdate)){
			JOptionPane.showMessageDialog(null, "Die Datei --> "+upddatei+" <-- ist bereits auf dem neuesten Stand. Update nicht erforderlich");
			return;
		}
		boolean ok = true;
		for(int i = 0; i < updatefiles.size();i++){
			if(updatefiles.get(i).get(0).equals(upddatei)){
				if(upddatei.equals("TabellenUpdate.sql")){
					int anfrage = JOptionPane.showConfirmDialog(null, "Soll der Tabellen-Update jetzt durchgeführt werden?","Achtung wichtige Benutzeranfrage",JOptionPane.YES_NO_OPTION);
					if(anfrage==JOptionPane.YES_OPTION){
						doTabellenUpdate();	
						tabmod.setValueAt((ImageIcon) icokeinupdate,row, 3);
					}
					break;
				}
				if(upddatei.equals("ProgrammAusfuehren.sql")){
					int anfrage = JOptionPane.showConfirmDialog(null, "Soll das im Change-Log aufgeführte Programm jetzt gestartet werden?","Achtung wichtige Benutzeranfrage",JOptionPane.YES_NO_OPTION);
					if(anfrage==JOptionPane.YES_OPTION){
						doProgExecute();	
						tabmod.setValueAt((ImageIcon) icokeinupdate,row, 3);
					}
					break;
				}
				String cmd = "<html>Wollen Sie die Update-Datei <b>"+updatefiles.get(i).get(0)+"</b> nach<br>"+
				"<b>"+updatefiles.get(i).get(1)+"</b> kopieren</html>";
				int anfrage = JOptionPane.showConfirmDialog(null, cmd,"Achtung wichtige Benutzeranfrage",JOptionPane.YES_NO_OPTION);
				if(anfrage==JOptionPane.YES_OPTION){
					pbar.setStringPainted(true);
					pbar.getParent().validate();
					final int ix = i;
					final String xupdate = upddatei;
					final long xgross = gross;
					final int xrow = row;
					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() throws Exception {
							pbar.setValue(0);
							String nurvz = updatefiles.get(ix).get(1).substring(0,updatefiles.get(ix).get(1).lastIndexOf("/")+1);
							System.out.println(updatefiles.get(ix).get(1));
							System.out.println(updatefiles.get(ix).get(0));
							System.out.println(nurvz);
							try{
								if(updatefiles.get(ix).get(1).endsWith(".ini")){
									File f = new File(updatefiles.get(ix).get(1));
									f.renameTo(new File(updatefiles.get(ix).get(1).replace(".ini", ".bak")));
								}
							}catch(Exception ex){
								JOptionPane.showMessageDialog(null, "Fehler beim Umbenennen der Datei "+updatefiles.get(ix).get(1));
							}
							ftpt = new FTPTools();
							ftpt.holeDatei(xupdate, nurvz, true, getInstance(),xgross);
							ftpt = null;
							pbar.setValue(0);
							tabmod.setValueAt((ImageIcon) icokeinupdate,xrow, 3);
							return null;
						}

					}.execute();
					
				}else{
					ok = false;
				}
			}
		}
		if(ok){
			//tabmod.setValueAt((ImageIcon) this.icokeinupdate,row, 3);	
		}
		
	}
	private UpdatePanel getInstance(){
		return this;
	}
	private void doHoleUpdateConf(){
		ftpt = new FTPTools();
		ftpt.holeDatei("update.files", TheraPiUpdates.proghome, false, getInstance(),-1);
		ftpt = null;
	}
	private void doFtpTest(){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				TheraPiUpdates.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				ftpt = new FTPTools();
				
				ffile = ftpt.holeDatNamen();
				Vector<Object> vec = new Vector<Object>();
				for(int i = 0; i < ffile.length;i++){
					if( (!ffile[i].getName().toString().trim().equals(".")) &&
							(!ffile[i].getName().toString().trim().equals("..")) &&
							(!ffile[i].getName().toString().startsWith("update."))){
						vec.clear();
						vec.add(ffile[i].getName().toString());
						vec.add(datumsFormat.format(ffile[i].getTimestamp().getTime().getTime()));
						vec.add(Long.valueOf(ffile[i].getSize()).toString());
						if(mussUpdaten(ffile[i].getName().toString().trim(),ffile[i].getTimestamp().getTime().getTime())){
							vec.add(icoupdate);
							tabmod.addRow((Vector<?>)vec.clone());
						}
						//System.out.println(ffile[i].getName());
						//System.out.println(ffile[i].getTimestamp().getTime().getTime());
						//System.out.println(ffile[i].getSize() + " Bytes");
					}
				}
				if(tabmod.getRowCount()> 0){
					tab.setRowSelectionInterval(0, 0);
				}else{
					JOptionPane.showMessageDialog(null, "Ihre Thera-Pi-Installation ist auf dem aktuellen Stand.");
				}
				System.out.println("\n*********************************");
				System.out.println("   Insgesamt getestete Files: "+ffile.length);
				System.out.println("Davon muessen updated werden: "+tabmod.getRowCount());
				System.out.println("*********************************\n");
				/*
				System.out.println("Insgesamt = "+ffile.length+" Dateien");
				File file = new File("C:/RehaVerwaltung/Reha.jar");
				System.out.println("Größe Originaldatei  = "+file.length()+" Bytes");
				System.out.println(file.lastModified());
				ftpt.holeDatei("update.files", "C:/ftptests/", true, getInstance(),-1);
				Thread.sleep(100);
				pbar.setValue(0);
				ftpt.holeDatei("Reha.jar", "C:/ftptests/", true, getInstance(),-1);
				*/
				ftpt = null;
				TheraPiUpdates.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

				return null;
			}
			
		}.execute();
		 
		
	}
	private boolean mussUpdaten(String datei,Long datum){
		try{
		File f = null;
		for(int i = 0; i < updatefiles.size();i++){
			if(updatefiles.get(i).get(0).equals(datei)){
				f = new File(updatefiles.get(i).get(1));
				if(!f.exists()){
					return true;
				}
				if(f.lastModified() < datum){
					//System.out.println("Zeitunterschied  = "+(f.lastModified()-datum));
					//System.out.println(datumsFormat.format(f.lastModified()));
					//System.out.println(datumsFormat.format(datum));
					return true;
				}
			}
		}
		//System.out.println("Zeitunterschied  = "+(f.lastModified()-datum));
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return false;
	}
	/************************************************************************/
	private static void updateCheck(String xupdatefile){
		String updatedir = "";
		String zeile = "";
		FileReader reader = null;
		BufferedReader in = null;
		
		/************************/
		INIFile inif = new INIFile(TheraPiUpdates.proghome+"ini/mandanten.ini");
		int AnzahlMandanten = inif.getIntegerProperty("TheraPiMandanten", "AnzahlMandanten");
		for(int i = 0; i < AnzahlMandanten;i++){
			String[] mand = {null,null};
			mand[0] = new String(inif.getStringProperty("TheraPiMandanten", "MAND-IK"+(i+1)));
			mand[1] = new String(inif.getStringProperty("TheraPiMandanten", "MAND-NAME"+(i+1)));
			mandvec.add(mand);
		}
		
		
		/************************/
	
		try {
			Vector<String> dummy = new Vector<String>();
			zeile = "";
			reader = new FileReader(xupdatefile);
			in = new BufferedReader(reader);
			//String pfad = "";
			String[] sourceAndTarget = {null,null};
			Vector<Object> targetvec = new Vector<Object>(); 
			while ((zeile = in.readLine()) != null) {
				if(!zeile.startsWith("#")){
					if(zeile.length()>5){
						sourceAndTarget = zeile.split("@");
						//System.out.println(zeile);
						//System.out.println(sourceAndTarget[0].trim());
						//System.out.println(sourceAndTarget[1].trim().replace("%proghome%", proghome));
						//System.out.println(sourceAndTarget.length);
						if(sourceAndTarget.length==2){
							//nur dann Dateien eintrgen
							//pfad = zeile;
							if(sourceAndTarget[1].contains("%proghome%")){
								dummy.clear();
								dummy.add(updatedir+sourceAndTarget[0].trim());
								dummy.add(sourceAndTarget[1].trim().replace("%proghome%", TheraPiUpdates.proghome).replace("//", "/"));
								if(! targetvec.contains(dummy.get(1))){
									targetvec.add(new String((String)dummy.get(1)));
									updatefiles.add( ((Vector<String>)dummy.clone()));									
								}
							}else if(sourceAndTarget[1].contains("%userdir%")){
								String home = sourceAndTarget[1].trim().replace("%userdir%", TheraPiUpdates.proghome).replace("//", "/"); 
								for(int i = 0; i < mandvec.size();i++){
									dummy.clear();
									dummy.add(updatedir+sourceAndTarget[0].trim());
									dummy.add(home.replace("%mandantik%", mandvec.get(i)[0]));
									if(! targetvec.contains(dummy.get(1))){
										updatefiles.add( (Vector<String>) ((Vector<String>)dummy.clone()) );									
									}
								}
							}
						// Ende nur dann Dateien eintragen	
						}
					}
				}
			}
			in.close();
			reader.close();
			if(updatefiles.size()>0){
				//System.out.println(updatefiles);
				//System.out.println("Anzahl Update-Dateien = "+updatefiles.size());
				//kopiereUpdate();
			}
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,"Fehler beim Bezug der Update-Informationen.\nBitte informieren Sie den Administrator umgehend");
		}
	}
	/*******************************************************/
	private void doProgExecute(){
		ftpt = new FTPTools();
		Long xgross = Long.parseLong(tab.getValueAt(tab.getSelectedRow(), 2).toString());
		ftpt.holeDatei("ProgrammAusfuehren.sql", TheraPiUpdates.proghome, true, getInstance(),xgross);
		ftpt = null;
		pbar.setValue(0);
	    File file = null;
	    FileReader freader = null;
	    LineNumberReader lnreader = null;
	    Vector<String> vecstmt = new Vector<String>();
	    try{
		      file = new File(TheraPiUpdates.proghome+"ProgrammAusfuehren.sql");
		      freader = new FileReader(file);
		      lnreader = new LineNumberReader(freader);
		      String line = "";
		      while ((line = lnreader.readLine()) != null){
		    	  if(!String.valueOf(line).trim().equals("")){
			    	  vecstmt.add(String.valueOf(line));		    		  
		    	  }
		    	  System.out.println("Statement = "+line);
		      }
	    } catch (FileNotFoundException e) {
				e.printStackTrace();
		} catch (IOException e) {
				e.printStackTrace();
		}
	    finally{
		      try {
				freader.close();
				lnreader.close();
		      } catch (IOException e) {
				e.printStackTrace();
		      }
	    }
	    if(vecstmt.size() > 0){
	    	String cmd = vecstmt.get(0);
	    	cmd = cmd.replace("@proghome/", TheraPiUpdates.proghome);
	    	final String command = String.valueOf(cmd);
	    	new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					Runtime.getRuntime().exec(command);
					return null;
				}
	    		
	    	}.execute();
	    }else{
	    	JOptionPane.showMessageDialog(null,"Keine Programm zur Ausführung gefunden");
	    }

		
	}
	public static String Escaped(String string){
		String escaped = string.replaceAll("\'", "\\\\'");
		escaped = escaped.replaceAll("\"", "\\\\\"");
		return escaped;
	}

	private void doTabellenUpdate(){
		String ik = "";
		ftpt = new FTPTools();
		Long xgross = Long.parseLong(tab.getValueAt(tab.getSelectedRow(), 2).toString());
		ftpt.holeDatei("TabellenUpdate.sql", TheraPiUpdates.proghome, true, getInstance(),xgross);
		ftpt = null;
		pbar.setValue(0);
	    File file = null;
	    FileReader freader = null;
	    LineNumberReader lnreader = null;
	    Vector<String> vecstmt = new Vector<String>();
	    try{
		      file = new File(TheraPiUpdates.proghome+"TabellenUpdate.sql");
		      freader = new FileReader(file);
		      lnreader = new LineNumberReader(freader);
		      String line = "";
		      while ((line = lnreader.readLine()) != null){
		    	  if(!String.valueOf(line).trim().equals("")){
			    	  //vecstmt.add(String.valueOf(Escaped(line)));		    		  
			    	  vecstmt.add(String.valueOf(line));
		    	  }
		    	  System.out.println("Statement = "+line);
		      }
	    } catch (FileNotFoundException e) {
				e.printStackTrace();
		} catch (IOException e) {
				e.printStackTrace();
		}
	    finally{
	      try {
			freader.close();
			lnreader.close();
	      } catch (IOException e) {
			e.printStackTrace();
	      }
	    }
	    if(vecstmt.size() > 0){
	    	SwingUtilities.invokeLater(new Runnable(){
	    		public void run(){
	    	    	TheraPiUpdates.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));	    			
	    		}
	    	});

			for(int i = 0; i < mandvec.size();i++){
				
				ik = mandvec.get(i)[0];
				holeDBZugang(TheraPiUpdates.proghome+"ini/"+ik+"/rehajava.ini");
				System.out.println("Mandant "+TheraPiUpdates.proghome+"ini/"+ik+"/rehajava.ini");
				StarteDB();
				for(int x = 0; x < vecstmt.size();x++){
					try {
						SqlInfo.sqlAusfuehren(conn, vecstmt.get(x));
						System.out.println("Execute = "+vecstmt.get(x));
						//System.out.println("Warnings = "+conn.getWarnings().getSQLState());
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null,"Fehler beim anlegen der Tabelle\nDer Fehlertext lautet:\n"+e.getMessage()); 
						//JOptionPane.showMessageDialog(null, "Fehler in der Ausführung des Sql-Statements\n"+vecstmt.get(x));
						e.printStackTrace();
					}
				}
				StopeDB();
			}
	    	SwingUtilities.invokeLater(new Runnable(){
	    		public void run(){
	    	    	TheraPiUpdates.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));	    			
	    		}
	    	});

	    }else{
	    	JOptionPane.showMessageDialog(null,"Keine Statements für Tabellen-Update gefunden");
	    }
	}
	/*******************************************************/
	private void StopeDB(){
		if (conn != null){
			try{
			conn.close();}
			catch(final SQLException e){}
		}
	}
	private void StarteDB(){
		final String sDB = "SQL";
		if (conn != null){
			try{
			conn.close();}
			catch(final SQLException e){}
		}
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (InstantiationException e) {
			e.printStackTrace();
    		System.out.println(sDB+"Treiberfehler: " + e.getMessage());
    		DbOk = false;
    		return ;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
    		System.out.println(sDB+"Treiberfehler: " + e.getMessage());
    		DbOk = false;
    		return ;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
    		System.out.println(sDB+"Treiberfehler: " + e.getMessage());
    		DbOk = false;
    		return ;
		}	
    	try {
    		
			conn = (Connection) DriverManager.getConnection(dbIpAndName,dbUser,dbPassword);
			DbOk = true;
			System.out.println("Datenbankkontakt hergestellt");
    	} 
    	catch (final SQLException ex) {
    		System.out.println("SQLException: " + ex.getMessage());
    		System.out.println("SQLState: " + ex.getSQLState());
    		System.out.println("VendorError: " + ex.getErrorCode());
    		DbOk = false;
    
    	}
        return;
	}

	private void holeDBZugang(String pfad){
			System.out.println("hole daten aus INI-Datei "+pfad);
			INIFile inif = new INIFile(pfad);
			dbIpAndName = inif.getStringProperty("DatenBank","DBKontakt1");
			dbUser = inif.getStringProperty("DatenBank","DBBenutzer1");
			String pw = inif.getStringProperty("DatenBank","DBPasswort1");
			String decrypted = null;
			if(pw != null){
				Verschluesseln man = Verschluesseln.getInstance();
				man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
				decrypted = man.decrypt (pw);
			}else{
				decrypted = new String("");
			}
			dbPassword = decrypted.toString();
	}
	
	
	/************************************************************************/

	class UpdateTableModel extends DefaultTableModel{
		   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Class<?> getColumnClass(int columnIndex) {
			if(columnIndex==3){
				return ImageIcon.class;
			}
			return String.class;
	    }

		public boolean isCellEditable(int row, int col) {
			
				return false;				
		}
		   
	}
	private boolean testeObLogVorhanden(String datei){
		for(int i = 0; i < ffile.length;i++){
			if(ffile[i].getName().equals(datei+".log")){
				return true;
			}
		}
		return false;
	}
	private String holeLogText(String logDatei){
		FTPTools ftp = new FTPTools();
		return ftp.holeLogDateiSilent(logDatei);
	}

	class UpdateListSelectionHandler implements ListSelectionListener {

	    public void valueChanged(ListSelectionEvent e) {
	        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
	        boolean isAdjusting = e.getValueIsAdjusting();
	        if(isAdjusting){
	        	return;
	        }
	        if (lsm.isSelectionEmpty()) {

	        } else {
	            int minIndex = lsm.getMinSelectionIndex();
	            int maxIndex = lsm.getMaxSelectionIndex();
	            for (int i = minIndex; i <= maxIndex; i++) {
	                if (lsm.isSelectedIndex(i)) {
	                	final int ix = i;
	                	
	                	new SwingWorker<Void,Void>(){

							@Override
							protected Void doInBackground() throws Exception {
								try{
									int modi = tab.convertRowIndexToModel(ix);
									if(testeObLogVorhanden(tabmod.getValueAt(modi,0).toString())){
										TheraPiUpdates.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
										try{
											ta.setForeground(Color.BLUE);	
											ta.setText(holeLogText( tabmod.getValueAt(modi,0).toString()+".log" ));
										}catch(Exception ex){
											ex.printStackTrace();
											ta.setText("Fehler beim Bezug der Log-Datei");
										}
										TheraPiUpdates.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
									}else{
										ta.setForeground(Color.RED);
										ta.setText("Für diese Update-Datei ist kein ChangeLog verfügbar");
									}
								}catch(Exception ex){
									ex.printStackTrace();
								}

	    						return null;
							}
	                		
	                	}.execute();

	                    break;
	                }
	            }
	        }
	    }
	}	

}
