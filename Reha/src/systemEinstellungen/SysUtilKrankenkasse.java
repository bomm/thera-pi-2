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
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
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
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import jxTableTools.DateTableCellEditor;
import jxTableTools.DatumTableCellEditor;
import jxTableTools.TableTool;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import systemTools.JCompTools;
import systemTools.JRtaTextField;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SysUtilKrankenkasse extends JXPanel implements KeyListener, ActionListener {
	
	JButton[] button = {null,null,null,null,null,null,null};
	JRtaTextField newgroup = null;
	JRtaTextField newdoc = null;
	MyTarifeTableModel modtarife = new MyTarifeTableModel();
	JXTable tarife = null;
	MyVorlagenTableModel modvorlagen = new MyVorlagenTableModel();
	JXTable vorlagen = null;

	JRadioButton oben = null;
	JRadioButton unten = null;
	JCheckBox optimize = null;
	JComboBox zuza = null;
	ButtonGroup bgroup = new ButtonGroup();
	String[] zzregel = null;
	String[] zzart = null;
	boolean formok = true;
	
	/***Neuer Kommentar
	 * 
	 */
	
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
			if(!formok){
				return;
			}
			//hmContainer.put("Patient", inif.getIntegerProperty("Container", "StarteIn"));	
			//hmContainer.put("PatientOpti", inif.getIntegerProperty("Container", "ImmerOptimieren"));
			if(SystemConfig.hmContainer.get("Kasse") == 0){
				oben.setSelected(true);
			}else{
				unten.setSelected(true);
			}
			if(SystemConfig.hmContainer.get("KasseOpti") == 0){
				optimize.setSelected(false);
			}else{
				optimize.setSelected(true);
			}
			INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/kasse.ini");
			int forms = inif.getIntegerProperty("Formulare", "KassenFormulareAnzahl");
			Vector<String> vec = new Vector<String>();
			for(int i = 1; i <= forms; i++){
				vec.clear();
				vec.add(inif.getStringProperty("Formulare","KFormularText"+i));
				vec.add(inif.getStringProperty("Formulare","KFormularName"+i));
				modvorlagen.addRow((Vector)vec.clone());
			}
			if(modvorlagen.getRowCount() > 0){
				vorlagen.setRowSelectionInterval(0, 0);
			}
			vorlagen.validate();
			/*
			vPreisGruppen.add(inif.getStringProperty("PreisGruppen","PGName"+i));
			vZuzahlRegeln.add(inif.getIntegerProperty("ZuzahlRegeln","ZuzahlRegel"+i));
			vNeuePreiseAb.add(inif.getStringProperty("PreisGruppen","NeuePreiseAb"+i));
			vNeuePreiseRegel.add(inif.getIntegerProperty("PreisGruppen","NeuePreiseRegel"+i));
			*/
			//vPreisGruppen.add(inif.getStringProperty("PreisGruppen","PGName"+i));
			//vZuzahlRegeln.add(inif.getIntegerProperty("ZuzahlRegeln","ZuzahlRegel"+i));
			int lang = SystemConfig.vPreisGruppen.size();
			vec = new Vector<String>();
			String wert = "";
			for(int i = 0;i < lang;i++){
				vec.clear();
				wert = SystemConfig.vPreisGruppen.get(i);
				vec.add(wert);
				vec.add(zzregel[SystemConfig.vZuzahlRegeln.get(i)]);
				wert = SystemConfig.vNeuePreiseAb.get(i);
				vec.add(wert);
				vec.add(zzart[SystemConfig.vNeuePreiseRegel.get(i)]);
				modtarife.addRow((Vector)vec.clone());
			}
			tarife.validate();
			

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
	
	/************** Beginn der Methode für die Objekterstellung und -platzierung *********/
	private JPanel getVorlagenSeite(){
		
		oben = new JRadioButton();
		bgroup.add(oben);
		unten = new JRadioButton();
		bgroup.add(unten);
		
		optimize = new JCheckBox();
		
		button[0] = new JButton("entfernen"); //buttons 1+2 für Gruppenverwaltung
		button[1] = new JButton("hinzufügen");
		button[2] = new JButton("entfernen"); //buttons 3-5 für Vorlagenverwaltung
		button[2].setActionCommand("entfernenvorlage");
		button[2].addActionListener(this);
		
		button[3] = new JButton("auswählen");
		
		button[4] = new JButton("hinzufügen");
		button[4].setActionCommand("neuvorlage");
		button[4].addActionListener(this);
		
		modtarife.setColumnIdentifiers(new String[] {"Tarifgruppe","Zuzahlungsregel","Neue Preise ab","Anwendungsregel"});
		tarife = new JXTable(modtarife);
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
		
		modvorlagen.setColumnIdentifiers(new String[] {"Titel der Vorlage","Vorlagendatei"});
		vorlagen = new JXTable(modvorlagen);
		vorlagen.getColumn(0).setCellEditor(new TitelEditor());
		vorlagen.setSortable(false);
		
		newgroup = new JRtaTextField("GROSS", true);
		newdoc = new JRtaTextField("GROSS", true);
		
		zuza = new JComboBox();
		
		
        //                                      1.            2.     3.     4.     5.     6.    7.      8.     9.
		FormLayout lay = new FormLayout("right:max(120dlu;p), 20dlu, 40dlu, 40dlu, 4dlu, 40dlu",
       //1.    2. 3.   4.  5.   6.   7.   8.     9.    10.  11.    12.   13.  14.   15.   16.    17.   18.  19.  20.    21.    22.   23.   24     25    26    27  28   29
		"p, 2dlu, p, 10dlu,p, 10dlu, p, 10dlu, 80dlu, 0dlu, 0dlu,  0dlu, 0dlu, 0dlu, 0dlu, 0dlu, 0dlu, 10dlu, p, 10dlu, 80dlu, 2dlu, p , 2dlu , 0dlu, 0dlu, p, 0dlu, p");
		
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

		JScrollPane jscrPane = JCompTools.getTransparentScrollPane(tarife);
		jscrPane.getVerticalScrollBar().setUnitIncrement(15);
		jscrPane.validate();
		builder.add(jscrPane, cc.xyw(1,9, 6));

		//// bis hierher abschalten
		/*
		builder.addLabel("aus Liste entfernen", cc.xy(1,11));
		builder.add(button[0], cc.xy(6, 11));
		
		builder.addLabel("neuer Gruppenname", cc.xy(1, 13));
		builder.add(newgroup, cc.xyw(3, 13, 4));
		builder.addLabel("Zuzahlungsregel der Tarifgruppe", cc.xy(1, 15));
		builder.add(zuza, cc.xyw(3,15,4));
		builder.addLabel("zu Liste hinzufügen", cc.xy(1, 17));
		builder.add(button[1], cc.xy(6, 17));
		*/
		//// bis hierher abschalten

		builder.addSeparator("Vorlagen-Verwaltung", cc.xyw(1, 19, 6));

		jscrPane = JCompTools.getTransparentScrollPane(vorlagen);
		jscrPane.getVerticalScrollBar().setUnitIncrement(15);
		jscrPane.validate();
		builder.add(jscrPane, cc.xyw(1,21, 6));

		builder.addLabel("aus Liste entfernen", cc.xy(1, 23));
		builder.add(button[2], cc.xy(6, 23));
		/*
		builder.addLabel("Vorlagenbezeichnung", cc.xy(1, 25));
		builder.add(newdoc, cc.xyw(3, 25, 4));
		builder.addLabel("Vorlage auswählen", cc.xy(1,27));
		builder.addLabel("Dateiname", cc.xy(3,27,CellConstraints.RIGHT, CellConstraints.BOTTOM)); 
		builder.add(button[3], cc.xy(6,27));
		*/
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
		String cmd = e.getActionCommand();
		System.out.println(cmd);
		for(int i = 0;i < 1;i++){
			if(cmd.equals("entfernenvorlage")){
				int row = vorlagen.getSelectedRow();
				if(row >=0){
					TableTool.loescheRow(vorlagen, row);
				}
				break;
			}
			if(cmd.equals("neuvorlage")){
				setCursor(new Cursor(Cursor.WAIT_CURSOR));
				String svorlage = dateiDialog(Reha.proghome+"vorlagen/"+Reha.aktIK);
				if(svorlage.equals("")){
					return;
				}


				Vector vec = new Vector();
				vec.add("");
				vec.add(svorlage);
				modvorlagen.addRow((Vector)vec.clone());
				vorlagen.validate();
				int rows = modvorlagen.getRowCount(); 
				final int xrows = rows -1;
				SwingUtilities.invokeLater(new Runnable(){
				 	   public  void run(){
				 		  vorlagen.requestFocus();
				 		  vorlagen.setRowSelectionInterval(xrows, xrows);
				 		  startCellEditing(vorlagen,xrows);
				 	   }
				});
				break;
			}
			
			if(cmd.equals("abbrechen")){
				SystemUtil.abbrechen();
				SystemUtil.thisClass.parameterScroll.requestFocus();
			}
			if(cmd.equals("speichern")){
				System.out.println("Es wird abgespeichert");
				doSpeichern();
				if(formok){
					JOptionPane.showMessageDialog(null,"Konfiguration wurden in Datei 'kasse.ini' erfolgreich gespeichert!");					
				}

			}
			
		}

		
	}
	private void doSpeichern(){
		String wert = "";
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/kasse.ini");
		System.out.println(Reha.proghome+"ini/"+Reha.aktIK+"/kasse.ini");
		wert = (unten.isSelected() ? "1" : "0");
		SystemConfig.hmContainer.put("Kasse", new Integer(wert));
		inif.setStringProperty("Container", "StarteIn",wert , null);
		
		wert = (optimize.isSelected() ? "1" : "0");
		SystemConfig.hmContainer.put("KasseOpti",new Integer(wert));
		inif.setStringProperty("Container", "ImmerOptimieren",wert , null);

		int rows = vorlagen.getRowCount();
		
		formok = true;
		for(int i = 0;i<rows;i++){
			String test = (String)vorlagen.getValueAt(i, 0);
			if(test.equals("")){
				String datei = (String)vorlagen.getValueAt(i, 1);
				String msg = "Für Vorlagendatei "+datei+" wurde kein Titel eingegeben!\nDie Vorlagen werden nicht(!!!) gespeichert.";
				JOptionPane.showMessageDialog(null,msg);
				formok = false;
				break;
			}else{
				formok = true;
				inif.setStringProperty("Formulare", "KassenFormulareAnzahl",new Integer(rows).toString() , null);				
			}
		}
		if(formok){
			for(int i = 0;i<rows;i++){
				inif.setStringProperty("Formulare", "KFormularText"+(i+1),(String)vorlagen.getValueAt(i, 0) , null);
				inif.setStringProperty("Formulare", "KFormularName"+(i+1),(String)vorlagen.getValueAt(i, 1) , null);
			}
		}
		
		
		/********************************/
		/*
		vPreisGruppen.add(inif.getStringProperty("PreisGruppen","PGName"+i));
		vZuzahlRegeln.add(inif.getIntegerProperty("ZuzahlRegeln","ZuzahlRegel"+i));
		vNeuePreiseAb.add(inif.getStringProperty("PreisGruppen","NeuePreiseAb"+i));
		vNeuePreiseRegel.add(inif.getIntegerProperty("PreisGruppen","NeuePreiseRegel"+i));
		*/
		//vPreisGruppen.add(inif.getStringProperty("PreisGruppen","PGName"+i));
		//vZuzahlRegeln.add(inif.getIntegerProperty("ZuzahlRegeln","ZuzahlRegel"+i));
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
			
			//Preisveränderung-einstellen
			swert = new String((String) tarife.getValueAt(i, 2));
			if(swert.equals(".  .")){
				swert="";
			}
			SystemConfig.vNeuePreiseAb.set(i, swert);
			inif.setStringProperty("PreisGruppen", "NeuePreiseAb"+(i+1),swert , null);
			
			//Splittingregel-einstellen
			swert = new String((String) tarife.getValueAt(i, 3));
			zzreg = stringPosErmitteln(zzart,swert);
			SystemConfig.vNeuePreiseRegel.set(i, zzreg);
			inif.setIntegerProperty("PreisGruppen", "NeuePreiseRegel"+(i+1),zzreg , null);
			
		}
		
		inif.save();
	}	
	
	private int stringPosErmitteln(String[] str,String vergleich){
		for(int i = 0;i < str.length;i++){
			if(str[i].equals(vergleich)){
				return i;
			}
		}
		return -1;
	}
	
	private void startCellEditing(JXTable table,int row){
		final int xrows = row;
		final JXTable xtable = table;
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run(){
		 		  xtable.scrollRowToVisible(xrows);
		 				xtable.editCellAt(xrows, 0);
		 	   }
		});
	}
	
	
	
	private String dateiDialog(String pfad){
		String sret = "";
		final JFileChooser chooser = new JFileChooser("Verzeichnis wÃhlen");
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        final File file = new File(pfad);

        chooser.setCurrentDirectory(file);

        chooser.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)
                        || e.getPropertyName().equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
                    final File f = (File) e.getNewValue();
                }
            }

        });
        chooser.setVisible(true);
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        final int result = chooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File inputVerzFile = chooser.getSelectedFile();
            String inputVerzStr = inputVerzFile.getPath();
            

            if(inputVerzFile.getName().trim().equals("")){
            	sret = "";
            }else{
            	sret = inputVerzFile.getName().trim();	
            }
        }else{
        	sret = ""; //vorlagenname.setText(SystemConfig.oTerminListe.NameTemplate);
        }
        chooser.setVisible(false); 

        return sret;
	}
	
	
	class MyVorlagenTableModel extends DefaultTableModel{
		   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Class getColumnClass(int columnIndex) {
			   return String.class;
	       }

	    public boolean isCellEditable(int row, int col) {
	        if (col == 0){
	        	return true;
	        }else{
	        	return false;
	        }
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
