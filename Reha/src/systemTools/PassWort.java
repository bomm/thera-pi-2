package systemTools;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXPanel;

import terminKalender.ParameterLaden;

import com.sun.star.awt.KeyModifier;

import dialoge.RehaSmartDialog;
import events.RehaTPEvent;
import events.RehaTPEventClass;

public class PassWort extends JXPanel implements KeyListener, ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -702150863186759990L;
	private JPasswordField pwTextFeld = null; 
	private JXButton pwButton = null;
	private int falscherLogin = 0;
	public static PassWort thisClass = null;
	public PassWort(){
		super();
		Reha.ProgRechte = "";
		setBorder(null);
		thisClass = this;
		setLayout(new BorderLayout());
        String ss = Reha.proghome+"icons/schluessel3.gif";
        JXHeader header = new JXHeader("Benutzer Authentifizierung",
                "In diesem Fenster geben Sie Ihr persönliches Passwort ein.\n" +
                "Abhängig vom Ihrem Passwort, haben Sie Zugang zu allen Programmteilen "+
                "die für Sie persönlich freigeschaltet wurden.\n"+
                "Noch kein Passwort? Dann geben Sie bitte das Universalpasswort 'rta' ein. \n\n" +
                "Hinweis--> Nach 3-maliger Falscheingabe wird der Administrator per Email über den fehlgeschlagenen Login-Versuch informiert.",
                new ImageIcon(ss));
        this.add(header,BorderLayout.NORTH);
		
		
		//JXPanel jgrid = new JXPanel(new GridBagLayout());
		JXPanel jgrid = new JXPanel(new GridLayout(4,1));
		jgrid.setBorder(null);
		//jgrid.setBackgroundPainter(Reha.RehaPainter[0]);
		//jgrid.setAlpha(0.5f);
		
		jgrid.add(new JLabel(""));
		
		GridBagConstraints gridBagConstraints = new GridBagConstraints() ;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.anchor = GridBagConstraints.CENTER;
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.ipadx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1.0D;
		gridBagConstraints.weighty = 1.0D;
		gridBagConstraints.insets = new Insets(10,10,10,10);

		JXPanel jp = new JXPanel(new FlowLayout());
		jp.setBorder(null);
		//jp.setBackgroundPainter(Reha.RehaPainter[0]);
		jp.add(new JLabel("Bitte Passwort eingeben:  "));
		pwTextFeld = new JPasswordField();
		pwTextFeld.setPreferredSize(new Dimension(120,25));
		pwTextFeld.addKeyListener(this);
		jp.add(pwTextFeld);

		//jgrid.add(jp,gridBagConstraints);
		jgrid.add(jp);

		GridBagConstraints gridBagConstraints2 = new GridBagConstraints() ;
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.anchor = GridBagConstraints.CENTER;
		gridBagConstraints2.fill = GridBagConstraints.NONE;
		gridBagConstraints2.ipadx = 0;
		gridBagConstraints2.gridy = 1;
		gridBagConstraints2.weightx = 1.0D;
		gridBagConstraints2.weighty = 0.5D;
		gridBagConstraints2.insets = new Insets(10,0,10,0);
		pwButton = new JXButton("Benutzer authentifizieren");
		pwButton.setPreferredSize(new Dimension(160,25));
		pwButton.addActionListener(this);
		JXPanel butpanel = new JXPanel(new FlowLayout());
		butpanel.setBorder(null);
		butpanel.add(pwButton);
		//jgrid.add(pwButton,gridBagConstraints2);
		jgrid.add(butpanel);
		jgrid.add(new JLabel(""));
		
		this.add(jgrid,BorderLayout.CENTER);
		this.setVisible(true);
		this.addKeyListener(this);

	}

	private void passWortCheck(){
		int i,size;
		boolean check = false;
		String name="",rechte="",test=String.valueOf(pwTextFeld.getPassword());
		size = ParameterLaden.pKollegen.size();
		for(i=0;i<size;i++){
			System.out.println(ParameterLaden.pKollegen.get(i).get(1));
			if(test.equals(ParameterLaden.pKollegen.get(i).get(1))){
				name = (String) ParameterLaden.pKollegen.get(i).get(0);
				rechte = (String) ParameterLaden.pKollegen.get(i).get(2);
				//System.out.println("Rechte = "+rechte);
				Reha.ProgRechte = rechte;
				Reha.thisFrame.setTitle(Reha.Titel+Reha.Titel2+"  -->  [Benutzer: "+name+"]");
				Reha.aktUser = name;
				check = true;
				break;
			}
		}
		if(!check){
			JOptionPane.showMessageDialog(null, "Benutzer mit diesem Passwort ist nicht vorhanden\n\nVersuch "+Integer.toString(falscherLogin+1)+" von 3");
			falscherLogin = falscherLogin+1;
			pwTextFeld.requestFocus();
			if(falscherLogin==3){
				//Hier Email an Admin
				falscherLogin = 0;
				System.exit(0);
			}
		}else{
			// Korrekter Login
			this.setName(this.grundContainer().getName());
			RehaTPEvent rEvt = new RehaTPEvent(this);
			rEvt.setRehaEvent("PinPanelEvent");
			rEvt.setDetails(this.getName(),"ROT") ;
			RehaTPEventClass.fireRehaTPEvent(rEvt);	
			Reha.thisFrame.setVisible(true);
			Reha.thisClass.setzeDivider();
			Reha.thisFrame.validate();
			this.grundContainer().Schliessen();
			
		}
	}
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		//System.out.println(e);
		int code = e.getKeyCode();
		if(code==27){
			e.consume();
		}else if(code==10){
			e.consume();
			passWortCheck();
		}else if((e.getModifiers() == KeyModifier.MOD1) || (e.getModifiers() == KeyModifier.MOD2) ){
			e.consume();
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
		passWortCheck();
	}

	public RehaSmartDialog grundContainer(){
		return (RehaSmartDialog) this.getParent().getParent().getParent().getParent().getParent();		
	}
	public static void zeigen(){
		//
		thisClass.grundContainer().toFront();
		thisClass.grundContainer().setVisible(true);
	}
}
