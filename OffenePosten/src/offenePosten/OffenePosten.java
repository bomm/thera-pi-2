package offenePosten;

import java.awt.Cursor;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdesktop.swingworker.SwingWorker;

import CommonTools.INIFile;
import CommonTools.INITool;
import CommonTools.SqlInfo;
import CommonTools.StartOOApplication;
import CommonTools.Verschluesseln;
import RehaIO.RehaIOMessages;
import RehaIO.RehaReverseServer;
import RehaIO.SocketClient;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;




public class OffenePosten implements WindowListener{

	/**
	 * @param args
	 */
	public static boolean DbOk;
	JFrame jFrame;
	public static JFrame thisFrame = null;
	public Connection conn;
	public static OffenePosten thisClass;
	
	public static IOfficeApplication officeapplication;
	
	public String dieseMaschine = null;
	/*
	public static String dbIpAndName = null;
	public static String dbUser = null;
	public static String dbPassword = null;
	
	
*/
	public final Cursor wartenCursor = new Cursor(Cursor.WAIT_CURSOR);
	public final Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);

	public static String dbIpAndName = "jdbc:mysql://192.168.2.3:3306/rtadaten";
	public static String dbUser = "rtauser";
	public static String dbPassword = "rtacurie";
	public static String officeProgrammPfad = "C:/Programme/OpenOffice.org 3";
	public static String officeNativePfad = "C:/RehaVerwaltung/Libraries/lib/openofficeorg/";
	public static String progHome = "C:/RehaVerwaltung/";
	public static String aktIK = "510841109";

	public static HashMap<String,Object> mahnParameter = new HashMap<String,Object>();
	
	public static HashMap<String,String> hmAbrechnung = new HashMap<String,String>();
	public static HashMap<String,String> hmFirmenDaten  = null;
	public static HashMap<String,String> hmAdrPDaten = new HashMap<String,String>();
	/*
	public static String hmRechnungPrivat = "C:/RehaVerwaltung/vorlagen/HMRechnungPrivat.ott.Kopie.ott";
	public static String hmRechnungKasse = "C:/RehaVerwaltung/vorlagen/HMRechnungPrivat.ott.Kopie.ott";
	public static String rhRechnungPrivat = "C:/RehaVerwaltung/vorlagen/HMRechnungPrivat.ott.Kopie.ott";
	public static String rhRechnungKasse = "C:/RehaVerwaltung/vorlagen/HMRechnungPrivat.ott.Kopie.ott";
	*/
	
	public static boolean testcase = false;
	public SqlInfo sqlInfo;
	
	OffenepostenTab otab = null;
	
	public static int xport = -1;
	public static boolean xportOk = false;
	public RehaReverseServer rehaReverseServer = null;
	public static int rehaReversePort = -1;

	public boolean  isLibreOffice;
	
	public static void main(String[] args) {
		OffenePosten application = new OffenePosten();
		application.getInstance();
		application.getInstance().sqlInfo = new SqlInfo();
		
		if(args.length > 0 || testcase){
			if(!testcase){
				System.out.println("hole daten aus INI-Datei "+args[0]);
				INIFile inif = new INIFile(args[0]+"ini/"+args[1]+"/rehajava.ini");
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
				officeProgrammPfad = inif.getStringProperty("OpenOffice.org","OfficePfad");
				officeNativePfad = inif.getStringProperty("OpenOffice.org","OfficeNativePfad");
				progHome = args[0];
				aktIK = args[1];
				INITool.init(progHome+"ini/"+aktIK+"/");
				/*******************************************************/
				final OffenePosten xoffeneposten = application;
				new SwingWorker<Void,Void>(){
					@Override
					
					protected Void doInBackground() throws java.lang.Exception {
						xoffeneposten.starteDB();
						long zeit = System.currentTimeMillis();
						while(! DbOk){
							try {
								Thread.sleep(20);
								if(System.currentTimeMillis()-zeit > 10000){
									System.exit(0);
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						if(!DbOk){
							JOptionPane.showMessageDialog(null, "Datenbank konnte nicht geöffnet werden!\nReha-Statistik kann nicht gestartet werden");
							System.exit(0);
						}
						OffenePosten.starteOfficeApplication();
						return null;
					}
					
				}.execute();
				/*******************************************************/
				final String arg0 = args[0];
				final String arg1 = args[1];
				new SwingWorker<Void,Void>(){
					@Override
					
					protected Void doInBackground() throws java.lang.Exception {
						while(!OffenePosten.DbOk){
							Thread.sleep(30);
						}
						INIFile oinif = INITool.openIni(arg0+"ini/"+arg1+"/", "offeneposten.ini");
						mahnParameter.put("frist1", (Integer) oinif.getIntegerProperty("General","TageBisMahnung1") );
						mahnParameter.put("frist2", (Integer) oinif.getIntegerProperty("General","TageBisMahnung2") );
						mahnParameter.put("frist3", (Integer) oinif.getIntegerProperty("General","TageBisMahnung3") );
						mahnParameter.put("einzelmahnung", (Boolean) (oinif.getIntegerProperty("General","EinzelMahnung") == 1 ? Boolean.TRUE : Boolean.FALSE) );
						mahnParameter.put("drucker", (String) oinif.getStringProperty("General","MahnungDrucker") );
						mahnParameter.put("exemplare", (Integer) oinif.getIntegerProperty("General","MahnungExemplare") );
						mahnParameter.put("inofficestarten", (Boolean) (oinif.getIntegerProperty("General","InOfficeStarten") == 1 ? Boolean.TRUE : Boolean.FALSE) );
						mahnParameter.put("erstsuchenab", (String) oinif.getStringProperty("General","AuswahlErstAb") );
						mahnParameter.put("formular1", (String) oinif.getStringProperty("General","FormularMahnung1")  );
						mahnParameter.put("formular2", (String) oinif.getStringProperty("General","FormularMahnung2")  );
						mahnParameter.put("formular3", (String) oinif.getStringProperty("General","FormularMahnung3")  );
						mahnParameter.put("formular4", (String) oinif.getStringProperty("General","FormularMahnung4")  );
						mahnParameter.put("diralterechnungen", (String) oinif.getStringProperty("General","DirAlteRechnungen")  );
						System.out.println(mahnParameter);
						AbrechnungParameter(progHome);
						FirmenDaten(progHome);
						
						
					return null;	
					}
				}.execute();	
				if(args.length >= 3){
					rehaReversePort = Integer.parseInt(args[2]);
				}

			}else{
				mahnParameter.put("frist1", (Integer) 31 );
				mahnParameter.put("frist2", (Integer) 11 );
				mahnParameter.put("frist3", (Integer) 11);
				mahnParameter.put("einzelmahnung", (Boolean) Boolean.TRUE);
				mahnParameter.put("drucker", (String) "RICOH Aficio MP C2800 PS SW" );
				mahnParameter.put("exemplare", (Integer) 5 );
				mahnParameter.put("inofficestarten", (Boolean) Boolean.TRUE);
				mahnParameter.put("erstsuchenab", (String) "2009-01-01" );
				mahnParameter.put("formular1", (String) "2009-01-01" );
				mahnParameter.put("formular2", (String) "2009-01-01" );
				mahnParameter.put("formular3", (String) "2009-01-01" );
				mahnParameter.put("formular4", (String) "2009-01-01" );
				mahnParameter.put("diralterechnungen", "l:/projekte/rta/dbf/rechnung/" );
				AbrechnungParameter(progHome);
				FirmenDaten(progHome);

			}
			if(testcase){
				System.out.println(mahnParameter);
				System.out.println("TestCase = "+testcase);
				AbrechnungParameter(progHome);
				FirmenDaten(progHome);

			}
			application.getJFrame();
		}else{
			JOptionPane.showMessageDialog(null, "Keine Datenbankparameter übergeben!\nReha-Statistik kann nicht gestartet werden");
			System.exit(0);
		}
		
	}
	/********************/
	
	public JFrame getJFrame(){
		try {
			UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		thisClass = this;
		jFrame = new JFrame(){
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void setVisible(final boolean visible) {
				
				if(getState()!=JFrame.NORMAL) { setState(JFrame.NORMAL); }

				  if (visible) {
				      //setDisposed(false);
				  }
				  if (!visible || !isVisible()) { 
				      super.setVisible(visible);
				  }

				  if (visible) {
				      int state = super.getExtendedState();
				      state &= ~JFrame.ICONIFIED;
				      super.setExtendedState(state);
				      super.setAlwaysOnTop(true);
				      super.toFront();
				      super.requestFocus();
				      super.setAlwaysOnTop(false);
				  }
			}

			@Override
			public void toFront() {
				  super.setVisible(true);
				  int state = super.getExtendedState();
				  state &= ~JFrame.ICONIFIED;
				  super.setExtendedState(state);
				  super.setAlwaysOnTop(true);
				  super.toFront();
				  super.requestFocus();
				  super.setAlwaysOnTop(false);
			}	
		};
		try{
			rehaReverseServer = new RehaReverseServer(7000);
		}catch(Exception ex){
			rehaReverseServer = null;
			ex.printStackTrace();
		}
		sqlInfo.setFrame(jFrame);
		jFrame.addWindowListener(this);
		jFrame.setSize(1000,750);
		jFrame.setTitle("Thera-Pi  Offene-Posten / Mahnwesen  [IK: "+aktIK+"] "+"[Server-IP: "+dbIpAndName+"]");
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setLocationRelativeTo(null);
		otab = new OffenepostenTab();
		otab.setHeader(0);
		
		jFrame.getContentPane().add (otab);
		jFrame.setVisible(true);
		thisFrame = jFrame;
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				otab.setFirstFocus();		
			}
		});

		try{
			new SocketClient().setzeRehaNachricht(OffenePosten.rehaReversePort,"AppName#OffenePosten#"+Integer.toString(OffenePosten.xport));
			new SocketClient().setzeRehaNachricht(OffenePosten.rehaReversePort,"OffenePosten#"+RehaIOMessages.IS_STARTET);
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null, "Fehler in der Socketkommunikation");
			ex.printStackTrace();
		}

		return jFrame;
	}
	
	
	/********************/
	
	public OffenePosten getInstance(){
		thisClass = this;
		return this;
	}
	
	/*******************/
	
	public void starteDB(){
		DatenbankStarten dbstart = new DatenbankStarten();
		dbstart.run(); 			
	}
	
	/*******************/
	
	public static void stoppeDB(){
		try {
			OffenePosten.thisClass.conn.close();
			OffenePosten.thisClass.conn = null;
		} catch (SQLException e) {
			e.printStackTrace();
		} 			
	}
	
	/**********************************************************
	 * 
	 */
	final class DatenbankStarten implements Runnable{
		private void StarteDB(){
			final OffenePosten obj = OffenePosten.thisClass;

			final String sDB = "SQL";
			if (obj.conn != null){
				try{
				obj.conn.close();}
				catch(final SQLException e){}
			}
			try{
				Class.forName("com.mysql.jdbc.Driver").newInstance();
	        } catch (InstantiationException e) {
				e.printStackTrace();
        		System.out.println(sDB+"Treiberfehler: " + e.getMessage());
        		OffenePosten.DbOk = false;
	    		return ;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
        		System.out.println(sDB+"Treiberfehler: " + e.getMessage());
        		OffenePosten.DbOk = false;
	    		return ;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
        		System.out.println(sDB+"Treiberfehler: " + e.getMessage());
        		OffenePosten.DbOk = false;
	    		return ;
			}	
        	try {
        		
   				obj.conn = (Connection) DriverManager.getConnection(dbIpAndName,dbUser,dbPassword);
   				OffenePosten.thisClass.sqlInfo.setConnection(obj.conn);
				OffenePosten.DbOk = true;
    			System.out.println("Datenbankkontakt hergestellt");
        	} 
        	catch (final SQLException ex) {
        		System.out.println("SQLException: " + ex.getMessage());
        		System.out.println("SQLState: " + ex.getSQLState());
        		System.out.println("VendorError: " + ex.getErrorCode());
        		OffenePosten.DbOk = false;
        
        	}
	        return;
		}
		public void run() {
			StarteDB();
		}
	
	
	}
	/*****************************************************************
	 * 
	 */

	@Override
	public void windowActivated(WindowEvent arg0) {
	}
	@Override
	public void windowClosed(WindowEvent arg0) {
		if(OffenePosten.thisClass.conn != null){
			try {
				OffenePosten.thisClass.conn.close();
				System.out.println("Datenbankverbindung wurde geschlossen");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(OffenePosten.thisClass.rehaReverseServer != null){
			new SocketClient().setzeRehaNachricht(OffenePosten.rehaReversePort,"OffenePosten#"+RehaIOMessages.IS_FINISHED);
			try {
				OffenePosten.thisClass.rehaReverseServer.serv.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.exit(0);
	}
	@Override
	public void windowClosing(WindowEvent arg0) {
		if(OffenePosten.thisClass.conn != null){
			try {
				OffenePosten.thisClass.conn.close();
				System.out.println("Datenbankverbindung wurde geschlossen");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(OffenePosten.thisClass.rehaReverseServer != null){
			new SocketClient().setzeRehaNachricht(OffenePosten.rehaReversePort,"OffenePosten#"+RehaIOMessages.IS_FINISHED);
			try {
				OffenePosten.thisClass.rehaReverseServer.serv.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.exit(0);
	}
	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}
	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}
	@Override
	public void windowIconified(WindowEvent arg0) {
	}
	@Override
	public void windowOpened(WindowEvent arg0) {
	}
	
	public static void AbrechnungParameter(String proghome){
		hmAbrechnung.clear();
		/********Heilmittelabrechnung********/

		INIFile inif = INITool.openIni(proghome+"ini/"+aktIK+"/", "abrechnung.ini");
		hmAbrechnung.put("hmgkvformular", inif.getStringProperty("HMGKVRechnung", "Rformular"));
		hmAbrechnung.put("hmgkvrechnungdrucker", inif.getStringProperty("HMGKVRechnung", "Rdrucker"));
		hmAbrechnung.put("hmgkvtaxierdrucker", inif.getStringProperty("HMGKVRechnung", "Tdrucker"));
		hmAbrechnung.put("hmgkvbegleitzettel", inif.getStringProperty("HMGKVRechnung", "Begleitzettel"));
		hmAbrechnung.put("hmgkvrauchdrucken", inif.getStringProperty("HMGKVRechnung", "Rauchdrucken"));
		hmAbrechnung.put("hmgkvrexemplare", inif.getStringProperty("HMGKVRechnung", "Rexemplare"));

		hmAbrechnung.put("hmpriformular", inif.getStringProperty("HMPRIRechnung", "Pformular"));
		hmAbrechnung.put("hmpridrucker", inif.getStringProperty("HMPRIRechnung", "Pdrucker"));
		hmAbrechnung.put("hmpriexemplare", inif.getStringProperty("HMPRIRechnung", "Pexemplare"));
		
		hmAbrechnung.put("hmbgeformular", inif.getStringProperty("HMBGERechnung", "Bformular"));
		hmAbrechnung.put("hmbgedrucker", inif.getStringProperty("HMBGERechnung", "Bdrucker"));
		hmAbrechnung.put("hmbgeexemplare", inif.getStringProperty("HMBGERechnung", "Bexemplare"));
		/********Rehaabrechnung********/
		hmAbrechnung.put("rehagkvformular", inif.getStringProperty("RehaGKVRechnung", "RehaGKVformular"));
		hmAbrechnung.put("rehagkvdrucker", inif.getStringProperty("RehaGKVRechnung", "RehaGKVdrucker"));
		hmAbrechnung.put("rehagkvexemplare", inif.getStringProperty("RehaGKVRechnung", "RehaGKVexemplare"));
		hmAbrechnung.put("rehagkvik", inif.getStringProperty("RehaGKVRechnung", "RehaGKVik"));
		
		hmAbrechnung.put("rehadrvformular", inif.getStringProperty("RehaDRVRechnung", "RehaDRVformular"));
		hmAbrechnung.put("rehadrvdrucker", inif.getStringProperty("RehaDRVRechnung", "RehaDRVdrucker"));
		hmAbrechnung.put("rehadrvexemplare", inif.getStringProperty("RehaDRVRechnung", "RehaDRVexemplare"));
		hmAbrechnung.put("rehadrvik", inif.getStringProperty("RehaDRVRechnung", "RehaDRVik"));
		
		hmAbrechnung.put("rehapriformular", inif.getStringProperty("RehaPRIRechnung", "RehaPRIformular"));
		hmAbrechnung.put("rehapridrucker", inif.getStringProperty("RehaPRIRechnung", "RehaPRIdrucker"));
		hmAbrechnung.put("rehapriexemplare", inif.getStringProperty("RehaPRIRechnung", "RehaPRIexemplare"));
		hmAbrechnung.put("rehapriik", inif.getStringProperty("RehaPRIRechnung", "RehaPRIik"));
		
		hmAbrechnung.put("hmallinoffice", inif.getStringProperty("GemeinsameParameter", "InOfficeStarten"));
	}
	
	/***************************/
	public static void FirmenDaten(String proghome){
		String[] stitel = {"Ik","Ikbezeichnung","Firma1","Firma2","Anrede","Nachname","Vorname",
				"Strasse","Plz","Ort","Telefon","Telefax","Email","Internet","Bank","Blz","Kto",
				"Steuernummer","Hrb","Logodatei","Zusatz1","Zusatz2","Zusatz3","Zusatz4","Bundesland"};
		hmFirmenDaten = new HashMap<String,String>();
		INIFile inif = INITool.openIni(proghome+"ini/"+OffenePosten.aktIK+"/", "firmen.ini");
		for(int i = 0; i < stitel.length;i++){
			hmFirmenDaten.put(stitel[i],inif.getStringProperty("Firma",stitel[i] ) );
		}
	}
	
	
	/***************************/
	
    public static void starteOfficeApplication(){ 
    	try {
			officeapplication = (IOfficeApplication)new StartOOApplication(OffenePosten.officeProgrammPfad,OffenePosten.officeNativePfad).start(false);
			 System.out.println("OpenOffice ist gestartet und Active ="+officeapplication.isActive());
		} catch (OfficeApplicationException e1) {
			e1.printStackTrace();
		}
    	
		/*
    	final String OPEN_OFFICE_ORG_PATH = OffenePosten.officeProgrammPfad;
    	String path = OPEN_OFFICE_ORG_PATH;
        try
        {
        	////System.out.println("**********Open-Office wird gestartet***************");
            System.setProperty(IOfficeApplication.NOA_NATIVE_LIB_PATH,OffenePosten.officeNativePfad);
            ILazyApplicationInfo info =  OfficeApplicationRuntime.getApplicationAssistant(OffenePosten.officeNativePfad).findLocalApplicationInfo(path);
            String[] names = info.getProperties().getPropertyNames();
            for(int i = 0; i < names.length;i++){
            	System.out.println(names[i]+" = "+info.getProperties().getPropertyValue(names[i]));
            	if(info.getProperties().getPropertyValue(names[i]).contains("LibreOffice")){
            		OffenePosten.thisClass.isLibreOffice = true;
            	}
            }
            Map <String, Object>config = new HashMap<String, Object>();
            config.put(IOfficeApplication.APPLICATION_HOME_KEY, path);
            config.put(IOfficeApplication.APPLICATION_TYPE_KEY, IOfficeApplication.LOCAL_APPLICATION);
            if(OffenePosten.thisClass.isLibreOffice){
                config.put(IOfficeApplication.APPLICATION_ARGUMENTS_KEY, 
                		new String[] {"--nodefault","--nologo",
                		"--nofirststartwizard",
                		"--nocrashreport",
                		"--norestore"
                		});

            }else{
                config.put(IOfficeApplication.APPLICATION_ARGUMENTS_KEY, 
                		new String[] {"-nodefault","-nologo",
                		"-nofirststartwizard",
                		"-nocrashreport",
                		"-norestore"
                		});
            	
            }
            System.setProperty(IOfficeApplication.NOA_NATIVE_LIB_PATH,path);
            try{
            	officeapplication = OfficeApplicationRuntime.getApplication(config);	
            }catch(NullPointerException ex){
            	ex.printStackTrace();
            }
            officeapplication.activate();
            try{
            	officeapplication.getDesktopService().addTerminateListener(new VetoTerminateListener() {
            	public void queryTermination(ITerminateEvent terminateEvent) {
            		super.queryTermination(terminateEvent);
            	    try {
            	      IDocument[] docs = officeapplication.getDocumentService().getCurrentDocuments();
            	      if (docs.length ==  1  ) { 
              	        docs[0].close();
            	      }
            	    }catch (DocumentException e) {
            	    	e.printStackTrace();
            	    } catch (OfficeApplicationException e) {
						e.printStackTrace();
					}
            	  }
            	});
            }catch(NullPointerException ex){
            	ex.printStackTrace();
            }
            
        }catch (OfficeApplicationException e) {
            e.printStackTrace();
        }
        */
    	
    }
	

}
