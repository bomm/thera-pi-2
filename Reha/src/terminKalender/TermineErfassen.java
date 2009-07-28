package terminKalender;

import java.util.Arrays;
import java.util.Vector;

import javax.swing.JOptionPane;

import sqlTools.ExUndHop;
import sqlTools.SqlInfo;

public class TermineErfassen implements Runnable {
	String scanrez = null;
	Vector alleterm;
	String copyright = null;
	String heute = null;
	public TermineErfassen(String reznr){
		scanrez = reznr;

	}
	@Override
	public void run() {
		heute = datFunk.sHeute();
		copyright = "© ";
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
		boolean ret;
		alleterm = new Vector();
		alleterm = SqlInfo.holeSaetze("flexkc", " * ", 
				"datum='"+datFunk.sDatInSQL(heute)+"'", 
				Arrays.asList(new String[] {}));
		Object[] obj = untersucheTermine();
		String string = null;
		if(! (Boolean) obj[0]){
			ret = false;
			System.out.println("Rezeptnummer wurde nicht gefunden");
		}else{
			if( !((String)obj[4]).contains(copyright.trim())){
				string = "Rezeptnummer wurde gefunden bei Kollege "+(String)obj[1]+" an Block "+(Integer)obj[2]+" Rezeptnummer:"+(String)obj[3];
				String stmt = " sperre = '"+(String)obj[1]+heute+"'";
				System.out.println(stmt);
				int gesperrt = SqlInfo.zaehleSaetze("flexlock", stmt);
				if( gesperrt == 0 ){
					System.out.println("Datensatz ist nicht gesperrt");
					String sblock  = new Integer(  (((Integer)obj[2]/5)+1)  ).toString();
					stmt = "Update flexkc set T"+sblock+" = '"+copyright+(String)obj[4]+"' where datum = '"+(String)obj[7]+"' AND "+
						"behandler ='"+(String)obj[1]+"' AND TS"+sblock+" = '"+(String)obj[5]+"'";
					new ExUndHop().setzeStatement(new String(stmt));
					System.out.println(stmt);	
				}else{
					System.out.println("Achtung Datensatz ist gesperrt!!!!!!!!!!!!!!!!!!!!!!!!");
				}
			}
			ret = true;
		}	
		alleterm = null;
		System.out.println("**********feddisch***********");
		return ret;
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
					obj[5] = new String((String) ((Vector)alleterm.get(i)).get( ((y*5))+2 )); // Beginn
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
