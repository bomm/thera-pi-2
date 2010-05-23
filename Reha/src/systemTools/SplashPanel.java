package systemTools;

import hauptFenster.ProgLoader;
import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.TextPainter;

import terminKalender.ParameterLaden;

import com.sun.star.awt.KeyModifier;

import dialoge.RehaSmartDialog;
import events.RehaTPEvent;
import events.RehaTPEventClass;

public class SplashPanel extends JXPanel implements KeyListener, ActionListener{ 

	/**
	 * 
	 */
	private static final long serialVersionUID = -702150863186759990L;
	private JPasswordField pwTextFeld = null; 
	private JXButton pwButton = null;
	private int falscherLogin = 0;
	public static JXLabel status = null;
	private BufferedImage bufimg = null;
	private JXLabel legende = null;
	public  static SplashPanel thisClass = null;
	public SplashPanel(){
		super();
		
		setBorder(null);
		setLayout(new BorderLayout());
        String ss = Reha.proghome+"icons/earth.gif";
        JXHeader header = new JXHeader("Opensource-Projekt Reha-Verwaltung",
        		"\nDas Entwicklerteam:\n\n"+
        		"J�rgen Steinhilber, Alexander Gross, Daniela Barth, Steffen Rothl�nder........\n" +
        		".......\n"+
        		"hier folgen hoffentlich bald zahlreiche Namensnennungen\n\n"+ 
        		"Im Moment wird das System initialisiert\n" +
                "Bitte haben Sie etwas Geduld",
                new ImageIcon(ss));
        this.add(header,BorderLayout.NORTH);
        thisClass = this;
		
		
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

		JXPanel jp = new JXPanel(new FlowLayout(FlowLayout.LEFT));
		jp.getInsets(new Insets(20,20,20,20));
		jp.setBorder(null);
		//jp.setBackgroundPainter(Reha.RehaPainter[0]);
		legende = new JXLabel("Status der Systeminitianlisierung:");
		//legende.setForeground(Color.BLUE);
		//legende.setFont(new Font("Tahoma", Font.PLAIN, 16));
		jp.add(legende);
		//jp.add(pwTextFeld);

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
		bufimg = new BufferedImage(200, 100,
                BufferedImage.TYPE_INT_ARGB);
		status = new JXLabel();
		status.setForeground(Color.BLUE);
		status.setFont(new Font("Tahoma", Font.BOLD, 11));
		//bufimg.add(status);
		JXPanel butpanel = new JXPanel(new FlowLayout(FlowLayout.CENTER));
		butpanel.getInsets(new Insets(20,20,20,20));
		butpanel.setBorder(null);
		butpanel.add(status);
		//butpanel.add(pwButton);
		//jgrid.add(pwButton,gridBagConstraints2);
		jgrid.add(butpanel);
		jgrid.add(new JLabel(""));
		this.add(jgrid,BorderLayout.CENTER);
		this.setVisible(true);
		this.addKeyListener(this);

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
		
	}

	public RehaSmartDialog grundContainer(){
		return (RehaSmartDialog) this.getParent().getParent().getParent().getParent().getParent();		
	}

	public static void beenden(){
		thisClass.disposen();
	}
	public static void labelSetzen(String str){
		status.setText(str);
		status.repaint();
	}

	public static void zeigen(){
		thisClass.grundContainer().toFront();
		thisClass.setVisible(true);
	}

	private void disposen(){
		RehaSmartDialog rsm = grundContainer();
		rsm.setVisible(false);
		rsm.Schliessen();
		rsm = null;
		//((RehaSmartDialog)getParent().getParent().getParent().getParent().getParent()).dispose();		
	}
	
}
