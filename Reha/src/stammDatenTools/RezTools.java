package stammDatenTools;

import java.util.Arrays;
import java.util.Vector;

import patientenFenster.PatGrundPanel;

import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;

public class RezTools {
	
	public static Vector<String> holeEinzelTermineAusRezept(String xreznr){
		Vector<String> xvec = null;
		Vector retvec = new Vector();
		xvec = SqlInfo.holeSatz("verordn", "termine", "rez_nr='"+xreznr+"'", Arrays.asList(new String[] {}));			

		String terms = (String) xvec.get(0);
		if(terms==null){
			return (Vector)retvec.clone();
		}
		if(terms.equals("")){
			return (Vector)retvec.clone();
		}
		String[] tlines = terms.split("\n");
		int lines = tlines.length;

		for(int i = 0;i<lines;i++){
			String[] terdat = tlines[i].split("@");
			int ieinzel = terdat.length;
			retvec.add(new String((terdat[0].trim().equals("") ? "  .  .    " : terdat[0])));
		}
		return (Vector)retvec.clone();
	}
	public static void constructRezHMap(){
		
	}

}
