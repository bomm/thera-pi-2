package geraeteInit;

import hauptFenster.AktiveFenster;
import hauptFenster.Reha;

import java.awt.Dimension;
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
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import org.jdesktop.swingworker.SwingWorker;

import dialoge.SchluesselDialog;

import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import terminKalender.TerminFenster;
import terminKalender.TermineErfassen;
import terminKalender.DatFunk;




public class BarCodeScanner implements Runnable, SerialPortEventListener{
	   static CommPortIdentifier portId = null;
	   static Enumeration  portList = null;
	   InputStream inputStream = null;
	   static OutputStream outputStream = null;
	   static SerialPort serialPort = null;
	   Thread readThread = null;;
	   boolean portFound = false; 
	   //static String port;
	   int baud,bits,stopbit,parity ;
	   final byte[] fbuffer = new byte[50];
	public BarCodeScanner(String port) throws Exception{
		portList = CommPortIdentifier.getPortIdentifiers();
		while (portList.hasMoreElements()) {
		    portId = (CommPortIdentifier) portList.nextElement();
		    ////System.out.println(portId);
		    if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (portId.getName().equals(port)) {
				    //System.out.println("Port gefunden und intitialisiert. Port = "+port);
				    portFound = true;
				    break;
				    //BarCodeScanner reader = new BarCodeScanner(port);
				} 
		    }
		}
		if(!portFound){
			//System.out.println("Port "+port+" wurde nicht gefunden");
		}
		String[] params = SystemConfig.hmGeraete.get(port);
		baud = new Integer(params[0]);
		bits = new Integer(params[1]);
		stopbit = new Integer(params[3]);
		if(params[2].equals("EVEN")){
			parity = SerialPort.PARITY_EVEN;
		}else if(params[2].equals("ODD")){
			parity = SerialPort.PARITY_ODD;
		}else if(params[2].equals("NONE")){
			parity = SerialPort.PARITY_NONE;
		}
		try {
			
		    serialPort = (SerialPort) portId.open("Thera-Pi", 2000);
		    serialPort.setSerialPortParams(baud, bits,
                    stopbit, 
                    parity); //     
		    serialPort.setFlowControlMode( SerialPort.FLOWCONTROL_NONE ); //     

		} catch (PortInUseException e) {
			//System.out.println("Port für Barcode-Scanner "+port+" konnte nicht geöffnet werden.\n\nBereits belegt?");
			//System.out.println("Derzeitiger Besitzer = "+e.currentOwner);
			return;
			//JOptionPane.showMessageDialog(null, "Port f�r Barcode-Scanner "+port+" konnte nicht ge�ffnet werden.\n\nBereits bele?");
		}
		

		try {
		    inputStream = serialPort.getInputStream();
		} catch (IOException e) {}

		try {
		    serialPort.addEventListener(this);
		} catch (TooManyListenersException e) {}

		serialPort.notifyOnDataAvailable(true);
        //serialPort.enableReceiveThreshold(1);
        //serialPort.enableReceiveTimeout(1000);

		

		try {
		    serialPort.setSerialPortParams(baud, bits,
                    stopbit, 
                    parity); //     
		} catch (UnsupportedCommOperationException e) {}
		if(portFound){
			readThread = new Thread(this);
			//System.out.println("Barcode-Thread gestartet\nBenutze Port: "+port);
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
		////System.out.println("Scanner-Thread beendet");
	}

	@Override
	public void serialEvent(SerialPortEvent event)  {
		switch (event.getEventType()) {

		case SerialPortEvent.BI:
			//System.out.println("Event Type "+event.getEventType());
			break;
		case SerialPortEvent.OE:
			//System.out.println("Event Type "+event.getEventType());
			break;			
		case SerialPortEvent.FE:
			//System.out.println("Event Type "+event.getEventType());
			break;			
		case SerialPortEvent.PE:
			//System.out.println("Event Type "+event.getEventType());
			break;			
		case SerialPortEvent.CD:
			//System.out.println("Event Type "+event.getEventType());
			break;			
		case SerialPortEvent.CTS:
			//System.out.println("Event Type "+event.getEventType());
			break;			
		case SerialPortEvent.DSR:
			//System.out.println("Event Type "+event.getEventType());
			break;			
		case SerialPortEvent.RI:
			//System.out.println("Event Type "+event.getEventType());
			break;			
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			//System.out.println("Event Type "+event.getEventType());
		case SerialPortEvent.DATA_AVAILABLE:
			////System.out.println("Data available");
		    //byte[] readBuffer = new byte[30];
		    byte[] outBuffer = null;
		    String sout = null;
		    StringBuffer sb = new StringBuffer();
		    //char[] zeichen = new char[1];  
		    byte[] buffer = fbuffer; //new byte[1024];
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
		    	////System.out.println("String2 = "+outString);
		    	byteArrayOutputStream.close();
				if(outString.length()>= 2){
					if("KGMALOERRH".contains(outString.substring(0,2))){
						Vector tvec = null;
						JComponent termin = AktiveFenster.getFensterAlle("TerminFenster");
						if(termin != null){
							if(Reha.thisClass.terminpanel.getAktuellerTag().equals(DatFunk.sHeute())){
								// Hier versuchen die Daten des Terminkalenders zu �bernehmen
							}
						}
						final String xoutString = outString.trim();
						Thread erfassen = new Thread(new TermineErfassen(xoutString,tvec));
						erfassen.start();
					}else if(outString.substring(0,1).equals("S")){
						////System.out.println("Schl�ssel Nr. "+outString.replaceAll("\\n", ""));
						outString = outString.substring(2).replaceAll("\\r","");
						outString = outString.replaceAll("\\n","");
						final String schluessel = outString;
						new SwingWorker<Void,Void>(){
							@Override
							protected Void doInBackground() throws Exception {
								try{
									doAusleihe(schluessel);	
								}catch(Exception ex){
									ex.printStackTrace();
								}
								
								return null;
							}
							
						}.execute();
					}else{
						JOptionPane.showMessageDialog(null,"Kein Rezeptscan\nScaninhalt = -> "+sb.toString());
					}
				}
				sb = null;
				outString = null;
				byteArrayOutputStream = null;
				
		    } catch (IOException e){
				//System.out.println("****in Catch-Clause - Fehler in IO-System****");
				e.printStackTrace();
		    } catch (InterruptedException e) {
				e.printStackTrace();
			}
		    break;
	    }
	}
	private void doAusleihe(String ausleihe){
		SchluesselDialog sdiag = new SchluesselDialog(null, "Schluessel Ein- / Ausgabe",ausleihe);
		sdiag.setSize(300,200);
		sdiag.setPreferredSize(new Dimension(300,200));
		sdiag.setLocationRelativeTo(null);
		sdiag.pack();
		sdiag.setModal(true);
		sdiag.setVisible(true);
		sdiag = null;
	}
}
