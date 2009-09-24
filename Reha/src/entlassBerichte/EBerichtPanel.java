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
			cbktraeger = new JRtaComboBox(SystemConfig.vGutachtenEmpfaenger);
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
			new Thread(){
				public void run(){
					Reha.thisClass.progressStarten(true);
					doVorschau();					
				}
			}.start();
			
		}
		if(cmd.equals("gutprint")){

			
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
	/***********ab hier Christian's Funktionen
	 * 
	 */
	private void setzeText(PdfContentByte cb,float x,float y,float space,BaseFont bf,int fsize,String text){
		cb.beginText();
		cb.moveText(x, y);
		cb.setFontAndSize(bf,fsize);
		cb.setCharacterSpacing(space);
		cb.showText(text);
		cb.endText();
	}
	private Float[] getFloats(float fx, float fy, float fcSpace){
		Float [] fret = {0.f,0.f,0.f};
		fret[0] = (595.0f/210.0f)*fx;
		fret[1] = (842.0f/297.0f)*fy;
		fret[2] = fcSpace;
		return fret;
	}
	private String macheDatum2Acht(String datum){
		String sret = "";
		try{
			String[] split = datum.split("\\.");
			sret = split[0]+split[1]+split[2].substring(2);
		}catch(Exception ex){
			sret = "";
		}
		return sret;
	}
	private void doVorschau(){
		//name und Pfad der PDF
		String pdfPfad = Reha.proghome+"vorlagen/"+Reha.aktIK+"/EBericht-Seite1-Variante2.pdf";
		PdfWriter writer2 = null;
		PdfCopy writer = null;
		PdfStamper stamper = null;
		
		String test = "23020562S512";
			try {
				// Geschiss bis die bestehende PDF eingelesen und gestampt ist
				String sdatei = "C:/ebericht1.pdf"; 
				BaseFont bf = BaseFont.createFont(BaseFont.COURIER,BaseFont.CP1252,BaseFont.NOT_EMBEDDED);
				PdfReader reader = new PdfReader (pdfPfad);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				//stamper = new PdfStamper(reader,baos);
				stamper = new PdfStamper(reader,new  FileOutputStream(sdatei));
				// Ende des Geschiss
				
				PdfContentByte cb = stamper.getOverContent(1);
				Float [] pos = {null,null,null};
				float fy0 =  0.25f;
				float fy1 =  6.9f;
				// Hier die Positionierung für das obere Gedönse
								//        0RV-Nr.
				Float[][] poswert1 = {getFloats(29.30f,268.25f,fy1),
						//      1Kennzeich                2Name                           3Geburtst
							getFloats(103.30f,268.25f,fy1),getFloats(24.5f,260.0f,fy0),getFloats(24.5f,251.5f,fy1),
							//   4Strasse                             5PLZ                               6Ort
							getFloats(24.5f,243.0f,fy0),getFloats(24.5f,234.5f,fy1), getFloats(51.5f,234.5f,fy0),
							//  7VersichertenName              8MSNR                     9BNR
							getFloats(24.5f,226.0f,fy0),getFloats(131.25f,268.25f,fy1),getFloats(156.25f,268.25f,fy1)
				};
				// Jetzt die Positionen abarbeiten
				String text = "";
				for(int i = 0; i < 10;i++){
					if(i==3){
						text = macheDatum2Acht(btf[i].getText().trim());
					}else{
						text = btf[i].getText();	
					}
					setzeText(cb,poswert1[i][0], poswert1[i][1],poswert1[i][2],bf,12,text);
				}
				//IK des Berichterstellers
				pos = getFloats(131.25f,225.75f,fy1);
				setzeText(cb,pos[0], pos[1],pos[2],bf,12,btf[28].getText());
				//Abteilung-Nr.
				pos = getFloats(181.50f,225.75f,fy1);
				setzeText(cb,pos[0], pos[1],pos[2],bf,12,(btf[10].getText().trim().equals("") ? "2300" : btf[10].getText().trim() ) );
				/* 
				 * Ab hier für Christian am Donnerstag
				 */
				// Jetzt kommen die Felder ab stationär, ganztägig ambulant etc. bis letztes Feld = Körpergröße
				Float[][] poswert2 = {	getFloats(29.25f,218.0f,fy1),getFloats(67.10f,218.0f,fy1),
										getFloats(29.25f,196.5f,fy1),getFloats(67.10f,196.5f,fy1),
										getFloats(29.25f,205.30f,fy1),getFloats(67.10f,205.30f,fy1)	};
				for(int i = 11; i < 17;i++){
					text = btf[i].getText().trim();
					if(! text.equals(".  .")){
						text = macheDatum2Acht(text);
						setzeText(cb,poswert2[i-11][0], poswert2[i-11][1],poswert2[i-11][2],bf,12,text);						
					}
				}	
				/*******************************************************************/
				// Jetzt die Diagnoseschlüssel
				//                             Diag1                            Diag2
				Float[][] poswert3 = {	getFloats(113.10f,176.0f,fy1),getFloats(113.10f,162.95f,fy1),
				//                             Diag3                            Diag4
										getFloats(113.10f,150.00f,fy1),getFloats(113.10f,137.50f,fy1),
				//                             Diag5						
										getFloats(113.10f,124.40f,fy1)};
				for(int i = 17; i < 22;i++){
					text = btf[i].getText().trim();
					setzeText(cb,poswert3[i-17][0], poswert3[i-17][1],poswert3[i-17][2],bf,12,text);						

				}	
				

				/*******************************************************************/
				// Dann die CheckBoxen auswerten
				float xfs = 0.0f;
				float yfs = 0.0f;
				bf = BaseFont.createFont(BaseFont.HELVETICA,BaseFont.CP1252,BaseFont.NOT_EMBEDDED);
				Float[][] poswert4 =
					//	 		  0                          1                                             					
						{getFloats(26.60f+xfs,77.75f+yfs,fy0),getFloats(54.60f+xfs,77.75f+yfs,fy0),
					//	          2                          3
						 getFloats(26.60f+xfs,69.25f+yfs,fy0),getFloats(54.60f+xfs,69.25f+yfs,fy0),
					//            4                          5                       
						 getFloats(26.60f+xfs,60.85f+yfs,fy0),getFloats(54.60f+xfs,60.85f+yfs,fy0),
					//			  6                          7                                         
						 getFloats(90.15f+xfs,77.75f+yfs,fy0),getFloats(120.50f+xfs,77.75f+yfs,fy0),
					//	          8							 9  
						 getFloats(90.15f+xfs,69.25f+yfs,fy0),getFloats(120.50f+xfs,69.25f+yfs,fy0),
					//	         10                         11               
						 getFloats(90.15f+xfs,60.85f+yfs,fy0),getFloats(120.50f+xfs,60.85f+yfs,fy0),
					//	         12                         13                                                
						getFloats(145.75f+xfs,77.75f+yfs,fy0),getFloats(176.20f+xfs,77.75f+yfs,fy0),
					//	         14                         15
						getFloats(145.75f+xfs,69.25f+yfs,fy0),getFloats(176.20f+xfs,69.25f+yfs,fy0),
					//           16
						getFloats(145.75f+xfs,60.85f+yfs,fy0)};
				for(int i = 0; i <17; i++){
					if(bchb[i].isSelected()){
						text = "X";
						setzeText(cb,poswert4[i][0], poswert4[i][1],poswert4[i][2],bf,14,text);
					}
				}
				bf = BaseFont.createFont(BaseFont.COURIER,BaseFont.CP1252,BaseFont.NOT_EMBEDDED);
				/*************************************************************************/
				// Gewichte und Körpergröße             1                              2                            3
				Float[][] poswert5 = {getFloats(29.25f,116.50f,fy1),getFloats(29.25f,108.25f,fy1),getFloats(29.25f,99.80f,fy1) }; 
				for(int i = 22; i < 25;i++){
					text = btf[i].getText().trim();
					setzeText(cb,poswert5[i-22][0], poswert5[i-22][1],poswert5[i-22][2],bf,12,text);						
				}
				/*************************************************************************/
				// Zuletzt das Unterschriftsdatum = btf[27].getText()
				text = btf[27].getText().trim();
				Float [] fsign = getFloats(24.25f,27.60f,fy1);
				if(! text.equals(".  .")){
					text = macheDatum2Acht(text);
					setzeText(cb,fsign[0], fsign[1],fsign[2],bf,12,text);						
				}
				/*************************************************************************/				
				/***********Jetzt der Mehrzeilige Text der Diagnosen 1-5******************/
				cb.setCharacterSpacing(0.5f);
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
					ct.setSimpleColumn(xstart, ystartunten,xend,ystartoben,8,Element.ANCHOR);
					ph = new Phrase();
					ph.setFont(FontFactory.getFont("Courier",9,Font.PLAIN));
					ph.add(bta[i].getText().trim());
					ct.addText(ph);
					ct.go();
					
					ystartunten -= (yschritt+zaehler);
					ystartoben = (ystartunten+yschritt);
				}
				// Erläuternungen Box
				Float[] empfunten =  getFloats(24.50f,40.0f,0.5f);
				Float[] empfoben =  getFloats(201.00f,51.25f,0.5f);
				ct = new ColumnText(cb);
				ct.setSimpleColumn(empfunten[0], empfunten[1],empfoben[0],empfoben[1],9,Element.ALIGN_BASELINE);
				ph = new Phrase();
				ph.setFont(FontFactory.getFont("Courier",10,Font.PLAIN));
				ph.add(bta[5].getText().trim());
				ct.addText(ph);
				ct.go();
				
				stamper.setFormFlattening(true);
				stamper.close();
				
				
				// AdobeReader starten
				final String xdatei = sdatei;
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						try{
						Process process = new ProcessBuilder(SystemConfig.hmFremdProgs.get("AcrobatReader"),"",xdatei).start();
					       InputStream is = process.getInputStream();
					       InputStreamReader isr = new InputStreamReader(is);
					       BufferedReader br = new BufferedReader(isr);
					       String line;
							Reha.thisClass.progressStarten(false);
					       while ((line = br.readLine()) != null) {
					         //System.out.println(line);
					       }
					       is.close();
					       isr.close();
					       br.close();
						}catch(Exception ex){
							Reha.thisClass.progressStarten(false);
						}
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
