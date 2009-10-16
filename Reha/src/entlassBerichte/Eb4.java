package entlassBerichte;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXPanel;

import sqlTools.SqlInfo;
import systemTools.JCompTools;
import systemTools.JRtaComboBox;
import systemTools.JRtaTextField;
import terminKalender.datFunk;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class Eb4 implements ActionListener {
	EBerichtPanel eltern = null;
	JXPanel pan = null;
	Font fontgross = null;
	Font fontklein = null;
	Font fontnormal = null;
	Font fontcourier = null;
	Font fontarialfett = null;
	Font fontarialnormal = null;
	Font fontarialfettgross = null;
	JScrollPane jscr = null;
	boolean inGuiInit = true;
	public Eb4(EBerichtPanel xeltern){
		pan = new JXPanel(new BorderLayout());
		pan.setOpaque(false);
		pan.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		eltern = xeltern;
		fontgross =new Font("Arial",Font.BOLD,14);
		fontklein =new Font("Arial",Font.PLAIN,9);
		fontnormal =new Font("Arial",Font.PLAIN,11);
		fontarialfett =new Font("Arial",Font.BOLD,11);
		fontarialnormal =new Font("Arial",Font.PLAIN,11);		
		fontcourier =new Font("Courier New",Font.PLAIN,12);
		fontarialfettgross = new Font("Arial",Font.BOLD,12);



		new SwingWorker<Void,Void>(){
			
			@Override
			protected Void doInBackground() throws Exception {
				try{
					pan.add(constructSeite());
					for(int i = 8; i < 10;i++){
						eltern.bta[i].setFont(fontcourier);
						eltern.bta[i].setForeground(Color.BLUE);
						eltern.bta[i].setWrapStyleWord(true);
						eltern.bta[i].setLineWrap(true);
						eltern.bta[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
					}
					new Thread(){
						public void run(){
							laden(eltern.neu);
							inGuiInit = false;
						}
					}.start();
				}catch(Exception ex){
					ex.printStackTrace();
				}
			return null;
			}
		}.execute();	
	}
	public JXPanel getSeite(){
		return pan;
	}
	public JScrollPane constructSeite(){
		FormLayout lay = new FormLayout("10dlu,fill:0:grow(0.25),p,fill:0:grow(0.25),10dlu",
				// 1    2   3    4   5   6  7     
				"20dlu, p ,20dlu,p,20dlu");
		PanelBuilder pb = new PanelBuilder(lay);
		pb.setOpaque(false);
		CellConstraints cc = new CellConstraints();
		pb.add(getSeiten(1),cc.xy(3,2,CellConstraints.FILL,CellConstraints.DEFAULT));
		pb.add(getSeiten(2),cc.xy(3,4,CellConstraints.FILL,CellConstraints.DEFAULT));

		jscr = JCompTools.getTransparentScrollPane(pb.getPanel());
		jscr.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
		jscr.getVerticalScrollBar().setUnitIncrement(15);
		jscr.validate();
		return jscr;
	}
	
	private JPanel getSeiten(int iseite){
		FormLayout laySeite = new FormLayout("fill:0:grow(0.5),550dlu,fill:0:grow(0.5)",
				//  1    2   3   4   5   6  7    8  9   10  11
				  " 0dlu,p, 5dlu,p, 5dlu,p, 0dlu,p,10dlu");		
		PanelBuilder seite = new PanelBuilder(laySeite);
		seite.setOpaque(false); 
		CellConstraints ccseite = new CellConstraints();
		seite.getPanel().setBorder(BorderFactory.createLineBorder(Color.BLACK));
		seite.add(getKopf(iseite),ccseite.xy(2,2));	
		seite.add(getLegende(),ccseite.xy(2,4));
		seite.add(getLeistung(iseite),ccseite.xy(2,6));
		seite.add(getErlaeuterung(iseite),ccseite.xy(2,8,CellConstraints.FILL,CellConstraints.DEFAULT));
		seite.getPanel().validate();
		return seite.getPanel();
		
	}
	private JPanel getErlaeuterung(int seite){
		FormLayout layErlaeut = new FormLayout("fill:0:grow(1.0)","p");
		PanelBuilder erleut = new PanelBuilder(layErlaeut);
		erleut.setOpaque(false); 
		CellConstraints ccerlaeut = new CellConstraints();
									//      1    2     3   4    5        6            7     8      9    10    1     12          13
		FormLayout dummy = new FormLayout("2dlu,15dlu,2dlu,1px,10dlu,fill:0:grow(1.0),10dlu,50dlu,10dlu,30dlu,10dlu,right:40dlu,10dlu",
	//    1   2  3    4     5   6
		"2dlu,p,5dlu,30dlu,5dlu,p");
		PanelBuilder dum = new PanelBuilder(dummy);
		dum.getPanel().setOpaque(false);
		CellConstraints ccdum = new CellConstraints();
		JLabel jlab = new JLabel("Erläuterungen:");
		jlab.setFont(fontarialfett);
		dum.add(jlab,ccdum.xywh(3,2,6,1));
		int area = (seite == 1 ? 8 : 9);
		String name = (seite == 1 ? "TERLEUT1" : "TERLEUT2");
		eltern.bta[area] = new JTextArea();
		eltern.bta[area].setName(name);
		dum.add(eltern.bta[area],ccdum.xywh(3,4,10,1,CellConstraints.DEFAULT,CellConstraints.FILL));
		FormLayout layKodierung = new FormLayout(
				"fill:0:grow(0.10),2dlu,fill:0:grow(0.10),2dlu,fill:0:grow(0.10),2dlu,fill:0:grow(0.10),2dlu,fill:0:grow(0.10),2dlu,"+
				"fill:0:grow(0.10),2dlu,fill:0:grow(0.10),2dlu,fill:0:grow(0.10),2dlu,fill:0:grow(0.10),2dlu,fill:0:grow(0.10)"
				,"p");
		PanelBuilder kodierung = new PanelBuilder(layKodierung); 
		kodierung.setOpaque(false);
		CellConstraints cckodierung = new CellConstraints();
		String [] code = {"A = ","B = ","C = ","D = ","E = ","F = ","G = ","H = ","I = ",
				"K = ","L = ","M = ","N = ","P = ","Q = ","R = ","S = ","T = ","U = ","Z = "};
		String [] zeit = {"5 Min.","10 Min.","15 Min.","20 Min.","25 Min.","30 Min.","35 Min.","40 Min.","45 Min.",
				"50 Min.","60 Min.","75 Min.","90 Min.","100 Min.","120 Min.","150 Min.","180 Min.","240 Min.","300 Min.","individuell"};
		int durchlauf = 1;
		int codes = 1;
		int xpos = 1;
		int item = 0;
		FormLayout layCelle = new FormLayout("p,p","p,p");
		PanelBuilder celle;
		CellConstraints cccelle;
		for(int i = 0; i < code.length;i+=2){
			celle = new PanelBuilder(layCelle);
			celle.getPanel().setOpaque(false);
			cccelle = new CellConstraints();
			celle.add(getLabel(code[item]),cckodierung.xy(1,1,CellConstraints.LEFT,CellConstraints.DEFAULT));
			celle.add(getLabelKleinRot(zeit[item]),cckodierung.xy(2,1,CellConstraints.RIGHT,CellConstraints.DEFAULT));
			celle.add(getLabel(code[item+1]),cckodierung.xy(1,2,CellConstraints.LEFT,CellConstraints.DEFAULT));
			celle.add(getLabelKleinRot(zeit[item+1]),cckodierung.xy(2,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
			celle.getPanel().validate();
			kodierung.add(celle.getPanel(),cckodierung.xy(xpos, 1));
			durchlauf ++;
			codes +=2;
			xpos += 2;
			item += 2;
		}
		kodierung.getPanel().validate();
		dum.add(kodierung.getPanel(),ccdum.xywh(3,6,11,1,CellConstraints.FILL,CellConstraints.FILL));
		dum.getPanel().validate();
		erleut.add(dum.getPanel(),ccerlaeut.xy(1, 1));

		

		erleut.getPanel().validate();
		return erleut.getPanel(); 
	}
	private JPanel getLegende(){
		FormLayout layLegende = new FormLayout("fill:0:grow(1.0)","p");
		PanelBuilder legende = new PanelBuilder(layLegende);
		legende.setOpaque(false); 
		CellConstraints cclegende = new CellConstraints();
								//           1    2    3   4     5          6          7    8      9    10     11        12          13
		FormLayout dummy = new FormLayout("2dlu,15dlu,2dlu,1px,10dlu,fill:0:grow(1.0),10dlu,50dlu,10dlu,30dlu,10dlu,right:40dlu,10dlu",
		"15dlu,0dlu");
		PanelBuilder dum = new PanelBuilder(dummy);
		dum.getPanel().setOpaque(false);
		CellConstraints ccdum = new CellConstraints();
		dum.add(getLabelKleinRot("Leistung im Klartext"),ccdum.xy(6,1,CellConstraints.LEFT,CellConstraints.DEFAULT));
		dum.add(getLabelKleinRot("KTL - Code"),ccdum.xy(8,1,CellConstraints.LEFT,CellConstraints.DEFAULT));
		dum.add(getLabelKleinRot("Dauer"),ccdum.xy(10,1,CellConstraints.LEFT,CellConstraints.DEFAULT));
		dum.add(getLabelKleinRot("Anzahl"),ccdum.xy(12,1,CellConstraints.LEFT,CellConstraints.DEFAULT));
		dum.getPanel().validate();
		legende.add(dum.getPanel(),cclegende.xy(1, 1));
		legende.getPanel().validate();
		return legende.getPanel();
		
	}	


	private JPanel getLeistung(int seite){
		FormLayout layKopf = new FormLayout("fill:0:grow(1.0)",
			    //           5=F174  7=F175  9=F176   11=F177
				// 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6  
				  "p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p");		
		PanelBuilder kopf = new PanelBuilder(layKopf);
		kopf.setOpaque(false); 
		CellConstraints cckopf = new CellConstraints();
								//           1    2    3   4     5          6         7    8      9    10     11        12       13
		FormLayout dummy = new FormLayout("2dlu,15dlu,2dlu,1px,10dlu,fill:0:grow(1.0),10dlu,50dlu,10dlu,30dlu,10dlu,right:40dlu,10dlu",
		"1px,20dlu");
		PanelBuilder dum;
		CellConstraints ccdum;
		JLabel lab;
		for(int i = 0; i < 25; i++){
			dum = new PanelBuilder(dummy);
			dum.getPanel().setOpaque(false);
			ccdum = new CellConstraints();
			lab = new JLabel(new Integer(i+1+((seite-1)*25)).toString()+".");
			dum.add(lab,ccdum.xy(2,2,CellConstraints.RIGHT,CellConstraints.CENTER));
			dum.add(getRand(Color.GRAY),cckopf.xywh(4, 1, 1, 2,CellConstraints.DEFAULT,CellConstraints.FILL));
			dum.add(getRand(Color.GRAY),cckopf.xywh(1, 1, 13, 1,CellConstraints.FILL,CellConstraints.DEFAULT));				
			eltern.ktlcmb[i+((seite-1)*25)] = new JRtaComboBox();
			eltern.ktlcmb[i+((seite-1)*25)].setActionCommand(new Integer(i+((seite-1)*25)).toString());
			eltern.ktlcmb[i+((seite-1)*25)].addActionListener(this);
			eltern.ktlcmb[i+((seite-1)*25)].setMaximumRowCount( 35 );
			// set Name nacholen
			dum.add(eltern.ktlcmb[i+((seite-1)*25)],ccdum.xy(6, 2,CellConstraints.FILL,CellConstraints.CENTER));
			eltern.ktltfc[i+((seite-1)*25)] = new JRtaTextField("nix",false);
			eltern.ktltfc[i+((seite-1)*25)].setFont(fontcourier);
			eltern.ktltfc[i+((seite-1)*25)].setForeground(Color.BLUE);

			// set Name nacholen
			dum.add(eltern.ktltfc[i+((seite-1)*25)],ccdum.xy(8, 2,CellConstraints.FILL,CellConstraints.CENTER));
			eltern.ktltfd[i+((seite-1)*25)] = new JRtaTextField("nix",false);
			eltern.ktltfd[i+((seite-1)*25)].setFont(fontcourier);
			eltern.ktltfd[i+((seite-1)*25)].setForeground(Color.BLUE);

			// set Name nacholen
			dum.add(eltern.ktltfd[i+((seite-1)*25)],ccdum.xy(10, 2,CellConstraints.FILL,CellConstraints.CENTER));
			eltern.ktltfa[i+((seite-1)*25)] = new JRtaTextField("ZAHLEN",false);
			eltern.ktltfa[i+((seite-1)*25)].setFont(fontcourier);
			eltern.ktltfa[i+((seite-1)*25)].setForeground(Color.BLUE);
			// set Name nacholen
			dum.add(eltern.ktltfa[i+((seite-1)*25)],ccdum.xy(12, 2,CellConstraints.FILL,CellConstraints.CENTER));			
			dum.getPanel().validate();
			kopf.add(dum.getPanel(),cckopf.xy(1,(i+1)) );
			/*
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			*/
		}
		dummy = new FormLayout("2dlu,15dlu,2dlu,1px,10dlu,fill:0:grow(1.0),10dlu,50dlu,10dlu,30dlu,10dlu,right:40dlu,10dlu",
		"1px,0dlu");
		dum = new PanelBuilder(dummy);
		dum.getPanel().setOpaque(false);
		ccdum = new CellConstraints();
		dum.add(getRand(Color.GRAY),cckopf.xywh(1, 1, 13, 1,CellConstraints.FILL,CellConstraints.DEFAULT));
		kopf.add(dum.getPanel(),cckopf.xy(1,26) );
		kopf.getPanel().validate();
		return kopf.getPanel();
	}
	private JPanel getKopf(int seite){
		FormLayout layKopf = new FormLayout("fill:0:grow(1.0)",
			    //           5=F174  7=F175  9=F176   11=F177
				//  1   2   3  4    5     6     7 
				  "p,p,p");		
		PanelBuilder kopf = new PanelBuilder(layKopf);
		kopf.setOpaque(false); 
		CellConstraints cckopf = new CellConstraints();
		kopf.getPanel().validate();
										// 1    2   3  4      5                 6             7     1    2  3
		FormLayout dummy = new FormLayout("2dlu,p,2dlu,p,fill:0:grow(1.0),right:max(50dlu;p),2dlu",
				"2dlu,p,5dlu");
		PanelBuilder dum = new PanelBuilder(dummy);
		dum.getPanel().setOpaque(false);
		CellConstraints ccdum = new CellConstraints();
		JLabel lab = new JLabel("Leistungsdaten:");
		lab.setFont(fontarialfett);
		dum.add(lab,ccdum.xy(2,2));
		lab = new JLabel("durchgeführte therapeutische Leistungen");
		lab.setFont(fontarialnormal);
		dum.add(lab,ccdum.xy(4,2));

		lab = new JLabel("Bl. 1b - "+seite+(seite==1 ? " (1-25)" : " (26-50)"));
		lab.setFont(fontarialfett);
		dum.add(lab,ccdum.xy(6,2));
		dum.getPanel().validate();
		kopf.add(dum.getPanel(),cckopf.xy(1,1,CellConstraints.FILL,CellConstraints.DEFAULT));

		kopf.getPanel().validate();
		return kopf.getPanel();
	}
	
	
	
	private void laden(boolean lneu){
		Vector<Vector<String>> vec = null;
		if(lneu){
			holeKTL(true);
		}else{
			vec = SqlInfo.holeFelder("select entdat3 from bericht2 where berichtid='"+
					new Integer(eltern.berichtid).toString()+"' LIMIT 1" );
			if(vec.size() <= 0){
				JOptionPane.showMessageDialog(null,"Achtung - kann KTL-Leistungen nicht laden");
				return;
			}
			if(vec.size() > 0){
				System.out.println("Das Datum = ***************"+vec.get(0).get(0));
				if(! vec.get(0).get(0).trim().equals("")){
					String entdat = datFunk.sDatInDeutsch(vec.get(0).get(0));
					if(datFunk.DatumsWert(entdat) < datFunk.DatumsWert("01.01.2007")){
						holeKTL(false);
					}else{
						holeKTL(true);
					}
				}else{
					holeKTL(true);
				}
			}else{
				holeKTL(true);
			}
		}
		if(!eltern.neu){
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					try{
						Vector<Vector<String>> vec = null;
						int istnull = 0;
						int pos = 0;
						vec = SqlInfo.holeFelder("select * from bericht2ktl where berichtid='"+
								new Integer(eltern.berichtid).toString()+"' LIMIT 1" );
						for(int i = 1;i<50;i++){
							pos = (i*4);
							//System.out.println(new Integer(i).toString()+". Massnahmennummer = "+vec.get(0).get(pos));
							if(vec.get(0).get(pos).equals("0")){
								istnull++;
								if(istnull > 3){
									break;
								}
							}else{
								eltern.ktlcmb[i-1].setSelectedVecIndex(3,
										(vec.get(0).get(pos).trim().equals("") ? "0" : vec.get(0).get(pos) ));
								//eltern.ktlcmb[i-1].setSelectedIndex(massnahme);
								eltern.ktltfc[i-1].setText(vec.get(0).get(pos+1));
								eltern.ktltfd[i-1].setText(vec.get(0).get(pos+2));
								eltern.ktltfa[i-1].setText(vec.get(0).get(pos+3));
							}
							

						}
						eltern.bta[8].setText(vec.get(0).get(2));
						eltern.bta[9].setText(vec.get(0).get(3));
						
					}catch(Exception ex){
						ex.printStackTrace();
					}
					return null;
				}
				
			}.execute();
		}
	}
	private void holeKTL(boolean ktlneu){
		Vector<Vector<String>> vec = null;
		String ktltabelle = "";
		if(ktlneu){
			ktltabelle = "masntex2";
		}else{
			ktltabelle = "masntext";
		}
		vec = SqlInfo.holeFelder("select * from "+ktltabelle);
		Comparator<Vector> comparator = new Comparator<Vector>() {
		    public int compare(String s1, String s2) {
		        String[] strings1 = s1.split("\\s");
		        String[] strings2 = s2.split("\\s");
		        return strings1[strings1.length - 1]
		            .compareTo(strings2[strings2.length - 1]);
		    }

			@Override
			public int compare(Vector o1, Vector o2) {
				// TODO Auto-generated method stub
				String s1 = (String)o1.get(0);
				String s2 = (String)o2.get(0);
				return s1.compareTo(s2);
			}
		};
		Collections.sort(vec,comparator);		
		
		//System.out.println("Dit ktltabelle = "+ktltabelle);
		Vector<String> vec2 = new Vector<String>();
		vec2.add("./.");
		vec2.add("");
		vec2.add("");
		vec2.add("0");
		vec.insertElementAt((Vector)((Vector<String>)vec2).clone(), 0);
		for(int i = 0;i < 50;i++){
			eltern.ktlcmb[i].setDataVectorVector((Vector<Vector<String>>)vec, 0, 3);
			eltern.ktlcmb[i].setName("TMA"+(i+1));
			eltern.ktltfc[i].setName("TKT"+(i+1));
			eltern.ktltfd[i].setName("TZT"+(i+1));			
			eltern.ktltfa[i].setName("TAZ"+(i+1));			
		}
	}
	private JPanel getRand(Color col){
		JPanel pan = new JPanel();
		pan.setOpaque(false);
		pan.setBorder(BorderFactory.createLineBorder(col));
		return pan;
	}
	private JLabel getLabel(String text){
		JLabel lab = new JLabel(text);
		lab.setFont(fontklein);
		return lab;
	}	
	private JLabel getLabelKleinRot(String text){
		JLabel lab = new JLabel(text);
		lab.setFont(fontklein);
		lab.setForeground(Color.RED);
		return lab;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(inGuiInit){
			return;
		}
		String cmd = arg0.getActionCommand();
		int combo = new Integer(cmd);

		eltern.ktltfc[combo].setText((String)eltern.ktlcmb[combo].getValueAt(1));
		eltern.ktltfd[combo].setText((String)eltern.ktlcmb[combo].getValueAt(2));
		if(eltern.ktlcmb[combo].getSelectedIndex()==0){
			eltern.ktltfa[combo].setText("");
			
		}
	}
	
	
}
