package hauptFenster;

import javax.swing.JDesktopPane;
import javax.swing.JButton;
import java.awt.Rectangle;

public class DeskTopTest extends JDesktopPane {

	private static final long serialVersionUID = 1L;
	static DeskTopTest thisClass;
	private JButton jButton = null;

	/**
	 * This is the default constructor
	 */
	public DeskTopTest() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);

		this.add(getJButton(), null);
		thisClass = this;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setBounds(new Rectangle(89, 117, 108, 62));
			jButton.setText("Schliessen");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
					thisClass.setVisible(false);
				}
			});
		}
		return jButton;
	}

}
