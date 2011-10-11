package entlassBerichte;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import oOorgTools.OOTools;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;

import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import ag.ion.bion.officelayer.NativeView;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.desktop.GlobalCommands;
import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.filter.PDFFilter;
import ag.ion.bion.officelayer.filter.RTFFilter;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextRange;
import ag.ion.bion.officelayer.text.IViewCursor;
import ag.ion.noa.frame.ILayoutManager;

import com.mysql.jdbc.PreparedStatement;
import com.sun.star.frame.XController;
import com.sun.star.text.XTextViewCursor;
import com.sun.star.text.XTextViewCursorSupplier;
import com.sun.star.uno.UnoRuntime;

import events.RehaEvent;
import events.RehaEventClass;
import events.RehaEventListener;

public class Eb3 implements RehaEventListener  {
	RehaEventClass rEvent = null;
	public JXPanel pan = null;
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
	boolean zugabe = false;
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
		if(!Reha.officeapplication.isActive()){
			//System.out.println("Aktiviere Office...");
			Reha.starteOfficeApplication();
		}
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					inseitenaufbau = true;
					baueSeite();
					while(inseitenaufbau){
						Thread.sleep(20);
					}
					//System.out.println("Vorhandener Bericht - Seite wurde zum StartAufgebaut");
				}catch(Exception ex){
					Reha.thisClass.progressStarten(false);
					ex.printStackTrace();
				}
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
		new Thread(){
			public void run(){
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					
					try {
						pdfok = false;
						
						eltern.officeFrame = constructOOOFrame(Reha.officeapplication,pan);
						configureOOOFrame(Reha.officeapplication,eltern.officeFrame);
			        	DocumentDescriptor d = new DocumentDescriptor();
			        	d.setTitle("Entlassbericht");
			        	//Sofern es sich um eine Berichtsneuanlage handelt
			        	if(eltern.neu){
			        		// wenn noch kein frame erstellt wurde und der outbuffe leer ist;
			        	
		        			//System.out.println("Neuanlage Bericht -> constructNewDocument");
		        			if(!Reha.officeapplication.isActive()){
		        				Reha.starteOfficeApplication();
		        			}
		        			eltern.document = (ITextDocument) Reha.officeapplication.getDocumentService().constructNewDocument(eltern.officeFrame,IDocument.WRITER,d);
				        	OOTools.setzePapierFormat(eltern.document, Integer.valueOf(25199), Integer.valueOf(19299));
				        	OOTools.setzeRaender(eltern.document, Integer.valueOf(1000), Integer.valueOf(1000),Integer.valueOf(1000),Integer.valueOf(1000));
				        	framegetrennt = false;
							eltern.meldeInitOk(2);
		        			SwingUtilities.invokeLater(new Runnable(){
		        				public void run(){
				        			eltern.jry.setSize(eltern.jry.getWidth()+(zugabe ? 1 : -1), eltern.jry.getHeight());
				        			zugabe = (zugabe ? false : true);
		        				}
		        			});
		        			eltern.document.setModified(false);
			        	}else{
							if(!Reha.officeapplication.isActive()){
								//System.out.println("Aktiviere Office...");
								Reha.starteOfficeApplication();
								Thread.sleep(100);
							}
			        		new SwingWorker<Void,Void>(){
								@Override
								protected Void doInBackground()
										throws Exception {
									InputStream ins = null;
									try{

					        			//System.out.println("starte Dokument mit temp. Stream-Daten");
					        			ins  = SqlInfo.holeStream("bericht2","freitext","berichtid='"+eltern.berichtid+"'");
					        			if(ins.available() > 0){
						        			DocumentDescriptor descript = new DocumentDescriptor();
						        			descript.setTitle("OpenOffice.org Bericht");
						        			//descript.setFilterDefinition(RTFFilter.FILTER.getFilterDefinition(IDocument.WRITER)); 
						        			try{
							        			eltern.document = (ITextDocument) Reha.officeapplication.getDocumentService().loadDocument(eltern.officeFrame,ins, descript);
							        			eltern.meldeInitOk(2);
							        			SwingUtilities.invokeLater(new Runnable(){
							        				public void run(){
									        			eltern.jry.setSize(eltern.jry.getWidth()+(zugabe ? 1 : -1), eltern.jry.getHeight());
									        			zugabe = (zugabe ? false : true);
									        			try {
															eltern.document.setModified(false);
														} catch (DocumentException e) {
															e.printStackTrace();
														}
							        				}
							        			});
						        			}catch(Exception ex){
												Reha.starteOfficeApplication();
							        			eltern.document = (ITextDocument) Reha.officeapplication.getDocumentService().loadDocument(eltern.officeFrame,ins, descript);
							        			eltern.meldeInitOk(2);
							        			SwingUtilities.invokeLater(new Runnable(){
							        				public void run(){
									        			eltern.jry.setSize(eltern.jry.getWidth()+(zugabe ? 1 : -1), eltern.jry.getHeight());
									        			zugabe = (zugabe ? false : true);
									        			try {
															eltern.document.setModified(false);
														} catch (DocumentException e) {
															e.printStackTrace();
														}
							        				}
							        			});
						        			}
					        			}else{
					        				DocumentDescriptor descript = new DocumentDescriptor();
						        			descript.setTitle("OpenOffice.org Bericht");
						        			eltern.document = (ITextDocument) Reha.officeapplication.getDocumentService().constructNewDocument(eltern.officeFrame,IDocument.WRITER,descript);
						        			
								        	OOTools.setzePapierFormat(eltern.document, Integer.valueOf(25199), Integer.valueOf(19299));
								        	OOTools.setzeRaender(eltern.document, Integer.valueOf(1000), Integer.valueOf(1000),Integer.valueOf(1000),Integer.valueOf(1000));
								        	framegetrennt = false;
											eltern.meldeInitOk(2);
											eltern.document.setModified(false);
											//JOptionPane.showMessageDialog(null, "Kann Daten aus Datenbank nicht öffnen");
					        			}
					        			if(ins != null){
					        				ins.close();
					        			}
					        			eltern.document.setModified(false);
									}catch(Exception ex2){
										inseitenaufbau = false;
										ex2.printStackTrace();
										return null;
									}
									
				        			new SwingWorker<Void,Void>(){
										@Override
										protected Void doInBackground()
												throws Exception {
											try{
											String url = tempPfad+"EBfliesstext.pdf";
						        			outtemp = new ByteArrayOutputStream();
						    				eltern.document.getPersistenceService().export(outtemp, new RTFFilter());
						    				eltern.document.getPersistenceService().export(url, new PDFFilter());
						    				outtemp.close();
						    				bytebufferok = true;
						    				pdfok = true;
											}catch(Exception ex){
												ex.printStackTrace();
												inseitenaufbau = false;
											}
											eltern.document.setModified(false);
						    				return null;

										}
				        			}.execute();
									XController xController = eltern.document.getXTextDocument().getCurrentController();
									XTextViewCursorSupplier xTextViewCursorSupplier = (XTextViewCursorSupplier) UnoRuntime.queryInterface(XTextViewCursorSupplier.class,
									xController);
									XTextViewCursor xtvc = xTextViewCursorSupplier.getViewCursor();
									xtvc.gotoStart(false);

									//eltern.document.getFrame().getXFrame().getContainerWindow().setVisible(true);
									//System.out.println("Status vorhandener Bericht -> am Ende des 2. Durchlaufes = "+getStatus());
									// TODO Auto-generated method stub
						        	OOTools.setzePapierFormat(eltern.document, Integer.valueOf(25199), Integer.valueOf(19299));
						        	OOTools.setzeRaender(eltern.document, Integer.valueOf(1000), Integer.valueOf(1000),Integer.valueOf(1000),Integer.valueOf(1000));
						        	framegetrennt = false;
									eltern.meldeInitOk(2);
				        			pan.setSize(pan.getWidth()+1, pan.getHeight());
				        			eltern.document.setModified(false);
									return null;
								}
			        		}.execute();
			        	}
			        	
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
	

	public JXPanel getSeite(){
		return pan;
	}
/**********************
 * 
 * @param officeApplication
 * @param parent
 * @return
 * @throws Throwable
 */

	private IFrame constructOOOFrame(IOfficeApplication officeApplication, final Container parent)
      throws Throwable {
		
		//final NativeView nativeView = new NativeView(SystemConfig.OpenOfficeNativePfad);
	    nativeView = new NativeView(SystemConfig.OpenOfficeNativePfad);
	    
	    if(nativeView == null){
	    	////System.out.println("nativeView == null");
	    }
	    if(parent == null){
	    	////System.out.println("parent == null");
	    }
	    parent.add(nativeView);
	    parent.validate();
	    parent.setVisible(true);	    


	    parent.addContainerListener(new ContainerAdapter(){
	    	public void componentAdded(ContainerEvent e) {
	    		////System.out.println(" added to "+e);
	    	    }
	    	    public void componentRemoved(ContainerEvent e) {
	    		////System.out.println(" removed from "+e);
	    	    }
	    });
	    parent.addComponentListener(new ComponentAdapter(){
	        public void componentResized(ComponentEvent e) {
	        	
        		//System.out.println("In  NativeView Resize");
        		refreshSize();
        		nativeView.setPreferredSize(new Dimension(parent.getWidth(),parent.getHeight()-5));
        		parent.getLayout().layoutContainer(parent);
        		//parent.repaint();
        		eltern.ebt.getTab1().refreshSize();
	        }  
	        public void componentHidden(ComponentEvent e) {
	            ////System.out.println(e.getComponent().getClass().getName() + " --- Hidden");
	        }

	        public void componentMoved(ComponentEvent e) {
	        	////System.out.println(e.getComponent().getClass().getName() + " --- Moved");
	        }
	        public void componentShown(ComponentEvent e) {
	        	////System.out.println(e.getComponent().getClass().getName() + " --- Shown");
	            nativeView.setPreferredSize(new Dimension(parent.getWidth(),parent.getHeight()-5));
		        parent.getLayout().layoutContainer(parent);
		        parent.setVisible(true);

	        }
	        
	      });
	    nativeView.setPreferredSize(new Dimension(parent.getWidth(), parent.getHeight()-5));
	    parent.getLayout().layoutContainer(parent);
	    eltern.officeFrame = officeApplication.getDesktopService().constructNewOfficeFrame(nativeView);
	    parent.validate();
	    ////System.out.println("natveView eingeh�ngt in Panel "+parent.getName());
    return eltern.officeFrame;
  }

	
	

	public void tempTextSpeichern() throws InterruptedException{
		String url = tempPfad+"EBfliesstext.pdf";
		if(eltern.document.isOpen()){
			if(eltern.document.isModified()){
				//System.out.println("speichere temporär in: "+url);
				outtemp = new ByteArrayOutputStream();
				try {
					eltern.document.getPersistenceService().store(outtemp);
					//eltern.document.getPersistenceService().export(outtemp, RTFFilter.FILTER);
					eltern.document.getPersistenceService().export(url, PDFFilter.FILTER);
					eltern.document.setModified(false);
				pdfok = true;
				bytebufferok = false;
				outtemp.close();
				} catch (DocumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				//System.out.println("Dokukment wurde nicht verändert, temporäres speichern daher nicht erforderlich" );
			}

		}	
	}	

	public void tempStartSpeichern() throws InterruptedException{
		
	}
	
	public boolean textSpeichernInDB(boolean mittemp){
		Statement stmt = null;;
		ResultSet rs = null;
		PreparedStatement ps = null;
		//boolean ret = false;
		//int bilder = 0;
		//FileInputStream fis = null;
		boolean fehler = false;

		try {
			if(eltern.document==null){
				Reha.thisClass.progressStarten(false);
				return false;
			}
			if(!eltern.document.isOpen()){
				Reha.thisClass.progressStarten(false);
				return false;
			}
			Reha.thisClass.progressStarten(true);
			//byte[] barr = null;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try{
				if(eltern.document == null ){
					//System.out.println("Dokument == null");
				}
				/*
				eltern.document.close();
				if(!eltern.document.isOpen()){
					return false;					
				}
				*/
				eltern.document.setModified(false);
				nativeView.setVisible(true);
				nativeView.requestFocus();

				
				//FileOutputStream fout = new FileOutputStream(Reha.proghome+"temp/"+Reha.aktIK+"/ootemp");
				eltern.document.getPersistenceService().store(out);
				//eltern.document.getPersistenceService().export(out, RTFFilter.FILTER);
				//eltern.document.getPersistenceService().store(Reha.proghome+"temp/"+Reha.aktIK+"/ootemp");
				//fout.flush();
				//fout.close();
				//barr = FileTools.File2ByteArray(new File(Reha.proghome+"temp/"+Reha.aktIK+"/ootemp"));
	


			}catch(Exception ex){
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null,"Fehler beim speichern, bitte erneut speichern drücken");
				fehler = true;
			}
			if(fehler){
				return false;
			}
			
			
			InputStream ins = new ByteArrayInputStream(out.toByteArray());
			String select = "Update bericht2 set freitext = ? where berichtid = ? LIMIT 1";
			ps = (PreparedStatement) Reha.thisClass.conn.prepareStatement(select);
			ps.setAsciiStream(1,ins);
			ps.setInt(2, eltern.berichtid);
			ps.execute();
			ins.close();
			
			if(mittemp){
				if(eltern.document == null){return false;}
				if(eltern.document.isOpen()){
					String url = tempPfad+"EBfliesstext.pdf";
					//System.out.println("Speichere in Datenbank und zusätzlich temporär in: "+url);
					outtemp = new ByteArrayOutputStream();
					eltern.document.getPersistenceService().store(outtemp);
					//eltern.document.getPersistenceService().export(outtemp, RTFFilter.FILTER);
					eltern.document.getPersistenceService().export(url, PDFFilter.FILTER);
					pdfok = true;
					outtemp.close();
				}			
			}
			out.close();
	    	eltern.document.setModified(false);
			//EBerichtPanel.document.close();
			Reha.thisClass.progressStarten(false);
			

		}catch(Exception ex){
			ex.printStackTrace();
			Reha.thisClass.progressStarten(false);
			return false;
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
		return true;
		
	}
	void insertTextAtCurrentPosition(String xtext){
		
	    IViewCursor viewCursor = eltern.document.getViewCursorService().getViewCursor();
	    ITextRange textRange = viewCursor.getStartTextRange();
	    textRange.setText(xtext);
	    try {
	    	eltern.officeFrame.setFocus();
	    	eltern.officeFrame.updateDispatches();
    		refreshSize();
	    	eltern.document.setModified(false);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}	
	
	  public void configureOOOFrame(IOfficeApplication officeApplication, IFrame officeFrame) throws Throwable {
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
		    officeFrame.disableDispatch(GlobalCommands.SAVE);
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
		public void rehaEventOccurred(RehaEvent evt) {
			if(evt.getRehaEvent().equals("REHAINTERNAL")){
				if(evt.getDetails()[1].equals("#DEICONIFIED") && evt.getDetails()[0].contains("Gutachten")){
					
					SwingUtilities.invokeLater(new Runnable(){
					 	   public  void run()
					 	   {
					 		   //System.out.println("Meldung Deiconified");
								//refreshSize();
								//pan.setVisible(true);
										 		   
					 	   }
					});	


				}
				if(evt.getDetails()[1].equals("#SPEICHERNUNDENDE") && evt.getDetails()[0].contains("Gutachten")){
					SwingUtilities.invokeLater(new Runnable(){
					 	   public  void run()
					 	   {
					 		   textSpeichernInDB(true);
					 		   //speichernSeite();
					 		   if(eltern.document != null){
					 			   if(eltern.document.isOpen()){
					 				  eltern.document.close();
					 			   }
					 		   }
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
					//System.out.println("Lösche Listener von Eb3-------------->");
					try {
						if(outtemp != null){
							outtemp.close();
						}
						if(eltern != null){
							if(eltern.document.isModified()){
								
							}
					 		   if(eltern.document != null){
					 			   if(eltern.document.isOpen()){
					 				  eltern.document.close();
					 				  nativeView = null;
					 			   }
					 		   }
						}

						//trenneFrame(false);
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
					//refreshSize(); 
				}
			}			
			

		}
		
		public final void refreshSize() {
		/*if (pan == null || framegetrennt) {
		return;
		}*/
		pan.setPreferredSize(new Dimension(pan.getWidth() , pan.getHeight()
		- 5));

		final Container parent = pan.getParent();
		if (parent instanceof JComponent) {
		((JComponent) parent).revalidate();
		}

		// ... and just in case, call validate() on the top-level window as well
		final Window window1 = SwingUtilities.getWindowAncestor(nativeView.getParent().getParent());
		if (window1 != null) {
		window1.validate();
		}
		/*
		final Window window2 = SwingUtilities.getWindowAncestor(pan);
		if (window2 != null) {
		window2.validate();
		}
		*/
		pan.getLayout().layoutContainer(pan);
		//pan.setVisible(true);
		}
		/*
		private void macheByteBuffer(){
			InputStream is = null;
			outtemp = new ByteArrayOutputStream();
			startStream = SqlInfo.holeStream("bericht2","freitext","berichtid='"+eltern.berichtid+"'");
			
		}
		*/
		
		/*
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
		*/
		

}
