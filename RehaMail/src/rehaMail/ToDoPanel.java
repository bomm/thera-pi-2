package rehaMail;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
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
import javax.swing.JLabel;
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
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import CommonTools.ButtonTools;
import CommonTools.DatFunk;
import CommonTools.DateTableCellRenderer;
import CommonTools.DblCellEditor;
import CommonTools.DoubleTableCellRenderer;
import CommonTools.JCompTools;
import CommonTools.JRtaTextField;
import CommonTools.MitteRenderer;
import Tools.OOTools;
import Tools.Rechte;
import CommonTools.SqlInfo;
import Tools.UIFSplitPane;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.mysql.jdbc.PreparedStatement;

public class ToDoPanel extends JXPanel implements TableModelListener, KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1065523479449767071L;
	Vector<String> colName = new Vector<String>();
	Vector<String> colClassName = new Vector<String>();
	Vector<Integer> colType = new Vector<Integer>();
	Vector<Boolean> colAutoinc = new Vector<Boolean>();
	Vector<String> colTypeName = new Vector<String>();
	Vector<Integer> colVisible = new Vector<Integer>();
	boolean isUpdateable = true;
	int autoIncCol = -1;
	String aktuelleTabelle = "";
	
	ActionListener al = null;
	
	RTFEditorPanel rtfEditor= null;
	ObjectInputStream ois = null;
	InputStream ins = null;
	ByteArrayInputStream bins;	

	JButton[] buts = {null,null,null,null,null,null};
	
	JRtaTextField suchen = null;
	
	JScrollPane jscr = null;
	
	boolean gelesen;
	String aktId = "";
	String aktAbsender = "";
	String aktBetreff = "";
	
	JXTable todotab = null;
	ToDoTableModel todomod = null;
	ToDoListSelectionHandler listhandler = null;
	
	CommonTools.DateTableCellEditor tabDateEditor = new CommonTools.DateTableCellEditor();
	DateTableCellRenderer tabDateRenderer = new DateTableCellRenderer(true);
	
	DblCellEditor tabDoubleEditor = new DblCellEditor();
	DoubleTableCellRenderer tabDoubleRenderer = new DoubleTableCellRenderer();
	
	CommonTools.IntTableCellEditor tabIntegerEditor = new CommonTools.IntTableCellEditor();
	CommonTools.IntTableCellRenderer tabIntegerRenderer = new CommonTools.IntTableCellRenderer();
	
	JRtaTextField sqlstatement = null;
	
	DecimalFormat dcf = new DecimalFormat("##########0.00");
	SimpleDateFormat datumsFormat = new SimpleDateFormat ("dd.MM.yyyy"); //Konv.	

	Vector<String> attachmentFileName = new Vector<String>();
	
	public static String titelString = "ToDo....";
	public static int anzahlAufgaben = 0;
	public ToDoPanel(){
		super();
		setLayout(new BorderLayout());
		setOpaque(false);
	    activateListener();
		try{
			CompoundPainter<Object> cp = null;
			MattePainter mp = null;
			LinearGradientPaint p = null;
			Point2D start = new Point2D.Float(0, 0);
			Point2D end = new Point2D.Float(960,100);		
			start = new Point2D.Float(0, 0);
			end = new Point2D.Float(0,400);
			float[] dist =null;
			Color[] colors = null;
		    dist = new float[] {0.0f, 0.75f};
		    colors = new Color[] {Color.WHITE,CommonTools.Colors.Green.alpha(0.45f)};
		    p = new LinearGradientPaint(start, end, dist, colors);
		    mp = new MattePainter(p);
		    cp = new CompoundPainter<Object>(mp);
		    this.setBackgroundPainter(cp);


		    
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
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				checkForNewToDo(true);	
				ToDoPanel.setTabTitel();
				if(todomod.getRowCount()>=1){
					SwingUtilities.invokeLater(new Runnable(){
						public void run(){
							todotab.setRowSelectionInterval(0, 0);		
						}
					});
				}

				return null;
			}
		}.execute();
		/*
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				checkForNewToDo(true);	
				ToDoPanel.setTabTitel();
			}
		});
		*/
		validate();
	}
	public ToDoPanel getInstance(){
		return this;
	}
	public static String setTabTitel(){
		return titelString+" (Aufgaben: "+Integer.toString(anzahlAufgaben)+")";

	}
	public void checkForNewToDo(boolean all){
		listenerAusschalten();
		String stmt = null; 
		if(all){
			
			stmt = "select absender,"+
			"gelesen,versanddatum,gelesendatum,betreff,id from todo where taskowner='"+
			RehaMail.mailUser+"' or empfaenger_gruppe like'%"+
			RehaMail.mailUser+"%' order by id DESC,versanddatum DESC";
		}else{
			stmt = "select absender,"+
			"gelesen,versanddatum,gelesendatum,betreff,id from todo where taskowner='"+
			RehaMail.mailUser+"' or empfaenger_gruppe like'%"+
			RehaMail.mailUser+"%' and gelesen = 'F' order by id DESC,versanddatum DESC";
		}
		doStatementAuswerten(stmt,all);
		for(int i = 0; i < 4; i++){
			if(buts[i] != null){
				buts[i].setEnabled(true);	
			}
		}
	}
	public void selectFirstRow(){
		if(todomod.getRowCount()>=1){
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					todotab.setRowSelectionInterval(0, 0);		
				}
			});
		}
	}
	/**************************************************/
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
	/**************************************************/	
	private Tools.UIFSplitPane constructSplitPaneOU(){
		UIFSplitPane jSplitRechtsOU =  UIFSplitPane.createStrippedSplitPane(JSplitPane.VERTICAL_SPLIT,
        		getToolsPanel(),
        		rtfEditor=new RTFEditorPanel(true,true,true)/*getOOorgPanel()*/);
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
	/**************************************************/
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
	/**************************************************/	
	private JToolBar getToolbar(){
		JToolBar jtb = new JToolBar();
		jtb.setOpaque(false);
		jtb.setRollover(true);
		jtb.setBorder(null);
		jtb.addSeparator(new Dimension(0,30));
		jtb.add( (buts[0]=ButtonTools.macheButton("", "newMail", al)));
		buts[0].setIcon(RehaMail.symbole.get("plus"));
		buts[0].setToolTipText("eine neue Nachricht erstellen");
		jtb.addSeparator(new Dimension(15,30));
		jtb.add( (buts[1]=ButtonTools.macheButton("", "replyMail", al)));
		buts[1].setIcon(RehaMail.symbole.get("refresh"));
		buts[1].setToolTipText("auf die gewählte Nachricht antworten");
		jtb.addSeparator(new Dimension(15,30));
		jtb.add( (buts[3]=ButtonTools.macheButton("", "loeschen", al)));
		buts[3].setIcon(RehaMail.symbole.get("minus"));
		buts[3].setToolTipText("die gewählte Nachricht loeschen");
		jtb.addSeparator(new Dimension(15,30));
		jtb.add( (buts[5]=ButtonTools.macheButton("", "todoneu", al)));
		buts[5].setIcon(RehaMail.symbole.get("todosolo"));
		buts[5].setToolTipText("Mailunabhängige Aufgabe anlegen");

		jtb.addSeparator(new Dimension(75,30));
		jtb.add( (buts[2]=ButtonTools.macheButton("", "print", al)));
		buts[2].setIcon(RehaMail.symbole.get("drucken"));
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
	/**************************************************/	
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
				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}
			
		}.execute();
		pan.validate();
		return pan;
	}
	/**************************************************/
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
					if(SqlInfo.holeEinzelFeld("select id from todo where id ='"+
							aktId+"' LIMIT 1").equals("")){
						JOptionPane.showMessageDialog(null,"Diese Nachricht existiert nicht mehr!\nAntwort nicht m�glich!");
						checkForNewToDo(true);
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
					//new ToolsDlgAktuelleRezepte(null,buts[4].getLocationOnScreen());
					return;
				}
				if(cmd.equals("speichern")){
					doSpeichern();
					return;
				}
				if(cmd.equals("todoneu")){
					soloTodo();
					return;
				}
				
				
			}
			
		};
	}
	private void soloTodo(){
		Object ret = JOptionPane.showInputDialog(null, "Geben Sie bitte einen Titel für die neue Aufgabe ein", "");
		if(ret == null){return;}
		if(ret.toString().trim().equals("")){return;}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			listenerAusschalten();
			try {
				rtfEditor.editorArea.getDocument().remove(0, rtfEditor.editorArea.getDocument().getLength());
				rtfEditor.editorArea.getEditorKit().createDefaultDocument();
				rtfEditor.editorArea.setCaretPosition(0);
				rtfEditor.editorArea.getDocument().insertString(0, "Noch kein Text für neue Aufgabe gespeichert", null);
				rtfEditor.editorArea.setCaretPosition(0);

				MutableAttributeSet attr = new SimpleAttributeSet();
				StyleConstants.setFontFamily(attr,"Arial");
				rtfEditor.editorArea.setCharacterAttributes(attr, true);
				attr = new SimpleAttributeSet();
				StyleConstants.setFontSize(attr,16);
				rtfEditor.editorArea.setCharacterAttributes(attr, true);
				
				rtfEditor.editorArea.select(0, rtfEditor.editorArea.getDocument().getLength());
				StyleConstants.setForeground(attr, Color.BLACK);
				rtfEditor.editorArea.setCharacterAttributes(attr, true);
				rtfEditor.editorArea.setCaretPosition(0);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}			
			rtfEditor.editorArea.getEditorKit().write(out,
						rtfEditor.editorArea.getDocument(),
						0,rtfEditor.editorArea.getDocument().getLength());
			
			ByteArrayInputStream ins = null;
			
			out.flush();
			doSpeichernNewTodo(
					RehaMail.mailUser,
					ret.toString(),
					ins = new ByteArrayInputStream(out.toByteArray()),
					new Vector<Vector<String>>());
			ins.close();
			out.close();
			this.checkForNewToDo(true);
			if(todomod.getRowCount()>=1){
				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						todotab.setRowSelectionInterval(0, 0);		
					}
				});
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void allesAufNull(){
		listenerAusschalten();
		try {
			rtfEditor.editorArea.getDocument().remove(0, rtfEditor.editorArea.getDocument().getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}		
		todotab.getSelectionModel().removeListSelectionListener(listhandler);
		todomod.setRowCount(0);
		todotab.validate();
		todotab.repaint();
		todotab.getSelectionModel().addListSelectionListener(listhandler);
		RehaMail.updateTitle("unbekannt");
		for(int i = 0; i < 5; i++){
			buts[i].setEnabled(false);
		}
		anzahlAufgaben = 0;
		((MailTab)this.getParent().getParent()).setzeTitel(2, setTabTitel());
	}
	
	public void listenerAusschalten(){
		todotab.getSelectionModel().removeListSelectionListener(listhandler);
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
	}
	@Override
	public void tableChanged(TableModelEvent arg0) {
	}
	public void panelRegeln(){
		if(todomod.getRowCount()<=0){tabelleLeeren();return;}
		int row = todotab.getSelectedRow();
		while(!RehaMail.DbOk);
		if(row < 0){tabelleLeeren();return;}
		gelesen = (Boolean)todomod.getValueAt(todotab.convertRowIndexToModel(row),1 );
		aktId = todomod.getValueAt(todotab.convertRowIndexToModel(row),5 ).toString();
		if(SqlInfo.holeEinzelFeld("select id from todo where id ='"+
				aktId+"' LIMIT 1").equals("")){
			JOptionPane.showMessageDialog(null,"Diese Nachricht existiert nicht mehr!");
			checkForNewToDo(true);
			return;
		}
		/****************************************************/
		bins = null;
		try{
			holeMail();
			holeAttachments();
		}catch(Exception ex){
			ex.printStackTrace();
		}




		//refreshSize();
	}
	public void tabelleLeeren(){
		todomod.setRowCount(0);
		todotab.validate();
		todotab.repaint();
	}
	public void holeNeueMail(){
		holeMail();
	}
	public void holeMail(){
		
		int row = todotab.getSelectedRow();
		if(row < 0){tabelleLeeren();return;}
		
		bins = null;
		
		ins = (ByteArrayInputStream)SqlInfo.holeStream("todo", "emailtext", "id='"+aktId+"'");
		System.out.println(ins);
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
	public void holeAttachments(){
		this.attachmentFileName.clear();
		this.attachmentFileName.trimToSize();
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				Vector<Vector<String>> vec = SqlInfo.holeFelder("select file1,file2,file3 from todo where id ='"+aktId+"' Limit 1" );
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
		
	private void doLoeschen(){
		if(todomod.getRowCount()<=0){tabelleLeeren();return;}
		listenerAusschalten();
		int[] rows = todotab.getSelectedRows();
		int frage = JOptionPane.showConfirmDialog(null,"Die ausgewählten Aufgaben wirklich löschen",
				"Achtung wichtige Benutzeranfrage",JOptionPane.YES_NO_OPTION);
		if(frage != JOptionPane.YES_OPTION){return;}
		for(int i = 0; i < rows.length;i++){
			aktId = todomod.getValueAt(todotab.convertRowIndexToModel(rows[i]),5 ).toString();
			SqlInfo.sqlAusfuehren("delete from todo where id='"+aktId+"' LIMIT 1");
		}
		checkForNewToDo(true);
		if(todomod.getRowCount()>=1){
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					todotab.setRowSelectionInterval(0, 0);		
				}
			});
		}
		textLoeschen();
		
	}
	private void textLoeschen(){
		if(todotab.getRowCount()<=0 || todotab.getSelectedRow()<0){
			try {
				rtfEditor.editorArea.getDocument().remove(0, rtfEditor.editorArea.getDocument().getLength());
				gelesen = false;
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,"Fehler beim Einlesen der Nachricht");
				e.printStackTrace();
			} 
		}
	}
	private void setzeGelesen(){
		SqlInfo.sqlAusfuehren("update todo set gelesen='T', gelesendatum='"+
				new Timestamp(new Date().getTime())+"' where id = '"+aktId+"' LIMIT 1");
		todomod.setValueAt(Boolean.TRUE,todotab.convertRowIndexToModel(todotab.getSelectedRow()),1 );
		gelesen = true;
		if(RehaMail.testcase){
			JOptionPane.showMessageDialog(null,"Zeitstempel f�r gelesen gesetzt!");
		}
	}
	
	private JXTable getTable(){
		todomod = new ToDoTableModel();
		todomod.setColumnIdentifiers(new String[] {"Absender","gelesen","Abs.Datum","Empf.Datum","Betreff","id"});
		todotab = new JXTable(todomod);
		
		todotab.addMouseListener(new MouseAdapter(){
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
		todotab.getColumn(0).setMinWidth(120);
		todotab.getColumn(0).setMaxWidth(120);
		todotab.getColumn(1).setMaxWidth(50);
		todotab.getColumn(2).setMaxWidth(100);
		todotab.getColumn(2).setMinWidth(100);
		todotab.getColumn(2).setCellEditor(tabDateEditor);
		todotab.getColumn(2).setCellRenderer(tabDateRenderer);
		todotab.getColumn(3).setCellEditor(tabDateEditor);
		todotab.getColumn(3).setCellRenderer(new MitteRenderer());
		todotab.getColumn(3).setMinWidth(155);
		todotab.getColumn(3).setMaxWidth(155);
		todotab.getColumn(5).setMinWidth(0);
		todotab.getColumn(5).setMaxWidth(0);

		todotab.setFont(new Font("Courier New",12,12));
		todotab.getSelectionModel().addListSelectionListener( (listhandler=new ToDoListSelectionHandler()));
		
		return todotab;
	}
	/********************************************/

	
	
	
class ToDoListSelectionHandler implements ListSelectionListener {
		
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
	            		gelesen = (Boolean)todomod.getValueAt(todotab.convertRowIndexToModel(i),1 );
	            		aktId = todomod.getValueAt(todotab.convertRowIndexToModel(i),5 ).toString();
	            		aktAbsender = todomod.getValueAt(todotab.convertRowIndexToModel(i),0 ).toString();
	            		aktBetreff = todomod.getValueAt(todotab.convertRowIndexToModel(i),4 ).toString();
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

class ToDoTableModel extends DefaultTableModel{
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

private void doStatementAuswerten(final String stat,boolean all){
	
	todotab.getRowSorter().setSortKeys(null);
	autoIncCol = -1;
	todomod.removeTableModelListener(this);
	if(all){
		todomod.setRowCount(0);	
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

	todotab.getSelectionModel().removeListSelectionListener(listhandler);	
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
				todomod.addRow( (Vector<?>) vec.clone());	
			}else{
				todomod.insertRow(0, (Vector<?>) vec.clone());
			}
			

			if(durchlauf>200){
				try {
					todotab.validate();
					todotab.repaint();
					Thread.sleep(80);
					durchlauf = 0;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			durchlauf++;
		}

		todotab.validate();
		todotab.repaint();
		anzahlAufgaben = todotab.getRowCount();
		if(anzahlAufgaben <= 0){
			aktId = "";
		}
		//((MailTab)this.getParent().getParent()).getToDoPanel().holeMail();
		((MailTab)this.getParent().getParent()).setzeTitel(2, setTabTitel());
		
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
	todotab.getSelectionModel().addListSelectionListener(listhandler);
	//todomod.addTableModelListener(this);
	
	
}
private String getTimestampString(String ts){
	try{
		return DatFunk.sDatInDeutsch(ts.split(" ")[0].trim())+"-"+ts.split(" ")[1].trim().substring(0,8);
	}catch(Exception ex){
		
	}
	return "";
}

	public void doSpeichern(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PreparedStatement ps = null;
		try {
			rtfEditor.editorArea.getEditorKit().write(out,
					rtfEditor.editorArea.getDocument(),
					0,rtfEditor.editorArea.getDocument().getLength());
			out.flush();
			ByteArrayInputStream ins = null;
			ins = new ByteArrayInputStream(out.toByteArray());
			String select = "update todo set emailtext=? where id=?";
			ps = (PreparedStatement) RehaMail.thisClass.conn.prepareStatement(select);
			ps.setBinaryStream(1,ins);
			ps.setString(2,aktId);
			ps.execute();
			out.close();
			ins.close();
			JOptionPane.showMessageDialog(null,"aktueller Text wurde erfolgreich gespeichert");
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		finally {
			if(ps != null){
				try {
					ps.close();
					out.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	public void doSpeichernNewTodo(
		String empfaenger,
		String betreff,
		InputStream insemailtext,
		Vector<Vector<String>> attaches
		) throws Exception{		
		PreparedStatement ps = null;
		FileInputStream[] ins = {null,null,null}; 
		try {
			String select = "insert into todo set "+
			"absender = ? ,"+
			"taskowner = ? ,"+
			"empfaenger_person = ?,"+
			"versanddatum = ?,"+
			"gelesen = ?, "+
			"betreff = ?,"+
			"emailtext = ?,"+
			"attach1 = ?,"+
			"attach2= ?,"+
			"attach3= ?,"+
			"file1 = ?,"+
			"file2 = ?,"+
			"file3 = ?";
			ps = (PreparedStatement) RehaMail.thisClass.conn.prepareStatement(select);
			ps.setString(1, RehaMail.mailUser);
			ps.setString(2, empfaenger);
			ps.setString(3, empfaenger);			  
			ps.setString(4, DatFunk.sDatInSQL(DatFunk.sHeute()));
			ps.setString(5, "F");			  
			ps.setString(6, betreff);
			ps.setBinaryStream(7,insemailtext);
			for(int i = 0; i < 3; i++){
				if(i <= (attaches.size()-1) ){
					ps.setBinaryStream(8+i, (ins[i]=new FileInputStream(attaches.get(i).get(1))));
					ps.setString(11+i,attaches.get(i).get(0));
				}else{
					ps.setBinaryStream(8+i,null);
					ps.setString(11+i,null);
				}
			}
			RehaMail.thisFrame.setCursor(RehaMail.WAIT_CURSOR);
			ps.execute();
			for(int i = 0; i < 3; i++){
				if(ins[i] != null){
					ins[i].close();
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}	
		finally {
			if(ps != null){
				ps.close();
			}
		}
	
	}

}
