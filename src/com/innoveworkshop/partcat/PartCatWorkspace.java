package com.innoveworkshop.partcat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.innoveworkshop.partcat.components.Component;
import com.innoveworkshop.partcat.components.ComponentCategory;
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
	 * Populates a folder with the structure required to be a workspace.
	 * 
	 * @param  root Path to the root folder where the workspace will live in.
	 * @return      An opened, recently created, workspace.
	 * 
	 * @throws IOException If something goes wrong while trying to create the
	 *                     folder structure of the workspace.
	 */
	public static PartCatWorkspace createNew(Path root) throws IOException {
		// Create the components stuff.
		Files.createDirectories(root.resolve(PartCatConstants.COMPONENTS_ROOT));
		
		// Create the assets stuff.
		Path assets = root.resolve(PartCatConstants.ASSETS_ROOT);
		Files.createDirectories(assets);
		Files.createDirectories(assets.resolve(PartCatConstants.IMAGES_DIR));
		
		try {
			return new PartCatWorkspace(root);
		} catch (WorkspaceNotFoundException e) {
			e.printStackTrace();
			return null;
		}
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
	 * Gets a list of all the component for a given category.
	 * 
	 * @param  category Category to search for components.
	 * @return          A list of components in that category.
	 */
	public ArrayList<Component> getComponentsByCategories(ComponentCategory category) {
		ArrayList<Component> components = new ArrayList<Component>();
		ListIterator<Component> iter = componentIterator();
		
		// Go through components matching the category.
		while (iter.hasNext()) {
			Component comp = iter.next();
			
			if (category.equals(comp.getCategory())) {
				components.add(comp);
			}
		}
		
		return components;
	}
	
	/**
	 * Gets a list of all the component categories (populated with sub-categories)
	 * that are used in this workspace.
	 * 
	 * @return A list of component categories.
	 * 
	 * @see {@link ComponentCategory}
	 */
	public ArrayList<ComponentCategory> getComponentCategories() {
		ArrayList<ComponentCategory> categories = new ArrayList<ComponentCategory>();
		ListIterator<Component> iterComponents = componentIterator();
		
		// Go through components looking for unique categories.
		while (iterComponents.hasNext()) {
			ComponentCategory cat = iterComponents.next().getCategory();
			
			// Looks like a unique category so far?
			if (!categories.contains(cat)) {
				categories.add(cat);
			}
		}
		
		// Populate the categories with their respective sub categories.
		ListIterator<ComponentCategory> iterCategories = categories.listIterator();
		while (iterCategories.hasNext()) {
			populateCategorySubCategories(iterCategories.next());
		}
		
		return categories;
	}
	
	/**
	 * Populates the category object with its sub categories.
	 * 
	 * @param category Category to be populated with sub categories.
	 */
	private void populateCategorySubCategories(ComponentCategory category) {
		ArrayList<Component> components = getComponentsByCategories(category);
		ListIterator<Component> iterComponents = components.listIterator();
		ArrayList<ComponentCategory> subCategories = new ArrayList<ComponentCategory>();

		// Go through components looking for unique sub categories.
		while (iterComponents.hasNext()) {
			ComponentCategory cat = iterComponents.next().getSubCategory();
			
			// Looks like a unique sub category so far?
			if (!subCategories.contains(cat)) {
				subCategories.add(cat);
			}
		}
		
		// Set the sub categories list.
		category.setSubCategories(subCategories);
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
