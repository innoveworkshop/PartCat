package com.innoveworkshop.partcat.components.comparators;

import java.util.Comparator;

import com.innoveworkshop.partcat.components.Component;

/**
 * A class that holds all of the available ways to sort components.
 * 
 * @see {@link java.util.Collections#sort}
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class ComponentComparators {
	/**
	 * Sorts the components in alphabetical order.
	 */
	public class Alphabetically implements Comparator<Component> {
		@Override
		public int compare(Component comp1, Component comp2) {
			if ((comp1 == null) || (comp2 == null))
				return 0;
			
			return comp1.getName().compareToIgnoreCase(comp2.getName());
		}
	}
}
