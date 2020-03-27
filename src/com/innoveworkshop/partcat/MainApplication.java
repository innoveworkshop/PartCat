package com.innoveworkshop.partcat;

import java.awt.EventQueue;

import com.innoveworkshop.partcat.ui.MainWindow;

/**
 * The main application class.
 *
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class MainApplication {
	private static PartCatWorkspace workspace;
	
	/**
	 * Application's main entry point.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) throws Exception {
		// Open workspace.
		workspace = new PartCatWorkspace("/home/nathan/partcat-test");
		System.out.println("PartCat Workspace\n");
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow(workspace);
					window.show();
					window.setComponentsViewIterator(workspace.componentIterator());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
