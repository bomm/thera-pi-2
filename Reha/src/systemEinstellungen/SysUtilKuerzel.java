package systemEinstellungen;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import jxTableTools.MitteRenderer;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dialoge.KuerzelNeu;

import CommonTools.SqlInfo;
import CommonTools.JCompTools;
import CommonTools.JRtaComboBox;
import CommonTools.JRtaRadioButton;

public class SysUtilKuerzel  extends JXPanel implements ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JXTable tblkuerzel = null;
	MyKuerzelTableModel modkuerzel = new MyKuerzelTableModel();
	JRtaRadioButton[] rbuts = {null,null,null,null,null};
	JButton[] button = {null,null,null,null,null,null,null};
	JRtaComboBox disziplin = null;
	String[] diszi = {"KG","MA","ER","LO","RH","PO"};
	String aktuelleID = "-1";
	int aktuelleRow = -1;
	final int I_LEISTUNG = 0;
	final int I_KUERZEL = 1;
	final int I_DISZIPLIN = 2;
	final int I_VORRANGIG = 3;
	final int I_EXTRAOK = 4;
	final int I_ID = 5;
	SysUtilKuerzel(){
		super(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 5));
		//this.setBorder(BorderFactory.createEmptyBorder(15, 40, 20, 20));
		/****/
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("SystemInit"));
		/****/
	    add(getVorlagenSeite(),BorderLayout.CENTER);
	    add(getKnopfPanel(),BorderLayout.SOUTH);
	    new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				tabelleFuellen(diszi[0],-1);
				return null;
			}
	    	
	    }.execute();

	}
	
	private JPanel getVorlagenSeite(){
		FormLayout lay = new FormLayout("right:max(120dlu;p), 20dlu, 40dlu, 40dlu, 4dlu, 80dlu:g",
			       //1.    2. 3.   4.  5.   6.   7.   8.     9.    10.    11.    12.   13.  14.   15.   16.    17.   18.  19.  20.    21.    22.   23.   24     25    26    27    28   29
					"p, 2dlu, p, 10dlu,p, 10dlu, p, 10dlu, 150dlu, 10dlu,  p,  10dlu");
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		builder.getPanel().validate();
		builder.addSeparator("Disziplin auswählen", cc.xyw(1, 3, 6));

		disziplin = new JRtaComboBox(new String[] {"Physio","Massage","Ergo","Logo","Reha","Podo"});
		disziplin.setActionCommand("disziplin");
		disziplin.addActionListener(this);
		modkuerzel.setColumnIdentifiers(new String[] {"Kürzel","Langtext","Disziplin","vorrangig","isoliert","id"});
		tblkuerzel = new JXTable(modkuerzel);
		MitteRenderer mr = new MitteRenderer();
		tblkuerzel.getColumn(0).setMaxWidth(80);
		tblkuerzel.getColumn(2).setMaxWidth(80);
		tblkuerzel.getColumn(2).setCellRenderer(mr);
		tblkuerzel.getColumn(3).setMaxWidth(60);
		tblkuerzel.getColumn(4).setMaxWidth(60);
		tblkuerzel.getColumn(5).setMaxWidth(60);
		tblkuerzel.getColumn(5).setCellRenderer(mr);
		tblkuerzel.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent arg0) {
				if(arg0.getClickCount()==2){
					doKuerzelNeu(false);
				}
			}
		});
		builder.add(disziplin, cc.xy(6,5));
		
		builder.addSeparator("Kürzel-Verwaltung", cc.xyw(1, 7, 6));
		JScrollPane jscrPane = JCompTools.getTransparentScrollPane(tblkuerzel);
		jscrPane.getVerticalScrollBar().setUnitIncrement(15);
		jscrPane.validate();

		builder.add(jscrPane, cc.xyw(1,9, 6));
		
		return builder.getPanel();
		
	}
	private JPanel getKnopfPanel(){
		button[3] = new JButton("ändern");
		button[3].setActionCommand("aendern");
		button[3].addActionListener(this);

		button[4] = new JButton("neu");
		button[4].setActionCommand("neu");
		button[4].addActionListener(this);
		if(!Reha.vollbetrieb){
			button[4].setEnabled(false);			
		}
		button[5] = new JButton("abbrechen");
		button[5].setActionCommand("abbrechen");
		button[5].addActionListener(this);
		
		button[6] = new JButton("speichern");
		button[6].setActionCommand("speichern");
		button[6].addActionListener(this);		
									//      1.                      2.    3.    4.     5.     6.    7.      8.     9.
		FormLayout jpanlay = new FormLayout("right:120dlu, 50dlu, 40dlu,4dlu, 40dlu, 4dlu, 40dlu,4dlu,40dlu",
       //1.    2. 3.   4.   5.   6.     7.    8. 9.  10.  11. 12. 13.  14.  15. 16.  17. 18.  19.   20.    21.   22.   23.
		"p, 10dlu, p");
		
		PanelBuilder jpan = new PanelBuilder(jpanlay);
		jpan.getPanel().setOpaque(false);		
		CellConstraints jpancc = new CellConstraints();
		
		jpan.addSeparator("", jpancc.xyw(1,1,9));
		jpan.add(button[4], jpancc.xy(3,3));
		jpan.add(button[3], jpancc.xy(5,3));
		jpan.add(button[6], jpancc.xy(7,3));
		jpan.add(button[5], jpancc.xy(9,3));
		jpan.addLabel("Neue Kürzel / Änderungen speichern?", jpancc.xy(1,3));
		
		
		return jpan.getPanel();
	}
	
	@SuppressWarnings("unchecked")
	private void tabelleFuellen(String diszi,int row){
		String cmd = "select * from kuerzel where disziplin ='"+diszi+"' order by kuerzel";
		Vector<Vector<String>> vec = SqlInfo.holeFelder(cmd);
		int lang = vec.size();
		Vector<Object> dummy = new Vector<Object>();
		modkuerzel.setRowCount(0);
		for(int i = 0; i < lang;i++){
			dummy.clear();
			dummy.add((String)vec.get(i).get(this.I_KUERZEL));
			dummy.add((String)vec.get(i).get(this.I_LEISTUNG));
			dummy.add((String)vec.get(i).get(this.I_DISZIPLIN));
			dummy.add((Boolean)(vec.get(i).get(this.I_VORRANGIG).equals("T") ? true : false));
			dummy.add((Boolean)(vec.get(i).get(this.I_EXTRAOK).equals("T") ? true : false));
			dummy.add((String)vec.get(i).get(this.I_ID));
			modkuerzel.addRow((Vector<Object>)dummy.clone());
		}
		tblkuerzel.validate();
	}
	
	class MyKuerzelTableModel extends DefaultTableModel{
		   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Class<?> getColumnClass(int columnIndex) {
			if(columnIndex==3 ||columnIndex==4){
				return Boolean.class;
			}
			return String.class;
	    }

		public boolean isCellEditable(int row, int col) {
			
			return false;
		}
		   
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		String cmd = arg0.getActionCommand();
		if(cmd.equals("disziplin")){
			tabelleFuellen(diszi[disziplin.getSelectedIndex()],-1);
			return;
		}
		if(cmd.equals("neu")){
			doKuerzelNeu(true);
		}
		if(cmd.equals("aendern")){
			if(tblkuerzel.getSelectedRow() < 0){
				JOptionPane.showMessageDialog(null, "Kein Kürzel zur Bearbeitung ausgewählt");
				return;
			}
			doKuerzelNeu(false);
		}

	}
	private void doKuerzelNeu(boolean neu){
		Point pt = button[4].getLocationOnScreen();
		String stitel = (neu ? "Neues Positionskürzel anlegen" : "bestehendes Positionskürzel ändern"); 
		KuerzelNeu kNeuDlg = new KuerzelNeu(Reha.thisFrame,stitel,neu,this);
		kNeuDlg.setPreferredSize(new Dimension(475,200));
		kNeuDlg.setLocation(pt.x-150,pt.y-230);
		kNeuDlg.pack();
		kNeuDlg.setVisible(true);

	}
	public Object[] getKuerzelDaten(){
		Object[] ret = {null,null,null,null};
		int row = tblkuerzel.getSelectedRow();
		if(row < 0){
			System.out.println("Keine Reihe ausgewählt");
			return ret;
		}
		aktuelleID = tblkuerzel.getValueAt(tblkuerzel.convertRowIndexToModel(row), this.I_ID).toString();
		aktuelleRow = row;
		ret[0] = tblkuerzel.getValueAt(tblkuerzel.convertRowIndexToModel(row), 0).toString();
		ret[1] = tblkuerzel.getValueAt(tblkuerzel.convertRowIndexToModel(row), 1).toString();
		ret[2] = ((Boolean) tblkuerzel.getValueAt(tblkuerzel.convertRowIndexToModel(row), 3));
		ret[3] = ((Boolean) tblkuerzel.getValueAt(tblkuerzel.convertRowIndexToModel(row), 4));
		return ret;
	}
	public void updateKuerzel(String kurz,String lang,Boolean vorrang,Boolean separat){
		String cmd = "update kuerzel set kuerzel='"+kurz+"', leistung='"+lang+"', "+
		"vorrangig='"+(vorrang ? "T" : "F")+"', extraok='"+(separat ? "T" : "F")+"'"+
		" where id='"+aktuelleID+"' LIMIT 1";
		SqlInfo.sqlAusfuehren(cmd);
		int row = tblkuerzel.getSelectedRow();
		
		modkuerzel.setValueAt((String) kurz, tblkuerzel.convertRowIndexToModel(row), 0);
		modkuerzel.setValueAt((String) lang, tblkuerzel.convertRowIndexToModel(row), 1);
		modkuerzel.setValueAt((Boolean) vorrang, tblkuerzel.convertRowIndexToModel(row), 3);
		modkuerzel.setValueAt((Boolean) separat, tblkuerzel.convertRowIndexToModel(row), 4);
		tblkuerzel.validate();
	}
	public void insertKuerzel(String kurz,String lang,Boolean vorrang,Boolean separat){
		boolean gibtsschon = SqlInfo.gibtsSchon("select kuerzel from kuerzel where kuerzel='"+kurz+"' and disziplin='"+
				diszi[disziplin.getSelectedIndex()]+"'");
		if(gibtsschon){
			JOptionPane.showMessageDialog(null, "Das Kürzel --> "+kurz+" <-- ist bereits vergeben");
			return;
		}
		String cmd = "insert into kuerzel set kuerzel='"+kurz+"', leistung='"+lang+"', disziplin='"+
		diszi[disziplin.getSelectedIndex()]+"', vorrangig='"+(vorrang ? "T" : "F")+"', extraok='"+(separat ? "T" : "F")+"'";
		SqlInfo.sqlAusfuehren(cmd);
		tabelleFuellen(diszi[disziplin.getSelectedIndex()],-1);
		for(int i = 0;i < tblkuerzel.getRowCount();i++){
			if(tblkuerzel.getValueAt(i, 0).toString().equals(kurz)){
				tblkuerzel.setRowSelectionInterval(i, i);
				break;
			}
		}
	}

}
