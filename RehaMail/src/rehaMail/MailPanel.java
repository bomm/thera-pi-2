package rehaMail;









import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;

import Tools.ButtonTools;
import Tools.DatFunk;
import Tools.DateTableCellRenderer;
import Tools.DblCellEditor;
import Tools.DoubleTableCellRenderer;
import Tools.IconListRenderer;
import Tools.JCompTools;
import Tools.JRtaTextField;
import Tools.MitteRenderer;
import Tools.OOTools;
import Tools.SqlInfo;
import Tools.ToolsDialog;
import Tools.UIFSplitPane;
import ag.ion.bion.officelayer.NativeView;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.filter.RTFFilter;
import ag.ion.bion.officelayer.internal.text.TextRange;
import ag.ion.bion.officelayer.text.IParagraph;
import ag.ion.bion.officelayer.text.ITextCursor;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextRange;
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
import com.sun.star.container.XContentEnumerationAccess;
import com.sun.star.container.XEnumeration;
import com.sun.star.container.XEnumerationAccess;
import com.sun.star.container.XNameAccess;
import com.sun.star.container.XNameContainer;
import com.sun.star.datatransfer.XTransferable;
import com.sun.star.datatransfer.XTransferableSupplier;
import com.sun.star.frame.XController;
import com.sun.star.frame.XLayoutManager;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.text.XParagraphCursor;
import com.sun.star.text.XText;
import com.sun.star.text.XTextContent;
import com.sun.star.text.XTextCursor;
import com.sun.star.text.XTextGraphicObjectsSupplier;
import com.sun.star.text.XTextRange;
import com.sun.star.ui.XUIElement;
import com.sun.star.uno.Any;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.view.DocumentZoomType;



public class MailPanel extends JXPanel implements TableModelListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4946342165531610888L;
	/**********für die Tabelle**************/
	JXTable eintab = null;
	EinTableModel einmod = null;
	Vector<String> colName = new Vector<String>();
	Vector<String> colClassName = new Vector<String>();
	Vector<Integer> colType = new Vector<Integer>();
	Vector<Boolean> colAutoinc = new Vector<Boolean>();
	Vector<String> colTypeName = new Vector<String>();
	Vector<Integer> colVisible = new Vector<Integer>();
	boolean isUpdateable = true;
	int autoIncCol = -1;
	String aktuelleTabelle = "";
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
	EinListSelectionHandler listhandler = null;
	
	private IFrame             officeFrame       = null;
	public	ITextDocument      document          = null;
	private JXPanel             noaPanel          = null;
	private JPanel				noaDummy = null;
	NativeView nativeView = null;
	DocumentDescriptor xdescript = null;
	
	boolean gelesen = false;
	String aktId = "";
	String aktAbsender = "";
	String aktBetreff = "";
	JButton[] buts = {null,null,null,null,null};

	Vector<String> attachmentFileName = new Vector<String>();


	/**************************/
	public MailPanel(){
		super();
		//setSize(1024,800);
		//setPreferredSize(new Dimension(1024,800));
		//setLayout(new GridLayout());
		setLayout(new BorderLayout());
		setOpaque(false);
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
					return null;
				}
			}.execute();
					

		}catch(Exception ex){
			ex.printStackTrace();
		}
		validate();

	}
	private JXPanel getToolsPanel(){
		JXPanel pan = new JXPanel();
		pan.setOpaque(false);
		String xwert = "fill:0:grow(1.0),p";
		String ywert = "fill:0:grow(1.0),2px,p";
		FormLayout lay = new FormLayout(xwert,ywert);
		pan.setLayout(lay);
		CellConstraints cc = new CellConstraints();
		pan.add(getContent(),cc.xyw(1, 1,2));
		/*
		pan.add(getToolbar(),cc.xy(1, 3));
		pan.add(getAttachmentButton(),cc.xy(2,3,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		*/
		pan.validate();
		return pan;
	}
	
	private Tools.UIFSplitPane constructSplitPaneOU(){
		UIFSplitPane jSplitRechtsOU =  UIFSplitPane.createStrippedSplitPane(JSplitPane.VERTICAL_SPLIT,
        		getToolsPanel(),
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
	

	private void activateListener(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("newMail")){
					SwingUtilities.invokeLater(new Runnable(){
						public void run(){
							Point pt = RehaMail.thisFrame.getLocationOnScreen();
							new NewMail("neue Nachricht erstellen",true,new Point(pt.x+50,pt.y+50),null,"","");
						}
					});
					return;
				}
				if(cmd.equals("replyMail")){
					if(!gelesen){
						JOptionPane.showMessageDialog(null,"Sie haben die Mail ja noch nicht einmal gelesen!\n"+
								"Wie bitteschön wollen Sie dann darauf antworten?");
						return;
					}
					SwingUtilities.invokeLater(new Runnable(){
						public void run(){
							ByteArrayOutputStream out = new ByteArrayOutputStream();
							//OutputStream out = null;
							try {
								try {
									document.getPersistenceService().export(out,OpenDocumentFilter.FILTER);
									//document.getPersistenceService().export(out,RTFFilter.FILTER);
								} catch (NOAException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								out.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							Point pt = RehaMail.thisFrame.getLocationOnScreen();
							new NewMail("Anwort auf Mail von: "+aktAbsender,false,new Point(pt.x+50,pt.y+50),
									out,aktAbsender,aktBetreff);
						}
					});
					return;
				}
				if(cmd.equals("loeschen")){
					return;
				}
				if(cmd.equals("print")){
					return;
				}
				if(cmd.equals("attachments")){
					new ToolsDlgAktuelleRezepte(null,buts[4].getLocationOnScreen());
					return;
				}
				
			}
			
		};
	}
	/*
	private void createIcons(){
		Image ico = new ImageIcon(RehaMail.progHome+"icons/pdf.png").getImage().getScaledInstance(26,26, Image.SCALE_SMOOTH);
		attachmentIco[0] = new ImageIcon(ico);
		ico = new ImageIcon(RehaMail.progHome+"icons/ooo-writer.png").getImage().getScaledInstance(26,26, Image.SCALE_SMOOTH);
		attachmentIco[1] = new ImageIcon(ico);
		ico = new ImageIcon(RehaMail.progHome+"icons/ooo-calc.png").getImage().getScaledInstance(26,26, Image.SCALE_SMOOTH);
		attachmentIco[2] = new ImageIcon(ico);
		ico = new ImageIcon(RehaMail.progHome+"icons/document-save-as.png").getImage().getScaledInstance(26,26, Image.SCALE_SMOOTH);
		attachmentIco[3] = new ImageIcon(ico);
		ico = new ImageIcon(RehaMail.progHome+"icons/application-exit.png").getImage().getScaledInstance(26,26, Image.SCALE_SMOOTH);
		attachmentIco[4] = new ImageIcon(ico);

	}
	*/
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
	private JXPanel getContent(){
		JXPanel pan = new JXPanel();
		setOpaque(false);
		String xwerte = "0dlu,fill:0:grow(1.0),0dlu";
		String ywerte = "0dlu,fill:0:grow(1.0),0dlu";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();

		pan.setLayout(lay);
		jscr = JCompTools.getTransparentScrollPane(getTable());
		jscr.validate();
		pan.add(jscr,cc.xy(2,2,CellConstraints.FILL,CellConstraints.FILL));
		
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					while(!RehaMail.DbOk){
						Thread.sleep(20);
					}
					checkForNewMail();					
				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}
			
		}.execute();
		pan.validate();
		return pan;
	}
	public void checkForNewMail(){
		doStatementAuswerten("select absender,"+
				"gelesen,versanddatum,gelesendatum,betreff,id from pimail where empfaenger_person='"+
				RehaMail.mailUser+"' or empfaenger_gruppe like'%"+
				RehaMail.mailUser+"%' order by gelesen DESC,versanddatum DESC");
		for(int i = 0; i < 4; i++){
			if(buts[i] != null){
				buts[i].setEnabled(true);	
			}
		}
	}
	private JXPanel getOOorgPanel(){

		noaPanel = new JXPanel(new BorderLayout());
		noaPanel.setDoubleBuffered(true);
		return noaPanel;
	}
	private JXTable getTable(){
		einmod = new EinTableModel();
		einmod.setColumnIdentifiers(new String[] {"Absender","gelesen","Abs.Datum","Empf.Datum","Betreff","id"});
		eintab = new JXTable(einmod);
		
		eintab.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getClickCount()==2 && arg0.getButton()==1){
					if(!gelesen){
						holeNeueMail();
						setzeGelesen();
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
		
		return eintab;
	}
	/********************************************/
	private void setzeGelesen(){
		SqlInfo.sqlAusfuehren("update pimail set gelesen='T', gelesendatum='"+
				new Timestamp(new Date().getTime())+"' where id = '"+aktId+"' LIMIT 1");
		einmod.setValueAt(Boolean.TRUE,eintab.convertRowIndexToModel(eintab.getSelectedRow()),1 );
		gelesen = true;
		if(RehaMail.testcase){
			JOptionPane.showMessageDialog(null,"Zeitstempel für gelesen gesetzt!");
		}
	}
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
	/********************************************/	
	public void panelRegeln(){
		//System.out.println("in PanelRegeln");
		if(einmod.getRowCount()<=0){tabelleLeeren();return;}
		
		int row = eintab.getSelectedRow();
		while(document==null || !RehaMail.DbOk);
		if(row < 0){tabelleLeeren();return;}
		gelesen = (Boolean)einmod.getValueAt(eintab.convertRowIndexToModel(row),1 );
		aktId = einmod.getValueAt(eintab.convertRowIndexToModel(row),5 ).toString();
		/****************************************************/
		ByteArrayInputStream ins = null;

		if(gelesen==Boolean.FALSE){
			try {
				buts[4].setEnabled(false);
				ins = new ByteArrayInputStream(RehaMail.notread.getBytes());
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
				return;
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}else{
			try{
				holeMail();
				holeAttachments();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		refreshSize();
	}
	
	/****************************************************/
	public void tabelleLeeren(){
		einmod.setRowCount(0);
		eintab.validate();
		eintab.repaint();
	}
	public void holeNeueMail(){
		holeMail();
	}
	public void holeMail(){
		
		int row = eintab.getSelectedRow();
		if(row < 0){tabelleLeeren();return;}

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
	  protected void resolveControls(XText xDocText) {
		    try {

		      // Get an enumeration of paragraphs.
		      XEnumerationAccess xParaAccess = (XEnumerationAccess) UnoRuntime.queryInterface(
		      XEnumerationAccess.class, xDocText );
		      XEnumeration xParaEnum = xParaAccess.createEnumeration();

		      // Creat a text cursor and go the beginning.
		      XTextCursor xTextCursor = xDocText.createTextCursor();
		      xTextCursor.gotoStart(false);

		      // Set up a paragraph cursor and point it to the first paragarph.
		      // when the enumeration gets a new element, select that paragraph using the cursor.
		      XParagraphCursor xParagraphCursor = (XParagraphCursor) UnoRuntime.queryInterface(
		      XParagraphCursor.class, xTextCursor );
		     
		      while ( xParaEnum.hasMoreElements() ) {

		        xParaEnum.nextElement();  // This line edited 11/21/2003, after original post.
		       
		        if (xParagraphCursor.isStartOfParagraph() &
		        xParagraphCursor.isEndOfParagraph()
		        ) {

		        }
		        else {
		          xParagraphCursor.gotoEndOfParagraph(true); //select the current paragraph.
		          System.out.println("xTextCursor.getString()=" + xTextCursor.getString());

		          // Access to frames, graphic objects, embedded objects or
		          // shapes that are anchored at or as character.
		          // Allows access to the collections of all content types.
		          XContentEnumerationAccess xContentAccess =
		          (XContentEnumerationAccess) UnoRuntime.queryInterface(
		          XContentEnumerationAccess.class, xParagraphCursor );

		          XEnumeration xContentEnum = xContentAccess.createContentEnumeration("com.sun.star.text.TextContent");

		          while ( xContentEnum.hasMoreElements() ) {
		            // Found a TextContent, my text box.
		            XTextContent xTextContent = (XTextContent) UnoRuntime.queryInterface(
		            XTextContent.class, xContentEnum.nextElement() );

		            // The box's anchor will allow me to insert text after I remove the box.
		            XTextRange xTextRange = xTextContent.getAnchor();

		            // Get the DefaultText of the text box here.
		            // ???  How to do this ???
		           
		            // Removes the form text box from the document.
		            XText xText = xTextCursor.getText();
		            xText.removeTextContent(xTextContent);

		            // Now insert some text in its place.
		            xText.insertString(xTextRange, "InsertedText42", false);
		          }
		        }
		        xParagraphCursor.gotoNextParagraph(false);
		      }
		    }
		    catch ( Exception e ) {
		      e.printStackTrace( System.out );
		    }
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
	private String getTimestampString(String ts){
		try{
			return DatFunk.sDatInDeutsch(ts.split(" ")[0].trim())+"-"+ts.split(" ")[1].trim().substring(0,8);
		}catch(Exception ex){
			
		}
		return "";
	}
	private void doStatementAuswerten(final String stat){
		
		
		autoIncCol = -1;
		einmod.removeTableModelListener(this);
		einmod.setRowCount(0);
		colName.clear();
		colType.clear();
		colAutoinc.clear();
		colClassName.clear();
		colTypeName.clear();
		isUpdateable = true;
		aktuelleTabelle = "";
		
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

		einmod.addTableModelListener(this);
		
		
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
	            		SwingUtilities.invokeLater(new Runnable(){
	            			public void run(){
	    	            		panelRegeln();	            				
	            			}
	            		});
	            		if(RehaMail.thisFrame != null)
	            		RehaMail.thisFrame.setCursor(RehaMail.DEFAULT_CURSOR);
	                    break;
	                }
	            }
	        }

	    }
	}
@Override
public void tableChanged(TableModelEvent arg0) {
	// TODO Auto-generated method stub
	
}
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


        	Tools.OOTools.setzePapierFormat(document, new Integer(25199), new Integer(19299));
        	Tools.OOTools.setzeRaender(document, new Integer(10), new Integer(10),new Integer(10),new Integer(10));
	        

        	//nativeView.validate();
	        try {
				document.zoom(DocumentZoomType.BY_VALUE, (short)90);
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

static void copy(ITextDocument sourceDoc, ITextDocument targetDoc) throws Exception {
    // the controllers 
    XController xController_sourceDoc = sourceDoc.getXTextDocument().getCurrentController();
    XController xController_targetDoc = targetDoc.getXTextDocument().getCurrentController();
    // getting the data supplier of our source doc 
    XTransferableSupplier xTransferableSupplier_sourceDoc = (XTransferableSupplier) UnoRuntime.queryInterface(XTransferableSupplier.class,
        xController_sourceDoc);
    // saving the selected contents 
    XTransferable xTransferable = xTransferableSupplier_sourceDoc.getTransferable();
    // getting the data supplier of our target doc 
    XTransferableSupplier xTransferableSupplier_targetDoc = (XTransferableSupplier) UnoRuntime.queryInterface(XTransferableSupplier.class,
        xController_targetDoc);
    // inserting the source document there 
    xTransferableSupplier_targetDoc.insertTransferable(xTransferable);
  }
/************************************************/
class ToolsDlgAktuelleRezepte{
	public ToolsDlgAktuelleRezepte(String command,Point pt){
		//boolean testcase = true;
		Object[] obi  = new Object[attachmentFileName.size()];
		Map<Object, ImageIcon> icons = new HashMap<Object, ImageIcon>();
		for(int i = 0; i < attachmentFileName.size();i++){
			icons.put(attachmentFileName.get(i),testDatei(attachmentFileName.get(i)));	
			obi[i] = attachmentFileName.get(i);
		}
		JList list = new JList(obi);
		list.setCellRenderer(new IconListRenderer(icons));	
		RehaMail.toolsDlgRueckgabe = -1;
		ToolsDialog tDlg = new ToolsDialog(RehaMail.thisFrame,"Dateianhänge",list);
		tDlg.setPreferredSize(new Dimension(200, 60+(attachmentFileName.size()*28)));
		tDlg.setLocation(pt.x-70,pt.y+30);
		tDlg.pack();
		tDlg.setModal(true);
		tDlg.activateListener();
		tDlg.setVisible(true);		
		
	}
	private ImageIcon testDatei(String filename){
		if(filename.toUpperCase().endsWith(".PDF")){
			return RehaMail.attachmentIco[0];
		}else if(filename.toUpperCase().endsWith(".ODT") || filename.toUpperCase().endsWith(".OTT")){
			return RehaMail.attachmentIco[1];
		}else if(filename.toUpperCase().endsWith(".ODS")){
			return RehaMail.attachmentIco[2];
		}
		return RehaMail.attachmentIco[4];
	}
}	


}
