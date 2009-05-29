package patientenFenster;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.WindowListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import events.RehaTPEventListener;
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
import hauptFenster.Reha;
import systemEinstellungen.SystemConfig;
import systemTools.JRtaTextField;

public class RezeptGebuehren extends RehaSmartDialog implements RehaTPEventListener,WindowListener{
	boolean nurkopie;
	boolean aushistorie;
	public JButton okknopf;
	public JRtaTextField gegeben;
	public JLabel rueckgeld;
	public JCheckBox direktdruck;
	public RezeptGebuehren(boolean kopie,boolean historie,Point pt){
		super(null,"RezeptGebuehr");		

		this.nurkopie = kopie;
		this.aushistorie = historie;
		
		PinPanel pinPanel = new PinPanel();
		pinPanel.setName("RezeptGebuehr");
		pinPanel.getGruen().setVisible(false);
		setPinPanel(pinPanel);
		getSmartTitledPanel().setTitle("Rezept-Gebühr");	
		setSize(200,250);
		setPreferredSize(new Dimension(200,250));
		getSmartTitledPanel().setPreferredSize(new Dimension (200,250));
		setPinPanel(pinPanel);
		getSmartTitledPanel().setContentContainer(getGebuehren());
		getSmartTitledPanel().getContentContainer().setName("RezeptGebuehr");
	    setName("RezeptGebuehr");
		setModal(true);
	    pack();
	    Point lpt = new Point(pt.x-75,pt.y+30);
	    setLocation(lpt);
	    setVisible(true);
		new Thread(){
			public void run(){
			//	rezGebDrucken();
			}
		}.start();
	}
/****************************************************/	
	/*
	public JButton okknopf;
	public JRtaTextField gegeben;
	public JLabel rueckgeld;
	public JCheckBox direktdruck;
	*/

	private JPanel getGebuehren(){
		FormLayout lay = new FormLayout("2dlu,right:max(40dlu;p),2dlu,40dlu,2dlu","5dlu,p,2dlu,p,2dlu,p,5dlu");
		PanelBuilder pb = new PanelBuilder(lay);
		CellConstraints cc = new CellConstraints();
		pb.getPanel().setBackground(Color.WHITE);
		direktdruck = new JCheckBox("Quittung direk drucken");
		pb.add(direktdruck,cc.xyw(2,2,3));
		pb.addLabel("Rezeptgebühren",cc.xy(2,4));
		JLabel lab = new JLabel(SystemConfig.hmAdrRDaten.get("<Rendbetrag>"));
		lab.setFont(new Font("Tahoma",Font.BOLD,14));
		pb.add(lab,cc.xy(4,4));
		pb.addLabel("gegeben",cc.xy(2,6));
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
        docdescript.setHidden(false);
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
			System.out.println("Platzhalter-Name = "+placeholderDisplayText);
			placeholders[i].getTextRange().setText(SystemConfig.hmAdrRDaten.get(placeholderDisplayText));
		}
		//document.getFrame().getXFrame().getContainerWindow().setVisible(true);
	}
}
