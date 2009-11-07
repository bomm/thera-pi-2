package hilfsFenster;

import java.awt.BorderLayout;
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
import org.jdesktop.swingx.JXPanel;

import rehaContainer.RehaTP;
import systemTools.JRtaTextField;
import dialoge.RehaSmartDialog;

public class DruckFensterDialog implements KeyListener, ActionListener, FocusListener{

	JRadioButton [] jrb = {null,null,null,null};
	JXButton [] jb = {null,null};
	JRtaTextField [] jrtaf = {null,null};
	ButtonGroup jrbg = new ButtonGroup();
	RehaSmartDialog rSmart = null;
	int iAktion = 1;
	public DruckFensterDialog(int x, int y){
		RehaTP jtp = new RehaTP(0); 
		jtp.setBorder(null);
		jtp.setTitle("Bitte warten...");
		jtp.setContentContainer(getForm());
	    jtp.setVisible(true);


		rSmart = new RehaSmartDialog(null,"DruckFenster");
		rSmart.setModal(false);
		rSmart.setResizable(false);
		rSmart.setSize(new Dimension(225,200));
		rSmart.getTitledPanel().setTitle("DruckFenster");
		rSmart.setContentPanel(jtp.getContentContainer());

		rSmart.setLocation(x, y); 
		rSmart.setVisible(true);
		
	}
	public void schliessen(){
		this.rSmart.dispose();
	}

	public void zeigen(){
		this.rSmart.setVisible(true);
		this.rSmart.toFront();
	}

	private JXPanel getForm(){
 
		JXPanel xbuilder = new JXPanel();
		xbuilder.setBorder(null);
		xbuilder.setLayout(new BorderLayout());
		xbuilder.setVisible(true);
		xbuilder.addKeyListener(this);
		return xbuilder;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
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
	
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
