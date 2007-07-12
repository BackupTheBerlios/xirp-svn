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
 * AbstractMultimediaPlugin.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 07.06.2006:		Created by Rabea Gransberger.
 */
package de.xirp.plugin;

/**
 * Abstract plugin implementation for multimedia plugins like
 * visualization of camera streams.<br>
 * 
 * @param <GAbstractData>
 *            The type for the data to use for this plugin. If none
 *            use {@link AbstractData}
 * @param <GAbstractPluginGUI>
 *            The type for the UI of this plugin. If none use
 *            {@link AbstractPluginGUI}
 * @author Rabea Gransberger
 */
public abstract class AbstractMultimediaPlugin<GAbstractData extends AbstractData, GAbstractPluginGUI extends AbstractPluginGUI>
		extends AbstractPlugin<GAbstractData, GAbstractPluginGUI> {

	/**
	 * Construct a new multimedia plugin, providing a method for<br>
	 * creating a screen shot.<br>
	 * 
	 * @param robotName
	 *            the name of the robot this plugin belongs to
	 * @param ownInfo
	 *            information about the plugin, read from the
	 *            plugin.properties file
	 */
	public AbstractMultimediaPlugin(String robotName, PluginInfo ownInfo) {
		super(robotName, ownInfo);
	}

	/**
	 * Makes a screen shot of the current video stream.<br>
	 * 
	 * @param path
	 *            path where the screen shot should be saved
	 * @return <code>true</code> if the screen shot has been made
	 */
	public abstract boolean makeScreenshot(String path);

	/**
	 * Checks if a screen shot is supported by this multimedia plugin.
	 * 
	 * @return <code>true</code> if this plugin supports screen
	 *         shots
	 */
	public abstract boolean supportsScreenshot();

	/**
	 * This is a multimedia plugin and therefor has the type
	 * {@link PluginType#MULTIMEDIA}.
	 * 
	 * @see de.xirp.plugin.IPlugable#getPluginType()
	 */
	public int getPluginType() {
		return PluginType.MULTIMEDIA;
	}

}
