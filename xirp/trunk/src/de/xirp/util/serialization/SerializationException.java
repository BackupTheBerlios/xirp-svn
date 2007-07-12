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
 * SerializationException.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 18.02.2007:		Created by Matthias Gernand.
 */
package de.xirp.util.serialization;

/**
 * Exception which is used when an error occurs on serialization or
 * de.serialization.
 * 
 * @author Matthias Gernand
 */
public class SerializationException extends Exception {

	/**
	 * The serial version unique identifier
	 */
	private static final long serialVersionUID = -3117299805033289334L;

	/**
	 * Constructs a new empty serialization exception with no message
	 * and no cause.
	 */
	public SerializationException() {
		super( );
	}

	/**
	 * Constructs a new exception with the given message and
	 * underlying cause.
	 * 
	 * @param message
	 *            the message which describes the exception
	 * @param throwable
	 *            the underlying cause of the exception
	 */
	public SerializationException(String message, Throwable throwable) {
		super(message, throwable);
	}

	/**
	 * Constructs a new exception with the given message.
	 * 
	 * @param message
	 *            the message which describes the exception
	 */
	public SerializationException(String message) {
		super(message);
	}

	/**
	 * Constructs a new exception with the given underlying cause.
	 * 
	 * @param throwable
	 *            the underlying cause of the exception
	 */
	public SerializationException(Throwable throwable) {
		super(throwable);
	}

}
