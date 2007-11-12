package de.xirp.ui.dock;

import de.xirp.ui.dock.PartDragDrop.DragDropLocation;

/*********************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others. All rights
 * reserved. This program and the accompanying materials are made
 * available under the terms of the Common Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ********************************************************************/

/**
 * 
 */
class PartDropEvent {

	/**
	 * 
	 */
	public int x;
	/**
	 * 
	 */
	public int y;
	/**
	 * 
	 */
	public int cursorX;
	/**
	 * 
	 */
	public int cursorY;
	/**
	 * 
	 */
	public DragDropLocation relativePosition;
	/**
	 * 
	 */
	public ILayoutPart dragSource;
	/**
	 * 
	 */
	public ILayoutPart dropTarget;
}
