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
 * ReportManager.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 07.04.2007:		Created by Matthias Gernand.
 */
package de.xirp.report;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.xirp.managers.AbstractManager;
import de.xirp.managers.DeleteManager;
import de.xirp.managers.ManagerException;
import de.xirp.ui.util.SWTUtil;
import de.xirp.util.Constants;

/**
 * This manager provides static method for handling 
 * {@link de.xirp.report.Report reports} and 
 * {@link de.xirp.report.ReportDescriptor report descriptors}.
 * 
 * @author Matthias Gernand
 * 
 * @see de.xirp.report.ReportDescriptor
 * @see de.xirp.report.Report
 */
public final class ReportManager extends AbstractManager {

	/**
	 * The logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(ReportManager.class);

	/**
	 * Constructs a new report manager.
	 * <br><br>
	 * The manager is initialized on startup. Never
	 * call this on your own. Use the statically provided
	 * methods.
	 * 
	 * @throws InstantiationException if an instance already exists.
	 */
	public ReportManager() throws InstantiationException {
		super( );
	}

	/**
	 * This method opens the contained PDF data of the 
	 * given {@link de.xirp.report.ReportDescriptor}
	 * in the default viewer of the underlying operating system.
	 * 
	 * @param rd
	 * 			The report descriptor to open.
	 */
	public static void viewReport(ReportDescriptor rd) {
		File tmp = new File(Constants.TMP_DIR, "report_temp_xirp2.pdf"); //$NON-NLS-1$
		try {
			tmp.createNewFile( );
			FileUtils.writeByteArrayToFile(tmp, rd.getPdfData( ));
			SWTUtil.openFile(tmp);
		}
		catch (IOException e) {
			logClass.error("Error: " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
		}
		if (tmp != null) {
			DeleteManager.deleteOnShutdown(tmp);
		}
	}

	/**
	 * Does nothing,
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
	}

	/**
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
	}
}
