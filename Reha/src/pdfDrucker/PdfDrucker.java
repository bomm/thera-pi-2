package pdfDrucker;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PagePanel;

import java.awt.Graphics;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import javax.swing.*;

/**
 * An example of using the PagePanel class to show PDFs. For more advanced
 * usage including navigation and zooming, look ad the 
 * com.sun.pdfview.PDFViewer class.
 *
 * @author joshua.marinacci@sun.com
 */
public class PdfDrucker {

    public static void setup(String sfile) throws IOException {
    
        //set up the frame and panel
    	
        JFrame frame = new JFrame("PDF Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        PagePanel panel = new PagePanel();
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);

        //load a pdf from a byte buffer
        File file = new File(sfile);
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        FileChannel channel = raf.getChannel();
        ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY,
            0, channel.size());
        PDFFile pdffile = new PDFFile(buf);

        // show the first page
        PDFPage page = pdffile.getPage(0);
        panel.showPage(page);
        
/******************************
 * 
 *         
 */
        File f = new File(sfile);
        FileInputStream fis = new FileInputStream(f);
        FileChannel fc = fis.getChannel();
        ByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
        PDFFile pdfFile = new PDFFile(bb); // Create PDF Print Page
        PDFPrintPage pages = new PDFPrintPage(pdfFile);
         
        // Create Print Job
        PrinterJob pjob = PrinterJob.getPrinterJob();
        PageFormat pf = PrinterJob.getPrinterJob().defaultPage();
        final Paper paper = new Paper ( );
        paper.setSize(595.f, 842.f);
        paper.setImageableArea(0., 0., 595., 842.);
	    int width = ( int ) paper.getWidth ( );
	    int height = ( int ) paper.getHeight ( );
	    System.out.println("width:"+width+" / height:"+height);
        pf.setPaper(paper);
        
        pjob.setJobName(f.getName());
        Book book = new Book();
        book.append(pages, pf, 1);
        //book.append(pages, pf, pdfFile.getNumPages());
        pjob.setPageable(book);
         
        // Send print job to default printer
        try {
			pjob.print();
		} catch (PrinterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
/*******************************
 * 
 *         
 *         
 */
    }


}

