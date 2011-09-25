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


public class RechnungIRENA {

	
	String xfdfFile = "";
	HashMap<String,String> hashMap = null;
	String formularpfad = null;
	String reader = null;
	public RechnungIRENA(String bid,String pfad){
		formularpfad = pfad;
		doSuche(bid);
	}
	
	private void doSuche(String bid){
		 
		initHashMap();
		Vector<Vector<String>> vec = SqlInfo.holeFelder("select vnummer,aigr,msnr,namevor,strasse,plz,ort,aufdat3,entdat3 from bericht2 where berichtid='"+bid+"'");
		if(vec == null){
			return;
		}
		auswertenVector(vec);
		doStart();
	}
	private void initHashMap(){
		hashMap = new HashMap<String,String>();	
		hashMap.put("VERS_VSNR1_1","");
		hashMap.put("14_BKZ","");
		hashMap.put("18_MSNR","");
		hashMap.put("VERS_N_VN_1","");
		hashMap.put("STR_HA_PLZ_ORT1","");
		hashMap.put("VOM_1","");
		hashMap.put("VOM_2","");
		hashMap.put("ANZA_1","24");
		hashMap.put("ANZA_2","24");
		hashMap.put("BETRAG_1","25,00");
		hashMap.put("BETRAG_2","600,00");
		hashMap.put("BETRAG_3","5,00");
		hashMap.put("BETRAG_4","120,00");
		//hashMap.put("BETRAG_5","");
		hashMap.put("NAME_GELD_1","Dresdner Bank Stuttgart");
		hashMap.put("BLZ_1","60080000");
		hashMap.put("KONTO_1","332325000");
		hashMap.put("KONTOINH_1","Reutlinger Therapie- &amp; Analysezentrum GmbH");
		hashMap.put("INST_KENNZ_1","510841109");
		hashMap.put("PAT_1","");
		hashMap.put("ORTDAT_1","Reutlingen, den "+DatFunk.sHeute());
	}
	
	private void auswertenVector(Vector<Vector<String>> ergebnis){
		hashMap.put("VERS_VSNR1_1",ergebnis.get(0).get(0));
		hashMap.put("14_BKZ",ergebnis.get(0).get(1));
		hashMap.put("MSNR",ergebnis.get(0).get(2));
		hashMap.put("VERS_N_VN_1",ergebnis.get(0).get(3));
		hashMap.put("STR_HA_PLZ_ORT1",ergebnis.get(0).get(4)+", "+ergebnis.get(0).get(5)+" "+ergebnis.get(0).get(6));
		if(ergebnis.get(0).get(7).trim().length()==10){
			hashMap.put("VOM_1",DatFunk.sDatInDeutsch(ergebnis.get(0).get(7)));			
		}
		if(ergebnis.get(0).get(8).trim().length()==10){
			hashMap.put("BIS_1",DatFunk.sDatInDeutsch(ergebnis.get(0).get(8)));			
		}
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
					"<f href='"+formularpfad+"\\Abrechnungsbogen für IRENA_NoRestriction.pdf'/>"+System.getProperty("line.separator")+
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
