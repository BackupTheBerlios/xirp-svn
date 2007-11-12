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
 * AbstractCommunicationInterface.java
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

import java.util.Vector;

import de.xirp.io.event.ConnectionEvent;
import de.xirp.io.event.ConnectionListener;
import de.xirp.plugin.AbstractPlugin;
import de.xirp.plugin.PluginInfo;
import de.xirp.plugin.PluginType;
import de.xirp.plugin.VisualizationType;

/**
 * An abstract default plugin implementation of a communication
 * interface.
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
@SuppressWarnings("unchecked")
public abstract class AbstractCommunicationInterface<S, R> extends
		AbstractPlugin implements ICommunicationInterface<S, R> {

	/**
	 * List of listeners which should be informed when bytes are
	 * received
	 */
	protected Vector<IByteListener> recvListeners = new Vector<IByteListener>( );
	/**
	 * List of listeners which should be informed when bytes are send
	 */
	protected Vector<IByteListener> sendListeners = new Vector<IByteListener>( );
	/**
	 * List of listeners which should be informed on
	 * connect/disconnect
	 */
	protected Vector<ConnectionListener> connectionListeners = new Vector<ConnectionListener>( );

	/**
	 * Constructs a new communication interface plugin for the given
	 * robot and information about the plugin itself.<br/><br/>Don't
	 * call it on your own, it is called when a connection to the
	 * robot is established.
	 * 
	 * @param robotName
	 *            the name of the robot this handler is for
	 * @param ownInfo
	 *            information about this plugin
	 */
	public AbstractCommunicationInterface(String robotName, PluginInfo ownInfo) {
		super(robotName, ownInfo);
	}

	/**
	 * @see de.xirp.io.comm.lowlevel.ICommunicationInterface#addBytesReceivedListener(de.xirp.io.comm.lowlevel.IByteListener)
	 */
	public void addBytesReceivedListener(IByteListener listener) {
		recvListeners.add(listener);
	}

	/**
	 * @see de.xirp.io.comm.lowlevel.ICommunicationInterface#addBytesSendListener(de.xirp.io.comm.lowlevel.IByteListener)
	 */
	public void addBytesSendListener(IByteListener listener) {
		sendListeners.add(listener);
	}

	/**
	 * @see de.xirp.io.comm.lowlevel.ICommunicationInterface#removeBytesReceivedListener(de.xirp.io.comm.lowlevel.IByteListener)
	 */
	public void removeBytesReceivedListener(IByteListener listener) {
		recvListeners.remove(listener);
	}

	/**
	 * @see de.xirp.io.comm.lowlevel.ICommunicationInterface#removeBytesSendListener(de.xirp.io.comm.lowlevel.IByteListener)
	 */
	public void removeBytesSendListener(IByteListener listener) {
		sendListeners.remove(listener);
	}

	/**
	 * Notifies all listeners that bytes were send over this interface
	 * 
	 * @param event
	 *            the event
	 */
	protected void fireByteSendEvent(ByteEvent event) {
		for (IByteListener listener : sendListeners) {
			listener.handleBytes(event);
		}
	}

	/**
	 * Notifies all listeners that bytes were received over this
	 * interface
	 * 
	 * @param event
	 *            the event
	 */
	protected void fireByteReceivedEvent(ByteEvent event) {
		for (IByteListener listener : recvListeners) {
			listener.handleBytes(event);
		}
	}

	/**
	 * @see de.xirp.io.comm.lowlevel.ICommunicationInterface#addConnectionEventListener(de.xirp.io.event.ConnectionListener)
	 */
	public void addConnectionEventListener(ConnectionListener listener) {
		connectionListeners.add(listener);
	}

	/**
	 * @see de.xirp.io.comm.lowlevel.ICommunicationInterface#removeConnectionEventListener(de.xirp.io.event.ConnectionListener)
	 */
	public void removeConnectionEventListener(ConnectionListener listener) {
		connectionListeners.remove(listener);
	}

	/**
	 * Notifies all listeners that a connection was established.
	 * 
	 * @param event
	 *            the event
	 */
	protected void fireConnectEvent(ConnectionEvent event) {
		for (ConnectionListener listener : connectionListeners) {
			listener.connectionEstablished(event);
		}
	}

	/**
	 * Notifies all listeners that disconnect occurred.
	 * 
	 * @param event
	 *            the event
	 */
	protected void fireDisconnectEvent(ConnectionEvent event) {
		for (ConnectionListener listener : connectionListeners) {
			listener.disconnected(event);
		}
	}

	/**
	 * Does nothing.
	 * 
	 * @see de.xirp.plugin.AbstractPlugin#runInternal()
	 */
	@Override
	protected void runInternal() {
		// nothing to do
	}

	/**
	 * Disconnects this interface.
	 * 
	 * @see de.xirp.plugin.AbstractPlugin#stopInternal()
	 */
	@Override
	protected boolean stopInternal() {
		disconnect( );
		return true;
	}

	/**
	 * Returns that this is communication plugin.
	 * 
	 * @return {@link de.xirp.plugin.PluginType#COMMUNICATION}
	 * @see de.xirp.plugin.IPlugable#getPluginType()
	 */
	public int getPluginType() {
		return PluginType.COMMUNICATION;
	}

	/**
	 * A communication plugin normally has no UI.
	 * 
	 * @return {@link de.xirp.plugin.VisualizationType#NONE}
	 * @see de.xirp.plugin.IPlugable#getVisualizationType()
	 */
	public int getVisualizationType() {
		return VisualizationType.NONE;
	}
}
