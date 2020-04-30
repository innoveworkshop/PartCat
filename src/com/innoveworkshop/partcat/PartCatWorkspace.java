package com.innoveworkshop.partcat;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.innoveworkshop.partcat.components.Component;
import com.innoveworkshop.partcat.exceptions.ComponentNotFoundException;
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
		this.open(path);
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
	 * Opens a workspace from a given path.
	 * 
	 * @param path Path to the root of a PartCat workspace folder as a
	 *             {@link Path}.
	 *             
	 * @throws WorkspaceNotFoundException If the specified path wasn't found or
	 *                                    isn't a directory.
	 */
	public void open(Path path) throws WorkspaceNotFoundException {
		this.setPath(path);
		this.populateComponents();
	}
	
	/**
	 * Closes the workspace.
	 */
	public void close() {
		root_path = null;
		opened = false;
		components.clear();
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
	 * Creates a workspace folder and populates it with the structure required.
	 * 
	 * @param root Path to the root where the workspace folder will be created in.
	 * @param name Name of the workspace folder that will be created.
	 */
	public static void createNew(Path root, String name) {
		// TODO: Create a whole new workspace folder structure from scratch for the user.
	}

	/**
	 * Gets the path to the workspace.
	 * 
	 * @return Path to the workspace as a {@link Path}.
	 */
	public Path getPath() {
		return root_path;
	}
	
	/**
	 * Gets the path to the images directory of the workspace.
	 * 
	 * @return Images directory path.
	 */
	public Path getImagesPath() {
		return root_path.resolve(PartCatConstants.ASSETS_ROOT).resolve(PartCatConstants.IMAGES_DIR);
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
		
		root_path = path;
		opened = true;
	}
	
	/**
	 * Gets a component from this workspace by its name.
	 * 
	 * @param  name Name of the component to search for.
	 * @return      The found component.
	 * 
	 * @throws ComponentNotFoundException If the name didn't match any components.
	 */
	public Component getComponent(String name) throws ComponentNotFoundException {
		ListIterator<Component> iter = components.listIterator();
		
		while (iter.hasNext()) {
			Component comp = iter.next();
			if (comp.getName().equals(name))
				return comp;
		}
		
		throw new ComponentNotFoundException(name);
	}
	
	/**
	 * Gets a list iterator for our component list.
	 * 
	 * @return Component list iterator.
	 */
	public ListIterator<Component> componentIterator() {
		return components.listIterator();
	}
	
	/**
	 * Checks if the workspace is currently opened.
	 * 
	 * @return True if the workspace is opened.
	 */
	public boolean isOpen() {
		return opened;
	}
}
