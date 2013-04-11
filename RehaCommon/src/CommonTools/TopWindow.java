package CommonTools;

import java.awt.KeyboardFocusManager;

import ag.ion.bion.officelayer.document.IDocument;

import com.sun.star.awt.XExtendedToolkit;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XTopWindowListener;
import com.sun.star.awt.XWindow;
import com.sun.star.awt.XWindowPeer;
import com.sun.star.lang.EventObject;
import com.sun.star.uno.UnoRuntime;

public class TopWindow implements XTopWindowListener{
	XWindow xWindow = null;
	XExtendedToolkit myExtToolkit = null;
	IDocument xdocument = null;
	
	public TopWindow(IDocument doc){
		xdocument = doc;
		
		
		XWindowPeer myWindowPeer = (XWindowPeer) UnoRuntime.queryInterface (XWindowPeer.class,xdocument.getFrame().getXFrame().getContainerWindow());

    	XToolkit myToolkit = myWindowPeer.getToolkit();
    	
    	myExtToolkit = (XExtendedToolkit) UnoRuntime.queryInterface (XExtendedToolkit.class, myToolkit);
    	
    	myExtToolkit.addTopWindowListener(this);
		
	}
	@Override
	public void disposing(EventObject arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(EventObject arg0) {
    	KeyboardFocusManager focman = KeyboardFocusManager.getCurrentKeyboardFocusManager();
    	focman.clearGlobalFocusOwner();
	}

	@Override
	public void windowClosed(EventObject arg0) {
		myExtToolkit.removeTopWindowListener(this);
		
	}

	@Override
	public void windowClosing(EventObject arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(EventObject arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowMinimized(EventObject arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowNormalized(EventObject arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(EventObject arg0) {
		// TODO Auto-generated method stub
		
	}
	

}
