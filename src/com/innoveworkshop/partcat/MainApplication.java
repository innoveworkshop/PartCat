package com.innoveworkshop.partcat;

import java.awt.EventQueue;
import java.util.prefs.Preferences;

import com.innoveworkshop.partcat.ui.MainWindow;

/**
 * The main application class.
 *
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class MainApplication {
	private static PartCatWorkspace workspace;
	private static Preferences prefs;
	
	/**
	 * Application's main entry point.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) throws Exception {
		// Get preferences.
		prefs = Preferences.userNodeForPackage(MainApplication.class.getClass());
		String lastWorkspace = prefs.get(PartCatConstants.LAST_OPENED_WORKSPACE_KEY, null);
		
		// Open workspace.
		workspace = null;
		if (lastWorkspace != null)
			workspace = new PartCatWorkspace(lastWorkspace);
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow(prefs);
					window.show();
					
					// Load last used workspace.
					if (workspace != null) {
						window.setWorkspace(workspace);
						window.populateComponentsTree(workspace.componentIterator());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
