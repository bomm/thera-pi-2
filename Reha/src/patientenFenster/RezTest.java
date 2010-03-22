package patientenFenster;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import systemTools.ListenerTools;
import dialoge.RehaSmartDialog;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;

public class RezTest extends RehaSmartDialog implements RehaTPEventListener,WindowListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5410743025474817628L;
	private RehaTPEventClass rtp = null;
	public RezTest(){
		super(null,"RezeptTest");
		this.setName("RezeptTest");
		//super.getPinPanel().setName("RezeptNeuanlage");
		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener((RehaTPEventListener) this);

	}
	public void rehaTPEventOccurred(RehaTPEvent evt) {
		try{
			if(evt.getDetails()[0] != null){
				if(evt.getDetails()[0].equals(this.getName())){
					System.out.println("In rezNeuDlg set Visible false***************");
					this.setVisible(false);
					this.dispose();
					rtp.removeRehaTPEventListener((RehaTPEventListener) this);
					rtp = null;
					ListenerTools.removeListeners(this);					
					super.dispose();
					System.out.println("****************RezeptTest -> Listener entfernt**************");				
				}
			}
		}catch(NullPointerException ne){
			System.out.println("In RezeptNeuanlage" +evt);
		}
	}
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		if(rtp != null){
			this.setVisible(false);			
			rtp.removeRehaTPEventListener((RehaTPEventListener) this);		
			rtp = null;
			dispose();
			ListenerTools.removeListeners(this);
			super.dispose();
			System.out.println("****************RezeptTest -> Listener entfernt (Closed)**********");
		}
	}
}