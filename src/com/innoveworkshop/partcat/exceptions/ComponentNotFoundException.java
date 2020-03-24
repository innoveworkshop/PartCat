package com.innoveworkshop.partcat.exceptions;

/**
 * A component not found exception.
 *
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class ComponentNotFoundException extends Exception {
	private static final long serialVersionUID = 8330898605510632213L;

	/**
	 * Creates a generic component not found exception with a pre-defined
	 * message.
	 */
	public ComponentNotFoundException() {
		super("The specified component wasn't found.");
	}

	/**
	 * Creates a component not found exception with a message that says the
	 * component name.
	 * 
	 * @param componentName The name of the component to be shown.
	 */
	public ComponentNotFoundException(String componentName) {
		super("The component '" + componentName + "' wasn't found.");
	}

	/**
	 * Creates a generic component not found exception with a pre-defined
	 * message and specifies the root cause via a {@link Throwable}.
	 * 
	 * @param err A root cause exception.
	 */
	public ComponentNotFoundException(Throwable err) {
		super("The specified component wasn't found.", err);
	}

	/**
	 * Creates a component not found exception with a message that says the
	 * component name and specifies the root cause via a {@link Throwable}.
	 * 
	 * @param componentName The name of the component to be shown.
	 * @param err           A root cause exception.
	 */
	public ComponentNotFoundException(String componentName, Throwable err) {
		super("The component '" + componentName + "' wasn't found.", err);
	}
}
