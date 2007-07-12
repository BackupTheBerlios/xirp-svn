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
 * IValidator.java
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

/**
 * Interface which may be implemented for validating changes made to
 * values of settings.
 * 
 * @author Rabea Gransberger
 */
public interface IValidator {

	/**
	 * Checks the given string.
	 * 
	 * @param strg
	 *            the string to check
	 * @return <code>true</code> if successfully validate
	 */
	public boolean validate(String strg);

	/**
	 * Checks the given string and returns a modified string in case
	 * this string was not correct.
	 * 
	 * @param strg
	 *            the string to validate
	 * @return the validate string
	 */
	public String checkString(String strg);

}
