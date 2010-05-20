package rehaInternalFrame;

import hauptFenster.AktiveFenster;
import hauptFenster.Reha;

import java.awt.event.FocusListener;
import java.beans.PropertyVetoException;

import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameEvent;

import events.PatStammEvent;
import events.PatStammEventClass;
import events.RehaEvent;
import events.RehaEventClass;
import events.RehaEventListener;

public class JPatientInternal extends JRehaInternal implements FocusListener, RehaEventListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8069000862982060024L;
	RehaEventClass rEvent = null;
	public JPatientInternal(String titel, ImageIcon img, int desktop) {
		super(titel, img, desktop);
		this.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
		rEvent = new RehaEventClass();
		rEvent.addRehaEventListener((RehaEventListener) this);
	}

	@Override
	public void internalFrameClosing(InternalFrameEvent arg0) {
		//System.out.println("Internal-Pat-Frame in schliessen***************");
	}
	@Override
	public void internalFrameClosed(InternalFrameEvent arg0) {
		try{
		//System.out.println("Lösche Patient von Desktop-Pane = "+Reha.thisClass.desktops[this.desktop]);
		//Nächsten JInternalFrame aktivieren
		Reha.thisClass.aktiviereNaechsten(this.desktop);
		//JInternalFram von Desktop lösen
		Reha.thisClass.desktops[this.desktop].remove(this);
		//Listener deaktivieren
		rEvent.removeRehaEventListener((RehaEventListener) this);
		rEvent = null;
		this.removeInternalFrameListener(this);
		
		try{
			Reha.thisFrame.requestFocus();
			/*
			Reha.thisClass.patpanel.fl = null;
			Reha.thisClass.patpanel.kli = null;
			Reha.thisClass.patpanel.gplst = null;
			Reha.thisClass.patpanel.newPolicy = null;
			*/
		}catch(Exception ex){
			//System.out.println("Fehler beim schließen des IFrames");
			//ex.printStackTrace();
		}
		
		String s1 = new String("#CLOSING");
		String s2 = "";
		PatStammEvent pEvt = new PatStammEvent(this);
		pEvt.setPatStammEvent("PatSuchen");
		pEvt.setDetails(s1,s2,"") ;
		PatStammEventClass.firePatStammEvent(pEvt);
		
		//System.out.println("Internal-Pat-Frame in geschlossen***************");
		Reha.thisClass.aktiviereNaechsten(this.desktop);
		Reha.thisClass.patpanel.allesAufraeumen();
		if(Reha.thisClass.patpanel.getInternal() != null){
			Reha.thisClass.patpanel.setInternalToNull();
			Reha.thisClass.patpanel = null;
		}
		/*
		if(Reha.thisClass.patpanel.jry != null){
			Reha.thisClass.patpanel.jry = null;
			Reha.thisClass.patpanel = null;
		}
		*/
		//Gutachten.gutachten = null;
		//Historie.historie = null;
		//Dokumentation.doku = null;
		//AktuelleRezepte.aktRez = null;
		//TherapieBerichte.aktBericht = null;
		this.destroyTitleBar();
		this.nord = null;
		this.inhalt = null;
		this.thisContent = null;

		
		this.removeAll();
		this.dispose();
		super.dispose();

		final String name = this.getName();

		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run()
		 	   {
		 		   AktiveFenster.loescheFenster(name);
		 		   Reha.thisClass.progLoader.loeschePatient();
		 	   }
		});
		}catch(Exception ex){
			ex.printStackTrace();
		}


		
	}
	public void setzeSuche(){
		Reha.thisClass.patpanel.setzeFocus();
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
	@Override
	public void rehaEventOccurred(RehaEvent evt) {
		if(evt.getRehaEvent().equals("REHAINTERNAL")){
			//System.out.println("es ist ein Reha-Internal-Event");
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
