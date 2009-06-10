package patientenFenster;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import sqlTools.SqlInfo;
import systemTools.Colors;
import systemTools.JCompTools;
import systemTools.JRtaTextField;
import systemTools.StringTools;
import terminKalender.datFunk;

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
	private JComboBox tbwahl;
	private JComboBox verfasser;
	private int arztid;
	public  JRtaTextField[] jtf = {null,null,null};
	private JTextPane[] icfblock = {null,null,null,null};


	public ArztBericht(JXFrame owner, String name,boolean bneu,String reznr,int iberichtid,int aufruf) {
		super(owner, name);
		super.getSmartTitledPanel().setName(name);
		super.getSmartTitledPanel().setTitleForeground(Color.WHITE);
		String xtitel = "<html>Arztbericht erstellen / ändern   -->&nbsp;&nbsp;&nbsp;&nbsp;<b><font color='#ffffff'>Tip:&nbsp;&nbsp;&nbsp;&nbsp;entscheiden Sie sich für das ICF-Schema</font></b>&nbsp;&nbsp;<img src='file:///C:/RehaVerwaltung/icons/Haken_klein.gif'>";
	    super.getSmartTitledPanel().setTitle(xtitel);
		setName(name);
		
		PinPanel pinPanel = new PinPanel();
		pinPanel.getGruen().setVisible(false);
		pinPanel.setName(name);
		setPinPanel(pinPanel);
		
		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener((RehaTPEventListener) this);


		this.neu = bneu;
		this.reznr = reznr;
		this.berichtid = iberichtid;
		this.aufrufvon = aufruf;
		setSize(new Dimension(900,650));
		
	    grundPanel = new JXPanel(new BorderLayout());
	    new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				/************BackgroundPainter basteln************/
				Point2D start = new Point2D.Float(0, 0);
				Point2D end = new Point2D.Float(0,getHeight());
			    float[] dist = {0.0f, 0.75f};
			    // Color[] colors = {Color.WHITE,new Color(231,120,23)};
			    Color[] colors = {Color.WHITE,Colors.Yellow.alpha(0.25f)};
			    //Color[] colors = {Color.WHITE,getBackground()};
			    LinearGradientPaint p =
			         new LinearGradientPaint(start, end, dist, colors);
			    MattePainter mp = new MattePainter(p);
			    /************Ende BackgroundPainter basteln************/
				grundPanel.setBackgroundPainter(new CompoundPainter(mp));
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

	}
	private void fuelleBericht(){
		if(this.berichtid <= 0){
			return;
		}
		String felder = "BERSTAND,BERBESO,BERPROG,BERVORS";
		Vector vec = SqlInfo.holeSatz("bericht1",felder , "berichtid='"+new Integer(this.berichtid).toString()+"'", Arrays.asList(new String[]{}));
		for(int i = 0;i < 4;i++){
			icfblock[i].setText((String)vec.get(i));
		}
	}
	private JPanel getBerichtPanel(){  // 1   2     3                  4              5
		FormLayout lay = new FormLayout("0dlu,p,fill:0:grow(1.00),right:max(40dlu;p),0dlu",
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
		pb.add(lab,cc.xy(4,2));
		icfblock[0] = new JTextPane();
		icfblock[0].setFont(fon);
		icfblock[0].setForeground(Color.BLUE);
		JScrollPane span = JCompTools.getTransparentScrollPane(icfblock[0]);
		span.setBorder(BorderFactory.createLineBorder(Colors.PiOrange.alpha(0.25f)));
		pb.add(span,cc.xyw(2,4,3));
		lbltext ="<html>2.Block: weitere therapierelevante Aspekte&nbsp;&nbsp;&nbsp;&nbsp;<b><font color='#e77817'>(ICF - Aktivitäten / Teilhabe)</font></b>";
		lab = new JLabel(lbltext);
		pb.add(lab,cc.xy(2,6));		
		icfblock[1] = new JTextPane();
		icfblock[1].setFont(fon);
		icfblock[1].setForeground(Color.BLUE);
		span = JCompTools.getTransparentScrollPane(icfblock[1]);
		span.setBorder(BorderFactory.createLineBorder(Colors.PiOrange.alpha(0.25f)));		
		pb.add(span,cc.xyw(2,8,3));
		lbltext = "<html>3.Block: prognostische Einschätzung&nbsp;&nbsp;&nbsp;&nbsp;<b><font color='#e77817'>(ICF - Umweltfaktoren)</font></b>";
		lab = new JLabel(lbltext);
		pb.add(lab,cc.xy(2,10));		
		icfblock[2] = new JTextPane();
		icfblock[2].setFont(fon);
		icfblock[2].setForeground(Color.BLUE);
		span = JCompTools.getTransparentScrollPane(icfblock[2]);
		span.setBorder(BorderFactory.createLineBorder(Colors.PiOrange.alpha(0.25f)));
		pb.add(span,cc.xyw(2,12,3));
		lbltext = "<html>4.Block: Reserveblock&nbsp;&nbsp;&nbsp;&nbsp;<b><font color='#e77817'>(ICF - personbezogene Faktoren)</font></b>";
		lab = new JLabel(lbltext);
		pb.add(lab,cc.xy(2,14));		
		icfblock[3] = new JTextPane();
		icfblock[3].setFont(fon);
		icfblock[3].setForeground(Color.BLUE);
		span = JCompTools.getTransparentScrollPane(icfblock[3]);
		span.setBorder(BorderFactory.createLineBorder(Colors.PiOrange.alpha(0.25f)));
		pb.add(span,cc.xyw(2,16,3));

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
		rlab[1] = new JLabel(datFunk.sDatInDeutsch((String)PatGrundPanel.thisClass.patDaten.get(4)));
		rlab[1].setForeground(Color.BLUE);
		pb.add(rlab[1],cc.xy(2,6));

		pb.addSeparator("",cc.xyw(2,8,3));
		
		lab = new JLabel("Berichtsempfänger");
		pb.add(lab,cc.xy(2,10));
		// hier testen ob ohne Rezeptbezug, wenn ja kann der Vector nicht verwendet werden
		if((! this.reznr.equals("")) && (this.aufrufvon < 3)){
			name = (String)PatGrundPanel.thisClass.vecaktrez.get(15);
			arztid = new Integer((String)PatGrundPanel.thisClass.vecaktrez.get(16));
		}else if((this.reznr.equals("")) && (this.aufrufvon < 3)){
			name = (String)PatGrundPanel.thisClass.patDaten.get(25);
			try{
				arztid = new Integer((String)PatGrundPanel.thisClass.patDaten.get(26));
			}catch(java.lang.NumberFormatException ex){
				arztid = new Integer(-1);
			}
		}else{
			Vector vec;
			name =  (String)(vec = SqlInfo.holeSatz("berhist", "empfaenger,empfid", "berichtid='"+new Integer(this.berichtid).toString()+"'", Arrays.asList(new String[] {}))).get(0) ;
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
		if((! this.reznr.equals("")) && (this.aufrufvon < 3)){
			diagnose.setText((String)PatGrundPanel.thisClass.vecaktrez.get(23));
		}else{
			diagnose.setText( (String) SqlInfo.holeSatz("lza", "diagnose", "rez_nr='"+this.reznr+"'", Arrays.asList(new String[] {})).get(0) ); 
		}
		JScrollPane span = JCompTools.getTransparentScrollPane(diagnose);
		pb.add(span,cc.xyw(2,24,3));
		
		pb.addSeparator("",cc.xyw(2,26,3));
		
		pb.addLabel("Textbausteine für",cc.xy(2,28));
		tbwahl = new JComboBox(new String[] {"KG-Wirbelsäule"});
		tbwahl.setActionCommand("tbladen");
		tbwahl.addActionListener(this);
		pb.add(tbwahl,cc.xy(2,30));
		
		pb.addSeparator("",cc.xyw(2,32,3));
		
		pb.addLabel("Verfasser des Berichtes",cc.xy(2,34));
		verfasser = new JComboBox(new String[] {"Beate Maute-Steinhilber"});
		pb.add(verfasser,cc.xy(2,36));
		
		pb.addSeparator("",cc.xyw(2,38,3));
		
		jbut = new JButton("Bericht speichern");
		jbut.setActionCommand("berichtspeichern");
		jbut.addActionListener(this);
		pb.add(jbut,cc.xy(2, 40));

		jbut = new JButton("Bericht drucken");
		jbut.setActionCommand("berichtdrucken");
		jbut.addActionListener(this);
		pb.add(jbut,cc.xy(2, 42));
		
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
			jtf[0] = new JRtaTextField("NORMAL",false);
			jtf[1] = new JRtaTextField("NORMAL",false);
			jtf[2] = new JRtaTextField("NORMAL",false);
			ArztAuswahl awahl = new ArztAuswahl(null,"arztwahlfuerbericht",
					new String[] {rlab[2].getText(),new Integer(arztid).toString()},
					jtf,rlab[2].getText());
			awahl.setModal(true);
			awahl.setLocationRelativeTo(this);
			awahl.setVisible(true);
			if(!jtf[2].equals("")){
				rlab[2].setText(jtf[0].getText());
				arztid = new Integer(jtf[2].getText());
			}
		}
		
	}

/*********************************
 * 
 * 
 * Nachfolgend die Standards für RehaSmartDialog
 * 
 */
	public void RehaTPEventOccurred(RehaTPEvent evt) {
		// TODO Auto-generated method stub
		try{
			if(evt.getDetails()[0] != null){
				if(evt.getDetails()[0].equals(this.getName())){
					this.setVisible(false);
					rtp.removeRehaTPEventListener((RehaTPEventListener) this);
					rtp = null;
					this.dispose();
					System.out.println("****************Arztbericht -> Listener entfernt**************");				
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
			dispose();
			System.out.println("****************Arztbericht -> Listener entfernt (Closed)**********");
		}
		
		
	}
	/*********************************
	 * 
	 * 
	 * Ende der Standards für RehaSmartDialog
	 * 
	 */

}
