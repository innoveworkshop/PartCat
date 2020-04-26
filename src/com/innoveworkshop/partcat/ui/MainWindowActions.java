package com.innoveworkshop.partcat.ui;

import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.innoveworkshop.partcat.PartCatWorkspace;
import com.innoveworkshop.partcat.components.Component;
import com.innoveworkshop.partcat.exceptions.WorkspaceNotFoundException;

/**
 * A class to organize the MainWindow action events.
 *
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class MainWindowActions {
	MainWindow window;

	/**
	 * Actions helper class constructor.
	 * 
	 * @param parent The parent window.
	 */
	public MainWindowActions(MainWindow parent) {
		this.window = parent;
	}
	
	/**
	 * Saves a component to disk.
	 * 
	 * @param component Component to be saved.
	 */
	public void saveComponent(Component component) {
		try {
			window.syncComponentChanges();
			component.save();
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(window.frmPartcat,
					"Something went wrong when trying to save the component.",
					"Saving Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Creates a new component from scratch.
	 */
	public void newComponent() {
		String name = JOptionPane.showInputDialog(window.frmPartcat,
				"Enter the new component name:", "New Component",
				JOptionPane.PLAIN_MESSAGE);
		
		// Check if the user hit the cancel button.
		if (name == null)
			return;
		
		// Check if the entered name is empty.
		if (!name.isEmpty()) {
			// Check if the component name already exists.
			if (Component.exists(window.workspace, name)) {
				JOptionPane.showMessageDialog(window.frmPartcat,
						name + " is already in the workspace. Choose a different name.",
						"Name Not Unique", JOptionPane.ERROR_MESSAGE);
				return;
			}
	
			try {
				// Create and save the new component.
				Component component = new Component(window.workspace);
				component.setName(name);
				component.save();
				
				// Refresh the workspace.
				refreshWorkspace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Open a new workspace using a dialog.
	 */
	public void openWorkspace() {
		// Setup the file chooser.
		JFileChooser dialog = new JFileChooser();
		dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		dialog.setDialogTitle("Open Workspace");
		
		// Show the dialog and handle the open operation only if approved.
		if (dialog.showOpenDialog(window.frmPartcat) == JFileChooser.APPROVE_OPTION) {
			try {
				PartCatWorkspace workspace = new PartCatWorkspace(dialog.getSelectedFile().getPath());
				this.openWorkspace(workspace);
			} catch (WorkspaceNotFoundException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(window.frmPartcat,
						"This doesn't look like a valid PartCat workspace folder.",
						"Not a Workspace", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	/**
	 * Open a specified workspace.
	 * 
	 * @param workspace Workspace to open in the window.
	 */
	public void openWorkspace(PartCatWorkspace workspace) {
		this.closeWorkspace();
		window.setWorkspace(workspace);
		window.populateComponentsTree(workspace.componentIterator());
	}
	
	/**
	 * Refreshes the currently opened workspace.
	 */
	public void refreshWorkspace() {
		try {
			Path path = window.workspace.getPath();
			
			this.closeWorkspace();
			this.openWorkspace(new PartCatWorkspace(path));
		} catch (WorkspaceNotFoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(window.frmPartcat,
					"Couldn't find the workspace when trying to reload.",
					"Workspace Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Closes the currently opened workspace.
	 */
	public void closeWorkspace() {
		window.clearComponentTreeAndView();
		window.workspace.close();
	}
	
	/**
	 * Closes the main window.
	 */
	public void closeWindow() {
		window.frmPartcat.dispatchEvent(new WindowEvent(window.frmPartcat,
				WindowEvent.WINDOW_CLOSING));
	}
}
