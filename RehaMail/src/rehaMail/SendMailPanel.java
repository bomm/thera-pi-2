package rehaMail;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;

import rehaMail.MailPanel.EinListSelectionHandler;
import rehaMail.MailPanel.EinTableModel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import Tools.DateTableCellRenderer;
import Tools.DblCellEditor;
import Tools.DoubleTableCellRenderer;
import Tools.JCompTools;
import Tools.JRtaTextField;
import Tools.MitteRenderer;
import ag.ion.bion.officelayer.NativeView;
import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.text.ITextDocument;

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
	private JPanel             noaPanel          = null;
	private JXPanel			noaDummy = null;
	NativeView nativeView = null;
	DocumentDescriptor xdescript = null;
	
	boolean gelesen = false;
	String aktId = "";
	String aktAbsender = "";
	String aktBetreff = "";
	JButton[] buts = {null,null,null,null,null};

	Vector<String> attachmentFileName = new Vector<String>();
	

	public SendMailPanel(){
		super();
		setOpaque(false);
		String xwert = "fill:0:grow(1.0),p";
		String ywert = "fill:0:grow(0.5),2px,p,2px,fill:0:grow(0.5)";
		FormLayout lay = new FormLayout(xwert,ywert);
		CellConstraints cc = new CellConstraints();
		setLayout(lay);

		add(getTabelle(),cc.xyw(1,1,2));
	}
	public void updateMails(){
		
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
		eintab.getSelectionModel().addListSelectionListener( new EinListSelectionHandler());
		jscr = JCompTools.getTransparentScrollPane(eintab);
		jscr.validate();
		pan.add(jscr,cc.xy(1,1));
		pan.validate();
		return pan;
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
	            		//panelRegeln();
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
