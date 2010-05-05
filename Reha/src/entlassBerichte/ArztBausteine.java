package entlassBerichte;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import systemEinstellungen.SystemConfig;
import systemTools.Colors;
import systemTools.JCompTools;

import ag.ion.bion.officelayer.NativeView;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.noa.frame.ILayoutManager;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.windows.WindowsTabbedPaneUI;

public class ArztBausteine extends JDialog implements MouseListener, KeyListener,WindowListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	EBerichtPanel eltern = null;
	JXPanel content = null;
	JXTable bausteintbl = null;
	MyBausteinTableModel bausteinmod = null;
	JTabbedPane tab = null;
	private IFrame             officeFrame       = null;
	private ITextDocument      document          = null;
	private JPanel             noaPanel          = null;
	NativeView nativeView = null; 

	
	public ArztBausteine(EBerichtPanel xeltern){
		super();
		this.setPreferredSize(new Dimension(400,420));
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setContentPane(getPane());
		this.setAlwaysOnTop(true);
		this.eltern = xeltern;
		
		setTitle("Textbausteine E-Bericht");
		setModal(false);
		this.fillNOAPanel();
		content.revalidate();

	}
	private JXPanel getPane(){
		content = new JXPanel(new BorderLayout());
		content.add(getTabs(),BorderLayout.CENTER);
		
		return content;
	}
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
	private JXPanel getTabPage1(){
		FormLayout lay = new FormLayout("0dlu,fill:0:grow(1.0),0dlu",
				"0dlu,p,5dlu,fill:0:grow(0.6),5dlu,fill:0:grow(0.4),5dlu");
		CellConstraints cc = new CellConstraints();
		JXPanel pan = new JXPanel();
		pan.setVisible(true);
		pan.setVisible(true);
		pan.setLayout(lay);
		JToolBar toolb = new JToolBar();
		pan.add(toolb,cc.xy(2,2));
		bausteinmod = new MyBausteinTableModel();
		String[] columns = {"Haupt-Rubrik","Untergliederung","Titel des Bausteins","id"};
		bausteinmod.setColumnIdentifiers(columns);
		bausteintbl = new JXTable(bausteinmod);
		bausteintbl.getColumn(3).setMinWidth(0);
		bausteintbl.getColumn(3).setMaxWidth(0);
		bausteintbl.validate();
		bausteintbl.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.QUICKSILVER));
		//bausteintbl.setHighlighters(HighlighterFactory.createSimpleStriping(Colors.PiOrange.alpha(0.25f)));
		JScrollPane jscr = JCompTools.getTransparentScrollPane(bausteintbl);
		jscr.validate();
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				fuelleTabelle();
				return null;
			}
		}.execute();
		pan.add(jscr,cc.xy(2,4));
		noaPanel = new JPanel();
		noaPanel.setPreferredSize(new Dimension(600,400));
		noaPanel.setVisible(true);
		pan.add(noaPanel,cc.xy(2,6));
		pan.validate();

		return pan;
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
	private void holeDenErsten(){
		
	}
	/**********
	 * 
	 * 
	 */
	private void fuelleTabelle(){

			Statement stmt = null;
			ResultSet rs = null;
			String ret = "";
			Vector<String> retvec = new Vector<String>();
			Vector<Vector<String>> retkomplett = new Vector<Vector<String>>();	
			ResultSetMetaData rsMetaData = null;
			int numberOfColumns = 0;
			int bausteine = 0;
			try {
				stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
				            ResultSet.CONCUR_UPDATABLE );
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try{
				Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				String sstmt = "select tbthema,tbuntert,tbtitel,id from tbar Order BY tbthema";
				rs = stmt.executeQuery(sstmt);
				while(rs.next()){
					retvec.clear();
					retvec.add( (rs.getString(1)==null  ? "" :  rs.getString(1)) );
					retvec.add( (rs.getString(2)==null  ? "" :  rs.getString(2)) );
					retvec.add( (rs.getString(3)==null  ? "" :  rs.getString(3)) );
					retvec.add( (rs.getString(4)==null  ? "" :  rs.getString(4)) );
					bausteinmod.addRow((Vector<String>)retvec.clone());
					if(bausteine==0){
						new SwingWorker<Void,Void>(){
							@Override
							protected Void doInBackground() throws Exception {
								holeDenErsten();
								return null;
							}
							
						}.execute();
					}
					bausteine++;
				}
				Reha.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				retvec.clear();
				retvec = null;
			}catch(SQLException ev){
				System.out.println("SQLException: " + ev.getMessage());
				System.out.println("SQLState: " + ev.getSQLState());
				System.out.println("VendorError: " + ev.getErrorCode());
			}

			finally {
				if(rsMetaData != null){
					rsMetaData = null;
				}
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
		        document = (ITextDocument) Reha.officeapplication.getDocumentService().constructNewDocument(officeFrame,
		            IDocument.WRITER,
		            DocumentDescriptor.DEFAULT);
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
	    ILayoutManager layoutManager = officeFrame.getLayoutManager();
	    layoutManager.hideAll();
	    nativeView.validate();
	    ((JPanel)parent).validate();

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

		public Class getColumnClass(int columnIndex) {
			   if(columnIndex==0){return String.class;}
			   /*else if(columnIndex==1){return JLabel.class;}*/
			   else{return String.class;}
	           //return (columnIndex == 0) ? Boolean.class : String.class;
	       }

		    public boolean isCellEditable(int row, int col) {
		    	if(Reha.thisClass.patpanel.vecaktrez.get(62).equals("T")){
		    		return false;
		    	}
		        if (col == 0){
		        	return true;
		        }else if(col == 1){
		        	return true;
		        }else if(col == 2){
		        	return true;
		        }else if(col == 3){
		        	return true;
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
		        	return true;
		        }else if(col == 3){
		        	return true;
		        }else if(col == 7){
		        	return true;
		        }else if(col == 11){
		        	return true;
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
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
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
	
	
}