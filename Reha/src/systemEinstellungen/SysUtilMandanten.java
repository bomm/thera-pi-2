package systemEinstellungen;

import hauptFenster.Reha;
import hilfsFenster.NeuerMandant;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import sqlTools.ExUndHop;
import systemTools.JRtaComboBox;
import systemTools.JRtaTextField;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SysUtilMandanten extends JXPanel implements KeyListener, ActionListener, FocusListener {
	
		JComboBox mandant = null;
		
		JRtaComboBox bula = null;
		String[] laender = {"Baden-Württemberg","Bayern","Berlin","Brandenburg","Bremen",
				"Hamburg","Hessen","Mecklenburg-Vorpommern","Niedersachsen","Rheinland-Pfalz",
				"Saarland","Sachsen","Sachsen-Anhalt","Schleswig-Holstein","Thüringen"};

		JRtaTextField mandantIK = null;
		JRtaTextField mandantname = null;
		JRtaTextField mandantname1 = null;
		JRtaTextField mandantname2 = null;		
		JRtaTextField anrede = null;
		JRtaTextField nachname = null;
		JRtaTextField vorname = null;
		JRtaTextField strasse = null;
		JRtaTextField plz = null;
		JRtaTextField ort = null;
		JRtaTextField telnr = null;
		JRtaTextField faxnr = null;
		JRtaTextField mail = null;
		JRtaTextField website = null;
		JRtaTextField bank = null;
		JRtaTextField blz = null;
		JRtaTextField kto = null;
		JRtaTextField steuernr = null;
		JRtaTextField handelsreg = null;
		JRtaTextField logo = null;
		JRtaTextField zusatz1 = null;
		JRtaTextField zusatz2 = null;
		JRtaTextField zusatz3 = null;
		JRtaTextField zusatz4 = null;
		JRtaTextField[] tfield = {null,null,null,null,null,null,null,null,null,null,
								null,null,null,null,null,null,null,null,null,null,
								null,null,null,null};
		
		JButton knopf1 = null;
		JButton knopf2 = null;
		JButton knopf3 = null;
		JButton knopf4 = null;
		JButton knopf5 = null;
		JButton knopf6 = null;
		JButton[] but = {null,null,null,null,null};
		
		JRadioButton bootman = null;
		JRadioButton defman = null;
		ButtonGroup bgroup = new ButtonGroup();
		
		JProgressBar jprog = null;
		JPanel felderpan = null;
		
		JScrollPane jscr = null;
		
		private boolean lneu;
		private boolean ledit;
		public String neuik = null;
		public String neuname = null;
		public static SysUtilMandanten thisClass = null;
		
	public SysUtilMandanten(){
		super(new BorderLayout());
		//System.out.println("Aufruf SysUtilMandanten");
		this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 20));
		/****/
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("SystemInit"));
		/****/
	     /*
	     JLabel jlbl = new JLabel("");
	     jlbl.setIcon(new ImageIcon(Reha.proghome+"icons/werkzeug.gif"));
	     add(jlbl,BorderLayout.CENTER);
	     */
	     jscr = new JScrollPane();
	     jscr.setBorder(null);
	     jscr.setOpaque(false);
	     jscr.getViewport().setOpaque(false);
	     jscr.getVerticalScrollBar().setUnitIncrement(15);
	     felderpan = getVorlagenSeite();
	     jscr.setViewportView(felderpan);
	     jscr.validate();
	     add(jscr,BorderLayout.CENTER);
	     add(knopfpanel(), BorderLayout.SOUTH);
	     thisClass = this;
	     MandantEinlesen me = new MandantEinlesen();
	     me.setzeMandant(mandant.getSelectedIndex());
	     felderEin(false);
	     for(int i = 0 ; i < tfield.length;i++){
	    	 tfield[i].addFocusListener(this);
	     }
	     knopfGedoense(new int[] {1,1,1,0,0});
		return;
	}
	private void knopfGedoense(int[] knopfstatus){
		knopf1.setEnabled((knopfstatus[0]== 0 ? false : true));
		knopf2.setEnabled((knopfstatus[1]== 0 ? false : true));
		knopf3.setEnabled((knopfstatus[2]== 0 ? false : true));
		knopf4.setEnabled((knopfstatus[3]== 0 ? false : true));		
		knopf5.setEnabled((knopfstatus[4]== 0 ? false : true));
	}
	private void felderEin(boolean ein){
	     for(int i = 0; i < tfield.length;i++){
	    	 tfield[i].setEnabled(ein);
	    	 ((JComponent)tfield[i]).setAutoscrolls(true);
	     }
	     bula.setEnabled(ein);
	     bootman.setEnabled(ein);
	     defman.setEnabled(ein);
	}
	/************** Beginn der Methode f�r die Objekterstellung und -platzierung *********/
	private JPanel knopfpanel(){
	       
		knopf1 = new JButton("neu"); 
		knopf1.setPreferredSize(new Dimension(70, 20));
		knopf1.addActionListener(this);
		knopf1.setActionCommand("neu");
		knopf1.addKeyListener(this);
		but[0] = knopf1;

		knopf2 = new JButton("löschen");
		knopf2.setPreferredSize(new Dimension(70, 20));
		knopf2.addActionListener(this);
		knopf2.setActionCommand("loeschen");
		knopf2.addKeyListener(this);
		but[1] = knopf2;		
		
		knopf3 = new JButton("ändern");
		knopf3.setPreferredSize(new Dimension(70, 20));
		knopf3.addActionListener(this);
		knopf3.setActionCommand("aendern");
		knopf3.addKeyListener(this);
		but[2] = knopf3;
		
		knopf4 = new JButton("speichern");
		knopf4.setPreferredSize(new Dimension(70, 20));
		knopf4.addActionListener(this);		
		knopf4.setActionCommand("speichern");
		knopf4.addKeyListener(this);		
		but[3] = knopf4;		
		
		knopf5 = new JButton("abbrechen");
		knopf5.setPreferredSize(new Dimension(70, 20));
		knopf5.addActionListener(this);		
		knopf5.setActionCommand("abbrechen");
		knopf5.addKeyListener(this);
		but[4] = knopf5;		
		
		bootman = new JRadioButton("Mandantenauswahl bei Programmstart zeigen");
		bgroup.add(bootman);
		defman = new JRadioButton("Dieser Mandant soll automatisch gewählt werden");
		bgroup.add(defman);
		//defman.setEnabled(false);
		
		
		
		//                                  1.   2.    3.     4.     5.    6.    7.      8.     9.
		FormLayout lay = new FormLayout("40dlu, 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu,60dlu",
			       //1.   2.  3.   4.   5.   
					"p, 2dlu, p, 2dlu, p, 5dlu, p");
					PanelBuilder builder = new PanelBuilder(lay);
					builder.setDefaultDialogBorder();
					builder.getPanel().setOpaque(false);	
					CellConstraints cc = new CellConstraints();
		
			builder.addSeparator("", cc.xyw(1, 1, 9));
			//builder.addLabel("Mandantenauswahl bei Programmstart zeigen", cc.xyw(1, 3, 9));
			builder.add(bootman, cc.xyw(1, 3,9));
			//builder.addLabel("Mandant ist beim Programmstart vorausgew�hlt", cc.xyw(1, 5, 9));
			builder.add(defman, cc.xyw(1,5,9));
			
			builder.add(knopf1,cc.xy(1,7));
			builder.add(knopf2, cc.xy(3,7));
			builder.add(knopf3, cc.xy(5,7));
			builder.add(knopf4, cc.xy(7, 7));
			builder.add(knopf5,cc.xy(9,7));
		
		
		
		return builder.getPanel();
	}
	
	
	private JPanel getVorlagenSeite(){
       
		
		
		knopf6 = new JButton("Auswahl");
		knopf6.setPreferredSize(new Dimension(70, 20));
		knopf6.addActionListener(this);		
		knopf6.setActionCommand("auswahl");
		knopf6.addKeyListener(this);
		
		jprog = new JProgressBar();
		progressInit();
		
		mandant = new JComboBox();
		for(int i = 0;i< SystemConfig.Mandanten.size();i++){
			String slabel = SystemConfig.Mandanten.get(i)[0] + " - "+
				SystemConfig.Mandanten.get(i)[1];
			mandant.addItem(slabel);	
		}
		mandant.setActionCommand("mandwahl");
		mandant.addActionListener(this);
		
		mandantIK = new JRtaTextField("ZAHLEN", true);
		tfield[0] = mandantIK;
		mandantname = new JRtaTextField("", true);
		tfield[1] = mandantname;
		mandantname1 = new JRtaTextField("", true);
		tfield[2] = mandantname1;		
		mandantname2 = new JRtaTextField("", true);
		tfield[3] = mandantname2;		
		anrede = new JRtaTextField("", true);
		tfield[4] = anrede;
		nachname = new JRtaTextField("", true);
		tfield[5] = nachname;		
		vorname = new JRtaTextField("", true);
		tfield[6] = vorname;		
		strasse = new JRtaTextField("", true);
		tfield[7] = strasse;		
		plz = new JRtaTextField("ZAHLEN", true);
		tfield[8] = plz;
		ort = new JRtaTextField("", true);
		tfield[9] = ort;		
		telnr = new JRtaTextField("", true);
		tfield[10] = telnr;
		faxnr = new JRtaTextField("", true);
		tfield[11] = faxnr;		
		mail = new JRtaTextField("", true);
		tfield[12] = mail;		
		website = new JRtaTextField("", true);
		tfield[13] = website;		
		bank = new JRtaTextField("", true);
		tfield[14] = bank;		
		blz = new JRtaTextField("ZAHLEN", true);
		tfield[15] = blz;		
		kto = new JRtaTextField("ZAHLEN", true);
		tfield[16] = kto;		
		steuernr = new JRtaTextField("", true);
		tfield[17] = steuernr;		
		handelsreg = new JRtaTextField("", true);
		tfield[18] = handelsreg;		
		logo = new JRtaTextField("", true);
		tfield[19] = logo;		
		zusatz1 = new JRtaTextField("", true);
		tfield[20] = zusatz1;		
		zusatz2 = new JRtaTextField("", true);
		tfield[21] = zusatz2;		
		zusatz3 = new JRtaTextField("", true);
		tfield[22] = zusatz3;		
		zusatz4 = new JRtaTextField("", true);
		tfield[23] = zusatz4;	
		
		bula = new JRtaComboBox(laender);
		bula.setEnabled(true);
		//                                      1.            2.     3.      4.     5.                  6.    7.      8.     9.
		FormLayout lay = new FormLayout("right:max(80dlu;p), 4dlu,  160dlu",
       //1.  2.   3.  4.   5.  6.   7.  8.   9.  10. 11.  12.  13. 14.  15. 16.  17. 18.  19.   20. 21.  22.   23.  24  25   26  27   28  29  30    31   32   33   34   35   36  37  38   39   40   41  42    43   44   45  46   47   48   49   50    51  52   53   54   55   56   57   58
		"p, 4dlu, p, 4dlu, p, 2dlu, p, 10dlu, p, 2dlu, p, 2dlu, p,  2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p,  2dlu , p, 2dlu, p, 2dlu, p,  2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 2dlu,  p , 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p , 2dlu,  p,  2dlu, p,  2dlu, p, 10dlu");
		
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		//builder.getPanel().setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		
		builder.addLabel("Mandant wählen", cc.xy(1,1));
		builder.add(mandant, cc.xy(3,1));
		builder.addLabel("Daten eingelesen", cc.xy(1,3));
		builder.add(jprog,cc.xy(3,3));
		jprog.setVisible(true);
		builder.addLabel("Institutionskennzeichen", cc.xy(1,5));
		builder.add(mandantIK, cc.xy(3, 5));
		builder.addLabel("Mandanten Bezeichnung", cc.xy(1,7));
		builder.add(mandantname, cc.xy(3, 7));
		
		builder.addLabel("Firmenname 1", cc.xy(1,9));
		builder.add(mandantname1, cc.xy(3, 9));
		builder.addLabel("Firmenname 2", cc.xy(1,11));
		builder.add(mandantname2, cc.xy(3, 11));
		
		builder.addLabel("Anrede", cc.xy(1, 13));
		builder.add(anrede, cc.xy(3, 13));
		builder.addLabel("Nachname", cc.xy(1, 15));
		builder.add(nachname, cc.xy(3, 15));
		builder.addLabel("Vorname", cc.xy(1, 17));
		builder.add(vorname, cc.xy(3,17));
		
		builder.addLabel("Straße und Haus-Nr.", cc.xy(1,19));
		builder.add(strasse, cc.xy(3, 19));
		builder.addLabel("PLZ", cc.xy(1,21));
		builder.add(plz, cc.xy(3,21));
		builder.addLabel("Ort", cc.xy(1, 23));
		builder.add(ort, cc.xy(3,23));
		//builder.addLabel("Vorwahl", cc.xy(1,17));
		//builder.add(vorwahl, cc.xy(3,17));
		builder.addLabel("Telefon", cc.xy(1,25));
		builder.add(telnr, cc.xy(3, 25));
		builder.addLabel("Fax", cc.xy(1, 27));
		builder.add(faxnr, cc.xy(3, 27));
		
		builder.addLabel("E-Mailadresse", cc.xy(1,29));
		builder.add(mail, cc.xy(3, 29));
		builder.addLabel("Internetseite", cc.xy(1,31));
		builder.add(website, cc.xy(3,31));
		
		builder.addSeparator("Finanzen und §", cc.xyw(1,33,3));
		
		builder.addLabel("Bank", cc.xy(1,35));
		builder.add(bank, cc.xy(3, 35));
		builder.addLabel("BLZ", cc.xy(1,37));
		builder.add(blz, cc.xy(3, 37));
		builder.addLabel("Konto", cc.xy(1,39));
		builder.add(kto, cc.xy(3,39));
		builder.addLabel("Steuernummer", cc.xy(1, 41));
		builder.add(steuernr,cc.xy(3, 41));
		builder.addLabel("Handelsregistereintrag", cc.xy(1,43));
		builder.add(handelsreg, cc.xy(3, 43));
		builder.addLabel("Bundesland (Standort)", cc.xy(1,45));
		builder.add(bula, cc.xy(3,45));
		
		builder.addSeparator("Sonstiges", cc.xyw(1,47,3));
		
		builder.addLabel("Firmenlogo", cc.xy(1,49));
		builder.add(logo, cc.xy(3, 49));
		
		//builder.add(knopf6, cc.xy(3,37));

		builder.addLabel("Zusatz 1", cc.xy(1,51));
		builder.add(zusatz1, cc.xy(3, 51));
		builder.addLabel("Zusatz 2", cc.xy(1, 53));
		builder.add(zusatz2, cc.xy(3, 53));
		builder.addLabel("Zusatz 3", cc.xy(1,55));
		builder.add(zusatz3, cc.xy(3, 55)); 
		builder.addLabel("Zusatz 4", cc.xy(1, 57));
		builder.add(zusatz4, cc.xy(3, 57));

		
		return builder.getPanel();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		//System.out.println(e.getKeyCode());
		if(e.getKeyCode() == 10){
			e.consume();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() == 10){
			e.consume();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() == 10){
			e.consume();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getActionCommand().equals("mandwahl")){
			MandantEinlesen me = new MandantEinlesen();
		     me.setzeMandant(mandant.getSelectedIndex());
		}
		if(e.getActionCommand().equals("speichern")){
			if(!lneu){
				MandantSpeichern ms = new MandantSpeichern();
				ms.setzeMandant(mandant.getSelectedIndex());
				felderEin(false);
				knopfGedoense(new int[]{1,1,1,0,0});
				mandant.setEnabled(true);
			}else{
				//JOptionPane.showMessageDialog(null,"Der neue Mandant wird nun angelegt und gespeichert");
				neuSpeichern();
			}
			JViewport vp = jscr.getViewport();
			vp.setViewPosition(new Point(0,0));
			jscr.validate();
			lneu = false;
		}
		if(e.getActionCommand().equals("aendern")){
			felderEin(true);
			knopfGedoense(new int[]{0,0,0,1,1});
			mandant.setEnabled(false);
			mandantname1.requestFocus();
			//macheVerzeichnis("ini",SystemConfig.Mandanten.get(mandant.getSelectedIndex())[0]);
		}
		if(e.getActionCommand().equals("abbrechen")){
			mandant.setEnabled(true);
			abbrechen();
		}
		if(e.getActionCommand().equals("neu")){
			mandant.setEnabled(false);
			lneu = true;
			felderEin(true);
			knopfGedoense(new int[]{0,0,0,1,1});
			felderAufNull();
			neuik = "";
			neuname = "";
			NeuerMandant nm = new NeuerMandant();
			if(neuik.equals("") || neuname.equals("")){
				abbrechen();
				mandant.setEnabled(true);
				return;
			}
			mandantIK.setText(neuik);
			mandantname.setText(neuname);
			mandantIK.setEnabled(false);
			mandantname.setEnabled(false);
			mandantname1.requestFocus();
		}
		if(e.getActionCommand().equals("loeschen")){
			if(mandant.getItemCount()==1){
				JOptionPane.showMessageDialog(null,"Dies ist der einzige Mandant!\nDer letzte, bzw. einzige Mandant darf nicht gelöscht werden!!");
				return;
			}
			int imandant = mandant.getSelectedIndex();
			if(SystemConfig.Mandanten.get(imandant)[0].equals(Reha.aktIK)){
				JOptionPane.showMessageDialog(null,"Der aktuelle (aktive) darf nicht gelöscht werden");
				return;
			}
			
			int anfrage = JOptionPane.showConfirmDialog(null, "Wollen Sie den eingestellten Mandanten wirklich löschen", "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
			if(anfrage == JOptionPane.YES_OPTION){
				loeschenRegeln();
			}
		}

	}
	
	private void loeschenRegeln(){
		int aktman = mandant.getSelectedIndex();
		SystemConfig.Mandanten.remove(aktman);
		INIFile ifile = new INIFile(Reha.proghome+"ini/mandanten.ini");		
		ifile.setIntegerProperty("TheraPiMandanten", "AuswahlImmerZeigen", 1, null);
		ifile.setIntegerProperty("TheraPiMandanten", "DefaultMandant", 1, null);
		SystemConfig.AuswahlImmerZeigen = 1;
		SystemConfig.DefaultMandant = 1;
		ifile.setIntegerProperty("TheraPiMandanten", "AnzahlMandanten",SystemConfig.Mandanten.size(),null);
		for(int i = 0;i< SystemConfig.Mandanten.size();i++ ){
			ifile.setStringProperty("TheraPiMandanten", "MAND-IK"+(i+1),SystemConfig.Mandanten.get(i)[0],null);
			ifile.setStringProperty("TheraPiMandanten", "MAND-NAME"+(i+1),SystemConfig.Mandanten.get(i)[1],null);
		}
		ifile.save();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//ifile = null;
		SystemConfig.MandantenEinlesen();
		mandant.removeItemAt(aktman);
		mandant.setSelectedIndex(0);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		MandantEinlesen me = new MandantEinlesen();
	    me.setzeMandant(0);

	    mandant.setEnabled(true);


	}
	private void neuSpeichern(){
		macheVerzeichnis("ini",neuik);
		macheVerzeichnis("vorlagen",neuik);
		macheVerzeichnis("temp",neuik);	
		macheVerzeichnis("edifact",neuik);
		macheVerzeichnis("keystore",neuik);
		
		INIFile inif = new INIFile(Reha.proghome+"ini/mandanten.ini");
		int AnzahlMandanten = inif.getIntegerProperty("TheraPiMandanten", "AnzahlMandanten")+1;
		//int AnzahlMandanten = mandant.getItemCount()+1;
		inif.setStringProperty("TheraPiMandanten", "AnzahlMandanten",new Integer(AnzahlMandanten).toString(),null);
		inif.setStringProperty("TheraPiMandanten", "MAND-IK"+AnzahlMandanten,neuik,null);
		inif.setStringProperty("TheraPiMandanten", "MAND-NAME"+AnzahlMandanten,neuname,null);
		if(defman.isSelected()){
			inif.setStringProperty("TheraPiMandanten", "AuswahlImmerZeigen","1",null);
			inif.setStringProperty("TheraPiMandanten", "DefaultMandant",new Integer(AnzahlMandanten).toString(),null);
		}else{
			inif.setStringProperty("TheraPiMandanten", "AuswahlImmerZeigen","0",null);			
		}
		//JOptionPane.showMessageDialog(null,"Es sind nun ingsgesamt - " + AnzahlMandanten + " - gespeichert");
		inif.save();
		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String[] mand = {null,null};
		mand[0] = neuik;
		mand[1] = neuname;
		SystemConfig.Mandanten.add(mand.clone());
		String slabel = neuik + " - "+ neuname;
		
		MandantSpeichern ms = new MandantSpeichern();
		ms.setzeMandant(AnzahlMandanten-1);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mandant.addItem(slabel);
		mandant.setSelectedItem(slabel);

		felderEin(false);
		SystemConfig.MandantenEinlesen();
		knopfGedoense(new int[]{1,1,1,0,0});
		MandantEinlesen me = new MandantEinlesen();
	    me.setzeMandant(mandant.getSelectedIndex());
	    mandant.setEnabled(true);
	    String cmd = "insert into nummern set mandant='"+neuik+"'";

	    new ExUndHop().setzeStatement(cmd);
	}
	

	private void abbrechen(){
		if(!lneu){
			mandant.setEnabled(true);
			MandantEinlesen me = new MandantEinlesen();
		    me.setzeMandant(mandant.getSelectedIndex());
			felderEin(false);
			knopfGedoense(new int[]{1,1,1,0,0});
		}else{
			mandant.setSelectedIndex(0);
			MandantEinlesen me = new MandantEinlesen();
		    me.setzeMandant(mandant.getSelectedIndex());
			felderEin(false);
			knopfGedoense(new int[]{1,1,1,0,0});
		}
		JViewport vp = jscr.getViewport();
		vp.setViewPosition(new Point(0,0));
		jscr.validate();
		lneu = false;
	}
	private void progressInit(){
		jprog.setMinimum(0);
		jprog.setMaximum(24);
		jprog.setStringPainted(true);
	}
	private void felderAufNull(){
		String[] stitel = {"Ik","Ikbezeichnung","Firma1","Firma2","Anrede","Nachname","Vorname",
				"Strasse","Plz","Ort","Telefon","Telefax","Email","Internet","Bank","Blz","Kto",
				"Steuernummer","Hrb","Logodatei","Zusatz1","Zusatz2","Zusatz3","Zusatz4"};
		for(int i = 0; i < stitel.length;i++){
			SysUtilMandanten.thisClass.tfield[i].setText("");
		}
		bula.setSelectedIndex(0);
		//bootman.setSelected(false);
		defman.setSelected(false);
	}
	private void macheVerzeichnis(String vz, String sIK){
		File verzeichnis = new File(Reha.proghome+vz+"/"+sIK);
		verzeichnis.mkdir();
		File defvz = new File(Reha.proghome+"defaults/"+vz);
		String[] dateien = defvz.list();
		for(int i = 0 ;i < dateien.length;i++){
			try {
				copyFile(new File(Reha.proghome+"defaults/"+vz+"/"+dateien[i]),
						new File(Reha.proghome+vz+"/"+sIK+"/"+dateien[i]),1024,true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	public static void copyFile(File src, File dest, int bufSize,
	        boolean force) throws IOException {
	    if(dest.exists()) {
	        if(force) {
	            dest.delete();
	        } else {
	            throw new IOException(
	                    "Kann existierende Datei nicht überschreiben: " + dest.getName());
	        }
	    }
	    byte[] buffer = new byte[bufSize];
	    int read = 0;
	    InputStream in = null;
	    OutputStream out = null;
	    try {
	        in = new FileInputStream(src);
	        out = new FileOutputStream(dest);
	        while(true) {
	            read = in.read(buffer);
	            if (read == -1) {
	                //-1 bedeutet EOF
	                break;
	            }
	            out.write(buffer, 0, read);
	        }
	    } finally {
	        // Sicherstellen, dass die Streams auch
	        // bei einem throw geschlossen werden.
	        // Falls in null ist, ist out auch null!
	        if (in != null) {
	            //Falls tats�chlich in.close() und out.close()
	            //Exceptions werfen, die jenige von 'out' geworfen wird.
	            try {
	                in.close();
	            }catch(Exception ex){
	            	
	            }
	            finally {
	                if (out != null) {
	                	out.flush();
	                    out.close();
	                }
	            }
	        }
	    }
	}
	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getSource() instanceof JRtaTextField){
			Rectangle rec1 =((JComponent)arg0.getSource()).getBounds();
			//System.out.println("Rec1 = "+rec1);
			Rectangle rec2 = jscr.getViewport().getViewRect();
			JViewport vp = jscr.getViewport();
			Rectangle rec3 = vp.getVisibleRect();
			//vp.scrollRectToVisible(rec1);
			if(rec1.y > (rec2.y+rec2.height)){
				vp.setViewPosition(new Point(rec1.x,(rec2.y+rec2.height)-rec1.height));
			}
			if(rec1.y < (rec2.y)){
				vp.setViewPosition(new Point(rec1.x,rec1.y));
				}

			jscr.validate();
			//vp.s
			////System.out.println("Rec2 = "+rec2);
			//jscr.scrollRectToVisible(rec1);
			
			//((JComponent)arg0.getSource()).scrollRectToVisible(rec2);
			//felderpan.g
			
		}
	}
	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

/*
	String verz= "";
  	try{
  	VerzErst uebergabe = new VerzErst();
  	verz = uebergabe.eingeben();		
  	File verzeichnis = new File(verz);	
  	verzeichnis.mkdirs();	
  	}
  	catch (Exception e){
  		//System.out.println ( "Fehler: " +e.getMessage());
  	}	
  	}//Ende erstellen
  	
  	File f = new File(PfadZumVerzeichnis)
  	f.delete()
*/


}
class MandantEinlesen extends SwingWorker<Integer,Void>{
	private int mandant;
	public void setzeMandant(int xmandant){
		this.mandant = xmandant;
		execute();
	}

	@Override
	protected Integer doInBackground() throws Exception {
		// TODO Auto-generated method stub
		//int man = this.mandant;
		INIFile ifile = new INIFile(Reha.proghome+"ini/"+SystemConfig.Mandanten.get(this.mandant)[0]+"/firmen.ini");
		
		String[] stitel = {"Ik","Ikbezeichnung","Firma1","Firma2","Anrede","Nachname","Vorname",
						"Strasse","Plz","Ort","Telefon","Telefax","Email","Internet","Bank","Blz","Kto",
						"Steuernummer","Hrb","Logodatei","Zusatz1","Zusatz2","Zusatz3","Zusatz4"};
		
		for(int i = 0; i < stitel.length;i++){
			SysUtilMandanten.thisClass.jprog.setValue(i+1);
			SysUtilMandanten.thisClass.tfield[i].setText(ifile.getStringProperty("Firma", stitel[i]));
		}
		SysUtilMandanten.thisClass.bula.setSelectedItem((String)ifile.getStringProperty("Firma", "Bundesland") );
		if(SystemConfig.AuswahlImmerZeigen==1){
			SysUtilMandanten.thisClass.bootman.setSelected(true);
		}else if(SystemConfig.AuswahlImmerZeigen==0 && SystemConfig.DefaultMandant==(this.mandant+1)){
			SysUtilMandanten.thisClass.defman.setSelected(true);
		}else{
			SysUtilMandanten.thisClass.bootman.setSelected(false);
			SysUtilMandanten.thisClass.defman.setSelected(false);
		}
		//ifile = null;
		return null;
	}
}
class MandantSpeichern extends SwingWorker<Integer,Void>{
	private int mandant;
	public void setzeMandant(int xmandant){
		this.mandant = xmandant;
		execute();
	}

	@Override
	protected Integer doInBackground() throws Exception {
		try{
			INIFile ifile = new INIFile(Reha.proghome+"ini/"+SystemConfig.Mandanten.get(this.mandant)[0]+"/firmen.ini");
			String[] stitel = {"Ik","Ikbezeichnung","Firma1","Firma2","Anrede","Nachname","Vorname",
							"Strasse","Plz","Ort","Telefon","Telefax","Email","Internet","Bank","Blz","Kto",
							"Steuernummer","Hrb","Logodatei","Zusatz1","Zusatz2","Zusatz3","Zusatz4"};
			
			for(int i = 0; i < stitel.length;i++){
				ifile.setStringProperty("Firma", stitel[i],SysUtilMandanten.thisClass.tfield[i].getText().trim() , null);
			}
			ifile.setStringProperty("Firma", "Bundesland",((String)SysUtilMandanten.thisClass.bula.getSelectedItem()).trim() , null);
			//ifile = null;
			ifile.save();
			
			ifile = new INIFile(Reha.proghome+"ini/mandanten.ini");		
			if(SysUtilMandanten.thisClass.bootman.isSelected()){
				ifile.setIntegerProperty("TheraPiMandanten", "AuswahlImmerZeigen", 1, null);
				SystemConfig.AuswahlImmerZeigen = 1;
				SystemConfig.DefaultMandant = 1;
			}else{
				ifile.setIntegerProperty("TheraPiMandanten", "AuswahlImmerZeigen", 0, null);
				SystemConfig.AuswahlImmerZeigen = 0;
			}
			if(SysUtilMandanten.thisClass.defman.isSelected()){
				ifile.setIntegerProperty("TheraPiMandanten", "DefaultMandant", this.mandant+1, null);
				SystemConfig.DefaultMandant = this.mandant+1;
			}
			ifile.save();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		/*
		ifile.setIntegerProperty("TheraPiMandanten", "AnzahlMandanten", SysUtilMandanten.thisClass.mandant.getItemCount(), null);
		ifile.save();
		*/
		//ifile = null;
	
		return null;
	}
}