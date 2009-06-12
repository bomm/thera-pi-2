package textBlockTherapeuten;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.LinearGradientPaint;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import patientenFenster.ArztNeuKurz;
import patientenFenster.PatGrundPanel;

import sqlTools.SqlInfo;
import systemTools.Colors;
import systemTools.JCompTools;
import systemTools.JRtaTextField;
import dialoge.RehaSmartDialog;

public class ThTextBlock extends RehaSmartDialog{
	public JFormattedTextField tf = null;
	String suchkrit = "";
	String suchid = "";
	
	public JButton neuarzt = null;
	public JRtaTextField[] elterntfs;
	public Container dummyPan = null;
	public Container dummyArzt = null;
	/***************/
	public JRtaTextField suchenach = null;
	public JXTable textblock = null;
	public MyTextBlockModel modtextblock = null;
	public JTextArea tbtext = null;
	boolean blockneugefunden = true;
	/***************/	
	JPanel content = null;
	public JXPanel grundPanel = null;
	public String arztbisher;

		public ThTextBlock(JXFrame owner, String name,String diag) {
			super(owner, name);
			//setSize(430,300);
			setSize(450,600);
			this.suchkrit = diag;
			Point2D start = new Point2D.Float(0, 0);
		     Point2D end = new Point2D.Float(0,getHeight());
		     float[] dist = {0.0f, 0.75f};
		     // Color[] colors = {Color.WHITE,new Color(231,120,23)};
		     Color[] colors = {Colors.Yellow.alpha(0.15f),Color.WHITE};
		     //Color[] colors = {Color.WHITE,Colors.TaskPaneBlau.alpha(0.45f)};
		     //Color[] colors = {Color.WHITE,getBackground()};
		     LinearGradientPaint p =
		         new LinearGradientPaint(start, end, dist, colors);
		     MattePainter mp = new MattePainter(p);
		     super.getSmartTitledPanel().setTitleForeground(Color.WHITE);
		     String xtitel = "<html>Textbausteine -->&nbsp;&nbsp;&nbsp;&nbsp;<b><font color='#ffffff'>"+diag+"</font></b>";
		     super.getSmartTitledPanel().setTitle(xtitel);
		     
		  
			grundPanel = new JXPanel(new BorderLayout());
			grundPanel.setBackgroundPainter(new CompoundPainter(mp));
			content = getAuswahl();
			grundPanel.add(content,BorderLayout.CENTER);
			getSmartTitledPanel().setContentContainer(grundPanel);

			//this.getContentPanel().add(content);
			/*
			final JXPanel thispan = content;
			SwingUtilities.invokeLater(new Runnable(){
				public  void run(){
					KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.ALT_MASK);
					((JComponent)getContentPanel()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "doSuchen");
					((JComponent)getContentPanel()).getActionMap().put("doSuchen", new ArztWahlAction());
				}
			});
			*/
			//pack();
			new Thread(){
				public void run(){
					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() throws Exception {
							fuelleTabelle("Knie");
							return null;
						}
					}.execute();
				}
			}.start();
		}
		private JPanel getAuswahl(){
			suchenach = new JRtaTextField("NIX",true);
			suchenach.addKeyListener(new KeyAdapter(){
				public void keyPressed(KeyEvent arg0) {
					if(arg0.getKeyCode()==10){
						arg0.consume();
					}
					/*
					if(arg0.getKeyCode()==27){
						arg0.consume();
					}
					*/
				}
			});
			tbtext = new JTextArea();
			tbtext.setFont(new Font("Courier",Font.PLAIN,11));
			tbtext.setForeground(Color.BLUE);
			tbtext.setLineWrap(true);
			tbtext.setName("tbtext");
			tbtext.setWrapStyleWord(true);
			tbtext.setEditable(false);
			

			modtextblock = new MyTextBlockModel();
			modtextblock.setColumnIdentifiers(new String[] {"Block-Rang","Titel",""});
			textblock = new JXTable(modtextblock);
			textblock.setEditable(false);
			textblock.setSortable(true);
			textblock.getColumn(0).setMaxWidth(75);
			textblock.getColumn(2).setMinWidth(0);
			textblock.getColumn(2).setMaxWidth(0);
			textblock.setSelectionMode(0);
			textblock.getSelectionModel().addListSelectionListener( new TblockListSelectionHandler());
			

			FormLayout lay = new FormLayout(
				//x   1    2          3           4         5          6    
					"5dlu,0dlu,right:max(40dlu;p),2dlu,fill:300dlu:grow(1.00),5dlu",
				//y	  1   2   3       4                  5    6     7                
					"5dlu,p,2dlu,fill:100dlu:grow(1.00),2dlu,30dlu,10dlu,5dlu"
					);
			PanelBuilder pb = new PanelBuilder(lay);
			CellConstraints cc = new CellConstraints();
			pb.getPanel().setOpaque(false);
			pb.addLabel("finde Textblock",cc.xy(3,2));
			pb.add(suchenach,cc.xy(5,2));
			JScrollPane jscr = JCompTools.getTransparentScrollPane(textblock);
			jscr.getVerticalScrollBar().setUnitIncrement(15);
			jscr.validate();
			pb.add(jscr,cc.xyw(2, 4,4));
			jscr = JCompTools.getTransparentScrollPane(tbtext);
			jscr.getVerticalScrollBar().setUnitIncrement(15);
			jscr.validate();
			pb.add(jscr,cc.xywh(2, 6,4,2));
			pb.getPanel().validate();
			return pb.getPanel();
		}
		private void fuelleTabelle(String diag){
			Vector vec = SqlInfo.holeSaetze("tbkg", "CONCAT(tbblock,' - ',tbrang) AS blockrang,tbtitel,id", "tbthema='"+diag+"' ORDER BY blockrang" , Arrays.asList(new String[] {}));
			int anz = vec.size();
			Vector<String> vec2 = new Vector<String>();
			if(anz>0){
				for(int i = 0;i<anz;i++){
					modtextblock.addRow((Vector)((Vector)vec.get(i)).clone() );
					if(i==0){
						holeTbText( new Integer((String)((Vector)vec.get(i)).get(2)) );
					}
				}
				textblock.setRowSelectionInterval(0, 0);
			}
		}
		private void holeTbText(int tbid){
			tbtext.setText((String) SqlInfo.holeSatz("tbkg", "tbtext", "id='"+tbid+"'", Arrays.asList(new String[] {})).get(0) );
		}

		class MyTextBlockModel extends DefaultTableModel{
			   /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Class getColumnClass(int columnIndex) {
				   if(columnIndex==0){
					   return String.class;
				   }else{
					   return String.class;
				   }
			}
		    public boolean isCellEditable(int row, int col) {
		        //Note that the data/cell address is constant,
		        //no matter where the cell appears onscreen.
		    	return true;
		    }
			public Object getValueAt(int rowIndex, int columnIndex) {
				String theData = (String) ((Vector)getDataVector().get(rowIndex)).get(columnIndex); 
				Object result = null;
				result = theData;
				return result;
			}
		}
		class TblockListSelectionHandler implements ListSelectionListener {

		    public void valueChanged(ListSelectionEvent e) {
				if(blockneugefunden){
					blockneugefunden = false;
					//return;
				}
		        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
		        
		        int firstIndex = e.getFirstIndex();
		        int lastIndex = e.getLastIndex();
		        boolean isAdjusting = e.getValueIsAdjusting();
		        if(isAdjusting){
		        	return;
		        }
				StringBuffer output = new StringBuffer();
		        if (lsm.isSelectionEmpty()) {

		        } else {
		            int minIndex = lsm.getMinSelectionIndex();
		            int maxIndex = lsm.getMaxSelectionIndex();
		            for (int i = minIndex; i <= maxIndex; i++) {
		                if (lsm.isSelectedIndex(i)) {
		                	final int ix = i;
		                	new SwingWorker<Void,Void>(){
								@Override
								protected Void doInBackground() throws Exception {
									holeTbText(new Integer((String)textblock.getValueAt(ix, 2)));
									return null;
								}
		                	}.execute();
		                    break;
		                }
		            }
		        }
		    }
		}

		
/*************************************************/		
}		
