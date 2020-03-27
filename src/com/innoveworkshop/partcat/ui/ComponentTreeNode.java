package com.innoveworkshop.partcat.ui;

import javax.swing.tree.DefaultMutableTreeNode;

import com.innoveworkshop.partcat.components.Component;

/**
 * A class to define a component in the component tree view list.
 *
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class ComponentTreeNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = -7779937697861432216L;
	private Component component;

	/**
	 * Create an empty tree node.
	 */
	public ComponentTreeNode() {
		super();
		this.component = null;
	}

	/**
	 * Creates a ready populated component tree node.
	 * 
	 * @param component Component to be associated with this node.
	 */
	public ComponentTreeNode(Component component) {
		this();
		this.setComponent(component);
	}

	/**
	 * Creates a ready populated component tree node and sets if it should allow
	 * children to be placed beneath it or not.
	 * 
	 * @param component      Component to be associated with this node.
	 * @param allowsChildren Should it allow children to be placed in it?
	 */
	public ComponentTreeNode(Component component, boolean allowsChildren) {
		this(component);
		this.setAllowsChildren(allowsChildren);
	}
	
	/**
	 * Gets the component associated with this tree node.
	 * 
	 * @return Associated component.
	 */
	public Component getComponent() {
		return this.component;
	}
	
	/**
	 * Associates a component to this tree node.
	 * 
	 * @param component Component to be associated.
	 */
	public void setComponent(Component component) {
		this.component = component;
		this.setUserObject(component.getName());
	}
}
