package hmrCheck;

import java.util.Arrays;
import java.util.Vector;

import javax.swing.JOptionPane;

import sqlTools.SqlInfo;

public class HMRCheck {
	Vector<Integer> anzahl = null;
	Vector<String> positionen = null;
	Vector<Vector<String>> preisvec = null;
	String indischluessel = null;
	String diszis[] = {"2","1","5","3","8"};
	int disziplin;
	int preisgruppe;
	final String maxanzahl = "Die Höchstmenge pro Rezept bei ";
	final String rotein = "<>";
	public HMRCheck(String indi,int idiszi,Vector<Integer> vecanzahl,Vector<String>vecpositionen,
			int xpreisgruppe,Vector<Vector<String>> xpreisvec){
		indischluessel = indi;
		disziplin = idiszi;
		anzahl = vecanzahl;
		positionen = vecpositionen;
		preisgruppe = xpreisgruppe;
		preisvec = xpreisvec;
		//aktualisiereHMRs();
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
		Vector<Vector<String>> vec = SqlInfo.holeFelder("select * from hmrcheck where indischluessel='"+
				indischluessel+"' LIMIT 1");
		if(vec.size() <= 0){
			JOptionPane.showMessageDialog(null,"Indikationsschlüssel "+indischluessel+" unbekannt!"); 
			return false;
		}
		int maxprorezept = Integer.parseInt(vec.get(0).get(2));
		String[] vorrangig = vec.get(0).get(3).split("@");
		String[] ergaenzend = vec.get(0).get(5).split("@");
		for(int i = 0; i < vorrangig.length;i++){
			vorrangig[i] = diszis[disziplin]+vorrangig[i];
		}
		for(int i = 0; i < ergaenzend.length;i++){
			ergaenzend[i] = diszis[disziplin]+ergaenzend[i];
		}
		for(int i = 0; i < anzahl.size();i++){
			if(anzahl.get(i) > maxprorezept){
				JOptionPane.showMessageDialog(null,"<html><b>Bei Indikationsschlüssel "+indischluessel+" sind maximal\n"+Integer.toString(maxprorezept)+" Behandlungen pro Rezept erlaubt!!</b></html>"); 
				return false;
			}
		}
		try{
			for(int i = 0; i < positionen.size();i++){
				if(i==0){
					if(! Arrays.asList(vorrangig).contains(positionen.get(i))){
						showDialog(true,getHeilmittel(positionen.get(i)),positionen.get(i),vorrangig);
						/*
						JOptionPane.showMessageDialog(null,"Bei Indikationsschlüssel "+indischluessel+" ist das vorrangige Heilmittel\n"+
								"--> "+getHeilmittel(positionen.get(i))+" <-- "+
								" nicht erlaubt!!\n\n"+"Mögliche vorrangie Heilmittel sind:\n"+getErlaubteHeilmittel(vorrangig));
						*/		
						return false;
					}
				}else{
					if(! Arrays.asList(ergaenzend).contains(positionen.get(i))){
						showDialog(false,getHeilmittel(positionen.get(i)),positionen.get(i),ergaenzend);
						/*
						JOptionPane.showMessageDialog(null,"Bei Indikationsschlüssel "+indischluessel+" ist das ergaenzende Heilmittel\n"+
								"--> "+getHeilmittel(positionen.get(i))+" <-- "+
								" nicht erlaubt!!\n\n"+"Mögliche ergaenzende Heilmittel sind:\n"+getErlaubteHeilmittel(ergaenzend));
						*/		
						return false;
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
		return ret;
	}
	private void showDialog(boolean vorrangig,String heilmittel,String hmpos,String[] positionen){
		String meldung = "<html>"+"Bei dem Indikationsschlüssel <b><font color='#ff0000'>"+indischluessel+"</font></b> ist das "+(vorrangig ? "vorrangige " : "ergänzende")+
		" Heilmittel<br><br>--> <b><font color='#ff0000'>"+heilmittel+"</font></b> <-- nicht erlaubt!<br><br><br>"+
		"Mögliche "+(vorrangig ? "vorrangige " : "ergänzende")+" Heilmittel sind:<br><b><font color='#ff0000'>"+
		getErlaubteHeilmittel(positionen)+"</font></b></html>";
		JOptionPane.showMessageDialog(null, meldung);
		
	}
	/************************/
	private String getErlaubteHeilmittel(String[] heilmittel){
		StringBuffer buf = new StringBuffer();
		String hm = "";
		for(int i = 0;i < heilmittel.length;i++){
			hm = getHeilmittel(heilmittel[i]);
			if(!hm.equals("")){
				buf.append(getHeilmittel(heilmittel[i])+"<br>");				
			}
		}
		return (buf.toString().equals("") ? "<br>keine<br>" : buf.toString());
	}
	/************************/	
	private String getHeilmittel(String heilmittel){
		for(int i = 0;i < preisvec.size();i++){
			if(preisvec.get(i).get(2).equals(heilmittel)){
				return preisvec.get(i).get(0);
			}
		}
		return "";
	}
	/************************/
	/*
	private void aktualisiereHMRs(){
		Vector<Vector<String>> vec = SqlInfo.holeFelder("select ergaenzend,maxergaenzend,id from hmrcheck  where ergaenzend LIKE '%1508%'");
		String sanzahl = "";
		String cmd = "";
		System.out.println("Anzahl indis = "+vec.size());
		for(int i = 0; i < vec.size();i++){
			
			try{
				sanzahl = vec.get(i).get(1).split("@")[0];
			}catch(Exception ex){
				sanzahl = "6";
			}
			cmd = "update hmrcheck set ergaenzend='"+vec.get(i).get(0)+"@1531', maxergaenzend='"+
			vec.get(i).get(1)+"@"+sanzahl+"' where id ='"+vec.get(i).get(2)+"' LIMIT 1";
			System.out.println(cmd);
			SqlInfo.sqlAusfuehren(cmd);
		}
	}
	*/
}
