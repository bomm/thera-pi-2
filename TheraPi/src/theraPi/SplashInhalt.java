package theraPi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import org.jdesktop.swingx.JXPanel;



public class SplashInhalt extends JXPanel implements ActionListener{
//BufferedImage img1 = null;
	SplashInhalt(){
		super(new BorderLayout());
		
		setBorder(null);
		add(getLabel(),BorderLayout.NORTH);
		add(getMands());
		
	}
	private JXPanel getMands(){
		JXPanel jpan = new JXPanel();
		jpan.setBorder(null);
		GridLayout grid = new GridLayout(TheraPi.AnzahlMandanten,1);
		
		jpan.setLayout(grid);
		JButton jbut;

		for(int i = 0; i < TheraPi.mandvec.size();i++){
			String btext = new String(TheraPi.mandvec.get(i)[1]+" - IK:"+TheraPi.mandvec.get(i)[0]);
			jbut = new JButton(btext);
			jbut.setActionCommand(new Integer(i).toString());
			jbut.addActionListener(this);
			jpan.add(jbut);
		}
		return jpan;
	}

	private JXPanel getLabel(){
		JXPanel jpan = new JXPanel(new BorderLayout());
		jpan.setBorder(null);
		jpan.setPreferredSize(new Dimension(0,55));
		jpan.setBackground(Color.WHITE);
		JLabel lbl = new JLabel("",JLabel.CENTER);
		lbl.setIcon(new ImageIcon(TheraPi.proghome+ "icons/TPorg.png"));
		jpan.add(lbl,BorderLayout.CENTER);
		return jpan;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		int action = new Integer(arg0.getActionCommand());
		String s1 = TheraPi.mandvec.get(action)[0];
		String s2 = TheraPi.mandvec.get(action)[1];		
		TheraPi.StartMandant = s1+'@'+s2;
		RehaStarter rst = new RehaStarter();
		rst.execute();
		try {
			int i = rst.get();
			if(i==1){

				Thread.sleep(10000);
				System.out.println("Rückgabewert = 1");						
				System.exit(0);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
