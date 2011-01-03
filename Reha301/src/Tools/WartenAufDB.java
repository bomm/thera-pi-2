package Tools;

import reha301.Reha301;

public class WartenAufDB {
	public static boolean IsDbOk(){
		boolean bret = true;
		long zeit = System.currentTimeMillis();
		while(!Reha301.DbOk){
			try{
				Thread.sleep(50);
				if( (System.currentTimeMillis()-zeit) > 5000){
					bret = false;
				}
			}catch(Exception ex){
				bret = false;
			}
			
		}
		return bret;
	}
}
