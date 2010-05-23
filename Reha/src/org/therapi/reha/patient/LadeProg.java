package org.therapi.reha.patient;

import hauptFenster.Reha;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
		String vmload = "java -jar ";
		String commandx = vmload + prog; 
		////System.out.println(vmload+prog);
		/*
	    File ausgabedatei = new File(Reha.proghome+"laden.bat"); 
	    FileWriter fw;
		try {
			fw = new FileWriter(ausgabedatei);
		    BufferedWriter bw = new BufferedWriter(fw); 
		    bw.write(commandx); 
		    bw.close(); 
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		*/
		final String xprog = prog;
		new SwingWorker<Void, Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				// TODO Auto-generated method stub
				try {
					List<String>list = Arrays.asList(xprog.split(" "));
					ArrayList<String> alist = new ArrayList<String>(list);
					alist.add(0,"-jar");
					alist.add(0,"java");
					////System.out.println(list);
					////System.out.println("Die Liste = "+alist);
					
					////System.out.println("Starte Prozess mit "+xprog);
					////System.out.println("Liste = "+list);
					Process process = new ProcessBuilder(alist).start();
					//Process process = new ProcessBuilder("java","-jar",xprog).start();
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			
		}.execute();
	}
}	
