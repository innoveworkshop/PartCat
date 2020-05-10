package com.innoveworkshop.partcat.ui;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import com.innoveworkshop.partcat.PartCatConstants;

/**
 * A simple class to handle all the properties table change events.
 * 
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class PropertiesTableModelListener implements TableModelListener {
	private MainWindow window;
	
	/**
	 * Creates the properties table model listener.
	 * 
	 * @param window Parent window.
	 */
	public PropertiesTableModelListener(MainWindow window) {
		this.window = window;
	}
	
	/**
	 * Handles the event when something on the table changes.
	 * 
	 * @param e Table change event.
	 */
	public void tableChanged(TableModelEvent e) {
		// Get the changed row contents.
		DefaultTableModel model = (DefaultTableModel)e.getSource();
		
		if (model.getRowCount() > 0) {
			if (e.getType() != TableModelEvent.DELETE) {
				String row_key = (String)model.getValueAt(e.getFirstRow(), 0);
			
				if (row_key.equals(PartCatConstants.PROPERTY_PACKAGE)) {
					// We are dealing with a package change.
					window.syncComponentChanges();
					window.currentComponent.reloadImage();
					window.setComponentImageLabel(window.currentComponent);
				}
			}
		}
		
		window.setUnsavedChanges(true);
	}
}
