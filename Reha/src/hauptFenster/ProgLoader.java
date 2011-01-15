package hauptFenster;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.beans.PropertyVetoException;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import krankenKasse.KassenPanel;
import menus.OOIFTest;

import org.therapi.reha.patient.PatientHauptPanel;

import rechteTools.Rechte;
import rehaContainer.RehaTP;
import rehaInternalFrame.JAbrechnungInternal;
import rehaInternalFrame.JAnmeldungenInternal;
import rehaInternalFrame.JArztInternal;
import rehaInternalFrame.JBarkassenInternal;
import rehaInternalFrame.JBenutzerInternal;
import rehaInternalFrame.JBeteiligungInternal;
import rehaInternalFrame.JDta301Internal;
import rehaInternalFrame.JGutachtenInternal;
import rehaInternalFrame.JKasseInternal;
import rehaInternalFrame.JPatientInternal;
import rehaInternalFrame.JRehaInternal;
import rehaInternalFrame.JRehaabrechnungInternal;
import rehaInternalFrame.JSysteminitInternal;
import rehaInternalFrame.JTerminInternal;
import rehaInternalFrame.JUmsaetzeInternal;
import rehaInternalFrame.JUrlaubInternal;
import rehaInternalFrame.JVerkaufInternal;
import roogle.RoogleFenster;
import systemEinstellungen.SystemConfig;
import systemEinstellungen.SystemInit;

import systemTools.PassWort;

import systemTools.WinNum;
import terminKalender.DatFunk;
import terminKalender.TerminFenster;
import urlaubBeteiligung.Beteiligung;
import urlaubBeteiligung.Urlaub;
import verkauf.Verkauf;
import abrechnung.AbrechnungGKV;
import abrechnung.AbrechnungReha;
import anmeldungUmsatz.Anmeldungen;
import anmeldungUmsatz.Umsaetze;
import arztFenster.ArztPanel;
import barKasse.Barkasse;
import benutzerVerwaltung.BenutzerRechte;

import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import dta301.Dta301;
import entlassBerichte.EBerichtPanel;
import events.RehaTPEvent;

public class ProgLoader {
public JPatientInternal patjry = null; 
public JGutachtenInternal gutjry = null;
public RoogleFenster roogleDlg = null;
public JArztInternal arztjry = null;
public JKasseInternal kassejry = null;
public JTerminInternal terminjry = null;
public JAbrechnungInternal abrechjry = null;
public JAnmeldungenInternal anmeldungenjry = null;
public JUmsaetzeInternal umsaetzejry = null;
public JVerkaufInternal verkaufjry = null;
public JBarkassenInternal barkassenjry = null;
public JRehaabrechnungInternal rehaabrechnungjry = null;
public JBeteiligungInternal beteiligungjry = null;
public JUrlaubInternal urlaubjry = null;
public JBenutzerInternal benutzerjry = null;
public JSysteminitInternal systeminitjry = null;
public JDta301Internal dta301jry = null;
//public static JTerminInternal tjry = null;
//public static JGutachtenInternal gjry = null;

	public ProgLoader(){
		
	}

protected static RehaSmartDialog xsmart;

/**************Patient suchen (Test)**********************************/
public static void ProgPatSuche(boolean setPos){
	
}
/**************Terminkalender Echtfunktion****************************/
public void ProgTerminFenster(int setPos,int ansicht) {
	if(! Reha.DbOk){
		return;
	}
	if(ansicht == 2){
		if(!Rechte.hatRecht(Rechte.Masken_erstellen, true)){
			return;
		}
	}
	JComponent termin = AktiveFenster.getFensterAlle("TerminFenster");
	if(termin != null){
		if(ansicht==2){
			JOptionPane.showMessageDialog(null,"Um die Wochenarbeitszeit zu starten,\nschließen Sie bitte zunächst den Terminkalender");
		}
		//////System.out.println("Der Terminkalender befindet sich in Container "+((JTerminInternal)termin).getDesktop());
		containerHandling(((JTerminInternal)termin).getDesktop());
		((JTerminInternal)termin).aktiviereDiesenFrame(((JTerminInternal)termin).getName());
		if( ((JTerminInternal)termin).isIcon() ){
			try {
				((JTerminInternal)termin).setIcon(false);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
		return;
	}
	final int xansicht=ansicht;

	SwingUtilities.invokeLater(new Runnable(){
		public  void run(){
			String name = "TerminFenster"+WinNum.NeueNummer();
 
			int containerNr = SystemConfig.hmContainer.get("Kalender");
			containerHandling(containerNr);
			LinkeTaskPane.thisClass.setCursor(Reha.thisClass.wartenCursor);
			terminjry = null;
			if(xansicht != 2){
				String stag = DatFunk.sHeute();
				String titel = DatFunk.WochenTag(stag)+" "+stag+" -- KW: "+DatFunk.KalenderWoche(stag)+" -- [Normalansicht]";
				terminjry = new JTerminInternal(titel,new ImageIcon(Reha.proghome+"icons/calendar.png"),containerNr);				
			}else{
				terminjry = new JTerminInternal("Terminkalender - "+DatFunk.sHeute(),new ImageIcon(Reha.proghome+"icons/calendar.png"),containerNr);
			}
			terminjry.setName(name);
			((JRehaInternal)terminjry).setImmerGross(true);
			Reha.thisClass.terminpanel = new TerminFenster();
			terminjry.setContent(Reha.thisClass.terminpanel.init(containerNr, xansicht,terminjry));
			terminjry.setLocation(new Point(5,5));
			terminjry.setSize(new Dimension(Reha.thisClass.jpOben.getWidth(),Reha.thisClass.jpOben.getHeight()));
			terminjry.setPreferredSize(new Dimension(Reha.thisClass.jpOben.getWidth(),Reha.thisClass.jpOben.getHeight()));
			terminjry.pack();
			terminjry.setVisible(true);
			Reha.thisClass.desktops[containerNr].add(terminjry);
			LinkeTaskPane.thisClass.setCursor(Reha.thisClass.normalCursor);
			AktiveFenster.setNeuesFenster(name,(JComponent)terminjry,containerNr,(Container)Reha.thisClass.terminpanel.getViewPanel());			
			((JTerminInternal)terminjry).aktiviereDiesenFrame(((JTerminInternal)terminjry).getName());
			SwingUtilities.invokeLater(new Runnable(){
			 	   public  void run()
			 	   {
			 		  Reha.thisClass.terminpanel.getViewPanel().requestFocus();
			 	   }
			}); 	   			
		}
	});

}
public void loescheTermine(){
	terminjry = null;
	Reha.thisClass.terminpanel = null;
}

/**************Roogle Echtfunktion***********************/
public void ProgRoogleFenster(int setPos,String droptext) {
	final String xdroptext = droptext;
	
	new Thread(){
		public void run(){
			if(!Rechte.hatRecht(Rechte.Rugl_open, true)){
				return;
			}
 		   	Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
 			roogleDlg = new RoogleFenster(Reha.thisFrame,xdroptext);
 			roogleDlg.setSize(940,680);
 			roogleDlg.setPreferredSize(new Dimension(940,680));
 			roogleDlg.setLocationRelativeTo(null);
 			roogleDlg.pack();
 			roogleDlg.setVisible(true);
 			Reha.thisFrame.setCursor(Reha.thisClass.normalCursor);
		}
	}.start();
}
public void loescheRoogle(){
	roogleDlg = null;
}


/**************Krankenkassenverwaltung Echtfunktion***********************/
public void KassenFenster(int setPos,String kid) {
	if(! Reha.DbOk){
		return;
	}
	JComponent kasse = AktiveFenster.getFensterAlle("KrankenKasse");
	if(kasse != null){
		containerHandling(((JKasseInternal)kasse).getDesktop());
		((JKasseInternal)kasse).aktiviereDiesenFrame( ((JKasseInternal)kasse).getName());
		((JKasseInternal)kasse).starteKasseID(kid);
		if( ((JKasseInternal)kasse).isIcon() ){
			try {
				((JKasseInternal)kasse).setIcon(false);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
		return;
	}
	Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
	String name = "KrankenKasse"+WinNum.NeueNummer();
	int containerNr = SystemConfig.hmContainer.get("Kasse");
	containerHandling(containerNr);
	kassejry = new JKasseInternal("thera-\u03C0 Krankenkassen-Verwaltung ",SystemConfig.hmSysIcons.get("kassenstamm"),containerNr) ;
	AktiveFenster.setNeuesFenster(name,(JComponent)kassejry,containerNr,(Container)kassejry.getContentPane());
	kassejry.setName(name);
	kassejry.setSize(new Dimension(650,500));
	kassejry.setPreferredSize(new Dimension(650,500));
	Reha.thisClass.kassenpanel = new KassenPanel(kassejry,kid); 
	kassejry.setContent(Reha.thisClass.kassenpanel);	
	kassejry.addComponentListener(Reha.thisClass);
	int comps = Reha.thisClass.desktops[containerNr].getComponentCount();
	kassejry.setLocation(comps*10, comps*10);
	kassejry.pack();
	kassejry.setVisible(true);
	Reha.thisClass.desktops[containerNr].add(kassejry);
	((JRehaInternal)kassejry).setImmerGross( (SystemConfig.hmContainer.get("KasseOpti") > 0 ? true : false));
	////System.out.println("Anzahl Fenster = "+Reha.thisClass.desktops[containerNr].getComponentCount());
	((JKasseInternal)kassejry).aktiviereDiesenFrame( ((JKasseInternal)kassejry).getName());
	Reha.thisFrame.setCursor(Reha.thisClass.normalCursor);
	Reha.thisClass.kassenpanel.setzeFocus();
}
public void loescheKasse(){
	kassejry = null;
	Reha.thisClass.kassenpanel = null;
}

/**************Ärzteverwaltung Echtfunktion***********************/
public void ArztFenster(int setPos,String aid) {
	if(! Reha.DbOk){
		return;
	}
	JComponent arzt = AktiveFenster.getFensterAlle("ArztVerwaltung");
	if(arzt != null){
		containerHandling(((JArztInternal)arzt).getDesktop());
		((JArztInternal)arzt).aktiviereDiesenFrame( ((JArztInternal)arzt).getName());
		((JArztInternal)arzt).starteArztID(aid);
		if( ((JArztInternal)arzt).isIcon() ){
			try {
				((JArztInternal)arzt).setIcon(false);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
		return;
	}
	Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
	String name = "ArztVerwaltung"+WinNum.NeueNummer();
	int containerNr = SystemConfig.hmContainer.get("Arzt");
	containerHandling(containerNr);
	arztjry = new JArztInternal("thera-\u03C0 Ärzte-Verwaltung ",SystemConfig.hmSysIcons.get("arztstamm"),containerNr) ;
	AktiveFenster.setNeuesFenster(name,(JComponent)arztjry,containerNr,(Container)arztjry.getContentPane());
	arztjry.setName(name);
	arztjry.setSize(new Dimension(650,500));
	arztjry.setPreferredSize(new Dimension(650,500));
	Reha.thisClass.arztpanel = new ArztPanel(arztjry,aid); 
	arztjry.setContent(Reha.thisClass.arztpanel);	
	arztjry.addComponentListener(Reha.thisClass);
	int comps = Reha.thisClass.desktops[containerNr].getComponentCount();
	arztjry.setLocation(comps*10, comps*10);
	arztjry.pack();
	arztjry.setVisible(true);
	Reha.thisClass.desktops[containerNr].add(arztjry);
	((JRehaInternal)arztjry).setImmerGross( (SystemConfig.hmContainer.get("ArztOpti") > 0 ? true : false));
	////System.out.println("Anzahl Fenster = "+Reha.thisClass.desktops[containerNr].getComponentCount());
	((JArztInternal)arztjry).aktiviereDiesenFrame( ((JArztInternal)arztjry).getName());
	Reha.thisFrame.setCursor(Reha.thisClass.normalCursor);
	Reha.thisClass.arztpanel.setzeFocus();
	
}
public void loescheArzt(){
	arztjry = null;
	Reha.thisClass.arztpanel = null;
}


/**************Gutachten Echtfunktion***********************/
public void GutachenFenster(int setPos,String pat_intern,int berichtid,String berichttyp,boolean neu,String empfaenger,int uebernahmeid ) {
	if(! Reha.DbOk){
		return;
	}
	if(!Rechte.hatRecht(Rechte.Gutachten_anlegen, true)){
		return;
	}
	JComponent gutachten = AktiveFenster.getFensterAlle("GutachtenFenster");
	if(gutachten != null){
		containerHandling(((JGutachtenInternal)gutachten).getDesktop());
		((JGutachtenInternal)gutachten).aktiviereDiesenFrame( ((JGutachtenInternal)gutachten).getName());

		if( ((JGutachtenInternal)gutachten).isIcon() ){
			try {
				((JGutachtenInternal)gutachten).setIcon(false);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
		return;
	}
	Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
	String name = "GutachtenFenster"+WinNum.NeueNummer();
	int containerNr = SystemConfig.hmContainer.get("Arzt");
	containerHandling(containerNr);
	gutjry = new JGutachtenInternal("thera-\u03C0 Gutachten ",SystemConfig.hmSysIcons.get("drvlogo"),containerNr) ;
	AktiveFenster.setNeuesFenster(name,(JComponent)gutjry,containerNr,(Container)gutjry.getContentPane());
	gutjry.setName(name);
	gutjry.setSize(new Dimension(900,Reha.thisClass.desktops[containerNr].getHeight()-20));
	gutjry.setPreferredSize(new Dimension(900,Reha.thisClass.desktops[containerNr].getHeight()-20));
	Reha.thisClass.eberichtpanel = new EBerichtPanel(gutjry,pat_intern,berichtid,berichttyp,neu,empfaenger,uebernahmeid ); 
	gutjry.setContent(Reha.thisClass.eberichtpanel);
	gutjry.addComponentListener(Reha.thisClass);
	int comps = Reha.thisClass.desktops[containerNr].getComponentCount();
	gutjry.setLocation(comps*10, comps*10);
	gutjry.pack();
	gutjry.setVisible(true);
	Reha.thisClass.desktops[containerNr].add(gutjry);
	((JGutachtenInternal)gutjry).aktiviereDiesenFrame( ((JGutachtenInternal)gutjry).getName());
	Reha.thisFrame.setCursor(Reha.thisClass.normalCursor);
}
public void loescheGutachten(){
	gutjry = null;
	Reha.thisClass.eberichtpanel = null;
}

/******************************************/
public void AbrechnungFenster(int setPos) {
	if(! Reha.DbOk){
		return;
	}
	if(!Rechte.hatRecht(Rechte.Funktion_kassenabrechnung, true)){
		return;
	}
	JComponent abrech1 = AktiveFenster.getFensterAlle("Abrechnung");
	if(abrech1 != null){
		////System.out.println("InternalFrame Kassenabrechnung bereits geöffnet");
		containerHandling(((JAbrechnungInternal)abrech1).getDesktop());
		((JAbrechnungInternal)abrech1).aktiviereDiesenFrame( ((JAbrechnungInternal)abrech1).getName());
		if( ((JAbrechnungInternal)abrech1).isIcon() ){
			try {
				((JAbrechnungInternal)abrech1).setIcon(false);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
		return;
	}
	Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
	String name = "Abrechnung"+WinNum.NeueNummer();
	int containerNr = setPos;
	containerHandling(containerNr);
	abrechjry = new JAbrechnungInternal("thera-\u03C0  - Kassen-Abrechnung nach §302 ",SystemConfig.hmSysIcons.get("arztstamm"),1) ;
	AktiveFenster.setNeuesFenster(name,(JComponent)abrechjry,1,(Container)abrechjry.getContentPane());
	abrechjry.setName(name);
	abrechjry.setSize(new Dimension(850,700));
	abrechjry.setPreferredSize(new Dimension(850,700));
	Reha.thisClass.abrechnungpanel = new AbrechnungGKV(abrechjry); 
	abrechjry.setContent(Reha.thisClass.abrechnungpanel);	
	abrechjry.addComponentListener(Reha.thisClass);
	int comps = Reha.thisClass.desktops[containerNr].getComponentCount();
	abrechjry.setLocation(comps*15, comps*15);
	abrechjry.pack();
	abrechjry.setVisible(true);
	Reha.thisClass.desktops[containerNr].add(abrechjry);
	//((JRehaInternal)abrechjry).setImmerGross( (SystemConfig.hmContainer.get("ArztOpti") > 0 ? true : false));
	abrechjry.aktiviereDiesenFrame( abrechjry.getName());
	Reha.thisFrame.setCursor(Reha.thisClass.normalCursor);
	
}
public void loescheAbrechnung(){
	abrechjry = null;
	Reha.thisClass.abrechnungpanel = null;
}
/**********************Neuanmeldungen****************************/
public void AnmeldungenFenster(int setPos,String sparam) {
	if(! Reha.DbOk){
		return;
	}
	if(!Rechte.hatRecht(Rechte.Funktion_neuanmeldungen, true)){
		return;
	}
	JComponent anmeld = AktiveFenster.getFensterAlle("Anmeldungen");
	if(anmeld != null){
		////System.out.println("InternalFrame Anmeldungen bereits geöffnet");
		containerHandling(((JAnmeldungenInternal)anmeld).getDesktop());
		((JAnmeldungenInternal)anmeld).aktiviereDiesenFrame( ((JAnmeldungenInternal)anmeld).getName());
		if( ((JAnmeldungenInternal)anmeld).isIcon() ){
			try {
				((JAnmeldungenInternal)anmeld).setIcon(false);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
		return;
	}
	Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
	String name = "Anmeldungen"+WinNum.NeueNummer();
	//int containerNr = SystemConfig.hmContainer.get("Arzt");
	int containerNr = setPos;
	containerHandling(containerNr);
	anmeldungenjry = new JAnmeldungenInternal("thera-\u03C0  - Ermittlung des Anmeldevolumens ",SystemConfig.hmSysIcons.get("arztstamm"),1) ;
	AktiveFenster.setNeuesFenster(name,(JComponent)anmeldungenjry,1,(Container)anmeldungenjry.getContentPane());
	anmeldungenjry.setName(name);
	anmeldungenjry.setSize(new Dimension(500,500));
	anmeldungenjry.setPreferredSize(new Dimension(500,500));
	Reha.thisClass.anmeldungenpanel = new Anmeldungen(anmeldungenjry); 
	anmeldungenjry.setContent(Reha.thisClass.anmeldungenpanel);	
	anmeldungenjry.addComponentListener(Reha.thisClass);
	int comps = Reha.thisClass.desktops[containerNr].getComponentCount();
	anmeldungenjry.setLocation(comps*15, comps*15);
	anmeldungenjry.pack();
	anmeldungenjry.setVisible(true);
	Reha.thisClass.desktops[containerNr].add(anmeldungenjry);
	anmeldungenjry.aktiviereDiesenFrame( anmeldungenjry.getName());
	Reha.thisFrame.setCursor(Reha.thisClass.normalCursor);
	
}
public void loescheAnmeldungen(){
	anmeldungenjry = null;
	Reha.thisClass.anmeldungenpanel = null;
}
/*****************************Umsätze von bis*********************************/
public void UmsatzFenster(int setPos,String sparam) {
	if(! Reha.DbOk){
		return;
	}
	if(!Rechte.hatRecht(Rechte.Funktion_umsatzvonbis, true)){
		return;
	}
	JComponent umsatz = AktiveFenster.getFensterAlle("Umsaetze");
	if(umsatz != null){
		////System.out.println("InternalFrame Anmeldungen bereits geöffnet");
		containerHandling(((JUmsaetzeInternal)umsatz).getDesktop());
		((JUmsaetzeInternal)umsatz).aktiviereDiesenFrame( ((JUmsaetzeInternal)umsatz).getName());
		if( ((JUmsaetzeInternal)umsatz).isIcon() ){
			try {
				((JUmsaetzeInternal)umsatz).setIcon(false);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
		return;
	}
	Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
	String name = "Umsaetze"+WinNum.NeueNummer();
	int containerNr = setPos;
	containerHandling(containerNr);
	umsaetzejry = new JUmsaetzeInternal("thera-\u03C0  - Ermittlung der realisierten Umsätze ",SystemConfig.hmSysIcons.get("arztstamm"),1) ;
	AktiveFenster.setNeuesFenster(name,(JComponent)umsaetzejry,1,(Container)umsaetzejry.getContentPane());
	umsaetzejry.setName(name);
	umsaetzejry.setSize(new Dimension(500,150));
	umsaetzejry.setPreferredSize(new Dimension(500,150));
	Reha.thisClass.umsaetzepanel = new Umsaetze(umsaetzejry); 
	umsaetzejry.setContent(Reha.thisClass.umsaetzepanel);	
	umsaetzejry.addComponentListener(Reha.thisClass);
	int comps = Reha.thisClass.desktops[containerNr].getComponentCount();
	umsaetzejry.setLocation(comps*15, comps*15);
	umsaetzejry.pack();
	umsaetzejry.setVisible(true);
	Reha.thisClass.desktops[containerNr].add(umsaetzejry);
	umsaetzejry.aktiviereDiesenFrame( umsaetzejry.getName());
	Reha.thisFrame.setCursor(Reha.thisClass.normalCursor);
}

public void loescheUmsaetze(){
	umsaetzejry = null;
	Reha.thisClass.umsaetzepanel = null;
}
/***************************Verkäufe in der Praxis*********************************/
public void VerkaufFenster(int setPos,String sparam) {
	if(! Reha.DbOk){
		return;
	}
	if(!Rechte.hatRecht(Rechte.Sonstiges_verkaufsmodul, true)){
		return;
	}
	JComponent vk = AktiveFenster.getFensterAlle("Verkauf");
	if(vk != null){
		////System.out.println("InternalFrame Anmeldungen bereits geöffnet");
		containerHandling(((JVerkaufInternal)vk).getDesktop());
		((JVerkaufInternal)vk).aktiviereDiesenFrame( ((JVerkaufInternal)vk).getName());
		if( ((JVerkaufInternal)vk).isIcon() ){
			try {
				((JVerkaufInternal)vk).setIcon(false);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
		return;
	}
	Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
	String name = "Verkauf"+WinNum.NeueNummer();
	int containerNr = setPos;
	containerHandling(containerNr);
	verkaufjry = new JVerkaufInternal("thera-\u03C0  - Verkäufe tätigen ",SystemConfig.hmSysIcons.get("arztstamm"),1) ;
	AktiveFenster.setNeuesFenster(name,(JComponent)verkaufjry,1,(Container)verkaufjry.getContentPane());
	verkaufjry.setName(name);
	verkaufjry.setSize(new Dimension(500,500));
	verkaufjry.setPreferredSize(new Dimension(500,500));
	Reha.thisClass.verkaufpanel = new Verkauf(verkaufjry); 
	verkaufjry.setContent(Reha.thisClass.verkaufpanel);	
	verkaufjry.addComponentListener(Reha.thisClass);
	int comps = Reha.thisClass.desktops[containerNr].getComponentCount();
	verkaufjry.setLocation(comps*15, comps*15);
	verkaufjry.pack();
	verkaufjry.setVisible(true);
	Reha.thisClass.desktops[containerNr].add(verkaufjry);
	verkaufjry.aktiviereDiesenFrame( verkaufjry.getName());
	Reha.thisFrame.setCursor(Reha.thisClass.normalCursor);
}

public void loescheVerkauf(){
	verkaufjry = null;
	Reha.thisClass.verkaufpanel = null;
}
/***************************Fallsteuerung nach § 301*********************************/
public void Dta301Fenster(int setPos,String sparam) {
	if(! Reha.DbOk){
		return;
	}
	if(!Rechte.hatRecht(Rechte.Sonstiges_verkaufsmodul, true)){
		return;
	}
	JComponent vk = AktiveFenster.getFensterAlle("DTA301");
	if(vk != null){
		////System.out.println("InternalFrame Anmeldungen bereits geöffnet");
		containerHandling(((JDta301Internal)vk).getDesktop());
		((JDta301Internal)vk).aktiviereDiesenFrame( ((JDta301Internal)vk).getName());
		if( ((JDta301Internal)vk).isIcon() ){
			try {
				((JDta301Internal)vk).setIcon(false);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
		return;
	}
	Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
	String name = "DTA301"+WinNum.NeueNummer();
	int containerNr = setPos;
	containerHandling(containerNr);
	dta301jry = new JDta301Internal("thera-\u03C0  - Fallsteuerung nach §301 ",SystemConfig.hmSysIcons.get("arztstamm"),1) ;
	AktiveFenster.setNeuesFenster(name,(JComponent)dta301jry,1,(Container)dta301jry.getContentPane());
	dta301jry.setName(name);
	dta301jry.setSize(new Dimension(780,470));
	dta301jry.setPreferredSize(new Dimension(780,470));
	Reha.thisClass.dta301panel = new Dta301(dta301jry); 
	dta301jry.setContent(Reha.thisClass.dta301panel);	
	dta301jry.addComponentListener(Reha.thisClass);
	int comps = Reha.thisClass.desktops[containerNr].getComponentCount();
	//dta301jry.setLocation(comps*15, comps*15);
	dta301jry.setLocation(200, 200);
	dta301jry.pack();
	dta301jry.setVisible(true);
	Reha.thisClass.desktops[containerNr].add(dta301jry);
	dta301jry.aktiviereDiesenFrame( dta301jry.getName());
	Reha.thisFrame.setCursor(Reha.thisClass.normalCursor);
}

public void loescheDta301(){
	dta301jry = null;
	Reha.thisClass.dta301panel = null;
}

/***********************Barkasse abrechnen*************************/
public void BarkassenFenster(int setPos,String sparam) {
	if(! Reha.DbOk){
		return;
	}
	if(!Rechte.hatRecht(Rechte.Funktion_barkasse, true)){
		return;
	}
	JComponent bk = AktiveFenster.getFensterAlle("Barkasse");
	if(bk != null){
		////System.out.println("InternalFrame Anmeldungen bereits geöffnet");
		containerHandling(((JBarkassenInternal)bk).getDesktop());
		((JBarkassenInternal)bk).aktiviereDiesenFrame( ((JBarkassenInternal)bk).getName());
		if( ((JBarkassenInternal)bk).isIcon() ){
			try {
				((JBarkassenInternal)bk).setIcon(false);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
		return;
	}
	Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
	String name = "Barkasse"+WinNum.NeueNummer();
	////System.out.println("Neues Barkassenfenster = "+name);
	int containerNr = setPos;
	containerHandling(containerNr);
	barkassenjry = new JBarkassenInternal("thera-\u03C0  - Barkasse abrechnen ",SystemConfig.hmSysIcons.get("arztstamm"),1) ;
	AktiveFenster.setNeuesFenster(name,(JComponent)barkassenjry,1,(Container)barkassenjry.getContentPane());
	barkassenjry.setName(name);
	barkassenjry.setSize(new Dimension(500,370));
	barkassenjry.setPreferredSize(new Dimension(500,370));
	Reha.thisClass.barkassenpanel = new Barkasse(barkassenjry); 
	barkassenjry.setContent(Reha.thisClass.barkassenpanel);	
	barkassenjry.addComponentListener(Reha.thisClass);
	int comps = Reha.thisClass.desktops[containerNr].getComponentCount();
	barkassenjry.setLocation(comps*15, comps*15);
	barkassenjry.pack();
	barkassenjry.setVisible(true);
	Reha.thisClass.desktops[containerNr].add(barkassenjry);
	barkassenjry.aktiviereDiesenFrame( barkassenjry.getName());
	Reha.thisFrame.setCursor(Reha.thisClass.normalCursor);
}

public void loescheBarkasse(){
	barkassenjry = null;
	Reha.thisClass.barkassenpanel = null;
}
/*************************Rehaabrechnungen*************************/
public void RehaabrechnungFenster(int setPos,String sparam) {
	if(! Reha.DbOk){
		return;
	}
	if(!Rechte.hatRecht(Rechte.Funktion_rehaabrechnung, true)){
		return;
	}
	JComponent rab = AktiveFenster.getFensterAlle("Rehaabrechnung");
	if(rab != null){
		containerHandling(((JRehaabrechnungInternal)rab).getDesktop());
		((JRehaabrechnungInternal)rab).aktiviereDiesenFrame( ((JRehaabrechnungInternal)rab).getName());
		if( ((JRehaabrechnungInternal)rab).isIcon() ){
			try {
				((JRehaabrechnungInternal)rab).setIcon(false);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
		return;
	}
	Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
	String name = "Rehaabrechnung"+WinNum.NeueNummer();
	int containerNr = setPos;
	containerHandling(containerNr);
	rehaabrechnungjry = new JRehaabrechnungInternal("thera-\u03C0  - ganztägig ambulante Reha abrechnen ",SystemConfig.hmSysIcons.get("arztstamm"),1) ;
	AktiveFenster.setNeuesFenster(name,(JComponent)rehaabrechnungjry,1,(Container)rehaabrechnungjry.getContentPane());
	rehaabrechnungjry.setName(name);
	rehaabrechnungjry.setSize(new Dimension(500,400));
	rehaabrechnungjry.setPreferredSize(new Dimension(500,400));
	Reha.thisClass.rehaabrechnungpanel = new AbrechnungReha(rehaabrechnungjry); 
	rehaabrechnungjry.setContent(Reha.thisClass.rehaabrechnungpanel);	
	rehaabrechnungjry.addComponentListener(Reha.thisClass);
	int comps = Reha.thisClass.desktops[containerNr].getComponentCount();
	rehaabrechnungjry.setLocation(comps*25, comps*25);
	rehaabrechnungjry.pack();
	rehaabrechnungjry.setVisible(true);
	Reha.thisClass.desktops[containerNr].add(rehaabrechnungjry);
	rehaabrechnungjry.aktiviereDiesenFrame( rehaabrechnungjry.getName());
	Reha.thisFrame.setCursor(Reha.thisClass.normalCursor);
}

public void loescheRehaabrechnung(){
	rehaabrechnungjry = null;
	Reha.thisClass.rehaabrechnungpanel = null;
}
/***********************************************************/
public void BeteiligungFenster(int setPos,String sparam) {
	if(! Reha.DbOk){
		return;
	}
	if(!Rechte.hatRecht(Rechte.Funktion_mitarbeiterbeteiligung, true)){
		return;
	}
	JComponent beteiligung = AktiveFenster.getFensterAlle("Beteiligung");
	if(beteiligung != null){
		////System.out.println("InternalFrame Anmeldungen bereits geöffnet");
		containerHandling(((JBeteiligungInternal)beteiligung).getDesktop());
		((JBeteiligungInternal)beteiligung).aktiviereDiesenFrame( ((JBeteiligungInternal)beteiligung).getName());
		if( ((JBeteiligungInternal)beteiligung).isIcon() ){
			try {
				((JBeteiligungInternal)beteiligung).setIcon(false);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
		return;
	}
	Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
	String name = "Beteiligung"+WinNum.NeueNummer();
	int containerNr = setPos;
	containerHandling(containerNr);
	beteiligungjry = new JBeteiligungInternal("thera-\u03C0  - Ermittlung der Umsatzbeteiligungen ",SystemConfig.hmSysIcons.get("arztstamm"),1) ;
	AktiveFenster.setNeuesFenster(name,(JComponent)beteiligungjry,1,(Container)beteiligungjry.getContentPane());
	beteiligungjry.setName(name);
	beteiligungjry.setSize(new Dimension(500,500));
	Reha.thisClass.beteiligungpanel = new Beteiligung(beteiligungjry); 
	beteiligungjry.setContent(Reha.thisClass.beteiligungpanel);	
	beteiligungjry.addComponentListener(Reha.thisClass);
	int comps = Reha.thisClass.desktops[containerNr].getComponentCount();
	beteiligungjry.setLocation(comps*15, comps*15);
	beteiligungjry.pack();
	beteiligungjry.setVisible(true);
	Reha.thisClass.desktops[containerNr].add(beteiligungjry);
	beteiligungjry.aktiviereDiesenFrame( beteiligungjry.getName());
	Reha.thisFrame.setCursor(Reha.thisClass.normalCursor);
}

public void loescheBeteiligung(){
	beteiligungjry = null;
	Reha.thisClass.beteiligungpanel = null;
}
/***************************Verkäufe in der Praxis*********************************/
public void BenutzerrechteFenster(int setPos,String sparam) {
	if(! Reha.DbOk){
		return;
	}
	if(!Rechte.hatRecht(Rechte.BenutzerDialog_open,true)){
		return;
	}
	JComponent benutzer = AktiveFenster.getFensterAlle("Benutzerrechte");
	if(benutzer != null){
		////System.out.println("InternalFrame Anmeldungen bereits geöffnet");
		containerHandling(((JBenutzerInternal)benutzer).getDesktop());
		((JBenutzerInternal)benutzer).aktiviereDiesenFrame( ((JBenutzerInternal)benutzer).getName());
		if( ((JBenutzerInternal)benutzer).isIcon() ){
			try {
				((JBenutzerInternal)benutzer).setIcon(false);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
		return;
	}
	Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
	String name = "Benutzerrechte"+WinNum.NeueNummer();
	int containerNr = setPos;
	containerHandling(containerNr);
	benutzerjry = new JBenutzerInternal("thera-\u03C0  - Benutzer- und Rechteverwaltung ",SystemConfig.hmSysIcons.get("arztstamm"),1) ;
	AktiveFenster.setNeuesFenster(name,(JComponent)benutzerjry,1,(Container)benutzerjry.getContentPane());
	benutzerjry.setName(name);
	benutzerjry.setSize(new Dimension(800,500));
	benutzerjry.setPreferredSize(new Dimension(800,500));
	Reha.thisClass.benutzerrechtepanel = new BenutzerRechte(benutzerjry); 
	benutzerjry.setContent(Reha.thisClass.benutzerrechtepanel);	
	benutzerjry.addComponentListener(Reha.thisClass);
	int comps = Reha.thisClass.desktops[containerNr].getComponentCount();
	benutzerjry.setLocation(comps*15, comps*15);
	benutzerjry.pack();
	benutzerjry.setVisible(true);
	Reha.thisClass.desktops[containerNr].add(benutzerjry);
	benutzerjry.aktiviereDiesenFrame(benutzerjry.getName());
	Reha.thisFrame.setCursor(Reha.thisClass.normalCursor);
}

public void loescheBenutzerrechte(){
	benutzerjry = null;
	Reha.thisClass.benutzerrechtepanel = null;
}

/***********************************************************/
public void UrlaubFenster(int setPos,String sparam) {
	if(! Reha.DbOk){
		return;
	}
	JComponent urlaub = AktiveFenster.getFensterAlle("Urlaub");
	if(urlaub != null){
		////System.out.println("InternalFrame Anmeldungen bereits geöffnet");
		containerHandling(((JUrlaubInternal)urlaub).getDesktop());
		((JUrlaubInternal)urlaub).aktiviereDiesenFrame( ((JUrlaubInternal)urlaub).getName());
		if( ((JUrlaubInternal)urlaub).isIcon() ){
			try {
				((JUrlaubInternal)urlaub).setIcon(false);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
		return;
	}
	Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
	String name = "Urlaub"+WinNum.NeueNummer();
	int containerNr = setPos;
	containerHandling(containerNr);
	urlaubjry = new JUrlaubInternal("thera-\u03C0  - Bearbeitung von Urlaub und Überstunden ",SystemConfig.hmSysIcons.get("arztstamm"),1) ;
	AktiveFenster.setNeuesFenster(name,(JComponent)urlaubjry,1,(Container)urlaubjry.getContentPane());
	urlaubjry.setName(name);
	urlaubjry.setSize(new Dimension(500,500));
	urlaubjry.setPreferredSize(new Dimension(500,500));
	Reha.thisClass.urlaubpanel = new Urlaub(urlaubjry); 
	urlaubjry.setContent(Reha.thisClass.urlaubpanel);	
	urlaubjry.addComponentListener(Reha.thisClass);
	int comps = Reha.thisClass.desktops[containerNr].getComponentCount();
	urlaubjry.setLocation(comps*15, comps*15);
	urlaubjry.pack();
	urlaubjry.setVisible(true);
	Reha.thisClass.desktops[containerNr].add(urlaubjry);
	urlaubjry.aktiviereDiesenFrame( urlaubjry.getName());
	Reha.thisFrame.setCursor(Reha.thisClass.normalCursor);
}

public void loescheUrlaub(){
	urlaubjry = null;
	Reha.thisClass.urlaubpanel = null;
}
/***************************Verkäufe in der Praxis*********************************/

public static void InternalGut2(){
	JInternalFrame iframe = new JInternalFrame();
	iframe.setSize(900,650);
	iframe.setResizable(true);
	iframe.setIconifiable(true);
	iframe.setClosable(true);
	Reha.thisClass.desktops[1].add(iframe);
	OOIFTest oif = new OOIFTest();
	iframe.getContentPane().add(oif);
	iframe.setVisible(true);
	iframe.toFront();
	try {
		iframe.setSelected(true);
	} catch (PropertyVetoException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	
}

/**************Patientenverwaltung Echtfunktion***********************/
public void ProgPatientenVerwaltung(int setPos) {
	if(! Reha.DbOk){
		Reha.thisClass.progressStarten(false);
		return;
	}
	try{
	JComponent patient = AktiveFenster.getFensterAlle("PatientenVerwaltung");
	if(patient != null){
		containerHandling(((JPatientInternal)patient).getDesktop());
		((JPatientInternal)patient).aktiviereDiesenFrame(((JPatientInternal)patient).getName());
		if( ((JPatientInternal)patient).isIcon() ){
			try {
				((JPatientInternal)patient).setIcon(false);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
		Reha.thisClass.progressStarten(false);
		//Reha.thisClass.patpanel.setzeFocus();
		((JPatientInternal)patient).setzeSuche();
		return;
	}
	
	Reha.thisClass.progressStarten(true);
	LinkeTaskPane.thisClass.setCursor(Reha.thisClass.wartenCursor);
	String name = "PatientenVerwaltung"+WinNum.NeueNummer();
	int containerNr = SystemConfig.hmContainer.get("Patient");
	containerHandling(containerNr);
	patjry = new JPatientInternal("thera-\u03C0 Patientenverwaltung "+
			Reha.thisClass.desktops[1].getComponentCount()+1 ,SystemConfig.hmSysIcons.get("patstamm"),containerNr) ;
	AktiveFenster.setNeuesFenster(name,(JComponent)patjry,0,(Container)patjry.getContentPane());
	
	//patjry.setDoNotClose(true);
	
	patjry.setName(name);
	patjry.setSize(new Dimension(900,650));
	patjry.setPreferredSize(new Dimension(900,650));
	
	//Bisheriges Fenster
	//Reha.thisClass.patpanel = new PatGrundPanel(patjry);
	//patjry.setContent(Reha.thisClass.patpanel);
	//Vorschlag
	Reha.thisClass.patpanel = new PatientHauptPanel(name,patjry);
	patjry.setContent(Reha.thisClass.patpanel);
	
	patjry.addComponentListener(Reha.thisClass);
	int comps = Reha.thisClass.desktops[containerNr].getComponentCount();
	patjry.setLocation(comps*10, comps*10);
	patjry.pack();
	patjry.setVisible(true);
	Reha.thisClass.desktops[containerNr].add(patjry);
	((JRehaInternal)patjry).setImmerGross( (SystemConfig.hmContainer.get("PatientOpti") > 0 ? true : false));
	////System.out.println("Anzahl Fenster = "+Reha.thisClass.desktops[containerNr].getComponentCount());
	LinkeTaskPane.thisClass.setCursor(Reha.thisClass.normalCursor);
	((JPatientInternal)patjry).aktiviereDiesenFrame(((JPatientInternal)patjry).getName());
	SwingUtilities.invokeLater(new Runnable(){
	 	   public  void run()
	 	   {
	 		   patjry.setzeSuche();
	 			////System.out.println("Focus auf PatPanel gesetzt");
	 	   }
	});
	Reha.thisClass.progressStarten(false);
	}catch(Exception ex){
		ex.printStackTrace();
	}
	return; 

}
public void loeschePatient(){
	patjry = null;
	Reha.thisClass.patpanel = null;
	//Reha.thisClass.PATINSTANCE = null;
}
/**************Passwortverwaltung Echtfunktion*************************/
public static void PasswortDialog(int setPos) {
	Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);	
	String name = "PasswortDialog"+WinNum.NeueNummer();
	RehaTP jtp = new RehaTP(setPos); 
	jtp.setBorder(null);
	jtp.setTitle("Passwort-Eingabe");
	jtp.setContentContainer(new PassWort());
	jtp.getContentContainer().setName(name);
    jtp.setVisible(true);
	RehaSmartDialog rSmart = new RehaSmartDialog(null,name);

	rSmart.setAlwaysOnTop(false);
	rSmart.setSize(new Dimension(700,300));
	rSmart.setModal(true);

	//rSmart.getTitledPanel().setTitle("Passwort-Eingabe");
	rSmart.setContentPanel(jtp.getContentContainer());
	Toolkit toolkit = Toolkit.getDefaultToolkit();
	Dimension screenSize = toolkit.getScreenSize();
	//Calculate the frame location
	int x = (screenSize.width - rSmart.getWidth()) / 2;
	int y = (screenSize.height - rSmart.getHeight()) / 2;
	rSmart.setLocation(x, y); 
	rSmart.setVisible(true);
	Reha.thisFrame.setCursor(new Cursor((Cursor.DEFAULT_CURSOR)));	
	if(Reha.progRechte.equals("")){
		System.exit(0);
	}
}

/**************System-Initialisierung*********************/
public void SystemInit(int setPos,String sparam) {
	if(! Reha.DbOk){
		return;
	}
	JComponent sysinit = AktiveFenster.getFensterAlle("SystemInit");
	if(sysinit != null){
		////System.out.println("InternalFrame SystemInit bereits geöffnet");
		containerHandling(((JSysteminitInternal)sysinit).getDesktop());
		((JSysteminitInternal)sysinit).aktiviereDiesenFrame( ((JSysteminitInternal)sysinit).getName());
		if( ((JSysteminitInternal)sysinit).isIcon() ){
			try {
				((JSysteminitInternal)sysinit).setIcon(false);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
		return;
	}
	Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
	String name = "SystemInit"+WinNum.NeueNummer();
	int containerNr = setPos;
	containerHandling(containerNr);
	systeminitjry = new JSysteminitInternal("thera-\u03C0  - System-Initialisierung und Einstellungen ",SystemConfig.hmSysIcons.get("arztstamm"),1) ;
	AktiveFenster.setNeuesFenster(name,(JComponent)systeminitjry,1,(Container)systeminitjry.getContentPane());
	systeminitjry.setName(name);
	systeminitjry.setSize(new Dimension(850,620));
	systeminitjry.setPreferredSize(new Dimension(850,620));
	Reha.thisClass.systeminitpanel = new SystemInit(systeminitjry); 
	systeminitjry.setContent(Reha.thisClass.systeminitpanel);	
	systeminitjry.addComponentListener(Reha.thisClass);
	int comps = Reha.thisClass.desktops[containerNr].getComponentCount();
	systeminitjry.setLocation(comps*15, comps*15);
	systeminitjry.pack();
	systeminitjry.setVisible(true);
	Reha.thisClass.desktops[containerNr].add(systeminitjry);
	systeminitjry.aktiviereDiesenFrame( systeminitjry.getName());
	Reha.thisFrame.setCursor(Reha.thisClass.normalCursor);
}

public void loescheSysteminit(){
	systeminitjry = null;
	Reha.thisClass.systeminitpanel = null;
}

/*********************************************************************************/
/*
public static void SystemInitialisierung(){
    SwingUtilities.invokeLater(new Runnable(){
  	   public  void run()
  	   {	
  		 SystemUtil sysUtil = new SystemUtil(null);
  		   //SystemUtil sysUtil = new SystemUtil(Reha.thisFrame);
  			sysUtil.setSize(850,620);
  			//roogle.setLocationRelativeTo(null);
  			sysUtil.setLocationRelativeTo(null);
  			sysUtil.setVisible(true);
  	   }
 	});
}
*/

public static void containerBelegen(int setPos,RehaTP jtp){
	if (setPos==1){
		if(Reha.thisClass.jLeerOben != null){
			Reha.thisClass.jLeerOben.setVisible(false);
			Reha.thisClass.jInhaltOben = jtp;
			Reha.thisClass.jContainerOben.add(Reha.thisClass.jInhaltOben,BorderLayout.CENTER);
			Reha.thisClass.jContainerOben.validate();
			Reha.thisClass.jContainerOben.remove(Reha.thisClass.jLeerOben);
			Reha.thisClass.jLeerOben = null;
		}else{
			RehaSmartDialog rsm = new RehaSmartDialog(Reha.thisFrame,jtp.getContentContainer().getName());
			PinPanel pinPanel = new PinPanel();
			pinPanel.setName(jtp.getName());
			rsm.setPinPanel(pinPanel);
			rsm.setName(jtp.getName());
			rsm.setLocation(300,300);
			rsm.setContentPanel(jtp.getContentContainer());
			rsm.setVisible(true);
		}
		
	}else if(setPos==2){
		if(Reha.thisClass.jLeerUnten != null){
			Reha.thisClass.jLeerUnten.setVisible(false);
			Reha.thisClass.jInhaltUnten = jtp;
			Reha.thisClass.jContainerUnten.add(Reha.thisClass.jInhaltUnten,BorderLayout.CENTER);
			Reha.thisClass.jContainerUnten.validate();
			Reha.thisClass.jContainerUnten.remove(Reha.thisClass.jLeerUnten);
			Reha.thisClass.jLeerUnten = null;
		}else{
			RehaSmartDialog rsm = new RehaSmartDialog(Reha.thisFrame,jtp.getContentContainer().getName());
			PinPanel pinPanel = new PinPanel();
			pinPanel.setName(jtp.getName());
			rsm.setPinPanel(pinPanel);
			rsm.setName(jtp.getName());
			rsm.setLocation(300,300);
			rsm.setContentPanel(jtp.getContentContainer());
			rsm.setVisible(true);
			}	
	}else if(setPos==0){
		RehaSmartDialog rsm = new RehaSmartDialog(Reha.thisFrame,jtp.getContentContainer().getName());
		PinPanel pinPanel = new PinPanel();
		pinPanel.setName(jtp.getName());
		////System.out.println("jtp.getName() = "+jtp.getName());
		jtp.setStandort(jtp.getName(),0);
		rsm.setPinPanel(pinPanel);
		rsm.setName(jtp.getName());
		rsm.setLocationRelativeTo(null);
		rsm.setContentPanel(jtp.getContentContainer());
		rsm.setVisible(true);
	}


}

public void RehaTPEventOccurred(RehaTPEvent evt) {
	// TODO Auto-generated method stub
	////System.out.println("ProgLoader Systemausl�ser"+evt.getSource());
	////System.out.println("ProgLoader Event getDetails[0]: = "+evt.getDetails()[0]);
	////System.out.println("ProgLoader Event getDetails[1]: = "+evt.getDetails()[1]);
	////System.out.println(((JXTitledPanel) evt.getSource()).getContentContainer().getName());
}

public static int PosTest(int pos){
	if((pos==1) && (Reha.thisClass.jLeerOben == null) ){
		return 0;
	}
	if((pos==2) && (Reha.thisClass.jLeerUnten == null) ){
		////System.out.println("pos = "+pos);
		return 0;
	}
	return pos;
}
public static void containerHandling(int cont){
	if(Reha.thisClass.vollsichtbar == -1){
		return;
	}
	if((Reha.thisClass.vollsichtbar == 1 && cont == 1) || (Reha.thisClass.vollsichtbar == 0 && cont == 0) ){
		return;
	}
	if(Reha.thisClass.vollsichtbar == 0 && cont == 1){
		Reha.thisClass.setDivider(6);
		return;
	}
	if(Reha.thisClass.vollsichtbar == 1 && cont == 0){
		Reha.thisClass.setDivider(5);
		return;
	}
	
}
}
