package testForUpdates;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.apache.commons.net.ftp.FTPFile;

import theraPiUpdates.FTPTools;
import theraPiUpdates.INIFile;
import theraPiUpdates.TheraPiUpdates;





public class TestForUpdates {
	public static String proghome = null; //java.lang.System.getProperty("user.dir").replace("\\","/")+"/";
	
	public static Vector<Vector<String>> updatefiles = new Vector<Vector<String>>();
	public static Vector<String[]> mandvec = new Vector<String[]>();

	
	theraPiUpdates.FTPTools ftpt = null;
	
	public TestForUpdates(){
		if(TheraPiUpdates.testphase){
			proghome = "C:/RehaVerwaltung/";
		}else{
			proghome = java.lang.System.getProperty("user.dir").replace("\\","/")+"/";	
		}
		TheraPiUpdates.proghome = proghome;
		TheraPiUpdates.oeffneIniDatei();
		doHoleUpdateConfSilent();
		
	}
	
	
	/*******************/
	
	
	/*******************/
	private void doHoleUpdateConfSilent(){
		ftpt = new FTPTools();
		ftpt.holeDateiSilent("update.files", proghome, false);
		ftpt = null;
		updateCheck(proghome+"update.files");
	}
	
	
	public boolean doFtpTest(){
		ftpt = new FTPTools();
		FTPFile[] ffile = ftpt.holeDatNamen();
		for(int i = 0; i < ffile.length;i++){
			if( (!ffile[i].getName().toString().trim().equals(".")) &&
					(!ffile[i].getName().toString().trim().equals("..")) &&
					(!ffile[i].getName().toString().startsWith("update."))){
				if(mussUpdaten(ffile[i].getName().toString().trim(),ffile[i].getTimestamp().getTime().getTime())){
					ftpt = null;
					return true;
				}					
			}
		}	
		ftpt = null;
		return false;
	}
	private boolean mussUpdaten(String datei,Long datum){
		try{
		File f = null;
		//JOptionPane.showMessageDialog(null, "Vector-Size = "+updatefiles.size());
		for(int i = 0; i < updatefiles.size();i++){
			if(updatefiles.get(i).get(0).equals(datei)){
				f = new File(updatefiles.get(i).get(1));
				if(!f.exists()){
					//JOptionPane.showMessageDialog(null, "Es muß updated werden-1");
					return true;
				}
				if(f.lastModified() < datum){
					//System.out.println("Zeitunterschied  = "+(f.lastModified()-datum));
					//System.out.println(datumsFormat.format(f.lastModified()));
					//System.out.println(datumsFormat.format(datum));
					//JOptionPane.showMessageDialog(null, "Es muß updated werden-2");
					return true;
				}
			}
		}
		//System.out.println("Zeitunterschied  = "+(f.lastModified()-datum));
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return false;
	}
	
	/************************************************************************/
	private void updateCheck(String xupdatefile){
		
		String updatedir = "";
		String zeile = "";
		FileReader reader = null;
		BufferedReader in = null;
		
		/************************/
		INIFile inif = new INIFile(proghome+"ini/mandanten.ini");
		int AnzahlMandanten = inif.getIntegerProperty("TheraPiMandanten", "AnzahlMandanten");
		for(int i = 0; i < AnzahlMandanten;i++){
			String[] mand = {null,null};
			mand[0] = new String(inif.getStringProperty("TheraPiMandanten", "MAND-IK"+(i+1)));
			mand[1] = new String(inif.getStringProperty("TheraPiMandanten", "MAND-NAME"+(i+1)));
			mandvec.add(mand);
		}
		
		
		/************************/
		try {
			Vector<Object> dummy = new Vector<Object>();
			zeile = "";
			reader = new FileReader(xupdatefile);
			in = new BufferedReader(reader);
			//String pfad = "";
			String[] sourceAndTarget = {null,null};
			Vector<Object> targetvec = new Vector<Object>(); 
			while ((zeile = in.readLine()) != null) {
				if(!zeile.startsWith("#")){
					if(zeile.length()>5){
						sourceAndTarget = zeile.split("@");
						//System.out.println(zeile);
						//System.out.println(sourceAndTarget[0].trim());
						//System.out.println(sourceAndTarget[1].trim().replace("%proghome%", proghome));
						//System.out.println(sourceAndTarget.length);
						if(sourceAndTarget.length==2){
							//nur dann Dateien eintrgen
							//pfad = zeile;
							if(sourceAndTarget[1].contains("%proghome%")){
								dummy.clear();
								dummy.add(updatedir+sourceAndTarget[0].trim());
								dummy.add(sourceAndTarget[1].trim().replace("%proghome%", proghome).replace("//", "/"));
								if(! targetvec.contains(dummy.get(1))){
									targetvec.add(new String((String)dummy.get(1)));
									updatefiles.add( ((Vector<String>)dummy.clone()));									
								}
							}else if(sourceAndTarget[1].contains("%userdir%")){
								String home = sourceAndTarget[1].trim().replace("%userdir%", proghome).replace("//", "/"); 
								for(int i = 0; i < mandvec.size();i++){
									dummy.clear();
									dummy.add(updatedir+sourceAndTarget[0].trim());
									dummy.add(home.replace("%mandantik%", mandvec.get(i)[0]));
									if(! targetvec.contains(dummy.get(1))){
										updatefiles.add( ((Vector<String>)dummy.clone()));									
									}
								}
							}
						// Ende nur dann Dateien eintragen	
						}
					}
				}
			}
			in.close();
			reader.close();
			if(updatefiles.size()>0){
				//System.out.println(updatefiles);
				System.out.println("Anzahl Update-Dateien = "+updatefiles.size());
				//kopiereUpdate();
			}
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,"Fehler beim Bezug der Update-Informationen.\nBitte informieren Sie den Administrator umgehend");
		}
	}
	
	/************************************************************************/
	
	
	
	

}
