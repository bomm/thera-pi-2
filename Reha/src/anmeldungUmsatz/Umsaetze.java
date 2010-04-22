package anmeldungUmsatz;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import rehaInternalFrame.JUmsaetzeInternal;
import systemTools.ButtonTools;
import systemTools.JRtaTextField;
import terminKalender.ParameterLaden;
import utils.DatFunk;

public class Umsaetze extends JXPanel{

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -906652242216759628L;
	JUmsaetzeInternal internal = null;
	JXPanel content = null;
	JLabel lab;
	JRtaTextField[] tfs = {null,null};
	JButton[] buts = {null};
	JLabel aktion = null;
	JProgressBar progress = null;
	Vector<Vector<Object>> kalUsers = new Vector<Vector<Object>>();
	Vector<Vector<Vector<String>>> allDates = new Vector<Vector<Vector<String>>>(); 
	ActionListener al = null;
	
	public Umsaetze(JUmsaetzeInternal uint){
		super();
		this.internal = uint;
		this.makeListeners();
		this.add(getContent(),BorderLayout.CENTER);
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				setzeFocus();
			}
		});		
	}
	private void setzeFocus(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				tfs[0].requestFocus();
			}
		});
	}
	private JXPanel getContent(){//             1          2   3      4    5     6      7    8     9     10         11    
		FormLayout lay = new FormLayout("fill:0:grow(0.5),5dlu,20dlu,60dlu,20dlu,20dlu,60dlu,25dlu,60dlu,5dlu,fill:0:grow(0.5),",
				"fill:0:grow(0.5),p,5dlu,p,2dlu,p,fill:0:grow(0.5)");
		CellConstraints cc = new CellConstraints();
		content = new JXPanel();
		content.setLayout(lay);

		lab = new JLabel("von..");
		this.content.add(lab,cc.xy(3,2));
		this.tfs[0] = new JRtaTextField("DATUM",false);
		this.tfs[0].setText(DatFunk.sHeute());
		this.content.add(this.tfs[0],cc.xy(4,2));
		
		lab = new JLabel("bis..");
		this.content.add(lab,cc.xy(6,2));
		this.tfs[1] = new JRtaTextField("DATUM",false);
		this.tfs[1].setText(DatFunk.sHeute());
		this.content.add(this.tfs[1],cc.xy(7,2));
		
		this.content.add((this.buts[0] = ButtonTools.macheButton("ermitteln", "ermitteln", al)),cc.xyw(9,2,2));
		
		aktion = new JLabel("  ");
		aktion.setForeground(Color.BLUE);
		this.content.add(aktion,cc.xyw(1,4,9,CellConstraints.LEFT,CellConstraints.DEFAULT));
		
		progress = new JProgressBar();
		this.content.add(progress,cc.xyw(1,6,10,CellConstraints.FILL,CellConstraints.DEFAULT));
		return content;
	}
	/**********************/
	private void ermittleDates(){
		try{
		int anzahlbehandler = allDates.size();
		int anzahltage = allDates.get(0).size();
		for(int i1 = 0; i1 < anzahlbehandler;i1++ ){
			//progressbar einstellen
			for(int i2 = 0; i2 < anzahltage;i2++){
				//text einstellen
				int anzahltermine = Integer.parseInt(allDates.get(i1).get(i2).get(300));
				/*
				for(int i = 0;i < allDates.get(i1).get(i2).size();i++){
					System.out.println("Feld "+i+" = "+allDates.get(i1).get(i2).get(i));
				}
				*/
				
				//text einstellen
				for(int i3 = 0; i3 < anzahltermine; i3++){
					if(! allDates.get(i1).get(i2).get(i3).contains("@FREI")){
						aktion.setText("Teste Rezepte von Termin"+allDates.get(i1).get(i2).get(i3));
					}
					
				}


			}
		}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	/**********************/	
	private int testeKalenderUser(){
		int lang = ParameterLaden.vKKollegen.size();
		Vector<Object> vec = new Vector<Object>();
		kalUsers.clear();
		kalUsers.trimToSize();
		for(int i = 0; i < lang; i++){
			if(! ParameterLaden.getMatchcode(i).trim().equals("./.")){
				vec.clear();
				vec.add((String)ParameterLaden.getMatchcode(i));
				vec.add((Integer)ParameterLaden.getDBZeile(i));
				kalUsers.add((Vector<Object>)vec.clone());
			}	
		}
		return kalUsers.size();
	}
	/**********************/
	private int ermittleFaelle(String datum_von,String datum_bis){
		int lang = kalUsers.size();
		int behandler = 0;
		progress.setMinimum(0);
		progress.setMaximum(lang-1);
		progress.setValue(0);
		Vector<Vector<String>> vec = new Vector<Vector<String>>();
		allDates.clear();
		allDates.trimToSize();
		for(int i = 0; i < lang; i++){
			vec.clear();
			aktion.setText("Hole Daten von Kalenderbenutzer: "+kalUsers.get(i).get(0));
			progress.setValue(i);
			behandler = (Integer) kalUsers.get(i).get(1);
			vec = 
				sqlTools.SqlInfo.holeFelder("select * from flexkc where datum >='"+
					datum_von+
					"' AND datum <='"+
					datum_bis+
					"' AND "+
					"behandler ='"+
					(behandler < 10 ? "0"+Integer.toString(behandler)+"BEHANDLER'" : Integer.toString(behandler)+"BEHANDLER'")
					);
			allDates.add((Vector)vec.clone());
		}
		progress.setValue(lang);
		return allDates.size();
	}
	/**********************/	
	private void makeListeners(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("ermitteln")){
							new SwingWorker<Void,Void>(){
								@Override
								protected Void doInBackground() throws Exception {
									try{
										setCursor(Reha.thisClass.wartenCursor);
										new SwingWorker<Void,Void>(){
											@Override
											protected Void doInBackground() throws Exception {
												if( testeKalenderUser() > 0){
													String dat1,dat2;
													dat1 = DatFunk.sDatInSQL(tfs[0].getText());
													dat2 = DatFunk.sDatInSQL(tfs[1].getText());
													try{
														if(ermittleFaelle(dat1,dat2) > 0){
															ermittleDates();															
														}
													}catch(Exception ex){
														JOptionPane.showMessageDialog(null,"Die von Ihnen eingegebenen Datumswerte sind nicht korrekt");
													}
					
												}
												setCursor(Reha.thisClass.cdefault);
												aktion.setText("  ");
												progress.setValue(0);
												System.out.println("Daten wurden gesammelt von "+allDates.size()+" Behandlern");
												System.out.println("Anzahl Tage analysiert "+allDates.get(0).size());
												return null;
											}
											
										}.execute();
									}catch(Exception ex){
										ex.printStackTrace();
									}
									return null;
								}
							}.execute();
							
					return;
				}
				if(cmd.equals("calc")){
					JOptionPane.showMessageDialog(null, "Diese Funktion ist noch nicht implementiert");
				}
			}
		};
	}
}
