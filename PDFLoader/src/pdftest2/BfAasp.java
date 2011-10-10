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

import javax.swing.JButton;
import javax.swing.JTextField;

import Tools.DatFunk;
import Tools.SqlInfo;


public class BfAasp  {

	
	JButton bnr1;
	JTextField[] tf1 = {null};
	String xfdfFile = "";
	HashMap<String,String> hashMap = null;
	String formularpfad = null;
	public BfAasp(String bid,String pfad){
		formularpfad = pfad;
		doSuche(bid);
	}

	
	
	private void doSuche(String bid){
		initHashMap();
		System.out.println("select * from bericht2 where berichtid='"+bid+"'");
		Vector<Vector<String>> vec = SqlInfo.holeFelder("select * from bericht2 where berichtid='"+bid+"'");
		if(vec == null){
			return;
		}
		auswertenVector(vec);
		doStart();
	}
	private void auswertenVector(Vector<Vector<String>> ergebnis){
		hashMap.put("VERS_VSNR1_1",ergebnis.get(0).get(2));
		hashMap.put("14_BKZ",ergebnis.get(0).get(153));
		hashMap.put("MSNR",ergebnis.get(0).get(9));
		hashMap.put("REHA_1","Reutlinger Therapie- und Analysezentrum\nMarie-Curie-Str.1\n72760 Reutlingen\nTel.: 07121/96170");
		hashMap.put("INST_KENNZ_1","510841109");
		hashMap.put("ABT_1","2300");
		
		hashMap.put("VERS_N_VN_1",ergebnis.get(0).get(3));
		hashMap.put("STR_HA_PLZ_ORT1",ergebnis.get(0).get(5)+", "+ergebnis.get(0).get(6)+" "+ergebnis.get(0).get(7));
		hashMap.put("AW_BETRIEB_1","987");
		if(ergebnis.get(0).get(15).trim().length()==10){
			hashMap.put("VOM_1",DatFunk.sDatInDeutsch(ergebnis.get(0).get(15)));			
		}
		if(ergebnis.get(0).get(16).trim().length()==10){
			hashMap.put("BIS_1",DatFunk.sDatInDeutsch(ergebnis.get(0).get(16)));			
		}
		hashMap.put("ENTLA_1",ergebnis.get(0).get(19)+"\n"+ergebnis.get(0).get(24)+"\n"+ergebnis.get(0).get(29)+"\n"+ergebnis.get(0).get(34)+"\n"+ergebnis.get(0).get(39));
		
		hashMap.put("HAUF_1","24");
		hashMap.put("DAU_1","3");
		hashMap.put("AW_ART1","985");
		//hashMap.put("AW_ART2",ergebnis.get(0).get(23));
		//hashMap.put("AW_ART3",ergebnis.get(0).get(23));
		//hashMap.put("frühs._Beginn",ergebnis.get(0).get());
		
		hashMap.put("ORT_1","Reutlinger Therapie- und Analysezentrum GmbH");
		hashMap.put("STR_HA_PLZ_ORT2","Marie-Curie-Str.1, 72760 Reutlingen");
		hashMap.put("ORT_DAT_1","Reutlingen,den " + DatFunk.sHeute());
		

		
			
		
	}
	private void initHashMap(){
		hashMap = new HashMap<String,String>();	
		hashMap.put("VERS_VSNR1_1","");
		hashMap.put("VERS_N_VN_1","");
		hashMap.put("14_BKZ","");
		hashMap.put("MSNR","");
		hashMap.put("REHA_1","");
		hashMap.put("INST_KENNZ_1","");
		hashMap.put("ABT_1","");
		
		hashMap.put("STR_HA_PLZ_ORT1","");
		hashMap.put("AW_BETRIEB_1","");
		hashMap.put("AW_BETRIEB_2","");
		hashMap.put("VOM_1","");
		hashMap.put("VOM_2","");
		hashMap.put("BIS_1","");
		hashMap.put("BIS_2","");
		hashMap.put("ENTLA_1","");
		hashMap.put("AW_A","");
		hashMap.put("AW_A1","");
		hashMap.put("AW_A2","");
		hashMap.put("AW_A3","");
		hashMap.put("AW_A4","");
		hashMap.put("AW_A5","");
		hashMap.put("AW_A6","");
		hashMap.put("AW_A7","");
		hashMap.put("AW_A8","");
		hashMap.put("ANDERE_1","");
		hashMap.put("AW_B","");
		hashMap.put("AW_B1","");
		hashMap.put("AW_B2","");
		hashMap.put("AW_B3","");
		hashMap.put("AW_B4","");
		hashMap.put("AW_B5","");
		hashMap.put("AW_B6","");
		hashMap.put("AW_B7","");
		hashMap.put("AW_B8","");
		hashMap.put("AW_B9","");
		hashMap.put("ANDERE_2","");
		hashMap.put("AW_C","");
		hashMap.put("AW_C1","");
		hashMap.put("AW_C2","");
		hashMap.put("AW_C3","");
		hashMap.put("AW_C4","");
		hashMap.put("AW_C5","");
		hashMap.put("ANDERE_3","");
		hashMap.put("HAUF_1","");
		hashMap.put("DAU_1","");
		hashMap.put("AW_ART1","");
		hashMap.put("AW_ART2","");
		hashMap.put("AW_ART3","");
		hashMap.put("frühs._Beginn","");
		hashMap.put("ORT_1","");
		hashMap.put("STR_HA_PLZ_ORT2","");
		hashMap.put("ORT_DAT_1","");
		hashMap.put("INST_KENNZ_1","");
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
					"<f href='"+formularpfad+"\\Empfehlung für IRENA_NoRestriction.pdf'/>"+System.getProperty("line.separator")+
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
