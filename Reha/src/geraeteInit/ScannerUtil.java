package geraeteInit;

import hauptFenster.Reha;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;
import java.net.URLDecoder;
import java.net.URLEncoder;
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
import systemEinstellungen.INIFile;
import systemEinstellungen.SystemConfig;
import systemTools.Colors;
import systemTools.JRtaCheckBox;
import systemTools.JRtaComboBox;
import systemTools.LeistungTools;
import uk.co.mmscomputing.device.scanner.Scanner;
import uk.co.mmscomputing.device.scanner.ScannerIOException;
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
	
	public JRtaComboBox[] jcmbscan = {null,null,null,null,null};
	public JRtaCheckBox[] jcbscan = {null,null,null,null,null};
	Scanner scanner;	
	public ScannerUtil(Point pt){
		super(null,"ScannerUtil");		

		
		PinPanel pinPanel = new PinPanel();
		pinPanel.setName("ScannerUtil");
		pinPanel.getGruen().setVisible(false);
		setPinPanel(pinPanel);
		getSmartTitledPanel().setTitle("Scanner-Einstellung");

		setSize(400,350);
		setPreferredSize(new Dimension(400,350));
		getSmartTitledPanel().setPreferredSize(new Dimension (400,350));
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
			     Color[] colors = {Color.WHITE,Colors.Gray.alpha(0.05f)};
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
		
		new SwingWorker<String,String>(){
			@Override
			protected String doInBackground() throws Exception {
				 scanner = Scanner.getDevice();
				    try {
						String[] names = scanner.getDeviceNames();
						for(int i = 0; i < names.length;i++){
							jcmbscan[0].addItem(names[i]);
						}
						if(!scanner.getSelectedDeviceName().equals(SystemConfig.sDokuScanner)){
							jcmbscan[0].setSelectedItem(scanner.getSelectedDeviceName());
						}else{
							jcmbscan[0].setSelectedItem(SystemConfig.sDokuScanner);	
						}
						
					} catch (ScannerIOException e2) {
						e2.printStackTrace();
					}
				return null;
			}
			
		}.execute();

		pack();
		
			
	    


	}
	
/****************************************************/	
	/*
	public JButton okknopf;
	public JRtaTextField gegeben;
	public JLabel rueckgeld;
	public JCheckBox direktdruck;
	*/

	private JPanel getGebuehren(){     // 1        2               3   4     5   6    7
		FormLayout lay = new FormLayout("40dlu, right:max(70dlu;p),5dlu,p,fill:0:grow(1.00)",
									//     1   2  3    4   5   6   7    8   9    10   11     12   13    14       15             16
										"30dlu,p,3dlu, p,3dlu, p, 3dlu, p, 10dlu, p,  10dlu, p,  20dlu,40dlu,fill:0:grow(1.00),40dlu ");
		PanelBuilder pb = new PanelBuilder(lay);
		CellConstraints cc = new CellConstraints();
		pb.getPanel().setOpaque(false);
		
		pb.addLabel("installierte Geräte",cc.xy(2, 2));
		jcmbscan[0] = new JRtaComboBox();
		pb.add(jcmbscan[0],cc.xy(4, 2));
		
		pb.addLabel("Scanmodus",cc.xy(2, 4));
		jcmbscan[1] = new JRtaComboBox(new String[] {"Schwarz/Weiß","Graustufen","Farbe"} );
		jcmbscan[1].setSelectedItem(SystemConfig.hmDokuScanner.get("farben"));
		pb.add(jcmbscan[1],cc.xy(4, 4));

		pb.addLabel("Auflösung",cc.xy(2, 6));
		jcmbscan[2] = new JRtaComboBox(new String[]{"50","75dpi","100dpi","150dpi","200dpi","300dpi"});		
		jcmbscan[2].setSelectedItem(SystemConfig.hmDokuScanner.get("aufloesung")+"dpi");
		pb.add(jcmbscan[2],cc.xy(4, 6));
		
		pb.addLabel("Seitenformat",cc.xy(2, 8));
		//jcmbscan[3] = new JRtaComboBox(new String[]{"Din A6","Din A6-quer","Din A5","Din A5-quer","Din A4","Din A4-quer"});
		jcmbscan[3] = new JRtaComboBox(new String[]{"Din A6","Din A5","Din A4"});		
		jcmbscan[3].setSelectedItem(SystemConfig.hmDokuScanner.get("seiten"));
		pb.add(jcmbscan[3],cc.xy(4, 8));
		
		pb.addLabel("Scandialog",cc.xy(2, 10));
		jcbscan[0] = new JRtaCheckBox("verwenden");
		jcbscan[0].setOpaque(false);
		jcbscan[0].setSelected((SystemConfig.hmDokuScanner.get("dialog").equals("1") ? true : false));
		pb.add(jcbscan[0],cc.xy(4, 10));
		
		pb.addLabel("Einstellungen als",cc.xy(2, 12));
		jcbscan[1] = new JRtaCheckBox("Standard verwenden");
		jcbscan[1].setOpaque(false);		
		pb.add(jcbscan[1],cc.xy(4, 12));
		
		JXPanel jpan = new JXPanel();
		//jpan.setBackground(Color.RED);
		jpan.setOpaque(false);
		pb.add(jpan,cc.xywh(1, 14,5,2));

		FormLayout lay2 = new FormLayout("fill:0:grow(0.33),80dlu,fill:0:grow(0.33),80dlu,fill:0:grow(0.33)",
				"fill:0:grow(0.50),p,fill:0:grow(0.50)");
		CellConstraints cc2 = new CellConstraints();
		PanelBuilder pb2 = new PanelBuilder(lay2);
		pb2.getPanel().setOpaque(false);
		uebernahme = new JButton("übernehmen");
		uebernahme.setActionCommand("uebernahme");
		uebernahme.addActionListener(this);
		uebernahme.addKeyListener(this);
		abbrechen = new JButton("abbrechen");
		abbrechen.setActionCommand("abbrechen");
		abbrechen.addActionListener(this);
		abbrechen.addKeyListener(this);
		pb2.add(uebernahme,cc2.xy(2,2));
		pb2.add(abbrechen,cc2.xy(4,2));
		pb.add(pb2.getPanel(),cc.xyw(1, 14,5));
		//public JRtaComboBox[] jcmbscan = {null,null,null,null,null};
		//public JRtaCheckBox[] jcbscan = {null,null,null,null,null};
		
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
		scanner = null;

		
		
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getActionCommand().equals("uebernahme")){
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					 
					return null;
				}
			}.execute();
			doSpeichernScanner();
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
	private void doSpeichernScanner(){
		String item = "";
		if(jcbscan[1].isSelected()){
			INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/geraete.ini");

			item = (String) jcmbscan[0].getSelectedItem();
			SystemConfig.sDokuScanner = item;
			inif.setStringProperty("DokumentenScanner","DokumentenScannerName" , item, null);

			item = (String) jcmbscan[1].getSelectedItem();
			SystemConfig.hmDokuScanner.put("farben",item);
			inif.setStringProperty("DokumentenScanner","DokumentenScannerFarben" , item, null);
			

			item = (String) jcmbscan[2].getSelectedItem();
			SystemConfig.hmDokuScanner.put("aufloesung",item.replaceAll("dpi", "") );
			inif.setStringProperty("DokumentenScanner","DokumentenScannerAufloesung" , item.replaceAll("dpi", ""), null);

			item = (String) jcmbscan[3].getSelectedItem();
			inif.setStringProperty("DokumentenScanner","DokumentenScannerSeiten" , item , null);
			SystemConfig.hmDokuScanner.put("seiten", item);
			
			item = (jcbscan[0].isSelected() ? "1" : "0");
			SystemConfig.hmDokuScanner.put("dialog", item);
			inif.setStringProperty("DokumentenScanner","DokumentenScannerDialog" , item , null);		
			inif.save();
		}else{
			item = (String) jcmbscan[0].getSelectedItem();
			SystemConfig.sDokuScanner = item;

			item = (String) jcmbscan[1].getSelectedItem();
			SystemConfig.hmDokuScanner.put("farben",item);

			item = (String) jcmbscan[2].getSelectedItem();
			SystemConfig.hmDokuScanner.put("aufloesung",item.replaceAll("dpi", "") );

			item = (String) jcmbscan[3].getSelectedItem();
			SystemConfig.hmDokuScanner.put("seiten", item);
			
			item = (jcbscan[0].isSelected() ? "1" : "0");
			SystemConfig.hmDokuScanner.put("dialog", item);
		}
		//this.dispose();

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
		hgicon = new ImageIcon(new ImageIcon(Reha.proghome+"icons/xsane.png").getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH));
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