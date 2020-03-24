package com.innoveworkshop.partcat;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.innoveworkshop.partcat.components.Component;
import com.innoveworkshop.partcat.exceptions.WorkspaceNotFoundException;

/**
 * A PartCat workspace (root folder) abstraction class.
 *
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class PartCatWorkspace {
	private Path root_path;
	private List<Component> components;
	private boolean opened;

	/**
	 * Creates an empty workspace object.
	 */
	public PartCatWorkspace() {
		this.root_path = null;
		this.opened = false;
		this.components = new ArrayList<Component>();
	}
	
	/**
	 * Opens a workspace from a given path.
	 * 
	 * @param path Path to the root of a PartCat workspace folder as a
	 *             {@link Path}.
	 *             
	 * @throws WorkspaceNotFoundException If the specified path wasn't found or
	 *                                    isn't a directory.
	 */
	public PartCatWorkspace(Path path) throws WorkspaceNotFoundException {
		this();
		this.setPath(path);
		this.populateComponents();
	}
	
	/**
	 * Opens a workspace from a given string path.
	 * 
	 * @param path Path to the root of a PartCat workspace folder as a
	 *             {@link String}.
	 *             
	 * @throws WorkspaceNotFoundException If the specified path wasn't found or
	 *                                    isn't a directory.
	 */
	public PartCatWorkspace(String path) throws WorkspaceNotFoundException {
		this(Paths.get(path));
	}
	
	/**
	 * Populates the components list of the workspace.
	 */
	public void populateComponents() {
		// Clear the list if we have something there.
		if (components.size() > 0)
			components.clear();
		
		// Go through the components folder.
		Path components_dir = root_path.resolve(PartCatConstants.COMPONENTS_ROOT);
		for (File file : components_dir.toFile().listFiles()) {
			// Only add to the list if the file is actually a directory.
			if (file.isDirectory()) {
				try {
					components.add(new Component(this, file.getName()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Gets the path to the workspace.
	 * 
	 * @return Path to the workspace as a {@link Path}.
	 */
	public Path getPath() {
		return this.root_path;
	}
	
	/**
	 * Sets the workspace path.
	 * 
	 * @param path Path to the root of a PartCat workspace folder as a
	 *             {@link Path}.
	 *             
	 * @throws WorkspaceNotFoundException If the specified path wasn't found or
	 *                                    isn't a directory.
	 */
	public void setPath(Path path) throws WorkspaceNotFoundException {
		// Check if the path actually exists.
		if (!Files.exists(path) && !Files.isDirectory(path))
			 throw new WorkspaceNotFoundException(path.toString());
		
		this.root_path = path;
		this.opened = true;
	}
	
	/**
	 * Gets a list iterator for our component list.
	 * 
	 * @return Component list iterator.
	 */
	public ListIterator<Component> componentIterator() {
		return this.components.listIterator();
	}
	
	/**
	 * Checks if the workspace is currently opened.
	 * 
	 * @return True if the workspace is opened.
	 */
	public boolean isOpen() {
		return this.opened;
	}
}
