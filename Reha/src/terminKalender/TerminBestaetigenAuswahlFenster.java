package terminKalender;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import javax.swing.WindowConstants;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

import stammDatenTools.RezTools;
import systemEinstellungen.SystemPreislisten;
import CommonTools.JRtaCheckBox;
import systemTools.ListenerTools;
import systemTools.WinNum;



import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;

//Drud 110418
//TODO 6. Anpassung des Umsatzbeteiligung-Moduls, um nur die tatsächlich geleisteten Heilmittel anzuzeigen 

public class TerminBestaetigenAuswahlFenster extends RehaSmartDialog implements   ActionListener, WindowListener, KeyListener, ItemListener{
	private static final long serialVersionUID = -2972115133247099975L;
	/**
	 * 
	 */
	private String eigenName = null;
	private JXPanel jcc = null;
	private JXPanel jpan  = null;
	private RehaTPEventClass rtp = null;
	private JRtaCheckBox[] btm = {null,null,null,null}; //Welche Position soll bestätigt werden?
	private JXLabel[] AnzTermine = {null, null, null,null}; //Bereits geleistete Therapien
	private JXLabel[] AnzRezept = {null, null, null,null};	//Max. Therapien lt. VO
	private JXLabel[] HMPosNr = {null,null,null,null}; //Positionsnummer
	private JXLabel[] SpaltenUeberschrift ={null, null, null, null};
	private JXButton okbut;
	private JXButton abbruchbut;
	public Vector<BestaetigungsDaten> hMPosLC = null;
	private int anzahlPos;


	public TerminBestaetigenAuswahlFenster(JXFrame owner, String name,Vector<BestaetigungsDaten> hMPos,String reznum,int preisgruppe){
		super(owner, "Eltern-TermBest"+WinNum.NeueNummer());
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		hMPosLC= hMPos;
		anzahlPos = hMPosLC.size();
		
		for (int i=0;i<4;i++){
			btm[i] = new JRtaCheckBox("");
			if(i<anzahlPos){
				btm[i].setName(Integer.toString(i));
				HMPosNr[i] = new JXLabel("HMPos");	
				AnzTermine[i] = new JXLabel("geleistet_Menge");
				AnzRezept[i] = new JXLabel("VO_Menge");

				btm[i].setEnabled((hMPosLC.get(i).anzBBT < hMPosLC.get(i).vOMenge) ? true : false);
				
				HMPosNr[i].setText(RezTools.getKurzformFromPos(hMPosLC.get(i).hMPosNr, Integer.toString(preisgruppe-1),
						SystemPreislisten.hmPreise.get(RezTools.putRezNrGetDisziplin(reznum)).get(preisgruppe-1)) );
				//HMPosNr[i].setText(hMPosLC.get(i).hMPosNr);
				AnzTermine[i].setText(Integer.toString(hMPosLC.get(i).anzBBT));
				AnzRezept[i].setText(Integer.toString(hMPosLC.get(i).vOMenge));

				btm[i].addItemListener(this);
				btm[i].setSelected(hMPosLC.get(i).best);  //TODO TerminFenster hat bereits eine Vorauswahl getroffen (z.B. Doppelbehandlungen)!
				btm[i].addKeyListener(this);
				btm[i].setSelected((hMPosLC.get(i).anzBBT < hMPosLC.get(i).vOMenge) ? true : false);
			}
		}
		SpaltenUeberschrift[0]= new JXLabel("bestätigen>");
		SpaltenUeberschrift[1]= new JXLabel("Heilmittel");
		SpaltenUeberschrift[2]= new JXLabel("geleistet");
		SpaltenUeberschrift[3]= new JXLabel("VO-Menge");

		setPreferredSize(new Dimension(240,250));

		eigenName = "TermBest"+WinNum.NeueNummer(); 
		this.setName(eigenName);
		getSmartTitledPanel().setPreferredSize(new Dimension(240,220));
		getSmartTitledPanel().setName("Eltern-"+eigenName);
		this.getParent().setName("Eltern-"+eigenName);

		this.setUndecorated(true);
		this.addWindowListener(this);
		this.addKeyListener(this);

		jcc = new JXPanel(new BorderLayout());
		jcc.setDoubleBuffered(true);
		jcc.setName(eigenName);
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				jcc.setBackgroundPainter(Reha.thisClass.compoundPainter.get("TerminBestaetigenAuswahlFenster"));
				return null;
			}
		}.execute();

		jcc.setBorder(null);
		jcc.addKeyListener(this);

		this.setContentPanel(jcc );

		getSmartTitledPanel().setTitle("Leistung bestätigen");
		getSmartTitledPanel().getContentContainer().setName(eigenName);
		getSmartTitledPanel().addKeyListener(this);
		getSmartTitledPanel().validate();
		PinPanel pinPanel = new PinPanel();
		pinPanel.getGruen().setVisible(false);
		pinPanel.setName(eigenName);
		pinPanel.setzeName(eigenName);
		pinPanel.addKeyListener(this);

		setPinPanel(pinPanel);
		
		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener((RehaTPEventListener) this);
		jcc.add(getTerminBest(jpan = new JXPanel()),BorderLayout.CENTER);
		jcc.add(getButtonBest(),BorderLayout.SOUTH);
		jpan.validate();
		jcc.validate();

		//this.setAlwaysOnTop(true); //gefährlich in Java, außer in begründeten Ausnahmefenstern eigentlich nur anzuwenden bei NON-Modalen Fenstern
		//this.setModal(true); //Wenn man Modal in der aufrufenden Methode setzt hat man noch die Chance den Focus zu setzten. 
		validate();
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				setzeFocus();
			}
		});
		
	}
	public void setzeFocus(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				okbut.requestFocus();
			}
		});
	}
	private JXPanel getTerminBest(JXPanel jp){
		                              // 1    2       3   4        5   6      7    8      9   10   11 12 13 14
		FormLayout lay = new FormLayout("6px,center:p,6px,right:p,6px,right:p,6px,right:p,6px,66px,6px,p,6px,p",
		//1.  2.   3.  4.  5.  6. 7.  8. 9   10 11  12 13  14  15  16
		"6px, p, 6dlu:g, p ,6dlu,p,6dlu,p,6dlu,p,6dlu:g"); /*,6dlu,p,6dlu,p,6dlu,p*/
		jp.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		jp.setBackground(Color.WHITE);
		//jp.setOpaque(false); // mit weiß ist es passend zu den anderen Terminkalender Optionsfenster
		jp.setLayout(lay);
		CellConstraints cc = new CellConstraints();
		/*
		 */
		 /***/
		for (int k=0;k<4;k++){
			jp.add(SpaltenUeberschrift[k], cc.xy(2+2*k, 2));
			if(k <anzahlPos ){
				jp.add(btm[k], cc.xy(2, 4+2*k));
				jp.add(HMPosNr[k], cc.xy(4, 4+2*k));
				jp.add(AnzTermine[k],cc.xy(6, 4+2*k));
				jp.add(AnzRezept[k],cc.xy(8, 4+2*k));
			}
		}
		/*
		okbut = new JXButton("ok");
		okbut.setActionCommand("ok");
		okbut.addActionListener(this);
		abbruchbut = new JXButton("abbrechen");
		abbruchbut.setActionCommand("abbruch");
		abbruchbut.addActionListener(this);
		jp.add(okbut,cc.xyw(2, 14,3));
		jp.add(abbruchbut,cc.xyw(6, 14,3));
		*/
		
		jp.addKeyListener(this);
		jp.validate();
		return jp;
	}
	private JXPanel getButtonBest(){
		String xwert = "fill:0:grow(0.5),50dlu,10dlu,50dlu,fill:0:grow(0.5)";
		String ywert = "5dlu,p,5dlu";
		FormLayout lay = new FormLayout(xwert,ywert);
		CellConstraints cc = new CellConstraints();
		JXPanel pan = new JXPanel();
		pan.setLayout(lay);
		okbut = new JXButton("ok");
		okbut.setActionCommand("ok");
		okbut.addActionListener(this);
		abbruchbut = new JXButton("abbrechen");
		abbruchbut.setActionCommand("abbruch");
		abbruchbut.addActionListener(this);
		pan.add(okbut,cc.xy(2,2));
		pan.add(abbruchbut,cc.xy(4,2));
		pan.addKeyListener(this);
		pan.validate();

		return pan;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getActionCommand().equals("ok")){
			new Thread(){
				public void run(){
					zurueck();				
				}
			}.start();
		}
		if(arg0.getActionCommand().equals("abbruch")){
			reset();
		}
	}
	private void zurueck(){
		int counter = 0;
		for (int i=0; i < btm.length; i++){
			counter += (btm[i].isSelected() ? 1 : 0);
		}
		if (counter != 0){
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					for (int i=0;i<anzahlPos;i++){
						hMPosLC.get(i).best = btm[i].isSelected();
					}
					RezTools.DIALOG_WERT = RezTools.DIALOG_OK;
					setVisible(false);
					dispose();
				}
			});
		}else{
			JOptionPane.showMessageDialog(null, "Sie haben noch keine Heilmittelposition ausgewählt!");
		}
	}
	private void reset(){
		for (int i=0; i <btm.length; i++){
			if(i < anzahlPos){
				hMPosLC.get(i).best = false;				
			}
		}
		RezTools.DIALOG_WERT = RezTools.DIALOG_ABBRUCH;
		setVisible(false);
		this.dispose();
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode() == KeyEvent.VK_ESCAPE){ //ESC 27
			arg0.consume();
			reset();
		}
		if(arg0.getKeyCode() == KeyEvent.VK_ENTER ){ //ENTER 10
			((JComponent) arg0.getSource()).requestFocus();
			arg0.consume();
			zurueck();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		/*
		if(e.getKeyCode() == KeyEvent.VK_ENTER){
			((JComponent) e.getSource()).requestFocus();
			e.consume();
			zurueck();
		}	
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
			e.consume();
			reset();
		}
		*/	
	}

	@Override
	public void keyTyped(KeyEvent e) {
		/*
		if(e.getKeyCode() == KeyEvent.VK_ENTER){
			((JComponent) e.getSource()).requestFocus();
			e.consume();
			zurueck();
		}	
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
			e.consume();
			reset();
		}
		*/	
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		if (okbut != null){
			ListenerTools.removeListeners(okbut);
			okbut = null;
		}
		if (abbruchbut != null){
			ListenerTools.removeListeners(abbruchbut);
			abbruchbut = null;	
		}
		ListenerTools.removeListeners(this);
		if(jcc != null){
			ListenerTools.removeListeners(jcc);
			jcc = null;
		}
	}
	public void rehaTPEventOccurred(RehaTPEvent evt) {
		String ss =  this.getName();
		try{
			if (evt.getDetails()[0].equals(ss) && evt.getDetails()[1]=="ROT"){
				FensterSchliessen(evt.getDetails()[0]);
				rtp.removeRehaTPEventListener((RehaTPEventListener) this);
				rtp = null;
			}	
		}catch(NullPointerException ne){

		}
	}
	public void FensterSchliessen(String welches){
		this.dispose();		
	}	
	
	@Override
	public void itemStateChanged(ItemEvent arg0) {
		int chkBoxNr= -1;
		try{  // was ist wenn eine Componente ItemChanged feuert, deren Name sich nicht zu einem Integer parsen lässt?
			//das ist dann Murks, bzw. wirft zurecht eine Exception, deshalb arbeite ich für solche Aufgaben wesentlich
			//lieber mit dem ActioListener
			chkBoxNr = Integer.parseInt(((JComponent)arg0.getSource()).getName());
		}catch (Exception Ex){
			System.out.println(Ex);
		}
		if (arg0.getStateChange() == ItemEvent.SELECTED) {
			AnzTermine[chkBoxNr].setText(Integer.toString(Integer.parseInt(AnzTermine[chkBoxNr].getText())+1)); 
			AnzTermine[chkBoxNr].setForeground(Color.BLUE);
		} else {
			AnzTermine[chkBoxNr].setText(Integer.toString(Integer.parseInt(AnzTermine[chkBoxNr].getText())-1));
			AnzTermine[chkBoxNr].setForeground(Color.BLACK);
		}
		validate();
	}


}



