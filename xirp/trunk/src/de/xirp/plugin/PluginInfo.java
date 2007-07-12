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
 * PluginInfo.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * 					 Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 14.01.2006:		Created by Matthias Gernand.
 */
package de.xirp.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * Information about a Plugin.<br>
 * This information is read from the <code>plugin.properties</code>
 * file. Important Information are the fully qualified path to the
 * main class and the absolute path to the Jar.
 * 
 * @author Matthias Gernand
 * @author Rabea Gransberger
 */
public final class PluginInfo {

	/**
	 * String if Property wasn't found
	 */
	public static final String UNSPECIFIED = "<unspecified>"; //$NON-NLS-1$

	/**
	 * Absolute path to the Jar file of this plugin
	 */
	private String absoluteJarPath;

	/**
	 * Fully qualified name of the mainClass of this plugin
	 */
	private String mainClass;
	/**
	 * Version of this plugin
	 */
	private String version = ""; //$NON-NLS-1$
	/**
	 * Author of this plugin
	 */
	private String author = ""; //$NON-NLS-1$	
	/**
	 * Flag if there is help available for this plugin
	 */
	private boolean hasHelp = false;
	/**
	 * Locals available for translation
	 */
	private ArrayList<Locale> availableLocals = new ArrayList<Locale>( );
	/**
	 * Default local used for translation
	 */
	private Locale defaultLocale = null;

	/**
	 * The default name of the plugin (without translation)
	 */
	private String defaultName;

	/**
	 * The default description of the plugin (without translation)
	 */
	private String defaultDescription;

	/**
	 * Constructs a new object with information about a plugin
	 * 
	 * @param absoluteJarPath
	 *            absolute Path to the Jar including name of the Jar
	 * @param mainClass
	 *            fully qualified path to the main Class inside the
	 *            jar
	 * @param props
	 *            <code>plugin.properties</code> from the jar which
	 *            contains all information about the plugin
	 */
	protected PluginInfo(String absoluteJarPath, String mainClass,
			Properties props) {
		this.absoluteJarPath = absoluteJarPath;
		this.mainClass = mainClass;
		init(props);
	}

	/**
	 * Reads the properties file, parses the information and makes it
	 * available
	 * 
	 * @param props
	 *            properties from the <code>plugin.properties</code>
	 */
	private void init(Properties props) {
		version = props.getProperty("plugin.version", UNSPECIFIED); //$NON-NLS-1$
		author = props.getProperty("plugin.author", UNSPECIFIED); //$NON-NLS-1$
		defaultName = props.getProperty("plugin.default.name", UNSPECIFIED); //$NON-NLS-1$
		defaultDescription = props.getProperty("plugin.default.description", UNSPECIFIED); //$NON-NLS-1$

		String help = props.getProperty("plugin.core.hasHelp", //$NON-NLS-1$
				UNSPECIFIED);
		hasHelp = help.trim( ).equalsIgnoreCase("true") //$NON-NLS-1$
				| help.trim( ).equalsIgnoreCase("t"); //$NON-NLS-1$

		String localString = props.getProperty("plugin.core.defaultLocal", //$NON-NLS-1$
				UNSPECIFIED);
		defaultLocale = getLocale(localString);
	}

	/**
	 * Gets an Instance of Local constructed from the standardized
	 * LocalString.<br>
	 * [isoLanguage]_[isoCountry]<br>
	 * [isoLanguage] as specified by
	 * http://www.loc.gov/standards/iso639-2/englangn.html 639-1<br>
	 * [isoCountry] as specified by
	 * http://www.iso.ch/iso/en/prods-services/iso3166ma/02iso-3166-code-lists/list-en1.html
	 * 
	 * @param localString
	 *            &lt;isoLanguage&gt;_&lt;isoCountry&gt;
	 * @return the Locale constructed from the String
	 */
	private Locale getLocale(String localString) {
		String[] info = localString.split("_"); //$NON-NLS-1$
		Locale newLocale = new Locale(info[0], info[1]);
		return newLocale;
	}

	/**
	 * Gets the absolute path to the jar file of the plugin
	 * 
	 * @return the absolute path to the jar file of the plugin
	 */
	public String getAbsoluteJarPath() {
		return absoluteJarPath;
	}

	/**
	 * Gets the author(s) of the plugins.
	 * 
	 * @return the authors name(s)
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * Gets the fully specified main class for this plugin which is
	 * located inside the jar of the plugin
	 * 
	 * @return fully specified main class of the plugin
	 */
	public String getMainClass() {
		return mainClass;
	}

	/**
	 * Gets the version of this plugin as string
	 * 
	 * @return the version of this plugin
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * The absolute jar path and the main class
	 */
	@Override
	public String toString() {
		return this.absoluteJarPath + " " + this.mainClass; //$NON-NLS-1$
	}

	/**
	 * Gets all available locals that correspond to the languages for
	 * which translations are available
	 * 
	 * @return available languages/locals
	 */
	public List<Locale> getAvailableLocals() {
		return Collections.unmodifiableList(availableLocals);
	}

	/**
	 * The plugin loader will add all available locales found for this
	 * plugin using this method
	 * 
	 * @param locale
	 *            the locale to add to the list of available locals
	 */
	protected void addAvailableLocale(Locale locale) {
		availableLocals.add(locale);
	}

	/**
	 * Returns the default locale that should be used if the requested
	 * locale isn't available
	 * 
	 * @return the default Locale
	 */
	public Locale getDefaultLocale() {
		return defaultLocale;
	}

	/**
	 * Checks if the locale is available for translation
	 * 
	 * @param locale
	 *            The locale to check for availability
	 * @return <code>true</code> if local is available,
	 *         <code>false</code> otherwise
	 */
	public boolean isLocalAvailable(Locale locale) {
		return availableLocals.contains(locale);
	}

	/**
	 * Checks if the plugin provides help files within the jar in the
	 * help directory.<br>
	 * They can be extracted using the {@link PluginManager}. The
	 * main help file has to be named: <code>help.html</code>
	 * 
	 * @return <code>true</code> if help directory provided in jar
	 * @see PluginManager#extractHelp(PluginInfo, String)
	 */
	public boolean hasHelp() {
		// TODO: remove this from the properties file
		// help may be used if directory and main file is available
		// in the jar
		return hasHelp;
	}

	/**
	 * Gets the default name of the plugin.<br>
	 * For a (possibly) translated name use
	 * {@link IPlugable#getName()}
	 * 
	 * @return the default name of the plugin
	 * @see IPlugable#getName()
	 */
	public String getDefaultName() {
		return defaultName;
	}

	/**
	 * Gets the default description of the plugin.<br>
	 * This description is not translated. For a (possibly) translated
	 * description use {@link IPlugable#getDescription()}
	 * 
	 * @return the default description of the plugin
	 * @see IPlugable#getDescription()
	 */
	public String getDefaultDescription() {
		return defaultDescription;
	}
}
