package dialoge;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import hauptFenster.Reha;

public class AaarghHinweis {
	public AaarghHinweis(String hinweis,String title){
		Object[] options = {"Ja - jetzt Benutzer-Gehirn auf Normalbetrieb umschalten"};
		int n = JOptionPane.showOptionDialog(null,
				hinweis,
				title,
				JOptionPane.YES_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				new ImageIcon(Reha.proghome+"/icons/strauss.png"),     //do not use a custom Icon
				options,  //the titles of buttons
				options[0]); //default button title
	}
}
