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
 * IProtocol.java
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
package de.xirp.io.comm.protocol;

import de.xirp.io.comm.lowlevel.ICommunicationInterface;

/**
 * Interface which defines the base methods for communication with a
 * robot. Implementations of this interface can be seen as the
 * protocol implementation for communication with the robot.
 * 
 * @param <S>
 *            Type of data which is send to the robot like
 *            <code>byte []</code>. Used for the communication
 *            interface.
 * @param <R>
 *            Type of stream for receiving data from the robot like
 *            <code>InputStream</code>. Used for the communication
 *            interface.
 * @author Matthias Gernand
 * @author Rabea Gransberger
 */
public interface IProtocol<S, R> {

	/**
	 * Start communication with the robot. At first you have to set up
	 * a connection using the communication interface.
	 * 
	 * @return <code>true</code> if communication was started
	 */
	public boolean startCommunication();

	/**
	 * Stops the communication with the robot and disconnects from it.
	 */
	public void stopCommunication();

	/**
	 * Sends the message directly to the robot using the interface.
	 * 
	 * @param message
	 *            the message to send
	 */
	public void sendToRobot(IProtocolMessage message);

	/**
	 * Starts receiving data from the robot using the interface
	 */
	public void receive();

	/**
	 * Sets the interface to use for lowlevel communication with the
	 * robot.<br/> This method is called by the CommunicationManager
	 * when a connection should be established.
	 * 
	 * @param cInterface
	 */
	public void setCInterface(ICommunicationInterface<S, R> cInterface);

}
