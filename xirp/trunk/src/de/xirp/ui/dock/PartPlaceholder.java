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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * A PlaceHolder is a non-visible stand-in for a IVisualPart.<br>
 */
class PartPlaceholder extends LayoutPart {

	/**
	 * @param id
	 */
	public PartPlaceholder(String id) {
		super(id);
	}

	/**
	 * Creates the SWT control.<br>
	 * 
	 * @param parent
	 */
	@Override
	public void createControl(Composite parent) {
	}

	/**
	 * @see LayoutPart#isViewPane()
	 */
	@Override
	public boolean isViewPane() {
		return false;
	}

	/**
	 * Get the part control. This method may return null.<br>
	 * 
	 * @return Control
	 */
	@Override
	public Control getControl() {
		return null;
	}

	/**
	 * @return boolean
	 * 
	 * @see ILayoutPart#isZoomed()
	 */
	@Override
	public boolean isZoomed() {
		return isZoomed;
	}
}
