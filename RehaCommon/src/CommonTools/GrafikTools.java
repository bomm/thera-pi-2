package CommonTools;

import java.awt.Graphics;
import java.awt.Image;
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
	 public static BufferedImage rotate90DX(BufferedImage bi)
		{
			int width = bi.getWidth();
			int height = bi.getHeight();
			
			BufferedImage biFlip = new BufferedImage(height, width, bi.getType());
			
			for(int i=0; i<width; i++)
				for(int j=0; j<height; j++)
					biFlip.setRGB(height-1-j, i, bi.getRGB(i, j));
					//biFlip.setRGB(height-1-j, width-1-i, bi.getRGB(i, j));
			
			return biFlip;
		}
	 public static BufferedImage rotateImage90DX(Image bi)
		{
			int width = bi.getWidth(null);
			int height = bi.getHeight(null);
			
			BufferedImage biConvert = new BufferedImage(height, width, BufferedImage.TYPE_INT_BGR);
			Graphics g = biConvert.createGraphics();
	        // Paint the image onto the buffered image
	        g.drawImage(bi, 0, 0, null);
	        g.dispose();

	        width = biConvert.getWidth(null);
			height = biConvert.getHeight(null);

	        BufferedImage biFlip = new BufferedImage(height, width, biConvert.getType());
			
			for(int i=0; i<width; i++)
				for(int j=0; j<height; j++)
					biFlip.setRGB(height-1-j, i, biConvert.getRGB(i, j));
					//biFlip.setRGB(height-1-j, width-1-i, biConvert.getRGB(i, j));
			
			return biFlip;
		}

	 public static BufferedImage rotate90SX(BufferedImage bi)
		{
			int width = bi.getWidth();
			int height = bi.getHeight();
			
			BufferedImage biFlip = new BufferedImage(height, width, bi.getType());
			
			for(int i=0; i<width; i++)
				for(int j=0; j<height; j++)
					biFlip.setRGB(j, width-1-i, bi.getRGB(i, j));
			
			return biFlip;
		}
	 public static BufferedImage rotateImage90SX(Image bi)
		{
			int width = bi.getWidth(null);
			int height = bi.getHeight(null);
			
			BufferedImage biConvert = new BufferedImage(height, width, BufferedImage.TYPE_INT_BGR);
			Graphics g = biConvert.createGraphics();
	        // Paint the image onto the buffered image
	        g.drawImage(bi, 0, 0, null);
	        g.dispose();

	        width = biConvert.getWidth(null);
			height = biConvert.getHeight(null);

	        BufferedImage biFlip = new BufferedImage(height, width, biConvert.getType());
			
			for(int i=0; i<width; i++)
				for(int j=0; j<height; j++)
					biFlip.setRGB(j, width-1-i, biConvert.getRGB(i, j));
					//biFlip.setRGB(j, i, biConvert.getRGB(i, j));
			
			return biFlip;
		}
	 
	 

}
