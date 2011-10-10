package ftpTools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import therapiHilfe.piHelp;






public class FTPTools {
	Vector<String> datNamenFern = null;
	public static FTPTools thisClass = null; 
	public FTPClient ftpClient = null;
    public static int BUFFER_SIZE = 1024*1024;
    org.apache.commons.net.ftp.FTPFile[] files;	
	
	public FTPTools(){
		ftpClient = new FTPClient();
		thisClass = this;
	}
	public static FTPTools getInstance(){
		return thisClass;
	}
	
	public Vector<String> holeDatNamen(){
		datNamenFern = new Vector<String>();
	    	try {
	    		ftpClient.connect(piHelp.hilfeftp);
	    		//ftpClient.connect("www.thera-pi.org");
				String sreply = ftpClient.getReplyString();
				//System.out.println("Connect-Reply = "+sreply);
	    		
				ftpClient.login(piHelp.hilfeuser, piHelp.hilfepasswd);
				//ftpClient.login("p8442191-pi", "AZ1704B8");
				sreply = ftpClient.getReplyString();
				//System.out.println("Login-Reply = "+sreply);
	    		
	    		//ftpClient.changeWorkingDirectory("./HowTo");
				sreply = ftpClient.getReplyString();
				//System.out.println("ChDir-Reply = "+sreply);

				
				//ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
				ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

				ftpClient.setTcpNoDelay(true);
				sreply = ftpClient.getReplyString();
				//System.out.println("SetBufferSize = "+sreply);

				String rueck = ftpClient.getStatus();
				//System.out.println("Status = :"+rueck);
				//ftpClient.setBufferSize(BUFFER_SIZE);
				/*
				if(helpFenster.thisClass.wf != null){
					helpFenster.thisClass.wf.setStand("Überprüfe Dateien");
				}
				*/

				files = ftpClient.listFiles();
				Vector<String> vec = new Vector<String>();
				
				for(int i = 0; i< files.length;i++){
						datNamenFern.add(files[i].getName());
				}
				
				ftpClient.logout();
				ftpClient.disconnect();

	    	} catch (SocketException e1) {
	    		// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(null, e1.getMessage());
	    	} catch (IOException e1) {
	    		// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(null, e1.getMessage());    		

	    	}
		return datNamenFern;  
	}
	private boolean nurConnect(){
		try {
			/*ftpClient.connect("www.thera-pi.org");
			ftpClient.login("p8442191-pi", "AZ1704B8");*/
			ftpClient.connect(piHelp.hilfeftp);
			//System.out.println(ftpClient.getReplyString());
			ftpClient.login(piHelp.hilfeuser, piHelp.hilfepasswd);
			//System.out.println(ftpClient.getReplyString());

			//ftpClient.changeWorkingDirectory("./HowTo");
			////System.out.println("ChDir-Reply = "+sreply);
			//ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			//System.out.println(ftpClient.getReplyString());

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	private boolean nurLogin(){
		try {
			ftpClient.login("p8442191-pi", "AZ1704B8");
			String sreply = ftpClient.getReplyString();
			//System.out.println("Login-Reply = "+sreply);
			//ftpClient.changeWorkingDirectory("./HowTo");
			//sreply = ftpClient.getReplyString();
			////System.out.println("ChDir-Reply = "+sreply);
			ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
			//System.out.println("Login-Reply = "+sreply);
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			//System.out.println("Login-Reply = "+sreply);

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	
/******************************************************************************/
	public boolean holeDatei(String datfern,String datnah,long vgroesse,JProgressBar jprog){
		boolean ret = false;
		long dgroesse = 0;
		if(ftpClient == null){
			System.out.println("Funktion holeDatei: ftpClient = null");
			return false;
		}
		if(!ftpClient.isConnected()){
			if(!nurConnect()){
				System.out.println("Funktion holeDatei: kann nicht connecten");
				return false;				
			}
		}
		dgroesse = vgroesse;
		String sreply;
		try {
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ftpClient.enterLocalPassiveMode();
		
		try {
    		files = ftpClient.listFiles();
    		//System.out.println(ftpClient.getReplyString());

			if(dgroesse==0){
//	    		files = ftpClient.listFiles();
//	    		//System.out.println(ftpClient.getReplyString());
				for(int i = 0; i< files.length;i++){
					////System.out.println(files[i].getName()+" <--> "+datfern);
					if(files[i].getName().equals(datfern)){
						dgroesse = files[i].getSize();
						break;
					}
				}
				//System.out.println("Dateigröße in Bytes = "+dgroesse);
			}
			boolean gefunden = false;
			for(int i = 0; i< files.length;i++){

				if(files[i].getName().equals(datfern)){
					gefunden = true;
					break;
				}
			}
			if(!gefunden){
				ftpClient.logout();
				//System.out.println(ftpClient.getReplyString());
				ftpClient.disconnect();
				//System.out.println(ftpClient.getReplyString());
				return false;
			}
			InputStream uis = null;
			uis = ftpClient.retrieveFileStream(datfern);
			String reply = ftpClient.getReplyString();
			//System.out.println("Nach Retrive Stream------>"+reply);
			//System.out.println("Pfadangabe -> C:/RehaVerwaltung/HowTo/"+datfern);
			FileOutputStream fos = null;;
			//fos = new FileOutputStream("C:/RehaVerwaltung/HowTo/"+datfern);
			fos = new FileOutputStream(piHelp.tempvz+datfern);
			int n = 0;
			//byte[] buf = new byte[512];
			//byte[] buf = new byte[4096];
			//byte[] buf = new byte[8192];
			byte[] buf = new byte[BUFFER_SIZE];
			
			int gesamt = 0;
			final JProgressBar xprog = jprog;
			final long xgross = dgroesse;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					xprog.setMinimum(0);
					xprog.setMaximum(new Long(xgross).intValue());
					xprog.repaint();
				}
			});				

			while ((n=uis.read(buf,0,buf.length))>0){
				gesamt = gesamt + n;
				fos.write(buf,0,n);
				////System.out.println("Abgeholt in Bytes = "+gesamt);
				final int xgesamt = gesamt;
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						xprog.setValue(xgesamt);
						xprog.repaint();
					}
				});	
			}
			System.out.println("Datei "+datfern+" abgeholt mit "+gesamt+ "Bytes");
			final int xgesamt = gesamt;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					xprog.setValue(xgesamt);
					xprog.repaint();
				}
			});	
			
			fos.flush();
			fos.close();		
			uis.close();
			fos = null;
			uis = null;
			String [] replies = ftpClient.getReplyStrings();
			//ftpClient.appendFile(remote, local)
			
			ftpClient.logout();
			reply = ftpClient.getReplyString();
			//System.out.println("Nach Logout = "+reply);
			ftpClient.disconnect();
			reply = ftpClient.getReplyString();
			//System.out.println("Nach Disconnect = "+reply);

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}
	
		return true;
	}

/******************************************************************************/
	public boolean schreibeDatei(String datfern,String datnah,long groesse,JProgressBar jprog ){
		boolean ret = false;
		if(ftpClient == null){
			System.out.println("Fehler ftpClient = null");
			return false;
		}
		if(!ftpClient.isConnected()){
			if(!nurConnect()){
				System.out.println("Fehler ftpClient ist nicht Connected");				
				return false;				
			}
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
    		//System.out.println(ftpClient.getReplyString());
    		/*
    		for(int i = 0; i< files.length;i++){
				if(files[i].getName().equals(datfern)){
					ftpClient.deleteFile(datfern);
					//System.out.println(ftpClient.getReplyString());
					break;
				}
			}
			*/
    		
			ftpClient.deleteFile(datfern);
			//ftpClient.sendCommand("OPTS LATIN1 ON");
			System.out.println(ftpClient.getReplyString());

			ftpClient.enterLocalPassiveMode();

			ftpClient.setSendBufferSize(1024*8);
			System.out.println(ftpClient.getReplyString());

			////System.out.println(ftpClient.getReplyString());

			System.out.println(ftpClient.getReplyString());
			//System.out.println("Öffne Datei: "+piHelp.tempvz+URLDecoder.decode(datnah,"UTF-8"));
			File src = new File(piHelp.tempvz+URLDecoder.decode(datnah,"UTF-8"));
			InputStream ins = new FileInputStream(src);
			//System.out.println("Ungefähr verfügbare Bytes: "+ins.available());

			ftpClient.enterLocalPassiveMode();
			////System.out.println(ftpClient.getReplyString());
			OutputStream uos = null;
			String encoded = URLEncoder.encode(datfern,"UTF-8");
			//System.out.println("UTF-8 = "+encoded);
			////System.out.println("windows-1251 = "+URLEncoder.encode(datfern,"windows-1252"));
			String fertig = encoded.replace("+", "%20");
			//System.out.println("Fertig encoded = "+fertig);
			
			/*
			ftpClient.storeFile(datfern, ins);
			ins.close();
			ins = null;
			uos = null;
			*/
			

			
			/*********************************/
			if(!FTPReply.isPositiveIntermediate(ftpClient.getReplyCode())) {
     			//ins.close();
     			//fos.close();
     			//ftpClient.logout();
     			//ftpClient.disconnect();
     			//System.err.println("Scheiß-Transfer fehlgeschlagen");
     			System.out.println("Datei = "+datfern);
     			
 			}


			OutputStream fos = ftpClient.storeFileStream(datfern);
			
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

			

			while ((n=ins.read(buf,0,buf.length))>0){
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
			}
			System.out.println("Datei "+datfern+" auf Server geschrieben mit "+gesamt+ "Bytes");
			if(progresszeigen){
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						xprog.setValue(0);
						xprog.repaint();
					}
				});	
			}
			fos.flush();
			fos.close();
			ins.close();
			System.out.println(ftpClient.getReplyString());
			if(!ftpClient.completePendingCommand()) {
				ftpClient.logout();
				ftpClient.disconnect();
				System.err.println("Die Puffer des belämmerten FTP's konnten nicht vollständig geschrieben werden!!!!(Ich krieg die Krise)");
			 }
			System.out.println(ftpClient.getReplyString());			
			
			ins = null;
			fos = null;

			/*********************************/
			
			String [] replies = ftpClient.getReplyStrings();
			//ftpClient.appendFile(remote, local)
			System.out.println(ftpClient.getReplyString());
			ftpClient.logout();
			
			ftpClient.disconnect();


		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
	
		return true;
	}
/******************************************************************************/	
	public boolean ftpTransferX(String datfern,String quelldat,long groesse,JProgressBar jprog ){
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
     			//System.err.println("Scheiß-Transfer fehlgeschlagen");
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
	
/******************************************************************************/	
	public void disconnect(){
		if(ftpClient != null){
			try {
				ftpClient.logout();
				String reply = ftpClient.getReplyString();
				//System.out.println("Nach Logout = "+reply);
				ftpClient.disconnect();
				reply = ftpClient.getReplyString();
				//System.out.println("Nach Disconnect = "+reply);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}
/******************************************************************************/	
}
