package terminKalender;
/* PrintWithJ2SE12.java: Drucken mit J2SE-1.2-Funktionen */

import java.awt.print.*;

public class PrintWithJ2SE12
{
  private PrinterJob prjob;
  private PageFormat pfUse;

  public static void main( String[] args )
  {
    int iResMul = 4;  // 1 = 72 dpi; 4 = 288 dpi
    if( 0 < args.length )
      try { iResMul = Integer.parseInt( args[0] ); }
      catch( Exception ex ) { }
    PrintWithJ2SE12 myPrint = new PrintWithJ2SE12();
    if( 1 < args.length || myPrint.setupDialogs() )
      try {
        myPrint.print( iResMul );
      } catch( Exception ex ) {
        System.err.println( ex.getMessage() );
        System.exit( 1 );
      }
    System.exit( 0 );
  }

  public PrintWithJ2SE12()
  {
    prjob = PrinterJob.getPrinterJob();
    pfUse = prjob.defaultPage();
  }

  public boolean setupDialogs()
  {
    PageFormat pfDflt = pfUse;
    pfUse = prjob.pageDialog( pfDflt );
    return ( pfUse == pfDflt ) ? false : prjob.printDialog();
  }

  public void print( int iResMul )
  throws PrinterException
  {
    // See file MyPrintableObject.java:
    MyPrintableObject myPrObj = new MyPrintableObject();
    myPrObj.iResMul = iResMul;
    prjob.setPrintable( myPrObj, pfUse );
    prjob.print();
  }
}

