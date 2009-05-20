package events;

import java.util.EventListener;
import java.util.EventObject;

public class PatStammEvent extends EventObject {
	// Declare the event. It must extend EventObject.

	    /**
		 * 
		 */
		private Object source;
		private String sPatStammEvent = "";
		private String[] sDetails = {"","",""};
		private static final long serialVersionUID = 1L;

		public PatStammEvent(Object source) {
	        super(source);
	        this.source = source;
	    }
		public void setPatStammEvent(String sOOEvent) {
			this.sPatStammEvent = sOOEvent;
			
		}
		public String getPatStammEvent() {
			return sPatStammEvent ;
			
		}
		public void setDetails(String sEvent,String sKommando,String sSuchen){
			sDetails[0]=sEvent;
			sDetails[1]=sKommando;
			sDetails[2]=sSuchen;			
		}
		public void setDetails1(String sEvent){
			sDetails[0]=sEvent;
		}
		public void setDetails2(String sEvent){
			sDetails[1]=sEvent;
		}
		public void setDetails3(String sEvent){
			sDetails[2]=sEvent;
		}

		public String[] getDetails(){
			return sDetails;
		}
		public void setNewSource(Object source){
	        this.source = source;
		}
		public PatStammEvent getPatStammEventObject(){
	        return this;
		}

	}


	 