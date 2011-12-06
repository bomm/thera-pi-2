package verkauf;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.thera_pi.nebraska.gui.utils.JCompTools;

import systemTools.JRtaTextField;
import verkauf.model.Artikel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class LagerGUI extends JXPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private VerkaufTab owner;
	private DefaultTableModel lgmod;
	private JXTable lgtab;
	private JScrollPane jscr;
	private Vector<String> columns;
	private ArtikelDialog adlg;
	private JRtaTextField sucheText = new JRtaTextField("nix", false);
	
	LagerGUI(VerkaufTab owner) {
		super();
		this.owner = owner;
		
		columns = new Vector<String>();
		
		columns.add("Artikel-ID");
		columns.add("Beschreibung");
		columns.add("VK-Preis");
		columns.add("EK-Preis");
		columns.add("Lieferant");
		columns.add("Lagerstand");
		columns.add("");
		
		this.setOpaque(false);
		this.setLayout(new BorderLayout());
		this.add(getContent(), BorderLayout.CENTER);
		this.setLastRowSelected();
		
	}
	
	private JXPanel getContent() {
		JXPanel pane = new JXPanel();
		pane.setOpaque(false);
		
		String xwerte = "5dlu, p:g, 5dlu";
		String ywerte = "5dlu, p, 5dlu, p:g, 5dlu";
		
		FormLayout lay = new FormLayout(xwerte, ywerte);
		CellConstraints cc = new CellConstraints();
		pane.setLayout(lay);
		
		pane.add(this.owner.getToolbar(sucheText), cc.xy(2,2));
		
		lgmod = new DefaultTableModel();
		lgmod.setColumnIdentifiers(columns);
		lgtab = new JXTable(lgmod);
		lgtab.getColumn(6).setMinWidth(0);
		lgtab.getColumn(6).setMaxWidth(0);
		lgtab.setEditable(false);
		this.setzeTabDaten(Artikel.liefereArtikelDaten());
		lgtab.addMouseListener(new MouseListener() {
			
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getClickCount() == 2) {
					owner.aktiviereFunktion(VerkaufTab.edit);
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
		jscr = JCompTools.getTransparentScrollPane(lgtab);
		jscr.validate();
		pane.add(jscr, cc.xy(2, 4, CellConstraints.FILL, CellConstraints.FILL));
		
		pane.validate();
		return pane;
	}
	
	public void aktiviereFunktion(int befehl) {
		if(befehl == VerkaufTab.neu) {
			doArtikelDialog(-1);
			this.setzeTabDaten(Artikel.liefereArtikelDaten());
			this.setLastRowSelected();
		} else if(befehl == VerkaufTab.edit) {
			if(this.lgtab.getSelectedRow() >= 0) {
				doArtikelDialog(Integer.parseInt((String)this.lgmod.getValueAt(this.lgtab.getSelectedRow(), this.lgmod.getColumnCount()-1)));
				this.setzeTabDaten(Artikel.liefereArtikelDaten());
				this.setLastRowSelected();
			} else {
				JOptionPane.showMessageDialog(null, "Wen oder was willst du ändern?");
			}
		} else if(befehl == VerkaufTab.delete) {
			if(this.lgtab.getSelectedRow() >= 0) {
				Artikel.loescheArtikel(Integer.parseInt((String)this.lgmod.getValueAt(this.lgtab.getSelectedRow(), this.lgmod.getColumnCount()-1)));
				this.setzeTabDaten(Artikel.liefereArtikelDaten());
				this.setLastRowSelected();
			} else {
				JOptionPane.showMessageDialog(null, "Wen oder was willst du löschen?");
			}
		} else if(befehl == VerkaufTab.suche) {
			this.setzeTabDaten(Artikel.sucheArtikelDaten(this.sucheText.getText()));
			this.setLastRowSelected();
		} else if(befehl == VerkaufTab.reload) {
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					setzeTabDaten(Artikel.liefereArtikelDaten());	
					setLastRowSelected();
				}
			});
		}
	}
	
	private void setzeTabDaten(Vector<Vector<String>> daten) {
		/*
		this.lgmod.setDataVector(daten, this.columns);
		this.lgtab.getColumn(this.lgmod.getColumnCount()-1).setMinWidth(0);
		this.lgtab.getColumn(this.lgmod.getColumnCount()-1).setMaxWidth(0);
		*/
		lgmod.setRowCount(0);
		for(int i = 0; i < daten.size();i++){
			lgmod.addRow(daten.get(i));
		}
		lgtab.repaint();
	}
	
	private void doArtikelDialog(int id) {
		adlg = new ArtikelDialog(id, this.owner.holePosition(300, 300));
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				adlg.setzeFocus();				
			}
		});
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				while(! adlg.getTextField().hasFocus()){
					adlg.setzeFocus();
					Thread.sleep(25);
					//System.out.println("erzwinge Focus");
				}
				return null;
			}
		}.execute();
		adlg.setModal(true);
		adlg.setVisible(true);
		adlg = null;
	}
	public void aufraeumen(){
		//hier sollten die Listener removed werden
		//anschließend die Listener genullt
	}
	
	private void setLastRowSelected() {
		if(this.lgmod.getRowCount() > 0) {
			this.lgtab.setRowSelectionInterval(this.lgmod.getRowCount()-1, this.lgmod.getRowCount()-1);
		}
	}
	
}
