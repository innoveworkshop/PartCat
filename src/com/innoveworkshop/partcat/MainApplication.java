package com.innoveworkshop.partcat;

import java.awt.EventQueue;
import java.util.prefs.Preferences;

import com.innoveworkshop.partcat.resources.ApplicationResources;
import com.innoveworkshop.partcat.ui.MainWindow;

/**
 * The main application class.
 *
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class MainApplication {
	private static PartCatWorkspace workspace;
	private static Preferences prefs;
	private static ApplicationResources res;
	
	/**
	 * Application's main entry point.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) throws Exception {
		// Get resources.
		res = new ApplicationResources();
		
		// Get preferences.
		prefs = Preferences.userNodeForPackage(MainApplication.class.getClass());
		String lastWorkspace = prefs.get(PartCatConstants.LAST_OPENED_WORKSPACE_KEY, null);
		
		// Open workspace.
		try {
			workspace = null;
			if (lastWorkspace != null)
				workspace = new PartCatWorkspace(lastWorkspace);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Looks like the last used workspace is no longer valid. " + 
					"Ignoring...");
			workspace = null;
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow(res, prefs);
					window.show();
					
					// Load last used workspace.
					if (workspace != null) {
						window.setWorkspace(workspace);
						window.populateComponentsTree();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
