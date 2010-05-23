package menus;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;

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
		fillNOAPanel();
		add(noaPanel,BorderLayout.CENTER);
		
		validate();
		
		
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
		    
		    if(nativeView == null){
		    	//System.out.println("nativeView == null");
		    }
		    if(parent == null){
		    	//System.out.println("parent == null");
		    }
		    parent.add(nativeView);
		    
		    parent.addContainerListener(new ContainerAdapter(){
		    	public void componentAdded(ContainerEvent e) {
		    		//System.out.println(" added to "+e);
		    	    }
		    	    public void componentRemoved(ContainerEvent e) {
		    		//System.out.println(" removed from "+e);
		    	    }
		    });
		    parent.addComponentListener(new ComponentAdapter(){
		        public void componentResized(ComponentEvent e) {
		        //System.out.println(e.getComponent().getClass().getName() + " -------- ResizeEvent");
		          nativeView.setPreferredSize(new Dimension(parent.getWidth(),parent.getHeight()-5));
		          parent.getLayout().layoutContainer(parent);
		          parent.repaint();
		        }  
		        public void componentHidden(ComponentEvent e) {
		            //System.out.println(e.getComponent().getClass().getName() + " --- Hidden");
		        }

		        public void componentMoved(ComponentEvent e) {
		        	//System.out.println(e.getComponent().getClass().getName() + " --- Moved");
		        }
		        public void componentShown(ComponentEvent e) {
		        	//System.out.println(e.getComponent().getClass().getName() + " --- Shown");
		            nativeView.setPreferredSize(new Dimension(parent.getWidth(),parent.getHeight()-5));
			        parent.getLayout().layoutContainer(parent);
			        parent.setVisible(true);

		        }
		        
		      });
		    nativeView.setPreferredSize(new Dimension(parent.getWidth(), parent.getHeight()-5));
		    parent.getLayout().layoutContainer(parent);
		    IFrame officeFrame = officeApplication.getDesktopService().constructNewOfficeFrame(nativeView);
		    parent.validate();
		    //System.out.println("natveView eingeh�ngt in Panel "+parent.getName());
	    return officeFrame;		  
		}
		  


}
