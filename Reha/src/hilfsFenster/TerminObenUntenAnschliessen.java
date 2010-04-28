package hilfsFenster;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

import rehaContainer.RehaTP;
import systemTools.JRtaTextField;
import terminKalender.TerminFenster;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dialoge.RehaSmartDialog;

public class TerminObenUntenAnschliessen implements KeyListener, ActionListener, FocusListener{

	JRadioButton [] jrb = {null,null,null,null};
	JXButton [] jb = {null,null};
	JRtaTextField [] jrtaf = {null,null};
	ButtonGroup jrbg = new ButtonGroup();
	RehaSmartDialog rSmart = null;
	int iAktion = 1;
	JXPanel tv;
	public TerminObenUntenAnschliessen(int x, int y){
		RehaTP jtp = new RehaTP(0); 
		jtp.setBorder(null);
		jtp.setTitle("Wohin mit dem Termin???");
		jtp.setContentContainer(getForm());
	    jtp.setVisible(true);


		rSmart = new RehaSmartDialog(null,"WohinmitTermin");
		rSmart.setModal(true);
		rSmart.setResizable(false);
		rSmart.setSize(new Dimension(225,200));
		rSmart.setPreferredSize(new Dimension(225,200));
		rSmart.getTitledPanel().setTitle("Wohin mit dem Termin???");
		rSmart.setContentPanel(jtp.getContentContainer());
		//Toolkit toolkit = Toolkit.getDefaultToolkit();
		//Dimension screenSize = toolkit.getScreenSize();
		//int x = (screenSize.width - rSmart.getWidth()) / 2;
		//int y = (screenSize.height - rSmart.getHeight()) / 2;
		/****************************************************************/
		tv = TerminFenster.getThisClass().getViewPanel();
		int xvp = tv.getLocationOnScreen().x+tv.getWidth();
		if((x+225+10) > xvp){
			x=x-225;
		}
		int yvp = tv.getLocationOnScreen().y+tv.getHeight();
		if(y+145 > yvp){
			y=y-145;
		}
		/****************************************************************/

		rSmart.setLocation(x, y);
		rSmart.pack();
		rSmart.setVisible(true);

		jrb[0].requestFocus();		
	}

	private JXPanel getForm(){
 
		FormLayout layout = 
			new FormLayout("10dlu,p,10dlu,p,2dlu,p,100dlu",
			"10dlu,p,3dlu,p,3dlu,p,3dlu,p,3dlu,p,5dlu");
			//new FormLayout("10dlu,p,4dlu,p,50dlu,p",
			//		"10dlu,p,3dlu,p,3dlu,p,3dlu,p");
		
		JXPanel xbuilder = new JXPanel();
		xbuilder.setBorder(null);
		xbuilder.setLayout(new BorderLayout());
		xbuilder.setVisible(true);
		//xbuilder.addFocusListener(this);
		xbuilder.addKeyListener(this);
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.getPanel().setBackground(Color.WHITE);
		//builder.getPanel().setPreferredSize(new Dimension(400,150));
		builder.getPanel().setOpaque(true);
		CellConstraints cc = new CellConstraints();
		jrb[0] = new JRadioButton("Termin oben anschließen");
		jrb[0].setBackground(Color.WHITE);
		jrb[0].addKeyListener(this);
		jrb[0].addActionListener(this);
		jrb[0].addFocusListener(this);
		jrbg.add(jrb[0]);
		builder.add(jrb[0],cc.xyw(2,2,6));
		jrb[1] = new JRadioButton("Termin unten anschließen");
		jrb[1].setBackground(Color.WHITE);
		jrb[1].addKeyListener(this);	
		jrb[1].addActionListener(this);
		jrb[1].addFocusListener(this);		
		jrbg.add(jrb[1]);
		builder.add(jrb[1],cc.xyw(2,4,6));
		jrb[2] = new JRadioButton("Termin auf ganzen Block ausdehnen");
		jrb[2].setBackground(Color.WHITE);
		jrb[2].addKeyListener(this);		
		jrb[2].addActionListener(this);
		jrb[2].addFocusListener(this);		
		jrbg.add(jrb[2]);
		builder.add(jrb[2],cc.xyw(2,6,6));
		jrb[3] = new JRadioButton("Startzeit manuell festlegen");
		jrb[3].setBackground(Color.WHITE);
		jrb[3].addKeyListener(this);		
		jrb[3].addActionListener(this);
		jrb[3].addFocusListener(this);		
		jrbg.add(jrb[3]);
		builder.add(jrb[3],cc.xyw(2,8,6));

		jrtaf[0] = new JRtaTextField("STUNDEN",true);
		jrtaf[0].setPreferredSize(new Dimension(25,20));
		jrtaf[0].setEnabled(false);
		builder.add(jrtaf[0],cc.xy(4,10));
		
		builder.add(new JXLabel(":"),cc.xy(5,10));
		
		jrtaf[1] = new JRtaTextField("MINUTEN",true);
		jrtaf[1].setPreferredSize(new Dimension(25,20));
		jrtaf[1].setEnabled(false);
		builder.add(jrtaf[1],cc.xy(6,10));

		builder.add(new JXLabel(""),cc.xy(2,11));
		jrb[0].setSelected(true);
		xbuilder.add(builder.getPanel(),BorderLayout.NORTH);

		layout = 
			new FormLayout("10dlu,p,25dlu,p,50dlu,p",
					"5dlu,p,10dlu,p,3dlu,p,3dlu,p,15dlu,p");
		builder = new PanelBuilder(layout);
		
		jb[0] = new JXButton("Ok");
		jb[0].addKeyListener(this);
		jb[0].addActionListener(this);
		jb[0].setPreferredSize(new Dimension (75, jb[0].getPreferredSize().height));
		builder.add(jb[0],cc.xy(2,2));		

		jb[1] = new JXButton("Abbruch");
		jb[1].addKeyListener(this);
		jb[1].addActionListener(this);
		jb[1].setPreferredSize(new Dimension (75, jb[0].getPreferredSize().height));
		builder.add(jb[1],cc.xy(4,2));		
		
		builder.add(new JXLabel(""),cc.xy(4,3));
		
		xbuilder.add(builder.getPanel(),BorderLayout.CENTER);
		return xbuilder;
	}
	private void testSelected(){
		for(int i = 0; i < 4;i++){
			if(jrb[i].isSelected()){
				iAktion = i+1;
				System.out.println("Aktion = "+iAktion);
				break;
			}
		}
	}
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode()==38){
			//auf
			((Component) e.getSource()).transferFocusBackward();
			//testSelected();
		}
		if(e.getKeyCode()==40){
			//ab
			((Component) e.getSource()).transferFocus();
			//testSelected();
		}
		if(e.getKeyCode()==10){
			String [] sret = {jrtaf[0].getText(),jrtaf[1].getText()};
			TerminFenster.setDialogRet(iAktion,sret);
			rSmart.dispose();
		}
		if(e.getKeyCode()==27){
			String [] sret = {null,null};
			TerminFenster.setDialogRet(0,sret);
			rSmart.dispose();
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		//System.out.println(arg0.getSource());
		String sAktion = ((AbstractButton) arg0.getSource()).getText(); 
		for (int i = 0 ; i < 1 ; i++){
			if( (sAktion =="Termin oben anschließen")){
				iAktion = 1;
				zeitLoeschen();
				break;
			}
			if( (sAktion== "Termin unten anschließen")){
				iAktion = 2;
				zeitLoeschen();
				break;
			}
			if( (sAktion== "Termin auf ganzen Block ausdehnen")){
				iAktion = 3;
				zeitLoeschen();
				break;
			}

			if(sAktion=="Startzeit manuell festlegen"){
				iAktion = 4;
				jrtaf[0].setEnabled(true);
				jrtaf[1].setEnabled(true);
				jrtaf[0].requestFocus();
				break;
			}
			if(sAktion=="Ok"){
				String [] sret = {jrtaf[0].getText(),jrtaf[1].getText()};
				TerminFenster.setDialogRet(iAktion,sret);
				rSmart.dispose();
				tv = null;
				break;
			}
			if(sAktion=="Abbruch"){
				String [] sret = {null,null};
				TerminFenster.setDialogRet(0,sret);
				rSmart.dispose();
				tv = null;
				break;
			}
		}
		
	}
	private void zeitLoeschen(){
		jrtaf[0].setText("");
		jrtaf[1].setText("");
		jrtaf[0].setEnabled(false);
		jrtaf[1].setEnabled(false);
	}
	
	@Override
	public void focusGained(FocusEvent arg0) {
		//System.out.println(arg0);
		if(arg0.getSource() instanceof JRadioButton){
			((AbstractButton) arg0.getSource()).setSelected(true);
			String sAktion = ((AbstractButton) arg0.getSource()).getText(); 
			for (int i = 0 ; i < 1 ; i++){
				if( (sAktion =="Termin oben anschließen")){
					iAktion = 1;
					zeitLoeschen();
					break;
				}
				if( (sAktion== "Termin unten anschließen")){
					iAktion = 2;
					zeitLoeschen();
					break;
				}
				if( (sAktion== "Termin auf ganzen Block ausdehnen")){
					iAktion = 3;
					zeitLoeschen();
					break;
				}
				if(sAktion=="Startzeit manuell festlegen"){
					iAktion = 4;
					jrtaf[0].setEnabled(true);
					jrtaf[1].setEnabled(true);
					jrtaf[0].requestFocus();
					break;
				}
			}
		}
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
