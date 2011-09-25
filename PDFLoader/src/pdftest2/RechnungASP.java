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


public class RechnungASP {
	
	String xfdfFile = "";
	HashMap<String,String> hashMap = null;
	String formularpfad = null;
	String reader = null;
	public RechnungASP(String bid,String pfad){
		formularpfad = pfad;
		doSuche(bid);
	}
	private void initHashMap(){
		hashMap = new HashMap<String,String>();	
		hashMap.put("Versicherungsnummer","");
		hashMap.put("Name","");
		hashMap.put("Geburtsdatum","");
		hashMap.put("Straﬂe","");
		hashMap.put("PLZ","");
		hashMap.put("Wohnort","");
		hashMap.put("von","");
		hashMap.put("bis","");
		hashMap.put("Betrag1","600,00");
		hashMap.put("Betrag2","0,00");
		hashMap.put("Betrag3","120,00");
		
		hashMap.put("Kontoinhaber","Reutlinger Therapie- &amp; Analysezentrum GmbH");
		hashMap.put("Bankinstitut","Dresdner Bank Stuttgart, eine Marke der Commerzbank");
		hashMap.put("Bankleizahl","60080000");
		hashMap.put("Kontonummer","332325000");
		hashMap.put("IK-Nummer","510841109");
		hashMap.put("Ort, Datum","Reutlingen, den "+DatFunk.sHeute());
		
	}
	private void auswertenVector(Vector<Vector<String>> ergebnis){
		hashMap.put("Versicherungsnummer",ergebnis.get(0).get(0));
		hashMap.put("Name",ergebnis.get(0).get(1));
		if(ergebnis.get(0).get(2).trim().length()==10){
			hashMap.put("Geburtsdatum",DatFunk.sDatInDeutsch(ergebnis.get(0).get(2)).replace(".", ""));
		}
		hashMap.put("Straﬂe",ergebnis.get(0).get(3));
		hashMap.put("PLZ",ergebnis.get(0).get(4));
		hashMap.put("Wohnort",ergebnis.get(0).get(5));
		if(ergebnis.get(0).get(6).trim().length()==10){
			hashMap.put("von",DatFunk.sDatInDeutsch(ergebnis.get(0).get(6)).replace(".", ""));
		}
		if(ergebnis.get(0).get(7).trim().length()==10){
			hashMap.put("bis",DatFunk.sDatInDeutsch(ergebnis.get(0).get(7)).replace(".", ""));
		}
	}
	
	private void doSuche(String bid){
 
		initHashMap();
		Vector<Vector<String>> vec = SqlInfo.holeFelder("select vnummer,namevor,geboren,strasse,plz,ort,aufdat3,entdat3 from bericht2 where berichtid='"+bid+"'");
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
					"<f href='"+formularpfad+"\\ASP-Abrechnungsformular_NoRestriction.pdf'/>"+System.getProperty("line.separator")+
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
