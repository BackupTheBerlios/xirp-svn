/** 
 * ============================================================================
 * Xirp 2: eXtendable interface for robotic purposes.
 * ============================================================================
 * 
 * Copyright (C) 2005-2007, by Authors and Contributors listed in CREDITS.txt
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at:
 *
 * 				http://www.opensource.org/licenses/cpl1.0.php
 *
 * ----------------------------
 * CodeLayoutPart.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 03.03.2007:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.panels.virtual;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.xirp.ui.dock.DockingManager;
import de.xirp.ui.widgets.panels.AbstractLayoutPart;
import de.xirp.util.FutureRelease;


/**
 * This is alpha API, may be removed in the future or
 * is planed to be integrated in version 3.0.0.
 * 
 * @author Matthias Gernand
 *
 */
@FutureRelease(version = "3.0.0") //$NON-NLS-1$
public class CodeLayoutPart extends AbstractLayoutPart {

	/**
	 * @param id
	 * @param dm
	 */
	public CodeLayoutPart(String id, DockingManager dm) {
		super(id, dm, null);
	}

	/* (non-Javadoc)
	 * @see de.xirp.ui.widgets.panels.AbstractLayoutPart#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Composite createContent(Composite parent) {
		return new CodeComposite(parent, SWT.NONE);
	}

	/* (non-Javadoc)
	 * @see de.xirp.ui.widgets.panels.AbstractLayoutPart#getNameKey()
	 */
	@Override
	public String getNameKey() {
		return "CodeLayoutPart.gui.name"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see de.xirp.ui.widgets.panels.virtual.AbstractVirtualLayoutPart#getContentClass()
	 */
	@Override
	public Class<? extends Composite> getContentClass() {
		return CodeComposite.class;
	}

	/* (non-Javadoc)
	 * @see de.xirp.ui.widgets.panels.AbstractLayoutPart#canShowInWindow()
	 */
	@Override
	public boolean canShowInWindow() {
		return true;
	}

}
