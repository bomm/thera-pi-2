package hauptFenster;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JOptionPane;

import org.jdesktop.swingworker.SwingWorker;

import events.PatStammEvent;
import events.PatStammEventClass;

public class RehaIOServer extends SwingWorker<Void,Void>{
	public ServerSocket serv = null;
	StringBuffer sb = new StringBuffer();
	InputStream input = null;
	OutputStream output = null;
	//public int port = 6000;
	public static boolean reha301IsActive = false;
	public static int reha301reversePort = -1;
	
	public static boolean offenePostenIsActive = false;
	public static int offenePostenreversePort = -1;
	
	public static boolean rgAfIsActive = false;
	public static int rgAfreversePort = -1;
	
	public static boolean rehaWorkFlowIsActive = false;
	public static int rehaWorkFlowreversePort = -1;
	
	public static boolean rehaSqlIsActive = false;
	public static int rehaSqlreversePort = -1;

	public static boolean rehaMailIsActive = false;
	public static int rehaMailreversePort = -1;

	public RehaIOServer(int x){
		Reha.xport = x;
		execute();
	}	
		
	public String getPort(){
		return Integer.toString(Reha.xport);
	}
	
	/**********
	 * 
	 * 
	 * RehaSql
	 * 
	 * 
	 */
	private void doRehaSql(String op){
		if(op.split("#")[1].equals(RehaIOMessages.IS_STARTET)){
			Reha.thisFrame.setCursor(Reha.thisClass.cdefault);
			rehaSqlIsActive = true;
			return;
		}else if(op.split("#")[1].equals(RehaIOMessages.IS_FINISHED)){
			rehaSqlIsActive = false;
			rehaSqlreversePort = -1;
			return;
		}else if(op.split("#")[1].equals(RehaIOMessages.MUST_PATFIND)){
			if(Reha.thisClass.patpanel != null){
				this.posteAktualisierePat(op.split("#")[2]);
			}
		}else if(op.split("#")[1].equals(RehaIOMessages.MUST_PATANDREZFIND)){
			if(Reha.thisClass.patpanel != null){
				this.posteAktualisierePatUndRez(op.split("#")[2], op.split("#")[3]);
			}
		}else if(op.split("#")[1].equals(RehaIOMessages.MUST_REZFIND)){
			
		}
	}

	/**********
	 * 
	 * 
	 * OffenePosten
	 * 
	 * 
	 */
	private void doOffenePosten(String op){
		if(op.split("#")[1].equals(RehaIOMessages.IS_STARTET)){
			Reha.thisFrame.setCursor(Reha.thisClass.cdefault);
			offenePostenIsActive = true;
			return;
		}else if(op.split("#")[1].equals(RehaIOMessages.IS_FINISHED)){
			offenePostenIsActive = false;
			offenePostenreversePort = -1;
			return;
		}else if(op.split("#")[1].equals(RehaIOMessages.MUST_PATFIND)){
			if(Reha.thisClass.patpanel != null){
				this.posteAktualisierePat(op.split("#")[2]);
			}
		}
	}
	/**********
	 * 
	 * 
	 * WorkFlow
	 * 
	 * 
	 */

	private void doWorkFlow(String op){
		if(op.split("#")[1].equals(RehaIOMessages.IS_STARTET)){
			Reha.thisFrame.setCursor(Reha.thisClass.cdefault);
			rehaWorkFlowIsActive = true;
			return;
		}else if(op.split("#")[1].equals(RehaIOMessages.IS_FINISHED)){
			rehaWorkFlowIsActive = false;
			rehaWorkFlowreversePort = -1;
			return;
		}
	}
	/**********
	 * 
	 * 
	 * OpRgaf
	 * 
	 * 
	 */
	
	private void doOpRgaf(String op){
		if(op.split("#")[1].equals(RehaIOMessages.IS_STARTET)){
			Reha.thisFrame.setCursor(Reha.thisClass.cdefault);
			rgAfIsActive = true;
			return;
		}else if(op.split("#")[1].equals(RehaIOMessages.IS_FINISHED)){
			rgAfIsActive = false;
			rgAfreversePort = -1;
			return;
		}else if(op.split("#")[1].equals(RehaIOMessages.MUST_PATANDREZFIND)){
			if(Reha.thisClass.patpanel != null){
				this.posteAktualisierePatUndRez(op.split("#")[2], op.split("#")[3]);
			}
		}
	}
	/**********
	 * 
	 * 
	 * 301-er
	 * 
	 * 
	 */
	private void doReha301(String op){
		if(op.split("#")[1].equals(RehaIOMessages.IS_STARTET)){
			Reha.thisFrame.setCursor(Reha.thisClass.cdefault);
			reha301IsActive = true;
			return;
		}else if(op.split("#")[1].equals(RehaIOMessages.IS_FINISHED)){
			reha301IsActive = false;
			reha301reversePort = -1;
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					Reha.thisFrame.setVisible(true);
					return null;
				}
			}.execute();
			return;
		}else if(op.split("#")[1].equals(RehaIOMessages.MUST_PATANDREZFIND)){
			if(Reha.thisClass.patpanel != null){
				this.posteAktualisierePatUndRez(op.split("#")[2], op.split("#")[3]);
			}
		}else if(op.split("#")[1].equals(RehaIOMessages.MUST_PATFIND)){
			if(Reha.thisClass.patpanel != null){
				this.posteAktualisierePat(op.split("#")[2]);
			}
		}
	}
	/**********
	 * 
	 * 
	 * RehaMail
	 * 
	 * 
	 */	
	private void doRehaMail(String op){
		if(op.split("#")[1].equals(RehaIOMessages.IS_STARTET)){
			Reha.thisFrame.setCursor(Reha.thisClass.cdefault);
			rehaMailIsActive = true;
			return;
		}else if(op.split("#")[1].equals(RehaIOMessages.IS_FINISHED)){
			rehaMailIsActive = false;
			rehaMailreversePort = -1;
			return;
		}
	}
	/*************
	 * 
	 * 
	 * 
	 * ReversePort-Handling
	 * 
	 * 
	 * ******************/
	private void doReversePort(String op){
//		new SocketClient().setzeRehaNachricht(rehaPort, "AppName#"+"Reha301#"+Integer.toString(Reha301.xport));
		if(op.split("#")[1].equals("Reha301")){
			reha301reversePort = Integer.parseInt(op.split("#")[2]);
			System.out.println("Port fuer den 301-er Manager = "+op.split("#")[2]);
		}else if(op.split("#")[1].equals("WorkFlow")){
			rehaWorkFlowreversePort = Integer.parseInt(op.split("#")[2]);
			System.out.println("Port fuer den Work-Flow Manager = "+op.split("#")[2]);
		}else if(op.split("#")[1].equals("OpRgaf")){
			rgAfreversePort = Integer.parseInt(op.split("#")[2]);
			System.out.println("Port fuer OpRgaf Modul = "+op.split("#")[2]);
		}else if(op.split("#")[1].equals("OffenePosten")){
			offenePostenreversePort = Integer.parseInt(op.split("#")[2]);
			System.out.println("Port fuer OffenePosten Modul = "+op.split("#")[2]);
		}else if(op.split("#")[1].equals("RehaSql")){
			rehaSqlreversePort = Integer.parseInt(op.split("#")[2]);
			System.out.println("Port fuer RehaSql = "+op.split("#")[2]);
		}else if(op.split("#")[1].equals("RehaMail")){
			rehaMailreversePort = Integer.parseInt(op.split("#")[2]);
			System.out.println("Port fuer RehaMail = "+op.split("#")[2]);
		}
	}
	/*******************************/
	@Override
	protected Void doInBackground() throws Exception {
			
			while(Reha.xport < 6020){
				try {
					serv = new ServerSocket(Reha.xport);
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
					//System.out.println("Port: "+Reha.xport+" bereits belegt");
					Reha.xport++;
				}
			}
			if(Reha.xport==6020){
				JOptionPane.showMessageDialog(null, "Fehler bei der Initialisierung des IO-Servers");
				Reha.xport = -1;
				serv = null;
				return null;
			}
			System.out.println("IO-SocketServer installiert auf Port: "+Reha.xport);
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
				int byteStream;
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
				if(sb.toString().startsWith("Reha301#")){
					doReha301(String.valueOf(sb.toString()) );
					
				}else if(sb.toString().startsWith("OffenePosten#")){
					doOffenePosten(String.valueOf(sb.toString()));
					
				}else if(sb.toString().startsWith("WorkFlow#")){
					doWorkFlow(String.valueOf(sb.toString()));
					
				}else if(sb.toString().startsWith("OpRgaf#")){
					doOpRgaf(String.valueOf(sb.toString()));
					
				}else if(sb.toString().startsWith("RehaSql#")){
					doRehaSql(String.valueOf(sb.toString()));
					
				}else if(sb.toString().startsWith("RehaMail#")){
					doRehaMail(String.valueOf(sb.toString()));
					
				}else if(sb.toString().startsWith("AppName#")){
					doReversePort(sb.toString());
				}

			}
		}
	private RehaIOServer getInstance(){
		return this;
	}
	
	private void posteAktualisierePatUndRez(String patid,String reznum){
		final String xpatid = patid;
		final String xreznum = reznum;
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					String s1 = String.valueOf("#PATSUCHEN");
					String s2 = xpatid;
					PatStammEvent pEvt = new PatStammEvent(getInstance());
					pEvt.setPatStammEvent("PatSuchen");
					pEvt.setDetails(s1,s2,xreznum) ;
					PatStammEventClass.firePatStammEvent(pEvt);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}
		}.execute();
	}
	private void posteAktualisierePat(String patid){
		final String xpatid = patid;
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				String s1 = String.valueOf("#PATSUCHEN");
				String s2 = xpatid;
				PatStammEvent pEvt = new PatStammEvent(getInstance());
				pEvt.setPatStammEvent("PatSuchen");
				pEvt.setDetails(s1,s2,"") ;
				PatStammEventClass.firePatStammEvent(pEvt);		
				return null;
			}
		}.execute();
	}
	
}