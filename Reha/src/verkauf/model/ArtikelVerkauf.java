package verkauf.model;

public class ArtikelVerkauf extends Artikel{

	int anzahl, position;
	double preis, rabatt = 0;
	String beschreibung;
	
	public ArtikelVerkauf(long ean) {
		super(ean);
		this.preis = super.getPreis();
		this.beschreibung = super.getBeschreibung();
	}
	
	
	@Override
	public double getPreis() {
		return this.preis;
	}
	@Override
	public void setPreis(double preis) {
		this.preis = preis;
	}
	
	public void gewaehreRabatt(double rabatt) {
		this.rabatt = rabatt;
		this.preis = super.getPreis();
		this.preis = this.preis * (1 - (rabatt / 100));
	}
	
	public void setAnzahl(int n) {
		this.anzahl = n;
	}
	
	public void setPosition(int n) {
		this.position = n;
	}
	
	public int getAnzahl() {
		return this.anzahl;
	}
	
	public int getPosition() {
		return this.position;
	}
	
	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}
	
	@Override
	public String getBeschreibung() {
		return this.beschreibung;
	}
	
	@Override
	public void verkaufeArtikel(int n) {
		super.verkaufeArtikel(this.anzahl);
	}
	
	public double getRabatt() {
		return this.rabatt;
	}
	

}
