package CommonTools;

import java.awt.Cursor;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import ag.ion.bion.officelayer.application.ILazyApplicationInfo;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.application.OfficeApplicationRuntime;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.event.ITerminateEvent;
import ag.ion.bion.officelayer.event.VetoTerminateListener;

public class StartOOApplication {
	static String ooPath,libPath;
	boolean isLibreOffice;
	public IOfficeApplication officeapplication = null; 
		

	public StartOOApplication(String oopath,String libpath){
		ooPath = oopath;
		libPath = libpath; 
	}
	
    public IOfficeApplication start(boolean mustRestart) throws OfficeApplicationException{ 
        	if(mustRestart){
        		JOptionPane.showMessageDialog(null,"Zur Info die UNO-Runtime wird neu gestartet!");
        	}
        	File file = new File(ooPath);
        	if(! file.exists()){
        		JOptionPane.showMessageDialog(null, "Der eingestellte OpenOffice-Pfad zeigt auf "+ooPath+"\nDieser Pfad existiert nicht auf Ihrem Rechner!\nDie Anwendung kann nicht korrekt gestartet werden");
        	}
            ILazyApplicationInfo info =  OfficeApplicationRuntime.getApplicationAssistant(libPath).findLocalApplicationInfo(ooPath);
            String[] names = info.getProperties().getPropertyNames();
            for(int i = 0; i < names.length;i++){
            	System.out.println(names[i]+" = "+info.getProperties().getPropertyValue(names[i]));
            	if(info.getProperties().getPropertyValue(names[i]).contains("LibreOffice")){
            		isLibreOffice = true;
            	}
            }
            Map <String, Object>config = new HashMap<String, Object>();
            config.put(IOfficeApplication.APPLICATION_HOME_KEY, ooPath);
            config.put(IOfficeApplication.APPLICATION_TYPE_KEY, IOfficeApplication.LOCAL_APPLICATION);
            if(isLibreOffice){
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
            System.setProperty(IOfficeApplication.NOA_NATIVE_LIB_PATH,libPath);
            try{
            	officeapplication = OfficeApplicationRuntime.getApplication(config);	
            }catch(NullPointerException ex){
            	ex.printStackTrace();
            }
            officeapplication.activate();
            	officeapplication.getDesktopService().addTerminateListener(new VetoTerminateListener() {
            	public void queryTermination(ITerminateEvent terminateEvent) {
            		super.queryTermination(terminateEvent);
            		IDocument[] docs = null;;
					try {
						docs = officeapplication.getDocumentService().getCurrentDocuments();
					} catch (DocumentException e) {
						e.printStackTrace();
					} catch (OfficeApplicationException e) {
						e.printStackTrace();
					}
					if (docs.length ==  1  ) { 
						docs[0].close();
					}
            	  }
            	});
			return officeapplication;
    }	
}
