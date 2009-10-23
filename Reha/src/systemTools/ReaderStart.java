package systemTools;

import hauptFenster.Reha;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import systemEinstellungen.SystemConfig;

public class ReaderStart{
	public ReaderStart(String datei ){
		final String xdatei = datei;
		new Thread(){
			public void run(){
				Process process;
				try {
					process = new ProcessBuilder(SystemConfig.hmFremdProgs.get("AcrobatReader"),"",xdatei).start();
					InputStream is = process.getInputStream();
					
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					String line;
					Reha.thisClass.progressStarten(false);							       
					while ((line = br.readLine()) != null) {
					     System.out.println("Lade Adobe "+line);
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