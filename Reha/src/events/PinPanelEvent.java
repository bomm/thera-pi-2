package events;
import java.util.EventObject;
public class PinPanelEvent extends EventObject {
	private PinStatus _pinStat;
    
    public PinPanelEvent( Object source, PinStatus pinStat ) {
        super( source );
        _pinStat = pinStat;
    }
    public PinStatus pinStat() {
        return _pinStat;
    }
    
}

