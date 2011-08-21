package ocf;

import hauptFenster.Reha;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;

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
	final byte[] CMD_READ_BINARY ={(byte)0x00, (byte)0xB0,(byte)0x00, (byte)0x00, (byte)0x300 };
	private static final int MAX_APDU_SIZE = 0x300;  //  bytes
	//Wird später ersetzt durch kvkTags
	final static String[] hmProperty = {"Rohdaten","Krankenkasse","Kassennummer","Kartennummer","Versichertennummer",
		"Status","Statusext","Titel","Vorname","Namenszusatz",
		"Nachname","Geboren","Strasse","Land","Plz",
		"Ort","Gueltigkeit","Checksumme"};
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
	public OcKVK(String readerName,String dllName,boolean test) throws Exception,UnsatisfiedLinkError {
		//SCR335
		//ctpcsc31kv
		// nur in der Testphase der testphase
		SystemConfig.hmKVKDaten = new HashMap<String,String>();
		// danach wird das Gedönse in der SystemConfig initialisiert.
		
		this.aktReaderName = readerName;
		this.aktDllName = dllName;
		initProperties();
		initTerminal(test);
		systemStarted = true;
		terminalOk = true;
	}

	public int lesen() throws CardTerminalException, CardServiceException, ClassNotFoundException{
		int ret = 0;

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
		    command.append(CMD_READ_BINARY);
		    response = ptcs.sendCommandAPDU(command);

		    if(response == null || response.getBytes().length==0){
		    	System.out.println("keine KV-Karte oder Karte defekt");
		    	sc.close();
		    	return -1;
		    }
		    if(response.getByte(0)== (byte)0x60){ //Nach ASN.1 Standard der KVK
		    	//getKVKDaten(response.getBytes());
		    	checkKVK_ASN1(response.getBytes(),kvkTags);
			    //System.out.print(SystemConfig.hmKVKDaten);
			    sc.close();
		    }else{
		    	//Hier entweder neue Routine, falls betriebsintern
		    	//noch anderweitige Chipkarten eingesetzt werden z.B. Zugangskontrolle etc.
		    	//oder Fehlermeldung daß Karte keine KV-Karte ist.
		    	ret = -1;
		    	sc.close();
		    }
		    sc.close();
		    return ret;    
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
					 aktReaderName+"|CTAPI|0|"+aktDllName);
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

