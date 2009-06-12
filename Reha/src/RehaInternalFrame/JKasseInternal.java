
package RehaInternalFrame;

import hauptFenster.AktiveFenster;
import hauptFenster.Reha;
import hauptFenster.SuchenDialog;

import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleValue;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JComponent.AccessibleJComponent;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import events.PatStammEvent;
import events.PatStammEventClass;

import terminKalender.TerminFenster;

public class JKasseInternal extends JRehaInternal{

	public JKasseInternal(String titel, ImageIcon img, int desktop) {
		super(titel, img, desktop);
	}
	@Override
	public void internalFrameClosing(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("Internal-KassenFrame in schliessen***************");
	}
	@Override
	public void internalFrameClosed(InternalFrameEvent arg0) {
		Reha.thisClass.desktops[this.desktop].remove(this);
		AktiveFenster.loescheFenster(this.getName());
		this.removeInternalFrameListener(this);
		Reha.thisFrame.requestFocus();
		System.out.println("Lösche KasseInternal von Desktop-Pane = "+Reha.thisClass.desktops[this.desktop]);
		Reha.thisClass.aktiviereNaechsten(this.desktop);
	}
	public void setzeTitel(String stitel){
		super.setzeTitel(stitel);
		repaint();
		
	}
	
}
