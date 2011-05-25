package terminKalender;

public class BestaetigungsDaten {
	boolean best; //Diese HMPosNr ist zu bestätigen oder nicht 
	String hMPosNr ="./."; //Heilmittelpositionsnummer Default gesetzt auf ./.
	int anzBBT; //Anzahl der bereits bestätigten Termine (dieser HMPosNr) 
	int vOMenge; //verordnete Menge dieser HMPos
	boolean vorrangig;
	BestaetigungsDaten(boolean best, String hmPosNr, int anzBBT, int vOMenge,boolean vorrangig) {
		this.best=best;
		this.hMPosNr=hmPosNr;
		this.anzBBT=anzBBT;
		this.vOMenge=vOMenge;
		this.vorrangig = vorrangig;
	}
}
