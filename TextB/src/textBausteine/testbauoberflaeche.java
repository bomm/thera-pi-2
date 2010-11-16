package textBausteine;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;

import sqlTools.SqlInfo;
import sqlTools.SystemEinstellungen;

import Tools.JCompTools;
import Tools.JRtaRadioButton;
import Tools.JRtaTextField;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class testbauoberflaeche extends JXPanel implements ActionListener,ListSelectionListener, FocusListener{
	
	JCheckBox[]checkbox = {null, null, null, null, null};
	JTextArea jtbf = null;
	JScrollPane jscr = null;
	JButton bnr1;	
	JButton bnr2;
	JButton bnr3;
	JButton bnr4;
	JButton bnr5;
	JButton[] buts = {null,null,null,null,null};
	JRtaRadioButton[] jrbtb = {null,null,null,null,null};
	TableModel Tabelle;
	MyTBTableModel tbmod = null;
	JXTable tbtab = null;
	Vector<Vector<String>> tbvec = new Vector<Vector<String>>(); 
	ButtonGroup bg = new ButtonGroup();

	JComboBox combobox;
	JComboBox oberbegriff;
	JComboBox textblock;
	JComboBox rang;
	JRtaTextField tftitel;
	JButton sysvars;
	
	String aktuellediszi = "Physio";
	String[] dbs = {"tbkg","tbma","tber","tblo","tbrh"};
	List allediszis;
	String aktuelledb = "tbkg";
	boolean neu;
	boolean initok = false;
	String akteditid;
	SysVars textVariable;
	int lastCurPos=0;
	public testbauoberflaeche(){
		super();
		
			setOpaque(false);
			allediszis = Arrays.asList(new String[] {"Physio","Massage","Ergo","Logo","Reha"});
	FormLayout laybau = new FormLayout("10dlu,fill:0:grow,10dlu",
	 		
	"10dlu,p,10dlu,fill:0:grow(1.0),10dlu,p,10dlu,p,10dlu,fill:0:grow(0.5),10dlu,p,10dlu");
			CellConstraints cb = new CellConstraints();
			setLayout(laybau);
	
			
			
			
			add(getCheckbox(),cb.xy(2,2));
			add(getTextarea(),cb.xy(2,4,CellConstraints.FILL,CellConstraints.FILL));
			add(getBottons(),cb.xy(2,6));
			add(getCombobox(),cb.xy(2,8));
			add(getTabelle(),cb.xy(2,10));
			add(getRadios(),cb.xy(2,12,CellConstraints.DEFAULT,CellConstraints.CENTER));
			validate();
			ladeCombo(aktuellediszi);
			ladeDaten(dbs[0],(String)combobox.getSelectedItem());
			regleCheckBoxen(0);
			regleButtons(new int[] {1,1,1,0,0});
	}
	
	private JPanel getCheckbox(){              // 1     2    3     4    5     6    7     8     9   10    11
		FormLayout checkboxPan = new FormLayout("right:60dlu,6dlu,60dlu,6dlu,right:60dlu,6dlu,60dlu,6dlu,right:60dlu,6dlu,60dlu","p");
		PanelBuilder pcbox = new PanelBuilder(checkboxPan);
		pcbox.getPanel().setOpaque(false);
		CellConstraints ccbox = new CellConstraints();
		pcbox.addLabel("Oberbegriff",ccbox.xy(1, 1));
		oberbegriff = new JComboBox();
		pcbox.add(oberbegriff,ccbox.xy(3, 1));
		pcbox.addLabel("Textblock",ccbox.xy(5, 1));
		textblock = new JComboBox(new String[]{"1","2","3","4"});
		pcbox.add(textblock,ccbox.xy(7, 1));
		pcbox.addLabel("Rang",ccbox.xy(9, 1));
		rang = new JComboBox(new String[]{"01","02","03","04","05","06","07","08","09","10","11","12","13","14","15"});
		pcbox.add(rang,ccbox.xy(11, 1));
		/*
		checkbox[0] = new JCheckBox("Physio");
		pcbox.add(checkbox[0],ccbox.xy(1, 1));
		
		checkbox[1] = new JCheckBox("Massage");
		pcbox.add(checkbox[1],ccbox.xy(3, 1));
		
		checkbox[2] = new JCheckBox("Ergotherapie");
		pcbox.add(checkbox[2],ccbox.xy(5, 1));
		
		checkbox[3] = new JCheckBox("Logopädie");
		pcbox.add(checkbox[3],ccbox.xy(7, 1));
		
		checkbox[4] = new JCheckBox("Reha");
		pcbox.add(checkbox[4],ccbox.xy(9, 1));
		
		for(int i = 0; i < 5; i++){
			checkbox[i].setEnabled(false);
		}
		*/
		oberbegriff.setEnabled(false);
		textblock.setEnabled(false);
		rang.setEnabled(false);
		pcbox.getPanel().validate();
		return pcbox.getPanel();
	
	}
	
	private JPanel getCombobox(){
		FormLayout comboboxPan = new FormLayout("126dlu,6dlu,192dlu","p");
		PanelBuilder pcombox = new PanelBuilder(comboboxPan);
		pcombox.getPanel().setOpaque(false);
		CellConstraints ccombox = new CellConstraints();
	
			combobox = new JComboBox();
			combobox.setName("thema");
			combobox.addActionListener(this);
			
			tftitel = new JRtaTextField("nix",true);
			tftitel.setEnabled(false);
			pcombox.add(tftitel,ccombox.xy(3,1));
			
			pcombox.add(combobox,ccombox.xy(1,1));
		
		
	
		pcombox.getPanel().validate();
		return pcombox.getPanel();
	}
		
		
	private JPanel getTextarea(){
		FormLayout textareaPan = new FormLayout("fill:0:grow","p:g");
		PanelBuilder ptarea = new PanelBuilder(textareaPan);
		ptarea.getPanel().setOpaque(false);
		CellConstraints ctarea = new CellConstraints();
	
	
		
		
		jtbf = new JTextArea();
		jtbf.setFont(new Font("Courier New",Font.PLAIN,12));
		jtbf.setLineWrap(true);
		jtbf.setName("saetze");
		jtbf.setWrapStyleWord(true);
		jtbf.setEditable(true);
		jtbf.setBackground(Color.WHITE);
		jtbf.setForeground(Color.BLUE);
		jtbf.setEnabled(false);
		jtbf.setDisabledTextColor(Color.RED);
		jtbf.addFocusListener(this);
		
		jscr = JCompTools.getTransparentScrollPane(jtbf);
		jscr.validate();
		ptarea.add(jscr,ctarea.xy(1,1,CellConstraints.FILL,CellConstraints.FILL));
	
	
	
		ptarea.getPanel().validate();
		return ptarea.getPanel();
	}
	
	
	
	private JPanel getBottons(){
		FormLayout bottonsPan = new FormLayout("60dlu,6dlu,60dlu,6dlu,60dlu,6dlu,60dlu,6dlu,60dlu","p");
		PanelBuilder pbottons = new PanelBuilder(bottonsPan);
		pbottons.getPanel().setOpaque(false);
		CellConstraints cbottons = new CellConstraints();
	
		bnr1 = new JButton("neu");
		bnr1.setActionCommand("neu");
		bnr1.addActionListener(this);
		buts[0] = bnr1;
		pbottons.add(bnr1,cbottons.xy(1,1));
		bnr2 = new JButton("ändern");
		bnr2.setActionCommand("aendern");
		bnr2.addActionListener(this);
		buts[1] = bnr2;
		pbottons.add(bnr2,cbottons.xy(3,1));
		bnr3 = new JButton("löschen");
		bnr3.setActionCommand("loeschen");
		bnr3.addActionListener(this);
		buts[2] = bnr3;
		pbottons.add(bnr3,cbottons.xy(5,1));
		bnr4 = new JButton("speichern");
		bnr4.setActionCommand("speichern");
		bnr4.addActionListener(this);
		buts[3] = bnr4;
		pbottons.add(bnr4,cbottons.xy(7,1));
		bnr5 = new JButton("abbrechen");
		bnr5.setActionCommand("abbrechen");
		bnr5.addActionListener(this);
		buts[4] = bnr5;
		pbottons.add(bnr5,cbottons.xy(9,1));

		pbottons.getPanel().validate();
		return pbottons.getPanel();
	}
	
	
	private JPanel getTabelle(){
		FormLayout tabellePan = new FormLayout("fill:0:grow","fill:0:grow(0.5)");
		PanelBuilder ptabelle = new PanelBuilder(tabellePan);
		ptabelle.getPanel().setOpaque(false);
		CellConstraints ctabelle = new CellConstraints();
		tbmod = new MyTBTableModel();
		tbmod.setColumnIdentifiers(new String[] {"TB-Titel","Tb-Block","TB-Rang","Thema","id"});
		tbtab = new JXTable(tbmod);
		tbtab.getColumn(0).setMinWidth(200);
		tbtab.getColumn(1).setMinWidth(75);
		tbtab.getColumn(2).setMinWidth(75);
		tbtab.getColumn(3).setMinWidth(75);
		tbtab.getColumn(4).setMinWidth(40);
		tbtab.getColumn(1).setMaxWidth(75);
		tbtab.getColumn(2).setMaxWidth(75);
		tbtab.getColumn(3).setMaxWidth(75);
		tbtab.getColumn(4).setMaxWidth(40);
		tbtab.getSelectionModel().addListSelectionListener( new TBListSelectionHandler());
		
		JScrollPane scrollpane = new JScrollPane(tbtab);
	      
	    ptabelle.add(scrollpane,ctabelle.xy(1,1,CellConstraints.FILL,CellConstraints.FILL));
		
		
		ptabelle.getPanel().validate();
		return ptabelle.getPanel();	
	}
	
	
	class TBListSelectionHandler implements ListSelectionListener {

	    public void valueChanged(ListSelectionEvent e) {
	    	
	        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
	        
	        int firstIndex = e.getFirstIndex();
	        int lastIndex = e.getLastIndex();
	        boolean isAdjusting = e.getValueIsAdjusting();
	        if(isAdjusting){
	        	return;
	        }
		
	        if (lsm.isSelectionEmpty()) {

	        } else {
	            int minIndex = lsm.getMinSelectionIndex();
	            int maxIndex = lsm.getMaxSelectionIndex();
	            for (int i = minIndex; i <= maxIndex; i++) {
	                if (lsm.isSelectedIndex(i)) {
	                	final int ix = i;
	                	new SwingWorker<Void,Void>(){
							@Override
							protected Void doInBackground() throws Exception {
								// TODO Auto-generated method stub
								try{
	                			setCursor(new Cursor(Cursor.WAIT_CURSOR));
	                			//String id = (String) tbtab.getValueAt(ix, 3);
	                				//System.out.println(tbtab.getValueAt(ix, 4));
	                				//System.out.println("Ausgewähle Reihe = "+ix);
            					ladeText(ix);
	                			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
								}catch(Exception ex){
									ex.printStackTrace();
								}
								return null;
							}
	                		
	                	}.execute();
	                    break;
	                }
	            }
	        }
	        //System.out.println(output.toString());
	    }
	}	
	
	
	private JPanel getRadios(){
		FormLayout radioPan = new FormLayout("p,6dlu,p,6dlu,p,6dlu,p,6dlu,p","p");
		PanelBuilder pb = new PanelBuilder(radioPan);
		pb.getPanel().setOpaque(false);
		CellConstraints ccrad = new CellConstraints();
		jrbtb[0] = new JRtaRadioButton("Physio");
		jrbtb[0].setActionCommand("Physio");
		jrbtb[0].addActionListener(this);
		jrbtb[0].setSelected(true);
		bg.add(jrbtb[0]);
		
		
		pb.add(jrbtb[0],ccrad.xy(1,1));
		jrbtb[1] = new JRtaRadioButton("Massage");
		jrbtb[1].setActionCommand("Massage");
		bg.add(jrbtb[1]);
		jrbtb[1].addActionListener(this);
		
		pb.add(jrbtb[1],ccrad.xy(3,1));
		jrbtb[2] = new JRtaRadioButton("Ergotherapie");
		jrbtb[2].setActionCommand("Ergo");
		bg.add(jrbtb[2]);
		jrbtb[2].addActionListener(this);
		
		pb.add(jrbtb[2],ccrad.xy(5,1));
		jrbtb[3] = new JRtaRadioButton("Logopädie");
		jrbtb[3].setActionCommand("Logo");
		bg.add(jrbtb[3]);
		jrbtb[3].addActionListener(this);
		
		pb.add(jrbtb[3],ccrad.xy(7,1));
		jrbtb[4] = new JRtaRadioButton("Reha");
		jrbtb[4].setActionCommand("Reha");
		bg.add(jrbtb[4]);
		jrbtb[4].addActionListener(this);
		
		pb.add(jrbtb[4],ccrad.xy(9,1));
		pb.getPanel().validate();
		return pb.getPanel();
	}
	
	class MyTBTableModel extends DefaultTableModel{
		   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Class getColumnClass(int columnIndex) {
			   if(columnIndex==0){return String.class;}

			   else{return String.class;}

	}

		    public boolean isCellEditable(int row, int col) {
		        //Note that the data/cell address is constant,
		        //no matter where the cell appears onscreen.
		    	return false;
		      }
			public Object getValueAt(int rowIndex, int columnIndex) {
				String theData = (String) ((Vector)getDataVector().get(rowIndex)).get(columnIndex); 
				Object result = null;
				//result = theData.toUpperCase();
				result = theData;
				return result;
			}
		    
		   
	}
	public void setSysVar(String svar){
		System.out.println("Eingang = "+svar);
		String xtext =  this.jtbf.getText();
		if(lastCurPos > 0){
			String x1 = xtext.substring(0,lastCurPos);
			String x2 = xtext.substring(lastCurPos);
			xtext = x1+svar+x2;
			this.jtbf.setText(xtext);
		}else{
			this.jtbf.setText(svar+xtext);
		}
	}
	
	public void ladeDaten(String db,String thema){
		tbvec = SqlInfo.holeSaetze(db, "TBTITEL,TBBLOCK,TBRANG,TBOBER,id", "tbthema='"+thema+"' ORDER BY TBTITEL", Arrays.asList(new String[]{}));
		int lang = tbvec.size();
		tbmod.setRowCount(0);
		System.out.println("Hole aus Datenbank "+db+" alles zum Thema "+thema);
		//System.out.println("Insgesamgt gefundene Textbausteine = "+tbvec.size());
		for(int i = 0; i < lang; i++){
			//System.out.println("Insgesamgt gefundene Textbausteine = "+tbvec.size()+" derzeitig Stand "+i);
			tbmod.addRow(tbvec.get(i));			
		}
		if(tbmod.getRowCount() > 0){
			tbtab.setRowSelectionInterval(0, 0);
		}else{
			jtbf.setText("");
		}
		//ladeCombo(aktuellediszi);
	}
	public void ladeCombo(String disziplin){
		combobox.removeAllItems();
		int anzahl = SystemEinstellungen.hmThema.get(disziplin).size();
		for(int i = 0; i < anzahl; i++){
			combobox.addItem((String) SystemEinstellungen.hmThema.get(disziplin).get(i));
		}
		ladeOberbegriff(disziplin);
	}
	public void ladeOberbegriff(String disziplin){
		oberbegriff.removeAllItems();
		int anzahl = SystemEinstellungen.hmOberbegriff.get(disziplin).size();
		for(int i = 0; i < anzahl; i++){
			oberbegriff.addItem((String) SystemEinstellungen.hmOberbegriff.get(disziplin).get(i));
		}
		
	}
	
	public void ladeText(int row){
			String wert = (String) tbtab.getValueAt(row, 4); 
			Vector<Vector<String>> tbtext = SqlInfo.holeSaetze(aktuelledb, "TBTEXT", "id='"+wert+"'", Arrays.asList(new String[]{}));
			int lang = tbtext.size();
				if(lang >0){
					jtbf.setText(tbtext.get(0).get(0));
					oberbegriff.setSelectedItem((String) tbtab.getValueAt(row, 3));
					textblock.setSelectedItem((String) tbtab.getValueAt(row, 1));
					rang.setSelectedItem((String) tbtab.getValueAt(row, 2));
					tftitel.setText((String) tbtab.getValueAt(row, 0));
				}
			}
	

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String cmd = arg0.getActionCommand();
		
		if(allediszis.contains(cmd)){
			aktuellediszi = cmd;
			ladeCombo(cmd);
			if(cmd.equals("Physio")){
				System.out.println("ActionCommand = "+cmd);
				//checkbox[0].setSelected(true);
				regleCheckBoxen(0);
				return;
			}
			if(cmd.equals("Massage")){
				//checkbox[1].setSelected(true);
				System.out.println("ActionCommand = "+cmd);
				regleCheckBoxen(1);
				return;
			}
			if(cmd.equals("Ergo")){
				//checkbox[2].setSelected(true);
				System.out.println("ActionCommand = "+cmd);
				regleCheckBoxen(2);
				return;
			}
			if(cmd.equals("Logo")){
				//checkbox[3].setSelected(true);
				regleCheckBoxen(3);
				return;
			}
			if(cmd.equals("Reha")){
				//checkbox[4].setSelected(true);
				System.out.println("ActionCommand = "+cmd);
				regleCheckBoxen(4);
				return;
			}
			return;
		}
		if(cmd.equals("comboBoxChanged") && combobox.getItemCount() > 0){
			int pos = allediszis.indexOf(aktuellediszi);
			if(pos >= 0){
				try{
					aktuelledb = dbs[pos];
					//ladeDaten(dbs[pos],(String)allediszis.get(combobox.getSelectedIndex()) );
					ladeDaten(dbs[pos],(String) combobox.getSelectedItem());				
					System.out.println(dbs[pos]);
					
				}catch(Exception ex){
					System.out.println("Datenbank = "+dbs[pos]);
					System.out.println("ausgewählt = "+combobox.getSelectedIndex());
					JOptionPane.showMessageDialog(null,"Fehler, bitte wählen Sie erneut aus...");
					
				}
			}

		}
		if(cmd.equals("neu")){
			neu = true;
			doNeu();
			this.regleButtons(new int[] {0,0,0,1,1});
			holeVars();
		}
		if(cmd.equals("aendern")){
			int row = tbtab.getSelectedRow();
			if(row < 0){
				return;
			}
			neu = false;
			doAendern(row);
			this.regleButtons(new int[] {0,0,0,1,1});
			holeVars();
		}
		if(cmd.equals("speichern")){
			doSpeichern();
			neu = false;
			this.regleButtons(new int[] {1,1,1,0,0});
			schliesseVars();
		}
		if(cmd.equals("abbrechen")){
			doAbbrechen();
			neu = false;
			this.regleButtons(new int[] {1,1,1,0,0});
			schliesseVars();
		}
		if(cmd.equals("loeschen")){
			doLoeschen();
		}


	}
	private void holeVars(){
		if(textVariable != null){return;}
		textVariable = new SysVars(this);
		textVariable.pack();
		Point p = this.getLocationOnScreen();
		int xwert = this.getWidth();
		textVariable.setLocation(p.x+xwert+5, p.y-30);
		textVariable.setVisible(true);
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				jtbf.requestFocus();		
			}
		});
		
	}
	private void schliesseVars(){
		if(textVariable == null){return;}
		textVariable.setVisible(false);
		textVariable.dispose();
		textVariable = null;
	}
	
	private void doLoeschen(){
		int row = tbtab.getSelectedRow();
		if(row < 0){
			return;
		}
		String id = (String)tbtab.getValueAt(row,4);
		int anfrage = JOptionPane.showConfirmDialog(null, "Wollen Sie diesen Textbaustein wirklich löschen", "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
		if(anfrage == JOptionPane.YES_OPTION){
			System.out.println("Satz wird gelöscht");
			SqlInfo.sqlAusfuehren("delete from "+getDb()+" where id = '"+id+"' LIMIT 1");
			this.jtbf.setText("");
			this.tftitel.setText("");
			int modrow = tbtab.convertRowIndexToModel(row);
			this.tbmod.removeRow(modrow);

		}
	}
	private void doNeu(){
		this.jtbf.setEnabled(true);
		this.jtbf.requestFocus();
		this.jtbf.setText("");
		this.tbtab.setEnabled(false);
		this.tftitel.setEnabled(true);
		this.tftitel.setText("");
		this.oberbegriff.setEnabled(true);
		this.textblock.setEnabled(true);
		this.rang.setEnabled(true);
	}
	private void doAendern(int row){
		this.jtbf.setEnabled(true);
		this.jtbf.requestFocus();
		this.tbtab.setEnabled(false);
		this.tftitel.setEnabled(true);
		this.oberbegriff.setEnabled(true);
		this.textblock.setEnabled(true);
		this.rang.setEnabled(true);
		this.akteditid = (String) tbtab.getValueAt(row,4);
	}
	/*************************************/
	private void doSpeichern(){
		int row = tbtab.getSelectedRow();
		String[] klasse = {"KG","MA","ER","LO","RH"};
		if(!neu && row < 0){
			doAbbrechen();
			return;
		}
		if(jtbf.getText().trim().equals("") || this.tftitel.getText().trim().equals("")){
			JOptionPane.showMessageDialog(null, "Titel und Text des Bausteins darf nicht leer sein");
			doAbbrechen();
			return;
		}
		//Datenbank ermitteln
		String cmd="";
		if(!neu){
			String idtitel = (String)tbtab.getValueAt(row, 0);
			cmd = "update "+getDb()+" set ";
		}else{
			cmd = "insert into "+getDb()+" set ";
		}
		String sthema = (combobox.getSelectedItem()==null ? "Allgemein" : (String) combobox.getSelectedItem());
		String sober = (oberbegriff.getSelectedItem()==null ? "Allgemein" : (String) oberbegriff.getSelectedItem());
		String stitel = (tftitel.getText().startsWith(sober+" - ") ?  tftitel.getText().trim() : 
			sober+" - "+tftitel.getText().trim()	); 
		
		cmd = cmd+"TBKLASSE='"+klasse[getPos()]+"', TBTHEMA='"+sthema+"', "+
		"TBBLOCK='"+textblock.getSelectedItem()+"', TBRANG='"+rang.getSelectedItem()+"', "+
		"TBOBER='"+sober+"', TBTEXT='"+jtbf.getText()+"', "+
		"TBTITEL='"+stitel+"' "+
			(!neu ? "where id='"+this.akteditid+"'" : "");
		SqlInfo.sqlAusfuehren(cmd);
		//System.out.println(stitel);
		doAbbrechen();
		if(neu){
			String id = SqlInfo.holeFelder("select max(id) from "+getDb()).get(0).get(0);
			Vector<String> xneu = new Vector<String>();
			xneu.add(stitel);
			xneu.add((String)textblock.getSelectedItem());
			xneu.add((String)rang.getSelectedItem());
			xneu.add(sober);
			xneu.add(id);
			tbmod.addRow(xneu);
		}else{
			if(row >= 0){
				int xrow = tbtab.convertRowIndexToModel(row);
				System.out.println("Tabelle wird aktualisiert...."+stitel);
				tbmod.setValueAt(stitel, xrow, 0);	
				tbmod.setValueAt((String)textblock.getSelectedItem(), xrow, 1);
				tbmod.setValueAt((String)rang.getSelectedItem(), xrow, 2);
				tbmod.setValueAt(sober, xrow, 3);
				tbtab.validate();
				tftitel.setText((String)tbmod.getValueAt(xrow,0));
			}

		}
		
	}
	/*************************************/
	private void doAbbrechen(){
		this.jtbf.setEnabled(false);
		this.jtbf.setText("");
		this.tbtab.setEnabled(true);
		this.tftitel.setEnabled(false);
		this.oberbegriff.setEnabled(false);
		this.textblock.setEnabled(false);
		this.rang.setEnabled(false);
		this.akteditid = "";
		int row = -1;
		if( (row = this.tbtab.getSelectedRow()) >= 0){
			this.ladeText(row);
		}
	}

	private void regleCheckBoxen(int box){
		for(int i = 0; i < 5; i++){
			if(i==box){
				//checkbox[i].setSelected(true);
			}else{
				//checkbox[i].setSelected(false);
			}
		}
	}
	private void regleButtons(int[] butts){
		for(int i = 0; i < 5; i++){
			buts[i].setEnabled( (butts[i]==1 ? true : false) );
		}
	}
	private int getPos(){
		return allediszis.indexOf(aktuellediszi);
	} 
	private String getDb(){
		int pos = allediszis.indexOf(aktuellediszi);
		if(pos >= 0){
			aktuelledb = dbs[pos];
		}
		return aktuelledb;
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		lastCurPos = this.jtbf.getCaretPosition();
		
	}
	
}
