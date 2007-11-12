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
 * VisualizationType.java
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
 * This class defines constants which specify how the plugin is shown
 * in the application.<br>
 * Additionally there are two utility methods to determine if a
 * specific type is contained in a type.<br>
 * 
 * @author Rabea Gransberger
 */
public final class VisualizationType {

	/**
	 * Plugin is embedded in workspace
	 */
	public static final int EMBEDDED = 1;
	/**
	 * Plugin is shown in own window, if the plugin type does not
	 * override the visualization type
	 */
	public static final int WINDOW = 2;
	/**
	 * If the plugins type contains {@link PluginType#TOOLBAR} this
	 * visualization type specifies that the tool bar should be added
	 * to the applications tool bar (the main tool bar)
	 */
	// public static final int APP_TOOLBAR = 4;
	/**
	 * If the plugins type contains {@link PluginType#TOOLBAR} this
	 * visualization type specifies that the tool bar should be added
	 * to the robots tool bar (a tool bar specific for the robot)
	 */
	public static final int ROBOT_TOOLBAR = 16;
	/**
	 * Plugin just executes, does not need UI
	 */
	public static final int NONE = 64;

	/**
	 * Possible types for a plugin
	 */
	private static Vector<Integer> types = new Vector<Integer>( );

	static {
		types.add(EMBEDDED);
		types.add(WINDOW);
		// types.add(APP_TOOLBAR);
		types.add(ROBOT_TOOLBAR);
		types.add(NONE);
	}

	/**
	 * Gets all integers which stand for specific visualization types
	 * 
	 * @return vector of integers according to the visualization types
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
		return containsType(plugin.getVisualizationType( ), type);
	}
}
