package pdftest2;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JApplet;
import javax.swing.JFrame;



import Tools.INIFile;
import Tools.Verschluesseln;







public class Rahmen extends JApplet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2251834036922672353L;
		public static boolean DbOk;
		JFrame jFrame;
		public Connection conn;
		public static Rahmen thisClass;
		public static String reader = null;  
		public static String patid = null;
		
		public String dieseMaschine = null;	
		public static String dbIpAndName = "jdbc:mysql://192.168.2.2:3306/dbf";
		public static String dbUser = "entwickler";
		public static String dbPassword = "entwickler";
		


		public static void main(String[] args) {
			Rahmen application = new Rahmen();
			application.getInstance();
			
			if(args.length > 0){
				System.out.println("hole daten aus INI-Datei "+args[5]);
				INIFile inif = new INIFile(args[5]);
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

			application.starteDB();
			long zeit = System.currentTimeMillis();
			while(! DbOk){
				try {
					Thread.sleep(20);
					if(System.currentTimeMillis()-zeit > 5000){
						System.exit(0);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			INIFile file = new INIFile(args[3]);
			reader = file.getStringProperty("FestProg", "FestProgPfad1");
			patid = args[4];
			//thisClass.jFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			System.out.println(args[0]);
			System.out.println(args[1]);
			System.out.println(args[2]);
			System.out.println(args[3]);
			System.out.println(args[4]);
			System.out.println(args[5]);
			if(args.length < 3){
				System.exit(0);
			}
			if(args[0].equals("90")){
				new LadeProg(args[2]+"\\ASP-Information für Patienten_NoRestriction.pdf");
			}
			if(args[0].equals("91")){
				new LadeProg(args[2]+"\\Patienteninformation für IRENA_NoRestriction.pdf");
			}
			
			if(args[0].equals("1")){
				try {
					new LVAasp(args[1],args[2]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(args[0].equals("2")){
				new ASPAnwesend(args[1],args[2]);
			}
			if(args[0].equals("3")){
				new ZustimmungASP(args[1],args[2]);
			}
			if(args[0].equals("4")){
				new BfAasp(args[1],args[2]);				
		
			}	
			if(args[0].equals("5")){
				new IRENAAnwesend(args[1],args[2]);
			}	
			if(args[0].equals("6")){
				new ZustimmungIRENA(args[1],args[2]);
				
				//WiedereingliederungBfA wiedbfa = new WiedereingliederungBfA(args[1]);
				//Verlaengerung_BFA verlbfa = new Verlaengerung_BFA(args[1]);
		
			}	
			if(args[0].equals("7")){
				new IRENABeginn(args[1],args[2]);
				//WiedereingliederungLVA wiedlva = new WiedereingliederungLVA(args[1]);
		
			}	
			if(args[0].equals("8")){
				new BFARehaVerlaengerung(args[1],args[2]);
				//BfAAufnahmemitteilung bfaaufn = new BfAAufnahmemitteilung(args[1]);
			}	
			if(args[0].equals("9")){
				//new LVAWiederEingliederung(args[1],args[2]);
				new LadeProg(args[2]+"\\WiedereingliederungSammelmappe2.pdf");
			}	
			if(args[0].equals("10")){
				new DRVWiedereingliederungNeu(args[1],args[2]);
				//new BFAWiederEingliederung(args[1],args[2]);
				//RechnungASP rechasp = new RechnungASP(args[1]);
		
			}
			if(args[0].equals("11")){
				new BFAAHBAufnahme(args[1],args[2],patid);
				//REHAbescheinigung rehabesch = new REHAbescheinigung(args[1]);
			}	
			if(args[0].equals("12")){
				new RechnungASP(args[1],args[2]);
				//REHAbescheinigung rehabesch = new REHAbescheinigung(args[1]);
			}	
			if(args[0].equals("13")){
				new RechnungIRENA(args[1],args[2]);
			}
			if(args[0].equals("14")){
				new REHAbescheinigung(args[1],args[2],patid);
			}	
			if(args[0].equals("15")){
				new GKVVerlaengerung(args[1],args[2],patid);
			}
			if(args[0].equals("16")){
				new DRVWiedereingliederungVerspaetet(args[1],args[2]);
			}
			
			
		}
		/******/
		public void starteDB(){
			DatenbankStarten dbstart = new DatenbankStarten();
			dbstart.run(); 			
		}
		public void stoppeDB(){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} 			
		}
		/******/

		
		
		
		
		public Rahmen(){

			

		}
		  public void start()
		   {
		      super.start();
		   }
		  public void stop()
		   {
		      super.stop();

		   }

		
		
		public Rahmen getInstance(){
			thisClass = this;
			return this;
		}

		
		final class DatenbankStarten implements Runnable{
			private void StarteDB(){
				final Rahmen obj = Rahmen.thisClass;

				final String sDB = "SQL";
				if (obj.conn != null){
					try{
					obj.conn.close();}
					catch(final SQLException e){}
				}
				try{
						Class.forName("com.mysql.jdbc.Driver").newInstance();

		    	}
		    	catch ( final Exception e ){
		        		System.out.println(sDB+"Treiberfehler: " + e.getMessage());
		        		Rahmen.DbOk = false;
			    		return ;
		        }	
			        	try {
			        		
			   				obj.conn = (Connection) DriverManager.getConnection(dbIpAndName,dbUser,dbPassword);
		    				//obj.conn = (Connection) DriverManager.getConnection("jdbc:mysql://192.168.2.2:3306/dbf","entwickler","entwickler");
		    				Rahmen.DbOk = true;
			    			System.out.println("Datenbankkontakt hergestellt");
			        	} 
			        	catch (final SQLException ex) {
			        		System.out.println("SQLException: " + ex.getMessage());
			        		System.out.println("SQLState: " + ex.getSQLState());
			        		System.out.println("VendorError: " + ex.getErrorCode());
			        		Rahmen.DbOk = false;
			        
			        	}
			        return;
			}
			public void run() {
				StarteDB();
			}
		
		
		}
		

}
