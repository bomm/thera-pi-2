package Suchen;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;



import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


public class ICDoberflaeche extends JXPanel implements ListSelectionListener, ActionListener{
	
	JComboBox combobox1;
	JComboBox combobox2;
	JButton bnr1;	
	JButton bnr2;
	JButton bnr3;
	JTextArea textfeld = null;
	JTextArea jtbf = null;
	JScrollPane jscr = null;
	TableModel Tabelle;
	MyTBTableModel tbmod = null;
	JXTable tbtab = null;
	JTextField[] tf1 = {null};
	Vector<Vector<String>> ergebnis_vec = new Vector<Vector<String>>();
	
	public ICDoberflaeche(){
		super();
		
			setOpaque(false);
	
			
			//                                   1    2     3   4     5   6       7      8    9  10  11 12  13  14 15    16             17
			FormLayout layob1 = new FormLayout("0dlu,0dlu,10dlu,p,5dlu,100dlu:g,5dlu,100dlu,5dlu,p,20dlu,p,5dlu,50dlu,5dlu,right:0:grow,10dlu,0dlu,0dlu",
			// 1  2     3    4  5           6           7          8            9     10    11   
			"0dlu,0dlu,10dlu,p,10dlu,fill:0:grow(0.5),10dlu,  fill:0:grow(0.5),10dlu,10dlu,0dlu");
					CellConstraints c1 = new CellConstraints();
					setLayout(layob1);
					
					
					
					JLabel lblsuche = new JLabel("Suche nach");
					add(lblsuche, c1.xy(4,4));
					add(getCombobox1(),c1.xy(6,4));
					tf1[0] = new JTextField("");
					tf1[0].addKeyListener(new KeyAdapter(){
						public void keyPressed(KeyEvent ev){
							if(ev.getKeyCode()==10){
								doSuchen();
							}
						}
					});
					add(tf1[0],c1.xy(8,4));
					add(getBottons(),c1.xy(10,4));
					JLabel lbllimit = new JLabel("Limit");
					add(lbllimit, c1.xy(12,4));
					add(getCombobox2(),c1.xy(14,4));
					add(getTabelle(),c1.xyw(4,6,13));
					add(getTextarea(),c1.xywh(4,8,13,2));
					
	}
	
	
	private JPanel getCombobox1(){
		FormLayout comboboxPan = new FormLayout("100dlu:g","p");
		PanelBuilder pcombox = new PanelBuilder(comboboxPan);
		pcombox.getPanel().setOpaque(false);
		CellConstraints ccombox = new CellConstraints();
	
			combobox1 = new JComboBox(new String[] {"ICD-10-Code eingeben -> ICD-10-Text suchen","ICD-10-Text eingeben -> ICD-10-Code suchen"});
			combobox1.setSelectedIndex(1);
			pcombox.add(combobox1,ccombox.xy(1,1));
	
		pcombox.getPanel().validate();
		return pcombox.getPanel();
	}
	
	

	private JPanel getCombobox2(){
		FormLayout comboboxPan = new FormLayout("50dlu:g","p");
		PanelBuilder pcombox = new PanelBuilder(comboboxPan);
		pcombox.getPanel().setOpaque(false);
		CellConstraints ccombox = new CellConstraints();
	
			combobox2 = new JComboBox(new String[] {"0","1","5","10","20","30","40","50"});
			pcombox.add(combobox2,ccombox.xy(1,1));
	
		pcombox.getPanel().validate();
		return pcombox.getPanel();
	}

	private JPanel getBottons(){
		FormLayout bottonsPan = new FormLayout("60dlu","p");
		PanelBuilder pbottons = new PanelBuilder(bottonsPan);
		pbottons.getPanel().setOpaque(false);
		CellConstraints cbottons = new CellConstraints();
	
		bnr1 = new JButton("Suchen");
		bnr1.setActionCommand("suchen");
		bnr1.addActionListener(this);
		pbottons.add(bnr1,cbottons.xy(1,1));
		
	
		pbottons.getPanel().validate();
		return pbottons.getPanel();
	}
	
	private JScrollPane getTabelle(){
		tbmod = new MyTBTableModel();
		tbmod.setColumnIdentifiers(new String[] {"ICD-Code o.Punkte","Titel","id"});
		tbtab = new JXTable(tbmod);
		tbtab.getColumn(0).setMaxWidth(150);
		tbtab.getColumn(0).setMinWidth(150);
		tbtab.getColumn(2).setMinWidth(0);
		tbtab.getColumn(2).setMaxWidth(0);
		tbtab.getSelectionModel().setSelectionMode(0);
		tbtab.getSelectionModel().addListSelectionListener( new TBListSelectionHandler());
		tbtab.setHighlighters(HighlighterFactory.createSimpleStriping());
		//tbtab.setRowHeight(20);
		JScrollPane scrollpane = new JScrollPane(tbtab);
	    scrollpane.validate();  

		return scrollpane;
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
	        if (!lsm.isSelectionEmpty()) {
	            int minIndex = lsm.getMinSelectionIndex();
	            int maxIndex = lsm.getMaxSelectionIndex();
	            int row = tbtab.getSelectedRow();
	            String id = (String) tbtab.getValueAt(row,2);
	            Vector<Vector<String>> text = SqlInfo.holeFelder("select icdtext from icd10 where id='"+id+"' LIMIT 1");
	            if(text.get(0).size() > 0){
	            	jtbf.setText(text.get(0).get(0));
	            }else{
	            	jtbf.setText("Kein Text f�r diesen ICD-10 Code vorhanden");
	            }
	        }    

	    }


	}	
	
	private JScrollPane getTextarea(){
		jtbf = new JTextArea();
		jtbf.setFont(new Font("Courier",Font.PLAIN,11));
		jtbf.setLineWrap(true);
		jtbf.setName("s�tze");
		jtbf.setWrapStyleWord(true);
		jtbf.setEditable(true);
		jtbf.setBackground(Color.WHITE);
		jtbf.setForeground(Color.BLUE);
		JScrollPane scrollpane = new JScrollPane(jtbf);
	    scrollpane.validate();  

		return scrollpane;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String cmd = arg0.getActionCommand();
		if(cmd.equals("suchen")){
			doSuchen();
		}
	}
	private void doSuchen(){
		String suchtext = tf1[0].getText().trim();
		if(suchtext.equals("")){
			JOptionPane.showMessageDialog(null, "Bitte Suchbegriff(e) eingeben");
			return;
		}
		String where = "";
		String select = "select schluessel2,titelzeile,id from icd10 where ";
		int ilimit = Integer.parseInt((String)combobox2.getSelectedItem());
		String limit = (ilimit > 0 ? "LIMIT "+Integer.toString(ilimit) : "");
		if(suchtext.indexOf(' ') >= 0){
			//Suche nach mehreren Begiffen
			if(combobox1.getSelectedIndex()==0){
				where = SqlInfo.macheWhereKlausel("", suchtext, new String[] {"schluessel2"});				
			}else{
				where = SqlInfo.macheWhereKlausel("", suchtext, new String[] {"icdtext"});				
			}
		}else{
			if(combobox1.getSelectedIndex()==0){
				where = "schluessel2 LIKE '%"+suchtext+"%'";
			}else{
				where = "icdtext LIKE '%"+suchtext+"%'";				
			}
		}	
		String cmd = select+where+limit;
		final String xcmd = cmd;
		
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				ergebnis_vec = SqlInfo.holeFelder(xcmd);
				int lang = ergebnis_vec.size(); 
				tbmod.setRowCount(0);
				if(lang > 0){
					for(int i = 0; i < lang;i++){
						tbmod.addRow(ergebnis_vec.get(i));
					}
					tbtab.validate();
					tbtab.setRowSelectionInterval(0, 0);
				}
				return null;
			}
			
		}.execute();
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
		
	

	public void valueChanged(ListSelectionEvent arg0) {
		System.out.println(arg0);
		
	}
	
	
		
	}




	@Override
	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub
		
	}	
}
