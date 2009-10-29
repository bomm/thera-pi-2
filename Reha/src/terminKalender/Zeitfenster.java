package terminKalender;


import hauptFenster.UIFSplitPane;

import java.awt.Frame;
import java.awt.BorderLayout;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;
import javax.swing.JWindow;
import javax.swing.JTextField;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LinearGradientPaint;

import javax.swing.border.SoftBevelBorder;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.awt.SystemColor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.border.EtchedBorder;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import systemTools.Colors;
import systemTools.JRtaTextField;

public class Zeitfenster extends JDialog implements KeyListener,FocusListener,ActionListener{

	private static final long serialVersionUID = 1L;
	private JXPanel jContentPane = null;  //  @jve:decl-index=0:visual-constraint="37,10"
	private JXPanel jPanel = null;
	private JRtaTextField NamePatient = null;
	private JRtaTextField Rezeptnummer = null;
	private JXPanel Zeitvorgaben = null;
	private JRtaTextField Dauer = null;
	private JRtaTextField BeginnStunde = null;
	private JRtaTextField BeginnMinute = null;
	private JRtaTextField EndeStunde = null;
	private JRtaTextField EndeMinute = null;
	private JButton Ok = null;
	private JButton Abbruch = null;
	private JXPanel jPanel1 = null;
	private JLabel SchriftDauer = null;
	private JLabel SchriftStartzeit = null;
	private JLabel SchriftEndzeit = null;
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
		//super(owner);
		initialize();
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		//System.out.println("ZeitFenster wird initialisiert");
		this.setSize(299, 77);
		//this.setSize(288, 77);		
		this.setContentPane(getJContentPane());
		this.setUndecorated(true);
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
			Point2D start = new Point2D.Float(0, 0);
		     Point2D end = new Point2D.Float(390,180);
		     //Point2D end = new Point2D.Float(getParent().getParent().getWidth(),getParent().getParent().getHeight());
		     float[] dist = {0.0f, 0.5f};
		     Color[] colors = {Colors.TaskPaneBlau.alpha(1.0f), Color.WHITE};
		     //Color[] colors = {Color.WHITE,getBackground()};
		     LinearGradientPaint p =
		         new LinearGradientPaint(start, end, dist, colors);
		     MattePainter mp = new MattePainter(p);
		     
			
			jContentPane = new JXPanel();
			jContentPane.setBackground(Color.WHITE);
			jContentPane.setBackgroundPainter(new CompoundPainter(mp));
			jContentPane.setBorder(BorderFactory.createLineBorder(Color.BLUE));
			jContentPane.setOpaque(true);
			jContentPane.setLayout(new BorderLayout());
			//jContentPane.setSize(new Dimension(319, 75));
			jContentPane.setPreferredSize(new Dimension(480,80));
			/*
			jContentPane.add(getJPanel(), BorderLayout.NORTH);
			jContentPane.add(getZeitvorgaben(), BorderLayout.SOUTH);
			jContentPane.add(getJPanel1(), BorderLayout.CENTER);
			*/
			
			panelRadio = new JXPanel(new BorderLayout());
			panelRadio.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
			panelRadio.setBackgroundPainter(new CompoundPainter(mp));
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
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	/*
	private JXPanel getJPanel() {
		if (jPanel == null) {
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);
			flowLayout.setVgap(5);
			flowLayout.setHgap(8);
			jPanel = new JXPanel();
			jPanel.setOpaque(false);
			jPanel.addKeyListener(this);
			jPanel.setLayout(flowLayout);
			jPanel.setPreferredSize(new Dimension(0, 30));
			jPanel.add(getNamePatient(), null);
			jPanel.add(getRezeptnummer(), null);
		}
		return jPanel;
	}
	*/
	/**
	 * This method initializes NamePatient	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	/*
	private JRtaTextField getNamePatient() {
		if (NamePatient == null) {
			NamePatient = new JRtaTextField("GROSS",false);
			//NamePatient.setRtaType("GROSS",NamePatient,false);
			NamePatient.setText("");
			NamePatient.setHorizontalAlignment(JTextField.LEADING);
			NamePatient.setPreferredSize(new Dimension(200, 20));
			//NamePatient.setBackground(Color.orange);
			NamePatient.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
		}
		return NamePatient;
	}
	*/
	private UIFSplitPane grundSplit(){
		jSplitLR =  UIFSplitPane.createStrippedSplitPane(JSplitPane.HORIZONTAL_SPLIT,
        		neuLayout(),
        		panelRadio); 
			jSplitLR.setOpaque(false);
			//jSplitLR.setSize(new Dimension(480,80));
			jSplitLR.setBackground(Color.WHITE);
			jSplitLR.setDividerSize(7);
			jSplitLR.addPropertyChangeListener(new PropertyChangeListener(){
				
				public void propertyChange(PropertyChangeEvent arg0) {
					dividerLocLR = jSplitLR.getDividerLocation();
					/*
					if(dividerLocLR == 290){
						jSplitLR.setDividerLocation(0);
					}else{
						jSplitLR.setDividerLocation(291);
					}
					*/
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
					
					int pos = ((UIFSplitPane)arg0.getSource()).getLastDividerLocation();
					//System.out.println( "Letzte Position = " +pos +" Jetzige Position = "+dividerLocLR);
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
		//neuPanel.setPreferredSize(new Dimension(480,80));
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

	/**
	 * This method initializes Rezeptnummer	
	 * 	
	 * @return javax.swing.JTextField	
	 */
/*
	private JRtaTextField getRezeptnummer() {
		if (Rezeptnummer == null) {
			Rezeptnummer = new JRtaTextField("GROSS",false);
			//Rezeptnummer.setRtaType("GROSS",Rezeptnummer,false);			
			Rezeptnummer.setPreferredSize(new Dimension(100, 20));
			Rezeptnummer.setText("");
			Rezeptnummer.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
		}
		return Rezeptnummer;
	}
*/	

	/**
	 * This method initializes Zeitvorgaben	
	 * 	
	 * @return javax.swing.JPanel	
	 */
/*	
	private JXPanel getZeitvorgaben() {
		if (Zeitvorgaben == null) {
			FlowLayout flowLayout1 = new FlowLayout();
			flowLayout1.setAlignment(java.awt.FlowLayout.CENTER);
			Zeitvorgaben = new JXPanel();
			Zeitvorgaben.addKeyListener(this);
			Zeitvorgaben.setLayout(flowLayout1);
			Zeitvorgaben.setPreferredSize(new Dimension(0, 30));
			Zeitvorgaben.add(getDauer(), null);
			Zeitvorgaben.add(getBeginnStunde(), null);
			Zeitvorgaben.add(getBeginnMinute(), null);
			Zeitvorgaben.add(getEndeStunde(), null);
			Zeitvorgaben.add(getEndeMinute(), null);
			Zeitvorgaben.add(getOk(), null);
			Zeitvorgaben.add(getAbbruch(), null);
		}
		return Zeitvorgaben;
	}
*/
	/**
	 * This method initializes Dauer	
	 * 	
	 * @return javax.swing.JTextField	
	 */
/*
	private JRtaTextField getDauer() {
		if (Dauer == null) {
			Dauer = new JRtaTextField("ZAHLEN",true);
			//Dauer.setRtaType("ZAHLEN",Dauer,true);			
			Dauer.setPreferredSize(new Dimension(38, 20));
			Dauer.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
			Dauer.setFont(new Font("Dialog", Font.PLAIN, 12));
						Dauer.addFocusListener(new java.awt.event.FocusAdapter() { 
							public void focusLost(java.awt.event.FocusEvent e) {
								int dauer1 = new Integer((String) ((JRtaTextField) e.getSource()).getText() );
								
								int dauer2 = (int) zeitFunk.MinutenSeitMitternacht(BeginnStunde.getText()+
															":"+BeginnMinute.getText()+":00");
								String sEnde = new String();
								sEnde = zeitFunk.MinutenZuZeit(dauer1+dauer2);
								EndeStunde.setText(sEnde.split(":")[0]);
								EndeMinute.setText(sEnde.split(":")[1]);
								//System.out.println("focusLost()"); // TODO Auto-generated Event stub focusLost()
							}
						});
		}
		return Dauer;
	}
*/
	/**
	 * This method initializes BeginnStunde	
	 * 	
	 * @return javax.swing.JTextField	
	 */
/*
	private JRtaTextField getBeginnStunde() {
		if (BeginnStunde == null) {
			BeginnStunde = new JRtaTextField("STUNDEN",true);
			//BeginnStunde.setRtaType("STUNDEN",BeginnStunde,true);			
			BeginnStunde.setPreferredSize(new Dimension(38, 20));
			BeginnStunde.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
			BeginnStunde.addFocusListener(new java.awt.event.FocusAdapter() { 
				public void focusLost(java.awt.event.FocusEvent e) {
					String sb = new String();
					sb = ((JRtaTextField) e.getSource()).getText(); 
					if(sb.isEmpty()){
						((JRtaTextField) e.getSource()).requestFocus();
						return;
					}
					if(sb.length()==1){
						((JRtaTextField) e.getSource()).setText("0"+sb);
					}
					int dauer1 = new Integer( (String) ((JRtaTextField)Dauer).getText() );
					int dauer2 = (int) zeitFunk.MinutenSeitMitternacht(BeginnStunde.getText()+
												":"+BeginnMinute.getText()+":00");
					String sEnde = new String();
					sEnde = zeitFunk.MinutenZuZeit(dauer1+dauer2);
					EndeStunde.setText(sEnde.split(":")[0]);
					EndeMinute.setText(sEnde.split(":")[1]);
				}
			});

		}
		return BeginnStunde;
	}
*/
	/**
	 * This method initializes BeginnMinute	
	 * 	
	 * @return javax.swing.JTextField	
	 */
/*	
	private JRtaTextField getBeginnMinute() {
		if (BeginnMinute == null) {
			BeginnMinute = new JRtaTextField("MINUTEN",true);
			//BeginnMinute.setRtaType("MINUTEN",BeginnMinute,true);			
			BeginnMinute.setPreferredSize(new Dimension(38, 20));
			BeginnMinute.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
			BeginnMinute.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent e) {
				String sb = new String();
				sb = ((JRtaTextField) e.getSource()).getText();
				if(sb.isEmpty()){
					((JRtaTextField) e.getSource()).requestFocus();
					return;
				}
				if(sb.length()==1){
					((JRtaTextField) e.getSource()).setText("0"+sb);
				}
				int dauer1 = new Integer( (String) ((JRtaTextField)Dauer).getText() );
				int dauer2 = (int) zeitFunk.MinutenSeitMitternacht(BeginnStunde.getText()+
											":"+BeginnMinute.getText()+":00");
				String sEnde = new String();
				sEnde = zeitFunk.MinutenZuZeit(dauer1+dauer2);
				EndeStunde.setText(sEnde.split(":")[0]);
				EndeMinute.setText(sEnde.split(":")[1]);
				//System.out.println("focusLost()"); // TODO Auto-generated Event stub focusLost()
				
			}
		});

		}
		return BeginnMinute;
	}
*/
	/**
	 * This method initializes EndeStunde	
	 * 	
	 * @return javax.swing.JTextField	
	 */
/*
	private JRtaTextField getEndeStunde() {
		if (EndeStunde == null) {
			EndeStunde = new JRtaTextField("STUNDEN",true);
			//EndeStunde.setRtaType("STUNDEN",EndeStunde,true);			
			EndeStunde.setPreferredSize(new Dimension(38, 20));
			EndeStunde.setAlignmentX(0.5F);
			EndeStunde.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
			EndeStunde.addFocusListener(new java.awt.event.FocusAdapter() { 
				public void focusLost(java.awt.event.FocusEvent e) {
					String sb = new String();
					sb = ((JRtaTextField) e.getSource()).getText(); 
					if(sb.isEmpty()){
						((JRtaTextField) e.getSource()).requestFocus();
						return;
					}
					if(sb.length()==1){
						((JRtaTextField) e.getSource()).setText("0"+sb);
					}
					//int dauer1 = new Integer( (String) ((JRtaTextField)Dauer).getText() );
					int dauer1 = (int) zeitFunk.MinutenSeitMitternacht(EndeStunde.getText()+
							":"+EndeMinute.getText()+":00");
					int dauer2 = (int) zeitFunk.MinutenSeitMitternacht(BeginnStunde.getText()+
												":"+BeginnMinute.getText()+":00");
					if (dauer1 <= dauer2){
						//WerteSetzen();
						//EndeStunde.setText(BeginnStunde.getText());
						//EndeMinute.setText(BeginnMinute.getText());
						//EndeStunde.requestFocus();
						//return;
					}
					//String sEnde = new String();
					//sEnde = zeitFunk.MinutenZuZeit(dauer1-dauer2);
					System.out.println("nach Ende Stunde");
					//Dauer.setText((String) new Integer(dauer1-dauer2).toString());
				}
			});
			
		}
		return EndeStunde;
	}
*/
	/**
	 * This method initializes EndeMinute	
	 * 	
	 * @return javax.swing.JTextField	
	 */
/*
	private JRtaTextField getEndeMinute() {
		if (EndeMinute == null) {
			EndeMinute = new JRtaTextField("MINUTEN",true);
			//EndeMinute.setRtaType("MINUTEN",EndeMinute,true);			

			EndeMinute.setPreferredSize(new Dimension(38, 20));
			EndeMinute.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
			EndeMinute.addFocusListener(new java.awt.event.FocusAdapter() { 
				public void focusLost(java.awt.event.FocusEvent e) {
					String sb = new String();
					sb = ((JRtaTextField) e.getSource()).getText(); 
					if(sb.isEmpty()){
						((JRtaTextField) e.getSource()).requestFocus();
						return;
					}
					if(sb.length()==1){
						((JRtaTextField) e.getSource()).setText("0"+sb);
					}
					//int dauer1 = new Integer( (String) ((JRtaTextField)Dauer).getText() );
					int dauer1 = (int) zeitFunk.MinutenSeitMitternacht(EndeStunde.getText()+
							":"+EndeMinute.getText()+":00");
					int dauer2 = (int) zeitFunk.MinutenSeitMitternacht(BeginnStunde.getText()+
												":"+BeginnMinute.getText()+":00");
					if (dauer1 <= dauer2){
						WerteSetzen();
						//EndeStunde.setText(BeginnStunde.getText());
						//EndeMinute.setText(BeginnMinute.getText());
						//EndeStunde.requestFocus();
						return;
					}else{
					//String sEnde = new String();
					//sEnde = zeitFunk.MinutenZuZeit(dauer1-dauer2);
					sb = zeitFunk.MinutenZuZeit(dauer1 - new Integer(Dauer.getText()));
					BeginnStunde.setText(sb.split(":")[0]);
					BeginnMinute.setText(sb.split(":")[1]);
					//Dauer.setText((String) new Integer(dauer1-dauer2).toString());
					}
				}
			});

		}
		return EndeMinute;
	}
*/
	/**
	 * This method initializes Ok	
	 * 	
	 * @return javax.swing.JButton	
	 */
/*
	private JButton getOk() {
		if (Ok == null) {
			Ok = new JButton();
			Ok.setPreferredSize(new Dimension(40, 20));
			Ok.setText("OK");
			Ok.setFont(new Font("Arial", Font.PLAIN, 10));
			Ok.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
			Ok.setMnemonic(KeyEvent.VK_O);
			Ok.setName("");
			Ok.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Beenden(1);
				}
			});
			Ok.addKeyListener(new KeyAdapter() {
	            public void keyPressed(KeyEvent event) {
	            	if (event.getKeyCode()==10){
	            		Beenden(1);
	            	}
	            	if (event.getKeyCode()==20){
	            		Beenden(0);
	            	}
	            }
	        });
			
		}
		return Ok;
	}
*/
	/**
	 * This method initializes Abbruch	
	 * 	
	 * @return javax.swing.JButton	
	 */
/*
	private JButton getAbbruch() {
		if (Abbruch == null) {
			Abbruch = new JButton();
			Abbruch.setPreferredSize(new Dimension(50, 20));
			Abbruch.setFont(new Font("Arial", Font.BOLD, 10));
			Abbruch.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
			Abbruch.setMnemonic(KeyEvent.VK_A);
			Abbruch.setText("Abbruch");
			Abbruch.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Beenden(0);
				}
			});
		}
		return Abbruch;
	}
*/
	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
/*	
	private JXPanel getJPanel1() {
		if (jPanel1 == null) {
			SchriftEndzeit = new JLabel();
			SchriftEndzeit.setText("Endzeit");
			SchriftEndzeit.setFont(new Font("Tahoma", Font.PLAIN, 11));
			SchriftEndzeit.setForeground(Color.red);
			SchriftEndzeit.setPreferredSize(new Dimension(80, 15));
			SchriftStartzeit = new JLabel();
			SchriftStartzeit.setText("Startzeit");
			SchriftStartzeit.setPreferredSize(new Dimension(85, 15));
			SchriftStartzeit.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
			SchriftStartzeit.setForeground(SystemColor.desktop);
			SchriftStartzeit.setFont(new Font("Tahoma", Font.PLAIN, 11));
			FlowLayout flowLayout2 = new FlowLayout();
			flowLayout2.setAlignment(java.awt.FlowLayout.LEFT);
			flowLayout2.setHgap(5);
			SchriftDauer = new JLabel();
			SchriftDauer.setText("Dauer");
			SchriftDauer.setPreferredSize(new Dimension(40, 15));
			SchriftDauer.setForeground(SystemColor.desktop);
			SchriftDauer.setBackground(new Color(238, 238, 238));
			SchriftDauer.setFont(new Font("Tahoma", Font.PLAIN, 11));
			jPanel1 = new JXPanel();
			jPanel1.setMinimumSize(new Dimension(40, 20));
			jPanel1.setLayout(flowLayout2);
			jPanel1.setName("Überschriften");
			jPanel1.setPreferredSize(new Dimension(40, 15));
			jPanel1.add(SchriftDauer, null);
			jPanel1.add(SchriftStartzeit, null);
			jPanel1.add(SchriftEndzeit, null);
		}
		return jPanel1;
	}
*/
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
		//System.out.println("KeyCode = "+arg0.getKeyCode());
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
					int dauer1 = new Integer((String) ((JRtaTextField) arg0.getSource()).getText() );
					
					int dauer2 = (int) zeitFunk.MinutenSeitMitternacht(BeginnStunde.getText()+
												":"+BeginnMinute.getText()+":00");
					String sEnde = new String();
					sEnde = zeitFunk.MinutenZuZeit(dauer1+dauer2);
					EndeStunde.setText(sEnde.split(":")[0]);
					EndeMinute.setText(sEnde.split(":")[1]);
				}
				if(rechenart ==1){
					int dauer1 = new Integer((String) ((JRtaTextField) arg0.getSource()).getText() );
					
					int dauer2 = (int) zeitFunk.MinutenSeitMitternacht(EndeStunde.getText()+
												":"+EndeMinute.getText()+":00");
					String sEnde = new String();
					sEnde = zeitFunk.MinutenZuZeit(dauer2-dauer1);
					BeginnStunde.setText(sEnde.split(":")[0]);
					BeginnMinute.setText(sEnde.split(":")[1]);
				}

				break;
			}
			if(((JComponent)arg0.getSource()).getName().equals("BeginnStunde")){
				String sb = new String();
				sb = ((JRtaTextField) arg0.getSource()).getText(); 
				if(sb.isEmpty()){
					((JRtaTextField) arg0.getSource()).requestFocus();
					return;
				}
				if(sb.length()==1){
					((JRtaTextField) arg0.getSource()).setText("0"+sb);
				}
				int dauer1 = new Integer( (String) ((JRtaTextField)Dauer).getText() );
				int dauer2 = (int) zeitFunk.MinutenSeitMitternacht(BeginnStunde.getText()+
											":"+BeginnMinute.getText()+":00");
				int dauer3 = (int) zeitFunk.MinutenSeitMitternacht(EndeStunde.getText()+
						":"+EndeMinute.getText()+":00");
				/*************************/
				if(rechenart==0){
					String sEnde = new String();
					sEnde = zeitFunk.MinutenZuZeit(dauer1+dauer2);
					EndeStunde.setText(sEnde.split(":")[0]);
					EndeMinute.setText(sEnde.split(":")[1]);
				}
				if(rechenart==2){
					Dauer.setText(new Integer(dauer3-dauer2).toString());
				}
				break;				
			}
			if(((JComponent)arg0.getSource()).getName().equals("BeginnMinute")){
				String sb = new String();
				sb = ((JRtaTextField) arg0.getSource()).getText();
				if(sb.isEmpty()){
					((JRtaTextField) arg0.getSource()).requestFocus();
					return;
				}
				if(sb.length()==1){
					((JRtaTextField) arg0.getSource()).setText("0"+sb);
				}
				int dauer1 = new Integer( (String) ((JRtaTextField)Dauer).getText() );
				int dauer2 = (int) zeitFunk.MinutenSeitMitternacht(BeginnStunde.getText()+
					":"+BeginnMinute.getText()+":00");
				int dauer3 = (int) zeitFunk.MinutenSeitMitternacht(EndeStunde.getText()+
						":"+EndeMinute.getText()+":00");
				/*************************/
				if(rechenart==0){
					String sEnde = new String();
					sEnde = zeitFunk.MinutenZuZeit(dauer1+dauer2);
					EndeStunde.setText(sEnde.split(":")[0]);
					EndeMinute.setText(sEnde.split(":")[1]);
				}
				if(rechenart==2){
					Dauer.setText(new Integer(dauer3-dauer2).toString());
				}

				break;				
			}
			if(((JComponent)arg0.getSource()).getName().equals("EndeStunde")){
				String sb = new String();
				sb = ((JRtaTextField) arg0.getSource()).getText(); 
				if(sb.isEmpty()){
					((JRtaTextField) arg0.getSource()).requestFocus();
					return;
				}
				if(sb.length()==1){
					((JRtaTextField) arg0.getSource()).setText("0"+sb);
				}
				//int dauer1 = new Integer( (String) ((JRtaTextField)Dauer).getText() );
				int dauer1 = (int) zeitFunk.MinutenSeitMitternacht(EndeStunde.getText()+
						":"+EndeMinute.getText()+":00");
				int dauer2 = (int) zeitFunk.MinutenSeitMitternacht(BeginnStunde.getText()+
											":"+BeginnMinute.getText()+":00");
				int dauer3 = Integer.parseInt(Dauer.getText().trim());
				if(rechenart==2){
						Dauer.setText(new Integer(dauer1-dauer2).toString());
				}
				if(rechenart==1){
					String sBeginn = new String();
					sBeginn = zeitFunk.MinutenZuZeit(dauer1-dauer3);
					BeginnStunde.setText(sBeginn.split(":")[0]);
					BeginnMinute.setText(sBeginn.split(":")[1]);
				}
				
				break;				
			}
			if(((JComponent)arg0.getSource()).getName().equals("EndeMinute")){
				String sb = new String();
				sb = ((JRtaTextField) arg0.getSource()).getText(); 
				if(sb.isEmpty()){
					((JRtaTextField) arg0.getSource()).requestFocus();
					return;
				}
				if(sb.length()==1){
					((JRtaTextField) arg0.getSource()).setText("0"+sb);
				}
				//int dauer1 = new Integer( (String) ((JRtaTextField)Dauer).getText() );
				int dauer1 = (int) zeitFunk.MinutenSeitMitternacht(EndeStunde.getText()+
						":"+EndeMinute.getText()+":00");
				int dauer2 = (int) zeitFunk.MinutenSeitMitternacht(BeginnStunde.getText()+
											":"+BeginnMinute.getText()+":00");
				int dauer3 = Integer.parseInt(Dauer.getText().trim());
				if(rechenart==1){
					String sBeginn;
					sBeginn = zeitFunk.MinutenZuZeit(dauer1-dauer3);
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
			int dauer1 = new Integer( (String) ((JRtaTextField)Dauer).getText() );
			int dauer2 = (int) zeitFunk.MinutenSeitMitternacht(BeginnStunde.getText()+
										":"+BeginnMinute.getText()+":00");
			int dauer3 = (int) zeitFunk.MinutenSeitMitternacht(EndeStunde.getText()+
					":"+EndeMinute.getText()+":00");

			if(dauer3 <= dauer2){
				WerteSetzen(false);
				Dauer.requestFocus();
				return;
			}
			if( (dauer3-dauer2) != dauer1){
				String sEnde;
				sEnde = zeitFunk.MinutenZuZeit(dauer2+dauer1);
				EndeStunde.setText(sEnde.split(":")[0]);
				EndeMinute.setText(sEnde.split(":")[1]);
			}
			
			dauer2 = (int) zeitFunk.MinutenSeitMitternacht(BeginnStunde.getText()+
										":"+BeginnMinute.getText()+":00");
			dauer3 = (int) zeitFunk.MinutenSeitMitternacht(EndeStunde.getText()+
					":"+EndeMinute.getText()+":00");
			dauer1 = dauer3-dauer2;
			if (dauer1 != new Integer(Dauer.getText().trim()) ){
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
			//System.out.println("Rechenart = 0");
			rechenart = 0;
			jSplitLR.setDividerLocation(399);
			Dauer.requestFocus();			
		}
		if(((JComponent)arg0.getSource()).getName().equals("radio2")){
			//System.out.println("Rechenart = 1");
			rechenart = 1;			
			jSplitLR.setDividerLocation(399);
			Dauer.requestFocus();						
		}
		if(((JComponent)arg0.getSource()).getName().equals("radio3")){
			//System.out.println("Rechenart = 2");
			rechenart = 2;			
			jSplitLR.setDividerLocation(399);
			BeginnStunde.requestFocus();				
		}
		
	}
}  //  @jve:decl-index=0:visual-constraint="27,105"
