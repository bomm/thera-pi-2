package rehaMail;









import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
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
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
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
import javax.swing.text.BadLocationException;

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
import Tools.ReaderStart;
import Tools.Rechte;
import Tools.SqlInfo;
import Tools.ToolsDialog;
import Tools.UIFSplitPane;

import ag.ion.bion.officelayer.text.ITextDocument;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;



public class MailPanel extends JXPanel implements TableModelListener, KeyListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4946342165531610888L;
	/**********f�r die Tabelle**************/
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
	RTFEditorPanel rtfEditor= null;
	ObjectInputStream ois = null;
	InputStream ins = null;
	
	ByteArrayInputStream bins;	
	boolean gelesen = false;
	String aktId = "";
	String aktAbsender = "";
	String aktBetreff = "";
	JButton[] buts = {null,null,null,null,null};

	Vector<String> attachmentFileName = new Vector<String>();
	
	JRtaTextField suchen = null;


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
			
			add(constructSplitPaneOU(),BorderLayout.CENTER);
			
			/*******************/
				

		}catch(Exception ex){
			ex.printStackTrace();
		}
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				checkForNewMail(true);	
			}
		});
		validate();
	}
	private void doNochNichtGelesen(){
		bins = new ByteArrayInputStream(RehaMail.notread.getBytes());
		if(rtfEditor.editorArea == null){System.out.println("der RTF-Editor ist noch null");return;}
		if(bins == null){System.out.println("Der Stream ist noch null");return;}
		try {
			rtfEditor.editorArea.getDocument().remove(0, rtfEditor.editorArea.getDocument().getLength());
			rtfEditor.editorArea.getEditorKit().createDefaultDocument();
			rtfEditor.editorArea.getEditorKit().read(bins, rtfEditor.editorArea.getDocument(), 0);
			bins.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
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
        		rtfEditor=new RTFEditorPanel(false,false)/*getOOorgPanel()*/);
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
								"Wie bittesch�n wollen Sie dann darauf antworten?");
						return;
					}
					if(SqlInfo.holeEinzelFeld("select id from pimail where id ='"+
							aktId+"' LIMIT 1").equals("")){
						JOptionPane.showMessageDialog(null,"Diese Nachricht existiert nicht mehr!\nAntwort nicht m�glich!");
						checkForNewMail(true);
						return;
					}
					
					SwingUtilities.invokeLater(new Runnable(){
						public void run(){
							ByteArrayOutputStream out = new ByteArrayOutputStream();
							//OutputStream out = null;
							try {
								try {
									rtfEditor.editorArea.getEditorKit().write(out,
											rtfEditor.editorArea.getDocument(),
											0,rtfEditor.editorArea.getDocument().getLength());
								} catch (IOException e1) {
									e1.printStackTrace();
								} catch (BadLocationException e1) {
									e1.printStackTrace();
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
					if(! Rechte.hatRecht(RehaMail.Sonstiges_NachrichtenLoeschen, false)){
						JOptionPane.showMessageDialog(null, "Keine Berechtigung zum L�schen der Nachricht");
						return;
					}
					if(! Rechte.hatRecht(RehaMail.Sonstiges_NachrichtenLoeschen, false)){
						JOptionPane.showMessageDialog(null, "Funktion noch nicht implementiert");
						return;
					}
					doLoeschen();

					return;
				}
				if(cmd.equals("print")){
					if(!gelesen){return;}
					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() throws Exception {
							ByteArrayOutputStream out = new ByteArrayOutputStream();
							try {
								rtfEditor.editorArea.getEditorKit().write(out,
										rtfEditor.editorArea.getDocument(),
										0,rtfEditor.editorArea.getDocument().getLength());
							} catch (IOException e1) {
								e1.printStackTrace();
							} catch (BadLocationException e1) {
								e1.printStackTrace();
							}
							out.flush();
							out.close();
							ByteArrayInputStream ins = new ByteArrayInputStream(out.toByteArray());
							OOTools.starteWriterMitStream(ins, "mailprint");
							ins.close();
							return null;
						}
						
					}.execute();
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
		jtb.add(new JLabel("Betreff oder Nachricht enthält: "));
		suchen = new JRtaTextField("nix",false);
		//suchen.setSize(new Dimension(60,15));
		suchen.setToolTipText("Ein oder mehrere Suchbegriffe eingeben und Enter drücken");
		suchen.setMaximumSize(new Dimension(150,25));
		suchen.setFont(new Font("Courier New",12,12));
		suchen.setName("suchen");
		suchen.addKeyListener(this);

		jtb.add(suchen);
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
					//checkForNewMail(true);					
				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}
			
		}.execute();
		pan.validate();
		return pan;
	}
	public void listenerAusschalten(){
		eintab.getSelectionModel().removeListSelectionListener(listhandler);
	}

	private void doLoeschen(){
		if(einmod.getRowCount()<=0){tabelleLeeren();return;}
		listenerAusschalten();
		System.out.println("einlesen text");
		int[] rows = eintab.getSelectedRows();
		int frage = JOptionPane.showConfirmDialog(null,"Die ausgew�hlten Emails wirklich l�schen",
				"Achtung wichtige Benutzeranfrage",JOptionPane.YES_NO_OPTION);
		if(frage != JOptionPane.YES_OPTION){return;}
		for(int i = 0; i < rows.length;i++){
			aktId = einmod.getValueAt(eintab.convertRowIndexToModel(rows[i]),5 ).toString();
			SqlInfo.sqlAusfuehren("delete from pimail where id='"+aktId+"' LIMIT 1");
		}
		checkForNewMail(true);
		textLoeschen();
		
	}
	private void textLoeschen(){
		if(eintab.getRowCount()<=0 || eintab.getSelectedRow()<0){
			try {
				rtfEditor.editorArea.getDocument().remove(0, rtfEditor.editorArea.getDocument().getLength());
				gelesen = false;
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,"Fehler beim Einlesen der Nachricht");
				e.printStackTrace();
			} 
		}
	}

	
	public void checkForNewMail(boolean all){
		listenerAusschalten();
		String stmt = null; 
		if(all){
			
			stmt = "select absender,"+
			"gelesen,versanddatum,gelesendatum,betreff,id from pimail where empfaenger_person='"+
			RehaMail.mailUser+"' or empfaenger_gruppe like'%"+
			RehaMail.mailUser+"%' order by gelesen DESC,versanddatum DESC";
		}else{
			stmt = "select absender,"+
			"gelesen,versanddatum,gelesendatum,betreff,id from pimail where empfaenger_person='"+
			RehaMail.mailUser+"' or empfaenger_gruppe like'%"+
			RehaMail.mailUser+"%' and gelesen = 'F' order by gelesen DESC,versanddatum DESC";
		}
		System.out.println(stmt);
		doStatementAuswerten(stmt,all);
		for(int i = 0; i < 4; i++){
			if(buts[i] != null){
				buts[i].setEnabled(true);	
			}
		}
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
						holeAttachments();
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
			JOptionPane.showMessageDialog(null,"Zeitstempel f�r gelesen gesetzt!");
		}
	}
	public void allesAufNull(){
		//document.getTextService().getText().setText("");
		//loescheBilder();
		//loescheParagraphen();
		try {
			rtfEditor.editorArea.getDocument().remove(0, rtfEditor.editorArea.getDocument().getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

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
		while(!RehaMail.DbOk);
		if(row < 0){tabelleLeeren();return;}
		gelesen = (Boolean)einmod.getValueAt(eintab.convertRowIndexToModel(row),1 );
		aktId = einmod.getValueAt(eintab.convertRowIndexToModel(row),5 ).toString();
		if(SqlInfo.holeEinzelFeld("select id from pimail where id ='"+
				aktId+"' LIMIT 1").equals("")){
			JOptionPane.showMessageDialog(null,"Diese Nachricht existiert nicht mehr!");
			checkForNewMail(true);
			return;
		}
		/****************************************************/
		bins = null;

		if(gelesen==Boolean.FALSE){
			try {
				buts[4].setEnabled(false);
				doNochNichtGelesen();
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
		//refreshSize();
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
		
		bins = null;
		ins = (ByteArrayInputStream)SqlInfo.holeStream("pimail", "emailtext", "id='"+aktId+"' LIMIT 1");
		try {
			rtfEditor.editorArea.getDocument().remove(0, rtfEditor.editorArea.getDocument().getLength());
			rtfEditor.editorArea.getEditorKit().read(ins, rtfEditor.editorArea.getDocument(),0);
			ins.close();
			rtfEditor.editorArea.setCaretPosition(0);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,"Fehler beim Einlesen der Nachricht");
			e.printStackTrace();
		} 
		
		
	}
	/*
	protected void loescheBilder(){
		ITextDocument textDocument = (ITextDocument)document;
		
		
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
*/
	/*
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
	*/
	/*
	  protected void resolveControls(XText xDocText) {
		    try {

		      
		      XEnumerationAccess xParaAccess = (XEnumerationAccess) UnoRuntime.queryInterface(
		      XEnumerationAccess.class, xDocText );
		      XEnumeration xParaEnum = xParaAccess.createEnumeration();

		      
		      XTextCursor xTextCursor = xDocText.createTextCursor();
		      xTextCursor.gotoStart(false);

		      
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
	 */ 
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
	private void doStatementAuswerten(final String stat,boolean all){
		
		eintab.getRowSorter().setSortKeys(null);
		autoIncCol = -1;
		einmod.removeTableModelListener(this);
		if(all){
			einmod.setRowCount(0);	
		}
		
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
		
		
		Vector<Object> vec = new Vector<Object>();
		int durchlauf = 0;

		//Saudummerweise entspricht der R�ckgabewert von getColumnTypeName() oder
		//getColumnType() nicht der Abfrag von describe tabelle
		//so werden alle Integer-Typen unter INT zusammengefa�t
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
				if(all){
					einmod.addRow( (Vector<?>) vec.clone());	
				}else{
					einmod.insertRow(0, (Vector<?>) vec.clone());
				}
				
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
/*
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
	       
	      catch (Throwable throwable) {
	        noaPanel.add(new JLabel("<html>Ein Fehler ist aufgetreten:<br>" + throwable.getMessage()+"</html>"));
	      }
	    }
	  }
*/	  
/*
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
    
    XController xController_sourceDoc = sourceDoc.getXTextDocument().getCurrentController();
    XController xController_targetDoc = targetDoc.getXTextDocument().getCurrentController();
    
    XTransferableSupplier xTransferableSupplier_sourceDoc = (XTransferableSupplier) UnoRuntime.queryInterface(XTransferableSupplier.class,
        xController_sourceDoc);
    
    XTransferable xTransferable = xTransferableSupplier_sourceDoc.getTransferable();
    
    XTransferableSupplier xTransferableSupplier_targetDoc = (XTransferableSupplier) UnoRuntime.queryInterface(XTransferableSupplier.class,
        xController_targetDoc);
    
    xTransferableSupplier_targetDoc.insertTransferable(xTransferable);
  }
*/  
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
		if(RehaMail.toolsDlgRueckgabe < 0){return;}
		String komplett = RehaMail.progHome+"temp/"+RehaMail.aktIK+"/"+attachmentFileName.get(RehaMail.toolsDlgRueckgabe);

		
		 if(komplett.toUpperCase().endsWith(".PDF") || komplett.toUpperCase().endsWith(".ODT")
				 || komplett.toUpperCase().endsWith(".OTT") || komplett.toUpperCase().endsWith(".ODS")){
				if(!speichereDatei(new String[] {null,RehaMail.progHome+"temp/"+RehaMail.aktIK},
						attachmentFileName.get(RehaMail.toolsDlgRueckgabe),
						Integer.toString(RehaMail.toolsDlgRueckgabe+1))){
					return;
				}
			 
			 if(komplett.toUpperCase().endsWith(".PDF")){
				 new ReaderStart(komplett);
			 }else if(komplett.toUpperCase().endsWith(".ODT") ||komplett.toUpperCase().endsWith(".ODT")  ){
				OOTools.starteWriterMitDatei(komplett.replace("//", "/")); 
			 }else if(komplett.toUpperCase().endsWith(".ODS")){
				 OOTools.starteCalcMitDatei(komplett); 
			 }
		 }else{
				String[] indatei = dateiDialog(attachmentFileName.get(RehaMail.toolsDlgRueckgabe));
				if(indatei[0]==null){return;}
				if(speichereDatei(indatei,
						attachmentFileName.get(RehaMail.toolsDlgRueckgabe),
						Integer.toString(RehaMail.toolsDlgRueckgabe+1))){
					JOptionPane.showMessageDialog(null, "Datei "+attachmentFileName.get(RehaMail.toolsDlgRueckgabe)+" erfolgreich gespeichert!\n\n"+
							"Verzeichnis: --> "+indatei[1].replace("\\", "/"));

				}
			 
		 }
		
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
private boolean speichereDatei(String[] pfade,String datei,String attachnumber){
	boolean success = true;
	//System.out.println(pfade[0]);
	//System.out.println(pfade[1]);
	String komplett = pfade[1].replace("\\", "/")+"/"+datei;
	  try{
		  RehaMail.thisFrame.setCursor(RehaMail.WAIT_CURSOR);
		  File f=new File(komplett);
		  InputStream inputStream= SqlInfo.holeStream("pimail", "attach"+attachnumber, "id='"+aktId+"'");
		  OutputStream out=new FileOutputStream(f);
		  byte buf[]=new byte[1024];
		  int len;
		  while((len=inputStream.read(buf))>0){
			  out.write(buf,0,len);  
		  }
		  out.close();
		  inputStream.close();
		  RehaMail.thisFrame.setCursor(RehaMail.DEFAULT_CURSOR);
	  }catch (IOException e){
		  RehaMail.thisFrame.setCursor(RehaMail.DEFAULT_CURSOR);
			JOptionPane.showMessageDialog(null, "Speichern der Datei "+datei+" fehlgeschlagen" );

		  return false;
	  }
	return success;
		
}	


private String[] dateiDialog(String pfad){
	//String sret = "";
	String[] sret ={null,null};
	System.out.println("Speichern in "+pfad);
	final JFileChooser chooser = new JFileChooser("Verzeichnis auswählen");
    chooser.setDialogType(JFileChooser.SAVE_DIALOG);
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    final File file = new File(pfad);

    chooser.setCurrentDirectory(new File(RehaMail.progHome));
    chooser.setSelectedFile(file);
    chooser.addPropertyChangeListener(new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)
                    || e.getPropertyName().equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
                //final File f = (File) e.getNewValue();
            }
        }

    });
    chooser.setVisible(true);
    setCursor(RehaMail.thisClass.normalCursor);
    final int result = chooser.showSaveDialog(null);

    if (result == JFileChooser.APPROVE_OPTION) {
        File inputVerzFile = chooser.getSelectedFile();
        String inputVerzStr = inputVerzFile.getPath();
        

        if(inputVerzFile.getName().trim().equals("")){
        	
        	//sret = "";
        }else{
        	sret[0] = inputVerzFile.getName().trim();
        	sret[1] = inputVerzStr;
        }
    }else{
    	//sret = ""; //vorlagenname.setText(SystemConfig.oTerminListe.NameTemplate);
    }
    chooser.setVisible(false); 

    return sret;
}



@Override
public void keyPressed(KeyEvent arg0) {
	if( ((JComponent)arg0.getSource()).getName().equals("suchen")){
		if(arg0.getKeyCode()==KeyEvent.VK_ENTER){
			if(suchen.getText().trim().equals("")){checkForNewMail(true);return;}
			try{
				//toRTF(suchen.getText())
			((JComponent)arg0.getSource()).requestFocus();
			String where = SqlInfo.macheWhereKlausel("where (empfaenger_person='"+
					RehaMail.mailUser+"' or empfaenger_gruppe like'%"+
					RehaMail.mailUser+"%') AND ", 
					suchen.getText(), new String[] {"betreff","emailtext"});
			String cmd = "select absender,"+
			"gelesen,versanddatum,gelesendatum,betreff,id from pimail "+where+
			" order by gelesen DESC,versanddatum DESC";
			//System.out.println(where);
			doStatementAuswerten(cmd,true);
			

			}catch(Exception ex){
				JOptionPane.showMessageDialog(null,ex.getMessage());
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



}
