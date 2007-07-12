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
 * II18nHandler.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 15.11.2006:		Created by Rabea Gransberger.
 */
package de.xirp.util;

import java.util.Locale;

/**
 * Interface for handling internationalization.
 * 
 * @author Rabea Gransberger
 */
public interface II18nHandler {

	/**
	 * Gets the translation for the given key using the given
	 * variables.<br>
	 * Example:<br>
	 * The properties file contains the following entry:<br>
	 * <code>key.for.teststring=Hello I'm {0} and I'm {1} years old</code>
	 * <code>getString("key.for.teststring","Rabea",23)</code>
	 * will result in the String
	 * <code>Hello I'm Rabea and I'm 23 years old </code><br>
	 * 
	 * @param key
	 *            the key for which the translation may be found
	 * @param vars
	 *            used for replacing variables in the translation
	 * @return the translation for the key or the key between
	 *         <code>!</code>
	 */
	public String getString(String key, Object... vars);

	/**
	 * Sets the locale to use for translations in this handler.
	 * 
	 * @param locale
	 *            the locale to set
	 */
	public void setLocale(Locale locale);

	/**
	 * Gets the locale which is used for translations in this handler.
	 * 
	 * @return the locale used for translations
	 */
	public Locale getLocale();
}
