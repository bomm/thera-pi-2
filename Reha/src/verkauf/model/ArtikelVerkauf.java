package verkauf.model;

public class ArtikelVerkauf extends Artikel{

	int position;
	double preis, rabatt = 0, anzahl = 1;
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
		if(rabatt != 0) {
			this.rabatt = rabatt;
			this.preis = super.getPreis();
			this.preis = this.preis * (1 - (rabatt / 100));
		}
	}
	
	public void setAnzahl(double d) {
		this.anzahl = d;
	}
	
	public void setPosition(int n) {
		this.position = n;
	}
	
	public double getAnzahl() {
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
	public void verkaufeArtikel(double anzahl, String vnummer, Double vpreis, int patid) {
		super.verkaufeArtikel(this.anzahl, vnummer, this.preis, patid);
	}
	
	public double getRabatt() {
		return this.rabatt;
	}
	

}
