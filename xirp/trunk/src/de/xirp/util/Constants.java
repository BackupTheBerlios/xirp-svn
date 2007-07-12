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
 * Constants.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * 					 Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 13.01.2006:		Created by Matthias Gernand.
 */
package de.xirp.util;

import java.io.File;

import org.eclipse.swt.graphics.RGB;


/**
 * This class defines constants needed in Xirp.
 * 
 * @author Matthias Gernand
 * @author Rabea Gransberger
 * 
 */
public final class Constants {

	
	/** The file separator used by the OS*/
	public static final String FS = File.separator;
	/**	The separator the OS uses for separating two lines */
	public static final String LINE_SEPARATOR = System.getProperty("line.separator"); //$NON-NLS-1$
	/** Path to main execution directory */
	public static final String USER_DIR = System.getProperty("user.dir"); //$NON-NLS-1$
	/** Temporary directory of the OS */
	public static final String TMP_DIR = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
	/** Path to the libs directory */
	@CreateOnStartup
	public static final String LIB_DIR = USER_DIR + FS + "lib"; //$NON-NLS-1$
	/** Path to the dll directory */
	@CreateOnStartup
	public static final String DLL_DIR = LIB_DIR + FS + "dll"; //$NON-NLS-1$
	/** Path to the directory for log files */
	@CreateOnStartup
	public static final String LOG_DIR = USER_DIR + FS + "log"; //$NON-NLS-1$
	/** Path to plugin directory */
	@CreateOnStartup
	public static final String PLUGIN_DIR = USER_DIR + FS + "plugins"; //$NON-NLS-1$
	/** Path to chart image directory */
	@CreateOnStartup
	public static final String CHART_DIR = USER_DIR + FS + "charts"; //$NON-NLS-1$
	/** Path to mail directory */
	@CreateOnStartup
	public static final String MAIL_DIR = USER_DIR + FS + "mail"; //$NON-NLS-1$
	/** Directory for the mail data */
	@CreateOnStartup
	public static final String MAIL_DATA_DIR = MAIL_DIR + FS + "data"; //$NON-NLS-1$
	/** Directory for mail descriptors */
	public static final String MAIL_DESCRIPTORS_FILE = MAIL_DIR + FS + "mail" + Constants.DAT_POSTFIX; //$NON-NLS-1$
	/** Directory for the contacts of the mail */
	public static final String MAIL_CONTACTS_FILE = MAIL_DIR + FS + "contact" + Constants.DAT_POSTFIX; //$NON-NLS-1$
	/** Path to Directory which contains the language jars */
	@CreateOnStartup
	public static final String LANGUAGE_DIR = USER_DIR + FS + "languages"; //$NON-NLS-1$
	/** Path to image directory */
	@CreateOnStartup
	public static final String IMAGE_DIR = USER_DIR + FS + "images"; //$NON-NLS-1$
	/** Path to icon directory */
	@CreateOnStartup
	public static final String ICON_DIR = IMAGE_DIR + FS + "icons"; //$NON-NLS-1$
	/** Path to system image directory */
	@CreateOnStartup
	public static final String SYSTEM_ICON_DIR = ICON_DIR + FS + "system"; //$NON-NLS-1$
	/** Path to configuration files directory */
	@CreateOnStartup
	public static final String CONF_DIR = USER_DIR + FS + "conf"; //$NON-NLS-1$
	/** Path to the ini file containing preferences for the application */
	public static final String INI_FILE = CONF_DIR + FS + "xirp.ini"; //$NON-NLS-1$
	/** Path to the file which contains a list of files which should be deleted on next startup */
	@CreateOnStartup(isDirectory=false)
	public static final String TO_DELETE_FILE = CONF_DIR + FS + "delete.properties"; //$NON-NLS-1$
	/** The path to the profiles configuration directory */
	@CreateOnStartup
	public static final String CONF_PROFILES_DIR = CONF_DIR + FS + "profiles"; //$NON-NLS-1$
	/** The path to the profile XML schema file */
	public static final String CONF_PROFILES_XML_SCHEMA = CONF_PROFILES_DIR + FS + "profile.xsd"; //$NON-NLS-1$
	/** The path to the workspace layout directory */
	@CreateOnStartup
	public static final String CONF_WORKSPACELAYOUT_DIR = CONF_DIR + FS + "workspacelayouts"; //$NON-NLS-1$
	/** The path to the communicationspecs configuration directory */
	@CreateOnStartup
	public static final String CONF_COMMSPECS_DIR = CONF_PROFILES_DIR + FS + "commspecs"; //$NON-NLS-1$
	/** The path to the file with the communicationspecs XML schema */
	public static final String CONF_COMMSPECS_XML_SCHEMA = CONF_COMMSPECS_DIR + FS + "communication.xsd"; //$NON-NLS-1$
	/** The path to the directory in which the robot configurations are saved */
	@CreateOnStartup
	public static final String CONF_ROBOTS_DIR = CONF_PROFILES_DIR + FS + "robots"; //$NON-NLS-1$
	/** The path to the file with the XML schema for robots */
	public static final String CONF_ROBOTS_XML_SCHEMA = CONF_ROBOTS_DIR + FS + "robot.xsd"; //$NON-NLS-1$
	/** Relative path to the file where the bindings to commands */
	public static final String COMMAND_BINDINGS_FILE = CONF_DIR + FS + "command_bindings.properties";//$NON-NLS-1$
	/** Relative path to HTML help files */
	@CreateOnStartup
	public static final String HELP_DIR = USER_DIR + FS + "help"; //$NON-NLS-1$
	/** Base name of the application without version */
	public static final String BASE_NAME = "\u03c7irp";/* Xirp */ //$NON-NLS-1$
	/** Base name of the application containing only ASCII characters*/
	public static final String NON_UNICODE_BASE_NAME = "Xirp";/* Xirp */ //$NON-NLS-1$	
	/** The long name of the application */
	public static final String LONG_NAME = "eXtendable interface for robotic purposes"; //$NON-NLS-1$
	/** Version string for the application */
	public static final String VERSION = Version.MAJOR_VERSION + "." + Version.MINOR_VERSION + "." + Version.PATCH_LEVEL; //$NON-NLS-1$ //$NON-NLS-2$ 
	/** Revision of the application */
	public static final String REVISION = Integer.toString(Version.REVISION);
	/** Application name constructed of BASE_NAME and Version.MAJOR_VERSION */
	public static final String BASE_NAME_MAJOR_VERSION = BASE_NAME + " " + Version.MAJOR_VERSION; //$NON-NLS-1$
	/** Application name constructed of BASE_NAME and VERSION */
	public static final String APP_NAME = BASE_NAME + " " + VERSION + "." + REVISION; //$NON-NLS-1$	//$NON-NLS-2$	
	/** Application name containing only ASCII characters with the version */
	public static final String NON_UNICODE_APP_NAME = NON_UNICODE_BASE_NAME + " " + VERSION + "." + REVISION; //$NON-NLS-1$	//$NON-NLS-2$	
	/** The default color for color settings (white) */
	public static final RGB DEFAULT_COLOR_WHITE = new RGB(255, 255, 255);
	/** The default color for color settings (black) */
	public static final RGB DEFAULT_COLOR_BLACK = new RGB(0, 0, 0);
	/** The report directory */
	@CreateOnStartup
	public static final String REPORT_DIR = USER_DIR + FS + "reports"; //$NON-NLS-1$
	/** The mysql driver to use */
	public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver"; //$NON-NLS-1$
	/** The hsqldb driver */
	public static final String HSQLDB_DRIVER = "org.hsqldb.jdbcDriver"; //$NON-NLS-1$
	/** The name of the database */
	public static final String DATABASE_NAME = "xirp2"; //$NON-NLS-1$
	/** The available database drivers */
	public static final String[][] DB_DRIVERS = new String[][] {
			{MYSQL_DRIVER, HSQLDB_DRIVER}, {"MySQL", "HSQLDB (default)"}}; //$NON-NLS-1$ //$NON-NLS-2$
	/**  */
	public static final String KEY = "&Hj2n?t$"; //$NON-NLS-1$
	/** Postfix for profile files */
	public static final String PROFILE_POSTFIX = ".pro"; //$NON-NLS-1$
	/** Postfix for communication specification files */
	public static final String COMM_SPEC_POSTFIX = ".cms"; //$NON-NLS-1$
	/** Postfix for robot configuration files */
	public static final String ROBOT_POSTFIX = ".bot"; //$NON-NLS-1$
	/** Directory for snapshots */
	public static final String SNAPSHOT_DIR = USER_DIR + FS + "snapshots"; //$NON-NLS-1$
	/** File in which the preferences of the plugins are saved */
	public static final String PLUGIN_PREFS_FILE = CONF_DIR + FS + "plugins.properties"; //$NON-NLS-1$
	/** Directory for a file based database like hsql */
	public static final String DATABASE_DIR = USER_DIR + FS + "database"; //$NON-NLS-1$
	/** The file which locks the application when it's already started */
	public static final String XIRP_LOCK_FILE = Constants.USER_DIR + FS + "xirp.lock"; //$NON-NLS-1$
	/** Postfix for files containing binary data */
	public static final String DAT_POSTFIX = ".dat"; //$NON-NLS-1$
	/** Separator between datapool key and sub-key*/
	public static final String KEY_SUBKEY_SEPARATOR = "_"; //$NON-NLS-1$

	/* Maze and virtual related constants */
	/** postfix for virtual profiles */
	@FutureRelease(version = "3.0.0") //$NON-NLS-1$
	public static final String VIRTUAL_POSTFIX = ".vpro"; //$NON-NLS-1$
	/** Path to the directory for virtual */
	@CreateOnStartup
	@FutureRelease(version = "3.0.0") //$NON-NLS-1$
	public static final String VIRTUAL_DIR = Constants.CONF_DIR + FS + "virtual"; //$NON-NLS-1$
	/** Path to the directory for generated mazecode */
	@CreateOnStartup
	@FutureRelease(version = "3.0.0") //$NON-NLS-1$
	public static final String MAZE_CODE_DIR = Constants.VIRTUAL_DIR + FS + "mazecode"; //$NON-NLS-1$
	/** Path to the directory for files containing a saved maze */
	@CreateOnStartup
	@FutureRelease(version = "3.0.0") //$NON-NLS-1$
	public static final String MAZE_FILES_DIR = Constants.VIRTUAL_DIR + FS + "mazefiles"; //$NON-NLS-1$
	/** postfix for mazes */
	@FutureRelease(version = "3.0.0") //$NON-NLS-1$
	public static final String MAZE_FILE_POSTFIX = ".maze"; //$NON-NLS-1$
}
