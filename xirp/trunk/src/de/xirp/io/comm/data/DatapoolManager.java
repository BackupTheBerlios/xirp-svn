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
 * DatapoolManager.java
 * ----------------------------
 *
 * Original Author:  Matthias Gernand [matthias.gernand AT gmx.de]
 * 					 Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 01.04.2006:		Created by Matthias Gernand.
 */
package de.xirp.io.comm.data;

import javolution.util.FastMap;

import org.apache.log4j.Logger;

import de.xirp.managers.AbstractManager;
import de.xirp.managers.ManagerException;
import de.xirp.profile.ProfileManager;
import de.xirp.profile.Robot;
import de.xirp.profile.RobotNotFoundException;
import de.xirp.util.Constants;
import de.xirp.util.I18n;

/**
 * This manager holds all created datapools for the robots.
 * 
 * @author Matthias Gernand
 * @author Rabea Gransberger
 */
public final class DatapoolManager extends AbstractManager {

	/**
	 * The Logger for this class.
	 */
	private static final Logger logClass = Logger.getLogger(DatapoolManager.class);
	/**
	 * The existing datapools for the robots (key)
	 */
	private static FastMap<String, Datapool> datapools = new FastMap<String, Datapool>( ).setShared(true);

	/**
	 * Constructs a new DatapoolManager. Never call this constructs on
	 * your own, it is initialized on startup. Use the statically
	 * provided methods.
	 * 
	 * @throws InstantiationException
	 *             if an instance of the datapool manager already
	 *             exists
	 */
	public DatapoolManager() throws InstantiationException {
		super( );

	}

	/**
	 * Returns the datapool for a given robot. If the datapool does
	 * not exist, it will be created.
	 * 
	 * @param robot
	 *            The robot.
	 * @return The datapool for the robot.
	 * @throws DatapoolException
	 *             If the robot is <code>null</code>
	 */
	public static Datapool getDatapool(Robot robot) throws DatapoolException {
		if (robot == null) {
			throw new DatapoolException(I18n.getString("DatapoolManager.exception.canNotCreateDatapool")); //$NON-NLS-1$
		}

		Datapool pool = datapools.get(robot.getName( ));
		if (pool == null) {
			pool = new Datapool(robot);
			datapools.put(robot.getName( ), pool);
		}

		return pool;
	}

	/**
	 * Returns the datapool for a given robot. If the datapool does
	 * not exist, it will be created.
	 * 
	 * @param robotName
	 *            The robots name.
	 * @return Datapool The datapool for the robot.
	 * @throws DatapoolException
	 *             If there is no robot for the given name
	 */
	public static Datapool getDatapool(String robotName)
			throws DatapoolException {
		try {
			return getDatapool(ProfileManager.getRobot(robotName));
		}
		catch (RobotNotFoundException e) {
			// logClass.error("Error " + e.getMessage() +
			// Constants.LINE_SEPARATOR, e);
			throw new DatapoolException(e);
		}
	}

	/**
	 * Initializes the datapools for all robots known to the
	 * {@link ProfileManager}.<br/><br/>This method is called from
	 * the {@link de.xirp.managers.ManagerFactory} on
	 * startup. Do not call it on your own.
	 * 
	 * @see de.xirp.managers.AbstractManager#start()
	 */
	@Override
	protected void start() throws ManagerException {
		super.start( );
		logClass.info(I18n.getString("DatapoolManager.log.initializing")); //$NON-NLS-1$
		for (Robot robot : ProfileManager.getRobots( )) {
			try {
				getDatapool(robot);
			}
			catch (DatapoolException e) {
				throw new ManagerException(e);
			}
		}
		logClass.info(I18n.getString("DatapoolManager.log.finished") + Constants.LINE_SEPARATOR); //$NON-NLS-1$
	}

	/**
	 * Stops all registered datapools.<br/><br/>This method is
	 * called from the
	 * {@link de.xirp.managers.ManagerFactory} on
	 * shutdown. Do not call it on your own.
	 * 
	 * @see de.xirp.managers.AbstractManager#stop()
	 */
	@Override
	protected void stop() throws ManagerException {
		super.stop( );
		for (Datapool pool : datapools.values( )) {
			pool.stop( );
		}
		datapools.clear( );
	}
}
