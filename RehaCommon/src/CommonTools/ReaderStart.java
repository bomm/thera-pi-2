package CommonTools;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;



public class ReaderStart{
	public ReaderStart(String datei,String pathToReader ){
		final String xdatei = datei;
		final String xpathToReader = pathToReader;
		new Thread(){
			public void run(){
				Process process;
				try {
					process = new ProcessBuilder(xpathToReader,"",xdatei).start();
					InputStream is = process.getInputStream();
					
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					//String line;
												       
					while ((br.readLine()) != null) {
					     //System.out.println("Lade Adobe "+line);
					}
					is.close();
					isr.close();
					br.close();
					is = null;
					br = null;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}
	
}