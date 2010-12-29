package systemEinstellungen;

import hauptFenster.Reha;
import hauptFenster.UIFSplitPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXPanel;

import rechteTools.Rechte;
import rehaInternalFrame.JSysteminitInternal;


public class SystemInit extends JXPanel implements TreeSelectionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4349616039904531899L;
	private JXHeader header = null;  
	private UIFSplitPane jSplitLR = null;
	private JXPanel jxLinks = null;
	private JXPanel jxRechts = null;
	private JXPanel jxInhaltRechts = null;

	public JScrollPane parameterScroll = null;
	
	private HashMap<String,String> htitel = new HashMap<String,String>();
	private HashMap<String,String> hdescription = new HashMap<String,String>();
	private HashMap<String,ImageIcon> hicon = new HashMap<String,ImageIcon>();
	public DefaultMutableTreeNode root;
	public DefaultTreeModel treeModel;
	public JTree tree;


	
	public SystemInit(JSysteminitInternal sai){
		super();
		setLayout(new BorderLayout());
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("SystemInit"));
		
        String ss = Reha.proghome+"icons/header-image.png"; //"icons/header-image.png";
        header = new JXHeader("Mit der Systeminitialisierung....",
                "....erstellen bzw. ändern Sie die Systemeinstellungen auf Ihre inividuelle Bedürfnisse hin.\n" +
                "Von den Behandlersets des Terminkalender, Aktualisierungszyklen bis hin zu Pfadangaben und Emailparameter.\n" +
                "Systemparameter die ein gewisses Maß an Computer-Kenntnisse voraussetzen haben wir mit einem roten Ausrufezeichen versehen.\n\n"+
                "Sie schließen dieses Fenster über den roten Punkt rechts oben, oder mit der Taste >>ESC<<.",
                new ImageIcon(ss));
        header.setPreferredSize(new Dimension(0,150));
        header.setBackground(Color.WHITE);
        ((JLabel)header.getComponent(1)).setVerticalAlignment(JLabel.NORTH);
        	add(header,BorderLayout.NORTH);
        	jxLinks = new JXPanel(new BorderLayout());
        	jxLinks.setBackground(Color.WHITE);
        	JXPanel dummy = new JXPanel();
        	dummy.setBorder(null);
        	dummy.setBackground(Color.WHITE);
        	dummy.setPreferredSize(new Dimension(10,20));
        	jxLinks.add(dummy,BorderLayout.NORTH);
        	dummy = new JXPanel();
        	dummy.setBorder(null);
        	dummy.setBackground(Color.WHITE);
        	dummy.setPreferredSize(new Dimension(10,20));
        	jxLinks.add(dummy,BorderLayout.SOUTH);
        	dummy = new JXPanel();
        	dummy.setBorder(null);
        	dummy.setBackground(Color.WHITE);
        	dummy.setPreferredSize(new Dimension(10,20));
        	jxLinks.add(dummy,BorderLayout.WEST);
        	dummy = new JXPanel();
        	dummy.setBorder(null);
        	dummy.setBackground(Color.WHITE);
        	dummy.setPreferredSize(new Dimension(10,20));
        	jxLinks.add(dummy,BorderLayout.EAST);

        	jxLinks.add(getParameterListe(),BorderLayout.CENTER);
        	// hier muß das add für die weitern Panels rein
        	jxRechts = new JXPanel(new BorderLayout());
    		/****/
    
   	     	jxRechts.setBackgroundPainter(Reha.thisClass.compoundPainter.get("SystemInit"));
   	     	/****/
        	// hier muß das add für die weitern Panels rein
        	jSplitLR =  UIFSplitPane.createStrippedSplitPane(JSplitPane.HORIZONTAL_SPLIT,
        		jxLinks,
        		jxRechts); 
        	jSplitLR.setOpaque(false);
			//jSplitLR.setBackground(Color.WHITE);
			jSplitLR.setDividerSize(7);
			//jSplitLR.setDividerBorderVisible(false);
			jSplitLR.setDividerBorderVisible(true);
			jSplitLR.setName("GrundSplitLinksRechts");
			jSplitLR.setOneTouchExpandable(true);
			jSplitLR.setDividerLocation(260);

			((BasicSplitPaneUI) jSplitLR.getUI()).getDivider().setBackground(Color.WHITE);
			add(jSplitLR,BorderLayout.CENTER);
			revalidate();
			auswertenSysUtil("nix");
			SwingUtilities.invokeLater(new Runnable(){
				public  void run(){
					new Thread(){
						public void run(){
							headerInfos();							
						}
					}.start();

		 	  	}
			});			
			
			
	}		    
/*******************************************************/			
/*********************************************************/
	
public void FensterSchliessen(String welches){
	////System.out.println("Eltern-->"+this.getParent().getParent().getParent().getParent().getParent());
	//webBrowser.dispose();
	//super.dispose();
	//this.dispose();
}
/*
private static JXPanel titlePanel(){
	SystemUtil.thisClass.jp = new RehaTP(0);
	SystemUtil.thisClass.jp.getContentContainer().setLayout(new BorderLayout());
	SystemUtil.thisClass.jtp = (JXPanel) SystemUtil.thisClass.jp.getContentContainer();
	SystemUtil.thisClass.jtp.setSize(new Dimension(200,200));
	SystemUtil.thisClass.jtp.setName(SystemUtil.thisClass.dieserName);
	SystemUtil.thisClass.jtp.setVisible(true);
	return SystemUtil.thisClass.jtp;
}
*/

public String dieserName(){
	return this.getName();
}


public void actionPerformed(ActionEvent arg0) {
	String cmd = arg0.getActionCommand();
	for(int i = 0; i< 1;i++){
		if(cmd.equals("Heute")){
			break;
		}
	}	
}

private JScrollPane getParameterListe(){
	DefaultMutableTreeNode treeitem = null;
	parameterScroll = new JScrollPane();
	parameterScroll.setBorder(null);
	parameterScroll.setBackground(Color.WHITE);
	
	root = new DefaultMutableTreeNode( "System Reha-Verwaltung" );
	treeModel = new DefaultTreeModel(root);


	DefaultMutableTreeNode node = new DefaultMutableTreeNode( "Mandant(en) / Datenbank");
	treeitem = new DefaultMutableTreeNode("Firmenangaben Mandanten");
	node.add(treeitem ); 
	treeitem = new DefaultMutableTreeNode("Datenbankparameter");
	node.add(treeitem );
	root.add(node);
	/***/
	node = new DefaultMutableTreeNode( "Terminkalender"); 
	treeitem = new DefaultMutableTreeNode("Terminkal. Grundeinstellungen");
	node.add(treeitem ); 
	treeitem = new DefaultMutableTreeNode("Kalenderbenutzer verwalten");
	node.add(treeitem ); 
	treeitem = new DefaultMutableTreeNode("Behandlersets definieren");
	node.add(treeitem );
	treeitem = new DefaultMutableTreeNode("Startoptionen definieren");
	node.add(treeitem );
	treeitem = new DefaultMutableTreeNode("Druckvorlage Terminliste");
	node.add(treeitem );
	//treeitem = new DefaultMutableTreeNode("Termin-Drucker verwalten");
	//node.add(treeitem );	
	treeitem = new DefaultMutableTreeNode("Kalenderfarben definieren");
	node.add(treeitem ); 
	treeitem = new DefaultMutableTreeNode("Gruppentermine definieren");
	node.add(treeitem );	
	treeitem = new DefaultMutableTreeNode("Neues Kalenderjahr anlegen");
	node.add(treeitem );	
	//treeitem = new DefaultMutableTreeNode("Feiertage eintragen");
	//node.add(treeitem );	
	root.add(node);
	/***/	
	node = new DefaultMutableTreeNode( "[Ru:gl]");
	treeitem = new DefaultMutableTreeNode("Ru:gl-Grundeinstellungen");
	node.add(treeitem);
	treeitem = new DefaultMutableTreeNode("Ru:gl-Gruppen definieren");
	node.add(treeitem);
	root.add(node);
	/***/
	/***/	
	node = new DefaultMutableTreeNode( "Stammdaten Optionen");
	treeitem = new DefaultMutableTreeNode("Patient");
	node.add(treeitem);
	treeitem = new DefaultMutableTreeNode("Rezepte");
	node.add(treeitem);
	treeitem = new DefaultMutableTreeNode("Krankenkasse");
	node.add(treeitem);
	treeitem = new DefaultMutableTreeNode("Arzt");
	node.add(treeitem);
	root.add(node);
	/***/
	node = new DefaultMutableTreeNode( "Geräte/Anschlüsse"); 
	treeitem = new DefaultMutableTreeNode("Anschlüsse");
	node.add(treeitem ); 
	treeitem = new DefaultMutableTreeNode("angeschlossene Geraete");
	node.add(treeitem ); 
	root.add(node);
	/***/
	node = new DefaultMutableTreeNode( "Preislisten");
	/*
	treeitem = new DefaultMutableTreeNode("Heilmittelkatalog einlesen");
	node.add(treeitem );
	*/ 
	treeitem = new DefaultMutableTreeNode("Positionskürzel anlegen");
	node.add(treeitem );
	treeitem = new DefaultMutableTreeNode("Tarifgruppen bearbeiten");
	node.add(treeitem );
	treeitem = new DefaultMutableTreeNode("Preise bearbeiten/importieren");
	node.add(treeitem );
	root.add(node);
	/***/
	node = new DefaultMutableTreeNode( "Abrechnung und §302");
	treeitem = new DefaultMutableTreeNode("Nebraska / Zertifikatshandling");
	node.add(treeitem ); 
	treeitem = new DefaultMutableTreeNode("Abrechnungsformulare und Drucker");
	node.add(treeitem ); 
	/*
	treeitem = new DefaultMutableTreeNode("Annahmekey abholen/einlesen");
	node.add(treeitem );
	*/ 
	treeitem = new DefaultMutableTreeNode("Kostenträgerdatei einlesen");
	node.add(treeitem ); 

	// Lemmi 20101228: OP & Mahnungen eingebaut
	treeitem = new DefaultMutableTreeNode( "Offene Posten & Mahnung");
	node.add(treeitem);
	root.add(node);
	/***/
	node = new DefaultMutableTreeNode( "sonstige Einstellungen");
	treeitem = new DefaultMutableTreeNode( "Emailparameter");
	node.add(treeitem);
	//root.add(node);
	/***/
	treeitem = new DefaultMutableTreeNode( "Fremdprogramme");
	node.add(treeitem);
	//root.add(node);
	/***/
	treeitem = new DefaultMutableTreeNode( "Fortlaufender Nummernkreis");
	node.add(treeitem);
	//
	treeitem = new DefaultMutableTreeNode( "Befreiungen zurücksetzen/Jahreswechsel");
	node.add(treeitem);
	//root.add(node);
	/***/
	// Lemmi 20101225: Bedienung eingebaut
	treeitem = new DefaultMutableTreeNode( "Bedienung");
	node.add(treeitem);
	root.add(node);
	
	/***/
	node = new DefaultMutableTreeNode( "Software-Updateservice");
	root.add(node);
	



	tree = new JTree( root );
	tree.getSelectionModel().addTreeSelectionListener(this); 


	parameterScroll.setViewportView(tree);
	return parameterScroll; 
}
/*******************************************************************/
private void auswertenSysUtil(String util){
	//System.out.println("SysUtil = "+util);
	for(int i = 0; i<1; i++){
		if(jxInhaltRechts!=null){
			cursorWait(true);
			//jxInhaltRechts.setVisible(false);
			jxRechts.remove(jxInhaltRechts);
			jxInhaltRechts.setVisible(false);
			jxInhaltRechts = null;
		}else{
			cursorWait(true);
		}
		if(util.equals("Behandlersets definieren")){
			if(!Rechte.hatRecht(Rechte.Systeminit_kalenderbenutzersets, false)){
				doAccessDenied();
				return;
			}
			jxInhaltRechts = new SysUtilBehandlerset();
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);			
			setHeader("Behandlersets");
			break;
		}
		if(util.equals("Kalenderbenutzer verwalten")){
			//headerInfos(0);
			//SwingUtilities.invokeLater(new Runnable(){
				//public  void run(){
			if(!Rechte.hatRecht(Rechte.Systeminit_kalenderbenutzer, false)){
				doAccessDenied();
				return;
			}
			jxInhaltRechts = new SysUtilKalenderBenutzer();
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);
			setHeader("Kalenderbenutzer");
			//}
			//});			
			break;
		}
		if(util.equals("Ru:gl-Gruppen definieren")){
			//headerInfos(0);
			if(!Rechte.hatRecht(Rechte.Systeminit_ruglgruppen, false)){
				doAccessDenied();
				return;
			}
			jxInhaltRechts = new SysUtilRoogleGruppen();
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);
			setHeader("RoogleGrundeinstellungen");
			break;
		}
		if(util.equals("Neues Kalenderjahr anlegen")){
			//headerInfos(0);
			if(!Rechte.hatRecht(Rechte.Systeminit_kalenderjahranlegen, false)){
				doAccessDenied();
				return;
			}
			jxInhaltRechts = new SysUtilKalenderanlegen();
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);
			setHeader("Kalenderjahranlegen");
			break;
		}
		if(util.equals("Kalenderfarben definieren")){
			//headerInfos(0);
			if(!Rechte.hatRecht(Rechte.Systeminit_kalenderfarbsets, false)){
				doAccessDenied();
				return;
			}
			jxInhaltRechts = new SysUtilKalenderfarben();
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);
			setHeader("Kalenderfarben");
			break;
		}
		if(util.equals("Terminkal. Grundeinstellungen")){
			//headerInfos(0);
			if(!Rechte.hatRecht(Rechte.Systeminit_kalendergrundeinstellung, false)){
				doAccessDenied();
				return;
			}
			jxInhaltRechts = new SysUtilKalendereinstell();
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);
			setHeader("TKGrundeinstellungen");			
			break;
		}
		if(util.equals("Startoptionen definieren")){
			jxInhaltRechts = new SysUtilAnsichtsOptionen();
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);
			setHeader("Ansichtsoptionen");			
			break;
		}
		if(util.equals("Gruppentermine definieren")){
			//headerInfos(0);
			if(!Rechte.hatRecht(Rechte.Systeminit_kalendergruppentermine, false)){
				doAccessDenied();
				return;
			}
			jxInhaltRechts = new SysUtilGruppenDef();
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);
			setHeader("Gruppentermine");
			break;
		}
		if(util.equals("Emailparameter")){
			if(!Rechte.hatRecht(Rechte.Systeminit_eimailadressen, false)){
				doAccessDenied();
				return;
			}
			jxInhaltRechts = new SysUtilEmailparameter();
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);
			break;
		}
		if(util.equals("Druckvorlage Terminliste")){
			if(!Rechte.hatRecht(Rechte.Systeminit_terminlistedruck, false)){
				doAccessDenied();
				return;
			}
			jxInhaltRechts = new SysUtilDruckvorlage();
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);
			setHeader("DruckvorlageTerminliste");
			break;
		}
		if(util.equals("Ru:gl-Grundeinstellungen")){
			if(!Rechte.hatRecht(Rechte.Systeminit_ruglgrundeinstellung, false)){
				doAccessDenied();
				return;
			}
			jxInhaltRechts = new SysUtilRoogleEinstellungen();
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);
			setHeader("RoogleGrundeinstellungen");
			break;
		}
		if(util.equals("Datenbankparameter")){
			if(!Rechte.hatRecht(Rechte.Systeminit_datenbank, false)){
				doAccessDenied();
				return;
			}
			jxInhaltRechts = new SysUtilDBdaten();
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);
			setHeader("RoogleGrundeinstellungen");
			break;
		}
		if(util.equals("Firmenangaben Mandanten")){
			if(!Rechte.hatRecht(Rechte.Systeminit_mandanten, false)){
				doAccessDenied();
				return;
			}
			jxInhaltRechts = new SysUtilMandanten();
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);
			setHeader("RoogleGrundeinstellungen");
			break;
		}
		if(util.equals("Patient")){
			if(!Rechte.hatRecht(Rechte.Systeminit_fensterpatient, false)){
				doAccessDenied();
				return;
			}
			jxInhaltRechts = new SysUtilPatient();
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);
			setHeader("PatientStammdaten");
			break;
		}
		if(util.equals("Arzt")){
			if(!Rechte.hatRecht(Rechte.Systeminit_fensterarzt, false)){
				doAccessDenied();
				return;
			}
			jxInhaltRechts = new SysUtilArzt();
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);
			setHeader("RoogleGrundeinstellungen");
			break;
		}
		if(util.equals("Krankenkasse")){
			if(!Rechte.hatRecht(Rechte.Systeminit_fensterkasse, false)){
				doAccessDenied();
				return;
			}
			jxInhaltRechts = new SysUtilKrankenkasse();
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);
			setHeader("RoogleGrundeinstellungen");
			break;
		}
		if(util.equals("Rezepte")){
			if(!Rechte.hatRecht(Rechte.Systeminit_fensterrezept, false)){
				doAccessDenied();
				return;
			}
			jxInhaltRechts = new SysUtilRezepte();
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);
			setHeader("RoogleGrundeinstellungen");
			break;
		}
		if(util.equals("angeschlossene Geraete")){
			if(!Rechte.hatRecht(Rechte.Systeminit_geraete, false)){
				doAccessDenied();
				return;
			}
			jxInhaltRechts = new SysUtilGeraete();
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);
			setHeader("RoogleGrundeinstellungen");
			break;
		}
		if(util.equals("Anschlüsse")){
			if(!Rechte.hatRecht(Rechte.Systeminit_schnittstellen, false)){
				doAccessDenied();
				return;
			}
			jxInhaltRechts = new SysUtilAnschluesse();
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);
			setHeader("RoogleGrundeinstellungen");
			break;
		}
		if(util.equals("Fremdprogramme")){
			if(!Rechte.hatRecht(Rechte.Systeminit_fremdprogramme, false)){
				doAccessDenied();
				return;
			}
			jxInhaltRechts = new SysUtilFremdprogramme();
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);
			setHeader("RoogleGrundeinstellungen");
			break;
		}
		if(util.equals("Preise bearbeiten/importieren")){
			if(!Rechte.hatRecht(Rechte.Systeminit_preiseimportieren, false)){
				doAccessDenied();
				return;
			}
			jxInhaltRechts = new SysUtilPreislisten();
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);
			setHeader("RoogleGrundeinstellungen");
			break;
		}
		if(util.equals("Anwendungsregeln bearbeiten")){
			jxInhaltRechts = new SysUtilPreisregeln();
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);
			setHeader("RoogleGrundeinstellungen");
			break;
		}
		if(util.equals("Tarifgruppen bearbeiten")){
			if(!Rechte.hatRecht(Rechte.Systeminit_tarifgruppen, false)){
				doAccessDenied();
				return;
			}
			jxInhaltRechts = new SysUtilTarifgruppen();
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);
			setHeader("RoogleGrundeinstellungen");
			break;
		}
		if(util.equals("Kostenträgerdatei einlesen")){
			if(!Rechte.hatRecht(Rechte.Systeminit_kostentraegerdatei, false)){
				doAccessDenied();
				return;
			}
			jxInhaltRechts = new SysUtilKostentraeger();
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);
			break;
		}
		if(util.equals("Nebraska / Zertifikatshandling")){
			if(!Rechte.hatRecht(Rechte.Systeminit_nebraska, false)){
				doAccessDenied();
				return;
			}
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					try {
						Runtime.getRuntime().exec("java -jar "+Reha.proghome+"Nebraska.jar "+Reha.aktIK);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return null;
				}
				
			}.execute();
		}
		if(util.equals("Abrechnungsformulare und Drucker")){
			if(!Rechte.hatRecht(Rechte.Systeminit_abrechnungformulare, false)){
				doAccessDenied();
				return;
			}
			jxInhaltRechts = new SysUtilAbrechnungFormulare();
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);
			break;
		}
		if(util.equals("Fortlaufender Nummernkreis")){
			if(!Rechte.hatRecht(Rechte.BenutzerSuper_user, false)){
				doAccessDenied();
				return;
			}
			jxInhaltRechts = new SysUtilNummernKreis(null);
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);
			break;
		}
		if(util.equals("Positionskürzel anlegen")){
			if(!Rechte.hatRecht(Rechte.Systeminit_preiseimportieren, false)){
				doAccessDenied();
				return;
			}
			jxInhaltRechts = new SysUtilKuerzel();
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);
			break;
		}
		if(util.equals("Software-Updateservice")){
			if(!Reha.updatesBereit){
				int anfrage = JOptionPane.showConfirmDialog(null,
						"<html>Es sind keine Updates verfügbar.<br>Wollen Sie Thera-Pi trotzdem beenden und den Update-Explorer starten?</html>",
						"Achtung: Wichtige Benuterzanfrage",JOptionPane.YES_NO_OPTION);
				if(anfrage == JOptionPane.YES_OPTION){
					try {
						Runtime.getRuntime().exec("java -jar "+Reha.proghome+"TheraPiUpdates.jar TheraPiStarten");
						Reha.thisClass.beendeSofort();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}else{
				int anfrage = JOptionPane.showConfirmDialog(null,
						"<html>Es sind Updates verfügbar.<br>Um den Update-Explorer zu starten <b>muß Thera-Pi beendet werden</b>.<br>Wollen Sie den Update-Explorer jetzt ausführen und <b>Thera-Pi beenden</b>?</html>",
						"Achtung: Wichtige Benuterzanfrage",JOptionPane.YES_NO_OPTION);
				if(anfrage == JOptionPane.YES_OPTION){
					try {
						Runtime.getRuntime().exec("java -jar "+Reha.proghome+"TheraPiUpdates.jar TheraPiStarten");
						Reha.thisClass.beendeSofort();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		if(util.equals("Befreiungen zurücksetzen/Jahreswechsel")){
			if(!Rechte.hatRecht(Rechte.BenutzerSuper_user, false)){
				doAccessDenied();
				return;
			}
			jxInhaltRechts = new SysUtilJahresUmstellung();
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);
			break;
		}

		// Lemmi 20101225: Neue Seite Bedienung eingebaut
		if(util.equals("Bedienung")){
			if(!Rechte.hatRecht(Rechte.BenutzerSuper_user, false)){
				doAccessDenied();
				return;
			}
			jxInhaltRechts = new SysUtilBedienung(null);
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);
			break;
		}

		// Lemmi 20101228: Neue Seite OP & Mahnung eingebaut
		if(util.equals("Offene Posten & Mahnung")){
			if(!Rechte.hatRecht(Rechte.BenutzerSuper_user, false)){
				doAccessDenied();
				return;
			}
			jxInhaltRechts = new SysUtilOpMahnung(null);
			jxInhaltRechts.setVisible(true);
			jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
			jxRechts.revalidate();
			cursorWait(false);
			break;
		}

		
		if(SystemConfig.hmSysIcons.get("werkzeuge") == null){
			SystemConfig.hmSysIcons.put("werkzeuge",new ImageIcon(Reha.proghome+"icons/werkzeug.gif"));
		}
		jxInhaltRechts = new SysUtilVorlage(SystemConfig.hmSysIcons.get("werkzeuge"));
		jxInhaltRechts.setVisible(true);
		jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
		cursorWait(false);
		jxRechts.revalidate();

	}
	
}
/*******************************************/
private void doAccessDenied(){
	if(SystemConfig.hmSysIcons.get("noaccess") == null){
		SystemConfig.hmSysIcons.put("noaccess",new ImageIcon(Reha.proghome+"icons/"+"noaccess.gif"));
	}
	jxInhaltRechts = new SysUtilVorlage(SystemConfig.hmSysIcons.get("noaccess"));
	jxInhaltRechts.setVisible(true);
	jxRechts.add(jxInhaltRechts,BorderLayout.CENTER);
	cursorWait(false);
	jxRechts.revalidate();
}
private void cursorWait(boolean ein){
	if(!ein){
		this.setCursor(Reha.thisClass.cdefault);
	}else{
		this.setCursor(Reha.thisClass.wartenCursor);
	}
}
@Override
public void valueChanged(TreeSelectionEvent e) {
	// TODO Auto-generated method stub
	//System.out.println(e);
	TreePath path = e.getNewLeadSelectionPath(); 
    String[] split = path.toString().split(",");
    auswertenSysUtil(split[split.length-1].replaceAll("\\]","").trim());
}

public static void abbrechen(){
	Reha.thisClass.systeminitpanel.tree.setSelectionInterval(0,0);
	Reha.thisClass.systeminitpanel.auswertenSysUtil("nix");
}
//private HashMap<String,String> htitel = new HashMap();
//private HashMap<String,String> hdescription = new HashMap();
//private HashMap<String,ImageIcon> hicon = new HashMap();

private void setHeader(String shm){
	header.setTitle(htitel.get(shm));
	header.setDescription(hdescription.get(shm));
	header.setIcon(hicon.get(shm));
}

private void headerInfos(){
	String sdummy = null;
	ImageIcon icodummy = null;
	/***********Numero uno***********/
	sdummy = "Terminkalender-Grundeinstellungen";
	htitel.put("TKGrundeinstellungen", sdummy);
	sdummy = "Die Grundeinstellungen beeinflussen die Struktur und die Funktionen der Kalenderdatenbank. Änderungen von Tagesbeginn \n"+
	"oder -ende führen zur Neudefinition ALLER bestehenden Datenbanken. Dieser Vorgang benötigt viel Rechenkapazität und dauert daher\n"+
	"entsprechend lange.\n"+
	"Der Refreshtakt definiert, in welchem Zyklus die Bildschirmanzeige mit der Datenbank abgeglichen wird. Einzelplatzinstallationen\n "+
	"arbeiten dabei ohne zeitliche Verzögerung.";
	hdescription.put("TKGrundeinstellungen", sdummy);
	icodummy = new ImageIcon(Reha.proghome+"icons/header-image.png");
	hicon.put("TKGrundeinstellungen", icodummy);

	/***********Numero due***********/
	sdummy = "Kalenderbenutzer verwalten";
	htitel.put("Kalenderbenutzer", sdummy);
	sdummy = "'Kalenderbenutzer' kann ein Behandler, sonstige Mitarbeiter oder auch eine besondere Ressource sein.\n\n"+
	"Der Matchcode ist die dargestellte Überschrift bzw. der in der Terminsuche verwendete Begriff. Er kann frei vergeben werden\n"+
	"und ist der Form nicht festgelegt. 'Peter', 'Müller Martina' oder 'Gewürzgurke007' - Alles erlaubt!";
	hdescription.put("Kalenderbenutzer", sdummy);
	icodummy = new ImageIcon(Reha.proghome+"icons/header-image.png");
	hicon.put("Kalenderbenutzer", icodummy);

	/***********Numero tre***********/
	sdummy = "Behandlersets definieren";
	htitel.put("Behandlersets", sdummy);
	sdummy = "In Behandlersets werden bis zu 7 Kalenderbenutzer gruppiert, die im Terminkalender gleichzeitig\n"+
	"nebeneinander dargestellt werden sollen. Im Terminkalender kann über F12 schnell zwischen den verschiedenen Sets\n"+
	"umgeschaltet werden.";
	hdescription.put("Behandlersets", sdummy);
	icodummy = new ImageIcon(Reha.proghome+"icons/header-image.png");
	hicon.put("Behandlersets", icodummy);
	
	/***********Numero tre bi***********/
	sdummy = "Terminkalender Startoptionen";
	htitel.put("Ansichtsoptionen", sdummy);
	sdummy = "Der Terminkalender startet standardmäßig in der Normalansicht ohne vordefiniertem Behandlerset.\n"+
	"Hier können Sie die Ansichtsoptionen festlegen die beim starten des Terminkalenders angewendet werden.\n";
	hdescription.put("Ansichtsoptionen", sdummy);
	icodummy = new ImageIcon(Reha.proghome+"icons/header-image.png");
	hicon.put("Ansichtsoptionen", icodummy);
	
	/***********Numero vier***********/
	sdummy = "Druckvorlage";
	htitel.put("DruckvorlageTerminliste", sdummy);
	sdummy = "In diesem Fenster können für jeden angeschlossenen Rechner Vorgaben zum Druck der Terminliste\n"+
	"gemacht werden. \n"+
	"Wenn Sie die Druckvorlagendatei hinsichtlich der Tabellen-, Zeilen- oder Spaltenzahl verändern, müssen diese\n"+
	"Strukturparameter unbedingt hier ebenfalls angepasst werden, da die Daten sonst nicht korrekt übergeben werden können."+
	"Angaben zu Papierformat und verwendetem Druckereinzug werden in der Vorlagendatei mit OpenOffice eingestellt.";
	hdescription.put("DruckvorlageTerminliste", sdummy);
	icodummy = new ImageIcon(Reha.proghome+"icons/header-image.png");
	hicon.put("DruckvorlageTerminliste", icodummy);

	/***********Numero fünef***********/
	sdummy = "Kalenderfarben definieren";
	htitel.put("Kalenderfarben", sdummy);
	sdummy = "Die farbliche Darstellung der Termine im Kalender ist eine sehr effiziente Signalsprache. \n"+
	"Sie können zwischen 4 vordefinierten Farbkombinationen wählen oder für die jeweilige Bedeutung eine eigene\n"+
	"Kombination vergeben.\n"+
	"Besondere Bedeutung haben die Codes 'A' bis 'H', die mit einer Bedeutung frei verknüpft werden können.\n"+
	"Diese Codes können dann wie in der Tabelle angegeben in den Terminen hinter der Rezeptnummer eingetragen werden\n"+
	"und bestimmen die Farbgebung (der Code für die Behandlungszeit wird dann nachrangig).";
	hdescription.put("Kalenderfarben", sdummy);
	icodummy = new ImageIcon(Reha.proghome+"icons/header-image.png");
	hicon.put("Kalenderfarben", icodummy);

	/***********Numero sex***********/
	sdummy = "Gruppentermine definieren";
	htitel.put("Gruppentermine", sdummy);
	sdummy = "Gruppentermine ist was für den Spezialisten\n"+
	"Der wird dann auch den Text schreiben....\n"+
	"...und beim Hilfetext richtig ins Zeugs legen!";
	hdescription.put("Gruppentermine", sdummy);
	icodummy = new ImageIcon(Reha.proghome+"icons/header-image.png");
	hicon.put("Gruppentermine", icodummy);

	/***********Numero siebene***********/
	sdummy = "Kalenderjahr anlegen";
	htitel.put("Kalenderjahranlegen", sdummy);
	sdummy = "Hier wird die Kalenderdatenbank um ein weiteres Jahr verlängert. Sinnvoll ist dies, wenn zuvor\n"+
	"die Arbeitszeitpläne inklusive der Dauertermine auf den aktuellen Stand gebracht wurden (sofern gewünscht). \n"+
	"Die Feiertagsliste kann importiert werden und durch eigene Daten (z.B. Betriebsausflug, -ferien) ergänzt werden.";
	hdescription.put("Kalenderjahranlegen", sdummy);
	icodummy = new ImageIcon(Reha.proghome+"icons/header-image.png");
	hicon.put("Kalenderjahranlegen", icodummy);

	/***********Numero achte***********/
	sdummy = "Ru:gl - Grundeinstellungen";
	htitel.put("RoogleGrundeinstellungen", sdummy);
	sdummy = "Die Grundeinstellungen für die Terminsuchmaschine [Ru:gl] legen die Vorgaben fest, mit denen\n"+
	"Ru:gl startet. Sie können natürlich für jeden Suchvorgang im Ru:gl-Fenster 'individueller Zeitraum' angepasst werden.\n";
	hdescription.put("RoogleGrundeinstellungen", sdummy);
	icodummy = new ImageIcon(Reha.proghome+"icons/header-image.png");
	hicon.put("RoogleGrundeinstellungen", icodummy);

	/***********Numero neune***********/
	sdummy = "Stammdaten - Patienten";
	htitel.put("PatientStammdaten", sdummy);
	sdummy = "Hier legen Sie fest in welchem Container das Patientenfenster erscheinen soll.\n"+
	"Sie können Textvorlagen definieren, und individuelle Merkmale aufnehmen, die Sie später dem Patienten zuordnen.\n";
	hdescription.put("PatientStammdaten", sdummy);
	icodummy = new ImageIcon(Reha.proghome+"icons/header-image.png");
	hicon.put("PatientStammdaten", icodummy);	
	}

/******************************************/

		
}	

