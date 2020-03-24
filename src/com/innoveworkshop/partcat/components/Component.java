package com.innoveworkshop.partcat.components;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import com.innoveworkshop.partcat.PartCatConstants;
import com.innoveworkshop.partcat.PartCatWorkspace;
import com.innoveworkshop.partcat.exceptions.WorkspaceNotOpenedException;

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
	private HashMap<String, String> properties;
	private String notes;
	private boolean newly_created;

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
		
		this.workspace = workspace;
		this.path = null;
		this.name = null;
		this.notes = null;
		this.properties = new HashMap<String, String>();
		this.newly_created = true;
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
	 */
	public Component(PartCatWorkspace workspace, String name) throws WorkspaceNotOpenedException,
																	FileNotFoundException,
																	IOException {
		// Initialize an empty object with a name.
		this(workspace);
		this.name = name;
		
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
			this.populateFromPath(path);
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
		// TODO: Load properties.
		
		// Load the notes file.
		try {
			// Get file path.
			Path notes_path = path.resolve(PartCatConstants.NOTES_FILE);
			File file = notes_path.toFile();
			
			// Read its contents.
			FileInputStream inStream = new FileInputStream(file);
			byte[] data = new byte[(int)file.length()];
			inStream.read(data);
			inStream.close();
			
			// Set the notes property.
			notes = new String(data, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			notes = null;
		}
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
		return true;
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
	 * Checks if a component has been newly created and hasn't been saved yet.
	 * 
	 * @return True if the component only exists in memory.
	 */
	public boolean isNewlyCreated() {
		return this.newly_created;
	}

	/**
	 * Gets a string representation of the component in the for of a summary.
	 */
	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		
		// Build the summary string.
		strBuilder.append("Name: ");
		strBuilder.append(this.getName());
		strBuilder.append("\nPath:");
		strBuilder.append(path.toString());
		strBuilder.append("\nNewly Created: ");
		strBuilder.append(this.isNewlyCreated());
		// TODO: Go through properties.
		strBuilder.append("\nNotes: ");
		strBuilder.append(this.getNotes());
		
		return strBuilder.toString();
	}
}
