package theraPiUpdates;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;







public class FTPTools {
	Vector<String> datNamenFern = null;
	public static FTPTools thisClass = null; 
	public FTPClient ftpClient = null;
    //public static int BUFFER_SIZE = 1024*1024;
    public static int BUFFER_SIZE = 1024*8;
    org.apache.commons.net.ftp.FTPFile[] files;	
    
	public FTPTools(){
		ftpClient = new FTPClient();
		thisClass = this;
	}
	
	public FTPFile[] holeDatNamen(){
	    	try {
	    		ftpClient.connect(TheraPiUpdates.UpdateFTP);
	    		//ftpClient.connect("www.thera-pi.org");
				//ftpClient.getReplyString();
				System.out.println(ftpClient.getReplyString());
	    		
				ftpClient.login(TheraPiUpdates.UpdateUser, TheraPiUpdates.UpdatePasswd);
				//ftpClient.login("p8442191-pi", "AZ1704B8");
				//ftpClient.getReplyString();
				System.out.println(ftpClient.getReplyString());
				
				//ftpClient.cwd("."+TheraPiUpdates.UpdateVerzeichnis);
				ftpClient.changeWorkingDirectory("."+TheraPiUpdates.UpdateVerzeichnis);
	    		//ftpClient.changeWorkingDirectory("./HowTo");
				//ftpClient.getReplyString();
				System.out.println(ftpClient.getReplyString());
				
				//ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
				//System.out.println(ftpClient.getReplyString());
				
				ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
				System.out.println(ftpClient.getReplyString());
				
				ftpClient.setTcpNoDelay(true);
				//ftpClient.getReplyString();
				System.out.println(ftpClient.getReplyString());

				String rueck = ftpClient.getStatus();
				System.out.println("Status = :"+rueck);
				//ftpClient.setBufferSize(BUFFER_SIZE);
				/*
				if(helpFenster.thisClass.wf != null){
					helpFenster.thisClass.wf.setStand("Überprüfe Dateien");
				}
				*/

				files = ftpClient.listFiles();
				
				ftpClient.logout();
				ftpClient.disconnect();

	    	} catch (SocketException e1) {
				//JOptionPane.showMessageDialog(null, e1.getMessage());
	    	} catch (IOException e2) {
				//JOptionPane.showMessageDialog(null, e1.getMessage());    		

	    	}
		return files;  
	}
	
	/*****************************************************/
	public boolean holeDatei(String datfern,String vznah,boolean doprogress,UpdatePanel eltern,long groesse){
		if(ftpClient == null){
			return false;
		}
		if(!ftpClient.isConnected()){
			if(!nurConnect()){
				return false;				
			}

		}
		ftpClient.enterLocalPassiveMode();
		try {

    		files = ftpClient.listFiles();
    		// Untersuchen ob Datei vorhanden
    		
    		long max = -1;
    		if(groesse < 0){
        		for(int i = 0; i < files.length;i++){
        			if(files[i].getName().equals(datfern)){
        				max = files[i].getSize();
        				System.out.println(files[i].getName());
        			}
        		}
    		}else{
    			max = Long.valueOf(groesse);
    		}
    		if(max < 0){
    			ftpClient.logout();
    			ftpClient.disconnect();
    			return false;
    		}
    		
			InputStream uis = null;
			uis = ftpClient.retrieveFileStream(datfern);
			
			
			System.out.println("**********************available**********************  "+max+" Bytes");
			ftpClient.getReplyString();
			FileOutputStream fos = null;;
			fos = new FileOutputStream(vznah+datfern);

			int n = 0;
			byte[] buf = new byte[BUFFER_SIZE];
			
			int gesamt = 0;

			if(doprogress){
				eltern.pbar.setMinimum(0);
				eltern.pbar.setMaximum(Integer.parseInt(Long.toString(max)));
			}
			
			while ((n=uis.read(buf,0,buf.length))>0){
				gesamt = gesamt + n;
				fos.write(buf,0,n);
				if(doprogress){
					eltern.pbar.setValue(gesamt);
				}
			}

			//System.out.println("Datei "+datfern+" wurde erfolgreich übertragen");
			fos.flush();
			fos.close();		
			uis.close();
			fos = null;
			uis = null;
			ftpClient.getReplyString();
			//System.out.println("Nach Logout = "+reply);
			ftpClient.logout();
			ftpClient.getReplyString();
			//System.out.println("Nach Disconnect = "+reply);
			ftpClient.disconnect();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}
	
		return true;
	}
	
	/*****************************************************/
	public String holeLogDateiSilent(String datfern){
		if(ftpClient == null){
			return "Fehler im Bezug der Log-Datei";
		}
		if(!ftpClient.isConnected()){
			if(!nurConnect()){
				return "Fehler im Bezug der Log-Datei";				
			}
		}
		ftpClient.enterLocalPassiveMode();
		try {
			InputStream uis = null;
			uis = ftpClient.retrieveFileStream(datfern);
			ftpClient.getReplyString();
			String ret = convertStreamToString(uis);
			uis.close();
			uis = null;
			ftpClient.getReplyString();
			//System.out.println("Nach Logout = "+reply);
			ftpClient.logout();
			ftpClient.getReplyString();
			//System.out.println("Nach Disconnect = "+reply);
			ftpClient.disconnect();
			return String.valueOf(ret);
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Fehler im Bezug der Log-Datei";
	}
	
	public boolean ftpTransferString(String datfern,String string,JProgressBar jprog ){
		boolean ret = false;
		if(ftpClient == null){
			System.out.println("ftpClient = null");
			return false;
		}
		if(!ftpClient.isConnected()){
			System.out.println("nicht connected");
			if(!nurConnect()){
				return false;				
			}
			System.out.println("connected");

		}

		String sreply;
		try {
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ftpClient.enterLocalPassiveMode();
		try {
    			ftpClient.deleteFile(datfern);
				InputStream ins = null;
				try {
					ins = convertStringToStream(ins,string);
				} catch (Exception e) {
					e.printStackTrace();
				}
				ftpClient.enterLocalPassiveMode();
			
				/*********************************/
			
				ftpClient.setSendBufferSize(1024*8);
				System.out.println(ftpClient.getReplyString());
				
				OutputStream fos = ftpClient.storeFileStream(datfern);
				if(!FTPReply.isPositiveIntermediate(ftpClient.getReplyCode())) {
	     			//ins.close();
	     			//fos.close();
	     			//ftpClient.logout();
	     			//ftpClient.disconnect();
	     			//System.err.println("Scheiß-Transfer fehlgeschlagen");
	     			//System.out.println("Datei = "+quelldat);
	     			
	 			}
			
				int n = 0;
				byte[] buf = new byte[1024*8];
				
				int gesamt = 0;
				
				boolean progresszeigen = false;
				final JProgressBar xprog = jprog;
				final int xgross = string.length();
				if(	jprog != null){
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							xprog.setStringPainted(true);
							xprog.setMinimum(0);
							xprog.setMaximum(xgross);
							xprog.repaint();
						}
					});
					progresszeigen = true;
				}

			
			/*****************************************/

				while ((n=ins.read(buf,0,buf.length))>0){
					try{
						gesamt = gesamt + n;
						fos.write(buf,0,n);
						
						if(progresszeigen){
							final int xgesamt = gesamt;
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									xprog.setValue(xgesamt);
									xprog.repaint();
								}
							});	
						}
						
					}catch(Exception ex){
						ex.printStackTrace();
					}
					
				}
				System.out.println("Datei "+datfern+" auf Server geschrieben mit "+gesamt+ " Bytes");
			
				fos.flush();
				fos.close();
				ins.close();
				System.out.println(ftpClient.getReplyString());
				if(!ftpClient.completePendingCommand()) {
					ftpClient.logout();
					ftpClient.disconnect();
					JOptionPane.showMessageDialog(null, "Die Puffer des belämmerten FTP's konnten nicht vollständig geschrieben werden!!!!(Ich krieg die Krise)");
					System.err.println("Die Puffer des belämmerten FTP's konnten nicht vollständig geschrieben werden!!!!(Ich krieg die Krise)");
				 }
				System.out.println(ftpClient.getReplyString());			
				ins = null;
				fos = null;
				if(progresszeigen){
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							xprog.setValue(0);
							xprog.repaint();
						}
					});	
				}

			/*********************************/
				String [] replies = ftpClient.getReplyStrings();
				//ftpClient.appendFile(remote, local)
				ftpClient.logout();
				System.out.println(ftpClient.getReplyString());
				ftpClient.disconnect();


			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return false;
			}
		
			return true;
	}
	/********************************************************/
	
	
	 public static String convertStreamToString(InputStream is) throws Exception {
		    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		    StringBuilder sb = new StringBuilder();
		    String line = null;
		    while ((line = reader.readLine()) != null) {
		      sb.append(line + "\n");
		    }
		    is.close();
		    return sb.toString();
	}
	 public static InputStream convertStringToStream(InputStream ins,String string) throws Exception {
		 		ins = new ByteArrayInputStream(string.getBytes());
		    return ins;
	}
	
	private boolean nurConnect(){
		try {
			ftpClient.connect(TheraPiUpdates.UpdateFTP);
			ftpClient.getReplyString();
			//System.out.println(ftpClient.getReplyString());
			ftpClient.login(TheraPiUpdates.UpdateUser, TheraPiUpdates.UpdatePasswd);
			ftpClient.getReplyString();
			//System.out.println(ftpClient.getReplyString());
			//ftpClient.cwd("."+TheraPiUpdates.UpdateVerzeichnis);
			ftpClient.changeWorkingDirectory("."+TheraPiUpdates.UpdateVerzeichnis);
			//ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			ftpClient.getReplyString();
			//System.out.println(ftpClient.getReplyString());

		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/*****************************************************/
	public boolean holeDateiSilent(String datfern,String vznah,boolean doprogress){
		if(ftpClient == null){
			return false;
		}
		if(!ftpClient.isConnected()){
			if(!nurConnect()){
				return false;				
			}

		}
		ftpClient.enterLocalPassiveMode();
		try {

    		files = ftpClient.listFiles();
    		// Untersuchen ob Datei vorhanden
    		

    		
			InputStream uis = null;
			uis = ftpClient.retrieveFileStream(datfern);
			
			

			ftpClient.getReplyString();
			FileOutputStream fos = null;;
			fos = new FileOutputStream(vznah+datfern);

			int n = 0;
			byte[] buf = new byte[BUFFER_SIZE];
			
			int gesamt = 0;

			
			while ((n=uis.read(buf,0,buf.length))>0){
				gesamt = gesamt + n;
				fos.write(buf,0,n);
			}

			//System.out.println("Datei "+datfern+" wurde erfolgreich übertragen");
			fos.flush();
			fos.close();		
			uis.close();
			fos = null;
			uis = null;
			ftpClient.getReplyString();
			//System.out.println("Nach Logout = "+reply);
			ftpClient.logout();
			ftpClient.getReplyString();
			//System.out.println("Nach Disconnect = "+reply);
			ftpClient.disconnect();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}
	
		return true;
	}
	/********************************************************/
	public boolean ftpTransferDatei(String datfern,String quelldat,long groesse,JProgressBar jprog ){
		boolean ret = false;
		if(ftpClient == null){
			System.out.println("ftpClient = null");
			return false;
		}
		if(!ftpClient.isConnected()){
			System.out.println("nicht connected");
			if(!nurConnect()){
				return false;				
			}
			System.out.println("connected");

		}

		String sreply;
		try {
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ftpClient.enterLocalPassiveMode();
		try {
    		//files = ftpClient.listFiles();

    		
			ftpClient.deleteFile(datfern);
			//ftpClient.sendCommand("OPTS LATIN1 ON");



			
			File src = new File(quelldat);
			InputStream ins = new FileInputStream(src);

			ftpClient.enterLocalPassiveMode();
			
			/*
			ftpClient.storeFile(datfern, ins);
			ins.close();
			ins = null;
			*/
			


			
			/*********************************/
			
			ftpClient.setSendBufferSize(1024*8);
			System.out.println(ftpClient.getReplyString());
			
			OutputStream fos = ftpClient.storeFileStream(datfern);
			if(!FTPReply.isPositiveIntermediate(ftpClient.getReplyCode())) {
     			//ins.close();
     			//fos.close();
     			//ftpClient.logout();
     			//ftpClient.disconnect();
     			System.err.println("Scheiß-Transfer fehlgeschlagen");
     			System.out.println("Datei = "+quelldat);
     			
 			}
			
			int n = 0;
			byte[] buf = new byte[1024*8];
			
			int gesamt = 0;
			
			boolean progresszeigen = false;
			final JProgressBar xprog = jprog;
			final long xgross = groesse;
			if(	jprog != null){
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						xprog.setStringPainted(true);
						xprog.setMinimum(0);
						xprog.setMaximum(new Long(xgross).intValue());
						xprog.repaint();
					}
				});
				progresszeigen = true;
			}

			
			/*****************************************/

			while ((n=ins.read(buf,0,buf.length))>0){
				try{
				gesamt = gesamt + n;
				fos.write(buf,0,n);
				
				
				if(progresszeigen){
					final int xgesamt = gesamt;
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							xprog.setValue(xgesamt);
							xprog.repaint();
						}
					});	
				}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
			}
			System.out.println("Datei "+datfern+" auf Server geschrieben mit "+gesamt+ "Bytes");
			
			fos.flush();
			fos.close();
			ins.close();
			System.out.println(ftpClient.getReplyString());
			if(!ftpClient.completePendingCommand()) {
				ftpClient.logout();
				ftpClient.disconnect();
				JOptionPane.showMessageDialog(null, "Die Puffer des belämmerten FTP's konnten nicht vollständig geschrieben werden!!!!(Ich krieg die Krise)");
				System.err.println("Die Puffer des belämmerten FTP's konnten nicht vollständig geschrieben werden!!!!(Ich krieg die Krise)");
			 }
			System.out.println(ftpClient.getReplyString());			
			ins = null;
			fos = null;
			if(progresszeigen){
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						xprog.setValue(0);
						xprog.repaint();
					}
				});	
			}
			

			/*********************************/
			
			String [] replies = ftpClient.getReplyStrings();
			//ftpClient.appendFile(remote, local)
			ftpClient.logout();
			System.out.println(ftpClient.getReplyString());
			ftpClient.disconnect();


		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}
	
		return true;
	}
	/********************************************************/
	/********************************************************/
	public boolean ftpTransferByteArray(String datfern,byte[] b,long groesse,JProgressBar jprog ){

		 
		boolean ret = false;
		if(ftpClient == null){
			System.out.println("ftpClient = null");
			return false;
		}
		if(!ftpClient.isConnected()){
			System.out.println("nicht connected");
			if(!nurConnect()){
				return false;				
			}
			System.out.println("connected");

		}

		String sreply;
		try {
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ftpClient.enterLocalPassiveMode();
		try {
    		//files = ftpClient.listFiles();

    		
			ftpClient.deleteFile(datfern);
			//ftpClient.sendCommand("OPTS LATIN1 ON");



			
			//File src = new File(quelldat);
			InputStream ins = new ByteArrayInputStream( b);

			ftpClient.enterLocalPassiveMode();
			
			/*
			ftpClient.storeFile(datfern, ins);
			ins.close();
			ins = null;
			*/
			


			
			/*********************************/
			
			ftpClient.setSendBufferSize(1024);
			System.out.println(ftpClient.getReplyString());
			
			OutputStream fos = ftpClient.storeFileStream(datfern);

			if(!FTPReply.isPositiveIntermediate(ftpClient.getReplyCode())) {
     			System.err.println("Scheiß-Transfer fehlgeschlagen");
     			System.out.println("Datei = "+datfern);
 			}
			
			int n = 0;
			byte[] buf = new byte[1024];
			
			int gesamt = 0;
			
			boolean progresszeigen = false;
			final JProgressBar xprog = jprog;
			final long xgross = groesse;
			if(	jprog != null){
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						xprog.setStringPainted(true);
						xprog.setMinimum(0);
						xprog.setMaximum(new Long(xgross).intValue());
						xprog.repaint();
					}
				});
				progresszeigen = true;
			}

			
			/*****************************************/

			while ((n=ins.read(buf,0,buf.length))>0){
				try{
				gesamt = gesamt + n;
				fos.write(buf,0,n);
				
				
				if(progresszeigen){
					final int xgesamt = gesamt;
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							xprog.setValue(xgesamt);
							xprog.repaint();
						}
					});	
				}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
			}
			System.out.println("Datei "+datfern+" auf Server geschrieben mit "+gesamt+ "Bytes");
			
			fos.flush();
			fos.close();
			ins.close();
			System.out.println(ftpClient.getReplyString());
			if(!ftpClient.completePendingCommand()) {
				ftpClient.logout();
				ftpClient.disconnect();
				JOptionPane.showMessageDialog(null, "Die Puffer des belämmerten FTP's konnten nicht vollständig geschrieben werden!!!!(Ich krieg die Krise)");
				System.err.println("Die Puffer des belämmerten FTP's konnten nicht vollständig geschrieben werden!!!!(Ich krieg die Krise)");
			 }
			System.out.println(ftpClient.getReplyString());			
			ins = null;
			fos = null;
			if(progresszeigen){
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						xprog.setValue(0);
						xprog.repaint();
					}
				});	
			}
			

			/*********************************/
			
			String [] replies = ftpClient.getReplyStrings();
			//ftpClient.appendFile(remote, local)
			ftpClient.logout();
			System.out.println(ftpClient.getReplyString());
			ftpClient.disconnect();


		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}
	
		return true;
	}
	/********************************************************/
	
	/*
	private boolean nurConnect(){
		try {
			ftpClient.connect(TheraPiUpdates.HilfeFTP);
			ftpClient.getReplyString();
			//System.out.println(ftpClient.getReplyString());
			ftpClient.login(TheraPiUpdates.HilfeUser, TheraPiUpdates.HilfePasswd);
			ftpClient.getReplyString();
			//System.out.println(ftpClient.getReplyString());
			ftpClient.cwd("./updates");
			ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			ftpClient.getReplyString();
			//System.out.println(ftpClient.getReplyString());

		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	*/
	
	

}
