package benutzerVerwaltung;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreePath;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.IconValues;
import org.jdesktop.swingx.renderer.MappedValue;
import org.jdesktop.swingx.renderer.StringValues;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableModel;

import rechteTools.Rechte;
import rehaInternalFrame.JBenutzerInternal;
import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemTools.ButtonTools;
import systemTools.JCompTools;
import systemTools.JRtaCheckBox;
import systemTools.JRtaComboBox;
import systemTools.JRtaTextField;
import systemTools.StringTools;
import systemTools.Verschluesseln;
import terminKalender.ParameterLaden;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.sun.star.uno.Exception;

import dialoge.AaarghHinweis;

public class BenutzerRechte extends JXPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6231533201163780445L;
	HashMap<String,String[]> rechteMap = new HashMap<String,String[]>();
	JBenutzerInternal internal = null;
	String[] hauptGruppen = {"Benutzer-Verwaltung","Patiente","Rezepte","Historie","Therapieberichte",
			"Dokumentation","Gutachten","Terminkalender","Arbeitszeitmasken","[Ru:gl]","Abrechnung / Statistik / Umsatz","System-Initialisierung",
			"Sonstige Programme und Funktionen"};

	String[] gruppe0 = {"Benutzer-Verwaltung öffnen","Benutzer anlegen/ändern/löschen","Benutzer hat alle Rechte = SuperUser"};

	String[] gruppe1 = {"anlegen","komplett ändern","nur Teile ändern:Tel,Fax,Akut,mögl. Termine","löschen",
			"Email an Patient versenden","SMS an Patient versenden","Zusatzinfos einsehen"};

	String[] gruppe2 = {"anlegen","ändern","löschen","Gebühren kassieren","Ausfallrechnung erstellen","Rezept abschließen",
			"Rezept aufschließen","Privat-/BG-Rechnung erstellen","Therapieberichte erstellen"};

	String[] gruppe3 = {"Gesamtumsatz einsehen","einzelne Behandlungstage drucken","nachträglich Therapie-Berichte erstellen/ändern"};
	
	String[] gruppe4 = {"bestehende Berichte ändern","löschen"};
	
	String[] gruppe5 = {"öffnen / einsehen","Scannen","löschen","OOorg Doku erstellen"};
	
	String[] gruppe6 = {"anlegen","ändern","löschen","Stammdaten auf neues Gutachten übertragen"};
	
	String[] gruppe7 = {"nur in leere Termine eintragen","Termine vollständig eintragen","bestehende Termine löschen","Behandlungen bestätigen","Termine gruppieren erlauben","Drag & Drop erlauben","unbelegt 1 für zukünftige Erweiterungen","unbelegt 2 für zukünftige Erweiterungen"};
	
	String[] gruppe8 = {"Masken erstellen / ändern","Masken in Kalender übertragen"};
	
	String[] gruppe9 = {"[Ru:gl] öffnen","Termine überschreiben","unbelegt 1 für zukünftige Erweiterungen","unbelegt für 2 zukünftige Erweiterungen"};
	
	String[] gruppe10 = {"Kassenabrechnung","Rehaabrechnung","Barkassen Abrechnung","Neuanmeldungen ermitteln","Umsätze von bis ermitteln","Mitarbeiterbeteiligung ermitteln",
			"Urlaub-/Überstunden ermitteln","Offene Posten / Mahnwesen","Kassenbuch anlegen / ändern",
			"unbelegt 3 für zukünftige Erweiterungen","unbelegt 4 für zukünftige Erweiterungen","unbelegt 5 für zukünftige Erweiterungen","unbelegt 6 für zukünftige Erweiterungen","unbelegt 7 für zukünftige Erweiterungen",
			"unbelegt 8 für zukünftige Erweiterungen","unbelegt 9 für zukünftige Erweiterungen","unbelegt 10 für zukünftige Erweiterungen"};
	
	String[] gruppe11 = {"Firmenangaben Mandanten","Datenbankparameter","Kalender Grundeinstellungen","Kalender-Benuter verwalten",
			"Behandlersets einstellen","Druckvorlage für Terminliste","Kalender-Farbdefinition","Gruppentermine verwalten",
			"Neues Kalenderjahr anlegen","[Ru:gl]-Grundeinstellungen","[Ru:gl]-Gruppen definieren","Einstellungen Patientenfenster",
			"Einstellungen Rezepte","Einstellungen Krankenfenster","Einstellungen Arztfenster","Emailparameter ändern","Geräteanschlüsse (Schnittstellen)",
			"angeschlossene Geräte","Behandlunskürzel verwalten","Tarifgruppen bearbeiten","Preise bearbeiten/importieren",
			"Nummernkreise verwalten","Nebraska benützen","Abrechnungsformulare und Druckparameter","Kostenträgerdatei einlesen","Fremdprogramme verwalten",
			"unbelegt 1 für zukünftige Erweiterungen","unbelegt 2 für zukünftige Erweiterungen",
			"unbelegt 3 für zukünftige Erweiterungen","unbelegt 4 für zukünftige Erweiterungen","unbelegt 5 für zukünftige Erweiterungen"};

	String[] gruppe12 = {"Verkaufsmodul benutzen","Rehaformulare verwenden","Textbausteine für Gutachten anlegen/ändern","Rezepte in Historie schieben und umgekehrt",
			"im Rezept gespeicherte Behandlungsarten löschen","Geburtstagsbriefe erstellen","unbelegt 4 für zukünftige Erweiterungen","unbelegt 5 für zukünftige Erweiterungen","unbelegt 6 für zukünftige Erweiterungen",
			"unbelegt 7 für zukünftige Erweiterungen","unbelegt 8 für zukünftige Erweiterungen","unbelegt 9 für zukünftige Erweiterungen",
			"unbelegt 10 für zukünftige Erweiterungen"};
	/*************************************/
	
	JXPanel content = null;
	JButton[] buts = {null,null,null,null,null,null,null};
	JRtaTextField[] tfs = {null};
	JPasswordField[] pws = {null,null};
	JRtaComboBox jcmb = null;
	JRtaCheckBox jchb = null;
	String aktuelleRechte = null;
	String klartextLabel = null;
	String elternTitel = null;
	private JXRechteTreeTableNode aktNode;
	private int aktRow;
	private JXRechteTreeTableNode root = null;
	private RechteTreeTableModel rechteTreeTableModel = null;
	private JXTreeTable jXTreeTable = null;
	private JXRechteTreeTableNode foo = null;
	private MyRechteComboBox comborechte = null;
	private String userid = "";
	private boolean neu = false;
	
	ActionListener al;
	KeyListener kl;
	public BenutzerRechte(JBenutzerInternal bint){
		super();
		this.internal = bint;
		elternTitel = this.internal.getTitle();
		putRechte();
		makeListeners();
		this.setLayout(new BorderLayout());
		add(getHeader(),BorderLayout.NORTH);
		add(getContent(),BorderLayout.CENTER);

		validate();
	}
	private JXHeader getHeader(){
		String ss = Reha.proghome+"icons/header-image.png";
	    JXHeader header = new JXHeader("Benutzerverwaltung",
	            "Hier legen Sie die Namen der Programmbenutzer fest. Sie können Passwörter erstellen oder ändern.\n" +
	            "Der Benutzername des eingeloggten Users erscheint später im Fenster-Titel. \n" +
	            "Darüberhinaus können Sie hier jedem Benutzer individuelle Berechtigungen für einzelne Programmteile zuweisen.",
	            new ImageIcon(ss));
	    return header;
	}
    
	private JXPanel getContent(){
		FormLayout lay = new FormLayout("fill:0:grow(0.5),fill:0:grow(0.5)","fill:0:grow(1.0)");
		CellConstraints cc = new CellConstraints();
		content = new JXPanel();
		content.setLayout(lay);
		content.add(getButtonTeil(),cc.xy(1,1));
		content.add(getTreeTableTeil(),cc.xy(2,1));
		content.validate();
		return content;
	}
	private JScrollPane getTreeTableTeil(){

			comborechte = new MyRechteComboBox();
			/*
			JLabel lab = new JLabel();
    		lab.setIcon(SystemConfig.hmSysIcons.get("zuzahlnichtok"));
			comborechte.component.addItem(lab);
    		lab = new JLabel();
    		lab.setIcon(SystemConfig.hmSysIcons.get("zuzahlok"));
			comborechte.component.addItem(lab);
			*/

			root = new JXRechteTreeTableNode("root",null, true);
	        rechteTreeTableModel = new RechteTreeTableModel(root);
	        String[] colidentify = {"Programmfunktion","berechtigt"};
	        rechteTreeTableModel.setColumnIdentifiers(Arrays.asList(colidentify));
	        
	        //Highlighter hl = HighlighterFactory.createAlternateStriping();

	        
	        jXTreeTable = new JXTreeTable(rechteTreeTableModel);
	        //jXTreeTable.addHighlighter(hl);
	        TableCellRenderer renderer = new DefaultTableRenderer(new MappedValue(StringValues.EMPTY, IconValues.ICON), JLabel.CENTER);
	        jXTreeTable.getColumn(1).setCellRenderer(renderer);
	        jXTreeTable.getColumn(1).setMaxWidth(100);
	        jXTreeTable.getColumn(1).setCellEditor(comborechte);
	        jXTreeTable.setSelectionMode(0);
	        jXTreeTable.setShowGrid(true, false);
	        for(int i1 = 0; i1 < hauptGruppen.length;i1++){
	        	JXRechteTreeTableNode node = new JXRechteTreeTableNode(hauptGruppen[i1].toString(),
	        			new Rechte(hauptGruppen[i1],-1,null), true);
	        	String[] programmteile = rechteMap.get("gruppe"+Integer.toString(i1));
	        	if(programmteile != null){
	            	for(int i2 = 0; i2 < programmteile.length;i2++){
	                	JXRechteTreeTableNode node2 = new JXRechteTreeTableNode(programmteile[i2].toString(),
	                			new Rechte(programmteile[i2].toString(),0,""), true);
	            		node.insert(node2,node.getChildCount());
	            	}
	        		
	        	}
	        	rechteTreeTableModel.insertNodeInto(node, root, root.getChildCount());
	        }
	        jXTreeTable.addTreeSelectionListener(new RechteTreeSelectionListener() );
	        jXTreeTable.setCellSelectionEnabled(true);
	        jXTreeTable.setEnabled(false);
	        jXTreeTable.validate();

	        jXTreeTable.repaint();
		
        JScrollPane jscr = JCompTools.getTransparentScrollPane(jXTreeTable);
        jscr.validate();

		return jscr;
	}
	
	private JXPanel getButtonTeil(){
		//                                   1            2  3   4        5 
		FormLayout lay = new FormLayout("fill:0:grow(0.5),80dlu,3dlu,80dlu,fill:0:grow(0.5)",
		//       1             2   3   4  5   6  7   8  9  10  11  12 13  14 15  16  17  18   19
			"fill:0:grow(0.33),p,20dlu,p,1dlu,p,1dlu,p,1dlu,p,25dlu,p,5dlu,p,5dlu,p,15dlu,p,fill:0:grow(0.66)");
		CellConstraints cc = new CellConstraints();
		JXPanel jpan = new JXPanel();
		jpan.setLayout(lay);

		JLabel lab = new JLabel("Benutzer auswählen");
		jpan.add(lab,cc.xy(2,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		jcmb = new JRtaComboBox();
		jcmb.setDataVectorWithStartElement(ParameterLaden.pKollegen, 0, 1, "./.");
		jcmb.setActionCommand("benutzerwahl");
		jcmb.addActionListener(al);
		jpan.add(jcmb,cc.xy(4,2));
		
		lab = new JLabel("Benutzername");
		jpan.add(lab,cc.xy(2,4,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		tfs[0] = new JRtaTextField("nix",false);
		tfs[0].setEnabled(false);
		jpan.add(tfs[0],cc.xy(4,4));
		
		lab = new JLabel("Passwortanzeige");
		jpan.add(lab,cc.xy(2,6,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		jchb = new JRtaCheckBox("im Klartext anzeigen");
		jpan.add(jchb,cc.xy(4,6));
		if(!rechteTools.Rechte.hatRecht(rechteTools.Rechte.BenutzerSuper_user, false)){
			jchb.setSelected(false);
			jchb.setEnabled(false);
		}
		jchb.setActionCommand("klartext");
		jchb.addActionListener(al);
		
		lab = new JLabel("Passwort");
		jpan.add(lab,cc.xy(2,8,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		pws[0] = new JPasswordField();
		pws[0].setEnabled(false);
		jpan.add(pws[0],cc.xy(4,8));
		
		lab = new JLabel("Passwort wiederholen");
		jpan.add(lab,cc.xy(2,10,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		pws[1] = new JPasswordField();
		pws[1].setEnabled(false);
		jpan.add(pws[1],cc.xy(4,10));

		jpan.add((buts[0] = ButtonTools.macheButton("neuer Benutzer", "neu", al)),cc.xy(2,12));
		jpan.add((buts[1] = ButtonTools.macheButton("Benutzer ändern", "edit", al)),cc.xy(4,12));
		jpan.add((buts[2] = ButtonTools.macheButton("Benutzer speichern", "save", al)),cc.xy(2,14));
		jpan.add((buts[3] = ButtonTools.macheButton("Benutzer löschen", "delete", al)),cc.xy(4,14));
		jpan.add((buts[4] = ButtonTools.macheButton("Vorgang abbrechen", "dobreak", al)),cc.xyw(2,16,3));
		
		jpan.add((buts[5] = ButtonTools.macheButton("Rechte exportieren", "doexport", al)),cc.xy(2,18));
		jpan.add((buts[6] = ButtonTools.macheButton("Rechte importieren", "doimport", al)),cc.xy(4,18));
		buts[5].setForeground(Color.BLUE);
		buts[6].setForeground(Color.RED);
		regleButtons("1101000");
		return jpan;
	}
	
	private void putRechte(){
		rechteMap.put("gruppe0",gruppe0);
		rechteMap.put("gruppe1",gruppe1);
		rechteMap.put("gruppe2",gruppe2);
		rechteMap.put("gruppe3",gruppe3);
		rechteMap.put("gruppe4",gruppe4);
		rechteMap.put("gruppe5",gruppe5);
		rechteMap.put("gruppe6",gruppe6);
		rechteMap.put("gruppe7",gruppe7);
		rechteMap.put("gruppe8",gruppe8);
		rechteMap.put("gruppe9",gruppe9);
		rechteMap.put("gruppe10",gruppe10);
		rechteMap.put("gruppe11",gruppe11);		
		rechteMap.put("gruppe12",gruppe12);

	}
	/*******************************/
	private void regleButtons(String enable){
		for(int i = 0; i < buts.length;i++){
			buts[i].setEnabled( (enable.substring(i,i+1).equals("1") ? true : false)  );
		}
		if(!rechteTools.Rechte.hatRecht(rechteTools.Rechte.BenutzerSuper_user,false)){
			buts[5].setEnabled(false); 
			buts[6].setEnabled(false);
		}
	}
	/*******************************/	
	private void doBenutzerWahl(){
		if(jcmb.getSelectedIndex()==0){
			tfs[0].setText("");
			pws[0].setText("");
			pws[1].setText("");
			aktuelleRechte = "";
			userid="";
			this.internal.setTitle(elternTitel);
			regleButtons("1101000");
		}else{
			tfs[0].setText(jcmb.getSelectedItem().toString());
			pws[0].setText(jcmb.getValue().toString());
			pws[1].setText(jcmb.getValue().toString());
			
			////System.out.println(ParameterLaden.pKollegen.get(jcmb.getSelectedIndex()-1).get(0));
			aktuelleRechte = ParameterLaden.pKollegen.get(jcmb.getSelectedIndex()-1).get(2);
			if(!rechteTools.Rechte.hatRecht(rechteTools.Rechte.BenutzerSuper_user, false) 
					&& rechteTools.Rechte.testeRecht(aktuelleRechte, rechteTools.Rechte.BenutzerSuper_user)){
				JOptionPane.showMessageDialog(null,"SuperUser-Rechte können nur von einem Benutzer mit SuperUser-Rechten geändert werden");
				jcmb.setSelectedIndex(0);
				tfs[0].setText("");
				pws[0].setText("");
				pws[1].setText("");
				aktuelleRechte = "";
				userid="";
				regleButtons("1101000");
				return;
			}
			if(jchb.isSelected()){
				this.internal.setTitle(elternTitel+" [PW:"+jcmb.getValue().toString()+"]");
			}else{
				this.internal.setTitle(elternTitel);
			}
			userid = ParameterLaden.pKollegen.get(jcmb.getSelectedIndex()-1).get(4);
			regleButtons("1101000");
			aktualisiereTree(false);
		}
	}

	/*******************************/
	private void aktualisiereTree(boolean allesaufnull){
		int lang = getNodeCount();
		int recht = 0;
		for(int i = 0; i < lang;i++){
			JXRechteTreeTableNode node = holeNode(i);
			////System.out.println(node.rechte.bildnummer);
			if(node.rechte.bildnummer >= 0){
				if(allesaufnull){
					rechteTreeTableModel.setValueAt(0 ,	node, 1);
				}else{
					try{
						rechteTreeTableModel.setValueAt((Integer)Integer.parseInt(aktuelleRechte.substring(recht,recht+1)) ,
							node, 1);
					}catch(java.lang.NumberFormatException ex){
						
					}catch( java.lang.StringIndexOutOfBoundsException ex2){
						
					}
				}
				recht++;
			}
		}
		jXTreeTable.revalidate();
		jXTreeTable.repaint();
		
	}
	/*******************************/	
	private void makeListeners(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("benutzerwahl")){
					neu = false;
					regleButtons("1101000");
					doEditsEinAus(false);
					doBenutzerWahl();
					return;
				}
				if(cmd.equals("neu")){
					neu = true;
					doEditsEinAus(true);
					regleButtons("0010111");					
					doNeu();
					return;
				}
				if(cmd.equals("edit")){
					neu = false;
					doEditsEinAus(true);
					regleButtons("0010111");
					doEdit();
					return;
				}
				if(cmd.equals("save")){
					doSave();
					doEditsEinAus(false);
					neu = false;
					regleButtons("1101000");
					return;
				}
				if(cmd.equals("delete")){
					doEditsEinAus(false);
					neu = false;
					doDelete();
					regleButtons("1101000");
					return;
				}
				if(cmd.equals("dobreak")){
					jcmb.setSelectedIndex(0);
					doEditsEinAus(false);
					neu = false;
					regleButtons("1101000");					
				}
				if(cmd.equals("doimport")){
					doImport();
				}
				if(cmd.equals("doexport")){
					doExport();
				}
				if(cmd.equals("klartext")){
					doKlartext();
				}

			}
		};
		kl = new KeyListener(){
			@Override
			public void keyPressed(KeyEvent arg0) {
			}
			@Override
			public void keyReleased(KeyEvent arg0) {
			}
			@Override
			public void keyTyped(KeyEvent arg0) {
			}
		};
	}
	/********************************************/
	private void doKlartext(){
		if(jchb.isSelected() && (jcmb.getSelectedIndex()>0)){
			this.internal.setTitle(elternTitel+"  [ PW: "+jcmb.getValue().toString()+" ]");
		}else{
			this.internal.setTitle(elternTitel);
		}

	}
	/********************************************/
	private void doImport(){
		Verschluesseln man = Verschluesseln.getInstance();
	    man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
	    Vector<Vector<String>> vec = SqlInfo.holeFelder("select * from restricttemplates");
	    for(int i = 0; i < vec.size();i++){
	    	vec.get(i).set(0, man.decrypt(vec.get(i).get(0)) );
	    	vec.get(i).set(1, man.decrypt(vec.get(i).get(1)) );
	    }
	    JRtaTextField importRechte = new JRtaTextField("nix",false);
	    //Point pt, JRtaTextField xtf,String xtitel,Vector<Vector<String>> rechte
	    RechteImport rimport = new RechteImport(buts[6].getLocationOnScreen(),importRechte,"Rechte-Gruppe auswählen",vec);
	    rimport.pack();
	    rimport.setModal(true);
	    rimport.setVisible(true);
	    if(!importRechte.getText().equals("")){
	    	setImportRechte(importRechte.getText());	    	
	    }
    			
	}
	private void doExport(){
		String rechtegruppe = "normaler Therapeut"; 
		Object ret = JOptionPane.showInputDialog(null, "Geben Sie bitte einen Namen für die Rechte-Gruppe ein", rechtegruppe);
		if(ret == null){
			return;
		}
		Verschluesseln man = Verschluesseln.getInstance();
	    man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());

		String rechte = getRechte();
		String cmd = "insert into restricttemplates set abteilung='"+man.encrypt(ret.toString())+"', sammlung='"+man.encrypt(rechte)+"'";
		System.out.println(cmd);
		SqlInfo.sqlAusfuehren(cmd);
	}
	/********************************************/
	private void doEdit(){
		if(jcmb.getSelectedIndex()==0){
			//new AaarghHinweis("Und welcher Benutzer soll geändert werden?","nicht zu fassen...");
			JOptionPane.showMessageDialog(null, "Depp!");
			doEditsEinAus(false);
			neu = false;
			regleButtons("1101000");
			return;
		}
	}
	/********************************************/
	private void doDelete(){
		if(jcmb.getSelectedIndex()==0){
			JOptionPane.showMessageDialog(null, "Depp!");
			doEditsEinAus(false);
			regleButtons("1101000");
			return;
		}
		if(jcmb.getSelectedItem().toString().trim().equals(Reha.aktUser)){
			int anfrage = JOptionPane.showConfirmDialog(null, "Sie sind im Begriff sich selbst zu löschen!!!!\n\nWollen Sie  das wirklich?\n\n", "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
			if(anfrage==JOptionPane.YES_OPTION){
				String id = (String) jcmb.getValueAt(4);				
				String cmd = "delete from rehalogin where id='"+id+"' LIMIT 1";
				SqlInfo.sqlAusfuehren(cmd);
				jcmb.removeVector(jcmb.getSelectedIndex());
				jcmb.setSelectedIndex(0);
				doEditsEinAus(false);				
			}
		}else if(!jcmb.getSelectedItem().toString().trim().equals(Reha.aktUser)){
			String user = jcmb.getSelectedItem().toString();
			int anfrage = JOptionPane.showConfirmDialog(null, "Sie sind im Begriff einen Thera-Pi-Benutzer zu löschen!!!!\n\nWollen Sie  den Benutzer --> "+user+" <-- wirklich löschen?\n\n", "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
			if(anfrage==JOptionPane.YES_OPTION){
				String id = (String) jcmb.getValueAt(4);				
				String cmd = "delete from rehalogin where id='"+id+"' LIMIT 1";
				SqlInfo.sqlAusfuehren(cmd);
				jcmb.removeVector(jcmb.getSelectedIndex());
			}
			jcmb.setSelectedIndex(0);
			doEditsEinAus(false);			
		}
	}
	/********************************************/	
	private void doNeu(){
		tfs[0].setText("");
		pws[0].setText("");
		pws[1].setText("");
		aktualisiereTree(true);
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				tfs[0].requestFocus();
			}
		});
	}
	/********************************************/
	private void doEditsEinAus(boolean ein){
		tfs[0].setEnabled(ein);
		pws[0].setEnabled(ein);
		pws[1].setEnabled(ein);
		buts[2].setEnabled(ein);
		if(!ein){
			jXTreeTable.clearSelection();
			jXTreeTable.setEnabled(false);
		}else{
			jXTreeTable.clearSelection();
			jXTreeTable.setEnabled(true);
		}
	}
	/********************************************/	
	private void doSave(){
		
		if ( !String.valueOf(pws[0].getPassword()).equals(
				String.valueOf(pws[1].getPassword())) ){
			JOptionPane.showMessageDialog(null, "Passwort und Passwortwiederholung sind nicht identisch");
			return;
		}
		if(!rechteTools.Rechte.hatRecht(rechteTools.Rechte.BenutzerRechte_set, true)){
			return;
		}
		int lang = getNodeCount();
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < lang;i++){
			JXRechteTreeTableNode node = holeNode(i);
			////System.out.println(node.rechte.bildnummer);
			if(node.rechte.bildnummer >= 0){
				buf.append(Integer.toString(node.rechte.bildnummer));
			}
			
		}
		String pw = buf.toString();
		if(!rechteTools.Rechte.hatRecht(rechteTools.Rechte.BenutzerSuper_user, false) 
				&& rechteTools.Rechte.testeRecht(pw, rechteTools.Rechte.BenutzerSuper_user)){
			JOptionPane.showMessageDialog(null,"SuperUser-Rechte können nur von einem Benutzer mit SuperUser-Rechten vergeben werden");
			return;
		}
		
		
		Verschluesseln man = Verschluesseln.getInstance();
	    man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
		String encrypted = man.encrypt(pw);
		
		if(!neu){
			////System.out.println("Username = "+tfs[0].getText());
			////System.out.println("Passwort = "+String.valueOf(pws[0].getPassword()));
			////System.out.println("Rechte   = "+String.valueOf(pw));
			
			String cmd = "update rehalogin set user='"+man.encrypt(tfs[0].getText())+"', password='"+
			man.encrypt(String.valueOf(pws[0].getPassword()))+"', rights='"+encrypted+"' where id='"+userid+"' LIMIT 1";
			SqlInfo.sqlAusfuehren(cmd);
			jcmb.setNewValueAtCurrentPosition(0, tfs[0].getText());
			jcmb.setNewValueAtCurrentPosition(1, String.valueOf(pws[1].getPassword()));
			jcmb.setNewValueAtCurrentPosition(2, buf.toString());
			if(Reha.aktUser.equals(jcmb.getSelectedItem().toString().trim())){
				Reha.progRechte = buf.toString();
			}
		}else{
			// neuen Benutzer anlegen erst noch entwickeln;
			if(tfs[0].getText().equals("") || String.valueOf(pws[0].getPassword()).trim().equals("")){
				JOptionPane.showMessageDialog(null, "Benutzername und Passwort darf nicht leer sein");
				jcmb.setSelectedIndex(0);
				doEditsEinAus(false);
				return;
			}
			int id = SqlInfo.holeId("rehalogin", "password");
			Vector<String> vec = new Vector<String>();
			vec.add(tfs[0].getText());
			vec.add(String.valueOf(pws[0].getPassword()));
			vec.add(buf.toString());
			vec.add("");
			vec.add(Integer.toString(id));
			jcmb.addNewVector((Vector<String>)vec.clone());
			String cmd = "update rehalogin set user='"+man.encrypt(tfs[0].getText())+"', password='"+
			man.encrypt(String.valueOf(pws[0].getPassword()))+"', rights='"+encrypted+"' where id='"+Integer.toString(id)+"' LIMIT 1";
			SqlInfo.sqlAusfuehren(cmd);
		}
		ParameterLaden.Passwort();
		
	}
/******************************************************************/
	private String getRechte(){
		int lang = getNodeCount();
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < lang;i++){
			JXRechteTreeTableNode node = holeNode(i);
			////System.out.println(node.rechte.bildnummer);
			if(node.rechte.bildnummer >= 0){
				buf.append(Integer.toString(node.rechte.bildnummer));
			}
			
		}
		return buf.toString();
	}
	private void setImportRechte(String rechte){

			int lang = getNodeCount();

			int recht = 0;
			for(int i = 0; i < lang;i++){
				JXRechteTreeTableNode node = holeNode(i);
				if(node.rechte.bildnummer >= 0){
					node.rechte.bildnummer = Integer.parseInt(rechte.substring(recht,recht+1));
					node.rechte.setRechteIcon(Integer.parseInt(rechte.substring(recht,recht+1)));
					//System.out.println(node.rechte.programmteil);
					//System.out.println(Integer.parseInt(rechte.substring(recht,recht+1)));
					//System.out.println(node.rechte.bildnummer);
					recht++;
				}
			}
			
			jXTreeTable.validate();
			jXTreeTable.repaint();
	}
	
	private int getNodeCount(){
		int ret = 0; 
		int  rootAnzahl;
		int  kindAnzahl;
		JXRechteTreeTableNode rootNode;
		JXRechteTreeTableNode childNode;
		rootAnzahl =  root.getChildCount();
		if(rootAnzahl<=0){return 0;}
		int geprueft = 0;
		for(int i = 0; i < rootAnzahl;i++){
			rootNode = (JXRechteTreeTableNode) root.getChildAt(i);
			ret += 1;
			if( (kindAnzahl = rootNode.getChildCount())>0){
				ret+=kindAnzahl;
			}
		}
		return ret;
	}
	
	private JXRechteTreeTableNode holeNode(int zeile){
		
		JXRechteTreeTableNode node = null;
		int  rootAnzahl;
		int  kindAnzahl;
		JXRechteTreeTableNode rootNode;
		JXRechteTreeTableNode childNode;
		rootAnzahl =  root.getChildCount();
		if(rootAnzahl<=0){return node;}
		int geprueft = 0;
		for(int i = 0; i < rootAnzahl;i++){
			
			rootNode = (JXRechteTreeTableNode) root.getChildAt(i);

			if(rootNode.isLeaf() ){
				if(geprueft == zeile){
					return rootNode;	
				}else{
					geprueft++;
					continue;
				}
				
			}else if((!rootNode.isLeaf()) && ((geprueft==zeile))){
				return rootNode;
			}else if(!rootNode.isLeaf()){
				kindAnzahl = rootNode.getChildCount();
				geprueft++;
				for(int i2 = 0; i2 < kindAnzahl;i2++){
					
					if(geprueft==zeile){
						childNode = (JXRechteTreeTableNode) rootNode.getChildAt(i2);
						return childNode;
					}else{
						childNode = (JXRechteTreeTableNode) rootNode.getChildAt(i2);
						geprueft ++;						
					}

				}
			}else{
				geprueft++;	
			}
		}
		return node;
	}
	
/****************************************************************************************/
    private static class JXRechteTreeTableNode extends DefaultMutableTreeTableNode {
    	private boolean enabled = false;
    	private Rechte rechte = null;
    	public JXRechteTreeTableNode(String name,Rechte rechte ,boolean enabled){
    		super(name);
    		this.enabled = enabled;
   			this.rechte = rechte;
   			if(rechte != null){
   				this.setUserObject(rechte);
   			}
    	}
 
		public boolean isEnabled() {
			return enabled;
		}
		
		public Rechte getObject(){
			return rechte;
		}
    }
	private class RechteTreeTableModel extends DefaultTreeTableModel {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		DecimalFormat dfx = new DecimalFormat( "0.00" );
		
        public RechteTreeTableModel(JXRechteTreeTableNode jXrechteTreeTableNode) {
            super(jXrechteTreeTableNode);
        }
        /******************/
        public Object getValueAt(Object node, int column) {
        	JXRechteTreeTableNode jXTreeTableNode = (JXRechteTreeTableNode) node;

        	Rechte o = null;
        	try{
        		o =  (Rechte) jXTreeTableNode.getUserObject();
        	}catch(ClassCastException cex){
        		return super.getValueAt(node, column);
        	}
        	 switch (column) {
        	 	case 0:
        	 		return o.hauptgruppe;
        	 	case 1:
        	 		return o.rechteicon.getIcon();
	            case 2:
	                //return o.programmteil;	               
        	 }        
        	return super.getValueAt(node, column);
        }
        /******************/
        public void setValueAt(Object value, Object node, int column){
        	JXRechteTreeTableNode jXTreeTableNode = (JXRechteTreeTableNode) node;
        	Rechte o;
        	
           	try{
            	o =  (Rechte) jXTreeTableNode.getUserObject();
            }catch(ClassCastException cex){
            	cex.printStackTrace();
            	return;
            }
            switch (column) {
            	case 0:
            		o.hauptgruppe =((String) value) ;
            		break;
            	case 1:
            		o.rechteicon.setIcon(o.img[(Integer) value]) ;   
            		o.bildnummer = (Integer) value;
            		break;
            	case 2:
            		o.programmteil =((String) value) ;
            		break;
            }	
        }  
        /******************/
        public int getColumnCount() {
            return 2;
        }

        /******************/
        public boolean isCellEditable(java.lang.Object node,int column){
        	Rechte o = null;
        	try{
        		o =  (Rechte) ((JXRechteTreeTableNode)node).getUserObject();
        	}catch(ClassCastException cex){
        		cex.printStackTrace();
        	}        	
            switch (column) {
            case 0:
                return false;
            case 1:
            	if(o.bildnummer >= 0){
            		return true;
            	}else{
                    return false;            		
            	}
            case 2:
                return true;
            default:
                return false;
            }
        }
        /******************/
        public Class<?> getColumnClass(int column) {
            switch (column) {
            case 0:
                return String.class;
            case 1:
                return JLabel.class;
            case 2:
                return String.class;
            default:
                return Object.class;
            }
        }
        /******************/        
        
	}    
    
    class Rechte{
    	String hauptgruppe="";
    	String programmteil="";
    	JLabel rechteicon = null;
    	int bildnummer = -1;
    	ImageIcon[] img = {SystemConfig.hmSysIcons.get("zuzahlnichtok"),SystemConfig.hmSysIcons.get("zuzahlok")};
    	
    	public Rechte(String hauptgruppe,int rechteicon,String xrechte){
    		this.hauptgruppe = hauptgruppe;
    		this.programmteil = xrechte;
			this.rechteicon = new JLabel("");
    		if(rechteicon>=0){
    			this.rechteicon.setHorizontalAlignment(JLabel.CENTER);
    			this.rechteicon.setIcon(img[rechteicon]);
        		this.bildnummer = rechteicon;
    		}
    		
    	}
    	public void setRechteIcon(int icon){
			this.rechteicon.setIcon(img[icon]);
    		this.bildnummer = icon;
    	}
    }
    class MyRechteComboBox extends AbstractCellEditor implements TableCellEditor{ 
    	/**
		 * 
		 */
		private static final long serialVersionUID = -1394804970777323591L;
		// This is the component that will handle the editing of the cell value 
    	/**
    	 * 
    	 */
    	public JComboBox component =null;
    	
    	ImageIcon[] img = {SystemConfig.hmSysIcons.get("zuzahlnichtok"),SystemConfig.hmSysIcons.get("zuzahlok")};
    	public MyRechteComboBox(){
    		component = new JComboBox(new Object[] { SystemConfig.hmSysIcons.get("zuzahlnichtok"), 
    				SystemConfig.hmSysIcons.get("zuzahlok")});

    		((JComboBox)component).setRenderer(new RechteComboBoxRenderer() );
    		
    	}
		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			if(isSelected){
				((JComboBox)component).requestFocus();
				if(value instanceof ImageIcon){
					if( ((ImageIcon)value).equals(img[0])){
						((JComboBox)component).setSelectedIndex(0);					
					}else if( ((ImageIcon)value).equals(img[1])){
						((JComboBox)component).setSelectedIndex(1);
					}else{
						((JComboBox)component).setSelectedIndex(0);
					}
				}
			}else{
				return null;
			}
			return component;			
		}

		@Override
		public Object getCellEditorValue() {
			return ((JComboBox)component).getSelectedIndex();
		}
		
    }
    class RechteComboBoxRenderer extends JLabel  implements ListCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1773401769072883430L;

		public RechteComboBoxRenderer() {
				super();
				setOpaque(true);
		        setHorizontalAlignment(CENTER);
		        setVerticalAlignment(CENTER);

		}

		@Override
		public Component getListCellRendererComponent(JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

			//int selectedIndex = ((Integer)value).intValue();

	        if (isSelected) {
	            setBackground(list.getSelectionBackground());
	            setForeground(list.getSelectionForeground());
	        } else {
	            setBackground(list.getBackground());
	            setForeground(list.getForeground());
	        }
	        if(value != null){
	        	setIcon((ImageIcon)value);
	        }

			return this;
		}
    	
    }
	class RechteTreeSelectionListener implements TreeSelectionListener {
		boolean isUpdating = false;
		
		public void valueChanged(TreeSelectionEvent e) {
			if (!isUpdating) {
				isUpdating = true;
				JXTreeTable tt = jXTreeTable;
				TreeTableModel ttmodel = tt.getTreeTableModel();
				TreePath[] selpaths = tt.getTreeSelectionModel().getSelectionPaths();
				
				if (selpaths !=null) {
					ArrayList<TreePath> selPathList = new ArrayList<TreePath>(Arrays.asList(selpaths));
					int i=1;
					while(i<=selPathList.size()) {
						TreePath currPath = selPathList.get(i-1);
						Object currentObj = currPath.getLastPathComponent();
						int childCnt = ttmodel.getChildCount(currentObj);
						for(int j=0;j<childCnt; j++) {
							Object child = ttmodel.getChild(currentObj, j);
							TreePath nuPath = currPath.pathByAddingChild(child);
							if(!selPathList.contains(nuPath)) {
								selPathList.add(nuPath);
							}
						}
						i++;
					}
					selpaths = selPathList.toArray(new TreePath[0]);

					tt.getTreeSelectionModel().setSelectionPaths(selpaths);
					
					TreePath tp = tt.getTreeSelectionModel().getSelectionPath();
					aktNode =  (JXRechteTreeTableNode) tp.getLastPathComponent();//selpaths[selpaths.length-1].getLastPathComponent();
					new SwingWorker<Void,Void>(){
						protected Void doInBackground() throws Exception {
							int lang = getNodeCount();
							aktRow = -1;
							
							for(int i = 0; i < lang; i++){
								if(aktNode == holeNode(i)){
									aktRow = i;
									////System.out.println("Zeilennummer =  = "+i);
									////System.out.println("Node selektiert = "+aktNode.abr.bezeichnung);
									////System.out.println("Behandlungsdatum selektiert = "+aktNode.abr.datum+" / "+aktNode.abr.bezeichnung);
									break;
								}
							}
							return null;
						}
					}.execute();
				}
			}
			isUpdating = false;
		}
		
		
	 
	}
    

}
