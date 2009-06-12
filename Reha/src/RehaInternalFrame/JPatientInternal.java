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

import patientenFenster.PatGrundPanel;

import events.PatStammEvent;
import events.PatStammEventClass;

import terminKalender.TerminFenster;

public class JPatientInternal extends JRehaInternal implements FocusListener{

	public JPatientInternal(String titel, ImageIcon img, int desktop) {
		super(titel, img, desktop);
		//addInternalFrameListener(this);
		// TODO Auto-generated constructor stub
	}
	/*
	@Override
	public void internalFrameActivated(InternalFrameEvent arg0) {
		isActive = true;
		requestFocus();
		toFront();
		super.repaint();
		frameAktivieren(super.getName());
	}
	*/
	@Override
	public void internalFrameClosing(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("Internal-Pat-Frame in schliessen***************");
	}
	@Override
	public void internalFrameClosed(InternalFrameEvent arg0) {
		Reha.thisClass.desktops[this.desktop].remove(this);
		AktiveFenster.loescheFenster(this.getName());
		this.removeInternalFrameListener(this);
		Reha.thisFrame.requestFocus();
		System.out.println("Lösche PatientInternal von Desktop-Pane = "+Reha.thisClass.desktops[this.desktop]);
		String s1 = new String("#CLOSING");
		String s2 = "";
		PatStammEvent pEvt = new PatStammEvent(this);
		pEvt.setPatStammEvent("PatSuchen");
		pEvt.setDetails(s1,s2,"") ;
		PatStammEventClass.firePatStammEvent(pEvt);	
		System.out.println("Internal-Pat-Frame in geschlossen***************");
		Reha.thisClass.aktiviereNaechsten(this.desktop);
		
	}
	public void setzeSuche(){
		PatGrundPanel.thisClass.setzeFocus();
	}
	public void setzeTitel(String stitel){
		super.setzeTitel(stitel);
		repaint();
		
	}
	public boolean isActivated(){
		if(! this.isActive){
			return false;
		}else{
			return true;
		}
	}
	public void activateInternal(){
		this.isActive = true;
		this.aktiviereDiesenFrame(this.getName());
		repaint();
	}
	
}
