package org.thera_pi.updates;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class TheraPiUpdates implements WindowListener {
	private JFrame jFrame = null;
	public static boolean starteTheraPi = false;
	
	public static void main(String[] args) {
		if(args.length > 0){
			starteTheraPi = true;
		}

		if(UpdateConfig.getInstance().isUseActiveMode())
		{
			System.out.println("FTP-Modus = ActiveMode");
		}else{
			System.out.println("FTP-Modus = PassiveMode");
		}

		System.out.println("program home: " + UpdateConfig.getProghome());
		
		TheraPiUpdates application = new TheraPiUpdates();
		application.createJFrame();
	}	
	
	public JFrame createJFrame(){
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
		if(UpdateConfig.getInstance().isDeveloperMode()){
			jFrame.getContentPane().add (new UpdateTab(this,jFrame));	
		}else{
			jFrame.getContentPane().add (new UpdatePanel(this,jFrame, null));	
		}
		jFrame.pack();
		jFrame.setVisible(true);

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
