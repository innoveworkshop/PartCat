package com.innoveworkshop.partcat.components;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import com.innoveworkshop.partcat.PartCatConstants;
import com.innoveworkshop.partcat.PartCatWorkspace;
import com.innoveworkshop.partcat.exceptions.WorkspaceNotOpenedException;
import com.innoveworkshop.utilities.FileUtilities;

/**
 * A component abstraction class. This represents a single populated component
 * folder.
 *
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class Component {
	private PartCatWorkspace workspace;
	private Path path;
	private String name;
	private int quantity;
	private ComponentProperties prop;
	private String notes;
	private ComponentImage image;
	private boolean newly_created;
	private boolean deleted;

	/**
	 * Creates an empty component object.
	 * 
	 * @param workspace A PartCat active workspace.
	 * 
	 * @throws WorkspaceNotOpenedException If the workspace isn't open. This is
	 *                                     needed to determine the component
	 *                                     folder path.
	 */
	public Component(PartCatWorkspace workspace) throws WorkspaceNotOpenedException {
		// Check if our workspace is opened.
		if (!workspace.isOpen())
			throw new WorkspaceNotOpenedException();
		
		// Set default parameters.
		this.workspace = workspace;
		this.path = null;
		this.name = null;
		this.setQuantity(0);
		this.setNotes(null);
		this.prop = new ComponentProperties();
		this.newly_created = true;
		this.deleted = false;
		this.image = new ComponentImage(this.workspace);
	}
	
	/**
	 * Creates a component object by reading the component folder with name.
	 * 
	 * @param workspace A PartCat active workspace.
	 * @param name      Name of the component folder to be read.
	 * 
	 * @throws WorkspaceNotOpenedException If the workspace isn't open. This is
	 *                                     needed to determine the component
	 *                                     folder path.
	 * @throws FileNotFoundException       If the path to the component folder
	 *                                     either doesn't exist or isn't a folder.
	 * @throws IOException                 If something goes wrong when reading
	 *                                     data from the files inside the
	 *                                     component folder.
	 * @throws Exception                   Something went wrong.
	 */
	public Component(PartCatWorkspace workspace, String name) throws WorkspaceNotOpenedException,
																	 FileNotFoundException,
																	 IOException,
																	 Exception {
		// Initialize an empty object with a name.
		this(workspace);
		this.setName(name);
		
		// Check if we are creating a new component or loading one.
		if (exists(name)) {
			newly_created = false;
			
			// Get component folder root path and check if the folder actually exists.
			path = workspace.getPath()
					.resolve(PartCatConstants.COMPONENTS_ROOT).resolve(name);
			if (!Files.exists(path) && !Files.isDirectory(path))
				throw new FileNotFoundException("The path provided doesn't " +
						"exist or isn't a directory.");
			
			// Populate the object.
			populateFromPath(path);
		}
	}
	
	/**
	 * Populates the object with information from a component path.
	 * 
	 * @param path Path to the component folder.
	 * 
	 * @throws IOException If something goes wrong when reading data from the
	 *                     files inside the component folder.
	 */
	protected void populateFromPath(Path path) throws IOException {
		// Load the manifest file.
		Path manifest_path = path.resolve(PartCatConstants.MANIFEST_FILE);
		prop.parseManifest(manifest_path);
		
		// Load image.
		loadImage();
		
		// Load the quantity file.
		try {
			// Get file path and set the quantity with its contents.
			Path quantity_path = path.resolve(PartCatConstants.QUANTITY_FILE);
			this.setQuantity(FileUtilities.slurpFile(quantity_path));
		} catch (Exception e) {
			this.setQuantity(0);
		}
		
		// Load the notes file.
		try {
			// Get file path and set the notes property with its contents.
			Path notes_path = path.resolve(PartCatConstants.NOTES_FILE);
			this.setNotes(FileUtilities.slurpFile(notes_path));
		} catch (Exception e) {
			this.setNotes(null);
		}
	}

	/**
	 * Saves the component as a new one with a different name to disk.
	 * 
	 * @param name New component name.
	 * 
	 * @throws IOException When something wrong happens.
	 */
	public void saveAs(String name) throws IOException {
		// Looks like it's the same name, so let's just save it and be done with it.
		if (name.equals(getName())) {
			save();
			return;
		}
		
		// Copy the source component folder first.
		Path targetPath = path = workspace.getPath()
				.resolve(PartCatConstants.COMPONENTS_ROOT).resolve(name);
		FileUtilities.copyDirectory(path, targetPath, StandardCopyOption.COPY_ATTRIBUTES);
		
		// Remake this object into the new component and save.
		this.path = targetPath;
		this.name = name;
		save();
	}
	
	/**
	 * Saves the component to disk. This will override all the files inside the
	 * component folder.
	 * 
	 * @throws IOException When something wrong happens.
	 */
	public void save() throws IOException {
		// Check if we are dealing with a new component. If so, create it first.
		if (isNewlyCreated()) {
			// Get path to the new component.
			path = workspace.getPath()
					.resolve(PartCatConstants.COMPONENTS_ROOT).resolve(name);
			
			// Create component folder.
			if (!path.toFile().mkdir())
				throw new IOException("Couldn't create the component folder at " +
						path.toString());
		}
		
		// Quantity file.
		FileUtilities.writeFileContents(path.resolve(PartCatConstants.QUANTITY_FILE),
				String.valueOf(this.getQuantity()));
		
		// Notes file.
		if (getNotes() != null)
			FileUtilities.writeFileContents(path.resolve(PartCatConstants.NOTES_FILE),
					getNotes());
		
		// Properties file.
		prop.saveManifest(path.resolve(PartCatConstants.MANIFEST_FILE));
		
		// Image file.
		if (!getImage().isUsingDefaults())
			FileUtilities.writeFileContents(path.resolve(PartCatConstants.IMAGE_FILE),
					getImage().getName());
	}
	
	/**
	 * Deletes the whole component folder.
	 * 
	 * @throws IOException If there's any issues while deleting the directory.
	 */
	public void delete() throws IOException {
		deleted = true;
		if (!FileUtilities.deleteDirectory(path.toFile()))
			throw new IOException("Something went wrong while deleting " + getName());
	}
	
	/**
	 * Checks if a component exists by its name.
	 * 
	 * @param  name      Component name to search for.
	 * @return           True if the component folder exists.
	 */
	public boolean exists(String name) {
		return exists(this.workspace, name);
	}
	
	/**
	 * Checks if a component exists in a workspace.
	 * 
	 * @param  workspace A PartCat active workspace.
	 * @param  name      Component name to search for.
	 * @return           True if the component folder exists.
	 */
	public static boolean exists(PartCatWorkspace workspace, String name) {
		Path path = workspace.getPath()
				.resolve(PartCatConstants.COMPONENTS_ROOT).resolve(name);
		
		return Files.exists(path) && Files.isDirectory(path);
	}
	
	/**
	 * Loads the component image appropriately, taking into account the package,
	 * and etc.
	 */
	private void loadImage() {
		try {
			Path image_path = path.resolve(PartCatConstants.IMAGE_FILE);
			
			if (image_path.toFile().exists()) {
				// Get image name from image declaration file.
				String image_name = FileUtilities.slurpFile(image_path);
				image_name.trim();
				
				image = new ComponentImage(workspace, image_name);
			} else if (getPackage() != null) {
				// Load image based on device package.
				image = new ComponentImage(workspace, getPackage(), true);
			} else {
				// Fallback to a default image.
				image = new ComponentImage(workspace);
			}
		} catch (Exception e) {
			// Fallback to a default image.
			image = new ComponentImage(workspace);
		}
	}
	
	/**
	 * Reloads the image associated with this component.
	 */
	public void reloadImage() {
		loadImage();
	}
	
	/**
	 * Gets the path to the component folder as a {@link Path}.
	 * 
	 * @return Path to the component folder.
	 */
	public Path getPath() {
		return this.path;
	}

	/**
	 * Gets the component name.
	 * 
	 * @return The component name.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Sets the component name.
	 * 
	 * @param name Name of the component.
	 * 
	 * @throws Exception If the name is already set. The {@link #rename()}
	 *                   function should be used instead.
	 */
	public void setName(String name) throws Exception {
		if (this.name != null)
			throw new Exception("Cannot set the name if the component " +
					"already has one");
		
		this.name = name;
	}
	
	/**
	 * Removes the associated image.
	 */
	public void removeImage() {
		try {
			// Delete the image definition file.
			Files.deleteIfExists(path.resolve(PartCatConstants.IMAGE_FILE));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Reload the images.
		reloadImage();
	}
	
	/**
	 * Gets the component image path.
	 * 
	 * @return Component image name or NULL if there isn't one associated.
	 */
	public ComponentImage getImage() {
		return image;
	}
	
	/**
	 * Gets the properties of this component.
	 * 
	 * @return The component properties.
	 */
	public ComponentProperties getProperties() {
		return this.prop;
	}
	
	/**
	 * Gets the quantity of a component.
	 * 
	 * @return The quantity of the component.
	 */
	public int getQuantity() {
		return this.quantity;
	}
	
	/**
	 * Sets the quantity of the component.
	 * 
	 * @param quantity New quantity of the component.
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	/**
	 * Sets the quantity of the component from a {@link String}.
	 * 
	 * @param quantity Quantity number as a {@link String}.
	 */
	public void setQuantity(String quantity) {
		this.quantity = Integer.parseInt(quantity.trim());
	}

	/**
	 * Gets the component notes.
	 * 
	 * @return The component notes.
	 */
	public String getNotes() {
		return this.notes;
	}
	
	/**
	 * Sets the component notes.
	 * 
	 * @param notes Notes to be set to this component.
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	/**
	 * Checks if a component has notes.
	 * 
	 * @return True if the component has notes.
	 */
	public boolean hasNotes() {
		if (notes == null)
			return false;
		
		return !notes.isEmpty();
	}
	
	/**
	 * Downloads a datasheet PDF from the internet and overwrites the file in
	 * the component folder.
	 * 
	 * @param url {@link URL} to a PDF datasheet.
	 * 
	 * @throws IOException If something went wrong when downloading.
	 */
	public void downloadDatasheet(URL url) throws IOException {
		InputStream in = url.openStream();
		Files.copy(in, getDatasheet(), StandardCopyOption.REPLACE_EXISTING);
	}
	
	/**
	 * Deletes the datasheet from the component folder.
	 * 
	 * @throws IOException If a problem occurred while deleting the file.
	 */
	public void removeDatasheet() throws IOException {
		Files.delete(getDatasheet());
	}
	
	/**
	 * Gets the datasheet file {@link Path} for opening.
	 * 
	 * @return The datasheet file {@link Path}.
	 */
	public Path getDatasheet() {
		return path.resolve(PartCatConstants.DATASHEET_FILE);
	}
	
	/**
	 * Checks if a component has a datasheet file associated with it.
	 * 
	 * @return True if the component has a datasheet file available.
	 */
	public boolean hasDatasheet() {
		return getDatasheet().toFile().exists();
	}
	
	/**
	 * Checks if a component has been newly created and hasn't been saved yet.
	 * 
	 * @return True if the component only exists in memory.
	 */
	public boolean isNewlyCreated() {
		return this.newly_created;
	}
	
	/**
	 * Checks is a component is in a deleted state.
	 * 
	 * @return True if the component is in a deleted state.
	 */
	public boolean isDeleted() {
		return this.deleted;
	}
	
	/**
	 * Gets the component category.
	 * 
	 * @return Component category.
	 * @see {@link ComponentCategory}
	 */
	public ComponentCategory getCategory() {
		return new ComponentCategory(prop, false);
	}
	
	/**
	 * Checks if this component has a category attached to its properties.
	 * 
	 * @return True if the component properties has a category field.
	 */
	public boolean hasCategory() {
		for (Map.Entry<String, String> entry : prop.entrySet()) {
			if (entry.getKey().equals(PartCatConstants.PROPERTY_CATEGORY))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Gets the component sub category.
	 * 
	 * @return Component sub category.
	 * @see {@link ComponentCategory}
	 */
	public ComponentCategory getSubCategory() {
		return new ComponentCategory(prop, true);
	}
	
	/**
	 * Checks if this component has a sub category attached to its properties.
	 * 
	 * @return True if the component properties has a sub category field.
	 */
	public boolean hasSubCategory() {
		for (Map.Entry<String, String> entry : prop.entrySet()) {
			if (entry.getKey().equals(PartCatConstants.PROPERTY_SUBCATEGORY))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Gets the device package if it has one.
	 * 
	 * @return Device package or null if it's not defined.
	 */
	public String getPackage() {
		return prop.get("Package");
	}

	/**
	 * Gets a string representation of the component in the form of a summary.
	 */
	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		
		// Build the summary string.
		strBuilder.append("Name: ");
		strBuilder.append(this.getName());
		strBuilder.append("\nPath:");
		strBuilder.append(this.getPath().toString());
		strBuilder.append("\nNewly Created: ");
		strBuilder.append(this.isNewlyCreated());
		strBuilder.append("\nQuantity: ");
		strBuilder.append(this.getQuantity());
		
		// Go through image.
		strBuilder.append("\nImage: ");
		strBuilder.append("\n\tName: ");
		strBuilder.append(this.getImage().getName());
		strBuilder.append("\n\tPath: ");
		strBuilder.append(this.getImage().getPath());
		
		// Go through properties.
		strBuilder.append("\nProperties:\n");
		for (Map.Entry<String, String> entry : prop.entrySet()) {
			strBuilder.append("\t");
			strBuilder.append(entry.getKey());
			strBuilder.append("\t");
			strBuilder.append(entry.getValue());
			strBuilder.append("\n");
		}
		
		// Add the notes.
		strBuilder.append("Notes: ");
		strBuilder.append(this.getNotes());
		
		return strBuilder.toString();
	}
}
