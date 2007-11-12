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
 * ICommunicationInterface.java
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
package de.xirp.io.comm.lowlevel;

import de.xirp.io.event.ConnectionListener;

/**
 * A interface for a communication interface over which the connection
 * to the robot is established.<br/><br/>Implementations might be a
 * SerialInterface or a IPCommunication. <br/><br/> The interface
 * defines method for connecting/disconnecting, receiving/sending data
 * and adding/removing some listeners to monitor the connection to the
 * robot.
 * 
 * @param <S>
 *            Type of data which is send to the robot like
 *            <code>byte []</code>
 * @param <R>
 *            Type of stream for receiving data from the robot like
 *            <code>InputStream</code>
 * @author Matthias Gernand
 * @author Rabea Gransberger
 */
public interface ICommunicationInterface<S, R> {

	/**
	 * Creates a connection
	 * 
	 * @return <code>true</code> if the connection was created
	 */
	public boolean connect();

	/**
	 * Disconnects from and cleans up all structures which are not
	 * used any more and reinitializes them to be able to reconnect
	 * again hereafter.
	 */
	public void disconnect();

	/**
	 * Checks if a connection exists
	 * 
	 * @return <code>true</code> if a connection exits
	 */
	public boolean isConnected();

	/**
	 * Sends data over this interface
	 * 
	 * @param sendObject
	 *            the data to send
	 */
	public void send(S sendObject);

	/**
	 * Gets the input stream to receive data from
	 * 
	 * @return the input stream for receiving
	 */
	public R receive();

	/**
	 * Adds a listener which is informed when bytes are send over this
	 * interface. The implementation should not sum up the send bytes,
	 * this is handled elsewhere.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void addBytesSendListener(IByteListener listener);

	/**
	 * Adds a listener which is informed when bytes are received over
	 * this interface. The implementation should not sum up the
	 * received bytes, this is handled elsewhere.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void addBytesReceivedListener(IByteListener listener);

	/**
	 * Removes the listener which is informed when bytes are send over
	 * this interface.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeBytesSendListener(IByteListener listener);

	/**
	 * Removes the listener which is informed when bytes are received
	 * over this interface.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeBytesReceivedListener(IByteListener listener);

	/**
	 * Adds a listener which has to be informed when a connection is
	 * created or a disconnect occurs.
	 * 
	 * @param listener
	 *            the listener to add
	 * @see de.xirp.io.comm.CommunicationManager#addConnectionListener(ConnectionListener)
	 */
	public void addConnectionEventListener(ConnectionListener listener);

	/**
	 * Removes the listener which has been informed when a connection
	 * was created or a disconnect occurs.
	 * 
	 * @param listener
	 *            the listener to remove
	 * @see de.xirp.io.comm.CommunicationManager#removeConnectionListener(ConnectionListener)
	 */
	public void removeConnectionEventListener(ConnectionListener listener);
}
