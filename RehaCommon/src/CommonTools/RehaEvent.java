package CommonTools;

import java.util.EventObject;

// Declare the event. It must extend EventObject.
public class RehaEvent extends EventObject {
    /**
	 * 
	 */
	@SuppressWarnings("unused")
	private Object source;
	private String sRehaEvent = "";
	private String[] sDetails = {"",""};
	@SuppressWarnings("unused")
	private String name = "";
	private static final long serialVersionUID = 1L;
	
	public static final String ERROR_EVENT = "REHA_ERROR";

	public RehaEvent(Object source) {
        super(source);
        this.source = source;
    }
	public void setRehaEvent(String sRehaEvent) {
		this.sRehaEvent = sRehaEvent;
		
	}
	public String getRehaEvent() {
		return sRehaEvent ;
		
	}
	public void setDetails(String sEvent,String sKommando){
		sDetails[0]=sEvent;
		sDetails[1]=sKommando;
	}
	public String[] getDetails(){
		return sDetails;
	}
}


 