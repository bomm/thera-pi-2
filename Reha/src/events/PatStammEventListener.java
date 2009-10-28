package events;



import java.util.EventListener;

//Declare the listener class. It must extend EventListener.
//A class must implement this interface to get RehaEvents.
public interface PatStammEventListener extends EventListener {
public void patStammEventOccurred(PatStammEvent evt );
}
