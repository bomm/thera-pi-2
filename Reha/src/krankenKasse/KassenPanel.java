package krankenKasse;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import oOorgTools.OOTools;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.thera_pi.nebraska.crypto.NebraskaCryptoException;
import org.thera_pi.nebraska.crypto.NebraskaDecryptor;
import org.thera_pi.nebraska.crypto.NebraskaEncryptor;
import org.thera_pi.nebraska.crypto.NebraskaFileException;
import org.thera_pi.nebraska.crypto.NebraskaKeystore;
import org.thera_pi.nebraska.crypto.NebraskaNotInitializedException;

import rehaInternalFrame.JKasseInternal;
import sqlTools.ExUndHop;
import sqlTools.SqlInfo;
import stammDatenTools.KasseTools;
import systemEinstellungen.INIFile;
import systemEinstellungen.SystemConfig;
import systemTools.Colors;
import systemTools.FileTools;
import systemTools.JCompTools;
import systemTools.JRtaTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;


public class KassenPanel extends JXPanel implements PropertyChangeListener,TableModelListener,KeyListener,FocusListener,ActionListener, MouseListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8071861793718571588L;
	JButton einlesen = null;
	JXPanel contPan = null;
	public JXTable kassentbl = null;
	public MyKassenTableModel ktblm;
	JRtaTextField suchen = null;
	//public static KassenPanel thisClass = null;
	public int suchestarten = -1;
	public JKasseInternal jry = null;
	public JButton[] memobut = {null,null,null};
	public JButton[] jbut = {null,null,null,null,null};
	//public JRtaTextField[] tf = {null,null,null,null,null,null,null,null,null,null,null,null,null,null,null};
	public JTextArea ta;
	public boolean inMemoEdit = false; 
	private JRtaTextField formularid = new JRtaTextField("NIX",false);
	Vector<String> titel = new Vector<String>() ;
	Vector<String> formular = new Vector<String>();
	int iformular = -1;
	KassenListSelectionHandler kassenlistener = new KassenListSelectionHandler();
	public KassenPanel(JKasseInternal jry,String kid){
		super();
		setBorder(null);
		this.jry = jry;
		//this.thisClass = this;
		addFocusListener(this);
		/*
		Point2D start = new Point2D.Float(0, 0);
	     Point2D end = new Point2D.Float(600,550);
	     float[] dist = {0.0f, 0.75f};
	     Color[] colors = {Color.WHITE,Colors.Green.alpha(0.5f)};
	     //Color[] colors = {Color.WHITE,Colors.TaskPaneBlau.alpha(0.5f)};
	     LinearGradientPaint p =
	         new LinearGradientPaint(start, end, dist, colors);
	     MattePainter mp = new MattePainter(p);
	     setBackgroundPainter(new CompoundPainter(mp));
		*/
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("KassenPanel"));
		setLayout(new BorderLayout());
		add(getContent(),BorderLayout.CENTER);
		if(!kid.equals("")){
			final String xkass = kid; 
			new SwingWorker<Void,Void>(){

				@Override
				protected Void doInBackground() throws Exception {
					holeAktKasse(xkass);
					SwingUtilities.invokeLater(new Runnable(){
						public  void run(){
					 		setzeFocus();
						}
					});
					return null;
				}
				
			}.execute();
			
		}else{
			SwingUtilities.invokeLater(new Runnable(){
				public  void run(){
			 		setzeFocus();
				}
			});
		}
		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				KeyStroke stroke = KeyStroke.getKeyStroke(70, KeyEvent.ALT_MASK);
				getInstance().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "doSuchen");
				getInstance().getActionMap().put("doSuchen", new KasseAction());
				stroke = KeyStroke.getKeyStroke(78, KeyEvent.ALT_MASK);
				getInstance().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "doNeu");
				getInstance().getActionMap().put("doNeu", new KasseAction());	
				stroke = KeyStroke.getKeyStroke(69, KeyEvent.ALT_MASK);
				getInstance().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "doEdit");
				getInstance().getActionMap().put("doEdit", new KasseAction());
				stroke = KeyStroke.getKeyStroke(76, KeyEvent.ALT_MASK);
				getInstance().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "doDelete");
				getInstance().getActionMap().put("doDelete", new KasseAction());
				stroke = KeyStroke.getKeyStroke(66, KeyEvent.ALT_MASK);
				getInstance().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "doBrief");
				getInstance().getActionMap().put("doBrief", new KasseAction());

				/*
				if(TerminFenster.thisClass != null){
			    	TerminFenster.thisClass.setUpdateVerbot(true);
			    }
			    */
			}
		});
		
		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
		 		setzeFocus();
			}
		});

		
	}
	public KassenPanel getInstance(){
		return this;
	}
	public void setzeFocus(){
		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				suchen.requestFocus();
			}
		});
	}

	public JXPanel getContent(){
		FormLayout lay = new FormLayout("pref:g,0dlu,pref:g","pref,5dlu,150dlu,5dlu,fill:0:grow(1.0),5dlu");
		CellConstraints cc = new CellConstraints();
		contPan = JCompTools.getEmptyJXPanel(lay);
		contPan.setBorder(BorderFactory.createEmptyBorder(0,10, 10, 10));
		contPan.setOpaque(false);
		JToolBar jtb = new JToolBar();
		jtb.setOpaque(false);
		jtb.setRollover(true);
		jtb.setBorder(null);
		jtb.setOpaque(false);
		
		jtb.add(new JLabel("finde Kasse -->"));

		JXPanel supan = new JXPanel(new BorderLayout());
		supan.setPreferredSize(new Dimension(100,25));
		supan.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
		supan.setOpaque(false);

		suchen = new JRtaTextField("",true);
		suchen.setSize(100, 10);
		//suchen.setPreferredSize(new Dimension(100,10));
		suchen.setName("suchen");
		suchen.addKeyListener(this);
		supan.add(suchen,BorderLayout.NORTH);
		jtb.add(supan);
		
		jtb.addSeparator(new Dimension(40,25));
		
		jbut[0] = new JButton();
		jbut[0].setIcon(SystemConfig.hmSysIcons.get("neu"));
		jbut[0].setToolTipText("neue Kasse anlegen (Alt+N)");
		jbut[0].setActionCommand("neu");
		jbut[0].addActionListener(this);
		jtb.add(jbut[0]);
		jbut[1] = new JButton();
		jbut[1].setIcon(SystemConfig.hmSysIcons.get("edit"));
		jbut[1].setToolTipText("aktuelle Kasse ändern/editieren (Alt+E)");		
		jbut[1].setActionCommand("edit");
		jbut[1].addActionListener(this);
		jtb.add(jbut[1]);
		jbut[2] = new JButton();
		jbut[2].setIcon(SystemConfig.hmSysIcons.get("delete"));
		jbut[2].setToolTipText("Kasse löschen (Alt+L)");
		jbut[2].setActionCommand("delete");
		jbut[2].addActionListener(this);
		jtb.add(jbut[2]);
		jtb.addSeparator(new Dimension(40,0));
		jbut[3] = new JButton();
		//jbut[3].setIcon(new ImageIcon(Reha.proghome+"icons/mail_write_22.png"));
		jbut[3].setIcon(SystemConfig.hmSysIcons.get("print"));
		jbut[3].setToolTipText("Brief/Formular für Kasse erstellen (Alt+B)");
		jbut[3].setActionCommand("formulare");
		jbut[3].addActionListener(this);
		jtb.add(jbut[3]);

		jtb.addSeparator(new Dimension(140,0));
		jbut[4] = new JButton();
		jbut[4].setIcon(SystemConfig.hmSysIcons.get("info2"));
		jbut[4].setToolTipText("Kasse auf 302-Tauglichkeit testen");
		jbut[4].setActionCommand("302tauglich");
		jbut[4].addActionListener(this);
		jtb.add(jbut[4]);


		contPan.add(jtb,cc.xy(1,1));
		contPan.add(getTabelle(),cc.xyw(1,3,3));

		/*
		ta = new JTextArea();
		JScrollPane span = JCompTools.getTransparentScrollPane(ta);
		span.setOpaque(true);
		span.setBackground(Color.WHITE);
		span.validate();
		contPan.add(ta,cc.xyw(1,5,3));
		*/
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run(){
		 		   holeFormulare();
		 		   return;
		 	   }
		}); 	  

		
		contPan.add(getEdits(),cc.xyw(1,5,3));
		contPan.validate();
		
		return contPan;
	}
	public JScrollPane getTabelle(){
		ktblm = new MyKassenTableModel();
		String[] column = 	{"Kürzel","Name1","Name2","Ort","Telefon","Telefax","IK-Kasse","id"};
		ktblm.setColumnIdentifiers(column);
		kassentbl = new JXTable(ktblm);
		kassentbl.setHighlighters(HighlighterFactory.createSimpleStriping(Colors.Green.alpha(0.2f)));
		kassentbl.setDoubleBuffered(true);
		kassentbl.setEditable(false);
		kassentbl.setSortable(true);
		kassentbl.getColumn(0).setMinWidth(50);
		kassentbl.getColumn(0).setMaxWidth(50);
		kassentbl.getColumn(1).setMinWidth(220);
		kassentbl.getColumn(4).setMinWidth(100);
		kassentbl.getColumn(5).setMinWidth(100);
		kassentbl.getColumn(7).setMinWidth(30);
		//kassentbl.getColumn(7).setMaxWidth(0);
		kassentbl.addKeyListener(this);
		kassentbl.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent arg0) {
				if(inMemoEdit){
					return;
				}
				// TODO Auto-generated method stub
				//System.out.println(" in Tabelle "+arg0.getKeyCode());
				if(arg0.getKeyCode()== 10){
					arg0.consume();
					int row = kassentbl.getSelectedRow(); 
					if(row >= 0){
						String sid =  (String) kassentbl.getValueAt(row,7);
						neuanlageKasse(sid);
					}
				}
			}	
		});

		kassentbl.addMouseListener(this);
		kassentbl.validate();
		kassentbl.setName("KrankenKasse");
		kassentbl.setHorizontalScrollEnabled(true);
		kassentbl.getSelectionModel().addListSelectionListener(kassenlistener);
		JScrollPane jscr = JCompTools.getTransparentScrollPane(kassentbl);
		jscr.validate();
		return jscr;
		
	}
	public void macheTabelle(Vector<?> vec){
		if(vec.size()> 0){
			ktblm.addRow(vec);	
		}else{
			ktblm.setRowCount(0);
			kassentbl.validate();
		}
		
	}
	/*
	private void testeTabelle(){
		String vgl = suchen.getText().trim().toUpperCase();
		String[] match = vgl.split(" ");
		if(match.length > 1){
			if(!match[1].trim().equals("")){
				//Filter[] filterArray = { new PatternAndOrFilterAcrossAllColumns(vgl, 0, 7,match)  };
				//FilterPipeline filters = new FilterPipeline(filterArray);
				//kassentbl.setFilters(filters);				
				//System.out.println("In mehrfachsuche");
			}else{
				//Filter[] filterArray = { new PatternFilterAcrossAllColumns(vgl, 0, 7)  };
				//FilterPipeline filters = new FilterPipeline(filterArray);
				//kassentbl.setFilters(filters);
			}
		}else{
			//Filter[] filterArray = { new PatternFilterAcrossAllColumns(vgl, 0, 7)  };
			//FilterPipeline filters = new FilterPipeline(filterArray);
			//kassentbl.setFilters(filters);
		}
		suchen.requestFocus();
		boolean treffer = false;
		if(!treffer){return;}
		int itreffer = -1;
		if(vgl.equals("")){
			return;
		}
		int anzahl = kassentbl.getRowCount();
		if(suchestarten > anzahl){
			JOptionPane.showMessageDialog(null,"Ende der Kassen-Tabelle erreicht");		
			return;
		}
		for(int i = suchestarten; i < anzahl;i++){
			if( ((String)kassentbl.getValueAt(i, 0)).toUpperCase().indexOf(vgl) >= 0 ||  
				((String)kassentbl.getValueAt(i, 1)).toUpperCase().indexOf(vgl) >= 0 ||
				((String)kassentbl.getValueAt(i, 2)).toUpperCase().indexOf(vgl) >= 0 || 
				((String)kassentbl.getValueAt(i, 3)).toUpperCase().indexOf(vgl) >= 0 ||
				((String)kassentbl.getValueAt(i, 4)).toUpperCase().indexOf(vgl) >= 0 	
			){
				treffer = true;
				itreffer = i;
				break; 
			}
		}
		if(treffer){
			kassentbl.scrollCellToVisible(itreffer, 0);
			kassentbl.setRowSelectionInterval(itreffer, itreffer);
			//System.out.println("nächster gefunden bei "+itreffer+" // suchestarten = "+suchestarten);
			suchestarten = itreffer+1;
			//JOptionPane.showMessageDialog(null,"Treffer bei "+itreffer);
		
		}else{
			if(suchestarten > 0){
				JOptionPane.showMessageDialog(null,"Keine weitere Übereinstimmung mit Suchkriterium");				
			}else{
				JOptionPane.showMessageDialog(null,"Keine Übereinstimmung mit Suchkriterium");				
			}
			
		}
		suchen.requestFocus();
	}
	*/
	private JXPanel getEdits(){
		JXPanel jpan = JCompTools.getEmptyJXPanel();
		jpan.setOpaque(false);
		FormLayout laye= new FormLayout("250dlu:g",
				"30px,0dlu,100dlu:g,1dlu");
		CellConstraints cce = new CellConstraints(); 
		jpan.setLayout(laye);
		JToolBar kedit = new JToolBar();
		kedit.setOpaque(false);
		kedit.setRollover(true);
		kedit.setBorder(null);
		
		memobut[0] = new JButton();
		memobut[0].setIcon(SystemConfig.hmSysIcons.get("edit"));
		memobut[0].setToolTipText("Langtext editieren");		
		memobut[0].setActionCommand("kedit");
		memobut[0].addActionListener(this);
		kedit.add(memobut[0]);
		memobut[1] = new JButton();
		memobut[1].setIcon(SystemConfig.hmSysIcons.get("save"));
		memobut[1].setToolTipText("Langtext speichern");		
		memobut[1].setActionCommand("ksave");
		memobut[1].addActionListener(this);
		memobut[1].setEnabled(false);
		kedit.add(memobut[1]);
		kedit.addSeparator(new Dimension(40,0));
		memobut[2] = new JButton();
		memobut[2].setIcon(SystemConfig.hmSysIcons.get("stop"));
		//memobut[2].setIcon(new ImageIcon(Reha.proghome+"icons/cancel.png"));
		memobut[2].setToolTipText("Langtext bearbeiten abbrechen");		
		memobut[2].setActionCommand("kbreak");
		memobut[2].addActionListener(this);
		memobut[2].setEnabled(false);
		kedit.add(memobut[2]);
		jpan.add(kedit,cce.xy(1,1));

		ta = new JTextArea();
		ta.setFont(new Font("Courier",Font.PLAIN,12));
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		ta.setEditable(false);
		ta.setBackground(Color.WHITE);
		ta.setForeground(Color.BLUE);
		//ta.setEnabled(false);
		//ta.setDisabledTextColor(Color.BLUE);
		JScrollPane span = JCompTools.getTransparentScrollPane(ta);
		span.setBackground(Color.WHITE);
		span.validate();
		jpan.add(span, cce.xywh(1,3,1,2));
		jpan.validate();
		return jpan;
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		if(inMemoEdit){
			return;
		}
		//System.out.println(arg0.getKeyCode());
		
		if(arg0.getSource() instanceof JRtaTextField){
			if(arg0.getKeyCode() == 10 && ((JComponent)arg0.getSource()).getName().equals("suchen")){
				ktblm.setRowCount(0);
				kassentbl.validate();
				new HoleKassen(suchen.getText().trim(),getInstance());
				//testeTabelle();
			}
			if(arg0.getKeyCode() == 40 && ((JComponent)arg0.getSource()).getName().equals("suchen")){
				if(kassentbl.getRowCount()<=0){
					suchen.requestFocus();
					return;
				}
				kassentbl.setRowSelectionInterval(0,0);
				kassentbl.requestFocus();
				//this.holeText();
			}

		}
	}
	
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		if(inMemoEdit){
			return;
		}
		if(((JComponent)arg0.getSource()).getName().equals("KrankenKasse") && (arg0.getKeyCode() != 10) ){
			//holeText();
			return;
		}		
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	/****************************/
/**************************/
	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run()
		 	   
		 	   {
		 		   suchen.requestFocus();
		 	   }
		});
		
	}
	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		String sc = arg0.getActionCommand();
		if(sc.equals("kedit")){
			//String sid = "";
			int row = kassentbl.getSelectedRow();
			if(row < 0){
				String mes = "\nWenn man den Langtext einer Kasse ändern will, empfiehlt es sich\n"+ 
				"vorher die Kasse auszuwählen deren Langtext man ändern will!!!\nHerr schmeiß Hirn ra....\n";
				JOptionPane.showMessageDialog(null, mes);
				suchen.requestFocus();
				return;
			}
			inMemoEdit = true;
			controlsEin(false);
			ta.setEditable(true);
			ta.setBackground(Color.WHITE);
			ta.setForeground(Color.RED);
			ta.setCaretPosition(0);
			return;
		}
		if(sc.equals("ksave")){
			inMemoEdit = false;
			controlsEin(true);			
			ta.setEditable(false);
			ta.setBackground(Color.WHITE);
			ta.setForeground(Color.BLUE);
			int row = kassentbl.getSelectedRow();
			String sid =  (String) kassentbl.getValueAt(row,7);
			new ExUndHop().setzeStatement("update kass_adr set KMEMO='"+ta.getText()+"' where id='"+sid+"'");
			return;
		}
		if(sc.equals("kbreak")){
			inMemoEdit = false;
			controlsEin(true);			
			ta.setEditable(false);
			ta.setBackground(Color.WHITE);
			ta.setForeground(Color.BLUE);
			int row = kassentbl.getSelectedRow();
			String sid =  (String) kassentbl.getValueAt(row,7);
			List<String> nichtlesen = Arrays.asList(new String[] {});
			Vector<String> vec = SqlInfo.holeSatz("kass_adr", "KMEMO", "id='"+sid+"'", nichtlesen);
			ta.setText((String) vec.get(0));
			return;
		}
		
		if(inMemoEdit){
			return;
		}
		if(sc.equals("neu")){
			neuanlageKasse("");
			return;
		}
		if(sc.equals("edit")){
			int row = kassentbl.getSelectedRow(); 
			if(row >= 0){
				String sid =  (String) kassentbl.getValueAt(row,7);
				neuanlageKasse(sid);
				return;
			}else{
				String mes = "\nWenn man eine Kasse ändern will, empfiehlt es sich\n"+ 
				"vorher die Kasse auszuwählen die man ändern will!!!";
				JOptionPane.showMessageDialog(null, mes);
				suchen.requestFocus();
				return;
			}
		}
		if(sc.equals("delete")){
			kasseLoeschen();
			return;
		}
		if(sc.equals("formulare")){
			formulareAuswerten();
			return;
		}
		if(sc.equals("302tauglich")){
			do302Check();
			return;
		}
	}
	
	@Override
	public void tableChanged(TableModelEvent e) {
		// TODO Auto-generated method stub
		//System.out.println("TabelEvent "+e);
		
	}
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		//System.out.println("PropertyChange "+evt);
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if(((JComponent)arg0.getSource()).getName().equals("KrankenKasse") && arg0.getClickCount()==2){
			int row = kassentbl.getSelectedRow(); 
			if(row >= 0){
				//holeText();
				String sid =  (String) kassentbl.getValueAt(row,7);
				neuanlageKasse(sid);
				return;
			}
		}

		if(((JComponent)arg0.getSource()).getName().equals("KrankenKasse")){
			int row = kassentbl.getSelectedRow(); 
			if(row >= 0){
				//holeText();
			}
		}

		
	}
	public void controlsEin(boolean ein){
		suchen.setEnabled((ein ? true : false));
		kassentbl.setEnabled((ein ? true : false));
		memobut[0].setEnabled((ein ? true : false));
		memobut[1].setEnabled((ein ? false : true));
		memobut[2].setEnabled((ein ? false : true));
		jbut[0].setEnabled((ein ? true : false));
		jbut[1].setEnabled((ein ? true : false));
		jbut[2].setEnabled((ein ? true : false));
		jbut[3].setEnabled((ein ? true : false));
	}
	public void kasseLoeschen(){
		int row = kassentbl.getSelectedRow(); 
		if(row >= 0){
        	int frage = JOptionPane.showConfirmDialog(null, "Wollen Sie diese Krankenkasse wirklich löschen??", "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
        	if(frage== JOptionPane.NO_OPTION){
        		return;
        	}
        	String kid = (String) kassentbl.getValueAt(row,7);
        	new ExUndHop().setzeStatement("delete from kass_adr where id='"+kid+"'");
			int model = kassentbl.convertRowIndexToModel(row);
			ktblm.removeRow(model);
			kassentbl.revalidate();
			kassentbl.repaint();
        	
		}else{
			String mes = "\nWenn man schon eine Kasse löschen will, empfiehlt es sich\n"+ 
			"vorher die Kasse auszuwählen die man löschen will!!!";
			JOptionPane.showMessageDialog(null, mes);
			suchen.requestFocus();
		}

	}
	public void holeText(){
		int row = kassentbl.getSelectedRow(); 
		if(row >= 0){
			//System.out.println("In HoleText");
			String sid = Integer.valueOf((String) kassentbl.getValueAt(row, 7)).toString();
			new HoleText(this,sid);
			ta.setCaretPosition(0);
			final String xsid = sid;
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					KasseTools.constructKasseHMap(xsid);
					return null;
				}
				
			}.execute();
		}
	}
	public void setMemo(String text){
		ta.setText(text);
		
	}
	public void holeFormulare(){
		new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				// TODO Auto-generated method stub
				INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/kasse.ini");
				int forms = inif.getIntegerProperty("Formulare", "KassenFormulareAnzahl");
				for(int i = 1; i <= forms; i++){
					titel.add(inif.getStringProperty("Formulare","KFormularText"+i));			
					formular.add(inif.getStringProperty("Formulare","KFormularName"+i));
				}	
				return null;
			}
			
		}.execute();
		
	}
	public void formulareAuswerten(){
		int row = kassentbl.getSelectedRow(); 
		if(row >= 0){
			//String sid = Integer.valueOf((String) kassentbl.getValueAt(row, 7)).toString();
    		iformular = -1;
    		KassenFormulare kf = new KassenFormulare(Reha.thisFrame,titel,formularid);
    		Point pt = jbut[3].getLocationOnScreen();
    		kf.setLocation(pt.x-100,pt.y+25);
    		kf.setModal(true);
    		kf.setVisible(true);
    		iformular = Integer.valueOf(formularid.getText());
    		kf = null;
    		//final String xid = sid;
    		if(iformular >= 0){
    			new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						try{
							//OOTools.testePlatzhalter(Reha.proghome+"vorlagen/"+Reha.aktIK+"/"+formular.get(iformular),null);
							OOTools.starteStandardFormular(Reha.proghome+"vorlagen/"+Reha.aktIK+"/"+formular.get(iformular),null);
							//ladeSchreiben(Reha.proghome+"vorlagen/"+Reha.aktIK+"/"+formular.get(iformular));
							// TODO Auto-generated method stub
						}catch(Exception ex){
							ex.printStackTrace();
						}
						return null;
					}
    			}.execute();
    			
    		}
 		}else{
			String mes = "\nWenn man eine Kasse anschreiben möchte, empfiehlt es sich\n"+ 
			"vorher die Kasse auszuwählen die man anschreiben möchte!!!\n\n"+
			"Aber trösten Sie sich, unser Herrgott hat ein Herz für eine ganz spezielle Randgruppe.\n"+
			"Sie dürfen also hoffen....\n\n";
			JOptionPane.showMessageDialog(null, mes);
			iformular = -1;
			suchen.requestFocus();
		}
		
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
	public void neuanlageKasse(String id){
		KasseNeuDlg neuKas = new KasseNeuDlg();
		//JDialog neuPat = new JDialog();
		PinPanel pinPanel = new PinPanel();
		pinPanel.getGruen().setVisible(false);
		pinPanel.setName("KassenNeuanlage");
		neuKas.setPinPanel(pinPanel);
		if(id.equals("")){
			neuKas.getSmartTitledPanel().setTitle("Kranke-Kasse anlegen - nur der Herrgott weiß ob es sich lohnt diesen Lotterladen anzulegen");			
		}else{
			neuKas.getSmartTitledPanel().setTitle("Daten einer kranken-Kasse ändern");
		}

		neuKas.setPinPanel(pinPanel);
		neuKas.getSmartTitledPanel().setContentContainer(new KasseNeuanlage(neuKas,getInstance(),new Vector<String>(),id));
		neuKas.getSmartTitledPanel().getContentContainer().setName("KassenNeuanlage");
		neuKas.getSmartTitledPanel().setName("KassenNeuanlage");
		neuKas.setName("KassenNeuanlage");
		//neuPat.setContentPane(new PatNeuanlage(new Vector()));
		neuKas.setSize(500,490);
		neuKas.setModal(true);
		neuKas.setLocationRelativeTo(null);
		//neuPat.setLocation(new Point(200,50));
		neuKas.setTitle("Kasse Neuanlage");
		final KasseNeuDlg xneuKas = neuKas; 
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run(){
		 			((KasseNeuanlage)xneuKas.getSmartTitledPanel().getContentContainer()).setzeFocus();
		 	   }
		}); 	   	
		neuKas.setVisible(true);
		neuKas.setVisible(false);
		neuKas = null;
		new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				/*
				Runtime r = Runtime.getRuntime();
			    r.gc();
			    long freeMem = r.freeMemory();
			    //System.out.println("Freier Speicher nach  gc():    " + freeMem);
			    */
				return null;
			}
			
		}.execute();
		
		
	}
	/*
	private void ladeSchreiben(String url){
		IDocumentService documentService = null;;
		
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

			String placeholderDisplayText = placeholders[i].getDisplayText();

		}
	}
	*/
	public void holeAktKasse(String id){
		//{"LANR","Nachname","Vorname","Strasse","Ort","Telefon","Telefax","Klinik","Facharzt",""};

		Vector<Vector<String>> vec;
		if(id.equals("")){
			return;
		}else{
			vec = SqlInfo.holeSaetze("kass_adr", "kuerzel,kassen_nam1,kassen_nam2,ort,telefon,fax,ik_kasse,id",
					"id='"+id+"' LIMIT 1", Arrays.asList(new String[]{}));
		}
		this.ktblm.setRowCount(0);
		int anzahl = 0;
		if( (anzahl = vec.size()) > 0){
			for(int i = 0; i < anzahl;i++ ){
				this.ktblm.addRow((Vector<?>)vec.get(i));
			}
			this.kassentbl.setRowSelectionInterval(0, 0);
			//holeText();
		}
		suchen.requestFocus();
	}
	/***************************************/
    private void do302Check(){
    /*
     *			buf.append("update kass_adr set ");
				buf.append("kassen_nam1='"+name1+"', ");
				buf.append("kassen_nam2='"+name2+"', ");
				buf.append("strasse='"+strasse+"', ");
				buf.append("plz='"+plz+"', ");
				buf.append("ort='"+ort+"', ");
				buf.append("ik_kasse='"+ikkasse+"', ");
				buf.append("ik_kostent='"+ikktraeger+"', ");
				buf.append("ik_physika='"+ikdaten+"', ");
				buf.append("ik_nutzer='"+iknutzer+"', ");
				buf.append("ik_papier='"+ikpapier+"', ");
	
     */
        if(kassentbl.getRowCount()<=0){
        	JOptionPane.showMessageDialog(null,"Keine Kasse für 302-er Test vorhanden");
        	return;
        }
        int iid = Integer.parseInt(kassentbl.getValueAt(kassentbl.getSelectedRow(), 7).toString());
        if(iid < 0){
        	JOptionPane.showMessageDialog(null,"Keine Kasse für 302-er Test ausgewählt");
        	return;
        }
        Vector <Vector<String>> testvec =SqlInfo.holeFelder(
        		"select kassen_nam1,ik_kasse,ik_kostent,ik_physika,ik_nutzer,ik_papier,email1 from kass_adr"+
        		" where id ='"+Integer.toString(iid)+"' LIMIT 1");
        boolean klappt = true;
        StringBuffer buf1 = new StringBuffer();
        HashMap<String,String> testHm = new HashMap<String,String>();
    	//buf1.setLength(0);
    	//buf1.trimToSize();
    	
    	buf1.append("<html><head>");
    	buf1.append("<STYLE TYPE='text/css'>");
    	buf1.append("<!--");
    	buf1.append("A{text-decoration:none;background-color:transparent;border:none}");
    	buf1.append("TD{font-family: Arial; font-size: 12pt; padding-left:5px;padding-right:30px}");
    	buf1.append(".spalte1{color:#FF0000;}");
    	buf1.append(".spalte2{color:#333333;}");
    	buf1.append(".spalte2{color:#333333;}");
    	buf1.append("--->");
    	buf1.append("</STYLE>");
    	buf1.append("</head>");
    	buf1.append("<div style=margin-left:30px;>");
    	buf1.append("<font face='Tahoma'><style=margin-left=30px;>");
    	buf1.append("<br>");
    	buf1.append("<table>");
    	/*****************************************/ 
    	//Test von Kassennamen und IK
    	
        //0=Kassenname
        //1=IK der Kasse
        //2=IK des Kostenträgers
        //3=Physikalischer Datenempfänger
        //4=Datenempfänger mit Entschlüsselungsbefugnis
        //5=Papierannahmestelle
        //6=Emailadresse der Datenannahmestelle
    	//K-Name
        if(testvec.get(0).get(0).equals("")){
            buf1.append("<tr><td class='spalte2' align='right'>Feld Kassenname</td><td>&nbsp;</td><td><img src='file:///"+Reha.proghome+"icons/nichtok.gif"+"'></td></tr>");
            klappt = false;
        }else{
            buf1.append("<tr><td class='spalte2' align='right'>Feld Kassenname</td><td>&nbsp;</td><td><img src='file:///"+Reha.proghome+"icons/ok.gif"+"'></td></tr>");
            testHm.put("kassen_name", String.valueOf(testvec.get(0).get(0).trim()));
        }
        //K-IK
        if(testvec.get(0).get(1).equals("")){
            buf1.append("<tr><td class='spalte2' align='right'>IK der Krankenkasse</td><td>&nbsp;</td><td><img src='file:///"+Reha.proghome+"icons/nichtok.gif"+"'></td></tr>");
            klappt = false;
        }else{
            buf1.append("<tr><td class='spalte2' align='right'>IK der Krankenkasse</td><td>&nbsp;</td><td><img src='file:///"+Reha.proghome+"icons/ok.gif"+"'></td></tr>");
            testHm.put("kassen_ik", String.valueOf(testvec.get(0).get(1).trim()));
        }
        //IK-Kostenträger
        if(testvec.get(0).get(2).equals("")){
            buf1.append("<tr><td class='spalte2' align='right'>IK des Kostenträgers</td><td>&nbsp;</td><td><img src='file:///"+Reha.proghome+"icons/nichtok.gif"+"'></td></tr>");
            klappt = false;
        }else{
            buf1.append("<tr><td class='spalte2' align='right'>IK des Kostenträgers</td><td>&nbsp;</td><td><img src='file:///"+Reha.proghome+"icons/ok.gif"+"'></td></tr>");
            testHm.put("ktraeger_ik", String.valueOf(testvec.get(0).get(2).trim()));
            //testen ob im Kassenstamm vorhanden
            if(! SqlInfo.holeEinzelFeld("select id from kass_adr where ik_kasse='"+testvec.get(0).get(2)+"' LIMIT 1").equals("")){
            	buf1.append("<tr><td class='spalte2' align='right'>Kostenträger im Kassenstamm vorhanden</td><td>&nbsp;</td><td><img src='file:///"+Reha.proghome+"icons/ok.gif"+"'></td></tr>");
            }else{
            	//wenn nicht im Kassenstamm dann Kostenträgerdatei auswerten
            	if( ! SqlInfo.holeEinzelFeld("select id from ktraeger where ikkasse='"+testvec.get(0).get(2)+"' LIMIT 1").equals("") ){
                	buf1.append("<tr><td class='spalte2' align='right'>Kostenträger im Kostenträgerstamm vorhanden</td><td>&nbsp;</td><td><img src='file:///"+Reha.proghome+"icons/ok.gif"+"'></td></tr>");
            	}else{
                    klappt = false;
            		buf1.append("<tr><td class='spalte2' align='right'>Kostenträger vorhanden</td><td>&nbsp;</td><td><img src='file:///"+Reha.proghome+"icons/nichtok.gif"+"'></td></tr>");
            	}
            }
        }
        //IK der Datenannahmestelle
        if(testvec.get(0).get(3).equals("")){
            buf1.append("<tr><td class='spalte2' align='right'>IK der Datenannahmestelle</td><td>&nbsp;</td><td><img src='file:///"+Reha.proghome+"icons/nichtok.gif"+"'></td></tr>");
            klappt = false;
        }else{
        	//testen om im Kassenstamm vohanden
            buf1.append("<tr><td class='spalte2' align='right'>IK der Datenannahmestelle</td><td>&nbsp;</td><td><img src='file:///"+Reha.proghome+"icons/ok.gif"+"'></td></tr>");
            testHm.put("annahme_ik", String.valueOf(testvec.get(0).get(3).trim()));
            if(!SqlInfo.holeEinzelFeld("select id from kass_adr where ik_kasse='"+testvec.get(0).get(3)+"' LIMIT 1").equals("")){
            	buf1.append("<tr><td class='spalte2' align='right'>Datenannahmestelle im Kassenstamm vorhanden</td><td>&nbsp;</td><td><img src='file:///"+Reha.proghome+"icons/ok.gif"+"'></td></tr>");	
            }else{
            	//wenn nicht im Kassenstamm dann Kostenträgerdatei auswerten
            	if( ! SqlInfo.holeEinzelFeld("select id from ktraeger where ikkasse='"+testvec.get(0).get(3)+"' LIMIT 1").equals("") ){
                	buf1.append("<tr><td class='spalte2' align='right'>Datenannahmestelle im Kostenträgerstamm vorhanden</td><td>&nbsp;</td><td><img src='file:///"+Reha.proghome+"icons/ok.gif"+"'></td></tr>");
            	}else{
                    klappt = false;
            		buf1.append("<tr><td class='spalte2' align='right'>Datenannahmestelle vorhanden</td><td>&nbsp;</td><td><img src='file:///"+Reha.proghome+"icons/nichtok.gif"+"'></td></tr>");
            	}
            }
        }
        //IK der Stelle mit Entschlüsselungsbefugnis
        if(testvec.get(0).get(4).equals("")){
            buf1.append("<tr><td class='spalte2' align='right'>IK der Stelle mit Entschlüsselungsbefugnis</td><td>&nbsp;</td><td><img src='file:///"+Reha.proghome+"icons/nichtok.gif"+"'></td></tr>");
            klappt = false;
        }else{
        	//testen om im Kassenstamm vohanden
            buf1.append("<tr><td class='spalte2' align='right'>IK der Stelle mit Entschlüsselungsbefugnis</td><td>&nbsp;</td><td><img src='file:///"+Reha.proghome+"icons/ok.gif"+"'></td></tr>");
            testHm.put("decode_ik", String.valueOf(testvec.get(0).get(4).trim()));
            if(!SqlInfo.holeEinzelFeld("select id from kass_adr where ik_kasse='"+testvec.get(0).get(4)+"' LIMIT 1").equals("")){
            	buf1.append("<tr><td class='spalte2' align='right'>Stelle mit Entschlüsselungsbefugnis im Kassenstamm vorhanden</td><td>&nbsp;</td><td><img src='file:///"+Reha.proghome+"icons/ok.gif"+"'></td></tr>");	
            }else{
            	//wenn nicht im Kassenstamm dann Kostenträgerdatei auswerten
            	if( ! SqlInfo.holeEinzelFeld("select id from ktraeger where ikkasse='"+testvec.get(0).get(4)+"' LIMIT 1").equals("") ){
                	buf1.append("<tr><td class='spalte2' align='right'>Stelle mit Entschlüsselungsbefugnis im Kostenträgerstamm vorhanden</td><td>&nbsp;</td><td><img src='file:///"+Reha.proghome+"icons/ok.gif"+"'></td></tr>");
            	}else{
                    klappt = false;
            		buf1.append("<tr><td class='spalte2' align='right'>Stelle mit Entschlüsselungsbefugnis vorhanden</td><td>&nbsp;</td><td><img src='file:///"+Reha.proghome+"icons/nichtok.gif"+"'></td></tr>");
            	}
            }
        }
        //IK der Papierannahmestelle
        if(testvec.get(0).get(5).equals("")){
            buf1.append("<tr><td class='spalte2' align='right'>IK der Papierannahmestelle</td><td>&nbsp;</td><td><img src='file:///"+Reha.proghome+"icons/nichtok.gif"+"'></td></tr>");
            klappt = false;
        }else{
        	//testen om im Kassenstamm vohanden
            buf1.append("<tr><td class='spalte2' align='right'>IK der Papierannahmestelle</td><td>&nbsp;</td><td><img src='file:///"+Reha.proghome+"icons/ok.gif"+"'></td></tr>");
            testHm.put("papier_ik", String.valueOf(testvec.get(0).get(5).trim()));
            if(!SqlInfo.holeEinzelFeld("select id from kass_adr where ik_kasse='"+testvec.get(0).get(5)+"' LIMIT 1").equals("")){
            	buf1.append("<tr><td class='spalte2' align='right'>Papierannahmestelle im Kassenstamm vorhanden</td><td>&nbsp;</td><td><img src='file:///"+Reha.proghome+"icons/ok.gif"+"'></td></tr>");	
            }else{
            	//wenn nicht im Kassenstamm dann Kostenträgerdatei auswerten
            	if( ! SqlInfo.holeEinzelFeld("select id from ktraeger where ikkasse='"+testvec.get(0).get(4)+"' LIMIT 1").equals("") ){
                	buf1.append("<tr><td class='spalte2' align='right'>Papierannahmestelle im Kostenträgerstamm vorhanden</td><td>&nbsp;</td><td><img src='file:///"+Reha.proghome+"icons/ok.gif"+"'></td></tr>");
            	}else{
                    klappt = false;
            		buf1.append("<tr><td class='spalte2' align='right'>Papierannahmestelle vorhanden</td><td>&nbsp;</td><td><img src='file:///"+Reha.proghome+"icons/nichtok.gif"+"'></td></tr>");
            	}
            }
        }
        //Emailadresse im Kassenstamm vorhanden
        String xemail= null;
        if(! (xemail=SqlInfo.holeEinzelFeld("select email1 from kass_adr where ik_kasse='"+testvec.get(0).get(4)+"' LIMIT 1")).equals("")){
            buf1.append("<tr><td class='spalte2' align='right'>Emailadresse der Datenannahmestelle</td><td>&nbsp;</td><td><img src='file:///"+Reha.proghome+"icons/ok.gif"+"'></td></tr>");
            testHm.put("email", String.valueOf(xemail.trim()));
        }else{
        	//testen om im Kostenträgerstamm vohanden
            if(! (xemail=SqlInfo.holeEinzelFeld("select email from ktraeger where ikkasse='"+testvec.get(0).get(4)+"' LIMIT 1")).equals("")){
            	buf1.append("<tr><td class='spalte2' align='right'>Emailadresse der Datenannahmestelle</td><td>&nbsp;</td><td><img src='file:///"+Reha.proghome+"icons/ok.gif"+"'></td></tr>");
            	testHm.put("email", String.valueOf(xemail.trim()));
            }else{
                klappt = false;
        		buf1.append("<tr><td class='spalte2' align='right'><br>Emailadresse der Datenannahmestelle</td><td>&nbsp;</td><td><img src='file:///"+Reha.proghome+"icons/nichtok.gif"+"'></td></tr>");
            }
        }
        if(klappt){
        	buf1.append("<tr><td class='spalte1' align='left'><br><b>Gratulation der 302-er Test wurde bestanden.</b><br><br>Wollen Sie im Anschluß "+
        			"eine Probeverschlüsselung durchführen?</td></tr>");
            buf1.append("</table>");
            buf1.append("<br>");
            buf1.append("</html>");
            int frage = JOptionPane.showConfirmDialog(null, buf1.toString(),
            		"Test der Krankenkasse auf 302-Fähigkeit",JOptionPane.YES_NO_OPTION);
            if(frage != JOptionPane.YES_OPTION){
            	return;
            }
            String datei = Reha.proghome+"edifact/"+Reha.aktIK+"/"+testHm.get("kassen_ik");
            
            String keystore = Reha.proghome+"keystore/"+Reha.aktIK+"/"+Reha.aktIK+".p12";
            File f = new File(keystore);
            if(! f.exists()){
            	JOptionPane.showMessageDialog(null,"Kein Keystore für den aktuellen Mandanten vorhanden");
            	return;
            }
            FileTools.ByteArray2File(testDatei(testHm).getBytes(), datei+".original");
			f = new File(datei+".original");
			long size = f.length();
			
			int[] ret = doVerschluesseln(datei,testHm);
			if(ret[0] < 0){
				JOptionPane.showMessageDialog(null,"Fehler in der Verschlüsselung");
				return;
			}
			if(ret[1] < 0){
				JOptionPane.showMessageDialog(null,"Fehler in der Entschlüsselung");
				return;
			}
			if(ret[1] != Integer.parseInt(Long.toString(size))){
				JOptionPane.showMessageDialog(null,"Fehler: Originaldatei und entschlüsselte Datei sind unterschliedlich groß");
				return;
			}
			String text = "Es wurde absolut fehlerfrei ver- und entschlüsselt\n\n"+
			"Die Testdateien liegen im Verzeichnis "+Reha.proghome+"edifact/"+Reha.aktIK+"/"+"\n"+
			"1. "+testHm.get("kassen_ik")+".original (Ursprungsdatei)\n"+
			"2. "+testHm.get("kassen_ik")+".encoded (verschlüsselte Variante der Ursprungsdatei)\n"+
			"3. "+testHm.get("kassen_ik")+".decoded (entschlüsselte Variante der .encoded Datei)\n\n"+
			"Die Dateigröße der Ursprungsdatei und der entschlüsselten Variante\n"+
			"ist absolut identisch ("+Integer.toString(ret[1])+" Bytes)";
			JOptionPane.showMessageDialog(null,text);
        }else{
        	buf1.append("<tr><td class='spalte1' align='left'><br><b>Der 302-er Test wurde nicht bestanden(!!!)</b><br><br>Mit dieser Kassen-Konfiguration kann "+
    		"keine maschinenlesbare Abrechnung nach §302 SGB V<br>durchgeführt werden!</td></tr>");
        	buf1.append("</table>");
        	buf1.append("<br>");
        	buf1.append("</html>");
        	JOptionPane.showMessageDialog(null, buf1.toString());
        }

    
    
    }
    private String testDatei(HashMap<String,String> hmap){
    	StringBuffer buf = new StringBuffer();
    	buf.append("Kassen-Name = "+hmap.get("kassen_name")+"\n");
    	buf.append("IK der Kasse = "+hmap.get("kassen_ik")+"\n");
    	buf.append("IK des Kostenträgers = "+hmap.get("ktraeger_ik")+"\n");
    	buf.append("IK der Datenannahmestelle = "+hmap.get("annahme_ik")+"\n");
    	buf.append("IK des Nutzer mit Entschlüsselungsbefugnis = "+hmap.get("decode_ik")+"\n");
    	buf.append("IK der Papierannahmestelle = "+hmap.get("papier_ik")+"\n");
    	buf.append("Emailadresse = "+hmap.get("email")+"\n");
    	//System.out.println(buf.toString());
    	return buf.toString();
    }
	private int[] doVerschluesseln(String datei,HashMap<String,String> hmap){
		int[] retint = {-1,-1};
		try {
			String keystore = Reha.proghome+"keystore/"+Reha.aktIK+"/"+Reha.aktIK+".p12";
			NebraskaKeystore store = new NebraskaKeystore(keystore, SystemConfig.hmAbrechnung.get("hmkeystorepw"),"123456", Reha.aktIK);
			//NebraskaKeystore store = new NebraskaKeystore(keystore, "123456","123456", Reha.aktIK);
			NebraskaEncryptor encryptor = store.getEncryptor(hmap.get("decode_ik"));
			
			String inFile = datei+".original";
			encryptor.setEncryptToSelf(true);
			long size = encryptor.encrypt(inFile.replace("\\", "/"), (datei+".encoded").replace("\\", "/"));
			retint[0] = Integer.parseInt(Long.toString(size));

			NebraskaDecryptor decryptor = store.getDecryptor();
			FileInputStream fin = new FileInputStream((datei+".encoded").replace("\\", "/"));
			FileOutputStream fout = new FileOutputStream(datei.replace("\\", "/")+".decoded");
			decryptor.decrypt(fin, fout);

			fin.close();
			fout.flush();
			fout.close();
			
			File f = new File(datei+".decoded");
			size = f.length();
			retint[1] = Integer.parseInt(Long.toString(size));
			return retint;
		} catch (NebraskaCryptoException e) {
			e.printStackTrace();
		} catch (NebraskaFileException e) {
			e.printStackTrace();
		} catch (NebraskaNotInitializedException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return retint;
	}

    /************************************/
    class KassenListSelectionHandler implements ListSelectionListener {
    	
        public void valueChanged(ListSelectionEvent e) {
            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
            boolean isAdjusting = e.getValueIsAdjusting();
            if(isAdjusting){
            	return;
            }
            if (lsm.isSelectionEmpty()) {

            } else {
                int minIndex = lsm.getMinSelectionIndex();
                int maxIndex = lsm.getMaxSelectionIndex();
                for (int i = minIndex; i <= maxIndex; i++) {
                    if (lsm.isSelectedIndex(i)) {
                    	holeText();
                        break;
                    }
                }
            }

        }
    }
    /************************************/
    
	/***************************************/

	class KasseAction extends AbstractAction {
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
	    	if( inMemoEdit){
	    		return;
	    	}
	        ////System.out.println("Roogle Action test");
	        ////System.out.println(e);
	        if(e.getActionCommand().equals("f")){
	        	suchen.requestFocusInWindow();
	        }
	        if(e.getActionCommand().equals("n")){
	        	neuanlageKasse("");
	        }
	        if(e.getActionCommand().equals("e")){
				int row = kassentbl.getSelectedRow(); 
				if(row >= 0){
					String sid =  (String) kassentbl.getValueAt(row,7);
					neuanlageKasse(sid);
				}else{
					String mes = "\nWenn man eine Kasse ändern will, empfiehlt es sich\n"+ 
					"vorher die Kasse auszuwählen die man ändern will!!!";
					JOptionPane.showMessageDialog(null, mes);
					suchen.requestFocus();
				}

	        }	            
	        if(e.getActionCommand().equals("l")){
	        	kasseLoeschen();
	        }
	        if(e.getActionCommand().equals("b")){
	        	formulareAuswerten();
	        }


	    }
	}
	
	/********************************************/
	/*****************************************************/
	/*
	class KassenListSelectionHandler implements KassenSelectionListener {
		
	    public void valueChanged(ListSelectionEvent e) {
	        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
	        boolean isAdjusting = e.getValueIsAdjusting();
	        if(isAdjusting){
	        	return;
	        }
	        if (lsm.isSelectionEmpty()) {

	        } else {
	            int minIndex = lsm.getMinSelectionIndex();
	            int maxIndex = lsm.getMaxSelectionIndex();
	            for (int i = minIndex; i <= maxIndex; i++) {
	                if (lsm.isSelectedIndex(i)) {
	                	x
	                	break;
	                }
	            }
	        }

	    }
	}
	*/
	
	
	
	/********************************************/
}
/*********************************************************/
class HoleKassen{
	HoleKassen(String suche,KassenPanel kpan){
	Statement stmt = null;
	ResultSet rs = null;
	String sstmt = "";
	String[] zweisuche = null;
	String krit = "";
	sstmt = "select kuerzel,kassen_nam1,kassen_nam2,ort,telefon,fax,ik_kasse,id from kass_adr ORDER BY kuerzel";
	if(suche.length()== 0){
		sstmt = "select kuerzel,kassen_nam1,kassen_nam2,ort,telefon,fax,ik_kasse,id from kass_adr ORDER BY kuerzel";
	}else{
		zweisuche = suche.split(" ");
		if(zweisuche.length==1){
			krit = " where (kuerzel like'%"+zweisuche[0]+"%' or kassen_nam1 like '%"+zweisuche[0]+"%' or "+
			"kassen_nam2 like '%"+zweisuche[0]+"%' or ort like '%"+zweisuche[0]+"%' or ik_kasse like '%"+zweisuche[0]+"%') ";
				sstmt = "select kuerzel,kassen_nam1,kassen_nam2,ort,telefon,fax,ik_kasse,id from kass_adr "+krit+ "ORDER BY kuerzel";			
		}else if(zweisuche.length >=2){
			krit = " where (kuerzel like'%"+zweisuche[0]+"%' or kassen_nam1 like '%"+zweisuche[0]+"%' or "+
			"kassen_nam2 like '%"+zweisuche[0]+"%' or ort like '%"+zweisuche[0]+"%' or ik_kasse like '%"+zweisuche[0]+"%') AND "+
			"(kuerzel like'%"+zweisuche[1]+"%' or kassen_nam1 like '%"+zweisuche[1]+"%' or "+
			"kassen_nam2 like '%"+zweisuche[1]+"%' or ort like '%"+zweisuche[1]+"%' or ik_kasse like '%"+zweisuche[1]+"%')";
				sstmt = "select kuerzel,kassen_nam1,kassen_nam2,ort,telefon,fax,ik_kasse,id from kass_adr "+krit+ "ORDER BY kuerzel";			
		}
	}
	
	//sstmt = "select kuerzel,kassen_nam1,kassen_nam2,ort,telefon,fax,ik_kasse,id from kass_adr ORDER BY kuerzel";
		
	try {
		stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
		            ResultSet.CONCUR_UPDATABLE );
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	try{
		rs = stmt.executeQuery(sstmt);
		Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
		Vector<String> xvec = new Vector<String>();
		int anzahl = 0;
		while( rs.next()){
			anzahl++;
			xvec.add(rs.getString("KUERZEL"));
			xvec.add(rs.getString("KASSEN_NAM1"));			
			xvec.add(rs.getString("KASSEN_NAM2"));			
			xvec.add(rs.getString("ORT"));
			xvec.add(rs.getString("TELEFON"));			
			xvec.add(rs.getString("FAX"));			
			xvec.add(rs.getString("IK_KASSE"));
			xvec.add(rs.getString("id"));
			kpan.macheTabelle((Vector<?>)xvec.clone());
			xvec.clear();
		}
		if(kpan.ktblm.getRowCount() > 0){
			kpan.kassentbl.setRowSelectionInterval(0, 0);
			kpan.holeText();
		}
		
		Reha.thisFrame.setCursor(Reha.thisClass.normalCursor);
		kpan.setzeFocus();
	}catch(SQLException ev){
		//System.out.println("SQLException: " + ev.getMessage());
		//System.out.println("SQLState: " + ev.getSQLState());
		//System.out.println("VendorError: " + ev.getErrorCode());
	}	
	finally {
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
	}
	//return datvec;
	}
}

/****************************************************/
class HoleText{
	HoleText(KassenPanel pan, String id){
	Statement stmt = null;
	ResultSet rs = null;
	String sstmt = "";
	
	sstmt = "select kmemo from kass_adr where id = '"+id+"'";
		
	try {
		stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
		            ResultSet.CONCUR_UPDATABLE );
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	try{
		rs = stmt.executeQuery(sstmt);
		Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
		if( rs.next()){
			pan.setMemo(rs.getString(1));
		}else{
			pan.setMemo("");
		}
		Reha.thisFrame.setCursor(Reha.thisClass.normalCursor);
		
	}catch(SQLException ev){
		//System.out.println("SQLException: " + ev.getMessage());
		//System.out.println("SQLState: " + ev.getSQLState());
		//System.out.println("VendorError: " + ev.getErrorCode());
	}	
	finally {
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
	}
	//return datvec;
	}

}


/***************************************************************/
class MyKassenTableModel extends DefaultTableModel{
	   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Class<?> getColumnClass(int columnIndex) {
		   if(columnIndex==0){return String.class;}
		  /* if(columnIndex==1){return JLabel.class;}*/
		   else{return String.class;}
     //return (columnIndex == 0) ? Boolean.class : String.class;
 }

	    public boolean isCellEditable(int row, int col) {
	        //Note that the data/cell address is constant,
	        //no matter where the cell appears onscreen.
	    	return true;
	      }
		public Object getValueAt(int rowIndex, int columnIndex) {
			String theData = (String) ((Vector<?>)getDataVector().get(rowIndex)).get(columnIndex); 
			Object result = null;
			//result = theData.toUpperCase();
			result = theData;
			return result;
		}
	    
	   
}


class KasseNeuDlg extends RehaSmartDialog implements RehaTPEventListener,WindowListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RehaTPEventClass rtp = null;
	public KasseNeuDlg(){
		super(null,"KassenNeuanlage");
		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener((RehaTPEventListener) this);

	}
	public void rehaTPEventOccurred(RehaTPEvent evt) {
		// TODO Auto-generated method stub
		try{
			if(evt.getDetails()[0].equals("KassenNeuanlage")){
				this.setVisible(false);
				rtp.removeRehaTPEventListener((RehaTPEventListener) this);
				rtp = null;
				this.dispose();
			}
			//System.out.println("****************Kasse Neu/ändern -> Listener entfernt**************");				
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
			//System.out.println("****************Kasse Neu/ändern -> Listener entfernt (Closed)**********");
		}
		
		
	}
	
}
