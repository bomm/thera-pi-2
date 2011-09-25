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
import Tools.StringTools;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;


public class REHAbescheinigung {

	
	String xfdfFile = "";
	HashMap<String,String> hashMap = null;
	String formularpfad = null;
	String patid = null;
	String reader = null;
	public REHAbescheinigung(String bid,String pfad,String xpatid){
		formularpfad = pfad;
		patid = xpatid;
		doSuche(bid);
	}
	
	
	private void doSuche(String bid){
		initHashMap();
		System.out.println();
		Vector<Vector<String>> vec = SqlInfo.holeFelder("select v_name,n_name,strasse,plz,ort,geboren from pat5 where pat_intern='"+patid+"'");
		Vector<Vector<String>> vec2 = SqlInfo.holeFelder("select aufdat3 from bericht2 where berichtid='"+bid+"'");
		if(vec == null){
			return;
		}
		auswertenVector(vec,vec2);
		doStart();
	}
	private void auswertenVector(Vector<Vector<String>> ergebnis,Vector<Vector<String>> ergebnis2){
		hashMap.put("Vorname Name",StringTools.EGross(ergebnis.get(0).get(0))+" "+StringTools.EGross(ergebnis.get(0).get(1)));
		hashMap.put("Strasse",StringTools.EGross(ergebnis.get(0).get(2)));
		hashMap.put("Plz Ort",ergebnis.get(0).get(3)+" "+ StringTools.EGross(ergebnis.get(0).get(4)));
		hashMap.put("heute",DatFunk.sHeute());
		hashMap.put("Derdie Patientin",StringTools.EGross(ergebnis.get(0).get(0))+" "+StringTools.EGross(ergebnis.get(0).get(1)));
		if(ergebnis.get(0).get(5).trim().length()==10){
			hashMap.put("geb am",DatFunk.sDatInDeutsch(ergebnis.get(0).get(5)));			
		}
		if(ergebnis2.size()>0){
			if(ergebnis2.get(0).get(0).trim().length()==10){
				hashMap.put("seitab dem",DatFunk.sDatInDeutsch(ergebnis2.get(0).get(0)));			
			}
		}
	}
	private void initHashMap(){
		hashMap = new HashMap<String,String>();
		hashMap.put("Vorname Name","");
		hashMap.put("Strasse","");
		hashMap.put("Plz Ort","");
		hashMap.put("heute","");
		hashMap.put("Derdie Patientin","");
		hashMap.put("geb am","");
		hashMap.put("seitab dem","");
		hashMap.put("für den","");
		hashMap.put("Die Maßnahme für og Patienten wurde aus medizinischen Gründen um","");
		hashMap.put("verlängert Der Abschluß der Maßnahme ist nunmehr für den","");
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
					"<f href='"+formularpfad+"\\Rehabescheinigung.pdf'/>"+System.getProperty("line.separator")+
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
