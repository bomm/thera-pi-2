package pdftest2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import Tools.DatFunk;
import Tools.SqlInfo;

public class BFAWiederEingliederung {
	String xfdfFile = "";
	HashMap<String,String> hashMap = null;
	String formularpfad = null;
	String reader = null;
	public BFAWiederEingliederung(String bid,String pfad){
		formularpfad = pfad;
		doSuche(bid);
	}
	private void initHashMap(){
		hashMap = new HashMap<String,String>();	
		hashMap.put("VERS_VSNR1_1","");
		hashMap.put("14_BKZ","");
		hashMap.put("18_MSNR","");
		hashMap.put("NAME_1","");
		hashMap.put("14_Geb.datum","");
		hashMap.put("STASSE_1","");
		
		
	}
	private void auswertenVector(Vector<Vector<String>> ergebnis){
		hashMap.put("VERS_VSNR1_1",ergebnis.get(0).get(0));
		hashMap.put("14_BKZ",ergebnis.get(0).get(1));
		hashMap.put("18_MSNR",ergebnis.get(0).get(2));
		hashMap.put("NAME_1",ergebnis.get(0).get(3));
		if(ergebnis.get(0).get(4).trim().length()==10){
			String deutschdat = DatFunk.sDatInDeutsch(ergebnis.get(0).get(4));  
			hashMap.put("14_Geb.datum",deutschdat.substring(0,2)+deutschdat.substring(3,5)+deutschdat.substring(8));
		}
		hashMap.put("STASSE_1",ergebnis.get(0).get(5)+", "+ergebnis.get(0).get(6)+" "+ergebnis.get(0).get(7));
	}
	private void doSuche(String bid){
 
		initHashMap();
		Vector<Vector<String>> vec = SqlInfo.holeFelder("select vnummer,aigr,msnr,namevor,geboren,strasse,plz,ort from bericht2 where berichtid='"+bid+"'");
		if(vec == null){
			return;
		}
		auswertenVector(vec);
		doStart();
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
					"<f href='"+formularpfad+"\\BfA_Stufenplan_neu.pdf'/>"+System.getProperty("line.separator")+
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
			System.out.println(xfdfFile);
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
