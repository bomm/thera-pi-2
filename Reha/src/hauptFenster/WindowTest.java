package hauptFenster;

import javax.swing.JPanel;

import java.awt.Component;
import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.Window;

import javax.swing.JWindow;
import javax.swing.JButton;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;

import java.awt.Dimension;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.border.SoftBevelBorder;
import org.jdesktop.swingx.JXTitledPanel;
import javax.swing.ImageIcon;
import javax.swing.JTextField;

public class WindowTest extends JWindow {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JButton jButton = null;
	private JXFrame eltern = null;
	private boolean clicked = false;
	private WindowTest thisWin = null;  //  @jve:decl-index=0:visual-constraint="10,51"
	private JXTitledPanel JXTitledPanel = null;
	private JXPanel jContent = null;
	private JTextField jTextField = null;
	/**
	 * @param component
	 */
	public WindowTest(JXFrame owner) {
		super();
		ElternSetzen(owner);
		initialize();
		
	}
	private void ElternSetzen(JXFrame eltern){
		this.eltern = eltern;
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setContentPane(getJContentPane());
		thisWin = this;
		thisWin.setSize(new Dimension(321, 163));
		
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.setBackground(new Color(238, 238, 65));
			jContentPane.setBorder(BorderFactory.createCompoundBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED), null));
			jContentPane.add(getJButton(), BorderLayout.SOUTH);
			jContentPane.add(getJXTitledPanel(), BorderLayout.CENTER);
			jContentPane.addMouseListener(new java.awt.event.MouseAdapter() {   
				public void mouseReleased(java.awt.event.MouseEvent e) {    
					//System.out.println("mouseReleased()"); // TODO Auto-generated Event stub mouseReleased()
					clicked = false;
				}   
				public void mousePressed(java.awt.event.MouseEvent e) {    
					//System.out.println("mousePressed()"); // TODO Auto-generated Event stub mousePressed()
					clicked = true;
				}
			});
			jContentPane.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {   
				public void mouseDragged(java.awt.event.MouseEvent e) {    
					//System.out.println("mouseDragged()"+e); // TODO Auto-generated Event stub mouseDragged()
					thisWin.setLocation(e.getX(),e.getY());
					//thisWin.repaint();
				}
			});
			jContentPane.addFocusListener(new java.awt.event.FocusAdapter() {
				public void focusLost(java.awt.event.FocusEvent e) {
					// TODO Auto-generated Event stub focusLost()
					thisWin.toFront();
				}
			});
		}
		return jContentPane;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setPreferredSize(new Dimension(34, 25));
			jButton.setIcon(new ImageIcon("C:/MeinWorkspace/pics/exit.png"));
			jButton.setText("Schliessen");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
					((Component) e.getSource()).getParent().setVisible(false);
					eltern.validate();
					Reha.thisFrame.getContentPane().invalidate();
					Reha.jContainerOben.revalidate();
					Reha.jContainerUnten.revalidate();
					Reha.thisFrame.getContentPane().repaint();
					((JPanel) Reha.thisFrame.getContentPane()).updateUI();
				}
			});
		}
		return jButton;
	}
	/**
	 * This method initializes JXTitledPanel	
	 * 	
	 * @return org.jdesktop.swingx.JXTitledPanel	
	 */
	private JXTitledPanel getJXTitledPanel() {
		if (JXTitledPanel == null) {
			JXTitledPanel = new JXTitledPanel();
			JXTitledPanel.setTitle("Demo Hilfsfenster");
			jContent = new JXPanel(new BorderLayout());
			jContent.add(getJTextField(),BorderLayout.NORTH);
			JXTitledPanel.setContentContainer(jContent);

		}
		return JXTitledPanel;
	}
	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setPreferredSize(new Dimension(50, 20));
		}
		return jTextField;
	}

}
