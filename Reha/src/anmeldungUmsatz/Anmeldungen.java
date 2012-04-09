package anmeldungUmsatz;

import hauptFenster.AktiveFenster;
import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;

import rehaInternalFrame.JAnmeldungenInternal;
import sqlTools.SqlInfo;
import stammDatenTools.RezTools;
import systemTools.ButtonTools;
import systemTools.JCompTools;
import systemTools.JRtaTextField;
import terminKalender.DatFunk;


import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import events.PatStammEvent;
import events.PatStammEventClass;

public class Anmeldungen extends JXPanel{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -3835067202659704351L;
	JXPanel content = null;
	JRtaTextField[] tfs = {null,null};
	JLabel[] kglab = {null,null,null,null,null};
	JLabel[] malab = {null,null,null,null,null};
	JLabel[] erlab = {null,null,null,null,null};
	JLabel[] lolab = {null,null,null,null,null};
	JLabel[] rhlab = {null,null,null,null,null};
	JLabel[] polab = {null,null,null,null,null};
	JLabel[] summenlab = {null,null,null,null,null};
	String[] diszi1 = {"KG","MA","ER","LO","RH","PO"};
	String[] diszi2 = {"Physio","Massage","Ergo","Logo","Reha","Podo"};
	List<String> listdiszi = null;
	int[] anzahlRezepte = {0,0,0,0,0,0};
	int[] anzahlEinheiten = {0,0,0,0,0,0};
	int[] anzahlMinuten = {0,0,0,0,0,0};
	Double[] umsaetze = {0.00,0.00,0.00,0.00,0.00,0.00};
	DecimalFormat dcf = new DecimalFormat("#####0.00");
	
	//String[] anzahlen = {"0","0","0","0","0","0"};
	//String[] preise = {"0,00","0,00","0,00","0,00","0.00"};
	JButton[] buts = {null,null};
	//FortschrittDlg fortschrittDlg = null;
	public JXTable anmeldetbl = null;
	public MyAnmeldeTableModel anmeldemod;

	JAnmeldungenInternal internal;
	ActionListener al = null;
	MouseListener tblmouse = null;
	
	
	public Anmeldungen(JAnmeldungenInternal jai){
		super();
		this.internal = jai;
		this.setLayout(new BorderLayout());
		this.makeListeners();
		this.add(getContent(),BorderLayout.CENTER);
		this.listdiszi = Arrays.asList(diszi1);
		this.setName(this.internal.getName());
		this.content.setName(this.internal.getName());
		//System.out.println("Name = "+getName());
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				setzeFocus();
			}
		});
	}
	private void setzeFocus(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				tfs[0].requestFocus();
			}
		});
	}
	private JXPanel getContent(){
		JLabel lab = null;			//   1                 2     3    4     5     6     7     8     9    10      11  12      13
		FormLayout lay = new FormLayout("fill:0:grow(0.5),5dlu,20dlu,60dlu,20dlu,20dlu,60dlu,25dlu,60dlu,25dlu,60dlu,5dlu,5dlu,fill:0:grow(0.5),",
			// 1   2   3    4       5    6  7   8  9   10 11 12 13  14 15  16  17  18 19  20  21
			"15dlu,p,15dlu,80dlu:g,10dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,5dlu,p,15dlu,p,15dlu");
		CellConstraints cc = new CellConstraints();
		this.content = new JXPanel();
		this.content.setOpaque(false);
		this.content.setLayout(lay);

		lab = new JLabel("von..");
		this.content.add(lab,cc.xy(3,2));
		this.tfs[0] = new JRtaTextField("DATUM",false);
		this.tfs[0].setText(DatFunk.sHeute());
		this.content.add(this.tfs[0],cc.xy(4,2));
		
		lab = new JLabel("bis..");
		this.content.add(lab,cc.xy(6,2));
		this.tfs[1] = new JRtaTextField("DATUM",false);
		this.tfs[1].setText(DatFunk.sHeute());
		this.content.add(this.tfs[1],cc.xy(7,2));
		
		this.content.add((this.buts[0] = ButtonTools.macheButton("ermitteln", "ermitteln", al)),cc.xyw(11,2,2));
		
		anmeldemod = new MyAnmeldeTableModel();
		String[] cols = {"angelegt am","RezeptNr.","Behandl.Beginn","angelegt von","",""};
		anmeldemod.setColumnIdentifiers(cols);
		anmeldetbl = new JXTable(anmeldemod);
		anmeldetbl.getColumn(4).setMinWidth(0);
		anmeldetbl.getColumn(4).setMaxWidth(0);
		anmeldetbl.getColumn(5).setMinWidth(0);
		anmeldetbl.getColumn(5).setMaxWidth(0);
		anmeldetbl.getColumn(5).setMaxWidth(0);
		
		anmeldetbl.setEditable(false);
		anmeldetbl.addMouseListener(tblmouse);
		
		JScrollPane jscr = JCompTools.getTransparentScrollPane(anmeldetbl);
		jscr.validate();
		this.content.add(jscr,cc.xyw(3,4,10,CellConstraints.FILL,CellConstraints.DEFAULT));
		
		lab = new JLabel("Anzahl");
		lab.setForeground(Color.BLUE);
		this.content.add(lab,cc.xy(4,6,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		lab = new JLabel("Beh.Einheiten");
		lab.setForeground(Color.BLUE);
		this.content.add(lab,cc.xy(7,6,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		lab = new JLabel("Umsätze");
		lab.setForeground(Color.BLUE);
		this.content.add(lab,cc.xyw(9,6,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		lab = new JLabel("Minuten");
		lab.setForeground(Color.RED);
		this.content.add(lab,cc.xyw(11,6,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));

		
		this.content.add((kglab[0]=new JLabel("Physio")),cc.xyw(3, 8, 2,CellConstraints.LEFT,CellConstraints.DEFAULT));
		this.content.add((kglab[1]=new JLabel("0")),cc.xy(4, 8,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		this.content.add((kglab[2]=new JLabel("0")),cc.xy(7, 8,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		this.content.add((kglab[3]=new JLabel("0,00")),cc.xyw(9, 8,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		this.content.add((kglab[4]=new JLabel("0")),cc.xyw(11, 8,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		
		this.content.add((malab[0]=new JLabel("Massage")),cc.xyw(3, 10, 2,CellConstraints.LEFT,CellConstraints.DEFAULT));
		this.content.add((malab[1]=new JLabel("0")),cc.xy(4, 10,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		this.content.add((malab[2]=new JLabel("0")),cc.xy(7, 10,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		this.content.add((malab[3]=new JLabel("0,00")),cc.xyw(9, 10,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		this.content.add((malab[4]=new JLabel("0")),cc.xyw(11, 10,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));

		this.content.add((erlab[0]=new JLabel("Ergo")),cc.xyw(3, 12, 2,CellConstraints.LEFT,CellConstraints.DEFAULT));
		this.content.add((erlab[1]=new JLabel("0")),cc.xy(4, 12,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		this.content.add((erlab[2]=new JLabel("0")),cc.xy(7, 12,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		this.content.add((erlab[3]=new JLabel("0,00")),cc.xyw(9, 12,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		this.content.add((erlab[4]=new JLabel("0")),cc.xyw(11, 12,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));

		this.content.add((lolab[0]=new JLabel("Logo")),cc.xyw(3, 14, 2,CellConstraints.LEFT,CellConstraints.DEFAULT));
		this.content.add((lolab[1]=new JLabel("0")),cc.xy(4, 14,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		this.content.add((lolab[2]=new JLabel("0")),cc.xy(7, 14,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		this.content.add((lolab[3]=new JLabel("0,00")),cc.xyw(9, 14,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		this.content.add((lolab[4]=new JLabel("0")),cc.xyw(11, 14,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		
		this.content.add((rhlab[0]=new JLabel("Reha")),cc.xyw(3, 16, 2,CellConstraints.LEFT,CellConstraints.DEFAULT));
		this.content.add((rhlab[1]=new JLabel("0")),cc.xy(4, 16,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		this.content.add((rhlab[2]=new JLabel("0")),cc.xy(7, 16,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		this.content.add((rhlab[3]=new JLabel("0,00")),cc.xyw(9, 16,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		this.content.add((rhlab[4]=new JLabel("0")),cc.xyw(11, 16,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));

		this.content.add((polab[0]=new JLabel("Podo")),cc.xyw(3, 18, 2,CellConstraints.LEFT,CellConstraints.DEFAULT));
		this.content.add((polab[1]=new JLabel("0")),cc.xy(4, 18,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		this.content.add((polab[2]=new JLabel("0")),cc.xy(7, 18,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		this.content.add((polab[3]=new JLabel("0,00")),cc.xyw(9, 18,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		this.content.add((polab[4]=new JLabel("0")),cc.xyw(11, 18,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));		
		
		summenlab[0] = new JLabel("Summen");
		summenlab[0].setForeground(Color.RED);
		summenlab[1] = new JLabel("0");
		summenlab[1].setForeground(Color.RED);
		summenlab[2] = new JLabel("0");
		summenlab[2].setForeground(Color.RED);
		summenlab[3] = new JLabel("0,00");
		summenlab[3].setForeground(Color.RED);
		summenlab[4] = new JLabel("0");
		summenlab[4].setForeground(Color.RED);

		this.content.add(summenlab[0],cc.xyw(3, 20, 2,CellConstraints.LEFT,CellConstraints.DEFAULT));
		this.content.add(summenlab[1],cc.xy(4, 20,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		this.content.add(summenlab[2],cc.xy(7, 20,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		this.content.add(summenlab[3],cc.xyw(9, 20,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		this.content.add(summenlab[4],cc.xyw(11, 20,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		
		this.content.add((this.buts[1] = ButtonTools.macheButton("komplette Rezeptdaten in OO-Calc übertragen", "calc", al)),cc.xyw(3, 22,10,CellConstraints.FILL,CellConstraints.DEFAULT));
		
		return this.content;
	}
	private Anmeldungen getInstance(){
		return this;
	}
	private void makeListeners(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("ermitteln")){
							new SwingWorker<Void,Void>(){
								@Override
								protected Void doInBackground() throws Exception {
									try{
										setCursor(Reha.thisClass.wartenCursor);
										/*
										setCursor(Reha.thisClass.wartenCursor);
										fortschrittDlg = new FortschrittDlg();
										fortschrittDlg.setzeLabel("Ermittle Rezeptanzahl");
										fortschrittDlg.setVisible(true);
										fortschrittDlg.toFront();
										*/
										doErmitteln();
									}catch(Exception ex){
										ex.printStackTrace();
									}
									return null;
								}
							}.execute();
							
					return;
				}
				if(cmd.equals("calc")){
					JOptionPane.showMessageDialog(null, "Diese Funktion ist noch nicht implementiert");
				}
			}
		};
		tblmouse = new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int clicks = arg0.getClickCount();
				if(clicks==2){
					int row = anmeldetbl.getSelectedRow();
					//int mod = anmeldetbl.convertRowIndexToModel(row);
					String reznum = anmeldetbl.getValueAt(row, 1).toString();
					String pat_intern = anmeldetbl.getValueAt(row, 4).toString();
					doRezeptZeigen(pat_intern,reznum);
				}
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
			
		};
	
	}

	private void doRezeptZeigen(String pat_intern,String reznum){
		//System.out.println("Hole Daten von Patient "+pat_intern+" Rezept "+reznum);
		JComponent patient = AktiveFenster.getFensterAlle("PatientenVerwaltung");
		if(patient != null){
			if(Reha.thisClass.patpanel.aktPatID.equals(pat_intern.trim())){
				Reha.thisClass.patpanel.aktRezept.holeRezepte(pat_intern.trim(), reznum);
				//Reha.thisClass.patpanel.aktRezept.holeRezepte(pat_intern.trim(), reznum);
			}else{
				posteAktualisierung(pat_intern,reznum);				
			}
			//Reha.thisClass.patpanel.aktRezept.holeRezepte(pat_intern, reznum);
			return;
		}else{
			Reha.thisClass.progLoader.ProgPatientenVerwaltung(0);
			long zeit = System.currentTimeMillis();
			while((patient = AktiveFenster.getFensterAlle("PatientenVerwaltung"))==null){
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
				if( (System.currentTimeMillis()-zeit) > 5000 ){
					return;
				}
			}
			if(Reha.thisClass.patpanel.aktPatID.equals(pat_intern.trim())){
				Reha.thisClass.patpanel.aktRezept.holeRezepte(pat_intern.trim(), reznum);
			}else{
				posteAktualisierung(pat_intern,reznum);				
			}
			//Reha.thisClass.patpanel.aktRezept.holeRezepte(pat_intern, reznum);
			return;
		}
	}
	private void posteAktualisierung(String patid,String reznum){
		final String xpatid = patid;
		final String xreznum = reznum;
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				//System.out.println("Suche Patient:"+xpatid+" und Rezept:"+xreznum);
				String s1 = String.valueOf("#PATSUCHEN");
				String s2 = xpatid;
				PatStammEvent pEvt = new PatStammEvent(getInstance());
				pEvt.setPatStammEvent("PatSuchen");
				pEvt.setDetails(s1,s2,xreznum) ;
				PatStammEventClass.firePatStammEvent(pEvt);		
				return null;
			}
			
		}.execute();
	}
	
	private void doErmitteln(){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				anmeldemod.setRowCount(0);
				for(int i = 0; i < anzahlRezepte.length;i++){
					anzahlRezepte[i] = 0;
					anzahlEinheiten[i] = 0;
					umsaetze[i] = 0.00;
				}
				String dat1 =null;
				String dat2 =null;
				int anzahl;
				try{
					dat1 = DatFunk.sDatInSQL(tfs[0].getText().trim());
					dat2 = DatFunk.sDatInSQL(tfs[1].getText().trim());
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null,"Die Datumswerte des eingegebenen Zeitraumes sind nicht korrekt");
					//beendeFortschritt();
					return null;
				}
				//fortschrittDlg.setzeLabel("Ermittle Anzahl Rezepte");
				//fortschrittDlg.fortschritt.setIndeterminate(true);
				anzahl = SqlInfo.zaehleSaetze("verordn", "datum >='"+dat1+"' AND datum <='"+dat2+"'");
				//fortschrittDlg.fortschritt.setIndeterminate(false);
				//fortschrittDlg.fortschritt.setMinimum(0);
				//fortschrittDlg.fortschritt.setMaximum(anzahl-1);
				//long zeit = System.currentTimeMillis();
				Vector<Vector<String>> vec = SqlInfo.holeFelder("select * from verordn where datum >='"+dat1+"' AND datum <='"+dat2+"'");
				Vector<String> vec2 = new Vector<String>();
				String fehler = "";
				for(int i = 0; i < anzahl;i++){
					//if(fortschrittDlg != null){
						//fortschrittDlg.fortschritt.setValue(i);	
						//fortschrittDlg.setzeLabel("Analysiere Rezept "+Integer.toString(i+1));
						vec2.clear();
						try{
							vec2.add(DatFunk.sDatInDeutsch(vec.get(i).get(22).trim()));	
						}catch(Exception ex){
							vec2.add("falsches Datum");
						}
						vec2.add(vec.get(i).get(1));
						fehler = ermittleFehler(vec.get(i));
						vec2.add(fehler);
						vec2.add(vec.get(i).get(45));
						vec2.add(vec.get(i).get(0));
						vec2.add("");
						try{
							doRechnen(vec.get(i));	
						}catch(Exception ex2){
							ex2.printStackTrace();
						}
						anmeldemod.addRow((Vector<?>)vec2.clone());
					//}
				}
				//beendeFortschritt();
				kglab[1].setText(Integer.toString(anzahlRezepte[0]));
				kglab[2].setText(Integer.toString(anzahlEinheiten[0]));
				kglab[3].setText(dcf.format(umsaetze[0]));
				kglab[4].setText(Integer.toString(anzahlMinuten[0]));

				malab[1].setText(Integer.toString(anzahlRezepte[1]));
				malab[2].setText(Integer.toString(anzahlEinheiten[1]));
				malab[3].setText(dcf.format(umsaetze[1]));
				malab[4].setText(Integer.toString(anzahlMinuten[1]));
				
				erlab[1].setText(Integer.toString(anzahlRezepte[2]));
				erlab[2].setText(Integer.toString(anzahlEinheiten[2]));
				erlab[3].setText(dcf.format(umsaetze[2]));
				erlab[4].setText(Integer.toString(anzahlMinuten[2]));

				lolab[1].setText(Integer.toString(anzahlRezepte[3]));
				lolab[2].setText(Integer.toString(anzahlEinheiten[3]));
				lolab[3].setText(dcf.format(umsaetze[3]));
				lolab[4].setText(Integer.toString(anzahlMinuten[3]));

				rhlab[1].setText(Integer.toString(anzahlRezepte[4]));
				rhlab[2].setText(Integer.toString(anzahlEinheiten[4]));
				rhlab[3].setText(dcf.format(umsaetze[4]));
				rhlab[4].setText(Integer.toString(anzahlMinuten[4]));
				
				polab[1].setText(Integer.toString(anzahlRezepte[5]));
				polab[2].setText(Integer.toString(anzahlEinheiten[5]));
				polab[3].setText(dcf.format(umsaetze[5]));
				polab[4].setText(Integer.toString(anzahlMinuten[5]));

				int anzahl1 = 0;
				int anzahl2 = 0;
				int minuten2 = 0;
				double umsatz1 = 0.00;
				for(int i = 0;i<5;i++){
					anzahl1 = anzahl1 +anzahlRezepte[i];
					anzahl2 = anzahl2 +anzahlEinheiten[i];
					umsatz1 = umsatz1+umsaetze[i];
					minuten2 = minuten2+anzahlMinuten[i];
					
				}
				summenlab[1].setText(Integer.toString(anzahl1));
				summenlab[2].setText(Integer.toString(anzahl2));
				summenlab[3].setText(dcf.format(umsatz1));
				summenlab[4].setText(Integer.toString(minuten2));
				setCursor(Reha.thisClass.cdefault);
				return null;
			}
			
		}.execute();
	}
	private void doRechnen(Vector<String> vec){
		int idisziplin =  this.listdiszi.indexOf(vec.get(1).substring(0,2));
		int anzahl;
		BigDecimal bdpreis; // = Double.valueOf("0.00");
		BigDecimal bdgesamt; // = Double.valueOf("0.00");
		Object[] hbs = null;
		anzahlRezepte[idisziplin]++;
		for(int i = 0;i<4;i++){
			try{
				if( Integer.parseInt(vec.get(i+8)) > 0){
					anzahl= Integer.parseInt(vec.get(i+3));
					if(i==0){
						anzahlEinheiten[idisziplin] = anzahlEinheiten[idisziplin]+anzahl; 
						anzahlMinuten[idisziplin] = anzahlMinuten[idisziplin]+( Integer.parseInt(vec.get(47))*anzahl );
					}
					bdpreis = BigDecimal.valueOf(Double.parseDouble(vec.get(i+3))).multiply(
							BigDecimal.valueOf(Double.parseDouble(vec.get(i+18))) );
					bdgesamt = BigDecimal.valueOf(umsaetze[idisziplin]).add(bdpreis);
					umsaetze[idisziplin] = bdgesamt.doubleValue();
				}
			}catch(Exception ex){
				
			}
		}
		//Hausbesuchssaich
		if(vec.get(43).trim().equals("T")){
			hbs = RezTools.ermittleHBwert(vec);
			if(hbs[0]!=null){
				bdgesamt = BigDecimal.valueOf(umsaetze[idisziplin]).add(BigDecimal.valueOf((Double)hbs[0]));
				umsaetze[idisziplin] = bdgesamt.doubleValue();
			}
			if(hbs[1]!=null){
				bdgesamt = BigDecimal.valueOf(umsaetze[idisziplin]).add(BigDecimal.valueOf((Double)hbs[1]));
				umsaetze[idisziplin] = bdgesamt.doubleValue();
			}
			/*
			//System.out.println("HB-Werte für Rezept: "+vec.get(1));
			//System.out.println(hbs[0]);
			//System.out.println(hbs[1]);
			*/
		}
		

	}
	private String ermittleFehler(Vector<String> vec){
		String lastdate = "";
		String heutedate = "";
		String erstdate = "";
		if(!vec.get(34).trim().equals("")){
			//Wenn bereits Termine eingetragen wurden
			try{
				erstdate = vec.get(34).split("\\n")[0].split("@")[0];
				long tage = DatFunk.TageDifferenz(DatFunk.sDatInDeutsch(vec.get(40)),erstdate);
				//System.out.println("Tage = "+tage);
				if(tage > 10){
					return "verspätet";
				}else{
					return "";
				}
			}catch(Exception ex){
				return "unbekannter Fehler";
			}
		}
		heutedate = DatFunk.sHeute();
		lastdate = DatFunk.sDatInDeutsch(vec.get(40));
		long tage = DatFunk.TageDifferenz(lastdate,heutedate );
		//System.out.println("Tage ohne eingetragene Behandlung "+tage);
		if(tage > 10){
			return "verspätet";
		}else{
			return "";
		}
	}

	class MyAnmeldeTableModel extends DefaultTableModel{
		   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Class<?> getColumnClass(int columnIndex) {
			   if(columnIndex==0){return String.class;}
			   else{return String.class;}
		}

	    public boolean isCellEditable(int row, int col) {
	    	return true;
	    }
		public Object getValueAt(int rowIndex, int columnIndex) {
			String theData = (String) ((Vector<?>)getDataVector().get(rowIndex)).get(columnIndex); 
			Object result = null;
			result = theData;
			return result;
		}
		    
		   
	}

	public void doAufraeumen(){
		buts[0].removeActionListener(al);
		buts[1].removeActionListener(al);
		al = null;
		anmeldetbl.removeMouseListener(tblmouse);
		tblmouse = null;
		tfs[0].listenerLoeschen();
		tfs[1].listenerLoeschen();
	}

}
