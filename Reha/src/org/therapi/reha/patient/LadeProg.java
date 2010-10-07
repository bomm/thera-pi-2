package org.therapi.reha.patient;

import hauptFenster.Reha;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

import org.jdesktop.swingworker.SwingWorker;

public class LadeProg {
	public LadeProg(String prog){
		
		String progname= null;
		if(prog.indexOf(" ")>=0){
			progname = prog.split(" ")[0];
		}else{
			progname = prog;
		}
		File f = new File(progname);
		if(! f.exists()){
			JOptionPane.showMessageDialog(null,"Diese Software ist auf Ihrem System nicht installiert!");
			return;
		}
		Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
		//String vmload = "java -jar ";
		//String commandx = vmload + prog; 

		final String xprog = prog;
		new SwingWorker<Void, Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				try {
					List<String>list = Arrays.asList(xprog.split(" "));
					ArrayList<String> alist = new ArrayList<String>(list);
					alist.add(0,"-jar");
					alist.add(0,"java");
					Process process = new ProcessBuilder(alist).start();
					Reha.thisFrame.setCursor(Reha.thisClass.normalCursor);
				       InputStream is = process.getInputStream();
				       InputStreamReader isr = new InputStreamReader(is);
				       BufferedReader br = new BufferedReader(isr);
				       String line;
				       while ((line = br.readLine()) != null) {
				         System.out.println(line);
				       }
				       is.close();
				       isr.close();
				       br.close();


				} catch (IOException e) {
					Reha.thisFrame.setCursor(Reha.thisClass.normalCursor);
					e.printStackTrace();
				}
				return null;
			}
		}.execute();
	}
}	
