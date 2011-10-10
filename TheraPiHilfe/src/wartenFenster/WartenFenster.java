package wartenFenster;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

public class WartenFenster extends JDialog{
	public static JXLabel stand = null;
	
	public WartenFenster(){
		super();
		this.setUndecorated(true);
		this.setBackground(Color.WHITE);
		this.setModal(false);
		this.setContentPane(new JXPanel(new BorderLayout()));
		this.getContentPane().setBackground(Color.RED);
		((JComponent) this.getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		JXLabel warten = new JXLabel("Bitte warten........");
		warten.setForeground(Color.WHITE);
		warten.setFont(new Font("Verdana", Font.BOLD, 14));
		this.getContentPane().add(warten,BorderLayout.NORTH);
		stand = new JXLabel("");
		stand.setFont(new Font("Verdana", Font.BOLD, 12));

		stand.setForeground(Color.WHITE);
		this.getContentPane().add(stand,BorderLayout.CENTER);
		this.setSize(500,100);
		this.getContentPane().validate();
		//this.setVisible(true);
		
	}
	public static void setStand(String sstand){
		stand.setText(sstand);
	}

}
