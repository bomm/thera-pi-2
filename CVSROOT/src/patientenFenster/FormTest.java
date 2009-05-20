package patientenFenster;

import java.awt.Rectangle;
import java.text.ParseException;

import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import systemTools.JRtaTextField;

public class FormTest extends JInternalFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JButton jButton = null;
	private JRtaTextField jFeld = null;
	private JRtaTextField jFeld2 = null;
	/**
	 * This is the xxx default constructor
	 * @throws ParseException 
	 */
	public FormTest() throws ParseException {
		super();
		initialize();
		this.setVisible(true);
		this.jFeld.requestFocus();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 * @throws ParseException 
	 */
	private void initialize() throws ParseException {
		this.setSize(498, 379);
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 * @throws ParseException 
	 */
	private JPanel getJContentPane() throws ParseException {
		if (jContentPane == null) {
			jLabel1 = new JLabel();
			jLabel1.setBounds(new Rectangle(102, 79, 117, 16));
			jLabel1.setText("Nur Zahlen");
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(24, 35, 140, 16));
			jLabel.setText("Nur GroßBuchstaben");
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(jLabel, null);
			jContentPane.add(jLabel1, null);
			jContentPane.add(getJButton(), null);
			jFeld = new JRtaTextField("GROSS",false);
			//jFeld.setRtaType("GROSS",jFeld,false);
			//jld.setSelectionEnd(5);
			jFeld.setBounds(new Rectangle(95, 149, 117, 25));
			jFeld.setHorizontalAlignment(JTextField.RIGHT);
			jContentPane.add(jFeld,null);			
			jFeld2 = new JRtaTextField("ZAHLEN",true);
			//jFeld2.setRtaType("ZAHLEN",jFeld2,true);
			jFeld2.setBounds(new Rectangle(271, 78, 117, 25));			
			/*
			jFeld2.addFocusListener(new java.awt.event.FocusAdapter() {   
				public void focusLost(java.awt.event.FocusEvent e) {    
					//System.out.println("focusLost()"); // TODO Auto-generated Event stub focusLost()
					e.getComponent().setBackground(Color.WHITE);
					//System.out.println("Focus verloren"+e);
				}
				public void focusGained(java.awt.event.FocusEvent e) {
					//System.out.println("focusGained()"); // TODO Auto-generated Event stub focusGained()
					e.getComponent().setBackground(Color.YELLOW);
					((JTextComponent) e.getComponent()).setCaretPosition(0);
					((JTextComponent) e.getComponent()).setSelectionStart(0);
					((JTextComponent) e.getComponent()).setSelectionEnd(((JTextComponent) e.getComponent()).getText().length() );	
				}
			});
			*/
			jContentPane.add(jFeld2,null);
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
			jButton.setBounds(new Rectangle(198, 267, 110, 31));
			jButton.setText("Inhalt der Edits");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jFeld2.requestFocus();
										
				}
			});
		}
		return jButton;
	}

}  //  @jve:decl-index=0:visual-constraint="525,20"

class PassVerifier extends InputVerifier {
	@Override
	public boolean verify(JComponent input) {
    	JFormattedTextField tf = (JFormattedTextField) input;
    	System.out.println("verifiziert: "+tf.getText());
    	//tf.setText(tf.getText().toUpperCase());
    	return "pass".equals(tf.getText());
    }

	public boolean shouldYieldFocus(javax.swing.JComponent input) {
        if (!verify(input)) {
            //Textfeld Vordergrund rot färben
            input.setForeground(java.awt.Color.RED);
        	System.out.println("test-rot");
            return false;
        }
        else {
            input.setForeground(java.awt.Color.GREEN);
        	System.out.println("test-grün");
            return true;
        }
    }
}
