package menus;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXPanel;

import systemEinstellungen.SystemConfig;

import ag.ion.bion.officelayer.NativeView;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.text.ITextDocument;

public class OOIFTest extends JXPanel{
	JPanel noaPanel = null;
	private IFrame             officeFrame       = null;
  	private ITextDocument      document          = null;

	
	public OOIFTest(){
		super();
		setLayout(new BorderLayout());
		noaPanel = new JPanel();
		add(noaPanel,BorderLayout.CENTER);
		
		validate();
		
		fillNOAPanel();
	}
	  private void fillNOAPanel() {
		    if (noaPanel != null) {
		      try {
		         officeFrame = constructOOOFrame(Reha.officeapplication, noaPanel);
		        document = (ITextDocument) Reha.officeapplication.getDocumentService().constructNewDocument(officeFrame,
		            IDocument.WRITER,
		            DocumentDescriptor.DEFAULT);

		        noaPanel.setVisible(true);
		      }
		      catch (Throwable throwable) {
		        noaPanel.add(new JLabel("An error occured while creating the NOA panel: " + throwable.getMessage()));
		      }
		    }
	}
  private IFrame constructOOOFrame(IOfficeApplication officeApplication, final Container parent)
      throws Throwable {
    final NativeView nativeView = new NativeView(SystemConfig.OpenOfficeNativePfad);
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
		  

		  


}
