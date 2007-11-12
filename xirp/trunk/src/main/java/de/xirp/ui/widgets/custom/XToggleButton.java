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
 * XToggleButton.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 03.06.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.widgets.custom;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.xirp.util.II18nHandler;

/**
 * This widget is enabled with on-the-fly translation and on-the-fly
 * color change capabilities.
 * 
 * @author Matthias Gernand
 */
public class XToggleButton extends AbstractXButton {

	/**
	 * Constructs a new toggle button. This constructor should be used
	 * in application-UI environment.
	 * 
	 * @param parent
	 *            The parent.
	 */
	public XToggleButton(Composite parent) {
		super(parent, SWT.TOGGLE);
	}

	/**
	 * Constructs a new toggle button. This constructor should be used
	 * in plugin-UI environment, because the
	 * {@link de.xirp.util.II18nHandler handler} is
	 * needed for translations.
	 * 
	 * @param parent
	 *            The parent.
	 * @param handler
	 *            The handler.
	 * @see de.xirp.util.II18nHandler
	 */
	public XToggleButton(Composite parent, II18nHandler handler) {
		super(parent, SWT.TOGGLE, handler);
	}
}
