package events;
import java.util.EventObject;

public class OOEvent extends EventObject {
	// Declare the event. It must extend EventObject.

	    /**
		 * 
		 */
		@SuppressWarnings("unused")
		private Object source;
		private String sOOEvent = "";
		private String[] sDetails = {"",""};
		private static final long serialVersionUID = 1L;

		public OOEvent(Object source) {
	        super(source);
	        this.source = source;
	    }
		public void setOOSEvent(String sOOEvent) {
			this.sOOEvent = sOOEvent;
			
		}
		public String getOOSEvent() {
			return sOOEvent ;
			
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
		public OOEvent getOOEvent(){
	        return this;
		}

	}


	 