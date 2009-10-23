package events;





import java.awt.Container;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;

import dialoge.RehaSmartDialog;




import rehaContainer.RehaTP;
import terminKalender.TerminFenster;

//Add the event registration and notification code to a class.
public class RehaTPEventClass{
    // Create the listener list
    protected static javax.swing.event.EventListenerList listenerList =
        new javax.swing.event.EventListenerList();

    private static List _listeners = new ArrayList();
    
    public void addListener(RehaTPEventListener l){
    	_listeners.add(l);
    }
    public synchronized void removeListener(RehaTPEventListener l){
    	_listeners.remove(l);
    }
    public static void _fireRehaTPEvent(RehaTPEvent evt) {
        
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        Iterator listeners = _listeners.iterator();
        while( listeners.hasNext() ) {
            ( (RehaTPEventListener) listeners.next() ).RehaTPEventOccurred(evt);
        }    }


    // This methods allows classes to unregister for RehaEvents
	public void removeRehaTPEventListener(RehaTPEventListener rehaListener) {
		// TODO Auto-generated method stub
		//System.out.println("Object Listener gelöscht "+rehaListener);
		listenerList.remove(RehaTPEventListener.class, rehaListener);
		//System.out.println("Aktive Listener: "+listenerList.getListenerList().length);
	}
    // Hier wird gefeuert
    public static void fireRehaTPEvent(RehaTPEvent evt) {
        Object[] listeners = listenerList.getListenerList();
        for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i]==RehaTPEventListener.class) {
                 ((RehaTPEventListener)listeners[i+1]).RehaTPEventOccurred(evt);
            }
        }
    }



	public void addRehaTPEventListener(RehaTPEventListener rehaListener) {
		// TODO Auto-generated method stub
		//System.out.println("Object Listener hinzugefügt "+rehaListener);
		listenerList.add(RehaTPEventListener.class, rehaListener);
	}

	public javax.swing.event.EventListenerList getListenerList(){
		return listenerList;
	}



	

}