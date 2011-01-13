package dta301;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXPanel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.windows.WindowsTabbedPaneUI;

import rehaInternalFrame.JDta301Internal;

public class Dta301 extends JXPanel {

	/**
	 * 
	 */
	ActionListener al = null;
	MouseListener tblmouse = null;
	JDta301Internal internal = null;
	JXPanel content = null;
	JTabbedPane tabPan = null;
	private static final long serialVersionUID = 7725262330614584928L;
	public Dta301(JDta301Internal jai){
		super();
		this.setLayout(new BorderLayout());
		this.internal = jai;
		this.makeListeners();
		this.add(getContent(),BorderLayout.CENTER);
		this.setName(this.internal.getName());
		this.content.setName(this.internal.getName());
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				setzeFocus();
			}
		});

	}
	private void setzeFocus(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				//tfs[0].requestFocus();
			}
		});
	}
	private void makeListeners(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("irgendwas")){
					
				}
			}
		};
	}
	private JXPanel getContent(){
		content = new JXPanel(new BorderLayout());
		content.setOpaque(false);
		tabPan = new JTabbedPane();
		tabPan.setUI(new WindowsTabbedPaneUI());
		tabPan.add("Beginn-Mitteilung",getBeginn());
		tabPan.add("Unterbrechung melden",getUnterbrechung());
		tabPan.add("Entlass-Mitteilung",getEntlassung());
		content.add(tabPan,BorderLayout.CENTER);
		return content;
	}
	
	private JXPanel getBeginn(){
		JXPanel pan = new JXPanel();
		pan.setOpaque(false);
		String xwerte = "";
		String ywerte = "";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		
		pan.validate();
		return pan;
	}
	private JXPanel getEntlassung(){
		JXPanel pan = new JXPanel();
		pan.setOpaque(false);
		String xwerte = "";
		String ywerte = "";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		
		pan.validate();
		return pan;
	}
	private JXPanel getUnterbrechung(){
		JXPanel pan = new JXPanel();
		pan.setOpaque(false);
		String xwerte = "";
		String ywerte = "";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		
		pan.validate();
		return pan;
	}
	

}
