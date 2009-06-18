package patientenFenster;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.event.TableColumnModelExtListener;

import patientenFenster.AktuelleRezepte.MyAktRezeptTableModel;
import patientenFenster.AktuelleRezepte.MyTermTableModel;

import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemTools.JCompTools;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class TherapieBerichte  extends JXPanel implements ListSelectionListener,TableModelListener,TableColumnModelExtListener,PropertyChangeListener, ActionListener{ 
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	public static TherapieBerichte aktBericht;
	public String aktPanel = "";
	JXPanel leerPanel = null;
	JXPanel vollPanel = null;
	JXPanel wechselPanel = null;
	JButton[] tberbut = {null,null,null};
	public JXTable tabbericht = null;
	public MyBerichtTableModel dtblm;

	
	
	public TherapieBerichte() {
		super();
		aktBericht = this;

		setOpaque(false);
		setBorder(null);
		setLayout(new BorderLayout());
		
		leerPanel = new KeinRezept("Keine Berichte angelegt f�r diesen Patient");
		leerPanel.setName("leerpanel");
		leerPanel.setOpaque(false);

		JXPanel allesrein = new JXPanel(new BorderLayout());
		allesrein.setOpaque(false);
		allesrein.setBorder(null);
		
		FormLayout lay = new FormLayout("5dlu,fill:0:grow(1.00),0dlu",
		"0dlu,p,2dlu,p,2dlu,fill:0:grow(1.00),5dlu");
		CellConstraints cc = new CellConstraints();
		allesrein.setLayout(lay);
		
		
		wechselPanel = new JXPanel(new BorderLayout());
		wechselPanel.setOpaque(false);
		wechselPanel.setBorder(null);
		//leerPanel = new KeinRezept();

		wechselPanel.add(leerPanel,BorderLayout.CENTER);

		aktPanel = "leerPanel";
		//wechselPanel.add(getDatenpanel(),BorderLayout.CENTER);
		allesrein.add(getToolbar(),cc.xy(2, 2));
		//allesrein.add(getTabelle(),cc.xy(2, 4));
		allesrein.add(wechselPanel,cc.xy(2, 6));

		add(JCompTools.getTransparentScrollPane(allesrein),BorderLayout.CENTER);
		validate();
		
		new Thread(){
			public void run(){
				new SwingWorker<Void,Void>(){

					@Override
					protected Void doInBackground() throws Exception {
				
						// TODO Auto-generated method stub
						vollPanel = new JXPanel();
						FormLayout vplay = new FormLayout("5dlu,fill:0:grow(1.00),5dlu","5dlu,fill:0:grow(1.00),5dlu");
						CellConstraints vpcc = new CellConstraints();
						vollPanel.setLayout(vplay);
						vollPanel.setOpaque(false);
						vollPanel.setBorder(null);
						vollPanel.add(getBerichteTbl(),vpcc.xy(2,2));
			
						return null;

					}
					
				}.execute();
			}
		}.start();

	}
	

	public void setzeRezeptPanelAufNull(boolean aufnull){
		if(aufnull){
			if(aktPanel.equals("vollPanel")){
				wechselPanel.remove(vollPanel);
				wechselPanel.add(leerPanel);
				aktPanel = "leerPanel";
				for(int i = 0; i < 3; i++){
					tberbut[i].setEnabled(false);
				}
			}
		}else{
			if(aktPanel.equals("leerPanel")){
				wechselPanel.remove(leerPanel);
				wechselPanel.add(vollPanel);
				aktPanel = "vollPanel";
				for(int i = 0; i < 3; i++){
					tberbut[i].setEnabled(true);
				}
			}
		}
	}
	public void holeBerichte(String patint,String rez){
/**********/

			final String xpatint = patint;
			final String xrez_nr = rez;

			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
			
					//String sstmt = "select * from verordn where PAT_INTERN ='"+xpatint+"' ORDER BY REZ_DATUM";
					Vector vec = SqlInfo.holeSaetze("berhist", "berichtid,berichttyp,bertitel,verfasser,DATE_FORMAT(erstelldat,'%d.%m.%Y') AS derstelldat,empfaenger,DATE_FORMAT(editdat,'%d.%m.%Y') AS deditdat,empfid,berichtid",
							"pat_intern='"+xpatint+"' ORDER BY erstelldat", Arrays.asList(new String[]{}));
					int anz = vec.size();
					for(int i = 0; i < anz;i++){
						if(i==0){
							dtblm.setRowCount(0);						
						}

						//int zzbild = 0;
						dtblm.addRow((Vector)vec.get(i));
					}
					if(anz > 0){
						setzeRezeptPanelAufNull(false);
						tabbericht.setRowSelectionInterval(0,0);
						int anzeigen = -1;
						wechselPanel.revalidate();
						wechselPanel.repaint();					
					}else{
						setzeRezeptPanelAufNull(true);
						wechselPanel.revalidate();
						wechselPanel.repaint();
						dtblm.setRowCount(0);
					}
					
					return null;
				}
				
			}.execute();
			
	}
		
		
/*********/
	private void doThBerichtEdit(int row){
		String xreznr = (String) dtblm.getValueAt(row, 2);
		String[] splitrez = xreznr.split(" ");
		int bid = new Integer((String) dtblm.getValueAt(row, 8));
		String xverfasser = (String) dtblm.getValueAt(row, 3);
		String xtitel = (String) dtblm.getValueAt(row, 2);
		//System.out.println("aufruf des Berichtes mit der ID "+bid);
		
		String[] splitdiag1 = xreznr.split("\\(");
		String[] splitdiag2 = splitdiag1[1].split("\\)");
		System.out.println("Die Diagnose = --------------->"+splitdiag2[0]);
		ArztBericht ab = new ArztBericht(null,"arztberichterstellen",false,splitrez[2],bid,3,xverfasser,splitdiag2[0]);
		ab.setModal(true);
		ab.setLocationRelativeTo(null);
		ab.setVisible(true);
		ab = null;
	}

	private JXPanel getBerichteTbl(){
		JXPanel dummypan = new JXPanel(new BorderLayout());
		dummypan.setOpaque(false);
		dummypan.setBorder(null);
		dtblm = new MyBerichtTableModel();
		String[] column = 	{"ID","Bericht-Typ","Titel","Verfasser","erstellt","Empf�nger","letzte �nderung","",""};
		dtblm.setColumnIdentifiers(column);
		tabbericht = new JXTable(dtblm);
		tabbericht.setEditable(false);
		tabbericht.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				if(arg0.getClickCount()==2){
					// hier pr�fen welcher Berichtstyp und dementsprechend das Berichtsfenster �ffnen
					///neuanlageRezept(false,"");
					int wahl = tabbericht.getSelectedRow();
					if(wahl < 0){
						return;
					}
					String verfas = (String)dtblm.getValueAt(wahl, 3); 
					if(! verfas.toUpperCase().contains("REHA-ARZT")){
						doThBerichtEdit(wahl);						
					}
				}
			}
		});
		tabbericht.getColumn(0).setMinWidth(50);
		tabbericht.getColumn(0).setMaxWidth(50);
		tabbericht.getColumn(7).setMinWidth(0);
		tabbericht.getColumn(7).setMaxWidth(0);
		tabbericht.getColumn(8).setMinWidth(0);
		tabbericht.getColumn(8).setMaxWidth(0);

		tabbericht.validate();

		JScrollPane jscr = JCompTools.getTransparentScrollPane(tabbericht);
		jscr.validate();
		dummypan.add(jscr,BorderLayout.CENTER);
		return dummypan;
	}
	public JToolBar getToolbar(){
		JToolBar jtb = new JToolBar();
		jtb.setOpaque(false);
		jtb.setRollover(true);
		jtb.setBorder(null);
		jtb.setOpaque(false);

		tberbut[0] = new JButton();
		tberbut[0].setIcon(SystemConfig.hmSysIcons.get("neu"));
		tberbut[0].setToolTipText("neues Rezept anlegen");
		tberbut[0].setActionCommand("rezneu");
		tberbut[0].addActionListener(this);		
		jtb.add(tberbut[0]);
		tberbut[1] = new JButton();
		tberbut[1].setIcon(SystemConfig.hmSysIcons.get("edit"));
		tberbut[1].setToolTipText("aktuelles Rezept �ndern/editieren");
		tberbut[1].setActionCommand("rezedit");
		tberbut[1].addActionListener(this);		
		jtb.add(tberbut[1]);
		tberbut[2] = new JButton();
		tberbut[2].setIcon(SystemConfig.hmSysIcons.get("delete"));
		tberbut[2].setToolTipText("aktuelles Rezept l�schen");
		tberbut[2].setActionCommand("rezdelete");
		tberbut[2].addActionListener(this);		
		jtb.add(tberbut[2]);
		for(int i = 0; i < 3; i++){
			tberbut[i].setEnabled(false);
		}
		return jtb;
	}
	
/**************
 * 
 * 
 * 
 */
@Override
public void valueChanged(ListSelectionEvent arg0) {
	// TODO Auto-generated method stub
	
}

@Override
public void tableChanged(TableModelEvent arg0) {
	// TODO Auto-generated method stub
	
}

@Override
public void columnPropertyChange(PropertyChangeEvent arg0) {
	// TODO Auto-generated method stub
	
}

@Override
public void columnAdded(TableColumnModelEvent arg0) {
	// TODO Auto-generated method stub
	
}

@Override
public void columnMarginChanged(ChangeEvent arg0) {
	// TODO Auto-generated method stub
	
}

@Override
public void columnMoved(TableColumnModelEvent arg0) {
	// TODO Auto-generated method stub
	
}

@Override
public void columnRemoved(TableColumnModelEvent arg0) {
	// TODO Auto-generated method stub
	
}

@Override
public void columnSelectionChanged(ListSelectionEvent arg0) {
	// TODO Auto-generated method stub
	
}

@Override
public void propertyChange(PropertyChangeEvent arg0) {
	// TODO Auto-generated method stub
	
}

@Override
public void actionPerformed(ActionEvent arg0) {
	// TODO Auto-generated method stub
	
}

class MyBerichtTableModel extends DefaultTableModel{
	   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Class getColumnClass(int columnIndex) {
		   if(columnIndex==1){
			   //return JLabel.class;}
		   		return String.class;}
		   else{
			   return String.class;
		   }
        //return (columnIndex == 0) ? Boolean.class : String.class;
    }

	    public boolean isCellEditable(int row, int col) {
	        //Note that the data/cell address is constant,
	        //no matter where the cell appears onscreen.

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

/**************Ende Klasse************/
}