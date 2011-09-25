package pdfDrucker;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFRenderer;

public class PDFPrintPage implements Printable {
	  private PDFFile file;
	  PDFPrintPage(PDFFile file) {
	    this.file = file;
	  }
	  @Override
	  public int print(Graphics g, PageFormat format, int index)
	    throws PrinterException {
	    int pagenum = index + 1;


	    if ((pagenum >= 1) && (pagenum <= file.getNumPages())) {

	      Graphics2D g2 = (Graphics2D)g;
	      PDFPage page = file.getPage(pagenum);

	      double pwidth = 595.f;
	      double pheight = 842.f;

	      Dimension dim;
          dim = page.getUnstretchedSize ((int) pwidth,
                                            (int) pheight,
                                            page.getBBox ());


	      double aspect = page.getAspectRatio();
	      //System.out.println("Aspect = "+aspect);

	      double paperaspect = pwidth / pheight;
	      //System.out.println("Paperaspect = "+paperaspect);

	      Rectangle imgbounds;

	      imgbounds = new Rectangle(0,0,(int)pwidth,(int)pheight);


	      // render the page
	      PDFRenderer pgs = new PDFRenderer(page, g2, imgbounds, null, null);
	      try {
	        page.waitForFinish();
	        pgs.run();
	      } catch (InterruptedException ie) {}

	      return PAGE_EXISTS;
	    } else {
	      return NO_SUCH_PAGE;
	    }
	  }
	}
