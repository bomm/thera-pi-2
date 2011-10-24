package verkauf.model;

import java.text.DecimalFormat;
import java.util.Vector;

import sqlTools.SqlInfo;

public class Artikel {
	private double preis;
	private double mwst;
	private long ean;
	private int lagerstand;
	private String beschreibung;
	
	public Artikel(long ean, String beschreibung, double preis, double mwst, int lagerstand) {
		this.preis = preis;
		this.mwst = mwst;
		this.lagerstand = lagerstand;
		this.ean = ean;
		this.beschreibung = beschreibung;
		SqlInfo.sqlAusfuehren("INSERT INTO `verkartikel` (`ean`, `beschreibung`, `preis`, `mwst`, `lagerstand`) " +
				"VALUES('"+ this.ean +"', '"+ this.beschreibung +"', '"+ this.preis +"', '"+ this.mwst +"', '"+ this.lagerstand +"' );");
	}
	
	public Artikel(long ean) {
		// Abfragen aus der Datenbank
		this.ean = ean;
		String sql = "SELECT beschreibung, preis, mwst, lagerstand FROM verkartikel WHERE ean = "+ this.ean;
		
		Vector<Vector<String>> felder = SqlInfo.holeFelder(sql);
		Vector<String> datensatz = felder.get(0);
		if(!datensatz.isEmpty()) {
			this.beschreibung = datensatz.get(0);
			this.preis = Double.parseDouble(datensatz.get(1));
			this.mwst = Double.parseDouble(datensatz.get(2));
			this.lagerstand = Integer.parseInt(datensatz.get(3));
		}
	}
	
	void verkaufeArtikel(int anzahl) {
		if(this.lagerstand != -1) {
			if(anzahl <= this.lagerstand) {
				this.lagerstand -= anzahl;
			} else {
				if(this.lagerstand > 0) {
					anzahl -= this.lagerstand;
				}
				this.lagerstand = -101;
				this.lagerstand -= anzahl;
			}
		}
		this.lagerstand = this.lagerstand - anzahl;
		this.update();
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

	public int getLagerstand() {
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

	public void setLagerstand(int lagerstand) {
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
			artikelliste[i][4] = String.valueOf(artikel.getLagerstand());
			artikelliste[i][5] = "0";
			i++;
		}
		return artikelliste;
	}
}
