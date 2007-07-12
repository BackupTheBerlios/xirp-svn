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
 * ProgressEvent.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 10.05.2006:		Created by Rabea Gransberger.
 */
package de.xirp.ui.event;

import java.util.EventObject;

/**
 * Event which notifies of progress made.
 * 
 * @author Rabea Gransberger
 */
public class ProgressEvent extends EventObject {

	/**
	 * The serial version unique identifier
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The message which describes the progress
	 */
	private String message;
	/**
	 * The percentage of the current progress
	 */
	private double percentage;

	/**
	 * Creates a new progress event.
	 * 
	 * @param source
	 *            the source of the event
	 * @param percentage
	 *            the percentage of the current progress
	 * @param message
	 *            the message which describes the progress
	 */
	public ProgressEvent(Object source, double percentage, String message) {
		super(source);
		this.message = message;
		this.percentage = percentage;
	}

	/**
	 * Gets the message which describes the progress made.
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Gets the percentage of progress made.
	 * 
	 * @return the percentage
	 */
	public double getPercentage() {
		return percentage;
	}

}
