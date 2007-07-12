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
 * ConnectionEvent.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 02.04.2006:		Created by Rabea Gransberger.
 */
package de.xirp.io.event;

import java.util.EventObject;

/**
 * Holds the data which informs about a connection event
 * 
 * @author Rabea Gransberger
 */
public class ConnectionEvent extends EventObject {

	/**
	 * ID for Serialization
	 */
	private static final long serialVersionUID = 3414866571213804726L;
	/**
	 * Name of the robot this event belongs to
	 */
	private String robotName;

	/**
	 * Constructs a new event to the given robot
	 * 
	 * @param source
	 *            Source of the Event
	 * @param robotName
	 *            Name of the Robot
	 */
	public ConnectionEvent(Object source, String robotName) {
		super(source);
		this.robotName = robotName;
	}

	/**
	 * Gets the name of the robot this event belongs to
	 * 
	 * @return the robotName
	 */
	public String getRobotName() {
		return robotName;
	}

}
