package rechteTools;

import javax.swing.JOptionPane;

import hauptFenster.Reha;

public class Rechte {
	public static boolean hatRecht(int recht){
		if(Reha.ProgRechte.substring(recht,recht+1).equals("1")){
			return true;
		}
		JOptionPane.showMessageDialog(null,"Keine Berechtigung zum Aufruf dieser Funktion\n\n"+
				"Funktion --> "+rechteExt[recht]+"\n");
		return false;
	}
	
	public static int BenutzerDialog_Open = 0;
	public static int BenutzerRechte_Set = 1;
	public static int BenutzerSuper_User = 2;
	
	private static String[] rechteExt = {"Benutzerverwaltung öffnen","Benutzerrechte ändern","Super-User"
		
	};
}
