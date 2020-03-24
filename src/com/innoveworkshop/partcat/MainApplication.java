package com.innoveworkshop.partcat;

import java.util.ListIterator;

import com.innoveworkshop.partcat.components.Component;

/**
 * The main application class.
 *
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class MainApplication {
	/**
	 * Application's main entry point.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) throws Exception {
		// Open workspace.
		PartCatWorkspace workspace = new PartCatWorkspace("/home/nathan/partcat-test");
		
		// Go through components.
		ListIterator<Component> iter = workspace.componentIterator();
		while (iter.hasNext()) {
			System.out.println(iter.next().toString());
		}
	}
}
