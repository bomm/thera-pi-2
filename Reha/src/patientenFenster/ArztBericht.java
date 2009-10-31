package patientenFenster;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import oOorgTools.OOTools;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import patientenFenster.PatGrundPanel.PatientAction;

import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import sqlTools.ExUndHop;
import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemTools.AdressTools;
import systemTools.Colors;
import systemTools.JCompTools;
import systemTools.JRtaComboBox;
import systemTools.JRtaTextField;
import systemTools.StringTools;
import terminKalender.ParameterLaden;
import terminKalender.TerminFenster;
import terminKalender.DatFunk;
import textBlockTherapeuten.ThTextBlock;

import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;

public class ArztBericht extends RehaSmartDialog implements RehaTPEventListener,WindowListener, ActionListener{
	private RehaTPEventClass rtp = null;
	private boolean neu;
	private String reznr;
	private int berichtid;
	private int aufrufvon;
	private JXPanel grundPanel;
	private JPanel content;
	private JLabel[] rlab = {null,null,null,null,null,null,null,null,null,null,null,null};
	private JTextArea diagnose;
	private JRtaComboBox tbwahl;
	private JComboBox verfasser;
	public int vorberichtid = -1;
	public boolean vorberichtdiagnose = false;
	int arztid;
	JButton jbutbericht = null;
	public  JRtaTextField[] jtf = {null,null,null};
	private JTextPane[] icfblock = {null,null,null,null};
	private ThTextBlock thblock = null;
	private String disziplin = null;
	private int zuletztaktiv = 0;
	private boolean gespeichert = false;
	private boolean ishmnull = true;
	private String pat_intern = "";
	String altverfasser = "";
	String diag = "";
	int tblreihe;
	String rezdatum = "";
	CompoundPainter cp = null;
	MattePainter mp = null;
	LinearGradientPaint p = null;
	//PinPanel pinPanel = null;
	public ArztBericht(JXFrame owner, String name,boolean bneu,String reznr,int iberichtid,int aufruf,String xverfasser,String xdiag,int row) {
		super(owner, name);
		super.getSmartTitledPanel().setName(name);
		super.getSmartTitledPanel().setTitleForeground(Color.WHITE);
		String xtitel = "<html>Arztbericht erstellen / ändern   -->&nbsp;&nbsp;&nbsp;&nbsp;<b><font color='#ffffff'>Tip:&nbsp;&nbsp;&nbsp;&nbsp;entscheiden Sie sich für das ICF-Schema</font></b>&nbsp;&nbsp;<img src='file:///C:/RehaVerwaltung/icons/Haken_klein.gif'>";
	    super.getSmartTitledPanel().setTitle(xtitel);
		this.setName(name);
		
		pinPanel = new PinPanel();
		pinPanel.getGruen().setVisible(false);
		pinPanel.setName(name);
		setPinPanel(pinPanel);
		super.getPinPanel().setName(name);
		
		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener((RehaTPEventListener) this);

		this.berichtid = iberichtid;
		this.neu = bneu;
		this.reznr = reznr;
		this.aufrufvon = aufruf;
		this.altverfasser = xverfasser;
		this.diag = xdiag;
		this.tblreihe = row;
		this.pat_intern = PatGrundPanel.thisClass.patDaten.get(29);
		/**
		 * 
		 * this.disziplin = this.reznr.substring(0,2);		
		 * hier den Fall für ohne Rezeptbezug einbauen!!!!!
		 * JXFrame owner, String name,boolean bneu,String reznr,int iberichtid,int aufruf,String xverfasser,String xdiag,int row) {
		 */
		System.out.println("In Arztbericht erstellen - ändern -> "+berichtid+" - "+neu+" - "+reznr);
		System.out.println("Die BerichtsID = ----------------->"+iberichtid);
		System.out.println("Die Rezeptnummer = --------------->"+reznr);
		System.out.println("Neuer Bericht = ------------------>"+bneu);
		System.out.println("Der Verfasser = ------------------>"+xverfasser);
		System.out.println("Aufruf aus Fenser Nr. = ---------->"+aufruf);
		System.out.println("Tabellenreihe = ------------------>"+row);

		setSize(new Dimension(950,650));
		
	    grundPanel = new JXPanel(new BorderLayout());
	    new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				/************BackgroundPainter basteln************/
				/*
				Point2D start = new Point2D.Float(0, 0);
				Point2D end = new Point2D.Float(0,getHeight());
			    float[] dist = {0.0f, 0.75f};
			    Color[] colors = {Color.WHITE,Colors.Yellow.alpha(0.25f)};
			    p =
			         new LinearGradientPaint(start, end, dist, colors);
			    mp = new MattePainter(p);
			
			    cp = new CompoundPainter(mp);
				grundPanel.setBackgroundPainter(cp);
				*/
				grundPanel.setBackgroundPainter(Reha.thisClass.compoundPainter.get("ArztBericht"));
			    return null;
			}
	    }.execute();
		content = getBerichtPanel();
		grundPanel.add(content,BorderLayout.CENTER);
		grundPanel.add(getFunktionsPanel(),BorderLayout.WEST);
		getSmartTitledPanel().setContentContainer(grundPanel);	
		new Thread(){
			public void run(){
				
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						fuelleBericht();
						return null;
					}
				}.execute();
			}
		}.start();
		final JXPanel ab = grundPanel;
		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0);
				ab.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "doTextBausteine");
				ab.getActionMap().put("doTextBausteine", new TextBausteine());
				setzeHmAufNull();
				setzeRezDatum();
	   	  	}
		});
		System.out.println("vor Pack");
		pack();
	}
	private void setzeRezDatum(){
		Vector vec = null;
		if(  (vec = SqlInfo.holeSatz("verordn", "rez_datum", "rez_nr='"+this.reznr+"'", Arrays.asList(new String[] {}) )).size() > 0 ){
			rezdatum = (String) vec.get(0);
		}else if((vec = SqlInfo.holeSatz("lza", "rez_datum", "rez_nr='"+this.reznr+"'", Arrays.asList(new String[] {}) )).size() > 0){
			rezdatum = (String) vec.get(0);
		}else{
			rezdatum = "nicht relevant";
		}
		
		//rezdatum = SqlInfo
	}
	private void fuelleBericht(){
		if(this.berichtid <= 0){
			return;
		}
		String felder = "BERSTAND,BERBESO,BERPROG,BERVORS";
		Vector vec = SqlInfo.holeSatz("bericht1",felder , "berichtid='"+Integer.toString(this.berichtid)+"'", Arrays.asList(new String[]{}));
		for(int i = 0;i < 4;i++){
			icfblock[i].setText((String)vec.get(i));
		}
	}
	private JPanel getBerichtPanel(){  // 1   2     3                  4              5              6
		FormLayout lay = new FormLayout("0dlu,p,fill:0:grow(1.00),right:max(40dlu;p),30dlu,right:max(50dlu;p),0dlu",
		//1   2   3       4            5    6   7        8             9  10 11         12          13   14 15       16
		"0dlu,p,2dlu,fill:0:grow(0.27),4dlu,p ,2dlu,fill:0:grow(0.27),4dlu,p,2dlu,fill:0:grow(0.27),4dlu,p,2dlu,fill:0:grow(0.15)");
		Font fon = new Font("Courier", Font.PLAIN,12);
		PanelBuilder pb = new PanelBuilder(lay);
		CellConstraints cc = new CellConstraints();
		pb.getPanel().setOpaque(false);
		pb.getPanel().setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		String lbltext = "<html>1.Block: akuteller Funktionsstatus&nbsp;&nbsp;&nbsp;&nbsp;<b><font color='#e77817'>(ICF - Körperfunktionen und -strukturen)</font></b>";
		JLabel lab = new JLabel(lbltext);
		pb.add(lab,cc.xy(2,2));
		lab = new JLabel("F2 für Textblock-Aufruf");
		pb.add(lab,cc.xy(6,2));
		icfblock[0] = new JTextPane();
		icfblock[0].setFont(fon);
		icfblock[0].setForeground(Color.BLUE);
		JScrollPane span = JCompTools.getTransparentScrollPane(icfblock[0]);
		span.setBorder(BorderFactory.createLineBorder(Colors.PiOrange.alpha(0.25f)));
		pb.add(span,cc.xyw(2,4,5));
		lbltext ="<html>2.Block: weitere therapierelevante Aspekte&nbsp;&nbsp;&nbsp;&nbsp;<b><font color='#e77817'>(ICF - Aktivitäten / Teilhabe)</font></b>";
		lab = new JLabel(lbltext);
		pb.add(lab,cc.xy(2,6));		
		icfblock[1] = new JTextPane();
		icfblock[1].setFont(fon);
		icfblock[1].setForeground(Color.BLUE);
		span = JCompTools.getTransparentScrollPane(icfblock[1]);
		span.setBorder(BorderFactory.createLineBorder(Colors.PiOrange.alpha(0.25f)));		
		pb.add(span,cc.xyw(2,8,5));
		lbltext = "<html>3.Block: prognostische Einschätzung&nbsp;&nbsp;&nbsp;&nbsp;<b><font color='#e77817'>(ICF - Umweltfaktoren)</font></b>";
		lab = new JLabel(lbltext);
		pb.add(lab,cc.xy(2,10));		
		icfblock[2] = new JTextPane();
		icfblock[2].setFont(fon);
		icfblock[2].setForeground(Color.BLUE);
		span = JCompTools.getTransparentScrollPane(icfblock[2]);
		span.setBorder(BorderFactory.createLineBorder(Colors.PiOrange.alpha(0.25f)));
		pb.add(span,cc.xyw(2,12,5));
		lbltext = "<html>4.Block: Reserveblock&nbsp;&nbsp;&nbsp;&nbsp;<b><font color='#e77817'>(ICF - personbezogene Faktoren)</font></b>";
		lab = new JLabel(lbltext);
		pb.add(lab,cc.xy(2,14));		
		icfblock[3] = new JTextPane();
		icfblock[3].setFont(fon);
		icfblock[3].setForeground(Color.BLUE);
		span = JCompTools.getTransparentScrollPane(icfblock[3]);
		span.setBorder(BorderFactory.createLineBorder(Colors.PiOrange.alpha(0.25f)));
		pb.add(span,cc.xyw(2,16,5));
		System.out.println("Rückgabe des JPanels");
		return pb.getPanel();
	}
	private JScrollPane getFunktionsPanel(){

		FormLayout lay = new FormLayout(
        //  1 2  3   4       5
		"2dlu,p,2dlu,30dlu,0dlu",
		// 1  2  3   4  5   6  7    8  9   10  11  12  13   14  15   16  17   18    19   20  21   22  23   24
		"5dlu,p,2dlu,p,0dlu,p,10dlu,p,10dlu,p ,2dlu,p ,1dlu, p,10dlu, p,10dlu, p, 2dlu,  p, 5dlu, p, 1dlu,40dlu,"+
		//25   26  27   28   29  30  31   32  33  34 35  36  37   38 39  40 41   42   43 44 45  46   
		"10dlu, p,10dlu, p, 2dlu,p, 10dlu,p,10dlu,p,2dlu, p,10dlu,p,20dlu,p,5dlu,p, 5dlu,p,10dlu,p"
		);
		PanelBuilder pb = new PanelBuilder(lay);
		CellConstraints cc = new CellConstraints();
		pb.getPanel().setOpaque(false);
		pb.getPanel().setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));
		JLabel lab = new JLabel("Arzbericht für Patient:");
		pb.add(lab,cc.xy(2,2));

		String name = (String)PatGrundPanel.thisClass.patDaten.get(2)+", "+(String)PatGrundPanel.thisClass.patDaten.get(3); 
		rlab[0] = new JLabel(name);
		rlab[0].setForeground(Color.BLUE);
		pb.add(rlab[0],cc.xy(2, 4));
		rlab[1] = new JLabel(DatFunk.sDatInDeutsch((String)PatGrundPanel.thisClass.patDaten.get(4)));
		rlab[1].setForeground(Color.BLUE);
		pb.add(rlab[1],cc.xy(2,6));

		pb.addSeparator("",cc.xyw(2,8,3));
		
		lab = new JLabel("Berichtsempfänger");
		pb.add(lab,cc.xy(2,10));
		// hier testen ob ohne Rezeptbezug, wenn ja kann der Vector nicht verwendet werden
		if((! this.reznr.equals("")) && (this.aufrufvon < 1)){
			name = (String)PatGrundPanel.thisClass.vecaktrez.get(15);
			arztid = new Integer((String)PatGrundPanel.thisClass.vecaktrez.get(16));
		}else if((! this.reznr.equals("")) && (this.aufrufvon == 1)){
			name = (String)PatGrundPanel.thisClass.patDaten.get(25);
			try{
				arztid = new Integer((String)PatGrundPanel.thisClass.patDaten.get(26));
			}catch(java.lang.NumberFormatException ex){
				arztid = new Integer(-1);
			}
		}else if((this.reznr.equals("")) && (this.aufrufvon < 3)){
			name = (String)PatGrundPanel.thisClass.patDaten.get(25);
			try{
				arztid = new Integer((String)PatGrundPanel.thisClass.patDaten.get(26));
			}catch(java.lang.NumberFormatException ex){
				arztid = new Integer(-1);
			}
		}else{
			Vector vec;
			name =  (String)(vec = SqlInfo.holeSatz("berhist", "empfaenger,empfid", "berichtid='"+Integer.toString(this.berichtid)+"'", Arrays.asList(new String[] {}))).get(0) ;
			arztid =StringTools.ZahlTest((String) vec.get(1));						
		}
		rlab[2] = new JLabel(name);
		rlab[2].setForeground(Color.BLUE);
		pb.add(rlab[2],cc.xy(2,12));
		
		JButton jbut = new JButton("Empfänger ändern");
		jbut.setActionCommand("neuerempfaenger");
		jbut.addActionListener(this);
		pb.add(jbut,cc.xy(2, 14));
		
		pb.addSeparator("",cc.xyw(2,16,3));
		
		pb.addLabel("Rezeptnummer",cc.xy(2,18));
		name = "";
		if(! this.reznr.equals("")){
			name = this.reznr;
			this.disziplin = name.substring(0,2);
		}else{
			name = "ohne Rezeptbezug";
		}
		rlab[3] = new JLabel(name);
		rlab[3].setForeground(Color.BLUE);
		pb.add(rlab[3],cc.xy(2,20));
		
		pb.addLabel("Diagnose",cc.xy(2,22));
		
		diagnose = new JTextArea();
		diagnose.setFont(new Font("Courier",Font.PLAIN,11));
		diagnose.setLineWrap(true);
		diagnose.setWrapStyleWord(true);
		diagnose.setEditable(true);
		diagnose.setOpaque(false);
		diagnose.setForeground(Color.BLUE);
		if((! this.reznr.equals("")) && (this.aufrufvon == 0)){
			diagnose.setText((String)PatGrundPanel.thisClass.vecaktrez.get(23));
		}else if((!this.reznr.equals("")) && (this.aufrufvon == 1)){
			diagnose.setText((String)PatGrundPanel.thisClass.vecakthistor.get(23));
		}else{
			Vector vec = null;
			vec = SqlInfo.holeSatz("verordn", "diagnose", "rez_nr='"+this.reznr+"'", Arrays.asList(new String[] {}));
			if(vec.size() > 0){
				diagnose.setText((String)vec.get(0));
			}else{
				vec = SqlInfo.holeSatz("lza", "diagnose", "rez_nr='"+this.reznr+"'", Arrays.asList(new String[] {}));
				if(vec.size()>0){
					diagnose.setText((String)vec.get(0));	
				}else{
					diagnose.setText("Diagnose kann nicht ermittelt werden bitte von Originalrezept übernehmen!!!!!!!!!");
				}
					
			}
		}
		JScrollPane span = JCompTools.getTransparentScrollPane(diagnose);
		pb.add(span,cc.xyw(2,24,3));
		
		pb.addSeparator("",cc.xyw(2,26,3));
		
		pb.addLabel("Textbausteine für",cc.xy(2,28));
		try{
			tbwahl = new JRtaComboBox(SystemConfig.hmTherapBausteine.get(this.disziplin));
		}catch(java.lang.ArrayIndexOutOfBoundsException ex){
			tbwahl = new JRtaComboBox();
		}
		tbwahl.setActionCommand("tbladen");
		tbwahl.addActionListener(this);
		pb.add(tbwahl,cc.xy(2,30));
		
		pb.addSeparator("",cc.xyw(2,32,3));
		
		pb.addLabel("Verfasser des Berichtes",cc.xy(2,34));
		verfasser = new JComboBox();
		/*
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
			*/
				int lang = ParameterLaden.vKKollegen.size(); 
				for(int i =0; i < lang;i++){
					verfasser.addItem((String) ParameterLaden.getMatchcode(i)  );					
				}
				if(!neu){
					System.out.println("Verfasser bisher = "+altverfasser);
					verfasser.setSelectedItem(altverfasser);
					tbwahl.setSelectedItem(diag);
				}
			/*	
				return null;
			}
			
		}.execute();
		*/
		pb.add(verfasser,cc.xy(2,36));
		
		pb.addSeparator("",cc.xyw(2,38,3));
		
		jbut = new JButton("Bericht speichern");
		jbut.setActionCommand("berichtspeichern");
		jbut.addActionListener(this);
		pb.add(jbut,cc.xy(2, 40));

		jbutbericht = new JButton("Bericht drucken");
		jbutbericht.setActionCommand("berichtdrucken");
		jbutbericht.addActionListener(this);
		if(this.neu){
			jbutbericht.setEnabled(false);			
		}
		pb.add(jbutbericht,cc.xy(2, 42));
		
		jbut = new JButton("Text aus Vorbericht");
		jbut.setActionCommand("berichtvorbericht");
		jbut.addActionListener(this);
		pb.add(jbut,cc.xy(2, 44));
		
		jbut = new JButton("abbrechen / zurück");
		jbut.setActionCommand("berichtabbrechen");
		jbut.addActionListener(this);
		pb.add(jbut,cc.xy(2, 46));

		span = JCompTools.getTransparentScrollPane(pb.getPanel());
		span.validate();
		return span;

		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		String cmd = arg0.getActionCommand();
		if(cmd.equals("neuerempfaenger")){
			neuerArzt();
			return;
		}
		if(cmd.equals("tbladen")){
			if(thblock != null){
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						thblock.fuelleTabelle((String)tbwahl.getSelectedItem());
						thblock.setzeSucheAufNull();
						return null;
					}
					
				}.execute();
			}
		}
		if(cmd.equals("berichtspeichern")){
			// unterscheidung alt und neu
			if(this.neu){
				SwingUtilities.invokeLater(new Runnable(){
					public  void run(){
						if(doSpeichernNeu()){
							neu = false;
							gespeichert = true;
							jbutbericht.setEnabled(true);
							JOptionPane.showMessageDialog(null, "Therapiebericht wurde erfolgreich gespeichert");
							return;
						}
			   	  	}
				});
			}else{
				SwingUtilities.invokeLater(new Runnable(){
					public  void run(){
						if(doSpeichernAlt()){
							neu = false;
							gespeichert = true;
							jbutbericht.setEnabled(true);
							return;
						}
			   	  	}
				});
			}
		}
		if(cmd.equals("berichtvorbericht")){
			doBerichtVorbericht(arg0);
		}
		if(cmd.equals("berichtdrucken")){
			doBerichtDrucken();

		}
		if(cmd.equals("berichtabbrechen")){
			this.dispose();
		}

		
		
	}
	private void doBerichtVorbericht(ActionEvent arg0){
		vorberichtid = -1;
		vorberichtdiagnose = false;
		int wieviel = SqlInfo.zaehleSaetze("berhist", "pat_intern='"+PatGrundPanel.thisClass.patDaten.get(29)+"'");
		if(wieviel > 0){
			System.out.println("Bericht bereits vorhanden: "+wieviel);
			Point pos = (Point) ((JComponent)arg0.getSource()).getLocation();
			pos.x = pos.x+40;
			VorBerichte vbe = new VorBerichte(false, false, pos,this);
			vbe.setModal(true);
			vbe.setVisible(true);
			System.out.println("rückgabewerte = "+vorberichtid+"  auch diagnose = "+vorberichtdiagnose);
			if(vorberichtid > 0){
				if(vorberichtid == this.berichtid){
					JOptionPane.showMessageDialog(null,"Sie können nicht den akutellen Bericht auf sich selbst kopieren....\n"+
							"(das wäre ganz nebenbei bemerkt auch reichlich idiotisch)");
				}else{
					final String xvorberichtid = Integer.toString(vorberichtid);
					final boolean xvorberichtdiagnose = vorberichtdiagnose;
					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() throws Exception {
							Vector vec = SqlInfo.holeSatz("bericht1",
									"berstand,berbeso,berprog,bervors,diagnose", 
									"berichtid='"+vorberichtid+"'", 
									Arrays.asList(new String[] {})
									);

							for(int i = 0; i < 4; i++){
								if(! ((String)vec.get(i)).trim().equals("") ){
									StringBuffer stbuf = new StringBuffer();
									stbuf.append("**********Anfang übernommener Text**************\n");
									stbuf.append(((String)vec.get(i)).trim()+"\n");
									stbuf.append("**********Ende übernommener Text**************\n\n");
									stbuf.append(icfblock[i].getText().trim());
									icfblock[i].setText(stbuf.toString());
								}
							}
							if(xvorberichtdiagnose){
								StringBuffer stbuf = new StringBuffer();
								stbuf.append("**********Anfang übernommene Diagnose**************\n");
								stbuf.append(((String)vec.get(4)).trim()+"\n");
								stbuf.append("**********Ende übernommene Diagnose**************\n\n");
								stbuf.append(diagnose.getText());
								diagnose.setText(stbuf.toString());
							}
							System.out.println(vec);
							return null;
						}
						
					}.execute();
					
				}
			}

		}else{
			JOptionPane.showMessageDialog(null,"Für diesen Patient wurden noch keine Berichte angelegt!");
			return;
		}
		/*
			//JOptionPane.showMessageDialog(null, "Funktion ist noch nicht implementiert");
			 * 
			 */
	}
	private void neuerArzt(){
		jtf[0] = new JRtaTextField("NORMAL",false);
		jtf[1] = new JRtaTextField("NORMAL",false);
		jtf[2] = new JRtaTextField("NORMAL",false);
		ArztAuswahl awahl = new ArztAuswahl(null,"arztwahlfuerbericht",
				new String[] {rlab[2].getText(),Integer.toString(arztid)},
				jtf,rlab[2].getText());
		awahl.setModal(true);
		awahl.setLocationRelativeTo(this);
		awahl.setVisible(true);
		if(!jtf[2].getText().equals("")){
			rlab[2].setText(jtf[0].getText());
			if(!jtf[2].getText().equals("")){
				arztid = new Integer(jtf[2].getText());					
			}else{
				arztid = -1;
			}
		}
	}
	private boolean doSpeichernNeu(){
		boolean saveok = true;
		if(arztid <= 0){
			JOptionPane.showMessageDialog(null,"Fehler - Angabe des Empfängers ist ungültig!\nBitte neuen Arzt auswählen");
			return false;
		}
		//verfasser testen
		String xverfasser = (String)verfasser.getSelectedItem();
		if(xverfasser.equals("./.")){
			JOptionPane.showMessageDialog(null,"Fehler - Angabe des Verfassers ist ungültig!");
			return false;
		}
		//testen ob alles leer
		boolean gefuellt = false;
		for(int i = 0; i < 3; i++){
			if(! icfblock[i].getText().trim().equals("")){
				gefuellt = true;
				break;
			}
		}
		if(!gefuellt){
			JOptionPane.showMessageDialog(null,"Ein bisschen Text zum Speichern wäre nicht schlecht....");
			return false;
		}
		//id holen
		int berichtnr = SqlInfo.erzeugeNummer("bericht");
		if(berichtnr < 0){
			JOptionPane.showMessageDialog(null,"Schwerwiegender Fehler beim Bezug einer neuen Berichts-ID!");
			return false;
		}
		
		//System.out.println("************************************************************************************");
		String tbs = (String) tbwahl.getSelectedItem(); 
		String cmd = "insert into berhist set erstelldat='"+DatFunk.sDatInSQL(DatFunk.sHeute())+"', verfasser='"+xverfasser+"', "+
		"bertitel='"+"Bericht zu "+this.reznr+" ("+tbs+")', "+
		"empfaenger='"+rlab[2].getText()+"', empfid='"+arztid+"', berichtid='"+berichtnr+"', "+
		"pat_intern='"+this.pat_intern+"'";
		
		new ExUndHop().setzeStatement(new String(cmd));
		long zeit = System.currentTimeMillis();
		while(! ExUndHop.processdone){
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if((System.currentTimeMillis() - zeit) > 2500){
				return false;
			}
		}

		System.out.println("Bericht-Historie wurde gespeichert");
		//System.out.println(cmd);
		//System.out.println("************************************************************************************");		
		cmd = "insert into bericht1 set verfasser='"+xverfasser+"', krbild='"+tbs+"', diagnose='"+StringTools.Escaped(diagnose.getText())+"' ,"+
		"berstand='"+StringTools.Escaped(icfblock[0].getText())+"' , berbeso='"+StringTools.Escaped(icfblock[1].getText())+"', "+
		"berprog='"+StringTools.Escaped(icfblock[2].getText())+"', "+
		"bervors='"+StringTools.Escaped(icfblock[3].getText())+"', berichtid='"+berichtnr+"', pat_intern='"+this.pat_intern+"', "+
		"bertyp='"+reznr+"'";
		while(! ExUndHop.processdone){
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if((System.currentTimeMillis() - zeit) > 2500){
				return false;
			}
		}

		new ExUndHop().setzeStatement(new String(cmd));
		System.out.println("Bericht-wurde gespeichert");

		final int xberichtnr = berichtnr;
		// hier war vorher ein SwinWorker aufruf;
				if(aufrufvon==0){
					while(! ExUndHop.processdone){
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if((System.currentTimeMillis() - zeit) > 2500){
							JOptionPane.showMessageDialog(null, "Abbruch der Speichernfunktion - 3");
							return false;
						}
					}
					cmd = "update verordn set berid='"+Integer.toString(xberichtnr)+"' where rez_nr='"+reznr+"'";
					new ExUndHop().setzeStatement(cmd);
					cmd = "update lza set berid='"+Integer.toString(xberichtnr)+"' where rez_nr='"+reznr+"'";
					new ExUndHop().setzeStatement(cmd);

					System.out.println("BerichtNr - "+xberichtnr+" - wurde in verordn und lza gespeichert");
					TherapieBerichte.aktBericht.holeBerichte(PatGrundPanel.thisClass.patDaten.get(29), "");
					//return null;
					return true;
				}
				if(aufrufvon==1){
					while(! ExUndHop.processdone){
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if((System.currentTimeMillis() - zeit) > 2500){
							return false;
						}
					}
					cmd = "update lza set berid='"+Integer.toString(xberichtnr)+"' where rez_nr='"+reznr+"'";
					new ExUndHop().setzeStatement(cmd);
					System.out.println("BerichtNr - "+xberichtnr+" - wurde nur in lza gespeichert");
					TherapieBerichte.aktBericht.holeBerichte(PatGrundPanel.thisClass.patDaten.get(29), "");
					//return null;
					return true;
				}

		return true;
	}
	private boolean doSpeichernAlt(){
		String empfaenger = "";
		if(arztid <= 0){
			String cmd = "Der angegebene Arzt kann als Berichtsempfänger nicht verwendet werden.\n\n"+
			"Bitte wählen Sie den korrekten Arzt aus.";
			JOptionPane.showMessageDialog(null,cmd);
			SwingUtilities.invokeLater(new Runnable(){
				public  void run(){
					neuerArzt();
		   	  	}
			});
			return false;
		}else{
			empfaenger = rlab[2].getText();
		}
 
		String xverf = (String)verfasser.getSelectedItem();
		if(xverf.equals("./.")){
			xverf = (String)verfasser.getItemAt(1);
		}
		//System.out.println("************************************************************************************");
		String tbs = (String) tbwahl.getSelectedItem(); 
		String cmd = "update berhist set editdat='"+DatFunk.sDatInSQL(DatFunk.sHeute())+"', verfasser='"+xverf+"', "+
		"bertitel='"+"Bericht zu "+this.reznr+" ("+tbs+")', "+
		"empfaenger='"+empfaenger+"', empfid='"+arztid+"' where berichtid='"+this.berichtid+"'";
		new ExUndHop().setzeStatement(cmd);
		//System.out.println(cmd);
		//System.out.println("************************************************************************************");
		///*****************hier noch die Tabelle aktualisieren*******************/
		cmd = "update bericht1 set verfasser='"+xverf+"', krbild='"+tbs+"', diagnose='"+StringTools.Escaped(diagnose.getText())+"' ,"+
		"berstand='"+StringTools.Escaped(icfblock[0].getText())+"' , berbeso='"+StringTools.Escaped(icfblock[1].getText())+"', "+
		"berprog='"+StringTools.Escaped(icfblock[2].getText())+"', "+
		"bervors='"+StringTools.Escaped(icfblock[3].getText())+"', bertyp='"+reznr+"' where berichtid='"+this.berichtid+"'";
		new ExUndHop().setzeStatement(cmd);
		//System.out.println(cmd);		
		//System.out.println("************************************************************************************");
		JOptionPane.showMessageDialog(null,"Der Bericht wurde erfolgreich gespeichert");
		final String xtbs = tbs;
		final String xxverf = xverf;
		final String xempfaenger = empfaenger;

		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {

				if(aufrufvon==3){
					//tabbericht
					//{"ID","Titel","Verfasser","erstellt","Empfänger","letzte Änderung",""};
					TherapieBerichte.aktBericht.dtblm.setValueAt("Bericht zu "+reznr+" ("+xtbs+")", tblreihe, 1);
					TherapieBerichte.aktBericht.dtblm.setValueAt(xxverf, tblreihe, 2);
					TherapieBerichte.aktBericht.dtblm.setValueAt(xempfaenger, tblreihe,4);
					TherapieBerichte.aktBericht.dtblm.setValueAt(DatFunk.sHeute(), tblreihe,5);
					TherapieBerichte.aktBericht.tabbericht.revalidate();
				}else if(aufrufvon==0){
					
				}

				return null;
			}
			
		}.execute();
		return true;
		
	}
	
	public void doBerichtDrucken(){
		/*
		List<String> lAdrBDaten = Arrays.asList(new String[]{"<Badr1>","<Badr2>","<Badr3>","<Badr4>","<Badr5>","<Banrede>",
				"<Bdisziplin>","<Bdiagnose>","<Breznr>","<Brezdatum>","<Bblock1>","<Bblock2>","<Bblock3>","<Bblock4>"});
				"<Btitel1>","<Btitel2>","<Btitel3>","<Btitel4>"});
		*/
		Vector vec = SqlInfo.holeSatz("arzt", 
				"anrede,titel,nachname,vorname,strasse,plz,ort",
				" id='"+arztid+"'", 
				Arrays.asList(new String[] {}));
		if(vec.size()<=0){
			JOptionPane.showMessageDialog(null,"Der zugeordnete Arzt ist nicht gültig bitte wählen Sie einen neuen Arzt!");
			return;
		}
		String[] str = AdressTools.machePrivatAdresse(vec.toArray());
		for(int i = 0; i < str.length; i++){
			System.out.println("Ergebnis von str"+i+" = "+str[i]);
		}
		SystemConfig.hmAdrBDaten.put("<Badr1>", str[0]);
		SystemConfig.hmAdrBDaten.put("<Badr2>", str[1]);
		SystemConfig.hmAdrBDaten.put("<Badr3>", str[2]);
		SystemConfig.hmAdrBDaten.put("<Badr4>", str[3]);
		SystemConfig.hmAdrBDaten.put("<Bbanrede>", str[4]);
		if(PatGrundPanel.thisClass.patDaten.get(0).toUpperCase().equals("HERR")){
			SystemConfig.hmAdrBDaten.put("<Bihrenpat>", "Ihren Patieten");
		}else{
			SystemConfig.hmAdrBDaten.put("<Bihrenpat>", "Ihre Patietin");
		}
		for(int i = 0; i < 4; i++){
			SystemConfig.hmAdrBDaten.put("<Btitel"+(i+1)+">", SystemConfig.berichttitel[i]);
			if(this.icfblock[i].getText().trim().equals("")){
				SystemConfig.hmAdrBDaten.put("<Bblock"+(i+1)+">", "");	
				SystemConfig.hmAdrBDaten.put("<Btitel"+(i+1)+">", "");
			}else{
				//String sblock = icfblock[i].getText().replaceAll("\\n", "")+"\r";
				String sblock = icfblock[i].getText()+"\n";
				SystemConfig.hmAdrBDaten.put("<Bblock"+(i+1)+">",sblock);
				SystemConfig.hmAdrBDaten.put("<Btitel"+(i+1)+">", SystemConfig.berichttitel[i]);
			}
			
		}
		SystemConfig.hmAdrBDaten.put("<Bnname>", StringTools.EGross(PatGrundPanel.thisClass.patDaten.get(2)));
		SystemConfig.hmAdrBDaten.put("<Bvname>", StringTools.EGross(PatGrundPanel.thisClass.patDaten.get(3)));
		SystemConfig.hmAdrBDaten.put("<Bgeboren>", DatFunk.sDatInDeutsch(PatGrundPanel.thisClass.patDaten.get(4)));
		SystemConfig.hmAdrBDaten.put("<Brezdatum>", DatFunk.sDatInDeutsch(rezdatum));
		SystemConfig.hmAdrBDaten.put("<Breznr>",reznr);
		String sblock = diagnose.getText().replaceAll("\\n", "");
		SystemConfig.hmAdrBDaten.put("<Bdiagnose>",sblock);
		SystemConfig.hmAdrBDaten.put("<Btherapeut>",(String) verfasser.getSelectedItem());
		System.out.println("Berichtsdatei = ------->"+SystemConfig.thberichtdatei);

		/*
		Set entries = SystemConfig.hmAdrBDaten.entrySet();
	    Iterator it = entries.iterator();
	    while (it.hasNext()) {
	      Map.Entry entry = (Map.Entry) it.next();
	      System.out.println("Key = "+(String)entry.getKey()+"\n"+"Wert = "+entry.getValue());
	    }
	    */
	    OOTools.starteTherapieBericht(SystemConfig.thberichtdatei);

		
	}
	public ArztBericht getInstance(){
		return this;
	}
	
	public void schreibeTextBlock(int block,String text){
		String icftext = icfblock[block].getText();
		if(icftext.equals("")){
			icfblock[block].setText(text);
			zuletztaktiv = block;
			final int xblock = block;
			SwingUtilities.invokeLater(new Runnable(){
				public  void run(){
					icfblock[xblock].requestFocus();
					//icfblock[xblock].setCaretPosition(icfblock[xblock].getText().length()-1);
		   	  	}
			});
		}else{
			icfblock[block].setText(icftext+text);
			zuletztaktiv = block;
			final int xblock = block;
			SwingUtilities.invokeLater(new Runnable(){
				public  void run(){
					icfblock[xblock].requestFocus();
					//icfblock[xblock].setCaretPosition( icfblock[xblock].getText().length()-1 );
		   	  	}
			});
		}
	}
class TextBausteine extends AbstractAction {
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(thblock == null){
			thblock = new ThTextBlock(null,"textblock",(String)tbwahl.getSelectedItem(),getInstance());
			thblock.setModal(true);
			thblock.setLocationRelativeTo(grundPanel);
			thblock.pack();
			thblock.setVisible(true);
			SwingUtilities.invokeLater(new Runnable(){
				public  void run(){
					icfblock[zuletztaktiv].requestFocus();
					try{
					icfblock[zuletztaktiv].setCaretPosition(icfblock[zuletztaktiv].getText().length());
					}catch(java.lang.IllegalArgumentException ex){
						// da passiert jetzt halt nix
					}
		   	  	}
			});
			
		}else{
			thblock.setVisible(true);
			SwingUtilities.invokeLater(new Runnable(){
				public  void run(){
					icfblock[zuletztaktiv].requestFocus();
					try{
						icfblock[zuletztaktiv].setCaretPosition(icfblock[zuletztaktiv].getText().length());
					}catch(java.lang.IllegalArgumentException ex){
						// da passiert jetzt halt nix						
					}
		   	  	}
			});
		}
	}
	 
}

/*********************************
 * 
 * 
 * Nachfolgend die Standards für RehaSmartDialog
 * 
 */
	public void rehaTPEventOccurred(RehaTPEvent evt) {
		// TODO Auto-generated method stub
		try{
			if(evt.getDetails()[0] != null){
				if(evt.getDetails()[0].equals(this.getName())){
					this.setVisible(false);
					rtp.removeRehaTPEventListener((RehaTPEventListener) this);
					rtp = null;
					new Thread(){
						public void run(){
							if(! ishmnull){
								setzeHmAufNull();
							}
							if(aufrufvon==0){
								System.out.println("Aufgerufen von "+aufrufvon);
								AktuelleRezepte.aktRez.setRezeptDaten();
							}else if(aufrufvon==1){
								Historie.historie.setRezeptDaten();									
							}
						}
					}.start();
					this.dispose();
					super.dispose();
					System.out.println("****************Arztbericht -> Listener entfernt**************"+this.getName());				
				}
			}
		}catch(NullPointerException ne){
			System.out.println("In PatNeuanlage" +evt);
		}
	}
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		if(rtp != null){
			this.setVisible(false);			
			rtp.removeRehaTPEventListener((RehaTPEventListener) this);		
			rtp = null;
			pinPanel = null;
			new Thread(){
				public void run(){
					if(! ishmnull){
						setzeHmAufNull();	
					}
					if(aufrufvon==0){
						System.out.println("Aufgerufen von "+aufrufvon);
						AktuelleRezepte.aktRez.setRezeptDaten();
					}else if(aufrufvon==1){
						Historie.historie.setRezeptDaten();									
					}
				}
			}.start();
			this.dispose();
			super.dispose();
			System.out.println("****************Arztbericht -> Listener entfernt (Closed)**********"+this.getName());
		}
		
		
	}
	private void setzeHmAufNull(){
		Set entries = SystemConfig.hmAdrBDaten.entrySet();
	    Iterator it = entries.iterator();
	    while (it.hasNext()) {
	      Map.Entry entry = (Map.Entry) it.next();
	      SystemConfig.hmAdrBDaten.put((String)entry.getKey(),"");
	      
	    }
	    ishmnull = true;
	}
	/*********************************
	 * 
	 * 
	 * Ende der Standards für RehaSmartDialog
	 * 
	 */

}
