package systemEinstellungen;
import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


public class SysUtilAnschluesse extends JXPanel implements KeyListener, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JComboBox anschluss = null;
	JComboBox baud = null;
	JComboBox datenbits = null;
	JComboBox parity = null;
	JComboBox stopbits = null;
	JButton knopf1 = null;
	JButton knopf2 = null;

	public SysUtilAnschluesse(){
	
	
	
		super(new BorderLayout());
		//System.out.println("Aufruf SysUtilAnschluesse");
		this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 20));
		/****/
		//System.out.println("Aufruf SysUtilGeraete");
		this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 20));
		/****/
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("SystemInit"));
	     add(getVorlagenSeite(),BorderLayout.CENTER);
		/****/
		return;
	}
	/************** Beginn der Methode f�r die Objekterstellung und -platzierung *********/
	private JPanel getVorlagenSeite(){
	
		knopf2 = new JButton("abbrechen");
		knopf2.setPreferredSize(new Dimension(70, 20));
		knopf2.addActionListener(this);
		knopf2.setActionCommand("abbruch");
		knopf2.addKeyListener(this);
		knopf1 = new JButton("speichern");
		knopf1.setPreferredSize(new Dimension(70, 20));
		knopf1.addActionListener(this);
		knopf1.setActionCommand("speichern");
		knopf1.addKeyListener(this);
		
		anschluss = new JComboBox(new String[] {"COM 1", "COM 2", "COM 3", "COM 4", "COM 5", "COM 6", "COM 7", "COM 8", "COM 9", "COM 10"});
		anschluss.setSelectedIndex(0);
		anschluss.setActionCommand("anschluss");
		anschluss.addActionListener(this);
		baud = new JComboBox(new String[] {"2400", "4800", "9600", "14400","19200", "38400", "57600", "115200" });
		baud.setSelectedItem(SystemConfig.hmGeraete.get("COM1")[0]);
		datenbits = new JComboBox(new String[] {"4", "5", "6", "7", "8" });
		datenbits.setSelectedItem(SystemConfig.hmGeraete.get("COM1")[1]);
		parity = new JComboBox(new String[] {"EVEN", "ODD", "NONE"});
		parity.setSelectedItem(SystemConfig.hmGeraete.get("COM1")[2]);
		stopbits = new JComboBox(new String[] {"0", "1", "2" });
		stopbits.setSelectedItem(SystemConfig.hmGeraete.get("COM1")[3]);
		
	
        //                                      1.            2.    3.    4.     5.     6.    7.      8.     9.

		FormLayout lay = new FormLayout("100dlu, right:max(120dlu;p), 10dlu, 50dlu",

	   //1.    2.   3.   4.  5.   6. 7.  8.  9.   10.  11.  12.  13. 14.  15. 16.  17. 18.  19. 20. 21.  22.  23.  24.  25   26  27  28   29  30   31   32  33    34  35  36     37
		"p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p");
		
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator("Anschlussdaten", cc.xyw(1, 1, 4));
		builder.addLabel("Anschluss auswählen", cc.xy(2,3));
		builder.add(anschluss, cc.xy(4,3));
		builder.addLabel("Baudrate", cc.xy(2,5));
		builder.add(baud, cc.xy(4,5));
		builder.addLabel("Datenbits", cc.xy(2,7));
		builder.add(datenbits, cc.xy(4,7));
		builder.addLabel("Parity", cc.xy(2,9));
		builder.add(parity, cc.xy(4,9));
		builder.addLabel("Stopbits", cc.xy(2,11));
		builder.add(stopbits, cc.xy(4,11));
		
		builder.addSeparator("Änderungen übernehmen", cc.xyw(1,13,4));
		builder.add(knopf2, cc.xy(2,15));
		builder.add(knopf1, cc.xy(4,15));
		
		return builder.getPanel();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String cmd = e.getActionCommand();
		if(cmd.equals("anschluss")){
			doConfig("COM"+Integer.valueOf(anschluss.getSelectedIndex()+1).toString());
		}
		if(cmd.equals("speichern")){
			doSpeichern("COM"+Integer.valueOf(anschluss.getSelectedIndex()+1).toString());
		}
		if(cmd.equals("abbruch")){
			SystemInit.abbrechen();
			//SystemUtil.thisClass.parameterScroll.requestFocus();			
		}
	}
	private void doConfig(String com){
		baud.setSelectedItem(SystemConfig.hmGeraete.get(com)[0]);
		datenbits.setSelectedItem(SystemConfig.hmGeraete.get(com)[1]);
		parity.setSelectedItem(SystemConfig.hmGeraete.get(com)[2]);
		stopbits.setSelectedItem(SystemConfig.hmGeraete.get(com)[3]);
	}
	private void doSpeichern(String com){
		String[] params = new String[4];
		params[0] = (String) baud.getSelectedItem();
		params[1] = (String) datenbits.getSelectedItem();
		params[2] = (String) parity.getSelectedItem();
		params[3] = (String) stopbits.getSelectedItem();

		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/geraete.ini");
		inif.setStringProperty(com, "BaudRate",params[0] , null);
		inif.setStringProperty(com, "Bits",params[1] , null);
		inif.setStringProperty(com, "Parity",params[2] , null);		
		inif.setStringProperty(com, "StopBit",params[3] , null);
		inif.save();
		SystemConfig.hmGeraete.put(com,params.clone());
		JOptionPane.showMessageDialog(null, "Anschlußkonfiguration wurde gespeichert und steht nach dem nächsten\nStart der Software zur Verfügung");
	}

}
