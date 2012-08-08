package pdftest2;



import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JTextField;

import org.thera_pi.tools.date.DatFunk;
import Tools.SqlInfo;


public class ZustimmungASP {

	
	
	JTextField[] tf1 = {null};
	String xfdfFile = "";
	String formularpfad = null;
	HashMap<String,String> hashMap = null;	
	public ZustimmungASP(String bid,String pfad){
		formularpfad = pfad;
		doSuche(bid);
	}
	
	

	private void doSuche(String bid){
		//String bid = tf1[0].getText().trim(); 
		initHashMap();
		System.out.println("select vnummer,namevor,geboren,strasse,plz,ort from bericht2 where berichtid='"+bid+"'");
		Vector<Vector<String>> vec = SqlInfo.holeFelder("select vnummer,namevor,geboren,strasse,plz,ort from bericht2 where berichtid='"+bid+"'");
		if(vec == null){
			return;
		}
		auswertenVector(vec);
		doStart();
	}
	private void auswertenVector(Vector<Vector<String>> ergebnis){
		hashMap.put("Versicherungsnummer",ergebnis.get(0).get(0));
		hashMap.put("Name",ergebnis.get(0).get(1));
		if(ergebnis.get(0).get(2).trim().length()==10){
			hashMap.put("Geburtsdatum",DatFunk.sDatInDeutsch(ergebnis.get(0).get(2)).replace(".", ""));			
		}
		hashMap.put("Straße",ergebnis.get(0).get(3));
		hashMap.put("PLZ",ergebnis.get(0).get(4));
		hashMap.put("Ort",ergebnis.get(0).get(5));
		hashMap.put("Ort, Datum","Reutlingen,den " + DatFunk.sHeute());
		
		

		
			
		
	}
	private void initHashMap(){
		hashMap = new HashMap<String,String>();	
		hashMap.put("Versicherungsnummer","");
		hashMap.put("Name","");
		hashMap.put("Geburtsdatum","");
		hashMap.put("Straße","");
		hashMap.put("PLZ","");
		hashMap.put("ORT","");
		hashMap.put("Ort/Datum","");
		
	}
	private void macheKopf(FileWriter fw){
	    try {
			fw.write(
					"<?xml version='1.0' encoding='iso-8859-1'?>"+System.getProperty("line.separator")+
					"<xfdf xmlns='http://ns.adobe.com/xfdf/' xml:space='preserve'>"+System.getProperty("line.separator")+
					"<fields>"+System.getProperty("line.separator")
					);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	private void macheFuss(FileWriter fw){
		try {
			fw.write(
					"</fields>"+System.getProperty("line.separator")+
					"<f href='"+formularpfad+"\\ASP Zustimmungserklärung des Patienten_NoRestriction.pdf'/>"+System.getProperty("line.separator")+
					"</xfdf>"
					);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}
	private void doStart(){
		Set entries = hashMap.entrySet();
	    Iterator it = entries.iterator();
	    FileWriter fw = null;
		try {
			xfdfFile = java.lang.System.getProperty("user.dir")+"/test.xfdf";
			fw = new FileWriter(new File(xfdfFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	    macheKopf(fw);
	    String whileBlock = "";
	    while(it.hasNext()){
	    	Map.Entry entry = (Map.Entry) it.next();
	    	whileBlock = "<field name=\"";
	    	whileBlock = whileBlock+entry.getKey().toString()+"\">"+System.getProperty("line.separator");
	    	whileBlock = whileBlock+"<value>";
	    	whileBlock = whileBlock+entry.getValue().toString();
	    	whileBlock = whileBlock+"</value>"+System.getProperty("line.separator");
	    	whileBlock = whileBlock+"</field>"+System.getProperty("line.separator");
	    	try {
				fw.write(whileBlock);
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
		macheFuss(fw);
		new LadeProg(xfdfFile);
		try {
			System.out.println("PDFLoader wird beendet in HauptProgramm");
			Rahmen.thisClass.conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
}
