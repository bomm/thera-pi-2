package systemEinstellungen;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import jxTableTools.DblCellEditor;
import jxTableTools.DoubleTableCellRenderer;
import jxTableTools.MitteRenderer;
import jxTableTools.TableTool;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import sqlTools.SqlInfo;
import systemTools.JCompTools;
import systemTools.JRtaCheckBox;
import systemTools.JRtaComboBox;
import systemTools.JRtaRadioButton;
import systemTools.JRtaTextField;
import terminKalender.DatFunk;
import terminKalender.ParameterLaden;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SysUtilPreislisten extends JXPanel implements KeyListener, ActionListener {
	
	JRtaComboBox[] jcmb = {null,null,null,null};
	JRtaTextField gueltig = null;
	JButton[] button = {null,null,null,null,null,null};
	JButton plServer = null;
	int kurztext = 1;
	
	JXTable preislisten = null;
	MyPreislistenTableModel modpreis = new MyPreislistenTableModel();
	JRtaRadioButton[] jradio = {null,null,null};
	ButtonGroup jradiogroup = new ButtonGroup();

	
	JXTable plserver = null;
	MyServerTableModel modserver = new MyServerTableModel();
	
	JPanel pledit = null;
	JPanel plupdate = null;
	
	JButton plEinlesen = null;
	
	// neue Elemente:
	JButton posneu = null;
	JButton posdel = null;
	JButton speichern = null;
	JButton abbruch = null;
	JButton zurueck = null;
	JButton ueber = null;
	JRtaCheckBox bezeich = null;
	JRtaCheckBox neuaufalt = null;
	Vector<String> delvec = new Vector<String>();
	String[] dbtarife = {"kgtarif","matarif","ertarif","lotarif","rhtarif"};	

	String[] disziplin = {"Physio","Massage","Ergo","Logo","Reha"};
 	JRtaComboBox kuerzelcombo = new JRtaComboBox();
	KeyListener kl = null;
	
	public SysUtilPreislisten(){
		super(new BorderLayout());
		//System.out.println("Aufruf SysUtilPreislisten");
		this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 20));
		/****/
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("SystemInit"));
		/****/
	     pledit = getVorlagenSeite();
	     add(pledit,BorderLayout.CENTER);

	     new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
				plupdate = getPlupdate();

				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}
	    	 
	     }.execute();

	}
	/************** Beginn der Methode für die Objekterstellung und -platzierung *********/
	private JPanel getVorlagenSeite(){
		try{
        //                                      1.            2.    3.    4.     5.     6.    7.      8.     9.
		FormLayout lay = new FormLayout("right:max(60dlu;p), 4dlu, 70dlu, 4dlu, 70dlu, 4dlu, 10dlu, 4dlu, 70dlu",
       //1.    2. 3.   4.   5.   6.  7.  8.    9.  10.  11. 12. 13.  14.  15. 16.  17. 18.  19.   20.    21.   22.   23.
		"p, 2dlu, p, 2dlu,  p,10dlu, p, 10dlu,130dlu, 5dlu, p,5dlu,p");
		
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		builder.addLabel("Heilmittelart auswählen",cc.xy(1, 1));

		jcmb[0] = new JRtaComboBox(SystemConfig.rezeptKlassen);
		jcmb[0].setSelectedItem(SystemConfig.initRezeptKlasse);
		jcmb[0].setActionCommand("tabelleRegeln");
		jcmb[0].addActionListener(this);
		builder.add(jcmb[0],cc.xyw(3,1,7));
		
		String[] xkuerzel = {"KG","MA","ER","LO","RH"};
		Vector<String> xvec = SqlInfo.holeFeld("select kuerzel from kuerzel where disziplin='"+xkuerzel[0]+"'" );
		if(xvec.size() > 0){
			//System.out.println(xvec);
			kuerzelcombo.setDataVector(xvec);
		}

		
		builder.addLabel("Tarifgruppe auswählen",cc.xy(1, 3));
		//jcmb[1] = new JRtaComboBox(SystemConfig.vPreisGruppen);
		jcmb[1] = new JRtaComboBox(SystemPreislisten.hmPreisGruppen.get(disziplin[jcmb[0].getSelectedIndex()]));
		jcmb[1].setActionCommand("tabelleRegeln");
		jcmb[1].addActionListener(this);
		builder.add(jcmb[1],cc.xyw(3,3,7));

		builder.addLabel("gültig ab",cc.xy(1,5));
		gueltig = new JRtaTextField("DATUM",true);
		//gueltig.setText(SystemConfig.vNeuePreiseAb.get(jcmb[0].getSelectedIndex()).get(jcmb[1].getSelectedIndex()));
		gueltig.setText(SystemPreislisten.hmNeuePreiseAb.get(disziplin[jcmb[0].getSelectedIndex()]).get(jcmb[1].getSelectedIndex()));
		builder.add(gueltig, cc.xy(3,5));

		builder.addLabel("Anwendungsregel",cc.xyw(4,5,4,CellConstraints.RIGHT,CellConstraints.CENTER));
		String[] zzart = new String[] {"nicht relevant","erste Behandlung >=","Rezeptdatum >=","beliebige Behandlung >=","Rezept splitten"};
		jcmb[2] = new JRtaComboBox(zzart);
		
		//int einstellung = ((Integer) ((Vector)SystemConfig.vNeuePreiseRegel.get(jcmb[0].getSelectedIndex())).get( jcmb[1].getSelectedIndex()) );
		int einstellung = ((Integer) SystemPreislisten.hmNeuePreiseRegel.get(disziplin[jcmb[0].getSelectedIndex()]).get(jcmb[1].getSelectedIndex()));
		jcmb[2].setSelectedIndex(einstellung);
		
		builder.add(jcmb[2],cc.xy(9,5));
		
		plServer = new JButton("Update der Preise über Preislistenserver");
		plServer.setIcon(SystemConfig.hmSysIcons.get("achtung"));
		plServer.setActionCommand("plUpdate");
		plServer.addActionListener(this);
		builder.add(plServer,cc.xyw(3,7,7));
		
		modpreis.setColumnIdentifiers(new String[] {"HM-Pos.","Kurzbez.","Langtext","aktuell","alt","id"});
		preislisten = new JXTable(modpreis);
		preislisten.getColumn(0).setMaxWidth(65);
		preislisten.getColumn(1).setMaxWidth(85);
		preislisten.getColumn(1).setCellEditor(new DefaultCellEditor(kuerzelcombo));
		preislisten.getColumn(3).setCellRenderer(new DoubleTableCellRenderer());
		preislisten.getColumn(3).setCellEditor(new DblCellEditor());
		preislisten.getColumn(3).setMaxWidth(50);
		preislisten.getColumn(4).setCellRenderer(new DoubleTableCellRenderer());
		preislisten.getColumn(4).setCellEditor(new DblCellEditor());
		preislisten.getColumn(4).setMaxWidth(50);
		preislisten.getColumn(5).setMinWidth(0);
		preislisten.getColumn(5).setMaxWidth(50);
		preislisten.getColumn(5).setCellRenderer(new MitteRenderer());
		preislisten.setSortable(false);
		JScrollPane jscr = JCompTools.getTransparentScrollPane(preislisten);
		
		jscr.validate();
		new SwingWorker(){
			@Override
			protected Object doInBackground() throws Exception {
				tabelleRegeln();
				return null;
			}
		}.execute();
		
		builder.add(jscr,cc.xyw(1,9,9));
		
		posneu = new JButton("hinzufügen");
		posneu.setActionCommand("hinzu");
		posneu.addActionListener(this);
		posdel = new JButton("entfernen");
		posdel.setActionCommand("entfernen");
		posdel.addActionListener(this);
		speichern = new JButton("speichern");
		speichern.setActionCommand("speichern");
		speichern.addActionListener(this);
		abbruch = new JButton("abbrechen");
		abbruch.setActionCommand("abbruch");
		abbruch.addActionListener(this);
		
		builder.addLabel("Position in Liste aufnehmen/entfernen",cc.xyw(1, 11,3));
		builder.add(posneu, cc.xy(5, 11));
		builder.add(posdel,cc.xy(9, 11));
		builder.addLabel("Anderungen speichern?",cc.xyw(1,13,3));
		builder.add(speichern, cc.xy(5, 13));

		builder.add(abbruch, cc.xy(9, 13));
		
		return builder.getPanel();
		}catch(Exception ex){
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null,"Fehler bei der Erstellung des Combo-Box-Panels");
		}
		return null;
	}
	private void fuelleMitWerten(){
		int aktiv;
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/rezept.ini");
		for(int i = 0;i < 5;i++){
			aktiv = inif.getIntegerProperty("RezeptKlassen", "KlasseAktiv"+new Integer(i+1).toString());
			if(aktiv > 0){
				//heilmittel[i].setSelected(true);
			}else{
				//heilmittel[i].setSelected(false);
			}
			
		}
		jcmb[0].setSelectedItem(SystemConfig.initRezeptKlasse);
	}	
	
	@Override
	public void keyPressed(KeyEvent e) {
		String name = ((JComponent)e.getSource()).getName();
		if(name != null){
			//System.out.println("Listener des Panels ----> TastaturEvent von "+name+" ausgelöst. Gedrückte Taste = "+e.getKeyChar());
		}
		
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
		String cmd = e.getActionCommand();
		if(cmd.equals("tabelleRegeln")){
			delvec.clear();
			tabelleRegeln();
			//int einstellung = ((Integer) ((Vector)SystemConfig.vNeuePreiseRegel.get(jcmb[0].getSelectedIndex())).get( jcmb[1].getSelectedIndex()) );
			int einstellung = ((Integer) SystemPreislisten.hmNeuePreiseRegel.get(disziplin[jcmb[0].getSelectedIndex()]).get(jcmb[1].getSelectedIndex()));
			jcmb[2].setSelectedIndex(einstellung);
			String[] xkuerzel = {"KG","MA","ER","LO","RH"};
			kuerzelcombo.setDataVector(SqlInfo.holeFeld("select kuerzel from kuerzel where disziplin='"+xkuerzel[jcmb[0].getSelectedIndex()]+"'" ));
		}
		if(cmd.equals("plUpdate")){
			SwingUtilities.invokeLater(new Runnable(){
			 	   public  void run(){
						String[] lists = {"Physio","Massage","Ergo","Logo","REHA"};
						plEinlesen.setText("<html>Verfügbare Preislisten für <b><font color='#ff0000'>"+lists[jcmb[0].getSelectedIndex()]+"</font></b> ermitteln");
						jcmb[3].removeAllItems();
						jcmb[3].addItem((String) jcmb[1].getSelectedItem());
						jcmb[3].setEnabled(false);
						modserver.setRowCount(0);
						plserver.validate();
						remove(pledit);
						plupdate.validate();
						add(plupdate,BorderLayout.CENTER);
						validate();
						repaint();
			 	   }
			});
			
		}
		if(cmd.equals("zurueck")){
			doZurueck();
		}
		if(cmd.equals("pleinlesen")){
			String[] lists = {"Physio","Massage","Ergo","Logo","Reha"};
			testeAllepreise(lists[jcmb[0].getSelectedIndex()]);
		}
		if(cmd.equals("plwahl")){
		}
		if(cmd.equals("hinzu")){
			//neue Position in lokaler Liste
			int row = preislisten.getRowCount();
			Vector<Object> nvec = new Vector<Object>();
			nvec.add((String)"00000");
			nvec.add( (String)"KurzNeu-"+Integer.toString(kurztext));
			kurztext++;
			nvec.add("");
			nvec.add(new Double("0.00"));
			nvec.add(new Double("0.00"));
			nvec.add("-1");
			modpreis.addRow((Vector) nvec.clone());
			preislisten.validate();
			preislisten.scrollRowToVisible(row);
			preislisten.setRowSelectionInterval(row, row);
		}
		if(cmd.equals("entfernen")){
			//Position in lokaler Liste l�schen
			String msg = "Wenn Sie eine bestehende Position aus der Preisliste löschen werden evtl.\n"+
			"Rezepte in der Historie nicht mehr korrekt dargestellt!\n\n"+
			"Wollen Sie das ausgewählte Heilmittel wirklich aus der Preisliste löschen?\n";
			int frage = JOptionPane.showConfirmDialog(null,msg, "Achtung - wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
			if(frage != JOptionPane.YES_OPTION){
				return;
			}
			int row = preislisten.getSelectedRow();
			if(row < 0){
				return;
			}
			String sid = (String) preislisten.getValueAt(row,5);
			if(!sid.equals("-1")){
				delvec.add(sid);
			}
			TableTool.loescheRow(preislisten, row);
		}
		if(cmd.equals("speichern")){
			//Position in lokaler Liste l�schen
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					try{
						doSpeichern();
						/*
						if(Reha.demoversion){
							JOptionPane.showMessageDialog(null, "Die Funktion -> Preisliste speichern <- ist in der Entwicklungsversion von Thera-Pi deaktiviert!");
							return null;
						}else{
														
						}
						*/
					}catch(Exception ex){
						ex.printStackTrace();
					}
					return null;
				}
			}.execute();

		}
		if(cmd.equals("abbruch")){
			//Position in lokaler Liste l�schen
			SystemUtil.abbrechen();
			SystemUtil.thisClass.parameterScroll.requestFocus();
		}
		if(cmd.equals("uebernehmen")){
			//Positionen der Tabelle �bernehmen
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					try{
						doUebernahme();	
					}catch(Exception ex){
						ex.printStackTrace();
					}
					return null;
				}
				
			}.execute();

		}


	}
	private void doSpeichern(){
		// in die Datenbank schreiben
		// in den Vector schreiben
		// Gültig ab erneuern
		// Anwendungsregel erneuern
		//{"HM-Pos.","Kurzbez.","Langtext","aktuell","alt",""});
		try{
			Reha.thisClass.Rehaprogress.setIndeterminate(true);
			int anzahl = modpreis.getRowCount();
			String hmpos,kurz,lang,akt,alt,sid;
			String sdb = dbtarife[jcmb[0].getSelectedIndex()];
			String gruppe = new Integer(jcmb[1].getSelectedIndex()+1).toString();
			int igruppe = jcmb[1].getSelectedIndex()+1;
			String cmd;
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			Reha.thisClass.progressStarten(true);
			for(int i = 0; i < anzahl; i++){
				hmpos = (String)modpreis.getValueAt(i,0);
				kurz = (String)modpreis.getValueAt(i,1);
				lang = (String)modpreis.getValueAt(i,2);
				akt = new Double((Double)modpreis.getValueAt(i,3)).toString().replaceAll(",", ".");
				alt = new Double((Double)modpreis.getValueAt(i,4)).toString().replaceAll(",", ".");
				sid = (String)modpreis.getValueAt(i,5);
				if(sid.equals("-1")){
					// neu
					cmd = "insert into "+sdb+Integer.toString(igruppe)+" set leistung='"+lang+"', kuerzel='"+kurz+"', T_POS='"+
					hmpos+"', T_AKT='"+akt+"', T_ALT='"+alt+"', T_PROZ='0.00'";
					////System.out.println(cmd);
					SqlInfo.sqlAusfuehren(cmd);
					
				}else{
					// bestehend
					cmd = "update "+sdb+Integer.toString(igruppe)+" set leistung='"+lang+"', kuerzel='"+kurz+"', T_POS='"+
					hmpos+"', T_AKT='"+akt+"', T_ALT='"+alt+"', T_PROZ='0.00' where id='"+
					sid+"'";
					SqlInfo.sqlAusfuehren(cmd);
				}

			}
			Reha.thisClass.progressStarten(true);
			String xgueltig = gueltig.getText();
			int regel = jcmb[2].getSelectedIndex();
			if(xgueltig.trim().equals(".  .") || xgueltig.trim().equals("") ){
				xgueltig = "";
			}
			
			String[] diszis = {"Physio","Massage","Ergo","Logo","Reha"};
			String dis = diszis[jcmb[0].getSelectedIndex()]; 
			int diswelche = jcmb[1].getSelectedIndex()+1;

			//((Vector)SystemConfig.vNeuePreiseAb.get(jcmb[0].getSelectedIndex())).set(jcmb[1].getSelectedIndex(), xgueltig);
			//((Vector)SystemConfig.vNeuePreiseRegel.get(jcmb[0].getSelectedIndex())).set(jcmb[1].getSelectedIndex(), jcmb[2].getSelectedIndex());
			SystemPreislisten.hmNeuePreiseAb.get(diszis[jcmb[0].getSelectedIndex()]).set(jcmb[1].getSelectedIndex(),xgueltig);
			SystemPreislisten.hmNeuePreiseRegel.get(diszis[jcmb[0].getSelectedIndex()]).set(jcmb[1].getSelectedIndex(),jcmb[2].getSelectedIndex());

			INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/preisgruppen.ini");
			inif.setStringProperty("PreisRegeln_"+diszis[jcmb[0].getSelectedIndex()], "PreisAb"+(diswelche),xgueltig , null);
			inif.setIntegerProperty("PreisRegeln_"+diszis[jcmb[0].getSelectedIndex()], "PreisRegel"+(diswelche),regel , null);
			inif.save();
			if(delvec.size()>0){
				for(int i = 0;i < delvec.size();i++){
					cmd = "delete from "+sdb+" where id='"+delvec.get(i)+"'";
					SqlInfo.sqlAusfuehren(cmd);
					////System.out.println("Löschen mit Kommando = "+cmd);
				}
			}
			//String[] diszi = {"KG","MA","ER","LO","RH"};
			//ParameterLaden.PreiseEinlesen(diszi[jcmb[0].getSelectedIndex()]);
			SystemPreislisten.ladePreise(diszis[jcmb[0].getSelectedIndex()]);
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			Reha.thisClass.Rehaprogress.setIndeterminate(false);
			JOptionPane.showMessageDialog(null,"Preisliste wurde erfolgreich gespeichert");
		}catch(Exception ex){
			ex.printStackTrace();
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			Reha.thisClass.Rehaprogress.setIndeterminate(false);
			JOptionPane.showMessageDialog(null,"Fehler beim Abspeichern der Preisliste");
		}
	}
	private void doZurueck(){
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run(){
					remove(plupdate);
					pledit.validate();
					add(pledit,BorderLayout.CENTER);
					validate();
					repaint();						
		 	   }
		});
	}
	private void doUebernahme(){
		try{
			if(modserver.getRowCount()<=0){
				JOptionPane.showMessageDialog(null, "1. Schritt: Verfügbare Preislisten ermitteln\n"+
						"2. Schritt: Gewünschte Preisliste in der Tabelle auswählen\n"+
						"3. Schritt: Die ausgewählte Preisliste übernehmen - aber eben erst im 3. Schritt!");
				// dummer Spruch
				return;
			}
			int row = plserver.getSelectedRow();
			if(row < 0){
				JOptionPane.showMessageDialog(null, "1. Schritt: Verfügbare Preislisten ermitteln\n"+
						"2. Schritt: Gewünschte Preisliste in der Tabelle auswählen\n"+
						"3. Schritt: Die ausgewählte Preisliste übernehmen - aber eben erst im 3. Schritt!");
				// dummer Spruch			
				return;
			}
			String[] lists = {"Physio","Massage","Ergo","Logo","REHA"};
			String msg = "<html><b><font color='#ff0000' size=+2>Bitte sorgfältig lesen!!!!</font></b><br><br><br>"+
			"Die von Ihnen ausgewählte Disziplin ist: <b><font color='#ff0000'> "+lists[jcmb[0].getSelectedIndex()]+"</font></b><br><br>"+
			"Die von Ihnen ausgewählte Tarifgruppe ist: <b><font color='#ff0000'> "+(String)jcmb[1].getSelectedItem()+"</font></b><br><br>"+
			"In die o.g. Tarifgruppe werden die Preise übernommen von:<br>"+
			"Preisliste: <b><font color='#ff0000'>"+plserver.getValueAt(row, 1)+"</font></b><br>"+
			"Gültigkeitsbereich: <b><font color='#ff0000'>"+plserver.getValueAt(row, 2)+"</font></b><br><br><br>"+
			"Wollen Sie den Preislisten-Import mit diesen Einstellungen durchführen<br><br></html>";
			int frage = JOptionPane.showConfirmDialog(null,msg,"Achtung absolut wichtige Benutzeranfrage",JOptionPane.YES_NO_OPTION);
			if(frage != JOptionPane.YES_OPTION){
				return;
			}
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			Reha.thisClass.Rehaprogress.setIndeterminate(true);
			if(neuaufalt.isSelected()){
				doSetzeNeuAufAlt();			
			}
			Vector preis = doHolePreiseNeu();
			setzePreise(preis);
			doZurueck();
			Reha.thisClass.Rehaprogress.setIndeterminate(false);
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}catch(Exception ex){
			ex.printStackTrace();
			Reha.thisClass.Rehaprogress.setIndeterminate(false);
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			JOptionPane.showMessageDialog(null, "Fehler beim Preislistenimport");
		}
	}
	private void setzePreise(Vector preis){
		Vector tab = modpreis.getDataVector();
		//System.out.println(preis);
		boolean mitbezeich = bezeich.isSelected();
		String posnr;
		int bisheranzahl;
		boolean getroffen = false;
		for(int i = 0; i < preis.size();i++){
			bisheranzahl = this.modpreis.getRowCount();
			posnr = ((String)((Vector)preis.get(i)).get(0)).trim();
			getroffen = false;
			for(int b = 0; b < bisheranzahl; b++){
				if( ((String)((Vector)tab.get(b)).get(0)).trim().equals(posnr.trim())){
					getroffen = true;
					modpreis.setValueAt(Double.parseDouble(((String)((Vector)preis.get(i)).get(1))), b, 3);
					if(mitbezeich){
						modpreis.setValueAt(((String)((Vector)preis.get(i)).get(3)), b, 2);						
					}
				}
			}
			if(!getroffen){
				preisAufnahme(getroffen,preis,i);
				getroffen = true;;
			}

		}
		if(preis.size()> 0){
			int aktrow = plserver.getSelectedRow(); 
			String datum = (String)plserver.getValueAt(aktrow,3);
			gueltig.setText(datum);			
		}
 
		
	}
	private void preisAufnahme(boolean getroffen,Vector preis,int i){
		if(!getroffen && (jradio[1].isSelected() || jradio[2].isSelected())){
			if(jradio[1].isSelected()){
				String msg = "<html>Die Position <b><font color='#ff0000'>"+((String)((Vector)preis.get(i)).get(0))+"</font></b> mit dem Langtext<br>"+"<b><font color='#ff0000'>"+
				((String)((Vector)preis.get(i)).get(3))+"<br></font></b>ist in Ihrer Preisliste nicht vorhanden."+
				"<br><br>Soll die Position in Ihre Preisliste aufgenommen werden?<br><br></html>";
				int frage = JOptionPane.showConfirmDialog(null,msg,"Achtung wichtige Benutzeranfrage",JOptionPane.YES_NO_OPTION );
				if(frage == JOptionPane.YES_OPTION){
					Vector xvec = new Vector();
					xvec.add( ((String)((Vector)preis.get(i)).get(0)) );
					xvec.add( "Neu-"+Integer.toString(kurztext) );
					kurztext++;
					xvec.add( ((String)((Vector)preis.get(i)).get(3)) );
					xvec.add( Double.parseDouble( ((String)((Vector)preis.get(i)).get(1)) ) );
					xvec.add( Double.parseDouble( ((String)((Vector)preis.get(i)).get(1)) ) );
					xvec.add("-1" );						
					modpreis.addRow(xvec);
					return;
				}else{
					return;
				}
			}else{
				Vector xvec = new Vector();
				xvec.add( ((String)((Vector)preis.get(i)).get(0)) );
				xvec.add( "Neu-"+Integer.toString(kurztext)  );
				kurztext++;
				xvec.add( ((String)((Vector)preis.get(i)).get(3)) );
				xvec.add( Double.parseDouble( ((String)((Vector)preis.get(i)).get(1)) ) );
				xvec.add( Double.parseDouble( ((String)((Vector)preis.get(i)).get(1)) ) );
				xvec.add("-1" );						
				modpreis.addRow(xvec);
			}
		}
		
	}
	
	private Vector doHolePreiseNeu(){
		int row = plserver.getSelectedRow();
		String hmsparte = (String) plserver.getValueAt(row, 0);
		String preisgruppe = (String) plserver.getValueAt(row, 1);
		String bundesland = (String) plserver.getValueAt(row, 2);
		if(bundesland.equals("bundesweit")){
			bundesland = "./.";
		}
		String cmd = null;

		cmd = "select posnr,preis,gueltigab,langtext from allepreise where disziplin='"+
		hmsparte+"' AND buland='"+bundesland+"' AND preisgruppe='"+preisgruppe+"'"; 
		
		Vector vec =  SqlInfo.holeFelder(cmd);
		return (Vector) vec.clone();
		
	}
	private void doSetzeNeuAufAlt(){
		int anzahl = modpreis.getRowCount();
		double wert;
		for(int i = 0; i < anzahl;i++){
			wert = (Double)modpreis.getValueAt(i, 3);
			modpreis.setValueAt(wert, i, 4);
		}
	}
	
	private void testeAllepreise(String disziplin){
		List<String> pbundesweit =  Arrays.asList(new String[] {"VdEK","PBeaKK","BG","Beihilfe"});
		Vector vec1 = null;
		Vector vec2 = new Vector();
		String testbuland = "";
		String testpg = "";
		String buland = "";
		String preisgr = "";
		String gueltig = "";
		Vector vbuland = new Vector();
		Vector vpreisgruppe = new Vector();
		modserver.setRowCount(0);
		vec1 = SqlInfo.holeFelder("select buland,preisgruppe,gueltigab from allepreise where disziplin='"+disziplin+"' ORDER BY buland,preisgruppe");
		if(vec1.size()<= 0){
				JOptionPane.showMessageDialog(null,"Bislang sind für -> "+disziplin+" <- keine Preislisten auf dem Server hinterlegt");
		}else{
				////System.out.println("Gr��e des Vectors = "+vec1.size());
				//buland = ((String)((Vector)vec1.get(0)).get(0)).trim();
				//preisgr = ((String)((Vector)vec1.get(0)).get(1)).trim();
				//vbuland.add(buland);
				//vpreisgruppe.add(preisgr);
				int anzahl = vec1.size();
				for(int y = 0; y < anzahl;y++){
					testbuland  = ((String)((Vector)vec1.get(y)).get(0)).trim();
					testpg  = ((String)((Vector)vec1.get(y)).get(1)).trim();
					vec2.clear();
					if( (!vbuland.contains(testbuland)) || (!vpreisgruppe.contains(testpg))){
						buland = ((String)((Vector)vec1.get(y)).get(0)).trim();
						preisgr = ((String)((Vector)vec1.get(y)).get(1)).trim();
						
						vec2.add(disziplin);
						vec2.add(preisgr);
						vec2.add( (pbundesweit.contains(preisgr) ? "bundesweit" : buland)  );
						try{
							gueltig = DatFunk.sDatInDeutsch( ((String)((Vector)vec1.get(y)).get(2)).trim() );
							vec2.add( gueltig );
						}catch(Exception ex){
							vec2.add(DatFunk.sHeute());
						}
						modserver.addRow((Vector)vec2.clone());
						vbuland.add(buland);
						vpreisgruppe.add(preisgr);
						
					}
				}
				plserver.setRowSelectionInterval(0,0);
			}
		
		
		return;
	}
	
	public JPanel getPlupdate(){        //    1              2      3      4     5     6      7     8      9
		FormLayout lay = new FormLayout("right:max(60dlu;p), 4dlu, 7dlu, 4dlu, 70dlu, 4dlu,  0dlu, 4dlu, 70dlu",
			       //1.    2. 3.   4.   5.     6.   7.  8. 9.10. 11.12. 13. 14.15. 16.17. 18. 19.   20.    21.   22.   23.
					"p, 2dlu, p, 2dlu, 100dlu,10dlu,p,2dlu,p,5dlu,p,2dlu,p,2dlu,p,2dlu,p,5dlu,p");
					
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		plEinlesen = new JButton("Verfügbare Preislisten einlesen");
		plEinlesen.setActionCommand("pleinlesen");
		plEinlesen.addActionListener(this);
		builder.add(plEinlesen,cc.xyw(3,1,7));
		builder.addLabel("Übernahme auf",cc.xy(1,3));
		jcmb[3] = new JRtaComboBox();
		jcmb[3].setActionCommand("plwahl");
		jcmb[3].addActionListener(this);
		builder.add(jcmb[3],cc.xyw(3,3,7));
		modserver.setColumnIdentifiers(new String[] {"HM-Sparte","Preisgruppe","Bundesland","gueltig ab"});
		plserver = new JXTable(modserver);
		JScrollPane jscr = JCompTools.getTransparentScrollPane(plserver);
		jscr.validate();
		/*
		new SwingWorker(){
			@Override
			protected Object doInBackground() throws Exception {
				tabelleRegeln();
				return null;
			}
		}.execute();
		*/
		builder.add(jscr,cc.xyw(1,5,9));
		
		bezeich = new JRtaCheckBox();
		builder.addLabel("Langtext-Bezeichnungen vom Preislistenserver übernehmen?",cc.xyw(1, 7, 8));
		builder.add(bezeich,cc.xy(9, 7,CellConstraints.RIGHT,CellConstraints.BOTTOM));
		builder.addLabel("Bisher aktuelle Preise auf 'Alte-Preise' übertragen?",cc.xyw(1, 9, 8));
		neuaufalt = new JRtaCheckBox();
		neuaufalt.setSelected(true);
		builder.add(neuaufalt,cc.xy(9, 9,CellConstraints.RIGHT,CellConstraints.BOTTOM));
		//jradiogroup
		jradio[0] = new JRtaRadioButton("nicht hinzufügen");
		jradio[0].setHorizontalTextPosition(SwingConstants.LEFT);
		jradiogroup.add(jradio[0]);
		jradio[1] = new JRtaRadioButton("vorher nachfragen");
		jradio[1].setHorizontalTextPosition(SwingConstants.LEFT);		
		jradiogroup.add(jradio[1]);
		jradio[2] = new JRtaRadioButton("automat. hinzufügen");
		jradio[2].setHorizontalTextPosition(SwingConstants.LEFT);
		jradiogroup.add(jradio[2]);
		jradio[0].setSelected(true);
		
		builder.addLabel("Wenn sich in der Datenbank neue Positionen befinden",cc.xy(1, 11));
		builder.add(jradio[0],cc.xyw(5, 11, 5,CellConstraints.RIGHT,CellConstraints.CENTER));
		builder.add(jradio[1],cc.xyw(5, 13, 5,CellConstraints.RIGHT,CellConstraints.CENTER));
		builder.add(jradio[2],cc.xyw(5, 15, 5,CellConstraints.RIGHT,CellConstraints.CENTER));
		
		ueber = new JButton("übernehmen");
		ueber.setActionCommand("uebernehmen");
		ueber.addActionListener(this);
		zurueck = new JButton("zurueck");
		zurueck.setActionCommand("zurueck");
		zurueck.addActionListener(this);
		builder.addLabel("übernehmen?",cc.xy(1, 19));
		builder.add(ueber,cc.xy(5,19));
		builder.addLabel("Abbruch?", cc.xy(7, 19));
		builder.add(zurueck,cc.xy(9,19));
		
		return builder.getPanel();
		
		
	}
	
	public void tabelleRegeln(){

		String spreisart = Integer.toString(jcmb[1].getSelectedIndex() +1);
		Vector preisvec = holePreisVec();
		int ipreis = jcmb[1].getSelectedIndex()+1;
		int anzahl = preisvec.size();
		modpreis.setRowCount(0);
		String[] diszi = {"Physio","Massage","Ergo","Logo","Reha"};
		//String disziplin = jcmb[0].getSelectedItem().toString();
		int preisgruppe = jcmb[1].getSelectedIndex();
		////System.out.println("Preisvec = "+preisvec);
		Vector vec = new Vector();
		int idpos = 0;
		if(anzahl > 0){
			idpos = ((Vector)preisvec.get(0)).size()-1;
		}
		for(int i = 0;i < anzahl ; i++){
			vec.clear();
			vec.add( (String) ((Vector)preisvec.get(i)).get(2));
			vec.add((String)((Vector)preisvec.get(i)).get(1));
			vec.add((String)((Vector)preisvec.get(i)).get(0));
			try{
				vec.add(Double.parseDouble( (String) ((Vector)preisvec.get(i)).get( 3) ) );
			}catch(Exception ex){
				vec.add(Double.parseDouble("0.00"));
			}
			try{
				vec.add(Double.parseDouble(  (String) ((Vector)preisvec.get(i)).get( 4) ) );
			}catch(Exception ex){
				vec.add(Double.parseDouble("0.00"));
			}
			vec.add( (String) ((Vector)preisvec.get(i)).get(idpos) );
			modpreis.addRow((Vector)vec.clone());
		}
		
		if(SystemPreislisten.hmNeuePreiseAb.get(diszi[preisgruppe]).get(preisgruppe).equals("")){
			gueltig.setText("  .  .    ");
			//System.out.println("Gültigkeitsdatum nicht angegeben");
		}else{
			gueltig.setText(SystemPreislisten.hmNeuePreiseAb.get(diszi[preisgruppe]).get(preisgruppe));			
		}
		

		preislisten.validate();
		
		
		
	}
 
	private Vector holePreisVec(){
		try{
		int pgs = jcmb[0].getSelectedIndex();
		int pgGruppe = jcmb[1].getSelectedIndex();
		String[] diszi = {"Physio","Massage","Ergo","Logo","Reha"};
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		return SystemPreislisten.hmPreise.get(diszi[pgs]).get(pgGruppe);
		}catch(Exception ex){
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null,"Fehler beim Bezug der Preisliste");
		}
		return null;
	}
/*****************vor Ende Klassenklammer*************/	
}



class MyPreislistenTableModel extends DefaultTableModel{
	   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Class<?> getColumnClass(int columnIndex) {
		if(columnIndex==3 || columnIndex==4){
			return Double.class;
		}
		   return String.class;
    }

	public boolean isCellEditable(int row, int col) {
		if(col == 5){
			return false;
		}
		return true;
	}
	   
}
class MyServerTableModel extends DefaultTableModel{
	   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Class<?> getColumnClass(int columnIndex) {
		return String.class;

 }

	public boolean isCellEditable(int row, int col) {
		return false;
	}
	   
}

