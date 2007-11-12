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
 * XRadioButton.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 01.06.200:		Created by Matthias Gernand.
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
public class XRadioButton extends AbstractXButton {

	/**
	 * Constructs a new radio button. This constructor should be used
	 * in application-UI environment.
	 * 
	 * @param parent
	 *            The parent.
	 */
	public XRadioButton(Composite parent) {
		super(parent, SWT.RADIO);
	}

	/**
	 * Constructs a new radio button. This constructor should be used
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
	public XRadioButton(Composite parent, II18nHandler handler) {
		super(parent, SWT.RADIO, handler);
	}
}
