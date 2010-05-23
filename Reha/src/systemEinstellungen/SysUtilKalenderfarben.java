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
import java.util.Arrays;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import jxTableTools.ColorEditor;
import jxTableTools.ColorRenderer;
import jxTableTools.JLabelRenderer;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.LabelProvider;
import org.jdesktop.swingx.renderer.StringValue;

import terminKalender.TerminFenster;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SysUtilKalenderfarben extends JXPanel implements KeyListener, ActionListener {
	
	JXTable FarbTab = null;
	JComboBox standard = null;
	JComboBox alphawahl = null;
	JButton knopf1 = null;
	JButton knopf2 = null;
	Vector columnData = new Vector();
	String colorset = null;
	JButton defaultSave = null;
	KalenderFarbenModel ftm;
	JScrollPane jscroll = null;	
	static String colorini = Reha.proghome+"ini/"+Reha.aktIK+"/color.ini";	
	public SysUtilKalenderfarben(){
		super(new GridLayout(1,1));
		//System.out.println("Aufruf SysUtilKalenderfarben");
		this.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 0));
		/****/
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("SystemInit"));
		/****/
		jscroll = new JScrollPane();
		jscroll.setOpaque(false);
		jscroll.getViewport().setOpaque(false);
		jscroll.setBorder(null);
		jscroll.getVerticalScrollBar().setUnitIncrement(15);
		jscroll.setViewportView(getVorlagenSeite());
		jscroll.validate();
		add(jscroll);	     
	     
//	     add(getVorlagenSeite());
	     validate();
	     //setVisible(true);
		return;
	}
	/************** Beginn der Methode f�r die Objekterstellung und -platzierung *********/
	private JPanel getVorlagenSeite(){
		knopf1 = new JButton("anwenden"); 
		knopf1.setPreferredSize(new Dimension(70, 20));
		knopf1.addActionListener(this);
		knopf1.setActionCommand("anwenden");
		knopf1.addKeyListener(this);

		knopf2 = new JButton("abbrechen");
		knopf2.setPreferredSize(new Dimension(70, 20));
		knopf2.addActionListener(this);
		knopf2.setActionCommand("abbrechen");
		knopf2.addKeyListener(this);
		
		//String[] colorset = {"akt. Einstellung", "Neutral", "OpenSea", "Freak", "Sundown"};
		int lang = SystemConfig.vSysDefNamen.size();
		standard = new JComboBox();
		standard.addItem("akt. Einstellung");
		for(int i = 0;i<lang;i++){
			standard.addItem(SystemConfig.vSysDefNamen.get(i));
		}
		standard.setSelectedIndex(0);
		standard.setActionCommand("defwechsel");
		standard.addActionListener(this);
		String[] alf = {"0.1","0.2","0.3","0.4","0.5","0.6","0.7","0.8","0.9","1.0"};
		alphawahl = new JComboBox(alf);
		alphawahl.setSelectedItem(new Float(SystemConfig.KalenderAlpha).toString());
		alphawahl.setActionCommand("alpha");
		alphawahl.addActionListener(this);
		
		FarbTab = new JXTable();
		
		ftm = new KalenderFarbenModel();
//		SwingUtilities.invokeLater(new Runnable(){
		 	   //public  void run()
		 	   //{
					String[] dat = {"Code","Bedeutung","Hintergrund","Schriftfarbe", "Darstellung"};
					ftm.setDataVector((Vector)SystemConfig.vSysColDlg.clone(), new Vector(Arrays.asList(dat))/*getColumnData(dat)*/);
					FarbTab.setModel(ftm);
					FarbTab.getColumn(2).setCellEditor( new ColorEditor());
					FarbTab.getColumn(2).setCellRenderer( new ColorRenderer(true));
					FarbTab.getColumn(3).setCellEditor( new ColorEditor());
					FarbTab.getColumn(3).setCellRenderer( new ColorRenderer(true));
					FarbTab.getColumn(4).setCellRenderer(new JLabelRenderer() );
					FarbTab.setSortable(false);
					FarbTab.validate();

//		 	   }
		//}); 	   

		

		JScrollPane listscr = new JScrollPane(FarbTab);
		listscr.setOpaque(true);
		listscr.getViewport().setOpaque(true);
		listscr.validate();
		//                                1.    2.    3.    4.     5.     6.    7.      8.     9.
		FormLayout lay = new FormLayout("p:g, 24dlu, 56dlu",
		//1.    2.   3.     4.      5.   6.   7.   8.  9.     10.    11.  12. 13.  14.  15.  16.  17. 18.  19.   20.    21.   22.   23.
		"10dlu, p, 10dlu, 70dlu:g, 10dlu,p, 10dlu, p, 20dlu, 10dlu, 10dlu, p, 4dlu, p, 4dlu, p");
	
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		
		//builder.getPanel().setVisible(true);
		//builder.getPanel().validate();
		
		builder.addLabel("Farbset zur Bearbeitung auswählen", cc.xy(1, 2));
		builder.add(standard, cc.xy(3, 2));
		
		builder.add(listscr, cc.xyw(1, 4, 3));
		
		builder.addLabel("Bearbeitung abbrechen, bisheriges Farbschema bleibt erhalten", cc.xy(1,6));
		builder.add(knopf2, cc.xy(3, 6));
		
		builder.addLabel("Farbschema auf Kalender anwenden", cc.xy(1, 8));
		builder.add(knopf1, cc.xy(3,8));
		
		builder.addSeparator("Optional: Transparenz einstellen", cc.xyw(1, 10, 3));
		
		builder.addLabel("Transparenz wählen", cc.xy(1,12));
		builder.add(alphawahl, cc.xy(3, 12));
		builder.addLabel("Der eingestellte Wert ist sofort im Kalender sichtbar.", cc.xy(1, 14));
		/*****************************/
		// dieser Knopf nur solange bis die Defaults entwickelt wurden
		/*
		defaultSave  = new JButton("eingestelltes Default abspeichern nur zur Entwicklung");
		defaultSave.setActionCommand("defaultsave");
		defaultSave.addActionListener(this);
		builder.add(defaultSave, cc.xyw(1, 16,3));
		*/
		return builder.getPanel();

	
	}
	private Vector getColumnData(String[] datx){
		Vector col = new Vector();
		for(int i= 0;i<datx.length;i++){
			col.add(datx[i]);
		}
		return col;
	}
		
	
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyPressed(KeyEvent e) {
	// TODO Auto-generated method stub
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		int i;
		for(i=0;i<1;i++){
			if(e.getActionCommand().equals("alpha")){
				TerminFenster.setDurchlass(new Float((String)alphawahl.getSelectedItem()) );
				break;
			}
			if(e.getActionCommand().equals("defwechsel")){
				setColorData(standard.getSelectedIndex());
				break;
			}
			if(e.getActionCommand().equals("defaultsave")){
				SwingUtilities.invokeLater(new Runnable(){
				 	   public  void run()
				 	   {
				 		   new Thread(){
				 			   public void run(){
				 				  saveColorData(standard.getSelectedIndex());	   
				 			   }
				 		   }.start();
				 	   }
				});
				break;
			}
			if(e.getActionCommand().equals("anwenden")){
				SwingUtilities.invokeLater(new Runnable(){
				 	   public  void run()
				 	   {
				 		   new Thread(){
				 			   public void run(){
				 				  saveColorData(0);	   
				 			   }
				 		   }.start();
				 	   }
				});
				break;
			}
			if(e.getActionCommand().equals("abbrechen")){
				SystemUtil.abbrechen();
				SystemUtil.thisClass.parameterScroll.requestFocus();
			}


		}	
		
	}
	private void saveColorData(int def){
		String defName = ((String)standard.getSelectedItem()).trim();
		int defNum = standard.getSelectedIndex();
		int lang = SystemConfig.vSysColsNamen.size();
		Color hg,vg;
		String farbsplit;
		if(def==0){
			defName = "UserFarben";
		}
		//setStringProperty
		INIFile ini = new INIFile(colorini);
		for(int i = 0; i<lang;i++){
			hg = ((Color)FarbTab.getValueAt(i, 2));
			vg = ((Color)FarbTab.getValueAt(i, 3));
			farbsplit = new Integer(hg.getRed()).toString()+","+new Integer(hg.getGreen()).toString()+","+
			new Integer(hg.getBlue()).toString()+","+
			new Integer(vg.getRed()).toString()+","+new Integer(vg.getGreen()).toString()+","+
			new Integer(vg.getBlue()).toString();
			ini.setStringProperty(defName,SystemConfig.vSysColsNamen.get(i) , farbsplit,null);
			ini.setStringProperty("Terminkalender","FarbenBedeutung"+(i+1),((String)FarbTab.getValueAt(i, 1)),null);
			SystemConfig.vSysColsObject.get(def).set(i, new Color[] {hg,vg});
			//vSysColsBedeut.add(new String(colini.getStringProperty("Terminkalender","FarbenBedeutung"+(i+1))));
			SystemConfig.aktTkCol.put(SystemConfig.vSysColsNamen.get(i), new Color[] {hg,vg});
			
		}

		/*
		for(int i = 0; i<lang;i++){
			hg = ((Color)FarbTab.getValueAt(i, 2));
			vg = ((Color)FarbTab.getValueAt(i, 3));
			farbsplit = new Integer(hg.getRed()).toString()+","+new Integer(hg.getGreen()).toString()+","+
			new Integer(hg.getBlue()).toString()+","+
			new Integer(vg.getRed()).toString()+","+new Integer(vg.getGreen()).toString()+","+
			new Integer(vg.getBlue()).toString();
			RWJedeIni.schreibeIniDatei(Reha.proghome+"ini\\color.ini", 
					defName,SystemConfig.vSysColsNamen.get(i) , farbsplit);
			RWJedeIni.schreibeIniDatei(Reha.proghome+"ini\\color.ini",
					"Terminkalender","FarbenBedeutung"+(i+1),((String)FarbTab.getValueAt(i, 1)) );
			SystemConfig.vSysColsObject.get(def).set(i, new Color[] {hg,vg});
			//vSysColsBedeut.add(new String(colini.getStringProperty("Terminkalender","FarbenBedeutung"+(i+1))));
			SystemConfig.aktTkCol.put(SystemConfig.vSysColsNamen.get(i), new Color[] {hg,vg});
			
		}
		*/
		
		if(def==0){
			SystemConfig.KalenderHintergrund = SystemConfig.aktTkCol.get("AusserAZ")[0];
		}
		TerminFenster.setDurchlass(new Float((String)alphawahl.getSelectedItem()) );
		ini.save();
		ini = null;
	}
	
	private void setColorData(int def){
		int lang = FarbTab.getRowCount();
		int i;
		//System.out.println("in set color Data");
		for(i=0;i<lang;i++){
			FarbTab.setValueAt(SystemConfig.vSysColsObject.get(def).get(i)[0], i, 2);	
			FarbTab.setValueAt(SystemConfig.vSysColsObject.get(def).get(i)[1], i, 3);
			FarbTab.validate();
		}
	}
	private Vector getColorData(){
		/*
		Vector vec = new Vector();
		JLabel BeispielDummi = new JLabel("so sieht's aus");		

		JLabel BeispielDummi = new JLabel("so sieht's aus");
		Vector vec = new Vector();
		Object[][]mydata = {
            {"au�er AZ", "Pause/Freizeit", new Color(111,141,223),
            new Color(111,141,223), BeispielDummi},
            {"Terminl�cke", "verf�gbar", new Color(190, 0, 4),
	        new Color(190, 0, 4), BeispielDummi},
	        {"unvollst�ndig", "ohne Rezept-Nr.", new Color(	190, 190, 0),
		    new Color(0, 0, 0), BeispielDummi},
            {"15�", "Termin 15 min", new Color(204, 204, 204),
		    new Color(25,25,25), BeispielDummi},
            {"20�", "Termin 20 min", new Color(153,153,153),
			new Color(25,25,25), BeispielDummi},
            {"25�", "Termin 25 min", new Color(102, 102, 102),
			new Color(255,255,255), BeispielDummi},
			{"30�", "Termin 30 min", new Color(0, 102, 102),
			new Color(255,255,255), BeispielDummi},
			{"45�", "Termin 45 min", new Color(102, 102, 0),
			new Color(255,255,51), BeispielDummi},
			{"50�", "Termin 50 min", new Color(255,153,255),
			new Color(0,0,0), BeispielDummi},
			{"60�", "Termin 60 min", new Color(102, 0, 102),
			new Color(255,255,51), BeispielDummi},
			{"90�", "Termin 90 min", new Color(0,0,255),
			new Color(255,255,51), BeispielDummi},
			{"Reha-Termin", "Reha-Patient", new Color(0,255,255),
			new Color(255,255,51), BeispielDummi},
			{"\\A", "", new Color(255,255,255),
			new Color(0,0,0), BeispielDummi},
			{"\\B", "", new Color(240,240,240),
			new Color(0,0,0), BeispielDummi},
			{"\\C", "", new Color(225, 225, 225),
			new Color(0,0,0), BeispielDummi},
			{"\\D", "", new Color(210,210,210),
			new Color(0,0,0), BeispielDummi},
			{"\\E", "", new Color(195,195,195),
			new Color(0,0,0), BeispielDummi},
			{"\\F", "", new Color(180,180,180),
			new Color(0,0,0), BeispielDummi},
			{"\\G", "", new Color(165,165,165),
			new Color(0,0,0), BeispielDummi},
			{"\\H", "Hausbesuch", new Color(51,255,51),
			new Color(0,0,0), BeispielDummi},
			{"\\M", "mehrere Disziplinen", new Color(77,185,77),
			new Color(0,0,0), BeispielDummi}			
		};

		
		
		for(int i = 0;i<mydata.length;i++){
			Vector ovec = new Vector();
			for(int j = 0;j<mydata[i].length;j++){
				ovec.add(mydata[i][j]);
			}
			vec.add(ovec);
		}
		*/
		/*
		public static Vector<Vector<Color[]>> hmDefaultCols;
		public static Vector<Color[]> vSysColsObject;
		public static Vector<String> vSysColsNamen;
		public static Vector<String> vSysDefNamen;
		public static Vector<String> vSysColsBedeut;
		*/
		Vector vec = new Vector();
		JLabel BeispielDummi = new JLabel("so sieht's aus");		
		int i,lang;
		lang = SystemConfig.vSysColsNamen.size();
		for(i=0;i<lang;i++){
			Vector ovec = new Vector();
			ovec.add(SystemConfig.vSysColsCode.get(i));
			ovec.add(SystemConfig.vSysColsBedeut.get(i));
			ovec.add(SystemConfig.vSysColsObject.get(0).get(i)[0]);
			ovec.add(SystemConfig.vSysColsObject.get(0).get(i)[1]);			
			ovec.add(BeispielDummi);
			vec.add(ovec.clone());
		}
		return (Vector)vec.clone();
	}

/*********Vor Ende Klassenklammer***************/	
}

class KalenderFarbenModel extends DefaultTableModel{

	public Class getColumnClass(int columnIndex) {
		   if(columnIndex >= 2 && columnIndex <=3){
			   return Color.class;
		   }else if(columnIndex ==  4){
			   return JLabel.class;			   
		   }else{
			   return String.class;
		   }
       }
	    public boolean isCellEditable(int row, int col) {
	        //Note that the data/cell address is constant,
	        //no matter where the cell appears onscreen.
	    	if(col==2){
	    		return true;
	    	}
	    	if(col==3){
	    		return true;
	    	}

	    	/*
	    	if(col==3 && (row <=1)){
	    		return false;
	    	}
	    	if(col==3 && (row > 1)){
	    		return true;
	    	}
	    	*/

	    	if(col==4 ){
	    		return false;
	    	}
	    	if(col==1 && (row >=14 && row < 22) ){
	    		return true;
	    	}

	    	return false;
	      }

}