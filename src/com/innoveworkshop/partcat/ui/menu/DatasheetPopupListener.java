package com.innoveworkshop.partcat.ui.menu;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import com.innoveworkshop.partcat.ui.MainWindow;

/**
 * A mouse adapter class to handle the datasheet popup menu.
 * 
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class DatasheetPopupListener extends MouseAdapter {
	private MainWindow window;
	public JPopupMenu popupMenu;
	public JMenuItem mitmOpen;
	public JMenuItem mitmRemove;
	
	/**
	 * Creates the popup menu for the component datasheet button.
	 * 
	 * @param window Parent window.
	 */
	public DatasheetPopupListener(final MainWindow window) {
		JMenuItem menuItem;
		
		// Set the current state of things.
		this.window = window;
		popupMenu = new JPopupMenu();
		
		// Open datasheet item.
		mitmOpen = new JMenuItem("Open");
		mitmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openDatasheet();
			}
		});
		popupMenu.add(mitmOpen);
		
		// Download datasheet item.
		menuItem = new JMenuItem("Download");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				downloadDatasheet();
			}
		});
		popupMenu.add(menuItem);
		
		// Remove datasheet item.
		mitmRemove = new JMenuItem("Remove");
		mitmRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeDatasheet();
			}
		});
		popupMenu.add(mitmRemove);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger() && (window.currentComponent != null)) {
			showPopup(e);
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger() && (window.currentComponent != null)) {
			showPopup(e);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if ((e.getClickCount() >= 2) && (window.currentComponent != null)) {
			// Double-click opens the datasheet directly.
			openDatasheet();
		} else {
			// Single-click shows the popup menu.
			showPopup(e);
		}
	}
	
	/**
	 * Shows the popup menu.
	 * 
	 * @param e A {@link MouseEvent} that triggered the menu opening.
	 */
	public void showPopup(MouseEvent e) {
		// Don't show the menu if there isn't a component selected.
		if (window.currentComponent == null)
			return;
		
		// Enable only the necessary bits.
		mitmOpen.setEnabled(window.currentComponent.hasDatasheet());
		mitmRemove.setEnabled(window.currentComponent.hasDatasheet());
		
		// Show the menu.
		popupMenu.show(e.getComponent(), e.getX(), e.getY());
	}
	
	/**
	 * Opens the datasheet using the default application for it.
	 */
	public void openDatasheet() {
		// Check for desktop support.
		if (!Desktop.isDesktopSupported()) {
			JOptionPane.showMessageDialog(window.frmPartcat,
					"Unable to use the Desktop class for opening files.",
					"Java Support Error", JOptionPane.WARNING_MESSAGE);
			return;
		}

		// Open the datasheet with the default application.
		try {
			File datasheet = window.currentComponent.getDatasheet().toFile();
			Desktop.getDesktop().open(datasheet);
		} catch (IOException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(window.frmPartcat,
					"An error occured while trying to open the datasheet with the default application.",
					"Error Opening Datasheet", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Downloadss the datasheet using the default application for it.
	 */
	public void downloadDatasheet() {
		String strURL = JOptionPane.showInputDialog(window.frmPartcat,
				"Enter the URL to download:", "Download Datasheet",
				JOptionPane.PLAIN_MESSAGE);
		
		// Check if the user hit the cancel button or entered an empty string.
		if (strURL == null)
			return;
		if (strURL.isEmpty())
			return;
		
		try {
			window.currentComponent.downloadDatasheet(new URL(strURL));
			JOptionPane.showMessageDialog(window.frmPartcat,
					"Datasheet downloaded successfully.", "Download Successful",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(window.frmPartcat,
					"This URL doesn't look valid.", "Malformed URL",
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(window.frmPartcat,
					"Something went while downloading the datasheet.",
					"Error Downloading", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Removes the datasheet.
	 */
	public void removeDatasheet() {
		int option = JOptionPane.showConfirmDialog(window.frmPartcat,
			"Are you sure you want to delete the datasheet?", "Delete Datasheet",
			JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		
		if (option == JOptionPane.YES_OPTION) {
			try {
				window.currentComponent.removeDatasheet();
			} catch (IOException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(window.frmPartcat,
						"Something went wrong while trying to delete the datasheet.",
						"Deletion Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
