package lvaEntlassmitteilung;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.calendar.DateSelectionModel.SelectionMode;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class plusdatum extends JDialog implements ActionListener{
	
	JButton bnr1;	
	JButton bnr2;
	JRtaTextField tf1 = new JRtaTextField("DATUM",true);
	JXDatePicker datePicker = new JXDatePicker();
	
	JXMonthView monthView;
	ActionListener al;	
	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
	
	LVAoberflaeche eltern;
	public plusdatum( LVAoberflaeche xeltern){
		super();
		this.eltern = xeltern;
		this.setTitle("Datum hinzufügen");
		this.setPreferredSize(new Dimension(220,250));
		this.setContentPane(getoberflaeche());
		
	}
	public JPanel getoberflaeche(){
		
		FormLayout Pan1 = new FormLayout("10dlu,60dlu,5dlu,60dlu,10dlu",
		"10dlu,p,5dlu,p,5dlu");
		PanelBuilder pan1 = new PanelBuilder(Pan1);
		pan1.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		
		

		
		al = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				monthView.removeActionListener(al);
				tf1.setText(  sdf.format(monthView.getSelectionDate() ) );
				doOk();
			}
		};
		
		monthView = new JXMonthView();
		monthView.setTraversable(true);
		monthView.addActionListener(al);
		pan1.add(monthView,cc.xyw(2,2,3));

		
		bnr1 = new JButton("OK");
		bnr1.setActionCommand("ok");
		bnr1.addActionListener(this);
		pan1.add(bnr1,cc.xy(2,4));
		bnr2 = new JButton("Abbrechen");
		bnr2.setActionCommand("abbrechen");
		bnr2.addActionListener(this);
		pan1.add(bnr2,cc.xy(4,4));
		
		
		
		pan1.getPanel().validate();
		return pan1.getPanel();
		
		
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String cmd = arg0.getActionCommand();
		if(cmd.equals("ok")){
			doOk();
		}
		if(cmd.equals("abbrechen")){
			doAbbrechen();
		}
	}
	private void doOk(){
		this.eltern.abbruch = false;
		if(tf1.getText().trim().equals(".  .")){
			this.eltern.returnString = DatFunk.sHeute();
		}else{
			this.eltern.returnString = tf1.getText();			
		}

		setVisible(false);
		dispose();
		return;
	}
	private void doAbbrechen(){
		this.eltern.abbruch = true;
		this.eltern.returnString = null;
		setVisible(false);
		dispose();
		return;
	}
}
