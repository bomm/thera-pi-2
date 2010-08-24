package systemEinstellungen;

import hauptFenster.Reha;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import systemTools.JRtaTextField;
import terminKalender.ParameterLaden;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SysUtilBehandlerset extends JXPanel implements KeyListener, ActionListener {
	JButton knopf1 = null;
	JButton knopf2 = null;
	JButton knopf3 = null;
	JButton knopf4 = null;
	JButton knopf5 = null;

	
	JComboBox SetName = null;
	JComboBox Spalte1 = null;
	JComboBox Spalte2 = null;
	JComboBox Spalte3 = null;
	JComboBox Spalte4 = null;
	JComboBox Spalte5 = null;
	JComboBox Spalte6 = null;
	JComboBox Spalte7 = null;
	JComboBox[] Spalten = {null,null,null,null,null,null,null};

	JRtaTextField SetNeu = null;
	String[] kollegen = null;
	String[][] teilnehmer = null;
	String[] fach = null;

	private boolean lneu = false;
	private boolean lspeichern = false;
	private int speichernKalZeile = 0;
	
	JScrollPane jscroll = null;
	
	SysUtilBehandlerset(){
		super(new GridLayout(1,1));
		//System.out.println("Aufruf SysUtilBehandlerset");
		this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 0));
		/****/
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("SystemInit"));
		/****/
		jscroll = new JScrollPane();
		jscroll.setOpaque(false);
		jscroll.getViewport().setOpaque(false);
		jscroll.setBorder(null);
		jscroll.getVerticalScrollBar().setUnitIncrement(15);



		macheKollegen();
		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				jscroll.setViewportView(getSetSeite());
				jscroll.validate();
				add(jscroll);
				validate();
				//add(getSetSeite());
       	  	}
		});


		//this.setBackground(Color.YELLOW);
		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				comboFuellen(true,0);
       	  	}
		});

		return;
	}
	private void macheKollegen(){
		int von = 0;
		int bis = ParameterLaden.vKKollegen.size();
		kollegen = new String[bis];
		for(von=0; von < bis; von++){
			kollegen[von] = ParameterLaden.getMatchcode(von);
		}	
	}
	
	/************** Beginn der Methode f�r die Objekterstellung und -platzierung *********/
	private JPanel getSetSeite(){
        //                                      1.            2.    3.    4.     5.     6.    7.      8.     9.
		FormLayout lay = new FormLayout("right:max(60dlu;p), 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu",
       //1.    2. 3.   4.   5.   6.     7.    8. 9.  10.  11. 12. 13.  14.  15. 16.  17. 18.  19.   20.    21.   22.   23.
		"p, 2dlu, p, 10dlu,10dlu,10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p,  10dlu ,10dlu, 10dlu, p");
		
		PanelBuilder builder = new PanelBuilder(lay);
		builder.setDefaultDialogBorder();
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();
		
		
		
		builder.addLabel("bestehendes Set auswählen", cc.xy(1,1));
		SetName = new JComboBox();
		SetName.addActionListener(this);
		SetName.setActionCommand("comboaktion");
		builder.add(SetName, cc.xyw(3,1,3));
		
		builder.addLabel("Name des Sets", cc.xy(1,3));
		SetNeu = new JRtaTextField("nix",true);
		builder.add(SetNeu, cc.xyw(3,3,3));
		
		builder.addSeparator("Kalenderspalten zuordnen", cc.xyw(1,5,9));
		
		builder.addLabel("Spalte 1", cc.xy(1,7));
		Spalte1 = new JComboBox(kollegen);
		Spalten[0] = Spalte1;
		builder.add(Spalte1, cc.xyw(3,7,3));
		
		builder.addLabel("Spalte 2", cc.xy(1,9));
		Spalte2 = new JComboBox(kollegen);
		Spalten[1] = Spalte2;
		builder.add(Spalte2, cc.xyw(3,9,3));
		
		builder.addLabel("Spalte 3", cc.xy(1,11));
		Spalte3 = new JComboBox(kollegen);
		Spalten[2] = Spalte3;
		builder.add(Spalte3, cc.xyw(3,11,3));
		
		builder.addLabel("Spalte 4", cc.xy(1,13));
		Spalte4 = new JComboBox(kollegen);
		Spalten[3] = Spalte4;
		builder.add(Spalte4, cc.xyw(3,13,3));
		
		builder.addLabel("Spalte 5", cc.xy(1,15));
		Spalte5 = new JComboBox(kollegen);
		Spalten[4] = Spalte5;
		builder.add(Spalte5, cc.xyw(3,15,3));
		
		builder.addLabel("Spalte 6", cc.xy(1,17));
		Spalte6 = new JComboBox(kollegen);
		Spalten[5] = Spalte6;
		builder.add(Spalte6, cc.xyw(3,17,3));
		
		builder.addLabel("Spalte 7", cc.xy(1,19));
		Spalte7 = new JComboBox(kollegen);
		Spalten[6] = Spalte7;
		builder.add(Spalte7, cc.xyw(3,19,3));
		
		builder.addSeparator("", cc.xyw(1, 21, 9));
		
		// buttons
		knopf1 = new JButton("neu"); 
		knopf1.setPreferredSize(new Dimension(70, 20));
		knopf1.addActionListener(this);
		knopf1.setActionCommand("neu");
		knopf1.addKeyListener(this);

		knopf2 = new JButton("löschen");
		knopf2.setPreferredSize(new Dimension(70, 20));
		knopf2.addActionListener(this);
		knopf2.setActionCommand("loeschen");
		knopf2.addKeyListener(this);
		
		knopf3 = new JButton("ändern");
		knopf3.setPreferredSize(new Dimension(70, 20));
		knopf3.addActionListener(this);
		knopf3.setActionCommand("aendern");
		knopf3.addKeyListener(this);
		
		knopf4 = new JButton("speichern");
		knopf4.setPreferredSize(new Dimension(70, 20));
		knopf4.addActionListener(this);		
		knopf4.setActionCommand("speichern");
		knopf4.addKeyListener(this);		
		
		knopf5 = new JButton("abbrechen");
		knopf5.setPreferredSize(new Dimension(70, 20));
		knopf5.addActionListener(this);		
		knopf5.setActionCommand("abbrechen");
		knopf5.addKeyListener(this);		
	
		builder.add(knopf1,cc.xy(1,23));
		builder.add(knopf2, cc.xy(3,23));
		builder.add(knopf3, cc.xy(5,23));
		builder.add(knopf4, cc.xy(7, 23));
		builder.add(knopf5,cc.xy(9,23));
		knopfGedoense(new int[]{1,1,1,0,0});
		SetNeu.setEnabled(false);
		
		return builder.getPanel();
	}
	/************** Ende Methode f�r die Objekterstellung und -platzierung *********/	
	private void knopfGedoense(int[] knopfstatus){
		knopf1.setEnabled((knopfstatus[0]== 0 ? false : true));
		knopf2.setEnabled((knopfstatus[1]== 0 ? false : true));
		knopf3.setEnabled((knopfstatus[2]== 0 ? false : true));
		knopf4.setEnabled((knopfstatus[3]== 0 ? false : true));		
		knopf5.setEnabled((knopfstatus[4]== 0 ? false : true));
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
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getActionCommand().equals("comboaktion")){comboAuswerten();}

		if(arg0.getActionCommand().equals("neu")){neuHandeln();}

		if(arg0.getActionCommand().equals("aendern")){aendernHandeln();}

		if(arg0.getActionCommand().equals("abbrechen")){abbrechenHandeln();}
		
		if(arg0.getActionCommand().equals("speichern")){speichernHandeln();}

		if(arg0.getActionCommand().equals("loeschen")){loeschenHandeln();}
	}
	private void comboAuswerten(){
		if(lspeichern){return;}
		int j;
		int index;
		index = SetName.getSelectedIndex();
		
		SetNeu.setText((String)SetName.getSelectedItem());
		
		for(j=0;j<7;j++){
			Spalten[j].setSelectedItem(teilnehmer[index][j]);
			if(! ((String)Spalten[j].getSelectedItem()).trim().equals(teilnehmer[index][j].trim()) ){
				Spalten[j].setSelectedItem("./.");
			}
		}
	}
	private void comboFuellen(boolean erster,int index){
		int lang = SystemConfig.aTerminKalender.size();
		int i,j;
		teilnehmer = new String[lang][7];
		fach = new String[lang];
		SetName.removeAllItems();
		for(i=0;i<lang;i++){
			fach[i] = (String)((ArrayList)SystemConfig.aTerminKalender.get(i).get(0)).get(0);
			teilnehmer[i] = ((ArrayList<String[]>)SystemConfig.aTerminKalender.get(i).get(1)).get(0);
			SetName.addItem(String.valueOf(fach[i]));
		}
		if(erster){
			SetName.setSelectedIndex(0);
			SetNeu.setText(String.valueOf(fach[0]));
		}else{
			SetName.setSelectedIndex(index);
			SetNeu.setText(String.valueOf(fach[index]));
			SetName.setEnabled(true);
			SetNeu.setEnabled(false);
		}
		
		for(j=0;j<7;j++){
/*
			Spalten[j].setRenderer(new ComboBoxRenderer(Spalten[j].getRenderer(), Spalten[j],
					UIManager.get("ComboBox.disabledForeground")));
*/					
			if(erster){
				Spalten[j].setSelectedItem(teilnehmer[0][j]);
				Spalten[j].setEnabled(false);
			}else{
				Spalten[j].setSelectedItem(teilnehmer[index][j]);
				Spalten[j].setEnabled(false);
			}
		}
		
	}
	private void aendernHandeln(){
		int j;
		for(j=0;j<7;j++){
			Spalten[j].setEnabled(true);
		}
		SetNeu.setEnabled(true);
		SetName.setEnabled(false);
		knopfGedoense(new int[]{0,0,0,1,1});
		SetNeu.requestFocus();
	}
	private void abbrechenHandeln(){
		int j;
		for(j=0;j<7;j++){
			Spalten[j].setEnabled(false);
		}
		SetNeu.setEnabled(false);
		knopfGedoense(new int[]{1,1,1,0,0});
		SetName.setEnabled(true);
		comboAuswerten();
		lspeichern = false;
		lneu = false;
		SystemInit.abbrechen();
		//SystemUtil.thisClass.parameterScroll.requestFocus();

	}
	private void neuHandeln(){
		int j;
		lneu = true;
		SetName.setSelectedIndex(0);
		SetName.setEnabled(false);
		for(j=0;j<7;j++){
			Spalten[j].setSelectedIndex(0);
			Spalten[j].setEnabled(true);
		}
		SetNeu.setText("");
		SetNeu.setEnabled(true);
		knopfGedoense(new int[]{0,0,0,1,1});
		SetNeu.requestFocus();
	}
	/***********************************************************************/
	private void speichernHandeln(){
		int j;
		if(!lneu){
			int i;
			boolean abbruch = false;
			String set = "";
			lspeichern = true;
			String setXname = SetNeu.getText().trim();
			if(setXname.equals("")){
				JOptionPane.showMessageDialog(null, "Das Feld 'Name des Sets' darf nicht leer sein");
				SetNeu.setText((String) SetName.getSelectedItem());
				SetNeu.setEnabled(false);
				lspeichern = false;
				urzustandHerstellen(SetName.getSelectedIndex());
				knopfGedoense(new int[]{1,1,1,0,0});
				lneu = false;
				return;
			}
			int setAnzahl =  SetName.getItemCount();
			int setIndex = SetName.getSelectedIndex();
			for(i=0;i<fach.length;i++){
				if( (i!=setIndex) && (fach[i].trim().equals(setXname)) ){
					abbruch = true;
					break;
				}
			}
			if(abbruch){
				JOptionPane.showMessageDialog(null, "Das Set-Name "+setXname+" ist bereits vorhanden.\n"+
						"Jeder Set-Name darf nur einmal vorkommen");
				SetNeu.setText((String) SetName.getSelectedItem());
				SetNeu.setEnabled(false);
				lspeichern = false;
				urzustandHerstellen(SetName.getSelectedIndex());
				knopfGedoense(new int[]{1,1,1,0,0});
				lneu = false;
				return;
			}
			String setAktuellName = "NameSet"+(setIndex+1);
			String setInhaltName = "FeldSet" +(setIndex+1);
			for(i=0;i<7;i++){
				set = set+(i>0 ? ","+Spalten[i].getSelectedItem() : Spalten[i].getSelectedItem());
				teilnehmer[setIndex][i] = (String) Spalten[i].getSelectedItem();
			}
			SystemConfig.UpdateIni("Kalender", setAktuellName, setXname);
			SystemConfig.UpdateIni("Kalender", setInhaltName, set);

			SystemConfig.NurSets();
			//comboFuellen(false,setIndex);
			knopfGedoense(new int[]{1,1,1,0,0});
			int lang = SystemConfig.aTerminKalender.size();
			comboFuellen(false,setIndex);
			lspeichern = false;
		}else{
			String set = "";
			lspeichern = true;
			String setXname = SetNeu.getText().trim();
			if(setXname.equals("")){
				JOptionPane.showMessageDialog(null, "Das Feld 'Name des Sets' darf nicht leer sein");
				SetNeu.setText((String) SetName.getSelectedItem());
				SetNeu.setEnabled(false);
				lspeichern = false;
				urzustandHerstellen(SetName.getSelectedIndex());
				knopfGedoense(new int[]{1,1,1,0,0});	
				lneu = false;
				return;
			}
			boolean abbruch = false;			
			int setAnzahl =  SetName.getItemCount()+1;
			int i;
			for(i=0;i<fach.length;i++){
				if(  fach[i].trim().equals(setXname) ){
					abbruch = true;
					break;
				}
			}
			if(abbruch){
				JOptionPane.showMessageDialog(null, "Das Set-Name "+setXname+" ist bereits vorhanden.\n"+
						"Jeder Set-Name darf nur einmal vorkommen");
				SetNeu.setText((String) SetName.getSelectedItem());
				SetNeu.setEnabled(false);
				lspeichern = false;
				urzustandHerstellen(SetName.getSelectedIndex());
				knopfGedoense(new int[]{1,1,1,0,0});								
				lneu = false;
				return;
			}
			
			String setAktuellName = "NameSet"+(setAnzahl);
			String setInhaltName = "FeldSet" +(setAnzahl);
			for(i=0;i<7;i++){
				set = set+(i>0 ? ","+Spalten[i].getSelectedItem() : Spalten[i].getSelectedItem());
			}
			SystemConfig.UpdateIni("Kalender", "AnzahlSets", Integer.valueOf(setAnzahl).toString());
			SystemConfig.UpdateIni("Kalender", setAktuellName, setXname);
			SystemConfig.UpdateIni("Kalender", setInhaltName, set);

			SystemConfig.NurSets();
			//comboFuellen(false,setIndex);
			knopfGedoense(new int[]{1,1,1,0,0});
			int lang = SystemConfig.aTerminKalender.size();
			comboFuellen(false,lang-1);
			lspeichern = false;
			lneu = false;
		}
	}
	/***********************************************************************/	
	private void loeschenHandeln(){
		int anzahlSets = SetName.getItemCount();
		if(anzahlSets==1){
			JOptionPane.showMessageDialog(null, "Dieses Set ist as einzige Set!\n"+
			"Das letzte Set darf nicht gelöscht werden!");
			return;
		}	

		int anfrage = JOptionPane.showConfirmDialog(null, "Wollen Sie dieses Set wirklich löschen", "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
		if(anfrage == JOptionPane.YES_OPTION){
			int aktSet = SetName.getSelectedIndex();
			int i,j;
			int iniSet = 0;
			String set = "";
			SystemConfig.UpdateIni("Kalender", "AnzahlSets", Integer.valueOf(anzahlSets-1).toString());
			lspeichern = true;
			for(i=0;i<anzahlSets;i++){
				if(i != aktSet){
					set = "";
					for(j=0;j<7;j++){
						set = set+(j>0 ? ","+teilnehmer[i][j] : teilnehmer[i][j]);
					}
					SystemConfig.UpdateIni("Kalender", "NameSet"+(++iniSet), fach[i]);
					SystemConfig.UpdateIni("Kalender", "FeldSet"+(iniSet), set);
				}
			}
			if(aktSet > 0){
				aktSet = aktSet-1;
			}
			SystemConfig.NurSets();
			//knopfGedoense(new int[]{1,1,1,0,0});
			SetName.setSelectedIndex(aktSet);
			//System.out.println("Aktuelles Set = "+aktSet);
			comboFuellen(false,aktSet);
			lspeichern = false;
			lneu = false;

		}

	}
	private void urzustandHerstellen(int index){
		int j;
		//int index;
		index = SetName.getSelectedIndex();
		
		SetNeu.setText((String)SetName.getSelectedItem());
		
		for(j=0;j<7;j++){
			Spalten[j].setSelectedItem(teilnehmer[index][j]);
			Spalten[j].setEnabled(false);
		}
		SetNeu.setEnabled(false);
		SetName.setEnabled(true);
	}

}

