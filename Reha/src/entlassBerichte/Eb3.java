package entlassBerichte;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.beans.PropertyVetoException;
import java.io.InputStream;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXPanel;

import com.sun.star.frame.XController;
import com.sun.star.text.XTextViewCursor;
import com.sun.star.text.XTextViewCursorSupplier;
import com.sun.star.uno.UnoRuntime;

import events.RehaEvent;
import events.RehaEventClass;
import events.RehaEventListener;

import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;

import ag.ion.bion.officelayer.NativeView;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.desktop.GlobalCommands;
import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.filter.RTFFilter;
import ag.ion.bion.officelayer.internal.text.TextRange;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextRange;
import ag.ion.bion.officelayer.text.IViewCursor;
import ag.ion.noa.frame.ILayoutManager;

public class Eb3 implements RehaEventListener  {
	RehaEventClass rEvent = null;
	JXPanel pan = null;
	JXPanel parken = null;
	JPanel oopan = null;
	//IFrame officeFrame = null;
	//static ITextDocument document = null;
	EBerichtPanel eltern = null;
	Container xparent = null;
	NativeView nativeView = null; 
	
	public Eb3(EBerichtPanel xeltern){
		eltern = xeltern; 
		rEvent = new RehaEventClass();
		rEvent.addRehaEventListener((RehaEventListener) this);
		pan = new JXPanel(new BorderLayout());
		pan.setDoubleBuffered(true);
		pan.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));
		pan.setVisible(true);
		pan.setName("ooNativePanel");
		//parken = new JXPanel();
		//oopan = new JPanel(new BorderLayout());
		
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try {
					eltern.officeFrame = constructOOOFrame(Reha.officeapplication,pan);
					configureOOOFrame(Reha.officeapplication,eltern.officeFrame);
		        	DocumentDescriptor d = new DocumentDescriptor();
		        	//d.setFilterDefinition(RTFFilter.FILTER.getFilterDefinition(IDocument.WRITER)); 
		        	d.setTitle("Entlassbericht");
		        	if(eltern.neu){
			        	eltern.document = (ITextDocument) Reha.officeapplication.getDocumentService().constructNewDocument(eltern.officeFrame,IDocument.WRITER,d);		        		
		        	}else{
		        		InputStream is = SqlInfo.holeStream("bericht2","freitext","berichtid='"+eltern.berichtid+"'");
			        	eltern.document = (ITextDocument) Reha.officeapplication.getDocumentService().constructNewDocument(eltern.officeFrame,IDocument.WRITER,d);
			        	d.setFilterDefinition(RTFFilter.FILTER.getFilterDefinition(IDocument.WRITER));
			        	eltern.document.getViewCursorService().getViewCursor().getTextCursorFromStart().insertDocument(is, new RTFFilter());
						XController xController = eltern.document.getXTextDocument().getCurrentController();
						XTextViewCursorSupplier xTextViewCursorSupplier = (XTextViewCursorSupplier) UnoRuntime.queryInterface(XTextViewCursorSupplier.class,
						xController);
						XTextViewCursor xtvc = xTextViewCursorSupplier.getViewCursor();
						xtvc.gotoStart(false);
						is.close();
		        	}
		        	
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		}.execute();

		//pan = new JXPanel();
	}
	public JXPanel getSeite(){
		return pan;
	}

	private IFrame constructOOOFrame(IOfficeApplication officeApplication, final Container parent)
      throws Throwable {
		
		//final NativeView nativeView = new NativeView(SystemConfig.OpenOfficeNativePfad);
	    nativeView = new NativeView(SystemConfig.OpenOfficeNativePfad);
	    if(nativeView == null){
	    	System.out.println("nativeView == null");
	    }
	    if(parent == null){
	    	System.out.println("parent == null");
	    }
	    parent.add(nativeView);
	    
	    parent.addContainerListener(new ContainerAdapter(){
	    	public void componentAdded(ContainerEvent e) {
	    		System.out.println(" added to "+e);
	    	    }
	    	    public void componentRemoved(ContainerEvent e) {
	    		System.out.println(" removed from "+e);
	    	    }
	    });
	    parent.addComponentListener(new ComponentAdapter(){
	        public void componentResized(ComponentEvent e) {
	        System.out.println(e.getComponent().getClass().getName() + " -------- ResizeEvent");
	          nativeView.setPreferredSize(new Dimension(parent.getWidth(),parent.getHeight()-5));
	          parent.getLayout().layoutContainer(parent);
	          parent.repaint();
	        }  
	        public void componentHidden(ComponentEvent e) {
	            System.out.println(e.getComponent().getClass().getName() + " --- Hidden");
	        }

	        public void componentMoved(ComponentEvent e) {
	        	System.out.println(e.getComponent().getClass().getName() + " --- Moved");
	        }
	        public void componentShown(ComponentEvent e) {
	        	System.out.println(e.getComponent().getClass().getName() + " --- Shown");
	            nativeView.setPreferredSize(new Dimension(parent.getWidth(),parent.getHeight()-5));
		        parent.getLayout().layoutContainer(parent);
		        parent.setVisible(true);

	        }
	        
	      });
	    nativeView.setPreferredSize(new Dimension(parent.getWidth(), parent.getHeight()-5));
	    parent.getLayout().layoutContainer(parent);
	    IFrame officeFrame = officeApplication.getDesktopService().constructNewOfficeFrame(nativeView);
	    parent.validate();
	    System.out.println("natveView eingehängt in Panel "+parent.getName());
    return officeFrame;
  }
	public void neuAnhaengen(){
		System.out.println("Neu eingehängt----->");
		//pan.add(nativeView);
		//pan.getLayout().layoutContainer(pan);
		pan.setSize(new Dimension(pan.getSize().width,pan.getSize().height));
		pan.revalidate();
		pan.setVisible(true);
	}
	public void trenneFrame(){
		System.out.println("ntiveView getrennt----->");
		//pan.remove(nativeView);
	}
	
	  public static void configureOOOFrame(IOfficeApplication officeApplication, IFrame officeFrame) throws Throwable {
		    ILayoutManager layoutManager = officeFrame.getLayoutManager();
		    layoutManager.hideAll();
		    layoutManager.showElement(ILayoutManager.URL_TOOLBAR_STANDARDBAR);
		    layoutManager.showElement(ILayoutManager.URL_TOOLBAR_TEXTOBJECTBAR);
		    layoutManager.showElement(ILayoutManager.URL_STATUSBAR);
		    
		    officeFrame.disableDispatch(GlobalCommands.CLOSE_DOCUMENT);
		    officeFrame.disableDispatch(GlobalCommands.CLOSE_WINDOW);
		    officeFrame.disableDispatch(GlobalCommands.QUIT_APPLICATION);
		    officeFrame.disableDispatch(GlobalCommands.NEW_MENU);
		    officeFrame.disableDispatch(GlobalCommands.NEW_DOCUMENT);
		    officeFrame.disableDispatch(GlobalCommands.OPEN_DOCUMENT);
		    officeFrame.disableDispatch(GlobalCommands.EDIT_DOCUMENT);
		    officeFrame.disableDispatch(GlobalCommands.DIREKT_EXPORT_DOCUMENT);
		    officeFrame.disableDispatch(GlobalCommands.MAIL_DOCUMENT);
		    officeFrame.disableDispatch(GlobalCommands.OPEN_HYPERLINK_DIALOG);
		    officeFrame.disableDispatch(GlobalCommands.EDIT_HYPERLINK);
		    officeFrame.disableDispatch(GlobalCommands.OPEN_DRAW_TOOLBAR);
		    officeFrame.disableDispatch(GlobalCommands.OPEN_NAVIGATOR);
		    officeFrame.disableDispatch(GlobalCommands.OPEN_GALLERY);
		    officeFrame.disableDispatch(GlobalCommands.OPEN_DATASOURCES);
		    officeFrame.disableDispatch(GlobalCommands.OPEN_STYLE_SHEET);
		    officeFrame.disableDispatch(GlobalCommands.OPEN_HELP);
		    //officeFrame.disableDispatch(GlobalCommands.PRINT_PREVIEW);
		    
		    
		    officeFrame.updateDispatches();
		    
		    //officeFrame.getDispatch(".uno:PrintLayout").dispatch();
		  }
	  
		@Override
		public void RehaEventOccurred(RehaEvent evt) {
			if(evt.getRehaEvent().equals("REHAINTERNAL")){
				if(evt.getDetails()[1].equals("#DEICONIFIED") && evt.getDetails()[0].contains("Gutachten")){
					this.neuAnhaengen();
				}
			}
			if(evt.getDetails()[0].contains("GutachtenFenster")){
				if(evt.getDetails()[1].equals("#SCHLIESSEN")){
					System.out.println("Lösche Listener von Eb3-------------->");
					this.rEvent.removeRehaEventListener((RehaEventListener)this);
				}
			}
			if(evt.getRehaEvent().equals("OOFrame")){
				if(evt.getDetails()[1].equals("#TRENNEN") ){
					this.trenneFrame();
				}
			}			
			

		}		
	

}
