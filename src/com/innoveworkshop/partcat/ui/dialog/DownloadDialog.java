package com.innoveworkshop.partcat.ui.dialog;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.innoveworkshop.partcat.components.Component;

/**
 * A simple class to help us deal with our download dialogs.
 * 
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class DownloadDialog {
	public static final String GENERIC_FILE = "File";
	public static final String DATASHEET_FILE = "Datasheet";
	public static final String IMAGE_FILE = "Image";
	
	private JFrame parent;
	
	/**
	 * Initializes the download dialog object.
	 * 
	 * @param parent Parent frame to the dialog.
	 */
	public DownloadDialog(JFrame parent) {
		this.parent = parent;
	}
	
	/**
	 * Downloads a datasheet for the user.
	 * 
	 * @param component Component that will have the datasheet downloaded for.
	 */
	public void datasheet(Component component) {
		try {
			// Get the URL from the user.
			URL url = getURL(DATASHEET_FILE);
			
			if (url != null) {
				component.downloadDatasheet(url);
				JOptionPane.showMessageDialog(parent,
						"Datasheet downloaded successfully.", "Download Successful",
						JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(parent,
					"Something went while downloading the datasheet.",
					"Error Downloading", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Gets a URL from the user using a simple dialog.
	 * 
	 * @param  fileType Name of the type of file to get from the user. This is
	 *                  used to display a nice message for the user.
	 * @return          The URL the user entered or {@code null} if they
	 *                  canceled, entered an empty, or malformed URL.
	 */
	public URL getURL(String fileType) {
		String strURL = JOptionPane.showInputDialog(parent,
				"Enter the URL to download:", "Download " + fileType,
				JOptionPane.PLAIN_MESSAGE);
		
		// Check if the user hit the cancel button or entered an empty string.
		if (strURL == null)
			return null;
		if (strURL.isEmpty())
			return null;
		
		try {
			return new URL(strURL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(parent,
					"This URL doesn't look valid.", "Malformed URL",
					JOptionPane.ERROR_MESSAGE);
		}
		
		return null;
	}
}
