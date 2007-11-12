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
 * CursorManager.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 18.09.2006:		Created by Rabea Gransberger.
 */
package de.xirp.ui.util.ressource;

import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;

import de.xirp.managers.AbstractManager;
import de.xirp.managers.ManagerException;

/**
 * Manager for cursors. This managers allows to receive cursors for
 * SWT, caches these cursors internally and disposes them on shutdown
 * to prevent memory leaks and having more than one instance per
 * cursors.
 * 
 * @author Rabea Gransberger
 */
public final class CursorManager extends AbstractManager {

	/**
	 * The Logger for this class.
	 */
	// private static Logger logClass =
	// Logger.getLogger(CursorManager.class);
	/**
	 * The display which is used for color creation.
	 */
	private static Display display = Display.getDefault( );

	/**
	 * Creates a new manager for cursors. The manager is initialized
	 * on startup. Never call this on your own. Use the statically
	 * provided methods.
	 * 
	 * @throws InstantiationException
	 *             if an instance of this manager does already exist
	 */
	public CursorManager() throws InstantiationException {
		super( );
	}

	/**
	 * Returns the matching standard platform cursor for the given
	 * constant, which should be one of the cursor constants specified
	 * in class SWT. This cursor should not be free'd because it was
	 * allocated by the system, not the application. A value of
	 * <code>null</code> will be returned if the supplied constant
	 * is not an SWT cursor constant.
	 * 
	 * @param swtConstant
	 *            the SWT cursor constant
	 * @return the corresponding cursor or <code>null</code>
	 */
	public static Cursor getCursor(int swtConstant) {
		return display.getSystemCursor(swtConstant);
	}

	/**
	 * @see de.xirp.managers.AbstractManager#start()
	 */
	@Override
	protected void start() throws ManagerException {
		super.start( );

	}

	/**
	 * @see de.xirp.managers.AbstractManager#stop()
	 */
	@Override
	protected void stop() throws ManagerException {
		super.stop( );
	}
}
