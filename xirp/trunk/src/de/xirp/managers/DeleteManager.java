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
 * DeleteManager.java
 * ----------------------------
 *
 * Original Author:	 Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 20.12.2006:		Created by Rabea Gransberger.
 */
package de.xirp.managers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.xirp.util.Constants;
import de.xirp.util.I18n;

/**
 * Manager which takes care of deleting files which should be only
 * temporary on startup or shutdown. The files to delete may be
 * registered at this manager by using the 4 methods provided.
 * 
 * @author Rabea Gransberger
 */
public final class DeleteManager extends AbstractManager {

	/**
	 * The log4j logger
	 */
	private static final Logger logClass = Logger.getLogger(DeleteManager.class);

	/**
	 * List with absolute paths of the files that should be deleted on
	 * next startup
	 */
	private static final List<String> deleteOnStartup = new ArrayList<String>( );
	/**
	 * List with absolute paths of files that should be deleted on
	 * shutdown
	 */
	private static final List<String> deleteOnShutdown = new ArrayList<String>( );

	/**
	 * Constructs a new DeleteManager. The manager is initialized on
	 * startup. Never call this on your own. Use the statically
	 * provided methods.
	 * 
	 * @throws InstantiationException
	 *             if an instance of this manager does already exist
	 */
	protected DeleteManager() throws InstantiationException {
		super( );
	}

	/**
	 * Starts the manager and deletes all files which were registered
	 * to be deleted on startup at the last run.
	 * 
	 * @see de.xirp.managers.AbstractManager#start()
	 */
	@Override
	protected void start() throws ManagerException {
		super.start( );
		deleteFromPropsFile( );
	}

	/**
	 * Registers the file at the given path to be deleted on next
	 * startup.<br/> This is just a shorthand for<br/> <code>
	 * deleteOnStartup(new File(path));
	 * </code>
	 * 
	 * @param path
	 *            the absolute or user dir relative path of the file
	 *            to delete on next startup
	 */
	public static void deleteOnStartup(final String path) {
		deleteOnStartup(new File(path));
	}

	/**
	 * Registers the file at the given path to be deleted on shutdown.<br/>
	 * This is just a shorthand for<br/> <code>
	 * deleteOnShutdown(new File(path));
	 * </code>
	 * 
	 * @param path
	 *            the absolute or user directory relative path of the
	 *            file to delete on next startup
	 */
	public static void deleteOnShutdown(final String path) {
		deleteOnShutdown(new File(path));
	}

	/**
	 * Registers the given file to be deleted on next startup.
	 * 
	 * @param f
	 *            the file to delete on next startup
	 */
	public static void deleteOnStartup(final File f) {
		deleteOnStartup.add(f.getAbsolutePath( ));

	}

	/**
	 * Registers the given file to be deleted on shutdown.
	 * 
	 * @param f
	 *            the file to delete on shutdown
	 */
	public static void deleteOnShutdown(final File f) {
		deleteOnShutdown.add(f.getAbsolutePath( ));
	}

	/**
	 * Deletes all files listed in the delete properties file and
	 * deletes the properties file itself afterwards.
	 */
	private void deleteFromPropsFile() {
		Properties props = new Properties( );
		File f = new File(Constants.TO_DELETE_FILE);
		try {
			FileReader fr = new FileReader(f);
			props.load(fr);
			fr.close( );
			for (Object obj : props.values( )) {
				String path = (String) obj;
				try {
					FileUtils.forceDelete(new File(path));
				}
				catch (IOException e) {
					logClass.warn(I18n.getString("DeleteManager.notDeleted", path) //$NON-NLS-1$
							+
							Constants.LINE_SEPARATOR);
				}
			}
			f.delete( );
		}
		catch (FileNotFoundException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
		catch (IOException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
	}

	/**
	 * Stops the manager, deletes all files which are registered to be
	 * deleted on shutdown and writes the the file with the list of
	 * files which should be deleted on next startup.<br/><br/>If
	 * the manager fails to delete a file on shutdown, it tries to
	 * delete the file on next startup.
	 * 
	 * @see de.xirp.managers.AbstractManager#stop()
	 */
	@Override
	protected void stop() throws ManagerException {
		super.stop( );
		Properties props = new Properties( );
		int cnt = 0;
		for (String path : deleteOnShutdown) {
			try {
				FileUtils.forceDelete(new File(path));
			}
			catch (IOException e) {
				props.put("File_" + cnt++, path); //$NON-NLS-1$
			}
		}
		File f = new File(Constants.TO_DELETE_FILE);
		if (!f.exists( )) {
			try {
				f.createNewFile( );
			}
			catch (IOException e) {
				// do nothing, cause we can't to anything in this case
			}
		}

		for (String path : deleteOnStartup) {
			props.put("File_" + cnt++, path); //$NON-NLS-1$
		}
		try {
			FileWriter w = new FileWriter(f);
			props.store(w, "Files to delete on startup"); //$NON-NLS-1$
		}
		catch (IOException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
	}
}
