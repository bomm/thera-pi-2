package krankenKasse;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;

import rehaContainer.RehaTP;
import systemEinstellungen.SystemConfig;
import systemTools.JRtaTextField;
import systemTools.WinNum;

import terminKalender.TerminFenster;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dialoge.DragWin;
import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;

public class KassenFormulare extends JXDialog implements FocusListener, ActionListener, WindowListener, KeyListener,RehaTPEventListener{
	String eigenName = null;
	KassenFormulare thisClass;
	JXPanel jcc;
	JXPanel jpan;
	JList jList1;
	int ret = -1;
	private JButton okButton = null;
	private JButton abbruchButton = null;
	private MouseAdapter mymouse = null;
	private RehaTPEventClass rtp = null;
	int wahl;
	Vector<String>titel = null;
	Vector<String>formular = null;
	private JXTitledPanel jtp = null;
	private JRtaTextField tfrueck = null;
	public KassenFormulare(JXFrame owner,Vector<String>titel,JRtaTextField rueckform){
		super(owner, (JComponent)Reha.thisFrame.getGlassPane());
		this.titel = titel;
		//this.formular = formular;
		tfrueck = rueckform;
		this.setUndecorated(true);
		this.setName("KFormularWahl");
		jtp = new JXTitledPanel();
		this.jtp.setName("KFormularWahl");
		mymouse = new DragWin(this);
		jtp.addMouseListener(mymouse);
		jtp.addMouseMotionListener(mymouse);
		jtp.setContentContainer(getSetWahl());
		jtp.setTitleForeground(Color.WHITE);
		jtp.setTitle("Brief / Formular auswählen");
		PinPanel pinPanel = new PinPanel();
		pinPanel.getGruen().setVisible(false);
		pinPanel.setName("KFormularWahl");
		jtp.setRightDecoration(pinPanel);
		
		this.setContentPane(jtp);
		
		
		setModal(true);
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		thisClass = this;
		setSize(250,250);



		jList1.setSelectedIndex(0);
		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener((RehaTPEventListener) this);

		validate();
		
	}
	public KassenFormulare getInstance(){
		return this;
	}
	private JXPanel getSetWahl(){
		JXPanel jpan = new JXPanel(new BorderLayout());
		jpan.add(new JScrollPane(getJList1()), BorderLayout.CENTER);
		JXPanel dummy = new JXPanel(new BorderLayout());
		dummy.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		//dummy.addMouseListener(this);
		dummy.addKeyListener(this);

		JXPanel dummy2 = new JXPanel( new GridLayout(1,4,7,10));
		dummy2.setName("dummy2");
		//dummy2.addMouseListener(this);
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
			jList1.addMouseListener(new MouseAdapter(){
				@Override
				public void mouseClicked(MouseEvent arg0) {
					// TODO Auto-generated method stub
					if (arg0.getClickCount() == 2){
						tfrueck.setText(Integer.toString(jList1.getSelectedIndex()));
						//KassenPanel.thisClass.iformular = jList1.getSelectedIndex();
						getInstance().FensterSchliessen("dieses");
						//KassenFormulare.thisClass.FensterSchliessen("dieses");
					}
				}
			});
			ListeFuellen(model);
		}
		return jList1;
	}
	private void ListeFuellen(DefaultListModel model){
		int i,max = 0;
		//max = KassenPanel.thisClass.titel.size();
		max = titel.size();
		//String[] fach = new String[max];
		for(i=0;i<max;i++){
			//model.add(i,(String)KassenPanel.thisClass.titel.get(i));
			model.add(i,(String)titel.get(i));
		}
		return;
	}
	
	public void FensterSchliessen(String welches){
		this.jtp.removeMouseListener(mymouse);
		mymouse = null; 
		if(rtp != null){
			rtp.removeRehaTPEventListener((RehaTPEventListener) this);
			rtp=null;			
		}
		setVisible(false);
		this.dispose();
	}	

	private void DialogBeenden(int wie){
		FensterSchliessen("dieses");
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
		System.out.println(arg0.getKeyCode());
		if (arg0.getKeyCode()==10){
			//arg0.consume();
			if(ret != -1){
				System.out.println("Beendet mit oben "+jList1.getSelectedIndex());
				tfrueck.setText(Integer.toString(jList1.getSelectedIndex()));
				//KassenPanel.thisClass.iformular = jList1.getSelectedIndex();
				FensterSchliessen("Dieses");
				return;
			}else{
				System.out.println("Beendet mit unten "+jList1.getSelectedIndex());
				tfrueck.setText(Integer.toString(jList1.getSelectedIndex()));
				//KassenPanel.thisClass.iformular = jList1.getSelectedIndex();
				FensterSchliessen("Dieses");
				return;
			}	
		}
		if (arg0.getKeyCode()==27){
			System.out.println("ESC gedr�ckt");	
			tfrueck.setText(Integer.toString(-1));
			//KassenPanel.thisClass.iformular = -1;
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
			System.out.println("EventListener geschlossen");	
		}
		if(mymouse != null){
			jtp.removeMouseListener(mymouse);
			mymouse = null;			
		}
	}
	
	public void rehaTPEventOccurred(RehaTPEvent evt) {
		// TODO Auto-generated method stub
		System.out.println("****************Schlie�en des KassenFormular **************");
		String ss =  getName();
		System.out.println(getName()+" Eltern "+ss);
		try{
			if (evt.getDetails()[0].equals(ss) && evt.getDetails()[1]=="ROT"){
				FensterSchliessen(evt.getDetails()[0]);
				rtp.removeRehaTPEventListener((RehaTPEventListener) this);
			}	
		}catch(NullPointerException ne){
			System.out.println("Schlie�en des KassenFormular" +evt);
		}
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
		String cmd = arg0.getActionCommand();
		if(cmd.equals("ok")){
			tfrueck.setText(new Integer(jList1.getSelectedIndex()).toString());
			//KassenPanel.thisClass.iformular = jList1.getSelectedIndex();
			FensterSchliessen("Dieses");
		}
		if(cmd.equals("abbrechen")){
			tfrueck.setText(Integer.toString(-1));
			//KassenPanel.thisClass.iformular = -1;
			FensterSchliessen("Dieses");
		}
	}
	
	

}
