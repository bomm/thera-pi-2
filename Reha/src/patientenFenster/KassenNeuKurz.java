package patientenFenster;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import sqlTools.ExUndHop;
import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemEinstellungen.SystemPreislisten;
import systemTools.JCompTools;
import systemTools.JRtaComboBox;
import systemTools.JRtaTextField;

public class KassenNeuKurz extends JXPanel implements ActionListener,KeyListener,FocusListener{

	public KassenAuswahl eltern;
	JScrollPane jscr = null;
	JButton speichern = null;
	JButton abbrechen = null;
	JRtaComboBox tarifGruppe = null;
	boolean ohneKuerzel = false;
	public JRtaTextField tfs[] = {null,null,null,null,null,
			  null,null,null,null,null,
			  null,null,null,null,null,
			  null};

	
	public KassenNeuKurz(KassenAuswahl eltern){
		super();
		this.eltern = eltern;
		setOpaque(false);
		setLayout(new BorderLayout());
		add(getFelderPanel(),BorderLayout.CENTER);
		add(getButtonPanel(),BorderLayout.SOUTH);		
		validate();		
	}
	public void setzeFocus(){
		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				tfs[0].requestFocus();
			}
		});
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
					if(tfs[1].getText().trim().equals("")){
						JOptionPane.showMessageDialog(null, "Also der Name des neuen Krankenkasse sollte wenigstens angegeben werden!");
						return null;
					}
					int iid;
					tfs[14].setText(Integer.toString(tarifGruppe.getSelectedIndex()+1));
					tfs[15].setText(Integer.toString( iid = SqlInfo.holeId("kass_adr", "kassen_nam1")));
					StringBuffer kkBuffer = new StringBuffer();
					kkBuffer.append("update kass_adr set ");
					//String stmt = "update kass_adr set ";
					for(int i = 0; i < 15; i++){
						kkBuffer.append((i==0 ? "": ", ")+tfs[i].getName()+"='"+tfs[i].getText()+"', ");						
					}
					kkBuffer.append("preisgruppe ='"+ Integer.toString(tarifGruppe.getSelectedIndex()+1).toString()+"', ");
					kkBuffer.append("pgkg ='"+ Integer.toString(tarifGruppe.getSelectedIndex()+1)+"', ");
					kkBuffer.append("pgma ='"+ Integer.toString(tarifGruppe.getSelectedIndex()+1)+"', ");
					kkBuffer.append("pger ='"+ Integer.toString(tarifGruppe.getSelectedIndex()+1)+"', ");
					kkBuffer.append("pglo ='"+ Integer.toString(tarifGruppe.getSelectedIndex()+1)+"', ");
					kkBuffer.append("pgrh ='"+ Integer.toString(tarifGruppe.getSelectedIndex()+1)+"' ");
					kkBuffer.append(" where id ='"+Integer.toString(iid)+"'");
					//stmt = stmt + " where id ='"+Integer.toString(iid)+"'";
					//System.out.println(stmt);
					System.out.println("In Preisgruppe abspeichern Preisgruppe = "+Integer.toString(tarifGruppe.getSelectedIndex()+1));
					SqlInfo.sqlAusfuehren(kkBuffer.toString());
					eltern.zurueckZurTabelle(tfs);
					return null;
				}
				
			}.execute();
		}else{
			eltern.zurueckZurTabelle(null);
		}

		
	}


	public JScrollPane getFelderPanel(){
		FormLayout lay = new FormLayout(
		        // 1                  2      3      4                 5    6
				"right:max(60dlu;p), 4dlu, 60dlu,right:max(60dlu;p), 4dlu, 60dlu",
				//"0dlu,right:max(50dlu;p),3dlu,150dlu,3dlu,p,fill:0:grow(1.00)",
				// 1  2  3   4  5   6  7   8  9  10  11 12 13   14  15 16  17   18  19  20  21  22  23  24   25  26  27  28 29  30  31  32  33
				"3dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,5dlu,p, 5dlu,p, 2dlu, p, 2dlu,p,5dlu, p,5dlu, p, 2dlu, p, 2dlu,p,2dlu,p, 2dlu,p,10dlu");
		PanelBuilder jpan = new PanelBuilder(lay);
		jpan.setDefaultDialogBorder();
		jpan.getPanel().setOpaque(false);	
		jpan.getPanel().addKeyListener(this);
		CellConstraints cc = new CellConstraints();

		jpan.add(new JLabel("Kuerzel"),cc.xy(1,2));
		tfs[0] = new JRtaTextField("GROSS",true);
		tfs[0].addKeyListener(this);
		tfs[0].addFocusListener(this);
		tfs[0].setName("kuerzel");
		MaskFormatter uppercase = null;
		try {
			uppercase = new MaskFormatter("AAA-AA");
		} catch (ParseException e) {
			e.printStackTrace();
		}
        DefaultFormatterFactory factory = new DefaultFormatterFactory(uppercase);
        tfs[0].setFormatterFactory(factory);
        tfs[0].addFocusListener(this);		
		jpan.add(tfs[0],cc.xy(3, 2));
		
		jpan.addLabel("Tarifgruppe", cc.xy(1,4));
		tarifGruppe = new JRtaComboBox();
		tarifGruppe.setName("tarifgruppe");
		tarifGruppe.addFocusListener(this);
		jpan.add(tarifGruppe, cc.xyw(3, 4, 4));
		new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				int gruppen = SystemPreislisten.hmPreisGruppen.get("Common").size();
				//int gruppen = SystemConfig.vPreisGruppen.size();
				for(int i = 0; i < gruppen;i++){
					tarifGruppe.addItem(SystemPreislisten.hmPreisGruppen.get("Common").get(i));
				}
				tarifGruppe.setSelectedIndex(0);
				return null;
			}
			
		}.execute();
		
		jpan.add(new JLabel("Name_1"),cc.xy(1,6));
		tfs[1] = new JRtaTextField("NIX",true);
		tfs[1].addKeyListener(this);
		tfs[1].addFocusListener(this);
		tfs[1].setName("kassen_nam1");
		jpan.add(tfs[1],cc.xyw(3, 6, 4));
		
		jpan.add(new JLabel("Name_2"),cc.xy(1,8));
		tfs[2] = new JRtaTextField("NORMAL",true);
		tfs[2].addKeyListener(this);
		tfs[2].addFocusListener(this);
		tfs[2].setName("kassen_nam2");
		jpan.add(tfs[2],cc.xyw(3, 8, 4));
		
		jpan.add(new JLabel("Strasse"),cc.xy(1,10));
		tfs[3] = new JRtaTextField("NORMAL",true);
		tfs[3].addKeyListener(this);
		tfs[3].addFocusListener(this);
		tfs[3].setName("strasse");
		jpan.add(tfs[3],cc.xyw(3, 10, 4));
		
		jpan.add(new JLabel("Plz/Ort"),cc.xy(1, 12));
		tfs[4] = new JRtaTextField("ZAHLEN",true);
		tfs[4].addKeyListener(this);
		tfs[4].addFocusListener(this);		
		tfs[4].setName("plz");
		jpan.add(tfs[4],cc.xy(3, 12));

		tfs[5] = new JRtaTextField("NORMAL",true);
		tfs[5].addKeyListener(this);
		tfs[5].addFocusListener(this);
		tfs[5].setName("ort");
		jpan.add(tfs[5],cc.xyw(4, 12,3));
		
		jpan.addSeparator("Kontakt",cc.xyw(1, 14, 6));		
		
		jpan.add(new JLabel("Telefon"),cc.xy(1, 16));
		tfs[6] = new JRtaTextField("ZAHLEN",true);
		tfs[6].addKeyListener(this);
		tfs[6].addFocusListener(this);
		tfs[6].setName("telefon");
		jpan.add(tfs[6],cc.xyw(3, 16,4));
		
		jpan.add(new JLabel("Telefax"),cc.xy(1, 18));
		tfs[7] = new JRtaTextField("ZAHLEN",true);
		tfs[7].addKeyListener(this);
		tfs[7].addFocusListener(this);
		tfs[7].setName("telefon");
		jpan.add(tfs[7],cc.xyw(3, 18,4));

		jpan.add(new JLabel("Email"),cc.xy(1, 20));
		tfs[8] = new JRtaTextField("NIX",true);
		tfs[8].addKeyListener(this);
		tfs[8].addFocusListener(this);
		tfs[8].setName("email1");
		jpan.add(tfs[8],cc.xyw(3, 20,4));

		jpan.addSeparator("IK-Daten für maschinenlesbare Abrechnung",cc.xyw(1, 22, 6));

		jpan.add(new JLabel("IK der Krankenkasse"),cc.xy(1, 24));
		tfs[9] = new JRtaTextField("ZAHLEN",true);
		tfs[9].addKeyListener(this);
		tfs[9].addFocusListener(this);
		tfs[9].setName("ik_kasse");
		jpan.add(tfs[9],cc.xyw(3, 24,4));
		
		jpan.add(new JLabel("IK des Kostenträgers"),cc.xy(1, 26));
		tfs[10] = new JRtaTextField("ZAHLEN",true);
		tfs[10].addKeyListener(this);
		tfs[10].addFocusListener(this);
		tfs[10].setName("ik_kostent");
		jpan.add(tfs[10],cc.xyw(3, 26,4));
		
		jpan.add(new JLabel("IK der Datenannahmest."),cc.xy(1, 28));
		tfs[11] = new JRtaTextField("ZAHLEN",true);
		tfs[11].addKeyListener(this);
		tfs[11].addFocusListener(this);
		tfs[11].setName("ik_physika");
		jpan.add(tfs[11],cc.xyw(3, 28,4));

		jpan.add(new JLabel("IK Nutzer/Entschl."),cc.xy(1, 30));
		tfs[12] = new JRtaTextField("ZAHLEN",true);
		tfs[12].addKeyListener(this);
		tfs[12].addFocusListener(this);
		tfs[12].setName("ik_physika");
		jpan.add(tfs[12],cc.xyw(3, 30,4));

		jpan.add(new JLabel("IK Papierannahme"),cc.xy(1, 32));
		tfs[13] = new JRtaTextField("ZAHLEN",true);
		tfs[13].addKeyListener(this);
		tfs[13].addFocusListener(this);
		tfs[13].setName("ik_physika");
		jpan.add(tfs[13],cc.xyw(3, 32,4));
		
		tfs[14] = new JRtaTextField("NIX",true);
		tfs[14].setName("preisgruppe");
		tfs[15] = new JRtaTextField("NIX",true);
		tfs[15].setName("id");
		/*
		jtf[13] = new JRtaTextField("ZAHLEN", true);
		jtf[13].setName("IK_KASSE"); //aus Kostentr�gerdatei/Karte einlesen?
		jtf[14] = new JRtaTextField("ZAHLEN", true);
		jtf[14].setName("IK_KOSTENT");
		jtf[15] = new JRtaTextField("ZAHLEN", true);
		jtf[15].setName("IK_PHYSIKA");
		jtf[16] = new JRtaTextField("ZAHLEN", true);
		jtf[16].setName("IK_NUTZER");
		jtf[17] = new JRtaTextField("ZAHLEN", true);
		jtf[17].setName("IK_PAPIER");
		*/
		
		jscr = JCompTools.getTransparentScrollPane(jpan.getPanel());
		jscr.getVerticalScrollBar().setUnitIncrement(15);
		jscr.validate();
		
		return jscr;
	}
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


	/**********************Listener der Klasse*****************************/
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
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		Rectangle rec1 =((JComponent)arg0.getSource()).getBounds();
		Rectangle rec2 = jscr.getViewport().getViewRect();
		JViewport vp = jscr.getViewport();
		Rectangle rec3 = vp.getVisibleRect();
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
		/*
		if(((JComponent)arg0.getSource()).getName().equals("kuerzel")){
			if( (tfs[0].getText().trim().equals("-")) && (!ohneKuerzel) ){
				ohneKuerzel = true;
				String message = "Tip:\n\nDie Vergabe von K�rzeln ist f�r die schnelle Bearbeitung einer Kasse (sp�ter) ideal\n"+
				"Z.B.: 'AOK-RT' f�r die 'AOK Reutlingen'";
				JOptionPane.showMessageDialog(null,message);
				tarifGruppe.requestFocus();
			}
		}
		*/
		
	}

}
