package theraPi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;






public class TheraPi implements WindowListener,KeyListener{
	

		/**
		 * @param args
		 */
		static JDialog jDiag = null;
		JXPanel contentPanel = null;
		static JLabel standDerDingelbl = null;
		static boolean socketoffen = false;
		public static String proghome;
		//static SockServer sock = null;
		public static int AnzahlMandanten;
		public static int AuswahlImmerZeigen;
		public static int DefaultMandant;		
		public static int LetzterMandant;
		public static String StartMandant;
		public static Vector<String[]> mandvec = new Vector<String[]>();
		public static Vector<Vector<String>> updatefiles = new Vector<Vector<String>>();

		public static void main(String[] args) {
			TheraPi application = new TheraPi();
			String prog = java.lang.System.getProperty("user.dir");
			try {
				UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (InstantiationException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			} catch (UnsupportedLookAndFeelException e1) {
				e1.printStackTrace();
			}
			if(System.getProperty("os.name").contains("Linux")){
				proghome = "/opt/RehaVerwaltung/";
			}else if(System.getProperty("os.name").contains("Windows")){
				proghome = prog.substring(0, 2)+"/RehaVerwaltung/";
			}else if(System.getProperty("os.name").contains("Mac")){
				proghome = "/opt/RehaVerwaltung/";				
			}
			System.out.println("Programmverzeichnis = "+proghome);
			INIFile inif = new INIFile(proghome+"ini/mandanten.ini");
			int AnzahlMandanten = inif.getIntegerProperty("TheraPiMandanten", "AnzahlMandanten");
			int AuswahlImmerZeigen = inif.getIntegerProperty("TheraPiMandanten", "AuswahlImmerZeigen");
			int DefaultMandant = inif.getIntegerProperty("TheraPiMandanten", "DefaultMandant");
			int LetzterMandant = inif.getIntegerProperty("TheraPiMandanten", "LetzterMandant");
			if(AuswahlImmerZeigen==0){
				String s1 = inif.getStringProperty("TheraPiMandanten", "MAND-IK"+DefaultMandant);
				String s2 = inif.getStringProperty("TheraPiMandanten", "MAND-NAME"+DefaultMandant);
				
				String[] mand = {null,null};
				mand[0] = s1;
				mand[1] = s2;
				mandvec.add(mand);
				//updateCheck();
				StartMandant = s1+'@'+s2;
				RehaStarter rst = new RehaStarter();
				rst.execute();
				try {
					int i = rst.get();
					if(i==1){
						Thread.sleep(10000);
						System.out.println("Rückgabewert = 1");						
						System.exit(0);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}else{
				
				for(int i = 0; i < AnzahlMandanten;i++){
					String[] mand = {null,null};
					mand[0] = new String(inif.getStringProperty("TheraPiMandanten", "MAND-IK"+(i+1)));
					mand[1] = new String(inif.getStringProperty("TheraPiMandanten", "MAND-NAME"+(i+1)));
					mandvec.add(mand);
				}
				//updateCheck();
				jDiag = application.getDialog();

				jDiag.validate();
				jDiag.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				jDiag.setVisible(true);
			}
			
		}
		private JDialog getDialog() {
			contentPanel = new JXPanel(new BorderLayout());
			contentPanel.setPreferredSize(new Dimension(400,300));
			contentPanel.add(new SplashInhalt(),BorderLayout.CENTER);
			contentPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
			JXPanel textPanel = new JXPanel(new BorderLayout());
			textPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
			textPanel.setBackground(Color.WHITE);
			textPanel.setPreferredSize(new Dimension(0,15));
			standDerDingelbl = new JLabel("OpenSource-Projekt Reha-xSwing",JLabel.CENTER);
			standDerDingelbl.setFont(new Font("Arial", 8, 10));
			textPanel.add(standDerDingelbl,BorderLayout.CENTER);
			contentPanel.add(textPanel,BorderLayout.SOUTH);
			contentPanel.validate();

			JDialog xDiag = new JDialog();
			xDiag.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			xDiag.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			xDiag.setUndecorated(true);
			xDiag.setContentPane(contentPanel);
			xDiag.setSize(450, 200);
			xDiag.setLocationRelativeTo(null);
			xDiag.addWindowListener(this);
			xDiag.addKeyListener(this);
			xDiag.validate();
			xDiag.pack();
			return xDiag;
		}
		
		private static void updateCheck(){
			String updatedir = "";
			String updatefile = "";
			String zeile = "";
			FileReader reader = null;
			BufferedReader in = null;
			
			File f = new File(proghome+"update.conf");
			if(!f.exists()){
				JOptionPane.showMessageDialog(null, "Ihr System ist nicht für automatisches Update eingerichtet.\n"+
						"Bitte wenden Sie sich an den Systemadministrator");
				return;
			}
			try{
				reader = new FileReader(proghome+"update.conf");
				in = new BufferedReader(reader);
				while ((zeile = in.readLine()) != null) {
					updatefile = zeile;
					break;
				}
				updatedir = updatefile.substring(0,updatefile.lastIndexOf("/")).trim()+"/";
				in.close();
				reader.close();
				System.out.println("Updatefile = "+updatefile);
				System.out.println("UpdateDir = "+updatedir);
				}catch (IOException e) {
					e.printStackTrace();
				}
				f = new File(updatefile);
				if(!f.exists()){
					//JOptionPane.showMessageDialog(null, "Das angegebene Update-Verzeichnis existiert nicht");
					return;
				}

			try {
				Vector<String> dummy = new Vector<String>();
				zeile = "";
				reader = new FileReader(updatefile);
				in = new BufferedReader(reader);
				String pfad = "";
				String[] sourceAndTarget = {null,null};
				Vector<String> targetvec = new Vector<String>(); 
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
								pfad = zeile;
								if(sourceAndTarget[1].contains("%proghome%")){
									dummy.clear();
									dummy.add(updatedir+sourceAndTarget[0].trim());
									dummy.add(sourceAndTarget[1].trim().replace("%proghome%", proghome));
									if(! targetvec.contains(dummy.get(1))){
										targetvec.add(new String(dummy.get(1)));
										updatefiles.add( ((Vector<String>)dummy.clone()));									
									}
								}else if(sourceAndTarget[1].contains("%userdir%")){
									String home = sourceAndTarget[1].trim().replace("%userdir%", proghome); 
									for(int i = 0; i < mandvec.size();i++){
										dummy.clear();
										dummy.add(updatedir+sourceAndTarget[0].trim());
										dummy.add(home.replace("%mandantik%", mandvec.get(i)[0]));
										if(! targetvec.contains(dummy.get(1))){
											targetvec.add(new String(dummy.get(1)));
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
					System.out.println(updatefiles);
					System.out.println("Anzahl Update-Dateien = "+updatefiles.size());
					kopiereUpdate();
				}
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,"Fehler beim Bezug der Update-Informationen.\nBitte informieren Sie den Administrator umgehend");
			}
		}
		private static void kopiereUpdate(){
			try {
			String source,target; 
			File fsource,ftarget;
			JDialog jdiag = null;
			JPanel jpan = null;
			JLabel jlab = null;
			for(int i = 0; i < updatefiles.size();i++){
				int lastoccurence = updatefiles.get(i).get(1).lastIndexOf("/");
				String verzeichnis = updatefiles.get(i).get(1).substring(0,lastoccurence);
				File vz = new File(verzeichnis);
				if(!vz.exists()){
					vz.mkdirs();
					System.out.println("Erstelle Verzeichnis "+verzeichnis);
				}
				source = updatefiles.get(i).get(0);
				target = updatefiles.get(i).get(1);
				ftarget = new File(target);
				if(!ftarget.exists()){
					if(jdiag==null){
						macheDialog(jdiag,jpan,jlab);
					}
					if(jlab != null){
						System.out.println(updatefiles);
						jlab.setText("aktualisiere Datei "+updatefiles.get(i).get(0));
					}
					System.out.println("Kopiere Datei "+source+" nach "+target);
					/*
					System.out.println("Datei existiert nicht und muß kopiert werden");
					System.out.println("Kopiere Datei -> "+updatefiles.get(i).get(0));
					System.out.println("Zieldatei -> "+updatefiles.get(i).get(1));
					*/
					copy(source, target);
				}else{
					fsource = new File(source);
					if(fsource.lastModified() > ftarget.lastModified()){
						if(jdiag==null){
							macheDialog(jdiag,jpan,jlab);
						}
						if(jlab != null){
							System.out.println(updatefiles);
							jlab.setText("aktualisiere Datei "+updatefiles.get(i).get(0));
						}
						/*
						System.out.println("Neue Updatedatei vorhanden");
						System.out.println("Kopiere Datei -> "+updatefiles.get(i).get(0));
						System.out.println("Zieldatei -> "+updatefiles.get(i).get(1));
						*/
						copy(source, target);
					}else{
						System.out.println("Bestehende Datei bereits auf dem aktuellen Stand");						
					}
				}
			}
			if(jdiag!=null){
				jdiag.setVisible(false);
				jdiag.dispose();
			}

			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		private static void macheDialog(JDialog jdiag,JPanel jpan,JLabel jlab){
			jpan = new JXPanel(new FlowLayout(FlowLayout.CENTER));
			jlab = new JLabel("bitte warten System wird aktualisiert");
			jpan.add(jlab);
			jpan.setPreferredSize(new Dimension(250,50));
			jdiag = new JDialog();
			jdiag.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			jdiag.setPreferredSize(new Dimension(250,50));
			jdiag.setContentPane(jpan);
			jdiag.setModal(false);
			jdiag.setUndecorated(true);
			jdiag.setLocationRelativeTo(null);
			jdiag.pack();
			jdiag.setVisible(true);
		}
		public static void copy(String from, String to) throws IOException{
			   InputStream in = null;
			   OutputStream out = null; 
			   try {
			      InputStream inFile = new FileInputStream(from);
			      in = new BufferedInputStream(inFile);
			      OutputStream outFile = new FileOutputStream(to);
			      out = new BufferedOutputStream(outFile);
			      while (true) {
			         int data = in.read();
			         if (data == -1) {
			            break;
			         }
			         out.write(data);
			      }
			   } finally {
			      if (in != null) {
			         in.close();
			      }
			      if (out != null) {
			         out.close();
			      }
			   }
			}
		
		@Override
		public void windowActivated(WindowEvent arg0) {
		}

		@Override
		public void windowClosed(WindowEvent arg0) {
		}

		@Override
		public void windowClosing(WindowEvent arg0) {
		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {
		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
		}

		@Override
		public void windowIconified(WindowEvent arg0) {
		}

		@Override
		public void windowOpened(WindowEvent arg0) {
		}

		@Override
		public void keyPressed(KeyEvent arg0) {
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
		}

}
class RehaStarter extends SwingWorker<Integer,Void>{

	@Override
	protected Integer doInBackground() throws Exception {
		try{
			

		String programm = TheraPi.proghome+"Reha.jar";
		System.out.println("In TheraPi.jar Programmstart = "+programm);
		String mandik = TheraPi.StartMandant.split("@")[0];
		INIFile minif = new INIFile(TheraPi.proghome+"ini/"+mandik+"/rehajava.ini");
		String memsizemin = "-Xms128m ";
		String memsizemax = "-Xmx256m ";
		//String memsizethread = "-Xxs2046k ";
		/****/
		String dummy = minif.getStringProperty("SystemIntern", "MinMemSize");
		if(dummy != null){
			memsizemin = "-Xms"+dummy+" ";
		}else{
			minif.setStringProperty("SystemIntern", "MinMemSize", "128m",null);
			minif.save();
		}
		/****/
		dummy = minif.getStringProperty("SystemIntern", "MaxMemSize");
		if(dummy != null){
			memsizemax = "-Xmx"+String.valueOf(dummy)+" ";
		}else{
			minif.setStringProperty("SystemIntern", "MaxMemSize", "256m",null);
			minif.save();
		}
		/****/
		/*
		dummy = minif.getStringProperty("SystemIntern", "MaxMemThread");
		if(dummy != null){
			memsizethread = "-Xxs"+String.valueOf(dummy)+" ";
		}else{
			minif.setStringProperty("SystemIntern", "MaxMemThread", "2046k",null);
			minif.save();
		}
		*/
		
		//String start = new String("java -jar "+memsizemin+memsizemax+memsizethread+TheraPi.proghome+"Reha.jar "+TheraPi.StartMandant /*+" > "+TheraPi.proghome+TheraPi.StartMandant.split("@")[0]+".log" */);
		String start = new String("java -jar "+memsizemin+memsizemax+TheraPi.proghome+"Reha.jar "+TheraPi.StartMandant );
		//String start = new String("cmd.exe /C start "+TheraPi.proghome.replace("/",File.separator)+"runtherapi.bat "+TheraPi.StartMandant+" "+TheraPi.StartMandant.split("@")[0]+".log");
		
		System.out.println("Kommando ist "+start);
		//JOptionPane.showMessageDialog(null, start);
		Runtime.getRuntime().exec(start);
        System.out.println("Reha gestartet");
        System.out.println(start);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return 1;

	}
}
