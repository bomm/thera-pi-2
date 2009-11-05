package abrechnung;

import org.jdesktop.swingx.JXPanel;

import RehaInternalFrame.JAbrechnungInternal;

import events.PatStammEvent;
import events.PatStammEventListener;

public class Abrechnung1 extends JXPanel implements PatStammEventListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3580427603080353812L;
	private JAbrechnungInternal jry;
	public Abrechnung1(JAbrechnungInternal xjry){
		super();
		this.setJry(xjry);
		
	}

	@Override
	public void patStammEventOccurred(PatStammEvent evt) {
		// TODO Auto-generated method stub
		
	}

	public void setJry(JAbrechnungInternal jry) {
		this.jry = jry;
	}

	public JAbrechnungInternal getJry() {
		return jry;
	}

}
