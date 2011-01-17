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
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
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
import systemTools.JRtaComboBox;
import systemTools.JRtaRadioButton;
import systemTools.JRtaTextField;
import systemTools.StringTools;
import terminKalender.DatFunk;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.windows.WindowsTabbedPaneUI;

public class Dta301 extends JXPanel implements FocusListener {

	/**
	 * 
	 */
	ActionListener al = null;
	MouseListener tblmouse = null;
	JDta301Internal internal = null;
	JXPanel content = null;
	JXPanel beginn = null;
	JXPanel unterbrechung = null;
	JXPanel abschluss = null;
	JXPanel uebersicht = null;
	JTabbedPane tabPan = null;
	
	JEditorPane fallPan = null;

	boolean is301Ok = false;
	String reznummer = "";
	String patnummer = "";
	
	//Beginn-Mitteilung
	JRtaTextField beginndatum = null;
	JRtaTextField beginnstunde = null;
	JRtaTextField beginnminute = null;

	//Unterbrechungsmeldung
	JRtaRadioButton[] ubradio = {null,null,null};
	JRtaTextField ubbeginndatum = null;
	JRtaTextField ubendedatum = null;
	ButtonGroup ubbg = new ButtonGroup();
	JRtaComboBox ucombo = null;
	JTextArea ueditpan = null;
	
	//Entlassmitteilung
	JRtaTextField entlasserstdatum = null;
	JRtaTextField entlassletztdatum = null;
	JRtaTextField entlassstunde = null;
	JRtaTextField entlassminute = null;
	JRtaComboBox entlassafcombo = null;
	JRtaComboBox entlassartcombo = null;
	
	//Übersicht der Nachrichten
	JXTable tabuebersicht = null;
	MyTermTableModel moduebersicht = null;	
	
	
	JButton[] buts = {null,null,null,null,null};
	
	private static final long serialVersionUID = 7725262330614584928L;
	public Dta301(JDta301Internal jai){
		super();
		this.setLayout(new BorderLayout());
		this.internal = jai;
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
			}
		};
	}
	private JXPanel getContent(){
		content = new JXPanel(new BorderLayout());
		content.setOpaque(false);
		//UIManager.put("TabbedPane.tabsOpaque", Boolean.FALSE);
		//UIManager.put("TabbedPane.contentOpaque", Boolean.FALSE);
		tabPan = new JTabbedPane();
		tabPan.setOpaque(false);
		tabPan.setUI(new WindowsTabbedPaneUI());
		
		

		tabPan.add("Beginn-Mitteilung", beginn=getBeginn());
		tabPan.add("Unterbrechung melden",unterbrechung=getUnterbrechung());
		tabPan.add("Entlass-Mitteilung",abschluss=getEntlassung());
		tabPan.add("Übersicht Fall-Nachrichten",uebersicht=getUebersicht());
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
		String xwerte = "100dlu,right:max(100dlu;p),50dlu,p,20dlu";
		String ywerte = "40dlu,p,5dlu,p,25dlu,p,fill:0:grow(1.0)";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		JLabel lab = new JLabel("Datum der Eingangsuntersuchung");
		lab.setForeground(Color.BLUE);
		pan.add(lab,cc.xy(2,2));
		beginndatum = new JRtaTextField("DATUM",true);
		beginndatum.setText(DatFunk.sHeute());
		pan.add(beginndatum,cc.xy(4,2));
		lab = new JLabel("Uhrzeit der Aufnahme");
		lab.setForeground(Color.BLUE);
		pan.add(lab,cc.xy(2,4));
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
		beginnstunde.setText("00");
		pan2.add(beginnstunde,cc2.xy(1,1));
		pan2.add(new JLabel(" : "),cc2.xy(2,1));
		beginnminute = new JRtaTextField("MINUTEN",true);
		beginnminute.setName("beginnminute");
		beginnminute.addFocusListener(this);
		beginnminute.setText("00");
		pan2.add(beginnminute,cc2.xy(3, 1));
		pan2.validate();
		pan.add(pan2,cc.xy(4, 4,CellConstraints.FILL,CellConstraints.FILL));
		/************/
		buts[0] = ButtonTools.macheBut("Nachricht erzeugen und senden", "beginnsenden", al);
		pan.add(buts[0],cc.xyw(2, 6, 3, CellConstraints.FILL,CellConstraints.FILL));
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
		String ywerte = "25dlu,p,2dlu,p,2dlu,p,10dlu,p,2dlu,p,2dlu,p,2dlu,25dlu,25dlu,p,fill:0:grow(1.0)";
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
		String[][] codeListe = Dta301CodeListen.codeB08;
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
		pan.add(ueditpan,cc.xyw(3,14,3,CellConstraints.FILL,CellConstraints.FILL));
		
		buts[1] = ButtonTools.macheBut("Nachricht erzeugen und senden", "ubsenden", al);
		pan.add(buts[1],cc.xyw(3, 16, 3, CellConstraints.FILL,CellConstraints.FILL));
		
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
		String ywerte = "25dlu,p,5dlu,p,5dlu,p,5dlu,p,5dlu,p,25dlu,p,fill:0:grow(1.0),5dlu";
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
		String[][] arbfaehig = Dta301CodeListen.codeB02;
		for(int i = 0; i < arbfaehig.length;i++){
			entlassafcombo.addItem(String.valueOf(arbfaehig[i][1]));
		}
		//Entlassform Combo bauen
		entlassartcombo = new JRtaComboBox();
		arbfaehig = Dta301CodeListen.codeB07;
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
		entlassstunde.setText("00");
		pan2.add(entlassstunde,cc2.xy(1,1));
		pan2.add(new JLabel(" : "),cc2.xy(2,1));
		entlassminute = new JRtaTextField("MINUTEN",true);
		entlassminute.setName("entlassminute");
		entlassminute.addFocusListener(this);
		entlassminute.setText("00");
		pan2.add(entlassminute,cc2.xy(3, 1));
		pan2.validate();
		pan.add(pan2,cc.xy(4, 6,CellConstraints.FILL,CellConstraints.FILL));
		/************/
		
		//
		pan.add(entlassafcombo,cc.xyw(2,8,3));
		pan.add(entlassartcombo,cc.xyw(2,10,3));

		buts[2] = ButtonTools.macheBut("Nachricht erzeugen und senden", "entlsenden", al);
		pan.add(buts[2],cc.xyw(2, 12, 3, CellConstraints.FILL,CellConstraints.FILL));
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
		String[] headers = {"Anlass","Datum","Bearbeiter"};
		moduebersicht = new MyTermTableModel();
		moduebersicht.setColumnIdentifiers(headers);
		tabuebersicht = new JXTable(moduebersicht);
		tabuebersicht.setHighlighters(HighlighterFactory.createSimpleStriping(Colors.PiOrange.alpha(0.25f)));
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
			buf.append("<tr><td class=\"spalte1\"><font size=\"+1\"><b>"+(reznummer=String.valueOf(Reha.thisClass.patpanel.vecaktrez.get(1))));
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
							"Aufnahmemitteilung","Unterbrechungsmeldung","Entlassmitteilung",
							"E-Bericht","Rechnung"};
					String cmd = "select nachrichttyp,nachrichtdatum,bearbeiter from dtafall where pat_intern='"+
					String.valueOf(Reha.thisClass.patpanel.patDaten.get(29))+
					"' and rez_nr='"+
					String.valueOf(Reha.thisClass.patpanel.vecaktrez.get(1))+
					"' order by nachrichtdatum,id";
					Vector<Vector<String>> vec = SqlInfo.holeFelder(cmd);
					Vector<String> dummy = new Vector<String>();
					String test = null;
					moduebersicht.setRowCount(0);
					System.out.println(vec);
					System.out.println(cmd);
					for(int i = 0;i < vec.size();i++){
						dummy.clear();
						dummy.trimToSize();
						test = vec.get(i).get(0);
						dummy.add(anlass[Integer.parseInt(test)]);
						dummy.add(DatFunk.sDatInDeutsch(vec.get(i).get(1)));
						dummy.add(vec.get(i).get(2));
						moduebersicht.addRow((Vector<String>)dummy.clone());
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
	        head.setTitle("Beginn-Mitteilung für Rehaleistungen (inkl. Nachsorgeleistung)");
	        head.setDescription("<html>Tragen Sie hier bitte das <u>Datum der Eingangsuntersuchung</u> des Patienten sowie die <u>Uhrzeit</u> der Aufnahme ein.</html>");
	        head.setIcon(new ImageIcon(Reha.proghome+"icons/start.jpg"));
	        break;
		case 1:
	        head.setTitle("Unterbrechung eine Reha");
	        head.setDescription("<html><br>Hier melden Sie <u>Reha-Unterbrechungen</u> sowie den <u>Grund der Unterbrechung</u> (z.B.Krankheit.)<br>"+
	        		"Tragen Sie den <u>ersten Fehltag</u> und den <u>letzten Fehltag</u> ein.<br>Bei Bedarf ergänzen Sie die Nachricht durch einen <u>kurzen</u> Text.</html>");
	        head.setIcon(null);
	        break;
		case 2:
	        head.setTitle("Entlass-Mitteilung eines Reha-Patienten");
	        head.setDescription("<html><br><u>Kontrollieren Sie im Rezeptstamm die einzelnen Datumswerte</u> auf Richtigkeit.<br><br>Tragen Sie dann das <u>Datum der Aufnahme</u> und das <u>Datum der Entlassung</u> ein.</html>");
	        head.setIcon(null);
	        break;
		case 3:
	        head.setTitle("Übersicht über alle Meldungen die diesen Fall betreffen");
	        head.setDescription("<html><br>Datmit eine lückenlose Fall-Kontrolle möglich ist können Sie sich hier<br>einen Überblick über die gesamte Kommunikation mit dem jeweiligen Kostenträger verschaffen.</html>");
	        head.setIcon(null);
	        break;
	        
		}
		
		return head;
	}
	private void doRegle301(int art,String id){
		if(art==0){
			RVMeldung301 meldung301 = new RVMeldung301(art,id);
			meldung301.doBeginn(this.beginndatum.getText().trim(),
					this.beginnstunde.getText().trim()+this.beginnminute.getText().trim());
			return;
		}
		if(art==1){
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
			return;
		}
		if(art==2){
			RVMeldung301 meldung301 = new RVMeldung301(art,id);
			meldung301.doEntlassung(this.entlasserstdatum.getText().trim(),
					this.entlassletztdatum.getText().trim(),
					this.entlassstunde.getText().trim()+this.entlassminute.getText().trim(),
					entlassafcombo.getSelectedIndex(),
					entlassartcombo.getSelectedIndex());

			return;
		}

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
					doRegle301(0,SqlInfo.holeEinzelFeld("select id from dta301 where pat_intern='"+patnummer+"' and rez_nr='"+reznummer+"' and nachrichtentyp='1' LIMIT 1"));
					JOptionPane.showMessageDialog(null, "Beginnmitteilung erfolgreich versandt!");
					buts[0].setEnabled(true);
					
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
					buts[0].setEnabled(false);
					doRegle301(1,SqlInfo.holeEinzelFeld("select id from dta301 where pat_intern='"+patnummer+"' and rez_nr='"+reznummer+"' and nachrichtentyp='1' LIMIT 1"));
					JOptionPane.showMessageDialog(null, "Beginnmitteilung erfolgreich versandt!");
					buts[0].setEnabled(true);
					
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
					buts[0].setEnabled(false);
					doRegle301(2,SqlInfo.holeEinzelFeld("select id from dta301 where pat_intern='"+patnummer+"' and rez_nr='"+reznummer+"' and nachrichtentyp='1' LIMIT 1"));
					JOptionPane.showMessageDialog(null, "Beginnmitteilung erfolgreich versandt!");
					buts[0].setEnabled(true);
					
				}catch(Exception ex){
					ex.printStackTrace();
				}
				setCursor(Reha.thisClass.normalCursor);
				return null;
			}
			
		}.execute();
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
