package terminKalender;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JLabel;
import java.awt.Rectangle;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Color;
import java.util.Date;
import java.awt.event.KeyEvent;
import java.awt.Font;
import javax.swing.ImageIcon;

import systemTools.JRtaTextField;

public class tagWahl {

	private JDialog jDialog = null;  //  @jve:decl-index=0:visual-constraint="184,28"
	private JPanel jContentPane = null;
	private JLabel jLabel = null;
	//public DateInputTextField jTextField = null;
	public 	JRtaTextField jTextField = null;
	private JButton jButton = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private JButton jButton1 = null;
/*******************/
	public  int ergebnis = 0;
	private String tag;  //  @jve:decl-index=0:
	private JLabel jLabel3 = null;
	/**
	 * This method initializes jDialog	
	 * 	
	 * @return javax.swing.JDialog	
	 */
	public JDialog getJDialog(String tag,String sDatText,String sTitel,int x, int y) {
		if (jDialog == null) {
			this.tag = tag;
			jDialog = new JDialog();
			jDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			jDialog.setSize(new Dimension(295, 153));
			//int x1 = ((x-jDialog.getSize().width)/2);
			//int y1 = ((y-jDialog.getSize().height)/2);			
			jDialog.setLocation(x-(jDialog.getWidth()/2), y-(jDialog.getHeight()/2));
			jDialog.setTitle(sTitel);
			//jDialog.setModal(true);
			jDialog.setContentPane(getJContentPane(sDatText,tag));
			jTextField.setCaretPosition(0);
		}
		return jDialog;
	}

	/**
	 * This method initializes jContentPane	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJContentPane(String sDatText,String tag) {
		//datFunk df = new datFunk();
		if (jContentPane == null) {
			jLabel3 = new JLabel();
			jLabel3.setBounds(new Rectangle(160, 54, 86, 16));
			jLabel3.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
			jLabel3.setFont(new Font("Dialog", Font.ITALIC, 12));
			jLabel3.setText(DatFunk.WochenTag(tag));
			jLabel2 = new JLabel();
			jLabel2.setBounds(new Rectangle(160, 5, 101, 16));
			jLabel2.setForeground(Color.red);
			jLabel2.setText(sDatText);
			jLabel2.setText(sDatText);
			//			jLabel2.setText(new globVars().WochenTag[0]);
			jLabel1 = new JLabel();
			jLabel1.setBounds(new Rectangle(25, 5, 116, 16));
			jLabel1.setText("aktuelle Darstellung:");
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(45, 35, 101, 16));
			jLabel.setText("Datum eingeben:");
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(jLabel, null);
			jContentPane.add(getJTextField(), null);
			jContentPane.add(getJButton(), null);
			jContentPane.add(jLabel1, null);
			jContentPane.add(jLabel2, null);
			jContentPane.add(getJButton1(), null);
			jContentPane.add(jLabel3, null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			//jTextField = new DateInputTextField();
			jTextField = new JRtaTextField("DATUM",false);
			//jTextField.setRtaType("DATUM",jTextField,false);
			jTextField.setBounds(new Rectangle(160, 35, 76, 20));
			jTextField.setText(this.tag);
			jTextField.setCaretPosition(0);
			jTextField.addKeyListener(new java.awt.event.KeyListener() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					//System.out.println("keyTyped()"); // TODO Auto-generated Event stub keyTyped()
					if(e.getKeyCode()==10){
						e.consume();
					}
				}
				public void keyPressed(java.awt.event.KeyEvent e) {
					//System.out.println("keypressed()"); // TODO Auto-generated Event stub keyTyped()
					TagEinstellen(e, jTextField.getText());// TODO Auto-generated Event stub keyTyped()
				}
				public void keyReleased(java.awt.event.KeyEvent e) {
					//System.out.println("keypreleased()e");
					if(e.getKeyCode()==10){
						e.consume();
					}
				}
			});		}
		return jTextField;
	}
	public void TagEinstellen(java.awt.event.KeyEvent e,String sTag){
		//datFunk dat = new datFunk();
		//System.out.println(e);		
		switch(e.getKeyCode()){
			case 33: //Bild auf
				//System.out.println("Bild auf");
				jTextField.setText(DatFunk.sDatPlusTage(sTag, +1));
				jLabel3.setText(DatFunk.WochenTag(jTextField.getText()));				
				jTextField.setCaretPosition(0);
				break;
			case 34: //Bild ab
				//System.out.println("Bild auf");
				jTextField.setText(DatFunk.sDatPlusTage(sTag, -1));
				jLabel3.setText(DatFunk.WochenTag(jTextField.getText()));
				jTextField.setCaretPosition(0);
				break;
			case 10: //Bild auf	
				e.consume();
				jDialog.dispose();
				
		}
	}
	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setBounds(new Rectangle(50, 80, 90, 28));
			jButton.setIcon(new ImageIcon(getClass().getResource("/icons/ok.gif")));
			jButton.setText("Ok");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					//System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
					SetzeButton(0);
					jDialog.dispose();
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
			jButton1.setBounds(new Rectangle(150, 80, 124, 28));
			jButton1.setIcon(new ImageIcon(getClass().getResource("/icons/nichtok.gif")));
			jButton1.setText("Abbruch");
			jButton1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SetzeButton(1);
					jDialog.dispose();
				}
			});
		}
		return jButton1;
	}

	private void SetzeButton(int result){
		this.ergebnis = result;
	}
}
