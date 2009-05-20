package hauptFenster;

import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.plaf.UIManagerExt;

public class RehaLoginPane extends JXLoginPane{

/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
public String CLASS_NAME = "RehaLoginPane"; 	

	RehaLoginPane(){
		super();
		UIManagerExt.addResourceBundle("RehaLoginPane.properties");
		
	}

}
