package CommonTools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class FileTools {
	public static boolean deleteAllFiles(File dir){
		File[] files = dir.listFiles();
		for (int x=0;x<files.length;x++){
			////System.out.println(files[x]);
			files[x].delete();
		}
		
		return true;
	}	

	public static boolean delDirAndFile(File dir){
		if (dir.isDirectory()){
				String[] entries = dir.list();
				for (int x=0;x<entries.length;x++){
					File aktFile = new File(dir.getPath(),entries[x]);
					delDirAndFile(aktFile);
				}
				if (dir.delete())
					return true;
				else
					return false;
			}
			else{
				if (dir.delete())
					return true;
				else
					return false;
			}
	}	
	public static boolean delFileWithSuffixAndPraefix(File dir,String xpraefix,String xsuffix){
		final String suffix = xsuffix;
		final String praefix = xpraefix;
		FilenameFilter fileFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return ( name.startsWith(praefix) && name.endsWith(suffix));
			}

	    };
		File[] files = dir.listFiles(fileFilter);
		boolean ok = true;
		for (int i = 0; i < files.length; i++) {
			if(!files[i].delete()){
				ok = false;
			}
		}
		return (files.length == 0 || !ok ? false : true);
	}
	public static boolean delFileWithPraefix(File dir,String xpraefix){

		final String praefix = xpraefix;
		FilenameFilter fileFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return ( name.startsWith(praefix));
			}

	    };
		File[] files = dir.listFiles(fileFilter);
		boolean ok = true;
		for (int i = 0; i < files.length; i++) {
			if(!files[i].delete()){
				ok = false;
			}
		}
		return (files.length == 0 || !ok ? false : true);
	}

	
	
	public static ArrayList<File> searchFile(File dir, String find) {

		File[] files = dir.listFiles();
		ArrayList<File> matches = new ArrayList<File> ();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().equalsIgnoreCase(find)) { // überprüft ob der Dateiname mit dem Suchstring
										 // übereinstimmt. Groß-/Kleinschreibung wird
										 // ignoriert.
					matches.add(files[i]);
				}
				if (files[i].isDirectory()) {
					matches.addAll(searchFile(files[i], find)); // fügt der ArrayList die ArrayList mit den
										    // Treffern aus dem Unterordner hinzu
				}
			}
		}
		return matches;
	}
	
	public static byte[] File2ByteArray(File file) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        FileInputStream fileInputStream = new FileInputStream(file);
 
        byte[] buffer = new byte[16384];
 
        for (int len = fileInputStream.read(buffer); len > 0; len = fileInputStream
                .read(buffer)) {
            byteArrayOutputStream.write(buffer, 0, len);
        }
 
        fileInputStream.close();
 
        return byteArrayOutputStream.toByteArray();
    }
	public static void ByteArray2File(byte[] xdata,String fileout){
		try
		{
		  // Byte Array laden
		  byte[] data = xdata;
		  // Zu erzeugende Datei angeben
		  File f = new File(fileout);

		  // Datei schreiben
		  FileOutputStream fileOut = new FileOutputStream(f);
		  fileOut.write(data);
		  fileOut.flush();
		  fileOut.close();
		}
		catch (IOException e)
		{
		  e.printStackTrace();
		}		
	}
	
	public static void copyFile(File src, File dest, int bufSize,
	        boolean force) throws IOException {
	    if(dest.exists()) {
	        if(force) {
	            dest.delete();
	        } else {
	            throw new IOException(
	                    "Kann existierende Datei nicht üerschreiben: " + dest.getName());
	        }
	    }
	    byte[] buffer = new byte[bufSize];
	    int read = 0;
	    InputStream in = null;
	    OutputStream out = null;
	    try {
	        in = new FileInputStream(src);
	        out = new FileOutputStream(dest);
	        while(true) {
	            read = in.read(buffer);
	            if (read == -1) {
	                //-1 bedeutet EOF
	                break;
	            }
	            out.write(buffer, 0, read);
	        }
	    } finally {
	        // Sicherstellen, dass die Streams auch
	        // bei einem throw geschlossen werden.
	        // Falls in null ist, ist out auch null!
	        if (in != null) {
	            //Falls tats�chlich in.close() und out.close()
	            //Exceptions werfen, die jenige von 'out' geworfen wird.
	            try {
	            	out.flush();
	                in.close();
	                out.close();
	            }catch(Exception ex){
	            	
	            }
	            
	            finally {
	                if (out != null) {
	                    out.close();
	                }
	            }
	        }
	    }
	}
	
/*******************************************/
	public static byte[] readFileToByteArray(String originalFilePath) throws IOException {
	      
	      FileInputStream fis = new FileInputStream(originalFilePath);
	      byte[] buf = new byte[1024];
	      int numRead = 0;
	      ByteArrayOutputStream baos = new ByteArrayOutputStream();

	      while ((numRead = fis.read(buf)) > 0) {
	         baos.write(buf, 0, numRead);
	         baos.flush();
	         
	      }
	      fis.close();
	      byte[] returnVal = baos.toByteArray();
	      baos.close();
	      return returnVal.clone();
	   }	

	public static String readFileToString(String originalFilePath) throws IOException {
	      
	      FileInputStream fis = new FileInputStream(originalFilePath);
	      byte[] buf = new byte[1024];
	      int numRead = 0;
	      ByteArrayOutputStream baos = new ByteArrayOutputStream();

	      while ((numRead = fis.read(buf)) > 0) {
	         baos.write(buf, 0, numRead);
	         baos.flush();
	         
	      }
	      fis.close();
	      byte[] returnVal = baos.toByteArray();
	      baos.close();
	      return new String(returnVal);
	   }
	  public static String testeString(String webstring,String fundstelle,String sbeginn,String sende){
		  int aktuell = 0;
		  int wo = 0;

		  String meinweb = new String(webstring);
		  int lang = meinweb.length();

		  wo = webstring.indexOf(fundstelle,aktuell);
			  String nurBild  = "";
			  boolean start = false;
			  boolean austritt = false;
			  int ende = 0;
			  for(int i = wo; i < lang; i++){
				  for(int d = 0; d < 1;d++){
					  if( (meinweb.substring(i,i+1).equals(sbeginn)) && (!start)){

						  i++;
						  start = true;
						  break;
					  }
					  if( (meinweb.substring(i,i+1).equals(ende)) && (start)){
						  start = false;
						  ende = i;
						  austritt = true;
						  break;
					  }
				  }
				  if(austritt){
					  break;
				  }
				  if(start){
					  nurBild = nurBild +meinweb.substring(i,i+1);
				  }
			  }
			  return new String(nurBild);	  
	  }



}
