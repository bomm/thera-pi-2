package Tools;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import CommonTools.DatFunk;
import CommonTools.SqlInfo;


public class RezTools {
	
	public static String getLangtextFromID(String id,Vector<Vector<String>> vec){
		try{
		int lang = vec.size(),i;
		int idpos = vec.get(0).size()-1;
		String ret = "kein Lantext vorhanden";
		for(i = 0; i < lang;i++){
			if( vec.get(i).get(idpos).equals(id)){
				ret = vec.get(i).get(0).toString();
				break;
			}
		}
		return ret;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	public static String getPreisAktFromID(String id,Vector<Vector<String>> vec){
		int lang = vec.size(),i;
		int idpos = vec.get(0).size()-1;
		String ret = "0.00";
		for(i = 0; i < lang;i++){
			if( vec.get(i).get(idpos).equals(id)){
				ret = vec.get(i).get(3).toString();
				break;
			}
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	public static Vector<String> holeEinzelTermineAusRezept(String xreznr,String termine){
		Vector<String> xvec = null;
		Vector<String> retvec = new Vector<String>();
		String terms = null;
		if(termine.equals("")){
			xvec = SqlInfo.holeSatz("verordn", "termine,pat_intern", "rez_nr='"+xreznr+"'", Arrays.asList(new String[] {}));			
			if(xvec.size()==0){
				return (Vector<String>)retvec.clone();
			}else{
				terms = (String) xvec.get(0);	
			}
		}else{
			terms = termine;
		}
		if(terms==null){
			return (Vector<String>)retvec.clone();
		}
		if(terms.equals("")){
			return (Vector<String>)retvec.clone();
		}
		String[] tlines = terms.split("\n");
		int lines = tlines.length;
		String[] terdat = null;
		for(int i = 0;i<lines;i++){
			terdat = tlines[i].split("@");
			//int ieinzel = terdat.length;
			retvec.add((terdat[0].trim().equals("") ? "  .  .    " : String.valueOf(terdat[0])));
		}
		Comparator<String> comparator = new Comparator<String>() {
			public int compare(String s1, String s2) {
		        String strings1 = DatFunk.sDatInSQL(s1);
		        String strings2 = DatFunk.sDatInSQL(s2);
		        return strings1.compareTo(strings2);
		    }
		};	
		Collections.sort(retvec,comparator);
		return (Vector<String>)retvec.clone();
	}


}
