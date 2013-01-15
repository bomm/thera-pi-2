package patientenFenster;

import hauptFenster.Reha;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.Buffer;
import javax.media.Manager;
import javax.media.control.FrameGrabbingControl;
import javax.media.format.VideoFormat;
import javax.media.util.BufferToImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;

import CommonTools.ButtonTools;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import systemEinstellungen.SystemConfig;
import systemTools.ListenerTools;



import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;

public class PatientenFoto  extends RehaSmartDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2540300265092788472L;
	private RehaTPEventClass rtp = null;
	Component content = null;
	public JXPanel grundPanel = null;
	boolean mouseIsIn;
	JRootPane rootpane;
	boolean bigvideo = false;
	PhotoFrame panel;
	
	FrameGrabbingControl frameGrabber = null;
	
	public JSlider jsl = null;
	static final int FPS_MIN = -40;
	static final int FPS_MAX = +40;
	static final int FPS_INIT = 0;  
	
	ActionListener al = null;
	ChangeListener cl = null;
	JButton[] buts = {null,null};
	int[] defaultsize = {0,0};
	
	PatNeuanlage eltern = null;
	public PatientenFoto(JXFrame owner, String name,PatNeuanlage xeltern) {
		super(owner, name);
		super.getSmartTitledPanel().setTitleForeground(Color.WHITE);
	    super.getSmartTitledPanel().setTitle("Patientenfoto erzeugen");
	    eltern = xeltern;
	    setName(name);
	    initListener();
		Reha.thisClass.player.start();
	    bigvideo = (SystemConfig.sWebCamSize[0]==640 ? true : false);

	    defaultsize[0] = (bigvideo ? 288 : 144);
    	defaultsize[1] = (bigvideo ? 360 : 180);
	   
	    pinPanel = new PinPanel();
	    pinPanel.setName(name);
	    pinPanel.getGruen().setVisible(false);
	    setPinPanel(pinPanel);
	    rtp = new RehaTPEventClass();
	    rtp.addRehaTPEventListener((RehaTPEventListener) this);
		grundPanel = new JXPanel(new BorderLayout());

		panel = new PhotoFrame();

		panel.setLayout(new BorderLayout());
		panel.add(getVideoPanel(),BorderLayout.CENTER);
		panel.setPreferredSize(new Dimension(SystemConfig.sWebCamSize[0],SystemConfig.sWebCamSize[1]));
		grundPanel.add(panel,BorderLayout.CENTER);
		getSmartTitledPanel().setContentContainer(grundPanel);
		
		grundPanel.add(this.actionPanel(),BorderLayout.SOUTH);
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				panel.setPhotoFrameSize(defaultsize[0],defaultsize[1]);
				panel.setCenter();
				frameGrabber  = (FrameGrabbingControl)Reha.thisClass.player.getControl("javax.media.control.FrameGrabbingControl");
			}
		});
	}
	private void initListener(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				String cmd = e.getActionCommand();
				if(cmd.equals("schuss")){
					doGrabbing();
					return;
				}
				if(cmd.equals("abbrechen")){
					doAbbrechen();
					return;
				}
			}
		};
		cl = new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				doScale();
			}
			
		};
	}
	private void doGrabbing(){
		Buffer buf = frameGrabber.grabFrame();	
	    Image img = (new BufferToImage((VideoFormat)buf.getFormat()).createImage(buf));	
	    
	    if(! (img==null)){
	    	Image imgr = null;
			int faktor = (bigvideo ? 1 : 2);
			int teiler = 1;

	    	BufferedImage feddisch = null;
		    BufferedImage img2 = null;
		    BufferedImage buffImg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);

		    Graphics2D g = buffImg.createGraphics();
		    g.drawImage(img, null, null);
			g.dispose();

			try{
				img2 = new BufferedImage(panel.getPhotoFrameSize().width,panel.getPhotoFrameSize().height,BufferedImage.TYPE_INT_RGB);
				if( (panel.getActivePoint().x*faktor) + (panel.getPhotoFrameSize().width*faktor) > img.getWidth(null) ||
						(panel.getActivePoint().y*faktor) + (panel.getPhotoFrameSize().height*faktor) > img.getHeight(null) ){
					teiler = 2;
				}
				img2 = buffImg.getSubimage(panel.getActivePoint().x*faktor/teiler,
						panel.getActivePoint().y*faktor/teiler,
						panel.getPhotoFrameSize().width*faktor/teiler,
						panel.getPhotoFrameSize().height*faktor/teiler);
		    	imgr =   img2.getScaledInstance(175, 220, Image.SCALE_SMOOTH);
		    	feddisch = new BufferedImage(175, 220, BufferedImage.TYPE_INT_RGB);
		    	g = feddisch.createGraphics();
	    	    g.drawImage(imgr, 0, 0, null);
	    	    g.dispose();
	    	    eltern.setNewPic(new ImageIcon(feddisch));
			}catch(Exception ex){
				System.out.println("****Fehler in der Funktion Patientenfoto");
				System.out.println("Größe des Fotoframes in X-Richtung = "+panel.getPhotoFrameSize().width);
				System.out.println("Größe des Fotoframes in Y-Richtung = "+panel.getPhotoFrameSize().height);
				System.out.println("Größe des Original-Videoframes in X-Richtung = "+img.getWidth(null));
				System.out.println("Größe des Original-Videoframes in Y-Richtung = "+img.getHeight(null));
				System.out.println("X-Position des aktiven Punktes  = "+panel.getActivePoint().x);
				System.out.println("Y-Position des aktiven Punktes  = "+panel.getActivePoint().y);
				System.out.println("Bigvideo-Parameter = "+bigvideo);
				System.out.println("X-Subimage = "+panel.getActivePoint().x*faktor);
				System.out.println("Y-Subimage = "+panel.getActivePoint().y*faktor);
				System.out.println("W-Subimage = "+panel.getPhotoFrameSize().width*faktor);
				System.out.println("H-Subimage = "+panel.getPhotoFrameSize().height*faktor);
				ex.printStackTrace();
			}
    	    /*
			try {
				ImageIO.write( feddisch , "png", new File("c:\\webcam2.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			*/
			buf = null;
			buffImg = null;
			img2 = null;
			imgr = null;
			feddisch = null;
	    }
	    doAbbrechen();
	    
	}
	private void doScale(){
		if(jsl.getValue() != 0){
			float fval = (jsl.getValue()*(bigvideo ? 2 : 1))+(defaultsize[0]);
			panel.setPhotoFrameSize(Double.valueOf(Math.abs(fval)).intValue(), Double.valueOf(Math.abs(fval*1.25)).intValue());
			panel.repaint();
		}else{
			panel.setPhotoFrameSize(defaultsize[0],defaultsize[1]);
		}
	}
	private void doAufraeumen(){
		if(al != null){
			buts[0].removeActionListener(al);
			buts[1].removeActionListener(al);
			al = null;
		}
		if(cl != null){
			jsl.removeChangeListener(cl);
			cl = null;
		}
		Runtime r = Runtime.getRuntime();
		r.gc();
	}
	public void rehaTPEventOccurred(RehaTPEvent evt) {
		try{
			if(evt.getDetails()[0] != null){
				if(evt.getDetails()[0].equals(this.getName())){
					this.setVisible(false);
					if(rtp != null){
						rtp.removeRehaTPEventListener((RehaTPEventListener) this);
						rtp = null;
						pinPanel = null;
						Reha.thisClass.player.stop();
						Reha.thisClass.player.deallocate();
						this.dispose();
						super.dispose();
					}
				}
			}
			doAufraeumen();
		}catch(NullPointerException ne){
		}
	}	
	public void doAbbrechen(){
		this.setVisible(false);
		if(rtp != null){
			rtp.removeRehaTPEventListener((RehaTPEventListener) this);
			rtp = null;
			pinPanel = null;
			Reha.thisClass.player.stop();
			Reha.thisClass.player.deallocate();
		}
		doAufraeumen();
		dispose();
		super.dispose();
	}
	
	public Component getVideoPanel(){
		//Reha.thisClass.player.start();
		return Reha.thisClass.player.getVisualComponent();
	}
	public JXPanel actionPanel(){
		JXPanel pan = new JXPanel();
		pan.setOpaque(false);
		pan.setPreferredSize(new Dimension(0,40));

		String xwert = "2dlu,"+
		"fill:0:grow(0.33),5dlu,"+
		"fill:0:grow(0.33),5dlu,"+
		"fill:0:grow(0.33),5dlu,"+
		"2dlu";

		String ywert = "2px:g,p,2px:g";
		FormLayout lay = new FormLayout(xwert,ywert);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		
		jsl = new JSlider(JSlider.HORIZONTAL,FPS_MIN, FPS_MAX, FPS_INIT);
		jsl.addChangeListener(cl);
		jsl.setMajorTickSpacing(2);
		jsl.setMinorTickSpacing(2);
		jsl.setPaintTicks(true);
		jsl.setSnapToTicks(true);

		pan.add(jsl,cc.xy(2,2,CellConstraints.DEFAULT,CellConstraints.CENTER));
		buts[0] = ButtonTools.macheButton("Schuß", "schuss", al);
		buts[0].setIcon(SystemConfig.hmSysIcons.get("camera"));
		pan.add(buts[0],cc.xy(4,2,CellConstraints.DEFAULT,CellConstraints.CENTER));
		buts[1] = ButtonTools.macheButton("abbrechen", "abbrechen", al);
		pan.add(buts[1],cc.xy(6,2,CellConstraints.DEFAULT,CellConstraints.FILL));

		pan.validate();
		return pan;
	}

	/****************************************/
	class PhotoFrame extends JXPanel implements MouseListener,MouseMotionListener{
		private static final long serialVersionUID = 2882847000287058980L;
		

		int width = defaultsize[0];
		int height = defaultsize[1];
		int x = 0;
		int y = 0;
		int dragX;
		int dragY;
		
		int clickX;
		int clickY;
		
		public PhotoFrame(){
			super();
			setOpaque(false);
			setDoubleBuffered(true);
			//setPreferredSize(new Dimension(320,240));
			//setVisible(true);
			addMouseListener(this);
			addMouseMotionListener(this);
		}
		public void setCenter(){
			x = (getWidth()/2) -(width/2);
			y = (getHeight()/2)-(height/2);
			repaint();
		}
		public void setActivePoint(int xValue,int yValue){
			x = xValue;
			y = yValue;
		}
		public Point getActivePoint(){
			return new Point(x,y);
		}

		public void setPhotoFrameSize(int x,int y){
			width = x;
			height = y;
		}
		public Dimension getPhotoFrameSize(){
			return new Dimension(width,height);
		}

		public void paint(Graphics g){
			super.paint(g);
		      Graphics2D g2d = (Graphics2D)g;
		      g2d.setColor(Color.RED);
		      BasicStroke bs1 = new BasicStroke(3, BasicStroke.CAP_BUTT, 
		              BasicStroke.JOIN_BEVEL);
		      
		      g2d.setStroke(bs1);
		      g2d.draw3DRect(x, y, width, height, true);
		      //g2d.drawLine(50, 200, 400, 200);

	    }

		@Override
		public void mouseDragged(MouseEvent arg0) {
			if(mouseIsIn){
				
				dragX = arg0.getX();
				dragY = arg0.getY();
				if((clickX+dragX) <= 0 ){
					x=0;
				}else if((clickX+dragX+width) > getWidth() ){
					x= getWidth()-width;
				}else if((clickY+dragY) <= 0 ){
					y = 0;
				}else if((clickY+dragY+height) > getHeight() ){
					y = getHeight()-height;
				}else{
					x =  (clickX+dragX);
					y =  (clickY+dragY);
				}
				repaint();
			}
		}

		@Override
		public void mouseMoved(MouseEvent arg0) {
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			
			//clickX = x-e.getX();
			//clickY = y-e.getY();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			clickX = x-e.getX();
			clickY = y-e.getY();
			if(e.getX() >= x && e.getX() <= x+width){
				if(e.getY() >= y && e.getY() <= y+height){
					setCursor(new Cursor(Cursor.MOVE_CURSOR));
					mouseIsIn = true;
					return;
				}
			}
			mouseIsIn = false;
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			clickX = 0;
			clickY = 0;
			dragX = 0;
			dragY = 0;
			mouseIsIn = false;
			
		}
			

	}
	

}
