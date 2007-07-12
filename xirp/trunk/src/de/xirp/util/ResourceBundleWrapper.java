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
 * ResourceBundleWrapper.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 11.11.2006:		Created by Rabea Gransberger.
 */
package de.xirp.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Wraps a resource bundle and offers methods to retrieve translated
 * strings with caught exceptions and variable replacement.
 * 
 * @see java.util.ResourceBundle
 * @author Rabea Gransberger
 */
public class ResourceBundleWrapper {

	/**
	 * The Logger for this class
	 */
	private static final Logger logClass = Logger.getLogger(I18n.class);

	/**
	 * The original resource bundle
	 */
	private ResourceBundle resourceBundle;

	/**
	 * Constructs a new wrapper around the given resource bundle.
	 * 
	 * @param bundle
	 *            the bundle
	 */
	public ResourceBundleWrapper(ResourceBundle bundle) {
		this.resourceBundle = bundle;
	}

	/**
	 * Gets the string for the given key in the current {@link Locale}.<br>
	 * If the key is empty or <code>null</code> an empty string is
	 * returned.
	 * 
	 * @param key
	 *            key to get the string for
	 * @return translated string for given key. If no translation for
	 *         the given key is found the key surrounded by
	 *         <code>!</code> is returned.
	 */
	public String getString(String key) {
		if (StringUtils.isEmpty(key)) {
			return ""; //$NON-NLS-1$
		}
		try {
			return resourceBundle.getString(key);
		}
		catch (MissingResourceException e) {
			logClass.trace("Translation error at key '" + key + "', " + e.getMessage( ) + Constants.LINE_SEPARATOR); //$NON-NLS-1$ //$NON-NLS-2$
			return '!' + key + '!';
		}
		catch (NullPointerException e) {
			logClass.trace("Translation error at key '" + key + "'" + Constants.LINE_SEPARATOR); //$NON-NLS-1$ //$NON-NLS-2$
			return '!' + key + '!';
		}
	}

	/**
	 * Gets the string for the given key in the current {@link Locale}
	 * and replaces all variables with the given values.<br/>
	 * Convenience method for<br/> <code>
	 * MessageFormat.format(getString(key),vars);
	 * </code>
	 * 
	 * @param key
	 *            key to get the translation for
	 * @param vars
	 *            values for the variables
	 * @return translated string with given values for variables. If
	 *         no translation for the given key is found the key
	 *         surrounded by <code>!</code> is returned.
	 */
	public String getString(String key, Object... vars) {
		return MessageFormat.format(getString(key), vars);
	}

	/**
	 * Gets the current locale.
	 * 
	 * @return the current locale
	 */
	public Locale getLocale() {
		if (resourceBundle != null) {
			return resourceBundle.getLocale( );
		}
		return null;
	}

	/**
	 * Gets the original resource bundle of this wrapper.
	 * 
	 * @return the resourceBundle
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
}
