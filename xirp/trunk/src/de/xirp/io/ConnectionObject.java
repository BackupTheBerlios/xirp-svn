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
 * ConnectionObject.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 16.02.2006:		Created by Rabea Gransberger.
 */
package de.xirp.io;

/**
 * Object which holds information needed to connect to a robot.
 * 
 * @author Rabea Gransberger
 */
public class ConnectionObject {

	/**
	 * Name of the robot for which this object is for
	 */
	private String robotName;
	/**
	 * Class for establishing connection to a device
	 */
	private String communicationClass;
	/**
	 * Class for communication with the robot
	 */
	private String protocolClass;

	/**
	 * Constructs a new connection object with the needed information
	 * to establish the connection to a robot
	 * 
	 * @param name
	 *            Name of the robot
	 * @param comClass
	 *            Name of the class for establishing connection to a
	 *            device
	 * @param robotComClass
	 *            Name of the class for communication with the robot
	 */
	public ConnectionObject(String name, String comClass, String robotComClass) {
		communicationClass = comClass;
		protocolClass = robotComClass;
		robotName = name;
	}

	/**
	 * Gets the fully qualified class name of the class used for
	 * connection to a device.
	 * 
	 * @return Returns the device communication main class.
	 */
	public String getCommunicationClass() {
		return communicationClass;
	}

	/**
	 * Gets the fully qualified class name of the protocol used for
	 * connection to a robot.
	 * 
	 * @return Returns the protocols main class
	 */
	public String getProtocolClass() {
		return protocolClass;
	}

	/**
	 * Gets the name of the robot for which the connection should be
	 * created
	 * 
	 * @return Returns the robotName.
	 */
	public String getRobotName() {
		return robotName;
	}

	/**
	 * Gets a debug string of this object holding all information of
	 * the object.
	 */
	@Override
	public String toString() {
		return robotName + " " + communicationClass + " " //$NON-NLS-1$ //$NON-NLS-2$
				+ protocolClass;
	}

}
