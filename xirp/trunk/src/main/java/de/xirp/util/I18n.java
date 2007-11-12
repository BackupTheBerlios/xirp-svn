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
 * I18n.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * 					 Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 15.01.2006:		Created by Rabea Gransberger.
 */
package de.xirp.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.ResourceBundle.Control;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;

/**
 * Class for internationalization of the application. The available
 * locales are determined by looking in the
 * {@link Constants#LANGUAGE_DIR} for jars which contain property
 * files for use in {@link java.util.ResourceBundle}s.
 * 
 * @see de.xirp.util.GenericI18n
 * @see de.xirp.plugin.PluginI18NHandler
 * @author Matthias Gernand
 * @author Rabea Gransberger
 */
public final class I18n {

	/**
	 * The Logger for this class
	 */
	private static final Logger logClass = Logger.getLogger(I18n.class);
	/**
	 * Name of the bundles
	 */
	private static final String BUNDLE_PREFIX = "messages"; //$NON-NLS-1$

	/**
	 * The object for the i18n
	 */
	private static GenericI18n generic;
	/**
	 * The default format for formatting {@link Number}s
	 */
	private static final String DEFAULT_FORMAT = "0.00"; //$NON-NLS-1$
	/**
	 * The formatter used to format numbers
	 */
	private static DecimalFormat format;

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
	public static final String getString(final String key, final Object... vars) {
		return generic.getString(key, vars);
	}

	/**
	 * Sets a new locale to use. Strings will be returned for the new
	 * locale from now on. If no resource bundle for the given
	 * {@link Locale} is found a log message is printed. The decimal
	 * format is also initialized for this new language.
	 * 
	 * @param locale
	 *            the new locale
	 */
	public static final void setLocale(final Locale locale) {
		if (generic == null) {
			init( );
		}
		if (generic != null) {
			generic.setLocale(locale);
		}
		Locale loc = getLocale( );
		if (loc == null) {
			loc = Locale.ENGLISH;
		}
		format = new DecimalFormat(DEFAULT_FORMAT,
				new DecimalFormatSymbols(loc));
		format.setRoundingMode(RoundingMode.HALF_UP);
	}

	/**
	 * Gets the decimal format for the current locale.
	 * 
	 * @return the decimal format for formatting numbers
	 */
	public static DecimalFormat getDefaultDecimalFormat() {
		return format;
	}

	/**
	 * Get the current locale.
	 * 
	 * @return the current locale or the last used locale if there was
	 *         nothing found for the last set locale.
	 */
	public static final Locale getLocale() {
		return generic.getLocale( );
	}

	/**
	 * Initialize the generic i18n
	 */
	public static final void init() {
		File f = new File(Constants.LANGUAGE_DIR);
		// get all jars from the languages folder
		if (f.exists( )) {
			File[] fileList = f.listFiles(new FilenameFilter( ) {

				public boolean accept(@SuppressWarnings("unused")
				File dir, String name) {
					return name.endsWith(".jar"); //$NON-NLS-1$
				}

			});
			if (fileList != null) {
				try {
					ArrayList<URL> urls = new ArrayList<URL>( );
					HashMap<Locale, String> map = new HashMap<Locale, String>(Util.getOptimalMapSize(fileList.length));
					// iterate over the jars and check for language
					// bundle entries
					for (File file : fileList) {
						JarFile jar = new JarFile(file);

						for (Enumeration<JarEntry> en = jar.entries( ); en.hasMoreElements( );) {
							JarEntry entry = en.nextElement( );
							String name = entry.getName( );
							// found a language bundle
							if (name.contains(BUNDLE_PREFIX) &&
									name.endsWith(".properties")) { //$NON-NLS-1$

								// extract the locale
								String locale = name.substring(name.indexOf(BUNDLE_PREFIX));
								locale = locale.replaceAll(BUNDLE_PREFIX + "_",//$NON-NLS-1$
										"");//$NON-NLS-1$
								locale = locale.substring(0,
										locale.indexOf(".properties"));//$NON-NLS-1$
								Locale loc = parseLocaleString(locale);

								// extract the name of the bundle
								// (with package path)
								String bundleName = name.replaceAll("/", ".");//$NON-NLS-1$ //$NON-NLS-2$
								bundleName = bundleName.substring(0,
										bundleName.indexOf(BUNDLE_PREFIX));
								bundleName += BUNDLE_PREFIX;

								// add mapping between locale and
								// bundle name
								map.put(loc, bundleName);
								// add the url of the jar
								urls.add(file.toURI( ).toURL( ));
							}
						}
					}
					// Construct a class loader over all language jars
					URL[] arr = new URL[urls.size( )];
					urls.toArray(arr);
					URLClassLoader loader = URLClassLoader.newInstance(arr);

					generic = new GenericI18n(map, loader);
				}
				catch (IOException e) {
					logClass.error("Error: " + e.getMessage( )//$NON-NLS-1$
							+ Constants.LINE_SEPARATOR, e);
				}
			}
		}
		else {
			logClass.warn("Could not find the directory for language bundles: " //$NON-NLS-1$
					+
					f.getAbsolutePath( ) + Constants.LINE_SEPARATOR);
		}

		if (generic == null) {
			Map<Locale, String> map = null;
			ClassLoader loader = null;
			generic = new GenericI18n(map, loader);
		}
	}

	/**
	 * Get all available locals (if set).
	 * 
	 * @return the available locals (or an empty set if it is not
	 *         known which locals are available)
	 */
	public static final Set<Locale> getAvailableLocals() {
		if (generic == null) {
			init( );
		}
		if (generic != null) {
			return generic.getAvailableLocals( );
		}
		return Collections.emptySet( );
	}

	/**
	 * Gets the {@link GenericI18n} object used for translation.
	 * 
	 * @return the generic i18n object
	 */
	public static final GenericI18n getGenericI18n() {
		if (generic == null) {
			init( );
		}
		return generic;
	}

	/**
	 * Gets the currency for the currently used locale.
	 * 
	 * @return the currency for the locale in use
	 * @see java.text.DecimalFormatSymbols#getCurrency()
	 */
	public static Currency getCurrency() {
		return generic.getCurrency( );
	}

	/**
	 * Gets the decimal separator for the currently used locale.
	 * 
	 * @return the decimal separator the locale in use
	 * @see java.text.DecimalFormatSymbols#getDecimalSeparator()
	 */
	public static char getDecimalSeparator() {
		return generic.getDecimalSeparator( );
	}

	/**
	 * Gets the string used to represent infinity for the current
	 * locale.
	 * 
	 * @return the string used to represent infinity
	 * @see java.text.DecimalFormatSymbols#getInfinity()
	 */
	public static String getInfinity() {
		return generic.getInfinity( );
	}

	/**
	 * Gets the character used to represent minus sign.
	 * 
	 * @return the character used to represent minus sign.
	 * @see java.text.DecimalFormatSymbols#getMinusSign()
	 */
	public static char getMinusSign() {
		return generic.getMinusSign( );
	}

	/**
	 * Returns the monetary decimal separator.
	 * 
	 * @return the monetary decimal separator.
	 * @see java.text.DecimalFormatSymbols#getMonetaryDecimalSeparator()
	 */
	public static char getMonetaryDecimalSeparator() {
		return generic.getMonetaryDecimalSeparator( );
	}

	/**
	 * Gets the character used for percent sign.
	 * 
	 * @return the character used for percent sign.
	 * @see java.text.DecimalFormatSymbols#getPercent()
	 */
	public static char getPercent() {
		return generic.getPercent( );
	}

	/**
	 * Gets the character used for per mille sign.
	 * 
	 * @return the character used for per mille sign.
	 * @see java.text.DecimalFormatSymbols#getPerMill()
	 */
	public static char getPerMill() {
		return generic.getPerMill( );
	}

	/**
	 * Parses the given locale string as it is produces by
	 * {@link Locale#toString()}.
	 * 
	 * @param localeStrg
	 *            the string with locale information to parse
	 * @return the locale parsed from the string
	 */
	public static Locale parseLocaleString(String localeStrg) {
		Locale locale = new Locale(localeStrg);
		final String[] split = localeStrg.split("_"); //$NON-NLS-1$
		if (split.length > 2) {
			locale = new Locale(split[0], split[1], split[2]);
		}
		else if (split.length > 1) {
			locale = new Locale(split[0], split[1]);
		}
		return locale;
	}

	/**
	 * Gets locales which are nearby to the given locale.
	 * 
	 * @param locale
	 *            the locale to get nearby locales for
	 * @return the locales nearby to the given locale
	 * @see ResourceBundle.Control#getCandidateLocales(String, Locale)
	 */
	public static List<Locale> getNearbyLocales(Locale locale) {
		final Control control = ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_PROPERTIES);
		final List<Locale> candidateLocales = control.getCandidateLocales("test", //$NON-NLS-1$
				locale);

		return candidateLocales;
	}

}
