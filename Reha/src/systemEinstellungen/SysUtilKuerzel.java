package systemEinstellungen;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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

import sqlTools.SqlInfo;
import systemTools.JCompTools;
import systemTools.JRtaComboBox;
import systemTools.JRtaRadioButton;

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
	String[] diszi = {"KG","MA","ER","LO","RH"};

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

		disziplin = new JRtaComboBox(new String[] {"Physio","Massage","Ergo","Logo","Reha"});
		disziplin.setActionCommand("disziplin");
		disziplin.addActionListener(this);
		modkuerzel.setColumnIdentifiers(new String[] {"Kürzel","Langtext","Disziplin","id"});
		tblkuerzel = new JXTable(modkuerzel);
		MitteRenderer mr = new MitteRenderer();
		tblkuerzel.getColumn(0).setMaxWidth(80);
		tblkuerzel.getColumn(2).setMaxWidth(80);
		tblkuerzel.getColumn(2).setCellRenderer(mr);
		tblkuerzel.getColumn(3).setMaxWidth(60);
		tblkuerzel.getColumn(3).setCellRenderer(mr);		
		builder.add(disziplin, cc.xy(6,5));
		
		builder.addSeparator("Kürzel-Verwaltung", cc.xyw(1, 7, 6));
		JScrollPane jscrPane = JCompTools.getTransparentScrollPane(tblkuerzel);
		jscrPane.getVerticalScrollBar().setUnitIncrement(15);
		jscrPane.validate();

		builder.add(jscrPane, cc.xyw(1,9, 6));
		
		return builder.getPanel();
		
	}
	private JPanel getKnopfPanel(){
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
		FormLayout jpanlay = new FormLayout("right:120dlu, 50dlu, 40dlu,10dlu, 40dlu, 4dlu, 40dlu",
       //1.    2. 3.   4.   5.   6.     7.    8. 9.  10.  11. 12. 13.  14.  15. 16.  17. 18.  19.   20.    21.   22.   23.
		"p, 10dlu, p");
		
		PanelBuilder jpan = new PanelBuilder(jpanlay);
		jpan.getPanel().setOpaque(false);		
		CellConstraints jpancc = new CellConstraints();
		
		jpan.addSeparator("", jpancc.xyw(1,1,7));
		jpan.add(button[4], jpancc.xy(3,3));
		jpan.add(button[5], jpancc.xy(5,3));
		jpan.add(button[6], jpancc.xy(7,3));
		jpan.addLabel("Neue Kürzel / Änderungen speichern?", jpancc.xy(1,3));
		
		
		return jpan.getPanel();
	}
	
	@SuppressWarnings("unchecked")
	private void tabelleFuellen(String diszi,int row){
		String cmd = "select * from kuerzel where disziplin ='"+diszi+"' order by kuerzel";
		Vector<Vector<String>> vec = SqlInfo.holeFelder(cmd);
		int lang = vec.size();
		Vector<String> dummy = new Vector<String>();
		modkuerzel.setRowCount(0);
		for(int i = 0; i < lang;i++){
			dummy.clear();
			dummy.add(vec.get(i).get(1));
			dummy.add(vec.get(i).get(0));
			dummy.add(vec.get(i).get(2));
			dummy.add(vec.get(i).get(3));
			modkuerzel.addRow((Vector<String>)dummy.clone());
		}
		tblkuerzel.validate();
	}
	
	class MyKuerzelTableModel extends DefaultTableModel{
		   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Class<?> getColumnClass(int columnIndex) {
			   return String.class;
	    }

		public boolean isCellEditable(int row, int col) {
			if(col == 1){
				return true;
			}
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
	}

}
