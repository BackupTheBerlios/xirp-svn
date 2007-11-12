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
 * ByteEvent.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 05.02.2007:		Created by Matthias Gernand.
 */
package de.xirp.io.comm.lowlevel;

import java.util.EventObject;

import de.xirp.io.comm.DataAmount;

/**
 * Event which is thrown when bytes are send or received.
 * 
 * @author Matthias Gernand
 */
public class ByteEvent extends EventObject {

	/**
	 * The serial version UID needed for serialization
	 */
	private static final long serialVersionUID = 5084872520464957455L;

	/**
	 * The data-amount which was send or received
	 */
	private DataAmount amount;

	/**
	 * Constructs a new event with the given data amount.
	 * 
	 * @param source
	 *            the source which threw the event
	 * @param amount
	 *            the data amount
	 */
	public ByteEvent(Object source, DataAmount amount) {
		super(source);
		this.amount = amount;
	}

	/**
	 * Gets the data amount which was send or received when throwing
	 * this event.
	 * 
	 * @return the amount
	 */
	public DataAmount getAmount() {
		return amount;
	}

}
