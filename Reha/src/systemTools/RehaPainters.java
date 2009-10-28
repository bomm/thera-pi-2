package systemTools;




import org.jdesktop.swingx.painter.Painter;





import grad.GradientPainter;
import grad.GradientSegment;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.LinearGradientPaint;
//import java.awt.Paint;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.geom.Point2D;



//import org.apache.batik.ext.awt.LinearGradientPaint;
import org.jdesktop.swingx.painter.*;

public final class RehaPainters {

	public static CompoundPainter getBlauPainter() {

		 // set the background painter

		 MattePainter mp = new MattePainter(Colors.TaskPaneBlau.alpha(1.0f));

		 GlossPainter gp = new GlossPainter(Colors.White.alpha(0.3f),

		 GlossPainter.GlossPosition.TOP);

		 //PinstripePainter pp = new PinstripePainter(Colors.White.alpha(0.2f),45d);
		 //PinstripePainter pp = new PinstripePainter(Colors.Blue.alpha(0.2f),45d);

		 return new CompoundPainter(mp, gp);

	 }
	 
	public static CompoundPainter getBlauGradientPainter() {
		/*
		BasicGradientPainter gradient = new BasicGradientPainter(
			    new GradientPaint(new Point2D.Double(0.0, 0.0), new Color(0xd67801),
			    new Point2D.Double(0.0, 1.0), new Color(0xb35b01)));
	*/
		 MattePainter mp = new MattePainter(Colors.TaskPaneBlau.alpha(1.0f));

		 GlossPainter gp = new GlossPainter(Colors.White.alpha(0.3f),

		 GlossPainter.GlossPosition.BOTTOM);
		 //return p;
		  return new CompoundPainter(mp,gp);

	 }
	public static Painter getGradientGradPainter() {
	  GradientPainter gp1 = new GradientPainter();
	  gp1.addSegment(new GradientSegment(new Color(0,51,255), new Color(128,153,255), 0.0,0.28045976,false));
	  gp1.addSegment(new GradientSegment(new Color(128,153,255), new Color(255,255,255), 0.28045976,1.0,false));
	  return (Painter) gp1;
	} 
	  

	public static CompoundPainter getSchwarzGradientPainter() {

		 // set the background painter
		LinearGradientPaint lgp = new LinearGradientPaint(new Point(0,0),new Point(1,0),new float[] {0.0f,1.0f},new Color[] {Colors.TaskPaneBlau.alpha(1.0f),new Color(255,255,255)});
		
		
		 //MattePainter mp = new MattePainter(Colors.TaskPaneBlau.alpha(1.0f));
		 
		 MattePainter mp = new MattePainter(lgp);
		 //GlossPainter gp = new GlossPainter(Colors.White.alpha(0.3f),
				 

		 //GlossPainter.GlossPosition.TOP);

		 //PinstripePainter pp = new PinstripePainter(Colors.White.alpha(0.2f),45d);
		 //PinstripePainter pp = new PinstripePainter(Colors.Blue.alpha(0.2f),45d);
		 CompoundPainter cp = new CompoundPainter();
		 cp.setPainters(mp);
		 
		 return cp; //new CompoundPainter(gp);

	 }

	

	Painter getPaint() {
	        Color start = SystemColor.activeCaption;
	        Color end = new Color(Math.min(start.getRed() + 30, 255),
	                Math.min(start.getGreen() + 30, 255),
	                Math.min(start.getBlue() + 30, 255), 100);
	        return (Painter) new GradientPaint(100, 0, start, 150, 0, end);
	    }	 
}
