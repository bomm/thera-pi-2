package patientenFenster;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.ImageIcon;

import org.jdesktop.swingx.JXPanel;

import systemEinstellungen.SystemConfig;

public class KeinRezept extends JXPanel{
/**
	 * 
	 */
	private static final long serialVersionUID = -851354557761331573L;
	ImageIcon hgicon = null;
	int icx;
	int icy;
	String itext = null;
	public KeinRezept(String txt){
		super();
		setLayout(new BorderLayout());
		hgicon = SystemConfig.hmSysIcons.get("keinerezepte");
		icx = hgicon.getIconWidth()/2;
		icy = hgicon.getIconHeight()/2;
		itext = txt;
		setOpaque(false);
		setBorder(null);
	}
	@Override
	public void paintComponent( Graphics g ) { 
		super.paintComponent( g );
		Graphics2D g2d = (Graphics2D)g;
		if(hgicon != null){
			g2d.drawImage(hgicon.getImage(), (getWidth()/2)-icx , (getHeight()/2)-icy,null);
			g2d.drawString(itext, 50, 50);

		}
	}

}
