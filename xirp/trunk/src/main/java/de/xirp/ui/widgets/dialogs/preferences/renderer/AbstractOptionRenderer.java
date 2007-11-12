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
 * AbstractOptionRenderer.java
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
 * An abstract renderer for options with basic support for adding a
 * validator.
 * 
 * @author Rabea Gransberger
 */
public abstract class AbstractOptionRenderer implements IOptionRenderer {

	/**
	 * The validator for validating modified settings
	 */
	protected IValidator validator;

	/**
	 * Adds a validator which is used when calling
	 * {@link #checkString(String)} or {@link #validate(String)}.
	 * 
	 * @see de.xirp.ui.widgets.dialogs.preferences.renderer.IOptionRenderer#addValidator(de.xirp.ui.widgets.dialogs.preferences.renderer.IValidator)
	 */
	public void addValidator(IValidator validator) {
		this.validator = validator;
	}

	/**
	 * Validates the given string.
	 * 
	 * @param strg
	 *            the string to validate.
	 * @return <code>true</code> if the given string was valid or no
	 *         validator was set.
	 * @see de.xirp.ui.widgets.dialogs.preferences.renderer.IValidator#validate(java.lang.String)
	 */
	public boolean validate(String strg) {
		if (validator != null) {
			return validator.validate(strg);
		}
		else {
			return true;
		}
	}

	/**
	 * Checks the given string and returns a corrected string.
	 * 
	 * @param strg
	 *            the string to check
	 * @return the corrected string if the string was not valid or the
	 *         given string if the string was valid or no validator
	 *         was set.
	 * @see de.xirp.ui.widgets.dialogs.preferences.renderer.IValidator#checkString(java.lang.String)
	 */
	public String checkString(String strg) {
		if (validator != null) {
			return validator.checkString(strg);
		}
		else {
			return strg;
		}
	}
}