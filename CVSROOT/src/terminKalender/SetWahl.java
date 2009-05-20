package terminKalender;

import javax.swing.JPanel;
import java.awt.Frame;
import java.awt.BorderLayout;
import javax.swing.JDialog;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import java.util.ArrayList;

import javax.swing.JList;

import systemEinstellungen.SystemConfig;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.List;
import java.awt.Cursor;
import java.awt.Button;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.ImageIcon;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;

import java.awt.FlowLayout;

public class SetWahl extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;  //  @jve:decl-index=0:visual-constraint="505,22"
	private JPanel jPanelTasten = null;
	private String[] behandler;
	private String setname;
	private JScrollPane jScrollPane = null;
	private JList jList = null;
	private JButton jButton = null;
	private JButton jButton1 = null;
	public int ret = 0;
	private JList jList1 = null;
	private int wahl;
	private TerminFenster Eltern; 
	/**
	 * @param owner
	 */
	public SetWahl(TerminFenster Eltern) {
		super();
		wahl = ((TerminFenster) Eltern).AktuellesSet();
		initialize();
	}

	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(293, 247);
		this.setTitle("Terminset auswählen");
		this.setBackground(Color.WHITE);
		this.setContentPane(getJContentPane());
		this.setModal(true);
		this.jList1.setSelectedIndex(this.wahl);
		this.setUndecorated(true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JXTitledPanel();
			JXPanel jcon = (JXPanel)((JXTitledPanel) jContentPane).getContentContainer();
			((JXTitledPanel) jContentPane).setTitle("Behandler - Set auswählen....");
			((JXTitledPanel) jContentPane).setTitleForeground(Color.WHITE);
//			((JXTitledPanel) jContentPane).getContentContainer().setLayout(new BorderLayout());
//			((JXTitledPanel) jContentPane).getContentContainer().add(getJPanelTasten(), BorderLayout.SOUTH);
//			((JXTitledPanel) jContentPane).getContentContainer().add(getJScrollPane(), BorderLayout.CENTER);
			jcon.setBackground(Color.WHITE);
			jcon.setLayout(new BorderLayout());
			jcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			jcon.add(getJPanelTasten(), BorderLayout.SOUTH);
			jcon.add(getJScrollPane(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanelTasten	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelTasten() {
		if (jPanelTasten == null) {
			jPanelTasten = new JPanel();
			jPanelTasten.setLayout(new FlowLayout());
			jPanelTasten.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
			jPanelTasten.setPreferredSize(new Dimension(0, 35));
			jPanelTasten.add(getJButton(), null);
			jPanelTasten.add(getJButton1(), null);
		}
		return jPanelTasten;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setBackground(Color.WHITE);
			jScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			jScrollPane.setViewportView(getJList1());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jList	
	 * 	
	 * @return javax.swing.JList	
	 */
	
	/**
	 * This method initializes jList	
	 * 	
	 * @return javax.swing.JList	
	 */

	private void ListeFuellen(DefaultListModel model){
		int i,max = 0;
		max = SystemConfig.aTerminKalender.size();
		String[] fach = new String[max];
		for(i=0;i<max;i++){
			model.add(i,(String)((ArrayList)SystemConfig.aTerminKalender.get(i).get(0)).get(0));
			//fach[i] = (String)((ArrayList)SystemConfig.aTerminKalender.get(i).get(0)).get(0);
		}
		//jList.setName((String)((ArrayList)SystemConfig.aTerminKalender.get(0).get(0)).get(0));
		return;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("Ok");
			jButton.setPreferredSize(new Dimension(102, 20));
			//jButton.setIcon(new ImageIcon("C:/MeinWorkspace/pics/ok.gif"));

			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
					DialogBeenden(jList1.getSelectedIndex());
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setPreferredSize(new Dimension(102, 20));
			jButton1.setText("Abbruch");
			//jButton1.setIcon(new ImageIcon("C:/MeinWorkspace/pics/nichtok.gif"));
			jButton1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
					DialogBeenden(-1);
				}
			});
		}
		return jButton1;
	}
	private void DialogBeenden(int wie){
		TerminFenster.swSetWahl = wie;
		this.ret = wie;
		this.dispose();
	}

	/**
	 * This method initializes jList1	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getJList1() {
		final DefaultListModel model = new DefaultListModel();
		if (jList1 == null) {
			jList1 = new JList(model);
			jList1.addKeyListener(new java.awt.event.KeyAdapter() {   
				public void keyPressed(java.awt.event.KeyEvent e) {    
					if (e.getKeyCode()==10){
						DialogBeenden(jList1.getSelectedIndex());
					}
					if (e.getKeyCode()==27){
						DialogBeenden(-1);
					}
				}
			});
			jList1.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					if (e.getClickCount() == 2){
						DialogBeenden(jList1.getSelectedIndex());
					}
					
				}
			});
			ListeFuellen(model);
		}
		return jList1;
	}
}  //  @jve:decl-index=0:visual-constraint="103,28"
