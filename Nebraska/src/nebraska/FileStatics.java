package nebraska;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;

import javax.swing.JFileChooser;

public class FileStatics {
	/*************************************************************************************/	
	 public static byte[] BytesFromFile(File file) throws IOException {
	        InputStream is = new FileInputStream(file);
	        long length = file.length();
	    
	        if (length > Integer.MAX_VALUE) {
	      System.out.println("Sorry! Your given file is too large.");
	      System.exit(0);
	        }

	        byte[] bytes = new byte[(int)length];
	        int offset = 0;
	        int numRead = 0;
	        while (offset < bytes.length && (numRead=is.read(bytes, 
	                    offset, bytes.length-offset)) >= 0) {
	            offset += numRead;
	        }
	        if (offset < bytes.length) {
	            throw new IOException("Could not completely read file "
	                                + file.getName());
	        }
	        is.close();
	        return bytes;
	    }
	 	public static void BytesToFile(byte[] xdata,File fileout){
			try
			{
			  // Byte Array laden
			  byte[] data = xdata;
			  // Zu erzeugende Datei angeben
			  // Datei schreiben
			  FileOutputStream fileOut = new FileOutputStream(fileout);
			  fileOut.write(data);
			  fileOut.flush();
			  fileOut.close();
			}
			catch (IOException e)
			{
			  e.printStackTrace();
			}		
		}
	 	public static void SomeBytesToFile(byte[] xdata,int from,int to,File fileout){
			try
			{
			  // Byte Array laden
			  //byte[] data = xdata;
			  byte[] data = new byte[to - from];
			  for(int i = from; i < to;i++){
				data[i] = xdata[i];  
			  }
			  // Zu erzeugende Datei angeben
			  // Datei schreiben
			  FileOutputStream fileOut = new FileOutputStream(fileout);
			  fileOut.write(data);
			  fileOut.flush();
			  fileOut.close();
			}
			catch (IOException e)
			{
			  e.printStackTrace();
			}		
		}

	 	public static byte[] TransferByteArray(byte[] source,int from,int to){
	 		
			try
			{
			  // Byte Array laden
			  //byte[] data = xdata;
			  byte[] target = new byte[to - from];
			  for(int i = from; i < to;i++){
				target[i] = source[i];  
			  }
			  return target;
			}catch(Exception ex){
				
			}
			return null;
		}
	 	
	 	public static byte[] inputStreamToBytes(InputStream in) throws IOException {

	 		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
	 		byte[] buffer = new byte[1024];
	 		int len;

	 		while((len = in.read(buffer)) >= 0)
	 		out.write(buffer, 0, len);
	 		out.flush();
	 		in.close();
	 		out.close();
	 		return out.toByteArray();
	 		} 	 	
	 public static void certToFile(X509Certificate x509Cert,byte[] b,String name) throws IOException{
			//String name = Nebraska.keystoredir + File.separator +System.currentTimeMillis();
			File f = new File(NebraskaTestPanel.keystoreDir + File.separator +name+".p7b");
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(b);
			fos.flush();
			fos.close();
			f =  new File(name+".pem");
			fos = new FileOutputStream(f);
			fos.write(x509Cert.toString().getBytes());
			fos.flush();
			fos.close();
		 
	 }
	 
	 public static String fileChooser(String pfad,String titel){
			//String pfad = "C:/Lost+Found/verschluesselung/";
			String sret = "";
			final JFileChooser chooser = new JFileChooser(pfad);
	        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
	        chooser.setDialogTitle(titel);
	        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	        final File file = new File(pfad);

	        chooser.setCurrentDirectory(file);

	        chooser.addPropertyChangeListener(new PropertyChangeListener() {
	            public void propertyChange(PropertyChangeEvent e) {
	                if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)
	                        || e.getPropertyName().equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
	                    // final File f = (File) e.getNewValue();
	                }
	            }
	        });
	        chooser.setVisible(true);
	        final int result = chooser.showOpenDialog(null);

	        if (result == JFileChooser.APPROVE_OPTION) {
	            File inputVerzFile = chooser.getSelectedFile();
	            if(inputVerzFile.getName().trim().equals("")){
	            	sret = "";
	            }else{
	            	sret = inputVerzFile.getName().trim();	
	            }
	        }else{
	        	sret = "";
	        }
	        return sret;
	 }

	 public static String dirChooser(String pfad,String titel){
			//String pfad = "C:/Lost+Found/verschluesselung/";
			String sret = "";
			final JFileChooser chooser = new JFileChooser(pfad);
	        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
	        chooser.setDialogTitle(titel);
	        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	        final File file = new File(pfad);

	        chooser.setCurrentDirectory(file);

	        chooser.addPropertyChangeListener(new PropertyChangeListener() {
	            public void propertyChange(PropertyChangeEvent e) {
	                if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)
	                        || e.getPropertyName().equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
	                    // final File f = (File) e.getNewValue();
	                }
	            }
	        });
	        chooser.setVisible(true);
	        final int result = chooser.showOpenDialog(null);

	        if (result == JFileChooser.APPROVE_OPTION) {
	            File inputVerzFile = chooser.getSelectedFile();
	            if(inputVerzFile.getName().trim().equals("")){
	            	sret = "";
	            }else{
	            	sret = inputVerzFile.getAbsolutePath().trim();	
	            }
	        }else{
	        	sret = "";
	        }
	        return sret;
	 }
	

}
