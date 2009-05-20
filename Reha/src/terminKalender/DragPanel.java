package terminKalender;

import hauptFenster.Reha;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import org.jdesktop.swingx.JXPanel;

import systemEinstellungen.SystemConfig;

public class DragPanel extends Thread implements Runnable{
	Point point = null;
	String name  = "";
	Point aktpoint = new Point(0,0);
	String reznummer = "";
	int dauer = 0;
	int breite = 0;
	float pixel = (float) 0.00;
	MyGlass myGlass = null;
	boolean aufhoeren = false;
	boolean insleep = true;
	
	public void dragPanelInit(String name,String reznummer,int dauer,int breite,float pixel){
		//super();
		myGlass = new MyGlass();
		myGlass.init(name,reznummer,dauer,breite,pixel);
		this.name = name;
		this.reznummer = reznummer;
		this.dauer = dauer;
		this.breite = breite;
		this.pixel = pixel;
		this.setPriority(Thread.MAX_PRIORITY);
		//setPreferredSize(new Dimension(1024,768));
		myGlass.setOpaque(false);
		myGlass.setVisible(true);
		Reha.thisFrame.setGlassPane(myGlass);
		Reha.thisFrame.getGlassPane().setVisible(true);
		Reha.thisFrame.getGlassPane().validate();
		start();
	}
	
/*
	protected void paintComponent(Graphics g) {
        	//System.out.println("in Paint Component");
            //g.setColor(Color.red);
        	Graphics2D g2d = (Graphics2D)g;
        	Composite original = g2d.getComposite();
			AlphaComposite ac1
              = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                           0.5f);
			g2d.setComposite(ac1);
            //g2d.setXORMode(Color.WHITE);
            g2d.drawRect(point.x-(this.breite/2), point.y-35, this.breite, (int) (this.pixel*this.dauer));
            g2d.fillRect(point.x-(this.breite/2), point.y-35, this.breite, (int) (this.pixel*this.dauer));
            g2d.setColor(Color.WHITE);
            g2d.setFont(g.getFont().deriveFont(9.5f));
            g2d.drawString(this.name
					, point.x-(this.breite/2), point.y-35+( ((int) (this.pixel*this.dauer))) );
    }
*/
    
    public void setPoint(Point p) {
    		myGlass.setPoint(p);
    		point = p;
    }

    public void setBeenden(boolean sollstoppen) {
        aufhoeren = sollstoppen;
    }

    @Override
	public void run() {
    	System.out.println("Drag gestartet");
		while(!aufhoeren){
			if (point != null && aktpoint != point){
				insleep = false;
				aktpoint = point;
				//Reha.thisFrame.getGlassPane().repaint();
				myGlass.repaint();
				
				//System.out.println("nach zeichnen");
			}
			//interrupt();
			//yield();
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		    
		}
    	System.out.println("Drag beendet");
		Reha.thisFrame.getGlassPane().setVisible(false);
		myGlass.setVisible(false);
		myGlass = null;
	}


}

class MyGlass extends JXPanel{
	Point point = null;
	String name  = "";
	Point aktpoint = new Point(0,0);
	String reznummer = "";
	int dauer = 0;
	int breite = 0;
	float pixel = (float) 0.00;
	
	int startx,starty,endy,lang,breit;

	MyGlass(){
		super();
		//setDoubleBuffered(true);
		
	}
	public void init(String name,String reznummer,int dauer,int breite,float pixel){
		this.name = name;
		this.reznummer = reznummer;
		this.dauer = dauer;
		this.breite = breite;
		this.pixel = pixel;
		
		
	}
	
    public void setPoint(Point p) {
        point = p;
        startx = point.x-(this.breite/2);
        starty = point.y-27;
        endy = (int) (this.pixel*this.dauer);
        //System.out.println("Drag-Point = "+p);
        //myGlass.repaint();
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
	
    protected void paintComponent(Graphics g) {
    	//System.out.println("in Paint Component");
        //g.setColor(Color.red);
    	Graphics2D g2d = (Graphics2D)g;
    	Composite original = g2d.getComposite();
		AlphaComposite ac1
          = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                       0.5f);
		g2d.setComposite(ac1);
        //g2d.setXORMode(Color.WHITE);
		//g2d.drawRect(startx, starty, this.breite, endy);
        g2d.fillRect(startx, starty, this.breite, endy);
		//g2d.drawRect(point.x-(this.breite/2), point.y-35, this.breite, (int) (this.pixel*this.dauer));
        //g2d.fillRect(point.x-(this.breite/2), point.y-35, this.breite, (int) (this.pixel*this.dauer));
        g2d.setComposite(original);
        g2d.setColor(Color.WHITE);
        g2d.setFont(g.getFont().deriveFont(9.5f));
        g2d.drawString(/*yEndeMin-yStartMin+"s2 "+*/this.name
				, point.x-(this.breite/2), (point.y-25)+( ((int) (this.pixel*this.dauer))) -  ((int) (this.pixel*this.dauer/2)));
}
	
	
}
