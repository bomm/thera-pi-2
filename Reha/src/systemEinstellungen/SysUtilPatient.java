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
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import systemTools.JCompTools;
import systemTools.JRtaTextField;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SysUtilPatient extends JXPanel implements KeyListener, ActionListener {
	
		JButton[] button = {null,null,null,null,null,null,null,null,null,null,null};
		JRtaTextField vorlage = null;
		JRtaTextField[] krit = {null,null,null,null,null,null};
		JRtaTextField[] icon = {null,null,null,null,null,null};
		JXTable vorlagen = null;
		DefaultTableModel defvorlagen = new DefaultTableModel();
		JRadioButton oben = null;
		JRadioButton unten = null;
		JCheckBox optimize = null;
		ButtonGroup bgroup = new ButtonGroup();
		
	
	public SysUtilPatient(){
		super(new BorderLayout());
		System.out.println("Aufruf SysUtilPatient");
		this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 20));
		/****/
		new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {
			     Point2D start = new Point2D.Float(0, 0);
			     Point2D end = new Point2D.Float(400,500);
			     float[] dist = {0.0f, 0.5f};
			     Color[] colors = {Color.WHITE,getBackground()};
			     LinearGradientPaint p =
			         new LinearGradientPaint(start, end, dist, colors);
			     MattePainter mp = new MattePainter(p);
			     setBackgroundPainter(new CompoundPainter(mp));
				return null;
			}
			
		}.execute();
	     
	     JScrollPane jscr = new JScrollPane();
	     jscr.setBorder(null);
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
	private JPanel getKnopfPanel(){
		
	
		button[9] = new JButton("abbrechen");
		button[10] = new JButton("speichern");
		
									//      1.                      2.    3.    4.     5.     6.    7.      8.     9.
		FormLayout jpanlay = new FormLayout("right:max(126dlu;p), 60dlu, 40dlu, 4dlu, 40dlu",
       //1.    2. 3.   4.   5.   6.     7.    8. 9.  10.  11. 12. 13.  14.  15. 16.  17. 18.  19.   20.    21.   22.   23.
		"p, 10dlu, p");
		
		PanelBuilder jpan = new PanelBuilder(jpanlay);
		jpan.getPanel().setOpaque(false);		
		CellConstraints jpancc = new CellConstraints();
		
		jpan.addSeparator("", jpancc.xyw(1,1,5));
		jpan.add(button[9], jpancc.xy(3,3));
		jpan.add(button[10], jpancc.xy(5,3));
		jpan.addLabel("Änderungen übernehmen?", jpancc.xy(1,3));
		
		
		return jpan.getPanel();
	}
	private void fuelleMitWerten(){
		//hmContainer.put("Patient", inif.getIntegerProperty("Container", "StarteIn"));	
		//hmContainer.put("PatientOpti", inif.getIntegerProperty("Container", "ImmerOptimieren"));
		if(SystemConfig.hmContainer.get("Patient") == 0){
			oben.setSelected(true);
		}else{
			unten.setSelected(true);
		}
		System.out.println("Patient optimierung = "+SystemConfig.hmContainer.get("PatientOpti"));
		if(SystemConfig.hmContainer.get("PatientOpti") == 0){
			optimize.setSelected(false);
		}else{
			optimize.setSelected(true);
		}
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/patient.ini");
		int forms = inif.getIntegerProperty("Formulare", "PatientFormulareAnzahl");
		Vector<String> vec = new Vector<String>();
		for(int i = 1; i <= forms; i++){
			vec.clear();
			vec.add(inif.getStringProperty("Formulare","PFormularText"+i));
			vec.add(inif.getStringProperty("Formulare","PFormularName"+i));
			defvorlagen.addRow((Vector)vec.clone());
		}
		if(defvorlagen.getRowCount() > 0){
			vorlagen.setRowSelectionInterval(0, 0);
		}
		vorlagen.validate();



	}
	/************** Beginn der Methode für die Objekterstellung und -platzierung *********/
	private JPanel getVorlagenSeite(){
		
		oben = new JRadioButton();
		bgroup.add(oben);
		unten = new JRadioButton();
		bgroup.add(unten);
		optimize = new JCheckBox();
		defvorlagen.setColumnIdentifiers(new String[] {"Titel der Vorlage","Vorlagendatei"});
		vorlagen = new JXTable(defvorlagen);
		vorlage = new JRtaTextField("GROSS", true);
		button[6] = new JButton("entfernen");
		button[7] = new JButton("auswählen");
		button[8] = new JButton("hinzufügen");
		
		krit[0] = new JRtaTextField("", true);
		krit[1] = new JRtaTextField("", true);
		krit[2] = new JRtaTextField("", true);
		krit[3] = new JRtaTextField("", true);
		krit[4] = new JRtaTextField("", true);
		krit[5] = new JRtaTextField("", true);
		button[0] = new JButton("auswählen");
		button[1] = new JButton("auswählen");
		button[2] = new JButton("auswählen");
		button[3] = new JButton("auswählen");
		button[4] = new JButton("auswählen");
		button[5] = new JButton("auswählen");
		icon[0] = new JRtaTextField("", true);
		icon[1] = new JRtaTextField("", true);
		icon[2] = new JRtaTextField("", true);
		icon[3] = new JRtaTextField("", true);
		icon[4] = new JRtaTextField("", true);
		icon[5] = new JRtaTextField("", true);
		
										//      1.            2.     3.     4.     5.     6.    7.      8.     9.
		FormLayout lay = new FormLayout("right:max(120dlu;p), 20dlu, 40dlu, 40dlu, 4dlu, 40dlu,0dlu",
       //1.    2. 3.   4.   5.   6.   7.  8.     9.    10.  11. 12.   13.  14. 15. 16.  17. 18.  19.    20. 21.  22.  23. 24   25  26   27  28  29  30   31   32   33  34   35  36   37 38   39    40  41  42  43  44
		"p, 2dlu, p, 10dlu, p, 10dlu, p, 10dlu, 100dlu, 2dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p,  2dlu , p, 5dlu, p, 2dlu, p, 5dlu,p, 2dlu, p, 5dlu, p, 2dlu, p, 5dlu, p, 2dlu, p, 5dlu, p, 2dlu, p, 10dlu");
		
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
		builder.addSeparator("Vorlagen-Verwaltung", cc.xyw(1,7,6));
		JScrollPane jscr = JCompTools.getTransparentScrollPane(vorlagen);
		jscr.validate();
		builder.add(jscr, cc.xyw(1,9,6));
		builder.addLabel("aus Liste entfernen", cc.xy(1, 11));
		builder.add(button[6], cc.xy(6, 11));
		builder.addLabel("neue Vorlagenbezeichnung", cc.xy(1, 13));
		builder.add(vorlage, cc.xyw(3,13,4));
		builder.addLabel("Datei auswählen", cc.xy(1, 15));
		builder.addLabel("DatLabel", cc.xyw(3, 15, 2));
		builder.add(button[7], cc.xy(6, 15));
		builder.addLabel("zu Liste hinzufügen", cc.xy(1, 17));
		builder.add(button[8], cc.xy(6, 17));
		builder.addSeparator("Kriteriendefinitionen / Icons", cc.xyw(1, 19, 6));
		builder.addLabel("1. Kriterium", cc.xy(1, 21));
		builder.add(krit[0], cc.xyw(3, 21, 4));
		builder.addLabel("1. Icon", cc.xy(1, 23));
		builder.add(icon[0], cc.xyw(3, 23, 2));
		builder.add(button[0], cc.xy(6, 23));
		builder.addLabel("2. Kriterium", cc.xy(1, 25));
		builder.add(krit[1], cc.xyw(3, 25, 4));
		builder.addLabel("2. Icon", cc.xy(1, 27));
		builder.add(icon[1], cc.xyw(3, 27, 2));
		builder.add(button[1], cc.xy(6, 27));
		builder.addLabel("3. Kriterium", cc.xy(1, 29));
		builder.add(krit[2], cc.xyw(3, 29, 4));
		builder.addLabel("3. Icon", cc.xy(1, 31));
		builder.add(icon[2], cc.xyw(3, 31, 2));
		builder.add(button[2], cc.xy(6, 31));
		builder.addLabel("4. Kriterium", cc.xy(1, 33));
		builder.add(krit[3], cc.xyw(3, 33, 4));
		builder.addLabel("4. Icon", cc.xy(1, 35));
		builder.add(icon[3], cc.xyw(3, 35, 2));
		builder.add(button[3], cc.xy(6, 35));
		builder.addLabel("5. Kriterium", cc.xy(1, 37));
		builder.add(krit[4], cc.xyw(3, 37, 4));
		builder.addLabel("5. Icon", cc.xy(1, 39));
		builder.add(icon[4], cc.xyw(3, 39, 2));
		builder.add(button[4], cc.xy(6, 39));
		builder.addLabel("6. Kriterium", cc.xy(1, 41));
		builder.add(krit[5], cc.xyw(3, 41, 4));
		builder.addLabel("6. Icon", cc.xy(1, 43));
		builder.add(icon[5], cc.xyw(3, 43, 2));
		builder.add(button[5], cc.xy(6, 43));
		
		
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
