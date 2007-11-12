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
 * ManagerException.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 13.02.2007:		Created by Matthias Gernand.
 */
package de.xirp.managers;

/**
 * Exception which is throw by the managers if something went wrong
 * during start or stop of the manager.
 * 
 * @author Matthias Gernand
 */
public class ManagerException extends Exception {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -6729118861368717995L;

	/**
	 * Constructs a new empty exception without message and throwable.
	 */
	public ManagerException() {
		super( );
	}

	/**
	 * Constructs a new exception with the given error message and
	 * throwable.
	 * 
	 * @param message
	 *            the message
	 * @param thr
	 *            the base exception
	 */
	public ManagerException(String message, Throwable thr) {
		super(message, thr);
	}

	/**
	 * Constructs a new exception with the given error message
	 * 
	 * @param message
	 *            the message
	 */
	public ManagerException(String message) {
		super(message);
	}

	/**
	 * Constructs a new exception with the given throwable.
	 * 
	 * @param thr
	 *            the base exception
	 */
	public ManagerException(Throwable thr) {
		super(thr);
	}
}
