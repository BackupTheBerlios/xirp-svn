package de.xirp.ui.dock;

/*********************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others. All rights
 * reserved. This program and the accompanying materials are made
 * available under the terms of the Common Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html Contributors: IBM
 * Corporation - initial API and implementation
 ********************************************************************/

public interface ILayoutContainer {

	/**
	 * Add a child to the container.<br>
	 * 
	 * @param newPart
	 */
	public void add(ILayoutPart newPart);

	/**
	 * Return true if the container allows its parts to show a border<br>
	 * if they choose to, else false if the container does not want<br>
	 * its parts to show a border.<br>
	 * 
	 * @return boolean
	 */
	boolean allowsBorder();

	/**
	 * Returns a list of layout children.<br>
	 * 
	 * @return ILayoutPart[]
	 */
	public ILayoutPart[] getChildren();

	/**
	 * Remove a child from the container.<br>
	 * 
	 * @param part
	 */
	public void remove(ILayoutPart part);

	/**
	 * Replace one child with another.<br>
	 * 
	 * @param oldPart
	 * @param newPart
	 */
	public void replace(ILayoutPart oldPart, ILayoutPart newPart);

	/**
	 * Gets the parent for this container.<br>
	 * 
	 * @return ILayoutContainer
	 */
	public ILayoutContainer getContainer();

	/**
	 * Gets root container for this part.<br>
	 * 
	 * @return RootLayoutContainer
	 */
	public RootLayoutContainer getRootContainer();
}
