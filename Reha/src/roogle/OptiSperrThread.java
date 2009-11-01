package roogle;

import hauptFenster.Reha;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ImageIcon;

import systemEinstellungen.SystemConfig;

public class OptiSperrThread extends Thread implements Runnable {
	boolean fertig = false;
	int aktuell = -1;
	public static ArrayList<String> sperrDatum = new ArrayList<String>();
	private ImageIcon img,img2;
	private String zeit;
	private int merken = -1;
	SuchenSeite eltern;
	public void init(SuchenSeite xeltern){
		img = SystemConfig.hmSysIcons.get("zuzahlnichtok");
		img2 = SystemConfig.hmSysIcons.get("zuzahlfrei");		
		img.setDescription("gesperrt");
		img2.setDescription("offen");
		eltern = xeltern;
		eltern.setZeit();
		zeit = eltern.getZeit();
		sperrDatum.clear();
		start();
	}
	public void run(){
		int anzahl;
		sperrDatum.clear();
		Vector nvec;
		String sperre;
		while(true){
			anzahl = eltern.sucheDaten.size();
			if( (SuchenSeite.mussUnterbrechen) && (anzahl==0) ){
				System.out.println("Unterbrechen und anzahl = 0");
				break;
			}
			if( (SuchenSeite.mussUnterbrechen) && (anzahl==(this.aktuell-1)) ){
				System.out.println("Unterbrechen und anzahl = this.aktuell == "+this.aktuell);
//				break;
			}

			if(anzahl > 0){
				if(aktuell != (anzahl-1)){
					 	
						aktuell++;
						
						//nvec = (Vector) ((Vector)SuchenSeite.thisClass.sucheDaten.get(aktuell)).clone();
						nvec = (Vector) ((Vector)eltern.sucheDaten.get(aktuell));
						
						
						sperre = (String)((Vector)nvec).get(13)+
											(String)((Vector)nvec).get(14) ; 

						if(sperrDatum.contains(sperre+SystemConfig.dieseMaschine+zeit)){
							nvec.set(1, img2);
						}else{
							//int ret = 0;
							int ret = XSperrenVerarbeiten(aktuell,nvec,zeit);
							if(ret==0){
								sperrDatum.add(sperre+SystemConfig.dieseMaschine+zeit);
							}
							nvec.set(1, (ret==0 ? img2 : img));
						}

						eltern.dtblm.addRow(nvec);
						eltern.jxSucheTable.repaint();
						System.out.println("Aktuell = "+aktuell+" / Merken = "+merken+"/ Anzahl = "+anzahl);

						merken = anzahl;
						try {
							Thread.sleep(10);
							} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}else{
					try {
						//System.out.println("Aktuell = "+aktuell+" / Merken = "+merken+"/ Anzahl = "+anzahl);
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}else{
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		nvec = null;
		sperre = null;
		System.out.println("Thread beendet");
		System.out.println("Variable aktuelle zum Zeitpunkt des ThreadEnde = "+this.aktuell);
		
		//Reha.thisClass.conn.setAutoCommit(false);
		//SuchenSeite.setKnopfGedoense(new int[]  {0,0,0,0,0,0,0,1,1,1});
		//SuchenSeite.getInstance().tabelleEinschalten();
		//SuchenSeite.getInstance().listenerEinschalten();

		//JOptionPane.showMessageDialog(null,"Anzahl Tabellenzeilen: "+SuchenSeite.getInstance().jxSucheTable.getRowCount());
		
	}
	private int XSperrenVerarbeiten(int akt,Vector vecx,String zeit){
		Statement stmtx = null;
		ResultSet rsx = null;

		/*
		try {
			Reha.thisClass.conn.setAutoCommit(true);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/
		String sperre;
		sperre = (String)((Vector)vecx).get(13)+
							(String)((Vector)vecx).get(14) ; 

			//if(! sperrDatum.contains(sperre+SystemConfig.dieseMaschine)){
				stmtx = null;
				rsx = null;
				try {
					stmtx = (Statement) Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					        ResultSet.CONCUR_UPDATABLE );
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					rsx = stmtx.executeQuery("select sperre,maschine from flexlock where sperre='"+sperre+"'");
					if(!rsx.next() ){
						//this.sperrDatum.add(sperre+SystemConfig.dieseMaschine+zeit);
						String st = "insert into flexlock set sperre='"+sperre+"', maschine='"+SystemConfig.dieseMaschine+"', "+
						"zeit='"+zeit+"'";
						stmtx.execute(st);
						//new ExUndHop().setzeStatement(new String(st));
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return(0);
					}else{
						return(1);
						/*
						if(! sperrDatum.contains(sperre+SystemConfig.dieseMaschine+SuchenSeite.getZeit())){
							return(1);
						}else{
							return(0);
						}
						*/

					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
						
			//}
			

			
			if (rsx != null) {
				try {
					rsx.close();
				} catch (SQLException sqlEx) { // ignore }
					System.out.println("Fehler bei ResultSet schliessen");					
					rsx = null;
				}
			}	
			if (stmtx != null) {
				try {
					stmtx.close();
				} catch (SQLException sqlEx) { // ignore }
					System.out.println("Fehler bei Statement schliessen");
					stmtx = null;
				}
			}
					
			return 0;
		}

}
