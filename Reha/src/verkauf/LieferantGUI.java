package verkauf;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.thera_pi.nebraska.gui.utils.JCompTools;

import verkauf.model.Lieferant;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class LieferantGUI extends JXPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private VerkaufTab owner;
	private Vector<String> columns;
	private JScrollPane jscr;
	private LieferantDialog lfdlg;
	private JXTable lftab;
	private DefaultTableModel lfmod;
	
	LieferantGUI(VerkaufTab owner) {
		super();
		this.owner = owner;
		this.setLayout(new BorderLayout());
		this.setOpaque(false);
		
		this.columns = new Vector<String>();
		this.columns.add("Name");
		this.columns.add("Ansprechpartner");
		this.columns.add("Telefon");
		this.columns.add("Telefax");
		this.columns.add("Adresse");
		this.columns.add("PLZ");
		this.columns.add("Ort");
		this.columns.add("");
		
		this.add(getContent(), BorderLayout.CENTER);
	}
	
	private JXPanel getContent() {
		JXPanel pane = new JXPanel();
		pane.setOpaque(false);
		String xwerte = "5dlu, p:g, 5dlu";
		String ywerte = "5dlu, p:g, 5dlu";
		FormLayout lay = new FormLayout(xwerte, ywerte);
		CellConstraints cc = new CellConstraints();
		pane.setLayout(lay);
		
		lfmod = new DefaultTableModel();
		lfmod.setColumnIdentifiers(columns);
		lftab = new JXTable(lfmod);
		lftab.getColumn(7).setMinWidth(0);
		lftab.getColumn(7).setMaxWidth(0);
		lftab.setEditable(false);
		lftab.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getClickCount() == 2) {
					aktiviereFunktion(VerkaufTab.edit);
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
			
		});
		this.setzeTabDaten(Lieferant.liefereLieferantenDaten());
		jscr = JCompTools.getTransparentScrollPane(lftab);
		jscr.validate();
		pane.add(jscr, cc.xy(2, 2));
		
		pane.add(new JXLabel(), cc.xy(3, 3));
		
		pane.validate();
		return pane;
	}
	
	public void aktiviereFunktion(int befehl) {
		if (befehl == VerkaufTab.neu) {
			this.doLieferantDialog(-1, this.owner.holePosition(300, 300));
			this.setzeTabDaten(Lieferant.liefereLieferantenDaten());
		} else if(befehl == VerkaufTab.edit) {
			if(this.lftab.getSelectedRow() >= 0) {
				this.doLieferantDialog(Integer.parseInt((String)this.lfmod.getValueAt(this.lftab.getSelectedRow(), 7)), this.owner.holePosition(300, 300));
				this.setzeTabDaten(Lieferant.liefereLieferantenDaten());
			}  else {
				JOptionPane.showMessageDialog(null, "Wen oder was willst du ändern?");
			}
		} else if(befehl == VerkaufTab.delete) {
			if(this.lftab.getSelectedRow() >= 0) {
				Lieferant.loesche(Integer.parseInt((String)this.lfmod.getValueAt(this.lftab.getSelectedRow(), 7)));
				this.setzeTabDaten(Lieferant.liefereLieferantenDaten());
			} else {
				JOptionPane.showMessageDialog(null, "Wen oder was willst du löschen?");
			}
		} else if(befehl == VerkaufTab.suche) {
			this.setzeTabDaten(Lieferant.sucheLieferantenDaten(this.owner.sucheText.getText()));
		}
	}
	
	private void doLieferantDialog(int id,Point pt){
		lfdlg = new LieferantDialog(id,pt);
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				while(! lfdlg.getTextField().hasFocus()){
					lfdlg.setzeFocus();
					Thread.sleep(25);
					//System.out.println("erzwinge Focus");
				}
				return null;
			}
		}.execute();
		lfdlg.setModal(true);
		lfdlg.setVisible(true);
		lfdlg = null;
	}
	
	private void setzeTabDaten(Vector<Vector<String>> daten) {
		/*
		this.lfmod.setDataVector(daten, columns);
		this.lftab.getColumn(lfmod.getColumnCount()-1).setMinWidth(0);
		this.lftab.getColumn(lfmod.getColumnCount()-1).setMaxWidth(0);
		*/
		lfmod.setRowCount(0);
		for(int i = 0; i < daten.size();i++){
			lfmod.addRow(daten.get(i));
		}
		lftab.repaint();
	}
	public void aufraeumen(){
		//hier sollten die Listener removed werden
		//anschließend die Listener genullt
	}


}
