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
 * UnrefPluginContainer.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 08.01.2007:		Created by Rabea Gransberger.
 */
package de.xirp.plugin;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import de.xirp.profile.Robot;
import de.xirp.util.Constants;
import de.xirp.util.I18n;

/**
 * A container for plugins which were found by the
 * {@link PluginLoader} but were not found in the profiles for the
 * robots as referenced plugin for a robot.
 * 
 * @author Rabea Gransberger
 */
final class UnrefPluginContainer {

	/**
	 * Log4j Logger for this Class
	 */
	private static Logger logClass = Logger.getLogger(PluginLoader.class);
	/**
	 * Information about the plugin this container is for
	 */
	private PluginInfo info;
	/**
	 * All instances of the plugin
	 */
	@SuppressWarnings("unchecked")
	private ConcurrentHashMap<Integer, SecurePluginView> instances = new ConcurrentHashMap<Integer, SecurePluginView>( );

	/**
	 * Constructs a new container for the given plugin
	 * 
	 * @param info
	 *            information about the plugin
	 */
	protected UnrefPluginContainer(PluginInfo info) {
		this.info = info;
	}

	/**
	 * Gets the information about the plugin of this container.
	 * 
	 * @return the information about the plugin
	 */
	protected PluginInfo getInfo() {
		return info;
	}

	/**
	 * Gets the plugin for the given instance id.
	 * 
	 * @param instanceID
	 *            the instance id to get the plugin for.
	 * @return the plugin or <code>null</code> if there's no running
	 *         plugin for the given id.
	 */
	@SuppressWarnings("unchecked")
	protected SecurePluginView get(int instanceID) {
		return instances.get(instanceID);
	}

	/**
	 * Gets the plugin instance with the highest id.
	 * 
	 * @return the plugin instance with the highest instance id.
	 */
	@SuppressWarnings("unchecked")
	protected SecurePluginView get() {
		return get(getHighestID( ));
	}

	/**
	 * Gets a sample of this plugin for no robot name.
	 * 
	 * @return a new plugin instance (the plugin is not running).
	 */
	@SuppressWarnings("unchecked")
	protected SecurePluginView getSample() {
		return getInstance(Robot.NAME_NONE);
	}

	/**
	 * Gets the highest id of instances currently used.
	 * 
	 * @return the highest instance id in used.
	 */
	private int getHighestID() {
		int id = instances.size( ) - 1;
		while (!instances.containsKey(id) && id > 0) {
			id--;
		}
		return id;
	}

	/**
	 * Gets all instances.
	 * 
	 * @return the instances.
	 */
	@SuppressWarnings("unchecked")
	protected Collection<SecurePluginView> getAll() {
		return instances.values( );
	}

	/**
	 * Gets an instance of this plugin for the given robot name.
	 * 
	 * @param robotName
	 *            the name of the robot for the new instance
	 * @return the plugin or <code>null</code> if an exception
	 *         occurred while instantiating the plugin.
	 */
	@SuppressWarnings("unchecked")
	private SecurePluginView getInstance(String robotName) {
		try {
			SecurePluginView secure = PluginManager.getInstance(info, robotName);

			return secure;
		}
		catch (Exception e) {
			logClass.error("Error: " + e.getMessage( ) //$NON-NLS-1$
					+ Constants.LINE_SEPARATOR, e);
		}
		return null;

	}

	/**
	 * Gets a new instance of a plugin and pre-loads it.
	 * 
	 * @param robotName
	 *            the robot name for the new instance
	 * @return the plugin or <code>null</code> if an exception
	 *         occurred while instantiating the plugin.
	 */
	@SuppressWarnings("unchecked")
	protected SecurePluginView preload(String robotName) {
		SecurePluginView secure = getInstance(robotName);

		IPlugable plugin = secure.getOriginalPlugin( );
		if (plugin != null) {
			plugin.preLoad( );
		}

		return secure;
	}

	/**
	 * Pre-loads a new instance of the plugin for no robot name.
	 * 
	 * @return the plugin or <code>null</code> if an exception
	 *         occurred while instantiating the plugin.
	 * @see #preload(String)
	 */
	@SuppressWarnings("unchecked")
	protected SecurePluginView preload() {
		return preload(Robot.NAME_NONE);
	}

	/**
	 * Runs a new plugin instance for the given robot name.
	 * 
	 * @param robotName
	 *            the robot name for the new instance
	 * @return the plugin or <code>null</code> if an exception
	 *         occurred while instantiating the plugin.
	 */
	@SuppressWarnings("unchecked")
	protected SecurePluginView run(String robotName) {
		SecurePluginView secure = preload(robotName);

		IPlugable plugin = secure.getOriginalPlugin( );
		if (plugin != null) {
			plugin.run( );
			Integer newInstance = instances.size( );
			plugin.setInstanceID(newInstance);
			instances.put(newInstance, secure);
		}

		return secure;
	}

	/**
	 * Runs a new plugin instance for no robot name.
	 * 
	 * @return the plugin or <code>null</code> if an exception
	 *         occurred while instantiating the plugin.
	 * @see #run(String)
	 */
	@SuppressWarnings("unchecked")
	protected SecurePluginView run() {
		return run(Robot.NAME_NONE);
	}

	/**
	 * Stops the plugin with the given instance id.
	 * 
	 * @param instanceID
	 *            the instance id of the plugin to stop.
	 * @return <code>true</code> if the plugin was stopped,
	 *         <code>false</code> if the plugin was not stopped or
	 *         not found at all.
	 */
	@SuppressWarnings("unchecked")
	protected boolean stop(int instanceID) {
		SecurePluginView secure = instances.remove(instanceID);
		if (secure != null) {
			return secure.getOriginalPlugin( ).stop( );
		}
		else {
			logClass.debug(I18n.getString("UnrefPluginContainer.stop.noinstance", //$NON-NLS-1$
					info.getMainClass( ),
					instanceID)
					+ Constants.LINE_SEPARATOR);
			return false;
		}
	}

	/**
	 * Stops the plugin with the highest id.
	 * 
	 * @see #stop(int)
	 */
	protected void stop() {
		stop(getHighestID( ));
	}
}
