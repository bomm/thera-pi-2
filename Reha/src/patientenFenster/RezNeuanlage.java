package patientenFenster;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import sqlTools.SqlInfo;
import stammDatenTools.RezTools;
import systemEinstellungen.SystemConfig;
import systemEinstellungen.SystemPreislisten;
import systemTools.Colors;
import systemTools.JCompTools;
import systemTools.JRtaCheckBox;
import systemTools.JRtaComboBox;
import systemTools.JRtaTextField;
import systemTools.ListenerTools;
import systemTools.StringTools;
import terminKalender.DatFunk;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;

public class RezNeuanlage extends JXPanel implements ActionListener, KeyListener,FocusListener,RehaTPEventListener{

	/**
	 * 
	 */
	public JRtaTextField[] jtf = {null,null,null,null,null,
								  null,null,null,null,null,
								  null,null,null,null,null,
								  null,null,null,null,null,
								  null,null,null,null,null,
								  null,null,null,null,null};
	public JRtaCheckBox[] jcb = {null,null,null,null,null,null};
	
	//public JRtaRadioButton[] jrb = {null,null,null,null,null}; 
	public JRtaComboBox[] jcmb = {null,null,null,null,null,null,null,null,null};
	
	public JTextArea jta = null;
	
	public JButton speichern = null;
	public JButton abbrechen = null;
	
	public boolean neu = false;
	public String feldname = "";
	public Vector<String> vec = null;
	public Vector<Vector<String>> preisvec = null;
	private boolean klassenReady = false;
	private boolean initReady = false;
	private static final long serialVersionUID = 1L;
	private int preisgruppe = -1;
	public boolean feldergefuellt = false;
	private String nummer = null;
	private String[] farbcodes = {null,null,null,null,null,null,null,null,null,null};
	private String[] heilmittel = {"KG","MA","ER","LO","RH"};
	
	private String aktuelleDisziplin = "";
	private int preisgruppen[] = {0,0,0,0,0};
	int[] comboid = {-1,-1,-1,-1};
	CompoundPainter cp = null;
	MattePainter mp = null;
	LinearGradientPaint p = null;
	private RehaTPEventClass rtp = null;
	public RezNeuanlage(Vector<String> vec,boolean neu,String sfeldname){
	
		
		super();
		this.neu = neu;
		this.feldname = sfeldname;
		this.vec = vec;
		
		setName("RezeptNeuanlage");
		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener((RehaTPEventListener) this);


		addKeyListener(this);
		
		setLayout(new BorderLayout());
		setOpaque(true);
		setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		add(getDatenPanel(),BorderLayout.CENTER);
		add(getButtonPanel(),BorderLayout.SOUTH);
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("RezNeuanlage"));
		validate();
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run()
		 	   {
		 			setzeFocus();		 		   
		 	   }
		});	
		initReady = true;
		
		

	}
	public void macheFarbcodes(){
		//System.out.println(SystemConfig.vSysColsBedeut.get(14));
		// 14 und < 23
		farbcodes[0] = "kein Farbcode";
		jcmb[8].addItem(farbcodes[0]);
		
		for (int i = 0;i < 9;i++){
			//System.out.println("Farbcode "+(i+1)+" = "+ SystemConfig.vSysColsBedeut.get(i+14)+" - "+(i+14));
			farbcodes[i+1] = SystemConfig.vSysColsBedeut.get(i+14);
			jcmb[8].addItem(farbcodes[i+1]);
		}
		if(! this.neu){
			int itest = StringTools.ZahlTest(this.vec.get(57));
			
			if(itest >= 0){
				//System.out.println("itest = "+itest);
				jcmb[8].setSelectedItem( (String)SystemConfig.vSysColsBedeut.get(itest) );			
			}else{
				//System.out.println("itest = "+itest);				
				jcmb[8].setSelectedIndex(0);
			}
		}else{
			jcmb[8].setSelectedIndex(0);			
		}
		
	}
	public void setzeFocus(){
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run()
		 	   {
		 		   if(neu){
		 			   int aid,kid;
		 			   boolean beenden = false;
		 			   String meldung = "";
		 			   kid = StringTools.ZahlTest(jtf[11].getText());
		 			   aid = StringTools.ZahlTest(jtf[12].getText());
		 			   if(kid < 0 && aid < 0){
		 				   beenden = true;
		 				   meldung = "Achtung - dem Patientenstamm ist keine verwertbare Krankenkasse\n"+
		 				    "sowie kein verwertbarer Arzt zugeordnet\n\n"+
		 				    "Gehen Sie im Patientenstamm auf ->Ändern/Editieren<- und ordnen Sie verwertaber Daten zu!";
		 			   }else if(kid >= 0 && aid < 0){
		 				   beenden = true;
		 				   meldung = "Achtung - dem Patientenstamm ist kein verwertbarer Arzt zugeordnet\n\n"+
		 				    "Gehen Sie im Patientenstamm auf ->Ändern/Editieren<- und ordnen Sie verwertaber Daten zu!";
		 			   }else if(kid < 0 && aid >= 0){
		 				   beenden = true;
		 				   meldung = "Achtung - dem Patientenstamm ist keine verwertbare Krankenkasse zugeordnet\n\n"+
		 				    "Gehen Sie im Patientenstamm auf ->Ändern/Editieren<- und ordnen Sie verwertaber Daten zu!";
		 			   }
		 			   if(beenden){
		 				  JOptionPane.showMessageDialog(null,meldung); 
		 				  aufraeumen();
		 				  ((JXDialog)getParent().getParent().getParent().getParent().getParent()).dispose();
		 			   }else{
			 			   holePreisGruppe(jtf[11].getText().trim());
				 			  SwingUtilities.invokeLater(new Runnable(){
				 				  public  void run()
				 				  {
				 					  jcmb[0].requestFocus();
				 				  }
				 			  });	   		
		 			   }
	 			   // else bedeutet nicht neu - sondern ändern
		 		   }else{
		 			   int aid,kid;
		 			   boolean beenden = false;
		 			   String meldung = "";
		 			   kid = StringTools.ZahlTest(jtf[11].getText());
		 			   aid = StringTools.ZahlTest(jtf[12].getText());
		 			   if(kid < 0 && aid < 0){
		 				   jtf[11].setText(Integer.toString(Reha.thisClass.patpanel.kid));
		 				   jtf[12].setText(Integer.toString(Reha.thisClass.patpanel.aid));
		 				   jtf[0].setText(Reha.thisClass.patpanel.patDaten.get(13));
		 				   //holePreisGruppe(Reha.thisClass.patpanel.kid);
		 			   }else if(kid >= 0 && aid < 0){
		 				   jtf[12].setText(Integer.toString(Reha.thisClass.patpanel.aid));
		 			   }else if(kid < 0 && aid >= 0){
		 				   jtf[11].setText(Integer.toString(Reha.thisClass.patpanel.kid));
		 				   jtf[0].setText(Reha.thisClass.patpanel.patDaten.get(13));
		 				   //holePreisGruppe(Reha.thisClass.patpanel.kid);		 				   
		 			   }else{
		 				   System.out.println("*****************Keine Preisgruppen bezogen*******************");
		 				  //preisgruppen
		 				  //RezTools.holePreisVector(vec.get(1), Integer.parseInt(vec.get(41))-1);
		 				  //ladePreise();
		 			   }
		 			   

		 			   SwingUtilities.invokeLater(new Runnable(){
			 			 	   public  void run()
			 			 	   {
			 			 		   jtf[0].requestFocus();
			 			 	   }
			 			});	   		
		 		   }
		 		   		 		   
		 	   }
		});	   		
		//****************Checken ob Preisgruppen bedient werden****************
	}
	
	
	
	public JXPanel getButtonPanel(){
		JXPanel	jpan = JCompTools.getEmptyJXPanel();
		//jpan.setBorder(BorderFactory.createLineBorder(Colors.PiOrange.alpha(0.25f)));
		jpan.addKeyListener(this);
		jpan.setOpaque(false);
		FormLayout lay = new FormLayout(
		        // 1                2          3             4      5    
				"fill:0:grow(0.33),50dlu,fill:0:grow(0.33),50dlu,fill:0:grow(0.33)",
				// 1  2  3  
				"5dlu,p,5dlu");
		CellConstraints cc = new CellConstraints();
		jpan.setLayout(lay);
		speichern = new JButton("speichern");
		speichern.setActionCommand("speichern");
		speichern.addActionListener(this);
		speichern.addKeyListener(this);
		speichern.setMnemonic(KeyEvent.VK_S);
		jpan.add(speichern,cc.xy(2,2));
		
		abbrechen = new JButton("abbrechen");
		abbrechen.setActionCommand("abbrechen");
		abbrechen.addActionListener(this);
		abbrechen.addKeyListener(this);
		abbrechen.setMnemonic(KeyEvent.VK_A);		
		jpan.add(abbrechen,cc.xy(4,2));

		return jpan;
	}	
	
	/********************************************/

	private JScrollPane getDatenPanel(){  //1                  2      3    4          5              6      7        8       
		FormLayout lay = new FormLayout("right:max(80dlu;p), 4dlu, 60dlu, 5dlu, right:max(60dlu;p), 4dlu, 60dlu",
			       //1.   2.   3.   4.   5.   6   7   8    9   10   11  12  13  14    15   16   17  18 19   20   21  22   23  24   25  
					"p, 10dlu, p, 5dlu,  p, 5dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, " +
		//26   27   28   29  30   31   32    33   34   35   36    37	 38	  39			
		"10dlu, p, 10dlu, p, 2dlu, p, 2dlu,  p,  10dlu, p, 10dlu, 30dlu,2dlu,2dlu");
					

		CellConstraints cc = new CellConstraints();
		PanelBuilder jpan = new PanelBuilder(lay);
		jpan.setDefaultDialogBorder();
		jpan.getPanel().setOpaque(false);
		String ywerte = "";
	
		jtf[0] = new JRtaTextField("NIX",false); // kasse/kostenträger
		jtf[1] = new JRtaTextField("NIX",false); // arzt
		jtf[2] = new JRtaTextField("DATUM",true); // rezeptdatum
		jtf[3] = new JRtaTextField("DATUM",true); // spätester beginn
		jtf[4] = new JRtaTextField("ZAHLEN",true); // Anzahl 1
		jtf[5] = new JRtaTextField("ZAHLEN",true); // Anzahl 2
		jtf[6] = new JRtaTextField("ZAHLEN",true); // Anzahl 3
		jtf[7] = new JRtaTextField("ZAHLEN",true); // Anzahl 4
		jtf[8] = new JRtaTextField("GROSS",true); // Frequenz		
		jtf[9] = new JRtaTextField("ZAHLEN",true); // Dauer		
		jtf[10] = new JRtaTextField("GROSS",true); // angelegt von
		jtf[11] = new JRtaTextField("GROSS",false); //kassenid
		jtf[12] = new JRtaTextField("GROSS",false); //arztid
		// ************alles nachfolgende mu� noch eingebaut werden.....
		jtf[13] = new JRtaTextField("GROSS",false); //preisgruppe
		jtf[14] = new JRtaTextField("GROSS",false); //heimbewohner
		jtf[15] = new JRtaTextField("GROSS",false); //befreit
		jtf[16] = new JRtaTextField("",false); //POS1		
		jtf[17] = new JRtaTextField("",false); //POS2
		jtf[18] = new JRtaTextField("",false); //POS3
		jtf[19] = new JRtaTextField("",false); //POS4
		jtf[20] = new JRtaTextField("",false); //PREIS1
		jtf[21] = new JRtaTextField("",false); //PREIS2
		jtf[22] = new JRtaTextField("",false); //PREIS3
		jtf[23] = new JRtaTextField("",false); //PREIS4
		jtf[24] = new JRtaTextField("DATUM",false); // ANLAGEDATUM
		jtf[25] = new JRtaTextField("",false); // KILOMETER
		jtf[26] = new JRtaTextField("",false); //id von Patient
		jtf[27] = new JRtaTextField("",false); //pat_intern von Patient
		jtf[28] = new JRtaTextField("",false); //zzstatus
		jtf[29] = new JRtaTextField("",false); //Heimbewohner aus PatStamm

		jcmb[0] = new JRtaComboBox();
		int lang = SystemConfig.rezeptKlassenAktiv.size();
		for(int i = 0;i < lang;i++){
			jcmb[0].addItem(SystemConfig.rezeptKlassenAktiv.get(i).get(0));	
		}
		
		jpan.addLabel("Rezeptklasse auswählen",cc.xy(1, 3));
		jpan.add(jcmb[0],cc.xyw(3, 3,5));
		jcmb[0].setActionCommand("rezeptklasse");
		jcmb[0].addActionListener(this);
		/********************/
		if(this.neu){
			jcmb[0].setSelectedItem(SystemConfig.initRezeptKlasse);
		}else{
			for(int i = 0;i < lang;i++){
				if(this.vec.get(1).substring(0,2).equals(SystemConfig.rezeptKlassenAktiv.get(i).get(1))){
					jcmb[0].setSelectedIndex(i);
				}
			}
			/*
			if(this.vec.get(1).contains("KG") ){
				jcmb[0].setSelectedIndex(0);
			}else if(this.vec.get(1).contains("MA") ){
				jcmb[0].setSelectedIndex(1);
			}else if(this.vec.get(1).contains("ER") ){
				jcmb[0].setSelectedIndex(2);
			}else if(this.vec.get(1).contains("LO") ){
				jcmb[0].setSelectedIndex(3);
			}else if(this.vec.get(1).contains("RH") ){
				jcmb[0].setSelectedIndex(4);
			}
			*/
			jcmb[0].setEnabled(false);
		}			

		
		jpan.addSeparator("Rezeptkopf", cc.xyw(1,5,7));
		
		
		jtf[0].setName("ktraeger");
		jtf[0].addKeyListener(this);
		jpan.addLabel("Kostenträger (?)",cc.xy(1,7));
		jpan.add(jtf[0],cc.xy(3,7));
		

		jtf[1].setName("arzt");
		jtf[1].addKeyListener(this);		
		jpan.addLabel("verordn. Arzt (?)",cc.xy(5,7));
		jpan.add(jtf[1],cc.xy(7,7));
		

		jtf[2].setName("rez_datum");
		jtf[2].addFocusListener(this);
		jpan.addLabel("Rezeptdatum",cc.xy(1,9));
		jpan.add(jtf[2],cc.xy(3,9));
		

		jtf[3].setName("lastdate");
		jpan.addLabel("spätester Beh.Beginn",cc.xy(5,9));
		jpan.add(jtf[3],cc.xy(7,9));

		jcmb[1] = new JRtaComboBox(new String[] {"Erstverodnung","Folgeverordnung",
				"außerhalb des Regelfalles"});
		jcmb[1].setActionCommand("verordnungsart");
		jcmb[1].addActionListener(this);
		jpan.addLabel("Art d. Verordn.",cc.xy(1, 11));
		jpan.add(jcmb[1],cc.xy(3, 11));
		jcb[0] = new JRtaCheckBox("vorhanden");
		jcb[0].setOpaque(false);
		jcb[0].setEnabled(false);
		jpan.addLabel("Begründ. für adR",cc.xy(5, 11));
		jpan.add(jcb[0],cc.xy(7, 11));
		
		jcb[1] = new JRtaCheckBox("Ja / Nein");
		jcb[1].setOpaque(false);
		jcb[1].setActionCommand("Hausbesuche");
		jcb[1].addActionListener(this);
		jpan.addLabel("Hausbesuch",cc.xy(1, 13));
		jpan.add(jcb[1],cc.xy(3, 13));

		jcb[5] = new JRtaCheckBox("abrechnen");
		jcb[5].setOpaque(false);
		jcb[5].setToolTipText("Nur aktiv wenn Patient Heimbewohner und Hausbesuch angekreuzt");
		jpan.addLabel("volle HB-Gebühr",cc.xy(5,13));
		if(neu){
			jcb[5].setEnabled(false);
			jcb[5].setSelected(false);
		}else{
			if(Reha.thisClass.patpanel.patDaten.get(44).equals("T")){
				// Wenn Heimbewohner
				if(this.vec.get(43).equals("T")){
					jcb[5].setEnabled(true);
					jcb[5].setSelected( (this.vec.get(61).equals("T") ? true : false));
				}else{
					jcb[5].setEnabled(false);
					jcb[5].setSelected(false);
				}
			}else{
				// Wenn kein(!!) Heimbewohner
				if(this.vec.get(43).equals("T")){
					jcb[5].setEnabled(false);
					jcb[5].setSelected(true);
				}else{
					jcb[5].setEnabled(false);
					jcb[5].setSelected(false);
				}
			}
		}
		jpan.add(jcb[5],cc.xy(7,13));
		


		jcb[2] = new JRtaCheckBox("angefordert");
		jcb[2].setOpaque(false);
		jpan.addLabel("Therapiebericht",cc.xy(1, 15));
		jpan.add(jcb[2],cc.xy(3, 15));
		
		jpan.addSeparator("Verordnete Heilmittel", cc.xyw(1,17,7));

		jtf[4].setName("anzahl1");
		jtf[4].addFocusListener(this);
		jpan.addLabel("Anzahl / Heilmittel 1",cc.xy(1, 19));
		jpan.add(jtf[4],cc.xy(3, 19));
		jcmb[2] = new JRtaComboBox();
		jcmb[2].setActionCommand("leistung1");
		jcmb[2].addActionListener(this);
		jpan.add(jcmb[2],cc.xyw(5, 19,3));
		
		jpan.addLabel("Anzahl / Heilmittel 2",cc.xy(1, 21));
		jpan.add(jtf[5],cc.xy(3, 21));
		jcmb[3] = new JRtaComboBox();
		jcmb[3].setActionCommand("leistung2");
		jcmb[3].addActionListener(this);
		jpan.add(jcmb[3],cc.xyw(5, 21,3));
		
		jpan.addLabel("Anzahl / Heilmittel 3",cc.xy(1, 23));
		jpan.add(jtf[6],cc.xy(3, 23));
		jcmb[4] = new JRtaComboBox();
		jcmb[4].setActionCommand("leistung3");
		jcmb[4].addActionListener(this);
		jpan.add(jcmb[4],cc.xyw(5, 23,3));

		jpan.addLabel("Anzahl / Heilmittel 4",cc.xy(1, 25));
		jpan.add(jtf[7],cc.xy(3, 25));
		jcmb[5] = new JRtaComboBox();
		jcmb[5].setActionCommand("leistung4");
		jcmb[5].addActionListener(this);
		jpan.add(jcmb[5],cc.xyw(5, 25,3));
		
		jpan.addSeparator("Durchführungsbestimmungen", cc.xyw(1,27,7));
		
		jpan.addLabel("Behandlungsfrequenz",cc.xy(1, 29));		
		jpan.add(jtf[8],cc.xy(3, 29));	

		jpan.addLabel("Dauer der Behandl. in Min.",cc.xy(5, 29));
		jpan.add(jtf[9],cc.xy(7, 29));

		
		jpan.addLabel("Indikationsschlüssel",cc.xy(1, 31));		
		jcmb[6] = new JRtaComboBox();
		jpan.add(jcmb[6],cc.xy(3, 31));
		
		klassenReady = true;
		fuelleIndis((String)jcmb[0].getSelectedItem());	
	
		jpan.addLabel("Barcode-Format",cc.xy(5, 31));
		//jcmb[7] = new JRtaComboBox(new String[] {"Muster 13/18","Muster 14","DIN A6-Format","DIN A4(BGE)","DIN A4 (REHA)"});
		jcmb[7] = new JRtaComboBox(SystemConfig.rezBarCodName);
		jpan.add(jcmb[7],cc.xy(7, 31));
		
		jpan.addLabel("FarbCode im TK",cc.xy(1, 33));
		jcmb[8] = new JRtaComboBox();
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				macheFarbcodes();
				return null;
			}
		}.execute();

		jpan.add(jcmb[8],cc.xy(3, 33));
		jpan.addLabel("Angelegt von",cc.xy(5, 33));
		jpan.add(jtf[10],cc.xy(7, 33));
		
		
		jpan.addSeparator("Ärztliche Diagnose laut Verordnung", cc.xyw(1,35,7));
		
		jta = new JTextArea();
		jta.setBorder(BorderFactory.createLineBorder(Colors.PiOrange.alpha(0.5f)));
		jta.setFont(new Font("Courier",Font.PLAIN,11));
		jta.setLineWrap(true);
		jta.setName("notitzen");
		jta.setWrapStyleWord(true);
		jta.setEditable(true);
		jta.setBackground(Color.WHITE);
		jta.setForeground(Color.RED);
		JScrollPane span = JCompTools.getTransparentScrollPane(jta);
		span.setBorder(BorderFactory.createLineBorder(Colors.PiOrange.alpha(0.25f)));
		jpan.add(span,cc.xywh(1, 37,7,2));
		JScrollPane jscr = JCompTools.getTransparentScrollPane(jpan.getPanel());
		jscr.getVerticalScrollBar().setUnitIncrement(15);
		
		if(this.neu){
			this.holePreisGruppe(Reha.thisClass.patpanel.patDaten.get(68).trim());
			this.ladePreisliste(jcmb[0].getSelectedItem().toString().trim(), preisgruppen[jcmb[0].getSelectedIndex()]);
			this.fuelleIndis(jcmb[0].getSelectedItem().toString().trim());
			ladeZusatzDatenNeu();
		}else{
			this.holePreisGruppe(this.vec.get(37));
			this.ladePreisliste(jcmb[0].getSelectedItem().toString().trim(), preisgruppen[jcmb[0].getSelectedIndex()]);
			this.fuelleIndis(jcmb[0].getSelectedItem().toString().trim());
			ladeZusatzDatenAlt();
		}

		
		jscr.validate();
		return jscr;
		
	}
	public int leistungTesten(int combo,int veczahl){
		int retwert = 0;
		if(veczahl==-1 || veczahl==0){
			return retwert;
		}
		if(preisvec==null){
			return 0;
		}
		for(int i = 0;i<preisvec.size();i++){
			if( new Integer( (String) ((Vector)preisvec.get(i)).get(preisvec.get(i).size()-1)) == veczahl ){
				return i+1;
			}
		}
		
		return retwert;
	}
	public RezNeuanlage getInstance(){
		return this;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("rezeptklasse") && klassenReady){
			this.ladePreisliste(jcmb[0].getSelectedItem().toString().trim(), preisgruppen[jcmb[0].getSelectedIndex()]);			
			this.fuelleIndis((String)jcmb[0].getSelectedItem());
			return;
		}
		/*********************/		
		if(e.getActionCommand().equals("verordnungsart") && klassenReady){
			if(jcmb[1].getSelectedIndex()==2){
				jcb[0].setEnabled(true);
			}else{
				jcb[0].setSelected(false);
				jcb[0].setEnabled(false);				
			}
			return;
		}
		/*********************/		
		if(e.getActionCommand().equals("speichern") ){
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					try{
						if(getInstance().neu){
							doSpeichernNeu();
						}else{
							doSpeichernAlt();
						}
					}catch(Exception ex){
						ex.printStackTrace();
					}
					return null;
				}
				
			}.execute();
			return;
		}
		/*********************/
		if(e.getActionCommand().equals("abbrechen") ){
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					doAbbrechen();
					return null;
				}
			}.execute();
			return;
		}
		if(e.getActionCommand().equals("Hausbesuche") ){
			if(jcb[1].isSelected()){
				// Hausbesuch gew�hlt
				if(Reha.thisClass.patpanel.patDaten.get(44).equals("T")){
					jcb[5].setEnabled(true);	
					SwingUtilities.invokeLater(new Runnable(){
					 	   public  void run()
					 	   {
								jcb[1].requestFocus();		 		   
					 	   }
					});	
				}else{
					jcb[5].setEnabled(false);
					jcb[5].setSelected(true);
					SwingUtilities.invokeLater(new Runnable(){
					 	   public  void run()
					 	   {
								jcb[1].requestFocus();		 		   
					 	   }
					});	
				}
				
			}else{
				// Haubesuch abgew�hlt
				jcb[5].setEnabled(false);
				jcb[5].setSelected(false);
				SwingUtilities.invokeLater(new Runnable(){
				 	   public  void run()
				 	   {
							jcb[1].requestFocus();		 		   
				 	   }
				});	
			}
			return;
		}

		/*********************/		
		if(e.getActionCommand().contains("leistung") && initReady){
			int lang = e.getActionCommand().length();
			//String test = this.preisvec.
			doRechnen( new Integer( e.getActionCommand().substring(lang-1 ) ) );
			String test = (String)((JRtaComboBox)e.getSource()).getSelectedItem();
			if(test==null){
				return;
			}
			if(! test.equals("./.")){
				String id = (String)((JRtaComboBox)e.getSource()).getValue();
				Double preis = holePreisDouble(id,preisgruppe);
				if(preis <= 0.0){
					JOptionPane.showMessageDialog(null,"Diese Position ist für die gewählte Preisgruppe ungültig\nBitte weisen Sie in der Preislisten-Bearbeitung der Position ein Kürzel zu");
					((JRtaComboBox)e.getSource()).setSelectedIndex(0);
				}
			}
			
			return; 
		}
		
	}
	private void doRechnen(int comb){
		System.out.println("Betroffen = Leistungscombo Nr. - "+comb);

	}

	
	private boolean komplettTest(){
		if(jtf[0].getText().trim().equals("")){
			JOptionPane.showMessageDialog(null, "Ohne die Angabe 'Kostenträger' kann ein Rezept nicht abgespeichert werden.");
			 SwingUtilities.invokeLater(new Runnable(){
			 	   public  void run()
			 	   {
						jtf[0].requestFocus();
			 	   }
			});	   		
			return false;
		}
		if(jtf[1].getText().trim().equals("")){
			JOptionPane.showMessageDialog(null, "Ohne die Angabe 'verordn. Arzt' kann ein Rezept nicht abgespeichert werden.");
			 SwingUtilities.invokeLater(new Runnable(){
			 	   public  void run()
			 	   {
						jtf[1].requestFocus();
			 	   }
			});	   		
			return false;
		}
		if(jtf[9].getText().trim().equals("")){
			JOptionPane.showMessageDialog(null, "Ohne die Angabe 'Behandlungsdauer' kann ein Rezept nicht abgespeichert werden.");
			 SwingUtilities.invokeLater(new Runnable(){
			 	   public  void run()
			 	   {
						jtf[9].requestFocus();
			 	   }
			});	   		
			return false;
		}
		if(jtf[10].getText().trim().equals("")){
			JOptionPane.showMessageDialog(null, "Ohne die Angabe 'Angelegt von' kann ein Rezept nicht abgespeichert werden.");
			 SwingUtilities.invokeLater(new Runnable(){
			 	   public  void run()
			 	   {
						jtf[10].requestFocus();
			 	   }
			});	   		
			return false;
		}

		return true;
	}
	private void ladePreisliste(String item,int preisgruppe){
		jcmb[2].removeAllItems();
		jcmb[3].removeAllItems();
		jcmb[4].removeAllItems();
		jcmb[5].removeAllItems();
		
		if(item.toLowerCase().contains("physio") ){
			aktuelleDisziplin = "Physio";
			nummer = "kg";
		}else if(item.toLowerCase().contains("massage")){
			aktuelleDisziplin = "Massage";
			nummer = "ma";
		}else if(item.toLowerCase().contains("ergo")){
			aktuelleDisziplin = "Ergo";
			nummer = "er";
		}else if(item.toLowerCase().contains("logo")){
			aktuelleDisziplin = "Logo";
			nummer = "lo";
		}else if(item.toLowerCase().contains("reha")){
			aktuelleDisziplin = "Reha";
			nummer = "rh";
		}		
		preisvec = SystemPreislisten.hmPreise.get(aktuelleDisziplin).get(preisgruppe);
		ladePreise();
	}

	private void fuelleIndis(String item){
		//System.out.println("*******ITEM-Parameter = "+item+"***********");
		if(jcmb[6].getItemCount() > 0){
			jcmb[6].removeAllItems();
		}
		if(item.toLowerCase().contains("reha")){
			return;
		}
		int anz = 0;
		String[] indis = null;
		if(item.toLowerCase().contains("physio") || item.toLowerCase().contains("massage") ){
			anz = Reha.thisClass.patpanel.aktRezept.indphysio.length;
			indis = Reha.thisClass.patpanel.aktRezept.indphysio; 
		}else if(item.toLowerCase().contains("ergo")){
			anz = Reha.thisClass.patpanel.aktRezept.indergo.length;
			indis = Reha.thisClass.patpanel.aktRezept.indergo; 
		}else if(item.toLowerCase().contains("logo")){
			anz = Reha.thisClass.patpanel.aktRezept.indlogo.length;
			indis = Reha.thisClass.patpanel.aktRezept.indlogo; 
		}
		for(int i = 0; i < anz; i++){
			jcmb[6].addItem(indis[i]);
		}
		return;
		/*
		jcmb[2].removeAllItems();
		jcmb[3].removeAllItems();
		jcmb[4].removeAllItems();
		jcmb[5].removeAllItems();
		
		if(item.contains("REHA")){
			//preisvec = ParameterLaden.vRHPreise;
			nummer="rh";
			aktuelleDisziplin = "Reha";
			preisvec = SystemPreislisten.hmPreise.get("Reha").get(preisgruppen[4]);
			preisgruppe = preisgruppen[4];
			jtf[13].setText(Integer.toString(preisgruppe+1));
			ladePreise();			
			return;
		}
		if(item.contains("Physio")){
			nummer="kg";
			int anz = Reha.thisClass.patpanel.aktRezept.indphysio.length;
			for(int i = 0; i < anz; i++){
				jcmb[6].addItem(Reha.thisClass.patpanel.aktRezept.indphysio[i]);
			}
			//preisvec = ParameterLaden.vMAPreise;;
			aktuelleDisziplin = "Physio";
			preisgruppe = preisgruppen[0];
			jtf[13].setText(Integer.toString(preisgruppe+1));			
			preisvec = SystemPreislisten.hmPreise.get("Physio").get(preisgruppen[0]);
			ladePreise();
			return;
		}
		if(item.contains("Massage")){
			nummer="ma";
			int anz = Reha.thisClass.patpanel.aktRezept.indphysio.length;
			for(int i = 0; i < anz; i++){
				jcmb[6].addItem(Reha.thisClass.patpanel.aktRezept.indphysio[i]);
			}
			//preisvec = ParameterLaden.vMAPreise;
			aktuelleDisziplin = "Massage";
			preisgruppe = preisgruppen[1];
			jtf[13].setText(Integer.toString(preisgruppe+1));			
			preisvec = SystemPreislisten.hmPreise.get("Massage").get(preisgruppen[1]);
			ladePreise();
			return;
		}		
		if(item.contains("Ergo")){
			nummer="er";
			int anz = Reha.thisClass.patpanel.aktRezept.indergo.length;
			for(int i = 0; i < anz; i++){
				jcmb[6].addItem(Reha.thisClass.patpanel.aktRezept.indergo[i]);
			}
			//preisvec = ParameterLaden.vERPreise;
			aktuelleDisziplin = "Ergo";
			preisgruppe = preisgruppen[2];
			jtf[13].setText(Integer.toString(preisgruppe+1));			
			preisvec = SystemPreislisten.hmPreise.get("Ergo").get(preisgruppen[2]);
			ladePreise();			
			return;			
		}
		if(item.contains("Logo")){
			nummer="lo";
			int anz = Reha.thisClass.patpanel.aktRezept.indlogo.length;
			for(int i = 0; i < anz; i++){
				jcmb[6].addItem(Reha.thisClass.patpanel.aktRezept.indlogo[i]);
			}
			//preisvec = ParameterLaden.vLOPreise;
			aktuelleDisziplin = "Logo";
			preisgruppe = preisgruppen[3];
			jtf[13].setText(Integer.toString(preisgruppe+1));			
			preisvec = SystemPreislisten.hmPreise.get("Logo").get(preisgruppen[3]);
			ladePreise();
		}
		*/
	}
	public void ladePreise(){
		jcmb[2].setDataVectorWithStartElement(preisvec,0,9,"./.");
		jcmb[3].setDataVectorWithStartElement(preisvec,0,9,"./.");
		jcmb[4].setDataVectorWithStartElement(preisvec,0,9,"./.");
		jcmb[5].setDataVectorWithStartElement(preisvec,0,9,"./.");
		/*
		int anz = preisvec.size();
		System.out.println(preisgruppe);
		System.out.println(preisvec);
		
		jcmb[2].addItem("./.");
		jcmb[3].addItem("./.");
		jcmb[4].addItem("./.");			
		jcmb[5].addItem("./.");						
		for(int i = 0; i < anz; i++ ){
			jcmb[2].addItem(preisvec.get(i).get(0));
			jcmb[3].addItem(preisvec.get(i).get(0));
			jcmb[4].addItem(preisvec.get(i).get(0));			
			jcmb[5].addItem(preisvec.get(i).get(0));			
		}
		*/
		return;		
	}
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getKeyChar()=='?' && ((JComponent)arg0.getSource()).getName().equals("arzt")){
			String[] suchkrit = new String[] {jtf[1].getText().replaceAll("\\?", ""),jtf[12].getText()};
			jtf[1].setText(new String(suchkrit[0]));
			arztAuswahl(suchkrit);
		}
		if(arg0.getKeyChar()=='?' && ((JComponent)arg0.getSource()).getName().equals("ktraeger")){
			String[] suchkrit = new String[] {jtf[0].getText().replaceAll("\\?", ""),jtf[11].getText()};
			jtf[0].setText(suchkrit[0]);
			kassenAuswahl(suchkrit);
		}
		if(arg0.getKeyCode()==27){
			//System.out.println("Escape-Gedr�ckt");
			//arg0.consume();
			//aufraeumen();
			doAbbrechen();
		}
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		if( ((JComponent)arg0.getSource()).getName() != null){
			if( ((JComponent)arg0.getSource()).getName().equals("rez_datum") ){
				return;
			}
			if( ((JComponent)arg0.getSource()).getName().equals("anzahl1") ){
				String text = jtf[4].getText();
				jtf[5].setText(text);
				jtf[6].setText(text);
				jtf[7].setText(text);				
				return;
			}
		}
		
	}
	
	private void arztAuswahl(String[] suchenach){
		jtf[2].requestFocus();
		ArztAuswahl awahl = new ArztAuswahl(null,"ArztAuswahl",suchenach,new JRtaTextField[] {jtf[1],new JRtaTextField("",false),jtf[12]},new String(jtf[1].getText().trim()));
		awahl.setModal(true);
		awahl.setLocationRelativeTo(this);
		awahl.setVisible(true);
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run(){
		 			jtf[2].requestFocus();
		 	   }
		});

		String aneu = "";
		if(! Reha.thisClass.patpanel.patDaten.get(63).contains( ("@"+(aneu = jtf[12].getText().trim())+"@\n")) ){
			final String xaneu = aneu;
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					String msg = "Dieser Arzt ist bislang nicht in der Arztliste dieses Patienten.\n"+
					"Soll dieser Arzt der Ärzteliste des Patienten zugeordnet werden?";
					int frage = JOptionPane.showConfirmDialog(null,msg,"Wichtige Benutzeranfrage",JOptionPane.YES_NO_OPTION);
					if(frage == JOptionPane.YES_OPTION){
						String aliste = Reha.thisClass.patpanel.patDaten.get(63)+ "@"+xaneu+"@\n";
						Reha.thisClass.patpanel.patDaten.set(63,aliste+ "@"+xaneu+"@\n");
						System.out.println("Arztliste in Rezept Neu = "+aliste);
						Reha.thisClass.patpanel.getLogic().arztListeSpeichernString(aliste,false,Reha.thisClass.patpanel.aktPatID);
						SwingUtilities.invokeLater(new Runnable(){
						 	   public  void run(){
						 			jtf[2].requestFocus();
						 	   }
						});
						
					}
					return null;
				}
			}.execute();
		}
		awahl.dispose();
		awahl = null;

	}
	private void kassenAuswahl(String[] suchenach){
		jtf[1].requestFocus();
		KassenAuswahl kwahl = new KassenAuswahl(null,"KassenAuswahl",suchenach,new JRtaTextField[] {jtf[0],jtf[26],jtf[11]},jtf[0].getText().trim());
		kwahl.setModal(true);
		kwahl.setLocationRelativeTo(this);
		kwahl.setVisible(true);
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run(){
		 		   if(jtf[11].getText().equals("")){
		 			   String meldung = "Achtung - kann Preisgruppe nicht ermitteln!\n"+
		 			   "Das bedeutet diese Rezept kann später nicht abgerechnet werden!\n\n"+
		 			   "Und bedenken Sie bitte Ihr Kürzel wird dauerhaft diesem Rezept zugeordnet....";
			 			  JOptionPane.showMessageDialog(null,meldung);		 			   
		 		   }else{
			 		   	holePreisGruppe(jtf[11].getText().trim());
						ladePreisliste(jcmb[0].getSelectedItem().toString().trim(), preisgruppen[jcmb[0].getSelectedIndex()]);
						jtf[1].requestFocus();
		 		   }
		 	   }
		});
		kwahl.dispose();
		kwahl = null;
	}
	private void holePreisGruppeMitWorker(String id){
		System.out.println("in Hole Preisgruppe für KassenId = "+id);
		final String xid = id;
		new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				try{
				Vector<Vector<String>> vec = SqlInfo.holeFelder("select preisgruppe,pgkg,pgma,pger,pglo,pgrh from kass_adr where id='"+xid+"' LIMIT 1");
				//Vector vec = SqlInfo.holeSatz("kass_adr", "preisgruppe,pgkg,pgma,pger,pglo,pgrh", " id='"+Integer.toString(xid)+"'", Arrays.asList(new String[] {}));
				if(vec.size()>0){
					System.out.println("Preisgruppen = "+vec.get(0));
					for(int i = 1; i < vec.get(0).size();i++){
						preisgruppen[i-1] = Integer.parseInt(vec.get(0).get(i))-1;
					}
					preisgruppe = Integer.parseInt((String)vec.get(0).get(0))-1;
					System.out.println("In holePreisGruppe = "+preisgruppe);
					jtf[13].setText((String)vec.get(0).get(0));
					ladePreisliste(jcmb[0].getSelectedItem().toString().trim(), preisgruppen[jcmb[0].getSelectedIndex()]);
					fuelleIndis(jcmb[0].getSelectedItem().toString().trim());
					ladeZusatzDatenNeu();
				}else{
					JOptionPane.showMessageDialog(null,"Achtung - kann Preisgruppe nicht ermitteln - Rezept kann später nicht abgerechnet werden!");
				}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}
			
		}.execute();
	}
	private void holePreisGruppe(String id){
		System.out.println("in Hole Preisgruppe für KassenId = "+id);
		Vector<Vector<String>> vec = SqlInfo.holeFelder("select preisgruppe,pgkg,pgma,pger,pglo,pgrh from kass_adr where id='"+id+"' LIMIT 1");
		if(vec.size()>0){
			for(int i = 1; i < vec.get(0).size();i++){
				preisgruppen[i-1] = Integer.parseInt(vec.get(0).get(i))-1;
			}
			preisgruppe = Integer.parseInt((String)vec.get(0).get(0))-1;
			jtf[13].setText((String)vec.get(0).get(0));
		}else{
			JOptionPane.showMessageDialog(null,"Achtung - kann Preisgruppe nicht ermitteln - Rezept kann später nicht abgerechnet werden!");
		}
	}

	/***********
	 * 
	 * 
	 */
	private void ladeZusatzDatenNeu(){
		String tests = "";
		jtf[0].setText(Reha.thisClass.patpanel.patDaten.get(13));
		jtf[11].setText(Reha.thisClass.patpanel.patDaten.get(68)); //kassenid
		if(jtf[11].getText().trim().equals("")){
			JOptionPane.showMessageDialog(null,"Achtung - kann Preisgruppe nicht ermitteln - Rezept kann später nicht abgerechnet werden!");
		}
		/*
		else{
			
			Vector vec = SqlInfo.holeSatz("kass_adr", "preisgruppe", " id='"+jtf[11].getText()+"'", Arrays.asList(new String[] {}));
			if(vec.size()>0){
				jtf[13].setText((String)vec.get(0));
			}else{
				JOptionPane.showMessageDialog(null,"Achtung - kann Preisgruppe nicht ermitteln!\nFunktion -> ladeZusatzDaten()\n\n"+
						"Bitte informieren Sie sofort den Administrator über diese Fehler-Meldung");
			}
		}
		*/
		jtf[1].setText(Reha.thisClass.patpanel.patDaten.get(25));
		jtf[12].setText(Reha.thisClass.patpanel.patDaten.get(67)); //arztid					
		//tests = Reha.thisClass.patpanel.patDaten.get(31);		// bef_dat = Datum der Befreiung
		jtf[14].setText(Reha.thisClass.patpanel.patDaten.get(44)); //heimbewohn
		jtf[15].setText(Reha.thisClass.patpanel.patDaten.get(30)); //befreit
		jtf[25].setText(Reha.thisClass.patpanel.patDaten.get(48)); //kilometer
		jtf[26].setText(Reha.thisClass.patpanel.patDaten.get(66)); //id von Patient
		jtf[27].setText(Reha.thisClass.patpanel.patDaten.get(29)); //pat_intern von Patient

	}
	/***********
	 * 
	 * 
	 */
	
	private void ladeZusatzDatenAlt(){
		String test = StringTools.NullTest(this.vec.get(36));
		jtf[0].setText(test); //kasse
		test = StringTools.NullTest(this.vec.get(37));
		jtf[11].setText(test);  //kid
		test = StringTools.NullTest(this.vec.get(15));
		jtf[1].setText(test); //arzt
		test = StringTools.NullTest(this.vec.get(16));
		jtf[12].setText(test); //arztid
		test = StringTools.NullTest(this.vec.get(2));
		if(!test.equals("")){
			jtf[2].setText(DatFunk.sDatInDeutsch(test));
		}
		test = StringTools.NullTest(this.vec.get(40));
		if(!test.equals("")){
			jtf[3].setText(DatFunk.sDatInDeutsch(test));
		}
		int itest = StringTools.ZahlTest(this.vec.get(27));
		if(itest >=0){
			jcmb[1].setSelectedIndex(itest);
		}
		test = StringTools.NullTest(this.vec.get(42));
		jcb[0].setSelected((test.equals("T")?true:false));
		test = StringTools.NullTest(this.vec.get(43));
		jcb[1].setSelected((test.equals("T")?true:false));
		test = StringTools.NullTest(this.vec.get(55));
		jcb[2].setSelected((test.equals("T")?true:false));
		test = StringTools.NullTest(this.vec.get(3));
		jtf[4].setText(test);
		test = StringTools.NullTest(this.vec.get(4));
		jtf[5].setText(test);
		test = StringTools.NullTest(this.vec.get(5));
		jtf[6].setText(test);
		test = StringTools.NullTest(this.vec.get(6));
		jtf[7].setText(test);
		itest = StringTools.ZahlTest(this.vec.get(8));
		jcmb[2].setSelectedIndex(leistungTesten(0,itest));
		itest = StringTools.ZahlTest(this.vec.get(9));
		jcmb[3].setSelectedIndex(leistungTesten(1,itest));
		itest = StringTools.ZahlTest(this.vec.get(10));
		jcmb[4].setSelectedIndex(leistungTesten(2,itest));
		itest = StringTools.ZahlTest(this.vec.get(11));
		jcmb[5].setSelectedIndex(leistungTesten(3,itest));
		test = StringTools.NullTest(this.vec.get(52));
		jtf[8].setText(test);
		test = StringTools.NullTest(this.vec.get(47));
		jtf[9].setText(test);
		test = StringTools.NullTest(this.vec.get(44));
		jcmb[6].setSelectedItem(test);
		itest = StringTools.ZahlTest(this.vec.get(46));
		jcmb[7].setSelectedIndex(itest);
		test = StringTools.NullTest(this.vec.get(45));
		jtf[10].setText(test);
		if(!test.trim().equals("")){
			jtf[10].setEnabled(false);				
		}
		jta.setText( StringTools.NullTest(this.vec.get(23)) );
		if(!jtf[11].getText().equals("")){
			holePreisGruppe(jtf[11].getText().trim());				
		}else{
			JOptionPane.showMessageDialog(null, "Ermittlung der Preisgruppen erforderlich");				
		}
		
		jtf[14].setText(Reha.thisClass.patpanel.patDaten.get(44)); //heimbewohn
		jtf[15].setText(Reha.thisClass.patpanel.patDaten.get(30)); //befreit
		jtf[25].setText(Reha.thisClass.patpanel.patDaten.get(48)); //kilometer
		jtf[26].setText(this.vec.get(38)); //id von Patient
		jtf[27].setText(this.vec.get(0)); //pat_intern von Patient
	}
	
	/********************************/
	private Double holePreisDoubleX(String pos,int ipreisgruppe){
		Double dbl = 0.0;
		for(int i = 0; i < preisvec.size();i++){
			if(this.preisvec.get(i).get(0).equals(pos)){
				if(this.preisvec.get(i).get(3).equals("")){
					return dbl;
				}
				return Double.parseDouble(this.preisvec.get(i).get(3));
			}
		}
		return dbl;
	}
	private Double holePreisDouble(String id,int ipreisgruppe){
		Double dbl = 0.0;
		System.out.println("ID des Preises = "+id);
		for(int i = 0; i < preisvec.size();i++){
			if(this.preisvec.get(i).get(9).equals(id)){
				if(this.preisvec.get(i).get(1).equals("")){
					return dbl;
				}
				return Double.parseDouble(this.preisvec.get(i).get(3));
			}
		}
		return dbl;
	}

	/*********************************/
	private String[] holePreis(int ivec,int ipreisgruppe){
		if(ivec > 0){
			int prid = new Integer((String) this.preisvec.get(ivec).get(this.preisvec.get(ivec).size()-1));
			Vector xvec = ((Vector)this.preisvec.get(ivec));
			return new String[] {(String)xvec.get(3),(String)xvec.get(2)};
		}else{
			return new String[] {"0.00",""};
		}
	}
	/***********
	 * 
	 * 
	 */
	/**************************************/
	private void doSpeichernAlt(){
		if(!komplettTest()){
			return;
		}
		String stest = "";
		int itest = -1;
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("update verordn set ktraeger='"+jtf[0].getText()+"', ");
		sbuf.append("kid='"+jtf[11].getText()+"', ");
		sbuf.append("arzt='"+jtf[1].getText()+"', ");
		sbuf.append("arztid='"+jtf[12].getText()+"', ");
		stest = jtf[2].getText().trim();
		if(stest.equals(".  .")){
			stest = DatFunk.sHeute();
		}
		sbuf.append("rez_datum='"+DatFunk.sDatInSQL(stest)+"', ");
		stest = jtf[3].getText().trim();
		if(stest.equals(".  .")){
			stest = DatFunk.sHeute();
		}
		sbuf.append("lastdate='"+DatFunk.sDatInSQL(stest)+"', ");
		sbuf.append("lasteddate='"+DatFunk.sDatInSQL(DatFunk.sHeute())+"', ");
		sbuf.append("lastedit='"+Reha.aktUser+"', ");
		sbuf.append("rezeptart='"+new Integer(jcmb[1].getSelectedIndex()).toString()+"', ");
		sbuf.append("begruendadr='"+(jcb[0].isSelected() ? "T" : "F")+"', ");
		sbuf.append("hausbes='"+(jcb[1].isSelected() ? "T" : "F")+"', ");
		sbuf.append("arztbericht='"+(jcb[2].isSelected() ? "T" : "F")+"', ");
		sbuf.append("anzahl1='"+jtf[4].getText()+"', ");		
		sbuf.append("anzahl2='"+jtf[5].getText()+"', ");
		sbuf.append("anzahl3='"+jtf[6].getText()+"', ");
		sbuf.append("anzahl4='"+jtf[7].getText()+"', ");
		itest = jcmb[2].getSelectedIndex();
		if(itest > 0){
			sbuf.append("art_dbeh1='"+preisvec.get(itest-1).get(9)+"', ");
		}else{
			sbuf.append("art_dbeh1='0', ");
		}
		itest = jcmb[3].getSelectedIndex();
		if(itest > 0){
			sbuf.append("art_dbeh2='"+preisvec.get(itest-1).get(9)+"', ");
		}else{
			sbuf.append("art_dbeh2='0', ");
		}
		itest = jcmb[4].getSelectedIndex();
		if(itest > 0){
			sbuf.append("art_dbeh3='"+preisvec.get(itest-1).get(9)+"', ");
		}else{
			sbuf.append("art_dbeh3='0', ");
		}
		itest = jcmb[5].getSelectedIndex();
		if(itest > 0){
			sbuf.append("art_dbeh4='"+preisvec.get(itest-1).get(9)+"', ");
		}else{
			sbuf.append("art_dbeh4='0', ");
		}
		sbuf.append("frequenz='"+jtf[8].getText()+"', ");
		sbuf.append("dauer='"+jtf[9].getText()+"', ");
		if(jcmb[6].getSelectedIndex() > 0){
			sbuf.append("indikatschl='"+(String)jcmb[6].getSelectedItem()+"', ");			
		}else{
			sbuf.append("indikatschl='"+"kein IndiSchl."+"', ");			
		}
		sbuf.append("barcodeform='"+new Integer(jcmb[7].getSelectedIndex()).toString()+"', ");
		sbuf.append("angelegtvon='"+jtf[10].getText()+"', ");
		sbuf.append("preisgruppe='"+jtf[13].getText()+"', ");
		
		if(jcmb[8].getSelectedIndex() > 0){
			sbuf.append("farbcode='"+new Integer(14+jcmb[8].getSelectedIndex()-1).toString()+"', ");	
		}else{
			sbuf.append("farbcode='-1', ");
		}
		
		

		//System.out.println("Speichern bestehendes Rezept -> Preisgruppe = "+jtf[13].getText());
		Integer izuzahl = new Integer(jtf[13].getText());
		String szzstatus = "";
		String unter18 = "F";
		for(int i = 0; i < 1;i++){
			//if(SystemConfig.vZuzahlRegeln.get(izuzahl-1) <= 0){
			if(SystemPreislisten.hmZuzahlRegeln.get(aktuelleDisziplin).get(izuzahl-1) <= 0){
				System.out.println("ZuzahlStatus = Zuzahlung nicht erforderlich");
				szzstatus = "0";
				break;
			}
			if(aktuelleDisziplin.equals("Reha")){
				szzstatus = "0";
				break;
			}
			//System.out.println("ZuzahlStatus = Zuzahlung (zunächst) erforderlich, prüfe ob befreit oder unter 18");
			if(Reha.thisClass.patpanel.patDaten.get(30).equals("T")){
				System.out.println("aktuelles Jahr ZuzahlStatus = Patient ist befreit");
				//laut Patientenstamm befreit aber evtl. noch nicht für dieses Rezept.
				//deshalb prüfen ob bereits bezahlt;
				
				//bereits bezahlt deshalb Status = 1 == bezahlt
				if(this.vec.get(14).equals("T")){
					szzstatus = "1";
				}else{
					
					if(RezTools.mitJahresWechsel(DatFunk.sDatInDeutsch(this.vec.get(2)))){
						
						String vorjahr = Reha.thisClass.patpanel.patDaten.get(69); 
						if(vorjahr.trim().equals("")){
							if(this.vec.get(34).indexOf(vorjahr)>=0){
								szzstatus = "2";
							}else{
								szzstatus = "0";
							}
						}else{
							szzstatus = "0";
						}
						System.out.println("Über den jahreswechsel....");
					}else{
						szzstatus = "0";
					}
					//Im Patientenstamm liegt eine aktuelle befreiung vor  
					//testen ob sich das Rezept über den Jahreswechsel erstreckt
					//wenn ja war er damals auch befreit, wenn ja Status == 0
					//wenn nein Status == 2 == nicht befreit und nicht bezahlt
					//szzstatus = "0";				
				}
				break;
			}
			
			if(DatFunk.Unter18(DatFunk.sHeute(), DatFunk.sDatInDeutsch(Reha.thisClass.patpanel.patDaten.get(4)))){
				System.out.println("ZuzahlStatus = Patient ist unter 18 also befreit...");
				int aj = Integer.parseInt(SystemConfig.aktJahr)-18;
				String gebtag = DatFunk.sHeute().substring(0,6)+Integer.toString(aj);
				long tage = DatFunk.TageDifferenz(DatFunk.sDatInDeutsch(Reha.thisClass.patpanel.patDaten.get(4)) ,gebtag);

				System.out.println("Differenz in Tagen = "+tage);
				System.out.println("Geburtstag = "+gebtag);
				
				if(tage < 0 && tage >= -45){
					JOptionPane.showMessageDialog(null ,"Achtung es sind noch "+(tage*-1)+" Tage bis zur Volljährigkeit\n"+
							"Unter Umständen wechselt der Zuzahlungsstatus im Verlauf dieses Rezeptes");
					szzstatus = "3";
				}else{
					szzstatus = "0";
				}
				//szzstatus = "0";
				unter18 = "T";
				break;
			}
			
			if(this.vec.get(14).equals("T") || 
					(new Double((String)this.vec.get(13)) > 0.00) ){
				szzstatus = "1";
			}else{
				szzstatus = "2";				
			}
		}
		
		sbuf.append("zzstatus='"+szzstatus+"', ");
		int leistung;
		String[] str;
		int xid;
		for(int i = 0;i < 4;i++){
			xid = ( jcmb[i+2].getSelectedIndex()-1);
			if(xid >= 0){
				leistung = leistungTesten(i, Integer.parseInt( (String)preisvec.get(xid).get(9)) ) ;	
			}else{
				leistung = -1;
			}
			if(leistung >= 0){
				str = holePreis(xid,new Integer(jtf[13].getText()) );
				sbuf.append("preise"+(i+1)+"='"+str[0]+"', ");
				sbuf.append("pos"+(i+1)+"='"+str[1]+"', ");
			}else{
				sbuf.append("preise"+(i+1)+"='0.00', ");
				sbuf.append("pos"+(i+1)+"='', ");
			}
		}
		sbuf.append("diagnose='"+StringTools.Escaped(jta.getText())+"', ");
		sbuf.append("jahrfrei='"+Reha.thisClass.patpanel.patDaten.get(69)+"', ");
		sbuf.append("heimbewohn='"+jtf[14].getText()+"', ");
		sbuf.append("hbvoll='"+(jcb[5].isSelected() ? "T" : "F")+"', ");
		sbuf.append("zzregel='"+SystemPreislisten.hmZuzahlRegeln.get(aktuelleDisziplin).get(Integer.parseInt(jtf[13].getText())-1 )+"'");
		sbuf.append(" where id='"+this.vec.get(35)+"'");

		SqlInfo.sqlAusfuehren(sbuf.toString());
		((JXDialog)this.getParent().getParent().getParent().getParent().getParent()).setVisible(false);
		aufraeumen();
		((JXDialog)this.getParent().getParent().getParent().getParent().getParent()).dispose();
		System.out.println("Rezept wurde mit Preisgruppe "+jtf[13].getText()+" gespeichert");
	}
	/**************************************/
	/**************************************/
	/**************************************/
	/**************************************/
	/**************************************/
	private void doSpeichernNeu(){
		int reznr = -1;
		if(!komplettTest()){
			//System.out.println("Komplett-Test fehlgeschlagen");
			return;
		}
		String stest = "";
		int itest = -1;
		StringBuffer sbuf = new StringBuffer();
		
		reznr = SqlInfo.erzeugeNummer(nummer);
		if(reznr < 0){
			JOptionPane.showMessageDialog(null,"Schwerwiegender Fehler beim Bezug einer neuen Rezeptnummer!");
			return;
		}
		int rezidneu = SqlInfo.holeId("verordn", "diagnose");
		sbuf.append("update verordn set rez_nr='"+nummer.toUpperCase()+
				new Integer(reznr).toString()+"', ");
		sbuf.append("pat_intern='"+jtf[27].getText()+"', ");
		sbuf.append("patid='"+jtf[26].getText()+"', ");
		sbuf.append("ktraeger='"+jtf[0].getText()+"', ");
		sbuf.append("kid='"+jtf[11].getText()+"', ");
		sbuf.append("arzt='"+jtf[1].getText()+"', ");
		sbuf.append("arztid='"+jtf[12].getText()+"', ");
		stest = DatFunk.sHeute();
		sbuf.append("datum='"+DatFunk.sDatInSQL(stest)+"', ");
		stest = jtf[2].getText().trim();
		if(stest.equals(".  .")){
			stest = DatFunk.sHeute();
		}
		sbuf.append("rez_datum='"+DatFunk.sDatInSQL(stest)+"', ");
		stest = jtf[3].getText().trim();
		if(stest.equals(".  .")){
			stest = DatFunk.sHeute();
		}
		sbuf.append("lastdate='"+DatFunk.sDatInSQL(stest)+"', ");
		sbuf.append("lasteddate='"+DatFunk.sDatInSQL(DatFunk.sHeute())+"', ");
		sbuf.append("lastedit='"+Reha.aktUser+"', ");
		sbuf.append("rezeptart='"+new Integer(jcmb[1].getSelectedIndex()).toString()+"', ");
		sbuf.append("begruendadr='"+(jcb[0].isSelected() ? "T" : "F")+"', ");
		sbuf.append("hausbes='"+(jcb[1].isSelected() ? "T" : "F")+"', ");
		sbuf.append("arztbericht='"+(jcb[2].isSelected() ? "T" : "F")+"', ");
		sbuf.append("anzahl1='"+jtf[4].getText()+"', ");		
		sbuf.append("anzahl2='"+jtf[5].getText()+"', ");
		sbuf.append("anzahl3='"+jtf[6].getText()+"', ");
		sbuf.append("anzahl4='"+jtf[7].getText()+"', ");
		sbuf.append("anzahlhb='"+jtf[4].getText()+"', ");
		itest = jcmb[2].getSelectedIndex();
		if(itest > 0){
			sbuf.append("art_dbeh1='"+preisvec.get(itest-1).get(9)+"', ");
		}else{
			sbuf.append("art_dbeh1='0', ");
		}
		itest = jcmb[3].getSelectedIndex();
		if(itest > 0){
			sbuf.append("art_dbeh2='"+preisvec.get(itest-1).get(9)+"', ");
		}else{
			sbuf.append("art_dbeh2='0', ");
		}
		itest = jcmb[4].getSelectedIndex();
		if(itest > 0){
			sbuf.append("art_dbeh3='"+preisvec.get(itest-1).get(9)+"', ");
		}else{
			sbuf.append("art_dbeh3='0', ");
		}
		itest = jcmb[5].getSelectedIndex();
		if(itest > 0){
			sbuf.append("art_dbeh4='"+preisvec.get(itest-1).get(9)+"', ");
		}else{
			sbuf.append("art_dbeh4='0', ");
		}
		sbuf.append("frequenz='"+jtf[8].getText()+"', ");
		sbuf.append("dauer='"+jtf[9].getText()+"', ");
		if(jcmb[6].getSelectedIndex() > 0){
			sbuf.append("indikatschl='"+(String)jcmb[6].getSelectedItem()+"', ");			
		}else{
			sbuf.append("indikatschl='"+"kein IndiSchl."+"', ");			
		}
		sbuf.append("barcodeform='"+new Integer(jcmb[7].getSelectedIndex()).toString()+"', ");
		sbuf.append("angelegtvon='"+jtf[10].getText()+"', ");
		sbuf.append("preisgruppe='"+jtf[13].getText()+"', ");
		if(jcmb[8].getSelectedIndex() > 0){
			sbuf.append("farbcode='"+new Integer(14+jcmb[8].getSelectedIndex()-1).toString()+"', ");	
		}else{
			sbuf.append("farbcode='-1', ");
		}
		
/*******************************************/		
		Integer izuzahl = new Integer(jtf[13].getText());
		String unter18 = "F";
		String szzstatus = "";
		for(int i = 0; i < 1;i++){
			//if(SystemConfig.vZuzahlRegeln.get(izuzahl-1) <= 0){
			if(SystemPreislisten.hmZuzahlRegeln.get(aktuelleDisziplin).get(izuzahl-1) <= 0){	
				System.out.println("1. ZuzahlStatus = Zuzahlung nicht erforderlich");
				szzstatus = "0";
				break;
			}
			if(nummer.equalsIgnoreCase("rh")){
				szzstatus = "0";
				break;
			}
			//System.out.println("ZuzahlStatus = Zuzahlung (zun�chst) erforderlich, pr�fe ob befreit oder unter 18");
			if(Reha.thisClass.patpanel.patDaten.get(30).equals("T")){
				System.out.println("2. ZuzahlStatus = Patient ist befreit");
				szzstatus = "0";				
				break;
			}
			if(DatFunk.Unter18(DatFunk.sHeute(), DatFunk.sDatInDeutsch(Reha.thisClass.patpanel.patDaten.get(4)))){
				//System.out.println("ZuzahlStatus = Patient ist unter 18 also befreit...");
				String gebtag = DatFunk.sHeute().substring(0,6)+new Integer(new Integer(SystemConfig.aktJahr)-18).toString();
				long tage = DatFunk.TageDifferenz(DatFunk.sDatInDeutsch(Reha.thisClass.patpanel.patDaten.get(4)) ,gebtag);

				System.out.println("Differenz in Tagen = "+tage);
				System.out.println("Geburtstag = "+gebtag);
				
				if(tage < 0 && tage >= -45){
					JOptionPane.showMessageDialog(null ,"Achtung es sind noch "+(tage*-1)+" Tage bis zur Volljährigkeit\n"+
							"Unter Umständen wechselt der Zuzahlungsstatus im Verlauf dieses Rezeptes");
					szzstatus = "3";
				}else{
					szzstatus = "0";
				}
				//szzstatus = "0";
				unter18 = "T";
				break;
			}
			//System.out.println("Normale Zuzahlung -> status noch nicht bezahlt");
			szzstatus = "2";				
		}
		sbuf.append("zzstatus='"+szzstatus+"', ");
		int leistung;
		String[] str;
		int xid;
		for(int i = 0;i < 4;i++){
			xid = ( jcmb[i+2].getSelectedIndex()-1);
			if(xid >= 0){
				leistung = leistungTesten(i, Integer.parseInt( (String)preisvec.get(xid).get(9)) ) ;	
			}else{
				leistung = -1;
			}
			if(leistung >= 0){
				str = holePreis(xid,preisgruppe );
				sbuf.append("preise"+(i+1)+"='"+str[0]+"', ");
				sbuf.append("pos"+(i+1)+"='"+str[1]+"', ");
			}else{
				sbuf.append("preise"+(i+1)+"='0.00', ");
				sbuf.append("pos"+(i+1)+"='', ");
			}
		}
		sbuf.append("diagnose='"+StringTools.Escaped(jta.getText())+"', ");
		sbuf.append("unter18='"+unter18+"', ");
		sbuf.append("jahrfrei='"+Reha.thisClass.patpanel.patDaten.get(69)+"', ");
		sbuf.append("heimbewohn='"+jtf[14].getText()+"', ");
		sbuf.append("hbvoll='"+(jcb[5].isSelected() ? "T" : "F")+"', ");
		sbuf.append("befr='"+Reha.thisClass.patpanel.patDaten.get(30)+"', ");
		sbuf.append("zzregel='"+SystemPreislisten.hmZuzahlRegeln.get(aktuelleDisziplin).get(new Integer(jtf[13].getText())-1 )+"'");
		sbuf.append("where id='"+Integer.toString(rezidneu)+"' ");
		SqlInfo.sqlAusfuehren(sbuf.toString());
		System.out.println("Rezept wurde mit Preisgruppe "+jtf[13].getText()+" gespeichert");
		final int xreznr = reznr;
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				Reha.thisClass.patpanel.aktRezept.holeRezepte(jtf[27].getText(),nummer.toUpperCase()+Integer.toString(xreznr));
				return null;
			}
		}.execute();
		((JXDialog)this.getParent().getParent().getParent().getParent().getParent()).setVisible(false);
		aufraeumen();
		((JXDialog)this.getParent().getParent().getParent().getParent().getParent()).dispose();
		
	}
	private void doAbbrechen(){
		aufraeumen();
		((JXDialog)this.getParent().getParent().getParent().getParent().getParent()).dispose();		
	}

	@Override
	public void rehaTPEventOccurred(RehaTPEvent evt) {
		//System.out.println("****************Setze alles auf null in RezNeuanlage**************");				

			try{
				if(evt.getDetails()[0] != null){
					if(evt.getDetails()[0].equals(this.getName())){
						System.out.println("****************Setze alles auf null in RezNeuanlage**************");
						System.out.println("Event=****************"+evt);
						this.setVisible(false);
						rtp.removeRehaTPEventListener((RehaTPEventListener) this);
						rtp = null;
						aufraeumen();
					}
				}
			}catch(NullPointerException ne){
				System.out.println("In RezeptNeuanlage" +evt);
			}
	}	

	public void aufraeumen(){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				for(int i = 0; i < jtf.length;i++){
					ListenerTools.removeListeners(jtf[i]);
				}
				for(int i = 0; i < jcb.length;i++){
					ListenerTools.removeListeners(jcb[i]);
				}
				for(int i = 0; i < jcmb.length;i++){
					ListenerTools.removeListeners(jcmb[i]);
				}
				ListenerTools.removeListeners(jta);
				ListenerTools.removeListeners(getInstance());
				if(rtp != null){
					rtp.removeRehaTPEventListener((RehaTPEventListener) getInstance());
					rtp = null;
				}

				return null;
		}
		}.execute();
	}	
}
