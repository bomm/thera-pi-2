package patientenFenster;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.TransferHandler;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;

import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemEinstellungen.SystemPreislisten;
import systemTools.JCompTools;
import systemTools.JRtaTextField;
import systemTools.StringTools;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class HistorDaten extends JXPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2322653747361882977L;
	//public JLabel[] rezlabs = {null,null,null,null,null,null,null,null,null};
	public JRtaTextField reznum = null;
	public JRtaTextField draghandler = null;
	public ImageIcon hbimg = null; 
	public Vector<String> vecaktrez = null;
	
	public JLabel[] rezlabs = {null,null,null,null,null,
			null,null,null,null,null,
			null,null,null,null,null};
	public JTextArea rezdiag = null;
	

	public String[] rezart = {"Erstverordnung","Folgeverordnung","Folgev. außerhalb d.R."};
	/*
	ImageIcon hgicon;
	int icx,icy;
	AlphaComposite xac1 = null;
	AlphaComposite xac2 = null;
	*/		
	
	public HistorDaten(){
		super();
		this.setOpaque(false);
		setBorder(null);
		setLayout(new BorderLayout());
		hbimg = SystemConfig.hmSysIcons.get("hausbesuch");
		//hgicon = SystemConfig.hmSysIcons.get("historie");
		/*
		HistorPanel datpan=new HistorPanel();
		datpan.setLayout(new BorderLayout());
		datpan.add(getDatenPanel(),BorderLayout.CENTER);
		datpan.validate();
		add(datpan,BorderLayout.CENTER);
		datpan.repaint();
		*/
		add(getDatenPanel(),BorderLayout.CENTER);
		

		validate();

		
	}
/*
	public void paintComponent( Graphics g ) { 
		super.paintComponent( g );
		Graphics2D g2d = (Graphics2D)g;
		
		if(hgicon != null){
			g2d.setComposite(this.xac1);
			g2d.drawImage(hgicon.getImage(), 0 , 0,null);
			//g2d.drawImage(hgicon.getImage(), (getWidth()/2)-icx , (getHeight()/2)-icy,null);
			g2d.setComposite(this.xac2);
		}
	}
*/
	
	public void setRezeptDaten(String reznummer,String sid){
		reznum.setText(reznummer);
		final String xreznummer = reznummer;
		final String xsid = sid;

		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{

				vecaktrez = SqlInfo.holeSatz("lza", " * ", "id = '"+xsid+"'", Arrays.asList(new String[] {}) );
				String stest = StringTools.NullTest((String)vecaktrez.get(43));
				Reha.thisClass.patpanel.vecakthistor = vecaktrez;
				if( stest.equals("T") ){
					rezlabs[1].setText(StringTools.NullTest((String)vecaktrez.get(64))+" *");
					rezlabs[1].setIcon(hbimg);
				}else{
					rezlabs[1].setText("");
					rezlabs[1].setIcon(null);
				}
				rezlabs[2].setText("angelegt von: "+StringTools.NullTest((String)vecaktrez.get(45)));
				if(StringTools.ZahlTest( (String)vecaktrez.get(37)) >= 0 ){
					rezlabs[3].setForeground(Color.BLACK);
				}else{
					rezlabs[3].setForeground(Color.RED);					
				}
				rezlabs[3].setText(StringTools.NullTest((String)vecaktrez.get(36)));

				if(StringTools.ZahlTest( (String)vecaktrez.get(16)) >= 0 ){
					rezlabs[4].setForeground(Color.BLACK);
				}else{
					rezlabs[4].setForeground(Color.RED);					
				}
				rezlabs[4].setText(StringTools.NullTest((String)vecaktrez.get(15)));

				int test = StringTools.ZahlTest((String)vecaktrez.get(27));
				if(test >= 0){
					if(test > 2){
						test = 2;
					}
					rezlabs[5].setText(rezart[test]);
					if(test==2){
						stest = StringTools.NullTest((String)vecaktrez.get(42));
						if(stest.equals("T")){
							rezlabs[6].setForeground(Color.BLACK);
							rezlabs[6].setText("Begründung o.k.");
						}else{
							rezlabs[6].setForeground(Color.RED);
							rezlabs[6].setText("Begründung fehlt");
						}
						
					}else{
						rezlabs[6].setText(" ");
					}
				}else{
					rezlabs[5].setText(" ");
					rezlabs[6].setText(" ");
				}
				stest = StringTools.NullTest((String)vecaktrez.get(55));
				if( stest.equals("T") ){
					test = StringTools.ZahlTest((String)vecaktrez.get(54));
					if(test >= 0){
						rezlabs[7].setForeground(Color.BLACK);
						rezlabs[7].setText("Therapiebericht o.k.");
					}else{
						rezlabs[7].setForeground(Color.RED);
						rezlabs[7].setText("Therapiebericht fehlt");
					}
				}else{
					rezlabs[7].setText(" ");
				}
				Vector<Vector<String>> preisvec = null;
				int prgruppe = 0;
				try{
					 prgruppe = Integer.parseInt((String)vecaktrez.get(41))-1;
				}catch(Exception ex){
					
				}
				/*
				int prgruppe = 0;
				try{
					 prgruppe = Integer.parseInt((String)vecaktrez.get(41));
				}catch(Exception ex){
					
				}
				*/
				if(xreznummer.contains("KG")){
					//preisvec = ParameterLaden.vKGPreise;
					preisvec = SystemPreislisten.hmPreise.get("Physio").get(prgruppe);
				}else if(xreznummer.contains("MA")){
					//preisvec = ParameterLaden.vMAPreise;
					preisvec = SystemPreislisten.hmPreise.get("Massage").get(prgruppe);					
				}else if(xreznummer.contains("ER")){
					//preisvec = ParameterLaden.vERPreise;
					preisvec = SystemPreislisten.hmPreise.get("Ergo").get(prgruppe);
				}else if(xreznummer.contains("LO")){
					//preisvec = ParameterLaden.vLOPreise;
					preisvec = SystemPreislisten.hmPreise.get("Logo").get(prgruppe);
				}else if(xreznummer.contains("RH")){
					//preisvec = ParameterLaden.vRHPreise;
					preisvec = SystemPreislisten.hmPreise.get("Reha").get(prgruppe);
				}else if(xreznummer.contains("PO")){
					//preisvec = ParameterLaden.vRHPreise;
					preisvec = SystemPreislisten.hmPreise.get("Podo").get(prgruppe);
				}
				rezlabs[8].setText( leistungTesten(0,preisvec,StringTools.ZahlTest((String)vecaktrez.get(8))) );

				stest = StringTools.NullTest((String)vecaktrez.get(52));
				if(stest.equals("")){
					rezlabs[9].setForeground(Color.RED);
					rezlabs[9].setText(stest+"??? / Wo.");					
				}else{
					rezlabs[9].setForeground(Color.BLACK);
					rezlabs[9].setText(stest+" / Wo.");					
				}
				
				rezlabs[10].setText( leistungTesten(1,preisvec,StringTools.ZahlTest((String)vecaktrez.get(9))) );				
				rezlabs[11].setText( leistungTesten(2,preisvec,StringTools.ZahlTest((String)vecaktrez.get(10))) );				
				rezlabs[12].setText( leistungTesten(3,preisvec,StringTools.ZahlTest((String)vecaktrez.get(11))) );				

				stest = StringTools.NullTest((String)vecaktrez.get(44));
				if(stest.equals("") || stest.equals("kein IndiSchl.") ){
					if(!vecaktrez.get(1).startsWith("RH")){
						rezlabs[13].setForeground(Color.RED);
						rezlabs[13].setText("??? "+stest);
					}else{
						rezlabs[13].setText("");
					}
				}else{
					if(!vecaktrez.get(1).startsWith("RH")){
						rezlabs[13].setForeground(Color.BLACK);
						rezlabs[13].setText(stest);				
					}else{
						rezlabs[13].setText("");
					}
				}

				
				stest = StringTools.NullTest((String)vecaktrez.get(47));
				if(stest.equals("") ){
					rezlabs[14].setForeground(Color.RED);
					rezlabs[14].setText("??? Min.");
				}else{
					rezlabs[14].setForeground(Color.BLACK);
					rezlabs[14].setText(stest+" Min.");
				}
				
				rezdiag.setText(StringTools.NullTest((String)vecaktrez.get(23)));
				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}
		}.execute();

		
	}
	@SuppressWarnings("unchecked")
	public String leistungTesten(int leistung,Vector<Vector<String>> preisevec,int veczahl){
		String retwert = "----";
		if(veczahl==-1 || veczahl==0){
			return retwert;
		}
		for(int i = 0;i<preisevec.size();i++){
			if( Integer.valueOf( (String) ((Vector)preisevec.get(i)).get(preisevec.get(i).size()-1)) == veczahl ){
				return StringTools.NullTest((String)vecaktrez.get(leistung+3))+"  *  "+
				(String) ((Vector)preisevec.get(i)).get(1);
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
						"p,1dlu,p,1dlu,p,1dlu,p,1dlu,p,5dlu,p,1dlu,p,1dlu,p, 1dlu,p,5dlu,p,5dlu,p,1dlu,p,20dlu:g,0px" );		
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
		    	draghandler.setText(((String)Reha.thisClass.patpanel.patDaten.get(0)).substring(0,1)+
		    			"-"+Reha.thisClass.patpanel.patDaten.get(2)+","+Reha.thisClass.patpanel.patDaten.get(3)+"°"+
		    			reznum.getText()+"°"+rezlabs[14].getText()
		    			);
		      JComponent c = (JComponent)draghandler;
		      TransferHandler th = c.getTransferHandler();
		      th.exportAsDrag(c, e, TransferHandler.COPY); //TransferHandler.COPY
		    }
		  });
		draghandler = new JRtaTextField("GROSS",true);
		draghandler.setTransferHandler(new TransferHandler("text"));		
		/*
		rezlabs[0] = new JLabel();
		rezlabs[0].setFont(fontreznr);
		rezlabs[0].setName("rezeptnummer");
		rezlabs[0].setForeground(Color.BLUE);
		rezlabs[0].setText("KG57606");
		*/

		
		rezlabs[1] = new JLabel(" ");
		rezlabs[1].setName("hausbesuch");
		rezlabs[1].setIcon(hbimg);

		rezlabs[2] = new JLabel(" ");
		rezlabs[2].setName("angelegt");
		
		rezlabs[3] = new JLabel(" ");
		rezlabs[3].setName("kostentraeger");

		rezlabs[4] = new JLabel(" ");
		rezlabs[4].setName("arzt");
		
		rezlabs[5] = new JLabel(" ");
		rezlabs[5].setName("verornungsart");
		
		rezlabs[6] = new JLabel(" ");
		rezlabs[6].setName("begruendung");
		rezlabs[6].setForeground(Color.RED);
		
		rezlabs[7] = new JLabel(" ");
		rezlabs[7].setName("arztbericht");

		rezlabs[8] = new JLabel("");
		rezlabs[8].setName("behandlung1");
		rezlabs[8].setFont(fontbehandlung);
		rezlabs[9] = new JLabel(" ");
		rezlabs[9].setName("frequenz");
		rezlabs[9].setFont(fontbehandlung);
		
		rezlabs[10] = new JLabel(" ");
		rezlabs[10].setName("behandlung2");
		rezlabs[10].setFont(fontbehandlung);
		rezlabs[11] = new JLabel(" ");
		rezlabs[11].setName("behandlung3");
		rezlabs[11].setFont(fontbehandlung);
		rezlabs[12] = new JLabel(" ");
		rezlabs[12].setName("behandlung4");
		rezlabs[12].setFont(fontbehandlung);
		
		rezlabs[13] = new JLabel(" ");
		rezlabs[13].setName("indikation");
		rezlabs[13].setFont(fontbehandlung);
		
		rezlabs[14] = new JLabel(" ");
		rezlabs[14].setName("Dauer");
		rezlabs[14].setFont(fontbehandlung);
		
		rezdiag = new JTextArea("");
		rezdiag.setOpaque(false);
		rezdiag.setFont(new Font("Courier",Font.PLAIN,11));
		rezdiag.setForeground(Color.BLUE);
		rezdiag.setLineWrap(true);
		rezdiag.setName("notitzen");
		rezdiag.setWrapStyleWord(true);
		rezdiag.setEditable(false);


		jpan.add(reznum,cc.xy(1, 1));
		//jpan.add(rezlabs[0],cc.xy(1, 1));
		jpan.add(rezlabs[1],cc.xy(3, 1));
		jpan.add(rezlabs[2],cc.xy(5, 1));

		jpan.addSeparator("", cc.xyw(1,3,5));

		jpan.add(rezlabs[3],cc.xy(1, 5));
		jpan.add(rezlabs[4],cc.xy(5, 5));

		jpan.add(rezlabs[5],cc.xy(1, 7));
		jpan.add(rezlabs[6],cc.xy(3, 7));
		jpan.add(rezlabs[7],cc.xy(5, 7));

		jpan.addSeparator("", cc.xyw(1,9,5));
		
		jpan.add(rezlabs[8],cc.xy(1, 11));
		jpan.add(rezlabs[9],cc.xy(3, 11));
		jpan.add(rezlabs[14],cc.xy(5, 11));
		jpan.add(rezlabs[10],cc.xy(1, 13));
		jpan.add(rezlabs[11],cc.xy(1, 15));
		jpan.add(rezlabs[12],cc.xy(1, 17));
		
		
		jpan.addSeparator("", cc.xyw(1,19,5));
		
		jpan.add(rezlabs[13],cc.xy(1, 21));
		//jpan.add(rezlabs[14],cc.xy(1, 23));
		JScrollPane jscrdiag = JCompTools.getTransparentScrollPane(rezdiag);
		jscrdiag.validate();
		jpan.add(jscrdiag,cc.xywh(3, 21,3,4));
		
		jscr = JCompTools.getTransparentScrollPane(jpan.getPanel());
		jscr.getVerticalScrollBar().setUnitIncrement(15);
		jscr.validate();
		return jscr;
	}

}
