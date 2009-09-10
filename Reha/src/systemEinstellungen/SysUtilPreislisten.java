package systemEinstellungen;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import jxTableTools.DblCellEditor;
import jxTableTools.DoubleTableCellRenderer;
import jxTableTools.TableTool;
import kvKarte.KVKWrapper;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import patientenFenster.AktuelleRezepte;

import sqlTools.SqlInfo;
import systemEinstellungen.SysUtilKrankenkasse.MyVorlagenTableModel;
import systemTools.JCompTools;
import systemTools.JRtaCheckBox;
import systemTools.JRtaComboBox;
import systemTools.JRtaRadioButton;
import systemTools.JRtaTextField;
import systemTools.PreisUpdate;
import terminKalender.ParameterLaden;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SysUtilPreislisten extends JXPanel implements KeyListener, ActionListener {
	
	JRtaComboBox[] jcmb = {null,null,null,null};
	JRtaTextField gueltig = null;
	JButton[] button = {null,null,null,null,null,null};
	JButton plServer = null;

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
	
	
	public SysUtilPreislisten(){
		super(new BorderLayout());
		System.out.println("Aufruf SysUtilPreislisten");
		this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 20));
		/****/
	     Point2D start = new Point2D.Float(0, 0);
	     Point2D end = new Point2D.Float(400,500);
	     float[] dist = {0.0f, 0.5f};
	     Color[] colors = {Color.WHITE,getBackground()};
	     LinearGradientPaint p =
	         new LinearGradientPaint(start, end, dist, colors);
	     MattePainter mp = new MattePainter(p);
	     setBackgroundPainter(new CompoundPainter(mp));
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
		
		builder.addLabel("Tarifgruppe auswählen",cc.xy(1, 3));
		jcmb[1] = new JRtaComboBox(SystemConfig.vPreisGruppen);
		jcmb[1].setActionCommand("tabelleRegeln");
		jcmb[1].addActionListener(this);
		builder.add(jcmb[1],cc.xyw(3,3,7));

		builder.addLabel("gültig ab",cc.xy(1,5));
		gueltig = new JRtaTextField("DATUM",true);
		gueltig.setText(SystemConfig.vNeuePreiseAb.get(jcmb[0].getSelectedIndex()).get(jcmb[1].getSelectedIndex()));
		builder.add(gueltig, cc.xy(3,5));

		builder.addLabel("Anwendungsregel",cc.xyw(4,5,4,CellConstraints.RIGHT,CellConstraints.CENTER));
		String[] zzart = new String[] {"nicht relevant","erste Behandlung >=","Rezeptdatum >=","beliebige Behandlung >=","Rezept splitten"};
		jcmb[2] = new JRtaComboBox(zzart);
		builder.add(jcmb[2],cc.xy(9,5));
		
		plServer = new JButton("Update der Preise über Preislistenserver");
		plServer.setIcon(SystemConfig.hmSysIcons.get("achtung"));
		plServer.setActionCommand("plUpdate");
		plServer.addActionListener(this);
		builder.add(plServer,cc.xyw(3,7,7));
		
		modpreis.setColumnIdentifiers(new String[] {"HM-Pos.","Kurzbez.","Langtext","aktuell","alt"});
		preislisten = new JXTable(modpreis);
		preislisten.getColumn(0).setMaxWidth(65);
		preislisten.getColumn(1).setMaxWidth(65);
		preislisten.getColumn(3).setCellRenderer(new DoubleTableCellRenderer());
		preislisten.getColumn(3).setCellEditor(new DblCellEditor());
		preislisten.getColumn(3).setMaxWidth(50);
		preislisten.getColumn(4).setCellRenderer(new DoubleTableCellRenderer());
		preislisten.getColumn(4).setCellEditor(new DblCellEditor());
		preislisten.getColumn(4).setMaxWidth(50);
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
		builder.addLabel("Änderungen speichern?",cc.xyw(1,13,3));
		builder.add(speichern, cc.xy(5, 13));
		builder.add(abbruch, cc.xy(9, 13));
		
		return builder.getPanel();
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
		String cmd = e.getActionCommand();
		if(cmd.equals("tabelleRegeln")){
			tabelleRegeln();
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
		if(cmd.equals("pleinlesen")){
			String[] lists = {"Physio","Massage","Ergo","Logo","REHA"};
			testeAllepreise(lists[jcmb[0].getSelectedIndex()]);
		}
		if(cmd.equals("plwahl")){
		}
		if(cmd.equals("hinzu")){
			//neue Position in lokaler Liste
			int row = preislisten.getRowCount();
			Vector nvec = new Vector();
			nvec.add("");
			nvec.add("");
			nvec.add("");
			nvec.add(new Double("0.00"));
			nvec.add(new Double("0.00"));
			modpreis.addRow((Vector) nvec.clone());
			preislisten.scrollRowToVisible(row);
			preislisten.setRowSelectionInterval(row, row);
		}
		if(cmd.equals("entfernen")){
			//Position in lokaler Liste löschen
			String msg = "Wenn Sie eine bestehende Position aus der Preisliste löschen werden evtl.\n"+
			"Rezepte in der Historie nicht mehr korrekt dargestellt!\n\n"+
			"Wollen Sie das ausgewählte Heilmittel wirklich aus der Preisliste löschen?\n";
			int frage = JOptionPane.showConfirmDialog(null,msg, "Achtung - wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
			if(frage != JOptionPane.YES_OPTION){
				return;
			}
			int row = preislisten.getRowCount();
			TableTool.loescheRow(preislisten, row);
		}
		if(cmd.equals("speichern")){
			//Position in lokaler Liste löschen
		}
		if(cmd.equals("abbruch")){
			//Position in lokaler Liste löschen
		}
		if(cmd.equals("uebernehmen")){
			//Positionen der Tabelle übernehmen
			doUebernahme();
		}


	}
	private void doUebernahme(){
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
	}
	private void testeAllepreise(String disziplin){
		List<String> pbundesweit =  Arrays.asList(new String[] {"VdEK","PBeaKK","BG","Beihilfe"});
		Vector vec1 = null;
		Vector vec2 = new Vector();
		String testbuland = "";
		String testpg = "";
		String buland = "";
		String preisgr = "";
		modserver.setRowCount(0);
		vec1 = SqlInfo.holeFelder("select buland,preisgruppe from allepreise where disziplin='"+disziplin+"' ORDER BY buland");
		if(vec1.size()<= 0){
				JOptionPane.showMessageDialog(null,"Bislang sind für -> "+disziplin+" <- keine Preislisten auf dem Server hinterlegt");
		}else{
				System.out.println("Größe des Vectors = "+vec1.size());
				//buland = ((String)((Vector)vec1.get(0)).get(0)).trim();
				//preisgr = ((String)((Vector)vec1.get(0)).get(1)).trim();
				int anzahl = vec1.size();
				for(int y = 0; y < anzahl;y++){
					testbuland  = ((String)((Vector)vec1.get(y)).get(0)).trim();
					testpg  = ((String)((Vector)vec1.get(y)).get(1)).trim();
					if( (!testbuland.equals(buland)) || (!testpg.equals(preisgr))){
						vec2.clear();
						buland = ((String)((Vector)vec1.get(y)).get(0)).trim();
						preisgr = ((String)((Vector)vec1.get(y)).get(1)).trim();
						vec2.add(disziplin);
						vec2.add(preisgr);
						vec2.add( (pbundesweit.contains(preisgr) ? "bundesweit" : buland)  );
						modserver.addRow((Vector)vec2.clone());
					}
				}
				plserver.setRowSelectionInterval(0,0);
			}
		
		
		return;
	}
	
	public JPanel getPlupdate(){        //    1              2      3      4     5     6      7     8      9
		FormLayout lay = new FormLayout("right:max(60dlu;p), 4dlu, 7dlu, 4dlu, 70dlu, 4dlu,  0dlu, 4dlu, 70dlu",
			       //1.    2. 3.   4.   5.     6.   7.  8. 9.10. 11.12. 13. 14.15. 16.17. 18.  19.   20.    21.   22.   23.
					"p, 2dlu, p, 2dlu, 100dlu,10dlu,p,5dlu,p,2dlu,p,2dlu,p,2dlu,p,5dlu,p");
					
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
		modserver.setColumnIdentifiers(new String[] {"HM-Sparte","Preisgruppe","Bundesland"});
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
		
		builder.addLabel("Wenn sich in der Datenbank neue Positionen befinden",cc.xy(1, 9));
		builder.add(jradio[0],cc.xyw(5,  9, 5,CellConstraints.RIGHT,CellConstraints.CENTER));
		builder.add(jradio[1],cc.xyw(5, 11, 5,CellConstraints.RIGHT,CellConstraints.CENTER));
		builder.add(jradio[2],cc.xyw(5, 13, 5,CellConstraints.RIGHT,CellConstraints.CENTER));
		
		ueber = new JButton("übernehmen");
		ueber.setActionCommand("uebernehmen");
		ueber.addActionListener(this);
		zurueck = new JButton("zurueck");
		zurueck.setActionCommand("zurueck");
		zurueck.addActionListener(this);
		builder.addLabel("übernehmen?",cc.xy(1, 17));
		builder.add(ueber,cc.xy(5,17));
		builder.addLabel("Abbruch?", cc.xy(7, 17));
		builder.add(zurueck,cc.xy(9,17));
		
		return builder.getPanel();
		
		
	}
	
	public void tabelleRegeln(){
		Vector preisvec = holePreisVec();
		String spreisart = new Integer(jcmb[1].getSelectedIndex() +1).toString();
		int ipreis = jcmb[1].getSelectedIndex()+1;
		int anzahl = preisvec.size();
		modpreis.setRowCount(0);
		Vector vec = new Vector();
		for(int i = 0;i < anzahl ; i++){
			vec.clear();
			vec.add( (String) ((Vector)preisvec.get(i)).get( 2+(ipreis*4)-4) );
			vec.add((String)((Vector)preisvec.get(i)).get(1));
			vec.add((String)((Vector)preisvec.get(i)).get(0));
			try{
				vec.add(new Double( (String) ((Vector)preisvec.get(i)).get( 2+(ipreis*4)-3) ) );
			}catch(Exception ex){
				vec.add(new Double(0.00));
			}
			try{
				vec.add(new Double(  (String) ((Vector)preisvec.get(i)).get( 2+(ipreis*4)-2) ) );
			}catch(Exception ex){
				vec.add(new Double(0.00));
			}
			modpreis.addRow((Vector)vec.clone());
		}
		if(SystemConfig.vNeuePreiseAb.get(jcmb[0].getSelectedIndex()).get(jcmb[1].getSelectedIndex()).equals("")){
			gueltig.setText("  .  .    ");
			System.out.println("Gültigkeitsdatum nicht angegeben");
		}else{
			gueltig.setText(SystemConfig.vNeuePreiseAb.get(jcmb[0].getSelectedIndex()).get(jcmb[1].getSelectedIndex()));			
		}

		preislisten.validate();
		
		
		
	}
 
	private Vector holePreisVec(){
		String diszi = (String)jcmb[0].getSelectedItem();
		if(diszi.contains("REHA")){
			return ParameterLaden.vRHPreise;
		}
		if(diszi.contains("Physio")){
			return ParameterLaden.vKGPreise;
		}
		if(diszi.contains("Massage")){
			return ParameterLaden.vMAPreise;
		}		
		if(diszi.contains("Ergo")){
			return ParameterLaden.vERPreise;
		}
		if(diszi.contains("Logo")){
			return ParameterLaden.vLOPreise;
		}
		return new Vector();
	}
/*****************vor Ende Klassenklammer*************/	
}



class MyPreislistenTableModel extends DefaultTableModel{
	   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Class getColumnClass(int columnIndex) {
		if(columnIndex==3 || columnIndex==4){
			return Double.class;
		}
		   return String.class;
    }

	public boolean isCellEditable(int row, int col) {
		return true;
	}
	   
}
class MyServerTableModel extends DefaultTableModel{
	   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Class getColumnClass(int columnIndex) {
		return String.class;
		/*
		if(columnIndex==3 || columnIndex==4){
			return Double.class;
		}
		   return String.class;
		*/   
 }

	public boolean isCellEditable(int row, int col) {
		return false;
	}
	   
}
