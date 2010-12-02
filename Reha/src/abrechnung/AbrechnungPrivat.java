package abrechnung;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jxTableTools.TableTool;
import oOorgTools.OOTools;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;

import sqlTools.SqlInfo;
import stammDatenTools.RezTools;
import systemEinstellungen.SystemConfig;
import systemEinstellungen.SystemPreislisten;
import systemTools.JRtaComboBox;
import systemTools.JRtaRadioButton;
import systemTools.StringTools;
import terminKalender.DatFunk;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.ITextTable;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.internal.printing.PrintProperties;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dialoge.DragWin;
import dialoge.PinPanel;
import events.PatStammEvent;
import events.PatStammEventClass;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;

public class AbrechnungPrivat extends JXDialog implements FocusListener, ActionListener, MouseListener, KeyListener,RehaTPEventListener, ChangeListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1036517682792665034L;

	private JXTitledPanel jtp = null;
	private MouseAdapter mymouse = null;
	private PinPanel pinPanel = null;
	private JXPanel content = null;
	private RehaTPEventClass rtp = null;
	public int rueckgabe;
	private int preisgruppe;
	private JRtaComboBox jcmb = null;
	private JRtaRadioButton[] jrb = {null,null};
	private JLabel[] labs = {null,null,null,null,null,null,null};
	private JLabel adr1 = null;
	private JLabel adr2 = null;
	//private JRtaTextField[] tfs = {null,null,null,null,null};
	private JButton[] but = {null,null,null};
	//private HashMap<String,String> hmRezgeb = null;
	DecimalFormat dcf = new DecimalFormat ( "#########0.00" );
	ButtonGroup bg = new ButtonGroup();
	String rgnrNummer;
	String[] diszis = {"KG","MA","ER","LO"};
	String disziplin = "";
	int aktGruppe = 0;

	private Vector<Vector<String>> preisliste = null;
	
	boolean preisok = false;
	boolean hausBesuch = false;
	boolean hbEinzeln = false;
	boolean hbPauschale = false;
	boolean hbmitkm = false;

	int kmBeiHB = 0;
	
	Vector<String> originalPos = new Vector<String>();
	Vector<Integer> originalAnzahl = new Vector<Integer>();
	Vector<Double> einzelPreis = new Vector<Double>();
	Vector<String> originalId = new Vector<String>();
	Vector<String> originalLangtext = new Vector<String>();
	
	Vector<BigDecimal> zeilenGesamt = new Vector<BigDecimal>();
	BigDecimal rechnungGesamt = BigDecimal.valueOf(Double.parseDouble("0.00"));
 
	
	HashMap<String,String> hmAdresse = new HashMap<String,String>();
	String aktRechnung = "";
	ITextTable textTable = null;
	ITextTable textEndbetrag = null;
	ITextDocument textDocument = null;
	int aktuellePosition = 0;
	int patKilometer = 0;	
	//AbrechnungDlg abrDlg = null;
	
	StringBuffer writeBuf = new StringBuffer();
	StringBuffer rechnungBuf = new StringBuffer();
	
	public AbrechnungPrivat(JXFrame owner,String titel,int rueckgabe,int preisgruppe) {
		super(owner, (JComponent)Reha.thisFrame.getGlassPane());
		final int ipg = preisgruppe-1;
		/*
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				//System.out.println("Preisgruppe = "+ipg);
				disziplin = RezTools.putRezNrGetDisziplin(Reha.thisClass.patpanel.vecaktrez.get(1));
				preisliste = SystemPreislisten.hmPreise.get(disziplin).get(ipg);
				preisok = true;
				return null;
			}
		}.execute();
		*/
		disziplin = RezTools.putRezNrGetDisziplin(Reha.thisClass.patpanel.vecaktrez.get(1));
		preisliste = SystemPreislisten.hmPreise.get(disziplin).get(ipg);
		preisok = true;

		this.rueckgabe = rueckgabe;
		this.preisgruppe = preisgruppe;
		this.setUndecorated(true);
		this.setName("Privatrechnung");
		this.jtp = new JXTitledPanel();
		this.jtp.setName("Privatrechnung");
		this.mymouse = new DragWin(this);
		this.jtp.addMouseListener(mymouse);
		this.jtp.addMouseMotionListener(mymouse);
		this.jtp.setContentContainer(getContent());
		this.jtp.setTitleForeground(Color.WHITE);
		this.jtp.setTitle(titel);
		this.pinPanel = new PinPanel();
		this.pinPanel.getGruen().setVisible(false);
		this.pinPanel.setName("Privatrechnung");
		this.jtp.setRightDecoration(this.pinPanel);
		this.setContentPane(jtp);
		//this.setModal(true);
		this.setResizable(false);
		this.rtp = new RehaTPEventClass();
		this.rtp.addRehaTPEventListener((RehaTPEventListener) this);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
	private JXPanel getContent(){
		content = new JXPanel(new BorderLayout());
		content.add(getFields(),BorderLayout.CENTER);
		content.add(getButtons(),BorderLayout.SOUTH);
		content.addKeyListener(this);
		return content;
	}
	private JXPanel getFields(){
		JXPanel pan = new JXPanel();
		//                                1           2             3             4     5     6                7
		FormLayout lay = new FormLayout("20dlu,fill:0:grow(0.5),p,fill:0:grow(0.5),20dlu",
				//1    2   3  4  5   6   7  8   9  10  11  12 13 14  15 16  17  18  19 20  21  22 23  24 25  26
				"20dlu,p,2dlu,p,10dlu,p,2dlu,p,10dlu,p,3dlu,p,5dlu, p,1dlu,p,1dlu,p,1dlu,p ,1dlu,p,1dlu,p,1dlu,p, fill:0:grow(0.5),5dlu");
		pan.setLayout(lay);
		CellConstraints cc = new CellConstraints();
		pan.setOpaque(false);
		JLabel lab = new JLabel("Abrechnung Rezeptnummer: "+Reha.thisClass.patpanel.vecaktrez.get(1));
		lab.setForeground(Color.BLUE);
		pan.add(lab ,cc.xy(3, 1,CellConstraints.DEFAULT,CellConstraints.CENTER));
		adr1  = new JLabel(" ");
		adr1.setForeground(Color.BLUE);
		pan.add(adr1 ,cc.xy(3, 2));
		adr2  = new JLabel(" ");
		adr2.setForeground(Color.BLUE);
		pan.add(adr2 ,cc.xy(3, 4));
		
		lab = new JLabel("Preisgruppe wählen:");
		pan.add(lab,cc.xy(3,6));
		//jcmb = new JRtaComboBox(SystemConfig.vPreisGruppen);


		jcmb = new JRtaComboBox(SystemPreislisten.hmPreisGruppen.get(StringTools.getDisziplin(Reha.thisClass.patpanel.vecaktrez.get(1))));
		jcmb.setSelectedIndex(this.preisgruppe-1);
		this.aktGruppe = this.preisgruppe-1;
		jcmb.setActionCommand("neuertarif");
		jcmb.addActionListener(this);
		pan.add(jcmb,cc.xy(3,8));
		jrb[0] = new JRtaRadioButton("Formular für Privatrechnung verwenden");
		jrb[0].addChangeListener(this);
		pan.add(jrb[0],cc.xy(3,10));
		jrb[1] = new JRtaRadioButton("Formular für Kostenträger Rechnung verwenden");
		jrb[1].addChangeListener(this);
		pan.add(jrb[1],cc.xy(3,12));
		bg.add(jrb[0]);
		bg.add(jrb[1]);
		if(preisgruppe==4){
			jrb[1].setSelected(true);
			regleBGE();
		}else{
			jrb[0].setSelected(true);
			reglePrivat();
		}

		if(!Reha.thisClass.patpanel.vecaktrez.get(8).equals("0")){
			labs[0] = new JLabel();
			/*
			labs[0] = new JLabel(Reha.thisClass.patpanel.vecaktrez.get(3)+" * "+
					RezTools.getKurzformFromID(Reha.thisClass.patpanel.vecaktrez.get(8), preisliste));
			*/		
			labs[0].setForeground(Color.BLUE);
			pan.add(labs[0],cc.xy(3, 14));

		}
		if(!Reha.thisClass.patpanel.vecaktrez.get(9).equals("0")){
			labs[1] = new JLabel();
			labs[1].setForeground(Color.BLUE);
			pan.add(labs[1],cc.xy(3, 16));
		}
		if(!Reha.thisClass.patpanel.vecaktrez.get(10).equals("0")){
			labs[2] = new JLabel();
			labs[2].setForeground(Color.BLUE);			
			pan.add(labs[2],cc.xy(3, 18));
		}
		if(!Reha.thisClass.patpanel.vecaktrez.get(11).equals("0")){
			labs[3] = new JLabel();
			labs[3].setForeground(Color.BLUE);			
			pan.add(labs[3],cc.xy(3, 20));
		}
		// Mit Hausbesuch
		if(Reha.thisClass.patpanel.vecaktrez.get(43).equals("T")){
			//Hausbesuch voll (Einzeln) abrechnen 
			hausBesuch = true;
			if(Reha.thisClass.patpanel.vecaktrez.get(61).equals("T")){
				hbEinzeln = true;
			}
			labs[4] = new JLabel();
			labs[4].setForeground(Color.RED);
			pan.add(labs[4],cc.xy(3, 22));
			labs[5] = new JLabel();
			labs[5].setForeground(Color.RED);
			pan.add(labs[5],cc.xy(3, 24));
		}
		labs[6] = new JLabel();
		labs[6].setForeground(Color.BLUE);
		pan.add(labs[6],cc.xy(3, 26));

		doNeuerTarif();
		pan.validate();
		return pan;
	}
	private JXPanel getButtons(){
		JXPanel pan = new JXPanel();
		pan.setOpaque(false);//           1        2             3    4     5     6     7
		FormLayout lay = new FormLayout("5dlu,fill:0:grow(0.5),50dlu,10dlu,50dlu,10dlu,50dlu,fill:0:grow(0.5),5dlu",
				//1          2         3   4  5  6   7  8   9  10  11  12
				"5dlu,fill:0:grow(0.5),p,fill:0:grow(0.5),5dlu");
		pan.setLayout(lay);
		CellConstraints cc = new CellConstraints();
		pan.add((but[0] = macheBut("Ok","ok")),cc.xy(3,3));
		but[0].addKeyListener(this);

		pan.add((but[1] = macheBut("Korrektur","korrektur")),cc.xy(5,3));
		but[1].addKeyListener(this);
		
		pan.add((but[2] = macheBut("abbrechen","abbrechen")),cc.xy(7,3));
		but[2].addKeyListener(this);
		return pan;
	}
	private JButton macheBut(String titel,String cmd){
		JButton but = new JButton(titel);
		but.setName(cmd);
		but.setActionCommand(cmd);
		but.addActionListener(this);
		return but;
	}	
	private void doRgRechnungPrepare(){
		//boolean privat = true;
		if(jrb[0].isSelected()){
			doPrivat();
		}else{
			doBGE();
		}
		posteAktualisierung((String) Reha.thisClass.patpanel.patDaten.get(29) );
		FensterSchliessen("dieses");
	}
	/*
	private AbrechnungPrivat getInstance(){
		return this;
	}
	*/
	
	private void posteAktualisierung(String patid){
		final String xpatid = patid;
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				String s1 = String.valueOf("#PATSUCHEN");
				String s2 = xpatid;
				PatStammEvent pEvt = new PatStammEvent(this);
				pEvt.setPatStammEvent("PatSuchen");
				pEvt.setDetails(s1,s2,"") ;
				PatStammEventClass.firePatStammEvent(pEvt);		
				return null;
			}
			
		}.execute();
	}
	private void holePrivat(){
		hmAdresse.put("<pri1>",SystemConfig.hmAdrPDaten.get("<Panrede>") );
		hmAdresse.put("<pri2>",SystemConfig.hmAdrPDaten.get("<Padr1>") );
		hmAdresse.put("<pri3>",SystemConfig.hmAdrPDaten.get("<Padr2>") );
		hmAdresse.put("<pri4>",SystemConfig.hmAdrPDaten.get("<Padr3>") );
		hmAdresse.put("<pri5>",SystemConfig.hmAdrPDaten.get("<Pbanrede>") );
		return;
	}
	private void holeBGE(){
		hmAdresse.put("<pri1>",SystemConfig.hmAdrKDaten.get("<Kadr1>") );
		hmAdresse.put("<pri2>",SystemConfig.hmAdrKDaten.get("<Kadr2>") );
		hmAdresse.put("<pri3>",SystemConfig.hmAdrKDaten.get("<Kadr3>") );
		hmAdresse.put("<pri4>",SystemConfig.hmAdrKDaten.get("<Kadr4>") );
		hmAdresse.put("<pri5>","Sehr geehrte Damen und Herren" );
		return;
	}
	private void doPrivat(){
		try {
			Thread.sleep(50);
			hmAdresse.put("<pri1>",SystemConfig.hmAdrPDaten.get("<Panrede>") );
			hmAdresse.put("<pri2>",SystemConfig.hmAdrPDaten.get("<Padr1>") );
			hmAdresse.put("<pri3>",SystemConfig.hmAdrPDaten.get("<Padr2>") );
			hmAdresse.put("<pri4>",SystemConfig.hmAdrPDaten.get("<Padr3>") );
			hmAdresse.put("<pri5>",SystemConfig.hmAdrPDaten.get("<Pbanrede>") );
			
			if((!hmAdresse.get("<pri2>").contains(StringTools.EGross(StringTools.EscapedDouble(Reha.thisClass.patpanel.patDaten.get(2))))) ||
					(!hmAdresse.get("<pri2>").contains(StringTools.EGross(StringTools.EscapedDouble(Reha.thisClass.patpanel.patDaten.get(3)))))	){
				String meldung = "Fehler!!!! aktuelle Patientendaten - soll = "+StringTools.EGross(StringTools.EscapedDouble(Reha.thisClass.patpanel.patDaten.get(3)))+" "+
				StringTools.EGross(StringTools.EscapedDouble(Reha.thisClass.patpanel.patDaten.get(2)))+"\n"+
				"Istdaten sind\n"+
				hmAdresse.get("<pri1>")+"\n"+
				hmAdresse.get("<pri2>")+"\n"+
				hmAdresse.get("<pri3>")+"\n"+
				hmAdresse.get("<pri4>")+"\n"+
				hmAdresse.get("<pri5>");
				JOptionPane.showMessageDialog(null,meldung);
				return;
			}
			aktRechnung = Integer.toString(SqlInfo.erzeugeNummer("rnr"));
			hmAdresse.put("<pri6>",aktRechnung);

			starteDokument(Reha.proghome+"vorlagen/"+Reha.aktIK+"/"+SystemConfig.hmAbrechnung.get("hmpriformular"));
			starteErsetzen();
			startePositionen();

			starteDrucken();

			if(Reha.vollbetrieb){			
				doFaktura("privat");

				doOffenePosten("privat");

				doUebertrag();
			}
			doTabelle();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void doBGE(){
		try {
			Thread.sleep(50);
			hmAdresse.put("<pri1>",SystemConfig.hmAdrKDaten.get("<Kadr1>") );
			hmAdresse.put("<pri2>",SystemConfig.hmAdrKDaten.get("<Kadr2>") );
			hmAdresse.put("<pri3>",SystemConfig.hmAdrKDaten.get("<Kadr3>") );
			hmAdresse.put("<pri4>",SystemConfig.hmAdrKDaten.get("<Kadr4>") );
			hmAdresse.put("<pri5>","Sehr geehrte Damen und Herren" );
			aktRechnung = Integer.toString(SqlInfo.erzeugeNummer("rnr"));
			hmAdresse.put("<pri6>",aktRechnung);
			
			starteDokument(Reha.proghome+"vorlagen/"+Reha.aktIK+"/"+SystemConfig.hmAbrechnung.get("hmbgeformular"));
			starteErsetzen();
			startePositionen();
			
			starteDrucken();

			if(Reha.vollbetrieb){
				doFaktura("bge");
			
				doOffenePosten("bge");
	
				doUebertrag();
			}
			doTabelle();
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
	}
	private void doFaktura(String kostentraeger){
		// Hier die Sätze in die faktura-datenbank schreiben
		
		String plz = "";
		String ort = "";
		int hbpos = -1;
		int wgpos = -1;
		int diff = originalPos.size()-originalId.size();
		if(diff==2){
			hbpos=originalId.size()+1;
			wgpos=originalId.size()+2;
		}else if(diff==1){
			hbpos=originalId.size()+1;
		}
		try{
			int idummy = hmAdresse.get("<pri4>").indexOf(" ");
			plz = hmAdresse.get("<pri4>").substring(0,idummy).trim();
			ort = hmAdresse.get("<pri4>").substring(idummy).trim();
		}catch(Exception ex){}
		for(int i = 0; i < originalPos.size();i++){
			writeBuf.setLength(0);
			writeBuf.trimToSize();
			if(i==0){
				writeBuf.append("insert into faktura set kassen_nam='"+StringTools.EscapedDouble(hmAdresse.get("<pri1>"))+"', ");
				writeBuf.append("kassen_na2='"+StringTools.EscapedDouble(hmAdresse.get("<pri2>"))+"', ");
				writeBuf.append("strasse='"+StringTools.EscapedDouble(hmAdresse.get("<pri3>"))+"', ");
				writeBuf.append("plz='"+plz+"', ort='"+ort+"', ");
				writeBuf.append("name='"+StringTools.EscapedDouble(Reha.thisClass.patpanel.patDaten.get(2)+", "+
						Reha.thisClass.patpanel.patDaten.get(3) )+"', ");
			}else{
				writeBuf.append("insert into faktura set ");
			}
			writeBuf.append("lfnr='"+Integer.toString(i)+"', "); 
			if(i == (hbpos-1)){
				//Hausbesuch
				writeBuf.append("pos_int='"+RezTools.getIDFromPos(originalPos.get(i), "", preisliste)+"', ");
				writeBuf.append("anzahl='"+Integer.toString(originalAnzahl.get(i))+"', ");
				writeBuf.append("anzahltage='"+Integer.toString(originalAnzahl.get(i))+"', ");
			}else if(i == (wgpos-1)){
				//Weggebühren Kilometer und Pauschale differenzieren
				writeBuf.append("pos_int='"+RezTools.getIDFromPos(originalPos.get(i), "", preisliste)+"', ");
				writeBuf.append("anzahl='"+Integer.toString(originalAnzahl.get(i))+"', ");
				if(patKilometer > 0){
					String tage = Integer.toString(originalAnzahl.get(i)/patKilometer);
					writeBuf.append("anzahltage='"+tage+"', ");					
					writeBuf.append("kilometer='"+dcf.format(Double.parseDouble(Integer.toString(patKilometer))).replace(",", ".")+"', ");
				}else{
					writeBuf.append("anzahltage='"+Integer.toString(originalAnzahl.get(i))+"', ");
				}
			}else{
				writeBuf.append("pos_int='"+originalId.get(i)+"', ");
				writeBuf.append("anzahl='"+Integer.toString(originalAnzahl.get(i))+"', ");
				writeBuf.append("anzahltage='"+Integer.toString(originalAnzahl.get(i))+"', ");
			}
			writeBuf.append("pos_kas='"+originalPos.get(i)+"', ");
			writeBuf.append("kuerzel='"+RezTools.getKurzformFromPos(originalPos.get(i), "", preisliste)+"', ");
			writeBuf.append("preis='"+dcf.format(einzelPreis.get(i)).replace(",", ".")+"', ");
			writeBuf.append("gesamt='"+dcf.format(zeilenGesamt.get(i).doubleValue()).replace(",", ".")+"', ");
			writeBuf.append("zuzahl='F', ");
			writeBuf.append("zzbetrag='0.00', ");
			writeBuf.append("netto='"+dcf.format(zeilenGesamt.get(i).doubleValue()).replace(",", ".")+"', ");
			writeBuf.append("rez_nr='"+Reha.thisClass.patpanel.vecaktrez.get(1)+"', ");
			writeBuf.append("rezeptart='"+(kostentraeger.equals("privat") ? "1" : "2" )+"', ");
			writeBuf.append("rnummer='"+aktRechnung+"', ");
			writeBuf.append("pat_intern='"+Reha.thisClass.patpanel.vecaktrez.get(0)+"', ");
			writeBuf.append("kassid='"+Reha.thisClass.patpanel.vecaktrez.get(37)+"', ");
			writeBuf.append("arztid='"+Reha.thisClass.patpanel.vecaktrez.get(16)+"', ");
			writeBuf.append("disziplin='"+Reha.thisClass.patpanel.vecaktrez.get(1).trim().substring(0,2)+"', ");
			writeBuf.append("rdatum='"+DatFunk.sDatInSQL(DatFunk.sHeute())+"'");
			SqlInfo.sqlAusfuehren(writeBuf.toString());
			////System.out.println(writeBuf.toString());
		}
	}
	private void doOffenePosten(String kostentraeger){
		rechnungBuf.setLength(0);
		rechnungBuf.trimToSize();
		rechnungBuf.append("insert into rliste set ");
		rechnungBuf.append("r_nummer='"+aktRechnung+"', ");
		rechnungBuf.append("r_datum='"+DatFunk.sDatInSQL(DatFunk.sHeute())+"', ");
		if(kostentraeger.equals("privat")){
			rechnungBuf.append("r_kasse='"+StringTools.EscapedDouble(Reha.thisClass.patpanel.patDaten.get(2)+", "+
					Reha.thisClass.patpanel.patDaten.get(3) )+"', ");
		}else{
			rechnungBuf.append("r_kasse='"+StringTools.EscapedDouble(hmAdresse.get("<pri1>"))+"', ");
			String rname = StringTools.EscapedDouble(Reha.thisClass.patpanel.patDaten.get(2)+","+
					Reha.thisClass.patpanel.patDaten.get(3)+","+DatFunk.sDatInDeutsch(Reha.thisClass.patpanel.patDaten.get(4)) );
			rechnungBuf.append("r_name='"+rname+"', ");
		}
		rechnungBuf.append("r_klasse='"+Reha.thisClass.patpanel.vecaktrez.get(1).trim().substring(0,2)+"', ");
		rechnungBuf.append("r_betrag='"+dcf.format(rechnungGesamt.doubleValue()).replace(",", ".")+"', ");
		rechnungBuf.append("r_offen='"+dcf.format(rechnungGesamt.doubleValue()).replace(",", ".")+"', ");
		rechnungBuf.append("r_zuzahl='0.00', ");
		rechnungBuf.append("ikktraeger='"+Reha.thisClass.patpanel.vecaktrez.get(37)+"',");
		rechnungBuf.append("pat_intern='"+Reha.thisClass.patpanel.vecaktrez.get(0)+"'");
		SqlInfo.sqlAusfuehren(rechnungBuf.toString());		
	}
	private void doUebertrag(){
		 String rez_nr = String.valueOf(Reha.thisClass.patpanel.vecaktrez.get(1).toString());
		 
		 SqlInfo.transferRowToAnotherDB("verordn", "lza","rez_nr", rez_nr, true, Arrays.asList(new String[] {"id"}));
		 
		 if(Reha.thisClass.patpanel.vecaktrez.get(62).trim().equals("T")){
			 SqlInfo.sqlAusfuehren("delete from fertige where rez_nr='"+rez_nr+"' LIMIT 1");			
		 }

		 SqlInfo.sqlAusfuehren("delete from verordn where rez_nr='"+rez_nr+"'");

		 Reha.thisClass.patpanel.historie.holeRezepte(Reha.thisClass.patpanel.patDaten.get(29), "");
		 SqlInfo.sqlAusfuehren("delete from volle where rez_nr='"+rez_nr+"'");

	}
	
	private void doTabelle(){
		int row =  Reha.thisClass.patpanel.aktRezept.tabaktrez.getSelectedRow();
		if(row >= 0){
			TableTool.loescheRowAusModel(Reha.thisClass.patpanel.aktRezept.tabaktrez, row);
			Reha.thisClass.patpanel.aktRezept.tabaktrez.repaint();
			Reha.thisClass.patpanel.aktRezept.setzeKarteiLasche();
		}
	}
	
	private void doNeuerTarif(){
		//System.out.println("Disziplin = "+this.disziplin);
		//System.out.println("AktGruppe = "+this.aktGruppe);
		preisliste = SystemPreislisten.hmPreise.get(this.disziplin).get(this.aktGruppe);
		//System.out.println("stelle neuen Tarif ein....");
		String pos = "";
		String preis = "";
		String anzahl = "";
		einzelPreis.clear();
		originalPos.clear();
		originalAnzahl.clear();
		originalId.clear();
		originalLangtext.clear();
		zeilenGesamt.clear();
		rechnungGesamt = BigDecimal.valueOf(Double.parseDouble("0.00"));
		patKilometer = 0;

		if(!Reha.thisClass.patpanel.vecaktrez.get(8).equals("0")){
			originalPos.add(Reha.thisClass.patpanel.vecaktrez.get(48));
			originalId.add(Reha.thisClass.patpanel.vecaktrez.get(8));
			originalAnzahl.add(Integer.parseInt(Reha.thisClass.patpanel.vecaktrez.get(3)));
			originalLangtext.add(RezTools.getLangtextFromID(Reha.thisClass.patpanel.vecaktrez.get(8), "", preisliste).replace("30Min.", "").replace("45Min.", "")); 

			pos = RezTools.getKurzformFromID(Reha.thisClass.patpanel.vecaktrez.get(8),preisliste);
			anzahl = Reha.thisClass.patpanel.vecaktrez.get(3);
			preis = RezTools.getPreisAktFromID(Reha.thisClass.patpanel.vecaktrez.get(8),"", preisliste);
			
			if(! pos.trim().equals("")){
				einzelPreis.add(Double.parseDouble(preis));
				labs[0].setText(anzahl+" * "+pos+" (Einzelpreis = "+preis+")");

			}else{
				JOptionPane.showMessageDialog(null,"Die Rezeptpositionen sind in dieser preisgruppe nicht vorhanden");
				labs[0].setText(anzahl+" * "+pos+" (Einzelpreis = 0.00)");
			}
		}
		/***********************************/
		if(!Reha.thisClass.patpanel.vecaktrez.get(9).equals("0")){
			originalPos.add(Reha.thisClass.patpanel.vecaktrez.get(49));
			originalId.add(Reha.thisClass.patpanel.vecaktrez.get(9));
			originalAnzahl.add(Integer.parseInt(Reha.thisClass.patpanel.vecaktrez.get(4)));
			originalLangtext.add(RezTools.getLangtextFromID(Reha.thisClass.patpanel.vecaktrez.get(9), "", preisliste).replace("30Min.", "").replace("45Min.", ""));
			
			pos = RezTools.getKurzformFromID(Reha.thisClass.patpanel.vecaktrez.get(9),preisliste);
			anzahl = Reha.thisClass.patpanel.vecaktrez.get(4);
			preis = RezTools.getPreisAktFromID(Reha.thisClass.patpanel.vecaktrez.get(9),"", preisliste);
			
			if(! pos.trim().equals("")){
				einzelPreis.add(Double.parseDouble(preis));
				labs[1].setText(anzahl+" * "+pos+" (Einzelpreis = "+preis+")");

			}else{
				JOptionPane.showMessageDialog(null,"Die Rezeptpositionen sind in dieser preisgruppe nicht vorhanden");
				labs[1].setText(anzahl+" * "+pos+" (Einzelpreis = 0.00)");
			}
		}
		/***********************************/		
		if(!Reha.thisClass.patpanel.vecaktrez.get(10).equals("0")){
			originalPos.add(Reha.thisClass.patpanel.vecaktrez.get(50));
			originalId.add(Reha.thisClass.patpanel.vecaktrez.get(10));
			originalAnzahl.add(Integer.parseInt(Reha.thisClass.patpanel.vecaktrez.get(5)));
			originalLangtext.add(RezTools.getLangtextFromID(Reha.thisClass.patpanel.vecaktrez.get(10), "", preisliste).replace("30Min.", "").replace("45Min.", ""));
			
			pos = RezTools.getKurzformFromID(Reha.thisClass.patpanel.vecaktrez.get(10),preisliste);
			anzahl = Reha.thisClass.patpanel.vecaktrez.get(5);
			preis = RezTools.getPreisAktFromID(Reha.thisClass.patpanel.vecaktrez.get(10),"", preisliste);
			
			if(! pos.trim().equals("")){
				einzelPreis.add(Double.parseDouble(preis));
				labs[2].setText(anzahl+" * "+pos+" (Einzelpreis = "+preis+")");

			}else{
				JOptionPane.showMessageDialog(null,"Die Rezeptpositionen sind in dieser preisgruppe nicht vorhanden");
				labs[2].setText(anzahl+" * "+pos+" (Einzelpreis = 0.00)");
			}
		}
		/***********************************/
		if(!Reha.thisClass.patpanel.vecaktrez.get(11).equals("0")){
			originalPos.add(Reha.thisClass.patpanel.vecaktrez.get(51));
			originalId.add(Reha.thisClass.patpanel.vecaktrez.get(11));
			originalAnzahl.add(Integer.parseInt(Reha.thisClass.patpanel.vecaktrez.get(6)));
			originalLangtext.add(RezTools.getLangtextFromID(Reha.thisClass.patpanel.vecaktrez.get(11), "", preisliste).replace("30Min.", "").replace("45Min.", ""));
			
			pos = RezTools.getKurzformFromID(Reha.thisClass.patpanel.vecaktrez.get(11),preisliste);
			anzahl = Reha.thisClass.patpanel.vecaktrez.get(6);
			preis = RezTools.getPreisAktFromID(Reha.thisClass.patpanel.vecaktrez.get(11),"", preisliste);
			
			if(! pos.trim().equals("")){
				einzelPreis.add(Double.parseDouble(preis));
				labs[3].setText(anzahl+" * "+pos+" (Einzelpreis = "+preis+")");

			}else{
				JOptionPane.showMessageDialog(null,"Die Rezeptpositionen sind in dieser preisgruppe nicht vorhanden");
				labs[3].setText(anzahl+" * "+pos+" (Einzelpreis = 0.00)");
			}
		}
		if(hausBesuch){
			analysiereHausbesuch();
		}
		/*
		//System.out.println("Anzahlen = "+originalAnzahl);
		//System.out.println("Positionen = "+originalPos);
		//System.out.println("ID in Preisliste = "+originalId);
		//System.out.println("EinzelPeise = "+einzelPreis);			
		//System.out.println("Langtexte = "+originalLangtext);
		*/
		
		
		for(int i = 0; i < originalAnzahl.size();i++){
			BigDecimal zeilengesamt = BigDecimal.valueOf(einzelPreis.get(i)).multiply(BigDecimal.valueOf(Double.valueOf(Integer.toString(originalAnzahl.get(i)))));
			zeilenGesamt.add(BigDecimal.valueOf(zeilengesamt.doubleValue()));
			rechnungGesamt = rechnungGesamt.add(BigDecimal.valueOf(zeilengesamt.doubleValue()));		
		}
		try{
			labs[6].setText("Rezeptwert = "+dcf.format(rechnungGesamt.doubleValue())+" EUR");
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	private void analysiereHausbesuch(){
		this.aktGruppe = jcmb.getSelectedIndex();
		//System.out.println("Hausbesuch = "+this.hausBesuch);
		//System.out.println("Hausbesuch Einzeln = "+this.hbEinzeln);

		/***********Hausbesuch voll abrechnen******/
		int hbanzahl = Integer.parseInt(Reha.thisClass.patpanel.vecaktrez.get(64));

		if(this.hbEinzeln){

			String pos = SystemPreislisten.hmHBRegeln.get(disziplin).get(this.aktGruppe).get(0);
			String preis = RezTools.getPreisAktFromPos(pos.toString(), "", preisliste);
			originalAnzahl.add(hbanzahl);
			originalPos.add(pos.toString());
			einzelPreis.add(Double.parseDouble(preis.toString()));
			originalLangtext.add("Hausbesuchspauschale");
			labs[4].setText(hbanzahl+" * "+pos+" (Einzelpreis = "+preis+")");
			patKilometer = StringTools.ZahlTest(Reha.thisClass.patpanel.patDaten.get(48));
			if(patKilometer <= 0){
				// Keine Kilometer Im Patientenstamm hinterlegt
				if( (pos = SystemPreislisten.hmHBRegeln.get(disziplin).get(this.aktGruppe).get(3)).trim().equals("")){
					//Wegegeldpauschale ist nicht vorgesehen und Kilometer sind null - ganz schön blöd....
					JOptionPane.showMessageDialog(null, "Im Patientenstamm sind keine Kilometer hinterlegt und eine pauschale\n"+
							"Wegegeldberechnung ist für diese Tarifgruppe nicht vorgesehen.\nWegegeld wird nicht abgerechnet!");
				}else{
					preis = RezTools.getPreisAktFromPos(pos.toString(), "", preisliste);
					originalAnzahl.add(hbanzahl);
					originalPos.add(pos.toString());
					einzelPreis.add(Double.parseDouble(preis.toString()));
					originalLangtext.add("Wegegeldpauschale");
					labs[5].setText(hbanzahl+" * "+pos+" (Einzelpreis = "+preis+")");
					hbPauschale = true;
				}
			}else{
				/*******es wurden zwar Kilometer angegeben aber diese Preisgruppe kennt keine Wegegebühr****/
				if( (pos = SystemPreislisten.hmHBRegeln.get(disziplin).get(this.aktGruppe).get(2)).trim().equals("")){
					JOptionPane.showMessageDialog(null, "Im Patientenstamm sind zwar "+patKilometer+" Kilometer hinterlegt aber Wegegeldberechnung\n"+
					"ist für diese Tarifgruppe nicht vorgesehen.\nWegegeld wird nicht aberechnet!");
				}else{
					preis = RezTools.getPreisAktFromPos(pos.toString(), "", preisliste);
					kmBeiHB = patKilometer;
					originalAnzahl.add(hbanzahl*patKilometer);
					originalPos.add(pos.toString());
					einzelPreis.add(Double.parseDouble(preis.toString()));
					originalLangtext.add("Wegegeld / km");
					labs[5].setText((hbanzahl*patKilometer)+" * "+pos+" (Einzelpreis = "+preis+")");
					hbmitkm = true;
				}
			}
 
		}else{ /****************************Hausbesuch mehrere abrechnen***************************/
			String pos = SystemPreislisten.hmHBRegeln.get(disziplin).get(this.aktGruppe).get(1);
			if(pos.trim().equals("")){
				JOptionPane.showMessageDialog(null, "In dieser Tarifgruppe ist die Ziffer Hausbesuche - mehrere Patienten - nicht vorgeshen!\n");
			}else{
				String preis = RezTools.getPreisAktFromPos(pos.toString(), "", preisliste);
				originalAnzahl.add(hbanzahl);
				originalPos.add(pos.toString());
				einzelPreis.add(Double.parseDouble(preis.toString()));
				originalLangtext.add("Hausbesuchspauschale (mehrere Patienten)");
				labs[5].setText((hbanzahl)+" * "+pos+" (Einzelpreis = "+preis+")");				
			}
		}
	}
	private void doKorrektur(){
		
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	private void regleBGE(){
		holeBGE();
		adr1.setText( (hmAdresse.get("<pri1>").trim().equals("")? " " : hmAdresse.get("<pri1>") )  );
		adr2.setText( (hmAdresse.get("<pri2>").trim().equals("")? " " : hmAdresse.get("<pri2>") )  );
		//adr1.getParent().validate();
	}
	private void reglePrivat(){
		holePrivat();
		adr1.setText( (hmAdresse.get("<pri1>").trim().equals("")? " " : hmAdresse.get("<pri1>") )  );
		adr2.setText( (hmAdresse.get("<pri2>").trim().equals("")? " " : hmAdresse.get("<pri2>") )  );
		//adr1.getParent().validate();
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String cmd = arg0.getActionCommand();
		if(cmd.equals("privatadresse")){
			reglePrivat();
			return;
		}
		if(cmd.equals("kassendresse")){
			regleBGE();
			return;
		}
		if(cmd.equals("neuertarif")){
			this.aktGruppe = jcmb.getSelectedIndex();
			doNeuerTarif();
			return;
		}
		if(cmd.equals("korrektur")){
			this.rueckgabe = -2;
			//doKorrektur();
			FensterSchliessen("dieses");
			return;
		}
		if(cmd.equals("abbrechen")){
			this.rueckgabe = -1;
			FensterSchliessen("dieses");
		}
		if(cmd.equals("ok")){
			this.rueckgabe = 0;
			doRgRechnungPrepare();
		}
		
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode()==27){
			this.rueckgabe = -1;
			FensterSchliessen("dieses");
			return;
		}
		if(arg0.getKeyCode()==10){
			if(((JComponent)arg0.getSource()) instanceof JButton){
				if(((JComponent)arg0.getSource()).getName().equals("abbrechen")){
					this.rueckgabe = -1;
					FensterSchliessen("dieses");
					return;
				}else if(((JComponent)arg0.getSource()).getName().equals("korrektur")){
					doKorrektur();
					return;
				}else if(((JComponent)arg0.getSource()).getName().equals("ok")){
					this.rueckgabe = 0;
					doRgRechnungPrepare();
				}
			}
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
	public void rehaTPEventOccurred(RehaTPEvent evt) {
		FensterSchliessen("dieses");
		
	}
	public void FensterSchliessen(String welches){
		this.jtp.removeMouseListener(this.mymouse);
		this.jtp.removeMouseMotionListener(this.mymouse);
		this.jcmb.removeActionListener(this);
		this.content.removeKeyListener(this);
		this.originalPos.clear();
		this.originalPos = null;
		this.originalAnzahl.clear();
		this.originalAnzahl = null;
		this.einzelPreis.clear();
		this.einzelPreis = null;
		this.originalId.clear();
		this.originalId = null;
		this.originalLangtext.clear();
		this.originalLangtext = null;
		this.zeilenGesamt.clear();
		this.zeilenGesamt = null;
		this.rechnungGesamt = null;
		this.hmAdresse.clear();
		this.hmAdresse = null;
		for(int i = 0; i < 3;i++){
			but[i].removeActionListener(this);
			but[i].removeKeyListener(this);
			but[i] = null;
		}
		this.mymouse = null; 
		if(this.rtp != null){
			this.rtp.removeRehaTPEventListener((RehaTPEventListener) this);
			this.rtp=null;			
		}
		this.pinPanel = null;
		setVisible(false);
		this.dispose();
	}
	public void starteDokument(String url) throws Exception{
		IDocumentService documentService = null;;
		documentService = Reha.officeapplication.getDocumentService();
		IDocumentDescriptor docdescript = new DocumentDescriptor();
        docdescript.setHidden(true);
        docdescript.setAsTemplate(true);
		IDocument document = null;
		document = documentService.loadDocument(url,docdescript);
		/**********************/
		textDocument = (ITextDocument)document;
		if(jrb[0].isSelected()){
			OOTools.druckerSetzen(textDocument, SystemConfig.hmAbrechnung.get("hmpridrucker"));
		}else{
			OOTools.druckerSetzen(textDocument, SystemConfig.hmAbrechnung.get("hmbgedrucker"));
		}
		textTable = textDocument.getTextTableService().getTextTable("Tabelle1");
		textEndbetrag = textDocument.getTextTableService().getTextTable("Tabelle2");
	}
	private void starteErsetzen(){
		ITextFieldService textFieldService = textDocument.getTextFieldService();
		ITextField[] placeholders = null;
		try {
			placeholders = textFieldService.getPlaceholderFields();
		} catch (TextException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < placeholders.length; i++) {
			if(placeholders[i].getDisplayText().toLowerCase().equals("<pri1>")){
				placeholders[i].getTextRange().setText(hmAdresse.get("<pri1>"));
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<pri2>")){
				placeholders[i].getTextRange().setText(hmAdresse.get("<pri2>"));				
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<pri3>")){
				placeholders[i].getTextRange().setText(hmAdresse.get("<pri3>"));				
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<pri4>")){
				placeholders[i].getTextRange().setText(hmAdresse.get("<pri4>"));				
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<pri5>")){
				placeholders[i].getTextRange().setText(hmAdresse.get("<pri5>"));				
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<pri6>")){
				placeholders[i].getTextRange().setText(hmAdresse.get("<pri6>"));
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<pnname>")){
				placeholders[i].getTextRange().setText(SystemConfig.hmAdrPDaten.get("<Pnname>"));
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<pvname>")){
				placeholders[i].getTextRange().setText(SystemConfig.hmAdrPDaten.get("<Pvname>"));
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<pgeboren>")){
				placeholders[i].getTextRange().setText(SystemConfig.hmAdrPDaten.get("<Pgeboren>"));
			}else if(placeholders[i].getDisplayText().toLowerCase().equals("<panrede>")){
				placeholders[i].getTextRange().setText(SystemConfig.hmAdrPDaten.get("<Panrede>"));
			}
		}
		
	}
	private void startePositionen() throws TextException{
		//ITextTableCell[] tcells;// = null;
		//Vector<BigDecimal> einzelpreis = new Vector<BigDecimal>();
		//Vector<BigDecimal> gesamtpreis = new Vector<BigDecimal>();
		aktuellePosition++;	 
		for(int i = 0; i < originalAnzahl.size();i++){
			//tcells = textTable.getRow(aktuellePosition).getCells();
			textTable.getCell(0,aktuellePosition).getTextService().getText().setText(originalLangtext.get(i));
			textTable.getCell(1,aktuellePosition).getTextService().getText().setText(Integer.toString(originalAnzahl.get(i)));
			textTable.getCell(2,aktuellePosition).getTextService().getText().setText(dcf.format(einzelPreis.get(i)));
			//BigDecimal zeilengesamt = BigDecimal.valueOf(einzelPreis.get(i)).multiply(BigDecimal.valueOf(Double.valueOf(Integer.toString(originalAnzahl.get(i)))));
			//zeilenGesamt.add(BigDecimal.valueOf(zeilengesamt.doubleValue()));
			//rechnungGesamt = rechnungGesamt.add(BigDecimal.valueOf(zeilenGesamt.get(i).doubleValue()));
			textTable.getCell(3,aktuellePosition).getTextService().getText().setText(dcf.format(zeilenGesamt.get(i).doubleValue()));
			//if(i < (originalAnzahl.size()-1) ){
				textTable.addRow(1);
				aktuellePosition++;				
			//}
		}
		//tcells = textEndbetrag.getRow(0).getCells();
		textEndbetrag.getCell(1,0).getTextService().getText().setText(dcf.format(rechnungGesamt.doubleValue())+" EUR");

	}
	private void starteDrucken() throws DocumentException, InterruptedException{
		if(SystemConfig.hmAbrechnung.get("hmallinoffice").equals("1")){
			textDocument.getFrame().getXFrame().getContainerWindow().setVisible(true);
		}else{
			int exemplare = 0;
			if(jrb[0].isSelected()){
				 exemplare = Integer.parseInt(SystemConfig.hmAbrechnung.get("hmpriexemplare"));	
			}else{
				exemplare = Integer.parseInt(SystemConfig.hmAbrechnung.get("hmbgeexemplare"));
			}
			PrintProperties printprop = new PrintProperties ((short)exemplare,null);
			textDocument.getPrintService().print(printprop);
			Thread.sleep(200);
			textDocument.close();
		}
	}
	@Override
	public void stateChanged(ChangeEvent arg0) {
		if(jrb[0].isSelected()){
			reglePrivat();
		}else{
			regleBGE();
		}
	}

}
