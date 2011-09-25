package einzigesPaket;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.image.RenderedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


import com.mysql.jdbc.PreparedStatement;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;



import sun.awt.image.ImageFormatException;
import sun.awt.image.ToolkitImage;


public class piTool implements ComponentListener,ContainerListener,MouseListener,MouseMotionListener,KeyListener,ActionListener,WindowListener, ChangeListener {
public static JFrame jFrame = null;
public static piTool app = null;

// erforderliche controls
public JPanel knopf = null;
public JPanel tabelle = null;
public JPanel shot = null;
public JButton speichern = null;
public JButton laden = null;
public JButton loeschen = null;

public static JButton senden = null;

public JTextField titel = null;
public JTextField beschreib = null;
JRadioButton [] jrb = {null,null};
ButtonGroup jrbg = new ButtonGroup();

public String aktbild = null;
public int iaktbild = -1;

public static JLabel vorschau = null;

public static Image img = null;
public static Image img2 = null;
public static Image vos = null;
public static Image orig = null;
public static BufferedImage original = null;
public static int xoriginal;
public static int yoriginal;
public static double zoom = 1.000000;
public static JSlider jsl = null;
static final int FPS_MIN = -25;
static final int FPS_MAX = +25;
static final int FPS_INIT = 0;    //initial frames per second
static JLabel lblzoom;
static JLabel lblinfo;

//Datenbank-Geraffel
public Connection conn = null;
public boolean dbok = false;

//Scrollpane für jroller
public JScrollPane vroller = null;
//hier werden die kleinen Grafiken dargestellt
public JPanel jroller = null;

public static Image[] vbilder = null;
public static Vector shotvec = new Vector();
public JProgressBar pbar = null;

public String dbConnection = null;
public String dbParameter = null;
public String emailDaten;

public static String proghome;
 
public static HashMap<String,String> hmEmailExtern;
public static HashMap<String,String> hmEmailIntern;
public String gimpCommand = "C:/Programme/GIMP-2.0/bin/gimp-2.6.exe";
	
	public static void main(String[] args) {
		String prog = java.lang.System.getProperty("user.dir");
		/*
		int ind1 = prog.lastIndexOf("PiTool");
		int ind2 = prog.indexOf("PiTool");
		if((ind1 >= 0) && (ind1 != ind2)){
			prog = prog.substring(0,ind1);
		}

		if(prog.contains("\\PiTool")){
			String cuthome = prog.replace("PiTool", "");
			System.out.println("nach cut "+cuthome);
			proghome = cuthome.replace('\\', '/');
		}else{
			proghome = prog.replace('\\', '/');			
		}


		 
		if(!proghome.substring(proghome.length()-1).equals("/")){
			proghome = proghome+'/';
		}else{
		}
		*/
		if(System.getProperty("os.name").contains("Linux")){
			proghome = "/opt/RehaVerwaltung/";
		}else if(System.getProperty("os.name").contains("Windows")){
			proghome = prog.substring(0, 2)+"/RehaVerwaltung/";
		}else if(System.getProperty("os.name").contains("Mac")){
			proghome = "/opt/RehaVerwaltung/";
		}

		System.out.println("ProgHome = "+proghome);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				piTool application = new piTool();
				application.getJFrame();
				app = application;
				app.jFrame.setIconImage( Toolkit.getDefaultToolkit().getImage( proghome+"icons/camera.png" ) );
				SwingUtilities.invokeLater(new Runnable(){
					public  void run(){
						new Thread(){
							public void run(){
								SystemStart();
								DatenBankStarten();
								if(app.dbok){
									piTool.jFrame.setCursor(Cursor.WAIT_CURSOR);
									holeShots hl = new holeShots();
									hl.execute();
									//HoleBilder();
									//JOptionPane.showMessageDialog(null,"Datenbank wurde gestartet!");
								}else{
									JOptionPane.showMessageDialog(null,"Datenbank konnte nicht gestartet werden!");							
								}

							}
						}.start();
						
						
					}
				});	

			}
		});		
	}
	public static void macheScroll(){
		if(vbilder != null){

			for(int i= 0; i < vbilder.length; i++){
				macheScroller(piTool.vbilder[i],(String)((Vector)shotvec.get(i)).get(1));
				
			}
			
		}
	}
	public static void macheScroller(Image imx,String name){
		JLabel label = new JLabel();
		label.setIcon(new ImageIcon(imx));
		label.setName(name);
		label.addMouseListener(app);
		label.setToolTipText(name);
		piTool.app.jroller.add(label);
		piTool.app.vroller.validate();
	}
	
	private void getJFrame(){
		jFrame = new JFrame();
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setSize(900, 700);
		//jFrame.setRootPane(getJRootPane());
		jFrame.setTitle("pi-ScreenShot Tool");
		//jFrame.addComponentListener(this);
		//jFrame.addContainerListener(this);		

		jFrame.setContentPane(getContentPane());
		jFrame.setVisible(true);
		
		
	}
	private JPanel getContentPane(){
		JPanel cont = new JPanel(new BorderLayout());
		cont.setBorder(null);
		cont.add(getSued(), BorderLayout.NORTH);
		
		//cont.add(getKnopf(),BorderLayout.WEST);
		JScrollPane contscr = new JScrollPane(getKnopf());
		contscr.getVerticalScrollBar().setUnitIncrement(15);
		contscr.validate();
		cont.add(contscr,BorderLayout.WEST);

		cont.add(getEast(),BorderLayout.EAST);
		cont.add(getTabelle(),BorderLayout.SOUTH);
		cont.add(getShot(),BorderLayout.CENTER);		
		return cont;
	}
	private JPanel getSued(){
		JPanel sued = new JPanel();
		sued.setPreferredSize(new Dimension(0,20));
		return sued;
	}
	private JPanel getEast(){
		JPanel sued = new JPanel();
		sued.setPreferredSize(new Dimension(20,0));
		return sued;
	}

	private JPanel getKnopf(){
		knopf = new JPanel();
		knopf.setBorder(null);
		
		int x = 140;
		int y = 20;
		knopf.setPreferredSize(new Dimension(160,0));

		FormLayout lay = new FormLayout("5px,140px,15px",
//           clipb        vorlab      vorsch           titlab       edit       rad1      rad2        speich    progr       vscroll       lade      inet       lösch   email
//		 1    2      3     4     5    	6        7      8     	9    10    11   12   13   14    5     16    17   18    19    	20        21   22   23    24   25  26  27   28
		"5px,20px,  4px , 20px, 4px,  15px, 2px,  100px   , 5px,   15px,   5px, 20px, 12px, 17px ,3px,17px  ,10px, 20px ,5px, 15px, 2px,  120dlu:g ,  5px, 5px, 20px ,5px,20px,8px,20px,8px");
		knopf.setLayout(lay);
		CellConstraints cc = new CellConstraints();


		JButton jb = new JButton("ClipBoard holen");
		jb.setSize(new Dimension(120,20));
		jb.setActionCommand("zeigen");
		jb.addActionListener(this);
		knopf.add(jb, cc.xy(2, 2));

		jb = new JButton("Gimp starten");
		jb.setSize(new Dimension(120,20));
		jb.setActionCommand("gimpstart");
		jb.addActionListener(this);
		knopf.add(jb, cc.xy(2, 4));
		
		knopf.add(new JLabel("Vorschau"),cc.xy(2,6));

		vorschau = new JLabel();
		vorschau.setBackground(Color.WHITE);
		vorschau.setPreferredSize(new Dimension(140,100));
		
		knopf.add(vorschau,cc.xy(2,8));
		knopf.add(new JLabel("Titel eingeben"),cc.xy(2,10));

		titel = new JTextField();
		titel.setSize(new Dimension(120,15));
		knopf.add(titel,cc.xy(2,12));

	
		jrb[0] = new JRadioButton("im I-Net veröffentlichen");
		jrb[0].setPreferredSize(new Dimension(0,10));
		jrb[1] = new JRadioButton("lokal als JPG speichern");
		jrb[0].setPreferredSize(new Dimension(0,10));		
		jrbg.add(jrb[0]);
		jrbg.add(jrb[1]);
		knopf.add(jrb[0],cc.xy(2,14));
		knopf.add(jrb[1],cc.xy(2,16));
		jrb[0].setSelected(true);

		speichern = new JButton("Speichern");
		speichern.setSize(new Dimension(120,speichern.getPreferredSize().height));
		speichern.setMinimumSize(new Dimension(140,0));
		speichern.setActionCommand("speichern");
		speichern.addActionListener(this);
		knopf.add(speichern,cc.xy(2,18));

		pbar = new JProgressBar();
		pbar.setBackground(Color.RED);
		pbar.setStringPainted(true);
		pbar.setPreferredSize(new Dimension(x,15));
		pbar.setEnabled(false);
		knopf.add(pbar,cc.xy(2,20));
		
		jroller = new JPanel();
		jroller.setLayout(new BoxLayout(jroller,BoxLayout.Y_AXIS));
		vroller = new JScrollPane(jroller);
		vroller.setPreferredSize(new Dimension(x+10,200));
		vroller.getVerticalScrollBar().setUnitIncrement(15);
		vroller.validate();
		knopf.add(vroller,cc.xywh(2,22,2,2));
		
		laden = new JButton("JPG-laden");
		laden.setSize(new Dimension(120,20));
		laden.setActionCommand("laden");
		laden.addActionListener(this);
		knopf.add(laden,cc.xy(2,23));

		loeschen = new JButton("Im I-Net löschen");
		loeschen.setSize(new Dimension(120,20));
		loeschen.setActionCommand("loeschen");
		loeschen.addActionListener(this);
		loeschen.setEnabled(false);
		knopf.add(loeschen,cc.xy(2,27));		
		
		senden = new JButton("Shot-Email");
		senden.setSize(new Dimension(120,20));
		senden.setActionCommand("senden");
		senden.addActionListener(this);
		senden.setEnabled(false);
		knopf.add(senden,cc.xy(2,29));		
		return knopf;
		
	}
	
	private JPanel getTabelle(){
		FlowLayout fl = new FlowLayout(FlowLayout.CENTER);
		fl.setHgap(5);
		tabelle = new JPanel(fl);
		//tabelle.setBorder(BorderFactory.createEmptyBorder(0, 0, 7, 0));
		//tabelle.setPreferredSize(new Dimension(0,25));
		

		jsl = new JSlider(JSlider.HORIZONTAL,FPS_MIN, FPS_MAX, FPS_INIT);
		jsl.addChangeListener(this);

		//Turn on labels at major tick marks.
		jsl.setMajorTickSpacing(10);
		jsl.setMinorTickSpacing(1);
		jsl.setPaintTicks(true);
		jsl.setSnapToTicks(true);
		
		lblzoom = new JLabel();
		lblzoom.setFont( new Font("SansSerif",Font.PLAIN,11) );
		lblzoom.setText("Skalierung (-25 bis +25): "+jsl.getValue()+"  ");

		tabelle.add(lblzoom);
		tabelle.add(jsl);

		JButton jbut = new JButton();
		jbut.setFont(new Font("SansSerif",Font.PLAIN,11));
		jbut.setText("anwenden");		
		jbut.setSize(new Dimension(40,15));
		jbut.setActionCommand("anwenden");
		jbut.addActionListener(this);
		tabelle.add(jbut);		

		lblinfo = new JLabel();
		lblinfo.setFont(new Font("SansSerif",Font.PLAIN,11));
		tabelle.add(lblinfo);


		return tabelle;
	}
	private JScrollPane getShot(){
		shot = new ShotBereich();
		shot.setBackground(Color.WHITE);
		JScrollPane shotscr = new JScrollPane(shot);
		shotscr.getVerticalScrollBar().setUnitIncrement(15);
		shotscr.getHorizontalScrollBar().setUnitIncrement(20);
		return shotscr;
	}
	
	public JRootPane getJRootPane() {
			JRootPane jp = new JRootPane();
			jp.setDoubleBuffered(true);
			jp.setBackground(Color.WHITE);
		return jp;
	}
	public static void pBarInit(int von, int bis){
		app.pbar.setBackground(Color.RED);
		app.pbar.setMinimum(von);
		app.pbar.setMaximum(bis);
	}
	public static void pBarAkt(int value){
		app.pbar.setValue(value);
	}
	public static Image getClipboard() {
		piTool.app.loeschen.setEnabled(false);
        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
    
        try {
            if (t != null && t.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                img = (Image)t.getTransferData(DataFlavor.imageFlavor);
                img2 = new BufferedImage(img.getWidth(jFrame), img.getHeight(jFrame),
                	      BufferedImage.TYPE_INT_RGB);//ARGB für Transparenz!
                	Graphics g = img2.getGraphics();
                	Graphics2D g2 = (Graphics2D) g;
                	g2.drawImage(img, 0, 0, null);

                	g2.dispose();
					original = new BufferedImage(img2.getWidth(null),img2.getHeight(null),BufferedImage.TYPE_INT_RGB);
                	g = original.getGraphics();
                	g2 = (Graphics2D) g;
                	g2.drawImage(img, 0, 0, null);
                	g2.dispose();

                	xoriginal = img2.getWidth(null);
                	yoriginal = img2.getHeight(null);
                	lblinfo.setText("  Bildinfo - Pixel: X="+xoriginal+" Y="+yoriginal);

                	System.out.println("X="+xoriginal+" / Y="+yoriginal);
                orig =(Image) ((ToolkitImage)((BufferedImage)t.getTransferData(DataFlavor.imageFlavor)).getScaledInstance(140, 100, Image.SCALE_SMOOTH)); 
                senden.setEnabled(true);
            	zoom = 1.000000;
                return img;
            }
        } catch (UnsupportedFlavorException e) {
        } catch (IOException e) {
        }
        senden.setEnabled(false);
        img = null;
        ShotBereich.setzeAusschnitt(false);
        return null;
    }
	public static byte[] bufferedImageToByteArray(BufferedImage img) throws ImageFormatException, IOException{
		if(img != null){
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
		JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(img);  
		param.setQuality((float) 1.0f, false);  
		encoder.setJPEGEncodeParam(param);  
		encoder.encode(img);
		os.close();
		return os.toByteArray();
		}else{
			return null;
		}
	}


	@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		knopf.revalidate();
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentAdded(ContainerEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentRemoved(ContainerEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		String comp = ((JComponent)arg0.getSource()).getName();
		boolean gefunden = false;
		if(arg0.getSource() instanceof JLabel){
			for(int i = 0;i< shotvec.size() ;i++){
				if(	comp.equals((String) ((Vector)shotvec.get(i)).get(1))){
					aktbild = comp;
					iaktbild = (Integer) ((Vector)shotvec.get(i)).get(0);
					gefunden = true;
					break;
				}
			}
		}
		if(gefunden){
			
			piTool.jFrame.setCursor(Cursor.WAIT_CURSOR);
			System.out.println("Bild angeklickt: BildName = "+aktbild);
			System.out.println("Bild angeklickt: BildID = "+iaktbild);
			img = HoleGrossesBild(((JComponent)arg0.getSource()).getName());
			img2 = img;
			original = new BufferedImage(img.getWidth(null),img.getHeight(null),BufferedImage.TYPE_INT_RGB);
        	Graphics g = original.getGraphics();
        	Graphics2D g2 = (Graphics2D) g;
        	g2.drawImage(img, 0, 0, null);
        	g2.dispose();
        	xoriginal = img2.getWidth(null);
        	yoriginal = img2.getHeight(null);
        	System.out.println("X="+xoriginal+" / Y="+yoriginal);
			jsl.setValue(0);
			((ShotBereich) shot).Zeichnen();
			piTool.app.loeschen.setEnabled(true);
			piTool.jFrame.setCursor(Cursor.DEFAULT_CURSOR);
			ShotBereich.setzeAusschnitt(false);

		}else{
			aktbild = "";
			iaktbild = -1;
			JOptionPane.showMessageDialog(null,"Das angeforderte Bild konnte nicht gefunden werden");
			piTool.app.loeschen.setEnabled(false);
		}
		
		
		
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getActionCommand().equals("anwenden")){
			skaliereBild();
		}
		if(arg0.getActionCommand().equals("zeigen")){
			getClipboard();
			//System.out.println("ClipBoardinhalt = "+getClipboard());
			jsl.setValue(0);
			((ShotBereich) shot).Zeichnen(); 
			shot.repaint();
			if(vos!=null){
	   		 	speichern.setEnabled(true);				
			}
			/*
			try {
				bufferedImageToByteArray((BufferedImage)img);
			} catch (ImageFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			*/
		}
		if(arg0.getActionCommand().equals("gimpstart")){
			try {
				Process process = new ProcessBuilder(gimpCommand,"").start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(arg0.getActionCommand().equals("speichern")){
			String stitel = titel.getText().trim();
			if(stitel.equals("")){
				JOptionPane.showMessageDialog(null,"Bitte Titel für den ScreenShot eingeben!");
				return;
			}
			if((img==null) || (img2==null) || (vos==null)){
				JOptionPane.showMessageDialog(null,"Ein erforderliches Objekt ist null. Speichern nicht möglich");
				return;
			}
			if(gibtsSchon(stitel) > 0){
				JOptionPane.showMessageDialog(null,"Dieser Titel existiert bereits.\nBitte geben Sie einen anderen Titel ein");
				titel.requestFocus();
				return;
			}

			try
		     {
				speichernQualitaet(stitel,1.0f);
		         //boolean boo = ImageIO.write((RenderedImage) img, "jpg", new File("C:\\ScreenShots\\"+stitel+".jpg") ) ;
		         //System.out.println("Ergebnis von Bild = "+boo);
		         boolean boo = ImageIO.write((RenderedImage) vos, "jpg", new File(piTool.proghome+"ScreenShots/"+stitel+"_kl.jpg") ) ;
		         System.out.println("Ergebnis von Vorschau = "+boo);
		         if(jrb[0].isSelected()){
			         final String xtitel = stitel;
			         new Thread(){
			        	 public void run(){
			        		 SpeichereBilder();	
			        		 piTool.vorschau.setIcon(new ImageIcon());
			        		 piTool.vorschau.validate();
			        		 speichern.setEnabled(false);
			        		 /*
			        		 Vector ar = new Vector();
							 ar.add(-1);
							 ar.add(xtitel);
							 shotvec.add((Vector)ar.clone());
							 */

			        	 }
			         }.start();
			         JOptionPane.showMessageDialog(null, "Die Grafiken wurde im Internet veröffentlichtund\nzusätzlich lokal auf Ihrer Festplatte gespeichert\n"+
			        		 		"Verzeichnis: "+piTool.proghome+"/ScreenShots");
		         }else{
			         JOptionPane.showMessageDialog(null, "Die Grafiken wurde auf Ihrer Festplatte gespeichert\n"+
			        		 "Verzeichnis: "+piTool.proghome+"/ScreenShots");
		        	 
		         }
		     }
		     catch(IOException ex)
		     {
		         //...
		     }
		     
		}
		if(arg0.getActionCommand().equals("laden")){
			piTool.jFrame.setCursor(Cursor.WAIT_CURSOR);
			SwingUtilities.invokeLater(new Runnable(){
				public  void run(){
					macheGrafikLaden();
				}
			});
			speichern.setEnabled(true);
			
		}
		if(arg0.getActionCommand().equals("loeschen")){
			if((!aktbild.equals("")) && (iaktbild >= 0)){
				loescheImInet();
				JOptionPane.showMessageDialog(null,"Die Grafik wurd im Internet gelöscht!");
			}else{
				JOptionPane.showMessageDialog(null,"Es wurde keine Grafik ausgewählt!");				
			}
		}
		if(arg0.getActionCommand().equals("senden")){
			if(img != null){
				//try {
					speichernQualitaet("email-shot",1.0f);
					//boolean boo = ImageIO.write((RenderedImage) img, "jpg", new File("C:\\ScreenShots\\email-shot.jpg") ) ;
					ShotSenden ss = new ShotSenden();
					ss.setLocationRelativeTo(jFrame);
					ss.setVisible(true);
				//} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				//}
			}else{
				JOptionPane.showMessageDialog(null,"Es ist kein ScreenShot für Email verfügbar!");
			}
		}

		
	}
	private void speichernQualitaet(String stitel,Float fQuality){
//		img, "jpg", new File("C:\\ScreenShots\\"+stitel+".jpg")
		IIOImage imgq = new IIOImage((RenderedImage) img, null, null);
        ImageWriter writer = ImageIO.getImageWritersBySuffix("jpeg").next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(fQuality);
        File fimg = new File(piTool.proghome+"ScreenShots/"+stitel+".jpg");
        try {
        	ImageOutputStream out = ImageIO.createImageOutputStream(fimg); 
			writer.setOutput(out);
			writer.write(null, imgq, param);
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //writer.write(null, img, param);
	}
/******************************/
		private void loescheImInet(){
			int anzahlcomp = jroller.getComponentCount();
			for(int i = 0; i<anzahlcomp;i++){
				try{
					if(jroller.getComponent(i).getName() != null){
						if(jroller.getComponent(i).getName().equals(aktbild)){
							JComponent com = (JComponent) jroller.getComponent(i);
							jroller.remove(com);
							jroller.revalidate();
							System.out.println("Jetzt den Satz mit id = "+iaktbild+" löschen");
							String stmt = "delete from sshots where id='"+iaktbild+"'";
							final String xstmt = stmt;
							new Thread(){
								public void run(){
									loescheAktuellesBild(xstmt);									
								}
							}.start();
							aktbild = "";
							iaktbild = -1;
							System.out.println(stmt);
						}
					}
				}catch(java.lang.ArrayIndexOutOfBoundsException e){
					
				}
			}	
		}
/******************************/
		private void macheGrafikLaden(){
			final JFileChooser chooser = new JFileChooser("Verzeichnis wÃhlen");
	        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
	        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	        final File file = new File(piTool.proghome+"ScreenShots/");

	        chooser.setCurrentDirectory(file);

	        chooser.addPropertyChangeListener(new PropertyChangeListener() {
	            public void propertyChange(PropertyChangeEvent e) {
	                if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)
	                        || e.getPropertyName().equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
	                    final File f = (File) e.getNewValue();
	                }
	            }
	        });
	        chooser.setVisible(true);
	        piTool.jFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	        final int result = chooser.showOpenDialog(null);

	        if (result == JFileChooser.APPROVE_OPTION) {
	            File inputVerzFile = chooser.getSelectedFile();
	            String inputVerzStr = inputVerzFile.getPath();
	            System.out.println("Eingabepfad:" + inputVerzStr);
	            if(inputVerzStr.trim().toUpperCase().indexOf(".JPG") < 0){
	            	JOptionPane.showMessageDialog(null,"Es sing nur JPG-Dateien erlaubt!");
	            }else{
	            	try {
						img = new ImageIcon(ImageIO.read(new File(inputVerzStr)))
						.getImage();
						img2 = img;
						((ShotBereich) shot).Zeichnen();
						original = new BufferedImage(img.getWidth(null),img.getHeight(null),BufferedImage.TYPE_INT_RGB);
						Graphics g = original.getGraphics();
						Graphics2D g2 = (Graphics2D) g;
	                	g2 = (Graphics2D) g;
	                	g2.drawImage(img, 0, 0, null);
	                	g2.dispose();
	                	xoriginal = img.getWidth(null);
	                	yoriginal = img.getHeight(null);
	                	lblinfo.setText("  Bildinfo - Pixel: X="+xoriginal+" Y="+yoriginal);
						
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

	            }
	        }
	        System.out.println("Abbruch");
	        chooser.setVisible(false); 			
			
		}
/******************************/
		public static void DatenBankStarten(){
			final String sDB = "SQL";
			try {
				Class.forName(piTool.app.dbConnection);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				piTool.app.conn = (Connection) DriverManager.getConnection(piTool.app.dbParameter,"entwickler","entwickler");
				//piTool.app.conn = (Connection) DriverManager.getConnection("jdbc:mysql://192.168.2.2:3306/dbf","entwickler","entwickler");
				//piTool.app.conn = (Connection) DriverManager.getConnection("jdbc:mysql://rtahost.dyndns.org:3306/dbf","entwickler","entwickler");
				piTool.app.dbok = true;
	    	} 
	    	catch (final SQLException ex) {
	    		System.out.println("SQLException: " + ex.getMessage());
	    		System.out.println("SQLState: " + ex.getSQLState());
	    		System.out.println("VendorError: " + ex.getErrorCode());
				piTool.app.dbok = false;
	    		return;
	    	}
	
		}
		
		public static int gibtsSchon(String titel){
			Statement stmt = null;;
			ResultSet rs = null;
			int bilder = 0;
		
			//piTool.app.conn.setAutoCommit(true);
			try {
				stmt = (Statement) piTool.app.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
	                    ResultSet.CONCUR_UPDATABLE );
				String test = "select count(*) as zaehler from sshots where titel='"+titel+"'";
				rs = (ResultSet) stmt.executeQuery(test);
				if(rs.next()){
					bilder = rs.getInt("zaehler");
				}	

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException sqlEx) { // ignore }
						rs = null;
					}
					if (stmt != null) {
						try {
							stmt.close();
						} catch (SQLException sqlEx) { // ignore }
							stmt = null;
						}
					}
				}
			}
			
			return bilder;
		}
		

/**********************/		
		public static void HoleBilder(){
			Statement stmt = null;;
			ResultSet rs = null;
			int bilder = 0;
			
			final bitteWarten bw=null;
			new Thread(){
				public void run(){
					bitteWarten bw = new bitteWarten("lade Bild - bitte warten...");
					bw.setLocationRelativeTo(piTool.jFrame);
					bw.setVisible(true);
				}
			}.start();
			while(bw == null){
				try {
					Thread.sleep(80);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}



		
			//piTool.app.conn.setAutoCommit(true);
			try {
				stmt = (Statement) piTool.app.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
	                    ResultSet.CONCUR_UPDATABLE );
				String test = "select count(*) as zaehler from sshots";
				rs = (ResultSet) stmt.executeQuery(test);
				bilder = 0;
				if(rs.next()){
					bilder = rs.getInt("zaehler");
				}	
				if(bilder > 0){
					pBarInit(0,bilder+1);
					piTool.vbilder = new Image[bilder];
					test = "select vorschau,id,titel from sshots";
					rs = (ResultSet) stmt.executeQuery(test);
					int lauf = 0;
					while(rs.next()){
						pBarAkt(lauf);
						piTool.vbilder[lauf] = ImageIO.read( new ByteArrayInputStream(rs.getBytes("vorschau")) );
						System.out.println("Länge des Bildes ist "+rs.getBytes("vorschau").length);
						Vector ar = new Vector();
						ar.add(rs.getInt("id"));
						ar.add(rs.getString("titel"));
						shotvec.add((Vector)ar.clone());
						//piTool.vbilder[lauf] = ImageIO.read(new ByteArrayInputStream(rs.getBlob("vorschau")));
						//JLabel label = new JLabel();
						//label.setIcon(new ImageIcon( ImageIO.read(new ByteArrayInputStream(rs.getBytes("vorschau")))  ));
						//piTool.app.jroller.add(label);

						lauf++;

					}
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException sqlEx) { // ignore }
						rs = null;
					}
					if (stmt != null) {
						try {
							stmt.close();
						} catch (SQLException sqlEx) { // ignore }
							stmt = null;
						}
					}
				}
			}
			while(bw == null){
				try {
					Thread.sleep(80);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			bw.dispose();
		
		}
/**************************/
		public static BufferedImage HoleGrossesBild(String titel){
			Statement stmt = null;;
			ResultSet rs = null;
			int bilder = 0;
			Image bild = null;
			

			//piTool.app.conn.setAutoCommit(true);
			try {
				stmt = (Statement) piTool.app.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
	                    ResultSet.CONCUR_UPDATABLE );
					
					String test = "select bild from sshots where titel ='"+titel+"'";
					rs = (ResultSet) stmt.executeQuery(test);

					while(rs.next()){
						bild = ImageIO.read( new ByteArrayInputStream(rs.getBytes("bild")) );

					}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException sqlEx) { // ignore }
						rs = null;
					}
					if (stmt != null) {
						try {
							stmt.close();
						} catch (SQLException sqlEx) { // ignore }
							stmt = null;
						}
					}
				}
			}
			
	
		return (BufferedImage) bild;	
		}
		
/**************************/		
		public static void SpeichereBilder(){
			Statement stmt = null;;
			ResultSet rs = null;
			boolean ret = false;
			int bilder = 0;
		
			//piTool.app.conn.setAutoCommit(true);
			try {
				stmt = (Statement) piTool.app.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
	                    ResultSet.CONCUR_UPDATABLE );
				
				String select = "Insert into sshots set vorschau = ? , titel = ?, bild = ?";
				  PreparedStatement ps = (PreparedStatement) piTool.app.conn.prepareStatement(select);
				  ps.setBytes(1, bufferedImageToByteArray( (BufferedImage) vos));
				  ps.setString(2, piTool.app.titel.getText());
				  ps.setBytes(3, bufferedImageToByteArray( (BufferedImage) img));
				  //ps.setString(2, "vorschau");
				  ps.execute();

				  macheScroller(vos,app.titel.getText());
				  
				  String neuid = "select max(id) from sshots";
				  rs = (ResultSet) stmt.executeQuery(neuid);
				  rs.next();
				  int ineuid = rs.getInt(1);
				  Vector ar = new Vector();
				  ar.add(ineuid);
				  ar.add(piTool.app.titel.getText());
				  shotvec.add((Vector)ar.clone());
				  System.out.println("Neues Bild Name= "+piTool.app.titel.getText()+ " / id in Datenbank = "+ineuid);



				  
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ImageFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException sqlEx) { // ignore }
						rs = null;
					}
					if (stmt != null) {
						try {
							stmt.close();
						} catch (SQLException sqlEx) { // ignore }
							stmt = null;
						}
					}
				}
			}
			
		}

		/******************************/
		/**************************/		
		public static void loescheAktuellesBild(String xStmt){
			Statement stmt = null;;
			ResultSet rs = null;
			boolean ret = false;
			int bilder = 0;
		
			//piTool.app.conn.setAutoCommit(true);
			try {
				stmt = (Statement) piTool.app.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
	                    ResultSet.CONCUR_UPDATABLE );
				
				  String neuid = new String(xStmt);
				  ret = stmt.execute(neuid);
				  
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException sqlEx) { // ignore }
						rs = null;
					}
					if (stmt != null) {
						try {
							stmt.close();
						} catch (SQLException sqlEx) { // ignore }
							stmt = null;
						}
					}
				}
			}
			
		}

		/******************************/
		
/***********************************/

@Override
public void windowActivated(WindowEvent arg0) {
	// TODO Auto-generated method stub
	
}

@Override
public void windowClosed(WindowEvent arg0) {
	// TODO Auto-generated method stub
	try {
		piTool.app.conn.close();
		JOptionPane.showMessageDialog(null,"Datenverbindung wurde gelöst");
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

@Override
public void windowClosing(WindowEvent arg0) {
	// TODO Auto-generated method stub
	
}

@Override
public void windowDeactivated(WindowEvent arg0) {
	// TODO Auto-generated method stub
	
}

@Override
public void windowDeiconified(WindowEvent arg0) {
	// TODO Auto-generated method stub
	
}

@Override
public void windowIconified(WindowEvent arg0) {
	// TODO Auto-generated method stub
	
}

@Override
public void windowOpened(WindowEvent arg0) {
	// TODO Auto-generated method stub
	
}
public static void SystemStart(){
		String datei = piTool.proghome+"ini/pitool.ini";
		File f = new File(datei);
		if(!f.exists()){
			System.out.println("Sie Rindvieh haben die pitool.ini im verkehrten Verzeichnis");
		}else{
			System.out.println("Ihre pitool.ini befindet sich im richtigen Verzeichnis--> "+datei);			
		}
		INIFile ini = new INIFile(datei);
		piTool.app.dbConnection = 	 new String(ini.getStringProperty("DatenBank","DBTreiber"));
		piTool.app.dbParameter =  new String(ini.getStringProperty("DatenBank","DBKontakt"));
		String pw = new String(ini.getStringProperty("EmailExtern","Password"));
		hmEmailExtern = new HashMap<String,String>();
		hmEmailExtern.put("SmtpHost",new String(ini.getStringProperty("EmailExtern","SmtpHost")));
		hmEmailExtern.put("SmtpAuth",new String(ini.getStringProperty("EmailExtern","SmtpAuth")));			
		hmEmailExtern.put("Pop3Host",new String(ini.getStringProperty("EmailExtern","Pop3Host")));
		hmEmailExtern.put("Username",new String(ini.getStringProperty("EmailExtern","Username")));
		hmEmailExtern.put("SenderAdresse",new String(ini.getStringProperty("EmailExtern","SenderAdresse")));			

		if(pw.trim().equals("")){
			passwortEncrypt pe = new passwortEncrypt("Passwort für Externe-Email");
			pe.setLocationRelativeTo(jFrame);
			pe.setVisible(true);
			pw = new String(ini.getStringProperty("EmailExtern","Password"));
			Verschluesseln man = Verschluesseln.getInstance();
		    man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
			String decrypted = man.decrypt (pw);
			hmEmailExtern.put("Password",new String(decrypted));
		}else{
			Verschluesseln man = Verschluesseln.getInstance();
		    man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
			String decrypted = man.decrypt (pw);
			hmEmailExtern.put("Password",new String(decrypted));			
		}
		hmEmailIntern = new HashMap<String,String>();
		hmEmailIntern.put("SmtpHost",new String(ini.getStringProperty("EmailIntern","SmtpHost")));
		hmEmailIntern.put("SmtpAuth",new String(ini.getStringProperty("EmailIntern","SmtpAuth")));			
		hmEmailIntern.put("Pop3Host",new String(ini.getStringProperty("EmailIntern","Pop3Host")));
		hmEmailIntern.put("Username",new String(ini.getStringProperty("EmailIntern","Username")));
		hmEmailIntern.put("SenderAdresse",new String(ini.getStringProperty("EmailIntern","SenderAdresse")));			

		pw = new String(ini.getStringProperty("EmailIntern","Password"));
		if(pw.trim().equals("")){
			passwortEncrypt pe = new passwortEncrypt("Passwort für Interne-Email");
			pe.setLocationRelativeTo(jFrame);
			pe.setVisible(true);
			pw = new String(ini.getStringProperty("EmailIntern","Password"));
			Verschluesseln man = Verschluesseln.getInstance();
		    man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
			String decrypted = man.decrypt (pw);
			hmEmailIntern.put("Password",new String(decrypted));
		}else{
			Verschluesseln man = Verschluesseln.getInstance();
		    man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
			String decrypted = man.decrypt (pw);
			hmEmailIntern.put("Password",new String(decrypted));			
		}

}
/*************************************************************************/
@Override
public void stateChanged(ChangeEvent arg0) {
	// TODO Auto-generated method stub
	float zwert = new Float(jsl.getValue());
	float fxoriginal = new Float(xoriginal);
	float fyoriginal = new Float(yoriginal);
	System.out.println("fXOriginal="+fxoriginal+" / fYOiginal="+fyoriginal);
	float zoomx = new Float(fxoriginal/100.0f*5.0f);
	float zoomy = new Float(fyoriginal/100.0f*5.0f); 
	float zx1 = new Float(fxoriginal+(zoomx*zwert));
	float zy1 = new Float(fyoriginal+(zoomy*zwert));
	
	System.out.println("fX="+zx1+" / fY="+zy1);
			
	

	int ixzoom = (int) Math.round(zx1);
	int iyzoom = (int) Math.round(zy1);
	
	if(ixzoom<= 0 || iyzoom <=0){
		lblzoom.setForeground(Color.RED);	
	}else{
		lblzoom.setForeground(Color.BLACK);		
	}

	
	
	lblzoom.setText("Skalierung (-25 bis +25): "+jsl.getValue()+"  ");
	//lblzoom.setText("Skalierung: "+jsl.getValue()+"  ");	
}
public void skaliereBild(){
	if(ShotBereich.label.getIcon() == null){
		ShotBereich.label.setText("Oh Herr sieh Dein Volk an - aber verzage nicht.....");
		String stext = "Was um Gottes Willen wollen Sie denn skalieren?\n\n"+
		"Etwa die die weiße Fläche - oder den Hammer unter Ihrem Helm?";
		JOptionPane.showMessageDialog(null,stext);
    	lblinfo.setText("");
		jsl.setValue(0);
		return;
	}
	//new SwingWorker<Void,Void>(){
	new SwingWorker(){

		@Override
		protected Object doInBackground() throws Exception {
			// TODO Auto-generated method stub
			
	piTool.jFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
	
	
	float fxoriginal = new Float(xoriginal);
	float fyoriginal = new Float(yoriginal);
	System.out.println("fXOriginal="+fxoriginal+" / fYOiginal="+fyoriginal);
	float zoomx = new Float(fxoriginal/100.0f*5.0f);
	float zoomy = new Float(fyoriginal/100.0f*5.0f); 	
	System.out.println("zoomx ="+zoomx+" / zoomy="+zoomy);

	float zwert = new Float(jsl.getValue());
	if(zwert == .0f){
		BufferedImage img2 = new BufferedImage(xoriginal,yoriginal,BufferedImage.TYPE_INT_RGB);
		Graphics g = img2.getGraphics();
		Graphics2D g2 = (Graphics2D) g;
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.drawImage(original, 0, 0, null);
		ShotBereich.label.setIcon(new ImageIcon(img2));
		piTool.img = img2;
		g2.dispose();
		piTool.jFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		return null;
	}

	float zx1 = new Float(fxoriginal+(zoomx*zwert));
	float zy1 = new Float(fyoriginal+(zoomy*zwert));
	
	System.out.println("fX="+zx1+" / fY="+zy1);
			
	int ixzoom = (int) Math.round(zx1);
	int iyzoom = (int) Math.round(zy1);
	
	if(ixzoom<= 0 || iyzoom <=0){
		JOptionPane.showMessageDialog(null, "Die gewählte Skalierung ergäbe ein Bild mit einer Größe < NULL.\nSkalierung nicht möglich");
		piTool.jFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		lblinfo.setText("  Bildinfo - Pixel: X="+ixzoom+" Y="+iyzoom);
		return null;
	}
	lblinfo.setText("  Bildinfo - Pixel: X="+ixzoom+" Y="+iyzoom);
	BufferedImage img2 = new BufferedImage(ixzoom,iyzoom,BufferedImage.TYPE_INT_RGB);
	Graphics g = img2.getGraphics();
	Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	g2.drawImage(original.getScaledInstance(
			ixzoom,iyzoom,Image.SCALE_AREA_AVERAGING), 0, 0, null);
	ShotBereich.label.setIcon(new ImageIcon(img2));
	piTool.img = img2;
	g2.dispose();
	
	piTool.senden.setEnabled(true);
	System.out.println("X="+ixzoom+" / Y="+iyzoom);
	System.out.println("Zoomfaktor = "+zoom);
	piTool.jFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	return null;
		}
		
	}.execute();
	
}

/*********************KlassenEndeKlammer nachstehend***********/
}
/*****************************************************/
class holeShots extends SwingWorker<Void,Void>{

	@Override
	protected Void doInBackground() throws Exception {
		Statement stmt = null;;
		ResultSet rs = null;
		int bilder = 0;


	
		//piTool.app.conn.setAutoCommit(true);
		try {
			stmt = (Statement) piTool.app.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE );
			String test = "select count(*) as zaehler from sshots";
			rs = (ResultSet) stmt.executeQuery(test);
			bilder = 0;
			if(rs.next()){
				bilder = rs.getInt("zaehler");
			}	
			if(bilder > 0){
				piTool.app.pbar.setEnabled(true);
				piTool.app.pBarInit(0,bilder);
				piTool.vbilder = new Image[bilder];
				test = "select vorschau,id,titel from sshots ORDER by id";
				rs = (ResultSet) stmt.executeQuery(test);
				int lauf = 0;
				while(rs.next()){
					piTool.app.pBarAkt(lauf+1);
					Thread.sleep(20);
					piTool.vbilder[lauf] = ImageIO.read( new ByteArrayInputStream(rs.getBytes("vorschau")) );
					System.out.println("Länge des Bildes ist "+rs.getBytes("vorschau").length);
					Vector ar = new Vector();
					ar.add(rs.getInt("id"));
					ar.add(rs.getString("titel"));
					piTool.app.shotvec.add((Vector)ar.clone());
					//piTool.vbilder[lauf] = ImageIO.read(new ByteArrayInputStream(rs.getBlob("vorschau")));
					//JLabel label = new JLabel();
					//label.setIcon(new ImageIcon( ImageIO.read(new ByteArrayInputStream(rs.getBytes("vorschau")))  ));
					//piTool.app.jroller.add(label);

					lauf++;

				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { // ignore }
					rs = null;
				}
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException sqlEx) { // ignore }
						stmt = null;
					}
				}
			}
		}
		
		piTool.app.macheScroll();
		piTool.app.pbar.setEnabled(false);
		piTool.jFrame.setCursor(Cursor.DEFAULT_CURSOR);		
		return null;
	}
	
}

class MacheBufferedImage extends JFrame {

    private final int WIDTH = 160;

    private final int HEIGHT = 120;

    private BufferStrategy strategy;

    private MemoryImageSource mis;

    private Image img;

    private int pix[] = new int[WIDTH * HEIGHT];

    private Thread runner = new Thread() {
        public void run() {

            init();
            int idx, dx, dy = dx = idx = 0;

            int red;
            int blue;

            while (true) {

                Graphics g = strategy.getDrawGraphics();

                idx = 0;
                dy += 2;
                dx += 2;

                for (int y = 0; y < HEIGHT; y++) {

                    for (int x = 0; x < WIDTH; x++) {

                        red = (int) (180 + 180
                                * Math.sin((x + dx)
                                        / (37 + 15 * Math.sin((y + dy) / 74)))
                                * Math.sin((y + dy)
                                        / (31 + 11 * Math.sin((x + dx) / 37))));
                        blue = (int) (180 + 180
                                * Math.cos((x + dx)
                                        / (37 + 15 * Math.cos((y + dy) / 74)))
                                * Math.cos((y + dy)
                                        / (31 + 11 * Math.cos((x + dx) / 37))));

                        pix[idx++] = (16 << 24) | (red << 16) | blue;

                    }
                }

                mis.newPixels(0, 0, WIDTH, HEIGHT);

                g.drawImage(img, 0, 0, null);
                g.dispose();

                strategy.show();

                try {
                    sleep(10L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void init() {
            int idx = 0;
            for (int y = 0; y < HEIGHT; y++) {
                int red = (y * 255) / (HEIGHT - 1);
                for (int x = 0; x < WIDTH; x++) {
                    int blue = (x * 255) / (WIDTH - 1);
                    pix[idx++] = (255 << 24) | (red << 16) | blue;
                }
            }
            mis = new MemoryImageSource(WIDTH, HEIGHT, pix, 0, WIDTH);
            mis.setAnimated(true);
            img = createImage(mis);
        }
    };

    public MacheBufferedImage() {
        super("BufferedImageExample");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setVisible(true);
        createBufferStrategy(2);
        strategy = getBufferStrategy();
        runner.start();
    }

    /**
     * @param args
     */

}
/**********************************/
class bitteWarten extends JDialog{
	public bitteWarten(String text){
		//super();
		//setUndecorated(true);
		setModal(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(250,125);
		/*
		JPanel jpWarten = new JPanel();
		jpWarten.setBackground(Color.WHITE);
		jpWarten.add(new JLabel(text));
		*/
		setBackground(Color.WHITE);
		add(new JLabel(text));
		//setContentPane(jpWarten);
		validate();
		setVisible(true);
	}
}
class passwortEncrypt extends JDialog{
	JPasswordField pf = new JPasswordField();
	JLabel lbl = new JLabel("Email-Passwort eingeben: ");
	JButton but = new JButton("Ok");
	public passwortEncrypt(String stitel){
		
		setTitle(stitel);	
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(350,125);
		setLayout(new BorderLayout());
		JPanel jpan = new JPanel();
		jpan.setPreferredSize(new Dimension(0,100));
		jpan.setBackground(Color.WHITE);
		pf.setPreferredSize(new Dimension(100,20));
		jpan.add(lbl);
		jpan.add(pf);
		but.addActionListener(new ActionListener(){
	    public void actionPerformed(ActionEvent e){
	    	verschluesseln(); }
	    });         
		add(jpan,BorderLayout.CENTER);
		add(but,BorderLayout.SOUTH);
		validate();

	}
	private void verschluesseln(){
		String pw = pf.getText();
		Verschluesseln man = Verschluesseln.getInstance();
		man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
		String encrypted = man.encrypt(pw);
		String gruppe = null;
		if(getTitle().equals("Passwort für Interne-Email" )){
			gruppe = "EmailIntern";
		}else{
			gruppe = "EmailExtern";			
		}
		String datei = piTool.proghome+"ini/pitool.ini";
		File f = new File(datei);
		if(!f.exists()){
			System.out.println("Sie Rindvieh haben die pitool.ini im verkehrten Verzeichnis");
			
		}else{
			System.out.println("Ihre pitool.ini befindet sich im richtigen Verzeichnis--> "+datei);			
		}
		INIFile ini = new INIFile(datei);
		ini.setStringProperty(gruppe, "Password", encrypted, null);
		ini.save();
		this.setVisible(false);
	}
}

class ShotSenden extends JDialog{
	JTextField tf = new JTextField();
	JLabel lbl = new JLabel("Empfängeradresse: ");
	JTextPane ta = new JTextPane();

	JComboBox comb = new JComboBox(new String[]{piTool.app.hmEmailIntern.get("SenderAdresse"),piTool.app.hmEmailExtern.get("SenderAdresse")});
	JButton but = new JButton("Senden");
	public ShotSenden(){
		
		setTitle("ScreenShot-Email senden");	
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(500,350);
		
		setLayout(new BorderLayout());

		JPanel jgross = new JPanel(new BorderLayout());

		tf.setPreferredSize(new Dimension(150,25));
		tf.setText(piTool.app.hmEmailIntern.get("SenderAdresse"));
		JPanel jpan = new JPanel(new FlowLayout());
		//jpan.setPreferredSize(new Dimension(0,100));
		jpan.setBackground(Color.WHITE);
		jpan.add(lbl);
		jpan.add(tf);

		jgross.add(jpan,BorderLayout.NORTH);

		ta.setPreferredSize(new Dimension(200,150));
		ta.setFont(new Font("Courier New",Font.PLAIN,12));
		
		JPanel tapan = new JPanel(new BorderLayout());
		tapan.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		tapan.add(new JLabel("Emailtext: "),BorderLayout.WEST);
		tapan.add(new JScrollPane(ta),BorderLayout.CENTER);
		jgross.add(tapan,BorderLayout.CENTER);

		but.addActionListener(new ActionListener(){
	    public void actionPerformed(ActionEvent e){
	    	senden(); }
	    });
		comb.setSelectedIndex(0);
		comb.addActionListener(new ActionListener(){
		    public void actionPerformed(ActionEvent e){
		    	tf.setText((String) ((JComboBox)e.getSource()).getSelectedItem()); }
		    }); 
		JPanel jcom = new JPanel(new FlowLayout());
		jcom.setBackground(Color.WHITE);
		jcom.add(new JLabel("sende über Account: "));
		jcom.add(comb);

		add(jcom,BorderLayout.NORTH);
		add(jgross,BorderLayout.CENTER);
		add(but,BorderLayout.SOUTH);
		validate();

	}
	private void senden(){
		String smtpHost = null,username=null,password=null,senderAddress=null;
		boolean smtpAuth = false;
		if(comb.getSelectedItem().equals(piTool.app.hmEmailIntern.get("SenderAdresse"))){
			username = piTool.hmEmailIntern.get("Username");
			password = piTool.hmEmailIntern.get("Password");
			senderAddress =piTool.hmEmailIntern.get("SenderAdresse");
			smtpHost = piTool.hmEmailIntern.get("SmtpHost");			
			smtpAuth = (piTool.hmEmailIntern.get("SmtpAuth").equals("1") ? true : false );
		}else{
			username = piTool.hmEmailExtern.get("Username");
			password = piTool.hmEmailExtern.get("Password");
			senderAddress =piTool.hmEmailExtern.get("SenderAdresse");
			smtpHost = piTool.hmEmailExtern.get("SmtpHost");			
			smtpAuth = (piTool.hmEmailExtern.get("SmtpAuth").equals("1") ? true : false );
		}
		
		ArrayList<String[]> attachments = new ArrayList<String[]>();
		String[] anhang = {piTool.proghome+"ScreenShots/email-shot.jpg","email-shot.jpg"};
		attachments.add(anhang);
		String recipientsAddress = tf.getText().trim();
		String subject = "ScreenShot-Email";
		String text = ta.getText();
		/*
		System.out.println("Username = "+username);
		System.out.println("Passwort = "+password);
		System.out.println("SenderAdress = "+senderAddress);
		System.out.println("smtpHost = "+smtpHost);
		System.out.println("smtpAuth = "+smtpAuth);
		System.out.println("Recipients = "+recipientsAddress);
		*/

		EmailSendenExtern oMail = new EmailSendenExtern();
		try{
		oMail.sendMail(smtpHost, username, password, senderAddress,
				recipientsAddress, subject, text,attachments,smtpAuth);
				JOptionPane.showMessageDialog (null, "Die ScreenShot-Email wurde aufbereitet versandt!\n");
		}catch(Exception e){
			e.printStackTrace( );
		}
		
		
		/*
		
		String[] anhang = {null,null};
		for(int i = 1; i <= DruckFenster.seiten;i++){
 			anhang[0] = SystemConfig.hmVerzeichnisse.get("Temp")+"Terminplan.pdf";
 			anhang[1] = "Terminplan.pdf";
           	System.out.println("In DruckenFenster - Files = "+anhang[1]);
			attachments.add(anhang.clone());
		}
		String username = SystemConfig.hmEmailExtern.get("Username");
		String password = SystemConfig.hmEmailExtern.get("Password");
		String senderAddress =SystemConfig.hmEmailExtern.get("SenderAdresse");
		System.out.println("Empfängeradresse = "+emailaddy);
		String recipientsAddress = emailaddy;
		String subject = "Ihre Behandlungstermine";
		String text = "";
		*/
		/*********/
		/*
    	  text = "Sehr geehrte Damen und Herren,\n"+
					"im Dateianhang finden Sie die von Ihnen gewünschten Behandlungstermine.\n\n"+
					"Termine die Sie nicht einhalten bzw. wahrnehmen können, müßen 24 Stunden vorher\n"+
					"abgesagt werden.\n\nIhr Planungs-Team vom RTA";
	      }
		String smtpHost = SystemConfig.hmEmailExtern.get("SmtpHost");
		
		EmailSendenExtern oMail = new EmailSendenExtern();
		try{
		oMail.sendMail(smtpHost, username, password, senderAddress, recipientsAddress, subject, text,attachments);
		}catch(Exception e){
			e.printStackTrace( );
		}
		JOptionPane.showMessageDialog (null, "Die Terminliste wurde aufbereitet und per Email versandt\n");
		*/
		this.setVisible(false);
	}
}
