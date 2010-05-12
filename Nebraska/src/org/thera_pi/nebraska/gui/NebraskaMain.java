package org.thera_pi.nebraska.gui;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationRuntime;

import nebraska.Constants;

public class NebraskaMain {
	public static IOfficeApplication officeapplication;
	public static HashMap<String,String> hmZertifikat = new HashMap<String,String>();
	public static JFrame jf;
	public static NebraskaJTabbedPaneOrganizer jtbo;
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		if(System.getProperty("os.name").contains("Windows")){
			try {
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
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
		}
		NebraskaMain nebMain = new NebraskaMain();
		nebMain.getFrame();

	}
	private JFrame getFrame() throws Exception{
		jf = new JFrame();
		jf.setPreferredSize(new Dimension(1000,700));
		jf.setTitle("Nebraska");
		jf.setLocation(200,200);
		jtbo = new NebraskaJTabbedPaneOrganizer(); 
		jf.setContentPane(jtbo);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.pack();
		jf.setVisible(true);
		starteOfficeApplication();	
		new Constants();
		return jf;
	}
    public static void starteOfficeApplication () throws Exception {
        new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
		    	final String OPEN_OFFICE_ORG_PATH = Constants.OPENOFFICE_HOME;
		    	final String OPEN_OFFICE_ORG_NATIVE_PATH = Constants.OPENOFFICE_JARS;
	            Map <String, String>config = new HashMap<String, String>();
	            config.put(IOfficeApplication.APPLICATION_HOME_KEY,  OPEN_OFFICE_ORG_PATH);
	            config.put(IOfficeApplication.APPLICATION_TYPE_KEY, IOfficeApplication.LOCAL_APPLICATION);
	            System.setProperty(IOfficeApplication.NOA_NATIVE_LIB_PATH,OPEN_OFFICE_ORG_NATIVE_PATH);
	            officeapplication = OfficeApplicationRuntime.getApplication(config);
	            officeapplication.activate();
	            System.out.println("Open-Office wurde gestartet");
	            System.out.println("Open-Office-Typ: "+officeapplication.getApplicationType());
				return null;
			}
        }.execute();
    }
	

}
