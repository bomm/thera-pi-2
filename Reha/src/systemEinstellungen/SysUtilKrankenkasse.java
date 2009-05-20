package systemEinstellungen;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import systemTools.JRtaTextField;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SysUtilKrankenkasse extends JXPanel implements KeyListener, ActionListener {
	
	JButton[] button = {null,null,null,null,null,null,null};
	JRtaTextField newgroup = null;
	JRtaTextField newdoc = null;
	JXTable tarife = null;
	JXTable vorlagen = null;
	JRadioButton oben = null;
	JRadioButton unten = null;
	JCheckBox optimize = null;
	JComboBox zuza = null;
	
	
	
	public SysUtilKrankenkasse(){
		super(new BorderLayout());
		System.out.println("Aufruf SysUtilKasse");
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
	     
	     JScrollPane jscr = new JScrollPane();
	     jscr.setBorder(null);
	     jscr.setOpaque(false);
	     jscr.getViewport().setOpaque(false);
	     jscr.setViewportView(getVorlagenSeite());
	     jscr.getVerticalScrollBar().setUnitIncrement(15);
	     jscr.validate();
	     
	     add(jscr,BorderLayout.CENTER);
	     add(getKnopfPanel(),BorderLayout.SOUTH);
		
	     return;
	}
	private JPanel getKnopfPanel(){
		
		
		button[5] = new JButton("abbrechen");
		button[6] = new JButton("speichern");
		
									//      1.                      2.    3.    4.     5.     6.    7.      8.     9.
		FormLayout jpanlay = new FormLayout("right:max(126dlu;p), 60dlu, 40dlu, 4dlu, 40dlu",
       //1.    2. 3.   4.   5.   6.     7.    8. 9.  10.  11. 12. 13.  14.  15. 16.  17. 18.  19.   20.    21.   22.   23.
		"p, 10dlu, p");
		
		PanelBuilder jpan = new PanelBuilder(jpanlay);
		jpan.getPanel().setOpaque(false);		
		CellConstraints jpancc = new CellConstraints();
		
		jpan.addSeparator("", jpancc.xyw(1,1,5));
		jpan.add(button[5], jpancc.xy(3,3));
		jpan.add(button[6], jpancc.xy(5,3));
		jpan.addLabel("Änderungen übernehmen?", jpancc.xy(1,3));
		
		
		return jpan.getPanel();
	}
	
	/************** Beginn der Methode für die Objekterstellung und -platzierung *********/
	private JPanel getVorlagenSeite(){
		
		oben = new JRadioButton();
		unten = new JRadioButton();
		optimize = new JCheckBox();
		
		button[0] = new JButton("entfernen"); //buttons 1+2 für Gruppenverwaltung
		button[1] = new JButton("hinzufügen");
		button[2] = new JButton("entfernen"); //buttons 3-5 für Vorlagenverwaltung
		button[3] = new JButton("auswählen");
		button[4] = new JButton("hinzufügen");
		
		tarife = new JXTable();
		vorlagen = new JXTable();
		
		newgroup = new JRtaTextField("GROSS", true);
		newdoc = new JRtaTextField("GROSS", true);
		
		zuza = new JComboBox();
		
		
        //                                      1.            2.     3.     4.     5.     6.    7.      8.     9.
		FormLayout lay = new FormLayout("right:max(120dlu;p), 20dlu, 40dlu, 40dlu, 4dlu, 40dlu",
       //1.    2. 3.   4.  5.   6.   7.   8.     9.    10.  11. 12.   13.  14. 15. 16.  17. 18.    19.    20.   21.   22.   23. 24  25  26    27
		"p, 2dlu, p, 10dlu,p, 10dlu, p, 10dlu, 40dlu, 2dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, 40dlu, 2dlu, p,  10dlu , p, 2dlu, p, 2dlu, p");
		
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		
		builder.addLabel("Fenster startet im ..." , cc.xy(1, 1, CellConstraints.LEFT, CellConstraints.BOTTOM));
		builder.addLabel("oberen Container", cc.xyw(4, 1, 2, CellConstraints.RIGHT, CellConstraints.BOTTOM));
		builder.add(oben, cc.xy(6, 1, CellConstraints.RIGHT, CellConstraints.BOTTOM));
		builder.addLabel("unteren Container", cc.xyw(4, 3, 2, CellConstraints.RIGHT, CellConstraints.BOTTOM));
		builder.add(unten, cc.xy(6, 3, CellConstraints.RIGHT, CellConstraints.BOTTOM));
		builder.addLabel("Fenstergröße automatisch optimieren", cc.xy(1, 5));
		builder.add(optimize, cc.xy(6,5, CellConstraints.RIGHT, CellConstraints.BOTTOM));
		builder.addSeparator("Tarifgruppen-Verwaltung", cc.xyw(1, 7, 6));
		builder.add(tarife, cc.xyw(1,9, 6));
		builder.addLabel("aus Liste entfernen", cc.xy(1,11));
		builder.add(button[0], cc.xy(6, 11));
		builder.addLabel("neuer Gruppenname", cc.xy(1, 13));
		builder.add(newgroup, cc.xyw(3, 13, 4));
		builder.addLabel("Zuzahlungsregel der Tarifgruppe", cc.xy(1, 15));
		builder.add(zuza, cc.xyw(3,15,4));
		builder.addLabel("zu Liste hinzufügen", cc.xy(1, 17));
		builder.add(button[1], cc.xy(6, 17));
		builder.addSeparator("Vorlagen-Verwaltung", cc.xyw(1, 19, 6));
		builder.add(vorlagen, cc.xyw(1,21,6));
		builder.addLabel("aus Liste entfernen", cc.xy(1, 23));
		builder.add(button[2], cc.xy(6, 23));
		builder.addLabel("Vorlagenbezeichnung", cc.xy(1, 25));
		builder.add(newdoc, cc.xyw(3, 25, 4));
		builder.addLabel("Vorlage auswählen", cc.xy(1,27));
		builder.addLabel("Dateiname", cc.xy(3,27,CellConstraints.RIGHT, CellConstraints.BOTTOM)); 
		builder.add(button[3], cc.xy(6,27));
		builder.addLabel("zu Liste hinzufügen", cc.xy(1, 29));
		builder.add(button[4], cc.xy(6,29));
		
		
		
		
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
