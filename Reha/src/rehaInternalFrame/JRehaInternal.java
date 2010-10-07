package rehaInternalFrame;





import hauptFenster.AktiveFenster;
import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyVetoException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.border.DropShadowBorder;

import systemEinstellungen.SystemConfig;
import systemTools.ListenerTools;

import com.jgoodies.looks.plastic.PlasticInternalFrameUI;

import events.RehaEvent;
import events.RehaEventClass;

public class JRehaInternal extends JInternalFrame implements ActionListener,ComponentListener,KeyListener,MouseListener,MouseMotionListener,InternalFrameListener,AncestorListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1404891339833332337L;
	/**
	 * 
	 */
	public String titel;
	public JTextField tf = null;
	public boolean isActive;
	public JXPanel thisContent;
	public int desktop;
	public int compOrder;
	private boolean stetsgross;
	public JComponent inhalt;
	public FocusListener fl = null;
	public int xWeit = -1;
	public int yHoch = -1;
	public JComponent nord = null;
	public boolean isIcon = false; 
	public boolean doNotClose = false;
	public JRehaInternal(String titel,ImageIcon img,int desktop){
		super();
		this.setBackground(Color.WHITE);
		this.setTitle(titel);
		this.titel = titel;
		this.desktop = desktop;
		this.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
		this.getContentPane().setSize(new Dimension(this.getContentPane().getWidth(),this.getContentPane().getHeight()-20));
		this.getContentPane().addMouseListener(this);
		this.getContentPane().addFocusListener(getFocusListener());
		this.setResizable(true);
		this.setFrameIcon(img);
		this.setIconifiable(true);
		this.setClosable(true);
		this.addComponentListener(this);
		this.addInternalFrameListener(this);

		
		thisContent = new JXPanel(new BorderLayout());
		thisContent.setBorder(null);
		thisContent.setOpaque(false);
		thisContent.addKeyListener(this);
		thisContent.addMouseListener(this);
		thisContent.addAncestorListener(this);
		thisContent.addMouseMotionListener(this);
		thisContent.addComponentListener(this);
		thisContent.addFocusListener(fl);
		final String xtitel = titel;
		this.setUI(new PlasticInternalFrameUI(this){
			@Override
			protected JComponent createNorthPane(JInternalFrame w) {
				nord = new RehaInternal(w,xtitel,SystemConfig.hmSysIcons.get("rot"),
						SystemConfig.hmSysIcons.get("gruen"),SystemConfig.hmSysIcons.get("inaktiv"),0);
				return nord;
			}
		});
		
		DropShadowBorder dropShadow = new DropShadowBorder(Color.BLACK, 3, 0.7f, 7, false, true, true, true);

		this.getContentPane().add(thisContent);
		this.setBorder(dropShadow);
	}
	public void setDesktop(int desktop){
		this.desktop = desktop;
	}
	public int getDesktop(){
		return this.desktop;
	}

	public void setContent(JComponent jpan){
		this.inhalt = jpan;
		this.thisContent.add(jpan);
	}
	public void setContent(JScrollPane jpan){
		this.inhalt = jpan;
		this.thisContent.add(jpan);
	}
	public void setContent(JXPanel jpan){
		this.inhalt = jpan;
		this.thisContent.add(jpan);
	}
	public  JXPanel getContent(){
		return (JXPanel)this.thisContent;
	}
	public  JComponent getComponent(){
		return (JComponent)this.thisContent;
	}
	public  JComponent getInhalt(){
		return (JComponent)this.inhalt;
	}

	public void setzeTitel(String titel){
		this.titel = titel;
		this.setTitle(this.titel);
		repaint();
	}
	public void setDoNotClose(boolean canClose){
		this.doNotClose = canClose;
	}
	

	public void gruenGedrueckt(){
		xWeit = this.getSize().width;
		yHoch = this.getSize().height;
		RehaEvent evt = new RehaEvent(this);
		evt.setDetails(this.getName(), "#ICONIFIED");
		evt.setRehaEvent("REHAINTERNAL");
		RehaEventClass.fireRehaEvent(evt);
		isIcon = true;
	}
	
	public void rotGedrueckt(){
		if(doNotClose){
			return;
		}
		this.dispose();
	}
	public boolean getActive(){
		return isActive;
	}
	public void setActive(boolean active){
		this.isActive = active;
	}
	
	public void setSpecialActive(boolean active){
	}

	public String getTitel(){
		return this.titel;
	}
	
	@Override
	public void setTitle(String titel){
		this.titel = titel;
		this.title = titel;
		this.repaint();
	}

	public void destroyTitleBar(){
		int comp = nord.getComponentCount();
		Component icomp;
		for(int i = 0 ; i < comp ; i ++){
			icomp = nord.getComponent(i);
			if(icomp != null){
				ListenerTools.removeListeners(icomp);
				icomp = null;	
			}
		}
		ListenerTools.removeListeners(thisContent);
		comp = thisContent.getComponentCount();
		for(int i = 0 ; i < comp ; i ++){
			icomp = thisContent.getComponent(i);
			if(icomp != null){
				ListenerTools.removeListeners(icomp);
				icomp = null;	
			}
		}
		ListenerTools.removeListeners(inhalt);
		comp = inhalt.getComponentCount();
		for(int i = 0 ; i < comp ; i ++){
			icomp = inhalt.getComponent(i);
			if(icomp != null){
				ListenerTools.removeListeners(icomp);
				icomp = null;	
			}
		}
	}

	public void setTitel(String titel){
		this.titel = titel;
		this.repaint();
	}

	public FocusListener getFocusListener(){
		fl = new FocusListener(){
			@Override
			public void focusGained(FocusEvent e) {
				if (inhalt != null){
					inhalt.requestFocus();
					setActive(true);
					frameAktivieren(getName());
					repaint();
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
			}
			
		};
		return fl;
	}


	public void focusGained(FocusEvent arg0) {
		if (this.inhalt != null){
			this.inhalt.requestFocus();
			setActive(true);			
		}
	}

	public void focusLost(FocusEvent arg0) {
	}
	
	public void aktiviereDiesenFrame(String frame){
		JRehaInternal iframe = null;
		for(int idesk = 0; idesk < 2;idesk++){
			int coms = Reha.thisClass.desktops[idesk].getComponentCount();
			String lays = "";
			for(int layers = 0;layers < coms;layers++){
				try{
					try{
					
					lays = ((JRehaInternal)Reha.thisClass.desktops[idesk].getComponent(layers)).getName();
					}catch(java.lang.ClassCastException ex){
						lays = frame;
					}
					if(!lays.equals(frame)){
						((JRehaInternal)Reha.thisClass.desktops[idesk].getComponent(layers)).setActive(false);
						try {
							((JRehaInternal)Reha.thisClass.desktops[idesk].getComponent(layers)).setSelected(false);
						} catch (PropertyVetoException e) {
							e.printStackTrace();
						}
						((JRehaInternal)Reha.thisClass.desktops[idesk].getComponent(layers)).repaint();
					}else{
						try{
							iframe = ((JRehaInternal)Reha.thisClass.desktops[idesk].getComponent(layers));
						}catch(Exception ex){
							// Hier muß der Kram mit iconified
							if(Reha.thisClass.desktops[idesk].getComponent(layers) instanceof JDesktopIcon){
								
							}else{
								((JRehaInternal)Reha.thisClass.desktops[idesk].getComponent(layers)).isIcon = false;
								System.out.println("icon = false "+((JRehaInternal)Reha.thisClass.desktops[idesk].getComponent(layers)).getName());
								iframe = ((JRehaInternal)Reha.thisClass.desktops[idesk].getComponent(layers));
							}
						}
					}

				}catch(java.lang.ArrayIndexOutOfBoundsException ex){
					ex.printStackTrace();
				}
			}
			
		}
		if(iframe != null){
			////System.out.println("Name des aufgerufenen iFrames = "+iframe.getName());
			iframe.setActive(true);
			final JRehaInternal xiframe = iframe;
			SwingUtilities.invokeLater(new Runnable(){
	  		 	   public  void run()
	  		 	   {	
	  					try {
	  						xiframe.setSelected(true);
	  						xiframe.toFront();
	  					} catch (PropertyVetoException e) {
	  						// TODO Auto-generated catch block
	  						e.printStackTrace();
	  					}

	  		 		   xiframe.repaint();
	  		 	   }
	  			});
		}
	}
	@Override
	public void internalFrameActivated(InternalFrameEvent arg0) {
		
		if(isSelected() && (!isActive)){
			aktiviereDiesenFrame(getName());
		}
	}
	@Override
	public void internalFrameClosed(InternalFrameEvent arg0) {
		Reha.thisClass.desktops[this.desktop].remove(this);
		this.removeInternalFrameListener(this);
		thisContent.removeKeyListener(this);
		thisContent.removeMouseListener(this);
		thisContent.removeAncestorListener(this);
		thisContent.removeMouseMotionListener(this);
		thisContent.removeComponentListener(this);
		thisContent.removeFocusListener(fl);
		fl = null;
	    thisContent = null;
		Reha.thisFrame.requestFocus();
		Reha.thisClass.aktiviereNaechsten(this.desktop);
		AktiveFenster.loescheFenster(this.getName());
	}
	@Override
	public void internalFrameClosing(InternalFrameEvent arg0) {
	}
	@Override
	public void internalFrameDeactivated(InternalFrameEvent arg0) {
		isActive = false;
		this.repaint();
	}
	@Override
	public void internalFrameDeiconified(InternalFrameEvent arg0) {
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				RehaEvent evt = new RehaEvent(this);
				evt.setDetails(getName(), "#DEICONIFIED");
				evt.setRehaEvent("REHAINTERNAL");
				RehaEventClass.fireRehaEvent(evt);
				setActive(true);
				repaint();
				return null;
			}
		}.execute();
	}
	@Override
	public void internalFrameIconified(InternalFrameEvent arg0)	{
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				RehaEvent evt = new RehaEvent(this);
				evt.setDetails(getName(), "#ICONIFIED");
				evt.setRehaEvent("REHAINTERNAL");
				RehaEventClass.fireRehaEvent(evt);
				titel = getTitle();
				setActive(false);
				repaint();
				return null;
			}
		}.execute();
	}
	@Override
	public void internalFrameOpened(InternalFrameEvent arg0) {
	}
	@Override
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode()==27){
			this.dispose();
		}
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
		if(arg0.getKeyCode()==27){
			this.dispose();
		}
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		if(arg0.getKeyCode()==27){
			this.dispose();
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if(arg0.getClickCount()==1 && !isActive){
			feuereEvent(25554);
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
	@Override
	public void ancestorAdded(AncestorEvent arg0) {
	}
	@Override
	public void ancestorMoved(AncestorEvent arg0) {
	}
	@Override
	public void ancestorRemoved(AncestorEvent arg0) {
	}
	@Override
	public void mouseDragged(MouseEvent arg0) {
	}
	@Override
	public void mouseMoved(MouseEvent arg0) {
	}
	@Override
	public void componentHidden(ComponentEvent arg0) {
	}
	@Override
	public void componentMoved(ComponentEvent arg0) {
	}
	@Override
	public void componentResized(ComponentEvent arg0) {
	}
	@Override
	public void componentShown(ComponentEvent arg0) {
	}
	public void setCompOrder(int com){
		this.compOrder = com;
	}
	public void setzeIcon(){
		if(this.isIcon){
		}
	}
	public void showPopUp(java.awt.event.MouseEvent evt){
		////System.out.println("Aufruf des PopUpMenues an der Stelle X="+evt.getX()+" / Y="+evt.getY());
		JPopupMenu jPop = getTerminPopupMenu();
		jPop.show( evt.getComponent(), evt.getX(), evt.getY() ); 
	}
	private JPopupMenu getTerminPopupMenu() {
		JPopupMenu jPopupMenu = new JPopupMenu();
		JMenuItem  jmen = new JMenuItem();
		jmen.setText((this.desktop==0 ? "in den unteren Container verschieben" : "in den oberen Container verschieben"));
		jmen.setIcon((this.desktop==0 ? new ImageIcon(Reha.proghome+"/icons/unten.gif") :
			new ImageIcon(Reha.proghome+"/icons/oben.gif")));
		jmen.setRolloverEnabled(true);
		jmen.setEnabled(true);
		jmen.setActionCommand("verschieben");
		jmen.addActionListener(this);
		jPopupMenu.add(jmen);

		jPopupMenu.addSeparator();
		jmen = new JMenuItem();
		jmen.setText("jetzt(!) Fenster auf verfügbare Größe anpassen");
		jmen.setEnabled(true);
		jmen.setRolloverEnabled(true);
		jmen.setActionCommand("anpassen");
		jmen.addActionListener(this);
		jPopupMenu.add(jmen);
		jPopupMenu.addSeparator();

		JCheckBoxMenuItem cbMenuItem = new JCheckBoxMenuItem("stets versuchen das Fenster auf maximale Größe einstellen");
		cbMenuItem.setSelected((stetsgross ? true : false));
		cbMenuItem.setActionCommand("stetsgross");
		cbMenuItem.addActionListener(this);		
		cbMenuItem.setEnabled(true);
		cbMenuItem.setRolloverEnabled(true);
		jPopupMenu.add(cbMenuItem);

		cbMenuItem = new JCheckBoxMenuItem("das Fenster immer so lassen wie es (nunmal) ist...");
		cbMenuItem.setSelected((!stetsgross ? true : false));
		cbMenuItem.setActionCommand("stetslassen");
		cbMenuItem.addActionListener(this);
		cbMenuItem.setEnabled(true);
		cbMenuItem.setRolloverEnabled(true);
		jPopupMenu.add(cbMenuItem);
		
		jPopupMenu.addSeparator();
		
		jmen = new JMenuItem();
		jmen.setText("Fenster zu Icon verkleinern");
		jmen.setIcon(new ImageIcon(Reha.proghome+"/icons/buttongreen.png"));
		jmen.setEnabled(true);
		jmen.setActionCommand("icon");
		jmen.addActionListener(this);
		jPopupMenu.add(jmen);

		jmen = new JMenuItem();
		jmen.setText("Fenster schließen");
		jmen.setIcon(new ImageIcon(Reha.proghome+"/icons/buttonred.png"));
		jmen.setEnabled(true);
		jmen.setActionCommand("schliessen");
		jmen.addActionListener(this);
		jPopupMenu.add(jmen);

		return jPopupMenu;
	}
	private void testeWechsel(){
		int vorher=0,nachher=0;
//		for(int i = 0;i<1;i++){
		if(this.desktop==0){
			vorher = 0;
			nachher = 1;
//			break;
		}else{
			vorher = 1;
			nachher = 0;
//			break;
		}
//		}
		this.removeInternalFrameListener(this);

		this.setVisible(false);
		////System.out.println("Layer vor dem Wechsel" +Reha.thisClass.desktops[vorher].getLayer(this));		
		Reha.thisClass.desktops[vorher].remove(this);
		((JDesktopPane)Reha.thisClass.desktops[vorher]).updateUI();
		Reha.thisClass.desktops[vorher].repaint();
		this.setVisible(true);
		Reha.thisClass.desktops[nachher].add(this);

		this.setLocation(20,10);
		Reha.thisClass.desktops[nachher].validate();		
		((JDesktopPane)Reha.thisClass.desktops[nachher]).updateUI();
		Reha.thisClass.desktops[nachher].repaint();
		this.desktop = nachher;
		this.addInternalFrameListener(this);
		try {
			this.toFront();
			this.setSelected(true);
			this.isActive = true;
			this.validateTree();
		} catch (PropertyVetoException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		frameAktivieren(this.getName());
		////System.out.println("Layer nach dem Wechsel" +Reha.thisClass.desktops[nachher].getLayer(this));		
		/*
		try {
			setSelected(true);
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		String comm = arg0.getActionCommand();

		for(int i = 0; i < 1; i++){
			if(comm.equals("stetsgross")){
				this.stetsgross=true;
				break;
			}
			if(comm.equals("stetslassen")){
				this.stetsgross=false;
				break;
			}
			if(comm.equals("anpassen")){
				sizeAnpassen();
				break;
			}
			if(comm.equals("verschieben")){
				testeWechsel();
				break;
			}
			if(comm.equals("icon")){
				gruenGedrueckt();
				break;
			}
			if(comm.equals("schliessen")){
				rotGedrueckt();
				break;
			}
		}
		
	}
	private void sizeAnpassen(){
		this.setLocation(new Point(2,2));
		this.setSize(Reha.thisClass.desktops[this.desktop].getWidth()-2,Reha.thisClass.desktops[this.desktop].getHeight()-2);
	}
	public boolean getImmerGross(){
		return this.stetsgross;
	}
	public void setImmerGross(boolean gross){
		this.stetsgross = gross;
	}
	public void feuereEvent(int event){
		this.fireInternalFrameEvent(event);
	}
	public void frameAktivieren(String xname){

				int deskaktiv=this.desktop;
				int deskinaktiv = (deskaktiv==0 ? 1 : 0);
				JInternalFrame[] frm = Reha.thisClass.desktops[deskaktiv].getAllFrames();
				////System.out.println("Anzahl Fenster auf Desktop "+deskaktiv+" = "+frm.length);
				
				for(int i = 0; i < frm.length;i++){
						if(((JRehaInternal)frm[i]).getName().equals(xname)){
							////System.out.println("In den Vordergrund "+xname);
							//((JRehaInternal)frm[i]).fireInternalFrameEvent(25554);
						}else{
							////System.out.println("In den Hintergrund "+xname);
							////System.out.println("In den Hintergrund "+((JRehaInternal)frm[i]).getName());							
							((JRehaInternal)frm[i]).fireInternalFrameEvent(25555);
							((JRehaInternal)frm[i]).validateTree();
						}
				}
				
				JInternalFrame[] frmin = Reha.thisClass.desktops[deskinaktiv].getAllFrames();
				////System.out.println("Anzahl Fenster auf Desktop "+deskinaktiv+" = "+frmin.length);				
				for(int i = 0; i < frmin.length;i++){
					
					if(!((JRehaInternal)frmin[i]).getName().equals(xname)){
						////System.out.println("Inaktiver Desktop Hintergrund -> "+((JRehaInternal)frmin[i]).getName());
						((JRehaInternal)frmin[i]).fireInternalFrameEvent(25555);
						((JRehaInternal)frmin[i]).validateTree();
						//((JRehaInternal)frmin[i]).repaint();
					}
				}
			
		
	}

}	
/********Neuer Title
 * 
 * @author admin
 *
 */

class RehaInternal extends BasicInternalFrameTitlePane{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8563410987754087430L;
	String titel = null;
	Image img1 = null;
	Image img2 = null;
	Image img3 = null;
	int pp = 0;
	CustomPinPanel jb1 = null;
	CustomPinPanel jb2 = null;
	Paint gp1 = null;
	Paint gp2 = null;	
	public RehaInternal(JInternalFrame f,String titel,ImageIcon img1,ImageIcon img2,ImageIcon img3,int pp) {
	
		super(f);
		gp1 = Reha.thisClass.gp1;//new GradientPaint(0,0,new Color(112,141,255),0,25,Color.WHITE,true);	
		gp2 = Reha.thisClass.gp1;//new GradientPaint(0,0,new Color(112,141,120),0,25,Color.WHITE,true);
		this.titel = titel;
		this.img1 = img1.getImage();
		this.img2 = img2.getImage();
		this.img3 = img3.getImage();
		this.pp = pp;
		addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
            	if(evt.getButton()==MouseEvent.BUTTON3){
                    ((JRehaInternal)getParent()).showPopUp(evt);
                    ((JRehaInternal)getParent()).feuereEvent(25554);
            	}
            	if(evt.getButton()==MouseEvent.BUTTON1){
                    ((JRehaInternal)getParent()).feuereEvent(25554);
            	}

            }
        });
		setPreferredSize(new Dimension(0,25));
		setLayout(new FlowLayout(FlowLayout.RIGHT));

		jb1 = new CustomPinPanel("",null,1);
		jb1.setName("GRUEN");
		jb1.setVisible(true);
		add(jb1);

		
		jb2 = new CustomPinPanel("",null,1);
		jb2.setName("ROT");
		jb2.setVisible(true);
		add(jb2);

	}

	public void paintComponent(Graphics g){
		setOpaque(false);
		Graphics2D g2d = (Graphics2D) g;
		
		int comp = this.getComponentCount();

		for(int i = 0 ; i < comp ; i ++){
			if(i < 4){
				this.getComponent(i).setVisible(false);
			}else{
				//this.getComponent(i).createImage(img1.getSource());
			}
			
		}
		Paint newPaint = null;
		if(((JRehaInternal)getParent()).getActive()){
			newPaint = new GradientPaint(0,0,new Color(112,141,255),0,getHeight(),Color.WHITE,true);			
		}else{
			newPaint = new GradientPaint(0,0,new Color(128,128,128),0,getHeight(),Color.WHITE,true);			
		}
			

		g2d.setPaint(newPaint);
		g2d.fillRect(0,0,getWidth(),getHeight());
		g2d.setColor(Color.WHITE);
		
		g2d.drawString(((JRehaInternal)getParent()).getTitle(), 5, 15);
		int x = this.getWidth()-21;
		if(this.img1 != null){
			if(((JRehaInternal)getParent()).doNotClose){
				g2d.drawImage((Image)this.img3, x, 5, this);	
			}else{
				g2d.drawImage((Image)this.img1, x, 5, this);
			}
			
			if(((JRehaInternal)getParent()).getActive()){
				g2d.drawImage((Image)this.img2, x-20, 5, this);
			}else{
				g2d.drawImage((Image)this.img3, x-20, 5, this);				
			}
		}

	}
}
/*********************************************************/
class CustomPinPanel extends JButton{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9078107053879258413L;
	CustomPinPanel(String titel,ImageIcon img,int position){
		super(titel);
		setBorder(null);
		setOpaque(false);
		if(img != null){
			setIcon(img);
		}
		setPreferredSize(new Dimension(15,15));
		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				////System.out.println("Action ausgel�st von: "+((JComponent)evt.getSource()).getName());
				if(((JComponent)evt.getSource()).getName().equals("GRUEN")){
					((JRehaInternal)getParent().getParent()).gruenGedrueckt();
					((JRehaInternal)getParent().getParent()).isIcon = true;
				}
				if(((JComponent)evt.getSource()).getName().equals("ROT")){
					if(! ((JRehaInternal)getParent().getParent()).doNotClose){
						((JRehaInternal)getParent().getParent()).rotGedrueckt();	
					}
					
				}

			}
		});
	}
	public void paintComponent(Graphics g)
	{
	@SuppressWarnings("unused")
	Graphics2D g2d = (Graphics2D) g;
		//g2d.drawImage(getToolkit().getImage("C:/RehaVerwaltung/icons/red.png"),0 ,20, this);
		//g2d.drawImage(getToolkit().getImage("C:/RehaVerwaltung/icons/red.png"),40 ,20, this);		
	}
}