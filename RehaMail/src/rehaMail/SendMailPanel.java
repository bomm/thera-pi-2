package rehaMail;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;

import rehaMail.MailPanel.EinListSelectionHandler;

import Tools.ButtonTools;
import Tools.DatFunk;
import Tools.DateTableCellRenderer;
import Tools.DblCellEditor;
import Tools.DoubleTableCellRenderer;
import Tools.JCompTools;
import Tools.JRtaTextField;
import Tools.MitteRenderer;
import Tools.SqlInfo;
import Tools.UIFSplitPane;
import ag.ion.bion.officelayer.NativeView;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.desktop.GlobalCommands;
import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.text.IParagraph;
import ag.ion.bion.officelayer.text.ITextCursor;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.IViewCursor;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;
import ag.ion.noa.filter.OpenDocumentFilter;
import ag.ion.noa.frame.ILayoutManager;
import ag.ion.noa.internal.frame.LayoutManager;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XNameAccess;
import com.sun.star.frame.XLayoutManager;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.text.XTextContent;
import com.sun.star.text.XTextGraphicObjectsSupplier;
import com.sun.star.ui.XUIElement;
import com.sun.star.uno.Any;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.view.DocumentZoomType;

public class SendMailPanel extends JXPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5871553539357474995L;
	

	
	Tools.DateTableCellEditor tabDateEditor = new Tools.DateTableCellEditor();
	DateTableCellRenderer tabDateRenderer = new DateTableCellRenderer(true);
	
	DblCellEditor tabDoubleEditor = new DblCellEditor();
	DoubleTableCellRenderer tabDoubleRenderer = new DoubleTableCellRenderer();
	
	Tools.IntTableCellEditor tabIntegerEditor = new Tools.IntTableCellEditor();
	Tools.IntTableCellRenderer tabIntegerRenderer = new Tools.IntTableCellRenderer();
	
	JRtaTextField sqlstatement = null;
	
	DecimalFormat dcf = new DecimalFormat("##########0.00");
	SimpleDateFormat datumsFormat = new SimpleDateFormat ("dd.MM.yyyy"); //Konv.
	ActionListener al = null;
	JScrollPane jscr = null;
	JXPanel grundpanel;
	
	/***********/
	JXTable eintab = null;
	EinTableModel einmod = null;
	/***********/
	
	private IFrame             officeFrame       = null;
	public	ITextDocument      document          = null;
	private JXPanel             noaPanel          = null;

	NativeView nativeView = null;
	DocumentDescriptor xdescript = null;
	
	boolean gelesen = false;
	String aktId = "";
	String aktAbsender = "";
	String aktBetreff = "";
	JButton[] buts = {null,null,null,null,null};
	
	EinListSelectionHandler listhandler = null;

	Vector<String> attachmentFileName = new Vector<String>();
	

	public SendMailPanel(){
		super(new BorderLayout());
		/**************/
		try{
			activateListener();
			/******************/
			JXPanel pan = new JXPanel();
			pan.setOpaque(false);
			String xwert = "fill:0:grow(1.0),p";
			String ywert = "5px,p,5px";
			FormLayout lay = new FormLayout(xwert,ywert);
			CellConstraints cc = new CellConstraints();
			pan.setLayout(lay);

			pan.add(getToolbar(),cc.xy(1, 2,CellConstraints.FILL,CellConstraints.FILL));
			pan.add(getAttachmentButton(),cc.xy(2,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
			pan.validate();
			add(pan,BorderLayout.NORTH);
			/*******************/
			
			add(constructSplitPaneOU(),BorderLayout.CENTER);
			//noaDummy.add(getOOorgPanel());
			//noaPanel.setVisible(true);
			//setVisible(true);
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					if(!RehaMail.officeapplication.isActive()){
						//System.out.println("Aktiviere Office...");
						RehaMail.starteOfficeApplication();
					}
					fillNOAPanel();
					checkForNewMail();
					return null;
				}
			}.execute();
					

		}catch(Exception ex){
			ex.printStackTrace();
		}
		validate();		
		/**************/
		/*
		setOpaque(false);
		String xwert = "fill:0:grow(1.0),p";
		String ywert = "fill:0:grow(0.5),2px,p,2px,fill:0:grow(0.5)";
		FormLayout lay = new FormLayout(xwert,ywert);
		CellConstraints cc = new CellConstraints();
		setLayout(lay);

		add(getTabelle(),cc.xyw(1,1,2));
		*/
	}
	/******************************************/
	public void updateMails(){
		/********
		 * 
		 */
		
		
	}
	public void checkForNewMail(){
		doStatementAuswerten("select empfaenger_person,"+
				"gelesen,versanddatum,gelesendatum,betreff,id from pimail where absender='"+
				RehaMail.mailUser+"' order by gelesen DESC,versanddatum DESC");
		for(int i = 0; i < 4; i++){
			if(buts[i] != null){
				buts[i].setEnabled(true);	
			}
		}
	}
	
	/******************************************/	
	private Tools.UIFSplitPane constructSplitPaneOU(){
		UIFSplitPane jSplitRechtsOU =  UIFSplitPane.createStrippedSplitPane(JSplitPane.VERTICAL_SPLIT,
        		getTabelle(),
        		getOOorgPanel());
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
	
	public JXPanel getTabelle(){
		JXPanel pan = new JXPanel();
		pan.setOpaque(false);
		String xwert = "fill:0:grow(1.0)";
		String ywert = "fill:0:grow(1.0)";
		FormLayout lay = new FormLayout(xwert,ywert);
		CellConstraints cc = new CellConstraints(); 
		pan.setLayout(lay);
		
		einmod = new EinTableModel();
		einmod.setColumnIdentifiers(new String[] {"Empfänger","gelesen","Abs.Datum","Empf.Datum","Betreff","id"});
		eintab = new JXTable(einmod);
		
		eintab.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getClickCount()==2 && arg0.getButton()==1){
					if(!gelesen){
						//holeNeueMail();
						//setzeGelesen();
					}
				}
			}	
		});
		eintab.getColumn(0).setMinWidth(120);
		eintab.getColumn(0).setMaxWidth(120);
		eintab.getColumn(1).setMaxWidth(50);
		eintab.getColumn(2).setMaxWidth(100);
		eintab.getColumn(2).setMinWidth(100);
		eintab.getColumn(2).setCellEditor(tabDateEditor);
		eintab.getColumn(2).setCellRenderer(tabDateRenderer);
		eintab.getColumn(3).setCellEditor(tabDateEditor);
		eintab.getColumn(3).setCellRenderer(new MitteRenderer());
		eintab.getColumn(3).setMinWidth(155);
		eintab.getColumn(3).setMaxWidth(155);
		eintab.getColumn(5).setMinWidth(0);
		eintab.getColumn(5).setMaxWidth(0);


		eintab.setFont(new Font("Courier New",12,12));
		eintab.getSelectionModel().addListSelectionListener( (listhandler=new EinListSelectionHandler()));
		jscr = JCompTools.getTransparentScrollPane(eintab);
		jscr.validate();
		pan.add(jscr,cc.xy(1,1));
		pan.validate();
		return pan;
	}
	
	private void activateListener(){
		 
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("attachments")){
					JOptionPane.showMessageDialog(null, "Funktion noch nicht implementiert");
					return;
				}
				if(cmd.equals("newMail")){
					JOptionPane.showMessageDialog(null, "Funktion noch nicht implementiert");
					return;
				}
				if(cmd.equals("replyMail")){
					JOptionPane.showMessageDialog(null, "Funktion noch nicht implementiert");
					return;
				}
				if(cmd.equals("loeschen")){
					JOptionPane.showMessageDialog(null, "Funktion noch nicht implementiert");
					return;
				}

				
				if(cmd.equals("print")){
					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() throws Exception {
							try {
								document.getFrame().getDispatch(GlobalCommands.PRINT_DOCUMENT).dispatch(); 
							} catch (NOAException e) {
								e.printStackTrace();
							}	
							Thread.sleep(150);
							return null;
						}
						
					}.execute();
					return;					
				}
				
			}
		};
	}	

	
	private JXPanel getAttachmentButton(){
		JXPanel pan = new JXPanel();
		pan.setOpaque(false); 
		
		buts[4]=ButtonTools.macheButton("", "attachments", al);
		buts[4].setIcon(RehaMail.attachmentIco[3]);
		buts[4].setToolTipText("Dateianhänge holen/ansehen");
		//buts[4].setEnabled(false);
		pan.add(buts[4]);
		pan.validate();
		return pan;
		
	}

	
	private JToolBar getToolbar(){
		JToolBar jtb = new JToolBar();
		jtb.setOpaque(false);
		jtb.setRollover(true);
		jtb.setBorder(null);
		jtb.addSeparator(new Dimension(0,30));
		jtb.add( (buts[0]=ButtonTools.macheButton("", "newMail", al)));
		Image ico = new ImageIcon(RehaMail.progHome+"icons/package-install.png").getImage().getScaledInstance(26,26, Image.SCALE_SMOOTH);
		buts[0].setIcon(new ImageIcon(ico));
		buts[0].setToolTipText("eine neue Nachricht erstellen");
		jtb.addSeparator(new Dimension(15,30));
		jtb.add( (buts[1]=ButtonTools.macheButton("", "replyMail", al)));
		ico = new ImageIcon(RehaMail.progHome+"icons/edit-undo.png").getImage().getScaledInstance(26,26, Image.SCALE_SMOOTH);
		buts[1].setIcon(new ImageIcon(ico));
		buts[1].setToolTipText("auf die gewählte Nachricht antworten");
		jtb.addSeparator(new Dimension(15,30));
		jtb.add( (buts[3]=ButtonTools.macheButton("", "loeschen", al)));
		ico = new ImageIcon(RehaMail.progHome+"icons/package-remove-red.png").getImage().getScaledInstance(26,26, Image.SCALE_SMOOTH);
		buts[3].setIcon(new ImageIcon(ico));
		buts[3].setToolTipText("die gewählte Nachricht loeschen");
		jtb.addSeparator(new Dimension(75,30));

		ico = new ImageIcon(RehaMail.progHome+"icons/document-print.png").getImage().getScaledInstance(26,26, Image.SCALE_SMOOTH);
		jtb.add( (buts[2]=ButtonTools.macheButton("", "print", al)));
		buts[2].setIcon(new ImageIcon(ico));
		buts[2].setToolTipText("die gewählte Nachricht drucken");
		jtb.addSeparator(new Dimension(50,30));
		
		
		
		return jtb;
	}
	private JXPanel getOOorgPanel(){

		noaPanel = new JXPanel(new BorderLayout());
		noaPanel.setDoubleBuffered(true);
		return noaPanel;
	}
	
	/********OO.org-Gedönse*******
	 * 
	 * 
	 * 
	 * ************/
	private void fillNOAPanel() {
	    if (noaPanel != null) {
		      try {
		        officeFrame = constructOOOFrame(RehaMail.officeapplication, noaPanel);
		        DocumentDescriptor desc = DocumentDescriptor.DEFAULT;
		        desc.setReadOnly(true);
		        document = (ITextDocument) RehaMail.officeapplication.getDocumentService().constructNewDocument(officeFrame,
		            IDocument.WRITER,
		            desc);
		        hideElements(LayoutManager.URL_MENUBAR);
		        hideElements(LayoutManager.URL_STATUSBAR);
		        hideElements(LayoutManager.URL_TOOLBAR_STANDARDBAR);
		        hideElements(LayoutManager.URL_TOOLBAR);

	        	//Tools.OOTools.setzePapierFormat(document, new Integer(25199), new Integer(19299));
	        	Tools.OOTools.setzeRaender(document, new Integer(1000), new Integer(1000),new Integer(1000),new Integer(1000));

	        	//nativeView.validate();
		        try {
					document.zoom(DocumentZoomType.BY_VALUE, (short)80);
				} catch (DocumentException e) {
					e.printStackTrace();
				}
		        /*noaPanel.setVisible(true);*/		      }
		      catch (Throwable throwable) {
		        noaPanel.add(new JLabel("<html>Ein Fehler ist aufgetreten:<br>" + throwable.getMessage()+"</html>"));
		      }
		    }
		  }

	private IFrame constructOOOFrame(IOfficeApplication officeApplication, final Container parent) throws Throwable {
	    nativeView = new NativeView(RehaMail.officeNativePfad);
	    parent.add(nativeView);
	    parent.addComponentListener(new ComponentAdapter() {
	      public void componentResized(ComponentEvent e) {
	    	refreshSize();
	        nativeView.setPreferredSize(new Dimension(parent.getWidth() - 5, parent.getHeight() - 5));
	        parent.getLayout().layoutContainer(parent);
	      }
	    });

	    nativeView.setPreferredSize(new Dimension(parent.getWidth() - 5, parent.getHeight() - 5));
	    parent.getLayout().layoutContainer(parent);
	    officeFrame = officeApplication.getDesktopService().constructNewOfficeFrame(nativeView);
	    return officeFrame;
	}
	private void hideElements(String url ) throws  PropertyVetoException, IllegalArgumentException, WrappedTargetException, NOAException, UnknownPropertyException{
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
	
	public void holeAttachments(){
		this.attachmentFileName.clear();
		this.attachmentFileName.trimToSize();
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				Vector<Vector<String>> vec = SqlInfo.holeFelder("select file1,file2,file3 from pimail where id ='"+aktId+"' Limit 1" );
				for(int i = 0; i < vec.get(0).size();i++){
					if(! vec.get(0).get(i).trim().equals("")){
						attachmentFileName.add(String.valueOf(vec.get(0).get(i).trim()));
					}
				}
				if(attachmentFileName.size() > 0){
					buts[4].setEnabled(true);
				}else{
					buts[4].setEnabled(false);
				}
			}
		});
	}

	
	/*********************
	 * 
	 * 
	 * 
	 * 
	 * 
	 * ******************/
	
	private void doStatementAuswerten(final String stat){
		
		
		
		
		einmod.setRowCount(0);
		
		
		Statement stmt = null;
		ResultSet rs = null;
		//ResultSet md = null;
		
		einmod.setRowCount(0);
		Vector<Object> vec = new Vector<Object>();
		int durchlauf = 0;

		//Saudummerweise entspricht der Rückgabewert von getColumnTypeName() oder
		//getColumnType() nicht der Abfrag von describe tabelle
		//so werden alle Integer-Typen unter INT zusammengefaßt
		//Longtext, Mediumtext, Varchar = alles VARCHAR
		//CHAR kann sowohl ein einzelnes Zeichen als auch enum('T','F') also boolean sein...
		//eigentlich ein Riesenmist!

		eintab.getSelectionModel().removeListSelectionListener(listhandler);	
		try {

			stmt =  RehaMail.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
			rs = stmt.executeQuery(stat);
			
			
			while(rs.next()){
				vec.clear();
				try{
					vec.add((String) (rs.getString(1)==null ? "" : rs.getString(1)));
					vec.add((Boolean) (rs.getString(2)==null ?  Boolean.FALSE : (rs.getString(2).equals("T") ? Boolean.TRUE : Boolean.FALSE)) );
					vec.add(rs.getDate(3));
					vec.add((String) (rs.getString(4)==null ? "" : getTimestampString(rs.getString(4)) ));
					vec.add((String) (rs.getString(5)==null ? "" : rs.getString(5)));
					vec.add((String) (rs.getString(6)==null ? "" : rs.getString(6)));
				
				}catch(Exception ex){
					ex.printStackTrace();
				}
				einmod.addRow( (Vector<?>) vec.clone());
				if(einmod.getRowCount()==1){
					SwingUtilities.invokeLater(new Runnable(){
						public void run(){
							eintab.getSelectionModel().addListSelectionListener(listhandler);
							eintab.setRowSelectionInterval(0, 0);		
						}
					});
					
				}

				if(durchlauf>200){
					try {
						eintab.validate();
						eintab.repaint();
						Thread.sleep(80);
						durchlauf = 0;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				durchlauf++;
			}
			
			eintab.validate();
			eintab.repaint();
			jscr.validate();
			//doSetAbfrageErgebnis();
			
			

		} catch (SQLException e) {
			e.printStackTrace();
			//textArea.setText(e.getMessage()+"\n"+textArea.getText());
		}finally {
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


		
		
	}
	
	private String getTimestampString(String ts){
		try{
			return DatFunk.sDatInDeutsch(ts.split(" ")[0].trim())+"-"+ts.split(" ")[1].trim().substring(0,8);
		}catch(Exception ex){
			
		}
		return "";
	}
	

	

	class EinTableModel extends DefaultTableModel{
		   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Class<?> getColumnClass(int columnIndex) {
			if(columnIndex==0){return String.class;}
			if(columnIndex==1){return Boolean.class;}
			if(columnIndex==2){return Date.class;}
			if(columnIndex==3){return Timestamp.class;}
			if(columnIndex==4){return String.class;}
			if(columnIndex==5){return String.class;}
			
		   return String.class;
	    }

		public boolean isCellEditable(int row, int col) {
			return false;
		}
		   
	}
	/******************************************************/
	public void allesAufNull(){
		document.getTextService().getText().setText("");
		loescheBilder();
		loescheParagraphen();
		eintab.getSelectionModel().removeListSelectionListener(listhandler);
		einmod.setRowCount(0);
		eintab.validate();
		eintab.repaint();
		eintab.getSelectionModel().addListSelectionListener(listhandler);
		RehaMail.updateTitle("unbekannt");
		for(int i = 0; i < 5; i++){
			buts[i].setEnabled(false);
		}
	}

	protected void loescheBilder(){
		ITextDocument textDocument = (ITextDocument)document;
		//resolveControls(textDocument.getXTextDocument().getText());
		/************************/
		XTextGraphicObjectsSupplier graphicObjSupplier = (XTextGraphicObjectsSupplier) UnoRuntime.queryInterface(XTextGraphicObjectsSupplier.class,
        	      textDocument.getXTextDocument());
		XNameAccess nameAccess = graphicObjSupplier.getGraphicObjects();
		
		String[] names = nameAccess.getElementNames();
		try{
		for(int i = 0; i < names.length;i++){
			Any xImageAny = (Any) nameAccess.getByName(names[i]);
			Object xImageObject = xImageAny.getObject();
		    XTextContent xImage = (XTextContent) xImageObject;
		    xImage.dispose();
		}
		}catch(Exception ex){
			ex.printStackTrace();
		}

	}
	protected void loescheParagraphen(){
		ITextDocument textDocument = (ITextDocument)document;
		IParagraph paragraphs[];
		try {
			paragraphs = textDocument.getTextService().getText().getTextContentEnumeration().getParagraphs();
			for(int i = 0; i < paragraphs.length; i++) {
				XTextContent textContent = paragraphs[i].getXTextContent();
				textContent.getAnchor().setString("");
				textContent.dispose();
			}	

		} catch (TextException e) {
			e.printStackTrace();
		}
		
	}
	
	public void tabelleLeeren(){
		einmod.setRowCount(0);
		eintab.validate();
		eintab.repaint();
	}

	public void textEinlesen(){
		if(einmod.getRowCount()<=0){tabelleLeeren();return;}
		
		int row = eintab.getSelectedRow();
		while(document==null || !RehaMail.DbOk);
		if(row < 0){tabelleLeeren();return;}
		gelesen = (Boolean)einmod.getValueAt(eintab.convertRowIndexToModel(row),1 );
		aktId = einmod.getValueAt(eintab.convertRowIndexToModel(row),5 ).toString();
		/****************************************************/
		ByteArrayInputStream ins = null;
		try{
			ins = (ByteArrayInputStream)SqlInfo.holeStream("pimail", "emailtext", "id='"+aktId+"' LIMIT 1");
			document.getTextService().getText().setText("");
			loescheBilder();
			loescheParagraphen();
			/************************/
			ITextCursor textCursor = document.getTextService().getCursorService().getTextCursor();
			textCursor.gotoStart(true);
			textCursor.insertDocument(ins,OpenDocumentFilter.FILTER);
			IViewCursor viewCursor = document.getViewCursorService().getViewCursor();
			viewCursor.getPageCursor().jumpToFirstPage();
			viewCursor.getPageCursor().jumpToStartOfPage();
			ins.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		

	}
	class EinListSelectionHandler implements ListSelectionListener {
		
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
	            		gelesen = (Boolean)einmod.getValueAt(eintab.convertRowIndexToModel(i),1 );
	            		aktId = einmod.getValueAt(eintab.convertRowIndexToModel(i),5 ).toString();
	            		aktAbsender = einmod.getValueAt(eintab.convertRowIndexToModel(i),0 ).toString();
	            		aktBetreff = einmod.getValueAt(eintab.convertRowIndexToModel(i),4 ).toString();
	            		if(RehaMail.thisFrame != null)
	            		RehaMail.thisFrame.setCursor(RehaMail.WAIT_CURSOR);
	            		textEinlesen();
	            		if(RehaMail.thisFrame != null)
	            		RehaMail.thisFrame.setCursor(RehaMail.DEFAULT_CURSOR);
	                    break;
	                }
	            }
	        }

	    }
	}
/***********************************/
}
