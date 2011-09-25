package rtaWissen;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.TimerTask;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.jdesktop.swingx.JXPanel;

import rehaWissen.RehaWissen;
import rehaWissen.SystemConfig;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserNavigationEvent;




public class RtaWissen extends JXPanel implements KeyListener,ComponentListener,ActionListener, AncestorListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3482074172384055074L;
	private int setOben;
	private JXPanel jp1 = null;
	private JWebBrowser webBro = null;
	public static boolean highlight = false; 
	public ActionListener list ;
	
	public RtaWissen(int setOben,String url){
		  super(new BorderLayout(0, 0));

		this.setOben = setOben;
		final String Url = url;
		setLayout(new BorderLayout());
		this.addKeyListener(this);
		
		setBorder(null);
		list = new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
            	if(e.getActionCommand().equals("SourceCode")){
            		neuesSourceFenster();
            	}
            }
        };
		//System.out.println("Pfad = "+RehaWissen.proghome+"xulrunner/");
		
		
		//System.setProperty("nativeswing.webbrowser.runtime","XULRunner");   
		//rtp.addListener((RehaTPEventListener) this);
/**************************************/

		    //final JCheckBox menuBarCheckBox = new JCheckBox("Menu Bar");
		    //final JCheckBox buttonBarCheckBox = new JCheckBox("Button Bar");
		    //final JCheckBox addressBarCheckBox = new JCheckBox("Address Bar");
		    //final JCheckBox statusBarCheckBox = new JCheckBox("Status Bar");
		    //JPanel webBrowserPanel = new JPanel(new BorderLayout(0, 0));
		    //webBrowserPanel.setBorder(BorderFactory.createTitledBorder("Native Web Browser component"));
				//NativeInterface.open();


		   		//System.out.println("NativeInterface = geöffnet");
        		
				        		
       			//final JWebBrowser webBrowser = new JWebBrowser(JWebBrowser.useXULRunnerRuntime());
        		//System.setProperty("nativeswing.webbrowser.runtime",RehaWissen.proghome+"xulrunner");
        		//System.setProperty("nativeswing.webbrowser.xulrunner.home",RehaWissen.proghome+"xulrunner");
				//System.out.println("xulruntime = "+System.getProperty("nativeswing.webbrowser.runtime"));
        		
        		final JWebBrowser webBrowser = new JWebBrowser();
        		//final JWebBrowser webBrowser = new JWebBrowser(JWebBrowser.useXULRunnerRuntime());
        		//JWebBrowser.useXULRunnerRuntime();	
		   			
		   		this.setName("RtaWissen");
		   		//webBrowser.
		   		//webBrowser.setEltern(this,list );

		   		webBrowser.setDoubleBuffered(true);
		   		webBro = webBrowser;
		   		webBrowser.setBackground(Color.WHITE);

		   		
				webBrowser.addWebBrowserListener(new WebBrowserAdapter() {
				  
					/*
				      public void commandReceived(WebBrowserEvent e, String command, String[] args) {   
				        String commandText = command;   
				        if(args.length > 0) {   
				          commandText += " " + Arrays.toString(args);   
				        }   
				        if("store".equals(command)) {   
				          String data = args[0];   
				          if(JOptionPane.showConfirmDialog(webBrowser, "Do you want to store \"" + data + "\" in a database?\n(Not for real of course!)", "Data received from the web browser", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {   
  
				          }   
				        }   
				      }
				      */
		   		
				      public void loadingProgressChanged(WebBrowserEvent e){
				    	  if(highlight){
					    	  String suchen = BrowserFenster.getSuchkriterium();
					    	  if(! suchen.equals("")){
					    		  starteJavaScript(suchen);  
					    	  }
				    	  }
				      }

				      public void locationChanged(WebBrowserNavigationEvent e){
				    	  //System.out.println(e);
				    	  if(highlight){
				    	  }
				    	  
				      }
					
					public void getSourceCode(WebBrowserEvent arg0) {
						// TODO Auto-generated method stub
						//System.out.println("Aufruf des Source-Codes im Listener");
					}
				    });   
				   

     		    webBrowser.validate();
     		    
			   
      		   add(webBrowser,BorderLayout.CENTER);
      		   //NativeInterface.runEventPump();
      			SwingUtilities.invokeLater(new Runnable(){
    				public  void run(){
       	      		   webBrowser.navigate(Url);
       	      		   //String str = webBrowser.getHTMLContent();
       	      		   //System.out.println(str);
    		 	  	}
    			});			

   
      		   //webBrowser.


/*
			webBrowser = new JWebBrowser();
		    webBrowser.setAddressBarVisible(false);
		    webBrowser.setDoubleBuffered(true);
		    webBrowser.setURL(Url);
		    this.add(webBrowser,BorderLayout.CENTER);
*/		    
	}		    
/*******************************************************/			
/*********************************************************/
	
public void FensterSchliessen(String welches){
	//System.out.println("Eltern-->"+this.getParent().getParent().getParent().getParent().getParent());
	//webBrowser.dispose();
	//Reha.thisClass.TPschliessen(setOben,(Object) this.getParent().getParent().getParent().getParent().getParent(),welches);
}
public void Navigiere(String url){
	//System.out.println("Navigiere zu "+url);
	webBro.navigate(url);
	webBro.requestFocus();
	//String str = webBro.getHTMLContent();
	//webBro.executeJavascript(javascript);
	//System.out.println(str);

}

public void starteJavaScript(String high){
	if(! high.trim().equals("")){
		String zusatz = new String("highlightSearchTerms('"+high+"');")	;
				try {
					String javascript = readFileToString(RehaWissen.proghome+"tools/highlight.js");
					javascript = javascript+"\n"+zusatz;
					webBro.executeJavascript(javascript);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

	}
}

public static String readFileToString(String originalFilePath) throws IOException {
    
    FileInputStream fis = new FileInputStream(originalFilePath);
    byte[] buf = new byte[1024];
    int numRead = 0;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    while ((numRead = fis.read(buf)) > 0) {
       baos.write(buf, 0, numRead);
       baos.flush();
       
    }
    fis.close();
    byte[] returnVal = baos.toByteArray();
    baos.close();
    return new String(returnVal);
 }

public void Markiere(String[] fundstelle){
	String str = webBro.getHTMLContent();
	String strmark = str;
	boolean gefunden = false;
	for(int i = 0 ; i < fundstelle.length;i++){
		
		String fs = fundstelle[i];
		if(str.indexOf(fs) >= 0){
			String neuString = "<span bgcolor='#FFFFFF'>"+fs+"</span>";
			strmark = str.replace(fs, neuString);
			gefunden = true;
	
		}
	}
	if(gefunden){
		//webBro.setHTMLContent(strmark);		
	}

	//webBro.executeJavascript(javascript);
	//System.out.println(str);

}


@Override
public void keyPressed(KeyEvent arg0) {
	// TODO Auto-generated method stub
	if(arg0.getKeyCode() == 10){
		arg0.consume();
	}	
}
@Override
public void keyReleased(KeyEvent arg0) {
	// TODO Auto-generated method stub
	if(arg0.getKeyCode() == 10){
		arg0.consume();
	}	
}
@Override
public void keyTyped(KeyEvent arg0) {
	// TODO Auto-generated method stub
	if(arg0.getKeyCode() == 10){
		arg0.consume();
	}	
}
@Override
public void componentHidden(ComponentEvent arg0) {
	// TODO Auto-generated method stub
	
}
@Override
public void componentMoved(ComponentEvent arg0) {
	// TODO Auto-generated method stub
	
}
@Override
public void componentResized(ComponentEvent arg0) {
	// TODO Auto-generated method stub
	webBro.revalidate();
	
}
@Override
public void componentShown(ComponentEvent arg0) {
	// TODO Auto-generated method stub
	
}

class ReloadTask   extends TimerTask
{
@Override   public void run()
  {
  }
}


public void actionPerformed(ActionEvent e) {
	// TODO Auto-generated method stub
	JOptionPane.showMessageDialog(null,"Action Performed");
	//System.out.println("Action performed -> "+e.getActionCommand());
	
}
public static void setQuelltext(){
	//System.out.println("Event ist agekommen");
	JOptionPane.showMessageDialog(null,"Event angekommen");	
}
@Override
public void ancestorAdded(AncestorEvent arg0) {
	System.out.println("Anc1");
	// TODO Auto-generated method stub
	
}
@Override
public void ancestorMoved(AncestorEvent arg0) {
	System.out.println("Anc2");
	// TODO Auto-generated method stub
	
}
@Override
public void ancestorRemoved(AncestorEvent arg0) {
	System.out.println("Anc3");
	// TODO Auto-generated method stub
	
}

public void neuesSourceFenster(){
	  /* Standard main method to try that test as a standalone application. */  
	    NativeInterface.open();   
	    SwingUtilities.invokeLater(new Runnable() {   
	      public void run() {  
	    	StringBuffer buf = new StringBuffer();
	    	buf.append(webBro.getHTMLContent());
	        JDialog frame = new JDialog();   
	        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);   
	        frame.getContentPane().add(new SourceFenster(buf), BorderLayout.CENTER);   
	        frame.setSize(800, 600);   
	        frame.setLocationByPlatform(true);   
	        frame.setVisible(true);   
	      }   
	    });   
	    NativeInterface.runEventPump();   
	  }   
/******************************************/	
}
/******************************************/

interface MeinListener extends ActionListener{
	
	public void actionPerformed(ActionEvent e);
	
	
}