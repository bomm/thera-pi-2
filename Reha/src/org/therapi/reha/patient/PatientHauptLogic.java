package org.therapi.reha.patient;

import hauptFenster.Reha;
import hauptFenster.SuchenDialog;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import krankenKasse.KassenFormulare;
import oOorgTools.OOTools;

import org.jdesktop.swingworker.SwingWorker;

import patientenFenster.PatNeuanlage;
import rechteTools.Rechte;
import sqlTools.SqlInfo;
import stammDatenTools.ArztTools;
import stammDatenTools.KasseTools;
import stammDatenTools.PatTools;
import systemEinstellungen.INIFile;
import systemEinstellungen.SystemConfig;
import systemTools.JRtaTextField;
import systemTools.StringTools;
import terminKalender.DatFunk;
import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import events.PatStammEvent;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;

public class PatientHauptLogic {
	
	public PatientHauptPanel patientHauptPanel = null;
	private String lastseek = "";
	public boolean neuDlgOffen = false;
	private Vector<String> titel = new Vector<String>();
	private Vector<String> formular = new Vector<String>();
	private int iformular;
	private JRtaTextField formularid = new JRtaTextField("NIX",false);
	public PatientHauptLogic(PatientHauptPanel patHauptPanel){
		this.patientHauptPanel = patHauptPanel;
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					patientHauptPanel.imgzuzahl[0] = SystemConfig.hmSysIcons.get("zuzahlfrei");
					patientHauptPanel.imgzuzahl[1] = SystemConfig.hmSysIcons.get("zuzahlok");			
					patientHauptPanel.imgzuzahl[2] = SystemConfig.hmSysIcons.get("zuzahlnichtok");
					patientHauptPanel.imgzuzahl[3] = SystemConfig.hmSysIcons.get("kleinehilfe");
					//imgrezstatus[0] = SystemConfig.hmSysIcons.get("statusoffen");
					patientHauptPanel.imgrezstatus[0] = null;
					patientHauptPanel.imgrezstatus[1] = SystemConfig.hmSysIcons.get("statuszu");
				
				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}
		}.execute();	
		SwingUtilities.invokeLater(new Runnable(){
			public  void run(){
				KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.ALT_MASK);
				patientHauptPanel.getInstance().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "doSuchen");
				patientHauptPanel.getActionMap().put("doSuchen", new PatientAction());
				stroke = KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.ALT_MASK);
				patientHauptPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "doNeu");
				patientHauptPanel.getActionMap().put("doNeu", new PatientAction());	
				stroke = KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.ALT_MASK);
				patientHauptPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "doEdit");
				patientHauptPanel.getActionMap().put("doEdit", new PatientAction());
				stroke = KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.ALT_MASK);
				patientHauptPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "doDelete");
				patientHauptPanel.getActionMap().put("doDelete", new PatientAction());
				stroke = KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.ALT_MASK);
				patientHauptPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "doFormulare");
				patientHauptPanel.getActionMap().put("doFormulare", new PatientAction());
				holeFormulare();
				if(Reha.thisClass.terminpanel != null){
			    	//TerminFenster.thisClass.setUpdateVerbot(true);
			    }

	   	  	}
		});
		
	}
	public void fireAufraeumen(){
		formularid.listenerLoeschen();
		formularid = null;
		patientHauptPanel = null;
	}
	
	/***
	 * SuchenDialog aufrufen
	 */
	public void starteSuche(){
		if(patientHauptPanel.tfsuchen.getText().trim().equals("")){
			String cmd = "<html>Sie haben <b>kein</b> Suchkriterium eingegeben.<br>"+
			"Das bedeutet Sie laden den <b>kompletten Patientenstamm!!!<b><br><br>"+
			"Wollen Sie das wirklich?";
			int anfrage = JOptionPane.showConfirmDialog(null, cmd,"Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
			if(anfrage == JOptionPane.NO_OPTION){
				return;
			}
		}
		if (patientHauptPanel.sucheComponent != null){
			Point thispoint = patientHauptPanel.tfsuchen.getLocationOnScreen();
			((SuchenDialog) patientHauptPanel.sucheComponent).setLocation(thispoint.x-60, thispoint.y+25);
			if(! patientHauptPanel.tfsuchen.getText().trim().equals(lastseek)){
				((SuchenDialog) patientHauptPanel.sucheComponent).suchDasDing(patientHauptPanel.tfsuchen.getText());
				lastseek = patientHauptPanel.tfsuchen.getText().trim();
			}
			((SuchenDialog) patientHauptPanel.sucheComponent).setVisible(true);
		}else{
			patientHauptPanel.sucheComponent = new SuchenDialog(null,Reha.thisClass.patpanel,
					patientHauptPanel.tfsuchen.getText(),patientHauptPanel.jcom.getSelectedIndex());
			Point thispoint = patientHauptPanel.tfsuchen.getLocationOnScreen();
			((SuchenDialog) patientHauptPanel.sucheComponent).setLocation(thispoint.x-60, thispoint.y+25);
			((SuchenDialog) patientHauptPanel.sucheComponent).setVisible(true);
			lastseek = patientHauptPanel.tfsuchen.getText().trim();
		}
	}
	class PatientAction extends AbstractAction {
	    /**
		 * 
		 */
		private static final long serialVersionUID = 6195808468235028392L;

		public void actionPerformed(ActionEvent e) {
	        ////System.out.println("Patient Action = "+e.getActionCommand());
	        ////System.out.println(e);
	        if(e.getActionCommand().equals("f")){
	        	 Reha.thisClass.patpanel.tfsuchen.requestFocus();
	        }
	        if(e.getActionCommand().equals("n")){
	        	patNeu();
	        }
	        if(e.getActionCommand().equals("e")){
	        	patEdit();

	        }	            
	        if(e.getActionCommand().equals("l")){
	       		patDelete();
	        }	            
	        if(e.getActionCommand().equals("b")){
	        	patStarteFormulare();
	        }	            
	    }
	}
	
	public void patNeu(){
		if(!Rechte.hatRecht(Rechte.Patient_anlegen, true)){
			return;
		}
		neuanlagePatient(true,"");
	}
	public void patEdit(){
		if(Rechte.hatRecht(Rechte.Patient_editteil, false) ||
				Rechte.hatRecht(Rechte.Patient_editvoll, false)){
			if(!patientHauptPanel.aktPatID.equals("")){
				neuanlagePatient(false,"");	
			}else{
				JOptionPane.showMessageDialog(null, "Welchen Patient bitteschön wollen Sie editieren?");
				setzeFocus();
				return;
			}
		}else{
			Rechte.hatRecht(Rechte.Patient_editvoll, true);
		}
	}
	public void editFeld(String feldname) {
		if(Rechte.hatRecht(Rechte.Patient_editteil, false) ||
				Rechte.hatRecht(Rechte.Patient_editvoll, false)){
			if(! patientHauptPanel.aktPatID.equals("")){
				neuanlagePatient(false, feldname);		
			}
		}else{
			Rechte.hatRecht(Rechte.Patient_editvoll, true);
		}
	}
	
	public void patDelete(){
		if(! Rechte.hatRecht(Rechte.Patient_delete, true)){
			return;
		}
		if(!patientHauptPanel.aktPatID.equals("")){
			//String spat = patientHauptPanel.ptfield[2].getText().trim()+", "+patientHauptPanel.ptfield[3].getText().trim()+", geb.am "+patientHauptPanel.ptfield[4].getText().trim();
			//String spat = "Wollen Sie den aktuellen Patient wirklich löschen?";
	    	int frage = JOptionPane.showConfirmDialog(null, "Wollen Sie den aktuellen Patient wirklich löschen??", "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
	    	if(frage != JOptionPane.YES_OPTION){
	    		return;
	    	}
			String stmt = "delete from pat5 where pat_intern='"+patientHauptPanel.aktPatID+"'";
			SqlInfo.sqlAusfuehren(stmt);
			allesAufNull();
			setzeFocus();
		}else{
			JOptionPane.showMessageDialog(null, "Welchen Patient bitteschön wollen Sie löschen?");
			setzeFocus();
			return;
		}		
	}
	private void allesAufNull(){
		/******************************************************************************/
		// erst die sichtbaren Edits löschen
		/*
		for(int i = 0; i <15;i++){
			patientHauptPanel.ptfield[i].setText("");
		}
		*/
		// aktPatID zurücksetzten dann ist in weiteres löschen nicht mehr möglich
		patientHauptPanel.aktPatID = "";
		patientHauptPanel.autoPatid = -1;
		// jetzt das RezeptPanel KeinRezept anhängen
		patientHauptPanel.getStammDaten().htmlPane.setText("");
		patientHauptPanel.aktRezept.setzeRezeptPanelAufNull(true);
		patientHauptPanel.historie.setzeHistoriePanelAufNull(true);
		patientHauptPanel.berichte.setzeBerichtPanelAufNull(true);
		patientHauptPanel.dokumentation.setzeDokuPanelAufNull(true);
		patientHauptPanel.gutachten.setzeGutachtenPanelAufNull(true);
		// dann die Icons löschen
		patientHauptPanel.pmemo[0].setText("");
		patientHauptPanel.pmemo[1].setText("");
	}
	public void patStarteFormulare(){
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					iformular = -1;
					KassenFormulare kf = new KassenFormulare(Reha.thisFrame,titel,formularid);
					Point pt = patientHauptPanel.jbut[3].getLocationOnScreen();
					kf.setLocation(pt.x-100,pt.y+25);
					kf.setModal(true);
					final KassenFormulare xkf = kf;
					SwingUtilities.invokeLater(new Runnable(){
						@Override
						public void run() {
							xkf.aufErsteElement();
						}
						
					});
					kf.setVisible(true);
					
					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() throws Exception {
							if(! formularid.getText().equals("") ){
								iformular = Integer.valueOf(formularid.getText());
								if(iformular >=0){
									String sdatei = formular.get(iformular);
									OOTools.starteStandardFormular(Reha.proghome+"vorlagen/"+Reha.aktIK+"/"+sdatei,null);
								}
							}
							return null;
						}
					}.execute();
					kf=null;
					return null;
				}
				
			}.execute();
		}
		

	public void holeFormulare(){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				INIFile inif = new INIFile(Reha.proghome+"ini/"+Reha.aktIK+"/patient.ini");
				int forms = inif.getIntegerProperty("Formulare", "PatientFormulareAnzahl");
				for(int i = 1; i <= forms; i++){
					titel.add(inif.getStringProperty("Formulare","PFormularText"+i));			
					formular.add(inif.getStringProperty("Formulare","PFormularName"+i));
				}	
				return null;
			}
		}.execute();
	}
	
	
	public void neuanlagePatient(boolean lneu,String feldname){
		if(neuDlgOffen){
			return;
		}
		neuDlgOffen = true;
		PatNeuDlg neuPat = new PatNeuDlg();
		PinPanel pinPanel = new PinPanel();
		pinPanel.setName("PatientenNeuanlage");
		pinPanel.getGruen().setVisible(false);
		neuPat.setPinPanel(pinPanel);
		if(lneu){
			neuPat.getSmartTitledPanel().setTitle("Patient Neuanlage");	
		}else{
			//neuPat.getSmartTitledPanel().setTitle("editieren ---> "+ptfield[2].getText().trim()+", "+ptfield[3].getText().trim()+", geboren am: "+ptfield[4].getText().trim());		
		}
		neuPat.setSize(960,600);
		neuPat.setPreferredSize(new Dimension(960,600));
		neuPat.getSmartTitledPanel().setPreferredSize(new Dimension (960,600));
		neuPat.setPinPanel(pinPanel);
		PatNeuanlage pneu = new PatNeuanlage(null,lneu,feldname);
		neuPat.getSmartTitledPanel().setContentContainer(pneu);
		neuPat.getSmartTitledPanel().getContentContainer().setName("PatientenNeuanlage");
	    neuPat.setName("PatientenNeuanlage");
		neuPat.setLocationRelativeTo(null);
		neuPat.setTitle("Patienten Neuanlage");
		neuPat.pack();
		neuPat.setModal(true);
		//neuPat.setAlwaysOnTop(true);
		neuPat.setVisible(true);
		//neuPat.dispose();
		neuPat = null;
	    pinPanel = null;
		//System.out.println("Pat Neu/ändern ist disposed");
		neuDlgOffen = false;
		

	}
	class PatNeuDlg extends RehaSmartDialog implements RehaTPEventListener,WindowListener{
		/**
		 * 
		 */
		private static final long serialVersionUID = -7706110755275876905L;
		private RehaTPEventClass rtp = null;
		public PatNeuDlg(){
			super(null,"PatientenNeuanlage");
			this.setName("PatientenNeuanlage");
			rtp = new RehaTPEventClass();
			rtp.addRehaTPEventListener((RehaTPEventListener) this);
		}
		public void rehaTPEventOccurred(RehaTPEvent evt) {
			try{
				if(evt.getDetails()[0] != null){
					if(evt.getDetails()[0].equals(this.getName())){
						this.setVisible(false);
						rtp.removeRehaTPEventListener((RehaTPEventListener) this);
						rtp = null;
						this.dispose();
						super.dispose();
						//System.out.println("****************Patient Neu/ändern -> Listener entfernt**************");				
					}
				}
			}catch(NullPointerException ne){
				//System.out.println("In PatNeuanlage" +evt);
			}
		}
		public void windowClosed(WindowEvent arg0) {
			if(rtp != null){
				this.setVisible(false);			
				rtp.removeRehaTPEventListener((RehaTPEventListener) this);		
				rtp = null;
				dispose();
				super.dispose();
				//System.out.println("****************Patient Neu/ändern -> Listener entfernt (Closed)**********");
			}
		}
		
		
	}
	public void arztListeSpeichernVector(Vector<?> vec, boolean inNeu, String xpatintern){
		String aliste = "";
		for(int i = 0;i < vec.size();i++){
			aliste = aliste+"@"+((String)((Vector<?>)vec.get(i)).get(5))+"@\n";
		}
		String sets = "aerzte='"+aliste+"'";
		SqlInfo.aktualisiereSaetze("pat5",sets , "pat_intern='"+xpatintern+"'");
		//System.out.println("Sets = "+sets +" pat_Intern = "+xpatintern);
		if(Reha.thisClass.patpanel.aktPatID.equals(xpatintern)){
			//System.out.println("Länge des patDaten.Arrays = "+Reha.thisClass.patpanel.patDaten.size());
			Reha.thisClass.patpanel.patDaten.set(63,aliste);		
		}
	}
	public void arztListeSpeichernString(String aliste,boolean inNeu,String xpatintern){
		String sets = "aerzte='"+aliste+"'";
		SqlInfo.aktualisiereSaetze("pat5",sets , "pat_intern='"+xpatintern+"'");
		//System.out.println("Sets = "+sets +" pat_Intern = "+xpatintern);
		if(Reha.thisClass.patpanel.aktPatID.equals(xpatintern)){
			Reha.thisClass.patpanel.patDaten.set(63,aliste);		
		}
	}
	
	
	public void patStammEventOccurred(PatStammEvent evt) {
		////System.out.println("Event im Neuen PatStamm = "+evt);
		////System.out.println("Detail 0 = "+evt.getDetails()[0]);
		////System.out.println("Detail 1 = "+evt.getDetails()[1]);	
		if(evt.getDetails()[0].equals("#PATSUCHEN")){
			final String xpatint = evt.getDetails()[1].trim();
			final String xrez = evt.getDetails()[2].trim();
			patientHauptPanel.aktPatID = xpatint;
			// Anzeigedaten holen

			new Thread(){
				public void run(){
					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() throws Exception {
							datenHolen(xpatint);
							SwingUtilities.invokeLater(new Runnable(){
							 	   public  void run()
							 	   {
							 		   try{
								 		   String titel = "Patient: "+Reha.thisClass.patpanel.patDaten.get(2)+", "+
						 					Reha.thisClass.patpanel.patDaten.get(3)+" geboren am: "+
						 					DatFunk.sDatInDeutsch(Reha.thisClass.patpanel.patDaten.get(4))+" - "+
						 					"Patienten-ID: "+Reha.thisClass.patpanel.patDaten.get(29);
								 		   	Reha.thisClass.patpanel.patientInternal.setzeTitel(titel);
								 		   	macheAlleHashMaps();
							 		   }catch(Exception ex){
							 			   ex.printStackTrace();
							 		   }
							 	   }
							});
							return null;
						}
					}.execute();
				}
			}.start();
			// kmplette Patdaten holen		
			//new Thread(){
				//public void run(){
				//}
			//}.start();
			// Rezeptdaten holen
			new Thread(){
				public void run(){		
					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() throws Exception {
							long zeit = System.currentTimeMillis();
							while(! Reha.thisClass.patpanel.patDatenOk){
								Thread.sleep(20);
								if(System.currentTimeMillis()-zeit > 10000){
									JOptionPane.showMessageDialog(null, "Fehler beim Bezug der Rezeptdaten");
									return null;
								}
							}
							if(!xrez.contains("#REZHOLEN-")){
								if(xrez.trim().equals("")){
									patientHauptPanel.aktRezept.holeRezepte(xpatint,"");									
								}else{
									patientHauptPanel.aktRezept.holeRezepte(xpatint,xrez.trim());
								}
							}else{
								patientHauptPanel.aktRezept.suchePatUeberRez = true;
								patientHauptPanel.aktRezept.holeRezepte(xpatint,xrez.split("#REZHOLEN-")[1].trim());
							}
							return null;
						}
					}.execute();
				}
			}.start();
			// Historie holen
			new Thread(){
				public void run(){		
					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() throws Exception {
							patientHauptPanel.historie.holeRezepte(xpatint,"");
							return null;
						}
					}.execute();
				}
			}.start();
			// Berichte holen
			new Thread(){
				public void run(){		
					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() throws Exception {
							patientHauptPanel.berichte.holeBerichte(xpatint,"");
							return null;
						}
					}.execute();
				}
			}.start();
			// Dokumentation holen
			new Thread(){
				public void run(){		
					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() throws Exception {
							patientHauptPanel.dokumentation.holeDokus(xpatint,"");
							return null;
						}
					}.execute();
				}
			}.start();
			// Gutachten (E-Berichte) holen
			new Thread(){
				public void run(){		
					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() throws Exception {
							patientHauptPanel.gutachten.holeGutachten(xpatint,"");
							return null;
						}
					}.execute();
				}
			}.start();

			int i = patientHauptPanel.multiTab.getTabCount();
			for(int y = 0;y < i;y++){
				////System.out.println("Tabtitel von "+y+" = "+jtab.getTitleAt(y));
			}
		}
		if(evt.getDetails()[0].equals("#SUCHENBEENDEN")){
			if(((SuchenDialog) patientHauptPanel.sucheComponent) != null){
				((SuchenDialog) patientHauptPanel.sucheComponent).dispose();
				patientHauptPanel.sucheComponent = null;
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						Runtime r = Runtime.getRuntime();
					    r.gc();
						return null;
					}
					
				}.execute();
			}
		}
		if(evt.getDetails()[0].equals("#PATEDIT")){
			//neuanlagePatient(false,"");	
		}
		if(evt.getDetails()[0].equals("#CLOSING")){
			if(patientHauptPanel.sucheComponent != null){
				//patientHauptPanel.ptp.removePatStammEventListener((PatStammEventListener) this);
				((SuchenDialog) patientHauptPanel.sucheComponent).dispose();	
			}
		}
		if(evt.getDetails()[0].equals("#FOCUSIEREN")){
			setzeFocus();
			patientHauptPanel.tfsuchen.requestFocus();
		}
		/*
		if(evt.getDetails()[0].equals("#KORRIGIEREN")){
			if(this.neuDlgOffen){return;}
			final String feld = (String)evt.getDetails()[1];
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					////System.out.println("Korrigieren->"+evx.getSource());
					editFeld(feld);
					return null;
				}
			}.execute();
		}*/

	}
	public void setzeFocus(){
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run(){
			 		  if(! patientHauptPanel.tfsuchen.hasFocus()){
			 			  if(! patientHauptPanel.patientInternal.getActive()){
			 				 patientHauptPanel.patientInternal.activateInternal();
			 				  SwingUtilities.invokeLater(new Runnable(){
			 					  public  void run(){
			 						 patientHauptPanel.tfsuchen.requestFocus();
			 					  }
			 				  }); 	   	
		 				  }else{
		 					 SwingUtilities.invokeLater(new Runnable(){
								  public  void run(){
									  patientHauptPanel.tfsuchen.requestFocus();
								  }
		 					 }); 	   	
		 				  }
					  }else{
						  SwingUtilities.invokeLater(new Runnable(){
							  public  void run(){
								  patientHauptPanel.tfsuchen.requestFocus();
							  }
						  }); 	   	
					  }
			 	   }
		}); 	   	
	}

	public void datenHolen(String patint){
		try{
			Reha.thisClass.patpanel.patDatenOk = false;
			Reha.thisClass.patpanel.patDaten = SqlInfo.holeSatz("pat5"," * ", "PAT_INTERN ='"+patint+"'", Arrays.asList(new String[] {}) );
			//System.out.println("Größe der Daten = "+Reha.thisClass.patpanel.patDaten.size());
			if(Reha.thisClass.patpanel.patDaten.size() == 71){
				if(Reha.thisClass.patpanel.patDaten.get(65).equals("")){
					Reha.thisClass.patpanel.pmemo[0].setText("");
				}else{
					Reha.thisClass.patpanel.pmemo[0].setText(Reha.thisClass.patpanel.patDaten.get(65));				
				}
				if(Reha.thisClass.patpanel.patDaten.get(64).equals("")){
					Reha.thisClass.patpanel.pmemo[1].setText("");
				}else{
					Reha.thisClass.patpanel.pmemo[1].setText(Reha.thisClass.patpanel.patDaten.get(64));				
				}
				Reha.thisClass.patpanel.autoPatid = Integer.parseInt(Reha.thisClass.patpanel.patDaten.get(66));
				Reha.thisClass.patpanel.aid = StringTools.ZahlTest(Reha.thisClass.patpanel.patDaten.get(67));
				Reha.thisClass.patpanel.kid = StringTools.ZahlTest(Reha.thisClass.patpanel.patDaten.get(68));
				Reha.thisClass.patpanel.patDatenOk = true;
				Reha.thisClass.patpanel.getStammDaten().parseHTML(true);			
			}else{
				JOptionPane.showMessageDialog(null, "Fehler beim Einlesen der Patientendaten");
				Reha.thisClass.patpanel.patDatenOk = false;
				Reha.thisClass.patpanel.getStammDaten().parseHTML(false);
			}
		}catch(Exception ex){
			Reha.thisClass.patpanel.patDatenOk = true;
		}
		
	}
	private void macheAlleHashMaps(){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					PatTools.constructPatHMap();
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null, "Fehler bei PatTools.constructPatHMap()");
				}
				try{
					ArztTools.constructArztHMap("");
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null, "Fehler bei	ArztTools.constructArztHMap('')");
				}
				try{
					KasseTools.constructKasseHMap("");
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null, "Fehler bei	KasseTools.constructKasseHMap('')");
				}
				if(((SuchenDialog) patientHauptPanel.sucheComponent) != null){
					((SuchenDialog) patientHauptPanel.sucheComponent).dispose();
					patientHauptPanel.sucheComponent = null;
				}
				return null;
			}
		}.execute();
	}
	
	
}
