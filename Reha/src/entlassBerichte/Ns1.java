package entlassBerichte;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXPanel;

import patientenFenster.PatGrundPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import systemEinstellungen.SystemConfig;
import systemTools.JCompTools;
import systemTools.JRtaCheckBox;
import systemTools.JRtaComboBox;
import systemTools.JRtaTextField;
import systemTools.StringTools;
import terminKalender.DatFunk;

public class Ns1 implements ActionListener,ComponentListener {
	
	JXPanel pan = null;
	EBerichtPanel eltern = null;
	Font fontgross = null;
	Font fontklein = null;
	Font fontnormal = null;
	Font fontcourier = null;
	Font fontarialfett = null;
	Font fontarialnormal = null;
	String[] seite = {"","R","L","B"};
	String[] sicher = {"","A","V","Z","G"};
	String[] erfolg = {"","0","1","2","3"};
	String[] ursache = {"","0","1","2","3","4","5"};
	String[] vorherau = {"","0","1","2","3","9"};
	String[] dmp = {"","0","1","2","3","4","5","6","7"};
	JLabel titel = null;
	JScrollPane jscr = null;
	boolean inGuiInit = true;
	

	public Ns1(EBerichtPanel xeltern){
		pan = new JXPanel(new BorderLayout());
		pan.addComponentListener(this);
		pan.setOpaque(false);
		pan.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		eltern = xeltern;
		fontgross =new Font("Arial",Font.BOLD,14);
		fontklein =new Font("Arial",Font.PLAIN,9);
		fontnormal =new Font("Arial",Font.PLAIN,10);
		fontarialfett =new Font("Arial",Font.BOLD,12);
		fontarialnormal =new Font("Arial",Font.PLAIN,12);		
		fontcourier =new Font("Courier New",Font.PLAIN,12);	
		new SwingWorker<Void,Void>(){
			
			@Override
			protected Void doInBackground() throws Exception {
				try{
				/*********************************************/	
				pan.add(constructSeite(),BorderLayout.CENTER);
				/*********************************************/				
				for(int i = 0; i < 24;i++){
					
					eltern.btf[i].setFont(fontcourier);
					eltern.btf[i].setForeground(Color.BLUE);
					//System.out.println("Name von btf["+i+"] = "+eltern.btf[i].getName());
					
				}
				for(int i = 0; i < 7;i++){

					eltern.bta[i].setFont(fontcourier);
					eltern.bta[i].setForeground(Color.BLUE);
					eltern.bta[i].setWrapStyleWord(true);
					eltern.bta[i].setLineWrap(true);
					eltern.bta[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));

				}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				pan.validate();
				pan.setVisible(true);
				SwingUtilities.invokeLater(new Runnable(){
				 	   public  void run(){
				 		if(!eltern.neu){
				 			new SwingWorker<Void,Void>(){
								@Override
								protected Void doInBackground()
										throws Exception {
									try{
									eltern.setCursor(new Cursor(Cursor.WAIT_CURSOR));
									Reha.thisClass.progressStarten(true);	
						 			laden();
						 			eltern.btf[0].requestFocusInWindow();
						 			jscr.scrollRectToVisible(new Rectangle(0,0,0,0));
						 			Reha.thisClass.progressStarten(false);
						 			eltern.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						 			inGuiInit = false;
									testeIK();
									}catch(Exception ex){
										ex.printStackTrace();
									}
									return null;
								}
				 				
				 			}.execute();
				 		}else{
				 			JOptionPane.showMessageDialog(null,"Bitte stellen Sie als erstes den Empfäger des Gutachtens ein (Berichttyp).");
				 			doKopfNeu();
				 		}
				 		  
				 	   }
				});
				return null;
			}
			
		}.execute();
	}
	public JXPanel getSeite(){
		return pan;
	}
	/************************
	 * 
	 * 
	 * 
	 */	
	public JScrollPane constructSeite(){// 1          2          3         4           5
		FormLayout lay = new FormLayout("10dlu,fill:0:grow(0.25),p,fill:0:grow(0.25),10dlu",
				
				//   2=titel   4=block1 6=block2 8=block3
				// 1    2   3    4   5    6      8   9   10  11   12  13  14   15  16  17  18 19
				"20dlu, p ,2dlu, p, 10dlu,p,5dlu,p ,5dlu,p,  2dlu, p, 5dlu,p, 2dlu,p,10dlu,p,10dlu");
		PanelBuilder pb = new PanelBuilder(lay);
		pb.setOpaque(false);
		CellConstraints cc = new CellConstraints();

		pb.add(getTitel(),cc.xy(3,2));
		//Block 1 sind die Adressdaten
		pb.add(getBlock1(),cc.xy(3,4));
		//VonBis ist Aufnahme und Entlassdatum
		pb.add(getVonBis(),cc.xy(3,6));
		//Block 3 ist der Diagnosen-Block
		pb.add(getBlock3(),cc.xy(3, 8));
		//Titel für die Rubrik Untersuchung
		pb.add(getTitelUntersuchung(),cc.xy(3, 10));
		//Dann die Rubrik Untersuchung komplett
		pb.add(getUntersuchung(),cc.xy(3, 12));
		//Titel für die Rubrik Arbeitsfähigkeit
		pb.add(getTitelArbeitsfaehigkeit(),cc.xy(3, 14));
		//Dann die Rubrik Arbeitsfähigkeit
		pb.add(getArbeitsfaehigkeit(),cc.xy(3, 16));
		//Unterschriftsdatum
		pb.add(getDatumUnterschrift(),cc.xy(3, 18));
		
		pb.getPanel().validate();
		jscr = JCompTools.getTransparentScrollPane(pb.getPanel());
		jscr.getVerticalScrollBar().setUnitIncrement(15);
		jscr.validate();
		return jscr;
		
	}
	
	private JPanel getDatumUnterschrift(){
		FormLayout dummy = new FormLayout("p,2dlu,60dlu","10dlu,p");
		PanelBuilder dum = new PanelBuilder(dummy);
		dum.getPanel().setOpaque(false);
		CellConstraints ccdum = new CellConstraints();
		JLabel lab = new JLabel("Datum der Unterschrift:");
		lab.setFont(this.fontarialnormal);
		dum.add(lab,ccdum.xy(1,2));
		eltern.btf[23] = new JRtaTextField("DATUM",true);
		eltern.btf[23].setName("UNTDAT");
		eltern.btf[23].setFont(fontcourier);
		eltern.btf[23].setForeground(Color.BLUE);

		dum.add(eltern.btf[23],ccdum.xy(3,2));
		dum.getPanel().validate();
		return dum.getPanel();
	}

	private JPanel getArbeitsfaehigkeit(){
		FormLayout layarbf = new FormLayout("0dlu,2dlu,p:g,2dlu",
//				 1   2  3  4   5  6   7  8	 9  10  11	12	13 14
				"2dlu,p,2dlu,p,3dlu");
				PanelBuilder arbf = new PanelBuilder(layarbf);
				arbf.getPanel().setBorder(BorderFactory.createLineBorder(Color.BLACK));
				arbf.getPanel().setOpaque(false);               
				CellConstraints ccarbf = new CellConstraints();
				//                                        1               2            3          4         5
				FormLayout laydummy = new FormLayout("fill:0:grow(0.33),10dlu,fill:0:grow(0.33),10dlu,fill:0:grow(0.33)",
						"p,1dlu,p");
				PanelBuilder dummy = new PanelBuilder(laydummy);
				dummy.getPanel().setOpaque(false);
				CellConstraints ccdum = new CellConstraints();
				JLabel lab = getLabelArialFettRot("arbeitsfähig");
				//lab.setForeground(Color.RED);
				dummy.add(lab,ccdum.xy(1,1,CellConstraints.LEFT,CellConstraints.BOTTOM));

				lab = getLabelArialNormalRot("arbeitsunfähig");
				//lab.setForeground(Color.RED);
				dummy.add(lab,ccdum.xy(3,1,CellConstraints.LEFT,CellConstraints.BOTTOM));
				dummy.add(getLabelKleinRot("Seit wann?"),ccdum.xy(3,3,CellConstraints.LEFT,CellConstraints.TOP));

				dummy.add(getLabelKleinRot("voraussichtlich bis:"),ccdum.xy(5,3,CellConstraints.LEFT,CellConstraints.TOP));
				dummy.getPanel().validate();
				arbf.add(dummy.getPanel(),ccarbf.xy(3,2));
				
				laydummy = new FormLayout("fill:0:grow(0.33),10dlu,fill:0:grow(0.33),10dlu,fill:0:grow(0.33)",
				"p,1dlu,p");
				dummy = new PanelBuilder(laydummy);
				dummy.getPanel().setOpaque(false);
				ccdum = new CellConstraints();	  //   1  2   3       4          5  6   7
				FormLayout laydummy2 = new FormLayout("p,2dlu,p,35dlu,p,2dlu,p",
				"p");
				PanelBuilder dummy2 = new PanelBuilder(laydummy2);
				dummy2.getPanel().setOpaque(false);
				CellConstraints ccdum2 = new CellConstraints();
				eltern.bchb[5] = new JRtaCheckBox("");
				eltern.bchb[5].setName("F_153");
				dummy2.add(eltern.bchb[5],ccdum2.xy(1, 1));
				lab = getLabelArialFettRot("ja");
				dummy2.add(lab,ccdum2.xy(3, 1));
				eltern.bchb[6] = new JRtaCheckBox("");
				eltern.bchb[6].setName("F_154");
				dummy2.add(eltern.bchb[6],ccdum2.xy(5, 1));
				lab = getLabelArialFettRot("nein");
				dummy2.add(lab,ccdum2.xy(7, 1));
				dummy2.getPanel().validate();
				dummy.add(dummy2.getPanel(),ccdum.xy(1,1));

				laydummy2 = new FormLayout("55dlu","p");
				dummy2 = new PanelBuilder(laydummy2);
				dummy2.getPanel().setOpaque(false);
				ccdum2 = new CellConstraints();
				eltern.btf[21] = new JRtaTextField("DATUM",true);
				eltern.btf[21].setName("ENTDAT2");
				dummy2.add(eltern.btf[21],ccdum.xy(1, 1,CellConstraints.FILL,CellConstraints.DEFAULT));
				dummy2.getPanel().validate();
				dummy.add(dummy2.getPanel(),ccdum.xy(3,1));
				
				laydummy2 = new FormLayout("55dlu","p");
				dummy2 = new PanelBuilder(laydummy2);
				dummy2.getPanel().setOpaque(false);
				//ccdum2 = new CellConstraints();
				eltern.btf[22] = new JRtaTextField("DATUM",true);
				eltern.btf[22].setName("AUFDAT2");
				dummy2.add(eltern.btf[22],ccdum.xy(1, 1,CellConstraints.FILL,CellConstraints.DEFAULT));
				dummy2.getPanel().validate();
				dummy.add(dummy2.getPanel(),ccdum.xy(5,1));

				dummy.getPanel().validate();

				arbf.add(dummy.getPanel(),ccarbf.xy(3,4));
				arbf.getPanel().validate();
				return arbf.getPanel();
	}			
	
	private JPanel getUntersuchung(){
		FormLayout laybefund = new FormLayout("0dlu,2dlu,p:g,2dlu",
//				 1   2  3  4   5  6   7  8	 9  10  11	12	13 14
				"p,5dlu,p,5dlu,p,5dlu,p,2dlu,p,5dlu,p, 2dlu,p,3dlu");
				PanelBuilder befund = new PanelBuilder(laybefund);
				befund.getPanel().setBorder(BorderFactory.createLineBorder(Color.BLACK));
				befund.getPanel().setOpaque(false);               
				CellConstraints ccbef = new CellConstraints();
				JLabel lab = new JLabel("Bestehende Funktionseinschrängkung(en) "+
						"und von der Norm abweichende Befunde an folgenden Organsystemen:");
				lab.setFont(fontarialnormal);
				befund.add(lab,ccbef.xy(3, 3));

				befund.add(getChecks1(),ccbef.xy(3,5));
				
				lab = new JLabel("Erläuterungen");
				lab.setFont(fontarialnormal);
				befund.add(lab,ccbef.xy(3, 7));

				befund.add(getErlaeut1(),ccbef.xy(3, 9));
				
				lab = new JLabel("Beschreibung der erreichten Funktionsveränderung(en) im prä / post Vergleich:");
				lab.setFont(fontarialnormal);
				befund.add(lab,ccbef.xy(3, 11));

				befund.add(getPraePost(),ccbef.xy(3, 13));
				
				befund.getPanel().validate();
				return befund.getPanel();
	}
	private JPanel getPraePost(){
		FormLayout laypreaepost = new FormLayout("fill:0:grow(0.7),10dlu,fill:0:grow(0.3)",
				"0dlu,75dlu,2dlu");
		PanelBuilder praepost = new PanelBuilder(laypreaepost);
		praepost.getPanel().setOpaque(false);
		CellConstraints ccerlaeut = new CellConstraints();

		eltern.bta[6] = new JTextArea();
		eltern.bta[6].setName("TERLEUT");
		praepost.add(eltern.bta[6],ccerlaeut.xy(1, 2,CellConstraints.FILL,CellConstraints.FILL));
		
		FormLayout laydummy = new FormLayout("fill:0:grow(1.0),p","p,1dlu,p");
		PanelBuilder dummy = new PanelBuilder(laydummy);
		dummy.getPanel().setOpaque(false);
		CellConstraints ccdum = new CellConstraints();
		dummy.add(getLabelKleinRot("Untersuchunsdatum"),ccdum.xy(2,1,CellConstraints.FILL,CellConstraints.DEFAULT));
		eltern.btf[20] = new JRtaTextField("DATUM",true);
		eltern.btf[20].setName("ENTDAT1");
		dummy.add(eltern.btf[20],ccdum.xy(2, 3,CellConstraints.FILL,CellConstraints.DEFAULT));
		
		dummy.getPanel().validate();
		praepost.add(dummy.getPanel(),ccerlaeut.xy(3,2,CellConstraints.FILL,CellConstraints.BOTTOM));
		praepost.getPanel().validate();
		return praepost.getPanel();
	}
	
	private JPanel getErlaeut1(){
		FormLayout layerlaeut = new FormLayout("fill:0:grow(0.7),10dlu,fill:0:grow(0.3)",
					
				//   1    2   3
					"0dlu,40dlu,0dlu");
			PanelBuilder erlaeut = new PanelBuilder(layerlaeut);
			erlaeut.getPanel().setOpaque(false);
			CellConstraints ccerlaeut = new CellConstraints();

			eltern.bta[5] = new JTextArea();
			eltern.bta[5].setName("FREITEXT");
			erlaeut.add(eltern.bta[5],ccerlaeut.xy(1, 2,CellConstraints.FILL,CellConstraints.FILL));
//                                                1  2    3   4    5
			FormLayout laydummy = new FormLayout("p,2dlu,p:g, 7dlu,p:g,1px","p,1dlu,p,p");
			PanelBuilder dummy = new PanelBuilder(laydummy);
			dummy.getPanel().setOpaque(false);
			CellConstraints ccdum = new CellConstraints();			
			dummy.add(getLabelArialNormal("Gewicht:"),ccdum.xy(1,3,CellConstraints.LEFT,CellConstraints.BOTTOM));
			dummy.add(getLabelArialNormal("(ganze kg)"),ccdum.xy(1,4,CellConstraints.LEFT,CellConstraints.BOTTOM));
			dummy.add(getLabelKleinRot("Beginn"),ccdum.xy(3,3,CellConstraints.LEFT,CellConstraints.BOTTOM));
			eltern.btf[18] = new JRtaTextField("ZAHLEN",true);
			eltern.btf[18].setName("F_114");
			eltern.btf[18].setHorizontalAlignment(SwingConstants.RIGHT);
			dummy.add(eltern.btf[18],ccdum.xy(3, 4,CellConstraints.FILL,CellConstraints.BOTTOM));
			
			dummy.add(getLabelKleinRot("Abschluss"),ccdum.xy(5,3,CellConstraints.LEFT,CellConstraints.BOTTOM));
			eltern.btf[19] = new JRtaTextField("ZAHLEN",true);
			eltern.btf[19].setName("F_117");
			eltern.btf[19].setHorizontalAlignment(SwingConstants.RIGHT);			
			dummy.add(eltern.btf[19],ccdum.xy(5, 4,CellConstraints.FILL,CellConstraints.BOTTOM));

			dummy.getPanel().validate();
			erlaeut.add(dummy.getPanel(),ccerlaeut.xy(3,2,CellConstraints.FILL,CellConstraints.BOTTOM));
			erlaeut.getPanel().validate();
			return erlaeut.getPanel() ;
	}
	/***********************************************/
	private JPanel getChecks1(){
		//                                     1                 2         3  
		FormLayout laychecks = new FormLayout("fill:0:grow(0.2),2dlu,fill:0:grow(0.2),"+
			//   4       5             6          7           8          9
				"2dlu,fill:0:grow(0.2),2dlu,fill:0:grow(0.2),2dlu,fill:0:grow(0.2)",
				
			//   1    2   3
				"2dlu,p,2dlu");
		PanelBuilder checks = new PanelBuilder(laychecks);
		checks.getPanel().setOpaque(false);
		CellConstraints ccchecks = new CellConstraints();
		String[] namen = new String[] {"F_173","F_174","F_175","F_176","F_177"};
		String[] titel = new String[] {"Herz / Kreislauf","Bewegungsapparat","Nervensystem","Psyche","sonstige"};
		int objstart = 0;
		int ystart = 1;
		for(int i = 0;i < 5;i++){
			FormLayout laydummy = new FormLayout("p,2dlu,p","p");
			PanelBuilder dummy = new PanelBuilder(laydummy);
			dummy.getPanel().setOpaque(false);
			CellConstraints ccdum = new CellConstraints();
			eltern.bchb[objstart] = new JRtaCheckBox("");
			eltern.bchb[objstart].setName(namen[i]);
			eltern.bchb[objstart].setOpaque(false);
			dummy.add(eltern.bchb[objstart],ccdum.xy(1, 1));
			//dummy.add(getLabelKleinRot(titel[i]),ccdum.xy(3, 1));
			dummy.add(getLabelArialFettRot(titel[i]),ccdum.xy(3, 1));
			dummy.getPanel().validate();
			checks.add(dummy.getPanel(),ccchecks.xy(ystart,2));
			ystart += 2;
			objstart++;
		}

		checks.getPanel().validate();
		return checks.getPanel(); 
		
	}
	/************************
	 * 
	 * ********Diagnosen 1 - 5
	 * 
	 */	

	private JPanel getBlock3(){
		FormLayout laytit = new FormLayout("550dlu","2dlu,p:g");
		PanelBuilder tit = new PanelBuilder(laytit);
		tit.setOpaque(false);
		CellConstraints cctit = new CellConstraints(); 
		tit.add(getBlock3Komplett(),cctit.xy(1,2,CellConstraints.FILL,CellConstraints.TOP));
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
		eltern.btf[13] = new JRtaTextField("GROSS",false);
		eltern.btf[13].setName("F_74");
		ent.add(eltern.btf[13],ccent.xy(1, 3));
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
		eltern.bcmb[0] = new JRtaComboBox(seite);
		eltern.bcmb[0].setName("F_79");
		ent.add(eltern.bcmb[0],ccent.xy(1, 3));
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
		eltern.bcmb[1] = new JRtaComboBox(sicher);
		eltern.bcmb[1].setName("F_80");
		ent.add(eltern.bcmb[1],ccent.xy(1, 3));
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
		eltern.bcmb[2] = new JRtaComboBox(erfolg);
		eltern.bcmb[2].setName("F_81");
		ent.add(eltern.bcmb[2],ccent.xy(1, 3));
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
		eltern.btf[14] = new JRtaTextField("GROSS",false);
		eltern.btf[14].setName("F_82");
		ent.add(eltern.btf[14],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(4, 7,CellConstraints.LEFT,CellConstraints.BOTTOM));
		
		entlay = new FormLayout("40dlu","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("87");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.bcmb[3] = new JRtaComboBox(seite);
		eltern.bcmb[3].setName("F_87");
		ent.add(eltern.bcmb[3],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(6, 7,CellConstraints.LEFT,CellConstraints.BOTTOM));
		
		entlay = new FormLayout("40dlu","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("88");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.bcmb[4] = new JRtaComboBox(sicher);
		eltern.bcmb[4].setName("F_88");
		ent.add(eltern.bcmb[4],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(8, 7,CellConstraints.LEFT,CellConstraints.BOTTOM));

		entlay = new FormLayout("40dlu","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("89");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.bcmb[5] = new JRtaComboBox(erfolg);
		eltern.bcmb[5].setName("F_81");
		ent.add(eltern.bcmb[5],ccent.xy(1, 3));
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
		eltern.btf[15] = new JRtaTextField("GROSS",false);
		eltern.btf[15].setName("F_90");
		ent.add(eltern.btf[15],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(4, 10,CellConstraints.LEFT,CellConstraints.BOTTOM));
		
		entlay = new FormLayout("40dlu","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("95");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.bcmb[6] = new JRtaComboBox(seite);
		eltern.bcmb[6].setName("F_95");
		ent.add(eltern.bcmb[6],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(6, 10,CellConstraints.LEFT,CellConstraints.BOTTOM));
		
		entlay = new FormLayout("40dlu","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("96");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.bcmb[7] = new JRtaComboBox(sicher);
		eltern.bcmb[7].setName("F_96");
		ent.add(eltern.bcmb[7],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(8, 10,CellConstraints.LEFT,CellConstraints.BOTTOM));

		entlay = new FormLayout("40dlu","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("97");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.bcmb[8] = new JRtaComboBox(erfolg);
		eltern.bcmb[8].setName("F_97");
		ent.add(eltern.bcmb[8],ccent.xy(1, 3));
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
		eltern.btf[16] = new JRtaTextField("GROSS",false);
		eltern.btf[16].setName("F_98");
		ent.add(eltern.btf[16],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(4, 13,CellConstraints.LEFT,CellConstraints.BOTTOM));
		
		entlay = new FormLayout("40dlu","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("103");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.bcmb[9] = new JRtaComboBox(seite);
		eltern.bcmb[9].setName("F_103");
		ent.add(eltern.bcmb[9],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(6, 13,CellConstraints.LEFT,CellConstraints.BOTTOM));
		
		entlay = new FormLayout("40dlu","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("104");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.bcmb[10] = new JRtaComboBox(sicher);
		eltern.bcmb[10].setName("F_104");
		ent.add(eltern.bcmb[10],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(8, 13,CellConstraints.LEFT,CellConstraints.BOTTOM));

		entlay = new FormLayout("40dlu","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("105");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.bcmb[11] = new JRtaComboBox(erfolg);
		eltern.bcmb[11].setName("F_105");
		ent.add(eltern.bcmb[11],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(10, 13,CellConstraints.LEFT,CellConstraints.BOTTOM));
		
		/**********Diagnose 5**********************/
		
		lab = getLabel("5.");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(2,15));
		eltern.bta[4] = new JTextArea();
		eltern.bta[4].setName("DIAG5");
		tit.add(eltern.bta[4],cctit.xy(2, 16,CellConstraints.FILL,CellConstraints.FILL));

		entlay = new FormLayout("55dlu","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("106");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.btf[17] = new JRtaTextField("GROSS",false);
		eltern.btf[17].setName("F_106");
		ent.add(eltern.btf[17],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(4, 16,CellConstraints.LEFT,CellConstraints.BOTTOM));
		
		entlay = new FormLayout("40dlu","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("111");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.bcmb[12] = new JRtaComboBox(seite);
		eltern.bcmb[12].setName("F_111");
		ent.add(eltern.bcmb[12],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(6, 16,CellConstraints.LEFT,CellConstraints.BOTTOM));
		
		entlay = new FormLayout("40dlu","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("112");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.bcmb[13] = new JRtaComboBox(sicher);
		eltern.bcmb[13].setName("F_112");
		ent.add(eltern.bcmb[13],ccent.xy(1, 3));
		tit.add(ent.getPanel(),cctit.xy(8, 16,CellConstraints.LEFT,CellConstraints.BOTTOM));

		entlay = new FormLayout("40dlu","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("113");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.bcmb[14] = new JRtaComboBox(erfolg);
		eltern.bcmb[14].setName("F_113");
		ent.add(eltern.bcmb[14],ccent.xy(1, 3));
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
	
	/************************
	 * 
	 * ********Aufnahme und Entlassdaten
	 * 
	 */	
	private JPanel getVonBis(){ //                               
		FormLayout laytit = new FormLayout("250dlu,0dlu,300dlu","0dlu,p,5dlu");
		PanelBuilder tit = new PanelBuilder(laytit);
		tit.setOpaque(false);
		CellConstraints cctit = new CellConstraints(); 
		                               //                                       1   2   3  4  5   6
		FormLayout dummy = new FormLayout("55dlu,fill:0:grow(1.0),55dlu,10dlu","0dlu,p,2dlu,p,0dlu,p");
		PanelBuilder dum = new PanelBuilder(dummy);
		dum.getPanel().setOpaque(false);
		CellConstraints ccdum = new CellConstraints();
		JLabel lab = new JLabel("Dauer");
		lab.setFont(this.fontarialfett);
		dum.add(lab,ccdum.xy(1,2));
		dum.add(getLabelKleinRot("Beginndatum"),ccdum.xy(1,4,CellConstraints.LEFT,CellConstraints.BOTTOM));
		eltern.btf[11] = new JRtaTextField("DATUM",true);
		eltern.btf[11].setName("AUFDAT3");
		dum.add(eltern.btf[11],ccdum.xy(1,6,CellConstraints.FILL,CellConstraints.TOP));
				
		dum.add(getLabelKleinRot("Abschlussdatum"),ccdum.xy(3,4,CellConstraints.DEFAULT,CellConstraints.BOTTOM));
		eltern.btf[12] = new JRtaTextField("DATUM",true);
		eltern.btf[12].setName("ENTDAT3");
		dum.add(eltern.btf[12],ccdum.xy(3,6,CellConstraints.FILL,CellConstraints.TOP));


		
		dum.getPanel().validate();
		tit.add(dum.getPanel(),cctit.xy(1,2));
		tit.getPanel().validate();
		return tit.getPanel();
	}
	/************************
	 * 
	 * ********Der Adressblock mit Stammdaten und VSNR
	 * 
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
	/************************************/
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

		eltern.btf[0] = new JRtaTextField("GROSS",false);
		eltern.btf[0].setName("VNUMMER");
		tit.add(eltern.btf[0],cctit.xyw(2,3,3));
		eltern.btf[1] = new JRtaTextField("GROSS",false);
		eltern.btf[1].setName("AIGR");
		tit.add(eltern.btf[1],cctit.xy(6,3));
		
		lab = getLabel ("Name, Vorname:");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xyw(2,4,3));
		eltern.btf[2] = new JRtaTextField("nix",false);
		eltern.btf[2].setName("NAMEVOR");
		tit.add(eltern.btf[2],cctit.xyw(2,5,5));
		
		lab = getLabel ("Geburtsdatum:");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xyw(2,6,3));
		eltern.btf[3] = new JRtaTextField("DATUM",false);
		eltern.btf[3].setName("GEBOREN");
		tit.add(eltern.btf[3],cctit.xy(2,7));
		tit.add(getLabel("(Sofern nicht in Vers. Nr. enthalten)"),cctit.xyw(4,7,3));
		
		lab = getLabel ("Straße,Hausnummer:");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xyw(2,8,3));
		eltern.btf[4] = new JRtaTextField("nix",false);
		eltern.btf[4].setName("STRASSE");
		tit.add(eltern.btf[4],cctit.xyw(2,9,5));
		
		lab = getLabel ("Postleitzahl:");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(2,10));
		lab = getLabel ("Wohnort");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(4,10));
		eltern.btf[5] = new JRtaTextField("ZAHLEN",false);
		eltern.btf[5].setName("PLZ");
		tit.add(eltern.btf[5],cctit.xy(2,11));
		eltern.btf[6] = new JRtaTextField("nix",false);
		eltern.btf[6].setName("ORT");
		tit.add(eltern.btf[6],cctit.xyw(4,11,3));

		lab = getLabel("Versicherter (Name, Vorname) falls nicht mit Patient indentisch");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xyw(2,12,5));
		eltern.btf[7] = new JRtaTextField("nix",false);
		eltern.btf[7].setName("VNAMEVO");
		tit.add(eltern.btf[7],cctit.xyw(2,13,5));
		tit.getPanel().validate();
		return tit.getPanel();
	}
	
	/***********************************************/	
	private JPanel getBlock1Rechts(){       // 1   2    3   4    5     6     7   
		FormLayout laytit = new FormLayout("4dlu,40dlu,10dlu,40,100dlu,40dlu,50dlu,4dlu",
				//     3=Vnr.  5=Name 7=geb 9=stras
				// 1  2  3   4  5   6    7      8   9  10 
				"4dlu,p, p,  p, p,  5dlu,p,     p,  p, 1dlu,"+
				//
				//11  12    13 14   15  16   17  18 19  20  21  22   23
				 "p,  5dlu, p, 13dlu,p, 1dlu,p, 7dlu,p, 1dlu,p, 0dlu,4dlu");
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
		eltern.btf[8] = new JRtaTextField("nix",false);
		eltern.btf[8].setName("MSNR");
		tit.add(eltern.btf[8],cctit.xy(2,4));
		eltern.btf[9] = new JRtaTextField("nix",false);
		eltern.btf[9].setName("BNR");
		tit.add(eltern.btf[9],cctit.xy(4,4));
		
		lab = getLabel("Behandlungsstätte:");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xyw(2,7,6));
		//SystemConfig.vGutachtenDisplay.get(0);
		lab = getLabel(SystemConfig.vGutachtenDisplay.get(0));
		lab.setFont(fontcourier);
		tit.add(lab,cctit.xyw(2,9,6));
		lab = new JLabel(SystemConfig.vGutachtenDisplay.get(1));
		lab.setFont(fontcourier);
		tit.add(lab,cctit.xyw(2,11,6));
		lab = new JLabel(SystemConfig.vGutachtenDisplay.get(2));
		lab.setFont(fontcourier);
		tit.add(lab,cctit.xyw(2,13,6));		
		
		lab = getLabel("Abteilung:");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(2,15));
		
		lab = new JLabel("ganztägig ambulante Rehabilitation");
		lab.setFont(fontcourier);
		tit.add(lab,cctit.xyw(2,17,6));

		lab = getLabel ("IK-NR:");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(2,19));
		
		lab = getLabel("Abt.NR:");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(6,19));

		eltern.btf[28] = new JRtaTextField("ZAHLEN",false);
		eltern.btf[28].setName("IK");
		eltern.btf[28].setText(SystemConfig.vGutachtenIK.get(eltern.cbktraeger.getSelectedIndex()));
		eltern.btf[28].setFont(fontcourier);
		eltern.btf[28].setForeground(Color.BLUE);

		tit.add(eltern.btf[28],cctit.xyw(2,21,3));
		
		eltern.btf[10] = new JRtaTextField("ZAHLEN",false);
		eltern.btf[10].setName("ABTEILUNG");
		eltern.btf[10].setText("2300");
		tit.add(eltern.btf[10],cctit.xy(6,21));
		
		return tit.getPanel();
	}
	/************************
	 * 
	 * 
	 * 
	 */	
	private void laden(){
		//"bericht2","freitext","berichtid='"+eltern.berichtid+"'");
		String berichtid = new Integer(eltern.berichtid).toString();
		StringBuffer buf = new StringBuffer();
		buf.append("select ");
		for(int i = 0; i < 24;i++){
			buf.append(eltern.btf[i].getName()+",");
		}
		for(int i = 0; i < 15;i++){
			buf.append(eltern.bcmb[i].getName()+",");
		}
		for(int i = 0; i < 7;i++){
			buf.append(eltern.bchb[i].getName()+",");
		}
		for(int i = 0; i < 7;i++){
			if(i < 6){
				buf.append(eltern.bta[i].getName()+",");	
			}else{
				buf.append(eltern.bta[i].getName());				
			}
			
		}
		buf.append(" from bericht2 where berichtid='"+berichtid+"'");
		//System.out.println(buf.toString());
		holeSatz(buf);
	}
	/************************
	 * 
	 * 
	 * 
	 */	
	private void holeSatz(StringBuffer buf){
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try{
			Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			rs = stmt.executeQuery(buf.toString());

			if(rs.next()){
				for(int i = 0; i < 7;i++){
					eltern.bta[i].setText( (rs.getString(eltern.bta[i].getName())==null  ? "" :  rs.getString(eltern.bta[i].getName())) ) ;
				}
				for(int i = 0; i < 24;i++){
					if("AUFDAT2AUFDAT3ENTDAT3UNTDATGEBORENENTDAT2ENTDAT1AUFDAT1".contains(eltern.btf[i].getName())){
						eltern.btf[i].setText( (rs.getString(eltern.btf[i].getName())==null  ? "  .  .    " :  DatFunk.sDatInDeutsch(rs.getString(eltern.btf[i].getName())) ) );	
					}else{
						eltern.btf[i].setText( (rs.getString(eltern.btf[i].getName())==null  ? "" :  rs.getString(eltern.btf[i].getName()))  );
					}
				}
				for(int i = 0; i < 7;i++){
					eltern.bchb[i].setSelected( ( rs.getString(eltern.bchb[i].getName()).equals("1") ? true : false) );
				}
				for(int i = 0; i < 15;i++){
					eltern.bcmb[i].setSelectedItem( (rs.getString(eltern.bcmb[i].getName())==null  ? "" :  rs.getString(eltern.bcmb[i].getName())) );
				}
				
				if(eltern.berichttyp.equals("LVA-ASP-Arztber")){
					eltern.cbktraeger.setSelectedIndex(1);
				}else if(eltern.berichttyp.equals("BfA-IRENA-Arztb")){
					eltern.cbktraeger.setSelectedIndex(0);
				}else{
					eltern.cbktraeger.setSelectedItem(eltern.empfaenger);
				}
			
			}
			Reha.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}catch(SQLException ev){
			System.out.println("SQLException: " + ev.getMessage());
			System.out.println("SQLState: " + ev.getSQLState());
			System.out.println("VendorError: " + ev.getErrorCode());
		}	
		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { // ignore }
					rs = null;
				}
			}	
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { // ignore }
					stmt = null;
				}
			}
		}
		
	}
	/************************
	 * 
	 * 
	 * 
	 */
	public void testeIK(){
		System.out.println(SystemConfig.vGutachtenAbsAdresse);
		eltern.btf[28].setText(SystemConfig.vGutachtenIK.get(eltern.cbktraeger.getSelectedIndex()));		
		eltern.btf[28].validate();
		eltern.btf[28].repaint();
	}
	/************************
	 * 
	 * 
	 * 
	 */
	private void doKopfNeu(){
		eltern.btf[2].setText(StringTools.EGross(PatGrundPanel.thisClass.patDaten.get(2))+
				", "+StringTools.EGross(PatGrundPanel.thisClass.patDaten.get(3)));
		eltern.btf[3].setText(DatFunk.sDatInDeutsch(PatGrundPanel.thisClass.patDaten.get(4)));
		eltern.btf[4].setText(StringTools.EGross(PatGrundPanel.thisClass.patDaten.get(21)));
		eltern.btf[5].setText(StringTools.EGross(PatGrundPanel.thisClass.patDaten.get(23)));
		eltern.btf[6].setText(StringTools.EGross(PatGrundPanel.thisClass.patDaten.get(24)));		
	}
	/************************
	 * 
	 * Der Titel (E-Bericht oder Nachsorge)
	 * 
	 */
	
	private JXPanel getTitel(){
		JXPanel tit = new JXPanel();
		tit.setOpaque(false);
		FormLayout laytit = new FormLayout("p,33dlu,p","2dlu,p");
		CellConstraints cctit = new CellConstraints(); 
		tit.setLayout(laytit);

		titel = new JLabel("Reha Nachsorgedokumentation");
		titel.setFont(fontgross);
		titel.setForeground(Color.BLUE);
		tit.add(titel,cctit.xy(1, 2));
		tit.add(eltern.cbktraeger,cctit.xy(3, 2));
		eltern.cbktraeger.setActionCommand("empfaenger");
		eltern.cbktraeger.addActionListener(this);
		
		tit.validate();
		return tit;
	}
	/************************
	 * 
	 * Der Titel (Untersuchungsbefund und....)
	 * 
	 */
	private JPanel getTitelUntersuchung(){
		FormLayout laytit = new FormLayout("0dlu,p",
		"2dlu,p,0dlu");
		PanelBuilder tit = new PanelBuilder(laytit);
		tit.setOpaque(false);               
		CellConstraints cctit = new CellConstraints();
		JLabel lab = new JLabel("Untersuchungsbefund und Funktionsveränderungen");
		lab.setFont(fontarialfett);
		tit.add(lab,cctit.xy(2, 2));
		tit.getPanel().validate();
		return tit.getPanel();	
	}
	/************************
	 * 
	 * Der Titel für die Rubrik Arbeitsfähigekeit
	 * 
	 */
	private JPanel getTitelArbeitsfaehigkeit(){
		FormLayout laytit = new FormLayout("0dlu,p",
		"2dlu,p,0dlu");
		PanelBuilder tit = new PanelBuilder(laytit);
		tit.setOpaque(false);               
		CellConstraints cctit = new CellConstraints();
		JLabel lab = new JLabel("Arbeitsfähigkeit");
		lab.setFont(fontarialfett);
		tit.add(lab,cctit.xy(2, 2));
		tit.getPanel().validate();
		return tit.getPanel();	
	}

	private JLabel getLabel(String text){
		JLabel lab = new JLabel(text);
		lab.setFont(fontklein);
		return lab;
	}
	private JLabel getLabelArialNormal(String text){
		JLabel lab = new JLabel(text);
		lab.setFont(fontarialnormal);
		return lab;
	}
	private JLabel getLabelArialNormalRot(String text){
		JLabel lab = new JLabel(text);
		lab.setFont(fontarialnormal);
		lab.setForeground(Color.RED);
		return lab;
	}
	private JLabel getLabelArialFettRot(String text){
		JLabel lab = new JLabel(text);
		lab.setFont(fontarialfett);
		lab.setForeground(Color.RED);
		return lab;
	}
	private JLabel getLabelArialFettNormal(String text){
		JLabel lab = new JLabel(text);
		lab.setFont(fontarialfett);
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
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentResized(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	

}
