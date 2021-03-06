package systemEinstellungen;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXPanel;

import CommonTools.JRtaTextField;
import terminKalender.ParameterLaden;

import CommonTools.INIFile;
import CommonTools.INITool;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;



public class SysUtilAnsichtsOptionen extends JXPanel implements KeyListener,ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	JCheckBox startWochenAnz = null;
	JComboBox defaultWA = null;
	JComboBox defaultNA = null;
	JRtaTextField [] jtfeld = {null,null,null,null,null,null,null};

	JButton knopf1 = null;
	JButton knopf2 = null;
	
	JRadioButton oben = null;
	JRadioButton unten = null;
	JCheckBox optimize = null;
	ButtonGroup bgroup = new ButtonGroup();
	

	SysUtilAnsichtsOptionen(){
		super(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 0));
		/****/
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("SystemInit"));
		/****/
		
		JComponent panel1 = getForm1();
		
		JScrollPane jscroll = new JScrollPane();
		jscroll.setOpaque(false);
		jscroll.getViewport().setOpaque(false);
		jscroll.setBorder(null);
		jscroll.getVerticalScrollBar().setUnitIncrement(15);
		jscroll.setViewportView(panel1);

        this.add(jscroll,BorderLayout.CENTER);
        this.addKeyListener(this);
		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				knopf2.requestFocus();
       	  	}
		});
		
		return;
	}
	/**************************************************************************/
	private JPanel getForm1(){
        //      								1.            2.    3.    4.     5.     6.    7.      8.     9.
		FormLayout lay = new FormLayout("right:max(60dlu;p), 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu",
       //1.    2.  3.   4. 5.   6.  7.   8.   9.  10.  
		"p, 5dlu, p, 5dlu,p,10dlu,p,40dlu,p, 2dlu,p, 10dlu, p, 10dlu, p, 10dlu");
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);	
		CellConstraints cc = new CellConstraints();
		
		// buttons
		knopf1 = new JButton("speichern");
		knopf1.setPreferredSize(new Dimension(70, 20));
		knopf1.addActionListener(this);		
		knopf1.setActionCommand("speichern");
		knopf1.addKeyListener(this);		
		
		knopf2 = new JButton("abbrechen");
		knopf2.setPreferredSize(new Dimension(70, 20));
		knopf2.addActionListener(this);		
		knopf2.setActionCommand("abbrechen");
		knopf2.addKeyListener(this);	
		
		oben = new JRadioButton((SystemConfig.desktopHorizontal ? "oberen" : "linken")+ " Container");
		oben.setHorizontalTextPosition(SwingConstants.LEFT);
		oben.setOpaque(false);
		bgroup.add(oben);
		unten = new JRadioButton((SystemConfig.desktopHorizontal ? "unteren" : "rechten")+ " Container");
		unten.setHorizontalTextPosition(SwingConstants.LEFT);
		unten.setOpaque(false);
		bgroup.add(unten);
		
		optimize = new JCheckBox("Fenstergröße automatisch optimieren");
		optimize.setHorizontalTextPosition(SwingConstants.LEFT);
		optimize.setOpaque(false);
		
		builder.addLabel("Fenster startet im.....",cc.xy(1,1));
		builder.add(oben,cc.xyw(3, 1,3,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		builder.add(unten,cc.xyw(3, 3,3,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		builder.add(optimize,cc.xyw(2, 5,4,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		
		builder.addSeparator("", cc.xyw(1, 6, 5,CellConstraints.DEFAULT,CellConstraints.CENTER));
		
		startWochenAnz= new JCheckBox();
		startWochenAnz.setSelected(SystemConfig.KalenderStartWochenAnsicht);
		builder.addLabel("Terminkalender in der Wochenansicht starten",cc.xy(1, 7));
		builder.add(startWochenAnz, cc.xy(5, 7, CellConstraints.RIGHT, CellConstraints.BOTTOM));
		
		builder.addLabel("Wochenanzeige mit folgendem Behandler starten", cc.xy(1, 9));
		defaultWA = new JComboBox();
		builder.add(defaultWA, cc.xyw(3,9,3));
		
		builder.addLabel("Normalansicht mit folgendem Behandlerset starten", cc.xy(1, 11));
		defaultNA = new JComboBox();
		builder.add(defaultNA, cc.xyw(3,11,3));

		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				comboFuellen();
				fuelleMitWerten();
       	  	}
		});
		
		builder.add(knopf1,cc.xy(3,13));
		builder.add(knopf2, cc.xy(5,13));
		builder.addSeparator("", cc.xyw(1, 15, 5));
		
		builder.getPanel().addKeyListener(this);
		return builder.getPanel();
	}
	/**************************************************************************/
	private void fuelleMitWerten(){
		if(SystemConfig.hmContainer.get("Kalender") == 0){
			oben.setSelected(true);
		}else{
			unten.setSelected(true);
		}
		if(SystemConfig.hmContainer.get("KalenderOpti") == 0){
			optimize.setSelected(false);
		}else{
			optimize.setSelected(true);
		}
	}	
	@SuppressWarnings("unchecked")
	private void comboFuellen(){
		int von = 0;
		int bis = ParameterLaden.vKKollegen.size();
		if(defaultWA.getItemCount()>0){
			defaultWA.removeAllItems();
		}
		for(von=0; von < bis; von++){
			defaultWA.addItem(ParameterLaden.getMatchcode(von));
		}	
		if(bis >=0){
			defaultWA.setSelectedItem(SystemConfig.KalenderStartWADefaultUser);
		}
		defaultWA.requestFocus();
		
		von = 0;
		bis = SystemConfig.aTerminKalender.size()+1;
		String[] fach = new String[bis];
		if(defaultNA.getItemCount()>0){
			defaultNA.removeAllItems();
		}
		defaultNA.addItem("./.");
		for(von=1; von < bis; von++){
			fach[von] = (String)((ArrayList)SystemConfig.aTerminKalender.get(von-1).get(0)).get(0);
			defaultNA.addItem(String.valueOf(fach[von]));
		}	
		if(bis >=0){
			defaultNA.setSelectedItem(SystemConfig.KalenderStartNADefaultSet);
		}
		defaultNA.requestFocus();		
	}
	/**************************************************************************/	
	private void speichernHandeln(){
		try{
			String wert;
			INIFile ini = INITool.openIni(Reha.proghome+"ini/"+Reha.aktIK+"/", "kalender.ini");
			
			wert = (unten.isSelected() ? "1" : "0");
			SystemConfig.hmContainer.put("Kalender", Integer.valueOf(wert));
			ini.setStringProperty("Container", "StarteIn",wert , null);
			
			wert = (optimize.isSelected() ? "1" : "0");
			SystemConfig.hmContainer.put("KalenderOpti",Integer.valueOf(wert));
			ini.setStringProperty("Container", "ImmerOptimieren",wert , null);

			ini.setStringProperty("Kalender", "StartWochenAnsicht", (startWochenAnz.isSelected() ? "1" : "0"), null);
			ini.setStringProperty("Kalender", "AnsichtDefault", defaultWA.getSelectedItem()+"@"+defaultNA.getSelectedItem(), null);
			INITool.saveIni(ini);
			SystemConfig.KalenderStartWochenAnsicht = startWochenAnz.isSelected();
			SystemConfig.KalenderStartNADefaultSet = defaultNA.getSelectedItem().toString();
			SystemConfig.KalenderStartWADefaultUser = defaultWA.getSelectedItem().toString();
			JOptionPane.showMessageDialog(null,"Konfiguration in kalender.ini erfolgreich gespeichert");
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null,"Speichern der Konfiguration in kalender.ini fehlgeschlagen");
		}
	}
	/**************************************************************************/	
	private void abbrechenHandeln(){
		SystemInit.abbrechen();	
	}
	/**************************************************************************/
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getActionCommand().equals("abbrechen")){abbrechenHandeln();}
		if(arg0.getActionCommand().equals("speichern")){speichernHandeln();}		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode() == 10 ){arg0.consume();}
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		if(arg0.getKeyCode() == 10 ){arg0.consume();}
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		if(arg0.getKeyCode() == 10 ){arg0.consume();}
		
	}
	/***********************************************************/
}