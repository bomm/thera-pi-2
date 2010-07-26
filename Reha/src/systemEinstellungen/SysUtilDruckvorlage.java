package systemEinstellungen;

import hauptFenster.Reha;
import hilfsFenster.EmailText;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;


import systemTools.JRtaTextField;
import terminKalender.ParameterLaden;
import terminKalender.TerminFenster;

import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.text.IBookmark;
import ag.ion.bion.officelayer.text.IBookmarkService;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.ITextRange;
import ag.ion.bion.officelayer.text.ITextService;
import ag.ion.bion.officelayer.text.ITextTable;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;
import ag.ion.noa.printing.IPrinter;
import ag.ion.noa.search.ISearchResult;
import ag.ion.noa.search.SearchDescriptor;
import ag.ion.noa.text.TextRangeSelection;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints.Alignment;



public class SysUtilDruckvorlage extends JXPanel implements KeyListener, ActionListener, FocusListener {
	
	JButton knopf0 = null;
	JButton knopf1 = null;

	JButton knopf2 = null;
	JButton knopf3 = null;
	JButton knopf4 = null;
	JButton knopf5 = null;
	
	JCheckBox patname = null;
	JCheckBox spaltenkopf = null;


	JComboBox spalte1 = null;
	JComboBox spalte2 = null;
	JComboBox spalte3 = null;
	JComboBox spalte4 = null;
	JComboBox[] spa = {null,null,null,null};
	JComboBox druckername = null;
	JRtaTextField vorlagenname = null;
	JRtaTextField tabanz = null;
	JRtaTextField zeilanz = null;
	JRtaTextField spaltanz = null;
	PrintService[] services = null;	
	String[] drucker = null;
	//neues Zuigs
	JRadioButton ddruck = null;
	JRadioButton odruck = null;
	ButtonGroup jrbg = new ButtonGroup();
	
	
	
	public static SysUtilDruckvorlage thisClass = null;

	public SysUtilDruckvorlage(){
		super(new GridLayout(1,1));
		////System.out.println("Aufruf SysUtilDruckvorlage");
		this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 0));
		/****/
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("SystemInit"));
		/****/
	 	/****/
		services = PrintServiceLookup.lookupPrintServices(null, null);
		drucker = new String[services.length];
		
		for (int i = 0 ; i < services.length; i++){
			drucker[i] = services[i].getName();
		}
		
	     JScrollPane jscr = new JScrollPane();
	     jscr.setBorder(null);
	     jscr.setOpaque(false);
	     jscr.getViewport().setOpaque(false);
	     jscr.getVerticalScrollBar().setUnitIncrement(15);
	     jscr.setViewportView(getVorlagenSeite());
	     jscr.validate();
	     add(jscr);
	     thisClass = this;
		return;
	}
	/************** Beginn der Methode f�r die Objekterstellung und -platzierung *********/
	private JPanel getVorlagenSeite(){
        
		//knopf1 = new JButton("w�hlen");
		//knopf1.setPreferredSize(new Dimension(70, 20));
		//knopf1.addActionListener(this);
		//knopf1.setActionCommand("printer");
		//knopf1.addKeyListener(this);
		knopf0 = new JButton("abbrechen");
		knopf0.setPreferredSize(new Dimension(70, 20));
		knopf0.addActionListener(this);
		knopf0.setActionCommand("abbruch");
		knopf0.addKeyListener(this);
		knopf1 = new JButton("speichern");
		knopf1.setPreferredSize(new Dimension(70, 20));
		knopf1.addActionListener(this);
		knopf1.setActionCommand("speichern");
		knopf1.addKeyListener(this);
		
		
		knopf2 = new JButton("wählen");
		knopf2.setPreferredSize(new Dimension(70, 20));
		knopf2.addActionListener(this);
		knopf2.setActionCommand("file");
		knopf2.addKeyListener(this);
		knopf3 = new JButton("bearbeiten");
		knopf3.setPreferredSize(new Dimension(70, 20));
		knopf3.addActionListener(this);
		knopf3.setActionCommand("filetext");
		knopf3.addKeyListener(this);
		knopf4 = new JButton("bearbeiten");
		knopf4.setPreferredSize(new Dimension(70, 20));
		knopf4.addActionListener(this);
		knopf4.setActionCommand("mailtext");
		knopf4.addKeyListener(this);
		knopf5 = new JButton("testen");
		knopf5.setPreferredSize(new Dimension(70, 20));
		knopf5.addActionListener(this);
		knopf5.setActionCommand("testen");
		knopf5.addKeyListener(this);
		
		patname = new JCheckBox();
		patname.setSelected( (SystemConfig.oTerminListe.PatNameDrucken==0 ? false : true ));

		
		spaltenkopf = new JCheckBox();
		spaltenkopf.setSelected( (SystemConfig.oTerminListe.MitUeberschrift==0 ? false : true ));
		
		druckername = new JComboBox(drucker);
		if(SystemConfig.oTerminListe.NameTerminDrucker.trim().equals("")){
			druckername.setSelectedIndex(0);
		}else{
			druckername.setSelectedItem(SystemConfig.oTerminListe.NameTerminDrucker.trim());
		}
		vorlagenname = new JRtaTextField("", true);
		vorlagenname.setEnabled(false);
		vorlagenname.setText(SystemConfig.oTerminListe.NameTemplate);
		//NameTemplate = C:\RehaVerwaltung\vorlagen\terminliste5.odt
		tabanz = new JRtaTextField("ZAHLEN", true);
		tabanz.setText(new Integer( SystemConfig.oTerminListe.AnzahlTerminTabellen).toString() );
		zeilanz = new JRtaTextField("ZAHLEN", true);
		zeilanz.setText(new Integer( SystemConfig.oTerminListe.AnzahlTermineProTabelle).toString() );
		spaltanz = new JRtaTextField("ZAHLEN", true);
		spaltanz.setText(new Integer( SystemConfig.oTerminListe.AnzahlSpaltenProTabellen).toString() );
		spaltanz.setName("spaltenanzahl");
		spaltanz.addFocusListener(this);
		ddruck = new JRadioButton();
		jrbg.add(ddruck);
		odruck = new JRadioButton();
		jrbg.add(odruck);
		if(SystemConfig.oTerminListe.DirektDruck){
			ddruck.setSelected(true);
		}else{
			odruck.setSelected(true);			
		}
		
		//                                      1.            2.    3.            4.     5.     6.    7.      8.     9.
		FormLayout lay = new FormLayout("max(80dlu;p), 20dlu, 80dlu, 4dlu, 40dlu", //, 4dlu, 40dlu, 4dlu, 40dlu",
       //1.    2.  3.   4.   5.   6.   7.   8.  9.  10.  11. 12.  13.  14.  15. 16.  17. 18.  19.   20. 21.  22.  23.  24  25  26    27  28   29   30   31  32  33   34   35  36   37  38   39  40    41    42  43  44 45   46   47
		"p, 10dlu, p, 10dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p,  2dlu , p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p");

		
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		
		builder.addLabel("Drucker auswählen", cc.xy(1,1));
		builder.add(druckername, cc.xy(3,1));
		builder.add(druckername, cc.xyw(3,1,3));
		
		builder.addLabel("Papierformat/-einzug in der Vorlage einstellen!", cc.xyw(1, 3, 3));
		
		builder.addLabel("Terminliste direkt drucken", cc.xy(1, 5));
		builder.add(ddruck, cc.xy(5, 5, CellConstraints.RIGHT, CellConstraints.BOTTOM));
		builder.addLabel("Terminliste in OpenOffice öffnen", cc.xy(1, 7));
		builder.add(odruck, cc.xy(5, 7, CellConstraints.RIGHT, CellConstraints.BOTTOM));
		
		builder.addSeparator("", cc.xyw(1, 9, 5));
		
		builder.addLabel("Vorlage wählen", cc.xy(1,11));
		builder.add(vorlagenname, cc.xy(3,11));
		builder.add(knopf2, cc.xy(5, 11));
		builder.addLabel("Vorlage bearbeiten", cc.xy(1, 13));
		builder.add(knopf3, cc.xy(5, 13));
		builder.addLabel("E-Mail-Anschreiben bearbeiten", cc.xyw(1, 15, 2));
		builder.add(knopf4, cc.xy(5, 15));
		builder.addLabel("Patientennamen drucken", cc.xyw(1,17,2));
		builder.add(patname,cc.xy(5, 17, CellConstraints.RIGHT, CellConstraints.BOTTOM));
		
		builder.addSeparator("", cc.xyw(1, 19, 5));
		
		builder.addLabel("Die Angaben müssen mit der Struktur der Vorlage übereinstimmen!", cc.xyw(1, 21, 5));
		builder.addLabel("", cc.xy(1,23));
		builder.addLabel("Tabellen je Seite", cc.xy(1,25));
		builder.add(tabanz, cc.xyw(5, 25, 1));
		builder.addLabel("Terminzeilen je Tabelle", cc.xy(1,27));
		builder.add(zeilanz, cc.xyw(5, 27, 1));
		builder.addLabel("Spalten je Tabelle (max. 4)", cc.xy(1, 29));
		builder.add(spaltanz, cc.xyw(5,29, 1));
		
		String[] spalten = {"WoTag", "Datum", "Uhrzeit", "Behandler"};
		builder.addLabel("Inhalt von Spalte 1", cc.xy(1,31));
		spalte1 = new JComboBox(spalten);
		spalte1.setSelectedIndex(0);
		spa[0] = spalte1;
		builder.add(spalte1, cc.xy(5, 31));
		builder.addLabel("Inhalt von Spalte 2", cc.xy(1,33));
		spalte2 = new JComboBox(spalten);
		spalte2.setSelectedIndex(1);
		spa[1] = spalte2;		
		builder.add(spalte2, cc.xy(5, 33));
		builder.addLabel("Inhalt von Spalte 3", cc.xy(1,35));
		spalte3 = new JComboBox(spalten);
		spalte3.setSelectedIndex(2);
		spa[2] = spalte3;		
		builder.add(spalte3, cc.xy(5, 35));
		builder.addLabel("Inhalt von Spalte 4", cc.xy(1,37));
		spalte4 = new JComboBox(spalten);
		spalte4.setSelectedIndex(3);
		spa[3] = spalte4;		
		builder.add(spalte4, cc.xy(5, 37));
		
		int anz = new Integer(spaltanz.getText().trim());
		for(int i = 0 ; i< 4;i++){
			if(i < anz){
				spa[i].setEnabled(true);
			}else{
				spa[i].setEnabled(false);
			}
		}
		builder.addLabel("Die Vorlage enthält Spaltenüberschriften", cc.xyw(1, 39, 3));
		builder.add(spaltenkopf, cc.xy(5, 39, CellConstraints.RIGHT, CellConstraints.BOTTOM));
		
		builder.addSeparator("", cc.xyw(1, 41, 5));
		builder.addLabel("Termindruck testen", cc.xy(1,43));
		builder.add(knopf5, cc.xy(5, 43));
		
		builder.addLabel("Änderungen verwerfen", cc.xyw(1, 45, 3));
		builder.add(knopf0, cc.xy(5, 45));
		builder.addLabel("Änderungen übernehmen", cc.xyw(1, 47, 3));
		builder.add(knopf1, cc.xy(5,47));
		
		
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
		// TODO Auto-generated method stub
		if(e.getActionCommand().equals("file")){
			//DateiDialog zur Auswahl der Vorlage;
	        thisClass.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			SwingUtilities.invokeLater(new Runnable(){
		        public  void run()
	           	   {
		        	new Thread(){
		        		public void run(){
					        dateiDialog();		        			
		        		}
		        	}.start();

	           	   }
			}); 
		}
		if(e.getActionCommand().equals("filetext")){
			//Mit OpenOffice Vorlage �ffnen
			thisClass.setCursor(new Cursor(Cursor.WAIT_CURSOR));
	        //Reha.thisFrame.getGlassPane().setCursor(Cursor.WAIT_CURSOR);
			SwingUtilities.invokeLater(new Runnable(){
		        public  void run()
	           	   {
		        	new Thread(){
		        		public void run(){
				        	vorlageBearbeiten();
		        		}
		        	}.start();
	           	   }
			}); 
		}
		if(e.getActionCommand().equals("mailtext")){
			//Eingabefenster f�r Emailtext
			thisClass.setCursor(new Cursor(Cursor.WAIT_CURSOR));
	        //Reha.thisFrame.getGlassPane().setCursor(Cursor.WAIT_CURSOR);
			SwingUtilities.invokeLater(new Runnable(){
		        public  void run()
	           	   {
		        	new Thread(){
		        		public void run(){
		        			EmailText et = new EmailText();
		        			
		        		}
		        	}.start();
	           	   }
			}); 

		}
		if(e.getActionCommand().equals("testen")){
			//TestDruck mit OpenOffice
			thisClass.setCursor(new Cursor(Cursor.WAIT_CURSOR));
	        //Reha.thisFrame.getGlassPane().setCursor(Cursor.WAIT_CURSOR);
			SwingUtilities.invokeLater(new Runnable(){
		        public  void run()
	           	   {
		        	new Thread(){
		        		public void run(){
		        			testDruck();
		        		}
		        	}.start();
	           	   }
			}); 

		}
		if(e.getActionCommand().equals("speichern")){
			//TestDruck mit OpenOffice
			thisClass.setCursor(new Cursor(Cursor.WAIT_CURSOR));
	        //Reha.thisFrame.getGlassPane().setCursor(Cursor.WAIT_CURSOR);
			SwingUtilities.invokeLater(new Runnable(){
		        public  void run()
	           	   {
		        	new Thread(){
		        		public void run(){
		        			datenSpeichern();
		        		}
		        	}.start();
	           	   }
			}); 

		}
		if(e.getActionCommand().equals("abbruch")){
			SystemInit.abbrechen();
			//SystemUtil.thisClass.parameterScroll.requestFocus();
		}


	}
	private void datenSpeichern(){
		
		int test1 = new Integer(tabanz.getText().trim());
		if(test1==0){
			JOptionPane.showMessageDialog(null,"Sie wollen eine Definition abspeichern mit Anzahl Tabelle = 0,\n\n"+
					"Mit diesem IQ sind Sie ein Fall für THEORG! Diese Definition wird jedenfalls\n"+
					"nicht abgespeichert!");
			return;
		}
		int test2 = new Integer(spaltanz.getText().trim());		
		if(test2==0){
			JOptionPane.showMessageDialog(null,"Sie wollen eine Definition abspeichern mit Anzahl Spalten = 0,\n\n"+
					"Mit diesem IQ sind Sie ein Fall für THEORG! Diese Definition wird jedenfalls\n"+
					"nicht abgespeichert!");
			return;
		}
		int test3 = new Integer(zeilanz.getText().trim());
		if(test3==0){
			JOptionPane.showMessageDialog(null,"Sie wollen eine Definition abspeichern mit Terminzeile pro Tabelle  = 0,\n\n"+
					"Mit diesem IQ sind Sie ein Fall für THEORG! Diese Definition wird jedenfalls\n"+
					"nicht abgespeichert!");
			return;
		}
		
		INIFile ini = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/terminliste.ini");
		ini.setStringProperty("TerminListe1", "AnzahlTabellen", new Integer( test1).toString(), null);
		SystemConfig.oTerminListe.AnzahlTerminTabellen = test1;
		
		ini.setStringProperty("TerminListe1", "AnzahlSpaltenProTabellen", new Integer( test2).toString(), null);
		SystemConfig.oTerminListe.AnzahlSpaltenProTabellen = test2;
		
		ini.setStringProperty("TerminListe1", "AnzahlTermineProTabelle", new Integer( test3).toString(), null);
		SystemConfig.oTerminListe.AnzahlTermineProTabelle = test3;
		
		ini.setStringProperty("TerminListe1", "NameTemplate", vorlagenname.getText().trim(), null);
		SystemConfig.oTerminListe.NameTemplate = vorlagenname.getText().trim();
		
		ini.setStringProperty("TerminListe1", "NameTerminDrucker", ((String)druckername.getSelectedItem()).trim(), null);
		SystemConfig.oTerminListe.NameTerminDrucker = ((String)druckername.getSelectedItem()).trim();

		ini.setStringProperty("TerminListe1", "PatNameDrucken", (patname.isSelected() ? "1" : "0"), null);
		SystemConfig.oTerminListe.PatNameDrucken = (patname.isSelected() ? 1 : 0);

		ini.setStringProperty("TerminListe1", "MitSpaltenUeberschrift", (spaltenkopf.isSelected() ? "1" : "0"), null);
		SystemConfig.oTerminListe.MitUeberschrift = (spaltenkopf.isSelected() ? 1 : 0);


		String[] spalten = {"Wochentag","Datum","Uhrzeit","Behandler"};
		SystemConfig.oTerminListe.NamenSpalten.clear();
		for(int i = 0; i < 4;i++){
			if(spa[i].isEnabled()){
				ini.setStringProperty("TerminListe1", "InhaltSpalte"+(i+1),spalten[ spa[i].getSelectedIndex()],null);
				SystemConfig.oTerminListe.NamenSpalten.add(spalten[ spa[i].getSelectedIndex()]);
			}else{
				ini.setStringProperty("TerminListe1", "InhaltSpalte"+(i+1),"",null);				
			}
		}
		if(ddruck.isSelected()){
			ini.setStringProperty("TerminListe1", "DirektDruck", "1", null);
			SystemConfig.oTerminListe.DirektDruck = true;
		}else{
			ini.setStringProperty("TerminListe1", "DirektDruck", "0", null);
			SystemConfig.oTerminListe.DirektDruck = false;			
		}
		ini.save();
		thisClass.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	
	private void speichernKonfig(){
		/*
		AnzahlTerminTabellen = new Integer(RWJedeIni.leseIniDatei(iniName, "TerminListe1", "AnzahlTabellen"));
		AnzahlSpaltenProTabellen = new Integer(RWJedeIni.leseIniDatei(iniName, "TerminListe1", "AnzahlSpaltenProTabellen"));
		for(int i = 0;i<AnzahlSpaltenProTabellen;i++){
			NamenSpalten.add(RWJedeIni.leseIniDatei(iniName, "TerminListe1", "InhaltSpalte"+(i+1)) );
		}
		AnzahlTermineProTabelle = new Integer(RWJedeIni.leseIniDatei(iniName, "TerminListe1", "AnzahlTermineProTabelle"));
		NameTemplate = RWJedeIni.leseIniDatei(iniName, "TerminListe1", "NameTemplate");
		NameTerminDrucker = RWJedeIni.leseIniDatei(iniName, "TerminListe1", "NameTerminDrucker");
		PatNameDrucken = new Integer(RWJedeIni.leseIniDatei(iniName, "TerminListe1", "PatNameDrucken"));
		PatNamenPlatzhalter = RWJedeIni.leseIniDatei(iniName, "TerminListe1", "PatNamePlatzhalter");
		MitUeberschrift = new Integer(RWJedeIni.leseIniDatei(iniName, "TerminListe1", "MitSpaltenUeberschrift"));
		*/
		
	}
	private void dateiDialog(){

		final JFileChooser chooser = new JFileChooser("Verzeichnis wählen");
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        final File file = new File(Reha.proghome+"/vorlagen/"+Reha.aktIK);

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
        thisClass.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        final int result = chooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File inputVerzFile = chooser.getSelectedFile();
            //String inputVerzStr = inputVerzFile.getPath();
            
            ////System.out.println("Eingabepfad:" + inputVerzStr);
            if(inputVerzFile.getName().trim().equals("")){
            	vorlagenname.setText(SystemConfig.oTerminListe.NameTemplate);
            }else{
            	vorlagenname.setText(inputVerzFile.getName().trim());	
            }
        }else{
        	vorlagenname.setText(SystemConfig.oTerminListe.NameTemplate);
        }
        chooser.setVisible(false); 			
	}
	/*************************************/
	private void vorlageBearbeiten(){
		IDocumentService documentService = null;
		String url = Reha.proghome+"vorlagen/"+Reha.aktIK+"/"+vorlagenname.getText().trim();
		//String url = urlx.replaceAll("/", "\\\\");
		////System.out.println("Url = -------------->"+url);
		try {
			documentService = Reha.officeapplication.getDocumentService();
		} catch (OfficeApplicationException e) {
			e.printStackTrace();
		}
		IDocument document = null;
		try {
			document = documentService.loadDocument(url,DocumentDescriptor.DEFAULT);
			//document = documentService.constructNewDocument(IDocument.WRITER, DocumentDescriptor.DEFAULT);
		} catch (NOAException e) {
			e.printStackTrace();
		}
		ITextDocument textDocument = (ITextDocument)document;
		/*
		 * Saichtext basteln und einsetzen
		 */
		ITextTable textTable = null;		
        thisClass.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		if(((JComponent)arg0.getSource()).getName() != null){
			if(((JComponent)arg0.getSource()).getName().equals("spaltenanzahl")){
				int anzahl;
				if(spaltanz.getText().trim().equals("")){
					anzahl = 0;
				}else{
				   anzahl = new Integer(spaltanz.getText().trim());	
				}
				if(anzahl == 0){
					String text = "Also das ist ja zwar alles Ihre Sache...\n\n"+
					"ein Geburtstagsbrief ohne eine Spalte mag ja gerade noch so durchgehen,\n"+
					"aber eine Terminliste....also ich weiß nicht so recht";
					JOptionPane.showMessageDialog(null, text);
				}
				if(anzahl > 4){
					anzahl = 4;
					spaltanz.setText("4");
				}
 
				for(int i = 0; i< 4;i++){
					if(i < anzahl){
						spa[i].setEnabled(true);						
					}else{
						spa[i].setEnabled(false);						
					}
				}
			}
		}
		
	}	
	private void testDruck(){
		String url = Reha.proghome+"vorlagen/"+Reha.aktIK+"/"+vorlagenname.getText().trim(); 
		////System.out.println("***************URL = "+url+"****************");
		String terminDrucker = (String) druckername.getSelectedItem();
		Vector<TermObjekt> termindat = new Vector<TermObjekt>();
		termindat.add(new TermObjekt("Mo-01.12.2008","14:25","Fräulein Smilla...","2008-12-0114:00"));
		termindat.add(new TermObjekt("Mi-03.12.2008","09.30","Herrlein Hugole...","2008-12-0314:00"));
		termindat.add(new TermObjekt("Fr-05.12.2008","17.07","Fräulein Smilla...","2008-12-05117:07"));		
		int anzahl = termindat.size();
/*
		JRtaTextField tabanz = null;
		JRtaTextField zeilanz = null;
		JRtaTextField spaltanz = null;
*/
		int AnzahlTabellen = new Integer(tabanz.getText().trim());
		if(AnzahlTabellen == 0){
			JOptionPane.showMessageDialog(null, "Ohne Termintabelle kein Terminplan und deshalb auch kein Test!");
			thisClass.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			return;
		}
		int maxTermineProTabelle = new Integer(zeilanz.getText().trim());
		if(maxTermineProTabelle == 0){
			JOptionPane.showMessageDialog(null, "Mit der Angabe Termine pro Tabelle == 0 -> kein Terminplan und deshalb auch kein Test!");
			thisClass.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			return;			
		}
		int maxTermineProSeite = AnzahlTabellen * maxTermineProTabelle;
		int spaltenProtabelle = new Integer(spaltanz.getText().trim());
		if(spaltenProtabelle==0){
			thisClass.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			JOptionPane.showMessageDialog(null, "Ohne Spalten keine Tabelle, ohne Tabelle kein Terminplan -> und deshalb auch kein Test!");
			return;			
		}
		String[] spalten = {"Wochentag","Datum","Uhrzeit","Behandler"};
		Vector<String> spaltenNamen = new Vector<String>(); 
		for(int i = 0; i < 4;i++){
			if(spa[i].isEnabled()){
				spaltenNamen.add((String)spalten[ spa[i].getSelectedIndex()]);
			}
		}

		int ipatdrucken = (patname.isSelected() ? 1 : 0);
		int iheader = (spaltenkopf.isSelected() ? 1 : 0);
		String patplatzhalter = SystemConfig.oTerminListe.PatNamenPlatzhalter;
		String[] tabName;
		//int anzahl = oOTermine.size();
		String patname = "Hr.Kracher,Karl";
		IDocumentService documentService = null;;
		
		try {
			documentService = Reha.officeapplication.getDocumentService();
		} catch (OfficeApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        IDocumentDescriptor docdescript = new DocumentDescriptor();
        if(SysUtilDruckvorlage.thisClass.ddruck.isSelected()){
            docdescript.setHidden(true);
        }else{
        	docdescript.setHidden(false);
        }	
        docdescript.setAsTemplate(true);
		IDocument document = null;
		ITextTable[] tbl = null;

		try {
			document = documentService.loadDocument(url,docdescript);

		} catch (NOAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/**********************/
		ITextDocument textDocument = (ITextDocument)document;
		tbl = textDocument.getTextTableService().getTextTables();

		if(tbl.length != AnzahlTabellen){
			JOptionPane.showMessageDialog (null, "Anzahl Tabellen stimmt nicht mit Ihren Angaben überein.\nDer Test wird abgebrochen!");
			textDocument.close();
			thisClass.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			return;
		}
		tabName = new String[AnzahlTabellen];
		int x = 0;
		for(int i=AnzahlTabellen;i>0;i--){
			tabName[x] = tbl[(tbl.length-1)-x].getName(); 
			////System.out.println(tabName[x]);
			x++;
		}
		/*********************/

		//Aktuellen Drucker ermitteln
		String druckerName = null;
		try {
			druckerName = textDocument.getPrintService().getActivePrinter().getName();
		} catch (NOAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Wenn nicht gleich wie in der INI angegeben -> Drucker wechseln
		IPrinter iprint = null;
		if(! druckerName.equals(terminDrucker)){
			try {
				iprint = (IPrinter) textDocument.getPrintService().createPrinter(terminDrucker);
			} catch (NOAException e) {
				JOptionPane.showMessageDialog(null, "Druckvorlage und aktuelle Definition passen nicht zusammen");
				// TODO Auto-generated catch block
				e.printStackTrace();
				thisClass.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			try {
				textDocument.getPrintService().setActivePrinter(iprint);
			} catch (NOAException e) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(null, "Druckvorlage und aktuelle Definition passen nicht zusammen");				
				e.printStackTrace();
				thisClass.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));				
			}
		}
		//Jetzt den Platzhalter ^Name^ suchen
		SearchDescriptor searchDescriptor = null;
		ISearchResult searchResult = null;
		if(ipatdrucken  > 0){
/************************************************/
			/*
			searchDescriptor = new SearchDescriptor(patplatzhalter);
			//SearchDescriptor searchDescriptor = new SearchDescriptor("^Name^");
			searchDescriptor.setIsCaseSensitive(true);
			//Suche durchf�hren


			searchResult = textDocument.getSearchService().findFirst(searchDescriptor);
			if(!searchResult.isEmpty()) {
				//Ergebnis seletieren
				ITextRange[] textRanges = searchResult.getTextRanges();
		        try {
					textDocument.setSelection(new TextRangeSelection(textRanges[0]));
				} catch (NOAException e) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, "Druckvorlage und aktuelle Definition passen nicht zusammen");
					e.printStackTrace();
					thisClass.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
		        //Selektion durch eigenen Text ersetzen
		        textRanges[0].setText(patname);
			}else{
				thisClass.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				JOptionPane.showMessageDialog (null, "Suche nach ^Name^ war erfolglos");				
			}
			*/
			/*
			ITextService textService = textDocument.getTextService();
		      IBookmarkService bookmarkService = textService.getBookmarkService();
		      IBookmark bookmark = bookmarkService.getBookmark("^Name^");
		      if(bookmark != null){
		      String name = bookmark.getName();
		      bookmark.setText(patname);
		      try {
					textDocument.getTextFieldService().refresh();
				} catch (TextException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		      }
		      */
		      ITextFieldService textFieldService = textDocument.getTextFieldService();
		      ITextField[] placeholders = null;
				try {
					placeholders = textFieldService.getPlaceholderFields();
				} catch (TextException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for (int i = 0; i < placeholders.length; i++) {
					String placeholderDisplayText = placeholders[i].getDisplayText();
					////System.out.println("Platzhalter-Name = "+placeholderDisplayText);
					if(placeholderDisplayText.equals("<^Name^>")){
						placeholders[i].getTextRange().setText(patname);
					}	
				}
		      
/************************************************/			
		}
		/********************************************/
		int zeile = 0;
		int startTabelle = 0;
		int aktTabelle = 0;
		int aktTermin = -1;
		int aktTerminInTabelle = -1;
		String druckDatum = "";
		ITextTable textTable = null;
		try {
			//textTable = textDocument.getTextTableService().getTextTable(SystemConfig.oTerminListe.NameTabelle[0]);				
			textTable = textDocument.getTextTableService().getTextTable(tabName[aktTabelle]);
		} catch (TextException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "Druckvorlage und aktuelle Definition passen nicht zusammen");
			thisClass.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			e.printStackTrace();
		}

		while(true){
			aktTerminInTabelle = aktTerminInTabelle+1;
			aktTermin = aktTermin+1;
			
			if(aktTermin >= anzahl){
				break;
			}
			
			/***********Wenn die Spalte voll ist und die aktuelle Tabelle nicht die letzte ist*/
			if(aktTerminInTabelle >= maxTermineProTabelle && aktTabelle < AnzahlTabellen-1  ){
				aktTabelle = aktTabelle+1;
				try {
					textTable = textDocument.getTextTableService().getTextTable(tabName[aktTabelle]);
				} catch (TextException e) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, "Druckvorlage und aktuelle Definition passen nicht zusammen");
					e.printStackTrace();
					thisClass.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
				aktTerminInTabelle = 0;
				////System.out.println("Spaltenwechsel nach Spalte"+aktTabelle);
			}

			/************Wenn die aktuelle Seite voll ist******************/
			if(aktTermin >= maxTermineProSeite && aktTerminInTabelle==maxTermineProTabelle){

				textDocument.getViewCursorService().getViewCursor().getPageCursor().jumpToEndOfPage();
				try {
					textDocument.getViewCursorService().getViewCursor().getTextCursorFromEnd().insertPageBreak();
					textDocument.getViewCursorService().getViewCursor().getTextCursorFromEnd().insertDocument(url) ;
				} catch (NOAException e) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, "Druckvorlage und aktuelle Definition passen nicht zusammen");
					e.printStackTrace();
					thisClass.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
				tbl = textDocument.getTextTableService().getTextTables();
				x = 0;
				for(int i=AnzahlTabellen;i>0;i--){
					tabName[x] = tbl[(tbl.length-1)-x].getName(); 
					////System.out.println(tabName[x]);
					x++;
				}
				
				if(ipatdrucken  > 0){
					/*
					searchResult = textDocument.getSearchService().findFirst(searchDescriptor);
					if(!searchResult.isEmpty()) {
						ITextRange[] textRanges = searchResult.getTextRanges();
				        try {
							textDocument.setSelection(new TextRangeSelection(textRanges[0]));
						} catch (NOAException e) {
							// TODO Auto-generated catch block
							JOptionPane.showMessageDialog(null, "Druckvorlage und aktuelle Definition passen nicht zusammen");
							e.printStackTrace();
							thisClass.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						}
				        textRanges[0].setText(patname);
					}
					*/
					////System.out.println("Suche ersetze durchgef�hrt*************");
				      ITextFieldService textFieldService = textDocument.getTextFieldService();
				      ITextField[] placeholders = null;
						try {
							placeholders = textFieldService.getPlaceholderFields();
						} catch (TextException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						for (int i = 0; i < placeholders.length; i++) {
							String placeholderDisplayText = placeholders[i].getDisplayText();
							////System.out.println("Platzhalter-Name = "+placeholderDisplayText);
							if(placeholderDisplayText.equals("<^Name^>")){
								placeholders[i].getTextRange().setText(patname);
							}	
						}
					
				}

				aktTabelle = 0;
				aktTerminInTabelle = 0;

				try {
					textTable = textDocument.getTextTableService().getTextTable(tabName[aktTabelle]);
				} catch (TextException e) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, "Druckvorlage und aktuelle Definition passen nicht zusammen");
					e.printStackTrace();
					thisClass.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
				////System.out.println("textTable gesetzt*************");
				////System.out.println("Druck wird fortgesetzt bei Termin Nr.:"+aktTermin);
			}
			/********************/
			/*public String tag;
			public String beginn;
			public String termtext;
			public String sorter;
			*/
			//**************/Hier die Zellen*************//
			if(spaltenNamen.contains("Wochentag")){
				int zelle = spaltenNamen.indexOf("Wochentag");
				
				druckDatum = termindat.get(aktTermin).tag;
				if(aktTerminInTabelle > 0){
					if(! druckDatum.equals(termindat.get(aktTermin-1).tag)){
						try {
							textTable.getCell(zelle,aktTerminInTabelle+iheader).getTextService().getText().setText(druckDatum.substring(0,2) );
						} catch (TextException e) {
							// TODO Auto-generated catch block
							JOptionPane.showMessageDialog(null, "Druckvorlage und aktuelle Definition passen nicht zusammen");
							e.printStackTrace();
							thisClass.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						}					
					}
				}else{
					try {
						textTable.getCell(zelle,aktTerminInTabelle+iheader).getTextService().getText().setText(druckDatum.substring(0,2) );
					} catch (TextException e) {
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(null, "Druckvorlage und aktuelle Definition passen nicht zusammen");
						e.printStackTrace();
						thisClass.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					}
				}
			}

		
			try {
				if(spaltenNamen.indexOf("Datum") > 0){
					int zelle = spaltenNamen.indexOf("Datum");
					textTable.getCell(zelle,aktTerminInTabelle+iheader).getTextService().getText().setText(druckDatum.substring(3) );
				}
				if(spaltenNamen.indexOf("Uhrzeit") > 0){
					int zelle = spaltenNamen.indexOf("Uhrzeit");
					textTable.getCell(zelle,aktTerminInTabelle+iheader).getTextService().getText().setText(termindat.get(aktTermin).beginn);
				}
				if(spaltenNamen.indexOf("Behandler") > 0){
					int zelle = spaltenNamen.indexOf("Behandler");						
					textTable.getCell(zelle,aktTerminInTabelle+iheader).getTextService().getText().setText(termindat.get(aktTermin).termtext);
				}

			} catch (TextException e) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(null, "Druckvorlage und aktuelle Definition passen nicht zusammen");
				e.printStackTrace();
				thisClass.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			
			/********************/
		}
        if(SysUtilDruckvorlage.thisClass.ddruck.isSelected()){
        	try {
				textDocument.print();
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			textDocument.close();
        }else{
        	document.getFrame().getXFrame().getContainerWindow().setVisible(true);
        }	
        


		thisClass.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
/********************************************/
	class TermObjekt implements Comparable<TermObjekt>{
		public String tag;
		public String beginn;
		public String termtext;
		public String sorter;
		
		public TermObjekt(String xtag,String xbeginn,String xtermtext,String xsorter){
			this.tag = xtag;
			this.beginn = xbeginn;
			this.termtext = xtermtext;
			this.sorter =  xsorter;
			
		}

		@Override
		public int compareTo(TermObjekt o) {
		      //First order by name
		      int result = sorter.compareTo(o.sorter);
		      if (0 == result) {
		        //if names are equal order by age, youngest first
		        result = termtext.compareTo(o.termtext);
		      }
		      return result;
		  }

		
	}	
/********************************************/
}
