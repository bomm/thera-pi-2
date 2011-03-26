package reha301;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.jdesktop.swingworker.SwingWorker;


public class RehaIOServer extends SwingWorker<Void,Void>{
	public ServerSocket serv = null;
	StringBuffer sb = new StringBuffer();
	InputStream input = null;
	OutputStream output = null;
	//public int port = 6000;
	public static boolean reha301IsActive = false;
	public static boolean offenePostenIsActive = false;
	
	public RehaIOServer(int x){
		Reha301.xport = x;
		execute();
	}	
		
	public String getPort(){
		return Integer.toString(Reha301.xport);
	}
	private void doOffenePosten(String op){
		if(op.split("#")[1].equals(RehaIOMessages.IS_STARTET)){
			Reha301.thisFrame.setCursor(Reha301.thisClass.cdefault);
			offenePostenIsActive = true;
			System.out.println("301-er  Modul gestartet");
			return;
		}else if(op.split("#")[1].equals(RehaIOMessages.IS_FINISHED)){
			offenePostenIsActive = false;
			Reha301.thisFrame.toFront();
			System.out.println("301-er  Modul beendet");
			return;
		}
	}
	/*****
	 * 
	 * 
	 * 301-er
	 */
	private void doReha301(String op){
		if(op.split("#")[1].equals(RehaIOMessages.IS_STARTET)){
			Reha301.thisFrame.setCursor(Reha301.thisClass.cdefault);
			reha301IsActive = true;
			System.out.println("301-er  Modul gestartet");
			return;
		}else if(op.split("#")[1].equals(RehaIOMessages.IS_FINISHED)){
			reha301IsActive = false;
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                	Reha301.thisFrame.toFront();
                	Reha301.thisFrame.repaint();
                }
            });
			System.out.println("301-er  Modul beendet");
			return;
		}else if(op.split("#")[1].equals(RehaIOMessages.MUST_PATANDREZFIND)){
		}else if(op.split("#")[1].equals(RehaIOMessages.MUST_PATFIND)){
		}
		//JOptionPane.showMessageDialog(null, "Hallo Reha hier spricht das 301-er Modul");
	}
	@Override
	protected Void doInBackground() throws Exception {
			
			while(Reha301.xport < 7050){
				try {
					serv = new ServerSocket(Reha301.xport);
					break;
				} catch (Exception e) {
					System.out.println("In Exception währen der Portsuche - 1");
					if(serv != null){
						try {
							serv.close();
						} catch (IOException e1) {
							System.out.println("In Exception währen der Portsuche - 2");
							e1.printStackTrace();
						}
						serv = null;
					}
					//e.printStackTrace();
					System.out.println("Port: "+Reha301.xport+" bereits belegt");
					Reha301.xport++;
				}
			}
			if(Reha301.xport==7050){
				JOptionPane.showMessageDialog(null, "Fehler bei der Initialisierung des IO-Servers (kein Port frei!)");
				Reha301.xport = -1;
				serv = null;
				return null;
			}
			System.out.println("IO-SocketServer installiert auf Port: "+Reha301.xport);
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
				if(sb.toString().startsWith("Reha301#")){
					doReha301(String.valueOf(sb.toString()) );
					
				}else if(sb.toString().startsWith("OffenePosten#")){
					doOffenePosten(String.valueOf(sb.toString()));
				}
			}

//			return null;

		}
	private RehaIOServer getInstance(){
		return this;
	}
	
	
}