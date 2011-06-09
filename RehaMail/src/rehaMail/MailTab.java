package rehaMail;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import com.jgoodies.looks.windows.WindowsTabbedPaneUI;

public class MailTab extends JXPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4803270370485221090L;
	JTabbedPane mailTab = null;
	MailPanel mailPanel = null;
	ReceivePanel receivePanel = null;
	public MailTab(){
		super(new BorderLayout());
		CompoundPainter<Object> cp = null;
		MattePainter mp = null;
		LinearGradientPaint p = null;
		/*****************/
		Point2D start = new Point2D.Float(0, 0);
		Point2D end = new Point2D.Float(960,100);		
		start = new Point2D.Float(0, 0);
		end = new Point2D.Float(0,400);
		float[] dist =null;
		Color[] colors = null;
	    dist = new float[] {0.0f, 0.75f};
	    colors = new Color[] {Color.WHITE,Tools.Colors.Green.alpha(0.45f)};
	    p = new LinearGradientPaint(start, end, dist, colors);
	    mp = new MattePainter(p);
	    cp = new CompoundPainter<Object>(mp);
	    this.setBackgroundPainter(cp);

		setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
		mailTab = new JTabbedPane();
		mailTab.setUI(new WindowsTabbedPaneUI());
		mailTab.setOpaque(false);
		
		mailPanel = new MailPanel();
		mailPanel.setOpaque(false);
		mailTab.add(mailPanel,"eingegangene Nachrichten");
		JXPanel pan = new JXPanel();
		pan.setOpaque(false);
		mailTab.add(pan,"gesendete Nachrichten");
		add(mailTab,BorderLayout.CENTER);
	}
	public MailPanel getMailPanel(){
		return mailPanel;
	}

}
