package org.thera_pi.updates;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;







public class TheraPiUpdates implements WindowListener {
	public static Connection conn_root;
	public static Connection conn_db;
	public JFrame jFrame = null;
	public static JFrame thisFrame = null;
	TheraPiUpdates thisClass = null;
	public static boolean  seite1Ok = false,seite2Ok = false,seite3Ok = false,seite4Ok = false;
	/*
	public static String UpdateVerzeichnis = "/updates";
	public static String UpdateFTP = "www.thera-pi.org";
	public static String UpdateUser = "u37262724-howto";
	public static String UpdatePasswd = "therapihowto";
	*/
	public static boolean isDeveloper = false;
	public static boolean starteTheraPi = false;
	
	private static UpdateConfig updateConfig;

	public static void main(String[] args) {
		if(args.length > 0){
			starteTheraPi = true;
		}

		updateConfig = UpdateConfig.getInstance();
		
		if(updateConfig.isUseActiveMode())
		{
			System.out.println("FTP-Modus = ActiveMode");
		}else{
			System.out.println("FTP-Modus = PassiveMode");
		}

		System.out.println("program home: " + UpdateConfig.getProghome());
		
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
		jFrame.setPreferredSize(new Dimension(ssize.width*3/4>=800 ? ssize.width*3/4 : 800, ssize.height/2>=600 ? ssize.height/2 : 600));
		//jFrame.setTitle("Thera-Pi  MySql-Konfigurationsassistent");
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setLocationRelativeTo(null);
		if(TheraPiUpdates.isDeveloper){
			jFrame.getContentPane().add (new UpdateTab(thisClass));	
		}else{
			jFrame.getContentPane().add (new UpdatePanel(thisClass,null));	
		}
		jFrame.pack();
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

}
