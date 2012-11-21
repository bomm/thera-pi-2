package systemEinstellungen;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterJob;
import java.io.File;

import javax.print.PrintService;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;


import CommonTools.ButtonTools;
import CommonTools.JCompTools;
import CommonTools.JRtaCheckBox;
import CommonTools.JRtaTextField;

import CommonTools.INIFile;
import CommonTools.INITool;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SysUtilVerkauf extends JXPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] druckernamen, spaltenNamen = {"ArtikelID", "Beschreibung", "Anzahl", "EinzelPreis", "GesamtPreis", "MwSt",
			"Rabatt", "Bemerkung", "Nettopreis"};
	private Integer[] spaltenAnzahl = {1, 2, 3, 4, 5, 6};;
	
	private JComboBox rechnungSpalte1, rechnungSpalte2, rechnungSpalte3, rechnungSpalte4, rechnungSpalte5, rechnungSpalte6,
	bonSpalte1, bonSpalte2, bonSpalte3, bonSpalte4, bonSpalte5, bonSpalte6, rechnungDrucker, bonDrucker, rechnungSpalten,
	bonSpalten;
	
	private JButton rechnungVorlageB, bonVorlageB, speichern, abbruch;
	
	private JRtaCheckBox bonAnpassen, sofortDrucken;
	
	private JRtaTextField rechnungVorlage, bonVorlage, rechnungExemplare, bonSeitenlaenge;
	
	private INIFile inif;
	
	private ActionListener al;
	


	SysUtilVerkauf() {
		super(new BorderLayout());
		//super(new GridLayout(1,1));
		this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 0));
		activateListener();
		
		
		this.setOpaque(false);
		
		JScrollPane jscr = new JScrollPane();
	     jscr.setBorder(null);
	     jscr.setOpaque(false);
	     jscr.getViewport().setOpaque(false);
	     jscr.getVerticalScrollBar().setUnitIncrement(15);
	     jscr.setViewportView(getContent());
	     jscr.validate();
	    
	     inif = INITool.openIni(Reha.proghome+"ini/"+Reha.aktIK+"/", "verkauf.ini");
		
		
		//add(getContent(),BorderLayout.CENTER);
		ladeEinstellungen();
		this.add(jscr,BorderLayout.CENTER);
		this.add(getKnopfPanel(),BorderLayout.SOUTH);
		System.out.println(getWidth()+"/"+getHeight());
	}
	
	private JPanel getKnopfPanel(){
		abbruch = ButtonTools.macheButton("abbrechen", "abbrechen", al);
		speichern = ButtonTools.macheButton("speichern", "speicher", al);
		/*
		abbruch = new JButton("abbrechen");
		abbruch.setActionCommand("abbrechen");
		abbruch.addActionListener(al);
		speichern = new JButton("speichern");
		speichern.setActionCommand("speichern");
		speichern.addActionListener(al);
		*/
									//      1.                      2.    3.    4.     5.     6.    7.      8.     9.
		FormLayout jpanlay = new FormLayout("right:max(150dlu;p), 60dlu, 60dlu, 4dlu, 60dlu",
       //1.    2. 3.   4.   5.   6.     7.    8. 9.  10.  11. 12. 13.  14.  15. 16.  17. 18.  19.   20.    21.   22.   23.
		"10dlu,p, 10dlu, p");
		
		PanelBuilder jpan = new PanelBuilder(jpanlay);
		jpan.getPanel().setOpaque(false);		
		CellConstraints jpancc = new CellConstraints();
		
		jpan.addSeparator("", jpancc.xyw(1,2,5));
		jpan.add(abbruch, jpancc.xy(3,4));
		jpan.add(speichern, jpancc.xy(5,4));
		jpan.addLabel("Änderungen übernehmen?", jpancc.xy(1,4));
		
		jpan.getPanel().validate();
		return jpan.getPanel();
	}
	
	
	private JPanel getContent() {

		
		//                1      2     3      4     5      6
		String xwerte = "15dlu, 3dlu, 60dlu, 5dlu, 40dlu:g, 15dlu";
		//                 1    2    3    4  5    6  7     8    9   10   11  12   13  14   15  16   17  18  19   20
		String ywerte = "5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p," +
		//		"" 21  22  23  24   25  26   27  28  29    30  31   32  33  34   35   36  37  38  39   40 
				"5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p," +
		//        41  42   43   44  45  46  47   48   49
				"5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu";
		FormLayout lay = new FormLayout(xwerte, ywerte);
		CellConstraints cc = new CellConstraints();
		//JXPanel pane = new JXPanel();
		PanelBuilder pane = new PanelBuilder(lay);
		pane.setDefaultDialogBorder();
		pane.getPanel().setOpaque(false);
				
		
		pane.addSeparator("Bondruck", cc.xyw(1,2,5));
		//JXLabel lab = new JXLabel("Bon:");
		//pane.add(lab, cc.xyw(2, 2, 2));
		
		JXLabel lab = new JXLabel("Drucker:");
		pane.add(lab, cc.xy(3, 4));
		
		
		PrintService[] printers = PrinterJob.lookupPrintServices();
		druckernamen = new String[printers.length];
		for(int i = 0; i < printers.length; i++) {
			druckernamen[i] = printers[i].getName();
		}
		bonDrucker = new JComboBox(druckernamen);
		pane.add(bonDrucker, cc.xy(5, 4));
		
		lab = new JXLabel("Vorlage:");
		pane.add(lab, cc.xy(3, 6));
		
		bonVorlage = new JRtaTextField("nix",false);
		bonVorlage.setLayout(new BorderLayout());
		bonVorlage.add(bonVorlageB = new JXButton("auswählen"), BorderLayout.EAST);
		bonVorlageB.setActionCommand("vorlageBon");
		bonVorlageB.addActionListener(al);
		pane.add(bonVorlage, cc.xy(5, 6));
		
		lab = new JXLabel("Anzahlspalten:");
		pane.add(lab, cc.xy(3, 8));
		
		bonSpalten = new JComboBox(spaltenAnzahl);
		pane.add(bonSpalten, cc.xy(5, 8));
		
		lab = new JXLabel("Spalte 1:");
		pane.add(lab, cc.xy(3, 10));
		
		bonSpalte1 = new JComboBox(spaltenNamen);
		pane.add(bonSpalte1, cc.xy(5, 10));
		
		lab = new JXLabel("Spalte 2:");
		pane.add(lab, cc.xy(3, 12));
		
		bonSpalte2 = new JComboBox(spaltenNamen);
		pane.add(bonSpalte2, cc.xy(5, 12));
		
		lab = new JXLabel("Spalte 3:");
		pane.add(lab, cc.xy(3, 14));
		
		bonSpalte3 = new JComboBox(spaltenNamen);
		pane.add(bonSpalte3, cc.xy(5, 14));
		
		lab = new JXLabel("Spalte 4:");
		pane.add(lab, cc.xy(3, 16));
		
		bonSpalte4 = new JComboBox(spaltenNamen);
		pane.add(bonSpalte4, cc.xy(5, 16));
		
		lab = new JXLabel("Spalte 5:");
		pane.add(lab, cc.xy(3, 18));
		
		bonSpalte5 = new JComboBox(spaltenNamen);
		pane.add(bonSpalte5, cc.xy(5, 18));
		
		lab = new JXLabel("Spalte 6:");
		pane.add(lab, cc.xy(3, 20));
		
		bonSpalte6 = new JComboBox(spaltenNamen);
		pane.add(bonSpalte6, cc.xy(5, 20));
		
		lab = new JXLabel("Seitenlänge anpassen?");
		pane.add(lab, cc.xy(3, 22));
		
		bonAnpassen = new JRtaCheckBox();
		pane.add(bonAnpassen, cc.xy(5, 22));
		
		lab = new JXLabel("Seitenlänge pro Artikel:");
		pane.add(lab, cc.xy(3, 24));
		
		bonSeitenlaenge = new JRtaTextField("nix", false);
		bonSeitenlaenge.setLayout(new BorderLayout());
		bonSeitenlaenge.add(new JXLabel("mm * 100"), BorderLayout.EAST);
		pane.add(bonSeitenlaenge, cc.xy(5, 24));
		
		pane.addSeparator("Rechnungsdruck", cc.xyw(1,26,5));
		/*
		lab = new JXLabel("Rechnung:");
		pane.add(lab, cc.xyw(2, 26, 2));
		*/
		lab = new JXLabel("Drucker:");
		pane.add(lab, cc.xy(3, 28));
		
		rechnungDrucker = new JComboBox(druckernamen);
		pane.add(rechnungDrucker, cc.xy(5, 28));
		
		lab = new JXLabel("Vorlage:");
		pane.add(lab, cc.xy(3, 30));
		
		rechnungVorlage = new JRtaTextField("nix",false);
		rechnungVorlage.setLayout(new BorderLayout());
		rechnungVorlage.add(rechnungVorlageB = new JXButton("auswählen"), BorderLayout.EAST);
		rechnungVorlageB.setActionCommand("vorlageRechnung");
		rechnungVorlageB.addActionListener(al);
		pane.add(rechnungVorlage, cc.xy(5, 30));
		
		lab = new JXLabel("Anzahlspalten:");
		pane.add(lab, cc.xy(3, 32));
		
		rechnungSpalten = new JComboBox(spaltenAnzahl);
		pane.add(rechnungSpalten, cc.xy(5, 32));
		
		lab = new JXLabel("Spalte 1:");
		pane.add(lab, cc.xy(3, 34));
		
		rechnungSpalte1 = new JComboBox(spaltenNamen);
		pane.add(rechnungSpalte1, cc.xy(5, 34));
		
		lab = new JXLabel("Spalte 2:");
		pane.add(lab, cc.xy(3, 36));
		
		rechnungSpalte2 = new JComboBox(spaltenNamen);
		pane.add(rechnungSpalte2, cc.xy(5, 36));
		
		lab = new JXLabel("Spalte 3:");
		pane.add(lab, cc.xy(3, 38));
		
		rechnungSpalte3 = new JComboBox(spaltenNamen);
		pane.add(rechnungSpalte3, cc.xy(5, 38));
		
		lab = new JXLabel("Spalte 4:");
		pane.add(lab, cc.xy(3, 40));
		
		rechnungSpalte4 = new JComboBox(spaltenNamen);
		pane.add(rechnungSpalte4, cc.xy(5, 40));
		
		lab = new JXLabel("Spalte 5:");
		pane.add(lab, cc.xy(3, 42));
		
		rechnungSpalte5 = new JComboBox(spaltenNamen);
		pane.add(rechnungSpalte5, cc.xy(5, 42));
		
		lab = new JXLabel("Spalte 6:");
		pane.add(lab, cc.xy(3, 44));
		
		rechnungSpalte6 = new JComboBox(spaltenNamen);
		pane.add(rechnungSpalte6, cc.xy(5, 44));
		
		lab = new JXLabel("Exemplare:");
		pane.add(lab, cc.xy(3, 46));
		
		rechnungExemplare = new JRtaTextField("ZAHLEN", false);
		pane.add(rechnungExemplare, cc.xy(5, 46));
		
		lab = new JXLabel("Sofort drucken?");
		pane.add(lab, cc.xy(3, 48));
		
		sofortDrucken = new JRtaCheckBox();
		pane.add(sofortDrucken, cc.xy(5, 48));
		/*
		speichern = new JXButton("speichern");
		speichern.setActionCommand("speicher");
		speichern.addActionListener(al);
		pane.add(speichern, cc.xy(5, 50));
		*/
		pane.getPanel().validate();

		return pane.getPanel();
	}
	
	private String dateiWaehlen() {
		String returnstmt = "";
		JFileChooser explorer = new JFileChooser();
		explorer.setDialogType(JFileChooser.OPEN_DIALOG);
		explorer.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		explorer.setCurrentDirectory(new File(Reha.thisClass.proghome+"/vorlagen"));
		
		explorer.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().toLowerCase().endsWith(".ott");
			}
			public String getDescription() {
				return "OpenOffice.org Vorlagen";
			}
		});
		
		explorer.setVisible(true);
		
		int state = explorer.showOpenDialog(null);
		
		if(state == JFileChooser.APPROVE_OPTION) {
			returnstmt = explorer.getSelectedFile().getName();
		} else {
			
		}
		
		return returnstmt;
	}
	
	private void ladeEinstellungen() {
		bonAnpassen.setSelected(inif.getBooleanProperty("Bon", "SeitenLaengeAendern"));
		sofortDrucken.setSelected(inif.getBooleanProperty("Bon", "SofortDrucken"));
		
		rechnungSpalte1.setSelectedItem(inif.getStringProperty("Rechnung", "Spalte1"));
		rechnungSpalte2.setSelectedItem(inif.getStringProperty("Rechnung", "Spalte2"));
		rechnungSpalte3.setSelectedItem(inif.getStringProperty("Rechnung", "Spalte3"));
		rechnungSpalte4.setSelectedItem(inif.getStringProperty("Rechnung", "Spalte4"));
		rechnungSpalte5.setSelectedItem(inif.getStringProperty("Rechnung", "Spalte5"));
		rechnungSpalte6.setSelectedItem(inif.getStringProperty("Rechnung", "Spalte6"));
		
		bonSpalte1.setSelectedItem(inif.getStringProperty("Bon", "Spalte1"));
		bonSpalte2.setSelectedItem(inif.getStringProperty("Bon", "Spalte2"));
		bonSpalte3.setSelectedItem(inif.getStringProperty("Bon", "Spalte3"));
		bonSpalte4.setSelectedItem(inif.getStringProperty("Bon", "Spalte4"));
		bonSpalte5.setSelectedItem(inif.getStringProperty("Bon", "Spalte5"));
		bonSpalte6.setSelectedItem(inif.getStringProperty("Bon", "Spalte6"));
		
		rechnungDrucker.setSelectedItem(inif.getStringProperty("Rechnung", "Drucker"));
		bonDrucker.setSelectedItem(inif.getStringProperty("Bon", "Drucker"));
		
		rechnungSpalten.setSelectedItem(inif.getIntegerProperty("Rechnung", "Spaltenanzahl"));
		bonSpalten.setSelectedItem(inif.getIntegerProperty("Bon", "Spaltenanzahl"));
		
		rechnungVorlage.setText(inif.getStringProperty("Rechnung", "Vorlage"));
		bonVorlage.setText(inif.getStringProperty("Bon", "Vorlage"));
		
		rechnungExemplare.setText(inif.getStringProperty("Rechnung", "Exemplare"));
		
		bonSeitenlaenge.setText(inif.getStringProperty("Bon", "ProArtikelSeitenLaenge"));
		
	}
	
	private void speicherEinstellungen() {
		try{
			inif.setBooleanProperty("Bon", "SeitenLaengeAendern", bonAnpassen.isSelected(), null);
			
			inif.setBooleanProperty("Bon", "SofortDrucken", sofortDrucken.isSelected(), null);
			inif.setBooleanProperty("Rechnung", "SofortDrucken", sofortDrucken.isSelected(), null);
			
			inif.setStringProperty("Rechnung", "Spalte1", (String) rechnungSpalte1.getSelectedItem(), null);
			inif.setStringProperty("Rechnung", "Spalte2", (String) rechnungSpalte2.getSelectedItem(), null);
			inif.setStringProperty("Rechnung", "Spalte3", (String) rechnungSpalte3.getSelectedItem(), null);
			inif.setStringProperty("Rechnung", "Spalte4", (String) rechnungSpalte4.getSelectedItem(), null);
			inif.setStringProperty("Rechnung", "Spalte5", (String) rechnungSpalte5.getSelectedItem(), null);
			inif.setStringProperty("Rechnung", "Spalte6", (String) rechnungSpalte6.getSelectedItem(), null);
			
			inif.setStringProperty("Bon", "Spalte1", (String) bonSpalte1.getSelectedItem(), null);
			inif.setStringProperty("Bon", "Spalte2", (String) bonSpalte2.getSelectedItem(), null);
			inif.setStringProperty("Bon", "Spalte3", (String) bonSpalte3.getSelectedItem(), null);
			inif.setStringProperty("Bon", "Spalte4", (String) bonSpalte4.getSelectedItem(), null);
			inif.setStringProperty("Bon", "Spalte5", (String) bonSpalte5.getSelectedItem(), null);
			inif.setStringProperty("Bon", "Spalte6", (String) bonSpalte6.getSelectedItem(), null);
			
			inif.setStringProperty("Rechnung", "Drucker", (String) rechnungDrucker.getSelectedItem(), null);
			inif.setStringProperty("Bon", "Drucker", (String) bonDrucker.getSelectedItem(), null);
			
			inif.setStringProperty("Rechnung", "Spaltenanzahl", String.valueOf(rechnungSpalten.getSelectedItem()), null);
			inif.setStringProperty("Bon", "Spaltenanzahl", String.valueOf(bonSpalten.getSelectedItem()), null);
			
			inif.setStringProperty("Rechnung", "Vorlage", rechnungVorlage.getText(), null);
			inif.setStringProperty("Bon", "Voralge", bonVorlage.getText(), null);
			
			inif.setStringProperty("Rechnung", "Exemplare", rechnungExemplare.getText(), null);
			
			inif.setStringProperty("Bon", "ProArtikelSeitenLaenge", bonSeitenlaenge.getText(), null);
			
			INITool.saveIni(inif);
			JOptionPane.showMessageDialog(null,"Konfiguration erfolgreich in verkauf.ini gespeichert.");
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null,"Fehler beim speichern der Konfiguration in verkauf.ini!!!");
		}
		
	}

	private void activateListener() {
		al = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(arg0.getActionCommand().equals("speicher")) {
					speicherEinstellungen();
				} else if(arg0.getActionCommand().equals("vorlageRechnung")) {
					String sdummy = dateiWaehlen();
					rechnungVorlage.setText((sdummy.equals("") ? rechnungVorlage.getText() : sdummy) );
				} else if(arg0.getActionCommand().equals("vorlageBon")) {
					String sdummy = dateiWaehlen();
					bonVorlage.setText((sdummy.equals("") ? bonVorlage.getText() : sdummy) );
				} else 	if(arg0.getActionCommand().equals("abbrechen")){
					SystemInit.abbrechen();
					return;
				}

				
			}
			
		};
	}
}
