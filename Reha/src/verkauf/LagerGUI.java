package verkauf;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.thera_pi.nebraska.gui.utils.JCompTools;

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
		
	}
	
	private JXPanel getContent() {
		JXPanel pane = new JXPanel();
		pane.setOpaque(false);
		
		String xwerte = "5dlu, p:g, 5dlu";
		String ywerte = "5dlu, p:g, 5dlu";
		
		FormLayout lay = new FormLayout(xwerte, ywerte);
		CellConstraints cc = new CellConstraints();
		pane.setLayout(lay);
		
		lgmod = new DefaultTableModel();
		lgtab = new JXTable(lgmod);
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
		pane.add(jscr, cc.xy(2, 2));
		
		pane.validate();
		return pane;
	}
	
	public void aktiviereFunktion(int befehl) {
		if(befehl == VerkaufTab.neu) {
			doArtikelDialog(-1);
			this.setzeTabDaten(Artikel.liefereArtikelDaten());
		} else if(befehl == VerkaufTab.edit) {
			if(this.lgtab.getSelectedRow() >= 0) {
				doArtikelDialog(Integer.parseInt((String)this.lgmod.getValueAt(this.lgtab.getSelectedRow(), this.lgmod.getColumnCount()-1)));
				this.setzeTabDaten(Artikel.liefereArtikelDaten());
			} else {
				JOptionPane.showMessageDialog(null, "Wenn oder was willst du ändern?");
			}
		} else if(befehl == VerkaufTab.delete) {
			if(this.lgtab.getSelectedRow() >= 0) {
				Artikel.loescheArtikel(Integer.parseInt((String)this.lgmod.getValueAt(this.lgtab.getSelectedRow(), this.lgmod.getColumnCount()-1)));
				this.setzeTabDaten(Artikel.liefereArtikelDaten());
			} else {
				JOptionPane.showMessageDialog(null, "Wenn oder was willst du löschen?");
			}
		} else if(befehl == VerkaufTab.suche) {
			this.setzeTabDaten(Artikel.sucheArtikelDaten(this.owner.sucheText.getText()));
		}
	}
	
	private void setzeTabDaten(Vector<Vector<String>> daten) {
		this.lgmod.setDataVector(daten, this.columns);
		this.lgtab.getColumn(this.lgmod.getColumnCount()-1).setMinWidth(0);
		this.lgtab.getColumn(this.lgmod.getColumnCount()-1).setMaxWidth(0);
	}
	
	private void doArtikelDialog(int id) {
		adlg = new ArtikelDialog(id, this.owner.holePosition(300, 300));
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				adlg.setzeFocus();				
			}
		});
		adlg.setModal(true);
		adlg.setVisible(true);
		adlg = null;
	}
	
}
