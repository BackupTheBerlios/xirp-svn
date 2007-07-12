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
 * GenericI18n.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 12.11.2006:		Created by Rabea Gransberger.
 */
package de.xirp.util;

import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Generic class for internationalization purposes.
 * 
 * @see de.xirp.util.ResourceBundleWrapper
 * @author Rabea Gransberger
 */
public class GenericI18n implements II18nHandler {

	/**
	 * The Logger for this class
	 */
	private static final Logger logClass = Logger.getLogger(I18n.class);

	/**
	 * ResourceBundle for the current Locale
	 */
	private ResourceBundleWrapper resourceBundle;

	/**
	 * Mapping from locale to the bundle name
	 */
	private Map<Locale, String> bundleNames;

	/**
	 * The last used locale, in case we don't have a resource bundle
	 * yet
	 */
	private Locale lastLocale;
	/**
	 * The locale used to initialize this object
	 */
	private static final Locale START_LOCALE = Locale.ENGLISH;
	/**
	 * Class loader for loading the resource bundles
	 */
	private ClassLoader loader;
	/**
	 * In case that there are no different bundle names for each
	 * locale, only one bundle name may be used
	 */
	private String bundleName;

	/**
	 * Decimal format symbols for the current locale
	 */
	private DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance( );

	/**
	 * Construct a new object for internationalization purposes.
	 * 
	 * @param bundleNames
	 *            a map from locale to the bundle name
	 * @param loader
	 *            the class loader to load the bundles
	 */
	public GenericI18n(Map<Locale, String> bundleNames, ClassLoader loader) {
		this.bundleNames = bundleNames;
		this.loader = loader;
		setLocale(START_LOCALE);
	}

	/**
	 * Construct a new object for internationalization purposes using
	 * the standard class loader.
	 * 
	 * @param bundleNames
	 *            a map from the locale to the bundle name
	 */
	public GenericI18n(Map<Locale, String> bundleNames) {
		this.bundleNames = bundleNames;
		setLocale(START_LOCALE);
	}

	/**
	 * Construct a new object for internationalization purposes and
	 * uses only one bundle name for each locale loading it with the
	 * given class loader.
	 * 
	 * @param bundleName
	 *            the bundle name (locale independent)
	 * @param loader
	 *            the class loader to load the bundles
	 */
	public GenericI18n(String bundleName, ClassLoader loader) {
		this.bundleName = bundleName;
		this.loader = loader;
		setLocale(START_LOCALE);
	}

	/**
	 * Construct a new object for internationalization purposes using
	 * the standard class loader.
	 * 
	 * @param bundleName
	 *            the bundle name (locale independent)
	 */
	public GenericI18n(String bundleName) {
		this.bundleName = bundleName;
		setLocale(START_LOCALE);
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
	 *         there's no resource bundle set or no translation for
	 *         the given key is found the key surrounded by
	 *         <code>!</code> is returned.
	 */
	public String getString(String key, Object... vars) {
		if (resourceBundle != null) {
			return resourceBundle.getString(key, vars);
		}
		else {
			return "!" + key + "!"; //$NON-NLS-1$ //$NON-NLS-2$
		}

	}

	/**
	 * Sets a new locale to use. Strings will be returned for the new
	 * locale from now on. If no resource bundle for the given
	 * {@link Locale} is found a log message is printed.
	 * 
	 * @param locale
	 *            the new locale
	 */
	public void setLocale(Locale locale) {
		// this locale is already set
		if (lastLocale != null && lastLocale.equals(locale)) {
			return;
		}
		boolean localeSet = false;
		// if there is a different name for each locale
		if (bundleNames != null) {
			// get the bundle name and set the locale
			String localeBundleName = bundleNames.get(locale);
			if (localeBundleName != null) {
				setLocale(locale, localeBundleName);
				localeSet = true;
			}
		}
		// set the locale for the constant bundle name
		else if (bundleName != null) {
			setLocale(locale, bundleName);
			localeSet = true;
		}
		// if locale was not found, warn about this
		if (!localeSet) {
			logClass.warn("Could not find bundle for locale " + locale //$NON-NLS-1$
					+ Constants.LINE_SEPARATOR);
		}
		else {
			symbols = DecimalFormatSymbols.getInstance(lastLocale);
		}
	}

	/**
	 * Refreshes the resource bundle for the given locale and bundle
	 * name.
	 * 
	 * @param locale
	 *            the locale to get a new bundle for
	 * @param bundleName
	 *            the name where the bundle may be found
	 */
	private void setLocale(Locale locale, String bundleName) {
		try {
			if (loader != null) {
				ResourceBundle bundle = ResourceBundle.getBundle(bundleName,
						locale,
						loader);
				resourceBundle = new ResourceBundleWrapper(bundle);
			}
			else {
				ResourceBundle bundle = ResourceBundle.getBundle(bundleName,
						locale);
				resourceBundle = new ResourceBundleWrapper(bundle);
			}
		}
		catch (Exception e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR); //$NON-NLS-1$
		}
		lastLocale = locale;
	}

	/**
	 * Get the current locale.
	 * 
	 * @return the current locale or the last used locale if there was
	 *         nothing found for the last set locale.
	 */
	public Locale getLocale() {
		if (resourceBundle != null) {
			return resourceBundle.getLocale( );
		}
		if (lastLocale == null) {
			lastLocale = Locale.ENGLISH;
		}
		return lastLocale;
	}

	/**
	 * Get all available locals which were given to the constructor
	 * {@link #GenericI18n(Map)} or
	 * {@link #GenericI18n(Map, ClassLoader)}.
	 * 
	 * @return the available locals or an empty set if it is not known
	 *         which locals are available
	 */
	public Set<Locale> getAvailableLocals() {
		if (bundleNames != null) {
			return Collections.unmodifiableSet(bundleNames.keySet( ));
		}
		return Collections.emptySet( );
	}

	/**
	 * The current bundle for this locale.
	 * 
	 * @return the currently used bundle
	 */
	public ResourceBundleWrapper getBundle() {
		return resourceBundle;
	}

	/**
	 * Gets the currency for the currently used locale.
	 * 
	 * @return the currency for the locale in use
	 * @see java.text.DecimalFormatSymbols#getCurrency()
	 */
	public Currency getCurrency() {
		return symbols.getCurrency( );
	}

	/**
	 * Gets the decimal separator for the currently used locale.
	 * 
	 * @return the decimal separator the locale in use
	 * @see java.text.DecimalFormatSymbols#getDecimalSeparator()
	 */
	public char getDecimalSeparator() {
		return symbols.getDecimalSeparator( );
	}

	/**
	 * Gets the string used to represent infinity for the current
	 * locale.
	 * 
	 * @return the string used to represent infinity
	 * @see java.text.DecimalFormatSymbols#getInfinity()
	 */
	public String getInfinity() {
		return symbols.getInfinity( );
	}

	/**
	 * Gets the character used to represent minus sign.
	 * 
	 * @return the character used to represent minus sign.
	 * @see java.text.DecimalFormatSymbols#getMinusSign()
	 */
	public char getMinusSign() {
		return symbols.getMinusSign( );
	}

	/**
	 * Returns the monetary decimal separator.
	 * 
	 * @return the monetary decimal separator.
	 * @see java.text.DecimalFormatSymbols#getMonetaryDecimalSeparator()
	 */
	public char getMonetaryDecimalSeparator() {
		return symbols.getMonetaryDecimalSeparator( );
	}

	/**
	 * Gets the character used for percent sign.
	 * 
	 * @return the character used for percent sign.
	 * @see java.text.DecimalFormatSymbols#getPercent()
	 */
	public char getPercent() {
		return symbols.getPercent( );
	}

	/**
	 * Gets the character used for per mille sign.
	 * 
	 * @return the character used for per mille sign.
	 * @see java.text.DecimalFormatSymbols#getPerMill()
	 */
	public char getPerMill() {
		return symbols.getPerMill( );
	}

}
