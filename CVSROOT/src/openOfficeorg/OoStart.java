package openOfficeorg;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;

import rehaContainer.RehaTP;
import terminKalender.TerminFenster;

import hauptFenster.AktiveFenster;
import hauptFenster.ContainerConfig;
import hauptFenster.ProgLoader;
import hauptFenster.Reha;
import hauptFenster.SystemLookAndFeel;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.desktop.DesktopException;
import ag.ion.bion.officelayer.desktop.GlobalCommands;
import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.event.DocumentAdapter;
import ag.ion.bion.officelayer.event.ICloseEvent;
import ag.ion.bion.officelayer.event.ICloseListener;
import ag.ion.bion.officelayer.event.IDocumentEvent;
import ag.ion.bion.officelayer.event.IDocumentListener;
import ag.ion.bion.officelayer.event.IEvent;
import ag.ion.bion.officelayer.internal.event.DocumentListenerWrapper;
import ag.ion.bion.officelayer.text.IParagraph;
import ag.ion.bion.officelayer.text.ITextCursor;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;
import ag.ion.noa.frame.IDispatchDelegate;

import com.sun.star.accessibility.XAccessible;
import com.sun.star.accessibility.XAccessibleComponent;
import com.sun.star.accessibility.XAccessibleContext;
import com.sun.star.accessibility.XAccessibleEventBroadcaster;
import com.sun.star.awt.FocusEvent;
import com.sun.star.awt.Key;
import com.sun.star.awt.KeyEvent;
import com.sun.star.awt.KeyModifier;
import com.sun.star.awt.XExtendedToolkit;
import com.sun.star.awt.XFocusListener;
import com.sun.star.awt.XKeyHandler;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XTopWindow;
import com.sun.star.awt.XTopWindowListener;
import com.sun.star.awt.XWindow;
import com.sun.star.awt.XWindowPeer;
import com.sun.star.frame.XFrame;
import com.sun.star.lang.EventObject;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XInterface;
import com.sun.star.view.DocumentZoomType;

import events.OOEvent;
import events.OOEventClass;
import events.OOEventListener;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;

public class  OoStart extends JXPanel implements RehaTPEventListener, ContainerListener, OOEventListener{
	
private RehaTPEventClass xEvent ;	
private OOEventClass oEvent ;
private int setOben;
public ITextDocument doc = null;
public static IFrame frame = null;
public ICloseListener clListener = null;

private XExtendedToolkit extendedToolkit = null;
private com.sun.star.awt.XTopWindowListener topWindowListener = null;
private XFrame xxframe = null;
private XTopWindow myTopWindow = null;

private OoStart thisClass =  null;
	public OoStart(int setOben){
	super();
	setLayout(new GridLayout());	
	this.setOben = setOben;
	//setName("OpenOffice");
	setBorder(null);
	setVisible(true);
	xEvent = new RehaTPEventClass();
	xEvent.addRehaTPEventListener((RehaTPEventListener)this);
	oEvent = new OOEventClass();
	oEvent.addOOEventListener((OOEventListener)this);	
	RehaDocumentListener rlist = null;
	//this.setName("ooPanel");
	thisClass = this;
	this.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusGained(java.awt.event.FocusEvent e) {    
					//System.out.println("************OoStart focus bekommen");
					if (doc != null){
						//System.out.println(doc.getFrame());
						//frame.setFocus();
						//doc.getFrame().setFocus();
					}	
			}   
			public void focusLost(java.awt.event.FocusEvent e) {    
					//System.out.println("************OoStart focus verloren");	
				}
		});	
	}
	public void setzeOO(){
	//Reha.thisFrame.add(this);
	try
    {
        System.out.println("Frame erstellen");
    	frame = Reha.officeapplication.getDesktopService().constructNewOfficeFrame(this);
        System.out.println("Dokument erstellt");       
        doc = (ITextDocument)Reha.officeapplication.getDocumentService().constructNewDocument(frame,"NeuesDokument", DocumentDescriptor.DEFAULT);
        System.out.println("Zoom auf OPTIMAL einstellen");
        doc.zoom(DocumentZoomType.OPTIMAL, (short)0);
        ITextCursor cursor = doc.getTextService().getCursorService().getTextCursor();
        //IParagraph para = doc.getTextService().getTextContentService().constructNewParagraph();
        System.out.println("Cursor gestartet");
        //doc.getTextService().getTextContentService().insertTextContent(cursor.getStart(),para);

        //doc.addCloseListener((ICloseListener) new RehaDocumentCloseListener(Reha.officeapplication));
/********/
        XWindowPeer myWindowPeer = (XWindowPeer) 
        UnoRuntime.queryInterface (XWindowPeer.class,doc.getFrame().getXFrame().getContainerWindow()); 
        XToolkit myToolkit = myWindowPeer.getToolkit(); 
        XExtendedToolkit myExtToolkit = (XExtendedToolkit) 
        UnoRuntime.queryInterface (XExtendedToolkit.class, myToolkit);
/*******/        
        //XKeyHandler xKeyHandler = new XKeyHandler();
        myExtToolkit.addKeyHandler(new XKeyHandler() {
        public boolean keyPressed(KeyEvent k) {
          if(k.Modifiers == KeyModifier.MOD1 && k.KeyCode == Key.LEFT) {
        	  //System.out.println("Strg+PFEIL-LEFT");
        	  //Reha.jContainerUnten.requestFocus(true);
        	  Reha.thisClass.setDivider(1);
          }else if(k.Modifiers == KeyModifier.MOD1 && k.KeyCode == Key.RIGHT){
              //System.out.println("Strg+PFEIL-RECHTS");
              //Reha.jContainerUnten.requestFocus(true);
              Reha.thisClass.setDivider(2);
          }else if(k.Modifiers == KeyModifier.MOD1 && k.KeyCode == Key.UP){
              //System.out.println("Strg+PFEIL-AUF");
              //Reha.jContainerUnten.requestFocus(true);
              Reha.thisClass.setDivider(3);
          }else if(k.Modifiers == KeyModifier.MOD1 && k.KeyCode == Key.DOWN){
              //System.out.println("Strg+PFEIL-AB");
              //Reha.jContainerUnten.requestFocus(true);
              Reha.thisClass.setDivider(4);
          }else if(k.Modifiers == KeyModifier.MOD1 && k.KeyCode == Key.T) {
            //System.out.println("Strg+F7");
            //Reha.jInhaltUnten.requestFocus();
            SwingUtilities.invokeLater(new Runnable(){
            	   public  void run()
            	   {
            		   JComponent termin = AktiveFenster.getFensterAlle("TerminFenster");
           				if(termin != null){
           					Reha.thisClass.setDivider(6);
           					((JXTitledPanel) termin).getContentContainer().requestFocusInWindow();
           				}else{
           					Reha.thisClass.setDivider(6);
           					ProgLoader.ProgTerminFenster(2,0);
           				}
            	   }
            	});
          }
          return false;
        }
        public boolean keyReleased(KeyEvent arg0) {return false;}
        public void disposing(EventObject arg0) {}

      }); 
        final XWindow myWindow = (XWindow)
        UnoRuntime.queryInterface (XWindow.class,doc.getFrame().getXFrame().getContainerWindow());
        myWindow.addFocusListener(new XFocusListener(){
        	public void focusGained(FocusEvent arg0) {
        		SwingUtilities.invokeLater(new Runnable(){
        			public  void run()
        		{
   				System.out.println("addFocusListener Focus bekommen - Icon aktiviert");        				
        		if (thisClass.setOben > 0){  
        			((RehaTP)thisClass.getParent()).aktiviereIcon();
        		}

   				}
        	});

        		return;
        	}
        	public void focusLost(FocusEvent arg0) {
        		System.out.println("add focus Listener FocusEvent Focus verloren");
	            SwingUtilities.invokeLater(new Runnable(){
	            	   public  void run()
	            	   {
	          				System.out.println("addFocusListener Focus verloren - Icon deaktiviert");
		            		if (thisClass.setOben > 0){  
		            			((RehaTP)thisClass.getParent()).deaktiviereIcon();
		            		}

	            	 }
	            	});
        		return;			
        	}
        	public void disposing(EventObject arg0) {
        		System.out.println("FocusEvent Meldung Disposing eingegangen");
	            SwingUtilities.invokeLater(new Runnable(){
	            	   public  void run()
	            	   {
	               		RehaTPEvent rEvt = new RehaTPEvent(thisClass);
	        			rEvt.setRehaEvent("PinPanelEvent");
	        			rEvt.setDetails(thisClass.getName(),"ROT") ;
	        			RehaTPEventClass.fireRehaTPEvent(rEvt);
	            	   }
	            	});
        		return;
        	}
        });
        
        /*
		doc.addDocumentListener(new DocumentAdapter() {
			public void onFocus(IDocumentEvent documentEvent) {
				System.out.println("focusGained2");
				System.out.println("Dokument Evetn = "+documentEvent);
			}
		});   
		
		doc.addCloseListener(new ICloseListener() {
			
			@Override
			public void notifyClosing(ICloseEvent arg0) {
				System.out.println("Dokument wurde Geschlossen");
				
				doc.removeCloseListener(clListener);
				// TODO Auto-generated method stub
				
			}

			@Override
			public void queryClosing(ICloseEvent arg0, boolean arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void disposing(IEvent arg0) {
				// TODO Auto-generated method stub
				
			}

		});  
		*/     
      
		/********/
/******************************************************************************************/       
	    
	   UnoRuntime.queryInterface (XWindowPeer.class,doc.getFrame().getXFrame()); 

       XToolkit myTool = myWindowPeer.getToolkit(); 
       //XToolkit myTool = myWinPeer.getToolkit();
       XExtendedToolkit myExtToolk = (XExtendedToolkit) 
       UnoRuntime.queryInterface (XExtendedToolkit.class, myTool);	   
	   
        myTopWindow = (XTopWindow) 
        UnoRuntime.queryInterface (XTopWindow.class,myExtToolk.getActiveTopWindow());
        topWindowListener = new XTopWindowListener(){
        //myTopWindow.addTopWindowListener(new XTopWindowListener() {
        //myTopWindow.addTopWindowListener(new XTopWindowListener() {

			@Override
			public void windowActivated(EventObject arg0) {
				// TODO Auto-generated method stub
				System.out.println("OOo-TopWin aktiviert");
	            SwingUtilities.invokeLater(new Runnable(){
	            	   public  void run()
	            	   {
	            		  System.out.println("OOFenster-Name = "+thisClass.getName());
	            		 
	            		  //frame.setFocus();
	            		if (thisClass.setOben > 0){  
	            			((RehaTP)thisClass.getParent()).aktiviereIcon();
	            		}else{
	            			//((RehaTP)thisClass.getParent().getParent().getParent().getParent().getParent()).aktiviereIcon();
	            		}
	       				}
	            	});
			}

			@Override
			public void windowClosed(EventObject arg0) {
				// TODO Auto-generated method stub
				if(topWindowListener != null){
					myTopWindow.removeTopWindowListener(topWindowListener);
					topWindowListener = null;	
				}
				System.out.println("********************OOo-TopWin geschlossen");				
				
			}

			@Override
			public void windowClosing(EventObject arg0) {
				// TODO Auto-generated method stub
				System.out.println("der ganze Frame ----> OOo-TopWin soll geschlossen werden");

			}

			@Override
			public void windowDeactivated(EventObject arg0) {
				// TODO Auto-generated method stub
				System.out.println("OOo-TopWin deaktiviert");	
	            SwingUtilities.invokeLater(new Runnable(){
	            	   public  void run()
	            	   {
	            		  System.out.println("OOFenster-Name = "+thisClass.getName());
		            		if (thisClass.setOben > 0){  
		            			((RehaTP)thisClass.getParent()).deaktiviereIcon();
		            		}else{
		            			//((RehaTP)thisClass.getParent().getParent().getParent().getParent().getParent()).deaktiviereIcon();
		            		}
	            	   }
	            	});
			}

			@Override
			public void windowMinimized(EventObject arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowNormalized(EventObject arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowOpened(EventObject arg0) {
				// TODO Auto-generated method stub
				System.out.println("OOo-TopWin geöffnet");				
				
			}

			@Override
			public void disposing(EventObject arg0) {
				// TODO Auto-generated method stub
				System.out.println("Der komplette Frame OOo-TopWin disposing (der ganze Frame.....)");	
				myTopWindow.removeTopWindowListener(topWindowListener);
				topWindowListener = null;
			}
        	
        };
/******************************************************************************************/        
        
/*******/        

/********/        
		
        this.validate();
       
        //para.getParagraphProperties().setParaStyleName("Title");
        //para.setParagraphText("Hallo, ich bin Dokument "+title);
   
    }
    catch (DesktopException e)
    {
        // TODO Auto-generated catch block
    	System.out.println("DesktopException");
        e.printStackTrace();
    }
    catch (OfficeApplicationException e)
    {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    catch (NOAException e)
    {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    catch (DocumentException e)
    {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
	/*
    catch (TextException e)
    {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
	*/
    this.revalidate();
    myTopWindow.addTopWindowListener(topWindowListener);

	}
	
	
	public void dokumenteSchliessen(){
    	IDocument[] docs;
    		try {
				int len = (docs = Reha.officeapplication.getDocumentService().getCurrentDocuments()).length;
				System.out.println("Anzahl Dokumente = "+len);
				int i;
				for(i=len-1;i>=0;i--){
					
					Reha.officeapplication.getDesktopService().dispose();
					if (Reha.officeapplication.getDocumentService().getCurrentDocuments()[0] != null){
						Reha.officeapplication.getDocumentService().getCurrentDocuments()[0].close();
						System.out.println("Fenster "+i+" geschlossen");
					}	
						
				}
			} catch (OfficeApplicationException e) {
				e.printStackTrace();
			} catch (DocumentException e1) {
			e1.printStackTrace();
		}
    }
	@Override
	public void RehaTPEventOccurred(RehaTPEvent evt) {
		try{
		if (evt.getDetails()[0].equals(this.getName()) && evt.getDetails()[1].equals("ROT")){
			
			dokumenteSchliessen();
			myTopWindow.removeTopWindowListener(topWindowListener);
			String fname = evt.getDetails()[0];
			xEvent.removeRehaTPEventListener((RehaTPEventListener)this);
			oEvent.removeOOEventListener((OOEventListener)this);	
			//Reha.thisClass.TPschliessen(setOben,null,fname);
			System.out.println("RehaTPEvent - OpenOffice für "+fname);
		}else if (evt.getDetails()[0].equals(this.getName()) && evt.getDetails()[1]=="GRUEN"){
			if(setOben == 0){
				ContainerConfig conf = new ContainerConfig();
				conf.addContainer("personen16.gif",evt.getDetails()[0],this.getParent().getParent().getParent().getParent().getParent(),null);
				System.out.println("Name für Container verkleinern = "+this.getName());
			}	

		}
		}catch(NullPointerException ne){
			System.out.println(evt);
		}
		
	}
	@Override
	public void componentAdded(ContainerEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("OOo Komponente hinzugefügt");		
		
	}
	@Override
	public void componentRemoved(ContainerEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("OOo Komponente removed");
	}
	@Override
	public void OOEventOccurred(OOEvent evt) {
		// TODO Auto-generated method stub
		if (evt.getDetails()[0].equals("RequestFocus") ){
			SwingUtilities.invokeLater(new Runnable(){
         	   public  void run()
         	   {
         		   System.out.println("OOEvent Klasse = "+thisClass.getName());
         		   if (thisClass.setOben > 0){  
         			   ((RehaTP)thisClass.getParent()).aktiviereIcon();
         			   try{
         				   doc.getFrame().setFocus();
         			   }catch(com.sun.star.lang.DisposedException ex){
         				   frame.setFocus();
         			   }
         		  }else{
         			//((RehaTP)thisClass.getParent().getParent().getParent().getParent().getParent()).aktiviereIcon();
         		   }
    			}
         	});
			
		}
		
	}
	
	

}
