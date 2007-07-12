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
 * Launcher.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 09.10.2006:		Created by Rabea Gransberger.
 */
package de.xirp.launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.xml.DOMConfigurator;

import de.xirp.util.Constants;
import de.xirp.util.Version;

/**
 * Updater and Launcher for Xirp.
 * 
 * @author Rabea Gransberger
 * 
 */
public final class Launcher {

	/**
	 * The Logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(Launcher.class);

	/**
	 * Is this a test run on local directories?
	 */
	private static final boolean LOCAL_TEST = true;

	/**
	 * Set the users main dir. In case of a local test run it is the
	 * deploy dir
	 */
	private static String USER_DIR = System.getProperty("user.dir"); //$NON-NLS-1$

	/**
	 * deploy dir for local tests
	 */
	private static final String DEPLOY_DIR = USER_DIR + File.separator
			+ "deploy"; //$NON-NLS-1$

	static {
		if (LOCAL_TEST) {
			USER_DIR = DEPLOY_DIR;
		}
	}

	/**
	 * Directory for the libraries
	 */
	private static final String LIB_DIR = USER_DIR + File.separator + "lib"; //$NON-NLS-1$
	/**
	 * Directory for the configuration
	 */
	private static final String CONF_DIR = USER_DIR + File.separator + "conf"; //$NON-NLS-1$
	/**
	 * Directory for the log files
	 */
	public static final String LOG_DIR = USER_DIR + File.separator + "log"; //$NON-NLS-1$

	/**
	 * The file where we will find information on what to update
	 */
	private static final File UPDATE_FILE = new File(CONF_DIR + File.separator
			+ "update.conf"); //$NON-NLS-1$

	/**
	 * The directory of the jdk
	 */
	private static final String JAVA_EXEC_DIR = System.getProperty("java.home") //$NON-NLS-1$
			+ File.separator + "bin" + File.separator; //$NON-NLS-1$

	/**
	 * The name of the application to launch, will help to find the
	 * jar to launch
	 */
	private static final String APP_NAME = "Xirp"; //$NON-NLS-1$

	/**
	 * Launches the newest Version of the application, afer updating
	 * all files.
	 */
	private static void launch() {
		// Init Logging
		File f = new File(LOG_DIR);
		if (!f.exists( )) {
			f.mkdirs( );
		}
		DOMConfigurator.configure(CONF_DIR + File.separator + "log4j.xml"); //$NON-NLS-1$

		// only show debug messages when development is on
		LoggerRepository repo = LogManager.getLoggerRepository( );
		if (!Version.DEVELOPMENT) {
			repo.setThreshold(Level.INFO);
		}

		deleteOld(true);
		startNew( );
	}

	/**
	 * Deletes or renames all old jars which are not needed any more
	 * after the update process.
	 * 
	 * @param rename
	 *            <code>true</code> if old files should only be
	 *            renamed but not deleted
	 */
	private static void deleteOld(boolean rename) {
		if (UPDATE_FILE.exists( )) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(
						UPDATE_FILE));
				String fileName = null;
				while ((fileName = br.readLine( )) != null) {
					File lib = new File(LIB_DIR + fileName);
					if (lib.exists( )) {
						if (LOCAL_TEST) {
							System.out.println("Delete: " //$NON-NLS-1$
									+ lib.getAbsolutePath( ));
						}
						else {
							if (rename) {
								lib.renameTo(new File(lib.getAbsolutePath( )
										+ "_" + System.currentTimeMillis( ) //$NON-NLS-1$
										+ ".bak")); //$NON-NLS-1$
							}
							else {
								lib.delete( );
							}
						}
					}
				}
			}
			catch (Exception e) {
				logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Starts the newest applications jar
	 */
	private static void startNew() {
		final String fileName = getCoreName( );
		File xirp = new File(fileName);
		if (xirp.exists( )) {
			// build the exec string
			String[] properties = {"-Duser.dir=\"" + USER_DIR + "\""}; //$NON-NLS-1$ //$NON-NLS-2$
			StringBuffer buf = new StringBuffer( );
			buf.append("\"").append(JAVA_EXEC_DIR).append("javaw\" ").append( //$NON-NLS-1$ //$NON-NLS-2$
					getLibraryPath( ));
			for (int i = 0; i < properties.length; i++) {
				buf.append(properties[i]).append(" "); //$NON-NLS-1$
			}

			buf.append("-jar \"").append(xirp.getAbsolutePath( )) //$NON-NLS-1$
					.append("\""); //$NON-NLS-1$

			final String exec = buf.toString( );

			System.out.println(exec);

			// execute the command to launch the application
			try {
				Process proc = Runtime.getRuntime( ).exec(exec);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(proc.getInputStream( )));

				String line = null;
				while ((line = reader.readLine( )) != null) {
					System.out.println(line);
				}
				proc.waitFor( );

			}
			catch (IOException e) {
				logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
			}
			catch (InterruptedException e) {
				logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
			}
			//TODO clean up dlls when exiting
		}
		else {
			logClass.error("There's no xirp.jar existing"+Constants.LINE_SEPARATOR); //$NON-NLS-1$
		}
	}

	/**
	 * Get's the path of the newest xirp.jar
	 * 
	 * @return the absolute path of the newest xirp.jar
	 */
	private static String getCoreName() {
		File dir = new File(USER_DIR);
		File[] cores = dir.listFiles(new FilenameFilter( ) {

			public boolean accept(File dir, String name) {
				return name.startsWith(APP_NAME) && name.endsWith(".jar"); //$NON-NLS-1$
			}
		});

		// find newest xirp.jar
		File newest = null;
		long newestDate = 0;
		if (cores != null) {
			for (File core : cores) {
				if (core.lastModified( ) > newestDate) {
					newestDate = core.lastModified( );
					newest = core;
				}
			}
		}
		// return it's path
		String corePath = ""; //$NON-NLS-1$
		if (newest != null) {
			corePath = newest.getAbsolutePath( );
		}
		return corePath;
	}

	/**
	 * Constructs a library path for the application
	 * 
	 * @return a <code>-Djava.library.path=path</code> expression
	 */
	private static String getLibraryPath() {
		String libraryPath = LIB_DIR + File.separator + "dll";// System.getProperty("java.library.path"); //$NON-NLS-1$

		if (libraryPath == null) {
			libraryPath = ""; //$NON-NLS-1$
		}
		else {
			// remove any quotes from the damn thing
			String temp = ""; //$NON-NLS-1$
			for (int i = 0; i < libraryPath.length( ); i++) {
				char c = libraryPath.charAt(i);
				if (c != '"') {
					temp += c;
				}
			}

			libraryPath = temp;

			// remove trailing separator chars if they exist as they
			// stuff up
			// the following "
			while (libraryPath.endsWith(File.separator)) {
				libraryPath = libraryPath.substring(0,
						libraryPath.length( ) - 1);
			}

			if (libraryPath.length( ) > 0) {
				libraryPath = "-Djava.library.path=\"" + libraryPath + "\" "; //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		return libraryPath;
	}

	public static void main(String[] args) {
		Launcher.launch( );
		
	}
}
