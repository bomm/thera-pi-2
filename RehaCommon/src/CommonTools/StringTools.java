package CommonTools;

import java.util.Vector;

import javax.swing.JOptionPane;

public class StringTools {
	public static String TRIGGER_EINRUECKEN = "          ";
	
	public static String EGross(String string){
		if(string == null){
			return "";
		}
		if(string.trim().equals("")){
			return "";
		}

		String test = String.valueOf(string.trim());
		String neuString = "";
		try{
		boolean zerhackt = false;	
		for(int y = 0; y < 1; y++){

			if(  (test.indexOf(" ") < 0)  && (test.indexOf("-") < 0) && (test.indexOf("/") < 0) ){
				neuString = test.substring(0,1).toUpperCase()+
				 (test.length() > 1 ? test.substring(1).toLowerCase() : "");
				test = String.valueOf(neuString.trim());
				/***********/
				if(test.indexOf("Mc") > -1){
					try{
						if(test.length() >=3){
							if(!test.substring(2,3).equals(" ")){
								test = "Mc"+test.substring(2,3).toUpperCase()+test.substring(3).toLowerCase();
							}
						}
					}catch(Exception ex){
						
					}
				}
				/*
				if(test.indexOf("Mac") > -1){
					try{
						if(test.length() >=4){
							if(!test.substring(3,4).equals(" ")){
								test = "Mac"+test.substring(3,4).toUpperCase()+test.substring(4).toLowerCase(); 
							}
						}
					}catch(Exception ex){
						
					}
				}
				*/
				/***********/
				return test;
			}
			
			if(test.indexOf(" ") > -1){
				String[] splitString = test.split(" ");
				for(int i = 0;i < splitString.length;i++){
					neuString = neuString + 
								(splitString[i].substring(0,1).toUpperCase())+
								 (splitString[i].length() > 1 ? splitString[i].substring(1).toLowerCase() : "");
					neuString = neuString + " ";
				}
				test = String.valueOf(neuString.trim());
				zerhackt = true;
			}

			if(test.indexOf(" - ") > -1){
				neuString = "";
				String[] splitString = test.split(" - ");
				for(int i = 0;i < splitString.length;i++){
					neuString = neuString + 
								(splitString[i].substring(0,1).toUpperCase())+
							 (splitString[i].length() > 1 ? splitString[i].substring(1).toLowerCase() : "");
					neuString = neuString + (i < (splitString.length-1) ? " - " : "");
				}
				test = String.valueOf(neuString.trim());
				zerhackt = true;
				break;
			}
			
			if(test.indexOf("-") > -1){
				neuString = "";
				String praefix = "";
				String[] splitString = test.split("-");
				if(splitString[0].indexOf(" ") >= 0){
					praefix = splitString[0].substring(0,splitString[0].lastIndexOf(" ")+1);
					splitString[0] = String.valueOf(splitString[0].substring(splitString[0].lastIndexOf(" ")+1)).trim();
				}
				for(int i = 0;i < splitString.length;i++){
					if(i==0){
						neuString = neuString+praefix;
					}
					neuString = neuString + 
								(splitString[i].substring(0,1).toUpperCase())+
							 (splitString[i].length() > 1 ? splitString[i].substring(1).toLowerCase() : "");
					neuString = neuString + (i < (splitString.length-1) ? "-" : "");
				}
				test = String.valueOf(neuString.trim());
				zerhackt = true;
				//System.out.println("in - Ergebnis = "+test);
			}
			
			
			if(test.indexOf("/") > -1 && !zerhackt){
				neuString = "";
				String[] splitString = test.split("/");
				for(int i = 0;i < splitString.length;i++){
					splitString[i] = splitString[i].trim();

					neuString = neuString +
								(splitString[i].substring(0,1).toUpperCase())+
							 (splitString[i].length() > 1 ? splitString[i].substring(1).toLowerCase() : "");
					neuString = neuString + (i < (splitString.length-1) ? " / " : "");
				}
				test = String.valueOf(neuString.trim());
				//System.out.println(test);
			}
		}
		if(test.indexOf("prof.") > -1){
			neuString = test.replaceAll("prof.", "Prof.");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf("dr.") > -1){
			neuString = test.replaceAll("dr.", "Dr.");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf("dres.") > -1){
			neuString = test.replaceAll("dres.", "Dres.");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf(" Med. ") > -1){
			neuString = test.replaceAll(" Med. ", " med. ");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf("Med.") > -1){
			neuString = test.replaceAll("Med.", "med.");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf(" Von ") > -1){
			neuString = test.replaceAll(" Von ", " von ");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf("Von ") > -1){
			neuString = test.replaceAll("Von ", " von ");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf(" Und ") > -1){
			neuString = test.replaceAll(" Und ", " und ");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf(" Zu ") > -1){
			neuString = test.replaceAll(" Zu ", " zu ");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf(" VON ") > -1){
			neuString = test.replaceAll(" VON ", " von ");
			test = String.valueOf(neuString.trim());
		}
		
		if(test.indexOf(" UND ") > -1){
			neuString = test.replaceAll(" UND ", " und ");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf(" ZU ") > -1){
			neuString = test.replaceAll(" ZU ", " zu ");
			test = String.valueOf(neuString.trim());
		}

		if(test.indexOf(" An ") > -1){
			neuString = test.replaceAll(" An ", " an ");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf(" Am ") > -1){
			neuString = test.replaceAll(" Am ", " am ");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf(" Auf ") > -1){
			neuString = test.replaceAll(" Auf ", " auf ");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf(" Der ") > -1){
			neuString = test.replaceAll(" Der ", " der ");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf(" Bei ") > -1){
			neuString = test.replaceAll(" Bei ", " bei ");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf(" Beim ") > -1){
			neuString = test.replaceAll(" Beim ", " bei ");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf(" Den ") > -1){
			neuString = test.replaceAll(" Den ", " den ");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf(" Dem ") > -1){
			neuString = test.replaceAll(" Dem ", " dem ");
			test = String.valueOf(neuString.trim());
		}
		if(test.indexOf(" Die ") > -1){
			neuString = test.replaceAll(" Die ", " die ");
			test = neuString;
		}
		if(test.indexOf("-Die ") > -1){
			neuString = test.replaceAll("-Die ", "-die ");
			test = neuString;
		}
		if(test.indexOf(" Ob ") > -1){
			neuString = test.replaceAll(" Ob ", " ob ");
			test = neuString;
		}
		if(test.indexOf(" Über ") > -1){
			neuString = test.replaceAll(" Über ", " über ");
			test = neuString;
		}
		if(test.indexOf(" Überm ") > -1){
			neuString = test.replaceAll(" Überm ", " überm ");
			test = neuString;
		}
		if(test.indexOf(" Unter ") > -1){
			neuString = test.replaceAll(" Unter ", " unter ");
			test = neuString;
		}
		if(test.indexOf(" A.d. ") > -1){
			neuString = test.replaceAll(" A.d. ", " a.d. ");
			test = neuString;
		}
		if(test.indexOf(" U.d. ") > -1){
			neuString = test.replaceAll(" U.d. ", " u.d. ");
			test = neuString;
		}
		if(test.indexOf("Aok") > -1){
			neuString = test.replaceAll("Aok", "AOK");
			test = neuString;
		}
		if(test.indexOf("Gek") > -1){
			neuString = test.replaceAll("Gek", "GEK");
			test = neuString;
		}
		if(test.indexOf("Bkk") > -1){
			neuString = test.replaceAll("Bkk", "BKK");
			test = neuString;
		}
		if(test.indexOf("Bek ") > -1){
			neuString = test.replaceAll("Bek ", "BEK ");
			test = neuString;
		}
		if(test.indexOf("Ikk") > -1){
			neuString = test.replaceAll("Ikk", "IKK");
			test = neuString;
		}
		if(test.indexOf("Lkk") > -1){
			neuString = test.replaceAll("Lkk", "LKK");
			test = neuString;
		}
		if(test.indexOf("Tkk") > -1){
			neuString = test.replaceAll("Tkk", "TKK");
			test = neuString;
		}
		if(test.indexOf("Dak") > -1){
			neuString = test.replaceAll("Dak", "DAK");
			test = neuString;
		}
		if(test.indexOf("Ddg") > -1){
			neuString = test.replaceAll("Ddg", "DDG");
			test = neuString;
		}
		if(test.indexOf("str.") > -1){
			neuString = test.replaceAll(" str.", " Str.");
			test = neuString;
		}

		if(test.indexOf(" U. ") > -1){
			neuString = test.replaceAll(" U. ", " u. ");
			test = neuString;
		}

		if(test.indexOf("gesundheitskasse") > -1){
			neuString = test.replaceAll("gesundheitskasse", "Gesundheitskasse");
			test = neuString;
		}
		
		}catch(java.lang.StringIndexOutOfBoundsException ex){
			////System.out.println(ex);
			return ""+test;
		}catch(Exception ex){
			return ""+test;
		}
		
		return test;
		//return neuString.trim();
	}
	
	public static String EGross2(String test){
		String retString = "";
		return retString;
	}
	
	public static String NullTest(String string){
		if(string==null){
			return "";
		}else{
			return string;
		}
	}
	public static int ZahlTest(String string){
		if(string==null){
			return -1;
		}else{
			int zahl;
			try {
				zahl = Integer.valueOf(string.trim());
			}catch(NumberFormatException ex){
				zahl = -1;
			}
			return zahl;
		}
	}
	public static String Escaped(String string){
		String escaped = string.replaceAll("\'", "\\\\'");
		escaped = escaped.replaceAll("\"", "\\\\\"");
		return escaped;
	}
	public static String EscapedDouble(String string){
		String escaped = string.replaceAll("\'", "\\\\\\'");
		return escaped;
	}
	public static String fuelleMitZeichen(String string,String zeichen,boolean vorne,int endlang){
		String orig = string;
		String praefi = zeichen;
		String dummy = "";
		String sret = ""; 
		int solllang = endlang;
		int istlang = orig.length();
		int differenz = solllang - istlang;
		if(differenz > 0){
			for(int i = 0; i < differenz;i++){
				dummy = dummy+praefi;
			}
			if(vorne){
				sret = dummy+orig;
			}else{
				sret = orig+dummy;
			}
		}else{
			sret = orig;
		}
		return sret;
	}
	public static String getDisziplin(String reznr){
		if(reznr.startsWith("KG")){
			return "Physio";
		}else if(reznr.startsWith("MA")){
			return "Massage";
		}else if(reznr.startsWith("ER")){
			return "Ergo";
		}else if(reznr.startsWith("LO")){
			return "Logo";
		}else if(reznr.startsWith("RH")){
			return "Reha";
		}else if(reznr.startsWith("PO")){
			return "Podo";
		}
		return "Physio";
	}
	
	@SuppressWarnings("unchecked")
	public static Vector<String> fliessTextZerhacken(String textcontent,int max_line_lenght,String trenner){
		//Ausgabe in eine Datei schreiben
		/*
		RandomAccessFile file = null;
		Writer out = null;
		try {
			file = new RandomAccessFile("C:/kontroll.txt", "rw");
			out = new OutputStreamWriter(new FileOutputStream(file.getFD()), "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		*/ 
		Vector<String> dtavec = new Vector<String>();
		try{
		String[] teile = textcontent.split(trenner);
		String ohneumbruch = null;
		String LEER = " ";
		String reststring = "";
		String dummy = "";
		int i = 0; int i2 = 0; int i3 = 0;
		for(i = 0; i < teile.length;i++){
			//out.append("0: Eintritt in Uebergeordnete for/next"+System.getProperty("line.separator"));
			ohneumbruch = teile[i].replace("\f","").replace("\r","").replace("\n","").replace("\t"," ");
			reststring = String.valueOf(ohneumbruch);
			if(ohneumbruch.length()==0){
				dtavec.add("");
				//out.append("1: = leer"+System.getProperty("line.separator"));
			}else if(ohneumbruch.length() > 0 && ohneumbruch.length() <= max_line_lenght){
				if(ohneumbruch.trim().length()>0){
					//out.append("2: "+(ohneumbruch.startsWith(TRIGGER_EINRUECKEN) ? ohneumbruch : ohneumbruch.trim())+System.getProperty("line.separator"));
					dummy = testeString( (ohneumbruch.startsWith(TRIGGER_EINRUECKEN) ? ohneumbruch : ohneumbruch.trim()), max_line_lenght);
					//dtavec.add( (ohneumbruch.startsWith(TRIGGER_EINRUECKEN) ? ohneumbruch : ohneumbruch.trim()) );
					dtavec.add( String.valueOf(dummy));	
				}
			//hier neu >= anstatt >
			}else if(ohneumbruch.length() > max_line_lenght){
				//out.append("3: = reststring");
				for(i2 = 0; i2 < reststring.length();i2++){
					//out.append("4.0: Eintritt in Pruefung: "+(reststring.length() < max_line_lenght ? reststring : " Laenge ="+reststring.length() )+System.getProperty("line.separator"));
					if(reststring.length() <= max_line_lenght){
						if(reststring.trim().length() > 0){
							dummy = testeString((reststring.startsWith(TRIGGER_EINRUECKEN) ? reststring : reststring.trim()),max_line_lenght );
							//dtavec.add( (reststring.startsWith(TRIGGER_EINRUECKEN) ? reststring : reststring.trim()) );
							dtavec.add( String.valueOf(dummy));
							//out.append("4.1: "+(ohneumbruch.startsWith(TRIGGER_EINRUECKEN) ? ohneumbruch : ohneumbruch.trim())+System.getProperty("line.separator"));
						}
						break;
					}else{

						for(i3 = max_line_lenght-1; i3 >= 0; i3--){
							//System.out.println("6: untere for/next");
							dummy = "";
							if(reststring.substring(i3,i3+1).equals(LEER)){
								if(reststring.substring(0,i3).trim().length()>0){
									//dtavec.add(reststring.substring(0,i3).trim());
									dummy = testeString((reststring.substring(0,i3).startsWith(TRIGGER_EINRUECKEN) ? reststring.substring(0,i3) : reststring.substring(0,i3).trim()),max_line_lenght );
									//dtavec.add( (reststring.substring(0,i3).startsWith(TRIGGER_EINRUECKEN) ? reststring.substring(0,i3) : reststring.substring(0,i3).trim()) );
									dtavec.add(String.valueOf(dummy));
									//out.append("7: "+(reststring.substring(0,i3).startsWith(TRIGGER_EINRUECKEN) ? reststring.substring(0,i3) : reststring.substring(0,i3).trim())+System.getProperty("line.separator"));
								}
								//reststring = reststring.substring(i3).trim();
								reststring = String.valueOf( (reststring.substring(i3).startsWith(TRIGGER_EINRUECKEN) ? reststring.substring(i3) : reststring.substring(i3).trim()) );
								//out.append("8: "+reststring+System.getProperty("line.separator"));
								if(  (reststring.length() <= max_line_lenght) && (i2 >= reststring.length())){
									//out.append("11!!!!!: Laenge = "+reststring.length()+" / "+
									//		" Stand i2 = "+i2+" / Stand i3 = "+i3+" String = "+reststring+System.getProperty("line.separator"));
									dummy = testeString(reststring,max_line_lenght );
									dtavec.add(String.valueOf(dummy));
									String mes = "Achtung!\n"+
									"Spezielle Fallkonstellation\n"+
									"Reststring = "+reststring+" / Laenge ="+Integer.toString(reststring.length())+"\n"+
									"Stand i3 = "+Integer.toString(i3)+"\n"+
									"Stand i2 = "+Integer.toString(i2)+"\n";
									//JOptionPane.showMessageDialog(null, mes);
								}
								break;
							}else if(i == 0){
								//hier neu >= anstatt >
								if(reststring.length() > max_line_lenght){
									//out.append("9: > max_line_length"+System.getProperty("line.separator"));
									continue;
								}else{
									dummy = testeString(reststring,max_line_lenght);
									//dtavec.add(reststring);
									dtavec.add(String.valueOf(dummy));
									//out.append("10: "+reststring+System.getProperty("line.separator"));
									break;
								}
								
							}
						}
					}
				/********ende der i2 for/next**********/	
				}
			}
		}
		//out.flush();
		//out.close();
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null, "Fehler in Fließtextaufbereitung. Bericht keinesfalls!!!! versenden");
		}
		/*
		for(int i = 0; i < dtavec.size();i++){
			System.out.println("L="+StringTools.fuelleMitZeichen(
					Integer.toString(dtavec.get(i).length()), "0", true, 3)+": "+dtavec.get(i));
		}
		*/
		return (Vector<String>)dtavec.clone();
	}
	public static String testeString(String string,int lang){
		if(string.length() > lang){
			JOptionPane.showMessageDialog(null, "Der String "+string+" ist länger als"+Integer.toString(lang)+" Zeichen\n"+
					"Bericht bitte keinesfalls versenden!!!!!");
		}
		return string;
	}
	public static String do301String(String string){
		String ret = string;
		ret = ret.replace("?", "??").replace("'","?'").replace(":", "?:").replace("+", "?+");
		ret = ret.replace("ü","}").replace("ä","{").replace("ö","|").replace("ß","~");
		ret = ret.replace("Ü","]").replace("Ä","[").replace("Ö","\\");
		ret = ret.replace("„","\"").replace("“","\"");
		
		//ret = ret.replace(":", "?:").replace(",","?,");
		return ret;
	}
	public static String do301NormalizeString(String string){
		String ret = string;
		ret = ret.replace("}","ü").replace("{","ä").replace("|","ö").replace("~","ß");
		ret = ret.replace("]","Ü").replace("[","Ä").replace("\\","Ö");
		ret = ret.replace("??","?").replace("?:",":").replace("?'","'");
		return ret;
	}
	public static int holeZahlVorneNullen(String zahl){
		int ret = 0;
		for(int i = 0; i < zahl.length();i++){
			try{
				if("0123456789".contains(zahl.substring(i,i+1))){
					return Integer.parseInt(zahl.substring(i));
				}
			}catch(Exception ex){
				return 0;
			}
		}
		return ret;
	}
	public static int killLeadingZero(String zahl){
		int ret = 0;
		for(int i = 0; i < zahl.length();i++){
			try{
				if(! "0".contains(zahl.substring(i,i+1))){
					//System.out.println("Zahl = "+zahl.substring(i));
					return Integer.parseInt(zahl.substring(i));
				}
			}catch(Exception ex){
				return 0;
			}
		}
		return ret;
	}
	

}
