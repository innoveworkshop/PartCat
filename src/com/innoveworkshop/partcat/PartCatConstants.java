package com.innoveworkshop.partcat;

/**
 * A simple constants-holding class to make the whole thing easier to modify.
 *
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public final class PartCatConstants {
	// File names.
	public static final String MANIFEST_FILE    = "MANIFEST";
	public static final String QUANTITY_FILE    = "QUANTITY";
	public static final String IMAGE_FILE       = "IMAGE";
	public static final String DATASHEET_FILE   = "datasheet.pdf";
	public static final String NOTES_FILE       = "notes.txt";
	
	// Root paths.
	public static final String COMPONENTS_ROOT  = "components";
	public static final String ASSETS_ROOT      = "assets";
	
	// Sub folders paths.
	public static final String IMAGES_DIR = "images";
	
	// Preferences keys.
	public static final String LAST_OPENED_WORKSPACE_KEY = "LastOpenedWorkspace";
	public static final String SELECTED_LOOK_FEEL_KEY = "LookAndFeel";
	public static final String WINDOW_POSITION_X_KEY = "WindowPositionX";
	public static final String WINDOW_POSITION_Y_KEY = "WindowPositionY";
	public static final String WINDOW_WIDTH_KEY = "WindowWidth";
	public static final String WINDOW_HEIGHT_KEY = "WindowHeight";
	
	// Special component property keys.
	public static final String PROPERTY_VALUE = "Value";
	public static final String PROPERTY_CATEGORY = "Category";
	public static final String PROPERTY_SUBCATEGORY = "Sub-Category";
	public static final String PROPERTY_PACKAGE = "Package";
}
