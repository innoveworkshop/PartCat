package com.innoveworkshop.partcat.resources;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

/**
 * An application resource handler abstraction class.
 *
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class ApplicationResources {
	private static final String APPLICATION_ICON_16 = "Icon-16.png";
	private static final String APPLICATION_ICON_32 = "Icon-32.png";
	private static final String APPLICATION_ICON_96 = "Icon-96.png";

	/**
	 * Creates an application resource object.
	 */
	public ApplicationResources() {
	}
	
	/**
	 * Gets an image from a resource.
	 * 
	 * @param  name Resource name.
	 * @return      Resource {@link Image}.
	 */
	public Image getImageFromResource(String name) {
		return Toolkit.getDefaultToolkit().getImage(getClass().getResource(name));
	}
	
	/**
	 * Gets the application icons list.
	 * 
	 * @return The application icon image list.
	 * @see javax.swing.JFrame#setIconImages()
	 */
	public List<Image> getApplicationIcons() {
		List<Image> images = new ArrayList<Image>();
		
		// Add the images for the application.
		images.add(getImageFromResource(APPLICATION_ICON_16));
		images.add(getImageFromResource(APPLICATION_ICON_32));
		images.add(getImageFromResource(APPLICATION_ICON_96));
		
		return images;
	}
}
