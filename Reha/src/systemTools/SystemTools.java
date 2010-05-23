package systemTools;

import java.awt.Point;

public class SystemTools {
	public static Point center(Point po,int x, int y){
	 return CenterFenster.center(po,x,y);	
	}
	
}

class CenterFenster {
	public static Point center(Point po,int x, int y){
		Point pneu = null;
		int xneu, yneu;
		xneu = po.x + (x/2);
		yneu = po.y + (y/2);
		pneu = new Point(xneu,yneu);
		////System.out.println("point="+po+" x="+x+" y="+y+" pneu="+pneu);
		return pneu;
	}
}
