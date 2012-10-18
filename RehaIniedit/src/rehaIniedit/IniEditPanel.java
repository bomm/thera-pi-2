package rehaIniedit;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;

import CommonTools.ButtonTools;
import CommonTools.JCompTools;
import CommonTools.JRtaCheckBox;
import CommonTools.JRtaComboBox;
import CommonTools.JRtaTextField;
import CommonTools.SqlInfo;
import CommonTools.TableTool;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class IniEditPanel extends JXPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4567739194696927888L;
	
	JXPanel content = null;
	JRtaTextField[] tfs = {null,null,null,null,null,null,null,null};
	JRtaCheckBox originalChb = null;
	
	JRtaComboBox chbstatement = null;
	JButton[] buts = {null,null,null,null,null};
	
	KeyListener kl = null;
	ActionListener al = null;

	IniEditTableModel alleinimod = null;
	JXTable alleini = null;
	
	JTextArea textArea = null;
	JLabel labeditdatei = null;
	
	boolean bneu;

	public IniEditPanel(){
		super();
		setLayout(new BorderLayout());
		activateListener();
		add(getContent(),BorderLayout.CENTER);
		validate();
		content.validate();
	}
	
	private JXPanel getContent(){
		String xwerte = "85dlu,fill:0:grow(1.0)";
		String ywerte = "0dlu,fill:0:grow(1.0),0dlu";
		
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		content = new JXPanel();
		content.setLayout(lay);
		content.add(getEditPanel(),cc.xy(2,2,CellConstraints.FILL,CellConstraints.FILL));
		content.add(getActionPanel(),cc.xy(1,2,CellConstraints.FILL,CellConstraints.FILL));
		content.revalidate();
		return content;
	}
	private JXPanel getActionPanel(){
		JXPanel pan= new JXPanel();
		String xwerte = "5dlu,fill:0:grow(1.0)";
		//                1   2  3     4    5    6  7   8  9  10  11   12  13  14
		String ywerte = "5dlu,p,5dlu,200dlu,5dlu,p,2dlu,p,2dlu,p,5dlu:g,p,15dlu,p,5dlu";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		pan.add(new JLabel("vorhandene INI-Dateien:"),cc.xy(2, 2));
		alleinimod = new IniEditTableModel();
		alleinimod.setColumnIdentifiers(new String[] {".ini",""});
		alleini = new JXTable(alleinimod);
		alleini.setEditable(false);
		
		alleini.getColumn(1).setMinWidth(0);
		alleini.getColumn(1).setMaxWidth(0);
		alleini.setSortable(false);
		alleini.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				if(alleini.isEnabled()){
					doHoleIni(alleini.getSelectedRow());	
				}
				
				/*
				if(me.getClickCount()==2){
					doHoleIni(alleini.getSelectedRow());
				}
				*/
			}
		});
		JScrollPane alleinijscr = JCompTools.getTransparentScrollPane(alleini);
		alleinijscr.validate();
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				long zeit = System.currentTimeMillis();
				while(! RehaIniedit.thisClass.DbOk){
					Thread.sleep(25);
					if(System.currentTimeMillis()-zeit > 10000){
						System.exit(0);
					}
				}
				Vector<Vector<String>> vec = SqlInfo.holeFelder("select dateiname,id from inidatei");
				for(int i = 0; i < vec.size();i++){
					alleinimod.addRow(vec.get(i));
					if(i==1){
						alleini.setRowSelectionInterval(0, 0);
						doHoleIni(0);
					}
				}
				return null;
			}
		}.execute();
		
		pan.add(alleinijscr,cc.xy(2,4,CellConstraints.FILL,CellConstraints.FILL));
		Image ico = new ImageIcon(RehaIniedit.progHome+"icons/package-install.png").getImage().getScaledInstance(26,26, Image.SCALE_SMOOTH);
		buts[0] = ButtonTools.macheButton("neu", "neu", al);
		buts[0].setIcon(new ImageIcon(ico));
		buts[0].setHorizontalAlignment(SwingConstants.LEFT);
		buts[0].setToolTipText("neue INI-Datei erstellen");
		
		ico = new ImageIcon(RehaIniedit.progHome+"icons/accessories-text-editor.png").getImage().getScaledInstance(26,26, Image.SCALE_SMOOTH);
		buts[1] = ButtonTools.macheButton("ändern", "edit", al);
		buts[1].setIcon(new ImageIcon(ico));
		buts[1].setHorizontalAlignment(SwingConstants.LEFT);
		buts[1].setToolTipText("ausgewählte INI-Datei bearbeiten");
		
		
		ico = new ImageIcon(RehaIniedit.progHome+"icons/package-remove-red.png").getImage().getScaledInstance(26,26, Image.SCALE_SMOOTH);
		buts[2] = ButtonTools.macheButton("löschen", "delete", al);
		buts[2].setIcon(new ImageIcon(ico));
		buts[2].setHorizontalAlignment(SwingConstants.LEFT);
		buts[2].setToolTipText("ausgewählte INI-Datei löschen!!!!");
		
		ico = new ImageIcon(RehaIniedit.progHome+"icons/document-save-as.png").getImage().getScaledInstance(26,26, Image.SCALE_SMOOTH);
		buts[3] = ButtonTools.macheButton("speichern", "save", al);
		buts[3].setIcon(new ImageIcon(ico));
		buts[3].setHorizontalAlignment(SwingConstants.LEFT);
		buts[3].setToolTipText("INI-Datei speichern");
		buts[3].setEnabled(false);
		
		ico = new ImageIcon(RehaIniedit.progHome+"icons/application-exit.png").getImage().getScaledInstance(26,26, Image.SCALE_SMOOTH);
		buts[4] = ButtonTools.macheButton("abbrechen", "break", al);
		buts[4].setIcon(new ImageIcon(ico));
		buts[4].setHorizontalAlignment(SwingConstants.LEFT);
		buts[4].setToolTipText("Aktion abbrechen");
		buts[4].setEnabled(false);
		

		pan.add(buts[0],cc.xy(2, 6));
		pan.add(buts[1],cc.xy(2, 8));
		pan.add(buts[2],cc.xy(2, 10));
		
		pan.add(buts[3],cc.xy(2, 12));
		pan.add(buts[4],cc.xy(2, 14));
		
		/*
		tfs[0] = new JRtaTextField("nix",true);
		tfs[0].setEditable(false);
		pan.add(tfs[0],cc.xy(2,10));
		*/
		
		pan.validate();
		return pan;
	}
	private JXPanel getEditPanel(){
		JXPanel pan= new JXPanel();
		String xwerte = "5dlu,p,2dlu,p,5dlu,fill:0:grow(1.0),5dlu";
		String ywerte = "5dlu,p,5dlu,fill:0:grow(1.0),5dlu";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		pan.add(new JLabel("bearbeite INI-Datei: "),cc.xy(2, 2));
		labeditdatei = new JLabel("keine");
		labeditdatei.setForeground(Color.RED);
		pan.add(labeditdatei,cc.xy(4, 2));
		textArea = new JTextArea();
		textArea.setFont(new Font("Courier",Font.PLAIN,12));
		textArea.setEditable(false);
		textArea.setLineWrap(false);
		textArea.setWrapStyleWord(true);
		textArea.setForeground(Color.BLUE);
		textArea.setOpaque(false);
		textArea.setName("sqlstatement");
		textArea.setFont(new Font("Courier New",Font.PLAIN,12));
		textArea.addKeyListener(kl);
		JScrollPane scrstmt = JCompTools.getTransparentScrollPane(textArea);
		scrstmt.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		scrstmt.validate();	
		pan.add(scrstmt,cc.xyw(2,4,5,CellConstraints.FILL,CellConstraints.FILL));
		pan.validate();
		return pan;
	}
	private void doHoleIni(int row){
		if(row < 0){return;}
		textArea.setText(SqlInfo.holeEinzelFeld("select inhalt from inidatei where dateiname='"+alleini.getValueAt(row,0).toString()+"' LIMIT 1"));
		textArea.setCaretPosition(0);
		if(tfs[0] != null){
			tfs[0].setText(alleini.getValueAt(row,0).toString());
		}
	}
	private void activateListener(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("neu")){
					doNeu();
					return;
				}else if(cmd.equals("edit")){
					doEdit();
					return;
				}else if(cmd.equals("delete")){
					doDelete();
					return;
				}else if(cmd.equals("save")){
					doSave();
					return;
				}else if(cmd.equals("break")){
					doBreak();
					return;
				}
			}
			
		};
		kl = new KeyListener(){
			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode()==10 && arg0.isShiftDown()){
					return;
				}
				if(arg0.getKeyCode()==10){
				}
				
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
			}
		};
	}	
	class IniEditTableModel extends DefaultTableModel{
		   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Class<?> getColumnClass(int columnIndex) {
		   return String.class;
	    }

		public boolean isCellEditable(int row, int col) {
			return false;
		}
		   
	}
	
	/*******************
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	private void doNeu(){
		bneu = true;
		Object obj = JOptionPane.showInputDialog(null, "Bitte geben Sie den Namen der neuen INI-Datei ein","neuername.ini");
		System.out.println(obj);
		if(obj == null){
			return;
		}
		if(obj.toString().trim().equals("")){
			JOptionPane.showMessageDialog(null,"der Dateiname darf nicht leer sein");
			return;
		}
		if(! obj.toString().trim().endsWith(".ini")){
			JOptionPane.showMessageDialog(null,"der Dateiname muß mit '.ini' enden");
			return;
		}
		textArea.setForeground(Color.BLACK);
		labeditdatei.setText(obj.toString().trim());
		tabelleEinschalten(false);
		butsEinschalten(false,false,false,true,true);
		textEinschalten(true);
	}
	private void doEdit(){
		bneu = false;
		labeditdatei.setText(alleini.getValueAt(alleini.getSelectedRow(), 0).toString());
		textArea.setForeground(Color.BLACK);
		tabelleEinschalten(false);
		butsEinschalten(false,false,false,true,true);
		textEinschalten(true);
	}
	private void doDelete(){
		int frage = JOptionPane.showConfirmDialog(null,"Achtung wollen Sie wirklich die INI-Datei --> "+alleini.getValueAt(alleini.getSelectedRow(), 0).toString()+" <-- löschen?",
				"Gefahr für den weiteren Betrieb von Thera-Pi !",JOptionPane.YES_NO_OPTION);
		if(frage==JOptionPane.YES_OPTION){
			int row = alleini.getSelectedRow();
			SqlInfo.sqlAusfuehren("delete from inidatei where dateiname = '"+alleini.getValueAt(alleini.getSelectedRow(), 0).toString()+"' LIMIT 1");
			TableTool.loescheRowAusModel(alleini, row);
		}
		if(alleini.getRowCount() <= 0){
			textArea.setText("");
		}else{
			alleini.setRowSelectionInterval(alleini.getRowCount()-1, alleini.getRowCount()-1);
			doHoleIni(alleini.getSelectedRow());
		}
	}
	private void doSave(){
		if(bneu){
			Vector<String> rowvec = new Vector<String>();
				rowvec.add(labeditdatei.getText());
				rowvec.add("");
				alleinimod.addRow(rowvec);
				alleini.setRowSelectionInterval(alleini.getRowCount()-1, alleini.getRowCount()-1);
		}
		bneu = false;
		SqlInfo.schreibeIniInTabelle(labeditdatei.getText(), textArea.getText().getBytes());
		textArea.setForeground(Color.BLUE);
		tabelleEinschalten(true);
		butsEinschalten(true,true,true,false,false);
		textEinschalten(false);
		doHoleIni(alleini.getSelectedRow());
		
	}
	private void doBreak(){
		bneu = false;
		labeditdatei.setText("keine");
		textArea.setForeground(Color.BLUE);
		tabelleEinschalten(true);
		butsEinschalten(true,true,true,false,false);
		textEinschalten(false);
		doHoleIni(alleini.getSelectedRow());
		
	}
	private void textEinschalten(boolean enable){
		textArea.setEditable(enable);
		if(enable){
			if(bneu){
				textArea.setText("");
			}
			textArea.requestFocus();
			textArea.setCaretPosition(0);
		}
	}
	private void tabelleEinschalten(boolean enable){
		alleini.setEnabled(enable);
	}
	private void butsEinschalten(boolean b0,boolean b1,boolean b2,boolean b3,boolean b4){
		buts[0].setEnabled(b0);
		buts[1].setEnabled(b1);
		buts[2].setEnabled(b2);
		buts[3].setEnabled(b3);
		buts[4].setEnabled(b4);
	}
	

}
