package preisListenHandling;

import java.util.Vector;

import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemTools.StringTools;
import terminKalender.ParameterLaden;

public class MachePreisListe {
	
	public MachePreisListe(String plName){
	String machetabelle = 
		"CREATE TABLE IF NOT EXISTS "+plName+" ("+
			  "LEISTUNG varchar(35) DEFAULT NULL,"+
			  "KUERZEL varchar(10) DEFAULT NULL,"+
			  "T_POS varchar(10) DEFAULT NULL,"+
			  "T_AKT decimal(12,2) DEFAULT \"0.00\" ,"+
			  "T_ALT decimal(12,2) DEFAULT \"0.00\","+
			  "T_PROZ decimal(6,2) DEFAULT \"0.00\","+
			  "T_PAUSCHAL decimal(6,2) DEFAULT \"0.00\","+
			  "ZUZAHLUNG enum(\"T\",\"F\") DEFAULT \"F\","+
			  "ZZART tinyint(4) DEFAULT 0,"+
			  //"ID int(11) NOT NULL AUTO_INCREMENT,"+
			  "ID int(11) DEFAULT 0)"+
			  //"PRIMARY KEY (ID)"+
			  //") ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1";
				" ENGINE=MyISAM DEFAULT CHARSET=latin1";
	SqlInfo.sqlAusfuehren(StringTools.EscapedDouble(machetabelle));
	}
	/*
	public static void preiseFuellenNeu(){
		String[] tarifeDb = {"kgtarif","matarif","ertarif","lotarif","rhtarif"};
		int lang = ParameterLaden.vKGPreise.size();
		Vector<Vector<String>> vec = ParameterLaden.vKGPreise;
		String preis_akt = "";
		String preis_alt = "";
		for(int i = 1; i < lang  ;i++){
			for(int i2 = 1; i2 < 6;i2++){
				preis_akt = (vec.get(i).get(2+((i2-1)*4)+1).trim().equals("")? "0.00" :
					vec.get(i).get(2+((i2-1)*4)+1).trim());
				preis_alt = (vec.get(i).get(2+((i2-1)*4)+2).trim().equals("")? "0.00" :
					vec.get(i).get(2+((i2-1)*4)+2).trim());
				if(!preis_akt.equals("0.00")){
					String cmd = "insert into "+"kgtarif"+Integer.toString(i2)+" set leistung='"+
					vec.get(i).get(0)+"', kuerzel='"+vec.get(i).get(1)+"', t_pos='"+
					vec.get(i).get(2+((i2-1)*4))+"', t_akt='"+preis_akt+"', "+
					"t_alt='"+preis_alt+"', id='"+vec.get(i).get(35)+"'";
					//System.out.println(cmd);
					SqlInfo.sqlAusfuehren(cmd);
				}
			}
			 
			
		}
		for(int i = 1; i < 6; i++){
			SqlInfo.sqlAusfuehren("ALTER TABLE "+"kgtarif"+i+" ADD PRIMARY KEY (ID)"); 
			SqlInfo.sqlAusfuehren("ALTER TABLE "+"kgtarif"+i+" CHANGE ID ID INT( 11 ) NOT NULL AUTO_INCREMENT");
		}
		
		
		lang = ParameterLaden.vMAPreise.size();
		vec = ParameterLaden.vMAPreise;
		for(int i = 1; i < lang  ;i++){
			for(int i2 = 1; i2 < 6;i2++){
				preis_akt = (vec.get(i).get(2+((i2-1)*4)+1).trim().equals("")? "0.00" :
					vec.get(i).get(2+((i2-1)*4)+1).trim());
				preis_alt = (vec.get(i).get(2+((i2-1)*4)+2).trim().equals("")? "0.00" :
					vec.get(i).get(2+((i2-1)*4)+2).trim());
				if(!preis_akt.equals("0.00")){
					String cmd = "insert into "+"matarif"+Integer.toString(i2)+" set leistung='"+
					vec.get(i).get(0)+"', kuerzel='"+vec.get(i).get(1)+"', t_pos='"+
					vec.get(i).get(2+((i2-1)*4))+"', t_akt='"+preis_akt+"', "+
					"t_alt='"+preis_alt+"', id='"+vec.get(i).get(35)+"'";
					//System.out.println(cmd);
					SqlInfo.sqlAusfuehren(cmd);
				}
			}
		}
		for(int i = 1; i < 6; i++){
			SqlInfo.sqlAusfuehren("ALTER TABLE "+"matarif"+i+" ADD PRIMARY KEY (ID)"); 
			SqlInfo.sqlAusfuehren("ALTER TABLE "+"matarif"+i+" CHANGE ID ID INT( 11 ) NOT NULL AUTO_INCREMENT");
		}

		lang = ParameterLaden.vERPreise.size();
		vec = ParameterLaden.vERPreise;
		for(int i = 1; i < lang  ;i++){
			for(int i2 = 1; i2 < 6;i2++){
				preis_akt = (vec.get(i).get(2+((i2-1)*4)+1).trim().equals("")? "0.00" :
					vec.get(i).get(2+((i2-1)*4)+1).trim());
				preis_alt = (vec.get(i).get(2+((i2-1)*4)+2).trim().equals("")? "0.00" :
					vec.get(i).get(2+((i2-1)*4)+2).trim());
				if(!preis_akt.equals("0.00")){
					String cmd = "insert into "+"ertarif"+Integer.toString(i2)+" set leistung='"+
					vec.get(i).get(0)+"', kuerzel='"+vec.get(i).get(1)+"', t_pos='"+
					vec.get(i).get(2+((i2-1)*4))+"', t_akt='"+preis_akt+"', "+
					"t_alt='"+preis_alt+"', id='"+vec.get(i).get(35)+"'";
					//System.out.println(cmd);
					SqlInfo.sqlAusfuehren(cmd);
				}
			}
		}
		for(int i = 1; i < 6; i++){
			SqlInfo.sqlAusfuehren("ALTER TABLE "+"ertarif"+i+" ADD PRIMARY KEY (ID)"); 
			SqlInfo.sqlAusfuehren("ALTER TABLE "+"ertarif"+i+" CHANGE ID ID INT( 11 ) NOT NULL AUTO_INCREMENT");
		}

		
		lang = ParameterLaden.vLOPreise.size();
		vec = ParameterLaden.vLOPreise;
		for(int i = 1; i < lang  ;i++){
			for(int i2 = 1; i2 < 6;i2++){
				preis_akt = (vec.get(i).get(2+((i2-1)*4)+1).trim().equals("")? "0.00" :
					vec.get(i).get(2+((i2-1)*4)+1).trim());
				preis_alt = (vec.get(i).get(2+((i2-1)*4)+2).trim().equals("")? "0.00" :
					vec.get(i).get(2+((i2-1)*4)+2).trim());
				if(!preis_akt.equals("0.00")){
					String cmd = "insert into "+"lotarif"+Integer.toString(i2)+" set leistung='"+
					vec.get(i).get(0)+"', kuerzel='"+vec.get(i).get(1)+"', t_pos='"+
					vec.get(i).get(2+((i2-1)*4))+"', t_akt='"+preis_akt+"', "+
					"t_alt='"+preis_alt+"', id='"+vec.get(i).get(35)+"'";
					//System.out.println(cmd);
					SqlInfo.sqlAusfuehren(cmd);
				}
			}
		}
		for(int i = 1; i < 6; i++){
			SqlInfo.sqlAusfuehren("ALTER TABLE "+"lotarif"+i+" ADD PRIMARY KEY (ID)"); 
			SqlInfo.sqlAusfuehren("ALTER TABLE "+"lotarif"+i+" CHANGE ID ID INT( 11 ) NOT NULL AUTO_INCREMENT");
		}
		
		
		lang = ParameterLaden.vRHPreise.size();
		vec = ParameterLaden.vRHPreise;
		for(int i = 1; i < lang  ;i++){
			for(int i2 = 1; i2 < 6;i2++){
				preis_akt = (vec.get(i).get(2+((i2-1)*4)+1).trim().equals("")? "0.00" :
					vec.get(i).get(2+((i2-1)*4)+1).trim());
				preis_alt = (vec.get(i).get(2+((i2-1)*4)+2).trim().equals("")? "0.00" :
					vec.get(i).get(2+((i2-1)*4)+2).trim());
				if(!preis_akt.equals("0.00")){
					String cmd = "insert into "+"rhtarif"+Integer.toString(i2)+" set leistung='"+
					vec.get(i).get(0)+"', kuerzel='"+vec.get(i).get(1)+"', t_pos='"+
					vec.get(i).get(2+((i2-1)*4))+"', t_akt='"+preis_akt+"', "+
					"t_alt='"+preis_alt+"', id='"+vec.get(i).get(35)+"'";
					//System.out.println(cmd);
					SqlInfo.sqlAusfuehren(cmd);
				}
			}
		}
		for(int i = 1; i < 6; i++){
			SqlInfo.sqlAusfuehren("ALTER TABLE "+"rhtarif"+i+" ADD PRIMARY KEY (ID)"); 
			SqlInfo.sqlAusfuehren("ALTER TABLE "+"rhtarif"+i+" CHANGE ID ID INT( 11 ) NOT NULL AUTO_INCREMENT");
		}


	}

	*/
	
}
