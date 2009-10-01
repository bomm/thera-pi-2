package entlassBerichte;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.beans.PropertyVetoException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import oOorgTools.OOTools;

import org.jdesktop.swingx.JXPanel;

import com.mysql.jdbc.PreparedStatement;

import com.sun.star.beans.Property;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XNameContainer;
import com.sun.star.frame.XController;
import com.sun.star.style.XStyle;
import com.sun.star.style.XStyleFamiliesSupplier;
import com.sun.star.text.XTextDocument;
import com.sun.star.text.XTextViewCursor;
import com.sun.star.text.XTextViewCursorSupplier;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.view.XLineCursor;

import events.RehaEvent;
import events.RehaEventClass;
import events.RehaEventListener;

import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemTools.FileTools;

import ag.ion.bion.officelayer.NativeView;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.desktop.GlobalCommands;
import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.filter.PDFFilter;
import ag.ion.bion.officelayer.filter.RTFFilter;
import ag.ion.bion.officelayer.internal.text.TextRange;
import ag.ion.bion.officelayer.text.ITextCursor;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextRange;
import ag.ion.bion.officelayer.text.IViewCursor;
import ag.ion.noa.NOAException;
import ag.ion.noa.frame.ILayoutManager;

public class Eb3 implements RehaEventListener  {
	RehaEventClass rEvent = null;
	JXPanel pan = null;
	//Panel pan = null;
	JXPanel parken = null;
	JPanel oopan = null;
	//IFrame officeFrame = null;
	//static ITextDocument document = null;
	EBerichtPanel eltern = null;
	Container xparent = null;
	NativeView nativeView = null; 
	ByteArrayOutputStream outtemp = null;	
	public String tempPfad = Reha.proghome+"temp/"+Reha.aktIK+"/";
	boolean gestartet = false;
	public Eb3(EBerichtPanel xeltern){
		eltern = xeltern; 
		rEvent = new RehaEventClass();
		rEvent.addRehaEventListener((RehaEventListener) this);
		pan = new JXPanel(new BorderLayout());
		//pan = new Panel(new BorderLayout());
		pan.setDoubleBuffered(true);
		pan.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));
		pan.setVisible(true);
		pan.setName("ooNativePanel");
		//parken = new JXPanel();
		//oopan = new JPanel(new BorderLayout());
		//baueSeite();
		/*
		new Thread(){
		public void run(){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try {
					Thread.sleep(30);
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
		}
		}.start();
		*/
		//pan = new JXPanel();
	}
	public void beendeSeite(){
		eltern.document.close();
	}
	public void baueSeite(){
		new Thread(){
			public void run(){
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					try {
						eltern.setCursor(new Cursor(Cursor.WAIT_CURSOR));
						eltern.officeFrame = constructOOOFrame(Reha.officeapplication,pan);
						configureOOOFrame(Reha.officeapplication,eltern.officeFrame);
			        	DocumentDescriptor d = new DocumentDescriptor();
			        	//d.setFilterDefinition(RTFFilter.FILTER.getFilterDefinition(IDocument.WRITER)); 
			        	d.setTitle("Entlassbericht");
			        	if(eltern.neu){
				        	eltern.document = (ITextDocument) Reha.officeapplication.getDocumentService().constructNewDocument(eltern.officeFrame,IDocument.WRITER,d);
							eltern.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
							Reha.thisClass.progressStarten(false);
			        	}else{
			        		if(!gestartet){
				        		InputStream is = SqlInfo.holeStream("bericht2","freitext","berichtid='"+eltern.berichtid+"'");
			        			DocumentDescriptor descript = new DocumentDescriptor();
			        			descript.setFilterDefinition(RTFFilter.FILTER.getFilterDefinition(IDocument.WRITER));
			        			descript.setHidden(true);
			        			eltern.document = (ITextDocument) Reha.officeapplication.getDocumentService().loadDocument(eltern.officeFrame,is, descript);
			        			/*
					        	eltern.document = (ITextDocument) Reha.officeapplication.getDocumentService().constructNewDocument(eltern.officeFrame,IDocument.WRITER,d);
					        	d.setFilterDefinition(RTFFilter.FILTER.getFilterDefinition(IDocument.WRITER));
					        	eltern.document.getViewCursorService().getViewCursor().getTextCursorFromStart().insertDocument(is, new RTFFilter());
					        	*/
								XController xController = eltern.document.getXTextDocument().getCurrentController();
								XTextViewCursorSupplier xTextViewCursorSupplier = (XTextViewCursorSupplier) UnoRuntime.queryInterface(XTextViewCursorSupplier.class,
								xController);
								XTextViewCursor xtvc = xTextViewCursorSupplier.getViewCursor();
								xtvc.gotoStart(false);
								is.close();
								eltern.document.getFrame().getXFrame().getContainerWindow().setVisible(true);

								eltern.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
								Reha.thisClass.progressStarten(false);
			        			
			        		}else{
			        			InputStream ins = new ByteArrayInputStream(outtemp.toByteArray());
			        			DocumentDescriptor descript = new DocumentDescriptor();
			        			descript.setFilterDefinition(RTFFilter.FILTER.getFilterDefinition(IDocument.WRITER)); 
			        			descript.setHidden(true);
			        			eltern.document = (ITextDocument) Reha.officeapplication.getDocumentService().loadDocument(eltern.officeFrame,ins, descript);
			        			/*
			        			eltern.document = (ITextDocument) Reha.officeapplication.getDocumentService().constructNewDocument(eltern.officeFrame,IDocument.WRITER,d);
					        	d.setFilterDefinition(RTFFilter.FILTER.getFilterDefinition(IDocument.WRITER));
					        	eltern.document.getViewCursorService().getViewCursor().getTextCursorFromStart().insertDocument(ins, new RTFFilter());
					        	*/
								XController xController = eltern.document.getXTextDocument().getCurrentController();
								XTextViewCursorSupplier xTextViewCursorSupplier = (XTextViewCursorSupplier) UnoRuntime.queryInterface(XTextViewCursorSupplier.class,
								xController);
								XTextViewCursor xtvc = xTextViewCursorSupplier.getViewCursor();
								xtvc.gotoStart(false);
								ins.close();
								eltern.document.getFrame().getXFrame().getContainerWindow().setVisible(true);
								eltern.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
								Reha.thisClass.progressStarten(false);
			        		}
			        	}
			        	OOTools.setzePapierFormat(eltern.document, new Integer(25199), new Integer(19299));
			        	OOTools.setzeRaender(eltern.document, new Integer(1000), new Integer(1000),new Integer(1000),new Integer(1000));

			        	
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
				}
			}.execute();
			}
			}.start();
		
	}
	public void speichernSeite(){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
					trenneFrame();
					eltern.document.close();
					Reha.thisClass.progressStarten(false);
				return null;
			}
			
		}.execute();
		
		
	}
	/*
	public Panel getSeite(){
		return pan;
	}
	*/
	public JXPanel getSeite(){
		return pan;
	}


	private IFrame constructOOOFrame(IOfficeApplication officeApplication, final Container parent)
      throws Throwable {
		
		//final NativeView nativeView = new NativeView(SystemConfig.OpenOfficeNativePfad);
	    nativeView = new NativeView(SystemConfig.OpenOfficeNativePfad);
	    
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
	        	//System.out.println(e.getComponent().getClass().getName() + " -------- ResizeEvent im ComponentListener");
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
	    eltern.officeFrame = officeApplication.getDesktopService().constructNewOfficeFrame(nativeView);
	    parent.validate();
	    //System.out.println("natveView eingehängt in Panel "+parent.getName());
    return eltern.officeFrame;
  }
	public void neuAnhaengen(){
		try{
			SwingUtilities.invokeLater(new Runnable(){
			 	   public  void run()
			 	   {
			 		   try {
			 			Reha.thisClass.progressStarten(true); 
			 			pan.add(nativeView);
			 			EBerichtPanel.document.getFrame().setFocus();
			 			EBerichtPanel.document.getFrame().updateDispatches();
						Thread.sleep(100);
						pan.setVisible(false);
						//System.out.println("Neu eingehängt----->");
						
						nativeView.setVisible(true);
						pan.getLayout().layoutContainer(pan);
						pan.setVisible(true);
						pan.validate();
						Reha.thisClass.progressStarten(false);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
								 		   
			 	   }
			});	
	
		/*
		System.out.println("Aktuelle Größe von pan in X-Richtung:"+ pan.getSize().width+" / Y-Richtung:"+pan.getSize().height);
		pan.setSize(new Dimension(pan.getSize().width-1,pan.getSize().height));
		nativeView.setVisible(true);
		
		System.out.println("Aktuelle Größe von nativeView in X-Richtung:"+ nativeView.getSize().width+" / Y-Richtung:"+nativeView.getSize().height);
		System.out.println("Aktuelle Position von nativeView  = "+nativeView.getLocation());
		System.out.println("Aktuelle Position von nativeView auf dem Bildschirm = "+nativeView.getLocationOnScreen());
		
		
		pan.revalidate();
		pan.setVisible(true);
		*/
		}catch(Exception ex){
			ex.printStackTrace();
		}

	}
	public void trenneFrame(){
		System.out.println("ntiveView getrennt----->");
		if(eltern.neu){
			EBerichtPanel.document.close();
			pan.remove(nativeView);
			Reha.thisClass.progressStarten(false);
			return;
		}
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				outtemp = new ByteArrayOutputStream();
				tempTextSpeichern();
				EBerichtPanel.document.close();
				pan.remove(nativeView);
				return null;
			}
			
		}.execute();
	}
	public void tempTextSpeichern(){
		String url = tempPfad+"EBfliesstext.pdf";
		System.out.println("speichere temporär in: "+url);
		try {
			if(EBerichtPanel.document == null){return;}
			if(EBerichtPanel.document.isOpen()){
				outtemp = new ByteArrayOutputStream();
				EBerichtPanel.document.getPersistenceService().export(outtemp, new RTFFilter());
				EBerichtPanel.document.getPersistenceService().export(url, new PDFFilter());
			}			
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("**********DokumentException************");
		} catch (NOAException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("**********NoeException************");
		}
	}
	public void textSpeichernInDB(boolean mittemp){
		Statement stmt = null;;
		ResultSet rs = null;
		PreparedStatement ps = null;
		boolean ret = false;
		int bilder = 0;
		FileInputStream fis = null;
		try {
			if(EBerichtPanel.document==null){
				Reha.thisClass.progressStarten(false);
				return;
			}
			if(!EBerichtPanel.document.isOpen()){
				Reha.thisClass.progressStarten(false);
				return;
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			EBerichtPanel.document.getPersistenceService().export(out, new RTFFilter());
			//EBerichtPanel.document.getPersistenceService().store(out);
			InputStream ins = new ByteArrayInputStream(out.toByteArray());
			String select = "Update bericht2 set freitext = ? where berichtid = ?";
			ps = (PreparedStatement) Reha.thisClass.conn.prepareStatement(select);
			ps.setAsciiStream(1,ins);
			ps.setInt(2, eltern.berichtid);
			ps.execute();
			if(mittemp){
				if(EBerichtPanel.document == null){return;}
				if(EBerichtPanel.document.isOpen()){
					String url = tempPfad+"EBfliesstext.pdf";
					System.out.println("Speichere in Datenbank und zusätzlich temporär in: "+url);
					outtemp = new ByteArrayOutputStream();
					EBerichtPanel.document.getPersistenceService().export(outtemp, new RTFFilter());
					EBerichtPanel.document.getPersistenceService().export(url, new PDFFilter());
				}			
			}
			ins.close();
			out.close();
			//EBerichtPanel.document.close();
			Reha.thisClass.progressStarten(false);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { // ignore }
					rs = null;
				}
			}	
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { // ignore }
					stmt = null;
				}
			}
			if(ps != null){
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
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
		   // officeFrame.disableDispatch(GlobalCommands.DIREKT_EXPORT_DOCUMENT);
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
					SwingUtilities.invokeLater(new Runnable(){
					 	   public  void run()
					 	   {
								baueSeite();
										 		   
					 	   }
					});	


				}
				if(evt.getDetails()[1].equals("#SPEICHERNUNDENDE") && evt.getDetails()[0].contains("Gutachten")){
					SwingUtilities.invokeLater(new Runnable(){
					 	   public  void run()
					 	   {
					 		   textSpeichernInDB(false);
					 		   speichernSeite();
										 		   
					 	   }
					});	
				}
				if(evt.getDetails()[1].equals("#SPEICHERNTEMP") && evt.getDetails()[0].contains("Gutachten")){
					SwingUtilities.invokeLater(new Runnable(){
					 	   public  void run()
					 	   {
					 		   textSpeichernInDB(true);
					 		   //tempTextSpeichern();
										 		   
					 	   }
					});	

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
