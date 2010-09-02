package patientenFenster;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;

import org.jdesktop.swingx.JXPanel;

import systemTools.JRtaRadioButton;
import systemTools.ListenerTools;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class RezTestPanel extends JXPanel implements ActionListener,KeyListener,FocusListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4311956819371096397L;
	JRtaRadioButton [] jrb = {null,null,null,null};
	JButton [] jb = {null,null};
	public JLabel dummylab;
	ButtonGroup jrbg = new ButtonGroup();
	
	public RezTestPanel(JLabel dl){
		super();
		this.dummylab = dl;
		this.setBackground(Color.WHITE);
		doRezLayout();
		addKeyListener(this);
		validate();
		setVisible(true);
	}
	public void doRezLayout(){
		FormLayout lay = new FormLayout("fill:0:grow(0.50),p,fill:0:grow(0.50)",
		//1    2   3  4   5  6   7  8   9  10   11
		"0dlu,5dlu,p,3dlu,p,3dlu,p,3dlu,p,15dlu,p");
		this.setLayout(lay);
		CellConstraints cc = new CellConstraints();
		jrb[0] = new JRtaRadioButton("K = Krankheit (Pat./Therap.)");
		jrb[0].setOpaque(false);
		add(jrb[0],cc.xy(2,3));
		jrbg.add(jrb[0]);
		jrb[1] = new JRtaRadioButton("U = Urlaub (Pat./Therap.)");
		jrb[1].setOpaque(false);
		add(jrb[1],cc.xy(2,5));
		jrbg.add(jrb[1]);
		jrb[2] = new JRtaRadioButton("A = Abbruch der Therapie");
		jrb[2].setOpaque(false);
		add(jrb[2],cc.xy(2,7));
		jrbg.add(jrb[2]);
		jrb[3] = new JRtaRadioButton("T = Therapiepause ärztl. veranl.");
		jrb[3].setOpaque(false);
		add(jrb[3],cc.xy(2,9));
		jrbg.add(jrb[3]);
		jrb[0].setSelected(true);
		FormLayout lay2= new FormLayout("fill:0:grow(0.33),p,fill:0:grow(0.33),p,fill:0:grow(0.33)",
				"fill:0:grow(0.5),p,fill:0:grow(0.5)");
		PanelBuilder pb = new PanelBuilder(lay2);
		pb.getPanel().setOpaque(false);
		CellConstraints cc2 = new CellConstraints();
		jb[0] = new JButton("Übernahme");
		jb[0].setActionCommand("uebernahme");
		jb[0].addActionListener(this);
		jb[0].addKeyListener(this);
		pb.add(jb[0],cc2.xy(2, 2));
		jb[1] = new JButton("abbrechen");
		jb[1].setActionCommand("abbrechen");
		jb[1].addActionListener(this);
		jb[1].addKeyListener(this);
		pb.add(jb[1],cc2.xy(4, 2));
		pb.getPanel().validate();
		add(pb.getPanel(),cc.xywh(1,11,3,1));
	}
	public RezTestPanel getInstance(){
		return this;
	}
	private void doAufraeumen(){
		ListenerTools.removeListeners(jb[0]);
		ListenerTools.removeListeners(jb[1]);
		for(int i = 0; i < 4; i++){
			ListenerTools.removeListeners(jrb[i]);
		}
		ListenerTools.removeListeners(this);
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String cmd = arg0.getActionCommand();
		if(cmd.equals("uebernahme")){
			doUebernahme();
			return;
		}
		if(cmd.equals("abbrechen")){
			doAbbrechen();
			return;
		}
	}
	private void doUebernahme(){
		String[] rueck = {"K","U","A","T"};
		for(int i = 0; i < 4; i++){
			if(jrb[i].isSelected()){
				dummylab.setText(rueck[i]);
				break;
			}
		}
		doAufraeumen();
		((RezTest)getParent().getParent().getParent().getParent().getParent()).dispose();
	}
	private void doAbbrechen(){
		dummylab.setText("");
		doAufraeumen();
		((RezTest)getParent().getParent().getParent().getParent().getParent()).dispose();
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		//System.out.println("Key-Pressed "+arg0.getKeyCode());
		int code = arg0.getKeyCode();
		if(code==40){this.transferFocus();return;}
		if(code==38){this.transferFocusBackward();return;}
		if(code==10){doUebernahme();return;}
		if(code==27){doAbbrechen();return;}
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
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
