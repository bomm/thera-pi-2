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
import java.io.IOException;
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

	//boolean gestartet = false;
	//boolean inseitenaufbau = false;
	//boolean getrennt = true;
	//boolean tempgespeichert = false;
	//boolean pdfok = false;
	/***********neue logische Variable***************/
	boolean newframeok = false;
	boolean bytebufferok = false;
	boolean pdfok = false;
	boolean inseitenaufbau = false;
	boolean framegetrennt = true;
	InputStream startStream = null;
	
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
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					if(eltern.neu){
						baueSeite();
						inseitenaufbau = true;
						while(inseitenaufbau){
							Thread.sleep(20);
						}
						System.out.println("Vorhandener Bericht - Seite wurde zum StartAufgebaut");
						trenneFrame(false);
					}else{
						macheByteBuffer();
						baueSeite();
						inseitenaufbau = true;
						while(inseitenaufbau){
							Thread.sleep(20);
						}
						System.out.println("Vorhandener Bericht - Seite wurde zum StartAufgebaut");
						trenneFrame(false);
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				//baueSeite();
				return null;
			}
		}.execute();
	}
	public void beendeSeite(){
		eltern.document.close();
		if(outtemp != null){
			try {
				outtemp.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	public void baueSeite(){
		if(!framegetrennt){
			System.out.println("Frame is nicht getrennt - > baueSeite() wird nicht durchlaufen");
			inseitenaufbau = false;
			return;
		}
			
		new Thread(){
			public void run(){
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					try {
						pdfok = false;
						eltern.setCursor(new Cursor(Cursor.WAIT_CURSOR));
						eltern.officeFrame = constructOOOFrame(Reha.officeapplication,pan);
						configureOOOFrame(Reha.officeapplication,eltern.officeFrame);
			        	DocumentDescriptor d = new DocumentDescriptor();
			        	d.setTitle("Entlassbericht");
			        	//Sofern es sich um eine Berichtsneuanlage handelt
			        	if(eltern.neu){
			        		// wenn noch kein frame erstellt wurde und der outbuffe leer ist;
			        		if(!newframeok && (outtemp == null)){
			        			System.out.println("Neuanlage Bericht -> constructNewDocument");
			        			eltern.document = (ITextDocument) Reha.officeapplication.getDocumentService().constructNewDocument(eltern.officeFrame,IDocument.WRITER,d);
			        			eltern.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			        			new SwingWorker<Void,Void>(){
									@Override
									protected Void doInBackground()
											throws Exception {
										String url = tempPfad+"EBfliesstext.pdf";
					        			outtemp = new ByteArrayOutputStream();
					        			eltern.document.getPersistenceService().export(outtemp, new RTFFilter());
					        			eltern.document.getPersistenceService().export(url, new PDFFilter());
					    				EBerichtPanel.document.setModified(false);
					        			Reha.thisClass.progressStarten(false);
					        			newframeok = true;
					        			bytebufferok = true;
										pdfok = true;
										System.out.println("ByteBuffer für Bericht Neuanlage wurde erstellt\n"+getStatus());
										return null;
									}
			        			}.execute();
			        			framegetrennt = false;
			        		}else{
			        			System.out.println("Neuanlage Bericht -> newFrame bereits erzeugt, verwende Inputstream");
			        			InputStream ins = new ByteArrayInputStream(outtemp.toByteArray());
			        			DocumentDescriptor descript = new DocumentDescriptor();
			        			descript.setFilterDefinition(RTFFilter.FILTER.getFilterDefinition(IDocument.WRITER)); 
			        			//descript.setHidden(true);
			        			if(ins==null){
			        				System.out.println("Neuanlage Bericht -> ByteArray = null, constructNewDocument");
				        			eltern.document = (ITextDocument) Reha.officeapplication.getDocumentService().constructNewDocument(eltern.officeFrame,IDocument.WRITER,d);			        				
				        			bytebufferok = false;
			        			}else{
			        				System.out.println("Neuanlage Bericht -> ByteArray o.k, lade Dokument aus InputStream");
				        			eltern.document = (ITextDocument) Reha.officeapplication.getDocumentService().loadDocument(eltern.officeFrame,ins, descript);			        				
			        			}
			        			new SwingWorker<Void,Void>(){
									@Override
									protected Void doInBackground()
											throws Exception {
										String url = tempPfad+"EBfliesstext.pdf";
					        			outtemp = new ByteArrayOutputStream();
					    				EBerichtPanel.document.getPersistenceService().export(outtemp, new RTFFilter());
					    				EBerichtPanel.document.getPersistenceService().export(url, new PDFFilter());
					    				EBerichtPanel.document.setModified(false);
					    				bytebufferok = true;
					    				pdfok = true;
					    				return null;
									}
			        			}.execute();
			        			System.out.println("Status Neuanlage Durchlauf >= 2:"+getStatus());
								XController xController = eltern.document.getXTextDocument().getCurrentController();
								XTextViewCursorSupplier xTextViewCursorSupplier = (XTextViewCursorSupplier) UnoRuntime.queryInterface(XTextViewCursorSupplier.class,
								xController);
								XTextViewCursor xtvc = xTextViewCursorSupplier.getViewCursor();
								xtvc.gotoStart(false);
								if(ins != null){
									ins.close();									
								}
								eltern.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
								Reha.thisClass.progressStarten(false);
			        			framegetrennt = false;
			        			
			        			System.out.println("Status Neuanlage Bericht -> am Ende des 2. Durchlaufes = "+getStatus());
			        		}
			        	}else{
			        		//Es handelt sich um einen vorhandenen Bericht
			        		if(!newframeok){
			        			/*
			        			InputStream is = null;
			        			if(outtemp == null){
			        				is = SqlInfo.holeStream("bericht2","freitext","berichtid='"+eltern.berichtid+"'");
			        				System.out.println("Vorhandener Bericht 1.Durchlauf, Status = "+getStatus());
			        			}else{
			        				is = new ByteArrayInputStream(outtemp.toByteArray());	
			        			}
			        			*/
			        			DocumentDescriptor descript = new DocumentDescriptor();
			        			descript.setFilterDefinition(RTFFilter.FILTER.getFilterDefinition(IDocument.WRITER));
			        			//descript.setHidden(true);
			        			if(startStream == null){
				        			eltern.document = (ITextDocument) Reha.officeapplication.getDocumentService().constructNewDocument(eltern.officeFrame,IDocument.WRITER,d);			        				
			        			}else{
			        				eltern.document = (ITextDocument) Reha.officeapplication.getDocumentService().loadDocument(eltern.officeFrame,startStream, descript);
			        			}
			        			new SwingWorker<Void,Void>(){
									@Override
									protected Void doInBackground()
											throws Exception {
										String url = tempPfad+"EBfliesstext.pdf";
					        			outtemp = new ByteArrayOutputStream();
					    				EBerichtPanel.document.getPersistenceService().export(outtemp, new RTFFilter());
					    				EBerichtPanel.document.getPersistenceService().export(url, new PDFFilter());
					    				EBerichtPanel.document.setModified(false);
					    				bytebufferok = true;
					    				pdfok = true;
					    				return null;
									}
			        			}.execute();

								XController xController = eltern.document.getXTextDocument().getCurrentController();
								XTextViewCursorSupplier xTextViewCursorSupplier = (XTextViewCursorSupplier) UnoRuntime.queryInterface(XTextViewCursorSupplier.class,
								xController);
								XTextViewCursor xtvc = xTextViewCursorSupplier.getViewCursor();
								xtvc.gotoStart(false);
								if(startStream != null){
									startStream.close();
								}	
								eltern.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
								Reha.thisClass.progressStarten(false);
								newframeok = true;
								System.out.println("Status vorhandener Bericht -> am Ende des 1. Durchlaufes = "+getStatus());
			        			
			        		}else{
			        			System.out.println("starte Dokument mit temp. Stream-Daten");
			        			InputStream ins = new ByteArrayInputStream(outtemp.toByteArray());
			        			DocumentDescriptor descript = new DocumentDescriptor();
			        			descript.setFilterDefinition(RTFFilter.FILTER.getFilterDefinition(IDocument.WRITER)); 
			        			//descript.setHidden(true);
			        			eltern.document = (ITextDocument) Reha.officeapplication.getDocumentService().loadDocument(eltern.officeFrame,ins, descript);
			        			new SwingWorker<Void,Void>(){
									@Override
									protected Void doInBackground()
											throws Exception {
										String url = tempPfad+"EBfliesstext.pdf";
					        			outtemp = new ByteArrayOutputStream();
					    				EBerichtPanel.document.getPersistenceService().export(outtemp, new RTFFilter());
					    				EBerichtPanel.document.getPersistenceService().export(url, new PDFFilter());
					    				EBerichtPanel.document.setModified(false);
					    				bytebufferok = true;
					    				pdfok = true;
					    				return null;
									}
			        			}.execute();
			        			
								XController xController = eltern.document.getXTextDocument().getCurrentController();
								XTextViewCursorSupplier xTextViewCursorSupplier = (XTextViewCursorSupplier) UnoRuntime.queryInterface(XTextViewCursorSupplier.class,
								xController);
								XTextViewCursor xtvc = xTextViewCursorSupplier.getViewCursor();
								xtvc.gotoStart(false);
								ins.close();
								//eltern.document.getFrame().getXFrame().getContainerWindow().setVisible(true);
								eltern.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
								Reha.thisClass.progressStarten(false);
								System.out.println("Status vorhandener Bericht -> am Ende des 2. Durchlaufes = "+getStatus());
			        		}
			        	}
			        	OOTools.setzePapierFormat(eltern.document, new Integer(25199), new Integer(19299));
			        	OOTools.setzeRaender(eltern.document, new Integer(1000), new Integer(1000),new Integer(1000),new Integer(1000));
			        	framegetrennt = false;
			        	
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					inseitenaufbau = false;
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
					
					trenneFrame(true);
					framegetrennt = true;
					//eltern.document.close();
					if(outtemp != null){
						try {
							outtemp.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
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
		 			   System.out.println("Aufruf der Funktion -> neuAnhaengen, Status = :"+getStatus());
			 			pdfok = false;
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
						framegetrennt = false;
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
	public void trenneFrame(boolean mitspeichern){
		System.out.println("ntiveView getrennt----->");
		if(eltern.neu){
			if(mitspeichern){
				System.out.println("Aufruf trenneFrame bei Neuanlage mit Speichern, Status:"+getStatus());
				try {
					tempTextSpeichern();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				System.out.println("Aufruf trenneFrame bei Neuanlage ohne Speichern, Status:"+getStatus());				
			}
			EBerichtPanel.document.close();
			pan.remove(nativeView);
			framegetrennt = true;
			Reha.thisClass.progressStarten(false);
			return;
		}else{
			
		}
		final boolean xmitspeichern = mitspeichern;
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				framegetrennt = true;
				if(xmitspeichern){
					tempTextSpeichern();	
					System.out.println("Aufruf trenneFrame bei vorhanden. Bericht mit Speichern, Status:"+getStatus());
				}else{
					System.out.println("Aufruf trenneFrame bei vorhanden. Bericht ohne Speichern, Status:"+getStatus());
				}
				EBerichtPanel.document.close();
				pan.remove(nativeView);


				return null;
			}
			
		}.execute();
	}
	public void tempTextSpeichern() throws InterruptedException{
		String url = tempPfad+"EBfliesstext.pdf";
		System.out.println("speichere temporär in: "+url);
		//pdfok = false;
		
		try {
			if(EBerichtPanel.document == null){
				System.out.println("Das Dokument ist momentan null");
				inseitenaufbau = true;
				baueSeite();
				while(inseitenaufbau){
					Thread.sleep(10);
				}
				System.out.println("Die Seite wurde neu afugebaut");
				//return;
			}
			if(EBerichtPanel.document.isOpen()){
				outtemp = new ByteArrayOutputStream();
				EBerichtPanel.document.getPersistenceService().export(outtemp, new RTFFilter());
				EBerichtPanel.document.getPersistenceService().export(url, new PDFFilter());
				EBerichtPanel.document.setModified(false);
				pdfok = true;
				bytebufferok = false;
			}	
			if(eltern.ebtab.getSelectedIndex() != 2){
				trenneFrame(false);
				framegetrennt = true;
				System.out.println("Trenne Frame Selected Tab ist nicht der OO-Tab");
			}else{
				framegetrennt = false;
			}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("**********DokumentException************");
		} catch (NOAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("**********NoeException************");
		}
	}
	public void tempStartSpeichern() throws InterruptedException{
		
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
					pdfok = true;
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
					try {
						if(outtemp != null){
							outtemp.close();
						}
						trenneFrame(false);
						outtemp = null;
						pdfok = false;
						//gestartet = false;
						//tempgespeichert = false;

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					this.rEvent.removeRehaEventListener((RehaEventListener)this);
				}
			}
			if(evt.getRehaEvent().equals("OOFrame")){
				if(evt.getDetails()[1].equals("#TRENNEN") ){
					this.trenneFrame(true);
				}
			}			
			

		}
		private void macheByteBuffer(){
			InputStream is = null;
			outtemp = new ByteArrayOutputStream();
			startStream = SqlInfo.holeStream("bericht2","freitext","berichtid='"+eltern.berichtid+"'");
			/*
			byte[] buff;
			try {
				buff = new byte[is.available()];
				outtemp.write(is.read(buff, 0, buff.length));
				outtemp.flush();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/

			
		}
		private String getStatus(){
			boolean newframeok = false;
			boolean bytebufferok = false;
			boolean pdfok = false;
			boolean inseitenaufbau = false;
			boolean framegetrennt = true;
			String ret      = "Statusanzeige:\n"+
			"newframeok     = "+newframeok+"\n"+
			"bytebufferok   = "+bytebufferok+"\n"+
			"pdfok          = "+pdfok+"\n"+
			"inseitenaufbau = "+inseitenaufbau+"\n"+
			"framegetrennt  = "+framegetrennt+"\n";
			return ret;
		}

}
