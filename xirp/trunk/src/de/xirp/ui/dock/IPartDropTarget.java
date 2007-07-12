package de.xirp.ui.dock;

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
 * Specifies the target layout part that accepts the source part
 * during drag and drop.
 * 
 * @see PartDragDrop
 */
interface IPartDropTarget {

	/**
	 * Return the layout part that would accept the drag source part,
	 * or null if not applicable.
	 * 
	 * @return org.eclipse.ui.internal.LayoutPart
	 * @param dragSource
	 *            org.eclipse.ui.internal.LayoutPart
	 */
	ILayoutPart targetPartFor(ILayoutPart dragSource);
}
