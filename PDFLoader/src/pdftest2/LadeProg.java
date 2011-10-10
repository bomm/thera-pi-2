package pdftest2;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;

import org.jdesktop.swingworker.SwingWorker;


public class LadeProg {
	public static boolean geladen = false;	
	public LadeProg(String prog){
		final String xprog = prog;
		System.out.println(prog);
		new SwingWorker<Void, Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				Process process;
				try {
					process = new ProcessBuilder(Rahmen.reader,"",xprog).start();
					InputStream is = process.getInputStream();
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					String line;

					while ((line = br.readLine()) != null) {
						//System.out.println(line);
					}
					is.close();
					isr.close();
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					System.out.println("PDFLoader wird beendet");
					Rahmen.thisClass.conn.close();
					System.exit(0);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return null;
			}		
				
		}.execute();
	}
}	
