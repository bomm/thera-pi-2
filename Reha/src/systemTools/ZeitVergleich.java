package systemTools;
import java.util.*;


final class ZeitVergleich {
	static Date zeit1=null;
	static Date zeit2=null;
	static Date datum1;
	static Date datum2;
	static int int1 = 0;
	static int int2 = 0;
	


	static boolean ZeitGroesserGleich(String szeit1,String szeit2){
		long z1 = MinutenSeitMitternacht(szeit1);
		long z2 = MinutenSeitMitternacht(szeit2);
		return( z2 >= z1 ? true : false);
	}
	static boolean ZeitGroesser(String szeit1,String szeit2){
		long z1 = MinutenSeitMitternacht(szeit1);
		long z2 = MinutenSeitMitternacht(szeit2);
		return( z2 > z1 ? true : false);
	}
	static boolean ZeitKleinerGleich(String szeit1,String szeit2){
		long z1 = MinutenSeitMitternacht(szeit1);
		long z2 = MinutenSeitMitternacht(szeit2);
		return( z2 <= z1 ? true : false);
	}
	static boolean ZeitKleiner(String szeit1,String szeit2){
		long z1 = MinutenSeitMitternacht(szeit1);
		long z2 = MinutenSeitMitternacht(szeit2);
		return( z2 < z1 ? true : false);
	}
	static boolean PasstZwischen(String sgrenzeklein,String sgrenzegross,String szeit,int dauer){
		long z1 = MinutenSeitMitternacht(sgrenzeklein);
		long z2 = MinutenSeitMitternacht(sgrenzegross);
		long z3 = MinutenSeitMitternacht(szeit)+Long.parseLong(Integer.toString(dauer));
		if(((z3 >= z1) &&  (z3<=z2))){
			return true;
		}else{
			return false;
		}
		//return( ((z3 >= z1) &&  (z3<=z2)) ? true : false);
	}
	
	static long ZeitDifferenzInMinuten(String szeit1,String szeit2){
		zeit1 = new Date();
		zeit2 = new Date();
		//Integer.parseInt(stringZahl)
		zeit1.setHours(Integer.parseInt(szeit1.substring(0,2)));
		zeit1.setMinutes(Integer.parseInt(szeit1.substring(3,5)));
		
		
		zeit2.setHours(Integer.parseInt(szeit2.substring(0,2)));
		zeit2.setMinutes(Integer.parseInt(szeit2.substring(3,5)));	
		
		return ((zeit2.getTime()-zeit1.getTime())/(1000*60));
	}

	static long MinutenSeitMitternacht(String szeit1){
		zeit1 = new Date();
		zeit2 = new Date();
		//Integer.parseInt(stringZahl)
		zeit1.setHours(Integer.parseInt(szeit1.substring(0,2)));
		zeit1.setMinutes(Integer.parseInt(szeit1.substring(3,5)));
		if (szeit1.length() > 5)
		zeit1.setSeconds(Integer.parseInt(szeit1.substring(6,8)));
		zeit2.setHours(Integer.parseInt("00"));
		zeit2.setMinutes(Integer.parseInt("00"));
		zeit2.setSeconds(Integer.parseInt("00"));	
		return (((zeit1.getTime())-zeit2.getTime())/(1000*60));
	}



	static String MinutenZuZeit(int minuten){
		int stunden;
		int dummyminuten;
		String sret = "";
		String sdummy = "";
		stunden = (minuten/60);
		dummyminuten = minuten % 60;
		sdummy = ""; 
		//System.out.println("Stunden= "+stunden);
		//System.out.println("Minuten= "+dummyminuten);
		if ((new Integer(stunden)).toString().length() == 1)
			sret = "0"+ (Integer.toString(stunden));
		else
			sret = (Integer.toString(stunden));
		
		if ((new Integer(dummyminuten)).toString().length() == 1)
			sret = sret+":0"+(Integer.toString(dummyminuten));
		else
			sret = sret+":"+(Integer.toString(dummyminuten));		
		sret = sret+":00";
		
	return sret;	
	}

	public static void main(String[] argv){
		long differenz = 0;
		String zeit = "";
		differenz = ZeitDifferenzInMinuten("07:00","12:30");
		System.out.println("Zeitdifferenz = "+differenz);
		differenz = MinutenSeitMitternacht("02:30:00");	
		System.out.println("Minuten seit Mitternacht = "+differenz);
		int intzeit = 90;
		zeit = MinutenZuZeit(intzeit);
		System.out.println("Mitternacht + "+intzeit+" Minuten = Uhrzeit: "+zeit);	
	}

}