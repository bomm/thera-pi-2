package entlassBerichte;

import javax.swing.JPanel;

import org.jdesktop.swingx.JXPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class Eb2 {
	JXPanel pan = null;
	public Eb2(EBerichtPanel xeltern){
		pan = new JXPanel();
		pan.setOpaque(false);
		pan.add(constructSeite());
	}
	public JXPanel getSeite(){
		return pan;
	}
	public JPanel constructSeite(){
		FormLayout lay = new FormLayout("","");
		PanelBuilder pb = new PanelBuilder(lay);
		pb.setOpaque(false);
		CellConstraints cc = new CellConstraints();
		return pb.getPanel();
	}
	

}
