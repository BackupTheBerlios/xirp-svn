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
 * PropertiesManager2.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 19.03.2007:		Created by Matthias Gernand.
 */
package de.xirp.settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.ini4j.Ini;
import org.ini4j.InvalidIniFormatException;
import org.ini4j.Ini.Section;

import de.xirp.managers.AbstractManager;
import de.xirp.managers.ManagerException;
import de.xirp.util.Constants;
import de.xirp.util.FutureRelease;
import de.xirp.util.I18n;
import de.xirp.util.Variables;
import de.xirp.util.collections.MultiValueHashMap;

/**
 * This manager will replace the existing properties manager
 * in future release. The goal is to make this manager more
 * flexible to changes in the <code>xirp.ini</code> file.
 * <br><br>
 * This is alpha API, may be removed in the future or
 * is planed to be integrated in version 3.0.0.
 * 
 * @author Matthias Gernand
 */
@FutureRelease(version = "3.0.0")
public final class PropertiesManager2 extends AbstractManager {

	/**
	 * The logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(PropertiesManager.class);
	/**
	 * The {@link org.ini4j.Ini} data structure.
	 * 
	 * @see org.ini4j.Ini
	 */
	private static Ini ini;
	/**
	 * A {@link de.xirp.util.collections.MultiValueHashMap}
	 * for a mapping from key name to section.
	 * 
	 * @see de.xirp.util.collections.MultiValueHashMap
	 */
	private static MultiValueHashMap<String, Section> keyToSectionMap;

	/**
	 * Enumeration with constants representing the boolean
	 * options in the ini.
	 * 
	 * @author Matthias Gernand
	 *
	 */
	public static enum BooleanKey {
		needsAuthentication, enable_tts,
	}

	/**
	 * Enumeration with constants representing the number
	 * options in the ini.
	 * 
	 * @author Matthias Gernand
	 *
	 */
	public static enum NumberKey {
		timerWarning, timerFinal, smtpPort, dbPort,
	}

	/**
	 * Enumeration with constants representing the text
	 * options in the ini.
	 * 
	 * @author Matthias Gernand
	 *
	 */
	public static enum TextKey {
		locale, smtpHost, smtpUser, smtpPassword, tabColor, tabTextColor, inactiveTabTextColor, focusColor, sashColor, backgroundColor, panelHeaderColor, dbIP, dbUser, dbPassword, defaultDriver, program_quit, program_preferences, program_help, edit_profiles, open_mail, plugin_info, manage_contacts, search_reports, program_info, libs_info, tts_voice,
	}

	/**
	 * Returns the text of the given text option.
	 * <br><br>
	 * <b>Note:</b> Does nothing at to moment.
	 * 
	 * @param key
	 * 			The option.
	 * 
	 * @return The text value of the option.
	 */
	public static String get(TextKey key) {
		return null;
	}

	/**
	 * Returns the text of the given number option.
	 * <br><br>
	 * <b>Note:</b> Does nothing at to moment.
	 * 
	 * @param key
	 * 			The option.
	 * 
	 * @return The number value of the option.
	 */
	public static Number get(NumberKey key) {
		return null;
	}

	/**
	 * Returns the text of the given boolean option.
	 * <br><br>
	 * <b>Note:</b> Does nothing at to moment.
	 * 
	 * @param key
	 * 			The option.
	 * 
	 * @return The boolean value of the option.
	 */
	public static Boolean get(BooleanKey key) {
		return null;
	}

	/**
	 * Sets the value of the given text key to the given value.
	 * <br><br>
	 * <b>Note:</b> Does nothing at to moment.
	 * 
	 * @param key
	 * 			The key to set.
	 * @param value
	 * 			The value to set.
	 */
	public static void set(TextKey key, String value) {

	}

	/**
	 * Sets the value of the given number key to the given value.
	 * <br><br>
	 * <b>Note:</b> Does nothing at to moment.
	 * 
	 * @param key
	 * 			The key to set.
	 * @param value
	 * 			The value to set.
	 */
	public static void set(NumberKey key, Number value) {

	}

	/**
	 * Sets the value of the given boolean key to the given value.
	 * <br><br>
	 * <b>Note:</b> Does nothing at to moment.
	 * 
	 * @param key
	 * 			The key to set.
	 * @param value
	 * 			The value to set.
	 */
	public static void set(BooleanKey key, Boolean value) {

	}

	/**
	 * Constructs a new manager.
	 * <br><br>
	 * The manager is initialized on startup. Never
	 * call this on your own. Use the statically provided
	 * methods.
	 * 
	 * @throws InstantiationException if an instance already exists.
	 */
	public PropertiesManager2() throws InstantiationException {
		super( );
	}

	/**
	 * Loads the properties from the ini file.
	 * <br><br>
	 * This method is called from the
	 * {@link de.xirp.managers.ManagerFactory} on
	 * startup. Do not call it on your own.
	 * 
	 * @see de.xirp.managers.AbstractManager#start()
	 */
	@Override
	protected void start() throws ManagerException {
		super.start( );
		logClass.info(I18n.getString("PropertiesManager.log.loading") + Constants.APP_NAME + I18n.getString("PropertiesManager.log.properties")); //$NON-NLS-1$ //$NON-NLS-2$

		loadProperties( );
		loadSections( );

		new Variables( );
		// I18n.setLocale(new
		// Locale(PropertiesManager.get(TextKey.locale)));

		logClass.info(I18n.getString("PropertiesManager.log.finished") + Constants.LINE_SEPARATOR); //$NON-NLS-1$
	}

	/**
	 * Loads the sections of the ini.
	 */
	private void loadSections() {

		for (Section s : ini.values( )) {
			if (s.getName( ).equals("General")) {
				;
			}
		}
	}

	/**
	 * Loads the properties and sets the values.
	 */
	private void loadProperties() {
		File iniFile = new File(Constants.INI_FILE);
		FileReader fr;
		try {
			fr = new FileReader(iniFile);
			ini = new Ini(fr);
		}
		catch (FileNotFoundException e) {
			logClass.info(I18n.getString("PropertiesManager.log.failed") + Constants.LINE_SEPARATOR); //$NON-NLS-1$
			logClass.error(e + Constants.LINE_SEPARATOR);
			logClass.error(I18n.getString("PropertiesManager.log.exitedAbnormal") + Constants.LINE_SEPARATOR); //$NON-NLS-1$
			throw new RuntimeException(e);
		}
		catch (InvalidIniFormatException e) {
			logClass.info(I18n.getString("PropertiesManager.log.failed") + Constants.LINE_SEPARATOR); //$NON-NLS-1$
			logClass.error(e + Constants.LINE_SEPARATOR);
			logClass.error(I18n.getString("PropertiesManager.log.exitedAbnormal") + Constants.LINE_SEPARATOR); //$NON-NLS-1$
			throw new RuntimeException(e);
		}
		catch (IOException e) {
			logClass.info(I18n.getString("PropertiesManager.log.failed") + Constants.LINE_SEPARATOR); //$NON-NLS-1$
			logClass.error(e + Constants.LINE_SEPARATOR);
			logClass.error(I18n.getString("PropertiesManager.log.exitedAbnormal") + Constants.LINE_SEPARATOR); //$NON-NLS-1$
			throw new RuntimeException(e);
		}
	}

	/**
	 * Stores the properties to the ini file.
	 * <br><br>
	 * This method is called from the
	 * {@link de.xirp.managers.ManagerFactory} on
	 * shutdown. Do not call it on your own.
	 * 
	 * @see de.xirp.managers.AbstractManager#stop()
	 */
	@Override
	protected void stop() throws ManagerException {
		super.stop( );
		store( );
	}

	/**
	 * Stores the properties ti the ini file.
	 */
	private void store() {
		File iniFile = new File(Constants.INI_FILE);
		FileWriter fw;
		try {
			fw = new FileWriter(iniFile);
			ini.store(fw);
		}
		catch (IOException e) {
			logClass.error(I18n.getString("PropertiesManager.log.error") + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
	}

}
