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
 * HotkeyException.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 09.02.2006:		Created by Matthias Gernand.
 */
package de.xirp.ui.util;

/**
 * Exception used by the {@link HotkeyManager}.
 * 
 * @author Matthias Gernand
 */
public class HotkeyException extends Exception {

	/**
	 * The serialization ID.
	 */
	private static final long serialVersionUID = 664791871669540272L;

	/**
	 * Constructs a new HotkeyException.
	 */
	public HotkeyException() {
		super( );
	}

	/**
	 * Constructs a new HotkeyException with the given message.
	 * 
	 * @param message
	 *            The exceptions message
	 */
	public HotkeyException(String message) {
		super(message);
	}

	/**
	 * Constructs a new HotkeyException with the given message and
	 * cause.
	 * 
	 * @param message
	 *            The exceptions message.
	 * @param cause
	 *            The exceptions cause.
	 */
	public HotkeyException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new KotkeyException with given cause.
	 * 
	 * @param cause
	 *            The exceptions cause.
	 */
	public HotkeyException(Throwable cause) {
		super(cause);
	}

}
