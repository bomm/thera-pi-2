package FileTools;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class FileTools {
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
				  ////System.out.println(meinweb.substring(i,i+1));
				  
			  }
			  ////System.out.println("Nur das Bild = -> "+meinweb.substring(wo,ende));
			  ////System.out.println("Nur das Bild = -> "+nurBild);
			 // aktuell = new Integer(ende);
		  
			  return new String(nurBild);	  
	  }
	
}
