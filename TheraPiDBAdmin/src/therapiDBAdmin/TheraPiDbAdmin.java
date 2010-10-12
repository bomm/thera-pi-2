package therapiDBAdmin;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;







public class TheraPiDbAdmin implements WindowListener {
	public static Connection conn_root;
	public static Connection conn_db;
	public JFrame jFrame = null;
	public static JFrame thisFrame = null;
	TheraPiDbAdmin thisClass = null;
	public static String proghome = null;
	public static boolean  seite1Ok = false,seite2Ok = false,seite3Ok = false; 

	
	public static void main(String[] args) {
		proghome = java.lang.System.getProperty("user.dir").replace("\\","/")+"/";
		System.out.println(proghome);		
		TheraPiDbAdmin application = new TheraPiDbAdmin();
		application.getInstance();
		application.getJFrame();
	}	
	
	public TheraPiDbAdmin getInstance(){
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
		jFrame.setSize(ssize.width,ssize.height);
		jFrame.setTitle("Thera-Pi  MySql-Konfigurationsassistent");
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setLocationRelativeTo(null);
		jFrame.getContentPane().add (new MySqlTab());
		jFrame.setVisible(true);

		thisFrame = jFrame;
		return jFrame;
		
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
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
