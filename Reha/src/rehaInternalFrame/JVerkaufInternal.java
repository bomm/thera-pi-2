package rehaInternalFrame;

import hauptFenster.AktiveFenster;
import hauptFenster.Reha;

import java.beans.PropertyVetoException;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameEvent;

import events.RehaEvent;
import events.RehaEventClass;
import events.RehaEventListener;

public class JVerkaufInternal extends JRehaInternal implements RehaEventListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2103241492769177163L;
	RehaEventClass rEvent = null;
	public JVerkaufInternal(String titel, ImageIcon img, int desktop) {
		super(titel, img, desktop);
		rEvent = new RehaEventClass();
		rEvent.addRehaEventListener((RehaEventListener) this);
	}
	@Override
	public void internalFrameClosing(InternalFrameEvent arg0) {
		//System.out.println("Internal-Verkauf in schliessen***************");
	}
	@Override
	public void internalFrameClosed(InternalFrameEvent arg0) {
		//System.out.println("Lösche VerkaufInternal von Desktop-Pane = "+Reha.thisClass.desktops[this.desktop]);
		//JInternalFram von Desktop lösen
		Reha.thisClass.desktops[this.desktop].remove(this);
		//nächsten JInternalFrame aktivieren
		Reha.thisClass.aktiviereNaechsten(this.desktop);		
		//Listener deaktivieren
		rEvent.removeRehaEventListener((RehaEventListener) this);
		this.removeInternalFrameListener(this);
		//
		Reha.thisFrame.requestFocus();
		//Componenten des InternalFrameTitelbar auf null setzen
		this.destroyTitleBar();
		this.nord = null;
		this.inhalt = null;
		this.thisContent = null;
		this.dispose();
		final String name = this.getName();


		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run()
		 	   {
				AktiveFenster.loescheFenster(name);
				Reha.thisClass.progLoader.loescheVerkauf();
		 	   }
		});


	}
	public void setzeTitel(String stitel){
		super.setzeTitel(stitel);
		repaint();
		
	}
	@Override
	public void rehaEventOccurred(RehaEvent evt) {
		if(evt.getRehaEvent().equals("REHAINTERNAL")){

		}
		if(evt.getDetails()[0].equals(this.getName())){
			if(evt.getDetails()[1].equals("#ICONIFIED")){
				try {
					this.setIcon(true);
				} catch (PropertyVetoException e) {
					e.printStackTrace();
				}
				this.setActive(false);
			}else if(evt.getDetails()[1].equals("#FRAMESCHLIESSEN")){
				Reha.thisClass.verkaufpanel.removeListeners();

			}

		}
		
	}
	
}
