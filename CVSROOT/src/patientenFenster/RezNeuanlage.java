package patientenFenster;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import systemTools.Colors;
import systemTools.JCompTools;
import systemTools.JRtaCheckBox;
import systemTools.JRtaComboBox;
import systemTools.JRtaRadioButton;
import systemTools.JRtaTextField;

public class RezNeuanlage extends JXPanel implements ActionListener, KeyListener,FocusListener {

	/**
	 * 
	 */
	public JRtaTextField[] jtf = {null,null,null,null,null,
									null,null,null,null,null,
									null,null,null,null,null};
	public JRtaCheckBox[] jcb = {null,null,null,null,null};
	
	//public JRtaRadioButton[] jrb = {null,null,null,null,null}; 
	public JRtaComboBox[] jcmb = {null,null,null,null,null,null,null,null};
	
	public JTextArea jta = null;
	
	private static final long serialVersionUID = 1L;
	public RezNeuanlage(Vector<String> vec,boolean neu,String sfeldname){
		
		super();
		setLayout(new BorderLayout());
		setOpaque(true);
		setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		add(getDatenPanel(),BorderLayout.CENTER);
		new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				// TODO Auto-generated method stub
				Point2D start = new Point2D.Float(0, 0);
			     Point2D end = new Point2D.Float(PatGrundPanel.thisClass.getWidth(),100);
			     float[] dist = {0.0f, 0.75f};
			     Color[] colors = {Color.WHITE,Colors.Yellow.alpha(0.05f)};
			     LinearGradientPaint p =
			         new LinearGradientPaint(start, end, dist, colors);
			     MattePainter mp = new MattePainter(p);
			     setBackgroundPainter(new CompoundPainter(mp));		
				return null;
			}
			
		}.execute();		

		validate();
	}
	
	/********************************************/

	private JScrollPane getDatenPanel(){  //1                  2      3    4          5              6      7        8       
		FormLayout lay = new FormLayout("right:max(80dlu;p), 4dlu, 60dlu, 5dlu, right:max(60dlu;p), 4dlu, 60dlu",
			       //1.   2.   3.   4.   5.   6   7   8    9   10   11  12  13  14    15   16   17  18   19   20   21  22   23  24   25   26   27  28  29   30    31   32   33  34    35  36   37  38   39    40   41   42  43   44   45  46  47  48    49   50   51 52   53  54    55  56  57   58   59   60   61   62   63  64   65    66    67
					"p, 10dlu, p, 5dlu,  p, 5dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 40dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p , 10dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p,"+
				   //68   69  70   71   72   73   74  75 76    77  78   79 71 72
					"2dlu, p, 2dlu, p, 2dlu, p,  2dlu, p, 2dlu, p, 10dlu,p,10dlu");

		CellConstraints cc = new CellConstraints();
		PanelBuilder jpan = new PanelBuilder(lay);
		jpan.setDefaultDialogBorder();
		jpan.getPanel().setOpaque(false);
		String ywerte = "";
		/*
		jrb[0] = new JRtaRadioButton("Physiotherapie");
		jrb[1] = new JRtaRadioButton("Massage/Lymphdrainage");
		jrb[2] = new JRtaRadioButton("Ergotherapie");
		jrb[3] = new JRtaRadioButton("Logopädie");
		jrb[4] = new JRtaRadioButton("ambulante Reha");		
		
		
		jpan.add(jrb[0],cc.xyw(3, 3,3));
		jpan.add(jrb[1],cc.xyw(3, 5,3));
		jpan.add(jrb[2],cc.xyw(3, 7,3));
		jpan.add(jrb[3],cc.xyw(3, 9,3));
		jpan.add(jrb[4],cc.xyw(3, 11,3));		
		*/
		jcmb[0] = new JRtaComboBox(new String[] {"Physio-Rezept","Massage/Lymphdrainage-Rezept",
				"Ergotherapie-Rezept","Logopädie-Rezept","REHA-Verordnung"});
		jpan.addLabel("Heilmittel auswählen",cc.xy(1, 3));
		jpan.add(jcmb[0],cc.xyw(3, 3,5));
		
		jpan.addSeparator("Rezeptkopf", cc.xyw(1,5,7));
		
		jtf[0] = new JRtaTextField("NORMAL",true);
		jtf[0].setName("ktraeger");
		jtf[0].setText(PatGrundPanel.thisClass.patDaten.get(13));
		jpan.addLabel("Kostenträger",cc.xy(1,7));
		jpan.add(jtf[0],cc.xy(3,7));
		
		jtf[1] = new JRtaTextField("NORMAL",true);
		jtf[1].setName("arzt");
		jtf[1].setText(PatGrundPanel.thisClass.patDaten.get(25));
		jpan.addLabel("verordn. Arzt",cc.xy(5,7));
		jpan.add(jtf[1],cc.xy(7,7));
		
		jtf[2] = new JRtaTextField("DATUM",true);
		jtf[2].setName("rez_datum");
		jtf[2].setText(PatGrundPanel.thisClass.patDaten.get(2));
		jpan.addLabel("Rezeptdatum",cc.xy(1,9));
		jpan.add(jtf[2],cc.xy(3,9));
		
		jtf[3] = new JRtaTextField("DATUM",true);
		jtf[3].setName("lastdate");
		jtf[3].setText(PatGrundPanel.thisClass.patDaten.get(40));
		jpan.addLabel("spätester Beh.Beginn",cc.xy(5,9));
		jpan.add(jtf[3],cc.xy(7,9));

		jcmb[1] = new JRtaComboBox(new String[] {"Erstverodnung","Folgeverordnung",
				"außerhalb des Regelfalles"});
		jpan.addLabel("Art d. Verordn.",cc.xy(1, 11));
		jpan.add(jcmb[1],cc.xy(3, 11));
		jcb[0] = new JRtaCheckBox("vorhanden");
		jcb[0].setOpaque(false);
		jpan.addLabel("Begründ. für adR",cc.xy(5, 11));
		jpan.add(jcb[0],cc.xy(7, 11));
		
		jcb[1] = new JRtaCheckBox("Ja / Nein");
		jcb[1].setOpaque(false);
		jpan.addLabel("Hausbesuch",cc.xy(1, 13));
		jpan.add(jcb[1],cc.xy(3, 13));

		jcb[2] = new JRtaCheckBox("angefordert");
		jcb[2].setOpaque(false);
		jpan.addLabel("Arztbericht",cc.xy(5, 13));
		jpan.add(jcb[2],cc.xy(7, 13));
		
		jpan.addSeparator("Verordnete Heilmittel", cc.xyw(1,15,7));
		
		jtf[3] = new JRtaTextField("ZAHLEN",true);
		jpan.addLabel("Anzahl / Heilmittel 1",cc.xy(1, 17));
		jpan.add(jtf[3],cc.xy(3, 17));
		jcmb[2] = new JRtaComboBox();
		jpan.add(jcmb[2],cc.xyw(5, 17,3));
		
		jtf[4] = new JRtaTextField("ZAHLEN",true);
		jpan.addLabel("Anzahl / Heilmittel 2",cc.xy(1, 19));
		jpan.add(jtf[4],cc.xy(3, 19));
		jcmb[3] = new JRtaComboBox();
		jpan.add(jcmb[3],cc.xyw(5, 19,3));
		
		jtf[5] = new JRtaTextField("ZAHLEN",true);
		jpan.addLabel("Anzahl / Heilmittel 3",cc.xy(1, 21));
		jpan.add(jtf[5],cc.xy(3, 21));
		jcmb[4] = new JRtaComboBox();
		jpan.add(jcmb[4],cc.xyw(5, 21,3));

		jtf[6] = new JRtaTextField("ZAHLEN",true);
		jpan.addLabel("Anzahl / Heilmittel 4",cc.xy(1, 23));
		jpan.add(jtf[6],cc.xy(3, 23));
		jcmb[5] = new JRtaComboBox();
		jpan.add(jcmb[5],cc.xyw(5, 23,3));
		
		jpan.addSeparator("Durchführungsbestimmungen", cc.xyw(1,25,7));
		
		jpan.addLabel("Behandlungsfrequenz",cc.xy(1, 27));		
		jtf[7] = new JRtaTextField("GROSS",true);
		jpan.add(jtf[7],cc.xy(3, 27));	
		jtf[8] = new JRtaTextField("GROSS",true);
		jpan.addLabel("Dauer der Behandl. in Min.",cc.xy(5, 27));
		jpan.add(jtf[8],cc.xy(7, 27));

		
		jpan.addLabel("Indikationsschlüssel",cc.xy(1, 29));		
		jcmb[6] = new JRtaComboBox();
		jpan.add(jcmb[6],cc.xy(3, 29));	
		jtf[9] = new JRtaTextField("GROSS",true);
		jpan.addLabel("Angelegt von",cc.xy(5, 29));
		jpan.add(jtf[9],cc.xy(7, 29));
		
		jpan.addSeparator("Ärztliche Diagnose laut Verordnung", cc.xyw(1,31,7));
		
		jta = new JTextArea();
		jta.setFont(new Font("Courier",Font.PLAIN,11));
		jta.setLineWrap(true);
		jta.setName("notitzen");
		jta.setWrapStyleWord(true);
		jta.setEditable(true);
		jta.setBackground(Color.WHITE);
		jta.setForeground(Color.RED);
		JScrollPane span = JCompTools.getTransparentScrollPane(jta);		
		jpan.add(span,cc.xywh(1, 33,7,2));
		JScrollPane jscr = JCompTools.getTransparentScrollPane(jpan.getPanel());
		
		jscr.getVerticalScrollBar().setUnitIncrement(15);
		jscr.validate();
		return jscr;
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
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

	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
