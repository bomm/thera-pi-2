package CommonTools;

import java.math.BigDecimal;

public class DoubleTools {
	public static Double Runde(Double dbl,int stellen){
		BigDecimal myDec = new BigDecimal( dbl );
		myDec = myDec.setScale( stellen, BigDecimal.ROUND_HALF_UP );
		return new Double(myDec.doubleValue());
	}
}
