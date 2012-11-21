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

import events.PatStammEvent;
import events.PatStammEventClass;

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
		//Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
		//String vmload = "java -jar ";
		//String commandx = vmload + prog;
		final String xprog = prog;
		new Thread(){
			public void run(){
				new SwingWorker<Void, Void>(){

					@Override
					protected Void doInBackground() throws Exception {
						//String cmd = "java -jar "+xprog;
						//Runtime.getRuntime().exec(cmd);

						try {
							
							
							
							List<String>list = Arrays.asList(xprog.split(" "));
							ArrayList<String> alist = new ArrayList<String>(list);
							alist.add(0,"-jar");
							alist.add(0,"java");
							Process process = new ProcessBuilder(alist).start();
							
							//Reha.thisFrame.setCursor(Reha.thisClass.normalCursor);
						       InputStream is = process.getInputStream();
						       InputStreamReader isr = new InputStreamReader(is);
						       BufferedReader br = new BufferedReader(isr);
						       String line;
						       
						       while ((line = br.readLine()) != null) {
						         //doTestLine(line);
						       }
						       
						       is.close();
						       isr.close();
						       br.close();
						       //System.out.println("Process.beendet");
						       process = null;
						    
						     
						} catch (IOException e) {
							Reha.thisFrame.setCursor(Reha.thisClass.normalCursor);
							e.printStackTrace();
						}

						return null;
					}
				}.execute();
				
			}
		}.start();

	}
	public void doTestLine(String line){
		final String xline = line;
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				//Nachricht aus dem 301-er-Modul
				if(xline.startsWith("#AktualisierePat@")){
					String[] befehle = xline.split("@");
					if(Reha.thisClass.patpanel != null){
						try{
							if(Reha.thisClass.patpanel.aktPatID.equals(befehle[1])){
								befehlAbfeuern("#PATSUCHEN",befehle[1],befehle[2]);								
							}
						}catch(Exception ex){};
					}
				}
				
				
				return null;
			}
			
		}.execute();
	}
	public void befehlAbfeuern(String befehl,String detail1,String detail2){
		String[] sEventDetails ={String.valueOf(detail1),String.valueOf(detail2)};
		PatStammEvent pEvt = new PatStammEvent(LadeProg.this);
		pEvt.setPatStammEvent("PatSuchen");
		pEvt.setDetails(befehl,sEventDetails[0],sEventDetails[1]) ;
		PatStammEventClass.firePatStammEvent(pEvt);		
	}

}	
