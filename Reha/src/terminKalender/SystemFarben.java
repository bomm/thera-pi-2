package terminKalender;

import java.awt.Color;
import java.awt.Font;

import systemEinstellungen.SystemConfig;

public class SystemFarben {
	static Color colKeinDienst = SystemConfig.KalenderHintergrund; //new Color(0x80,0x9f,0xff); //Color.BLUE;
	static Color colRehaTermin = Color.YELLOW;
	static Color colNormalTermin = new Color(238,238,238);
	static Color colFahrDienst = new Color(128,255,128);
	static Color colTerminFrei = Color.RED;
	static Color colOhneNummer = new Color(255,217,217);
	static Color colZweiBehandler = Color.MAGENTA;// new Color(255,217,217);	
	static Color colSchwarz = Color.BLACK;
	static Color colWeiss = Color.WHITE;
	static Color col15Min = new Color(165,100,181);
	static Color col20Min = Color.WHITE;
	static Color col25Min = new Color(192,192,192);
	static Color col30Min = new Color(128,255,255);
	static Color col40Min = new Color(0,0,255);
	static Color col45Min = new Color(0,128,0);
	static Color col50Min = new Color(192,40,192);
	static Color col60Min = new Color(64,128,128);
	static Color colAktiv = new Color(0,0,0);
	static Color colGrau1 = new Color(128,128,128);
	static Font fon1 = new Font("SansSerif", Font.PLAIN, 12);
	static Font fon2 = new Font("Lucida", Font.PLAIN, 12);
	static Font fon3 = new Font("Dialog", Font.PLAIN, 12);	

	public void init(){
		
	}
	public static Color farbenErstellen(String farbe){
		//int[] farbeAusRgbString = {0,0,0};
		String[] sfarbe = farbe.split(",");
		return new Color(Integer.valueOf(sfarbe[0]),Integer.valueOf(sfarbe[1]),Integer.valueOf(sfarbe[2]));
	}
}
