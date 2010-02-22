package nebraska;



import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationRuntime;


public class Nebraska {
	public final static String cakeystorefile = "";
	public final static String keystorefile = "";
	public final static String keystoredir = Constants.KEYSTORE_DIR;
	public final static String OpenOfficePfad = Constants.OPENOFFICE_HOME;
	public final static String OfficeNativePfad = Constants.OPENOFFICE_JARS;  
	public static IOfficeApplication officeapplication;
	public static HashMap<String,String> hmZertifikat = new HashMap<String,String>();
	public static JFrame jf;
	
	public static void main(String[] args) throws Exception{
		if(System.getProperty("os.name").contains("Windows")){
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");			
		}
		//UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
		new Constants();
		jf = new JFrame();
		jf.setPreferredSize(new Dimension(1000,600));
		jf.setTitle("Nebraska");
		jf.setLocation(200,200);
		//jf.setContentPane(new NebraskaPanel());
		jf.setContentPane(new NebraskaTestPanel());
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.pack();
		jf.setVisible(true);
		starteOfficeApplication();
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
