package com.innoveworkshop.partcat.ui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.innoveworkshop.partcat.ui.MainWindow;

/**
 * A mouse adapter class to handle the image popup menu.
 * 
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class ImageMousePopupListener extends MouseAdapter {
	private MainWindow window;
	public JPopupMenu popupMenu;
	
	/**
	 * Creates the popup menu for the component image label.
	 * 
	 * @param window Parent window.
	 */
	public ImageMousePopupListener(final MainWindow window) {
		JMenuItem menuItem;
		
		// Set the current state of things.
		this.window = window;
		popupMenu = new JPopupMenu();
		
		// Add image item.
		menuItem = new JMenuItem("Add");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				window.action.selectComponentImage(window.currentComponent);
			}
		});
		popupMenu.add(menuItem);
		
		// Download image item.
		menuItem = new JMenuItem("Download");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: Implement a download dialog and function here.
				window.action.selectComponentImage(window.currentComponent);
			}
		});
		popupMenu.add(menuItem);
		
		// Remove image item.
		menuItem = new JMenuItem("Remove");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				window.currentComponent.removeImage();
				window.setComponentImageLabel(window.currentComponent);
				window.setUnsavedChanges(true);
			}
		});
		popupMenu.add(menuItem);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger() && (window.currentComponent != null)) {
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger() && (window.currentComponent != null)) {
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if ((e.getClickCount() == 2) && (window.currentComponent != null)) {
			window.action.selectComponentImage(window.currentComponent);
		}
	}
}
