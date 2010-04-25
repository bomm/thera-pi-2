package urlaubBeteiligung;

import org.jdesktop.swingx.JXPanel;

import rehaInternalFrame.JUrlaubInternal;


public class Urlaub   extends JXPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6404833174512703189L;
	JUrlaubInternal internal = null;
	public Urlaub(JUrlaubInternal uint){
		super();
		this.internal = uint;
	}

}
