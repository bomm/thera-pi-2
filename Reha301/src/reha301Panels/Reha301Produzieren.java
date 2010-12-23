package reha301Panels;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import Tools.DatFunk;
import Tools.JCompTools;
import Tools.SqlInfo;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import reha301.Reha301Tab;

public class Reha301Produzieren extends JXPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Reha301Tab eltern = null;
	public JXTable tab = null;
	public MyTableModel tabmod;
	ActionListener al = null;
	int anzeigeart = -1;
	public String[] artderNachricht = {"Nachr.Typ unbekannt","Bewilligung","Ablehnung","Verl. Zustimmung","Verl. Ablehnung","sonstige Nachricht"};

	public Reha301Produzieren(Reha301Tab xeltern){
		super(new BorderLayout());
		eltern = xeltern;
		add(getContent(),BorderLayout.CENTER);
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					String cmd = "select eingelesen,sender,nachrichtentyp,patangaben,versicherungsnr,ktraeger,datum,id from dta301 where eingelesen='F' order by datum";
					regleTabelle(cmd,1);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}
			
		}.execute();
		validate();
	}

	public JXPanel getContent(){
		JXPanel pan = new JXPanel();
		String xwerte = "10dlu,fill:0:grow(1.0),10dlu";
		String ywerte = "10dlu,200dlu,10dlu:g";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		tabmod = new MyTableModel();
		tabmod.setColumnIdentifiers(new String[] {"lfnr","eingelesen","Sender","Nachrichtentyp","Name, Vorname","Adresse","VSNR","Kostenträger","Krankenkasse","Datum","",""});
		tab = new JXTable(tabmod);
		tab.getColumn(10).setMinWidth(0);
		tab.getColumn(10).setMaxWidth(0);
		tab.getColumn(11).setMinWidth(0);
		tab.getColumn(11).setMaxWidth(0);

		tab.setEditable(false);
		tab.setSortable(false);
		tab.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.CLASSIC_LINE_PRINTER));
		tab.validate();
		tab.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getClickCount()==2){
					if(anzeigeart==1){
						if(tab.getSelectedRow()>=0){
							doPatUntersuchen(tab.getSelectedRow(),arg0.getLocationOnScreen());							
						}
					}
					
				}
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		JScrollPane jscr = JCompTools.getTransparentScrollPane(tab);
		jscr.validate();
		pan.add(jscr,cc.xy(2,2,CellConstraints.FILL,CellConstraints.FILL));

		
		pan.validate();
		return pan;
	}
	
	public void ActivateListener(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("einlesen")){

				}
			}
		};
	}
	private void doPatUntersuchen(int row,Point pos){
		String[] teilen = tab.getValueAt(row,4).toString().split("#");
		String cmd = "select * from pat5 where n_name='"+teilen[1]+"' and v_name='"+teilen[2]+"' and "+
		"geboren='"+tab.getValueAt(row,10).toString()+"'";
		//System.out.println(tab.getValueAt(row,10));
		Vector<Vector<String>> patvec = SqlInfo.holeFelder(cmd);
		if(patvec.size()<=0){
			JOptionPane.showMessageDialog(null, "Patient nicht in Datenbank vorhanden");
		}else{
			doPatientenWahl(tab.getValueAt(row,11).toString(),pos);
		}
	}
	private void doPatientenWahl(String id,Point pos){
		Reha301PatAuswahl patwahl = new Reha301PatAuswahl(this,id);
		patwahl.setModal(true);
		patwahl.setLocation(pos);
		patwahl.pack();
		patwahl.setVisible(true);
		patwahl = null;
		
	}
	private void regleTabelle(String statement,int tabart){
//		"select eingelesen,sender,nachrichtentyp,patangaben,versicherungsnr,ktraeger,datum from dta301 where eingelesen='F' order by datum";
		Vector<Vector<String>> vec = SqlInfo.holeFelder(statement);
		tabmod.setRowCount(0);
		anzeigeart = tabart;
		Vector<Object> vecobj = new Vector<Object>();
		String[] pat = null;
		String patangaben = null;
		String ortsangaben = null;
		String kassenangaben = null;
		String patgeboren = null;
		if(vec.size()> 0){
			for(int i = 0; i < vec.size();i++){
				vecobj.clear();
				
				vecobj.add((String) Integer.toString(i+1));
				vecobj.add((Boolean) (vec.get(i).get(0).equals("T")? true : false) );
				vecobj.add((String)vec.get(i).get(1));
				vecobj.add((String) artderNachricht[Integer.parseInt(vec.get(i).get(2))]);
				pat = vec.get(i).get(3).split("#");
				patangaben = "";
				ortsangaben = "";
				kassenangaben = "";
				patgeboren = null;
				if(pat.length >=7){
					patangaben =pat[0]+"#"+pat[1]+"#"+pat[2];
					ortsangaben = pat[4]+"#"+pat[5]+"#"+pat[6];
					try{
						patgeboren = pat[3];
					}catch(Exception ex){
						
					}
				}
				vecobj.add((String) patangaben);
				vecobj.add((String) ortsangaben);
				vecobj.add((String) vec.get(i).get(4));
				vecobj.add((String) vec.get(i).get(5));
				if(pat.length >=8){
					kassenangaben = SqlInfo.holeEinzelFeld("select name1 from ktraeger where ikkasse='"+pat[7]+"' LIMIT 1");
					kassenangaben = kassenangaben+String.valueOf("#"+pat[7]);
				}
				vecobj.add((String) kassenangaben);
				try{
					vecobj.add((String) DatFunk.sDatInDeutsch(vec.get(i).get(6)));	
				}catch(Exception ex){
					vecobj.add("01.01.0000");
				}
				vecobj.add((String) patgeboren);
				vecobj.add((String) vec.get(i).get(7));
				tabmod.addRow( (Vector<?>)vecobj.clone());
				
				
			}
			tab.setRowSelectionInterval(0, 0);
		}
		tab.validate();
	}
	
/********************************************************/	
	class MyTableModel extends DefaultTableModel{
		   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Class<?> getColumnClass(int columnIndex) {
			   if(columnIndex==1 ){
				   return Boolean.class;}
			   else{
				   return String.class;
			   }
	       }

	    public boolean isCellEditable(int row, int col) {

	    	if (col == 0){
	    		return true;
		    }else if(col == 3){
		    	return true;
		    }else if(col == 7){
	        	return true;
	        }else if(col == 11){
	        	return true;
	        } else{
	          return false;
	        }
	    }
	}
	
	
}
