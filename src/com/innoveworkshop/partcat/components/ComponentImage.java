package com.innoveworkshop.partcat.components;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.innoveworkshop.partcat.PartCatWorkspace;
import com.innoveworkshop.utilities.FileUtilities;
import com.innoveworkshop.utilities.ImageUtilities;

/**
 * A component image abstraction class.
 *
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class ComponentImage {
	private PartCatWorkspace workspace;
	private Path path;
	private String name;
	private boolean usingDefault;

	/**
	 * Creates a new, empty, component image.
	 * 
	 * @param workspace A PartCat active workspace.
	 */
	public ComponentImage(PartCatWorkspace workspace) {
		this.workspace = workspace;
		setPath(null);
		setName(null, false);
	}
	
	/**
	 * Creates a new component image based on its name.
	 * 
	 * @param workspace A PartCat active workspace.
	 * @param name      Name of the image to use.
	 */
	public ComponentImage(PartCatWorkspace workspace, String name) throws Exception {
		this(workspace);
		
		setName(name);
		Path path = matchesName(name);
		if (path == null)
			throw new Exception("Couldn't find any image with a name of " + name);
		setPath(path);
	}
	
	/**
	 * Creates a new component image based on its name.
	 * 
	 * @param workspace A PartCat active workspace.
	 * @param name      Name of the image to use.
	 * @param isDefault Should this name be treated as a "default image".
	 */
	public ComponentImage(PartCatWorkspace workspace, String name,
			boolean isDefault) throws Exception {
		this(workspace, name);
		this.usingDefault = isDefault;
	}
	
	/**
	 * Gets the name of the image.
	 * 
	 * @return Name of the image.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the image name after checking if it actually exists.
	 * 
	 * @param name Image name to be set.
	 * 
	 * @throws Exception If no image was found with such name.
	 */
	public void setName(String name) throws Exception {
		if (!setName(name, true))
			throw new Exception("No image was found with the name " + name);
	}
	
	/**
	 * Sets the image name with or without checking if it actually exists first.
	 * 
	 * @param  name           Name of the image to be set.
	 * @param  checkExistence Should we check for existence?
	 * @return                True if the image exists and was set. If not
	 *                        checking for existence, this function will always
	 *                        return True.
	 */
	public boolean setName(String name, boolean checkExistence) {
		if (checkExistence) {
			if (matchesName(name) == null)
				return false;
		}
		
		this.name = name;
		return true;
	}
	
	/**
	 * Gets the path to the image file.
	 * 
	 * @return Image file path.
	 */
	public Path getPath() {
		return path;
	}
	
	/**
	 * Sets the path to the image file. If the image is outside the workspace,
	 * this function will also import the file to the workspace.
	 * 
	 * @param path Path to the image file.
	 * @param name Name of the image. Only used if it's necessary to import the
	 *             image into workspace.
	 */
	public void setPath(Path path, String name) {
		if (path != null) {
			if (!path.startsWith(workspace.getImagesPath())) {
				// The selected image is outside the workspace images folder. Let's bring it in.
				String destFilename = name + FileUtilities.getFileExtension(path, true);
				Path dest = workspace.getImagesPath().resolve(destFilename);
				
				try {
					Files.copy(path, dest, StandardCopyOption.COPY_ATTRIBUTES);
					path = dest;
				} catch (IOException e) {
					e.printStackTrace();
					path = null;
				}
			}
		}
		
		this.path = path;
		usingDefault = (this.path == null);
		setName(FileUtilities.getFilenameWithoutExt(path), false);
	}
	
	/**
	 * Sets the path to the image file.
	 * 
	 * @param path Path to the image file.
	 */
	public void setPath(Path path) {
		setPath(path, null);
	}
	
	/**
	 * Gets a {@link BufferedImage} of the component image.
	 * 
	 * @return Component image as a {@link BufferedImage}.
	 */
	public BufferedImage getImage() {
		Path path = getPath();

		try {
			if (path != null)
				return ImageIO.read(getPath().toFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Gets an {@link ImageIcon} from the component image to use directly in an
	 * UI element.
	 * 
	 * @return Image icon for usage in UI. If there isn't one set, a default one
	 *         will be returned.
	 */
	public ImageIcon getIcon() {
		return getIcon(null, true);
	}
	
	/**
	 * Gets an {@link ImageIcon} from the component image and resizes it to fit
	 * the dimension specified, being able to use it directly in an UI element.
	 * 
	 * @param  dim            Dimension to fit the image into. Can be null if you want the
	 *                        original image, no resizing done.
	 * @param  maintainAspect Should we maintain the image's aspect ratio?
	 * @return                Image icon pre-sized for usage in UI. If there isn't one set, a
	 *                        default one will be returned.
	 */
	public ImageIcon getIcon(Dimension dim, boolean maintainAspect) {
		BufferedImage image = getImage();
		if (image == null)
			return null;
		
		// Resize the image to fit these dimensions.
		if (dim != null)
			image = ImageUtilities.resizeImage(image, dim, maintainAspect);
		
		return new ImageIcon(image);
	}
	
	/**
	 * Checks if we are using a default placeholder image instead of a set one.
	 * 
	 * @return True if we are using a default placeholder image.
	 */
	public boolean isUsingDefaults() {
		return usingDefault;
	}

	/**
	 * Checks if a name matches the name of an image (filename without the
	 * extension) in the images directory.
	 * 
	 * @param  name Filename without the extension to be matched against.
	 * @return      Path to the file if it exists, otherwise NULL.
	 */
	public Path matchesName(String name) {
		// Check if we actually have a name.
		if (name == null)
			return null;
		
		// Go through images looking for one that actually has the name we want.
		File[] contents = workspace.getImagesPath().toFile().listFiles();
		if (contents != null) {
			for (File file : contents) {
				if (FileUtilities.getFilenameWithoutExt(file.toPath()).equals(name)) {
					return file.toPath();
				}
			}
		}
		
		return null;
	}
}
