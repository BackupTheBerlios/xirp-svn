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
 * IHandler.java
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
package de.xirp.io.comm.handler;

import de.xirp.io.comm.data.DatapoolMessage;
import de.xirp.io.comm.protocol.IProtocol;

/**
 * A handler interface which handles data sent between datapool and
 * robot.<br/><br/>Sometimes it may be necessary to convert data
 * which is received from the robot before it is forwarded to the
 * datapool to provide it in a form a plugin you might want to use
 * understands.<br/><br/>The other way round plugins which have the
 * ability to control the robot may have been written generic, so that
 * they just send raw data to the robot. In this case the handler can
 * intercept the data and convert it to a form the robot understands.<br/><br/>
 * Without a handler you can not send data to the robot, but you may
 * receive data without a handler.
 * 
 * @author Matthias Gernand
 * @author Rabea Gransberger
 * @see de.xirp.io.comm.handler.AbstractHandler
 */
public interface IHandler {

	/**
	 * Convert the data to a form the robot will understand and send
	 * it to the robot using the protocol.
	 * 
	 * @param message
	 *            the message to send to the robot
	 */
	public void sendToRobot(DatapoolMessage message);

	/**
	 * Convert the data incoming from the protocol to a form the
	 * datapool can handle and forward it to the datapool.
	 * 
	 * @param message
	 *            the message to forward to the datapool
	 */
	public void receiveToDatapool(IHandlerMessage message);

	/**
	 * Sets the protocol to use in this handler.
	 * 
	 * @param protocol
	 *            the protocol to use to send data to the robot
	 */
	public void setIProtocol(IProtocol<?, ?> protocol);

}
