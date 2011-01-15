package dta301;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXPanel;
import org.thera_pi.nebraska.gui.utils.ButtonTools;

import rehaInternalFrame.JDta301Internal;
import sqlTools.SqlInfo;
import systemTools.JCompTools;
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
	
	JRtaTextField beginndatum = null;
	JRtaTextField beginnstunde = null;
	JRtaTextField beginnminute = null;
	
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
				if(!is301Ok){
					JOptionPane.showMessageDialog(null,"Diese Verordnung ist nicht für DTA-301 vorgesehen");
					return;
				}
				String cmd = arg0.getActionCommand();
				if(cmd.equals("beginnsenden")){
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
		String xwerte = "100dlu,right:max(100dlu;p),5dlu,p,20dlu";
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
	private JXPanel getUnterbrechung(){
		JXPanel headerpan = new JXPanel(new BorderLayout());
		headerpan.setOpaque(false);
		//headerpan.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		JXPanel pan = new JXPanel();
		pan.setOpaque(false);
		String xwerte = "";
		String ywerte = "";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		
		pan.validate();
		headerpan.add(getHeader(1),BorderLayout.NORTH);
		headerpan.add(pan,BorderLayout.CENTER);
		return headerpan;
	}
	private JXPanel getEntlassung(){
		JXPanel headerpan = new JXPanel(new BorderLayout());
		headerpan.setOpaque(false);
		//headerpan.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		JXPanel pan = new JXPanel();
		pan.setOpaque(false);
		String xwerte = "";
		String ywerte = "";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		
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
		String xwerte = "";
		String ywerte = "";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		
		pan.validate();
		headerpan.add(getHeader(3),BorderLayout.NORTH);
		headerpan.add(pan,BorderLayout.CENTER);
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
	        head.setDescription("<html><br><u>Kontrollieren Sie die einzelnen Datumswerte</u> auf Richtigkeit.<br><br>Tragen Sie dann das <u>Datum der Entlassung</u> ein.</html>");
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
		}
	}

	
	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
