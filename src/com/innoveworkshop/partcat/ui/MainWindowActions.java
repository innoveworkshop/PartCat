package com.innoveworkshop.partcat.ui;

import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFileChooser;

import com.innoveworkshop.partcat.PartCatWorkspace;
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
				// TODO: Show error dialog.
				e.printStackTrace();
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
		}
	}
	
	/**
	 * Closes the currently opened workspace.
	 */
	public void closeWorkspace() {
		window.treeComponents.setModel(null);
		window.clearComponentView();
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
