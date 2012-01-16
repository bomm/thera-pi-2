package ocf;

import hauptFenster.Reha;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import javax.swing.JOptionPane;

import org.jdesktop.swingworker.SwingWorker;

import opencard.core.OpenCardException;
import opencard.core.event.CTListener;
import opencard.core.event.CardTerminalEvent;
import opencard.core.event.EventGenerator;
import opencard.core.service.CardRequest;
import opencard.core.service.CardServiceException;
import opencard.core.service.SmartCard;
import opencard.core.terminal.CardID;
import opencard.core.terminal.CardTerminal;
import opencard.core.terminal.CardTerminalException;
import opencard.core.terminal.CardTerminalRegistry;
import opencard.core.terminal.CommandAPDU;
import opencard.core.terminal.ResponseAPDU;
import opencard.opt.util.PassThruCardService;
import systemEinstellungen.SystemConfig;
import systemTools.StringTools;


public class OcKVK {
	// CLA || INS || P1 || P2 || Le (= erwartete Länge der Daten)
	final byte[] CMD_READ_BINARY ={(byte)0x00, (byte)0xB0,(byte)0x00, (byte)0x00, (byte)0x00 };
	final byte[] CMD_READ_BINARY_EF ={(byte)0x00, (byte)0xB0,
	        (byte)0x81, (byte)0x00, (byte)0x02 };
	final byte[] CMD_SELECT_KVKFILE = {(byte)0x00, (byte)0xA4, (byte)0x04, (byte)0x00, (byte)0x06,
			(byte)0xD2, (byte)0x76, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x01};
	final byte[] CMD_SELECT_EGK_HDC = {(byte)0x00, (byte)0xA4, (byte)0x04, (byte)0x0C, (byte)0x06,
			(byte)0xD2, (byte)0x76, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x02};
	final byte[] CMD_SELCT_VD ={(byte)0x00, (byte)0xA4,
	        (byte)0x02, (byte)0x04, (byte)0x02, (byte)0xD0, (byte)0x02,(byte)0x00 };
	final byte[] CMD_SELCT_PD ={(byte)0x00, (byte)0xA4,
	        (byte)0x02, (byte)0x04, (byte)0x02, (byte)0xD0, (byte)0x01,(byte)0x00 };
	
	ByteArrayInputStream in = null; 
	ByteArrayOutputStream out = null;
	
	
	private static final int MAX_APDU_SIZE = 0x300;  //  bytes
	//Wird später ersetzt durch kvkTags
	final static String[] hmProperty = {"Rohdaten","Krankenkasse","Kassennummer","Kartennummer","Versichertennummer",
		"Status","Statusext","Titel","Vorname","Namenszusatz",
		"Nachname","Geboren","Strasse","Land","Plz",
		"Ort","Gueltigkeit","Checksumme","Anrede"};
	//Wird später ersetzt durch kvkTags	
	final int[] tags = { 0x60,0x80,0x81,0x8F,0x82,0x83,0x90,0x84,0x85,
			0x86,0x87,0x88,0x89,0x8A,0x8B,0x8C,0x8D,0x8E};
	
	//Integer Tag-Identifier
	//String (Thera-Pi interner)
	//Tag-Name
	//boolean Optional
	//Integer Länge
	//String Inhalt
	static Object[][] kvkTags = {
		{0x60,"Rohdaten",false,0,""},
		{0x80,"Krankenkasse",false,0,""},
		{0x81,"Kassennummer",false,0,""},
		{0x8F,"Kartennummer",false,0,""},
		{0x82,"Versichertennummer",false,0,""},
		{0x83,"Status",false,0,""},
		{0x90,"Statusext",false,0,""},
		{0x84,"Titel",true,0,""},
		{0x85,"Vorname",false,0,""},
		{0x86,"Namenszusatz",true,0,""},
		{0x87,"Nachname",false,0,""},
		{0x88,"Geboren",false,0,""},
		{0x89,"Strasse",false,0,""},
		{0x8A,"Land",true,0,""},
		{0x8B,"Plz",false,0,""},
		{0x8C,"Ort",false,0,""},
		{0x8D,"Gueltigkeit",false,0,""},
		{0x8E,"Checksumme",false,0,""}
		};
	
	public boolean terminalOk = false;
	public boolean cardOk = false;

	public Vector<Vector<String>> vecreader = new Vector<Vector<String>>();
	public String aktReaderName;
	public String aktDllName;
	public String aktDeviceId;
	
	private PassThruCardService ptcs;
	public SmartCard sc;
	private CommandAPDU command;
	public CardListener clistener;
	private CardRequest cr;
	private byte[] i;
	private int n, x;
	private String s,satrId;
	
	public boolean isCardReady;
	public boolean systemStarted = false;

	ResponseAPDU response;
	boolean blockIKKasse = false;
	String ikktraeger = "";
	String namektraeger = "";
	
	BufferedReader br = null;
	String resultString = null;
	StringBuffer neustring = new StringBuffer();
	
	public OcKVK(String readerName,String dllName,String deviceid,boolean test) throws Exception,UnsatisfiedLinkError {
		//SCR335
		//ctpcsc31kv
		// nur in der Testphase der testphase
		SystemConfig.hmKVKDaten = new HashMap<String,String>();
		// danach wird das Gedönse in der SystemConfig initialisiert.
		
		this.aktReaderName = readerName;
		this.aktDllName = dllName;
		this.aktDeviceId = deviceid;
		initProperties();
		initTerminal(test);
		systemStarted = true;
		terminalOk = true;
		Properties sysProps = System.getProperties();
		sysProps.put ("OpenCard.services", "de.cardcontact.opencard.factory.IsoCardServiceFactory");
	}

	public int lesen() throws CardTerminalException, CardServiceException, ClassNotFoundException{
		int ret = 0;
			blockIKKasse = false;
			sc = SmartCard.waitForCard(cr);
			ptcs = (PassThruCardService) sc.getCardService(PassThruCardService.class, true);
			/*****Karte testen*****/
			CardID cardID = sc.getCardID ();
		    i = cardID.getATR();
		    s = "";
		    satrId = ""; 
		    for (n = 0; n < i.length; n++) {
		       x = (int) (0x000000FF & i[n]);
		       s = Integer.toHexString(x).toUpperCase();
		       if (s.length() == 1) s = "0" + s;
		       satrId = satrId+String.valueOf(s);
		    }
		    if(satrId.trim().equals("AAFFFFFF")){
		    	System.out.println("keine KV-Karte oder Karte defekt");
		    	//Karte ist keine KV-Karte, sondern eine x-beliebige Speicherkarte
		    	sc.close();
		    	return -1;
		    }
			command = new CommandAPDU(MAX_APDU_SIZE); 
			command.append(CMD_SELECT_KVKFILE);
			response = ptcs.sendCommandAPDU(command);
		    if(response == null || response.getBytes().length==0){
		    	System.out.println("keine KV-Karte oder Karte defekt");
		    	sc.close();
		    	return -1;
		    }

		    if(getResponseValue(response.getBytes()).equals("9000")){
		    	//es ist eine KVK
			    command.setLength(0);
				command.append(CMD_READ_BINARY);
			    response = ptcs.sendCommandAPDU(command);

			    if(response.getByte(0)== (byte)0x60){ //Nach ASN.1 Standard der KVK
			    	checkKVK_ASN1(response.getBytes(),kvkTags);
				    sc.close();
			    }else{
			    	//Hier entweder neue Routine, falls betriebsintern
			    	//noch anderweitige Chipkarten eingesetzt werden z.B. Zugangskontrolle etc.
			    	//oder Fehlermeldung daß Karte keine KV-Karte ist.
			    	ret = -1;
			    	sc.close();
			    }
		    }else{
		    	ptcs.getCard().reset(true);
		    	//Hier testen ob es eine eGK ist
		    	command.setLength(0);
				command.append(CMD_SELECT_EGK_HDC);
			    response = ptcs.sendCommandAPDU(command);
			    if(getResponseValue(response.getBytes()).equals("9000")){
			    	// ja es ist eine eGK;
			    	command.setLength(0);
					command.append(this.CMD_READ_BINARY_EF);
				    response = ptcs.sendCommandAPDU(command);
			    	//System.out.println("Response = "+getResponseValue(response.getBytes()));
			        /***********PD-Daten********************/
			        byte[] resultpd = new byte[850];
			        byte[] offset = {(byte)0x00,(byte)0x01,(byte)0x02,(byte)0x03,(byte)0x04};
			        byte[] cmd = {(byte)0x00, (byte)0xB0, (byte)0x00, (byte)0x00, (byte)0x00};
			        int bytes;
			        int zaehler = 0;
			        try{
				        for(int of = 0; of < 4;of++){
				            command.setLength(0);
				            cmd[2] = offset[of];
				            command.append(cmd);
				            response = ptcs.sendCommandAPDU(command);
				            /*********************/         
				            bytes = response.getLength();
				            for (n = 0; n < bytes; n++) {
					              if( (n < (bytes-2))){
						           	   try{
						           		   if( ( (of==0 && n > 1) || (of > 0)) /*&& (zaehler < lang)*/ ){
						               		   resultpd[zaehler]=(byte)response.getByte(n);
						               		   zaehler++;
						           		   }
						           	   }catch(Exception ex){
						           		   System.out.println("Fehler bei Zähler: "+zaehler);
						           		   ex.printStackTrace();
						           	   }
					              }
				            }
				        }
			        }catch(Exception ex){
			        	ex.printStackTrace();
			        }
			        try{
			        	
			        	
			        	in = new ByteArrayInputStream(resultpd); 
			        	out = Unzip("",in);
			        	in.close();
			        	out.flush();
			        	out.close();
			        	resultString = new String(out.toByteArray());
				       	/**********HashMap leeren**********/ 
				       	SystemConfig.hmKVKDaten.clear();
					    if(resultString.indexOf("\n") >= 0){					       	 

					    	readAndParseXML(new ByteArrayInputStream(resultString.getBytes()),0);
			        	}else{
			        		if( createLineBrake(resultString) ){
			        			//System.out.println(neustring.toString());
			        			readAndParseXML(new ByteArrayInputStream(neustring.toString().getBytes()),0);
			        		}
			        	}
			        }catch(Exception ex){
			       	 	ex.printStackTrace();
			       	 	SystemConfig.hmKVKDaten.clear();
			       	 	sc.close();
			       	 	return -1;
			        }
			        /*******************************VD-Daten**************************
			         * 
			         * 
			         *****/
					ikktraeger = "";
					namektraeger = "";

			        command.setLength(0);
			        command.append(this.CMD_SELCT_VD);
			        response = ptcs.sendCommandAPDU(command);
			        byte[] resultvd = new byte[1250];
			        zaehler = 0;

			        /**********************************************************/     
			        for(int of = 0; of < 5;of++){
			            command.setLength(0);
			            cmd[2] = offset[of];
			            command.append(cmd);
			            response = ptcs.sendCommandAPDU(command);
			            /*********************/         
			            bytes = response.getLength();
			            for (n = 0; n < bytes; n++) {
				              if( (n < (bytes-2))){
					           	   try{
					           		   if( ( (of==0 && n > 7) || (of > 0)) ){
					               		   resultvd[zaehler]=(byte)response.getByte(n);
					               		   zaehler++;
					           		   }

					           	   }catch(Exception ex){
					           		   System.out.println("Fehler bei Zähler: "+zaehler);
					           		   ex.printStackTrace();
					           	   }
				              }
			            }
			        }
			        try{
			        	in = new ByteArrayInputStream(resultvd); 
			        	out = Unzip("",in);
			        	in.close();
			        	out.flush();
			        	out.close();
					    resultString = new String(out.toByteArray());
			        	if(resultString.indexOf("\n") >= 0){
			        		readAndParseXML(new ByteArrayInputStream(resultString.getBytes()),1);
			        	}else{
			        		if( createLineBrake(resultString) ){
			        			//System.out.println(neustring.toString());
			        			readAndParseXML(new ByteArrayInputStream(neustring.toString().getBytes()),1);
			        		}
			        	}
			        }catch(Exception ex){
			         	ex.printStackTrace();
			         	SystemConfig.hmKVKDaten.clear();
			         	sc.close();
				    	return -1;
			        }
			        sc.close(); 
			    }else{
			    	//es ist auch keine eGK;
			    	ret = -1;
			    	sc.close();
			    	return -1;
			    }
		    	
		    }
		    sc.close();
		    return ret;    
		}
	private boolean createLineBrake(String string){

		int aktindex = 0;
		int indexauf = 0;
		int indexzu = 0;
		
		neustring.setLength(0);
		neustring.trimToSize();
		try{
		while(aktindex < string.length()){
			indexauf = string.indexOf("<",aktindex);
			indexzu = string.indexOf(">",aktindex);
			/***letztes Element*****/
			//System.out.println("Start:"+indexauf+" / Ende:"+indexzu);
			if(indexzu+1 >= string.length()){
				neustring.append(string.substring(indexauf,indexzu+1));
				break;
			}
			/*****Elementgruppe = einzelnes Element Anfang oder Ende*****/
			if(string.substring(indexzu+1, indexzu+2).equals("<")){
				neustring.append(string.substring(indexauf,indexzu+1)+System.getProperty("line.separator"));
				aktindex=indexzu+1;
				continue;
			}else{
				/*******Datenelement******/
				indexzu = string.indexOf(">",indexzu+1);
				if(indexzu > 0){
					neustring.append(string.substring(indexauf,indexzu+1)+System.getProperty("line.separator"));
					aktindex=indexzu+1;
				}else{
					System.out.println("Fehler bei indexzu="+indexzu);
					aktindex +=1;
				}
			}
		}
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
		//System.out.println("Bei Austritt -- Start:"+indexauf+" / Ende:"+indexzu);
		return true;
	}
	
	private String getResponseValue(byte[] by){
		String wert = "";
		int bytes = by.length;
		int x;
        for (n = 0; n < bytes; n++) {
        	x = (int) (0x000000FF & by[n]);
   			s = Integer.toHexString(x).toUpperCase();
        	if (s.length() == 1) s = "0" + s;
            if( (n >= (bytes-2))){
         	   	try{
         	   		wert = wert+s;
         	   	}catch(Exception ex){
         	   		ex.printStackTrace();
         	   	}
            }
        }		
		return wert;
	}
	private void readAndParseXML(ByteArrayInputStream in,int datenart){
		InputStreamReader inread = new InputStreamReader(in);
		try {
		BufferedReader br = new BufferedReader(inread);
		String s;
			int zaehler = 0;
			while((s = br.readLine()) != null) {
				if(datenart==0){
					//PD
					testePD(s,s.indexOf("<"),s.indexOf(">"),zaehler);
					zaehler++;
				}else if(datenart == 1){
					//VD
					testeVD(s,s.indexOf("<"),s.indexOf(">"),zaehler);
					zaehler++;
				}

			}
			inread.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		if(SystemConfig.hmKVKDaten.get("Kassennummer") == null || SystemConfig.hmKVKDaten.get("Kassennummer").equals("")){ 
			SystemConfig.hmKVKDaten.put("Kassennummer",this.ikktraeger);
			SystemConfig.hmKVKDaten.put("Krankenkasse",this.namektraeger);
		}
	}
	/***********
	 * 
	 * @param zeile
	 * @param first
	 * @param last
	 * @param durchlauf
	 */
	private void testePD(String zeile,int first,int last,int durchlauf){
		String dummy = zeile.substring(first+1,last).trim();
		if(dummy.startsWith("/")) return;
		int dataend = zeile.indexOf("</");
		if(dataend==0 || dataend < last) return;
		
		/*
		System.out.print(dummy+" = ");
		System.out.println("Start = "+last+" / Ende = "+dataend);
		System.out.println(zeile.substring(last+1,dataend));
		*/
		
		/*
	Versicherten_ID = X110121694
	Geburtsdatum = 19470413
	Vorname = Ullrich
	Nachname = D�mmer-Meningham
	Geschlecht = M
	Vorsatzwort = 
	Namenszusatz = 
	Titel
	Postleitzahl = 20355
	Ort = Hamburg
	Postfach = 
	Wohnsitzlaendercode = D
	Strasse = Steinwegpassage
	Hausnummer = 2	
	Anschriftenzusatz = 
	final static String[] hmProperty = {"Rohdaten","Krankenkasse","Kassennummer","Kartennummer","Versichertennummer",
		"Status","Statusext","Titel","Vorname","Namenszusatz",
		"Nachname","Geboren","Strasse","Land","Plz",
		"Ort","Gueltigkeit","Checksumme","Anrede"};
	
		 */
		if(dummy.equals("Versicherten_ID")){
			SystemConfig.hmKVKDaten.put("Versichertennummer",zeile.substring(last+1,dataend));
			return;
		}else if(dummy.equals("Geburtsdatum")){
			String geboren = zeile.substring(last+1,dataend);
			SystemConfig.hmKVKDaten.put("Geboren",geboren.substring(6)+
					geboren.substring(4,6)+
					geboren.substring(0,4));
			return;
		}else if(dummy.equals("Vorname")){
			SystemConfig.hmKVKDaten.put("Vorname",zeile.substring(last+1,dataend));
			return;
		}else if(dummy.equals("Nachname")){
			SystemConfig.hmKVKDaten.put("Nachname",zeile.substring(last+1,dataend));
			return;
		}else if(dummy.equals("Titel")){
			SystemConfig.hmKVKDaten.put("Titel",zeile.substring(last+1,dataend));
			return;
		}else if(dummy.equals("Namenszusatz")){
			SystemConfig.hmKVKDaten.put("Namenszusatz",zeile.substring(last+1,dataend));
			return;
		}else if(dummy.equals("Postleitzahl")){
			SystemConfig.hmKVKDaten.put("Plz",zeile.substring(last+1,dataend));
			return;
		}else if(dummy.equals("Ort")){
			SystemConfig.hmKVKDaten.put("Ort",zeile.substring(last+1,dataend));
			return;
		}else if(dummy.equals("Geschlecht")){
			SystemConfig.hmKVKDaten.put("Anrede",(zeile.substring(last+1,dataend).equals("M") ? "HERR" : "FRAU"));
			return;
		}else if(dummy.equals("Strasse")){
			SystemConfig.hmKVKDaten.put("Strasse",zeile.substring(last+1,dataend));
			return;
		}else if(dummy.equals("Hausnummer")){
			SystemConfig.hmKVKDaten.put("Strasse", SystemConfig.hmKVKDaten.get("Strasse")+" "+zeile.substring(last+1,dataend));
			return;
		}
	}
	/**********
	 * 
	 * @param zeile
	 * @param first
	 * @param last
	 * @param durchlauf
	 */
	private void testeVD(String zeile,int first,int last,int durchlauf){
		String dummy = zeile.substring(first+1,last).trim();
		if(dummy.startsWith("/")) return;
		int dataend = zeile.indexOf("</");
		if(dataend==0 || dataend < last) return;
		/*
		System.out.print(dummy+" = ");
		System.out.println("Start = "+last+" / Ende = "+dataend);
		System.out.println(zeile.substring(last+1,dataend));
		*/
		
		
		/*
	Beginn = 20100301
	Ende = 20131231
	Kostentraegerkennung = 999567890
	Kostentraegerlaendercode = D
	Name = gematik Musterkasse1GKV
	Kostentraegerkennung = 991534564
	Name = gematik Musterkasse1GKV
	Rechtskreis = 1
	Versichertenart = 5
	Versichertenstatus_RSA = 0
	Kostenerstattung_ambulant = 0
	Kostenerstattung_stationaer = 0
	WOP = 02
	Status = 0
	Shutdown 
	final static String[] hmProperty = {"Rohdaten","Krankenkasse","Kassennummer","Kartennummer","Versichertennummer",
		"Status","Statusext","Titel","Vorname","Namenszusatz",
		"Nachname","Geboren","Strasse","Land","Plz",
		"Ort","Gueltigkeit","Checksumme","Anrede"};
		
		 */
		if(dummy.equals("Kostentraegerkennung") && (!this.blockIKKasse) ){
			String ik = zeile.substring(last+1,dataend);
			ikktraeger = ik.substring(2);
			return;
		}else if(dummy.equals("Name") && (!this.blockIKKasse) ){
			namektraeger = zeile.substring(last+1,dataend);
			this.blockIKKasse = true;
			return;
		}else if(dummy.equals("Kostentraegerkennung") && (this.blockIKKasse) ){
			String ik = zeile.substring(last+1,dataend);
			SystemConfig.hmKVKDaten.put("Kassennummer",ik.substring(2));
			return;
		}else if(dummy.equals("Name") && (this.blockIKKasse) ){
			SystemConfig.hmKVKDaten.put("Krankenkasse",zeile.substring(last+1,dataend));
			return;
		}else if(dummy.equals("Versichertenart") ){
			//SystemConfig.hmKVKDaten.put("Statusext",zeile.substring(last+1,dataend));
			SystemConfig.hmKVKDaten.put("Status",zeile.substring(last+1,dataend));
			return;
		}else if(dummy.equals("Ende") ){
			String ende = zeile.substring(last+1,dataend);
			SystemConfig.hmKVKDaten.put("Gueltigkeit",ende.substring(4,6)+
					ende.substring(2,4));
			return;
		}
		
	}
	/*******
	 * 
	 * @param inFilePath
	 * @param in
	 * @return
	 * @throws Exception
	 */
	private ByteArrayOutputStream Unzip(String inFilePath, ByteArrayInputStream in) throws Exception
	{
		ByteArrayOutputStream out = null;
		try{
			GZIPInputStream gzipInputStream = null;
			gzipInputStream = new GZIPInputStream(in);
			out = new ByteArrayOutputStream();
		 
		    byte[] buf = new byte[1024];
		    int len;
		    while ((len = gzipInputStream.read(buf)) > 0)
		        out.write(buf, 0, len);
		 
		    gzipInputStream.close();
		    out.close();
		    if(in==null) new File(inFilePath).delete();
		    
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	    return out;
	}	
	
	//Kann später gelöscht werden wird ersetzt durch die Methode 
	public HashMap<String,String> getKVKDaten(byte[] daten){
		SystemConfig.hmKVKDaten.clear();
		//System.out.println("Eintritt in getKVKDaten");
		try{
		byte[] bytes = daten; //daten.getBytes();
		String string = new String(daten);
		int found = -1;
		int lang;
		int stand = 0;
		for(int y = 1; y < tags.length;y++){
			found = -1;
			for(int i = stand; i < bytes.length;i++){
				if(bytes[i] == (byte)tags[y]){
					found = i;
					stand = i;
					break;
				}
				
			}
			if(found >= 0){
				try{
					lang = (int) (0x000000FF & bytes[found+1]);
					found = found+2;
					SystemConfig.hmKVKDaten.put(hmProperty[y],
							StringTools.do301NormalizeString(string.substring(found,found+lang)));
				}catch(Exception ex){
					//ex.printStackTrace();
					//Karte ist keine KV-Karte, Anzahl Tags stimmt nicht.
					JOptionPane.showMessageDialog(null,"keine KV-Karte oder Karte defekt");
					SystemConfig.hmKVKDaten.clear();
					isCardReady = false;
					return SystemConfig.hmKVKDaten;
				}
			}else{
				try{
					SystemConfig.hmKVKDaten.put(hmProperty[y],"");
				}catch(Exception ex){
					//ex.printStackTrace();
					//Karte ist keine KV-Karte, Anzahl Tags stimmt nicht.
					JOptionPane.showMessageDialog(null,"keine KV-Karte oder Karte defekt");
					SystemConfig.hmKVKDaten.clear();
					isCardReady = false;
					return SystemConfig.hmKVKDaten;
				}	
			}
		}
		}catch(Exception ex){
			//ex.printStackTrace();
			//Karte ist keine KV-Karte, Anzahl Tags stimmt nicht.
			System.out.println("keine KV-Karte oder Karte defekt");
			SystemConfig.hmKVKDaten.clear();
			isCardReady = false;
			return SystemConfig.hmKVKDaten;
		}
		if(SystemConfig.hmKVKDaten.isEmpty()){
			System.out.println("keine KV-Karte oder Karte defekt");
			SystemConfig.hmKVKDaten.clear();
			isCardReady = false;
		}
		return SystemConfig.hmKVKDaten;
		
	}
	private HashMap<String,String> checkKVK_ASN1(byte[] response,Object[][] tags){
		int dataLength = -1;
		int tagLength = -1;
		int startByte = -1;
		int i = -1,i2=-1;
		int testen = -1;
		byte[] value = null;
		if((int) (0x000000FF & response[0]) !=  (Integer)tags[0][0] ){
			JOptionPane.showMessageDialog(null,"Chip-Karte ist defekt oder keine KV-Karte!" );
			SystemConfig.hmKVKDaten.clear();
			//System.out.println("Die Karte ist keine KV-Karte");
			SystemConfig.hmKVKDaten.clear();
			return SystemConfig.hmKVKDaten;
		}else{
			dataLength = (int) (0x000000FF & response[1]);
		}
		
		//Falls nicht sofort mit dem Tag 1 begonnen wird;
		for(i = 2; i < dataLength; i++){
			if((int) (0x000000FF & response[i]) ==  (Integer)tags[1][0] ){
				//System.out.println("Tag 1 beginnt bei Byte "+i);
				startByte = Integer.valueOf(i);
				break;
			}	
		}

		if(startByte < 0){
			/*System.out.println("Fehler Tag 1 nicht gefunden");*/
			SystemConfig.hmKVKDaten.clear();
			return SystemConfig.hmKVKDaten;
		}
		SystemConfig.hmKVKDaten.put("Anrede", "HERR");
		for(i=1; i < tags.length;i++ ){
			//Wenn eines der optionalen Tags nicht vorhanden ist...
			if((int) (0x000000FF & response[startByte]) !=  (Integer)tags[i][0] ){
				//kvinhalte.put((String)tags[i][1],"");
				if(! (Boolean) tags[i][2] ){
					JOptionPane.showMessageDialog(null,"Das Pflichtfeld "+(String)tags[i][1]+" ist auf der Karte nicht vorhanden" );
					SystemConfig.hmKVKDaten.clear();
					return SystemConfig.hmKVKDaten;
				}
				SystemConfig.hmKVKDaten.put((String)tags[i][1],"");
				continue;
			}
			startByte += 1;
			tagLength = (int) (0x000000FF & response[startByte]);
			startByte += 1;
			value = new byte[tagLength];
			for(i2 = startByte; i2 < startByte+tagLength;i2++){
				value[i2-startByte] = response[i2];
			}
			//kvinhalte.put((String)tags[i][1],new String(value));
			SystemConfig.hmKVKDaten.put((String)tags[i][1],
					StringTools.do301NormalizeString(new String(value)));
			startByte += tagLength;
		}
		return SystemConfig.hmKVKDaten;
		
	}

	
	private int initProperties(){
		int ret = 0;
		try{
			 Properties sysProps = System.getProperties();
			 sysProps.put ("OpenCard.terminals", "de.cardcontact.opencard.terminal.ctapi4ocf.CTAPICardTerminalFactory|"+
					 aktReaderName+"|CTAPI|"+aktDeviceId+"|"+aktDllName);
			 sysProps.put ("OpenCard.services", "opencard.opt.util.PassThruCardServiceFactory");
		}catch(Exception ex){
			return -1;
		}
		return ret;
	}
	private void initTerminal(boolean test) throws Exception,ClassNotFoundException,
	CardTerminalException,UnsatisfiedLinkError{
		int ret = -1;
		SmartCard.start();
		if(! test){
			clistener = new CardListener(this);
			clistener.register();
		}
		cr = new CardRequest(CardRequest.ANYCARD, null, PassThruCardService.class);
		cr.setTimeout(0);
	}
	/********
	 * 
	 * ermittelt die angeschlossenen CardReader und legt die Daten im Instanz-Vector vecreader ab
	 * 
	 * @return
	 */
	public Vector<Vector<String>> getReaderList(){
		vecreader.clear();
		Vector<String> vname = new Vector<String>();
		Vector<String> vadress = new Vector<String>();
		Vector<String> vslots = new Vector<String>();
		Vector<String> vtype = new Vector<String>();
		try {
			Enumeration<?> terminals = CardTerminalRegistry.getRegistry().getCardTerminals();
			CardTerminal terminal = null;
			while (terminals.hasMoreElements()) {
			terminal = (CardTerminal) terminals.nextElement();
				vname.add(String.valueOf(terminal.getName()));
				vadress.add(String.valueOf(terminal.getAddress()));
				vslots.add(String.valueOf(terminal.getSlots()));
				vtype.add(String.valueOf(terminal.getType()));
			}
		} catch(Exception e) {
				//e.printStackTrace();
				return vecreader;
		}
		vecreader.add(vname);
		vecreader.add(vadress);
		vecreader.add(vslots);
		vecreader.add(vtype);
		return vecreader;
	}
	public void TerminalDeaktivieren(){
		if(sc != null){
			try {
				clistener.unregister();
				sc.close();
				SmartCard.shutdown();
				clistener = null;
				sc = null;
			} catch (CardTerminalException e) {
				e.printStackTrace();
			}
		}else{
			clistener.unregister();
			clistener = null;
			try {
				SmartCard.shutdown();
			} catch (CardTerminalException e) {
				e.printStackTrace();
			}
		}
	}
}
class CardListener implements CTListener {
	private SmartCard smartcard = null;
	private CardTerminal terminal=null;
	private int slotID = 0;
	OcKVK eltern = null;
	public CardListener(OcKVK xeltern){
		eltern =xeltern;
	}
	public void register(){
		EventGenerator.getGenerator().addCTListener(this);
		try {
			EventGenerator.getGenerator().createEventsForPresentCards(this);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
	public void unregister(){
		EventGenerator.getGenerator().removeCTListener(this);
	}
	public void cardInserted(CardTerminalEvent event) throws CardTerminalException{
		if(! eltern.systemStarted){
			JOptionPane.showMessageDialog(null,"Im Kartenlesegerät steckt noch eine Chipkarte!!!!\n\nBitte entnehmen und Eigentümer aushändigen\n");
			return;
		}
		//System.out.println("karte eingesteckt");
		if (smartcard == null) {

				smartcard = SmartCard.getSmartCard(event);
				terminal = event.getCardTerminal();
				slotID = event.getSlotID();
				eltern.isCardReady = false;
				try {
					eltern.lesen();
					eltern.isCardReady = true;
					if(Reha.thisClass.patpanel != null){
						if(Reha.thisClass.patpanel.getLogic().pneu != null){
							new SwingWorker<Void,Void>(){
								@Override
								protected Void doInBackground()
										throws Exception {
									Reha.thisClass.patpanel.getLogic().pneu.enableReaderButton();
									return null;
								}
							}.execute();
						}	
					}
				} catch (CardServiceException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
		}else{
			smartcard = SmartCard.getSmartCard(event);
			terminal = event.getCardTerminal();
			slotID = event.getSlotID();
			eltern.isCardReady = false;
		}
	}
	public void cardRemoved(CardTerminalEvent event){
		//System.out.println("karte ausgezogen");
		if ((event.getSlotID() == slotID) &&(event.getCardTerminal()==terminal)) {
			smartcard = null;
			terminal = null;
			slotID = -1;
			eltern.isCardReady = false;
			if(Reha.thisClass.patpanel != null){
				if(Reha.thisClass.patpanel.getLogic().pneu != null){
					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground()
								throws Exception {
							Reha.thisClass.patpanel.getLogic().pneu.disableReaderButton();
							return null;
						}
					}.execute();
				}	
			}
			
		}else{
			//System.out.println("anderer Slot oder anderer Terminal");
		}
	}


}

