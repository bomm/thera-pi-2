package events;


public class PatStammEventClass {

	//Add the event registration and notification code to a class.
	    // Create the listener list
	    protected static javax.swing.event.EventListenerList listenerList =
	        new javax.swing.event.EventListenerList();

	    // This methods allows classes to unregister for RehaEvents
		public void removePatStammEventListener(PatStammEventListener ooListener) {
			// TODO Auto-generated method stub
			//System.out.println("PatStamm Object Listener gelöscht "+ooListener);
			listenerList.remove(PatStammEventListener.class, ooListener);
		}
		public void addPatStammEventListener(PatStammEventListener ooListener) {
			// TODO Auto-generated method stub
			//System.out.println("PatStamm Object Listener hinzugefügt "+ooListener);
			listenerList.add(PatStammEventListener.class, ooListener);
		}

		// This private class is used to fire RehaEvents
	    public static void firePatStammEvent(PatStammEvent evt) {
	        Object[] listeners = listenerList.getListenerList();
	        // Each listener occupies two elements - the first is the listener class
	        // and the second is the listener instance
	        for (int i=0; i<listeners.length; i+=2) {
	            if (listeners[i]==PatStammEventListener.class) {
	                 ((PatStammEventListener)listeners[i+1]).patStammEventOccurred(evt);
	            }
	        }
	    }
		public javax.swing.event.EventListenerList getListenerList(){
			return listenerList;
		}



		

	}
