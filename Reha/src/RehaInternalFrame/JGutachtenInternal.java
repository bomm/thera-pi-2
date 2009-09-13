package RehaInternalFrame;

import hauptFenster.AktiveFenster;
import hauptFenster.Reha;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.event.InternalFrameEvent;

import entlassBerichte.EBerichtPanel;
import events.RehaEvent;
import events.RehaEventClass;

import arztFenster.ArztPanel;

public class JGutachtenInternal extends JRehaInternal {

	public JGutachtenInternal(String titel, ImageIcon img, int desktop) {
		super(titel, img, desktop);
		// TODO Auto-generated constructor stub
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
	}
	public void setzeTitel(String stitel){
		super.setzeTitel(stitel);
		repaint();
		
	}
	public void starteArztID(String aID){
		if(aID.equals("")){return;}
		//ArztPanel.thisClass.holeAktArzt(aID);
	}
	

}
