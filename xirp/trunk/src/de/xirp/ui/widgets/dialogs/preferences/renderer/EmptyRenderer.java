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
 * EmptyRenderer.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 29.10.2006:		Created by Rabea Gransberger.
 */
package de.xirp.ui.widgets.dialogs.preferences.renderer;

import java.util.Observable;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;

import de.xirp.settings.Option;
import de.xirp.util.Constants;
import de.xirp.util.I18n;

/**
 * An empty implementation of an option renderer which does absolutely
 * nothing. This is the default renderer for options which have an
 * unknown style ({@link de.xirp.settings.Option.OptionType#UNKNOWN}.
 * 
 * @author Rabea Gransberger
 */
public class EmptyRenderer implements IOptionRenderer {

	/**
	 * The Logger of this class.
	 */
	private static Logger logClass = Logger.getLogger(EmptyRenderer.class);

	/**
	 * Does nothing.
	 * 
	 * @param validator
	 *            (unused)
	 */
	public void addValidator(@SuppressWarnings("unused")
	IValidator validator) {
		// does nothing
	}

	/**
	 * Just prints a log message.
	 * 
	 * @param parent
	 *            (unused)
	 * @param option
	 *            (unused)
	 */
	public void render(@SuppressWarnings("unused")
	Composite parent, @SuppressWarnings("unused")
	Option option) {
		logClass.error(I18n.getString("EmptyRenderer.log.noRenderer") + Constants.LINE_SEPARATOR); //$NON-NLS-1$
	}

	/**
	 * Does nothing.
	 * 
	 * @param observable
	 *            (unused)
	 * @param obj
	 *            (unused)
	 */
	public void update(@SuppressWarnings("unused")
	Observable observable, @SuppressWarnings("unused")
	Object obj) {
		// does nothing
	}

}
