package rehaUrlaub;

import org.jdesktop.swingx.JXPanel;

public class RehaUrlaubTabelle extends JXPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7059098044660707403L;
	RehaUrlaubTab eltern = null;
	
	
	public RehaUrlaubTabelle(RehaUrlaubTab xeltern){
		super();
		this.eltern = xeltern;
		
	}

}
