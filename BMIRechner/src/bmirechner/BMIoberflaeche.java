package bmirechner;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXPanel;

import Tools.JCompTools;
import Tools.JRtaTextField;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;



public class BMIoberflaeche extends JXPanel implements ListSelectionListener, ActionListener{
	
	JComboBox combobox1;
	JComboBox combobox2;
	JButton bnr1;	
	JButton bnr2;
	JButton bnr3;
	JRtaTextField masse;
	JRtaTextField cm;
	JLabel bmi = null;
	JLabel gruppe = null;
	JLabel teil2[] = {null,null,null,null,null,null,null,null,null,null,null,null};
	
	
	
	public BMIoberflaeche(){
		super();
		
			setOpaque(false);
	
			
			//                                  
			FormLayout layob1 = new FormLayout("30dlu,p,10dlu,60dlu,fill:0:grow,30dlu",
			//    
			"10dlu,p,5dlu,p,5dlu,p,5dlu,p,5dlu,p,2dlu,p,20dlu,p,20dlu,p,10dlu");
					CellConstraints c1 = new CellConstraints();
					setLayout(layob1);
					
					JLabel lblkg = new JLabel("Körpergewicht in kg");
					add(lblkg, c1.xy(2,2));	
					masse = new JRtaTextField("FL", true,"6.2","RECHTS");
					masse.setText("0,00");
					add(masse,c1.xy(4,2));
					JLabel lblcm = new JLabel("Körpergröße in cm");
					add(lblcm, c1.xy(2,4));	
					cm = new JRtaTextField("ZAHLEN",true);
					cm.setHorizontalAlignment(SwingConstants.RIGHT);
					cm.setText("0");
					add(cm,c1.xy(4,4));
					
					bnr1 = new JButton("rechnen...");
					bnr1.setActionCommand("rechnen");
					bnr1.addActionListener(this);
					add(bnr1,c1.xy(4,6));
					
					JLabel lblbmi = new JLabel("individueller BMI:");
					add(lblbmi, c1.xy(2,8));	
					bmi = new JLabel("");
					bmi.setForeground(Color.BLUE);
					add(bmi,c1.xy(4,8));
					
					
					JLabel lblsort1 = new JLabel("individuelle");
					add(lblsort1, c1.xy(2,10));	
					JLabel lblsort2 = new JLabel("Eingruppierung:");
					add(lblsort2, c1.xy(2,12));	
					gruppe = new JLabel("");
					gruppe.setForeground(Color.RED);
					add(gruppe,c1.xy(4,12));

					add(getTeil2(),c1.xyw(2,14,4, CellConstraints.FILL,CellConstraints.FILL));
					
					bnr1 = new JButton("Schliessen");
					bnr1.setActionCommand("schliessen");
					bnr1.addActionListener(this);
					add(bnr1,c1.xy(4,16));
	
	}	
	private JPanel getTeil2(){
		FormLayout teil2Pan = new FormLayout("0dlu,p,10dlu,60dlu,0dlu",
				"p,10dlu,p,5dlu,p,5dlu,p,5dlu,p,5dlu,p,10dlu");
		PanelBuilder t2area = new PanelBuilder(teil2Pan);
		t2area.getPanel().setOpaque(false);
		CellConstraints ct2 = new CellConstraints();
		
		teil2[0] = new JLabel("Gewichtsklassifiktation nach WHO");
		t2area.add(teil2[0], ct2.xyw(2,1,4));	
		
		teil2[1] = new JLabel("Normalgewicht");
		t2area.add(teil2[1], ct2.xy(2,3));	
		teil2[2] = new JLabel("Übergewicht");
		t2area.add(teil2[2], ct2.xy(2,5));	
		teil2[3] = new JLabel("Adipositas Grad 1");
		t2area.add(teil2[3], ct2.xy(2,7));	
		teil2[4] = new JLabel("Adipositas Grad 2");
		t2area.add(teil2[4], ct2.xy(2,9));	
		teil2[5] = new JLabel("Adipositas Grad 3");
		t2area.add(teil2[5], ct2.xy(2,11));	
		
		teil2[6] = new JLabel("18,5 - 24,9");
		t2area.add(teil2[6], ct2.xy(4,3));	
		teil2[7] = new JLabel("25,0 - 29,9");
		t2area.add(teil2[7], ct2.xy(4,5));	
		teil2[8] = new JLabel("30,0 - 34,9");
		t2area.add(teil2[8], ct2.xy(4,7));	
		teil2[9] = new JLabel("35,0 - 39,9");
		t2area.add(teil2[9], ct2.xy(4,9));	
		teil2[10] = new JLabel("40,0 - mehr");
		t2area.add(teil2[10], ct2.xy(4,11));	
		
		
	
	
		
		
		
	
	
		t2area.getPanel().validate();
		return t2area.getPanel();
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	private void doRechnen(){
		DecimalFormat dcf = new DecimalFormat("#####0.00");
		BigDecimal gross = new BigDecimal(Double.parseDouble(cm.getText().trim())/100);
		gross =gross.pow(2);
		//System.out.println(gross);
		BigDecimal  gewicht = new BigDecimal(Double.parseDouble(masse.getText().trim().replace(",", ".")));
		//System.out.println(gewicht);
		BigDecimal dbmi = gewicht.divide(gross, BigDecimal.ROUND_HALF_UP);
		bmi.setText(dcf.format(dbmi));
		Double ddbmi = dbmi.doubleValue();
		Double[][] dvergleich = {{0.0,18.4},
								{18.5,24.9},
								{25.0,29.9},
								{30.0,34.9},
								{35.0,39.9},
								{40.0,0.00}};
		if(ddbmi < dvergleich[0][1]){
			gruppe.setText("Untergewicht");
			doClipBoard(gruppe.getText(),dcf.format(dbmi));
			return;
		}
		if( (ddbmi >= dvergleich[1][0]) && (ddbmi <= dvergleich[1][1])){
			gruppe.setText("Normalgewicht");
			doClipBoard(gruppe.getText(),dcf.format(dbmi));
			return;
		}
		if( (ddbmi >= dvergleich[2][0]) && (ddbmi <= dvergleich[2][1])){
			gruppe.setText("Übergewicht");
			doClipBoard(gruppe.getText(),dcf.format(dbmi));
			return;
		}
		if( (ddbmi >= dvergleich[3][0]) && (ddbmi <= dvergleich[3][1])){
			gruppe.setText("Adipositas Grad 1");
			doClipBoard(gruppe.getText(),dcf.format(dbmi));
			return;
		}
		if( (ddbmi >= dvergleich[4][0]) && (ddbmi <= dvergleich[4][1])){
			gruppe.setText("Adipositas Grad 2");
			doClipBoard(gruppe.getText(),dcf.format(dbmi));
			return;
		}
		if( (ddbmi >= dvergleich[5][0]) ){
			gruppe.setText("Adipositas Grad 3");
			doClipBoard(gruppe.getText(),dcf.format(dbmi));
			return;
		}		
		
	}
	private void doClipBoard(String gruppe,String bmi){
		String toClip = "Der ermittelte BodyMass-Index (BMI) liegt bei "+bmi+
		". Die Eingruppierung nach WHO ist daher "+gruppe+".";
		StringSelection stringSelection = new StringSelection( toClip );
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    clipboard.setContents( stringSelection, null );

	
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String cmd = arg0.getActionCommand();
		
		if(cmd.equals("rechnen")){
			doRechnen();
			//masse/(cm*cm) = bmi 

		}	
		if(cmd.equals("schliessen")){
			System.exit(0);

		}	
		
		
		
	}
}
