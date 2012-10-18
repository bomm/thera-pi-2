package CommonTools;
import java.util.*;


public final class ZeitFunk {
	static Date zeit1=null;
	static Date zeit2=null;
	static Date datum1;
	static Date datum2;
	static int int1 = 0;
	static int int2 = 0;
	

public static long ZeitDifferenzInMinuten(String szeit1,String szeit2){
/*	
	zeit1 = new Date();
	zeit2 = new Date();
	//Integer.parseInt(stringZahl)
	zeit1.setHours(Integer.parseInt(szeit1.substring(0,2)));
	zeit1.setMinutes(Integer.parseInt(szeit1.substring(3,5)));
	
	
	zeit2.setHours(Integer.parseInt(szeit2.substring(0,2)));
	zeit2.setMinutes(Integer.parseInt(szeit2.substring(3,5)));	
	
	return ((zeit2.getTime()-zeit1.getTime())/(1000*60));
}	
*/
	Calendar c1 = Calendar.getInstance();
	c1.set(Calendar.MONTH, 1);
	c1.set(Calendar.DAY_OF_MONTH, 1);
	c1.set(Calendar.HOUR_OF_DAY,Integer.parseInt(szeit1.substring(0,2)));
	c1.set(Calendar.MINUTE,Integer.parseInt(szeit1.substring(3,5)));
	c1.set(Calendar.SECOND,0);
	c1.set(Calendar.MILLISECOND,0);

	Calendar c2 = Calendar.getInstance();
	c2.set(Calendar.MONTH, 1);
	c2.set(Calendar.DAY_OF_MONTH, 1);
	c2.set(Calendar.HOUR_OF_DAY,Integer.parseInt(szeit2.substring(0,2)));
	c2.set(Calendar.MINUTE,Integer.parseInt(szeit2.substring(3,5)));
	c2.set(Calendar.SECOND,0);
	c2.set(Calendar.MILLISECOND,0);
	return (((c2.getTimeInMillis())-c1.getTimeInMillis())/(1000*60));

	
}

public static long MinutenSeitMitternacht(String szeit1){
	Calendar c1 = Calendar.getInstance();
	c1.set(Calendar.MONTH, 1);
	c1.set(Calendar.DAY_OF_MONTH, 1);
	c1.set(Calendar.HOUR_OF_DAY,Integer.parseInt(szeit1.substring(0,2)));
	c1.set(Calendar.MINUTE,Integer.parseInt(szeit1.substring(3,5)));
	c1.set(Calendar.SECOND,0);
	c1.set(Calendar.MILLISECOND,0);
	
	Calendar c2 = Calendar.getInstance();
	c2.set(Calendar.MONTH, 1);
	c2.set(Calendar.DAY_OF_MONTH, 1);
	c2.set(Calendar.HOUR_OF_DAY,0);
	c2.set(Calendar.MINUTE,0);
	c2.set(Calendar.SECOND,0);
	c2.set(Calendar.MILLISECOND,0);
	return (((c1.getTimeInMillis())-c2.getTimeInMillis())/(1000*60));
}

public static String MinutenZuZeit(int minuten){
	int stunden;
	int dummyminuten;
	String sret = "";
	
	stunden = (minuten/60);
	dummyminuten = minuten % 60;
	 
	////System.out.println("Stunden= "+stunden);
	////System.out.println("Minuten= "+dummyminuten);
	if ((Integer.valueOf(stunden)).toString().length() == 1)
		sret = "0"+ (Integer.toString(stunden));
	else
		sret = (Integer.toString(stunden));
	
	if ((Integer.valueOf(dummyminuten)).toString().length() == 1)
		sret = sret+":0"+(Integer.toString(dummyminuten));
	else
		sret = sret+":"+(Integer.toString(dummyminuten));		
	sret = sret+":00";
	////System.out.println("errechnete Zeit = "+sret);
return sret;	
}

public static void main(String[] argv){
	/*
	long differenz = 0;
	String zeit = "";
	differenz = ZeitDifferenzInMinuten("07:00","12:30");
	//System.out.println("Zeitdifferenz = "+differenz);
	differenz = MinutenSeitMitternacht("02:30:00");	
	//System.out.println("Minuten seit Mitternacht = "+differenz);
	int intzeit = 90;
	zeit = MinutenZuZeit(intzeit);
	//System.out.println("Mitternacht + "+intzeit+" Minuten = Uhrzeit: "+zeit);
	 * 	
	 */
}


}

/*
public static long MinutenSeitMitternacht(String szeit1){
	
	zeit1 = new Date();
	zeit2 = new Date();
	//Integer.parseInt(stringZahl)
	zeit1.setHours(Integer.parseInt(szeit1.substring(0,2)));
	zeit1.setMinutes(Integer.parseInt(szeit1.substring(3,5)));
	zeit1.setSeconds(Integer.parseInt("00"));
	//if (szeit1.length() > 5)
	//zeit1.setSeconds(Integer.parseInt(szeit1.substring(6,8)));
	zeit2.setHours(Integer.parseInt("00"));
	zeit2.setMinutes(Integer.parseInt("00"));
	zeit2.setSeconds(Integer.parseInt("00"));
	////System.out.println("Date-Wert = "+new Date().toString());
	////System.out.println("Gregorian-Wert = "+ Calendar.getInstance());
	
	Calendar c1 = Calendar.getInstance();
	c1.set(Calendar.MONTH, 1);
	c1.set(Calendar.DAY_OF_MONTH, 1);
	c1.set(Calendar.HOUR_OF_DAY,Integer.parseInt(szeit1.substring(0,2)));
	c1.set(Calendar.MINUTE,Integer.parseInt(szeit1.substring(3,5)));
	c1.set(Calendar.SECOND,Integer.parseInt("00"));

	Calendar c2 = Calendar.getInstance();
	c2.set(Calendar.MONTH, 1);
	c2.set(Calendar.DAY_OF_MONTH, 1);
	c2.set(Calendar.HOUR_OF_DAY,0);
	c2.set(Calendar.MINUTE,0);
	c2.set(Calendar.SECOND,0);

	////System.out.println("Gregorian gettime = "+sec1.getTimeInMillis());
	////System.out.println("Date gettime = "+new Date().getTime());
	
	
	////System.out.println("Zeitzone = "+TimeZone.getDefault());
	//System.out.println("Startzeit = Date"+szeit1+" / Minuten seit Mitternacht = "+
			(((zeit1.getTime())-zeit2.getTime())/(1000*60)));
	//System.out.println("Startzeit = Gregorian"+szeit1+" / Minuten seit Mitternacht = "+
			(((c1.getTimeInMillis())-c2.getTimeInMillis())/(1000*60)));

	return (((zeit1.getTime())-zeit2.getTime())/(1000*60));
}
 * 
 */
