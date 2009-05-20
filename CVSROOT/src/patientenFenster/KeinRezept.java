package patientenFenster;

import hauptFenster.Reha;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.ImageIcon;

import org.jdesktop.swingx.JXPanel;

public class KeinRezept extends JXPanel{
ImageIcon hgicon = null;
int icx;
int icy;
	public KeinRezept(){
		super();
		hgicon = new ImageIcon(Reha.proghome+"icons/KeineRezepte.png");
		icx = hgicon.getIconWidth()/2;
		icy = hgicon.getIconHeight()/2;

		setOpaque(false);
		setBorder(null);
	}
	@Override
	public void paintComponent( Graphics g ) { 
		super.paintComponent( g );
		Graphics2D g2d = (Graphics2D)g;
		
		if(hgicon != null){

			g2d.drawImage(hgicon.getImage(), (getWidth()/2)-icx , (getHeight()/2)-icy,null);
			g2d.drawString("Keine Rezepte angelegt für diesen Patient", 50, 50);

		}
	}

}
