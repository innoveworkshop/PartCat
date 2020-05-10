package com.innoveworkshop.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * A simple class to help us with our file needs.
 *
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class FileUtilities {
	/**
	 * Slurps the contents of a file.
	 * 
	 * @param  path {@link Path} to the file.
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
	 * @param path     {@link Path} to the file to be written.
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
	 * @param  path {@link Path} to the directory to be deleted.
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
	 * Copies a whole directory recursively.
	 * 
	 * @param source  {@link Path} to the source directory.
	 * @param target  {@link Path} to the target directory.
	 * @param options Necessary copy options.
	 * 
	 * @throws IOException If something goes wrong while copying.
	 */
	public static void copyDirectory(Path source, Path target, CopyOption... options) throws IOException {
		// Create the directory.
		Files.createDirectory(target);
		
		// Copy directory contents recursively.
		Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				Files.createDirectories(target.resolve(source.relativize(dir)));
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.copy(file, target.resolve(source.relativize(file)), options);
				return FileVisitResult.CONTINUE;
			}
		});
	}
	
	/**
	 * Gets the filename from a path and removes the extension.
	 * 
	 * @param  path {@link Path} to the file.
	 * @return      Filename without extension.
	 */
	public static String getFilenameWithoutExt(Path path) {
		if (path == null)
			return null;
		
		return path.getFileName().toString().replaceFirst("[.][^.]+$", "");
	}
	
	/**
	 * Gets the extension of a given file {@link Path}.
	 * 
	 * @param  path       {@link Path} to the file to get the extension.
	 * @param  includeDot Should we include the extension dot?
	 * @return            Extension of the file, or an empty string if the file
	 *                    doesn't exist of doesn't contain one.
	 */
    public static String getFileExtension(Path path, boolean includeDot) {
        String extension = "";
        
		if (path == null)
			return extension;
 
		try {
            String name = path.getFileName().toString();
            
            if (includeDot) {
            	extension = name.substring(name.lastIndexOf("."));
            } else {
            	extension = name.substring(name.lastIndexOf(".") + 1);
            }
        } catch (Exception e) {
            extension = "";
        }
 
        return extension;
    }
    
	/**
	 * Gets the extension without the extension dot from a file {@link Path}.
	 * 
	 * @param  path {@link Path} to the file to get the extension.
	 * @return      Extension of the file, or an empty string if the file
	 *              doesn't exist of doesn't contain one.
	 */
    public static String getFileExtension(Path path) {
    	return getFileExtension(path, false);
    }
}
