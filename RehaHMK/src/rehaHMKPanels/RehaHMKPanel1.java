package rehaHMKPanels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreePath;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.jdesktop.swingx.treetable.TreeTableModel;

import rehaHMK.RehaHMK;
import rehaHMK.RehaHMKTab;
import CommonTools.JCompTools;
import CommonTools.JRtaComboBox;
import CommonTools.JRtaRadioButton;
import CommonTools.SqlInfo;
import Tools.HMKTreeTableModel;
import Tools.IndiKey;

import Tools.JXHMKTreeTableNode;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.windows.WindowsTabbedPaneUI;





public class RehaHMKPanel1 extends JXPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	RehaHMKTab eltern;
	String[] disziLang = {"Physiotherapie","Massagetherapie","Ergotherapie","Logopädie","Podologie"};
	String[] disziKurz = {"Physio","Massage","Ergo","Logo","Podo"};
	String[] tarifKuerzel = {"kgtarif","matarif","ertarif","lotarif","potarif"};
	String[] disziPraefix = {"2","1","5","3","?"};
	JRtaRadioButton[] rbuts = {null,null,null,null,null};
	JRtaComboBox[] combos = {null,null};
	ButtonGroup bg = new ButtonGroup();
	Vector<Vector<String>> preisliste = new Vector<Vector<String>>();
	ActionListener al = null;
	HyperlinkListener hl = null;
	String aktwebsite = "";
	StringBuffer bufhtmlcode = new StringBuffer();
	JXTable inditab = null;
	IndiTableModel indimod = new IndiTableModel();
	IndiListSelectionHandler indihandler;
	
	JScrollPane htmlScroll = null;
	
	private JXHMKTreeTableNode aktNode;
	private int aktRow;
	private JXHMKTreeTableNode root = null;
	private HMKTreeTableModel hmkTreeTableModel = null;
	private JXTreeTable jXTreeTable = null;
	private JXHMKTreeTableNode foo = null;
	private TreeSelectionListener tsl = null;	
	private int aktindex = 0;	
	
	boolean ready = false;
	
	JEditorPane htmlPane = null;
	
	String aktvorrangig = "";
	String aktergaenzend = "";
 
	JWebBrowser webBrowser = null;
	
	JTabbedPane tpane = null;
	
	JXPanel searchpan = null;
	JXPanel webpan = null;
	
	public RehaHMKPanel1(RehaHMKTab xeltern){
		super(new BorderLayout());
		eltern = xeltern;
		activateListener();
		

		
	    this.setBackgroundPainter(RehaHMK.cp);
		setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		
		tpane = new JTabbedPane();
		tpane.setUI(new WindowsTabbedPaneUI());
		tpane.setOpaque(false);
		tpane.setTabPlacement(3);
		JXPanel searchpan = new JXPanel();
		searchpan.setOpaque(false);
		searchpan.setLayout(new BorderLayout());
		searchpan.add(getContent(),BorderLayout.CENTER);
		searchpan.add(getDiszis(),BorderLayout.EAST);
		searchpan.setBackgroundPainter(RehaHMK.cp);
		/*
		icons.put("browser",new ImageIcon(RehaHMK.progHome+"icons/internet-web-browser.png"));
		icons.put("key",new ImageIcon(RehaHMK.progHome+"icons/entry-pk.gif"));
		icons.put("lupe",new ImageIcon(RehaHMK.progHome+"icons/mag.png"));
		*/
		tpane.addTab("Recherche",RehaHMK.icons.get("key"),(Component)searchpan,"");
		tpane.addTab("Web",RehaHMK.icons.get("browser"),(Component)(webpan = webPan()),"");
		add(tpane, BorderLayout.CENTER);
		
		
		ladehead();
		ladeend();
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					while(!RehaHMK.DbOk){
						Thread.sleep(25);
					}
					fuelleCombos(0);
					ready = true;
					SwingUtilities.invokeLater(new Runnable(){
						public void run(){
							htmlPane.setText(getPiLogo());
							//"http://www.heilmittelkatalog.de/tl_files/hmk/"+"physio/index.htm"
							webBrowser.navigate(RehaHMK.hmkURL+"physio/index.htm");
						}
					});
					
				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}
			
		}.execute();

	}
	private JPanel getDiszis(){
		String xwerte = "10dlu,80dlu,10dlu";
		//                1    2  3   4  5   6  7   8  9   10 11 //12 13  14 15 16 17  18
		String ywerte = "5dlu,p,5dlu,p,5dlu,p,5dlu,p,5dlu,p,20dlu,p,2dlu,p,5dlu,p,2dlu,p";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		PanelBuilder pan = new PanelBuilder(lay);
		pan.getPanel().setBorder(BorderFactory.createLineBorder(Color.BLACK));
		pan.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		
		//pan.setBackground(Color.WHITE);
		for(int i = 0; i < 5; i++){
			rbuts[i] = new JRtaRadioButton(disziLang[i]);
			rbuts[i].setOpaque(false);
			rbuts[i].setActionCommand("radio-"+Integer.toString(i));
			rbuts[i].addActionListener(al);
			pan.add(rbuts[i],cc.xy(2 ,(i*2)+2) );
			bg.add(rbuts[i]);
		}
		rbuts[0].setSelected(true);
		combos[0] = new JRtaComboBox();
		combos[1] = new JRtaComboBox();
		combos[0].setActionCommand("sucheIndi");
		combos[1].setActionCommand("sucheIndi");
		pan.addLabel("<html>Vorrangiges<br>Heilmittel</html>",cc.xy(2,12));
		pan.add(combos[0],cc.xy(2,14));
		pan.addLabel("<html>Ergänzendes<br>Heilmittel</html>",cc.xy(2,16));
		pan.add(combos[1],cc.xy(2,18));

		
		pan.getPanel().validate();
		
		return pan.getPanel();
	}
	private JPanel getContent(){
		//                1    2    3   4     5   6  7     8     9         10   11
		String xwerte = "0dlu,p,5dlu,150dlu,2dlu:g,p,5dlu,150dlu,0dlu";
		//                1    2  3   4  5     6      7     8              9
		String ywerte = "0dlu,p,0dlu,p,0dlu,200dlu,5dlu,fill:0:grow(1.0),0dlu";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		PanelBuilder pan = new PanelBuilder(lay);
		pan.getPanel().setBorder(BorderFactory.createLineBorder(Color.BLACK));
		CellConstraints cc = new CellConstraints();
		pan.getPanel().setOpaque(false);

		/*  normale Tabelle
		indimod.setColumnIdentifiers(new String[] {"Indi.Schl�ssel","Gesamtmenge",
				"max.pro Rezept","Vorrangiges HM","max.Vorrangig","Erg�nzend. HM","max.Erg�nzend."});
		inditab = new JXTable(indimod);
		inditab.setHighlighters(HighlighterFactory.createSimpleStriping(new Color(204,255,255)));
		inditab.getSelectionModel().addListSelectionListener( (indihandler=new IndiListSelectionHandler()));		
		JScrollPane jscr = JCompTools.getTransparentScrollPane(inditab);
		jscr.validate();
		pan.add(jscr,cc.xyw(2,6,7));
		anhaengen();
		*/
		
		/*  JXTreeTable
		root = new JXHMKTreeTableNode("passende Indi-Schl�ssel",null, true);
        hmkTreeTableModel = new HMKTreeTableModel(root);
        jXTreeTable = new JXTreeTable(hmkTreeTableModel);
        jXTreeTable.setOpaque(true);
        jXTreeTable.setRootVisible(true);
        jXTreeTable.setTreeCellRenderer(new HMKTreeCellRenderer());
        tsl = new HMKTreeSelectionListener();
        jXTreeTable.addTreeSelectionListener(tsl);
        jXTreeTable.setSelectionMode(0);
		JScrollPane jscr = JCompTools.getTransparentScrollPane(jXTreeTable);
		jscr.validate();
        pan.add(jscr,cc.xyw(2,6,7));
		*/
		
		htmlPane = new JEditorPane(/*initialURL*/);
        htmlPane.setContentType("text/html");
        htmlPane.setEditable(false);
        htmlPane.setOpaque(false);
        htmlPane.addHyperlinkListener(hl);
        htmlScroll = JCompTools.getTransparentScrollPane(htmlPane);
        htmlScroll.getVerticalScrollBar().setUnitIncrement(15);
        htmlScroll.validate();
        pan.add(htmlScroll,cc.xywh(2,6,7,3,CellConstraints.FILL,CellConstraints.FILL));
		
   		//pan.add(webBrowser,cc.xyw(2,8,7,CellConstraints.FILL,CellConstraints.FILL));
		pan.getPanel().validate();
   		
   		if(!ready){

   		}
		return pan.getPanel();
	}
	private void anhaengen(){
	}
	private JXPanel webPan(){
		JXPanel web = new JXPanel();
		web.setLayout(new BorderLayout());
		web.setOpaque(false);
		webBrowser = new JWebBrowser();
		webBrowser.setStatusBarVisible(false);
		webBrowser.setMenuBarVisible(false);
		webBrowser.setButtonBarVisible(false);
		webBrowser.setLocationBarVisible(false);
   		webBrowser.setDoubleBuffered(true);
		web.add(webBrowser,BorderLayout.CENTER);
   		webBrowser.validate();
   		web.validate();
   		return web;
	}
	private void activateListener(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				String cmd = e.getActionCommand();
				if(cmd.startsWith("radio-")){
					aktindex = Integer.parseInt(cmd.split("-")[1]);
					fuelleCombos(Integer.parseInt(cmd.split("-")[1]));
					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() throws Exception {
							try{
								String diszi = "";
								if(aktindex <= 1 || aktindex == 4){
									diszi = "physio";
								}else{
									diszi = disziKurz[aktindex].toLowerCase()	;
								}
								
								//final String url = "http://www.heilmittelkatalog.de/"+diszi+"/index.html";
								final String url = RehaHMK.hmkURL+diszi+"/index.htm";
								//System.out.println("angestossen über Radiobuttons: "+url);
								SwingUtilities.invokeLater(new Runnable(){
									public void run(){
										webBrowser.navigate(url);
										htmlPane.setText(getPiLogo());
									}
								});
							}catch(Exception ex){
								ex.printStackTrace();
							}
							return null;
						}
						
					}.execute();
				}else if(cmd.equals("sucheIndi")){
					sucheIndis();
				}
			}
		};
		hl = new HyperlinkListener(){
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				String url = "";
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				
					url = e.getURL().toString();
					char c = url.charAt(url.length()-1);
					if(Character.isLowerCase(c)){
						url = url.substring(7,url.length()-1);
					}else{
						url = url.substring(7);
					}
					url = RehaHMK.hmkURL+aktwebsite.toLowerCase()+"/"+url.toLowerCase()+".htm";
					//url = "http://www.heilmittelkatalog.de/"+aktwebsite.toLowerCase()+"/"+url.toLowerCase()+".htm";
					//System.out.println(url);
					//System.out.println("url="+e.getURL());
					//htmlPane.setPage(url);
					webBrowser.navigate(url);
					tpane.setSelectedIndex(1);
				}	
			}
		};
	}
	private void deactivateListener(){
		for(int i = 0; i < 5; i++){
			rbuts[i].removeActionListener(al);
		}
		htmlPane.removeHyperlinkListener(hl);
	}
	private void fuelleCombos(int disziplin){
		/*
		jXTreeTable.removeTreeSelectionListener(tsl);
		while( root.getChildCount() > 0){
			hmkTreeTableModel.removeNodeFromParent((MutableTreeTableNode) root.getChildAt(0));
		}
		*/

		combos[0].removeActionListener(al);
		combos[1].removeActionListener(al);
		String diszi = tarifKuerzel[disziplin];
		String nummer = Integer.toString(RehaHMK.pgReferenz.get(disziKurz[disziplin]));
		String stmt = "select * from "+diszi+nummer;
		preisliste.clear();
		preisliste = SqlInfo.holeFelder(stmt);
		combos[0].setDataVectorWithStartElement(preisliste, 0, 2, "./.");
		combos[1].setDataVectorWithStartElement(preisliste, 0, 2, "./.");
		combos[0].addActionListener(al);
		combos[1].addActionListener(al);
		//inditab.getSelectionModel().removeListSelectionListener(indihandler);
		aktwebsite = (disziKurz[disziplin].equals("Massage") ? "physio" : disziKurz[disziplin].toUpperCase());
		//indimod.setRowCount(0);
		//inditab.repaint();
		if(ready){
			webBrowser.setHTMLContent(getPiLogo());
		}else{
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					webBrowser.setHTMLContent(getPiLogo());					
				}
			});
		}
		//jXTreeTable.addTreeSelectionListener(tsl);
		//inditab.getSelectionModel().addListSelectionListener(indihandler);
		
	}
	private void sucheIndis(){
		/*indimod.setRowCount(0);
		jXTreeTable.removeTreeSelectionListener(tsl);
		while( root.getChildCount() > 0){
			hmkTreeTableModel.removeNodeFromParent((MutableTreeTableNode) root.getChildAt(0));
		}
		*/
		String[] suchen = {combos[0].getSecValue().toString(),combos[1].getSecValue().toString()};
		String cmd = "";
		if(suchen[0].equals("")){
			JOptionPane.showMessageDialog(null, "Depp");
			return;
		}
		aktvorrangig = disziPraefix[aktindex]+suchen[0].substring(1);
		aktergaenzend = (suchen[1].equals("") ? "" : disziPraefix[aktindex]+suchen[1].substring(1));
		//System.out.println(aktvorrangig + " / "+aktergaenzend);
		cmd = "select * from hmrcheck where vorrangig like '%"+suchen[0].substring(1)+"%'";
		cmd = cmd+(!suchen[1].equals("") ? " and ergaenzend like '%"+suchen[1].substring(1)+"%'" : "");
		cmd = cmd+" order by indischluessel";
		
		Vector<Vector<String>> vec = SqlInfo.holeFelder(cmd);

		bufhtmlcode.setLength(0);
		bufhtmlcode.trimToSize();
		bufhtmlcode.append(this.bufhead);
		String tdclass = "";
		String tdclassklein = "";
		String aclass = "";
		bufhtmlcode.append("<table width='100%'>");
		for(int i = 0; i < vec.size();i++){
			if(i%2 == 0){
				tdclass = "itemeven";
				tdclassklein = "itemkleineven";
				aclass = "even";
			}else{
				tdclass = "itemodd";
				tdclassklein = "itemkleinodd";
				aclass = "odd";
			}
			bufhtmlcode.append("<tr><td class='"+tdclass+"'><a class='"+aclass+"' href='http://"+vec.get(i).get(0)+"'>"+vec.get(i).get(0)+"</a></td>");
			bufhtmlcode.append("<td class='"+tdclass+"'>");
			bufhtmlcode.append("<table>");
			bufhtmlcode.append("<tr><td>max. pro Fall = "+vec.get(i).get(1)+"</td></tr>");
			bufhtmlcode.append("<tr><td>max. pro Rezept = "+vec.get(i).get(2)+"</td></tr>");
			bufhtmlcode.append("</table>");

			Vector<String> vecx = holeHeilmittel(true,vec.get(i),vec.get(i).get(1));
			if(vecx.size() > 0){
				bufhtmlcode.append("<td class='"+tdclass+"'>");
				bufhtmlcode.append("<ul class='paeb'>");
				for(int i1 = 0;i1 < vecx.size();i1++){
					bufhtmlcode.append("<li>"+vecx.get(i1)+"</li>");
				}
				bufhtmlcode.append("</ul>");
				bufhtmlcode.append("</td>");
			}
			vecx = holeHeilmittel(false,vec.get(i),vec.get(i).get(1));
			if(vecx.size() > 0){
				bufhtmlcode.append("<td class='"+tdclass+"'>");
				bufhtmlcode.append("<ul class='paeb'>");
				for(int i1 = 0;i1 < vecx.size();i1++){
					bufhtmlcode.append("<li>"+vecx.get(i1)+"</li>");
				}
				bufhtmlcode.append("</ul>");
				bufhtmlcode.append("</td>");
			}

			bufhtmlcode.append("</tr>");
		}
		bufhtmlcode.append("</table>");
		bufhtmlcode.append(this.bufend);
		this.htmlPane.setText(bufhtmlcode.toString());
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				htmlScroll.validate();
				JViewport jv = htmlScroll.getViewport();  
				jv.setViewPosition(new Point(0,0));  
			};
		});
		//htmlScroll.scrollRectToVisible(new Rectangle(0,0,0,0));
		//this.htmlPane.repaint();
		/*
		for(int i = 0; i < vec.size();i++){
			indimod.addRow(vec.get(i));
		}
		inditab.repaint();
		*/
		/*
		JXHMKTreeTableNode node = null;
		for(int i = 0; i < vec.size();i++){
			IndiKey key = new IndiKey(vec.get(i).get(0),vec.get(i).get(1)+" max. pro Fall","","",i);
			node = new JXHMKTreeTableNode("WS2a",key,true);
			node = machDenRest(node,vec.get(i),i);
			
			this.hmkTreeTableModel.insertNodeInto(node, root, root.getChildCount());
		}
		jXTreeTable.validate();

		jXTreeTable.addTreeSelectionListener(tsl);
		if(root.getChildCount() > 0){
			jXTreeTable.addRowSelectionInterval(0, 0);			
		}
		//jXTreeTable.expandAll();
		 */
	}
	private Vector<String> holeHeilmittel(boolean vorrang, Vector<String> vec1,String maxfall){
		String[] heilmittel;
		String[] anzahlheilmittel;
  
		if(vorrang){
			heilmittel =  vec1.get(3).split("@");
			anzahlheilmittel =  vec1.get(4).split("@");
		}else{
			heilmittel =  vec1.get(5).split("@");
			anzahlheilmittel =  vec1.get(6).split("@");
		}
		String hmpos = "";
		String id = "";
		Vector<String> retvec = new Vector<String>();
		boolean gleich = false;
		if(heilmittel.length==1){
			if(heilmittel[0].trim().equals("")){
				retvec.add((!vorrang ? "kein ergänzendes Heilmittel erlaubt" : "kein Heilmittel vorgesehen") );
				return retvec;
			}
		}
		for(int i = 0; i < heilmittel.length;i++){
			//System.out.println(maxfall + " / "+anzahlheilmittel[i]);
			hmpos = disziPraefix[aktindex]+heilmittel[i];
			if(hmpos.equals( (vorrang ? this.aktvorrangig : this.aktergaenzend))){
				gleich = true;
			}else{
				gleich = false;
			}
			id = getIDFromPos(hmpos,"",this.preisliste);
			retvec.add((gleich ? "<b><u>" : "")+
					hmpos+" - "+getLangtextFromID(id,"",this.preisliste)+" max. "+
					(! anzahlheilmittel[i].equals(maxfall) ? "<span style='color:#FF0000;'>"+anzahlheilmittel[i]+"</span>" : anzahlheilmittel[i])+
					(gleich ? "</u></b>" : ""));
		}
		return retvec;
	}
	/*******************************************/
	private JXHMKTreeTableNode machDenRest(JXHMKTreeTableNode node,Vector<String> vec,int durchlauf){
		boolean alwayskey = true;
		String nofolge = "";
		if( vec.get(1).equals(vec.get(2))){
			nofolge = "keine Folge-VO";
		}
		IndiKey key = new IndiKey((alwayskey ? vec.get(0) : ""),"<html>"+vec.get(2)+" max. pro Rezept "+
				(nofolge.equals("") ? "" : "(<b><font color='#ff0000'>"+nofolge+"</font></b>)" )+"</html>","","",durchlauf);
		JXHMKTreeTableNode xnode = new JXHMKTreeTableNode("nixgibts",key,true);
		node.insert((MutableTreeTableNode)xnode,node.getChildCount());

		//
		String[] vorrangig =  vec.get(3).split("@");
		String[] anzahlvorrangig =  vec.get(4).split("@");
		String[] ergaenzend =  vec.get(5).split("@");
		String[] anzahlergaenzend =  vec.get(6).split("@");
		JXHMKTreeTableNode xnode2 = null;
		JXHMKTreeTableNode xnode3 = null;
		JXHMKTreeTableNode dummynode = null;
		String hmpos = "";
		String id = "";
		for(int i = 0; i < vorrangig.length;i++){
			hmpos = disziPraefix[aktindex]+vorrangig[i];
			id = getIDFromPos(hmpos,"",this.preisliste);
			key = new IndiKey((alwayskey ? vec.get(0) : ""),"",hmpos+" - "+
					getLangtextFromID(id,"",this.preisliste)+" max. "+anzahlvorrangig[i] ,"",durchlauf);
			if(i==0){
				xnode2 = new JXHMKTreeTableNode("xds",key,true);
			}else{
				dummynode = new JXHMKTreeTableNode("xds",key,true);
				xnode2.insert(dummynode, xnode2.getChildCount());
			}
			
		}
		node.insert(xnode2, node.getChildCount());
		for(int i = 0; i < ergaenzend.length;i++){
			hmpos = disziPraefix[aktindex]+ergaenzend[i];
			id = getIDFromPos(hmpos,"",this.preisliste);
			key = new IndiKey((alwayskey ? vec.get(0) : ""),"","" ,hmpos+" - "+
					getLangtextFromID(id,"",this.preisliste)+" max. "+anzahlergaenzend[i],durchlauf);
			if(i==0){
				xnode3 = new JXHMKTreeTableNode("xds",key,true);
			}else{
				dummynode = new JXHMKTreeTableNode("xds",key,true);
				xnode3.insert(dummynode, xnode3.getChildCount());
			}
		}
		if(xnode3 != null){
			node.insert(xnode3, node.getChildCount());	
		}
		return node;
	}
	public static String getIDFromPos(String pos,String preisgruppe,Vector<Vector<String>> vec){
		int lang = vec.size(),i;
		int idpos = vec.get(0).size()-1;
		String ret = "-1";
		for(i = 0; i < lang;i++){
			if( vec.get(i).get(2).equals(pos)){
				ret = vec.get(i).get(idpos).toString();
				break;
			}
		}
		return ret;
	}
	
	public static String getLangtextFromID(String id,String preisgruppe,Vector<Vector<String>> vec){
		int lang = vec.size(),i;
		int idpos = vec.get(0).size()-1;
		String ret = "kein Langtext vorhanden";
		for(i = 0; i < lang;i++){
			if( vec.get(i).get(idpos).equals(id)){
				ret = vec.get(i).get(0).toString();
				break;
			}
		}
		return ret;
	}
	/********************************************************/
	private void regleHMKSeite(int i){
		String indi = inditab.getValueAt(i,0).toString();
		char c = indi.charAt(indi.length()-1);
		if(Character.isLowerCase(c)){
			indi = indi.substring(0,indi.length()-1);
		}
		//http://www.heilmittelkatalog.de/tl_files/hmk/physio/ws1.htm
		String url = RehaHMK.hmkURL+aktwebsite.toLowerCase()+"/"+indi.toLowerCase()+".htm";
		//String url = "http://www.heilmittelkatalog.de/"+aktwebsite.toLowerCase()+"/"+indi.toLowerCase()+".htm";
		//htmlPane.setPage(url);
		webBrowser.navigate(url);
	}
	
	class IndiTableModel extends DefaultTableModel{
		   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Class<?> getColumnClass(int columnIndex) {
			/*
			if(columnIndex==0){return String.class;}
			if(columnIndex==1){return Boolean.class;}
			if(columnIndex==2){return Date.class;}
			if(columnIndex==3){return Timestamp.class;}
			if(columnIndex==4){return String.class;}
			if(columnIndex==5){return String.class;}
			*/
		   return String.class;
	    }

		public boolean isCellEditable(int row, int col) {
			return false;
		}
		   
	}
	class IndiListSelectionHandler implements ListSelectionListener {
	    public void valueChanged(ListSelectionEvent e) {
	        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
	        boolean isAdjusting = e.getValueIsAdjusting();
	        if(isAdjusting){
	        	return;
	        }
	        if (lsm.isSelectionEmpty()) {

	        } else {
	            int minIndex = lsm.getMinSelectionIndex();
	            int maxIndex = lsm.getMaxSelectionIndex();
	            for (int i = minIndex; i <= maxIndex; i++) {
	                if (lsm.isSelectedIndex(i)) {
	                	final int ix = i;
	            		SwingUtilities.invokeLater(new Runnable(){
	            			public void run(){
	    	            		regleHMKSeite(ix);	            				
	            			}
	            		});
	                    break;
	                }
	            }
	        }

	    }
	}
	private String getPiLogo(){
		StringBuffer html = new StringBuffer();
			html.append("<html><body><CENTER><div style='margin-top:100px;'>"+"<img src=file:///"+RehaHMK.progHome+"icons/TPorg.png>" +"</div></CENTER></body></html>");
			html.append("<html><body>");
			html.append("<table width='100%' height='100%' border='0' cellpadding='0' cellspacing='0'>");
			html.append("<tr>");
			html.append("<td align='center' valign='middle'>");
			html.append("<img src=file:///"+RehaHMK.progHome+"icons/TPorg.png>");
			html.append("</td></td></table>");
			return html.toString();	
	}
	/******************************************************
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 *
	 */
	private int getNodeCount(){
		int ret = 0; 
		int  rootAnzahl;
		int  kindAnzahl;
		JXHMKTreeTableNode rootNode;
		//JXTTreeTableNode childNode;
		rootAnzahl =  root.getChildCount();
		if(rootAnzahl<=0){return 0;}
		//int geprueft = 0;
		for(int i = 0; i < rootAnzahl;i++){
			rootNode = (JXHMKTreeTableNode) root.getChildAt(i);
			ret += 1;
			if( (kindAnzahl = rootNode.getChildCount())>0){
				ret+=kindAnzahl;
			}
		}
		return ret;
	}
	private JXHMKTreeTableNode holeNode(int zeile){
		
		JXHMKTreeTableNode node = null;
		int  rootAnzahl;
		int  kindAnzahl;
		JXHMKTreeTableNode rootNode;
		JXHMKTreeTableNode childNode;
		rootAnzahl =  root.getChildCount();
		if(rootAnzahl<=0){return node;}
		int geprueft = 0;
		for(int i = 0; i < rootAnzahl;i++){
			
			rootNode = (JXHMKTreeTableNode) root.getChildAt(i);
			//////System.out.println("Anzahl Root-Knoten = "+rootAnzahl);
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
				//////System.out.println("Anzahl Kind-Knoten = "+kindAnzahl);
				geprueft++;
				for(int i2 = 0; i2 < kindAnzahl;i2++){
					
					if(geprueft==zeile){
						childNode = (JXHMKTreeTableNode) rootNode.getChildAt(i2);
						//////System.out.println("Zeile gefunden geprueft wurden "+geprueft);
						return childNode;
					}else{
						childNode = (JXHMKTreeTableNode) rootNode.getChildAt(i2);
						//////System.out.println("Zeile �berp�ft="+geprueft+" - "+childNode.abr.datum+" - "+childNode.abr.bezeichnung);
						geprueft ++;						
					}

				}
			}else{
				//////System.out.println("Keine Bedingung trifft zu="+geprueft);
				geprueft++;	
			}
		}
		
		return node;
		
	}


	class HMKTreeSelectionListener implements TreeSelectionListener {
		boolean isUpdating = false;
		
		public void valueChanged(TreeSelectionEvent e) {
			if (!isUpdating) {
				/******/
				try{
				isUpdating = true;
				JXTreeTable tt = jXTreeTable;//(JXTreeTable) e.getSource();
				TreeTableModel ttmodel = tt.getTreeTableModel();
				TreePath[] selpaths = tt.getTreeSelectionModel().getSelectionPaths();
				
				if (selpaths !=null) {
					ArrayList<TreePath> selPathList = new ArrayList<TreePath>(Arrays.asList(selpaths));
					int i=1;
					while(i<=selPathList.size()) {
						//add all kiddies.
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
					aktNode =  (JXHMKTreeTableNode) tp.getLastPathComponent();//selpaths[selpaths.length-1].getLastPathComponent();
					new SwingWorker<Void,Void>(){
						protected Void doInBackground() throws Exception {
							try{
							int lang = getNodeCount();
							aktRow = -1;
							for(int i = 0; i < lang; i++){
								if(aktNode == holeNode(i)){
									aktRow = i;
									String indi = aktNode.key.indischl;
									char c = indi.charAt(indi.length()-1);
									if(Character.isLowerCase(c)){
										indi = indi.substring(0,indi.length()-1);
									}
									// http://www.heilmittelkatalog.de/tl_files/hmk/physio/ws1.htm
									final String url = RehaHMK.hmkURL+aktwebsite.toLowerCase()+"/"+indi.toLowerCase()+".htm";
									//final String url = "http://www.heilmittelkatalog.de/"+aktwebsite.toLowerCase()+"/"+indi.toLowerCase()+".htm";
									//htmlPane.setPage(url);
									//System.out.println(url);
									SwingUtilities.invokeLater(new Runnable(){
										public void run(){
											webBrowser.navigate(url);
											
										}
									});
									//System.out.println("Zeilennummer =  = "+i);
									//////System.out.println("Zeilennummer =  = "+i);
									//////System.out.println("Node selektiert = "+aktNode.abr.bezeichnung);
									//////System.out.println("Behandlungsdatum selektiert = "+aktNode.abr.datum+" / "+aktNode.abr.bezeichnung);
									break;
								}
							}
							}catch(Exception ex){
								ex.printStackTrace();
							}
							return null;
						}
						
					}.execute();
				}
				/**********/
			}catch(NullPointerException ex){}	
				
				/**********/
			}
			isUpdating = false;

		}
		
		
	 
	}
	/******************************************************************/
	final StringBuffer  bufhead = new StringBuffer();
	final StringBuffer  bufend = new StringBuffer();
	public void ladehead(){
		bufhead.append("<html><head>");
		bufhead.append("<STYLE TYPE=\"text/css\">");
		bufhead.append("<!--");
		bufhead.append("A{text-decoration:none;background-color:transparent;border:none}");
		bufhead.append("A.even{text-decoration:underline;color: #000000; background-color:transparent;border:none}");
		bufhead.append("A.odd{text-decoration:underline;color: #FFFFFF;background-color:transparent;border:none}");
		bufhead.append("TD{font-family: Arial; font-size: 12pt; vertical-align: top;}");
		bufhead.append("TD.inhalt {font-family: Arial, Helvetica, sans-serif; font-size: 20px;background-color: #7356AC;color: #FFFFFF;}");
		bufhead.append("TD.inhaltinfo {font-family: Arial, Helvetica, sans-serif; font-size: 20px;background-color: #DACFE7; color: #1E0F87;}");
		bufhead.append("TD.headline1 {font-family: Arial, Helvetica, sans-serif; font-size: 14px; background-color: #EADFF7; color: #000000;}");
		bufhead.append("TD.headline2 {font-family: Arial, Helvetica, sans-serif; font-size: 14px; background-color: #DACFE7; color: #000000;}");		
		bufhead.append("TD.headline3 {font-family: Arial, Helvetica, sans-serif; font-size: 14px; background-color: #7356AC; color: #FFFFFF;}");		
		bufhead.append("TD.headline4 {font-family: Arial, Helvetica, sans-serif; font-size: 14px; background-color: #1E0F87; color: #FFFFFF;}");		
		bufhead.append("TD.itemeven {font-family: Arial, Helvetica, sans-serif; font-size: 14px; background-color: #E6E6E6; color: #000000;}");
		bufhead.append("TD.itemodd {font-family: Arial, Helvetica, sans-serif; font-size: 14px; background-color: #737373; color: #F0F0F0;}");
		bufhead.append("TD.itemkleineven {font-family: Arial, Helvetica, sans-serif; font-size: 9px; background-color: #E6E6E6; color: #000000;}");
		bufhead.append("TD.itemkleinodd {font-family: Arial, Helvetica, sans-serif; font-size: 9px; background-color: #737373; color: #F0F0F0;}");
		bufhead.append("TD.header {font-family: Arial, Helvetica, sans-serif; font-size: 14px; background-color: #1E0F87; color: #FFFFFF;}");
		bufhead.append("UL {font-family: Arial, Helvetica, sans-serif; font-size: 9px;}");
		bufhead.append("UL.paeb { margin-top: 0px; margin-bottom: 0px; }");
		bufhead.append("--->");
		bufhead.append("</STYLE>");
		bufhead.append("</head>");
		bufhead.append("<body>");
		//bufhead.append("<div style=margin-left:30px;>");
		//bufhead.append("<font face=\"Tahoma\"><style=margin-left=30px;>");
		
		

					
	}
	public void ladeend(){
		bufend.append("</body><br></html>");
	}

	
	/******************************************************************/
}
