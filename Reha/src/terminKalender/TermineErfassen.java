package terminKalender;

import hauptFenster.AktiveFenster;
import hauptFenster.Reha;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import errorMail.ErrorMail;




import rehaInternalFrame.JPatientInternal;
import rehaInternalFrame.JTerminInternal;
import sqlTools.ExUndHop;
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
	public StringBuffer sbuftermine;
	public TermineErfassen(String reznr, Vector termvec){
		scanrez = reznr.trim();
		firstfound = false;
		erstfund = -1;

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
				System.out.println("Erstfund = "+erstfund);
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
					new ErrorMail("Rezept am heutigen Tag nicht eingetragen: Rezept = "+scanrez,
							SystemConfig.dieseMaschine.toString(),
							this.kollege,
							SystemConfig.hmEmailIntern.get("Username"),
							"Fehler-Mail");
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
						new ErrorMail("Das gescannte Rezept existiert weder im aktuellen Rezeptstamm noch in der Historie.\nRezept ="+scanrez+"\nMitarbeiterspalte:"+this.kollege,
								SystemConfig.dieseMaschine.toString(),
								Reha.aktUser,
								SystemConfig.hmEmailIntern.get("Username"),
								"Fehler-Mail");

						break;
					case 2:
						JOptionPane.showMessageDialog(null, "<html><b><font size='5'>Dieses Rezept wurde bereits abgerechnet!</font><br><br>"+
								"Das gescannte Rezept -><font size='6' color='#ff0000'> "+scanrez+"<br></font></html>");
						//System.out.println("Das Rezept wurde bereits abgerechnet");
						new ErrorMail("Das gescannte Rezept ist bereits abgerechnet. Rezept ="+scanrez+"\nMitarbeiterspalte:"+this.kollege,
								SystemConfig.dieseMaschine.toString(),
								Reha.aktUser,
								SystemConfig.hmEmailIntern.get("Username"),
								"Fehler-Mail");

						break;
					case 3:
						JOptionPane.showMessageDialog(null, "<html><b><font size='5'>Dieses Rezept wurde am heutigen Tab bereits erfaßt!<br><br>"+
								"Das gescannte Rezept -><font size='6' color='#ff0000'> "+scanrez+"<br></font></html>");
						//System.out.println("Das Rezept wurde an diesen Tag bereits erfaßt");
						new ErrorMail("Doppelerfassung eines Rezeptes. Rezept ="+scanrez+"\nMitarbeiterspalte:"+this.kollege,
								SystemConfig.dieseMaschine.toString(),
								Reha.aktUser,
								SystemConfig.hmEmailIntern.get("Username"),
								"Fehler-Mail");

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
		vec = SqlInfo.holeSatz("verordn","termine,anzahl1,pos1,pos2,pos3,pos3,hausbes,unter18,jahrfrei,pat_intern,preisgruppe,zzregel"," rez_nr='"+scanrez+"'",Arrays.asList(new String[]{}));
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
		unter18 =  ( ((String)vec.get(7)).equals("T") ? true : false );
		vorjahrfrei = ( ((String)vec.get(8)).equals("") ? false : true );
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
				this.kollege = new String((String)obj[1]);
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
					new ExUndHop().setzeStatement(new String(stmt));
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
							inum = new Integer(snum.substring(1,2))-1;
						}else{
							inum = new Integer(snum.substring(0,2))-1;						
						}

						JComponent termin = AktiveFenster.getFensterAlle("TerminFenster");
						if(termin != null){
							int ansicht;
							if((ansicht = Reha.thisClass.terminpanel.ansicht) == 0){
								if(Reha.thisClass.terminpanel.getAktuellerTag().equals(DatFunk.sHeute())){
									int iblock = new Integer(sblock)-1;
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
					//((Vector)alleterm.get(i)).set((y*5), copyright+new String((String)obj[4]));
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
							new ExUndHop().setzeStatement(new String(stmt));
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
										int iblock = new Integer(sblock)-1;
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
		////System.out.println("Eintritt in schreibeTermin");
		int ikoll = (kollege.substring(0,1).equals("0") ?
					new Integer(kollege.substring(1,2)) :
					new Integer(kollege.substring(0,2)) 	
					);
		////System.out.println("Kollegen-Nummer = "+ikoll);
		this.kollege = ParameterLaden.getKollegenUeberDBZeile(ikoll);
		//String termkollege = 
		sbuftermine = new StringBuffer();
		vec2 = null;
		String terminneu = "";
		if(! ((String)vec.get(0)).trim().equals("")){
			
			sbuftermine.append((String)vec.get(0));
			//System.out.println("****Beginn Termine bisher****\n"+sbuftermine.toString()+"****Ende Termine****");
			////System.out.println("termine bisher = "+sbuftermine.toString());
			//hier die Einzelnen Termin holen
			vec2 = RezTools.splitteTermine(sbuftermine.toString());
			int anzahl = vec2.size();
			int lautrezept = new Integer((String)vec.get(1)); 
			if(anzahl >= lautrezept){
				String message = "";
				int variante = -1;
				if(anzahl == lautrezept){
					variante = 0;
					/*
						JOptionPane.showMessageDialog(null, "<html><b><font size='5'>Dieses Rezept wurde am heutigen Tab bereits erfaßt!<br><br>"+
								"Das gescannte Rezept -><font size='6' color='#ff0000'> "+scanrez+"<br></font></html>");
					 
					 */
					message = "<html><b><font size='5'>Auf dieses Rezept wurden bereits<font size='6' color='#ff0000'> "+anzahl+" </font>Behandlungen durchgeführt!"+
					"<br>Verordnete Menge ist<font size='6' color='#ff0000'> "+lautrezept+
					"</font><br>Das Rezept ist somit bereits voll und darf für aktuelle Behandlung nicht mehr<br>"+
					"verwendet werden!!!!<br><br>"+
					"Gescannte Rezeptnummer =<font size='6' color='#ff0000'> "+scanrez+"</font><br>"+
					"Wollen Sie die aktuelle Behandlung trotzdem auf dieses Rezept buchen?</font></html>";
				}else if(anzahl > lautrezept){
					variante = 1;
					message = "<html><b><font size='5'>Auf dieses Rezept wurden bereits<font size='6' color='#ff0000'> "+anzahl+" </font>Behandlungen durchgeführt!"+
					"<br>Verordnete Menge ist<font size='6' color='#ff0000'> "+lautrezept+"</font><br>"+
					"<br>Das Rezept ist somit bereits <font size='7'> übervoll</font> und darf für aktuelle Behandlung nicht mehr<br>"+
					"verwendet werden!!!!<br><br>"+
					"Wollen Sie die aktuelle Behandlung trotzdem auf dieses Rezept buchen?</font></html>";
				}
				int frage = JOptionPane.showConfirmDialog(null, message, "Benutzeranfrage", JOptionPane.YES_NO_OPTION);
				if(variante > 0){
					new ErrorMail("Hinweis das Rezept ist bereits"+(variante==0 ? " voll. " : " übervoll!!! ")+" Rezept ="+scanrez+"\nMitarbeiterspalte:"+this.kollege,
							SystemConfig.dieseMaschine.toString(),
							Reha.aktUser,
							SystemConfig.hmEmailIntern.get("Username"),
							"Fehler-Mail!!!!!");
					
				}
				if(frage == JOptionPane.NO_OPTION){
				  return;
				}
				terminneu = macheNeuTermin("zu viele Behandlungen");
				new ErrorMail("Trotz Hinweis!!! Aufnahme in das Rezept. Rezept ist aber bereits"+(variante==0 ? " voll. " : " übervoll!!! ")+" Rezept ="+scanrez+"\nMitarbeiterspalte:"+this.kollege,
						SystemConfig.dieseMaschine.toString(),
						Reha.aktUser,
						SystemConfig.hmEmailIntern.get("Username"),
						"Fehler-Mail!!!!!");
				
			}else{
				////System.out.println("Es sind noch Termine Frei");
				terminneu = macheNeuTermin("");
				if(anzahl == (lautrezept-1)){
					String message = "<html><b><font size='5'>Das Rezept ist jetzt voll"+
					"<br>Rezeptnummer = <font size='6' color='#ff0000'> "+scanrez+"</font><br>"+
					"<br>Bitte das Rezept zur Abrechnung vorbereiten.</font></b></html>";
					JOptionPane.showMessageDialog(null, message);

					new ErrorMail("Letzte Behandlung Rezept = voll.\nRezept ="+scanrez+"\nMitarbeiterspalte:"+this.kollege,
							SystemConfig.dieseMaschine.toString(),
							Reha.aktUser,
							SystemConfig.hmEmailIntern.get("Username"),
							"Rezept voll!!!!!");
				}
			}
				
		}else{
			////System.out.println("der Termin ist der erste Termin.");
			terminneu = macheNeuTermin("");			
		}
		sbuftermine.append(terminneu);

/*******************************/
		System.out.println("Unter 18 = "+unter18+" Vorjahrfei = "+vorjahrfrei);
		if(!unter18 && !vorjahrfrei){
			//System.out.println("In Variante 1");
			SqlInfo.aktualisiereSatz("verordn", "termine='"+sbuftermine.toString()+"'", "rez_nr='"+scanrez+"'");			
		}else if( (unter18) && (!vorjahrfrei) ){
			/// Testen ob immer noch unter 18 ansonsten ZuZahlungsstatus �ndern;
			//System.out.println("Pat_intern = "+vec.get(9));
			String geboren = DatFunk.sDatInDeutsch(SqlInfo.holePatFeld("geboren","pat_intern='"+vec.get(9)+"'" ));
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
			String bef_dat = SqlInfo.holePatFeld("befreit","pat_intern='"+vec.get(9)+"'" );
			//String bef_dat = datFunk.sDatInDeutsch(SqlInfo.holePatFeld("befreit","pat_intern='"+vec.get(9)+"'" ));
			if(!bef_dat.equals("T")){
				if(DatFunk.DatumsWert("31.12."+vec.get(9)) < DatFunk.DatumsWert(DatFunk.sHeute()) ){
					//System.out.println("In Variante 4");
					SqlInfo.aktualisiereSatz("verordn", "termine='"+sbuftermine.toString()+"', zzstatus='2'", "rez_nr='"+scanrez+"'");
				}else{
					//System.out.println("In Variante 5");
					SqlInfo.aktualisiereSatz("verordn", "termine='"+sbuftermine.toString()+"'", "rez_nr='"+scanrez+"'");					
				}
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
			(String)vec.get(2)+
			( ((String)vec.get(3)).trim().equals("") ? "" : ","+ ((String)vec.get(3)) )+
			( ((String)vec.get(4)).trim().equals("") ? "" : ","+ ((String)vec.get(3)) )+
			( ((String)vec.get(5)).trim().equals("") ? "" : ","+ ((String)vec.get(3)) )+
			"@"+
			DatFunk.sDatInSQL(DatFunk.sHeute())+"\n";
		return ret;
			
	}
	
}
