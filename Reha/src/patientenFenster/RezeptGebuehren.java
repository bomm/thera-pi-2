package patientenFenster;

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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;
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
import hauptFenster.Reha;
import sqlTools.ExUndHop;
import systemEinstellungen.SystemConfig;
import systemTools.Colors;
import systemTools.JRtaTextField;
import terminKalender.datFunk;

public class RezeptGebuehren extends RehaSmartDialog implements RehaTPEventListener,WindowListener, ActionListener{
	boolean nurkopie;
	boolean aushistorie;
	public JButton okknopf;
	public JRtaTextField gegeben;
	public JLabel rueckgeld;
	public JCheckBox direktdruck;
	private RehaTPEventClass rtp = null;
	private RgebHintergrund rgb;	
	public RezeptGebuehren(boolean kopie,boolean historie,Point pt){
		super(null,"RezeptGebuehr");		

		this.nurkopie = kopie;
		this.aushistorie = historie;
		
		PinPanel pinPanel = new PinPanel();
		pinPanel.setName("RezeptGebuehr");
		pinPanel.getGruen().setVisible(false);
		setPinPanel(pinPanel);
		getSmartTitledPanel().setTitle("Rezept-Gebühr");	
		setSize(175,250);
		setPreferredSize(new Dimension(175,250));
		getSmartTitledPanel().setPreferredSize(new Dimension (175,250));
		setPinPanel(pinPanel);
		rgb = new RgebHintergrund();
		rgb.setLayout(new BorderLayout());
		new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				// TODO Auto-generated method stub
				Point2D start = new Point2D.Float(0, 0);
			     Point2D end = new Point2D.Float(PatGrundPanel.thisClass.getWidth(),100);
			     float[] dist = {0.0f, 0.75f};
			     Color[] colors = {Color.WHITE,Colors.Yellow.alpha(0.05f)};
			     LinearGradientPaint p =
			         new LinearGradientPaint(start, end, dist, colors);
			     MattePainter mp = new MattePainter(p);
			     rgb.setBackgroundPainter(new CompoundPainter(mp));		
				return null;
			}
			
		}.execute();	
		rgb.add(getGebuehren(),BorderLayout.CENTER);
		
		getSmartTitledPanel().setContentContainer(rgb);
		getSmartTitledPanel().getContentContainer().setName("RezeptGebuehr");
	    setName("RezeptGebuehr");
		setModal(true);
	    Point lpt = new Point(pt.x-125,pt.y+30);
	    setLocation(lpt);
	    
		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener((RehaTPEventListener) this);

		pack();
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run()
		 	   {
		 		   setVisible(true);
		 	   }
		});
		
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
		 			gegeben.requestFocus();		 		   
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

	private JPanel getGebuehren(){
		FormLayout lay = new FormLayout("10dlu,fill:0:grow(0.50),right:max(40dlu;p),5dlu,40dlu,fill:0:grow(0.50),10dlu",
									//     1   2  3    4  5    6  7   8  9   10   11  12 
										"15dlu,p,10dlu,p,10dlu,p,4dlu,p,4dlu,p,  20dlu,p,15dlu");
		PanelBuilder pb = new PanelBuilder(lay);
		CellConstraints cc = new CellConstraints();

		pb.getPanel().setOpaque(false);
		direktdruck = new JCheckBox("Quittung direkt drucken");
		direktdruck.setOpaque(false);
		direktdruck.setSelected(true);			


		pb.add(direktdruck,cc.xyw(3,2,3));

		pb.addSeparator("Für Rechenkünstler",cc.xyw(2,4,5));

		pb.addLabel("Rezeptgebühren",cc.xy(3,6));
		JLabel lab = new JLabel(SystemConfig.hmAdrRDaten.get("<Rendbetrag>"));
		lab.setFont(new Font("Tahoma",Font.BOLD,14));
		lab.setForeground(Color.BLUE);
		pb.add(lab,cc.xy(5,6));
		
		pb.addLabel("gegeben",cc.xy(3,8));
		gegeben = new JRtaTextField("D",true,"6.2","RECHTS");
		gegeben.setDValueFromS(SystemConfig.hmAdrRDaten.get("<Rendbetrag>"));
		gegeben.addKeyListener(new KeyAdapter(){
			public void keyTyped(KeyEvent event) {
				if(event.getKeyCode()==10){
					event.consume();					
				}
			}
			public void keyPressed(KeyEvent event) {
				if(event.getKeyCode()==10){
					event.consume();					
				}
			}
		    public void keyReleased(KeyEvent event) {
		    	if(event.getKeyCode()==10){
		    		event.consume();
		    	}else{
		    		DecimalFormat df = new DecimalFormat ( "#####0.00" );
		    		Double test = gegeben.getDValueFromS();
		    		Double rg = new Double(SystemConfig.hmAdrRDaten.get("<Rendbetrag>").replaceAll(",","."));
		    		Double rgtest = new Double(test-rg);
		    		if(rgtest <  0.00){
		    			rueckgeld.setForeground(Color.RED);
		    		}else{
		    			rueckgeld.setForeground(Color.BLUE);
		    		}
		    		rueckgeld.setText(df.format(rgtest) );
		    		//System.out.println("Text = "+gegeben.getText()+ " inhalt von Test = "+test.toString());
		    	}
		    }
		});

		pb.add(gegeben,cc.xy(5,8));
		
		pb.addLabel("zurück",cc.xy(3,10));
		rueckgeld = new JLabel("0,00");
		rueckgeld.setFont(new Font("Tahoma",Font.BOLD,14));
		rueckgeld.setForeground(Color.BLUE);
		pb.add(rueckgeld,cc.xy(5,10));
		if(this.nurkopie){
			okknopf = new JButton("Quittung drucken (Kopie)");			
		}else{
			okknopf = new JButton("Quittung drucken & buchen");	
		}
		okknopf.addActionListener(this);
		okknopf.setActionCommand("okknopf");
		okknopf.setName("okknopf");
		okknopf.addKeyListener(this);
		pb.add(okknopf,cc.xyw(3,12,3));
		
		pb.getPanel().validate();
		return pb.getPanel();
	}
/****************************************************/	
	public void rezGebDrucken(){
		String url = SystemConfig.rezGebVorlageNeu;	
		IDocumentService documentService = null;;
		
		try {
			documentService = Reha.officeapplication.getDocumentService();
		} catch (OfficeApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        IDocumentDescriptor docdescript = new DocumentDescriptor();
        //docdescript.setHidden(true);
		if(!direktdruck.isSelected()){
	        docdescript.setHidden(false);			
		}else{
	        docdescript.setHidden(true);			
		}

        docdescript.setAsTemplate(true);
		IDocument document = null;
		try {
			document = documentService.loadDocument(url,docdescript);

		} catch (NOAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/**********************/
		ITextDocument textDocument = (ITextDocument)document;
		String druckerName = null;
		try {
			druckerName = textDocument.getPrintService().getActivePrinter().getName();
		} catch (NOAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Wenn nicht gleich wie in der INI angegeben -> Drucker wechseln
		IPrinter iprint = null;
		if(! druckerName.equals(SystemConfig.rezGebDrucker)){
			try {
				iprint = (IPrinter) textDocument.getPrintService().createPrinter(SystemConfig.rezGebDrucker);
			} catch (NOAException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				textDocument.getPrintService().setActivePrinter(iprint);
			} catch (NOAException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/**********************/
		ITextFieldService textFieldService = textDocument.getTextFieldService();
		ITextField[] placeholders = null;
		try {
			placeholders = textFieldService.getPlaceholderFields();
		} catch (TextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < placeholders.length; i++) {
			String placeholderDisplayText = placeholders[i].getDisplayText();
			//System.out.println("Platzhalter-Name = "+placeholderDisplayText);
			placeholders[i].getTextRange().setText(SystemConfig.hmAdrRDaten.get(placeholderDisplayText));
		}
		if(direktdruck.isSelected()){
			try {
				textDocument.print();
				textDocument.close();
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//document.getFrame().getXFrame().getContainerWindow().setVisible(true);
	}
	
	public void RehaTPEventOccurred(RehaTPEvent evt) {
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
		if(arg0.getActionCommand().equals("okknopf")){
			new Thread(){
				public void run(){
					if(!nurkopie){
						doBuchen();
					}
					rezGebDrucken();
				}
			}.start();
			this.dispose();
		}
	}
	
	public void keyPressed(KeyEvent event) {
		if(event.getKeyCode()==10){
			event.consume();
			if( ((JComponent)event.getSource()).getName().equals("okknopf")){
				new Thread(){
					public void run(){
						if(!nurkopie){
							doBuchen();
						}
						rezGebDrucken();
					}
				}.start();
				this.dispose();
			}
			System.out.println("Return Gedrückt");
		}
	}
	public void doBuchen(){
		String cmd = "insert into kasse set einnahme='"+
		SystemConfig.hmAdrRDaten.get("<Rendbetrag>").replaceAll(",",".")+"', datum='"+
		datFunk.sDatInSQL(datFunk.sHeute())+"', ktext='"+
		PatGrundPanel.thisClass.patDaten.get(2)+","+
		SystemConfig.hmAdrRDaten.get("<Rnummer>")+"', "+
		"pat_intern='"+SystemConfig.hmAdrRDaten.get("<Rpatid>")+"', "+
		"rez_nr='"+SystemConfig.hmAdrRDaten.get("<Rnummer>")+"'";
		new ExUndHop().setzeStatement(cmd);
		//System.out.println("Kassenbuch -> "+cmd);
		
		cmd = "update verordn set rez_geb='"+
		SystemConfig.hmAdrRDaten.get("<Rendbetrag>").replaceAll(",",".")+"', "+
		"rez_bez='T', zzstatus='1' where id='"+SystemConfig.hmAdrRDaten.get("<Rid>")+"'";
		new ExUndHop().setzeStatement(cmd);
		//System.out.println("Rezeptstamm -> "+cmd);
		int row = AktuelleRezepte.aktRez.tabaktrez.getSelectedRow();
		if(row >= 0){
			AktuelleRezepte.aktRez.dtblm.setValueAt(PatGrundPanel.thisClass.imgzuzahl[1],row,1);
			AktuelleRezepte.aktRez.tabaktrez.repaint();
		}
	}
	
}
class RgebHintergrund extends JXPanel{
	ImageIcon hgicon;
	int icx,icy;
	AlphaComposite xac1 = null;
	AlphaComposite xac2 = null;		
	public RgebHintergrund(){
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