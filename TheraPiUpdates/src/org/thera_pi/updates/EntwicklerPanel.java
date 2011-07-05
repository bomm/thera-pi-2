package org.thera_pi.updates;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.net.ftp.FTPFile;
import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class EntwicklerPanel extends JXPanel implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private TheraPiUpdates eltern = null;
	private UpdateTab updateTab = null;
	private JPasswordField pw = null;
	private JTextArea talog = null;
	private JTextArea tafiles = null;
	private JLabel uploadfile = null;
	private JButton hochladen = null;
	private JButton dateiwahl = null;
	private JButton beenden = null;
	
	private String updatepfad = null;
	private String updatedatei = null;
	private JProgressBar pbar = null;
	private String entwicklerpw = "therapi1updates2";
	
	EntwicklerPanel(TheraPiUpdates xeltern,UpdateTab xupdatetab){
		super();
		eltern = xeltern;
		updateTab = xupdatetab;
		setLayout(new BorderLayout());
		add(doContent(),BorderLayout.CENTER);
		validate();
	}
	
	private JXPanel doContent(){
		JXPanel jpan = new JXPanel();
		//                1    2     3    4   5    6     7
		String xwerte = "5dlu,80dlu,5dlu,p:g,2dlu,60dlu,5dlu";
		//                1     2    3    4   5   6  7     8      9  10  11  12
		String ywerte = "15dlu, p ,10dlu, p,5dlu,fill:0:grow(1.0),5dlu,100dlu,2dlu,p,5dlu,p,5dlu";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		jpan.setLayout(lay);
		CellConstraints cc = new CellConstraints();

		jpan.add(new JLabel("FTP-Passwort"),cc.xy(2, 2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		pw = new JPasswordField();
		jpan.add(pw,cc.xy(4, 2));
		
		//jpan.add(new JLabel("Update-Datei wählen"),cc.xy(2, 4,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		uploadfile = new JLabel(" ");
		uploadfile.setForeground(Color.BLUE);
		jpan.add(uploadfile,cc.xy(4,4));
		dateiwahl = new JButton("Datei wählen");
		dateiwahl.setActionCommand("datwahl");
		dateiwahl.addActionListener(this);
		jpan.add(dateiwahl,cc.xy(2,4));
		
		jpan.add(new JLabel("Change-Log"),cc.xy(2, 6,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		talog = new JTextArea();
		talog.setFont(new Font("Courier",Font.PLAIN,12));
		talog.setLineWrap(true);
		talog.setWrapStyleWord(true);
		//talog.setEditable(false);
		talog.setBackground(Color.WHITE);
		talog.setForeground(Color.BLUE);
		JScrollPane jscr = JCompTools.getTransparentScrollPane(talog);
		jscr.validate();
		jpan.add(jscr,cc.xy(4,6,CellConstraints.FILL,CellConstraints.FILL));
		
		jpan.add(new JLabel("update.files"),cc.xy(2, 8,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tafiles = new JTextArea();
		tafiles.setFont(new Font("Courier",Font.PLAIN,12));
		tafiles.setLineWrap(true);
		tafiles.setWrapStyleWord(true);
		//tafiles.setEditable(false);
		tafiles.setBackground(Color.WHITE);
		tafiles.setForeground(Color.BLUE);
		jscr = JCompTools.getTransparentScrollPane(tafiles);
		jscr.validate();
		jpan.add(jscr,cc.xy(4,8,CellConstraints.FILL,CellConstraints.FILL));
		
		pbar = new JProgressBar();
		pbar.setStringPainted(true);
		jpan.add(pbar,cc.xy(4, 10));
		
		hochladen = new JButton("gewählte Datei + Change-Log + update.files hochladen");
		hochladen.setActionCommand("hochladen");
		hochladen.addActionListener(this);
		jpan.add(hochladen,cc.xy(4, 12));
		hochladen.setEnabled(false);
		
		beenden = new JButton("beenden");
		beenden.setActionCommand("beenden");
		beenden.addActionListener(this);
		jpan.add(beenden,cc.xy(6, 12));

		jpan.validate();
		return jpan;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String cmd = arg0.getActionCommand();
		if(cmd.equals("datwahl")){
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					doDateiwahl();
					return null;
				}
			}.execute();
		}
		if(cmd.equals("hochladen")){
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					doHochladen();
					JOptionPane.showMessageDialog(null, "Update erfolgreich beendet");
					return null;
				}
			}.execute();
			hochladen.setEnabled(false);

		}
		if(cmd.equals("beenden")){
			doBeenden();
		}
		
	}
	
	public void doBeenden(){
		System.exit(0);
	}
	
	public void doDateiwahl(){
		String passw = new String(pw.getPassword());
		if(! passw.equals(entwicklerpw)){
			JOptionPane.showMessageDialog(null,"Bitte geben Sie ein gültiges FTP-Passwort für Entwickler ein.\nSofern Sie noch kein Passwort haben nehmen Sie Kontakt mit Herrn Steinhilber auf.");
			hochladen.setEnabled(false);
			return;
		}
		dateiDialog(UpdateConfig.getProghome());
		if(updatedatei==null){
			updatepfad = "";
			updatedatei = "";
		}
		if(updatedatei.equals("")){
			updatepfad = "";
			uploadfile.setText("");
			hochladen.setEnabled(false);
			return;
		}
		uploadfile.setText(updatepfad.replace("\\", "/"));
		FTPFile[] ffile = updateTab.getFilesFromUpdatePanel();
		boolean holeAltenLog = false;
		for(int i = 0; i < ffile.length;i++){
			if(ffile[i].getName().toString().equals(updatedatei+".log")){
				holeAltenLog = true;
				break;
			}
		}
		if(holeAltenLog){
			talog.setText(holeLogText( updatedatei+".log"));
			talog.setCaretPosition(0);
		}else{
			talog.setText("");
		}
		tafiles.setText(holeLogText("update.files"));
		tafiles.setCaretPosition(0);
		hochladen.setEnabled(true);
	}

	public void doHochladen(){
		String passw = new String(pw.getPassword());
		if(! passw.equals(entwicklerpw)){
			JOptionPane.showMessageDialog(null,"Bitte geben Sie ein gültiges FTP-Passwort für Entwickler ein.\nSofern Sie noch kein Passwort haben nehmen Sie Kontakt mit Herrn Steinhilber auf.");
			return;
		}
		if(updatedatei.equals("")){
			JOptionPane.showMessageDialog(null,"Keine gültige Update-Datei ausgewählt.");
			return;
		}
		if(talog.getText().trim().equals("")){
			JOptionPane.showMessageDialog(null,"Ein Eintrag im Change-Log ist zwingend vorgeschrieben");
			return;
		}
		long gross = new File(updatepfad).length();
		FTPTools ftpClient = new FTPTools();
		ftpClient.ftpTransferDatei(updatedatei, updatepfad, gross, pbar);
		ftpClient.ftpTransferString(updatedatei+".log",talog.getText(),pbar);
		ftpClient.ftpTransferString("update.files",tafiles.getText(),pbar);

	}
	
	private String holeLogText(String logDatei){
		FTPTools ftp = new FTPTools();
		return ftp.holeLogDateiSilent(logDatei);
	}

	private String dateiDialog(String pfad){
		String sret = "";
		final JFileChooser chooser = new JFileChooser("Verzeichnis wählen");
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        final File file = new File(pfad);

        chooser.setCurrentDirectory(file);

        chooser.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)
                        || e.getPropertyName().equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
                    final File f = (File) e.getNewValue();
                }
            }

        });
        chooser.setVisible(true);

        final int result = chooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File inputVerzFile = chooser.getSelectedFile();
            updatepfad = inputVerzFile.getPath();
            

            if(inputVerzFile.getName().trim().equals("")){
            	updatedatei = "";
            }else{
            	updatedatei = inputVerzFile.getName().trim();	
            }
        }else{
        	updatedatei = ""; //vorlagenname.setText(SystemConfig.oTerminListe.NameTemplate);
        }
        chooser.setVisible(false); 

        return sret;
	}


}
