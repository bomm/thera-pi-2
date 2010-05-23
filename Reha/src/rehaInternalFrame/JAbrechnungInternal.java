package rehaInternalFrame;

import hauptFenster.AktiveFenster;
import hauptFenster.Reha;

import java.awt.event.FocusListener;
import java.beans.PropertyVetoException;

import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameEvent;

import events.RehaEvent;
import events.RehaEventClass;
import events.RehaEventListener;

public class JAbrechnungInternal extends JRehaInternal implements FocusListener, RehaEventListener{
		/**
	 * 
	 */
	private static final long serialVersionUID = -4989326440978535166L;
		RehaEventClass rEvent = null;
		public JAbrechnungInternal(String titel, ImageIcon img, int desktop) {
			super(titel, img, desktop);
			this.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
			rEvent = new RehaEventClass();
			rEvent.addRehaEventListener((RehaEventListener) this);
		}
		@Override
		public void internalFrameClosed(InternalFrameEvent arg0) {
			//System.out.println("JInternalFrame-Kassenabrechnung aufräumen");
			Reha.thisClass.aktiviereNaechsten(this.desktop);
			Reha.thisClass.desktops[this.desktop].remove(this);
			rEvent.removeRehaEventListener(this);
			removeFocusListener(this);
			this.removeAncestorListener(this);
			final String name = this.getName();

			SwingUtilities.invokeLater(new Runnable(){
			 	   public  void run()
			 	   {
					AktiveFenster.loescheFenster(name);
					Reha.thisClass.progLoader.loescheAbrechnung();
			 	   }
			});
			//AktiveFenster.loescheFenster("Abrechnung");
			rEvent = null;


		}
		@Override
		public void rehaEventOccurred(RehaEvent evt) {
			if(evt.getRehaEvent().equals("REHAINTERNAL")){
				////System.out.println("es ist ein Reha-Internal-Event");
			}
			if(evt.getDetails()[0].equals(this.getName())){
				if(evt.getDetails()[1].equals("#ICONIFIED")){
					try {
						this.setIcon(true);
					} catch (PropertyVetoException e) {
						e.printStackTrace();
					}
					this.setActive(false);
				}
			}
		}
		

}
