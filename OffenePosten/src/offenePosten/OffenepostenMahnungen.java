package offenePosten;

import java.awt.BorderLayout;

import org.jdesktop.swingx.JXPanel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class OffenepostenMahnungen extends JXPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7011413450109922373L;
	OffenepostenTab eltern = null;
	JXPanel content = null;

	public OffenepostenMahnungen(OffenepostenTab xeltern){
		super();
		this.eltern = xeltern;
		this.setLayout(new BorderLayout());
		add(getContent(),BorderLayout.CENTER);
		
		
	}
	
	private JXPanel getContent(){
		String xwerte = "";
		String ywerte = "";
		content = new JXPanel();
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		content.setLayout(lay);
		
		content.validate();
		return content;
	}

}
