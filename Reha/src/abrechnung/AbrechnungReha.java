package abrechnung;

import hauptFenster.AktiveFenster;
import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import oOorgTools.OOTools;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;

import patientenFenster.KassenAuswahl;
import rehaInternalFrame.JRehaabrechnungInternal;
import sqlTools.SqlInfo;
import stammDatenTools.PatTools;
import stammDatenTools.RezTools;
import systemEinstellungen.SystemConfig;
import systemEinstellungen.SystemPreislisten;
import systemTools.JRtaCheckBox;
import systemTools.JRtaComboBox;
import systemTools.JRtaTextField;
import systemTools.StringTools;
import terminKalender.DatFunk;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.ITextTable;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;
import ag.ion.noa.internal.printing.PrintProperties;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dta301.RVMeldung301;
import events.PatStammEvent;
import events.PatStammEventClass;

public class AbrechnungReha extends JXPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7029656388659186476L;

	/**
	 * 
	 */
	JRehaabrechnungInternal internal = null;
	JXPanel content;
	JLabel patLabel = null;
	JLabel kassenLabel = null;
	JLabel kasseGesamt = null;
	JLabel patGesamt = null;
	JRtaTextField[] tfanzahl = {null,null,null,null};
	JRtaTextField[] tfpreis = {null,null,null,null};
	JRtaTextField[] tfgesamt = {null,null,null,null};
	JRtaComboBox[] jcmb = {null,null,null,null};
	JRtaTextField[] tfrehanr = {null};
	
	JRtaCheckBox[] jckb = {null,null};
	JRtaTextField[] tfpatanzahl = {null};
	JRtaTextField[] tfpatpreis = {null};
	JRtaTextField[] tfpatgesamt = {null};
	JRtaComboBox[] jcmbpat = {null};

	JButton[] jbut = {null,null,null,null,null,null};
	Vector<Vector<String>> rehavec = new Vector<Vector<String>>(); 
	Vector<Vector<String>> kassvec = new Vector<Vector<String>>();
	Vector<Vector<String>> rechnungvec = new Vector<Vector<String>>();
	Vector<Vector<String>> patvec = new Vector<Vector<String>>();
	Vector<String> tagevec = new Vector<String>();

	DecimalFormat dcf = new DecimalFormat("####0.00");
	BigDecimal[] zeilenPreis = {BigDecimal.valueOf(Double.parseDouble("0.00")),
			BigDecimal.valueOf(Double.parseDouble("0.00")),BigDecimal.valueOf(Double.parseDouble("0.00")),
			BigDecimal.valueOf(Double.parseDouble("0.00"))
	};
	BigDecimal gesamtPreis = BigDecimal.valueOf(Double.parseDouble("0.00"));
	BigDecimal gesamtPatPreis = BigDecimal.valueOf(Double.parseDouble("0.00"));
	boolean initOk = false;
	boolean abrechnungOk = true;
	boolean istprivat = false;
	ActionListener al;
	KeyListener kl;

	
	ITextTable textTable = null;
	ITextTable textEndbetrag = null;
	ITextDocument textDocument = null;
	int aktuellePosition = 0;


	String aktRechnung;
	int druckExemplare = 0;
	String druckDrucker = "";
	String druckFormular = "";
	String druckIk = "";
	String anschriftID = "";
	StringBuffer rechnungBuf = new StringBuffer();
	HashMap<String,String> hmRechnung = new HashMap<String,String>();
	Vector<Vector<String>> vecposrechnung = new Vector<Vector<String>>();
	
	public AbrechnungReha(JRehaabrechnungInternal rai){
		super();
		this.internal = rai;
		this.setName(this.internal.getName());
		this.setLayout(new BorderLayout());
		this.createListener();
		this.add(getContent(),BorderLayout.CENTER);
		this.add(getButtons(),BorderLayout.SOUTH);
		this.validate();
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				for(int i = 0;i<4;i++){
					if(i==0){
						tfanzahl[i].addKeyListener(kl);
						tfanzahl[i].setName("tfanzahl_"+Integer.toString(i));
						tfpreis[i].addKeyListener(kl);
						tfpreis[i].setName("tfpreis_"+Integer.toString(i));
						tfpatanzahl[i].addKeyListener(kl);
						tfpatanzahl[i].setName("tfpatanzahl"+Integer.toString(i));
						tfpatpreis[i].addKeyListener(kl);
						tfpatpreis[i].setName("tfpatpreis"+Integer.toString(i));
					}else{
						tfanzahl[i].addKeyListener(kl);
						tfanzahl[i].setName("tfanzahl_"+Integer.toString(i));						
						tfpreis[i].addKeyListener(kl);
						tfpreis[i].setName("tfpreis_"+Integer.toString(i));
					}
				}
				return null;
			}
			
		}.execute();
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				setzeFocus();
			}
		});
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					JComponent patient = AktiveFenster.getFensterAlle("PatientenVerwaltung");
					if(Reha.thisClass.patpanel == null){
						return null;
					}
					if(  (patient != null)  && (Reha.thisClass.patpanel.vecaktrez.size() > 0) ){
						if(Reha.thisClass.patpanel.vecaktrez.get(1).startsWith("RH")){
							tfrehanr[0].setText(Reha.thisClass.patpanel.vecaktrez.get(1));
							doEinlesen(Reha.thisClass.patpanel.vecaktrez.get(1));
							doRechnen();
							initOk = true;
						}
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}
		}.execute();
	}
	
	private void setzeFocus(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				tfrehanr[0].requestFocus();
			}
		});
	}
	private JButton macheBut(String titel,String cmd){
		JButton but = new JButton(titel);
		but.setName(cmd);
		but.setActionCommand(cmd);
		but.addActionListener(al);
		return but;
	}
	private JRtaComboBox macheCombo(String name){
		JRtaComboBox box = new JRtaComboBox();
		box.setName(name);
		box.setActionCommand(name);
		box.addActionListener(al);
		return box;
	}
	private JXPanel getButtons(){
		FormLayout lay = new FormLayout(
		//     1           2    3    4     5     6     7                 8
		"fill:0:grow(0.5),80dlu,5dlu,80dlu,5dlu,80dlu,fill:0:grow(0.5)",
		"10dlu,p,10dlu"
		);
		CellConstraints cc = new CellConstraints();
		JXPanel jpan = new JXPanel();
		jpan.setLayout(lay);
		jpan.add((jbut[0] = macheBut("drucken & buchen","drucken")),cc.xy(2,2));
		jpan.add((jbut[1] = macheBut("Korrektur","korrektur")),cc.xy(4,2));
		jpan.add((jbut[2] = macheBut("abbrechen","abbrechen")),cc.xy(6,2));
		
		return jpan;
	}

	private JXPanel getContent(){
		FormLayout lay = new FormLayout(
		//     1           2    3    4     5     6    7     8
		"fill:0:grow(0.5),40dlu,5dlu,120dlu,5dlu,40dlu,5dlu,40dlu,fill:0:grow(0.5)",
		//  1  2  3    4  5   6  7   8  9  10 11  12  13 14  15  16 17 18 19   20
		"15dlu,p,15dlu,p,2dlu,p,5dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,15dlu,p,5dlu,p,2dlu,p"
		);
		CellConstraints cc = new CellConstraints();
		content = new JXPanel();
		content.setLayout(lay);
		JLabel lab = new JLabel("RehaNr. einlesen");
		content.add(lab,cc.xy(2,2));
		tfrehanr[0] = new JRtaTextField("GROSS",true);
		content.add(tfrehanr[0],cc.xy(4,2));
		content.add((jbut[4] = macheBut("einlesen","einlesen")),cc.xy(6,2));
		content.add((jbut[5] = macheBut("Anschrift","anschrift")),cc.xy(8,2));
		patLabel = new JLabel(" ");
		patLabel.setForeground(Color.BLUE);
		content.add(patLabel,cc.xyw(2,4,7));
		kassenLabel = new JLabel(" ");
		kassenLabel.setForeground(Color.BLUE);
		content.add(kassenLabel,cc.xyw(2,6,7));
		
		for(int i = 0; i < 4;i++){
			tfanzahl[i] = new JRtaTextField("ZAHLEN",true);
			content.add(tfanzahl[i],cc.xy(2,8+(i*2)));
			content.add((jcmb[i]=macheCombo("combo_"+Integer.toString(i))),cc.xy(4, 8+(i*2)));
			tfpreis[i] = new JRtaTextField("FL",true,"6.2","RECHTS");
			content.add(tfpreis[i],cc.xy(6,8+(i*2)));
			tfgesamt[i] = new JRtaTextField("FL",true,"6.2","RECHTS");
			tfgesamt[i].setEditable(false);
			content.add(tfgesamt[i],cc.xy(8,8+(i*2)));
		}
		lab = new JLabel("Kassenrechnung Summe :");
		lab.setForeground(Color.BLUE);
		content.add(lab,cc.xy(4,16));
		kasseGesamt = new JLabel("0,00");
		kasseGesamt.setForeground(Color.RED);
		kasseGesamt.setHorizontalAlignment(JLabel.RIGHT);
		content.add(kasseGesamt,cc.xy(8,16));
		
		jckb[1] = new JRtaCheckBox("nach §301 abrechnen und zusätzlich 1 Original drucken");
		jckb[1].setOpaque(false);
		jckb[1].setForeground(Color.BLUE);
		content.add(jckb[1] ,cc.xyw(4,18,5));
		
		jckb[0] = new JRtaCheckBox("zusätzlich Eigenanteile der Patienten abrechnen");
		jckb[0].setOpaque(false);
		jckb[0].setActionCommand("auchpatliquidieren");
		jckb[0].addActionListener(al);
		content.add(jckb[0] ,cc.xyw(2,20,7));
		tfpatanzahl[0]= new JRtaTextField("ZAHLEN",true);
		tfpatanzahl[0].setEnabled(false);
		content.add(tfpatanzahl[0] ,cc.xy(2,22));
		jcmbpat[0]=macheCombo("combopat");
		jcmbpat[0].setEnabled(false);
		content.add(jcmbpat[0],cc.xy(4, 22));
		tfpatpreis[0] = new JRtaTextField("FL",true,"6.2","RECHTS");
		tfpatpreis[0].setEnabled(false);
		content.add(tfpatpreis[0],cc.xy(6,22));
		tfpatgesamt[0] = new JRtaTextField("FL",true,"6.2","RECHTS");
		tfpatgesamt[0].setEditable(false);
		content.add(tfpatgesamt[0],cc.xy(8,22));
		lab = new JLabel("Patientenrechung Summe :");
		lab.setForeground(Color.BLUE);
		content.add(lab,cc.xy(4,24));
		patGesamt = new JLabel("0,00");
		patGesamt.setForeground(Color.RED);
		patGesamt.setHorizontalAlignment(JLabel.RIGHT);
		content.add(patGesamt,cc.xy(8,24));
		content.validate();
		return content;
	}
	private void reglePreise(int preisnummer){
		if(jcmb[preisnummer].getSelectedIndex() > 0){
			tfpreis[preisnummer].setText(((String)jcmb[preisnummer].getValueAt(3)).replace(".", ","));			
		}else{
			tfpreis[preisnummer].setText("0,00");
			tfanzahl[preisnummer].setText("0");
		}

	}
	private void reglePreisePat(){
		if(jcmbpat[0].getSelectedIndex() > 0){
			tfpatpreis[0].setText(((String)jcmbpat[0].getValueAt(3)).replace(".", ","));			
		}else{
			tfpatpreis[0].setText("0,00");
			tfpatanzahl[0].setText("0");
		}

	}

	private void doRechnen(){
		gesamtPreis = BigDecimal.valueOf(Double.parseDouble("0.00"));
		for(int i = 0; i < 4;i++){
			zeilenPreis[i] = BigDecimal.valueOf(Double.parseDouble(tfanzahl[i].getText().trim()+".00")).multiply(
					BigDecimal.valueOf(Double.parseDouble(tfpreis[i].getText().replace(",","."))));
			tfgesamt[i].setText(dcf.format(zeilenPreis[i].doubleValue()).replace(".",","));
			gesamtPreis = gesamtPreis.add(zeilenPreis[i]);
		}
		kasseGesamt.setText(dcf.format(gesamtPreis.doubleValue()));
	}
	private void doRechnenPat(){
		gesamtPatPreis = BigDecimal.valueOf(Double.parseDouble(tfpatanzahl[0].getText().trim()+".00")).multiply(
				BigDecimal.valueOf(Double.parseDouble(tfpatpreis[0].getText().replace(",","."))));
		tfpatgesamt[0].setText(dcf.format(gesamtPatPreis.doubleValue()));
		patGesamt.setText(tfpatgesamt[0].getText());
	}
	 
		
	private void doEinlesen(String rehanummer){
		String suchenach = rehanummer;
		if(rehanummer.equals("")){
			JOptionPane.showMessageDialog(null, "Witzbolde gibt's....");
			return;
		}	
		if(!rehanummer.startsWith("RH")){
			tfrehanr[0].setText("RH"+rehanummer);
			suchenach = "RH"+rehanummer;
		}
		rehavec.clear();
		tagevec.clear();
		abrechnungOk = true;
		rehavec = SqlInfo.holeFelder("select * from verordn where rez_nr='"+suchenach+"' LIMIT 1");
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				holePatientUndKostentraeger(rehavec.get(0).get(0).trim(),rehavec.get(0).get(37).trim());
				holeRechnungsAnschrift(rehavec.get(0).get(37).trim());
				return null;
			}
			
		}.execute();
		if(rehavec.size()<=0){
			JOptionPane.showMessageDialog(null, "Reha-Verordnung mit Nummer "+suchenach+" wurde nicht gefunden");
			return;
		}
		int preisgruppe = Integer.parseInt(rehavec.get(0).get(41));
		String artdbeh;
		for(int i = 0;i < 4;i++){
			jcmb[i].setDataVectorWithStartElement(SystemPreislisten.hmPreise.get("Reha").get(preisgruppe-1), 0, 9, "./.");
			artdbeh = rehavec.get(0).get(8+i);
			if(!artdbeh.equals("0")){
				tfanzahl[i].setText(rehavec.get(0).get(3+i));
				jcmb[i].setSelectedVecIndex(9,artdbeh);
			}else{
				tfanzahl[i].setText("0");
			}
		}
		jcmbpat[0].setDataVectorWithStartElement(SystemPreislisten.hmPreise.get("Reha").get(preisgruppe-1), 0, 9, "./.");
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					tagevec = RezTools.holeEinzelTermineAusRezept("", rehavec.get(0).get(34));
					if(Integer.parseInt(tfanzahl[0].getText().trim()) != tagevec.size()){
						String meldung = "Achtung!\nDie Anzahl der erfaßten Einzeltermine stimmt nicht mit der\n"+
						"Anzahl der Verordnung überein!\n\n"+
						"Anzahl der erfaßten Einzeltermine = "+Integer.toString(tagevec.size())+"\n"+
						"Anzahl laut Angaben auf der Verordnung = "+tfanzahl[0].getText().trim()+"\n\n"+
						"Eine Fortsetzung der Abrechnung ist nicht empfehlenswert\n";
						JOptionPane.showMessageDialog(internal, meldung);
					}
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null, "Fehler beim Bezug der Behandlungstermine!\nFortsetzung der Abrechnung ist nicht empfehlenswert.");
					return null;
				}

				return null;
			}
			
		}.execute();
	}
	
	
	@SuppressWarnings("unchecked")
	private void holePatientUndKostentraeger(String pat_intern,String kassenid){
		patvec = SqlInfo.holeFelder("select n_name,v_name,geboren,anrede,titel,strasse,plz,ort,kv_status from pat5 where pat_intern='"+pat_intern+"' LIMIT 1");
		kassvec = SqlInfo.holeFelder("select kassen_nam1,kassen_nam2,strasse,plz,ort,ik_papier,ik_kostent from kass_adr where id='"+kassenid+"' LIMIT 1");
		rechnungvec = (Vector<Vector<String>>)kassvec.clone();
		anschriftID = rehavec.get(0).get(37).trim().trim();
		if(patvec.size()<=0){
			patLabel.setText("Patient wurde nicht gefunden");
			abrechnungOk = false;
			return;
		}
		if(kassvec.size()<=0){
			patLabel.setText("Kostenträger wurde nicht gefunden");
			abrechnungOk = false;
			return;
		}
		istprivat = kassvec.get(0).get(4).trim().equals("");
		patLabel.setText(
				StringTools.EGross(patvec.get(0).get(0))+", "+
				StringTools.EGross(patvec.get(0).get(1))+", geb.am: "+
				DatFunk.sDatInDeutsch(patvec.get(0).get(2))+(istprivat ? " - privat" : "")
				);
		doKassenLabel();
				
	}
	private void controlsEinschalten(boolean einschalten){
		if(einschalten){
			tfpatanzahl[0].setEnabled(true);
			tfpatanzahl[0].setText("0");
			tfpatpreis[0].setEnabled(true);
			tfpatpreis[0].setText("0,00");
			tfpatgesamt[0].setText("0,00");
			jcmbpat[0].setEnabled(true);		
		}else{
			tfpatanzahl[0].setEnabled(false);
			tfpatanzahl[0].setText("");
			tfpatpreis[0].setEnabled(false);
			tfpatpreis[0].setText("");
			tfpatgesamt[0].setText("");
			if(jcmbpat[0].getItemCount()>0){
				jcmbpat[0].setSelectedIndex(0);
			}
			jcmbpat[0].setEnabled(false);	
			patGesamt.setText("0,00");
		}
	}
	private void allesAusschalten(){
		for(int i = 0;i<4;i++){
			if(i==0){
				tfpatanzahl[i].setEnabled(false);
				tfpatanzahl[i].setText("");
				tfpatpreis[i].setEnabled(false);
				tfpatpreis[i].setText("");
				tfpatgesamt[i].setText("");
				jcmbpat[i].removeAllItems();
				jcmbpat[i].setEnabled(false);
				tfanzahl[i].setText("");
				tfpreis[i].setText("");
				tfgesamt[i].setText("");
				jcmb[i].removeAllItems();
			}else{
				tfanzahl[i].setText("");
				tfpreis[i].setText("");
				tfgesamt[i].setText("");
				jcmb[i].removeAllItems();				
			}
		}
		jckb[1].setSelected(false);
		jckb[0].setSelected(false);
		patGesamt.setText(" ");
		kasseGesamt.setText(" ");
		patLabel.setText(" ");
		kassenLabel.setText(" "); 
		this.initOk = false;
		setzeFocus();
	}
	
	private void createListener(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("einlesen")){
					initOk = false;
					jckb[0].setSelected(false);
					controlsEinschalten(false);
					doEinlesen(tfrehanr[0].getText().trim());
					doRechnen();
					initOk = true;
					return;
				}
				if(cmd.contains("combo_")){
					reglePreise(Integer.parseInt(cmd.split("_")[1]));
					if(initOk){
						doRechnen();
						return;
					}
				}
				if(cmd.contains("auchpatliquidieren")){
					controlsEinschalten(jckb[0].isSelected());
					tfpatanzahl[0].requestFocus();
				}
				if(cmd.contains("combopat")){
					reglePreisePat();
					if(initOk){
						doRechnenPat();
						return;
					}
				}
				if(cmd.contains("abbrechen")){
					allesAusschalten();
				}
				if(cmd.contains("korrektur")){
					doKorrektur();
					//allesAusschalten();
				}
				if(cmd.contains("drucken")){
					aktuellePosition = 0;
					doRehaAbrechnung();
					//allesAusschalten();
				}
				if(cmd.contains("anschrift")){
					doAnschrift();
				}
				
			}
		};
		kl = new KeyListener(){
			@Override
			public void keyPressed(KeyEvent arg0) {
			}
			@Override
			public void keyReleased(KeyEvent arg0) {
				if(!initOk){
					return;
				}
				if ( ((JComponent)arg0.getSource()).getName().contains("_")){
					doRechnen();					
				}else{
					doRechnenPat();					
				}
			}
			@Override
			public void keyTyped(KeyEvent arg0) {
			}
			
		};
	}
	private void doKassenLabel(){
		kassenLabel.setText(rechnungvec.get(0).get(0)+" "+
				rechnungvec.get(0).get(1)+" "+rechnungvec.get(0).get(2)+" "+
				rechnungvec.get(0).get(3)+" "+rechnungvec.get(0).get(4));
	}


	private void doKorrektur(){
		JOptionPane.showMessageDialog(null, "Korrektur-Funktion noch nicht implementiert");
	}
	private AbrechnungReha getInstance(){
		return this;
	}
	private void holeRechnungsAnschrift(String id){
		rechnungvec = SqlInfo.holeFelder("select kassen_nam1,kassen_nam2,strasse,plz,ort,ik_papier,ik_kostent from kass_adr where id='"+id+"' LIMIT 1");
		doKassenLabel();
	}

	private void doAnschrift(){
		//KassenAuswahl(JXFrame owner, String name,String[] suchegleichnach,JRtaTextField[] elterntf,String kassennum){
		JRtaTextField[] tfs = {null,null,null};
		tfs[0] = new JRtaTextField("nix",false);
		tfs[1] = new JRtaTextField("nix",false);
		tfs[2] = new JRtaTextField("nix",false);
		tfs[1].setText(anschriftID);

		//System.out.println(tfs[1].getText());
		//this.suchkrit = suchegleichnach[0];
		//this.suchid = suchegleichnach[1];

		String[] suchegleichnach = {null,null};
		suchegleichnach[0] = "";
		suchegleichnach[1] = "";
		Point p = jbut[5].getLocationOnScreen();
		KassenAuswahl kwahl = new KassenAuswahl(null,"RechnungsAnschrift",suchegleichnach,tfs,"");
		kwahl.setLocation(p.x-50,p.y+30);
		kwahl.fuelleIdTabelle(tfs[1].getText());
		kwahl.pack();
		kwahl.setModal(true);
		kwahl.setVisible(true);
		if(! tfs[0].getText().contains("***")){
			if(! tfs[2].getText().trim().equals(anschriftID)){
				anschriftID = String.valueOf(tfs[2].getText().trim());
				holeRechnungsAnschrift(anschriftID);
			}
		}
		for(int i = 0; i < 3; i++){
			tfs[i].listenerLoeschen();
			tfs[i] = null;
		}
	}
	private void doRehaAbrechnung(){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try {
					getInstance().setCursor(Reha.thisClass.wartenCursor);
					if(abrechnungOk){
						//System.out.println("AbrechnungOK-1 = "+abrechnungOk);
						doHauptRechnungDrucken();
						//System.out.println("AbrechnungOK-2 = "+abrechnungOk);
						doFaktura();
						//System.out.println("AbrechnungOK-3 = "+abrechnungOk);
						doAnlegenOP();
						//System.out.println("AbrechnungOK-4 = "+abrechnungOk);
						if(jckb[0].isSelected()){
							Thread.sleep(300);
							doEigenanteilDrucken();
							doFaktura();
							doAnlegenOP();
						}

						if(jckb[1].isSelected()){
							abrechnungOk = do301er();
						}
						if(!abrechnungOk){
							JOptionPane.showConfirmDialog(null, "Während der Rechnungserstellung ist ein Fehler aufgetreten!");
						}else{
							if(Reha.vollbetrieb){
								doUebertragen();								
							}
						}
						allesAusschalten();
					}else{
						JOptionPane.showConfirmDialog(null, "Die Abrechnung enthält Fehler. Bitte beheben Sie die Fehler und starten Sie die RehaAbrechnung erneut");
						allesAusschalten();
					}
					getInstance().setCursor(Reha.thisClass.cdefault);
				} catch (Exception e) {
					e.printStackTrace();
					getInstance().setCursor(Reha.thisClass.cdefault);
					JOptionPane.showMessageDialog(null, "Fehler bei der Abrechnung\n"+e.getMessage());
					e.printStackTrace();
					allesAusschalten();
				}
				return null;
			}
		}.execute();
	}
	private boolean do301er(){
		try{
			Object[] obj = {null,null,null};
			Vector<Object[]> objvec = new Vector<Object[]>();
			for(int i = 0; i < 4; i++){
				if(tfanzahl[i].getText().trim().equals("0")){
					break;
				}else{
					obj[0] = tfanzahl[i].getText().trim();
					obj[1] = tfpreis[i].getText().trim();
					obj[2] = jcmb[i].getValueAt(2).toString().trim();
					objvec.add(obj.clone());
					//System.out.println(obj[0]+" - "+obj[1]+" - "+obj[2]);
				}
			}
			if(objvec.size() <= 0){
				JOptionPane.showMessageDialog(null, "Keine Positionen angegeben, keine Abrechnung möglich - ganz einfach!");
				return false;
			}
			String id = SqlInfo.holeEinzelFeld("select id from dta301 where pat_intern='"+
					rehavec.get(0).get(0)+"' and rez_nr='"+rehavec.get(0).get(1)+"' and nachrichtentyp='1' LIMIT 1");
			if(id.equals("")){
				JOptionPane.showMessageDialog(null, "Dieser Fall wurde nicht als DTA-301 Fall angelegt\nund kann deshalb auch nicht nach 301 abgerechnet werden!");
				return true;
			}
			//System.out.println("Gesamtbetrag = "+dcf.format(gesamtPreis.doubleValue()) );
			//System.out.println("ID von dta301 = "+id );
			RVMeldung301 meldung = new RVMeldung301(11,id);
			meldung.doRechnung(tagevec.get(0), tagevec.get(tagevec.size()-1), objvec, dcf.format(gesamtPreis.doubleValue()));
			return true;
		}catch(Exception ex){ex.printStackTrace();return false;}
		
	}
	private void doUebertragen(){
		String delrez = String.valueOf(rehavec.get(0).get(1));
		SqlInfo.transferRowToAnotherDB("verordn", "lza","rez_nr", delrez, true, Arrays.asList(new String[] {"id"}));
		SqlInfo.sqlAusfuehren("delete from verordn where rez_nr='"+delrez+"'");
		String aktiverPatient = "";
		//System.out.println("delete from volle where rez_nr='"+delrez+"'");
		SqlInfo.sqlAusfuehren("delete from volle where rez_nr='"+delrez+"'");
		JComponent patient = AktiveFenster.getFensterAlle("PatientenVerwaltung");
		if(patient != null){
			aktiverPatient = Reha.thisClass.patpanel.aktPatID;
		}
		if(aktiverPatient.equals(rehavec.get(0).get(0))){
			posteAktualisierung(rehavec.get(0).get(0));
		}
	}
	private void posteAktualisierung(String patid){
		final String xpatid = patid;
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				String s1 = String.valueOf("#PATSUCHEN");
				String s2 = xpatid;
				PatStammEvent pEvt = new PatStammEvent(getInstance());
				pEvt.setPatStammEvent("PatSuchen");
				pEvt.setDetails(s1,s2,"") ;
				PatStammEventClass.firePatStammEvent(pEvt);		
				return null;
			}
			
		}.execute();
	}
	
	@SuppressWarnings("unchecked")
	private void doEigenanteilDrucken() throws Exception{
		aktuellePosition = 0;
		istprivat = true;
		hmRechnung.clear();
		String[] padressDaten = PatTools.constructPatHMapFromStrings(patvec.get(0).get(3),
				patvec.get(0).get(4),patvec.get(0).get(1),patvec.get(0).get(0),
				patvec.get(0).get(5),patvec.get(0).get(6),patvec.get(0).get(7));
		hmRechnung.put("<pri1>",padressDaten[0]);
		hmRechnung.put("<pri2>",padressDaten[1]);
		hmRechnung.put("<pri3>",padressDaten[2]);
		hmRechnung.put("<pri4>",padressDaten[3]);
		hmRechnung.put("<pri5>",padressDaten[4]+",");
		aktRechnung = Integer.toString(SqlInfo.erzeugeNummer("rnr"));
		if(aktRechnung.equals("-1")){
			JOptionPane.showMessageDialog(null, "Fehler - Rechnungsnummer für Eigenanteile kann nicht bezogen werden" );
			abrechnungOk = false;
			return;
		}
		vecposrechnung.clear();
		Vector<String> vecpos = new Vector<String>();
		
		
		if( (jcmbpat[0].getSelectedIndex()==0) || (tfpatanzahl[0].getText().trim().equals(0)) ){
			JOptionPane.showMessageDialog(null, "Fehler - Eigenenanteilsrechnung kann nicht erstellt werden" );
			abrechnungOk = false;
			return;
		}
		hmRechnung.put("<pri6>",aktRechnung);
		hmRechnung.put("<pri7>",StringTools.EGross(patvec.get(0).get(0))+", "+
				StringTools.EGross(patvec.get(0).get(1))+", geb.am: "+
				DatFunk.sDatInDeutsch(patvec.get(0).get(2)));
		this.druckIk = SystemConfig.hmAbrechnung.get("rehapriik");
		this.druckDrucker = SystemConfig.hmAbrechnung.get("rehapridrucker");
		this.druckFormular = SystemConfig.hmAbrechnung.get("rehapriformular");
		this.druckExemplare = Integer.parseInt(SystemConfig.hmAbrechnung.get("rehapriexemplare"));
		hmRechnung.put("<pri8>",this.druckIk);

		vecpos.add(jcmbpat[0].getSelectedItem().toString());
		vecpos.add(tfpatanzahl[0].getText().trim());
		vecpos.add(tfpatpreis[0].getText().trim());
		vecpos.add(tfpatgesamt[0].getText().trim());
		vecpos.add(jcmbpat[0].getValueAt(2).toString());
		vecpos.add(jcmbpat[0].getValueAt(9).toString());
		vecposrechnung.add((Vector<String>)((Vector<String>)vecpos).clone() );
		gesamtPreis = BigDecimal.valueOf(Double.parseDouble(tfpatgesamt[0].getText().trim().replace(",", ".")));

		starteDokument(Reha.proghome+"vorlagen/"+Reha.aktIK+"/"+this.druckFormular,this.druckDrucker);
		//System.out.println("Dokument gestartet");
		starteErsetzen(hmRechnung);
		//System.out.println("suche ersetzen abeschlossen");
		startePositionen(vecposrechnung,gesamtPreis);
		//System.out.println("Positionen gesetzt");
		starteDrucken(this.druckExemplare);
		//System.out.println("Druck gestartet");
	}

	private void doFaktura(){
		String cmdKopf = "insert into faktura set ";
		
		for(int i = 0; i< vecposrechnung.size();i++){
			////System.out.println("In RechnungFaktura "+hmRechnung);
			rechnungBuf.setLength(0);
			rechnungBuf.trimToSize();
			rechnungBuf.append(cmdKopf);				
			if(i==0){
				rechnungBuf.append("kassen_nam='"+hmRechnung.get("<pri1>")+"', ");
				rechnungBuf.append("kassen_na2='"+hmRechnung.get("<pri2>")+"', ");
				rechnungBuf.append("strasse='"+hmRechnung.get("<pri3>")+"', ");
				try{
					if( hmRechnung.get("<pri4>").indexOf(" ") >= 0){
						rechnungBuf.append("plz='"+hmRechnung.get("<pri4>").split(" ")[0]+"', ");	
						int leeran = hmRechnung.get("<pri4>").indexOf(" ");
						rechnungBuf.append("ort='"+hmRechnung.get("<pri4>").substring(leeran).trim()+"', ");	
						
					}else{
						rechnungBuf.append("plz='"+""+"', ");	
						rechnungBuf.append("ort='"+hmRechnung.get("<pri4>")+"', ");
					}
					rechnungBuf.append("name='"+patvec.get(0).get(0)+","+patvec.get(0).get(1)+
							",geb."+DatFunk.sDatInDeutsch(patvec.get(0).get(2))+"', ");
				}catch(Exception ex){
					//System.out.println("PLZ/Ort nicht angegeben");
				}
			}
			rechnungBuf.append("lfnr='"+Integer.toString(i)+"', ");
			rechnungBuf.append("status='"+ patvec.get(0).get(8)+"', ");
			rechnungBuf.append("pos_kas='"+ vecposrechnung.get(i).get(4) +"', ");
			rechnungBuf.append("pos_int='"+ vecposrechnung.get(i).get(5) +"', ");
			rechnungBuf.append("anzahl='"+  vecposrechnung.get(i).get(1) +"', ");
			rechnungBuf.append("anzahltage='"+ vecposrechnung.get(i).get(1) +"', ");
			rechnungBuf.append("preis='"+  vecposrechnung.get(i).get(2).replace(",", ".")  +"', ");
			rechnungBuf.append("gesamt='"+  vecposrechnung.get(i).get(3).replace(",", ".")  +"', ");
			rechnungBuf.append("zzbetrag='"+  "0.00" +"', ");
			rechnungBuf.append("netto='"+  vecposrechnung.get(i).get(3).replace(",", ".")  +"', ");
			rechnungBuf.append("pauschale='"+  "0.00" +"', ");
			rechnungBuf.append("rez_nr='"+   rehavec.get(0).get(1) +"', ");		
			rechnungBuf.append("rezeptart='8', ");
			rechnungBuf.append("pat_intern='"+rehavec.get(0).get(0)+"', ");
			rechnungBuf.append("rnummer='"+  aktRechnung +"', ");
			rechnungBuf.append("kassid='"+rehavec.get(0).get(37)+"', ");
			rechnungBuf.append("arztid='"+rehavec.get(0).get(16)+"', ");
			rechnungBuf.append("disziplin='"+  "RH" +"', ");
			rechnungBuf.append("rdatum='"+  DatFunk.sDatInSQL(DatFunk.sHeute()) +"'");
			SqlInfo.sqlAusfuehren(rechnungBuf.toString());
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	private void doAnlegenOP(){
		rechnungBuf.setLength(0);
		rechnungBuf.trimToSize();
		rechnungBuf.append("insert into rliste set ");
		rechnungBuf.append("r_nummer='"+aktRechnung+"', ");
		rechnungBuf.append("r_datum='"+DatFunk.sDatInSQL(DatFunk.sHeute())+"', ");
		String r_kasse;
		if(istprivat){
			r_kasse = hmRechnung.get("<pri2>");
		}else{
			r_kasse = hmRechnung.get("<pri1>");
		}
		rechnungBuf.append("r_kasse='"+r_kasse+"', ");
		rechnungBuf.append("r_name='"+patvec.get(0).get(0)+","+patvec.get(0).get(1)+","+DatFunk.sDatInDeutsch(patvec.get(0).get(2))+"', ");
		rechnungBuf.append("r_klasse='"+"RH"+"', ");
		rechnungBuf.append("r_betrag='"+dcf.format(gesamtPreis.doubleValue()).replace(",", ".")+"', ");
		rechnungBuf.append("r_offen='"+dcf.format(gesamtPreis.doubleValue()).replace(",", ".")+"', ");
		rechnungBuf.append("r_zuzahl='"+"0.00"+"', ");
		if(istprivat){
			rechnungBuf.append("pat_intern='"+rehavec.get(0).get(0)+"', ");
		}
		rechnungBuf.append("ikktraeger='"+kassvec.get(0).get(6)+"'");
		SqlInfo.sqlAusfuehren(rechnungBuf.toString());
		
	}
	@SuppressWarnings("unchecked")
	private void doHauptRechnungDrucken() throws Exception{
		hmRechnung.clear();

		try{
			if(istprivat){
				String[] padressDaten = PatTools.constructPatHMapFromStrings(patvec.get(0).get(3),
						patvec.get(0).get(4),patvec.get(0).get(1),patvec.get(0).get(0),
						patvec.get(0).get(5),patvec.get(0).get(6),patvec.get(0).get(7));
				hmRechnung.put("<pri1>",padressDaten[0]);
				hmRechnung.put("<pri2>",padressDaten[1]);
				hmRechnung.put("<pri3>",padressDaten[2]);
				hmRechnung.put("<pri4>",padressDaten[3]);
				hmRechnung.put("<pri5>",padressDaten[4]+",");
			}else{
				//kassvec = SqlInfo.holeFelder("select kassen_nam1,kassen_nam2,strasse,plz,ort,ik_papier,ik_ktraeger from kass_adr where id='"+kassenid+"' LIMIT 1");
				hmRechnung.put("<pri1>",rechnungvec.get(0).get(0));
				hmRechnung.put("<pri2>",rechnungvec.get(0).get(1));
				hmRechnung.put("<pri3>",rechnungvec.get(0).get(2));
				hmRechnung.put("<pri4>",rechnungvec.get(0).get(3)+" "+rechnungvec.get(0).get(4));
				hmRechnung.put("<pri5>","Sehr geehrte Damen und Herren,");
			}
			
			aktRechnung = Integer.toString(SqlInfo.erzeugeNummer("rnr"));
			
			if(aktRechnung.equals("-1")){
				JOptionPane.showMessageDialog(null, "Fehler - Rechnungsnummer für Reharechnung kann nicht bezogen werden" );
				abrechnungOk = false;
				return;
			}
			hmRechnung.put("<pri6>",aktRechnung);
			hmRechnung.put("<pri7>",StringTools.EGross(patvec.get(0).get(0))+", "+
					StringTools.EGross(patvec.get(0).get(1))+", geb.am: "+
					DatFunk.sDatInDeutsch(patvec.get(0).get(2)));
			/*******************************/
			//int testik = 0;
			if(jcmb[0].getSelectedItem().toString().toUpperCase().contains("GKV")){
				this.druckIk = SystemConfig.hmAbrechnung.get("rehagkvik");
				this.druckDrucker = SystemConfig.hmAbrechnung.get("rehagkvdrucker");
				this.druckFormular = SystemConfig.hmAbrechnung.get("rehagkvformular");
				this.druckExemplare = Integer.parseInt(SystemConfig.hmAbrechnung.get("rehagkvexemplare"));
			}else if(jcmb[0].getSelectedItem().toString().toUpperCase().startsWith("LVA")||
					jcmb[0].getSelectedItem().toString().toUpperCase().startsWith("BFA") ||
					jcmb[0].getSelectedItem().toString().toUpperCase().startsWith("KNAPPSCH")){
				this.druckIk = SystemConfig.hmAbrechnung.get("rehadrvik");
				this.druckDrucker = SystemConfig.hmAbrechnung.get("rehadrvdrucker");
				this.druckFormular = SystemConfig.hmAbrechnung.get("rehadrvformular");
				if(this.jckb[1].isSelected()){
					this.druckExemplare = 1;
				}else{
					this.druckExemplare = Integer.parseInt(SystemConfig.hmAbrechnung.get("rehadrvexemplare"));	
				}
				//testik = 1;
			}else if(jcmb[0].getSelectedItem().toString().toUpperCase().contains("PRI")){
				this.druckIk = SystemConfig.hmAbrechnung.get("rehapriik");
				this.druckDrucker = SystemConfig.hmAbrechnung.get("rehapridrucker");
				this.druckFormular = SystemConfig.hmAbrechnung.get("rehapriformular");
				this.druckExemplare = Integer.parseInt(SystemConfig.hmAbrechnung.get("rehapriexemplare"));
				//testik = 2;
			}else{
				this.druckIk = SystemConfig.hmAbrechnung.get("rehadrvik");
				this.druckDrucker = SystemConfig.hmAbrechnung.get("rehadrvdrucker");
				this.druckFormular = SystemConfig.hmAbrechnung.get("rehadrvformular");
				this.druckExemplare = Integer.parseInt(SystemConfig.hmAbrechnung.get("rehadrvexemplare"));
				
				//testik = 3;
			}
			hmRechnung.put("<pri8>",this.druckIk);
			//JOptionPane.showMessageDialog(null,"Verwende IK "+this.druckIk+" in Testvariante = "+Integer.toString(testik));
			/*******************************/

			vecposrechnung.clear();

			Vector<String> vecpos = new Vector<String>(); 
			for(int i = 0; i < 4;i++){
				vecpos.clear();
				if(jcmb[i].getSelectedIndex()>0){
					vecpos.add(jcmb[i].getSelectedItem().toString());
					vecpos.add(tfanzahl[i].getText().trim());
					vecpos.add(tfpreis[i].getText().trim());
					vecpos.add(tfgesamt[i].getText().trim());
					if(jcmb[i].getSelectedIndex()>0){
						vecpos.add(jcmb[i].getValueAt(2).toString());
						vecpos.add(jcmb[i].getValueAt(9).toString());
					}else{
						vecpos.add("");
						vecpos.add("");
					}
					vecposrechnung.add((Vector<String>)((Vector<String>)vecpos).clone() );
				}
			}
			
			starteDokument(Reha.proghome+"vorlagen/"+Reha.aktIK+"/"+this.druckFormular,this.druckDrucker);
			starteErsetzen(hmRechnung);
			startePositionen(vecposrechnung,gesamtPreis);
			starteDrucken(this.druckExemplare);
		}catch(Exception ex){
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "In der Abrechnung ist ein Fehler aufgetreten, bitte beheben Sie den Fehler und starten Sie die Abrechnung erneut");
			abrechnungOk = false;
		}
	}
	
	/******************Nachfolgend die OO.writer - Funktionen**************************/
	public void starteDokument(String url,String drucker) {
		//System.out.println("URL="+url);
		//System.out.println("Drucker="+drucker);
		IDocumentService documentService = null;
		try {
			documentService = Reha.officeapplication.getDocumentService();
		} catch (OfficeApplicationException e) {
			e.printStackTrace();
		}
		IDocumentDescriptor docdescript = new DocumentDescriptor();
        docdescript.setHidden(true);
        docdescript.setAsTemplate(true);
		IDocument document = null;
		try {
			document = documentService.loadDocument(url,docdescript);
		} catch (NOAException e) {
			e.printStackTrace();
		}
		/**********************/
		textDocument = (ITextDocument)document;
		OOTools.druckerSetzen(textDocument, drucker);
		try {
			textTable = textDocument.getTextTableService().getTextTable("Tabelle1");
		} catch (TextException e) {
			e.printStackTrace();
		}
		try {
			textEndbetrag = textDocument.getTextTableService().getTextTable("Tabelle2");
		} catch (TextException e) {
			e.printStackTrace();
		}
	}
	private void starteErsetzen(HashMap<String,String> hmAdresse){
		ITextFieldService textFieldService = textDocument.getTextFieldService();
		ITextField[] placeholders = null;
		try {
			placeholders = textFieldService.getPlaceholderFields();
		} catch (TextException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < placeholders.length; i++) {
			if(placeholders[i].getDisplayText().toLowerCase().equals("<pri1>")){
				placeholders[i].getTextRange().setText(hmAdresse.get("<pri1>"));
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<pri2>")){
				placeholders[i].getTextRange().setText(hmAdresse.get("<pri2>"));				
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<pri3>")){
				placeholders[i].getTextRange().setText(hmAdresse.get("<pri3>"));				
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<pri4>")){
				placeholders[i].getTextRange().setText(hmAdresse.get("<pri4>"));				
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<pri5>")){
				placeholders[i].getTextRange().setText(hmAdresse.get("<pri5>"));				
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<pri6>")){
				placeholders[i].getTextRange().setText(hmAdresse.get("<pri6>"));
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<pri7>")){
				placeholders[i].getTextRange().setText(hmAdresse.get("<pri7>"));
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<pri8>")){
				placeholders[i].getTextRange().setText(hmAdresse.get("<pri8>"));
			}
		}
		
	}
	private void startePositionen(Vector<Vector<String>> vecpositionen,BigDecimal gesamt) throws TextException{
		aktuellePosition++;	 
		for(int i = 0; i < vecpositionen.size();i++){
			textTable.getCell(0,aktuellePosition).getTextService().getText().setText(vecpositionen.get(i).get(0));
			textTable.getCell(1,aktuellePosition).getTextService().getText().setText(vecpositionen.get(i).get(1));
			textTable.getCell(2,aktuellePosition).getTextService().getText().setText(vecpositionen.get(i).get(2));
			textTable.getCell(3,aktuellePosition).getTextService().getText().setText(vecpositionen.get(i).get(3));
			textTable.addRow(1);
			aktuellePosition++;				
		}
		textEndbetrag.getCell(1,0).getTextService().getText().setText(dcf.format(gesamt.doubleValue())+" EUR");
	}
	private void starteDrucken(int exemplare){
		if(SystemConfig.hmAbrechnung.get("hmallinoffice").equals("1")){
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					textDocument.getFrame().getXFrame().getContainerWindow().setVisible(true);
					textDocument.getFrame().setFocus();
				}
			});
		}else{
			//System.out.println("Vor PrintProperties");
			PrintProperties printprop = new PrintProperties ((short)exemplare);
			try {
				//System.out.println("Vor print(printprop)");
				textDocument.getPrintService().print(printprop);
				//System.out.println("Vor textDocument.close()");
				while(textDocument.getPrintService().isActivePrinterBusy()){
					Thread.sleep(50);
				}
				Thread.sleep(150);
				textDocument.close();
				//System.out.println("Nach textDocument.close()");
			} catch (DocumentException e1) {
				e1.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NOAException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	
	/******************Ende der OO.writer - Funktionen*********************************/
	
}
