package systemEinstellungen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import systemEinstellungen.SysUtilKrankenkasse.MyVorlagenTableModel;
import systemEinstellungen.SysUtilKrankenkasse.TitelEditor;
import systemEinstellungen.SysUtilPatient.MyDefaultTableModel;
import systemTools.JCompTools;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SysUtilFremdprogramme extends JXPanel implements KeyListener, ActionListener {
	
	
	
	JXTable progtab = null;
	MyProgTableModel modprog = new MyProgTableModel();


	JButton[] button = {null,null,null, null,null,null,null, null,null,null,null, null};
	JTextField oopfad = null;
	JTextField adobepfad = null;
	
	
	public SysUtilFremdprogramme(){
		
		super(new BorderLayout());
		System.out.println("Aufruf SysUtilFremdprogramme");
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
	     add(getKnopfPanel(),BorderLayout.SOUTH);
		return;
	}
	
	
		JButton abbruch = null;
		JButton speichern = null;
		
		private JPanel getKnopfPanel(){
			
			
			abbruch = new JButton("abbrechen");
			abbruch.setActionCommand("abbrechen");
			abbruch.addActionListener(this);
			speichern = new JButton("speichern");
			speichern.setActionCommand("speichern");
			speichern.addActionListener(this);
			
										//      1.                      2.    3.    4.     5.     6.    7.      8.     9.
			FormLayout jpanlay = new FormLayout("right:max(150dlu;p), 60dlu, 60dlu, 4dlu, 60dlu",
	       //1.    2. 3.   4.   5.   6.     7.    8. 9.  10.  11. 12. 13.  14.  15. 16.  17. 18.  19.   20.    21.   22.   23.
			"p, 10dlu, p");
			
			PanelBuilder jpan = new PanelBuilder(jpanlay);
			jpan.getPanel().setOpaque(false);		
			CellConstraints jpancc = new CellConstraints();
			
			jpan.addSeparator("", jpancc.xyw(1,1,5));
			jpan.add(abbruch, jpancc.xy(3,3));
			jpan.add(speichern, jpancc.xy(5,3));
			jpan.addLabel("�nderungen �bernehmen?", jpancc.xy(1,3));
			
			
			return jpan.getPanel();
		}
	
	/************** Beginn der Methode f�r die Objekterstellung und -platzierung *********/
	private JPanel getVorlagenSeite(){
		
		
		button[0] = new JButton("entfernen");
		button[0].setActionCommand("entfernen");
		button[0].addActionListener(this);
		button[1] = new JButton("hinzuf�gen");
		button[1].setActionCommand("hinzuf�gen");
		button[1].addActionListener(this);
		button[2] = new JButton("ausw�hlen");
		button[2].setActionCommand("oopfad");
		button[2].addActionListener(this);
		button[3] = new JButton("ausw�hlen");
		button[3].setActionCommand("adobepfad");
		button[3].addActionListener(this);
		
		progtab = new JXTable();
		oopfad = new JTextField();
		adobepfad = new JTextField();
		
        //                                      1.            2.    3.    4.     5.     6.    7.      8.     9.
		FormLayout lay = new FormLayout("right:max(80dlu;p), 4dlu, 180dlu, 4dlu, 60dlu",
       //1.    2.      3.   4.   5. 6.   7.   8. 9.    10.  11. 12. 13.  14.  15. 16.  17. 18.  19.   20.    21.   22.   23.
		"10dlu, 120dlu, 2dlu,p,2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 10dlu");
		
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		
		modprog.setColumnIdentifiers(new String[] {"Name d. Programmes","Kompletter Pfad"});
		progtab = new JXTable(modprog);
		//progtab.getColumn(0).setCellEditor(new TitelEditor());
		progtab.setSortable(false);
		JScrollPane jscrProg = JCompTools.getTransparentScrollPane(progtab);
		builder.add(jscrProg, cc.xyw(1,2,5));
		
		builder.addLabel("markiertes Programm aus Liste entfernen", cc.xyw(1,4,3, CellConstraints.RIGHT, CellConstraints.BOTTOM));
		builder.add(button[0], cc.xy(5, 4));
		builder.addLabel("Programm zu Liste hinzuf�gen", cc.xyw(1, 6, 3, CellConstraints.RIGHT, CellConstraints.BOTTOM));
		builder.add(button[1], cc.xy(5, 6));
		builder.addSeparator("systemrelevante Programme / Pfade", cc.xyw(1, 8, 5));
		builder.addLabel("Pfad zu OpenOffice", cc.xy(1, 10));
		builder.add(oopfad, cc.xy(3, 10));
		builder.add(button[2], cc.xy(5, 10));
		builder.addLabel("Pfad zu AdobeReader", cc.xy(1,12));
		builder.add(adobepfad, cc.xy(3, 12));
		builder.add(button[3],cc.xy(5, 12));
		
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
class MyProgTableModel extends DefaultTableModel{
	   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Class getColumnClass(int columnIndex) {
		 return String.class;
    }
	

 public boolean isCellEditable(int row, int col) {
 	
 	return true;
 }
	   
}
