package com.innoveworkshop.partcat.ui;

import java.awt.Desktop;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.innoveworkshop.partcat.PartCatWorkspace;
import com.innoveworkshop.partcat.components.Component;
import com.innoveworkshop.partcat.exceptions.WorkspaceNotFoundException;
import com.innoveworkshop.partcat.ui.dialog.DownloadDialog;

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
	 * Sets the component image based on a file path and handles all the UI
	 * changes.
	 * 
	 * @param component {@link Component} to have its image changed.
	 * @param imgPath   {@link Path} to the image for this component.
	 */
	public void setComponentImage(Component component, Path imgPath) {
		component.getImage().setPath(imgPath, component.getName());
		window.setComponentImageLabel(component);
		window.setUnsavedChanges(true);
	}
	
	/**
	 * Shows an open file chooser and updates the image asset of a component
	 * object accordingly.
	 * 
	 * @param component {@link Component} to have its image changed.
	 */
	public void selectComponentImage(Component component) {
		// Setup the file chooser.
		JFileChooser dialog = new JFileChooser();
		dialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
		dialog.setAcceptAllFileFilterUsed(true);
		dialog.setDialogTitle("Select Component Image");
		dialog.setCurrentDirectory(window.workspace.getImagesPath().toFile());
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files",
				"gif", "png", "jpg", "jpeg", "bmp");
		dialog.addChoosableFileFilter(filter);
		dialog.setFileFilter(filter);
		
		// Show the dialog and handle the open operation only if approved.
		if (dialog.showDialog(window.frmPartcat, "Select Image") == JFileChooser.APPROVE_OPTION)
			setComponentImage(component, dialog.getSelectedFile().toPath());
	}
	
	/**
	 * Let's the user set a URL to download the image from and updates the image
	 * asset of a component object accordingly.
	 * 
	 * @param component {@link Component} to have its image changed.
	 */
	public void downloadComponentImage(Component component) {
		// Download the image to a temporary directory.
		DownloadDialog download = new DownloadDialog(window.frmPartcat);
		Path imgPath = download.image(window.currentComponent);
		
		// Set the image as if it were selected from the open dialog.
		if (imgPath != null)
			setComponentImage(component, imgPath);
	}
	
	/**
	 * Saves a component to disk.
	 * 
	 * @param component {@link Component} to be saved.
	 */
	public void saveComponent(Component component) {
		saveComponent(component, null);
	}
	
	/**
	 * Saves a component to disk.
	 * 
	 * @param component {@link Component} to be saved.
	 * @param newName   New component name in case of "Saving as".
	 */
	public void saveComponent(Component component, String newName) {
		try {
			// Sync our changes with the component object.
			window.syncComponentChanges();
			
			// Decide if we are saving or saving as.
			if (newName == null) {
				component.save();
			} else {
				component.saveAs(newName);
			}
			
			// Reset the saved changes flag and refresh the workspace.
			window.setUnsavedChanges(false);
			refreshWorkspace();
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(window.frmPartcat,
					"Something went wrong when trying to save the component.",
					"Saving Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Saves a component "as" to disk.
	 * 
	 * @param component {@link Component} to be saved.
	 */
	public void saveComponentAs(Component component) {
		String name = JOptionPane.showInputDialog(window.frmPartcat,
				"Enter the new name for " + component.getName() + ":",
				"Save " + component.getName() + " As",
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
	
			// Finally save the component as usual.
			saveComponent(component, name);
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
				window.setUnsavedChanges(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Open the component folder.
	 */
	public void openComponentFolder() {
		// Check for desktop support.
		if (!Desktop.isDesktopSupported()) {
			JOptionPane.showMessageDialog(window.frmPartcat,
					"Unable to use the Desktop class for opening files.",
					"Java Support Error", JOptionPane.WARNING_MESSAGE);
			return;
		}

		// Open the folder.
		try {
			Desktop.getDesktop().open(window.currentComponent.getPath().toFile());
		} catch (IOException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(window.frmPartcat,
					"An error occured while trying to open the component folder using the default application.",
					"Error Opening Datasheet", JOptionPane.ERROR_MESSAGE);
		}
	}
	/**
	 * Creates a new workspace from scratch using a dialog.
	 */
	public void createWorkspace() {
		// Setup the file chooser.
		JFileChooser dialog = new JFileChooser();
		dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		dialog.setDialogTitle("Create Workspace Root Folder");
		
		// Show the dialog and handle the create operation only if approved.
		if (dialog.showDialog(window.frmPartcat, "Select Workspace Root") == JFileChooser.APPROVE_OPTION) {
			try {
				Path path = Paths.get(dialog.getSelectedFile().getPath());
				PartCatWorkspace workspace = PartCatWorkspace.createNew(path);
				
				openWorkspace(workspace);
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(window.frmPartcat,
						"Something went wrong while trying to create the workspace.",
						"Problems Creating Workspace", JOptionPane.ERROR_MESSAGE);
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
		if (dialog.showDialog(window.frmPartcat, "Open Workspace") == JFileChooser.APPROVE_OPTION) {
			try {
				PartCatWorkspace workspace = new PartCatWorkspace(dialog.getSelectedFile().getPath());
				openWorkspace(workspace);
			} catch (WorkspaceNotFoundException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(window.frmPartcat,
						"This doesn't look like a valid PartCat workspace folder.",
						"Not a Workspace", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		window.setUnsavedChanges(false);
	}
	
	/**
	 * Open a specified workspace.
	 * 
	 * @param workspace Workspace to open in the window.
	 */
	public void openWorkspace(PartCatWorkspace workspace) {
		openWorkspace(workspace, false);
	}
	
	/**
	 * Open a specified workspace.
	 * 
	 * @param workspace    Workspace to open in the window.
	 * @param isRefreshing Are we doing just a workspace refresh?
	 */
	public void openWorkspace(PartCatWorkspace workspace, boolean isRefreshing) {
		closeWorkspace(isRefreshing);
		window.setWorkspace(workspace);
		window.populateComponentsTree();
		window.setUnsavedChanges(false);
		window.setLastOpenedWorkspace(workspace);
	}
	
	/**
	 * Refreshes the currently opened workspace.
	 */
	public void refreshWorkspace() {
		try {
			Path path = window.workspace.getPath();
			
			// Actually refresh the workspace.
			closeWorkspace(true);
			openWorkspace(new PartCatWorkspace(path), true);
			
			// Reload the component that was last in use.
			window.restoreSelectedComponent();
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
		closeWorkspace(false);
	}
	
	/**
	 * Closes the currently opened workspace.
	 * 
	 * @param isRefreshing Are we doing just a workspace refresh?
	 */
	public void closeWorkspace(boolean isRefreshing) {
		window.setUnsavedChanges(false);
		window.clearComponentTreeAndView(!isRefreshing);
		
		if (window.workspace != null)
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
