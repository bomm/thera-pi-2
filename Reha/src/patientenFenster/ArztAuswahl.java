package patientenFenster;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import patientenFenster.PatGrundPanel.PatientAction;

import sqlTools.SqlInfo;
import systemTools.Colors;
import systemTools.JCompTools;
import systemTools.JRtaTextField;
import terminKalender.TerminFenster;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dialoge.RehaSmartDialog;

public class ArztAuswahl extends RehaSmartDialog{
public JFormattedTextField tf = null;
String suchkrit = "";
String suchid = "";
public JXTable arztwahltbl = null;
public JButton neuarzt = null;
public MyArztWahlModel arztwahlmod = null;
public JRtaTextField[] elterntfs;
public Container dummyPan = null;
public Container dummyArzt = null;
JXPanel content = null;
ArztNeuKurz ank = null;
public JXPanel grundPanel = null;
public String arztbisher;
CompoundPainter cp = null;
MattePainter mp = null;
LinearGradientPaint p = null;


	public ArztAuswahl(JXFrame owner, String name,String[] suchegleichnach,JRtaTextField[] elterntf,String arzt) {
		super(owner, name);
		//setSize(430,300);
		setSize(450,400);
		this.suchkrit = suchegleichnach[0];
		this.suchid = suchegleichnach[1];
		this.elterntfs = elterntf;
		this.arztbisher = arzt;
		Point2D start = new Point2D.Float(0, 0);
	     Point2D end = new Point2D.Float(0,getHeight());
	     float[] dist = {0.0f, 0.75f};
	     // Color[] colors = {Color.WHITE,new Color(231,120,23)};
	     Color[] colors = {Color.WHITE,Colors.TaskPaneBlau.alpha(0.45f)};
	     //Color[] colors = {Color.WHITE,getBackground()};
	     p =
	         new LinearGradientPaint(start, end, dist, colors);
	     mp = new MattePainter(p);
	     super.getSmartTitledPanel().setTitleForeground(Color.WHITE);
	     super.getSmartTitledPanel().setTitle("Arzt auswählen");
	     
		//((JXPanel)super.getSmartTitledPanel().getContentContainer()).setBackgroundPainter(new CompoundPainter(mp));;;
		grundPanel = new JXPanel(new BorderLayout());
		cp = new CompoundPainter(mp);
		grundPanel.setBackgroundPainter(cp);
		content = getAuswahl();
		grundPanel.add(content,BorderLayout.CENTER);
		getSmartTitledPanel().setContentContainer(grundPanel);

		//this.getContentPanel().add(content);

		final JXPanel thispan = content;
		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.ALT_MASK);
				((JComponent)getContentPanel()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "doSuchen");
				((JComponent)getContentPanel()).getActionMap().put("doSuchen", new ArztWahlAction());
				/*
				stroke = KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.ALT_MASK);
				((JComponent)getContentPanel()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "doNeu");
				((JComponent)getContentPanel()).getActionMap().put("doNeu", new ArztWahlAction());
				*/

			}
		});
	}
	
	private JXPanel getAuswahl(){
		JXPanel jpan = new JXPanel();
		jpan.setOpaque(false);
		jpan.setBackground(Color.WHITE);
		FormLayout lay = new FormLayout("5dlu,p,3dlu,100dlu:g,3dlu,40dlu,fill:0:grow(1.00),5dlu","3dlu,p,3dlu,150dlu:g,5dlu");
		CellConstraints cc = new CellConstraints();
		jpan.setLayout(lay);
		jpan.add(new JLabel("Arzt finden:"),cc.xy(2,2));
		tf = new JFormattedTextField();
		tf.setText(this.suchkrit);
		tf.setName("suchfeld");
		KeyListener akl = new ArztListener();
		tf.addKeyListener(akl);
		jpan.add(tf,cc.xy(4,2));
		
		neuarzt = new JButton("neu anlegen");
		neuarzt.setName("neuarzt");
		neuarzt.setMnemonic(KeyEvent.VK_N);
		neuarzt.addKeyListener(akl);
		neuarzt.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				neuAnlageArzt();
			}
	    });
		jpan.add(neuarzt,cc.xyw(6,2,2));
		
		arztwahlmod = new MyArztWahlModel();
		String[] column = 	{"Name","Vorname","Strasse","Ort", "LANR",""};
		arztwahlmod.setColumnIdentifiers(column);
		arztwahltbl = new JXTable(arztwahlmod);
		arztwahltbl.addKeyListener(akl);
		arztwahltbl.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getClickCount() == 2){
					werteUebergeben();					
				}
			}
		});
		arztwahltbl.setName("arzttabelle");
		arztwahltbl.setHighlighters(HighlighterFactory.createSimpleStriping(Colors.Blue.alpha(0.1f)));
		arztwahltbl.setDoubleBuffered(true);
		arztwahltbl.setEditable(false);
		arztwahltbl.setSortable(true);
		arztwahltbl.setSelectionMode(0);
		arztwahltbl.getColumn(0).setMinWidth(100);
		arztwahltbl.getColumn(5).setMinWidth(0);
		arztwahltbl.getColumn(5).setMaxWidth(0);
		arztwahltbl.setHorizontalScrollEnabled(true);
		if( (this.suchid.length()) > 0 && (!this.suchid.equals("-1")) ){
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					fuelleIdTabelle(suchid);
					return null;
				}
			}.execute();
		}else{
			if(this.suchkrit.length() > 0){
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						fuelleTabelle(suchkrit);
						return null;
					}
				}.execute();
			}
		}
		JScrollPane span = JCompTools.getTransparentScrollPane(arztwahltbl);
		jpan.add(span,cc.xyw(2, 4,6));
		return jpan;
	}
	public void fuelleTabelle(String suchkrit){
		arztwahlmod.setRowCount(0);
		arztwahltbl.validate();
		//{"Name","Vorname","Strasse","Ort", "LANR",""};
		String[] zweikrit = suchkrit.split(" ");
		String krit = "";
		if(zweikrit.length > 1){
			krit = "(nachname like '%"+zweikrit[0].trim()+"%' or vorname like '%"+zweikrit[0].trim()+
			"%' or klinik like '%"+zweikrit[0].trim()+ "%' or arztnum like '%"+zweikrit[0].trim()+ "%' or ort like '%"+zweikrit[0].trim()+ "%') AND "+
			"(nachname like '%"+zweikrit[1].trim()+"%' or vorname like '%"+zweikrit[1].trim()+
			"%' or klinik like '%"+zweikrit[1].trim()+ "%' or arztnum like '%"+zweikrit[1].trim()+ "%' or ort like '%"+zweikrit[1].trim()+"%') order by nachname";			
	
		}else{
			krit = "nachname like '%"+suchkrit+"%' or vorname like '%"+suchkrit+
			"%' or klinik like '%"+suchkrit+ "%' or arztnum like '%"+suchkrit+"%' or ort like '%"+suchkrit+"%' order by nachname";			
		}

		Vector vec = SqlInfo.holeSaetze("arzt", "nachname,vorname,strasse,ort,arztnum,id", krit ,Arrays.asList(new String[]{}));
		int bis = vec.size();
		int i;
		for(i = 0; i < bis; i++){
			arztwahlmod.addRow((Vector)vec.get(i));	
		}
	}
	public void fuelleIdTabelle(String suchid){
		arztwahlmod.setRowCount(0);
		arztwahltbl.validate();
		Vector vec = SqlInfo.holeSaetze("arzt", "nachname,vorname,strasse,ort,arztnum,id", "id='"+suchid+"'" ,Arrays.asList(new String[]{}));
		int bis = vec.size();
		int i;
		for(i = 0; i < bis; i++){
			arztwahlmod.addRow((Vector)vec.get(i));	
		}
	}
	public void werteUebergeben(){
		int i = arztwahltbl.getSelectedRow();
		if(i >= 0){
			int model = arztwahltbl.convertRowIndexToModel(i);
			elterntfs[0].setText((String)arztwahlmod.getValueAt(model, 0));			
			elterntfs[1].setText((String)arztwahlmod.getValueAt(model, 4));	
			elterntfs[2].setText((String)arztwahlmod.getValueAt(model, 5));
			this.dispose();
		}
	}
	/************************************************************/
	public void neuAnlageArzt(){
		super.getSmartTitledPanel().setTitle("Arzt neu anlegen");
		if(ank == null){
			ank = new ArztNeuKurz(this);			
		}else{
			ank.allesAufNull();
		}
		grundPanel.remove(this.content);
		grundPanel.add(ank,BorderLayout.CENTER);
		ank.setzteFocus();
		grundPanel.validate();
		repaint();
	}
	public void zurueckZurTabelle(JRtaTextField[] jtfs){
		super.getSmartTitledPanel().setTitle("Arzt auswählen");
		if(jtfs != null){
			Vector<String> vec = new Vector<String>();
			vec.add(jtfs[2].getText());
			vec.add(jtfs[3].getText());
			vec.add(jtfs[4].getText());
			vec.add(jtfs[6].getText());
			vec.add(jtfs[7].getText());			
			vec.add(jtfs[14].getText());	
			arztwahlmod.addRow(vec);
			arztwahltbl.validate();
		}
		try{
			grundPanel.remove(ank);
		}catch(java.lang.NullPointerException ex){
			
		}
		grundPanel.add(this.content);
		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				tf.requestFocus();
			}
		});		
		grundPanel.validate();		
		repaint();
		ank = null;
	}
	/************************************************************/	
	class ArztWahlAction extends AbstractAction {
	    public void actionPerformed(ActionEvent e) {

	        if(e.getActionCommand().equals("f")){
	        	 tf.requestFocus();
	        }
	        if(e.getActionCommand().equals("n")){
	        	 neuAnlageArzt();
	        }

	    }
	}
	

	class ArztListener implements KeyListener{

		@Override
		public void keyPressed(KeyEvent arg0) {
			// TODO Auto-generated method stub
			if(((JComponent)arg0.getSource()).getName().equals("suchfeld") && arg0.getKeyCode() == 10){
				arg0.consume();
				fuelleTabelle(tf.getText().trim());
			}else if(((JComponent)arg0.getSource()).getName().equals("suchfeld") && arg0.getKeyCode() == 40){
				arztwahltbl.requestFocus();
				arztwahltbl.setRowSelectionInterval(0, 0);
			}else if(((JComponent)arg0.getSource()).getName().equals("arzttabelle") && arg0.getKeyCode() == 10){
				arg0.consume();
				werteUebergeben();
			}else if(((JComponent)arg0.getSource()).getName().equals("neuarzt") && arg0.getKeyCode() == 10){
				arg0.consume();
				neuAnlageArzt();
			}

			
			if(arg0.getKeyCode() == 27){
				arg0.consume();
				if(arztbisher.length() <= 1){
					//System.out.println("Arzt = "+arztbisher);
					elterntfs[0].setText("***nachtragen!!!***");			
					elterntfs[1].setText("999999999");
					
				}else{
					elterntfs[0].setText(arztbisher);			
				}
				dispose();
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

	
}
class MyArztWahlModel extends DefaultTableModel{
	   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Class getColumnClass(int columnIndex) {
		   if(columnIndex==0){return String.class;}
		  /* if(columnIndex==1){return JLabel.class;}*/
		   else{return String.class;}
  //return (columnIndex == 0) ? Boolean.class : String.class;
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
			//result = theData;
			return result;
		}
	    
	   
}