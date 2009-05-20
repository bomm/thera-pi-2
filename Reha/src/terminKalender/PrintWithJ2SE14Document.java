package terminKalender;
/* PrintWithJ2SE14Document.java: Drucken eines Dokuments mit J2SE 1.4 */

import java.util.*;
import java.io.*;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;

public class PrintWithJ2SE14Document
{
        
  public static void main( String args[] )
  {
    new PrintWithJ2SE14Document( args );
  }

  public PrintWithJ2SE14Document( String args[] )
  {
    final String sErrNoPrintService =
      "\nZu diesem Dateityp ist kein passender Print-Service installiert.";
    final String sPrintFile = "PrintFile.ps";
    final String[] ssFileExtensionsAccepted =
      { "JPEG", "JPG", "PNG", "GIF", "TXT", "HTM", "HTML", "PS", "PDF" };
    final DocFlavor[] docFlavorsAccepted =
      { DocFlavor.INPUT_STREAM.JPEG, DocFlavor.INPUT_STREAM.JPEG,
        DocFlavor.INPUT_STREAM.PNG,  DocFlavor.INPUT_STREAM.GIF,
        DocFlavor.INPUT_STREAM.TEXT_PLAIN_HOST,
        DocFlavor.INPUT_STREAM.TEXT_HTML_HOST,
        DocFlavor.INPUT_STREAM.TEXT_HTML_HOST,
        DocFlavor.INPUT_STREAM.POSTSCRIPT,
        DocFlavor.INPUT_STREAM.PDF };
    DocFlavor flavor = null;
    int i, idxPrintService=-1;

    // Check first commandline parameter (input file):
    if( null != args && 0 < args.length && null != args[0]
        && 0 < (i = args[0].lastIndexOf( '.' )) )
    {
      String sInputFilenameExtension = args[0].substring( ++i ).toUpperCase();
      for( i=0; i<ssFileExtensionsAccepted.length; i++ )
        if( ssFileExtensionsAccepted[i].equals( sInputFilenameExtension ) )
        {
          flavor = docFlavorsAccepted[i];
          break;
        }
    }
    if( null == flavor )
    {
      System.out.println( "Drucken eines Dokuments entweder mit dem Drucker oder "
                        + "als PostScript-Datei." );
      System.out.println( "Erster Kommandozeilenparameter: Dokument-Datei." );
      System.out.println( "Erlaubt sind die Dateitypen:" );
      System.out.print(   "  " + ssFileExtensionsAccepted[0] );
      for( i=1; i<ssFileExtensionsAccepted.length; i++ )
        System.out.print( ", " + ssFileExtensionsAccepted[i] );
      System.out.println( "." );
      System.out.println( "Ohne zweiten Parameter wird eine Ausgabedatei im "
                        + "PostScript-Format erzeugt." );
      System.out.println( "Eine Zahl >= 0 als zweiter Parameter steuert den zu "
                        + "dieser Nummer gehoerenden " );
      System.out.println( "Print-Service (z.B. Drucker) an." );
      System.exit( 1 );
    }
    System.out.println( "" );
    System.out.println( "Eingangsdatei '" + args[0] + "':" );
    System.out.println( "  MIME-Typ '" + flavor.getMimeType() + "'" );

    // Check second commandline parameter (PrintService index):
    if( 1 < args.length )
      try {
        idxPrintService = Integer.parseInt( args[1] );
      }
      catch( Exception ex ) {
      }

    // Set print attributes:
    PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
    aset.add( MediaSizeName.ISO_A4 );

    try {

      if( 0 > idxPrintService )
      {
        // Print to Stream (here to PostScript File):
        StreamPrintServiceFactory[] prservFactories =
          StreamPrintServiceFactory.lookupStreamPrintServiceFactories(
            flavor, DocFlavor.BYTE_ARRAY.POSTSCRIPT.getMimeType() );
        if( null == prservFactories || 0 >= prservFactories.length )
        {
          System.err.println( sErrNoPrintService );
          System.exit( 2 );
        }
        System.out.println( "Stream-PrintService-Factory:" );
        for( i=prservFactories.length-1; i>=0; i-- )
          System.out.println( "  " + prservFactories[i]
                            + " (" + prservFactories[i].getOutputFormat() + ")" );
        FileOutputStream   fos = new FileOutputStream( sPrintFile );
        StreamPrintService sps = prservFactories[0].getPrintService( fos );
        System.out.println( "Stream-PrintService:" );
        System.out.println( "  " + sps + " (" + sps.getOutputFormat() + ")" );
        printPrintServiceAttributesAndDocFlavors( sps );
        DocPrintJob pj = sps.createPrintJob();
        FileInputStream fis = new FileInputStream( args[0] );
        Doc doc = new SimpleDoc( fis, flavor, null );
        pj.print( doc, aset );
        fos.close();
        System.out.println( "Ausgabedatei '" + sPrintFile
                          + "' ist erfolgreich generiert." );
      }
      else
      {
        // Print to PrintService (e.g. to Printer):
        PrintService   prservDflt = PrintServiceLookup.lookupDefaultPrintService();
        PrintService[] prservices = PrintServiceLookup.lookupPrintServices( flavor, aset );
        if( null == prservices || 0 >= prservices.length )
          if( null != prservDflt )
          {
            System.err.println( "Nur Default-Printer, da lookupPrintServices fehlgeschlagen." );
            prservices = new PrintService[] { prservDflt };
          }
          else
          {
            System.err.println( sErrNoPrintService );
            System.exit( 3 );
          }
        System.out.println( "Print-Services:" );
        for( i=0; i<prservices.length; i++ )
          System.out.println( "  " + i + ":  " + prservices[i]
                              + (( prservDflt != prservices[i] ) ? "" : " (Default)") );
        PrintService prserv = null;
        if( 0 <= idxPrintService && idxPrintService < prservices.length )
          prserv = prservices[idxPrintService];
        else
        {
          if( !Arrays.asList( prservices ).contains( prservDflt ) )  prservDflt = null;
          prserv = ServiceUI.printDialog( null, 50, 50, prservices, prservDflt, null, aset );
        }
        if( null != prserv )
        {
          System.out.println( "Ausgewaehlter Print-Service:" );
          System.out.println( "      " + prserv );
          printPrintServiceAttributesAndDocFlavors( prserv );
          DocPrintJob pj = prserv.createPrintJob();
          FileInputStream fis = new FileInputStream( args[0] );
          Doc doc = new SimpleDoc( fis, flavor, null );
          pj.print( doc, aset );
          System.out.println( "Dokument '" + args[0] + "' ist erfolgreich gedruckt." );
        }
      }

    } catch( PrintException pe ) { 
      System.err.println( pe );
    } catch( IOException ie ) { 
      System.err.println( ie );
    }
    System.exit( 0 );
  }

  private void printPrintServiceAttributesAndDocFlavors( PrintService prserv )
  {
    String s1=null, s2;
    Attribute[] prattr = prserv.getAttributes().toArray();
    DocFlavor[] prdfl  = prserv.getSupportedDocFlavors();
    if( null != prattr && 0 < prattr.length )
      for( int i=0; i<prattr.length; i++ )
        System.out.println( "      PrintService-Attribute["+i+"]: "
                            + prattr[i].getName() + " = " + prattr[i] );
    if( null != prdfl && 0 < prdfl.length )
      for( int i=0; i<prdfl.length; i++ )
      {
        s2 = prdfl[i].getMimeType();
        if( null != s2 && !s2.equals( s1 ) )
          System.out.println( "      PrintService-DocFlavor-Mime["+i+"]: " + s2 );
        s1 = s2;
      }
  }
}