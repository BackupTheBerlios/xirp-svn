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
 * PluginType.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 05.06.2006:		Created by Rabea Gransberger.
 */
package de.xirp.plugin;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * This class defines constants which are used to classify the purpose
 * of a plugin.<br>
 * Additionally there are two utility methods to determine if a
 * specific type is contained in a type.
 * 
 * @author Rabea Gransberger
 */
public final class PluginType {

	/**
	 * Plugin that does not match the other Types
	 */
	public static final int MISC = 1;
	/**
	 * Sensor Plugin
	 */
	public static final int SENSOR = 2;
	/**
	 * Communication Plugin, fe DECT Communication.<br>
	 * Has to implement
	 * {@link de.xirp.io.comm.lowlevel.ICommunicationInterface}
	 */
	public static final int COMMUNICATION = 4;
	/**
	 * Communication with Robot-System, fe MONSTER.<br>
	 * Has to implement
	 * {@link de.xirp.io.comm.protocol.IProtocol}
	 */
	public static final int PROTOCOL = 16;
	/**
	 * Plugin for controlling the robot
	 */
	public static final int ROBOT_CONTROL = 64;
	/**
	 * Plugin to generate reports off the referenced plugins
	 */
	public static final int REPORT = 128;
	/**
	 * Plugin for handling robot specific message sending
	 */
	public static final int MESSAGE_HANDLER = 256;
	/**
	 * Everything like camera or audio plugins
	 */
	public static final int MULTIMEDIA = 512;
	/**
	 * A plugin which adds items to the tool bar of the robot
	 */
	public static final int TOOLBAR = 1024;

	/**
	 * Possible types for a plugin
	 */
	private static Vector<Integer> types = new Vector<Integer>( );

	static {
		types.add(MISC);
		types.add(SENSOR);
		types.add(COMMUNICATION);
		types.add(PROTOCOL);
		types.add(ROBOT_CONTROL);
		types.add(REPORT);
		types.add(MESSAGE_HANDLER);
		types.add(MULTIMEDIA);
		types.add(TOOLBAR);
	}

	/**
	 * Gets all integers which stand for specific plugin types
	 * 
	 * @return vector of integers according to the plugin types
	 */
	public static List<Integer> getTypeVector() {
		return Collections.unmodifiableList(types);
	}

	/**
	 * Checks if a integer representing the concatenated types of a
	 * plugin contains a specific type
	 * 
	 * @param check
	 *            the concatenated type to check
	 * @param type
	 *            the specific type to check for
	 * @return <code>true</code> if the concatenated type contains
	 *         the specific type
	 */
	public static boolean containsType(int check, int type) {
		return (check & type) != 0;
	}

	/**
	 * Checks if a plugins type contains a specific type.<br>
	 * <br>
	 * This is just an utility method as shortcut for:<br>
	 * <code>containsType(plugin.getPluginType( ),type);</code>
	 * 
	 * @param plugin
	 *            the plugin to check
	 * @param type
	 *            the specific type to check
	 * @return <code>true</code> if the given plugin has the
	 *         specific type
	 */
	@SuppressWarnings("unchecked")
	public static boolean containsType(IPlugable plugin, int type) {
		return containsType(plugin.getPluginType( ), type);
	}

}
