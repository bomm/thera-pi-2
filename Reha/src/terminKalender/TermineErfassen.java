package terminKalender;

import java.util.Arrays;
import java.util.Vector;

import javax.swing.JOptionPane;

import sqlTools.SqlInfo;

public class TermineErfassen implements Runnable {
	String scanrez = null;
	Vector alleterm;
	public TermineErfassen(String reznr){
		scanrez = reznr;

	}
	@Override
	public void run() {
		testeTermine();
		// TODO Auto-generated method stub
		
	}
	
	public void erfasseTermin(){
		Vector<String> pat_int = SqlInfo.holeSatz("verordn", "pat_intern,anzahl1,termine", "rez_nr='"+scanrez+"'", Arrays.asList(new String[] {}));
		if(pat_int.size()==0){

			return;
		}
	}
	private boolean testeTermine(){
		long zeit1 = System.currentTimeMillis();
		alleterm = new Vector();
		alleterm = SqlInfo.holeSaetze("flexkc", " * ", 
				"datum='"+datFunk.sDatInSQL(datFunk.sHeute())+"'", 
				Arrays.asList(new String[] {}));
		Object[] obj = untersucheTermine();
		String string = null;
		if(! (Boolean) obj[0]){
			System.out.println("Rezeptnummer wurde nicht gefunden");
		}else{
			string = "Rezeptnummer wurde gefunden bei Kollege "+(String)obj[1]+" an Block "+(Integer)obj[2]+" Rezeptnummer:"+(String)obj[3];

			String stmt = "sperre='"+(String)obj[1]+datFunk.sDatInDeutsch((String)obj[7])+"'";
			System.out.println(stmt);
			if( SqlInfo.holeSatz("flexlock", "sperre", stmt, Arrays.asList(new String[]{})).size() == 0 ){
				System.out.println("Datensatz ist nicht gesperrt");
				//"update flexlock set "
			}else{
				System.out.println("Datensatz ist nicht gesperrt!!!!!!!!!!!!!!!!!!!!!!!!");
			}
		}
		JOptionPane.showMessageDialog(null,string+"\n\nDauer für das Einlesen aller akutellen Termindaten = "+(System.currentTimeMillis()-zeit1)+" Millisekunden");
		alleterm = null;
		return true;
	}
	private Object[] untersucheTermine(){
		
		int spalten = alleterm.size();
		//System.out.println("eingelesene Spalten = "+spalten);
		int i,y;
		boolean gefunden = false;
		Object[] obj = {new Boolean(false),null,null,null,null,null,null,null};
		for(i=0;i<spalten;i++){
			int bloecke = ((Vector)alleterm.get(0)).size();
			int belegt = new Integer( (String) ((Vector)alleterm.get(i)).get(bloecke-6) );
			for(y=0;y<belegt;y++){
				//int block = ((y*5)+1);
				if( ((String) ((Vector)alleterm.get(i)).get( ((y*5)+1) )).contains(scanrez) ){
					obj[0] = new Boolean(true); //gefunden
					obj[1] = new String((String) ((Vector)alleterm.get(i)).get(bloecke-4) );//Kollege
					obj[2] = new Integer(((y*5)+1));//Blocknummer
					obj[3] = new String((String) ((Vector)alleterm.get(i)).get( ((y*5)+1) )); // Rezeptnummer
					obj[4] = new String((String) ((Vector)alleterm.get(i)).get( ((y*5)) )); // Name
					obj[5] = new String((String) ((Vector)alleterm.get(i)).get( ((y*5)) )); // Beginn
					obj[6] = new String((String) ((Vector)alleterm.get(i)).get( ((y*5)) )); // Name
					obj[7] = new String((String) ((Vector)alleterm.get(i)).get(bloecke-2) );//Datum
					gefunden = true;
					break;
				}
			}
			if(gefunden){
				break;
			}
		}
		alleterm = null;
		return obj;
	}


}
