package systemEinstellungen;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;

import CommonTools.JRtaCheckBox;
import CommonTools.JRtaComboBox;
import CommonTools.JRtaRadioButton;
import CommonTools.JRtaTextField;

import CommonTools.INIFile;
import CommonTools.INITool;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SysUtilAbrechnungFormulare extends JXPanel implements KeyListener, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	PrintService[] services = null;	
	String[] drucker = null;
	JRtaComboBox[] jcmb = {null,null,null,null,null,null,null};
	JButton[] but = {null,null,null,null,null};
	JRtaTextField[] tf = {null,null,null,null};
	JRtaRadioButton[] rbut = {null,null,null,null,null,null,null,null};
	JRtaCheckBox cbemail = null;
	String[] exemplare = {"0","1","2","3","4","5"};
	ButtonGroup bg = new ButtonGroup();
	ButtonGroup bg2 = new ButtonGroup();
	ButtonGroup bg3 = new ButtonGroup();
	ButtonGroup bg4 = new ButtonGroup();
	public SysUtilAbrechnungFormulare(){
		super(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 20));
		/****/
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("SystemInit"));
	     services = PrintServiceLookup.lookupPrintServices(null, null);
			drucker = new String[services.length];
			
			for (int i = 0 ; i < services.length; i++){
				drucker[i] = services[i].getName();
			}
		/****/
	     //JLabel jlbl = new JLabel("");
	     //jlbl.setIcon(new ImageIcon(Reha.proghome+"icons/werkzeug.gif"));
	     //add(jlbl,BorderLayout.CENTER);
	     JScrollPane jscr = new JScrollPane();
	     jscr.setBorder(null);
	     jscr.setOpaque(false);
	     jscr.getViewport().setOpaque(false);
	     jscr.getVerticalScrollBar().setUnitIncrement(15);
	     jscr.setViewportView(getVorlagenSeite());
	     jscr.validate();
	     add(jscr,BorderLayout.CENTER);
	     add(getKnopfPanel(),BorderLayout.SOUTH);
		return;
	}
	/************** Beginn der Methode für die Objekterstellung und -platzierung *********/
	private JPanel getVorlagenSeite(){
		FormLayout lay = new FormLayout("right:max(80dlu;p), 20dlu, 120dlu, 4dlu, 40dlu", //, 4dlu, 40dlu, 4dlu, 40dlu",
			       //1.    2.  3.   4.  5.   6.  7.   8.  9.  10.  11. 12.  13.  14.  15. 16.   17. 18.   19.  20. 21.  22.  23.  24   25  26    27   28   29   30   31  32   33   34   35  36   37  38   39  40    41    42  43  44 45   46   47
					"p, 10dlu, p, 3dlu, p, 3dlu, p,  3dlu, p, 2dlu, p, 3dlu, p, 10dlu, p, 10dlu, p, 3dlu, p, 3dlu, p, 10dlu, p,  10dlu, p,  3dlu , p, 3dlu, p, 10dlu, p,10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p");
		
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		builder.addSeparator("Heilmittelabrechnung GKV", cc.xyw(1, 1, 5));
		builder.addLabel("Rechnungsformular",cc.xy(1,3));
		tf[0] = new JRtaTextField("nix",false);
		//tf[0].setEditable(false);
		builder.add(tf[0],cc.xy(3,3));
		builder.add((but[0] = macheBut("auswaehlen","gkvrechnwahl")),cc.xy(5,3));;

		builder.addLabel("Rechnungsdrucker",cc.xy(1,5));
		jcmb[0] = new JRtaComboBox(drucker);
		builder.add(jcmb[0],cc.xyw(3,5,3));
				
		builder.addLabel("Drucker für Taxierung",cc.xy(1,7));
		jcmb[1] = new JRtaComboBox(drucker);
		builder.add(jcmb[1],cc.xyw(3,7,3));
		
		
		builder.addLabel("folgende Ausdrucke erstellen",cc.xy(1,9));
		builder.add(rbut[0] = macheRadio("nur den Begleitzettel ausdrucken","nurbegleitzettel"),cc.xyw(3,9,3));
		rbut[0].setOpaque(false);
		bg.add(rbut[0]);
		builder.add(rbut[1] = macheRadio("Begleitzettel und Rechnung ausdrucken","beides"),cc.xyw(3,11,3));
		rbut[1].setOpaque(false);
		bg.add(rbut[1]);
		
		builder.addLabel("Rechnungsexemplare",cc.xy(1,13));
		jcmb[2] = new JRtaComboBox(exemplare);
		builder.add(jcmb[2],cc.xy(5,13));
		
		
		/********************************************/
		
		builder.addSeparator("Heilmittelabrechnung Privatpatienten", cc.xyw(1, 15, 5));
		
		builder.addLabel("Rechnungsformular",cc.xy(1,17));
		tf[1] = new JRtaTextField("nix",false);
		//tf[0].setEditable(false);
		builder.add(tf[1],cc.xy(3,17));
		builder.add((but[1] = macheBut("auswaehlen","prirechnwahl")),cc.xy(5,17));
		
		builder.addLabel("Rechnungsdrucker",cc.xy(1,19));
		jcmb[3] = new JRtaComboBox(drucker);
		builder.add(jcmb[3],cc.xyw(3,19,3));
		
		builder.addLabel("Rechnungsexemplare",cc.xy(1,21));
		jcmb[4] = new JRtaComboBox(exemplare);
		builder.add(jcmb[4],cc.xy(5,21));
		
		builder.addSeparator("Heilmittelabrechnung Berufsgenossenschaft", cc.xyw(1, 23, 5));

		builder.addLabel("Rechnungsformular",cc.xy(1,25));
		tf[2] = new JRtaTextField("nix",false);
		//tf[0].setEditable(false);
		builder.add(tf[2],cc.xy(3,25));
		builder.add((but[2] = macheBut("auswaehlen","bgerechnwahl")),cc.xy(5,25));
		
		builder.addLabel("Rechnungsdrucker",cc.xy(1,27));
		jcmb[5] = new JRtaComboBox(drucker);
		builder.add(jcmb[5],cc.xyw(3,27,3));
		
		builder.addLabel("Rechnungsexemplare",cc.xy(1,29));
		jcmb[6] = new JRtaComboBox(exemplare);
		builder.add(jcmb[6],cc.xy(5,29));
		
		builder.addSeparator("Gemeinsame Einstellungen", cc.xyw(1, 31, 5));
		
		builder.addLabel("alle Ausdrucke",cc.xy(1,33));
		builder.add(rbut[2] = macheRadio("direkt zum Drucker leiten","druckdirekt"),cc.xyw(3,33,3));
		rbut[2].setOpaque(false);
		bg2.add(rbut[2]);
		builder.add(rbut[3] = macheRadio("im OpenOffice-Writer öffnen","druckoffice"),cc.xyw(3,35,3));
		rbut[3].setOpaque(false);
		bg2.add(rbut[3]);
		
		
		builder.addLabel("Vor dem Versand der  302-er Mail",cc.xy(1, 37));
		builder.add(cbemail = new JRtaCheckBox("immer fragen"), cc.xyw(3, 37,3));
		cbemail.setOpaque(false);
		

		

		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				doEinstellungen();
				return null;
			}
			
		}.execute();
		return builder.getPanel();
	}
	
	private JPanel getKnopfPanel(){
		
		
		but[3] = macheBut("abbrechen","abbrechen");
		but[4] = macheBut("speichern","speichern");
									//      1.                      2.    3.    4.     5.     6.    7.      8.     9.
		FormLayout jpanlay = new FormLayout("right:max(120dlu;p), 80dlu, 40dlu, 4dlu, 40dlu",
       //1.    2. 3.   4.   5.   6.     7.    8. 9.  10.  11. 12. 13.  14.  15. 16.  17. 18.  19.   20.    21.   22.   23.
		"p, 10dlu, p");
		
		PanelBuilder jpan = new PanelBuilder(jpanlay);
		jpan.getPanel().setOpaque(false);		
		CellConstraints jpancc = new CellConstraints();
		
		jpan.addSeparator("", jpancc.xyw(1,1,5));
		jpan.add(but[3], jpancc.xy(3,3));
		jpan.add(but[4], jpancc.xy(5,3));
		jpan.addLabel("Änderungen übernehmen?", jpancc.xy(1,3));
		
		
		return jpan.getPanel();
	}
	private JButton macheBut(String titel,String cmd){
		JButton but = new JButton(titel);
		but.setActionCommand(cmd);
		but.addActionListener(this);
		return but;
	}
	private JRtaRadioButton macheRadio(String titel,String cmd){
		JRtaRadioButton but = new JRtaRadioButton(titel);
		but.setActionCommand(cmd);
		but.addActionListener(this);
		return but;
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
		String cmd = e.getActionCommand();
		if(cmd.equals("gkvrechnwahl")){
			doWaehlen(tf[0]);
			return;
		}
		if(cmd.equals("prirechnwahl")){
			doWaehlen(tf[1]);
			return;
		}
		if(cmd.equals("bgerechnwahl")){
			doWaehlen(tf[2]);
			return;
		}
		if(cmd.equals("nurbegleitzettel")){
			jcmb[2].setSelectedIndex(0);
			jcmb[2].setEnabled(false);
			return;
		}
		if(cmd.equals("beides")){
			jcmb[2].setEnabled(true);
			return;
		}
		if(cmd.equals("abbrechen")){
			SystemInit.abbrechen();
			//SystemUtil.thisClass.parameterScroll.requestFocus();
			return;
		}
		if(cmd.equals("speichern")){
			doSpeichern();
			return;
		}
	}
	private void doEinstellungen(){
		/**GKV**/
		tf[0].setText(SystemConfig.hmAbrechnung.get("hmgkvformular"));
		jcmb[0].setSelectedItem(SystemConfig.hmAbrechnung.get("hmgkvrechnungdrucker"));
		jcmb[1].setSelectedItem(SystemConfig.hmAbrechnung.get("hmgkvtaxierdrucker"));
		String wert = SystemConfig.hmAbrechnung.get("hmgkvrauchdrucken");
		if(wert.equals("1")){
			rbut[1].setSelected(true);
		}else{
			rbut[0].setSelected(true);
		}
		jcmb[2].setSelectedItem(SystemConfig.hmAbrechnung.get("hmgkvrexemplare"));
		/***PRI****/
		tf[1].setText(SystemConfig.hmAbrechnung.get("hmpriformular"));
		jcmb[3].setSelectedItem(SystemConfig.hmAbrechnung.get("hmpridrucker"));
		jcmb[4].setSelectedItem(SystemConfig.hmAbrechnung.get("hmpriexemplare"));		
		/***BGE****/		
		tf[2].setText(SystemConfig.hmAbrechnung.get("hmbgeformular"));
		jcmb[5].setSelectedItem(SystemConfig.hmAbrechnung.get("hmbgedrucker"));
		jcmb[6].setSelectedItem(SystemConfig.hmAbrechnung.get("hmbgeexemplare"));	
		wert = SystemConfig.hmAbrechnung.get("hmallinoffice");
		if(wert.equals("1")){
			rbut[3].setSelected(true);
		}else{
			rbut[2].setSelected(true);
		}
		wert = SystemConfig.hmAbrechnung.get("hmaskforemail");
		if(wert.equals("1")){
			cbemail.setSelected(true);
		}else{
			cbemail.setSelected(false);
		}
		
		
	}
	private void doSpeichern(){
		try{
		String wert = "";
		INIFile inif = INITool.openIni(Reha.proghome+"ini/"+Reha.aktIK+"/", "abrechnung.ini");
		inif.setStringProperty("HMGKVRechnung", "Rformular",tf[0].getText().trim() , null);
		inif.setStringProperty("HMGKVRechnung", "Rdrucker",((String) jcmb[0].getSelectedItem()).trim() , null);
		inif.setStringProperty("HMGKVRechnung", "Tdrucker",((String) jcmb[1].getSelectedItem()).trim() , null);
		wert = (rbut[1].isSelected() ? "1" : "0");
		//inif.setStringProperty("HMGKVRechnung", "Begleitzettel","1" , null);
		inif.setStringProperty("HMGKVRechnung", "Rauchdrucken",wert , null);
		inif.setStringProperty("HMGKVRechnung", "Rexemplare",(String)jcmb[2].getSelectedItem() , null);

		inif.setStringProperty("HMPRIRechnung", "Pformular",tf[1].getText().trim() , null);
		inif.setStringProperty("HMPRIRechnung", "Pdrucker",((String) jcmb[3].getSelectedItem()).trim() , null);
		inif.setStringProperty("HMPRIRechnung", "Pexemplare",(String)jcmb[4].getSelectedItem() , null);
		
		inif.setStringProperty("HMBGERechnung", "Bformular",tf[2].getText().trim() , null);
		inif.setStringProperty("HMBGERechnung", "Bdrucker",((String) jcmb[5].getSelectedItem()).trim() , null);
		inif.setStringProperty("HMBGERechnung", "Bexemplare",(String)jcmb[6].getSelectedItem() , null);
		wert = (rbut[3].isSelected() ? "1" : "0");
		inif.setStringProperty("GemeinsameParameter", "InOfficeStarten",wert , null);
		wert = (cbemail.isSelected() ? "1" : "0");
		inif.setStringProperty("GemeinsameParameter", "FragenVorEmail",wert , null);
		INITool.saveIni(inif);
		JOptionPane.showMessageDialog(null,"Die Werte wurden erfolgreich in abrechung.ini gespeichert");
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				SystemConfig.AbrechnungParameter();
				return null;
			}
		}.execute();
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null,"Fehler beim speichern in die abrechnung.ini!!!");
		}
	}
	private void doWaehlen(JRtaTextField tf){
		String s = dateiDialog();
		tf.setText(s);
	}
	private String dateiDialog(){

		final JFileChooser chooser = new JFileChooser("Verzeichnis wählen");
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        final File file = new File(Reha.proghome+"/vorlagen/"+Reha.aktIK);

        chooser.setCurrentDirectory(file);

        chooser.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)
                        || e.getPropertyName().equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
                    //final File f = (File) e.getNewValue();
                }
            }
        });
        chooser.setVisible(true);
        //thisClass.setCursor(Reha.thisClass.normalCursor);
        final int result = chooser.showOpenDialog(null);
        chooser.setVisible(false);
        if (result == JFileChooser.APPROVE_OPTION) {
            File inputVerzFile = chooser.getSelectedFile();
            //String inputVerzStr = inputVerzFile.getPath();
            
            //System.out.println("Eingabepfad:" + inputVerzStr);
            if(inputVerzFile.getName().trim().equals("")){
            	return  "";
            	//vorlagenname.setText(SystemConfig.oTerminListe.NameTemplate);
            }else{
            	return inputVerzFile.getName().trim();
            	//vorlagenname.setText(inputVerzFile.getName().trim());	
            }
        }else{
        	return  "";
        }
 

	}	

}
