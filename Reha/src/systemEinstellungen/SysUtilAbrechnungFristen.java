package systemEinstellungen;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXPanel;

import systemTools.ButtonTools;
import CommonTools.JRtaCheckBox;
import CommonTools.JRtaComboBox;
import CommonTools.JRtaRadioButton;
import CommonTools.JRtaTextField;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SysUtilAbrechnungFristen extends JXPanel implements KeyListener, ActionListener, FocusListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3684039924876612917L;
	JButton knopf1 = null;
	JButton knopf2 = null;
	JButton knopf3 = null;
	JComboBox refresh = null;
	static JProgressBar Fortschritt = null;
	JRtaRadioButton[] rads = {null,null,null,null,null,null};
	JRtaTextField tage1 = null;
	JRtaTextField tage2 = null;
	
	JRtaComboBox cmbdiszi = null;
	JRtaComboBox cmbtarife = null;
	
	ButtonGroup bgkalender1 = new ButtonGroup();
	ButtonGroup bgkalender2 = new ButtonGroup();
	ButtonGroup bgzuzahl = new ButtonGroup();
	boolean werteGeaendet = false;
	JButton[] but = {null,null};
	JRtaCheckBox[] cbox = {null,null};
	
	private HashMap<String,Vector<Object>> hmFristen = new HashMap<String,Vector<Object>>();
	private HashMap<String,Vector<Integer>> hmZuzahlModus = new HashMap<String,Vector<Integer>>();
	String[] xdisziplin = {"Physio","Massage","Ergo","Logo","Reha","Podo"};
	
	String lastdiszi = null;
	int lasttarif = -1;
	
	@SuppressWarnings("unchecked")
	public SysUtilAbrechnungFristen(){
		super(new BorderLayout());
		//System.out.println("Aufruf SysUtilKalendereinstell");
		this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 20));
		hmFristen = (HashMap<String,Vector<Object>>) SystemPreislisten.hmFristen.clone();
		hmZuzahlModus = (HashMap<String,Vector<Integer>>) SystemPreislisten.hmZuzahlModus.clone();
		// hier noch das Email und Bayern-Gedönse rein;
		/****/
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("SystemInit"));
		/****/
	    add(getVorlagenSeite(),BorderLayout.CENTER);
	    add(getKnopfPanel(),BorderLayout.SOUTH);
	    validate();
	    SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				cmbtarife.addActionListener(getInstance());
				cmbdiszi.addActionListener(getInstance());
				werteEinlesen();
	 	  	}
	    });			
	     

		return;
	}
	private SysUtilAbrechnungFristen getInstance(){
		return this;
	}
	private JPanel getKnopfPanel(){
		
		
		but[0] = ButtonTools.macheButton("abbrechen","abbrechen",this);
		but[1] = ButtonTools.macheButton("speichern","speichern",this);
									//      1.                      2.    3.    4.     5.     6.    7.      8.     9.
		FormLayout jpanlay = new FormLayout("right:max(130dlu;p), 20dlu, 65dlu, 3dlu, 4dlu, 3dlu, 65dlu",
       //1.    2. 3.   4.   5.   6.     7.    8. 9.  10.  11. 12. 13.  14.  15. 16.  17. 18.  19.   20.    21.   22.   23.
		"p, 10dlu, p");
		
		PanelBuilder jpan = new PanelBuilder(jpanlay);
		jpan.setDefaultDialogBorder();
		jpan.getPanel().setOpaque(false);		
		CellConstraints jpancc = new CellConstraints();
		
		jpan.addSeparator("", jpancc.xyw(1,1,7));
		jpan.add(but[0], jpancc.xy(3,3));
		jpan.add(but[1], jpancc.xy(7,3));
		jpan.addLabel("Änderungen übernehmen?", jpancc.xy(1,3));
		jpan.getPanel().validate();
		return jpan.getPanel();
	}	
	/************** Beginn der Methode für die Objekterstellung und -platzierung *********/
	private JPanel getVorlagenSeite(){
        //                                      1.            2.    3.    4.     5.     6.    7.      8.     9.
		FormLayout lay = new FormLayout("right:max(130dlu;p), 20dlu, 65dlu, 3dlu, 4dlu, 3dlu, 65dlu",
       //1.    2. 3.   4.   5.   6.   7.    8.  9.  10.  11. 12.   13.  14.  15.  16.  17. 18.   19. 20.  21. 22.   23.  24.  25. 26 27
		"p, 5dlu,p, 1dlu,  p,  5dlu,  p, 5dlu, p, 0dlu,  p, 0dlu,  p,  5dlu, p, 0dlu,  p, 0dlu, p, 1dlu, p, 5dlu,  p, 1dlu, p,1dlu,p");

		
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator("Einstellungen je nach Rahmenvertrag und/oder gültiger HMR");
		builder.addLabel("Disziplin auswählen", cc.xy(1, 3));
		builder.add(cmbdiszi = new JRtaComboBox(xdisziplin),cc.xyw(3,3,5));
		cmbdiszi.setActionCommand("neuedisziplin");
		builder.addLabel("Tarif-/Preisgruppe auswählen", cc.xy(1, 5));
		String defaultHM = null;
		for(int y = 0; y < xdisziplin.length;y++){
			if(SystemConfig.initRezeptKlasse.toLowerCase().startsWith(xdisziplin[y].toLowerCase())){
				defaultHM = xdisziplin[y].toString();
				break;
			}
		}
		builder.add(cmbtarife = new JRtaComboBox(SystemPreislisten.hmPreisGruppen.get((defaultHM != null ? defaultHM : "Physio"))), cc.xyw(3, 5,5));
		//builder.add(cmbtarife = new JRtaComboBox(SystemPreislisten.hmPreisGruppen.get(xdisziplin[cmbdiszi.getSelectedIndex()])), cc.xyw(3, 5,5));

		builder.addSeparator("Fristenregegelung",cc.xyw(1,7,7));
		
		builder.addLabel("Rezeptdatum <-> Ersttermin (max. in Tagen)", cc.xy(1, 9));
		
		builder.add(tage1 = new JRtaTextField("ZAHLEN",true), cc.xy(3, 9));
		builder.add(rads[0] = new JRtaRadioButton("Kalendertage"), cc.xy(3, 11));
		builder.add(rads[1] = new JRtaRadioButton("Werktage"), cc.xy(3, 13));
		builder.add(cbox[0] = new JRtaCheckBox("inkl. Samstag"),cc.xyw(6,13,2));
		
		bgkalender1.add(rads[0]);
		bgkalender1.add(rads[1]);
		rads[0].setOpaque(false);rads[1].setOpaque(false);
		cbox[0].setOpaque(false);
		
		builder.addLabel("Max. Tage zwischen den Behandlungen", cc.xy(1, 15));
		builder.add(tage2 = new JRtaTextField("ZAHLEN",true), cc.xy(3, 15));
		builder.add(rads[2] = new JRtaRadioButton("Kalendertage"), cc.xy(3, 17));
		builder.add(rads[3] = new JRtaRadioButton("Werktage"), cc.xy(3, 19));
		builder.add(cbox[1] = new JRtaCheckBox("inkl. Samstag"),cc.xyw(6,19,2));
		
		bgkalender2.add(rads[2]);
		bgkalender2.add(rads[3]);
		rads[2].setOpaque(false);rads[3].setOpaque(false);
		cbox[1].setOpaque(false);
		
		builder.addSeparator("Abrechnungsdatei (EDIFACT)",cc.xyw(1,21,7));
		builder.addLabel("Darstellung der Zuzahlung i.d. Abrechnungsdatei", cc.xy(1, 23));
		builder.add(rads[4] = new JRtaRadioButton("Normale Variante"), cc.xyw(3, 23,5));
		builder.add(rads[5] = new JRtaRadioButton("Bayerische Variante"), cc.xyw(3, 25,5));
		bgzuzahl.add(rads[4]);
		bgzuzahl.add(rads[5]);
		rads[4].setOpaque(false);rads[5].setOpaque(false);
		

		builder.getPanel().validate();
		return builder.getPanel();
	}
	private void werteEinlesen(){
		lastdiszi = cmbdiszi.getSelectedItem().toString();
		lasttarif = cmbtarife.getSelectedIndex();
		
		tage1.setText( Integer.toString( (Integer)((Vector<?>)hmFristen.get(lastdiszi).get(0)).get(lasttarif) ) );
		if( (Boolean) ((Vector<?>)hmFristen.get(lastdiszi).get(1)).get(lasttarif) ){
			rads[0].setSelected(true);
		}else{
			rads[1].setSelected(true);
		}
		tage2.setText( Integer.toString( (Integer)((Vector<?>)hmFristen.get(lastdiszi).get(2)).get(lasttarif) ) );
		if( (Boolean) ((Vector<?>)hmFristen.get(lastdiszi).get(3)).get(lasttarif) ){
			rads[2].setSelected(true);
		}else{
			rads[3].setSelected(true);
		}
		if( hmZuzahlModus.get(lastdiszi).get(lasttarif) == 1){
			rads[4].setSelected(true);
		}else{
			rads[5].setSelected(true);
		}
		cbox[0].setSelected((Boolean) ((Vector<?>)hmFristen.get(lastdiszi).get(4)).get(lasttarif));
		cbox[1].setSelected((Boolean) ((Vector<?>)hmFristen.get(lastdiszi).get(5)).get(lasttarif));
	}
	private boolean werteChanged(){
		boolean changed = false;
		try{
			if(Integer.parseInt(tage1.getText()) != 
				(Integer)((Vector<?>)hmFristen.get(lastdiszi).get(0)).get(lasttarif)){changed=true;}
			if(Integer.parseInt(tage2.getText()) != 
				(Integer)((Vector<?>)hmFristen.get(lastdiszi).get(2)).get(lasttarif)){changed=true;}
			if(rads[0].isSelected() != 
				(Boolean) ((Vector<?>)hmFristen.get(lastdiszi).get(1)).get(lasttarif) ){changed=true;}
			if(rads[2].isSelected() != 
				(Boolean) ((Vector<?>)hmFristen.get(lastdiszi).get(3)).get(lasttarif) ){changed=true;}
			if(rads[4].isSelected() !=
				(hmZuzahlModus.get(lastdiszi).get(lasttarif) == 1 ? true : false) ){changed=true;}
			if(cbox[0].isSelected() != 
				(Boolean) ((Vector<?>)hmFristen.get(lastdiszi).get(4)).get(lasttarif) ){changed=true;}
			if(cbox[1].isSelected() != 
				(Boolean) ((Vector<?>)hmFristen.get(lastdiszi).get(5)).get(lasttarif) ){changed=true;}

		}catch(Exception ex){
			JOptionPane.showMessageDialog(null, "Fehler bei der Ermittlung der Werte");
		}
		return changed;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void doSpeichern(){
		try{
			((Vector)hmFristen.get(lastdiszi).get(0)).set(lasttarif,(Integer) Integer.parseInt(tage1.getText()));
			((Vector)hmFristen.get(lastdiszi).get(2)).set(lasttarif,(Integer) Integer.parseInt(tage2.getText()));
			((Vector)hmFristen.get(lastdiszi).get(1)).set(lasttarif,(Boolean)rads[0].isSelected() );
			((Vector)hmFristen.get(lastdiszi).get(3)).set(lasttarif,(Boolean)rads[2].isSelected() );
			((Vector)hmFristen.get(lastdiszi).get(4)).set(lasttarif,(Boolean)cbox[0].isSelected() );
			((Vector)hmFristen.get(lastdiszi).get(5)).set(lasttarif,(Boolean)cbox[1].isSelected() );
			((Vector)hmZuzahlModus.get(lastdiszi)).set( lasttarif,(rads[4].isSelected()? 1 : 0));
			SystemConfig.UpdateIni("fristen.ini", "Fristen_"+lastdiszi, "FristBeginn"+Integer.toString(lasttarif+1),tage1.getText());
			SystemConfig.UpdateIni("fristen.ini", "Fristen_"+lastdiszi, "FristUnterbrechung"+Integer.toString(lasttarif+1),tage2.getText());
			SystemConfig.UpdateIni("fristen.ini", "Fristen_"+lastdiszi, "BeginnKalendertage"+Integer.toString(lasttarif+1),(rads[0].isSelected() ? "1" : "0"));
			SystemConfig.UpdateIni("fristen.ini", "Fristen_"+lastdiszi, "UnterbrechungKalendertage"+Integer.toString(lasttarif+1),(rads[2].isSelected() ? "1" : "0"));
			SystemConfig.UpdateIni("fristen.ini", "Fristen_"+lastdiszi, "BeginnMitSamstag"+Integer.toString(lasttarif+1),(cbox[0].isSelected() ? "1" : "0"));
			SystemConfig.UpdateIni("fristen.ini", "Fristen_"+lastdiszi, "UnterbrechungMitSamstag"+Integer.toString(lasttarif+1),(cbox[1].isSelected() ? "1" : "0"));
			SystemConfig.UpdateIni("preisgruppen.ini", "ZuzahlRegeln_"+lastdiszi, "ZuzahlModus"+Integer.toString(lasttarif+1),(rads[4].isSelected() ? "1" : "0"));
			JOptionPane.showMessageDialog(null, "Werte wurden erfolgreich gespeichert");
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null, "Fehler beim speichern der Werte");
		}
	}
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getActionCommand().equals("speichern")){
			doSpeichern();
			werteEinlesen();
			return;
		}
		if(e.getActionCommand().equals("abbruch")){
			SystemInit.abbrechen();
			return;
		}
		if(werteChanged()){
			int frage = JOptionPane.showConfirmDialog(null,"Sie haben die Werte verändert!\n"+
					"Sollen die Änderungen jetzt abgespeichert werden","Wichtige Benutzeranfrage",JOptionPane.YES_NO_OPTION);
			if(frage == JOptionPane.YES_OPTION){doSpeichern();}
		}
		if(e.getActionCommand().equals("neuedisziplin")){
			if(lasttarif >= 0){
				cmbtarife.removeActionListener(getInstance());
				cmbtarife.setDataVector(SystemPreislisten.hmPreisGruppen.get(xdisziplin[cmbdiszi.getSelectedIndex()]));
				cmbtarife.setSelectedIndex(lasttarif);
				cmbtarife.addActionListener(getInstance());
			}
		}
		werteEinlesen();
	}
	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		if(((JComponent)arg0.getSource()).getName() != null){
		}
		
	}
	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}	

}
