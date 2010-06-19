package rehaStatistik;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;



import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;

import Tools.ButtonTools;
import Tools.DatFunk;
import Tools.OOTools;
import Tools.SqlInfo;
import Tools.SystemPreislisten;

import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.spreadsheet.ISpreadsheetDocument;
import ag.ion.noa.NOAException;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.XSheetCellCursor;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XSpreadsheets;
import com.sun.star.uno.UnoRuntime;

public class StatistikPanel extends JXPanel implements ListSelectionListener, ActionListener  {
	
	JXPanel content = null;
	JLabel status1 = null;
	JLabel status2 = null;
	JButton[] buts = {null,null,null,null};
	
	Vector<Vector<String>> anwesend = new Vector<Vector<String>>();
	Vector<Vector<String>> angemeldet = new Vector<Vector<String>>();
	
	Vector<Vector<String>> unklar = new Vector<Vector<String>>();
	Vector<Vector<String>> berichtfehlt = new Vector<Vector<String>>();
	
	int anwesendlvamed = 0;
	int anwesendlvaahb = 0;
	int anwesendbfamed = 0;
	int anwesendbfaahb = 0;
	int anwesendgkvmed = 0;
	int anwesendgkvahb = 0;
	int anwesendbahnseemed = 0;
	int anwesendbahnseeahb = 0;

	int angemeldetlvamed = 0;
	int angemeldetlvaahb = 0;
	int angemeldetbfamed = 0;
	int angemeldetbfaahb = 0;
	int angemeldetgkvmed = 0;
	int angemeldetgkvahb = 0;
	int angemeldetbahnseemed = 0;
	int angemeldetbahnseeahb = 0;

	int calcrow = 0;	
	ISpreadsheetDocument spreadsheetDocument = null;;
	IDocument document  = null;
	XSheetCellCursor cellCursor = null;
	String sheetName = "Tabelle1";
	
	int dlgRet = -1;
	String von = "";
	String bis = "";	
	Vector<String> rehaArt = new Vector<String>();
	Vector<Integer> rehaFaelle = new Vector<Integer>();
	Vector<Integer> rehaTage = new Vector<Integer>();
	Vector<Vector<String>> unklareFaelle = new Vector<Vector<String>>();
	String[] ktraegerArt = {"BFA","LVA","KBS","AOK","IKK","BKK","LKK","BKN","DAK","PRI","BGE","ORTHO"};
	
	public StatistikPanel(){
		super();
		setPreferredSize(new Dimension(500,500));
		add(getContent(),BorderLayout.CENTER);
		validate();
	}

	
	private JXPanel getContent(){
		content = new JXPanel();
		content.setPreferredSize(new Dimension(500,500));
		//                                1         2           3      4            5       6
		FormLayout lay = new FormLayout("0dlu,fill:0:grow(0.5),10dlu,60dlu,fill:0:grow(0.5),0dlu",
				//1           2        3   4    5    6  7   8   9 10   11       12         13 
				"0dlu,fill:0:grow(0.25),p,15dlu,p,15dlu,p,15dlu,p,15dlu,p,fill:0:grow(0.5),0dlu");
		CellConstraints cc = new CellConstraints();
		content.setLayout(lay);
		
		JLabel lab = new JLabel("LVA/BfA Wochenstatistik");
		content.add(lab,cc.xy(2,3,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		content.add( (buts[0]=ButtonTools.macheButton("starten", "wochestarten", this)),cc.xy(4,3));
		
		lab = new JLabel("LVA Quartalsstatistik");
		content.add(lab,cc.xy(2,5,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		content.add( (buts[1]=ButtonTools.macheButton("starten", "quartalstarten", this)),cc.xy(4,5));
		
		lab = new JLabel("GKV Jahresstatistik");
		content.add(lab,cc.xy(2,7,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		content.add( (buts[2]=ButtonTools.macheButton("starten", "gkvjahr", this)),cc.xy(4,7));

		lab = new JLabel("BGE EAP-Jahresstatistik");
		content.add(lab,cc.xy(2,9,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		content.add( (buts[3]=ButtonTools.macheButton("starten", "bgejahr", this)),cc.xy(4,9));
		
		status1 = new JLabel(" ");
		status1.setForeground(Color.BLUE);
		content.add(status1,cc.xyw(2,11,4,CellConstraints.CENTER,CellConstraints.DEFAULT));
		for(int i = 0;i < 4; i++){
			buts[i].setEnabled(false);
		}
		
		content.validate();
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				enableButtons();
				return null;
			}

		}.execute();
		return content;
		
	}
	public void enableButtons(){
		long zeit = System.currentTimeMillis();
		while(! RehaStatistik.DbOk){
			try {
				Thread.sleep(50);
				if( (System.currentTimeMillis()-zeit) > 3000 ){
					break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(!RehaStatistik.DbOk){
			RehaStatistik.stoppeDB();
			System.exit(0);
		}else{
			for(int i = 0;i < 4; i++){
				buts[i].setEnabled(true);
			}
		}
	}
	@Override
	public void valueChanged(ListSelectionEvent arg0) {
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String cmd = arg0.getActionCommand();
		if(cmd.equals("wochestarten")){
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					doWochenStatistik();
					return null;
				}
			}.execute();
		}
		if(cmd.equals("quartalstarten")){
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					doQuartalStatistik();
					return null;
				}
			}.execute();
		}
		if(cmd.equals("gkvjahr")){
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					doJahresStatistik();
					return null;
				}
			}.execute();
		}
	}
	/************************/
	private void doJahresStatistik(){
		dlgRet = -1;
		Point pt = buts[1].getLocationOnScreen();
		von = "";
		bis = "";
		ZeitraumFenster zf = new ZeitraumFenster(this);
		zf.setPreferredSize(new Dimension(250,150));
		zf.setLocation(pt.x-50, pt.y+30);
		zf.setModal(true);
		zf.pack();
		zf.setVisible(true);
		zf = null;
		//System.out.println("DialogRückgabe="+dlgRet+"\nvon = "+von+"\nbis = "+bis);
		if(dlgRet >= 0){
			holeJahr();
		}
	}
	private void holeJahr(){
			//                     0       1         2        3       4      5
			String cmd = "select rez_nr,pat_intern,anzahl1,art_dbeh1,kid,preisgruppe from lza where rez_datum >='"+
			von+"' and rez_datum <= '"+bis+"' and rez_nr like 'RH%'";
			Vector<Vector<String>> vec = SqlInfo.holeFelder(cmd);
			System.out.println(vec.size());
			int kuerzelID = -1;
			int unbekannte = 0;
			rehaArt.clear();
			rehaFaelle.clear();
			rehaTage.clear();
			unklareFaelle.clear();
			Vector<String> dummy = new Vector<String>();
			List<String> rehaKuerzel = Arrays.asList(ktraegerArt);
			for(int i = 0; i < rehaKuerzel.size();i++){
				rehaFaelle.add(0);
				rehaTage.add(0);
			}

			for(int i = 0; i < vec.size();i++){
				kuerzelID = holeKuerzelID(vec.get(i).get(3),vec.get(i).get(5),vec.get(i).get(4),rehaKuerzel);
				if(kuerzelID < 0){
					unbekannte++;
					dummy.clear();
					dummy.add(vec.get(i).get(0));
					dummy.add(vec.get(i).get(1));
					dummy.add(vec.get(i).get(2));
					unklareFaelle.add((Vector<String>)dummy.clone());
				}else{
					rehaFaelle.set(kuerzelID,rehaFaelle.get(kuerzelID)+1 );
					rehaTage.set(kuerzelID, rehaTage.get(kuerzelID)+Integer.parseInt(vec.get(i).get(2)));
				}
			}
			if(unbekannte > 0){
				JOptionPane.showMessageDialog(null, "Unklare Rehafälle wurden entdeckt: "+Integer.toString(unbekannte)+" Fälle");
			}
			starteCalcJahr(rehaKuerzel);

		
	}
	//	String[] ktraegerArt = {"BFA","LVA","KNP","AOK","IKK","BKK","LKK","DAK","PRI","BGE"};
	private int holeKuerzelID(String behandlung,String preisgruppe,String kassenID,List<String> kuerzel){
		int ret = -1;
		
		Vector<Vector<String>> vec = SystemPreislisten.hmPreise.get("Reha").get(Integer.parseInt(preisgruppe)-1);
		String kurz = "";
		Vector<Vector<String>> knamen = null;
		for(int i = 0; i < vec.size();i++){
			if(vec.get(i).get(9).equals(behandlung)){
				kurz = vec.get(i).get(1).toUpperCase();
				if(kurz.contains("BFA")){
					return kuerzel.indexOf("BFA");
				}else if(kurz.contains("LVA")){
					return kuerzel.indexOf("LVA");
				}else if(kurz.contains("KBS")){
					return kuerzel.indexOf("KBS");
				}else if(kurz.contains("GKV") && preisgruppe.equals("2")){
					return kuerzel.indexOf("DAK");
				}else if(preisgruppe.equals("3") && (kurz.contains("GKV") || kurz.contains("PRI"))   ){
					return kuerzel.indexOf("PRI");
				}else if(preisgruppe.equals("3") && kurz.contains("ORTH") ){
					return kuerzel.indexOf("ORTHO");
				}else if(kurz.contains("GKV") && preisgruppe.equals("1")){
					knamen = SqlInfo.holeFelder("select kassen_nam1,kassen_nam2 from kass_adr where id='"+kassenID+"' LIMIT 1");
					if(beinhaltetFragment(knamen,"innung","ikk")){
						return kuerzel.indexOf("IKK");
					}else if(beinhaltetFragment(knamen,"aok","orts")){
						return kuerzel.indexOf("AOK");
					}else if(beinhaltetFragment(knamen,"bkk","betriebs")){
						return kuerzel.indexOf("BKK");
					}else if(beinhaltetFragment(knamen,"lkk","landw")){
						return kuerzel.indexOf("LKK");
					}else if(beinhaltetFragment(knamen,"knappsch","bundes")){
						return kuerzel.indexOf("BKN");
					}
				}else if(preisgruppe.equals("4")){
					return kuerzel.indexOf("BGE");
				}
			}
		}
		
		
		return ret;
	}
	private boolean beinhaltetFragment(Vector<Vector<String>> knamen,String krit1,String krit2){
		boolean ret = false;
		for(int i = 0; i < knamen.size();i++){
			if(knamen.get(i).get(0).toUpperCase().contains(krit1.toUpperCase()) || 
					knamen.get(i).get(1).toUpperCase().contains(krit2.toUpperCase())	){
				return true;
			}
		}
		return ret;
	}
	private void starteCalcJahr(List<String> ktraeger){
		try {
	
			starteCalc();
			OOTools.doColWidth(spreadsheetDocument,sheetName,0,0,10000);
			OOTools.doColWidth(spreadsheetDocument,sheetName,1,2,3000);
			OOTools.doCellValue(cellCursor, 0, 0, (String) "GKV - Jahresstatistik");
			OOTools.doCellFontBold(cellCursor, 0, 0);
			OOTools.doCellValue(cellCursor, 0, 1, (String) "Zeitraum: "+DatFunk.sDatInDeutsch(von)+
					" bis "+DatFunk.sDatInDeutsch(bis));
			OOTools.doCellFontBold(cellCursor, 0, 1);
			for(int i = 0; i <ktraeger.size();i++){
				OOTools.doCellValue(cellCursor, 0, i+3, (String) ktraeger.get(i));
				OOTools.doCellValue(cellCursor, 1, i+3, (Double) Double.parseDouble(Integer.toString(rehaFaelle.get(i))));
				OOTools.doCellValue(cellCursor, 2, i+3, (Double) Double.parseDouble(Integer.toString(rehaTage.get(i))));
			}
			if(unklareFaelle.size() > 0){
				int zeile = ktraeger.size()+5;
				OOTools.doCellValue(cellCursor, 0, zeile, (String) "unklare Rehafälle");
				zeile++;
				for(int i = 0; i < unklareFaelle.size();i++){
					OOTools.doCellValue(cellCursor, 0, zeile, (String) unklareFaelle.get(i).get(0)+" - "+
							unklareFaelle.get(i).get(1));
					OOTools.doCellValue(cellCursor, 2, zeile, (Double) Double.parseDouble(unklareFaelle.get(i).get(2)));
					zeile++;
				}
				
			}
			zeigeCalc();
		} catch (NoSuchElementException e) {
			e.printStackTrace();
		} catch (WrappedTargetException e) {
			e.printStackTrace();
		} catch (UnknownPropertyException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (OfficeApplicationException e) {
			e.printStackTrace();
		} catch (NOAException e) {
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}
	
	/**********
	 * 
	 * 
	 * LVA-Quartalsstatistik
	 * 
	 */
	private void doQuartalStatistik(){
		dlgRet = -1;
		Point pt = buts[1].getLocationOnScreen();
		von = "";
		bis = "";
		ZeitraumFenster zf = new ZeitraumFenster(this);
		zf.setPreferredSize(new Dimension(250,150));
		zf.setLocation(pt.x-50, pt.y+30);
		zf.setModal(true);
		zf.pack();
		zf.setVisible(true);
		zf = null;
		//System.out.println("DialogRückgabe="+dlgRet+"\nvon = "+von+"\nbis = "+bis);
		if(dlgRet >= 0){
			holeQuartal();
		}
	}
	private void holeQuartal(){
		//                     0       1         2        3       4      5
		String cmd = "select rez_nr,pat_intern,anzahl1,art_dbeh1,kid,preisgruppe from lza where rez_datum >='"+
		von+"' and rez_datum <= '"+bis+"' and rez_nr like 'RH%'";
		Vector<Vector<String>> vec = SqlInfo.holeFelder(cmd);
		System.out.println(vec.size());
		String kuerzel = "";
		rehaArt.clear();
		rehaFaelle.clear();
		rehaTage.clear();

		int position = -1;
		for(int i = 0; i < vec.size();i++){
			kuerzel = holeKuerzel(vec.get(i).get(3),vec.get(i).get(5));
			if( (position = rehaArt.indexOf(kuerzel)) < 0){
				rehaArt.add(String.valueOf(kuerzel));
				rehaFaelle.add(1);
				rehaTage.add(Integer.parseInt(vec.get(i).get(2)));
			}else{
				rehaFaelle.set(position, rehaFaelle.get(position)+1);
				rehaTage.set(position, rehaTage.get(position)+Integer.parseInt(vec.get(i).get(2)));
			}
		}
		if(rehaArt.size() > 0){
			starteCalcQuartal();
		}
	}
	private String holeKuerzel(String behandlung,String preisgruppe){
		String ret = "";
		Vector<Vector<String>> vec = SystemPreislisten.hmPreise.get("Reha").get(Integer.parseInt(preisgruppe)-1);
		for(int i = 0; i < vec.size();i++){
			if(vec.get(i).get(9).equals(behandlung)){
				if(preisgruppe.equals("3")){
					return "PRI-"+vec.get(i).get(1).toUpperCase();
				}else if(preisgruppe.equals("4")){
					return "BGE-"+vec.get(i).get(1).toUpperCase();
				}else{
					return vec.get(i).get(1).toUpperCase();	
				}
				
			}
		}
		return ret;
	}
	private void starteCalcQuartal(){
		try {
			/*
			rehaArt.clear();
			rehaFaelle.clear();
			rehaTage.clear();
			rehaPreisgruppe.clear();
			*/
			starteCalc();
			OOTools.doColWidth(spreadsheetDocument,sheetName,0,0,10000);
			OOTools.doColWidth(spreadsheetDocument,sheetName,1,2,3000);
			OOTools.doCellValue(cellCursor, 0, 0, (String) "LVA-Quartalsstatistik");
			OOTools.doCellFontBold(cellCursor, 0, 0);
			OOTools.doCellValue(cellCursor, 0, 1, (String) "Zeitraum: "+DatFunk.sDatInDeutsch(von)+
					" bis "+DatFunk.sDatInDeutsch(bis));
			OOTools.doCellFontBold(cellCursor, 0, 1);
			for(int i = 0; i < rehaArt.size();i++){
				OOTools.doCellValue(cellCursor, 0, i+3, (String) rehaArt.get(i));
				OOTools.doCellValue(cellCursor, 1, i+3, (Double) Double.parseDouble(Integer.toString(rehaFaelle.get(i))));
				OOTools.doCellValue(cellCursor, 2, i+3, (Double) Double.parseDouble(Integer.toString(rehaTage.get(i))));
			}
			zeigeCalc();
		} catch (NoSuchElementException e) {
			e.printStackTrace();
		} catch (WrappedTargetException e) {
			e.printStackTrace();
		} catch (UnknownPropertyException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (OfficeApplicationException e) {
			e.printStackTrace();
		} catch (NOAException e) {
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}
	/***********
	 * 
	 * 
	 * 
	 * LVA / BfA Wochenstatistik
	 * 
	 * 
	 * *************/
	private void doWochenStatistik(){

		unklar.clear();
		berichtfehlt.clear();

		anwesendlvamed = 0;
		anwesendlvaahb = 0;
		anwesendbfamed = 0;
		anwesendbfaahb = 0;
		anwesendgkvmed = 0;
		anwesendgkvahb = 0;
		anwesendbahnseemed = 0;
		anwesendbahnseeahb = 0;
		
		angemeldetlvamed = 0;
		angemeldetlvaahb = 0;
		angemeldetbfamed = 0;
		angemeldetbfaahb = 0;
		angemeldetgkvmed = 0;
		angemeldetgkvahb = 0;
		angemeldetbahnseemed = 0;
		angemeldetbahnseeahb = 0;

		status1.setText("Datensätze abholen");
		String cmd = "select rez_nr,pat_intern,rez_datum,termine,anzahl1,art_dbeh1,kid,preisgruppe from verordn "+
		"where ( (rez_nr like 'RH%') and (art_dbeh1='24' or art_dbeh1='25' or art_dbeh1='26' "+
		"or art_dbeh1='27' or art_dbeh1='28' or art_dbeh1='29') )";
		Vector<Vector<String>> rehavec = SqlInfo.holeFelder(cmd);
		int lang = rehavec.size();
		//System.out.println("Es wurden insgesamt "+Integer.toString(lang)+" Reha-Verordnungen gefunden");
		int tagedifferenz = 0;
		int isttage = 0;
		int solltage = 0;
		int rehaart = 0;
		status1.setText("Datensätze auswerten");
		for(int i = 0; i < lang; i++){
			
			try{
				isttage = tageTest(rehavec.get(i).get(3));				
			}catch(Exception ex){
				isttage = -1;
			}
			try{
				solltage = Integer.parseInt(rehavec.get(i).get(4));				
			}catch(Exception ex){
				solltage = -1;
			}
			try{
				tagedifferenz = doUnklartest(rehavec.get(i).get(2));				
			}catch(Exception ex){
				tagedifferenz = -1;
			}
			try{
				rehaart = Integer.parseInt(rehavec.get(i).get(5));				
			}catch(Exception ex){
				rehaart = -1;
			}
			//System.out.println("Isttage:"+isttage+" / Solltage:"+solltage+" / Tage Differenz:"+tagedifferenz+" / Rehaart:"+rehaart);
			if(isttage >= solltage){
				berichtfehlt.add( (Vector<String>) rehavec.get(i).clone() ); 
				continue;
			}else if( (isttage >= 1) && (isttage < solltage) ){
				//System.out.println("in anwesend");
				doAnwesend(rehaart);
				anwesend.add( (Vector<String>) rehavec.get(i).clone() );
				continue;
			}else if( (tagedifferenz <= 14) && (isttage <= 0) ){
				//System.out.println("in angemeldet");
				doAngemeldet(rehaart);
				angemeldet.add( (Vector<String>) rehavec.get(i).clone() );
				continue;
			}else if( (tagedifferenz > 14) && (isttage <= 0) ){
				unklar.add( (Vector<String>) rehavec.get(i).clone() );
				continue;
			}
			/*
			System.out.println(SqlInfo.holePatFeld("n_name", "pat_intern='"+rehavec.get(i).get(1)+"'"));
			System.out.println(SqlInfo.holePatFeld("v_name", "pat_intern='"+rehavec.get(i).get(1)+"'"));
			System.out.println("Bereits absolvierte Reha-Tage = "+isttage+" Tage");
			*/
		}
		starteCalcLvaWoche();
		status1.setText("starte Tabellenkalkulation");
	}
	private void doAnwesend(int art){
		switch(art){
		case 24:
			anwesendlvamed++;
			break;
		case 25:
			anwesendlvaahb++;
			break;
		case 26:
			anwesendbfamed++;
			break;
		case 27:
			anwesendbfaahb++;
			break;
		case 28:
			anwesendgkvahb++;
			break;
		case 29:
			anwesendgkvmed++;
			break;
		case 30:
			anwesendbahnseemed++;
			break;
		case 31:
			anwesendbahnseeahb++;
			break;
		}
	}
	private void doAngemeldet(int art){
		switch(art){
		case 24:
			angemeldetlvamed++;
			break;
		case 25:
			angemeldetlvaahb++;
			break;
		case 26:
			angemeldetbfamed++;
			break;
		case 27:
			angemeldetbfaahb++;
			break;
		case 28:
			angemeldetgkvahb++;
			break;
		case 29:
			angemeldetgkvmed++;
			break;
		case 30:
			angemeldetbahnseemed++;
			break;
		case 31:
			angemeldetbahnseeahb++;
			break;
		}
	}
	private String getRehaArt(String art){
		int iart = Integer.parseInt(art);
		switch(iart){
		case 24:
			return  "DRV-BaWü (med)";
		case 25:
			return  "DRV-BaWü (AHB)";
		case 26:
			return  "DRV-Bund (med)";
		case 27:
			return  "DRV-Bund (AHB)";		
		case 28:
			return  "GKV (med)";
		case 29:
			return  "GKV (AHB)";
		case 30:
			return  "DRV-Knappsch.(med)";
		case 31:
			return  "DRV-Knappsch.(AHB)";
		}
		return "unbekannte Rehaart";
	}

	private int doUnklartest(String sqlrezdatum){
		long differenz = DatFunk.TageDifferenz(DatFunk.sDatInDeutsch(sqlrezdatum), DatFunk.sHeute());
		//System.out.println(Long.toString(differenz)+" Tage differenz");
		return Integer.parseInt(Long.toString(differenz));		

	}
	private int tageTest(String termine){
		String[] tage = termine.split("\n");
		//System.out.println(termine);
		if(tage[tage.length-1].split("@")[0].length() != 10){
			return 0;
		}
		return tage.length;
	}
	private boolean rehaFertig(String anzahltage, String termine){
		int itage = Integer.parseInt(anzahltage);
		int iabsolviert = tageTest(termine);
		if(iabsolviert >= itage){
			return true;
		}
		return false;
	}
	private String letzterRehaTag(String termine){
		String[] tage = termine.split("\n");
		String[] einzelwerte = tage[tage.length-1].split("@"); 
		return einzelwerte[0];
	}
	private String ersterRehaTag(String termine){
		String[] tage = null;
		String[] einzelwerte = null;
		try{
		tage = termine.split("\n");
		einzelwerte = tage[0].split("@");
		}catch(Exception ex){
			//System.out.println("Wert erster Tag = "+tage[0]);
			return "----------";
		}
		return einzelwerte[0];
	}

	/************************************************/
	private void starteCalc() throws OfficeApplicationException, NOAException, NoSuchElementException, WrappedTargetException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException{
		if(!RehaStatistik.officeapplication.isActive()){
			RehaStatistik.starteOfficeApplication();
		}
		IDocumentService documentService = RehaStatistik.officeapplication.getDocumentService();
        IDocumentDescriptor docdescript = new DocumentDescriptor();
       	docdescript.setHidden(true);
        docdescript.setAsTemplate(true);
		document = documentService.constructNewDocument(IDocument.CALC, docdescript);
		spreadsheetDocument = (ISpreadsheetDocument) document;
		OOTools.setzePapierFormatCalc((ISpreadsheetDocument) spreadsheetDocument, 21000, 29700);
		OOTools.setzeRaenderCalc((ISpreadsheetDocument) spreadsheetDocument, 1000,1000, 1000, 1000);
		XSpreadsheets spreadsheets = spreadsheetDocument.getSpreadsheetDocument().getSheets();
		String sheetName= "Tabelle1";
		XSpreadsheet spreadsheet1 = (XSpreadsheet)UnoRuntime.queryInterface(XSpreadsheet.class,spreadsheets.getByName(sheetName));
		cellCursor = spreadsheet1.createCursor();
	}
	/************************************************/
	
	private void starteCalcLvaWoche(){
		try {
			starteCalc();
			
			OOTools.doColWidth(spreadsheetDocument,sheetName,0,0,10000);
			OOTools.doColWidth(spreadsheetDocument,sheetName,1,2,3000);

			
			OOTools.doColTextAlign(spreadsheetDocument, sheetName, 1, 2, 2);
			
			OOTools.doCellFontSize(cellCursor, 0, 0, Float.valueOf("12."));
			OOTools.doCellFontBold(cellCursor, 0, 0);
			OOTools.doCellValue(cellCursor, 0, 0, (String) "Reha-Belegung vom "+DatFunk.sHeute());

			OOTools.doCellFontBold(cellCursor, 0, 2);
			OOTools.doCellValue(cellCursor, 0, 2, (String) "Kostenträger");
			OOTools.doCellFontBold(cellCursor, 1, 2);
			OOTools.doCellValue(cellCursor, 1, 2, (String) "anwesend");
			OOTools.doCellFontBold(cellCursor, 2, 2);
			OOTools.doCellValue(cellCursor, 2, 2, (String) "angemeldet");
			
			OOTools.doCellValue(cellCursor, 0, 3, (String) "DRV-BaWü (med)");
			OOTools.doCellFontName(cellCursor, 0, 3, "Courier New");
			OOTools.doCellValue(cellCursor, 0, 4, (String) "DRV-BaWü (AHB)");
			OOTools.doCellFontName(cellCursor, 0, 4, "Courier New");
			OOTools.doCellValue(cellCursor, 0, 5, (String) "DRV-Bund (med)");
			OOTools.doCellFontName(cellCursor, 0, 5, "Courier New");
			OOTools.doCellValue(cellCursor, 0, 6, (String) "DRV-Bund (AHB)");
			OOTools.doCellFontName(cellCursor, 0, 6, "Courier New");
			OOTools.doCellValue(cellCursor, 0, 7, (String) "DRV-Knappsch.(med)");
			OOTools.doCellFontName(cellCursor, 0, 7, "Courier New");
			OOTools.doCellValue(cellCursor, 0, 8, (String) "DRV-Knappsch.(AHB)");
			OOTools.doCellFontName(cellCursor, 0, 8, "Courier New");
			OOTools.doCellValue(cellCursor, 0, 9, (String) "GKV (med)");
			OOTools.doCellFontName(cellCursor, 0, 9, "Courier New");
			OOTools.doCellValue(cellCursor, 0, 10, (String) "GKV (AHB)");
			OOTools.doCellFontName(cellCursor, 0, 10, "Courier New");
			OOTools.doCellValue(cellCursor, 0, 11, (String) "Summen");
			OOTools.doCellFontName(cellCursor, 0, 11, "Courier New");
			
			OOTools.doCellValue(cellCursor, 1, 3, (Double) Double.valueOf(Integer.toString(anwesendlvamed))  );
			OOTools.doCellFontName(cellCursor, 1, 3, "Courier New");
			OOTools.doCellValue(cellCursor, 1, 4, (Double) Double.valueOf(Integer.toString(anwesendlvaahb)) );
			OOTools.doCellFontName(cellCursor, 1, 4, "Courier New");
			OOTools.doCellValue(cellCursor, 1, 5, (Double) Double.valueOf(Integer.toString(anwesendbfamed)) );
			OOTools.doCellFontName(cellCursor, 1, 5, "Courier New");
			OOTools.doCellValue(cellCursor, 1, 6, (Double) Double.valueOf(Integer.toString(anwesendbfaahb)) );
			OOTools.doCellFontName(cellCursor, 1, 6, "Courier New");
			OOTools.doCellValue(cellCursor, 1, 7, (Double) Double.valueOf(Integer.toString(anwesendbahnseemed)) );
			OOTools.doCellFontName(cellCursor, 1, 7, "Courier New");
			OOTools.doCellValue(cellCursor, 1, 8, (Double) Double.valueOf(Integer.toString(anwesendbahnseeahb)) );
			OOTools.doCellFontName(cellCursor, 1, 8, "Courier New");
			OOTools.doCellValue(cellCursor, 1, 9, (Double) Double.valueOf(Integer.toString(anwesendgkvmed)) );
			OOTools.doCellFontName(cellCursor, 1, 9, "Courier New");
			OOTools.doCellValue(cellCursor, 1, 10, (Double) Double.valueOf(Integer.toString(anwesendgkvahb)) );
			OOTools.doCellFontName(cellCursor, 1, 10, "Courier New");
			
			OOTools.doCellValue(cellCursor, 2, 3, (Double) Double.valueOf(Integer.toString(angemeldetlvamed))  );
			OOTools.doCellFontName(cellCursor, 2, 3, "Courier New");
			OOTools.doCellValue(cellCursor, 2, 4, (Double) Double.valueOf(Integer.toString(angemeldetlvaahb)) );
			OOTools.doCellFontName(cellCursor, 2, 4, "Courier New");
			OOTools.doCellValue(cellCursor, 2, 5, (Double) Double.valueOf(Integer.toString(angemeldetbfamed)) );
			OOTools.doCellFontName(cellCursor, 2, 5, "Courier New");
			OOTools.doCellValue(cellCursor, 2, 6, (Double) Double.valueOf(Integer.toString(angemeldetbfaahb)) );
			OOTools.doCellFontName(cellCursor, 2, 6, "Courier New");
			OOTools.doCellValue(cellCursor, 2, 7, (Double) Double.valueOf(Integer.toString(angemeldetbahnseemed)) );
			OOTools.doCellFontName(cellCursor, 2, 7, "Courier New");
			OOTools.doCellValue(cellCursor, 2, 8, (Double) Double.valueOf(Integer.toString(angemeldetbahnseeahb)) );
			OOTools.doCellFontName(cellCursor, 2, 8, "Courier New");
			OOTools.doCellValue(cellCursor, 2, 9, (Double) Double.valueOf(Integer.toString(angemeldetgkvmed)) );
			OOTools.doCellFontName(cellCursor, 2, 9, "Courier New");
			OOTools.doCellValue(cellCursor, 2, 10, (Double) Double.valueOf(Integer.toString(angemeldetgkvahb)) );
			OOTools.doCellFontName(cellCursor, 2, 10, "Courier New");

			OOTools.doCellFontName(cellCursor, 1, 11, "Courier New");
			OOTools.doCellFormula(cellCursor, 1, 11, (String) "=sum(B4:B11)");

			OOTools.doCellFontName(cellCursor, 2, 11, "Courier New");
			OOTools.doCellFormula(cellCursor, 2, 11, (String) "=sum(C4:C11)");
			
			
			OOTools.doCellValue(cellCursor, 0, 13, (String) "*********************ausstehende Entlassberichte ("+berichtfehlt.size()+" Fälle) ********************" );
			OOTools.doCellFontName(cellCursor, 0, 13, "Courier");
			calcrow = 14;
			String name = "";
			String letzterRehatag = "";
			String rehaart = "";
			String angelegtam = "";
			String ersterRehatag = "";
			status1.setText("ermittle fehlende Entlassberichte");
			for(int i = 0; i < berichtfehlt.size();i++){
				status1.setText("ermittle fehlende Entlassberichte: "+Integer.toString(i+1)+" von "+berichtfehlt.size());
				name = SqlInfo.holePatFeld("n_name","pat_intern='"+berichtfehlt.get(i).get(1)+"'")+", "+SqlInfo.holePatFeld("v_name","pat_intern='"+berichtfehlt.get(i).get(1)+"'");
				letzterRehatag = letzterRehaTag(berichtfehlt.get(i).get(3));
				rehaart = getRehaArt(berichtfehlt.get(i).get(5));
				OOTools.doCellValue(cellCursor, 0, calcrow, rehaart+" - "+name+" - Entlassung am "+letzterRehatag+" Rehaverordnung: "+berichtfehlt.get(i).get(0));
				OOTools.doCellFontName(cellCursor, 0, calcrow, "Courier New");
				calcrow+=1;
			}
			
			calcrow +=3;
			status1.setText("ermittle unklare Rehafälle");
			OOTools.doCellValue(cellCursor, 0, calcrow, (String) "*********************unklare Rehafälle ("+unklar.size()+" Fälle) ******************************" );
			OOTools.doCellFontName(cellCursor, 0, calcrow, "Courier New");
			calcrow +=1;
			
			for(int i = 0; i < unklar.size();i++){
				status1.setText("ermittle unklare Rehafälle: "+Integer.toString(i+1)+" von "+unklar.size());
				name = SqlInfo.holePatFeld("n_name","pat_intern='"+unklar.get(i).get(1)+"'")+", "+SqlInfo.holePatFeld("v_name","pat_intern='"+unklar.get(i).get(1)+"'");
				angelegtam = DatFunk.sDatInDeutsch(unklar.get(i).get(2));
				rehaart = getRehaArt(unklar.get(i).get(5));
				OOTools.doCellValue(cellCursor, 0, calcrow, rehaart+" - "+name+" angelegt am: "+angelegtam+" Rehaverordnung: "+unklar.get(i).get(0));
				OOTools.doCellFontName(cellCursor, 0, calcrow, "Courier New");
				calcrow+=1;
			}

			calcrow +=3;
			status1.setText("ermittle anwesende Rehafälle");
			OOTools.doCellValue(cellCursor, 0, calcrow, (String) "*********************anwesende Rehafälle ("+anwesend.size()+" Fälle) *****************************" );
			OOTools.doCellFontName(cellCursor, 0, calcrow, "Courier New");
			calcrow +=1;
			for(int i = 0; i < anwesend.size();i++){
				status1.setText("ermittle anwesende Rehafälle: "+Integer.toString(i+1)+" von "+anwesend.size());
				name = SqlInfo.holePatFeld("n_name","pat_intern='"+anwesend.get(i).get(1)+"'")+", "+SqlInfo.holePatFeld("v_name","pat_intern='"+anwesend.get(i).get(1)+"'");
				angelegtam = DatFunk.sDatInDeutsch(anwesend.get(i).get(2));
				ersterRehatag = ersterRehaTag(anwesend.get(i).get(3));
				rehaart = getRehaArt(anwesend.get(i).get(5));
				OOTools.doCellValue(cellCursor, 0, calcrow, rehaart+" - "+name+" angelegt am: "+angelegtam+" Erster Rehatag:"+ersterRehatag+" Rehaverordnung: "+anwesend.get(i).get(0));
				OOTools.doCellFontName(cellCursor, 0, calcrow, "Courier New");
				calcrow+=1;
			}

			calcrow +=3;
			status1.setText("ermittle angemeldete Rehafälle");
			OOTools.doCellValue(cellCursor, 0, calcrow, (String) "*********************angemeldete Rehafälle ("+angemeldet.size()+" Fälle) ****************************" );
			OOTools.doCellFontName(cellCursor, 0, calcrow, "Courier New");
			calcrow +=1;
			for(int i = 0; i < angemeldet.size();i++){
				status1.setText("ermittle angemeldete Rehafälle: "+Integer.toString(i+1)+" von "+angemeldet.size());
				name = SqlInfo.holePatFeld("n_name","pat_intern='"+angemeldet.get(i).get(1)+"'")+", "+SqlInfo.holePatFeld("v_name","pat_intern='"+angemeldet.get(i).get(1)+"'");
				angelegtam = DatFunk.sDatInDeutsch(anwesend.get(i).get(2));
				rehaart = getRehaArt(angemeldet.get(i).get(5));
				OOTools.doCellValue(cellCursor, 0, calcrow, rehaart+" - "+name+" angelegt am: "+angelegtam+" Rehaverordnung: "+angemeldet.get(i).get(0));
				OOTools.doCellFontName(cellCursor, 0, calcrow, "Courier New");
				calcrow+=1;
			}			
			final ISpreadsheetDocument xspredsheetDocument = spreadsheetDocument;
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					xspredsheetDocument.getFrame().getXFrame().getContainerWindow().setVisible(true);
					xspredsheetDocument.getFrame().setFocus();
				}
			});


		} catch (NoSuchElementException e) {
			e.printStackTrace();
		} catch (WrappedTargetException e) {
			e.printStackTrace();
		} catch (UnknownPropertyException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (OfficeApplicationException e) {
			e.printStackTrace();
		} catch (NOAException e) {
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}
	
	public void zeigeCalc(){
		final ISpreadsheetDocument xspredsheetDocument = spreadsheetDocument;
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				xspredsheetDocument.getFrame().getXFrame().getContainerWindow().setVisible(true);
				xspredsheetDocument.getFrame().setFocus();
			}
		});
	}

}
