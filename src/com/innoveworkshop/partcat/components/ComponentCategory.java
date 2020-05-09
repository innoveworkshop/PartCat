package com.innoveworkshop.partcat.components;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map;

import com.innoveworkshop.partcat.PartCatConstants;

/**
 * A component category abstraction class.
 * 
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class ComponentCategory {
	private ArrayList<ComponentCategory> subCategories;
	private boolean isSubCategory;
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
	 * Creates a component (sub-)category based on the component properties.
	 * 
	 * @param prop          Component properties.
	 * @param isSubCategory Is this a sub category?
	 */
	public ComponentCategory(ComponentProperties prop, boolean isSubCategory) {
		// Sets the sub category flag.
		this.isSubCategory = isSubCategory;
		
		// Go through the component properties looking for one named "(Sub-)Category".
		for (Map.Entry<String, String> entry : prop.entrySet()) {
			if ((entry.getKey().equals(PartCatConstants.PROPERTY_CATEGORY) && !isSubCategory) ||
					(entry.getKey().equals(PartCatConstants.PROPERTY_SUBCATEGORY) && isSubCategory)) {
				setName(entry.getValue());
			}
		}
	}
	
	/**
	 * Creates a component category based on the component properties.
	 * 
	 * @param prop Component properties.
	 */
	public ComponentCategory(ComponentProperties prop) {
		this(prop, false);
	}
	
	/**
	 * Gets the component category name. If a category isn't set, this will return
	 * "Uncategorized".
	 * 
	 * @return Category name.
	 */
	public String getName() {
		if ((name == null) && !isSubCategory())
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
	 * Gets the iterator for the sub categories list.
	 * 
	 * @return Sub categories {@link ListIterator}.
	 */
	public ListIterator<ComponentCategory> getSubCategoriesIterator() {
		return subCategories.listIterator();
	}
	
	/**
	 * Sets the sub categories list.
	 * 
	 * @param subCategories Sub categories {@link ArrayList}.
	 */
	public void setSubCategories(ArrayList<ComponentCategory> subCategories) {
		this.subCategories = subCategories;
	}
	
	/**
	 * Appends a sub category to the sub categories list.
	 * 
	 * @param category Component sub category.
	 */
	public void appendSubCategory(ComponentCategory category) {
		subCategories.add(category);
	}
	
	/**
	 * Is this a sub category?
	 * 
	 * @return True if this is a sub category.
	 */
	public boolean isSubCategory() {
		return isSubCategory;
	}
	
	/**
	 * Sets this category as a sub category.
	 * 
	 * @param isSubCategory Is this a sub category?
	 */
	public void setAsSubCategory(boolean isSubCategory) {
		this.isSubCategory = isSubCategory;
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
		if (category instanceof ComponentCategory) {
			if (this.getName() == null)
				return false;
			
			return this.getName().equals(((ComponentCategory) category).getName());
		}
		
		return false;
	}
}
