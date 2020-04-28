package com.innoveworkshop.partcat.components;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.innoveworkshop.utilities.FileUtilities;

/**
 * A collection of properties related to a {@link Component}.
 *
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class ComponentProperties extends HashMap<String, String> {
	private static final long serialVersionUID = -3136245942637832206L;

	/**
	 * Initializes an empty properties collection.
	 */
	public ComponentProperties() {
	}
	
	/**
	 * Populates the properties with data from a manifest file.
	 * 
	 * @param manifestPath Manifest file path.
	 * 
	 * @throws FileNotFoundException If the manifest file doesn't exist.
	 */
	public void parseManifest(Path manifestPath) throws FileNotFoundException {
		File file = manifestPath.toFile();
		if (!file.exists())
			throw new FileNotFoundException("Couldn't locate the component manifest in path " +
					manifestPath.toString());
		
		Scanner input = new Scanner(file);
		Pattern pattern = Pattern.compile("^([A-Za-z0-9\\-]+): (.+)$");
		
		// Read manifest line-by-line.
		while (input.hasNextLine()) {
			String line = input.nextLine().trim();
			
			// Ignore empty lines.
			if (line.isEmpty())
				continue;
			
			// Only do something if a valid manifest line was found.
			Matcher matcher = pattern.matcher(line);
			if (matcher.find()) {
				this.put(matcher.group(1), matcher.group(2));
			} else {
				System.err.println("Invalid manifest line: " + line);
			}
		}
		
		input.close();
	}
	
	/**
	 * Saves the component properties as a manifest file.
	 * 
	 * @param manifestPath Manifest file path.
	 * 
	 * @throws IOException If something goes wrong when saving the file.
	 */
	public void saveManifest(Path manifestPath) throws IOException {
		// Build manifest file contents string.
		StringBuilder strBuilder = new StringBuilder();
		for (Map.Entry<String, String> entry : this.entrySet()) {
			strBuilder.append(entry.getKey());
			strBuilder.append(": ");
			strBuilder.append(entry.getValue());
			strBuilder.append("\n");
		}
		
		// Save contents to file.
		FileUtilities.writeFileContents(manifestPath, strBuilder.toString());
	}
}
