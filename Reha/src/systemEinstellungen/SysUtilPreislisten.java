package systemEinstellungen;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import jxTableTools.DblCellEditor;
import jxTableTools.DoubleTableCellRenderer;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import patientenFenster.AktuelleRezepte;

import systemEinstellungen.SysUtilKrankenkasse.MyVorlagenTableModel;
import systemTools.JCompTools;
import systemTools.JRtaComboBox;
import systemTools.JRtaTextField;
import terminKalender.ParameterLaden;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SysUtilPreislisten extends JXPanel implements KeyListener, ActionListener {
	
	JRtaComboBox[] jcmb = {null,null,null,null};
	JRtaTextField gueltig = null;
	JButton[] button = {null,null,null,null,null,null};
	MyPreislistenTableModel modpreis = new MyPreislistenTableModel();
	JXTable preislisten = null;
	
	public SysUtilPreislisten(){
		super(new BorderLayout());
		System.out.println("Aufruf SysUtilPreislisten");
		this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 20));
		/****/
	     Point2D start = new Point2D.Float(0, 0);
	     Point2D end = new Point2D.Float(400,500);
	     float[] dist = {0.0f, 0.5f};
	     Color[] colors = {Color.WHITE,getBackground()};
	     LinearGradientPaint p =
	         new LinearGradientPaint(start, end, dist, colors);
	     MattePainter mp = new MattePainter(p);
	     setBackgroundPainter(new CompoundPainter(mp));
		/****/
	     add(getVorlagenSeite(),BorderLayout.CENTER);

		return;
	}
	/************** Beginn der Methode für die Objekterstellung und -platzierung *********/
	private JPanel getVorlagenSeite(){
        //                                      1.            2.    3.    4.     5.     6.    7.      8.     9.
		FormLayout lay = new FormLayout("right:max(60dlu;p), 4dlu, 70dlu, 4dlu, 40dlu, 4dlu, 10dlu, 4dlu, 70dlu",
       //1.    2. 3.   4.   5.   6.  7.  8.    9.  10.  11. 12. 13.  14.  15. 16.  17. 18.  19.   20.    21.   22.   23.
		"p, 2dlu, p, 2dlu,  p,10dlu, p, 10dlu,160dlu");
		
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		builder.addLabel("Heilmittelart auswählen",cc.xy(1, 1));

		jcmb[0] = new JRtaComboBox(SystemConfig.rezeptKlassen);
		jcmb[0].setSelectedItem(SystemConfig.initRezeptKlasse);
		jcmb[0].setActionCommand("tabelleRegeln");
		jcmb[0].addActionListener(this);
		builder.add(jcmb[0],cc.xyw(3,1,7));
		
		builder.addLabel("Tarifgruppe auswählen",cc.xy(1, 3));
		jcmb[1] = new JRtaComboBox(SystemConfig.vPreisGruppen);
		jcmb[1].setActionCommand("tabelleRegeln");
		jcmb[1].addActionListener(this);
		builder.add(jcmb[1],cc.xyw(3,3,7));

		builder.addLabel("gültig ab",cc.xy(1,5));
		gueltig = new JRtaTextField("DATUM",true);
		gueltig.setText(SystemConfig.vNeuePreiseAb.get(jcmb[0].getSelectedIndex()));
		builder.add(gueltig, cc.xy(3,5));

		//builder.addLabel("Anwendungsregel",cc.xy(7,5,CellConstraints.RIGHT,CellConstraints.BOTTOM));
		String[] zzart = new String[] {"nicht relevant","erste Behandlung >=","Rezeptdatum >=","beliebige Behandlung >=","Rezept splitten"};
		jcmb[2] = new JRtaComboBox(zzart);
		builder.add(jcmb[2],cc.xy(9,5));
		
		modpreis.setColumnIdentifiers(new String[] {"HM-Pos.","Kurzbez.","Langtext","aktuell","alt"});
		preislisten = new JXTable(modpreis);
		preislisten.getColumn(0).setMaxWidth(65);
		preislisten.getColumn(1).setMaxWidth(65);
		preislisten.getColumn(3).setCellRenderer(new DoubleTableCellRenderer());
		preislisten.getColumn(3).setCellEditor(new DblCellEditor());
		preislisten.getColumn(3).setMaxWidth(50);
		preislisten.getColumn(4).setCellRenderer(new DoubleTableCellRenderer());
		preislisten.getColumn(4).setCellEditor(new DblCellEditor());
		preislisten.getColumn(4).setMaxWidth(50);
		JScrollPane jscr = JCompTools.getTransparentScrollPane(preislisten);
		jscr.validate();
		new SwingWorker(){
			@Override
			protected Object doInBackground() throws Exception {
				tabelleRegeln();
				return null;
			}
		}.execute();
		
		builder.add(jscr,cc.xyw(1,9,9));
		
		return builder.getPanel();
	}
	private void fuelleMitWerten(){
		int aktiv;
		INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/rezept.ini");
		for(int i = 0;i < 5;i++){
			aktiv = inif.getIntegerProperty("RezeptKlassen", "KlasseAktiv"+new Integer(i+1).toString());
			if(aktiv > 0){
				//heilmittel[i].setSelected(true);
			}else{
				//heilmittel[i].setSelected(false);
			}
			
		}
		jcmb[0].setSelectedItem(SystemConfig.initRezeptKlasse);
	}	
	
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String cmd = e.getActionCommand();
		if(cmd.equals("tabelleRegeln")){
			tabelleRegeln();
		}
		
	}
	
	public void tabelleRegeln(){
		Vector preisvec = holePreisVec();
		String spreisart = new Integer(jcmb[1].getSelectedIndex() +1).toString();
		int ipreis = jcmb[1].getSelectedIndex()+1;
		int anzahl = preisvec.size();
		modpreis.setRowCount(0);
		Vector vec = new Vector();
		for(int i = 0;i < anzahl ; i++){
			vec.clear();
			vec.add( (String) ((Vector)preisvec.get(i)).get( 2+(ipreis*4)-4) );
			vec.add((String)((Vector)preisvec.get(i)).get(1));
			vec.add((String)((Vector)preisvec.get(i)).get(0));
			try{
				vec.add(new Double( (String) ((Vector)preisvec.get(i)).get( 2+(ipreis*4)-3) ) );
			}catch(Exception ex){
				vec.add(new Double(0.00));
			}
			try{
				vec.add(new Double(  (String) ((Vector)preisvec.get(i)).get( 2+(ipreis*4)-2) ) );
			}catch(Exception ex){
				vec.add(new Double(0.00));
			}
			modpreis.addRow((Vector)vec.clone());
		}
		preislisten.validate();
		
		
		
	}
 
	private Vector holePreisVec(){
		String diszi = (String)jcmb[0].getSelectedItem();
		if(diszi.contains("REHA")){
			return ParameterLaden.vRHPreise;
		}
		if(diszi.contains("Physio")){
			return ParameterLaden.vKGPreise;
		}
		if(diszi.contains("Massage")){
			return ParameterLaden.vMAPreise;
		}		
		if(diszi.contains("Ergo")){
			return ParameterLaden.vERPreise;
		}
		if(diszi.contains("Logo")){
			return ParameterLaden.vLOPreise;
		}
		return new Vector();
	}
/*****************vor Ende Klassenklammer*************/	
}



class MyPreislistenTableModel extends DefaultTableModel{
	   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Class getColumnClass(int columnIndex) {
		if(columnIndex==3 || columnIndex==4){
			return Double.class;
		}
		   return String.class;
    }

	public boolean isCellEditable(int row, int col) {
		return true;
	}
	   
}
