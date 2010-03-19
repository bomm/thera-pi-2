package org.therapi.reha.patient;

import hauptFenster.Reha;
import hauptFenster.SuchenDialog;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.jdesktop.swingworker.SwingWorker;

import com.mysql.jdbc.ResultSetMetaData;

import sqlTools.SqlInfo;
import stammDatenTools.ArztTools;
import stammDatenTools.KasseTools;
import stammDatenTools.PatTools;
import systemTools.JRtaTextField;
import systemTools.StringTools;
import terminKalender.DatFunk;
import events.PatStammEvent;
import events.PatStammEventListener;

public class PatientLogic {
	
	public PatientHauptPanel patientHauptPanel = null;
	private String lastseek = "";
	
	public PatientLogic(PatientHauptPanel patHauptPanel){
		this.patientHauptPanel = patHauptPanel;
	}
	public void fireAufraeumen(){
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
			Point thispoint = Reha.thisClass.patpanel.getLocationOnScreen();
			((SuchenDialog) patientHauptPanel.sucheComponent).setLocation(thispoint.x+30, thispoint.y+80);
			if(! patientHauptPanel.tfsuchen.getText().trim().equals(lastseek)){
				((SuchenDialog) patientHauptPanel.sucheComponent).suchDasDing(patientHauptPanel.tfsuchen.getText());
				lastseek = patientHauptPanel.tfsuchen.getText().trim();
			}
			((SuchenDialog) patientHauptPanel.sucheComponent).setVisible(true);
		}else{
			patientHauptPanel.sucheComponent = new SuchenDialog(null,Reha.thisClass.patpanel,patientHauptPanel.tfsuchen.getText());
			Point thispoint = Reha.thisClass.patpanel.getLocationOnScreen();
			((SuchenDialog) patientHauptPanel.sucheComponent).setLocation(thispoint.x+30, thispoint.y+80);
			((SuchenDialog) patientHauptPanel.sucheComponent).setVisible(true);
			lastseek = patientHauptPanel.tfsuchen.getText().trim();
		}
	}
	
	public void patStammEventOccurred(PatStammEvent evt) {
		//System.out.println("Event im Neuen PatStamm = "+evt);
		//System.out.println("Detail 0 = "+evt.getDetails()[0]);
		//System.out.println("Detail 1 = "+evt.getDetails()[1]);	
		if(evt.getDetails()[0].equals("#PATSUCHEN")){
			final String xpatint = evt.getDetails()[1].trim();
			patientHauptPanel.aktPatID = xpatint;
			final String xrez = evt.getDetails()[2].trim();
			// Anzeigedaten holen

			new Thread(){
				public void run(){
					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() throws Exception {
							datenHolen(xpatint);
							//new DatenHolen(xpatint);
							SwingUtilities.invokeLater(new Runnable(){
							 	   public  void run()
							 	   {
							 		   String titel = "Patient: "+Reha.thisClass.patpanel.ptfield[2].getText()+", "+
							 					Reha.thisClass.patpanel.ptfield[3].getText()+" geboren am: "+
							 					Reha.thisClass.patpanel.ptfield[4].getText();
							 			Reha.thisClass.patpanel.patientInternal.setzeTitel(titel);
							 			//System.out.println("neuer Titel = "+titel);
							 	   }
							});
							return null;
						}
					}.execute();
				}
			}.start();
			// kmplette Patdaten holen		
			new Thread(){
				public void run(){
					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() throws Exception {
										//Reha.thisClass.patpanel.patDaten.clear();
										//Reha.thisClass.patpanel.patDaten = (Vector<String>)SqlInfo.holeSatz("pat5", " * ", "pat_intern='"+xpatint+"'", Arrays.asList(new String[] {}));
										long zeit = System.currentTimeMillis();
										while(! Reha.thisClass.patpanel.patDatenOk){
											Thread.sleep(20);
											if(System.currentTimeMillis()-zeit > 10000){
												return null;
											}
										}
										PatTools.constructPatHMap();		
										ArztTools.constructArztHMap("");
										KasseTools.constructKasseHMap("");

										new Thread(){
											public void run(){
											}
										}.start();
							return null;
						}
					}.execute();
				}
			}.start();
			// Rezeptdaten holen
			new Thread(){
				public void run(){		
					new SwingWorker<Void,Void>(){
						@Override
						protected Void doInBackground() throws Exception {
							if(!xrez.contains("#REZHOLEN-")){
								patientHauptPanel.aktRezept.holeRezepte(xpatint,"");	
							}else{
								//patientHauptPanel.aktRezept.suchePatUeberRez = true;
								//patientHauptPanel.aktRezept.holeRezepte(xpatint,xrez.split("#REZHOLEN-")[1].trim());
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
							//patientHauptPanel.historie.holeRezepte(xpatint,"");
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
							//patientHauptPanel.berichte.holeBerichte(xpatint,"");
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
							//patientHauptPanel.dokumentation.holeDokus(xpatint,"");
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
							//patientHauptPanel.gutachten.holeGutachten(xpatint,"");
							return null;
						}
					}.execute();
				}
			}.start();

			int i = patientHauptPanel.multiTab.getTabCount();
			for(int y = 0;y < i;y++){
				//System.out.println("Tabtitel von "+y+" = "+jtab.getTitleAt(y));
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
			patientHauptPanel.tfsuchen.requestFocus();
		}
		/*
		if(evt.getDetails()[0].equals("#KORRIGIEREN")){
			if(this.neuDlgOffen){return;}
			final String feld = (String)evt.getDetails()[1];
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					//System.out.println("Korrigieren->"+evx.getSource());
					editFeld(feld);
					return null;
				}
			}.execute();
		}*/

	}
	public void datenHolen(String patint){
		Statement stmt = null;
		ResultSet rs = null;
		String sstmt = "";

		sstmt = "select * from pat5 where PAT_INTERN ='"+patint+"'";
			
		try {
			stmt =  Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try{
			rs = stmt.executeQuery(sstmt);
			Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			while( rs.next()){
				
				ResultSetMetaData met = (ResultSetMetaData) rs.getMetaData();
				int cols = met.getColumnCount();
				int type;
				String colname = "";
				String colvalue = "";
				Reha.thisClass.patpanel.patDatenOk = false;
				Reha.thisClass.patpanel.patDaten.clear();
				
				for(int i = 0;i < cols;i++){
					Reha.thisClass.patpanel.patDaten.add((rs.getString(i+1)==null ? "" : rs.getString(i+1)) );
					if(i <15){
					//for(int i = 0; i < 15; i++){
						try{
							if(Reha.thisClass.patpanel.ptfield[i]==null){continue;}
							colname = Reha.thisClass.patpanel.ptfield[i].getName();
							if(((JRtaTextField)Reha.thisClass.patpanel.ptfield[i]).getRtaType().equals("DATUM")){
								colvalue = DatFunk.sDatInDeutsch(rs.getString("GEBOREN"))+" ";
							}else{
								colvalue = rs.getString(colname);
							}
							Reha.thisClass.patpanel.ptfield[i].setText(StringTools.EGross(colvalue));
							}catch(Exception ex){
								ex.printStackTrace();
							}

					}
				}
				Reha.thisClass.patpanel.patDatenOk = true;
				String instring = (rs.getString("ANAMNESE")==null ? "" : rs.getString("ANAMNESE"));
				if(instring.equals("")){
					Reha.thisClass.patpanel.pmemo[0].setText("");
				}else{
					Reha.thisClass.patpanel.pmemo[0].setText(instring);				
				}
				instring = (rs.getString("PAT_TEXT")==null ? "" : rs.getString("PAT_TEXT"));
				
				if(instring.equals("")){
					Reha.thisClass.patpanel.pmemo[1].setText("");
				}else{
					Reha.thisClass.patpanel.pmemo[1].setText(instring);				
				}
				Reha.thisClass.patpanel.autoPatid = rs.getInt("id");
				Reha.thisClass.patpanel.aid = StringTools.ZahlTest(rs.getString("arztid"));
				Reha.thisClass.patpanel.kid = StringTools.ZahlTest(rs.getString("kassenid"));
				if(Reha.thisClass.patpanel.aid < 0){
					Reha.thisClass.patpanel.ptfield[13].setForeground(Color.RED);
					Reha.thisClass.patpanel.ptfield[13].setFont(Reha.thisClass.patpanel.fehler);
				}else{
					Reha.thisClass.patpanel.ptfield[13].setForeground(Color.BLUE);
					Reha.thisClass.patpanel.ptfield[13].setFont(Reha.thisClass.patpanel.font);
				}
				if(Reha.thisClass.patpanel.kid < 0){
					Reha.thisClass.patpanel.ptfield[14].setForeground(Color.RED);				
					Reha.thisClass.patpanel.ptfield[14].setFont(Reha.thisClass.patpanel.fehler);
				}else{
					Reha.thisClass.patpanel.ptfield[14].setForeground(Color.BLUE);				
					Reha.thisClass.patpanel.ptfield[14].setFont(Reha.thisClass.patpanel.font);
				}

			}
			Reha.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}catch(SQLException ev){
			System.out.println("SQLException: " + ev.getMessage());
			System.out.println("SQLState: " + ev.getSQLState());
			System.out.println("VendorError: " + ev.getErrorCode());
		} 

		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { // ignore }
					rs = null;
				}
			}
		
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { // ignore }
					stmt = null;
				}
			}
		}
	  }

	
	
}
