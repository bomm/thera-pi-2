package hilfsFenster;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import rehaContainer.RehaTP;
import systemTools.JRtaTextField;
import systemTools.PassWort;
import systemTools.WinNum;
import terminKalender.TerminFenster;
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
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		//Dimension screenSize = toolkit.getScreenSize();
		//int x = (screenSize.width - rSmart.getWidth()) / 2;
		//int y = (screenSize.height - rSmart.getHeight()) / 2;
		/****************************************************************/
		JXPanel tv = TerminFenster.getThisClass().getViewPanel();
/*
		int xvp = tv.getLocationOnScreen().x+tv.getWidth();
		if((x+225+10) > xvp){
			x=x-225;
		}
		int yvp = tv.getLocationOnScreen().y+tv.getHeight();
		if(y+145 > yvp){
			y=y-145;
			
		}
*/		
		/****************************************************************/

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
		// TODO Auto-generated method stub
		//System.out.println(arg0.getSource());
		String sAktion = ((AbstractButton) arg0.getSource()).getText(); 
		/*
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
				break;
			}
			if(sAktion=="Abbruch"){
				String [] sret = {null,null};
				TerminFenster.setDialogRet(0,sret);
				rSmart.dispose();
				break;
			}
		}
		*/
		
	}
	private void zeitLoeschen(){
		jrtaf[0].setText("");
		jrtaf[1].setText("");
		jrtaf[0].setEnabled(false);
		jrtaf[1].setEnabled(false);
	}
	private void nullRet(){
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
