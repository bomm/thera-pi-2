
package systemEinstellungen;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ocf.OcKVK;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;

import uk.co.mmscomputing.device.scanner.Scanner;
import uk.co.mmscomputing.device.scanner.ScannerIOException;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SysUtilGeraete extends JXPanel implements KeyListener, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JCheckBox kvkakt = null;
	JCheckBox docscanakt = null;
	JCheckBox barcodeakt = null;
	JCheckBox ecakt = null;
	JCheckBox webcam = null;
	JComboBox kvkgeraet = null;
	
	//JComboBox kvkan = null;
	JComboBox docscangeraet = null;
	JComboBox barcodegeraet = null;
	JComboBox barcodean = null;
	JComboBox ecgeraet = null;
	JComboBox ecan = null;
	JComboBox webcamsize = null;
	JButton kvktest = null;
	JButton knopf1 = null;
	JButton knopf2 = null;
	boolean readerTestSuccess = true;
	String[] anschluesse = new String[] {"./.", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "COM10","USB"};
	Scanner scanner;
	public SysUtilGeraete(){
		
		
		super(new BorderLayout());
		//System.out.println("Aufruf SysUtilKasse");
		this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 20));
		/****/
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("SystemInit"));
	     
	     JScrollPane jscr = new JScrollPane();
	     jscr.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
	     jscr.setOpaque(false);
	     jscr.getViewport().setOpaque(false);
	     jscr.setViewportView(getVorlagenSeite());
	     jscr.getVerticalScrollBar().setUnitIncrement(15);
	     jscr.validate();
	     
	     add(jscr,BorderLayout.CENTER);

	     add(getKnopfPanel(),BorderLayout.SOUTH);
		return;
	}
	/************** Beginn der Methode für die Objekterstellung und -platzierung *********/
	
private JPanel getKnopfPanel(){
		
	knopf1 = new JButton("abbrechen");
	knopf1.setPreferredSize(new Dimension(70, 20));
	knopf1.addActionListener(this);
	knopf1.setActionCommand("abbruch");
	knopf1.addKeyListener(this);
	knopf2 = new JButton("speichern");
	knopf2.setPreferredSize(new Dimension(70, 20));
	knopf2.addActionListener(this);
	knopf2.setActionCommand("speichern");
	knopf2.addKeyListener(this);
	
									//      1.                      2.    3.    4.     5.     6.    7.      8.     9.
		FormLayout jpanlay = new FormLayout("right:max(120dlu;p), 60dlu, 60dlu, 10dlu, 60dlu",
       //1.    2. 3.   4.   5.   6.     7.    8. 9.  10.  11. 12. 13.  14.  15. 16.  17. 18.  19.   20.    21.   22.   23.
		"p, 10dlu, p");
		
		PanelBuilder jpan = new PanelBuilder(jpanlay);
		jpan.getPanel().setOpaque(false);		
		CellConstraints jpancc = new CellConstraints();
		
		jpan.addSeparator("Änderungen übernehmen?", jpancc.xyw(1,1,5));
		jpan.add(knopf1, jpancc.xy(3,3));
		jpan.add(knopf2, jpancc.xy(5,3));
		
		
		
		return jpan.getPanel();
	}
	
	
	
	private JPanel getVorlagenSeite(){
		
		kvkakt = new JCheckBox();
		kvkakt.setSelected((SystemConfig.sReaderAktiv.equals("1") ? true : false));

		docscanakt = new JCheckBox();
		docscanakt.setSelected((SystemConfig.hmDokuScanner.get("aktivieren").equals("1") ? true : false));
		
		barcodeakt = new JCheckBox();
		barcodeakt.setSelected((SystemConfig.sBarcodeAktiv.equals("1") ? true : false));

		ecakt = new JCheckBox();
		ecakt.setEnabled(false);
		
		kvkgeraet = new JComboBox( (String[]) SystemConfig.hmGeraete.get("Kartenleser"));
		kvkgeraet.setSelectedItem(SystemConfig.sReaderName);
		
		kvktest = new JButton("testen....");
		kvktest.setActionCommand("kvktesten");
		kvktest.addActionListener(this);
		//kvkan = new JComboBox((String[]) SystemConfig.hmGeraete.get("CTApi"));
		//kvkan.setSelectedItem(SystemConfig.sReaderCtApiLib);
		
		docscangeraet = new JComboBox();
		new SwingWorker<String,String>(){
			@Override
			protected String doInBackground() throws Exception {
				 scanner = Scanner.getDevice();
				    try {
						String[] names = scanner.getDeviceNames();
						for(int i = 0; i < names.length;i++){
							docscangeraet.addItem(names[i]);
						}
						if(!scanner.getSelectedDeviceName().equals(SystemConfig.sDokuScanner)){
							docscangeraet.setSelectedItem(scanner.getSelectedDeviceName());
						}else{
							docscangeraet.setSelectedItem(SystemConfig.sDokuScanner);	
						}
						
					} catch (ScannerIOException e2) {
						e2.printStackTrace();
					}
				return null;
			}
			
		}.execute();


		barcodegeraet = new JComboBox(SystemConfig.hmGeraete.get("Barcode"));
		barcodegeraet.setSelectedItem(SystemConfig.sBarcodeScanner);

		barcodean = new JComboBox(anschluesse);
		barcodean.setSelectedItem(SystemConfig.sBarcodeCom);
		
		ecgeraet = new JComboBox(SystemConfig.hmGeraete.get("ECKarte"));
		ecgeraet.setEnabled(false);
		ecan = new JComboBox(anschluesse);
		ecan.setEnabled(false);
		
		webcam = new JCheckBox(); 
		webcam.setSelected((SystemConfig.sWebCamActive.equals("1") ? true : false));
		
		webcamsize = new JComboBox(new String[] {"640 x 480","320 x 240"});
		webcamsize.setSelectedIndex( (SystemConfig.sWebCamSize[0]==640 ? 0 : 1) );

        //                                      1.            2.    3.      4.    5.     6.                   7.      8.     9.
		FormLayout lay = new FormLayout("right:max(100dlu;p), 10dlu, 80dlu, 10dlu, right:max(100dlu;p)",
				   //1.    2. 3.   4.  5.   6.   7.  8.    9.   10. 11.  12. 13. 14.   15. 16.  17. 18.  19. 20.   21.  22.  23.  24.  25   26  27  28   29  30    31   32    33    34  35  36     37
					"p, 10dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p ,10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 10dlu ,p, 10dlu ,p,   2dlu, p ");
		
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator("KV-Karten-Lesegerät", cc.xyw(1,1,5));
		builder.addLabel("aktivieren", cc.xy(3,3));
		builder.add(kvkakt, cc.xy(5,3,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		builder.addLabel("Gerät", cc.xy(3,5));
		builder.add(kvkgeraet, cc.xyw(4,5,2));
		builder.addLabel("Anschluss", cc.xy(3,7));
		//builder.add(kvkan, cc.xyw(4,7,2));
		builder.add(kvktest, cc.xyw(4,7,2));
		builder.addSeparator("Dokumentenscanner", cc.xyw(1,9,5));
		builder.addLabel("aktivieren", cc.xy(3,11));
		builder.add(docscanakt, cc.xy(5,11));
		builder.addLabel("Gerät", cc.xy(3,13));
		builder.add(docscangeraet, cc.xyw(4,13,2));
		builder.addSeparator("Barcodescanner", cc.xyw(1,15,5));
		builder.addLabel("aktivieren", cc.xy(3,17));
		builder.add(barcodeakt, cc.xy(5,17));
		builder.addLabel("Gerät", cc.xy(3,19));
		builder.add(barcodegeraet, cc.xyw(4,19,2));
		builder.addLabel("Anschluss", cc.xy(3,21));
		builder.add(barcodean, cc.xyw(4,21,2));
		builder.addSeparator("EC-Karte", cc.xyw(1,23,5));
		builder.addLabel("aktivieren",cc.xy(3,25));
		builder.add(ecakt, cc.xy(5,25));
		builder.addLabel("Gerät", cc.xy(3,27));
		builder.add(ecgeraet, cc.xyw(4,27,2));
		builder.addLabel("Anschluss", cc.xy(3,29));
		builder.add(ecan, cc.xyw(4,29,2));
		builder.addSeparator("Patienten-Fotos (JM-Studio muß installiert sein)",cc.xyw(1,31,5));		
		builder.addLabel("Web-Cam aktivieren",cc.xy(3,33));
		builder.add(webcam,cc.xy(5,33));
		builder.addLabel("Videoauflösung",cc.xy(3,35));
		builder.add(webcamsize,cc.xyw(4,35,2));
		return builder.getPanel();
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
		String com = e.getActionCommand();
		if(com.equals("speichern")){
			doSpeichern();
			return;
		}
		if(com.equals("abbruch")){
			SystemInit.abbrechen();
			//SystemUtil.thisClass.parameterScroll.requestFocus();			
		}
		if(com.equals("kvktesten")){
			readerTest();
		}

		
	}
	private void readerTest(){
		OcKVK ocKVK = null;
		String reader = null;
		String api = null;
		boolean ocAktive = false;
		//Damit der ganze Scheiß nicht abstürtzt muß hier noch eine
		//Routine eingebaut werden die überprüft ob die angegebene .dll bzw. .so überhaupt existiert.
		try {
			if(Reha.thisClass.ocKVK != null){
				Reha.thisClass.ocKVK.TerminalDeaktivieren();
				ocAktive = true;
			}
			int antwort = JOptionPane.showConfirmDialog(null,"Bitte KV-Karte einlegen und ok. drücken","Vorbereitung für Reader-Test",JOptionPane.OK_CANCEL_OPTION);
			if(antwort == JOptionPane.OK_OPTION){
				reader = kvkgeraet.getSelectedItem().toString().trim().replace(" ", "_")+"test";
				api = SystemConfig.hmGeraete.get("CTApi")[kvkgeraet.getSelectedIndex()];
				System.out.println("Starte Test mit Reader="+reader+" und API="+api);
				
				ocKVK = new OcKVK(reader,api,true);
				if(ocKVK.terminalOk){
					SystemConfig.hmKVKDaten.clear();
					ocKVK.lesen();
					if(!SystemConfig.hmKVKDaten.isEmpty()){
						//ocKVK.TerminalDeaktivieren();
						Set<?> entries = SystemConfig.hmKVKDaten.entrySet();
					    Iterator<?> it = entries.iterator();
					    String message = "";
					    while (it.hasNext()) {
					      Map.Entry<?,?> entry = (Map.Entry<?, ?>) it.next();
					      message = message + entry.getKey().toString()+"="+entry.getValue().toString()+"\n";
					    }
					    JOptionPane.showMessageDialog(null, "Glückwunsch der Card-Reader funktoniert\n\nnachfolgend die eingelesenen Rohdaten der KV-Karte\n"+message);
					}else{
						JOptionPane.showMessageDialog(null, "Card-Reader konnte nicht angesprochen werden - Fehler:1");
						ocKVK = null;
					}

				}else{
					JOptionPane.showMessageDialog(null, "Card-Reader konnte nicht angesprochen werden - Fehler:2");
					ocKVK = null;
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Card-Reader konnte nicht angesprochen werden oder keine KV-Karte im Reader - Fehler:3");
			ocKVK = null;
			//e.printStackTrace();
		}
		if(ocAktive){
			try {
				Reha.thisClass.ocKVK = new OcKVK(SystemConfig.sReaderName.trim().replace(" ", "_"),
						SystemConfig.sReaderCtApiLib,false);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	
	
	private void doSpeichern(){
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/geraete.ini");

		SystemConfig.sReaderAktiv = (kvkakt.isSelected() ? "1" : "0");
		inif.setStringProperty("KartenLeser", "KartenLeserAktivieren",SystemConfig.sReaderAktiv , null);
		
		SystemConfig.sReaderName = (kvkgeraet.getSelectedItem()==null ? "" : (String)kvkgeraet.getSelectedItem()); 
		inif.setStringProperty("KartenLeser", "KartenLeserName",SystemConfig.sReaderName , null);

		SystemConfig.sReaderCtApiLib = (kvkgeraet.getSelectedItem()==null ? "" : 
			SystemConfig.hmGeraete.get("CTApi")[kvkgeraet.getSelectedIndex()] );
		
		
		inif.setStringProperty("KartenLeser", "KartenLeserCTAPILib",SystemConfig.sReaderCtApiLib , null);
		/***************************/
		SystemConfig.sBarcodeAktiv = (barcodeakt.isSelected() ? "1" : "0");
		inif.setStringProperty("BarcodeScanner", "BarcodeScannerAktivieren",SystemConfig.sBarcodeAktiv , null);
		
		SystemConfig.sBarcodeScanner = (barcodegeraet.getSelectedItem()==null ? "" : (String)barcodegeraet.getSelectedItem());
		inif.setStringProperty("BarcodeScanner", "BarcodeScannerName",SystemConfig.sBarcodeScanner , null);
		
		SystemConfig.sBarcodeCom = (barcodean.getSelectedItem()==null ? "" : (String)barcodean.getSelectedItem());
		inif.setStringProperty("BarcodeScanner", "BarcodeScannerAnschluss",SystemConfig.sBarcodeCom , null);
		/***************************/
		SystemConfig.hmDokuScanner.put("aktivieren",(docscanakt.isSelected() ? "1" : "0"));
		inif.setStringProperty("DokumentenScanner", "DokumentenScannerAktivieren",(docscanakt.isSelected() ? "1" : "0") , null);

		SystemConfig.sDokuScanner = (docscangeraet.getSelectedItem()==null ? "" : (String)docscangeraet.getSelectedItem());
		inif.setStringProperty("DokumentenScanner", "DokumentenScannerName",SystemConfig.sDokuScanner , null);
		
		/***************************/
		SystemConfig.sWebCamActive = (webcam.isSelected() ? "1" : "0");
		inif.setStringProperty("WebCam", "WebCamActive",SystemConfig.sWebCamActive , null);
		
		SystemConfig.sWebCamSize[0] = (webcamsize.getSelectedIndex() == 0 ? 640 : 320);
		inif.setIntegerProperty("WebCam", "WebCamX",SystemConfig.sWebCamSize[0] , null);

		SystemConfig.sWebCamSize[1] = (webcamsize.getSelectedIndex() == 0 ? 480 : 240);
		inif.setIntegerProperty("WebCam", "WebCamY",SystemConfig.sWebCamSize[1] , null);

		inif.save();
		
		JOptionPane.showMessageDialog(null, "Gerätekonfiguration wurde erfolgrich gespeichert und steht nach dem nächsten\nStart der Software zur Verfügung");
	}
}
