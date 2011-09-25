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

import javax.swing.JButton;
import javax.swing.JTextField;

import Tools.DatFunk;
import Tools.SqlInfo;


public class LVAasp {

	
	JButton bnr1;
	JTextField[] tf1 = {null};
	String xfdfFile = "";
	HashMap<String,String> hashMap = null;
	String formularpfad = null;
	String reader = null;
	public LVAasp(String bid,String pfad) throws Exception{
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
		hashMap.put("Versicherungsnummer",ergebnis.get(0).get(2));
		hashMap.put("Name",ergebnis.get(0).get(3));
		if(ergebnis.get(0).get(4).trim().length()==10){
			hashMap.put("Geburtsdatum",DatFunk.sDatInDeutsch(ergebnis.get(0).get(4)).replace(".", ""));			
		}
		hashMap.put("Straﬂe",ergebnis.get(0).get(5));
		hashMap.put("PLZ",ergebnis.get(0).get(6));
		hashMap.put("Ort",ergebnis.get(0).get(7));
		hashMap.put("Kontrollk‰stchen40","ambulant");
		hashMap.put("Kontrollk‰stchen41",(ergebnis.get(0).get(18).equals("3") ? "Ja" : "nein" ));
		hashMap.put("Kontrollk‰stchen35","Ja");
		
		hashMap.put("Text7",DatFunk.sDatInDeutsch(ergebnis.get(0).get(15)).replace(".", ""));
		hashMap.put("Text8",DatFunk.sDatInDeutsch(ergebnis.get(0).get(16)).replace(".", ""));
		
		hashMap.put("Entlassdiagnosen1",ergebnis.get(0).get(19));
		hashMap.put("Entlassdiagnosen2",ergebnis.get(0).get(24));
		hashMap.put("Entlassdiagnosen3",ergebnis.get(0).get(29));
		hashMap.put("Entlassdiagnosen4",ergebnis.get(0).get(34));
		hashMap.put("Entlassdiagnosen5",ergebnis.get(0).get(39));
		hashMap.put("ICD10_1",ergebnis.get(0).get(20));
		hashMap.put("ICD10_2",ergebnis.get(0).get(25));
		hashMap.put("ICD10_3",ergebnis.get(0).get(30));
		hashMap.put("ICD10_4",ergebnis.get(0).get(35));
		hashMap.put("ICD10_5",ergebnis.get(0).get(40));
		hashMap.put("Text9","Reutlinger Therapie- und Analysezentrum GmbH\nMarie-Curie-Str.1\n72760 Reutlingen");
		
		hashMap.put("Zusatz1",ergebnis.get(0).get(21));
		hashMap.put("Zusatz2",ergebnis.get(0).get(26)); 
		hashMap.put("Zusatz3",ergebnis.get(0).get(31));
		hashMap.put("Zusatz4",ergebnis.get(0).get(36));
		hashMap.put("Zusatz5",ergebnis.get(0).get(41));
		hashMap.put("Sicherheit1",ergebnis.get(0).get(22));
		hashMap.put("Sicherheit2",ergebnis.get(0).get(27));
		hashMap.put("Sicherheit3",ergebnis.get(0).get(32));
		hashMap.put("Sicherheit4",ergebnis.get(0).get(37));
		hashMap.put("Sicherheit5",ergebnis.get(0).get(42));
		hashMap.put("Beh.Ergeb.1",ergebnis.get(0).get(23));
		hashMap.put("Beh.Ergeb.2",ergebnis.get(0).get(28));
		hashMap.put("Beh.Ergeb.3",ergebnis.get(0).get(33));
		hashMap.put("Beh.Ergeb.4",ergebnis.get(0).get(38));
		hashMap.put("Beh.Ergeb.5",ergebnis.get(0).get(43));
		

		hashMap.put("Frage8_1","Reutlinger Therapie- und Analysezentrum GmbH");
		hashMap.put("Frage8_2","Marie-Curie-Str.1, 72760 Reutlingen");
		hashMap.put("Ort, Datum","Reutlingen,den " + DatFunk.sHeute());
			
			
		
	}
	private void initHashMap(){
		hashMap = new HashMap<String,String>();	
		hashMap.put("Versicherungsnummer","");
		hashMap.put("Name","");
		hashMap.put("Geburtsdatum","");
		hashMap.put("Straﬂe","");
		hashMap.put("PLZ","");
		hashMap.put("Ort","");
		hashMap.put("Kontrollk‰stchen35","");
		hashMap.put("Kontrollk‰stchen36","");
		hashMap.put("Kontrollk‰stchen37","");
		hashMap.put("Kontrollk‰stchen38","");
		hashMap.put("Kontrollk‰stchen39","");
		hashMap.put("Kontrollk‰stchen40","");
		hashMap.put("Kontrollk‰stchen41","");
		hashMap.put("Text7","");
		hashMap.put("Text8","");
		hashMap.put("Text49","");
		hashMap.put("Text9","");
		hashMap.put("Entlassdiagnosen1","");
		hashMap.put("Entlassdiagnosen2","");
		hashMap.put("Entlassdiagnosen3","");
		hashMap.put("Entlassdiagnosen4","");
		hashMap.put("Entlassdiagnosen5","");
		hashMap.put("ICD10_1","");
		hashMap.put("ICD10_2","");
		hashMap.put("ICD10_3","");
		hashMap.put("ICD10_4","");
		hashMap.put("ICD10_5","");
		hashMap.put("Zusatz1","");
		hashMap.put("Zusatz2","");
		hashMap.put("Zusatz3","");
		hashMap.put("Zusatz4","");
		hashMap.put("Zusatz5","");
		hashMap.put("Sicherheit1","");
		hashMap.put("Sicherheit2","");
		hashMap.put("Sicherheit3","");
		hashMap.put("Sicherheit4","");
		hashMap.put("Sicherheit5","");
		hashMap.put("Beh.Ergeb.1","");
		hashMap.put("Beh.Ergeb.2","");
		hashMap.put("Beh.Ergeb.3","");
		hashMap.put("Beh.Ergeb.4","");
		hashMap.put("Beh.Ergeb.5","");
		hashMap.put("Frage5","");
		hashMap.put("Frage6","");
		hashMap.put("Frage7","");
		hashMap.put("Frage7","");
		hashMap.put("Frage8_1","");
		hashMap.put("Frage8_2","");
		hashMap.put("Ort, Datum","");

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
					"<f href='"+formularpfad+"\\Verordnung-ASP_NoRestriction.pdf'/>"+System.getProperty("line.separator")+
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
