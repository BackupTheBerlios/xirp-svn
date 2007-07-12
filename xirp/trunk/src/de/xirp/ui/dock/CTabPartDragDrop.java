package de.xirp.ui.dock;

/*********************************************************************
 * Copyright (c) 2003 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available
 * under the terms of the Common Public License v1.0 which accompanies
 * this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ********************************************************************/

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import de.xirp.ui.widgets.custom.XTabFolder;

/**
 * Controls the drag and drop of the part which is contained within<br>
 * the CTabFolder tab.<br>
 */
class CTabPartDragDrop extends PartDragDrop {

	/**
	 * A CTabItem
	 */
	private CTabItem tab;

	/**
	 * Constructs a new tab drag part.<br>
	 * 
	 * @param dragPart
	 * @param tabFolder
	 * @param tabItem
	 */
	public CTabPartDragDrop(ILayoutPart dragPart, CTabFolder tabFolder,
			CTabItem tabItem) {
		super(dragPart, tabFolder);
		this.tab = tabItem;

	}

	/**
	 * Returns the tab folder.<br>
	 * (mg: HTabFolder for xirp2)<br>
	 * 
	 * @return HTabFolder
	 */
	protected XTabFolder getCTabFolder() {
		return (XTabFolder) getDragControl( );
	}

	/**
	 * Returns the source's bounds.<br>
	 * 
	 * @return Rectangle<br>
	 *         The bounds of the source.
	 */
	@Override
	public Rectangle getSourceBounds() {
		return PartTabFolder.calculatePageBounds(getCTabFolder( ));
	}

	/**
	 * Verifies that the tab under the mouse pointer is the same as<br>
	 * for this drag operation.<br>
	 * 
	 * @param position
	 */
	@Override
	public void isDragAllowed(Point position) {
		XTabFolder tabFolder = getCTabFolder( );
		CTabItem tabUnderPointer = tabFolder.getItem(position);
		if (tabUnderPointer != tab){
			return;
		}
		if (tabUnderPointer == null) {
			// Avoid drag from the borders.
			Rectangle clientArea = tabFolder.getClientArea( );
			if ((tabFolder.getStyle( ) & SWT.TOP) != 0) {
				if (position.y > clientArea.y){
					return;
				}
			}
			else {
				if (position.y < clientArea.y + clientArea.height){
					return;
				}
			}
		}

		super.isDragAllowed(position);
	}

	/**
	 * @param newTab
	 */
	public void setTab(CTabItem newTab) {
		tab = newTab;
	}
}
