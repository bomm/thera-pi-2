package rehaInternalFrame;
import hauptFenster.Reha;

import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultDesktopManager;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JInternalFrame.JDesktopIcon;

/*
 * THE LINES MARKED WITH XXX IS THE CHANGE THAT WAS DONE TO THE ORIGINAL METHODS FROM DefaultDesktopManager
 */
public class OOODesktopManager extends DefaultDesktopManager {

  Map<JInternalFrame, Rectangle> oldBounds = new HashMap<JInternalFrame, Rectangle>();
  int desktopNo = 0;
  int aktuell = 0;
  String frameaktuell = "";
  public OOODesktopManager(int desk){
	  desktopNo = desk;
  }
  @Override
  public void iconifyFrame(JInternalFrame f) {
    JInternalFrame.JDesktopIcon desktopIcon;
    Container c = f.getParent();
    JDesktopPane d = f.getDesktopPane();
    boolean findNext = f.isSelected();

    desktopIcon = f.getDesktopIcon();
    if (!wasIcon(f)) {
      Rectangle r = getBoundsForIconOf(f);
      desktopIcon.setBounds(r.x, r.y, r.width, r.height);
      setWasIcon(f, Boolean.TRUE);
      frameaktuell = f.getName();
    }

    if (c == null) {
      return;
    }

    if (c instanceof JLayeredPane) {
      JLayeredPane lp = (JLayeredPane) c;
      int layer = JLayeredPane.getLayer(f);
      JLayeredPane.putLayer(desktopIcon, layer);
    }

    // If we are maximized we already have the normal bounds recorded
    // don't try to re-record them, otherwise we incorrectly set the
    // normal bounds to maximized state.
    if (!f.isMaximum()) {
      f.setNormalBounds(f.getBounds());
    }
    //c.remove(f); XXX
    oldBounds.put(f, f.getBounds()); //XXX
    f.setBounds(0, 0, 0, 0); //XXX

    c.add(desktopIcon);
    c.repaint(f.getX(), f.getY(), f.getWidth(), f.getHeight());
    try {
      f.setSelected(false);
    }
    catch (PropertyVetoException e2) {
    }

    // Get topmost of the remaining frames
    if (findNext) {
      //System.err.println("activateNextFrame="+c.getName());
      activateNextFrame(c);
      
    }
  }

  void activateNextFrame(Container c) {
    int i;
    JInternalFrame nextFrame = null;
    if (c == null)
      return;
    for (i = 0; i < c.getComponentCount(); i++) {

      if (c.getComponent(i) instanceof JInternalFrame && (!c.getComponent(i).getName().equals(frameaktuell)) ) {
    	  if(! ((JRehaInternal)c.getComponent(i)).isIcon){
    	        nextFrame = (JInternalFrame) c.getComponent(i);
    	        break;
    	  }
      }
    }
    /*
	  for(int y = 0; y < c.getComponentCount();y++){
		  if((c.getComponent(y) instanceof JInternalFrame)){
			  String status = "Komponente = "+c.getComponent(y).getName().toString()+" / frameaktuell = "+
			  frameaktuell+" / isIcon = "+ ((JRehaInternal)c.getComponent(y)).isIcon;
			  System.err.println(status);
		  }
  	  }
	  System.out.println(nextFrame);
	*/
    if (nextFrame != null) {
      try {
        nextFrame.setSelected(true);
        frameaktuell = nextFrame.getName();
      	((JRehaInternal)nextFrame).setActive(true);
      }
      catch (PropertyVetoException e2) {
      }
    }

  }
/******************************************************************/
  public void deiconifyFrame(JInternalFrame f) {
    JInternalFrame.JDesktopIcon desktopIcon = f.getDesktopIcon();
    Container c = desktopIcon.getParent();
    if (c != null) {
      f.setBounds(oldBounds.remove(f)); //XXX
      //c.add(f); //XXX

      // If the frame is to be restored to a maximized state make
      // sure it still fills the whole desktop.
      if (f.isMaximum()) {
        Rectangle desktopBounds = c.getBounds();
        if (f.getWidth() != desktopBounds.width || f.getHeight() != desktopBounds.height) {
          setBoundsForFrame(f, 0, 0, desktopBounds.width, desktopBounds.height);
        }
      }
      removeIconFor(f);
      if (f.isSelected()) {
        f.moveToFront();
        frameaktuell = f.getName();
        ((JRehaInternal)f).isIcon = false;
      }
      else {
        try {
        	
          f.setSelected(true);
          frameaktuell = f.getName();
          ((JRehaInternal)f).isIcon = false;
        }
        catch (PropertyVetoException e2) {
        }
      }
    }
  }
/******************************************************/
  public void activateFrame(JInternalFrame f) {
    Container p = f.getParent();
    Component[] c;
    JDesktopPane d = f.getDesktopPane();
    JInternalFrame currentlyActiveFrame = (d == null) ? null : d.getSelectedFrame();
    // fix for bug: 4162443
    if (p == null) {
      // If the frame is not in parent, its icon maybe, check it
      p = f.getDesktopIcon().getParent();
      if (p == null)
        return;
    }
    // we only need to keep track of the currentActive InternalFrame, if any
    if (currentlyActiveFrame == null) {
      if (d != null) {
        //d.setSelectedFrame(f);
      }
    }
    else if (currentlyActiveFrame != f) {
      // if not the same frame as the current active
      // we deactivate the current 
      if (currentlyActiveFrame.isSelected()) {
        try {
          currentlyActiveFrame.setSelected(false);
        }
        catch (Exception e2) {
        }
      }
      if (d != null) {
        d.setSelectedFrame(f);
		frameaktuell = f.getName();
      }
    }
    f.moveToFront();
  }
}
