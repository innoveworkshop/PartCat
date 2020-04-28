package com.innoveworkshop.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
	
	/**
	 * Overrides the contents of a file. This function will create the file if
	 * it doesn't exist.
	 * 
	 * @param path     Path to the file to be written.
	 * @param contents Contents to be written to the file.
	 * 
	 * @throws IOException If something goes wrong.
	 */
	public static void writeFileContents(Path path, String contents) throws IOException {
		// Open file and create if it doesn't exist.
		File file = new File(path.toString());
		file.createNewFile();  // Just making sure.
		
		// Write contents to the file.
		FileOutputStream outStream = new FileOutputStream(file);
		outStream.write(contents.getBytes());
		outStream.close();
	}
	
	/**
	 * Deletes a whole directory recursively.
	 * 
	 * @param  path Path to the directory to be deleted.
	 * @return      True if the directory was deleted successfully.
	 */
	public static boolean deleteDirectory(File path) {
		File[] contents = path.listFiles();
		
		if (contents != null) {
			for (File file : contents) {
				deleteDirectory(file);
			}
		}
		
		return path.delete();
	}
	
	/**
	 * Gets the filename from a path and removes the extension.
	 * 
	 * @param  path Path to the file.
	 * @return      Filename without extension.
	 */
	public static String getFilenameWithoutExt(Path path) {
		if (path == null)
			return null;
		
		return path.getFileName().toString().replaceFirst("[.][^.]+$", "");
	}
}
