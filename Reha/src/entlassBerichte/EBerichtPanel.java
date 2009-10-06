package entlassBerichte;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import oOorgTools.OOTools;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import patientenFenster.Gutachten;
import patientenFenster.PatGrundPanel;

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
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

import events.PatStammEventClass;
import events.PatStammEventListener;
import events.RehaEvent;
import events.RehaEventClass;
import events.RehaEventListener;

import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.filter.PDFFilter;
import ag.ion.bion.officelayer.filter.RTFFilter;
import ag.ion.bion.officelayer.text.ITextDocument;

import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemTools.Colors;
import systemTools.FileTools;
import systemTools.JRtaCheckBox;
import systemTools.JRtaComboBox;
import systemTools.JRtaTextField;
import terminKalender.datFunk;

import RehaInternalFrame.JArztInternal;
import RehaInternalFrame.JGutachtenInternal;

public class EBerichtPanel extends JXPanel implements ChangeListener,RehaEventListener,PropertyChangeListener,TableModelListener,KeyListener,FocusListener,ActionListener, MouseListener{
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
	public String empfaenger = null;
	public boolean neu = false;
	public boolean jetztneu = false;
	public String tempPfad = Reha.proghome+"temp/"+Reha.aktIK+"/";
	public String vorlagenPfad = Reha.proghome+"vorlagen/"+Reha.aktIK+"/";
	public String[] rvVorlagen = {null,null,null,null};
	EBerichtTab ebt;	
	IFrame officeFrame = null;
	RehaEventClass evt = null;
	
	String[][] tempDateien = {null,null,null,null,null};
	
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
	public JRtaComboBox[] acmb = {  null,null,null};
			
	
	public EBerichtPanel(JGutachtenInternal xjry,String xpat_intern,int xberichtid,String xberichttyp,boolean xneu,String xempfaenger ){
		setBorder(null);
		
		this.jry = xjry;
		this.pat_intern = xpat_intern;
		this.berichtid = xberichtid;
		this.berichttyp = xberichttyp;
		this.empfaenger = xempfaenger;
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

		if(berichttyp.contains("E-Bericht") || berichttyp.contains("LVA-A") || berichttyp.contains("BfA-A") 
				|| berichttyp.contains("GKV-A")){
			cbktraeger = new JRtaComboBox(SystemConfig.vGutachtenEmpfaenger);
			UIManager.put("TabbedPane.tabsOpaque", Boolean.FALSE);
			UIManager.put("TabbedPane.contentOpaque", Boolean.FALSE);
			ebtab = getEBerichtTab();
			ebtab.setSelectedIndex(0);
			add(ebtab,BorderLayout.CENTER);
			ebtab.addChangeListener(this);
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
		System.out.println("             Empfaenger ="+ this.empfaenger);
		System.out.println("          Neuer Bericht ="+ this.neu);
		rvVorlagen[0]  = vorlagenPfad+"EBericht-Seite1-Variante2.pdf";
		rvVorlagen[1]  = vorlagenPfad+"EBericht-Seite2-Variante2.pdf";
		rvVorlagen[2]  = vorlagenPfad+"EBericht-Seite3-Variante2.pdf";
		rvVorlagen[3]  = vorlagenPfad+"EBericht-Seite4-Variante2.pdf";
	}
	/******************************************************************/
	private JTabbedPane getEBerichtTab(){
		ebt = new EBerichtTab(this);
		return ebt.getTab();
	}
	private JTabbedPane getNachsorgeTab(){
		NachsorgeTab nat = new NachsorgeTab(this);
		return nat.getTab();
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		// TODO Auto-generated method stub
            // Get current tab
		//System.out.println(arg0);
		JTabbedPane pane = (JTabbedPane)arg0.getSource();
        int sel = pane.getSelectedIndex();
        //System.out.println("Tab mit Index "+sel+" wurde selektiert");
        if(sel==2){
        	new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					if(ebt.getTab3().framegetrennt){
						System.out.println("OOTab ist!!!! selektiert und getrennt deshalb BaueSeite");
						ebt.getTab3().baueSeite();
					}else{
						System.out.println("OOTab ist!!!! selektiert und nicht!!!!getrennt deshalb nur tempTextSpeichern");
						ebt.getTab3().tempTextSpeichern();
					}
			
					return null;
				}
        	}.execute();
        }else{
        }

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
			if(this.neu){
				doSpeichernNeu();	
				JOptionPane.showMessageDialog(null,"Entlassbericht wurde gespeichert");
			}else{
				doSpeichernAlt();
				JOptionPane.showMessageDialog(null,"Entlassbericht wurde gespeichert");
			}
			
		}
		if(cmd.equals("gutvorschau")){
			new Thread(){
				public void run(){
					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() throws Exception {
							Reha.thisClass.progressStarten(true);
							ebtab.setSelectedIndex(0);
							if(((String)cbktraeger.getSelectedItem()).contains("DRV ")){
								try {
									 
							        int sel = ebtab.getSelectedIndex();
									if(document.isModified()){
										System.out.println("in getrennt.....");
										if(sel != 2){
												ebt.getTab3().tempTextSpeichern();
												document.close();
										}else{
											if(document.isModified()){											
												ebt.getTab3().tempTextSpeichern();
											}
										}

									}else{
										System.out.println("in nicht!!!!!getrennt.....");
										if(sel != 2){
											ebt.getTab3().tempTextSpeichern();
											document.close();
										}else{
											ebt.getTab3().tempTextSpeichern();
										}
									}
									
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								doRVVorschau("DRV Bund","EDV");

							}
							return null;
						}
					}.execute();
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
	private void doSpeichernAlt(){
		Reha.thisClass.progressStarten(true);
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		StringBuffer buf = new StringBuffer();
		buf.append("update bericht2 set ");
		for(int i = 0; i < 28;i++){
			if(!btf[i].getRtaType().equals("DATUM")){
				buf.append(btf[i].getName()+"='"+btf[i].getText()+"', ");				
			}else{
				if(!btf[i].getText().trim().equals(".  .")){
					buf.append(btf[i].getName()+"='"+datFunk.sDatInSQL(btf[i].getText())+"', ");
				}
			}

		}
		for(int i = 0; i < 3;i++){
			buf.append(barzttf[i].getName()+"='"+barzttf[i].getText()+"', ");
		}
		for(int i = 0; i < 44;i++){
			buf.append(bchb[i].getName()+"='"+(bchb[i].isSelected() ? "1" : "0")+"', ");
		}
		for(int i = 0; i < 8;i++){
			buf.append(bta[i].getName()+"='"+bta[i].getText()+"', ");
		}
		for(int i = 0; i < 20;i++){
			if(i < 19){
				buf.append(bcmb[i].getName()+"='"+bcmb[i].getSelectedItem()+"', ");				
			}else{
				buf.append(bcmb[i].getName()+"='"+bcmb[i].getSelectedItem()+"' ");
			}
		}
		buf.append( " where berichtid = '"+berichtid+"'");
		SqlInfo.sqlAusfuehren(buf.toString());
		/******************************************************************************************/
		buf = new StringBuffer();
		buf.append("Update bericht2ktl set " ); 
		for(int i = 0;i < 50;i++){
			buf.append(ktlcmb[i].getName()+"='"+ktlcmb[i].getValue()+"', ");
			buf.append(ktltfc[i].getName()+"='"+ktltfc[i].getText()+"', ");
			buf.append(ktltfd[i].getName()+"='"+ktltfd[i].getText()+"', ");
			buf.append(ktltfa[i].getName()+"='"+ktltfa[i].getText()+"', ");			
		}
		for(int i = 8; i < 10;i++){
			if(i == 8){
				buf.append(bta[i].getName()+"='"+bta[i].getText()+"', ");				
			}else{
				buf.append(bta[i].getName()+"='"+bta[i].getText()+"'");				
			}
		}
		buf.append( " where berichtid = '"+berichtid+"'");
		//System.out.println(buf.toString());
		SqlInfo.sqlAusfuehren(buf.toString());
		
		ebt.getTab3().textSpeichernInDB(true);
		if(!jetztneu){
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			String empf = (String) cbktraeger.getSelectedItem();
			Gutachten.gutachten.aktualisiereGutachten(datFunk.sHeute(),(empf.contains("DRV") ? "DRV E-Bericht" : "GKV E-Bericht"),empf,"Reha-Arzt",berichtid,pat_intern);
			Reha.thisClass.progressStarten(false);
		}else{
			jetztneu = false;
		}
	}
	private void doSpeichernNeu(){
		try{
			Reha.thisClass.progressStarten(true);
			int nummer = SqlInfo.erzeugeNummer("bericht");
			berichtid = nummer;
			String empf = (String) cbktraeger.getSelectedItem();
			String btype = (empf.contains("DRV") ? "DRV E-Bericht" : "GKV E-Bericht");
			String cmd = "insert into berhist set berichtid='"+berichtid+"', erstelldat='"
			+datFunk.sDatInSQL(datFunk.sHeute())+"', berichttyp='"+btype+"', "+
			"verfasser='Reha-Arzt', empfaenger='"+empf+"', pat_intern='"+pat_intern+"'";
			SqlInfo.sqlAusfuehren(cmd);
			cmd = "insert into bericht2 set berichtid='"+berichtid+"', pat_intern='"+pat_intern+"'";
			SqlInfo.sqlAusfuehren(cmd);
			cmd = "insert into bericht2ktl set berichtid='"+berichtid+"', pat_intern='"+pat_intern+"'";
			SqlInfo.sqlAusfuehren(cmd);
			jetztneu = true; // ganz wichtig
			neu = false;
			System.out.println("Historie- und Bericht wurden angelegt");
			doSpeichernAlt();
			System.out.println("Nach Speichern alt");
			Gutachten.gutachten.neuesGutachten(new Integer(berichtid).toString(),
					btype,"Reha-Arzt",datFunk.sHeute() ,empf, pat_intern);
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			Reha.thisClass.progressStarten(false);
		}catch(Exception ex){
			ex.printStackTrace();
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
	private float rechneX(float fx){
		return (595.0f/210.0f)*fx;
	}
	private float rechneY(float fy){
		return (842.0f/297.0f)*fy;
	}

	private Float[] getFloats(float fx, float fy, float fcSpace){
		Float [] fret = {0.f,0.f,0.f};
		fret[0] = (595.0f/210.0f)*fx;
		fret[1] = (842.0f/297.0f)*fy;
		fret[2] = fcSpace;
		return fret;
	}
	private String macheDatum2Sechs(String datum){
		String sret = "";
		try{
			String[] split = datum.split("\\.");
			sret = split[0]+split[1]+split[2].substring(2);
		}catch(Exception ex){
			sret = "";
		}
		return sret;
	}
	private void doRVVorschau(String ausfertigung,String bereich){
		//name und Pfad der PDF
		String pdfPfad = rvVorlagen[0];
		//PdfWriter writer2 = null;
		//PdfCopy writer = null;
		
		PdfStamper stamper = null;
		PdfStamper stamper2 = null;
		String test = "23020562S512";
		// Zu Beginn sicherstellen daß die OO.org PDF produziert wird.
			try {
				
				File ft = new File(tempPfad+"EBfliesstext.pdf");
				if(! ft.exists()){
					System.out.println("In tempTextSpeichern!********************");
					try {
						ebt.getTab3().tempTextSpeichern();
						long zeit = System.currentTimeMillis();
						Reha.thisClass.progressStarten(true);
						while(!ebt.getTab3().pdfok){
							Thread.sleep(50);
							if(( System.currentTimeMillis() - zeit) > 15000){
								break;
							}
						}
						if(!ebt.getTab3().pdfok){
							JOptionPane.showMessageDialog(null,"Der Fliesstext des Gutachten konnte nicht erstellt werden");							
						}

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				// Geschiss bis die bestehende PDF eingelesen und gestampt ist
				tempDateien[0] = new String[]{Reha.proghome+"temp/"+Reha.aktIK+"/EB1"+System.currentTimeMillis()+".pdf"};
				//String sdatei = "C:/ebericht1.pdf"; 
				BaseFont bf = BaseFont.createFont(BaseFont.COURIER,BaseFont.CP1252,BaseFont.NOT_EMBEDDED);
				PdfReader reader = new PdfReader (pdfPfad);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				//stamper = new PdfStamper(reader,baos);
				stamper = new PdfStamper(reader,new  FileOutputStream(tempDateien[0][0]));
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
						text = macheDatum2Sechs(btf[i].getText().trim());
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
				 * 
				 */
				// Jetzt kommen die Felder ab stationär, ganztägig ambulant etc. bis letztes Feld = Körpergröße
				Float[][] poswert2 = {	getFloats(29.25f,218.0f,fy1),getFloats(67.10f,218.0f,fy1),
										getFloats(29.25f,196.5f,fy1),getFloats(67.10f,196.5f,fy1),
										getFloats(29.25f,205.30f,fy1),getFloats(67.10f,205.30f,fy1)	};
				for(int i = 11; i < 17;i++){
					text = btf[i].getText().trim();
					if(! text.equals(".  .")){
						text = macheDatum2Sechs(text);
						setzeText(cb,poswert2[i-11][0], poswert2[i-11][1],poswert2[i-11][2],bf,12,text);						
					}
				}	
				/*******************************************************************/
				// Jetzt die Diagnoseschlüssel
				//                             Diag1                            Diag2
				Float[][] poswert3 = {	getFloats(113.10f,175.70f,fy1),getFloats(113.10f,162.95f,fy1),
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
				// Das Unterschriftsdatum = btf[27].getText() + den Ort
				text = btf[27].getText().trim();
				Float [] fsign = getFloats(24.25f,27.60f,fy1);
				if(! text.equals(".  .")){
					text = macheDatum2Sechs(text);
					setzeText(cb,fsign[0], fsign[1],fsign[2],bf,12,text);						
				}
				Float [] fort = getFloats(61.25f,27.60f,fy0);
				setzeText(cb,fort[0], fort[1],fort[2],bf,12,SystemConfig.sGutachtenOrt);
				/*************************************************************************/
				// Jetzt die ComboBoxen abarbeiten
				Float[][] poswert6 = {
					//   0=Enlassform                      1	
					getFloats(115.65f,213.65f,fy1),getFloats(163.45f,213.65f,fy1),
					//   2=Diag1Teil                       3                                  4
					getFloats(143.35f,175.70f,fy1),getFloats(155.75f,175.70f,fy1),getFloats(168.25f,175.70f,fy1),
					//   5=Diag2Teil				       6                                  7
					getFloats(143.35f,162.95f,fy1),getFloats(155.75f,162.95f,fy1),getFloats(168.25f,162.95f,fy1),
					//   8=Diag3Teil 					   9								 10
					getFloats(143.35f,150.00f,fy1),getFloats(155.75f,150.00f,fy1),getFloats(168.25f,150.00f,fy1),
					//  11=Diag4Teil					  12								 13 
					getFloats(143.35f,137.50f,fy1),getFloats(155.75f,137.50f,fy1),getFloats(168.25f,137.50f,fy1),
					//  14=Diag5Teil					  15								 16
					getFloats(143.35f,124.40f,fy1),getFloats(155.75f,124.40f,fy1),getFloats(168.25f,124.40f,fy1),
					//  17=Ursache der..				  18								 19
					getFloats(77.15f,116.50f,fy1),getFloats(128.35f,116.50f,fy1),getFloats(166.35f,116.50f,fy1)
					
				};
				for(int i = 0; i < poswert6.length;i++){
					text = ((String)bcmb[i].getSelectedItem()).trim();
					setzeText(cb,poswert6[i][0], poswert6[i][1],poswert6[i][2],bf,12,text);						
				}
				/***********Jetzt der mehrzeilige Text der Diagnosen 1-5******************/
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
				/*****************************************************************/
				// Der Block rechts oben mit der Einrichtungsadresse
				StringBuffer reha = new StringBuffer();
				int lang = SystemConfig.vGutachtenAbsAdresse.size();
				for(int i = 0; i < lang;i++){
					reha.append(SystemConfig.vGutachtenAbsAdresse.get(i)+(i < (lang-1) ? "\n" : ""));
				}
				// Reha-Einrichtung
				Float[] rehaunten =  getFloats(131.25f,242.0f,0.5f);
				Float[] rehaoben =  getFloats(199.00f,264.0f,0.5f);
				ct = new ColumnText(cb);
				ct.setSimpleColumn(rehaunten[0], rehaunten[1],rehaoben[0],rehaoben[1],11,Element.ALIGN_BASELINE);
				ph = new Phrase();
				ph.setFont(FontFactory.getFont("Helvectica",10,Font.PLAIN));
				ph.add(reha.toString());
				ct.addText(ph);
				ct.go();
				
				/*****************************************************************/
				stamper.setFormFlattening(true);
				stamper.close();
				reader.close();
				/*****************************************************************/
				boolean geklappt = false;
				try{
				//Seite 2 öffnen
				pdfPfad = rvVorlagen[1];
				tempDateien[1] = new String[]{Reha.proghome+"temp/"+Reha.aktIK+"/EB2"+System.currentTimeMillis()+".pdf"};
				reader = new PdfReader (pdfPfad);
				stamper2 = new PdfStamper(reader,new  FileOutputStream(tempDateien[1][0]));
				geklappt = doSeite2(stamper2);
				}catch(Exception ex){
					ex.printStackTrace();
				}

				geklappt = doSeite3();
				
				if(geklappt){
		
				
				}
				geklappt = doSeiteTest();
				if(!geklappt){
				}
				// AdobeReader starten
				//final String xdatei =  "C:/RehaVerwaltung/temp/510841109/freitext.pdf";
				//final String xdatei =  tempDateien[3][0];//sdatei;
				final String xdatei =  tempDateien[4][0];
						new SwingWorker<Void,Void>(){
							@Override
							protected Void doInBackground() throws Exception {
								try{
									Reha.thisClass.progressStarten(true);
									/*
									try{
							       			//pdfBoxTest(xdatei);
									       pdfBoxTest(tempPfad+"EBfliesstext.pdf");
									}catch(Exception ex){
										ex.printStackTrace();
									}
									*/
									Process process = new ProcessBuilder(SystemConfig.hmFremdProgs.get("AcrobatReader"),"",xdatei).start();
									InputStream is = process.getInputStream();
									InputStreamReader isr = new InputStreamReader(is);
									BufferedReader br = new BufferedReader(isr);
									String line;
									 Reha.thisClass.progressStarten(false);							       
							       while ((line = br.readLine()) != null) {
							         System.out.println("Lade Adobe "+line);
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
			
	}
	/*************************************/
	public void pdfBoxTest(String file) {
	    PDDocument doc=null;
	    try {
	        doc = PDDocument.load(file);
	        doc.print();
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    } finally {
	        if (doc != null) {
	            try {
	                doc.close();
	            } catch (IOException ex1) {
	                ex1.printStackTrace();
	            }
	        }
	    }
	}
	private boolean doSeite3(){
		tempDateien[2] = new String[]{Reha.proghome+"temp/"+Reha.aktIK+"/EB3"+System.currentTimeMillis()+".pdf"};
		String pdfPfad = rvVorlagen[2];
		PdfReader reader;
		try {
			BaseFont bf = BaseFont.createFont(BaseFont.COURIER,BaseFont.CP1252,BaseFont.NOT_EMBEDDED);
			String text = "";
			Float [] pos = {null,null,null};
			float fy0 =  0.25f;
			float fy1 =  6.9f;
			Float[] xseite = getFloats(188.25f,278.5f,fy0);
			
			Document docktl = new Document(PageSize.A4);
			PdfCopy cop = new PdfCopy(docktl,new FileOutputStream(tempDateien[2][0]));
			docktl.open();
			int zugabe = 0;
			for(int i = 0;i < 2;i++){
				ByteArrayOutputStream baos  = new ByteArrayOutputStream();
				PdfReader readeroriginal = new PdfReader(rvVorlagen[2]);
				PdfStamper stamper2 = new PdfStamper(readeroriginal,baos);
				PdfContentByte cb = stamper2.getOverContent(1);


				
				
				pos = getFloats(24.50f,268.00f,fy0);
				setzeText(cb,pos[0], pos[1],pos[2],bf,12,btf[2].getText());
				pos = getFloats(171.50f,268.00f,fy1);
				setzeText(cb,pos[0], pos[1],pos[2],bf,12,macheDatum2Sechs(btf[3].getText()));
				int seite = i+1;

				cb.beginText();
				cb.moveText(xseite[0], xseite[1]);
				cb.setFontAndSize(bf,11);
				cb.setCharacterSpacing(xseite[2]);
				cb.showText(new Integer(seite).toString());
				cb.endText();
				
				//                    0x-start,  1y-start,  2höhe  3y-ende  4xCode   5xDauer  6xAnzahl
				Float[] startwerte = {30.0f ,    251.f      ,8.5f, 149.f,  153.75f,   178.5f  ,191.75f};
				for(int y=0;y < 25;y++){
					if(ktlcmb[y+zugabe].getSelectedIndex() > 0){
						cb.setCharacterSpacing(0.25f);
						schreibeKTLText(y,y+zugabe,startwerte,cb);
						schreibeKTLCode(y,y+zugabe,startwerte,cb);
					}else{
						break;
					}
				}

				cb.setCharacterSpacing(0.25f);
				schreibeKTLErlaeut(zugabe,cb);
				// ab hier das Stamper und Copy Gedönse....				
				stamper2.close();
				cop.addPage(cop.getImportedPage(new PdfReader(baos.toByteArray()),1));
				baos.close();
				readeroriginal.close();
				zugabe = 25;
				if(ktlcmb[25].getSelectedIndex()<=0){
					break;
				}
				

			}
			docktl.close();	
			
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	private void schreibeKTLErlaeut(int ierlaeut,PdfContentByte cb){
		ColumnText ct = null;
		Phrase ph = null;
		float xstart = rechneX(25.f);
		float ystartunten = rechneY(27.0f);
		float xende = rechneX(195.0f);
		float ystartoben = rechneY(42.f);
		ct = new ColumnText(cb);
		ct.setSimpleColumn(xstart, ystartunten,xende,ystartoben,8,Element.ANCHOR);
		ph = new Phrase();
		ph.setFont(FontFactory.getFont("Courier",9,Font.PLAIN));
		if(ierlaeut > 0){
			ph.add((String)this.bta[9].getText());			
		}else{
			ph.add((String)this.bta[8].getText());
		}
		ct.addText(ph);
		try {
			ct.go();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private void schreibeKTLCode(int position,int ktlpos,Float[] startwerte,PdfContentByte cb){
		//                    0x-start,  1y-start,  2höhe  3y-ende  4xCode   5xDauer  6xAnzahl
		//Float[] startwerte = {30.0f ,    251.f      ,8.5f, 149.f,  154.75f,   181.f  ,191.f};
		float stretch = 6.9f;
		float ystartunten = rechneY(startwerte[1]-(new Float(position) * startwerte[2])+0.5f);
		float xcode = rechneX(startwerte[4]);
		float xdauer = rechneX(startwerte[5]);
		float xanzahl = rechneX(startwerte[6]);	
		BaseFont bf;
		try {
			bf = BaseFont.createFont(BaseFont.COURIER,BaseFont.CP1252,BaseFont.NOT_EMBEDDED);
			setzeText(cb,xcode, ystartunten,stretch,bf,12,ktltfc[ktlpos].getText());
			setzeText(cb,xdauer, ystartunten,stretch,bf,12,ktltfd[ktlpos].getText());
			if(ktltfa[ktlpos].getText().trim().length()==1 ){
				setzeText(cb,xanzahl, ystartunten,stretch,bf,12,"0"+ktltfa[ktlpos].getText());
			}else{
				setzeText(cb,xanzahl, ystartunten,stretch,bf,12,ktltfa[ktlpos].getText());				
			}

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void schreibeKTLText(int position,int ktlpos,Float[] startwerte,PdfContentByte cb){
		ColumnText ct = null;
		Phrase ph = null;
		float zaehler = 1.f;
 
		//                     x-start,  y-start,  höhe   x-ende
		//Float[] startwerte = {28.0f ,  251.f      ,8.5f, 152.f};

		float xstart = rechneX(startwerte[0]+1.5f);
		float ystartunten = startwerte[1]-(new Float(position) * startwerte[2])-2.f;
		float xende = rechneX(startwerte[3]);
		float ystartoben = rechneY(ystartunten+ startwerte[2]);
		ystartunten = rechneY(ystartunten);
		ct = new ColumnText(cb);
		ct.setSimpleColumn(xstart, ystartunten,xende,ystartoben,8,Element.ALIGN_BOTTOM);
		
		ph = new Phrase();
		ph.setFont(FontFactory.getFont("Courier",9,Font.PLAIN));
		ph.add((String)ktlcmb[ktlpos].getSelectedItem());
		ct.addText(ph);
		try {
			ct.go();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*************************************/
	private boolean doSeite2(PdfStamper stamper2){
		
		try {
			PdfContentByte cb = stamper2.getOverContent(1);
			BaseFont bf = BaseFont.createFont(BaseFont.COURIER,BaseFont.CP1252,BaseFont.NOT_EMBEDDED);
			String text = "";
			Float [] pos = {null,null,null};
			float fy0 =  0.25f;
			float fy1 =  6.9f;
			pos = getFloats(24.50f,268.00f,fy0);
			setzeText(cb,pos[0], pos[1],pos[2],bf,12,btf[2].getText());
			pos = getFloats(171.50f,268.00f,fy1);
			setzeText(cb,pos[0], pos[1],pos[2],bf,12,macheDatum2Sechs(btf[3].getText()));
			pos = getFloats(78.00f,248.00f,fy0);
			setzeText(cb,pos[0], pos[1],pos[2],bf,9,btf[25].getText());
			Float[] xseite = getFloats(188.25f,278.5f,fy0);
			int seite = 1;
			cb.beginText();
			cb.moveText(xseite[0], xseite[1]);
			cb.setFontAndSize(bf,11);
			cb.setCharacterSpacing(xseite[2]);
			cb.showText(new Integer(seite).toString());
			cb.endText();


			/* Für ältere Berichte < 01.01.2008 noch den Berufsklassenschlüssel einbauen
			pos = getFloats(78.00f,247.00f,fy0);
			setzeText(cb,pos[0], pos[1],pos[2],bf,12,macheDatum2Sechs(btf[26].getText()));
			*/
			
			/************
			 * FloatWerte von Christian
			 * 
			 */


			Float[][] poswert1 = 
			//	  17                                  18                          19
			{getFloats(90.1f,238.0f,fy0),getFloats(128.25f,238.0f,fy0),getFloats(171.1f,238.0f,fy0),
					//   20                                 21                               22                           23
					getFloats(77.4f,204.75f,fy0),getFloats(112.8f,204.75f,fy0),getFloats(156.0f,204.75f,fy0),getFloats(186.4f,204.75f,fy0),	
					//       24                            25                          26                       27        
					getFloats(28.75f,192.0f,fy0),getFloats(46.7f,192.0f,fy0),getFloats(64.7f,192.0f,fy0),getFloats(87.7f,192.0f,fy0),
					//         28                          29                              30                       31         
					getFloats(105.3f,192.0f,fy0),getFloats(123.2f,192.0f,fy0),getFloats(146.0f,192.0f,fy0),getFloats(163.6f,192.0f,fy0),
					//         32
					getFloats(181.4f,192.0f,fy0),
					//          33                      34                               35
					getFloats(77.3f,183.55f,fy0),getFloats(112.8f,183.55f,fy0),getFloats(155.9f,183.55f,fy0),
					//          36                        37                         38
					getFloats(36.4f,175.0f,fy0),getFloats(36.4f,153.9f,fy0),getFloats(36.4f,141.3f,fy0),
					//          39                        40
					getFloats(36.4f,128.85f,fy0),getFloats(36.40f,116.15f,fy0),
					//           41                        42                       43
					getFloats(90.23f,22.8f,fy0),getFloats(128.23f,22.8f,fy0),getFloats(171.13f,22.8f,fy0),

			};
			bf = BaseFont.createFont(BaseFont.HELVETICA,BaseFont.CP1252,BaseFont.NOT_EMBEDDED);
			text = "X";    // < 44
			for(int i = 17; i < 44;i++){
				if(bchb[i].isSelected()){
					setzeText(cb,poswert1[i-17][0], poswert1[i-17][1],poswert1[i-17][2],bf,14,text);
				}
			}
			ColumnText ct = null;
			Phrase ph = null;
			//bf = BaseFont.createFont(BaseFont.COURIER,BaseFont.CP1252,BaseFont.NOT_EMBEDDED);
			Float[] rehaunten =  getFloats(31.00f,34.0f,0.5f);
			Float[] rehaoben =  getFloats(195.00f,104.0f,0.5f);
			ct = new ColumnText(cb);
			ct.setSimpleColumn(rehaunten[0], rehaunten[1],rehaoben[0],rehaoben[1],9,Element.ANCHOR);
			ph = new Phrase();
			ph.setFont(FontFactory.getFont("Courier",9,Font.PLAIN));
			ph.add(bta[7].getText());
			ct.addText(ph);
			ct.go();

			stamper2.close();
			
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
		
	}
	/*
	public boolean doSeite5(PdfStamper stamper4) throws IOException, JRException{
		System.out.println("Starte JasperSoft");
		Map params = new HashMap<String,String>();
		params.put("patient","Steinhilber, Jürgen");
		params.put("geboren","020562");
		params.put("berichtid","'"+this.berichtid+"'");
		JasperReport jasperReport;

	
		JasperPrint jasperPrint 
        = JasperFillManager.fillReport("C:/RehaVerwaltung/reports/510841109/report3.jasper",  
                                       params, 
                                       Reha.thisClass.conn);
		JasperExportManager.exportReportToPdfFile(jasperPrint,
        "C:/RehaVerwaltung/temp/510841109/freitext.pdf");
		System.out.println("Fertig mit JasperSoft");
			return false;
	}
	*/
	
	/**************
	 * 
	 * 
	 * 
	 * @return
	 * @throws IOException
	 * @throws DocumentException
	 */
	public boolean doSeiteTest() throws IOException, DocumentException{
		InputStream isb = null;
		tempDateien[3] = new String[]{tempPfad+"EB4"+System.currentTimeMillis()+".pdf"};
		String pdfPfad = rvVorlagen[3];
		Document docgesamt = new Document(PageSize.A4);
		File ft = new File(tempPfad+"EBfliesstext.pdf");
		if(! ft.exists()){
			/*
			try {
				long zeit = System.currentTimeMillis();
				boolean freitextok = true;
				while(!ebt.getTab3().pdfok){
					Thread.sleep(50);
					if(System.currentTimeMillis() - zeit > 3000){
						JOptionPane.showMessageDialog(null,"Fehler in der Zusammenstellung des Freitextes");
						freitextok = false;
						break;
					}
				}
				if(!freitextok){
					JOptionPane.showMessageDialog(null,"Fehler in der Zusammenstellung des Freitextes");
					return false;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
		}
		PdfReader reader = new PdfReader(tempPfad+"EBfliesstext.pdf");

		int seiten = reader.getNumberOfPages();
		System.out.println("Insgesamt Seiten Fließtext = "+seiten);
	
		PdfImportedPage page2;		
		
		tempDateien[4] = new String[]{tempPfad+"EBGesamt"+System.currentTimeMillis()+".pdf"};
		PdfCopy cop = new PdfCopy(docgesamt,new FileOutputStream(tempDateien[4][0]));
		docgesamt.open();
		
		ByteArrayOutputStream bpage1 = new ByteArrayOutputStream();
		PdfReader readerPage1 = new PdfReader (tempDateien[0][0]);
		PdfStamper stampPage1 = new PdfStamper(readerPage1,bpage1);
		PdfContentByte cbPage1 = stampPage1.getOverContent(1);
		schreibeVonBis(cbPage1,seiten);
		stampPage1.close();
		
		cop.addPage(cop.getImportedPage(new PdfReader(bpage1.toByteArray()),1));
		cop.addPage(cop.getImportedPage(new PdfReader(tempDateien[1][0]),1));
		
		PdfReader ktlreader = new PdfReader(tempDateien[2][0]);
		int ktlseiten = ktlreader.getNumberOfPages();
		for(int i = 1; i <= ktlseiten;i++){
			cop.addPage(cop.getImportedPage(ktlreader,i));
		}
		
		ByteArrayOutputStream bvorlage;
		Float[] xy = getFloats(17.f,13.f,0.f);
		for(int i = 1; i <= seiten; i++){
			//PdfReader rvorlage = new PdfReader(vorlage);
			PdfReader rvorlage = new PdfReader(pdfPfad);
			bvorlage = new ByteArrayOutputStream();
			PdfStamper stamper = new PdfStamper(rvorlage,bvorlage);
			PdfContentByte cb2 = stamper.getOverContent(1);
			page2 = cb2.getPdfWriter().getImportedPage(reader, i);
			cb2.addTemplate(page2,xy[0],xy[1]);
			try{
				schreibeKopf(cb2,i);				
			}catch(Exception ex){
				ex.printStackTrace();
			}
			stamper.setFormFlattening(true);
			stamper.close();
			PdfReader komplett = new PdfReader(bvorlage.toByteArray());
			cop.addPage(cop.getImportedPage(komplett,1));
			bvorlage.close();
		}
		

		docgesamt.close();
		reader.close();
		ktlreader.close();
		
		return false;
	}
	private void schreibeKopf(PdfContentByte cb,int seite) throws DocumentException, IOException{
		float fy0 =  0.25f;
		float fy1 =  7.1f;

		Float[] xname = getFloats(24.f,268.f,fy0);
		Float[] xgeboren = getFloats(171.5f,268.f,fy1);
		Float[] xseite = getFloats(188.25f,278.5f,fy0);
		BaseFont bf = BaseFont.createFont(BaseFont.COURIER,BaseFont.CP1252,BaseFont.NOT_EMBEDDED); 
		cb.beginText();
		cb.moveText(xname[0], xname[1]);
		cb.setFontAndSize(bf,12);
		cb.setCharacterSpacing(xname[2]);
		cb.showText(btf[2].getText());
		cb.endText();
		cb.beginText();
		cb.moveText(xgeboren[0], xgeboren[1]);
		cb.setFontAndSize(bf,12);
		cb.setCharacterSpacing(xgeboren[2]);
		cb.showText(this.macheDatum2Sechs(btf[3].getText()));
		cb.endText();
		cb.beginText();
		cb.moveText(xseite[0], xseite[1]);
		cb.setFontAndSize(bf,11);
		cb.setCharacterSpacing(xseite[2]);
		cb.showText(new Integer(seite).toString());
		cb.endText();

	}
	private void schreibeVonBis(PdfContentByte cb,int seiten) throws DocumentException, IOException{
		Float[] xseite = getFloats(174.0f,27.0f,0.f);
		BaseFont bf = BaseFont.createFont(BaseFont.COURIER,BaseFont.CP1252,BaseFont.NOT_EMBEDDED); 
		cb.beginText();
		cb.moveText(xseite[0], xseite[1]);
		cb.setFontAndSize(bf,11);
		cb.setCharacterSpacing(xseite[2]);
		cb.showText(new Integer(seiten).toString());
		cb.endText();
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
		System.out.println("GutachtenFenster wird geschlossen.......");
		if(evt.getDetails()[0].contains("GutachtenFenster")){
			if(evt.getDetails()[1].equals("#SCHLIESSEN")){
				dokumentSchliessen();
				if(document.isOpen()){
					document.close();
				}
				this.evt.removeRehaEventListener((RehaEventListener)this);
				FileTools.delFileWithSuffixAndPraefix(new File(tempPfad), "EB", ".pdf");
			}
		}
	}


}
