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
 * AbstractHandler.java
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

import java.util.List;

import org.apache.log4j.Logger;

import de.xirp.io.comm.data.Datapool;
import de.xirp.io.comm.data.DatapoolException;
import de.xirp.io.comm.data.DatapoolManager;
import de.xirp.io.comm.data.DatapoolMessage;
import de.xirp.io.comm.protocol.IProtocol;
import de.xirp.io.comm.protocol.IProtocolMessage;
import de.xirp.plugin.AbstractData;
import de.xirp.plugin.AbstractPlugin;
import de.xirp.plugin.AbstractPluginGUI;
import de.xirp.plugin.PluginInfo;
import de.xirp.plugin.PluginType;
import de.xirp.plugin.VisualizationType;
import de.xirp.util.Constants;

/**
 * An abstract plugin implementation of the handler interface.<br/><br/>
 * You should use this if you want to implement a handler as a plugin.
 * Just override the two convert methods.
 * 
 * @author Matthias Gernand
 * @author Rabea Gransberger
 * @param <GAbstractData>
 *            The AbstractData implementation that should be used by
 *            the plugin.
 * @param <GAbstractPluginGUI>
 *            The AbstractPluginGUI implementation that should be used
 *            by the plugin.
 */
public abstract class AbstractHandler<GAbstractData extends AbstractData, GAbstractPluginGUI extends AbstractPluginGUI>
		extends AbstractPlugin<GAbstractData, GAbstractPluginGUI> implements
		IHandler {

	/**
	 * The Logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(AbstractHandler.class);

	/**
	 * The protocol used for sending data to the robot
	 */
	protected IProtocol<?, ?> protocol;
	/**
	 * The datapool to write data to
	 */
	protected Datapool datapool;

	/**
	 * Constructs a new hander plugin for the given robot and
	 * information about the plugin itself.<br/><br/>Don't call it
	 * on your own, it is called when a connection to the robot is
	 * established.
	 * 
	 * @param robotName
	 *            the name of the robot this handler is for
	 * @param ownInfo
	 *            information about this plugin
	 */
	public AbstractHandler(String robotName, PluginInfo ownInfo) {
		super(robotName, ownInfo);
	}

	/**
	 * Gets the datapool for this robot
	 * 
	 * @return the datapool
	 */
	private Datapool getDatapool() {
		// double check to be fast and sure that not two instances are
		// created
		if (datapool == null) {
			synchronized (this) {
				if (datapool == null) {
					try {
						datapool = DatapoolManager.getDatapool(this.getRobotName( ));
					}
					catch (DatapoolException e) {
						logClass.error("Error " + e.getMessage( ) + Constants.LINE_SEPARATOR, e); //$NON-NLS-1$
					}
				}
			}
		}
		return datapool;
	}

	/**
	 * Calls {@link #convert(IHandlerMessage)} to convert the message
	 * to a form the datapool understands and than forwards it to the
	 * datapool
	 * 
	 * @see de.xirp.io.comm.handler.IHandler#receiveToDatapool(de.xirp.io.comm.handler.IHandlerMessage)
	 */
	public void receiveToDatapool(IHandlerMessage message) {
		Datapool datapool = getDatapool( );
		if (datapool != null) {
			List<DatapoolMessage> messages = convert(message);
			if (messages != null) {
				for (DatapoolMessage m : messages) {
					datapool.receiveToDatapool(m);
				}
			}
		}
	}

	/**
	 * Converts the datapool message to messages the robot can
	 * understands by calling {@link #convert(DatapoolMessage)} and
	 * then sends the message to the robot by using the protocols send
	 * method.
	 * 
	 * @see de.xirp.io.comm.handler.IHandler#sendToRobot(de.xirp.io.comm.data.DatapoolMessage)
	 */
	public void sendToRobot(DatapoolMessage message) {
		if (protocol != null) {
			List<IProtocolMessage> messages = convert(message);
			if (messages != null) {
				for (IProtocolMessage mess : messages) {
					if (mess != null && protocol != null) {
						protocol.sendToRobot(mess);
					}
				}
			}
		}
	}

	/**
	 * Sets the protocol which is used to send data to the robot
	 * 
	 * @param iProtocol
	 *            the protocol to set
	 */
	public void setIProtocol(IProtocol<?, ?> iProtocol) {
		this.protocol = iProtocol;
	}

	/**
	 * Does nothing
	 * 
	 * @see de.xirp.plugin.AbstractPlugin#runInternal()
	 */
	@Override
	protected void runInternal() {
		// nothing to do
	}

	/**
	 * Does nothing
	 * 
	 * @see de.xirp.plugin.AbstractPlugin#stopInternal()
	 */
	@Override
	protected boolean stopInternal() {
		return true;
	}

	/**
	 * Returns the message handler plugin type
	 * 
	 * @return {@link de.xirp.plugin.PluginType#MESSAGE_HANDLER}
	 * @see de.xirp.plugin.IPlugable#getPluginType()
	 */
	public int getPluginType() {
		return PluginType.MESSAGE_HANDLER;
	}

	/**
	 * Normally handlers don't have a user interface
	 * 
	 * @return {@link de.xirp.plugin.VisualizationType#NONE}
	 * @see de.xirp.plugin.IPlugable#getVisualizationType()
	 */
	public int getVisualizationType() {
		return VisualizationType.NONE;
	}

	/**
	 * Converts a message which should be send to the robot from the
	 * form the datapool uses to a form the robot understands. One
	 * message from the datapool may result in more than one message
	 * send to the robot. This method is called by
	 * {@link #sendToRobot(DatapoolMessage)}
	 * 
	 * @param message
	 *            the message to convert before sending it to the
	 *            robot
	 * @return a list of messages ready to be send to the robot
	 */
	protected abstract List<IProtocolMessage> convert(DatapoolMessage message);

	/**
	 * Converts a message received from the robot to a form the
	 * datapool understands. One message from the robot may result in
	 * more than one message forwarded to the datapool. This method is
	 * called by {@link #receiveToDatapool(IHandlerMessage)}
	 * 
	 * @param message
	 *            the message to forward to the datapool
	 * @return the converted messages
	 */
	protected abstract List<DatapoolMessage> convert(IHandlerMessage message);
}
