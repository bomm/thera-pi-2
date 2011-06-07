package rehaMail;



import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
import Tools.DateTableCellRenderer;
import Tools.DblCellEditor;
import Tools.DoubleTableCellRenderer;
import Tools.JCompTools;
import Tools.JRtaTextField;
import ag.ion.bion.officelayer.NativeView;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.noa.NOAException;
import ag.ion.noa.frame.ILayoutManager;
import ag.ion.noa.internal.frame.LayoutManager;

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
	
	private IFrame             officeFrame       = null;
	public	ITextDocument      document          = null;
	private JPanel             noaPanel          = null;
	private JXPanel			noaDummy = null;
	NativeView nativeView = null; 
	
	JButton[] buts = {null,null,null,null,null};
	/**************************/
	public MailPanel(){
		/*
		super(new GridLayout(0,1));
		add(getContent());
		getnoaDummy();
		add(noaDummy);
		noaDummy.setVisible(true);
		*/
		super();
		activateListener();
		String xwert = "fill:0:grow(1.0)";
		String ywert = "fill:0:grow(0.33),2px,p,2px,fill:0:grow(0.66)";
		FormLayout lay = new FormLayout(xwert,ywert);
		CellConstraints cc = new CellConstraints();
		setLayout(lay);
		validate();
		add(getContent(),cc.xy(1, 1));
		add(getToolbar(),cc.xy(1, 3));
		getnoaDummy();
		add(noaDummy,cc.xy(1,5));
		noaDummy.setVisible(true);
		
		noaDummy.add(getOOorgPanel(),BorderLayout.CENTER);
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				while(RehaMail.officeapplication==null){
				}

				while(!RehaMail.officeapplication.isActive()){
					Thread.sleep(75);
				}
				fillNOAPanel();
				validate();
				setVisible(true);
				return null;
			}
			
		}.execute();
	}
	private void activateListener(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("newMail")){
					SwingUtilities.invokeLater(new Runnable(){
						public void run(){
							new NewMail("neue Nachricht erstellen",true,buts[0].getLocationOnScreen());
						}
					});
				}
			}
			
		};
	}
	private JToolBar getToolbar(){
		JToolBar jtb = new JToolBar();
		jtb.setOpaque(false);
		jtb.setRollover(true);
		jtb.setBorder(null);
		jtb.setOpaque(false);
		jtb.add( (buts[0]=ButtonTools.macheButton("neu", "newMail", al)));
		Image ico = new ImageIcon(RehaMail.progHome+"icons/package-install.png").getImage().getScaledInstance(26,26, Image.SCALE_SMOOTH);
		buts[0].setIcon(new ImageIcon(ico));
		buts[0].setToolTipText("eine neue Nachricht erstellen");
		return jtb;
	}
	private JXPanel getContent(){
		JXPanel pan = new JXPanel();
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
					doStatementAuswerten("select absender,"+
					"gelesen,versanddatum,empfangsdatum,betreff,id from pimail where empfaenger_person='"+
					RehaMail.mailUser+"' or empfaenger_gruppe like'%"+
					RehaMail.mailUser+"%' order by gelesen,versanddatum");
				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}
			
		}.execute();
		pan.validate();
		return pan;
	}
	private JPanel getOOorgPanel(){
		noaPanel = new JPanel(new GridLayout());
		noaPanel.setPreferredSize(new Dimension(1024,800));
		noaPanel.validate();
		return noaPanel;
	}
	private JXPanel getnoaDummy(){
		noaDummy = new JXPanel(new GridLayout(1,1));
		return noaDummy;
	}
	private JXTable getTable(){
		einmod = new EinTableModel();
		einmod.setColumnIdentifiers(new String[] {"Absender","gelesen","Abs.Datum","Empf.Datum","Betreff","id"});
		eintab = new JXTable(einmod);
		eintab.addMouseListener(new MouseAdapter(){
			
		});
		eintab.getColumn(0).setMinWidth(120);
		eintab.getColumn(0).setMaxWidth(120);
		eintab.getColumn(1).setMaxWidth(50);
		eintab.getColumn(2).setMaxWidth(120);
		eintab.getColumn(2).setMinWidth(120);
		eintab.getColumn(2).setCellEditor(tabDateEditor);
		eintab.getColumn(2).setCellRenderer(tabDateRenderer);
		eintab.getColumn(3).setCellEditor(tabDateEditor);
		eintab.getColumn(3).setCellRenderer(tabDateRenderer);
		eintab.getColumn(3).setMinWidth(120);
		eintab.getColumn(3).setMaxWidth(120);
		eintab.getColumn(5).setMinWidth(0);
		eintab.getColumn(5).setMaxWidth(0);

		eintab.setFont(new Font("Courier New",12,12));
		eintab.getSelectionModel().addListSelectionListener( new EinListSelectionHandler());
		
		return eintab;
	}
	public void panelRegeln(){
		if(einmod.getRowCount()<=0){tabelleLeeren();return;}
		int row = eintab.getSelectedRow();
		while(document==null);
		if(row < 0){tabelleLeeren();return;}
		if(einmod.getValueAt(eintab.convertRowIndexToModel(row),1 )==Boolean.FALSE){
			System.out.println("Text wird gesetzt");
			document.getTextService().getText().setText(
					"Damit Sie die Nachricht lesen können:\n\n" +
					"-> bitte Doppelklick in der Tabellenzeile");
		}else{
			
		}
	}
	public void tabelleLeeren(){
		einmod.setRowCount(0);
		eintab.validate();
		eintab.repaint();
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
			if(columnIndex==3){return Date.class;}
			if(columnIndex==4){return String.class;}
			/*
			if(colTypeName.size() <= 0){return String.class;}
			System.out.println(colTypeName.get(columnIndex));
			if(colTypeName.get(columnIndex).contains("VARCHAR")){
				return String.class;
			}else if(colTypeName.get(columnIndex).equals("BOOLEAN")){
				return Boolean.class;
			}else if(colTypeName.get(columnIndex).contains("DECIMAL")){
				return Double.class;
			}else if(colTypeName.get(columnIndex).contains("tinyint(")){
				return Integer.class;
			}else if(colTypeName.get(columnIndex).contains("INT")){
				return Integer.class;
			}else if(colTypeName.get(columnIndex).contains("DATE")){
				return Date.class;
			}else if(colTypeName.get(columnIndex).contains("longtext")){
				return String.class;
			}
			*/
		   return String.class;
	    }

		public boolean isCellEditable(int row, int col) {
			return false;
		}
		   
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
		
		
		Vector<Object> vec = new Vector<Object>();
		int durchlauf = 0;

		//Saudummerweise entspricht der Rückgabewert von getColumnTypeName() oder
		//getColumnType() nicht der Abfrag von describe tabelle
		//so werden alle Integer-Typen unter INT zusammengefaßt
		//Longtext, Mediumtext, Varchar = alles VARCHAR
		//CHAR kann sowohl ein einzelnes Zeichen als auch enum('T','F') also boolean sein...
		//eigentlich ein Riesenmist!

			
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
					vec.add(rs.getDate(4));
					vec.add((String) (rs.getString(5)==null ? "" : rs.getString(5)));
				
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
				einmod.addRow( (Vector<?>) vec.clone());
				if(durchlauf>200){
					try {
						eintab.validate();
						eintab.repaint();
						Thread.sleep(100);
						durchlauf = 0;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				durchlauf++;
			}
			
			eintab.validate();
			eintab.repaint();
			if(eintab.getRowCount() > 0){
				eintab.setRowSelectionInterval(0, 0);
			}
			jscr.validate();
			//doSetAbfrageErgebnis();
			
			

		} catch (SQLException e) {
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
	                	panelRegeln();
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
	        DocumentDescriptor docdescript = new DocumentDescriptor();
	        docdescript.setReadOnly(true);
	        document = (ITextDocument) RehaMail.officeapplication.getDocumentService().constructNewDocument(officeFrame,
	            IDocument.WRITER,
	            docdescript);
	        hideElements(LayoutManager.URL_MENUBAR);
	        hideElements(LayoutManager.URL_STATUSBAR);
	        hideElements(LayoutManager.URL_TOOLBAR_STANDARDBAR);
	        hideElements(LayoutManager.URL_TOOLBAR);


        	Tools.OOTools.setzePapierFormat(document, new Integer(25199), new Integer(19299));
        	Tools.OOTools.setzeRaender(document, new Integer(10), new Integer(10),new Integer(10),new Integer(10));
	        /*
	        String[] bars = LayoutManager.ALL_BARS_URLS;
	        for(int i = 0;i < bars.length;i++){
	        	System.out.println(bars[i]);
	        	hideElements(bars[i]);
	        }
	        */
	        
        	nativeView.validate();
	        try {
				document.zoom(DocumentZoomType.BY_VALUE, (short)90);
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


}
