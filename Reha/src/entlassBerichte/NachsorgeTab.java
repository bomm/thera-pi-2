package entlassBerichte;

import javax.swing.JTabbedPane;

import org.jdesktop.swingx.JXPanel;

import com.jgoodies.looks.windows.WindowsTabbedPaneUI;

public class NachsorgeTab {
	EBerichtPanel eltern = null;
	JTabbedPane tab = null;
	Ns1 seite1 = null;
	Ns2 seite2 = null;

	public NachsorgeTab(EBerichtPanel xeltern){
		eltern = xeltern;
		tab = new JTabbedPane();
		tab.setUI(new WindowsTabbedPaneUI());
		seite1 = new Ns1(eltern); 
		seite2 = new Ns2(eltern);
		tab.addTab("Reha-Nachsorge Seite-1", seite1.getSeite());
		tab.addTab("Reha-Nachsorge Seite-2", seite2.getSeite());
	}
	public JTabbedPane getTab(){
		return tab;
	}
}
