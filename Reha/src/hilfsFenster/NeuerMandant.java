package hilfsFenster;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

import rehaContainer.RehaTP;
import systemEinstellungen.SysUtilMandanten;
import systemEinstellungen.SystemConfig;
import systemTools.JRtaTextField;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dialoge.RehaSmartDialog;

public class NeuerMandant implements KeyListener, ActionListener, FocusListener{

	//JRadioButton [] jrb = {null,null,null,null};
	JXButton [] jb = {null,null};
	JRtaTextField [] jrtaf = {null,null,null};
	ButtonGroup jrbg = new ButtonGroup();
	RehaSmartDialog rSmart = null;
	boolean aktuell;
	boolean neu;
	int iAktion = 1;
	String gruppe,abwann,dauer;
	public NeuerMandant(){
		RehaTP jtp = new RehaTP(0); 
		jtp.setBorder(null);
		jtp.setTitle("Neuer Mandant");
		jtp.setContentContainer(getForm());
	    jtp.setVisible(true);
		rSmart = new RehaSmartDialog(null,"Neuer Mandant");
		rSmart.setModal(true);
		rSmart.setResizable(false);
		rSmart.setSize(new Dimension(300,175));
		rSmart.getTitledPanel().setTitle("Neuer Mandant");
		rSmart.setContentPanel(jtp.getContentContainer());
		//Toolkit toolkit = Toolkit.getDefaultToolkit();
		//Dimension screenSize = toolkit.getScreenSize();
		//int x = (screenSize.width - rSmart.getWidth()) / 2;
		//int y = (screenSize.height - rSmart.getHeight()) / 2;
		/****************************************************************/
		//JXPanel tv = TerminFenster.getThisClass().getViewPanel();

		rSmart.setLocationRelativeTo(Reha.thisFrame); 
		rSmart.setVisible(true);

		//jrb[0].requestFocus();		
	}

	private JXPanel getForm(){
 
		FormLayout layout = 
			new FormLayout("10dlu,right:max(20dlu;p),2dlu,100dlu,10dlu,p,2dlu,p,100dlu",
			"20dlu,p,3dlu,p,3dlu,p,15dlu,5dlu");
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
		builder.add(new JLabel("Name des Mandanten"),cc.xy(2,2));
		jrtaf[0] = new JRtaTextField("",true);
		//jrtaf[0].setPreferredSize(new Dimension(25,20));
		jrtaf[0].setEnabled(true);
		jrtaf[0].setText(this.gruppe);
		builder.add(jrtaf[0],cc.xy(4,2));

		builder.add(new JLabel("IK des Mandanten"),cc.xy(2,4));
		jrtaf[1] = new JRtaTextField("ZAHLEN",true);
		//jrtaf[0].setPreferredSize(new Dimension(25,20));
		jrtaf[1].setEnabled(true);
		jrtaf[1].setText(this.gruppe);
		builder.add(jrtaf[1],cc.xy(4,4));


		xbuilder.add(builder.getPanel(),BorderLayout.NORTH);

		layout = 
			new FormLayout("35dlu,p,25dlu,p,50dlu,p",
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

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode()==10){
			if(!endeRegeln()){e.consume();return;}
			SysUtilMandanten.thisClass.neuik = jrtaf[1].getText().trim();
			SysUtilMandanten.thisClass.neuname = jrtaf[0].getText().trim();
			rSmart.dispose();
		}
		if(e.getKeyCode()==27){
			e.consume();
			rSmart.dispose();
		}

	}

	private boolean endeRegeln(){
		String sret = jrtaf[0].getText().trim();
		String sret2 = jrtaf[1].getText().trim();
		for(int i = 0; i < SystemConfig.Mandanten.size();i++){
			if(SystemConfig.Mandanten.get(i)[1].trim().equals(sret)){
				JOptionPane.showMessageDialog(null,"Dieser Mandanten-Namen ist bereits vorhanden");
				jrtaf[0].requestFocus();
				return false;
			}
			if(SystemConfig.Mandanten.get(i)[0].trim().equals(sret2)){
				JOptionPane.showMessageDialog(null,"Für dieses IK wurde bereits ein Mandant angelegt");
				jrtaf[1].requestFocus();
				return false;
			}
		}
		if(sret2.equals("") || sret2.length() != 9){
			JOptionPane.showMessageDialog(null,"Angabe des IK ist ungültig (9-stellig?)");
			jrtaf[1].requestFocus();
			return false;
		}
		if(sret.equals("")){
			JOptionPane.showMessageDialog(null,"Mandanten-Namen darf nicht leer sein");
			jrtaf[1].requestFocus();
			return false;
		}
		return true;
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
			if(sAktion=="Ok"){
				if(!endeRegeln()){return;}
				SysUtilMandanten.thisClass.neuik = jrtaf[1].getText().trim();
				SysUtilMandanten.thisClass.neuname = jrtaf[0].getText().trim();
				rSmart.dispose();
				break;
			}
			if(sAktion=="Abbruch"){
				rSmart.dispose();
				break;
			}
		}
		
	}
	@Override
	public void focusGained(FocusEvent arg0) {
		//System.out.println(arg0);
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
