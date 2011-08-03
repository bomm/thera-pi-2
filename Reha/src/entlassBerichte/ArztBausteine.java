package entlassBerichte;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import oOorgTools.OOTools;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemTools.JCompTools;
import systemTools.JRtaComboBox;
import systemTools.JRtaTextField;
import ag.ion.bion.officelayer.NativeView;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.filter.RTFFilter;
import ag.ion.bion.officelayer.text.IText;
import ag.ion.bion.officelayer.text.ITextCursor;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextRange;
import ag.ion.bion.officelayer.text.IViewCursor;
import ag.ion.noa.NOAException;
import ag.ion.noa.frame.ILayoutManager;
import ag.ion.noa.search.ISearchResult;
import ag.ion.noa.search.SearchDescriptor;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.frame.XLayoutManager;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.ui.XUIElement;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.view.DocumentZoomType;

public class ArztBausteine extends JDialog implements WindowListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	EBerichtPanel eltern = null;
	JXPanel content = null;
	JXTable bausteintbl = null;
	MyBausteinTableModel bausteinmod = null;
	JTabbedPane tab = null;
	JRtaTextField suchenach = null;
	JButton suchen = null;
	JRtaComboBox suchart = null;
	private IFrame             officeFrame       = null;
	private ITextDocument      document          = null;
	private JPanel             noaPanel          = null;
	NativeView nativeView = null; 
	ActionListener al = null;
	MouseListener ml = null;
	KeyListener kl = null;
	
	public ArztBausteine(EBerichtPanel xeltern,Point pt){
		super();
		this.setPreferredSize(new Dimension(400,520));
		this.setLocation(pt);
		this.setVisible(true);
		this.activateListener();
		this.setContentPane(getPane());
		this.setAlwaysOnTop(true);
		this.eltern = xeltern;
		this.addWindowListener(this);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Textbausteine E-Bericht");
		setModal(false);
		this.fillNOAPanel();
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				fuelleTabelle("");
				return null;
			}
		}.execute();
		content.revalidate();
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				setzeFocus();
			}
		});
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
				suchenach.requestFocus();
				doKeyTest();
			}
		};
		/*******************************/
		ml = new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getClickCount()==2){
					if(arg0.getSource() instanceof JXTable){
						if(sucheNachPlatzhalter()){
							sendeText();							
						}
						suchenach.requestFocus();
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
						suchenach.requestFocus();
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
	private boolean sucheNachPlatzhalter(){
		IText text = document.getTextService().getText();
		String stext = text.getText();
		int start = 0;
		//int end = 0;
		String dummy;
		int vars = 0;
		int sysvar = -1;
		boolean noendfound = false;
		int row = bausteintbl.getSelectedRow();
		String titel = "unbekannter Baustein";
		if(row >=0){
			row = bausteintbl.convertRowIndexToModel(row);
			titel = bausteinmod.getValueAt(row,2).toString();
		}
		 
		while ((start = stext.indexOf("^")) >= 0){
			noendfound = true;
			for(int i = 1;i < 150;i++){
				if(stext.substring(start+i,start+(i+1)).equals("^")){
					dummy = stext.substring(start,start+(i+1));
					////System.out.println("Variable gefunden - Variablenname = "+dummy);
					stext = stext.replace(dummy,"ersetzte Variable "+vars);
					if((sysvar=isSysVar(dummy)) >= 0){
						sucheErsetze(dummy,eltern.sysVarInhalt.get(sysvar),true);
					}else{
						String sanweisung = dummy.toString().replace("^", "");
						Object ret = JOptionPane.showInputDialog(this,"<html>Bitte Wert für eingeben für: --\u003E<b> "+sanweisung+" </b> &nbsp; </html>","Baustein: "+titel, 1);						
						//Object ret = JOptionPane.showInputDialog(this,"Bitte Wert für eingeben für: --> "+sanweisung+" <-- ","Baustein: "+titel, 1);
						if(ret==null){
							return true;
							//sucheErsetze(dummy,"");
						}else{
							sucheErsetze(dummy,((String)ret).trim(),false);
						}
					}
					noendfound = false;
					vars++;
					break;
				}
			}
			if(noendfound){
				JOptionPane.showMessageDialog(null,"Der Baustein ist fehlerhaft, eine Übernahme deshalb nicht möglich"+
						"\n\nVermutete Ursache des Fehlers: es wurde ein Start-/Endezeichen '^' für Variable vergessen\n");
				return false;
			}
		}
		return true;
	}
	private int isSysVar(String svar){
		return eltern.sysVarList.indexOf(svar);
	}
	private void sucheErsetze(String suchenach,String ersetzemit,boolean alle){
		SearchDescriptor searchDescriptor = new SearchDescriptor(suchenach);
		searchDescriptor.setIsCaseSensitive(true);
		ISearchResult searchResult = null;
		if(alle){
			searchResult = document.getSearchService().findAll(searchDescriptor);
		}else{
			searchResult = document.getSearchService().findFirst(searchDescriptor);			
		}

		if(!searchResult.isEmpty()) {
			ITextRange[] textRanges = searchResult.getTextRanges();
			for (int resultIndex=0; resultIndex<textRanges.length; resultIndex++) {
				textRanges[resultIndex].setText(ersetzemit);
				
			}
		}
	}
	
	private void sendeText(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			document.getPersistenceService().store(out);
			ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
			out.flush();
			out.close();
			eltern.insertStreamAtCurrentPosition(in);
			in.close();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void doKeyTest(){
		String suche = suchenach.getText().trim();
		if(suche.equals("")){
			fuelleTabelle("");
		}else{
			String[] spalten = {"tbtitel","tbuntert"};
			String where = SqlInfo.macheWhereKlausel("select tbthema,tbuntert,tbtitel,id from tbar where ", suchenach.getText().trim(), spalten);
			////System.out.println("where = "+where);
			fuelleTabelle(where+" Order BY tbthema");
		}
	}
	/*
	private void doActionTest(String action){
		if(action.equals("tabellenklick")){
			
		}
	}
	*/
	private void holeIdUndText(){
		int row = bausteintbl.getSelectedRow();
		if(row >= 0){
			row = bausteintbl.convertRowIndexToModel(row);
			String id = bausteinmod.getValueAt(row, 3).toString();
			holeTextBaustein(id);
		}
	}
	private JXPanel getPane(){
		content = new JXPanel(new BorderLayout());
		//content.add(getTabs(),BorderLayout.CENTER);
		content.add(getTabPage1(),BorderLayout.CENTER);
		
		return content;
	}
	/*
	private JTabbedPane getTabs(){
		UIManager.put("TabbedPane.tabsOpaque", Boolean.FALSE);
		UIManager.put("TabbedPane.contentOpaque", Boolean.FALSE);

		tab = new JTabbedPane();
		try{
			tab.setUI(new WindowsTabbedPaneUI());
		}catch(Exception ex){
		}

		tab.addTab("Bausteine abrufen", getTabPage1());
		tab.addTab("Bausteine bearbeiten", new JXPanel());
		tab.addTab("Bausteine testen", new JXPanel());
		
		UIManager.put("TabbedPane.tabsOpaque", Boolean.TRUE);
		UIManager.put("TabbedPane.contentOpaque", Boolean.TRUE);

		return tab;
	}
	*/
	private JXPanel getTabPage1(){
		FormLayout lay = new FormLayout("0dlu,fill:0:grow(1.0),0dlu",
				"5dlu,p,5dlu,fill:0:grow(0.5),5dlu,fill:0:grow(0.5),5dlu");
		CellConstraints cc = new CellConstraints();
		JXPanel pan = new JXPanel();
		pan.setVisible(true);
		pan.setVisible(true);
		pan.setLayout(lay);
		JToolBar toolb = new JToolBar();
		toolb.setOpaque(false);
		toolb.setRollover(true);
		toolb.setBorder(null);
		
		String[] cmbinhalt = {"suche in Titel","suche in Untergliederung","suche in Haupt-Rubrik"};
		suchart = new JRtaComboBox(cmbinhalt);
		toolb.add(suchart);
		suchenach = new JRtaTextField("nix",true);
		suchenach.setPreferredSize(new Dimension(40,20));
		suchenach.addKeyListener(kl);
		toolb.add(suchenach);
		suchen = new JButton("suche starten");
		suchen.addActionListener(al);
		suchen.addKeyListener(kl);
		toolb.add(suchen);
		pan.add(toolb,cc.xy(2,2));
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
		/*
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				fuelleTabelle("");
				return null;
			}
		}.execute();
		*/
		pan.add(jscr,cc.xy(2,4));
		noaPanel = new JPanel();
		noaPanel.setPreferredSize(new Dimension(600,400));
		noaPanel.setVisible(true);
		pan.add(noaPanel,cc.xy(2,6));
		pan.validate();

		return pan;
	}
	/**********
	 * 
	 * 
	 * @param id
	 */

	private void holeTextBaustein(String id){
		InputStream ins = SqlInfo.holeStream("tbar", "tbtext", "id='"+id+"'");
		//String instring = SqlInfo.holeEinzelFeld("select tbtext from tbar where id='"+id+"' LIMIT 1");
		document.getTextService().getText().setText("");
		
		ITextCursor textCursor = null;
		IViewCursor viewCursor = null;
		try {
			textCursor = document.getTextService().getCursorService().getTextCursor();
			textCursor.insertDocument(ins, RTFFilter.FILTER);
			textCursor.gotoStart(false);
			viewCursor = document.getViewCursorService().getViewCursor();
			viewCursor.getPageCursor().jumpToFirstPage();
			viewCursor.getPageCursor().jumpToStartOfPage();
			ins.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
	}
	
	/**********
	 * 
	 * 
	 */
	private void fuelleTabelle(String cmd){

			Statement stmt = null;
			ResultSet rs = null;
			Vector<String> retvec = new Vector<String>();
			int bausteine = 0;
			bausteinmod.setRowCount(0);
			try {
				stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
				            ResultSet.CONCUR_UPDATABLE );
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try{
				String sstmt = null;
				Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
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
						//final String id = ((Vector<String>)retvec).get(3).toString();
						new SwingWorker<Void,Void>(){
							@Override
							protected Void doInBackground() throws Exception {
								bausteintbl.setRowSelectionInterval(0, 0);
								return null;
							}
						}.execute();
					}

					bausteine++;
				}
				Reha.thisFrame.setCursor(Reha.thisClass.normalCursor);
				retvec.clear();
				retvec = null;
			}catch(SQLException ev){
				//System.out.println("SQLException: " + ev.getMessage());
				//System.out.println("SQLState: " + ev.getSQLState());
				//System.out.println("VendorError: " + ev.getErrorCode());
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
	/**********
	 * 
	 * 
	 */
	
	private void fillNOAPanel() {
	    if (noaPanel != null) {
		      try {
		        officeFrame = constructOOOFrame(Reha.officeapplication, noaPanel);
		        //DocumentDescriptor desc = DocumentDescriptor.DEFAULT;
		        //desc.setReadOnly(true);
		        document = (ITextDocument) Reha.officeapplication.getDocumentService().constructNewDocument(officeFrame,
		            IDocument.WRITER,
		            DocumentDescriptor.DEFAULT);
	        	OOTools.setzePapierFormat(document, Integer.valueOf(25199), Integer.valueOf(19299));
	        	OOTools.setzeRaender(document, Integer.valueOf(1000), Integer.valueOf(1000),Integer.valueOf(1000),Integer.valueOf(1000));
	        	hideAllElements();
		        nativeView.validate();
		        
		        try {
					document.zoom(DocumentZoomType.BY_VALUE, (short)70);
				} catch (DocumentException e) {
					e.printStackTrace();
				}
		        noaPanel.setVisible(true);		      }
		      catch (Throwable throwable) {
		        noaPanel.add(new JLabel("Ein Fehler ist aufgetreten: " + throwable.getMessage()));
		      }
		    }
		  }
	
	private IFrame constructOOOFrame(IOfficeApplication officeApplication, final Container parent) throws Throwable {
	    nativeView = new NativeView(SystemConfig.OpenOfficeNativePfad);
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
	
	public final void refreshSize() {
		noaPanel.setPreferredSize(new Dimension(noaPanel.getWidth() , noaPanel.getHeight()
		- 5));

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
	
	class MyTermTableModel extends DefaultTableModel{
		   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Class<?> getColumnClass(int columnIndex) {
			   if(columnIndex==0){return String.class;}
			   /*else if(columnIndex==1){return JLabel.class;}*/
			   else{return String.class;}
	           //return (columnIndex == 0) ? Boolean.class : String.class;
	       }

		    public boolean isCellEditable(int row, int col) {
		        if (col == 0){
		        	return false;
		        }else if(col == 1){
		        	return false;
		        }else if(col == 2){
		        	return false;
		        }else if(col == 3){
		        	return false;
		        }else if(col == 11){
		        	return true;
		        } else{
		          return false;
		        }
		      }
	}
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
	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowClosed(WindowEvent arg0) {
		if(document != null){
			document.close();
		}
		eltern.arztbaus = null;
	}
	@Override
	public void windowClosing(WindowEvent arg0) {
		////System.out.println("in closing.....");
		if(document != null){
			document.close();
		}
	}
	private void hideAllElements() throws UnknownPropertyException, PropertyVetoException, IllegalArgumentException, WrappedTargetException, NOAException{
        ILayoutManager layoutManager = officeFrame.getLayoutManager();
        XLayoutManager xLayoutManager = layoutManager.getXLayoutManager();
        for (int i = 0; i < ILayoutManager.ALL_BARS_URLS.length; i++) {
          String url = ILayoutManager.ALL_BARS_URLS[i];
          XUIElement element = xLayoutManager.getElement(url);
          if (element != null) {
            XPropertySet xps = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, element);
            xps.setPropertyValue("Persistent", new Boolean(false));
            xLayoutManager.hideElement(url);
          }
        }		
	}
	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	class BausteinSelectionHandler implements ListSelectionListener {

	    public void valueChanged(ListSelectionEvent e) {
	        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
	        
	        //int firstIndex = e.getFirstIndex();
	        //int lastIndex = e.getLastIndex();
	        boolean isAdjusting = e.getValueIsAdjusting();
	        if(isAdjusting){
	        	return;
	        }
	        if (lsm.isSelectionEmpty()) {
	        }else {
	            //int minIndex = lsm.getMinSelectionIndex();
	            //int maxIndex = lsm.getMaxSelectionIndex();
	            holeIdUndText();
	        }
	    }
	}

	
}