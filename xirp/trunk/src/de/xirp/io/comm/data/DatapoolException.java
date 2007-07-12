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
 * DatapoolException.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 11.02.2007:		Created by Matthias Gernand.
 */
package de.xirp.io.comm.data;

/**
 * Exception which is thrown when something is not normal at the
 * datapool.
 * 
 * @author Matthias Gernand
 * @author Rabea Gransberger
 */
public class DatapoolException extends Exception {

	/**
	 * Unique id for serialization.
	 */
	private static final long serialVersionUID = -8068239132273497566L;

	/**
	 * Constructs a new datapool exception without a message and
	 * cause.
	 */
	public DatapoolException() {
		super( );
	}

	/**
	 * Constructs a new datapool exception with the given message and
	 * cause.
	 * 
	 * @param message
	 *            the message which describes this exception
	 * @param thr
	 *            the cause of this exception
	 */
	public DatapoolException(String message, Throwable thr) {
		super(message, thr);
	}

	/**
	 * Constructs a new datapool exception with the given message.
	 * 
	 * @param message
	 *            the message which describes this exception
	 */
	public DatapoolException(String message) {
		super(message);
	}

	/**
	 * Constructs a new datapool exception with the given cause.
	 * 
	 * @param thr
	 *            the cause of this exception
	 */
	public DatapoolException(Throwable thr) {
		super(thr);
	}

}
