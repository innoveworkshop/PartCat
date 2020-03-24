package com.innoveworkshop.partcat.exceptions;

/**
 * A workspace not found exception.
 *
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class WorkspaceNotFoundException extends Exception {
	private static final long serialVersionUID = 8330898605510632213L;

	/**
	 * Creates a generic workspace not found exception with a pre-defined
	 * message.
	 */
	public WorkspaceNotFoundException() {
		super("The specified workspace wasn't found.");
	}

	/**
	 * Creates a workspace not found exception with a message that says the
	 * workspace path.
	 * 
	 * @param workspacePath The path of the workspace to be shown.
	 */
	public WorkspaceNotFoundException(String workspacePath) {
		super("The workspace at '" + workspacePath + "' wasn't found.");
	}

	/**
	 * Creates a generic workspace not found exception with a pre-defined
	 * message and specifies the root cause via a {@link Throwable}.
	 * 
	 * @param err A root cause exception.
	 */
	public WorkspaceNotFoundException(Throwable err) {
		super("The specified workspace wasn't found.", err);
	}

	/**
	 * Creates a workspace not found exception with a message that says the
	 * workspace path and specifies the root cause via a {@link Throwable}.
	 * 
	 * @param workspacePath The path of the workspace to be shown.
	 * @param err           A root cause exception.
	 */
	public WorkspaceNotFoundException(String workspacePath, Throwable err) {
		super("The workspace at '" + workspacePath + "' wasn't found.", err);
	}
}
