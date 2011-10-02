package einzigesPaket;

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import sun.awt.image.ToolkitImage;



public class ShotBereich extends JPanel implements MouseListener,MouseMotionListener, KeyListener, ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static float faktorx;
	public static float faktory;	
	public static JLabel label = null;
	public boolean dragstart;
	public Point startpunkt;
	public Point stoppunkt;
	public Point altpunkt;
	public boolean shiftunten;
	public static boolean ausschnittok;
	public ShotBereich(){
		super();
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		label = new JLabel();
		add(label,BorderLayout.CENTER);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addKeyListener(this);
		
	}
	public void Zeichnen(){
		if (piTool.img != null){
			label.setText("");
			label.setIcon(new ImageIcon(piTool.img));   //  = new JLabel(new ImageIcon(piTool.img));
			
			
			float x = new Float( piTool.img.getWidth((ImageObserver) piTool.jFrame) );
			float y = new Float( piTool.img.getHeight((ImageObserver) piTool.jFrame) );

			faktory = y/x;
			faktorx = x/y;
			float orig = new Float( (100.000/140.000) );
			//float orig = (float) (140.000/100.000);
			//float result = new Float ( (orig+faktory) );
			//float ende = new Float ( (result*100.000) );

			float result = new Float ( (orig-faktory)/2 );
			faktory = faktory+result;
			
			System.out.println("Original Verhältnis y/x = "+faktory);
			System.out.println("Vorschau Verhältnis y/x = "+orig);

			
			
/*			
			ToolkitImage tk = ((ToolkitImage)piTool.img.getScaledInstance(140, 100, Image.SCALE_SMOOTH));
			System.out.println("tk (toolkit) = "+tk);
			piTool.vos = ((ToolkitImage)tk).getBufferedImage();
			System.out.println("Vos = "+piTool.vos);
			ImageIcon img = new ImageIcon();
			img.setImage(tk);
			piTool.vorschau.setIcon(img);
*/			
			
	         //Image scaled = piTool.img.getScaledInstance(140,  new Integer (new Float( result * 100.0000).toString()), Image.SCALE_SMOOTH );
			boolean zentrieren = false;
			float hoch;
			if((orig-faktory) >= 0.125f){
				hoch = new Float(100*faktory).intValue();
				zentrieren = true;
			}else{
				hoch = new Float(100);
			}
			System.out.println("Vorschaufaktor - Bildfaktor = "+(orig-faktory));
			 System.out.println("Höhe = "+hoch);
	         Image scaled = piTool.img.getScaledInstance(140,  (int) Math.abs(hoch), Image.SCALE_SMOOTH );
	         BufferedImage outImg = new BufferedImage(140, 100, BufferedImage.TYPE_INT_RGB);
	         Graphics g = outImg.getGraphics();
	         if(zentrieren){
	        	 float aby = 50-(hoch/2);
	        	 g.setColor( Color.WHITE);
	        	 g.fillRect(0, 0, 140, 100);
	        	 g.drawImage(scaled, 0, (int)Math.abs(aby), null);
	         }else{
	        	 g.drawImage(scaled, 0, 0, null);
	         }

	         g.dispose(); 
	         piTool.vos = outImg;
			piTool.vorschau.setIcon(new ImageIcon(piTool.vos));
	         
			
		}else{
			removeLabel();
			label.setIcon(null);
			label.setText("************ Clipboard leer oder ClipboardInhalt ist keine Grafik*************");
		}

	}
	public void markiereBereich(){

		Graphics g = this.getGraphics();
		Graphics2D g2d = (Graphics2D)g;	
        if(dragstart){
        	
          	 int x1,y1,w,h;
          	 x1 = startpunkt.x;
          	 y1 = startpunkt.y;
          	 w = stoppunkt.x-x1;
          	 h = stoppunkt.y-y1;
          	 /*
          	 if(altpunkt != null){
         		 g2d.setXORMode(Color.YELLOW);
          		 g2d.setColor( Color.YELLOW);
          		 g2d.drawRect(x1, y1, altpunkt.x, altpunkt.y);
          		 altpunkt.x = w;
          		 altpunkt.y = h;
          	 }else{
          		 altpunkt = new Point(stoppunkt);
          	 }
          	 */
     		 g2d.setXORMode(Color.YELLOW);
          	 g2d.setColor( Color.BLACK);
          	 g2d.drawRect(x1, y1, w, h);
           }
	}

	public void paintComponent( Graphics g ) { 
		super.paintComponent( g );
		Graphics2D g2d = (Graphics2D)g;	
		  if(dragstart && shiftunten){
			  	g2d.setStroke(new BasicStroke(2));

	          	 int x1,y1,w,h;
	          	 x1 = startpunkt.x;
	          	 y1 = startpunkt.y;
	          	 w = stoppunkt.x-x1;
	          	 h = stoppunkt.y-y1;
	          	 
	          	 if(altpunkt != null){
	         		 g2d.setXORMode(Color.WHITE);
	          		 g2d.setColor( Color.WHITE);
	          		 g2d.drawRect(x1, y1, altpunkt.x, altpunkt.y);
	          		 altpunkt.x = w;
	          		 altpunkt.y = h;
	          	 }else{
	          		 altpunkt = new Point(stoppunkt);
	          	 }
	          	 g2d.setColor( Color.RED);	          	
	     		 g2d.drawString(""+w+"x"+h, x1, y1-5);
	          	 g2d.drawRect(x1, y1, w, h);
	           }
		
	}
	
	public void removeLabel(){
		System.out.println("In Remove");
		this.remove(label);
		label = new JLabel();
		piTool.vorschau.setIcon(new ImageIcon());
		this.add(label);
		this.validate();

	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		//Cursor curs = new Cursor(Cursor.CROSSHAIR_CURSOR);
		piTool.jFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		requestFocus();
		
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		piTool.jFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		transferFocus();
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("Button Nr."+arg0.getButton());
		if(arg0.getButton() == MouseEvent.BUTTON1){
			dragstart = true;
			startpunkt = arg0.getPoint();
		}else if(arg0.getButton()==MouseEvent.BUTTON3){
			//if(ausschnittok){
				ZeigePopupMenu(arg0);
			//}
		}
	}
	private void ZeigePopupMenu(java.awt.event.MouseEvent me){
		JPopupMenu jPop = getTerminPopupMenu();
		jPop.show( me.getComponent(), me.getX(), me.getY() ); 
	}
	private JPopupMenu getTerminPopupMenu() {
		JPopupMenu jPopupMenu = new JPopupMenu();
		JMenuItem men1 = new JMenuItem("Bildausschnitt in Zwischenablage (Clipboard) übertragen");
		men1.setIcon(new ImageIcon("C:/RehaVerwaltung/icons/help.gif"));
		men1.setRolloverEnabled(true);
		men1.setEnabled(true);
		men1.setActionCommand("inclipboard");
		men1.addActionListener(this);
		jPopupMenu.add(men1);
		/*
		men1 = new JMenuItem("Bildausschnitt als neue Datei speichern");
		men1.setRolloverEnabled(true);
		men1.setEnabled(true);
		men1.setActionCommand("speichern");
		men1.addActionListener(this);
		jPopupMenu.add(men1);
		*/
		return jPopupMenu;
	}	
	
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		dragstart = false;
		stoppunkt = arg0.getPoint();
		if(shiftunten){
			if(piTool.img2 == null){
				return;
			}
			macheAusschnitt();
			ausschnittok = true;
			piTool.jsl.setValue(0);
			piTool.senden.setEnabled(true);
			piTool.app.titel.setText("");
		}else{
			//ausschnittok = false;
		}
		repaint();
	}
	public static void setzeAusschnitt(boolean setzen){
		ausschnittok = setzen;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		stoppunkt = arg0.getPoint();
		repaint();
		//markiereBereich();
	}
	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		stoppunkt = arg0.getPoint();		
	}
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.isShiftDown()){
			shiftunten = true;
		}
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		shiftunten = false;
		repaint();
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void macheAusschnitt(){
		repaint();
		int w = stoppunkt.x - startpunkt.x;
		int h = stoppunkt.y - startpunkt.y;
		if(w < 0 || h < 0){
			return;
		}
		
		int height = this.getHeight();
		int width = this.getWidth();
		BufferedImage img = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		this.paint(img.getGraphics());
		piTool.img = img;
		piTool.jFrame.repaint();

		BufferedImage img2 = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
		img2 = img.getSubimage(startpunkt.x,startpunkt.y, w, h);
		piTool.lblinfo.setText("  Bildinfo - Pixel: X="+img2.getWidth(null)+" Y="+img2.getHeight(null));

//		piTool.img = img2;
//		piTool.jFrame.repaint();
			
		label.setIcon(new ImageIcon(img2));
		piTool.original = new BufferedImage(img2.getWidth(null),img2.getHeight(null),BufferedImage.TYPE_INT_RGB);
    	Graphics g = piTool.original.getGraphics();
    	Graphics2D g2d = (Graphics2D) g;
    	g2d.drawImage(img2, 0, 0, null);
    	g2d.dispose();
    	piTool.xoriginal = img2.getWidth(null);
    	piTool.yoriginal = img2.getHeight(null);
		piTool.img = img2;
		piTool.img2 = img2;
		piTool.jFrame.repaint();
    	piTool.zoom = 1.000000;
		Zeichnen();
		
			
		/*
		Image imgx =  
		Image imgx = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		//g.drawImage(imgx, w, h, null);
		g.drawImage(imgx, 0, 0, w, h, null);
    	g.dispose();
    	label.setIcon(new ImageIcon(imgx));
    	
*/    	
		/*
		System.out.println("Größe des Ausschnitts = x:"+w+" / h:"+h );
		BufferedImage bufi = (BufferedImage) ShotBereich.label.getIcon();
        File file = new File("C:\\ScreenShots\\test.jpg");
        try {
			ImageIO.write((RenderedImage) bufi, "jpg", file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		*/

		/*
		Image img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        File file = new File("C:\\ScreenShots\\test.jpg");
        try {
			ImageIO.write((RenderedImage) img2, "jpg", file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	*/
		
		/*
		Image img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		g.drawImage(piTool.img, w, h, null);
    	g.dispose();
    	
    	label.setIcon(new ImageIcon(img));
    	
    	*/
    	/*
		try {
			label.setIcon(new ImageIcon(ImageIO.read(new File("C:\\ScreenShots\\test2.jpg"))));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	/*
    img = (Image)t.getTransferData(DataFlavor.imageFlavor);
    img2 = new BufferedImage(img.getWidth(jFrame), img.getHeight(jFrame),
    	      BufferedImage.TYPE_INT_RGB);//ARGB für Transparenz!
    	Graphics g = img2.getGraphics();
    	g.drawImage(img, 0, 0, null);
    	g.dispose();
    	
    orig =(Image) ((ToolkitImage)((BufferedImage)t.getTransferData(DataFlavor.imageFlavor)).getScaledInstance(140, 100, Image.SCALE_SMOOTH)); 
    senden.setEnabled(true);
	*/
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getActionCommand().equals("inclipboard")){
			System.out.println("Es wird in Clipboard übertragen");
			TransferableImage ti = new TransferableImage( (ImageIcon)label.getIcon() );
			Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
			cb.setContents(ti, ti);
		}
		if(arg0.getActionCommand().equals("speichern")){
			System.out.println("Es wird gespeichert");	
			   
		}
	}

}

/***********************************/
class TransferableImage implements Transferable, ClipboardOwner
{
   private BufferedImage bufImg;

   public TransferableImage(ImageIcon ic)
   {
      this( ic.getImage() );
   }

   public TransferableImage(Image img)
   {
      int w = img.getWidth(null); // es muß keinen ImageObserver geben
      int h = img.getHeight(null);

      bufImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
      // einfachster Fall : BufferedImage.TYPE_INT_RGB
      // mit drawImage das Image auf ein BufferedImage zeichnen
      Graphics g = bufImg.createGraphics();
      g.drawImage(img, 0, 0, null);
      g.dispose();
   }

   public TransferableImage(BufferedImage bImg)
   {
      bufImg = bImg;
   }

   //Returns an object which represents the data to be transferred.
   public Object getTransferData(DataFlavor flavor)
      throws UnsupportedFlavorException
   {
      if( flavor.equals(DataFlavor.imageFlavor) )
         return bufImg;

      throw new UnsupportedFlavorException(flavor);
   }

   //Returns an array of DataFlavor objects indicating the flavors
   //the data can be provided in.
   public DataFlavor[] getTransferDataFlavors()
   {
      return new DataFlavor[] {DataFlavor.imageFlavor} ;
   }

   //Returns whether or not the specified data flavor is supported
   //for this object.
   public boolean isDataFlavorSupported(DataFlavor flavor)
   {
      return flavor.equals(DataFlavor.imageFlavor) ;
   }

   // Implementierung des Interfaces ClipboardOwner
   public void lostOwnership(Clipboard clipboard, Transferable contents)
   {
   }

}

