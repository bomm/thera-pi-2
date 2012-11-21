package verkauf;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

import jxTableTools.DoubleTableCellRenderer;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;


import CommonTools.JCompTools;
import CommonTools.SqlInfo;
import CommonTools.JRtaTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;


public class ArtikelSuchenDialog extends RehaSmartDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JXPanel pane = null;
	JRtaTextField suche = null;	
	JXTable tabelle = null;
	MyArtikelWahlModel tabellenModel = null;
	UebergabeTool ean = null;
	String search = null;
	ActionListener al = null;
	FocusListener fl = null;
	KeyListener kl = null;
	MouseListener ml = null;
	RehaTPEventClass rtp = null;
	PinPanel pinPanel = null;
	
	public ArtikelSuchenDialog(Frame owner, UebergabeTool ean, Point position,String initsearch) {
		super(null, "ArtSuchen");
		this.ean = ean;
		this.search = initsearch;
		this.activateListener();
		this.setSize(300, 400);
		this.setLocation(position);
		this.setUndecorated(true);
		
		pinPanel = new PinPanel();
		pinPanel.getGruen().setVisible(false);
		pinPanel.setName("ArtSuchen");
		setPinPanel(pinPanel);
		getSmartTitledPanel().setContentContainer(getJContentPane());
		getSmartTitledPanel().getContentContainer().setName("ArtSuchen");
		getSmartTitledPanel().setTitle("Aritkel suchen");
		
		
		//this.setContentPane(getJContentPane()); 
		this.setName("ArtSuchen");
		//this.setModal(true); //ausgeschaltet /st.
		this.setResizable(true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		//this.setzeFocus(); //ausgeschaltet /st.
		//this.setVisible(true); //ausgeschaltet /st.
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				if(!suche.getText().trim().equals("")){
					holeDaten();
				}
				return null;
			}
			
		}.execute();
	}
	
	public void setzeFocus(){
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
			
			//pane.add(getJXTitledPanel(), cc.xyw(1, 1, 5));
			
			JLabel lab = new JLabel("Suchtext:");
			pane.add(lab, cc.xy(2, 3));
			
			suche = new JRtaTextField("nix",true);
			suche.setName("suche");
			suche.addFocusListener(fl);
			suche.addKeyListener(kl);
			pane.add(suche, cc.xy(4, 3));
			
			if(ean.getString() != null) {
				this.suche.setText(ean.getString());
				ean.setString(null);
			}
			
			String[] spaltenNamen = {"Artikel-ID", "Beschreibung", "Preis"};
			tabellenModel = new MyArtikelWahlModel();
			tabellenModel.setColumnIdentifiers(spaltenNamen);
			tabelle = new JXTable(tabellenModel);
			tabelle.addKeyListener(kl);
			tabelle.addMouseListener(ml);
			tabelle.setEditable(false);
			tabelle.getColumn(2).setCellRenderer(new DoubleTableCellRenderer());
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
	class MyArtikelWahlModel extends DefaultTableModel{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Class<?> getColumnClass(int columnIndex) {
			   if(columnIndex==2 ){
				   return Double.class;}
			   else{
				   return String.class;
			   }
	    }
	}
	//in RehaSmartDialog bereits enthalten
	/*
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
	*/
	
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
					if(arg0.getSource() instanceof JRtaTextField){
						if(arg0.getKeyCode() == KeyEvent.VK_ENTER ){
							holeDaten();
							suche.requestFocus();
						}else if(arg0.getKeyCode() == KeyEvent.VK_DOWN){
							if(tabelle.getRowCount() <= 0){return;}
							if( tabelle.getSelectedRow() <= 0){
								tabelle.setRowSelectionInterval(0,0);
							}
							SwingUtilities.invokeLater(new Runnable(){
								public void run(){
									tabelle.requestFocus();		
								}
							});
						}else if(arg0.getKeyCode() == KeyEvent.VK_ESCAPE){
							schliessen(false);
						}
					}else if(arg0.getSource() instanceof JXTable){
						if(arg0.getKeyCode() == KeyEvent.VK_ENTER ){
							schliessen(true);							
						}else if( (arg0.getKeyCode() == KeyEvent.VK_A) &&
								arg0.isAltDown()){
							suche.requestFocus();
						}else if(arg0.getKeyCode() == KeyEvent.VK_ESCAPE){
							schliessen(false);
						}
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
					schliessen(true);
				}
				
			}
			
		};
		
		ml = new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getClickCount() == 2) {
					schliessen(true);
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
		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener((RehaTPEventListener) this);
	}
	
	
	private void holeDaten() {
		String sql;
		tabellenModel.setRowCount(0);
		if(suche.getText().equals("")) {
			sql = "SELECT ean, beschreibung, preis FROM verkartikel;";
		} else {
			String kriterium = suche.getText();
			sql = "SELECT ean, beschreibung, preis FROM verkartikel WHERE ean LIKE '%"+ kriterium +"%'" +
					" OR beschreibung LIKE '%"+ kriterium +"%';";
		}
		//Renderer und Editoren gehen offensichtlich nicht mit setDataVector
		/*
		Vector<String> spaltenNamen = new Vector<String>();
		spaltenNamen.add("Artikel-ID");
		spaltenNamen.add("Beschreibung");
		spaltenNamen.add("Preis");
		tabellenModel.setDataVector(SqlInfo.holeFelder(sql), spaltenNamen);
		*/
		Vector<Vector<String>> spaltenWerte = SqlInfo.holeFelder(sql);
		int size = spaltenWerte.size();
		for(int i = 0; i < size; i++){
			tabellenModel.addRow(spaltenWerte.get(i));
		}
		if(tabellenModel.getRowCount() > 0){
			tabelle.setRowSelectionInterval(0,0);
		}
		tabelle.repaint();
	}
	
	private void schliessen(boolean eansetzen) {
		int row = tabelle.getSelectedRow();
		//ungünstig, da ein Artikel übergeben wird sobald eine Tabellenzeile selektiert ist,
		//obwohl evtl. der rote Knopf für abbrechen angeklickt wurde
		if(eansetzen){
			if(row != -1) {
				ean.setString((String)tabellenModel.getValueAt(row, 0));
			}
		}
		if(rtp != null){
			rtp.removeRehaTPEventListener((RehaTPEventListener) this);
			rtp = null;
			pinPanel = null;
		}
		this.setVisible(false);
		this.dispose();
	}
	public void rehaTPEventOccurred(RehaTPEvent evt) {
		try{
			if(evt.getDetails()[0].equals("ArtSuchen")){
				this.setVisible(false);
				rtp.removeRehaTPEventListener((RehaTPEventListener) this);
				rtp = null;
				pinPanel = null;
				this.dispose();
			}
		}catch(NullPointerException ne){
		}
	}	

}
