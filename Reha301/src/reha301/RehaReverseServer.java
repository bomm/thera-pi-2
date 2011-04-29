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


public class RehaReverseServer extends SwingWorker<Void,Void>{
	public ServerSocket serv = null;
	StringBuffer sb = new StringBuffer();
	InputStream input = null;
	OutputStream output = null;
	//public int port = 6000;
	public static boolean reha301IsActive = false;
	public static boolean offenePostenIsActive = false;
	
	public RehaReverseServer(int x){
		Reha301.xport = x;
		execute();
	}	
		
	public String getPort(){
		return Integer.toString(Reha301.xport);
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
			//System.out.println("301-er  Modul beendet");
			return;
		}else if(op.split("#")[1].equals(RehaIOMessages.MUST_PATANDREZFIND)){
		}else if(op.split("#")[1].equals(RehaIOMessages.MUST_PATFIND)){
		}else if(op.split("#")[1].equals(RehaIOMessages.MUST_GOTOFRONT)){
			Reha301.thisFrame.setVisible(true);
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
					////System.out.println("In Exception währen der Portsuche - 1");
					if(serv != null){
						try {
							serv.close();
						} catch (IOException e1) {
							////System.out.println("In Exception währen der Portsuche - 2");
							e1.printStackTrace();
						}
						serv = null;
					}
					Reha301.xport++;
				}
			}
			if(Reha301.xport==7050){
				JOptionPane.showMessageDialog(null, "Fehler bei der Initialisierung des IO-Servers (kein Port frei!)");
				Reha301.xport = -1;
				serv = null;
				return null;
			}
			Reha301.xportOk = true;
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
					//System.out.println("In Exception währen der while input.read()-Schleife");
				}
				/***************************/
				//System.out.println("In Reha301 - eingegangene Nachricht = "+sb.toString());
				if(sb.toString().startsWith("Reha301#")){
					doReha301(String.valueOf(sb.toString()) );
					
				}
			}

//			return null;

		}
	private RehaReverseServer getInstance(){
		return this;
	}
	
	
}