package terminKalender;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import systemEinstellungen.SystemConfig;
import systemTools.Colors;
import systemTools.WinNum;
import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;

public class SetWahlNeu extends JDialog implements  MouseListener, FocusListener, ActionListener, WindowListener, KeyListener,RehaTPEventListener{
	String eigenName = null;
	//SetWahlNeu thisClass;
	JXPanel jcc;
	JXPanel jpan;
	JList jList1;
	int ret = -1;
	private JButton okButton = null;
	private JButton abbruchButton = null;
	
	private RehaTPEventClass rtp = null;
	private TerminFenster eltern;
	int wahl;
	PinPanel pinPanel;
	public SetWahlNeu(TerminFenster xeltern){
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setSize(293,247);
		this.setContentPane(new JXTitledPanel());
		eltern = xeltern;

		eigenName = "TagWahl"+WinNum.NeueNummer(); 
		this.setModal(true);
		this.setUndecorated(true);

		this.addFocusListener(this);
		this.addWindowListener(this);
		this.addKeyListener(this);
		this.addMouseListener(this);		
		jcc = new JXPanel(new GridLayout(1,1));
		jcc.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		jcc.setDoubleBuffered(true);
		jcc.setName(eigenName);

		
		jcc.setBackground(Color.WHITE);
		jcc.setBorder(null);
		jcc.addKeyListener(this);
		jcc.addFocusListener(this);
		jcc.addMouseListener(this);
		

	
		
		pinPanel = new PinPanel();
		pinPanel.getGruen().setVisible(false);
		pinPanel.setName(eigenName);
		pinPanel.setzeName(eigenName);
		pinPanel.addKeyListener(this);
		
		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener((RehaTPEventListener) this);
		
		jcc.add(getSetWahl(jpan = new JXPanel(new BorderLayout())));
		jcc.validate();
		((JXTitledPanel)this.getContentPane()).getContentContainer().setBackground(Color.WHITE);
		((JComponent) ((JXTitledPanel)this.getContentPane()).getContentContainer()).setBorder(null);
		((JXTitledPanel)this.getContentPane()).getContentContainer().add(jcc,BorderLayout.CENTER);
		
		
		this.wahl = ((TerminFenster) Reha.thisClass.terminpanel).aktuellesSet();
		this.jList1.setSelectedIndex(this.wahl);
		this.validate();
		
	
	}
	private JXPanel getSetWahl(JXPanel jpan){
		jpan.add(new JScrollPane(getJList1()), BorderLayout.CENTER);
		JXPanel dummy = new JXPanel(new BorderLayout());
		dummy.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		dummy.addMouseListener(this);
		dummy.addKeyListener(this);

		JXPanel dummy2 = new JXPanel( new GridLayout(1,4,7,10));
		dummy2.setName("dummy2");
		dummy2.addMouseListener(this);
		dummy2.addKeyListener(this);
		dummy2.add(new JLabel(""));
		okButton = new JButton("Ok");
		okButton.setActionCommand("ok");
		okButton.addActionListener(this);
		dummy2.add(okButton);
		abbruchButton = new JButton("Abbruch");
		abbruchButton.setActionCommand("abbrechen");		
		abbruchButton.addActionListener(this);
		dummy2.add(abbruchButton);
		dummy2.add(new JLabel(""));
		dummy.add(dummy2,BorderLayout.CENTER);
		jpan.add(dummy,BorderLayout.SOUTH);
		return jpan;
	}
	private JList getJList1() {
		final DefaultListModel model = new DefaultListModel();
		if (jList1 == null) {
			jList1 = new JList(model);
			jList1.addKeyListener(this);
			jList1.addMouseListener(this);

			ListeFuellen(model);
		}
		return jList1;
	}
	private void ListeFuellen(DefaultListModel model){
		int i,max = 0;
		max = SystemConfig.aTerminKalender.size();
		for(i=0;i<max;i++){
			model.add(i,(String)((ArrayList)SystemConfig.aTerminKalender.get(i).get(0)).get(0));
		}
		return;
	}
	
	private void DialogBeenden(int wie){
		this.ret = wie;
		FensterSchliessen("dieses");
		this.setVisible(false);
		this.dispose();
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		//System.out.println(arg0.getKeyCode());
		if (arg0.getKeyCode()==10){
			//arg0.consume();
			if(ret != -1){
				//System.out.println("Beendet mit oben "+jList1.getSelectedIndex());
				eltern.swSetWahl = jList1.getSelectedIndex();
				FensterSchliessen("Dieses");
				return;
			}else{
				//System.out.println("Beendet mit unten "+jList1.getSelectedIndex());
				eltern.swSetWahl = jList1.getSelectedIndex();
				FensterSchliessen("Dieses");
				return;
			}	
		}
		if (arg0.getKeyCode()==27){
			//System.out.println("ESC gedrückt");			
			eltern.swSetWahl = -1;
			DialogBeenden(-1);
			return;
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
	public void windowClosed(WindowEvent arg0) {
		if(rtp != null){
			rtp.removeRehaTPEventListener((RehaTPEventListener) this);
			rtp = null;
		}
		if(pinPanel != null){
			pinPanel = null;
		}
	}
	
	public void rehaTPEventOccurred(RehaTPEvent evt) {
		// TODO Auto-generated method stub
		String ss =  this.getName();
		try{
			if (evt.getDetails()[0].equals(ss) && evt.getDetails()[1]=="ROT"){
				FensterSchliessen(evt.getDetails()[0]);
				rtp.removeRehaTPEventListener((RehaTPEventListener) this);
				rtp = null;
			}	
		}catch(NullPointerException ne){
			//System.out.println("In RoogleFenster" +evt);
		}
	}

	public void FensterSchliessen(String welches){
		this.setVisible(false);
		this.dispose();	
	}	

	class Task   extends TimerTask  
	{
	    public void run()  
	  {
	    	while(!jList1.hasFocus()){
	    		jList1.grabFocus();
	    		jList1.requestFocus();
			}	
	  }
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getClickCount() == 2){
			eltern.swSetWahl = jList1.getSelectedIndex();
			FensterSchliessen("Dieses");
			//System.out.println("In Mausdoppelklick");
			//System.out.println(arg0.getSource());
		}
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
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
