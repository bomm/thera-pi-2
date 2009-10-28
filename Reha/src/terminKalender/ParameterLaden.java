package terminKalender;

import hauptFenster.Reha;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class ParameterLaden {


public static Vector<ArrayList> vKollegen = new Vector<ArrayList>();
public static Vector<Kollegen> vKKollegen = new Vector<Kollegen>();
public static Vector<ArrayList> pKollegen = new Vector<ArrayList>();
public static Vector<Kollegen> pKKollegen = new Vector<Kollegen>();

public static Vector<Vector> vKGPreise = new Vector<Vector>();
public static Vector<Vector> vMAPreise = new Vector<Vector>();
public static Vector<Vector> vERPreise = new Vector<Vector>();
public static Vector<Vector> vLOPreise = new Vector<Vector>();
public static Vector<Vector> vRHPreise = new Vector<Vector>();

/*
public static Vector<ArrayList> vKGPreise = new Vector<ArrayList>();
public static Vector<ArrayList> vMAPreise = new Vector<ArrayList>();
public static Vector<ArrayList> vERPreise = new Vector<ArrayList>();
public static Vector<ArrayList> vLOPreise = new Vector<ArrayList>();
public static Vector<ArrayList> vRHPreise = new Vector<ArrayList>();
*/

public static String[][] col;	
public static Kollegen [] cKollegen[]; 

public static int maxKalZeile = 0;

public static int suchen(String ss){
	int ret = -1;
	int lang = vKKollegen.size();
	int i;
	for (i=0;i<lang;i++){
		//System.out.println(vKKollegen.get(i).Matchcode);
		if(vKKollegen.get(i).Matchcode.equals(ss)){
			ret = i;
			break;
		}	
	}
return ret;
}

public static int getDBZeile(int kollege){
return vKKollegen.get(kollege).Reihe;
}
public static String getKollegenUeberReihe(int reihe){
	String ret ="";
	int lang = vKKollegen.size();
	int i;
	for (i=0;i<lang;i++){
		//System.out.println(vKKollegen.get(i).Matchcode);
		if(vKKollegen.get(i).Reihe == reihe){
			ret = vKKollegen.get(i).Matchcode;
			break;
		}	
	}
	return ret;
}

public static String getKollegenUeberDBZeile(int reihe){
	String ret = new String("");
	int lang = vKKollegen.size();
	int i;
	for (i=0;i<lang;i++){
		//System.out.println(vKKollegen.get(i).Matchcode);
		if(vKKollegen.get(i).Reihe == reihe){
			ret = vKKollegen.get(i).Matchcode;
			break;
		}	
	}
	return ret;
}

public static int getPosUeberReihe(int reihe){
	int ret = 0;
	int lang = vKKollegen.size();
	int i;
	for (i=0;i<lang;i++){
		if(vKKollegen.get(i).Reihe == reihe){
			ret = vKKollegen.get(i).Position;
			break;
		}	
	}
	return ret;
}

public static int getPosUeberDB(int dbzeile){
	int ret = 0;
	int lang = vKKollegen.size();
	int i;
	for (i=0;i<lang;i++){
		if(vKKollegen.get(i).Position == dbzeile){
			ret = vKKollegen.get(i).Reihe;
			break;
		}	
	}
	return ret;
}

public static String getMatchcode(int kollege){
	return vKKollegen.get(kollege).Matchcode;
	}
public static String getZeigen(int kollege){
	return vKKollegen.get(kollege).Zeigen;
	}
public static String getAbteilung(int kollege){
	return vKKollegen.get(kollege).Abteilung;
	}

public static void Init(){
	Reha obj = Reha.thisClass;
	Statement stmt = null;
	ResultSet rs = null;
	if(vKKollegen.size() > 0){
		vKKollegen.clear();
		vKollegen.clear();
		maxKalZeile = 0;
		//col = null;
	}

	
	try {
		stmt = obj.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE );
		try{
			int anz = 0;
			rs = stmt.executeQuery("select count(*) from kollegen2");
			if (rs.next()){
			anz = rs.getInt(1);
			}
			rs.close();
			//rs = stmt.executeQuery("SELECT * FROM flexkc WHERE datum >= '2008-01-14' AND datum <= '2008-01-20' AND behandler = '05BEHANDLER'");
		rs = stmt.executeQuery("SELECT Matchcode,Nachname,Nicht_Zeig,Kalzeile,Abteilung FROM kollegen2");

//		Kollegen cKollegen[] = new Kollegen([anz+1]);
		col = new String[anz+1][4];
		int i = 0;
		int durchlauf = 0;
		//int maxbehandler = 53;
		int maxblock = 0;
		int aktbehandler = 1;	
		
		//String sText = "";
		//zeit2 = System.currentTimeMillis();
		//jZeit.setText((zeit2-zeit1)+" ms");
		//List<Player> players = new ArrayList<Player>();
		ArrayList<String> aKollegen = new ArrayList<String>();
		//aSpaltenDaten.clear();
	 	aKollegen.add(new String("./."));
	 	aKollegen.add(new String(""));
	 	aKollegen.add(new String(""));
	 	aKollegen.add(new String("00"));
	 	//aKollegen.add(durchlauf);
	 	vKollegen.add((ArrayList)aKollegen.clone());

	 	vKKollegen.add(new Kollegen("./.","","",0,"","F",0));

	 	col[0][0] = (String) aKollegen.get(0);
	 	aKollegen.clear();
	 	durchlauf++;
	 	String test = new String();
	 	int itest = 0;
	 	while( rs.next()){
		 	aKollegen.add(new String(rs.getString("Matchcode")));
		 	test = rs.getString("Nachname");
		 	aKollegen.add((test != null ?  test : "" ));
		 	test = rs.getString("Nicht_Zeig");
		 	aKollegen.add((test != null ?  test : "F" ));
		 	itest = new Integer(rs.getString("Kalzeile"));
		 	if (itest > maxKalZeile){
		 		maxKalZeile = itest;
		 	}
		 	if(itest < 10){
		 		aKollegen.add("0"+ new Integer(itest).toString());
		 	}else{
		 		aKollegen.add(new Integer(itest).toString());
		 	}
		 	//aKollegen.add(itest);		 	
		 	vKollegen.add((ArrayList)aKollegen.clone());
		 	vKKollegen.add(new Kollegen(rs.getString("Matchcode"),
		 							rs.getString("Nachname"),
		 							rs.getString("Nicht_Zeig"),
		 							itest,
		 							rs.getString("Abteilung"),
		 							aKollegen.get(2),
		 							durchlauf) );
		 	//vKKollegen.add(new Kollegen(rs.getString("Matchcode"),rs.getString("Nachname"),rs.getString("Nicht_Zeig"),durchlauf) );
		 	col[durchlauf][0] = (String) aKollegen.get(0);
		 	col[durchlauf][1] = (String) aKollegen.get(1);
		 	col[durchlauf][2] = (String) aKollegen.get(2);
		 	//col[durchlauf][3] = (String) aKollegen.get(3);		 	
		 	aKollegen.clear();
		 	durchlauf++;
		}
	 	Collections.sort(vKKollegen);
	 	//System.out.println(vKKollegen);
	 	
	 	//System.out.println("Index von a-Wolf = "+suchen("Verwaltung"));
		}catch(SQLException ex){
			System.out.println("Kollegen1="+ex);
			  
		}
	}catch(SQLException ex){
		System.out.println("Kollegen2="+ex);
	}
	finally {
		if (rs != null) {
		try {
			rs.close();
		} catch (SQLException sqlEx) { // ignorieren }
			rs = null;
		}
		if (stmt != null) {
		try {
			stmt.close();
		} catch (SQLException sqlEx) { // ignorieren }
			stmt = null;
		}
		}
		}
	}
	
	
}
/***********Ende Klasse************/

public static void Passwort() {
	// TODO Auto-generated method stub
	Reha obj = Reha.thisClass;
	Statement stmt = null;
	ResultSet rs = null;
	if(pKollegen.size() > 0){
		pKollegen.clear();
	}
	
	
	try {
		stmt = obj.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE );
		try{
			int anz = 0;
			rs = stmt.executeQuery("select count(*) from kollegen2");
			if (rs.next()){
			anz = rs.getInt(1);
			}
			rs.close();

		rs = stmt.executeQuery("SELECT * from rehalogin");
		ArrayList aKollegen = new ArrayList();
	 	String test = new String();
	 	while( rs.next()){
	 		test = rs.getString("user");
		 	aKollegen.add((test != null ?  test : "" ));
		 	test = rs.getString("password");
		 	aKollegen.add((test != null ?  test : "" ));
	 		test = rs.getString("rights");
		 	aKollegen.add((test != null ?  test : "" ));
	 		test = rs.getString("email");
		 	aKollegen.add((test != null ?  test : "" ));		 	
	 		test = rs.getString("id");
		 	aKollegen.add((test != null ?  test : "" ));		 	
		 	pKollegen.add((ArrayList)aKollegen.clone());
		 	aKollegen.clear();
		}


		}catch(SQLException ex){
			System.out.println("Kollegen1="+ex);
			  
		}
	}catch(SQLException ex){
		System.out.println("Kollegen2="+ex);
	}
	finally {
		if (rs != null) {
		try {
			rs.close();
		} catch (SQLException sqlEx) { // ignore }
			rs = null;
		}
		if (stmt != null) {
		try {
			stmt.close();
		} catch (SQLException sqlEx) { // ignore }
			stmt = null;
		}
		}
		}
	}
	
}
/********************************************************/
public static void PreiseEinlesen(String preisklasse) {
	// TODO Auto-generated method stub
	Reha obj = Reha.thisClass;
	Statement stmt = null;
	ResultSet rs = null;
	for(int i = 0;i<1;i++){
		if(preisklasse == "KG" && vKGPreise.size() > 0){
			vKGPreise.clear();
			break;
		}
		if(preisklasse == "MA" && vMAPreise.size() > 0){
			vMAPreise.clear();
			break;
		}
		if(preisklasse == "ER" && vERPreise.size() > 0){
			vERPreise.clear();
			break;
		}
		if(preisklasse == "LO" && vLOPreise.size() > 0){
			vLOPreise.clear();
			break;
		}
		if(preisklasse == "RH" && vRHPreise.size() > 0){
			vRHPreise.clear();
			break;
		}
	}
	
	
	try {
		stmt = obj.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE );
		try{
		
		if(preisklasse=="KG"){
			rs = stmt.executeQuery("SELECT * from kgtarif");
		}else if(preisklasse=="MA"){
			rs = stmt.executeQuery("SELECT * from matarif");
		}else if(preisklasse=="ER"){
			rs = stmt.executeQuery("SELECT * from ertarif");
		}else if(preisklasse=="LO"){
			rs = stmt.executeQuery("SELECT * from lotarif");
		}else if(preisklasse=="RH"){
			rs = stmt.executeQuery("SELECT * from rhtarif");
		}
		
		//ArrayList<String> aPreise = new ArrayList<String>();
		Vector<String> vPreise = new Vector<String>();
	 	String test = new String();
	 	while( rs.next()){
	 		//System.out.println("Anzahl Spalten "+rs.getMetaData().getColumnCount());
	 		int i = rs.getMetaData().getColumnCount();
	 		int start;
	 		for(start=1;start<=i;start++){
		 		test = rs.getString(start);
		 		//aPreise.add((test != null ?  test : "" ));
		 		vPreise.add((test != null ?  test : "" ));
	 		}
	 		for(i = 0;i<1;i++){
	 			if(preisklasse == "KG"){
	 				//vKGPreise.add((ArrayList)aPreise.clone());
	 			 	//aPreise.clear();
	 				vKGPreise.add((Vector)vPreise.clone());
	 				vPreise.clear();
	 				break;
	 			}
	 			if(preisklasse == "MA"){
	 				//vMAPreise.add((ArrayList)aPreise.clone());
	 			 	//aPreise.clear();
	 				vMAPreise.add((Vector)vPreise.clone());
	 			 	vPreise.clear();

	 				break;
	 			}
	 			if(preisklasse == "ER"){
	 				//vERPreise.add((ArrayList)aPreise.clone());
	 			 	//aPreise.clear();
	 				vERPreise.add((Vector)vPreise.clone());
	 			 	vPreise.clear();

	 				break;
	 			}
	 			if(preisklasse == "LO"){
	 				//vLOPreise.add((ArrayList)aPreise.clone());
	 			 	//aPreise.clear();
	 				vLOPreise.add((Vector)vPreise.clone());
	 			 	vPreise.clear();
	 			 	
	 				break;
	 			}
	 			if(preisklasse == "RH"){
	 				//vRHPreise.add((ArrayList)aPreise.clone());
	 			 	//aPreise.clear();
	 				vRHPreise.add((Vector)vPreise.clone());
	 			 	vPreise.clear();	 			 	
	 				break;
	 			}
	 		}
	 		
		}
	 	Comparator<Vector> comparator = new Comparator<Vector>() {

			@Override
			public int compare(Vector o1, Vector o2) {
				
				String s1 = (String)((Vector)o1).get(0).toString();
				String s2 = (String)((Vector)o2).get(0).toString();
				return s1.compareTo(s2);
			}
		};
		for(int i = 0;i < 1;i++){
			if(preisklasse.equals("KG")){
				Collections.sort((Vector)vKGPreise,comparator);
				break;
			}
			if(preisklasse.equals("MA")){
				Collections.sort((Vector)vMAPreise,comparator);
				break;
			}
			if(preisklasse.equals("ER")){
				Collections.sort((Vector)vERPreise,comparator);
				break;
			}
			if(preisklasse.equals("LO")){
				Collections.sort((Vector)vLOPreise,comparator);
				break;
			}
			if(preisklasse.equals("RH")){
				Collections.sort((Vector)vRHPreise,comparator);
				break;
			}
		}		
		

		}catch(SQLException ex){
			System.out.println("Kollegen1="+ex);
			  
		}
	}catch(SQLException ex){
		System.out.println("Kollegen2="+ex);
	}
	finally {
		if (rs != null) {
		try {
			rs.close();
		} catch (SQLException sqlEx) { // ignore }
			rs = null;
		}
		if (stmt != null) {
		try {
			stmt.close();
		} catch (SQLException sqlEx) { // ignore }
			stmt = null;
		}
		}
		}
	}
	
}




/********************************************************/
}
class Kollegen implements Comparable<Kollegen> {
	  String Matchcode, Vorname, Nachname, Abteilung,Zeigen;
	  int Reihe,Position;
	  
	  public Kollegen (String m, String n, String v, int r,String a, String z,int p)   {
	    Matchcode = m;
	    Nachname = n;
	    Vorname = v;
	    Reihe = r;
	    Abteilung = a;
	    Zeigen = z;
	    Position = p;
	  }
	  public int compareTo(Kollegen o) {
	      //First order by name
	      int result = Matchcode.compareTo(o.Matchcode);
	      if (0 == result) {
	        //if names are equal order by age, youngest first
	        result = Reihe - o.Reihe;
	      }
	      return result;
	  }
} 



