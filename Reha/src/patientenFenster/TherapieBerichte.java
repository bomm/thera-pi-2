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
import javax.swing.JOptionPane;
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

import jxTableTools.TableTool;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.event.TableColumnModelExtListener;

import patientenFenster.AktuelleRezepte.MyAktRezeptTableModel;
import patientenFenster.AktuelleRezepte.MyTermTableModel;

import sqlTools.ExUndHop;
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
		
		leerPanel = new KeinRezept("Keine Berichte angelegt für diesen Patient");
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
				for(int i = 1; i < 3; i++){
					tberbut[i].setEnabled(true);
				}
			}
		}
	}
	private String macheHtmlTitel(int anz,String titel){
		
		String ret = titel+" - "+new Integer(anz).toString();
		
		/*
		String ret = "<html>"+titel+
		(anz > 0 ? " - <font color='#ff0000'>"+new Integer(anz).toString()+"<font></html>" : " - <font color='#000000'>"+new Integer(anz).toString()+"</font>");
		*/
		return ret;
	}
	
	public void holeBerichte(String patint,String rez){
/**********/

			final String xpatint = patint;
			final String xrez_nr = rez;

			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
			
					//String sstmt = "select * from verordn where PAT_INTERN ='"+xpatint+"' ORDER BY REZ_DATUM";
					Vector vec = SqlInfo.holeSaetze("berhist", 
							"berichtid," +
							"bertitel," +
							"verfasser," +
							"DATE_FORMAT(erstelldat,'%d.%m.%Y') AS derstelldat," +
							"empfaenger," +
							"DATE_FORMAT(editdat,'%d.%m.%Y') AS deditdat," +
							"empfid",
							"pat_intern='"+xpatint+"' ORDER BY erstelldat", Arrays.asList(new String[]{}));
					int anz = vec.size();
					for(int i = 0; i < anz;i++){
						if(i==0){
							dtblm.setRowCount(0);						
						}

						//int zzbild = 0;
						dtblm.addRow((Vector)vec.get(i));
					}
					PatGrundPanel.thisClass.jtab.setTitleAt(2,macheHtmlTitel(anz,"Therapieberichte"));
					if(anz > 0){
						setzeRezeptPanelAufNull(false);
						if(xrez_nr.equals("")){
							tabbericht.setRowSelectionInterval(0,0);							
						}else{
							for(int i = 0;i<anz;i++){
								if(((String)dtblm.getValueAt(i,1)).contains(xrez_nr)){
									tabbericht.setRowSelectionInterval(i,i);
									break;
								}
							}
						}

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
		
	public int berichtExistiert(String xrez){
		int ret = -1;
		int iberichte = dtblm.getRowCount();
		if(iberichte <= 0){
			return ret;
		}else{
			for(int i = 0; i < iberichte; i++){
				if(((String)dtblm.getValueAt(i,1)).contains(xrez)){
					tabbericht.setRowSelectionInterval(i, i);
					ret = new Integer(((String)dtblm.getValueAt(i,0)));
					break;
				}
			}
		}
		return ret;
		
	}
/*********/
	private void doBerichtDelete(){
		int wahl = tabbericht.getSelectedRow();
		if(wahl < 0){
			return;
		}
		int anfrage = JOptionPane.showConfirmDialog(null, "Wollen Sie den ausgewählten Bericht wirklich löschen", "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
		if(anfrage == JOptionPane.NO_OPTION){
			return;
		}
		String xpat_int = PatGrundPanel.thisClass.patDaten.get(29);
		String berid = (String)tabbericht.getValueAt(wahl,0);
		// zunächst aus der berhist löschen
		String xcmd = "delete from berhist where berichtid='"+berid+"'";
		new ExUndHop().setzeStatement(xcmd);
		// jetzt ermitteln ob tabelle bericht1 (Therapeuten) oder bericht2 (Arzt) betroffen ist. 
		String verfas = (String)dtblm.getValueAt(wahl, 3); 
		Vector vec = null;
		if(! verfas.toUpperCase().contains("REHA-ARZT")){
			// rez_nr ermitteln
			vec = SqlInfo.holeSatz("bericht1", "bertyp", " berichtid='"+berid+"'", Arrays.asList(new String[] {}));
			xcmd = "delete from bericht1 where berichtid='"+berid+"'";
			new ExUndHop().setzeStatement(xcmd);
		}else{
			xcmd = "delete from bericht2 where berichtid='"+berid+"'";
			new ExUndHop().setzeStatement(xcmd);
		}
		if(vec.size()>0){
			// in verordn und in lza löschversuch
			xcmd = "update verordn set berid='-1' where rez_nr='"+vec.get(0)+"'";
			System.out.println(xcmd);
			new ExUndHop().setzeStatement(xcmd);
			xcmd = "update lza set berid='-1' where rez_nr='"+vec.get(0)+"'";
			System.out.println(xcmd);
			new ExUndHop().setzeStatement(xcmd);			
		}else{
			System.out.println("Rezeptnummer konnte nicht ermittelt werden");
		}
		//tabbericht
		TableTool.loescheRow(tabbericht, wahl);
		int anzber = tabbericht.getRowCount();
		PatGrundPanel.thisClass.jtab.setTitleAt(2,macheHtmlTitel(anzber,"Therapieberichte"));
		if(anzber > 0){
		}else{
		}

		
		
	}
	public String holeVerfasser(){
		String verf = "";
		int row = tabbericht.getSelectedRow();
		if(row >=0){
			verf = (String) dtblm.getValueAt(row, 2);
		}
		return verf;
	}
	private void doThBerichtEdit(int row){
		String xreznr = (String) dtblm.getValueAt(row, 1);
		String[] splitrez = xreznr.split(" ");
		int bid = new Integer((String) dtblm.getValueAt(row, 0));
		String xverfasser = (String) dtblm.getValueAt(row, 2);
		//String xtitel = (String) dtblm.getValueAt(row, 1);
		//System.out.println("aufruf des Berichtes mit der ID "+bid);
		
		String[] splitdiag1 = xreznr.split("\\(");
		String[] splitdiag2 = splitdiag1[1].split("\\)");
		/*
		System.out.println("Die BerichtsID = ----------------->"+bid);
		System.out.println("Die Rezeptnummer = --------------->"+splitrez[2]);
		System.out.println("Die TB-Grupper = ----------------->"+splitdiag2[0]);
		System.out.println("Der Verfasser = ------------------>"+xverfasser);
		System.out.println("Aufruf aus Fenser Nr. = ---------->"+3);
		System.out.println("Tabellenreihe = ------------------>"+row);
		*/
		ArztBericht ab = new ArztBericht(null,"arztberichterstellen",false,splitrez[2],bid,3,xverfasser,splitdiag2[0],row);
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
		String[] column = 	{"ID","Titel","Verfasser","erstellt","Empfänger","letzte Änderung",""};
		dtblm.setColumnIdentifiers(column);
		tabbericht = new JXTable(dtblm);
		tabbericht.setEditable(true);
		tabbericht.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				if(arg0.getClickCount()==2){
					// hier prüfen welcher Berichtstyp und dementsprechend das Berichtsfenster öffnen
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
/*
		0		"berichtid," +
		1		"bertitel," +
		2		"verfasser," +
		3		"DATE_FORMAT(erstelldat,'%d.%m.%Y') AS derstelldat," +
		4		"empfaenger," +
		5		"DATE_FORMAT(editdat,'%d.%m.%Y') AS deditdat,
		6		"empfid",
*/
		tabbericht.getColumn(0).setMinWidth(50);
		tabbericht.getColumn(0).setMaxWidth(50);
		tabbericht.getColumn(1).setMinWidth(140);
		tabbericht.getColumn(3).setMaxWidth(80);
		tabbericht.getColumn(5).setMaxWidth(80);
		tabbericht.getColumn(6).setMinWidth(0);
		tabbericht.getColumn(6).setMaxWidth(0);

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
		tberbut[0].setToolTipText("neuer Bericht anlegen");
		tberbut[0].setActionCommand("berneu");
		tberbut[0].setEnabled(false);
		tberbut[0].addActionListener(this);		
		jtb.add(tberbut[0]);
		tberbut[1] = new JButton();
		tberbut[1].setIcon(SystemConfig.hmSysIcons.get("edit"));
		tberbut[1].setToolTipText("ausgewählten Bericht ändern//editieren");
		tberbut[1].setActionCommand("beredit");
		tberbut[1].addActionListener(this);		
		jtb.add(tberbut[1]);
		tberbut[2] = new JButton();
		tberbut[2].setIcon(SystemConfig.hmSysIcons.get("delete"));
		tberbut[2].setToolTipText("ausgewählten Bericht löschen");
		tberbut[2].setActionCommand("berdelete");
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
	String cmd = arg0.getActionCommand();
	if(cmd.equals("beredit")){
		int wahl = tabbericht.getSelectedRow();
		if(wahl < 0){
			return;
		}
		String verfas = (String)dtblm.getValueAt(wahl, 3); 
		if(! verfas.toUpperCase().contains("REHA-ARZT")){
			doThBerichtEdit(wahl);						
		}
	}
	if(cmd.equals("berdelete")){
		doBerichtDelete();
	}
	
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
	    	return false;
	    	/*
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
	        */
	      }
	   
}

/**************Ende Klasse************/
}