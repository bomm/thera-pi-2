package splashWin;

import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jdesktop.swingx.JXPanel;



public class SplashInhalt extends JXPanel{
BufferedImage img1 = null;
	SplashInhalt(){
		super(new GridLayout(1,1));
		setBorder(null);
		try {
			String file = RehaxSwing.proghome+"icons/therapieMT1.gif";
			//String file = "C:\\RehaVerwaltung\\RehaxSwing\\src\\ressources\\earthSwing.gif";
			File grafik = new File(file);
			img1 = ImageIO.read( grafik);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		repaint();
		
	}

	public void paintComponent( Graphics g ) {
		g.drawImage(this.img1,0, 0, this);
	}
}
