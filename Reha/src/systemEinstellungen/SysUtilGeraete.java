
package systemEinstellungen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SysUtilGeraete extends JXPanel implements KeyListener, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JCheckBox kvkakt = null;
	JCheckBox docscanakt = null;
	JCheckBox barcodeakt = null;
	JCheckBox ecakt = null;
	JComboBox kvkgeraet = null;
	JComboBox kvkan = null;
	JComboBox docscangeraet = null;
	JComboBox barcodegeraet = null;
	JComboBox barcodean = null;
	JComboBox ecgeraet = null;
	JComboBox ecan = null;
	
	JButton knopf1 = null;
	JButton knopf2 = null;

	public SysUtilGeraete(){
		
		
		super(new BorderLayout());
		System.out.println("Aufruf SysUtilGeraete");
		this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 20));
		/****/
	     Point2D start = new Point2D.Float(0, 0);
	     Point2D end = new Point2D.Float(400,500);
	     float[] dist = {0.0f, 0.5f};
	     Color[] colors = {Color.WHITE,getBackground()};
	     LinearGradientPaint p =
	         new LinearGradientPaint(start, end, dist, colors);
	     MattePainter mp = new MattePainter(p);
	     setBackgroundPainter(new CompoundPainter(mp));
		/****/
	     add(getVorlagenSeite(),BorderLayout.CENTER);
		return;
	}
	/************** Beginn der Methode für die Objekterstellung und -platzierung *********/
	private JPanel getVorlagenSeite(){
		
		knopf1 = new JButton("abbrechen");
		knopf1.setPreferredSize(new Dimension(70, 20));
		knopf1.addActionListener(this);
		knopf1.setActionCommand("abbruch");
		knopf1.addKeyListener(this);
		knopf2 = new JButton("speichern");
		knopf2.setPreferredSize(new Dimension(70, 20));
		knopf2.addActionListener(this);
		knopf2.setActionCommand("speichern");
		knopf2.addKeyListener(this);
		
		kvkakt = new JCheckBox();
		docscanakt = new JCheckBox();
		barcodeakt = new JCheckBox();
		ecakt = new JCheckBox();
		
		kvkgeraet = new JComboBox(new String[]{"eins","zwei","drei","vier","fuenf","sex","sieben","acht","neun"});
		kvkan = new JComboBox(new String[] {"USB", "COM 1", "COM 2", "COM 3", "COM 4", "COM 5", "COM 6", "COM 7", "COM 8", "COM 9", "COM 10"});
		docscangeraet = new JComboBox(new String[] {"eins","zwei","drei","vier","fuenf","sex","sieben","acht","neun"});
		barcodegeraet = new JComboBox(new String[] {"eins","zwei","drei","vier","fuenf","sex","sieben","acht","neun"});
		barcodean = new JComboBox(new String[] {"USB", "COM 1", "COM 2", "COM 3", "COM 4", "COM 5", "COM 6", "COM 7", "COM 8", "COM 9", "COM 10"});
		ecgeraet = new JComboBox(new String[] {"eins","zwei","drei","vier","fuenf","sex","sieben","acht","neun"});
		ecan = new JComboBox(new String[] {"USB", "COM 1", "COM 2", "COM 3", "COM 4", "COM 5", "COM 6", "COM 7", "COM 8", "COM 9", "COM 10"});
		

		
		
        //                                      1.            2.    3.      4.    5.     6.                   7.      8.     9.
		FormLayout lay = new FormLayout("right:max(60dlu;p), 10dlu, 60dlu, 10dlu, right:max(60dlu;p)",
				   //1.    2. 3.   4.  5.   6.   7.  8.    9.   10. 11.  12. 13. 14.   15. 16.  17. 18.  19. 20.   21.  22.  23.  24.  25   26  27  28   29  30    31   32  33    34  35  36     37
					"p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 10dlu, p ,10dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 10dlu, p");
		
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator("KV-Karten-Lesegerät", cc.xyw(1,1,5));
		builder.addLabel("aktivieren", cc.xy(3,3));
		builder.add(kvkakt, cc.xy(5,3,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		builder.addLabel("Gerät wählen", cc.xy(3,5));
		builder.add(kvkgeraet, cc.xy(5,5));
		builder.addLabel("Anschluss wählen", cc.xy(3,7));
		builder.add(kvkan, cc.xy(5,7));
		builder.addSeparator("Dokumentenscanner", cc.xyw(1,9,5));
		builder.addLabel("aktivieren", cc.xy(3,11));
		builder.add(docscanakt, cc.xy(5,11));
		builder.addLabel("Gerät wählen", cc.xy(3,13));
		builder.add(docscangeraet, cc.xy(5,13));
		builder.addSeparator("Barcodescanner", cc.xyw(1,15,5));
		builder.addLabel("aktivieren", cc.xy(3,17));
		builder.add(barcodeakt, cc.xy(5,17));
		builder.addLabel("Gerät wählen", cc.xy(3,19));
		builder.add(barcodegeraet, cc.xy(5,19));
		builder.addLabel("Anschluss wählen", cc.xy(3,21));
		builder.add(barcodean, cc.xy(5,21));
		builder.addSeparator("EC-Karte", cc.xyw(1,23,5));
		builder.addLabel("aktivieren",cc.xy(3,25));
		builder.add(ecakt, cc.xy(5,25));
		builder.addLabel("Gerät wählen", cc.xy(3,27));
		builder.add(ecgeraet, cc.xy(5,27));
		builder.addLabel("Anschluss wählen", cc.xy(3,29));
		builder.add(ecan, cc.xy(5,29));
		builder.addSeparator("", cc.xyw(1,31,5));
		builder.addLabel("Änderungen übernehmen?", cc.xy(1,33));
		builder.add(knopf1, cc.xy(3,33));
		builder.add(knopf2, cc.xy(5,33));
		
		return builder.getPanel();
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
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
