package entlassBerichte;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import oOorgTools.OOTools;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import com.jgoodies.forms.layout.CellConstraints;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.GrayColor;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;

import events.PatStammEventClass;
import events.PatStammEventListener;
import events.RehaEvent;
import events.RehaEventClass;
import events.RehaEventListener;

import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.text.ITextDocument;

import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemTools.Colors;
import systemTools.JRtaCheckBox;
import systemTools.JRtaComboBox;
import systemTools.JRtaTextField;

import RehaInternalFrame.JArztInternal;
import RehaInternalFrame.JGutachtenInternal;

public class EBerichtPanel extends JXPanel implements RehaEventListener,PropertyChangeListener,TableModelListener,KeyListener,FocusListener,ActionListener, MouseListener{
	JGutachtenInternal jry = null;
	public EBerichtPanel thisClass = null;
	public JXPanel seite1;
	public JXPanel seite2;
	public JXPanel seite3;
	public JXPanel seite4;
	public JTabbedPane ebtab = null;
	JButton[] gutbut = {null,null,null,null,null};
	String[] ktraeger = {"DRV Bund","DRV Baden-Württemberg","DRV Bayern","DRV Berlin","DRV Brandenburg","DRV Bremen",
			"DRV Hamburg","DRV Hessen","DRV Mecklenburg-Vorpommern","DRV Niedersachsen","DRV Rheinland-Pfalz",
			"DRV Saarland","DRV Sachsen","DRV Sachsensen-Anhalt","DRV Schleswig-Holstein","DRV Thüringen","DRV Knappschaft Bahn/See","GKV"};
//	String[] ktraeger = {"DRV Bund","DRV Baden-Württemberg","DRV Knappschaft Bahn/See","DRV Bayer","GKV"};
	public JRtaComboBox cbktraeger = null;
	/**********************/
	public String pat_intern = null;
	public int berichtid = -1;
	public String berichttyp = null;
	public boolean neu = false;
	
	IFrame officeFrame = null;
	RehaEventClass evt = null;
	static ITextDocument document = null;
	
	public JRtaTextField[] barzttf = {null,null,null}; 

	public JRtaTextField[] btf = {  null,null,null,null,null,null,null,null,null,null,
									null,null,null,null,null,null,null,null,null,null,
									null,null,null,null,null,null,null,null,null,null,
									null,null,null,null,null,null,null,null,null,null
	};
	public JRtaComboBox[] bcmb = {  null,null,null,null,null,null,null,null,null,null,
									null,null,null,null,null,null,null,null,null,null,
									null,null,null,null,null,null,null,null,null,null,
									null,null,null,null,null,null,null,null,null,null
	};
	
	public JRtaCheckBox[] bchb = {  null,null,null,null,null,null,null,null,null,null,
									null,null,null,null,null,null,null,null,null,null,
									null,null,null,null,null,null,null,null,null,null,
									null,null,null,null,null,null,null,null,null,null,
									null,null,null,null,null,null,null,null,null,null
	};
	public JTextArea[] 	  bta = {  null,null,null,null,null,null,null,null,null,null};

	public JRtaComboBox[] ktlcmb={  null,null,null,null,null,null,null,null,null,null,
									null,null,null,null,null,null,null,null,null,null,
									null,null,null,null,null,null,null,null,null,null,
									null,null,null,null,null,null,null,null,null,null,
									null,null,null,null,null,null,null,null,null,null
	};
	public JRtaTextField[] ktltfc={ null,null,null,null,null,null,null,null,null,null,
									null,null,null,null,null,null,null,null,null,null,
									null,null,null,null,null,null,null,null,null,null,
									null,null,null,null,null,null,null,null,null,null,
									null,null,null,null,null,null,null,null,null,null
	};

	public JRtaTextField[] ktltfd={ null,null,null,null,null,null,null,null,null,null,
									null,null,null,null,null,null,null,null,null,null,
									null,null,null,null,null,null,null,null,null,null,
									null,null,null,null,null,null,null,null,null,null,
									null,null,null,null,null,null,null,null,null,null
	};
	public JRtaTextField[] ktltfa={ null,null,null,null,null,null,null,null,null,null,
									null,null,null,null,null,null,null,null,null,null,
									null,null,null,null,null,null,null,null,null,null,
									null,null,null,null,null,null,null,null,null,null,
									null,null,null,null,null,null,null,null,null,null
	};
	
	public EBerichtPanel(JGutachtenInternal xjry,String xpat_intern,int xberichtid,String xberichttyp,boolean xneu ){
		setBorder(null);
		
		this.jry = xjry;
		this.pat_intern = xpat_intern;
		this.berichtid = xberichtid;
		this.berichttyp = xberichttyp;
		this.neu = xneu;
		
		thisClass = this;
		
		evt = new RehaEventClass();
		evt.addRehaEventListener((RehaEventListener) this);

		addFocusListener(this);
		Point2D start = new Point2D.Float(0, 0);
	    Point2D end = new Point2D.Float(400,550);
	    float[] dist = {0.0f, 0.75f};
	    Color[] colors = {Color.WHITE,Colors.Gray.alpha(0.15f)};
	    LinearGradientPaint p =  new LinearGradientPaint(start, end, dist, colors);
	    MattePainter mp = new MattePainter(p);
	    setBackgroundPainter(new CompoundPainter(mp));
		setLayout(new BorderLayout());
		
		add(this.getToolbar(),BorderLayout.NORTH);

		if(berichttyp.contains("-Arztbericht")){
			cbktraeger = new JRtaComboBox(ktraeger);
			UIManager.put("TabbedPane.tabsOpaque", Boolean.FALSE);
			UIManager.put("TabbedPane.contentOpaque", Boolean.FALSE);
			ebtab = getEBerichtTab(); 
			add(ebtab,BorderLayout.CENTER);
			UIManager.put("TabbedPane.tabsOpaque", Boolean.TRUE);
			UIManager.put("TabbedPane.contentOpaque", Boolean.TRUE);
			
		}else{
			UIManager.put("TabbedPane.tabsOpaque", Boolean.FALSE);
			UIManager.put("TabbedPane.contentOpaque", Boolean.FALSE);
			ebtab = getNachsorgeTab();
			add(ebtab,BorderLayout.CENTER);
			UIManager.put("TabbedPane.tabsOpaque", Boolean.TRUE);
			UIManager.put("TabbedPane.contentOpaque", Boolean.TRUE);
		}
		
		System.out.println("Bericht von Patient Nr. ="+ this.pat_intern);
		System.out.println("             Bericht ID ="+ this.berichtid);
		System.out.println("             Berichttyp ="+ this.berichttyp);
		System.out.println("          Neuer Bericht ="+ this.neu);
	}
	/******************************************************************/
	private JTabbedPane getEBerichtTab(){
		EBerichtTab ebt = new EBerichtTab(this);
		return ebt.getTab();
	}
	private JTabbedPane getNachsorgeTab(){
		NachsorgeTab nat = new NachsorgeTab(this);
		return nat.getTab();
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tableChanged(TableModelEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
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

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String cmd = arg0.getActionCommand();
		if(cmd.equals("gutsave")){
			
		}
		if(cmd.equals("gutvorschau")){
			
		}
		if(cmd.equals("gutprint")){
			new Thread(){
				public void run(){
					doPrint();					
				}
			}.start();

			
		}
		if(cmd.equals("guttools")){
			
		}
		if(cmd.equals("guttext")){
			if(!neu){
        		InputStream is = SqlInfo.holeStream("bericht2","freitext","berichtid='"+berichtid+"'");
        		document = OOTools.starteWriterMitStream(is, "Vorhandener Bericht");
			}else{
				document = OOTools.starteLeerenWriter();
			}
		}
		
	}
	/***********ab hier Christian's Funktion
	 * 
	 */
	private void doPrint(){
		//name und Pfad der PDF
		String pdfPfad = Reha.proghome+"vorlagen/"+Reha.aktIK+"/EBericht-Seite1-Variante2.pdf";
		PdfWriter writer2 = null;
		PdfCopy writer = null;
		PdfStamper stamper = null;
		
		String test = "23020562S512";
			try {
				String sdatei = "C:/ebericht1.pdf"; 
				BaseFont bf = BaseFont.createFont(BaseFont.COURIER,BaseFont.CP1252,BaseFont.NOT_EMBEDDED);
				PdfReader reader = new PdfReader (pdfPfad);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				//stamper = new PdfStamper(reader,baos);
				stamper = new PdfStamper(reader,new  FileOutputStream(sdatei));
				PdfContentByte cb = stamper.getOverContent(1);

				cb.beginText();
				//Einfacher Text
				cb.moveText(83.f, 760.f);
				cb.setFontAndSize(bf,12);
				cb.setCharacterSpacing(6.9f);
				cb.showText(test);
				Float[][] poswert = {{0.0f,0.0f},
						{291.83f,759.80f},
						{62.33f,745.17f},
						
				};
				
				cb.endText();
				for(int i = 1; i < 2;i++){
					cb.beginText();
					cb.setFontAndSize(bf,12);
					cb.moveText(poswert[i][0], poswert[i][1]);
					
					//cb.setCharacterSpacing(5.7f);
					cb.showText(btf[i].getText());
					cb.endText();
				}
				cb.setCharacterSpacing(1.0f);

				// nur zur Überprüfung der Positionen
				/*
				cb.setColorStroke(new GrayColor(0.2f)) ;
				cb.moveTo(80, 494);
				cb.lineTo(80, 530);
				cb.lineTo(318, 530);
				cb.lineTo(318, 494);	
				cb.lineTo(80, 494);
				cb.stroke();
				*/
				//cb.closePathFillStroke();
				// Ende PosPrüfung

				float xstart = 82.f;
				float xend = 282.f;
				float ystartunten = 495.f;
				float ystartoben = 530.f;
				float yschritt = 35.f;
				ColumnText ct = null;
				Phrase ph = null;
				float zaehler = 1.f;
				for(int i = 0;i < 5; i++){
					ct = new ColumnText(cb);
					ct.setSimpleColumn(xstart, ystartunten,xend,ystartoben,8,Element.ALIGN_TOP);
					ph = new Phrase();
					ph.setFont(FontFactory.getFont("Courier",9,Font.PLAIN));
					ph.add(bta[i].getText().trim());
					ct.addText(ph);
					ct.go();
					
					ystartunten -= (yschritt+zaehler);
					ystartoben = (ystartunten+yschritt);
				}
				stamper.setFormFlattening(true);
				stamper.close();
				
				
				// AdobeReader starten
				final String xdatei = sdatei;
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						Process process = new ProcessBuilder(SystemConfig.hmFremdProgs.get("AcrobatReader"),"",xdatei).start();
					       InputStream is = process.getInputStream();
					       InputStreamReader isr = new InputStreamReader(is);
					       BufferedReader br = new BufferedReader(isr);
					       String line;
					       while ((line = br.readLine()) != null) {
					         //System.out.println(line);
					       }
					       is.close();
					       isr.close();
					       br.close();
						return null;
					}
				}.execute();
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		 {
			
		}
	}
	/***********Ende Christian's Funktion
	 * 
	 */
	
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
	public void dokumentSchliessen(){
		try{
			document.close();			
		}catch(Exception ex){
			
		}

	}
	
	public JToolBar getToolbar(){
		JToolBar jtb = new JToolBar();
		jtb.setOpaque(false);
		jtb.setRollover(true);
		jtb.setBorder(null);
		jtb.setOpaque(false);

		gutbut[0] = new JButton();
		gutbut[0].setIcon(SystemConfig.hmSysIcons.get("save"));
		gutbut[0].setToolTipText("Gutachten speichern");
		gutbut[0].setActionCommand("gutsave");
		gutbut[0].addActionListener(this);		
		jtb.add(gutbut[0]);
/*
		gutbut[4] = new JButton();
		gutbut[4].setIcon(new ImageIcon(SystemConfig.hmSysIcons.get("ooowriter").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
		gutbut[4].setToolTipText("Freitext starten");
		gutbut[4].setActionCommand("guttext");
		gutbut[4].addActionListener(this);		
		jtb.add(gutbut[4]);
*/		
		jtb.addSeparator(new Dimension(30,0));
		
		gutbut[1] = new JButton();
		gutbut[1].setIcon( SystemConfig.hmSysIcons.get("vorschau") );
		gutbut[1].setToolTipText("Druckvorschau");
		gutbut[1].setActionCommand("gutvorschau");
		gutbut[1].addActionListener(this);		
		jtb.add(gutbut[1]);

		gutbut[2] = new JButton();
		gutbut[2].setIcon(SystemConfig.hmSysIcons.get("print"));
		gutbut[2].setToolTipText("Alle Ausfertigungen des Gutachten drucken");
		gutbut[2].setActionCommand("gutprint");
		gutbut[2].addActionListener(this);		
		jtb.add(gutbut[2]);
		
		jtb.addSeparator(new Dimension(30,0));
		
		gutbut[3] = new JButton();
		gutbut[3].setIcon(SystemConfig.hmSysIcons.get("tools"));
		gutbut[3].setToolTipText("Werkzeugkasten für Gutachten");
		gutbut[3].setActionCommand("guttools");
		gutbut[3].addActionListener(this);		
		jtb.add(gutbut[3]);
		for(int i = 0; i < 5;i++){
			//gutbut[i].setEnabled(false);
		}
		return jtb;
	}
	@Override
	public void RehaEventOccurred(RehaEvent evt) {
		// TODO Auto-generated method stub
		System.out.println(evt);
		if(evt.getDetails()[0].contains("GutachtenFenster")){
			if(evt.getDetails()[1].equals("#SCHLIESSEN")){
				dokumentSchliessen();
				this.evt.removeRehaEventListener((RehaEventListener)this);
			}
		}
	}


}
