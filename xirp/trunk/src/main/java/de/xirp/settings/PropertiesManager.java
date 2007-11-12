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
 * PropertiesManager.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 20.02.2006:		Created by Matthias Gernand.
 */
package de.xirp.settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.RGB;
import org.ini4j.Ini;
import org.ini4j.InvalidIniFormatException;
import org.ini4j.Ini.Section;

import com.swtplus.widgets.combo.NamedRGB;

import de.xirp.managers.AbstractManager;
import de.xirp.managers.ManagerException;
import de.xirp.util.Constants;
import de.xirp.util.I18n;
import de.xirp.util.Util;
import de.xirp.util.Variables;

/**
 * This manager reads/writes the options found in
 * <code>xirp.ini</code>. The properties in the
 * <code>xirp.ini</code> are loaded when the manager starts and are
 * written to the file when the manager stops, or when
 * {@link de.xirp.settings.PropertiesManager#store()} is
 * called. <br>
 * <br>
 * Every property in the <code>xirp.ini</code> has a corresponding
 * getter/setter in this manager. The value can be queried using the
 * corresponding static method.
 * 
 * @author Matthias Gernand
 */
public final class PropertiesManager extends AbstractManager {

	/**
	 * The Logger of this class.
	 */
	private static final Logger logClass = Logger.getLogger(PropertiesManager.class);
	/**
	 * An {@link org.ini4j.Ini} object representing the
	 * <code>xirp.ini</code> structure.
	 */
	private static Ini ini;
	/**
	 * The systems language settings.
	 */
	private static Locale locale;
	/**
	 * Object representing the [CTRL] section in <code>xirp.ini</code>.
	 */
	private static Section ctrl;
	/**
	 * Object representing the [CTRL+ALT] section in
	 * <code>xirp.ini</code>.
	 */
	private static Section ctrlAlt;
	/**
	 * Object representing the [CTRL+SHIFT] section in
	 * <code>xirp.ini</code>.
	 */
	private static Section ctrlShift;
	/**
	 * The shortcut value for quitting the program.
	 */
	private static String programQuit;
	/**
	 * The shortcut for opening the mail dialog.
	 */
	private static String openMail;
	/**
	 * The shortcut for opening the contact manager.
	 */
	private static String manageContacts;
	/**
	 * The shortcut for opening the program preferences.
	 */
	private static String programPreferences;
	/**
	 * The shortcut for opening the help.
	 */
	private static String programHelp;
	/**
	 * The shortcut for opening opening the report search.
	 */
	private static String searchReports;
	/**
	 * The shortcut for opening the program info.
	 */
	private static String programInfo;
	/**
	 * The shortcut for opening the plugin info.
	 */
	private static String pluginInfo;
	/**
	 * IP of the database.
	 */
	private static String dbIP;
	/**
	 * Port of the database.
	 */
	private static String dbPort;
	/**
	 * User of the database.
	 */
	private static String dbUser;
	/**
	 * Password of the database.
	 */
	private static String dbPassword;
	/**
	 * Diver to use to connect to a database.
	 */
	private static String defaultDriver;
	/**
	 * Value for timer warning.
	 */
	private static int timerWarning;
	/**
	 * Value for timer finished.
	 */
	private static int timerFinal;
	/**
	 * The tab color.
	 */
	private static String tabColor;
	/**
	 * The inactive tab color.
	 */
	private static String inactiveTabTextColor;
	/**
	 * The focus color.
	 */
	private static String focusColor;
	/**
	 * the {@link de.xirp.ui.widgets.custom.XSash sash}
	 * color.
	 * 
	 * @see de.xirp.ui.widgets.custom.XSash
	 */
	private static String sashColor;
	/**
	 * The background color.
	 */
	private static String backgroundColor;
	/**
	 * The tab text color.
	 */
	private static String tabTextColor;
	/**
	 * The panel header color.
	 */
	private static String panelHeaderColor;
	/**
	 * The SMTP host.
	 */
	private static String smtpHost;
	/**
	 * The SMTP user.
	 */
	private static String smtpUser;
	/**
	 * The SMTP password.
	 */
	private static String smtpPassword;
	/**
	 * Flag if the SMTP server needs authentication.
	 */
	private static boolean needsAuthentication;
	/**
	 * The SMTP port.
	 */
	private static int smtpPort;
	/**
	 * Flag indicating if TTS is activated or not.
	 */
	private static boolean enableTTS;
	/**
	 * The voice to use for TTS.
	 * 
	 * @see de.xirp.speech.TextToSpeechManager
	 */
	private static String ttsVoice;
	/**
	 * The shortcut for opening the chart dialog.
	 */
	private static String createChart;
	/**
	 * Flag indicating if live charts should be exported as PDF after
	 * the plotting.
	 */
	private static boolean exportPDF;
	/**
	 * Flag indicating if live charts should be exported as PNG after
	 * the plotting.
	 */
	private static boolean exportPNG;
	/**
	 * Flag indicating if live charts should be exported as JPG after
	 * the plotting.
	 */
	private static boolean exportJPG;
	/**
	 * Flag indicating if live charts should be exported as CSV after
	 * the plotting.
	 */
	private static boolean exportCSV;
	/**
	 * The view options.
	 */
	private static Map<String, Boolean> viewItems = new HashMap<String, Boolean>( );
	/**
	 * The no-reply mail address for the mailing system.
	 * 
	 * @see de.xirp.mail.MailManager
	 */
	private static String noReplyAddress;

	/**
	 * Constructs a new manager. <br>
	 * <br>
	 * The manager is initialized on startup. Never call this on your
	 * own. Use the statically provided methods.
	 * 
	 * @throws InstantiationException
	 *             if an instance already exists.
	 */
	public PropertiesManager() throws InstantiationException {
		super( );
	}

	/**
	 * Loads the xirp.ini file an constructs the
	 * {@link org.ini4j.Ini ini} object.
	 * 
	 * @see org.ini4j.Ini
	 */
	private void loadProperties() {
		File iniFile = new File(Constants.INI_FILE);
		FileReader fr;
		try {
			fr = new FileReader(iniFile);
			ini = new Ini(fr);
			setProperties( );
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
	 * Sets the values of the fields to the corresponding value in the
	 * xirp.ini
	 */
	private void setProperties() {
		/* [General] */
		Section general = ini.get("General"); //$NON-NLS-1$
		String localeStrg = get(general, "locale", "en"); //$NON-NLS-1$ //$NON-NLS-2$ 
		locale = checkLocale(localeStrg);
		timerWarning = Integer.parseInt(get(general, "timerWarning", "500")); //$NON-NLS-1$ //$NON-NLS-2$ 
		timerFinal = Integer.parseInt(get(general, "timerFinal", "600")); //$NON-NLS-1$ //$NON-NLS-2$ 

		/* [Mail] */
		Section mail = ini.get("Mail"); //$NON-NLS-1$
		smtpHost = mail.get("smtpHost"); //$NON-NLS-1$
		smtpUser = mail.get("smtpUser"); //$NON-NLS-1$
		smtpPassword = mail.get("smtpPassword"); //$NON-NLS-1$
		needsAuthentication = Boolean.parseBoolean(get(mail,
				"needsAuthentication", "false")); //$NON-NLS-1$ //$NON-NLS-2$
		smtpPort = Integer.parseInt(get(mail, "smtpPort", "25")); //$NON-NLS-1$ //$NON-NLS-2$
		noReplyAddress = mail.get("noReplyAddress"); //$NON-NLS-1$

		/* [Appearance] */
		Section appearance = ini.get("Appearance"); //$NON-NLS-1$
		tabColor = get(appearance, "tabColor", "default"); //$NON-NLS-1$ //$NON-NLS-2$
		focusColor = get(appearance, "focusColor", "default"); //$NON-NLS-1$ //$NON-NLS-2$
		sashColor = get(appearance, "sashColor", "default"); //$NON-NLS-1$ //$NON-NLS-2$
		backgroundColor = get(appearance, "backgroundColor", "default"); //$NON-NLS-1$ //$NON-NLS-2$
		tabTextColor = get(appearance, "tabTextColor", "default"); //$NON-NLS-1$ //$NON-NLS-2$
		panelHeaderColor = get(appearance, "panelHeaderColor", "default"); //$NON-NLS-1$ //$NON-NLS-2$
		inactiveTabTextColor = get(appearance,
				"inactiveTabTextColor", "default"); //$NON-NLS-1$ //$NON-NLS-2$

		/* [Database] */
		Section database = ini.get("Database"); //$NON-NLS-1$
		dbIP = database.get("dbIP"); //$NON-NLS-1$
		dbPort = database.get("dbPort"); //$NON-NLS-1$
		dbUser = database.get("dbUser"); //$NON-NLS-1$
		dbPassword = database.get("dbPassword"); //$NON-NLS-1$
		defaultDriver = get(database, "defaultDriver", Constants.HSQLDB_DRIVER); //$NON-NLS-1$

		/* [Hotkeys - CTRL] */
		ctrl = ini.get("Hotkeys - CTRL"); //$NON-NLS-1$
		programQuit = get(ctrl, "program_quit", "Q"); //$NON-NLS-1$ //$NON-NLS-2$ 
		programPreferences = get(ctrl, "program_preferences", "P"); //$NON-NLS-1$ //$NON-NLS-2$ 
		programHelp = get(ctrl, "program_help", "H"); //$NON-NLS-1$ //$NON-NLS-2$ 
		openMail = get(ctrl, "open_mail", "M"); //$NON-NLS-1$ //$NON-NLS-2$ 
		createChart = get(ctrl, "create_chart", "D"); //$NON-NLS-1$ //$NON-NLS-2$ 

		/* [Hotkeys - CTRL+ALT] */
		ctrlAlt = ini.get("Hotkeys - CTRL+ALT"); //$NON-NLS-1$
		pluginInfo = get(ctrlAlt, "plugin_info", "I"); //$NON-NLS-1$ //$NON-NLS-2$
		manageContacts = get(ctrlAlt, "manage_contatcs", "C"); //$NON-NLS-1$ //$NON-NLS-2$ 
		searchReports = get(ctrlAlt, "search_reports", "S"); //$NON-NLS-1$ //$NON-NLS-2$ 

		/* [Hotkeys - CTRL+SHIFT] */
		ctrlShift = ini.get("Hotkeys - CTRL+SHIFT"); //$NON-NLS-1$
		programInfo = get(ctrlShift, "program_info", "I"); //$NON-NLS-1$ //$NON-NLS-2$

		/* [Speech] */
		Section speech = ini.get("Speech"); //$NON-NLS-1$
		enableTTS = Boolean.parseBoolean(get(speech, "enable_tts", "false")); //$NON-NLS-1$ //$NON-NLS-2$
		ttsVoice = get(speech, "tts_voice", "kevin16"); //$NON-NLS-1$ //$NON-NLS-2$

		/* [Chart] */
		Section chart = ini.get("Chart"); //$NON-NLS-1$
		exportPDF = Boolean.parseBoolean(get(chart, "export_pdf", "false")); //$NON-NLS-1$ //$NON-NLS-2$
		exportPNG = Boolean.parseBoolean(get(chart, "export_png", "false")); //$NON-NLS-1$ //$NON-NLS-2$
		exportJPG = Boolean.parseBoolean(get(chart, "export_jpg", "false")); //$NON-NLS-1$ //$NON-NLS-2$
		exportCSV = Boolean.parseBoolean(get(chart, "export_csv", "false")); //$NON-NLS-1$ //$NON-NLS-2$

		/* [View] */
		Section view = ini.get("View"); //$NON-NLS-1$
		if (view != null) {
			viewItems.clear( );
			for (Map.Entry<String, String> entry : view.entrySet( )) {
				boolean val = Boolean.parseBoolean(entry.getValue( ));
				viewItems.put(entry.getKey( ), val);
			}
		}

	}

	/**
	 * Checks if a locale for the given string is available for
	 * translation.
	 * 
	 * @param locale
	 *            the locale string to check
	 * @return the locale to use
	 */
	private Locale checkLocale(String locale) {
		final Locale loc = I18n.parseLocaleString(locale);
		Locale toUse = null;
		final Set<Locale> availableLocals = I18n.getAvailableLocals( );
		if (!availableLocals.contains(loc)) {
			final List<Locale> nearbyLocales = I18n.getNearbyLocales(loc);
			if (!nearbyLocales.isEmpty( )) {
				for (Locale candidate : nearbyLocales) {
					if (availableLocals.contains(candidate)) {
						toUse = candidate;
						break;
					}
				}
			}
		}
		else {
			toUse = loc;
		}
		if (toUse == null) {
			if (!availableLocals.isEmpty( )) {
				toUse = availableLocals.iterator( ).next( );
			}
			else {
				toUse = Locale.ENGLISH;
			}
		}
		return toUse;
	}

	/**
	 * Returns the value of an option in the xirp.ini. The
	 * {@link com.lowagie.text.Section} object and the option name are
	 * needed. A default value can be specified.
	 * 
	 * @param section
	 *            The section object where the variable is located in.
	 * @param variable
	 *            The name of the variable.
	 * @param defaultValue
	 *            The default value of the variable.
	 * @return The value of the variable.
	 */
	private String get(Section section, String variable, String defaultValue) {
		if (Util.isEmpty(section.get(variable))) {
			return defaultValue;
		}
		return section.get(variable);
	}

	/**
	 * Returns the systems locale as string.
	 * 
	 * @return The locale.
	 */
// public static String getLocaleString() {
// return locale;
// }
	/**
	 * Returns the systems locale.
	 * 
	 * @return The locale.
	 */
	public static Locale getLocale() {
		return locale;
	}

	/**
	 * Returns the [CTRL] section.
	 * 
	 * @return The ctrl section.
	 */
	public static Section getCtrl() {
		return ctrl;
	}

	/**
	 * Returns the [CTRL+ALT] section.
	 * 
	 * @return The ctrl+alt section.
	 */
	public static Section getCtrlAlt() {
		return ctrlAlt;
	}

	/**
	 * Returns the [CTRL+SHIFT] section.
	 * 
	 * @return The ctrl+shift section.
	 */
	public static Section getCtrlShift() {
		return ctrlShift;
	}

	/**
	 * Returns the shortcut for opening the report search dialog.
	 * 
	 * @return The shortcut.
	 */
	public static String getSearchReports() {
		return searchReports;
	}

	/**
	 * Returns the shortcut for opening the program help.
	 * 
	 * @return The shortcut.
	 */
	public static String getProgramHelp() {
		return programHelp;
	}

	/**
	 * Returns the shortcut for opening the program info.
	 * 
	 * @return The shortcut.
	 */
	public static String getProgramInfo() {
		return programInfo;
	}

	/**
	 * Returns the shortcut for opening the program preferences.
	 * 
	 * @return The shortcut.
	 */
	public static String getProgramPreferences() {
		return programPreferences;
	}

	/**
	 * Returns the shortcut for closing the program.
	 * 
	 * @return The shortcut.
	 */
	public static String getProgramQuit() {
		return programQuit;
	}

	/**
	 * Returns the database IP.
	 * 
	 * @return The IP.
	 */
	public static String getDbIP() {
		return dbIP;
	}

	/**
	 * Returns the database password.
	 * 
	 * @return The password.
	 */
	public static String getDbPassword() {
		return dbPassword;
	}

	/**
	 * Returns the database port.
	 * 
	 * @return The port.
	 */
	public static String getDbPort() {
		return dbPort;
	}

	/**
	 * Returns the database user.
	 * 
	 * @return The user.
	 */
	public static String getDbUser() {
		return dbUser;
	}

	/**
	 * Returns the shortcut for opening the plugin info.
	 * 
	 * @return The shortcut.
	 */
	public static String getPluginInfo() {
		return pluginInfo;
	}

	/**
	 * Returns the timer waring level.
	 * 
	 * @return The timer waring level.
	 */
	public static int getTimerWarning() {
		return timerWarning;
	}

	/**
	 * Returns the timer final level.
	 * 
	 * @return The timer final level.
	 */
	public static int getTimerFinal() {
		return timerFinal;
	}

	/**
	 * Returns the driver used to connect to a database.
	 * 
	 * @return The default driver.
	 */
	public static String getDefaultDriver() {
		return defaultDriver;
	}

	/**
	 * Returns the {@link com.swtplus.widgets.combo.NamedRGB named}
	 * RGB value of the given color name.
	 * 
	 * @param colorName
	 *            The name of the color.
	 * @return The named RGB for the color.
	 * @see com.swtplus.widgets.combo.NamedRGB
	 */
	private static NamedRGB getDefaultNamedRGB(String colorName) {
		if (colorName.equals("default")) { //$NON-NLS-1$
			return new NamedRGB("default", Constants.DEFAULT_COLOR_WHITE); //$NON-NLS-1$
		}
		else {
			return getNamedRGB(colorName);
		}
	}

	/**
	 * Returns the {@link com.swtplus.widgets.combo.NamedRGB named}
	 * RGB value for the focus color.
	 * 
	 * @return The named RGB for the focus color.
	 * @see com.swtplus.widgets.combo.NamedRGB
	 */
	public static NamedRGB getFocusColorNamedRGB() {
		return getDefaultNamedRGB(focusColor);
	}

	/**
	 * Returns the {@link com.swtplus.widgets.combo.NamedRGB named}
	 * RGB value for the tab color.
	 * 
	 * @return The named RGB for the tab color.
	 * @see com.swtplus.widgets.combo.NamedRGB
	 */
	public static NamedRGB getTabNamedRGB() {
		return getDefaultNamedRGB(tabColor);
	}

	/**
	 * Returns the {@link com.swtplus.widgets.combo.NamedRGB named}
	 * RGB value for the sash color.
	 * 
	 * @return The named RGB for the sash color.
	 * @see com.swtplus.widgets.combo.NamedRGB
	 */
	public static NamedRGB getSashNamedRGB() {
		return getDefaultNamedRGB(sashColor);
	}

	/**
	 * Returns the {@link com.swtplus.widgets.combo.NamedRGB named}
	 * RGB value for the background color.
	 * 
	 * @return The named RGB for the background color.
	 * @see com.swtplus.widgets.combo.NamedRGB
	 */
	public static NamedRGB getBackgroundNamedRGB() {
		return getDefaultNamedRGB(backgroundColor);
	}

	/**
	 * Returns the {@link com.swtplus.widgets.combo.NamedRGB named}
	 * RGB value for the tab text color.
	 * 
	 * @return The named RGB for the tab text color.
	 * @see com.swtplus.widgets.combo.NamedRGB
	 */
	public static NamedRGB getTabTextColorNamedRGB() {
		if (tabTextColor.equals("default")) { //$NON-NLS-1$
			return new NamedRGB("default", new RGB(0, 0, 0)); //$NON-NLS-1$
		}
		else {
			return getNamedRGB(tabTextColor);
		}
	}

	/**
	 * Returns the {@link com.swtplus.widgets.combo.NamedRGB named}
	 * RGB value for the inactive tab text color.
	 * 
	 * @return The named RGB for the inactive tab text color.
	 * @see com.swtplus.widgets.combo.NamedRGB
	 */
	public static NamedRGB getInactiveTabTextColorNamedRGB() {
		if (inactiveTabTextColor.equals("default")) { //$NON-NLS-1$
			return new NamedRGB("default", new RGB(0, 0, 0)); //$NON-NLS-1$
		}
		else {
			return getNamedRGB(inactiveTabTextColor);
		}
	}

	/**
	 * Returns the {@link com.swtplus.widgets.combo.NamedRGB named}
	 * RGB value for the panel header color.
	 * 
	 * @return The named RGB for the panel header color.
	 * @see com.swtplus.widgets.combo.NamedRGB
	 */
	public static NamedRGB getPanelHeaderColorNamedRGB() {
		return getDefaultNamedRGB(panelHeaderColor);
	}

	/**
	 * Returns a {@link com.swtplus.widgets.combo.NamedRGB named} RGB
	 * for the given string. The string has the format: <br>
	 * <br>
	 * <code>Light Yellow {255,255,153}</code>
	 * 
	 * @param from
	 *            The source string.
	 * @return The new named RGB with the name of the color and the
	 *         RGB value.
	 */
	private static NamedRGB getNamedRGB(String from) {
		String name;
		int r;
		int g;
		int b;

		String values[] = from.split("[\\{,\\}]"); //$NON-NLS-1$
		name = values[0].trim( );
		r = Integer.parseInt(values[1]);
		g = Integer.parseInt(values[2]);
		b = Integer.parseInt(values[3]);

		return new NamedRGB(name, new RGB(r, g, b));
	}

	/**
	 * Returns the name of the given
	 * {@link com.swtplus.widgets.combo.NamedRGB named} RGB.
	 * 
	 * @param color
	 *            The color to name.
	 * @return The name of the color.
	 */
	private static String getSetColorName(NamedRGB color) {
		if (color.getName( ).startsWith("default")) { //$NON-NLS-1$
			return "default"; //$NON-NLS-1$
		}
		else {
			return color.toString( );
		}
	}

	/**
	 * Sets the background color.
	 * 
	 * @param backgroundColor
	 *            The background color to set.
	 */
	public static void setBackgroundColor(NamedRGB backgroundColor) {
		PropertiesManager.backgroundColor = getSetColorName(backgroundColor);
	}

	/**
	 * Sets the focus color.
	 * 
	 * @param focusColor
	 *            The focus color to set.
	 */
	public static void setFocusColor(NamedRGB focusColor) {
		PropertiesManager.focusColor = getSetColorName(focusColor);
	}

	/**
	 * Sets the sash color.
	 * 
	 * @param sashColor
	 *            The sash color to set.
	 */
	public static void setSashColor(NamedRGB sashColor) {
		PropertiesManager.sashColor = getSetColorName(sashColor);
	}

	/**
	 * Sets the tab color.
	 * 
	 * @param tabColor
	 *            The tab color to set.
	 */
	public static void setTabColor(NamedRGB tabColor) {
		PropertiesManager.tabColor = getSetColorName(tabColor);
	}

	/**
	 * Sets the tab text color.
	 * 
	 * @param tabTextColor
	 *            The tab text color to set.
	 */
	public static void setTabTextColor(NamedRGB tabTextColor) {
		PropertiesManager.tabTextColor = getSetColorName(tabTextColor);
	}

	/**
	 * Sets the inactive tab text color.
	 * 
	 * @param inactiveTabTextColor
	 *            The inactive tab text color to set.
	 */
	public static void setInactiveTabTextColor(NamedRGB inactiveTabTextColor) {
		PropertiesManager.inactiveTabTextColor = getSetColorName(inactiveTabTextColor);
	}

	/**
	 * Sets the panel header color.
	 * 
	 * @param panelHeaderColor
	 *            The panel header color to set.
	 */
	public static void setPanelHeaderColor(NamedRGB panelHeaderColor) {
		PropertiesManager.panelHeaderColor = getSetColorName(panelHeaderColor);
	}

	/**
	 * Sets the DB IP.
	 * 
	 * @param dbIP
	 *            The database IP to set.
	 */
	public static void setDbIP(String dbIP) {
		PropertiesManager.dbIP = dbIP;
	}

	/**
	 * Sets the DB password.
	 * 
	 * @param dbPassword
	 *            The database password to set.
	 */
	public static void setDbPassword(String dbPassword) {
		PropertiesManager.dbPassword = dbPassword;
	}

	/**
	 * Sets the DB port.
	 * 
	 * @param dbPort
	 *            The database port to set.
	 */
	public static void setDbPort(String dbPort) {
		PropertiesManager.dbPort = dbPort;
	}

	/**
	 * Sets the DB user.
	 * 
	 * @param dbUser
	 *            The database user to set.
	 */
	public static void setDbUser(String dbUser) {
		PropertiesManager.dbUser = dbUser;
	}

	/**
	 * Sets the default driver.
	 * 
	 * @param defaultDriver
	 *            The default database driver name to set.
	 */
	public static void setDefaultDriver(String defaultDriver) {
		PropertiesManager.defaultDriver = defaultDriver;
	}

	/**
	 * Sets the locale.
	 * 
	 * @param locale
	 *            The locale to set.
	 */
	public static void setLocale(String locale) {
		PropertiesManager.locale = I18n.parseLocaleString(locale);
	}

	/**
	 * Sets the plugin info shortcut.
	 * 
	 * @param pluginInfo
	 *            The plugin info shortcut to set.
	 */
	public static void setPluginInfo(String pluginInfo) {
		PropertiesManager.pluginInfo = pluginInfo;
	}

	/**
	 * Sets the program help shortcut.
	 * 
	 * @param programHelp
	 *            The program help shortcut to set.
	 */
	public static void setProgramHelp(String programHelp) {
		PropertiesManager.programHelp = programHelp;
	}

	/**
	 * Sets the program info shortcut.
	 * 
	 * @param programInfo
	 *            The program info shortcut to set.
	 */
	public static void setProgramInfo(String programInfo) {
		PropertiesManager.programInfo = programInfo;
	}

	/**
	 * Sets the program preferences shortcut.
	 * 
	 * @param programPreferences
	 *            The program preferences shortcut to set.
	 */
	public static void setProgramPreferences(String programPreferences) {
		PropertiesManager.programPreferences = programPreferences;
	}

	/**
	 * Sets the search reports shortcut.
	 * 
	 * @param searchReports
	 *            The search reports shortcut to set.
	 */
	public static void setSearchReports(String searchReports) {
		PropertiesManager.searchReports = searchReports;
	}

	/**
	 * Sets the program quit shortcut.
	 * 
	 * @param programQuit
	 *            The program quit shortcut to set.
	 */
	public static void setProgramQuit(String programQuit) {
		PropertiesManager.programQuit = programQuit;
	}

	/**
	 * Sets the timer final value.
	 * 
	 * @param timerFinal
	 *            The timer final value to set.
	 */
	public static void setTimerFinal(int timerFinal) {
		PropertiesManager.timerFinal = timerFinal;
	}

	/**
	 * Sets the timer warning value.
	 * 
	 * @param timerWarning
	 *            The timer warning value to set.
	 */
	public static void setTimerWarning(int timerWarning) {
		PropertiesManager.timerWarning = timerWarning;
	}

	/**
	 * Stores the values to the xirp.ini file.
	 */
	public static void store() {
		/* [General] */
		Section general = ini.get("General"); //$NON-NLS-1$
		general.put("locale", locale.toString( )); //$NON-NLS-1$ 
		general.put("timerWarning", Integer.toString(timerWarning)); //$NON-NLS-1$ 
		general.put("timerFinal", Integer.toString(timerFinal)); //$NON-NLS-1$ 

		/* [Mail] */
		Section mail = ini.get("Mail"); //$NON-NLS-1$
		mail.put("smtpHost", smtpHost); //$NON-NLS-1$
		mail.put("smtpUser", smtpUser); //$NON-NLS-1$
		mail.put("smtpPassword", smtpPassword); //$NON-NLS-1$
		mail.put("needsAuthentication", Boolean.toString(needsAuthentication)); //$NON-NLS-1$
		mail.put("smtpPort", Integer.toString(smtpPort)); //$NON-NLS-1$
		mail.put("noReplyAddress", noReplyAddress); //$NON-NLS-1$

		/* [Appearance] */
		Section appearance = ini.get("Appearance"); //$NON-NLS-1$
		appearance.put("tabColor", tabColor); //$NON-NLS-1$ 
		appearance.put("focusColor", focusColor); //$NON-NLS-1$ 
		appearance.put("sashColor", sashColor); //$NON-NLS-1$ 
		appearance.put("backgroundColor", backgroundColor); //$NON-NLS-1$ 
		appearance.put("tabTextColor", tabTextColor); //$NON-NLS-1$ 
		appearance.put("panelHeaderColor", panelHeaderColor); //$NON-NLS-1$ 
		appearance.put("inactiveTabTextColor", inactiveTabTextColor); //$NON-NLS-1$ 

		/* [Database] */
		Section database = ini.get("Database"); //$NON-NLS-1$
		database.put("dbIP", dbIP); //$NON-NLS-1$ 
		database.put("dbPort", dbPort); //$NON-NLS-1$ 
		database.put("dbUser", dbUser); //$NON-NLS-1$ 
		database.put("dbPassword", dbPassword); //$NON-NLS-1$ 
		database.put("defaultDriver", defaultDriver); //$NON-NLS-1$ 

		/* [Hotkeys - CTRL] */
		ctrl = ini.get("Hotkeys - CTRL"); //$NON-NLS-1$
		ctrl.put("program_quit", programQuit); //$NON-NLS-1$ 
		ctrl.put("program_preferences", programPreferences); //$NON-NLS-1$ 
		ctrl.put("program_help", programHelp); //$NON-NLS-1$ 
		ctrl.put("open_mail", openMail); //$NON-NLS-1$ 
		ctrl.put("create_chart", createChart); //$NON-NLS-1$ 

		/* [Hotkeys - CTRL+ALT] */
		ctrlAlt = ini.get("Hotkeys - CTRL+ALT"); //$NON-NLS-1$
		ctrlAlt.put("plugin_info", pluginInfo); //$NON-NLS-1$ 
		ctrlAlt.put("search_reports", searchReports); //$NON-NLS-1$ 
		ctrlAlt.put("manage_contacts", manageContacts); //$NON-NLS-1$ 

		/* [Hotkeys - CTRL+SHIFT] */
		ctrlShift = ini.get("Hotkeys - CTRL+SHIFT"); //$NON-NLS-1$
		ctrlShift.put("program_info", programInfo); //$NON-NLS-1$ 

		/* [Speech] */
		Section speech = ini.get("Speech"); //$NON-NLS-1$
		speech.put("enable_tts", Boolean.toString(enableTTS)); //$NON-NLS-1$
		speech.put("tts_voice", ttsVoice); //$NON-NLS-1$

		/* [Chart] */
		Section chart = ini.get("Chart"); //$NON-NLS-1$
		chart.put("export_pdf", Boolean.toString(exportPDF)); //$NON-NLS-1$
		chart.put("export_png", Boolean.toString(exportPNG)); //$NON-NLS-1$
		chart.put("export_jpg", Boolean.toString(exportJPG)); //$NON-NLS-1$
		chart.put("export_csv", Boolean.toString(exportCSV)); //$NON-NLS-1$

		/* [View] */
		Section view = ini.get("View"); //$NON-NLS-1$
		if (view == null) {
			view = ini.add("View");
		}
		for (Map.Entry<String, Boolean> entry : viewItems.entrySet( )) {
			view.put(entry.getKey( ), entry.getValue( ).toString( ));
		}

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

	/**
	 * The options are read from the <code>xirp.ini</code> file and
	 * loaded to the fields of this manager. <br>
	 * <br>
	 * This method is called from the
	 * {@link de.xirp.managers.ManagerFactory} on
	 * startup. Do not call it on your own.
	 * 
	 * @see de.xirp.managers.AbstractManager#start()
	 */
	@Override
	protected void start() throws ManagerException {
		super.start( );
		logClass.info(I18n.getString("PropertiesManager.log.loading") + Constants.NON_UNICODE_APP_NAME + I18n.getString("PropertiesManager.log.properties")); //$NON-NLS-1$ //$NON-NLS-2$

		loadProperties( );

		Variables.reloadVariables( );

		I18n.setLocale(PropertiesManager.getLocale( ));

		logClass.info(I18n.getString("PropertiesManager.log.finished") + Constants.LINE_SEPARATOR); //$NON-NLS-1$
	}

	/**
	 * Writes the possibly changed values to the <code>xirp.ini</code>
	 * file. <br>
	 * <br>
	 * This method is called from the
	 * {@link de.xirp.managers.ManagerFactory} on
	 * shutdown. Do not call it on your own.
	 * 
	 * @see de.xirp.managers.AbstractManager#stop()
	 */
	@Override
	protected void stop() throws ManagerException {
		super.stop( );
		PropertiesManager.store( );
	}

	/**
	 * Returns the SMTP host.
	 * 
	 * @return The SMTP host.
	 */
	public static String getSmtpHost() {
		return smtpHost;
	}

	/**
	 * Returns the SMTP password.
	 * 
	 * @return The SMTP password.
	 */
	public static String getSmtpPassword() {
		return smtpPassword;
	}

	/**
	 * Returns the create chart shortcut.
	 * 
	 * @return The create chart shortcut.
	 */
	public static String getCreateChart() {
		return createChart;
	}

	/**
	 * Sets the create chart shortcut.
	 * 
	 * @param createChart
	 *            The create chart shortcut to set.
	 */
	public static void setCreateChart(String createChart) {
		PropertiesManager.createChart = createChart;
	}

	/**
	 * Returns the SMTP user.
	 * 
	 * @return The SMTP user.
	 */
	public static String getSmtpUser() {
		return smtpUser;
	}

	/**
	 * Sets the SMTP host.
	 * 
	 * @param smtpHost
	 *            The SMTP host to set.
	 */
	public static void setSmtpHost(String smtpHost) {
		PropertiesManager.smtpHost = smtpHost;
	}

	/**
	 * Sets the SMTP password.
	 * 
	 * @param smtpPassword
	 *            The SMTP password to set.
	 */
	public static void setSmtpPassword(String smtpPassword) {
		PropertiesManager.smtpPassword = smtpPassword;
	}

	/**
	 * Sets the SMTP user.
	 * 
	 * @param smtpUser
	 *            The SMTP user to set.
	 */
	public static void setSmtpUser(String smtpUser) {
		PropertiesManager.smtpUser = smtpUser;
	}

	/**
	 * Returns the flag, indicating if the SMTP server needs
	 * authentication.
	 * 
	 * @return A <code>boolean</code><br>
	 *         <code>true</code>: the server needs authentication.<br>
	 *         <code>false</code>: the server does not need
	 *         authentication.<br>
	 */
	public static boolean isNeedsAuthentication() {
		return needsAuthentication;
	}

	/**
	 * Sets the flag, indicating if the SMTP server needs
	 * authentication.
	 * 
	 * @param needsAuthentication
	 *            <code>true</code>: the server needs
	 *            authentication.<br>
	 *            <code>false</code>: the server does not need
	 *            authentication.<br>
	 */
	public static void setNeedsAuthentication(boolean needsAuthentication) {
		PropertiesManager.needsAuthentication = needsAuthentication;
	}

	/**
	 * Returns the SMTP port.
	 * 
	 * @return The SMTP port.
	 */
	public static int getSmtpPort() {
		return smtpPort;
	}

	/**
	 * Sets the SMTP port.
	 * 
	 * @param smtpPort
	 *            The SMTP port to set.
	 */
	public static void setSmtpPort(int smtpPort) {
		PropertiesManager.smtpPort = smtpPort;
	}

	/**
	 * Returns the contact manager shortcut.
	 * 
	 * @return The contact manager shortcut.
	 */
	public static String getManageContacts() {
		return manageContacts;
	}

	/**
	 * Sets the contact manager shortcut.
	 * 
	 * @param manageContacts
	 *            The contact manager shortcut to set.
	 */
	public static void setManageContacts(String manageContacts) {
		PropertiesManager.manageContacts = manageContacts;
	}

	/**
	 * Returns the open mail shortcut.
	 * 
	 * @return The open mail shortcut.
	 */
	public static String getOpenMail() {
		return openMail;
	}

	/**
	 * Sets the open mail shortcut.
	 * 
	 * @param openMail
	 *            The open mail shortcut to set.
	 */
	public static void setOpenMail(String openMail) {
		PropertiesManager.openMail = openMail;
	}

	/**
	 * Returns the flag, indicating if the text to speech feature
	 * should be active or not.
	 * 
	 * @return A <code>boolean</code><br>
	 *         <code>true</code>: the TTS feature is active.<br>
	 *         <code>false</code>: the TTS feature is not active.<br>
	 */
	public static boolean isEnableTTS() {
		return enableTTS;
	}

	/**
	 * Sets the flag, indicating if the text to speech feature should
	 * be active or not.
	 * 
	 * @param enableTTS
	 *            <code>true</code>: the TTS feature is active.<br>
	 *            <code>false</code>: the TTS feature is not
	 *            active.<br>
	 */
	public static void setEnableTTS(boolean enableTTS) {
		PropertiesManager.enableTTS = enableTTS;
	}

	/**
	 * Returns the voice name used for the text to speech feature.
	 * 
	 * @return The name of the voice.
	 */
	public static String getTTSVoice() {
		return ttsVoice;
	}

	/**
	 * Sets the voice name used for the text to speech feature.
	 * 
	 * @param voice
	 *            The voice name to set.
	 */
	public static void setTTSVoice(String voice) {
		ttsVoice = voice;
	}

	/**
	 * Returns the flag, indicating if a CSV file should be generated
	 * automatically after the plotting in the live chart feature.
	 * 
	 * @return A <code>boolean</code><br>
	 *         <code>true</code>: file is exported.<br>
	 *         <code>false</code>: file is not exported.<br>
	 */
	public static boolean isExportCSV() {
		return exportCSV;
	}

	/**
	 * Returns the flag, indicating if a JPG file should be generated
	 * automatically after the plotting in the live chart feature.
	 * 
	 * @return A <code>boolean</code><br>
	 *         <code>true</code>: file is exported.<br>
	 *         <code>false</code>: file is not exported.<br>
	 */
	public static boolean isExportJPG() {
		return exportJPG;
	}

	/**
	 * Returns the flag, indicating if a PDF file should be generated
	 * automatically after the plotting in the live chart feature.
	 * 
	 * @return A <code>boolean</code><br>
	 *         <code>true</code>: file is exported.<br>
	 *         <code>false</code>: file is not exported.<br>
	 */
	public static boolean isExportPDF() {
		return exportPDF;
	}

	/**
	 * Returns the flag, indicating if a PNG file should be generated
	 * automatically after the plotting in the live chart feature.
	 * 
	 * @return A <code>boolean</code><br>
	 *         <code>true</code>: file is exported.<br>
	 *         <code>false</code>: file is not exported.<br>
	 */
	public static boolean isExportPNG() {
		return exportPNG;
	}

	/**
	 * Returns the flag, indicating if a CSV file should be generated
	 * automatically after the plotting in the live chart feature.
	 * 
	 * @param exportCSV
	 *            <code>true</code>: file is exported.<br>
	 *            <code>false</code>: file is not exported.<br>
	 */
	public static void setExportCSV(boolean exportCSV) {
		PropertiesManager.exportCSV = exportCSV;
	}

	/**
	 * Returns the flag, indicating if a JPG file should be generated
	 * automatically after the plotting in the live chart feature.
	 * 
	 * @param exportJPG
	 *            <code>true</code>: file is exported.<br>
	 *            <code>false</code>: file is not exported.<br>
	 */
	public static void setExportJPG(boolean exportJPG) {
		PropertiesManager.exportJPG = exportJPG;
	}

	/**
	 * Returns the flag, indicating if a PDF file should be generated
	 * automatically after the plotting in the live chart feature.
	 * 
	 * @param exportPDF
	 *            <code>true</code>: file is exported.<br>
	 *            <code>false</code>: file is not exported.<br>
	 */
	public static void setExportPDF(boolean exportPDF) {
		PropertiesManager.exportPDF = exportPDF;
	}

	/**
	 * Returns the flag, indicating if a PNG file should be generated
	 * automatically after the plotting in the live chart feature.
	 * 
	 * @param exportPNG
	 *            <code>true</code>: file is exported.<br>
	 *            <code>false</code>: file is not exported.<br>
	 */
	public static void setExportPNG(boolean exportPNG) {
		PropertiesManager.exportPNG = exportPNG;
	}

	/**
	 * Returns the no-reply mail address used in the mail feature.
	 * 
	 * @return The no-reply address.
	 */
	public static String getNoReplyAddress() {
		return noReplyAddress;
	}

	/**
	 * Sets the the no-reply mail address used in the mail feature.
	 * 
	 * @param noReplyAddress
	 *            The no-reply address to set.
	 */
	public static void setNoReplyAddress(String noReplyAddress) {
		PropertiesManager.noReplyAddress = noReplyAddress;
	}

	/**
	 * Sets the view flag for the given key. The view flags are
	 * responsible for the appearance of the <code>View</code> menu.
	 * 
	 * @param key
	 *            The key to set the flag for.
	 * @param flag
	 *            The flag to set.<br>
	 *            <code>true</code>: corresponding menu entry is
	 *            checked.<br>
	 *            <code>false</code>: corresponding menu entry is
	 *            not checked.<br>
	 */
	public static void setViewItem(String key, boolean flag) {
		viewItems.put(key, flag);
	}

	/**
	 * Returns the view flag for the given key. The view flags are
	 * responsible for the appearance of the <code>View</code> menu.
	 * 
	 * @param key
	 *            The key to return the flag for.
	 * @return A <code>boolean</code><br>
	 *         <code>true</code>: corresponding menu entry is
	 *         checked.<br>
	 *         <code>false</code>: corresponding menu entry is not
	 *         checked.<br>
	 */
	public static boolean isViewItemVisible(String key) {
		Boolean b = viewItems.get(key);
		if (b != null) {
			return b.booleanValue( );
		}
		return false;
	}
}
