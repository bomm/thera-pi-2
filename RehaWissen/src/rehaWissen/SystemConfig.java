package rehaWissen;








import java.awt.Color;
import java.awt.Cursor;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;



public class SystemConfig {
	private static INIFile ini; 
	private static INIFile colini;
	public static java.net.InetAddress dieseMaschine = null;
	public static String wissenURL = null;
	public static String homeDir = null;
	public static String homePageURL = null;
	public static int TerminUeberlappung = 1;

	public static Vector<ArrayList<String>>InetSeiten = null;
	public static String HilfeServer = null;
	public static boolean HilfeServerIstDatenServer;
	public static HashMap <String,String> hmHilfeServer;

	
	
	public SystemConfig(){
	
	}

	

	public static void InetSeitenEinlesen(){
		INIFile inif = new INIFile(RehaWissen.proghome+"ini/rehabrowser.ini");
		int seitenanzahl = inif.getIntegerProperty("RehaBrowser", "SeitenAnzahl");
		InetSeiten = new Vector<ArrayList<String>>();
		ArrayList<String> seite = null;
		for(int i = 0; i < seitenanzahl; i++){
			seite = new ArrayList<String>();
			seite.add(inif.getStringProperty("RehaBrowser", "SeitenName"+(i+1)));
			seite.add(inif.getStringProperty("RehaBrowser", "SeitenIcon"+(i+1)));
			seite.add(inif.getStringProperty("RehaBrowser", "SeitenAdresse"+(i+1)));
			InetSeiten.add(seite);
		}
		HilfeServer = inif.getStringProperty("TheraPiHilfe", "HilfeServer");
		HilfeServerIstDatenServer = (inif.getIntegerProperty("TheraPiHilfe", "HilfeDBIstDatenDB") > 0 ? true : false);
		// Wird nicht mehr gebraucht, da MySql von 1&1 fest verdrahtet ist
		/*
		if(! HilfeServerIstDatenServer){
			hmHilfeServer = new HashMap<String,String>();
			hmHilfeServer.put("HilfeDBTreiber", inif.getStringProperty("TheraPiHilfe", "HilfeDBTreiber"));
			hmHilfeServer.put("HilfeDBLogin", inif.getStringProperty("TheraPiHilfe", "HilfeDBLogin"));		
			hmHilfeServer.put("HilfeDBUser", inif.getStringProperty("TheraPiHilfe", "HilfeDBUser"));
			String pw = new String(inif.getStringProperty("TheraPiHilfe","HilfeDBPassword"));
			Verschluesseln man = Verschluesseln.getInstance();
		    man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
			String decrypted = man.decrypt (pw);
			hmHilfeServer.put("HilfeDBPassword", new String(decrypted));			
		}
		try {
			dieseMaschine = java.net.InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}	
 

	
}

/*****************************************/

