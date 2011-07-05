package org.thera_pi.updates;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.net.ftp.FTPFile;
import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;


import com.jgoodies.looks.windows.WindowsTabbedPaneUI;

public class UpdateTab  extends JXPanel implements ChangeListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	TheraPiUpdates eltern;
	JTabbedPane updateTab = null;
	public JXTitledPanel jxTitel;
	public JXHeader jxh;
	JXPanel tab1 = null;
	JXPanel tab2 = null;
	JXPanel tab3 = null;
	JXPanel tab4 = null;

	
	UpdateTab(TheraPiUpdates xeltern){
		super();
		eltern = xeltern;
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createRaisedBevelBorder());
		updateTab = new JTabbedPane();
		updateTab.setUI(new WindowsTabbedPaneUI());
		tab1 = new UpdatePanel(eltern,null, this);
		tab2 = new EntwicklerPanel(eltern,this);
		updateTab.add("<html>Anwenderseite</html>",tab1);
		updateTab.add("<html>Entwicklerseite</html>",tab2);
		
		add(updateTab,BorderLayout.CENTER);
		validate();
	}
	public void activateUpdateCheck(){
		((UpdatePanel)tab1).doUpdateCheck();
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		// TODO Auto-generated method stub
	}
	public FTPFile[] getFilesFromUpdatePanel(){
		return ((UpdatePanel)tab1).ffile;
	}
	
}
