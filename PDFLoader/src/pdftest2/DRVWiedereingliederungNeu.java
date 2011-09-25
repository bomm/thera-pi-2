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

import Tools.DatFunk;
import Tools.SqlInfo;

public class DRVWiedereingliederungNeu{
	String xfdfFile = "";
	HashMap<String,String> hashMap = null;
	String formularpfad = null;
	String reader = null;
	final int vnummer = 0; 
	final int aigr = 1;
	final int msnr = 2;
	final int namevor = 3;
	final int geboren = 4;
	final int strasse = 5;
	final int plz = 6;
	final int ort = 7;
	
	
	public DRVWiedereingliederungNeu(String bid,String pfad){
		formularpfad = pfad;
		doSuche(bid);
	}
	private void initHashMap(){
		hashMap = new HashMap<String,String>();
		//G833
		hashMap.put("VERS_VSNR","");
		hashMap.put("VERS_KENNZ","");
		hashMap.put("VERS_NAME_VORNAME","");
		hashMap.put("VERS_GEBDAT","");
		hashMap.put("VERS_GEBORT","");
		hashMap.put("VERS_REHA_ENDE","");
		hashMap.put("ORT_DAT","");
		//G834
		hashMap.put("VERS_NAME","");
		hashMap.put("ARZT_ORT_DAT","");
		hashMap.put("SBN_VERS_VSNR","");
		hashMap.put("SBN_VERS_KENNZ","");
		//G836
		hashMap.put("VERS_ANSCHRIFT","");
		//G842
		hashMap.put("ARZT_ORT_DAT","");
		
		
	}
	/*
	final int vnummer = 0; 
	final int aigr = 1;
	final int msnr = 2;
	final int namevor = 3;
	final int geboren = 4;
	final int strasse = 5;
	final int plz = 6;
	final int ort = 7;
	*/
	private void auswertenVector(Vector<Vector<String>> ergebnis){
		try{
			hashMap.put("ORT_DAT","Reutlingen, "+DatFunk.sHeute());
			hashMap.put("ARZT_ORT_DAT","Reutlingen, "+DatFunk.sHeute());

			
			hashMap.put("VERS_VSNR",ergebnis.get(0).get(vnummer) );
			hashMap.put("SB_VERS_VSNR",ergebnis.get(0).get(vnummer) );
			hashMap.put("VERS_KENNZ",ergebnis.get(0).get(aigr) );
			hashMap.put("SBN_VERS_KENNZ",ergebnis.get(0).get(aigr));
			
			hashMap.put("VERS_NAME_VORNAME",ergebnis.get(0).get(namevor));
			hashMap.put("VERS_NAME",ergebnis.get(0).get(namevor));
			

			
			hashMap.put("VERS_GEBORT",ergebnis.get(0).get(strasse)+", "+
					ergebnis.get(0).get(plz)+" "+ergebnis.get(0).get(ort));
			hashMap.put("VERS_ANSCHRIFT",ergebnis.get(0).get(strasse)+", "+
					ergebnis.get(0).get(plz)+" "+ergebnis.get(0).get(ort));


			hashMap.put("VERS_GEBDAT",DatFunk.sDatInDeutsch(ergebnis.get(0).get(geboren)).replace(".", ""));
			
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
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
					"<f href='"+formularpfad+"\\WiedereingliederungSammelmappe1.pdf'/>"+System.getProperty("line.separator")+
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
