package entlassBerichte;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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

public class Eb4 {
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

		pan.add(constructSeite());

		new SwingWorker<Void,Void>(){
			
			@Override
			protected Void doInBackground() throws Exception {
				try{
					laden(eltern.neu);
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
				// 1    2   3     4   5    
				"20dlu, p ,10dlu, p,20dlu");
		PanelBuilder pb = new PanelBuilder(lay);
		pb.setOpaque(false);
		CellConstraints cc = new CellConstraints();
		pb.add(getSeite1(1),cc.xy(3,2,CellConstraints.FILL,CellConstraints.DEFAULT));
		jscr = JCompTools.getTransparentScrollPane(pb.getPanel());
		jscr.getVerticalScrollBar().setUnitIncrement(15);
		jscr.validate();
		return jscr;
	}
	
	private JPanel getSeite1(int iseite){
		FormLayout laySeite = new FormLayout("fill:0:grow(0.5),550dlu,fill:0:grow(0.5)",
				//  1    2   3  4    5     6     7 
				  " 0dlu,p, 5dlu,p");		
		PanelBuilder seite = new PanelBuilder(laySeite);
		seite.setOpaque(false); 
		CellConstraints ccseite = new CellConstraints();
		seite.getPanel().setBorder(BorderFactory.createLineBorder(Color.BLACK));
		seite.add(getKopf(iseite),ccseite.xy(2,2));		
		seite.add(getLeistung(iseite),ccseite.xy(2,4));
		seite.getPanel().validate();
		return seite.getPanel();
		
	}
	private JPanel getLeistung(int seite){
		FormLayout layKopf = new FormLayout("fill:0:grow(1.0)",
			    //           5=F174  7=F175  9=F176   11=F177
				// 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5  
				  "p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p");		
		PanelBuilder kopf = new PanelBuilder(layKopf);
		kopf.setOpaque(false); 
		CellConstraints cckopf = new CellConstraints();
								//           1    2    3   4     5          6         7    8     9    10             11        12
		FormLayout dummy = new FormLayout("2dlu,15dlu,2dlu,1px,2dlu,fill:0:grow(1.0),2dlu,30dlu,2dlu,15dlu,right:max(30dlu;p),2dlu",
		"1px,15dlu");
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
			dum.add(getRand(Color.GRAY),cckopf.xywh(1, 1, 12, 1,CellConstraints.FILL,CellConstraints.DEFAULT));				
			eltern.ktlcmb[i+((seite-1)*25)] = new JRtaComboBox();
			dum.add(eltern.ktlcmb[i],ccdum.xy(6, 2,CellConstraints.FILL,CellConstraints.CENTER));
			kopf.add(dum.getPanel(),cckopf.xy(1,(i+1)) );
		}

		kopf.getPanel().validate();
		return kopf.getPanel();
	}
	private JPanel getKopf(int seite){
		FormLayout layKopf = new FormLayout("fill:0:grow(1.0)",
			    //           5=F174  7=F175  9=F176   11=F177
				//  1   2   3  4    5     6     7 
				  "p,15dlu,p");		
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
			String entdat = datFunk.sDatInDeutsch(vec.get(0).get(0));
			if(datFunk.DatumsWert(entdat) < datFunk.DatumsWert("01.01.2008")){
				holeKTL(false);
			}else{
				holeKTL(true);
			}
		}
	}
	private void holeKTL(boolean ktlneu){
		Vector<Vector<String>> vec = null;
		String ktltabelle = "";
		if(ktlneu){
			ktltabelle = "masntext2";
		}else{
			ktltabelle = "masntext";
		}
		vec = SqlInfo.holeFelder("select * from "+ktltabelle);
		System.out.println("Dit ktltabelle = "+ktltabelle);
		Vector<String> vec2 = new Vector<String>();
		vec2.add("./.");
		vec2.add("");
		vec2.add("");
		vec2.add("0");
		vec.insertElementAt((Vector)((Vector<String>)vec2).clone(), 0);
		for(int i = 0;i < 50;i++){
			eltern.ktlcmb[i].setDataVectorVector((Vector<Vector<String>>)vec, 0, 3);
			//eltern.ktlcmb[i] = new JRtaComboBox((Vector<Vector<String>>)vec,0,3);
			eltern.ktltfc[i] = new JRtaTextField("GROSS",false);
			eltern.ktltfd[i] = new JRtaTextField("GROSS",false);
			eltern.ktltfa[i] = new JRtaTextField("ZAHLEN",false);
		}
		eltern.ktlcmb[3].setSelectedIndex(5);
		System.out.println(" Der Rückgabewert der Combobox = "+eltern.ktlcmb[3].getValue());
	}
	private JPanel getRand(Color col){
		JPanel pan = new JPanel();
		pan.setOpaque(false);
		pan.setBorder(BorderFactory.createLineBorder(col));
		return pan;
	}
	
}
