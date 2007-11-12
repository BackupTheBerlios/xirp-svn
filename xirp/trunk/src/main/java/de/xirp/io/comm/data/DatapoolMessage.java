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
 * DatapoolMessage.java
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

/**
 * A Message which can be handled by the datapool and must be used for
 * received and send data which goes to the datapool.
 * 
 * @author Matthias Gernand
 * @author Rabea Gransberger
 */
public class DatapoolMessage {

	/**
	 * the datapool key
	 */
	private String key;
	/**
	 * the value
	 */
	private Object value;
	/**
	 * [Received only] <code>true</code> if the message may be
	 * dropped if a message with the same key is already in process at
	 * the datapool
	 */
	private boolean mayDrop = false;
	/**
	 * The timestamp at which the message was received by the robot
	 */
	private long timestamp;

	/**
	 * Constructs a new message with the given key and value, the
	 * timestamp pointing to the current time and a drop flag of
	 * <code>true</code>
	 * 
	 * @param key
	 *            the datapool key of the message
	 * @param value
	 *            the value of the message
	 */
	public DatapoolMessage(String key, Object value) {
		this(key, value, true);
	}

	/**
	 * Constructs a new message with the given timestamp, key and
	 * value and a drop flag of <code>true</code>
	 * 
	 * @param timestamp
	 *            the timestamp for this message
	 * @param key
	 *            the datapool key of the message
	 * @param value
	 *            the value of the message
	 */
	public DatapoolMessage(long timestamp, String key, Object value) {
		this(timestamp, key, value, true);
	}

	/**
	 * Constructs a new message with the given drop flag, key and
	 * value, the timestamp pointing to the current time
	 * 
	 * @param key
	 *            the datapool key of the message
	 * @param value
	 *            the value of the message
	 * @param mayDrop
	 *            <code>true</code> if the message may be dropped if
	 *            a message with the same key is already in process at
	 *            the datapool
	 */
	public DatapoolMessage(String key, Object value, boolean mayDrop) {
		this(System.currentTimeMillis( ), key, value, mayDrop);
	}

	/**
	 * Constructs a new message with the given parameters
	 * 
	 * @param timestamp
	 *            the timestamp for this message
	 * @param key
	 *            the datapool key of the message
	 * @param value
	 *            the value of the message
	 * @param mayDrop
	 *            <code>true</code> if the message may be dropped if
	 *            a message with the same key is already in process at
	 *            the datapool
	 */
	public DatapoolMessage(long timestamp, String key, Object value,
			boolean mayDrop) {
		this.timestamp = timestamp;
		this.key = key;
		this.value = value;
		this.mayDrop = mayDrop;
	}

	/**
	 * Gets the datapool key of the message
	 * 
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Gets the value of the message
	 * 
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * [Received only] <code>true</code> if the message may be
	 * dropped if a message with the same key is already in process at
	 * the datapool
	 * 
	 * @return <code>true</code> if the message may be dropped
	 */
	public boolean isMayDrop() {
		return mayDrop;
	}

	/**
	 * Gets the time in milliseconds at which the message was created
	 * or received by the robot, dependent on how the communication
	 * uses this timestamp.
	 * 
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

}
