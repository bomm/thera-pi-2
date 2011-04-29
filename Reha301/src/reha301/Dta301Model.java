package reha301;

import java.util.Vector;

import javax.swing.JOptionPane;

import org.jdesktop.swingx.JXTable;

import Tools.DatFunk;
import Tools.SqlInfo;
import Tools.StringTools;

public class Dta301Model {
	
	private String dtaId = null;
	private String dtaNachrichtAnlass = null;
	private String dtaNachrichtDatum = null;
	private String dtaKtraegerIK = null;
	private String dtaSenderIK = null;
	private String dtaVsnr = null;

	private boolean isRvTraegerKtraeger = true;
	private boolean dtaReha = true;
	
	private String dtaFallart = null;
	private String dtaEilfall = null;
	private String dtaAnzahl = null;
	
	private String rezNummer = null;
	private String barcodeForm = null;

	private String patAnrede = null;
	private String patNachname = null;
	private String patVorname = null;
	private String patStrasse = null;
	private String patPlz = null;
	private String patOrt = null;
	private String patGeboren = null;
	private String patVsnr = null;
	private String patIntern = null;

	private String patKassenId = null;
	private String patKassenIk = null;
	private String patKassenName = null;


	private String patArztName = null;
	private String patArztNummer = null;
	private String patArztId = null;
	private String patArztOrt = null;
	
	private String patTelefon = null;

	private String x_patAnrede = null;
	private String x_patNachname = null;
	private String x_patVorname = null;
	private String x_patStrasse = null;
	private String x_patPlz = null;
	private String x_patOrt = null;
	private String x_patGeboren = null;
	private String x_patVsnr = null;
	private String x_patIntern = null;
	private String x_patId = null;
	private String x_patKassenId = null;
	private String x_patKassenName = null;
	private String x_patKassenIk = null;
	private String x_patArztName = null;
	private String x_patArztId = null;
	private String x_patArztNummer = null;
	private String x_patArztOrt = null;
	private String x_patTelefon = null;
	private JXTable table = null;
	
	public Dta301Model(JXTable table,int row){
		this.setTable(table);
	}
	
	public void setXPatData(Vector<String> vec_patstamm){
		try{
			x_patAnrede = StringTools.EGross(vec_patstamm.get(1));
			x_patNachname = StringTools.EGross(vec_patstamm.get(2));
			x_patVorname = StringTools.EGross(vec_patstamm.get(3));
			x_patGeboren = DatFunk.sDatInDeutsch(vec_patstamm.get(4));
			x_patStrasse = StringTools.EGross(vec_patstamm.get(5));
			x_patPlz = vec_patstamm.get(6);
			x_patOrt = StringTools.EGross(vec_patstamm.get(7));
			x_patIntern = vec_patstamm.get(8);
			x_patKassenName = StringTools.EGross(vec_patstamm.get(9));
			x_patId = vec_patstamm.get(10);
			x_patKassenId = vec_patstamm.get(11);
			x_patArztName = StringTools.EGross(vec_patstamm.get(12));
			x_patArztId = vec_patstamm.get(13);
			x_patKassenIk = vec_patstamm.get(14);
			x_patArztNummer = vec_patstamm.get(15);
			x_patTelefon = vec_patstamm.get(16);
			x_patArztOrt = "";
		}catch(Exception ex){
			x_patAnrede = null;
			x_patNachname = null;
			x_patVorname = null;
			x_patGeboren = null;
			x_patStrasse = null;
			x_patPlz = null;
			x_patOrt = null;
			x_patIntern = null;
			x_patKassenName = null;
			x_patId = null;
			x_patKassenId = null;
			x_patArztName = null;
			x_patArztId = null;
			x_patKassenIk = null;
			x_patArztNummer = null;
			x_patTelefon = null;
			x_patArztOrt = null;
			JOptionPane.showMessageDialog(null,"Fehler beim Bezug der passenden Patientendaten im Thera-Pi Pat.Stamm");
		}
	}
	public void transferXPatDataTo301(){
		this.patAnrede= String.valueOf(x_patAnrede);
		this.patNachname = String.valueOf(x_patNachname);
		this.patVorname = String.valueOf(x_patVorname);
		this.patStrasse =  String.valueOf(x_patStrasse);
		this.patPlz =  String.valueOf(x_patPlz);
		this.patOrt =  String.valueOf(x_patOrt);
		this.patKassenName = String.valueOf(x_patKassenName);
		this.patKassenIk = String.valueOf(x_patKassenIk);
		this.patKassenId = String.valueOf(x_patKassenId);
		this.patGeboren = String.valueOf(x_patGeboren);
		this.patArztName = String.valueOf(x_patArztName);
		this.patArztNummer = String.valueOf(x_patArztNummer);
		this.patArztId = String.valueOf(x_patArztId);
		this.patIntern = String.valueOf(x_patIntern);
		this.patTelefon = String.valueOf(x_patTelefon);
		this.patArztOrt = String.valueOf(x_patArztOrt);
	}
	public void set301Data(){
		try{
			String pat = table.getValueAt(table.getSelectedRow(), 4).toString();
			String adress = table.getValueAt(table.getSelectedRow(), 5).toString();
			String vsnr = table.getValueAt(table.getSelectedRow(), 6).toString();
			String ktraeger = table.getValueAt(table.getSelectedRow(), 7).toString();
			String anlass = table.getValueAt(table.getSelectedRow(), 3).toString();
			String kkasse = table.getValueAt(table.getSelectedRow(), 8).toString();
			String geboren = DatFunk.sDatInDeutsch(table.getValueAt(table.getSelectedRow(), 12).toString());		
			this.patAnrede= (pat.split("#").length >= 1 ? pat.split("#")[0] : "");
			this.patNachname = (pat.split("#").length >= 2 ? pat.split("#")[1] : "");
			this.patVorname = (pat.split("#").length >= 3 ? pat.split("#")[2] : "");
			this.patStrasse =  (adress.split("#").length >= 1 ? adress.split("#")[0] : "");
			this.patPlz =  (adress.split("#").length >= 2 ? adress.split("#")[1] : "");
			this.patOrt =  (adress.split("#").length >= 3 ? adress.split("#")[2] : "");
			this.patVsnr = vsnr;
			try{
				this.dtaKtraegerIK = ktraeger;				
			}catch(Exception ex){
				this.dtaKtraegerIK = "";
			}

			try{
				this.patKassenName = (kkasse.split("#").length >=1 ? kkasse.split("#")[0] : "");	
			}catch(Exception ex){
				this.patKassenName = "";
			}
			try{
				this.patKassenIk = kkasse.split("#")[1];
				this.patKassenIk = (kkasse.split("#").length >=2 ? kkasse.split("#")[1] : "");	
			}catch(Exception ex){
				this.patKassenIk = "";
			}
	
			this.dtaNachrichtAnlass = anlass;
			this.patGeboren = geboren;
			this.patKassenId = null;
			try{
				this.patArztName = SqlInfo.holeEinzelFeld("select adr1 from dta301 where id='"+this.getDtaID()+"' LIMIT 1").split("#")[0];	
			}catch(Exception ex){
				this.patArztName = "";
			}
			
			this.patArztNummer = null;
			this.patArztId = null;
			this.patTelefon = SqlInfo.holeEinzelFeld("select adr0 from dta301 where id='"+this.getDtaID()+"' LIMIT 1");
			try{
				this.patArztOrt = SqlInfo.holeEinzelFeld("select adr1 from dta301 where id='"+this.getDtaID()+"' LIMIT 1").split("#")[1];
			}catch(Exception ex){
				this.patArztOrt = "";
			}
		}catch(Exception ex){
			ex.printStackTrace();
			this.patAnrede = null;
			this.patNachname = null;
			this.patVorname = null;
			this.patStrasse =  null;
			this.patPlz =  null;
			this.patOrt =  null;
			this.setPatVsnr(null);
			this.dtaKtraegerIK =  null;
			this.patKassenName =  null;
			this.patKassenIk =  null;	
			this.dtaNachrichtAnlass =  null;
			this.patGeboren =  null;
			this.patKassenId = null;
			this.patArztName = null;
			this.patArztNummer = null;
			this.patArztId = null;
			this.patTelefon = null;
			this.patArztOrt = null;
			JOptionPane.showMessageDialog(null,"Fehler beim Bezug der 301-er Daten, Klasse Dta301Model");
		}
	}
	public String getDtaID(){
		return this.dtaId;
	}
	public void setDtaID(){
		int row = table.getSelectedRow();
		row = table.convertRowIndexToModel(row);
		this.dtaId =  table.getModel().getValueAt(row, 11).toString() ;
		return;
	}
	
/*************
 * 
 * 
 * 
 * 
 * Getters & Setters
 * 
 * 
 * 
 */
	public void setDtaId(String dtaId) {
		this.dtaId = dtaId;
	}
	public String getDtaId() {
		return dtaId;
	}
	public void setDtaKtraegerIK(String dtaKtraegerIK) {
		this.dtaKtraegerIK = dtaKtraegerIK;
	}
	public String getDtaKtraegerIK() {
		return dtaKtraegerIK;
	}
	public void setDtaSenderIK(String dtaSenderIK) {
		this.dtaSenderIK = dtaSenderIK;
	}
	public String getDtaSenderIK() {
		return dtaSenderIK;
	}
	public void setDtaReha(boolean dtaReha) {
		this.dtaReha = dtaReha;
	}
	public boolean isDtaReha() {
		return dtaReha;
	}
	public void setDtaFallart(String dtaFallart) {
		this.dtaFallart = dtaFallart;
	}
	public String getDtaFallart() {
		return dtaFallart;
	}
	public void setDtaEilfall(String dtaEilfall) {
		this.dtaEilfall = dtaEilfall;
	}
	public String getDtaEilfall() {
		return dtaEilfall;
	}
	public void setDtaAnzahl(String dtaAnzahl) {
		this.dtaAnzahl = dtaAnzahl;
	}
	public String getDtaAnzahl() {
		return dtaAnzahl;
	}

	public void setPatAnrede(String patAnrede) {
		this.patAnrede = patAnrede;
	}
	public String getPatAnrede() {
		return patAnrede;
	}
	public void setPatNachname(String patNachname) {
		this.patNachname = patNachname;
	}
	public String getPatNachname() {
		return patNachname;
	}
	public void setPatVorname(String patVorname) {
		this.patVorname = patVorname;
	}
	public String getPatVorname() {
		return patVorname;
	}
	public void setPatStrasse(String patStrasse) {
		this.patStrasse = patStrasse;
	}
	public String getPatStrasse() {
		return patStrasse;
	}
	public void setPatPlz(String patPlz) {
		this.patPlz = patPlz;
	}
	public String getPatPlz() {
		return patPlz;
	}
	public void setPatOrt(String patOrt) {
		this.patOrt = patOrt;
	}
	public String getPatOrt() {
		return patOrt;
	}
	public void setPatGeboren(String patGeboren) {
		this.patGeboren = patGeboren;
	}
	public String getPatGeboren() {
		return patGeboren;
	}
	public void setRezNummer(String rezNummer) {
		this.rezNummer = rezNummer;
	}
	public String getRezNummer() {
		return rezNummer;
	}
	public void setDtaVsnr(String dtaVsnr) {
		this.dtaVsnr = dtaVsnr;
	}
	public String getDtaVsnr() {
		return dtaVsnr;
	}
	public void setDtaNachrichtAnlass(String dtaNachrichtAnlass) {
		this.dtaNachrichtAnlass = dtaNachrichtAnlass;
	}
	public String getDtaNachrichtAnlass() {
		return dtaNachrichtAnlass;
	}
	public void setDtaNachrichtDatum(String dtaNachrichtDatum) {
		this.dtaNachrichtDatum = dtaNachrichtDatum;
	}
	public String getDtaNachrichtDatum() {
		return dtaNachrichtDatum;
	}
	public void setRvTraegerKtraeger(boolean isRvTraegerKtraeger) {
		this.isRvTraegerKtraeger = isRvTraegerKtraeger;
	}
	public boolean isRvTraegerKtraeger() {
		return isRvTraegerKtraeger;
	}
	public void setPatVsnr(String patVsnr) {
		this.patVsnr = patVsnr;
	}
	public String getPatVsnr() {
		return patVsnr;
	}
	public void setX_patAnrede(String x_patAnrede) {
		this.x_patAnrede = x_patAnrede;
	}
	public String getX_patAnrede() {
		return x_patAnrede;
	}
	public void setX_patNachname(String x_patNachname) {
		this.x_patNachname = x_patNachname;
	}
	public String getX_patNachname() {
		return x_patNachname;
	}
	public void setX_patVorname(String x_patVorname) {
		this.x_patVorname = x_patVorname;
	}
	public String getX_patVorname() {
		return x_patVorname;
	}
	public void setX_patStrasse(String x_patStrasse) {
		this.x_patStrasse = x_patStrasse;
	}
	public String getX_patStrasse() {
		return x_patStrasse;
	}
	public void setX_patPlz(String x_patPlz) {
		this.x_patPlz = x_patPlz;
	}
	public String getX_patPlz() {
		return x_patPlz;
	}
	public void setX_patOrt(String x_patOrt) {
		this.x_patOrt = x_patOrt;
	}
	public String getX_patOrt() {
		return x_patOrt;
	}
	public void setX_patGeboren(String x_patGeboren) {
		this.x_patGeboren = x_patGeboren;
	}
	public String getX_patGeboren() {
		return x_patGeboren;
	}
	public void setX_patVsnr(String x_patVsnr) {
		this.x_patVsnr = x_patVsnr;
	}
	public String getX_patVsnr() {
		return x_patVsnr;
	}
	public void setX_patIntern(String x_patIntern) {
		this.x_patIntern = x_patIntern;
	}
	public String getX_patIntern() {
		return x_patIntern;
	}
	public void setX_patKassenId(String x_patKassenId) {
		this.x_patKassenId = x_patKassenId;
	}
	public String getX_patKassenId() {
		return x_patKassenId;
	}
	public void setX_patKassenName(String x_patKassenName) {
		this.x_patKassenName = x_patKassenName;
	}
	public String getX_patKassenName() {
		return x_patKassenName;
	}
	public void setX_patArztId(String x_patArztId) {
		this.x_patArztId = x_patArztId;
	}
	public String getX_patArztId() {
		return x_patArztId;
	}
	public void setX_patArztName(String x_patArztName) {
		this.x_patArztName = x_patArztName;
	}
	public String getX_patArztName() {
		return x_patArztName;
	}
	public void setX_patId(String x_patId) {
		this.x_patId = x_patId;
	}
	public String getX_patId() {
		return x_patId;
	}
	public void setPatIntern(String patIntern) {
		this.patIntern = patIntern;
	}
	public String getPatIntern() {
		return patIntern;
	}
	public void setPatKassenId(String patKassenId) {
		this.patKassenId = patKassenId;
	}
	public String getPatKassenId() {
		return patKassenId;
	}
	public void setPatKassenIk(String patKassenIk) {
		this.patKassenIk = patKassenIk;
	}
	public String getPatKassenIk() {
		return patKassenIk;
	}
	public void setPatKassenName(String patKassenName) {
		this.patKassenName = patKassenName;
	}
	public String getPatKassenName() {
		return patKassenName;
	}
	public void setPatArztName(String patArztName) {
		this.patArztName = patArztName;
	}
	public String getPatArztName() {
		return patArztName;
	}
	public void setPatArztNummer(String patArztNummer) {
		this.patArztNummer = patArztNummer;
	}
	public String getPatArztNummer() {
		return patArztNummer;
	}
	public void setPatArztId(String patArztId) {
		this.patArztId = patArztId;
	}
	public String getPatArztId() {
		return patArztId;
	}
	public void setBarcodeForm(String barcodeForm) {
		this.barcodeForm = barcodeForm;
	}
	public String getBarcodeForm() {
		return barcodeForm;
	}
	public void setX_patKassenIk(String x_patKassenIk) {
		this.x_patKassenIk = x_patKassenIk;
	}

	public String getX_patKassenIk() {
		return x_patKassenIk;
	}
	public void setX_patArztNummer(String x_patArztNummer) {
		this.x_patArztNummer = x_patArztNummer;
	}

	public String getX_patArztNummer() {
		return x_patArztNummer;
	}

	public void setPatTelefon(String patTelefon) {
		this.patTelefon = patTelefon;
	}

	public String getPatTelefon() {
		return patTelefon;
	}

	public void setX_patTelefon(String x_patTelefon) {
		this.x_patTelefon = x_patTelefon;
	}

	public String getX_patTelefon() {
		return x_patTelefon;
	}

	public void setPatArztOrt(String patArztOrt) {
		this.patArztOrt = patArztOrt;
	}

	public String getPatArztOrt() {
		return patArztOrt;
	}

	public void setX_patArztOrt(String x_patArztOrt) {
		this.x_patArztOrt = x_patArztOrt;
	}

	public String getX_patArztOrt() {
		return x_patArztOrt;
	}

	public void setTable(JXTable table) {
		this.table = table;
	}
	public JXTable getTable() {
		return table;
	}
	/**************************************************************/
	public void show_X_PatData(){
		StringBuffer buf = new StringBuffer();
		buf.append("***********Beginn Pat_X-Data****************\n");
		buf.append("Anrede     "+this.x_patAnrede+"\n");
		buf.append("Nachname   "+this.x_patNachname+"\n");
		buf.append("Vorname    "+this.x_patVorname+"\n");
		buf.append("Geboren    "+this.x_patGeboren+"\n");
		buf.append("Strasse    "+this.x_patStrasse+"\n");
		buf.append("Plz        "+this.x_patPlz+"\n");
		buf.append("Ort        "+this.x_patOrt+"\n");
		buf.append("Pat_Intern "+this.x_patIntern+"\n");
		buf.append("KassenName "+this.x_patKassenName+"\n");
		buf.append("KassenIk   "+this.x_patKassenIk+"\n");
		buf.append("KassenId   "+this.x_patKassenId+"\n");
		buf.append("ArztName   "+this.x_patArztName+"\n");
		buf.append("ArztNummer "+this.x_patArztNummer+"\n");
		buf.append("ArztId     "+this.x_patArztId+"\n");
		buf.append("PatId      "+this.x_patId+"\n");
		buf.append("***********Ende Pat_X-Data******************\n");
		//System.out.println(buf.toString());
	}
	public void show301Data(){
		StringBuffer buf = new StringBuffer();
		buf.append("***********Beginn 301-Data****************\n");
		buf.append("Anrede     "+this.patAnrede+"\n");
		buf.append("Nachname   "+this.patNachname+"\n");
		buf.append("Vorname    "+this.patVorname+"\n");
		buf.append("Geboren    "+this.patGeboren+"\n");
		buf.append("Strasse    "+this.patStrasse+"\n");
		buf.append("Plz        "+this.patPlz+"\n");
		buf.append("Ort        "+this.patOrt+"\n");
		buf.append("VSNR       "+this.patVsnr+"\n");
		buf.append("Pat_Intern "+this.patIntern+"\n");
		buf.append("KassenName "+this.patKassenName+"\n");
		buf.append("KassenId   "+this.patKassenId+"\n");
		buf.append("KassenIK   "+this.patKassenIk+"\n");
		buf.append("ArztName   "+this.patArztName+"\n");
		buf.append("ArztNummer "+this.patArztNummer+"\n");
		buf.append("ArztId     "+this.patArztId+"\n");
		buf.append("Kostentr.  "+this.dtaKtraegerIK+"\n");
		buf.append("Anlass     "+this.dtaNachrichtAnlass+"\n");
		buf.append("***********Ende 301-Data******************\n");
		//System.out.println(buf.toString());
	}
	public void showRezeptData(){
		StringBuffer buf = new StringBuffer();
		buf.append("***********Beginn Rezept-Data****************\n");
		buf.append("RezeptNr.   "+this.rezNummer+"\n");
		buf.append("BarcodeForm "+this.barcodeForm+"\n");
		buf.append("***********Ende Rezept-Data******************\n");
		//System.out.println(buf.toString());
	}
}