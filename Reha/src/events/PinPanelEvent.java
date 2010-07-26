package events;
import java.util.EventObject;
public class PinPanelEvent extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2377132884186788719L;
	private PinStatus _pinStat;
    
    public PinPanelEvent( Object source, PinStatus pinStat ) {
        super( source );
        _pinStat = pinStat;
    }
    public PinStatus pinStat() {
        return _pinStat;
    }
    
}

