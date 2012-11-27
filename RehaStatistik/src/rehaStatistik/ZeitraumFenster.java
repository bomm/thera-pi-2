package rehaStatistik;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import org.jdesktop.swingx.JXPanel;

import CommonTools.ButtonTools;
import CommonTools.DatFunk;
import CommonTools.JRtaTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class ZeitraumFenster extends JDialog{
	/**
	 * 
	 */
	private static final long serialVersionUID = -287590702881008588L;
	StatistikPanel eltern = null;
	ActionListener al  = null;
	JRtaTextField von = null;
	JRtaTextField bis = null;
	JButton[] buts = {null,null};
	int retwert = -1;
	
	public ZeitraumFenster(StatistikPanel xeltern){
		super();
		this.activateActionListener();
		this.setContentPane(getContent());
		this.eltern = xeltern;
		this.setTitle("Zeitraum der Auswertung");
	}
	
	public JXPanel getContent(){
		String xwerte = "fill:0:grow(0.5),50dlu,2dlu,55dlu,fill:0:grow(0.5)";
		String ywerte = "10dlu,p,5dlu,p,10dlu:g,p,10dlu";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		JXPanel jpan = new JXPanel();
		jpan.setLayout(lay);
		
		JLabel lab = new JLabel("von Zeitraum");
		jpan.add(lab,cc.xy(2,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		von = new JRtaTextField("DATUM",true);
		jpan.add(von,cc.xy(4,2));
		
		lab = new JLabel("bis Zeitraum");
		jpan.add(lab,cc.xy(2,4,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		bis = new JRtaTextField("DATUM",true);
		jpan.add(bis,cc.xy(4,4));
		
		String xwerte2 = "fill:0:grow(0.33),65dlu,fill:0:grow(0.33),65dlu,fill:0:grow(0.33)";
		String ywerte2 = "p";
		FormLayout lay2 = new FormLayout(xwerte2,ywerte2);
		CellConstraints cc2 = new CellConstraints();
		JXPanel jpan2 = new JXPanel();
		jpan2.setLayout(lay2);
		
		buts[0] = ButtonTools.macheButton("übernehmen", "uebernehmen", al);
		buts[1] = ButtonTools.macheButton("abbrechen", "abbrechen", al);
		jpan2.add(buts[0],cc2.xy(2, 1));
		jpan2.add(buts[1],cc2.xy(4, 1));
		
		jpan.add(jpan2,cc.xyw(1,6,5));
		
		jpan.validate();
		return jpan;
	}
	private void activateActionListener(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("uebernehmen")){
					doUebernehmen();
					return;
				}
				if(cmd.equals("abbrechen")){
					doAbbrechen();
					return;
				}

			}
			
		};
	}
	private void doUebernehmen(){
		buts[0].removeActionListener(al);
		buts[1].removeActionListener(al);
		try{
			eltern.dlgRet = 0;
			retwert = 0;
			eltern.von = String.valueOf(DatFunk.sDatInSQL(von.getText()));			
			eltern.bis = String.valueOf(DatFunk.sDatInSQL(bis.getText()));
			System.out.println(eltern.von);
			System.out.println(eltern.bis);
		}catch(Exception ex){
			ex.printStackTrace();
			eltern.dlgRet = -1;
			retwert = -1;
			eltern.von = "";			
			eltern.bis = "";
		}
		this.dispose();
	}
	private void doAbbrechen(){
		buts[0].removeActionListener(al);
		buts[1].removeActionListener(al);
		eltern.dlgRet = -1;
		retwert = -1;
		eltern.von = "";			
		eltern.bis = "";
		System.out.println(eltern.von);
		System.out.println(eltern.bis);
		this.dispose();
	}
	
	/***********************************/


}
