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

public class GKVVerlaengerung {
	
	String xfdfFile = "";
	HashMap<String,String> hashMap = null;
	String formularpfad = null;
	String patid = null;
	String reader = null;
	
	public GKVVerlaengerung(String bid,String pfad,String xpatid){
		formularpfad = pfad;
		patid = xpatid;
		doSuche(bid);
	}
	private void doSuche(String bid){
		initHashMap();
		//                     0        1    2   3     4
		String cmd = "select namevor,strasse,plz,ort,geboren,"+
		//        5     6     7    8        9
				"F_74,Diag1, F_82,Diag2, aufdat3 from bericht2 where berichtid='"+bid+"'";
		Vector<Vector<String>> vec = SqlInfo.holeFelder(cmd);
 
		if(vec == null){
			return;
		}
		auswertenVector(vec);
		cmd = "select v_nummer,kassenid from pat5 where pat_intern='"+patid+"'";
		Vector<Vector<String>> vec2 = SqlInfo.holeFelder(cmd);
		hashMap.put("Vnummer",vec2.get(0).get(0));
		cmd = "select kassen_nam1,kassen_nam2,strasse,plz,ort,fax from kass_adr where id='"+vec2.get(0).get(1)+"'";
		Vector<Vector<String>> vec3 = SqlInfo.holeFelder(cmd);
		hashMap.put("Krankenkasse",vec3.get(0).get(0)+ (vec3.get(0).get(1).trim().equals("") ? "\n" : vec3.get(0).get(1)+"\n" )+
				vec3.get(0).get(2)+"\n"+vec3.get(0).get(3)+" "+vec3.get(0).get(4)+"\n"+
				"FAX: "+vec3.get(0).get(5));
		doStart();
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

	private void auswertenVector(Vector<Vector<String>> vec){
		hashMap.put("NameVorname",vec.get(0).get(0));
		hashMap.put("Adresse",vec.get(0).get(1)+", "+vec.get(0).get(2)+" "+vec.get(0).get(3));
		try{
			hashMap.put("Geboren",DatFunk.sDatInDeutsch(vec.get(0).get(4)));
			hashMap.put("Rehabeginn",DatFunk.sDatInDeutsch(vec.get(0).get(9)));
		}catch(Exception ex){

		}
		hashMap.put("ICD1-1",vec.get(0).get(5));
		hashMap.put("ICD1-Text1",vec.get(0).get(6));
		hashMap.put("ICD2-1",vec.get(0).get(7));
		hashMap.put("ICD2-Text1",vec.get(0).get(8));
		
	}
	private void initHashMap(){
		hashMap = new HashMap<String,String>();	
		hashMap.put("Krankenkasse","");
		hashMap.put("NameVorname","");
		hashMap.put("Adresse","");
		hashMap.put("Geboren","");
		hashMap.put("Vnummer","");
		hashMap.put("Rehabeginn","");
		hashMap.put("DatumVerlaengerung",DatFunk.sHeute());
		hashMap.put("ICD1-1","");
		hashMap.put("ICD2-1","");
		hashMap.put("ICD1-Text1","");
		hashMap.put("ICD1-Text2","");
		hashMap.put("ICD1-Text3","");
		hashMap.put("ICD2-Text1","");
		hashMap.put("ICD2-Text2","");
		hashMap.put("ICD2-Text3","");
		
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
					"<f href='"+formularpfad+"\\GKVVerlaengerung.pdf'/>"+System.getProperty("line.separator")+
					"</xfdf>"
					);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}


}
