package systemTools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXFrame;

import systemEinstellungen.SysUtilPreislisten;
import systemEinstellungen.SystemUtil;

import dialoge.RehaSmartDialog;

public class PreisUpdate extends RehaSmartDialog implements ActionListener{

	public PreisUpdate(SystemUtil owner, String name,String disziplin,String preisgruppe,SysUtilPreislisten upl) {
		super(null,"preisupdate");
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	

}
