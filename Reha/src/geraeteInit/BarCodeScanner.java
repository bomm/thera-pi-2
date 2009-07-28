package geraeteInit;

import hauptFenster.Reha;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.TooManyListenersException;
import java.util.Vector;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import javax.comm.UnsupportedCommOperationException;
import javax.swing.JOptionPane;

import sqlTools.SqlInfo;




public class BarCodeScanner implements Runnable, SerialPortEventListener{
	   static CommPortIdentifier portId = null;
	   static Enumeration  portList = null;
	   InputStream inputStream = null;
	   static OutputStream outputStream = null;
	   static SerialPort serialPort = null;
	   Thread readThread = null;;
	   boolean portFound = false; 
	   //static String port;
	public BarCodeScanner(String port) throws Exception{
		portList = CommPortIdentifier.getPortIdentifiers();
		while (portList.hasMoreElements()) {
		    portId = (CommPortIdentifier) portList.nextElement();
		    //System.out.println(portId);
		    if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (portId.getName().equals(port)) {
				    System.out.println("Port gefunden und intitialisiert. Port = "+port);
				    portFound = true;
				    break;
				    //BarCodeScanner reader = new BarCodeScanner(port);
				} 
		    }
		}
		if(!portFound){
			System.out.println("Port "+port+" wurde nicht gefunden");
		}
		
		try {
			
		    serialPort = (SerialPort) portId.open("Thera-Pi", 2000);
		    serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1, 
                    SerialPort.PARITY_NONE); //     
		    serialPort.setFlowControlMode( SerialPort.FLOWCONTROL_NONE ); //     

		} catch (PortInUseException e) {
			System.out.println("Port f�r Barcode-Scanner "+port+" konnte nicht ge�ffnet werden.\n\nBereits belegt?");
			System.out.println("Derzeitiger Besitzer = "+e.currentOwner);
			//JOptionPane.showMessageDialog(null, "Port f�r Barcode-Scanner "+port+" konnte nicht ge�ffnet werden.\n\nBereits bele?");
		}
		

		try {
		    inputStream = serialPort.getInputStream();
		} catch (IOException e) {}

		try {
		    serialPort.addEventListener(this);
		} catch (TooManyListenersException e) {}

		serialPort.notifyOnDataAvailable(true);
		

		try {
		    serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, 
						   SerialPort.STOPBITS_1, 
						   SerialPort.PARITY_NONE);
		} catch (UnsupportedCommOperationException e) {}
		if(portFound){
			readThread = new Thread(this);
			System.out.println("Barcode-Thread gestartet\nBenutze Port: "+port);
			readThread.start();
		}else{
			JOptionPane.showMessageDialog(null, "Barcode-Scanner konnte nicht akiviert werden");
		}
    }

	@Override
	public void run() {
		/*
		while(Reha.thisClass != null){
			try {
			    Thread.sleep(2000);
			} catch (InterruptedException e) {
				
			}
		}*/
		//System.out.println("Scanner-Thread beendet");
	}

	@Override
	public void serialEvent(SerialPortEvent event)  {
		switch (event.getEventType()) {

		case SerialPortEvent.BI:
			System.out.println("Event Type "+event.getEventType());
			break;
		case SerialPortEvent.OE:
			System.out.println("Event Type "+event.getEventType());
			break;			
		case SerialPortEvent.FE:
			System.out.println("Event Type "+event.getEventType());
			break;			
		case SerialPortEvent.PE:
			System.out.println("Event Type "+event.getEventType());
			break;			
		case SerialPortEvent.CD:
			System.out.println("Event Type "+event.getEventType());
			break;			
		case SerialPortEvent.CTS:
			System.out.println("Event Type "+event.getEventType());
			break;			
		case SerialPortEvent.DSR:
			System.out.println("Event Type "+event.getEventType());
			break;			
		case SerialPortEvent.RI:
			System.out.println("Event Type "+event.getEventType());
			break;			
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			System.out.println("Event Type "+event.getEventType());
		case SerialPortEvent.DATA_AVAILABLE:
			//System.out.println("Data available");
		    byte[] readBuffer = new byte[30];
		    byte[] outBuffer = null;
		    String sout = null;
		    StringBuffer sb = new StringBuffer();
		    char[] zeichen = new char[1];  
		    byte[] buffer = new byte[1024];
		    String outString = "";
		    try {
		    	int numBytes = 0;
		    	/*
				while (inputStream.available() > 0 ) {
					numBytes = inputStream.read();
					if(numBytes >= 32){
						zeichen[0] = (char) numBytes;
						sb.append(zeichen); 
						
					}
				}*/

	    		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    		while (inputStream.available() > 0 ){
	    			numBytes = inputStream.read(buffer);
		    		byteArrayOutputStream.write(buffer, 0, numBytes);
		    		Thread.sleep(50);
				}
		    	
		    	byteArrayOutputStream.flush();
		    	outString = sb.append( byteArrayOutputStream.toString() ).toString();
		    	//System.out.println("String2 = "+outString);
		    	byteArrayOutputStream.close();
				if(outString.length()>= 2){
					if("KGMALOERRH".contains(outString.substring(0,2))){
						final String xoutString = outString;
						new Thread(){
							public void run(){
								erfasseTermin(xoutString);
							}
						}.start();
						JOptionPane.showMessageDialog(null,"Rezept-Scan angekommen\nScaninhalt = -> "+sb.toString());
					}else{
						JOptionPane.showMessageDialog(null,"Kein Rezeptscan\nScaninhalt = -> "+sb.toString());
					}
				}

		    } catch (IOException e){
				System.out.println("****in Catch-Clause - Fehler in IO-System****");
				e.printStackTrace();
		    } catch (InterruptedException e) {
				e.printStackTrace();
			}
		    break;
	    }
	}
	public void erfasseTermin(String reznum){
		Vector<String> pat_int = SqlInfo.holeSatz("verordn", "pat_int,anzahl1", "rez_nr='"+reznum+"'", Arrays.asList(new String[] {}));
		if(pat_int.size()==0){
			return;
		}
		
	}
}
