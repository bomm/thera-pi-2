package terminKalender;

import hauptFenster.Reha;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXPanel;

import sun.awt.image.ImageFormatException;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.star.beans.PropertyValue;
import com.sun.star.text.HoriOrientation;
import com.sun.star.text.TextContentAnchorType;
import com.sun.star.text.VertOrientation;



import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.text.ITextContentService;
import ag.ion.bion.officelayer.text.ITextCursor;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextDocumentImage;
import ag.ion.bion.officelayer.text.ITextTable;
import ag.ion.noa.graphic.GraphicInfo;



public class DruckeViewPanel extends SwingWorker<Void, Void>{
	JXPanel printPan = null;
	BufferedImage bufimg = null;
	
	public void setPrintPanel(JXPanel pan){
		this.printPan = pan;
		execute();
	}
	@Override
	protected Void doInBackground() throws Exception {
		// TODO Auto-generated method stub
		if(printPan == null){
			return null;
		}
		Reha.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		int pixelWidth = printPan.getWidth();
		int pixelHeight = printPan.getHeight();
		bufimg = new BufferedImage(pixelWidth,pixelHeight,BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufimg.createGraphics();
        printPan.paint(g2d); 
    	g2d.dispose();
    	speichernQualität("",1.0F);
    	
    	String url = Reha.proghome+"vorlagen/"+Reha.aktIK+"/terminshot_.ott";
    	IDocumentService documentService = Reha.officeapplication.getDocumentService();
    	 IDocumentDescriptor docdescript = new DocumentDescriptor();
	        docdescript.setHidden(false);
	        docdescript.setAsTemplate(true);
		IDocument document = null;
		document = documentService.loadDocument(url,docdescript);
        ITextDocument textDocument = (ITextDocument) document;

        boolean useStream = true;

        
        String imagePath = Reha.proghome+"ScreenShots/termin__temp.jpg";
        GraphicInfo graphicInfo = null;

        float fx =  new Float(pixelWidth);
        float fy = new Float(pixelHeight);
        
        float verhaeltnis = fx / fy;
        float rasterx = new Float(27900.000);
        float testy = rasterx/verhaeltnis;
        float test2y = 0.00F;
        if(testy > 19000.00F){
        	test2y = 19000.00F*100.0F/testy;
        	fx = rasterx/100*test2y;
        	fy = 19000.00F;
        	System.out.println("Höhentest = "+test2y);
        	
        }else{
        	fx = rasterx;
        	fy = testy;
        }
        
        System.out.println("Die neuen Maße sind X:"+new Float(fx).intValue()+" / Y:"+new Float(fy).intValue());

        if(!useStream) {
          //with url
        	
          graphicInfo = new GraphicInfo(imagePath, pixelWidth, true, pixelHeight, true,
              VertOrientation.TOP, HoriOrientation.LEFT,
              TextContentAnchorType.AT_FRAME);

        }
        else {
/*        	
        	InputStream is = new ByteArrayInputStream(bufferedImageToByteArray(bufimg));  
           graphicInfo = new GraphicInfo(is, pixelWidth,
                    true, pixelHeight, true, VertOrientation.TOP, HoriOrientation.LEFT,
                     TextContentAnchorType.AS_CHARACTER);
*/                     
        	System.out.println("Pixe des Bildes = X:"+pixelWidth+" / Y:"+pixelHeight);
        	System.out.println("Seitenverhältnis = "+verhaeltnis);
        	
            graphicInfo = new GraphicInfo(new FileInputStream(imagePath), new Float(fx).intValue(),
                    false, new Float(fy).intValue(), false, VertOrientation.TOP, HoriOrientation.LEFT,
                    TextContentAnchorType.AS_CHARACTER);
            /*
        	graphicInfo = new GraphicInfo(new FileInputStream(imagePath), pixelWidth,
                    true, pixelHeight, true, VertOrientation.TOP, HoriOrientation.LEFT,
                    TextContentAnchorType.AS_CHARACTER);
            */        
                    
            /*
          graphicInfo = new GraphicInfo(new FileInputStream(imagePath), pixelWidth,
              true, pixelHeight, true, VertOrientation.TOP, HoriOrientation.LEFT,
              TextContentAnchorType.AT_PARAGRAPH);
           */   
        }

        ITextContentService textContentService = textDocument.getTextService()
            .getTextContentService();

        ITextCursor textCursor = textDocument.getTextService().getText()
            .getTextCursorService().getTextCursor();

        ITextDocumentImage textDocumentImage = textContentService
            .constructNewImage(graphicInfo);
        textContentService.insertTextContent(textCursor.getEnd(),
            textDocumentImage);

		Reha.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

		
		return null;
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
	public static ByteArrayOutputStream bufferedImageToOutputStream(BufferedImage img) throws ImageFormatException, IOException{
		if(img != null){
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
		encoder.encode(img);
		return os;
		}else{
			return null;
		}
	}
	
	private void speichernQualität(String stitel,Float fQuality){
//		img, "jpg", new File("C:\\ScreenShots\\"+stitel+".jpg")
		IIOImage imgq = new IIOImage((RenderedImage) bufimg, null, null);
        ImageWriter writer = ImageIO.getImageWritersBySuffix("jpg").next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(fQuality);
        File fimg = new File(Reha.proghome+"ScreenShots/termin__temp.jpg");
        try {
			writer.setOutput(ImageIO.createImageOutputStream(fimg));
			writer.write(null, imgq, param);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //writer.write(null, img, param);
	}	

	

}
