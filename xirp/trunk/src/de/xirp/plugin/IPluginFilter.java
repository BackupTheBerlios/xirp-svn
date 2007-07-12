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
 * IPluginFilter.java
 * ----------------------------
 *
 * Original Author:  Rabea Gransberger [rgransberger AT web.de]
 * Contributor(s):   
 *
 * Changes
 * -------
 * 24.10.2006:		Created by Rabea Gransberger.
 */
package de.xirp.plugin;

/**
 * Filter definition for plugins.
 * 
 * @author Rabea Gransberger
 */
public interface IPluginFilter {

	/**
	 * This method receives the plugin that should be filtered and
	 * returns <code>true</code> or <code>false</code>.
	 * 
	 * @param plugin
	 *            the plugin to check
	 * @return <code>true</code> if the the plugin is accepted
	 */
	@SuppressWarnings("unchecked")
	public boolean filterPlugin(IPlugable plugin);

}
