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
 * PrintManager.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 21.02.2007:		Created by Matthias Gernand.
 */
package de.xirp.managers;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.xirp.report.ReportDescriptor;
import de.xirp.util.Constants;
import de.xirp.util.I18n;

/**
 * This manager provides several methods for printing different files.
 * 
 * @author Matthias Gernand
 */
public final class PrintManager extends AbstractManager {

	/**
	 * The logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(PrintManager.class);

	/**
	 * Constructs a new instance of this manager. <br>
	 * <br>
	 * The manager is initialized on startup. Never call this on your
	 * own. Use the statically provided methods.
	 * 
	 * @throws InstantiationException
	 *             if an instance already exists.
	 */
	protected PrintManager() throws InstantiationException {
		super( );
	}

	/**
	 * Prints the {@link java.io.File}'s contained in he given list.
	 * 
	 * @param documents
	 *            The list of files to print.
	 * @throws IOException
	 *             if something went wrong printing.
	 */
	public static void print(final List<File> documents) throws IOException {
		for (File f : documents) {
			print(f);
		}
	}

	/**
	 * Prints the given {@link java.io.File} using the
	 * {@link java.awt.Desktop} class.
	 * 
	 * @param document
	 *            The file to print.
	 * @throws IOException
	 *             if something went wrong printing.
	 * @see java.awt.Desktop
	 */
	public static void print(final File document) throws IOException {
		// TODO: remove awt print stuff, use swt.
		Desktop desktop;
		if (Desktop.isDesktopSupported( )) {
			desktop = Desktop.getDesktop( );
			try {
				desktop.print(document);
			}
			catch (IOException e) {
				throw e;
			}
		}
		else {
			logClass.error(I18n.getString("PrintManager.log.printingNotSupported") + Constants.LINE_SEPARATOR); //$NON-NLS-1$
		}
	}

	/**
	 * Does nothing. <br>
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
	}

	/**
	 * Does nothing. <br>
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
	}

	/**
	 * Prints the corresponding report pdf for the given
	 * {@link de.xirp.report.ReportDescriptor}
	 * 
	 * @param rd
	 *            The descriptor to print the corresponding pdf for.
	 */
	public static void print(final ReportDescriptor rd) {
		File tmp = new File(Constants.TMP_DIR, rd.getFileName( ));
		DeleteManager.deleteOnShutdown(tmp);
		try {
			FileUtils.writeByteArrayToFile(tmp, rd.getPdfData( ));
			print(tmp);
		}
		catch (IOException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
	}
}
