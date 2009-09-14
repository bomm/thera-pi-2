package RehaInternalFrame;

import java.beans.PropertyVetoException;

import hauptFenster.AktiveFenster;
import hauptFenster.Reha;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.event.InternalFrameEvent;

import entlassBerichte.EBerichtPanel;
import events.RehaEvent;
import events.RehaEventClass;
import events.RehaEventListener;

import arztFenster.ArztPanel;

public class JGutachtenInternal extends JRehaInternal implements RehaEventListener {
	RehaEventClass rEvent = null;
	public JGutachtenInternal(String titel, ImageIcon img, int desktop) {
		super(titel, img, desktop);
		rEvent = new RehaEventClass();
		rEvent.addRehaEventListener((RehaEventListener) this);
	}
	@Override
	public void internalFrameClosing(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("Internal-GutachtenFrame in schliessen***************");
	}
	@Override
	public void internalFrameClosed(InternalFrameEvent arg0) {
		Reha.thisClass.desktops[this.desktop].remove(this);
		AktiveFenster.loescheFenster(this.getName());
		RehaEvent evt = new RehaEvent(this);
		evt.setDetails(this.getName(), "#SCHLIESSEN");
		evt.setRehaEvent("Gutachten");
		RehaEventClass.fireRehaEvent(evt);
		
		//((EBerichtPanel)((JComponent)getComponent())).dokumentSchliessen();
		this.removeInternalFrameListener(this);
		Reha.thisFrame.requestFocus();
		System.out.println("Lösche GutachtenInternal von Desktop-Pane = "+Reha.thisClass.desktops[this.desktop]);
		Reha.thisClass.aktiviereNaechsten(this.desktop);
		rEvent.removeRehaEventListener((RehaEventListener) this);		
	}
	public void setzeTitel(String stitel){
		super.setzeTitel(stitel);
		repaint();
		
	}
	public void starteArztID(String aID){
		if(aID.equals("")){return;}
		//ArztPanel.thisClass.holeAktArzt(aID);
	}
	@Override
	public void RehaEventOccurred(RehaEvent evt) {
		if(evt.getRehaEvent().equals("REHAINTERNAL")){
			System.out.println("es ist ein Reha-Internal-Event");
		}
		if(evt.getDetails()[0].equals(this.getName())){
			if(evt.getDetails()[1].equals("#ICONIFIED")){
				try {
					RehaEvent xevt = new RehaEvent(this);
					xevt.setDetails(this.getName(), "#TRENNEN");
					xevt.setRehaEvent("OOFrame");
					RehaEventClass.fireRehaEvent(xevt);
					
					this.setIcon(true);
				} catch (PropertyVetoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.setActive(false);
			}
		}
	}		
	

}
