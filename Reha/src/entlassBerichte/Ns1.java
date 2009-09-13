package entlassBerichte;

import org.jdesktop.swingx.JXPanel;

public class Ns1 {
	JXPanel pan = null;
	public Ns1(EBerichtPanel xeltern){
		pan = new JXPanel();
	}
	public JXPanel getSeite(){
		return pan;
	}

}
