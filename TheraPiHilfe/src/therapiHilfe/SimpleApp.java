package therapiHilfe;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ag.ion.bion.officelayer.NativeView;
import ag.ion.bion.officelayer.application.IApplicationAssistant;
import ag.ion.bion.officelayer.application.ILazyApplicationInfo;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.application.OfficeApplicationRuntime;
import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.internal.application.ApplicationAssistant;
import ag.ion.bion.officelayer.text.ITextDocument;

public class SimpleApp extends JFrame {

  private IOfficeApplication officeApplication = null;
  private IFrame             officeFrame       = null;
  private ITextDocument      document          = null;
  private JPanel             noaPanel          = null;

  public SimpleApp() {
    super(SimpleApp.class.getName());
    getContentPane().setLayout(new GridLayout());
    noaPanel = new JPanel();
    getContentPane().add(noaPanel);
    setSize(500, 500);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
    fillNOAPanel();
    
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent windowEvent) {
        try {
          if (document != null)
            document.close();
          document = null;
          if (officeApplication != null) {
            officeApplication.deactivate();
            //officeApplication.dispose();
            officeApplication = null;
          }
        }
        catch (OfficeApplicationException applicationException) {
          //do not consume
        }
      }
    });
  }

  private void fillNOAPanel() {
    if (noaPanel != null) {
      try {
        if (officeApplication == null)
          officeApplication = startOOO();
        officeFrame = constructOOOFrame(officeApplication, noaPanel);
        document = (ITextDocument) officeApplication.getDocumentService().constructNewDocument(officeFrame,
            IDocument.WRITER,
            DocumentDescriptor.DEFAULT);

        noaPanel.setVisible(true);
      }
      catch (Throwable throwable) {
        noaPanel.add(new JLabel("An error occured while creating the NOA panel: " + throwable.getMessage()));
      }
    }
  }

  private IOfficeApplication startOOO() throws Throwable {
	  IApplicationAssistant applicationAssistant = new ApplicationAssistant("C:\\RehaVerwaltung\\RTAJars\\openofficeorg");
	  //IApplicationAssistant applicationAssistant = new ApplicationAssistant(System.getProperty("user.dir") + "\\lib");
    ILazyApplicationInfo[] appInfos = applicationAssistant.getLocalApplications();
    for(int i = 0; i < appInfos.length;i++){
    	//System.out.println(appInfos[i]);
    }

    if (appInfos.length < 1)
      throw new Throwable("No OpenOffice.org Application found.");
    HashMap configuration = new HashMap();
    configuration.put(IOfficeApplication.APPLICATION_HOME_KEY, appInfos[0].getHome());
    configuration.put(IOfficeApplication.APPLICATION_TYPE_KEY, IOfficeApplication.LOCAL_APPLICATION);
    IOfficeApplication officeAplication = OfficeApplicationRuntime.getApplication(configuration);

    officeAplication.setConfiguration(configuration);
    officeAplication.activate();
    return officeAplication;
  }

  private IFrame constructOOOFrame(IOfficeApplication officeApplication, final Container parent)
      throws Throwable {
    final NativeView nativeView = new NativeView("C:\\RehaVerwaltung\\RTAJars\\openofficeorg");
    parent.add(nativeView);
    parent.addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        nativeView.setPreferredSize(new Dimension(parent.getWidth() - 5, parent.getHeight() - 5));
        parent.getLayout().layoutContainer(parent);
      }
    });
    nativeView.setPreferredSize(new Dimension(parent.getWidth() - 5, parent.getHeight() - 5));
    parent.getLayout().layoutContainer(parent);
    IFrame officeFrame = officeApplication.getDesktopService().constructNewOfficeFrame(nativeView);
    parent.validate();
    return officeFrame;
  }

  public static void main(String[] argv) {
    new SimpleApp();
  }
}
