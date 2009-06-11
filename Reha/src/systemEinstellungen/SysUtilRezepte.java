package systemEinstellungen;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import systemTools.JRtaCheckBox;
import systemTools.JRtaComboBox;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SysUtilRezepte extends JXPanel implements KeyListener, ActionListener {
	
	JButton[] button = {null,null,null,null,null,null,null};
	JRtaCheckBox[] heilmittel = {null,null,null,null,null};
	JRtaComboBox voreinstellung = null;
	public SysUtilRezepte(){
		
		super(new BorderLayout());
		System.out.println("Aufruf SysUtilRezepte");
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
	     JScrollPane jscr = new JScrollPane();
	     jscr.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
	     jscr.setOpaque(false);
	     jscr.getViewport().setOpaque(false);
	     jscr.setViewportView(getVorlagenSeite());
	     jscr.getVerticalScrollBar().setUnitIncrement(15);
	     jscr.validate();
	     
	     add(jscr,BorderLayout.CENTER);
	     add(getKnopfPanel(),BorderLayout.SOUTH);
			new SwingWorker<Void,Void>(){

				@Override
				protected Void doInBackground() throws Exception {
					fuelleMitWerten();
					return null;
				}
				
			}.execute();

		return;
	}
	private void fuelleMitWerten(){
		int aktiv;
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/rezept.ini");
		for(int i = 0;i < 5;i++){
			aktiv = inif.getIntegerProperty("RezeptKlassen", "KlasseAktiv"+new Integer(i+1).toString());
			if(aktiv > 0){
				heilmittel[i].setSelected(true);
			}else{
				heilmittel[i].setSelected(false);
			}
			
		}
		voreinstellung.setSelectedItem(SystemConfig.initRezeptKlasse);
	}
	/************** Beginn der Methode für die Objekterstellung und -platzierung *********/
	private JPanel getVorlagenSeite(){

		for(int i = 0; i<5;i++){
			heilmittel[i] = new JRtaCheckBox();
		}
		voreinstellung = new JRtaComboBox(SystemConfig.rezeptKlassen);
			
		//                                      1.             2.     3.     4.     5.     6.    7. 
		FormLayout lay = new FormLayout("right:max(120dlu;p), 20dlu, 40dlu, 70dlu, 4dlu, 10dlu,0dlu",
       //1.    2. 3.   4.   5.   6.  7.   8.  9.  10.  11. 12.  13.  14.  15. 16.  17. 18.  19.   20.    21.   22.   23.
		"p, 2dlu, p,  2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p,  10dlu ,10dlu, 10dlu, p");
		
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		builder.addLabel("Krezen Sie an welche Praxisart(en) Sie betreiben", cc.xyw(1, 1,6));
		builder.addLabel("Physio-Praxis", cc.xyw(4, 3, 2));
		builder.add(heilmittel[0], cc.xy(6, 3, CellConstraints.RIGHT, CellConstraints.BOTTOM));
		builder.addLabel("Massage-Praxis", cc.xyw(4, 5, 2));
		builder.add(heilmittel[1], cc.xy(6, 5, CellConstraints.RIGHT, CellConstraints.BOTTOM));
		builder.addLabel("Ergo-Praxis", cc.xyw(4, 7, 2));
		builder.add(heilmittel[2], cc.xy(6, 7, CellConstraints.RIGHT, CellConstraints.BOTTOM));
		builder.addLabel("Logo-Praxis", cc.xyw(4, 9, 2));
		builder.add(heilmittel[3], cc.xy(6, 9, CellConstraints.RIGHT, CellConstraints.BOTTOM));
		builder.addLabel("Reha-Zentrum", cc.xyw(4, 11, 2));
		builder.add(heilmittel[4], cc.xy(6, 11, CellConstraints.RIGHT, CellConstraints.BOTTOM));
		
		builder.addSeparator("Voreinstellung bei Rezeptanlage", cc.xyw(1, 13, 6));

		builder.addLabel("Rezeptklasse", cc.xy(1, 15));
		builder.add(voreinstellung,cc.xyw(3,15,4));
		
		return builder.getPanel();
	}

	private JPanel getKnopfPanel(){
		
		
		button[5] = new JButton("abbrechen");
		button[5].setActionCommand("abbrechen");
		button[5].addActionListener(this);
		button[6] = new JButton("speichern");
		button[6].setActionCommand("speichern");
		button[6].addActionListener(this);		
		

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
		String cmd = e.getActionCommand();
		if(cmd.equals("abbrechen")){
			SystemUtil.abbrechen();
			SystemUtil.thisClass.parameterScroll.requestFocus();
		}
		if(cmd.equals("speichern")){
			System.out.println("Es wird abgespeichert");
			doSpeichern();
			JOptionPane.showMessageDialog(null,"Konfiguration wurden in Datei 'rezept.ini' erfolgreich gespeichert!");					

		}
		
	}
	private void doSpeichern(){
		String wert = "";
		int iwert;
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/rezept.ini");
		inif.setStringProperty("RezeptKlassen", "InitKlasse",(String)voreinstellung.getSelectedItem(),null);
		for(int i = 0; i < 5;i++){
			iwert = (heilmittel[i].isSelected() ? 1 : 0);
			inif.setIntegerProperty("RezeptKlassen", "KlasseAktiv"+new Integer(i+1).toString(),iwert,null);
			
		}
		inif.save();
		SystemConfig.RezeptInit();
	}

}
