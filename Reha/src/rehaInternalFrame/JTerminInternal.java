package rehaInternalFrame;

import hauptFenster.AktiveFenster;
import hauptFenster.Reha;

import java.awt.event.ComponentListener;
import java.beans.PropertyVetoException;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleValue;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import events.PatStammEvent;
import events.PatStammEventClass;
import events.RehaEvent;
import events.RehaEventClass;
import events.RehaEventListener;

import terminKalender.TerminFenster;

public class JTerminInternal extends JRehaInternal implements RehaEventListener{
	RehaEventClass rEvent = null;
	public JTerminInternal(String titel, ImageIcon img, int desktop) {
		super(titel, img, desktop);
		rEvent = new RehaEventClass();
		rEvent.addRehaEventListener((RehaEventListener) this);
		addInternalFrameListener(this);
		// TODO Auto-generated constructor stub
	}
	/*
	public void internalFrameActivated(InternalFrameEvent arg0) {
		isActive = true;
		TerminFenster.thisClass.getViewPanel().requestFocus();
		toFront();
		super.repaint();
		frameAktivieren(super.getName());
	}
	*/
	@Override
	public void internalFrameClosed(InternalFrameEvent arg0) {
		Reha.thisClass.desktops[this.desktop].remove(this);
		this.removeInternalFrameListener(this);
		Reha.thisFrame.requestFocus();
		Reha.thisClass.aktiviereNaechsten(this.desktop);
		this.removeAll();
		System.out.println("L�sche Termin Internal von Desktop-Pane = "+Reha.thisClass.desktops[this.desktop]);
		System.out.println("Termin-Internal geschlossen***************");

		rEvent.removeRehaEventListener((RehaEventListener) this);
		if(Reha.thisClass.terminpanel != null){
			try{
				Reha.thisClass.terminpanel.db_Aktualisieren.interrupt();
			}catch(Exception ex){
				
			}
		}
		if(Reha.thisClass.terminpanel != null){
			Reha.thisClass.terminpanel = null;			
		}
		
		Reha.thisClass.terminpanel = null;
		this.nord = null;
		this.inhalt = null;
		this.thisContent = null;
		this.dispose();
		super.dispose();
		AktiveFenster.loescheFenster(this.getName());
		Reha.thisClass.progLoader.loescheTermine();
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
final class JDesktopIcon extends JComponent implements Accessible
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

/**
   * DOCUMENT ME!
   */
  protected class AccessibleJDesktopIcon extends AccessibleJComponent
    implements AccessibleValue
  {
    private static final long serialVersionUID = 5035560458941637802L;
    /**
     * Creates a new AccessibleJDesktopIcon object.
     */
    protected AccessibleJDesktopIcon()
    {
	super();
    }
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public AccessibleRole getAccessibleRole()
    {
	return null;
    }
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public AccessibleValue getAccessibleValue()
    {
	return null;
    }
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Number getCurrentAccessibleValue()
    {
	return null;
    }
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Number getMaximumAccessibleValue()
    {
	return null;
    }
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Number getMinimumAccessibleValue()
    {
	return null;
    }
    /**
     * DOCUMENT ME!
     *
     * @param n DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean setCurrentAccessibleValue(Number n)
    {
	return false;
    }
  }
}  

