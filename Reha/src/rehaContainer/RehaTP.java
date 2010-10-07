package rehaContainer;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;

import org.jdesktop.swingx.JXTitledPanel;

import dialoge.PinPanel;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;

public class RehaTP extends JXTitledPanel implements RehaTPEventListener,FocusListener,MouseListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2594000954785231427L;
	/**
	 * 
	 */
	
	RehaTPEventClass rEvent = null;
	private RehaTP thisClass = null;
	public int setOben; 
	private PinPanel pinPanel = null;
	public String eigenerName = ""; 
	public RehaTP(int setOben){
		super();
		thisClass = this;
		this.setTitleForeground(Color.WHITE);
		this.addFocusListener(this);
		this.addMouseListener(this);
		this.setOben = setOben;
		/*
		this.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent e) {
			}
		});
		*/	
			
		this.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusGained(java.awt.event.FocusEvent e) {    
				if(thisClass.pinPanel != null){
					thisClass.pinPanel.SetzeAktivButton(true);
					RehaTPEvent tPEvent = new RehaTPEvent(this);   
					tPEvent.setRehaEvent("FocusRequest");  
					tPEvent.setDetails(thisClass.getName(), "RequestFocus");
					RehaTPEventClass.fireRehaTPEvent(tPEvent);
				}
			}   
			public void focusLost(java.awt.event.FocusEvent e) {    
				if(thisClass.pinPanel != null){
					thisClass.pinPanel.SetzeAktivButton(false);
				}
			}   
		});	



	}
	public void setzePinPanel(){
		
	}
	public void setzeName(String sname){
		this.setName(sname);
		this.eigenerName = sname;
		this.getContentContainer().setName(sname);
		if (this.pinPanel != null){
			this.pinPanel.setzeName(sname);			
		}
	}
	public void aktiviereIcon(){
		if(pinPanel != null){
			pinPanel.SetzeAktivButton(true);			
		}
	}
	public void deaktiviereIcon(){
		if(pinPanel != null){
			pinPanel.SetzeAktivButton(false);
		}	
	}	
	public void setPinPanel (PinPanel pinPanel){
		this.pinPanel = pinPanel;
	}
	public JComponent getPinPanel (){
		return this.pinPanel;
	}

	@Override
	public void rehaTPEventOccurred(RehaTPEvent evt) {
	}
	public void setStandort(String fenster,int setOben){
		RehaTPEvent rEvt = new RehaTPEvent(this);
		rEvt.setRehaEvent("ChangeLocation");
		rEvt.setDetails(fenster,Integer.toString(setOben)) ;
	}
	@Override
	public void focusGained(FocusEvent arg0) {
	}
	@Override
	public void focusLost(FocusEvent arg0) {
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		thisClass.pinPanel.SetzeAktivButton(true);
		RehaTPEvent tPEvent = new RehaTPEvent(this);   
		tPEvent.setRehaEvent("FocusRequest");  
		tPEvent.setDetails(thisClass.getName(), "RequestFocus");
		RehaTPEventClass.fireRehaTPEvent(tPEvent);
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
}
