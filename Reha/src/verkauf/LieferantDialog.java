package verkauf;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.border.EtchedBorder;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;

import systemTools.JRtaTextField;
import verkauf.model.Lieferant;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class LieferantDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private JXButton speichern = new JXButton("speichern");

	private JRtaTextField name, ansprechpartner, anschrift, plz, ort, telefon, telefax;
	
	private ActionListener al;
	
	private KeyListener kl;
	
	private Lieferant lieferant;
	
	public LieferantDialog(int id) {
		super();
		this.activateListener();
		this.setLayout(new BorderLayout());
		this.setSize(300, 300);
		this.setUndecorated(true);
		this.add(getJXTitledPanel(), BorderLayout.NORTH);
		
		this.add(getContent(), BorderLayout.CENTER);
		if(id != -1) {
			lieferant = new Lieferant(id);
			this.lade();
		}
		this.setModal(true);
		this.setVisible(true);
	}
	
	private JXPanel getContent() {
		JXPanel pane = new JXPanel();
		this.addKeyListener(kl);
		pane.setBorder(new EtchedBorder(Color.white, Color.gray));
		
		String xwerte = "5dlu, 50dlu, 5dlu, 50dlu:g, 5dlu";
		
		String ywerte = "5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu:g, p";
		
		FormLayout lay = new FormLayout(xwerte, ywerte);
		CellConstraints cc = new CellConstraints();
		
		pane.setLayout(lay);
		
		JXLabel lab = new JXLabel("Name");
		pane.add(lab, cc.xy(2, 2));
		
		name = new JRtaTextField("nix", false);
		pane.add(name,cc.xy(4, 2));
		
		lab = new JXLabel("Ansprechpartner");
		pane.add(lab, cc.xy(2, 4));
		
		ansprechpartner = new JRtaTextField("nix", false);
		pane.add(ansprechpartner,cc.xy(4, 4));
		
		lab = new JXLabel("Anschrift");
		pane.add(lab, cc.xy(2, 6));
		
		anschrift = new JRtaTextField("nix", false);
		pane.add(anschrift,cc.xy(4, 6));
		
		lab = new JXLabel("PLZ");
		pane.add(lab, cc.xy(2, 8));
		
		plz = new JRtaTextField("nix", false);
		pane.add(plz,cc.xy(4, 8));
		
		lab = new JXLabel("Ort");
		pane.add(lab, cc.xy(2, 10));
		
		ort = new JRtaTextField("nix", false);
		pane.add(ort,cc.xy(4, 10));
		
		lab = new JXLabel("Telefon");
		pane.add(lab, cc.xy(2, 12));
		
		telefon = new JRtaTextField("nix", false);
		pane.add(telefon,cc.xy(4, 12));
		
		lab = new JXLabel("Telefax");
		pane.add(lab, cc.xy(2, 14));
		
		telefax = new JRtaTextField("nix", false);
		pane.add(telefax,cc.xy(4, 14));
		
		speichern.setActionCommand("speicher");
		speichern.addActionListener(al);
		pane.add(speichern, cc.xyw(2, 16, 3));
		
		return pane;
	}

	private JXTitledPanel getJXTitledPanel() {
		JXTitledPanel panel = new JXTitledPanel();
		
		JXButton close = new JXButton("x");
		close.setBorder(null);
		close.setOpaque(false);
		close.setPreferredSize(new Dimension(16, 16));
		close.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				schliessen();
			}
		});
		panel.setRightDecoration(close);
		
		panel.setTitle("Lieferanten");
		panel.setTitleForeground(Color.white);
		panel.setBorder(null);
		return panel;
	}
	
	private void activateListener() {
		al = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				speicher();
			}
			
		};
		
		kl = new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
					schliessen();
				} else if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					speicher();
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				
			}
			
		};
	}
	
	private void schliessen() {
		this.setVisible(false);
		this.dispose();
	}
	
	private void speicher() {
		//name, ansprechpartner, anschrift, plz, ort, telefon, telefax
		
		if(this.lieferant == null) {
			this.lieferant = new Lieferant(name.getText(), ansprechpartner.getText(), 
					anschrift.getText(), plz.getText(), ort.getText(), telefon.getText(), telefax.getText());
		} else {
			this.lieferant.setName(name.getText());
			this.lieferant.setAnsprechpartner(ansprechpartner.getText());
			this.lieferant.setAnschrift(anschrift.getText());
			this.lieferant.setPlz(plz.getText());
			this.lieferant.setOrt(ort.getText());
			this.lieferant.setTelefon(telefon.getText());
			this.lieferant.setTelefax(telefax.getText());
		}
		schliessen();
	}
	
	private void lade() {
		
		this.name.setText(this.lieferant.getName());
		this.ansprechpartner.setText(this.lieferant.getAnsprechpartner());
		this.anschrift.setText(this.lieferant.getAnschrift());
		this.plz.setText(this.lieferant.getPlz());
		this.ort.setText(this.lieferant.getOrt());
		this.telefon.setText(this.lieferant.getTelefon());
		this.telefax.setText(this.lieferant.getTelefax());
		
	}

}












