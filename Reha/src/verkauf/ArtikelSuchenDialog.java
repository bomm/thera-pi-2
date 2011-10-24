package verkauf;

import hauptFenster.Reha;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;
import org.thera_pi.nebraska.gui.utils.JCompTools;

import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemTools.JRtaTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


public class ArtikelSuchenDialog extends JXDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JXPanel pane = null;
	JRtaTextField suche = null;	
	JXTable tabelle = null;
	DefaultTableModel tabellenModel = null;
	UebergabeTool ean = null;
	ActionListener al = null;
	FocusListener fl = null;
	KeyListener kl = null;
	MouseListener ml = null;
	
	public ArtikelSuchenDialog(Frame owner, UebergabeTool ean, Point position) {
		super(owner, (JComponent)Reha.thisFrame.getGlassPane());
		this.ean = ean;
		this.activateListener();
		this.setSize(300, 400);
		this.setLocation((int) (position.getX() - (this.getWidth() / 2)), (int) (position.getY() + 20));
		this.setUndecorated(true);
		this.setContentPane(getJContentPane());
		this.setName("ArtSuchen");
		this.setModal(true);
		this.setResizable(true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setzeFocus();
		this.setVisible(true);
	}
	
	private void setzeFocus(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				suche.requestFocus();
			}
		});
	}
	
	private JPanel getJContentPane() {
		if(pane == null) {
			pane = new JXPanel();
			pane.setBorder(new EtchedBorder(Color.white, Color.gray));
			
			//			      1     2      3     4        5
			String xwerte = "5dlu, 40dlu, 5dlu, 80dlu:g, 5dlu";
			//              1   2    3   4     5         6    7   8
			String ywerte ="p, 5dlu, p, 5dlu, 120dlu:g, 5dlu, p, 5dlu ";
			
			FormLayout lay = new FormLayout(xwerte, ywerte);
			CellConstraints cc = new CellConstraints();
			
			pane.setLayout(lay);
			pane.setBackground(Color.white);
			
			pane.add(getJXTitledPanel(), cc.xyw(1, 1, 5));
			
			JLabel lab = new JLabel("Suchtext:");
			pane.add(lab, cc.xy(2, 3));
			
			suche = new JRtaTextField("nix",true);
			suche.setName("suche");
			suche.addFocusListener(fl);
			pane.add(suche, cc.xy(4, 3));
			
			String[] spaltenNamen = {"Artikel-ID", "Beschreibung", "Preis"};
			tabellenModel = new DefaultTableModel();
			tabellenModel.setColumnIdentifiers(spaltenNamen);
			tabelle = new JXTable(tabellenModel);
			tabelle.addKeyListener(kl);
			JScrollPane scr = JCompTools.getTransparentScrollPane(tabelle);
			scr.validate();	
			pane.add(scr, cc.xyw(2, 5, 3));
			
			JXButton close = new JXButton("schliessen");
			close.setActionCommand("schliessen");
			close.addActionListener(al);
			pane.add(close, cc.xyw(2, 7, 3)); //actionlistener!
		
			pane.validate();
		}
		return pane;		
	}
	
	private JXTitledPanel getJXTitledPanel() {
		JXTitledPanel panel = new JXTitledPanel();
		
		JXButton close = new JXButton();
		close.setBorder(null);
		close.setOpaque(false);
		close.setPreferredSize(new Dimension(16, 16));
		close.setIcon(SystemConfig.hmSysIcons.get("rot"));
		close.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				schliessen();
			}
		});
		panel.setRightDecoration(close);
		
		panel.setTitle("Aritkel suchen");
		panel.setTitleForeground(Color.white);
		panel.setBorder(null);
		return panel;
	}
	
	private void activateListener() {
		fl = new FocusListener() {

			@Override
			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				holeDaten();
			}
			
		};
		
		kl = new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode() == KeyEvent.VK_ENTER || arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
					schliessen();
				}
				
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		};
		
		al = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(arg0.getActionCommand().equals("schliessen")) {
					schliessen();
				}
				
			}
			
		};
		
		ml = new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getClickCount() == 2) {
					schliessen();
				}
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		};
	}
	
	private void holeDaten() {
		String sql;
		if(suche.getText().equals("")) {
			sql = "SELECT `ean`, `beschreibung`, `preis` FROM verkartikel;";
		} else {
			String kriterium = suche.getText();
			sql = "SELECT `ean`, `beschreibung`, `preis` FROM verkartikel WHERE `ean` LIKE '%"+ kriterium +"%'" +
					" OR `beschreibung` LIKE '%"+ kriterium +"%';";
		}
		Vector<String> spaltenNamen = new Vector<String>();
		spaltenNamen.add("Artikel-ID");
		spaltenNamen.add("Beschreibung");
		spaltenNamen.add("Preis");
		
		tabellenModel.setDataVector(SqlInfo.holeFelder(sql), spaltenNamen);
	}
	
	private void schliessen() {
		int row = tabelle.getSelectedRow();
		if(row != -1) {
			ean.setString((String)tabellenModel.getValueAt(row, 0));
		}
		this.setVisible(false);
	}

}
