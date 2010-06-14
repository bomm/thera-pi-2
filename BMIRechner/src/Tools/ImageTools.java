package Tools;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import sun.awt.image.ImageFormatException;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class ImageTools {

	public static byte[] bufferedImageToByteArray(BufferedImage img) throws ImageFormatException, IOException{
		if(img != null){
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
		  
		JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(img);  
		//int quality = 5;  
		//quality = Math.max(0, Math.min(quality, 100));  
		param.setQuality((float) 1.0f, false);  
		encoder.setJPEGEncodeParam(param);  
		encoder.encode(img);
		os.close();
		return os.toByteArray();
		}else{
			return null;
		}
	}

}
