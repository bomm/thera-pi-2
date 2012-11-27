package arztBaustein;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import CommonTools.JCompTools;
import CommonTools.JRtaTextField;
import CommonTools.OOTools;
import CommonTools.SqlInfo;
import CommonTools.TableTool;
import ag.ion.bion.officelayer.NativeView;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.text.ITextCursor;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextRange;
import ag.ion.bion.officelayer.text.IViewCursor;
import ag.ion.noa.NOAException;
import ag.ion.noa.filter.OpenDocumentFilter;
import ag.ion.noa.frame.ILayoutManager;
import ag.ion.noa.internal.frame.LayoutManager;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.mysql.jdbc.PreparedStatement;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.frame.XLayoutManager;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.ui.XUIElement;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.view.DocumentZoomType;



public class ArztBausteinPanel extends JXPanel {

	/**
	 * 
	 */
	private JXPanel content = null;
	private static final long serialVersionUID = -3384203389588570947L;
	private JXTable bausteintbl = null;
	MyBausteinTableModel bausteinmod = null;
	
	private IFrame             officeFrame       = null;
	public	ITextDocument      document          = null;
	private JPanel             noaPanel          = null;
	private JXPanel			noaDummy = null;
	NativeView nativeView = null; 
	
	ActionListener al = null;
	KeyListener kl = null;
	MouseListener ml = null;
	
	JRtaTextField[] jtfs = {null,null,null};
	JRtaTextField suchenach = null;
	
	String[] rubriken = {" ","01.Allgem. u. klin. Anamnese","02.Jetzige Beschwerden u...",
				"03.Gegenwärtige Therapie","04.Allgem. Sozialanamnese","05.Arbeits- u. Berufsanamnese",						
				"06.Aufnahme, Vorbefunde...","07.Rehabilitationsdiagnosen",						
				"08.Rehabilitationsverlauf","09.Rehabilitationsergebnis",												
				"10.Sozialmed. Epikrise","99.sonstige Textbausteine"};																		

	Object[] varnamen = {"heutiges Datum","Anrede Pat.","Nachname Pat.","Vorname Pat.",
			"Geburtsdatum Pat.","Strasse Pat.","PLZ Pat.","Ort Pat.",
			"Aufnahmedatum","Entlassdatum","arbeitsfähig/-unfähig",
			"Der/Die Patient/in","der/die Patient/in","Er/Sie","er/sie","Seines/Ihres","seines/ihres",
			"Sein/Ihr","sein/ihr",
			"Dem/Der Patienten/in","dem/der Patienten/in","Des/Der Patienten/in","des/der Patienten/in",
			"Der/Die Versicherte","der/die Versicherte","Der/Die Rehabilitand/in","der/die Rehabilitand/in",
			"Seine/Ihre","seine/ihre",
			"Der/Die 99-jährige Pat.","der/die 99-jährige Pat.",
			"freie Variable setzen!!!!"};
	String[] varinhalt = {"Heute","Anrede","PatName","PatVorname","Geburtsdatum",
						"Strasse","PLZ","Ort","Aufnahme","Etlassung",
						"arbeitsfähig?","Der/Die Pat.","der/die Pat.",
						"Er/Sie","er/sie","Seines/Ihres","seines/ihres","Sein/Ihr","sein/ihr",
						"Dem/Der Pat.","dem/der Pat.","Des/Der Pat.","des/der Pat.",
						"Der/Die Vers.","der/die Vers.","Der/Die Rehab.","der/die Rehab.",
						"Seine/Ihre","seine/ihre",
						"Der/Die 99-jährige","der/die 99-jährige",
						"frei"};

	JComboBox jcmb = null;
	JList list = null;
	
	JButton suchen = null;
	JButton[] buts = {null,null,null,null,null};
	
	boolean neu = false;
	
	private boolean noapanelready = false;
	private boolean tablepanelready = false;

	public ArztBausteinPanel(){
		super();
		setSize(1024,800);
		setPreferredSize(new Dimension(1024,800));
		setLayout(new GridLayout());
		activateListener();
		add(constructSplitPaneLR(),BorderLayout.CENTER);
		//add(getnoaDummy(),BorderLayout.CENTER);
		/*
		try{
		noaDummy.add(getOOorgPanel());
		noaPanel.setVisible(true);
		fillNOAPanel();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		noapanelready = true;
		*/
		
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					noaDummy.add(getOOorgPanel());
					//noaPanel.setVisible(true);
					fillNOAPanel();		
				}catch(Exception ex){
					ex.printStackTrace();
				}
				noapanelready = true;
				return null;
			}
		}.execute();
		
		validate();
		setVisible(true);

		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				for(int i = 0; i < 10000;i++){
					if(noapanelready && tablepanelready){
						bausteintbl.setRowSelectionInterval(0, 0);
						//System.out.println(noapanelready+"  /  "+tablepanelready);
						setzeFocus();
						break;
					}else{
						//System.out.println(noapanelready+"  /  "+tablepanelready);
					}
					try {
						Thread.sleep(25);
					} catch (InterruptedException e) {
						
						e.printStackTrace();
					}
				}
				return null;
			}
			
		}.execute();
		/*
		*/
	}
	private void setzeFocus(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				suchenach.requestFocus();
			}
		});
	}
	private void activateListener(){
		/*******************************/
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("suchen")){
					doKeyTest();	
				}else if(cmd.equals("edit")){
					doEdit();
				}else if(cmd.equals("break")){
					doBreak();
				}else if(cmd.equals("save")){
					doSave();
				}else if(cmd.equals("neu")){
					doNeu();
				}else if(cmd.equals("delete")){
					doDelete();
				}
				
			}
		};
		/*******************************/
		ml = new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getClickCount()==2){
					if(arg0.getSource() instanceof JXTable){
						/*
						if(sucheNachPlatzhalter()){
							sendeText();							
						}
						suchenach.requestFocus();
						*/
					}
					if(arg0.getSource() instanceof JList){
						regleListe();
					}
				}
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
		};
		/*******************************/
		kl = new KeyListener(){
			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getSource() instanceof JRtaTextField){
					if(arg0.getKeyCode()==10){
						((JComponent)arg0.getSource()).requestFocus();
						doKeyTest();
					}
				}else if(arg0.getSource() instanceof JButton){
					if(arg0.getKeyCode()==10){
						//suchenach.requestFocus();
						doKeyTest();
					}
				}
			}
			@Override
			public void keyReleased(KeyEvent arg0) {
			}
			@Override
			public void keyTyped(KeyEvent arg0) {
			}
			
		};
	}
	private void controlsEin(boolean ein){
		jcmb.setEnabled(ein);
		jtfs[0].setEnabled(ein);
		jtfs[1].setEnabled(ein);
		list.setEnabled(ein);
	}
	/***************************************************/
	private void doDelete(){
		loeschen();
		controlsEin(false);
		neu = false;
		bausteintbl.setEnabled(true);
		this.regleButton(true, true, false, true, false);
	}
	/***************************************************/
	private void doNeu(){
		controlsEin(true);
		jcmb.setSelectedIndex(0);
		jtfs[0].setText("");
		jtfs[1].setText("");
		neu = true;
		bausteintbl.setEnabled(false);
		regleButton(false,false,true,false,true);
		document.getTextService().getText().setText("");
	}
	/***************************************************/
	private void doSave(){
		speichern(neu);
		controlsEin(false);
		neu = false;
		bausteintbl.setEnabled(true);
		this.regleButton(true, true, false, true, false);
	}
	/***************************************************/
	private void doBreak(){
		controlsEin(false);
		neu = false;
		bausteintbl.setEnabled(true);
		this.regleButton(true, true, false, true, false);
		holeIdUndText();
	}
	/***************************************************/
	private void doEdit(){
		int row = bausteintbl.getSelectedRow();
		if(row < 0){
			JOptionPane.showMessageDialog(null,"Bitte wählen Sie zuerst den Baustein aus den Sie bearbeiten wollen");
			return;
		}
		controlsEin(true);
		neu = false;
		bausteintbl.setEnabled(false);
		regleButton(false,false,true,false,true);
	}
	/***************************************************/
	private void regleButton(boolean neu,boolean edit,boolean save,boolean delete,boolean abbruch ){
		buts[0].setEnabled(neu);
		buts[1].setEnabled(edit);
		buts[2].setEnabled(save);
		buts[3].setEnabled(delete);
		buts[4].setEnabled(abbruch);
	}
	private void doKeyTest(){
		String suche = suchenach.getText().trim();
		if(suche.equals("")){
			fuelleTabelle("");
		}else{
			String[] spalten = {"tbtitel","tbuntert"};
			String where = SqlInfo.macheWhereKlausel("select tbthema,tbuntert,tbtitel,id from tbar where ", suchenach.getText().trim(), spalten);
			//System.out.println("where = "+where);
			fuelleTabelle(where+" Order BY tbthema");
		}
	}

	private JPanel getOOorgPanel(){
		noaPanel = new JPanel(new GridLayout());
		noaPanel.setPreferredSize(new Dimension(1024,800));
		noaPanel.validate();
		return noaPanel;
	}
	private JXPanel getnoaDummy(){
		noaDummy = new JXPanel(new GridLayout(0,1));
		return noaDummy;
	}

	private JXPanel getToolsPanel(){
		JXPanel pan = new JXPanel();   //1             2        3           4         5      6
		FormLayout lay = new FormLayout("0dlu,fill:0:grow(0.75),5dlu,fill:0:grow(0.25),5dlu,80dlu,0dlu",
				"0dlu,fill:0:grow(1.0),0dlu");
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		pan.add(getTablePanel(),cc.xy(2,2));
		pan.add(getControlPanel(),cc.xy(4,2));
		pan.add(getButtonPanel(),cc.xy(6,2));
		return pan;
	}

	private JXPanel getPlatzhalterPanel(){
		JXPanel pan = new JXPanel();
		final JXPanel xpan = pan;
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					FormLayout lay = new FormLayout("5dlu,fill:0:grow(1.0),5dlu",
					"15dlu,p,20dlu,fill:0:grow(1.0),5dlu");
					CellConstraints cc = new CellConstraints();
					xpan.setBackground(Color.WHITE);
					xpan.setLayout(lay);
					JLabel lab = new JLabel("System-Variable");
					lab.setForeground(Color.BLUE);
					xpan.add(lab,cc.xy(2, 2));
					list = new JList();
					list.setListData(varnamen);
					list.addMouseListener(ml);
					JScrollPane jscr = JCompTools.getTransparentScrollPane(list);
					jscr.setBorder(BorderFactory.createLineBorder(Color.GRAY));
					jscr.validate();
					xpan.add(jscr,cc.xy(2,4));
					xpan.validate();
					list.setEnabled(false);

				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}
			
		}.execute();
		return pan;
	}
	
	private JXPanel getButtonPanel(){
		JXPanel pan = new JXPanel();
		try{
			FormLayout lay = new FormLayout("5dlu,fill:0:grow(1.0),5dlu",
					//1   2  3   4  5   6  7    8  9  10 11 
					"3dlu,p,5dlu,p,5dlu,p,5dlu,p,5dlu,p,3dlu");
			CellConstraints cc = new CellConstraints();
			pan.setLayout(lay);
			pan.setBackground(Color.GRAY);
			buts[0] = new JButton("neuer Baustein");
			buts[0].setActionCommand("neu");
			buts[0].addActionListener(al);
			pan.add(buts[0],cc.xy(2,2));
			
			buts[1] = new JButton("Baustein ändern");
			buts[1].setActionCommand("edit");
			buts[1].addActionListener(al);
			pan.add(buts[1],cc.xy(2,4));
			
			buts[2] = new JButton("speichern");
			buts[2].setActionCommand("save");
			buts[2].addActionListener(al);
			pan.add(buts[2],cc.xy(2,6));

			buts[3] = new JButton("löschen");
			buts[3].setActionCommand("delete");
			buts[3].addActionListener(al);
			pan.add(buts[3],cc.xy(2,8));

			buts[4] = new JButton("abbrechen");
			buts[4].setActionCommand("break");
			buts[4].addActionListener(al);
			pan.add(buts[4],cc.xy(2,10));
			
			this.regleButton(true, true, false, true, false);

		}catch(Exception ex){
			ex.printStackTrace();
		}
		pan.validate();
		return pan;
	}
	private JXPanel getControlPanel(){
		JXPanel pan = new JXPanel();
		try{
		FormLayout lay = new FormLayout("0dlu,fill:0:grow(0.5),5dlu,fill:0:grow(0.5),0dlu",
				//1   2  3   4  5   6  7   8 9   10 11 12 13  14
				"3dlu,p,15dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,3dlu");
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		suchenach = new JRtaTextField("nix",true);
		suchenach.addKeyListener(kl);
		suchenach.setToolTipText("Geben Sie hier Ihr Suchkriterium ein");
		pan.add(suchenach,cc.xy(2,2,CellConstraints.FILL,CellConstraints.DEFAULT));
		suchen = new JButton("suchen");
		suchen.addKeyListener(kl);
		suchen.setActionCommand("suchen");
		suchen.addActionListener(al);
		pan.add(suchen,cc.xy(4,2));

		pan.add(new JLabel("Haupt-Rubrik"),cc.xyw(2,4,3,CellConstraints.DEFAULT,CellConstraints.BOTTOM));
		jcmb = new JComboBox(rubriken);
		jcmb.setEnabled(false);
		jcmb.setMaximumRowCount(5);
		pan.add(jcmb,cc.xyw(2,6,3));
		
		pan.add(new JLabel("Untergliederung"),cc.xyw(2,8,3,CellConstraints.DEFAULT,CellConstraints.BOTTOM));
		jtfs[0] = new JRtaTextField("nix",true);
		jtfs[0].setEnabled(false);
		pan.add(jtfs[0],cc.xyw(2,10,3));

		pan.add(new JLabel("Titel des Bausteins"),cc.xyw(2,12,3,CellConstraints.DEFAULT,CellConstraints.BOTTOM));
		jtfs[1] = new JRtaTextField("nix",true);
		jtfs[1].setEnabled(false);
		pan.add(jtfs[1],cc.xyw(2,14,3));
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return pan;
		
	}
	private JScrollPane getTablePanel(){ 
		bausteinmod = new MyBausteinTableModel();
		String[] columns = {"Haupt-Rubrik","Untergliederung","Titel des Bausteins","id"};
		bausteinmod.setColumnIdentifiers(columns);
		bausteintbl = new JXTable(bausteinmod);
		bausteintbl.getColumn(3).setMinWidth(0);
		bausteintbl.getColumn(3).setMaxWidth(0);
		bausteintbl.validate();
		bausteintbl.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.CLASSIC_LINE_PRINTER));
		bausteintbl.setName("tabelle");
		bausteintbl.addMouseListener(ml);
		bausteintbl.setSelectionMode(0);
		//tabaktrez.addPropertyChangeListener(this);
		bausteintbl.getSelectionModel().addListSelectionListener( new BausteinSelectionHandler());
		JScrollPane jscr = JCompTools.getTransparentScrollPane(bausteintbl);
		jscr.validate();
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					while(!ArztBaustein.DbOk){
						Thread.sleep(20);
					}
					fuelleTabelle("");	
					tablepanelready = true;
				}catch(Exception ex){
					ex.printStackTrace();
				}

				return null;
			}
			
		}.execute();
		return jscr;
	}
	private UIFSplitPane constructSplitPaneLR(){
		UIFSplitPane jSplitLR =  UIFSplitPane.createStrippedSplitPane(JSplitPane.HORIZONTAL_SPLIT,
        		constructSplitPaneOU(),getPlatzhalterPanel());
		jSplitLR.setOpaque(false);
		jSplitLR.setDividerSize(7);
		jSplitLR.setDividerBorderVisible(true);
		jSplitLR.setName("PatGrundSplitLinksRechts");
		jSplitLR.setOneTouchExpandable(true);
		jSplitLR.setDividerColor(Color.LIGHT_GRAY);
		jSplitLR.setDividerLocation(850);
		jSplitLR.validate();
		jSplitLR.setVisible(true);
		jSplitLR.validate();
		return jSplitLR;
	}
	private UIFSplitPane constructSplitPaneOU(){
		UIFSplitPane jSplitRechtsOU =  UIFSplitPane.createStrippedSplitPane(JSplitPane.VERTICAL_SPLIT,
        		getToolsPanel(),
        		getnoaDummy());
		jSplitRechtsOU.setOpaque(false);
		jSplitRechtsOU.setDividerSize(7);
		jSplitRechtsOU.setDividerBorderVisible(true);
		jSplitRechtsOU.setName("PatGrundSplitRechteSeiteObenUnten");
		jSplitRechtsOU.setOneTouchExpandable(true);
		jSplitRechtsOU.setDividerColor(Color.LIGHT_GRAY);
		jSplitRechtsOU.setDividerLocation(175);
		jSplitRechtsOU.validate();
		return jSplitRechtsOU;
	}	
	private void holeIdUndText(){
		int row = bausteintbl.getSelectedRow();
		if(row >= 0){
			row = bausteintbl.convertRowIndexToModel(row);
			String id = bausteinmod.getValueAt(row, 3).toString();
			holeTextBaustein(id);
			jcmb.setSelectedItem(bausteinmod.getValueAt(row, 0).toString());
			jtfs[0].setText( bausteinmod.getValueAt(row, 1).toString() );
			jtfs[1].setText( bausteinmod.getValueAt(row, 2).toString() );
		}
	}
	private void holeTextBaustein(String id){
		InputStream ins = SqlInfo.holeStream("tbar", "tbtext", "id='"+id+"'");
/*****/
		/*
	    File f=new File(ArztBausteine.proghome+"tbout.tb");
	    f.deleteOnExit();
	    OutputStream out;
		try {
			out = new FileOutputStream(f);
		    byte buf[]=new byte[8192];
		    int len;
		    while((len=ins.read(buf))>0)
		    out.write(buf,0,len);
		    out.close();
		    ins.close();		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
/*****/		
		
		//String instring = SqlInfo.holeEinzelFeld("select tbtext from tbar where id='"+id+"' LIMIT 1");
		document.getTextService().getText().setText("");
		
		ITextCursor textCursor = null;
		IViewCursor viewCursor = null;
		try {
			textCursor = document.getTextService().getCursorService().getTextCursor();
			textCursor.insertDocument(ins, OpenDocumentFilter.FILTER);
			
			//textCursor.insertDocument(ArztBausteine.proghome+"tbout.tb");
			//textCursor.insertDocument(ins, RTFFilter.FILTER);
			textCursor.gotoStart(false);
			viewCursor = document.getViewCursorService().getViewCursor();
			viewCursor.getPageCursor().jumpToFirstPage();
			viewCursor.getPageCursor().jumpToStartOfPage();
			//ins.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}	
	private void fillNOAPanel() {
	    if (noaPanel != null) {
		      try {
		    	  System.out.println("Konstruiere das NOA-Panel");
		        officeFrame = constructOOOFrame(ArztBaustein.officeapplication, noaPanel);
		        document = (ITextDocument) ArztBaustein.officeapplication.getDocumentService().constructNewDocument(officeFrame,
		            IDocument.WRITER,
		            DocumentDescriptor.DEFAULT);
		        
	        	OOTools.setzePapierFormat(document, new Integer(25199), new Integer(19299));
	        	OOTools.setzeRaender(document, new Integer(1000), new Integer(1000),new Integer(1000),new Integer(1000));
		        hideElements(LayoutManager.URL_MENUBAR);
	        	//nativeView.validate();
		        try {
					document.zoom(DocumentZoomType.BY_VALUE, (short)100);
				} catch (DocumentException e) {
					e.printStackTrace();
				}
		        noaPanel.setVisible(true);		      }
		      catch (Throwable throwable) {
		        noaPanel.add(new JLabel("Ein Fehler ist aufgetreten: " + throwable.getMessage()));
		      }
		      noapanelready = true;
		    }
		  }
	
	private IFrame constructOOOFrame(IOfficeApplication officeApplication, final Container parent) throws Throwable {
  	  System.out.println("Konstruiere die Native-View");
	    nativeView = new NativeView(ArztBaustein.OpenOfficeNativePfad);
	    parent.add(nativeView);
	    parent.validate();
	    parent.setVisible(true);	    
	    parent.addComponentListener(new ComponentAdapter() {
	      public void componentResized(ComponentEvent e) {
	        nativeView.setPreferredSize(new Dimension(parent.getWidth() - 5, parent.getHeight() - 5));
	        parent.getLayout().layoutContainer(parent);
	    	refreshSize();
	      }
	    });

	    nativeView.setPreferredSize(new Dimension(parent.getWidth() - 5, parent.getHeight() - 5));
	    parent.getLayout().layoutContainer(parent);
	    officeFrame = officeApplication.getDesktopService().constructNewOfficeFrame(nativeView);
	    return officeFrame;
	}
	private void hideElements(String url ) throws UnknownPropertyException, PropertyVetoException, IllegalArgumentException, WrappedTargetException, NOAException{
        ILayoutManager layoutManager = officeFrame.getLayoutManager();
        XLayoutManager xLayoutManager = layoutManager.getXLayoutManager();
        XUIElement element = xLayoutManager.getElement(url);
        if (element != null) {
            XPropertySet xps = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, element);
            xps.setPropertyValue("Persistent", new Boolean(false));
            xLayoutManager.hideElement(url);
        }
	}
	
	public final void refreshSize() {
		noaPanel.setPreferredSize(new Dimension(noaPanel.getWidth() , noaPanel.getHeight()- 5));
		final Container parent = noaPanel.getParent();
		if (parent instanceof JComponent) {
			((JComponent) parent).revalidate();
		}
		final Window window1 = SwingUtilities.getWindowAncestor(nativeView.getParent().getParent());
		if (window1 != null) {
			window1.validate();
		}
		noaPanel.getLayout().layoutContainer(noaPanel);

	}
	private void loeschen(){
		int row = 0;
		String id = "-1";
		row = bausteintbl.getSelectedRow();
		if(row >= 0){
			row = bausteintbl.convertRowIndexToModel(row);
			id = bausteinmod.getValueAt(row, 3).toString();
		}else{
			return;
		}
		int anfrage = JOptionPane.showConfirmDialog(null, "Wollen Sie diesen Baustein wirklich löschen?\n\n"+
				"Baustein= "+bausteinmod.getValueAt(row, 2).toString(),"Wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
		if(anfrage == JOptionPane.YES_OPTION){
			TableTool.loescheRowAusModel(bausteintbl,-1);
			SqlInfo.sqlAusfuehren("delete from tbar where id='"+id+"' LIMIT 1");
		}
		return;
	}
	private void speichern(boolean neu){
		String cmd = "";
		String id = "";
		int row = 0;
		if(neu){
			id = Integer.toString(SqlInfo.holeId("tbar","tbtext"));
			if(id.equals("-1")){
				JOptionPane.showMessageDialog(null,"Fehler beim speichern des neuen Bausteins\nBitte verständigen Sie den Administrator!");
				return;
			}
		}else{
			row = bausteintbl.getSelectedRow();
			if(row >= 0){
				row = bausteintbl.convertRowIndexToModel(row);
				id = bausteinmod.getValueAt(row, 3).toString();
			}else{
				JOptionPane.showMessageDialog(null,"Fehler beim speichern des Bausteins\nBitte verständigen Sie den Administrator!");
				return;
			}
		}
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try {
			document.getPersistenceService().store(bout);
			bout.flush();
			bout.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PreparedStatement ps = null;
		boolean ret = false;
		Statement stmt = null;

		try {
			stmt = (Statement) ArztBaustein.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE );
			
			String select = "update tbar set tbthema = ? , tbuntert = ?, tbtitel = ?,"+
			"tbtext = ? where id = ? LIMIT 1";
			 ps = (PreparedStatement) ArztBaustein.thisClass.conn.prepareStatement(select);
			 ps.setString(1, jcmb.getSelectedItem().toString());			  
			 ps.setString(2, jtfs[0].getText());
			 ps.setString(3, jtfs[1].getText());			  
			 ps.setBytes(4, bout.toByteArray());
			 ps.setInt(5, Integer.parseInt(id));
			 ps.execute();			 
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		finally {
			if (stmt != null) {
				try {
					stmt.close();
					stmt = null;
				} catch (SQLException sqlEx) {
					stmt = null;
				}
			}
			if(ps != null){
				try {
					ps.close();
					ps = null;
				} catch (SQLException e) {
					ps = null;
				}
			}
		}
		if(!neu){
			bausteinmod.setValueAt(jcmb.getSelectedItem().toString(), row, 0);
			bausteinmod.setValueAt(jtfs[0].getText(), row, 1);
			bausteinmod.setValueAt(jtfs[1].getText(), row, 2);
		}
		
	}
	private void fuelleTabelle(String cmd){

		Statement stmt = null;
		ResultSet rs = null;
		Vector<String> retvec = new Vector<String>();
		int bausteine = 0;
		bausteinmod.setRowCount(0);
		try {
			stmt =  ArztBaustein.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			String sstmt = null;
			ArztBaustein.thisClass.jFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			if(cmd.equals("")){
				sstmt = "select tbthema,tbuntert,tbtitel,id from tbar Order BY tbthema";					
			}else{
				sstmt = cmd;
			}
			rs = stmt.executeQuery(sstmt);
			while(rs.next()){
				retvec.clear();
				retvec.add( (rs.getString(1)==null  ? "" :  rs.getString(1)) );
				retvec.add( (rs.getString(2)==null  ? "" :  rs.getString(2)) );
				retvec.add( (rs.getString(3)==null  ? "" :  rs.getString(3)) );
				retvec.add( (rs.getString(4)==null  ? "" :  rs.getString(4)) );
				bausteinmod.addRow((Vector<?>)retvec.clone());

				if(bausteine==0){
					final String id = ((Vector<String>)retvec).get(3).toString();
					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() throws Exception {
							//bausteintbl.setRowSelectionInterval(0, 0);
							return null;
						}
					}.execute();
				}

				bausteine++;
			}
			ArztBaustein.thisClass.jFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			retvec.clear();
			retvec = null;
		}catch(SQLException ev){
			System.out.println("SQLException: " + ev.getMessage());
			System.out.println("SQLState: " + ev.getSQLState());
			System.out.println("VendorError: " + ev.getErrorCode());
		}

		finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException sqlEx) { // ignore }
					rs = null;
				}
			}	
			if (stmt != null) {
				try {
					stmt.close();
					stmt = null;
				} catch (SQLException sqlEx) { // ignore }
					stmt = null;
				}
			}
		}
	}
	private void regleListe(){
		int pos = list.getSelectedIndex();
		String toSet = "";
		if(pos== (varinhalt.length-1)){
			//freie Variable
			Object ret = JOptionPane.showInputDialog(this,"Bitte einen Namen für die Variable eingeben","Baustein-Variable setzen", 1);
			if(ret==null){
				toSet = "^NoName set^";
			}else{
				toSet = "^"+ret.toString().trim()+"^";
			}
		}else{
			//System-Variable
			toSet = "^"+varinhalt[pos]+"^";
		}
		insertTextAtCurrentPosition(toSet);
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				nativeView.requestFocus();
			}
		});
	}
	public void insertTextAtCurrentPosition(String xtext){
	    IViewCursor viewCursor = document.getViewCursorService().getViewCursor();
	    ITextRange textRange = viewCursor.getStartTextRange();
	    textRange.setText(xtext);
	    try {
			document.setModified(false);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	

	/*****************************************/
	class MyBausteinTableModel extends DefaultTableModel{
		   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Class<?> getColumnClass(int columnIndex) {
			   if(columnIndex==1 || columnIndex==5 ){
				   return String.class;}
			   else{
				   return String.class;
			   }
	       }

		    public boolean isCellEditable(int row, int col) {
		        if (col == 0){
		        	return false;
		        }else if(col == 3){
		        	return false;
		        }else if(col == 7){
		        	return false;
		        }else if(col == 11){
		        	return false;
		        } else{
		          return false;
		        }
		      }
		   
	}	
	class BausteinSelectionHandler implements ListSelectionListener {

	    public void valueChanged(ListSelectionEvent e) {
	        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
	        boolean isAdjusting = e.getValueIsAdjusting();
	        if(isAdjusting){
	        	return;
	        }
	        if (lsm.isSelectionEmpty()) {
	        }else {
	            holeIdUndText();
	        }
	    }
	}
}
