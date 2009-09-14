package entlassBerichte;

import hauptFenster.Reha;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.InputStream;

import javax.swing.BorderFactory;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXPanel;

import com.sun.star.frame.XController;
import com.sun.star.text.XTextViewCursor;
import com.sun.star.text.XTextViewCursorSupplier;
import com.sun.star.uno.UnoRuntime;

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

public class Eb3 {
	JXPanel pan = null;
	//IFrame officeFrame = null;
	//static ITextDocument document = null;
	EBerichtPanel eltern = null;
	
	public Eb3(EBerichtPanel xeltern){
		eltern = xeltern; 
		pan = new JXPanel();
		pan.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));
		pan.setVisible(true);
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
		
	    final NativeView nativeView = new NativeView(SystemConfig.OpenOfficeNativePfad);
	    if(nativeView == null){
	    	System.out.println("nativeView == null");
	    }
	    if(parent == null){
	    	System.out.println("parent == null");
	    }
	    parent.add(nativeView);
	    parent.addComponentListener(new ComponentAdapter(){
	        public void componentResized(ComponentEvent e) {
	          nativeView.setPreferredSize(new Dimension(parent.getWidth(),parent.getHeight()-5));
	          parent.getLayout().layoutContainer(parent);
	        }      
	      });
	    nativeView.setPreferredSize(new Dimension(parent.getWidth(), parent.getHeight()-5));
	    parent.getLayout().layoutContainer(parent);
	    IFrame officeFrame = officeApplication.getDesktopService().constructNewOfficeFrame(nativeView);
	    parent.validate();
    return officeFrame;
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
	
	

}
