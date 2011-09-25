package rtaWissen;



import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.jdesktop.swingx.JXPanel;


public class BrowserPanel extends JXPanel implements HyperlinkListener{
	
	int setOben;
	private JEditorPane htmlPane;

	
	public BrowserPanel(int setOben,String url) {
		this.setOben = setOben;
		final String Url = url;
		setLayout(new BorderLayout());
		setBorder(null);
		
		add(init(Url));

	}
	private JScrollPane init(String url){
		JScrollPane scrollPane = null;;
	    try {
	        htmlPane = new JEditorPane(url);
	        htmlPane.setEditable(false);
	        htmlPane.addHyperlinkListener(this);
	        scrollPane = new JScrollPane(htmlPane);
	        
	    } catch(IOException ioe) {
	       warnUser("Kann keine HTML-Pane machen für die Adresse: " + url 
	                + ": " + ioe);
	    }

	    return scrollPane;
	}

	@Override
	  public void hyperlinkUpdate(HyperlinkEvent event) {
	    if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
	      try {
	        htmlPane.setPage(event.getURL());
	        //urlField.setText(event.getURL().toExternalForm());
	      } catch(IOException ioe) {
	        warnUser("Kann Link nicht folgen " 
	                 + event.getURL().toExternalForm() + ": " + ioe);
	      }
	    }
	  }
	public void FensterSchliessen(String welches){

	}


	 private void warnUser(String message) {
		    JOptionPane.showMessageDialog(this, message, "Error", 
		                                  JOptionPane.ERROR_MESSAGE);
		  }


}
