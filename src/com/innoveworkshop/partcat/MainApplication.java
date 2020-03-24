package com.innoveworkshop.partcat;

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
		PartCatWorkspace workspace = new PartCatWorkspace("/home/nathan/partcat-test");
		Component comp = new Component(workspace, "BC817");
		
		System.out.println(comp.toString());
	}
}
