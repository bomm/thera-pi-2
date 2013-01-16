package rehaHMK;



import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;

import rehaHMKPanels.RehaHMKPanel1;
import rehaHMKPanels.RehaHMKPanel2;


import com.jgoodies.looks.windows.WindowsTabbedPaneUI;



public class RehaHMKTab extends JXPanel implements ChangeListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4602438532850826409L;
	JTabbedPane hmkTab = null;
	public JXTitledPanel jxTitel;
	public JXHeader jxh;
	RehaHMKPanel1 rehaHMKPanel1;
	RehaHMKPanel2 rehaHMKPanel2;
	
	public RehaHMKTab(){
		super();
		setLayout(new BorderLayout());
		hmkTab = new JTabbedPane();
		hmkTab.setUI(new WindowsTabbedPaneUI());
		
		
		rehaHMKPanel1 = new RehaHMKPanel1(this);
		rehaHMKPanel2 = new RehaHMKPanel2(this);
		hmkTab.addTab("Passenden Indikatiosschlüssel suchen",RehaHMK.icons.get("lupe"),rehaHMKPanel1);
		hmkTab.addTab("Rezeptänderung beantragen",RehaHMK.icons.get("blitz") ,rehaHMKPanel2);
		/*
		sqlEditPanel = new RehaSqlEdit(this);
		hmkTab.add("Sql-Befehle entwerfen/bearbeiten",sqlEditPanel);
		*/
		
		jxh = new JXHeader();
        ((JLabel)jxh.getComponent(1)).setVerticalAlignment(JLabel.NORTH);
        
        /*        
        jxh.setTitle("Nach machbaren Indikationsschlüsseln suchen");
        jxh.setDescription("<html><br>Sie wissen welche Behandlungsformen gut sind für Ihren Patienten?<br>"+
        		"Sie wissen aber nicht welche Indikationsschlüssel für diese Behandlung(en) möglich sind?<br><br>"+
        		"<b>Dann ist diese Seite genau das Richtige für Sie!</b>");
        jxh.setIcon(RehaHMK.icons.get("erde"));   
        */
        add(getHeader(0), BorderLayout.NORTH);
        add(hmkTab, BorderLayout.CENTER);
        hmkTab.validate();
        hmkTab.addChangeListener(this);
		validate();
		
	}

	
	private JXHeader getHeader(int welcher){
		switch(welcher){
		case 0:
	        jxh.setTitle("Nach machbaren Indikationsschlüsseln suchen");
	        jxh.setDescription("<html><br>Sie wissen welche Behandlungsformen gut sind für Ihren Patienten?<br>"+
	        		"Sie wissen aber nicht welche Indikationsschlüssel für diese Behandlung(en) möglich sind?<br><br>"+
	        		"<b>Dann ist diese Seite genau das Richtige für Sie!</b>");
	        jxh.setIcon(RehaHMK.icons.get("erde"));   
	        jxh.validate();
	        break;
		case 1:   
	        jxh.setTitle("Rezeptänderung beim zuständigen Arzt beantragen");
	        jxh.setDescription("<html><br>Indikationsschlüssel falsch, ein Kreuzchen fehlt?<br>"+
	        		"Hier können Sie auf einfache Weise die Änderung Ihres Rezeptes beantragen<br><br>"+
	        		"<b>Auch wenn einem dieser Mist zutiefst zuwider ist....</b>");
	        jxh.setIcon(RehaHMK.icons.get("strauss"));   
	        jxh.validate();
	        break;
		}
		return jxh;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		JTabbedPane pane = (JTabbedPane)e.getSource();
        int sel = pane.getSelectedIndex();
        jxh = getHeader(sel);
	}	
	
}
