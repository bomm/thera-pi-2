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
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import sqlTools.ExUndHop;
import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemTools.Colors;
import systemTools.JCompTools;
import systemTools.JRtaCheckBox;
import systemTools.JRtaComboBox;
import systemTools.JRtaRadioButton;
import systemTools.JRtaTextField;
import systemTools.StringTools;
import terminKalender.ParameterLaden;
import terminKalender.datFunk;

public class RezNeuanlage extends JXPanel implements ActionListener, KeyListener,FocusListener{

	/**
	 * 
	 */
	public JRtaTextField[] jtf = {null,null,null,null,null,
								  null,null,null,null,null,
								  null,null,null,null,null,
								  null,null,null,null,null,
								  null,null,null,null,null,
								  null,null,null,null,null};
	public JRtaCheckBox[] jcb = {null,null,null,null,null};
	
	//public JRtaRadioButton[] jrb = {null,null,null,null,null}; 
	public JRtaComboBox[] jcmb = {null,null,null,null,null,null,null,null,null};
	
	public JTextArea jta = null;
	
	public JButton speichern = null;
	public JButton abbrechen = null;
	
	public boolean neu = false;
	public String feldname = "";
	public Vector<String> vec = null;
	public Vector<ArrayList> preisvec = null;
	private boolean klassenReady = false;
	private boolean initReady = false;
	private static final long serialVersionUID = 1L;
	private int preisgruppe;
	public boolean feldergefuellt = false;
	private String nummer = null;
	private String[] farbcodes = {null,null,null,null,null,null,null,null,null,null};
	public RezNeuanlage(Vector<String> vec,boolean neu,String sfeldname){
 	
		
		super();
		this.neu = neu;
		this.feldname = sfeldname;
		this.vec = vec;

		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				macheFarbcodes();
				return null;
			}
		}.execute();

		
		setLayout(new BorderLayout());
		setOpaque(true);
		setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		add(getDatenPanel(),BorderLayout.CENTER);
		add(getButtonPanel(),BorderLayout.SOUTH);
		new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				// TODO Auto-generated method stub
				Point2D start = new Point2D.Float(0, 0);
			     Point2D end = new Point2D.Float(PatGrundPanel.thisClass.getWidth(),100);
			     float[] dist = {0.0f, 0.75f};
			     Color[] colors = {Color.WHITE,Colors.Yellow.alpha(0.05f)};
			     LinearGradientPaint p =
			         new LinearGradientPaint(start, end, dist, colors);
			     MattePainter mp = new MattePainter(p);
			     setBackgroundPainter(new CompoundPainter(mp));		
				return null;
			}
			
		}.execute();		

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
		for (int i = 0;i < 9;i++){
			farbcodes[i+1] = SystemConfig.vSysColsBedeut.get(i+14);
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
		 				  ((JXDialog)getParent().getParent().getParent().getParent().getParent()).dispose();
		 			   }else{
			 			   holePreisGruppe(new Integer(jtf[11].getText()));
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
		 				   jtf[11].setText(new Integer(PatGrundPanel.thisClass.kid).toString());
		 				   jtf[12].setText(new Integer(PatGrundPanel.thisClass.aid).toString());
		 				   jtf[0].setText(PatGrundPanel.thisClass.patDaten.get(13));
		 				   holePreisGruppe(new Integer(PatGrundPanel.thisClass.kid));
		 			   }else if(kid >= 0 && aid < 0){
		 				   jtf[12].setText(new Integer(PatGrundPanel.thisClass.aid).toString());
		 			   }else if(kid < 0 && aid >= 0){
		 				   jtf[11].setText(new Integer(PatGrundPanel.thisClass.kid).toString());
		 				   jtf[0].setText(PatGrundPanel.thisClass.patDaten.get(13));
		 				   holePreisGruppe(new Integer(PatGrundPanel.thisClass.kid));		 				   
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
			       //1.   2.   3.   4.   5.   6   7   8    9   10   11  12  13  14    15   16   17  18   19   20   21  22   23       
					"p, 10dlu, p, 5dlu,  p, 5dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, " +
		//24   25   26   27  28   29   30    31   32   33   34    35	 36	  37			
		"10dlu, p, 10dlu, p, 2dlu, p, 2dlu,  p,  10dlu, p, 10dlu, 40dlu,2dlu,2dlu");
					

		CellConstraints cc = new CellConstraints();
		PanelBuilder jpan = new PanelBuilder(lay);
		jpan.setDefaultDialogBorder();
		jpan.getPanel().setOpaque(false);
		String ywerte = "";
		
		/*
		jcmb[0] = new JRtaComboBox(new String[] {"Physio-Rezept","Massage/Lymphdrainage-Rezept",
				"Ergotherapie-Rezept","Logopädie-Rezept","REHA-Verordnung"});
		 */
		/*
		JLabel rezlbl = new JLabel("KG60123");
		Font fontreznr = new Font("Tahoma",Font.BOLD,16);
		rezlbl.setFont(fontreznr);
		jpan.add(rezlbl,cc.xyw(3, 1,3));
		*/
		jtf[0] = new JRtaTextField("NIX",false); // kasse/kostenträger
		jtf[1] = new JRtaTextField("NIX",false); // arzt
		jtf[2] = new JRtaTextField("DATUM",true); // rezeptdatum
		jtf[3] = new JRtaTextField("DATUM",true); // spätester beginn
		jtf[4] = new JRtaTextField("ZAHLEN",true); // Anzahl 1
		jtf[5] = new JRtaTextField("ZAHLEN",true); // Anzahl 2
		jtf[6] = new JRtaTextField("ZAHLEN",true); // Anzahl 3
		jtf[7] = new JRtaTextField("ZAHLEN",true); // Anzahl 4
		jtf[8] = new JRtaTextField("GROSS",true); // Frequenz		
		jtf[9] = new JRtaTextField("GROSS",true); // Dauer		
		jtf[10] = new JRtaTextField("GROSS",true); // angelegt von
		jtf[11] = new JRtaTextField("GROSS",false); //kassenid
		jtf[12] = new JRtaTextField("GROSS",false); //arztid
		// ************alles nachfolgende muß noch eingebaut werden.....
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
		/*
		if(this.neu){
			ladeZusatzDaten();
		}
		*/
		jcmb[0] = new JRtaComboBox(SystemConfig.rezeptKlassen);
		jpan.addLabel("Rezeptklasse auswählen",cc.xy(1, 3));
		jpan.add(jcmb[0],cc.xyw(3, 3,5));
		jcmb[0].setActionCommand("rezeptklasse");
		jcmb[0].addActionListener(this);
		if(this.neu){
			jcmb[0].setSelectedItem(SystemConfig.initRezeptKlasse);
		}else{
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
		jpan.addLabel("Hausbesuch",cc.xy(1, 13));
		jpan.add(jcb[1],cc.xy(3, 13));

		jcb[2] = new JRtaCheckBox("angefordert");
		jcb[2].setOpaque(false);
		jpan.addLabel("Therapiebericht",cc.xy(5, 13));
		jpan.add(jcb[2],cc.xy(7, 13));
		
		jpan.addSeparator("Verordnete Heilmittel", cc.xyw(1,15,7));

		jtf[4].setName("anzahl1");
		jtf[4].addFocusListener(this);
		jpan.addLabel("Anzahl / Heilmittel 1",cc.xy(1, 17));
		jpan.add(jtf[4],cc.xy(3, 17));
		jcmb[2] = new JRtaComboBox();
		jcmb[2].setActionCommand("leistung1");
		jcmb[2].addActionListener(this);
		jpan.add(jcmb[2],cc.xyw(5, 17,3));
		
		jpan.addLabel("Anzahl / Heilmittel 2",cc.xy(1, 19));
		jpan.add(jtf[5],cc.xy(3, 19));
		jcmb[3] = new JRtaComboBox();
		jcmb[3].setActionCommand("leistung2");
		jcmb[3].addActionListener(this);
		jpan.add(jcmb[3],cc.xyw(5, 19,3));
		
		jpan.addLabel("Anzahl / Heilmittel 3",cc.xy(1, 21));
		jpan.add(jtf[6],cc.xy(3, 21));
		jcmb[4] = new JRtaComboBox();
		jcmb[4].setActionCommand("leistung3");
		jcmb[4].addActionListener(this);
		jpan.add(jcmb[4],cc.xyw(5, 21,3));

		jpan.addLabel("Anzahl / Heilmittel 4",cc.xy(1, 23));
		jpan.add(jtf[7],cc.xy(3, 23));
		jcmb[5] = new JRtaComboBox();
		jcmb[5].setActionCommand("leistung4");
		jcmb[5].addActionListener(this);
		jpan.add(jcmb[5],cc.xyw(5, 23,3));
		
		jpan.addSeparator("Durchführungsbestimmungen", cc.xyw(1,25,7));
		
		jpan.addLabel("Behandlungsfrequenz",cc.xy(1, 27));		
		jpan.add(jtf[8],cc.xy(3, 27));	

		jpan.addLabel("Dauer der Behandl. in Min.",cc.xy(5, 27));
		jpan.add(jtf[9],cc.xy(7, 27));

		
		jpan.addLabel("Indikationsschlüssel",cc.xy(1, 29));		
		jcmb[6] = new JRtaComboBox();
		jpan.add(jcmb[6],cc.xy(3, 29));
		
		klassenReady = true;
		fuelleIndis((String)jcmb[0].getSelectedItem());		
		/*
		if(this.neu){
			klassenReady = true;
			fuelleIndis((String)jcmb[0].getSelectedItem());			
		}else{
			klassenReady = true;
			fuelleIndis((String)jcmb[0].getSelectedItem());			
		}
		*/
		jpan.addLabel("Barcode-Format",cc.xy(5, 29));
		jcmb[7] = new JRtaComboBox(new String[] {"Muster 13/18","Muster 14","DIN A6-Format","DIN A4(BGE)","DIN A4 (REHA)"});
		jpan.add(jcmb[7],cc.xy(7, 29));
		
		jpan.addLabel("FarbCode im TK",cc.xy(1, 31));
		jcmb[8] = new JRtaComboBox(farbcodes);
		jpan.add(jcmb[8],cc.xy(3, 31));
		jpan.addLabel("Angelegt von",cc.xy(5, 31));
		jpan.add(jtf[10],cc.xy(7, 31));
		
		
		jpan.addSeparator("Ärztliche Diagnose laut Verordnung", cc.xyw(1,33,7));
		
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
		jpan.add(span,cc.xywh(1, 35,7,2));
		JScrollPane jscr = JCompTools.getTransparentScrollPane(jpan.getPanel());
		jscr.getVerticalScrollBar().setUnitIncrement(15);
		if(this.neu){
			ladeZusatzDatenNeu();
		}else{
			ladeZusatzDatenAlt();
		}

		
		jscr.validate();
		return jscr;
		
	}
	public int leistungTesten(int veczahl){
		int retwert = 0;
		if(veczahl==-1 || veczahl==0){
			return retwert;
		}
		if(veczahl <= preisvec.size()){
			int idtest =  new Integer( (String) ((ArrayList)preisvec.get(veczahl-1)).get(35) );
			if(idtest == veczahl){
				return veczahl;
			}
		}
		for(int i = 0;i<preisvec.size();i++){
			if( new Integer( (String) ((ArrayList)preisvec.get(i)).get(35)) == veczahl ){
				return veczahl;
			}
		}
		
		return retwert;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("rezeptklasse") && klassenReady){
			fuelleIndis((String)jcmb[0].getSelectedItem());
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
			if(this.neu){
				doSpeichernNeu();
			}else{
				doSpeichernAlt();
			}
			return;
		}
		/*********************/
		if(e.getActionCommand().equals("abbrechen") ){
			doAbbrechen();
			return;
		}
		/*********************/		
		if(e.getActionCommand().contains("leistung") && initReady){
			int lang = e.getActionCommand().length();
			doRechnen( new Integer( e.getActionCommand().substring(lang-1 ) ) );
			return; 
		}
		
	}
	private void doRechnen(int comb){
		//System.out.println("Betroffen = Leistungscombo Nr. - "+comb);
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

	private void fuelleIndis(String item){
		if(jcmb[6].getItemCount() > 0){
			jcmb[6].removeAllItems();

		}
		jcmb[2].removeAllItems();
		jcmb[3].removeAllItems();
		jcmb[4].removeAllItems();
		jcmb[5].removeAllItems();
		
		if(item.contains("REHA")){
			preisvec = ParameterLaden.vRHPreise;
			nummer="rh";
			ladePreise();			
			return;
		}
		if(item.contains("Physio")){
			nummer="kg";
			int anz = AktuelleRezepte.aktRez.indphysio.length;
			for(int i = 0; i < anz; i++){
				jcmb[6].addItem(AktuelleRezepte.aktRez.indphysio[i]);
			}
			preisvec = ParameterLaden.vKGPreise;
			ladePreise();
			return;
		}
		if(item.contains("Massage")){
			nummer="ma";
			int anz = AktuelleRezepte.aktRez.indphysio.length;
			for(int i = 0; i < anz; i++){
				jcmb[6].addItem(AktuelleRezepte.aktRez.indphysio[i]);
			}
			preisvec = ParameterLaden.vMAPreise;
			ladePreise();
			return;
		}		
		if(item.contains("Ergo")){
			nummer="er";
			int anz = AktuelleRezepte.aktRez.indergo.length;
			for(int i = 0; i < anz; i++){
				jcmb[6].addItem(AktuelleRezepte.aktRez.indergo[i]);
			}
			preisvec = ParameterLaden.vERPreise;
			ladePreise();			
			return;			
		}
		if(item.contains("Logo")){
			nummer="lo";
			int anz = AktuelleRezepte.aktRez.indlogo.length;
			for(int i = 0; i < anz; i++){
				jcmb[6].addItem(AktuelleRezepte.aktRez.indlogo[i]);
			}
			preisvec = ParameterLaden.vLOPreise;
			ladePreise();
		}
	}
	public void ladePreise(){
		int anz = preisvec.size();
		jcmb[2].addItem("./");
		jcmb[3].addItem("./");
		jcmb[4].addItem("./");			
		jcmb[5].addItem("./");						
		for(int i = 0; i < anz; i++ ){
			jcmb[2].addItem(preisvec.get(i).get(0));
			jcmb[3].addItem(preisvec.get(i).get(0));
			jcmb[4].addItem(preisvec.get(i).get(0));			
			jcmb[5].addItem(preisvec.get(i).get(0));			
		}			
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
			jtf[0].setText(new String(suchkrit[0]));
			kassenAuswahl(suchkrit);
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
		awahl.dispose();
		awahl = null;

	}
	private void kassenAuswahl(String[] suchenach){
		jtf[1].requestFocus();
		KassenAuswahl kwahl = new KassenAuswahl(null,"KassenAuswahl",suchenach,new JRtaTextField[] {jtf[0],jtf[26],jtf[11]},new String(jtf[0].getText().trim()));
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
			 		   	holePreisGruppe(new Integer(jtf[11].getText()));
			 			jtf[1].requestFocus();
		 		   }
		 	   }
		});
		kwahl.dispose();
		kwahl = null;
	}
	private void holePreisGruppe(int id){
		final int xid = id;
		new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				Vector vec = SqlInfo.holeSatz("kass_adr", "preisgruppe", " id='"+new Integer(xid).toString()+"'", Arrays.asList(new String[] {}));
				if(vec.size()>0){
					jtf[13].setText((String)vec.get(0));
				}else{
					JOptionPane.showMessageDialog(null,"Achtung - kann Preisgruppe nicht ermitteln - Rezept kann später nicht abgerechnet werden!");
				}
				return null;
			}
			
		}.execute();
	}
	/***********
	 * 
	 * 
	 */
	private void ladeZusatzDatenNeu(){
		String tests = "";
		jtf[0].setText(PatGrundPanel.thisClass.patDaten.get(13));
		jtf[11].setText(PatGrundPanel.thisClass.patDaten.get(68)); //kassenid
		if(jtf[11].getText().trim().equals("")){
			//JOptionPane.showMessageDialog(null,"Achtung - kann Preisgruppe nicht ermitteln - Rezept kann später nicht abgerechnet werden!");
		}else{
			Vector vec = SqlInfo.holeSatz("kass_adr", "preisgruppe", " id='"+jtf[11].getText()+"'", Arrays.asList(new String[] {}));
			if(vec.size()>0){
				jtf[13].setText((String)vec.get(0));
			}else{
				JOptionPane.showMessageDialog(null,"Achtung - kann Preisgruppe nicht ermitteln!\nFunktion -> ladeZusatzDaten()\n\n"+
						"Bitte informieren Sie sofort den Administrator über diese Fehler-Meldung");
			}
		}
		jtf[1].setText(PatGrundPanel.thisClass.patDaten.get(25));
		jtf[12].setText(PatGrundPanel.thisClass.patDaten.get(67)); //arztid					
		//tests = PatGrundPanel.thisClass.patDaten.get(31);		// bef_dat = Datum der Befreiung
		jtf[14].setText(PatGrundPanel.thisClass.patDaten.get(44)); //heimbewohn
		jtf[15].setText(PatGrundPanel.thisClass.patDaten.get(30)); //befreit
		jtf[25].setText(PatGrundPanel.thisClass.patDaten.get(48)); //kilometer
		jtf[26].setText(PatGrundPanel.thisClass.patDaten.get(66)); //id von Patient
		jtf[27].setText(PatGrundPanel.thisClass.patDaten.get(29)); //pat_intern von Patient

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
			jtf[2].setText(datFunk.sDatInDeutsch(test));
		}
		test = StringTools.NullTest(this.vec.get(40));
		if(!test.equals("")){
			jtf[3].setText(datFunk.sDatInDeutsch(test));
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
		jcmb[2].setSelectedIndex(leistungTesten(itest));
		itest = StringTools.ZahlTest(this.vec.get(9));
		jcmb[3].setSelectedIndex(leistungTesten(itest));
		itest = StringTools.ZahlTest(this.vec.get(10));
		jcmb[4].setSelectedIndex(leistungTesten(itest));
		itest = StringTools.ZahlTest(this.vec.get(11));
		jcmb[5].setSelectedIndex(leistungTesten(itest));
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
			holePreisGruppe(new Integer(jtf[11].getText()));				
		}else{
			JOptionPane.showMessageDialog(null, "Ermittlung der Preisgruppen erforderlich");				
		}
		
		jtf[14].setText(PatGrundPanel.thisClass.patDaten.get(44)); //heimbewohn
		jtf[15].setText(PatGrundPanel.thisClass.patDaten.get(30)); //befreit
		jtf[25].setText(PatGrundPanel.thisClass.patDaten.get(48)); //kilometer
		jtf[26].setText(this.vec.get(38)); //id von Patient
		jtf[27].setText(this.vec.get(0)); //pat_intern von Patient

		itest = StringTools.ZahlTest(this.vec.get(57));
		if(itest >0){
			jcmb[8].setSelectedItem( (String)SystemConfig.vSysColsBedeut.get(itest) );			
		}else{
			jcmb[8].setSelectedIndex(0);
		}

	}
	private String[] holePreis(int ivec,int ipreisgruppe){
		if(ivec > 0){
			ArrayList xvec = ((ArrayList)this.preisvec.get(ivec-1));
			int preispos = ((ipreisgruppe*4)-4)+3;
			return new String[] {(String)xvec.get(preispos),(String)xvec.get(preispos-1)};
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
			stest = datFunk.sHeute();
		}
		sbuf.append("rez_datum='"+datFunk.sDatInSQL(stest)+"', ");
		stest = jtf[3].getText().trim();
		if(stest.equals(".  .")){
			stest = datFunk.sHeute();
		}
		sbuf.append("lastdate='"+datFunk.sDatInSQL(stest)+"', ");
		sbuf.append("lasteddate='"+datFunk.sDatInSQL(datFunk.sHeute())+"', ");
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
			sbuf.append("art_dbeh1='"+preisvec.get(itest-1).get(35)+"', ");
		}else{
			sbuf.append("art_dbeh1='0', ");
		}
		itest = jcmb[3].getSelectedIndex();
		if(itest > 0){
			sbuf.append("art_dbeh2='"+preisvec.get(itest-1).get(35)+"', ");
		}else{
			sbuf.append("art_dbeh2='0', ");
		}
		itest = jcmb[4].getSelectedIndex();
		if(itest > 0){
			sbuf.append("art_dbeh3='"+preisvec.get(itest-1).get(35)+"', ");
		}else{
			sbuf.append("art_dbeh3='0', ");
		}
		itest = jcmb[5].getSelectedIndex();
		if(itest > 0){
			sbuf.append("art_dbeh4='"+preisvec.get(itest-1).get(35)+"', ");
		}else{
			sbuf.append("art_dbeh4='0', ");
		}
		sbuf.append("frequenz='"+jtf[8].getText()+"', ");
		sbuf.append("dauer='"+jtf[9].getText()+"', ");		
		sbuf.append("indikatschl='"+(String)jcmb[6].getSelectedItem()+"', ");
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
		for(int i = 0; i < 1;i++){
			if(SystemConfig.vZuzahlRegeln.get(izuzahl-1) <= 0){
				//System.out.println("ZuzahlStatus = Zuzahlung nicht erforderlich");
				szzstatus = "0";
				break;
			}
			System.out.println("ZuzahlStatus = Zuzahlung (zunächst) erforderlich, prüfe ob befreit oder unter 18");
			if(PatGrundPanel.thisClass.patDaten.get(30).equals("T")){
				//System.out.println("ZuzahlStatus = Patient ist befreit");
				//laut Patientenstamm befreit aber evtl. noch nicht für dieses Rezept.
				//deshalb prüfen ob bereits bezahlt;
				if(this.vec.get(14).equals("T")){
					szzstatus = "2";
				}else{
					szzstatus = "0";				
				}
				break;
			}
			if(datFunk.Unter18(datFunk.sHeute(), PatGrundPanel.thisClass.patDaten.get(4))){
				//System.out.println("ZuzahlStatus = Patient ist unter 18 also befreit...");
				szzstatus = "0";				
				break;
			}
			if(this.vec.get(14).equals("T")){
				szzstatus = "1";
			}else{
				szzstatus = "2";				
			}
		}
		//System.out.println("ZuzahlStatus = "+szzstatus);		
		sbuf.append("zzstatus='"+szzstatus+"', ");
		int leistung;
		String[] str;
		for(int i = 0;i < 4;i++){
			leistung = leistungTesten(jcmb[i+2].getSelectedIndex());
			if(leistung >= 0){
				str = holePreis(leistung,new Integer(jtf[13].getText()) );
				sbuf.append("preise"+(i+1)+"='"+str[0]+"', ");
				sbuf.append("pos"+(i+1)+"='"+str[1]+"', ");
				//System.out.println("Preis"+ (i+1)+" für Leistung "+leistung + " = " +  str[0]);				
				//System.out.println("Position"+ (i+1)+" für Leistung "+leistung + " = " +  str[1]);
			}else{
				sbuf.append("preise"+(i+1)+"='0.00'");
				sbuf.append("pos"+(i+1)+"='', ");
			}
		}
		
		sbuf.append("diagnose='"+jta.getText()+"' ");
		sbuf.append(" where id='"+this.vec.get(35)+"'");
		//System.out.println(sbuf.toString());	
		
		new ExUndHop().setzeStatement(sbuf.toString());
		((JXDialog)this.getParent().getParent().getParent().getParent().getParent()).setVisible(false);
		((JXDialog)this.getParent().getParent().getParent().getParent().getParent()).dispose();
	}
	/**************************************/	
	private void doSpeichernNeu(){
		int reznr = -1;
		if(!komplettTest()){
			return;
		}
		String stest = "";
		int itest = -1;
		StringBuffer sbuf = new StringBuffer();
		
/*******************************************/
		/****** Zunächst eine neue Rezeptnummer holen ******/
		Vector numvec = null;
		try {
			Reha.thisClass.conn.setAutoCommit(false);
			numvec = SqlInfo.holeSatz("nummern", nummer+",id", "mandant='"+Reha.aktIK+"' FOR UPDATE", Arrays.asList(new String[] {}));
			//System.out.println(Reha.aktIK);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(numvec.size() > 0){
			reznr = new Integer( (String)((Vector) numvec).get(0) );
			String cmd = "update nummern set "+nummer+"='"+(reznr+1)+"' where id='"+((Vector) numvec).get(1)+"'";
			//System.out.println("Kommando = "+cmd);
			new ExUndHop().setzeStatement(cmd);
			//System.out.println("bisherige Rezeptnummer = "+nummer.toUpperCase()+reznr+" / neue Rezeptnummer = "+nummer.toUpperCase()+(reznr+1));
			try {
				Reha.thisClass.conn.setAutoCommit(true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			try {
				Reha.thisClass.conn.rollback();
				Reha.thisClass.conn.setAutoCommit(true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		if(reznr < 0){
			JOptionPane.showMessageDialog(null,"Schwerwiegender Fehler beim Bezug einer neuen Rezeptnummer!");
			return;
		}
/*******************************************/
		int rezidneu = SqlInfo.holeId("verordn", "diagnose");
		sbuf.append("update verordn set rez_nr='"+nummer.toUpperCase()+
				new Integer(reznr).toString()+"', ");
		sbuf.append("pat_intern='"+jtf[27].getText()+"', ");
		sbuf.append("patid='"+jtf[26].getText()+"', ");
		sbuf.append("ktraeger='"+jtf[0].getText()+"', ");
		sbuf.append("kid='"+jtf[11].getText()+"', ");
		sbuf.append("arzt='"+jtf[1].getText()+"', ");
		sbuf.append("arztid='"+jtf[12].getText()+"', ");
		stest = datFunk.sHeute();
		sbuf.append("datum='"+datFunk.sDatInSQL(stest)+"', ");
		stest = jtf[2].getText().trim();
		if(stest.equals(".  .")){
			stest = datFunk.sHeute();
		}
		sbuf.append("rez_datum='"+datFunk.sDatInSQL(stest)+"', ");
		stest = jtf[3].getText().trim();
		if(stest.equals(".  .")){
			stest = datFunk.sHeute();
		}
		sbuf.append("lastdate='"+datFunk.sDatInSQL(stest)+"', ");
		sbuf.append("lasteddate='"+datFunk.sDatInSQL(datFunk.sHeute())+"', ");
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
			sbuf.append("art_dbeh1='"+preisvec.get(itest-1).get(35)+"', ");
		}else{
			sbuf.append("art_dbeh1='0', ");
		}
		itest = jcmb[3].getSelectedIndex();
		if(itest > 0){
			sbuf.append("art_dbeh2='"+preisvec.get(itest-1).get(35)+"', ");
		}else{
			sbuf.append("art_dbeh2='0', ");
		}
		itest = jcmb[4].getSelectedIndex();
		if(itest > 0){
			sbuf.append("art_dbeh3='"+preisvec.get(itest-1).get(35)+"', ");
		}else{
			sbuf.append("art_dbeh3='0', ");
		}
		itest = jcmb[5].getSelectedIndex();
		if(itest > 0){
			sbuf.append("art_dbeh4='"+preisvec.get(itest-1).get(35)+"', ");
		}else{
			sbuf.append("art_dbeh4='0', ");
		}
		sbuf.append("frequenz='"+jtf[8].getText()+"', ");
		sbuf.append("dauer='"+jtf[9].getText()+"', ");		
		sbuf.append("indikatschl='"+(String)jcmb[6].getSelectedItem()+"', ");
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
		//System.out.println(izuzahl.toString());
		String szzstatus = "";
		for(int i = 0; i < 1;i++){
			if(SystemConfig.vZuzahlRegeln.get(izuzahl-1) <= 0){
				//System.out.println("ZuzahlStatus = Zuzahlung nicht erforderlich");
				szzstatus = "0";
				break;
			}
			//System.out.println("ZuzahlStatus = Zuzahlung (zunächst) erforderlich, prüfe ob befreit oder unter 18");
			if(PatGrundPanel.thisClass.patDaten.get(30).equals("T")){
				//System.out.println("ZuzahlStatus = Patient ist befreit");
				szzstatus = "0";				
				break;
			}
			if(datFunk.Unter18(datFunk.sHeute(), PatGrundPanel.thisClass.patDaten.get(4))){
				//System.out.println("ZuzahlStatus = Patient ist unter 18 also befreit...");
				szzstatus = "0";				
				break;
			}
			szzstatus = "2";				
		}
		sbuf.append("zzstatus='"+szzstatus+"', ");
		int leistung;
		String[] str;
		for(int i = 0;i < 4;i++){
			leistung = leistungTesten(jcmb[i+2].getSelectedIndex());
			if(leistung >= 0){
				str = holePreis(leistung,new Integer(jtf[13].getText()) );
				sbuf.append("preise"+(i+1)+"='"+str[0]+"', ");
				sbuf.append("pos"+(i+1)+"='"+str[1]+"', ");
				//System.out.println("Preis"+ (i+1)+" für Leistung "+leistung + " = " +  str[0]);				
				//System.out.println("Position"+ (i+1)+" für Leistung "+leistung + " = " +  str[1]);
			}else{
				sbuf.append("preise"+(i+1)+"='0.00'");
				sbuf.append("pos"+(i+1)+"='', ");
			}
		}
		
		sbuf.append("diagnose='"+jta.getText()+"' ");
		sbuf.append("where id='"+new Integer(rezidneu).toString()+"' ");
		//System.out.println(sbuf.toString());
		new ExUndHop().setzeStatement(sbuf.toString());
		/*
		Vector tabvec = new Vector();
		tabvec.add(nummer.toUpperCase()+new Integer(reznr).toString());
		tabvec.add(PatGrundPanel.thisClass.imgzuzahl[new Integer(szzstatus)]);
		tabvec.add(jtf[2].getText());
		tabvec.add(datFunk.sHeute());
		tabvec.add(jtf[3].getText());
		tabvec.add(jtf[27].getText());		
		tabvec.add(new Integer(rezidneu).toString());
		*/
		final int xreznr = reznr;
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				// TODO Auto-generated method stub
				AktuelleRezepte.aktRez.holeRezepte(jtf[27].getText(),nummer.toUpperCase()+new Integer(xreznr).toString());
				return null;
			}
		}.execute();

		//AktuelleRezepte.aktRez.dtblm.addRow((Vector)tabvec.clone());
		((JXDialog)this.getParent().getParent().getParent().getParent().getParent()).setVisible(false);
		((JXDialog)this.getParent().getParent().getParent().getParent().getParent()).dispose();
		
		
		
	}
	private void doAbbrechen(){
		((JXDialog)this.getParent().getParent().getParent().getParent().getParent()).dispose();		
	}

}
