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
import javax.swing.JRadioButton;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

import rehaContainer.RehaTP;
import systemEinstellungen.SysUtilGruppenDef;
import systemEinstellungen.SystemConfig;
import systemTools.JRtaTextField;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dialoge.RehaSmartDialog;

public class NeueGruppe implements KeyListener, ActionListener, FocusListener{

	//JRadioButton [] jrb = {null,null,null,null};
	JXButton [] jb = {null,null};
	JRtaTextField [] jrtaf = {null,null,null};
	ButtonGroup jrbg = new ButtonGroup();
	RehaSmartDialog rSmart = null;
	boolean aktuell;
	boolean neu;
	int iAktion = 1;
	String gruppe,abwann,dauer;
	public NeueGruppe(String titel,String gruppe,String abwann,String dauer,boolean aktuell,boolean neu){
	    this.aktuell = aktuell;
	    this.gruppe = gruppe;
	    this.abwann = abwann;
	    this.dauer = dauer;
	    this.neu = neu;
		RehaTP jtp = new RehaTP(0); 
		jtp.setBorder(null);
		jtp.setTitle(titel);
		jtp.setContentContainer(getForm());
	    jtp.setVisible(true);
		rSmart = new RehaSmartDialog(null,"GruppenAnlage");
		rSmart.setModal(true);
		rSmart.setResizable(false);
		rSmart.setSize(new Dimension(225,200));
		rSmart.getTitledPanel().setTitle(titel);
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
			new FormLayout("10dlu,right:max(20dlu;p),2dlu,60dlu,10dlu,p,2dlu,p,100dlu",
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
		builder.add(new JLabel("Gruppenname"),cc.xy(2,2));
		jrtaf[0] = new JRtaTextField("GROSS",true);
		//jrtaf[0].setPreferredSize(new Dimension(25,20));
		jrtaf[0].setEnabled(true);
		jrtaf[0].setText(this.gruppe);
		builder.add(jrtaf[0],cc.xy(4,2));
		
		builder.add(new JXLabel("Dauer im Kalender"),cc.xy(2,4));
		jrtaf[1] = new JRtaTextField("ZAHLEN",true);
		//jrtaf[1].setPreferredSize(new Dimension(25,20));
		jrtaf[1].setEnabled(true);
		jrtaf[1].setText(this.dauer);		
		builder.add(jrtaf[1],cc.xy(4,4));
		

		if(this.aktuell){
			builder.add(new JXLabel("gültig ab"),cc.xy(2,6));
		}else{
			builder.add(new JXLabel("gültig bis"),cc.xy(2,6));			
		}
		jrtaf[2] = new JRtaTextField("DATUM",false);
		//jrtaf[1].setPreferredSize(new Dimension(25,20));
		jrtaf[2].setEnabled(true);
		jrtaf[2].setText(this.abwann);		
		builder.add(jrtaf[2],cc.xy(4,6));
		

		builder.add(new JXLabel(""),cc.xy(2,8));

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

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode()==10){
			if(!endeRegeln()){e.consume();return;}
			rSmart.dispose();
		}
		if(e.getKeyCode()==27){
			e.consume();
			SysUtilGruppenDef.neuGruppenName = "";
			SysUtilGruppenDef.neuGruppenGueltigAb = "";
			SysUtilGruppenDef.neuGruppenDauer = "0";
			rSmart.dispose();
		}

	}

	private boolean endeRegeln(){
		//String [] sret = {jrtaf[0].getText(),jrtaf[1].getText()};
		if(this.neu){
			if(SystemConfig.oGruppen.gruppenNamen.contains(jrtaf[0].getText().trim())){
				JOptionPane.showMessageDialog(null,"Dieser Gruppenname ist bereits vorhanden!");
				jrtaf[0].requestFocus();
				return false;
			}
		}
		if(jrtaf[0].getText().trim().equals("")){
			JOptionPane.showMessageDialog(null,"Der Gruppenname darf nicht leer sein!");
			jrtaf[0].requestFocus();
			return false;
		}
		SysUtilGruppenDef.neuGruppenName = jrtaf[0].getText().trim();
		SysUtilGruppenDef.neuGruppenDauer = jrtaf[1].getText();
		SysUtilGruppenDef.neuGruppenGueltigAb = jrtaf[2].getText();

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
				rSmart.dispose();
				break;
			}
			if(sAktion=="Abbruch"){
				SysUtilGruppenDef.neuGruppenName = "";
				SysUtilGruppenDef.neuGruppenGueltigAb = "";
				SysUtilGruppenDef.neuGruppenDauer = "0";				
				rSmart.dispose();
				break;
			}
		}
		
	}
	@Override
	public void focusGained(FocusEvent arg0) {
		//System.out.println(arg0);
		if(arg0.getSource() instanceof JRadioButton){
			((AbstractButton) arg0.getSource()).setSelected(true);
			//String sAktion = ((AbstractButton) arg0.getSource()).getText(); 
			for (int i = 0 ; i < 1 ; i++){
			}
		}
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
