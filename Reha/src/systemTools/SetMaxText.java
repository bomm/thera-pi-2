package systemTools;

import java.awt.Color;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.PlainDocument;
import javax.swing.text.StyleConstants;

public class SetMaxText extends PlainDocument {
	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int limit;
	  // optional uppercase conversion
	  private boolean toUppercase = false;
	  private Color normalFg = Color.BLACK;
	  private Color errorFg = Color.RED;
	  
	  public SetMaxText(int limit) {
	   super();
	   this.limit = limit;
	   }
	   
	  public SetMaxText(int limit, boolean upper) {
	   super();
	   this.limit = limit;
	   toUppercase = upper;
	   }
	  
	  public SetMaxText(int limit, boolean upper,Color normalFg,Color errorFg) {
		   super();
		   this.limit = limit;
		   this.toUppercase = upper;
		   this.normalFg = normalFg;
		   this.errorFg = errorFg;
	  }

	 
	  public void insertString
	    (int offset, String  str, AttributeSet attr)
	      throws BadLocationException {
		  if (str == null) return;

		  if ((getLength() + str.length()) <= limit) {
			  if (toUppercase) str = str.toUpperCase();
			  //StyleConstants.setForeground((MutableAttributeSet) attr, Color.BLUE);
			  super.insertString(offset, str, attr);
		  }else{
			  //if (toUppercase) str = str.toUpperCase();
			  //StyleConstants.setForeground((MutableAttributeSet) attr, Color.RED);
			  if(str.length() > limit){
				  super.insertString(0, str.substring(0,limit), attr);  
			  }
			  
			  
		  }
		   
	   }
}
