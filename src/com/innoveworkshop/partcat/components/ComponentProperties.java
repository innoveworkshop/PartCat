package com.innoveworkshop.partcat.components;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	 * @param manifest_path Manifest file path.
	 * 
	 * @throws FileNotFoundException If the manifest file doesn't exist.
	 */
	public void parseManifest(Path manifest_path) throws FileNotFoundException {
		Scanner input = new Scanner(manifest_path.toFile());
		Pattern pattern = Pattern.compile("^([A-Za-z0-9\\-]+): (.+)$");
		
		// Read manifest line-by-line.
		while (input.hasNextLine()) {
			String line = input.nextLine();
			Matcher matcher = pattern.matcher(line);
			
			// Only do something if a valid manifest line was found.
			if (matcher.find()) {
				this.put(matcher.group(1), matcher.group(2));
			} else {
				System.err.println("Invalid manifest line: " + line);
			}
		}
	}
}
