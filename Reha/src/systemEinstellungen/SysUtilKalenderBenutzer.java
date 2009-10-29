package systemEinstellungen;

import hauptFenster.AktiveFenster;
import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import systemTools.JRtaTextField;
import terminKalender.ParameterLaden;
import terminKalender.TerminFenster;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.text.IText;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextService;
import ag.ion.bion.officelayer.text.ITextTable;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.windows.WindowsTabbedPaneUI;



public class SysUtilKalenderBenutzer extends JXPanel implements KeyListener,ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */

	JComboBox jcomboWahl = null;
	JRtaTextField anrede = null;
	JRtaTextField vorname = null;
	JRtaTextField nachname = null;
	JRtaTextField matchcode = null;
	JRtaTextField arbstd = null;
	JComboBox abteilung = null;
	JRtaTextField deftakt = null;
	JRtaTextField kalzeile = null;
	JRtaTextField [] jtfeld = {null,null,null,null,null,null,null};
	
	String Abt = null;
	String ListLabel = null;{
	Abt = new String("        Abteilung");
	//Abt.setHorizontalAlignment(JTextField.RIGHT);
	ListLabel = "         MA-Liste";
	//ListLabel.setHorizontalAlignment(JTextField.RIGHT);
	}
	
	JButton knopf1 = null;
	JButton knopf2 = null;
	JButton knopf3 = null;
	JButton knopf4 = null;
	JButton knopf5 = null;
	JButton knopf6 = null;	
	JCheckBox naz = null;
	String[] abteil = new String[6+SystemConfig.oGruppen.gruppenNamen.size()];
	
	public ArrayList<String> kollegenDaten = new ArrayList<String>();
	private boolean lneu = false;
	private int speichernKalZeile = 0;
	
	SysUtilKalenderBenutzer(){
		//super(new GridLayout(1,1));
		super(new BorderLayout());
		//super(new FlowLayout(FlowLayout.CENTER));
		System.out.println("Aufruf SysUtilKalenderBenutzer");
		this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 0));
		/****/
	     Point2D start = new Point2D.Float(0, 0);
	     Point2D end = new Point2D.Float(400,500);
	     //Point2D end = new Point2D.Float(getParent().getParent().getWidth(),getParent().getParent().getHeight());
	     float[] dist = {0.0f, 0.5f};
	     //Color[] colors = {Colors.TaskPaneBlau.alpha(1.0f), Color.WHITE};
	     Color[] colors = {Color.WHITE,getBackground()};
	     LinearGradientPaint p =
	         new LinearGradientPaint(start, end, dist, colors);
	     MattePainter mp = new MattePainter(p);
	     setBackgroundPainter(new CompoundPainter(mp));
		/****/
		
		/*******Karteireiter erzeugen und Seite 1 in Scrollpane legen**********/
		//JTabbedPane tabbedPane = new JTabbedPane();
		//tabbedPane.setUI(new WindowsTabbedPaneUI());
			abteil[0]= " ";
			abteil[1]= "KG";
			abteil[2]= "MA";
			abteil[3]= "ER";
			abteil[4]= "LO";		
			abteil[5]= "SP";
			for(int i = 6; i < 6+SystemConfig.oGruppen.gruppenNamen.size();i++ ){
				abteil[i] = SystemConfig.oGruppen.gruppenNamen.get(i-6);
			}

	     JComponent panel1 = getForm1();
		
		JScrollPane jscroll = new JScrollPane();
		jscroll.setOpaque(false);
		jscroll.getViewport().setOpaque(false);
		jscroll.setBorder(null);
		jscroll.getVerticalScrollBar().setUnitIncrement(15);
		jscroll.setViewportView(panel1);
		

		/*
		tabbedPane.addTab("Seite 1", null, jscroll,
        "Alle Felder dieser Seite sind Pflichtfelder");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        */
        /***********ab hier Seite 2*************/
		/*
		panel1 = getForm2();
        tabbedPane.addTab("Seite 2", null, panel1,
        "Zusatzfelder für die Personalabteilung");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

        this.add(tabbedPane);
        */
        this.add(jscroll,BorderLayout.CENTER);
		//this.add(panel1);
        this.addKeyListener(this);
        
		//setVisible(true);
		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				jcomboWahl.requestFocus();
       	  	}
		});
		
		return;
	}
	/**************************************************************************/
	private JPanel getForm1(){
        //      1.            2.    3.    4.     5.     6.    7.      8.     9.
		FormLayout lay = new FormLayout("right:max(60dlu;p), 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu",
       //1.    2.  3.   4. 5.   6.  7.   8.   9.  10.  11.  12. 13. 14.  15.  16.   17.   18.  19.  20.
		"p, 10dlu, p, 2dlu,p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 10dlu, 10dlu, p,  2dlu , p");
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);	
		CellConstraints cc = new CellConstraints();


		// buttons
		knopf1 = new JButton("neu"); 
		knopf1.setPreferredSize(new Dimension(70, 20));
		knopf1.addActionListener(this);
		knopf1.setActionCommand("neu");
		knopf1.addKeyListener(this);

		knopf2 = new JButton("löschen");
		knopf2.setPreferredSize(new Dimension(70, 20));
		knopf2.addActionListener(this);
		knopf2.setActionCommand("loeschen");
		knopf2.addKeyListener(this);
		
		knopf3 = new JButton("ändern");
		knopf3.setPreferredSize(new Dimension(70, 20));
		knopf3.addActionListener(this);
		knopf3.setActionCommand("aendern");
		knopf3.addKeyListener(this);
		
		knopf4 = new JButton("speichern");
		knopf4.setPreferredSize(new Dimension(70, 20));
		knopf4.addActionListener(this);		
		knopf4.setActionCommand("speichern");
		knopf4.addKeyListener(this);		
		
		knopf5 = new JButton("abbrechen");
		knopf5.setPreferredSize(new Dimension(70, 20));
		knopf5.addActionListener(this);		
		knopf5.setActionCommand("abbrechen");
		knopf5.addKeyListener(this);		

		knopf6 = new JButton("export");
		knopf6.setPreferredSize(new Dimension(70, 20));
		knopf6.addActionListener(this);		
		knopf6.setActionCommand("liste");
		knopf6.addKeyListener(this);	
		
		// checkbox "nicht anzeigen"
		naz = new JCheckBox("");
		
		builder.addLabel("Benutzer auswählen", cc.xy(1,1));
		jcomboWahl = new JComboBox();
		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				comboFuellen();
       	  	}
		});
		jcomboWahl.addActionListener(this);
		jcomboWahl.setActionCommand("comboaktion");
		builder.add(jcomboWahl, cc.xyw(3,1,3));
		
		builder.addLabel(ListLabel, cc.xy(7,1));
		builder.add(knopf6,cc.xy(9,1));
		
		builder.addLabel("Anrede", cc.xy(1,3));
		anrede = new JRtaTextField("NORMAL",true);
		builder.add(anrede, cc.xy(3,3));
		builder.addLabel("Vorname", cc.xy(1,5));
		vorname = new JRtaTextField("NORMAL",true);
		builder.add(vorname, cc.xyw(3,5,3));
		builder.addLabel("Nachname", cc.xy(1,7));
		nachname = new JRtaTextField("NORMAL",true);
		builder.add(nachname, cc.xyw(3,7,3));
		builder.addSeparator("Kalenderstammdaten", cc.xyw(1,9,9));
		builder.addLabel("Matchcode", cc.xy(1,11));
		matchcode = new JRtaTextField("NORMAL",true);
		builder.add(matchcode, cc.xyw(3,11,3));
		builder.addLabel("Arbeitsstd.", cc.xy(1,13));
		arbstd = new JRtaTextField("FL",true,"10.2","RECHTS");
		builder.add(arbstd, cc.xyw(3,13,1));
		builder.addLabel(Abt, cc.xy(7,11));
		//String[] items = {" " ,"KG","MA","ER","LO","SP","GR"};
		//abteilung = new JComboBox(abteil);
		abteilung = new JComboBox(abteil);
		abteilung.setSelectedIndex(0);
		builder.add(abteilung, cc.xy(9, 11));
		builder.addLabel("Default-Takt", cc.xy(1,15));
		deftakt = new JRtaTextField("ZAHLEN",true);
		deftakt.setToolTipText("Dieses Feld ist für eine spätere Erweiterung gedacht und hat derzeit noch keinen Einfluß auf den Programmablauf!");
		builder.add(deftakt, cc.xyw(3, 15, 1));
		
		// builder.addLabel("Kal.-Zeile", cc.xy(7, 15));
		kalzeile = new JRtaTextField("NORMAL",true);
		//kalzeile.setEditable(false);
		//builder.add(kalzeile, cc.xyw(9, 15, 1));
		
		builder.addLabel("nicht anzeigen", cc.xy(7, 15));
		builder.add(naz, cc.xy(9,15));
		
		builder.addSeparator("", cc.xyw(1, 16, 9));
		
		
		builder.add(knopf1,cc.xy(1,18));
		builder.add(knopf2, cc.xy(3,18));
		builder.add(knopf3, cc.xy(5,18));
		builder.add(knopf4, cc.xy(7, 18));
		builder.add(knopf5,cc.xy(9,18));
			
		
		knopfGedoense(new int[]{1,0,0,0,0});
		felderEinschalten(false);
		builder.getPanel().addKeyListener(this);
		return builder.getPanel();
	
	}
	/**************************************************************************/
	private JPanel getForm2(){
		return new JPanel();
	}
	/**************************************************************************/
	private void comboFuellen(){
		int von = 0;
		int bis = ParameterLaden.vKKollegen.size();
		if(jcomboWahl.getItemCount()>0){
			jcomboWahl.removeAllItems();
		}
		for(von=0; von < bis; von++){
			jcomboWahl.addItem(ParameterLaden.getMatchcode(von));
		}	
		if(bis >=0){
			jcomboWahl.setSelectedItem("./.");
		}
		jcomboWahl.requestFocus();
	}
	/**************************************************************************/	
	private void knopfGedoense(int[] knopfstatus){
		knopf1.setEnabled((knopfstatus[0]== 0 ? false : true));
		knopf2.setEnabled((knopfstatus[1]== 0 ? false : true));
		knopf3.setEnabled((knopfstatus[2]== 0 ? false : true));
		knopf4.setEnabled((knopfstatus[3]== 0 ? false : true));		
		knopf5.setEnabled((knopfstatus[4]== 0 ? false : true));
	}
	/**************************************************************************/	
	private void comboAuswerten(){
		if(jcomboWahl.getSelectedIndex()> 0){
			holeKollege((String) jcomboWahl.getSelectedItem());
			felderFuellen(kollegenDaten);
			knopfGedoense(new int[] {1,1,1,0,0});			
		}else{
			kollegenDaten.clear();
			for(int i = 0; i <= 8;i++){
				kollegenDaten.add("");
			}
			felderFuellen(kollegenDaten);
			knopfGedoense(new int[] {1,0,0,0,0});
		}
		felderEinschalten(false);
	}
	/**************************************************************************/	
	private void felderEinschalten(boolean einschalten){
		anrede.setEnabled(einschalten);
		anrede.validate();
		vorname.setEnabled(einschalten);
		vorname.validate();
		nachname.setEnabled(einschalten);
		matchcode.setEnabled(einschalten);
		arbstd.setEnabled(einschalten);	
		abteilung.setEnabled(einschalten);
		deftakt.setEnabled(einschalten);
		naz.setEnabled(einschalten);
		
	}
	/**************************************************************************/	
	private void felderFuellen(ArrayList<String> felder){
		anrede.setText(felder.get(0));
		vorname.setText(felder.get(1));		
		nachname.setText(felder.get(2));
		matchcode.setText(felder.get(3));
		DecimalFormat df = new DecimalFormat ( "#########0.00" );
		arbstd.setText( (felder.get(4).trim().equals("") ? df.format(new Double(0.00)) : df.format(new Double(felder.get(4))) )  );	
		abteilung.setSelectedItem(felder.get(5));
		deftakt.setText(felder.get(6));
		kalzeile.setText(felder.get(7));
		naz.setSelected((felder.get(8).equals("T") ? true : false));
	}
	/**************************************************************************/
	private void neuHandeln(){
		if(ParameterLaden.vKKollegen.size() == 60){
			JOptionPane.showMessageDialog(null, "Es existieren bereits 60 Kalenderbenutzer! Derezeit ist die Benutzeranzahl auf 60 limitiert!");
			return;
		}
		lneu = true;
		knopfGedoense(new int[] {0,0,0,1,1});
		kollegenDaten.clear();
		for(int i = 0; i <= 8;i++){
			kollegenDaten.add("");
		}
		felderEinschalten(true);
		felderFuellen(kollegenDaten);
		anrede.requestFocus();
	}
	/**************************************************************************/	
	private void speichernHandeln(){
		if(matchcode.getText().trim().contains(",")){
			JOptionPane.showMessageDialog(null, "Ein Komma im Feld 'Matchcode' ist nicht erlaubt");	
			return;
		}
		if(matchcode.getText().trim().equals("")){
			JOptionPane.showMessageDialog(null, "Das Feld 'Matchcode' darf nicht leer sein");	
			return;
		}
		boolean lneueZeile = false;
		String statement = null;
		if(lneu){
			if(matchVorhanden(matchcode.getText().trim())){
				JOptionPane.showMessageDialog(null, "Dieser 'Matchcode' ist bereits vorhanden");	
				return;
			}

			lneueZeile = testObNeueKalZeile();
			if(lneueZeile){
				statement = "Insert into kollegen2 set Anrede='"+anrede.getText()+"', "+
											"Vorname='"+vorname.getText()+"', "+
											"Nachname='"+nachname.getText()+"', "+
											"Matchcode='"+matchcode.getText()+"', "+
											"Astunden='"+arbstd.getText().trim().replace(",", ".")+"', "+
											"Abteilung='"+abteilung.getSelectedItem()+"', "+											
											"Deftakt='"+(deftakt.getText().trim().equals("") ? "0" : deftakt.getText())+"', "+
											"Nicht_Zeig='"+(naz.isSelected() ? "T" : "F")+"', "+
											"Kalzeile='"+new Integer(speichernKalZeile).toString()+"'";											
			}else{
				statement = "Insert into kollegen2 set Anrede='"+anrede.getText()+"', "+
				"Vorname='"+vorname.getText()+"', "+
				"Nachname='"+nachname.getText()+"', "+
				"Matchcode='"+matchcode.getText()+"', "+
				"Astunden='"+arbstd.getText().trim().replace(",", ".")+"', "+
				"Abteilung='"+abteilung.getSelectedItem()+"', "+											
				"Deftakt='"+(deftakt.getText().trim().equals("") ? "0" : deftakt.getText())+"', "+											
				"Nicht_Zeig='"+(naz.isSelected() ? "T" : "F")+"', "+
				"Kalzeile='"+new Integer(speichernKalZeile).toString()+"'";											
			}
		}else{
			statement = "Update kollegen2 set Anrede='"+anrede.getText()+"', "+
			"Vorname='"+vorname.getText()+"', "+
			"Nachname='"+nachname.getText()+"', "+
			"Matchcode='"+matchcode.getText()+"', "+
			"Astunden='"+arbstd.getText().trim().replace(",", ".")+"', "+
			"Abteilung='"+abteilung.getSelectedItem()+"', "+											
			"Deftakt='"+(deftakt.getText().trim().equals("") ? "0" : deftakt.getText())+"', "+											
			"Nicht_Zeig='"+(naz.isSelected() ? "T" : "F")+"'"+
			"where Kalzeile='"+kalzeile.getText()+"'";											
		}
		knopfGedoense(new int[] {1,1,1,0,0});
		lneu = false;
		executeStatement(statement);
		String aktuell = matchcode.getText();
		ParameterLaden.Init();
		comboFuellen();
		jcomboWahl.setSelectedItem(aktuell);
		comboAuswerten();
		felderEinschalten(false);
		System.out.println(statement);
		JComponent termin = AktiveFenster.getFensterAlle("TerminFenster");
		if(termin != null){
			TerminFenster.thisClass.setCombosOutside();
			JOptionPane.showMessageDialog(null,"Die Kalenderbenutzer wurden geändert!\n"+
					"Die Behandlersets des aktiven Terminkalender wurden zurückgesetzt.");
		}

	}
	/**************************************************************************/	
	private void loeschenHandeln(){
		knopfGedoense(new int[] {1,1,1,0,0});
		lneu = false;
		String statement = null;
		int anfrage = JOptionPane.showConfirmDialog(null, "Wollen Sie diesen Kalenderbenutzer wirklich löschen", "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
		if(anfrage == JOptionPane.YES_OPTION){
			int aktwahl = jcomboWahl.getSelectedIndex();
			if(aktwahl> 0){
				statement = "Delete from kollegen2 where Kalzeile='"+kalzeile.getText()+"'";
				executeStatement(statement);
				ParameterLaden.Init();
				comboFuellen();
				jcomboWahl.setSelectedIndex(aktwahl-1);
				comboAuswerten();
				JComponent termin = AktiveFenster.getFensterAlle("TerminFenster");
				if(termin != null){
					TerminFenster.thisClass.setCombosOutside();
					JOptionPane.showMessageDialog(null,"Die Kalenderbenutzer wurden geändert!\n"+
							"Die Behandlersets des aktiven Terminkalender wurden zurückgesetzt.");
				}

			}
			System.out.println(statement);
		}
	}
	/**************************************************************************/	
	private void aendernHandeln(){
		felderEinschalten(true);
		knopfGedoense(new int[] {0,0,0,1,1});
		anrede.requestFocus();
		lneu = false;
	}
	/**************************************************************************/	
	private void abbrechenHandeln(){
		knopfGedoense(new int[] {1,0,1,0,0});
		lneu = false;
		for(int i = 0; i <= 7;i++){
			kollegenDaten.add("");
		}
		felderEinschalten(false);
		comboAuswerten();
		SystemUtil.abbrechen();
		SystemUtil.thisClass.parameterScroll.requestFocus();
	}
	/**************************************************************************/	
	private void listeHandeln(){
		IDocumentService documentService = null;
		try {
			documentService = Reha.officeapplication.getDocumentService();
		} catch (OfficeApplicationException e) {
			e.printStackTrace();
		}
		IDocument document = null;
		try {
			document = documentService.constructNewDocument(IDocument.WRITER, DocumentDescriptor.DEFAULT);
		} catch (NOAException e) {
			e.printStackTrace();
		}
		ITextDocument textDocument = (ITextDocument)document;
		/*
		 * Saichtext basteln und einsetzen
		 */
		ITextTable textTable = null;
		try {
			textTable = textDocument.getTextTableService().constructTextTable(ParameterLaden.vKKollegen.size()+1, 3);
		} catch (TextException e) {
			e.printStackTrace();
		}				
		try {
			textDocument.getTextService().getTextContentService().insertTextContent(textTable);
		} catch (TextException e) {
			e.printStackTrace();
		}
		try {
			textTable.getCell(0,0).getTextService().getText().setText("Rang im Kalender");
			textTable.getCell(1,0).getTextService().getText().setText("MatchCode");
			textTable.getCell(2,0).getTextService().getText().setText("Zeile im Kalender");				  
		} 
		  catch (TextException exception) {
		  	exception.printStackTrace();
		}

		for(int i = 0; i < ParameterLaden.vKKollegen.size();i++){
			  try {
				  textTable.getCell(0,i+1).getTextService().getText().setText(new Integer(i).toString());
				  textTable.getCell(1,i+1).getTextService().getText().setText(ParameterLaden.getMatchcode(i));
				  textTable.getCell(2,i+1).getTextService().getText().setText(new Integer(ParameterLaden.getDBZeile(i)).toString());				  
				} 
			  catch (TextException exception) {
			  	exception.printStackTrace();
				}
		}
	}	
	/**************************************************************************/
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getActionCommand().equals("comboaktion")){comboAuswerten();}
		if(arg0.getActionCommand().equals("neu")){neuHandeln();}
		if(arg0.getActionCommand().equals("aendern")){aendernHandeln();}
		if(arg0.getActionCommand().equals("abbrechen")){abbrechenHandeln();}
		if(arg0.getActionCommand().equals("speichern")){speichernHandeln();}		
		if(arg0.getActionCommand().equals("loeschen")){loeschenHandeln();}		
		if(arg0.getActionCommand().equals("liste")){
			new Thread(){
				public void run(){
					listeHandeln();
				}
			}.start();
		}		
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
	private boolean testObNeueKalZeile(){
		boolean ret = false;
		if( (ParameterLaden.vKKollegen.size() >= (ParameterLaden.maxKalZeile+1)) ){
			//Es muß eine neue Kalenderzeile belegt werden.
			speichernKalZeile = ParameterLaden.maxKalZeile+1;
			ret = true;
			return ret;
			
		}else{
			//Es muß nach einer freien also unbelegten Kalenderzeile gesucht werden.
			testeKollegen();
			ret = false;
		}
		//System.out.println("vkkollgen.size = "+ParameterLaden.vKKollegen.size());
		//System.out.println("maxKalZeile = "+ParameterLaden.maxKalZeile);		
		return ret;
	}
	/***********************************************************/
	private void holeKollege(String match){
		//Reha obj = Reha.thisClass;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
	                ResultSet.CONCUR_UPDATABLE );
			rs = stmt.executeQuery("SELECT * FROM kollegen2 where Matchcode='"+match+"'");
			kollegenDaten.clear();

			String test = null;
			while( rs.next()){
				test = rs.getString("Anrede");
				kollegenDaten.add(new String((test != null ?  test : "" )) );
				test = rs.getString("Vorname");
				kollegenDaten.add(new String((test != null ?  test : "" )) );
				test = rs.getString("Nachname");
				kollegenDaten.add(new String((test != null ?  test : "" )) );
				test = rs.getString("Matchcode");
				kollegenDaten.add(new String((test != null ?  test : "" )) );
				test = rs.getString("Astunden");
				kollegenDaten.add(new String((test != null ?  test : "" )) );
				test = rs.getString("Abteilung");
				kollegenDaten.add(new String((test != null ?  test : "" )) );
				test = rs.getString("Deftakt");
				kollegenDaten.add(new String((test != null ?  test : "" )) );				
				test = rs.getString("Kalzeile");
				kollegenDaten.add(new String((test != null ?  test : "" )) );				
				test = rs.getString("Nicht_zeig");
				kollegenDaten.add(new String((test != null ?  test : "F" )) );
				System.out.println(test);
			}

		}catch(SQLException ex){
			System.out.println("Kollegen2="+ex);
		}
		finally {
			if (rs != null) {
				try {
					rs.close();
				}catch (SQLException sqlEx) { // ignorieren }
					rs = null;
				}
			}	
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { // ignorieren }
					stmt = null;
				}
			}
			
		}
	}
/***********************************************************/
	private int testeKollegen(){
		Statement stmt = null;
		ResultSet rs = null;
		int itest = 0;
		try {
			stmt = Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
	                ResultSet.CONCUR_UPDATABLE );

			rs = stmt.executeQuery("SELECT KALZEILE FROM kollegen2 ORDER BY KALZEILE");
			int durchlauf = 0;

			while( rs.next()){
				if(durchlauf == 0){
					itest = rs.getInt("KALZEILE");
					if(itest > 1){
						speichernKalZeile = 1;
						break;
					}
				}else{
					if (rs.getInt("KALZEILE") > (itest+1)){
						speichernKalZeile = itest+1;
						break;
					}else{
						itest = rs.getInt("KALZEILE");
					}
				}
				durchlauf++;
			}

		}catch(SQLException ex){
			System.out.println("Kollegen2="+ex);
		}
		finally {
			if (rs != null) {
				try {
					rs.close();
				}catch (SQLException sqlEx) { // ignorieren }
					rs = null;
				}
			}	
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { // ignorieren }
					stmt = null;
				}
			}
			
		}
		return itest;
	}
	/**************************************************************/
	private void executeStatement(String match){
		Statement stmt = null;
		try {
			stmt = Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
	                ResultSet.CONCUR_UPDATABLE );
			stmt.execute(match);

		}catch(SQLException ex){
			System.out.println("Kollegen2="+ex);
		}
		finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { // ignorieren }
					stmt = null;
				}
			}
		}
	}
	/***********************************************************/	
	private boolean matchVorhanden(String match) {
	Statement stmt = null;
	ResultSet rs = null;
	boolean ret = true;
	try {
		stmt = Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE );
		try{
			int anz = 0;
			rs = stmt.executeQuery("select count(*) from kollegen2 where matchcode='"+match+"'");
			if (rs.next()){
				anz = rs.getInt(1);
			}
			rs.close();
			if(anz == 0){
				ret = false;
			}
			}catch(SQLException ex){
				System.out.println("Kollegen2="+ex);
			}
	}catch(SQLException ex){
			System.out.println("Kollegen2="+ex);
	}
	finally {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException sqlEx) { // ignore }
				rs = null;
			}
		}	
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException sqlEx) { // ignore }
				stmt = null;
			}
		}
	
	}
		return ret;
	}	
	/***********************************************************/			

}