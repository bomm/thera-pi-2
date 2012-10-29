package CommonTools;

import java.io.InputStream;
import java.util.Arrays;

public class INITool {
	static String[] dbInis = null;
	
	public static void setDBInis(String[] xdbini){
		dbInis = xdbini;
	}
	public static String[] getDBInis(){
		return dbInis;
	}
	public static INIFile openIni(String path,String iniToOpen){
		INIFile inif = null;
		try{
			if(Arrays.asList(dbInis).contains(iniToOpen)){
				InputStream stream = SqlInfo.liesIniAusTabelle(iniToOpen);
				inif = new INIFile(stream,iniToOpen);
			}else{
				inif = new INIFile(path+iniToOpen);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return inif;
	}
	/************************/
	public static boolean saveIni(INIFile iniToSave){
		boolean ret = false;
		try{
			if(Arrays.asList(dbInis).contains(iniToSave.getFileName())){
				SqlInfo.schreibeIniInTabelle(iniToSave.getFileName(),iniToSave.saveToStringBuffer().toString().getBytes());
				iniToSave.getInputStream().close();
				iniToSave = null;
			}else{
				iniToSave.save();
			}
			ret = true;
		}catch(Exception ex){
			ex.printStackTrace();
		}	
		return ret;
	}
}
