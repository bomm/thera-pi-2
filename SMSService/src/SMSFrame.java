import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


public class SMSFrame extends JFrame{
	
	private static final long serialVersionUID = 1L;
	private SMSService sms;
	private JTextField number, text;
	private JButton btn, btnFile;
	
	SMSFrame(SMSService sms) {
		super();
		this.sms = sms;
		this.setSize(400, 120);
		this.setTitle("SMS-Sender");
		this.addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				SMSService.close();
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowOpened(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		this.setLayout(new BorderLayout());
		this.add(getContent());
		this.setVisible(true);
	}
	
	private JPanel getContent() {
		JPanel pane = new JPanel();
		String xwerte = "5dlu, 50dlu, 2dlu, 20dlu:g, 30dlu, 5dlu";
		String ywerte = "5dlu, p, 2dlu, p, 2dlu, p, 5dlu";
		FormLayout lay = new FormLayout(xwerte, ywerte);
		pane.setLayout(lay);
		CellConstraints cc = new CellConstraints();
		
		JLabel label = new JLabel("Empfänger:");
		pane.add(label, cc.xy(2, 2));
		
		number = new JTextField();
		pane.add(number, cc.xy(4, 2));
		
		btnFile = new JButton("File");
		btnFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				chooseFile();
			}
			
		});
		pane.add(btnFile, cc.xy(5, 2));
		
		label = new JLabel("Text:");
		pane.add(label, cc.xy(2, 4));
				
		text = new JTextField();
		pane.add(text, cc.xyw(4, 4, 2));
		
		btn = new JButton("Feuer!");
		pane.add(btn, cc.xyw(2, 6, 4));
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				number.setEditable(false);
				text.setEditable(false);
				String[] numbers = number.getText().split(";");
				for(int i = 0; i < numbers.length; i++) {
					sms.sendSMS(numbers[i], text.getText());
				}
				number.setEditable(true);
				text.setEditable(true);
			}
			
		});
		
		return pane;
	}
	
	private void chooseFile() {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.showOpenDialog(null);
		File f = fc.getSelectedFile();
		try {
			BufferedReader in = new BufferedReader(new FileReader(f));
			String numbers = "";
			String zeile = null;
			while((zeile = in.readLine()) != null) {
				numbers += zeile;
			}
			number.setText(numbers);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
