package org.thera_pi.nebraska.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXPanel;
import org.thera_pi.nebraska.crypto.NebraskaCryptoException;
import org.thera_pi.nebraska.crypto.NebraskaFileException;
import org.thera_pi.nebraska.crypto.NebraskaKeystore;
import org.thera_pi.nebraska.crypto.NebraskaNotInitializedException;

import utils.ButtonTools;
import utils.DatFunk;
import utils.JRtaTextField;
import utils.MultiLineLabel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class NebraskaRequestDlg extends JDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6958116962234351720L;
	
	private JRtaTextField pw = null;
	private JButton[] buts = {null,null,null,null};
	private ActionListener al = null;
	private NebraskaZertAntrag zertantrag = null;
	private JXPanel content = null;
	private JLabel[] labs = {null,null,null};
	private MultiLineLabel[] mlabs = {null,null,null,null};
	private String pathtokeystoredir = null;
	private String pathtoprivkeydir = null;
	private String ik = null;
	private String institution = null;
	private String person = null;
	NebraskaKeystore keystore = null;
	
	public NebraskaRequestDlg(NebraskaZertAntrag zertantrag,boolean importiert,String therapidir){
		super();
		this.activateListeners();
		this.setTitle("4 Schritte zum Zertifikatsrequest");
		
		this.zertantrag = zertantrag;
		this.ik = zertantrag.getIK();
		this.institution = zertantrag.getInstitution();
		this.person = zertantrag.getPerson();
		
		if(this.ik.equals("") || this.institution.equals("") || this.person.equals("") ){
			JOptionPane.showMessageDialog(null,"IK, Antragsteller und Ansprechpartner dürfen nicht leer sein");
			this.dispose();
			return;
		}
		
		this.content = this.contentPanel();

		if(importiert){
			pathtokeystoredir = (therapidir+File.separator+"keystore"+File.separator+ik).replace("\\", "/");
			pathtoprivkeydir = (pathtokeystoredir+File.separator+"privkeys").replace("\\", "/");
			this.content.add(getImportPanel(importiert,therapidir,ik),BorderLayout.CENTER);
		}else{
			this.content.add(getStandAlonePanel(),BorderLayout.CENTER);
		}
		this.setContentPane(this.content);
		this.getContentPane().setPreferredSize(new Dimension(800,400));
		this.getContentPane().validate();
	}
	
	/*****
	 * Für Benutzer von Thera-Pi, die die Mandantendaten zuvor importiert haben
	 * @param importiert 
	 * @param therapidir
	 * @param ik
	 * @return
	 */

	private JXPanel getImportPanel(boolean importiert,String therapidir,String ik){
		//								 1           2         3   4   5  6    7        8            9 
		FormLayout lay = new FormLayout("0dlu,fill:0:grow(0.5),p,10dlu,p,5dlu,50dlu,fill:0:grow(0.5),0dlu",
		//		  1               2   3   4   5   6   7   8		9 
				"fill:0:grow(0.5),p,15dlu,p,15dlu,p,15dlu,p,fill:0:grow(0.5)");
		CellConstraints cc = new CellConstraints();
		JXPanel jpan = new JXPanel();
		jpan.setLayout(lay);
		mlabs[0] = new MultiLineLabel("1. Verzeichnis der Zertifikatsdatenbank\n(Bei Import aus Thera-Pi fest vorgegeben)",0,0);
		mlabs[0].setAlignment(MultiLineLabel.RIGHT);
		jpan.add(mlabs[0],cc.xy(3, 2,CellConstraints.RIGHT,CellConstraints.CENTER));
		labs[0] = new JLabel(pathtokeystoredir);
		labs[0].setForeground(Color.RED);
		jpan.add(labs[0],cc.xy(5, 2));
		jpan.add( (buts[0]=ButtonTools.macheBut("ändern", "edit", al)),cc.xy(7,2) );
		buts[0].setEnabled(false);
		mlabs[1] = new MultiLineLabel("2. Passwort für die Zertifikatsdatenbank eingeben\n(sofort notieren und sorgfältig aufbewahren!!!)",0,0);
		mlabs[1].setAlignment(MultiLineLabel.RIGHT);
		jpan.add(mlabs[1],cc.xy(3, 4,CellConstraints.RIGHT,CellConstraints.CENTER));
		pw = new JRtaTextField("nix",true);
		jpan.add(pw,cc.xy(5,4));
		jpan.add( (buts[1]=ButtonTools.macheBut("fixieren", "fixit", al)),cc.xy(7,4) );
		
		mlabs[2] = new MultiLineLabel("3. Sodele.... jetzt das geheime Schlüsselpaar erzeugen und speichern",0,0);
		mlabs[2].setEnabled(false);
		jpan.add(mlabs[2],cc.xyw(3,6,3,CellConstraints.RIGHT,CellConstraints.CENTER));
		jpan.add( (buts[2]=ButtonTools.macheBut("und los...", "generatekeypair", al)),cc.xy(7,6) );
		buts[2].setEnabled(false);
		
		mlabs[3] = new MultiLineLabel("4. Abschließend den Zertifikats-Request für die ITSG erzeugen\nund fertig ist die Laube",0,0);
		mlabs[3].setAlignment(MultiLineLabel.RIGHT);
		jpan.add(mlabs[3],cc.xyw(3,8,3,CellConstraints.RIGHT,CellConstraints.CENTER));
		jpan.add( (buts[3]=ButtonTools.macheBut("und los...", "generaterequest", al)),cc.xy(7,8) );
		buts[3].setEnabled(false);
		jpan.validate();
		return jpan;
	}
	
	private JXPanel getStandAlonePanel(){
		JXPanel jpan = new JXPanel();
		return jpan;
	}
	
	private JXPanel contentPanel(){
		JXPanel pan = new JXPanel(new BorderLayout());
		JXHeader jxhead = new JXHeader("Zertifikats-Request erzeugen",
				"Gehen Sie einfach Schritt für Schritt durch die 4 angegebenen Punkte - nur keine Panik, Sie haben Zeit...\n\n" +
				"Vergessen Sie bitte nicht Ihr gewähltes Passwort zu notieren und an sicherer Stelle zu verwahren!",null);
		pan.add(jxhead,BorderLayout.NORTH);
		return pan;
	}

	private void activateListeners(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("fixit")){
					doFixIt();
					return;
				}
				if(cmd.equals("generatekeypair")){
					doGenerateKeys();
					return;
				}
				if(cmd.equals("generaterequest")){
					doGenerateRequest();
					return;
				}
			}
			
		};
	}
	private void doFixIt(){
		if(pw.getText().trim().equals("")){
			JOptionPane.showMessageDialog(null, "Passwort darf nicht leer sein!");
			return;
		}
		mlabs[0].setEnabled(false);
		mlabs[1].setEnabled(false);
		buts[1].setEnabled(false);
		pw.setEditable(false);
		mlabs[2].setEnabled(true);
		buts[2].setEnabled(true);
		
	}
	private void doGenerateKeys(){
		mlabs[2].setEnabled(false);
		buts[2].setEnabled(false);
		mlabs[3].setEnabled(true);
		buts[3].setEnabled(true);
		String privkeyfile = "privkey"+DatFunk.sDatInSQL(DatFunk.sHeute()).replace("-", "");

		try {
			keystore = new NebraskaKeystore(
					this.pathtokeystoredir+File.separator+this.ik+".p12",
					pw.getText().trim(),
					"",
					"IK"+this.ik,
					this.institution,
					this.person);
			keystore.generateKeyPair(false, true, privkeyfile, pathtoprivkeydir);
		} catch (NebraskaCryptoException e) {
			e.printStackTrace();
		} catch (NebraskaFileException e) {
			e.printStackTrace();
		} catch (NebraskaNotInitializedException e) {
			e.printStackTrace();
		}

	}
	private void doGenerateRequest(){
		mlabs[3].setEnabled(false);
		buts[3].setEnabled(false);
		StringBuffer md5Buf = new StringBuffer();
		try {
			OutputStream out = new FileOutputStream(this.pathtokeystoredir+File.separator+this.ik+".p10");
			keystore.createCertificateRequest(out, md5Buf);
			out.flush();
			out.close();
			System.out.println(md5Buf);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NebraskaCryptoException e) {
			e.printStackTrace();
		} catch (NebraskaFileException e) {
			e.printStackTrace();
		} catch (NebraskaNotInitializedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
