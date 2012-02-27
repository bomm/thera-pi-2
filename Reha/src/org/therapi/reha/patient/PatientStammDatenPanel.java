package org.therapi.reha.patient;

import hauptFenster.AktiveFenster;
import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;

import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemTools.JCompTools;
import systemTools.StringTools;
import terminKalender.DatFunk;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import events.PatStammEvent;
import events.PatStammEventClass;


public class PatientStammDatenPanel extends JXPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4929837198414837133L;
	private HyperlinkListener linkListener = null;
	PatientHauptPanel patientHauptPanel = null;
	JEditorPane htmlPane = null;	
	StringBuffer buf1 = new StringBuffer();
	StringBuffer buf2 = new StringBuffer();
	StringBuffer buf3 = new StringBuffer();
	PatientStammDatenLogic stammDatenLogic = null;
	public PatientStammDatenPanel(PatientHauptPanel patHauptPanel){
		super();
		stammDatenLogic = new PatientStammDatenLogic(patHauptPanel,this);
		createLinkListener();
		setLayout(new BorderLayout());
		setOpaque(false);
		this.patientHauptPanel = patHauptPanel;
		
		setBorder(BorderFactory.createEmptyBorder(0, 10, 20, 10));
		
		//getStammDatenPanel();
		//add(getStammDatenPanel(),BorderLayout.CENTER);
		add(getHTMLPanel(),BorderLayout.CENTER);
		setPreferredSize(new Dimension(200,0));
		validate();
	}
	public PatientStammDatenLogic getLogic(){
		return stammDatenLogic;
	}
	public void fireAufraeumen(){
		htmlPane.removeHyperlinkListener(linkListener);
		linkListener = null;
		htmlPane = null;
		stammDatenLogic = null;
	}

	private void createLinkListener(){
		linkListener = new HyperlinkListener(){
			@Override
			public void hyperlinkUpdate(HyperlinkEvent arg0) {
				if(!patientHauptPanel.getLogic().neuDlgOffen){
					stammDatenLogic.reactOnHyperlink(arg0);
				}
			}
		};
	}
	private JScrollPane getHTMLPanel(){
		htmlPane = new JEditorPane(/*initialURL*/);
        htmlPane.setContentType("text/html");
        htmlPane.setEditable(false);
        htmlPane.setOpaque(false);
        htmlPane.addHyperlinkListener(linkListener);
        parseHTML(false);
        JScrollPane jscr = JCompTools.getTransparentScrollPane(htmlPane);
        jscr.validate();
        DropTarget dndt = new DropTarget();
		DropTargetListener dropTargetListener =
			 new DropTargetListener() {
			  public void dragEnter(DropTargetDragEvent e) {}
			  public void dragExit(DropTargetEvent e) {}
			  public void dragOver(DropTargetDragEvent e) {}
			  public void drop(DropTargetDropEvent e) {
				  String mitgebracht = "";
			    try {
			      Transferable tr = e.getTransferable();
			      DataFlavor[] flavors = tr.getTransferDataFlavors();
			      for (int i = 0; i < flavors.length; i++){
			        	mitgebracht  = (String) tr.getTransferData(flavors[i]);
			      }
			      ////System.out.println(mitgebracht);
			      if(mitgebracht.indexOf("°") >= 0){
			    	  if( ! mitgebracht.split("°")[0].contains("TERMDAT")){
			    		  return;
			    	  }
			    	  doPatientDrop(mitgebracht.split("°")[2].trim());
			      }
			      ////System.out.println(mitgebracht+" auf Patientenstamm gedropt");
			    } catch (Throwable t) { t.printStackTrace(); }
			    // Ein Problem ist aufgetreten
			    e.dropComplete(true);
			  }
			  public void dropActionChanged(
			         DropTargetDragEvent e) {}
		};
		try {
			dndt.addDropTargetListener(dropTargetListener);
		} catch (TooManyListenersException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}        
		htmlPane.setDropTarget(dndt);
		return jscr;
	}
	private String makeLink(String toLink,String linkTarget){
		
		return "<a href=\"http://"+linkTarget+".de\">"+toLink+"</a>";
	}
	public void parseHTML(boolean parse){
		if(!parse){
			htmlPane.setText(getLeerHtml());
			return;
		}
		try{
			buf1.setLength(0);
			buf1.trimToSize();
			//String text = 
			buf1.append("<html><head>");
			buf1.append("<STYLE TYPE=\"text/css\">");
			buf1.append("<!--");
			buf1.append("A{text-decoration:none;background-color:transparent;border:none}");
			buf1.append("TD{font-family: Arial; font-size: 12pt; padding-left:5px;padding-right:5px;padding-top:0px,padding-bottom:0px}");
			buf1.append(".spalte1{color:#0000FF;}");
			buf1.append(".spalte2{color:#333333;}");
			buf1.append(".spalte3{color:#000000;}");
			buf1.append("--->");
			buf1.append("</STYLE>");
			buf1.append("</head>");
			buf1.append("<div style=margin-left:5px;>");
			buf1.append("<font face=\"Tahoma\"><style=margin-left=5px;>");
			/*"<br>"+*/
			buf1.append("<table>");
			/*****Rezept****/
			/*******/
			String linktext = "<img src='file:///"+Reha.proghome+"icons/kontact_contacts.png' width=36 height=36 border=0>";
			buf1.append("<tr><td class=\"spalte1\" align=\"left\">"+makeLink(linktext,"FOTO"));;
			//buf1.append("<tr><td class=\"spalte1\" align=\"left\">"+"<img src='file:///"+Reha.proghome+"icons/kontact_contacts.png' width=36 height=36 border=0>");
			buf1.append("</td></tr>" );
			buf1.append("<tr><td class=\"spalte1\" align=\"left\">");
			buf1.append(makeLink(StringTools.EGross(patientHauptPanel.patDaten.get(0).trim()),"ANREDE")+" "+
					makeLink(StringTools.EGross(patientHauptPanel.patDaten.get(1).trim()),"TITEL"));
			buf1.append("</td></tr>" );
			buf1.append("<tr><td class=\"spalte3\" align=\"left\">");
			buf1.append("<b><font color=#000000>"+makeLink(StringTools.EGross(patientHauptPanel.patDaten.get(2)),"N_NAME")+", "+
					makeLink(StringTools.EGross(patientHauptPanel.patDaten.get(3)),"V_NAME")+"</font></b>");
			buf1.append("</td></tr>" );
			buf1.append("<tr><td class=\"spalte1\" align=\"left\">");
			buf1.append("geb.: "+"<b><font color=#000000>"+makeLink(DatFunk.sDatInDeutsch(patientHauptPanel.patDaten.get(4)),"GEBOREN")+"</font></b>");
			buf1.append("</td></tr>" );
			buf1.append("<tr><td class=\"spalte1\" align=\"left\">");
			buf1.append(makeLink(StringTools.EGross(patientHauptPanel.patDaten.get(21)),"STRASSE"));
			buf1.append("</td></tr>" );

			buf1.append("<tr><td class=\"spalte1\" align=\"left\">");
			buf1.append(makeLink(patientHauptPanel.patDaten.get(23),"PLZ")+" "+makeLink(StringTools.EGross(patientHauptPanel.patDaten.get(24)),"ORT"));
			buf1.append("</td></tr>" );

			buf1.append("<tr><td class=\"spalte1\" align=\"left\">");
			buf1.append("&nbsp;");
			buf1.append("</td></tr>" );

			buf1.append("<tr><td class=\"spalte1\" align=\"left\">");
			buf1.append(makeLink("Tel.(p): "+StringTools.EGross(patientHauptPanel.patDaten.get(18)),"TELEFONP"));
			buf1.append("</td></tr>" );
			
			buf1.append("<tr><td class=\"spalte1\" align=\"left\">");
			buf1.append(makeLink("Tel.(g): "+StringTools.EGross(patientHauptPanel.patDaten.get(19)),"TELEFONG"));
			buf1.append("</td></tr>" );
			
			buf1.append("<tr><td class=\"spalte1\" align=\"left\">");
			buf1.append(makeLink("Mobil: "+StringTools.EGross(patientHauptPanel.patDaten.get(20)),"TELEFONM"));
			buf1.append("</td></tr>" );

			buf1.append("<tr><td class=\"spalte1\" align=\"left\">");
			buf1.append(makeLink("Email: "+patientHauptPanel.patDaten.get(50).toLowerCase(),"EMAILA"));
			buf1.append("</td></tr>" );

			buf1.append("<tr><td class=\"spalte1\" align=\"left\">");
			buf1.append("&nbsp;");
			buf1.append("</td></tr>" );
			buf1.append("<tr><td class=\"spalte1\" align=\"left\">");
			buf1.append("<img src='file:///"+Reha.proghome+"icons/krankenkasse.png' width=30 height=30 border=0><b>"+"+"+"</b>");
			buf1.append("<img src='file:///"+Reha.proghome+"icons/system-users.png' width=30 height=30 border=0>");
			buf1.append("</td></tr>" );
			buf1.append("<tr><td class=\"spalte1\" align=\"left\">");
			buf1.append(makeLink(StringTools.EGross(patientHauptPanel.patDaten.get(13)),"KASSE"));
			buf1.append("</td></tr>" );
			buf1.append("<tr><td class=\"spalte1\" align=\"left\">");
			buf1.append(makeLink("Hausarzt: "+StringTools.EGross(patientHauptPanel.patDaten.get(25)),"ARZT"));
			buf1.append("</td></tr>" );
			buf1.append("<tr><td class=\"spalte1\" align=\"left\">");
			buf1.append("&nbsp;");
			buf1.append("</td></tr>" );
			buf1.append("<tr><td class=\"spalte1\" align=\"left\">");
			buf1.append("<img src='file:///"+Reha.proghome+"icons/evolution-addressbook.png' width=32 height=32 border=0>");
			buf1.append("</td></tr>" );
			buf1.append(getAkutDaten());
			buf1.append(getBefreiungsDaten());
			buf1.append("</table>");
			buf1.append("</font>");
			buf1.append("</div>");
			buf1.append("</html>");
			this.htmlPane.setText(buf1.toString());
		}catch(Exception ex){
			ex.printStackTrace();
		}
		((JScrollPane)this.htmlPane.getParent().getParent()).validate();
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				try{
				JViewport vp = ((JScrollPane)htmlPane.getParent().getParent()).getViewport();
				vp.setViewPosition(new Point(0,0));
				((JScrollPane)htmlPane.getParent().getParent()).validate();
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		});
	}
	private String getAkutDaten(){
		buf2.setLength(0);
		buf2.trimToSize();
		String dummy;
		buf2.append("<tr><td class=\"spalte1\" align=\"left\">");
		dummy = patientHauptPanel.patDaten.get(56).trim().replace("<", "&#60;");
		buf2.append(makeLink("Therapeut: "+(dummy.equals("") ? "<font color=#FF0000>k.A.</font>" : dummy.toString()),"THERAPEUT"));
		buf2.append("</td></tr>" );
		if(patientHauptPanel.patDaten.get(33).equals("T")){
			buf2.append("<tr><td class=\"spalte1\" align=\"left\">");
			dummy = patientHauptPanel.patDaten.get(46).trim().replace("<", "&#60;");
			buf2.append(makeLink("Akutpat.: <font color=#FF0000>JA bis "+(dummy.length()==10 ? DatFunk.sDatInDeutsch(dummy.toString()) : "k.A.")+"</font>","AKUTBIS"));
			buf2.append("</td></tr>" );
		}else{
			buf2.append("<tr><td class=\"spalte1\" align=\"left\">");
			buf2.append(makeLink("Akutpat.: Nein","AKUTBIS"));
			buf2.append("</td></tr>" );
		}
		if(! patientHauptPanel.patDaten.get(36).trim().equals("")){
			buf2.append("<tr><td class=\"spalte1\" align=\"left\">");
			buf2.append("<u><i>Terminwünsche</i></u>");
			buf2.append("</td></tr>" );		
			buf2.append("<tr><td class=\"spalte1\" align=\"left\">");
			buf2.append(makeLink(patientHauptPanel.patDaten.get(36).replace("<", "&#60;"),"TERMINE1"));
			buf2.append("</td></tr>" );		
		}
		if(! patientHauptPanel.patDaten.get(37).trim().equals("")){
			buf2.append("<tr><td class=\"spalte1\" align=\"left\">");
			buf2.append(makeLink(patientHauptPanel.patDaten.get(37).replace("<", "&#60;"),"TERMINE2"));
			buf2.append("</td></tr>" );		
		}
		return buf2.toString();
	}
	private String getBefreiungsDaten(){
		buf3.setLength(0);
		buf3.trimToSize();
		String dummy;
		buf3.append("<tr><td class=\"spalte1\" align=\"left\">");
		dummy = patientHauptPanel.patDaten.get(31).trim();
		if(dummy.length()==10){
			buf3.append("<tr><td class=\"spalte1\" align=\"left\">");
			buf3.append(makeLink("Befreiung bis: <font color=#FF0000><b>"+DatFunk.sDatInDeutsch(dummy)+"</b></font>","AKUT_DAT"));
			buf3.append("</td></tr>" );
		}
		dummy = patientHauptPanel.patDaten.get(69).trim();
		if(! dummy.equals("")){
			buf3.append("<tr><td class=\"spalte1\" align=\"left\">");
			buf3.append(makeLink("Vorjahr befreit: <font color=#FF0000><b>"+(dummy.equals(SystemConfig.vorJahr) ? "JA" : "NEIN")+"</b></font>","VORJAHR"));
			buf3.append("</td></tr>" );
		}
		//System.out.println(dummy);
		return buf3.toString();
	}
	private String getLeerHtml(){
		buf3.setLength(0);
		buf3.trimToSize();
		buf3.append("<html>");
		buf3.append("<div style=margin-left:5px;>");
		buf3.append("<font face=\"Tahoma\"><style=margin-left=5px;>");
			/*"<br>"+*/
		buf2.append("<table>");
			/*****Rezept****/
			/*******/
		buf2.append("<tr><td class=\"spalte1\" align=\"left\">"+"<img src='file:///"+Reha.proghome+"icons/kontact_contacts.png' width=52 height=52 border=0>");
		buf2.append("</tr></td>");
		buf2.append("</table>");
		buf2.append("</font>");
		buf2.append("</html>");
		return buf2.toString();
		
	}
 
	@SuppressWarnings("unused")
	private JXPanel getStammDatenPanel(){   //1         2            3       4    5           6           7         8         
		FormLayout lay = new FormLayout("3dlu,right:max(38dlu;p),3dlu,55dlu:g,3dlu,right:max(39dlu;p),3dlu,45dlu:g,5dlu",
				// 1     2  3  4  5  6	7  8  9 10 11 12 13
				"0dlu ,0dlu,p,1px,p,1px,p,1px,p,1px,p,1px,p,"+
				//14 15 16 17 18 19 20 21  22 23 24 25
				"15px,p,1px,p,1px,p,1px,p,15px,p,1px,p");
		JXPanel jpan = new JXPanel();
		jpan.setOpaque(false);
		jpan.setLayout(lay);
		CellConstraints cc = new CellConstraints();
		jpan.add(new JLabel("Anrede / Titel"),cc.xy(2,3));
		patientHauptPanel.ptfield[0] = new JPatTextField("XGROSS",false);
		patientHauptPanel.ptfield[0].setName("ANREDE");
		jpan.add(patientHauptPanel.ptfield[0],cc.xy(4,3));
		
		//add(new JLabel("Titel"),cc.xy(6,3));
		patientHauptPanel.ptfield[1] = new JPatTextField("XGROSS",false);
		patientHauptPanel.ptfield[1].setName("TITEL");
		jpan.add(patientHauptPanel.ptfield[1],cc.xyw(6,3,3));	
		
		jpan.add(new JLabel("Name"),cc.xy(2,5));
		patientHauptPanel.ptfield[2] = new JPatTextField("XGROSS",false);
		patientHauptPanel.ptfield[2].setName("N_NAME");		
		jpan.add(patientHauptPanel.ptfield[2],cc.xyw(4,5,5));		

		jpan.add(new JLabel("Vorname"),cc.xy(2,7));
		patientHauptPanel.ptfield[3] = new JPatTextField("XGROSS",false);
		patientHauptPanel.ptfield[3].setName("V_NAME");		
		jpan.add(patientHauptPanel.ptfield[3],cc.xyw(4,7,5));		

		jpan.add(new JLabel("Geboren"),cc.xy(2,9));
		patientHauptPanel.ptfield[4] = new JPatTextField("DATUM",false); //new JPatTextField("DATUM",true);
		patientHauptPanel.ptfield[4].setName("GEBOREN");		
		jpan.add(patientHauptPanel.ptfield[4],cc.xy(4,9));
		
		jpan.add(new JLabel("Strasse"),cc.xy(2,11));
		patientHauptPanel.ptfield[10] = new JPatTextField("XROSS",false);
		patientHauptPanel.ptfield[10].setName("STRASSE");
		jpan.add(patientHauptPanel.ptfield[10],cc.xyw(4,11,5));		

		jpan.add(new JLabel("PLZ, Ort"),cc.xy(2,13));
		patientHauptPanel.ptfield[11] = new JPatTextField("ZAHLEN",false);
		patientHauptPanel.ptfield[11].setName("PLZ");		
		jpan.add(patientHauptPanel.ptfield[11],cc.xy(4,13));		

		patientHauptPanel.ptfield[12] = new JPatTextField("XGROSS",false);
		patientHauptPanel.ptfield[12].setName("ORT");		
		jpan.add(patientHauptPanel.ptfield[12],cc.xyw(6,13,3));		

		jpan.add(new JLabel("Telefon(p)"),cc.xy(2,15));
		patientHauptPanel.ptfield[6] = new JPatTextField("XGROSS",false);
		patientHauptPanel.ptfield[6].setName("TELEFONP");
		jpan.add(patientHauptPanel.ptfield[6],cc.xyw(4,15,5));		

		jpan.add(new JLabel("Telefon(g)"),cc.xy(2,17));
		patientHauptPanel.ptfield[7] = new JPatTextField("XGROSS",false);
		patientHauptPanel.ptfield[7].setName("TELEFONG");		
		jpan.add(patientHauptPanel.ptfield[7],cc.xyw(4,17,5));		

		jpan.add(new JLabel("Mobil"),cc.xy(2,19));
		patientHauptPanel.ptfield[8] = new JPatTextField("XGROSS",false);
		patientHauptPanel.ptfield[8].setName("TELEFONM");
		//patientHauptPanel.ptfield[8].addMouseListener(patientHauptPanel.ml);		
		jpan.add(patientHauptPanel.ptfield[8],cc.xyw(4,19,5));		

		jpan.add(new JLabel("Email"),cc.xy(2,21));
		patientHauptPanel.ptfield[9] = new JPatTextField("KLEIN",false);
		patientHauptPanel.ptfield[9].setName("EMAILA");
		//patientHauptPanel.ptfield[9].addMouseListener(patientHauptPanel.ml);
		jpan.add(patientHauptPanel.ptfield[9],cc.xyw(4,21,5));	
		
		jpan.add(new JLabel("Krankenkasse"),cc.xy(2,23));
		patientHauptPanel.ptfield[14] = new JPatTextField("XROSS",false);
		patientHauptPanel.ptfield[14].setName("KASSE");
		jpan.add(patientHauptPanel.ptfield[14],cc.xyw(4,23,5));		
		
		jpan.add(new JLabel("Hausarzt"),cc.xy(2,25));
		patientHauptPanel.ptfield[13] = new JPatTextField("XROSS",false);
		patientHauptPanel.ptfield[13].setName("ARZT");
		jpan.add(patientHauptPanel.ptfield[13],cc.xyw(4,25,5));
		
		patientHauptPanel.ptfield[5] = new JPatTextField("XROSS",false);
		patientHauptPanel.ptfield[5].setName("PAT_INTERN");
	    for(int i = 0;i < 		patientHauptPanel.ptfield.length;i++){
	    	patientHauptPanel.ptfield[i].setForeground(Color.BLUE);
	    	patientHauptPanel.ptfield[i].setFont(patientHauptPanel.font);
	    	//patientHauptPanel.ptfield[i].addFocusListener(getTextFieldFocusListener());
	    }
		patientHauptPanel.ptfield[2].setForeground(Color.RED);
		patientHauptPanel.ptfield[3].setForeground(Color.RED);
		patientHauptPanel.ptfield[4].setForeground(Color.RED);
		
		jpan.validate();
		return jpan;
		
	}
	
	private void doPatientDrop(String rez_nr){
		String pat_int = "";
		String reznr = rez_nr;
		boolean inhistorie = false;
		int ind = reznr.indexOf("\\");
		if(ind >= 0){
			reznr = reznr.substring(0,ind);
		}
		
		Vector<String> vec = SqlInfo.holeSatz("verordn", "pat_intern", "rez_nr='"+reznr+"'",(List<?>) new ArrayList<String>() );
		if(vec.size() == 0){
			vec = SqlInfo.holeSatz("lza", "pat_intern", "rez_nr='"+reznr+"'",(List<?>) new ArrayList<String>() );
			if(vec.size() == 0){
				JOptionPane.showMessageDialog(null,"Rezept weder im aktuellen Rezeptstamm noch in der Historie vorhanden!\nIst die eingetragene Rezeptnummer korrekt?");
				return;
			}else{
				JOptionPane.showMessageDialog(null,"Rezept ist bereits abgerechnet und somit in der Historie des Patienten!");
				inhistorie = true;
			}
		}
		vec = SqlInfo.holeSatz("pat5", "pat_intern", "pat_intern='"+vec.get(0)+"'",(List<?>) new ArrayList<String>() );
		if(vec.size() == 0){
			JOptionPane.showMessageDialog(null,"Patient mit zugeordneter Rezeptnummer -> "+reznr+" <- wurde nicht gefunden");
			return;
		}
		pat_int = (String) vec.get(0);
		JComponent patient = AktiveFenster.getFensterAlle("PatientenVerwaltung");
		final String xreznr = reznr;
		final boolean xinhistorie = inhistorie;
		if(patient == null){
			final String xpat_int = pat_int;
			new SwingWorker<Void,Void>(){
				protected Void doInBackground() throws Exception {
					JComponent xpatient = AktiveFenster.getFensterAlle("PatientenVerwaltung");
					Reha.thisClass.progLoader.ProgPatientenVerwaltung(1);
					while( (xpatient == null) ){
						Thread.sleep(20);
						xpatient = AktiveFenster.getFensterAlle("PatientenVerwaltung");
					}
					while(  (!AktuelleRezepte.initOk) ){
						Thread.sleep(20);
					}
					
					String s1 = "#PATSUCHEN";
					String s2 = (String) xpat_int;
					PatStammEvent pEvt = new PatStammEvent(Reha.thisClass.terminpanel);
					pEvt.setPatStammEvent("PatSuchen");
					pEvt.setDetails(s1,s2,"#REZHOLEN-"+xreznr) ;
					PatStammEventClass.firePatStammEvent(pEvt);
					if(xinhistorie){
						Reha.thisClass.patpanel.getTab().setSelectedIndex(1);	
					}else{
						Reha.thisClass.patpanel.getTab().setSelectedIndex(0);
					}

					return null;
				}
				
			}.execute();
		}else{
			Reha.thisClass.progLoader.ProgPatientenVerwaltung(1);
			String s1 = "#PATSUCHEN";
			String s2 = (String) pat_int;
			PatStammEvent pEvt = new PatStammEvent(Reha.thisClass.terminpanel);
			pEvt.setPatStammEvent("PatSuchen");
			pEvt.setDetails(s1,s2,"#REZHOLEN-"+xreznr) ;
			PatStammEventClass.firePatStammEvent(pEvt);
			if(xinhistorie){
				Reha.thisClass.patpanel.getTab().setSelectedIndex(1);	
			}else{
				Reha.thisClass.patpanel.getTab().setSelectedIndex(0);
			}

		}		
	}

	
	
}

