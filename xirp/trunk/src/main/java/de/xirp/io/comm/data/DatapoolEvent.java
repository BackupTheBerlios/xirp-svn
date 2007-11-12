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
 * DatapoolEvent.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * 					 Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 05.02.2007:		Created by Matthias Gernand.
 */
package de.xirp.io.comm.data;

import java.util.EventObject;

import de.xirp.profile.Robot;

/**
 * Event thrown by the datapool.
 * 
 * @author Matthias Gernand
 * @author Rabea Gransberger
 */
public class DatapoolEvent extends EventObject {

	/**
	 * Serial Version UID needed for serialization
	 */
	private static final long serialVersionUID = 5709906669995114351L;

	/**
	 * The robot this event belongs to
	 */
	private Robot robot;
	/**
	 * The timestamp at which the event occurred
	 */
	private long timestamp;
	/**
	 * The datapool key of the event
	 */
	private String key;
	/**
	 * The event value
	 */
	private Object value;

	/**
	 * Constructs a new datapool event with the given data
	 * 
	 * @param source
	 *            the object which fired the event
	 * @param robot
	 *            The robot this event belongs to
	 * @param timestamp
	 *            The timestamp at which the event occurred
	 * @param key
	 *            The datapool key of the event
	 * @param value
	 *            The event value
	 */
	protected DatapoolEvent(Object source, Robot robot, long timestamp,
			String key, Object value) {
		super(source);
		this.robot = robot;
		this.timestamp = timestamp;
		this.key = key;
		this.value = value;
	}

	/**
	 * Gets the datapool key of the event.
	 * 
	 * @return the datapool key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Gets the robot the event occurred at.
	 * 
	 * @return the robot
	 */
	public Robot getRobot() {
		return robot;
	}

	/**
	 * Gets the timestamp in milliseconds at which the event occurred.
	 * 
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Gets the value of the event.
	 * 
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Gets a string with all the data of this event.
	 * 
	 * @see java.util.EventObject#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(50);
		builder.append("Timestamp ").append(timestamp).append(", "); //$NON-NLS-1$ //$NON-NLS-2$
		builder.append("Robot ").append(robot.getName( )).append(", "); //$NON-NLS-1$ //$NON-NLS-2$
		builder.append("Key ").append(key).append(", "); //$NON-NLS-1$ //$NON-NLS-2$
		builder.append("Value ").append(value); //$NON-NLS-1$
		return builder.toString( );
	}

}
