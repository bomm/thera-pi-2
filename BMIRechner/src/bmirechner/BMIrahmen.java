package bmirechner;

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;




public class BMIrahmen extends JApplet implements WindowListener{

	public static BMIrahmen thisClass;
	public static boolean DbOk;
	JFrame jFrame;
	public Connection conn;
	
	
	   

	public static void main(String[] args) {
		//System.out.println(" Name des Betriebssystems: "+System.getProperty("os.name"));
		//System.out.println("      Benutzerverzeichnis: "+java.lang.System.getProperty("user.dir").replaceAll("\\\\","/"));
		//System.out.println("Installierte Java-Version: "+java.lang.System.getProperty("java.version"));
		//System.out.println("         Java-Verzeichnis: "+java.lang.System.getProperty("java.home").replaceAll("\\\\","/"));
		
		BMIrahmen frm = new BMIrahmen();
		frm.getJFrame();
		

	}


	
	public JFrame getJFrame(){
		try {
			UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//thisClass = this;
		
		jFrame = new JFrame();
		jFrame.setSize(350,450);
		jFrame.setPreferredSize(new Dimension(350,450));
		jFrame.setTitle("BMI-Rechner");
		jFrame.setLocationRelativeTo(null);
		jFrame.setContentPane(new BMIoberflaeche());// .add (jpan);
		thisClass = this;
		
		jFrame.pack();
		jFrame.setVisible(true);
		return jFrame;
	}
	
	
	
	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		if(BMIrahmen.thisClass.conn != null){
			try {
				BMIrahmen.thisClass.conn.close();
				System.out.println("Datenbankverbindung wurde geschlossen");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.exit(0);
	}

	
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



	@Override
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}

