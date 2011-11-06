package verkauf.model;

import java.util.Vector;

import sqlTools.SqlInfo;

public class Lieferant {
	
	private int id;
	private String name, ansprechpartner, anschrift, plz, ort, telefon, telefax;
	
	public Lieferant(int id) {
		this.id = id;
		String sql = "SELECT * FROM verklieferant WHERE verklieferantID = " + id;
		Vector<Vector<String>> datensaetze = SqlInfo.holeFelder(sql);
		Vector<String> datensatz = datensaetze.get(0);
		this.name = datensatz.get(1);
		this.ansprechpartner = datensatz.get(2);
		this.anschrift = datensatz.get(3);
		this.plz = datensatz.get(4);
		this.ort = datensatz.get(5);
		this.telefon = datensatz.get(6);
		this.telefax = datensatz.get(7);
	}
	
	public Lieferant(String name, String ansprechpartner, String anschrift, String plz, String ort, String telefon, String telefax) {
		this.name = name;
		this.ansprechpartner = ansprechpartner;
		this.anschrift = anschrift;
		this.plz = plz;
		this.ort = ort;
		this.telefon = telefon;
		this.telefax = telefax;
		
		String sql = "INSERT INTO verklieferant (verklieferantID, name, ansprechpartner, anschrift, plz, ort, telefon, telefax) " +
				"VALUES (NULL, '"+ this.name +"', '"+ this.ansprechpartner +"', '"+ this.anschrift +"', '"+ this.plz +"', '"+ this.ort +"', '"+ this.telefon +"', '"+ this.telefax +"');";
		SqlInfo.sqlAusfuehren(sql);
	}
	
	private void update() {
		String sql ="UPDATE verklieferant SET name = '"+ this.name +"', "+
				"ansprechpartner = '"+ this.ansprechpartner +"', "+
				"anschrift = '"+ this.anschrift +"', "+
				"plz = '"+ this.plz +"', "+
				"ort = '"+ this.ort +"', "+
				"telefon = '"+ this.telefon +"', "+
				"telefax = '"+ this.telefax +"' WHERE verklieferantID = "+ this.id +";";
		SqlInfo.sqlAusfuehren(sql);
	}

	public String getName() {
		return name;
	}

	public String getAnsprechpartner() {
		return ansprechpartner;
	}

	public String getAnschrift() {
		return anschrift;
	}

	public String getPlz() {
		return plz;
	}

	public String getOrt() {
		return ort;
	}

	public String getTelefon() {
		return telefon;
	}

	public String getTelefax() {
		return telefax;
	}

	public void setName(String name) {
		this.name = name;
		this.update();
	}

	public void setAnsprechpartner(String ansprechpartner) {
		this.ansprechpartner = ansprechpartner;
		this.update();
	}

	public void setAnschrift(String anschrift) {
		this.anschrift = anschrift;
		this.update();
	}

	public void setPlz(String plz) {
		this.plz = plz;
		this.update();
	}

	public void setOrt(String ort) {
		this.ort = ort;
		this.update();
	}

	public void setTelefon(String telefon) {
		this.telefon = telefon;
		this.update();
	}

	public void setTelefax(String telefax) {
		this.telefax = telefax;
		this.update();
	}
	
	public int getID() {
		return this.id;
	}
	
	public boolean equals(Object o) {
		if(o instanceof Lieferant ) {
			Lieferant vergleichsObjekt = (Lieferant) o; 
			if(vergleichsObjekt.getID() == this.id) {
				return true;
			}
		}
		return false;
	}
	
	public String toString() {
		String returnsstmt = this.name + " - " + this.ansprechpartner;
		return returnsstmt;
	}
	
	public static Vector<Vector<String>> liefereLieferantenDaten() {
		String sql = "SELECT name, ansprechpartner, telefon, telefax, anschrift, plz, ort, verklieferantID FROM verklieferant;";
		Vector<Vector<String>> lieferanten = SqlInfo.holeFelder(sql);
		return lieferanten;
	}
	
	public static Vector<Vector<String>> sucheLieferantenDaten(String suche) {
		String sql = "SELECT name, ansprechpartner, telefon, telefax, anschrift, plz, ort, verklieferantID FROM verklieferant WHERE name LIKE '%"+ suche +"%' OR ansprechpartner LIKE '%"+ suche +"%' " +
				"OR telefon LIKE '%"+ suche +"%' OR telefax LIKE '%"+ suche +"%';";
		Vector<Vector<String>> lieferanten = SqlInfo.holeFelder(sql);
		return lieferanten;
	}
	
	public static void loesche(int ID) {
		String sql = "DELETE FROM verklieferant WHERE verklieferantID = " + ID;
		SqlInfo.sqlAusfuehren(sql);
	}

	public static Lieferant[] liefereLieferantenCombo() {
		String sql = "SELECT verklieferantID FROM verklieferant;";
		Vector<String> ids = SqlInfo.holeFeld(sql);
		Lieferant[] returnsstmt = new Lieferant[ids.size()];
		int i = 0;
		while(!ids.isEmpty()) {
			returnsstmt[i] = new Lieferant(Integer.parseInt(ids.get(0)));
			ids.remove(0);
			i++;
		}
		return returnsstmt;
	}
	

}
