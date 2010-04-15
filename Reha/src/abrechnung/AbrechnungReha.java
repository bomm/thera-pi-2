package abrechnung;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;

import rehaInternalFrame.JRehaabrechnungInternal;
import sqlTools.SqlInfo;
import systemEinstellungen.SystemPreislisten;
import systemTools.JRtaCheckBox;
import systemTools.JRtaComboBox;
import systemTools.JRtaTextField;
import systemTools.StringTools;
import terminKalender.DatFunk;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

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
	JLabel kasseGesamt = null;
	JLabel patGesamt = null;
	JRtaTextField[] tfanzahl = {null,null,null,null};
	JRtaTextField[] tfpreis = {null,null,null,null};
	JRtaTextField[] tfgesamt = {null,null,null,null};
	JRtaComboBox[] jcmb = {null,null,null,null};
	JRtaTextField[] tfrehanr = {null};
	
	JRtaCheckBox[] jckb = {null};
	JRtaTextField[] tfpatanzahl = {null};
	JRtaTextField[] tfpatpreis = {null};
	JRtaTextField[] tfpatgesamt = {null};
	JRtaComboBox[] jcmbpat = {null};

	JButton[] jbut = {null,null,null,null,null};
	Vector<Vector<String>> rehavec = new Vector<Vector<String>>(); 

	DecimalFormat dcf = new DecimalFormat("####0.00");
	BigDecimal[] zeilenPreis = {BigDecimal.valueOf(Double.parseDouble("0.00")),
			BigDecimal.valueOf(Double.parseDouble("0.00")),BigDecimal.valueOf(Double.parseDouble("0.00")),
			BigDecimal.valueOf(Double.parseDouble("0.00"))
	};
	BigDecimal gesamtPreis = BigDecimal.valueOf(Double.parseDouble("0.00"));
	BigDecimal gesamtPatPreis = BigDecimal.valueOf(Double.parseDouble("0.00"));
	boolean initOk = false;
	
	ActionListener al;
	KeyListener kl;
	
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
	}
	private void setzeFocus(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				tfrehanr[0].setText("RH5529");
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
		//  1  2  3    4  5   6  7   8  9  10 11  12  13 14  15  16  17  18 19 20
		"20dlu,p,20dlu,p,5dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,20dlu,p,5dlu,p,2dlu,p"
		);
		CellConstraints cc = new CellConstraints();
		content = new JXPanel();
		content.setLayout(lay);
		JLabel lab = new JLabel("RehaNr. einlesen");
		content.add(lab,cc.xy(2,2));
		tfrehanr[0] = new JRtaTextField("GROSS",true);
		content.add(tfrehanr[0],cc.xy(4,2));
		content.add((jbut[4] = macheBut("einlesen","einlesen")),cc.xyw(6,2,3));
		patLabel = new JLabel(" ");
		patLabel.setForeground(Color.BLUE);
		content.add(patLabel,cc.xyw(2,4,7));
		for(int i = 0; i < 4;i++){
			tfanzahl[i] = new JRtaTextField("ZAHLEN",true);
			content.add(tfanzahl[i],cc.xy(2,6+(i*2)));
			content.add((jcmb[i]=macheCombo("combo_"+Integer.toString(i))),cc.xy(4, 6+(i*2)));
			tfpreis[i] = new JRtaTextField("FL",true,"6.2","RECHTS");
			content.add(tfpreis[i],cc.xy(6,6+(i*2)));
			tfgesamt[i] = new JRtaTextField("FL",true,"6.2","RECHTS");
			tfgesamt[i].setEditable(false);
			content.add(tfgesamt[i],cc.xy(8,6+(i*2)));
		}
		lab = new JLabel("Kassenrechnung Summe :");
		lab.setForeground(Color.BLUE);
		content.add(lab,cc.xy(4,14));
		kasseGesamt = new JLabel("0,00");
		kasseGesamt.setForeground(Color.RED);
		kasseGesamt.setHorizontalAlignment(JLabel.RIGHT);
		content.add(kasseGesamt,cc.xy(8,14));
		
		jckb[0] = new JRtaCheckBox("zusätzlich Eigenanteile der Patienten abrechnen");
		jckb[0].setActionCommand("auchpatliquidieren");
		jckb[0].addActionListener(al);
		content.add(jckb[0] ,cc.xyw(2,16,7));
		tfpatanzahl[0]= new JRtaTextField("ZAHLEN",true);
		tfpatanzahl[0].setEnabled(false);
		content.add(tfpatanzahl[0] ,cc.xy(2,18));
		jcmbpat[0]=macheCombo("combopat");
		jcmbpat[0].setEnabled(false);
		content.add(jcmbpat[0],cc.xy(4, 18));
		tfpatpreis[0] = new JRtaTextField("FL",true,"6.2","RECHTS");
		tfpatpreis[0].setEnabled(false);
		content.add(tfpatpreis[0],cc.xy(6,18));
		tfpatgesamt[0] = new JRtaTextField("FL",true,"6.2","RECHTS");
		tfpatgesamt[0].setEditable(false);
		content.add(tfpatgesamt[0],cc.xy(8,18));
		lab = new JLabel("Patientenrechung Summe :");
		lab.setForeground(Color.BLUE);
		content.add(lab,cc.xy(4,20));
		patGesamt = new JLabel("0,00");
		patGesamt.setForeground(Color.RED);
		patGesamt.setHorizontalAlignment(JLabel.RIGHT);
		content.add(patGesamt,cc.xy(8,20));
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
		if(rehanummer.equals("")){
			JOptionPane.showMessageDialog(null, "Witzbolde gibt's....");
			return;
		}	
		rehavec.clear();
		rehavec = SqlInfo.holeFelder("select * from verordn where rez_nr='"+rehanummer+"' LIMIT 1");
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				holePatient(rehavec.get(0).get(0));
				return null;
			}
			
		}.execute();
		if(rehavec.size()<=0){
			JOptionPane.showMessageDialog(null, "Reha-Verordnung mit Nummer "+rehanummer+" wurde nicht gefunden");
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
	}
	
	
	private void holePatient(String pat_intern){
		Vector<Vector<String>> vec = SqlInfo.holeFelder("select n_name,v_name,geboren from pat5 where pat_intern='"+pat_intern+"' LIMIT 1");
		if(vec.size()<=0){
			patLabel.setText("Patient wurde nicht gefunden");
			return;
		}
		patLabel.setText(
				StringTools.EGross(vec.get(0).get(0))+", "+
				StringTools.EGross(vec.get(0).get(1))+", geb.am: "+
				DatFunk.sDatInDeutsch(vec.get(0).get(2))
				);
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
		jckb[0].setSelected(false);
		patGesamt.setText("");
		kasseGesamt.setText("");
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

	
}
