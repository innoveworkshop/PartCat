package com.innoveworkshop.partcat;

import java.awt.EventQueue;
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
		System.out.println("PartCat Workspace\n");
		
		// Go through components.
		System.out.println("Components:");
		ListIterator<Component> iter = workspace.componentIterator();
		while (iter.hasNext()) {
			System.out.println(iter.next().toString());
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
