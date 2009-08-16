package patientenFenster;

import hauptFenster.Reha;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.plaf.TabbedPaneUI;

import kvKarte.KVKWrapper;

import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import sqlTools.ExUndHop;
import sqlTools.SqlInfo;
import stammDatenTools.ZuzahlTools;
import systemEinstellungen.SystemConfig;
import systemTools.Colors;
import systemTools.JCompTools;
import systemTools.JRtaCheckBox;
import systemTools.JRtaComboBox;
import systemTools.JRtaTextField;
import systemTools.StringTools;
import terminKalender.datFunk;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.windows.WindowsTabbedPaneUI;

import events.PatStammEvent;
import events.PatStammEventClass;

public class PatNeuanlage extends JXPanel implements ActionListener, KeyListener,FocusListener {
	
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
public JRtaTextField[] jtf = {null,null,null,null,null,
		                      null,null,null,null,null,
		                      null,null,null,null,null,
		                      null,null,null,null,null,
		                      null,null,null,null,null,
		                      null,null,null,null,null,
		                      null,null,null,null,null,
		                      null,null,null,null};
JRtaCheckBox[]jcheck  ={null,null,null,null,null,null,null,null,null};

JButton knopf3 = null;
JButton knopf4 = null;
JButton knopf5 = null;
JRtaComboBox cbanrede = null;

String kassenid = ""; 
String befreitdatum = "";
boolean freizumstart = false;
boolean freibeimspeichern = false;

public boolean feldergefuellt = false;

Font font = null;
JScrollPane jscr = null;
List<String>xfelder = Arrays.asList( new String[] {"anrede" ,"n_name","v_name","strasse","plz","ort","geboren","telefonp",
										"telefong","telefonm","emaila","kasse","kv_nummer","v_nummer","kv_status",
										"bef_dat","artz","arzt_num","therapeut","abwanrede","abwtitel","abn_name",
										"abwv_name","abwstrasse","abwort","akutdat","termine1","termine2","kilometer",
										"heimbewohn"});
List<String> checks = Arrays.asList( new String[] {"abwadress","akutpat" ,"merk1","merk2","merk3","merk4",
										"merk5","merk6","heimbewohn"});
               //0   1   2   3   4  5  6  7  8  9  10 1112 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34
int[] fedits =  {0 , 1 , 2 , 3 , 4, 5, 6,11, 7, 8, 9,10,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34};
int[] ffelder = {0 , 1 , 2 , 3 ,21,23,24, 4,18,19,20,50,13,14,16,15,31,25,26,56, 6, 7, 8, 9,10,11,12,46,34,36,37,48,40,65,66};
int[] fchecks =  {0 , 1 , 2 , 3 , 4  , 5 ,6 , 7 , 8};
int[] ffelder2 = {5 ,33 ,62 ,61 , 60 ,59 ,58, 57, 44};
Vector<String> patDaten = null;
ImageIcon hgicon;
int icx,icy;
AlphaComposite xac1 = null;
AlphaComposite xac2 = null;	
String feldname = "";	
public static String arztbisher = "";
public static String kassebisher = "";
boolean inNeu = false;
	public PatNeuanlage(Vector<String> vec,boolean neu,String sfeldname){
		super();
		setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				// TODO Auto-generated method stub
				Point2D start = new Point2D.Float(0, 0);
			     Point2D end = new Point2D.Float(PatGrundPanel.thisClass.getWidth(),100);
			     float[] dist = {0.0f, 0.75f};
			     Color[] colors = {Color.WHITE,Colors.PiOrange.alpha(0.25f)};
			     //Color[] colors = {Color.WHITE,Colors.TaskPaneBlau.alpha(0.5f)};
			     //Color[] colors = {Color.WHITE,getBackground()};
			     LinearGradientPaint p =
			         new LinearGradientPaint(start, end, dist, colors);
			     MattePainter mp = new MattePainter(p);
			     setBackgroundPainter(new CompoundPainter(mp));		
				return null;
			}
			
		}.execute();
		this.setDoubleBuffered(true);
		this.patDaten = vec;
		this.inNeu = neu;
		if(! inNeu){
			
		}
		this.feldname = sfeldname;
		this.setBackground(Color.WHITE);
		this.setLayout(new BorderLayout());
		this.font = new Font("Tahome",Font.BOLD,11);
		this.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		
		add(getButtonPanel(),BorderLayout.SOUTH);
		//add(getDatenPanel(),BorderLayout.CENTER);

		UIManager.put("TabbedPane.tabsOpaque", Boolean.FALSE);
		UIManager.put("TabbedPane.contentOpaque", Boolean.FALSE);
		
		JTabbedPane patTab = new JTabbedPane();
		patTab.setUI(new WindowsTabbedPaneUI());
		TabbedPaneUI tpUi = patTab.getUI();

		patTab.setOpaque(false);
		
		final JTabbedPane xpatTab = patTab;
		/*
		new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				xpatTab.addTab("Seite-1", getDatenPanel());
				xpatTab.addTab("Seite-2", getZusatzPanel());
				return null;
			}
		}.execute();
		*/
		patTab.addTab("Seite-1", getDatenPanel());
		patTab.addTab("Seite-2", getZusatzPanel());

		add(patTab,BorderLayout.CENTER);
		
		hgicon = Reha.rehaBackImg;//new ImageIcon(Reha.proghome+"icons/therapieMT1.gif");
		icx = hgicon.getIconWidth()/2;
		icy = hgicon.getIconHeight()/2;
		xac1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.07f); 
		xac2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f);	
		

//****************Checken ob Preisgruppen bedient werden****************		
		
		// nur für den ersten Focus setzen
		final FocusListener flis = new FocusListener(){

			@Override
			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub
				try{
					if( ((JComponent)arg0.getSource()).getName().equals("PatientenNeuanlage") ){
						if(inNeu || feldname.equals("")){
							setzeFocus();
						}else{
							if(PatGrundPanel.thisClass.kid < 0 && feldname.equals("KASSE")){
								if(feldergefuellt){
									//jtf[12].setText("?"+jtf[12].getText());
								}
							}
							if(PatGrundPanel.thisClass.aid < 0 && feldname.equals("ARZT")){
								if(feldergefuellt){
									//jtf[17].setText("?"+jtf[17].getText());
								}
							}
							geheAufFeld(feldname);
						}
						removeFocusListener(this);
					}
				}catch(java.lang.NullPointerException ex){
				}
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				// TODO Auto-generated method stub
			}
			
		};
		this.addFocusListener(flis);
	
		new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				for(int i = 0; i < ffelder.length;i++ ){
					jtf[ffelder[i]].addFocusListener(flis);	
				}
				for(int i = 0; i < ffelder2.length;i++ ){
					jcheck[ffelder2[i]].addFocusListener(flis);	
				}
				return null;
			}
		}.execute();
		

		if((!this.inNeu)){
			new Thread(){
				public void run(){
					fuelleFelder();
					if(!feldname.equals("")){
						geheAufFeld(feldname);
					}
				}
			}.start();
		}else{
			jtf[12].setText("?");
			jtf[17].setText("?");
		}
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run(){
		 			if(SystemConfig.sReaderAktiv.equals("1")){
		 				KVKWrapper kvw = new KVKWrapper(SystemConfig.sReaderName);
		 				int ret = kvw.KVK_Einlesen();
		 			}
		 	   }
		});
		UIManager.put("TabbedPane.tabsOpaque", Boolean.TRUE);
		UIManager.put("TabbedPane.contentOpaque", Boolean.TRUE);
	
		//validate();
		
	}
	
	
	
	public void geheAufFeld(String feld){
		//System.out.println("Focus setzen auf "+feld);
		final String xfeld = feld;
		new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				// TODO Auto-generated method stub
	 			for(int i = 0; i < jtf.length;i++){
	 				if(jtf[i].getName() != null){
	 					if( jtf[i].getName().trim().toUpperCase().equals(xfeld)){
	 						if(! jtf[i].hasFocus()){
	 							jtf[i].setCaretPosition(0);
	 							jtf[i].requestFocusInWindow();
	 						}
	 						break;
	 					}
	 				}
	 			}
				return null;
			}
			
		}.execute();
		/*
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run(){
		 	   }
		});
		*/
	}
	@Override
	public void paintComponent( Graphics g ) { 
		super.paintComponent( g );
		Graphics2D g2d = (Graphics2D)g;
		
		if(hgicon != null){
			g2d.setComposite(this.xac1);
			g2d.drawImage(hgicon.getImage(), (getWidth()/2)-icx , (getHeight()/2)-icy,null);
			g2d.setComposite(this.xac2);
		}
	}
	
	public void setzeFocus(){
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run(){
		 		   if(! cbanrede.hasFocus()){
		 			   cbanrede.requestFocusInWindow();
		 		   }	   
		 	   }
		}); 	   		
	}
	
	private void fuelleFelder(){
		final String xfeld = this.feldname;

		new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {

		 		List<String> nichtlesen = Arrays.asList(new String[] {"anamnese","pat_text"});
				Vector felder = SqlInfo.holeSatz("pat5", "*", "pat_intern='"+PatGrundPanel.thisClass.aktPatID+"'",nichtlesen);
				int gros = felder.size();
				int anzahlf = fedits.length;
				int anzahlc = fchecks.length;
				if(gros > 0){
					String name = "";
					for(int i = 0; i < anzahlf;i++){
						name = jtf[fedits[i]].getName().trim();
						if(name.contains("geboren") || 
								name.contains("akutdat") || 
								name.contains("akutbis") || 
								name.contains("bef_dat") ||
								name.contains("er_dat")){
							String datum = new String((String)felder.get(ffelder[i]));
							if(datum.trim().length() > 0){
								//System.out.println("Datum wäre gewesen->"+datum+" Länge->"+datum.trim().length());
								jtf[fedits[i]].setText(datFunk.sDatInDeutsch(datum) );								
							}
						}else{
							jtf[fedits[i]].setText((String) felder.get(ffelder[i]) );
						}
					}
				}

				for(int i = 0; i < anzahlc;i++){
					jcheck[i].setSelected( (felder.get(ffelder2[i]).equals("F")||felder.get(ffelder2[i]).equals("") ? false : true) );
				}
				cbanrede.setSelectedItem(jtf[0].getText());

				/*
				if(!feldname.equals("")){
					geheAufFeld(feldname);
				}*/
				arztbisher = new String(jtf[17].getText());
				kassebisher = new String(jtf[12].getText()); 
				kassenid = PatGrundPanel.thisClass.patDaten.get(68);
				befreitdatum = datFunk.sDatInDeutsch(PatGrundPanel.thisClass.patDaten.get(31));
				freizumstart = (PatGrundPanel.thisClass.patDaten.get(30).equals("T") ? true : false);
				System.out.println("Gehe auf Feld 1 -> "+xfeld);
				if(! "".equals(xfeld)){
					SwingUtilities.invokeLater(new Runnable(){
					 	   public  void run(){
								System.out.println("Gehe auf Feld 2 -> "+xfeld);
								feldergefuellt = true;
								geheAufFeld(xfeld);
					 	   }
					}); 	   
				}

				return null;
			}
			
		}.execute();
		
	}
	private void schreibeInDb(){
		if( jtf[2].getText().trim().equals("") || jtf[3].getText().trim().equals("")
				|| jtf[4].getText().trim().equals("") || jtf[5].getText().trim().equals("")
				|| jtf[6].getText().trim().equals("") || jtf[12].getText().trim().equals("")
				|| jtf[11].getText().trim().equals(".  .") || jtf[17].getText().trim().equals("") ){
			JOptionPane.showMessageDialog(null, "Die Daten des Patienten wurden unvollständig eingegeben!\n\nSpeichern ist nicht möglich.\n");
			cbanrede.requestFocus();
			return;
		}
		//int gros = felder.size();
		int anzahlf = fedits.length;
		int anzahlc = fchecks.length;
		int patintern = 0;
		String name = "";
		String wert = "";
		String spatintern;
		StringBuffer buf = new StringBuffer();
		buf.append("update pat5 set ");
		for(int i = 1; i < anzahlf;i++){
			name = jtf[fedits[i]].getName().trim();
			if(name.contains("geboren") || 
					name.contains("akutdat") || 
					name.contains("akutbis") || 
					name.contains("bef_dat") ||
					name.contains("er_dat")){
				if(jtf[fedits[i]].getText().trim().equals(".  .")){
					buf.append(name+"=NULL, ");	
					if(name.equals("bef_dat")){ //Wenn befreit bis testen ob er wert größer als heute
						buf.append("befreit ='F', ");
						freibeimspeichern = false;
						System.out.println("Patient ist -> nicht <- befreit!");
					}
				}else{
					try{
						buf.append(name+"='"+datFunk.sDatInSQL(jtf[fedits[i]].getText())+"', ");
						if(name.equals("bef_dat")){ //Wenn befreit bis testen ob er wert größer als heute
							if( datFunk.DatumsWert(jtf[fedits[i]].getText()) >= datFunk.DatumsWert(datFunk.sHeute()) ){
								buf.append("befreit ='T', ");
								freibeimspeichern = true;
								System.out.println("Patient ist befreit!");
							}else{
								buf.append("befreit ='F', ");
								freibeimspeichern = false;								
							}
						}
					}catch(java.lang.ArrayIndexOutOfBoundsException ex){
						buf.append(name+"='"+"*****Problem"+jtf[fedits[i]].getText()+"', ");
					}
				}

			}else{
				buf.append(name+"='"+StringTools.Escaped(jtf[fedits[i]].getText())+"', ");
				//buf.append(name+"='"+jtf[fedits[i]].getText()+"', "); // bislang o.k. 

			}
		}
		buf.append("anrede='"+((String)cbanrede.getSelectedItem()).trim()+"'");
		for(int i = 0; i < anzahlc;i++){
			name = jcheck[i].getName();
			buf.append(", "+name+"='"+(jcheck[i].isSelected() ? "T' " : "F' " ));
		}
		//System.out.println("Inhalt = "+buf.toString());
		if(!this.inNeu){
			buf.append(" where pat_intern='"+PatGrundPanel.thisClass.aktPatID+"'");
			spatintern = PatGrundPanel.thisClass.aktPatID;
			// Wenn Kasse veränder wurde....
			if(!jtf[34].getText().trim().equals(kassenid)){
				JOptionPane.showMessageDialog(null, "Achtung - Sie haben dem Patient eine neue Kasse zugewiesen.\n"+
						"Eventuell ändert sich dadurch der Zuzahlungsstatus vorhandener Rezepte. Bitte prüfen!!!");
			}

			//if(!jtf[16].getText().trim().equals(befreitdatum) ){
			System.out.println("Befreiung beim Start ="+freizumstart);
			System.out.println("Befreiung beim Speichern ="+freibeimspeichern);
			int zzregel = -1;
			boolean doof = false;
			if(! (freizumstart == freibeimspeichern)){
				if ( ((zzregel = ZuzahlTools.getZuzahlRegel(jtf[34].getText().trim())) <= 0) ){
				//if ( ((zzregel = ZuzahlTools.getZuzahlRegel(jtf[34].getText().trim())) <= 0) && freibeimspeichern){
					JOptionPane.showMessageDialog(null,"Sie haben einen Kostenträger gwählt der keine Zuzahlung verlangt und\n"+
							"jetzt wollen Sie im Feld Zuzahlungsbefreiung rummurksen???????\n\nNa ja.....");
					doof = true;
				}
				if(!doof){
					// hier wäre es optimal eine  ZuzahlToolsFunktion zu haben.....
					int anzahl = SqlInfo.zaehleSaetze("verordn", "pat_intern='"+PatGrundPanel.thisClass.aktPatID+"' AND abschluss='F'");
					if(anzahl > 0){
						String meldung = "Dieser Patient hat -> "+anzahl+" laufende Rezepte <- ohne Abschluss\n"+
						"Soll der veränderte Befreiungsstatus auf alle nicht(!) abgeschlossenen Rezepte übertragen werden?";
						int frage = JOptionPane.showConfirmDialog(null,meldung,"Achtung wichtige Benuterzanfrage",JOptionPane.YES_NO_OPTION);
						if(frage == JOptionPane.NO_OPTION){
							JOptionPane.showMessageDialog(null,"Dann eben nicht!\nVergessen Sie aber nicht den Befreiungsstatus der Rezepte von Hand zu ändern");
						}else if(frage == JOptionPane.YES_OPTION){
							String pat_intern = PatGrundPanel.thisClass.aktPatID;
							String geboren = datFunk.sDatInDeutsch(PatGrundPanel.thisClass.patDaten.get(4));
							String befreit = (freibeimspeichern ? "T" : "F");
							String datum = (freibeimspeichern ? "" : jtf[16].getText().trim());
							ZuzahlTools.zzStatusEdit(pat_intern, geboren, "", befreit, jtf[34].getText().trim());
							//SqlInfo.aktualisiereSaetze("verordn", "befr='"+befreit+"',"+, "pat_intern='"+pat_intern+"'");
						}
					}
				}else{
					System.out.println("Doof = true and zzregel = "+zzregel);					
				}

			}
		}else{
			// Angelegt von aufgenommen werden
			int neuid = SqlInfo.holeId("pat5", "n_name");
			patintern = neuid+Reha.thisClass.patiddiff;
			buf.append(",anl_datum='"+datFunk.sDatInSQL(datFunk.sHeute())+"' ");
			buf.append(",pat_intern='"+new Integer(patintern).toString() +"' where id='"+new Integer(neuid).toString()+"'");
			spatintern = new Integer(patintern).toString();
		}
		new ExUndHop().setzeStatement(buf.toString());

		
		((JXDialog)this.getParent().getParent().getParent().getParent().getParent()).setVisible(false);
		((JXDialog)this.getParent().getParent().getParent().getParent().getParent()).dispose();

		String s1 = new String("#PATSUCHEN");
		String s2 = new String(spatintern);
		PatStammEvent pEvt = new PatStammEvent(PatNeuanlage.this);
		pEvt.setPatStammEvent("PatSuchen");
		pEvt.setDetails(s1,s2,"") ;
		PatStammEventClass.firePatStammEvent(pEvt);


	}
	private JXPanel getButtonPanel(){
		JXPanel but = new JXPanel(new BorderLayout());
		//but.add(new JLabel("Hier ist Raum für Buttons"));
		but.setOpaque(false);
		but.setDoubleBuffered(true);
		
		knopf3 = new JButton("Chipkarte");
		knopf3.setPreferredSize(new Dimension(70, 20));
		knopf3.addActionListener(this);		
		knopf3.setActionCommand("einlesen");
		knopf3.addKeyListener(this);
		knopf3.setMnemonic(KeyEvent.VK_C);
		if(SystemConfig.sReaderAktiv.equals("0")){
			knopf3.setEnabled(false);
		}
	
		knopf4 = new JButton("speichern");
		knopf4.setPreferredSize(new Dimension(70, 20));
		knopf4.addActionListener(this);		
		knopf4.setActionCommand("speichern");
		knopf4.addKeyListener(this);
		knopf4.setMnemonic(KeyEvent.VK_S);
		
		knopf5 = new JButton("abbrechen");
		knopf5.setPreferredSize(new Dimension(70, 20));
		knopf5.addActionListener(this);		
		knopf5.setActionCommand("abbrechen");
		knopf5.addKeyListener(this);
		knopf5.setMnemonic(KeyEvent.VK_A);
		
									//      1.                2.    3.     4.     5.    6.       7    
		FormLayout lay = new FormLayout("fill:0:grow(0.50), 60dlu,15dlu, 60dlu, 15dlu,60dlu,fill:0:grow(0.50) ",
			       //1.   2.  3.   4.   5.   
					"4dlu, p, 4dlu");
					PanelBuilder builder = new PanelBuilder(lay);
					builder.setDefaultDialogBorder();
					builder.getPanel().setOpaque(false);
					
					CellConstraints cc = new CellConstraints();
		builder.add(knopf3, cc.xy(2,2));					
		builder.add(knopf4, cc.xy(4,2));
		builder.add(knopf5, cc.xy(6,2));
					
		but.add(builder.getPanel(),BorderLayout.CENTER);
		return but;
	}
	private JXPanel getZusatzPanel(){
		JXPanel but = new JXPanel(new BorderLayout());
		//but.setBorder(null);
		but.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		but.setOpaque(false);
		but.setDoubleBuffered(true);

		FormLayout lay = new FormLayout("","");
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);	
		CellConstraints cc = new CellConstraints();
		builder.getPanel().setDoubleBuffered(true);
		/********
		 * 
		 */
		 // Hier die neuen Felder aufnehmen
		
		/*******
		 * 
		 */
		JScrollPane jscrzusatz = JCompTools.getTransparentScrollPane(builder.getPanel());
		jscrzusatz.getVerticalScrollBar().setUnitIncrement(15);
		jscrzusatz.getViewport().setOpaque(false);
		jscrzusatz.setBorder(null);
		jscrzusatz.setViewportBorder(null);
		jscrzusatz.validate();
		jscrzusatz.addKeyListener(this);
		but.add(jscrzusatz,BorderLayout.CENTER);
		but.validate();
		return but;
	}
	private JXPanel getDatenPanel(){
		JXPanel but = new JXPanel(new BorderLayout());
		//but.setBorder(null);
		but.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		but.setOpaque(false);
		but.setDoubleBuffered(true);
	
		cbanrede = new JRtaComboBox(new String[] {"HERR","FRAU"});

		jtf[0] = new JRtaTextField("GROSS", true);
		jtf[0].setName("anrede");

		jtf[1] = new JRtaTextField("GROSS", true);
		jtf[1].setName("titel");

		jtf[2] = new JRtaTextField("GROSS", true);
		jtf[2].setName("n_name");
		jtf[2].setFont(font);
		jtf[2].setForeground(Color.RED);
		
		jtf[3] = new JRtaTextField("GROSS", true);
		jtf[3].setName("v_name");
		jtf[3].setFont(font);
		jtf[3].setForeground(Color.RED);

		jtf[4] = new JRtaTextField("GROSS", true);
		jtf[4].setName("strasse");

		jtf[5] = new JRtaTextField("ZAHLEN", true);
		jtf[5].setName("plz");

		jtf[6] = new JRtaTextField("GROSS", true);
		jtf[6].setName("ort");

		jtf[7] = new JRtaTextField("GROSS", true);
		jtf[7].setName("telefonp");

		jtf[8] = new JRtaTextField("GROSS", true);
		jtf[8].setName("telefong");

		jtf[9] = new JRtaTextField("GROSS", true);
		jtf[9].setName("telefonm");

		jtf[10] = new JRtaTextField("", true);
		jtf[10].setName("emaila");

		jtf[11] = new JRtaTextField("DATUM", true);
		jtf[11].setName("geboren");
		jtf[11].setFont(font);
		jtf[11].setForeground(Color.RED);

		
		jtf[12] = new JRtaTextField("GROSS", true);
		jtf[12].setName("kasse");
		jtf[12].addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent arg0) {
				//System.out.println("code = "+arg0.getKeyCode());
				//System.out.println("char = "+arg0.getKeyChar());
				if(arg0.getKeyChar()=='?'){
					String suchkrit = jtf[12].getText().replaceAll("\\?", "");
					kassenAuswahl(new String[] {suchkrit,jtf[34].getText().trim(),jtf[34].getText()});
				}
			}
				
		});
		

		jtf[13] = new JRtaTextField("ZAHLEN", true);
		jtf[13].setName("kv_nummer");

		jtf[14] = new JRtaTextField("ZAHLEN", true);
		jtf[14].setName("v_nummer");

		jtf[15] = new JRtaTextField("ZAHLEN", true);
		jtf[15].setName("kv_status");

		jtf[16] = new JRtaTextField("DATUM", true);
		jtf[16].setName("bef_dat"); //aus Kostenträgerdatei/Karte einlesen?
		
		jtf[17] = new JRtaTextField("GROSS", true);
		jtf[17].setName("arzt");
		jtf[17].addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent arg0) {
				//System.out.println("code = "+arg0.getKeyCode());
				//System.out.println("char = "+arg0.getKeyChar());
				if(arg0.getKeyChar()=='?'){
					String[] suchkrit = new String[] {jtf[17].getText().replaceAll("\\?", ""),jtf[33].getText()};
					arztAuswahl(suchkrit);
				}
			}
				
		});
		jtf[18] = new JRtaTextField("ZAHLEN", true);
		jtf[18].setName("arzt_num");
		
		jtf[19] = new JRtaTextField("GROSS", true);
		jtf[19].setName("therapeut");
		
		jtf[20] = new JRtaTextField("GROSS", true);
		jtf[20].setName("abwanrede");
		
		jtf[21] = new JRtaTextField("GROSS", true);
		jtf[21].setName("abwtitel");
		
		jtf[22] = new JRtaTextField("GROSS", true);
		jtf[22].setName("abwn_name");
		
		jtf[23] = new JRtaTextField("GROSS", true);
		jtf[23].setName("abwv_name");
		
		jtf[24] = new JRtaTextField("GROSS", true);
		jtf[24].setName("abwstrasse");
		
		jtf[25] = new JRtaTextField("GROSS", true);
		jtf[25].setName("abwplz");
		
		jtf[26] = new JRtaTextField("GROSS", true);
		jtf[26].setName("abwort");
		
		jtf[27] = new JRtaTextField("DATUM", true);
		jtf[27].setName("akutbis");
		
		jtf[28] = new JRtaTextField("DATUM", true);
		jtf[28].setName("akutdat");

		jtf[29] = new JRtaTextField("GROSS", true);
		jtf[29].setName("termine1");
		
		jtf[30] = new JRtaTextField("GROSS", true);
		jtf[30].setName("termine2");
		
		jtf[31] = new JRtaTextField("ZAHLEN", true);
		jtf[31].setName("kilometer");
		
		jtf[32] = new JRtaTextField("DATUM", true);
		jtf[32].setName("er_dat");
		
		jtf[33] = new JRtaTextField("ZAHLEN", true);
		jtf[33].setName("arztid");
		
		jtf[34] = new JRtaTextField("ZAHLEN", true);
		jtf[34].setName("kassenid");
		/*		
		jtf[35] = new JRtaTextField("GROSS", true);
		jtf[35].setName("arztid");

		jtf[35] = new JRtaTextField("GROSS", true);
		jtf[35].setName("arztid");

		jtf[35] = new JRtaTextField("GROSS", true);
		jtf[35].setName("kassenid");
		*/

		jcheck[0] = new JRtaCheckBox();
		jcheck[0].setOpaque(false);
		jcheck[0].setName("abwadress");
		
		jcheck[1] = new JRtaCheckBox();
		jcheck[1].setOpaque(false);
		jcheck[1].setName("akutpat");
		
		jcheck[2] = new JRtaCheckBox(SystemConfig.vPatMerker.get(0));
		jcheck[2].setOpaque(false);
		jcheck[2].setName("merk1");
		
		jcheck[3] = new JRtaCheckBox(SystemConfig.vPatMerker.get(1));
		jcheck[3].setOpaque(false);
		jcheck[3].setName("merk2");
		
		jcheck[4] = new JRtaCheckBox(SystemConfig.vPatMerker.get(2));
		jcheck[4].setOpaque(false);
		jcheck[4].setName("merk3");

		jcheck[5] = new JRtaCheckBox(SystemConfig.vPatMerker.get(3));
		jcheck[5].setOpaque(false);
		jcheck[5].setName("merk4");
		
		jcheck[6] = new JRtaCheckBox(SystemConfig.vPatMerker.get(4));
		jcheck[6].setOpaque(false);
		jcheck[6].setName("merk5");
		
		jcheck[7] = new JRtaCheckBox(SystemConfig.vPatMerker.get(5));
		jcheck[7].setOpaque(false);
		jcheck[7].setName("merk6");
		
		jcheck[8] = new JRtaCheckBox();
		jcheck[8].setOpaque(false);
		jcheck[8].setName("heimbewohn");		
		
		//die Labeltexte merk2 bis merk7 aus Datenbank/SysINI einlesen?
		
									//      1.                2.    3.     4.                 5.    6.    
		FormLayout lay = new FormLayout("right:max(80dlu;p), 4dlu, 60dlu,right:max(60dlu;p), 4dlu, 60dlu",
			       //1.   2.  3.   4.   5.   6  7   8    9   10   11  12  13  14   15   16  17  18   19   20   21  22   23  24   25  26   27  28  29   30   31   32  33  34   35  36   37  38   39    40  41  42  43   44   45  46  47  48    49   50   51 52   53  54   55  56  57   58   59   60  61   62  63  64   65   66   67
					"p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 7dlu, p, 7dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 7dlu, p, 7dlu, p, 2dlu, p, 2dlu, p,  7dlu, p, 7dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 7dlu, p , 7dlu, p, 2dlu, p, 2dlu, p, 7dlu, p, 7dlu, p,"+
				   //68   69  70   71   72  73   74   75   76  77  78  79 80 
					"2dlu, p, 2dlu, p, 2dlu, p,  2dlu, p, 2dlu, p, 7dlu,p,7dlu");
					PanelBuilder builder = new PanelBuilder(lay);
					builder.setDefaultDialogBorder();
					builder.getPanel().setOpaque(false);	
					CellConstraints cc = new CellConstraints();
					builder.getPanel().setDoubleBuffered(true);

		builder.addLabel("Anrede", cc.xy(1,1));
		builder.add(cbanrede, cc.xy(3,1));
		//builder.add(jtf[0], cc.xy(3,1));
		builder.addLabel("Titel", cc.xy(4,1));
		builder.add(jtf[1], cc.xy(6, 1));
		builder.addLabel("Nachname", cc.xy(1,3));
		builder.add(jtf[2], cc.xyw(3, 3, 4));
		builder.addLabel("Vorname", cc.xy(1,5));
		builder.add(jtf[3], cc.xyw(3, 5, 4));
		builder.addLabel("Straße, Nr.", cc.xy(1,7));
		builder.add(jtf[4], cc.xyw(3, 7, 4));
		builder.addLabel("PLZ, Ort", cc.xy(1,9));
		builder.add(jtf[5], cc.xy(3, 9));
		builder.add(jtf[6], cc.xyw(4, 9, 3));
		builder.addLabel("Geburtstag", cc.xy(1,11));
		builder.add(jtf[11], cc.xy(3, 11));
		builder.addLabel("Telefon priv.", cc.xy(1, 13));
		builder.add(jtf[7], cc.xyw(3, 13, 4));
		builder.addLabel("Telefon gesch.", cc.xy(1, 15));
		builder.add(jtf[8], cc.xyw(3, 15, 4));
		builder.addLabel("Mobil", cc.xy(1, 17));
		builder.add(jtf[9], cc.xyw(3, 17, 4));
		builder.addLabel("Email", cc.xy(1, 19));
		builder.add(jtf[10], cc.xyw(3, 19, 4));
		
		builder.addSeparator("Krankenversicherung", cc.xyw(1, 21, 6));
		builder.addLabel("Kasse (?)", cc.xy(1, 23));
		builder.add(jtf[12], cc.xyw(3, 23, 4));
		builder.addLabel("Kassen-IK", cc.xy(1,25));
		builder.add(jtf[13], cc.xyw(3, 25, 4));
		builder.addLabel("Vers-Nr.", cc.xy(1,27));
		builder.add(jtf[14], cc.xyw(3, 27, 4));
		builder.addLabel("Status", cc.xy(1, 29));
		builder.add(jtf[15], cc.xy(3,29));
		builder.addLabel("Befreit bis", cc.xy(1, 31));
		builder.add(jtf[16], cc.xy(3, 31));
		
		builder.addSeparator("Arzt / Therapeut", cc.xyw(1, 33, 6));

		builder.addLabel("Hausarzt (?)", cc.xy(1, 35));
		builder.add(jtf[17], cc.xyw(3, 35, 4));
		builder.addLabel("ArztNummer (LANR)", cc.xy(1, 37));
		builder.add(jtf[18], cc.xyw(3,37,4));
		builder.addLabel("Betreuer/Therapeut", cc.xy(1, 39));
		builder.add(jtf[19], cc.xyw(3,39,4));

		builder.addSeparator("Plandaten / sonstiges", cc.xyw(1, 41, 6));

		builder.addLabel("Akutpatient", cc.xy(1,43));
		builder.add(jcheck[1], cc.xy(3,43));
		
		builder.addLabel("akut von", cc.xy(1,45));
		builder.add(jtf[28], cc.xy(3, 45));
		builder.addLabel("akut bis", cc.xy(4,45));
		builder.add(jtf[27], cc.xy(6, 45));
		
		builder.addLabel("mögliche Termine 1", cc.xy(1, 47));
		builder.add(jtf[29], cc.xyw(3, 47, 4));
		builder.addLabel("mögliche Termine 2", cc.xy(1, 49));
		builder.add(jtf[30], cc.xyw(3, 49, 4));
		builder.addLabel("Kilometer bei HB", cc.xy(1, 51));
		builder.add(jtf[31], cc.xy(3, 51));
		builder.addLabel("Heimbewohner", cc.xy(1,53));
		builder.add(jcheck[8], cc.xy(3, 53));
		builder.addLabel("Vertrag unterz. am", cc.xy(1, 55));
		builder.add(jtf[32], cc.xy(3, 55));

		builder.addSeparator("individuelle Merkmale", cc.xyw(1, 57, 6));

		builder.add(jcheck[2], cc.xy(3, 59));
		builder.add(jcheck[3], cc.xy(6, 59));
		builder.add(jcheck[4], cc.xy(3, 61));
		builder.add(jcheck[5], cc.xy(6, 61));
		builder.add(jcheck[6], cc.xy(3, 63));
		builder.add(jcheck[7], cc.xy(6,63));
		
		builder.addSeparator("abweichender Rechnungsempfänger/Versicherter", cc.xyw(1, 65, 6));
		builder.addLabel("verwenden", cc.xy(1,67));
		builder.add(jcheck[0], cc.xy(3,67));
		builder.addLabel("Anrede", cc.xy(1, 69));
		builder.add(jtf[20], cc.xy(3, 69));
		builder.addLabel("Titel", cc.xy(4, 69));
		builder.add(jtf[21], cc.xy(6, 69));
		builder.addLabel("Nachname", cc.xy(1, 71));
		builder.add(jtf[22], cc.xyw(3, 71, 4));
		builder.addLabel("Vorname", cc.xy(1, 73));
		builder.add(jtf[23], cc.xyw(3,73,4));
		builder.addLabel("Straße, Nr.", cc.xy(1, 75));
		builder.add(jtf[24], cc.xyw(3, 75, 4));
		builder.addLabel("PLZ, Ort", cc.xy(1, 77));
		builder.add(jtf[25], cc.xy(3, 77));
		builder.add(jtf[26], cc.xyw(4, 77, 3));
		builder.addSeparator("Ende der Fahnenstange", cc.xyw(1, 79, 6));
		
		builder.getPanel().addKeyListener(this);
		builder.getPanel().addFocusListener(this);
		builder.getPanel().validate();
		jscr = new JScrollPane();
		jscr.setOpaque(false);
		jscr.getViewport().setOpaque(false);
		jscr.setBorder(null);
		jscr.setViewportBorder(null);
		jscr.getVerticalScrollBar().setUnitIncrement(15);
		jscr.setViewportView(builder.getPanel());

		jscr.validate();
		jscr.addKeyListener(this);

		but.add(jscr,BorderLayout.CENTER);
		but.validate();
		return but;
	}
	
	public static String getArztBisher(){
		return arztbisher;
	}
	public static String getKasseBisher(){
		return kassebisher;
	}

	private void einlesen(){

		if(SystemConfig.sReaderAktiv.equals("0")){
			return;
		}
		//System.out.println("Aufruf der KVK");
		KVKWrapper kvw = new KVKWrapper(SystemConfig.sReaderName);
		int ret = kvw.KVK_Einlesen();
		if(ret==0){
			KVKRohDaten kvkr = new KVKRohDaten(this);
			kvkr.setModal(true);
			kvkr.setLocationRelativeTo(this);
			kvkr.setVisible(true);
			setzeFocus();
			kvkr = null;
			//cbanrede.requestFocus();
		}else{
			String fehlertext = "Fehler beim einlesen der Versichertenkarte.\nBitter erneut einlesen\n\n"+
						"Fehler-Code: "+SystemConfig.hmKVKDaten.get("Fehlercode")+"\n"+
						"Fehler-Text: "+SystemConfig.hmKVKDaten.get("Fehlertext")+"\n";
			JOptionPane.showMessageDialog(null,fehlertext);
		}	
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		String com = arg0.getActionCommand();
		if(com.equals("einlesen")){
			if(SystemConfig.sReaderAktiv.equals("0")){
				return;
			}
			einlesen();
		}else if(com.equals("speichern")){
			schreibeInDb();
		}else if(com.equals("abbrechen")){
			((JXDialog)this.getParent().getParent().getParent().getParent().getParent()).dispose();
		}

	}
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		//System.out.println("Neuanlage Pressed "+arg0.getKeyCode());
		if(arg0.getKeyCode()== 10 || arg0.getKeyCode()==0){
			arg0.consume();
			if(((JComponent)arg0.getSource()).getName().equals("einlesen")){
				einlesen();
				return;
			}
			if(((JComponent)arg0.getSource()).getName().equals("speichern")){
				schreibeInDb();
				return;
			}
			if(((JComponent)arg0.getSource()).getName().equals("abbrechen")){
				((JXDialog)this.getParent().getParent().getParent().getParent().getParent()).setVisible(false);
				((JXDialog)this.getParent().getParent().getParent().getParent().getParent()).dispose();
				return;
			}
			
		}
		if(arg0.getKeyCode()==27){
			((JXDialog)this.getParent().getParent().getParent().getParent().getParent()).setVisible(false);
			((JXDialog)this.getParent().getParent().getParent().getParent().getParent()).dispose();
		}
		
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		//System.out.println("Neuanlage Released "+arg0);		
		if(arg0.getKeyCode()== 10 || arg0.getKeyCode()==0)
			arg0.consume();
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		//System.out.println("Neuanlage Typed "+arg0);		
		if(arg0.getKeyCode()== 10 || arg0.getKeyCode()==0)
			arg0.consume();
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		Rectangle rec1 =((JComponent)arg0.getSource()).getBounds();
		Rectangle rec2 = jscr.getViewport().getViewRect();
		JViewport vp = jscr.getViewport();
		Rectangle rec3 = vp.getVisibleRect();
		if((rec1.y+((JComponent)arg0.getSource()).getHeight()) > (rec2.y+rec2.height)){
			vp.setViewPosition(new Point(rec1.x,(rec2.y+rec2.height)-rec1.height));
		}
		if(rec1.y < (rec2.y)){
			vp.setViewPosition(new Point(rec1.x,rec1.y));
		}
		jscr.validate();
		jscr.repaint();
		if( ((JComponent)arg0.getSource()).getName() != null){
			if(((JComponent)arg0.getSource()).getName().equals("arzt")){
				if(testObDialog(jtf[17].getText())){
					System.out.println("Arzt-Dialog erforderlich");
					String[] suchenach = null;
					if(jtf[17].getText().trim().length() > 1){
						suchenach = new String[] {jtf[17].getText().trim().substring(1),jtf[33].getText().trim()}; 
					}else{
						suchenach = new String[] {"",""};
					}
					arztAuswahl(suchenach);
				}
			}else if(((JComponent)arg0.getSource()).getName().equals("kasse")){
				if(testObDialog(jtf[12].getText())){
					System.out.println("Kasen-Dialog erforderlich");
					String[] suchenach = null;
					if(jtf[12].getText().trim().length() > 1){
						suchenach = new String[] {jtf[12].getText().trim().substring(1),jtf[34].getText().trim()}; 
					}else{
						suchenach = new String[] {"",""};
					}
					kassenAuswahl(suchenach);
				}
			}
		}		
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		if( ((JComponent)arg0.getSource()).getName() != null){
			System.out.println(((JComponent)arg0.getSource()).getName());
		}

		// TODO Auto-generated method stub
	}
	private void arztAuswahl(String[] suchenach){
		jtf[19].requestFocus();
		ArztAuswahl awahl = new ArztAuswahl(null,"ArztAuswahl",suchenach,new JRtaTextField[] {jtf[17],jtf[18],jtf[33]},new String(jtf[17].getText().trim()));
		awahl.setModal(true);
		awahl.setLocationRelativeTo(this);
		awahl.setVisible(true);
		awahl.dispose();
		awahl = null;

		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run(){
		 			jtf[19].requestFocus();
		 	   }
		});
		if(jtf[17].getText().indexOf("\\?") >= 0){
			String text = jtf[17].getText().replaceAll("\\?","");
			jtf[17].setText(text);
		}


	}
	private void kassenAuswahl(String[] suchenach){
		jtf[14].requestFocus();
		KassenAuswahl kwahl = new KassenAuswahl(null,"KassenAuswahl",suchenach,new JRtaTextField[] {jtf[12],jtf[13],jtf[34]},new String(jtf[12].getText().trim()));
		kwahl.setModal(true);
		kwahl.setLocationRelativeTo(this);
		kwahl.setVisible(true);
		kwahl.dispose();
		kwahl = null;
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run(){
		 			jtf[14].requestFocus();
		 	   }
		});
		if(jtf[12].getText().indexOf("\\?") >= 0){
			String text = jtf[12].getText().replaceAll("\\?","");
			jtf[12].setText(text);
		}
	}
	
	private boolean testObDialog(String string){
		if(string==null){
			return false;
		}
		if(string.trim().length() == 0){
			return false;
		}
		if(string.substring(0,1).equals("?")){
			return true;
		}
		return false;
	}


}
