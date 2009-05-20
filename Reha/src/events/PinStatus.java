package events;

public class PinStatus {
    public static final PinStatus GRUEN   = new PinStatus( "GRUEN");
    public static final PinStatus ROT = new PinStatus( "ROT" );
    public static final PinStatus SCHLIESSEN   = new PinStatus( "SCHLIESSEN" );
    public static PinStatus FENSTERNAME = new PinStatus("");
    private String _pinStatus;
 
    
    public String toString() {
        return _pinStatus;
    }
    
    private PinStatus( String pinStatus ) {
        _pinStatus = pinStatus;
    }
    public void setFensterName(String pinStatus){
    	_pinStatus = pinStatus;
    }
}
