package com.innoveworkshop.partcat.components;

import java.nio.file.Path;
import java.util.HashMap;

import com.innoveworkshop.partcat.PartCatWorkspace;
import com.innoveworkshop.partcat.exceptions.ComponentNotFoundException;

/**
 * A component abstraction class. This represents a single populated component
 * folder.
 *
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class Component {
	private PartCatWorkspace workspace;
	private Path root;
	private String name;
	private HashMap<String, String> properties;
	private String notes;
	private boolean newly_created;

	/**
	 * Creates an empty component object.
	 * 
	 * @param workspace A PartCat active workspace.
	 */
	public Component(PartCatWorkspace workspace) {
		this.workspace = workspace;
		
		this.root = null;
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
	 */
	public Component(PartCatWorkspace workspace, String name) {
		// Initialize an empty object with a name.
		this(workspace);
		this.name = name;
		
		// Check if we are creating a new component or loading one.
		if (this.exists(name)) {
			this.newly_created = false;
			
			// TODO: Get component root path.
			
			// TODO: Load properties.
			
			// TODO: Load notes.
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
	 * Checks if a component has been newly created and hasn't been saved yet.
	 * 
	 * @return True if the component only exists in memory.
	 */
	public boolean isNewlyCreated() {
		return this.newly_created;
	}
}
