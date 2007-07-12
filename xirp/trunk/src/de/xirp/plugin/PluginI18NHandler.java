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
 * PluginI18NHandler.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 15.03.2006:		Created by Rabea Gransberger.
 */
package de.xirp.plugin;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import de.xirp.util.Constants;
import de.xirp.util.GenericI18n;
import de.xirp.util.I18n;
import de.xirp.util.II18nHandler;

/**
 * PluginI18NHandler makes it easier for plugins to handle their
 * translations by providing the common methods needed for
 * translation.
 * 
 * @author Rabea Gransberger
 */
public final class PluginI18NHandler implements II18nHandler {

	/**
	 * Log4j Logger of this Class
	 */
	private static Logger logClass = Logger.getLogger(PluginI18NHandler.class);

	/**
	 * Plugin to which this Handler belongs to
	 */
	@SuppressWarnings("unchecked")
	private IPlugable plugin;

	/**
	 * The generic i18n object used by this handler
	 */
	private GenericI18n generic;

	/**
	 * Initializes the translation.<br>
	 * The current locale of the application is used for translations
	 * after calling this constructor. Therefor
	 * {@link #setLocale(Locale)} must not be used.
	 * 
	 * @param bundleName
	 *            Name of the bundle where translations are saved
	 * @param plugin
	 *            The plugin for which this handler stands for
	 * @see ResourceBundle
	 */
	@SuppressWarnings("unchecked")
	protected PluginI18NHandler(String bundleName, IPlugable plugin) {
		try {
			// Constructing URLClassLoader to load resource
			// it's the only possibility to load the resource from the
			// jar
			this.plugin = plugin;

			File file = new File(plugin.getInfo( ).getAbsoluteJarPath( ));
			URL[] urls = {file.toURI( ).toURL( )};
			URLClassLoader loader = URLClassLoader.newInstance(urls);

			generic = new GenericI18n(bundleName, loader);

			Locale locale = I18n.getLocale( );
			Locale newLocale = checkLocale(locale);

			generic.setLocale(newLocale);
		}
		catch (Exception e) {
			logClass.warn("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
	}

	/**
	 * Checks if the given locale is available for this plugin<br>
	 * If it's available it's returned. Otherwise the following locale
	 * will be used:<br/>
	 * <ul>
	 * <li>A similar locale like de_DE instead of de_AT</li>
	 * <li>The default locale</li>
	 * <li>{@link java.util.Locale#ENGLISH}</li>
	 * </ul>
	 * 
	 * @param locale
	 *            the locale to check
	 * @return Checked Locale
	 */
	private Locale checkLocale(Locale locale) {
		Locale newLocale = locale;

		try {
			if (plugin != null) {
				if (!plugin.getInfo( ).isLocalAvailable(locale)) {
					Locale nearestLocale = null;

					final List<Locale> candidateLocales = I18n.getNearbyLocales(locale);

					for (Locale candidate : candidateLocales) {
						if (plugin.getInfo( ).isLocalAvailable(candidate)) {
							nearestLocale = candidate;
							break;
						}
					}
					if (nearestLocale == null) {
						newLocale = plugin.getInfo( ).getDefaultLocale( );
					}
					else {
						newLocale = nearestLocale;
					}
				}
			}
		}
		catch (Exception e) {
			newLocale = Locale.ENGLISH;
		}

		return newLocale;
	}

	/**
	 * Sets the locale to use for translation. The locale is only used
	 * if it's listed in the languages available for this plugin.
	 * Otherwise the following locale will be used:<br/>
	 * <ul>
	 * <li>A similar locale like de_DE instead of de_AT</li>
	 * <li>The default locale</li>
	 * <li>{@link java.util.Locale#ENGLISH}</li>
	 * </ul>
	 * 
	 * @param locale
	 *            the locale to set
	 */
	public void setLocale(Locale locale) {
		try {
			Locale checked = checkLocale(locale);
			generic.setLocale(checked);
		}
		catch (Exception e) {
			logClass.warn("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
	}

	/**
	 * Gets the locale used by this handler.<br/> Note that the
	 * locale used by this handler must not be the same as given to
	 * the setter method.
	 * 
	 * @see #setLocale(Locale)
	 * @see de.xirp.util.II18nHandler#getLocale()
	 */
	public Locale getLocale() {
		return generic.getLocale( );
	}

	/**
	 * Translates the given key in the current language and replaces
	 * all variables with the given values.<br/> Convenience method
	 * for<br/><code>
	 * MessageFormat.format(I18n.getString(key),vars);
	 * </code>
	 * 
	 * @param key
	 *            key to get the translation for
	 * @param vars
	 *            values for the variables
	 * @return translated String with given values for variables
	 */
	public String getString(String key, Object... vars) {
		return generic.getString(key, vars);
	}
}
