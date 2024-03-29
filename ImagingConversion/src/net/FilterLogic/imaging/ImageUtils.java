package net.FilterLogic.imaging;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.PixelGrabber;
import java.util.HashMap;
import java.util.Map;

import javax.media.jai.RenderedOp;
import javax.swing.ImageIcon;

import sun.awt.image.BufferedImageGraphicsConfig;

public class ImageUtils 
{
	/**
	 * Convert buffered image to RGB.
	 * 
	 * @param inImg
	 * @return RGB BufferedImage
	 */
	public static BufferedImage convertToRgb(BufferedImage bufImage) 
	{
		//BufferedImage bufImage = inImg.getAsBufferedImage();
		ICC_Profile iccProfile = ICC_Profile.getInstance(ICC_ColorSpace.CS_sRGB);
		ColorSpace cSpace = new ICC_ColorSpace(iccProfile);
		ColorConvertOp op = new ColorConvertOp(bufImage.getColorModel().getColorSpace(), cSpace, null);
		BufferedImage newImage = new BufferedImage(bufImage.getWidth(),	bufImage.getHeight(), BufferedImage.OPAQUE);
		op.filter(bufImage, newImage);

		return newImage;
	}

	public static java.awt.image.BufferedImage resizeImage(java.awt.Image image, int newWidth)
	{
		Image resizedImage = null; 

		int iWidth = image.getWidth(null);
		int iHeight = image.getHeight(null); 

		iWidth = image.getWidth(null);

		if (iWidth > iHeight) 
		{ 
			//resizedImage = image.getScaledInstance(newWidth, (newWidth * iHeight) / iWidth, Image.SCALE_SMOOTH);
			resizedImage = image.getScaledInstance(1024, 768, Image.SCALE_SMOOTH);
		} 
		else 
		{ 
			//resizedImage = image.getScaledInstance((newWidth * iWidth) / iHeight, newWidth, Image.SCALE_SMOOTH);
			resizedImage = image.getScaledInstance(768, 1024, Image.SCALE_SMOOTH);
		} 

//		 This code ensures that all the pixels in the image are loaded. 
		Image temp = new ImageIcon(resizedImage).getImage();
		
//		 Create the buffered image. 
		BufferedImage bufferedImage = new BufferedImage(temp.getWidth(null), temp.getHeight(null), 
		BufferedImage.TYPE_INT_RGB); 

//		 Copy image to buffered image. 
		Graphics g = bufferedImage.createGraphics(); 

//		 Clear background and paint the image. 
		g.setColor(Color.white); 
		g.fillRect(0, 0, temp.getWidth(null), temp.getHeight(null)); 
		g.drawImage(temp, 0, 0, null); 
		g.dispose(); 

//		 Soften. 
		float softenFactor = 0.05f; 
		float[] softenArray = {0, softenFactor, 0, softenFactor, 1-(softenFactor*4), softenFactor, 0, softenFactor, 0}; 
		Kernel kernel = new Kernel(3, 3, softenArray); 
		ConvolveOp cOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null); 
		bufferedImage = cOp.filter(bufferedImage, null); 



		return bufferedImage;
	}
	
	/**
	 * Resize image based on max image file size.
	 * 
	 * @return  Int array where 0 = x, and 1 = y.
	 */
	public static int[] calculateResize(int width, int height, long imageByteSize, long maxImageSize)
	{
		int size[] = new int[2];
		int x = 0;
		int y = 0;
		
		float pct = (100 - (((float)maxImageSize / imageByteSize) * 100)) * (float).01;
		
		x = width - (int)(width * pct);
		y = height - (int)(height * pct);
		
		size[0] = x;
		size[1] = y;

		return size;
	}
	
	private static BufferedImage createCompatibleImage(java.awt.Image image,GraphicsConfiguration gc,int width, int height) 
	{ 
		//GraphicsConfiguration gc = BufferedImageGraphicsConfig.getConfig(image);

		BufferedImage result = gc.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
		Graphics2D g2 = result.createGraphics();
		//g2.drawRenderedImage(image, null);
		g2.drawImage(image, width, height, null);
		g2.dispose();
		return result; 
	}

	private static BufferedImage resize(BufferedImage image, int width, int height) 
	{ 
		int type = image.getType() == 0? BufferedImage.TYPE_INT_ARGB : image.getType();
		BufferedImage resizedImage = new BufferedImage(width, height, type);
		Graphics2D g = resizedImage.createGraphics();
		g.setComposite(AlphaComposite.Src);

		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
		RenderingHints.VALUE_INTERPOLATION_BILINEAR);


		g.setRenderingHint(RenderingHints.KEY_RENDERING, 
		RenderingHints.VALUE_RENDER_QUALITY);


		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
		RenderingHints.VALUE_ANTIALIAS_ON);

		g.drawImage(image, 0, 0, width, height, null);
		g.dispose();
		return resizedImage; 
	} 

	public static BufferedImage blurImage(BufferedImage image) 
	{ 
		float ninth = 1.0f/9.0f;
		float[] blurKernel = { 
		ninth, ninth, ninth,
		ninth, ninth, ninth,
		ninth, ninth, ninth

		};

		Map<RenderingHints.Key, Object> map = new HashMap<RenderingHints.Key, Object>();

		map.put(RenderingHints.KEY_INTERPOLATION, 
		RenderingHints.VALUE_INTERPOLATION_BILINEAR);


		map.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		map.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		RenderingHints hints = new RenderingHints(map);
		BufferedImageOp op = new ConvolveOp(new Kernel(3, 3, blurKernel), ConvolveOp.EDGE_NO_OP, hints);
		return op.filter(image, null);

	}

	public static BufferedImage resizeTrick(BufferedImage image, int width, int height) 
	{ 
		//image = createCompatibleImage(image);
		//image = resize(image, 800, 600);

		image = resize(image, width, height);
		//image = blurImage(image);
		
		return image; 
	} 
        
        
        
        
    public static Image rotateImage(Image img,double degree){
        BufferedImage bufImg = toBufferedImage(img);
        double angle = Math.toRadians(degree);

        return tilt(bufImg,angle);
    }

    public static BufferedImage tilt(BufferedImage image, double angle) {
        double sin = Math.abs(Math.sin(angle)), cos = Math.abs(Math.cos(angle));
        int w = image.getWidth(), h = image.getHeight();
        int neww = (int)Math.floor(w*cos+h*sin), newh = (int)Math.floor(h*cos+w*sin);
        GraphicsConfiguration gc = getDefaultConfiguration();
        BufferedImage result = gc.createCompatibleImage(neww, newh, Transparency.TRANSLUCENT);
        Graphics2D g = result.createGraphics();
        g.translate((neww-w)/2, (newh-h)/2);
        g.rotate(angle, w/2, h/2);
        g.drawRenderedImage(image, null);
        g.dispose();
        return result;
    }

    public static GraphicsConfiguration getDefaultConfiguration() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        return gd.getDefaultConfiguration();
    }

    // http://www.exampledepot.com/egs/java.awt.image/Image2Buf.html
    // An Image object cannot be converted to a BufferedImage object.
    // The closest equivalent is to create a buffered image and then draw the image on the buffered image.
    // This example defines a method that does this.

    // This method returns a buffered image with the contents of an image
    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage)image;
        }

        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();

        // Determine if the image has transparent pixels; for this method's
        // implementation, see e661 Determining If an Image Has Transparent Pixels
        boolean hasAlpha = hasAlpha(image);

        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            }

            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(
                image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }

        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }

        // Copy image to buffered image
        Graphics g = bimage.createGraphics();

        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }

    // http://www.exampledepot.com/egs/java.awt.image/HasAlpha.html
    // This method returns true if the specified image has transparent pixels
    public static boolean hasAlpha(Image image) {
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage)image;
            return bimage.getColorModel().hasAlpha();
        }

        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
         PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
        }

        // Get the image's color model
        ColorModel cm = pg.getColorModel();
        return cm.hasAlpha();
    }

    public static Image toImage(BufferedImage bufferedImage) {
        return Toolkit.getDefaultToolkit().createImage(bufferedImage.getSource());
    }
}
