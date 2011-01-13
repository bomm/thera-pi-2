package dta301;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.windows.WindowsTabbedPaneUI;

import rehaInternalFrame.JDta301Internal;
import systemTools.JCompTools;
import systemTools.StringTools;
import terminKalender.DatFunk;

public class Dta301 extends JXPanel {

	/**
	 * 
	 */
	ActionListener al = null;
	MouseListener tblmouse = null;
	JDta301Internal internal = null;
	JXPanel content = null;
	JTabbedPane tabPan = null;
	JEditorPane fallPan = null;
	boolean is301Ok = false;
	String reznummer = "";
	String patnummer = "";
	
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
				//tfs[0].requestFocus();
			}
		});
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
			patnummer = String.valueOf(Reha.thisClass.patpanel.patDaten.get(29));
			
			
			
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
	private void makeListeners(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("irgendwas")){
					
				}
			}
		};
	}
	private JXPanel getContent(){
		content = new JXPanel(new BorderLayout());
		content.setOpaque(false);
		tabPan = new JTabbedPane();
		tabPan.setUI(new WindowsTabbedPaneUI());
		tabPan.add("Beginn-Mitteilung",getBeginn());
		tabPan.add("Unterbrechung melden",getUnterbrechung());
		tabPan.add("Entlass-Mitteilung",getEntlassung());
		content.add(tabPan,BorderLayout.CENTER);
		content.add(getFallDaten(),BorderLayout.WEST);
		return content;
	}
	
	private JXPanel getBeginn(){
		JXPanel pan = new JXPanel();
		pan.setOpaque(false);
		String xwerte = "";
		String ywerte = "";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		
		pan.validate();
		return pan;
	}
	private JXPanel getEntlassung(){
		JXPanel pan = new JXPanel();
		pan.setOpaque(false);
		String xwerte = "";
		String ywerte = "";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		
		pan.validate();
		return pan;
	}
	private JXPanel getUnterbrechung(){
		JXPanel pan = new JXPanel();
		pan.setOpaque(false);
		String xwerte = "";
		String ywerte = "";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		
		pan.validate();
		return pan;
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
	

}
