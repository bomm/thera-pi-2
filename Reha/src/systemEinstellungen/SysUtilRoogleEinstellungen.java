package systemEinstellungen;

import hauptFenster.Reha;

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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import CommonTools.JRtaTextField;

import CommonTools.INIFile;
import CommonTools.INITool;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SysUtilRoogleEinstellungen extends JXPanel implements KeyListener, ActionListener {
	
		JButton knopf1 = null;
		JButton knopf2 = null;
		JRtaTextField range = null;
		JCheckBox montag = null;
		JCheckBox dienstag = null;
		JCheckBox mittwoch = null;
		JCheckBox donnerstag = null;
		JCheckBox freitag = null;
		JCheckBox samstag = null;
		JCheckBox sonntag = null;
		JCheckBox[] tage = {null,null,null,null,null,null,null};
		JComboBox[] zeiten = {null,null,null,null,null};
		String[] zeitentext = {"KG","MA","ER","LO","SP"};
	public SysUtilRoogleEinstellungen(){
		super(new GridLayout(1,1));
		//System.out.println("Aufruf SysUtilRoogleEinstellungen");
		this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 0));
		/****/
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("SystemInit"));
		/****/
	     add(getVorlagenSeite());
		return;
	}
	/************** Beginn der Methode f�r die Objekterstellung und -platzierung *********/
	private JPanel getVorlagenSeite(){
		String[] string = {"10","15","20","25","30","35","40","45","50","55","60","90","120"};
		montag = new JCheckBox();
		tage[0] = montag;
		dienstag = new JCheckBox();
		tage[1] = dienstag;		
		mittwoch = new JCheckBox();
		tage[2] = mittwoch;		
		donnerstag = new JCheckBox();
		tage[3] = donnerstag;
		freitag = new JCheckBox();
		tage[4] = freitag;		
		samstag = new JCheckBox();
		tage[5] = samstag;		
		sonntag = new JCheckBox();
		tage[6] = sonntag;		
		range = new JRtaTextField("ZAHLEN",true);
		range.setPreferredSize(new Dimension(70,20));
		range.setText(Integer.valueOf(SystemConfig.RoogleZeitraum).toString());
		for(int i = 0;i<7;i++){
			tage[i].setSelected(SystemConfig.RoogleTage[i]);
		}
		knopf2 = new JButton("abbrechen");
		knopf2.setPreferredSize(new Dimension(70, 20));
		knopf2.addActionListener(this);
		knopf2.setActionCommand("abbruch");
		knopf2.addKeyListener(this);
		knopf1 = new JButton("speichern");
		knopf1.setPreferredSize(new Dimension(70, 20));
		knopf1.addActionListener(this);
		knopf1.setActionCommand("speichern");
		knopf1.addKeyListener(this);
		
        //                                      1.            2.    3.          4.     5.     6.    7.      8.     9.
		FormLayout lay = new FormLayout("left:max(60dlu;p), 4dlu, right:45dlu, 4dlu, 45dlu",
       //1.    2. 3.   4.   5.   6. 7.    8. 9.  10.  11. 12. 13.  14.  15. 16.   17. 18.  19. 20.  21.  22.  23.  24.   25.  26.   27.   28   29
		"p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p,5dlu, p, 5dlu, p,  5dlu, p,  5dlu,  p , 10dlu, p  , 10dlu, p ");
		
		
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		
		builder.addLabel("voreingestellte Wochentage für Terminsuche", cc.xy(1, 1));
		builder.add(montag, cc.xy(3, 1));
		builder.addLabel("Montag", cc.xy(5, 1));
		builder.add(dienstag, cc.xy(3, 3));
		builder.addLabel("Dienstag", cc.xy(5, 3));
		builder.add(mittwoch, cc.xy(3, 5));
		builder.addLabel("Mittwoch", cc.xy(5, 5));
		builder.add(donnerstag, cc.xy(3, 7));
		builder.addLabel("Donnerstag", cc.xy(5, 7));
		builder.add(freitag, cc.xy(3,9));
		builder.addLabel("Freitag", cc.xy(5,9));
		builder.add(samstag, cc.xy(3,11));
		builder.addLabel("Samstag", cc.xy(5,11));
		builder.add(sonntag, cc.xy(3, 13));
		builder.addLabel("Sonntag", cc.xy(5,13));
		
		builder.addLabel("voreingestellter Zeitraum für Terminsuche", cc.xy(1,15));
		builder.add(range, cc.xy(3,15));
		builder.addLabel("Tage", cc.xy(5,15));
		//***********************************//
		builder.addLabel("voreingestellte Behandlungszeiten für", cc.xy(1, 17));
		builder.addLabel("Physio", cc.xy(3, 17));
		zeiten[0] = new JComboBox(string);
		zeiten[0].setSelectedItem(SystemConfig.RoogleZeiten.get("KG"));
		builder.add(zeiten[0], cc.xy(5, 17));
		builder.addLabel("Massage", cc.xy(3, 19));
		zeiten[1] = new JComboBox(string);
		zeiten[1].setSelectedItem(SystemConfig.RoogleZeiten.get("MA"));
		builder.add(zeiten[1], cc.xy(5, 19));
		builder.addLabel("Ergotherapie", cc.xy(3, 21));
		zeiten[2] = new JComboBox(string);
		zeiten[2].setSelectedItem(SystemConfig.RoogleZeiten.get("ER"));
		builder.add(zeiten[2], cc.xy(5, 21));
		builder.addLabel("Logopädie", cc.xy(3, 23));
		zeiten[3] = new JComboBox(string);
		zeiten[3].setSelectedItem(SystemConfig.RoogleZeiten.get("LO"));
		builder.add(zeiten[3], cc.xy(5, 23));
		builder.addLabel("Sporttherapie", cc.xy(3, 25));
		zeiten[4] = new JComboBox(string);
		zeiten[4].setSelectedItem(SystemConfig.RoogleZeiten.get("SP"));
		builder.add(zeiten[4], cc.xy(5, 25));
		
				
		
		
		builder.addSeparator("", cc.xyw(1, 27, 5));
		
		builder.add(knopf2, cc.xy(3,29));
		builder.add(knopf1, cc.xy(5,29));
		
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
			try{
				INIFile ini = INITool.openIni(Reha.proghome+"ini/"+Reha.aktIK+"/","terminkalender.ini");
				for(int i = 0;i<7;i++){
					ini.setStringProperty("RoogleEinstellungen", "Tag"+(i+1),(tage[i].isSelected() ? "1" : "0"),null);
					SystemConfig.RoogleTage[i] = tage[i].isSelected();
				}
				String szeit = (range.getText().trim().equals("") ? "0" : range.getText().trim()); 
				ini.setStringProperty("RoogleEinstellungen", "Zeitraum",szeit,null);
				ini.setStringProperty("RoogleEinstellungen", "KG",((String)zeiten[0].getSelectedItem()).trim(),null);
				ini.setStringProperty("RoogleEinstellungen", "MA",((String)zeiten[1].getSelectedItem()).trim(),null);
				ini.setStringProperty("RoogleEinstellungen", "ER",((String)zeiten[2].getSelectedItem()).trim(),null);
				ini.setStringProperty("RoogleEinstellungen", "LO",((String)zeiten[3].getSelectedItem()).trim(),null);
				ini.setStringProperty("RoogleEinstellungen", "SP",((String)zeiten[4].getSelectedItem()).trim(),null);
				SystemConfig.RoogleZeiten.put("KG", ((String)zeiten[0].getSelectedItem()));
				SystemConfig.RoogleZeiten.put("MA", ((String)zeiten[1].getSelectedItem()));
				SystemConfig.RoogleZeiten.put("ER", ((String)zeiten[2].getSelectedItem()));
				SystemConfig.RoogleZeiten.put("LO", ((String)zeiten[3].getSelectedItem()));
				SystemConfig.RoogleZeiten.put("SP", ((String)zeiten[4].getSelectedItem()));			
				INITool.saveIni(ini);
				SystemConfig.RoogleZeitraum = Integer.valueOf(szeit);
				JOptionPane.showMessageDialog(null,"Konfiguration wurde erfolgreich in terminkalender.ini gespeichert.");
			}catch(Exception ex){
				JOptionPane.showMessageDialog(null,"Fehler beim speichern der Konfiguration in terminkalender!!!!!");				
			}

		}
		if(e.getActionCommand().equals("abbruch")){
			SystemInit.abbrechen();
			//SystemUtil.thisClass.parameterScroll.requestFocus();
		}

	}

}
