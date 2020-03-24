package com.innoveworkshop.partcat.exceptions;

/**
 * A workspace not opened exception.
 *
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class WorkspaceNotOpenedException extends Exception {
	private static final long serialVersionUID = 5855960793198597854L;

	/**
	 * Creates a generic workspace not opened exception with a pre-defined
	 * message.
	 */
	public WorkspaceNotOpenedException() {
		super("The workspace hasn't been opened yet.");
	}
	
	/**
	 * Creates a generic workspace not opened exception with a pre-defined
	 * message and specifies the root cause via a {@link Throwable}.
	 * 
	 * @param err A root cause exception.
	 */
	public WorkspaceNotOpenedException(Throwable err) {
		super("The workspace hasn't been opened yet.", err);
	}
}
