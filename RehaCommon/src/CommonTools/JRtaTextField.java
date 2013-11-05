package CommonTools;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.MaskFormatter;




public class JRtaTextField extends JFormattedTextField implements PropertyChangeListener,FocusListener,KeyListener{
	private String type=""; 
	private String muster="";
	//private Container feld = null;
	private boolean selectWhenFocus;
	private String gleitkomma ="";
	private NumberFormat gleitDisplayFormat;
	private NumberFormat gleitEditFormat;
	private int nachkommas = 0;
	private KeyListener kl;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6776374376473011458L;


	public JRtaTextField(String type,boolean selectWhenFocus){
		
		super();
		//new JFormattedTextField();
		setRtaType(type,this,selectWhenFocus);
		
		this.addFocusListener(this);
		this.addKeyListener(this);
		
		setDisabledTextColor(Color.RED);
	}

	public JRtaTextField(String type,boolean selectWhenFocus,String text){
		
		super();
		//new JFormattedTextField();
		setRtaType(type,this,selectWhenFocus);
		//this.addFocusListener(this);
		setText(text);
		this.addFocusListener(this);
		this.addKeyListener(this);
		
		setDisabledTextColor(Color.RED);
	}
	/*****************/
	public static String toRtaUpper(String s){
		StringBuffer sbuf = new StringBuffer();
		char[] cha = s.toCharArray();
		int i,len=s.length();
		
		for (i = 0; i < len; i++) {
			   char ch = cha[i];
			   if (ch == '\u00DF') { // sharp s
			      sbuf.append("ß");
			      continue;
			   }
			   sbuf.append(Character.toUpperCase(ch));
			}
		return sbuf.toString();
	}
	/*****************/
	public JRtaTextField(String type,boolean selectWhenFocus,String gleitkomma,String xalign){
		super();
		this.nachkommas = Integer.valueOf(gleitkomma.split("\\.")[1]); 
		//setupFormat(nachkommas);

		new JFormattedTextField(createFormatter("#########0.00"));
		if(xalign.equals("RECHTS")){
			setHorizontalAlignment(SwingConstants.RIGHT);
			//setAlignmentX(JFormattedTextField.RIGHT_ALIGNMENT);
		}	
		this.type = type;
		this.selectWhenFocus = selectWhenFocus;
		this.addFocusListener(this);
		this.addKeyListener(this);
		this.addPropertyChangeListener("value",this);
		setDisabledTextColor(Color.RED);		
	}
	public double getDValueFromS(){
		String test;
		test = getText();
		if(test.equals("")){
			return Double.parseDouble("0.00");
		}
		if(test.indexOf(".") < 0 && test.indexOf(",") < 0 ){
			return (Double.parseDouble(test.trim()+".00"));
			
		}
		double fielddbl = Double.parseDouble(test.replaceAll(",", "."));
		return fielddbl;
	}
	public void setDValueFromS(String value){
		String wert = value.replaceAll(",","."); 
		////System.out.println(wert);
		
		DecimalFormat df = new DecimalFormat ( "#########0.00" );
		setText( (wert.trim().equals("") ? df.format(Double.parseDouble("0.00")) : df.format(Double.parseDouble(wert)) )  );
		return;
	}

	private void setDisabledForeground(JFormattedTextField jcomp,Color c){
		jcomp.setDisabledTextColor(c);
	}
	
	public void listenerLoeschen(){
		this.removeFocusListener(this);
		this.removeKeyListener(this);
		this.removePropertyChangeListener(this);
	}
	
	
    protected MaskFormatter createFormatter(String s) {
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter(s);
       
        } catch (java.text.ParseException exc) {
            System.err.println("formatter is bad: " + exc.getMessage());
            System.exit(-1);
        }
        return formatter;
    }

	
	/*
	public JRtaTextField JRtaTextField(String muster,String type,Container con){
		
		
		JFormattedTextField comp = new JFormattedTextField();
		return this;
		//return comp;
	}
*/
	public void setRtaType(String type,JRtaTextField feld, boolean selectWhenFocus){
		this.type = type;
		this.selectWhenFocus = selectWhenFocus;
		
		for (int i = 0;i<1;i++){
			if(type.equals("GROSS")){
				this.setDocument(new NurGrossDocument(this));
				break;
			}
			if(type.equals("KLEIN")){
				this.setDocument(new NurKleinDocument(this));
				break;
			}
			if(type.equals("NORMAL")){
				//this.setDocument(new NurNormalDocument(this));
				break;
			}
			if(type.equals("ZAHLEN")){
				this.setDocument(new NurZahlenDocument(this));
				break;
			}
			if(type.equals("STUNDEN")){
				this.setDocument(new NurStundenDocument(this));
				break;
			}
			if(type.equals("MINUTEN")){
				this.setDocument(new NurMinutenDocument(this));
				break;
			}
			if(type.equals("DATUM")){
				this.setDocument(new XDateFieldDocument(this,false));
				this.setInputVerifier(new DateInputVerifier());
				break;
			}
			if(type.equals("D")){
				this.setDocument(new NurZahlenDocument(this));
				break;
			}
			if(type.equals("FL")){ //Fließkomma
				this.setDocument(new NurZahlenDocument(this));
				break;
			}

		}

	}
	
	public void setGleitType(String type,JRtaTextField feld, boolean selectWhenFocus,String gleitkomma){
		this.type = type;
		this.selectWhenFocus = selectWhenFocus;
		this.gleitkomma = gleitkomma;
	}
	public void setupFormat(int digits){
		gleitDisplayFormat = NumberFormat.getInstance(Locale.GERMAN);
		gleitDisplayFormat.setMinimumFractionDigits(digits);
		gleitDisplayFormat.setMaximumFractionDigits(digits);
		((DecimalFormat) gleitDisplayFormat).setDecimalSeparatorAlwaysShown(true);
		gleitEditFormat = NumberFormat.getInstance(Locale.GERMAN);
        gleitEditFormat.setMinimumFractionDigits(digits);
        gleitEditFormat.setMaximumFractionDigits(digits);
		((DecimalFormat) gleitEditFormat).setDecimalSeparatorAlwaysShown(true);
	}
	public String getRtaType(){
		return this.type;
	}
	public boolean getSelectOnFocus(){
		return this.selectWhenFocus;
	}
	public void focusNachUnten(){
		this.transferFocus();
		return;
	}
	public void focusNachOben(){
		this.transferFocusBackward();
		return;
	}	
	private void veroeffentlicheEvent(KeyEvent event){
		try{
			this.getParent().dispatchEvent(event);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		try{
			if(this.getParent().getParent() != null)
			this.getParent().getParent().dispatchEvent(event);	
		}catch(Exception ex){
			ex.printStackTrace();
		}
		try{
			if(this.getParent().getParent().getParent() != null)
			this.getParent().getParent().getParent().dispatchEvent(event);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		try{
			if(this.getParent().getParent().getParent().getParent()!= null)
			this.getParent().getParent().getParent().getParent().dispatchEvent(event);			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	/***********************************************************/	
	protected MaskFormatter getDateMask () {
	    MaskFormatter formatter = null;
	    try {
	      if (Locale.getDefault ().getLanguage ().equals (Locale.GERMANY.getLanguage())) {
	    	  ////System.out.println("Formatter - Locale = Germany");
	        formatter = new MaskFormatter ("##.##.####");
	      }
	      else {
	        formatter = new MaskFormatter ("####-##-##");
	      }
	      if (this.getPlaceHolder() != null) {
	        formatter.setPlaceholderCharacter (this.getPlaceHolder());
	      }
	    }
	    catch (final ParseException ignored) {
	      Logger.getLogger(this.getClass().getName()).throwing (this.getClass().getName(),"getDateMask", ignored);
	    }
	    return formatter;
	  }


	  private Character placeholder = null;
	  /**
	   * Set an Empty Character for delete the Input. If Empty Character is null,
	   * a valid value need to input.
	   * @param c Character
	   */
	  public void setPlaceholder (final Character c) {
	    this.placeholder = c;
	  }

	  /**
	   * Return the char for delete the input or null if delete not allowed.
	   * @return Character
	   */
	  public Character getPlaceHolder () {
	    return this.placeholder;
	  }

	

/*************************************************/


  protected static class DateInputVerifier extends InputVerifier {
    public boolean verify (final JComponent input) {
    ////System.out.println("Input getText = "+((JRtaTextField)input).getText());
    ////System.out.println("L�nge des Inputs = "+((JRtaTextField)input).getText().length());    
      if (input instanceof JRtaTextField) {
        return this.isAlowedDate((JRtaTextField)input);
      }
      else {
        return true;
      }
    }

  
    protected boolean isAlowedDate (final JRtaTextField input) {
    	String inhalt = input.getText();
     if(inhalt.equals("  .  .    ")){
    	 return true;
     }
    	////System.out.println("In verify / input = "+input.getText());
      final DateFormat sdf = this.getDateFormat ();
      try {
    	String teil = inhalt.substring(6).trim();
    	if(teil.length()==0){
    		input.setText("  .  .    ");
    		return true;
    	}
    	if(teil.length() == 2){
    		String jahrtausend = "";
    		if(IntegerTools.trailNullAndRetInt(teil) > 20){
    			jahrtausend = inhalt.substring(0,6).trim()+"19"+teil;
    		}else{
    			jahrtausend = inhalt.substring(0,6).trim()+"20"+teil;    			
    		}
    		input.setText(jahrtausend);
    		////System.out.println("Datum = "+jahrtausend);
    	}
    	if(inhalt.length() >= 8){
    		////System.out.println("L�nge des Strings = "+input.getText().length());
    		if(inhalt.substring(6,7).equals("0")){
    			String korrekt = inhalt.substring(0,6);
    			korrekt = korrekt+"20"+inhalt.substring(6,8);
    			input.setText(korrekt);
    			////System.out.println("korrigiertes Datum = "+korrekt);
    		}
    	}
    	
        final Date d = sdf.parse (input.getText());
        SwingUtilities.invokeLater(new Runnable () {
          public void run () {
            input.setText(sdf.format(d));
          }
        });
        return true;
      }
      catch (final ParseException notValidOrDelete) {
        if (input.getPlaceHolder() != null) {
          String noMaskValue = null;
          if (Locale.getDefault ().getLanguage ().equals (Locale.GERMANY.getLanguage ())) {
        	  ////System.out.println("InputVerifier - Locale = Germany");
            noMaskValue = input.getText().replace ('.',input.getPlaceHolder ());
          }
          else {
            noMaskValue = input.getText().replace ('-',input.getPlaceHolder ());
      	  	////System.out.println("InputVerifier - Locale = English");
          }
          for (char c : noMaskValue.toCharArray()) {
            if (c != input.getPlaceHolder()) return false;
          }
          return true;
        }
        //JOptionPane.showMessageDialog(null,"Unzul�ssige Datumseingabe");
        
        return false;
      }
    }

    
    protected DateFormat getDateFormat () {
      if (Locale.getDefault().getLanguage().equals(Locale.GERMANY.getLanguage())) {
        return new SimpleDateFormat ("dd.MM.yyyy");
      }
      else {
        return new SimpleDateFormat("yyyy-MM-dd");
      }
    }

    public boolean shouldYieldFocus (final JComponent input) {
      if (!verify(input)) {
        input.setForeground(Color.RED);
        input.setBorder(BorderFactory.createEtchedBorder(Color.RED, new Color (255,50,50)));
        return false;
      }
      else {
        //input.setForeground(Color.BLACK);
        input.setBorder((Border)UIManager.getLookAndFeelDefaults().get("TextField.border"));
        return true;
      }
    }
  }



@Override
public void propertyChange(PropertyChangeEvent arg0) {
	////System.out.println("PropertyChangeListener "+arg0);
	
	if(((JRtaTextField) arg0.getSource()).type.equals("FL")){
		String zahl = ((JRtaTextField)arg0.getSource()).getText();
		if(zahl.equals("")){zahl="0,00";}
		zahl = zahl.replaceAll(",", "\\.");
		DecimalFormat df = new DecimalFormat ( "#########0.00" );
		((JRtaTextField)arg0.getSource()).setText(df.format(new Double(zahl)));
	}
	
	
}

@Override
public void focusLost(FocusEvent e) {
	if( ((JRtaTextField)e.getComponent()).isEditable()){
		e.getComponent().setBackground(Color.WHITE);
		if((type == "STUNDEN" || type == "MINUTEN") && (getText().length()<2)){
			if(getText().length() ==1){setText((type=="MINUTEN" ? getText()+"0" :"0"+getText()) );}
			if(getText().length() ==0){setText("00");}
		}
		if((type == "NORMAL")){
			if(getText().length() > 0){
				setText(StringTools.EGross(getText().trim()));
			}
		}
	}
	try{
	e.getComponent().getParent().dispatchEvent(e);
	}catch(java.lang.NullPointerException ex){
		//System.out.println("in JRtaTextField Execption");
		// nixlos in der Hos
	}
}
	


@Override
public void focusGained(FocusEvent e) {	
	if( ((JRtaTextField)e.getComponent()).isEditable()){
		e.getComponent().setBackground(Color.YELLOW);
	}

	final FocusEvent xe = e;	
	SwingUtilities.invokeLater(new Runnable(){
	 	   public  void run()
	 	   {

	 		   if(getSelectOnFocus()){
		 		   ((JRtaTextField) xe.getComponent()).select(0,((JRtaTextField) xe.getComponent()).getText().length());
		 		   ////System.out.println(((JRtaTextField) xe.getComponent()).getName()+" soll bei Focus eine Selection erhalten");
	 		   }else{
	 			   ((JRtaTextField) xe.getComponent()).setCaretPosition(0);					
	 			   ////System.out.println(((JRtaTextField) xe.getComponent()).getName()+" soll bei Focus keine(!!!!) Selection erhalten");					
	 		   }
	
	 	   }
	});
	e.getComponent().getParent().dispatchEvent(e);
}

	@Override
    public void keyPressed(KeyEvent event) {
        // look for tab keys
    	int code = event.getKeyCode();
    	//SucheFenster in den Vordergrund stellen
    	if(event.isControlDown() && code== 83){
    		veroeffentlicheEvent(event);
    		event.consume();
    	}
    	if (code==27){
    		veroeffentlicheEvent(event);
    	}
    	if(code == KeyEvent.VK_TAB
        || code == KeyEvent.VK_ENTER || code == 40) {
            // if no valid data entered in field, consume event
            // so that it won't be passed on to focus manager
            if(1==1) {
                event.consume();
            	focusNachUnten();

            }
        }
    	if(code == 38){
        	focusNachOben();
        }
    	if(code == 27){
        	//((Component) event.getSource()).getParent().dispatchEvent(event);
        }
    	if(((JRtaTextField)event.getSource()).type.equals("DATUM")){
    		
    		////System.out.println("Feld ist Datumsfeld und KeyCode = "+event.getKeyCode());
    		if(code == 127){
    			((JRtaTextField)event.getSource()).setText("  .  .    ");
    			((JRtaTextField)event.getSource()).setCaretPosition(0);
    		}
    		if(code == 8){
    			((JRtaTextField)event.getSource()).setText("  .  .    ");
    			((JRtaTextField)event.getSource()).setCaretPosition(0);    			
    		}
    		

    	}
    	
    }


	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	
/******************Klassen-Ende********************/	
}
  
  
/*************************************************/
class NurZahlenDocument extends javax.swing.text.PlainDocument
{
/**
	 * 
	 */
	private static final long serialVersionUID = -29699654036665632L;
	private JTextField textField;
	private String text;

	public NurZahlenDocument(JFormattedTextField tf)
	{
		textField = tf;
	}

	public void insertString(int offs, String str, javax.swing.text.AttributeSet a)
	throws javax.swing.text.BadLocationException
	{
		try
		{
			super.insertString(offs, str, a);
			text = textField.getText();
			if ((text.length() == 1) & (text.equals("-")))
				return;
			Long.parseLong(text);
		}
		catch (NumberFormatException e)
		{
			//e.printStackTrace();
			super.remove(offs, 1);
			Toolkit.getDefaultToolkit().beep();
		}
	}
}
/*************************************************/

class NurNormalDocument extends javax.swing.text.PlainDocument
{
/**
	 * 
	 */
	private static final long serialVersionUID = -1708532746033381872L;
	private JTextField textField;
	private String text;

	public NurNormalDocument(JFormattedTextField tf)
	{
		textField = tf;
	}

	public void insertString(int offs, String str, javax.swing.text.AttributeSet a)
	throws javax.swing.text.BadLocationException
	{
		try
		{
			//	super.insertString(offs, str, a);
			////System.out.println("str = "+str);
			////System.out.println("Offset = "+offs);
			////System.out.println("a = "+a);
			text = textField.getText().trim();
			if(text.length() > 0){
				if(!str.substring(offs,1).equals(" ")){
					super.insertString(offs,StringTools.EGross(str), a);					
				}else{
					super.insertString(offs,str, a);
				}
				
			}else{
				super.insertString(offs,str, a);
			}

			return;
			//Integer.parseInt(text);
		}
		catch (NumberFormatException e)
		{
			super.remove(offs, 1);
			//Toolkit.getDefaultToolkit().beep();
		}

	}
}

/*************************************************/

class NurGrossDocument extends javax.swing.text.PlainDocument
{
/**
	 * 
	 */
	private static final long serialVersionUID = -1708532746033381872L;
	private JTextField textField;
	private String text;

	public NurGrossDocument(JFormattedTextField tf)
	{
		textField = tf;
	}

	public void insertString(int offs, String str, javax.swing.text.AttributeSet a)
	throws javax.swing.text.BadLocationException
	{
		try
		{
			//	super.insertString(offs, str, a);
			////System.out.println("Offset: "+offs);
			text = textField.getText();
			/*
			if(!str.contains("�")){
				super.insertString(offs,str.toUpperCase(), a);
			}else{
				super.insertString(offs,str, a);				
			}
			*/
			super.insertString(offs,JRtaTextField.toRtaUpper(str), a);
			
			return;
			//Integer.parseInt(text);
		}
		catch (NumberFormatException e)
		{
			super.remove(offs, 1);
			//Toolkit.getDefaultToolkit().beep();
		}

	}
}

/*************************************************/

class NurKleinDocument extends javax.swing.text.PlainDocument
{
/**
	 * 
	 */
	private static final long serialVersionUID = -1708532746033381872L;
	private JTextField textField;
	private String text;

	public NurKleinDocument(JFormattedTextField tf)
	{
		textField = tf;
	}

	public void insertString(int offs, String str, javax.swing.text.AttributeSet a)
	throws javax.swing.text.BadLocationException
	{
		try
		{
			//	super.insertString(offs, str, a);
			////System.out.println("Offset: "+offs);
			text = textField.getText();
			super.insertString(offs,str.toLowerCase(), a);
			return;
			//Integer.parseInt(text);
		}
		catch (NumberFormatException e)
		{
			super.remove(offs, 1);
			//Toolkit.getDefaultToolkit().beep();
		}

	}
}
/*************************************************/

class NurStundenDocument extends javax.swing.text.PlainDocument
{
/**
	 * 
	 */
	private static final long serialVersionUID = -29699654036665632L;
	private JTextField textField;
	private String text;

	public NurStundenDocument(JFormattedTextField tf)
	{
		textField = tf;
	}

	public void insertString(int offs, String str, javax.swing.text.AttributeSet a)
	throws javax.swing.text.BadLocationException
	{
		try
		{
			super.insertString(offs, str, a);
			////System.out.println("Offset: "+offs);
			text = textField.getText();
			if (Integer.valueOf(text) > 24){
				super.remove(offs, 1);
				//Toolkit.getDefaultToolkit().beep();
				return;
			}
			if ((text.length() == 1) & (text.equals("-")))
				return;
			Integer.parseInt(text);
		}
		catch (NumberFormatException e)
		{
			super.remove(offs, 1);
			//Toolkit.getDefaultToolkit().beep();
		}
	}
}
/*************************************************/
class NurMinutenDocument extends javax.swing.text.PlainDocument
{
/**
	 * 
	 */
	private static final long serialVersionUID = -29699654036665632L;
	private JTextField textField;
	private String text;

	public NurMinutenDocument(JFormattedTextField tf)
	{
		textField = tf;
	}

	public void insertString(int offs, String str, javax.swing.text.AttributeSet a)
	throws javax.swing.text.BadLocationException
	{
		try
		{
			super.insertString(offs, str, a);
			////System.out.println("Offset: "+offs);
			text = textField.getText();
			if (Integer.valueOf(text) > 59){
				super.remove(offs, 1);
				//Toolkit.getDefaultToolkit().beep();				
				return;
			}
			if ((text.length() == 1) & (text.equals("-")))
				return;
			Integer.parseInt(text);
		}
		catch (NumberFormatException e)
		{
			super.remove(offs, 1);
			//Toolkit.getDefaultToolkit().beep();
		}
	}
}
/*************************************************/

/*****************************************************************/

class DateFieldDocument extends javax.swing.text.PlainDocument {
	 // **** Attribute
	 private static final String JAHR  = "0123456789";// Erlaubte Ziffern Jahr
	 private static final String DREI  = "0123";// Erlaubte Ziffern Tag 10er
	 private static final String MONAT = "01";  // Erlaubte Zeichen Monat 10er
	 private Calendar initDate = new GregorianCalendar(); // Calender fuers init
	 private String initString;                 // Voreingestellter String
	 private static int trenner1 = 2, trenner2 = 5;  // Position vor dem Trenner
	 private JRtaTextField textComponent;      // F�r Referenz auf das TextFeld
	 private int newOffset;                     // Caret Position bei Trennern
	 private boolean init = false;
	 SimpleDateFormat datumsFormat = new SimpleDateFormat ("dd.MM.yyyy"); //Konv.
	 // **** Attribute Ende

	 // **** Konstruktor 1
	 public DateFieldDocument(JFormattedTextField textComponent,boolean datumHeute) { 
	  this.textComponent = (JRtaTextField)textComponent;       // Hiermit wird jetzt gearbeitet
	  initDate.setTime(new Date());             // Kalender auf heute
	  initString = datumsFormat.format(initDate.getTime()); // Nach String
	  
	  try {                                     // Jetzt den Inhalt mit dem Datum
		  if(datumHeute){
		  insertString(0, initString, null);       // initialisieren
		  this.init = true;
		  } else{
		  insertString(0, "  .  .    ", null);       // initialisieren
		  this.init = true;
		  }
	  }
	  catch(Exception KonstrEx) { KonstrEx.printStackTrace(); }
	  ////System.out.println("In Exception 1 KonstrEX - Zeichen = ");
	 }
	 // **** Konstruktor 1 Ende
	 // **** Konstruktor 2
	 public DateFieldDocument(JRtaTextField textComponent, Calendar givenDate){ 
	  this.textComponent = textComponent;       // Hiermit wird jetzt gearbeitet
	  initDate=givenDate;                       // Kalender auf Parameter
	  initString = datumsFormat.format(initDate.getTime()); // Nach String
	  try {                                     // Jetzt den Inhalt mit dem Datum
	   insertString(0, initString, null);       // initialisieren
	  }
	  catch(Exception KonstrEx) { KonstrEx.printStackTrace(); }
	  ////System.out.println("In Exception 2 KonstrEX - Zeichen = ");
	 }
	 // **** Konstruktor 2 Ende

	 // **** �berschreiben Insert-Methode
	 public void insertString(int offset, String zeichen, 
	       AttributeSet attributeSet) 
	       throws BadLocationException {
		  ////System.out.println("In insert String - Zeichen = "+zeichen);
	  if(zeichen.equals(initString) || zeichen.equals("  .  .    ")) { // Wenn initString oder leeres Datum, gleich rein
		  if (zeichen.equals("  .  .    ") ){
			  if(!this.init){
				  super.insertString(0, "  .  .    ", attributeSet);
			  }else{
				  super.remove(0, 10);
				  super.insertString(0, "  .  .    ", attributeSet);
			  }
		  }else{
			  if(!this.init){
				  super.insertString(0, zeichen, attributeSet);
			  }else{
				  super.remove(0, 10);
				  super.insertString(0, zeichen, attributeSet);
			  }
			  //super.insertString(offset, zeichen, attributeSet);			  
		  }

	  }
	  else if(zeichen.length()==10) {           // Wenn komplettes Datum, und
		  if (zeichen.equals("  .  .    ")) {        // richtig, dann rein
			  super.remove(0, 10);
			  super.insertString(0, zeichen, attributeSet);
		  }else{
			  super.remove(0, 10);
			  super.insertString(0, zeichen, attributeSet);		   
		  }
	  }
	  else if(zeichen.length()==1) {            // Wenn nicht, nur Einzelzeichen
	   try {                                    // annehmen
	    Integer.parseInt(zeichen);
	   }
	   catch(Exception NumEx) {                 // Kein Integer?
	    return;                                 // Keine Verarbeitung!
	   }
	   if(offset==0) {                          // Tage auf 10 20 30 pr�fen
	    if( DREI.indexOf( zeichen.valueOf(zeichen.charAt(0) ) ) == -1 ) {
	     //Toolkit.getDefaultToolkit().beep();
	     return;
	    }
	   }
	   if(offset==1) {                          // Tage 32-39 unterbinden
	    if(textComponent.getText().substring(0, 1).equals("3")) {
	     int tag = Integer.valueOf(zeichen).intValue();
	     if(tag>1) {
	      //Toolkit.getDefaultToolkit().beep();
	      return;
	     }
	    }
	   }
	   if(offset==1) {                          // Tag 00 unterbinden
	    if(textComponent.getText().substring(0, 1).equals("0")) {
	     int tag = Integer.valueOf(zeichen).intValue();
	     if(tag==0) {
	      //Toolkit.getDefaultToolkit().beep();
	      return;
	     }
	    }
	   }
	   if(offset==2) {                         // Monate auf 0x-1x pr�fen
	                                           // (Caret links vom Trenner)
	    if( MONAT.indexOf( zeichen.valueOf(zeichen.charAt(0) ) ) == -1 ) {
	     //Toolkit.getDefaultToolkit().beep();
	     return;
	    }
	   }
	   if(offset==3) {                         // Monate auf 0x-1x pr�fen
	                                           // (Caret rechts vom Trenner)
	    if( MONAT.indexOf( zeichen.valueOf(zeichen.charAt(0) ) ) == -1 ) {
	     //Toolkit.getDefaultToolkit().beep();
	     return;
	    }
	   }
	   if(offset==4) {                         // Monate 13-19 unterbinden
	    if(textComponent.getText().substring(3, 4).equals("1")) {
	     int monat = Integer.valueOf(zeichen).intValue();
	     if(monat>2) {
	      //Toolkit.getDefaultToolkit().beep();
	      return;
	     }
	    }
	   }
	   if(offset==4) {                         // Monat 00 unterbinden
	         if(textComponent.getText().substring(3, 4).equals("0")) {
	     int monat = Integer.valueOf(zeichen).intValue();
	     if(monat==0) {
	      //Toolkit.getDefaultToolkit().beep();
	      return;
	     }
	    }
	   }



	   newOffset = offset;
	   if(atSeparator(offset)) {             // Wenn am trenner, dann den offset
	    newOffset++;                         // vor dem einf�gen um 1 verschieben
	    textComponent.setCaretPosition(newOffset);
	   }
	   super.remove(newOffset, 1);           // Aktuelles zeichen entfernen
	   super.insertString(newOffset, zeichen, attributeSet);    // Neues einf�gen
	  }
	 }
	 // **** �berschreiben Insert Ende

	 // **** �berschreiben Remove
	 public void remove(int offset, int length) 
	       throws BadLocationException {
	  if(atSeparator(offset)) 
	   textComponent.setCaretPosition(offset-1);
	  else
	   textComponent.setCaretPosition(offset);
	 }
	 // **** �berschreiben Remove Ende

	 // **** Hilfsmethode f�r die Punkte zwischen den Feldern
	 private boolean atSeparator(int offset) {
	  return offset == trenner1 || offset == trenner2;
	 }
	 // **** Hilfsmethode Ende
	}

