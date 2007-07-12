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
 * ConnectionListener.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 02.04.2006:		Created by Rabea Gransberger.
 */
package de.xirp.io.event;

import java.util.EventListener;

/**
 * Listener which is informed on connect or disconnect to/from a robot.
 * 
 * @author Rabea Gransberger
 */
public interface ConnectionListener extends EventListener {

	/**
	 * Information that a connection has been established.<br>
	 * <b>Note:</b> This event may have been thrown by a thread. In
	 * SWT only the ONE GUI thread is allowed to access SWT widgets or
	 * other GUI elements. In order to access the GUI elements from
	 * this event you have to encapsulate this code in a
	 * {@link de.xirp.ui.util.SWTUtil#asyncExec(Runnable)}
	 * or
	 * {@link de.xirp.ui.util.SWTUtil#syncExec(Runnable)}
	 * method.
	 * 
	 * @param event
	 *            Information about the connection
	 */
	public void connectionEstablished(ConnectionEvent event);

	/**
	 * Information about a disconnect.<br>
	 * <b>Note:</b> This event may have been thrown by a thread. In
	 * SWT only the ONE GUI thread is allowed to access SWT widgets or
	 * other GUI elements. In order to access the GUI elements from
	 * this event you have to encapsulate this code in a
	 * {@link de.xirp.ui.util.SWTUtil#asyncExec(Runnable)}
	 * or
	 * {@link de.xirp.ui.util.SWTUtil#syncExec(Runnable)}
	 * method.
	 * 
	 * @param event
	 *            Holds the information
	 */
	public void disconnected(ConnectionEvent event);
}
