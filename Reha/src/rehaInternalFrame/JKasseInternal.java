
package rehaInternalFrame;

import hauptFenster.AktiveFenster;
import hauptFenster.Reha;

import java.beans.PropertyVetoException;

import javax.swing.ImageIcon;
import javax.swing.event.InternalFrameEvent;

import krankenKasse.KassenPanel;
import events.RehaEvent;
import events.RehaEventClass;
import events.RehaEventListener;

public class JKasseInternal extends JRehaInternal implements RehaEventListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 538247311504244175L;
	RehaEventClass rEvent = null;
	public JKasseInternal(String titel, ImageIcon img, int desktop) {
		super(titel, img, desktop);
		rEvent = new RehaEventClass();
		rEvent.addRehaEventListener((RehaEventListener) this);
	}
	@Override
	public void internalFrameClosing(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
		//System.out.println("Internal-KassenFrame in schliessen***************");
	}
	@Override
	public void internalFrameClosed(InternalFrameEvent arg0) {
		Reha.thisClass.desktops[this.desktop].remove(this);
		this.removeInternalFrameListener(this);
		Reha.thisFrame.requestFocus();
		//System.out.println("Lösche KasseInternal von Desktop-Pane = "+Reha.thisClass.desktops[this.desktop]);
		Reha.thisClass.desktops[this.desktop].remove(this);
		Reha.thisClass.aktiviereNaechsten(this.desktop);
		rEvent.removeRehaEventListener((RehaEventListener) this);
		this.nord = null;
		this.inhalt = null;
		this.thisContent = null;
		this.dispose();
		super.dispose();
		AktiveFenster.loescheFenster(this.getName());
		Reha.thisClass.progLoader.loescheKasse();

	}
	public void setzeTitel(String stitel){
		super.setzeTitel(stitel);
		repaint();
		
	}
	public void starteKasseID(String aID){
		if(aID.equals("")){return;}
		((KassenPanel)inhalt).holeAktKasse(aID);
		//KassenPanel.thisClass.holeAktKasse(aID);
	}
	@Override
	public void rehaEventOccurred(RehaEvent evt) {
		if(evt.getRehaEvent().equals("REHAINTERNAL")){
			//System.out.println("es ist ein Reha-Internal-Event");
		}
		if(evt.getDetails()[0].equals(this.getName())){
			if(evt.getDetails()[1].equals("#ICONIFIED")){
				try {
					this.setIcon(true);
					isIcon = true;
				} catch (PropertyVetoException e) {
					e.printStackTrace();
				}
				this.setActive(false);
			}
		}
		if(evt.getDetails()[0].equals(this.getName())){
			if(evt.getDetails()[1].equals("#DEICONIFIED")){
				try {
					this.setIcon(false);
					isIcon = false;
				} catch (PropertyVetoException e) {
					e.printStackTrace();
				}
				this.setActive(true);
				repaint();
			}
		}
	}
	
}
