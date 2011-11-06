package verkauf;

import hauptFenster.Reha;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;

import systemEinstellungen.SystemConfig;
import systemTools.JRtaTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


public class WechselgeldDialog extends JXDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JXPanel pane = null;
	JXLabel summeLab = null, rest = null;
	JRtaTextField gegebenFeld = null;
	double summe, gegeben = 0;
	ActionListener al = null;
	FocusListener fl = null;
	KeyListener kl = null;
	DecimalFormat df = new DecimalFormat("0.00");
	
	public WechselgeldDialog(Frame owner, Point position, double summe) {
		super(owner, (JComponent)Reha.thisFrame.getGlassPane());
		this.summe = summe;
		this.activateListener();
		this.setSize(200, 150);
		this.setLocation(position);
		this.setUndecorated(true);
		this.setContentPane(getJContentPane());
		this.setName("Wechselgeld");
		this.setModal(true);
		this.setResizable(true);
		this.setzeFocus();
		this.setVisible(true);
	}
	
	private void setzeFocus(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				gegebenFeld.requestFocus();
			}
		});
	}
	
	private JPanel getJContentPane() {
		if(pane == null) {
			pane = new JXPanel();
			pane.setBorder(new EtchedBorder(Color.white, Color.gray));
			
			//			      1     2      3     4        5
			String xwerte = "5dlu, 40dlu, 5dlu, 60dlu:g, 5dlu";
			//              1   2    3   4     5  6    7   8    9
			String ywerte ="p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu:g, p, 5dlu ";
			
			FormLayout lay = new FormLayout(xwerte, ywerte);
			CellConstraints cc = new CellConstraints();
			
			pane.setLayout(lay);
			pane.setBackground(Color.white);
			
			pane.add(getJXTitledPanel(), cc.xyw(1, 1, 5));
			
			JXLabel lab = new JXLabel("Summe");
			pane.add(lab, cc.xy(2, 3));
			
			lab = new JXLabel("gegeben:");
			pane.add(lab, cc.xy(2, 5));
			
			lab = new JXLabel("Rückgeld:");
			pane.add(lab, cc.xy(2, 7));
			
			summeLab = new JXLabel(df.format(summe).replace(".", ","));
			pane.add(summeLab, cc.xy(4, 3));
			
			rest = new JXLabel("0,00");
			pane.add(rest, cc.xy(4, 7));
			
			gegebenFeld = new JRtaTextField("FL",true,"6.2","RECHTS");
			gegebenFeld.setText(df.format(summe));
			gegebenFeld.addKeyListener(kl);
			pane.add(gegebenFeld, cc.xy(4, 5));
			
			JXButton close = new JXButton("schliessen");
			close.setActionCommand("schliessen");
			close.addActionListener(al);
			pane.add(close, cc.xyw(2, 9, 3));
		
			pane.validate();
		}
		return pane;		
	}
	
	private JXTitledPanel getJXTitledPanel() {
		JXTitledPanel panel = new JXTitledPanel();
		
		JXButton close = new JXButton();
		close.setBorder(null);
		close.setOpaque(false);
		close.setPreferredSize(new Dimension(16, 16));
		close.setIcon(SystemConfig.hmSysIcons.get("rot"));
		close.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				schliessen();
			}
		});
		panel.setRightDecoration(close);
		
		panel.setTitle("Wechselgeld");
		panel.setTitleForeground(Color.white);
		panel.setBorder(null);
		return panel;
	}
	
	private void activateListener() {
		fl = new FocusListener() {

			@Override
			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void focusLost(FocusEvent arg0) {
			}
			
		};
		
		kl = new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					schliessen();
				}
				
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				
				double uebrig = Double.parseDouble(gegebenFeld.getText().replace(",", ".")) - summe;		
				rest.setText(df.format(uebrig));				
			}
			
		};
		
		al = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(arg0.getActionCommand().equals("schliessen")) {
					schliessen();
				}
				
			}
			
		};
	}
	
	private void schliessen() {
		this.setVisible(false);
	}

}
