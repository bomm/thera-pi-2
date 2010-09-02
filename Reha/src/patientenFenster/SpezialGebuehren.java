package patientenFenster;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXFrame;

import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;

public class SpezialGebuehren extends RehaSmartDialog implements RehaTPEventListener,WindowListener, ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RehaTPEventClass rtp = null;

	public SpezialGebuehren(JXFrame owner, String name) {
		super(null,"SpezialGebuehr");		
		setName("SpezialGebuehr");
		PinPanel pinPanel = new PinPanel();
		pinPanel.setName("SpezialGebuehr");
		pinPanel.getGruen().setVisible(false);
		setPinPanel(pinPanel);
		getSmartTitledPanel().setTitle("SpezialGebuehr");	
		setSize(175,250);
		setPreferredSize(new Dimension(175,250));
		getSmartTitledPanel().setPreferredSize(new Dimension (175,250));
		setPinPanel(pinPanel);
		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener((RehaTPEventListener) this);
		pack();
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run()
		 	   {
		 		   setVisible(true);
		 	   }
		});
		
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	/*********************************************/
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		if(rtp != null){
			this.setVisible(false);			
			rtp.removeRehaTPEventListener((RehaTPEventListener) this);		
			rtp = null;
			super.dispose();
			dispose();
			//System.out.println("****************SpezialGebuehr-> Listener entfernt**************");
		}
		
		
	}
	public void rehaTPEventOccurred(RehaTPEvent evt) {
		// TODO Auto-generated method stub
		try{
			if(evt.getDetails()[0] != null){
				if(evt.getDetails()[0].equals(this.getName())){
					this.setVisible(false);
					rtp.removeRehaTPEventListener((RehaTPEventListener) this);
					rtp = null;
					super.dispose();
					this.dispose();
					//System.out.println("****************SpezialGebuehr-> Listener entfernt**************");				
				}
			}
		}catch(NullPointerException ne){
			//System.out.println("In PatNeuanlage" +evt);
		}
	}
	

}
