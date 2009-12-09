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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.ParseException;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.CellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.text.DefaultFormatterFactory;

import jxTableTools.TableTool;
import jxTableTools.ZeitTableCellEditor;
import kvKarte.KVKWrapper;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import systemTools.JCompTools;
import systemTools.JRtaTextField;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SysUtilPatient extends JXPanel implements KeyListener, ActionListener, CellEditorListener {
	
		JButton[] button = {null,null,null,null,null,null,null,null,null,null,null};
		JRtaTextField vorlage = null;
		JRtaTextField[] krit = {null,null,null,null,null,null};
		JRtaTextField[] icon = {null,null,null,null,null,null};
		JXTable vorlagen = null;
		MyDefaultTableModel defvorlagen = new MyDefaultTableModel();
		JRadioButton oben = null;
		JRadioButton unten = null;
		JCheckBox optimize = null;
		ButtonGroup bgroup = new ButtonGroup();
		JLabel datLabel = null;
		JLabel[] kritlab = {null,null,null,null,null,null};
		boolean formok = true;
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
	private JPanel getKnopfPanel(){
		
	
		button[9] = new JButton("abbrechen");
		button[9].setActionCommand("abbrechen");
		button[9].addActionListener(this);
		button[10] = new JButton("speichern");
		button[10].setActionCommand("speichern");
		button[10].addActionListener(this);
		
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
		if(!formok){
			return;
		}
		//hmContainer.put("Patient", inif.getIntegerProperty("Container", "StarteIn"));	
		//hmContainer.put("PatientOpti", inif.getIntegerProperty("Container", "ImmerOptimieren"));
		if(SystemConfig.hmContainer.get("Patient") == 0){
			oben.setSelected(true);
		}else{
			unten.setSelected(true);
		}
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

		for(int i = 0;i < 9;i++){
			button[i].addActionListener(this);
		}
		for(int i = 0;i < 6;i++){
			krit[i].setText((String)SystemConfig.vPatMerker.get(i));
			String sico = ""; 
			if(SystemConfig.vPatMerkerIcon.get(i)==null){
				sico = "";
			}else{
				sico = inif.getStringProperty("Kriterien","Image"+(i+1));
				kritlab[0].setIcon(SystemConfig.vPatMerkerIcon.get(i));
			}
			icon[i].setText(sico);
			icon[i].setEditable(false);
		}
		

	}
	/************** Beginn der Methode f�r die Objekterstellung und -platzierung *********/
	private JPanel getVorlagenSeite(){
		
		oben = new JRadioButton();
		bgroup.add(oben);
		unten = new JRadioButton();
		bgroup.add(unten);
		optimize = new JCheckBox();
		defvorlagen.setColumnIdentifiers(new String[] {"Titel der Vorlage","Vorlagendatei"});
		vorlagen = new JXTable(defvorlagen);
		vorlagen.addMouseListener(new MouseAdapter(){		
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				if(arg0.getClickCount()==2 && arg0.getButton()==1){
					int row = vorlagen.getSelectedRow();
					row = vorlagen.convertRowIndexToModel(row);
					int col = vorlagen.getSelectedColumn();	
					if(col==1){
						setCursor(new Cursor(Cursor.WAIT_CURSOR));
						String svorlage = dateiDialog(Reha.proghome+"vorlagen/"+Reha.aktIK);
						if(svorlage.equals("")){
							return;
						}
						defvorlagen.setValueAt(svorlage, row, col);
						vorlagen.validate();
					}
				}
			}	
		});
		
		//vorlagen.setDefaultEditor(new Object().getClass(), new MyEditor());
		vorlagen.getColumn(0).setCellEditor((TableCellEditor) new TitelEditor());
		vorlagen.getColumn(0).getCellEditor().addCellEditorListener(this);
		vorlage = new JRtaTextField("NIX", true);
		button[6] = new JButton("entfernen");
		button[6].setActionCommand("entfernen");
		button[7] = new JButton("auswählen");
		button[7].setActionCommand("vorlagenwahl");
		button[8] = new JButton("hinzufügen");
		button[8].setActionCommand("vorlagenneu");
		
		krit[0] = new JRtaTextField("", true);
		krit[1] = new JRtaTextField("", true);
		krit[2] = new JRtaTextField("", true);
		krit[3] = new JRtaTextField("", true);
		krit[4] = new JRtaTextField("", true);
		krit[5] = new JRtaTextField("", true);
		button[0] = new JButton("auswählen");
		button[0].setActionCommand("iwahl0");
		button[1] = new JButton("auswählen");
		button[1].setActionCommand("iwahl1");
		button[2] = new JButton("auswählen");
		button[2].setActionCommand("iwahl2");		
		button[3] = new JButton("auswählen");
		button[3].setActionCommand("iwahl3");		
		button[4] = new JButton("auswählen");
		button[4].setActionCommand("iwahl4");		
		button[5] = new JButton("auswählen");
		button[5].setActionCommand("iwahl5");		
		icon[0] = new JRtaTextField("", true);
		icon[1] = new JRtaTextField("", true);
		icon[2] = new JRtaTextField("", true);
		icon[3] = new JRtaTextField("", true);
		icon[4] = new JRtaTextField("", true);
		icon[5] = new JRtaTextField("", true);
		kritlab[0] = new JLabel("1. Icon"); 
		kritlab[1] = new JLabel("2. Icon");
		kritlab[2] = new JLabel("3. Icon");
		kritlab[3] = new JLabel("4. Icon");
		kritlab[4] = new JLabel("5. Icon");
		kritlab[5] = new JLabel("6. Icon");
										//      1.            2.     3.     4.     5.     6.    7.      8.     9.
		FormLayout lay = new FormLayout("right:max(120dlu;p), 20dlu, 40dlu, 40dlu, 4dlu, 40dlu,0dlu",
       //1.    2. 3.   4.   5.   6.   7.  8.     9.     10.  11. 12.   13.     14.  15. 16.  17. 18.  19.    20. 21.  22.  23. 24   25  26   27  28  29  30   31   32   33  34   35  36   37 38   39    40  41  42  43  44
		"p, 2dlu, p, 10dlu, p, 10dlu, p, 10dlu, 80dlu, 2dlu, p, 2dlu, 0dlu, 0dlu, 0dlu, 0dlu, p, 10dlu, p, 10dlu, p,  2dlu , p, 5dlu, p, 2dlu, p, 5dlu,p, 2dlu, p, 5dlu, p, 2dlu, p, 5dlu, p, 2dlu, p, 5dlu, p, 2dlu, p, 10dlu");
		
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
		//builder.addLabel("Titel der neuen Vorlagen", cc.xy(1, 13));
		//builder.add(vorlage, cc.xyw(3,13,4));
		//builder.addLabel("Datei ausw�hlen", cc.xy(1, 15));
		datLabel = new JLabel();
		//datLabel.setForeground(Color.BLUE);
		//builder.add(datLabel, cc.xyw(3, 15, 2));
		//builder.add(button[7], cc.xy(6, 15));
		builder.addLabel("neue Vorlagendatei hinzufügen", cc.xy(1, 17));
		builder.add(button[8], cc.xy(6, 17));
		builder.addSeparator("Kriteriendefinitionen / Icons", cc.xyw(1, 19, 6));
		builder.addLabel("1. Kriterium", cc.xy(1, 21));
		builder.add(krit[0], cc.xyw(3, 21, 4));
		builder.add(kritlab[0], cc.xy(1, 23));
		builder.add(icon[0], cc.xyw(3, 23, 2));
		builder.add(button[0], cc.xy(6, 23));
		builder.addLabel("2. Kriterium", cc.xy(1, 25));
		builder.add(krit[1], cc.xyw(3, 25, 4));
		builder.add(kritlab[1], cc.xy(1, 27));
		builder.add(icon[1], cc.xyw(3, 27, 2));
		builder.add(button[1], cc.xy(6, 27));
		builder.addLabel("3. Kriterium", cc.xy(1, 29));
		builder.add(krit[2], cc.xyw(3, 29, 4));
		builder.add(kritlab[2], cc.xy(1, 31));
		builder.add(icon[2], cc.xyw(3, 31, 2));
		builder.add(button[2], cc.xy(6, 31));
		builder.addLabel("4. Kriterium", cc.xy(1, 33));
		builder.add(krit[3], cc.xyw(3, 33, 4));
		builder.add(kritlab[3], cc.xy(1, 35));
		builder.add(icon[3], cc.xyw(3, 35, 2));
		builder.add(button[3], cc.xy(6, 35));
		builder.addLabel("5. Kriterium", cc.xy(1, 37));
		builder.add(krit[4], cc.xyw(3, 37, 4));
		builder.add(kritlab[4], cc.xy(1, 39));
		builder.add(icon[4], cc.xyw(3, 39, 2));
		builder.add(button[4], cc.xy(6, 39));
		builder.addLabel("6. Kriterium", cc.xy(1, 41));
		builder.add(krit[5], cc.xyw(3, 41, 4));
		builder.add(kritlab[5], cc.xy(1, 43));
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
		String cmd = e.getActionCommand();
		for(int i = 0;i < 1;i++){
			if(cmd.equals("entfernen")){
				int row = vorlagen.getSelectedRow();
				int frage = JOptionPane.showConfirmDialog(null, "Wollen Sie die ausgewählte Tabellenzeile wirklich löschen?", "Wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
				if(frage == JOptionPane.NO_OPTION){
					return;
				}
				if(row >=0){
					TableTool.loescheRow(vorlagen, row);
				}
				break;
			}
			if(cmd.equals("vorlagenwahl")){
				setCursor(new Cursor(Cursor.WAIT_CURSOR));
				String svorlage = dateiDialog(Reha.proghome+"vorlagen/"+Reha.aktIK);
				if(! svorlage.equals("")){
					datLabel.setText(svorlage);
				}else{
					datLabel.setText("");
				}
				break;
			}
			if(cmd.equals("vorlagenneu")){
				
				setCursor(new Cursor(Cursor.WAIT_CURSOR));
				String svorlage = dateiDialog(Reha.proghome+"vorlagen/"+Reha.aktIK);
				if(! svorlage.equals("")){
					datLabel.setText(svorlage);
				}else{
					datLabel.setText("");
					break;
				}


				if(vorlage.getText().equals("") || datLabel.getText().equals("")){
					JOptionPane.showMessageDialog(null,"Geben Sie jetzt einen Titel für die neue Text-Vorlage ein");
					//return;
				}
				Vector vec = new Vector();
				vec.add("");
				vec.add(datLabel.getText());
				defvorlagen.addRow((Vector)vec.clone());
				vorlagen.validate();
				int rows = defvorlagen.getRowCount(); 
		
				//vorlagen.setCellSelectionEnabled(true);
				final int xrows = rows -1;
				SwingUtilities.invokeLater(new Runnable(){
				 	   public  void run(){
				 		  vorlagen.requestFocus();
				 		  vorlagen.setRowSelectionInterval(xrows, xrows);
				 		  startCellEditing(xrows);
				 	   }
				});
				
				//vorlage.setText("");
				//datLabel.setText("");
				break;
			}
			
			if(cmd.contains("iwahl")){
				int wahl = new Integer(cmd.substring(cmd.length()-1));
				setCursor(new Cursor(Cursor.WAIT_CURSOR));
				String sicon = dateiDialog(Reha.proghome+"icons/");
				if(! sicon.equals("")){
					icon[wahl].setText(sicon);
					kritlab[wahl].setIcon(new ImageIcon(Reha.proghome+"icons/"+sicon));
				}else{
					icon[wahl].setText("");
					kritlab[wahl].setIcon(null);
				}
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
					JOptionPane.showMessageDialog(null,"Konfiguration wurden in Datei 'patient.ini' erfolgreich gespeichert!");					
				}

			}
			
		}
	}
	private void startCellEditing(int row){
		final int xrows = row;
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run(){
		 				vorlagen.editCellAt(xrows, 0);
		 	   }
		});
	}
	
	private void doSpeichern(){
		String wert = "";
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/patient.ini");
		System.out.println(Reha.proghome+"ini/"+Reha.aktIK+"/patient.ini");
		wert = (unten.isSelected() ? "1" : "0");
		SystemConfig.hmContainer.put("Patient", new Integer(wert));
		inif.setStringProperty("Container", "StarteIn",wert , null);
		
		wert = (optimize.isSelected() ? "1" : "0");
		SystemConfig.hmContainer.put("PatientOpti",new Integer(wert));
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
				inif.setStringProperty("Formulare", "PatientFormulareAnzahl",new Integer(rows).toString() , null);				
			}
		}
		if(formok){
			for(int i = 0;i<rows;i++){
				inif.setStringProperty("Formulare", "PFormularText"+(i+1),(String)vorlagen.getValueAt(i, 0) , null);
				inif.setStringProperty("Formulare", "PFormularName"+(i+1),(String)vorlagen.getValueAt(i, 1) , null);
			}
		}
		for(int i = 0;i<6;i++){
			wert = krit[i].getText();
			inif.setStringProperty("Kriterien", "Krit"+(i+1),wert , null);
			SystemConfig.vPatMerker.set(i, wert);
			
			wert = icon[i].getText();
			inif.setStringProperty("Kriterien", "Image"+(i+1),icon[i].getText() , null);
			SystemConfig.vPatMerkerIcon.set(i, (wert.equals("") ? null : new ImageIcon(Reha.proghome+"icons/"+wert)));
			
		}
		inif.save();
	}	
	
	private String dateiDialog(String pfad){
		String sret = "";
		final JFileChooser chooser = new JFileChooser("Verzeichnis wählen");
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
            //String inputVerzStr = inputVerzFile.getPath();
            

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
	
	class MyDefaultTableModel extends DefaultTableModel{
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
	
	/**********************************************/
	class TitelEditor extends AbstractCellEditor implements TableCellEditor{
		Object value;
		JComponent component = new JFormattedTextField();
	   public TitelEditor(){
		   //component = new JRtaTextField("NIX",true);
		   System.out.println("editor-Component wurde initialisiert");
		   component.addKeyListener(new KeyAdapter(){
			   public void keyPressed(KeyEvent arg0) {
					//System.out.println("********Button in KeyPressed*********");	
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
			((JFormattedTextField)component).requestFocus();
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
	@Override
	public void editingCanceled(ChangeEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("In Hauptprogramm-Listener editingCanceled");
	}
	@Override
	public void editingStopped(ChangeEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("In Hauptprogramm-Listener editingStopped");
	}
	
}
