package patientenFenster;

import hauptFenster.Reha;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import jxTableTools.TableTool;
import krankenKasse.KassenFormulare;
import kvKarte.KVKWrapper;
import oOorgTools.OOTools;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;

import rechteTools.Rechte;
import sqlTools.ExUndHop;
import sqlTools.SqlInfo;
import stammDatenTools.ArztTools;
import stammDatenTools.ZuzahlTools;
import sun.awt.image.ImageFormatException;
import systemEinstellungen.INIFile;
import systemEinstellungen.SystemConfig;
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
import com.jgoodies.looks.windows.WindowsTabbedPaneUI;
import com.mysql.jdbc.PreparedStatement;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import events.PatStammEvent;
import events.PatStammEventClass;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;

public class PatNeuanlage extends JXPanel implements RehaTPEventListener,Serializable,ActionListener, KeyListener,FocusListener {
	
/**
	 * 
	 */
private static final long serialVersionUID = -5089258058628709139L;
/**
	 * 
	 */

//private JXPanel Tab1 = null;
public JRtaTextField[] jtf = {null,null,null,null,null,
		                      null,null,null,null,null,
		                      null,null,null,null,null,
		                      null,null,null,null,null,
		                      null,null,null,null,null,
		                      null,null,null,null,null,
		                      null,null,null,null,null,
		                      null,null}; //,null,null, null};
JRtaCheckBox[]jcheck  ={null,null,null,null,null,
						null,null,null,null,null,
						null};


JXTable doclist = null;
MyDocTableModel docmod = null;
JButton knopf0 = null;
JButton knopf1 = null;
JButton knopf3 = null;
JButton knopf4 = null;
JButton knopf5 = null;
JButton pic0 = null;
JButton pic1 = null;
JButton pic2 = null;

JRtaComboBox cbanrede = null;

//Lemmi 20110103: Merken der Originalwerte der eingelesenen Textfelder, Combo- und Check-Boxen
Vector<Object> originale = new Vector<Object>();

String kassenid = ""; 
String befreitdatum = "";
String befreitbeginn = "";
JLabel lblbild = null;
boolean freizumstart = false;
boolean freibeimspeichern = false;
public FocusListener flis;
public boolean feldergefuellt = false;

JLabel kassenLab;
JLabel arztLab;

Font font = null;
JScrollPane jscr = null;
public List<String>xfelder = Arrays.asList( new String[] {"anrede" ,"n_name","v_name","strasse","plz","ort","geboren","telefonp",
										"telefong","telefonm","emaila","kasse","kv_nummer","v_nummer","kv_status",
										"bef_dat","artz","arzt_num","therapeut","abwanrede","abwtitel","abn_name",
										"abwv_name","abwstrasse","abwort","akutdat","termine1","termine2","kilometer",
										"heimbewohn","jahrfrei","bef_ab"});
public List<String> checks = Arrays.asList( new String[] {"abwadress","akutpat" ,"merk1","merk2","merk3","merk4",
										"merk5","merk6","heimbewohn","nobefr","u18no"});
//Achtung bei Feldgr��en �ber > 65 immer 2 abziehen wg. memofelder die nicht eingelesen werden
			   //0   1   2   3   4  5  6  7  8  9  10 1112 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36
int[] fedits =  {0 , 1 , 2 , 3 , 4, 5, 6,11, 7, 8, 9,10,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36 };
int[] ffelder = {0 , 1 , 2 , 3 ,21,23,24, 4,18,19,20,50,13,14,16,15,31,25,26,56, 6, 7, 8, 9,10,11,12,46,34,36,37,48,40,65,66,67,41};
int[] fchecks =  {0 , 1 , 2 , 3 , 4  , 5 ,6 , 7 , 8, 9};
int[] ffelder2 = {5 ,33 ,62 ,61 , 60 ,59 ,58, 57, 44,68};
Vector<String> patDaten = null;
ImageIcon hgicon;
int icx,icy;
AlphaComposite xac1 = null;
AlphaComposite xac2 = null;	
String feldname = "";
String globPat_intern = "";
public static String arztbisher = "";
public static String kassebisher = "";
boolean inNeu = false;
Vector<String> titel = new Vector<String>() ;
Vector<String> formular = new Vector<String>();
int iformular = -1;

private RehaTPEventClass rtp = null;

boolean editvoll = false;
private JRtaTextField formularid = new JRtaTextField("NIX",false);

private KVKWrapper kvw;

boolean startMitBild = false;
boolean updateBild = false;

	public PatNeuanlage(Vector<String> vec,boolean neu,String sfeldname){
		setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				setBackgroundPainter(Reha.thisClass.compoundPainter.get("PatNeuanlage"));
				return null;
			}
		}.execute();
		this.setDoubleBuffered(true);
		this.patDaten = vec;
		this.inNeu = neu;
		if(inNeu){
			editvoll = Rechte.hatRecht(Rechte.Patient_anlegen, false);
		}else{
			editvoll = Rechte.hatRecht(Rechte.Patient_editvoll, false);			
		}
		this.feldname = sfeldname;
		this.setBackground(Color.WHITE);
		this.setLayout(new BorderLayout());
		this.font = new Font("Tahome",Font.BOLD,11);
		this.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		
		add(getButtonPanel(),BorderLayout.SOUTH);

		UIManager.put("TabbedPane.tabsOpaque", Boolean.FALSE);
		UIManager.put("TabbedPane.contentOpaque", Boolean.FALSE);
		
		JTabbedPane patTab = new JTabbedPane();
		try{
			patTab.setUI(new WindowsTabbedPaneUI());
		}catch(Exception ex){
			// Kein KarstenLentsch LAF
		}
		//TabbedPaneUI tpUi = patTab.getUI();

		patTab.setOpaque(false);
		
		
		
		patTab.addTab("1 - Stammdaten", Tab1());
		patTab.addTab("2 - Zusätze", Tab2());
		patTab.addTab("3 - Sonstiges", Tab3());
		patTab.setMnemonicAt(0, (int) '1');
		patTab.setMnemonicAt(1, (int) '2');		
		patTab.setMnemonicAt(2, (int) '3');

		if(!editvoll){
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					for(int i = 0; i < jtf.length;i++){
						if(jtf[i] != null){
							jtf[i].setEnabled(false);	
						}
					}
					for(int i = 0; i < jcheck.length;i++){
						if(jcheck[i] != null){
							jcheck[i].setEnabled(false);	
						}
					}
					doclist.setEnabled(false);
					pic0.setEnabled(false);
					pic1.setEnabled(false);
					pic2.setEnabled(false);
					knopf0.setEnabled(false);
					knopf1.setEnabled(false);
					knopf3.setEnabled(false);
					cbanrede.setEnabled(false);
					jcheck[1].setEnabled(true);
					jtf[7].setEnabled(true);
					jtf[8].setEnabled(true);
					jtf[9].setEnabled(true);
					jtf[10].setEnabled(true);
					jtf[19].setEnabled(true);
					jtf[27].setEnabled(true);
					jtf[28].setEnabled(true);
					jtf[29].setEnabled(true);
					jtf[30].setEnabled(true);

					return null;
				}
			}.execute();
			
		}

		add(patTab,BorderLayout.CENTER);
		
		hgicon = Reha.rehaBackImg;
		icx = hgicon.getIconWidth()/2;
		icy = hgicon.getIconHeight()/2;
		xac1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.07f); 
		xac2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f);	
		
		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener((RehaTPEventListener) this);
		this.addKeyListener(this);

//****************Checken ob Preisgruppen bedient werden****************		
		
		// nur für den ersten Focus setzen
		flis = new FocusListener(){

			@Override
			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub
				try{
					if( ((JComponent)arg0.getSource()).getName().equals("PatientenNeuanlage") ){
						if(inNeu || feldname.equals("")){
							setzeFocus();
						}else{
							if(Reha.thisClass.patpanel.kid < 0 && feldname.equals("KASSE")){
								if(feldergefuellt){
									//jtf[12].setText("?"+jtf[12].getText());
								}
							}
							if(Reha.thisClass.patpanel.aid < 0 && feldname.equals("ARZT")){
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
		 				kvw = new KVKWrapper(SystemConfig.sReaderName);
		 				kvw.KVK_Einlesen();
		 			}
		 	   }
		});
		UIManager.put("TabbedPane.tabsOpaque", Boolean.TRUE);
		UIManager.put("TabbedPane.contentOpaque", Boolean.TRUE);
	
		validate();
		repaint();
		
		// Lemmi 20110103: Merken der Originalwerte der eingelesenen Textfelder
//		SaveChangeStatus();
	}
	
	
	
	public void geheAufFeld(String feld){
		////System.out.println("Focus setzen auf "+feld);
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
	public PatNeuanlage getInstance(){
		return this;
	}
	
	private void fuelleFelder(){
		//final String xfeld = this.feldname;

		new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {

		 		List<String> nichtlesen = Arrays.asList(new String[] {"anamnese","pat_text"});
				Vector<?> felder = SqlInfo.holeSatz("pat5", "*", "pat_intern='"+Reha.thisClass.patpanel.aktPatID+"'",nichtlesen);
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
								name.contains("bef_ab") ||
								name.contains("er_dat")){
							String datum = String.valueOf((String)felder.get(ffelder[i]));
							if(datum.trim().length() > 0){
								////System.out.println("Datum w�re gewesen->"+datum+" L�nge->"+datum.trim().length());
								jtf[fedits[i]].setText(DatFunk.sDatInDeutsch(datum) );								
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
				arztbisher = jtf[17].getText();
				kassebisher = jtf[12].getText(); 
				kassenid = Reha.thisClass.patpanel.patDaten.get(68);
				befreitdatum = DatFunk.sDatInDeutsch(Reha.thisClass.patpanel.patDaten.get(31));
				freizumstart = (Reha.thisClass.patpanel.patDaten.get(30).equals("T") ? true : false);
				if(!jtf[35].getText().trim().equals("")){
					jcheck[10].setEnabled(true);
				}
				
				//System.out.println("Gehe auf Feld 1 -> "+getInstance().feldname);
				if(! "".equals(getInstance().feldname)){
					SwingUtilities.invokeLater(new Runnable(){
					 	   public  void run(){
								//System.out.println("Gehe auf Feld 2 -> "+getInstance().feldname);
								feldergefuellt = true;
								geheAufFeld(getInstance().feldname);
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
		//String wert = "";
		String spatintern;
		StringBuffer buf = new StringBuffer();
		if(this.inNeu){
			buf.append("insert into pat5 set "); 
		}else{
			buf.append("update pat5 set ");			
		}

		for(int i = 1; i < anzahlf;i++){
			name = jtf[fedits[i]].getName().trim();
			if(name.contains("geboren") || 
					name.contains("akutdat") || 
					name.contains("akutbis") || 
					name.contains("bef_dat") ||
					name.contains("bef_ab") ||
					name.contains("er_dat")){
				if(jtf[fedits[i]].getText().trim().equals(".  .")){
					buf.append(name+"=NULL, ");	
					if(name.equals("bef_dat")){ //Wenn befreit bis testen ob er wert gr��er als heute
						buf.append("befreit ='F', ");
						freibeimspeichern = false;
						//System.out.println("Patient ist -> nicht <- befreit!");
					}
				}else{
					try{
						buf.append(name+"='"+DatFunk.sDatInSQL(jtf[fedits[i]].getText())+"', ");
						if(name.equals("bef_dat")){ //Wenn befreit bis testen ob er wert gr��er als heute
							if( DatFunk.DatumsWert(jtf[fedits[i]].getText()) >= DatFunk.DatumsWert(DatFunk.sHeute()) ){
								buf.append("befreit ='T', ");
								freibeimspeichern = true;
								//System.out.println("Patient ist befreit!");
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
			}
		}
		buf.append("anrede='"+((String)cbanrede.getSelectedItem()).trim()+"'");
		for(int i = 0; i < anzahlc;i++){
			name = jcheck[i].getName();
			buf.append(", "+name+"='"+(jcheck[i].isSelected() ? "T' " : "F' " ));
		}

		if(!this.inNeu){
			globPat_intern = Reha.thisClass.patpanel.aktPatID;
			buf.append(" where pat_intern='"+globPat_intern+"' LIMIT 1");
			spatintern = Reha.thisClass.patpanel.aktPatID;
			// Wenn Kasse veränderr wurde....
			if(!jtf[34].getText().trim().equals(kassenid)){
				JOptionPane.showMessageDialog(null, "Achtung - Sie haben dem Patient eine neue Kasse zugewiesen.\n"+
						"Eventuell ändert sich dadurch der Zuzahlungsstatus vorhandener Rezepte. Bitte prüfen!!!");
			}
			@SuppressWarnings("unused")
			int zzregel = -1;
			boolean doof = false;
			if(! (freizumstart == freibeimspeichern)){
				if ( ((zzregel = ZuzahlTools.getZuzahlRegel(jtf[34].getText().trim())) <= 0) ){
					//System.out.println("Zuzahlregel = "+zzregel+" Kassen-ID = "+jtf[34].getText().trim());
				//if ( ((zzregel = ZuzahlTools.getZuzahlRegel(jtf[34].getText().trim())) <= 0) && freibeimspeichern){
					JOptionPane.showMessageDialog(null,"Sie haben einen Kostenträger gwählt der keine Zuzahlung verlangt und\n"+
							"jetzt wollen Sie im Feld Zuzahlungsbefreiung rummurksen???????\n\nNa ja.....");
					doof = true;
				}
				if(!doof){
					// hier wäre es optimal eine  ZuzahlToolsFunktion zu haben.....
					int anzahl = SqlInfo.zaehleSaetze("verordn", "pat_intern='"+Reha.thisClass.patpanel.aktPatID+"' AND REZ_GEB='0.00'");
					if(anzahl > 0){
						String meldung = "Dieser Patient hat -> "+anzahl+" laufende Rezepte <- ohne Abschluss\n"+
						"Soll der veränderte Befreiungsstatus auf alle noch nicht(!) bezahlten Rezepte übertragen werden?";
						int frage = JOptionPane.showConfirmDialog(null,meldung,"Achtung wichtige Benuterzanfrage",JOptionPane.YES_NO_OPTION);
						if(frage == JOptionPane.NO_OPTION){
							JOptionPane.showMessageDialog(null,"Dann eben nicht!\nVergessen Sie aber nicht den Befreiungsstatus der Rezepte von Hand zu ändern");
						}else if(frage == JOptionPane.YES_OPTION){
							String pat_intern = Reha.thisClass.patpanel.aktPatID;
							String geboren = DatFunk.sDatInDeutsch(Reha.thisClass.patpanel.patDaten.get(4));
							String befreit = (freibeimspeichern ? "T" : "F");
							@SuppressWarnings("unused")
							String datum = (freibeimspeichern ? "" : jtf[16].getText().trim());
							ZuzahlTools.zzStatusEdit(pat_intern, geboren, "", befreit, jtf[34].getText().trim());
						}
					}
				}else{
					////System.out.println("Doof = true and zzregel = "+zzregel);					
				}

			}
		}else{
			// Angelegt von aufgenommen werden
			//int neuid = SqlInfo.holeId("pat5", "n_name");
			patintern = SqlInfo.erzeugeNummer("pat"); //neuid+Reha.thisClass.patiddiff;
			if(patintern < 0){
				JOptionPane.showMessageDialog(null,"Fehler beim Bezug einer neuen Patientennummer\nNeustart des Programmes vermutlich erforderlich");
				return;
			}
			globPat_intern = Integer.toString(patintern); //Integer.valueOf(patintern).toString();
			buf.append(",anl_datum='"+DatFunk.sDatInSQL(DatFunk.sHeute())+"' ");
			buf.append(",pat_intern='"+globPat_intern +"'"); // where id='"+Integer.valueOf(neuid).toString()+"'");
			spatintern = Integer.toString(patintern);
		}
		SqlInfo.sqlAusfuehren(buf.toString());
		//new ExUndHop().setzeStatement(buf.toString());
		if(updateBild){
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					speichernPatBild( ( ((inNeu) || (!startMitBild)) ? true : false ),(ImageIcon)lblbild.getIcon(),globPat_intern);
					return null;
				}
				
			}.execute();
			
		}

		((JXDialog)this.getParent().getParent().getParent().getParent().getParent()).setVisible(false);
		//final PatNeuanlage xthis = this;

		String rez_num = "";
		if(Reha.thisClass.patpanel.aktRezept.tabaktrez.getRowCount()>0){
			int row = Reha.thisClass.patpanel.aktRezept.tabaktrez.getSelectedRow();
			if(row>=0){
				rez_num = Reha.thisClass.patpanel.aktRezept.tabaktrez.getValueAt(row,0).toString().trim();
			}
		}
		final String xpatintern = spatintern;
		final String xrez = rez_num;
		new Thread(){
			public void run(){
				Reha.thisClass.patpanel.getLogic().arztListeSpeichernVector((Vector<?>)docmod.getDataVector().clone(), inNeu, String.valueOf(globPat_intern));
//				new ArztListeSpeichern((Vector)docmod.getDataVector().clone(),inNeu,globPat_intern);
				//System.out.println("Es wirde die ArztListe gespeichert.....");
				finalise();
				((JXDialog)getInstance().getParent().getParent().getParent().getParent().getParent()).dispose();
				String s1 = "#PATSUCHEN";
				String s2 = xpatintern;
				PatStammEvent pEvt = new PatStammEvent(getInstance());
				pEvt.setPatStammEvent("PatSuchen");
				pEvt.setDetails(s1,s2,xrez);
				PatStammEventClass.firePatStammEvent(pEvt);
				pEvt = null;
				
			}
		}.start();


	}
	private JXPanel getButtonPanel(){
		JXPanel but = new JXPanel(new BorderLayout());
		//but.add(new JLabel("Hier ist Raum f�r Buttons"));
		but.setOpaque(false);
		but.setDoubleBuffered(true);
		
		knopf3 = new JButton("Chipkarte");
		knopf3.setPreferredSize(new Dimension(70, 20));
		knopf3.addActionListener(this);		
		knopf3.setActionCommand("einlesen");
		knopf3.setName("einlesen");
		knopf3.addKeyListener(this);
		knopf3.setMnemonic(KeyEvent.VK_C);
		//if(SystemConfig.sReaderAktiv.equals("0")){
			knopf3.setEnabled(false);
		//}
	
		knopf4 = new JButton("speichern");
		knopf4.setPreferredSize(new Dimension(70, 20));
		knopf4.addActionListener(this);		
		knopf4.setActionCommand("speichern");
		knopf4.setName("speichern");
		knopf4.addKeyListener(this);
		knopf4.setMnemonic(KeyEvent.VK_S);
		
		knopf5 = new JButton("abbrechen");
		knopf5.setPreferredSize(new Dimension(70, 20));
		knopf5.addActionListener(this);		
		knopf5.setActionCommand("abbrechen");
		knopf5.setName("abbrechen");
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

		private JXPanel Tab1(){
		JXPanel tab1 = new JXPanel(new BorderLayout());
		tab1.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		tab1.setOpaque(false);
		tab1.setDoubleBuffered(true);
		tab1.add(getDatenPanel12(),BorderLayout.EAST);
		tab1.add(getDatenPanel11(),BorderLayout.WEST);
		tab1.validate();	
		return tab1;	
		}
	
		private JXPanel Tab2(){
			JXPanel tab2 = new JXPanel(new BorderLayout());
			tab2.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
			tab2.setOpaque(false);
			tab2.setDoubleBuffered(true);
			tab2.add(getDatenPanel22(),BorderLayout.EAST);
			tab2.add(getDatenPanel21(),BorderLayout.WEST);
			tab2.validate();	
			holeFormulare();
			return tab2;	
			}
		
		private JXPanel Tab3(){
			JXPanel tab3 = new JXPanel(new BorderLayout());
			tab3.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
			tab3.setOpaque(false);
			tab3.setDoubleBuffered(true);
			tab3.add(getDatenPanel32(),BorderLayout.EAST);
			tab3.add(getDatenPanel31(),BorderLayout.WEST);
				
			return tab3;	
			}
	
		private JXPanel getDatenPanel12(){
	
		JXPanel pat12 = new JXPanel(new BorderLayout());
		//but.setBorder(null);
		pat12.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		pat12.setOpaque(false);
		pat12.setDoubleBuffered(true);
		
		 
	
		jtf[12] = new JRtaTextField("GROSS", true);
		jtf[12].setName("kasse");
		jtf[12].addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent arg0) {
				////System.out.println("code = "+arg0.getKeyCode());
				////System.out.println("char = "+arg0.getKeyChar());
				if(arg0.getKeyChar()=='?'){
					String suchkrit = jtf[12].getText().replaceAll("\\?", "");
					kassenAuswahl(new String[] {suchkrit,jtf[34].getText().trim(),jtf[34].getText()});
				}
			}
		});
		jtf[12].addFocusListener(this);
		
		jtf[13] = new JRtaTextField("ZAHLEN", true);
		jtf[13].setName("kv_nummer");

		jtf[14] = new JRtaTextField("ZAHLEN", true);
		jtf[14].setName("v_nummer");

		jtf[15] = new JRtaTextField("ZAHLEN", true);
		jtf[15].setName("kv_status");

		jtf[16] = new JRtaTextField("DATUM", true);
		jtf[16].setName("bef_dat"); //aus Kostentr�gerdatei/Karte einlesen?
		jtf[16].setFont(font);
		jtf[16].setForeground(Color.RED);
		
		jtf[36] = new JRtaTextField("DATUM", true);
		jtf[36].setName("bef_ab");
		jtf[36].setFont(font);
		jtf[36].setForeground(Color.RED);
		
		
		jtf[17] = new JRtaTextField("GROSS", true);
		jtf[17].setName("arzt");
		jtf[17].addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyChar()=='?'){
					arg0.consume();
					String[] suchkrit =  {jtf[17].getText().replaceAll("\\?", ""),jtf[33].getText()};
					arztAuswahl(suchkrit);
				}
			}
				
		});
		jtf[17].addFocusListener(this);
		
		jtf[18] = new JRtaTextField("ZAHLEN", true);
		jtf[18].setName("arzt_num");
		
		jtf[19] = new JRtaTextField("GROSS", true);
		jtf[19].setName("therapeut");
		
		
		
	
		
		jtf[33] = new JRtaTextField("ZAHLEN", true);
		jtf[33].setName("arztid");
		
		jtf[34] = new JRtaTextField("ZAHLEN", true);
		jtf[34].setName("kassenid");
		
		
											//      1.                2.    3.     4.                 5.    6.    
				FormLayout lay12 = new FormLayout("right:max(80dlu;p), 4dlu, 60dlu,right:max(60dlu;p), 4dlu, 60dlu",
					       //1.   2.   3.   4.  5.   6   7   8    9   10   11  12  13  14   15   16  17  18   19   20   21  22   23  24   25  26    27  28   29   30   31   32  33  34   35  36   37  38   39    40  41  42  43   44   45  46  47  48    49   50   51 52   53  54   55  56  57   58   59   60  61   62  63  64   65   66   67
							"p, 10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 82dlu, p, 20dlu, p, 2dlu, p, 2dlu, p, 2dlu, p");
							
					PanelBuilder builder12 = new PanelBuilder(lay12);
					builder12.setDefaultDialogBorder();
					builder12.getPanel().setOpaque(false);	
					CellConstraints cc12 = new CellConstraints();
					builder12.getPanel().setDoubleBuffered(true);
					
					builder12.addSeparator("Krankenversicherung", cc12.xyw(1, 1, 6));
					kassenLab = new JLabel("Kasse *)");
					kassenLab.setIcon(SystemConfig.hmSysIcons.get("kleinehilfe"));
					kassenLab.setHorizontalTextPosition(JLabel.LEFT);
					
					kassenLab.addMouseListener(new MouseAdapter(){
						public void mousePressed(MouseEvent ev){
							if(editvoll){
								SwingUtilities.invokeLater(new Runnable(){
									public void run(){
										String suchkrit = jtf[12].getText().replace("?", "");
										kassenAuswahl(new String[] {suchkrit,jtf[34].getText().trim(),jtf[34].getText()});
									}
								});
							}
						}
					});
					builder12.add(kassenLab, cc12.xy(1, 3));
					builder12.add(jtf[12], cc12.xyw(3, 3, 4));
					builder12.addLabel("Kassen-IK", cc12.xy(1,5));
					builder12.add(jtf[13], cc12.xyw(3, 5, 4));
					builder12.addLabel("Vers-Nr.", cc12.xy(1,7));
					builder12.add(jtf[14], cc12.xyw(3, 7, 4));
					builder12.addLabel("Status", cc12.xy(1, 9));
					builder12.add(jtf[15], cc12.xy(3,9));
					builder12.addLabel("Befreit von", cc12.xy(1, 11));
					builder12.add(jtf[36], cc12.xy(3, 11));
					builder12.addLabel("bis ", cc12.xy(4,11));
					builder12.add(jtf[16], cc12.xy(6, 11));
					
					builder12.addSeparator("Arzt / Therapeut", cc12.xyw(1, 23, 6));
					arztLab = new JLabel("Hausarzt *)");
					arztLab.setIcon(SystemConfig.hmSysIcons.get("kleinehilfe"));
					arztLab.setHorizontalTextPosition(JLabel.LEFT);
					arztLab.addMouseListener(new MouseAdapter(){
						public void mousePressed(MouseEvent ev){
							if(editvoll){
								SwingUtilities.invokeLater(new Runnable(){
									public void run(){
										String[] suchkrit =  {jtf[17].getText().replace("?", ""),jtf[33].getText()};
										arztAuswahl(suchkrit);
									}
								});
							}
						}
					});
					builder12.add(arztLab, cc12.xy(1, 25));
					builder12.add(jtf[17], cc12.xyw(3, 25, 4));
					builder12.addLabel("ArztNummer (LANR)", cc12.xy(1, 27));
					builder12.add(jtf[18], cc12.xyw(3,27,4));
					builder12.addLabel("Betreuer/Therapeut", cc12.xy(1, 29));
					builder12.add(jtf[19], cc12.xyw(3,29,4));
		
		JScrollPane jscrzusatz = JCompTools.getTransparentScrollPane(builder12.getPanel());
		jscrzusatz.getVerticalScrollBar().setUnitIncrement(15);
		jscrzusatz.getViewport().setOpaque(false);
		jscrzusatz.setBorder(null);
		jscrzusatz.setViewportBorder(null);
		jscrzusatz.validate();
		jscrzusatz.addKeyListener(this);
		pat12.add(jscrzusatz,BorderLayout.CENTER);
		pat12.validate();
		return pat12;
	}
		
	private void finalise(){
		for(int i = 0; i < jtf.length; i++){
			if(jtf[i]!= null){
				ListenerTools.removeListeners(jtf[i]);
				jtf[i] = null;
			}

		}
		for(int i = 0; i < jcheck.length; i++){
			if(jcheck[i]!= null){
				ListenerTools.removeListeners(jcheck[i]);
				jcheck[i] = null;
			}	
		}
		ListenerTools.removeListeners(knopf0);
		ListenerTools.removeListeners(knopf1);
		ListenerTools.removeListeners(knopf3);
		ListenerTools.removeListeners(knopf4);
		ListenerTools.removeListeners(knopf5);
		xfelder = null;
		checks = null;
		doclist = null;
		docmod = null;
		if(rtp != null){
			rtp.removeRehaTPEventListener((RehaTPEventListener) this);
			rtp = null;
		}
	}
	
	
	private JXPanel getDatenPanel21(){
		JXPanel pat21 = new JXPanel(new BorderLayout());
		//but.setBorder(null);
		pat21.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		pat21.setOpaque(false);
		pat21.setDoubleBuffered(true);
		
		
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
		
		jtf[31] = new JRtaTextField("ZAHLEN", true);
		jtf[31].setName("kilometer");
		
		jtf[32] = new JRtaTextField("DATUM", true);
		jtf[32].setName("er_dat");
		
		jtf[35] = new JRtaTextField("ZAHLEN", true);
		jtf[35].setName("jahrfrei");
		jtf[35].setEnabled(false);
		
		jcheck[0] = new JRtaCheckBox();
		jcheck[0].setOpaque(false);
		jcheck[0].setName("abwadress");
		
		jcheck[8] = new JRtaCheckBox();
		jcheck[8].setOpaque(false);
		jcheck[8].setName("heimbewohn");	
		
		jcheck[10] = new JRtaCheckBox();
		jcheck[10].setOpaque(false);
		jcheck[10].setName("vorjahrfrei");
		jcheck[10].setActionCommand("vorjahrfrei");
		//jcheck[10].setEnabled(false);
		jcheck[10].addActionListener(this);
		
		jcheck[9] = new JRtaCheckBox();
		jcheck[9].setOpaque(false);
		jcheck[9].setName("u18ignore");
		jcheck[9].setActionCommand("u18ignore");
		jcheck[9].setEnabled(false);
		jcheck[9].addActionListener(this);
		
	
		FormLayout lay21 = new FormLayout("right:max(80dlu;p), 4dlu, 60dlu,right:max(60dlu;p), 4dlu, 60dlu",
			       //1.   2.   3.   4.  5.   6   7   8    9   10   11  12   13  14    15   16  17  18   19   20  21  22   23  24   25  26   27  28  29   30   31   32  33  34   35  36   37  38   39    40  41  42  43   44   45  46  47  48    49   50   51 52   53  54   55  56  57   58   59   60  61   62  63  64   65   66   67
					"p, 10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p");
		
					PanelBuilder builder21 = new PanelBuilder(lay21);
					builder21.setDefaultDialogBorder();
					builder21.getPanel().setOpaque(false);	
					CellConstraints cc21 = new CellConstraints();
					builder21.getPanel().setDoubleBuffered(true);
				
					builder21.addSeparator("Zusätze", cc21.xyw(1, 1, 6));
					builder21.addLabel("Kilometer bei HB", cc21.xy(1, 3));
					builder21.add(jtf[31], cc21.xy(3, 3));
					builder21.addLabel("Heimbewohner", cc21.xy(1,5));
					builder21.add(jcheck[8], cc21.xy(3, 5));
					builder21.addLabel("befreit im Vorjahr", cc21.xy(1,7));
					builder21.add(jtf[35],cc21.xy(3,7));
					builder21.addLabel("löschen?", cc21.xy(4,7));
					builder21.add(jcheck[10],cc21.xy(6, 7));
					builder21.addLabel("U18-Regel ignorieren?",cc21.xy(1,9));
					builder21.add(jcheck[9],cc21.xy(3, 9));
					builder21.addLabel("Vertrag unterz. am", cc21.xy(1, 11));
					builder21.add(jtf[32], cc21.xy(3, 11));
					
					builder21.addSeparator("abweichender Rechnungsempfänger/Versicherter", cc21.xyw(1, 13, 6));
					builder21.addLabel("verwenden", cc21.xy(1,15));
					builder21.add(jcheck[0], cc21.xy(3,15));
					builder21.addLabel("Anrede", cc21.xy(1, 17));
					builder21.add(jtf[20], cc21.xy(3, 17));
					builder21.addLabel("Titel", cc21.xy(4, 17));
					builder21.add(jtf[21], cc21.xy(6, 17));
					builder21.addLabel("Nachname", cc21.xy(1, 19));
					builder21.add(jtf[22], cc21.xyw(3, 19, 4));
					builder21.addLabel("Vorname", cc21.xy(1, 21));
					builder21.add(jtf[23], cc21.xyw(3,21,4));
					builder21.addLabel("Strasse, Nr.", cc21.xy(1, 23));
					builder21.add(jtf[24], cc21.xyw(3, 23, 4));
					builder21.addLabel("PLZ, Ort", cc21.xy(1, 25));
					builder21.add(jtf[25], cc21.xy(3, 25));
					builder21.add(jtf[26], cc21.xyw(4, 25, 3));
				
					
		
					JScrollPane jscrzusatz = JCompTools.getTransparentScrollPane(builder21.getPanel());
					jscrzusatz.getVerticalScrollBar().setUnitIncrement(15);
					jscrzusatz.getViewport().setOpaque(false);
					jscrzusatz.setBorder(null);
					jscrzusatz.setViewportBorder(null);
					jscrzusatz.validate();
					jscrzusatz.addKeyListener(this);
					pat21.add(jscrzusatz,BorderLayout.CENTER);
					pat21.validate();
					return pat21;
	}
	
	private JXPanel getDatenPanel22(){
		JXPanel pat22 = new JXPanel(new BorderLayout());
		//but.setBorder(null);
		pat22.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		pat22.setOpaque(false);
		pat22.setDoubleBuffered(true);
		
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
	
		//die Labeltexte merk2 bis merk7 aus Datenbank/SysINI einlesen?
		docmod = new MyDocTableModel();
		docmod.setColumnIdentifiers(new String[] {"LANR","Nachname","Strasse","Ort","BSNR",""});
		doclist = new JXTable(docmod);
		doclist.getColumn(5).setMinWidth(0);
		doclist.getColumn(5).setMaxWidth(0);
		doclist.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent arg0) {
				
				if(arg0.getClickCount()==2 && arg0.getButton()==1){
					final Point pt = arg0.getLocationOnScreen();
					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() throws Exception {
							try{
								arztInListeDoppelKlick(pt);								
							}catch(Exception ex){
								ex.printStackTrace();
							}
							return null;
						}
						
					}.execute();
				}
			}	
			
		});
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				doArztListe();
				return null;
			}
		}.execute();
		JScrollPane docscr = JCompTools.getTransparentScrollPane(doclist);
		docscr.validate();
		
		knopf0 = new JButton("entfernen");
		knopf0.setPreferredSize(new Dimension(70, 20));
		knopf0.addActionListener(this);	
		knopf0.setName("entfernen");
		knopf0.setActionCommand("deldoc");
		knopf0.addKeyListener(this);
		knopf1 = new JButton("hinzu");
		knopf1.setPreferredSize(new Dimension(70, 20));
		knopf1.addActionListener(this);
		knopf1.setName("hinzu");
		knopf1.setActionCommand("adddoc");
		knopf1.addKeyListener(this);
		 
		
		FormLayout lay22 = new FormLayout("right:max(80dlu;p), 4dlu, 60dlu,right:max(60dlu;p), 4dlu, 60dlu",
			       //1.   2.   3.   4.  5.   6   7   8     9   10   11      12   13  14    15   16  17  18   19   20  21  22   23  24   25  26   27  28  29   30   31   32  33  34   35  36   37  38   39    40  41  42  43   44   45  46  47  48    49   50   51 52   53  54   55  56  57   58   59   60  61   62  63  64   65   66   67
					"p, 10dlu, p, 2dlu, p, 2dlu, p, 42dlu, p, 11dlu, 90dlu, 5dlu, p");
		
					PanelBuilder builder22 = new PanelBuilder(lay22);
					builder22.setDefaultDialogBorder();
					builder22.getPanel().setOpaque(false);	
					CellConstraints cc22 = new CellConstraints();
					builder22.getPanel().setDoubleBuffered(true);
					 
					builder22.addSeparator("individuelle Merkmale", cc22.xyw(1, 1, 6));

					builder22.add(jcheck[2], cc22.xy(3, 3));
					builder22.add(jcheck[3], cc22.xy(6, 3));
					builder22.add(jcheck[4], cc22.xy(3, 5));
					builder22.add(jcheck[5], cc22.xy(6, 5));
					builder22.add(jcheck[6], cc22.xy(3, 7));
					builder22.add(jcheck[7], cc22.xy(6,7));
					
					builder22.addSeparator("Ärzteliste des Patienten", cc22.xyw(1,9,6));
					builder22.add(docscr, cc22.xyw(1, 11, 6));
					builder22.addLabel("Arzt aufnehmen", cc22.xy(1,13));
					builder22.add(knopf1, cc22.xy(3,13));
					builder22.addLabel("Arzt entfernen", cc22.xy(4,13));
					builder22.add(knopf0, cc22.xy(6,13));
					
					
		
		JScrollPane jscrzusatz = JCompTools.getTransparentScrollPane(builder22.getPanel());
		jscrzusatz.getVerticalScrollBar().setUnitIncrement(15);
		jscrzusatz.getViewport().setOpaque(false);
		jscrzusatz.setBorder(null);
		jscrzusatz.setViewportBorder(null);
		jscrzusatz.validate();
		jscrzusatz.addKeyListener(this);
		pat22.add(jscrzusatz,BorderLayout.CENTER);
		pat22.validate();
		return pat22;
	}	
	public void setNewPic(ImageIcon img){
		lblbild.setText("");
		lblbild.setIcon(img);
		updateBild = true;
	}
	
	private JXPanel getDatenPanel31(){
		JXPanel pat31 = new JXPanel(new BorderLayout());
		//but.setBorder(null);
		pat31.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		pat31.setOpaque(false);
		pat31.setDoubleBuffered(true);
		
		
		pic0 = new JButton("verwerfen");
		pic0.setPreferredSize(new Dimension(70, 20));
		pic0.addActionListener(this);		
		pic0.setActionCommand("delpic");
		pic0.addKeyListener(this);
		pic1 = new JButton("Aufnahme");
		pic1.setPreferredSize(new Dimension(70, 20));
		pic1.addActionListener(this);		
		pic1.setActionCommand("addpic");
		pic1.addKeyListener(this);
		pic2 = new JButton("Bilddatei");
		pic2.setPreferredSize(new Dimension(70, 20));
		pic2.addActionListener(this);		
		pic2.setActionCommand("addjpg");
		pic2.addKeyListener(this);
		 
		
		FormLayout lay31 = new FormLayout("right:max(80dlu;p), 4dlu, 175px,right:max(60dlu;p), 4dlu, 60dlu",
			       //1.   2.   3.   4.  5.   6   7   8     9   10   11      12   13  14    15   16  17  18   19   20  21  22   23  24   25  26   27  28  29   30   31   32  33  34   35  36   37  38   39    40  41  42  43   44   45  46  47  48    49   50   51 52   53  54   55  56  57   58   59   60  61   62  63  64   65   66   67
					"p, 10dlu, 220px, 2dlu, p");
		
					PanelBuilder builder31 = new PanelBuilder(lay31);
					builder31.setDefaultDialogBorder();
					builder31.getPanel().setOpaque(false);	
					CellConstraints cc31 = new CellConstraints();
					builder31.getPanel().setDoubleBuffered(true);
					 
					builder31.addSeparator("Patientenfoto", cc31.xyw(1, 1, 6));
					lblbild = new JLabel();
					builder31.add(lblbild, cc31.xy(3, 3));
					builder31.addLabel("Bild aufnehmen", cc31.xy(1,5));
					/****/
					JXPanel pan = new JXPanel();
					pan.setOpaque(false);
					FormLayout lay2 = new FormLayout("fill:0:grow(0.5),2px,fill:0:grow(0.5)","p");
					pan.setLayout(lay2);
					CellConstraints cc2 = new CellConstraints();
					pan.add(pic1, cc2.xy(1,1,CellConstraints.FILL,CellConstraints.DEFAULT));
					pan.add(pic2, cc2.xy(3,1,CellConstraints.FILL,CellConstraints.DEFAULT));
					builder31.add(pan, cc31.xy(3,5));
					/****/
					//builder31.add(pic1, cc31.xy(3,5));
					builder31.addLabel("Bild verwerfen", cc31.xy(4,5));
					builder31.add(pic0, cc31.xy(6,5));
					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() throws Exception {
							try{
							if(! inNeu){
								BufferedImage img = holePatBild(Reha.thisClass.patpanel.aktPatID);
								if(img==null){
									lblbild.setText("Kein Bild des Patienten vorhanden");	
								}else{
									lblbild.setText("");
									lblbild.setIcon(new ImageIcon(img));
									startMitBild = true;
								}
							}else{
								lblbild.setText("Kein Bild des Patienten vorhanden");
								lblbild.setIcon(null);
							}
							}catch(Exception ex){
								ex.printStackTrace();
							}
							return null;
						}
					}.execute();
					
					
		
		JScrollPane jscrzusatz = JCompTools.getTransparentScrollPane(builder31.getPanel());
		jscrzusatz.getVerticalScrollBar().setUnitIncrement(15);
		jscrzusatz.getViewport().setOpaque(false);
		jscrzusatz.setBorder(null);
		jscrzusatz.setViewportBorder(null);
		jscrzusatz.validate();
		jscrzusatz.addKeyListener(this);
		pat31.add(jscrzusatz,BorderLayout.CENTER);
		pat31.validate();
		return pat31;
	}
	
	@SuppressWarnings("unused")
	private ImageIcon getPatBild(){
		return null;
	}
	
	private JXPanel getDatenPanel32(){
		JXPanel pat32 = new JXPanel(new BorderLayout());
		//but.setBorder(null);
		pat32.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		pat32.setOpaque(false);
		pat32.setDoubleBuffered(true);
		
		
		
		 
		
		FormLayout lay32 = new FormLayout("right:max(80dlu;p), 4dlu, 60dlu,right:max(60dlu;p), 4dlu, 60dlu",
			       //1.   2.   3.   4.  5.   6   7   8     9   10   11      12   13  14    15   16  17  18   19   20  21  22   23  24   25  26   27  28  29   30   31   32  33  34   35  36   37  38   39    40  41  42  43   44   45  46  47  48    49   50   51 52   53  54   55  56  57   58   59   60  61   62  63  64   65   66   67
					"p, 10dlu, 160dlu, 2dlu, p");
		
					PanelBuilder builder32 = new PanelBuilder(lay32);
					builder32.setDefaultDialogBorder();
					builder32.getPanel().setOpaque(false);	
					CellConstraints cc32 = new CellConstraints();
					builder32.getPanel().setDoubleBuffered(true);
					 
					builder32.addLabel("Space for future Extensions", cc32.xyw(1,3, 6));
					
		JScrollPane jscrzusatz = JCompTools.getTransparentScrollPane(builder32.getPanel());
		jscrzusatz.getVerticalScrollBar().setUnitIncrement(15);
		jscrzusatz.getViewport().setOpaque(false);
		jscrzusatz.setBorder(null);
		jscrzusatz.setViewportBorder(null);
		jscrzusatz.validate();
		jscrzusatz.addKeyListener(this);
		pat32.add(jscrzusatz,BorderLayout.CENTER);
		pat32.validate();
		return pat32;
	}	
	
	private JXPanel getDatenPanel11(){
		JXPanel pat11 = new JXPanel(new BorderLayout());
		//but.setBorder(null);
		pat11.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		pat11.setOpaque(false);
		pat11.setDoubleBuffered(true);
	
		cbanrede = new JRtaComboBox(new String[] {"HERR","FRAU"});
		cbanrede.addKeyListener(this);
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
		
		jtf[27] = new JRtaTextField("DATUM", true);
		jtf[27].setName("akutbis");
		
		jtf[28] = new JRtaTextField("DATUM", true);
		jtf[28].setName("akutdat");

		jtf[29] = new JRtaTextField("GROSS", true);
		jtf[29].setName("termine1");
		
		jtf[30] = new JRtaTextField("GROSS", true);
		jtf[30].setName("termine2");
		
		jcheck[1] = new JRtaCheckBox();
		jcheck[1].setOpaque(false);
		jcheck[1].setName("akutpat");
		
		
		
									//      1.                2.    3.     4.                 5.    6.    
		FormLayout lay11 = new FormLayout("right:max(80dlu;p), 4dlu, 60dlu,right:max(60dlu;p), 4dlu, 60dlu",
			       //1.   2.   3.   4.  5.   6   7   8    9   10   11  12  13  14   15   16  17  18   19   20   21  22   23  24   25  26    27  28   29   30   31   32  33  34   35  36   37  38   39    40  41  42  43   44   45  46  47  48    49   50   51 52   53  54   55  56  57   58   59   60  61   62  63  64   65   66   67
					"p, 10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p");
					PanelBuilder builder = new PanelBuilder(lay11);
					builder.setDefaultDialogBorder();
					builder.getPanel().setOpaque(false);	
					CellConstraints cc11 = new CellConstraints();
					builder.getPanel().setDoubleBuffered(true);

		builder.addSeparator("Personendaten",cc11.xyw(1,1,6));
		builder.addLabel("Anrede", cc11.xy(1,3));
		builder.add(cbanrede, cc11.xy(3,3));
		//builder.add(jtf[0], cc11.xy(3,1));
		builder.addLabel("Titel", cc11.xy(4,3));
		builder.add(jtf[1], cc11.xy(6,3));
		builder.addLabel("Nachname *)", cc11.xy(1,5));
		builder.add(jtf[2], cc11.xyw(3, 5, 4));
		builder.addLabel("Vorname *)", cc11.xy(1,7));
		builder.add(jtf[3], cc11.xyw(3, 7, 4));
		builder.addLabel("Strasse, Nr. *)", cc11.xy(1,9));
		builder.add(jtf[4], cc11.xyw(3, 9, 4));
		builder.addLabel("PLZ, Ort *)", cc11.xy(1,11));
		builder.add(jtf[5], cc11.xy(3, 11));
		builder.add(jtf[6], cc11.xyw(4, 11, 3));
		builder.addLabel("Geburtstag *)", cc11.xy(1,13));
		builder.add(jtf[11], cc11.xy(3, 13));
		builder.addLabel("Telefon priv.", cc11.xy(1, 15));
		builder.add(jtf[7], cc11.xyw(3, 15, 4));
		builder.addLabel("Telefon gesch.", cc11.xy(1, 17));
		builder.add(jtf[8], cc11.xyw(3, 17, 4));
		builder.addLabel("Mobil", cc11.xy(1, 19));
		builder.add(jtf[9], cc11.xyw(3, 19, 4));
		builder.addLabel("Email", cc11.xy(1, 21));
		builder.add(jtf[10], cc11.xyw(3, 21, 4));
		
		

		builder.addSeparator("Plandaten", cc11.xyw(1, 23, 6));

		builder.addLabel("Akutpatient", cc11.xy(1,25));
		builder.add(jcheck[1], cc11.xy(3,25));
		
		builder.addLabel("akut von", cc11.xy(1,27));
		builder.add(jtf[28], cc11.xy(3, 27));
		builder.addLabel("akut bis", cc11.xy(4,27));
		builder.add(jtf[27], cc11.xy(6, 27));
		
		builder.addLabel("mögliche Termine 1", cc11.xy(1, 29));
		builder.add(jtf[29], cc11.xyw(3, 29, 4));
		builder.addLabel("mögliche Termine 2", cc11.xy(1, 31));
		builder.add(jtf[30], cc11.xyw(3, 31, 4));
	

		
		builder.getPanel().addKeyListener(this);
		builder.getPanel().addFocusListener(this);
		builder.getPanel().validate();
		JScrollPane xjscr = JCompTools.getTransparentScrollPane(builder.getPanel());
		//jscr = new JScrollPane();
		//jscr.setOpaque(false);
		//jscr.getViewport().setOpaque(false);
		//jscr.setBorder(null);
		//jscr.setViewportBorder(null);
		xjscr.getVerticalScrollBar().setUnitIncrement(15);
		//jscr.setViewportView(builder.getPanel());

		xjscr.validate();
		xjscr.addKeyListener(this);

		pat11.add(xjscr,BorderLayout.CENTER);
		pat11.validate();
		return pat11;
	}
	
	public static String getArztBisher(){
		return arztbisher;
	}
	public static String getKasseBisher(){
		return kassebisher;
	}
	private void doArztListe(){
		if(this.inNeu){
			return;
		}
		//System.out.println("in doArztListe");
		String aerzte = Reha.thisClass.patpanel.patDaten.get(63);
		String[] einzelarzt = null;
		String[] arztdaten = null;
		Vector<?> arztvec = null;
		if(!aerzte.trim().equals("")){
			einzelarzt = aerzte.split("\n");
			//System.out.println("Anzahl Ärzte = "+einzelarzt.length);
			for(int i = 0; i < einzelarzt.length;i++){
				arztdaten = einzelarzt[i].split("@");
				//docmod.setColumnIdentifiers(new String[] {"LANR","Nachname","Strasse","Ort","BSNR",""});
				arztvec = SqlInfo.holeFelder("select arztnum,nachname,strasse,ort,bsnr,id  from arzt where id='"+arztdaten[1]+"' LIMIT 1" );
				if(arztvec.size() > 0){
					docmod.addRow((Vector<?>)arztvec.get(0));
				}
			}
			if(docmod.getRowCount()>0){
				doclist.setRowSelectionInterval(0, 0);
			}
		}
	}
	public void enableReaderButton(){
		if(SystemConfig.sReaderAktiv.equals("0")){
			return;
		}
		knopf3.setEnabled(true);
		einlesen();
	}
	public void disableReaderButton(){
		if(SystemConfig.sReaderAktiv.equals("0")){
			return;
		}
		knopf3.setEnabled(false);
	}

	private void einlesen(){

		if(SystemConfig.sReaderAktiv.equals("0")){
			return;
		}
		if(!Reha.thisClass.ocKVK.isCardReady){
			JOptionPane.showMessageDialog(null,"Chipkarten-Lesegerät ist nicht bereit");
			return;
		}
		if(SystemConfig.hmKVKDaten.isEmpty()){
			JOptionPane.showMessageDialog(null,"Daten der Chipkarte konnten nicht gelesen werden");
			return;
		}
		////System.out.println("Aufruf der KVK");
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
			String fehlertext = "Fehler beim einlesen der Versichertenkarte.\nBitte erneut einlesen\n\n";
						//"Fehler-Code: "+SystemConfig.hmKVKDaten.get("Fehlercode")+"\n"+
						//"Fehler-Text: "+SystemConfig.hmKVKDaten.get("Fehlertext")+"\n";
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
			// Lemmi 20110103: Verhinderung von Datenverlust bei unbeabsichtigtem Zumachen des geänderten Patientent-Dialoges
//			if ( HasChanged() && askForCancelUsaved() == 1 )
	//			return;
			finalise();
			((JXDialog)this.getParent().getParent().getParent().getParent().getParent()).dispose();
		}else if(com.equals("adddoc")){
			arztInListeAuswahl();
		}else if(com.equals("deldoc")){
			int row = -1;
			if( (row = doclist.getSelectedRow()) < 0){
				return;
			}else{
				TableTool.loescheRow(doclist, row);
			}
			
		}else if(com.equals("u18ignore")){
			
		}else if(com.equals("vorjahrfrei")){
			//System.out.println("in checkBox Vorjahrfrei");
			if(jcheck[10].isSelected()){
				jtf[35].setText("");
		    }else{
		    	jtf[35].setText(SystemConfig.vorJahr);
		    }
		}else if(com.equals("delpic")){
			if( lblbild.getIcon()!=null && !inNeu){
				int frage = JOptionPane.showConfirmDialog(null,"Wollen Sie das Patientenfoto wirklich löschen?","Wichtige Benutzeranfrage",JOptionPane.YES_NO_OPTION);
				if(frage==JOptionPane.YES_OPTION){
					SqlInfo.sqlAusfuehren("delete from patbild where pat_intern = '"+
							Reha.thisClass.patpanel.aktPatID+"' LIMIT 1");
				}else{
					return;
				}
			}
			lblbild.setIcon(null);
			lblbild.setText("Kein Bild des Patienten vorhanden");
			startMitBild = false;
			updateBild = false;
			return;
		}else if(com.equals("addpic")){
			if(SystemConfig.sWebCamActive.equals("0")){
				JOptionPane.showMessageDialog(null,"WebCam entweder nicht aktiviert (System-Initialisierung)\noder nicht angeschlossen!");
				return;
			}
			PatientenFoto foto = new PatientenFoto(null,"patBild",this);
			foto.setModal(true);
			foto.setLocationRelativeTo(null);
			foto.pack();
			foto.setVisible(true);
			foto = null;
			return;
			
		}else if(com.equals("addjpg")){
			JOptionPane.showMessageDialog(null,"Aufnahme von fertigen Bilddateien (noch) nicht implementiert");
		}
			

	}
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		////System.out.println("Neuanlage Pressed "+arg0.getKeyCode());
		if(arg0.getKeyCode()== 10 || arg0.getKeyCode()==0){
			arg0.consume();
			try{
				if(((JComponent)arg0.getSource()).getName().equals("einlesen")){
					einlesen();
					return;
				}
				if(((JComponent)arg0.getSource()).getName().equals("speichern")){
					schreibeInDb();
					return;
				}
				if(((JComponent)arg0.getSource()).getName().equals("abbrechen")){
					
					// Lemmi 20110103: Verhinderung von Datenverlust bei unbeabsichtigtem Zumachen des geänderten Patientent-Dialoges
//					if ( HasChanged() && askForCancelUsaved() == 1 )
//						return;
					
					((JXDialog)this.getParent().getParent().getParent().getParent().getParent()).setVisible(false);
					finalise();
					((JXDialog)this.getParent().getParent().getParent().getParent().getParent()).dispose();
					return;
				}
			}catch(Exception ex){
				
			}
			
		}
		if(arg0.getKeyCode()==27){
			
			// Lemmi 20110103: Verhinderung von Datenverlust bei unbeabsichtigtem Zumachen des geänderten Patientent-Dialoges
//			if ( HasChanged() && askForCancelUsaved() == 1 )
//				return;

			((JXDialog)this.getParent().getParent().getParent().getParent().getParent()).setVisible(false);
			finalise();
			((JXDialog)this.getParent().getParent().getParent().getParent().getParent()).dispose();
		}
		
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		////System.out.println("Neuanlage Released "+arg0);		
		if(arg0.getKeyCode()== 10 || arg0.getKeyCode()==0)
			arg0.consume();
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		////System.out.println("Neuanlage Typed "+arg0);		
		if(arg0.getKeyCode()== 10 || arg0.getKeyCode()==0)
			arg0.consume();
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		/*
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

		*/
		if( ((JComponent)arg0.getSource()).getName() != null){
			if(((JComponent)arg0.getSource()).getName().equals("arzt")){
				if(testObDialog(jtf[17].getText())){
					//System.out.println("Arzt-Dialog erforderlich");
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
					//System.out.println("Kassen-Dialog erforderlich");
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
			//System.out.println(((JComponent)arg0.getSource()).getName());
		}

		// TODO Auto-generated method stub
	}
	private void arztAuswahl(String[] suchenach){
		jtf[19].requestFocus();
		ArztAuswahl awahl = new ArztAuswahl(null,"ArztAuswahl",suchenach,new JRtaTextField[] {jtf[17],jtf[18],jtf[33]},jtf[17].getText().trim());
		awahl.setModal(true);
		awahl.setLocationRelativeTo(this);
		awahl.setVisible(true);
		awahl.dispose();
		awahl = null;
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
				//System.out.println("Beginne ArztOrganisation mit Arzt ID ="+jtf[33].getText());
				aerzteOrganisieren(jtf[33].getText(),inNeu,docmod,doclist,true);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}
			
		}.execute();
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run(){
		 			jtf[19].requestFocus();
		 	   }
		});
		if(jtf[17].getText().indexOf("?") >= 0){
			String text = jtf[17].getText().replace("?","");
			jtf[17].setText(text);
		}


	}

	private void kassenAuswahl(String[] suchenach){
		jtf[14].requestFocus();
		KassenAuswahl kwahl = new KassenAuswahl(null,"KassenAuswahl",suchenach,new JRtaTextField[] {jtf[12],jtf[13],jtf[34]},jtf[12].getText().trim());
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
		if(jtf[12].getText().indexOf("?") >= 0){
			String text = jtf[12].getText().replace("?","");
			jtf[12].setText(text);
		}
	}
	
	private void arztInListeAuswahl(){
		JRtaTextField[] tfaliste = {new JRtaTextField("nix",false),new JRtaTextField("nix",false),new JRtaTextField("nix",false)};
		ArztAuswahl awahl = new ArztAuswahl(null,"ArztAuswahl",new String[] {"",""} ,tfaliste,"");
		awahl.setModal(true);
		awahl.setLocationRelativeTo(this);
		awahl.setVisible(true);
		awahl.dispose();
		awahl = null;
		final JRtaTextField xtf = tfaliste[2];
		if(!xtf.getText().equals("")){
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					try{
					//System.out.println("Beginne ArztOrganisation mit Arzt ID ="+xtf.getText());
					aerzteOrganisieren(xtf.getText(),inNeu,docmod,doclist,false);
					}catch(Exception ex){
						ex.printStackTrace();
					}
					return null;
				}
				
			}.execute();
		}
	}
	private void arztInListeDoppelKlick(Point klick){
		int row = doclist.getSelectedRow();
		if(row >= 0){
			formulareAuswerten(klick);
			//ProgLoader.ArztFenster(0,(String)doclist.getValueAt(row,5) );			
		}

	}
	public void holeFormulare(){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/arzt.ini");
					int forms = inif.getIntegerProperty("Formulare", "ArztFormulareAnzahl");
					for(int i = 1; i <= forms; i++){
						titel.add(inif.getStringProperty("Formulare","AFormularText"+i));			
						formular.add(inif.getStringProperty("Formulare","AFormularName"+i));
					}
				}catch(Exception ex){
						ex.printStackTrace();
				}
				return null;
			}
			
		}.execute();
		
	}
	
	public void formulareAuswerten(Point klick){
		int row = doclist.getSelectedRow();
		if(row >= 0){
			String sid = Integer.toString(Integer.parseInt((String) doclist.getValueAt(row, 5)));
    		iformular = -1;
    		KassenFormulare kf = new KassenFormulare(null,titel,formularid);
    		Point pt = klick;
    		kf.setLocation(pt.x-100,pt.y+25);
    		kf.setModal(true);
    		kf.setVisible(true);
    		iformular = Integer.valueOf(formularid.getText());
    		kf = null;
    		final String xid = sid;
    		if(iformular >= 0){
    			new SwingWorker<Void,Void>(){

					@Override
					protected Void doInBackground() throws Exception {
						ArztTools.constructArztHMap(xid);
						OOTools.starteStandardFormular(Reha.proghome+"vorlagen/"+Reha.aktIK+"/"+formular.get(iformular),null);
						return null;
					}
    			}.execute();
    			
    		}
 
    		//System.out.println("Es wurde Formular "+iformular+" gew�hlt");
        	
		}else{
			String mes = "Wenn man eine Kasse anschreiben möchte, empfiehlt es sich\n"+ 
			"vorher die Kasse auszuwählen die man anschreiben möchte!!!\n\n"+
			"Aber trösten Sie sich, unser Herrgott hat ein Herz für eine ganz spezielle Randgruppe.\n"+
			"Sie dürfen also hoffen....\n\n";
			JOptionPane.showMessageDialog(null, mes);
			iformular = -1;
		}
	}
	
	public void aerzteOrganisieren(String aid,boolean neu,MyDocTableModel mod,JXTable tbl,boolean bloednachfragen){
		//hier weitermachen
		// wenn neu dann Tabelle mit nur einem arzt
		// *******************/
		// wenn nicht neu und nicht in bisherigem aerzt.feld
		// nachfragen ob neu aufgenommen werden soll
		// wenn ja aufnehmen und in dbAbspeichern
		// wenn nicht zur�ck
		if(mod != null){
			if(neu){
				if(! inTableEnthalten(aid,mod)){
					//System.out.println("Neuanlage Pat. Arzt wird in Liste übernommen");
					mod.setRowCount(0);
					arztInTableAufnehmen(aid,mod);
					tbl.validate();
				}else{
					//System.out.println("Neuanlage Pat. Arzt bereits in der Liste enthalten");
				}
			}else{ // in Patient �ndern
				if(! inTableEnthalten(aid,mod)){
					//System.out.println("Ändern Pat. Arzt wird in Liste übernommen");
					if(bloednachfragen){
						int frage = JOptionPane.showConfirmDialog(null,"Den gewählten Arzt in die Arztliste dieses Patienten aufnehmen?","Wichtige Benutzeranfrage",JOptionPane.YES_NO_OPTION);
						if(frage == JOptionPane.YES_OPTION){
							arztInTableAufnehmen(aid,mod);
							tbl.validate();
						}
					}else{
						arztInTableAufnehmen(aid,mod);
						tbl.validate();
					}
				}else{
					//System.out.println("Ändern Pat. Arzt bereits in der Liste enthalten");
				}
			}
		}else{ // funktion wurde �ber Rezept aufgerufen
			
		}
	}
	private void arztInTableAufnehmen(String aid,MyDocTableModel mod ){
		Vector<Vector<String>> vecx;
		if(mod != null){
			vecx = SqlInfo.holeFelder("select arztnum,nachname,strasse,ort,bsnr,id  from arzt where id='"+aid+"' LIMIT 1" );
			if(vecx.size() > 0){
				mod.addRow((Vector<String>)vecx.get(0));
			}
		}else{
			
		}
	}
	private boolean inTableEnthalten(String aid,MyDocTableModel mod){
		boolean bret = false; 
		for(int i = 0; i<mod.getRowCount();i++){
			if( ((String)mod.getValueAt(i, 5)).equals(aid)){
				bret = true;
				return bret;
			}
		}
		return bret;
	}
	
	private boolean testObDialog(String string){
		if(string==null){
			return false;
		}
		if(string.trim().length() == 0){
			return false;
		}
		if(string.substring(0,1).
				equals("?")){
			return true;
		}
		return false;
	}

	private void speichernPatBild(boolean neu,ImageIcon ico,String pat_intern){
		Statement stmt = null;;
		ResultSet rs = null;
		PreparedStatement ps = null;
		//boolean ret = false;
		//int bilder = 0;
	
		//piTool.app.conn.setAutoCommit(true);
		try {
			//stmt = (Statement) Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
            //        ResultSet.CONCUR_UPDATABLE );
			if(neu){
				String select = "Insert into patbild set bild = ? , pat_intern = ?, vorschau = ?";
				  ps = (PreparedStatement) Reha.thisClass.conn.prepareStatement(select);
				  ps.setBytes(1, bufferedImageToByteArray( (BufferedImage) ico.getImage()));
				  ps.setString(2, pat_intern);
				  BufferedImage buf = new BufferedImage(35,40,BufferedImage.TYPE_INT_RGB);
				  Graphics2D g = buf.createGraphics();
				    g.drawImage(ico.getImage().getScaledInstance(35, 44, Image.SCALE_SMOOTH), null, null);
					g.dispose();
				  ps.setBytes(3, bufferedImageToByteArray( (BufferedImage) buf));
				  ps.execute();
				  buf = null;
			}else{
				String select = "Update patbild set bild = ? , vorschau = ?  where pat_intern = ?";
				  ps = (PreparedStatement) Reha.thisClass.conn.prepareStatement(select);
				  ps.setBytes(1, bufferedImageToByteArray( (BufferedImage) ico.getImage()));
				  BufferedImage buf = new BufferedImage(35,44,BufferedImage.TYPE_INT_RGB);
				  Graphics2D g = buf.createGraphics();
				    g.drawImage(ico.getImage().getScaledInstance(35, 44, Image.SCALE_SMOOTH), null, null);
					g.dispose();
				  ps.setBytes(2, bufferedImageToByteArray( (BufferedImage) buf));
				  ps.setString(3, pat_intern);
				  ps.execute();
				  buf = null;
			}
			  
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ImageFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
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
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException sqlEx) { // ignore }
					ps = null;
				}
			}
		}
	}
	private static byte[] bufferedImageToByteArray(BufferedImage img) throws ImageFormatException, IOException{
		if(img != null){
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
		encoder.encode(img);
		return os.toByteArray();
		}else{
			return null;
		}
	}
	public static BufferedImage holePatBild(String pat_intern){
		Statement stmt = null;;
		ResultSet rs = null;
		//int bilder = 0;
		Image bild = null;
		try {
			stmt = (Statement) Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE );
				String test = "select bild from patbild where pat_intern ='"+pat_intern+"'";
				rs = (ResultSet) stmt.executeQuery(test);
				while(rs.next()){
					bild = ImageIO.read( new ByteArrayInputStream(rs.getBytes("bild")) );
				}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
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
		return (BufferedImage) bild;	
	}



	class MyDocTableModel extends DefaultTableModel{
		   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Class<?> getColumnClass(int columnIndex) {
			   if(columnIndex==1){
				   return String.class;}
			   else{
				   return String.class;
			   }
	       }
		   public boolean isCellEditable(int row, int col) {
		          return false;
	      }
	}


	public void aufraeumen(){
		for(int i = 0; i < jtf.length;i++ ){
			jtf[i].listenerLoeschen();
			ListenerTools.removeListeners(jtf[i]);	
		}
		for(int i = 0; i < jcheck.length;i++ ){
			ListenerTools.removeListeners(jcheck[i]);
		}
		xfelder.clear();
		checks.clear();
		xfelder = null;
		checks = null;
		rtp.removeRehaTPEventListener((RehaTPEventListener) this);
		rtp = null;
	}
	@Override
	public void rehaTPEventOccurred(RehaTPEvent evt) {
		try{
			if(evt.getDetails()[0] != null){
				if(evt.getDetails()[0].equals(this.getName())){
					((JXDialog)this.getParent().getParent().getParent().getParent().getParent()).setVisible(false);
					finalise();
					((JXDialog)this.getParent().getParent().getParent().getParent().getParent()).dispose();
					////System.out.println("****************Patient Neu/ändern -> Listener entfernt**************");				
				}
			}
		}catch(NullPointerException ne){
			//System.out.println("In PatNeuanlage" +evt);
		}
		
	}
	
/*  Lemmi ToDo: Den intransparente Handhabung mit jtf[0] und Anrede prüfen !!!
	// Lemmi 20110103: Merken der Originalwerte der eingelesenen Textfelder
	// ACHTUNG: Die Reihenfolge der Abfragen muß in SaveChangeStatus() und HasChanged() exakt identisch sein ! 
	private void SaveChangeStatus(){
		int i;
		originale.clear();  // vorherige Merkung wegwerfen

		// Alle Text-Eingabefelder
		for ( i = 0; i < jtf.length; i++ ) {
			//String strText = jtf[i].getText();
			originale.add( jtf[i].getText() );
		}
		
		// Einzelne Text-Felder
//		originale.add( jta.getText() );
		
		// alle ComboBoxen
		originale.add( (Integer)cbanrede.getSelectedIndex() );  

		// alle CheckBoxen
		for ( i = 0; i < jcheck.length; i++ ) {  
			originale.add( (Boolean)(jcheck[i].isSelected() ) );  // 
		}
	}
	
	// Lemmi 20110103: prüft, ob sich Einträge geändert haben
	// ACHTUNG: Die Reihenfolge der Abfragen muß in SaveChangeStatus() und HasChanged() exakt identisch sein ! 
	public Boolean HasChanged()  {
		int i, idx = 0;
		
		// Alle Text-Eingabefelder
		for ( i = 0; i < jtf.length; i++) {
			String strTex1 = ">" + jtf[i].getText() + "<    >" + originale.get(idx++) + "<";
			if(! jtf[i].getText().equals( originale.get(idx++) ) )
				return true;
		}
		
		// Einzelne Textfelder
//		if(! jta.getText().equals( originale.get(idx++) ) )	   
//			return true;
		
		// alle ComboBoxen
		if( cbanrede.getSelectedIndex() != (Integer)originale.get(idx++) )	// Art d. Verordn. etc.
			return true;
		
		// alle CheckBoxen
		for ( i = 0; i < jcheck.length; i++) {		// CheckBoxen
			if( jcheck[i].isSelected() != (Boolean)originale.get(idx++) )	// Begründung außer der Regel vorhanden ? .....
				return true;
		}		

		return false;
	}

	// Lemmi 20110103: Stndard-Abfrage nach Prüfung, ob sich Einträge geändert haben
	// fragt nach, ob wirklich ungesichert abgebrochen werden soll !
	public int askForCancelUsaved(){
		String[] strOptions = {"ja", "nein"};  // Defaultwert euf "nein" gesetzt !
		return JOptionPane.showOptionDialog(null, "Es wurden Patienten-Anngaben geändert!\nWollen sie die Änderung(en) wirklich verwerfen?",
				 "Angaben wurden geändert", 
				 JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				 strOptions, strOptions[1] );
	 }

}
*/
}
class ArztListeSpeichern{
	public ArztListeSpeichern(Vector<Vector<String>> vec,boolean neu,String xpatintern){
		if(vec.size() <= 0){
			return;
		}
		String cmd = "update pat5 set aerzte = '";
		String aliste = "";
		for(int i = 0;i < vec.size();i++){
			aliste = aliste+"@"+((String)((Vector<String>)vec.get(i)).get(5))+"@\n";
		}
		SqlInfo.aktualisiereSaetze("pat5", "aerzte='"+aliste+"'", "pat_intern='"+xpatintern+"'");
		new ExUndHop().setzeStatement(cmd);
		Reha.thisClass.patpanel.patDaten.set(63,aliste);
		//System.out.println(cmd);
	}
}	
