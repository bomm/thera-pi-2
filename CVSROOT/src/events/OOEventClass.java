package events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OOEventClass {

	//Add the event registration and notification code to a class.
	    // Create the listener list
	    protected static javax.swing.event.EventListenerList listenerList =
	        new javax.swing.event.EventListenerList();



	    // This methods allows classes to unregister for RehaEvents
		public void removeOOEventListener(OOEventListener ooListener) {
			// TODO Auto-generated method stub
			System.out.println("Office Object Listener gelöscht "+ooListener);
			listenerList.remove(OOEventListener.class, ooListener);
		}
		public void addOOEventListener(OOEventListener ooListener) {
			// TODO Auto-generated method stub
			System.out.println("Office Object Listener hinzugefügt "+ooListener);
			listenerList.add(OOEventListener.class, ooListener);
		}

		// This private class is used to fire RehaEvents
	    public static void fireOOEvent(OOEvent evt) {
	        Object[] listeners = listenerList.getListenerList();
	        // Each listener occupies two elements - the first is the listener class
	        // and the second is the listener instance
	        for (int i=0; i<listeners.length; i+=2) {
	            if (listeners[i]==OOEventListener.class) {
	                 ((OOEventListener)listeners[i+1]).OOEventOccurred(evt);
	            }
	        }
	    }


		public javax.swing.event.EventListenerList getListenerList(){
			return listenerList;
		}



		

	}