package de.xirp.ui.dock;

/*********************************************************************
 * Copyright (c) 2003 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available
 * under the terms of the Common Public License v1.0 which accompanies
 * this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html Contributors: IBM
 * Corporation - initial API and implementation
 ********************************************************************/

class ContainerPlaceholder extends PartPlaceholder implements ILayoutContainer {

	/**
	 * 
	 */
	//FIXME static
	private static int nextId = 0;
	/**
	 * 
	 */
	private ILayoutContainer realContainer;

	/**
	 * ContainerPlaceholder constructor comment.<br>
	 * 
	 * @param id
	 *            java.lang.String
	 */
	public ContainerPlaceholder(String id) {
		super(((id == null) ? "Container Placeholder " + nextId++ : id)); //$NON-NLS-1$
	}

	/**
	 * @param child
	 */
	public void add(ILayoutPart child) {
		if (!(child instanceof PartPlaceholder)){
			return;
		}
		realContainer.add(child);
	}

	/**
	 * Returns whether a border should be allowed or not.<br>
	 * 
	 * @return boolean<br>
	 *         <code>true</code>, if border is allowed.<br>
	 *         <code>false</code>, if no border is allowed.
	 * @see ILayoutContainer#allowsBorder
	 */
	public boolean allowsBorder() {
		return true;
	}

	/**
	 * @return ILayoutPart[]<br>
	 */
	public ILayoutPart[] getChildren() {
		return realContainer.getChildren( );
	}

	/**
	 * @return ILayoutPart<br>
	 */
	public ILayoutPart getFocus() {
		return null;
	}

	/**
	 * @return ILayoutPart<br>
	 */
	public ILayoutPart getRealContainer() {
		return (ILayoutPart) realContainer;
	}

	/**
	 * @param value
	 */
	public void setRealContainer(Object value) {
		ILayoutContainer container = (ILayoutContainer) value;
		if (container == null) {
			// set the parent container of the children back to the
			// real
			// container
			if (realContainer != null) {
				ILayoutPart[] children = realContainer.getChildren( );
				if (children != null) {
					for(ILayoutPart child : children){
						child.setContainer(realContainer);
					}
				}
			}
		}
		else {
			// replace the real container with this place holder
			ILayoutPart[] children = container.getChildren( );
			if (children != null) {
				for(ILayoutPart child : children){
					child.setContainer(this);
				}
			}
		}

		this.realContainer = container;
	}

	/**
	 * Returns whether a child is visible or not.<br>
	 * 
	 * @param child
	 *            The child to check.
	 * @return boolean<br>
	 *         <code>true</code>, if child is visible.
	 *         <code>false</code>, if child is not visible.
	 */
	public boolean isChildVisible(ILayoutPart child) {
		// return false;
		return child.isVisible( );
	}

	/**
	 * @param child
	 */
	public void remove(ILayoutPart child) {
		if (!(child instanceof PartPlaceholder)){
			return;
		}
		realContainer.remove(child);
		
	}

	/**
	 * @param oldChild
	 * @param newChild
	 */
	public void replace(ILayoutPart oldChild, ILayoutPart newChild) {
		if (!(oldChild instanceof PartPlaceholder)
				&& !(newChild instanceof PartPlaceholder)){
			return;
		}
		realContainer.replace(oldChild, newChild);
	}

	/**
	 * Sets a childs visibility.<br>
	 * 
	 * @param child
	 *            The child to set.
	 * @param visible
	 *            The visibility.
	 */
	public void setChildVisible(ILayoutPart child, boolean visible) {
		// Before: Nothing. mg
		child.setVisible(visible);
	}

	/**
	 * Sets the focus of a child.<br>
	 * 
	 * @param child
	 *            The child to focus.
	 */
	public void setFocus(ILayoutPart child) {
		child.setFocus( );
	}

}
