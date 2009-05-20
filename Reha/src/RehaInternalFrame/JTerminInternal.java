package RehaInternalFrame;

import hauptFenster.Reha;

import java.awt.event.ComponentListener;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleValue;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import terminKalender.TerminFenster;

public class JTerminInternal extends JRehaInternal {

	public JTerminInternal(String titel, ImageIcon img, int desktop) {
		super(titel, img, desktop);
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

