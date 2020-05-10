package com.innoveworkshop.partcat.components.comparators;

import java.util.Comparator;

import com.innoveworkshop.partcat.components.ComponentCategory;

/**
 * A class that holds all of the available ways to sort categories.
 * 
 * @see {@link java.util.Collections#sort}
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class CategoryComparators {
	/**
	 * Sorts the categories in alphabetical order.
	 */
	public class Alphabetically implements Comparator<ComponentCategory> {
		@Override
		public int compare(ComponentCategory cat1, ComponentCategory cat2) {
			if ((cat1 == null) || (cat2 == null))
				return 0;
			
			if ((cat1.getName() == null) || (cat2.getName() == null))
				return 0;
			
			return cat1.getName().compareToIgnoreCase(cat2.getName());
		}
	}
}
