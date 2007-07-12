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
 * Message.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 30.05.2006:		Created by Rabea Gransberger.
 */
package de.xirp.io;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * A class which could be used to encapsulated parameters and values
 * as a message to send as one object.
 * 
 * @author Rabea Gransberger
 */
public class Message {

	/**
	 * Map with the data of this message, identified by the parameter
	 * names
	 */
	private HashMap<String, Object> dataMap = new HashMap<String, Object>( );
	/**
	 * Name identifying the message
	 */
	private String messageName = "none"; //$NON-NLS-1$

	/**
	 * Constructs a new message with the given name.
	 * 
	 * @param messageName
	 *            Name of the message
	 */
	public Message(String messageName) {
		this.messageName = messageName;
	}

	/**
	 * Gets the name of this message.
	 * 
	 * @return name of the message
	 */
	public String getMessageName() {
		return messageName;
	}

	/**
	 * Adds data for the given key to this message.
	 * 
	 * @param key
	 *            Key identifying the data
	 * @param data
	 *            the data
	 */
	public void addData(String key, Object data) {
		dataMap.put(key, data);
	}

	/**
	 * Gets data for the given key from this message.
	 * 
	 * @param key
	 *            key identifying the data
	 * @return data as object.
	 */
	public Object getData(String key) {
		return dataMap.get(key);
	}

	/**
	 * Gets all the known keys of this message.
	 * 
	 * @return the keys used in this message
	 */
	public Set<String> getKeys() {
		return Collections.unmodifiableSet(dataMap.keySet( ));
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
