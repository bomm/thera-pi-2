package rehaMail;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;



public class DateFieldDocument extends javax.swing.text.PlainDocument {
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// **** Attribute
	 //private static final String JAHR  = "0123456789";// Erlaubte Ziffern Jahr
	 private static final String DREI  = "0123";// Erlaubte Ziffern Tag 10er
	 private static final String MONAT = "01";  // Erlaubte Zeichen Monat 10er
	 private Calendar initDate = new GregorianCalendar(); // Calender fuers init
	 private String initString;                 // Voreingestellter String
	 private static int trenner1 = 2, trenner2 = 5;  // Position vor dem Trenner
	 private JTextField textComponent;      // Für Referenz auf das TextFeld
	 private int newOffset;                     // Caret Position bei Trennern
	 private boolean init = false;
	 SimpleDateFormat datumsFormat = new SimpleDateFormat ("dd.MM.yyyy"); //Konv.
	 // **** Attribute Ende

	 // **** Konstruktor 1
	 public DateFieldDocument(JFormattedTextField textComponent,boolean datumHeute) { 
	  this.textComponent = textComponent;       // Hiermit wird jetzt gearbeitet
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
	 public DateFieldDocument(JFormattedTextField textComponent, Calendar givenDate){ 
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

	 // **** Überschreiben Insert-Methode
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
	   if(offset==0) {                          // Tage auf 10 20 30 prüfen
	    if( DREI.indexOf( String.valueOf(zeichen.charAt(0) ) ) == -1 ) {
	     //Toolkit.getDefaultToolkit().beep();
	     return;
	    }
	   }
	   if(offset==1) {                          // Tage 32-39 unterbinden
	    if(textComponent.getText().substring(0, 1).equals("3")) {
	     int tag = new Integer(zeichen).intValue();
	     if(tag>1) {
	      //Toolkit.getDefaultToolkit().beep();
	      return;
	     }
	    }
	   }
	   if(offset==1) {                          // Tag 00 unterbinden
	    if(textComponent.getText().substring(0, 1).equals("0")) {
	     int tag = new Integer(zeichen).intValue();
	     if(tag==0) {
	      //Toolkit.getDefaultToolkit().beep();
	      return;
	     }
	    }
	   }
	   if(offset==2) {                         // Monate auf 0x-1x prüfen
	                                           // (Caret links vom Trenner)
	    if( MONAT.indexOf( String.valueOf(zeichen.charAt(0) ) ) == -1 ) {
	     //Toolkit.getDefaultToolkit().beep();
	     return;
	    }
	   }
	   if(offset==3) {                         // Monate auf 0x-1x prüfen
	                                           // (Caret rechts vom Trenner)
	    if( MONAT.indexOf( String.valueOf(zeichen.charAt(0) ) ) == -1 ) {
	     //Toolkit.getDefaultToolkit().beep();
	     return;
	    }
	   }
	   if(offset==4) {                         // Monate 13-19 unterbinden
	    if(textComponent.getText().substring(3, 4).equals("1")) {
	     int monat = new Integer(zeichen).intValue();
	     if(monat>2) {
	      //Toolkit.getDefaultToolkit().beep();
	      return;
	     }
	    }
	   }
	   if(offset==4) {                         // Monat 00 unterbinden
	         if(textComponent.getText().substring(3, 4).equals("0")) {
	     int monat = new Integer(zeichen).intValue();
	     if(monat==0) {
	      //Toolkit.getDefaultToolkit().beep();
	      return;
	     }
	    }
	   }



	   newOffset = offset;
	   if(atSeparator(offset)) {             // Wenn am trenner, dann den offset
	    newOffset++;                         // vor dem einfügen um 1 verschieben
	    textComponent.setCaretPosition(newOffset);
	   }
	   super.remove(newOffset, 1);           // Aktuelles zeichen entfernen
	   super.insertString(newOffset, zeichen, attributeSet);    // Neues einfügen
	  }
	 }
	 // **** Überschreiben Insert Ende

	 // **** Überschreiben Remove
	 public void remove(int offset, int length) 
	       throws BadLocationException {
	  if(atSeparator(offset)) 
	   textComponent.setCaretPosition(offset-1);
	  else
	   textComponent.setCaretPosition(offset);
	 }
	 // **** Überschreiben Remove Ende

	 // **** Hilfsmethode für die Punkte zwischen den Feldern
	 private boolean atSeparator(int offset) {
	  return offset == trenner1 || offset == trenner2;
	 }
	 // **** Hilfsmethode Ende
	}

