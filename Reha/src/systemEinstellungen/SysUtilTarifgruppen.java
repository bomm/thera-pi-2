package systemEinstellungen;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import jxTableTools.DateTableCellEditor;
import jxTableTools.MitteRenderer;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;

import preisListenHandling.MachePreisListe;
import systemTools.JCompTools;
import systemTools.JRtaComboBox;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SysUtilTarifgruppen extends JXPanel implements KeyListener, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6545731409903464590L;

	JButton[] button = {null,null,null,null,null,null,null};

	MyTarifeTableModel modtarife = new MyTarifeTableModel();
	JXTable tarife = null;
	JComboBox zuza = null;
	ButtonGroup bgroup = new ButtonGroup();
	String[] zzregel = null;
	String[] zzart = null;
	JRtaComboBox disziplin = null;
	String[] bereich_lang = null;
	String[] bereich_kurz = null;
	String[] janein = {"  - JA -  ","  - NEIN -  "};
	String[] hbmehrereart = {"nicht abrechenbar","x9902 Mehrere o. Wegegeld","x9934 Mehrere incl. Wegegeld","8602 BG (Einzel) o. Wegegeld","53 Beih. (Einzel) o. Wegegeld"};
	String[] hbeinzelart = {"nicht abrechenbar","x9901 Einzel o. Wegegeld","x9933 Einzel incl. Wegegeld","8602 BG Einzel o. Wegegeld","53 Beih. Einzel o. Wegegeld"};
	String[] wgkm = {"nicht abrechenbar","x9907 (teilw.GKV u.teilw.BG)","8603 (teilw. BG)","54 (Beihilfe)"};
	String[] wgpausch = {"nicht abrechenbar","x9903 (teilw. GKV)","x9906 (teilw. GKV)"};
	String[] arztbericht = {"nicht abrechenbar","x9701 (teilw. GKV)"};
	JLabel lbltgruppe = null; 
	public SysUtilTarifgruppen(){
		super(new BorderLayout());
		//System.out.println("Aufruf SysUtilKalenderanlagen");
		this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 5));
		/****/
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("SystemInit"));
		/****/
	     add(getVorlagenSeite(),BorderLayout.CENTER);
	     add(getKnopfPanel(),BorderLayout.SOUTH);
			new SwingWorker<Void,Void>(){

				@Override
				protected Void doInBackground() throws Exception {
					fuelleMitWerten(0);
					return null;
				}
				
			}.execute();	     
		return;
	}
	/************** Beginn der Methode f�r die Objekterstellung und -platzierung *********/
	private JPanel getVorlagenSeite(){
		
		disziplin = new JRtaComboBox(new String[] {"Physio","Massage","Ergo","Logo","Reha","Podo"});
		disziplin.setActionCommand("disziplin");
		disziplin.addActionListener(this);
		modtarife.setColumnIdentifiers(new String[] {"Tarifgruppe","Zuzahlungsregel","gültig ab","Anwendungsregel","Gültik.Bereich","§302-Abrechnung","Pos.HB-Einzeln","Pos.HB-Mehrere","Pos.Weg/km","Pos.Weg/Pauschal","HB-Heim mit Zuzahl.","Arztbericht"});
		tarife = new JXTable(modtarife);
		tarife.getColumn(0).setMinWidth(120);
		tarife.getColumn(1).setMinWidth(100);
		tarife.getColumn(2).setMinWidth(80);
		tarife.getColumn(2).setMaxWidth(80);
		tarife.getColumn(3).setMinWidth(120);
		tarife.getColumn(4).setMinWidth(120);
		MitteRenderer mr = new MitteRenderer();
		tarife.getColumn(5).setCellRenderer(mr);
		tarife.getColumn(5).setMinWidth(90);
		tarife.getColumn(6).setMinWidth(180);
		tarife.getColumn(6).setCellRenderer(mr);
		tarife.getColumn(7).setMinWidth(180);
		tarife.getColumn(7).setCellRenderer(mr);
		tarife.getColumn(8).setMinWidth(180);
		tarife.getColumn(8).setCellRenderer(mr);
		tarife.getColumn(9).setMinWidth(180);
		tarife.getColumn(9).setCellRenderer(mr);
		tarife.getColumn(10).setMinWidth(120);
		tarife.getColumn(10).setCellRenderer(mr);
		tarife.getColumn(11).setMinWidth(120);
		tarife.getColumn(11).setCellRenderer(mr);

		TableColumn zuzahlColumn = tarife.getColumnModel().getColumn(1);
		zzregel = new String[] {"keine Zuzahlung","gesetzl. Zuzahlung"};
		JComboBox comboBox = new JComboBox(zzregel);

		zuzahlColumn.setCellEditor(new DefaultCellEditor(comboBox));
		zuzahlColumn = tarife.getColumnModel().getColumn(3);
		zzart = new String[] {"nicht relevant","erste Behandlung >=","Rezeptdatum >=","beliebige Behandlung >=","Rezept splitten"};
		comboBox = new JComboBox(zzart);
		zuzahlColumn.setCellEditor(new DefaultCellEditor(comboBox));
		TableColumn neuPreisDat = tarife.getColumnModel().getColumn(2);
		neuPreisDat.setCellEditor(new DateTableCellEditor());
		//neuPreisDat.setCellEditor(new DatumTableCellEditor());
		comboBox = new JComboBox(getBereichLang());
		tarife.getColumn(4).setCellEditor(new DefaultCellEditor(comboBox));
		
		//String[] janein = {"  - JA -  ","  - NEIN -  "};
		comboBox = new JComboBox(janein);
		tarife.getColumn(5).setCellEditor(new DefaultCellEditor(comboBox));

		//String[] hbeinzelart = {"nicht abrechenbar","x9901 Einzel o. Wegegeld","x9933 Einzel incl. Wegegeld","8602 BG Einzel o. Wegegeld","53 Beih. Einzel o. Wegegeld"};
		comboBox = new JComboBox(hbeinzelart);
		tarife.getColumn(6).setCellEditor(new DefaultCellEditor(comboBox));
		
		//String[] hbmehrereart = {"nicht abrechenbar","x9902 Mehrere o. Wegegeld","x9934 Mehrere incl. Wegegeld","8602 BG (Einzel) o. Wegegeld","53 Beih. (Einzel) o. Wegegeld"};
		comboBox = new JComboBox(hbmehrereart);
		tarife.getColumn(7).setCellEditor(new DefaultCellEditor(comboBox));
		
		//String[] wgkm = {"nicht abrechenbar","x9907 (teilw.GKV u.teilw.BG)","8603 (teilw. BG)","54 (Beihilfe)"};
		comboBox = new JComboBox(wgkm);
		tarife.getColumn(8).setCellEditor(new DefaultCellEditor(comboBox));
		
		//String[] wgpausch = {"nicht abrechenbar","x9903 (teilw. GKV)"};
		comboBox = new JComboBox(wgpausch);
		tarife.getColumn(9).setCellEditor(new DefaultCellEditor(comboBox));
		
		comboBox = new JComboBox(janein);
		tarife.getColumn(10).setCellEditor(new DefaultCellEditor(comboBox));

		comboBox = new JComboBox(arztbericht);
		tarife.getColumn(11).setCellEditor(new DefaultCellEditor(comboBox));

		tarife.setSelectionMode(0);
		tarife.setSortable(false);
		tarife.getSelectionModel().addListSelectionListener( new TarifeListSelectionHandler());
		tarife.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent arg0) {
				tarife.requestFocus();
				if(arg0.getClickCount()==1){
					SwingUtilities.invokeLater(new Runnable(){
						public void run(){
							tarife.setRowSelectionInterval(tarife.getSelectedRow(), tarife.getSelectedRow());
							tarife.setColumnSelectionInterval(tarife.getSelectedColumn(), tarife.getSelectedColumn());
							//startCellEditing(tarife,tarife.getSelectedRow(),tarife.getSelectedColumn());
						}
					});
					return;
				}else if(arg0.getClickCount()==2){
					SwingUtilities.invokeLater(new Runnable(){
						public void run(){
							tarife.setRowSelectionInterval(tarife.getSelectedRow(), tarife.getSelectedRow());
							tarife.setColumnSelectionInterval(tarife.getSelectedColumn(), tarife.getSelectedColumn());
							startCellEditing(tarife,tarife.getSelectedRow(),tarife.getSelectedColumn());
						}
					});
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			private void startCellEditing(JXTable table,int row,int col){
				final int xrows = row;
				final int xcols = col;
				final JXTable xtable = table;
				SwingUtilities.invokeLater(new Runnable(){
				 	   public  void run(){
				 		  xtable.setRowSelectionInterval(xrows, xrows);
				 		 xtable.setColumnSelectionInterval(xcols, xcols);
				 		  xtable.scrollRowToVisible(xrows);
				 				xtable.editCellAt(xrows,xcols );
				 	   }
				});
			}

			
		});
		
        //                                      1.            2.     3.     4.     5.     6.    7.      8.     9.
		FormLayout lay = new FormLayout("right:120dlu, 20dlu, 40dlu, 40dlu, 4dlu, 80dlu:g",
       //1.    2. 3.   4.  5.   6.   7.   8.     9.    10.    11.    12.   13.  14.   15.   16.    17.   18.  19.  20.    21.    22.   23.   24     25    26    27    28   29
		"p, 2dlu, p, 10dlu,p, 10dlu, p, 10dlu, 150dlu, 10dlu,  p,  10dlu");
		

		
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		builder.addSeparator("Disziplin auswählen", cc.xyw(1, 3, 6));
		builder.add(disziplin, cc.xy(6,5));
		
		
		builder.addSeparator("Tarifgruppen-Verwaltung", cc.xyw(1, 7, 6));

		JScrollPane jscrPane = JCompTools.getTransparentScrollPane(tarife);
		jscrPane.validate();
		
		JScrollPane jscrPane2 = JCompTools.getTransparent2ScrollPane(jscrPane);
		jscrPane2.validate();
		
		jscrPane.getVerticalScrollBar().setUnitIncrement(15);
		
		//builder.add(jscrPane, cc.xyw(1,9, 6));

		builder.add(jscrPane2, cc.xyw(1,9, 6));
		
		lbltgruppe = new JLabel();
		builder.add(lbltgruppe,cc.xy(1,11));
		builder.getPanel().validate();
		
		return builder.getPanel();
	}
	private JPanel getKnopfPanel(){
		button[4] = new JButton("neu");
		button[4].setActionCommand("neu");
		button[4].addActionListener(this);
		if(!Reha.vollbetrieb){
			button[4].setEnabled(false);			
		}
		button[5] = new JButton("abbrechen");
		button[5].setActionCommand("abbrechen");
		button[5].addActionListener(this);
		button[6] = new JButton("speichern");
		button[6].setActionCommand("speichern");
		button[6].addActionListener(this);		
									//      1.                      2.    3.    4.     5.     6.    7.      8.     9.
		FormLayout jpanlay = new FormLayout("right:120dlu, 50dlu, 40dlu,10dlu, 40dlu, 4dlu, 40dlu",
       //1.    2. 3.   4.   5.   6.     7.    8. 9.  10.  11. 12. 13.  14.  15. 16.  17. 18.  19.   20.    21.   22.   23.
		"p, 10dlu, p");
		
		PanelBuilder jpan = new PanelBuilder(jpanlay);
		jpan.getPanel().setOpaque(false);		
		CellConstraints jpancc = new CellConstraints();
		
		jpan.addSeparator("", jpancc.xyw(1,1,7));
		jpan.add(button[4], jpancc.xy(3,3));
		jpan.add(button[5], jpancc.xy(5,3));
		jpan.add(button[6], jpancc.xy(7,3));
		jpan.addLabel("Neue Tarifgruppe / Änderungen speichern?", jpancc.xy(1,3));
		
		
		return jpan.getPanel();
	}
	private void fuelleMitWerten(int cmbwert){
		modtarife.setRowCount(0);
		//int lang = SystemConfig.vPreisGruppen.size();
		String diszi = (String)disziplin.getSelectedItem();
		int lang = SystemPreislisten.hmPreisGruppen.get(diszi).size();
		Vector<String> vec = new Vector<String>();
		String wert = "";
		for(int i = 0;i < lang;i++){
			vec.clear();

			/*********************************/
			wert = SystemPreislisten.hmPreisGruppen.get(diszi).get(i);
			vec.add(wert);
			/*********************************/
			vec.add(zzregel[SystemPreislisten.hmZuzahlRegeln.get(diszi).get(i)]);
			wert = SystemPreislisten.hmNeuePreiseAb.get(diszi).get(i);
			vec.add(wert);
			/*********************************/
			vec.add(zzart[SystemPreislisten.hmNeuePreiseRegel.get(diszi).get(i)]);
			/*********************************/			
			wert = SystemPreislisten.hmPreisBereich.get(diszi).get(i);
			int treffer = Arrays.asList(getBereichKurz()).indexOf(wert);
			if(treffer >= 0){
				vec.add(getBereichLang()[treffer]);
			}else{
				vec.add(getBereichLang()[0]);
			}
			/*********************************/
			int iwert = SystemPreislisten.hmHMRAbrechnung.get(diszi).get(i);
			if(iwert==0){
				vec.add("  - NEIN -  ");
			}else{
				vec.add("  - JA -  ");
			}
			/*********************************/
			wert = SystemPreislisten.hmHBRegeln.get(diszi).get(i).get(0);
			if(wert.trim().equals("")){
				vec.add("nicht abrechenbar");
			}else if(wert.trim().contains("9901")){
				vec.add("x9901 Einzel o. Wegegeld");
			}else if(wert.trim().contains("9933")){
				vec.add("x9933 Einzel incl. Wegegeld");
			}else if(wert.trim().contains("8602")){
				vec.add("8602 BG Einzel o. Wegegeld");
			}else if(wert.trim().contains("53")){
				vec.add("53 Beih. Einzel o. Wegegeld");
			}
			/*********************************/
			//String[] hbmehrereart = {"nicht abrechenbar","x9902 Mehrere o. Wegegeld","x9934 Mehrere incl. Wegegeld","8602 BG (Einzel) o. Wegegeld","54 Beih. (Einzel) o. Wegegeld"};
			wert = SystemPreislisten.hmHBRegeln.get(diszi).get(i).get(1);
			if(wert.trim().equals("")){
				vec.add("nicht abrechenbar");
			}else if(wert.trim().contains("9902")){
				vec.add("x9902 Mehrere o. Wegegeld");
			}else if(wert.trim().contains("9934")){
				vec.add("x9934 Mehrere incl. Wegegeld");
			}else if(wert.trim().contains("8602")){
				vec.add("8602 BG (Einzel) o. Wegegeld");
			}else if(wert.trim().contains("53")){
				vec.add("53 Beih. (Einzel) o. Wegegeld");
			} 			
			/*********************************/			
			wert = SystemPreislisten.hmHBRegeln.get(diszi).get(i).get(2);
			if(wert.trim().equals("")){
				vec.add("nicht abrechenbar");
			}else if(wert.trim().contains("9907")){
				vec.add("x9907 (teilw.GKV u.teilw.BG)");
			}else if(wert.trim().contains("8603")){
				vec.add("8603 (teilw. BG)");
			}else if(wert.trim().contains("54")){
				vec.add("54 (Beihilfe)");
			} 			
			/*********************************/
			wert = SystemPreislisten.hmHBRegeln.get(diszi).get(i).get(3);
			if(wert.trim().equals("")){
				vec.add("nicht abrechenbar");
			}else if(wert.trim().contains("9903")){
				vec.add("x9903 (teilw. GKV)");
			}else if(wert.trim().contains("9906")){
				vec.add("x9906 (teilw. GKV)");
			}else{
				vec.add("nicht abrechenbar");				
			}
			/*********************************/
			wert = SystemPreislisten.hmHBRegeln.get(diszi).get(i).get(4);
			if(wert.equals("0")){
				vec.add("  - NEIN -  ");
			}else{
				vec.add("  - JA -  ");
			}	
			/*********************************/
			if(SystemPreislisten.hmBerichtRegeln.get(diszi).get(i).indexOf("9701") < 0){
				vec.add("nicht abrechenbar");
			}else{
				vec.add("x9701 (teilw. GKV)");
			}			
			modtarife.addRow((Vector<?>)vec.clone());
		}
		tarife.validate();
		tarife.repaint();
		
	}
	
	private void doSpeichern(){
		//String wert = "";
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/preisgruppen.ini");

		//int lang = SystemConfig.vPreisGruppen.size();
		int lang = tarife.getRowCount();
		String diszi = (String)disziplin.getSelectedItem();
		String[] disziindex = {"2","1","5","3","8","7"};
		int idiszi = disziplin.getSelectedIndex();
		String swert = "";
		
		String[] resulthbeinzel = {"",disziindex[idiszi]+"9901",disziindex[idiszi]+"9933","8602","53"};
		String[] resulthbmehrere = {"",disziindex[idiszi]+"9902",disziindex[idiszi]+"9934","8602","53"};
		String[] resultwgkm = {"",disziindex[idiszi]+"9907","8603","54"};
		String[] resultwgpausch = {"",disziindex[idiszi]+"9903",disziindex[idiszi]+"9906"};
		String[] resultarztbericht = {"",disziindex[idiszi]+"9701"};


		for(int i = 0;i<lang;i++){
			try{
				//Preisgruppen-Name
				swert = String.valueOf((String) tarife.getValueAt(i, 0));
				//SystemConfig.vPreisGruppen.set(i, swert);
				SystemPreislisten.hmPreisGruppen.get(diszi).set(i,swert);
				inif.setStringProperty("PreisGruppen_"+diszi, "PGName"+(i+1),swert , null);
				
				//Zuzahlregel-einstellen
				swert = String.valueOf((String) tarife.getValueAt(i, 1));
				int zzreg = stringPosErmitteln(zzregel,swert);
				//SystemConfig.vZuzahlRegeln.set(i, zzreg);
				SystemPreislisten.hmZuzahlRegeln.get(diszi).set(i, zzreg);
				
				inif.setIntegerProperty("ZuzahlRegeln_"+diszi, "ZuzahlRegel"+(i+1),zzreg , null);
				
				//Preisveränderung-einstellen
				/**********
				 * Abpr�fen welche Disziplin
				 */
				swert = String.valueOf((String) tarife.getValueAt(i, 2));
				if(swert.equals(".  .")){
					swert="";
				}
				
				//((Vector)SystemConfig.vNeuePreiseAb.get(disziplin.getSelectedIndex())).set(i, swert);
				SystemPreislisten.hmNeuePreiseAb.get(diszi).set(i, swert);
				//Vergessen!!!!!!
				inif.setStringProperty("PreisRegeln_"+diszi, "PreisAb"+(i+1),swert , null);
				
				//Splittingregel-einstellen
				swert = String.valueOf((String) tarife.getValueAt(i, 3));
				zzreg = stringPosErmitteln(zzart,swert);
				//((Vector)SystemConfig.vNeuePreiseRegel.get(disziplin.getSelectedIndex())).set(i, zzreg);
				SystemPreislisten.hmNeuePreiseRegel.get(diszi).set(i, zzreg);
				inif.setIntegerProperty("PreisRegeln_"+diszi, "PreisRegel"+(i+1),zzreg , null);

				swert = String.valueOf((String) tarife.getValueAt(i, 4));
				int treffer = Arrays.asList(getBereichLang()).indexOf(swert);
				if(treffer < 0){
					SystemPreislisten.hmPreisBereich.get(diszi).set(i, "-1");
					inif.setStringProperty("PreisGruppen_"+diszi, "PGBereich"+(i+1),"-1" , null);
				}else{
					SystemPreislisten.hmPreisBereich.get(diszi).set(i, getBereichKurz()[treffer]);
					inif.setStringProperty("PreisGruppen_"+diszi, "PGBereich"+(i+1),getBereichKurz()[treffer] , null);
				}
				/*
			 	String[] janein = {"  - JA -  ","  - NEIN -  "};
				String[] hbmehrereart = {"nicht abrechenbar","x9902 Mehrere o. Wegegeld","x9934 Mehrere incl. Wegegeld","8602 BG (Einzel) o. Wegegeld","53 Beih. (Einzel) o. Wegegeld"};
				String[] hbeinzelart = {"nicht abrechenbar","x9901 Einzel o. Wegegeld","x9933 Einzel incl. Wegegeld","8602 BG Einzel o. Wegegeld","53 Beih. Einzel o. Wegegeld"};
				String[] wgkm = {"nicht abrechenbar","x9907 (teilw.GKV u.teilw.BG)","8603 (teilw. BG)","54 (Beihilfe)"};
				String[] wgpausch = {"nicht abrechenbar","x9903 (teilw. GKV)"};
				 */
				swert = String.valueOf((String) tarife.getValueAt(i, 5));
				treffer = Arrays.asList(janein).indexOf(swert);
				
				SystemPreislisten.hmHMRAbrechnung.get(diszi).set(i, (treffer==0 ? 1 : 0));
				inif.setIntegerProperty("HMRAbrechnung_"+diszi, "HMRAbrechnung"+(i+1), (treffer==0 ? 1 : 0), null);
				
				
				swert = String.valueOf((String) tarife.getValueAt(i, 6));
				treffer = Arrays.asList(hbeinzelart).indexOf(swert);
				if(treffer >= 0){
					SystemPreislisten.hmHBRegeln.get(diszi).get(i).set(0, resulthbeinzel[treffer]);
					inif.setStringProperty("HBRegeln_"+diszi, "HBPosVoll"+(i+1), resulthbeinzel[treffer], null);
				}else{
					SystemPreislisten.hmHBRegeln.get(diszi).get(i).set(0, "");
					inif.setStringProperty("HBRegeln_"+diszi, "HBPosVoll"+(i+1), "", null);
				}
				
				swert = String.valueOf((String) tarife.getValueAt(i, 7));
				treffer = Arrays.asList(hbmehrereart).indexOf(swert);
				if(treffer >= 0){
					SystemPreislisten.hmHBRegeln.get(diszi).get(i).set(1, resulthbmehrere[treffer]);
					inif.setStringProperty("HBRegeln_"+diszi, "HBPosMit"+(i+1), resulthbmehrere[treffer], null);
				}else{
					SystemPreislisten.hmHBRegeln.get(diszi).get(i).set(1, "");
					inif.setStringProperty("HBRegeln_"+diszi, "HBPosMit"+(i+1), "", null);
				}
				
				swert = String.valueOf((String) tarife.getValueAt(i, 8));
				treffer = Arrays.asList(wgkm).indexOf(swert);
				if(treffer >= 0){
					SystemPreislisten.hmHBRegeln.get(diszi).get(i).set(2, resultwgkm[treffer]);
					inif.setStringProperty("HBRegeln_"+diszi, "HBKilometer"+(i+1), resultwgkm[treffer], null);
				}else{
					SystemPreislisten.hmHBRegeln.get(diszi).get(i).set(2, "");
					inif.setStringProperty("HBRegeln_"+diszi, "HBKilometer"+(i+1),"", null);
				}

				
				swert = String.valueOf((String) tarife.getValueAt(i, 9));
				treffer = Arrays.asList(wgpausch).indexOf(swert);
				if(treffer >= 0){
					SystemPreislisten.hmHBRegeln.get(diszi).get(i).set(3, resultwgpausch[treffer]);
					inif.setStringProperty("HBRegeln_"+diszi, "HBPauschal"+(i+1), resultwgpausch[treffer], null);
				}else{
					SystemPreislisten.hmHBRegeln.get(diszi).get(i).set(3, "");
					inif.setStringProperty("HBRegeln_"+diszi, "HBPauschal"+(i+1), "", null);
				}
				
						
				swert = String.valueOf((String) tarife.getValueAt(i, 10));
				treffer = Arrays.asList(janein).indexOf(swert);
				SystemPreislisten.hmHBRegeln.get(diszi).get(i).set(4, (treffer==0 ? "1" : "0"));
				inif.setStringProperty("HBRegeln_"+diszi, "HBHeimMitZuZahl"+(i+1), (treffer==0 ? "1" : "0"), null);
				
				swert = String.valueOf((String) tarife.getValueAt(i, 11));
				treffer = Arrays.asList(arztbericht).indexOf(swert);
				SystemPreislisten.hmBerichtRegeln.get(diszi).set(i, (treffer==0 ? "" : resultarztbericht[treffer]));
				inif.setStringProperty("BerichtRegeln_"+diszi, "Bericht"+(i+1), (treffer==0 ? "" : resultarztbericht[treffer]), null);
				/*
				System.out.println("*********");
				System.out.println("BerichtRegeln_"+diszi);
				System.out.println("Bericht"+(i+1));
				System.out.println((treffer==0 ? "" : resultarztbericht[treffer]));
				System.out.println("*********");
				*/
				/*
				 * 
				 * Hier rein die Fristengeschichte
				 * zuerst abprüfen ob die Fristen existieren wenn nicht neue anlegen.				 *
				 *  
				 */
				if( i > ((Vector<?>)SystemPreislisten.hmFristen.get(diszi).get(0)).size() ){
					System.out.println("Erstelle Parameter für Disziplin="+diszi+" Preisgruppe="+Integer.toString(i+1));
					SystemPreislisten.fristenini.setIntegerProperty("Fristen_"+diszi, "FristBeginn"+Integer.toString(i+1),14,null);
					SystemPreislisten.fristenini.setIntegerProperty("Fristen_"+diszi, "BeginnKalendertage"+Integer.toString(i+1),1,null);
					SystemPreislisten.fristenini.setIntegerProperty("Fristen_"+diszi, "FristUnterbrechung"+Integer.toString(i+1),14,null);
					SystemPreislisten.fristenini.setIntegerProperty("Fristen_"+diszi, "UnterbrechungKalendertage"+Integer.toString(i+1),1,null);
					SystemPreislisten.fristenini.save();
				}
				
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		inif.save();
		JOptionPane.showMessageDialog(null,"Konfiguration für -> "+diszi+" <- wurde erfolgreich gespeichert");
	}
	private int stringPosErmitteln(String[] str,String vergleich){
		for(int i = 0;i < str.length;i++){
			if(str[i].equals(vergleich)){
				return i;
			}
		}
		return -1;
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
		if(cmd.equals("disziplin")){
			this.fuelleMitWerten(disziplin.getSelectedIndex());
			return;
		}
		if(cmd.equals("speichern")){
			doSpeichern();
			return;
		}
		if(cmd.equals("abbrechen")){
			SystemInit.abbrechen();
			//SystemUtil.thisClass.parameterScroll.requestFocus();
			return;
		}
		if(cmd.equals("neu")){
			doNeueGruppe();
			return;
		}
	}
	private String[] getBereichLang(){
		String[] ret = {"nicht relevant", "Bundesweit","Baden-Württemberg","Bayern",
				"Berlin Ost","Bremen","Hamburg","Hessen","Niedersachsen",
				"Nordrhein-Westfalen","Rheinland-Pfalz","Saarland","Schleswig-Holstein",
				"Brandenburg","Sachsen","Sachsen-Anhalt","Mecklenburg-Vorpommern",
				"Thüringen","Stgt. und Karlsruhe","Freiburg u. Tübingen","Berlin West",
				"Nordrhein","Westfalen-Lippe","Lippe","Berlin (gesamt)",
				"alte Bundesländer","neue Bundesländer","KK-Einzelvertrag","sonstige Idiviualtarife"};
		return ret;
	}

	private String[] getBereichKurz(){
		String[] ret = {"-1","00","01","02",
				"03","04","05","06","07",
				"08","09","10","11",
				"12","13","14","15",
				"16","17","18","19",
				"20","21","22","23",
				"24","25","91","99"};
		return ret;
	}
	private void doNeueGruppe(){
		String message = "<html><b>In der Folge wird eine neue Tarifgruppe angelegt,<br>gültig für die Disziplinen<br><br>"+
		"<font color='#0000ff'>Physio<br>Massage<br>Ergo<br>Logo<br>Reha<br>Podo</font><br><br>"+
		"Wollen Sie die Tarifgruppe jetzt anlegen<b><br></html>";
		int anfrage = JOptionPane.showConfirmDialog(null, message, "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
		if(anfrage == JOptionPane.YES_OPTION){
			Object ret = JOptionPane.showInputDialog(null, "Geben Sie bitte einen gemeinsamen Namen für die neue Preisgruppe ein", "gemeinsamer Name");
			if(ret == null){
				return;
			}
			if(ret.equals("")){
				return;
			}
			macheNeuePreistabelle();
			setzeIniEintraege(tarife.getRowCount()+1,String.valueOf(ret));
			SystemPreislisten.loescheHashMaps();
			SystemPreislisten.ladePreise("Physio");
			SystemPreislisten.ladePreise("Massage");
			SystemPreislisten.ladePreise("Ergo");
			SystemPreislisten.ladePreise("Logo");
			SystemPreislisten.ladePreise("Reha");
			SystemPreislisten.ladePreise("Common");
			SystemPreislisten.ladePreise("Podo");
			fuelleMitWerten(disziplin.getSelectedIndex());
		}
	}
	private void setzeIniEintraege(int position,String commonname){
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/preisgruppen.ini");
		inif.setIntegerProperty("PreisGruppen_Common", "AnzahlPreisGruppen", position, null);
		inif.setStringProperty("PreisGruppen_Common", "PGName"+Integer.toString(position), commonname, null);
		inif.setStringProperty("PreisGruppen_Common", "PGBereich"+Integer.toString(position), "00", null);
		String[] diszis = {"Physio","Massage","Ergo","Logo","Reha","Podo"};
		for(int i = 0; i < diszis.length;i++){
			inif.setIntegerProperty("PreisGruppen_"+diszis[i], "AnzahlPreisGruppen", position, null);
			inif.setStringProperty("PreisGruppen_"+diszis[i], "PGName"+Integer.toString(position), commonname, null);
			inif.setStringProperty("PreisGruppen_"+diszis[i], "PGBereich"+Integer.toString(position), "00", null);
			
			inif.setStringProperty("PreisRegeln_"+diszis[i], "PreisAb"+Integer.toString(position), "", null);
			inif.setStringProperty("PreisRegeln_"+diszis[i], "PreisRegel"+Integer.toString(position), "0", null);
			
			inif.setStringProperty("ZuzahlRegeln_"+diszis[i], "ZuzahlRegel"+Integer.toString(position), "0", null);
			
			inif.setStringProperty("BerichtRegeln_"+diszis[i], "Bericht"+Integer.toString(position), "", null);
			
			inif.setStringProperty("HBRegeln_"+diszis[i], "HBPosVoll"+Integer.toString(position), "", null);
			inif.setStringProperty("HBRegeln_"+diszis[i], "HBPosMit"+Integer.toString(position), "", null);
			inif.setStringProperty("HBRegeln_"+diszis[i], "HBKilometer"+Integer.toString(position), "", null);
			inif.setStringProperty("HBRegeln_"+diszis[i], "HBPauschal"+Integer.toString(position), "", null);
			inif.setStringProperty("HBRegeln_"+diszis[i], "HBHeimMitZuZahl"+Integer.toString(position), "", null);
			
			inif.setStringProperty("HMRAbrechnung_"+diszis[i], "HMRAbrechnung"+Integer.toString(position), "0", null);
		}
		inif.save();
	}
	private void macheNeuePreistabelle(){
		String[] tabName = {"kgtarif","matarif","ertarif","lotarif","rhtarif","potarif"};
		int anzahltarife = tarife.getRowCount()+1;
		for(int i = 0; i < tabName.length; i++){
			new MachePreisListe(tabName[i]+Integer.toString(anzahltarife));
		}
	}
	class MyTarifeTableModel extends DefaultTableModel{
		   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Class<?> getColumnClass(int columnIndex) {
			 return String.class;
		}
		

	    public boolean isCellEditable(int row, int col) {
	    	
	    	return true;
	    }
		   
	}
	/**********************************************/
	/**********************************************/
	class TitelEditor extends AbstractCellEditor implements TableCellEditor{
		/**
		 * 
		 */
		private static final long serialVersionUID = 8334815830491722997L;
		Object value;
		JComponent component = new JFormattedTextField();
	   public TitelEditor(){
		   //component = new JRtaTextField("NIX",true);
		   //System.out.println("editor-Component wurde initialisiert");
		   component.addKeyListener(new KeyAdapter(){
			   public void keyPressed(KeyEvent arg0) {
					//System.out.println("********Button in KeyPressed*********");	
					if(arg0.getKeyCode()== 10){
						arg0.consume();
						stopCellEditing();
					}
			   }
		   });
	    }
		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			((JFormattedTextField)component).setText((String)value);
			((JFormattedTextField)component).setCaretPosition(0);
			return component;
		}

		@Override
		public Object getCellEditorValue() {
			// TODO Auto-generated method stub
			return  ((JFormattedTextField)component).getText();
		}


		public boolean isCellEditable(EventObject anEvent) {
			if(anEvent instanceof MouseEvent)
	          {
	             MouseEvent me = (MouseEvent)anEvent;
	             if(me.getClickCount() != 2){
	            	 return false;
	             }
	          }
			//System.out.println("isCellEditable");
			return true;
		}


		@Override
		public boolean shouldSelectCell(EventObject anEvent) {
			//System.out.println("in schouldCellSelect"+anEvent);
			return super.shouldSelectCell(anEvent);
		}

		@Override
		public boolean stopCellEditing() {
			value = ((JFormattedTextField) component).getText();
			//System.out.println("in stopCellediting");
			super.stopCellEditing();
			return true;
		}
		public boolean startCellEditing() {
	        return false;//super.startCellEditing();//false;
		}

		
	}

	class TarifeListSelectionHandler implements ListSelectionListener {

	    public void valueChanged(ListSelectionEvent e) {
	        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
	        
	        //int firstIndex = e.getFirstIndex();
	        //int lastIndex = e.getLastIndex();
	        boolean isAdjusting = e.getValueIsAdjusting();
	        if(isAdjusting){
	        	return;
	        }
			//StringBuffer output = new StringBuffer();
	        if (lsm.isSelectionEmpty()) {

	        } else {
	            int minIndex = lsm.getMinSelectionIndex();
	            int maxIndex = lsm.getMaxSelectionIndex();
	            for (int i = minIndex; i <= maxIndex; i++) {
	                if (lsm.isSelectedIndex(i)) {
	                	lbltgruppe.setText(tarife.getValueAt(i, 0).toString());
	                    break;
	                }
	            }
	        }
	    }
	}

	


}
