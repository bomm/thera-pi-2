package systemEinstellungen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class SysUtilKostentraeger extends JXPanel implements KeyListener, ActionListener {
	public SysUtilKostentraeger(){
		super(new BorderLayout());
		System.out.println("Aufruf SysUtilKostentraeger");
		this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 20));
		/****/
	     Point2D start = new Point2D.Float(0, 0);
	     Point2D end = new Point2D.Float(400,500);
	     float[] dist = {0.0f, 0.5f};
	     Color[] colors = {Color.WHITE,getBackground()};
	     LinearGradientPaint p =
	         new LinearGradientPaint(start, end, dist, colors);
	     MattePainter mp = new MattePainter(p);
	     setBackgroundPainter(new CompoundPainter(mp));
		/****/
	     add(getVorlagenSeite());
	     try {
			starteSession("","");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}
	/************** Beginn der Methode f�r die Objekterstellung und -platzierung *********/
	private JPanel getVorlagenSeite(){
        //                                      1.            2.    3.    4.     5.     6.    7.      8.     9.
		FormLayout lay = new FormLayout("right:max(60dlu;p), 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu",
       //1.    2. 3.   4.   5.   6.     7.    8. 9.  10.  11. 12. 13.  14.  15. 16.  17. 18.  19.   20.    21.   22.   23.
		"p, 2dlu, p, 10dlu,10dlu,10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p,  10dlu ,10dlu, 10dlu, p");
		
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		//CellConstraints cc = new CellConstraints();
		
		return builder.getPanel();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	private void starteSession(String land,String jahr) throws IOException{
		String urltext = "http://www.gkv-datenaustausch.de/Leistungserbringer_Sole_Kostentraegerdateien.gkvnet";
		String text = null;
		URL url = new URL(urltext);
		   
		      URLConnection conn = url.openConnection();
		      //System.out.println(conn.getContentEncoding());
		      

		      BufferedReader inS = new BufferedReader( new InputStreamReader( conn.getInputStream() ));
		      int durchlauf = 0;
		      Vector ktraegerdat = new Vector();
		      Vector kassendat = new Vector();
		      int index;
		      boolean gestartet = false;
		      while ( (text  = inS.readLine())!= null ) {
		    	  
		    	  if(durchlauf > 0){
		        	  if(text.indexOf("<h2>Kostentr") >= 0){
		        		  kassendat.clear();
		        		  gestartet = true;
		        		  text = text.replace("<h2>", "");
		        		  text = text.replace("</h2>", "");
		        		  kassendat.add(text.trim());
		        		  continue;
		        	  }
		        	  if( (index = text.indexOf("\">gültig")) >= 0){
		        		  text = text.substring(index+2);
		        		  text = text.replace("</span>", "");
		        		  kassendat.add(text.trim());
		        		  continue;
		        	  }
		        	  if( ((index = text.indexOf("href=\"/upload/")) >= 0) && (gestartet)  ){
		        		  text = text.substring(index+6);
		        		  text = text.substring(0,text.indexOf("\""));
		        		  kassendat.add(text.trim());
		        		  ktraegerdat.add(kassendat.clone());
		        		  gestartet = false;
		        		  continue;
		        	  }
		        	  

		          }
		          ++durchlauf;
		      }
		inS.close();
		for(int i = 0; i < ktraegerdat.size();i++){
			System.out.println(ktraegerdat.get(i));	
		}
		
	}
	

}
