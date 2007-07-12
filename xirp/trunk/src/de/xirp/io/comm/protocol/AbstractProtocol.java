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
 * AbstractProtocol.java
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

import org.apache.log4j.Logger;

import de.xirp.io.comm.lowlevel.ICommunicationInterface;
import de.xirp.plugin.AbstractPlugin;
import de.xirp.plugin.PluginInfo;
import de.xirp.plugin.PluginType;
import de.xirp.plugin.VisualizationType;
import de.xirp.profile.CommunicationProtocol;
import de.xirp.profile.CommunicationSpecification;
import de.xirp.profile.ProfileManager;
import de.xirp.profile.Robot;
import de.xirp.profile.RobotNotFoundException;
import de.xirp.util.Constants;

/**
 * Abstract plugin implementation of a protocol for communication with
 * a robot.
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
@SuppressWarnings("unchecked")
public abstract class AbstractProtocol<S, R> extends AbstractPlugin implements
		IProtocol<S, R> {

	/**
	 * Log4j Logger for this class
	 */
	private static Logger logClass = Logger.getLogger(AbstractProtocol.class);

	/**
	 * The communication interface to use for lowlevel communication
	 * or <code>null</code> if none was set yet
	 */
	protected ICommunicationInterface<S, R> cInterface;

	/**
	 * Constructs a new protocol plugin for the given robot and
	 * information about the plugin itself.<br/><br/>Don't call it
	 * on your own, it is called when a connection to the robot is
	 * established.
	 * 
	 * @param robotName
	 *            the name of the robot this handler is for
	 * @param ownInfo
	 *            information about this plugin
	 */
	public AbstractProtocol(String robotName, PluginInfo ownInfo) {
		super(robotName, ownInfo);
	}

	/**
	 * Connects to the robot using the interface and starts receiving
	 * by calling {@link #receive()}
	 * 
	 * @see de.xirp.io.comm.protocol.IProtocol#startCommunication()
	 */
	public boolean startCommunication() {
		if (cInterface != null) {
			cInterface.connect( );
			receive( );
			return true;
		}
		return false;
	}

	/**
	 * Disconnects from the robot using the interface's disconnect
	 * method.
	 * 
	 * @see de.xirp.io.comm.protocol.IProtocol#stopCommunication()
	 */
	public void stopCommunication() {
		if (cInterface != null) {
			cInterface.disconnect( );
		}
	}

	/**
	 * Sets the communication interface to a locale field.
	 * 
	 * @param cInterface
	 *            the communication interface to set
	 */
	public void setCInterface(ICommunicationInterface<S, R> cInterface) {
		this.cInterface = cInterface;
	}

	/**
	 * @see de.xirp.plugin.AbstractPlugin#runInternal()
	 */
	@Override
	protected void runInternal() {
		// do nothing
	}

	/**
	 * Stops the communication by calling {@link #stopCommunication()}
	 * 
	 * @see de.xirp.plugin.AbstractPlugin#stopInternal()
	 */
	@Override
	protected boolean stopInternal() {
		stopCommunication( );
		return true;
	}

	/**
	 * Returns the plugin type for protocols
	 * 
	 * @return {@link de.xirp.plugin.PluginType#PROTOCOL}
	 * @see de.xirp.plugin.IPlugable#getPluginType()
	 */
	public int getPluginType() {
		return PluginType.PROTOCOL;
	}

	/**
	 * Normally protocols have no UI
	 * 
	 * @return {@link de.xirp.plugin.VisualizationType#NONE}
	 * @see de.xirp.plugin.IPlugable#getVisualizationType()
	 */
	public int getVisualizationType() {
		return VisualizationType.NONE;
	}

	/**
	 * Gets information about this communication from the profile.
	 * 
	 * @return the communication or <code>null</code> if non found
	 *         for this robot
	 */
	public CommunicationProtocol getCommunicationInfo() {
		try {
			Robot robot = ProfileManager.getRobot(this.getRobotName( ));
			CommunicationSpecification comSpec = robot.getCommunicationSpecification( );
			for (CommunicationProtocol com : comSpec.getCommunicationProtocols( )) {
				if (com.getClassName( ).equals(this.getClass( ).getName( ))) {
					return com;
				}
			}
		}
		catch (RobotNotFoundException e) {
			logClass.error("Error " + e.getMessage( ) //$NON-NLS-1$
					+ Constants.LINE_SEPARATOR, e);
		}
		return null;
	}
}
