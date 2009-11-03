package hauptFenster;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.beans.PropertyVetoException;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import krankenKasse.KassenPanel;
import menus.OOIFTest;


import openOfficeorg.RehaDocumentCloseListener;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;

import patientenFenster.PatGrundPanel;
import patientenFenster.PatientenFenster;

import RehaInternalFrame.JArztInternal;
import RehaInternalFrame.JGutachtenInternal;
import RehaInternalFrame.JKasseInternal;
import RehaInternalFrame.JPatientInternal;
import RehaInternalFrame.JRehaInternal;
import RehaInternalFrame.JTerminInternal;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.desktop.DesktopException;
import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.text.IParagraph;
import ag.ion.bion.officelayer.text.ITextCursor;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;

import ag.ion.bion.officelayer.event.ICloseEvent;
import ag.ion.bion.officelayer.event.ICloseListener;
import ag.ion.bion.officelayer.event.IEvent;
import arztFenster.ArztPanel;
import benutzerVerwaltung.BenutzerVerwaltung;

import com.sun.star.view.DocumentZoomType;

import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import entlassBerichte.EBerichtPanel;
import events.PatStammEvent;
import events.PatStammEventClass;
import events.RehaEvent;
import events.RehaEventClass;
import events.RehaEventListener;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;
import generalSplash.RehaSplash;

import rehaContainer.RehaTP;
import roogle.RoogleFenster;
import systemEinstellungen.SystemConfig;
import systemEinstellungen.SystemUtil;
import systemTools.PassWort;
import systemTools.SplashPanel;
import systemTools.WinNum;
import terminKalender.ParameterLaden;
import terminKalender.TerminFenster;
import terminKalender.DatFunk;

public class ProgLoader {
public JPatientInternal patjry = null; 
public JGutachtenInternal gutjry = null;
public RoogleFenster roogleDlg = null;
public JArztInternal arztjry = null;
public JKasseInternal kassejry = null;
public JTerminInternal terminjry = null;

//public static JTerminInternal tjry = null;
//public static JGutachtenInternal gjry = null;

	public ProgLoader(){
		
	}

protected static RehaSmartDialog xsmart;

/**************Test Panel (Test für BackGroundPainter****************/	
public static void ProgTestPanel(int setPos){
	RehaTP jtp = new RehaTP(setPos); 
	jtp.setTitle("Test-Inhalt");
	jtp.setBorder(null);
	jtp.setName("TestInhaltTP");	
	jtp.setRightDecoration((JComponent) new PinPanel());
	jtp.setContentContainer(new TestPanel(setPos));
	containerBelegen(setPos,jtp);
}
/**************Patient suchen (Test)**********************************/
public static void ProgPatSuche(boolean setPos){
	
}
public void XProgTerminFenster(int setPos,int ansicht) {
	/*
	String name = "TerminFenster"+WinNum.NeueNummer();
	JInternalFrame iframe = new JInternalFrame("",true,true,true,true);
	iframe.setName(name);
	Reha.thisClass.terminpanel = new TerminFenster();
	int containerNr = SystemConfig.hmContainer.get("Kalender");
	iframe.setContentPane(Reha.thisClass.terminpanel.init(containerNr, ansicht,iframe));
	Reha.thisClass.desktops[containerNr].add(iframe);
	iframe.setLocation(new Point(5,5));
	iframe.setSize(new Dimension(Reha.thisClass.jpOben.getWidth(),Reha.thisClass.jpOben.getHeight()));
	iframe.setVisible(true);
*/
}
/**************Terminkalender Echtfunktion****************************/
public void ProgTerminFenster(int setPos,int ansicht) {
	if(! Reha.thisClass.DbOk){
		return;
	}
	JComponent termin = AktiveFenster.getFensterAlle("TerminFenster");
	if(termin != null){
		if(ansicht==2){
			JOptionPane.showMessageDialog(null,"Um die Wochenarbeitszeit zu starten,\nschließen Sie bitte zunächst den Terminkalender");
		}
		System.out.println("Der Terminkalender befindet sich in Container "+((JTerminInternal)termin).getDesktop());
		//((JTerminInternal)termin).toFront();
		containerHandling(((JTerminInternal)termin).getDesktop());
		((JTerminInternal)termin).aktiviereDiesenFrame(((JTerminInternal)termin).getName());
		if( ((JTerminInternal)termin).isIcon() ){
			try {
				((JTerminInternal)termin).setIcon(false);
			} catch (PropertyVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return;
	}
	final int xsetPos=setPos,xansicht=ansicht;
	SwingUtilities.invokeLater(new Runnable(){
		public  void run(){
			String name = "TerminFenster"+WinNum.NeueNummer();
 
			int containerNr = SystemConfig.hmContainer.get("Kalender");
			System.out.println("Terminkalender starten in Container "+containerNr);
			containerHandling(containerNr);
			LinkeTaskPane.thisClass.setCursor(new Cursor(Cursor.WAIT_CURSOR));
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
			//TerminFenster termWin = new TerminFenster();
			terminjry.setContent(Reha.thisClass.terminpanel.init(containerNr, xansicht,terminjry));
			terminjry.setLocation(new Point(5,5));
			terminjry.setSize(new Dimension(Reha.thisClass.jpOben.getWidth(),Reha.thisClass.jpOben.getHeight()));
			terminjry.setVisible(true);
			Reha.thisClass.desktops[containerNr].add(terminjry);
			LinkeTaskPane.thisClass.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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

/**************OpenOffice Echtfunktion*******************************/


/**************Benutzerverwaltung Echtfunktion***********************/
public static void ProgBenutzerVerwaltung(int setPos) {
	final int xsetPos = setPos;
    SwingUtilities.invokeLater(new Runnable(){
 	   @SuppressWarnings("deprecation")
	public  void run()
 	   {	
 		   Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
 		   String name = "Benutzerverwaltung"+WinNum.NeueNummer();
 		   RehaTP jtp = new RehaTP(xsetPos); 
 		   jtp.setBorder(null);
 		   PinPanel pinPanel = new PinPanel();
 		   jtp.setPinPanel(pinPanel);
 		   jtp.setRightDecoration((JComponent) pinPanel );
 		   jtp.setTitle("Benutzer-Verwaltung");
 		   //String name = "Benutzerverwaltung"+WinNum.NeueNummer();
 		   jtp.setContentContainer(new BenutzerVerwaltung(xsetPos));
 		   jtp.getContentContainer().setName(name);
 		   jtp.setzeName(name);
 		   //	pinPanel.setName(name);
 		   jtp.setVisible(true);
 		   AktiveFenster.setNeuesFenster(name, jtp,PosTest(xsetPos),jtp.getParent());
 		   containerBelegen(xsetPos,jtp);
 		   jtp.validate();
 		   Reha.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
 	   }
	});

}
/**************Roogle Echtfunktion***********************/
public void ProgRoogleFenster(int setPos,String droptext) {
	final String xdroptext = droptext;
	
	new Thread(){
		public void run(){
			
 		   	Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
 			roogleDlg = new RoogleFenster(Reha.thisFrame,xdroptext);
 			roogleDlg.setSize(940,680);
 			roogleDlg.setPreferredSize(new Dimension(940,680));
 			roogleDlg.setLocationRelativeTo(null);
 			roogleDlg.pack();
 			roogleDlg.setVisible(true);
 			Reha.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}.start();
}
public void loescheRoogle(){
	roogleDlg = null;
}


/**************Krankenkassenverwaltung Echtfunktion***********************/
public void KassenFenster(int setPos,String kid) {
	if(! Reha.thisClass.DbOk){
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
	Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
	String name = "KrankenKasse"+WinNum.NeueNummer();
	int containerNr = SystemConfig.hmContainer.get("Kasse");
	containerHandling(containerNr);
	kassejry = new JKasseInternal("thera-\u03C0 Krankenkassen-Verwaltung ",SystemConfig.hmSysIcons.get("kassenstamm"),containerNr) ;
	AktiveFenster.setNeuesFenster(name,(JComponent)kassejry,containerNr,(Container)kassejry.getContentPane());
	kassejry.setName(name);
	kassejry.setSize(new Dimension(650,500));
	kassejry.setPreferredSize(new Dimension(650,500));
	//KassenPanel kasspan = new KassenPanel(jry,kid);
	Reha.thisClass.kassenpanel = new KassenPanel(kassejry,kid); 
	kassejry.setContent(Reha.thisClass.kassenpanel);	
	kassejry.addComponentListener(Reha.thisClass);
	int comps = Reha.thisClass.desktops[containerNr].getComponentCount();
	kassejry.setLocation(comps*10, comps*10);
	kassejry.pack();
	kassejry.setVisible(true);
	Reha.thisClass.desktops[containerNr].add(kassejry);
	((JRehaInternal)kassejry).setImmerGross( (SystemConfig.hmContainer.get("KasseOpti") > 0 ? true : false));
	System.out.println("Anzahl Fenster = "+Reha.thisClass.desktops[containerNr].getComponentCount());
	((JKasseInternal)kassejry).aktiviereDiesenFrame( ((JKasseInternal)kassejry).getName());
	Reha.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	
}
public void loescheKasse(){
	kassejry = null;
	Reha.thisClass.kassenpanel = null;
}

/**************^Ärzteverwaltung Echtfunktion***********************/
public void ArztFenster(int setPos,String aid) {
	if(! Reha.thisClass.DbOk){
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
	Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
	String name = "ArztVerwaltung"+WinNum.NeueNummer();
	int containerNr = SystemConfig.hmContainer.get("Arzt");
	containerHandling(containerNr);
	arztjry = new JArztInternal("thera-\u03C0 Ärzte-Verwaltung ",SystemConfig.hmSysIcons.get("arztstamm"),containerNr) ;
	AktiveFenster.setNeuesFenster(name,(JComponent)arztjry,containerNr,(Container)arztjry.getContentPane());
	arztjry.setName(name);
	arztjry.setSize(new Dimension(650,500));
	//ArztPanel arztpan = new ArztPanel(jry,aid);
	Reha.thisClass.arztpanel = new ArztPanel(arztjry,aid); 
	arztjry.setContent(Reha.thisClass.arztpanel);	
	arztjry.addComponentListener(Reha.thisClass);
	int comps = Reha.thisClass.desktops[containerNr].getComponentCount();
	arztjry.setLocation(comps*10, comps*10);
	arztjry.setVisible(true);
	Reha.thisClass.desktops[containerNr].add(arztjry);
	((JRehaInternal)arztjry).setImmerGross( (SystemConfig.hmContainer.get("ArztOpti") > 0 ? true : false));
	System.out.println("Anzahl Fenster = "+Reha.thisClass.desktops[containerNr].getComponentCount());
	((JArztInternal)arztjry).aktiviereDiesenFrame( ((JArztInternal)arztjry).getName());
	Reha.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	
}
public void loescheArzt(){
	arztjry = null;
	Reha.thisClass.arztpanel = null;
}


/**************Gutachten Echtfunktion***********************/
public void GutachenFenster(int setPos,String pat_intern,int berichtid,String berichttyp,boolean neu,String empfaenger ) {
	if(! Reha.thisClass.DbOk){
		return;
	}
	JComponent gutachten = AktiveFenster.getFensterAlle("GutachtenFenster");
	if(gutachten != null){
		System.out.println("Gutachten ist nicht null");
		containerHandling(((JGutachtenInternal)gutachten).getDesktop());
		((JGutachtenInternal)gutachten).aktiviereDiesenFrame( ((JGutachtenInternal)gutachten).getName());

		if( ((JGutachtenInternal)gutachten).isIcon() ){
			try {
				((JGutachtenInternal)gutachten).setIcon(false);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
		//((JGutachtenInternal)gutachten).starteArztID(pat_intern);
		return;
	}
	Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
	String name = "GutachtenFenster"+WinNum.NeueNummer();
	int containerNr = SystemConfig.hmContainer.get("Arzt");
	containerHandling(containerNr);
	gutjry = new JGutachtenInternal("thera-\u03C0 Gutachten ",SystemConfig.hmSysIcons.get("drvlogo"),containerNr) ;
	AktiveFenster.setNeuesFenster(name,(JComponent)gutjry,containerNr,(Container)gutjry.getContentPane());
	gutjry.setName(name);
	gutjry.setSize(new Dimension(900,650));
	//EBerichtPanel ebericht = new EBerichtPanel(jry,pat_intern,berichtid,berichttyp,neu,empfaenger );
	Reha.thisClass.eberichtpanel = new EBerichtPanel(gutjry,pat_intern,berichtid,berichttyp,neu,empfaenger ); 
	gutjry.setContent(Reha.thisClass.eberichtpanel);
	//jry.setContent((EBerichtPanel)ebericht);	
	gutjry.addComponentListener(Reha.thisClass);
	int comps = Reha.thisClass.desktops[containerNr].getComponentCount();
	gutjry.setLocation(comps*10, comps*10);
	gutjry.setVisible(true);
	Reha.thisClass.desktops[containerNr].add(gutjry);
	//jry.setImmerGross(true); 
	//((JRehaInternal)jry).setImmerGross( (SystemConfig.hmContainer.get("ArztOpti") > 0 ? true : false));
	System.out.println("Anzahl Fenster = "+Reha.thisClass.desktops[containerNr].getComponentCount());
	((JGutachtenInternal)gutjry).aktiviereDiesenFrame( ((JGutachtenInternal)gutjry).getName());
	Reha.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
  	int icomp = SwingUtilities.getAccessibleChildrenCount(gutjry.thisContent);
  	System.out.println("Es gibt AccessibleChldren : "+icomp);
}
public void loescheGutachten(){
	gutjry = null;
	Reha.thisClass.eberichtpanel = null;
}


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

/**************Pateintenverwaltung Echtfunktion***********************/
public void ProgPatientenVerwaltung(int setPos) {
	if(! Reha.thisClass.DbOk){
		Reha.thisClass.progressStarten(false);
		return;
	}
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
		Reha.thisClass.patpanel.setzeFocus();
		((JPatientInternal)patient).setzeSuche();
		return;
	}
	
	Reha.thisClass.progressStarten(true);
	LinkeTaskPane.thisClass.setCursor(new Cursor(Cursor.WAIT_CURSOR));
	String name = "PatientenVerwaltung"+WinNum.NeueNummer();
	int containerNr = SystemConfig.hmContainer.get("Patient");
	containerHandling(containerNr);
	patjry = new JPatientInternal("thera-\u03C0 Patientenverwaltung "+
			Reha.thisClass.desktops[1].getComponentCount()+1 ,SystemConfig.hmSysIcons.get("patstamm"),containerNr) ;
	AktiveFenster.setNeuesFenster(name,(JComponent)patjry,0,(Container)patjry.getContentPane());
	patjry.setName(name);
	patjry.setSize(new Dimension(900,650));
	//PatGrundPanel patpan = new PatGrundPanel(jry);
	//patjry.setContent(new PatGrundPanel(patjry));

	Reha.thisClass.patpanel = new PatGrundPanel(patjry);
	patjry.setContent(Reha.thisClass.patpanel);

	patjry.addComponentListener(Reha.thisClass);
	int comps = Reha.thisClass.desktops[containerNr].getComponentCount();
	patjry.setLocation(comps*10, comps*10);
	patjry.setVisible(true);
	Reha.thisClass.desktops[containerNr].add(patjry);
	((JRehaInternal)patjry).setImmerGross( (SystemConfig.hmContainer.get("PatientOpti") > 0 ? true : false));
	System.out.println("Anzahl Fenster = "+Reha.thisClass.desktops[containerNr].getComponentCount());
	LinkeTaskPane.thisClass.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	((JPatientInternal)patjry).aktiviereDiesenFrame(((JPatientInternal)patjry).getName());
	SwingUtilities.invokeLater(new Runnable(){
	 	   public  void run()
	 	   {
	 			patjry.setzeSuche();
	 			Reha.thisClass.progressStarten(false);
	 			System.out.println("Focus auf PatPanel gesetzt");
	 	   }
	});
	return; 

}
public void loeschePatient(){
	patjry = null;
	Reha.thisClass.patpanel = null;
	//Reha.thisClass.PATINSTANCE = null;
}
/**************Passwortverwaltung Echtfunktion*************************/
public static void PasswortDialog(int setPos) {
 
	Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));	
	String name = "PasswortDialog"+WinNum.NeueNummer();
	RehaTP jtp = new RehaTP(setPos); 
	jtp.setBorder(null);
	jtp.setTitle("Passwort-Eingabe");
	jtp.setContentContainer(new PassWort());
	jtp.getContentContainer().setName(name);
    jtp.setVisible(true);
	RehaSmartDialog rSmart = new RehaSmartDialog(null,name);
	rSmart.setModal(true);
	rSmart.setSize(new Dimension(700,300));
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
}
/**************SplashPanel Echtfunktion*************************/
public static RehaSmartDialog SplashPanelDialog(int setPos) {
	final int xsetPos = setPos;

    SwingUtilities.invokeLater(new Runnable(){
 	   public  void run()
 	   {

 		   RehaTP jtp = new RehaTP(xsetPos); 
 		   String name = "SplashPanel"+WinNum.NeueNummer();
 		   jtp.setBorder(null);
 		   jtp.setTitle("Passwort-Eingabe");
 		   jtp.setzeName(name);
 		   jtp.setContentContainer(new SplashPanel());
 		   jtp.getContentContainer().setName(name);
 		   jtp.setVisible(true);


 		   RehaSmartDialog rSmart = new RehaSmartDialog(null,name);
 		   rSmart.setModal(true);
 		   rSmart.setSize(new Dimension(700,300));
 		   //	rSmart.getTitledPanel().setTitle("Passwort-Eingabe");
 		   rSmart.setContentPanel(jtp.getContentContainer());
 		   Toolkit toolkit = Toolkit.getDefaultToolkit();
 		   Dimension screenSize = toolkit.getScreenSize();
 		   //	Calculate the frame location
 		   int x = (screenSize.width - rSmart.getWidth()) / 2;
 		   int y = (screenSize.height - rSmart.getHeight()) / 2;
 		   rSmart.setLocation(x, y); 
 		   rSmart.setVisible(true);
 		   ProgLoader.xsmart = rSmart;	
 	   }
 	});
 		   
    return ProgLoader.xsmart;
}


/**************RTA-Wissen Echtfunktion*************************/



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
		System.out.println("jtp.getName() = "+jtp.getName());
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
	System.out.println("ProgLoader Systemauslöser"+evt.getSource());
	System.out.println("ProgLoader Event getDetails[0]: = "+evt.getDetails()[0]);
	System.out.println("ProgLoader Event getDetails[1]: = "+evt.getDetails()[1]);
	System.out.println(((JXTitledPanel) evt.getSource()).getContentContainer().getName());
}

public static int PosTest(int pos){
	if((pos==1) && (Reha.thisClass.jLeerOben == null) ){
		return 0;
	}
	if((pos==2) && (Reha.thisClass.jLeerUnten == null) ){
		System.out.println("pos = "+pos);
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
