package com.innoveworkshop.partcat.ui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.innoveworkshop.partcat.ui.AddPropertyDialog;
import com.innoveworkshop.partcat.ui.MainWindow;

/**
 * A mouse adapter class to handle the properties table popup menu.
 * 
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class PropertiesMousePopupListener extends MouseAdapter {
	private MainWindow window;
	private int row;
	private boolean isOutsideTable;
	public JTable tblTable;
	public JPopupMenu popupMenu;
	
	/**
	 * Creates the popup menu for the properties table.
	 * 
	 * @param window     Parent window.
	 * @param table      Properties table.
	 * @param showDelete Show the delete menu item?
	 */
	public PropertiesMousePopupListener(final MainWindow window, JTable table, boolean showDelete) {
		JMenuItem menuItem;
		
		// Set the current state of things.
		this.window = window;
		row = -1;
		tblTable = table;
		isOutsideTable = !showDelete;
		popupMenu = new JPopupMenu();
		
		// Add property item.
		menuItem = new JMenuItem("Add");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addProperty();
			}
		});
		popupMenu.add(menuItem);
		
		// Remove property item.
		menuItem = new JMenuItem("Remove");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int option = JOptionPane.showConfirmDialog(window.frmPartcat,
						"Are you sure you want to delete this row?",
						"Delete Parameter", JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
				
				if (option == JOptionPane.YES_OPTION)
					removeTableRow();
			}
		});
		menuItem.setEnabled(showDelete);
		popupMenu.add(menuItem);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger() && (window.currentComponent != null)) {
			row = tblTable.rowAtPoint(e.getPoint());
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger() && (window.currentComponent != null)) {
			row = tblTable.rowAtPoint(e.getPoint());
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if ((e.getClickCount() == 2) && isOutsideTable
				&& (window.currentComponent != null)) {
			addProperty();
		}
	}
	
	/**
	 * Add a property using the dialog.
	 */
	public void addProperty() {
		AddPropertyDialog dialog = new AddPropertyDialog();
		
		if (dialog.showDialog(window.frmPartcat) == AddPropertyDialog.ADD_PROPERTY_OPTION) {
			DefaultTableModel model = (DefaultTableModel)tblTable.getModel();
			model.addRow(new Object[] { dialog.getName(), dialog.getValue() });
		}
	}
	
	/**
	 * Adds a new blank row to the table.
	 * 
	 * @param key   Property key.
	 * @param value Property value.
	 */
	public void addTableRow(String key, String value) {
		DefaultTableModel model = (DefaultTableModel)tblTable.getModel();
		model.addRow(new Object[] { key, value });
		window.setUnsavedChanges(true);
	}
	
	/**
	 * Removes a specific row from the table.
	 */
	public void removeTableRow() {
		DefaultTableModel model = (DefaultTableModel)tblTable.getModel();
		model.removeRow(row);
		window.setUnsavedChanges(true);
	}
}
