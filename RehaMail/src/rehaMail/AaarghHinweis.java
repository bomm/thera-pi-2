package rehaMail;



import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class AaarghHinweis {
	public AaarghHinweis(String hinweis,String title){
		Object[] options = {"Ja - jetzt Benutzer-Gehirn auf Normalbetrieb umschalten"};
		JOptionPane.showOptionDialog(null,
				hinweis,
				title,
				JOptionPane.YES_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				new ImageIcon(RehaMail.progHome+"/icons/strauss.png"),     //do not use a custom Icon
				options,  //the titles of buttons
				options[0]); //default button title
	}
}
