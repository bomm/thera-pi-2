package menus;

import hauptFenster.AktiveFenster;
import hauptFenster.ProgLoader;
import hauptFenster.Reha;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.jdesktop.swingx.JXTitledPanel;

import terminKalender.TerminFenster;

import RehaInternalFrame.JRehaInternal;

import java.awt.Component;
import java.awt.Event;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;

public class TerminMenu {

	private JMenu jMenu = null;  //  @jve:decl-index=0:visual-constraint="363,126"
	private JMenuItem TermStart = null;
	private JMenuItem RoogleStart = null;	
	public TerminMenu thisClass;  //  @jve:decl-index=0:
	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	public JMenu getJMenu() {
		if (jMenu == null) {
			jMenu = new JMenu();
			thisClass = this;
			jMenu.setFont(new Font("Dialog", Font.PLAIN, 12));
			jMenu.setText("Terminkalender");
			jMenu.add(getTermStart());
			jMenu.add(getRoogleStart());
			jMenu.addSeparator();
			jMenu.add(getWochenArbeitszeit());
		}
		return jMenu;
	}

	/**
	 * This method initializes TermStart	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getTermStart() {
		if (TermStart == null) {
			TermStart = new JMenuItem();
			TermStart.setText("Terminkalender starten");
			TermStart.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, Event.CTRL_MASK, false));
			TermStart.setMnemonic(KeyEvent.VK_T);
			TermStart.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("Terminkalenderaufruf-Menü"); // TODO Auto-generated Event stub actionPerformed()
					JComponent termin = AktiveFenster.getFensterAlle("TerminFenster");
					Reha.thisClass.progLoader.ProgTerminFenster(0, 0);
					//ProgLoader.ProgTerminFenster(0,0);
					if(termin == null){

						//ProgLoader.ProgTerminFenster(0,0);
					}else{
						//((JRehaInternal)termin).feuereEvent(25554);
						//TerminFenster.thisClass.getViewPanel().requestFocus();
					}
				}
			});
			
		}
		return TermStart;
	}

	private JMenuItem getRoogleStart() {
		RoogleStart = new JMenuItem();
		RoogleStart.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK, false));
		RoogleStart.setText("Roogle - die Suchmaschine für Termine");
		RoogleStart.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				System.out.println("Roogle"); // TODO Auto-generated Event stub actionPerformed()
				Reha.thisClass.messageLabel.setText("Roogle");
				Reha.thisClass.progLoader.ProgRoogleFenster(0,null);
				//ProgLoader.ProgRoogleFenster(0,null);
			}
		});

		return RoogleStart;
	}
	private JMenuItem getWochenArbeitszeit() {
		JMenuItem waz = new JMenuItem();
		waz.setText("Wochenarbeitszeit definieren");
		waz.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				System.out.println("Roogle"); // TODO Auto-generated Event stub actionPerformed()
				Reha.thisClass.messageLabel.setText("Wochenarbeitszeit definieren");
				//ProgLoader.ProgTerminFenster(0,2);
				Reha.thisClass.progLoader.ProgTerminFenster(0, 2);
			}
		});

		return waz;
	}

}
