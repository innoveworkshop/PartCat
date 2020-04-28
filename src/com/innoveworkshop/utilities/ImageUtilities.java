package com.innoveworkshop.utilities;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * A simple class to help us with our image needs.
 *
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class ImageUtilities {
	/**
	 * Resizes a {@link BufferedImage} to a new dimension.
	 * 
	 * @param image          Image to be resized
	 * @param dim            Dimension to resize the image to.
	 * @param maintainAspect Should we maintain the original aspect ratio?
	 * @return               Properly resized {@link BufferedImage}.
	 * 
	 * @see <a href="https://stackoverflow.com/a/9417836/126353">Source of part of this snippet</a>
	 */
	public static BufferedImage resizeImage(BufferedImage image, Dimension dim,
			boolean maintainAspect) {
		// Handle the aspect ratio stuff.
		if (maintainAspect)
			dim = getScaledDimension(new Dimension(image.getWidth(), image.getHeight()), dim);
		
		// Create temporary images with the new size.
		Image tempImage = image.getScaledInstance(dim.width, dim.height, Image.SCALE_SMOOTH);
	    BufferedImage scaledImage = new BufferedImage(dim.width, dim.height,
	    		BufferedImage.TYPE_INT_ARGB);

	    // Create a scaled version.
	    Graphics2D graphics = scaledImage.createGraphics();
	    graphics.drawImage(tempImage, 0, 0, null);
	    graphics.dispose();

	    return scaledImage;
	}
	
	/**
	 * Gets a scaled {@link Dimension} that respects the aspect aspect ratio of the image.
	 * 
	 * @param imgSize  Image's original size.
	 * @param boundary Boundaries to fit this image into.
	 * @return         {@link Dimension} of an appropriately sized image.
	 * 
	 * @see <a href="https://stackoverflow.com/a/10245583/126353">Source of this snippet</a>
	 */
	public static Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {
	    int original_width = imgSize.width;
	    int original_height = imgSize.height;
	    int bound_width = boundary.width;
	    int bound_height = boundary.height;
	    int new_width = original_width;
	    int new_height = original_height;

	    // First check if we need to scale width.
	    if (original_width > bound_width) {
	        // Scale width to fit and scale height to maintain aspect ratio.
	        new_width = bound_width;
	        new_height = (new_width * original_height) / original_width;
	    }

	    // Check if we need to scale even with the new height.
	    if (new_height > bound_height) {
	        // Scale height to fit instead and scale width to maintain aspect ratio
	        new_height = bound_height;
	        new_width = (new_height * original_width) / original_height;
	    }

	    return new Dimension(new_width, new_height);
	}
}
