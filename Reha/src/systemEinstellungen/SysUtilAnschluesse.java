package systemEinstellungen;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


public class SysUtilAnschluesse extends JXPanel implements KeyListener, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JComboBox anschluss = null;
	JComboBox baud = null;
	JComboBox datenbits = null;
	JComboBox parity = null;
	JComboBox stopbits = null;
	JButton knopf1 = null;
	JButton knopf2 = null;

	public SysUtilAnschluesse(){
	
	
	
		super(new BorderLayout());
		System.out.println("Aufruf SysUtilAnschluesse");
		this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 20));
		/****/
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
	     add(getVorlagenSeite(),BorderLayout.CENTER);
		/****/
		return;
	}
	/************** Beginn der Methode f�r die Objekterstellung und -platzierung *********/
	private JPanel getVorlagenSeite(){
	
		knopf2 = new JButton("abbrechen");
		knopf2.setPreferredSize(new Dimension(70, 20));
		knopf2.addActionListener(this);
		knopf2.setActionCommand("abbruch");
		knopf2.addKeyListener(this);
		knopf1 = new JButton("�bernahme");
		knopf1.setPreferredSize(new Dimension(70, 20));
		knopf1.addActionListener(this);
		knopf1.setActionCommand("speichern");
		knopf1.addKeyListener(this);
		
		anschluss = new JComboBox(new String[] {"COM 1", "COM 2", "COM 3", "COM 4", "COM 5", "COM 6", "COM 7", "COM 8", "COM 9", "COM 10"});
		baud = new JComboBox(new String[] {"eins", "zwei", "drei", "vier", "fuenf", "sex", "sieben" });
		datenbits = new JComboBox(new String[] {"eins", "zwei", "drei", "vier", "fuenf", "sex", "sieben" });
		parity = new JComboBox(new String[] {"eins", "zwei", "drei", "vier", "fuenf", "sex", "sieben" });
		stopbits = new JComboBox(new String[] {"eins", "zwei", "drei", "vier", "fuenf", "sex", "sieben" });
		
	
        //                                      1.            2.    3.    4.     5.     6.    7.      8.     9.
		FormLayout lay = new FormLayout("right:max(60dlu;p), 10dlu, 60dlu",
	   //1.    2.   3.   4.  5.   6. 7.  8.  9.   10.  11.  12.  13. 14.  15. 16.  17. 18.  19. 20. 21.  22.  23.  24.  25   26  27  28   29  30   31   32  33    34  35  36     37
		"p, 10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p");
		
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		
		builder.addLabel("Anschluss ausw�hlen", cc.xy(1,1));
		builder.add(anschluss, cc.xy(3,1));
		builder.addLabel("Baudrate", cc.xy(1,3));
		builder.add(baud, cc.xy(3,3));
		builder.addLabel("Datenbits", cc.xy(1,5));
		builder.add(datenbits, cc.xy(3,5));
		builder.addLabel("Parity", cc.xy(1,7));
		builder.add(parity, cc.xy(3,7));
		builder.addLabel("Stopbits", cc.xy(1,9));
		builder.add(stopbits, cc.xy(3,9));
		
		builder.addSeparator("Vorgenommene �nderungen werden erst nach �bernahme wirksam.", cc.xyw(1,11,3));
		builder.add(knopf2, cc.xy(1,13));
		builder.add(knopf1, cc.xy(3,13));
		
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
