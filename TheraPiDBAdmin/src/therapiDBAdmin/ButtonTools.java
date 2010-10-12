package therapiDBAdmin;

import javax.swing.JButton;
import java.awt.event.ActionListener;

public class ButtonTools {

	public static JButton macheButton(String titel,String cmd,ActionListener al){
		JButton but = new JButton(titel);
		but.setName(cmd);
		but.setActionCommand(cmd);
		but.addActionListener(al);
		return but;
	}

}
