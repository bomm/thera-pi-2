package entlassBerichte;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jdesktop.swingx.JXPanel;

import systemTools.JCompTools;
import systemTools.JRtaComboBox;
import systemTools.JRtaTextField;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class Eb1 {
	JXPanel pan = null;
	EBerichtPanel eltern = null;
	Font fontgross = null;
	Font fontklein = null;
	Font fontnormal = null;
	Font fontcourier = null;
	String[] seite = {"","R","L","B"};
	String[] sicher = {"","A","V","Z","G"};
	String[] erfolg = {"","0","1","2","3"};
	JLabel titel = null;
	
	public Eb1(EBerichtPanel xeltern){
		pan = new JXPanel(new BorderLayout());
		pan.setOpaque(false);
		pan.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		eltern = xeltern;
		fontgross =new Font("Arial",Font.BOLD,14);
		fontklein =new Font("Arial",Font.PLAIN,9);
		fontnormal =new Font("Arial",Font.PLAIN,10);
		fontcourier =new Font("Courier New",Font.PLAIN,12);
		//pan.add(getTitel());
		//JPanel cs = constructSeite();
		//cs.validate();
		pan.add(constructSeite(),BorderLayout.CENTER);
		for(int i = 0; i < 22;i++){
			eltern.btf[i].setFont(fontcourier);
			eltern.btf[i].setForeground(Color.BLUE);
		}
		for(int i = 0; i < 5;i++){
			eltern.bta[i].setFont(fontcourier);
			eltern.bta[i].setWrapStyleWord(true);
			eltern.bta[i].setLineWrap(true);
			eltern.bta[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
		}
		pan.validate();
		pan.setVisible(true);

	}
	public JXPanel getSeite(){
		return pan;
	}
	/****************************
	 * 
	 * 
	 * 
	 * @return
	 */
	public JScrollPane constructSeite(){
		FormLayout lay = new FormLayout("10dlu,fill:0:grow(0.25),p,fill:0:grow(0.25),10dlu",
				
				//   2=titel   4=block1 6=block2 8=block3
				// 1   2   3    4   5    6   7   8  9   10
				"20dlu, p ,2dlu, p, 10dlu,p,5dlu,p ,5dlu,p");
		PanelBuilder pb = new PanelBuilder(lay);
		pb.setOpaque(false);
		CellConstraints cc = new CellConstraints();
		//pb.addLabel("Das ist ein Label",cc.xy(2,2));
		pb.add(getTitel(),cc.xy(3,2));
		//Block 1 sind die Adressdaten
		pb.add(getBlock1(),cc.xy(3,4));
		//Block 2 sind Aufnahme/Entlassung/Entlassform
		pb.add(getBlock2(),cc.xy(3, 6));
		//Block 3 ist der Diagnosen-Block
		pb.add(getBlock3(),cc.xy(3, 8));
		//Block 4 ist Gewicht, Arbeitsunfähigkeitszeiten
		pb.add(getBlock4(),cc.xy(3, 8));
		pb.getPanel().validate();
		
		JScrollPane jscr = JCompTools.getTransparentScrollPane(pb.getPanel());
		jscr.getVerticalScrollBar().setUnitIncrement(15);
		jscr.validate();
		return jscr;
		
	}
	/******************
	 * 
	 * 
	 * 
	 * 
	 * @return
	 */
	private JPanel getBlock1(){
		//280 330
		FormLayout laytit = new FormLayout("250dlu,0dlu,300dlu","2dlu,p:g");
		PanelBuilder tit = new PanelBuilder(laytit);
		tit.setOpaque(false);
		CellConstraints cctit = new CellConstraints(); 
		tit.add(getBlock1Links(),cctit.xy(1,2,CellConstraints.LEFT,CellConstraints.TOP));
		tit.add(getBlock1Rechts(),cctit.xy(3,2,CellConstraints.FILL,CellConstraints.TOP));		
		tit.getPanel().validate();
		return tit.getPanel();
	}
	private JPanel getBlock2(){
		//240 370
		FormLayout laytit = new FormLayout("210dlu,340dlu","2dlu,p:g");
		PanelBuilder tit = new PanelBuilder(laytit);
		tit.setOpaque(false);
		CellConstraints cctit = new CellConstraints(); 
		tit.add(getBlock2Links(),cctit.xy(1,2,CellConstraints.LEFT,CellConstraints.TOP));
		tit.add(getBlock2Rechts(),cctit.xy(2,2,CellConstraints.FILL,CellConstraints.TOP));		
		tit.getPanel().validate();
		return tit.getPanel();
	}
	private JPanel getBlock3(){
		FormLayout laytit = new FormLayout("550dlu","2dlu,p:g");
		PanelBuilder tit = new PanelBuilder(laytit);
		tit.setOpaque(false);
		CellConstraints cctit = new CellConstraints(); 
		tit.add(getBlock3Komplett(),cctit.xy(1,2,CellConstraints.FILL,CellConstraints.TOP));
		tit.getPanel().validate();
		return tit.getPanel();
	}
	private JPanel getBlock4(){
		FormLayout laytit = new FormLayout("550dlu","2dlu,p:g");
		PanelBuilder tit = new PanelBuilder(laytit);
		tit.setOpaque(false);
		CellConstraints cctit = new CellConstraints(); 
		tit.getPanel().validate();
		return tit.getPanel();
	}
	private JPanel getBlock3Komplett(){  
		//insgesamt 610                      1   diag    3   icd-10  5     Seite    7    Sicher  9    Ergeb. 11   Y-Label
		FormLayout laytit = new FormLayout("4dlu,230dlu,5dlu,55dlu, 10dlu, 40dlu, 5dlu, 40dlu, 5dlu,40dlu,10dlu,140dlu,4dlu",
				//           dia1            dia2            dia3             dia4            dia5
				// 1  2  3    4     5    6     7    8    9   10     11   12    13    14   15   16   17
				"4dlu,p, p,  30dlu, 2dlu,p, 30dlu, 2dlu, p, 30dlu, 2dlu, p,  30dlu, 2dlu, p ,30dlu,4dlu");
		PanelBuilder tit = new PanelBuilder(laytit);
		tit.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		tit.setOpaque(false);               
		CellConstraints cctit = new CellConstraints();
		JLabel lab = getLabel("DIAGNOSEN");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(2,2));
		lab = getLabel("1.");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(2,3));
		lab  = getLabel("Diagnoseschlüssel");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(4,3));
		lab  = getLabel("Seiten-");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(6,3));
		lab  = getLabel("Diagn.");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(8,3));
		lab  = getLabel("Beh.");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(10,3));
		lab = getLabel("SEITENLOKALISATION");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(12,3));
		eltern.bta[0] = new JTextArea();
		eltern.bta[0].setName("DIAG1");
		tit.add(eltern.bta[0],cctit.xy(2, 4,CellConstraints.FILL,CellConstraints.FILL));
		
		/**********Diagnose 1**********************/
		
		FormLayout entlay = new FormLayout("55dlu","p,p,p");
		PanelBuilder ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		CellConstraints ccent = new CellConstraints();
		lab = getLabel ("ICD10 - GM");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 1));
		lab = getLabel("74");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.btf[17] = new JRtaTextField("GROSS",true);
		eltern.btf[17].setName("F_74");
		ent.add(eltern.btf[17],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(4, 4,CellConstraints.LEFT,CellConstraints.BOTTOM));
		
		entlay = new FormLayout("40dlu:g","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("lokalis.");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 1));
		lab = getLabel("79");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.bcmb[2] = new JRtaComboBox(seite);
		eltern.bcmb[2].setName("F_79");
		ent.add(eltern.bcmb[2],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(6, 4,CellConstraints.LEFT,CellConstraints.BOTTOM));
		
		entlay = new FormLayout("40dlu:g","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("sicherh.");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 1));
		lab = getLabel("80");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.bcmb[3] = new JRtaComboBox(sicher);
		eltern.bcmb[3].setName("F_80");
		ent.add(eltern.bcmb[3],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(8, 4,CellConstraints.LEFT,CellConstraints.BOTTOM));

		entlay = new FormLayout("40dlu:g","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("Ergebn.");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 1));
		lab = getLabel("81");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.bcmb[4] = new JRtaComboBox(erfolg);
		eltern.bcmb[4].setName("F_81");
		ent.add(eltern.bcmb[4],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(10, 4,CellConstraints.LEFT,CellConstraints.BOTTOM));
		
		/**********Diagnose 2**********************/
		
		lab = getLabel("2.");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(2,6));
		eltern.bta[1] = new JTextArea();
		eltern.bta[1].setName("DIAG2");
		tit.add(eltern.bta[1],cctit.xy(2, 7,CellConstraints.FILL,CellConstraints.FILL));

		entlay = new FormLayout("55dlu","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("82");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.btf[18] = new JRtaTextField("GROSS",true);
		eltern.btf[18].setName("F_82");
		ent.add(eltern.btf[18],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(4, 7,CellConstraints.LEFT,CellConstraints.BOTTOM));
		
		entlay = new FormLayout("40dlu","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("87");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.bcmb[5] = new JRtaComboBox(seite);
		eltern.bcmb[5].setName("F_87");
		ent.add(eltern.bcmb[5],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(6, 7,CellConstraints.LEFT,CellConstraints.BOTTOM));
		
		entlay = new FormLayout("40dlu","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("88");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.bcmb[6] = new JRtaComboBox(sicher);
		eltern.bcmb[6].setName("F_88");
		ent.add(eltern.bcmb[6],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(8, 7,CellConstraints.LEFT,CellConstraints.BOTTOM));

		entlay = new FormLayout("40dlu","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("89");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.bcmb[7] = new JRtaComboBox(erfolg);
		eltern.bcmb[7].setName("F_81");
		ent.add(eltern.bcmb[7],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(10, 7,CellConstraints.LEFT,CellConstraints.BOTTOM));
		
		/**********Diagnose 3**********************/
		
		lab = getLabel("3.");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(2,9));
		eltern.bta[2] = new JTextArea();
		eltern.bta[2].setName("DIAG3");
		tit.add(eltern.bta[2],cctit.xy(2, 10,CellConstraints.FILL,CellConstraints.FILL));

		entlay = new FormLayout("55dlu","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("90");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.btf[19] = new JRtaTextField("GROSS",true);
		eltern.btf[19].setName("F_90");
		ent.add(eltern.btf[19],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(4, 10,CellConstraints.LEFT,CellConstraints.BOTTOM));
		
		entlay = new FormLayout("40dlu","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("95");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.bcmb[8] = new JRtaComboBox(seite);
		eltern.bcmb[8].setName("F_95");
		ent.add(eltern.bcmb[8],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(6, 10,CellConstraints.LEFT,CellConstraints.BOTTOM));
		
		entlay = new FormLayout("40dlu","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("96");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.bcmb[9] = new JRtaComboBox(sicher);
		eltern.bcmb[9].setName("F_96");
		ent.add(eltern.bcmb[9],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(8, 10,CellConstraints.LEFT,CellConstraints.BOTTOM));

		entlay = new FormLayout("40dlu","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("97");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.bcmb[10] = new JRtaComboBox(erfolg);
		eltern.bcmb[10].setName("F_97");
		ent.add(eltern.bcmb[10],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(10, 10,CellConstraints.LEFT,CellConstraints.BOTTOM));

		/**********Diagnose 4**********************/
		
		lab = getLabel("4.");
		lab.setForeground(Color.RED);		
		tit.add(lab,cctit.xy(2,12));
		eltern.bta[3] = new JTextArea();
		eltern.bta[3].setName("DIAG4");
		tit.add(eltern.bta[3],cctit.xy(2, 13,CellConstraints.FILL,CellConstraints.FILL));

		entlay = new FormLayout("55dlu","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("98");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.btf[20] = new JRtaTextField("GROSS",true);
		eltern.btf[20].setName("F_98");
		ent.add(eltern.btf[20],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(4, 13,CellConstraints.LEFT,CellConstraints.BOTTOM));
		
		entlay = new FormLayout("40dlu","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("103");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.bcmb[11] = new JRtaComboBox(seite);
		eltern.bcmb[11].setName("F_103");
		ent.add(eltern.bcmb[11],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(6, 13,CellConstraints.LEFT,CellConstraints.BOTTOM));
		
		entlay = new FormLayout("40dlu","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("104");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.bcmb[12] = new JRtaComboBox(sicher);
		eltern.bcmb[12].setName("F_104");
		ent.add(eltern.bcmb[12],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(8, 13,CellConstraints.LEFT,CellConstraints.BOTTOM));

		entlay = new FormLayout("40dlu","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("105");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.bcmb[13] = new JRtaComboBox(erfolg);
		eltern.bcmb[13].setName("F_105");
		ent.add(eltern.bcmb[13],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(10, 13,CellConstraints.LEFT,CellConstraints.BOTTOM));
		
		/**********Diagnose 5**********************/
		
		lab = getLabel("5.");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(2,15));
		eltern.bta[4] = new JTextArea();
		eltern.bta[4].setName("DIAG4");
		tit.add(eltern.bta[4],cctit.xy(2, 16,CellConstraints.FILL,CellConstraints.FILL));

		entlay = new FormLayout("55dlu","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("106");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.btf[21] = new JRtaTextField("GROSS",true);
		eltern.btf[21].setName("F_106");
		ent.add(eltern.btf[21],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(4, 16,CellConstraints.LEFT,CellConstraints.BOTTOM));
		
		entlay = new FormLayout("40dlu","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("111");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.bcmb[14] = new JRtaComboBox(seite);
		eltern.bcmb[14].setName("F_111");
		ent.add(eltern.bcmb[14],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(6, 16,CellConstraints.LEFT,CellConstraints.BOTTOM));
		
		entlay = new FormLayout("40dlu","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("112");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.bcmb[15] = new JRtaComboBox(sicher);
		eltern.bcmb[15].setName("F_112");
		ent.add(eltern.bcmb[15],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(8, 16,CellConstraints.LEFT,CellConstraints.BOTTOM));

		entlay = new FormLayout("40dlu","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("113");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.bcmb[16] = new JRtaComboBox(erfolg);
		eltern.bcmb[16].setName("F_113");
		ent.add(eltern.bcmb[16],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(10, 16,CellConstraints.LEFT,CellConstraints.BOTTOM));
		//                                       4    5 6 7 8 9  10   11
		entlay = new FormLayout("140dlu","p,p,p,40dlu,p,p,p,p,p,40dlu,p,p,p,p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		ent.add(getLabel("R = rechts"),ccent.xy(1,1));
		ent.add(getLabel("L = links"),ccent.xy(1,2));
		ent.add(getLabel("B = beidseits"),ccent.xy(1,3));
		lab = getLabel("DIAGNOSESICHERHEIT");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1,5));
		ent.add(getLabel("A = ausgeschlossen"),ccent.xy(1,6));
		ent.add(getLabel("V = Verdachtsdiagnose"),ccent.xy(1,7));
		ent.add(getLabel("Z = Zustand nach"),ccent.xy(1,8));
		ent.add(getLabel("G = gesicherten Diagnose"),ccent.xy(1,9));
		lab = getLabel("BEHANDLUNGS-");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1,11));
		lab = getLabel("ERGEBNIS");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1,12));
		ent.add(getLabel("0 = 1-3 triff nicht zu"),ccent.xy(1,13));
		ent.add(getLabel("1 = gebessert"),ccent.xy(1,14));		
		ent.add(getLabel("2 = unverändert"),ccent.xy(1,15));
		ent.add(getLabel("3 = verschlechtert"),ccent.xy(1,16));
		tit.add(ent.getPanel(),cctit.xywh(12, 4,1,14,CellConstraints.LEFT,CellConstraints.TOP));
		
		return tit.getPanel();
	}
	/***************************************************/
	private JPanel getBlock2Links(){       //1    2     3    4       5      6      7
		FormLayout laytit = new FormLayout("4dlu,55dlu,5dlu,55dlu, 5dlu, 50dlu,4dlu",
				//     3=Vnr.  5=Name 7=geb 9=stras
				// 1  2  3   4     5   6      7   8  9   
				"0dlu,p, p,  2dlu, p,  2dlu,  p,  p, p,"+
				//11=PLZ 13=Vers.Name
				//10  11 12 13 14
				 "p,  p, p, p, 4dlu");

		PanelBuilder tit = new PanelBuilder(laytit);
		//tit.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		tit.setOpaque(false);               
		CellConstraints cctit = new CellConstraints(); 
		JLabel lab = getLabel("Aufnahmedatum");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(2,2));
		lab = getLabel("Entlassdatum");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(4,2));
		eltern.btf[11] = new JRtaTextField("DATUM",false);
		eltern.btf[11].setName("AUFDAT1");
		eltern.btf[11].setEnabled(false);
		eltern.btf[12] = new JRtaTextField("DATUM",false);
		eltern.btf[12].setName("ENTDAT1");
		eltern.btf[12].setEnabled(false);
		eltern.btf[13] = new JRtaTextField("DATUM",false);
		eltern.btf[13].setName("AUFDAT2");
		eltern.btf[13].setEnabled(false);
		eltern.btf[14] = new JRtaTextField("DATUM",false);
		eltern.btf[14].setName("ENTDAT2");
		eltern.btf[14].setEnabled(false);
		eltern.btf[15] = new JRtaTextField("DATUM",false);
		eltern.btf[15].setName("AUFDAT3");
		eltern.btf[15].setEnabled(true);
		eltern.btf[16] = new JRtaTextField("DATUM",false);
		eltern.btf[16].setName("ENTDAT3");
		eltern.btf[16].setEnabled(true);
		tit.add(eltern.btf[11],cctit.xy(2,3));
		tit.add(eltern.btf[12],cctit.xy(4,3));
		tit.add(eltern.btf[13],cctit.xy(2,5));
		tit.add(eltern.btf[14],cctit.xy(4,5));
		tit.add(eltern.btf[15],cctit.xy(2,7));
		tit.add(eltern.btf[16],cctit.xy(4,7));
		lab = getLabel("stationäir");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(6,3));
		lab = getLabel("teilstationär");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(6,5));
		lab =  getLabel("ambulant");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(6,7));		
		return tit.getPanel();
	}
	/***************************************************/	
	private JPanel getBlock2Rechts(){       //1    2     3    4       5    6    7    8      9
		FormLayout laytit = new FormLayout("0dlu,25dlu,2dlu,140dlu,20dlu,25dlu,2dlu,140dlu,4dlu",
				//     3=Vnr.  5=Name 7=geb 9=stras
				// 1  2  3      4  5   6   7   8  9   
				"0dlu,p, 1dlu,  p, p:g,  p,  p,  p, p,"+
				//11=PLZ 13=Vers.Name
				//10  11 12 13 14
				 "p,  p, p, p, 4dlu");

		PanelBuilder tit = new PanelBuilder(laytit);
		//tit.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		tit.setOpaque(false);               
		CellConstraints cctit = new CellConstraints();
		
		JLabel lab = getLabel("ENTLASSFORM");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(4, 2));
		
		lab = getLabel("ARBEITSFÄHIGKEIT");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(8, 2));

		eltern.bcmb[0] = new JRtaComboBox(new String[] {"","1","2","3","4","5","6","7","9"});
		eltern.bcmb[0].setName("ENTFORM");
		tit.add(eltern.bcmb[0],cctit.xy(2, 4));
		//										       1 2 3 4 5 6 7 8 9 10 11 12
		FormLayout entlay = new FormLayout("p,2dlu,p","p,p,p,p,p,p,p,p,p, p, p, p");
		PanelBuilder ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		CellConstraints ccent = new CellConstraints();
		ent.add(getLabel("1 = "),ccent.xy(1,1));
		ent.add(getLabel("regulär"),ccent.xy(3,1));
		ent.add(getLabel("2 = "),ccent.xy(1,2));
		ent.add(getLabel("vorzeitig auf ärztl. Veranlassung"),ccent.xy(3,2));
		ent.add(getLabel("3 = "),ccent.xy(1,3));
		ent.add(getLabel("vorzeitig mit ärztl. Einverständnis"),ccent.xy(3,3));
		ent.add(getLabel("4 = "),ccent.xy(1,4));
		ent.add(getLabel("vorzeitig ohne ärztl. Einverständnis"),ccent.xy(3,4));
		ent.add(getLabel("5 = "),ccent.xy(1,5));
		ent.add(getLabel("disziplinarisch"),ccent.xy(3,5));
		ent.add(getLabel("6 = "),ccent.xy(1,6));
		ent.add(getLabel("verlegt"),ccent.xy(3,6));
		ent.add(getLabel("7 = "),ccent.xy(1,7));
		ent.add(getLabel("Wechsel zu ambulanter"),ccent.xy(3,7));
		ent.add(getLabel("teilstationärer, stationärer Reha"),ccent.xy(3,8));
		ent.add(getLabel("9 = "),ccent.xy(1,9));
		ent.add(getLabel("gestorben"),ccent.xy(3,9));
		
		tit.add(ent.getPanel(),cctit.xywh(4,4,1,2));
		
		eltern.bcmb[1] = new JRtaComboBox(new String[] {"","0","1","3","4","5","9"});
		eltern.bcmb[1].setName("ARBFAE");
		tit.add(eltern.bcmb[1],cctit.xy(6, 4));
		
		entlay = new FormLayout("p,2dlu,p","p,p,p,p,p,p,p,p,p, p, p, p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		ent.add(getLabel("0 = "),ccent.xy(1,1));
		ent.add(getLabel("Maßnahme nicht ordnungsgemäß"),ccent.xy(3,1));
		ent.add(getLabel("abgeschlosse, gestorben"),ccent.xy(3,2));
		ent.add(getLabel("1 = "),ccent.xy(1,3));
		ent.add(getLabel("arbeitsfähig"),ccent.xy(3,3));
		ent.add(getLabel("3 = "),ccent.xy(1,4));
		ent.add(getLabel("arbeitsunfähig"),ccent.xy(3,4));
		ent.add(getLabel("4 = "),ccent.xy(1,5));
		ent.add(getLabel("Kinder-HB"),ccent.xy(3,5));
		ent.add(getLabel("5 = "),ccent.xy(1,6));
		ent.add(getLabel("Hausfrau/Hausmann"),ccent.xy(3,6));
		ent.add(getLabel("9 = "),ccent.xy(1,7));
		ent.add(getLabel("Beurteilung nicht erforderlich"),ccent.xy(3,7));
		ent.add(getLabel("(Altersrente, Angehörige)"),ccent.xy(3,8));
		tit.add(ent.getPanel(),cctit.xywh(8,4,1,2));
		
		return tit.getPanel();
	}
	
	/***********************************************/
	private JPanel getBlock1Links(){       //1    2     3    4       5     6    7   8
		FormLayout laytit = new FormLayout("4dlu,55dlu,2dlu,80dlu,40dlu,50dlu,4dlu,4dlu",
				//     3=Vnr.  5=Name 7=geb 9=stras
				// 1  2  3   4  5   6   7   8  9   
				"4dlu,p, p,  p, p,  p,  p,  p, p,"+
				//11=PLZ 13=Vers.Name
				//10  11 12 13 14
				 "p,  p, p, p, 4dlu");

		PanelBuilder tit = new PanelBuilder(laytit);
		tit.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		tit.setOpaque(false);               
		CellConstraints cctit = new CellConstraints();
		JLabel lab = getLabel("Versicherungsnummer:");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xyw(2,2,3));
		lab = getLabel("AIGR/BKZ");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(6,2));

		eltern.btf[0] = new JRtaTextField("GROSS",true);
		eltern.btf[0].setName("VNUMMER");
		tit.add(eltern.btf[0],cctit.xyw(2,3,3));
		eltern.btf[1] = new JRtaTextField("GROSS",true);
		eltern.btf[1].setName("AIGR");
		tit.add(eltern.btf[1],cctit.xy(6,3));
		
		lab = getLabel ("Name, Vorname:");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xyw(2,4,3));
		eltern.btf[2] = new JRtaTextField("nix",true);
		eltern.btf[2].setName("NAMEVOR");
		tit.add(eltern.btf[2],cctit.xyw(2,5,5));
		
		lab = getLabel ("Geburtsdatum:");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xyw(2,6,3));
		eltern.btf[3] = new JRtaTextField("DATUM",true);
		eltern.btf[3].setName("GEBOREN");
		tit.add(eltern.btf[3],cctit.xy(2,7));
		tit.add(getLabel("(Sofern nicht in Vers. Nr. enthalten)"),cctit.xyw(4,7,3));
		
		lab = getLabel ("Straße,Hausnummer:");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xyw(2,8,3));
		eltern.btf[4] = new JRtaTextField("nix",true);
		eltern.btf[4].setName("STRASSE");
		tit.add(eltern.btf[4],cctit.xyw(2,9,5));
		
		lab = getLabel ("Postleitzahl:");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(2,10));
		lab = getLabel ("Wohnort");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(4,10));
		eltern.btf[5] = new JRtaTextField("ZAHLEN",true);
		eltern.btf[5].setName("PLZ");
		tit.add(eltern.btf[5],cctit.xy(2,11));
		eltern.btf[6] = new JRtaTextField("nix",true);
		eltern.btf[6].setName("ORT");
		tit.add(eltern.btf[6],cctit.xyw(4,11,3));

		lab = getLabel("Versicherter (Name, Vorname) falls nicht mit Patient indentisch");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xyw(2,12,5));
		eltern.btf[7] = new JRtaTextField("nix",true);
		eltern.btf[7].setName("VNAMEVO");
		tit.add(eltern.btf[7],cctit.xyw(2,13,5));
		tit.getPanel().validate();
		return tit.getPanel();
	}
	
	/***********************************************/	
	private JPanel getBlock1Rechts(){       // 1   2    3   4    5     6     7   
		FormLayout laytit = new FormLayout("4dlu,40dlu,10dlu,40,100dlu,40dlu,50dlu,4dlu",
				//     3=Vnr.  5=Name 7=geb 9=stras
				// 1  2  3   4  5   6   7      8   9   
				"4dlu,p, p,  p, p,  p,  3dlu,  p, 1dlu,"+
				//
				//10  11    12 13   14  15   16  17 18  19  20  21   22
				 "p,  5dlu, p, 15dlu,p, 1dlu,p, 7dlu,p, 1dlu,p, 0dlu,4dlu");
		PanelBuilder tit = new PanelBuilder(laytit);
		tit.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		tit.setOpaque(false);
		//tit.setBackground(Color.BLUE);
		CellConstraints cctit = new CellConstraints();		
		JLabel lab = getLabel("MSNR:");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(2,2));
		lab = getLabel("BNR:");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(4,2));
		eltern.btf[8] = new JRtaTextField("nix",true);
		eltern.btf[8].setName("MSNR");
		tit.add(eltern.btf[8],cctit.xy(2,4));
		eltern.btf[9] = new JRtaTextField("nix",true);
		eltern.btf[9].setName("BNR");
		tit.add(eltern.btf[9],cctit.xy(4,4));
		
		lab = getLabel("Behandlungsstätte:");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xyw(2,6,6));
		lab = getLabel("Reutlinger Therapie- und Analysezentrum GmbH");
		lab.setFont(fontcourier);
		tit.add(lab,cctit.xyw(2,8,6));
		lab = new JLabel("Marie-Curie-Str. 1");
		lab.setFont(fontcourier);
		tit.add(lab,cctit.xyw(2,10,6));
		lab = new JLabel("72760 Reutlingen");
		lab.setFont(fontcourier);
		tit.add(lab,cctit.xyw(2,12,6));		
		
		lab = getLabel("Abteilung:");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(2,14));
		
		lab = new JLabel("ambulante Rehabilitation");
		lab.setFont(fontcourier);
		tit.add(lab,cctit.xyw(2,16,6));

		lab = getLabel ("IK-NR:");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(2,18));
		
		lab = getLabel("Abt.NR:");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(6,18));

		lab = new JLabel("510841109");
		lab.setFont(fontcourier);
		tit.add(lab,cctit.xyw(2,20,3));
		
		eltern.btf[10] = new JRtaTextField("ZAHLEN",true);
		eltern.btf[10].setName("ABTEILUNG");
		tit.add(eltern.btf[10],cctit.xy(6,20));
		
		return tit.getPanel();
	}
	/***********************************************/
	private JXPanel getTitel(){
		JXPanel tit = new JXPanel();
		tit.setOpaque(false);
		FormLayout laytit = new FormLayout("p,30dlu,p","2dlu,p");
		CellConstraints cctit = new CellConstraints(); 
		tit.setLayout(laytit);

		titel = new JLabel("DRV Baden-Württemberg");
		titel.setFont(fontgross);
		titel.setForeground(Color.RED);
		tit.add(titel,cctit.xy(1, 2));
		
		titel = new JLabel("Reha-Entlassbericht");
		titel.setFont(fontgross);
		titel.setForeground(Color.RED);
		tit.add(titel,cctit.xy(3, 2));
		tit.validate();
		return tit;
	}
	private JLabel getLabel(String text){
		JLabel lab = new JLabel(text);
		lab.setFont(fontklein);
		return lab;
	}
	class JLabelX extends JLabel{
		public JLabelX(String text){
			super();
			setFont(fontklein);
			setVisible(true);
		}
	}
	
}

