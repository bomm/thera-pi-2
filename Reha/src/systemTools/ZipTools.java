package systemTools;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
public class ZipTools {

	  public static void scanZipFile(String zipname) {
	    try {
	      ZipInputStream zin = new ZipInputStream(
	          new FileInputStream(zipname));
	      ZipEntry entry;
	      while ((entry = zin.getNextEntry()) != null) {
	        //System.out.println(entry.getName());
	        zin.closeEntry();
	      }
	      zin.close();
	    } catch (IOException e) {
	    }
	  }

	  public static void loadZipFile(String zipname, String name) {
	    try {
	      ZipInputStream zin = new ZipInputStream(
	          new FileInputStream(zipname));
	      ZipEntry entry;
	      //System.out.println("");
	      while ((entry = zin.getNextEntry()) != null) {
	        if (entry.getName().equals(name)) {
	          BufferedReader in = new BufferedReader(
	              new InputStreamReader(zin));
	          String s;
	          
	          //while ((s = in.readLine()) != null)
	            //System.out.println(s + "\n");
	        }
	        zin.closeEntry();
	      }
	      zin.close();
	    } catch (IOException e) {
	    }
	  }
}	  