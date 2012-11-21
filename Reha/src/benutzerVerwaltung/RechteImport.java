package benutzerVerwaltung;

import hauptFenster.Reha;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.MattePainter;

import CommonTools.JRtaCheckBox;
import CommonTools.JRtaComboBox;
import CommonTools.JRtaTextField;
import systemTools.ListenerTools;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;

public class RechteImport extends RehaSmartDialog implements RehaTPEventListener,WindowListener, ActionListener{


	/**
	 * 
	 */
	private static final long serialVersionUID = 357869906417195786L;

	public JRtaCheckBox[] leistung = {null,null,null,null,null}; 

	private RehaTPEventClass rtp = null;
	private GutachtenWahlHintergrund rgb;	
	
	public JButton uebernahme;
	public JButton abbrechen;
	
	JRtaTextField rechteImport = null;
	ButtonGroup bg = new ButtonGroup();
	JRadioButton[] rbut = {null,null};
	JButton[] but = {null,null};
	JRtaComboBox combo = null;
	
	MattePainter mp = null;
	LinearGradientPaint p = null;

	public RechteImport(Point pt, JRtaTextField xtf,String xtitel,Vector<Vector<String>> rechte){
		super(null,"RechteImportieren");		

		rechteImport = xtf;
		rechteImport.setText("");
		pinPanel = new PinPanel();
		pinPanel.setName("RechteImportieren");
		pinPanel.getGruen().setVisible(false);
		super.getPinPanel().setName("RechteImportieren");
		//setPinPanel(pinPanel);
		getSmartTitledPanel().setTitle(xtitel);

		setSize(300,180);
		setPreferredSize(new Dimension(250,180));
		getSmartTitledPanel().setPreferredSize(new Dimension (250,180));
		setPinPanel(pinPanel);
		rgb = new GutachtenWahlHintergrund();
		rgb.setLayout(new BorderLayout());

		
		new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {
			    rgb.setBackgroundPainter(Reha.thisClass.compoundPainter.get("GutachtenWahl"));
				return null;
			}
			
		}.execute();
 
		rgb.add(getImportRechte(rechte),BorderLayout.CENTER);
		rgb.revalidate();
		getSmartTitledPanel().setContentContainer(rgb);
		getSmartTitledPanel().getContentContainer().setName("GutachtenWahl");
		setName("GutachtenWahl");
		Point lpt = new Point(pt.x,pt.y-200);
	    setLocation(lpt);
	    
		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener((RehaTPEventListener) this);


		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run(){
		 		   if(! combo.hasFocus()){
		 			  combo.requestFocus();
		 		   }  
			   }
		}); 	   		
		pack();
		setModal(true);

	}
	public void setzeFocus(){
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run(){
		 		   if(! combo.hasFocus()){
			 		  combo.requestFocus();
		 		   }  
			   }
		}); 	   		
	}

	
/****************************************************/	


	private JPanel getImportRechte(Vector<Vector<String>> rechte){     // 1        2             3        4            5  
		FormLayout lay = new FormLayout("15dlu,fill:0:grow(0.50),120dlu,fill:0:grow(0.50),10dlu",
									//     1               2  3    4  5    6  7                 8     9   10  
										"20dlu,p,10dlu,p,15dlu,p,fill:0:grow(0.50),5dlu:g,p,20dlu");
		PanelBuilder pb = new PanelBuilder(lay);
		CellConstraints cc = new CellConstraints();

		pb.getPanel().setOpaque(false);
		
		combo = new JRtaComboBox();
		combo.setDataVectorVector(rechte, 0, 1);
		pb.add(combo,cc.xy(3,2));

		
		FormLayout lay2 = new FormLayout("fill:0:grow(0.33),60dlu,fill:0:grow(0.33),60dlu,fill:0:grow(0.33)",
				"p");
		PanelBuilder pb2 = new PanelBuilder(lay2);
		pb2.getPanel().setOpaque(false);
		CellConstraints cc2 = new CellConstraints();
		but[0] = new JButton("übernehmen");
		but[0].setName("uebernahme");
		but[0].addKeyListener(this);
		but[0].setActionCommand("uebernahme");
		but[0].addActionListener(this);
		but[1] = new JButton("abbrechen");
		but[1].setName("abbrechen");
		but[1].addKeyListener(this);
		but[1].setActionCommand("abbrechen");
		but[1].addActionListener(this);

		pb2.add(but[0],cc2.xy(2,1));
		pb2.add(but[1],cc2.xy(4,1));
		pb2.getPanel().validate();
		pb.add(pb2.getPanel(),cc.xyw(1, 9, 5));
		
		pb.getPanel().validate();
		return pb.getPanel();
	}
/****************************************************/	
	
	public void rehaTPEventOccurred(RehaTPEvent evt) {

		try{
			if(evt.getDetails()[0] != null){
				if(evt.getDetails()[0].equals(this.getName())){
					this.setVisible(false);
					rtp.removeRehaTPEventListener((RehaTPEventListener) this);
					rtp = null;
					ListenerTools.removeListeners(rbut[0]);
					ListenerTools.removeListeners(rbut[1]);
					ListenerTools.removeListeners(but[0]);
					ListenerTools.removeListeners(but[1]);
					super.dispose();
					this.dispose();
					//System.out.println("****************GutachtenWahl -> Listener entfernt**************");				
				}
			}
		}catch(NullPointerException ne){
			//System.out.println("In PatNeuanlage" +evt);
		}
	}
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		if(rtp != null){
			this.setVisible(false);			
			rtp.removeRehaTPEventListener((RehaTPEventListener) this);		
			rtp = null;
			ListenerTools.removeListeners(but[0]);
			ListenerTools.removeListeners(but[1]);
			super.dispose();
			dispose();
			//System.out.println("****************GutachtenWahl -> Listener entfernt (Closed)**********");
		}
		
		
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
		if(arg0.getActionCommand().equals("uebernahme")){
			//System.out.println("In Übernahme");
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					rechteImport.setText(combo.getValueAt(1).toString());
					dispose();
					return null;
				}
			}.execute();
			
			/********
			 * 
			 * Hier noch schnell buchen entwickeln und feddisch...
			 * 
			 */
		}
		if(arg0.getActionCommand().equals("abbrechen")){
			rechteImport.setText("");
			this.dispose();
		}

	}
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		if(event.getKeyCode()==38){
			//auf
			((Component) event.getSource()).transferFocusBackward();
			return;
		}
		if(event.getKeyCode()==40){
			//ab
			((Component) event.getSource()).transferFocus();
			return;
		}

		if(event.getKeyCode()==10){
			event.consume();
			if(((JComponent)event.getSource()).getName() == null){
				//System.out.println(event.getSource());
				return;
			}
			if( ((JComponent)event.getSource()).getName().equals("uebernahme")){
				rechteImport.setText(combo.getValueAt(1).toString());
				this.dispose();
			}
			if( ((JComponent)event.getSource()).getName().equals("abbrechen")){
				rechteImport.setText("");
				this.dispose();
			}
		}
	}

	
	
}
class GutachtenWahlHintergrund extends JXPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ImageIcon hgicon;
	int icx,icy;
	AlphaComposite xac1 = null;
	AlphaComposite xac2 = null;		
	public GutachtenWahlHintergrund(){
		super();
		hgicon = null; //new ImageIcon(Reha.proghome+"icons/geld.png");
		//icx = hgicon.getIconWidth()/2;
		//icy = hgicon.getIconHeight()/2;
		xac1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.15f); 
		xac2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f);			
		
	}
	@Override
	public void paintComponent( Graphics g ) { 
		super.paintComponent( g );
		Graphics2D g2d = (Graphics2D)g;
		
		if(hgicon != null){
			g2d.setComposite(this.xac1);
			g2d.drawImage(hgicon.getImage(), (getWidth()/2)-icx , (getHeight()/2)-icy,null);
			g2d.setComposite(this.xac2);
		}
	}
}