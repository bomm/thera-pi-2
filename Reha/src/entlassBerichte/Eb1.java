package entlassBerichte;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;


import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;

import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemTools.JCompTools;
import systemTools.JRtaCheckBox;
import systemTools.JRtaComboBox;
import systemTools.JRtaTextField;
import systemTools.StringTools;
import terminKalender.DatFunk;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class Eb1 implements ActionListener,ComponentListener {
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
	JLabel[][] labstitel = { {null,null},{null,null},
			{null},{null},
			{null,null},{null,null,null},
			{null,null},{null},
			{null},{null},
			{null},{null},
			{null},{null},
			{null,null},{null,null,null},
			{null,null}
	};
	JLabel[] labsheadline = {null,null,null};
	String[] headlineneu = {"Weiterbehandelnde Ärzte / Psychologen",
			"Patienten","Rentenversicherung"};

	String[] headlinealt = {"",
			"",""};

	public String[][] stitelalt = {{"Diagn.","Klärung"},{"Stat.","Behandl."},
			{"Selbsthilfegruppe"},{"Amb.Reha Sucht"},
			{"Gewichts-","reduktion"},{"Alkohol-", "karenz",""},
			{"Operation",""},{"Psychotherapie"},
			{"Vorstell. Suchtberatung"},{"spezielle Nachsorge"},
			{"Nikotinkarenz"},{"Heil- und Hilfsmittel"},
			{"Rehabilitationssport"},{"Funktionstraining"},
			{"stufenweise","Wiedereingliederung"},{"Bf. Leistung","prüfen",""},
			{"sonstige","Anregung"}
	};
	public String[][] stitelneu = {{"Diagnostische","Klärung"},{"Kontrolle Laborwerte/","Medikamente"},
			{"Stat.Behandlung / OP"},{"Suchtberatung"},
			{"Psychol. Beratung /","Psychotherapie"},{"Heil- und Hilfsmittel","inkl. Physiotherapie","und Ergotherapie"},
			{"Übungen selbständig","fortführen"},{"Sport und Bewegung"},
			{"Gewichtsreduktion",""},{"Nikotinkarenz"},
			{"Alkoholkarenz",""},{"Selbsthilfegruppe"},
			{"Rehabilitationssport"},{"Funktionstraining"},
			{"stufenweise","Wiedereingliederung"},{"Leistungen zur","Teilhabe am","Arbeitsleben prüfen"},
			{"Reha-Nachsorge (z.B.","IRENA oder ASP"}
	};

	JScrollPane jscr = null;
	boolean inGuiInit = true;
	public Eb1(EBerichtPanel xeltern){
		pan = new JXPanel(new BorderLayout());
		pan.setOpaque(false);
		pan.addComponentListener(this);
		pan.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		eltern = xeltern;
		fontgross =new Font("Arial",Font.BOLD,14);
		fontklein =new Font("Arial",Font.PLAIN,9);
		fontnormal =new Font("Arial",Font.PLAIN,10);
		fontarialfett =new Font("Arial",Font.BOLD,12);
		fontarialnormal =new Font("Arial",Font.PLAIN,12);		
		fontcourier =new Font("Courier New",Font.PLAIN,12);
		//pan.add(getTitel());
		//JPanel cs = constructSeite();
		//cs.validate();
		new SwingWorker<Void,Void>(){
			
			@Override
			protected Void doInBackground() throws Exception {
				pan.add(constructSeite(),BorderLayout.CENTER);
				for(int i = 0; i < 25;i++){
					eltern.btf[i].setFont(fontcourier);
					eltern.btf[i].setForeground(Color.BLUE);
				}
				for(int i = 0; i < 7;i++){
					eltern.bta[i].setFont(fontcourier);
					eltern.bta[i].setForeground(Color.BLUE);
					eltern.bta[i].setWrapStyleWord(true);
					eltern.bta[i].setLineWrap(true);
					eltern.bta[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
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
						 			laden();
						 			eltern.btf[0].requestFocusInWindow();
						 			jscr.scrollRectToVisible(new Rectangle(0,0,0,0));
						 			inGuiInit = false;
									testeIK();
									eltern.meldeInitOk(0);
									eltern.doSysVars();
									testeObAlt();
									}catch(Exception ex){
										ex.printStackTrace();
									}
									return null;
								}
				 				
				 			}.execute();
				 		}else if(eltern.uebernahmeid >= 0){
				 			ladeDatenAusVorbericht(eltern.uebernahmeid);
				 			eltern.meldeInitOk(0);
							eltern.doSysVars();
				 			SwingUtilities.invokeLater(new Runnable(){
				 				public void run(){
						 			jscr.scrollRectToVisible(new Rectangle(0,0,0,0));				 					
				 				}
				 			});
				 			JOptionPane.showMessageDialog(null,"Bitte stellen Sie als erstes den Empfäger des Gutachtens ein (Berichttyp).");
				 		}else{
				 			eltern.meldeInitOk(0);
				 			JOptionPane.showMessageDialog(null,"Bitte stellen Sie als erstes den Empfäger des Gutachtens ein (Berichttyp).");
				 			doKopfNeu();
							eltern.doSysVars();
				 			testeObAlt();
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
	private void doKopfNeu(){
		eltern.btf[2].setText(StringTools.EGross(Reha.thisClass.patpanel.patDaten.get(2))+
				", "+StringTools.EGross(Reha.thisClass.patpanel.patDaten.get(3)));
		eltern.btf[3].setText(DatFunk.sDatInDeutsch(Reha.thisClass.patpanel.patDaten.get(4)));
		eltern.btf[4].setText(StringTools.EGross(Reha.thisClass.patpanel.patDaten.get(21)));
		eltern.btf[5].setText(StringTools.EGross(Reha.thisClass.patpanel.patDaten.get(23)));
		eltern.btf[6].setText(StringTools.EGross(Reha.thisClass.patpanel.patDaten.get(24)));		
	}

	private void laden(){
		//"bericht2","freitext","berichtid='"+eltern.berichtid+"'");
		String berichtid = Integer.toString(eltern.berichtid);
		StringBuffer buf = new StringBuffer();
		buf.append("select ");
		for(int i = 0; i < 25;i++){
			buf.append(eltern.btf[i].getName()+",");
		}
		buf.append(eltern.btf[27].getName()+",");

		for(int i = 0; i < 20;i++){
			buf.append(eltern.bcmb[i].getName()+",");
		}
		for(int i = 0; i < 17;i++){
			buf.append(eltern.bchb[i].getName()+",");
		}
		for(int i = 0; i < 7;i++){
			buf.append(eltern.bta[i].getName()+",");
		}
		buf.append("ARZT1,ARZT2,ARZT3,");
		buf.append("UNTDAT from bericht2 where berichtid='"+berichtid+"'");
		////System.out.println(buf.toString());
		holeSatz(buf);
	}
	
	private void ladeDatenAusVorbericht(int vorbericht){
		String cmd = "select vnummer,aigr,namevor,geboren,strasse,plz,ort,msnr,"+
		"diag1,diag2,diag3,diag4,diag5,"+
		"F_74,F_79,F_80,F_81,F_82,F_87,F_88,F_89,F_90,F_95,F_96,F_97,F_98,F_103,F_104,F_105,"+
		"F_106,F_111,F_112,F_113 from bericht2 where berichtid='"+Integer.toString(vorbericht)+"' LIMIT 1";
		Vector<Vector<String>> vec = SqlInfo.holeFelder(cmd);
		//System.out.println(vec);
		if(vec.size() > 0){
			try{
				for(int i = 0; i < 7;i++){
					if(eltern.btf[i].getRtaType().equals("DATUM")){
						if(vec.get(0).get(i).length()==10){
							eltern.btf[i].setText( DatFunk.sDatInDeutsch(vec.get(0).get(i)) );
						}
					}else{
						eltern.btf[i].setText(vec.get(0).get(i));
					}
				}
				eltern.btf[8].setText(vec.get(0).get(7));
				for(int i = 0; i < 5;i++){
					eltern.bta[i].setText(vec.get(0).get(i+8));
				}
				for(int i = 0; i < 5;i++){
					eltern.btf[i+17].setText(vec.get(0).get(i+13+(i*3)));
				}
				for(int i = 0; i < 5;i++){
					eltern.bcmb[2+(i*3)].setSelectedItem(vec.get(0).get(i+14+(i*3)));
				}
				for(int i = 0; i < 5;i++){
					eltern.bcmb[3+(i*3)].setSelectedItem(vec.get(0).get(i+15+(i*3)));
				}
				for(int i = 0; i < 5;i++){
					eltern.bcmb[4+(i*3)].setSelectedItem(vec.get(0).get(i+16+(i*3)));
				}
				
			}catch(Exception ex){
				ex.printStackTrace();
			}

		}
	}
	
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
			rs = stmt.executeQuery(buf.toString());
			String test = "";
			if(rs.next()){
				for(int i = 0; i < 7;i++){
					eltern.bta[i].setText( (rs.getString(eltern.bta[i].getName())==null  ? "" :  rs.getString(eltern.bta[i].getName())) ) ;
				}
				for(int i = 0; i < 25;i++){
					if("AUFDAT3ENTDAT3UNTDATGEBOREN".contains(eltern.btf[i].getName())){
						eltern.btf[i].setText( (rs.getString(eltern.btf[i].getName())==null  ? "  .  .    " :  DatFunk.sDatInDeutsch(rs.getString(eltern.btf[i].getName())) ) );	
					}else{
						eltern.btf[i].setText( (rs.getString(eltern.btf[i].getName())==null  ? "" :  rs.getString(eltern.btf[i].getName()))  );
					}
				}
				for(int i = 0; i < 17;i++){
					test = ( rs.getString(eltern.bchb[i].getName())==null ? "0" : rs.getString(eltern.bchb[i].getName()));
					//eltern.bchb[i].setSelected( ( rs.getString(eltern.bchb[i].getName()).equals("1") ? true : false) );
					eltern.bchb[i].setSelected( test.equals("1") ? true : false );
				}
				for(int i = 0; i < 20;i++){
					eltern.bcmb[i].setSelectedItem( (rs.getString(eltern.bcmb[i].getName())==null  ? "" :  rs.getString(eltern.bcmb[i].getName())) );
				}

				String xname = "UNTDAT";
				String inhalt = (rs.getString(xname)==null ? ""  : rs.getString((xname)));
				if(!inhalt.trim().equals("")){
					eltern.btf[27].setText(DatFunk.sDatInDeutsch(inhalt));
				}
				xname = "ARZT1";
				inhalt = (rs.getString(xname)==null ? ""  : rs.getString((xname)));
				eltern.barzttf[0].setText(inhalt);
				xname = "ARZT2";
				inhalt = (rs.getString(xname)==null ? ""  : rs.getString((xname)));
				eltern.barzttf[1].setText(inhalt);
				xname = "ARZT3";
				inhalt = (rs.getString(xname)==null ? ""  : rs.getString((xname)));
				eltern.barzttf[2].setText(inhalt);
				if(eltern.berichttyp.equals("LVA-Arztbericht")){
					eltern.cbktraeger.setSelectedIndex(1);
				}else if(eltern.berichttyp.equals("BfA-Arztbericht")){
					eltern.cbktraeger.setSelectedIndex(0);
				}else{
					eltern.cbktraeger.setSelectedItem(eltern.empfaenger);
				}
			}
		}catch(SQLException ev){
			//System.out.println("SQLException: " + ev.getMessage());
			//System.out.println("SQLState: " + ev.getSQLState());
			//System.out.println("VendorError: " + ev.getErrorCode());
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

	/****************************
	 * 
	 * 
	 * 
	 * @return
	 */
	public JScrollPane constructSeite(){// 1          2          3         4           5
		FormLayout lay = new FormLayout("10dlu,fill:0:grow(0.25),p,fill:0:grow(0.25),10dlu",
				
				//   2=titel   4=block1 6=block2 8=block3
				// 1    2   3    4   5    6      8   9   10  11   12  13 14   15  16  17
				"20dlu, p ,2dlu, p, 10dlu,p,5dlu,p ,5dlu,p,  5dlu,p, 0dlu,p, 15dlu,p,20dlu");
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
		//Block 4 ist Gewicht, Arbeitsunf�higkeitszeiten
		pb.add(getBlock4(),cc.xy(3, 10));
		//Block 5 �berschrift f�r Vorschl�ge f�r nachfolgende Ma�nahmen
		pb.add(getBlock5(),cc.xy(3, 12));
		//Block 5 Riesenschei�block mit den nachfolgenden Ma�nahmen
		pb.add(getBlock6(),cc.xy(3, 14));
		// Unterschriften ged�nse
		pb.add(getBlock7(),cc.xy(3, 16));
		pb.getPanel().validate();
		
		jscr = JCompTools.getTransparentScrollPane(pb.getPanel());
		jscr.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);

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
	private JPanel getBlock7(){
		FormLayout lay1 = new FormLayout("p,fill:0:grow(1.0)",
			    //           5=F174  7=F175  9=F176   11=F177
				//  1   2   3  4    5     6     7 
				  "p,15dlu,p");		
		PanelBuilder lei1 = new PanelBuilder(lay1);
		lei1.setOpaque(false); 
		CellConstraints ccl1 = new CellConstraints();
		
		FormLayout dummy = new FormLayout("p,2dlu,60dlu","p");
		PanelBuilder dum = new PanelBuilder(dummy);
		dum.getPanel().setOpaque(false);
		CellConstraints ccdum = new CellConstraints();
		JLabel lab = new JLabel("Datum der Unterschrift:");
		lab.setFont(this.fontarialnormal);
		dum.add(lab,ccdum.xy(1,1));
		eltern.btf[27] = new JRtaTextField("DATUM",false);
		eltern.btf[27].setName("UNTDAT");
		eltern.btf[27].setFont(fontcourier);
		eltern.btf[27].setForeground(Color.BLUE);

		dum.add(eltern.btf[27],ccdum.xy(3,1));
		dum.getPanel().validate();
		lei1.add(dum.getPanel(),ccl1.xy(1,1));
//                                   1             2         3             4          5
		dummy = new FormLayout("fill:0:grow(0.33),20dlu,fill:0:grow(0.33),20dlu,fill:0:grow(0.33)",
//  		 1  2   3    4   5
			"p,2dlu,1px,2dlu,p");
		dum = new PanelBuilder(dummy);
		dum.getPanel().setOpaque(false);
		ccdum = new CellConstraints();
		eltern.barzttf[0] = new JRtaTextField("nix",false);
		eltern.barzttf[0].setName("ARZT1");
		eltern.barzttf[0].setFont(fontcourier);
		eltern.barzttf[0].setForeground(Color.BLUE);
		dum.add(eltern.barzttf[0],ccdum.xy(1, 1));

		eltern.barzttf[1] = new JRtaTextField("nix",false);
		eltern.barzttf[1].setName("ARZT2");
		eltern.barzttf[1].setFont(fontcourier);
		eltern.barzttf[1].setForeground(Color.BLUE);
		dum.add(eltern.barzttf[1],ccdum.xy(3, 1));

		eltern.barzttf[2] = new JRtaTextField("nix",false);
		eltern.barzttf[2].setName("ARZT3");
		eltern.barzttf[2].setFont(fontcourier);
		eltern.barzttf[2].setForeground(Color.BLUE);
		dum.add(eltern.barzttf[2],ccdum.xy(5, 1));
		dum.add(getRand(Color.BLACK),ccdum.xy(1,3));
		dum.add(getRand(Color.BLACK),ccdum.xy(3,3));
		dum.add(getRand(Color.BLACK),ccdum.xy(5,3));
		dum.add(getLabel("Ltd. Ärztin / ltd. Arzt"),ccdum.xy(1,5));
		dum.add(getLabel("Oberärztin / Oberarzt"),ccdum.xy(3,5));
		dum.add(getLabel("Stat.-Ärztin / Stat.-Arzt"),ccdum.xy(5,5));
		dum.getPanel().validate();
		lei1.add(dum.getPanel(),ccl1.xyw(1, 3,2,CellConstraints.FILL,CellConstraints.DEFAULT));
		
		lei1.getPanel().validate();
		return lei1.getPanel();
		
	}

	
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
		tit.add(getBlock4Komplett(),cctit.xy(1,2,CellConstraints.FILL,CellConstraints.TOP));
		tit.getPanel().validate();
		return tit.getPanel();
	}
	private JPanel getBlock5(){
		FormLayout laytit = new FormLayout("550dlu","2dlu,p:g");
		PanelBuilder tit = new PanelBuilder(laytit);
		tit.setOpaque(false);
		CellConstraints cctit = new CellConstraints();
		tit.add(getBlock5Komplett(),cctit.xy(1,2));
		tit.getPanel().validate();
		return tit.getPanel();
	}
	private JPanel getBlock6(){
		FormLayout laytit = new FormLayout("550dlu","2dlu,p:g");
		PanelBuilder tit = new PanelBuilder(laytit);
		tit.setOpaque(false);
		CellConstraints cctit = new CellConstraints();
		tit.add(getBlock6Komplett(),cctit.xy(1,2,CellConstraints.FILL,CellConstraints.FILL));
		tit.getPanel().validate();
		return tit.getPanel();
	}
	private JPanel getBlock6Komplett(){   //1         2               3   4           5             6  7              8             9  
		FormLayout laytit = new FormLayout("4dlu,fill:0:grow(0.33),  2px,4dlu,  fill:0:grow(0.33) ,2px,4dlu      ,fill:0:grow(0.33),4dlu",
			// 1   2   3    4   5  6     7   8  9     10 //xyw(2,y,7);
			"4dlu,p:g,4dlu,4dlu,p,30dlu,4dlu,p,30dlu,4dlu");
		PanelBuilder tit = new PanelBuilder(laytit);
		tit.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		tit.setOpaque(false);               
		CellConstraints cctit = new CellConstraints();
		/*********************************/ //  1   2   3  4     5     6   7  8     9   10   11
		FormLayout links = new FormLayout(  "10dlu,2dlu,p,30dlu,10dlu,2dlu,p",
			//// 1 2 3  4    5 6 7	8    9 10 11
				"p,p,p,10dlu,p,p,p,10dlu,p, p,p");
		PanelBuilder titl = new PanelBuilder(links);
		titl.getPanel().setOpaque(false);
		CellConstraints cctitl = new CellConstraints();
		// hier dazwischen den ganzen linken Schei�
		labsheadline[0] = new JLabel(headlineneu[0]);
		labsheadline[0].setFont(fontarialfett);
		titl.add(labsheadline[0],cctitl.xyw(1,1,7,CellConstraints.FILL,CellConstraints.TOP));
		/****/
		JLabel lab = getLabel("125");
		lab.setForeground(Color.RED);
		titl.add(lab,cctitl.xy(1,2));
		eltern.bchb[0] = new JRtaCheckBox("");
		eltern.bchb[0].setName("F_125");
		titl.add(eltern.bchb[0],cctitl.xy(1,3));
		FormLayout labs = new FormLayout("p","p,p,p");
		PanelBuilder ltitl = new PanelBuilder(labs);
		ltitl.getPanel().setOpaque(false);
		CellConstraints cclabs = new CellConstraints();

		ltitl.add( (labstitel[0][0] = getLabel(stitelneu[0][0])),cclabs.xy(1, 1));
		ltitl.add((labstitel[0][1] = getLabel(stitelneu[0][1])),cclabs.xy(1, 2));
		titl.add(ltitl.getPanel(),cctitl.xy(3,3,CellConstraints.DEFAULT,CellConstraints.CENTER));
		/***/
		lab = getLabel("126");
		lab.setForeground(Color.RED);
		titl.add(lab,cctitl.xy(5,2));
		eltern.bchb[1] = new JRtaCheckBox("");
		eltern.bchb[1].setName("F_126");
		titl.add(eltern.bchb[1],cctitl.xy(5,3));
		labs = new FormLayout("p","p,p,p");
		ltitl = new PanelBuilder(labs);
		ltitl.getPanel().setOpaque(false);
		cclabs = new CellConstraints();
		ltitl.add( (labstitel[1][0] = getLabel(stitelneu[1][0])),cclabs.xy(1, 1));
		ltitl.add((labstitel[1][1] = getLabel(stitelneu[1][1])),cclabs.xy(1, 2));
		//ltitl.add(getLabel("Neue Zeile"),cclabs.xy(1, 3));
		//titl.add(ltitl.getPanel(),cctitl.xywh(7,1,1,3,CellConstraints.DEFAULT,CellConstraints.BOTTOM));
		titl.add(ltitl.getPanel(),cctitl.xy(7,3,CellConstraints.DEFAULT,CellConstraints.CENTER));
		/***/
		lab = getLabel("131");
		lab.setForeground(Color.RED);
		titl.add(lab,cctitl.xy(1,5));
		eltern.bchb[2] = new JRtaCheckBox("");
		eltern.bchb[2].setName("F_131");
		titl.add(eltern.bchb[2],cctitl.xy(1,6));
		labs = new FormLayout("p","p,p,p");
		ltitl = new PanelBuilder(labs);
		ltitl.getPanel().setOpaque(false);
		cclabs = new CellConstraints();
		ltitl.add((labstitel[2][0] = getLabel(stitelneu[2][0])),cclabs.xy(1, 1));
		titl.add(ltitl.getPanel(),cctitl.xy(3,6,CellConstraints.DEFAULT,CellConstraints.CENTER));
		/***/
		lab = getLabel("132");
		lab.setForeground(Color.RED);
		titl.add(lab,cctitl.xy(5,5));
		eltern.bchb[3] = new JRtaCheckBox("");
		eltern.bchb[3].setName("F_132");
		titl.add(eltern.bchb[3],cctitl.xy(5,6));
		labs = new FormLayout("p","p,p,p");
		ltitl = new PanelBuilder(labs);
		ltitl.getPanel().setOpaque(false);
		cclabs = new CellConstraints();
		ltitl.add( (labstitel[3][0] = getLabel(stitelneu[3][0])),cclabs.xy(1, 1));
		titl.add(ltitl.getPanel(),cctitl.xy(7,6,CellConstraints.DEFAULT,CellConstraints.CENTER));
		/***/
		lab = getLabel("137");
		lab.setForeground(Color.RED);
		titl.add(lab,cctitl.xy(1,9));
		eltern.bchb[4] = new JRtaCheckBox("");
		eltern.bchb[4].setName("F_137");
		titl.add(eltern.bchb[4],cctitl.xy(1,10));
		labs = new FormLayout("p","p,p,p");
		ltitl = new PanelBuilder(labs);
		ltitl.getPanel().setOpaque(false);
		cclabs = new CellConstraints();
		ltitl.add((labstitel[4][0] = getLabel(stitelneu[4][0])),cclabs.xy(1, 1));
		ltitl.add((labstitel[4][1] = getLabel(stitelneu[4][1])),cclabs.xy(1, 2));
		titl.add(ltitl.getPanel(),cctitl.xy(3,10,CellConstraints.DEFAULT,CellConstraints.CENTER));
		/***/
		lab = getLabel("138");
		lab.setForeground(Color.RED);
		titl.add(lab,cctitl.xy(5,9));
		eltern.bchb[5] = new JRtaCheckBox("");
		eltern.bchb[5].setName("F_138");
		titl.add(eltern.bchb[5],cctitl.xy(5,10));
		labs = new FormLayout("p","p,p,p");
		ltitl = new PanelBuilder(labs);
		ltitl.getPanel().setOpaque(false);
		cclabs = new CellConstraints();
		ltitl.add((labstitel[5][0] = getLabel(stitelneu[5][0])),cclabs.xy(1, 1));
		ltitl.add((labstitel[5][1] = getLabel(stitelneu[5][1])),cclabs.xy(1, 2));
		ltitl.add((labstitel[5][2] = getLabel(stitelneu[5][2])),cclabs.xy(1, 3));
		//titl.add(ltitl.getPanel(),cctitl.xy(3,10,CellConstraints.DEFAULT,CellConstraints.CENTER));
		titl.add(ltitl.getPanel(),cctitl.xywh(7,9,1,3,CellConstraints.DEFAULT,CellConstraints.BOTTOM));	
		/***************/
		titl.getPanel().validate();
		tit.add(titl.getPanel(),cctit.xy(2,2,CellConstraints.FILL,CellConstraints.FILL));
		/*********************************/
		/*
		 * Hochkanntstriche
		 */
		JXPanel rand = new JXPanel();
		rand.setOpaque(false);
		rand.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		tit.add(rand,cctit.xy(3,2,CellConstraints.FILL,CellConstraints.FILL));
		/*********************************/
		/*********************************/	
		FormLayout mitte = new FormLayout(  "10dlu,2dlu,p,30dlu,10dlu,2dlu,p",
				//// 1 2 3  4    5 6 7	8    9 10 11
					"p,p,p,10dlu,p,p,p,10dlu,p, p,p");
		PanelBuilder titm = new PanelBuilder(mitte);
		titm.getPanel().setOpaque(false);
		//CellConstraints cctitm = new CellConstraints();
		// hier dazwischen den ganzen mittleren Schei�
		labsheadline[1] = new JLabel(headlineneu[1]);
		labsheadline[1].setFont(fontarialfett);
		titm.add(labsheadline[1],cctitl.xyw(1,1,7,CellConstraints.FILL,CellConstraints.TOP));
		/****/
		lab = getLabel("127");
		lab.setForeground(Color.RED);
		titm.add(lab,cctitl.xy(1,2));
		eltern.bchb[6] = new JRtaCheckBox("");
		eltern.bchb[6].setName("F_127");
		titm.add(eltern.bchb[6],cctitl.xy(1,3));
		labs = new FormLayout("p","p,p,p");
		ltitl = new PanelBuilder(labs);
		ltitl.getPanel().setOpaque(false);
		cclabs = new CellConstraints();
		ltitl.add((labstitel[6][0] = getLabel(stitelneu[6][0])),cclabs.xy(1, 1));
		ltitl.add((labstitel[6][1] = getLabel(stitelneu[6][1])),cclabs.xy(1, 2));
		titm.add(ltitl.getPanel(),cctitl.xy(3,3,CellConstraints.DEFAULT,CellConstraints.CENTER));
		/***/
		lab = getLabel("128");
		lab.setForeground(Color.RED);
		titm.add(lab,cctitl.xy(5,2));
		eltern.bchb[7] = new JRtaCheckBox("");
		eltern.bchb[7].setName("F_128");
		titm.add(eltern.bchb[7],cctitl.xy(5,3));
		labs = new FormLayout("p","p,p,p");
		ltitl = new PanelBuilder(labs);
		ltitl.getPanel().setOpaque(false);
		cclabs = new CellConstraints();
		ltitl.add((labstitel[7][0] = getLabel(stitelneu[7][0])),cclabs.xy(1, 1));
		titm.add(ltitl.getPanel(),cctitl.xy(7,3,CellConstraints.DEFAULT,CellConstraints.CENTER));
		/***/
		lab = getLabel("133");
		lab.setForeground(Color.RED);
		titm.add(lab,cctitl.xy(1,5));
		eltern.bchb[8] = new JRtaCheckBox("");
		eltern.bchb[8].setName("F_133");
		titm.add(eltern.bchb[8],cctitl.xy(1,6));
		labs = new FormLayout("p","p,p,p");
		ltitl = new PanelBuilder(labs);
		ltitl.getPanel().setOpaque(false);
		cclabs = new CellConstraints();
		ltitl.add((labstitel[8][0] = getLabel(stitelneu[8][0])),cclabs.xy(1, 1));
		titm.add(ltitl.getPanel(),cctitl.xy(3,6,CellConstraints.DEFAULT,CellConstraints.CENTER));
		/***/
		lab = getLabel("134");
		lab.setForeground(Color.RED);
		titm.add(lab,cctitl.xy(5,5));
		eltern.bchb[9] = new JRtaCheckBox("");
		eltern.bchb[9].setName("F_134");
		titm.add(eltern.bchb[9],cctitl.xy(5,6));
		labs = new FormLayout("p","p,p,p");
		ltitl = new PanelBuilder(labs);
		ltitl.getPanel().setOpaque(false);
		cclabs = new CellConstraints();
		ltitl.add((labstitel[9][0] = getLabel(stitelneu[9][0])),cclabs.xy(1, 1));
		titm.add(ltitl.getPanel(),cctitl.xy(7,6,CellConstraints.DEFAULT,CellConstraints.CENTER));
		/***/
		lab = getLabel("139");
		lab.setForeground(Color.RED);
		titm.add(lab,cctitl.xy(1,9));
		eltern.bchb[10] = new JRtaCheckBox("");
		eltern.bchb[10].setName("F_139");
		titm.add(eltern.bchb[10],cctitl.xy(1,10));
		labs = new FormLayout("p","p,p,p");
		ltitl = new PanelBuilder(labs);
		ltitl.getPanel().setOpaque(false);
		cclabs = new CellConstraints();
		ltitl.add((labstitel[10][0] = getLabel(stitelneu[10][0])),cclabs.xy(1, 1));
		titm.add(ltitl.getPanel(),cctitl.xy(3,10,CellConstraints.DEFAULT,CellConstraints.CENTER));
		/***/
		lab = getLabel("140");
		lab.setForeground(Color.RED);
		titm.add(lab,cctitl.xy(5,9));
		eltern.bchb[11] = new JRtaCheckBox("");
		eltern.bchb[11].setName("F_140");
		titm.add(eltern.bchb[11],cctitl.xy(5,10));
		labs = new FormLayout("p","p,p,p");
		ltitl = new PanelBuilder(labs);
		ltitl.getPanel().setOpaque(false);
		cclabs = new CellConstraints();
		ltitl.add((labstitel[11][0] = getLabel(stitelneu[11][0])),cclabs.xy(1, 1));
		titm.add(ltitl.getPanel(),cctitl.xy(7,10,CellConstraints.DEFAULT,CellConstraints.CENTER));
		
		tit.add(titm.getPanel(),cctit.xy(5,2,CellConstraints.FILL,CellConstraints.FILL));
		/*********************************/
		/*
		 * Hochkanntstriche
		 */
		rand = new JXPanel();
		rand.setOpaque(false);
		rand.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		tit.add(rand,cctit.xy(6,2,CellConstraints.FILL,CellConstraints.FILL));
		/*********************************/
		/*********************************/	
		FormLayout rechts = new FormLayout(  "10dlu,2dlu,p,20dlu,10dlu,2dlu,p",
				//// 1 2 3  4    5 6 7	8    9 10 11
					"p,p,p,10dlu,p,p,p,10dlu,p, p,p");
		PanelBuilder titr = new PanelBuilder(rechts);
		titr.getPanel().setOpaque(false);
		//CellConstraints cctitr = new CellConstraints();
		labsheadline[2] = new JLabel(headlineneu[2]);
		labsheadline[2].setFont(fontarialfett);
		titr.add(labsheadline[2],cctitl.xyw(1,1,7,CellConstraints.FILL,CellConstraints.TOP));
		/****/
		lab = getLabel("129");
		lab.setForeground(Color.RED);
		titr.add(lab,cctitl.xy(1,2));
		eltern.bchb[12] = new JRtaCheckBox("");
		eltern.bchb[12].setName("F_129");
		titr.add(eltern.bchb[12],cctitl.xy(1,3));
		labs = new FormLayout("p","p,p,p");
		ltitl = new PanelBuilder(labs);
		ltitl.getPanel().setOpaque(false);
		cclabs = new CellConstraints();
		ltitl.add((labstitel[12][0] = getLabel(stitelneu[12][0])),cclabs.xy(1, 1));
		titr.add(ltitl.getPanel(),cctitl.xy(3,3,CellConstraints.DEFAULT,CellConstraints.CENTER));
		/***/
		lab = getLabel("130");
		lab.setForeground(Color.RED);
		titr.add(lab,cctitl.xy(5,2));
		eltern.bchb[13] = new JRtaCheckBox("");
		eltern.bchb[13].setName("F_130");
		titr.add(eltern.bchb[13],cctitl.xy(5,3));
		labs = new FormLayout("p","p,p,p");
		ltitl = new PanelBuilder(labs);
		ltitl.getPanel().setOpaque(false);
		cclabs = new CellConstraints();
		ltitl.add((labstitel[13][0] = getLabel(stitelneu[13][0])),cclabs.xy(1, 1));
		titr.add(ltitl.getPanel(),cctitl.xy(7,3,CellConstraints.DEFAULT,CellConstraints.CENTER));
		/***/
		lab = getLabel("135");
		lab.setForeground(Color.RED);
		titr.add(lab,cctitl.xy(1,5));
		eltern.bchb[14] = new JRtaCheckBox("");
		eltern.bchb[14].setName("F_135");
		titr.add(eltern.bchb[14],cctitl.xy(1,6));
		labs = new FormLayout("p","p,p,p");
		ltitl = new PanelBuilder(labs);
		ltitl.getPanel().setOpaque(false);
		cclabs = new CellConstraints();
		ltitl.add((labstitel[14][0] = getLabel(stitelneu[14][0])),cclabs.xy(1, 1));
		ltitl.add((labstitel[14][1] = getLabel(stitelneu[14][1])),cclabs.xy(1, 2));		
		titr.add(ltitl.getPanel(),cctitl.xy(3,6,CellConstraints.DEFAULT,CellConstraints.CENTER));
		/***/
		lab = getLabel("136");
		lab.setForeground(Color.RED);
		titr.add(lab,cctitl.xy(5,5));
		eltern.bchb[15] = new JRtaCheckBox("");
		eltern.bchb[15].setName("F_136");
		titr.add(eltern.bchb[15],cctitl.xy(5,6));
		labs = new FormLayout("p","p,p,p");
		ltitl = new PanelBuilder(labs);
		ltitl.getPanel().setOpaque(false);
		cclabs = new CellConstraints();
		ltitl.add((labstitel[15][0] = getLabel(stitelneu[15][0])),cclabs.xy(1, 1));
		ltitl.add((labstitel[15][1] = getLabel(stitelneu[15][1])),cclabs.xy(1, 2));
		ltitl.add((labstitel[15][2] = getLabel(stitelneu[15][2])),cclabs.xy(1, 3));	
		//titr.add(ltitl.getPanel(),cctitl.xy(7,6,CellConstraints.DEFAULT,CellConstraints.CENTER));
		titr.add(ltitl.getPanel(),cctitl.xywh(7,5,1,3,CellConstraints.DEFAULT,CellConstraints.BOTTOM));
		/***/
		lab = getLabel("141");
		lab.setForeground(Color.RED);
		titr.add(lab,cctitl.xy(1,9));
		eltern.bchb[16] = new JRtaCheckBox("");
		eltern.bchb[16].setName("F_141");
		titr.add(eltern.bchb[16],cctitl.xy(1,10));
		labs = new FormLayout("p","p,p,p");
		ltitl = new PanelBuilder(labs);
		ltitl.getPanel().setOpaque(false);
		cclabs = new CellConstraints();
		ltitl.add((labstitel[16][0] = getLabel(stitelneu[16][0])),cclabs.xy(1, 1));
		ltitl.add((labstitel[16][1] = getLabel(stitelneu[16][1])),cclabs.xy(1, 2));
		titr.add(ltitl.getPanel(),cctitl.xy(3,10,CellConstraints.DEFAULT,CellConstraints.CENTER));
		/***/
		titr.getPanel().validate();
		tit.add(titr.getPanel(),cctit.xy(8,2,CellConstraints.FILL,CellConstraints.FILL));
		// 1  2   3    4   5  6     7   8  9     10 //xyw(2,y,7);	
	// "4dlu,p:g,4dlu,4dlu,p,30dlu,4dlu,p,30dlu,4dlu");
		lab = getLabel("ERLÄUTERUNGEN");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xyw(2,5,3));
		eltern.bta[5] = new JTextArea();
		eltern.bta[5].setName("ERLAEUT");
		tit.add(eltern.bta[5],cctit.xyw(2,6,7,CellConstraints.FILL,CellConstraints.FILL));

		lab = getLabel("LETZTE MEDIKATION");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xyw(2,8,3));
		eltern.bta[6] = new JTextArea();
		eltern.bta[6].setName("LMEDIKAT");
		eltern.bta[6].setEnabled(false);
		eltern.bta[6].setText("Bei Entlassdatum > 31.12.2007 ungültig");
		tit.add(eltern.bta[6],cctit.xyw(2,9,7,CellConstraints.FILL,CellConstraints.FILL));

		tit.getPanel().validate();
		return tit.getPanel();
	}	
	
	private JPanel getBlock5Komplett(){
		FormLayout laytit = new FormLayout("0dlu,p,4dlu,p",
				"4dlu,p,0dlu");
		PanelBuilder tit = new PanelBuilder(laytit);
		//tit.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		tit.setOpaque(false);               
		CellConstraints cctit = new CellConstraints();
		JLabel lab = new JLabel("Vorschläge für nachfolgende Maßnahmen");
		lab.setFont(fontarialfett);
		tit.add(lab,cctit.xy(2,2));
		lab = new JLabel("Zutreffendes bitte ankreuzen (X), Mehrfachnennungen sind möglich");
		lab.setFont(fontarialnormal);
		tit.add(lab,cctit.xy(4,2));
		tit.getPanel().validate();
		return tit.getPanel();
	}
	private JPanel getBlock4Komplett(){
		//insgesamt 610                      1    2     3   4     5      6    7   8   9     10   11  12  13    14   15   16  17
		FormLayout laytit = new FormLayout("4dlu,25dlu,2dlu,p,  35dlu, 25dlu,2dlu,p, 20dlu,25dlu,2dlu,p,20dlu,25dlu,2dlu,p:g,20dlu",
				//           dia1            dia2            dia3             dia4            dia5
				// 1  2   3      4     5    6     7    8  
				"4dlu,p, 5dlu,   p,  5dlu,  p, 0dlu:g,4dlu");
				//"4dlu,p, 5dlu,   p,  5dlu,  p, 0dlu:g, 2dlu, p, 30dlu, 2dlu, p,  30dlu, 2dlu, p ,30dlu,4dlu");
		PanelBuilder tit = new PanelBuilder(laytit);
		//tit.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		tit.setOpaque(false);               
		CellConstraints cctit = new CellConstraints();
		tit.getPanel().validate();
		eltern.btf[22] = new JRtaTextField("ZAHLEN",false);
		eltern.btf[22].setName("F_114");
		tit.add(eltern.btf[22],cctit.xy(2,2));
		FormLayout entlay = new FormLayout("55dlu","p,p");
		PanelBuilder ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		CellConstraints ccent = new CellConstraints();
		JLabel lab = getLabel ("Aufnahmegewicht");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 1));
		lab = getLabel("(ganze kg)");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		tit.add(ent.getPanel(),cctit.xy(4,2,CellConstraints.FILL,CellConstraints.TOP));		
		
		
		eltern.btf[23] = new JRtaTextField("ZAHLEN",false);
		eltern.btf[23].setName("F_117");		
		tit.add(eltern.btf[23],cctit.xy(2,4));
		entlay = new FormLayout("55dlu","p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel ("Entlassungsgewicht");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 1));
		lab = getLabel("(ganze kg)");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		tit.add(ent.getPanel(),cctit.xy(4,4,CellConstraints.FILL,CellConstraints.TOP));
		
		
		eltern.btf[24] = new JRtaTextField("ZAHLEN",false);
		tit.add(eltern.btf[24],cctit.xy(2,6));
		eltern.btf[24].setName("F_120");
		entlay = new FormLayout("55dlu","p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel ("Körpergröße");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 1));
		lab = getLabel("(ganze cm)");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		tit.add(ent.getPanel(),cctit.xy(4,6,CellConstraints.FILL,CellConstraints.TOP));
		
		eltern.bcmb[17] = new JRtaComboBox(ursache);
		eltern.bcmb[17].setName("F_123");
		tit.add(eltern.bcmb[17],cctit.xy(6,2));
		//                                  1 2 3 4 5 6 7 8 9 10 11 12 
		entlay = new FormLayout("p,2dlu,p","p,p,p,p,p,p,p,p,p,p, p, p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel ("URSACHE DER");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xyw(1, 1,3));
		lab = getLabel("ERKRANKUNG");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xyw(1, 2,3));
		ent.add(getLabel("(1.Diagnose)"),ccent.xyw(1,3,3 ));
		ent.add(getLabel("0 = "),ccent.xy(1,4 ));
		ent.add(getLabel("1 - 4 trifft nicht zu"),ccent.xy(3,4,CellConstraints.LEFT,CellConstraints.DEFAULT ));
		ent.add(getLabel("1 = "),ccent.xy(1,5 ));
		ent.add(getLabel("Arbeitsunfall einschl."),ccent.xy(3,5,CellConstraints.LEFT,CellConstraints.DEFAULT ));
		ent.add(getLabel("Wegeunfall"),ccent.xy(3,6,CellConstraints.LEFT,CellConstraints.DEFAULT ));
		ent.add(getLabel("2 = "),ccent.xy(1,7 ));
		ent.add(getLabel("Berufserkrankung"),ccent.xy(3,7,CellConstraints.LEFT,CellConstraints.DEFAULT ));
		ent.add(getLabel("3 = "),ccent.xy(1,8 ));
		ent.add(getLabel("Schädigung durch"),ccent.xy(3,8,CellConstraints.LEFT,CellConstraints.DEFAULT ));
		ent.add(getLabel("Einwirkung Dritter"),ccent.xy(3,9,CellConstraints.LEFT,CellConstraints.DEFAULT ));
		ent.add(getLabel("4 = "),ccent.xy(1,10 ));
		ent.add(getLabel("Folge von Kriegs-, Zivil-"),ccent.xy(3,10,CellConstraints.LEFT,CellConstraints.DEFAULT ));
		ent.add(getLabel("oder Whrdienst"),ccent.xy(3,11,CellConstraints.LEFT,CellConstraints.DEFAULT ));
		ent.add(getLabel("5 = "),ccent.xy(1,12 ));
		ent.add(getLabel("Meldepflichtige Erkrankung"),ccent.xy(3,12,CellConstraints.LEFT,CellConstraints.DEFAULT ));
		ent.getPanel().validate();
		tit.add(ent.getPanel(),cctit.xywh(8,2,1,6,CellConstraints.LEFT,CellConstraints.TOP));
		
		eltern.bcmb[18] = new JRtaComboBox(vorherau);
		eltern.bcmb[18].setName("F_124");
		tit.add(eltern.bcmb[18],cctit.xy(10,2));
		//                                                 1 2 3 4 5 6 7 8  
		entlay = new FormLayout("p,2dlu,fill:0:grow(1.0)","p,p,p,p,p,p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel ("ARBEITSUNFÄHIGKEITSZEITEN");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xyw(1, 1,3));
		ent.add(getLabel("innerhalb der letzten"),ccent.xyw(1,2,3 ));
		ent.add(getLabel("12 Monate vor Aufnahme"),ccent.xyw(1,3,3 ));
		ent.add(getLabel("0 = "),ccent.xy(1,4 ));
		ent.add(getLabel("keine"),ccent.xy(3,4,CellConstraints.LEFT,CellConstraints.DEFAULT ));
		ent.add(getLabel("1 = "),ccent.xy(1,5 ));
		ent.add(getLabel("bis unter 3 Monate"),ccent.xy(3,5,CellConstraints.LEFT,CellConstraints.DEFAULT ));
		ent.add(getLabel("2 = "),ccent.xy(1,6 ));
		ent.add(getLabel("3 bis unter 6 Monate"),ccent.xy(3,6,CellConstraints.LEFT,CellConstraints.DEFAULT ));
		ent.add(getLabel("3 = "),ccent.xy(1,7 ));
		ent.add(getLabel("6 und mehr Monate"),ccent.xy(3,7,CellConstraints.LEFT,CellConstraints.DEFAULT ));
		ent.add(getLabel("9 = "),ccent.xy(1,8 ));
		ent.add(getLabel("nicht erwerbstätig"),ccent.xy(3,8,CellConstraints.LEFT,CellConstraints.DEFAULT ));
		ent.getPanel().validate();
		tit.add(ent.getPanel(),cctit.xywh(12,2,1,6,CellConstraints.LEFT,CellConstraints.TOP));		
		
		eltern.bcmb[19] = new JRtaComboBox(dmp);
		eltern.bcmb[19].setName("DMP");
		tit.add(eltern.bcmb[19],cctit.xy(14,2));
		//                                                 1 2 3 4 5 6 7 8 9 
		entlay = new FormLayout("p,2dlu,fill:0:grow(1.0)","p,p,p,p,p,p,p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel ("DMP-Patient");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xyw(1, 1,3));
		ent.add(getLabel("0 = "),ccent.xy(1,2 ));
		ent.add(getLabel("kein DMP-Patient"),ccent.xy(3,2,CellConstraints.LEFT,CellConstraints.DEFAULT ));
		ent.add(getLabel("1 = "),ccent.xy(1,3 ));
		ent.add(getLabel("Diabetes mellitus Typ 1"),ccent.xy(3,3,CellConstraints.LEFT,CellConstraints.DEFAULT ));
		ent.add(getLabel("2 = "),ccent.xy(1,4 ));
		ent.add(getLabel("Diabetes mellitus Typ 2"),ccent.xy(3,4,CellConstraints.LEFT,CellConstraints.DEFAULT ));
		ent.add(getLabel("3 = "),ccent.xy(1,5 ));
		ent.add(getLabel("Brustkrebs"),ccent.xy(3,5,CellConstraints.LEFT,CellConstraints.DEFAULT ));
		ent.add(getLabel("4 = "),ccent.xy(1,6 ));
		ent.add(getLabel("KHK"),ccent.xy(3,6,CellConstraints.LEFT,CellConstraints.DEFAULT ));
		ent.add(getLabel("5 = "),ccent.xy(1,7 ));
		ent.add(getLabel("Asthma bronchiale / COPD"),ccent.xy(3,7,CellConstraints.LEFT,CellConstraints.DEFAULT ));
		ent.add(getLabel("6 = "),ccent.xy(1,8 ));
		ent.add(getLabel("mehrere DMP"),ccent.xy(3,8,CellConstraints.LEFT,CellConstraints.DEFAULT ));
		ent.add(getLabel("7 = "),ccent.xy(1,9 ));
		ent.add(getLabel("andere DMP"),ccent.xy(3,9,CellConstraints.LEFT,CellConstraints.DEFAULT ));
		ent.getPanel().validate();
		tit.add(ent.getPanel(),cctit.xywh(16,2,1,6,CellConstraints.FILL,CellConstraints.TOP));
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
		eltern.btf[17] = new JRtaTextField("GROSS",false);
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
		eltern.btf[18] = new JRtaTextField("GROSS",false);
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
		eltern.bcmb[7].setName("F_89");
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
		eltern.btf[19] = new JRtaTextField("GROSS",false);
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
		eltern.btf[20] = new JRtaTextField("GROSS",false);
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
		eltern.bta[4].setName("DIAG5");
		tit.add(eltern.bta[4],cctit.xy(2, 16,CellConstraints.FILL,CellConstraints.FILL));

		entlay = new FormLayout("55dlu","p,p,p");
		ent = new PanelBuilder(entlay);
		ent.getPanel().setOpaque(false);
		ccent = new CellConstraints();
		lab = getLabel("106");
		lab.setForeground(Color.RED);
		ent.add(lab,ccent.xy(1, 2));
		eltern.btf[21] = new JRtaTextField("GROSS",false);
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
		tit.add(eltern.btf[13],cctit.xy(2,7));
		tit.add(eltern.btf[14],cctit.xy(4,7));
		tit.add(eltern.btf[15],cctit.xy(2,5));
		tit.add(eltern.btf[16],cctit.xy(4,5));
		lab = getLabel("stationär");
		lab.setForeground(Color.RED);
		tit.add(lab,cctit.xy(6,3));
		lab = getLabel("ganztägig ambulant");
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
	/***********************************************/
	private JXPanel getTitel(){
		JXPanel tit = new JXPanel();
		tit.setOpaque(false);
		FormLayout laytit = new FormLayout("p,33dlu,p","2dlu,p");
		CellConstraints cctit = new CellConstraints(); 
		tit.setLayout(laytit);

		titel = new JLabel("Reha-Entlassbericht");
		titel.setFont(fontgross);
		titel.setForeground(Color.BLUE);
		tit.add(titel,cctit.xy(1, 2));
		/*
		titel = new JLabel("Reha-Entlassbericht");
		titel.setFont(fontgross);
		titel.setForeground(Color.BLUE);
		*/
		tit.add(eltern.cbktraeger,cctit.xy(3, 2));
		eltern.cbktraeger.setActionCommand("empfaenger");
		eltern.cbktraeger.addActionListener(this);
		
		tit.validate();
		return tit;
	}
	private JLabel getLabel(String text){
		JLabel lab = new JLabel(text);
		lab.setFont(fontklein);
		return lab;
	}
	private JPanel getRand(Color col){
		JPanel pan = new JPanel();
		pan.setOpaque(false);
		pan.setBorder(BorderFactory.createLineBorder(col));
		return pan;
	}
	class JLabelX extends JLabel{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public JLabelX(String text){
			super();
			setFont(fontklein);
			setVisible(true);
		}
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(inGuiInit){
			return;
		}
		String cmd = arg0.getActionCommand();
		if(cmd.equals("empfaenger")){
			testeIK();
			if( ((String)eltern.cbktraeger.getSelectedItem()).contains("GKV") ){
				//System.out.println("In alte Labels");
				setzeNeueLabels(false);
				eltern.ebt.getTab1().pan.revalidate();
				eltern.bcmb[19].setEnabled(false);
				eltern.bta[6].setEnabled(true);
			}else{
				// Hier noch das RV - Entlassdatum einbauen ggfls. ebenfalls alte labels zeigen
				if(eltern.btf[15].getText().length()==10){
					if(DatFunk.TageDifferenz("31.12.2007",eltern.btf[15].getText() ) < 0){
						setzeNeueLabels(false);
					}else{
						setzeNeueLabels(true);
					}
				}else{
					setzeNeueLabels(true);
				}
				eltern.ebt.getTab1().pan.revalidate();
				eltern.bcmb[19].setEnabled(true);
				eltern.bta[6].setEnabled(false);				
			}
		}
	}
	public void testeObAlt(){
		if( ((String)eltern.cbktraeger.getSelectedItem()).contains("GKV") ){
			System.out.println("In alte Labels");
			setzeNeueLabels(false);
			eltern.ebt.getTab1().pan.revalidate();
			eltern.bcmb[19].setEnabled(false);
			eltern.bta[6].setEnabled(true);
		}else{
			// Hier noch das RV - Entlassdatum einbauen ggfls. ebenfalls alte labels zeigen
			if(eltern.btf[15].getText().length()==10){
				try{
					if(DatFunk.TageDifferenz("31.12.2007",eltern.btf[15].getText() ) < 0){
						setzeNeueLabels(false);
					}else{
						setzeNeueLabels(true);
					}
				}catch(Exception ex){
					setzeNeueLabels(true);
				}
			}else{
				setzeNeueLabels(true);
			}
			eltern.ebt.getTab1().pan.revalidate();
			eltern.bcmb[19].setEnabled(true);
			eltern.bta[6].setEnabled(false);				
		}
	}
	public void testeIK(){
		//System.out.println(SystemConfig.vGutachtenAbsAdresse);
		eltern.btf[28].setText(SystemConfig.vGutachtenIK.get(eltern.cbktraeger.getSelectedIndex()));		
		eltern.btf[28].validate();
		eltern.btf[28].repaint();
	}
	public void setzeNeueLabels(boolean neu){
		//System.out.println("Setze neue Labels");
		for(int i = 0; i < 17; i++){
			for(int t = 0; t < labstitel[i].length; t++){
				if(neu){
					if(t < 3){labsheadline[t].setText(headlineneu[t]);}
					labstitel[i][t].setText(stitelneu[i][t]);
					if(t == 16){  ((JComponent)labstitel[i][t].getParent()).revalidate() ; }
				}else{
					if(t < 3){labsheadline[t].setText(headlinealt[t]);}
					labstitel[i][t].setText(stitelalt[i][t]);
					if(t == 16){  ((JComponent)labstitel[i][t].getParent()).revalidate() ; }					
				}
			}
		}
	}
	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentResized(ComponentEvent e) {
		refreshSize();
	}
	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	public final void refreshSize() {
		if(jscr==null){
			return;
		}
		eltern.ebtab.revalidate();
		//System.out.println("Aufruf refreshSize in EB1");
		jscr.validate();
		pan.revalidate();
		//pan.setVisible(true);
		// ... and just in case, call validate() on the top-level window as well
		final Window window1 = SwingUtilities.getWindowAncestor(jscr);
		if (window1 != null) {
		window1.validate();
		}
	}	
}

