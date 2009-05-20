package terminKalender;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.border.DropShadowBorder;


public class TerminLayout extends JScrollPane{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Reha eltern;
	private boolean setOben;
	
	private JXPanel GrundFlaeche = null;
	private JXPanel ComboFlaeche = null;	
	private JXPanel TerminFlaeche = null;
/*	
	private JComboBox jComboBox0 = null;
	private JComboBox jComboBox1 = null;
	private JComboBox jComboBox2 = null;
	private JComboBox jComboBox3 = null;
	private JComboBox jComboBox4 = null;
	private JComboBox jComboBox5 = null;
	private JComboBox jComboBox6 = null;
	
	private JCheckBox jCheckBox0 = null;
	private JCheckBox jCheckBox1 = null;
	private JCheckBox jCheckBox2 = null;
	private JCheckBox jCheckBox3 = null;
	private JCheckBox jCheckBox4 = null;
	private JCheckBox jCheckBox5 = null;
	private JCheckBox jCheckBox6 = null;
	
	private kalenderPanel jPanelT0 = null;
	private kalenderPanel jPanelT1 = null;
	private kalenderPanel jPanelT2 = null;
	private kalenderPanel jPanelT3 = null;
	private kalenderPanel jPanelT4 = null;
	private kalenderPanel jPanelT5 = null;
	private kalenderPanel jPanelT6 = null;
*/
	private kalenderPanel[] oSpalten = {null,null,null,null,null,null,null};	
	private JComboBox[] oCombo = {null,null,null,null,null,null,null};
	private JCheckBox[] oCheck = {null,null,null,null,null,null,null};	
	

	private int ansicht = 0; // 0=Normalansicht 1=Wochenansicht
	private int[] belegung = {-1,-1,-1,-1,-1,-1,-1}; //Welcher Kollege(Nr. ist in der jeweiligen Spalte sichtbar
	private int wochenbelegung = 0; // nimmt die KollegenNr auf dessen Woche angezeigt wird
	
	public TerminLayout(Reha eltern, boolean setOben) {
		super((JComponent)Reha.thisFrame.getGlassPane());
		//this.setUndecorated(true);
		this.eltern = eltern;
		this.setOben = setOben;
		//this.setBorder(null);
		//this.setViewportBorder(null);
		
		JXPanel viewPanel = new JXPanel(new BorderLayout());
/**** Terminspalten zuweisen ********************************/
/*		oSpalten[0]		= jPanelT0;
		oSpalten[1] 	= jPanelT1;
		oSpalten[2] 	= jPanelT2;
		oSpalten[3] 	= jPanelT3;
		oSpalten[4] 	= jPanelT4;
		oSpalten[5] 	= jPanelT5;
		oSpalten[6] 	= jPanelT6;*/
/**** ComboBoxen zuweisen ***********************************/
/*		oCombo[0] 		= jComboBox0;			
		oCombo[1] 		= jComboBox1;
		oCombo[2] 		= jComboBox2;			
		oCombo[3] 		= jComboBox3;
		oCombo[4] 		= jComboBox4;			
		oCombo[5] 		= jComboBox5;
		oCombo[6] 		= jComboBox6;*/
/**** Checkboxen innerhalb des ComboPanels zuweisen *********/
/*		oCheck[0]		= jCheckBox0;			
		oCheck[1]		= jCheckBox1;
		oCheck[2]		= jCheckBox2;
		oCheck[3]		= jCheckBox3;
		oCheck[4]		= jCheckBox4;
		oCheck[5]		= jCheckBox5;
		oCheck[6]		= jCheckBox6;*/		
		System.out.println("Anzahl Kollegen in Combos = "+ParameterLaden.vKKollegen.size());
		GrundFlaeche = getGrundFlaeche();
		viewPanel.add(GrundFlaeche,BorderLayout.CENTER);
		viewPanel.revalidate();
		setCombos();
		this.setViewportView(viewPanel);	


		//this.setVisible(true);
		//return;
	}
	
	private JXPanel	getGrundFlaeche(){
		if(GrundFlaeche == null){
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.anchor = GridBagConstraints.SOUTH;
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.weighty = 0.0;
			gridBagConstraints1.ipady = 0;
			gridBagConstraints1.gridwidth = 0;
			gridBagConstraints1.gridheight = 0;
			gridBagConstraints1.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints1.ipadx = 0;
			gridBagConstraints1.gridy = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = GridBagConstraints.NORTH;
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.gridwidth = 0;
			gridBagConstraints.gridheight = 0;
			gridBagConstraints.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints.ipadx = 0;
			gridBagConstraints.ipady = 0;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.gridy = 0;
			GrundFlaeche = new JXPanel();
			GrundFlaeche.setDoubleBuffered(true);
			GrundFlaeche.setBorder(null);
			GrundFlaeche.setLayout(new GridBagLayout());
			
			GrundFlaeche.add(getComboFlaeche(),gridBagConstraints);
			//GrundFlaeche.add(getTerminFlaeche(),gridBagConstraints1);			
		}
		return GrundFlaeche;
	}

	private JXPanel getComboFlaeche(){
		if(ComboFlaeche==null){
			DropShadowBorder dropShadow = new DropShadowBorder(Color.BLACK, 5, 1, 3, true, true, false, true);
			ComboFlaeche = new JXPanel();
			ComboFlaeche.setDoubleBuffered(true);
			ComboFlaeche.setBorder(null);
			BoxLayout boxlay = new BoxLayout(ComboFlaeche, BoxLayout.X_AXIS);
			ComboFlaeche.setLayout(boxlay);
			JXPanel cb = null;
			for(int i = 0;i<7;i++){
				cb = new JXPanel(new BorderLayout());
				cb.setBorder(dropShadow);
				oCombo[i] = new JComboBox();
				oCombo[i].addItem("Text");
				oCheck[i] = new JCheckBox();
				cb.add(oCheck[i],BorderLayout.WEST);
				cb.add(oCombo[i],BorderLayout.CENTER);
				ComboFlaeche.add(cb);
			}
			ComboFlaeche.revalidate();			
		}	
		return ComboFlaeche;
	}
/***
 * Jetzt die Listener für die Combos installieren
 * 
 */
	private void ComboListenerInit(final int welche){
		System.out.println("Anzahl Kollegen von Combo "+welche+" = "+ParameterLaden.vKKollegen.size());
		oCombo[welche].setPopupVisible(false);	
		oCombo[welche].addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent e) {
				if (e.getKeyCode()==33 || e.getKeyCode()==34){
					//ComboTastenAuswerten(e);
				}else{
					oCombo[welche].setPopupVisible(false);
					if (e.getKeyCode()==123){
						oSpalten[0].requestFocus();
						//SetAufruf();
					}else{
						//ComboTastenAuswerten(e);
					}
				}
				
			}
		});
		oCombo[welche].addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				int wahl = ParameterLaden.suchen((String)oCombo[welche].getSelectedItem());							
				if (ansicht == 0){
					belegung[welche] = ParameterLaden.vKKollegen.get(wahl).Reihe -1 ;
					//oSpalten[welche].datenZeichnen(vTerm,belegung[welche]);
					oSpalten[welche].requestFocus();
					if (welche==0){
						wochenbelegung = ParameterLaden.vKKollegen.get(wahl).Reihe ;
					}
				}else{
					if (welche==0){
						wochenbelegung = ParameterLaden.vKKollegen.get(wahl).Reihe ;
						//AnsichtStatement(ansicht,aktuellerTag);
					}	
				}
			}
		});
		}
/****
 * die Comboboxen mit Werden füllen
 * 	
 */
	public void setCombos(){
		int von = 0;
		int bis = ParameterLaden.vKKollegen.size();
		for(von=0; von < bis; von++){
			oCombo[0].addItem(ParameterLaden.vKKollegen.get(von).Matchcode);
			oCombo[1].addItem(ParameterLaden.vKKollegen.get(von).Matchcode);
			oCombo[2].addItem(ParameterLaden.vKKollegen.get(von).Matchcode);			
			oCombo[3].addItem(ParameterLaden.vKKollegen.get(von).Matchcode);
			oCombo[4].addItem(ParameterLaden.vKKollegen.get(von).Matchcode);
			oCombo[5].addItem(ParameterLaden.vKKollegen.get(von).Matchcode);			
			oCombo[6].addItem(ParameterLaden.vKKollegen.get(von).Matchcode);
		}
		/*************
		jComboBox0.setFont(new Font("Dialog", Font.PLAIN, 11));
		jComboBox1.setFont(new Font("Dialog", Font.PLAIN, 11));
		jComboBox2.setFont(new Font("Dialog", Font.PLAIN, 11));
		jComboBox3.setFont(new Font("Dialog", Font.PLAIN, 11));
		jComboBox4.setFont(new Font("Dialog", Font.PLAIN, 11));
		jComboBox5.setFont(new Font("Dialog", Font.PLAIN, 11));
		jComboBox6.setFont(new Font("Dialog", Font.PLAIN, 11));
		**************/		
		oCombo[0].setMaximumRowCount( 35 ); oCombo[0].setSelectedItem( "./." ); 
		oCombo[1].setMaximumRowCount( 35 ); oCombo[1].setSelectedItem( "./." );			
		oCombo[2].setMaximumRowCount( 35 );	oCombo[2].setSelectedItem( "./." );
		oCombo[3].setMaximumRowCount( 35 );	oCombo[3].setSelectedItem( "./." );
		oCombo[4].setMaximumRowCount( 35 );	oCombo[4].setSelectedItem( "./." );		
		oCombo[5].setMaximumRowCount( 35 );	oCombo[5].setSelectedItem( "./." );
		oCombo[6].setMaximumRowCount( 35 );	oCombo[6].setSelectedItem( "./." );
		/**jetzt noch die Listener initialisieren**/
		for(int i = 0; i < 7; i++){
			ComboListenerInit(i);
		}
	}
	
/****
 * 	
 * @return
 */
	private JXPanel	getTerminFlaeche(){
		if(TerminFlaeche==null){
			TerminFlaeche = new JXPanel();
			BoxLayout boxlay = new BoxLayout(TerminFlaeche,BoxLayout.X_AXIS);
			TerminFlaeche.setLayout(boxlay);
			TerminFlaeche.setBorder(null);
			DropShadowBorder dropShadow = new DropShadowBorder(Color.BLACK, 5, 1, 3, true, true, true, true);
			JXPanel cb = null;
			for(int i = 0;i<7;i++){
				cb = new JXPanel(new BorderLayout());
				oSpalten[i] =  new kalenderPanel();
				oSpalten[i].setDoubleBuffered(true);
				//oSpalten[i].setBorder(dropShadow);
				//oSpalten[i].setBackgroundPainter(Reha.RehaPainter[0]);
				cb.add(oSpalten[i],BorderLayout.CENTER);
				TerminFlaeche.add(cb);
			}
			TerminFlaeche.revalidate();
		}
		return TerminFlaeche;
	}

/****
 * 
 * 
 * 
 * 
 * 	
 */
}
