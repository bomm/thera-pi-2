package dialoge;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;


import Tools.JCompTools;
import Tools.JRtaComboBox;
import Tools.JRtaTextField;
import Tools.SqlInfo;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class ArztNeuKurz extends JXPanel implements ActionListener,KeyListener,FocusListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8412450784730057738L;
	public JRtaTextField tfs[] = {null,null,null,null,null,
								  null,null,null,null,null,
								  null,null,null,null,null};
	public JButton speichern;
	public JButton abbrechen;
	public ArztAuswahl eltern; 
	public JScrollPane jscr;  
	public JRtaComboBox arztgruppe;
	public ArztNeuKurz(ArztAuswahl eltern){
		super();
		this.eltern = eltern;
		setOpaque(false);
		setLayout(new BorderLayout());
		add(getFelderPanel(),BorderLayout.CENTER);
		add(getButtonPanel(),BorderLayout.SOUTH);		
		validate();
	}
	public JScrollPane getFelderPanel(){
		FormLayout lay = new FormLayout(
		        // 1                  2      3      4                 5    6
				"right:max(60dlu;p), 4dlu, 60dlu,right:max(60dlu;p), 4dlu, 60dlu",
				//"0dlu,right:max(50dlu;p),3dlu,150dlu,3dlu,p,fill:0:grow(1.00)",
				// 1  2  3   4  5   6  7   8  9  10  11 12 13   14  15   16  17 18  19  20  21  22 23   24   25  26 27  28 29
				"3dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,5dlu,p,5dlu,p,2dlu,p, 5dlu, p, 5dlu,p,2dlu,p,2dlu, p, 5dlu, p, 5dlu,p,2dlu,p,10dlu");
		PanelBuilder jpan = new PanelBuilder(lay);
		jpan.setDefaultDialogBorder();
		jpan.getPanel().setOpaque(false);	
		jpan.getPanel().addKeyListener(this);
		CellConstraints cc = new CellConstraints();

		jpan.add(new JLabel("Anrede"),cc.xy(1,2));
		tfs[0] = new JRtaTextField("NORMAL",true);
		tfs[0].addKeyListener(this);
		tfs[0].addFocusListener(this);
		tfs[0].setName("anrede");
		jpan.add(tfs[0],cc.xy(3, 2));

		jpan.add(new JLabel("Titel"),cc.xy(4,2));
		tfs[1] = new JRtaTextField("NORMAL",true);
		tfs[1].addKeyListener(this);
		tfs[1].addFocusListener(this);
		tfs[1].setName("titel");
		jpan.add(tfs[1],cc.xy(6, 2));

		jpan.add(new JLabel("Nachname"),cc.xy(1,4));
		tfs[2] = new JRtaTextField("NORMAL",true);
		tfs[2].addKeyListener(this);
		tfs[2].addFocusListener(this);
		tfs[2].setName("nachname");
		jpan.add(tfs[2],cc.xyw(3, 4, 4));

		jpan.add(new JLabel("Vorname"),cc.xy(1,6));
		tfs[3] = new JRtaTextField("NORMAL",true);
		tfs[3].addKeyListener(this);
		tfs[3].addFocusListener(this);
		tfs[3].setName("vorname");
		jpan.add(tfs[3],cc.xyw(3, 6, 4));

		jpan.add(new JLabel("Strasse"),cc.xy(1, 8));
		tfs[4] = new JRtaTextField("NORMAL",true);
		tfs[4].addKeyListener(this);
		tfs[4].addFocusListener(this);		
		tfs[4].setName("strasse");
		jpan.add(tfs[4],cc.xyw(3, 8,4));
		
		jpan.add(new JLabel("Plz/Ort"),cc.xy(1, 10));
		tfs[5] = new JRtaTextField("ZAHLEN",true);
		tfs[5].addKeyListener(this);
		tfs[5].addFocusListener(this);		
		tfs[5].setName("plz");
		jpan.add(tfs[5],cc.xy(3, 10));

		tfs[6] = new JRtaTextField("NORMAL",true);
		tfs[6].addKeyListener(this);
		tfs[6].addFocusListener(this);
		tfs[6].setName("ort");
		jpan.add(tfs[6],cc.xyw(4, 10,3));

		
		jpan.addSeparator("Arztidentifikation",cc.xyw(1, 12, 6));

		
		jpan.add(new JLabel("LANR"),cc.xy(1, 14));
		tfs[7] = new JRtaTextField("ZAHLEN",true);
		tfs[7].addKeyListener(this);
		tfs[7].addFocusListener(this);		
		tfs[7].setName("arztnum");
		jpan.add(tfs[7],cc.xyw(3, 14,4));

		jpan.add(new JLabel("Betriebsstätte"),cc.xy(1, 16));
		tfs[8] = new JRtaTextField("ZAHLEN",true);
		tfs[8].addKeyListener(this);
		tfs[8].addFocusListener(this);		
		tfs[8].setName("bsnr");
		jpan.add(tfs[8],cc.xyw(3, 16,4));
		
		jpan.addSeparator("Kontakt",cc.xyw(1, 18, 6));

		jpan.add(new JLabel("Telefon"),cc.xy(1, 20));
		tfs[9] = new JRtaTextField("NORMAL",true);
		tfs[9].addKeyListener(this);
		tfs[9].addFocusListener(this);		
		tfs[9].setName("telefon");
		jpan.add(tfs[9],cc.xyw(3, 20,4));

		jpan.add(new JLabel("Telefax"),cc.xy(1, 22));
		tfs[10] = new JRtaTextField("NORMAL",true);
		tfs[10].addKeyListener(this);
		tfs[10].addFocusListener(this);		
		tfs[10].setName("fax");
		jpan.add(tfs[10],cc.xyw(3, 22,4));

		jpan.add(new JLabel("Email"),cc.xy(1, 24));
		tfs[11] = new JRtaTextField("NIX",true);
		tfs[11].addKeyListener(this);
		tfs[11].addFocusListener(this);
		tfs[11].setName("email1");
		jpan.add(tfs[11],cc.xyw(3, 24,4));
		
		jpan.addSeparator("Zusätze",cc.xyw(1, 26, 6));		

		jpan.add(new JLabel("Facharzt"),cc.xy(1, 28));
		arztgruppe = new JRtaComboBox();
		//arztgruppe = new JRtaComboBox(SystemConfig.arztGruppen);
		arztgruppe.addFocusListener(this);
		jpan.add(arztgruppe,cc.xyw(3, 28,4));

		jpan.add(new JLabel("Klinik"),cc.xy(1, 30));
		tfs[12] = new JRtaTextField("NIX",true);
		tfs[12].addKeyListener(this);
		tfs[12].addFocusListener(this);
		tfs[12].setName("klinik");
		jpan.add(tfs[12],cc.xyw(3, 30,4));

		tfs[13] = new JRtaTextField("NIX",true);
		tfs[13].setName("facharzt");
		tfs[14] = new JRtaTextField("NIX",true);
		tfs[14].setName("id");

		jpan.getPanel().validate();
		jscr = JCompTools.getTransparentScrollPane(jpan.getPanel());
		jscr.getVerticalScrollBar().setUnitIncrement(15);
		jscr.validate();
		return jscr;
}
	/**************************************************/
	public JXPanel getButtonPanel(){
		JXPanel	jpan = JCompTools.getEmptyJXPanel();
		jpan.addKeyListener(this);
		jpan.setOpaque(false);
		FormLayout lay = new FormLayout(
		        // 1                2          3             4      5    
				"fill:0:grow(0.33),50dlu,fill:0:grow(0.33),50dlu,fill:0:grow(0.33)",
				// 1  2  3  
				"5dlu,p,5dlu");
		CellConstraints cc = new CellConstraints();
		jpan.setLayout(lay);
		speichern = new JButton("speichern");
		speichern.setActionCommand("speichern");
		speichern.addActionListener(this);
		speichern.addKeyListener(this);
		speichern.setMnemonic(KeyEvent.VK_S);
		jpan.add(speichern,cc.xy(2,2));
		
		abbrechen = new JButton("abbrechen");
		abbrechen.setActionCommand("abbrechen");
		abbrechen.addActionListener(this);
		abbrechen.addKeyListener(this);
		abbrechen.setMnemonic(KeyEvent.VK_A);		
		jpan.add(abbrechen,cc.xy(4,2));

		return jpan;
	}
	public JXPanel getNeuKurzPanel(){
		return this;
	}
	public void setzteFocus(){
		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				tfs[0].requestFocus();
			}
		});
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		String comm = arg0.getActionCommand();
		if(comm.equals("speichern")){
			panelWechsel(true);
		}
		if(comm.equals("abbrechen")){
			panelWechsel(false);
		}

	}
	public void allesAufNull(){
		for(int i = 0; i < 15; i++){
			tfs[i].setText("");
		}
	}
	public void panelWechsel(boolean uebernahme){
		if(uebernahme){
			new SwingWorker<Void,Void>(){

				@Override
				protected Void doInBackground() throws Exception {
					if(tfs[2].getText().trim().equals("")){
						JOptionPane.showMessageDialog(null, "Also der Name des Arztes sollte wenigstens angegeben werden!");
						return null;
					}
					int iid;
					tfs[13].setText((String)arztgruppe.getSelectedItem());
					tfs[14].setText(Integer.toString(iid = SqlInfo.holeId("arzt", "nachname")));
					String stmt = "update arzt set ";
					for(int i = 0; i < 14; i++){
						stmt = stmt+ (i==0 ? "" : ", ")+tfs[i].getName()+"='"+tfs[i].getText()+"'";
					}
					stmt = stmt + " where id ='"+Integer.toString(iid)+"'";
					//System.out.println(stmt);
					SqlInfo.sqlAusfuehren(stmt);
					//new ExUndHop().setzeStatement(stmt);
					eltern.zurueckZurTabelle(tfs);
					return null;
				}
			}.execute();
		}else{
			eltern.zurueckZurTabelle(null);
		}

		
	}
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getKeyCode() == 27){
			arg0.consume();
			panelWechsel(false);
			eltern.zurueckZurTabelle(null);
			return;
		}
		try{
			if(arg0.getKeyCode() == 10){
				arg0.consume();

			if(((JComponent)arg0.getSource()).getName().equals("speichern")){
				arg0.consume();
				panelWechsel(true);
			}else if(((JComponent)arg0.getSource()).getName().equals("abbrechen")){
				arg0.consume();
				panelWechsel(true);
			}
			}
		}catch(java.lang.NullPointerException ex){
			arg0.consume();
		}

	}
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getKeyCode() == 27){
			arg0.consume();
		}
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getKeyCode() == 27){
			arg0.consume();
		}
	}
	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		Rectangle rec1 =((JComponent)arg0.getSource()).getBounds();
		Rectangle rec2 = jscr.getViewport().getViewRect();
		JViewport vp = jscr.getViewport();
		//Rectangle rec3 = vp.getVisibleRect();
		if((rec1.y+((JComponent)arg0.getSource()).getHeight()) > (rec2.y+rec2.height)){
			vp.setViewPosition(new Point(0,(rec2.y+rec2.height)-rec1.height));
		}
		if(rec1.y < (rec2.y)){
			vp.setViewPosition(new Point(0,rec1.y));
		}
		
		
	}
	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
