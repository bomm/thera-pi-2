package terminKalender;

import hauptFenster.Reha;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;

import sun.awt.image.ImageFormatException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.text.ITextContentService;
import ag.ion.bion.officelayer.text.ITextCursor;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextDocumentImage;
import ag.ion.noa.graphic.GraphicInfo;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XNameContainer;
import com.sun.star.graphic.XGraphic;
import com.sun.star.graphic.XGraphicProvider;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.text.HoriOrientation;
import com.sun.star.text.TextContentAnchorType;
import com.sun.star.text.VertOrientation;
import com.sun.star.text.XText;
import com.sun.star.text.XTextContent;
import com.sun.star.text.XTextCursor;
import com.sun.star.uno.AnyConverter;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;



public class DruckeViewPanel extends SwingWorker<Void, Void>{
	JXPanel printPan = null;
	BufferedImage bufimg = null;
	
	public void setPrintPanel(JXPanel pan){
		this.printPan = pan;
		execute();
	}
	@SuppressWarnings("unused")
	@Override
	protected Void doInBackground() throws Exception {
		// TODO Auto-generated method stub
		if(printPan == null){
			return null;
		}
		try{
		Reha.thisFrame.setCursor(Reha.thisClass.wartenCursor);
		int pixelWidth = printPan.getWidth();
		int pixelHeight = printPan.getHeight();
		bufimg = new BufferedImage(pixelWidth,pixelHeight,BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufimg.createGraphics();
        printPan.paint(g2d); 
    	g2d.dispose();
    	speichernQualitaet("",1.0F);
    	
    	String url = Reha.proghome+"vorlagen/"+Reha.aktIK+"/terminshot_.ott";
    	IDocumentService documentService = Reha.officeapplication.getDocumentService();
    	 IDocumentDescriptor docdescript = new DocumentDescriptor();
	        docdescript.setHidden(false);
	        docdescript.setAsTemplate(true);
		IDocument document = null;
		document = documentService.loadDocument(url,docdescript);
        ITextDocument textDocument = (ITextDocument) document;

        boolean useStream = false;

        
        String imagePath = (Reha.proghome+"ScreenShots/termin__temp.jpg").replace("\\", "/");
        //String imagePath = "file:///"+Reha.proghome.replace("C:/", "/")+"ScreenShots/termin__temp.jpg";
        
        imagePath = "file:///"+imagePath;


        //"file:///tmp/myDocument.odt
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
        	//System.out.println("H�hentest = "+test2y);
        	
        }else{
        	fx = rasterx;
        	fy = testy;
        }
        
        //System.out.println("Die neuen Ma�e sind X:"+new Float(fx).intValue()+" / Y:"+new Float(fy).intValue());
        XMultiServiceFactory multiServiceFactory = null;
        XTextCursor xcursor = null;
        XTextCursor xTextCursor = null;
        if(!useStream) {
          //with url
        	
          graphicInfo = new GraphicInfo(imagePath, Float.valueOf(pixelWidth).intValue(), true, Float.valueOf(pixelHeight).intValue(), true,
              VertOrientation.TOP, HoriOrientation.LEFT,
              TextContentAnchorType.AT_FRAME);

        }
        else {

        	//System.out.println("Pixe des Bildes = X:"+pixelWidth+" / Y:"+pixelHeight);
        	//System.out.println("Seitenverh�ltnis = "+verhaeltnis);
        	/*
            graphicInfo = new GraphicInfo(imagePath, new Float(fx).intValue(),
                    false, new Float(fy).intValue(), false, VertOrientation.TOP, HoriOrientation.LEFT,
                    TextContentAnchorType.AS_CHARACTER);
            
            System.out.println(graphicInfo.getUrl());
            */
        	
            //URL = file:/C:/RehaVerwaltung/Reha/file:/RehaVerwaltung/ScreenShots/termin__temp.jpg
            
        	graphicInfo = new GraphicInfo(new FileInputStream(imagePath), new Float(fx).intValue(),
                    false, new Float(fy).intValue(), false, VertOrientation.TOP, HoriOrientation.LEFT,
                    TextContentAnchorType.AS_CHARACTER);
                
            /*
            multiServiceFactory = (XMultiServiceFactory) UnoRuntime.queryInterface(XMultiServiceFactory.class,
                    textDocument.getXTextDocument());
            XText xText = textDocument.getXTextDocument().getText();
            
            xTextCursor = xText.createTextCursor();
            */
            
            //XComponentContext
            /*
            try{
                XComponentContext xcomponentcontext = (XComponentContext) Bootstrap.createInitialComponentContext(null);
                XGraphic xGrafik = (XGraphic)getGraphicFromURL(xcomponentcontext, imagePath);
            }catch(Exception ex){
            	System.out.println("Exception in XComponentContext");
            	ex.printStackTrace();
            	System.out.println("**************Ende Exception in XComponentContext");
            }
			*/


            /*
             * 
            Object oFCProvider = _xMCF.createInstanceWithContext("com.sun.star.ucb.FileContentProvider", this.m_xContext);
            XFileIdentifierConverter xFileIdentifierConverter = (XFileIdentifierConverter) UnoRuntime.queryInterface(XFileIdentifierConverter.class, oFCProvider);
            String sImageUrl = xFileIdentifierConverter.getFileURLFromSystemPath(_sImageSystemPath, oFile.getAbsolutePath());

            Object oFCProvider = multiServiceFactory.createInstanceWithContext("com.sun.star.ucb.FileContentProvider", xText);
 
            XFileIdentifierConverter xFileIdentifierConverter = (XFileIdentifierConverter) UnoRuntime.queryInterface(XFileIdentifierConverter.class, oFCProvider);
            String sImageUrl = xFileIdentifierConverter.getFileURLFromSystemPath(_sImageSystemPath, oFile.getAbsolutePath());
            XGraphic xGraphic = getGraphic(sImageUrl);
			*/
            Thread.sleep(100);
        }
        
        //embedGraphic(graphicInfo,multiServiceFactory,xTextCursor);
        
        
        
        ITextContentService textContentService = textDocument.getTextService()
            .getTextContentService();

        ITextCursor textCursor = textDocument.getTextService().getText()
            .getTextCursorService().getTextCursor();
        

        
        ITextDocumentImage textDocumentImage = textContentService
            .constructNewImage(graphicInfo);
        textContentService.insertTextContent(textCursor.getEnd(),
            textDocumentImage);
		

		Reha.thisFrame.setCursor(Reha.thisClass.normalCursor);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return null;
	}
	
	

	@SuppressWarnings("unused")
	private void embedGraphic(GraphicInfo grProps,
	                XMultiServiceFactory xMSF, XTextCursor xCursor) {

	        XNameContainer xBitmapContainer = null;
	        XText xText = xCursor.getText();
	        XTextContent xImage = null;
	        String internalURL = null;
	        String url = null;

	        try {
	                xBitmapContainer = (XNameContainer) UnoRuntime.queryInterface(
	                                XNameContainer.class, xMSF.createInstance(
	                                                "com.sun.star.drawing.BitmapTable"));
	                xImage = (XTextContent) UnoRuntime.queryInterface(
	                                XTextContent.class,     xMSF.createInstance(
	                                                "com.sun.star.text.TextGraphicObject"));
	                XPropertySet xProps = (XPropertySet) UnoRuntime.queryInterface(
	                                XPropertySet.class, xImage);

	                // helper-stuff to let OOo create an internal name of the graphic
	                // that can be used later (internal name consists of various checksums)
	                //url = "file:///RehaVerwaltung/ScreenShots/termin__temp.jpg";
	                url = "C:/RehaVerwaltung/ScreenShots/termin__temp.jpg";

	                xBitmapContainer.insertByName("someID",(Object) url);
	                //xBitmapContainer.insertByName("someID", grProps.getUrl());
	                internalURL = AnyConverter.toString(xBitmapContainer
	                                .getByName("someID"));

	                xProps.setPropertyValue("AnchorType",
	                                com.sun.star.text.TextContentAnchorType.AS_CHARACTER);
	                xProps.setPropertyValue("GraphicURL", internalURL);
	                xProps.setPropertyValue("Width", (int) grProps.getWidth());
	                xProps.setPropertyValue("Height", (int) grProps.getHeight());

	                // inser the graphic at the cursor position
	                xText.insertTextContent(xCursor, xImage, false);

	                // remove the helper-entry
	                xBitmapContainer.removeByName("someID");
	        } catch (Exception e) {
	        	e.printStackTrace();
	                System.out.println("Failed to insert Graphic");
	                System.out.println(url);
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
	
    public XGraphic getGraphicFromURL(XComponentContext xContext, String sURL ){
    	XGraphic xGraphic = null;
    	try {
    		XGraphicProvider xGraphicProvider =
    			(XGraphicProvider) UnoRuntime.queryInterface(
    					XGraphicProvider.class,
    					xContext.getServiceManager().createInstanceWithContext(
    							"com.sun.star.graphic.GraphicProvider",
    							xContext));
    		PropertyValue[] aMediaProperties = new PropertyValue[1];
    		aMediaProperties[0] = new PropertyValue();
    		aMediaProperties[0].Name = "URL";
    		aMediaProperties[0].Value = sURL;
    		xGraphic = xGraphicProvider.queryGraphic(aMediaProperties);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return xGraphic;
    }

	
	
	private void speichernQualitaet(String stitel,Float fQuality){
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
	}	

	

}
