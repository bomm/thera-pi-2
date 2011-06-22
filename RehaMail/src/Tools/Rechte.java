package Tools;



import rehaMail.RehaMail;

public class Rechte {
	public static boolean hatRecht(int recht,boolean dialogzeigen){
		if(RehaMail.progRechte.substring(recht,recht+1).equals("1") ||
				RehaMail.progRechte.substring(RehaMail.BenutzerSuper_user,RehaMail.BenutzerSuper_user+1).equals("1")){
			return true;
		}
		return false;
	}
}	