package com.innoveworkshop.partcat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.innoveworkshop.partcat.exceptions.WorkspaceNotFoundException;

/**
 * A PartCat workspace (root folder) abstraction class.
 *
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class PartCatWorkspace {
	private Path root_path;
	private boolean opened;

	/**
	 * Creates an empty workspace object.
	 */
	public PartCatWorkspace() {
		this.root_path = null;
		this.opened = false;
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
		this.setPath(path);
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
	 * Checks if the workspace is currently opened.
	 * 
	 * @return True if the workspace is opened.
	 */
	public boolean isOpen() {
		return this.opened;
	}
}
