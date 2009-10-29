package kvKarte;





import systemEinstellungen.SystemConfig;
import hauptFenster.Reha;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.examples.win32.W32API.HWND;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallFunctionMapper;
import com.sun.jna.win32.StdCallLibrary;

public class Win_ChipDriveMicro100 {
	String sCommando = "";
	//SCard32 lib = null;
	String sCommand = "";
	//HWND hwnd;
	int bufLen = 0;
	int ret = -1;
	static SCard32 lib = null;
	static boolean isreg = false; 
	public Win_ChipDriveMicro100(){
		if(this.lib == null){
			this.lib = SCard32.INSTANCE;			
		}
		if(! isreg){
			HWND hwnd = new HWND(); 
			hwnd.setPointer(Native.getWindowPointer(Reha.thisFrame));
			PointerByReference pbf = new PointerByReference((Pointer)hwnd.getPointer());
			String[] spbf = pbf.getValue().toString().split("@");
			int i = Integer.parseInt(spbf[1].split("x")[1],16);
			sCommand = "System,AddHWndMsg,"+i+",1524";
			bufLen = 560;
			String[] lesen = kartenAktion();
			isreg = true;
		}

	}
	public int patientEinlesen(){
		sCommand = "Apps,KVK";
		bufLen = 560;
		String[] lesen = kartenAktion();
		return Integer.parseInt(lesen[0],16);
	}
 

	public String[] kartenAktion(){
 
			
			Pointer hwnd = new IntByReference(0).getPointer();
			String sCommand = new String(this.sCommand);
			//String sCommand = new String("Device,Info");
			Pointer cmdLen = new IntByReference(sCommand.length()).getPointer();

			byte []sIn = "".getBytes();
			Pointer inLen = new IntByReference(0).getPointer();
			byte[] output = new byte[this.bufLen];
			Pointer outLen = new IntByReference(this.bufLen).getPointer();		
			try {
			ret = lib.SCardComand(
									hwnd,
									sCommand,
									cmdLen,
									"",
									inLen,
									output,
									outLen
									);
			
			//System.out.println("Rückgabewert der Funktion  = "+new Integer(ret).toHexString(ret) );
			//System.out.println("Output = "+Native.toString(output) );
			}catch (UnsatisfiedLinkError e) {
				e.printStackTrace();
			}
			if(ret != 0){
				String fehler = fehlerDeuten(ret,lib);	
				return new String[] {Integer.toString(-1),fehler};
			}
			System.out.println(Native.toString(output));
			if(fuelleHashMap(Native.toString(output))){
				return new String[] {Integer.toString(ret),Native.toString(output)};
			}else{
				return new String[] {Integer.toString(-1),Native.toString(output)};
			}
			
		
	}
		public boolean fuelleHashMap(String hm){
			String[] hmdaten = hm.split("\r\n");
			if(hmdaten.length == 15){
				SystemConfig.hmKVKDaten.put("Krankekasse", hmdaten[0].split("=")[1]);
				SystemConfig.hmKVKDaten.put("Kassennummer", hmdaten[1].split("=")[1]);
				SystemConfig.hmKVKDaten.put("Kartennummer", hmdaten[2].split("=")[1]);
				SystemConfig.hmKVKDaten.put("Versichertennummer", hmdaten[3].split("=")[1]);			
				SystemConfig.hmKVKDaten.put("Status", hmdaten[4].split("=")[1]);			
				SystemConfig.hmKVKDaten.put("Statusext", hmdaten[5].split("=")[1]);
				SystemConfig.hmKVKDaten.put("Vorname", hmdaten[6].split("=")[1]);			
				SystemConfig.hmKVKDaten.put("Nachname", hmdaten[7].split("=")[1]);			
				SystemConfig.hmKVKDaten.put("Geboren", hmdaten[8].split("=")[1]);			
				SystemConfig.hmKVKDaten.put("Strasse", hmdaten[9].split("=")[1]);			
				SystemConfig.hmKVKDaten.put("Land", hmdaten[10].split("=")[1]);			
				SystemConfig.hmKVKDaten.put("Plz", hmdaten[11].split("=")[1]);			
				SystemConfig.hmKVKDaten.put("Ort", hmdaten[12].split("=")[1]);			
				SystemConfig.hmKVKDaten.put("Gueltigkeit", hmdaten[13].split("=")[1]);			
				SystemConfig.hmKVKDaten.put("Checksumme", hmdaten[14].split("=")[1]);
				return true;
			}
			return false;
		}
		
		public String fehlerDeuten(int ibr,SCard32 lib){
			Pointer hwnd = new IntByReference(0).getPointer();
			String sCommand = new String("System,Info");
			Pointer cmdLen = new IntByReference(sCommand.length()).getPointer();
			byte []sIn = "".getBytes();
			Pointer inLen = new IntByReference(0).getPointer();
			byte[] output = new byte[560];
			Pointer outLen = new IntByReference(560).getPointer();		
			String str = null;
			int ibrx = lib.SCardComand(
					hwnd,
					sCommand,
					cmdLen,
					"",
					inLen,
					output,
					outLen
					);
			String[] hmdaten = Native.toString(output).split("\r\n");
			SystemConfig.hmKVKDaten.put("Fehlercode",hmdaten[4].split("=")[1] );
			SystemConfig.hmKVKDaten.put("Fehlertext",hmdaten[5].split("=")[1] );

			
			return Native.toString(output);
		}
	

		interface SCard32 extends StdCallLibrary { 

			 StdCallFunctionMapper mapper = new StdCallFunctionMapper();

			 SCard32 INSTANCE = (SCard32) Native.loadLibrary("Scard32", SCard32.class);

			 public int SCardComand(
					 Pointer hwnd,
					 String Cmd, 
					 Pointer CmdLen, 
					 String dataIn, 
					 Pointer dataInLen, 
					 byte[] dataOut, 
					 Pointer dataoutlen
			 		);
			
			 /*
			  * Original C-Deklaration
			 LPINT Handle  Zeiger auf einen 32 Bit signed integer
			 LPSTR Cmd  Zeiger auf einen null terminierten String 
			 LPINT CmdLen  Zeiger auf einen 32 Bit signed integer 
			 LPSTR DataIn  Zeiger auf ein array of byte oder einen String 
			 LPINT DataInLen  Zeiger auf einen 32 Bit signed integer 
			 LPSTR DataOut  Zeiger auf ein array of byte oder einen String 
			 LPINT DataOutLen  Zeiger auf einen 32 Bit signed integer 
			 INT Response  32 Bit signed integer 	 
			 */

			} 

}
