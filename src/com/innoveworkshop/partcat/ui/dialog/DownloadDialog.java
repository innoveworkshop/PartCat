package com.innoveworkshop.partcat.ui.dialog;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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
	 * Downloads an image for the user.
	 * 
	 * @param  component {@link Component} that will have the image downloaded.
	 * @return           {@link Path} to the downloaded image or {@code null} if
	 *                   there were any problems.
	 */
	public Path image(Component component) {
		try {
			// Get the URL from the user.
			URL url = getURL(IMAGE_FILE);
			
			if (url != null) {
				InputStream in = url.openStream();
				String mime = URLConnection.guessContentTypeFromStream(in);
				System.out.println(mime);
				//Files.copy(in, getDatasheet(), StandardCopyOption.REPLACE_EXISTING);
				downloadSuccessfulMessage(IMAGE_FILE);
			}
		} catch (IOException e) {
			e.printStackTrace();
			downloadErrorMessage(IMAGE_FILE);
			
		}
		
		return null;
	}
	
	/**
	 * Downloads a datasheet for the user.
	 * 
	 * @param  component {@link Component} that will have the datasheet downloaded.
	 * @return           {@link Path} to the downloaded datasheet or {@code null}
	 *                   if there were any problems.
	 */
	public Path datasheet(Component component) {
		try {
			// Get the URL from the user.
			URL url = getURL(DATASHEET_FILE);
			
			if (url != null) {
				component.downloadDatasheet(url);
				downloadSuccessfulMessage(DATASHEET_FILE);
				
				return component.getDatasheet();
			}
		} catch (IOException e) {
			e.printStackTrace();
			downloadErrorMessage(DATASHEET_FILE);
		}
		
		return null;
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
				"Enter the URL to download: ", "Download " + fileType,
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
	
	/**
	 * Shows a friendly error message that something went wrong while trying to
	 * download a file.
	 * 
	 * @param fileType Name of the type of file to get from the user. This is
	 *                 used to display a nice message for the user.
	 */
	private void downloadSuccessfulMessage(String fileType) {
		JOptionPane.showMessageDialog(parent,
				fileType + " downloaded successfully.", "Download Successful",
				JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Shows a friendly error message that something went wrong while trying to
	 * download a file.
	 * 
	 * @param fileType Name of the type of file to get from the user. This is
	 *                 used to display a nice message for the user.
	 */
	private void downloadErrorMessage(String fileType) {
		JOptionPane.showMessageDialog(parent,
				"Something went while downloading the " + fileType.toLowerCase() + ".",
				"Error Downloading", JOptionPane.ERROR_MESSAGE);
	}
}
