package verkauf.model;

import java.text.DecimalFormat;
import java.util.Vector;

import sqlTools.SqlInfo;

public class Artikel {
	private double preis;
	private double mwst;
	private long ean;
	private double lagerstand;
	private String beschreibung, einheit;
	
	public Artikel(long ean, String beschreibung, String einheit, double preis, double mwst, double lagerstand) {
		this.preis = preis;
		this.einheit = einheit;
		this.mwst = mwst;
		this.lagerstand = lagerstand;
		this.ean = ean;
		this.beschreibung = beschreibung;
		SqlInfo.sqlAusfuehren("INSERT INTO `verkartikel` (`ean`, `beschreibung`, `preis`, `mwst`, `lagerstand`, `einheit`) " +
				"VALUES('"+ this.ean +"', '"+ this.beschreibung +"', '"+ this.preis +"', '"+ this.mwst +"', '"+ this.lagerstand +"', '"+ this.einheit +"' );");
	}
	
	public Artikel(long ean) {
		// Abfragen aus der Datenbank
		this.ean = ean;
		String sql = "SELECT beschreibung, preis, mwst, lagerstand, einheit FROM verkartikel WHERE ean = "+ this.ean;
		
		Vector<Vector<String>> felder = SqlInfo.holeFelder(sql);
		Vector<String> datensatz = felder.get(0);
		if(!datensatz.isEmpty()) {
			this.beschreibung = datensatz.get(0);
			this.preis = Double.parseDouble(datensatz.get(1));
			this.mwst = Double.parseDouble(datensatz.get(2));
			this.lagerstand = Double.parseDouble(datensatz.get(3));
			this.einheit = datensatz.get(4);
		}
	}
	
	void verkaufeArtikel(double anzahl, String vnummer, Double vpreis, int patid) {
		this.lagerstand = this.lagerstand - anzahl;
		this.update();
		String sql = "INSERT INTO `verkfaktura` (`verkfakturaID`, `v_nummer`, `art_id`, `art_beschreibung`, `art_einzelpreis`, `art_mwst`, `anzahl`, `pat_id`) " +
				"VALUES (NULL, '"+ vnummer +"', '"+ this.ean +"', '"+ this.getBeschreibung() +"', '"+ vpreis +"', '"+ this.mwst +"', '"+ anzahl +"', '"+ patid +"')";
		SqlInfo.sqlAusfuehren(sql);
	}

	
	public void löscheArtikel() {
		String sql = "DELETE FROM `verkartikel` WHERE `ean` = "+ this.ean +";";
		SqlInfo.sqlAusfuehren(sql);
	}

	void update() {
		String sql = "UPDATE verkartikel SET `beschreibung` = '"+ this.beschreibung +"', `preis` = '"+ this.preis +"'," +
				"`mwst` = '"+ this.mwst +"' , `lagerstand` = '"+ this.lagerstand +"'  WHERE ean = "+ this.ean +";";
		//System.out.println(sql);
		SqlInfo.sqlAusfuehren(sql);
	}
	
	public double getPreis() {
		return preis;
	}

	public double getMwst() {
		return mwst;
	}

	public double getLagerstand() {
		return lagerstand;
	}

	public long getEan() {
		return ean;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public void setPreis(double preis) {
		this.preis = preis;
		this.update();
	}

	public void setMwst(double mwst) {
		this.mwst = mwst;
		this.update();
	}

	public void setLagerstand(double lagerstand) {
		this.lagerstand = lagerstand;
		this.update();
	}

	public void setEan(long ean) {
		String sql = "UPDATE verkartikel SET `ean` = '"+ ean +"' WHERE ean = "+ this.ean +";";
		SqlInfo.sqlAusfuehren(sql);
		this.ean = ean;
	}

	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
		this.update();
	}
	
	public void setEinheit(String einheit) {
		this.einheit = einheit;
		this.update();
	}
	
	public String getEinheit() {
		return this.einheit;
	}
	
	public static boolean artikelExistiert(long ean) {
		String sql = "Select * From verkartikel Where ean = " + ean; 
		return SqlInfo.gibtsSchon(sql);
	}
	
	public static String[][] artikelListe() {
		String sql = "Select ean From verkartikel";
		Vector<String> eans = SqlInfo.holeFeld(sql);
		String[][] artikelliste = new String[eans.size()][6];
		DecimalFormat df = new DecimalFormat("0.00");
		DecimalFormat df2 = new DecimalFormat("0");
		int i = 0;
		while(!eans.isEmpty()) {
			Artikel artikel = new Artikel(Long.parseLong(eans.get(0)));
			eans.remove(0);
			
			artikelliste[i][0] = String.valueOf(artikel.getEan());
			artikelliste[i][1] = artikel.getBeschreibung();
			artikelliste[i][2] = df.format(artikel.getPreis());
			artikelliste[i][3] = df2.format(artikel.mwst);
			artikelliste[i][4] = df.format(artikel.getLagerstand()) + " " + artikel.getEinheit();
			artikelliste[i][5] = "0";
			i++;
		}
		return artikelliste;
	}
}
