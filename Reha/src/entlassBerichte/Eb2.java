package entlassBerichte;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import org.jdesktop.swingx.JXPanel;

import systemTools.JCompTools;
import systemTools.JRtaCheckBox;
import systemTools.JRtaTextField;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class Eb2 {
	EBerichtPanel eltern = null;
	JXPanel pan = null;
	Font fontgross = null;
	Font fontklein = null;
	Font fontnormal = null;
	Font fontcourier = null;
	Font fontarialfett = null;
	Font fontarialnormal = null;
	
	public Eb2(EBerichtPanel xeltern){
		pan = new JXPanel(new BorderLayout());
		pan.setOpaque(false);
		pan.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		eltern = xeltern;
		fontgross =new Font("Arial",Font.BOLD,14);
		fontklein =new Font("Arial",Font.PLAIN,9);
		fontnormal =new Font("Arial",Font.PLAIN,10);
		fontarialfett =new Font("Arial",Font.BOLD,12);
		fontarialnormal =new Font("Arial",Font.PLAIN,12);		
		fontcourier =new Font("Courier New",Font.PLAIN,12);
		pan.add(constructSeite(),BorderLayout.CENTER);
		pan.validate();
		pan.setVisible(true);
	}
	
		
		
	public JXPanel getSeite(){
		return pan;
	}
	public JScrollPane constructSeite(){
		FormLayout lay = new FormLayout("5dlu,fill:0:grow(0.25),20dlu,p,fill:0:grow(0.25),5dlu",
				
				//   2=titel   4=block1 6=block2 8=block3
				// 1    2   3    4  5    6      7   9   10  11   12  13 14
				"20dlu, p ,2dlu, p, p , 20dlu,  p,5dlu,p ,5dlu,p,  5dlu,p, 0dlu,p, 4dlu");
		PanelBuilder pb = new PanelBuilder(lay);
		pb.setOpaque(false);
		CellConstraints cc = new CellConstraints();
		//pb.addLabel("Das ist ein Label",cc.xy(2,2));
		pb.add(getTitel1(),cc.xy(4,2,CellConstraints.FILL,CellConstraints.CENTER));
		pb.add(getTitel2(),cc.xyw(3,4,2,CellConstraints.LEFT,CellConstraints.CENTER));
		pb.add(getKasten1(),cc.xy(4,5,CellConstraints.RIGHT,CellConstraints.FILL));
		pb.add(getTitel3(),cc.xyw(3,7,2,CellConstraints.LEFT,CellConstraints.BOTTOM));
		//pb.add(getBezeichnung(),cc.xy(4,5,CellConstraints.RIGHT,CellConstraints.FILL));
		//pb.add(getBeurteilung1(),cc.xy(4,6,CellConstraints.RIGHT,CellConstraints.FILL));
		
		JScrollPane jscr = JCompTools.getTransparentScrollPane(pb.getPanel());
		jscr.getVerticalScrollBar().setUnitIncrement(15);
		jscr.validate();
		return jscr;
	}
	private JPanel getTitel3(){
		FormLayout laytit = new FormLayout("20dlu,p,2dlu,p",
		"4dlu,p,2dlu,p,4dlu");
		PanelBuilder tit = new PanelBuilder(laytit);
		//tit.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		tit.setOpaque(false);               
		CellConstraints cctit = new CellConstraints();
		
		JLabel lab = new JLabel("B.");
		lab.setFont(fontarialfett);
		tit.add(lab,cctit.xy(1, 2));
		lab = new JLabel("Positives und negatives Leistungsvermögen");
		lab.setFont(fontarialfett);
		tit.add(lab,cctit.xy(2, 2));
		lab = new JLabel("(allgemeinser Arbeitsmarkt)");
		lab.setFont(fontarialnormal);
		tit.add(lab,cctit.xy(4, 2));
		lab = new JLabel("Zutreffendes bitte ankreuzen (X), Mehrfachnennungen sind möglich");
		lab.setFont(fontarialnormal);
		tit.add(lab,cctit.xyw(2, 4,3,CellConstraints.FILL,CellConstraints.DEFAULT));
		tit.getPanel().validate();
		return tit.getPanel();		
	}
	private JPanel getKasten1(){
		FormLayout laytit = new FormLayout("530dlu",
		"0dlu,p,0dlu,p");
		PanelBuilder tit = new PanelBuilder(laytit);
		//Border bd = BorderFactory.createLineBorder(Color.BLACK);
		tit.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		tit.setOpaque(false);		
		CellConstraints cctit = new CellConstraints();
		tit.add(getBezeichnung(),cctit.xy(1,2,CellConstraints.RIGHT,CellConstraints.FILL));
		tit.add(getBeurteilung1(),cctit.xy(1,4,CellConstraints.RIGHT,CellConstraints.FILL));
		tit.getPanel().validate();
		return tit.getPanel();
	}
	private JPanel getBeurteilung1(){
		FormLayout laytit = new FormLayout("530dlu",
		"1px,p,0dlu");
		PanelBuilder tit = new PanelBuilder(laytit);
		//Border bd = BorderFactory.createLineBorder(Color.BLACK);
		//tit.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		tit.setOpaque(false);               
		CellConstraints cctit = new CellConstraints();
		tit.add(getRand(Color.GRAY),cctit.xy(1, 1,CellConstraints.FILL,CellConstraints.TOP));
		//                                                         6=1.Cmb     8=1.Lab 
		//                                  1    2      3   4   5     6   7      8
		FormLayout links = new FormLayout("2dlu,150dlu,2dlu,1px,30dlu,p,2dlu,fill:0:grow(0.33),"+
			//   9   10   11             12  13      14     
				"p,2dlu,fill:0:grow(0.33),p,2dlu,fill:0:grow(0.33)",
				
			//   1    2 3 4  5   6
				"0dlu,p,p,p,1px,0dlu");
		PanelBuilder plinks = new PanelBuilder(links);
		plinks.getPanel().setOpaque(false);
		CellConstraints ccli = new CellConstraints();
		plinks.add(getLabelKleinRot("Beurteilung des zeitlichen Umfangs. in dem"),ccli.xy(2,2));
		plinks.add(getLabelKleinRot("die letzte berufliche Tätigkeit ausgeübt"),ccli.xy(2,3));
		plinks.add(getLabelKleinRot("werden kann"),ccli.xy(2,4));
		plinks.add(getRand(Color.GRAY),ccli.xywh(4, 1, 1, 5));
		plinks.add(getLabelKleinRot("153"),ccli.xywh(6,2,1,2,CellConstraints.DEFAULT,CellConstraints.CENTER));
		eltern.bchb[17] = new JRtaCheckBox("");
		eltern.bchb[17].setName("F_153");
		plinks.add(eltern.bchb[17],ccli.xywh(6, 3,1, 2,CellConstraints.DEFAULT,CellConstraints.BOTTOM));
		plinks.add(getLabel("6 Stunden und mehr"),ccli.xywh(8, 3,1, 2,CellConstraints.DEFAULT,CellConstraints.BOTTOM));
		plinks.add(getLabelKleinRot("154"),ccli.xywh(9,2,1,2,CellConstraints.DEFAULT,CellConstraints.CENTER));
		eltern.bchb[18] = new JRtaCheckBox("");
		eltern.bchb[18].setName("F_154");
		plinks.add(eltern.bchb[18],ccli.xywh(9, 3,1, 2,CellConstraints.DEFAULT,CellConstraints.BOTTOM));
		plinks.add(getLabel("3 bis unter 6 Stunden"),ccli.xywh(11, 3,1, 2,CellConstraints.DEFAULT,CellConstraints.BOTTOM));
		plinks.add(getLabelKleinRot("156"),ccli.xywh(12,2,1,2,CellConstraints.DEFAULT,CellConstraints.CENTER));
		eltern.bchb[19] = new JRtaCheckBox("");
		eltern.bchb[19].setName("F_156");
		plinks.add(eltern.bchb[19],ccli.xywh(12, 3,1, 2,CellConstraints.DEFAULT,CellConstraints.BOTTOM));
		plinks.add(getLabel("unter 3 Stunden"),ccli.xywh(14, 3,1, 2,CellConstraints.DEFAULT,CellConstraints.BOTTOM));

		
		tit.add(plinks.getPanel(),cctit.xy(1,2));		

		return tit.getPanel();
	}
	private JPanel getBezeichnung(){
		FormLayout laytit = new FormLayout("530dlu",
		"0px,p,0dlu");
		PanelBuilder tit = new PanelBuilder(laytit);
		
		//tit.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		tit.setOpaque(false);               
		CellConstraints cctit = new CellConstraints();

		//                                  1     2     3   4    5   6   7    8    9   10     11      1  2 3 4
		FormLayout links = new FormLayout("2dlu,150dlu,2dlu,1px,2dlu,p:g,2dlu,1px,2dlu,80dlu,2dlu,","1px,p,p,1px");
		PanelBuilder plinks = new PanelBuilder(links);
		plinks.getPanel().setOpaque(false);
		CellConstraints ccli = new CellConstraints();
		plinks.add(getLabelKleinRot("Bezeichnung der Tätigkeit:"),ccli.xywh(2, 2, 2,2,CellConstraints.DEFAULT,CellConstraints.CENTER));
		plinks.add(getRand(Color.GRAY),ccli.xywh(4, 2, 1, 2));
		eltern.btf[25] = new JRtaTextField("nix",true);
		eltern.btf[25].setName("TAET");
		plinks.add(eltern.btf[25],ccli.xy(6, 3,CellConstraints.FILL,CellConstraints.BOTTOM));
		plinks.add(getRand(Color.GRAY),ccli.xywh(8, 2, 1, 2));
		plinks.add(getLabelKleinRot("Berufsklassenschlüssel"),ccli.xy(10,2));
		eltern.btf[26] = new JRtaTextField("ZAHLEN",true);
		eltern.btf[26].setName("BKS");
		plinks.add(eltern.btf[26],ccli.xy(10, 3,CellConstraints.FILL,CellConstraints.BOTTOM));
		plinks.getPanel().validate();
		tit.add(plinks.getPanel(),cctit.xy(1,2));
		

		tit.getPanel().validate();
		return tit.getPanel();
	}
	private JPanel getTitel2(){
			FormLayout laytit = new FormLayout("20dlu,p",
				"4dlu,p,4dlu");
			PanelBuilder tit = new PanelBuilder(laytit);
			//tit.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			tit.setOpaque(false);               
			CellConstraints cctit = new CellConstraints();
			
			JLabel lab = new JLabel("A.");
			lab.setFont(fontarialfett);
			tit.add(lab,cctit.xy(1, 2));
			lab = new JLabel("Letzte berufliche Tätigkeit");
			lab.setFont(fontarialfett);
			tit.add(lab,cctit.xy(2, 2));
			tit.getPanel().validate();
			return tit.getPanel();
	}

	private JXPanel getTitel1(){
		JXPanel tit = new JXPanel();
		tit.setOpaque(false);
		FormLayout laytit = new FormLayout("fill:0:grow(0.50),p,fill:0:grow(0.50)","2dlu,p");
		CellConstraints cctit = new CellConstraints();
		tit.setLayout(laytit);
		JLabel lab = new JLabel("Sozialmedizinische Leistungsbeurteilung");
		lab.setFont(fontarialfett);
		tit.add(lab,cctit.xy(2, 2));
		return tit;
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
	private JPanel getRand(Color col){
		JPanel pan = new JPanel();
		pan.setOpaque(false);
		pan.setBorder(BorderFactory.createLineBorder(col));
		return pan;
	}
}
