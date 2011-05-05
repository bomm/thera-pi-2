package reha301;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JTabbedPane;

import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;

import reha301Panels.Reha301Auswerten;
import reha301Panels.Reha301Einlesen;

import com.jgoodies.looks.windows.WindowsTabbedPaneUI;



public class Reha301Tab extends JXPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JTabbedPane reha301Tab = null;
	public JXTitledPanel jxTitel;
	public JXHeader jxh;
	
	Reha301Einlesen reha301Einlesen = null;
	Reha301Auswerten reha301Auswerten = null;
	
	Reha301Tab(){
		super();
		setLayout(new BorderLayout());
		reha301Tab = new JTabbedPane();
		reha301Tab.setUI(new WindowsTabbedPaneUI());
		
		reha301Auswerten = new Reha301Auswerten(this);
		reha301Tab.add("Nachrichten auswerten/importieren",reha301Auswerten);

		//reha301Einlesen = new Reha301Einlesen(this);
		//reha301Tab.add("Nachrichten einlesen",reha301Einlesen);
		

		jxh = new JXHeader();
        ((JLabel)jxh.getComponent(1)).setVerticalAlignment(JLabel.NORTH);
        add(jxh, BorderLayout.NORTH);
        add(reha301Tab, BorderLayout.CENTER);
        jxh.validate();
        reha301Tab.validate();
		validate();
		
	}
	public void activateNachricht(){
		reha301Auswerten.initReha301Auswerten();
	}

}
