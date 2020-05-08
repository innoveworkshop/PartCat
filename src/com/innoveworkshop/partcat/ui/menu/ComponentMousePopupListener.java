package com.innoveworkshop.partcat.ui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import com.innoveworkshop.partcat.components.Component;
import com.innoveworkshop.partcat.ui.ComponentTreeNode;
import com.innoveworkshop.partcat.ui.MainWindow;

/**
 * A mouse adapter class to handle the component list popup menu.
 * 
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class ComponentMousePopupListener extends MouseAdapter {
	private MainWindow window;
	private JTree treeView;
	public JPopupMenu popupMenu;
	public JMenuItem mitmDelete;
	public Component selComponent;
	
	/**
	 * Creates the popup menu for the component list.
	 * 
	 * @param window   Parent window.
	 * @param treeView Components tree view.
	 */
	public ComponentMousePopupListener(final MainWindow window, JTree treeView) {
		// Set the current state of things.
		this.window = window;
		this.treeView = treeView;
		popupMenu = new JPopupMenu();
		selComponent = null;
		
		// New component item.
		JMenuItem menuItem = new JMenuItem("New");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (window.defaultUnsavedChangesBehaviour())
					return;
				
				window.action.newComponent();
			}
		});
		popupMenu.add(menuItem);
		
		// Delete component item.
		mitmDelete = new JMenuItem("Delete");
		mitmDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (window.defaultUnsavedChangesBehaviour())
					return;
				
				int option = JOptionPane.showConfirmDialog(window.frmPartcat,
						"Are you sure you want to delete " + selComponent.getName() + "?",
						"Delete Component", JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
				
				if (option == JOptionPane.YES_OPTION) {
					try {
						selComponent.delete();
					} catch (IOException e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(window.frmPartcat,
								"Something went wrong while trying to delete " + selComponent.getName(),
								"Deletion Error", JOptionPane.ERROR_MESSAGE);
					}
					
					// Clear everything and reload the tree view.
					window.clearComponentTreeAndView();
					window.populateComponentsTree(window.workspace.componentIterator());
				}
			}
		});
		popupMenu.add(mitmDelete);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger() && window.workspace.isOpen()) {
			enableDeleteMenu(getComponentFromPosition(e.getX(), e.getY()));
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger() && window.workspace.isOpen()) {
			enableDeleteMenu(getComponentFromPosition(e.getX(), e.getY()));
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
	
	/**
	 * Retrieves the component object from a tree view by using the mouse
	 * position.
	 * 
	 * @param  x Mouse X position.
	 * @param  y Mouse Y position.
	 * @return   True if we are hovering a component.
	 */
	public boolean getComponentFromPosition(int x, int y) {
		TreePath treePath = treeView.getPathForLocation(x, y);
		if (treePath == null)
			return false;
		
		Object node = treePath.getLastPathComponent();
		if (node instanceof ComponentTreeNode) {
			selComponent = ((ComponentTreeNode)node).getComponent();
			return true;
		}
		
		selComponent = null;
		return false;
	}
	
	/**
	 * Manages if we should enable or disable the delete item.
	 * 
	 * @param enable Should we enable the menu item?
	 */
	private void enableDeleteMenu(boolean enable) {
		mitmDelete.setEnabled(enable);
	}
}
