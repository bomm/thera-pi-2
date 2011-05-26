package terminKalender;

public class BestaetigungsDaten {
	public boolean best; //Diese HMPosNr ist zu bestätigen oder nicht 
	public String hMPosNr ="./."; //Heilmittelpositionsnummer Default gesetzt auf ./.
	public int anzBBT; //Anzahl der bereits bestätigten Termine (dieser HMPosNr) 
	public int vOMenge; //verordnete Menge dieser HMPos
	public int vORestMenge; //verordnete Menge dieser HMPos
	public boolean vorrangig;
	public boolean invOBelegt;
	public boolean einerOk;
	public boolean danachVoll;
	public boolean jetztVoll; 

	public BestaetigungsDaten(boolean best, String hmPosNr, int anzBBT, int vOMenge,boolean vorrangig,boolean belegt) {
		this.best=best;
		this.hMPosNr=hmPosNr;
		this.anzBBT=anzBBT;
		this.vOMenge=vOMenge;
		this.vorrangig = vorrangig;
		this.invOBelegt = belegt;
		this.einerOk = false;
		this.jetztVoll = false;
		
	}
	public void gehtNochEiner(){
		if(this.vOMenge > this.anzBBT){this.einerOk=true;}
		this.vORestMenge = this.vOMenge-this.anzBBT;
	}
	public void danachVoll(){
		if(this.vOMenge == (this.anzBBT+1)){
			this.danachVoll=true;
		}
	}
	public boolean jetztVoll(){
		if(this.vOMenge == (this.anzBBT)){
			this.jetztVoll=true;
		}else{
			this.jetztVoll=false;
		}
		return this.jetztVoll;
	}
}
