package dialoge;

import java.awt.Container;

import org.jdesktop.swingx.JXTitledPanel;

import events.RehaTPEvent;
import events.RehaTPEventListener;





public interface ISmartDialog extends RehaTPEventListener{
	public PinPanel pinPanel=null;
	public JXTitledPanel jtp = null;
	
	public JXTitledPanel getSmartTitledPanel();
	

	public JXTitledPanel getTitledPanel();
	
	public void setContentPanel(Container cont);
	public void aktiviereIcon();
	public void deaktiviereIcon();
	public void setPinPanel (PinPanel pinPanel);
	public PinPanel getPinPanel ();
	public void setIgnoreReturn(boolean ignore);
	
	public void ListenerSchliessen();
	public void rehaTPEventOccurred(RehaTPEvent evt);


}  //  @jve:decl-index=0:visual-constraint="387,36"
