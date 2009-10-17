package entlassBerichte;

import hauptFenster.Reha;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


import systemEinstellungen.SystemConfig;
import systemTools.Colors;
import systemTools.JRtaCheckBox;

public class BerichtDrucken extends JXPanel implements ActionListener, KeyListener,FocusListener  {
	ImageIcon hgicon;	
	int icx,icy;
	AlphaComposite xac1 = null;
	AlphaComposite xac2 = null;	
	JRtaCheckBox[] checks =	{null,null,null,null,null,null};
	JButton drucken = null;
	EBerichtPanel eltern = null;
	Font fon = new Font("Tahoma",Font.BOLD,11);
	int[] exemplare = {-1,-1,-1,-1,-1,0};
	public BerichtDrucken(EBerichtPanel xeltern, int[] check, boolean[] enable){
		super();
		setLayout(new BorderLayout());
		
		new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				// TODO Auto-generated method stub
				Point2D start = new Point2D.Float(0, 0);
			    Point2D end = new Point2D.Float(300,270);
			    float[] dist = {0.0f, 0.75f};
			    Color[] colors = {Color.WHITE,Colors.Gray.alpha(0.15f)};
			    LinearGradientPaint p =  new LinearGradientPaint(start, end, dist, colors);
			    MattePainter mp = new MattePainter(p);
			    setBackgroundPainter(new CompoundPainter(mp));
				return null;
			}
			
		}.execute();

		hgicon = new ImageIcon(Reha.proghome+"icons/document-print.png"); //SystemConfig.hmSysIcons.get("print");
		icx = hgicon.getIconWidth()/2;
		icy = hgicon.getIconHeight()/2;
		xac1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.17f); 
		xac2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f);	
		add(getForm(),BorderLayout.CENTER);
		addKeyListener(this);

		eltern = xeltern;


		for(int i = 0 ; i < 5; i++){
			checks[i].addKeyListener(this);
			checks[i].setFont(fon);
			if(i < 4){
				checks[i].setForeground(Color.BLUE);				
			}else{
				checks[i].setForeground(Color.RED);
			}

			if(enable[i]){
				checks[i].setSelected( (check[i] > 0  ? true : false) );				
			}else{
				checks[i].setEnabled(false);
			}
			exemplare[i] = new Integer(eltern.druckversion[i]);
			eltern.druckversion[i] = -1;;

		}
		checks[5].setForeground(Color.RED);
		checks[5].addKeyListener(this);
		checks[5].setFont(fon);
		eltern.druckversion[5] = -1;;
		checks[0].requestFocus();
		
		validate();

	}
	public JPanel getForm(){
		FormLayout lay = new FormLayout("fill:0:grow(0.50),p,fill:0:grow(0.5)",
				//  1              2   3   4  5  6   7  8   9  10  11  12     
				"fill:0:grow(0.33),5dlu,p,5dlu,p,5dlu,p,5dlu,p,5dlu,p,5dlu," +
				//13 14     15            16    17
				" p,5dlu,fill:0:grow(0.33),p,fill:0:grow(0.33)");
		PanelBuilder pb = new PanelBuilder(lay);
		pb.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		checks[0] = new JRtaCheckBox("Deckblatt (Seite 1)");
		pb.add(checks[0],cc.xy(2,3));
		checks[1] = new JRtaCheckBox("Sozialmedizin (Seite 2)");
		pb.add(checks[1],cc.xy(2,5));
		checks[2] = new JRtaCheckBox("KTL-Seiten ( 3 bzw. 3+4)");
		pb.add(checks[2],cc.xy(2,7));
		checks[3] = new JRtaCheckBox("Freitext (4 bzw. 5 ff)");
		pb.add(checks[3],cc.xy(2,9));
		checks[4] = new JRtaCheckBox("Alle Exemplare erstellen!!");
		pb.add(checks[4],cc.xy(2,11));
		checks[5] = new JRtaCheckBox("Im PDF-Reader öffnen");
		pb.add(checks[5],cc.xy(2,13));
		
		drucken = new JButton("Druck starten");
		drucken.setActionCommand("drucken");
		drucken.setName("drucken");
		drucken.addActionListener(this);
		drucken.addKeyListener(this);
		pb.add(drucken,cc.xy(2,16));
		
		return pb.getPanel();
	}
	
	@Override
	public void paintComponent( Graphics g ) { 
		super.paintComponent( g );
		Graphics2D g2d = (Graphics2D)g;
		
		if(hgicon != null){
			g2d.setComposite(this.xac1);
			g2d.drawImage(hgicon.getImage(), (getWidth()/2)-icx , (getHeight()/2)-icy-(30),null);
			g2d.setComposite(this.xac2);
		}
	}	

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String cmd = arg0.getActionCommand();
		if(cmd.equals("drucken")){
			doDrucken();
		}
		
	}
	private void doDrucken(){
		for(int i = 0; i < 6; i++){
			eltern.druckversion[i] = (checks[i].isSelected() ? 1 : 0);
			((JXDialog)this.getParent().getParent().getParent().getParent().getParent()).setVisible(false);
			((JXDialog)this.getParent().getParent().getParent().getParent().getParent()).dispose();
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {

		if(arg0.getKeyCode()== 10 || arg0.getKeyCode()==0){
			arg0.consume();
			String name = ((JComponent)arg0.getSource()).getName(); 
			if( name != null){
				doDrucken();
			}
			System.out.println("Return gedrückt");
		}
		if(arg0.getKeyCode()==27){
			arg0.consume();
			System.out.println("ESC gedrückt");
			((JXDialog)this.getParent().getParent().getParent().getParent().getParent()).setVisible(false);
			((JXDialog)this.getParent().getParent().getParent().getParent().getParent()).dispose();
			return;
		}
		

		
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
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
