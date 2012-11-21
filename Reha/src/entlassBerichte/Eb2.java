package entlassBerichte;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;


import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;

import CommonTools.JCompTools;
import CommonTools.JRtaCheckBox;
import CommonTools.JRtaTextField;
import systemTools.SetMaxText;

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
	Font fontarialfettgross = null;
	JScrollPane jscr = null;
	Vector<String> ktl = new Vector<String>();
	String[] sktl = null;
	public Eb2(EBerichtPanel xeltern){
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
				pan.add(constructSeite(),BorderLayout.CENTER);
				for(int i = 25; i < 27;i++){
					eltern.btf[i].setFont(fontcourier);
					eltern.btf[i].setForeground(Color.BLUE);
				}

				eltern.bta[7].setFont(fontcourier);
				eltern.bta[7].setForeground(Color.BLUE);
				eltern.bta[7].setWrapStyleWord(true);
				eltern.bta[7].setLineWrap(true);
				eltern.bta[7].setBorder(BorderFactory.createLineBorder(Color.BLACK));
				
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
										eltern.meldeInitOk(1);
										}catch(Exception ex){
											ex.printStackTrace();
										}
										return null;
									}
					 				
					 			}.execute();
					 		}else{
					 			eltern.meldeInitOk(1);
					 		}
				 	   }
				}); 
				}catch(Exception ex){
					ex.printStackTrace();
				}

				return null;
			}
			
		}.execute();
	}
	private void laden(){
		String berichtid = Integer.toString(eltern.berichtid);
		StringBuffer buf = new StringBuffer();
		buf.append("select ");
		for(int i = 25; i < 27;i++){
			buf.append(eltern.btf[i].getName()+",");
		}
		for(int i = 17; i < 44;i++){
			buf.append(eltern.bchb[i].getName()+",");
		}
		buf.append(eltern.bta[7].getName()+",");
		buf.append("UNTDAT from bericht2 where berichtid='"+berichtid+"'");
		holeSatz(buf);
	}
	private void holeSatz(StringBuffer buf){
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			
			rs = stmt.executeQuery(buf.toString());
			String test = "";
			if(rs.next()){
				eltern.bta[7].setText( (rs.getString(eltern.bta[7].getName())==null  ? "" :  rs.getString(eltern.bta[7].getName())) ) ;
				for(int i = 25; i < 27;i++){
						eltern.btf[i].setText( (rs.getString(eltern.btf[i].getName())==null  ? "" :  rs.getString(eltern.btf[i].getName()))  );
				}
				for(int i = 17; i < 44;i++){
					test = ( rs.getString(eltern.bchb[i].getName())==null ? "0" : rs.getString(eltern.bchb[i].getName())); 
					//eltern.bchb[i].setSelected( ( rs.getString(eltern.bchb[i].getName()).equals("1") ? true : false) );
					eltern.bchb[i].setSelected( test.equals("1") ? true : false );
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
					rs = null;
				} catch (SQLException sqlEx) { // ignore }
					rs = null;
				}
			}	
			if (stmt != null) {
				try {
					stmt.close();
					stmt = null;
				} catch (SQLException sqlEx) { // ignore }
					stmt = null;
				}
			}
		}
		
	}
	
		
	public JXPanel getSeite(){
		return pan;
	}
	public JScrollPane constructSeite(){
		FormLayout lay = new FormLayout("5dlu,fill:0:grow(0.25),20dlu,p,fill:0:grow(0.25),5dlu",
				
				//   2=titel   4=block1 6=block2 8=block3
				// 1    2   3    4   5    6    7  8   9  11   12  13 14
				"20dlu, p ,2dlu, p,  p , 20dlu,  p,0dlu,p ");
		PanelBuilder pb = new PanelBuilder(lay);
		pb.setOpaque(false);
		CellConstraints cc = new CellConstraints();
		//pb.addLabel("Das ist ein Label",cc.xy(2,2));
		pb.add(getTitel1(),cc.xy(4,2,CellConstraints.FILL,CellConstraints.CENTER));
		pb.add(getTitel2(),cc.xyw(3,4,2,CellConstraints.LEFT,CellConstraints.CENTER));
		pb.add(getKasten1(),cc.xy(4,5,CellConstraints.RIGHT,CellConstraints.FILL));
		pb.add(getTitel3(),cc.xyw(3,7,2,CellConstraints.LEFT,CellConstraints.BOTTOM));
		pb.add(getGesamtLeistBild(),cc.xyw(3,9,2,CellConstraints.FILL,CellConstraints.TOP));
		//pb.add(getBezeichnung(),cc.xy(4,5,CellConstraints.RIGHT,CellConstraints.FILL));
		//pb.add(getBeurteilung1(),cc.xy(4,6,CellConstraints.RIGHT,CellConstraints.FILL));
		
		jscr = JCompTools.getTransparentScrollPane(pb.getPanel());
		jscr.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
		jscr.getVerticalScrollBar().setUnitIncrement(15);
		jscr.validate();
		return jscr;
	}
	private JPanel getGesamtLeistBild(){
		FormLayout layleibild = new FormLayout("20dlu,p:g",
//		 1 2 3 4 5	  6   7  8	9  10  11	12	13
		"p,p,p,p,2dlu,p,2dlu,p,2dlu,p,10dlu,p,20dlu");
		PanelBuilder leibild = new PanelBuilder(layleibild);
		//tit.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		leibild.setOpaque(false);               
		CellConstraints cclei = new CellConstraints();
		JLabel lab = new JLabel("1.");
		lab.setFont(fontarialnormal);
		leibild.add(lab,cclei.xy(1, 1,CellConstraints.DEFAULT,CellConstraints.TOP));
		leibild.add(leiBild1(),cclei.xy(2, 1,CellConstraints.FILL,CellConstraints.TOP));
		leibild.add(leiBild2(),cclei.xy(2, 2,CellConstraints.FILL,CellConstraints.TOP));
		leibild.add(leiBild3(),cclei.xy(2, 3,CellConstraints.FILL,CellConstraints.TOP));
		leibild.add(leiBild4(),cclei.xy(2, 4,CellConstraints.FILL,CellConstraints.TOP));
		leibild.add(getLRStriche(),cclei.xy(2,5,CellConstraints.FILL,CellConstraints.TOP));
		lab = new JLabel("2.");
		lab.setFont(fontarialnormal);
		leibild.add(lab,cclei.xy(1, 6,CellConstraints.DEFAULT,CellConstraints.TOP));
		leibild.add(leiBild5(),cclei.xy(2, 6,CellConstraints.FILL,CellConstraints.TOP));
		leibild.add(getLRStriche(),cclei.xy(2,7,CellConstraints.FILL,CellConstraints.TOP));
		lab = new JLabel("3.");
		lab.setFont(fontarialnormal);
		leibild.add(lab,cclei.xy(1, 8,CellConstraints.DEFAULT,CellConstraints.TOP));
		leibild.add(leiBild6(),cclei.xy(2, 8,CellConstraints.FILL,CellConstraints.TOP));
		leibild.add(getLRStriche(),cclei.xy(2,9,CellConstraints.FILL,CellConstraints.TOP));
		lab = new JLabel("4.");
		lab.setFont(fontarialnormal);
		leibild.add(lab,cclei.xy(1, 10,CellConstraints.DEFAULT,CellConstraints.TOP));
		leibild.add(leiBild7(),cclei.xy(2, 10,CellConstraints.FILL,CellConstraints.TOP));
		//leibild.add(leiBild8(),cclei.xy(2, 12,CellConstraints.FILL,CellConstraints.TOP));
		leibild.getPanel().validate();
		return leibild.getPanel();
	}
	/*
	private JPanel leiBild8(){
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
		dum.add(eltern.barzttf[0],ccdum.xy(1, 1));
		eltern.barzttf[1] = new JRtaTextField("nix",false);
		eltern.barzttf[1].setName("ARZT2");
		dum.add(eltern.barzttf[1],ccdum.xy(3, 1));
		eltern.barzttf[2] = new JRtaTextField("nix",false);
		eltern.barzttf[2].setName("ARZT3");
		dum.add(eltern.barzttf[2],ccdum.xy(5, 1));
		dum.add(getRand(Color.BLACK),ccdum.xy(1,3));
		dum.add(getRand(Color.BLACK),ccdum.xy(3,3));
		dum.add(getRand(Color.BLACK),ccdum.xy(5,3));
		dum.add(getLabel("Ltd. �rztin / ltd. Arzt"),ccdum.xy(1,5));
		dum.add(getLabel("Ober�rztin / Oberarzt"),ccdum.xy(3,5));
		dum.add(getLabel("Stat.-�rztin / Stat.-Arzt"),ccdum.xy(5,5));
		dum.getPanel().validate();
		lei1.add(dum.getPanel(),ccl1.xyw(1, 3,2,CellConstraints.FILL,CellConstraints.DEFAULT));
		
		lei1.getPanel().validate();
		return lei1.getPanel();
		
	}
	*/
	private JPanel leiBild7(){			//	1      2              3
		FormLayout laytit = new FormLayout("1px,fill:0:grow(1.0),1px",
	//    1  1  2    4			
		"1px,p,2dlu,1px");
		PanelBuilder tit = new PanelBuilder(laytit);
		//Border bd = BorderFactory.createLineBorder(Color.BLACK);
		//tit.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		tit.setOpaque(false);
		CellConstraints cctit = new CellConstraints();

		tit.add(getRand(Color.BLACK),cctit.xywh(1, 1, 1, 4));
		tit.add(getRand(Color.BLACK),cctit.xywh(3, 1, 1, 4));
		tit.add(getRand(Color.BLACK),cctit.xywh(1, 4,3,1));		

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
		plinks.add(getLabelKleinRot("Beurteilung des zeitlichen Umfangs, in dem"),ccli.xy(2,2));
		plinks.add(getLabelKleinRot("eine Tätigkeit entsprechend dem positiven"),ccli.xy(2,3));
		plinks.add(getLabelKleinRot("negativen Leitungsb. ausgeübt werden kann"),ccli.xy(2,4));
		plinks.add(getRand(Color.GRAY),ccli.xywh(4, 1, 1, 5));
		plinks.add(getLabelKleinRot("178"),ccli.xywh(6,2,1,2,CellConstraints.DEFAULT,CellConstraints.CENTER));
		eltern.bchb[41] = new JRtaCheckBox("");
		eltern.bchb[41].setName("F_178");
		plinks.add(eltern.bchb[41],ccli.xywh(6, 3,1, 2,CellConstraints.DEFAULT,CellConstraints.BOTTOM));
		plinks.add(getLabel("6 Stunden und mehr"),ccli.xywh(8, 3,1, 2,CellConstraints.DEFAULT,CellConstraints.BOTTOM));
		plinks.add(getLabelKleinRot("179"),ccli.xywh(9,2,1,2,CellConstraints.DEFAULT,CellConstraints.CENTER));
		eltern.bchb[42] = new JRtaCheckBox("");
		eltern.bchb[42].setName("F_179");
		plinks.add(eltern.bchb[42],ccli.xywh(9, 3,1, 2,CellConstraints.DEFAULT,CellConstraints.BOTTOM));
		plinks.add(getLabel("3 bis unter 6 Stunden"),ccli.xywh(11, 3,1, 2,CellConstraints.DEFAULT,CellConstraints.BOTTOM));
		plinks.add(getLabelKleinRot("181"),ccli.xywh(12,2,1,2,CellConstraints.DEFAULT,CellConstraints.CENTER));
		eltern.bchb[43] = new JRtaCheckBox("");
		eltern.bchb[43].setName("F_181");
		plinks.add(eltern.bchb[43],ccli.xywh(12, 3,1, 2,CellConstraints.DEFAULT,CellConstraints.BOTTOM));
		plinks.add(getLabel("unter 3 Stunden"),ccli.xywh(14, 3,1, 2,CellConstraints.DEFAULT,CellConstraints.BOTTOM));

		
		tit.add(plinks.getPanel(),cctit.xy(2,2));		

		return tit.getPanel();
		
		
	}
	private JPanel leiBild6(){        //   1   2     3              4     5
		FormLayout lay1 = new FormLayout("1px,2dlu,fill:0:grow(1.0),2dlu,1px",
			    //           5=F174  7=F175  9=F176   11=F177
				//  1   2   3  4    5     6     7 
				  "1px,0dlu,p,2dlu,150dlu,4dlu,1px");		
		PanelBuilder lei1 = new PanelBuilder(lay1);
		lei1.setOpaque(false); 
		CellConstraints ccl1 = new CellConstraints();

		lei1.add(getRand(Color.BLACK),ccl1.xywh(1, 1, 1, 7));
		lei1.add(getRand(Color.BLACK),ccl1.xywh(5, 1, 1, 7));
		lei1.add(getRand(Color.GRAY),ccl1.xywh(1, 7, 5, 1));

		
		FormLayout dummy = new FormLayout("p,2dlu,p","p");
		PanelBuilder dum = new PanelBuilder(dummy);
		dum.getPanel().setOpaque(false);
		CellConstraints ccdum = new CellConstraints();
		
		JLabel lab = new JLabel("Beschreibung des Leistungsbildes");
		lab.setFont(fontarialfett);
		dum.add(lab,ccdum.xy(1,1));
		dum.add(getLabel("(insbesondere der unter Ziff. 2 genannten Einschränkungen)"),ccdum.xy(3,1,CellConstraints.DEFAULT,CellConstraints.BOTTOM));
		dum.getPanel().validate();
		lei1.add(dum.getPanel(),ccl1.xy(3,3));
		/***********************/
		eltern.bta[7] = new JTextArea();
		eltern.bta[7].setName("LEISTBI");
		lei1.add(eltern.bta[7],ccl1.xy(3,5,CellConstraints.FILL,CellConstraints.FILL));
		lei1.getPanel().validate();
		return lei1.getPanel();
	}

	private JPanel leiBild5(){         //  1   2         3          4      
		FormLayout lay1 = new FormLayout("1px,2dlu,fill:0:grow(1.0),1px",
	    //           5=F174  7=F175  9=F176   11=F177
		//  1   2   3  4   5  6   7  8   9 10  11  12  13
		  "1px,0dlu,p,5dlu,p,5dlu,p,7dlu,p,5dlu,p,4dlu,1px");		
		PanelBuilder lei1 = new PanelBuilder(lay1);
		lei1.setOpaque(false);               
		CellConstraints ccl1 = new CellConstraints();
		lei1.add(getRand(Color.BLACK),ccl1.xywh(1, 1, 1, 13));
		lei1.add(getRand(Color.BLACK),ccl1.xywh(4, 1, 1, 13));
		lei1.add(getRand(Color.GRAY),ccl1.xywh(1, 13, 4, 1));

		/******/
		FormLayout dummy = new FormLayout("p,2dlu,p","p,p");
		PanelBuilder dum = new PanelBuilder(dummy);
		dum.getPanel().setOpaque(false);
		CellConstraints ccdum = new CellConstraints();
		JLabel lab = new JLabel("Negatives Leistungsbild:");
		lab.setFont(fontarialfett);
		dum.add(lab,ccdum.xyw(1, 1,3,CellConstraints.FILL,CellConstraints.DEFAULT));
		lab = new JLabel("Einschränkungen beziehen sich auf:");
		lab.setFont(fontarialnormal);
		dum.add(lab,ccdum.xy(1, 2,CellConstraints.LEFT,CellConstraints.DEFAULT));
		dum.add(getLabel("(Art und Ausmaß müssen differenzeirt unter Ziff. 3 beschrieben werden)"),
				ccdum.xy(3, 2,CellConstraints.LEFT,CellConstraints.BOTTOM));
		dum.getPanel().validate();
		lei1.add(dum.getPanel(),ccl1.xy(3,3,CellConstraints.FILL,CellConstraints.TOP));
		/*******/
		dummy = new FormLayout("p,5dlu,p","p");
		dum = new PanelBuilder(dummy);
		dum.getPanel().setOpaque(false);
		ccdum = new CellConstraints();
		FormLayout dummy2 = new FormLayout("p","p,p");
		PanelBuilder dum2 = new PanelBuilder(dummy2);
		dum2.getPanel().setOpaque(false);
		CellConstraints ccdum2 = new CellConstraints();
		dum2.add(getLabelKleinRot("174"),ccdum2.xy(1,1));
		eltern.bchb[37] = new JRtaCheckBox("");
		eltern.bchb[37].setName("F_174");
		dum2.add(eltern.bchb[37],ccdum2.xy(1,2));
		dum2.getPanel().validate();
		dum.add(dum2.getPanel(),ccdum.xy(1,1,CellConstraints.LEFT,CellConstraints.TOP));
		/**/
		dummy2 = new FormLayout("p","p,p,p");
		dum2 = new PanelBuilder(dummy2);
		dum2.getPanel().setOpaque(false);
		ccdum2 = new CellConstraints();
		lab = new JLabel("geistig/psychische Belastbarkeit");
		lab.setFont(fontarialfett);
		dum2.add(lab,ccdum2.xy(1,1,CellConstraints.LEFT,CellConstraints.TOP));
		dum2.add(getLabel("(Zu beachten sind insbesondere Konzentrations-/Reaktionsvermögen, Umstellungs-,"+
				"Anpassungsvermögen, Verantwortung für Personen"),ccdum2.xy(1,2,CellConstraints.LEFT,CellConstraints.TOP));
		dum2.add(getLabel("und Maschinen, Publikumsverkehr, Überwachung, Steuerung komplexer Arbeitsvorgänge)."),
				ccdum2.xy(1,3,CellConstraints.LEFT,CellConstraints.TOP));
		dum.add(dum2.getPanel(),ccdum.xy(3,1,CellConstraints.LEFT,CellConstraints.CENTER));
		dum.getPanel().validate();
		lei1.add(dum.getPanel(),ccl1.xy(3, 5,CellConstraints.FILL,CellConstraints.TOP));
		/********Ende F174*******************/
		dummy = new FormLayout("p,5dlu,p","p");
		dum = new PanelBuilder(dummy);
		dum.getPanel().setOpaque(false);
		ccdum = new CellConstraints();
		/**/
		dummy2 = new FormLayout("p","p,p");
		dum2 = new PanelBuilder(dummy2);
		dum2.getPanel().setOpaque(false);
		ccdum2 = new CellConstraints();
		dum2.add(getLabelKleinRot("175"),ccdum2.xy(1,1));
		eltern.bchb[38] = new JRtaCheckBox("");
		eltern.bchb[38].setName("F_175");
		dum2.add(eltern.bchb[38],ccdum2.xy(1,2));
		dum2.getPanel().validate();
		dum.add(dum2.getPanel(),ccdum.xy(1,1,CellConstraints.LEFT,CellConstraints.TOP));
		/**/
		dummy2 = new FormLayout("p","p,p");
		dum2 = new PanelBuilder(dummy2);
		dum2.getPanel().setOpaque(false);
		ccdum2 = new CellConstraints();
		lab = new JLabel("Sinnesorgane");
		lab.setFont(fontarialfett);
		dum2.add(lab,ccdum2.xy(1,1,CellConstraints.LEFT,CellConstraints.TOP));
		dum2.add(getLabel("(Zu beachten sind insbesondere Seh-, Hör-, Sprach-, Tast- und Riechvermögen)"),
				ccdum2.xy(1,2,CellConstraints.LEFT,CellConstraints.TOP));
		dum2.getPanel().validate();
		dum.add(dum2.getPanel(),ccdum.xy(3,1,CellConstraints.LEFT,CellConstraints.CENTER));
		dum.getPanel().validate();
		lei1.add(dum.getPanel(),ccl1.xy(3, 7,CellConstraints.FILL,CellConstraints.TOP));
		/********Ende F175*******************/
		dummy = new FormLayout("p,5dlu,p","p");
		dum = new PanelBuilder(dummy);
		dum.getPanel().setOpaque(false);
		ccdum = new CellConstraints();
		/**/
		dummy2 = new FormLayout("p","p,p");
		dum2 = new PanelBuilder(dummy2);
		dum2.getPanel().setOpaque(false);
		ccdum2 = new CellConstraints();
		dum2.add(getLabelKleinRot("176"),ccdum2.xy(1,1));
		eltern.bchb[39] = new JRtaCheckBox("");
		eltern.bchb[39].setName("F_176");
		dum2.add(eltern.bchb[39],ccdum2.xy(1,2));
		dum2.getPanel().validate();
		dum.add(dum2.getPanel(),ccdum.xy(1,1,CellConstraints.LEFT,CellConstraints.TOP));
		/**/
		dummy2 = new FormLayout("p","p,p,p");
		dum2 = new PanelBuilder(dummy2);
		dum2.getPanel().setOpaque(false);
		ccdum2 = new CellConstraints();
		lab = new JLabel("Bewegungs-/Haltungsapparat");
		lab.setFont(fontarialfett);
		dum2.add(lab,ccdum2.xy(1,1,CellConstraints.LEFT,CellConstraints.TOP));
		dum2.add(getLabel("(Zu beachten sind insbesondere Gebrauchsfähigkeit der Hände, häufiges Bücken, "+
				"Ersteigen von Treppen, Leitern und Gerüsten, Heben, Tragen"),
				ccdum2.xy(1,2,CellConstraints.LEFT,CellConstraints.TOP));
		dum2.add(getLabel("und Bewegen von Lasten, Gang- und Standsicherheit, Zwangshaltungen)"),
				ccdum2.xy(1,3,CellConstraints.LEFT,CellConstraints.TOP));
		
		dum2.getPanel().validate();
		dum.add(dum2.getPanel(),ccdum.xy(3,1,CellConstraints.LEFT,CellConstraints.CENTER));
		dum.getPanel().validate();
		lei1.add(dum.getPanel(),ccl1.xy(3, 9,CellConstraints.FILL,CellConstraints.TOP));
		/********Ende F176*******************/
		dummy = new FormLayout("p,5dlu,p","p");
		dum = new PanelBuilder(dummy);
		dum.getPanel().setOpaque(false);
		ccdum = new CellConstraints();
		/**/
		dummy2 = new FormLayout("p","p,p");
		dum2 = new PanelBuilder(dummy2);
		dum2.getPanel().setOpaque(false);
		ccdum2 = new CellConstraints();
		dum2.add(getLabelKleinRot("177"),ccdum2.xy(1,1));
		eltern.bchb[40] = new JRtaCheckBox("");
		eltern.bchb[40].setName("F_177");
		dum2.add(eltern.bchb[40],ccdum2.xy(1,2));
		dum2.getPanel().validate();
		dum.add(dum2.getPanel(),ccdum.xy(1,1,CellConstraints.LEFT,CellConstraints.TOP));
		/**/
		dummy2 = new FormLayout("p","p,p,p");
		dum2 = new PanelBuilder(dummy2);
		dum2.getPanel().setOpaque(false);
		ccdum2 = new CellConstraints();
		lab = new JLabel("Gefährdungs- und Belastungsfaktoren");
		lab.setFont(fontarialfett);
		dum2.add(lab,ccdum2.xy(1,1,CellConstraints.LEFT,CellConstraints.TOP));
		dum2.add(getLabel("(Zu beachten sind insbesondere Nässe, Zugluft, extrem schwankende Termperaturen, inhaltive "+
				"Belastungen und Allergene, Erschütterungen,"),
				ccdum2.xy(1,2,CellConstraints.LEFT,CellConstraints.TOP));
		dum2.add(getLabel("Vibrationen, Tätigkeiten mit erhöhter Unfallgefahr, häufig wechselnde Arbeitszeiten)"),
				ccdum2.xy(1,3,CellConstraints.LEFT,CellConstraints.TOP));
		
		dum2.getPanel().validate();
		dum.add(dum2.getPanel(),ccdum.xy(3,1,CellConstraints.LEFT,CellConstraints.CENTER));
		dum.getPanel().validate();
		lei1.add(dum.getPanel(),ccl1.xy(3, 11,CellConstraints.FILL,CellConstraints.TOP));
		/********Ende F177*******************/
		
		/***************************/
		lei1.getPanel().validate();
		return lei1.getPanel();
	}	
	private JPanel leiBild4(){
		FormLayout lay1 = new FormLayout("1px,fill:0:grow(1.0),1px","1px,p,1px");		
		PanelBuilder lei1 = new PanelBuilder(lay1);
		lei1.setOpaque(false);               
		CellConstraints ccl1 = new CellConstraints();
		lei1.add(getRand(Color.BLACK),ccl1.xywh(1, 1, 1, 3));
		lei1.add(getRand(Color.BLACK),ccl1.xywh(3, 1, 1, 3));
		lei1.add(getRand(Color.GRAY),ccl1.xywh(1, 3, 3, 1));

		FormLayout dummy = new FormLayout("2dlu,p,2dlu,p","5dlu,p,p,10dlu");
		PanelBuilder dum = new PanelBuilder(dummy);
		dum.getPanel().setOpaque(false);
		CellConstraints ccdum = new CellConstraints();
		
		dum.add(getLabelKleinRot("173"),ccdum.xy(2,2));
		eltern.bchb[36] = new JRtaCheckBox("");
		eltern.bchb[36].setName("F_173");
		dum.add(eltern.bchb[36],ccdum.xy(2,3,CellConstraints.DEFAULT,CellConstraints.CENTER));
		JLabel lab = new JLabel("keine wesentlichen Einschränkungen");
		lab.setFont(fontarialfett);
		dum.add(lab,ccdum.xy(4,3));
		dum.getPanel().validate();
		lei1.add(dum.getPanel(),ccl1.xy(2, 2));
		
		/*************************/
		lei1.getPanel().validate();		
		return lei1.getPanel();
	}
	private JPanel leiBild3(){
		FormLayout lay1 = new FormLayout("1px,fill:0:grow(1.0),1px","1px,p,1px");		
		PanelBuilder lei1 = new PanelBuilder(lay1);
		lei1.setOpaque(false);               
		CellConstraints ccl1 = new CellConstraints();
		lei1.add(getRand(Color.BLACK),ccl1.xywh(1, 1, 1, 3));
		lei1.add(getRand(Color.BLACK),ccl1.xywh(3, 1, 1, 3));
		lei1.add(getRand(Color.GRAY),ccl1.xywh(1, 3, 3, 1));
		
										  // 1   2      3   4  5   6  7   8  9   10  11  12 13   14 15 16   
		FormLayout dummy = new FormLayout("2dlu,112dlu,2dlu,p,2dlu,p,2dlu,p,69dlu,p,2dlu,p, 58dlu,p,2dlu,p",
		//	  1  2     3  4    5 
			"1px,2dlu, p,p,2dlu,1px");
		PanelBuilder dum = new PanelBuilder(dummy);
		dum.getPanel().setOpaque(false);
		CellConstraints ccdum = new CellConstraints();
		JLabel lab = new JLabel("Arbeitsorganisation");
		lab.setFont(this.fontarialnormal);
		dum.add(lab,ccdum.xyw(2, 4, 8));
		dum.add(getLabelKleinRot("170"),ccdum.xy(6,3));
		eltern.bchb[33] = new JRtaCheckBox("");
		eltern.bchb[33].setName("F_170");
		dum.add(eltern.bchb[33],ccdum.xy(6,4));
		dum.add(getLabel("Tagesschicht"),ccdum.xy(8, 4));

		dum.add(getLabelKleinRot("171"),ccdum.xy(10,3));
		eltern.bchb[34] = new JRtaCheckBox("");
		eltern.bchb[34].setName("F_171");
		dum.add(eltern.bchb[34],ccdum.xy(10,4));
		dum.add(getLabel("Früh-/Spätschicht"),ccdum.xy(12, 4));
		
		dum.add(getLabelKleinRot("172"),ccdum.xy(14,3));
		eltern.bchb[35] = new JRtaCheckBox("");
		eltern.bchb[35].setName("F_172");
		dum.add(eltern.bchb[35],ccdum.xy(14,4));
		dum.add(getLabel("Nachtschicht"),ccdum.xy(16, 4));

		dum.getPanel().validate();
		/*************************/
		lei1.getPanel().validate();
		lei1.add(dum.getPanel(),ccl1.xy(2,2,CellConstraints.FILL,CellConstraints.DEFAULT));
		
		return lei1.getPanel();
	}
	private JPanel leiBild2(){         // 1        2            3        4            5        6            7     1   2  3
		FormLayout lay1 = new FormLayout("1px,fill:0:grow(0.33),1px,fill:0:grow(0.33),1px,fill:0:grow(0.33),1px","1px,p,1px");		
		PanelBuilder lei1 = new PanelBuilder(lay1);
		lei1.setOpaque(false);               
		CellConstraints ccl1 = new CellConstraints();
		lei1.add(getRand(Color.BLACK),ccl1.xywh(1, 1, 1, 3));
		lei1.add(getRand(Color.BLACK),ccl1.xywh(7, 1, 1, 3));
		
		
		/********************************/
		//										2=chk1        6=chk2       10=chk3 
		                                  // 1  2  3   4  5    6  7   8  9   10  11  12     
		FormLayout dummy = new FormLayout("2dlu,p,2dlu,p,25dlu,p,2dlu,p,25dlu,p,2dlu,p",
		//	  1  2  3   4  5   6  7
			"1px,p,5dlu,p,0dlu,p,2px");
		PanelBuilder dum = new PanelBuilder(dummy);
		dum.getPanel().setOpaque(false);
		CellConstraints ccdum = new CellConstraints();
		/**********************************/
		/*****1-te Abteilung im Stehen*****/
		FormLayout dummy2 = new FormLayout("p,13dlu,p","p");
		PanelBuilder dum2 = new PanelBuilder(dummy2);
		dum2.getPanel().setOpaque(false);
		CellConstraints ccdum2 = new CellConstraints();
		JLabel lab = new JLabel("Arbeitshaltung");
		lab.setFont(fontarialnormal);
		dum2.add(lab,ccdum2.xy(1, 1));
		lab = new JLabel("im Stehen");
		lab.setFont(fontarialnormal);
		lab.setForeground(Color.RED);
		dum2.add(lab,ccdum2.xy(3, 1));
		dum2.getPanel().validate();
		dum.add(dum2.getPanel(),ccdum.xyw(2, 2,11));
		/*******************/
		dum.add(getLabelKleinRot("161"),ccdum.xy(2, 4,CellConstraints.LEFT,CellConstraints.BOTTOM));
		eltern.bchb[24] = new JRtaCheckBox("");
		eltern.bchb[24].setName("F_161");
		dum.add(eltern.bchb[24],ccdum.xy(2, 6,CellConstraints.LEFT,CellConstraints.CENTER));
		dum.add(getLabel("ständig"),ccdum.xy(4, 6,CellConstraints.LEFT,CellConstraints.CENTER));
		dum.add(getLabelKleinRot("162"),ccdum.xy(6, 4,CellConstraints.LEFT,CellConstraints.BOTTOM));
		eltern.bchb[25] = new JRtaCheckBox("");
		eltern.bchb[25].setName("F_162");
		dum.add(eltern.bchb[25],ccdum.xy(6, 6,CellConstraints.LEFT,CellConstraints.CENTER));
		/***/
		dummy2 = new FormLayout("p,","p,p");
		dum2 = new PanelBuilder(dummy2);
		dum2.getPanel().setOpaque(false);
		ccdum2 = new CellConstraints();
		dum2.add(getLabel("über-"),ccdum2.xy(1, 1));
		dum2.add(getLabel("wiegend"),ccdum2.xy(1, 2));
		dum.add(dum2.getPanel(),ccdum.xy(8, 6,CellConstraints.LEFT,CellConstraints.BOTTOM));
		/***/
		dum.add(getLabelKleinRot("163"),ccdum.xy(10, 4,CellConstraints.LEFT,CellConstraints.BOTTOM));
		eltern.bchb[26] = new JRtaCheckBox("");
		eltern.bchb[26].setName("F_163");
		dum.add(eltern.bchb[26],ccdum.xy(10, 6,CellConstraints.LEFT,CellConstraints.CENTER));
		/***/
		dummy2 = new FormLayout("p,","p,p");
		dum2 = new PanelBuilder(dummy2);
		dum2.getPanel().setOpaque(false);
		ccdum2 = new CellConstraints();
		dum2.add(getLabel("zeit-"),ccdum2.xy(1, 1));
		dum2.add(getLabel("weise"),ccdum2.xy(1, 2));
		dum.add(dum2.getPanel(),ccdum.xy(12, 6,CellConstraints.LEFT,CellConstraints.BOTTOM));
		/***/
		/***************/
		dum.getPanel().validate();
		lei1.add(dum.getPanel(),ccl1.xy(2, 2));
		lei1.add(getRand(Color.GRAY),ccl1.xywh(3, 1, 1, 3));
		/********************************/
		/*****2-te Abteilungim Gehen*****/
		//										2=chk1        6=chk2       10=chk3 
        // 1  2  3   4  5    6  7   8  9   10  11  12     
		dummy = new FormLayout("2dlu,p,2dlu,p,25dlu,p,2dlu,p,25dlu,p,2dlu,p",
				//	  1  2  3   4  5   6  7
					"1px,p,5dlu,p,0dlu,p,2px");
		dum = new PanelBuilder(dummy);
		dum.getPanel().setOpaque(false);
		ccdum = new CellConstraints();
		lab = new JLabel("im Gehen");
		lab.setFont(fontarialnormal);
		lab.setForeground(Color.RED);
		dum.add(lab,ccdum.xyw(2, 2,11));
		/*******************/
		dum.add(getLabelKleinRot("164"),ccdum.xy(2, 4,CellConstraints.LEFT,CellConstraints.BOTTOM));
		eltern.bchb[27] = new JRtaCheckBox("");
		eltern.bchb[27].setName("F_164");
		dum.add(eltern.bchb[27],ccdum.xy(2, 6,CellConstraints.LEFT,CellConstraints.CENTER));
		dum.add(getLabel("ständig"),ccdum.xy(4, 6,CellConstraints.LEFT,CellConstraints.CENTER));
		dum.add(getLabelKleinRot("165"),ccdum.xy(6, 4,CellConstraints.LEFT,CellConstraints.BOTTOM));
		eltern.bchb[28] = new JRtaCheckBox("");
		eltern.bchb[28].setName("F_165");
		dum.add(eltern.bchb[28],ccdum.xy(6, 6,CellConstraints.LEFT,CellConstraints.CENTER));
		/***/
		dummy2 = new FormLayout("p,","p,p");
		dum2 = new PanelBuilder(dummy2);
		dum2.getPanel().setOpaque(false);
		ccdum2 = new CellConstraints();
		dum2.add(getLabel("über-"),ccdum2.xy(1, 1));
		dum2.add(getLabel("wiegend"),ccdum2.xy(1, 2));
		dum.add(dum2.getPanel(),ccdum.xy(8, 6,CellConstraints.LEFT,CellConstraints.BOTTOM));
		/***/
		dum.add(getLabelKleinRot("166"),ccdum.xy(10, 4,CellConstraints.LEFT,CellConstraints.BOTTOM));
		eltern.bchb[29] = new JRtaCheckBox("");
		eltern.bchb[29].setName("F_166");
		dum.add(eltern.bchb[29],ccdum.xy(10, 6,CellConstraints.LEFT,CellConstraints.CENTER));
		/***/
		dummy2 = new FormLayout("p,","p,p");
		dum2 = new PanelBuilder(dummy2);
		dum2.getPanel().setOpaque(false);
		ccdum2 = new CellConstraints();
		dum2.add(getLabel("zeit-"),ccdum2.xy(1, 1));
		dum2.add(getLabel("weise"),ccdum2.xy(1, 2));
		dum.add(dum2.getPanel(),ccdum.xy(12, 6,CellConstraints.LEFT,CellConstraints.BOTTOM));
		/***/
		/***************/
		dum.getPanel().validate();
		lei1.add(dum.getPanel(),ccl1.xy(4, 2));
		lei1.add(getRand(Color.GRAY),ccl1.xywh(5, 1, 1, 3));
		/********************************/
		/********************************/
		/***3-te Abteilung im Sitzen*****/
		//										2=chk1        6=chk2       10=chk3 
        // 1  2  3   4  5    6  7   8  9   10  11  12     
		dummy = new FormLayout("2dlu,p,2dlu,p,25dlu,p,2dlu,p,25dlu,p,2dlu,p",
				//	  1  2  3   4  5   6  7
					"1px,p,5dlu,p,0dlu,p,2px");
		dum = new PanelBuilder(dummy);
		dum.getPanel().setOpaque(false);
		ccdum = new CellConstraints();
		lab = new JLabel("im Sitzen");
		lab.setFont(fontarialnormal);
		lab.setForeground(Color.RED);
		dum.add(lab,ccdum.xyw(2, 2,11));
		/*******************/
		dum.add(getLabelKleinRot("167"),ccdum.xy(2, 4,CellConstraints.LEFT,CellConstraints.BOTTOM));
		eltern.bchb[30] = new JRtaCheckBox("");
		eltern.bchb[30].setName("F_167");
		dum.add(eltern.bchb[30],ccdum.xy(2, 6,CellConstraints.LEFT,CellConstraints.CENTER));
		dum.add(getLabel("ständig"),ccdum.xy(4, 6,CellConstraints.LEFT,CellConstraints.CENTER));
		dum.add(getLabelKleinRot("168"),ccdum.xy(6, 4,CellConstraints.LEFT,CellConstraints.BOTTOM));
		eltern.bchb[31] = new JRtaCheckBox("");
		eltern.bchb[31].setName("F_168");
		dum.add(eltern.bchb[31],ccdum.xy(6, 6,CellConstraints.LEFT,CellConstraints.CENTER));
		/***/
		dummy2 = new FormLayout("p,","p,p");
		dum2 = new PanelBuilder(dummy2);
		dum2.getPanel().setOpaque(false);
		ccdum2 = new CellConstraints();
		dum2.add(getLabel("über-"),ccdum2.xy(1, 1));
		dum2.add(getLabel("wiegend"),ccdum2.xy(1, 2));
		dum.add(dum2.getPanel(),ccdum.xy(8, 6,CellConstraints.LEFT,CellConstraints.BOTTOM));
		/***/
		dum.add(getLabelKleinRot("169"),ccdum.xy(10, 4,CellConstraints.LEFT,CellConstraints.BOTTOM));
		eltern.bchb[32] = new JRtaCheckBox("");
		eltern.bchb[32].setName("F_169");
		dum.add(eltern.bchb[32],ccdum.xy(10, 6,CellConstraints.LEFT,CellConstraints.CENTER));
		/***/
		dummy2 = new FormLayout("p,","p,p");
		dum2 = new PanelBuilder(dummy2);
		dum2.getPanel().setOpaque(false);
		ccdum2 = new CellConstraints();
		dum2.add(getLabel("zeit-"),ccdum2.xy(1, 1));
		dum2.add(getLabel("weise"),ccdum2.xy(1, 2));
		dum.add(dum2.getPanel(),ccdum.xy(12, 6,CellConstraints.LEFT,CellConstraints.BOTTOM));
		/***/
		/***************/
		dum.getPanel().validate();
		lei1.add(dum.getPanel(),ccl1.xy(6, 2));
		//lei1.add(getRand(Color.GRAY),ccl1.xywh(5, 1, 1, 3));
		/********************************/

		
		/***************/
		lei1.add(getRand(Color.GRAY),ccl1.xywh(1, 3, 7, 1));
		lei1.getPanel().validate();
		return lei1.getPanel();
		
	}
	                                    //               5=cb1           9=cb2        13=cb3         17=cb4
	private JPanel leiBild1(){          // 1   2   3   4   5  6   7  8    9  10  11 12  13  14  15 16  17  18 19      20        21
		FormLayout lay1 = new FormLayout("1px,2dlu,p,20dlu,p,2dlu,p,20dlu,p,2dlu,p,20dlu,p,2dlu,p,30dlu,p,2dlu,p,fill:0:g(1.0),1px",
//				 1   2  3   4  5   6  7	 8			
				"1px,p,3dlu,p,0dlu,p,0px,1px");
				PanelBuilder lei1 = new PanelBuilder(lay1);
				//tit.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				lei1.setOpaque(false);               
				CellConstraints ccl1 = new CellConstraints();
				
				lei1.add(getRand(Color.BLACK),ccl1.xyw(1,1,21,CellConstraints.FILL,CellConstraints.TOP));
				lei1.add(getRand(Color.GRAY),ccl1.xyw(1,8,21,CellConstraints.FILL,CellConstraints.TOP));
				lei1.add(getRand(Color.BLACK),ccl1.xywh(1,1,1,8,CellConstraints.LEFT,CellConstraints.FILL));
				lei1.add(getRand(Color.BLACK),ccl1.xywh(21,1,1,8,CellConstraints.RIGHT,CellConstraints.FILL));

				/*************************/
				FormLayout dummy = new FormLayout("p,2dlu,p","p");
				PanelBuilder dum = new PanelBuilder(dummy);
				dum.getPanel().setOpaque(false);
				CellConstraints ccdum = new CellConstraints();
				JLabel lab = new JLabel("Positives Leistungsbild: ");
				lab.setFont(fontarialfett);
				dum.add(lab,ccdum.xy(1, 1));
				lab = new JLabel("Folgende Arbeiten können verrichtet werden");
				lab.setFont(fontarialnormal);
				dum.add(lab,ccdum.xy(3, 1));
				dum.getPanel().validate();
				lei1.add(dum.getPanel(),ccl1.xyw(3, 2,11));
				/****************/
				lab = new JLabel("Körperliche Arbeitsschwere");
				lab.setFont(fontarialnormal);
				lei1.add(lab,ccl1.xy(3, 6,CellConstraints.LEFT,CellConstraints.CENTER));
				lei1.add(getLabelKleinRot("157"),ccl1.xy(5, 4,CellConstraints.LEFT,CellConstraints.BOTTOM));
				eltern.bchb[20] = new JRtaCheckBox("");
				eltern.bchb[20].setName("F_157");
				lei1.add(eltern.bchb[20],ccl1.xy(5, 6,CellConstraints.LEFT,CellConstraints.CENTER));
				lei1.add(getLabel("schwere Arbeiten"),ccl1.xy(7, 6,CellConstraints.LEFT,CellConstraints.CENTER));
				lei1.add(getLabelKleinRot("158"),ccl1.xy(9, 4,CellConstraints.LEFT,CellConstraints.BOTTOM));
				eltern.bchb[21] = new JRtaCheckBox("");
				eltern.bchb[21].setName("F_158");
				lei1.add(eltern.bchb[21],ccl1.xy(9, 6,CellConstraints.LEFT,CellConstraints.CENTER));
				lei1.add(getLabel("mittelschwere Arbeiten"),ccl1.xy(11, 6,CellConstraints.LEFT,CellConstraints.CENTER));
				/*************************/
				lei1.add(getLabelKleinRot("159"),ccl1.xy(13, 4,CellConstraints.LEFT,CellConstraints.BOTTOM));
				eltern.bchb[22] = new JRtaCheckBox("");
				eltern.bchb[22].setName("F_159");
				lei1.add(eltern.bchb[22],ccl1.xy(13, 6,CellConstraints.LEFT,CellConstraints.CENTER));
				dummy = new FormLayout("p","p,p");
				dum = new PanelBuilder(dummy);
				dum.getPanel().setOpaque(false);
				ccdum = new CellConstraints();
				dum.add(getLabel("leichte bis mittel-"),ccdum.xy(1,1));
				dum.add(getLabel("schwere Arbeiten"),ccdum.xy(1,2));
				dum.getPanel().validate();
				lei1.add(dum.getPanel(),ccl1.xy(15, 6,CellConstraints.LEFT,CellConstraints.BOTTOM));
				/*******************/
				lei1.add(getLabelKleinRot("160"),ccl1.xy(17, 4,CellConstraints.LEFT,CellConstraints.BOTTOM));
				eltern.bchb[23] = new JRtaCheckBox("");
				eltern.bchb[23].setName("F_160");
				lei1.add(eltern.bchb[23],ccl1.xy(17, 6,CellConstraints.LEFT,CellConstraints.CENTER));
				lei1.add(getLabel("leichte Arbeiten"),ccl1.xy(19, 6,CellConstraints.LEFT,CellConstraints.CENTER));
				/*******************/
				
				lei1.getPanel().validate();				
				return lei1.getPanel();
	}
	private JPanel getTitel3(){
		FormLayout laytit = new FormLayout("20dlu,p,2dlu,p",
		"4dlu,p,0dlu,p,2dlu");
		PanelBuilder tit = new PanelBuilder(laytit);
		//tit.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		tit.setOpaque(false);               
		CellConstraints cctit = new CellConstraints();
		JLabel lab = new JLabel("B.");
		lab.setFont(fontarialfett);
		tit.add(lab,cctit.xy(1, 2));
		lab = new JLabel("Positives und negatives Leistungsbild");
		lab.setFont(fontarialfett);
		tit.add(lab,cctit.xy(2, 2));
		lab = new JLabel("(allgemeiner Arbeitsmarkt)");
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
		eltern.btf[25] = new JRtaTextField("nix",false);
		eltern.btf[25].setName("TAET");
		eltern.btf[25].setDocument(new SetMaxText(55));
		plinks.add(eltern.btf[25],ccli.xy(6, 3,CellConstraints.FILL,CellConstraints.BOTTOM));
		plinks.add(getRand(Color.GRAY),ccli.xywh(8, 2, 1, 2));
		plinks.add(getLabelKleinRot("Berufsklassenschlüssel"),ccli.xy(10,2));
		eltern.btf[26] = new JRtaTextField("ZAHLEN",false);
		eltern.btf[26].setName("BKS");
		eltern.btf[26].setEnabled(false);
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
		lab.setFont(fontarialfettgross);
		tit.add(lab,cctit.xy(2, 2));
		return tit;
	}
	private JPanel getLRStriche(){
		FormLayout laytit = new FormLayout("1px,fill:0:grow(1.0),1px",
		"2dlu");
		PanelBuilder tit = new PanelBuilder(laytit);
		tit.setOpaque(false);               
		CellConstraints cctit = new CellConstraints();
		tit.add(getRand(Color.BLACK),cctit.xy(1, 1,CellConstraints.LEFT,CellConstraints.FILL));
		tit.add(getRand(Color.BLACK),cctit.xy(3, 1,CellConstraints.RIGHT,CellConstraints.FILL));
		tit.getPanel().validate();
		return tit.getPanel();
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
