package terminKalender;


import hauptFenster.Reha;
import hauptFenster.UIFSplitPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;
import javax.swing.border.SoftBevelBorder;

import org.jdesktop.swingx.JXPanel;

import systemTools.JRtaTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class Zeitfenster extends JDialog implements KeyListener,FocusListener,ActionListener{

	private static final long serialVersionUID = 1L;
	private JXPanel jContentPane = null;  //  @jve:decl-index=0:visual-constraint="37,10"
	private JRtaTextField NamePatient = null;
	private JRtaTextField Rezeptnummer = null;

	private JRtaTextField Dauer = null;
	private JRtaTextField BeginnStunde = null;
	private JRtaTextField BeginnMinute = null;
	private JRtaTextField EndeStunde = null;
	private JRtaTextField EndeMinute = null;
	private JButton Ok = null;
	private JButton Abbruch = null;
	private TerminFenster Eltern = null;
	private JXPanel neuPanel = null;
	private JRadioButton[] rb = {null,null,null};
	private JLabel[] lbl = {null,null,null};
	private int rechenart = 0;
	private UIFSplitPane jSplitLR = null;
	private JXPanel panelRadio = null;
	private int dividerLocLR = 0;
	ButtonGroup jrbg = new ButtonGroup();  //  @jve:decl-index=0:
	/**
	 * @param owner
	 */
	public Zeitfenster(TerminFenster owner) {
		Eltern = owner;

		initialize();
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(299, 77);
		this.setPreferredSize(new Dimension(299,77));
		this.setContentPane(getJContentPane());
		this.setUndecorated(true);
		this.setAlwaysOnTop(true);
		this.WerteSetzen(true);
		NamePatient.setCaretPosition(0);
		this.addKeyListener(this);

	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JXPanel getJContentPane() {
		if (jContentPane == null) {
		     
			
			jContentPane = new JXPanel();
			jContentPane.setBackground(Color.WHITE);
			jContentPane.setBackgroundPainter(Reha.thisClass.compoundPainter.get("Zeitfenster"));
			jContentPane.setBorder(BorderFactory.createLineBorder(Color.BLUE));
			jContentPane.setOpaque(true);
			jContentPane.setLayout(new BorderLayout());

			jContentPane.setPreferredSize(new Dimension(480,80));
			
			panelRadio = new JXPanel(new BorderLayout());
			panelRadio.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
			panelRadio.setBackgroundPainter(Reha.thisClass.compoundPainter.get("Zeitfenster"));
			panelRadio.setPreferredSize(new Dimension(480,80));
			panelRadio.add(radioPan(),BorderLayout.CENTER);
			
			jContentPane.add(grundSplit(), BorderLayout.CENTER);
			jContentPane.addKeyListener(this);
		}
		jSplitLR.setDividerLocation(399);
		return jContentPane;
	}

	private JXPanel radioPan(){
		JXPanel radio = new JXPanel();
		radio.setOpaque(false);
		radio.setLayout( new GridLayout(3,1,0,3) );
		jrbg = new ButtonGroup();
		
		rb[0] = new JRadioButton("rechne Endzeit aus Startzeit + Dauer");
		rb[0].addKeyListener(this);
		rb[0].addActionListener(this);
		rb[0].setOpaque(false);
		rb[0].setName("radio1");
		jrbg.add(rb[0]);
		radio.add(rb[0]);
		
		rb[1] = new JRadioButton("rechne Startzeit aus Endzeit - Dauer");
		rb[1].addKeyListener(this);
		rb[1].addActionListener(this);
		rb[1].setOpaque(false);		
		rb[1].setName("radio2");
		jrbg.add(rb[1]);
		radio.add(rb[1]);
		
		rb[2] = new JRadioButton("rechne Dauer aus Startzeit und Endzeit");
		rb[2].addKeyListener(this);
		rb[2].addActionListener(this);
		rb[2].setOpaque(false);		
		rb[2].setName("radio3");
		jrbg.add(rb[2]);
		radio.add(rb[2]);		

		rb[0].setSelected(true);
		return radio;
	}

	private UIFSplitPane grundSplit(){
		jSplitLR =  UIFSplitPane.createStrippedSplitPane(JSplitPane.HORIZONTAL_SPLIT,
        		neuLayout(),
        		panelRadio); 
			jSplitLR.setOpaque(false);
			jSplitLR.setBackground(Color.WHITE);
			jSplitLR.setDividerSize(7);
			jSplitLR.addPropertyChangeListener(new PropertyChangeListener(){
				
				public void propertyChange(PropertyChangeEvent arg0) {
					dividerLocLR = jSplitLR.getDividerLocation();
					int letzte = ((UIFSplitPane)arg0.getSource()).getLastDividerLocation(); 
					for(int i = 0; i < 1; i++){
						if( (letzte==290) && (dividerLocLR==279)){
							jSplitLR.setDividerLocation(0);
							break;
						}
						if( (letzte==0) && (dividerLocLR==279)){
							jSplitLR.setDividerLocation(290);
							break;
						}
						if( (letzte==0) && (dividerLocLR==0)){
							jSplitLR.setDividerLocation(290);
							break;
						}
						if( (letzte==290) && (dividerLocLR==290)){
							jSplitLR.setDividerLocation(0);
							break;
						}

					}
				}
			});
			
			jSplitLR.setDividerBorderVisible(false);
			jSplitLR.setName("GrundSplitLinksRechts");
			jSplitLR.setOneTouchExpandable(true);
			dividerLocLR = 400;
			jSplitLR.setDividerLocation(400);
			return jSplitLR;
	}
	private JXPanel neuLayout(){
		neuPanel = new JXPanel();  //     1.  2.Min. 3.  4.SS   5.  6.SM   7.     8.ES   9.  10.EM  11.  12. 13.OK  14. 15.Abb 
		FormLayout lay = new FormLayout("5dlu,25dlu,10dlu,15dlu,2dlu,15dlu,10dlu,15dlu,2dlu,15dlu,2dlu,6dlu,30dlu,2dlu,30dlu,p:g",
									"2dlu,p,5dlu,p,2dlu,p");
		CellConstraints cc = new CellConstraints();
		neuPanel.setLayout(lay);
		neuPanel.setBackground(Color.WHITE);
		neuPanel.setOpaque(false);
		
		NamePatient = new JRtaTextField("GROSS",false);
		NamePatient.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
		NamePatient.setName("NamePatient");
		NamePatient.addKeyListener(this);
		neuPanel.add(NamePatient,cc.xyw(2, 2,9));

		Rezeptnummer = new JRtaTextField("GROSS",false);
		Rezeptnummer.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
		Rezeptnummer.setName("Rezeptnummer");
		Rezeptnummer.addKeyListener(this);		
		neuPanel.add(Rezeptnummer,cc.xyw(13, 2,3));

		lbl[0] = new JLabel("Minuten");
		lbl[0].setForeground(Color.WHITE);
		lbl[0].setOpaque(false);
		neuPanel.add(lbl[0],cc.xy(2,4));
		
		lbl[1] = new JLabel("Startzeit");
		lbl[1].setForeground(Color.BLUE);
		lbl[1].setOpaque(false);
		neuPanel.add(lbl[1],cc.xyw(4,4,4));
		
		lbl[2] = new JLabel("Endzeit");
		lbl[2].setForeground(Color.RED);
		lbl[2].setOpaque(false);
		neuPanel.add(lbl[2],cc.xyw(8,4,4));
		
		Dauer = new JRtaTextField("ZAHLEN",true);
		Dauer.setHorizontalAlignment(JFormattedTextField.RIGHT);
		Dauer.setName("Dauer");
		Dauer.addFocusListener(this);
		Dauer.addKeyListener(this);
		neuPanel.add(Dauer,cc.xy(2,6));
		
		BeginnStunde = new JRtaTextField("STUNDEN",true);
		BeginnStunde.setHorizontalAlignment(JFormattedTextField.RIGHT);
		BeginnStunde.setName("BeginnStunde");
		BeginnStunde.addFocusListener(this);
		BeginnStunde.addKeyListener(this);
		
		neuPanel.add(BeginnStunde,cc.xy(4,6));
		
		BeginnMinute = new JRtaTextField("MINUTEN",true);
		BeginnMinute.setName("BeginnMinute");		
		BeginnMinute.addFocusListener(this);
		BeginnMinute.addKeyListener(this);
		neuPanel.add(BeginnMinute,cc.xy(6,6));
		
		EndeStunde = new JRtaTextField("STUNDEN",true);
		EndeStunde.setHorizontalAlignment(JFormattedTextField.RIGHT);
		EndeStunde.setName("EndeStunde");				
		EndeStunde.addFocusListener(this);
		EndeStunde.addKeyListener(this);		
		neuPanel.add(EndeStunde,cc.xy(8,6));
		
		EndeMinute = new JRtaTextField("MINUTEN",true);
		EndeMinute.setName("EndeMinute");		
		EndeMinute.addFocusListener(this);
		EndeMinute.addKeyListener(this);		
		neuPanel.add(EndeMinute,cc.xy(10,6));

		
		Ok = new JButton("Ok");
		Ok.setName("Ok");
		Ok.setMnemonic(KeyEvent.VK_O);
		Ok.addKeyListener(this);		
		Ok.addActionListener(this);
		neuPanel.add(Ok,cc.xy(13,6));

		Abbruch = new JButton("Abbruch");
		Abbruch.setName("Abbruch");		
		Abbruch.setMnemonic(KeyEvent.VK_A);
		Abbruch.addKeyListener(this);
		Abbruch.addActionListener(this);		
		neuPanel.add(Abbruch,cc.xy(15,6));

		return neuPanel;
	}

	private void Beenden(int endewert){
		if (endewert==1){
			String[] srueck = {NamePatient.getText(),
								Rezeptnummer.getText(),
								BeginnStunde.getText()+":"+
								BeginnMinute.getText()+":00",
								Dauer.getText(),
								EndeStunde.getText()+":"+
								EndeMinute.getText()+":00",
								""};
			Eltern.setWerte(srueck);
			this.setVisible(false);
			this.dispose();
		}
		if (endewert==0){
			String[] srueck = {"","","","","",""};
			Eltern.setWerte(srueck);
			this.setVisible(false);
			this.dispose();
		}
	}
	public void WerteSetzen(boolean aufName){
		String[] werte = Eltern.getWerte();
		NamePatient.setText(werte[0]);
		Rezeptnummer.setText(werte[1]);
		Dauer.setText(werte[3]);
		BeginnStunde.setText(werte[2].split(":")[0]);
		BeginnMinute.setText(werte[2].split(":")[1]);
		EndeStunde.setText(werte[4].split(":")[0]);
		EndeMinute.setText(werte[4].split(":")[1]);
		if(aufName){
			NamePatient.requestFocus();	
			NamePatient.setCaretPosition(0);
		}
	}


	
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == 27){
			Beenden(0);
			return;
		}
		if(((JComponent)arg0.getSource()).getName().equals("Ok")){
			if (arg0.getKeyCode()==10){
	    		Beenden(1);
				return;
	    	}
	    	if (arg0.getKeyCode()==20){
	    		Beenden(0);
				return;
	    	}
		}
		if(((JComponent)arg0.getSource()).getName().equals("Abbruch")){
			if (arg0.getKeyCode()==10){
	    		Beenden(0);
				return;
	    	}
	    	if (arg0.getKeyCode()==20){
	    		Beenden(0);
				return;
	    	}
		}
		String name = ((JComponent)arg0.getSource()).getName().trim();
		if(name=="Dauer" || name=="BeginnStunde" || name=="BeginnMinute" 
			|| name=="EndeStunde" || name=="EndeMinute" || name=="Abbruch"
			|| name=="Rezeptnummer" || name=="NamePatient"){
	    	if (arg0.getKeyCode()==20){
	    		Beenden(0);
				return;
	    	}
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
		// TODO Auto-generated method stub
		
	}


	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		for(int i = 0; i< 1;i++){
			if(((JComponent)arg0.getSource()).getName().equals("Dauer")){
				if(rechenart ==0){
					int dauer1 = Integer.parseInt((String) ((JRtaTextField) arg0.getSource()).getText() );
					
					int dauer2 = (int) ZeitFunk.MinutenSeitMitternacht(BeginnStunde.getText()+
												":"+BeginnMinute.getText()+":00");
					//String sEnde = new String();
					String sEnde = ZeitFunk.MinutenZuZeit(dauer1+dauer2);
					EndeStunde.setText(sEnde.split(":")[0]);
					EndeMinute.setText(sEnde.split(":")[1]);
				}
				if(rechenart ==1){
					int dauer1 = Integer.parseInt((String) ((JRtaTextField) arg0.getSource()).getText() );
					
					int dauer2 = (int) ZeitFunk.MinutenSeitMitternacht(EndeStunde.getText()+
												":"+EndeMinute.getText()+":00");
					//String sEnde = new String();
					String sEnde = ZeitFunk.MinutenZuZeit(dauer2-dauer1);
					BeginnStunde.setText(sEnde.split(":")[0]);
					BeginnMinute.setText(sEnde.split(":")[1]);
				}

				break;
			}
			if(((JComponent)arg0.getSource()).getName().equals("BeginnStunde")){
				//String sb = new String();
				String sb = ((JRtaTextField) arg0.getSource()).getText(); 
				if(sb.isEmpty()){
					((JRtaTextField) arg0.getSource()).requestFocus();
					return;
				}
				if(sb.length()==1){
					((JRtaTextField) arg0.getSource()).setText("0"+sb);
				}
				int dauer1 = Integer.parseInt( (String) ((JRtaTextField)Dauer).getText() );
				int dauer2 = (int) ZeitFunk.MinutenSeitMitternacht(BeginnStunde.getText()+
											":"+BeginnMinute.getText()+":00");
				int dauer3 = (int) ZeitFunk.MinutenSeitMitternacht(EndeStunde.getText()+
						":"+EndeMinute.getText()+":00");
				/*************************/
				if(rechenart==0){
					//String sEnde = new String();
					String sEnde = ZeitFunk.MinutenZuZeit(dauer1+dauer2);
					EndeStunde.setText(sEnde.split(":")[0]);
					EndeMinute.setText(sEnde.split(":")[1]);
				}
				if(rechenart==2){
					Dauer.setText(Integer.toString(dauer3-dauer2));
				}
				break;				
			}
			if(((JComponent)arg0.getSource()).getName().equals("BeginnMinute")){
				//String sb = new String();
				String sb = ((JRtaTextField) arg0.getSource()).getText();
				if(sb.isEmpty()){
					((JRtaTextField) arg0.getSource()).requestFocus();
					return;
				}
				if(sb.length()==1){
					((JRtaTextField) arg0.getSource()).setText("0"+sb);
				}
				int dauer1 = Integer.parseInt( (String) ((JRtaTextField)Dauer).getText() );
				int dauer2 = (int) ZeitFunk.MinutenSeitMitternacht(BeginnStunde.getText()+
					":"+BeginnMinute.getText()+":00");
				int dauer3 = (int) ZeitFunk.MinutenSeitMitternacht(EndeStunde.getText()+
						":"+EndeMinute.getText()+":00");
				/*************************/
				if(rechenart==0){
					//String sEnde = new String();
					String sEnde = ZeitFunk.MinutenZuZeit(dauer1+dauer2);
					EndeStunde.setText(sEnde.split(":")[0]);
					EndeMinute.setText(sEnde.split(":")[1]);
				}
				if(rechenart==2){
					Dauer.setText(Integer.toString(dauer3-dauer2));
				}

				break;				
			}
			if(((JComponent)arg0.getSource()).getName().equals("EndeStunde")){
				//String sb = new String();
				String sb = ((JRtaTextField) arg0.getSource()).getText(); 
				if(sb.isEmpty()){
					((JRtaTextField) arg0.getSource()).requestFocus();
					return;
				}
				if(sb.length()==1){
					((JRtaTextField) arg0.getSource()).setText("0"+sb);
				}
				//int dauer1 = new Integer( (String) ((JRtaTextField)Dauer).getText() );
				int dauer1 = (int) ZeitFunk.MinutenSeitMitternacht(EndeStunde.getText()+
						":"+EndeMinute.getText()+":00");
				int dauer2 = (int) ZeitFunk.MinutenSeitMitternacht(BeginnStunde.getText()+
											":"+BeginnMinute.getText()+":00");
				int dauer3 = Integer.parseInt(Dauer.getText().trim());
				if(rechenart==2){
						Dauer.setText(Integer.toString(dauer1-dauer2));
				}
				if(rechenart==1){
					//String sBeginn = new String();
					String sBeginn = ZeitFunk.MinutenZuZeit(dauer1-dauer3);
					BeginnStunde.setText(sBeginn.split(":")[0]);
					BeginnMinute.setText(sBeginn.split(":")[1]);
				}
				
				break;				
			}
			if(((JComponent)arg0.getSource()).getName().equals("EndeMinute")){
				//String sb = new String();
				String sb = ((JRtaTextField) arg0.getSource()).getText(); 
				if(sb.isEmpty()){
					((JRtaTextField) arg0.getSource()).requestFocus();
					return;
				}
				if(sb.length()==1){
					((JRtaTextField) arg0.getSource()).setText("0"+sb);
				}
				//int dauer1 = new Integer( (String) ((JRtaTextField)Dauer).getText() );
				int dauer1 = (int) ZeitFunk.MinutenSeitMitternacht(EndeStunde.getText()+
						":"+EndeMinute.getText()+":00");
				int dauer2 = (int) ZeitFunk.MinutenSeitMitternacht(BeginnStunde.getText()+
											":"+BeginnMinute.getText()+":00");
				int dauer3 = Integer.parseInt(Dauer.getText().trim());
				if(rechenart==1){
					String sBeginn;
					sBeginn = ZeitFunk.MinutenZuZeit(dauer1-dauer3);
					BeginnStunde.setText(sBeginn.split(":")[0]);
					BeginnMinute.setText(sBeginn.split(":")[1]);
				}
				if(rechenart==2){
					Dauer.setText(Integer.toString(dauer1-dauer2));
				}
				dauer3 = Integer.parseInt(Dauer.getText().trim());
				if((dauer1-dauer2) != dauer3){
					WerteSetzen(false);
					Dauer.requestFocus();
					return;
				}

				break;				
			}
			
		}
		
	}



	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if(((JComponent)arg0.getSource()).getName().equals("Ok")){
			int dauer1 = Integer.parseInt( (String) ((JRtaTextField)Dauer).getText() );
			int dauer2 = (int) ZeitFunk.MinutenSeitMitternacht(BeginnStunde.getText()+
										":"+BeginnMinute.getText()+":00");
			int dauer3 = (int) ZeitFunk.MinutenSeitMitternacht(EndeStunde.getText()+
					":"+EndeMinute.getText()+":00");

			if(dauer3 <= dauer2){
				WerteSetzen(false);
				Dauer.requestFocus();
				return;
			}
			if( (dauer3-dauer2) != dauer1){
				String sEnde;
				sEnde = ZeitFunk.MinutenZuZeit(dauer2+dauer1);
				EndeStunde.setText(sEnde.split(":")[0]);
				EndeMinute.setText(sEnde.split(":")[1]);
			}
			
			dauer2 = (int) ZeitFunk.MinutenSeitMitternacht(BeginnStunde.getText()+
										":"+BeginnMinute.getText()+":00");
			dauer3 = (int) ZeitFunk.MinutenSeitMitternacht(EndeStunde.getText()+
					":"+EndeMinute.getText()+":00");
			dauer1 = dauer3-dauer2;
			if (dauer1 != Integer.parseInt(Dauer.getText().trim()) ){
				WerteSetzen(false);
				Dauer.requestFocus();
				return;
			}
			if(dauer1<=0){
				WerteSetzen(false);
				Dauer.requestFocus();
				return;
			}

			
			Beenden(1);
		}
		if(((JComponent)arg0.getSource()).getName().equals("Abbruch")){
			Beenden(0);
		}
		if(((JComponent)arg0.getSource()).getName().equals("radio1")){
			////System.out.println("Rechenart = 0");
			rechenart = 0;
			jSplitLR.setDividerLocation(399);
			Dauer.requestFocus();			
		}
		if(((JComponent)arg0.getSource()).getName().equals("radio2")){
			////System.out.println("Rechenart = 1");
			rechenart = 1;			
			jSplitLR.setDividerLocation(399);
			Dauer.requestFocus();						
		}
		if(((JComponent)arg0.getSource()).getName().equals("radio3")){
			////System.out.println("Rechenart = 2");
			rechenart = 2;			
			jSplitLR.setDividerLocation(399);
			BeginnStunde.requestFocus();				
		}
		
	}
}  //  @jve:decl-index=0:visual-constraint="27,105"
