package kvKarte;

import systemEinstellungen.SystemConfig;

public class KVKWrapper {
	public String sReaderName = "";
	public KVKWrapper(String readerName){
		sReaderName = readerName;
	}
	public int KVK_Einlesen(){
		int ret = -1;
		if(! SystemConfig.hmKVKDaten.isEmpty()){
			return 0;
		}else{
			
		}
		/*
		if(Reha.osVersion.contains("Windows")){

			if(sReaderName.equals("Chipdrive micro 100")){
				Win_ChipDriveMicro100 wcm = new Win_ChipDriveMicro100();
				ret = wcm.patientEinlesen();
				return ret;
			}
			if(sReaderName.equals("SCM-Micro SCR3xx-Serie")){
				Win_ChipDriveMicro100 wcm = new Win_ChipDriveMicro100();
				ret = wcm.patientEinlesen();
				return ret;
			}
		}
		*/
		return ret;		
	}
}
