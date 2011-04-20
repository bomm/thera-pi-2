package terminKalender;

import hauptFenster.Reha;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
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

import systemTools.ListenerTools;
import systemTools.WinNum;
import terminKalender.TerminFenster.BestaetigungsDaten;
import utils.JRtaCheckBox;

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

	public TerminBestaetigenAuswahlFenster(JXFrame owner, String name,Vector<TerminFenster.BestaetigungsDaten> hMPos){
		super(owner, "Eltern-TermBest"+WinNum.NeueNummer());
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		hMPosLC= hMPos;

		for (int i=0;i<4;i++){
			btm[i] = new JRtaCheckBox("");
			btm[i].setName(Integer.toString(i));
			HMPosNr[i] = new JXLabel("HMPos");	
			AnzTermine[i] = new JXLabel("geleistet_Menge");
			AnzRezept[i] = new JXLabel("VO_Menge");

			btm[i].setEnabled((hMPosLC.get(i).anzBBT < hMPosLC.get(i).vOMenge) ? true : false);
			HMPosNr[i].setText(hMPosLC.get(i).hMPosNr);
			AnzTermine[i].setText(Integer.toString(hMPosLC.get(i).anzBBT));
			AnzRezept[i].setText(Integer.toString(hMPosLC.get(i).vOMenge));

			btm[i].addItemListener(this);
			btm[i].setSelected(hMPosLC.get(i).best);  //TODO TerminFenster hat bereits eine Vorauswahl getroffen (z.B. Doppelbehandlungen)!
			btm[i].addKeyListener(this);
		}
		SpaltenUeberschrift[0]= new JXLabel("bestätigen");
		SpaltenUeberschrift[1]= new JXLabel("HMPosNr");
		SpaltenUeberschrift[2]= new JXLabel("geleistet");
		SpaltenUeberschrift[3]= new JXLabel("VO-Menge");

		setPreferredSize(new Dimension(240,220));

		eigenName = "TermBest"+WinNum.NeueNummer(); 
		this.setName(eigenName);
		getSmartTitledPanel().setPreferredSize(new Dimension(240,220));
		getSmartTitledPanel().setName("Eltern-"+eigenName);
		this.getParent().setName("Eltern-"+eigenName);

		this.setUndecorated(true);
		this.addWindowListener(this);
		this.addKeyListener(this);

		jcc = new JXPanel(new GridLayout(1,1));
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

		jcc.add(getTerminBest(jpan = new JXPanel()));
		jpan.validate();
		jcc.validate();

		this.setAlwaysOnTop(true);
		this.setModal(true);
		validate();
	}

	private JXPanel getTerminBest(JXPanel jp){
		// 1    2       3   4 5   6 7   8 9  10 11 12 13 14
		FormLayout lay = new FormLayout("6px,center:p,6px,right:p,6px,right:p,6px,right:p,6px,66px,6px,p,6px,p",
				//1.    2.   3.  4.  5.  6. 7.  8. 9   10 11  12 13  14  15  16
		"6px, p, 6dlu, p ,6dlu,p,6dlu,p,6dlu,p,6dlu,p,6dlu,p,6dlu,p");
		jp.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		jp.setOpaque(false);
		jp.setLayout(lay);
		CellConstraints cc = new CellConstraints();
		/*
		 */
		 /***/
		for (int k=0;k<4;k++){
			jp.add(SpaltenUeberschrift[k], cc.xy(2+2*k, 2));
			jp.add(btm[k], cc.xy(2, 4+2*k));
			jp.add(HMPosNr[k], cc.xy(4, 4+2*k));
			jp.add(AnzTermine[k],cc.xy(6, 4+2*k));
			jp.add(AnzRezept[k],cc.xy(8, 4+2*k));
		}

		okbut = new JXButton("ok");
		okbut.setActionCommand("ok");
		okbut.addActionListener(this);
		abbruchbut = new JXButton("abbrechen");
		abbruchbut.setActionCommand("abbruch");
		abbruchbut.addActionListener(this);

		jp.add(okbut,cc.xyw(2, 14,3));
		jp.add(abbruchbut,cc.xyw(6, 14,3));

		jp.addKeyListener(this);
		jp.validate();
		return jp;
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
					for (int i=0;i<4;i++){
						hMPosLC.get(i).best = btm[i].isSelected();
					}
					setVisible(false);
				}
			});
			this.dispose();
		}else{
			JOptionPane.showMessageDialog(null, "Sie haben noch keine Heilmittelposition ausgewählt!");
		}
	}
	private void reset(){
		for (int i=0; i <btm.length; i++){
			hMPosLC.get(i).best = false;
			setVisible(false);
			this.dispose();
		}
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
		if(e.getKeyCode() == KeyEvent.VK_ENTER){
			((JComponent) e.getSource()).requestFocus();
			e.consume();
			zurueck();
		}	
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
			e.consume();
			reset();
		}	
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER){
			((JComponent) e.getSource()).requestFocus();
			e.consume();
			zurueck();
		}	
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
			e.consume();
			reset();
		}	
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



