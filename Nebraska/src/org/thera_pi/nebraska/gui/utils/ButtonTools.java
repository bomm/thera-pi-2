package org.thera_pi.nebraska.gui.utils;

import java.awt.event.ActionListener;

import javax.swing.JButton;

public class ButtonTools {
	
	public static JButton macheBut(String titel,String cmd,ActionListener al){
		JButton but = new JButton(titel);
		but.setActionCommand(cmd);
		but.addActionListener(al);
		return but;
	}

}
