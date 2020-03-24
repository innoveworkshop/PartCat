package com.innoveworkshop.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

/**
 * A simple class to help us with our file needs.
 *
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class FileUtilities {
	/**
	 * Slurps the contents of a file.
	 * 
	 * @param  path Path to the file as a {@link Path}
	 * @return      Contents of the file.
	 * 
	 * @throws FileNotFoundException If the file wasn't found.
	 * @throws IOException           If there were any problems when reading the
	 *                               contents of the file.
	 */
	public static String slurpFile(Path path) throws FileNotFoundException, IOException {
		// Get file path.
		File file = path.toFile();
		
		// Read its contents.
		FileInputStream inStream = new FileInputStream(file);
		byte[] data = new byte[(int)file.length()];
		inStream.read(data);
		inStream.close();
		
		// Set the notes property.
		return new String(data, "UTF-8");
	}
}