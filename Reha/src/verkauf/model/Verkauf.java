package verkauf.model;

import java.util.ArrayList;

public class Verkauf {
	private double betrag7 = 0, betrag19 = 0,  betragBrutto = 0, rabatt = 0;
	//private Date datum;
	private ArrayList<ArtikelVerkauf> artikel;
		
	public Verkauf() {
		this.artikel = new ArrayList<ArtikelVerkauf>();
	}
	
	public void fügeArtikelHinzu(ArtikelVerkauf position) {
		this.artikel.add(position);
		position.setPosition(this.artikel.lastIndexOf(position));
		this.betragBrutto += position.getAnzahl() * position.getPreis();
		if(position.getMwst() == 7) {
			this.betrag7 += (position.getPreis() * 0.07) * position.getAnzahl();
		} else if(position.getMwst() == 19) {
			this.betrag19 += (position.getPreis() * 0.19) * position.getAnzahl();
		}
	}
	
	public void gewaehreRabatt(Double rabatt) {
		this.betragBrutto = this.betragBrutto /(1 - (this.rabatt / 100));
		this.betrag7 = this.betrag7 / (1 - (this.rabatt / 100));
		this.betrag19 = this.betrag19 / (1 - (this.rabatt / 100));
		this.rabatt = rabatt;
		this.betragBrutto = this.betragBrutto * (1 - (this.rabatt / 100));
		this.betrag7 = this.betrag7 * (1 - (this.rabatt / 100));
		this.betrag19 = this.betrag19 * (1 - (this.rabatt / 100));
	}
	
	public void loescheArtikel(int n) {
		ArtikelVerkauf position = this.artikel.get(n);
		this.betragBrutto -= position.getPreis() * position.getAnzahl();
		if(position.getMwst() == 7) {
			this.betrag7 -= position.getPreis() * 0.07  * position.getAnzahl();
		} else if(position.getMwst() == 19) {
			this.betrag19 -= position.getPreis() * 0.19  * position.getAnzahl();
		}
		this.artikel.remove(position);
	}
	
	public double getBetrag7() {
		return this.betrag7;
	}
	
	public double getBetrag19() {
		return this.betrag19;
	}
	
	public double getBetragBrutto() {
		return this.betragBrutto;
	}
	
	public double getRabatt() {
		return this.rabatt;
	}
	
	public int getAnzahlPositionen() {
		return artikel.size();
	}
	
	public String[][] liefereTabDaten() {
		ArtikelVerkauf[] positionen = new ArtikelVerkauf[this.artikel.size()];
		positionen = this.artikel.toArray(positionen);
		String[][] returns = new String[positionen.length][6];
		for(int i = 0; i < positionen.length; i++) {
			returns[i][0] = String.valueOf(positionen[i].getEan());
			returns[i][1] = positionen[i].getBeschreibung();
			returns[i][2] = String.valueOf(positionen[i].getPreis());
			returns[i][3] = String.valueOf(positionen[i].getAnzahl());
			returns[i][4] = String.valueOf(positionen[i].getPreis() * positionen[i].getAnzahl());
			returns[i][5] = String.valueOf(positionen[i].getPosition());
		}
		
		return returns;
	}
	
	public ArtikelVerkauf[] liefereArtikel() {
		ArtikelVerkauf[] positionen = new ArtikelVerkauf[this.artikel.size()];
		positionen = this.artikel.toArray(positionen);
		return positionen;
	}
	
	public void fuehreVerkaufdurch() {
		for(int n = 0; n < artikel.size(); n++) {
			artikel.get(n).verkaufeArtikel(0);
		}
	}
}
