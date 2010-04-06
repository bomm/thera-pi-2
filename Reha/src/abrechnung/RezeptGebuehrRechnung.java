package abrechnung;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import oOorgTools.OOTools;

import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;

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

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import systemEinstellungen.SystemConfig;
import systemTools.JRtaTextField;
import terminKalender.DatFunk;
import dialoge.DragWin;
import dialoge.PinPanel;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;

public class RezeptGebuehrRechnung extends JXDialog implements FocusListener, ActionListener, MouseListener, KeyListener,RehaTPEventListener{
	/**
	* 
	*/
	private static final long serialVersionUID = 7491942845791659861L;
	private JXTitledPanel jtp = null;
	private MouseAdapter mymouse = null;
	private PinPanel pinPanel = null;
	private JXPanel content = null;
	private RehaTPEventClass rtp = null;
	private int rueckgabe;
	private JRtaTextField[] tfs = {null,null,null,null,null};
	private JButton[] but = {null,null};
	private HashMap<String,String> hmRezgeb = null;
	DecimalFormat dcf = new DecimalFormat ( "#########0.00" );
	String rgnrNummer;
	public RezeptGebuehrRechnung(JXFrame owner,String titel,int rueckgabe,HashMap<String,String> hmRezgeb){
		super(owner, (JComponent)Reha.thisFrame.getGlassPane());
		this.setUndecorated(true);
		this.setName("RezgebDlg");
		this.rueckgabe = rueckgabe;
		this.hmRezgeb = hmRezgeb;
		this.jtp = new JXTitledPanel();
		this.jtp.setName("RezgebDlg");
		this.mymouse = new DragWin(this);
		this.jtp.addMouseListener(mymouse);
		this.jtp.addMouseMotionListener(mymouse);
		this.jtp.setContentContainer(getContent());
		this.jtp.setTitleForeground(Color.WHITE);
		this.jtp.setTitle(titel);
		this.pinPanel = new PinPanel();
		this.pinPanel.getGruen().setVisible(false);
		this.pinPanel.setName("RezgebDlg");
		this.jtp.setRightDecoration(this.pinPanel);
		this.setContentPane(jtp);
		this.setModal(true);
		this.setResizable(false);
		this.rtp = new RehaTPEventClass();
		this.rtp.addRehaTPEventListener((RehaTPEventListener) this);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				setzeFelder();
			}
		});
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				setzeFocus();
			}
		});


	}
	private void setzeFocus(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				//but[0].requestFocus();
				tfs[3].requestFocus();
			}
		});
	}
	private void setzeFelder(){
		tfs[0].setText(hmRezgeb.get("<rgreznum>"));
		tfs[1].setText(hmRezgeb.get("<rgdatum>"));
		tfs[2].setText(hmRezgeb.get("<rgbetrag>"));
		tfs[3].setText(hmRezgeb.get("<rgpauschale>"));
		tfs[4].setText(hmRezgeb.get("<rgbehandlung>"));
	}
	
	private JXPanel getContent(){
		content = new JXPanel(new BorderLayout());
		content.add(getFields(),BorderLayout.CENTER);
		content.add(getButtons(),BorderLayout.SOUTH);
		content.addKeyListener(this);
		return content;
	}
	private JXPanel getFields(){
		JXPanel pan = new JXPanel();
		//                                1           2             3             4     5     6                7
		FormLayout lay = new FormLayout("5dlu,fill:0:grow(0.5),right:max(40dlu;p),5dlu,60dlu,fill:0:grow(0.5),5dlu",
				//1          2         3   4  5  6   7  8   9  10  11  12               13
				"5dlu,fill:0:grow(0.5),p,3dlu,p,3dlu,p,3dlu,p,3dlu, p,fill:0:grow(0.5),5dlu");
		pan.setLayout(lay);
		CellConstraints cc = new CellConstraints();
		pan.setOpaque(false);
		JLabel lab = new JLabel("Rezeptnummer");
		pan.add(lab,cc.xy(3,3));
		tfs[0] = new JRtaTextField("GROSS",true);
		pan.add(tfs[0],cc.xy(5,3));
		lab = new JLabel("Rezeptdatum");
		pan.add(lab,cc.xy(3,5));
		tfs[1] = new JRtaTextField("DATUM",true);
		pan.add(tfs[1],cc.xy(5,5));
		lab = new JLabel("Rezeptgebühr");
		lab.setForeground(Color.RED);
		pan.add(lab,cc.xy(3,7));
		tfs[2] = new JRtaTextField("FL",true,"6.2","RECHTS");
		tfs[2].setupFormat(2);
		tfs[2].setDValueFromS("0,00");
		pan.add(tfs[2],cc.xy(5,7));
		lab = new JLabel("Bearbeitungsgebühr");
		lab.setForeground(Color.RED);
		pan.add(lab,cc.xy(3,9));
		tfs[3] = new JRtaTextField("FL",true,"6.2","RECHTS");
		tfs[3].setDValueFromS("0,00");
		tfs[3].setupFormat(2);
		pan.add(tfs[3],cc.xy(5,9));
		lab = new JLabel("Behandlungen");
		pan.add(lab,cc.xy(3,11));
		tfs[4] = new JRtaTextField("NIX",true);
		pan.add(tfs[4],cc.xy(5,11));
		return pan;
	}
	private JXPanel getButtons(){
		JXPanel pan = new JXPanel();
		pan.setOpaque(false);
		FormLayout lay = new FormLayout("5dlu,fill:0:grow(0.5),50dlu,10dlu,50dlu,fill:0:grow(0.5),5dlu",
				//1          2         3   4  5  6   7  8   9  10  11  12
				"5dlu,fill:0:grow(0.5),p,fill:0:grow(0.5),5dlu");
		pan.setLayout(lay);
		CellConstraints cc = new CellConstraints();
		pan.add((but[0] = macheBut("Ok","ok")),cc.xy(3,3));
		but[0].addKeyListener(this);
		pan.add((but[1] = macheBut("abbrechen","abbrechen")),cc.xy(5,3));
		but[1].addKeyListener(this);
		return pan;
	}
	private JButton macheBut(String titel,String cmd){
		JButton but = new JButton(titel);
		but.setName(cmd);
		but.setActionCommand(cmd);
		but.addActionListener(this);
		return but;
	}	
	private void doRgRechnungPrepare(){
		double rezgeb = 	Double.parseDouble(tfs[2].getText().replace(",","."))+
		Double.parseDouble(tfs[3].getText().replace(",","."));
		hmRezgeb.put("<rggesamt>",dcf.format(rezgeb));
		hmRezgeb.put("<rgbetrag>",dcf.format(Double.parseDouble(tfs[2].getText().replace(",","."))));
		hmRezgeb.put("<rgpauschale>",dcf.format(Double.parseDouble(tfs[3].getText().replace(",","."))));
		hmRezgeb.put("<rgnr>","RGR-"+Integer.toString(sqlTools.SqlInfo.erzeugeNummer("rgrnr")));
		String url = Reha.proghome+"vorlagen/"+Reha.aktIK+"/RezeptgebuehrRechnung.ott";
		try {
			officeStarten(url);
		} catch (OfficeApplicationException e) {
			e.printStackTrace();
		} catch (NOAException e) {
			e.printStackTrace();
		} catch (TextException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		buchungStarten();
		FensterSchliessen("dieses");
	}
	private void buchungStarten(){
		
		StringBuffer buf = new StringBuffer();
		buf.append("insert into rgaffaktura set ");
		buf.append("rnr='"+hmRezgeb.get("<rgnr>")+"', ");
		buf.append("reznr='"+hmRezgeb.get("<rgreznum>")+"', ");
		buf.append("pat_intern='"+hmRezgeb.get("<rgpatintern>")+"', ");
		buf.append("rgesamt='"+hmRezgeb.get("<rggesamt>").replace(",",".")+"', ");
		buf.append("roffen='"+hmRezgeb.get("<rggesamt>").replace(",",".")+"', ");
		buf.append("rgbetrag='"+hmRezgeb.get("<rgbetrag>").replace(",",".")+"', ");
		buf.append("rpbetrag='"+hmRezgeb.get("<rgpauschale>").replace(",",".")+"', ");		
		buf.append("rdatum='"+DatFunk.sDatInSQL(DatFunk.sHeute())+"'");
		sqlTools.SqlInfo.sqlAusfuehren(buf.toString());		
	}
	private void officeStarten(String url) throws OfficeApplicationException, NOAException, TextException, DocumentException{
		IDocumentService documentService = null;
		Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		//System.out.println("Starte Datei -> "+url);
		if(!Reha.officeapplication.isActive()){
			Reha.starteOfficeApplication();
		}

		documentService = Reha.officeapplication.getDocumentService();

        IDocumentDescriptor docdescript = new DocumentDescriptor();
       	docdescript.setHidden(true);
        docdescript.setAsTemplate(true);
		IDocument document = null;

		document = documentService.loadDocument(url,docdescript);
		ITextDocument textDocument = (ITextDocument)document;
		/**********************/
		OOTools.druckerSetzen(textDocument, SystemConfig.hmAbrechnung.get("hmgkvrechnungdrucker"));
		/**********************/
		ITextFieldService textFieldService = textDocument.getTextFieldService();
		ITextField[] placeholders = null;

		placeholders = textFieldService.getPlaceholderFields();
		String placeholderDisplayText = "";

		for (int i = 0; i < placeholders.length; i++) {
			placeholderDisplayText = placeholders[i].getDisplayText().toLowerCase();
			Set<?> entries = hmRezgeb.entrySet();
		    Iterator<?> it = entries.iterator();
			    while (it.hasNext()) {
			      Map.Entry entry = (Map.Entry) it.next();
			      if(((String)entry.getKey()).toLowerCase().equals(placeholderDisplayText)){
			    	  try{
			    		  
			    	  }catch(com.sun.star.uno.RuntimeException ex){
			    		  System.out.println("Fehler bei "+placeholderDisplayText);
			    	  }
			    	  placeholders[i].getTextRange().setText(((String)entry.getValue()));		    		  

			    	  break;
			      }
			    }
		}
		if(SystemConfig.hmAbrechnung.get("hmallinoffice").equals("1")){
			textDocument.getFrame().getXFrame().getContainerWindow().setVisible(true);
		}else{
			for(int i = 0; i < 2; i++){
				textDocument.print();
			}
			textDocument.close();
			textDocument = null;
		}
		
		
	}
	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String cmd = arg0.getActionCommand();
		if(cmd.equals("abbrechen")){
			this.rueckgabe = -1;
			FensterSchliessen("dieses");
		}else{
			this.rueckgabe = 0;
			doRgRechnungPrepare();
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode()==27){
			this.rueckgabe = -1;
			FensterSchliessen("dieses");
			return;
		}
		if(arg0.getKeyCode()==10){
			if(((JComponent)arg0.getSource()) instanceof JButton){
				if(((JComponent)arg0.getSource()).getName().equals("abbrechen")){
					this.rueckgabe = -1;
					FensterSchliessen("dieses");
					return;
				}else if(((JComponent)arg0.getSource()).getName().equals("ok")){
					this.rueckgabe = 0;
					doRgRechnungPrepare();
				}
			}
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
	public void rehaTPEventOccurred(RehaTPEvent evt) {
		FensterSchliessen("dieses");
		
	}
	public void FensterSchliessen(String welches){
		this.jtp.removeMouseListener(this.mymouse);
		this.jtp.removeMouseMotionListener(this.mymouse);
		this.content.removeKeyListener(this);
		for(int i = 0; i < 2;i++){
			but[i].removeActionListener(this);
			but[i].removeKeyListener(this);
			but[i] = null;
		}
		this.mymouse = null; 
		if(this.rtp != null){
			this.rtp.removeRehaTPEventListener((RehaTPEventListener) this);
			this.rtp=null;			
		}
		this.pinPanel = null;
		setVisible(false);
		this.dispose();
	}	

}
