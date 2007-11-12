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
 * AbstractManager.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   Rabea Gransberger [rgransberger AT web.de]
 *
 * Changes
 * -------
 * 23.10.2006:		Created by Matthias Gernand.
 * 10.10.2007:		Progress events.
 */
package de.xirp.managers;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.xirp.ui.event.ProgressEvent;
import de.xirp.ui.event.ProgressListener;
import de.xirp.util.Constants;
import de.xirp.util.I18n;

/**
 * The abstract base for managers of the application.<br/> If you
 * implement your own manager, be sure to put startup code into the
 * {@link ManagerFactory}.
 * 
 * @author Matthias Gernand
 * @author Rabea Gransberger
 */
public abstract class AbstractManager {

	/**
	 * The log4j logger
	 */
	private Logger logClass = Logger.getLogger(AbstractManager.class);
	/**
	 * List of listeners which should be informed if the manager makes
	 * progress on startup
	 */
	private List<ProgressListener> listeners = new ArrayList<ProgressListener>( );
	/**
	 * A static list of manager to be sure that only one instance of
	 * each manager is started and an {@link InstantiationException}
	 * is thrown otherwise.
	 */
	private static List<String> managers = new ArrayList<String>( );

	/**
	 * Constructs a new instance of this manager if an instance does
	 * not already exist.<br/> Any manager implementation has to have
	 * the constructor at public access (except these at the local
	 * manager package), even if the compiler does not warn at
	 * protected access.
	 * 
	 * @throws InstantiationException
	 *             thrown if an instance of the manager does already
	 *             exist.
	 */
	protected AbstractManager() throws InstantiationException {
		if (managers.contains(getClass( ).getName( ))) {
			throw new InstantiationException(I18n.getString("AbstractManager.exception.onlyOneInstanceAllowed") + getClass( ).getSimpleName( )); //$NON-NLS-1$
		}
		else {
			managers.add(getClass( ).getName( ));
		}
	}

	/**
	 * Adds a listener to this manager which likes to be informed on
	 * every progress this manager makes on startup.
	 * 
	 * @param listener
	 *            the listener to register
	 */
	protected void addProgressListener(ProgressListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes the given progress listener of this manager
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	protected void removeProgressListener(ProgressListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Sets the listener list to <code>null</code> to free the
	 * memory used by the managers.
	 */
	protected final void deleteProgressListenerList() {
		listeners.clear( );
		listeners = null;
	}

	/**
	 * Informs all progress listeners that this manager progressed on
	 * startup.
	 * 
	 * @param message
	 *            the message according to the progress
	 * @param percentage
	 *            the percentage of the progress
	 */
	protected void throwProgressEvent(String message, double percentage) {
		ProgressEvent event = new ProgressEvent(this, percentage, message);
		for (ProgressListener listener : listeners) {
			listener.progress(event);
		}
	}

	/**
	 * Informs all progress listeners that this manager progressed on
	 * startup.
	 * 
	 * @param message
	 *            the message according to the progress
	 */
	protected void throwProgressEvent(String message) {
		ProgressEvent event = new ProgressEvent(this, -1, message);
		for (ProgressListener listener : listeners) {
			listener.progress(event);
		}
	}

	/**
	 * Gets the message which is shown as default in the progress bar
	 * when starting this manager
	 * 
	 * @return the message shown on startup of this manager
	 */
	protected String getStartMessage() {
		return I18n.getString("AbstractManager.message.starting", getClass( ).getSimpleName( )); //$NON-NLS-1$
	}

	/**
	 * Create a new Manager object. You have to call super.start() to
	 * inform the Application of the start of the manager.<br/> This
	 * method needs only protected access in your own implementations.
	 * 
	 * @throws ManagerException
	 *             thrown if some exception occurs at startup
	 */
	@SuppressWarnings("unused")
	protected void start() throws ManagerException {
		logClass.info(I18n.getString("AbstractManager.log.started", getClass( ).getSimpleName( )) + Constants.LINE_SEPARATOR); //$NON-NLS-1$ 
	}

	/**
	 * Cleanup the manager. You have to call super.stop() to inform
	 * the Application of the stoppage of the manager.<br/> This
	 * method needs only protected access in your own implementations.
	 * 
	 * @throws ManagerException
	 *             thrown if some exception occurs at shutdown
	 */
	@SuppressWarnings("unused")
	protected void stop() throws ManagerException {
		logClass.info(I18n.getString("AbstractManager.log.stopped", getClass( ).getSimpleName( )) + Constants.LINE_SEPARATOR); //$NON-NLS-1$ 
	}
}
