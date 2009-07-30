
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
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import uk.co.mmscomputing.device.scanner.Scanner;
import uk.co.mmscomputing.device.scanner.ScannerIOException;

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
	String[] anschluesse = new String[] {"./.", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "COM10","USB"};
	Scanner scanner;
	public SysUtilGeraete(){
		
		
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
	     jscr.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
	     jscr.setOpaque(false);
	     jscr.getViewport().setOpaque(false);
	     jscr.setViewportView(getVorlagenSeite());
	     jscr.getVerticalScrollBar().setUnitIncrement(15);
	     jscr.validate();
	     
	     add(jscr,BorderLayout.CENTER);

	     add(getKnopfPanel(),BorderLayout.SOUTH);
		return;
	}
	/************** Beginn der Methode für die Objekterstellung und -platzierung *********/
	
private JPanel getKnopfPanel(){
		
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
	
									//      1.                      2.    3.    4.     5.     6.    7.      8.     9.
		FormLayout jpanlay = new FormLayout("right:max(120dlu;p), 60dlu, 60dlu, 10dlu, 60dlu",
       //1.    2. 3.   4.   5.   6.     7.    8. 9.  10.  11. 12. 13.  14.  15. 16.  17. 18.  19.   20.    21.   22.   23.
		"p, 10dlu, p");
		
		PanelBuilder jpan = new PanelBuilder(jpanlay);
		jpan.getPanel().setOpaque(false);		
		CellConstraints jpancc = new CellConstraints();
		
		jpan.addSeparator("Änderungen übernehmen?", jpancc.xyw(1,1,5));
		jpan.add(knopf1, jpancc.xy(3,3));
		jpan.add(knopf2, jpancc.xy(5,3));
		
		
		
		return jpan.getPanel();
	}
	
	
	
	private JPanel getVorlagenSeite(){
		
		kvkakt = new JCheckBox();
		kvkakt.setSelected((SystemConfig.sReaderAktiv.equals("1") ? true : false));

		docscanakt = new JCheckBox();
		docscanakt.setSelected((SystemConfig.hmDokuScanner.get("aktivieren").equals("1") ? true : false));
		
		barcodeakt = new JCheckBox();
		barcodeakt.setSelected((SystemConfig.sBarcodeAktiv.equals("1") ? true : false));

		ecakt = new JCheckBox();
		ecakt.setEnabled(false);
		
		kvkgeraet = new JComboBox( (String[]) SystemConfig.hmGeraete.get("Kartenleser"));
		kvkgeraet.setSelectedItem(SystemConfig.sReaderName);
		
		kvkan = new JComboBox(anschluesse);
		kvkan.setSelectedItem(SystemConfig.sReaderCom);
		
		docscangeraet = new JComboBox();
		new SwingWorker<String,String>(){
			@Override
			protected String doInBackground() throws Exception {
				 scanner = Scanner.getDevice();
				    try {
						String[] names = scanner.getDeviceNames();
						for(int i = 0; i < names.length;i++){
							docscangeraet.addItem(names[i]);
						}
						if(!scanner.getSelectedDeviceName().equals(SystemConfig.sDokuScanner)){
							docscangeraet.setSelectedItem(scanner.getSelectedDeviceName());
						}else{
							docscangeraet.setSelectedItem(SystemConfig.sDokuScanner);	
						}
						
					} catch (ScannerIOException e2) {
						e2.printStackTrace();
					}
				return null;
			}
			
		}.execute();


		barcodegeraet = new JComboBox(SystemConfig.hmGeraete.get("Barcode"));
		barcodegeraet.setSelectedItem(SystemConfig.sBarcodeScanner);

		barcodean = new JComboBox(anschluesse);
		barcodean.setSelectedItem(SystemConfig.sBarcodeCom);
		
		ecgeraet = new JComboBox(SystemConfig.hmGeraete.get("ECKarte"));
		ecgeraet.setEnabled(false);
		ecan = new JComboBox(anschluesse);
		ecan.setEnabled(false);
		
		
        //                                      1.            2.    3.      4.    5.     6.                   7.      8.     9.
		FormLayout lay = new FormLayout("right:max(100dlu;p), 10dlu, 80dlu, 10dlu, right:max(100dlu;p)",
				   //1.    2. 3.   4.  5.   6.   7.  8.    9.   10. 11.  12. 13. 14.   15. 16.  17. 18.  19. 20.   21.  22.  23.  24.  25   26  27  28   29  30    31   32  33    34  35  36     37
					"p, 10dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p ,10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p");
		
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator("KV-Karten-Lesegerät", cc.xyw(1,1,5));
		builder.addLabel("aktivieren", cc.xy(3,3));
		builder.add(kvkakt, cc.xy(5,3,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		builder.addLabel("Gerät", cc.xy(3,5));
		builder.add(kvkgeraet, cc.xyw(4,5,2));
		builder.addLabel("Anschluss", cc.xy(3,7));
		builder.add(kvkan, cc.xyw(4,7,2));
		builder.addSeparator("Dokumentenscanner", cc.xyw(1,9,5));
		builder.addLabel("aktivieren", cc.xy(3,11));
		builder.add(docscanakt, cc.xy(5,11));
		builder.addLabel("Gerät", cc.xy(3,13));
		builder.add(docscangeraet, cc.xyw(4,13,2));
		builder.addSeparator("Barcodescanner", cc.xyw(1,15,5));
		builder.addLabel("aktivieren", cc.xy(3,17));
		builder.add(barcodeakt, cc.xy(5,17));
		builder.addLabel("Gerät", cc.xy(3,19));
		builder.add(barcodegeraet, cc.xyw(4,19,2));
		builder.addLabel("Anschluss", cc.xy(3,21));
		builder.add(barcodean, cc.xyw(4,21,2));
		builder.addSeparator("EC-Karte", cc.xyw(1,23,5));
		builder.addLabel("aktivieren",cc.xy(3,25));
		builder.add(ecakt, cc.xy(5,25));
		builder.addLabel("Gerät", cc.xy(3,27));
		builder.add(ecgeraet, cc.xyw(4,27,2));
		builder.addLabel("Anschluss", cc.xy(3,29));
		builder.add(ecan, cc.xyw(4,29,2));
		
		
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
