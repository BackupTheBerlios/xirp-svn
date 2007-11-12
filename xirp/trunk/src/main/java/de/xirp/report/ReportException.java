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
 * ReportException.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 17.04.2006:		Created by Matthias Gernand.
 */
package de.xirp.report;

/**
 * An exception indicating an report error.
 * 
 * @author Matthias Gernand
 * 
 */
public class ReportException extends Exception {

	/**
	 * The serial version UID of this {@link java.io.Serializable}.
	 */
	private static final long serialVersionUID = 1478973854109299139L;

	/**
	 * Constructs a new exception.
	 */
	public ReportException() {
		super( );
	}

	/**
	 * Constructs a new exception for the given message.
	 * 
	 * @param message
	 * 			The message.
	 */
	public ReportException(String message) {
		super(message);
	}

	/**
	 * Constructs a new exception for the given message and cause.
	 * 
	 * @param message
	 * 			The message.
	 * @param cause
	 * 			The cause.
	 */
	public ReportException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new exception for the given cause.
	 * 
	 * @param cause
	 * 			The cause.
	 */
	public ReportException(Throwable cause) {
		super(cause);
	}

}
