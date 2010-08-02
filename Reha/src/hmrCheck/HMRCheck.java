package hmrCheck;

import java.util.Vector;

public class HMRCheck {
	Vector<Integer> anzahl = null;
	Vector<String> positionen = null;
	String indischluessel = null;
	public HMRCheck(String indi,Vector<Integer> vecanzahl,Vector<String>vecpositionen){
		indischluessel = indi;
		anzahl = vecanzahl;
		positionen = vecpositionen;
	}
	/*
	 * 
	 * Abhängig vom Indikationsschlüssel muß geprüft werden
	 * 1. Ist die Anzahl pro Rezept o.k.
	 * 2. Ist das gewählte Heilmittel o.k.
	 * 3. ist das ergänzende Heilmittel o.k.
	 * 4.
	 * 5. 
	 * 
	 */
	
	public boolean check(){
		boolean ret = true;
		return ret;
	}
}
