package terminKalender;

import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import systemEinstellungen.SystemConfig;


public class FinalGlassPane extends JPanel implements AWTEventListener { 
    /**
	 * 
	 */
	private static final long serialVersionUID = -532516864328575805L;
	private final JInternalFrame frame; 
    private Point point = new Point();
    private float fPixelZuMinute = 0.f;
    private String maxhoch = "0";
    private String uhrZeit = "00:00";
    Font font = null;
    public FinalGlassPane(JInternalFrame frame) { 
        super(); 
        this.frame = frame;
        add(new JLabel("label"));
        
        setOpaque(false); 
    } 
 
    public void setPoint(Point point) { 
        this.point = point; 
    } 
 
    protected void paintComponent(Graphics g) { 
        Graphics2D g2 = (Graphics2D) g; 
        //g2.setColor(Color.GREEN.darker()); 
        g2.setColor(Color.BLACK.darker());
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f)); 
        int d = 22; 
        int w = 53;
        //g2.fillRect(getWidth() - d, 0, d, d); 
        if (point != null) { 
            //g2.fillOval(point.x + d, point.y + d, d, d); 
            g2.fillRect(point.x + d-5, point.y-2, w, d);
            g2.setColor(Color.YELLOW);
            g2.drawString(uhrZeit, point.x-5 + d+2, point.y+13);
        } 
        g2.dispose(); 
    } 

    public void eventDispatched(AWTEvent event) {
    	//System.out.println(event.getClass());
        if (event instanceof MouseEvent) { 
            MouseEvent me = (MouseEvent) event; 
           
            if (!SwingUtilities.isDescendingFrom(me.getComponent(), frame)) {
            	 System.out.println("! Disending");
                return; 
            } 
            if (me.getID() == MouseEvent.MOUSE_EXITED && me.getComponent() == frame) { 
                point = null; 
            } else { 
                MouseEvent converted = SwingUtilities.convertMouseEvent(me.getComponent(), me, frame.getGlassPane()); 
                point = converted.getPoint();
                maxhoch = Integer.toString(me.getComponent().getHeight());
                fPixelZuMinute = Float.valueOf(900.f/Float.valueOf(maxhoch));
                uhrZeit = ZeitFunk.MinutenZuZeit(Long.valueOf(ZeitFunk.MinutenSeitMitternacht(SystemConfig.KalenderUmfang[0])).intValue()+
                		Math.round(Float.valueOf(fPixelZuMinute* (me.getY()) )));
                
                /*
                System.out.println("Minuten bis 07:00 = "+ZeitFunk.MinutenSeitMitternacht(SystemConfig.KalenderUmfang[0]));
                System.out.println("Höhe in Pixel = "+maxhoch);
                System.out.println(me.getComponent().getName());
                System.out.println(fPixelZuMinute);
                System.out.println("Minuten = "+Math.round(Float.valueOf(fPixelZuMinute*Float.valueOf(me.getY()))));
                System.out.println("Position = "+(me.getY()));
                System.out.println(uhrZeit);
                */
                //System.out.println( ((JTerminInternal)me.getComponent().getParent().getParent()).getContentPane());
                //me.getComponent().dispatchEvent(event);
                
            }
            
            repaint(); 
        }
        
        //Reha.thisClass.terminpanel.getViewPanel().dispatchEvent(event);
        //Reha.thisClass.terminpanel.getTerminFlaecheFromOutside().dispatchEvent(event);

    } 
 
    /** 
     * If someone adds a mouseListener to the GlassPane or set a new cursor 
     * we expect that he knows what he is doing 
     * and return the super.contains(x, y) 
     * otherwise we return false to respect the cursors 
     * for the underneath components 
     */ 

    public boolean contains(int x, int y) { 
        if (getMouseListeners().length == 0 && getMouseMotionListeners().length == 0 
                && getMouseWheelListeners().length == 0 
                && getCursor() == Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)) { 
            return false; 
        } 
        return super.contains(x, y); 
    }

	
	
} 
 

