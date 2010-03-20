package org.therapi.reha.patient;

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
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;

import sqlTools.ExUndHop;
import sqlTools.SqlInfo;
import stammDatenTools.ArztTools;
import stammDatenTools.KasseTools;
import stammDatenTools.RezTools;
import systemEinstellungen.SystemConfig;
import systemTools.JCompTools;
import systemTools.JRtaTextField;
import systemTools.StringTools;
import terminKalender.ParameterLaden;
import terminKalender.TerminFenster;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class RezeptDaten2 extends JXPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6994295488322966514L;
	//public JLabel[] rezlabs = {null,null,null,null,null,null,null,null,null};
	public JRtaTextField reznum = null;
	public JRtaTextField draghandler = null;
	public ImageIcon hbimg = null; 
	public Vector<String> vecaktrez = null;
	public static boolean feddisch = false;

	public String[] rezart = {"Erstverordnung","Folgeverordnung","Folgev. außerhalb d.R."};
	public RezeptDaten2(PatientHauptPanel eltern){
		super();
		this.setOpaque(false);
		setBorder(null);
		setLayout(new BorderLayout());
		add(getDatenPanel(eltern),BorderLayout.CENTER);
		hbimg = SystemConfig.hmSysIcons.get("hausbesuch");
		eltern.rezlabs[1].setHorizontalTextPosition(JLabel.LEFT);
		eltern.rezlabs[1].addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				if( (arg0.getSource() instanceof JLabel) && (arg0.getClickCount()==2)){
					String anzhb = StringTools.NullTest((String)vecaktrez.get(64)).trim();
					Object ret = JOptionPane.showInputDialog(null, "Geben Sie bitte die neue Anzahl für Hausbesuch ein", anzhb);
					if(ret == null){
						return;
					}
					if( ! ((String)ret).trim().equals(anzhb) ){
						Reha.thisClass.patpanel.rezlabs[1].setText(((String)ret).trim()+" *");
						new ExUndHop().setzeStatement("update verordn set anzahlhb='"+((String)ret).trim()+"' "+
								"where rez_nr='"+reznum.getText()+"' LIMIT 1");
						vecaktrez.set(64, ((String)ret).trim());
					}
				}
			}
		});
	}
	public void setRezeptDaten(String reznummer,String sid){
		RezeptDaten2.feddisch = false;		
		reznum.setText(reznummer);
		Reha.thisClass.patpanel.aktRezept.rezAngezeigt = reznummer;
		//AktuelleRezepte.aktRez.rezAngezeigt = reznummer;
		final String xreznummer = reznummer;
		final String xsid = sid;
		
		new SwingWorker<Void,Void>(){
			@SuppressWarnings("unchecked")
			@Override
			protected Void doInBackground() throws Exception {
				try{
					vecaktrez = ((Vector<String>)SqlInfo.holeSatz("verordn", " * ", "id = '"+xsid+"'", Arrays.asList(new String[] {}) ));
					Reha.thisClass.patpanel.vecaktrez = vecaktrez;
					String stest = StringTools.NullTest((String)vecaktrez.get(43));
					if( stest.equals("T") ){
						Reha.thisClass.patpanel.rezlabs[1].setText(StringTools.NullTest((String)vecaktrez.get(64))+" *");
						Reha.thisClass.patpanel.rezlabs[1].setIcon(hbimg);
					}else{
						Reha.thisClass.patpanel.rezlabs[1].setText(null);
						Reha.thisClass.patpanel.rezlabs[1].setIcon(null);
					}
					Reha.thisClass.patpanel.rezlabs[2].setText("angelegt von: "+StringTools.NullTest((String)vecaktrez.get(45)));
					if(StringTools.ZahlTest( (String)vecaktrez.get(37)) >= 0 ){
						Reha.thisClass.patpanel.rezlabs[3].setForeground(Color.BLACK);
					}else{
						Reha.thisClass.patpanel.rezlabs[3].setForeground(Color.RED);					
					}
					Reha.thisClass.patpanel.rezlabs[3].setText(StringTools.NullTest((String)vecaktrez.get(36)));

					if(StringTools.ZahlTest( (String)vecaktrez.get(16)) >= 0 ){
						Reha.thisClass.patpanel.rezlabs[4].setForeground(Color.BLACK);
					}else{
						Reha.thisClass.patpanel.rezlabs[4].setForeground(Color.RED);					
					}
					Reha.thisClass.patpanel.rezlabs[4].setText(StringTools.NullTest((String)vecaktrez.get(15)));

					int test = StringTools.ZahlTest((String)vecaktrez.get(27));
					if(test >= 0){
						Reha.thisClass.patpanel.rezlabs[5].setText(rezart[test]);
						if(test==2){
							stest = StringTools.NullTest((String)vecaktrez.get(42));
							if(stest.equals("T")){
								Reha.thisClass.patpanel.rezlabs[6].setForeground(Color.BLACK);
								Reha.thisClass.patpanel.rezlabs[6].setText("Begründung o.k.");
							}else{
								Reha.thisClass.patpanel.rezlabs[6].setForeground(Color.RED);	
								Reha.thisClass.patpanel.rezlabs[6].setText("Begründung fehlt");
							}
						}else{
							Reha.thisClass.patpanel.rezlabs[6].setText(" ");
						}
					}else{
						Reha.thisClass.patpanel.rezlabs[5].setText(" ");
						Reha.thisClass.patpanel.rezlabs[6].setText(" ");
					}
					stest = StringTools.NullTest((String)vecaktrez.get(55));
					if( stest.equals("T") ){
						test = StringTools.ZahlTest((String)vecaktrez.get(54));
						if(test >= 0){
							Reha.thisClass.patpanel.rezlabs[7].setForeground(Color.BLACK);
							Reha.thisClass.patpanel.rezlabs[7].setText("Therapiebericht o.k.");
						}else{
							Reha.thisClass.patpanel.rezlabs[7].setForeground(Color.RED);
							Reha.thisClass.patpanel.rezlabs[7].setText("Therapiebericht fehlt");
						}
						
					}else{
						Reha.thisClass.patpanel.rezlabs[7].setText(" ");
					}
					Vector<Vector<String>> preisvec = null;
					if(xreznummer.contains("KG")){
						preisvec = ParameterLaden.vKGPreise;
					}else if(xreznummer.contains("MA")){
						preisvec = ParameterLaden.vMAPreise;
					}else if(xreznummer.contains("ER")){
						preisvec = ParameterLaden.vERPreise;
					}else if(xreznummer.contains("LO")){
						preisvec = ParameterLaden.vLOPreise;
					}else if(xreznummer.contains("RH")){
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
					Reha.thisClass.patpanel.rezlabs[8].setText( leistungTesten(0,preisvec,StringTools.ZahlTest((String)vecaktrez.get(8))) );

					stest = StringTools.NullTest((String)vecaktrez.get(52));
					if(stest.equals("")){
						Reha.thisClass.patpanel.rezlabs[9].setForeground(Color.RED);
						Reha.thisClass.patpanel.rezlabs[9].setText(stest+"??? / Wo.");					
					}else{
						Reha.thisClass.patpanel.rezlabs[9].setForeground(Color.BLACK);
						Reha.thisClass.patpanel.rezlabs[9].setText(stest+" / Wo.");					
					}

					Reha.thisClass.patpanel.rezlabs[10].setText( leistungTesten(1,preisvec,StringTools.ZahlTest((String)vecaktrez.get(9))) );				
					Reha.thisClass.patpanel.rezlabs[11].setText( leistungTesten(2,preisvec,StringTools.ZahlTest((String)vecaktrez.get(10))) );				
					Reha.thisClass.patpanel.rezlabs[12].setText( leistungTesten(3,preisvec,StringTools.ZahlTest((String)vecaktrez.get(11))) );				

					stest = StringTools.NullTest((String)vecaktrez.get(44));
					if(stest.equals("") || stest.equals("kein IndiSchl.") ){
						Reha.thisClass.patpanel.rezlabs[13].setForeground(Color.RED);
						Reha.thisClass.patpanel.rezlabs[13].setText("??? "+stest);					
					}else{
						Reha.thisClass.patpanel.rezlabs[13].setForeground(Color.BLACK);
						Reha.thisClass.patpanel.rezlabs[13].setText(stest);					
					}

					
					stest = StringTools.NullTest((String)vecaktrez.get(47));
					if(stest.equals("") ){
						Reha.thisClass.patpanel.rezlabs[14].setForeground(Color.RED);
						Reha.thisClass.patpanel.rezlabs[14].setText("??? Min.");
					}else{
						Reha.thisClass.patpanel.rezlabs[14].setForeground(Color.BLACK);
						Reha.thisClass.patpanel.rezlabs[14].setText(stest+" Min.");
					}
					
					Reha.thisClass.patpanel.rezdiag.setText(StringTools.NullTest((String)vecaktrez.get(23)));
					Reha.thisClass.patpanel.aktRezept.rezAngezeigt = reznum.getText();
					//AktuelleRezepte.aktRez.rezAngezeigt = reznum.getText();
					int zzbild = 0;
					try{
						zzbild = Integer.parseInt((String)vecaktrez.get(39) );
					}catch(Exception ex){
						ex.printStackTrace();
					}
					
					int row = Reha.thisClass.patpanel.aktRezept.tabaktrez.getSelectedRow();
					if(row >= 0){
						if(Reha.thisClass.patpanel.aktRezept.dtblm.getValueAt(row,1) != Reha.thisClass.patpanel.imgzuzahl[zzbild]){

							System.out.println("Zuzahlungsstatus für Bilderstellung in Reihe "+row+" = "+zzbild);
							Reha.thisClass.patpanel.aktRezept.dtblm.setValueAt(Reha.thisClass.patpanel.imgzuzahl[zzbild],row,1);
							Reha.thisClass.patpanel.aktRezept.tabaktrez.validate();
						}
					}

					RezeptDaten2.feddisch = true;

					new Thread(){
						public void run(){
							RezTools.constructVirginHMap();
							ArztTools.constructArztHMap((String)vecaktrez.get(16));
							KasseTools.constructKasseHMap((String)vecaktrez.get(37));
							//int i = RezTools.testeRezGebArt(true,reznum.getText().trim(),(String)vecaktrez.get(34));
						}
					}.start();
				
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
			if( new Integer( (String) ((Vector)preisevec.get(i)).get(35)) == veczahl ){
				return StringTools.NullTest((String)vecaktrez.get(leistung+3))+"  *  "+
				(String) ((Vector)preisevec.get(i)).get(1);
			}
		}
	
		return retwert;
	}
	
	public JScrollPane getDatenPanel(PatientHauptPanel eltern){
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
		    	int farbcode = StringTools.ZahlTest((String)vecaktrez.get(57));
		    	TerminFenster.DRAG_MODE = TerminFenster.DRAG_UNKNOWN;
		    	draghandler.setText(
		    			"TERMDATEXT"+"°"+
		    			((String)Reha.thisClass.patpanel.patDaten.get(0)).substring(0,1)+
		    			"-"+Reha.thisClass.patpanel.patDaten.get(2)+","+
		    			Reha.thisClass.patpanel.patDaten.get(3)+"°"+
		    			reznum.getText()+
		    			(farbcode > 0 ? (String)SystemConfig.vSysColsCode.get(farbcode) : "")+
		    			"°"+Reha.thisClass.patpanel.rezlabs[14].getText()
		    			);
		      JComponent c = (JComponent)draghandler;
		      TransferHandler th = c.getTransferHandler();
		      th.exportAsDrag(c, e, TransferHandler.COPY); //TransferHandler.COPY
		    }
		  });
		draghandler = new JRtaTextField("GROSS",true);
		draghandler.setTransferHandler(new TransferHandler("text"));		
		/*
		Reha.thisClass.patpanel.rezlabs[0] = new JLabel();
		Reha.thisClass.patpanel.rezlabs[0].setFont(fontreznr);
		Reha.thisClass.patpanel.rezlabs[0].setName("rezeptnummer");
		Reha.thisClass.patpanel.rezlabs[0].setForeground(Color.BLUE);
		Reha.thisClass.patpanel.rezlabs[0].setText("KG57606");
		*/

		
		eltern.rezlabs[1] = new JLabel(" ");
		eltern.rezlabs[1].setName("hausbesuch");
		eltern.rezlabs[1].setIcon(hbimg);

		eltern.rezlabs[2] = new JLabel(" ");
		eltern.rezlabs[2].setName("angelegt");
		
		eltern.rezlabs[3] = new JLabel(" ");
		eltern.rezlabs[3].setName("kostentraeger");

		eltern.rezlabs[4] = new JLabel(" ");
		eltern.rezlabs[4].setName("arzt");
		
		eltern.rezlabs[5] = new JLabel(" ");
		eltern.rezlabs[5].setName("verornungsart");
		
		eltern.rezlabs[6] = new JLabel(" ");
		eltern.rezlabs[6].setName("begruendung");
		eltern.rezlabs[6].setForeground(Color.RED);
		
		eltern.rezlabs[7] = new JLabel(" ");
		eltern.rezlabs[7].setName("arztbericht");

		eltern.rezlabs[8] = new JLabel("");
		eltern.rezlabs[8].setName("behandlung1");
		eltern.rezlabs[8].setFont(fontbehandlung);
		eltern.rezlabs[9] = new JLabel(" ");
		eltern.rezlabs[9].setName("frequenz");
		eltern.rezlabs[9].setFont(fontbehandlung);
		
		eltern.rezlabs[10] = new JLabel(" ");
		eltern.rezlabs[10].setName("behandlung2");
		eltern.rezlabs[10].setFont(fontbehandlung);
		eltern.rezlabs[11] = new JLabel(" ");
		eltern.rezlabs[11].setName("behandlung3");
		eltern.rezlabs[11].setFont(fontbehandlung);
		eltern.rezlabs[12] = new JLabel(" ");
		eltern.rezlabs[12].setName("behandlung4");
		eltern.rezlabs[12].setFont(fontbehandlung);
		
		eltern.rezlabs[13] = new JLabel(" ");
		eltern.rezlabs[13].setName("indikation");
		eltern.rezlabs[13].setFont(fontbehandlung);
		
		eltern.rezlabs[14] = new JLabel(" ");
		eltern.rezlabs[14].setName("Dauer");
		eltern.rezlabs[14].setFont(fontbehandlung);
		
		eltern.rezdiag = new JTextArea("");
		eltern.rezdiag.setOpaque(false);
		eltern.rezdiag.setFont(new Font("Courier",Font.PLAIN,11));
		eltern.rezdiag.setForeground(Color.BLUE);
		eltern.rezdiag.setLineWrap(true);
		eltern.rezdiag.setName("notitzen");
		eltern.rezdiag.setWrapStyleWord(true);
		eltern.rezdiag.setEditable(false);


		jpan.add(reznum,cc.xy(1, 1));
		//jpan.add(Reha.thisClass.patpanel.rezlabs[0],cc.xy(1, 1));
		jpan.add(eltern.rezlabs[1],cc.xy(3, 1));
		jpan.add(eltern.rezlabs[2],cc.xy(5, 1));

		jpan.addSeparator("", cc.xyw(1,3,5));

		jpan.add(eltern.rezlabs[3],cc.xy(1, 5));
		jpan.add(eltern.rezlabs[4],cc.xy(5, 5));

		jpan.add(eltern.rezlabs[5],cc.xy(1, 7));
		jpan.add(eltern.rezlabs[6],cc.xy(3, 7));
		jpan.add(eltern.rezlabs[7],cc.xy(5, 7));

		jpan.addSeparator("", cc.xyw(1,9,5));
		
		jpan.add(eltern.rezlabs[8],cc.xy(1, 11));
		jpan.add(eltern.rezlabs[9],cc.xy(3, 11));
		jpan.add(eltern.rezlabs[14],cc.xy(5, 11));
		jpan.add(eltern.rezlabs[10],cc.xy(1, 13));
		jpan.add(eltern.rezlabs[11],cc.xy(1, 15));
		jpan.add(eltern.rezlabs[12],cc.xy(1, 17));
		
		
		jpan.addSeparator("", cc.xyw(1,19,5));
		
		jpan.add(eltern.rezlabs[13],cc.xy(1, 21));
		//jpan.add(Reha.thisClass.patpanel.rezlabs[14],cc.xy(1, 23));
		JScrollPane jscrdiag = JCompTools.getTransparentScrollPane(eltern.rezdiag);
		jscrdiag.validate();
		jpan.add(jscrdiag,cc.xywh(3, 21,3,4));
		
		jscr = JCompTools.getTransparentScrollPane(jpan.getPanel());
		jscr.getVerticalScrollBar().setUnitIncrement(15);
		jscr.validate();
		return jscr;
	}

}
