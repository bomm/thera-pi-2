package geraeteInit;

import hauptFenster.Reha;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
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
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import oOorgTools.OOTools;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;


import patientenFenster.PatGrundPanel;
import systemEinstellungen.SystemConfig;
import systemTools.Colors;
import systemTools.JRtaCheckBox;
import systemTools.LeistungTools;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;

public class ScannerUtil extends RehaSmartDialog implements RehaTPEventListener,WindowListener, ActionListener{
	public JRtaCheckBox[] leistung = {null,null,null,null}; 

	private RehaTPEventClass rtp = null;
	private ScannerUtilHintergrund rgb;	
	
	public JButton uebernahme;
	public JButton abbrechen;
	

	public ScannerUtil(Point pt){
		super(null,"ScannerUtil");		

		
		PinPanel pinPanel = new PinPanel();
		pinPanel.setName("ScannerUtil");
		pinPanel.getGruen().setVisible(false);
		setPinPanel(pinPanel);
		getSmartTitledPanel().setTitle("Scanner-Einstellung");

		setSize(300,250);
		setPreferredSize(new Dimension(300,250));
		getSmartTitledPanel().setPreferredSize(new Dimension (300,250));
		setPinPanel(pinPanel);
		rgb = new ScannerUtilHintergrund();
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
		getSmartTitledPanel().getContentContainer().setName("ScannerUtil");
		setName("ScannerUtil");
		setModal(true);
	    //Point lpt = new Point(pt.x-125,pt.y+30);
		Point lpt = new Point(pt.x-150,pt.y+30);
	    setLocation(lpt);
	    
		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener((RehaTPEventListener) this);

		pack();
		
			
	    


	}
	
/****************************************************/	
	/*
	public JButton okknopf;
	public JRtaTextField gegeben;
	public JLabel rueckgeld;
	public JCheckBox direktdruck;
	*/

	private JPanel getGebuehren(){     // 1     2                   3         4     5        6              7
		FormLayout lay = new FormLayout("",
									//     1   2  3    4  5   6  7   8  9   10  11  12  13
										"");
		PanelBuilder pb = new PanelBuilder(lay);
		CellConstraints cc = new CellConstraints();
		pb.getPanel().setOpaque(false);
		
		
		pb.getPanel().validate();
		return pb.getPanel();
	}
/****************************************************/	
	
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
					System.out.println("****************Scanner-Util -> Listener entfernt**************");				
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
			System.out.println("****************Scanner-Util -> Listener entfernt (Closed)**********");
		}
		
		
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getActionCommand().equals("uebernahme")){
			macheAFRHmap();
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					starteAusfallRechnung(Reha.proghome+"vorlagen/"+Reha.aktIK+"/AusfallRechnung.ott");
					return null;
				}
			}.execute();

			this.dispose();
			/********
			 * 
			 * Hier noch schnell buchen entwickeln und feddisch...
			 * 
			 */
		}
		if(arg0.getActionCommand().equals("abbrechen")){
			this.dispose();
		}

	}
	private void macheAFRHmap(){
		String mappos = "";
		String mappreis = "";
		String mapkurz = "";
		String maplang = "";
		String[] inpos = {null,null};
		String spos = "";
		String sart = "";
		Double gesamt = new Double(0.00); 
		/*
		List<String> lAdrAFRDaten = Arrays.asList(new String[]{"<AFRposition1>","<AFRposition2>","<AFRposition3>"
				,"<AFRposition4>","<AFRpreis1>","<AFRpreis2>","<AFRpreis3>","<AFRpreis4>","<AFRgesamt>","<AFRnummer>"});
		*/	
		DecimalFormat df = new DecimalFormat( "0.00" );
		
		for(int i = 0 ; i < 4; i++){
			mappos = "<AFRposition"+(i+1)+">";
			mappreis = "<AFRpreis"+(i+1)+">";
			mapkurz = "<AFRkurz"+(i+1)+">";
			maplang = "<AFRlang"+(i+1)+">";
			if(leistung[i].isSelected()){
				Double preis = new Double( (String)PatGrundPanel.thisClass.vecaktrez.get(18+i));
				String s = df.format( preis);
				SystemConfig.hmAdrAFRDaten.put(mappos,leistung[i].getText());
				SystemConfig.hmAdrAFRDaten.put(mappreis,s);
				gesamt = gesamt+preis;
				
				spos = (String)PatGrundPanel.thisClass.vecaktrez.get(8+i);
				sart = (String)PatGrundPanel.thisClass.vecaktrez.get(1);
				sart = sart.substring(0,2);
				inpos = LeistungTools.getLeistung(sart, spos);	
				SystemConfig.hmAdrAFRDaten.put(maplang,inpos[0]);
				SystemConfig.hmAdrAFRDaten.put(mapkurz,inpos[1]);
				//System.out.println(inpos[0]);
				//System.out.println(inpos[1]);
				
			}else{
				spos = (String)PatGrundPanel.thisClass.vecaktrez.get(8+i);
				sart = (String)PatGrundPanel.thisClass.vecaktrez.get(1);
				sart = sart.substring(0,2);
				inpos = LeistungTools.getLeistung(sart, spos);	

				SystemConfig.hmAdrAFRDaten.put(mappos,leistung[i].getText());
				SystemConfig.hmAdrAFRDaten.put(mappreis,"0,00");
				SystemConfig.hmAdrAFRDaten.put(maplang,(!inpos[0].equals("") ? inpos[0] : "----") );
				SystemConfig.hmAdrAFRDaten.put(mapkurz,(!inpos[1].equals("") ? inpos[1] : "----") );

			}
			
		}
		SystemConfig.hmAdrAFRDaten.put("<AFRgesamt>",df.format( gesamt));
		SystemConfig.hmAdrAFRDaten.put("<AFRnummer>","AF-010101");
	}
	public void keyPressed(KeyEvent event) {
		if(event.getKeyCode()==10){
			event.consume();
			if( ((JComponent)event.getSource()).getName().equals("uebernahme")){
				//doUebernahme();
			}
			if( ((JComponent)event.getSource()).getName().equals("abbrechen")){
				this.dispose();
			}

			System.out.println("Return Gedrückt");
		}
	}
	public static void starteAusfallRechnung(String url){
		IDocumentService documentService = null;;
		System.out.println("Starte Datei -> "+url);
		try {
			documentService = Reha.officeapplication.getDocumentService();
		} catch (OfficeApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        IDocumentDescriptor docdescript = new DocumentDescriptor();
       	docdescript.setHidden(false);
        docdescript.setAsTemplate(true);
		IDocument document = null;
		//ITextTable[] tbl = null;
		try {
			document = documentService.loadDocument(url,docdescript);
		} catch (NOAException e) {

			e.printStackTrace();
		}
		ITextDocument textDocument = (ITextDocument)document;
		ITextFieldService textFieldService = textDocument.getTextFieldService();
		ITextField[] placeholders = null;
		try {
			placeholders = textFieldService.getPlaceholderFields();
		} catch (TextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < placeholders.length; i++) {
			boolean loeschen = false;
			boolean schonersetzt = false;
			String placeholderDisplayText = placeholders[i].getDisplayText().toLowerCase();
			//System.out.println(placeholderDisplayText);	
		    /*****************/			
			Set entries = SystemConfig.hmAdrPDaten.entrySet();
		    Iterator it = entries.iterator();
		    while (it.hasNext()) {
		      Map.Entry entry = (Map.Entry) it.next();
		      if(((String)entry.getKey()).toLowerCase().equals(placeholderDisplayText)){
		    	  placeholders[i].getTextRange().setText(((String)entry.getValue()));
		    	  schonersetzt = true;
		    	  break;
		      }
		    }
		    /*****************/
		    entries = SystemConfig.hmAdrAFRDaten.entrySet();
		    it = entries.iterator();
		    while (it.hasNext() && (!schonersetzt)) {
		      Map.Entry entry = (Map.Entry) it.next();
		      if(((String)entry.getKey()).toLowerCase().equals(placeholderDisplayText)){
		    	  placeholders[i].getTextRange().setText(((String)entry.getValue()));
		    	  schonersetzt = true;
		    	  break;
		      }
		    }
		    if(!schonersetzt){
		    	OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
		    }
		    /*****************/
		}
		
		
	}
	
	
}
class ScannerUtilHintergrund extends JXPanel{
	ImageIcon hgicon;
	int icx,icy;
	AlphaComposite xac1 = null;
	AlphaComposite xac2 = null;		
	public ScannerUtilHintergrund(){
		super();
		hgicon = new ImageIcon(Reha.proghome+"icons/xsane.png");
		//hgicon = new ImageIcon(Reha.proghome+"icons/geld.png");
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