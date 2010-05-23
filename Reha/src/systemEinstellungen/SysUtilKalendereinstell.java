package systemEinstellungen;

import hauptFenster.Reha;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import sqlTools.ExUndHop;
import systemTools.JRtaTextField;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SysUtilKalendereinstell extends JXPanel implements KeyListener, ActionListener, FocusListener {
	
	JButton knopf1 = null;
	JButton knopf2 = null;
	JButton knopf3 = null;
	JComboBox refresh = null;
	static JProgressBar Fortschritt = null;
	JRtaTextField STD1 = null;
	JRtaTextField MIN1 = null;
	JRtaTextField STD2 = null;
	JRtaTextField MIN2 = null;
	JCheckBox scan = null;
	private boolean kalNeuAnfang = false;
	private boolean kalNeuEnde = false;
	public SysUtilKalendereinstell(){
		super(new GridLayout(1,1));
		//System.out.println("Aufruf SysUtilKalendereinstell");
		this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 20));
		/****/
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("SystemInit"));
		/****/
	     add(getVorlagenSeite());
	     SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				STD1.requestFocus();
	 	  	}
	     });			
	     

		return;
	}
	/************** Beginn der Methode f�r die Objekterstellung und -platzierung *********/
	private JPanel getVorlagenSeite(){
		
		knopf2 = new JButton("abbrechen");
		knopf2.setPreferredSize(new Dimension(70, 20));
		knopf2.addActionListener(this);
		knopf2.setActionCommand("abbruch");
		knopf2.addKeyListener(this);
		knopf1 = new JButton("anwenden");
		knopf1.setPreferredSize(new Dimension(70, 20));
		knopf1.addActionListener(this);
		knopf1.setActionCommand("speichern");
		knopf1.addKeyListener(this);
		knopf3 = new JButton("entsperren");
		knopf3.addActionListener(this);
		knopf3.setActionCommand("unlock");
		knopf3.addKeyListener(this);

		
		Fortschritt = new JProgressBar();
		
		String[] refreshtakt = {"Einzel-PC", "LAN", "DSL"};
		refresh = new JComboBox(refreshtakt);
		
		scan = new JCheckBox();
		scan.setSelected(SystemConfig.KalenderBarcode);

		
		STD1 = new JRtaTextField("STUNDEN", true);
		STD1.setText(SystemConfig.KalenderUmfang[0].substring(0,2));
		STD2 = new JRtaTextField("STUNDEN", true);
		STD2.setText(SystemConfig.KalenderUmfang[1].substring(0,2));
		MIN1 = new JRtaTextField("MINUTEN", true);
		MIN1.setText(SystemConfig.KalenderUmfang[0].substring(3,5));
		MIN1.setName("MIN1");
		//MIN1.addKeyListener(this);		
		MIN1.addFocusListener(this);
		MIN2 = new JRtaTextField("MINUTEN", true);
		MIN2.setText(SystemConfig.KalenderUmfang[1].substring(3,5));
		MIN2.setName("MIN2");
		//MIN2.addKeyListener(this);
		MIN2.addFocusListener(this);		
		
        //                                      1.            2.    3.    4.     5.     6.    7.      8.     9.
		FormLayout lay = new FormLayout("left:max(120dlu;p), 20dlu, 15dlu, 3dlu, 4dlu, 3dlu, 15dlu",
       //1.    2. 3.   4.   5.   6.   7.    8.  9.  10.  11. 12.   13.  14.  15. 16.  17. 18.  19.   20.    21.   22.   23.
		"p, 2dlu, p, 10dlu, p, 10dlu, p, 10dlu, p, 10dlu, p, 10dlu, p, 15dlu, p, 2dlu, p, 10dlu, p, 10dlu, p");

		
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		
		builder.addLabel("Tagesbeginn im Kalender", cc.xy(1, 1));
		builder.add(STD1, cc.xy(3,1));
		builder.addLabel(":", cc.xy(5, 1));
		builder.add(MIN1, cc.xy(7, 1));
		builder.addLabel("Tagesende im Kalender", cc.xy(1,3));
		builder.add(STD2, cc.xy(3,3));
		builder.addLabel(":", cc.xy(5, 3));
		builder.add(MIN2, cc.xy(7, 3));
		builder.addLabel("Refresh-Takt", cc.xy(1,5));
		builder.add(refresh, cc.xyw(3,5,5));
		builder.addLabel("Barcodescanner für Behandlungsbestätigungen", cc.xy(1,7));
		builder.add(scan, cc.xy(7,7, CellConstraints.RIGHT, CellConstraints.BOTTOM));
		builder.addSeparator("", cc.xyw(1, 9, 7));
		builder.addLabel("Abbruch ohne Übernahme", cc.xy(1, 11));
		builder.add(knopf2, cc.xyw(3, 11, 5));
		builder.addLabel("Parameter übernehmen", cc.xy(1, 13));
		builder.add(knopf1, cc.xyw(3, 13, 5));
		builder.addLabel("Fortschritt beim Verändern der Datenbank", cc.xy(1, 15, CellConstraints.LEFT, CellConstraints.BOTTOM));
		builder.add(Fortschritt, cc.xyw(1, 17, 7));
		builder.addSeparator("", cc.xyw(1,19,7));
		builder.addLabel("gesperrte Spalten freigeben", cc.xy(1,21));
		builder.add(knopf3, cc.xyw(3, 21, 5));		
	     SwingUtilities.invokeLater(new Runnable(){
				public  void run(){
					STD1.requestFocus();
		 	  	}
		     });			
		
		
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
		if(e.getActionCommand().equals("speichern")){
			if(kalNeuAnfang || kalNeuEnde){
				JOptionPane.showMessageDialog(null, "Die Funktion Kalenderzeiten verändern, wird während der Softwarentwicklung nicht aufgerufen!");
			}
			INIFile ini = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/rehajava.ini");
			ini.setStringProperty("Kalender", "KalenderBarcode",(scan.isSelected() ? "1" : "0"),null);
			ini.save();
			SystemConfig.KalenderBarcode = scan.isSelected();
		}
		if(e.getActionCommand().equals("abbruch")){
			SystemUtil.abbrechen();
			SystemUtil.thisClass.parameterScroll.requestFocus();
		}
		if(e.getActionCommand().equals("unlock")){
			String cmd = "delete from flexlock";
			new ExUndHop().setzeStatement(cmd);
			JOptionPane.showMessageDialog(null,"Sämtliche Sperren der Terminspalten wurden aufgehoben");
		}
		
	}
	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		if(((JComponent)arg0.getSource()).getName() != null){
			if(((JComponent)arg0.getSource()).getName().equals("MIN1")){
				String s1,s2,s3;
				s1 = STD1.getText().trim();
				s2 = MIN1.getText().trim();
				s3 = s1+":"+s2+":00";
				if(!s3.equals(SystemConfig.KalenderUmfang[0])){
					JOptionPane.showMessageDialog(null, "Sie haben die Kalenderanfangszeit verändert.\n\n"+
							"Für die Neuorganisation des Terminkalenders können Sie schon mal einige Kannen Kaffee kochen!");
					SwingUtilities.invokeLater(new Runnable(){
						public  void run(){
							STD2.requestFocus();
				 	  	}
					});			
					kalNeuAnfang = true;
				}else{
					kalNeuAnfang = false;					
				}
			}
			if(((JComponent)arg0.getSource()).getName().equals("MIN2")){
				String s1,s2,s3;
				s1 = STD2.getText().trim();
				s2 = MIN2.getText().trim();
				s3 = s1+":"+s2+":00";
				if(!s3.equals(SystemConfig.KalenderUmfang[1])){
					JOptionPane.showMessageDialog(null, "Sie haben die Kalenderendzeit verändert.\n\n"+
							"Für die Neuorganisation des Terminkalenders können Sie schon mal einige Kannen Kaffee kochen!");
					kalNeuEnde = true;
					SwingUtilities.invokeLater(new Runnable(){
						public  void run(){
							refresh.requestFocus();
				 	  	}
					});			
				}else{
					kalNeuEnde = false;					
				}
			}
		}
		
	}
	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}	

}
