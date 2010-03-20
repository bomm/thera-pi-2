package patientenFenster;

import hauptFenster.Reha;
import javax.swing.text.SimpleAttributeSet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.rtf.RTFEditorKit;


import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import RehaInternalFrame.JPatientInternal;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.windows.WindowsTabbedPaneUI;
import com.mysql.jdbc.PreparedStatement;

import events.PatStammEvent;
import events.PatStammEventListener;

import sqlTools.ExUndHop;
import systemEinstellungen.SystemConfig;
import systemTools.Colors;
import systemTools.JCompTools;
import systemTools.JRtaTextField;


public class HauptPanel extends JXPanel implements ComponentListener{

	public JXPanel oben;
	public JXPanel mittelinksoben;
	public JXPanel mittelinksunten;
	public JXPanel rechts;
	public MouseListener ml = null;
	public StyledEditorKit rdefedit;
	public int m_xStart = -1;
	public int m_xFinish = -1;
 
	CompoundPainter cp = null;
	MattePainter mp = null;
	LinearGradientPaint p = null;
	
	public HauptPanel getInstance(){
		return this;
	}
	public HauptPanel(PatGrundPanel eltern){
		super();
		//thisClass = this;
		setOpaque(true);
		/*
		Point2D start = new Point2D.Float(0, 0);
	     Point2D end = new Point2D.Float(Reha.thisClass.patpanel.getWidth(),150);
	     float[] dist = {0.0f, 0.75f};
	     Color[] colors = {Color.WHITE,Colors.PiOrange.alpha(0.5f)};

	     p =
	         new LinearGradientPaint(start, end, dist, colors);
	     mp = new MattePainter(p);
	     cp = new CompoundPainter(mp);
	     setBackgroundPainter(cp);
	     */
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("HauptPanel"));
		setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		
		ml = new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				if(arg0.getSource() instanceof JLabel){
					if(((JComponent)arg0.getSource()).getName().equals("sackgasse")){
						System.out.println("Sackgasse angeklickt");
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				//arg0.getComponent().requestFocus();
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
			
		};
		addComponentListener(this);
		//setPreferredSize(new Dimension(5000,5000));
		setBorder(BorderFactory.createEmptyBorder());
		FormLayout lay = new FormLayout("fill:0:grow(0.33),fill:0:grow(0.66)",
				"40dlu,fill:0:grow(1.00)");
		CellConstraints cc = new CellConstraints();
		setLayout(lay);
		add(getSymbole(eltern),cc.xy(1,1));
		add(getFliessText(eltern),cc.xy(1,2));
		add(getTabs(eltern),cc.xywh(2, 1, 1,2));		
			revalidate();
	}
	
	private JXPanel getSymbole(PatGrundPanel eltern){
		mittelinksoben = new JXPanel(new BorderLayout());
		mittelinksoben.setOpaque(false);
		mittelinksoben.addFocusListener(eltern.getFocusListener());
		 JXPanel dummy = new JXPanel(new FlowLayout());
		 dummy.setOpaque(false);
		 dummy.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 5));
		 eltern.imglabs[0] = new JLabel("");
		 //Reha.thisClass.patpanel.imglabs[0].setIcon(new ImageIcon(Reha.proghome+"icons/achtung.gif"));
		 dummy.add(eltern.imglabs[0]);
		 eltern.imglabs[1] = new JLabel("");
		 //jlbl.setIcon(new ImageIcon(Reha.proghome+"icons/nogo.gif"));
		 dummy.add(eltern.imglabs[1]);
		 eltern.imglabs[2] = new JLabel("");
		 //jlbl.setIcon(new ImageIcon(Reha.proghome+"icons/sackgasse.gif"));
		 eltern.imglabs[2].setName("sackgasse");
		 eltern.imglabs[2].addMouseListener(ml);
		 dummy.add(eltern.imglabs[2]);
		 eltern.imglabs[3] = new JLabel("");		 
		 dummy.add(eltern.imglabs[3]);
		 eltern.imglabs[4] = new JLabel("");
		 dummy.add(eltern.imglabs[4]);		 
		 JScrollPane jscr = new JScrollPane();
		 jscr.setBorder(null);
		 jscr.setViewportBorder(null);
		 jscr.setOpaque(false);
		 jscr.getViewport().setOpaque(false);
		 jscr.setViewportView(dummy);
		 mittelinksoben.add(jscr,BorderLayout.CENTER);
		return mittelinksoben;
	}
	private JXPanel getFliessText(PatGrundPanel eltern){
		mittelinksunten = new JXPanel(new BorderLayout());
		mittelinksunten.setOpaque(false);
		
		mittelinksunten.addFocusListener(eltern.getFocusListener());
	    
		//mittelinksunten.setBorder(BorderFactory.createEmptyBorder());
		mittelinksunten.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		/*
		Point2D start = new Point2D.Float(0, 0);
	     Point2D end = new Point2D.Float(Reha.thisClass.patpanel.getWidth(),150);
	     float[] dist = {0.0f, 0.75f};
	     Color[] colors = {Color.WHITE,Colors.PiOrange.alpha(0.5f)};
	     LinearGradientPaint p =
	         new LinearGradientPaint(start, end, dist, colors);
	     MattePainter mp = new MattePainter(p);
		*/
		
		JToolBar jtoolb = new JToolBar();
		jtoolb.setOpaque(false);
		jtoolb.setBorder(null);
		jtoolb.setBorderPainted(false);
		jtoolb.setRollover(true);
		eltern.memobut[0] = new JButton();
		eltern.memobut[0].setIcon(SystemConfig.hmSysIcons.get("edit"));
		//eltern.memobut[0].setIcon(new ImageIcon(Reha.proghome+"icons/edit.png"));
		eltern.memobut[0].setToolTipText("Langtext editieren");		
		eltern.memobut[0].setActionCommand("kedit");
		eltern.memobut[0].addActionListener(eltern.gplst);
		jtoolb.add(eltern.memobut[0]);
		eltern.memobut[1] = new JButton();
		eltern.memobut[1].setIcon(SystemConfig.hmSysIcons.get("save"));		
		//eltern.memobut[1].setIcon(new ImageIcon(Reha.proghome+"icons/Save_22x22.png"));
		eltern.memobut[1].setToolTipText("Langtext speichern");		
		eltern.memobut[1].setActionCommand("ksave");
		eltern.memobut[1].addActionListener(eltern.gplst);			
		//eltern.memobut[1].addActionListener(this);
		eltern.memobut[1].setEnabled(false);
		jtoolb.add(eltern.memobut[1]);
		jtoolb.addSeparator(new Dimension(40,0));
		eltern.memobut[2] = new JButton();
		eltern.memobut[2].setIcon(SystemConfig.hmSysIcons.get("stop"));
		//Reha.thisClass.patpanel.memobut[2].setIcon(new ImageIcon(Reha.proghome+"icons/process-stop.png"));
		//memobut[2].setIcon(new ImageIcon(Reha.proghome+"icons/cancel.png"));
		eltern.memobut[2].setToolTipText("Langtext bearbeiten abbrechen");		
		eltern.memobut[2].setActionCommand("kbreak");
		eltern.memobut[2].addActionListener(eltern.gplst);
		eltern.memobut[2].setEnabled(false);
		jtoolb.add(eltern.memobut[2]);
		
		eltern.memotab = new JTabbedPane();
		eltern.memotab.setUI(new WindowsTabbedPaneUI());
		eltern.memotab.setOpaque(false);
		eltern.memotab.setBorder(null);
		

		eltern.pmemo[0] = new JTextArea();
		eltern.pmemo[0].setFont(new Font("Courier",Font.PLAIN,11));
		eltern.pmemo[0].setLineWrap(true);
		eltern.pmemo[0].setName("notitzen");
		eltern.pmemo[0].setWrapStyleWord(true);
		eltern.pmemo[0].setEditable(false);
		eltern.pmemo[0].setBackground(Color.WHITE);
		eltern.pmemo[0].setForeground(Color.BLUE);
		JScrollPane span = JCompTools.getTransparentScrollPane(eltern.pmemo[0]);
		//span.setBackground(Color.WHITE);
		span.validate();
		JXPanel jpan = JCompTools.getEmptyJXPanel(new BorderLayout());
		jpan.setOpaque(true);
		JXPanel jpan2 = JCompTools.getEmptyJXPanel(new BorderLayout());
		/*****************/
			//jpan2.setBackgroundPainter(new CompoundPainter(mp));
		jpan2.setBackgroundPainter(Reha.thisClass.compoundPainter.get("FliessText"));
	     jpan2.add(jtoolb);
		jpan.add(jpan2,BorderLayout.NORTH);
		jpan.add(span,BorderLayout.CENTER);
		eltern.memotab.addTab("Notitzen", jpan);
		/******************************************/
		JToolBar jtoolb2 = new JToolBar();
		jtoolb2.setOpaque(false);
		jtoolb2.setBorder(null);
		jtoolb2.setBorderPainted(false);
		jtoolb2.setRollover(true);
		eltern.memobut[3] = new JButton();
		eltern.memobut[3].setIcon(SystemConfig.hmSysIcons.get("edit"));
		eltern.memobut[3].setToolTipText("Langtext editieren");		
		eltern.memobut[3].setActionCommand("kedit2");
		eltern.memobut[3].addActionListener(eltern.gplst);
		jtoolb2.add(eltern.memobut[3]);
		eltern.memobut[4] = new JButton();
		eltern.memobut[4].setIcon(SystemConfig.hmSysIcons.get("save"));
		//eltern.memobut[4].setIcon(new ImageIcon(Reha.proghome+"icons/Save_22x22.png"));
		eltern.memobut[4].setToolTipText("Langtext speichern");		
		eltern.memobut[4].setActionCommand("ksave2");
		eltern.memobut[4].addActionListener(eltern.gplst);
		eltern.memobut[4].setEnabled(false);
		jtoolb2.add(eltern.memobut[4]);
		jtoolb2.addSeparator(new Dimension(40,0));
		eltern.memobut[5] = new JButton();
		eltern.memobut[5].setIcon(SystemConfig.hmSysIcons.get("stop"));
		//eltern.memobut[5].setIcon(new ImageIcon(Reha.proghome+"icons/process-stop.png"));
		//memobut[2].setIcon(new ImageIcon(Reha.proghome+"icons/cancel.png"));
		eltern.memobut[5].setToolTipText("Langtext bearbeiten abbrechen");		
		eltern.memobut[5].setActionCommand("kbreak2");
		eltern.memobut[5].addActionListener(eltern.gplst);
		eltern.memobut[5].setEnabled(false);
		jtoolb2.add(eltern.memobut[5]);
		
		
		eltern.pmemo[1] = new JTextArea();
		eltern.pmemo[1].setFont(new Font("Courier",Font.PLAIN,11));
		eltern.pmemo[1].setLineWrap(true);
		eltern.pmemo[1].setName("notitzen");
		eltern.pmemo[1].setWrapStyleWord(true);
		eltern.pmemo[1].setEditable(false);
		eltern.pmemo[1].setBackground(Color.WHITE);
		eltern.pmemo[1].setForeground(Color.BLUE);
		span = JCompTools.getTransparentScrollPane(eltern.pmemo[1]);
		span.setBackground(Color.WHITE);
		span.validate();
		jpan = JCompTools.getEmptyJXPanel(new BorderLayout());
		jpan.setOpaque(true);
		jpan2 = JCompTools.getEmptyJXPanel(new BorderLayout());
		jpan2.setBackgroundPainter(Reha.thisClass.compoundPainter.get("FliessText"));
	    //jpan2.setBackgroundPainter(new CompoundPainter(mp));
	    jpan2.add(jtoolb2);
		jpan.add(jpan2,BorderLayout.NORTH);
		jpan.add(span,BorderLayout.CENTER);
		eltern.memotab.addTab("Fehldaten", jpan);
		

		
		mittelinksunten.add(eltern.memotab,BorderLayout.CENTER);
		mittelinksunten.revalidate();

		return mittelinksunten;
	}
	private JXPanel getTabs(PatGrundPanel eltern){
		rechts = new JXPanel(new BorderLayout());
		/*
		Point2D start = new Point2D.Float(0, 0);
	     Point2D end = new Point2D.Float(Reha.thisClass.patpanel.getWidth(),150);
	     float[] dist = {0.0f, 0.75f};
	     Color[] colors = {Color.WHITE,Colors.PiOrange.alpha(0.5f)};
	     //Color[] colors = {Color.WHITE,Colors.TaskPaneBlau.alpha(0.5f)};
	     //Color[] colors = {Color.WHITE,getBackground()};
	     LinearGradientPaint p =
	         new LinearGradientPaint(start, end, dist, colors);
	     MattePainter mp = new MattePainter(p);
	     rechts.setBackgroundPainter(new CompoundPainter(mp));
	     */
		rechts.setBackgroundPainter(Reha.thisClass.compoundPainter.get("getTabs"));
		rechts.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		
		eltern.jtab = new JTabbedPane();
		//Reha.thisClass.patpanel.getTab().setBorder(null);
		eltern.jtab.addFocusListener(eltern.getFocusListener());
		try{
			eltern.jtab.setUI(new WindowsTabbedPaneUI());
		}catch(Exception ex){
			// Kein KarstenLentzsch LAF
		}
		JXPanel tabpan = new JXPanel(new BorderLayout());
		tabpan.setBorder(BorderFactory.createEmptyBorder(0,0, 0, 0));
		tabpan.setOpaque(true);
		/*
		Point2D xstart = new Point2D.Float(0, 0);
	     Point2D xend = new Point2D.Float(Reha.thisClass.patpanel.getWidth(),450);
	     float[] xdist = {0.0f, 0.75f};
	     Color[] xcolors = {Colors.PiOrange.alpha(0.25f),Color.WHITE};
	     LinearGradientPaint xp =
	         new LinearGradientPaint(xstart, xend, xdist, xcolors);
	     MattePainter xmp = new MattePainter(xp);
	     tabpan.setBackgroundPainter(new CompoundPainter(xmp));
	    */ 
	     tabpan.setBackgroundPainter(Reha.thisClass.compoundPainter.get("getTabs2"));
	     //JLabel lbl = new JLabel("Hier stehen Tabellen, Tabellen, und noch mal Tabellen....");
	     //lbl.setForeground(Color.WHITE);
	     //tabpan.add(lbl,JLabel.CENTER);
	     /*
	     new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				// TODO Auto-generated method stub
			    Reha.thisClass.patpanel.aktRezept = new AktuelleRezepte();
			    Reha.thisClass.patpanel.aktRezept.validate();
				return null;
			}
	    	 
	     }.execute();
	      */
	     /*
	     final JXPanel xtabpan = tabpan;
	     new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
	    
	     try{
	     */
	    eltern.aktRezept = new AktuelleRezepte(eltern);
	    tabpan.add(eltern.aktRezept);
	    eltern.jtab.addTab(eltern.tabTitel[0]+" - 0", tabpan);
	     
	    eltern.historie = new Historie();
		eltern.jtab.addTab(eltern.tabTitel[1]+" - 0", eltern.historie);
	

		eltern.berichte = new TherapieBerichte();
		eltern.jtab.addTab(eltern.tabTitel[2]+" - 0", eltern.berichte);


		eltern.dokumentation = new Dokumentation();
		eltern.jtab.addTab(eltern.tabTitel[3]+" - 0", eltern.dokumentation);
	
		eltern.gutachten = new Gutachten();
		eltern.jtab.addTab(eltern.tabTitel[4]+" - 0", eltern.gutachten);
		/*
	     }catch(Exception ex){
	    	 ex.printStackTrace();
	     }
		return null;
				}
		    	 
     }.execute();
	*/
		rechts.add(eltern.jtab,BorderLayout.CENTER);
		rechts.revalidate();
		return rechts;
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentResized(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		this.revalidate();
		if(Reha.thisClass.patpanel.ptfield[2] != null){
			int x = getInstance().getWidth();
			int y = getInstance().getHeight();			
			//HauptPanel.thisClass.tf[2].setText("X="+x+"/Y="+y);
		}
	}
	@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
    public void vpaint(Graphics g){
    	/*
    	public JXPanel oben;
    	public JXPanel mittelinksoben;
    	public JXPanel mittelinksunten;
    	public JXPanel rechts;
    	*/

    	Graphics2D g2d = (Graphics2D)g;
    	int x,y,sizex,sizey;
    	Rectangle bound;
    	Point poi;
    	JXPanel[] pans = {oben,mittelinksoben,mittelinksunten,rechts};
    	Color back;
    	for(int i = 1; i <4; i++){
        	
        	poi = pans[i].getLocation();
        	x = poi.x;
        	y = poi.y;
        	
        	sizex = pans[i].getWidth();
        	sizey = pans[i].getHeight();
        	back = pans[i].getBackground();
        	g2d.setColor(back);
        	g2d.fillRect(x, y, sizex, sizey);
        	g2d.setColor(Color.BLACK);
        	g2d.drawString(""+sizex+"-"+sizey, x+(sizex/2), y+(sizey/2));
        	System.out.println("Location = "+x+"-"+y+" Gr��en = "+sizex+"-"+sizey);
    	}
  	

  /*
        g2d.drawRect(100,100,200,120);
        Color s2 = Color.yellow;
        Color e1 = Color.pink;
        GradientPaint gradient1 = new GradientPaint(10,10,s2,30,30,e1,true);
        g2d.setPaint(gradient1);
        g2d.fillRect(99,99,199,119);
  */      
      }

}
/******************************
 * 
 * 
 * Zum Testen
 * 
 */
/************
 * 
 * 
 * @author admin
 *
 */
class AnamneseSpeichern extends SwingWorker<Void,Void>{

	public void init(){
		execute();
	}
	
	protected Void doInBackground() throws Exception {
		Statement stmt = null;;
		ResultSet rs = null;
		PreparedStatement ps = null;
		boolean ret = false;
		int bilder = 0;
				try {
		     		OutputStream baos = new ByteArrayOutputStream();

		     		//InputStream in = new ByteArrayInputStream(null);
					Reha.thisClass.patpanel.jtp.getEditorKit().write(baos, Reha.thisClass.patpanel.m_doc, 0, Reha.thisClass.patpanel.m_doc.getLength());
					InputStream in = new ByteArrayInputStream( ((ByteArrayOutputStream) baos).toByteArray() );					
					  StringBuffer out = new StringBuffer();
					    byte[] b = new byte[Reha.thisClass.patpanel.m_doc.getLength()];
					    for (int n; (n = in.read(b)) != -1;) {
					        out.append(new String(b, 0, n));
					    }
						  //System.out.println("InputStream = "+out.toString());
					Reha.thisClass.patpanel.jtp.getEditorKit().read(in, Reha.thisClass.patpanel.m_doc, 0);

						String select = "Update pat5 set anamnese = ? where pat_intern= ?";
						  ps = (PreparedStatement) Reha.thisClass.conn.prepareStatement(select);
						  ps.setBytes(1, out.toString().getBytes());
						  //ps.setBytes(1,((byte[]) baos));

						  ps.setString(2,  Reha.thisClass.patpanel.aktPatID);
						  ps.execute();

						  
						 // System.out.println("OutputStream = "+new String(((ByteArrayOutputStream) baos).toByteArray()));


						  
						  
						  baos.close();
						  in.close();

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
					if (rs != null) {
						try {
							rs.close();
						} catch (SQLException sqlEx) { // ignore }
							rs = null;
						}
					}	
					if (stmt != null) {
						try {
							stmt.close();
						} catch (SQLException sqlEx) { // ignore }
							stmt = null;
						}
					}
					if (ps != null) {
						try {
							ps.close();
						} catch (SQLException sqlEx) { // ignore }
							stmt = null;
						}
					}
		
		return null;
			
	}
	/************************/
}

