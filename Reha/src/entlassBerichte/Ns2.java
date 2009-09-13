package entlassBerichte;

import org.jdesktop.swingx.JXPanel;

public class Ns2 {
	JXPanel pan = null;
	public Ns2(EBerichtPanel xeltern){
		pan = new JXPanel();
	}
	public JXPanel getSeite(){
		return pan;
	}

}

