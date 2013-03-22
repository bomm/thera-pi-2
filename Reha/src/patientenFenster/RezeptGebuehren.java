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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.DecimalFormat;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import oOorgTools.OOTools;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.MattePainter;
import org.therapi.reha.patient.AktuelleRezepte;

import CommonTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import CommonTools.JRtaTextField;
import terminKalender.DatFunk;
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

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import errorMail.ErrorMail;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;

public class RezeptGebuehren extends RehaSmartDialog implements RehaTPEventListener,WindowListener, ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean nurkopie;
	boolean aushistorie;
	public JButton okknopf;
	public JRtaTextField gegeben;
	public JLabel rueckgeld;
	public JCheckBox direktdruck;
	private RehaTPEventClass rtp = null;
	private RgebHintergrund rgb;	
	//CompoundPainter cp = null;
	MattePainter mp = null;
	LinearGradientPaint p = null;
	private AktuelleRezepte aktuelleRezepte;
	ITextDocument textDocument = null;
	
	public RezeptGebuehren(AktuelleRezepte aktrez,boolean kopie,boolean historie,Point pt){
		super(null,"RezeptGebuehr");
		if(aktrez!=null){
			this.aktuelleRezepte = aktrez;			
		}
		this.nurkopie = kopie;
		this.aushistorie = historie;
		
		PinPanel pinPanel = new PinPanel();
		pinPanel.setName("RezeptGebuehr");
		pinPanel.getGruen().setVisible(false);
		setPinPanel(pinPanel);
		getSmartTitledPanel().setTitle("Rezept-Gebühr");
		getSmartTitledPanel().setName("RezeptGebuehr");
		setSize(175,250);
		setPreferredSize(new Dimension(175,250));
		getSmartTitledPanel().setPreferredSize(new Dimension (175,250));
		setPinPanel(pinPanel);
		rgb = new RgebHintergrund();
		rgb.setLayout(new BorderLayout());
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					rgb.setBackgroundPainter(Reha.thisClass.compoundPainter.get("RezeptGebuehren"));
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null, "Fehler im BackgroundPainter Rezeptgebühren");
				}
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
				if(event.getKeyCode()==27){
					dispose();
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
		    		////System.out.println("Text = "+gegeben.getText()+ " inhalt von Test = "+test.toString());
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
		okknopf.setActionCommand("okknopf");
		okknopf.addActionListener(this);
		okknopf.setName("okknopf");
		okknopf.addKeyListener(this);
		pb.add(okknopf,cc.xyw(3,12,3));
		
		pb.getPanel().validate();
		return pb.getPanel();
	}
/****************************************************/	
	public synchronized void rezGebDrucken(){
		if(!Reha.officeapplication.isActive()){
			Reha.starteOfficeApplication();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		try{
		String url = "";
		if( ((String)Reha.thisClass.patpanel.vecaktrez.get(43)).equals("T") ){
			url = SystemConfig.rezGebVorlageHB;
		}else{
			url = SystemConfig.rezGebVorlageNeu;			
		}
	
		// Wenn Hausbesuch andere Vorlage.....
		IDocumentService documentService = null;
		
		try {
			documentService = Reha.officeapplication.getDocumentService();
		} catch (OfficeApplicationException e) {
			e.printStackTrace();
		}catch(Exception ex){
			ex.printStackTrace();
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
			textDocument = (ITextDocument)document;

		}catch (NOAException e) {
			e.printStackTrace();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		OOTools.druckerSetzen(textDocument, SystemConfig.rezGebDrucker);
		/**********************/
		ITextFieldService textFieldService = textDocument.getTextFieldService();
		ITextField[] placeholders = null;
		try {
			placeholders = textFieldService.getPlaceholderFields();
		} catch (TextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try{
		for (int i = 0; i < placeholders.length; i++) {
			String placeholderDisplayText = placeholders[i].getDisplayText();
			if(placeholderDisplayText.startsWith("<R")){
				placeholders[i].getTextRange().setText(SystemConfig.hmAdrRDaten.get(placeholderDisplayText));	
			}else if(placeholderDisplayText.startsWith("<P")){
				placeholders[i].getTextRange().setText(SystemConfig.hmAdrPDaten.get(placeholderDisplayText));
			}else{
				placeholders[i].getTextRange().setText("\b");
			}
		}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		if(direktdruck.isSelected()){
			try {
				textDocument.print();
				while(textDocument.getPrintService().isActivePrinterBusy()){
					Thread.sleep(50);
				}
				Thread.sleep(100);
				textDocument.close();
			} catch (DocumentException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}catch (NOAException e) {
				e.printStackTrace();
			}
			
		}else{
			final ITextDocument xtextDocument = textDocument;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					//Reha.officeapplication.getDocumentService().
					xtextDocument.getFrame().getXFrame().activate();
				}
			});
		}
		//document.getFrame().getXFrame().getContainerWindow().setVisible(true);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
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
					//System.out.println("****************Rezeptgebühren -> Listener entfernt**************");				
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
			super.dispose();
			dispose();
			//System.out.println("****************Rezeptgebühren -> Listener entfernt (Closed)**********");
		}
		
		
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getActionCommand().equals("okknopf")){
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					try{
						setVisible(false);
						if(!nurkopie){
							doBuchen();
						}
						rezGebDrucken();
						doSchliessen();
					}catch(Exception ex){
						ex.printStackTrace();
						JOptionPane.showMessageDialog(null,"Fehler in der Funktion Drucken und Buchen\n"+ex.getMessage());
					}

					return null;
				}
			}.execute();

			/*
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					try{
						if(!nurkopie){
							doBuchen();
						}
						rezGebDrucken();
						doSchliessen();
					}catch(Exception ex){
						ex.printStackTrace();
					}
					return null;
				}
				
			}.execute();
			*/
		}
	}
	public void doSchliessen(){
		this.dispose();
		super.dispose();
	}
	
	public void keyPressed(KeyEvent event) {
		if(event.getKeyCode()==10){
			event.consume();
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					try{
						if(!nurkopie){
							doBuchen();
						}
						rezGebDrucken();
						doSchliessen();
					}catch(Exception ex){
						ex.printStackTrace();
					}
					return null;
				}
			}.execute();
			//System.out.println("Return Gedrückt");
		}
	}
	public void doBuchen(){
		try{
			String cmd = null;
			try{
				String srgeb = "";
				try{
					srgeb = (SystemConfig.hmAdrRDaten.get("<Rendbetrag>").replace(",",".")==null ? 
							"0.00" : SystemConfig.hmAdrRDaten.get("<Rendbetrag>").replace(",",".") );
				}catch(Exception ex1){
					JOptionPane.showMessageDialog(null, "Fehler-Nr. 1 beim einstellen der Rezeptgebühr im Rezept\n\n"+
							"Der Wert der HashMap hat aktuell: "+SystemConfig.hmAdrRDaten.get("<Rendbetrag>")+"\n"+
							"Der Wert für Rezeptnummer ist aktuell: "+Reha.thisClass.patpanel.vecaktrez.get(1)+"\n"+
							"\nSql-Befehl = "+cmd+"\n\n"+
							"Bitte notieren Sie diese Fehlermeldung und informieren Sie den Administrator umgehend.");
					srgeb = "0.00";
					//ErrorMail(String text,String comp,String user,String senderadress,String xtitel)
					new ErrorMail("Fehler bei Rezeptgebührenkassieren - Fehler-Nr. 1 - Reznr."+SystemConfig.hmAdrRDaten.get("<Rnummer>"),
							"Computer: "+SystemConfig.dieseMaschine.toString(),
							"Benutzer: "+Reha.aktUser,
							SystemConfig.hmEmailIntern.get("Username"),
							"Fehler-Mail");

				}
				try{
					Reha.thisClass.patpanel.vecaktrez.set(39, "1");
					String cmd2 = "update verordn set rez_geb='"+
					srgeb+"', "+
					"rez_bez='T', zzstatus='1' where rez_nr='"+SystemConfig.hmAdrRDaten.get("<Rnummer>")+"' LIMIT 1";
					boolean allesok = SqlInfo.sqlAusfuehren(cmd2.toString());
					this.aktuelleRezepte.doVectorAktualisieren(new int[]{14,39}, new String[]{"T","1"});
					if(!allesok){
						JOptionPane.showMessageDialog(null, "Fehler-Nr. 2 beim einstellen der Rezeptgebühr im Rezept\n\n"+
								"Der Wert der HashMap hat aktuell: "+SystemConfig.hmAdrRDaten.get("<Rendbetrag>")+"\n"+
								"Der Wert für Rezeptnummer ist aktuell: "+Reha.thisClass.patpanel.vecaktrez.get(1)+"\n"+
								"\nSql-Befehl = "+cmd+"\n\n"+
								"Bitte notieren Sie diese Fehlermeldung und informieren Sie den Administrator umgehend.");
						new ErrorMail("Fehler bei Rezeptgebührenkassieren - Fehler-Nr. 2 - Reznr."+SystemConfig.hmAdrRDaten.get("<Rnummer>"),
								"Computer: "+SystemConfig.dieseMaschine.toString(),
								"Benutzer: "+Reha.aktUser,
								SystemConfig.hmEmailIntern.get("Username"),
								"Fehler-Mail");

					}
				}catch(Exception ex3){
					JOptionPane.showMessageDialog(null, "Fehler-Nr. 2.1 beim einstellen der Rezeptgebühr im Rezept\n\n"+
							"Der Wert der HashMap hat aktuell: "+SystemConfig.hmAdrRDaten.get("<Rendbetrag>")+"\n"+
							"Der Wert für Rezeptnummer ist aktuell: "+Reha.thisClass.patpanel.vecaktrez.get(1)+"\n"+
							"\nSql-Befehl = "+cmd+"\n\n"+
							"Bitte notieren Sie diese Fehlermeldung und informieren Sie den Administrator umgehend.");
				}

			}catch(Exception ex2){
				JOptionPane.showMessageDialog(null, "Fehler-Nr. 3 beim einstellen der Rezeptgebühr im Rezept\n\n"+
						"Der Wert der HashMap hat aktuell: "+SystemConfig.hmAdrRDaten.get("<Rendbetrag>")+"\n"+
						"Der Wert für Rezeptnummer ist aktuell: "+Reha.thisClass.patpanel.vecaktrez.get(1)+"\n"+
						"\nSql-Befehl = "+cmd+"\n\n"+
						"Bitte notieren Sie diese Fehlermeldung und informieren Sie den Administrator umgehend.");
				new ErrorMail("Fehler bei Rezeptgebührenkassieren - Fehler-Nr. 3 - Reznr."+SystemConfig.hmAdrRDaten.get("<Rnummer>"),
						"Computer: "+SystemConfig.dieseMaschine.toString(),
						"Benutzer: "+Reha.aktUser,
						SystemConfig.hmEmailIntern.get("Username"),
						"Fehler-Mail");
			}
			try{
				aktuelleRezepte.setZuzahlImage(1);
			}catch(Exception ex3){
				JOptionPane.showMessageDialog(null,"Der Zuzahlungsstatus im Rezeptstamm konnte nicht korrekt gesetzt werden.\n+" +
					"Bitte notieren Sie den Namen des Patienten und die Rezeptnummer und verständigen\n"+
					"Sie den Administrator");
				new ErrorMail("Fehler bei Rezeptgebührenkassieren - Der Zuzahlungsstatus im Rezeptstamm....Reznr.:"+SystemConfig.hmAdrRDaten.get("<Rnummer>"),
						"Computer: "+SystemConfig.dieseMaschine.toString(),
						"Benutzer: "+Reha.aktUser,
						SystemConfig.hmEmailIntern.get("Username"),
						"Fehler-Mail");

			}
			/**/

			try{
				cmd = "insert into kasse set einnahme='"+
				SystemConfig.hmAdrRDaten.get("<Rendbetrag>").replaceAll(",",".")+"', datum='"+
				DatFunk.sDatInSQL(DatFunk.sHeute())+"', ktext='"+
				Reha.thisClass.patpanel.patDaten.get(2)+","+
				SystemConfig.hmAdrRDaten.get("<Rnummer>")+"', "+
				"pat_intern='"+SystemConfig.hmAdrRDaten.get("<Rpatid>")+"', "+
				"rez_nr='"+SystemConfig.hmAdrRDaten.get("<Rnummer>")+"' ," +
				"user='"+ Reha.aktUser +"'";
				SqlInfo.sqlAusfuehren(cmd);
				////System.out.println("Kassenbuch -> "+cmd);
			}catch(Exception ex){
				JOptionPane.showMessageDialog(null,"Die bezahlten Rezeptgebühren konnten nicht verbucht werden.\n+" +
						"Bitte notieren Sie den Namen des Patienten und die Rezeptnummer und verständigen\n"+
						"Sie den Administrator");
				new ErrorMail("Die bezahlten Rezeptgebühren konnten nicht verbucht....Reznr.:"+SystemConfig.hmAdrRDaten.get("<Rnummer>"),
						"Computer: "+SystemConfig.dieseMaschine.toString(),
						"Benutzer: "+Reha.aktUser,
						SystemConfig.hmEmailIntern.get("Username"),
						"Fehler-Mail");
								
			}
			/**/
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null,"Der Zuzahlungsstatus im Rezeptstamm konnte nicht korrekt gesetzt werden.\n+" +
					"Bitte notieren Sie den Namen des Patienten und die Rezeptnummer und verständigen\n"+
					"Sie den Administrator");
			new ErrorMail("Der Zuzahlungsstatus im Rezeptstamm konnte nicht....Rnr.:"+SystemConfig.hmAdrRDaten.get("<Rnummer>"),
					"Computer: "+SystemConfig.dieseMaschine.toString(),
					"Benutzer: "+Reha.aktUser,
					SystemConfig.hmEmailIntern.get("Username"),
					"Fehler-Mail");
		}
	}
	
}

class RgebHintergrund extends JXPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ImageIcon hgicon;
	int icx,icy;
	AlphaComposite xac1 = null;
	AlphaComposite xac2 = null;		
	public RgebHintergrund(){
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
			g2d.setComposite(this.xac1);
			g2d.drawImage(hgicon.getImage(), (getWidth()/2)-icx , (getHeight()/2)-icy,null);
			g2d.setComposite(this.xac2);
		}
	}
}