package RehaIO;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;



import offenePosten.OffenePosten;

import org.jdesktop.swingworker.SwingWorker;


public class RehaReverseServer extends SwingWorker<Void,Void>{
	public ServerSocket serv = null;
	StringBuffer sb = new StringBuffer();
	InputStream input = null;
	OutputStream output = null;
	//public int port = 6000;
	public static boolean OffenePostenIsActive = false;
	public static boolean offenePostenIsActive = false;
	
	public RehaReverseServer(int x){
		OffenePosten.xport = x;
		execute();
	}	
		
	public String getPort(){
		return Integer.toString(OffenePosten.xport);
	}
	
	private void doReha(String op){
		if(op.split("#")[1].equals(RehaIOMessages.MUST_GOTOFRONT)){
			OffenePosten.thisFrame.setVisible(true);
		}else if(op.split("#")[1].equals(RehaIOMessages.MUST_REZFIND)){
			
		}

	}
	@Override
	protected Void doInBackground() throws Exception {
			
			while(OffenePosten.xport < 7050){
				try {
					serv = new ServerSocket(OffenePosten.xport);
					break;
				} catch (Exception e) {
					//System.out.println("In Exception währen der Portsuche - 1");
					if(serv != null){
						try {
							serv.close();
						} catch (IOException e1) {
							//System.out.println("In Exception währen der Portsuche - 2");
							e1.printStackTrace();
						}
						serv = null;
					}
					OffenePosten.xport++;
				}
			}
			if(OffenePosten.xport==7050){
				JOptionPane.showMessageDialog(null, "Fehler bei der Initialisierung des IO-Servers (kein Port frei!)");
				OffenePosten.xport = -1;
				serv = null;
				return null;
			}
			OffenePosten.xportOk = true;
			Socket client = null;

			while(true){
				try {
					client = serv.accept();
				} catch (SocketException se) {
					//se.printStackTrace();
					return null;
				}
				sb.setLength(0);
				sb.trimToSize();
				input = client.getInputStream();
				//output = client.getOutputStream();
				int byteStream;
				//String test = "";
				try {
					while( (byteStream =  input.read()) > -1){
						char b = (char)byteStream;
						sb.append(b);
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("In Exception währen der while input.read()-Schleife");
				}
				/***************************/
				
				if(sb.toString().startsWith("Reha#")){
					doReha(String.valueOf(sb.toString()) );
				}
			}

//			return null;

		}
	private RehaReverseServer getInstance(){
		return this;
	}
	
	
}