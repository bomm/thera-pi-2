package dta301;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.thera_pi.nebraska.gui.utils.ButtonTools;

import rehaInternalFrame.JDta301Internal;
import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemTools.Colors;
import systemTools.JCompTools;
import systemTools.JRtaCheckBox;
import systemTools.JRtaComboBox;
import systemTools.JRtaRadioButton;
import systemTools.JRtaTextField;
import systemTools.StringTools;
import terminKalender.DatFunk;

import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.TextException;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.windows.WindowsTabbedPaneUI;

public class Dta301 extends JXPanel implements FocusListener {

	/**
	 * 
	 */
	ActionListener al = null;
	MouseListener ml = null;
	MouseListener tblmouse = null;
	JDta301Internal internal = null;
	JXPanel content = null;
	JXPanel beginn = null;
	JXPanel unterbrechung = null;
	JXPanel abschluss = null;
	JXPanel uebersicht = null;
	JXPanel verlaengerung = null;
	JTabbedPane tabPan = null;
	
	JEditorPane fallPan = null;

	boolean is301Ok = false;
	String reznummer = "";
	String patnummer = "";
	
	//Beginn-Mitteilung
	JRtaRadioButton[] beginnradio = {null,null,null}; 
	ButtonGroup bggroup = new ButtonGroup();
	JRtaTextField beginndatum = null;
	JRtaTextField beginnstunde = null;
	JRtaTextField beginnminute = null;
	JTextArea beginneditpan = null;
	JRtaCheckBox beginncheck = null;
	
	//Unterbrechungsmeldung
	JRtaRadioButton[] ubradio = {null,null,null};
	JRtaTextField ubbeginndatum = null;
	JRtaTextField ubendedatum = null;
	ButtonGroup ubbg = new ButtonGroup();
	JRtaComboBox ucombo = null;
	JTextArea ueditpan = null;
	
	//Verlängerungsanzeige/Antrag
	JRtaRadioButton[] vbradio = {null,null};
	JRtaTextField vbbeginndatum = null;
	JRtaTextField vbendedatum = null;
	ButtonGroup vbbg = new ButtonGroup();
	JRtaComboBox vcombo = null;
	JTextArea veditpan = null;

	//Entlassmitteilung
	JRtaTextField entlasserstdatum = null;
	JRtaTextField entlassletztdatum = null;
	JRtaTextField entlassstunde = null;
	JRtaTextField entlassminute = null;
	JRtaComboBox entlassafcombo = null;
	JRtaComboBox entlassartcombo = null;
	JRtaCheckBox entlassmitfahrgeld = null;
	JRtaCheckBox entlassnurfahrgeld = null;
	JRtaTextField entlassfahrgeld = null;
	JTextArea entlasstpan = null;
	
	//Übersicht der Nachrichten
	JXTable tabuebersicht = null;
	MyTermTableModel moduebersicht = null;	
	
	
	JButton[] buts = {null,null,null,null,null};
	
	private static final long serialVersionUID = 7725262330614584928L;
	public Dta301(JDta301Internal jai,String xreznummer	){
		super();
		this.setLayout(new BorderLayout());
		this.internal = jai;
		this.reznummer = xreznummer;
		this.makeListeners();
		this.add(getContent(),BorderLayout.CENTER);
		this.setName(this.internal.getName());
		this.content.setName(this.internal.getName());
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				holeDaten();
				if(!is301Ok){
					disableButtons();
				}
				return null;
			}
		}.execute();
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				setzeFocus();
			}
		});
	}
	private void setzeFocus(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				beginndatum.requestFocus();
			}
		});
	}
	public void aktualisieren(String xreznummer){
		this.reznummer = xreznummer;
		holeDaten();
		doTabelleFuellen();
	}
	private void makeListeners(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("beginnsenden")){
					if(!is301Ok){
						JOptionPane.showMessageDialog(null,"Diese Verordnung ist nicht für DTA-301 vorgesehen");
						return;
					}
					doBeginn();
					return;
				}
				if(cmd.startsWith("ubart")){
					doRegleFelder(cmd.substring(5));
				}
				if(cmd.startsWith("ubsenden")){
					if(!is301Ok){
						JOptionPane.showMessageDialog(null,"Diese Verordnung ist nicht für DTA-301 vorgesehen");
						return;
					}
					doUnterbrechung();					
				}
				if(cmd.startsWith("entlsenden")){
					if(!is301Ok){
						JOptionPane.showMessageDialog(null,"Diese Verordnung ist nicht für DTA-301 vorgesehen");
						return;
					}
					doEntlassung();					
				}
				if(cmd.startsWith("vbsenden")){
					if(!is301Ok){
						JOptionPane.showMessageDialog(null,"Diese Verordnung ist nicht für DTA-301 vorgesehen");
						return;
					}
					doVerlaengerung();					
				}
				if(cmd.equals("original")){
					int row = tabuebersicht.getSelectedRow();
					if(row < 0){return;}
					String scmd = "select nachrichtorg from dtafall where id ='"+
					tabuebersicht.getValueAt(row,3)+"' LIMIT 1";
					doZeigeEdifact(SqlInfo.holeEinzelFeld(scmd));
				}
				if(cmd.equals("aufbereitet")){
					int row = tabuebersicht.getSelectedRow();
					if(row < 0){return;}
					String id = tabuebersicht.getValueAt(row,3).toString();
					String typ = (tabuebersicht.getValueAt(row,0).toString().contains("Ablehnung") ? "2" : "1");
					Vector<Vector<String>> vec = SqlInfo.holeFelder("select pat_intern,rez_nr from dtafall where id ='"+id+"' LIMIT 1");
					String scmd = "select edifact from dta301 where pat_intern='"+
					vec.get(0).get(0)+"' and rez_nr='"+vec.get(0).get(1)+"' and nachrichtentyp='"+typ+"' LIMIT 1";
					doZeigeEdifact(SqlInfo.holeEinzelFeld(scmd));
				}

				
			}
		};
		ml = new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getSource() instanceof JXTable){
					if(e.getClickCount()==1 && e.getButton()==3){
						int row = tabuebersicht.getSelectedRow();
						if(row < 0){return;}
						String typ = tabuebersicht.getValueAt(row,0).toString();
						boolean bewilligung =  tabuebersicht.getValueAt(row,0).toString().equals("Bewilligung");
						doNachrichtenPopUp(e,bewilligung,typ);
					}
					if(e.getClickCount()==2 && e.getButton()==1){
						int row = tabuebersicht.getSelectedRow();
						if(row < 0){return;}
						String scmd = "select nachrichtorg from dtafall where id ='"+
						tabuebersicht.getValueAt(row,3)+"' LIMIT 1";
						doZeigeEdifact(SqlInfo.holeEinzelFeld(scmd));
					}
				}
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
			@Override
			public void mouseReleased(MouseEvent e) {
			}
		};
	}
	private String[] getEdiTimeString(){
		String[] sret = {null,null};
		Date date = new Date();
		String[] datesplit = date.toString().split(" ");
		////System.out.println(date.toString());
		sret[0] = datesplit[3].substring(0,2);
		sret[1] = datesplit[3].substring(3,5);
		return sret;
	}

	private JXPanel getContent(){
		content = new JXPanel(new BorderLayout());
		content.setOpaque(false);
		//UIManager.put("TabbedPane.tabsOpaque", Boolean.FALSE);
		//UIManager.put("TabbedPane.contentOpaque", Boolean.FALSE);
		tabPan = new JTabbedPane();
		tabPan.setOpaque(false);
		tabPan.setUI(new WindowsTabbedPaneUI());
		
		
		tabPan.add("Nachrichtenübersicht",uebersicht=getUebersicht());
		tabPan.add("Aufnahme/Absage", beginn=getBeginn());
		tabPan.add("Unterbrechung",unterbrechung=getUnterbrechung());
		tabPan.add("Verlängerung",verlaengerung=getVerlaengerung());
		tabPan.add("Entlass-Mitteilung",abschluss=getEntlassung());
		
		content.add(tabPan,BorderLayout.CENTER);
		content.add(getFallDaten(),BorderLayout.WEST);
		return content;
	}
	
	private JXPanel getBeginn(){
		JXPanel headerpan = new JXPanel(new BorderLayout());
		headerpan.setOpaque(false);
		//headerpan.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		JXPanel pan = new JXPanel();
		pan.setOpaque(false);
		//                 1             2           3   4   5   
		String xwerte = "100dlu,right:max(100dlu;p),50dlu,p,20dlu:g";
		//		          1    2  3   4  5   6  7   8   9   10    11  12   13
		String ywerte = "25dlu,p,2dlu,p,2dlu,p,2dlu,p,20dlu,p,2dlu,p,2dlu,35dlu,2dlu,p,fill:0:grow(1.0)";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		/****Aufnahme oder Absage*****/
		beginncheck = new JRtaCheckBox("Bewilligung liegt vor");
		beginnradio[0] = new JRtaRadioButton("Nachricht ist eine Aufnahmemitteilung");
		beginnradio[0].setOpaque(false);
		bggroup.add(beginnradio[0]);
		beginnradio[1] = new JRtaRadioButton("Nachricht ist eine Rückstellung der Aufnahme");
		bggroup.add(beginnradio[1]);
		beginnradio[1].setOpaque(false);
		beginnradio[2] = new JRtaRadioButton("Nachricht ist eine Absage an den Kostenträger");
		bggroup.add(beginnradio[2]);
		beginnradio[2].setOpaque(false);

		beginnradio[0].setSelected(true);
		beginncheck.setSelected(true);
		pan.add(beginncheck,   cc.xyw(2, 2, 3, CellConstraints.FILL,CellConstraints.FILL));
		pan.add(beginnradio[0],cc.xyw(2, 4, 3, CellConstraints.FILL,CellConstraints.FILL));
		pan.add(beginnradio[1],cc.xyw(2, 6, 3, CellConstraints.FILL,CellConstraints.FILL));
		pan.add(beginnradio[2],cc.xyw(2, 8, 3, CellConstraints.FILL,CellConstraints.FILL));
		/************Datum und Uhrzeit*******/
		JLabel lab = new JLabel("Datum der Aufnahme / Rückstellung");
		lab.setForeground(Color.BLUE);
		pan.add(lab,cc.xy(2,10));
		beginndatum = new JRtaTextField("DATUM",true);
		beginndatum.setText(DatFunk.sHeute());
		pan.add(beginndatum,cc.xy(4,10));
		lab = new JLabel("Uhrzeit der Aufnahme");
		lab.setForeground(Color.BLUE);
		pan.add(lab,cc.xy(2,12));
		/***********/
		JXPanel pan2 = new JXPanel();
		pan2.setOpaque(false);
		String xwerte2 = "p:g,p,p:g";
		String ywerte2 = "p";
		FormLayout lay2 = new FormLayout(xwerte2,ywerte2);
		CellConstraints cc2 = new CellConstraints();
		pan2.setLayout(lay2);
		beginnstunde = new JRtaTextField("STUNDEN",true);
		beginnstunde.setName("beginnstunde");
		beginnstunde.addFocusListener(this);
		beginnstunde.setText(getEdiTimeString()[0]);
		pan2.add(beginnstunde,cc2.xy(1,1));
		pan2.add(new JLabel(" : "),cc2.xy(2,1));
		beginnminute = new JRtaTextField("MINUTEN",true);
		beginnminute.setName("beginnminute");
		beginnminute.addFocusListener(this);
		beginnminute.setText(getEdiTimeString()[1]);
		pan2.add(beginnminute,cc2.xy(3, 1));
		pan2.validate();
		pan.add(pan2,cc.xy(4, 12,CellConstraints.FILL,CellConstraints.FILL));
		/*********Textfeld für die Nachricht*********/
		beginneditpan = new JTextArea();
		beginneditpan.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		beginneditpan.setFont(new Font("Courier",Font.PLAIN,11));
		beginneditpan.setLineWrap(true);
		beginneditpan.setName("beginnabsage");
		beginneditpan.setToolTipText("Sofern erforderlich kurze Notiz für den Empfänger eingeben");
		beginneditpan.setWrapStyleWord(true);
		beginneditpan.setEditable(true);
		beginneditpan.setBackground(Color.WHITE);
		beginneditpan.setForeground(Color.BLUE);
		JScrollPane jscr = JCompTools.getTransparentScrollPane(beginneditpan);
		jscr.validate();
		pan.add(jscr,cc.xyw(2,14,3,CellConstraints.FILL,CellConstraints.FILL));
		/*********Button zum Abfeuern********/
		buts[0] = ButtonTools.macheBut("Nachricht erzeugen und senden", "beginnsenden", al);
		pan.add(buts[0],cc.xyw(2, 16, 3, CellConstraints.FILL,CellConstraints.FILL));
		pan.validate();
		headerpan.add(getHeader(0),BorderLayout.NORTH);
		headerpan.add(pan,BorderLayout.CENTER);
		return headerpan;
	}
	/******************************************************/
	private JXPanel getUnterbrechung(){
		JXPanel headerpan = new JXPanel(new BorderLayout());
		headerpan.setOpaque(false);
		//headerpan.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		JXPanel pan = new JXPanel();
		pan.setOpaque(false);
		//                 1     2       3                4   5   6
		String xwerte = "100dlu,0dlu,right:max(100dlu;p),5dlu,50dlu,20dlu";
		//                 1  2  3   4  5   6  7    8  9  10   11 12 13   14   15   16
		String ywerte = "25dlu,p,2dlu,p,2dlu,p,10dlu,p,2dlu,p,2dlu,p,2dlu,35dlu,25dlu,p,fill:0:grow(1.0)";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		ubradio[0] = new JRtaRadioButton("Beginn einer Unterbrechung melden");
		ubradio[1] = new JRtaRadioButton("Ende einer Unterbrechung melden");
		ubradio[2] = new JRtaRadioButton("Beginn und Ende einer Unterbrechung melden");
		for(int i = 0;i < 3 ; i++){
			ubbg.add(ubradio[i]);
			ubradio[i].setActionCommand("ubart"+Integer.toString(i));
			ubradio[i].addActionListener(al);
			pan.add(ubradio[i],cc.xyw(2,2+(i*2),4 ));
		}
		ubradio[0].setSelected(true);
		JLabel lab = new JLabel("Erster Tag der Unterbrechung");
		lab.setForeground(Color.BLUE);
		pan.add(lab,cc.xy(3,8));
		ubbeginndatum = new JRtaTextField("DATUM",true);
		ubbeginndatum.setText(DatFunk.sHeute());
		pan.add(ubbeginndatum,cc.xy(5,8));
		lab = new JLabel("Letzter Tag der Unterbrechung");
		lab.setForeground(Color.BLUE);
		pan.add(lab,cc.xy(3,10));
		ubendedatum = new JRtaTextField("DATUM",true);
		ubendedatum.setText(DatFunk.sHeute());
		pan.add(ubendedatum,cc.xy(5,10));
		ucombo = new JRtaComboBox();
		String[][] codeListe = Dta301CodeListen.getCodeListe("B08");
		for(int i = 0; i < codeListe.length;i++){
		  ucombo.addItem(codeListe[i][1]);
		}
		ucombo.setSelectedIndex(2);
		pan.add(ucombo,cc.xyw(3,12,3));
		ubendedatum.setEnabled(false);
		ueditpan = new JTextArea();
		ueditpan.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		ueditpan.setFont(new Font("Courier",Font.PLAIN,11));
		ueditpan.setLineWrap(true);
		ueditpan.setName("notitzen");
		ueditpan.setToolTipText("Sofern erforderlich kurze Notiz für den Empfänger eingeben");
		ueditpan.setWrapStyleWord(true);
		ueditpan.setEditable(true);
		ueditpan.setBackground(Color.WHITE);
		ueditpan.setForeground(Color.BLUE);
		JScrollPane jscr = JCompTools.getTransparentScrollPane(ueditpan);
		jscr.validate();
		pan.add(jscr,cc.xyw(3,14,3,CellConstraints.FILL,CellConstraints.FILL));
		
		buts[1] = ButtonTools.macheBut("Nachricht erzeugen und senden", "ubsenden", al);
		pan.add(buts[1],cc.xyw(3, 16, 3, CellConstraints.FILL,CellConstraints.FILL));
		
		pan.validate();
		headerpan.add(getHeader(1),BorderLayout.NORTH);
		headerpan.add(pan,BorderLayout.CENTER);
		return headerpan;
	}
	/************************************************/
	private JXPanel getVerlaengerung(){
		JXPanel headerpan = new JXPanel(new BorderLayout());
		headerpan.setOpaque(false);
		//headerpan.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		JXPanel pan = new JXPanel();
		pan.setOpaque(false);
		//                 1     2       3                4   5   6
		String xwerte = "100dlu,0dlu,right:max(100dlu;p),5dlu,50dlu,20dlu";
		//                 1  2  3   4  5   6  7    8  9  10   11 12 13   14   15   16
		String ywerte = "25dlu,p,2dlu,p,2dlu,p,10dlu,p,2dlu,p,2dlu,p,2dlu,35dlu,25dlu,p,fill:0:grow(1.0)";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		vbradio[0] = new JRtaRadioButton("Verlängerung anzeigen");
		vbradio[1] = new JRtaRadioButton("Verlängerung beantragen mit Begründung");
		
		for(int i = 0;i < 2 ; i++){
			vbbg.add(vbradio[i]);
			vbradio[i].setActionCommand("vbart"+Integer.toString(i));
			vbradio[i].addActionListener(al);
			pan.add(vbradio[i],cc.xyw(2,2+(i*2),4 ));
		}
		vbradio[0].setSelected(true);
		JLabel lab = new JLabel("Erster Tag der Verlängerung");
		lab.setForeground(Color.BLUE);
		pan.add(lab,cc.xy(3,8));
		vbbeginndatum = new JRtaTextField("DATUM",true);
		vbbeginndatum.setText(DatFunk.sHeute());
		pan.add(vbbeginndatum,cc.xy(5,8));
		lab = new JLabel("Letzter Tag der Verlängerung");
		lab.setForeground(Color.BLUE);
		pan.add(lab,cc.xy(3,10));
		vbendedatum = new JRtaTextField("DATUM",true);
		vbendedatum.setText(DatFunk.sHeute());
		pan.add(vbendedatum,cc.xy(5,10));
		/*
		vcombo = new JRtaComboBox();
		String[][] codeListe = Dta301CodeListen.getCodeListe("B08");
		for(int i = 0; i < codeListe.length;i++){
		  ucombo.addItem(codeListe[i][1]);
		}
		vcombo.setSelectedIndex(2);
		pan.add(vcombo,cc.xyw(3,12,3));
		*/
		veditpan = new JTextArea();
		veditpan.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		veditpan.setFont(new Font("Courier",Font.PLAIN,11));
		veditpan.setLineWrap(true);
		veditpan.setName("notitzen");
		veditpan.setToolTipText("Sofern erforderlich kurze Notiz für den Empfänger eingeben");
		veditpan.setWrapStyleWord(true);
		veditpan.setEditable(true);
		veditpan.setBackground(Color.WHITE);
		veditpan.setForeground(Color.BLUE);
		JScrollPane jscr = JCompTools.getTransparentScrollPane(veditpan);
		jscr.validate();
		pan.add(jscr,cc.xyw(3,14,3,CellConstraints.FILL,CellConstraints.FILL));
		
		buts[3] = ButtonTools.macheBut("Nachricht erzeugen und senden", "vbsenden", al);
		pan.add(buts[3],cc.xyw(3, 16, 3, CellConstraints.FILL,CellConstraints.FILL));
		
		pan.validate();
		headerpan.add(getHeader(1),BorderLayout.NORTH);
		headerpan.add(pan,BorderLayout.CENTER);
		return headerpan;
	}	
	/************************************************/
	private JXPanel getEntlassung(){
		JXPanel headerpan = new JXPanel(new BorderLayout());
		headerpan.setOpaque(false);
		//headerpan.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		JXPanel pan = new JXPanel();
		pan.setOpaque(false);
		String xwerte = "100dlu,right:max(100dlu;p),5dlu,50dlu,20dlu";
		//                1   2   3   4   5  6  7   8  9   10 11  12     13  14 15 16   17  18 
		String ywerte = "25dlu,p,5dlu,p,5dlu,p,5dlu,p,5dlu,p,5dlu,35dlu,5dlu,p,2dlu,p, 35dlu,p,fill:0:grow(1.0),5dlu";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		//Tagestabelle bauen
		/*
		modentlass = new MyTermTableModel();
		String[] column = 	{"Beh.Datum","Behandler","Text","Beh.Art",""};
		modentlass.setColumnIdentifiers(column);
		tabentlass = new JXTable(modentlass);
		*/
		//Arbeitsfähigkeits Combo bauen
		entlassafcombo = new JRtaComboBox();
		String[][] arbfaehig = Dta301CodeListen.getCodeListe("B02");
		for(int i = 0; i < arbfaehig.length;i++){
			entlassafcombo.addItem(String.valueOf(arbfaehig[i][1]));
		}
		//Entlassform Combo bauen
		entlassartcombo = new JRtaComboBox();
		arbfaehig = Dta301CodeListen.getCodeListe("B07");
		for(int i = 0; i < arbfaehig.length;i++){
			entlassartcombo.addItem(String.valueOf(arbfaehig[i][1]));
		}
		//jetzt den ganzen Sermons in das Panel hängen
		JLabel lab = new JLabel("Aufnahmedatum");
		lab.setForeground(Color.BLUE);
		pan.add(lab,cc.xy(2,2));
		entlasserstdatum = new JRtaTextField("DATUM",true);
		entlasserstdatum.setText(SystemConfig.hmAdrRDaten.get("<Rerstdat>"));
		pan.add(entlasserstdatum,cc.xy(4,2));
		//
		lab = new JLabel("Entlassdatum");
		lab.setForeground(Color.BLUE);
		pan.add(lab,cc.xy(2,4));
		entlassletztdatum = new JRtaTextField("DATUM",true);
		entlassletztdatum.setText(SystemConfig.hmAdrRDaten.get("<Rletztdat>"));
		pan.add(entlassletztdatum,cc.xy(4,4));
		
		lab = new JLabel("Uhrzeit der Entlassung");
		lab.setForeground(Color.BLUE);
		pan.add(lab,cc.xy(2,6));
		/***********/
		JXPanel pan2 = new JXPanel();
		pan2.setOpaque(false);
		String xwerte2 = "p:g,p,p:g";
		String ywerte2 = "p";
		FormLayout lay2 = new FormLayout(xwerte2,ywerte2);
		CellConstraints cc2 = new CellConstraints();
		pan2.setLayout(lay2);
		entlassstunde = new JRtaTextField("STUNDEN",true);
		entlassstunde.setName("entlassstunde");
		entlassstunde.addFocusListener(this);
		entlassstunde.setText(getEdiTimeString()[0]);
		pan2.add(entlassstunde,cc2.xy(1,1));
		pan2.add(new JLabel(" : "),cc2.xy(2,1));
		entlassminute = new JRtaTextField("MINUTEN",true);
		entlassminute.setName("entlassminute");
		entlassminute.addFocusListener(this);
		entlassminute.setText(getEdiTimeString()[1]);
		pan2.add(entlassminute,cc2.xy(3, 1));
		pan2.validate();
		pan.add(pan2,cc.xy(4, 6,CellConstraints.FILL,CellConstraints.FILL));
		/************/
		//
		pan.add(entlassafcombo,cc.xyw(2,8,3));
		pan.add(entlassartcombo,cc.xyw(2,10,3));

		entlasstpan = new JTextArea();
		entlasstpan.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		entlasstpan.setFont(new Font("Courier",Font.PLAIN,11));
		entlasstpan.setLineWrap(true);
		entlasstpan.setName("notitzen");
		entlasstpan.setToolTipText("Sofern erforderlich kurze Notiz für den Empfänger eingeben");
		entlasstpan.setWrapStyleWord(true);
		entlasstpan.setEditable(true);
		entlasstpan.setBackground(Color.WHITE);
		entlasstpan.setForeground(Color.BLUE);
		JScrollPane jscr = JCompTools.getTransparentScrollPane(entlasstpan);
		jscr.validate();
		pan.add(jscr,cc.xyw(2,12,3,CellConstraints.FILL,CellConstraints.FILL));
		entlassmitfahrgeld = new JRtaCheckBox("Fahrtgeld in Rechnung stellen");
		entlassmitfahrgeld.setHorizontalTextPosition(SwingConstants.LEFT);
		pan.add(entlassmitfahrgeld,cc2.xy(2,14));
		entlassfahrgeld = new JRtaTextField("D",true,"6.2","RECHTS");
		pan.add(entlassfahrgeld,cc.xy(4,14));
		entlassnurfahrgeld = new JRtaCheckBox("Nur Fahrtgeld in Rechnung stellen");
		entlassnurfahrgeld.setHorizontalTextPosition(SwingConstants.LEFT);
		pan.add(entlassnurfahrgeld,cc2.xy(2,16));
		buts[2] = ButtonTools.macheBut("Nachricht erzeugen und senden", "entlsenden", al);
		pan.add(buts[2],cc.xyw(2, 18, 3, CellConstraints.FILL,CellConstraints.FILL));
		pan.validate();
		headerpan.add(getHeader(2),BorderLayout.NORTH);
		headerpan.add(pan,BorderLayout.CENTER);
		return headerpan;
	}
	private JXPanel getUebersicht(){
		JXPanel headerpan = new JXPanel(new BorderLayout());
		headerpan.setOpaque(false);
		//headerpan.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		JXPanel pan = new JXPanel();
		pan.setOpaque(false);
		String xwerte = "25dlu,fill:0:grow(1.0),25dlu";
		String ywerte = "25dlu,p,5dlu,fill:0:grow(1.0),25dlu";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		JLabel lab = new JLabel("Übersicht der Nachrichten zu diesem Fall");
		lab.setForeground(Color.BLUE);
		pan.add(lab,cc.xy(2,2));
		String[] headers = {"Anlass","Datum","Bearbeiter","id","esol","icr"};
		moduebersicht = new MyTermTableModel();
		moduebersicht.setColumnIdentifiers(headers);
		tabuebersicht = new JXTable(moduebersicht);
		tabuebersicht.setHighlighters(HighlighterFactory.createSimpleStriping(Colors.PiOrange.alpha(0.25f)));
		tabuebersicht.getColumn(0).setMinWidth(150);
		tabuebersicht.getColumn(3).setMinWidth(0);
		tabuebersicht.getColumn(3).setMaxWidth(35);
		tabuebersicht.getColumn(4).setMaxWidth(60);
		tabuebersicht.getColumn(5).setMaxWidth(60);
		tabuebersicht.setName("uebersicht");
		tabuebersicht.addMouseListener(ml);
		JScrollPane jscr = JCompTools.getTransparentScrollPane(tabuebersicht);
		jscr.validate();
		pan.add(jscr, cc.xy(2,4,CellConstraints.FILL,CellConstraints.FILL));
		pan.validate();
		headerpan.add(getHeader(3),BorderLayout.NORTH);
		headerpan.add(pan,BorderLayout.CENTER);
		doTabelleFuellen();
		return headerpan;
	}
	private JXPanel getFallDaten(){
		JXPanel pan = new JXPanel(new BorderLayout());
		pan.setOpaque(false);
		pan.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		fallPan = new JEditorPane();
		fallPan.setContentType("text/html");
		fallPan.setEditable(false);
		fallPan.setPreferredSize(new Dimension(200,200));
		fallPan.setOpaque(false);
		JScrollPane jscr = JCompTools.getTransparentScrollPane(fallPan);
		jscr.validate();
		pan.add(jscr,BorderLayout.CENTER);
		pan.validate();
		return pan;
	}
	
	private void holeDaten(){
		StringBuffer buf = new StringBuffer();
		try{
			buf.append("<html><head>");
			buf.append("<STYLE TYPE=\"text/css\">");
			buf.append("<!--");
			buf.append("A{text-decoration:none;background-color:transparent;border:none}");
			buf.append("TD{font-family: Arial; font-size: 12pt; padding-left:5px;padding-right:5px;padding-top:0px,padding-bottom:0px}");
			buf.append(".spalte1{color:#0000FF;}");
			buf.append(".spalte2{color:#333333;}");
			buf.append(".spalte3{color:#000000;}");
			buf.append("--->");
			buf.append("</STYLE>");
			buf.append("</head>");
			buf.append("<div style=margin-left:5px;>");
			buf.append("<font face=\"Tahoma\"><style=margin-left=5px;>");
			buf.append("<table>");
			/****************************/
			buf.append("<tr><td class=\"spalte3\" align=\"left\">"+"<img src='file:///"+Reha.proghome+"icons/rezept.png' border=0>");
			buf.append("</td></tr>");
			buf.append("<tr><td class=\"spalte1\"><font size=\"+1\"><b>"+reznummer);
			buf.append("</b></font></td></tr>");
			buf.append("<tr><td>&nbsp</td></tr>");
			buf.append("<tr><td class=\"spalte1\" align=\"left\">"+"<img src='file:///"+Reha.proghome+"icons/kontact_contacts.png' width=52 height=52 border=0>");
			buf.append("</td></tr>");
			
			buf.append("<tr><td class=\"spalte1\">");
			buf.append(StringTools.EGross(Reha.thisClass.patpanel.patDaten.get(0).trim())+" "+
					StringTools.EGross(Reha.thisClass.patpanel.patDaten.get(1).trim()));
			buf.append("</td></tr>" );
			
			buf.append("<tr><td class=\"spalte3\" align=\"left\">");
			buf.append("<b><font color=#000000>"+StringTools.EGross(Reha.thisClass.patpanel.patDaten.get(2))+", "+
					StringTools.EGross(Reha.thisClass.patpanel.patDaten.get(3))+"</font></b>");
			buf.append("</td></tr>" );
			buf.append("<tr><td class=\"spalte1\" align=\"left\">");
			buf.append("geb.: "+"<b><font color=#000000>"+DatFunk.sDatInDeutsch(Reha.thisClass.patpanel.patDaten.get(4))+"</font></b>");
			buf.append("</td></tr>" );
			buf.append("<tr><td class=\"spalte1\" align=\"left\">");
			buf.append(StringTools.EGross(Reha.thisClass.patpanel.patDaten.get(21)));
			buf.append("</td></tr>" );
			buf.append("<tr><td class=\"spalte1\" align=\"left\">");
			buf.append(Reha.thisClass.patpanel.patDaten.get(23)+" "+StringTools.EGross(Reha.thisClass.patpanel.patDaten.get(24)));
			buf.append("</td></tr>" );
			
			buf.append("<tr><td>&nbsp</td></tr>");
			buf.append("<tr><td class=\"spalte1\" align=\"left\">"+"<img src='file:///"+Reha.proghome+"icons/emblem-mail.png' width=52 height=52 border=0>");
			buf.append("</td></tr>");

			patnummer = String.valueOf(Reha.thisClass.patpanel.patDaten.get(29));
			if(Integer.parseInt( SqlInfo.holeEinzelFeld("select count(*) from dta301 where pat_intern='"+patnummer+"' and rez_nr='"+reznummer+"' and nachrichtentyp='1'")) <= 0){
				is301Ok = false;
				buf.append("<tr><td class=\"spalte1\"><font size=\"+1\" color=#ff0000><b>"+"§301-DTA nicht möglich mit dieser Verordnung");
				buf.append("</b></font></td></tr>");
			}else{
				String cmd = "select kassen_nam1 from kass_adr where ik_kasse ="+
				"(select ktraeger from dta301 where pat_intern='"+patnummer+"' and rez_nr='"+reznummer+"' LIMIT 1) LIMIT 1";
				buf.append("<tr><td class=\"spalte1\"><font color=#ff0000><b>"+SqlInfo.holeEinzelFeld(cmd));
				buf.append("</b></font></td></tr>");
				cmd = "select kassen_nam2 from kass_adr where ik_kasse ="+
				"(select ktraeger from dta301 where pat_intern='"+patnummer+"' and rez_nr='"+reznummer+"' LIMIT 1) LIMIT 1";
				buf.append("<tr><td class=\"spalte1\"><font color=#ff0000><b>"+SqlInfo.holeEinzelFeld(cmd));
				buf.append("</b></font></td></tr>");

				is301Ok = true;
			}
			/****************************/
			getEndeHtml(buf);
			//fallPan.setText(buf.toString());
		}catch(Exception ex){
			//
		}
	}
	private void getEndeHtml(StringBuffer buf){
		buf.append("</table>");
		buf.append("</font>");
		buf.append("</div>");
		buf.append("</html>");
		fallPan.setText(buf.toString());
		return;
	}
	private void doTabelleFuellen(){
		new SwingWorker<Void,Void>(){
			@SuppressWarnings("unchecked")
			@Override
			protected Void doInBackground() throws Exception {
				try{
					String[] anlass = {"Unbekannter Anlass","Bewilligung","Ablehnung",
							"Aufnahmemitteilung","Unterbrechung (Beginn)","Verlängerung","Entlassmitteilung",
							"E-Bericht","Rechnung","Absage an den Kostenträger","Einberufung","Rückstellung",
							"Entlassmitteilung und Fahrgeldabrechnung","Fahrgeldabrechnung","Bestätigung der Verlängerung",
							"Unterbrechung (Ende)","Unterbrechung (Beginn und Ende)"};
					String cmd = "select nachrichttyp,nachrichtdatum,bearbeiter,id,esolname,icr from dtafall where pat_intern='"+
					String.valueOf(Reha.thisClass.patpanel.patDaten.get(29))+
					"' and rez_nr='"+
					reznummer+
					"' order by nachrichtdatum,id";
					//System.out.println(cmd);
					Vector<Vector<String>> vec = SqlInfo.holeFelder(cmd);
					Vector<String> dummy = new Vector<String>();
					String test = null;
					moduebersicht.setRowCount(0);
					//System.out.println(vec);
					//System.out.println(cmd);
					for(int i = 0;i < vec.size();i++){
						dummy.clear();
						dummy.trimToSize();
						test = vec.get(i).get(0);
						dummy.add(anlass[Integer.parseInt(test)]);
						dummy.add(DatFunk.sDatInDeutsch(vec.get(i).get(1)));
						dummy.add(vec.get(i).get(2));
						dummy.add(vec.get(i).get(3));
						dummy.add(vec.get(i).get(4));
						dummy.add(vec.get(i).get(5));
						moduebersicht.addRow((Vector<String>)dummy.clone());
					}
					if(moduebersicht.getRowCount()>0){
						tabuebersicht.setRowSelectionInterval(0, 0);
					}
					tabuebersicht.validate();
				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}
			
		}.execute();
	}
	private void disableButtons(){
		
	}
	private JXHeader getHeader(int welcher){
		JXHeader head = new JXHeader();
		head.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		
		switch(welcher){
		case 0:
	        head.setTitle("Aufnahme oder Absage einer Rehaleistungen mitteilen");
	        head.setDescription("<html>Tragen Sie hier bitte das <u>Datum der Eingangsuntersuchung</u> des Patienten sowie die <u>Uhrzeit</u> der Aufnahme ein.<br>"+
	        		"Im Fall einer Absage benutzen Sie bitte die untere Rubrik</html>");
	        head.setIcon(new ImageIcon(Reha.proghome+"icons/start.jpg"));
	        break;
		case 1:
	        head.setTitle("Unterbrechung eine Reha");
	        head.setDescription("<html><br>Hier melden Sie <u>Reha-Unterbrechungen</u> sowie den <u>Grund der Unterbrechung</u> (z.B.Krankheit.)<br>"+
	        		"Tragen Sie den <u>ersten Fehltag</u> und den <u>letzten Fehltag</u> ein.<br>Bei Bedarf ergänzen Sie die Nachricht durch einen <u>kurzen</u> Text.</html>");
	        head.setIcon(new ImageIcon(Reha.proghome+"icons/break.jpg"));
	        break;
		case 2:
	        head.setTitle("Entlass-Mitteilung eines Reha-Patienten");
	        head.setDescription("<html><br><u>Kontrollieren Sie im Rezeptstamm die einzelnen Datumswerte</u> auf Richtigkeit.<br><br>Tragen Sie dann das <u>Datum der Aufnahme</u> und das <u>Datum der Entlassung</u> ein.</html>");
	        head.setIcon(new ImageIcon(Reha.proghome+"icons/ziel.jpg"));
	        break;
		case 3:
	        head.setTitle("Übersicht über alle Meldungen die diesen Fall betreffen");
	        head.setDescription("<html><br>Datmit eine lückenlose Fall-Kontrolle möglich ist können Sie sich hier<br>einen Überblick über die gesamte Kommunikation mit dem jeweiligen Kostenträger verschaffen.</html>");
	        head.setIcon(new ImageIcon(Reha.proghome+"icons/uebersicht.jpg"));
	        break;
	        
		}
		
		return head;
	}
	private boolean doRegle301(int art,String id){
		if(art==0){
			if(!executeNachricht(art)){return false;}
			RVMeldung301 meldung301 = new RVMeldung301(art,id);
			int aufnahmeart = -1;
			for(int i = 0; i < 3;i++){
				if(beginnradio[i].isSelected()){
					aufnahmeart = i;
					break;
				}
			}
			meldung301.doBeginn(this.beginndatum.getText().trim(),
					this.beginnstunde.getText().trim()+this.beginnminute.getText().trim(),
					beginneditpan.getText().trim(),aufnahmeart,beginncheck.isSelected()
					);
			doTabelleFuellen();
			return true;
		}
		if(art==1){
			if(!executeNachricht(art)){return false;}
			RVMeldung301 meldung301 = new RVMeldung301(art,id);
			int iart = -1;
			for(int i  = 0; i < 3; i++){
				if(ubradio[i].isSelected()){
					iart = i;
					break;
				}
			}
			meldung301.doUnterbrechung(this.ubbeginndatum.getText().trim(),
					this.ubendedatum.getText().trim(),iart,ucombo.getSelectedIndex(),this.ueditpan.getText().trim());
			doTabelleFuellen();
			return true;
		}
		if(art==2){
			if(!executeNachricht(art)){return false;}
			RVMeldung301 meldung301 = new RVMeldung301(art,id);
			//JRtaCheckBox entlassmitfahrgeld = null;
			//JRtaTextField entlassfahrgeld = null;
			//JTextArea entlasstpan = null;

			meldung301.doEntlassung(this.entlasserstdatum.getText().trim(),
					this.entlassletztdatum.getText().trim(),
					this.entlassstunde.getText().trim()+this.entlassminute.getText().trim(),
					entlassafcombo.getSelectedIndex(),
					entlassartcombo.getSelectedIndex(),
					entlassmitfahrgeld.isSelected(),
					entlassfahrgeld.getText(),
					entlasstpan.getText(),
					entlassnurfahrgeld.isSelected()
					);
			doTabelleFuellen();
			return true;
		}
		if(art==3){
			if(!executeNachricht(art)){return false;}
			RVMeldung301 meldung301 = new RVMeldung301(art,id);
			int iart = -1;
			for(int i  = 0; i < 3; i++){
				if(vbradio[i].isSelected()){
					iart = i;
					break;
				}
			}
			meldung301.doVerlaengerung(this.vbbeginndatum.getText().trim(),
					this.vbendedatum.getText().trim(),iart,this.veditpan.getText().trim());
			doTabelleFuellen();
			return true;
		}

		return false;

	}
	/*****************************************************/
	private void doRegleFelder(String art){
		if(art.equals("0")){
			this.ubbeginndatum.setEnabled(true);
			this.ubendedatum.setEnabled(false);
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					ubbeginndatum.requestFocus();					
				}
			});
			return;
		}
		if(art.equals("1")){
			this.ubbeginndatum.setEnabled(false);
			this.ubendedatum.setEnabled(true);
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					ubendedatum.requestFocus();					
				}
			});
			return;
		}
		if(art.equals("2")){
			this.ubbeginndatum.setEnabled(true);
			this.ubendedatum.setEnabled(true);
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					ubbeginndatum.requestFocus();					
				}
			});
			return;
		}
	}
	private void doBeginn(){
		
		setCursor(Reha.thisClass.wartenCursor);
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				
				try{
					buts[0].setEnabled(false);
					if(! doRegle301(0,SqlInfo.holeEinzelFeld("select id from dta301 where pat_intern='"+patnummer+"' and rez_nr='"+reznummer+"' and nachrichtentyp='1' LIMIT 1")) ){
						buts[0].setEnabled(true);
						setCursor(Reha.thisClass.normalCursor);
						return null;
					}
					JOptionPane.showMessageDialog(null, "Beginnmitteilung erfolgreich versandt!");
					buts[0].setEnabled(true);
					tabPan.setSelectedIndex(0);
					
				}catch(Exception ex){
					ex.printStackTrace();
				}
				setCursor(Reha.thisClass.normalCursor);
				return null;
			}
			
		}.execute();
	}
	private void doVerlaengerung(){
		setCursor(Reha.thisClass.wartenCursor);
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				
				try{
					buts[3].setEnabled(false);
					if(! doRegle301(3,SqlInfo.holeEinzelFeld("select id from dta301 where pat_intern='"+patnummer+"' and rez_nr='"+reznummer+"' and nachrichtentyp='1' LIMIT 1"))){
						buts[3].setEnabled(true);
						setCursor(Reha.thisClass.normalCursor);
						return null;
					}
					JOptionPane.showMessageDialog(null, "Verlängerung erfolgreich versandt!");
					buts[3].setEnabled(true);
					tabPan.setSelectedIndex(0);
					
				}catch(Exception ex){
					ex.printStackTrace();
				}
				setCursor(Reha.thisClass.normalCursor);
				return null;
			}
			
		}.execute();
	}

	private void doUnterbrechung(){
		setCursor(Reha.thisClass.wartenCursor);
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				
				try{
					buts[1].setEnabled(false);
					if(! doRegle301(1,SqlInfo.holeEinzelFeld("select id from dta301 where pat_intern='"+patnummer+"' and rez_nr='"+reznummer+"' and nachrichtentyp='1' LIMIT 1"))){
						buts[1].setEnabled(true);
						setCursor(Reha.thisClass.normalCursor);
						return null;
					}
					JOptionPane.showMessageDialog(null, "Unterbrechungsmeldung erfolgreich versandt!");
					buts[1].setEnabled(true);
					tabPan.setSelectedIndex(0);
					
				}catch(Exception ex){
					ex.printStackTrace();
				}
				setCursor(Reha.thisClass.normalCursor);
				return null;
			}
			
		}.execute();
	}
	private void doEntlassung(){
		setCursor(Reha.thisClass.wartenCursor);
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				
				try{
					buts[2].setEnabled(false);
					if(! doRegle301(2,SqlInfo.holeEinzelFeld("select id from dta301 where pat_intern='"+patnummer+"' and rez_nr='"+reznummer+"' and nachrichtentyp='1' LIMIT 1")) ){
						buts[2].setEnabled(true);
						setCursor(Reha.thisClass.normalCursor);
						return null;						
					}
					JOptionPane.showMessageDialog(null, "Entlassmitteilung erfolgreich versandt!");
					buts[2].setEnabled(true);
					setCursor(Reha.thisClass.normalCursor);
					tabPan.setSelectedIndex(0);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				setCursor(Reha.thisClass.normalCursor);
				return null;
			}
			
		}.execute();
	}
	/*****************************************************/
	private boolean executeNachricht(int art){
		String meldung = null;
				int aufnahmeart = 0;
				int unterbart = 0;
				int verlart = 0;
		boolean keingrundbeginn = beginneditpan.getText().trim().equals("");

		//Aufnahme/Rückstellung/Absage**********************************************/
		if(art == 0){
			for(int i = 0; i < 3;i++){
				if(beginnradio[i].isSelected()){
					aufnahmeart = i;
					break;
				}
			}
			if(aufnahmeart == 0){
				meldung = "<html><font color='#ff0000' size=+2>Nachricht mit diesen Parametern erzeugen?</font><br><br>"+
				"Narchittyp: <b>Aufnahmemitteilung"+
				"</b><br><br>Aufnahmedatum: <b>"+beginndatum.getText()+
				"</b><br><br>Aufnahme Uhrzeit: <b>"+beginnstunde.getText()+":"+beginnminute.getText()+
				"</b><br><br><b>Begründung bei Aufnahme i.d.R. nicht erforderlich</b>"+
				"</html>";		
			}else if(aufnahmeart == 1){
				meldung = "<html><font color='#ff0000' size=+2>Nachricht mit diesen Parametern erzeugen?</font><br><br>"+
				"Narchittyp: <b>Rückstellung einer Aufnahme"+
				"</b><br><br>Rückstellung bis Datum: <b>"+beginndatum.getText()+
				"</b><br><br><b>Rückstellung Uhrzeit nicht erforderlich"+
				"</b><br><br>"+(keingrundbeginn ? 
						"<b><font color='#ff0000'>Achtung bei Rückstellung muß eine Begründung eingegeben werden</font></b>" : 
							"<b>Begründung für Rückstellung o.k. ??</b>")+
				"</html>";		
			}else if(aufnahmeart == 2){
				meldung = "<html><font color='#ff0000' size=+2>Nachricht mit diesen Parametern erzeugen?</font><br><br>"+
				"Narchittyp: <b>Absage an den Kostenträger"+
				"</b><br><br>Absagedatum bis Datum: <b>"+beginndatum.getText()+
				"</b><br><br><b>Absage Uhrzeit nicht erforderlich"+
				"</b><br><br>"+(keingrundbeginn ? 
						"<b><font color='#ff0000'>Achtung bei Absagen muß eine Begründung eingegeben werden</font></b>" : 
							"<b>Begründung für Absage o.k. ??</b>")+
				"</html>";		
			}
		}
		
		//Unterbrechung**********************************************/		
		if(art == 1){
			for(int i = 0; i < 3;i++){
				if(ubradio[i].isSelected()){
					unterbart = i;
					break;
				}
			}
			meldung = "<html><font color='#ff0000' size=+2>Nachricht mit diesen Parametern erzeugen?</font><br><br>";
			if(unterbart==0){
				meldung = meldung + "Narchittyp: <b>Beginn einer Unterbrechung melden"+
				"</b><br><br>Erster Tag der Unterbrechung: <b>"+ubbeginndatum.getText()+
				"</b><br><br>Grund der Unterbrechung: "+ucombo.getSelectedItem().toString()+
				"</b><br><br><b>Erklärungstext nicht unbedingt erforderlich"+
				"</b></html>";
			}
			if(unterbart==1){
				meldung = meldung + "Narchittyp: <b>Ende einer Unterbrechung melden"+
				"</b><br><br>Letzter Tag der Unterbrechung: <b>"+ubendedatum.getText()+
				"</b><br><br>Grund der Unterbrechung: "+ucombo.getSelectedItem().toString()+
				"</b><br><br><b>Erklärungstext nicht unbedingt erforderlich"+
				"</b></html>";
			}
			if(unterbart==2){
				meldung = meldung + "Narchittyp: <b>Beginn und Ende einer Unterbrechung melden"+
				"</b><br><br>Erster Tag der Unterbrechung: <b>"+ubbeginndatum.getText()+				
				"</b><br><br>Letzter Tag der Unterbrechung: <b>"+ubendedatum.getText()+
				"</b><br><br>Grund der Unterbrechung: "+ucombo.getSelectedItem().toString()+
				"</b><br><br><b>Erklärungstext nicht unbedingt erforderlich"+
				"</b></html>";
			}
		}
		//Entlassmitteilung**********************************************/
		if(art == 2){
			meldung = "<html><font color='#ff0000' size=+2>Nachricht mit diesen Parametern erzeugen?</font><br><br>";
			if(!entlassnurfahrgeld.isSelected()){
				meldung = meldung + "Narchittyp: <b>Entlassmitteilung"+
				"</b><br><br>Aufnahmetag: <b>"+entlasserstdatum.getText()+
				"</b><br><br>Entlasstag: <b>"+entlassletztdatum.getText()+
				"</b><br><br>Entlass-Uhrzeit: <b>"+entlassstunde.getText()+":"+entlassminute.getText()+
				"</b><br><br>Arbeitsfähigkeit: <b><font color='#ff0000'>"+entlassafcombo.getSelectedItem().toString()+"</font>"+
				"</b><br><br>Arbeitsform: <b>"+entlassartcombo.getSelectedItem().toString();
			}else{
				meldung = meldung + "Narchittyp: <b>Nur Fahrgeldberechnung";
			}
			if(!entlassmitfahrgeld.isSelected()){
				meldung = meldung + "</b><br><br>Fahrgeld wird berechnet: <font color='#ff0000'><b>NEIN</b></font>"+
				"</b><br><br><b>Erklärungstext nicht unbedingt erforderlich"+
				"</b></html>";
			}else{
				meldung = meldung + "</b><br><br>Fahrgeld wird berechnet: <font color='#ff0000'><b>JA</b></font>"+
				"</b><br><br><b>Höhe des Fahrgeldes: <b>"+entlassfahrgeld.getText()+
				"</b><br><br><b>Erklärungstext nicht unbedingt erforderlich"+
				"</b></html>";

			}
		}
		//Verlängerungs Mitteilung/Antrag**********************************************/
		if(art == 3){
			verlart = (vbradio[0].isSelected() ? 0 : 1);
			meldung = "<html><font color='#ff0000' size=+2>Nachricht mit diesen Parametern erzeugen?</font><br><br>";
			if(verlart == 0){
				meldung = meldung + "Narchittyp: <b>Verlängerungsmitteilung"+
				"</b><br><br>Erster Tag der Verlängerung: <b>"+vbbeginndatum.getText()+
				"</b><br><br>Letzter Tag der Verlängerung: <b>"+vbendedatum.getText()+
				"</b><br><br><b>Erklärungstext nicht unbedingt erforderlich";
			}else{
				meldung = meldung + "Narchittyp: <b>Antrag auf Verlängerung"+
				"</b><br><br>Erster Tag der Verlängerung: <b>"+vbbeginndatum.getText()+
				"</b><br><br>Letzter Tag der Verlängerung: <b>"+vbendedatum.getText();
				if(veditpan.getText().trim().equals("")){
					meldung = meldung+"</b><br><br><b><font color='#ff0000'>Achtung bei Absagen muß eine Begründung eingegeben werden</font>"+
					"</b></html>";
				}else{
					meldung = meldung+"</b><br><br><b>Medizinsiche Begründung wurde eingegeben"+
					"</b></html>";
				}
			}
		}

		int anfrage = JOptionPane.showConfirmDialog(null, meldung, "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
		if(anfrage != JOptionPane.YES_OPTION){
			return false;
		}
		if((art==0) && (aufnahmeart > 0) && (keingrundbeginn)){
			return false;
		}
		if((art==3) && (aufnahmeart > 0) && (veditpan.getText().trim().equals(""))){
			return false;
		}

		return true;
	}
	private void doNachrichtenPopUp(java.awt.event.MouseEvent me, boolean bewilligung,String typ){
		JPopupMenu jPop = doNachrichtenMenue(bewilligung,typ);
		jPop.show( me.getComponent(), me.getX(), me.getY() ); 
	}
	private JPopupMenu doNachrichtenMenue(boolean bewilligung, String typ){
		JPopupMenu jPopupMenu = new JPopupMenu();

		JMenuItem item = new JMenuItem("Original "+typ+" im OO-Writer öffnen");
		item.setActionCommand("original");
		item.addActionListener(al);
		jPopupMenu.add(item);
		if(bewilligung || typ.equals("Ablehnung")){
			item = new JMenuItem("Bearbeitete "+typ+" im OO-Writer öffnen");
			item.setActionCommand("aufbereitet");
			item.addActionListener(al);
			jPopupMenu.add(item);
		}
		jPopupMenu.addSeparator();
		item = new JMenuItem("Nachricht löschen");
		item.setActionCommand("deletemessage");
		item.addActionListener(al);
		jPopupMenu.add(item);

		return jPopupMenu;
		
	}	
 
	private void doZeigeEdifact(String buf){
		ITextDocument document = oOorgTools.OOTools.starteLeerenWriter();
		try {
			document.getTextService().getCursorService().getTextCursor().getCharacterProperties().setFontName("Courier New");
			document.getTextService().getCursorService().getTextCursor().getCharacterProperties().setFontSize(9.f);
		} catch (TextException e) {
			e.printStackTrace();
		}
		document.getTextService().getText().setText(buf);

	}
	/*****************************************************/	
	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	class MyTermTableModel extends DefaultTableModel{
		   /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Class<?> getColumnClass(int columnIndex) {
				   if(columnIndex==0){return String.class;}
				   /*else if(columnIndex==1){return JLabel.class;}*/
				   else{return String.class;}
		           //return (columnIndex == 0) ? Boolean.class : String.class;
		       }

			    public boolean isCellEditable(int row, int col) {
			        //Note that the data/cell address is constant,
			        //no matter where the cell appears onscreen.
			    	return false;
			    }
		}


}
