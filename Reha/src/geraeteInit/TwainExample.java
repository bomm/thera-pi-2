package geraeteInit;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXImagePanel;
import org.jdesktop.swingx.JXImagePanel.Style;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import sun.awt.image.ImageFormatException;
import uk.co.mmscomputing.device.scanner.Scanner;
import uk.co.mmscomputing.device.scanner.ScannerIOException;
import uk.co.mmscomputing.device.scanner.ScannerIOMetadata;
import uk.co.mmscomputing.device.scanner.ScannerListener;
public class TwainExample
{
  @SuppressWarnings("serial")
  public static void main( String[] args )
  {

    final JXImagePanel imagePanel = new JXImagePanel();
    imagePanel.setStyle( Style.SCALED_KEEP_ASPECT_RATIO );
    //final Scanner scanner = Scanner.getDevice();
    final Scanner scanner = Scanner.getDevice();
    
    try {
    	scanner.select("CanoScan 5600F");
		System.out.println("*********************"+scanner.getSelectedDeviceName());
	} catch (ScannerIOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
    JXFrame f = new JXFrame( "SSP", true );
    Action action = new AbstractAction("Scan") {
      @Override public void actionPerformed( ActionEvent e ) {
        try { scanner.acquire(); } catch ( ScannerIOException ex ) { }
      }
    };
    f.add( new JButton(action), BorderLayout.PAGE_START );
    f.add( imagePanel );
    f.setSize( 1000, 700 );
    f.setVisible( true );
    scanner.addListener( new ScannerListener()
    {
        public void update( ScannerIOMetadata.Type type, ScannerIOMetadata metadata )
        {
      	  if ( ScannerIOMetadata.STATECHANGE.equals(type)){
      		System.out.println(metadata.getStateStr());    		
      	  }
            if ( ScannerIOMetadata.ACQUIRED.equals( type ) ){
               	System.out.println("Metadata-Info"+metadata.getInfo());
                  BufferedImage img = metadata.getImage();
                //  BufferedImage img2 = img;
                  imagePanel.setImage( img.getScaledInstance(100, 150,4)  );
                  doPDF(img);
            }
        }
    } );
    

  }
  
  public static void doPDF(BufferedImage img){
	  try {
			byte[] bild = bufferedImageToByteArray(img);
			Image jpg1 = Image.getInstance(bild);  
			float imgHeight = jpg1.getPlainHeight();  
			float imgWidth = jpg1.getPlainWidth();  
			Rectangle pageSize = new Rectangle(imgWidth, imgHeight); 
			Document document = new Document(pageSize);          

			PdfWriter.getInstance(document, new FileOutputStream("C:/rtadoku.pdf"));  
      
			document.open();  
			document.add(jpg1);  
			document.close();  
		} catch (ImageFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadElementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	  
  }

  public static byte[] bufferedImageToByteArray(BufferedImage img) throws ImageFormatException, IOException{
		if(img != null){
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
		encoder.encode(img);
		return os.toByteArray();
		}else{
			return null;
		}
	}


}