package patientenFenster;

import hauptFenster.Reha;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import patientenFenster.TherapieBerichte.MyBerichtTableModel;

import sqlTools.ExUndHop;
import systemEinstellungen.SystemConfig;
import systemTools.Colors;
import systemTools.JCompTools;
import systemTools.JRtaTextField;
import terminKalender.datFunk;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;
import ag.ion.noa.printing.IPrinter;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;

public class VorBerichte extends RehaSmartDialog implements RehaTPEventListener,WindowListener, ActionListener{
	boolean nurkopie;
	boolean aushistorie;
	public JButton uebernahme;
	public JButton abbrechen;
	public JRtaTextField gegeben;
	public JLabel rueckgeld;
	public JCheckBox diagnoseuebernahme;
	private RehaTPEventClass rtp = null;
	private VorBerichtHintergrund rgb;	
	private ArztBericht clazz;
	
	public JXTable tabbericht = null;
	public MyBerichtTableModel dtblm;
	CompoundPainter cp = null;
	MattePainter mp = null;
	LinearGradientPaint p = null;


	public VorBerichte(boolean kopie,boolean historie,Point pt,ArztBericht xclazz){
		super(null,"VorberichtLaden");		

		this.nurkopie = kopie;
		this.aushistorie = historie;
		this.clazz = xclazz;
		
		PinPanel pinPanel = new PinPanel();
		pinPanel.setName("VorberichtLaden");
		pinPanel.getGruen().setVisible(false);
		setPinPanel(pinPanel);
		getSmartTitledPanel().setTitle("Text aus Vorbericht laden");	
		setSize(750,290);
		setPreferredSize(new Dimension(750,290));
		getSmartTitledPanel().setPreferredSize(new Dimension (750,290));
		setPinPanel(pinPanel);
		rgb = new VorBerichtHintergrund();
		rgb.setLayout(new BorderLayout());

		
		new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				// TODO Auto-generated method stub
				Point2D start = new Point2D.Float(0, 0);
			     Point2D end = new Point2D.Float(PatGrundPanel.thisClass.getWidth(),100);
			     float[] dist = {0.0f, 0.75f};
			     Color[] colors = {Color.WHITE,Colors.Yellow.alpha(0.05f)};
			     p =
			         new LinearGradientPaint(start, end, dist, colors);
			     mp = new MattePainter(p);
			     cp = new CompoundPainter(mp);
			     rgb.setBackgroundPainter(cp);		
				return null;
			}
			
		}.execute();	
		rgb.add(getGebuehren(),BorderLayout.CENTER);
		
		getSmartTitledPanel().setContentContainer(rgb);
		getSmartTitledPanel().getContentContainer().setName("VorberichtLaden");
	    setName("VorberichtLaden");
		setModal(true);
	    //Point lpt = new Point(pt.x-125,pt.y+30);
	    setLocation(pt);
	    
		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener((RehaTPEventListener) this);

		pack();
		
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run()
		 	   {
		 		 setzeFocus();
		 	   }
		});
				
	    


	}
	
	private void setzeFocus(){
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run()
		 	   {
		 			//gegeben.requestFocus();		 		   
		 	   }
		});
	}
/****************************************************/	
	/*
	public JButton okknopf;
	public JRtaTextField gegeben;
	public JLabel rueckgeld;
	public JCheckBox direktdruck;
	*/

	private JPanel getGebuehren(){     // 1      2    3     4       5               6                7
		FormLayout lay = new FormLayout("10dlu,80dlu,10dlu,80dlu,fill:0:grow(1.00),10dlu",
									//     1   2  3    4       5   6  7    
										"15dlu,p,2dlu,100dlu,15dlu,p,15dlu");
		PanelBuilder pb = new PanelBuilder(lay);
		CellConstraints cc = new CellConstraints();

		pb.getPanel().setOpaque(false);
		
		diagnoseuebernahme = new JCheckBox("Diagnose ebenfalls übernehmen");
		diagnoseuebernahme.setOpaque(false);
		diagnoseuebernahme.setSelected(false);	
		

		pb.add(diagnoseuebernahme,cc.xyw(2,2,3));

		dtblm = TherapieBerichte.aktBericht.dtblm;
		tabbericht = new JXTable(dtblm);
		tabbericht.getColumn(1).setMinWidth(200);
		tabbericht.getColumn(6).setMinWidth(0);
		tabbericht.getColumn(6).setMaxWidth(0);
		tabbericht.setRowSelectionInterval(0, 0);
		tabbericht.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getClickCount()==2){
					doUebernahme();
				}
			}
		});
		tabbericht.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode()==10){
					doUebernahme();
				}
			}
		});
		
		
		JScrollPane jscr = JCompTools.getTransparentScrollPane(tabbericht);
		jscr.validate();
		pb.add(jscr,cc.xyw(2,4,4));
		
		uebernahme = new JButton("Text übernehmen");
		uebernahme.setActionCommand("uebernahme");
		uebernahme.addActionListener(this);
		uebernahme.addKeyListener(this);
		pb.add(uebernahme,cc.xy(2,6));
		
		abbrechen = new JButton("abbrechen");
		abbrechen.setActionCommand("abbrechen");
		abbrechen.addActionListener(this);
		abbrechen.addKeyListener(this);		
		pb.add(abbrechen,cc.xy(4,6));
		
		pb.getPanel().validate();
		return pb.getPanel();
	}
/****************************************************/	
	
	public void rehaTPEventOccurred(RehaTPEvent evt) {
		// TODO Auto-generated method stub
		try{
			if(evt.getDetails()[0] != null){
				if(evt.getDetails()[0].equals(this.getName())){
					this.setVisible(false);
					rtp.removeRehaTPEventListener((RehaTPEventListener) this);
					rtp = null;
					super.dispose();
					this.dispose();
					System.out.println("****************Rezeptgebühren -> Listener entfernt**************");				
				}
			}
		}catch(NullPointerException ne){
			System.out.println("In PatNeuanlage" +evt);
		}
	}
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		if(rtp != null){
			this.setVisible(false);			
			rtp.removeRehaTPEventListener((RehaTPEventListener) this);		
			rtp = null;
			super.dispose();
			dispose();
			System.out.println("****************Rezeptgebühren -> Listener entfernt (Closed)**********");
		}
		
		
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getActionCommand().equals("uebernahme")){
			doUebernahme();
		}
		if(arg0.getActionCommand().equals("abbrechen")){
			this.dispose();
		}

	}
	
	public void keyPressed(KeyEvent event) {
		if(event.getKeyCode()==10){
			event.consume();
			if( ((JComponent)event.getSource()).getName().equals("uebernahme")){
				doUebernahme();
			}
			if( ((JComponent)event.getSource()).getName().equals("abbrechen")){
				this.dispose();
			}

			System.out.println("Return Gedrückt");
		}
	}
	public void doUebernahme(){
		int row = tabbericht.getSelectedRow();
		int altid = -1;
		if(row >=0){
			altid = new Integer((String)tabbericht.getValueAt(row,0));
			clazz.vorberichtid = altid;
			clazz.vorberichtdiagnose = diagnoseuebernahme.isSelected();
		}
		this.dispose();
	}
	
}
class VorBerichtHintergrund extends JXPanel{
	ImageIcon hgicon;
	int icx,icy;
	AlphaComposite xac1 = null;
	AlphaComposite xac2 = null;		
	public VorBerichtHintergrund(){
		super();
		hgicon = new ImageIcon(Reha.proghome+"icons/geld.png");
		icx = hgicon.getIconWidth()/2;
		icy = hgicon.getIconHeight()/2;
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