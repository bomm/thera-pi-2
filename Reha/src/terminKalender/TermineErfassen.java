package terminKalender;

import hauptFenster.AktiveFenster;
import hauptFenster.Reha;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import sqlTools.SqlInfo;
import stammDatenTools.RezTools;
import systemEinstellungen.SystemConfig;


public class TermineErfassen implements Runnable {
	String scanrez = null;
	Vector alleterm;
	Vector vec = null;
	Vector vec2 = null;
	String copyright = null;
	String heute = null;
	int erstfund = -1;
	String kollege = "";
	boolean firstfound;
	boolean ergebnis = true;
	public static boolean success = true;
	public boolean unter18 = false;
	public boolean vorjahrfrei = false;
	public static int errorint = 0;
	public StringBuffer sbuftermine = new StringBuffer();
	static TermineErfassen thisClass = null;
	public TermineErfassen(String reznr, Vector termvec){
		scanrez = reznr.trim();
		firstfound = false;
		erstfund = -1;
		thisClass = this;

	}
	@Override
	public void run() {
		heute = DatFunk.sHeute();
		copyright = "\u00AE"  ;
		int ret = -1;
		try {
			// Zunächst testen ob der Tab bereits erfaßt war
			if( (ret = testeVerordnung()) == 0){
				
				//termok liefert false wenn der Termin bereits mit dem "copyright"-Zeichen im Terminkalender steht.
				boolean termok = testeTermine(); 
				if(!termok){
					////System.out.println("Rezept steht an diesem Tag nicht im Kalender");
					//JOptionPane.showMessageDialog(null, "Dieses Rezept ist nicht am heutigen Tag im Kalender\n\n"+
							//"Gescannte Rezeptnummer -> "+scanrez);
					//return;
				}
				//System.out.println("Erstfund = "+erstfund);
				if(erstfund >= 0){
					scheibeTermin();
					JComponent patient = AktiveFenster.getFensterAlle("PatientenVerwaltung");
					if(patient != null){
						//System.out.println("in aktualisierung");
						//System.out.println("angezeigt wird aktuell "+Reha.thisClass.patpanel.aktRezept.rezAngezeigt);
						if(Reha.thisClass.patpanel.aktRezept.rezAngezeigt.equalsIgnoreCase(scanrez.trim())){
							try{
								//System.out.println("Ansicht ist gleich aktuellem Rezept");
								Reha.thisClass.patpanel.aktRezept.updateEinzelTermine(sbuftermine.toString());								
							}catch(Exception ex){
								JOptionPane.showMessageDialog(null,"Fehler bei der Aktualisierung der Rezeptansicht");
								ex.printStackTrace();
							}

						
						}
					}
					
				}else{
					String htmlmeldung = "<html><b><font size='5'>Dieses Rezept ist am heutigen Tag nicht im Kalender eingetragen<br><br>"+
					"Gescannte Rezeptnummer -><font color='#ff0000'> "+scanrez+"</font></font></b></html>";
					JOptionPane.showMessageDialog(null, htmlmeldung);
					//public ErrorMail(String text,String comp,String user,String senderadress){
					/*
					new ErrorMail("Rezept am heutigen Tag nicht eingetragen: Rezept = "+scanrez,
							SystemConfig.dieseMaschine.toString(),
							this.kollege,
							SystemConfig.hmEmailIntern.get("Username"),
							"Fehler-Mail");
					*/		
					//System.out.println("Rezept steht an diesem Tag nicht im Kalender");
					setTerminSuccess(false);
					ergebnis = false;
				}
				if(firstfound && scanrez.startsWith("RH")){
					fahreFortMitTerminen();			
				}
			// Tag	
			}else{
				if(ret > 0){
					setTerminSuccess(false);
					ergebnis = false;
					//System.out.println("hier die Fehlerbehandlung einbauen---->Fehler = "+ret);
					switch (ret){
					case 1:
						//System.out.println("Das Rezept nicht in Historie und nicht in Rez-Stamm");
						String htmlmeldung = "<html><b><font size='5'>Das gescannte Rezept -><font size='6' color='#ff0000'> "+scanrez+"<br></font>"+
						"existiert weder im<font color='#ff0000'> aktuellen Rezeptstamm</font><br>noch in der<font color='#ff0000'> Historie</font>"+
						"<br><br>Bitte melden Sie dieses Rezept dem Administrator</font></b></html>";
						JOptionPane.showMessageDialog(null, htmlmeldung );
						/*
						new ErrorMail("Das gescannte Rezept existiert weder im aktuellen Rezeptstamm noch in der Historie.\nRezept ="+scanrez+"\nMitarbeiterspalte:"+this.kollege,
								SystemConfig.dieseMaschine.toString(),
								Reha.aktUser,
								SystemConfig.hmEmailIntern.get("Username"),
								"Fehler-Mail");
						*/
						break;
					case 2:
						JOptionPane.showMessageDialog(null, "<html><b><font size='5'>Dieses Rezept wurde bereits abgerechnet!</font><br><br>"+
								"Das gescannte Rezept -><font size='6' color='#ff0000'> "+scanrez+"<br></font></html>");
						//System.out.println("Das Rezept wurde bereits abgerechnet");
						/*
						new ErrorMail("Das gescannte Rezept ist bereits abgerechnet. Rezept ="+scanrez+"\nMitarbeiterspalte:"+this.kollege,
								SystemConfig.dieseMaschine.toString(),
								Reha.aktUser,
								SystemConfig.hmEmailIntern.get("Username"),
								"Fehler-Mail");
						*/		

						break;
					case 3:
						JOptionPane.showMessageDialog(null, "<html><b><font size='5'>Dieses Rezept wurde am heutigen Tab bereits erfaßt!<br><br>"+
								"Das gescannte Rezept -><font size='6' color='#ff0000'> "+scanrez+"<br></font></html>");
						//System.out.println("Das Rezept wurde an diesen Tag bereits erfaßt");
						/*
						new ErrorMail("Doppelerfassung eines Rezeptes. Rezept ="+scanrez+"\nMitarbeiterspalte:"+this.kollege,
								SystemConfig.dieseMaschine.toString(),
								Reha.aktUser,
								SystemConfig.hmEmailIntern.get("Username"),
								"Fehler-Mail");
						*/		

						break;
					}
				}
			}
		} catch (Exception e) {
			setTerminSuccess(false);
			ergebnis = false;

		}
		//System.out.println("Terminerfassen beendet");
		alleterm = null;
		return;
	}
	private void setTerminSuccess(boolean xsuccess){
		this.success = xsuccess;
	}
	public static boolean getTerminSuccess(){
		return success;
	}
	public static int getTerminError(){
		return errorint;
	}
	/********************/
	public int testeVerordnung() throws Exception{
		vec = SqlInfo.holeSatz("verordn","termine,pos1,pos2,pos3,pos4,hausbes,unter18,jahrfrei,pat_intern,preisgruppe,zzregel,anzahl1,anzahl2,anzahl3,anzahl4,preisgruppe"," rez_nr='"+scanrez+"'",Arrays.asList(new String[]{}));
		if(vec.size()==0){
			vec = SqlInfo.holeSatz("lza","termine"," rez_nr='"+scanrez+"'",Arrays.asList(new String[]{}));
			if(vec.size()==0){
				////System.out.println("Rezept ist weder im aktuellen Rezeptstamm noch in der Historie");
				return 1;
			}else{
				////System.out.println("Das Rezept wurde bereits abgerechnet");
				return 2;
			}
						
		}
		String termine = (String)vec.get(0);
		//Tag ist bereits erfaßt !
		if(termine.contains(DatFunk.sHeute())){
			//JOptionPane.showMessageDialog(null, "Dieser Termin wurde heute bereits erfa�t");
			return 3;
		}
		unter18 =  ( ((String)vec.get(6)).equals("T") ? true : false );
		vorjahrfrei = ( ((String)vec.get(7)).equals("") ? false : true );
		// 0 = Tage ist noch nicht erfaßt
		return 0;
	}
	/********************/	
	public void erfasseTermin(){
		Vector<String> pat_int = SqlInfo.holeSatz("verordn", "pat_intern,anzahl1,termine", "rez_nr='"+scanrez+"'", Arrays.asList(new String[] {}));
		if(pat_int.size()==0){

			return;
		}
	}
	/********************/	
	private boolean testeTermine() throws Exception{
		long zeit1 = System.currentTimeMillis();
		boolean ret;
		/*
		alleterm = new Vector();
		
		alleterm = SqlInfo.holeSaetze("flexkc", " * ", 
				"datum='"+DatFunk.sDatInSQL(heute)+"'", 
				Arrays.asList(new String[] {}));
				
		*/
		alleterm = SqlInfo.holeFelder("select * from flexkc where datum='"+DatFunk.sDatInSQL(DatFunk.sHeute())+"' LIMIT "+Integer.toString(ParameterLaden.maxKalZeile));
		Object[] obj = untersucheTermine();
		String string = null;
		//System.out.println("Rückgabewert der Untersuchung = "+obj[0]);
		if(! (Boolean) obj[0]){
			ret = false;
		}else{
			//System.out.println(obj[4]);
			//System.out.println(copyright.trim());
			//System.out.println("if(!(String)obj[4]).contains(copyright.trim()) "+ obj[4]+" contains("+copyright.trim()+")");
			if( !((String)obj[4]).contains(copyright.trim())){
				this.kollege = String.valueOf((String)obj[1]);
				string = "Rezeptnummer wurde gefunden bei Kollege "+(String)obj[1]+" an Block "+(Integer)obj[2]+" Rezeptnummer:"+(String)obj[3];
				String stmt = " sperre = '"+(String)obj[1]+heute+"'";
				////System.out.println(stmt);
				int gesperrt = SqlInfo.zaehleSaetze("flexlock", stmt);
				//if( gesperrt == 0 ){
					String sblock  = Integer.toString(  (((Integer)obj[2]/5)+1)  );
					/*
					stmt = "Update flexkc set T"+sblock+" = '"+copyright+(String)obj[4]+"' where datum = '"+(String)obj[7]+"' AND "+
						"behandler = '"+(String)obj[1]+"' AND TS"+sblock+" = '"+(String)obj[5]+"' AND T"+sblock+" = '"+(String)obj[4]+
						"' AND N"+sblock+" LIKE '%"+scanrez+"%' LIMIT 1";
					new ExUndHop().setzeStatement(String.valueOf(stmt));
					//System.out.println("Ex und Hopp Statement =\n"+stmt+"\n************");
					*/
					SqlInfo.aktualisiereSatz("flexkc",
							"T"+sblock+" = '"+copyright+(String)obj[4]+"'",
							"datum='"+(String)obj[7]+"' AND "+
							"behandler='"+(String)obj[1]+"' AND TS"+sblock+"='"+(String)obj[5]+"' AND T"+sblock+"='"+(String)obj[4]+
							"' AND N"+sblock+" LIKE '%"+scanrez+"%'"); 

					try{
						String snum = ((String)obj[1]).substring(0, 2);
						int inum;
						if(snum.substring(0,1).equals("0)")){
							inum = Integer.valueOf(snum.substring(1,2))-1;
						}else{
							inum = Integer.valueOf(snum.substring(0,2))-1;						
						}

						JComponent termin = AktiveFenster.getFensterAlle("TerminFenster");
						if(termin != null){
							int ansicht;
							if((ansicht = Reha.thisClass.terminpanel.ansicht) == 0){
								if(Reha.thisClass.terminpanel.getAktuellerTag().equals(DatFunk.sHeute())){
									int iblock = Integer.valueOf(sblock)-1;
									((ArrayList<Vector<String>>)((Vector)Reha.thisClass.terminpanel.getDatenVector()).get(inum)).get(0).set(iblock,copyright+(String)obj[4]);
									Reha.thisClass.terminpanel.ViewPanel.repaint();
								}else{
									//System.out.println("Aktueller Tag = "+Reha.thisClass.terminpanel.getAktuellerTag());
								}
							}else{
								//System.out.println("Ansicht im TK = "+ansicht);
							}
						}
					}catch(Exception ex){
						ex.printStackTrace();
					}

				/*
				}else{
					JOptionPane.showMessageDialog(null, "Die Spalte ist momentan gesperrt, der Termin kann zwar\n"+
					"nicht markiert werden, wird aber im Rezeptstamm erfaßt");
				}
				*/
				ret = true;
			}else{
				
				this.kollege = (String)obj[1];
				//System.out.println("this.kollege = "+(String)obj[1]);
				ret = false;
			}
		}	
		return ret;
	}
	/********************/
	private Object[] untersucheTermine() throws Exception{
		
		int spalten = alleterm.size();
		////System.out.println("eingelesene Spalten = "+spalten);
		int i,y;
		boolean gefunden = false;
		
		Object[] obj = {Boolean.valueOf(false),null,null,null,null,null,null,null};
		for(i=0;i<spalten;i++){
			//System.out.println("Untersuche Terminspalte "+Integer.toString(i+1));
			int bloecke = ((Vector)alleterm.get(0)).size();
			int belegt = Integer.parseInt( (String) ((Vector)alleterm.get(i)).get(bloecke-6) );
			for(y=0;y<belegt;y++){
				//System.out.println("Untersuche Block "+Integer.toString(y+1)+" von "+Integer.toString(belegt));
				//int block = ((y*5)+1);
				if( ((String) ((Vector)alleterm.get(i)).get( ((y*5)+1) )).contains(scanrez) ){
					obj[0] = Boolean.valueOf(true); //gefunden
					obj[1] = (String) ((Vector)alleterm.get(i)).get(bloecke-4) ;//Kollege
					obj[2] = ((y*5)+1);//Blocknummer
					obj[3] = (String) ((Vector)alleterm.get(i)).get( ((y*5)+1) ); // Rezeptnummer
					obj[4] = (String) ((Vector)alleterm.get(i)).get( ((y*5)) ); // Name
					obj[5] = (String) ((Vector)alleterm.get(i)).get( ((y*5))+2 ); // Beginn
					obj[6] = (String) ((Vector)alleterm.get(i)).get( ((y*5)) ); // Name
					obj[7] = (String) ((Vector)alleterm.get(i)).get(bloecke-2) ;//Datum
					//((Vector)alleterm.get(i)).set((y*5), copyright+String.valueOf((String)obj[4]));
					//System.out.println("Gefunden in Spalte "+Integer.toString(i+1)+
					//		" in Block "+Integer.toString(y+1)+" Ergebnis = "+obj[3]);
					gefunden = true;
					erstfund = i;
					break;
				}
			}
			if(gefunden){
				firstfound = true;
				break;
			}
		}
		return obj;
	}

	/********************/
	private void fahreFortMitTerminen()throws Exception{
		int spalten = alleterm.size();
		int mehrstellen = 0;
		boolean termOk = false;
		////System.out.println("eingelesene Spalten = "+spalten);
		int i,y;
		Object[] obj = {Boolean.valueOf(false),null,null,null,null,null,null,null};
		for(i=(erstfund+1);i<spalten;i++){
			int bloecke = ((Vector)alleterm.get(0)).size();
			int belegt = Integer.parseInt( (String) ((Vector)alleterm.get(i)).get(bloecke-6) );
			for(y=0;y<belegt;y++){
				//int block = ((y*5)+1);
				if( ((String) ((Vector)alleterm.get(i)).get( ((y*5)+1) )).contains(scanrez) ){
					obj[0] = Boolean.valueOf(true); //gefunden
					obj[1] = (String) ((Vector)alleterm.get(i)).get(bloecke-4) ;//Kollege
					obj[2] = ((y*5)+1);//Blocknummer
					obj[3] = (String) ((Vector)alleterm.get(i)).get( ((y*5)+1) ); // Rezeptnummer
					obj[4] = (String) ((Vector)alleterm.get(i)).get( ((y*5)) ); // Name
					obj[5] = (String) ((Vector)alleterm.get(i)).get( ((y*5))+2 ); // Beginn
					obj[6] = (String) ((Vector)alleterm.get(i)).get( ((y*5)) ); // Name
					obj[7] = (String) ((Vector)alleterm.get(i)).get(bloecke-2) ;//Datum
					
					if( !((String)obj[4]).contains(copyright.trim())){
						mehrstellen++;
						String stmt = " sperre = '"+(String)obj[1]+heute+"'";
						int gesperrt = SqlInfo.zaehleSaetze("flexlock", stmt);
						String sblock = "";
						//if( gesperrt == 0 ){
							sblock  = Integer.toString((((Integer)obj[2]/5)+1));
							/*
							stmt = "Update flexkc set T"+sblock+" = '"+copyright+(String)obj[4]+"' where datum = '"+(String)obj[7]+"' AND "+
								"behandler = '"+(String)obj[1]+"' AND TS"+sblock+" = '"+(String)obj[5]+"' AND T"+sblock+" = '"+(String)obj[4]+
								"' AND N"+sblock+" LIKE '%"+scanrez+"%' LIMIT 1";
							new ExUndHop().setzeStatement(String.valueOf(stmt));
							*/
							SqlInfo.aktualisiereSatz("flexkc",
							"T"+sblock+" = '"+copyright+(String)obj[4]+"'",
							"datum='"+(String)obj[7]+"' AND "+
							"behandler='"+(String)obj[1]+"' AND TS"+sblock+"='"+(String)obj[5]+"' AND T"+sblock+"='"+(String)obj[4]+
							"' AND N"+sblock+" LIKE '%"+scanrez+"%'"); 

						//}else{
						//}
						try{
							String snum = ((String)obj[1]).substring(0, 2);
							int inum;
							if(snum.substring(0,1).equals("0)")){
								inum = Integer.parseInt(snum.substring(1,2))-1;
							}else{
								inum = Integer.parseInt(snum.substring(0,2))-1;						
							}

							JComponent termin = AktiveFenster.getFensterAlle("TerminFenster");
							if(termin != null){
								int ansicht;
								if((ansicht = Reha.thisClass.terminpanel.ansicht) == 0){
									if(Reha.thisClass.terminpanel.getAktuellerTag().equals(DatFunk.sHeute())){
										if(!termOk){
											termOk = true;
										}
										int iblock = Integer.valueOf(sblock)-1;
										((ArrayList<Vector<String>>)((Vector)Reha.thisClass.terminpanel.getDatenVector()).get(inum)).get(0).set(iblock,copyright+(String)obj[4]);
									}else{
										//System.out.println("Aktueller Tag = "+Reha.thisClass.terminpanel.getAktuellerTag());
									}
								}else{
									//System.out.println("Ansicht im TK = "+ansicht);
								}
							}
						}catch(Exception ex){
							ex.printStackTrace();
						}
					}
				}
			}
		}
		if(termOk){
			Reha.thisClass.terminpanel.ViewPanel.repaint();			
		}
		////System.out.println("Anzahl zusätzlicher Fundstellen = "+mehrstellen);
	}
	
	
	
	/********************/
	private void scheibeTermin() throws Exception{
		int ikoll = (kollege.substring(0,1).equals("0") ?
					Integer.valueOf(kollege.substring(1,2)) :
					Integer.valueOf(kollege.substring(0,2)) 	
					);
		try{
		////System.out.println("Kollegen-Nummer = "+ikoll);
		this.kollege = ParameterLaden.getKollegenUeberDBZeile(ikoll);
		//String termkollege = 
		sbuftermine.setLength(0);
		sbuftermine.toString();
		vec2 = null;
		if(! ((String)vec.get(0)).trim().equals("")){
			sbuftermine.append((String)vec.get(0));
		}
		
		Object[] objTerm = RezTools.BehandlungenAnalysieren(scanrez, false,false,false, ((Vector<String>)vec.clone()),null,this.kollege,DatFunk.sHeute());
		if(objTerm==null){return;}
		if( (Integer)objTerm[1] == RezTools.REZEPT_ABBRUCH ){
			return;
		}else if((Integer)objTerm[1] == RezTools.REZEPT_IST_BEREITS_VOLL){
			String anzahl = "??????"; //TodDo
			String message = "<html><b><font size='5'>Auf dieses Rezept wurden bereits<font size='6' color='#ff0000'> "+anzahl+" </font>Behandlungen durchgeführt!"+
			"<br>Verordnete Menge ist<font size='6' color='#ff0000'> "+vec.get(11)+
			"</font><br>Das Rezept ist somit bereits voll und darf für aktuelle Behandlung nicht mehr<br>"+
			"verwendet werden!!!!<br><br>"+
			"Gescannte Rezeptnummer =<font size='6' color='#ff0000'> "+scanrez+"</font><br><br></html>";
			JOptionPane.showMessageDialog(null, message);
			return;
		}else{
			sbuftermine.append( (String) objTerm[0]);
			if((Integer)objTerm[1] == RezTools.REZEPT_IST_JETZ_VOLL){
				String message = "<html><b><font size='5'>Das Rezept ist jetzt voll"+
				"<br>Rezeptnummer = <font size='6' color='#ff0000'> "+scanrez+"</font><br>"+
				"<br>Bitte das Rezept zur Abrechnung vorbereiten.</font></b></html>";
				JOptionPane.showMessageDialog(null, message);
				try{
					RezTools.fuelleVolleTabelle( scanrez , this.kollege);					
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null,"Fehler beim Aufruf von 'fuelleVolleTabelle'");
				}
			}
		}
		}catch(Exception ex){
			ex.printStackTrace();
		}
			
		

/*******************************/
		//System.out.println("Unter 18 = "+unter18+" Vorjahrfei = "+vorjahrfrei);
		if(!unter18 && !vorjahrfrei){
			//System.out.println("In Variante 1");
			SqlInfo.aktualisiereSatz("verordn", "termine='"+sbuftermine.toString()+"'", "rez_nr='"+scanrez+"'");			
		}else if( (unter18) && (!vorjahrfrei) ){
			/// Testen ob immer noch unter 18 ansonsten ZuZahlungsstatus �ndern;
			//System.out.println("Pat_intern = "+vec.get(9));
			String geboren = DatFunk.sDatInDeutsch(SqlInfo.holePatFeld("geboren","pat_intern='"+vec.get(8)+"'" ));
			//System.out.println("Geboren = "+geboren);
			boolean u18 = DatFunk.Unter18(DatFunk.sHeute(), geboren);
			//System.out.println(u18);
			if(u18){
				//System.out.println("In Variante 2");
				SqlInfo.aktualisiereSatz("verordn", "termine='"+sbuftermine.toString()+"'", "rez_nr='"+scanrez+"'");				
			}else{
				//System.out.println("In Variante 3");
				SqlInfo.aktualisiereSatz("verordn", "termine='"+sbuftermine.toString()+"', zzstatus='2'", "rez_nr='"+scanrez+"'");				
			}

		}else if(!unter18 && vorjahrfrei){
			String bef_dat = SqlInfo.holePatFeld("befreit","pat_intern='"+vec.get(8)+"'" );
			//String bef_dat = datFunk.sDatInDeutsch(SqlInfo.holePatFeld("befreit","pat_intern='"+vec.get(9)+"'" ));
			if(!bef_dat.equals("T")){
				if(DatFunk.DatumsWert("31.12."+vec.get(8)) < DatFunk.DatumsWert(DatFunk.sHeute()) ){
					//System.out.println("In Variante 4");
					SqlInfo.aktualisiereSatz("verordn", "termine='"+sbuftermine.toString()+"', zzstatus='2'", "rez_nr='"+scanrez+"'");
				}else{
					//System.out.println("In Variante 5");
					SqlInfo.aktualisiereSatz("verordn", "termine='"+sbuftermine.toString()+"'", "rez_nr='"+scanrez+"'");					
				}
			}else{
				SqlInfo.aktualisiereSatz("verordn", "termine='"+sbuftermine.toString()+"'", "rez_nr='"+scanrez+"'");
			}
		}else{
			//System.out.println("In Variante 6");
			SqlInfo.aktualisiereSatz("verordn", "termine='"+sbuftermine.toString()+"'", "rez_nr='"+scanrez+"'");			
		}
/*******************************/		


		//String cmd = "update verordn set termine='"+sbuftermine.toString()+"' where rez_nr='"+scanrez+"'";
		//new ExUndHop().setzeStatement(cmd);
	}

	/********************/
	private String macheNeuTermin(String text){
		String ret =
			DatFunk.sHeute()+
			"@"+
			this.kollege+
			"@"+
			text+
			"@"+
			(String)vec.get(1)+
			( ((String)vec.get(2)).trim().equals("") ? "" : ","+ ((String)vec.get(2)) )+
			( ((String)vec.get(3)).trim().equals("") ? "" : ","+ ((String)vec.get(3)) )+
			( ((String)vec.get(4)).trim().equals("") ? "" : ","+ ((String)vec.get(4)) )+
			"@"+
			DatFunk.sDatInSQL(DatFunk.sHeute())+"\n";
		return ret;
			
	}
	public static String macheNeuTermin2(String pos1,String pos2,String pos3,String pos4,String xkollege,String datum){
		String ret =
			datum+
			"@"+
			(xkollege==null ? "" : xkollege)+
			"@"+
			""+
			"@"+
			/*
			pos1 + ( pos1.trim().equals("") || pos2.trim().equals("") ? "" : "," )+ 
			pos2 + ( pos2.trim().equals("") || pos3.trim().equals("") ? "" : "," )+
			pos3 + ( pos3.trim().equals("") || pos4.trim().equals("") ? "" : "," )+
			pos4 +  
			*/
			machePositionsString(Arrays.asList(pos1,pos2,pos3,pos4))+
			"@"+
			DatFunk.sDatInSQL(datum)+"\n";
		return ret;
	}
	private static String machePositionsString(List<String> list){
		String ret = "";
		for(int i = 0; i < list.size();i++){
			if(!list.get(i).equals("")){
				if(i==0){
					ret = ret+list.get(i);
				}else{
					if(ret.length() > 0){
						//erstes element war nicht leer
						ret = ret+","+list.get(i);
					}else{
						ret = ret+list.get(i);
					}
				}
			}
		}
		return String.valueOf(ret);
	}
	

	/***********************************************************************************/

		public static Object[] BehandlungenAnalysieren(String swreznum,
				boolean doppeltOk,boolean xforceDlg,boolean alletermine,
				Vector<String> vecx,Point pt,String datum){
		
		int i,j,count =0;
		boolean doppelBeh = false;
		int doppelBehA = 0, doppelBehB = 0;
		boolean springen = false; // unterdrückt die Anzeige des TeminBestätigenAuswahlFensters
		Vector<BestaetigungsDaten> hMPos= new Vector<BestaetigungsDaten>();
		hMPos.add(new BestaetigungsDaten(false, "./.", 0, 0,false,false));
		hMPos.add(new BestaetigungsDaten(false, "./.", 0, 0,false,false));
		hMPos.add(new BestaetigungsDaten(false, "./.", 0, 0,false,false));
		hMPos.add(new BestaetigungsDaten(false, "./.", 0, 0,false,false));
		Vector<String> vec = null;
		String copyright = "\u00AE"  ;
		StringBuffer termbuf = new StringBuffer();

		int iposindex = -1;
		boolean erstedoppel = true;
		
		Object[] retObj = {null,null,null};

		try{
			// die anzahlen 1-4 werden jetzt zusammenhängend ab index 11 abgerufen
			if(vecx==null){
				vec = SqlInfo.holeSatz("verordn", "termine,pos1,pos2,pos3,pos4,hausbes,unter18,jahrfrei,pat_intern,preisgruppe,zzregel,anzahl1,anzahl2,anzahl3,anzahl4,preisgruppe", "rez_nr='"+swreznum+"'", Arrays.asList(new String[] {}));	
			}else{
				vec = vecx;
			}
			
			if (vec.size() > 0){
				termbuf = new StringBuffer();
				if(alletermine){
					termbuf.append((String) vec.get(0));	
				}
				
				Vector<ArrayList<?>> termine = RezTools.holePosUndAnzahlAusTerminen(swreznum);

				for (i=0;i<=3;i++){
					if(vec.get(1+i).toString().trim().equals("") ){
						hMPos.get(i).hMPosNr = "./.";
						hMPos.get(i).vOMenge = 0;
					}else{
						hMPos.get(i).hMPosNr = String.valueOf(vec.get(1+i));
						hMPos.get(i).vOMenge = Integer.parseInt( (String) vec.get(i+11) );
						hMPos.get(i).vorrangig = (Boolean)((ArrayList<?>)((Vector<?>)termine).get(2)).get(i);
						hMPos.get(i).invOBelegt = true;
					}
					count = 0; // Anzahl bereits bestätigter Termine mit dieser HMPosNr
					if(hMPos.get(i).invOBelegt){
					//if (!hMPos.get(i).hMPosNr.equals("./.")){
						//Vector<ArrayList<?>> termine = RezTools.holePosUndAnzahlAusTerminen(swreznum);
						if ( (iposindex=termine.get(0).indexOf(hMPos.get(i).hMPosNr)) >=0 &&
							(termine.get(0).lastIndexOf(hMPos.get(i).hMPosNr) == iposindex)	){
							//Einzeltermin
							count = Integer.parseInt(termine.get(1).get(termine.get(0).indexOf(hMPos.get(i).hMPosNr)).toString());
						}else if((iposindex=termine.get(0).indexOf(hMPos.get(i).hMPosNr)) >=0 &&
								(termine.get(0).lastIndexOf(hMPos.get(i).hMPosNr) != iposindex)	){
							//Doppeltermin
							if(!erstedoppel){
								doppelBehB = i;	
								count = Integer.parseInt(termine.get(1).get(termine.get(0).lastIndexOf(hMPos.get(i).hMPosNr)).toString());
							}else{
								doppelBehA = i;
								doppelBeh = true;
								erstedoppel = false;
								count = Integer.parseInt(termine.get(1).get(termine.get(0).indexOf(hMPos.get(i).hMPosNr)).toString());
							}
						}
					}
					hMPos.get(i).anzBBT = count; //außerhalb der if-Abfrage i.O. -> dann anzBBT = count(==0)
				}
				
				count = 0; //Prüfen, ob es nur eine HMPos gibt, bei der anzBBT < vOMenge; dann überspringe AuswahlFenster und bestätige diese HMPos
				//alternativ: nur die beiden Doppelbehandlungspositionen sind noch offen
				for (i=0; i<=3; i++){
					if (hMPos.get(i).anzBBT < hMPos.get(i).vOMenge){
						count++;
					}
				}
				if (count == 1){
					for (i=0; i<=3; i++){
						if (hMPos.get(i).anzBBT < hMPos.get(i).vOMenge){
							hMPos.get(i).best = true;
							springen = true;
							break;
						}
					}
				}else if ((count >= 2) && doppelBeh && (hMPos.get(doppelBehA).anzBBT < hMPos.get(doppelBehA).vOMenge)){
						hMPos.get(doppelBehA).best = true;
						hMPos.get(doppelBehB).best = true;
						springen = true; // false: Auswalfenster bei Doppelbehandlungen trotzdem anzeigen
				}

				count = 0; // Prüfen, ob alle HMPos bereits voll bestätigt sind
				for (i=0; i<=3; i++){
					if (hMPos.get(i).anzBBT == hMPos.get(i).vOMenge){
						if(hMPos.get(i).vorrangig){
							count=4;
							break;
						}
						hMPos.get(i).best = false;
						count++;
					}
				}
				
				//Testen ob beide oder auch nur eine der Doppelbehandlungen voll ist.
				
				if(doppelBeh){
					 
					int max = welcheIstMaxInt(hMPos.get(doppelBehA).vOMenge, hMPos.get(doppelBehB).vOMenge);
					if( max==1 && hMPos.get(doppelBehA).anzBBT == hMPos.get(doppelBehA).vOMenge ){
						count = 4;
					}
					if( max==2 && hMPos.get(doppelBehB).anzBBT == hMPos.get(doppelBehB).vOMenge ){
						count = 4;
					}
					if( max==0 && hMPos.get(doppelBehB).anzBBT == hMPos.get(doppelBehB).vOMenge){
						count = 4;
					}
				}
				
				if (count == 4){
					//Rezept ist bereit jetzt voll neuer Termin nicht möglich
					retObj[0] = termbuf.toString();
					retObj[1] = 2; //bereits voll normalfall
					return 	retObj.clone();								
				}

				count = 0; // Prüfen, ob eine oder mehrere HMPos bereits übervoll bestätigt sind
				for (i=0; i<=3; i++){
					if (hMPos.get(i).anzBBT > hMPos.get(i).vOMenge){
						hMPos.get(i).best = false;
						count++;
					}
				}
				if (count !=0){
					retObj[0] = termbuf.toString();
					retObj[1] = 3; //bereits übervoll
					return 	retObj.clone();								

				}
				// TerminBestätigenAuswahlFenster anzeigen oder überspringen
				if (xforceDlg || (!springen && (Boolean)SystemConfig.hmTerminBestaetigen.get("dlgzeigen") ) ){
							
							TerminBestaetigenAuswahlFenster termBestAusw = new TerminBestaetigenAuswahlFenster(Reha.thisFrame,null,(Vector<BestaetigungsDaten>)hMPos,swreznum,Integer.parseInt((String)vec.get(15)));
							termBestAusw.pack();
							if(pt==null){
								termBestAusw.setLocationRelativeTo(null);
							}else{
								termBestAusw.setLocation(pt);
							}
							//
							termBestAusw.setzeFocus();
							termBestAusw.setModal(true);
							termBestAusw.setVisible(true);
							
							
				}else{
					/*
					 * Der Nutzer wünscht kein Auswahlfenster:
					 * bestätige alle noch offenen Heilmittel
					 *   
					 */		
					for (i=0; i<=3; i++){
						hMPos.get(i).best = (hMPos.get(i).anzBBT < hMPos.get(i).vOMenge);
						//hMPos.get(i).anzBBT += 1;
					}
				}

				count = 0; // Dialog abgebrochen

				for (i=0; i < 4 ; i++){
					count += (hMPos.get(i).best ? 1 : 0);
				}
				if (count == 0){
					retObj[0] = termbuf.toString();
					retObj[1] = 4; //abgebrochen
					return 	retObj.clone();								

				}
				
				count = 0; // Prüfe, ob der oder die letzten offene(n) Termin(e) bestätigt werden sollen: Hinweis, dass VO abgerechnet werden kann und in VolleTabelle schreiben
				for (i=0; i<=3; i++){
					if ((hMPos.get(i).anzBBT + (hMPos.get(i).best ? 1 : 0)) == hMPos.get(i).vOMenge){
						hMPos.get(i).anzBBT += 1; //wird unten nochmals ausgewertet
						count++;
					}	
					if (count == 4){
						try{
							RezTools.fuelleVolleTabelle(swreznum, (thisClass == null ?  "" : thisClass.kollege) );	
						}catch(Exception ex){
							JOptionPane.showMessageDialog(null,"Fehler beim Aufruf von 'fuelleVolleTabelle'");
						}						

						termbuf.append(TermineErfassen.macheNeuTermin2(
								(String) (hMPos.get(0).best ? vec.get(1) : ""),
								(String) (hMPos.get(1).best ? vec.get(2) : ""),
								(String) (hMPos.get(2).best ? vec.get(3) : ""),
								(String) (hMPos.get(3).best ? vec.get(4) : ""),
								(thisClass == null ?  null : thisClass.kollege),
								datum));
						//hier zunächst den neuen Termin basteln;
						retObj[0] = termbuf.toString();
						retObj[1] = 0; //normalfall mind. eine Bahndlung konnte noch eingetragen werden und jetz Rezept voll
						return retObj.clone(); 
					}
				}
				termbuf.append(TermineErfassen.macheNeuTermin2(
						(String) (hMPos.get(0).best ? vec.get(1) : ""),
						(String) (hMPos.get(1).best ? vec.get(2) : ""),
						(String) (hMPos.get(2).best ? vec.get(3) : ""),
						(String) (hMPos.get(3).best ? vec.get(4) : ""),
						(thisClass == null ?  null : thisClass.kollege),
						datum));
				//hier zunächst den neuen Termin basteln;
				retObj[0] = termbuf.toString();
				//dann nochmal testen ob ein vorrangiges Heilmittel die Mengengrenze erreicht hat.
				for (i=0; i<=3; i++){
					//System.out.println(hMPos.get(i).anzBBT+" / "+hMPos.get(i).vOMenge);
					if (hMPos.get(i).anzBBT == hMPos.get(i).vOMenge){
						if(hMPos.get(i).vorrangig){
							retObj[1] = 0;
							return retObj.clone();	
						}
					}
				}						
				retObj[1] = -1; //Behandlung(en) konnten eingetragen werden Rezept hat noch Luft nach oben
				return 	retObj.clone();								

			}	
			}catch(Exception ex){				
				ex.printStackTrace();
			}finally{
				vec = null;
				hMPos = null;					
			}
			retObj[0] = termbuf.toString();
			retObj[1] = 5; //Rezept wurde nicht gefunden
			return 	retObj.clone();								

		
	}
		private static int welcheIstMaxInt(int i1,int i2){
			if(i1 > i2){return 1;}
			if(i1==i2){return 0;}
			return 2;
		}
}
/************************************/

