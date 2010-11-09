package theraPiUpdates;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.sql.Connection;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;







public class TheraPiUpdates implements WindowListener {
	public static Connection conn_root;
	public static Connection conn_db;
	public JFrame jFrame = null;
	public static JFrame thisFrame = null;
	TheraPiUpdates thisClass = null;
	public static String proghome = null;
	public static boolean  seite1Ok = false,seite2Ok = false,seite3Ok = false,seite4Ok = false;
	/*
	public static String UpdateVerzeichnis = "/updates";
	public static String UpdateFTP = "www.thera-pi.org";
	public static String UpdateUser = "u37262724-howto";
	public static String UpdatePasswd = "therapihowto";
	*/
	public static String UpdateVerzeichnis = "";
	public static String UpdateFTP = "";
	public static String UpdateUser = "";
	public static String UpdatePasswd = "";
	public static boolean isDeveloper = false;

	public static boolean testphase = false;
	public static boolean starteTheraPi = false;
	public static void main(String[] args) {
		if(args.length > 0){
			starteTheraPi = true;
		}
		if(testphase){
			proghome = "C:/RehaVerwaltung/";
		}else{
			proghome = java.lang.System.getProperty("user.dir").replace("\\","/")+"/";
		}	
		oeffneIniDatei();
		System.out.println(proghome);
		
		TheraPiUpdates application = new TheraPiUpdates();
		application.getInstance();
		application.getJFrame();
	}	
	
	public TheraPiUpdates getInstance(){
		thisClass = this;
		return this;
	}
	public JFrame getJFrame(){
		try {
			
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			//UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
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
		jFrame = new JFrame();
		jFrame.setUndecorated(true);
		jFrame.addWindowListener(this);
		Dimension ssize = Toolkit.getDefaultToolkit().getScreenSize(); 
		//jFrame.setSize(ssize.width-(ssize.width/4),ssize.height/2);
		jFrame.setTitle("Thera-Pi  Update-Explorer");
		jFrame.setSize(ssize.width*3/4>=800 ? ssize.width*3/4 : 800, ssize.height/2>=600 ? ssize.height/2 : 600);
		//jFrame.setTitle("Thera-Pi  MySql-Konfigurationsassistent");
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setLocationRelativeTo(null);
		if(TheraPiUpdates.isDeveloper){
			jFrame.getContentPane().add (new UpdateTab(thisClass));	
		}else{
			jFrame.getContentPane().add (new UpdatePanel(thisClass,null));	
		}
		jFrame.setVisible(true);

		thisFrame = jFrame;
		return jFrame;
		
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public static void oeffneIniDatei(){
		INIFile ini = new INIFile(proghome+"/ini/tpupdate.ini");
		//System.out.println(proghome+"update.ini");
		UpdateFTP = ini.getStringProperty("TheraPiUpdates", "UpdateFTP");
		UpdateVerzeichnis = ini.getStringProperty("TheraPiUpdates", "UpdateVerzeichnis");
		UpdateUser = ini.getStringProperty("TheraPiUpdates", "UpdateUser");
		String pw = ""; 
		pw = ini.getStringProperty("TheraPiUpdates", "UpdatePasswd");
		Verschluesseln man = Verschluesseln.getInstance();
		man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
		if(pw.length() <= 20){
			ini.setStringProperty("TheraPiUpdates", "UpdatePasswd", man.encrypt(String.valueOf(pw)),null);
			ini.save();
			UpdatePasswd = String.valueOf(pw);
		}else{
			UpdatePasswd = man.decrypt(ini.getStringProperty("TheraPiUpdates", "UpdatePasswd"));
		}
		isDeveloper = (ini.getStringProperty("TheraPiUpdates", "UpdateEntwickler").equals("0") ? false : true);
	}


}
