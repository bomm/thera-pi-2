package events;

//import hauptFenster.SystemLookAndFeel;



import java.util.EventListener;

//Declare the listener class. It must extend EventListener.
//A class must implement this interface to get RehaEvents.
public interface RehaTPEventListener extends EventListener {
public void rehaTPEventOccurred(RehaTPEvent evt );
/*public void RehaTPEventOccurred(TerminFenster evt );
public void RehaTPEventOccurred(Container evt );
public void RehaTPEventOccurred(Object evt );
public void RehaTPEventOccurred(SystemLookAndFeel evt );
*/
}
