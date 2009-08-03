package terminKalender;

import hauptFenster.AktiveFenster;

import java.util.Arrays;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import patientenFenster.PatGrundPanel;
import RehaInternalFrame.JPatientInternal;

import sqlTools.ExUndHop;
import sqlTools.SqlInfo;
import stammDatenTools.RezTools;

public class TermineErfassen implements Runnable {
	String scanrez = null;
	Vector alleterm;
	Vector vec = null;
	Vector vec2 = null;
	String copyright = null;
	String heute = null;
	int erstfund = -1;
	String kollege = "";
	boolean firstfound;
	boolean ergebnis = true;
	public static boolean success = true;
	public static int errorint = 0;
	public TermineErfassen(String reznr, Vector termvec){
		scanrez = reznr;
		firstfound = false;

	}
	@Override
	public void run() {
		heute = datFunk.sHeute();
		copyright = "© ";
		int ret = -1;
		try {
			if((ret = testeVerordnung())==0){
				boolean termok = testeTermine(); 
				if(!termok){
					//System.out.println("Rezept steht an diesem Tag nicht im Kalender");
				}
				if(erstfund >= 0){
					scheibeTermin();
					JComponent patient = AktiveFenster.getFensterAlle("PatientenVerwaltung");
					if(patient != null){
						if(PatGrundPanel.thisClass.aktRezept.rezAngezeigt.equals(scanrez)){
							PatGrundPanel.thisClass.aktRezept.holeRezepte(
									PatGrundPanel.thisClass.aktPatID, scanrez);
						}
					}
					
				}else{
					System.out.println("Rezept steht an diesem Tag nicht im Kalender");
					setTerminSuccess(false);
					ergebnis = false;
				}
				
				if(firstfound && scanrez.startsWith("RH")){
					fahreFortMitTerminen();			
				}
			}else{
				if(ret > 0){
					setTerminSuccess(false);
					ergebnis = false;
					System.out.println("hier die Fehlerbehandlung einbauen---->Fehler = "+ret);
					switch (ret){
					case 1:
						System.out.println("Das Rezept nicht in Historie und nicht in Rez-Stamm");
					case 2:
						System.out.println("Das Rezept wurde bereits abgerechnet");
					case 3:
						System.out.println("Das Rezept wurde an diesen Tag bereits erfaßt");
					}
				}
			}
		} catch (Exception e) {
			setTerminSuccess(false);
			ergebnis = false;

		}
		System.out.println("Terminerfassen beendet");
		alleterm = null;
		return;
	}
	private void setTerminSuccess(boolean xsuccess){
		this.success = xsuccess;
	}
	public static boolean getTerminSuccess(){
		return success;
	}
	public static int getTerminError(){
		return errorint;
	}
	/********************/
	public int testeVerordnung() throws Exception{
		vec = SqlInfo.holeSatz("verordn","termine,anzahl1,pos1,pos2,pos3,pos3,hausbes"," rez_nr='"+scanrez+"'",Arrays.asList(new String[]{}));
		if(vec.size()==0){
			vec = SqlInfo.holeSatz("lza","termine"," rez_nr='"+scanrez+"'",Arrays.asList(new String[]{}));
			if(vec.size()==0){
				//System.out.println("Rezept ist weder im aktuellen Rezeptstamm noch in der Historie");
				return 1;
			}else{
				//System.out.println("Das Rezept wurde bereits abgerechnet");
				return 2;
			}
						
		}
		String termine = (String)vec.get(0);
		if(termine.contains(datFunk.sHeute())){
			//JOptionPane.showMessageDialog(null, "Dieser Termin wurde heute bereits erfaßt");
			return 3;
		}
		
		return 0;
	}
	/********************/	
	public void erfasseTermin(){
		Vector<String> pat_int = SqlInfo.holeSatz("verordn", "pat_intern,anzahl1,termine", "rez_nr='"+scanrez+"'", Arrays.asList(new String[] {}));
		if(pat_int.size()==0){

			return;
		}
	}
	/********************/	
	private boolean testeTermine() throws Exception{
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
		}else{
			if( !((String)obj[4]).contains(copyright.trim())){
				this.kollege = new String((String)obj[1]);
				string = "Rezeptnummer wurde gefunden bei Kollege "+(String)obj[1]+" an Block "+(Integer)obj[2]+" Rezeptnummer:"+(String)obj[3];
				String stmt = " sperre = '"+(String)obj[1]+heute+"'";
				//System.out.println(stmt);
				int gesperrt = SqlInfo.zaehleSaetze("flexlock", stmt);
				if( gesperrt == 0 ){
					//System.out.println("Datensatz ist nicht gesperrt");
					String sblock  = new Integer(  (((Integer)obj[2]/5)+1)  ).toString();
					stmt = "Update flexkc set T"+sblock+" = '"+copyright+(String)obj[4]+"' where datum = '"+(String)obj[7]+"' AND "+
						"behandler = '"+(String)obj[1]+"' AND TS"+sblock+" = '"+(String)obj[5]+"' AND T"+sblock+" = '"+(String)obj[4]+
						"' AND N"+sblock+" LIKE '%"+scanrez+"%' LIMIT 1";
					new ExUndHop().setzeStatement(new String(stmt));
					//System.out.println(stmt);	
				}else{
					JOptionPane.showMessageDialog(null, "Die Spalte ist momentan gesperrt, der Termin kann zwar\n"+
					"nicht markiert werden, wird aber im Rezeptstamm erfaßt");
				}
				ret = true;
			}else{
				this.kollege = new String((String)obj[1]);
				ret = false;
			}
		}	
		return ret;
	}
	/********************/
	private Object[] untersucheTermine() throws Exception{
		
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
					//((Vector)alleterm.get(i)).set((y*5), copyright+new String((String)obj[4]));
					gefunden = true;
					erstfund = i;
					break;
				}
			}
			if(gefunden){
				firstfound = true;
				break;
			}
		}
		return obj;
	}

	/********************/
	private void fahreFortMitTerminen()throws Exception{
		int spalten = alleterm.size();
		int mehrstellen = 0;
		//System.out.println("eingelesene Spalten = "+spalten);
		int i,y;
		Object[] obj = {new Boolean(false),null,null,null,null,null,null,null};
		for(i=(erstfund+1);i<spalten;i++){
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
					
					if( !((String)obj[4]).contains(copyright.trim())){
						mehrstellen++;
						String stmt = " sperre = '"+(String)obj[1]+heute+"'";
						int gesperrt = SqlInfo.zaehleSaetze("flexlock", stmt);
						if( gesperrt == 0 ){
							String sblock  = new Integer(  (((Integer)obj[2]/5)+1)  ).toString();
							stmt = "Update flexkc set T"+sblock+" = '"+copyright+(String)obj[4]+"' where datum = '"+(String)obj[7]+"' AND "+
								"behandler = '"+(String)obj[1]+"' AND TS"+sblock+" = '"+(String)obj[5]+"' AND T"+sblock+" = '"+(String)obj[4]+
								"' AND N"+sblock+" LIKE '%"+scanrez+"%' LIMIT 1";
							new ExUndHop().setzeStatement(new String(stmt));
							//System.out.println(stmt);	
						}else{
						}
					}
				}
			}
		}
		//System.out.println("Anzahl zusätzlicher Fundstellen = "+mehrstellen);
	}
	
	
	
	/********************/
	private void scheibeTermin() throws Exception{
		//System.out.println("Eintritt in schreibeTermin");
		int ikoll = (kollege.substring(0,1).equals("0") ?
					new Integer(kollege.substring(1,2)) :
					new Integer(kollege.substring(0,2)) 	
					);
		//System.out.println("Kollegen-Nummer = "+ikoll);
		this.kollege = ParameterLaden.getKollegenUeberDBZeile(ikoll);
		//String termkollege = 
		StringBuffer sbuftermine = new StringBuffer();
		vec2 = null;
		String terminneu = "";
		if(! ((String)vec.get(0)).trim().equals("")){
			
			sbuftermine.append((String)vec.get(0));
			//System.out.println("termine bisher = "+sbuftermine.toString());
			//hier die Einzelnen Termin holen
			vec2 = RezTools.splitteTermine(sbuftermine.toString());
			int anzahl = vec2.size();
			int lautrezept = new Integer((String)vec.get(1)); 
			if(anzahl >= lautrezept){
				String message = "";
				if(anzahl == lautrezept){
					message = "Auf dieses Rezept wurden bereits "+anzahl+" Behandlungen durchgeführt!"+
					"\nVerordnete Menge ist "+lautrezept+
					"\nDas Rezept ist somit bereits voll und darf für aktuelle Behandlung nicht mehr\n"+
					"verwendet werden!!!!\n\n"+
					"Wollen Sie die aktuelle Behandlung trotzdem auf dieses Rezept buchen?";
				}else{
					message = "Auf dieses Rezept wurden bereits "+anzahl+" Behandlungen durchgeführt!"+
					"\nVerordnete Menge ist "+lautrezept+
					"\nDas Rezept ist somit bereits übervoll und darf für aktuelle Behandlung nicht mehr\n"+
					"verwendet werden!!!!\n\n"+
					"Wollen Sie die aktuelle Behandlung trotzdem auf dieses Rezept buchen?";
				}
				int frage = JOptionPane.showConfirmDialog(null, message, "Benutzeranfrage", JOptionPane.YES_NO_OPTION);
				if(frage == JOptionPane.NO_OPTION){
				  return;
				}
				terminneu = macheNeuTermin("zu viele Behandlungen");
			}else{
				//System.out.println("Es sind noch Termine Frei");
				terminneu = macheNeuTermin("");			}
				
		}else{
			//System.out.println("der Termin ist der erste Termin.");
			terminneu = macheNeuTermin("");			
		}
		sbuftermine.append(terminneu);
		String cmd = "update verordn set termine='"+sbuftermine.toString()+"' where rez_nr='"+scanrez+"'";
		//System.out.println("cmd = "+cmd);
		new ExUndHop().setzeStatement(cmd);
	}

	/********************/
	private String macheNeuTermin(String text){
		String ret =
			datFunk.sHeute()+
			"@"+
			this.kollege+
			"@"+
			text+
			"@"+
			(String)vec.get(2)+
			( ((String)vec.get(3)).trim().equals("") ? "" : ","+ ((String)vec.get(3)) )+
			( ((String)vec.get(4)).trim().equals("") ? "" : ","+ ((String)vec.get(3)) )+
			( ((String)vec.get(5)).trim().equals("") ? "" : ","+ ((String)vec.get(3)) )+
			"@"+
			datFunk.sDatInSQL(datFunk.sHeute())+"\n";
		return ret;
			
	}
	
}
