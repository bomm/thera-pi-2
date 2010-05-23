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
import java.util.Arrays;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
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
import javax.swing.SwingUtilities;
import org.jdesktop.swingworker.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import jxTableTools.TableTool;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
//import org.jdesktop.swingx.decorator.SortOrder;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import systemTools.JCompTools;
import systemTools.JRtaTextField;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SysUtilArzt extends JXPanel implements KeyListener, ActionListener {
	
	JButton[] button = {null,null,null,null,null,null,null};
	JRtaTextField newgroup = null;
	JRtaTextField newdoc = null;
	JXTable gruppen = null;
	MyVorlagenTableModel modarzt = null;
	JXTable vorlagen = null;
	MyVorlagenTableModel modvorl = null;	

	
	JRadioButton oben = null;
	JRadioButton unten = null;
	JCheckBox optimize = null;
	ButtonGroup bgroup = new ButtonGroup();
	boolean formok = true;
	
	public SysUtilArzt(){
		super(new BorderLayout());
		//System.out.println("Aufruf SysUtilArzt");
		this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 20));
		/****/
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("SystemInit"));
	     
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
		
		if(SystemConfig.hmContainer.get("Arzt") == 0){
			oben.setSelected(true);
		}else{
			unten.setSelected(true);
		}
		if(SystemConfig.hmContainer.get("ArztOpti") == 0){
			optimize.setSelected(false);
		}else{
			optimize.setSelected(true);
		}
		
		Vector<String> vec = new Vector<String>();

		int lang = SystemConfig.arztGruppen.length;
		for(int i = 0; i < lang;i++){
			vec.clear();
			vec.add(SystemConfig.arztGruppen[i]);
			modarzt.addRow((Vector) vec.clone());
		}
		if(gruppen.getRowCount() > 0){
			gruppen.setRowSelectionInterval(0, 0);			
		}
		gruppen.validate();
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/arzt.ini");
		int forms = inif.getIntegerProperty("Formulare", "ArztFormulareAnzahl");
		vec = new Vector<String>();
		for(int i = 1; i <= forms; i++){
			vec.clear();
			vec.add(inif.getStringProperty("Formulare","AFormularText"+i));
			vec.add(inif.getStringProperty("Formulare","AFormularName"+i));
			modvorl.addRow((Vector)vec.clone());
		}
		if(modvorl.getRowCount() > 0){
			vorlagen.setRowSelectionInterval(0, 0);
		}
		vorlagen.validate();
		

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
	
	/************** Beginn der Methode f�r die Objekterstellung und -platzierung *********/
	private JPanel getVorlagenSeite(){
		oben = new JRadioButton();
		bgroup.add(oben);
		unten = new JRadioButton();
		bgroup.add(unten);
		
		optimize = new JCheckBox();
		
		button[0] = new JButton("entfernen"); //buttons 1+2 f�r Gruppenverwaltung
		button[0].setActionCommand("entfernengruppe");
		button[0].addActionListener(this);
		button[1] = new JButton("hinzufügen");
		button[1].setActionCommand("neugruppen");
		button[1].addActionListener(this);

		button[2] = new JButton("entfernen"); //buttons 3-5 f�r Vorlagenverwaltung
		button[2].setActionCommand("entfernenvorlage");
		button[2].addActionListener(this);
		//button[3] = new JButton("ausw�hlen");
		button[4] = new JButton("hinzufügen");
		button[4].setActionCommand("neuvorlagen");
		button[4].addActionListener(this);




		modarzt = new MyVorlagenTableModel();
		modarzt.setColumnIdentifiers(new String[] {"Arzt / Facharztgruppe"});
		gruppen = new JXTable(modarzt);
		gruppen.getColumn(0).setCellEditor(new TitelEditor());
		gruppen.setSortable(true);
		//gruppen.setSortOrder(0, SortOrder.ASCENDING);

		modvorl = new MyVorlagenTableModel();
		modvorl.setColumnIdentifiers(new String[] {"Titel der Vorlage","Vorlagendatei"});
		vorlagen = new JXTable(modvorl);
		vorlagen.getColumn(0).setCellEditor(new TitelEditor());
		vorlagen.setSortable(false);
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
						modvorl.setValueAt(svorlage, row, col);
						vorlagen.validate();
					}
				}
			}	
		});
		newgroup = new JRtaTextField("GROSS", true);
		newdoc = new JRtaTextField("GROSS", true);
		
									//      1.           	  2.     3.     4.     5.     6.    7.      8.     9.
		FormLayout lay = new FormLayout("right:max(120dlu;p), 20dlu, 40dlu, 40dlu, 4dlu, 40dlu",
       //1.   2.  3.  4.   5.   6.   7.   8.     9.    10. 11. 12.   13.  14.  15. 16.   17. 18.    19.    20.   21.   22.   23.     24  25     26   27
		"p, 2dlu, p, 10dlu,p, 10dlu, p, 10dlu, 80dlu, 2dlu,p, 0dlu,0dlu, 2dlu, p, 10dlu, p, 10dlu, 80dlu, 2dlu, p,   0dlu , 0dlu, 0dlu, 0dlu, 2dlu, p");
		
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

		builder.addSeparator("Arztgruppen-Verwaltung", cc.xyw(1, 7, 6));
		JScrollPane jscrPane = JCompTools.getTransparentScrollPane(gruppen);
		jscrPane.getVerticalScrollBar().setUnitIncrement(15);
		jscrPane.validate();
		builder.add(jscrPane, cc.xyw(1,9, 6));
		
		builder.addLabel("aus Liste entfernen", cc.xy(1,11));
		builder.add(button[0], cc.xy(6, 11));
		//builder.addLabel("neuer Gruppenname", cc.xy(1, 13));
		//builder.add(newgroup, cc.xyw(3, 13, 4));
		builder.addLabel("zu Liste hinzufügen", cc.xy(1, 15));
		builder.add(button[1], cc.xy(6, 15));

		builder.addSeparator("Vorlagen-Verwaltung", cc.xyw(1, 17, 6));
		jscrPane = JCompTools.getTransparentScrollPane(vorlagen);
		jscrPane.getVerticalScrollBar().setUnitIncrement(15);
		jscrPane.validate();
		builder.add(jscrPane, cc.xyw(1,19,6));

		builder.addLabel("aus Liste entfernen", cc.xy(1, 21));
		builder.add(button[2], cc.xy(6, 21));
		//builder.addLabel("Vorlagenbezeichnung", cc.xy(1, 23));
		//builder.add(newdoc, cc.xyw(3, 23, 4));
		//builder.addLabel("Vorlage ausw�hlen", cc.xy(1,25));
		//builder.addLabel("Dateiname", cc.xy(3,25,CellConstraints.RIGHT, CellConstraints.BOTTOM)); 
		//builder.add(button[3], cc.xy(6,25));
		builder.addLabel("zu Liste hinzufügen", cc.xy(1, 27));
		builder.add(button[4], cc.xy(6,27));
		
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
		for(int i = 0;i < 1;i++){
			if(cmd.equals("entfernengruppe")){
				//int row = gruppen.convertRowIndexToModel(gruppen.getSelectedRow());
				int row = gruppen.getSelectedRow();
				int frage = JOptionPane.showConfirmDialog(null, "Wollen Sie die ausgewählte Tabellenzeile wirklich löschen?", "Wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
				if(frage == JOptionPane.NO_OPTION){
					return;
				}
				if(row >=0){
					TableTool.loescheRowAusModel(gruppen, row);
				}
				break;
			}
			if(cmd.equals("entfernenvorlage")){
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
			if(cmd.equals("neugruppen")){
				Vector<String> vec = new Vector<String>();
				vec.add("");
				modarzt.addRow((Vector)vec.clone());
				gruppen.validate();
				gruppen.setRowSelectionInterval(0, 0);
				int rows = gruppen.convertRowIndexToModel(gruppen.getSelectedRow());
				
				
				//int rows = modarzt.getRowCount(); 
				final int xrows = rows;
				SwingUtilities.invokeLater(new Runnable(){
				 	   public  void run(){
				 		  gruppen.requestFocus();
				 		  startCellEditing(gruppen,0);
				 	   }
				});
				break;
				
				
			}
			if(cmd.equals("neuvorlagen")){
				setCursor(new Cursor(Cursor.WAIT_CURSOR));
				String svorlage = dateiDialog(Reha.proghome+"vorlagen/"+Reha.aktIK);
				if(svorlage.equals("")){
					return;
				}


				Vector vec = new Vector();
				vec.add("");
				vec.add(svorlage);
				modvorl.addRow((Vector)vec.clone());
				vorlagen.validate();
				int rows = modvorl.getRowCount(); 
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
				//System.out.println("Es wird abgespeichert");
				doSpeichern();
				if(formok){
					JOptionPane.showMessageDialog(null,"Konfiguration wurden in Datei 'arzt.ini' erfolgreich gespeichert!");					
				}

			}
			
		}

		
	}
	
	private void doSpeichern(){
		String wert = "";
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/arzt.ini");
		//System.out.println(Reha.proghome+"ini/"+Reha.aktIK+"/patient.ini");
		wert = (unten.isSelected() ? "1" : "0");
		SystemConfig.hmContainer.put("Arzt", new Integer(wert));
		inif.setStringProperty("Container", "StarteIn",wert , null);
		
		wert = (optimize.isSelected() ? "1" : "0");
		SystemConfig.hmContainer.put("ArztOpti",new Integer(wert));
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
				inif.setStringProperty("Formulare", "ArztFormulareAnzahl",new Integer(rows).toString() , null);				
			}
		}
		if(formok){
			for(int i = 0;i<rows;i++){
				inif.setStringProperty("Formulare", "AFormularText"+(i+1),(String)vorlagen.getValueAt(i, 0) , null);
				inif.setStringProperty("Formulare", "AFormularName"+(i+1),(String)vorlagen.getValueAt(i, 1) , null);
			}
		}
		rows = gruppen.getRowCount();
		inif.setStringProperty("ArztGruppen", "AnzahlGruppen",new Integer(rows).toString() , null);		
		for(int i = 0;i<rows;i++){
			inif.setStringProperty("ArztGruppen", "Gruppe"+(i+1),(String)gruppen.getValueAt(i, 0) , null);
		}
		inif.save();
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
	/**********************************************/
	class TitelEditor extends AbstractCellEditor implements TableCellEditor{
		Object value;
		JComponent component = new JFormattedTextField();
	   public TitelEditor(){
		   //component = new JRtaTextField("NIX",true);
		   //System.out.println("editor-Component wurde initialisiert");
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
			//System.out.println("isCellEditable");
			return true;
		}


		@Override
		public boolean shouldSelectCell(EventObject anEvent) {
			//System.out.println("in schouldCellSelect"+anEvent);
			return super.shouldSelectCell(anEvent);
		}

		@Override
		public boolean stopCellEditing() {
			value = ((JFormattedTextField) component).getText();
			//System.out.println("in stopCellediting");
			super.stopCellEditing();
			return true;
		}
		public boolean startCellEditing() {
	        return false;//super.startCellEditing();//false;
		}

		
	}



}
