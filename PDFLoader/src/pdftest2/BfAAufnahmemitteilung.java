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


public class BfAAufnahmemitteilung {

	
	JButton bnr1;
	JTextField[] tf1 = {null};
	String xfdfFile = "";
	HashMap<String,String> hashMap = null;	
	public BfAAufnahmemitteilung(String bid){
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
		hashMap.put("VERS_VSNR1_1",ergebnis.get(0).get(2));
		//hashMap.put("ANSCHRIFT_KRA_KA",ergebnis.get(0).get(10));
		hashMap.put("PAT_GEBDAT",ergebnis.get(0).get(4));
		hashMap.put("NAME_AHB_EINRICHTUNG","Reutlinger Therapie- und Analysezentrum\nMarie-Curie-Str.1\n72760 Reutlingen");
		hashMap.put("NAME_VORNAME_PAT",ergebnis.get(0).get(3));
		hashMap.put("ANSCHIRFT_PAT",ergebnis.get(0).get(5)+ ergebnis.get(0).get(6)+ ergebnis.get(0).get(7));
		hashMap.put("AUFNAHME_DAT",ergebnis.get(0).get(15));
		hashMap.put("ORT_DATUM","Reutlingen,den " + DatFunk.sHeute());
		//hashMap.put("NAME_VORNAME_MITGLIED",ergebnis.get(0).get(3));
		//hashMap.put("MITGLIED_GEBDAT",ergebnis.get(0).get(3));
		
		

		
			
		
	}
	private void initHashMap(){
		hashMap = new HashMap<String,String>();	
		hashMap.put("VERS_VSNR1_1","");
		hashMap.put("14_BKZ","");
		hashMap.put("18_MSNR","");
		hashMap.put("Anschrift","");
		hashMap.put("VERS_N_VN_1","");
		hashMap.put("ORTDAT_1","");
		hashMap.put("AW_ZUSTIMMUNG","");
		hashMap.put("AW_KEINE_WEITERLEITUNG","");
		hashMap.put("UNT_1","");
		hashMap.put("DATUM_1","");
		hashMap.put("DATUM_2","");
		hashMap.put("DATUM_3","");
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
					"<f href='C:\\Daten\\formulare\\BfA-Aufnahmemitteilung_NoRestriction.pdf'/>"+System.getProperty("line.separator")+
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
