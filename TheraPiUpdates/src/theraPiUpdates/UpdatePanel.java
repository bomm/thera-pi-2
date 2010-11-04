package theraPiUpdates;



import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.net.ftp.FTPFile;
import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;





public class UpdatePanel extends JXPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8445390544969581869L;
	TheraPiUpdates eltern = null;
	
	UpdateTableModel tabmod = null;
	JXTable tab = null;

	public JProgressBar pbar = null;
	
	DecimalFormat dcf = new DecimalFormat("##########0.00");
	SimpleDateFormat datumsFormat = new SimpleDateFormat ("dd.MM.yyyy  HH:mm:ss"); //Konv.

	public static Vector<Vector<String>> updatefiles = new Vector<Vector<String>>();
	public static Vector<String[]> mandvec = new Vector<String[]>();
	
	FTPTools ftpt = null;
	
	Image imgkeinupdate = new ImageIcon(TheraPiUpdates.proghome+"icons/clean.png").getImage().getScaledInstance(16,16, Image.SCALE_SMOOTH);
	Image imgupdate = new ImageIcon(TheraPiUpdates.proghome+"icons/application-exit.png").getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);	
	ImageIcon icokeinupdate;
	ImageIcon icoupdate;
	UpdatePanel(TheraPiUpdates xeltern){
	
		
		super();
		eltern = xeltern;
		setLayout(new BorderLayout());
		add(getHeader(),BorderLayout.NORTH);
		add(getContent(),BorderLayout.CENTER);

		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				icokeinupdate = new ImageIcon(imgkeinupdate);
				icoupdate = new ImageIcon(imgupdate);
				doHoleUpdateConf();
				updateCheck(TheraPiUpdates.proghome+"update.files");
				doFtpTest();
				return null;
			}
		}.execute();
	}
	private JXHeader getHeader(){
		JXHeader head = new JXHeader();
		String titel = "<html><font size='5'><font color='e77817'>Thera-Pi</font>&nbsp;&nbsp; Update-Explorer</font></html>";
        head.setTitle(titel);
        String description = "<html>Ein rotes "+
        "<img src='file:///"+TheraPiUpdates.proghome+"icons/application-exit.png' width='16' height='16' align=\"bottom\">"+ 
        "signalisiert, daß die angezeigte Datei <b>neuer</b> ist als die Datei die sich auf Ihrem Rechner befindet.<br>"+
        "Wenn Sie in der Tabelle einen Doppelklick auf einer dieser Dateien ausführen, kopieren Sie diese Datei in Ihre Thera-Pi-Installation."+
        "<b><font color='aa0000'><br>Achtung:</font></b><br>Wenn INI-Dateien zum Update angeboten werden überschreiben Sie evtl. bestehende individuelle INI-Dateien. Bitte "+
        "machen Sie in diesem Fall vor dem Update eine <b>Sicherungskopie Ihres 'INI-Verzeichnisses'</b></html>";
        head.setDescription(description);
        head.setIcon(new ImageIcon(TheraPiUpdates.proghome+"icons/TPorg.png"));   
		return head;
	}
	
	private JXPanel getContent(){
		JXPanel jpan = new JXPanel();
		String xwerte = "5dlu,p:g,5dlu";
		//                1     2    3    4      5   6  7      8
		String ywerte = "10dlu,0dlu,0dlu,150dlu,5dlu,p,0dlu:g,10dlu";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		jpan.setLayout(lay);
		tabmod = new UpdateTableModel();
		tabmod.setColumnIdentifiers(new String[] { "Update-Datei","Dateidatum/Uhrzeit","Größe in Bytes","aktuell"});
		tab = new JXTable(tabmod);
		tab.setSortable(false);
		tab.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent evt){
				if(evt.getClickCount()==2){
					testeObUpdate(tab.getSelectedRow());
				}
			}
		});
		JScrollPane jscr = JCompTools.getTransparentScrollPane(tab);
		jscr.validate();
		jpan.add(jscr,cc.xy(2,4));
		pbar = new JProgressBar();
		pbar.setStringPainted(false);
		jpan.add(pbar,cc.xy(2,6));
		
		JButton but = new JButton("Update-Explorer beenden");
		but.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(TheraPiUpdates.starteTheraPi){
					int anfrage = JOptionPane.showConfirmDialog(null, "Wollen Sie Thera-Pi 1.0 jetzt starten?","Thera-Pi starten?",JOptionPane.YES_NO_OPTION);
					if(anfrage == JOptionPane.YES_OPTION){
						try {
							Runtime.getRuntime().exec("java -jar "+TheraPiUpdates.proghome+"TheraPi.jar");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					System.exit(0);
				}else{
					System.exit(0);
				}
			}
		});
		jpan.add(but,cc.xy(2,7,CellConstraints.RIGHT,CellConstraints.BOTTOM));
		jpan.validate();

		return jpan;
	}
	private void testeObUpdate(int row){
		String upddatei = tab.getValueAt(row, 0).toString();
		Long gross = Long.parseLong(tab.getValueAt(row, 2).toString());
		if(tab.getValueAt(row, 3).equals(this.icokeinupdate)){
			JOptionPane.showMessageDialog(null, "Die Datei --> "+upddatei+" <-- ist bereits auf dem neuesten Stand. Update nicht erforderlich");
			return;
		}
		boolean ok = true;
		for(int i = 0; i < updatefiles.size();i++){
			if(updatefiles.get(i).get(0).equals(upddatei)){
				String cmd = "<html>Wollen Sie die Update-Datei <b>"+updatefiles.get(i).get(0)+"</b> nach<br>"+
				"<b>"+updatefiles.get(i).get(1)+"</b> kopieren</html>";
				int anfrage = JOptionPane.showConfirmDialog(null, cmd,"Achtung wichtige Benutzeranfrage",JOptionPane.YES_NO_OPTION);
				if(anfrage==JOptionPane.YES_OPTION){
					pbar.setStringPainted(true);
					pbar.getParent().validate();
					final int ix = i;
					final String xupdate = upddatei;
					final long xgross = gross;
					final int xrow = row;
					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() throws Exception {
							pbar.setValue(0);
							String nurvz = updatefiles.get(ix).get(1).substring(0,updatefiles.get(ix).get(1).lastIndexOf("/")+1);
							System.out.println(updatefiles.get(ix).get(1));
							System.out.println(updatefiles.get(ix).get(0));
							System.out.println(nurvz);
							try{
								if(updatefiles.get(ix).get(1).endsWith(".ini")){
									File f = new File(updatefiles.get(ix).get(1));
									f.renameTo(new File(updatefiles.get(ix).get(1).replace(".ini", ".bak")));
								}
							}catch(Exception ex){
								JOptionPane.showMessageDialog(null, "Fehler beim Umbenennen der Datei "+updatefiles.get(ix).get(1));
							}
							ftpt = new FTPTools();
							ftpt.holeDatei(xupdate, nurvz, true, getInstance(),xgross);
							ftpt = null;
							pbar.setValue(0);
							tabmod.setValueAt((ImageIcon) icokeinupdate,xrow, 3);
							return null;
						}

					}.execute();
					
				}else{
					ok = false;
				}
			}
		}
		if(ok){
			//tabmod.setValueAt((ImageIcon) this.icokeinupdate,row, 3);	
		}
		
	}
	private UpdatePanel getInstance(){
		return this;
	}
	private void doHoleUpdateConf(){
		ftpt = new FTPTools();
		ftpt.holeDatei("update.files", TheraPiUpdates.proghome, false, getInstance(),-1);
		ftpt = null;
	}
	private void doFtpTest(){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				TheraPiUpdates.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				ftpt = new FTPTools();
				FTPFile[] ffile = ftpt.holeDatNamen();
				Vector<Object> vec = new Vector<Object>();
				for(int i = 0; i < ffile.length;i++){
					if( (!ffile[i].getName().toString().trim().equals(".")) &&
							(!ffile[i].getName().toString().trim().equals("..")) &&
							(!ffile[i].getName().toString().startsWith("update."))){
						vec.clear();
						vec.add(ffile[i].getName().toString());
						vec.add(datumsFormat.format(ffile[i].getTimestamp().getTime().getTime()));
						vec.add(Long.valueOf(ffile[i].getSize()).toString());
						if(mussUpdaten(ffile[i].getName().toString().trim(),ffile[i].getTimestamp().getTime().getTime())){
							vec.add(icoupdate);
							tabmod.addRow((Vector<?>)vec.clone());
						}
						//System.out.println(ffile[i].getName());
						//System.out.println(ffile[i].getTimestamp().getTime().getTime());
						//System.out.println(ffile[i].getSize() + " Bytes");
					}
				}
				if(tabmod.getRowCount()> 0){
					tab.setRowSelectionInterval(0, 0);
				}else{
					JOptionPane.showMessageDialog(null, "Ihre Thera-Pi-Installation ist auf dem aktuellen Stand.");
				}
				/*
				System.out.println("Insgesamt = "+ffile.length+" Dateien");
				File file = new File("C:/RehaVerwaltung/Reha.jar");
				System.out.println("Größe Originaldatei  = "+file.length()+" Bytes");
				System.out.println(file.lastModified());
				ftpt.holeDatei("update.files", "C:/ftptests/", true, getInstance(),-1);
				Thread.sleep(100);
				pbar.setValue(0);
				ftpt.holeDatei("Reha.jar", "C:/ftptests/", true, getInstance(),-1);
				*/
				ftpt = null;
				TheraPiUpdates.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

				return null;
			}
			
		}.execute();
		 
		
	}
	private boolean mussUpdaten(String datei,Long datum){
		try{
		File f = null;
		for(int i = 0; i < updatefiles.size();i++){
			if(updatefiles.get(i).get(0).equals(datei)){
				f = new File(updatefiles.get(i).get(1));
				if(!f.exists()){
					return true;
				}
				if(f.lastModified() < datum){
					//System.out.println("Zeitunterschied  = "+(f.lastModified()-datum));
					//System.out.println(datumsFormat.format(f.lastModified()));
					//System.out.println(datumsFormat.format(datum));
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
	private static void updateCheck(String xupdatefile){
		String updatedir = "";
		String zeile = "";
		FileReader reader = null;
		BufferedReader in = null;
		
		/************************/
		INIFile inif = new INIFile(TheraPiUpdates.proghome+"ini/mandanten.ini");
		int AnzahlMandanten = inif.getIntegerProperty("TheraPiMandanten", "AnzahlMandanten");
		for(int i = 0; i < AnzahlMandanten;i++){
			String[] mand = {null,null};
			mand[0] = new String(inif.getStringProperty("TheraPiMandanten", "MAND-IK"+(i+1)));
			mand[1] = new String(inif.getStringProperty("TheraPiMandanten", "MAND-NAME"+(i+1)));
			mandvec.add(mand);
		}
		
		
		/************************/
		/*
		File f = new File(TheraPiUpdates.proghome+"update.conf");
		if(!f.exists()){
			JOptionPane.showMessageDialog(null, "Ihr System ist nicht für automatisches Update eingerichtet.\n"+
					"Bitte wenden Sie sich an den Systemadministrator");
			return;
		}
		try{
			reader = new FileReader(TheraPiUpdates.proghome+"update.conf");
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
			*/

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
								dummy.add(sourceAndTarget[1].trim().replace("%proghome%", TheraPiUpdates.proghome).replace("//", "/"));
								if(! targetvec.contains(dummy.get(1))){
									targetvec.add(new String((String)dummy.get(1)));
									updatefiles.add( ((Vector<String>)dummy.clone()));									
								}
							}else if(sourceAndTarget[1].contains("%userdir%")){
								String home = sourceAndTarget[1].trim().replace("%userdir%", TheraPiUpdates.proghome).replace("//", "/"); 
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

	class UpdateTableModel extends DefaultTableModel{
		   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Class<?> getColumnClass(int columnIndex) {
			if(columnIndex==3){
				return ImageIcon.class;
			}
			return String.class;
	    }

		public boolean isCellEditable(int row, int col) {
			
				return false;				
		}
		   
	}

}
