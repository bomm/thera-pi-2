package pdftest2;



import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;



import Tools.DatFunk;
import Tools.SqlInfo;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;


public class WiedereingliederungLVA {

	
	JButton bnr1;
	JTextField[] tf1 = {null};
	String xfdfFile = "";
	HashMap<String,String> hashMap = null;	
	public WiedereingliederungLVA(String bid){
		doSuche(bid);
	}

	
	
	private void doSuche(String bid){
		//String bid = tf1[0].getText().trim(); 
		initHashMap();
		System.out.println("select * from bericht2 where berichtid='"+bid+"'");
		Vector<Vector<String>> vec = SqlInfo.holeFelder("select * from bericht2 where berichtid='"+bid+"'");
		//System.out.println(vec);
		//System.out.println(vec.get(0).get(2));
		if(vec == null){

			return;
		}
		auswertenVector(vec);
		doStart();
	}
	private void auswertenVector(Vector<Vector<String>> ergebnis){
		hashMap.put("Versicherungsnummer",ergebnis.get(0).get(2));
		hashMap.put("Entlassdatum",ergebnis.get(0).get(16));
		hashMap.put("Name",ergebnis.get(0).get(3));
		hashMap.put("Geburtsdatum",ergebnis.get(0).get(4));
		hashMap.put("Anschrift",ergebnis.get(0).get(5)+ergebnis.get(0).get(6)+ergebnis.get(0).get(7));
		//hashMap.put("letzteTätigkeit",ergebnis.get(0).get(0));
		//hashMap.put("tägl.Arbeitszeit",ergebnis.get(0).get(3));
		//hashMap.put("vom1",ergebnis.get(0).get(3));
		//hashMap.put("vom2",ergebnis.get(0).get(3));
		//hashMap.put("vom3",ergebnis.get(0).get(3));
		//hashMap.put("vom4",ergebnis.get(0).get(3));
		//hashMap.put("bis1",ergebnis.get(0).get(3));
		//hashMap.put("bis2",ergebnis.get(0).get(3));
		//hashMap.put("bis3",ergebnis.get(0).get(3));
		//hashMap.put("bis4",ergebnis.get(0).get(3));
		//hashMap.put("Std.1",ergebnis.get(0).get(3));
		//hashMap.put("Std.2",ergebnis.get(0).get(3));
		//hashMap.put("Std.3",ergebnis.get(0).get(3));
		//hashMap.put("Std.4",ergebnis.get(0).get(3));
		//hashMap.put("Tätigkeit1",ergebnis.get(0).get(3));
		//hashMap.put("Tätigkeit2",ergebnis.get(0).get(3));
		//hashMap.put("Tätigkeit3",ergebnis.get(0).get(3));
		//hashMap.put("Tätigkeit4",ergebnis.get(0).get(3));
		//hashMap.put("Bemerkung",ergebnis.get(0).get(3));
		//hashMap.put("Datum1",ergebnis.get(0).get(3));
		//hashMap.put("Datum2",ergebnis.get(0).get(3));
		//hashMap.put("Kontrollkästchen31",ergebnis.get(0).get(3));
		//hashMap.put("Kontrollkästchen33",ergebnis.get(0).get(3));
		//hashMap.put("Grund",ergebnis.get(0).get(3));
		//hashMap.put("bis5",ergebnis.get(0).get(3));
		//hashMap.put("Datum3",ergebnis.get(0).get(3));
		

		
			
		
	}
	private void initHashMap(){
		hashMap.put("Versicherungsnummer","");
		hashMap.put("Entlassdatum","");
		hashMap.put("Name","");
		hashMap.put("Geburtsdatum","");
		hashMap.put("Anschrift","");
		hashMap.put("letzteTätigkeit","");
		hashMap.put("tägl.Arbeitszeit","");
		hashMap.put("vom1","");
		hashMap.put("vom2","");
		hashMap.put("vom3","");
		hashMap.put("vom4","");
		hashMap.put("bis1","");
		hashMap.put("bis2","");
		hashMap.put("bis3","");
		hashMap.put("bis4","");
		hashMap.put("Std.1","");
		hashMap.put("Std.2","");
		hashMap.put("Std.3","");
		hashMap.put("Std.4","");
		hashMap.put("Tätigkeit1","");
		hashMap.put("Tätigkeit2","");
		hashMap.put("Tätigkeit3","");
		hashMap.put("Tätigkeit4","");
		hashMap.put("Bemerkung","");
		hashMap.put("Datum1","");
		hashMap.put("Datum2","");
		hashMap.put("Kontrollkästchen31","");
		hashMap.put("Kontrollkästchen33","");
		hashMap.put("Grund","");
		hashMap.put("bis5","");
		hashMap.put("Datum3","");
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
					"<f href='C:\\Daten\\formulare\\Stufenweise Wiedereingliederung-LVA_NoRestriction.pdf'/>"+System.getProperty("line.separator")+
					"</xfdf>"
					);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}
	private void doStart(){
		/*
		initHashMap();
		hashMap.put("Versicherungsnummer","12123456S012");
		hashMap.put("VERS_N_VN_1","Steinhilber, Jürgen");
		hashMap.put("14_BKZ","02051962");
		hashMap.put("MSNR","Theodor-Fontane-Str.4");
		hashMap.put("REHA_1","72760");		
		hashMap.put("INST_KENNZ_1","Reutlingen");
		hashMap.put("ABT_1","stationär");
		hashMap.put("VERS_N_VN_1","Ja");
		hashMap.put("INST_KENNZ_1","Reutlingen");
		*/
		Set entries = hashMap.entrySet();
	    Iterator it = entries.iterator();
	    FileWriter fw = null;
		try {
			xfdfFile = "C:/test.xfdf";
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


