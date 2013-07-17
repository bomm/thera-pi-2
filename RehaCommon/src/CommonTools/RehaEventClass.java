package CommonTools;




//Add the event registration and notification code to a class.
public class RehaEventClass {
    // Create the listener list
    protected static javax.swing.event.EventListenerList listenerList =
        new javax.swing.event.EventListenerList();

    
    public Object RehaEventListener;

	public Object addRehaEventListener;

    // This methods allows classes to unregister for RehaEvents
    public void removeRehaEventListener(RehaEventListener listener) {
        listenerList.remove(RehaEventListener.class, listener);
        listener = null;
    }
    
    // This methods allows classes to unregister for RehaEvents
    public void addRehaEventListener(RehaEventListener listener) {
        listenerList.add(RehaEventListener.class, listener);
    }
 
    // This private class is used to fire RehaEvents
    public static void fireRehaEvent(RehaEvent evt) {
        Object[] listeners = listenerList.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance

        for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i]==RehaEventListener.class) {
                //System.out.println("Listeners= "+listeners[i]);
            	//evt.add("DummDödel");
                ((RehaEventListener)listeners[i+1]).rehaEventOccurred(evt);
            }
        }
    }





	

}
