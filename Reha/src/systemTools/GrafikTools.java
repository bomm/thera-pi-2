package systemTools;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import sun.awt.image.ImageFormatException;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class GrafikTools {
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
