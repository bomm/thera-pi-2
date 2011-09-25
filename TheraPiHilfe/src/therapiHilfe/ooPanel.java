package therapiHilfe;




import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;


import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;



import java.awt.Container;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Vector;

import ag.ion.bion.officelayer.NativeView;
import ag.ion.bion.officelayer.application.IApplicationAssistant;
import ag.ion.bion.officelayer.application.ILazyApplicationInfo;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.application.OfficeApplicationRuntime;
import ag.ion.bion.officelayer.desktop.DesktopException;
import ag.ion.bion.officelayer.desktop.GlobalCommands;
import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.event.ICloseListener;
import ag.ion.bion.officelayer.event.IDocumentEvent;
import ag.ion.bion.officelayer.event.IDocumentListener;
import ag.ion.bion.officelayer.event.IEvent;
import ag.ion.bion.officelayer.filter.HTMLFilter;
import ag.ion.bion.officelayer.filter.PDFFilter;
import ag.ion.bion.officelayer.internal.application.ApplicationAssistant;
import ag.ion.bion.officelayer.text.ITextCursor;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.web.IWebDocument;
import ag.ion.noa.NOAException;
import ag.ion.noa.frame.ILayoutManager;

import com.sun.star.awt.Key;
import com.sun.star.awt.KeyEvent;
import com.sun.star.awt.KeyModifier;
import com.sun.star.awt.XExtendedToolkit;
import com.sun.star.awt.XKeyHandler;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XTopWindow;
import com.sun.star.awt.XWindow;
import com.sun.star.awt.XWindowPeer;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.container.XNameAccess;
import com.sun.star.container.XNameContainer;
import com.sun.star.frame.XController;
import com.sun.star.frame.XFrame;
import com.sun.star.frame.XFrameActionListener;
import com.sun.star.frame.XFramesSupplier;
import com.sun.star.lang.EventObject;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XEventListener;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.lang.XServiceInfo;
import com.sun.star.text.XTextContent;
import com.sun.star.text.XTextGraphicObjectsSupplier;
import com.sun.star.uno.Any;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.view.DocumentZoomType;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;

public class ooPanel{
	
	public ITextDocument doc = null;
	public IFrame frame = null;
	public ICloseListener clListener = null;

	private XExtendedToolkit extendedToolkit = null;
	private com.sun.star.awt.XTopWindowListener topWindowListener = null;
	private XFrame xxframe = null;
	private XTopWindow myTopWindow = null;
	static ooPanel thisClass; 
	public JXPanel noaPanel = null;
	private static IFrame             officeFrame       = null;
	static ITextDocument      document          = null;
	public static ITextDocument textDocument;
	public static IWebDocument      webdocument          = null;
	public static IWebDocument webtextDocument;
	final static int ANSICHT_WEB = 1;
	final static int ANSICHT_DOKUMENT = 0;
	public static int ansicht = 0;
	DokumentListener doclistener = null;

	
	ooPanel(JXPanel jpan){
		noaPanel = jpan;
		thisClass = this;
		fillNOAPanel();
		/*
		try {
			//configureOOOFrame(piHelp.officeapplication,officeFrame);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
	}

/*********************************************************/
	 private void fillNOAPanel() {
		    if (noaPanel != null) {
		      try {
		        if (piHelp.officeapplication == null)
		        	piHelp.officeapplication = startOOO();
		        
		      
		        	officeFrame = constructOOOFrame(piHelp.officeapplication, noaPanel);
		        	DocumentDescriptor d = new DocumentDescriptor();
		        	d.setTitle("piHelp- leeres Dokument");
		        	//d.setURL(piHelp.tempvz+"dummy.html");
		        	//d.setAsTemplate(true);
		        	//d.setFilterDefinition(HTMLFilter.FILTER.getFilterDefinition(IDocument.WRITER)); 
		        	//
		        	/*
		        	File f = new File( piHelp.tempvz+"dummy.html" ); 
		        	byte[] buffer = new byte[ (int) f.length() ]; 
		        	InputStream in = new FileInputStream( f ); 
		        	in.read( buffer ); 
		        	in.close();
		        	*/

		        	//document = (ITextDocument) piHelp.officeapplication.getDocumentService().loadDocument(officeFrame,in, d);
		        	//document = (ITextDocument) piHelp.officeapplication.getDocumentService().loadDocument(helpFenster.readFileToOutputStream(piHelp.tempvz+"dummy.html"), d);
		        	document = (ITextDocument) piHelp.officeapplication.getDocumentService().constructNewDocument(officeFrame,IDocument.WRITER,d);
		        	ansicht = ANSICHT_DOKUMENT;
		        	piHelp.thisClass.jFrame.setExtendedState(JXFrame.MAXIMIZED_BOTH);
		        	piHelp.thisClass.jFrame.validate();
		        	
		        	//textDocument = (ITextDocument)document;
		        	if(doclistener == null){
			        	doclistener = new DokumentListener(piHelp.officeapplication);		        		
		        	}
		        	piHelp.officeapplication.getDesktopService().addDocumentListener(doclistener);		        	
				    officeFrame.disableDispatch(GlobalCommands.CLOSE_DOCUMENT);
				    officeFrame.disableDispatch(GlobalCommands.QUIT_APPLICATION);
				    officeFrame.disableDispatch(GlobalCommands.CLOSE_WINDOW);
				    officeFrame.updateDispatches();
		        	/*
		        	webdocument = (IWebDocument) piHelp.officeapplication.getDocumentService().constructNewDocument(officeFrame,
		        			IDocument.WEB,
		            DocumentDescriptor.DEFAULT);
		        	*/
		        	
		        	
		        noaPanel.setVisible(true);
				piHelp.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		      }
		      catch (Throwable throwable) {
		        noaPanel.add(new JLabel("Error in creating the NOA panel: " + throwable.getMessage()));
		      }
		    }
		  }
 /*********************************************************/
		  private IOfficeApplication startOOO() throws Throwable {
			  IApplicationAssistant applicationAssistant = new ApplicationAssistant(piHelp.OfficeNativePfad);
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
		    final NativeView nativeView = new NativeView(piHelp.OfficeNativePfad);
		    if(nativeView == null){
		    	//System.out.println("nativeView == null");
		    }
		    if(parent == null){
		    	//System.out.println("nativeView == null");
		    }
		    parent.add(nativeView);
		    parent.addComponentListener(new ComponentAdapter(){
		        public void componentResized(ComponentEvent e) {
		          nativeView.setPreferredSize(new Dimension(parent.getWidth()-5,parent.getHeight()-5));
		          parent.getLayout().layoutContainer(parent);
		        }      
		      });
		    nativeView.setPreferredSize(new Dimension(parent.getWidth() - 5, parent.getHeight() - 5));
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
	
		  public static void allesOOZu(){
			  
		  }
		  public static void schliesseText(){
			  	try {
					piHelp.officeapplication.getDesktopService().removeDocumentListener(thisClass.doclistener);
				} catch (OfficeApplicationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		 
				if(ansicht == ANSICHT_WEB){
					if(webdocument.isOpen()){
						webdocument.close();	
					}
					thisClass.noaPanel.remove(0);
				}else{
					document.close();	
					thisClass.noaPanel.remove(0);					
				}
		  }
		  public static void neuesNoaPanel(){
			  thisClass.fillNOAPanel();
		  }
/*********************************************************/
		  public static void starteNeuenText(){
	        	try {
		        	DocumentDescriptor d = new DocumentDescriptor();
		        	document = (ITextDocument) piHelp.officeapplication.getDocumentService().constructNewDocument(officeFrame,IDocument.WRITER,d);
		        	ansicht = ANSICHT_DOKUMENT;
				} catch (NOAException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OfficeApplicationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			  
		  }
/*********************************************************/		  
		  public static void erzeugeAusByteArray(byte[] bhtml,String datei,boolean alsweb){
			  InputStream is = new ByteArrayInputStream( bhtml ); 
			  FileOutputStream fileOut;
			  String indatei = datei;
			  if(! indatei.contains(".html")){
				  indatei = indatei+".html";
			  }
			try {
				fileOut = new FileOutputStream(indatei);
				fileOut.write(bhtml);
				fileOut.flush();
				fileOut.close();
				extrahiereBilder(datei);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }
/********************************************************/		  
		  
		  public static void starteDatei(String datei,boolean alsweb){

			 /************
			  * 
			  *  Erst hier das Dokument laden
			  *  
			  */
		      //ITextDocument document;
			  String startdatei = datei;
				if(! startdatei.contains(".html")){
					startdatei = datei+".html";
				}

			try {
				/*
				webdocument = (IWebDocument) piHelp.officeapplication.getDocumentService().constructNewDocument(officeFrame,
	        			IDocument.WEB,docdescript);
	        	*/		
				try {

					if(alsweb){
						IDocumentDescriptor docdescript = DocumentDescriptor.DEFAULT;
						docdescript.setURL(datei);
						docdescript.setFilterDefinition(HTMLFilter.FILTER.getFilterDefinition(IDocument.WEB));
						webdocument = (IWebDocument) piHelp.officeapplication.getDocumentService().loadDocument(officeFrame, startdatei, docdescript);					
			        	ansicht = ANSICHT_WEB;
					}else{
						IDocumentDescriptor docdescript = DocumentDescriptor.DEFAULT;
						docdescript.setURL(datei);
						docdescript.setFilterDefinition(HTMLFilter.FILTER.getFilterDefinition(IDocument.WRITER));
						//System.out.println("***************Datei = *****************"+startdatei);
						document = (ITextDocument) piHelp.officeapplication.getDocumentService().loadDocument(officeFrame, startdatei, docdescript);					
			        	ansicht = ANSICHT_DOKUMENT;
					}
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				


				//ITextDocument doc = (ITextDocument) webdocument.getWebDocument();
	            //webdocument.getViewCursorService().getViewCursor().getTextCursorFromStart().insertDocument( is, new HTMLFilter() );
				//doc.getViewCursorService().getViewCursor().getTextCursorFromStart().insertDocument( is, new HTMLFilter() );
				//doc.reformat();
				/*
				document = (ITextDocument)piHelp.officeapplication.getDocumentService().constructNewDocument( officeFrame,
						  IDocument.WRITER, DocumentDescriptor.DEFAULT);
				document.getViewCursorService().getViewCursor().getTextCursorFromStart().insertDocument( is, new HTMLFilter() );
				 textDocument = (ITextDocument) document;
				 */
				 
			}	catch (OfficeApplicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    		  
			  
		  }
/**
 * @throws Exception *******************************************************/		  
		  public static String speichernText(String aktdatei,Boolean neu) {
			  FileOutputStream outputStream = null;
			  String datei = null;
			  try {
				  
				  /*
				  File tempFile;
				try {
					tempFile = File.createTempFile("noatemp" + System.currentTimeMillis(), "html");
					tempFile.deleteOnExit();
					outputStream = new FileOutputStream(tempFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			     */   
			        

				  datei = new String(aktdatei);
				  if(!datei.contains(".html")){
					  datei = datei+".html";
				  }else{
					 //System.out.println("ooPane - speichernText, Dateiname korrekt ->"+datei); 
				  }
				  //System.out.println("ooPane - exportiere Datei nach ->"+datei);				  
				 // textDocument.getPersistenceService().export(temppfad+tempname, new HTMLFilter());
		        	if(ansicht == ANSICHT_DOKUMENT){
		        		//System.out.println(document.getDocumentType());
		        		File f = new File(datei);
		        		/*
		        		try {

			        		ITextDocument textDocument = document;
			        		  XTextGraphicObjectsSupplier graphicObjSupplier = (XTextGraphicObjectsSupplier) UnoRuntime.queryInterface(XTextGraphicObjectsSupplier.class,
			        		      textDocument.getXTextDocument());
			        		  XNameAccess nameAccess = graphicObjSupplier.getGraphicObjects();
			        		  String[] names = nameAccess.getElementNames();
			        		  for (int i = 0; i < names.length; i++) {
			        		    Any xImageAny;
									xImageAny = (Any) nameAccess.getByName(names[i]);
			        		    Object xImageObject = xImageAny.getObject();
			        		    XTextContent xImage = (XTextContent) xImageObject;
			        		    XServiceInfo xInfo = (XServiceInfo) UnoRuntime.queryInterface(XServiceInfo.class, xImage);
			        		    if (xInfo.supportsService("com.sun.star.text.TextGraphicObject")) {
			        		      XPropertySet xPropSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
			        		          xImage);
			        		      String name = xPropSet.getPropertyValue("LinkDisplayName").toString();
			        		      String graphicURL = xPropSet.getPropertyValue("GraphicURL").toString();
			        		      //only ones that are not embedded
			        		      if (graphicURL.indexOf("vnd.sun.") == -1) {
			        		        XMultiServiceFactory multiServiceFactory = (XMultiServiceFactory) UnoRuntime.queryInterface(XMultiServiceFactory.class,
			        		            textDocument.getXTextDocument());
			        		        XNameContainer xBitmapContainer = (XNameContainer) UnoRuntime.queryInterface(XNameContainer.class,
			        		            multiServiceFactory.createInstance("com.sun.star.drawing.BitmapTable"));
			        		        if (!xBitmapContainer.hasByName(name)) {
			        		          xBitmapContainer.insertByName(name, graphicURL);
			        		          String newGraphicURL = xBitmapContainer.getByName(name).toString();
			        		          xPropSet.setPropertyValue("GraphicURL", newGraphicURL);
			        		        }
			        		      }
			        		    }
			        		  }
							} catch (NoSuchElementException e) {
								e.printStackTrace();
							} catch (WrappedTargetException e) {
								e.printStackTrace();
							} catch (UnknownPropertyException e) {
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							}
							*/
		        		
		        		
		        		if(f.exists()){
		        			//System.out.println("IDcument - Datei wird nur gespeichert");
		        			//document.getPersistenceService().export(datei, new HTMLFilter());
		        			document.getPersistenceService().store();	
		        			Thread.sleep(100);
		        		}else{
		        			//System.out.println("IDcument - Datei wird exportiert");
		        			document.getPersistenceService().export(datei, new HTMLFilter());
		        			Thread.sleep(100);
		        		}

						  //document.getPersistenceService().store(datei);
						  //document.getPersistenceService().export(datei+".html", new HTMLFilter());
						  //document.getPersistenceService().export(datei+"#", new HTMLFilter());
		        	}else{
		        		File f = new File(datei);
		        		/*
		        		try {
							
		        		IWebDocument textDocument = webdocument;
		        		  XTextGraphicObjectsSupplier graphicObjSupplier = (XTextGraphicObjectsSupplier) UnoRuntime.queryInterface(XTextGraphicObjectsSupplier.class,
		        		      textDocument.getWebDocument());
		        		  XNameAccess nameAccess = graphicObjSupplier.getGraphicObjects();
		        		  String[] names = nameAccess.getElementNames();
		        		  for (int i = 0; i < names.length; i++) {
		        		    Any xImageAny;
								xImageAny = (Any) nameAccess.getByName(names[i]);
		        		    Object xImageObject = xImageAny.getObject();
		        		    XTextContent xImage = (XTextContent) xImageObject;
		        		    XServiceInfo xInfo = (XServiceInfo) UnoRuntime.queryInterface(XServiceInfo.class, xImage);
		        		    if (xInfo.supportsService("com.sun.star.text.TextGraphicObject")) {
		        		      XPropertySet xPropSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
		        		          xImage);
		        		      String name = xPropSet.getPropertyValue("LinkDisplayName").toString();
		        		      String graphicURL = xPropSet.getPropertyValue("GraphicURL").toString();
		        		      //only ones that are not embedded
		        		      if (graphicURL.indexOf("vnd.sun.") == -1) {
		        		        XMultiServiceFactory multiServiceFactory = (XMultiServiceFactory) UnoRuntime.queryInterface(XMultiServiceFactory.class,
		        		            textDocument.getWebDocument());
		        		        XNameContainer xBitmapContainer = (XNameContainer) UnoRuntime.queryInterface(XNameContainer.class,
		        		            multiServiceFactory.createInstance("com.sun.star.drawing.BitmapTable"));
		        		        if (!xBitmapContainer.hasByName(name)) {
		        		          xBitmapContainer.insertByName(name, graphicURL);
		        		          String newGraphicURL = xBitmapContainer.getByName(name).toString();
		        		          xPropSet.setPropertyValue("GraphicURL", newGraphicURL);
		        		        }
		        		      }
		        		    }
		        		  }
						} catch (NoSuchElementException e) {
							e.printStackTrace();
						} catch (WrappedTargetException e) {
							e.printStackTrace();
						} catch (UnknownPropertyException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}

		        		*/
		        		if(f.exists()){
		        			//System.out.println("IWeb - Datei wird nur gespeichert");
		        			webdocument.getPersistenceService().store();
		        			Thread.sleep(100);
		        			//webdocument.getPersistenceService().export(datei, new HTMLFilter());
		        		}else{
		        			//System.out.println("IWeb - Datei wird exportiert");		        			
		        			webdocument.getPersistenceService().export(datei, new HTMLFilter());
		        			Thread.sleep(100);
		        		}
		        		
		        		//webdocument.getPersistenceService().export(datei+".html", new HTMLFilter());
		        		//webdocument.getPersistenceService().export(datei+".export", new HTMLFilter());
		        	}
				
				} catch (DocumentException e) {

					e.printStackTrace();
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
				return new String(datei);
		  }
		  
/***********************************************/		  
		  public static void extrahiereBilder(String url){
			  //System.out.println("Beginn extrahiereBilder aus "+url);			  
			  helpFenster.thisClass.bilder.clear();
			  BufferedReader infile = null;
			  BufferedWriter outfile = null;
			  
			  try {
				  infile = new BufferedReader(new FileReader(url));
				  outfile = new BufferedWriter(new FileWriter(url+".html"));
				  String str;
				  while((str=infile.readLine())!=null){
					  if(str.contains("IMG SRC=")){
						  outfile.write( testeString(new String(str),"/")+"\n" );
						  outfile.flush();
					  }else{
						  outfile.write(new String(str)+"\n");
						  outfile.flush();
					  }
				  }
				  outfile.flush();
				  outfile.close();
				  infile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }
/***********************************************/		  
		  public static String testeString(String webstring,String trenner){
			  int aktuell = 0;
			  int wo = 0;

			  String meinweb = new String(webstring);
			  String ssret = "";
			  int lang = meinweb.length();

			  while( (wo = webstring.indexOf("IMG SRC=\"",aktuell)) > 0){
				  String nurBild  = "";
				  boolean start = false;
				  boolean austritt = false;
				  int iende = 0;
				  int istart = 0;
				  for(int i = wo; i < lang; i++){
					  for(int d = 0; d < 1;d++){
						  if( (meinweb.substring(i,i+1).equals("\"")) && (!start)){
							  i++;
							  istart = i;
							  start = true;
							  break;
						  }
						  if( (meinweb.substring(i,i+1).equals("\"")) && (start)){
							  start = false;
							  iende = i;
							  austritt = true;
							  break;
						  }
					  }
					  if(austritt){
						  break;
					  }
					  if(start){
						  nurBild = nurBild +meinweb.substring(i,i+1);
					  }
				  }
				  int ergebnis = nurBild.lastIndexOf(trenner);
				  String sret = "";
				  if(ergebnis > -1){
					  sret = new String(nurBild.substring(ergebnis+1));
					  String salt = meinweb.substring(istart,iende);
					  ssret =   new String( meinweb.replaceAll(salt, sret));
					  //sret = new String(nurBild);  
					  helpFenster.thisClass.bilder.add(sret.replaceAll("%20"," "));
				  }else{
					  //String salt = meinweb.substring(istart,iende);
					  sret = nurBild;
					  ssret =   new String(meinweb);
					  helpFenster.thisClass.bilder.add(nurBild.replaceAll("%20", " "));
				  }
				  //System.out.println("Gefunden "+sret);
				  aktuell = new Integer(iende);
			  }
				  
			  return ssret;
			  
		  }
/***********************************************/		  
}
class testObVorhanden{
	String svorhanden = null;
	public boolean init(String svorhanden){
		boolean ret = true;
		Statement stmtx = null;
		ResultSet rsx = null;
		String[] comboInhalt = null;
				stmtx = null;
				rsx = null;
				//System.out.println("In holeGruppen");
				try {
					stmtx = (Statement) piHelp.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					        ResultSet.CONCUR_UPDATABLE );
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					rsx = stmtx.executeQuery("select count(*) from htitel where titel="+svorhanden);
					rsx.next();
					if(rsx.getInt(1) <=0){
						ret = false;
					}	
				}catch(SQLException e){
					e.printStackTrace();
				}
					if (rsx != null) {
						try {
							rsx.close();
						} catch (SQLException sqlEx) { // ignore }
							rsx = null;
						}
					}	
					if (stmtx != null) {
						try {
							stmtx.close();
						} catch (SQLException sqlEx) { // ignore }
							stmtx = null;
						}
					}
					
			
			return ret;
	
	}
}
class DokumentListener implements IDocumentListener {

	private IOfficeApplication officeAplication = null;
	public DokumentListener(IOfficeApplication officeAplication) {
		this.officeAplication = officeAplication;
	}
	@Override
	public void onAlphaCharInput(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onFocus(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onInsertDone(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onInsertStart(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onLoad(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onLoadDone(IDocumentEvent arg0) {
		System.out.println("onLoad Done");
		// TODO Auto-generated method stub
		//System.out.println("************************Dokument geladen************************* "+arg0);
		
	}
	@Override
	public void onLoadFinished(IDocumentEvent arg0) {
		System.out.println("onLoad Finished");
		// TODO Auto-generated method stub
		//System.out.println("************************Dokument geladen finished************************* "+arg0);	
		/*
		try {
			Reha.officeapplication.getDesktopService().removeDocumentListener(this);
		} catch (OfficeApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	@Override
	public void onModifyChanged(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onMouseOut(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onMouseOver(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onNew(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onNonAlphaCharInput(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSave(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		//System.out.println("************************Dokument gespeichert - doneSave************************* "+arg0);		
	}
	@Override
	public void onSaveAs(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSaveAsDone(IDocumentEvent arg0) {
		System.out.println("saveas Done");
		
	}
	@Override
	public void onSaveDone(IDocumentEvent arg0) {
		System.out.println("save Done");
		// TODO Auto-generated method stub
		//System.out.println("************************Dokument gespeichert - done************************* "+arg0);		
	}
	@Override
	public void onSaveFinished(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		//System.out.println("************************Dokument gespeichert - finished************************* "+arg0);		
	}
	@Override
	public void onUnload(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void disposing(IEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	
}	
	
