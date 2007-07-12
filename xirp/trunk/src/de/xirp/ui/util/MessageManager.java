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
 * MessageManager.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 26.05.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.util;

import de.xirp.managers.AbstractManager;
import de.xirp.managers.ManagerException;
import de.xirp.ui.Application;

/**
 * This manager provides a method to show a pop-up
 * tool-tip at the tray icon. This feature may be
 * not available in MS Windows systems, because the
 * tray icon can be deactivated.
 * 
 * @author Matthias Gernand
 */
public final class MessageManager extends AbstractManager {

	// private static Logger logClass =
	// Logger.getLogger(MessageManager.class);

	/**
	 * Enumeration for indicating the type of
	 * the message to show.
	 * 
	 * @author Matthias Gernand
	 */
	public enum MessageType {
		/**
		 * Informations are shown.
		 */
		INFO,
		/**
		 * Warnings are shown.
		 */
		WARN,
		/**
		 * Errors are shown.
		 */
		ERROR
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
	public MessageManager() throws InstantiationException {
		super( );
	}

	/**
	 * Shows a tool-tip at the tray icon for the given
	 * message, title and type.
	 * 
	 * @param title
	 * 			The title of the tool-tip.
	 * @param message
	 * 			The message of the tool-tip.
	 * @param type
	 * 			The type of the message.
	 * 
	 * @see de.xirp.ui.util.MessageManager.MessageType
	 */
	public static void showToolTip(String title, String message,
			MessageType type) {
		Application.getApplication( ).getAppTray( ).showToolTip(title,
				message,
				type);
	}

	/**
	 * Does nothing.
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
	 * Does nothing.
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
