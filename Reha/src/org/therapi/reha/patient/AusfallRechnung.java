package org.therapi.reha.patient;

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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;


import oOorgTools.OOTools;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import sqlTools.ExUndHop;
import systemEinstellungen.SystemConfig;
import systemTools.Colors;
import systemTools.JCompTools;
import systemTools.JRtaCheckBox;
import systemTools.JRtaTextField;
import systemTools.LeistungTools;
import terminKalender.DatFunk;

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

public class AusfallRechnung extends RehaSmartDialog implements RehaTPEventListener,WindowListener, ActionListener{
	public JRtaCheckBox[] leistung = {null,null,null,null,null}; 

	private RehaTPEventClass rtp = null;
	private AusfallRechnungHintergrund rgb;	
	
	public JButton uebernahme;
	public JButton abbrechen;
	public String afrNummer;
	public AusfallRechnung(Point pt){
		super(null,"AusfallRechnung");		

		
		pinPanel = new PinPanel();
		pinPanel.setName("AusfallRechnung");
		pinPanel.getGruen().setVisible(false);
		setPinPanel(pinPanel);
		getSmartTitledPanel().setTitle("Ausfallrechnung erstellen");

		setSize(300,270);
		setPreferredSize(new Dimension(300,270));
		getSmartTitledPanel().setPreferredSize(new Dimension (300,270));
		setPinPanel(pinPanel);
		rgb = new AusfallRechnungHintergrund();
		rgb.setLayout(new BorderLayout());

		
		new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {
	
			     rgb.setBackgroundPainter(Reha.thisClass.compoundPainter.get("RezeptGebuehren"));
				return null;
			}
			
		}.execute();	
		rgb.add(getGebuehren(),BorderLayout.CENTER);
		
		getSmartTitledPanel().setContentContainer(rgb);
		getSmartTitledPanel().getContentContainer().setName("AusfallRechnung");
		setName("AusfallRechnung");
		setModal(true);
	    //Point lpt = new Point(pt.x-125,pt.y+30);
		Point lpt = new Point(pt.x-150,pt.y+30);
	    setLocation(lpt);
	    
		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener((RehaTPEventListener) this);

		pack();
		
			
	    


	}
	
/****************************************************/	

	private JPanel getGebuehren(){     // 1     2                   3         4     5        6              7
		FormLayout lay = new FormLayout("10dlu,fill:0:grow(0.50),right:80dlu,10dlu,80dlu,fill:0:grow(0.50),10dlu",
									//     1   2  3    4  5   6  7   8  9   10  11    12  13    14  15
										"15dlu,p,10dlu,p,2dlu,p,2dlu,p,2dlu,p, 10dlu, p,  20dlu, p ,20dlu");
		PanelBuilder pb = new PanelBuilder(lay);
		CellConstraints cc = new CellConstraints();

		pb.getPanel().setOpaque(false);
		
		pb.addLabel("Bitte die Positionen auswählen die Sie berechnen wollen",cc.xyw(2, 2, 4));

		pb.addLabel("Heilmittel 1",cc.xy(3, 4));
		String lab = (String)Reha.thisClass.patpanel.vecaktrez.get(48);
		leistung[0] = new JRtaCheckBox((lab.equals("") ? "----" : lab));
		leistung[0].setOpaque(false);
		if(!lab.equals("")){
			leistung[0].setSelected(true);			
		}else{
			leistung[0].setSelected(false);
			leistung[0].setEnabled(false);
		}
		pb.add(leistung[0],cc.xyw(5, 4, 2));
		
		pb.addLabel("Heilmittel 2",cc.xy(3, 6));
		lab = (String)Reha.thisClass.patpanel.vecaktrez.get(49);
		leistung[1] = new JRtaCheckBox((lab.equals("") ? "----" : lab));
		leistung[1].setOpaque(false);
		if(!lab.equals("")){
						
		}else{
			leistung[1].setSelected(false);
			leistung[1].setEnabled(false);
		}
		pb.add(leistung[1],cc.xyw(5, 6, 2));

		pb.addLabel("Heilmittel 3",cc.xy(3, 8));
		lab = (String)Reha.thisClass.patpanel.vecaktrez.get(50);
		leistung[2] = new JRtaCheckBox((lab.equals("") ? "----" : lab));
		leistung[2].setOpaque(false);
		if(!lab.equals("")){
						
		}else{
			leistung[2].setSelected(false);
			leistung[2].setEnabled(false);
		}
		pb.add(leistung[2],cc.xyw(5, 8, 2));

		pb.addLabel("Heilmittel 4",cc.xy(3, 10));
		lab = (String)Reha.thisClass.patpanel.vecaktrez.get(51);
		leistung[3] = new JRtaCheckBox((lab.equals("") ? "----" : lab));
		leistung[3].setOpaque(false);
		if(!lab.equals("")){
						
		}else{
			leistung[3].setSelected(false);
			leistung[3].setEnabled(false);
		}
		pb.add(leistung[3],cc.xyw(5, 10, 2));

		pb.addLabel("Eintragen in Memo",cc.xy(3, 12));
		leistung[4] = new JRtaCheckBox("Fehldaten");
		leistung[4].setOpaque(false);
		leistung[4].setSelected(true);
		pb.add(leistung[4],cc.xyw(5, 12, 2));
		
		uebernahme = new JButton("drucken & buchen");
		uebernahme.setActionCommand("uebernahme");
		uebernahme.addActionListener(this);
		uebernahme.addKeyListener(this);
		pb.add(uebernahme,cc.xy(3,14));
		
		abbrechen = new JButton("abbrechen");
		abbrechen.setActionCommand("abbrechen");
		abbrechen.addActionListener(this);
		abbrechen.addKeyListener(this);
		pb.add(abbrechen,cc.xy(5,14));
		
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
					this.dispose();
					super.dispose();
					//System.out.println("****************Ausfallrechnung -> Listener entfernt**************");				
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
			pinPanel = null;
			dispose();
			super.dispose();
			//System.out.println("****************Ausfallrechnung -> Listener entfernt (Closed)**********");
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
					try{
						starteAusfallRechnung(Reha.proghome+"vorlagen/"+Reha.aktIK+"/AusfallRechnung.ott");
						doBuchen();
						if(leistung[4].isSelected()){
							macheMemoEintrag();
						}
					}catch(Exception ex){
							ex.printStackTrace();
							JOptionPane.showMessageDialog(null, "Fehler bei der Erstellung der Ausfallrechnung");
					}
					getInstance().dispose();
					return null;
				}
			}.execute();
			/*
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					if(leistung[4].isSelected()){
						macheMemoEintrag();
					}
					return null;
				}
			}.execute();
			this.dispose();
			*/
		}
		if(arg0.getActionCommand().equals("abbrechen")){
			this.dispose();
		}

	}
	private AusfallRechnung getInstance(){
		return this;
	}
	private void doBuchen(){
		StringBuffer buf = new StringBuffer();
		buf.append("insert into rgaffaktura set ");
		buf.append("rnr='"+afrNummer+"', ");
		buf.append("reznr='"+(String)Reha.thisClass.patpanel.vecaktrez.get(1)+"', ");
		buf.append("pat_intern='"+(String)Reha.thisClass.patpanel.vecaktrez.get(0)+"', ");
		buf.append("rgesamt='"+(String)SystemConfig.hmAdrAFRDaten.get("<AFRgesamt>").replace(",",".")+"', ");
		buf.append("roffen='"+(String)SystemConfig.hmAdrAFRDaten.get("<AFRgesamt>").replace(",",".")+"', ");
		buf.append("rdatum='"+DatFunk.sDatInSQL(DatFunk.sHeute())+"'");
		////System.out.println(buf.toString());
		sqlTools.SqlInfo.sqlAusfuehren(buf.toString());
	}
	private void macheMemoEintrag(){
		StringBuffer sb = new StringBuffer();
		sb.append(DatFunk.sHeute()+" - unentschuldigt oder zu spät abgesagt - Rechnung!!\n");
		sb.append(Reha.thisClass.patpanel.pmemo[1].getText());
		Reha.thisClass.patpanel.pmemo[1].setText(sb.toString());
		String cmd = "update pat5 set pat_text='"+sb.toString()+"' where pat_intern = '"+Reha.thisClass.patpanel.aktPatID+"'";
		new ExUndHop().setzeStatement(cmd);
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
		int preisgruppe = 0;
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
				Double preis = new Double( (String)Reha.thisClass.patpanel.vecaktrez.get(18+i));
				String s = df.format( preis);
				SystemConfig.hmAdrAFRDaten.put(mappos,leistung[i].getText());
				SystemConfig.hmAdrAFRDaten.put(mappreis,s);
				gesamt = gesamt+preis;
				
				spos = (String)Reha.thisClass.patpanel.vecaktrez.get(8+i);
				sart = (String)Reha.thisClass.patpanel.vecaktrez.get(1);
				sart = sart.substring(0,2);
				preisgruppe = Integer.parseInt(Reha.thisClass.patpanel.vecaktrez.get(41))-1;		
				inpos = LeistungTools.getLeistung(sart, spos,preisgruppe);	
				SystemConfig.hmAdrAFRDaten.put(maplang,inpos[0]);
				SystemConfig.hmAdrAFRDaten.put(mapkurz,inpos[1]);
				////System.out.println(inpos[0]);
				////System.out.println(inpos[1]);
				
			}else{
				spos = (String)Reha.thisClass.patpanel.vecaktrez.get(8+i);
				sart = (String)Reha.thisClass.patpanel.vecaktrez.get(1);
				sart = sart.substring(0,2);
				preisgruppe = Integer.parseInt(Reha.thisClass.patpanel.vecaktrez.get(41))-1;
				inpos = LeistungTools.getLeistung(sart, spos,preisgruppe);	

				SystemConfig.hmAdrAFRDaten.put(mappos,leistung[i].getText());
				SystemConfig.hmAdrAFRDaten.put(mappreis,"0,00");
				SystemConfig.hmAdrAFRDaten.put(maplang,(!inpos[0].equals("") ? inpos[0] : "----") );
				SystemConfig.hmAdrAFRDaten.put(mapkurz,(!inpos[1].equals("") ? inpos[1] : "----") );

			}
			
		}
		SystemConfig.hmAdrAFRDaten.put("<AFRgesamt>",df.format( gesamt));
		/// Hier mu� noch die Rechnungsnummer bezogen und eingetragen werden
		afrNummer = "AFR-"+Integer.toString(sqlTools.SqlInfo.erzeugeNummer("afrnr"));
		SystemConfig.hmAdrAFRDaten.put("<AFRnummer>",afrNummer);
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

			//System.out.println("Return Gedrückt");
		}
		if(event.getKeyCode()==27){
			this.dispose();
		}
	}
	public static void starteAusfallRechnung(String url){
		IDocumentService documentService = null;;
		//System.out.println("Starte Datei -> "+url);
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
			//boolean loeschen = false;
			boolean schonersetzt = false;
			String placeholderDisplayText = placeholders[i].getDisplayText().toLowerCase();
			////System.out.println(placeholderDisplayText);	
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
class AusfallRechnungHintergrund extends JXPanel{
	ImageIcon hgicon;
	int icx,icy;
	AlphaComposite xac1 = null;
	AlphaComposite xac2 = null;		
	public AusfallRechnungHintergrund(){
		super();
		/*
		hgicon = new ImageIcon(Reha.proghome+"icons/geld.png");
		icx = hgicon.getIconWidth()/2;
		icy = hgicon.getIconHeight()/2;
		xac1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.15f); 
		xac2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f);
		*/			
		
	}
	@Override
	public void paintComponent( Graphics g ) { 
		super.paintComponent( g );
		Graphics2D g2d = (Graphics2D)g;
		
		if(hgicon != null){
			//g2d.setComposite(this.xac1);
			//g2d.drawImage(hgicon.getImage(), (getWidth()/2)-icx , (getHeight()/2)-icy,null);
			//g2d.setComposite(this.xac2);
		}
	}
}