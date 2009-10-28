
package RehaInternalFrame;

import hauptFenster.AktiveFenster;
import hauptFenster.Reha;
import hauptFenster.SuchenDialog;

import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyVetoException;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleValue;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.JComponent.AccessibleJComponent;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import krankenKasse.KassenPanel;

import arztFenster.ArztPanel;

import events.PatStammEvent;
import events.PatStammEventClass;
import events.RehaEvent;
import events.RehaEventClass;
import events.RehaEventListener;

import terminKalender.TerminFenster;

public class JKasseInternal extends JRehaInternal implements RehaEventListener{
	RehaEventClass rEvent = null;
	public JKasseInternal(String titel, ImageIcon img, int desktop) {
		super(titel, img, desktop);
		rEvent = new RehaEventClass();
		rEvent.addRehaEventListener((RehaEventListener) this);
	}
	@Override
	public void internalFrameClosing(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("Internal-KassenFrame in schliessen***************");
	}
	@Override
	public void internalFrameClosed(InternalFrameEvent arg0) {
		Reha.thisClass.desktops[this.desktop].remove(this);
		this.removeInternalFrameListener(this);
		Reha.thisFrame.requestFocus();
		System.out.println("Lösche KasseInternal von Desktop-Pane = "+Reha.thisClass.desktops[this.desktop]);
		Reha.thisClass.desktops[this.desktop].remove(this);
		Reha.thisClass.aktiviereNaechsten(this.desktop);
		rEvent.removeRehaEventListener((RehaEventListener) this);
		this.nord = null;
		this.inhalt = null;
		this.thisContent = null;
		this.dispose();
		super.dispose();
		AktiveFenster.loescheFenster(this.getName());
		/*
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run()
		 	   {
		 			Runtime r = Runtime.getRuntime();
		 		    r.gc();
		 		    long freeMem = r.freeMemory();
		 		    System.out.println("Freier Speicher nach  gc():    " + freeMem);
		 	   }
		});
		*/

	}
	public void setzeTitel(String stitel){
		super.setzeTitel(stitel);
		repaint();
		
	}
	public void starteKasseID(String aID){
		if(aID.equals("")){return;}
		KassenPanel.thisClass.holeAktKasse(aID);
	}
	@Override
	public void rehaEventOccurred(RehaEvent evt) {
		if(evt.getRehaEvent().equals("REHAINTERNAL")){
			System.out.println("es ist ein Reha-Internal-Event");
		}
		if(evt.getDetails()[0].equals(this.getName())){
			if(evt.getDetails()[1].equals("#ICONIFIED")){
				try {
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
