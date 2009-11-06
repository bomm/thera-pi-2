package RehaInternalFrame;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

import hauptFenster.AktiveFenster;
import hauptFenster.Reha;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameEvent;

import systemTools.ListenerTools;

import entlassBerichte.EBerichtPanel;
import events.RehaEvent;
import events.RehaEventClass;
import events.RehaEventListener;

import arztFenster.ArztPanel;

public class JGutachtenInternal extends JRehaInternal implements RehaEventListener {
	RehaEventClass rEvent = null;
	public JGutachtenInternal(String titel, ImageIcon img, int desktop) {
		super(titel, img, desktop);
		this.setIconifiable(false);
		rEvent = new RehaEventClass();
		rEvent.addRehaEventListener((RehaEventListener) this);
		this.addPropertyChangeListener(new PropertyChangeListener() {
	          public void propertyChange(PropertyChangeEvent evt) {
	        	//System.out.println(evt);
	              if (evt.getPropertyName().equalsIgnoreCase(JInternalFrame.IS_ICON_PROPERTY) 
	            		  && evt.getNewValue().equals(Boolean.TRUE)){
	            	  ((EBerichtPanel)getInhalt()).setOOPanelIcon();
            	  		revalidate();
              			System.out.println("Jetzt icon...........");
	          }
	              if (evt.getPropertyName().equalsIgnoreCase(JInternalFrame.IS_ICON_PROPERTY) 
	            		  && evt.getNewValue().equals(Boolean.FALSE)){
	            	  System.out.println("Jetzt normal...........");
	            	  ((EBerichtPanel)getInhalt()).setOOPanelDeIcon();
	            	  //setSize(new Dimension(xWeit-1,yHoch-1));
	            	  revalidate();
	            	  //pack();
	              }
	              		
	          }
	      });
	}
	@Override
	public void internalFrameClosing(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
		//System.out.println("Internal-GutachtenFrame in schliessen***************");
	}
	@Override
	public void internalFrameClosed(InternalFrameEvent arg0) {
		System.out.println("Lösche Gutachten-Internal von Desktop-Pane = "+Reha.thisClass.desktops[this.desktop]);
		//Nächsten JInternalFrame aktivieren
		Reha.thisClass.aktiviereNaechsten(this.desktop);		
		//JInternalFram von Desktop lösen
		Reha.thisClass.desktops[this.desktop].remove(this);
		((EBerichtPanel)this.inhalt).finalise();
		//Listener deaktivieren
		rEvent.removeRehaEventListener((RehaEventListener) this);
		this.removeInternalFrameListener(this);
		this.removeAncestorListener(this);
		((EBerichtPanel)this.inhalt).dokumentSchliessen();

		RehaEvent evt = new RehaEvent(this);
		evt.setDetails(this.getName(), "#SCHLIESSEN");
		evt.setRehaEvent("Gutachten");
		RehaEventClass.fireRehaEvent(evt);


		//
		Reha.thisFrame.requestFocus();
		//Componenten des InternalFrameTitelbar auf null setzen
		this.destroyTitleBar();
		this.nord = null;

		//((EBerichtPanel)((JComponent)getComponent())).dokumentSchliessen();

		Reha.thisFrame.requestFocus();

		this.nord = null;
		ListenerTools.removeListeners(thisContent);
		this.thisContent = null;
		ListenerTools.removeListeners(inhalt);
		this.inhalt = null;

		this.removeAll();
		
		this.dispose();
		
		final String name = this.getName();

		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run()
		 	   {
				AktiveFenster.loescheFenster(name);
				Reha.thisClass.progLoader.loescheGutachten();
		 	   }
		});


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
	public void rehaEventOccurred(RehaEvent evt) {
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
