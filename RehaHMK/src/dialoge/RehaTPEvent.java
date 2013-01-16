package dialoge;

import java.util.EventObject;

// Declare the event. It must extend EventObject.
public class RehaTPEvent extends EventObject {
    /**
	 * 
	 */
	@SuppressWarnings("unused")
	private Object source;
	private String sRehaEvent = "";
	private String[] sDetails = {"",""};
	private static final long serialVersionUID = 1L;

	public RehaTPEvent(Object source) {
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
	public void setDetails1(String sEvent){
		sDetails[0]=sEvent;
	}
	public void setDetails2(String sEvent){
		sDetails[1]=sEvent;
	}
	public String[] getDetails(){
		return sDetails;
	}
	public void setNewSource(Object source){
        this.source = source;
	}
	public RehaTPEvent getRehaTPEvent(){
        return this;
	}

}


 