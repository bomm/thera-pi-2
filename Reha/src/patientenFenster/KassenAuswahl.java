package patientenFenster;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import patientenFenster.ArztAuswahl.ArztListener;
import patientenFenster.ArztAuswahl.ArztWahlAction;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import sqlTools.SqlInfo;
import systemTools.Colors;
import systemTools.JCompTools;
import systemTools.JRtaTextField;

import dialoge.RehaSmartDialog;

public class KassenAuswahl extends RehaSmartDialog{
	
	public JFormattedTextField tf = null;
	String suchkrit = "";
	String suchid = "";
	public JXTable kassenwahltbl = null;
	public JButton neukasse = null;
	public MyKassenWahlModel kassenwahlmod = null;
	public JRtaTextField[] elterntfs;
	JXPanel content = null;
	KassenNeuKurz knk = null;
	public JXPanel grundPanel = null;
	public String kassenbisher;



	public KassenAuswahl(JXFrame owner, String name,String[] suchegleichnach,JRtaTextField[] elterntf,String kassennum){
		super(owner, name);
		//setSize(430,300);
		setSize(450,400);
		this.suchkrit = suchegleichnach[0];
		this.suchid = suchegleichnach[1];
		this.elterntfs = elterntf;
		this.kassenbisher = kassennum;

		super.getSmartTitledPanel().setTitleForeground(Color.WHITE);
	    super.getSmartTitledPanel().setTitle("Krankenkasse auswählen");
	     

		grundPanel = new JXPanel(new BorderLayout());
		/************BackgroundPainter basteln************/
		/*
		Point2D start = new Point2D.Float(0, 0);
		Point2D end = new Point2D.Float(0,getHeight());
	    float[] dist = {0.0f, 0.75f};
	    // Color[] colors = {Color.WHITE,new Color(231,120,23)};
	    Color[] colors = {Color.WHITE,Colors.Green.alpha(0.45f)};
	    //Color[] colors = {Color.WHITE,getBackground()};
	    p =
	         new LinearGradientPaint(start, end, dist, colors);
	    mp = new MattePainter(p);
	    */
	    /************Ende BackgroundPainter basteln************/
	    //cp = new CompoundPainter(mp);
		grundPanel.setBackgroundPainter(Reha.thisClass.compoundPainter.get("KassenAuswahl"));
		content = getAuswahl();
		grundPanel.add(content,BorderLayout.CENTER);
		getSmartTitledPanel().setContentContainer(grundPanel);
		final JXPanel thispan = content;
		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.ALT_MASK);
				((JComponent)getContentPanel()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "doSuchen");
				((JComponent)getContentPanel()).getActionMap().put("doSuchen", new KassenWahlAction());
			}
		});
		
	}
	public JXPanel getAuswahl(){
		JXPanel jpan = new JXPanel();
		jpan.setOpaque(false);
		jpan.setBackground(Color.WHITE);
		FormLayout lay = new FormLayout("5dlu,p,3dlu,100dlu:g,3dlu,40dlu,fill:0:grow(1.00),5dlu","3dlu,p,3dlu,150dlu:g,5dlu");
		CellConstraints cc = new CellConstraints();
		jpan.setLayout(lay);
		jpan.add(new JLabel("Kasse finden:"),cc.xy(2,2));
		tf = new JFormattedTextField();
		tf.setText(this.suchkrit);
		tf.setName("suchfeld");
		KeyListener akl = new KassenListener();
		tf.addKeyListener(akl);
		jpan.add(tf,cc.xy(4,2));
		neukasse = new JButton("neu anlegen");
		neukasse.setName("neukasse");
		neukasse.setMnemonic(KeyEvent.VK_N);
		neukasse.addKeyListener(akl);
		neukasse.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				neuAnlageKassen();
			}
	    });
		jpan.add(neukasse,cc.xyw(6,2,2));
		
		kassenwahlmod = new MyKassenWahlModel();
		String[] column = 	{"Kürzel","Name1","Name2","Ort", "IK",""};
		kassenwahlmod.setColumnIdentifiers(column);
		kassenwahltbl = new JXTable(kassenwahlmod);
		kassenwahltbl.addKeyListener(akl);
		kassenwahltbl.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getClickCount() == 2){
					werteUebergeben();					
				}
			}
		});
		kassenwahltbl.setName("kassentabelle");
		kassenwahltbl.setHighlighters(HighlighterFactory.createSimpleStriping(Colors.Green.alpha(0.1f)));
		kassenwahltbl.setDoubleBuffered(true);
		kassenwahltbl.setEditable(false);
		kassenwahltbl.setSortable(true);
		kassenwahltbl.setSelectionMode(0);
		
		kassenwahltbl.getColumn(0).setMinWidth(50);
		kassenwahltbl.getColumn(0).setMaxWidth(50);
		kassenwahltbl.getColumn(1).setMinWidth(150);		
		kassenwahltbl.getColumn(2).setMinWidth(150);
		kassenwahltbl.getColumn(3).setMinWidth(100);		
		kassenwahltbl.getColumn(5).setMinWidth(0);
		kassenwahltbl.getColumn(5).setMaxWidth(0);
		
		kassenwahltbl.setHorizontalScrollEnabled(true);
		if(this.suchid.length() > 0){
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
		JScrollPane span = JCompTools.getTransparentScrollPane(kassenwahltbl);
		jpan.add(span,cc.xyw(2, 4,6));
		return jpan;
	}
	public void fuelleTabelle(String suchkrit){
		kassenwahlmod.setRowCount(0);
		kassenwahltbl.validate();
		//{"Name","Vorname","Strasse","Ort", "LANR",""};
		String[] zweikrit = suchkrit.split(" ");
		String krit = "";
		if(zweikrit.length > 1){
			krit = "(kuerzel like '%"+zweikrit[0].trim()+"%' or kassen_nam1 like '%"+zweikrit[0].trim()+
			"%' or kassen_nam2 like '%"+zweikrit[0].trim()+ "%' or ort like '%"+zweikrit[0].trim()+ "%' or ik_kasse like '%"+zweikrit[0].trim()+ "%') AND "+
			"(kuerzel like '%"+zweikrit[1].trim()+"%' or kassen_nam1 like '%"+zweikrit[1].trim()+
			"%' or kassen_nam2 like '%"+zweikrit[1].trim()+ "%' or ik_kasse like '%"+zweikrit[1].trim()+ "%' or ort like '%"+zweikrit[1].trim()+"%') order by kassen_nam1";			
	
		}else{
			krit = "kuerzel like '%"+suchkrit+"%' or kassen_nam1 like '%"+suchkrit+
			"%' or kassen_nam2 like '%"+suchkrit+ "%' or ort like '%"+suchkrit+"%' or ik_kasse like '%"+suchkrit+"%' order by kassen_nam1";			
		}

		Vector vec = SqlInfo.holeSaetze("kass_adr", "kuerzel,kassen_nam1,kassen_nam2,ort,ik_kasse,id", krit ,Arrays.asList(new String[]{}));
		int bis = vec.size();
		int i;
		for(i = 0; i < bis; i++){
			kassenwahlmod.addRow((Vector)vec.get(i));	
		}
	}
	
	public void fuelleIdTabelle(String suchid){
		kassenwahlmod.setRowCount(0);
		kassenwahltbl.validate();
		Vector vec = SqlInfo.holeSaetze("kass_adr", "kuerzel,kassen_nam1,kassen_nam2,ort,ik_kasse,id", "id='"+suchid+"'" ,Arrays.asList(new String[]{}));
		int bis = vec.size();
		int i;
		for(i = 0; i < bis; i++){
			kassenwahlmod.addRow((Vector)vec.get(i));	
		}
	}

	
	public void neuAnlageKassen(){
		super.getSmartTitledPanel().setTitle("Kasse neu anlegen");
		if(knk == null){
			knk = new KassenNeuKurz(this);			
		}else{
			knk.allesAufNull();
		}
		grundPanel.remove(this.content);
		grundPanel.add(knk,BorderLayout.CENTER);
		knk.setzeFocus();
		grundPanel.validate();
		repaint();
		
	}
	public void zurueckZurTabelle(JRtaTextField[] jtfs){
		super.getSmartTitledPanel().setTitle("Kasse auswählen");
		if(jtfs != null){
			//{"K�rzel","Name1","Name2","Ort", "IK",""};
			Vector<String> vec = new Vector<String>();
			vec.add(jtfs[0].getText());
			vec.add(jtfs[1].getText());
			vec.add(jtfs[2].getText());
			vec.add(jtfs[5].getText());
			vec.add(jtfs[9].getText());			
			vec.add(jtfs[15].getText());	
			kassenwahlmod.addRow(vec);
			kassenwahltbl.validate();
			int anzahl = kassenwahltbl.getRowCount()-1;
			kassenwahltbl.requestFocus();
			kassenwahltbl.setRowSelectionInterval(anzahl, anzahl);
		}
		try{
			grundPanel.remove(knk);
		}catch(java.lang.NullPointerException ex){
			System.out.println(ex);
		}
		grundPanel.add(this.content);
		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				tf.requestFocus();
			}
		});		
		grundPanel.validate();		
		repaint();
		knk = null;
	}
	public void werteUebergeben(){
		int i = kassenwahltbl.getSelectedRow();
		if(i >= 0){
			int model = kassenwahltbl.convertRowIndexToModel(i);
			elterntfs[0].setText((String)kassenwahlmod.getValueAt(model, 1));			
			elterntfs[1].setText((String)kassenwahlmod.getValueAt(model, 4));	
			elterntfs[2].setText((String)kassenwahlmod.getValueAt(model, 5));
			this.dispose();
		}
	}

	class KassenWahlAction extends AbstractAction {
	    public void actionPerformed(ActionEvent e) {

	        if(e.getActionCommand().equals("f")){
	        	 tf.requestFocus();
	        }
	        if(e.getActionCommand().equals("n")){
	        	 neuAnlageKassen();
	        }

	    }
	}
	/************************************************/
	class KassenListener implements KeyListener{

		@Override
		public void keyPressed(KeyEvent arg0) {
			// TODO Auto-generated method stub
			if(((JComponent)arg0.getSource()).getName().equals("suchfeld") && arg0.getKeyCode() == 10){
				arg0.consume();
				fuelleTabelle(tf.getText().trim());
			}else if(((JComponent)arg0.getSource()).getName().equals("suchfeld") && arg0.getKeyCode() == 40){
				kassenwahltbl.requestFocus();
				kassenwahltbl.setRowSelectionInterval(0, 0);
			}else if(((JComponent)arg0.getSource()).getName().equals("kassentabelle") && arg0.getKeyCode() == 10){
				arg0.consume();
				werteUebergeben();
			}else if(((JComponent)arg0.getSource()).getName().equals("neuarzt") && arg0.getKeyCode() == 10){
				arg0.consume();
				neuAnlageKassen();
			}

			
			if(arg0.getKeyCode() == 27){
				arg0.consume();
				if(kassenbisher.length() <= 1){
					//System.out.println("Arzt = "+arztbisher);
					elterntfs[0].setText("***nachtragen!!!***");			
					elterntfs[1].setText("999999999");
					
				}else{
					elterntfs[0].setText(kassenbisher);			
				}
				dispose();
			}
		}
		@Override
		public void keyReleased(KeyEvent arg0) {
			
		}
		@Override
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub
		}
	}	
	/************************************************/	
	/******************************************************/
	class MyKassenWahlModel extends DefaultTableModel{
		private static final long serialVersionUID = 1L;

		public Class getColumnClass(int columnIndex) {
			   if(columnIndex==0){return String.class;}
			   else{return String.class;}
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
}
