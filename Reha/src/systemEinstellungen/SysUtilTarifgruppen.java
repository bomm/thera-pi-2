package systemEinstellungen;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import org.jdesktop.swingworker.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import jxTableTools.DateTableCellEditor;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import systemEinstellungen.SysUtilKrankenkasse.MyTarifeTableModel;
import systemEinstellungen.SysUtilKrankenkasse.MyVorlagenTableModel;
import systemEinstellungen.SysUtilKrankenkasse.TitelEditor;
import systemTools.JCompTools;
import systemTools.JRtaComboBox;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SysUtilTarifgruppen extends JXPanel implements KeyListener, ActionListener {
	JButton[] button = {null,null,null,null,null,null,null};

	MyTarifeTableModel modtarife = new MyTarifeTableModel();
	JXTable tarife = null;
	JComboBox zuza = null;
	ButtonGroup bgroup = new ButtonGroup();
	String[] zzregel = null;
	String[] zzart = null;
	JRtaComboBox disziplin = null;
	
	public SysUtilTarifgruppen(){
		super(new BorderLayout());
		System.out.println("Aufruf SysUtilKalenderanlagen");
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
			new SwingWorker<Void,Void>(){

				@Override
				protected Void doInBackground() throws Exception {
					fuelleMitWerten(0);
					return null;
				}
				
			}.execute();	     
		return;
	}
	/************** Beginn der Methode f�r die Objekterstellung und -platzierung *********/
	private JPanel getVorlagenSeite(){
		
		disziplin = new JRtaComboBox(new String[] {"Physio","Massage","Ergo","Logo","REHA"});
		disziplin.setActionCommand("disziplin");
		disziplin.addActionListener(this);
		modtarife.setColumnIdentifiers(new String[] {"Tarifgruppe","Zuzahlungsregel","gültig ab","Anwendungsregel"});
		tarife = new JXTable(modtarife);
		tarife.getColumn(0).setMinWidth(120);
		tarife.getColumn(2).setMaxWidth(80);
		tarife.getColumn(3).setMinWidth(120);
		TableColumn zuzahlColumn = tarife.getColumnModel().getColumn(1);
		zzregel = new String[] {"keine Zuzahlung","gesetzl. Zuzahlung"};
		JComboBox comboBox = new JComboBox(zzregel);

		zuzahlColumn.setCellEditor(new DefaultCellEditor(comboBox));
		zuzahlColumn = tarife.getColumnModel().getColumn(3);
		zzart = new String[] {"nicht relevant","erste Behandlung >=","Rezeptdatum >=","beliebige Behandlung >=","Rezept splitten"};
		comboBox = new JComboBox(zzart);
		zuzahlColumn.setCellEditor(new DefaultCellEditor(comboBox));
		TableColumn neuPreisDat = tarife.getColumnModel().getColumn(2);
		neuPreisDat.setCellEditor(new DateTableCellEditor());
		//neuPreisDat.setCellEditor(new DatumTableCellEditor());
		tarife.setSortable(false);
		
		
        //                                      1.            2.     3.     4.     5.     6.    7.      8.     9.
		FormLayout lay = new FormLayout("right:max(120dlu;p), 20dlu, 40dlu, 40dlu, 4dlu, 80dlu",
       //1.    2. 3.   4.  5.   6.   7.   8.     9.    10.  11.    12.   13.  14.   15.   16.    17.   18.  19.  20.    21.    22.   23.   24     25    26    27  28   29
		"p, 2dlu, p, 10dlu,p, 10dlu, p, 10dlu, 140dlu, 0dlu, 0dlu,  0dlu, 0dlu, 0dlu, 0dlu, 0dlu, 0dlu, 10dlu, p, 10dlu, 80dlu, 2dlu, p , 2dlu , 0dlu, 0dlu, p, 0dlu, p");
		

		
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		builder.addSeparator("Disziplin auswählen", cc.xyw(1, 3, 6));
		builder.add(disziplin, cc.xy(6,5));
		
		
		builder.addSeparator("Tarifgruppen-Verwaltung", cc.xyw(1, 7, 6));

		JScrollPane jscrPane = JCompTools.getTransparentScrollPane(tarife);
		jscrPane.getVerticalScrollBar().setUnitIncrement(15);
		jscrPane.validate();
		builder.add(jscrPane, cc.xyw(1,9, 6));
		
		
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
		FormLayout jpanlay = new FormLayout("right:max(126dlu;p), 100dlu, 40dlu, 4dlu, 40dlu",
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
	private void fuelleMitWerten(int cmbwert){
		modtarife.setRowCount(0);
		int lang = SystemConfig.vPreisGruppen.size();
		Vector vec = new Vector<String>();
		String wert = "";
		for(int i = 0;i < lang;i++){
			vec.clear();
			wert = SystemConfig.vPreisGruppen.get(i);
			vec.add(wert);
			vec.add(zzregel[SystemConfig.vZuzahlRegeln.get(i)]);
			//abpr�fen welche Disziplin!!!!
			wert = SystemConfig.vNeuePreiseAb.get(cmbwert).get(i);
			vec.add(wert);
			//abpr�fen welche Disziplin!!!!
			vec.add(zzart[SystemConfig.vNeuePreiseRegel.get(cmbwert).get(i)]);
			modtarife.addRow((Vector)vec.clone());
		}
		tarife.validate();
		
	}
	
	private void doSpeichern(){
		String wert = "";
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/kasse.ini");

		int lang = SystemConfig.vPreisGruppen.size();
		String swert = "";
		for(int i = 0;i<lang;i++){
			//Preisgruppen-Name
			swert = new String((String) tarife.getValueAt(i, 0));
			SystemConfig.vPreisGruppen.set(i, swert);
			inif.setStringProperty("PreisGruppen", "PGName"+(i+1),swert , null);
			
			//Zuzahlregel-einstellen
			swert = new String((String) tarife.getValueAt(i, 1));
			int zzreg = stringPosErmitteln(zzregel,swert);
			SystemConfig.vZuzahlRegeln.set(i, zzreg);
			inif.setIntegerProperty("ZuzahlRegeln", "ZuzahlRegel"+(i+1),zzreg , null);
			
			//Preisver�nderung-einstellen
			/**********
			 * Abpr�fen welche Disziplin
			 */
			swert = new String((String) tarife.getValueAt(i, 2));
			if(swert.equals(".  .")){
				swert="";
			}
			((Vector)SystemConfig.vNeuePreiseAb.get(disziplin.getSelectedIndex())).set(i, swert);
			//Vergessen!!!!!!
			inif.setStringProperty("PreisRegeln", "Preis"+(String)disziplin.getSelectedItem()+"Ab"+(i+1),swert , null);
			
			//Splittingregel-einstellen
			swert = new String((String) tarife.getValueAt(i, 3));
			zzreg = stringPosErmitteln(zzart,swert);
			((Vector)SystemConfig.vNeuePreiseRegel.get(disziplin.getSelectedIndex())).set(i, zzreg);
			inif.setIntegerProperty("PreisRegeln", "Preis"+(String)disziplin.getSelectedItem()+"Regel"+(i+1),zzreg , null);
			inif.save();
		}
	}
	private int stringPosErmitteln(String[] str,String vergleich){
		for(int i = 0;i < str.length;i++){
			if(str[i].equals(vergleich)){
				return i;
			}
		}
		return -1;
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
		String cmd = e.getActionCommand();
		if(cmd.equals("disziplin")){
			this.fuelleMitWerten(disziplin.getSelectedIndex());
			return;
		}
		if(cmd.equals("speichern")){
			doSpeichern();
			return;
		}
		if(cmd.equals("abbrechen")){
			return;
		}
	}
	
	class MyTarifeTableModel extends DefaultTableModel{
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
	/**********************************************/
	/**********************************************/
	class TitelEditor extends AbstractCellEditor implements TableCellEditor{
		Object value;
		JComponent component = new JFormattedTextField();
	   public TitelEditor(){
		   //component = new JRtaTextField("NIX",true);
		   System.out.println("editor-Component wurde initialisiert");
		   component.addKeyListener(new KeyAdapter(){
			   public void keyPressed(KeyEvent arg0) {
					System.out.println("********Button in KeyPressed*********");	
					if(arg0.getKeyCode()== 10){
						arg0.consume();
						stopCellEditing();
					}
			   }
		   });
	    }
		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			((JFormattedTextField)component).setText((String)value);
			((JFormattedTextField)component).setCaretPosition(0);
			return component;
		}

		@Override
		public Object getCellEditorValue() {
			// TODO Auto-generated method stub
			return  ((JFormattedTextField)component).getText();
		}


		public boolean isCellEditable(EventObject anEvent) {
			if(anEvent instanceof MouseEvent)
	          {
	             MouseEvent me = (MouseEvent)anEvent;
	             if(me.getClickCount() != 2){
	            	 return false;
	             }
	          }
			System.out.println("isCellEditable");
			return true;
		}


		@Override
		public boolean shouldSelectCell(EventObject anEvent) {
			System.out.println("in schouldCellSelect"+anEvent);
			return super.shouldSelectCell(anEvent);
		}

		@Override
		public boolean stopCellEditing() {
			value = ((JFormattedTextField) component).getText();
			System.out.println("in stopCellediting");
			super.stopCellEditing();
			return true;
		}
		public boolean startCellEditing() {
	        return false;//super.startCellEditing();//false;
		}

		
	}
	


}
