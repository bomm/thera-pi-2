package arztFenster;

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
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import krankenKasse.KassenFormulare;
import oOorgTools.OOTools;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import rehaInternalFrame.JArztInternal;
import sqlTools.ExUndHop;
import sqlTools.SqlInfo;
import stammDatenTools.ArztTools;
import systemEinstellungen.INIFile;
import systemEinstellungen.SystemConfig;
import systemTools.Colors;
import systemTools.JCompTools;
import systemTools.JRtaTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;

public class ArztPanel extends JXPanel implements PropertyChangeListener,TableModelListener,KeyListener,FocusListener,ActionListener, MouseListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3764586594168846949L;
	JButton einlesen = null;
	JXPanel contPan = null;
	public JXTable arzttbl = null;
	public MyArztTableModel atblm;
	JRtaTextField suchen = null;
	//public static ArztPanel thisClass = null;
	public int suchestarten = -1;
	public JArztInternal jry = null;
	public JButton[] memobut = {null,null,null};
	public JButton[] jbut = {null,null,null,null};
	//public JRtaTextField[] tf = {null,null,null,null,null,null,null,null,null,null,null,null,null,null,null};
	public JTextArea ta;
	public boolean inMemoEdit = false; 
	private JRtaTextField formularid = new JRtaTextField("NIX",false);
	Vector<String> titel = new Vector<String>() ;
	Vector<String> formular = new Vector<String>();
	int iformular = -1;
	public ArztPanel(JArztInternal jry,String arztid){
		super();
		setBorder(null);
		this.jry = jry;

		addFocusListener(this);
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("ArztPanel"));
	     
		
		setLayout(new BorderLayout());
		add(getContent(),BorderLayout.CENTER);
		if(!arztid.equals("")){
			final String xarzt = arztid; 
			new SwingWorker<Void,Void>(){

				@Override
				protected Void doInBackground() throws Exception {
					SwingUtilities.invokeLater(new Runnable(){
						public  void run(){
							holeAktArzt(xarzt);
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
				getInstance().getActionMap().put("doSuchen", new ArztAction());
				stroke = KeyStroke.getKeyStroke(78, KeyEvent.ALT_MASK);
				getInstance().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "doNeu");
				getInstance().getActionMap().put("doNeu", new ArztAction());	
				stroke = KeyStroke.getKeyStroke(69, KeyEvent.ALT_MASK);
				getInstance().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "doEdit");
				getInstance().getActionMap().put("doEdit", new ArztAction());
				stroke = KeyStroke.getKeyStroke(76, KeyEvent.ALT_MASK);
				getInstance().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "doDelete");
				getInstance().getActionMap().put("doDelete", new ArztAction());
				stroke = KeyStroke.getKeyStroke(66, KeyEvent.ALT_MASK);
				getInstance().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "doBrief");
				getInstance().getActionMap().put("doBrief", new ArztAction());

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
	private ArztPanel getInstance(){
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
		
		jtb.add(new JLabel("finde Arzt -->"));

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
		jbut[0].setToolTipText("neuen Arzt anlegen (Alt+N)");
		jbut[0].setActionCommand("neu");
		jbut[0].addActionListener(this);
		jtb.add(jbut[0]);
		jbut[1] = new JButton();
		jbut[1].setIcon(SystemConfig.hmSysIcons.get("edit"));
		jbut[1].setToolTipText("aktuellen Arzt ändern/editieren (Alt+E)");		
		jbut[1].setActionCommand("edit");
		jbut[1].addActionListener(this);
		jtb.add(jbut[1]);
		jbut[2] = new JButton();
		jbut[2].setIcon(SystemConfig.hmSysIcons.get("delete"));
		jbut[2].setToolTipText("Arzt löschen (Alt+L)");
		jbut[2].setActionCommand("delete");
		jbut[2].addActionListener(this);
		jtb.add(jbut[2]);
		jtb.addSeparator(new Dimension(40,0));
		jbut[3] = new JButton();
		//jbut[3].setIcon(new ImageIcon(Reha.proghome+"icons/mail_write_22.png"));
		jbut[3].setIcon(SystemConfig.hmSysIcons.get("print"));
		jbut[3].setToolTipText("Brief/Formular für Arzt erstellen (Alt+B)");
		jbut[3].setActionCommand("formulare");
		jbut[3].addActionListener(this);
		jtb.add(jbut[3]);



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
		atblm = new MyArztTableModel();
		String[] column = 	{"LANR","Nachname","Vorname","Strasse","Ort","Telefon","Telefax","Klinik","Facharzt","id"};
		atblm.setColumnIdentifiers(column);
		arzttbl = new JXTable(atblm);
		arzttbl.setHighlighters(HighlighterFactory.createSimpleStriping(Colors.Blue.alpha(0.1f)));
		arzttbl.setDoubleBuffered(true);
		arzttbl.setEditable(false);
		arzttbl.setSortable(true);
		arzttbl.getColumn(0).setMinWidth(80);
		arzttbl.getColumn(0).setMaxWidth(80);
		arzttbl.getColumn(1).setMinWidth(100);
		arzttbl.getColumn(2).setMinWidth(100);
		arzttbl.getColumn(3).setMinWidth(100);
		arzttbl.getColumn(4).setMinWidth(100);
		arzttbl.getColumn(9).setMinWidth(30);
		//arzttbl.getColumn(9).setMaxWidth(30);
		arzttbl.addKeyListener(this);
		arzttbl.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent arg0) {
				if(inMemoEdit){
					return;
				}
				// TODO Auto-generated method stub
				//System.out.println(" in Tabelle "+arg0.getKeyCode());
				if(arg0.getKeyCode()== 10){
					arg0.consume();
					int row = arzttbl.getSelectedRow(); 
					if(row >= 0){
						String sid =  (String) arzttbl.getValueAt(row,9);
						neuanlageArzt(sid);
					}
				}
			}	
		});

		arzttbl.addMouseListener(this);
		arzttbl.validate();
		arzttbl.setName("ArztVerwaltung");
		arzttbl.setHorizontalScrollEnabled(true);
		JScrollPane jscr = JCompTools.getTransparentScrollPane(arzttbl);
		jscr.validate();
		return jscr;
	}
	
	
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

	private void fuelleTabelle(String where){
		//{"LANR","Nachname","Vorname","Strasse","Ort","Telefon","Telefax","Klinik","Facharzt",""};
		Vector<Vector<String>> vec;
		if(where.equals("")){
			vec = SqlInfo.holeSaetze("arzt", "arztnum,nachname,vorname,strasse,ort,telefon,fax,klinik,facharzt,id",
					"id >='0'", Arrays.asList(new String[]{}));
		}else{
			vec = SqlInfo.holeSaetze("arzt", "arztnum,nachname,vorname,strasse,ort,telefon,fax,klinik,facharzt,id",
					where, Arrays.asList(new String[]{}));
		}
		int anzahl = 0;
		if( (anzahl = vec.size()) > 0){
			for(int i = 0; i < anzahl;i++ ){
				this.atblm.addRow((Vector<String>)vec.get(i));
			}
			this.arzttbl.setRowSelectionInterval(0, 0);
			holeText();
		}else{
			ta.setText("");
		}
		suchen.requestFocus();
	}

	public void holeAktArzt(String id){
		//{"LANR","Nachname","Vorname","Strasse","Ort","Telefon","Telefax","Klinik","Facharzt",""};

		Vector<Vector<String>> vec;
		if(id.equals("")){
			return;
		}else{
			vec = SqlInfo.holeSaetze("arzt", "arztnum,nachname,vorname,strasse,ort,telefon,fax,klinik,facharzt,id",
					"id='"+id+"' LIMIT 1", Arrays.asList(new String[]{}));
		}
		this.atblm.setRowCount(0);		
		int anzahl = 0;
		if( (anzahl = vec.size()) > 0){
			for(int i = 0; i < anzahl;i++ ){
				this.atblm.addRow((Vector<String>)vec.get(i));
			}
			this.arzttbl.setRowSelectionInterval(0, 0);
			holeText();
		}
		suchen.requestFocus();
	}

	@SuppressWarnings("unchecked")
	private void holeText(){
		int row = this.arzttbl.getSelectedRow();
		if(row < 0){return;}
		String id = (String)this.arzttbl.getValueAt(row, 9);
		ta.setText( (String) ((Vector)((Vector)SqlInfo.holeFelder("select mtext from arzt where id='"+id+"' LIMIT 1")).get(0)).get(0) );
		ta.setCaretPosition(0);
	}
	public void setMemo(String text){
		ta.setText(text);
		
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
		if(arg0.getSource() instanceof JRtaTextField){
			if(arg0.getKeyCode() == 10 && ((JComponent)arg0.getSource()).getName().equals("suchen")){
				atblm.setRowCount(0);
				arzttbl.validate();
				//new HoleKassen(suchen.getText().trim());
				if(suchen.getText().trim().equals("")){
					fuelleTabelle("");
					
				}else{
					fuelleTabelle(SqlInfo.macheWhereKlausel("",suchen.getText().trim() , 
							new String[] {"nachname","vorname","ort","facharzt","klinik"}));
				}
			}
			if(arg0.getKeyCode() == 40 && ((JComponent)arg0.getSource()).getName().equals("suchen")){
				if(arzttbl.getRowCount()<=0){
					suchen.requestFocus();
					return;
				}
				arzttbl.setRowSelectionInterval(0,0);
				arzttbl.requestFocus();
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
		if(((JComponent)arg0.getSource()).getName().equals("ArztVerwaltung") && (arg0.getKeyCode() != 10) ){
			holeText();
			return;
		}		
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
	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String sc = arg0.getActionCommand();
		if(sc.equals("kedit")){
			//String sid = "";
			int row = arzttbl.getSelectedRow();
			if(row < 0){
				String mes = "\nWenn man den Langtext eines Arztes ändern will, empfiehlt es sich\n"+ 
				"vorher den Arzt auszuwählen dessen Langtext man ändern will!!!\nHerr schmeiß Hirn ra....\n";
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
			int row = arzttbl.getSelectedRow();
			String sid =  (String) arzttbl.getValueAt(row,9);
			new ExUndHop().setzeStatement("update arzt set MTEXT='"+ta.getText()+"' where id='"+sid+"'");
			return;
		}
		if(sc.equals("kbreak")){
			inMemoEdit = false;
			controlsEin(true);			
			ta.setEditable(false);
			ta.setBackground(Color.WHITE);
			ta.setForeground(Color.BLUE);
			int row = arzttbl.getSelectedRow();
			String sid =  (String) arzttbl.getValueAt(row,9);
			Vector<String> vec = SqlInfo.holeSatz("arzt", "MTEXT", "id='"+sid+"'", (List)new ArrayList());
			ta.setText((String) vec.get(0));
			return;
		}
		
		if(inMemoEdit){
			return;
		}
		if(sc.equals("neu")){
			neuanlageArzt("");
		}
		if(sc.equals("edit")){
			int row = arzttbl.getSelectedRow(); 
			if(row >= 0){
				String sid =  (String) arzttbl.getValueAt(row,9);
				neuanlageArzt(sid);
			}else{
				String mes = "\nWenn man einen Arzt ändern will, empfiehlt es sich\n"+ 
				"vorher den Arzt auszuwählen den man ändern will!!!";
				JOptionPane.showMessageDialog(null, mes);
				suchen.requestFocus();
				return;
			}
		}
		if(sc.equals("delete")){
			arztLoeschen();
		}
		if(sc.equals("formulare")){
			formulareAuswerten();
		}
	}
	
	
	public void controlsEin(boolean ein){
		suchen.setEnabled((ein ? true : false));
		arzttbl.setEnabled((ein ? true : false));
		memobut[0].setEnabled((ein ? true : false));
		memobut[1].setEnabled((ein ? false : true));
		memobut[2].setEnabled((ein ? false : true));
		jbut[0].setEnabled((ein ? true : false));
		jbut[1].setEnabled((ein ? true : false));
		jbut[2].setEnabled((ein ? true : false));
		jbut[3].setEnabled((ein ? true : false));
	}
	public void holeFormulare(){
		new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				// TODO Auto-generated method stub
				INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/arzt.ini");
				int forms = inif.getIntegerProperty("Formulare", "ArztFormulareAnzahl");
				for(int i = 1; i <= forms; i++){
					titel.add(inif.getStringProperty("Formulare","AFormularText"+i));			
					formular.add(inif.getStringProperty("Formulare","AFormularName"+i));
				}	
				return null;
			}
			
		}.execute();
		
	}

	public void formulareAuswerten(){
		int row = arzttbl.getSelectedRow(); 
		if(row >= 0){
			String sid = Integer.valueOf((String) arzttbl.getValueAt(row, 9)).toString();
    		iformular = -1;
    		KassenFormulare kf = new KassenFormulare(Reha.thisFrame,titel,formularid);
    		Point pt = jbut[3].getLocationOnScreen();
    		kf.setLocation(pt.x-100,pt.y+25);
    		kf.setModal(true);
    		kf.setVisible(true);
    		iformular = Integer.valueOf(formularid.getText());
    		kf = null;
    		final String xid = sid;
    		if(iformular >= 0){
    			new SwingWorker<Void,Void>(){

					@Override
					protected Void doInBackground() throws Exception {
						try{
							ArztTools.constructArztHMap(xid);
							OOTools.starteStandardFormular(Reha.proghome+"vorlagen/"+Reha.aktIK+"/"+formular.get(iformular),null);
						}catch(Exception ex){
							JOptionPane.showMessageDialog(null, "Fehler beim Bezug der Arztadresse");
						}
						return null;
					}
    			}.execute();
    			
    		}
 
    		//System.out.println("Es wurde Formular "+iformular+" gew�hlt");
        	
		}else{
			String mes = "\nWenn man einen Arzt anschreiben möchte, empfiehlt es sich\n"+ 
			"vorher den Arzt auszuwählen den man anschreiben möchte!!!\n\n"+
			"Aber trösten Sie sich, unser Herrgott hat ein Herz für eine ganz spezielle Randgruppe.\n"+
			"Sie dürfen also hoffen....\n\n";
			JOptionPane.showMessageDialog(null, mes);
			iformular = -1;
			suchen.requestFocus();
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public void neuanlageArzt(String id){
		ArztNeuDlg neuArzt = new ArztNeuDlg();
		//JDialog neuPat = new JDialog();
		PinPanel pinPanel = new PinPanel();
		pinPanel.getGruen().setVisible(false);
		pinPanel.setName("ArztNeuanlage");
		neuArzt.setPinPanel(pinPanel);
		if(id.equals("")){
			neuArzt.getSmartTitledPanel().setTitle("Arzt neu anlegen");			
		}else{
			neuArzt.getSmartTitledPanel().setTitle("Daten eines Arztes ändern");
		}

		neuArzt.setPinPanel(pinPanel);
		neuArzt.getSmartTitledPanel().setContentContainer(new ArztNeuanlage(neuArzt,getInstance(),new Vector(),id));
		neuArzt.getSmartTitledPanel().getContentContainer().setName("ArztNeuanlage");
		neuArzt.setName("ArztNeuanlage");
		//neuPat.setContentPane(new PatNeuanlage(new Vector()));
		neuArzt.setSize(470,480);
		neuArzt.setModal(true);
		neuArzt.setLocationRelativeTo(null);
		//neuPat.setLocation(new Point(200,50));
		neuArzt.setTitle("Arzt Neuanlage");
		final ArztNeuDlg xneuArzt = neuArzt; 
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run(){
		 			((ArztNeuanlage)xneuArzt.getSmartTitledPanel().getContentContainer()).setzeFocus();
		 	   }
		}); 	   	
		neuArzt.setVisible(true);
		neuArzt.setVisible(false);
		neuArzt = null;
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
	
	public void arztLoeschen(){
		int row = arzttbl.getSelectedRow(); 
		if(row >= 0){
        	int frage = JOptionPane.showConfirmDialog(null, "Wollen Sie diesen Arzt wirklich löschen??", "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
        	if(frage== JOptionPane.NO_OPTION){
        		return;
        	}
        	String kid = (String) arzttbl.getValueAt(row,9);
        	new ExUndHop().setzeStatement("delete from arzt where id='"+kid+"'");
			int model = arzttbl.convertRowIndexToModel(row);
			atblm.removeRow(model);
			arzttbl.revalidate();
			getInstance().arzttbl.repaint();
        	
		}else{
			String mes = "\nWenn man schon einen Arzt löschen will, empfiehlt es sich\n"+ 
			"vorher den Arzt auszuwählen den man löschen will!!!";
			JOptionPane.showMessageDialog(null, mes);
			suchen.requestFocus();
		}

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if(((JComponent)arg0.getSource()).getName().equals("ArztVerwaltung") && arg0.getClickCount()==2){
			int row = arzttbl.getSelectedRow(); 
			if(row >= 0){
				holeText();
				String sid =  (String) arzttbl.getValueAt(row,9);
				neuanlageArzt(sid);
				return;
			}
		}
		if(((JComponent)arg0.getSource()).getName().equals("ArztVerwaltung")){
			int row = arzttbl.getSelectedRow(); 
			if(row >= 0){
				holeText();
			}
		}

		
	}	@Override
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
	
	class ArztAction extends AbstractAction {
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
	    	if( inMemoEdit){
	    		return;
	    	}

	        if(e.getActionCommand().equals("f")){
	        	suchen.requestFocusInWindow();
	        }
	        if(e.getActionCommand().equals("n")){
	        	neuanlageArzt("");
	        }
	        if(e.getActionCommand().equals("e")){
				int row = arzttbl.getSelectedRow(); 
				if(row >= 0){
					String sid =  (String) arzttbl.getValueAt(row,9);
					neuanlageArzt(sid);
				}else{
					String mes = "\nWenn man einen Arzt ändern will, empfiehlt es sich\n"+ 
					"vorher den Arzt auszuwählen den man ändern will!!!";
					JOptionPane.showMessageDialog(null, mes);
					suchen.requestFocus();
				}

	        }	            
	        if(e.getActionCommand().equals("l")){
	        	arztLoeschen();
	        }
	        if(e.getActionCommand().equals("b")){
	        	formulareAuswerten();
	        }
	        
	    }
	}
	
}
class MyArztTableModel extends DefaultTableModel{
	   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public Class getColumnClass(int columnIndex) {
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
		@SuppressWarnings("unchecked")
		public Object getValueAt(int rowIndex, int columnIndex) {
			String theData = (String) ((Vector)getDataVector().get(rowIndex)).get(columnIndex); 
			Object result = null;
			//result = theData.toUpperCase();
			result = theData;
			return result;
		}
	    
	   
}


class ArztNeuDlg extends RehaSmartDialog implements RehaTPEventListener,WindowListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RehaTPEventClass rtp = null;
	public ArztNeuDlg(){
		super(null,"ArztNeuanlage");
		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener((RehaTPEventListener) this);

	}
	public void rehaTPEventOccurred(RehaTPEvent evt) {
		// TODO Auto-generated method stub
		try{
			this.setVisible(false);
			rtp.removeRehaTPEventListener((RehaTPEventListener) this);
			rtp = null;
			this.dispose();
			//System.out.println("****************Arzt Neu/ändern -> Listener entfernt**************");				
	
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
			//System.out.println("****************Arzt Neu/ändern -> Listener entfernt (Closed)**********");
		}
		
		
	}
	
}
