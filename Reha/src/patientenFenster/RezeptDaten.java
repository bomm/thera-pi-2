package patientenFenster;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;

import patientenFenster.ArztAuswahl.ArztWahlAction;

import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemTools.JCompTools;
import systemTools.JRtaTextField;
import systemTools.StringTools;
import terminKalender.ParameterLaden;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
 
public class RezeptDaten extends JXPanel{
	//public JLabel[] rezlabs = {null,null,null,null,null,null,null,null,null};
	public JRtaTextField reznum = null;
	public JRtaTextField draghandler = null;
	public ImageIcon hbimg = null; 
	public Vector vecaktrez = null;

	public String[] rezart = {"Erstverordnung","Folgeverordnung","Folgev. auﬂerhalb d.R."};
	public RezeptDaten(){
		super();
		this.setOpaque(false);
		setBorder(null);
		setLayout(new BorderLayout());
		add(getDatenPanel(),BorderLayout.CENTER);
		hbimg = SystemConfig.hmSysIcons.get("hausbesuch");
	}
	public void setRezeptDaten(String reznummer,String sid){
		reznum.setText(reznummer);
		final String xreznummer = reznummer;
		final String xsid = sid;
		/*
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
		*/

				vecaktrez = SqlInfo.holeSatz("verordn", " * ", "id = '"+xsid+"'", Arrays.asList(new String[] {}) );
				PatGrundPanel.thisClass.vecaktrez = vecaktrez;
				String stest = StringTools.NullTest((String)vecaktrez.get(43));
				if( stest.equals("T") ){
					PatGrundPanel.thisClass.rezlabs[1].setIcon(hbimg);
				}else{
					PatGrundPanel.thisClass.rezlabs[1].setIcon(null);
				}
				PatGrundPanel.thisClass.rezlabs[2].setText("angelegt von: "+StringTools.NullTest((String)vecaktrez.get(45)));
				if(StringTools.ZahlTest( (String)vecaktrez.get(37)) >= 0 ){
					PatGrundPanel.thisClass.rezlabs[3].setForeground(Color.BLACK);
				}else{
					PatGrundPanel.thisClass.rezlabs[3].setForeground(Color.RED);					
				}
				PatGrundPanel.thisClass.rezlabs[3].setText(StringTools.NullTest((String)vecaktrez.get(36)));

				if(StringTools.ZahlTest( (String)vecaktrez.get(16)) >= 0 ){
					PatGrundPanel.thisClass.rezlabs[4].setForeground(Color.BLACK);
				}else{
					PatGrundPanel.thisClass.rezlabs[4].setForeground(Color.RED);					
				}
				PatGrundPanel.thisClass.rezlabs[4].setText(StringTools.NullTest((String)vecaktrez.get(15)));

				int test = StringTools.ZahlTest((String)vecaktrez.get(27));
				if(test >= 0){
					PatGrundPanel.thisClass.rezlabs[5].setText(rezart[test]);
					if(test==2){
						stest = StringTools.NullTest((String)vecaktrez.get(42));
						if(stest.equals("T")){
							PatGrundPanel.thisClass.rezlabs[6].setForeground(Color.BLACK);		
						}else{
							PatGrundPanel.thisClass.rezlabs[6].setForeground(Color.RED);							
						}
						PatGrundPanel.thisClass.rezlabs[6].setText("Begr¸ndung");
					}else{
						PatGrundPanel.thisClass.rezlabs[6].setText(" ");
					}
				}else{
					PatGrundPanel.thisClass.rezlabs[5].setText(" ");
					PatGrundPanel.thisClass.rezlabs[6].setText(" ");
				}
				stest = StringTools.NullTest((String)vecaktrez.get(55));
				if( stest.equals("T") ){
					test = StringTools.ZahlTest((String)vecaktrez.get(54));
					if(test >= 0){
						PatGrundPanel.thisClass.rezlabs[7].setForeground(Color.BLACK);
					}else{
						PatGrundPanel.thisClass.rezlabs[7].setForeground(Color.RED);
					}
					PatGrundPanel.thisClass.rezlabs[7].setText("Arztbericht");
				}else{
					PatGrundPanel.thisClass.rezlabs[7].setText(" ");
				}
				Vector<ArrayList> preisvec = null;
				if(reznummer.contains("KG")){
					preisvec = ParameterLaden.vKGPreise;
				}else if(reznummer.contains("MA")){
					preisvec = ParameterLaden.vMAPreise;
				}else if(reznummer.contains("ER")){
					preisvec = ParameterLaden.vERPreise;
				}else if(reznummer.contains("LO")){
					preisvec = ParameterLaden.vLOPreise;
				}else if(reznummer.contains("RH")){
					preisvec = ParameterLaden.vRHPreise;
				}

				SwingUtilities.invokeLater(new Runnable(){
					public  void run(){
						int farbcode = StringTools.ZahlTest((String)vecaktrez.get(57));
						if(farbcode > 0){
							reznum.setForeground( (SystemConfig.vSysColsObject.get(0).get(farbcode)[0]) );
						}else{
							reznum.setForeground(Color.BLUE);
						}
					}
				});				
				PatGrundPanel.thisClass.rezlabs[8].setText( leistungTesten(preisvec,StringTools.ZahlTest((String)vecaktrez.get(8))) );

				stest = StringTools.NullTest((String)vecaktrez.get(52));
				if(stest.equals("")){
					PatGrundPanel.thisClass.rezlabs[9].setForeground(Color.RED);
					PatGrundPanel.thisClass.rezlabs[9].setText(stest+"??? / Wo.");					
				}else{
					PatGrundPanel.thisClass.rezlabs[9].setForeground(Color.BLACK);
					PatGrundPanel.thisClass.rezlabs[9].setText(stest+" / Wo.");					
				}

				PatGrundPanel.thisClass.rezlabs[10].setText( leistungTesten(preisvec,StringTools.ZahlTest((String)vecaktrez.get(9))) );				
				PatGrundPanel.thisClass.rezlabs[11].setText( leistungTesten(preisvec,StringTools.ZahlTest((String)vecaktrez.get(10))) );				
				PatGrundPanel.thisClass.rezlabs[12].setText( leistungTesten(preisvec,StringTools.ZahlTest((String)vecaktrez.get(11))) );				

				stest = StringTools.NullTest((String)vecaktrez.get(44));
				if(stest.equals("") || stest.equals("kein IndiSchl.") ){
					PatGrundPanel.thisClass.rezlabs[13].setForeground(Color.RED);
					PatGrundPanel.thisClass.rezlabs[13].setText("??? "+stest);					
				}else{
					PatGrundPanel.thisClass.rezlabs[13].setForeground(Color.BLACK);
					PatGrundPanel.thisClass.rezlabs[13].setText(stest);					
				}

				
				stest = StringTools.NullTest((String)vecaktrez.get(47));
				if(stest.equals("") ){
					PatGrundPanel.thisClass.rezlabs[14].setForeground(Color.RED);
					PatGrundPanel.thisClass.rezlabs[14].setText("??? Min.");
				}else{
					PatGrundPanel.thisClass.rezlabs[14].setForeground(Color.BLACK);
					PatGrundPanel.thisClass.rezlabs[14].setText(stest+" Min.");
				}
				
				PatGrundPanel.thisClass.rezdiag.setText(StringTools.NullTest((String)vecaktrez.get(23)));
				/*
				return null;
			}
		}.execute();
		*/
		
	}
	public String leistungTesten(Vector<ArrayList> preisevec,int veczahl){
		String retwert = "----";
		if(veczahl==-1 || veczahl==0){
			return retwert;
		}
		if(veczahl <= preisevec.size()){
			int idtest =  new Integer( (String) ((ArrayList)preisevec.get(veczahl-1)).get(35) );
			if(idtest == veczahl){
				return StringTools.NullTest((String)vecaktrez.get(3))+"  *  "+
				(String) ((ArrayList)preisevec.get(veczahl-1)).get(1);
			}
		}
		for(int i = 0;i<preisevec.size();i++){
			if( new Integer( (String) ((ArrayList)preisevec.get(i)).get(35)) == veczahl ){
				return StringTools.NullTest((String)vecaktrez.get(3))+"  *  "+
				(String) ((ArrayList)preisevec.get(i)).get(1);
			}
		}
		
		return retwert;
	}
	
	public JScrollPane getDatenPanel(){
		JScrollPane jscr = null;
		FormLayout lay = new FormLayout("fill:0:grow(0.33),2px,fill:0:grow(0.33),2px,fill:0:grow(0.33)",
		//FormLayout lay = new FormLayout("p,fill:0:grow(0.50),p,fill:0:grow(0.50),p",
				//      1.Sep                2.Sep                              3.Sep
				//1 2   3  4   5  6   7  8   9 10   11 12  13 14  15 16  17  18 19  20 21 22  23 24       25
				"p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,5dlu,p,2dlu,p,2dlu,p, 2dlu,p,5dlu,p,5dlu,p,2dlu,p,20dlu:g,22px" );
		CellConstraints cc = new CellConstraints();
		PanelBuilder jpan = new PanelBuilder(lay);
		jpan.getPanel().setOpaque(false);
		Font fontreznr = new Font("Tahoma",Font.BOLD,16);
		Font fontbehandlung = new Font("Tahoma",Font.BOLD,11);		
		reznum = new JRtaTextField("GROSS",true);
		reznum.setText("  ");
		reznum.setFont(fontreznr);
		reznum.setForeground(Color.BLUE);
		reznum.setOpaque(false);
		reznum.setEditable(false);
		reznum.setBorder(null);
		reznum.setDragEnabled(true);
		reznum.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent e) {
		    	int farbcode = StringTools.ZahlTest((String)vecaktrez.get(57));
		    	draghandler.setText(
		    			((String)PatGrundPanel.thisClass.patDaten.get(0)).substring(0,1)+
		    			"-"+PatGrundPanel.thisClass.patDaten.get(2)+","+
		    			PatGrundPanel.thisClass.patDaten.get(3)+"∞"+
		    			reznum.getText()+
		    			(farbcode > 0 ? (String)SystemConfig.vSysColsCode.get(farbcode) : "")+
		    			"∞"+PatGrundPanel.thisClass.rezlabs[14].getText()
		    			);
		      JComponent c = (JComponent)draghandler;
		      TransferHandler th = c.getTransferHandler();
		      th.exportAsDrag(c, e, TransferHandler.COPY); //TransferHandler.COPY
		    }
		  });
		draghandler = new JRtaTextField("GROSS",true);
		draghandler.setTransferHandler(new TransferHandler("text"));		
		/*
		PatGrundPanel.thisClass.rezlabs[0] = new JLabel();
		PatGrundPanel.thisClass.rezlabs[0].setFont(fontreznr);
		PatGrundPanel.thisClass.rezlabs[0].setName("rezeptnummer");
		PatGrundPanel.thisClass.rezlabs[0].setForeground(Color.BLUE);
		PatGrundPanel.thisClass.rezlabs[0].setText("KG57606");
		*/

		
		PatGrundPanel.thisClass.rezlabs[1] = new JLabel(" ");
		PatGrundPanel.thisClass.rezlabs[1].setName("hausbesuch");
		PatGrundPanel.thisClass.rezlabs[1].setIcon(hbimg);

		PatGrundPanel.thisClass.rezlabs[2] = new JLabel(" ");
		PatGrundPanel.thisClass.rezlabs[2].setName("angelegt");
		
		PatGrundPanel.thisClass.rezlabs[3] = new JLabel(" ");
		PatGrundPanel.thisClass.rezlabs[3].setName("kostentraeger");

		PatGrundPanel.thisClass.rezlabs[4] = new JLabel(" ");
		PatGrundPanel.thisClass.rezlabs[4].setName("arzt");
		
		PatGrundPanel.thisClass.rezlabs[5] = new JLabel(" ");
		PatGrundPanel.thisClass.rezlabs[5].setName("verornungsart");
		
		PatGrundPanel.thisClass.rezlabs[6] = new JLabel(" ");
		PatGrundPanel.thisClass.rezlabs[6].setName("begruendung");
		PatGrundPanel.thisClass.rezlabs[6].setForeground(Color.RED);
		
		PatGrundPanel.thisClass.rezlabs[7] = new JLabel(" ");
		PatGrundPanel.thisClass.rezlabs[7].setName("arztbericht");

		PatGrundPanel.thisClass.rezlabs[8] = new JLabel("");
		PatGrundPanel.thisClass.rezlabs[8].setName("behandlung1");
		PatGrundPanel.thisClass.rezlabs[8].setFont(fontbehandlung);
		PatGrundPanel.thisClass.rezlabs[9] = new JLabel(" ");
		PatGrundPanel.thisClass.rezlabs[9].setName("frequenz");
		PatGrundPanel.thisClass.rezlabs[9].setFont(fontbehandlung);
		
		PatGrundPanel.thisClass.rezlabs[10] = new JLabel(" ");
		PatGrundPanel.thisClass.rezlabs[10].setName("behandlung2");
		PatGrundPanel.thisClass.rezlabs[10].setFont(fontbehandlung);
		PatGrundPanel.thisClass.rezlabs[11] = new JLabel(" ");
		PatGrundPanel.thisClass.rezlabs[11].setName("behandlung3");
		PatGrundPanel.thisClass.rezlabs[11].setFont(fontbehandlung);
		PatGrundPanel.thisClass.rezlabs[12] = new JLabel(" ");
		PatGrundPanel.thisClass.rezlabs[12].setName("behandlung4");
		PatGrundPanel.thisClass.rezlabs[12].setFont(fontbehandlung);
		
		PatGrundPanel.thisClass.rezlabs[13] = new JLabel(" ");
		PatGrundPanel.thisClass.rezlabs[13].setName("indikation");
		PatGrundPanel.thisClass.rezlabs[13].setFont(fontbehandlung);
		
		PatGrundPanel.thisClass.rezlabs[14] = new JLabel(" ");
		PatGrundPanel.thisClass.rezlabs[14].setName("Dauer");
		PatGrundPanel.thisClass.rezlabs[14].setFont(fontbehandlung);
		
		PatGrundPanel.thisClass.rezdiag = new JTextArea("");
		PatGrundPanel.thisClass.rezdiag.setOpaque(false);
		PatGrundPanel.thisClass.rezdiag.setFont(new Font("Courier",Font.PLAIN,11));
		PatGrundPanel.thisClass.rezdiag.setForeground(Color.BLUE);
		PatGrundPanel.thisClass.rezdiag.setLineWrap(true);
		PatGrundPanel.thisClass.rezdiag.setName("notitzen");
		PatGrundPanel.thisClass.rezdiag.setWrapStyleWord(true);
		PatGrundPanel.thisClass.rezdiag.setEditable(false);


		jpan.add(reznum,cc.xy(1, 1));
		//jpan.add(PatGrundPanel.thisClass.rezlabs[0],cc.xy(1, 1));
		jpan.add(PatGrundPanel.thisClass.rezlabs[1],cc.xy(3, 1));
		jpan.add(PatGrundPanel.thisClass.rezlabs[2],cc.xy(5, 1));

		jpan.addSeparator("", cc.xyw(1,3,5));

		jpan.add(PatGrundPanel.thisClass.rezlabs[3],cc.xy(1, 5));
		jpan.add(PatGrundPanel.thisClass.rezlabs[4],cc.xy(5, 5));

		jpan.add(PatGrundPanel.thisClass.rezlabs[5],cc.xy(1, 7));
		jpan.add(PatGrundPanel.thisClass.rezlabs[6],cc.xy(3, 7));
		jpan.add(PatGrundPanel.thisClass.rezlabs[7],cc.xy(5, 7));

		jpan.addSeparator("", cc.xyw(1,9,5));
		
		jpan.add(PatGrundPanel.thisClass.rezlabs[8],cc.xy(1, 11));
		jpan.add(PatGrundPanel.thisClass.rezlabs[9],cc.xy(3, 11));
		jpan.add(PatGrundPanel.thisClass.rezlabs[14],cc.xy(5, 11));
		jpan.add(PatGrundPanel.thisClass.rezlabs[10],cc.xy(1, 13));
		jpan.add(PatGrundPanel.thisClass.rezlabs[11],cc.xy(1, 15));
		jpan.add(PatGrundPanel.thisClass.rezlabs[12],cc.xy(1, 17));
		
		
		jpan.addSeparator("", cc.xyw(1,19,5));
		
		jpan.add(PatGrundPanel.thisClass.rezlabs[13],cc.xy(1, 21));
		//jpan.add(PatGrundPanel.thisClass.rezlabs[14],cc.xy(1, 23));
		JScrollPane jscrdiag = JCompTools.getTransparentScrollPane(PatGrundPanel.thisClass.rezdiag);
		jscrdiag.validate();
		jpan.add(jscrdiag,cc.xywh(3, 21,3,4));
		
		jscr = JCompTools.getTransparentScrollPane(jpan.getPanel());
		jscr.getVerticalScrollBar().setUnitIncrement(15);
		jscr.validate();
		return jscr;
	}

}
