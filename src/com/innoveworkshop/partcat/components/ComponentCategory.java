package com.innoveworkshop.partcat.components;

import java.util.Map;

import com.innoveworkshop.partcat.PartCatConstants;

/**
 * A component category abstraction class.
 * 
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class ComponentCategory {
	private String name;
	
	/**
	 * Creates an empty component category.
	 */
	public ComponentCategory() {
		name = null;
	}
	
	/**
	 * Creates a component category with a name already set.
	 * 
	 * @param name Category name.
	 */
	public ComponentCategory(String name) {
		setName(name);
	}
	
	/**
	 * Creates a component category based on the component properties.
	 * 
	 * @param prop Component properties.
	 */
	public ComponentCategory(ComponentProperties prop) {
		// Go through the component properties looking for one named "Category".
		for (Map.Entry<String, String> entry : prop.entrySet()) {
			if (entry.getKey().equals(PartCatConstants.PROPERTY_CATEGORY)) {
				setName(entry.getValue());
			}
		}
	}
	
	/**
	 * Gets the component category name. If a category isn't set, this will return
	 * "Uncategorized".
	 * 
	 * @return Category name.
	 */
	public String getName() {
		if (name == null)
			return "Uncategorized";
		
		return name;
	}
	
	/**
	 * Sets the component category name.
	 * 
	 * @param name Category name.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * String representation of the class. This is the same as {@link #getName()}.
	 * 
	 * @see {@link #getName()}
	 */
	@Override
	public String toString() {
		return getName();
	}
	
	/**
	 * Checks if a category is the same as the other one based on its name.
	 * 
	 * @param  category Comparing category object.
	 * @return          True if the categories are equal.
	 */
	@Override
	public boolean equals(Object category) {
		if (category instanceof ComponentCategory)
			return this.getName().equals(((ComponentCategory) category).getName());
		
		return false;
	}
}
